/*
    $Header:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/client/EaiClient.java-arc   1.5   Nov 16 2015 10:28:30   makuier  $

    $Log:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/client/EaiClient.java-arc  $
 * 
 *    Rev 1.5   Nov 16 2015 10:28:30   makuier
 * Pass soapAction for performance logging.
 * 
 *    Rev 1.4   Nov 02 2015 14:27:44   makuier
 * Default the error message in case the exception message is empty
 * 
 *    Rev 1.3   Jul 17 2015 10:04:26   makuier
 * adapted to EAI / TIBCO headers.
 * 
 *    Rev 1.2   Jun 30 2015 06:28:38   makuier
 * reading Appmondetails
 * 
 *    Rev 1.1   May 26 2015 08:40:40   makuier
 * Some improvements.
 * 
 *    Rev 1.0   May 11 2015 13:13:26   makuier
 * Initial revision.
*/
package net.arcor.bks.client;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.Detail;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import net.arcor.bks.common.BksDataException;
import net.arcor.bks.common.BksHttpServiceHandler;
import net.arcor.bks.common.BksHttpServiceRegistry;
import net.arcor.bks.common.BksRefDataCacheHandler;
import net.arcor.bks.common.BksStaticContainer;
import net.arcor.bks.common.Log4jConfig;
import net.arcor.bks.db.BksDataAccessObject;
import net.arcor.mcf2.exception.base.MCFException;

import org.apache.log4j.Logger;
import org.apache.log4j.helpers.Loader;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.vodafone.mcf2.ws.tools.NamespacePrefixMapperImpl;

import de.vodafone.esb.schema.common.basetypes_esb_001.AppMonDetailsType;
import de.vodafone.esb.schema.common.basetypes_esb_001.ESBException;
import de.vodafone.esb.schema.common.basetypes_esb_001.ErrorDetailsType;
import de.vodafone.esb.schema.common.basetypes_esb_001.FunctionalESBException;
import de.vodafone.esb.schema.common.basetypes_esb_001.TechnicalESBException;

public final class EaiClient extends HttpServlet {

	private static final ResourceBundle rb = ResourceBundle.getBundle("config/BksWebClient");
	private static final long serialVersionUID = 0L;

	/**
	 * The log4j logger.
	 */
	private static Logger logger = Logger.getLogger(EaiClient.class);

	/**
	 * Indicates whether this class is initialized.
	 */
	private static boolean initialized = false;

	private static Unmarshaller unmarshaller;

	/*---------*
	 * METHODS *
	 *---------*/

    /**
     * Determines whether this class is initialized.
     * @return true if initialize, false if not.
     */
    protected static boolean isInitialized() {
        return initialized;
    }

    /**
     * Initializes the application.
     * @param configFile  the name of the property file to get the settings from.
     * @throws FIFException if the application could not be initialized.
     */
    public static synchronized void initProperties() throws Exception {
        // Bail out if the client was initialized already
        if (initialized == true) {
            return;
        }

        // Set the initialized flag
        initialized = true;

        // Initialize the queue client config
        DatabaseClientConfig.init(rb);

        // Initialize the logger
        URL url = Loader.getResource("log4j.xml");
        Log4jConfig.init(url);

        logger.info(
            "Initializing DatabaseClient with property file "
                + "...");

        logger.info("Successfully initialized .");
    }

    /**
     * Starts the client.
     * @throws Exception 
     * @throws FIFException if the client could not be started.
     */
    public static void start() throws Exception {
    	String beans;
		try {
			beans = DatabaseClientConfig.getSetting("webclient.BeanConfigurationFile");
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		}
    	final String [] beanConfiguration = beans.split(",");
		FileSystemXmlApplicationContext ac = new FileSystemXmlApplicationContext(beanConfiguration);
		BksStaticContainer.setAc(ac);
		BksStaticContainer.getAc().registerShutdownHook();
		BksRefDataCacheHandler.loadWebRefData();
     }

    /**
     * Starts the <code>DatabaseClient</code>.
     */
    public void init () {
    	// Initialize the application
    	try {
    		JAXBContext jbContext = JAXBContext.newInstance("de.vodafone.esb.schema.common.basetypes_esb_001");
    		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    		ClassLoader b = EaiClient.class.getClassLoader();
    		URL r = b.getResource("schema/BaseTypes-ESB-001.xsd");
     		Schema schema = schemaFactory.newSchema(r);
     		unmarshaller = jbContext.createUnmarshaller();
     		unmarshaller.setSchema(schema); 

    		initProperties();
    		// Start the database client
    		start();
    	} catch (final Exception e) {
    		e.printStackTrace();
    		System.exit(1);
    	}
    }

