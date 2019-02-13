/*
    $Header:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/ConsolidateSubscriptionDataHandler.java-arc   1.28   Sep 25 2018 07:41:06   makuier  $

    $Log:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/ConsolidateSubscriptionDataHandler.java-arc  $
 * 
 *    Rev 1.28   Sep 25 2018 07:41:06   makuier
 * Fibre services added
 * 
 *    Rev 1.27   Apr 26 2018 11:48:24   makuier
 * USe AIDA instead of Aida as leading system
 * 
 *    Rev 1.26   Feb 09 2018 07:24:12   vikas.s
 * Fix error message of discrepancy for a static AAA acount between CCM and AIDA
 * 
 *    Rev 1.25   Feb 02 2018 15:33:52   makuier
 * Mock the 2nd AIDA connection if the mock flag is set.
 * 
 *    Rev 1.24   Jan 23 2018 11:06:32   makuier
 * Bug fix
 * 
 *    Rev 1.23   Jan 23 2018 08:13:46   makuier
 * Support Siptrunk + Business DSL
 * 
 *    Rev 1.22   Jan 12 2018 14:13:16   makuier
 * Refactoring to support AIDA structure correctly. ITK 33956
 * 
 *    Rev 1.21   Nov 30 2017 09:40:04   makuier
 * do not consolidate ASB for bit stream products
 * 
 *    Rev 1.20   Nov 27 2017 13:15:30   makuier
 * Compare CCM and Infport data even if Cramer data is not available.
 * 
 *    Rev 1.19   Oct 05 2017 08:08:18   makuier
 * Generate a report item when Infport returns an exception.
 * 
 *    Rev 1.18   Aug 03 2017 11:48:14   makuier
 * Set the Infport validated flag
 * 
 *    Rev 1.17   Jul 24 2017 13:50:32   makuier
 * Consider LTE as not supported in AIDA
 * 
 *    Rev 1.16   Jul 24 2017 12:30:14   makuier
 * get the characteristic values from chaild services
 * 
 *    Rev 1.15   May 31 2017 15:33:36   makuier
 * Changes for TKG project
 * 
 *    Rev 1.14   Dec 23 2016 10:27:44   makuier
 * Handle ZAR unsupported status.
 * Print active Service subscription id on report.
 * 
 *    Rev 1.13   Oct 27 2016 08:37:20   makuier
 * Support Cramer consolidation for Bitstream 
 * 
 *    Rev 1.12   Jun 14 2016 08:42:38   makuier
 * Made the error message correct.
 * 
 *    Rev 1.11   Jul 03 2014 10:05:26   makuier
 * Call the stored procedure instaed of direct query.
 * 
 *    Rev 1.10   Mar 26 2014 11:24:50   makuier
 * Detect multiple line bundle and insert a report action.
 * 
 *    Rev 1.9   Feb 05 2014 12:27:18   makuier
 * - Maps the correct CCB number in ZAR report.
 * 
 *    Rev 1.8   Dec 27 2013 10:30:22   makuier
 * - Do not fill any report if the bundle has other pending orders.
 * 
 *    Rev 1.7   Sep 30 2013 09:59:48   makuier
 * Truncate error  messages.
 * 
 *    Rev 1.6   Jul 19 2013 10:51:20   makuier
 * Ignore items from ZAR populated
 * 
 *    Rev 1.5   Jun 11 2013 12:53:38   makuier
 * Do not match characteristics if the product is not valid for external system.
 * 
 *    Rev 1.4   Jun 05 2013 10:11:28   makuier
 * Corrected the ZAR error message
 * 
 *    Rev 1.3   Mar 20 2013 10:49:32   makuier
 * Ignore DIM-21305 from cramer as empty return collection is indicator enough.
 * 
 *    Rev 1.2   Mar 14 2013 12:33:30   makuier
 * Misc. fixes.
 * 
 *    Rev 1.1   Jan 31 2013 16:59:38   makuier
 * Second revision.
 * 
 *    Rev 1.0   Jan 11 2013 09:51:32   makuier
 * Initial revision.
*/
package net.arcor.bks.requesthandler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBElement;

import net.arcor.bks.client.DatabaseClient;
import net.arcor.bks.client.DatabaseClientConfig;
import net.arcor.bks.common.BksDataException;
import net.arcor.bks.common.BksHelper;
import net.arcor.bks.common.BksRefDataCacheHandler;
import net.arcor.bks.db.AidaDAO;
import net.arcor.bks.db.ConsolidateSubscriptionDataDAO;
import net.arcor.bks.db.CramerDAO;
import net.arcor.bks.db.GeneralDAO;
import net.arcor.bks.db.ibatis.AidaDAOImpl;
import net.arcor.bks.db.ibatis.CramerDAOImpl;
import net.arcor.ccm.epsm_ccm_consolidatesubscriptiondata_001.ActionItem;
import net.arcor.ccm.epsm_ccm_consolidatesubscriptiondata_001.ActionList;
import net.arcor.ccm.epsm_ccm_consolidatesubscriptiondata_001.ConsolidateSubscriptionDataInput;
import net.arcor.ccm.epsm_ccm_consolidatesubscriptiondata_001.ConsolidateSubscriptionDataOutput;
import net.arcor.ccm.epsm_ccm_consolidatesubscriptiondata_001.ConsolidateSubscriptionDataService;
import net.arcor.epsm_basetypes_001.ProviderService;
import net.arcor.mcf2.exception.base.MCFException;
import net.arcor.mcf2.exception.internal.XMLException;
import net.arcor.mcf2.model.ServiceObjectEndpoint;
import net.arcor.mcf2.model.ServiceResponse;
import net.arcor.mcf2.sender.MessageSender;
import net.arcor.mcf2.service.Service;
import net.arcor.orderschema.Address;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.vodafone.mcf2.ws.model.impl.ServiceRequestSoap;

import de.dialogika.infport.schema.basic.v1.ActionRequest;
import de.dialogika.infport.schema.basic.v1.CtBasicData;
import de.dialogika.infport.schema.basic.v1.CtBasicResponse;
import de.dialogika.infport.schema.basic.v1.CtIDSet;
import de.dialogika.infport.schema.common.v1.CtInstallationAddress;
import de.dialogika.infport.schema.cu.r2017.MtGetInstallationAddressRequest;
import de.dialogika.infport.schema.cu.r2017.MtGetInstallationAddressResponse;
import de.dialogika.infport.schema.fn.r2017.MtGetCombinedCustomerDataRequest;
import de.dialogika.infport.schema.fn.r2017.MtGetCombinedCustomerDataResponse;
import de.vodafone.esb.schema.common.basetypes_esb_001.AppMonDetailsType;
import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * @author MAKUIER
 *
 */
public class ConsolidateSubscriptionDataHandler   implements Service {

	@Autowired
	private MessageSender messageSender;

	protected final Log logger = LogFactory.getLog(getClass());
	enum Products {UNDEFINED,ISDN_WITH_ACS,ISDN_WITHOUT_ACS,NGN,BIT_STREAM,LTE,BUSINESS_DSL,DSLR,ONE_AND_ONE,NOT_SUPPORTED};
	private static final Map<String, String> errorMap  = new HashMap<String, String>()
    {
		private static final long serialVersionUID = 7831420967495650133L;
		{
			put("E0001", "Customer exists in %s but not in %s.");
			put("E0002", "IP-TV account does not exists in CCM (I1330).");
			put("E0003", "AAA account does not exists in CCM (I9058).");
			put("E0004", "Device Id does not exists in CCM (V0153).");
			put("E0005", "The TASI is not populated in AIDA");
			put("E0006", "The TASI does not exists in CCM (V0152).");
			put("E0007", "The service subscription id that is identifying the bundle is not populated in AIDA. (TSG = %s)");
			put("E0008", "Accessnumber found in %s for different customer %s on service subscription %s");
			put("E0009", "Exception %s\nAdditional Info: %s\nStacktrace:\n%s");
			put("E0010", "Accessnumber found multiple times on services %s and %s");
			put("E0011", "Accessnumber exists in %s but not in %s and should be added.");
			put("E0012", "Accessnumber exists in %s but not in %s and should be removed.");
			put("E0013", "Accessnumber from %s found in %s for different customer %s");
			put("E0014", "AAA has a wrong value in CCM. CCM value %s.");
			put("E0016", "The TSG does not exists in CCM (V0154).");
			put("E0017", "TSG (V0154) has a wrong value in CCM. CCM value %s");
			put("E0018", "The customer has ISDN with ACS, but is unknown for AIDA.");
			put("E0019", "Objectquery from %s returned error (%s) in Cramer: %s");
			put("E0020", "The TASI is not populated in Cramer");
			put("E0022", "Accessnumber is corrupted and needs to be cleared: %s");
			put("E0023", "The service subscription id that is identifying the bundle is not populated in Cramer.(TASI = %s)");
			put("E0024", "The service subscription id from Cramer is not found in CCM.");
			put("E0025", "Customer exists in %s but no data found for CCM and should be removed.");
			put("E0026", "Customer exists in %s but not in CCM and all object related to the TSG: %s should be removed.");
			put("E0028", "Customer %s has no subscribed services relevant to the validation or doesn't exist in CCM.");
			put("E0029", "Following %s %s are not populated in the CCM. It is imposible to determine corresponding service subscription ids.");
			put("E0030", "For the SIP %s (%s) value in CCM (Access Number) there are missing entries in AIDA.");
			put("E0031", "The service subscription %s has multiple active %s CSCs.");
			put("E0032", "For given %s: %s multiple records were found in AIDA: %s");
			put("E0033", "For the SIP %s (%s) value in CCM (Access Number) there are found entries in AIDA not connected to any TSG.");
			put("E0034", "Object from %s is corrupted in Cramer and further validations for Cramer are skipped: (%s):%s");
			put("E0035", "FIF tries to reconfigure the same CSC with multiple different values: %s = %s");
			put("E0036", "No service subscription id for %s found. Action: %s");
			put("E0037", "Service subscription id for %s is not unique for action %s: %s");
			put("E0038", "Access Number %s, belonging to service subscription %s, is wrongly grouped in ZAR");
			put("E0039", "Customer from %s doesn't have any active services in CCM");
			put("E0040", "Customer from %s has active non-voice services in CCM: %s");
			put("E0041", "IPTV (I1330) has a wrong value in CCM. CCM value %s");
			put("E0042", "DeviceId(V0153) has a wrong value in CCM. CCM value %s");
			put("E0043", "TSG %s is not associated with the customer %s is AIDA.");
			put("E0044", "%s");	
			put("E0045", "Unassigned STP from %s found.");
			put("E0046", "Customer %s has active contract %s");
			put("E0047", "Customer %s is missing active contracts");
			put("E0048", "ProductSubscription %s is missing contract or product commitment data");
			put("E0049", "ProductSubscription %s doesn't exist");
			put("E0050", "SIP domain does not exists in CCM (VI321).");
			put("E0051", "SIP account %s does not exists in CCM (VI011).");
			put("E0052", "SIP account %s from VI0011 does not correspond to V0001 access number %s.");
			put("E0053", "The Line is terminated in CCM but exists in %s ");
			put("E0054", "ASB %s has a wrong value in CCM (V0934)");
			put("E0055", "SIP account has a wrong value in CCM (VI011)");
			put("E0056", "SIP account %s belongs to a different TSG.");
			put("E0057", "SIP account %s exist in %s but not in %s (VI011).");
			put("E0058", "TASI has worng value in CCM. CCM value %s");
			put("E0059", "The bundle contains more than one line. Please Correct the CCM data.");
			put("E0060", "Line ID has a wrong value in CCM. CCM value %s");
			put("E0061", "Address field %s has a wrong value in infport CCM value %s Infport value %s.");
			put("E0062", "IP-Address has a wrong value in CCM. CCM value %s");
			put("E0063", "Line ID %s has a wrong value in Infport.");
			put("E0064", "Items of bundle %s have different installation addresses");
			put("E0065", "Ambiguous TSG found for Customer %s in AIDA.");
			put("E0066", "Customer number %s is wrong in AIDA. CCM value = %s for TSG %s");
			put("E0067", "Ambiguous Tasi found for Customer %s in Cramer.");
			put("E0068", "Customer number %s is wrong in Cramer. CCM value = %s for Tasi %s");
		}
    };
	final static String[] mainServiceCodes = {"V0003","V0010","V0011","I1210","I121z","I1213","I1215","I1216","I1290","I1043","Wh003","Wh010"};
	final static String[] mainAccNumCodes = {"V0001","V0070","V0071","V0072","V0073","V0074","V0075","V0076","V0077","V0078"};
	final static String[] sipAccountCodes = {"VI011","VI012","VI013","VI014","VI015","VI016","VI017","VI018","VI019","VI020"};
	final static String[] accNumRangeCodes = {"V0002","W9002","W9003"};
	final static HashMap<String , String[]> charTargetSystem = new HashMap<String , String[]>() {
		private static final long serialVersionUID = 1L;
		{ 
			put("V0152",new String[] {"Cramer","Technische_Service_ID","STRING","E0006","E0058"});
			put("V0153",new String[] {"AIDA","IAD-PIN","STRING","E0004","E0042"});
			put("V0154",new String[] {"AIDA","Technical-Service-Group","STRING","E0016","E0017"});
			put("I9058",new String[] {"AIDA","Dial-In Account Name","USER_ACCOUNT_NUM","E0003","E0014"});
			put("I905A",new String[] {"AIDA","Dial-In Account Name Fixed","USER_ACCOUNT_NUM","E0003","E0014"});
			put("I905B",new String[] {"AIDA","IP Address","IP_NET_ADDRESS","E0062","E0062"});
			put("I1330",new String[] {"AIDA","TV-PLATTFORM-ACCOUNT","USER_ACCOUNT_NUM","E0002","E0041"});
			put("V0934",new String[] {"Cramer","Anschlussbereich-Kennzeichen","INTEGER","E0054","E0054"});
			put("I1020",new String[] {"Cramer","NGAB Line ID","STRING","E0060","E0060"});
			put("V0014",new String[] {"CCM","Address","STRING","E0061","E0061"});
		}
	}; 
	boolean isCramerBitMigrationDone = true;				
		
