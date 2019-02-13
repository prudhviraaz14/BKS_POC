/*
 * $Header:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/BksServiceHandler.java-arc   1.23   Apr 24 2013 16:43:22   makuier  $
 *
 *  $Log:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/BksServiceHandler.java-arc  $
 * 
 *    Rev 1.23   Apr 24 2013 16:43:22   makuier
 * read jms property and populate the handler field.
 * 
 *    Rev 1.22   May 31 2012 14:36:46   makuier
 * Format access numbers as string when logging.
 * 
 *    Rev 1.21   Jan 24 2012 10:12:22   makuier
 * log both to ONL and EFFONL
 * 
 *    Rev 1.20   Jan 11 2012 13:02:12   makuier
 * Some log refactoring
 * 
 *    Rev 1.19   Dec 09 2011 14:07:44   makuier
 * populateErrorLogParam added.
 * 
 *    Rev 1.18   Oct 21 2011 15:57:48   makuier
 * messageSender defined
 * 
 *    Rev 1.17   Nov 09 2009 11:19:46   makuier
 * Adaption to new BksHelper.
 * 
 *    Rev 1.16   Aug 27 2009 17:54:40   makuier
 * Terminate the application if it is not intialized after max. number of tries.
 * 
 *    Rev 1.15   Aug 13 2009 11:38:46   makuier
 * Print input XML without serializing.
 * 
 *    Rev 1.14   Aug 11 2009 15:01:28   makuier
 * serialize input message only if debug is enabled
 * 
 *    Rev 1.13   Aug 10 2009 10:52:04   makuier
 * print the stack trace.
 * 
 *    Rev 1.12   May 27 2009 16:06:24   makuier
 * MCF2 adaption
 * 
 *    Rev 1.11   Sep 17 2008 16:03:44   makuier
 * Use the context name from AAW
 * 
 *    Rev 1.10   Jul 17 2008 13:19:00   schwarje
 * SPN-BKS-000073952: PruefeBestandskunde: disabled eh-cache and enabled iBatis query cache
 * 
 *    Rev 1.9   May 13 2008 12:05:26   makuier
 * try to find the cached object using access number
 * 
 *    Rev 1.8   Apr 15 2008 10:59:54   makuier
 * Catch and rethrow  McfTechnicalException 
 * 
 *    Rev 1.7   Apr 07 2008 15:38:48   makuier
 * delay until the ref data is initialized.
 * 
 *    Rev 1.6   Feb 27 2008 11:20:40   makuier
 * refactoring.
 * 
 *    Rev 1.5   Feb 15 2008 15:29:32   makuier
 * performance logging added.
 * 
 *    Rev 1.4   Jan 22 2008 16:44:46   makuier
 * In and output messages are logged
 * 
 *    Rev 1.3   Dec 19 2007 17:59:02   makuier
 * Catch runtime exception and re.throw to mcf.
 * 
 *    Rev 1.2   Nov 27 2007 14:03:46   makuier
 * Some Clean ups
 * 
 *    Rev 1.1   Nov 06 2007 17:54:16   makuier
 * Changes for 1.25
 * 
 *    Rev 1.0   Aug 10 2007 18:03:28   makuier
 * Initial revision.
*/
package net.arcor.bks.requesthandler;

import java.lang.reflect.Method;
import java.sql.Timestamp;

import javax.xml.bind.JAXBElement;

import net.arcor.bks.client.DatabaseClient;
import net.arcor.bks.client.DatabaseClientConfig;
import net.arcor.bks.common.BksHelper;
import net.arcor.bks.common.BksRefDataCacheHandler;
import net.arcor.bks.db.GeneralDAO;
import net.arcor.epsm_basetypes_001.InputData;
import net.arcor.epsm_basetypes_001.ProviderService;
import net.arcor.epsm_commontypes_kontaktrufnummer_001.Kontaktrufnummer;
import net.arcor.mcf2.exception.base.MCFException;
import net.arcor.mcf2.model.ServiceObjectEndpoint;
import net.arcor.mcf2.model.ServiceResponse;
import net.arcor.mcf2.sender.MessageSender;
import net.arcor.mcf2.service.Service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author MAKUIER
 *
 */
public class BksServiceHandler implements Service {

	protected final Log logger = LogFactory.getLog(getClass());

	@Autowired
	private MessageSender messageSender;
	
	public MessageSender getMessageSender() {
		return messageSender;
	}

	public void setMessageSender(MessageSender messageSender) {
		this.messageSender = messageSender;
	}