    public void destroy(){
    	BksStaticContainer.getAc().close();
   }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,IOException{
        String contentType = req.getContentType();
        String[] fields = null;
		if (contentType!=null) 
			fields = contentType.split(";");
		
        String encoding="UTF-8";
        for (int i = 0; fields!=null&&i < fields.length; i++) {
        	String field=fields[i].trim();
        	if (field.startsWith("charset")) {
        		String[] charset = field.split("=");
        		if (charset.length>=2){
        			encoding=charset[1];
        			encoding=encoding.replaceAll("\"", "");
        		}
        	}
		}
		BufferedReader br
    		   = new BufferedReader(new InputStreamReader(req.getInputStream(), encoding));
//        BufferedReader br1 = req.getReader();    
    	logger.debug(req.getContentType());
    	String line = null;  
    	String bpId =  "unknown"; 
    	String requestMessage = "";  
    	String responseMessage = null;
    	String soapAction = req.getHeader("soapAction");
    	if(soapAction!=null)
    		soapAction=soapAction.trim().replaceAll("\"", "");
    	while ((line = br.readLine()) != null) {  
    		requestMessage += URLDecoder.decode(line,"UTF-8");
    	}  
    	logger.debug("Received request :"+requestMessage);
    	try {
    		SOAPMessage mes = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createMessage(null, new ByteArrayInputStream(requestMessage.getBytes()));
        	AppMonDetailsType appmon = getAppMonDetails(mes);
        	if (appmon!=null) {
        		bpId = appmon.getBpId();
        		logger.info("Processing request for service " + soapAction + ", bpId " + bpId + " started.");
			}
			BksHttpServiceHandler aHandler = BksHttpServiceRegistry.getInternalregistry().get(soapAction);
			if(aHandler==null)
				throw new BksDataException("BKS0071","There is no handler defined for soap action :"+soapAction);
    		responseMessage = aHandler.execute(mes,soapAction);
    	} catch (BksDataException e) {
    		logger.error(e.getText());
    		logger.error("Exception raised while processing request " + bpId
    				+ ": ErrorCode: " + e.getCode()
    				+ ", ErrorMessage: " + e.getText());
    		try {
    			responseMessage = generateSoapFault(e);
    		} catch (Exception e1) {
    			throw new ServletException(e);
    		}
    	} catch (Exception e) {
    		logger.error(e.getMessage());
    		logger.error("Exception raised while processing request " + bpId
    				+ ": ErrorCode: BKS0008 " 
    				+ ", ErrorMessage: " + e.getMessage(), e);
    		try {
    			responseMessage = generateSoapFault(e);
    		} catch (Exception e1) {
    			throw new ServletException(e);
    		}
    	}
    	resp.setContentType(contentType);
    	resp.setCharacterEncoding("UTF-8");
    	PrintWriter out = resp.getWriter();
    	logger.info("BKS response: "+responseMessage);
    	out.println(responseMessage);
    	br.close();  

    	out.close();
    	out.flush();
    }

    private String generateSoapFault(Exception fault) throws JAXBException, SOAPException, IOException{
    	MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
    	SOAPMessage message = factory.createMessage();

    	SOAPBody body = message.getSOAPBody();
    	SOAPFault soapFault = body.addFault();
        soapFault.setFaultCode(SOAPConstants.SOAP_RECEIVER_FAULT);
        String errorCode=null;
        String errorText=null;
        if (fault instanceof BksDataException){
            errorCode=((BksDataException)fault).getCode();
            errorText=((BksDataException)fault).getText();
        } else {
            errorCode="BKS0008";
        	errorText="UnknownException";
            if (fault.getMessage()!=null)
            	errorText=fault.getMessage();
        }

        soapFault.addFaultReasonText(errorCode, Locale.ENGLISH);
        Detail detail = soapFault.addDetail();

        JAXBContext ctx = JAXBContext.newInstance("de.vodafone.esb.schema.common.basetypes_esb_001");
        Marshaller marshaller = ctx.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE); 

        try {
            marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new NamespacePrefixMapperImpl());
        }
        catch (PropertyException e) {
        }

        ErrorDetailsType details = new ErrorDetailsType();
        details.setErrorCode(errorCode);
        details.setErrorMessage(errorText);

        de.vodafone.esb.schema.common.basetypes_esb_001.ObjectFactory of =
                new de.vodafone.esb.schema.common.basetypes_esb_001.ObjectFactory();
        ESBException eSBException=null;
        if (fault instanceof BksDataException){
        	eSBException = new FunctionalESBException();
        	eSBException.setErrorDetails(details);
        	marshaller.marshal(of.createFunctionalESBException((FunctionalESBException) eSBException), detail);
        } else {
        	eSBException = new TechnicalESBException();
        	eSBException.setErrorDetails(details);
        	marshaller.marshal(of.createTechnicalESBException((TechnicalESBException) eSBException), detail);
        }
        
        message.saveChanges();
        message.setProperty(SOAPMessage.WRITE_XML_DECLARATION, "true");

		ByteArrayOutputStream out = new ByteArrayOutputStream();
        message.writeTo(out);
		return new String(out.toByteArray());
    }
    
    private static AppMonDetailsType getAppMonDetails(SOAPMessage message) throws Exception {
    	NodeList nodeList=null;
		if (message.getSOAPHeader()!=null) 
			nodeList = message.getSOAPHeader().getElementsByTagName("*");

    	for (int i = 0;nodeList!=null && i < nodeList.getLength(); i++) {
    		Node node = nodeList.item(i);
    		if (node.getNodeType() == Node.ELEMENT_NODE && node.getLocalName().equals("appMonDetails")) {
    			AppMonDetailsType appMonDetails=null;
				synchronized (unmarshaller) {
					appMonDetails = ((JAXBElement<AppMonDetailsType>) unmarshaller.unmarshal(node)).getValue();
				}
    			return appMonDetails;
    		}
    	}
    	return null;
 }
 	public static BksDataAccessObject getBksDaoForTargetDb(String preferedDataSource,String daoName) throws MCFException {
		BksDataAccessObject ret = null;
		ret = (BksDataAccessObject) BksStaticContainer.getAc().getBean(daoName);

		ret.connectToTargetDb(preferedDataSource);
		return ret;
	}

	public static BksDataAccessObject getBksDataAccessObject(
			String preferedDataSource, String daoName) {
		
		return BksStaticContainer.getBksDataAccessObject(preferedDataSource, daoName);
	}
}