	public ServiceResponse<?> execute(ServiceObjectEndpoint<?> serviceInput)
	throws MCFException {
		int tries =0;
		int maxTries = getIntegerProperty("databaseclient.MaxRetries",10);
		String errorText = null;
		String errorCode = null;
		String correlationId = serviceInput.getCorrelationID();
        boolean cacheHit = false;

        long startTime = System.currentTimeMillis();
		try {
	        isCramerBitMigrationDone = Boolean.parseBoolean(DatabaseClientConfig.getSetting("databaseclient.IsCramerBitstreamMigrationDone"));
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
		BksHelper theHelper = new BksHelper();

        
		@SuppressWarnings("unchecked")
		ProviderService service = ((JAXBElement<ProviderService>)(serviceInput.getPayload())).getValue();

		String returnObject = null;
		ConsolidateSubscriptionDataInput input = null;
		try {
			// get the input object from the service object
			input = ((ConsolidateSubscriptionDataService)service).getConsolidateSubscriptionDataInput();
			returnObject = doExecute(input,theHelper);
			logger.info("Output XML:");
			logger.info(returnObject);
		} catch (Throwable e) {
			String errorMessage = "Unexpected error occured during the Exceution.";
			logger.fatal(errorMessage, e);
			e.printStackTrace();
			throw new MCFException();
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
			logToDB(errorCode,errorText,correlationId,startTime,endTime,cacheHit);
		}
		if (returnObject == null)
			return null;
		return serviceInput.createResponse(serviceInput.getServiceName(), serviceInput.getServiceVersion(), returnObject);
	}
	
	private void logToDB(String errorCode, String errorText, String correlationId, long startTime, long endTime, boolean cacheHit) {
		long duration = endTime - startTime;
		logger.info("duration : "+duration);
		GeneralDAO dao = null;
		try {
			dao = (GeneralDAO)DatabaseClient.getBksDaoForTargetDb("onl","GeneralDAO");
		} catch (Exception e1) {
		}
		if (dao != null) {
			dao.insertLog(errorCode, errorText,
					(errorCode == null) ? "SUCCESS" : "FAILED", null, null,
					correlationId, "ConsolidateSubscriptionData", cacheHit ? "Y" : "N",
					new Timestamp(startTime), new Timestamp(endTime));
		}
		try {
			dao = null;
			dao = (GeneralDAO)DatabaseClient.getBksDaoForTargetDb("effonl","GeneralDAO");
		} catch (Exception e) {
		}
		if (dao != null) {
			dao.insertLog(errorCode, errorText,
					(errorCode == null) ? "SUCCESS" : "FAILED", null, null,
					correlationId, "ConsolidateSubscriptionData", cacheHit ? "Y" : "N",
					new Timestamp(startTime), new Timestamp(endTime));
		}
	}
	
	private String getInfportData (String tasi,CtInstallationAddress address) throws Exception{
		String lineID=null;
		try {
			MtGetInstallationAddressRequest  getInstallReq= new MtGetInstallationAddressRequest();
			if (tasi==null)
				return null;
			getInstallReq.setLocation(tasi);
			MtGetInstallationAddressResponse getInstallresp = (MtGetInstallationAddressResponse) callInfportService(getInstallReq,tasi);
			List<CtInstallationAddress> instalAddr = getInstallresp.getInstallationAddress();
			String clientID=null;
			if (instalAddr!=null && instalAddr.size() == 1)
				clientID=instalAddr.get(0).getClientId();
			MtGetCombinedCustomerDataRequest infportreq = new MtGetCombinedCustomerDataRequest();
			infportreq.setClientId(clientID);
			infportreq.setLocation(tasi);
			infportreq.setLookupPhoneNumbers(true);
			infportreq.setLookupLineParameters(true);
			MtGetCombinedCustomerDataResponse infportresp = (MtGetCombinedCustomerDataResponse) callInfportService(infportreq,tasi);
			instalAddr = infportresp.getInstallationAddress();
			if (instalAddr!=null && instalAddr.size() == 1) {
				address.setStreet(instalAddr.get(0).getStreet());
				address.setHouseNumber(instalAddr.get(0).getHouseNumber());
				address.setHouseNoAddition(instalAddr.get(0).getHouseNoAddition());
				address.setPostcode(instalAddr.get(0).getPostcode());
				address.setCity(instalAddr.get(0).getCity());
			}
			if (infportresp.getLineParameters()!=null && infportresp.getLineParameters().size()>0)
				lineID = infportresp.getLineParameters().get(0).getLineid();
		} catch (Exception e) {
			logger.error("Call to infport service failed.",e);
			throw e;
		}

		return lineID;		
	}
	/** 
	 * This method executes the data retrieval queries and returns the result in output object.
	 * The result is also cached in JCS cache to speed up the retrieval in case anything goes
	 * wrong.
	 * @param input 
	 * @param theHelper 
	 * @param unvalidatedXml 
	 */
	public String doExecute(ConsolidateSubscriptionDataInput input, BksHelper theHelper) throws Exception {
		String returnXml = null;
		String errorText = null;
		String errorCode = null;
		ArrayList<Products> product;
		String customerNumber = null;
		String bundleId = null;
		ActionList actionlist = new ActionList();
		HashMap<String, Object> cramerData = null;
		HashMap<String, Object> aidaData = null;
		ArrayList<HashMap<String, Object[]>> ccmData = new ArrayList<HashMap<String,Object[]>>();
		HashMap<String, ArrayList<String[]>> servCodeMap=new HashMap<String, ArrayList<String[]>>();
		HashMap<String, ArrayList<String>> zarCustomerData = new HashMap<String, ArrayList<String>>();
		HashMap<String, ArrayList<String>> zarAccNumData = new HashMap<String, ArrayList<String>>();
		String extDatabase="Cramer";
		ConsolidateSubscriptionDataDAO dao = 
			(ConsolidateSubscriptionDataDAO) DatabaseClient.getBksDataAccessObject(null,"ConsolidateSubscriptionDataDAO");
		bundleId=input.getBundleId();
		String orderId=input.getOrderId();
		ArrayList<HashMap<String, Object>> bundle = null;
		if (bundleId==null){
			bundle = dao.getDataBySerSu(input.getServiceSubscriptionId());
			if (bundle!=null&&bundle.size()>0){
				customerNumber = bundle.get(0).get("CUSTOMER_NUMBER").toString();
				bundleId=bundle.get(0).get("BUNDLE_ID").toString();
			}
		} else {
			bundle = dao.getDataByBundleId(bundleId);
			if (bundle!=null&&bundle.size()>0)
				customerNumber = bundle.get(0).get("CUSTOMER_NUMBER").toString();
		}
		try {
			try {
				product=loadCcmData(dao,bundleId,ccmData,servCodeMap);
			} catch (BksDataException e) {
				returnXml =  populateOutputObject(dao,null,null, null,bundleId,theHelper,
						null,null,null,null,null,null,false,false,false,false,
						Products.NOT_SUPPORTED,Products.NOT_SUPPORTED,null,"E0059", actionlist);
				return returnXml;
			}
			if (isOtherOrderPending(dao,orderId,ccmData)){
				returnXml =  populateOutputObject(dao,null,null, null,null,theHelper,
						null,null,null,null,null,null,false,false,false,false,
						Products.NOT_SUPPORTED,Products.NOT_SUPPORTED,null,null, actionlist);
				logger.warn("There are other pending orders for this bundle ("+bundleId+"). No reconciliation processed.");
				return returnXml;
			}
			if (ccmData==null || ccmData.size()==0)
				throw new BksDataException("BKS0026", "Couldn't find serviceSubscription for input "+
						((bundleId!=null)?bundleId:input.getServiceSubscriptionId()));
			boolean cramerValid = false;
			boolean aidaValid = false;
			boolean zarValid = false;
			ArrayList<String[]> internalErrorList=new ArrayList<String[]>();
			if (product.get(0)!=Products.NOT_SUPPORTED&&
				(product.get(1)!=Products.NOT_SUPPORTED||product.get(0)!=Products.UNDEFINED)) {
				if (input.isSkipCramer() == null || !input.isSkipCramer()) {
					String ccmTasi= getLeadingValue(ccmData,"V0152",true);
					cramerData= loadCramerData(customerNumber,ccmTasi,actionlist, internalErrorList);
					cramerValid = true;
				}
				if (input.isSkipAida() == null || !input.isSkipAida()) {
					extDatabase = "Aida";
					String ccmTsg= getLeadingValue(ccmData,"V0154",true);
					aidaData= loadAidaData(customerNumber,ccmTsg,actionlist);
					aidaValid = true;
				}
				if (input.isSkipZar() == null || !input.isSkipZar()) {
					extDatabase = "Zar";
					loadZarData(dao,customerNumber, ccmData, zarCustomerData,
							zarAccNumData);
					zarValid = true;
				}
			} else {
				if (input.isSkipCramer() == null || !input.isSkipCramer())
					cramerValid = true;
				if (input.isSkipAida() == null || !input.isSkipAida())
					aidaValid = true;
				if (input.isSkipZar() == null || !input.isSkipZar()) 
					zarValid = true;
			}
			boolean infportValid= (input.isSkipInfPort() == null || !input.isSkipInfPort());
			returnXml =  populateOutputObject(dao,errorCode, errorText, customerNumber,bundleId,theHelper,
					ccmData,cramerData,aidaData,zarCustomerData,zarAccNumData,
					servCodeMap,cramerValid,aidaValid,zarValid,infportValid,product.get(0), product.get(1),internalErrorList,null, actionlist);
		} catch (BksDataException e) {
			errorCode=e.getCode();
			errorText=e.getText();
			returnXml =  populateOutputObject(dao,errorCode, errorText, null,null,theHelper,
					null,null,null,null,null,null,false,false,false,false,Products.UNDEFINED,Products.UNDEFINED,null,null, actionlist);
		} catch (SQLException e) {
			errorCode="BKS0030";
			errorText="Cannot connect to "+extDatabase+" database.";
			logger.error(errorText, e);
			returnXml =  populateOutputObject(dao,errorCode, errorText, null,null,theHelper,
					null,null,null,null,null,null,false,false,false,false,Products.UNDEFINED,Products.UNDEFINED,null,null, actionlist);
		} catch (Exception e) {
			errorCode="BKS0008";
			if (e.getMessage()!=null)
				errorText=e.getMessage();
			else
				errorText="Unexpected error occured during the Exceution.";
			logger.error(errorText, e);
			returnXml =  populateOutputObject(dao,errorCode, errorText, null,null,theHelper,
					null,null,null,null,null,null,false,false,false,false,Products.UNDEFINED,Products.UNDEFINED,null,null, actionlist);
		} finally {
			dao.closeConnection();
		}
		return returnXml;
	}

	private boolean isOtherOrderPending(ConsolidateSubscriptionDataDAO dao,
			String orderId, ArrayList<HashMap<String, Object[]>> ccmData) throws Exception {
		String serSuId=null;
		for (int i = 0; i < ccmData.size(); i++) {
			serSuId = (String) ccmData.get(i).get("ServiceSubscriptionId")[0];
			ArrayList<HashMap<String, Object>> tmpData = dao.getOpenOrders(serSuId,orderId);
			if (tmpData!=null&&tmpData.size()>0)
				return true;
		}
		return false;
	}
	private ArrayList<Products> loadCcmData(ConsolidateSubscriptionDataDAO dao, String bundleId, 
			ArrayList<HashMap<String, Object[]>> ccmData, HashMap<String, ArrayList<String[]>> servCodeMap) throws Exception {
		ArrayList<HashMap<String, Object>> bundleData = dao.getCharValuesForBundle(bundleId);
		ArrayList<HashMap<String, Object>> tmpData = dao.getCharValuesForChildServices(bundleId);
		if (tmpData!=null&&tmpData.size()>0)
			bundleData.addAll(tmpData);
		int index = -1;
		ArrayList<Products> product = new ArrayList<Products>(); 
		String firstAccessService=null;
		product.add(Products.UNDEFINED);
		product.add(Products.UNDEFINED);
		for (int i = 0; i < bundleData.size(); i++) {
			String charCode = (String)bundleData.get(i).get("SERVICE_CHARACTERISTIC_CODE");
			String serSuId = (String)bundleData.get(i).get("SERVICE_SUBSCRIPTION_ID");
			String status = (String)bundleData.get(i).get("CSC_STATUS");
			String serviceCode = (String)bundleData.get(i).get("SERVICE_CODE");
			boolean serviceActive = !((String)bundleData.get(i).get("ITEM_STATUS")).equals("DEACT");
			String charValue = (String)bundleData.get(i).get("VALUE");

			if (charValue==null)
				continue;
			determainProduct(serviceCode,charCode,status,serviceActive,product);
			if (Arrays.asList(mainServiceCodes).contains(serviceCode) && serviceActive){
				if (firstAccessService==null)
					firstAccessService=serSuId;
				else if (!firstAccessService.equals(serSuId))
					throw new BksDataException("BKS0050","There are more tahn one access item in the bundle "+bundleId);
			}
			
			if (servCodeMap.get(serviceCode)==null)
				servCodeMap.put(serviceCode,new ArrayList<String[]>());
			if(!isAlreadyInList(servCodeMap.get(serviceCode),serSuId))
				servCodeMap.get(serviceCode).add(new String[] {serSuId,serviceActive?"ACTIVE":"DEACT"});
			if (index == -1 || !serSuId.equals(ccmData.get(index).get("ServiceSubscriptionId")[0])){
				ccmData.add(new HashMap<String, Object[]>());
				ccmData.get(++index).put("ServiceSubscriptionId", new String[]{serSuId});
				ccmData.get(index).put("BundleId", new String[]{bundleId});
				ccmData.get(index).put("serviceCode", new String[]{serviceCode});
				if (serviceActive)
					populateAddress(dao,serSuId,ccmData.get(index));
			}
			if (Arrays.asList(mainAccNumCodes).contains(charCode)||
					Arrays.asList(accNumRangeCodes).contains(charCode)){
				ArrayList<String> existAccnumList=null;
				if (ccmData.get(index).get("AccessNumbers")!=null) 
					existAccnumList = (ArrayList<String>) ccmData.get(index).get("AccessNumbers")[0];

				if (existAccnumList== null)
					existAccnumList=new ArrayList<String>();
				if (charValue!=null && serviceActive && status.equals("ACTIVE")) {
					existAccnumList.add(charValue);
					ccmData.get(index).put("AccessNumbers", new Object[]{existAccnumList});
				}
			} 
			if ((serviceActive &&status.equals("ACTIVE")) || (!serviceActive &&
					(charCode.equals("V0152") || charCode.equals("V0154"))))
				ccmData.get(index).put(charCode,new String[]{charValue,serviceActive?"ACTIVE":"DEACT"});
		}
		return product;
	}

	private void populateAddress(ConsolidateSubscriptionDataDAO dao, String serSuId,
			HashMap<String, Object[]> CcmSubscrData) throws Exception {
		ArrayList<HashMap<String, Object>> addrChar = dao.getAddressCharacteristic(serSuId,"V0014");
		Address addr= new Address();
		if (addrChar!=null && addrChar.size() > 0) {
			addr.setStreet((String) addrChar.get(0).get("STREET_NAME"));
			addr.setStreetNumber((String) addrChar.get(0).get("STREET_NUMBER"));
			addr.setStreetNumberSuffix((String) addrChar.get(0).get("STREET_NUMBER_SUFFIX"));
			addr.setPostalCode((String) addrChar.get(0).get("POSTAL_CODE"));
			addr.setCity((String) addrChar.get(0).get("CITY_NAME"));
			addr.setCitySuffix((String) addrChar.get(0).get("CITY_SUFFIX_NAME"));
			CcmSubscrData.put("V0014",new Object[] {addr});
		}
	}
	
	private void determainProduct(String serviceCode, String charCode, String status, boolean serviceActive, ArrayList<Products> product) {
		if (serviceCode.equals("I1210")||serviceCode.equals("I1209"))
			product.set(serviceActive?0:1,Products.NGN);
		if (serviceCode.equals("I1215")||serviceCode.equals("I1216")||serviceCode.equals("I1207"))
			product.set(serviceActive?0:1,Products.BUSINESS_DSL);
		if (serviceCode.equals("Wh003")||serviceCode.equals("Wh010"))
			product.set(serviceActive?0:1,Products.ONE_AND_ONE);
		if (serviceCode.equals("I1213"))
			product.set(serviceActive?0:1,Products.BIT_STREAM);
		if (serviceCode.equals("I1043"))
			product.set(serviceActive?0:1,Products.DSLR);
		if (serviceCode.equals("I1290"))
			product.set(serviceActive?0:1,Products.LTE);
		if ((serviceCode.equals("VI010")||serviceCode.equals("VI020")||serviceCode.equals("VI021"))
				&& product.get(serviceActive?0:1)==Products.UNDEFINED)
			product.set(serviceActive?0:1,Products.NOT_SUPPORTED);
		if ((serviceCode.equals("V0003")||serviceCode.equals("V0010")||
			serviceCode.equals("V0011")) &&  product.get(serviceActive?0:1)!=Products.ISDN_WITH_ACS){
			product.set(serviceActive?0:1,Products.ISDN_WITHOUT_ACS);
			if (charCode.equals("V0153") && ((serviceActive&&status.equals("ACTIVE"))||(!serviceActive&&!status.equals("ACTIVE"))))
				product.set(serviceActive?0:1,Products.ISDN_WITH_ACS);
		}
	}
	private boolean isAlreadyInList(ArrayList<String[]> serviceCodeIds, String serSuId) {
		for (int i = 0; i < serviceCodeIds.size(); i++) {
			if (serSuId.equals(serviceCodeIds.get(i)[0]))
				return true;
		}
		return false;
	}
	private void loadZarData(ConsolidateSubscriptionDataDAO dao, String customerNumber, ArrayList<HashMap<String,Object[]>> ccmData,
			HashMap<String,ArrayList<String>> zarCustomerData, HashMap<String,ArrayList<String>> zarAccNumData) throws Exception {
		for (int i=0;i<ccmData.size();i++){
			ArrayList<String> accNums = null;
			if (ccmData.get(i).get("AccessNumbers")!=null) 
				accNums = (ArrayList<String>)ccmData.get(i).get("AccessNumbers")[0];
			if (accNums==null)
				continue;
			for (int j = 0;j < accNums.size(); j++) {
				String accnum = accNums.get(j);
				String[] accnumParts= accnum.split(";");
				String number=null;
				if (accnumParts.length == 3)
					number=accnumParts[2];
				if (accnumParts.length > 4)
					number=accnumParts[2]+";"+accnumParts[3]+";"+accnumParts[4];
				HashMap<String, Object> accInfo = dao.GetZarAccNumInfo(null, "0"+accnumParts[1],number);
				if (accInfo!=null) {
					ArrayList<String> accNumList = null;
					String groupId = (String) accInfo.get("groupId");
					if (groupId == null)
						groupId = "UNIQUE_NULL_REPLACEMENT";
					if (zarCustomerData.containsKey(groupId))
						accNumList = zarCustomerData.get(groupId);
					else
						accNumList = new ArrayList<String>();
					accNumList.add(accnum);
					zarCustomerData.put(groupId, accNumList);
					if (!customerNumber.equals((String) accInfo
							.get("customerNumber"))) {
						ArrayList<String> custnums = new ArrayList<String>();
						custnums.add((String) accInfo.get("customerNumber"));
						zarAccNumData.put(accnum, custnums);
					}
				} 
			}
		}
	}
	private HashMap<String,Object> loadCramerData(String customerNumber, String ccmTasi, ActionList actionlist, ArrayList<String[]> internalErrorList) throws Exception {
		CramerDAO cdi = new CramerDAOImpl();
		boolean mockCramer=false;
		try {
			String useCramerMock = DatabaseClientConfig.getSetting("USE_CRAMER_MOCK_UP");
			mockCramer=(useCramerMock.equals("Y"));
		} catch (Exception e)  {
		}
		HashMap<String,Object> cramerData=null;
		if (!mockCramer){
			Connection connection = null;
			try {
				connection = cdi.initDatabase();
				cramerData = loadCramerRecords(cdi, connection, ccmTasi, customerNumber,actionlist,internalErrorList);
			}finally {
				if (connection!=null)
					cdi.closeConnection(connection);
			}
		} else {
			cramerData=new HashMap<String,Object>();
			readMockFile("mockCramerData.txt",cramerData,"V0152",customerNumber,ccmTasi,actionlist);
			if (cramerData.isEmpty())
				return null;
		}
		return cramerData;
	}
	private HashMap<String, Object> loadCramerRecords(CramerDAO cdi,
			Connection connection, String ccmTasi, String customerNumber,
			ActionList actionlist, ArrayList<String[]> internalErrorList) throws Exception {
		HashMap<String, Object> cramerData = new HashMap<String, Object>();
		String tasi = ccmTasi;
		ActionItem ai= new ActionItem();
		if (ccmTasi!=null){
			cdi.getCramerDataByTasi(connection, ccmTasi, cramerData, internalErrorList);
			if (!cramerData.isEmpty()&&!customerNumber.equals((String)cramerData.get("customerNumber"))){
				ArrayList<HashMap<String, Object>> cramerCustList = cdi.getCramerDataByCustomerNumber(connection, customerNumber, internalErrorList);
				if (cramerCustList.isEmpty()){
					String s1 = String.format(errorMap.get("E0068"),(String)cramerData.get("customerNumber"), customerNumber, ccmTasi);
					populateActionItem(ai,null,null,
							customerNumber,null,(String)cramerData.get("customerNumber"),null,null,null,"Report","CUSTOMER_NUMBER",
							"Customer Number",null,"E0068",s1,"Cramer",null,null);
					actionlist.getActionItem().add(ai);
				} else {
					String s1 = String.format(errorMap.get("E0067"), customerNumber);
					populateActionItem(ai,null,null,
							customerNumber,null,null,null,null,null,"Report","None",
							"General Error",null,"E0067",s1,"Cramer",null,null);
					actionlist.getActionItem().add(ai);
					return null;
				}					
			}
		}

		if (cramerData==null || cramerData.isEmpty()){
			ArrayList<HashMap<String, Object>> cramerCustList = cdi.getCramerDataByCustomerNumber(connection, customerNumber, internalErrorList);
			if (cramerCustList.isEmpty()){
				String s1 = String.format(errorMap.get("E0001"), "CCM","Cramer");
				populateActionItem(ai,null,null,
						customerNumber,null,null,null,null,null,"Report","CUSTOMER_NUMBER",
						"Customer Number Cramer",null,"E0001",s1,"Cramer",null,null);
				actionlist.getActionItem().add(ai);
				return null;
			} else if (cramerCustList.size()==1){
				String errorCode = charTargetSystem.get("V0152")[4];
				String errorText = String.format(errorMap.get(errorCode),ccmTasi);
				String cramerTasi = (String)cramerCustList.get(0).get("V0152");
				populateActionItem(ai,(String) cramerCustList.get(0).get("ServiceSubscriptionId"),"V0154",
					ccmTasi,null,cramerTasi,null,null,"Cramer","Change",charTargetSystem.get("V0152")[2],
					charTargetSystem.get("V0152")[1],cramerTasi,errorCode,errorText,"CCM",null,null);
				actionlist.getActionItem().add(ai);
				tasi= cramerTasi;
				cramerData = cramerCustList.get(0);
			} else {
				String s1 = String.format(errorMap.get("E0067"), customerNumber);
				populateActionItem(ai,null,null,
						customerNumber,null,null,null,null,null,"Report","None",
						"General Error",null,"E0067",s1,"Cramer",null,null);
				actionlist.getActionItem().add(ai);
				return null;
			}					
		}
		return cramerData;
	}

	private HashMap<String,Object> loadAidaData(String customerNumber, String ccmTsg, ActionList actionlist) throws Exception {
		AidaDAO cdi = new AidaDAOImpl();
		boolean mockAida=false;
		try {
			String useAidaMock = DatabaseClientConfig.getSetting("USE_AIDA_MOCK_UP");
			mockAida=(useAidaMock.equals("Y"));
		} catch (Exception e)  {
		}
		HashMap<String,Object> aidaData=null;
		if (!mockAida){
			Connection connection = null;
			try {
				connection = cdi.initDatabase(0,"aida");
				aidaData = loadAidaRecords(cdi, connection, ccmTsg, customerNumber,actionlist);
			}finally {
				if (connection!=null)
					cdi.closeConnection(connection);
			}
		} else {
			Connection connection = null;
			try {
				connection = cdi.initDatabase(0,"mock");
				aidaData = loadAidaRecords(cdi, connection, ccmTsg, customerNumber,actionlist);
			}finally {
				if (connection!=null)
					cdi.closeConnection(connection);
			}
		}
		return aidaData;
	}
	public HashMap<String, Object> loadAidaRecords(AidaDAO cdi,Connection connection,String ccmTsg, String customerNumber, ActionList actionlist) throws Exception
	{
		HashMap<String, Object> aidaData = null;
		String tsg = ccmTsg;
		ActionItem ai= new ActionItem();
		if (ccmTsg!=null){
			aidaData = cdi.getTechServRecByTSG(connection, ccmTsg);
			if (!aidaData.isEmpty()){
				if (!customerNumber.equals((String)aidaData.get("customerNumber"))){
					ArrayList<HashMap<String, Object>> aidaCustList = cdi.getTechServRecByCustomer(connection,customerNumber);
					if (aidaCustList.isEmpty()){
						String s1 = String.format(errorMap.get("E0066"),(String)aidaData.get("customerNumber"), customerNumber, ccmTsg);
						populateActionItem(ai,null,null,
								customerNumber,(String)aidaData.get("customerNumber"),null,null,null,null,"Report","CUSTOMER_NUMBER",
								"Customer Number",null,"E0066",s1,"AIDA",null,null);
						actionlist.getActionItem().add(ai);
					} else {
						String s1 = String.format(errorMap.get("E0065"), customerNumber);
						populateActionItem(ai,null,null,
								customerNumber,null,null,null,null,null,"Report","None",
								"General Error",null,"E0065",s1,"AIDA",null,null);
						actionlist.getActionItem().add(ai);
						return null;
					}	
				}
			}
		}
		if (aidaData==null || aidaData.isEmpty()){
			ArrayList<HashMap<String, Object>> aidaCustList = cdi.getTechServRecByCustomer(connection,customerNumber);
			if (aidaCustList.isEmpty()){
					String s1 = String.format(errorMap.get("E0017"), ccmTsg);
					populateActionItem(ai,null,null,
							ccmTsg,null,null,null,null,null,"Report","STRING",
							"Technical-Service-Group",null,"E0017",s1,"AIDA",null,null);
					actionlist.getActionItem().add(ai);
				return null;
			} else if (aidaCustList.size()==1){
				String aidaTsg = (String)aidaCustList.get(0).get("V0154");
				tsg= aidaTsg;
				aidaData =  aidaCustList.get(0);
			} else {
				String s1 = String.format(errorMap.get("E0065"), customerNumber);
				populateActionItem(ai,null,null,
						customerNumber,null,null,null,null,null,"Report","None",
						"General Error",null,"E0065",s1,"AIDA",null,null);
				actionlist.getActionItem().add(ai);
				return null;
			}					
		}
		cdi.getIpAddressInfo(connection, tsg,aidaData);
		cdi.getDeviceInfo(connection, tsg, aidaData);
		cdi.getIpTvInfo(connection, tsg, aidaData);
		cdi.getSipInfo(connection, tsg, aidaData);
		return aidaData;
	}
	private void readMockFile(String name, HashMap<String, Object> cramerData, String groupingKey, String customerNumber, String ccmTasi, ActionList actionlist) throws Exception {
		Resource resource = new ClassPathResource(name);
		FileReader fr = new FileReader(resource.getFile());		   
		BufferedReader br = new BufferedReader(fr);
		HashMap<String,HashMap<String, Object>> cramerTasiData=new HashMap<String, HashMap<String,Object>>();
		ArrayList<HashMap<String, Object>> cramerCustData=new ArrayList<HashMap<String,Object>>();
		try {
			String line = br.readLine();
			HashMap<String, Object> item=null;
			while (line != null) {
				String[] fields = line.split(";;");
				if (groupingKey.equals(fields[0])) {
					item = new HashMap<String, Object>();
					cramerTasiData.put(fields[1].trim(),item);
					cramerCustData.add(item);
				}
				if (fields.length>1) {
					if (fields.length==2)
						item.put(fields[0], fields[1].trim());
					else {
						ArrayList<String> list = new ArrayList<String>();
						item.put(fields[0], list);
						for (int i=1;i<fields.length;i++)
							list.add(fields[i]);
					}
				}
				line = br.readLine();
			}
			ArrayList<HashMap<String, Object>>  filteredCustList = new ArrayList<HashMap<String, Object>> ();
		        
			for (HashMap<String, Object> custMap: cramerCustData) {
				if (customerNumber.equals(custMap.get("customerNumber"))) {
					filteredCustList.add(custMap);
				}
			}

			ActionItem ai = new ActionItem();
			if (cramerTasiData.containsKey(ccmTasi)) {
				cramerData.putAll(cramerTasiData.get(ccmTasi));
				if (!customerNumber.equals(cramerData.get("customerNumber"))){
					logger.debug("Wrong customer number in Cramer "+cramerData.get("customerNumber")+" CCM value: "+customerNumber);
					if (filteredCustList.isEmpty()){
						String s1 = String.format(errorMap.get("E0068"),(String)cramerData.get("customerNumber"), customerNumber, ccmTasi);
						populateActionItem(ai ,null,null,
								customerNumber,null,(String)cramerData.get("customerNumber"),null,null,null,"Report","CUSTOMER_NUMBER",
								"Customer Number",null,"E0068",s1,"Cramer",null,null);
						actionlist.getActionItem().add(ai);
					} else {
						String s1 = String.format(errorMap.get("E0067"), customerNumber);
						populateActionItem(ai,null,null,
								customerNumber,null,null,null,null,null,"Report","None",
								"General Error",null,"E0067",s1,"Cramer",null,null);
						actionlist.getActionItem().add(ai);
						return;
					}					
				}
			} else {
				if (filteredCustList.isEmpty()){
					String s1 = String.format(errorMap.get("E0001"), "CCM","Cramer");
					populateActionItem(ai,null,null,
							customerNumber,null,null,null,null,null,"Report","CUSTOMER_NUMBER",
							"Customer Number Cramer",null,"E0001",s1,"Cramer",null,null);
					actionlist.getActionItem().add(ai);
					return;
				} else if (filteredCustList.size()==1){
					String errorCode = charTargetSystem.get("V0152")[4];
					String errorText = String.format(errorMap.get(errorCode),ccmTasi);
					String cramerTasi = (String)filteredCustList.get(0).get("V0152");
					populateActionItem(ai,(String) filteredCustList.get(0).get("ServiceSubscriptionId"),"V0154",
						ccmTasi,null,cramerTasi,null,null,"Cramer","Change",charTargetSystem.get("V0152")[2],
						charTargetSystem.get("V0152")[1],cramerTasi,errorCode,errorText,"CCM",null,null);
					actionlist.getActionItem().add(ai);
					cramerData.putAll(filteredCustList.get(0));
				} else {
					String s1 = String.format(errorMap.get("E0067"), customerNumber);
					populateActionItem(ai,null,null,
							customerNumber,null,null,null,null,null,"Report","None",
							"General Error",null,"E0067",s1,"Cramer",null,null);
					actionlist.getActionItem().add(ai);
					return;
				}					
			}
		} finally {
			br.close();
		}
	}
	private String getAidaParam(String paramName, String paramValue) throws Exception {
		AidaDAO cdi = new AidaDAOImpl();
		boolean mockAida=false;
		try {
			String useAidaMock = DatabaseClientConfig.getSetting("USE_AIDA_MOCK_UP");
			mockAida=(useAidaMock.equals("Y"));
		} catch (Exception e)  {
		}
		Connection connection = null;
		if (mockAida)
			connection = cdi.initDatabase(0,"mock");
		else
			connection = cdi.initDatabase(0,"aida");
		String tsg = null;
		try {
			tsg = cdi.loadValueFromAIDA(connection, paramName, paramValue);
		}finally {
			cdi.closeConnection(connection);
		}
		return tsg;
	}
	private String populateOutputObject(ConsolidateSubscriptionDataDAO dao, String errorCode, 
			String errorText, String customerNumber, String bundleId, BksHelper theHelper,ArrayList<HashMap<String,Object[]>> ccmData, 
			HashMap<String,Object> cramerMap, HashMap<String,Object> aidaMap, 
			HashMap<String,ArrayList<String>> zarCustomerData, HashMap<String,ArrayList<String>> zarAccNumData, 
			HashMap<String, ArrayList<String[]>> servCodeMap, boolean cramerValid, boolean aidaValid,boolean zarValid,
			boolean infportValid, Products actProduct, Products deactProduct, ArrayList<String[]> internalErrorList,String externalErrCode, ActionList actionlist) throws Exception {
			 
		String returnXml = null;
		ConsolidateSubscriptionDataOutput output = new ConsolidateSubscriptionDataOutput();
		if (errorCode!=null || errorText!=null){
			output.setResult(false);
			output.setErrorCode(errorCode);
			if (errorText.length() > 255)
				errorText = errorText.substring(0, 255);
			output.setErrorText(errorText);
		} else {
			output.setResult(true);
			output.setCramerValidated(cramerValid);
			output.setAidaValidated(aidaValid);
			output.setZarValidated(zarValid);
			output.setInfPortValidated(infportValid);
			output.setActionList(actionlist);
			if (externalErrCode!=null){
				ActionItem ai = new ActionItem();
				populateActionItem(ai,null,null,
						bundleId,null,null,null,null,null,"Report","BUNDLE_ID",
						"CCM Bundle",null,externalErrCode,errorMap.get(externalErrCode),"CCM",null,null);
				actionlist.getActionItem().add(ai);
			} else if (actProduct!=Products.NOT_SUPPORTED &&
				(deactProduct!=Products.NOT_SUPPORTED||actProduct!=Products.UNDEFINED)){
				ActionItem ai = new ActionItem();
				populateInternalErrorOnActionList(internalErrorList,actionlist);
				if (actProduct==Products.UNDEFINED&&deactProduct!=Products.UNDEFINED){
					if (aidaMap!=null){
						String s1 = String.format(errorMap.get("E0053"), "Aida");
						populateActionItem(ai,null,"V0154",
								null,getLeadingValue(ccmData, "V0154",false),null,null,null,null,"Report","STRING",
								"Technical-Service-Group",null,"E0053",s1,"AIDA",null,null);
						actionlist.getActionItem().add(ai);
					}
					if (cramerMap!=null){
						String s1 = String.format(errorMap.get("E0053"), "Cramer");
						populateActionItem(ai,null,"V0152",
								null,null,getLeadingValue(ccmData, "V0152",false),null,null,null,"Report","STRING",
								"Technische_Service_ID",null,"E0053",s1,"Cramer",null,null);
						actionlist.getActionItem().add(ai);
					}			
				} else if (actProduct!=Products.NOT_SUPPORTED) {
					consolidateCcm(dao,actionlist, ccmData, cramerMap, aidaMap, actProduct,cramerValid, aidaValid, infportValid);
					consolidateAccessNumbers(dao,actionlist,ccmData,cramerMap,aidaMap, actProduct,cramerValid, aidaValid);
					consolidateVoipAccounts(dao, actionlist, ccmData, aidaMap, actProduct, aidaValid);
					if (aidaValid)
						consolidateAida(dao,actionlist, ccmData, aidaMap,servCodeMap, actProduct);
					if (cramerValid)
						consolidateCramer(dao,actionlist, ccmData, cramerMap,servCodeMap, actProduct);
					if (zarValid){
						consolidateZarCustomerAccNum(dao,actionlist,customerNumber,ccmData,zarCustomerData,zarAccNumData, actProduct);
						consolidateZarAccNum(actionlist,customerNumber,ccmData,zarAccNumData);
					}
				}
			}
		}
		try{
			returnXml = theHelper.serialize(output,"net.arcor.ccm.epsm_ccm_consolidatesubscriptiondata_001","schema/EPSM-CCM-ConsolidateSubscriptionData-001.xsd" ,"ConsolidateSubscriptionData");
		} catch (XMLException e) {
			throw e;
		}
		return returnXml;
	}
	private void populateInternalErrorOnActionList(
			ArrayList<String[]> internalErrorList, ActionList actionlist) {
		for (int i = 0; i < internalErrorList.size(); i++) {
			ActionItem ai = new ActionItem();
			String errorCode = internalErrorList.get(i)[2];
			if (!errorCode.equals("DIM-21305") && !errorCode.equals("DIM-21306")){
				String s1 = String.format(errorMap.get("E0034"), "[CCM, Cramer]", errorCode, internalErrorList.get(i)[3]);
				populateActionItem(ai, null, null, null, null, null,null,
						null, null, "Report", "-","Internal Error "+internalErrorList.get(i)[1], null,
						"E0034", s1, internalErrorList.get(i)[0], internalErrorList.get(i)[1],
				"Access Number");
				actionlist.getActionItem().add(ai);
			}
		}
	}
	private void consolidateZarAccNum(ActionList actionlist, String customerNumber, ArrayList<HashMap<String,Object[]>> ccmData, 
			HashMap<String,ArrayList<String>> zarAccNumData) {
		for (int i=0;i<ccmData.size();i++){
			ArrayList<String> ccmAccNums = null;
			if (ccmData.get(i).get("AccessNumbers")!=null) 
				ccmAccNums = (ArrayList<String>)ccmData.get(i).get("AccessNumbers")[0];
			if (ccmAccNums==null)
				continue;
			String serSuId = (String)ccmData.get(i).get("ServiceSubscriptionId")[0];
			for (int j = 0;j < ccmAccNums.size(); j++) {
				String ccmAccnum = ccmAccNums.get(j);
				if (zarAccNumData!=null&&zarAccNumData.containsKey(ccmAccnum)&&
					zarAccNumData.get(ccmAccnum)!=null&&zarAccNumData.get(ccmAccnum).size()>0){
					ActionItem ai = new ActionItem();
					String s1 = String.format(errorMap.get("E0013"), "CCM","Zar",zarAccNumData.get(ccmAccnum).get(0));
					populateActionItem(ai,serSuId,null,customerNumber,null,null,zarAccNumData.get(ccmAccnum).get(0),null,null,"Change","CUSTOMER_NUMBER",
							"Wrong Zar Customer Number for "+ccmAccnum,customerNumber,"E0013",s1,"Zar",ccmAccnum,
							(ccmAccnum.split(";").length>3)?"ACC_NUM_RANGE":"MAIN_ACCESS_NUM");
					actionlist.getActionItem().add(ai);
				}
			}
		}
	}
	private void consolidateZarCustomerAccNum(ConsolidateSubscriptionDataDAO dao, ActionList actionlist, String customerNumber, 
			ArrayList<HashMap<String,Object[]>> ccmData, HashMap<String,ArrayList<String>> zarCustomerData,
			HashMap<String,ArrayList<String>> zarAccNumData, Products product) throws Exception {
		if ((zarCustomerData==null || zarCustomerData.isEmpty()) && product!=Products.UNDEFINED){
			ActionItem ai = new ActionItem();
			String s1 = String.format(errorMap.get("E0001"), "CCM","Zar");
			populateActionItem(ai,null,null,
					customerNumber,null,null,null,null,null,"Report","CUSTOMER_NUMBER",
					"Customer Number Zar",null,"E0001",s1,"Zar",null,null);
			actionlist.getActionItem().add(ai);
			return;
		}
		for (int i=0;i<ccmData.size();i++){
			ArrayList<String> accNums = null;
			if (ccmData.get(i).get("AccessNumbers")!=null) 
				accNums = (ArrayList<String>)ccmData.get(i).get("AccessNumbers")[0];
			if (accNums==null)
				continue;
			String serSuId = (String)ccmData.get(i).get("ServiceSubscriptionId")[0];
			String matchedGroup=null;
			boolean accNumsInDifferentGroups=false;
			boolean errorRaised = false;
			for (int j = 0;!accNumsInDifferentGroups&&j < accNums.size(); j++) {
				String accnum = accNums.get(j);
				boolean matchFound=false;
				Set<String> keys = zarCustomerData.keySet();
				Iterator<String> keyiter = keys.iterator();
				while (keyiter.hasNext()){
					String groupId = keyiter.next();
					if (zarCustomerData.get(groupId).contains(accnum)){
						matchFound=true;
						if (matchedGroup==null)
							matchedGroup=groupId;
						if (matchedGroup==null||!matchedGroup.equals(groupId)){
							populateZarGrInconst(serSuId, accNums, actionlist);
							accNumsInDifferentGroups=true;
						}
						break;
					}
				}
				if (!matchFound&&!accNumsInDifferentGroups&&
						(zarAccNumData==null||!zarAccNumData.containsKey(accnum))){
					ActionItem ai = new ActionItem();
					String s1 = String.format(errorMap.get("E0011"), "CCM","Zar");
					populateActionItem(ai,serSuId,null,accnum,null,null,null,null,null,"Change",
							(accnum.split(";").length>3)?"MAIN_ACCESS_NUM":"ACC_NUM_RANGE",
							"Zar Access Number Missing "+accnum,accnum,"E0011",s1,"Zar",null,null);
					actionlist.getActionItem().add(ai);
					errorRaised = true;
				}
			}
			if (!accNumsInDifferentGroups&&matchedGroup!=null&&!matchedGroup.equals("UNIQUE_NULL_REPLACEMENT")&&
				(accNums.size()!=zarCustomerData.get(matchedGroup).size())){
				for (int k = 0; k < zarCustomerData.get(matchedGroup).size(); k++) {
					if (!accNums.contains(zarCustomerData.get(matchedGroup).get(k))) {
						String zarAccnum = zarCustomerData.get(matchedGroup).get(k);
						ActionItem ai = new ActionItem();
						if (accessExistsInCCM(dao,zarAccnum)){
							String s1 = String.format(errorMap.get("E0038"), zarAccnum, serSuId);
							populateActionItem(ai, serSuId, null, zarAccnum, null, null, null, null, null,
									"Change", (zarAccnum.split(";").length>3)?"ACC_NUM_RANGE":"MAIN_ACCESS_NUM",
									"Zar Access Number inconsistant "+zarAccnum, zarAccnum, "E0038", s1,"Zar",null,null);
						} else {
							String s1 = String.format(errorMap.get("E0012"), "Zar","CCM");
							populateActionItem(ai, serSuId, null,null,null,null,zarAccnum,null,null, "Change", 
								 (zarAccnum.split(";").length>3)?"MAIN_ACCESS_NUM":"ACC_NUM_RANGE",
								  "Ccm Access Number Missing "+zarAccnum,null, "E0012",s1, "Zar",null,null);
						}
						actionlist.getActionItem().add(ai);
						errorRaised = true;
					}
				}
			}
			if (!errorRaised){
				Set<String> keys = ccmData.get(i).keySet();
				Iterator<String> keyiter = keys.iterator();
				while (keyiter.hasNext()){
					String charCode = keyiter.next();
					if (Arrays.asList(mainAccNumCodes).contains(charCode)||
							Arrays.asList(accNumRangeCodes).contains(charCode)){
						String ccmValue = (String) ccmData.get(i).get(charCode)[0];
						if (ccmValue==null)
							continue;
						ActionItem ai = new ActionItem();
						String desc="Access Number "+(Arrays.asList(mainAccNumCodes).indexOf(charCode)+1);
						String type="MAIN_ACCESS_NUM";
						if (Arrays.asList(accNumRangeCodes).contains(charCode)){
							desc="Access Number Range"+(Arrays.asList(accNumRangeCodes).indexOf(charCode)+1);
							type="ACC_NUM_RANGE";
						}
						populateActionItem(ai,(String) ccmData.get(i).get("ServiceSubscriptionId")[0],charCode,
								ccmValue,null,null,ccmValue,null,null,"Ignore",type,desc,null,null,null,"CCM",null,null);
						actionlist.getActionItem().add(ai);
					}
				}
			}
		}

	}
    private boolean accessExistsInCCM(ConsolidateSubscriptionDataDAO dao, String zarAccnum) throws Exception {
    	ArrayList<HashMap<String, Object>> accNum=dao.getAccessNumber(zarAccnum);
    	if (accNum!=null&&accNum.size()>0)
    		return true;
		return false;
	}
	private void populateZarGrInconst(String serSuId, ArrayList<String> accNums,
    		ActionList actionlist){
		for (int i = 0; i < accNums.size(); i++) {
			ActionItem ai = new ActionItem();
			String s1 = String.format(errorMap.get("E0038"), accNums.get(i), serSuId);
			populateActionItem(ai, serSuId, null, accNums.get(i), null, null, null, null, null,
					"Change", (accNums.get(i).split(";").length>3)?"ACC_NUM_RANGE":"MAIN_ACCESS_NUM",
					"Zar Access Number inconsistant "+accNums.get(i), accNums.get(i), "E0038", s1,"Zar",null,null);
			actionlist.getActionItem().add(ai);
		}

    }
	private boolean isAidaProduct(Products prod) {
		return prod!=Products.ISDN_WITHOUT_ACS && 
				prod!=Products.UNDEFINED&& 
				prod!=Products.LTE;
	}
	private boolean isCramerProduct(Products prod) {
		return (prod == Products.ISDN_WITH_ACS ||
				prod == Products.ISDN_WITHOUT_ACS ||
				prod == Products.NGN ||
				prod == Products.BIT_STREAM);
	}
	private void consolidateAccessNumbers(ConsolidateSubscriptionDataDAO dao, ActionList actionlist, 
			ArrayList<HashMap<String,Object[]>> ccmData, HashMap<String,Object> cramerData, 
			HashMap<String,Object> aidaData, 
			Products product, boolean cramerValid, boolean aidaValid) throws Exception {
		ArrayList<String> srcAidaList = (aidaData!=null)?(ArrayList<String>) aidaData.get("AccessNumbers"):null;
		ArrayList<String> srcCramerList = (cramerData!=null)?(ArrayList<String>) cramerData.get("AccessNumbers"):null;

		ArrayList<String> aidaList=null;
		if (srcAidaList!=null) 
			aidaList = (ArrayList<String>) srcAidaList.clone();
		ArrayList<String> cramerList=null;
		if (srcCramerList!=null) 
			cramerList = (ArrayList<String>) srcCramerList.clone();

		int currentPos=0;
		for (int i=0;i<ccmData.size();i++){
			Set<String> keys = ccmData.get(i).keySet();
			Iterator<String> keyiter = keys.iterator();
			while (keyiter.hasNext()){
				String charCode = keyiter.next();
				if (Arrays.asList(mainAccNumCodes).contains(charCode)||
						Arrays.asList(accNumRangeCodes).contains(charCode)){
					String ccmValue = (String) ccmData.get(i).get(charCode)[0];
					if (ccmValue==null)
						continue;
					ActionItem ai = new ActionItem();
					String desc="Access Number "+(Arrays.asList(mainAccNumCodes).indexOf(charCode)+1);
					String type="MAIN_ACCESS_NUM";
					if (Arrays.asList(accNumRangeCodes).contains(charCode)){
						desc="Access Number Range"+(Arrays.asList(accNumRangeCodes).indexOf(charCode)+1);
						type="ACC_NUM_RANGE";
					}
					if (aidaList!=null && isNumberInList(aidaList,ccmValue)&&
						cramerList!=null && isNumberInList(cramerList,ccmValue)){
						populateActionItem(ai,(String) ccmData.get(i).get("ServiceSubscriptionId")[0],charCode,
								ccmValue,ccmValue,ccmValue,null,null,null,"Ignore",type,desc,null,null,null,"CCM",null,null);
						cramerList.remove(ccmValue);
						aidaList.remove(ccmValue);
					} else if (aidaList!=null && isNumberInList(aidaList,ccmValue)){
						populateActionItem(ai,(String) ccmData.get(i).get("ServiceSubscriptionId")[0],charCode,
								ccmValue,ccmValue,null,null,null,"AIDA","Ignore",type,desc,null,null,null,"CCM",null,null);
						aidaList.remove(ccmValue);
					} else if (cramerList!=null && isNumberInList(cramerList,ccmValue)){
						populateActionItem(ai,(String) ccmData.get(i).get("ServiceSubscriptionId")[0],charCode,
								ccmValue,null,ccmValue,null,null,"Cramer","Ignore",type,desc,null,null,null,"CCM",null,null);
						cramerList.remove(ccmValue);
					} else {
						if (isAidaProduct(product) && aidaValid) {
							String s1 = String.format(errorMap.get("E0011"), "CCM","Aida");
							populateActionItem(ai,(String)ccmData.get(i).get("ServiceSubscriptionId")[0], 
									charCode,ccmValue,null,null,null,null,null,"Report",type,desc,ccmValue,"E0011",s1,"AIDA",null,null);
						}
						if (isCramerProduct(product) && cramerValid && (isCramerBitMigrationDone||cramerList!=null)) {
							String s1 = String.format(errorMap.get("E0011"), "CCM","Cramer");
							populateActionItem(ai,(String)ccmData.get(i).get("ServiceSubscriptionId")[0],
									charCode,ccmValue,null,null,null,null,null,"Report",type,desc,ccmValue,"E0011",s1,"Cramer",null,null);
						}
						if ((!isAidaProduct(product)||!aidaValid) &&
							(!isCramerProduct(product)||!cramerValid||!isCramerBitMigrationDone&&cramerList==null))
							continue;
					}
					actionlist.getActionItem().add(ai);
					currentPos++;
				}
			}
		}
		for (int i=0;aidaList!=null && i<aidaList.size();i++){
			if (aidaList.get(i)==null)
				continue;
			ActionItem ai = new ActionItem();
			String s1 = String.format(errorMap.get("E0012"), "Aida","CCM");
			populateActionItem(ai,null,(currentPos<10)?mainAccNumCodes[currentPos++]:null,
					null,aidaList.get(i),null,null,null,"AIDA","Report",
					"MAIN_ACCESS_NUM","Access Number "+currentPos,null,"E0012",s1,"AIDA",null,null);
			actionlist.getActionItem().add(ai);
		}
		for (int i=0;cramerList!=null && i<cramerList.size();i++){
			if (cramerList.get(i)==null)
				continue;
			ActionItem ai = new ActionItem();
			String s1 = String.format(errorMap.get("E0012"), "Cramer","CCM");
			populateActionItem(ai,null,(currentPos<10)?mainAccNumCodes[currentPos++]:null,
					null,null,cramerList.get(i),null,null,"Cramer","Report",
					"MAIN_ACCESS_NUM","Access Number "+currentPos,null,"E0012",s1,"Cramer",null,null);
			actionlist.getActionItem().add(ai);		
		}

	}
	private void consolidateVoipAccounts(ConsolidateSubscriptionDataDAO dao, ActionList actionlist, 
			ArrayList<HashMap<String,Object[]>> ccmData,	HashMap<String,Object> aidaData, 
			Products product, boolean aidaValid) throws Exception {
		
		if ((!isAidaProduct(product)||!aidaValid))
			return;

		ArrayList<String> srcAidaVoipList = (aidaData!=null)?(ArrayList<String>) aidaData.get("VoipAccounts"):null;

		ArrayList<String> aidaVoipList=null;
		if (srcAidaVoipList!=null) 
			aidaVoipList = (ArrayList<String>) srcAidaVoipList.clone();

		int currentPos=0;
		String type = "USER_ACCOUNT_NUM";
		for (int i=0;i<ccmData.size();i++){
			Set<String> keys = ccmData.get(i).keySet();
			Iterator<String> keyiter = keys.iterator();
			while (keyiter.hasNext()){
				String charCode = keyiter.next();
				if (Arrays.asList(sipAccountCodes).contains(charCode)){
					String ccmValue = (String) ccmData.get(i).get(charCode)[0];
					if (ccmValue==null)
						continue;
					ActionItem ai = new ActionItem();

					String desc = "Voip Account "+(Arrays.asList(sipAccountCodes).indexOf(charCode)+1);

					if (aidaVoipList!=null && isNumberInList(aidaVoipList,ccmValue)){
						populateActionItem(ai,(String) ccmData.get(i).get("ServiceSubscriptionId")[0],charCode,
								ccmValue,ccmValue,null,null,null,"AIDA","Ignore",type,desc,null,null,null,"CCM",null,null);
						aidaVoipList.remove(ccmValue);
					} else {
						String s1 = String.format(errorMap.get("E0011"), "CCM","Aida");
						populateActionItem(ai,(String)ccmData.get(i).get("ServiceSubscriptionId")[0], 
								charCode,ccmValue,null,null,null,null,null,"Report",type,desc,ccmValue,"E0011",s1,"AIDA",null,null);
					}
					actionlist.getActionItem().add(ai);
					currentPos++;
				}
			}
		}
		for (int i=0;aidaVoipList!=null && i<aidaVoipList.size();i++){
			if (aidaVoipList.get(i)==null)
				continue;
			ActionItem ai = new ActionItem();
			String s1 = String.format(errorMap.get("E0012"), "Aida","CCM");
			populateActionItem(ai,null,(currentPos<10)?mainAccNumCodes[currentPos++]:null,
					null,aidaVoipList.get(i),null,null,null,"AIDA","Report",
					type,"Voip Account "+currentPos,null,"E0012",s1,"AIDA",null,null);
			actionlist.getActionItem().add(ai);
		}
	}
	private boolean isNumberInList(ArrayList<String> srcList, String ccmValue) {
		String[] parts = ccmValue.split(";");
		if (parts.length!=5)
			return srcList.contains(ccmValue);
		for (int i = 0;srcList!=null && i < srcList.size(); i++) {
			String listNo = srcList.get(i);
			String[] srcParts = listNo.split(";");
			if (srcParts.length==5){
				String srcStart=srcParts[0]+srcParts[1]+srcParts[2]+srcParts[3];
				String srcEnd=srcParts[0]+srcParts[1]+srcParts[2]+srcParts[4];
				if(srcStart.equals(parts[0]+parts[1]+parts[2]+parts[3])&&
					srcEnd.equals(parts[0]+parts[1]+parts[2]+parts[4]))
					return true;
			}
		}
		return false;
	}
	private void populateActionItem(ActionItem ai, String serSuId, String charCode,
			String ccmValue,String aidaValue,String cramerValue, String zarValue,String infportValue,  
			String leadingsys,String action, String dataType, String description,
			String targetValue,String errorCode,String errorText,String clearingSystem,
			String relatedObjectId,String relatedObjectType) {
		ai.setValidatingSystem(clearingSystem);
		ai.setServiceSubscriptionId(serSuId);
		ai.setServiceCharCode(charCode);
		ai.setServiceCharDescription(description);
		ai.setAidaValue(aidaValue);
		ai.setCcmValue(ccmValue);
		ai.setCramerValue(cramerValue);
		ai.setZarValue(zarValue);
		ai.setInfportValue(infportValue);
		ai.setLeadingSystem(leadingsys);
		ai.setActionType(action);		
		ai.setTargetValue(targetValue);
		if (!action.equals("Ignore")){
			ai.setErrorCode(errorCode);
			ai.setErrorMessage(errorText);		
		}
		ai.setDataType(dataType);
		ai.setRelatedObjectId(relatedObjectId);
		ai.setRelatedObjectType(relatedObjectType);
	}
	private void consolidateCcm(ConsolidateSubscriptionDataDAO dao, ActionList actionlist, 
			ArrayList<HashMap<String,Object[]>> ccmData, HashMap<String,Object> cramerMap, 
			HashMap<String,Object> aidaMap, Products product,
			boolean cramerValid, boolean aidaValid, boolean infportValid) throws Exception {
		if (infportValid)
			compareCramerInfportData(actionlist,cramerMap,ccmData);
		for (int i=0;i<ccmData.size();i++){
			Set<String> keys = ccmData.get(i).keySet();
			Iterator<String> keyiter = keys.iterator();
			while (keyiter.hasNext()){
				String charCode = keyiter.next();
				if(ccmData.get(i).get(charCode)[0] instanceof String && charTargetSystem.get(charCode)!=null &&
					ccmData.get(i).get(charCode)[1]!=null && ccmData.get(i).get(charCode)[1].equals("ACTIVE")){
					ActionItem ai = new ActionItem();
					String leadingSystem = charTargetSystem.get(charCode)[0];
					if (leadingSystem.equals("AIDA")&&(!aidaValid||!isAidaProduct(product))||
						leadingSystem.equals("Cramer")&&(!cramerValid||!isCramerProduct(product)))
						continue;

					String ccmValue = (String) ccmData.get(i).get(charCode)[0];
					String targetValue = null;
					if (leadingSystem.equals("AIDA")){
						if (aidaMap==null) 
							continue;
						Object aidaGen = aidaMap.get(charCode);
						String aidaValue=getAidaValue(aidaGen, ccmValue, (String) ccmData.get(i).get("ServiceSubscriptionId")[0], 
								(String) ccmData.get(i).get("serviceCode")[0], charCode, product);
						targetValue=aidaValue;
					}
					if (leadingSystem.equals("Cramer")) {
						if (cramerMap==null || charCode.equals("V0934") && product==Products.BIT_STREAM)
							continue;
						String cramerValue = (String) cramerMap.get(charCode);
						targetValue=cramerValue;
					}
					String actionType = "Ignore";
					String errorCode = null;
					String errorText = null;
					// IP address only the first field is stored in AIDA
					if (charCode.equals("I905B") && ccmValue!=null ){
						int index=ccmValue.indexOf(";");
						if (index>0)
							targetValue+= ccmValue.substring(index);
					}
						
					if (ccmValue==null || targetValue==null || !areValuesEqual(ccmValue,targetValue,charCode,product)){
						if (targetValue==null)
							actionType = "Report";
						else
							actionType = "Change";
						errorCode = charTargetSystem.get(charCode)[4];
						errorText = String.format(errorMap.get(errorCode),ccmValue);
					}
					String aidaValue=leadingSystem.equals("AIDA")?targetValue:null;
					String cramerValue=leadingSystem.equals("Cramer")?targetValue:null;
					populateActionItem(ai,(String) ccmData.get(i).get("ServiceSubscriptionId")[0],charCode,
							ccmValue,aidaValue,cramerValue,null,null,leadingSystem,actionType,charTargetSystem.get(charCode)[2],
							charTargetSystem.get(charCode)[1],targetValue,errorCode,errorText,"CCM",null,null);
					actionlist.getActionItem().add(ai);
					logger.info("Code "+charCode+" CCM "+ccmValue+" cramer "+cramerValue+
							" aida "+aidaValue + " target system "+leadingSystem);
				}
			}
		}
	}
	
	private String getAidaValue(Object aidaGen, String ccmValue, String serSuId, String serviceCode, 
			String charCode, Products product) throws Exception{
		String aidaValue = null;
		if (aidaGen==null){
			Integer aidaParam = valueExistsInAida(charCode,ccmValue,product);
			if (aidaParam!=null && aidaParam==0)
				aidaValue=ccmValue;
		} else if (aidaGen instanceof String) {
			aidaValue=(String) aidaGen;
		}
		return aidaValue;
	}

	private boolean areValuesEqual(String ccmValue, String targetValue,
			String charCode, Products product) {
// OPM does not send Line ID for ISDN products to Cramer
		if (charCode.equals("I1020")) {
			return product==Products.ISDN_WITH_ACS || product==Products.ISDN_WITHOUT_ACS || ccmValue.equals(targetValue);
		}
		return ccmValue.equals(targetValue);
	}
	private void compareCramerInfportData(ActionList actionlist,
			HashMap<String, Object> cramerMap,
			ArrayList<HashMap<String, Object[]>> ccmData) {
		CtInstallationAddress infportAddress=new CtInstallationAddress();
		String tasi = null;
		if (cramerMap!=null)
			tasi=(String) cramerMap.get("V0152");
		else 
			tasi=getLeadingValue(ccmData,"V0152",true);
		try {
			String lineID = getInfportData(tasi, infportAddress);
			if (cramerMap!=null) {
				String cramerLineID = (String) cramerMap.get("I1020");
				if (cramerLineID != null && !cramerLineID.equals(lineID)) {
					String errorCode = "E0063";
					String errorText = String.format(errorMap.get("E0063"),
							lineID);
					ActionItem ai = new ActionItem();
					populateActionItem(ai, null, "I1020", null, null,
							cramerLineID, null, lineID, "Cramer", "Report",
							"STRING", "NGAB Line ID", cramerLineID, errorCode,
							errorText, "Infport", null, null);
					actionlist.getActionItem().add(ai);
				}
			}
			Address addr= null;
			boolean bCCMAddrConsist=true;
			for (int i=0;i<ccmData.size();i++){
				if (ccmData.get(i).containsKey("V0014")){
					Address nextAddr=(Address) ccmData.get(i).get("V0014")[0];
					if (addr!=null && nextAddr!=null && !areAddressesEqual(addr,nextAddr)){
						String bundleId=(String) ccmData.get(i).get("BundleId")[0];
						String errorCode = "E0064";
						String errorText = String.format(errorMap.get("E0064"),bundleId);
						ActionItem ai = new ActionItem();
						populateActionItem(ai ,null,"V0014",
								null,null,null,null,null,"CCM","Report","STRING",
								"Address",null,errorCode,errorText,"CCM",null,null);
						actionlist.getActionItem().add(ai);
						bCCMAddrConsist=false;
					}
					addr=nextAddr;
				}
			}
			if (addr!=null&&bCCMAddrConsist){
				compareAddresses(addr,infportAddress,actionlist);
			}
		} catch (Exception e) {
			logger.warn("The call to infoport services failed. Infport validation has been skiped.");
			ActionItem ai = new ActionItem();
			populateActionItem(ai,null,null,
					null,null,null,null,null,null,"Report","None",
					"General Error",null,"E0044","Infport failure: "+e.getMessage(),"Infport",null,null);
			actionlist.getActionItem().add(ai);
		}
	}
	private boolean areAddressesEqual(Address addr, Address nextAddr) {
		if (addr.getStreet()!=null && !addr.getStreet().equalsIgnoreCase(nextAddr.getStreet())|| 
				addr.getStreet()==null && nextAddr.getStreet()!=null )
			return false;
		if (addr.getStreetNumber()!=null && !addr.getStreetNumber().equalsIgnoreCase(nextAddr.getStreetNumber()) || 
				addr.getStreetNumber()==null && nextAddr.getStreetNumber()!=null)
			return false;
		if (addr.getStreetNumberSuffix()!=null && !addr.getStreetNumberSuffix().equalsIgnoreCase(nextAddr.getStreetNumberSuffix()) || 
				addr.getStreetNumberSuffix()==null && nextAddr.getStreetNumberSuffix()!= null)
			return false;
		if (addr.getPostalCode()!=null && !addr.getPostalCode().equalsIgnoreCase(nextAddr.getPostalCode()) ||
				addr.getPostalCode()==null && nextAddr.getPostalCode()!= null)
			return false;
		if (addr.getCity()!=null && !addr.getCity().equalsIgnoreCase(nextAddr.getCity()) ||
				addr.getCity()==null && nextAddr.getCity()!= null)
			return false;
		if (addr.getCitySuffix()!=null && !addr.getCitySuffix().equalsIgnoreCase(nextAddr.getCitySuffix()) ||
				addr.getCitySuffix()==null && nextAddr.getCitySuffix()!= null)
			return false;
		return true;

	}

	private void compareAddresses(Address addr,
			CtInstallationAddress infportAddress, ActionList actionlist) {
		if (addr.getStreet()!=null && !addr.getStreet().equalsIgnoreCase(infportAddress.getStreet())|| 
				addr.getStreet()==null && infportAddress.getStreet()!=null )
			addInfportReportItem(actionlist,"Street",addr.getStreet(),infportAddress.getStreet());
		if (addr.getStreetNumber()!=null && !addr.getStreetNumber().equalsIgnoreCase(infportAddress.getHouseNumber()) || 
				addr.getStreetNumber()==null && infportAddress.getHouseNumber()!=null)
			addInfportReportItem(actionlist,"Street Number",addr.getStreetNumber(),infportAddress.getHouseNumber());
		if (addr.getStreetNumberSuffix()!=null && !addr.getStreetNumberSuffix().equalsIgnoreCase(infportAddress.getHouseNoAddition()) || 
				addr.getStreetNumberSuffix()==null && infportAddress.getHouseNoAddition()!= null)
			addInfportReportItem(actionlist,"Street Number Suffix",addr.getStreetNumberSuffix(),infportAddress.getHouseNoAddition());
		if (addr.getPostalCode()!=null && !addr.getPostalCode().equalsIgnoreCase(infportAddress.getPostcode()) ||
				addr.getPostalCode()==null && infportAddress.getPostcode()!= null)
			addInfportReportItem(actionlist,"Postal Code",addr.getPostalCode(),infportAddress.getPostcode());
		if (addr.getCity()==null || infportAddress.getCity()== null ||
				!infportAddress.getCity().toUpperCase().contains(addr.getCity().toUpperCase()) ||
				addr.getCitySuffix()!=null && !infportAddress.getCity().toUpperCase().contains(addr.getCitySuffix().toUpperCase()))
			addInfportReportItem(actionlist,"City",(addr.getCitySuffix()==null)?addr.getCity():addr.getCity()+" "+addr.getCitySuffix()
					,infportAddress.getCity());
	}

	private void addInfportReportItem(ActionList actionlist, String attribute,
			String ccmValue, String infportValue) {
		ActionItem ai = new ActionItem();
		String errorCode = charTargetSystem.get("V0014")[4];
		String errorText = String.format(errorMap.get(errorCode),attribute,ccmValue,infportValue);
		populateActionItem(ai ,null,"V0014",
				ccmValue,null,null,null,infportValue,"CCM","Report",charTargetSystem.get("V0014")[2],
				charTargetSystem.get("V0014")[1]+"/"+attribute,ccmValue,errorCode,errorText,"CCM",null,null);
		actionlist.getActionItem().add(ai);
	}
	private Integer valueExistsInAida(String charCode, String ccmValue, Products product) throws Exception {
		String tsg = null;
		if (charCode.equals("I9058"))
			tsg = getAidaParam("AAA", ccmValue);
		if (charCode.equals("VI011")){
			String[] parts = ccmValue.split("@");
			tsg = getAidaParam("SIP", parts[0]);
		}
		if (tsg==null)
			return null;
		if (tsg.length()==0)
			return 0;

		return 1;

	}
	private void consolidateAida(ConsolidateSubscriptionDataDAO dao, ActionList actionlist, 
			ArrayList<HashMap<String,Object[]>> ccmData, HashMap<String,Object> aidaMap,
			HashMap<String, ArrayList<String[]>> servCodeMap,Products product) throws Exception {
		if (aidaMap!=null) {
			String aidaSerSuId = (String) aidaMap.get("ServiceSubscriptionId");
			if (aidaSerSuId==null){
				ActionItem ai = new ActionItem();
				String ReportMsg=String.format(errorMap.get("E0007"), (String) aidaMap.get("V0154"));
				populateActionItem(ai,getMainSerSuId(servCodeMap),null,null,null,null,null,null,"AIDA",
						"Report","ServiceSubscriptionId","Service Subscription Id",
						null,"E0007",ReportMsg,"AIDA",(String) aidaMap.get("V0154"),"TSG");
				actionlist.getActionItem().add(ai);
			}
			Set<String> keys = aidaMap.keySet();
			Iterator<String> keyiter = keys.iterator();
			while (keyiter.hasNext()) {
				String charCode = keyiter.next();
				if (charTargetSystem.get(charCode) != null
						&& charTargetSystem.get(charCode)[0].equals("AIDA")
						&& !doesValueExistForCharCode(ccmData, charCode)
						&& aidaMap.get(charCode) != null) {
					ActionItem ai = new ActionItem();
					String aidaValue = null;
					if (aidaMap.get(charCode) instanceof String)
						aidaValue = (String) aidaMap.get(charCode);
					else
						continue;
					String serSuId = getServSubsIdForChar(dao, charCode,servCodeMap);
					String action = "Change";
					if (serSuId == null)
						action = "Ignore";
					populateActionItem(ai, serSuId, charCode, null,
							aidaValue,null,null,null, "AIDA", action,
							charTargetSystem.get(charCode)[2],
							charTargetSystem.get(charCode)[1], aidaValue,
							charTargetSystem.get(charCode)[3], errorMap
							.get(charTargetSystem.get(charCode)[3]),"CCM",null,null);
					actionlist.getActionItem().add(ai);
					logger.info("Code " + charCode + " CCM " + null
							+ " cramer " + null + " aida " + aidaValue
							+ " target system "
							+ charTargetSystem.get(charCode)[0]);
				}
			}
		}
	}
	private void consolidateCramer(ConsolidateSubscriptionDataDAO dao, ActionList actionlist, ArrayList<HashMap<String,Object[]>> ccmData, 
			HashMap<String,Object> cramerData, HashMap<String, ArrayList<String[]>> servCodeMap, 
			Products product) throws Exception {
		if (cramerData!=null) {
			String cramerSerSuId = (String) cramerData.get("ServiceSubscriptionId");
			if (cramerSerSuId==null){
				ActionItem ai = new ActionItem();
				String ReportMsg=String.format(errorMap.get("E0023"), (String) cramerData.get("V0152"));
				populateActionItem(ai,getMainSerSuId(servCodeMap),null,null,null,null,null,null,"Cramer",
						"Ignore","ServiceSubscriptionId","Service Subscription Id",
						null,"E0023",ReportMsg,"Cramer",(String) cramerData.get("V0152"),"TASI");
				actionlist.getActionItem().add(ai);
			}
			Set<String> keys = cramerData.keySet();
			Iterator<String> keyiter = keys.iterator();
			while (keyiter.hasNext()){
				String charCode = keyiter.next();
				if(charTargetSystem.get(charCode)!=null&&charTargetSystem.get(charCode)[0].equals("Cramer")&&
						!doesValueExistForCharCode(ccmData,charCode)&&cramerData.get(charCode)!=null){
					ActionItem ai = new ActionItem();
					String cramerValue = (String) cramerData.get(charCode);
					String serSuId = getServSubsIdForChar(dao,charCode,servCodeMap);
					String action="Change";
					if (serSuId==null)
						action="Ignore";
					populateActionItem(ai,serSuId,charCode,
							null,null,cramerValue,null,null,"Cramer",action,charTargetSystem.get(charCode)[2],
							charTargetSystem.get(charCode)[1],cramerValue,charTargetSystem.get(charCode)[3],
							errorMap.get(charTargetSystem.get(charCode)[3]),"CCM",null,null);
					actionlist.getActionItem().add(ai);
					logger.info("Code "+charCode+" CCM "+null+" cramer "+ cramerValue+
							" aida "+ null + " target system "+charTargetSystem.get(charCode)[0]);
				}
			}
		}
	}
	private boolean doesValueExistForCharCode(
			ArrayList<HashMap<String,Object[]>> checkdata, String charCode) {
		for (int i=0;i<checkdata.size();i++){
			HashMap<String, Object[]> map = checkdata.get(i);
			if (map.containsKey(charCode))
				return true;
		}
		return false;
	}
	private String getServSubsIdForChar(ConsolidateSubscriptionDataDAO dao, String charCode, HashMap<String, ArrayList<String[]>> servCodeMap) throws Exception {
		ArrayList<HashMap<String, Object>> serviceCodes = dao.getServiceCodesForCharCode(charCode);
		for (int i = 0; i < serviceCodes.size(); i++) {
			String tmpServCode=(String) serviceCodes.get(i).get("SERVICE_CODE");
			if (servCodeMap.get(tmpServCode)!=null && servCodeMap.get(tmpServCode).size()==1)
				return servCodeMap.get(tmpServCode).get(0)[0];
		}
		return null;
	}

	private String getLeadingValue(ArrayList<HashMap<String,Object[]>> srcData,String leadingChar, boolean isActiveProduct){
		String deactLeadValue = null;
		String actLeadValue = null;
		for (int i = 0;srcData!=null && i < srcData.size(); i++) {
			Object[] leadingValue = srcData.get(i).get(leadingChar);
			if(leadingValue!=null && srcData.get(i).get(leadingChar)[1]!=null){
				if (srcData.get(i).get(leadingChar)[1].equals("ACTIVE"))
					actLeadValue=(String)srcData.get(i).get(leadingChar)[0];
				else
					deactLeadValue=(String)srcData.get(i).get(leadingChar)[0];
			}
			
		}
		return (isActiveProduct)?actLeadValue:deactLeadValue;
	}
	
	private String getMainSerSuId(HashMap<String, ArrayList<String[]>> servCodeMap) {
		Set<String> keys = servCodeMap.keySet();
		Iterator<String> keyiter = keys.iterator();
		while (keyiter.hasNext()){
			String serviceCode = keyiter.next();
			if (Arrays.asList(mainServiceCodes).contains(serviceCode)){
				if(servCodeMap.get(serviceCode).size()==1)
					return servCodeMap.get(serviceCode).get(0)[0];
				if (servCodeMap.get(serviceCode).size()>1) {
					for (int i = 0; i < servCodeMap.get(serviceCode).size(); i++) {
						String[] SerSuIds= servCodeMap.get(serviceCode).get(i);
						if (SerSuIds[1].equals("ACTIVE"))
							return SerSuIds[0];
					}
				}
			}
		}
		return null;
	}
	private int getIntegerProperty(String tagName,int defaultValue){
		int value = defaultValue;
		try {
			value = Integer.parseInt(DatabaseClientConfig.getSetting(tagName));
		} catch (Exception e1) {
			// Ignore (Use Default)
		}
		return value;
	}
	
	private CtBasicResponse callInfportService(CtBasicData input, String tasi) throws Exception {
		ApplicationContext ac = DatabaseClient.getAc();
		if (ac == null)
			throw new MCFException("Application context has not been initialized.");
		int timeToLive = getIntegerProperty("servicebusclient.TimeToLive", 3600000);
		int timeOut = getIntegerProperty("servicebusclient.TimeOut",100000);
		try {
			ActionRequest request = new ActionRequest();
			request.setIdSet(new CtIDSet());
			request.getIdSet().setMandant("BKS");
			request.setData(input);
			ServiceRequestSoap<ActionRequest> serviceRequest =
					new ServiceRequestSoap<ActionRequest>(request,
							"classpath:schema/basic.xsd",
					"de.dialogika.infport.schema.basic.v1");
			serviceRequest.setOperationName("/INF-001/ActionRequest");		
			serviceRequest.setTimeToLive(timeToLive);
			AppMonDetailsType amdt = new AppMonDetailsType();
			amdt.setCallingApp("BKS");
			amdt.setBoId("Infport_"+tasi);
			Date current = new Date();
			amdt.setBpId("Infport_"+tasi+"_"+current.getTime());
			amdt.setBpName("Infport BKS");
			amdt.setInitiator("SBUSBKS - " + java.net.InetAddress.getLocalHost().getHostName());
			serviceRequest.setAppMonDetails(amdt);
			ServiceResponse<JAXBElement<CtBasicResponse>> respServ = messageSender.sendSyncSoapRequest(serviceRequest, timeOut);
			JAXBElement<CtBasicResponse> resp = respServ.getPayload();
			return  resp.getValue();
		} catch (Exception e) {
			logger.error("Call to Infport failed. Message: "+e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}
}