	public ServiceResponse<?> execute(final ServiceObjectEndpoint<?> serviceInput)
	throws MCFException {
		int tries =0;
		int maxTries = 10;
		try {
			maxTries = Integer.parseInt(DatabaseClientConfig.getSetting("databaseclient.MaxRetries"));
		} catch (Exception e1) {
			// Ignore (Use Default)
		}
        while (!BksRefDataCacheHandler.isRefdataInitialized() && tries < maxTries){
        	tries++;
        	try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
        }
        if (!BksRefDataCacheHandler.isRefdataInitialized()){
        	logger.error("The application is not initialized after max. number of tries ( "+ tries +").");
        	logger.error("Please increase the value of databaseclient.MaxRetries and restart the application.");
        	System.exit(0);
        }
        logger.info("Tries "+ tries);
		String correlationId = serviceInput.getCorrelationID();
        boolean cacheHit = false;
        long startTime = System.currentTimeMillis();
		logger.info("Thread Name : "+ Thread.currentThread().getName());
		logger.info("Input XML:");
		logger.info(serviceInput.getXml());

		@SuppressWarnings("unchecked")
		ProviderService service = ((JAXBElement<ProviderService>)(serviceInput.getPayload())).getValue();

		// extract some useful names from the service
		String serviceClassName = service.getClass().getSimpleName();
		String serviceName = 
			serviceClassName.substring(0, serviceClassName.length()	- "Service".length());

		logger.info("Request for service '" + serviceName + "' received.");

		String returnObject = null;
		BksHelper theHelper = new BksHelper();
		InputData input;

		try {
			// get the input object from the service object
			input = (InputData) theHelper.invokeMethod(service, "get"
					+ serviceName + "Input", null,null);

			BaseServiceHandler serviceHnd = (BaseServiceHandler)Class.forName(
					"net.arcor.bks.requesthandler." + serviceName + "Handler")
					.newInstance();

			serviceHnd.setInput(input);
			serviceHnd.setOriginator(serviceInput.getServiceOriginator());
			String somVersion = serviceInput.getIncomingMessage().getStringProperty("SBUS_SOMRelease");
			serviceHnd.setClientSchemaVersion(somVersion);
			// TODO replace by a real solution
			// temporary hack to disable cache for one service while keeping it for all the others
			String customerNumber = serviceHnd.getCustomerNumber();
			String accessNumber = serviceHnd.getAccessNumber();
			if (!serviceName.equals("PruefeBestandskunde") && somVersion==null) {
				returnObject = (String) theHelper.fetchFromCache(serviceName, customerNumber,accessNumber);
			}
			else 
				logger.debug("Cache disabled temporarily, see SPN-BKS-000073952");
			
			if (returnObject != null){
				logger.info("Output XML from the cache:");
				logger.info(returnObject);
		        cacheHit = true;
			} else {
				// populate the output on the service object
				returnObject = serviceHnd.execute();
				logger.info("Output XML:");
				logger.info(returnObject);
			}
	        long endTime = System.currentTimeMillis();
	        boolean loggingEnabled = false;
	        try {
				String tmp = DatabaseClientConfig.getSetting("databaseclient.EnableDbLogging");
				loggingEnabled = (tmp.equalsIgnoreCase("true"));
			} catch (Exception e) {
				// ignore
			}
			if (loggingEnabled) {
			    long duration = endTime - startTime;
			    logger.info("duration : "+duration);
				GeneralDAO dao = null;
				try {
					dao = (GeneralDAO)DatabaseClient.getBksDaoForTargetDb("onl","GeneralDAO");
				} catch (Exception e) {
				}
				if (dao != null) {
					dao.insertLog(serviceHnd.getErrorCode(), serviceHnd
							.getErrorText(),
							(serviceHnd.getErrorCode() == null) ? "SUCCESS"
									: "FAILED", customerNumber, accessNumber,
							correlationId, serviceName, cacheHit ? "Y" : "N",
							new Timestamp(startTime), new Timestamp(endTime));
					if (serviceHnd.getErrorText() != null
							|| serviceHnd.getErrorCode() != null) {
						populateErrorLogParams(dao, correlationId, serviceHnd,
								serviceName);
					}
				}
				try {
					dao = null;
					dao = (GeneralDAO)DatabaseClient.getBksDaoForTargetDb("effonl","GeneralDAO");
				} catch (Exception e) {
				}
				if (dao != null) {
					dao.insertLog(serviceHnd.getErrorCode(), serviceHnd
							.getErrorText(),
							(serviceHnd.getErrorCode() == null) ? "SUCCESS"
									: "FAILED", customerNumber, accessNumber,
							correlationId, serviceName, cacheHit ? "Y" : "N",
							new Timestamp(startTime), new Timestamp(endTime));
					if (serviceHnd.getErrorText() != null
							|| serviceHnd.getErrorCode() != null) {
						populateErrorLogParams(dao, correlationId, serviceHnd,
								serviceName);
					}
				}
			}
		} catch (MCFException e) {
			throw e;
		} catch (Exception e) {
			String errorMessage = "Exception caught during the execution of service.";
			logger.error(errorMessage, e);
			e.printStackTrace();
			throw new MCFException();
		}
		catch (Throwable e) {
			String errorMessage = "Unexpected error occured during the Exceution.";
			logger.fatal(errorMessage, e);
			e.printStackTrace();
			throw new MCFException();
		}
		if (returnObject == null)
			return null;
		return serviceInput.createResponse(serviceInput.getServiceName(), serviceInput.getServiceVersion(), returnObject);
	}

	private void populateErrorLogParams(GeneralDAO dao,String correlationId,BaseServiceHandler serviceHnd, String serviceName) throws Exception {
		InputData indata = serviceHnd.getInput();
		Method[] methods = indata.getClass().getMethods();
		Method theMethod = null;
		Integer seqNum = 0;
		// find the right method
		for (int i=0;i<methods.length;i++) {
			if (methods[i].getName().startsWith("get")){
				theMethod = methods[i];
				Object value = theMethod.invoke(indata, (Object[]) null);
				if (value != null){
					String name = theMethod.getName().substring(3);
					try {
						Method m = value.getClass().getMethod("getExisting");
						value = m.invoke(value, (Object[])null);
					} catch (NoSuchMethodException  e) {
						// Ignore
					}
					if (value instanceof Kontaktrufnummer){
						String strValue = ((Kontaktrufnummer)value).getLaenderkennzeichen()+ " " +
						                  ((Kontaktrufnummer)value).getVorwahl() + "/" +
						                  ((Kontaktrufnummer)value).getRufnummer();
						dao.createErrorLogParam(correlationId,name, strValue,null,seqNum++);
					} else	
						dao.createErrorLogParam(correlationId,name, value.toString(),null,seqNum++);
				}
			}
		}
		if (serviceHnd.getUnvalidatedXml() != null){
			dao.createErrorLogParam(correlationId, "XML", null, serviceHnd.getUnvalidatedXml().toString(),seqNum);
		}
	}
}
