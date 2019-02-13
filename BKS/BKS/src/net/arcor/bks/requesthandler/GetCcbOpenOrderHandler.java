/*
    $Header:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/GetCcbOpenOrderHandler.java-arc   1.31   Nov 07 2013 12:46:02   makuier  $

    $Log:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/GetCcbOpenOrderHandler.java-arc  $
 * 
 *    Rev 1.31   Nov 07 2013 12:46:02   makuier
 * ResellerId added to the signature.
 * 
 *    Rev 1.30   Apr 30 2012 13:54:50   makuier
 * Adapted to XSD change
 * 
 *    Rev 1.29   Apr 19 2012 09:51:04   makuier
 * Allow conversion method for access numbers.
 * 
 *    Rev 1.28   Feb 15 2012 09:28:56   wlazlow
 * SPN-BKS-000118790, maping for chracteristic V0165-V0174 added
 * 
 *    Rev 1.27   Jan 25 2012 13:09:32   makuier
 * Craete fake installation contact role only when call comes from GetCCBOpenOrder.
 * 
 *    Rev 1.26   Jan 24 2012 10:12:24   makuier
 * log both to ONL and EFFONL
 * 
 *    Rev 1.25   Jan 20 2012 15:47:28   makuier
 * Dummy installation contact role added.
 * 
 *    Rev 1.24   Jan 17 2012 11:39:18   makuier
 *  Use default value for configured value parts
 * 
 *    Rev 1.23   Jan 12 2012 14:51:12   makuier
 * - Handle reconfiguration/termination of HW in same order.
 * 
 *    Rev 1.22   Jan 11 2012 13:42:10   makuier
 * - Handle recreation of DSL service
 * 
 *    Rev 1.21   Nov 28 2011 16:54:18   wlazlow
 * SPN-CCB-000115605, logging added
 * 
 *    Rev 1.20   Nov 15 2011 09:23:38   makuier
 * jump over empty values.
 * 
 *    Rev 1.20   Nov 11 2011 10:09:58   makuier
 * jump over empty values.
 * 
 *    Rev 1.19   Nov 08 2011 10:23:58   makuier
 * relocation variant populated
 * 
 *    Rev 1.18   Oct 27 2011 16:59:04   makuier
 * Action populated on DSL bandwidth.
 * 
 *    Rev 1.17   Oct 20 2011 13:35:00   makuier
 * Do not rerurn null values.
 * 
 *    Rev 1.16   Sep 23 2011 10:07:58   makuier
 * check if the ID node exists under the accesses
 * 
 *    Rev 1.15   Aug 17 2011 11:14:06   makuier
 * send fetch product flag to get customer data.
 * 
 *    Rev 1.14   Aug 10 2011 17:13:38   makuier
 * Corrected parent node search.
 * 
 *    Rev 1.13   Jul 18 2011 17:51:44   makuier
 * Handle non referenced characteristics
 * 
 *    Rev 1.12   Jun 21 2011 16:43:12   makuier
 * Adapted to the new SOM definition.
 * 
 *    Rev 1.11   Jun 15 2011 13:43:44   makuier
 * Sales org number populated.
 * 
 *    Rev 1.10   Jun 06 2011 17:35:56   makuier
 * Handle oracle date casting.
 * 
 *    Rev 1.9   Jun 01 2011 14:39:24   schwarje
 * SPN-BKS-000112834: populate entrySystem and entryDateTime
 * 
 *    Rev 1.8   May 26 2011 12:25:48   makuier
 * do not populate major and minor versions.
 * 
 *    Rev 1.7   May 25 2011 15:08:52   makuier
 * Ibatis maps date to timestamp.
 * 
 *    Rev 1.6   May 19 2011 13:58:48   makuier
 * Misc. Fixes.
 * 
 *    Rev 1.5   May 13 2011 14:54:08   makuier
 * Misc. Fixes.
 * 
 *    Rev 1.4   May 11 2011 15:02:18   makuier
 * misc. Changes
 * 
 *    Rev 1.3   Apr 29 2011 13:08:50   makuier
 * Do not send McfExceptíon as it causes loop.
 * 
 *    Rev 1.2   Dec 15 2010 09:29:06   makuier
 * Handle addresses
 * 
 *    Rev 1.1   Dec 06 2010 16:27:14   makuier
 * Adapted to new XSD changes.
 * 
 *    Rev 1.0   Oct 15 2010 15:41:26   makuier
 * Initial revision.
*/
package net.arcor.bks.requesthandler;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBElement;

import net.arcor.bks.client.DatabaseClient;
import net.arcor.bks.client.DatabaseClientConfig;
import net.arcor.bks.common.BksDataException;
import net.arcor.bks.common.BksHelper;
import net.arcor.bks.common.BksRefDataCacheHandler;
import net.arcor.bks.common.FNPInformationHolder;
import net.arcor.bks.db.GeneralDAO;
import net.arcor.bks.db.GetCcbOpenOrderDAO;
import net.arcor.ccm.epsm_bks_getccbopenorder_001.GetCcbOpenOrderInput;
import net.arcor.ccm.epsm_bks_getccbopenorder_001.GetCcbOpenOrderOutput;
import net.arcor.ccm.epsm_bks_getccbopenorder_001.GetCcbOpenOrderService;
import net.arcor.epsm_basetypes_001.ProviderService;
import net.arcor.mcf2.exception.base.MCFException;
import net.arcor.mcf2.exception.internal.XMLException;
import net.arcor.mcf2.model.ServiceObjectEndpoint;
import net.arcor.mcf2.model.ServiceResponse;
import net.arcor.mcf2.service.Service;
import net.arcor.orderschema.Access;
import net.arcor.orderschema.Action;
import net.arcor.orderschema.AssociatedObjectReference;
import net.arcor.orderschema.Beneficiary;
import net.arcor.orderschema.CcbIdAny;
import net.arcor.orderschema.CcbIdC;
import net.arcor.orderschema.CcbIdS;
import net.arcor.orderschema.ContactRole;
import net.arcor.orderschema.ContactRoleRefIdList;
import net.arcor.orderschema.ExistConfDescriptionString100;
import net.arcor.orderschema.ExistConfStringId;
import net.arcor.orderschema.Function;
import net.arcor.orderschema.Functions;
import net.arcor.orderschema.Hardware;
import net.arcor.orderschema.HardwareConfiguration;
import net.arcor.orderschema.Internet;
import net.arcor.orderschema.LineChange;
import net.arcor.orderschema.LineCreation;
import net.arcor.orderschema.ObjectFactory;
import net.arcor.orderschema.Order;
import net.arcor.orderschema.OrderPositionType;
import net.arcor.orderschema.PayerAllCharges;
import net.arcor.orderschema.ProcessingType;
import net.arcor.orderschema.ProviderChange;
import net.arcor.orderschema.Relocation;
import net.arcor.orderschema.SalesOrganisation;
import net.arcor.orderschema.SalesOrganisationNumber;
import net.arcor.orderschema.Termination;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.domainlanguage.time.TimePoint;

/**
 * @author MAKUIER
 *
 */
public class GetCcbOpenOrderHandler   implements Service {


	protected final Log logger = LogFactory.getLog(getClass());
	final static HashMap<String , String> specialChar = new HashMap<String , String>() {{ 
		put("V0165","V0001");
		put("V0166","V0070");
		put("V0167","V0071");
		put("V0168","V0072");
		put("V0169","V0073");
		put("V0170","V0074");
		put("V0171","V0075");
		put("V0172","V0076");
		put("V0173","V0077");
		put("V0174","V0078");
		put("V0936",null);}}; 

	public ServiceResponse<?> execute(ServiceObjectEndpoint<?> serviceInput)
	throws MCFException {
		int tries =0;
		int maxTries = 10;
		String errorText = null;
		String errorCode = null;
		String correlationId = serviceInput.getCorrelationID();
        boolean cacheHit = false;
        StringBuilder unvalidatedXml = new StringBuilder();

        long startTime = System.currentTimeMillis();
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
		BksHelper theHelper = new BksHelper();

        
		@SuppressWarnings("unchecked")
		ProviderService service = ((JAXBElement<ProviderService>)(serviceInput.getPayload())).getValue();

		String returnObject = null;
		GetCcbOpenOrderInput input = null;
		try {
			// get the input object from the service object
			input = ((GetCcbOpenOrderService)service).getGetCcbOpenOrderInput();

			returnObject = doExecute(input,theHelper,unvalidatedXml);
			logger.info("Output XML:");
			logger.info(returnObject);
		} catch (BksDataException e) {
			try {
				errorText = e.getText();
				errorCode = e.getCode();
				returnObject = populateOutputObject(null,e.getCode(),e.getText(), theHelper,null);
			} catch (Exception e1) {
			}
		} catch (Exception e) {
			try {
				errorText = "Exception caught during the execution of service: " + e.getMessage();
				errorCode = "BKS-0008";
				if (e.getMessage() != null && e.getMessage().length() > 255)
					errorText = e.getMessage().substring(0, 255);
				else
					errorText = e.getMessage();
				logger.error("Exception caught during the execution of service: " + e.getMessage());

				e.printStackTrace();
				returnObject = populateOutputObject(null,errorCode,errorText, theHelper,null);
			} catch (Exception e1) {
			}
		}
		catch (Throwable e) {
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
						correlationId, "GetCcbOpenOrder", cacheHit ? "Y" : "N",
						new Timestamp(startTime), new Timestamp(endTime));
				if (errorText != null || errorCode != null) {
					try {
						populateErrorLogParams(dao, correlationId, input,
								unvalidatedXml.toString(), "GetCcbOpenOrder");
					} catch (Exception e) {
						// ignore
					}
				}
			}
			try {
				dao = null;
				dao = (GeneralDAO)DatabaseClient.getBksDaoForTargetDb("effonl","GeneralDAO");
			} catch (Exception e) {
			}
			if (dao != null) {
				dao.insertLog(errorCode, errorText,
						(errorCode == null) ? "SUCCESS" : "FAILED", null, null,
						correlationId, "GetCcbOpenOrder", cacheHit ? "Y" : "N",
						new Timestamp(startTime), new Timestamp(endTime));
				if (errorText != null || errorCode != null) {
					try {
						populateErrorLogParams(dao, correlationId, input,
								unvalidatedXml.toString(), "GetCcbOpenOrder");
					} catch (Exception e) {
						// ignore
					}
				}
			}
		}
		if (returnObject == null)
			return null;
		return serviceInput.createResponse(serviceInput.getServiceName(), serviceInput.getServiceVersion(), returnObject);
	}
	/** 
	 * This method executes the data retrieval queries and returns the result in output object.
	 * The result is also cached in JCS cache to speed up the retrieval in case anything goes
	 * wrong.
	 * @param input 
	 * @param theHelper 
	 * @param unvalidatedXml 
	 */
	public String doExecute(GetCcbOpenOrderInput input, BksHelper theHelper, StringBuilder unvalidatedXml) throws Exception {
		String returnXml = null;
		ArrayList<HashMap<String, Object>> bundleItems = new ArrayList<HashMap<String,Object>>();
		String errorText = null;
		String errorCode = null;
		String parentBarCode = null;
		GetCcbOpenOrderDAO dao = 
			(GetCcbOpenOrderDAO) DatabaseClient.getBksDataAccessObject(null,"GetCcbOpenOrderDAO");

		try {
			String barcode = input.getBarCode();
			ArrayList<HashMap<String, Object>> custdata = dao.getCustomerDataForBarcode(barcode);
			if (custdata==null || custdata.size() == 0){
				errorCode = "BKS-00008";
				errorText = "No Customer / Account found for barcode "+barcode;
				return  populateOutputObject(null,errorCode,errorText, theHelper,null);
			}
			String customerNumber = custdata.get(0).get("CUSTOMER_NUMBER").toString();
			ArrayList<String> accounts = new ArrayList<String>();
			accounts.add(custdata.get(0).get("ACCOUNT_NUMBER").toString());
			ArrayList<String> sdContractNumbers = new ArrayList<String>();
			Order order =getCustomerData(accounts,sdContractNumbers,customerNumber);
			order.setOrderID(barcode);
			ArrayList<HashMap<String, Object>> openOrders = dao.getOpenOrdersForBarcode(barcode);
			ArrayList<HashMap<String, Object>> stps = getTickets(dao,openOrders);
			String scenario = DetermainScenario(dao,openOrders,stps,parentBarCode);
			ArrayList<String> contractNumbers = new ArrayList<String>();
			populateOrderPositions(dao,scenario,order, customerNumber,parentBarCode,accounts, stps,bundleItems,theHelper,contractNumbers);
			GetSomMasterDataHandler mdh = new GetSomMasterDataHandler();
			ArrayList<Access> consAccesses = new ArrayList<Access>();
			consolidateAccesses(order.getOrderPosition().get(0).getValue(),consAccesses);
			for (int j = 0; j < consAccesses.size(); j++) {
				Access access = consAccesses.get(j);
				List<ContactRole> contactRole = order.getCustomerData().get(0).getContactRole();
				addInstallContactRoleRef(access.getID(),contactRole);
				access.setContactRoleRefIdList(new ContactRoleRefIdList());
				mdh.associateRelevantContactIds(access.getID(), contactRole,
						access.getContactRoleRefIdList().getContactRoleRef());
			}
			
			if (order.getFixedSomMajorVersion()!=null) {
				order.setSomVersion(order.getFixedSomMajorVersion().toString());
			}
			// schwarje, Hack for SPN-BKS-000112834
			order.setEntrySystem("CCM");
			order.setEntryDateTime(TimePoint.from(new Date(System.currentTimeMillis())));
			populateSalesOrgNumber(dao,order,contractNumbers);
			returnXml = populateOutputObject(order,null,null, theHelper,unvalidatedXml);
		} finally {
			dao.closeConnection();
		}
		return returnXml;
	}

	void addInstallContactRoleRef(String supportedNodeId, List<ContactRole> contactRole){
		ContactRole installCr = null;
		for (int i= 0;contactRole!=null&&i<contactRole.size();i++){
			if (contactRole.get(i).getID().equals("INSTALLATION")){
				installCr = contactRole.get(i);
				break;
			}
		}
			
		if (installCr != null) {
			AssociatedObjectReference value = new AssociatedObjectReference();
			CcbIdAny ccbId = new CcbIdAny();
			ccbId.setExisting(supportedNodeId.substring(1));
			ccbId.setType("S");
			value.setCcbId(ccbId);
			value.setNodeRefId(supportedNodeId);
			installCr.getAssociatedObjectReference().add(value);
		}
	}
	private void populateSalesOrgNumber(GetCcbOpenOrderDAO dao, Order order, ArrayList<String> contractNumbers) throws Exception {
		ArrayList<HashMap<String, Object>> sons = dao.getSalesOrgNumber(contractNumbers);
		SalesOrganisation salesOrg = new SalesOrganisation();
		boolean arcorFound = false;
		boolean vodafoneFound = false;
		for (int i = 0;sons!=null && i < sons.size(); i++) {
			BigDecimal number = (BigDecimal) sons.get(i).get("CIO_DATA");
			String type = (String) sons.get(i).get("CIO_TYPE_RD");
			SalesOrganisationNumber salesOrgNum = new SalesOrganisationNumber();
			salesOrgNum.setContent(number.toString());
			if (type.endsWith("VF") || type.equals("VODAFONE")) {
				if (vodafoneFound)
					continue;
				salesOrgNum.setType("V");
				vodafoneFound = true;
			} else {
				if (arcorFound)
					continue;
				salesOrgNum.setType("A");
				arcorFound = true;
			}
			salesOrg.getSalesOrganisationNumber().add(salesOrgNum);
		}
		order.setSalesOrganisation(salesOrg);
	}
	private void populateOrderPositions(GetCcbOpenOrderDAO dao, String scenario, Order order,
			String customerNumber, String parentBarCode,ArrayList<String> accounts,
			ArrayList<HashMap<String, Object>> stps, 
			ArrayList<HashMap<String,Object>> bundleItems, BksHelper theHelper,
			ArrayList<String> contractNumbers) throws Exception {
		OrderPositionType orderPosition = null;
		FNPInformationHolder infoHolder = new  FNPInformationHolder();
		if (scenario.equals("TAKEOVERCONTRACT") || scenario.equals("PROVIDER_CHANGE"))
			orderPosition = new ProviderChange();
		else if (scenario.equals("LineCreation"))
			orderPosition = new LineCreation();
		else if (scenario.equals("Relocation"))
			orderPosition = new Relocation();
		else if (scenario.equals("Termination"))
			orderPosition = new Termination();
		else if (scenario.equals("LineChange"))
			orderPosition = new LineChange();
		else
			return;
		
		orderPosition.setOrderPositionNumber(1);

		Beneficiary benef = new Beneficiary();
		CcbIdC custNum = new CcbIdC();
		custNum.setExisting(customerNumber);
		benef.setCcbId(custNum);
		benef.setCustomerNodeRefID("C"+customerNumber);
		PayerAllCharges account = new PayerAllCharges();
		account.setBillingAccountNodeRefID("AC"+accounts.get(0));
		orderPosition.setPayerAllCharges(account);
		orderPosition.setBeneficiary(benef);
		
		HashMap<String,String> mainIds = new HashMap<String,String>();
		ArrayList<String> hwIds = new ArrayList<String>();
		for (int i = 0;stps!=null && i < stps.size(); i++) {
			String usageMode = stps.get(i).get("USAGE_MODE_VALUE_RD").toString();
			String servSubsId = stps.get(i).get("SERVICE_SUBSCRIPTION_ID").toString();
			String serviceCode = stps.get(i).get("SERVICE_CODE").toString();
			String serviceType = stps.get(i).get("SERVICE_TYPE").toString();
			String parentServiceCode = null;
			String productCode = null;
			if (serviceType.equals("MAIN_ACCESS") || serviceCode.equals("V9001")){
				if (!mainIds.containsKey(servSubsId)){
					ArrayList<HashMap<String, Object>> bdlItems = dao.getBundleItems(servSubsId);
					String bundleId = null;
					if (bdlItems != null && bdlItems.size() > 0) 
						bundleId = bdlItems.get(0).get("BUNDLE_ID").toString();

					if (bundleItems.size() == 0 || (bundleId!=null&&!bundleId.equals(bundleItems.get(0).get("BUNDLE_ID").toString())))
						bundleItems.addAll(bdlItems);
					theHelper.setDesiredNewElement(-1);
					if (serviceCode.equals("V8000") || serviceCode.equals("VI201"))
						theHelper.setDesiredNewElement(0);
					if (usageMode.equals("1") && !serviceCode.equals("V9001"))
						productCode = populateTableData(dao,servSubsId,serviceCode,orderPosition,true,scenario,usageMode,null, theHelper,bundleItems, contractNumbers);
					else 
						productCode = populateTableData(dao,servSubsId,serviceCode,orderPosition,false,scenario,usageMode,null, theHelper,bundleItems, contractNumbers);
					mainIds.put(servSubsId,productCode);
				}
			} else {
				ArrayList<HashMap<String, Object>> mainServSub =dao.getMainAccessService(servSubsId);
				if (mainServSub == null || mainServSub.size() == 0) {
					String errorText = "No main access service found for service " + servSubsId;
					String errorCode = "BKS-0008";
					throw new BksDataException(errorCode,errorText);
				}
				String parentServSubsId = mainServSub.get(0).get("SERVICE_SUBSCRIPTION_ID").toString();
				parentServiceCode = mainServSub.get(0).get("SERVICE_CODE").toString();
				if (!mainIds.containsKey(parentServSubsId)){
					productCode = populateTableData(dao,parentServSubsId,parentServiceCode,orderPosition,false,scenario,usageMode,null, theHelper, bundleItems, contractNumbers);
					mainIds.put(parentServSubsId,productCode);
				}
				if ((serviceType.equals("ACCESS_HW") || serviceType.equals("TERMINAL_HW"))&&!hwIds.contains(servSubsId)){
					populateDepHardwareList(dao,stps.get(i),mainIds.get(parentServSubsId),parentServSubsId,serviceCode,orderPosition, parentBarCode, theHelper,stps);
					hwIds.add(servSubsId);
				} else if (serviceType.equals("TEL_BOOK_ENTRY")) {
					if (usageMode.equals("1"))
						productCode = populateTableData(dao,servSubsId,serviceCode,orderPosition,true,scenario,usageMode,parentServiceCode, theHelper, bundleItems, contractNumbers);
					else
						productCode = populateTableData(dao,servSubsId,serviceCode,orderPosition,false,scenario,usageMode,parentServiceCode, theHelper, bundleItems, contractNumbers);
					int lastInd = orderPosition.getFunctions().getDirectoryEntry().size() - 1;

					ExistConfStringId refId = new ExistConfStringId();
					if (usageMode.equals("1") && !existsTerminateTicketSameType(serviceCode,stps)) 
						refId.setConfigured("F"+parentServSubsId);
					else 
						refId.setExisting("F"+parentServSubsId);
					orderPosition.getFunctions().getDirectoryEntry().get(lastInd).setRefFunctionID(refId);
				} else
					populateFeaturesList(dao,stps.get(i),mainIds.get(parentServSubsId),parentServSubsId,parentServiceCode,orderPosition, theHelper);
			}
			if (serviceType.equals("MAIN_ACCESS") || serviceType.equals("TEL_BOOK_ENTRY"))
					setProcessingType(usageMode,serviceCode,servSubsId,orderPosition,parentBarCode,stps);
			// if ticket.servicesubsscriptionid = parent..
			//    find the function
			//    if usageMode = 1 set processing type to ADD
			//    if usageMode = 2 and there is no terminate stp for same SS set processing type to CHANGE    
			//    if usageMode = 4  and there is no subscribe stp for the same type of function
			//                  set processing type to TERM    
			//    if usageMode = 4  and there is a subscribe stp for the same type of function
			//                  set processing type to CHANGE    
			if (!usageMode.equals("4") && !serviceCode.equals("V0113") && !serviceCode.equals("V0088")) {
				populateAccessNumbers(dao,stps.get(i),parentServiceCode,orderPosition,theHelper);
				populateConfiguredValues(dao,stps.get(i),parentServiceCode,orderPosition,theHelper,true,infoHolder);
				populateAddresses(dao,stps.get(i),parentServiceCode,orderPosition,theHelper);
				populateServiceLocation(dao,stps.get(i),parentServiceCode,orderPosition,theHelper);
			} else if (serviceCode.equals("V0113") || serviceCode.equals("V0088")) {
				theHelper.setDesiredNewElement(-1);
				populateConfiguredValues(dao,stps.get(i),parentServiceCode,orderPosition,theHelper,!usageMode.equals("4"),infoHolder);
				Functions functions = orderPosition.getFunctions();			    
				Internet func;
				if (functions.getInternet().size() > 0) {
					func = functions.getInternet().get(functions.getInternet().size() - 1);
					if (func.getAdslInternetConfiguration() != null &&
					 func.getAdslInternetConfiguration().getDSLBandwidth()!=null)
						func.getAdslInternetConfiguration().getDSLBandwidth().setAction(Action.REMOVE);
				}
			}
		}
		checkBundle(dao,mainIds,orderPosition,scenario,bundleItems, theHelper);
		ObjectFactory of = new ObjectFactory();
		if (orderPosition instanceof Termination){
			((Termination) orderPosition).setTerminationReasonCode("Something");
			order.getOrderPosition().add(of.createTermination((Termination) orderPosition));
		} else if (orderPosition instanceof ProviderChange){
			((ProviderChange) orderPosition).setParentBarcode(parentBarCode);
			if (scenario.equals("TAKEOVERCONTRACT"))
				order.getOrderPosition().add(of.createContractPartnerChange((ProviderChange) orderPosition));
			else if (scenario.equals("PROVIDER_CHANGE"))
				order.getOrderPosition().add(of.createProviderChange((ProviderChange) orderPosition));
		} else if (orderPosition instanceof LineChange){
			order.getOrderPosition().add(of.createLineChange((LineChange) orderPosition));
		} else if (orderPosition instanceof LineCreation){
			order.getOrderPosition().add(of.createLineCreation((LineCreation) orderPosition));
		} else if (orderPosition instanceof Relocation){
			((Relocation) orderPosition).setSeatRelocationIndicator(false);
			((Relocation) orderPosition).setKeepFixedNumbersIndicator(false);
			if (infoHolder.getRelocationVariant() != null)
				((Relocation) orderPosition).setRelocationVariant(infoHolder.getRelocationVariant());
			else
				((Relocation) orderPosition).setRelocationVariant("kein Umzug");
			order.getOrderPosition().add(of.createRelocation((Relocation) orderPosition));
		}
	}
	
	private void checkBundle(GetCcbOpenOrderDAO dao, HashMap<String, String> mainIds,OrderPositionType orderPosition, String scenario, ArrayList<HashMap<String,Object>> bundleItems, BksHelper theHelper ) throws Exception {
		if (mainIds == null || mainIds.size() ==0)
			return;
		for (int i = 0; bundleItems != null && i < bundleItems.size(); i++) {
			String servSubsId = bundleItems.get(i).get("SERVICE_SUBSCRIPTION_ID").toString();
			String serviceCode = bundleItems.get(i).get("SERVICE_CODE").toString();
			String usageMode = bundleItems.get(i).get("USAGE_MODE_VALUE_RD").toString();
			 theHelper.setDesiredNewElement(-1);
			if (!mainIds.containsKey(servSubsId))
				populateTableData(dao, servSubsId, serviceCode, orderPosition,false,scenario,usageMode,null, theHelper, bundleItems,null);
		}
	}
	private void setProcessingType(String usageMode,
			String serviceCode,	String servSubsId, 
			OrderPositionType orderPosition, String parentBarCode, ArrayList<HashMap<String, Object>> stps) throws Exception {
      Functions functions = orderPosition.getFunctions();
      String funcType = null;
	if (serviceCode.equals("V0100")) 
		funcType = "directoryEntry";
	else
		funcType = BksRefDataCacheHandler.getFunction().get(serviceCode);
	
      if (funcType == null)
    	  throw new Exception();
      Function func = null;
      if (funcType.equals("Sprache"))
    	  func = functions.getVoice().get(functions.getVoice().size() - 1);
      else if (funcType.equals("Internet"))
    	  func = functions.getInternet().get(functions.getInternet().size() - 1);
      else if (funcType.equals("TVCenter"))
    	  func = functions.getTvCenter().get(functions.getTvCenter().size() - 1);
      else if (funcType.equals("IPTV"))
    	  func = functions.getIptv().get(functions.getIptv().size() - 1);
      else if (funcType.equals("SafetyPackage"))
    	  func = functions.getSafetyPackage().get(functions.getSafetyPackage().size() - 1);
      else if (funcType.equals("directoryEntry"))
    	  func = functions.getDirectoryEntry().get(functions.getDirectoryEntry().size() - 1);
      else
    	  return;

      if (usageMode.equals("1") && !existsTerminateTicketSameType(serviceCode,stps))
	  	func.setProcessingType(ProcessingType.ADD);
	  else if (usageMode.equals("2") && !existsTerminateTicket(servSubsId,stps)) 
		  func.setProcessingType(ProcessingType.CHANGE);   
	  else if (usageMode.equals("2") && existsTerminateTicket(servSubsId,stps) && 
			  !existsCreateTicketSameType(serviceCode,stps) && parentBarCode == null) 
		  func.setProcessingType(ProcessingType.TERM);   
	  else if (usageMode.equals("4") && !existsCreateTicketSameType(serviceCode,stps) && parentBarCode == null)
		  func.setProcessingType(ProcessingType.TERM);   
	  else if (usageMode.equals("4") && existsCreateTicketSameType(serviceCode,stps))
		  func.setProcessingType(ProcessingType.CHANGE);   
	  else 
		  func.setProcessingType(ProcessingType.CHANGE);
	}

	private boolean existsTerminateTicketSameType(String serviceCode, ArrayList<HashMap<String, Object>> stps) {
		String funcType = BksRefDataCacheHandler.getFunction().get(serviceCode);  
		if (funcType == null && !serviceCode.equals("V0100"))
			return false;
		for (int i = 0;stps!=null && i < stps.size(); i++) {
			String usageMode = stps.get(i).get("USAGE_MODE_VALUE_RD").toString();
			String tmpServCode = stps.get(i).get("SERVICE_CODE").toString();
			String tmpFuncType = BksRefDataCacheHandler.getFunction().get(tmpServCode);
			if (funcType != null && funcType.equals(tmpFuncType) && usageMode.equals("4"))
				return true;
			if(funcType == null && tmpServCode.equals(serviceCode) && usageMode.equals("4"))
				return true;
		}
		return false;
	}
	private boolean existsCreateTicketSameType(String serviceCode, ArrayList<HashMap<String,Object>> stps){
		String funcType = BksRefDataCacheHandler.getFunction().get(serviceCode);  
		if (funcType == null && !serviceCode.equals("V0100"))
			return false;
		for (int i = 0;stps!=null && i < stps.size(); i++) {
			String usageMode = stps.get(i).get("USAGE_MODE_VALUE_RD").toString();
			String tmpServCode = stps.get(i).get("SERVICE_CODE").toString();
			String tmpFuncType = BksRefDataCacheHandler.getFunction().get(tmpServCode);
			if (funcType != null && funcType.equals(tmpFuncType) && usageMode.equals("1"))
				return true;
			if(funcType == null && tmpServCode.equals(serviceCode) && usageMode.equals("1"))
				return true;
		}
		return false;
	}

	private boolean existsTerminateTicket(String servSubsId, ArrayList<HashMap<String,Object>> stps) {
		for (int i = 0;stps!=null && i < stps.size(); i++) {
			String usageMode = stps.get(i).get("USAGE_MODE_VALUE_RD").toString();
			String servId = stps.get(i).get("SERVICE_SUBSCRIPTION_ID").toString();
			if (usageMode.equals("4") && servSubsId.equals(servId))
				return true;
		}
		return false;
	}

	private void populateDepHardwareList(GetCcbOpenOrderDAO dao,
			HashMap<String, Object> ticket, String productCode,
			String parentServSubsId, String serviceCode,
			OrderPositionType orderPosition, String parentBarCode, BksHelper theHelper, 
			ArrayList<HashMap<String, Object>> stps) {
		String usageMode = ticket.get("USAGE_MODE_VALUE_RD").toString();
		String servSubsId = ticket.get("SERVICE_SUBSCRIPTION_ID").toString();
		Hardware hardware = new Hardware();
		CcbIdS ccbId = new CcbIdS();
		ccbId.setConfigured(servSubsId);
		hardware.setCcbId(ccbId);
		hardware.setID("F"+servSubsId);
		ExistConfStringId refId = new ExistConfStringId();
		refId.setConfigured("F"+parentServSubsId);
		hardware.setRefFunctionID(refId);
		if (hardware.getHardwareConfiguration()==null)
			hardware.setHardwareConfiguration(new HardwareConfiguration());
		
		ExistConfDescriptionString100 sc = new ExistConfDescriptionString100();
		sc.setConfigured(serviceCode);
		hardware.getHardwareConfiguration().setHardwareServiceCode(sc);
		if (usageMode.equals("5") || usageMode.equals("6")) 
			hardware.setProcessingType(ProcessingType.ADD);
		else if (existsTerminateTicket(servSubsId,stps) && parentBarCode == null) 
			hardware.setProcessingType(ProcessingType.TERM);
		else if ((usageMode.equals("2") && !existsTerminateTicket(servSubsId,stps)) || parentBarCode != null) 
			hardware.setProcessingType(ProcessingType.CHANGE);
		else 
			hardware.setProcessingType(ProcessingType.IGNORE);
		orderPosition.getFunctions().getHardware().add(hardware);
		theHelper.setDesiredNewElement(-1);
	}

	private void populateFeaturesList(GetCcbOpenOrderDAO dao,HashMap<String, Object> ticket,
			 String productCode, String parentServSubsId, String parentServiceCode,
			OrderPositionType orderPosition, BksHelper theHelper) throws Exception {
		String usageMode = ticket.get("USAGE_MODE_VALUE_RD").toString();
		String serviceCode = ticket.get("SERVICE_CODE").toString();
		if (!usageMode.equals("1"))
			return;
	    String funcType = BksRefDataCacheHandler.getFunction().get(parentServiceCode);  
	    if (funcType == null)
	    	throw new Exception();
		 Functions functions = orderPosition.getFunctions();
		 Function func = getFunctionNode(funcType,parentServSubsId,functions);
		 theHelper.setDesiredNewElement(0);
		 HashMap<String, ArrayList<String[]>> tableColMap = 
			 BksRefDataCacheHandler.getSomTableColumnMap().get(productCode+";"+serviceCode+";"+parentServiceCode);
		 if (tableColMap != null) {
			 Set<String> keys = tableColMap.keySet();
			 Iterator<String> keyiter = keys.iterator();
			 while (keyiter.hasNext()){
				 String columnName = keyiter.next();
				 if (columnName.equals("-")){
					 for (int j = 0; j < tableColMap.get(columnName).size(); j++) {
						 String path = (String) tableColMap.get(columnName).get(j)[0];
						 path=path.substring(path.indexOf(".", 10)+1);
						 path=path.replaceAll(".Existing", ".Configured");
						 theHelper.setAttributeForPath(func, "set", path, tableColMap.get(columnName).get(j)[1].toString());
					 }
					 continue;
				 }
				 if (ticket.get(columnName)==null)
					 continue;
				 for (int j = 0; j < tableColMap.get(columnName).size(); j++) {
					 String columnValue = ticket.get(columnName).toString();
					 if (tableColMap.get(columnName).get(j)[2] != null)
						 columnValue = tableColMap.get(columnName).get(j)[2] + columnValue;
					 String conversionMethod = tableColMap.get(columnName).get(j)[1];
					 String path = (String) tableColMap.get(columnName).get(j)[0];
					 path=path.substring(path.indexOf(".", 10)+1);
					 path = path.replaceAll(".Existing", ".Configured");
					 if (conversionMethod != null) {
						 Object converted = theHelper.invokeMethod(
								 theHelper, conversionMethod, columnValue,
								 String.class);
						 theHelper.setAttributeForPath(func, "set", path, converted);
					 } else {
						 theHelper.setAttributeForPath(func, "set", path, columnValue);
					 }
				 }
			 }
		 }
	}

	private Function getFunctionNode(String funcType, String parentServSubsId,Functions functions) {
		List<?> list = null;
		if (funcType.equals("Sprache"))
			list = functions.getVoice();
		if (funcType.equals("Internet"))
			list = functions.getInternet();
		if (funcType.equals("TVCenter"))
			list = functions.getTvCenter();
		if (funcType.equals("SafetyPackage"))
			list = functions.getSafetyPackage();
		if (funcType.equals("IPTV"))
			list = functions.getIptv();
		for (int j = 0; j < list.size(); j++) {
			Function func = (Function) list.get(j);
			if (func.getCcbId() != null) {
				if (func.getCcbId().getConfigured() != null &&
					func.getCcbId().getConfigured().equals(parentServSubsId))
					return func;
				if (func.getCcbId().getExisting() != null &&
					func.getCcbId().getExisting().equals(parentServSubsId))
					return func;
			}
		}
		return null;
	}

	private void populateServiceLocation(GetCcbOpenOrderDAO dao,
			HashMap<String, Object> ticket,String parentServiceCode,
			OrderPositionType orderPosition, BksHelper theHelper) {
		// TODO Auto-generated method stub
	}

	private void populateAddresses(GetCcbOpenOrderDAO dao,HashMap<String, Object> ticket, 
			 String parentServiceCode, OrderPositionType orderPosition, BksHelper theHelper) throws Exception {
		String ticketId = ticket.get("SERVICE_TICKET_POSITION_ID").toString();
		String serviceCode = ticket.get("SERVICE_CODE").toString();
		ArrayList<HashMap<String, Object>> addresses = dao.getAddress(ticketId);
		if (addresses == null || addresses.size() == 0)
			return;
		HashMap<String,HashMap<String,Object>> charMap = BksRefDataCacheHandler.getSomServiceColumnMap().get(serviceCode);
		for (int i =0;charMap!=null&&i<addresses.size();i++) {
			String serviceCharCode = (String) addresses.get(i).get("SERVICE_CHARACTERISTIC_CODE");
			if (charMap.get(serviceCharCode) != null){
				Set<String> keys = charMap.get(serviceCharCode).keySet();
				Iterator<String> keyiter = keys.iterator();
				while (keyiter.hasNext()){
					String columnName = keyiter.next();
					String columnValue = (String) addresses.get(i).get(columnName);
					Object valueObj = charMap.get(serviceCharCode).get(columnName);
					if (!(valueObj instanceof ArrayList)){
						populateSingleColumn((String[])valueObj,columnValue,orderPosition, theHelper);
					} else {
						ArrayList<String[]> list = (ArrayList<String[]>)valueObj;
						for (int j = 0; list != null && j < list.size(); j++) {
							String[] item = list.get(j);
							populateSingleColumn(item,columnValue,orderPosition, theHelper);
						}
					}
				}
			}
		}
	}

	private void populateSingleColumn(String[] valueObj, String columnValue,
			OrderPositionType orderPosition, BksHelper theHelper) throws Exception {
		String path = valueObj[0];
		String conversionMethod = valueObj[1];
		String fieldNumber = valueObj[2];
		if (path != null && columnValue != null){
			path = path.replaceAll(".Existing", ".Configured");
			if (fieldNumber != null)
				columnValue = getPartValue(columnValue,Integer.parseInt(fieldNumber));
			if (conversionMethod == null) {
				String value = (String)	theHelper.setAttributeForPath(orderPosition, "get",path, null);
				if (value == null)
					theHelper.setAttributeForPath(orderPosition, "set", path, columnValue);
				else{
					Pattern p = Pattern.compile("[a-zA-Z]*");
					Matcher m = p.matcher(value);
					boolean b = m.matches();
					if (b)
						value = columnValue + value;
					else
						value = value + columnValue;
					theHelper.setAttributeForPath(orderPosition, "set", path, value);
				}
			} else {
				if (columnValue.trim().length() != 0){
					Object converted = theHelper.invokeMethod(theHelper, conversionMethod, columnValue, String.class);
					theHelper.setAttributeForPath(orderPosition, "set", path, converted);
				}
			}
		}
	}

	private String getPartValue(String columnValue, int fieldNumber) {
		int i=0;
        for (;i<columnValue.length()&&Character.isDigit(columnValue.charAt(i));i++);
        if (fieldNumber == 1 && i<=columnValue.length())
        	return columnValue.substring(0,i);
        if (fieldNumber != 1 && i<columnValue.length())
        	return columnValue.substring(i,columnValue.length());
		return "";
	}

	private void populateConfiguredValues(GetCcbOpenOrderDAO dao,
			HashMap<String, Object> ticket,String parentServiceCode,
			OrderPositionType orderPosition, BksHelper theHelper,boolean bConfigured, FNPInformationHolder infoHolder) throws Exception {		
		String ticketId = ticket.get("SERVICE_TICKET_POSITION_ID").toString();
		String servSubsId = ticket.get("SERVICE_SUBSCRIPTION_ID").toString();
		String serviceCode = ticket.get("SERVICE_CODE").toString();
		ArrayList<HashMap<String,Object>> configValues;
		if (bConfigured) {
			configValues = dao.getConfiguredValues(ticketId);
		}else{
			configValues = dao.getTerminatedConfigValues(servSubsId);
		}
		HashMap<String,ArrayList<String[]>> charPath = null;
		if (parentServiceCode == null)
			charPath = BksRefDataCacheHandler.getSomServiceCharMap().get(serviceCode);
		else
			charPath = BksRefDataCacheHandler.getSomServiceCharMap().get(serviceCode+";"+parentServiceCode);
		if (configValues == null || charPath == null )
			return;
		for (int i = 0;i < configValues.size(); i++) {
			String serviceCharCode = (String) configValues.get(i).get("SERVICE_CHARACTERISTIC_CODE");
			String columnValue = (String) configValues.get(i).get("CONFIGURED_VALUE_STRING");
			if (serviceCharCode.equals("V2024") && columnValue!=null)
				infoHolder.setRelocationVariant(columnValue);
			if (charPath.get(serviceCharCode) != null) {
				for (int j = 0; j < charPath.get(serviceCharCode).size(); j++) {
					String rdsId = charPath.get(serviceCharCode).get(j)[1];
					String conversionMethod = charPath.get(serviceCharCode).get(j)[2];
					String fieldNumber = charPath.get(serviceCharCode).get(j)[3];
					String defaultValue = charPath.get(serviceCharCode).get(j)[5];
					if (columnValue == null)
						columnValue=defaultValue;
					String charValue = columnValue;
					if (fieldNumber != null){
						String[] fields = charValue.split(";");
						if (fields.length >= Integer.parseInt(fieldNumber))
							charValue = fields[Integer.parseInt(fieldNumber)-1];
						else
							continue;
					}
					if (charValue==null || charValue.length() == 0)
						continue;
					String path = charPath.get(serviceCharCode).get(j)[0];
					if (bConfigured)
						path = path.replaceAll(".Existing", ".Configured");
					if (rdsId == null && conversionMethod == null)
						theHelper.setAttributeForPath(orderPosition, "set",
								path, charValue);
					else if (conversionMethod != null) {
						Object converted = theHelper.invokeMethod(theHelper,
								conversionMethod, charValue, String.class);
						if (converted==null)
							converted=defaultValue;
						if (converted!=null)
							theHelper.setAttributeForPath(orderPosition, "set",
								path, converted);
					}
				}
			}
			if (specialChar.containsKey(serviceCharCode)) {
				String depCode = specialChar.get(serviceCharCode);
				String[] pathInfo = findCorrespondingPath(charPath.get("-"),depCode);
				if (pathInfo != null && pathInfo[0] != null) {					
					pathInfo[0] = pathInfo[0].replaceAll(".Existing", ".Configured");
					if (pathInfo[1] != null) {
						Object converted = theHelper.invokeMethod(theHelper,
								pathInfo[1], columnValue, String.class);
						theHelper.setAttributeForPath(orderPosition, "set",pathInfo[0], converted);
					} else {
						if(columnValue.equals("\u00C4ndern")){							  
							 theHelper.setAttributeForPath(orderPosition, "set",pathInfo[0], "L\u00F6schen");
						}						  
						else if(columnValue.equals("nein")){
							 theHelper.setAttributeForPath(orderPosition, "set",pathInfo[0], "Nein");
						}
						else if(columnValue.equals("ja")){
							 theHelper.setAttributeForPath(orderPosition, "set",pathInfo[0], "Standard");
						}
						else{							 
							theHelper.setAttributeForPath(orderPosition, "set",pathInfo[0], columnValue);
						}
							
					}
				}
			}

		}
	}

	private String[] findCorrespondingPath(ArrayList<String[]> chars,String depCharCode) {
		for (int j = 0;chars != null && j < chars.size(); j++) {
			String path = chars.get(j)[0];
			String conversionMethod = chars.get(j)[2];
			String refDepCharCode = chars.get(j)[4];
			if (depCharCode!=null && refDepCharCode.equals(depCharCode))
				return new String[] {path,conversionMethod};
			if (depCharCode==null && path.contains("NumberOfNewAccessNumbers"))
				return new String[] {path,conversionMethod};
		}
		return null;
	}
	private void populateAccessNumbers(GetCcbOpenOrderDAO dao,
			HashMap<String, Object> ticket,String parentServiceCode,
			OrderPositionType orderPosition, BksHelper theHelper) throws Exception {
		String ticketId = ticket.get("SERVICE_TICKET_POSITION_ID").toString();
		String serviceCode = ticket.get("SERVICE_CODE").toString();
		ArrayList<HashMap<String,Object>> accessNumbers = dao.getAccessNumbers(ticketId);
		HashMap<String,HashMap<Integer,Object[]>>  charMap = null;
		if (parentServiceCode == null)
			charMap = BksRefDataCacheHandler.getSomServiceFieldMap().get(serviceCode);
		else
			charMap = BksRefDataCacheHandler.getSomServiceFieldMap().get(serviceCode+";"+parentServiceCode);
		if (accessNumbers == null || charMap == null )
			return;

		for (int i =0;charMap!=null&&i<accessNumbers.size();i++) {
			String serviceCharCode = (String) accessNumbers.get(i).get("SERVICE_CHARACTERISTIC_CODE");
			String number = (String) accessNumbers.get(i).get("ACCESS_NUMBER");
			theHelper.setDesiredNewElement(1);
			if (number != null && charMap.get(serviceCharCode) != null){
				HashMap<Integer,Object[]> fieldPathMap = charMap.get(serviceCharCode);
				String[] numberFields = number.split(";");
				for(Integer j=0;j<numberFields.length;j++){
					Object[] paths = fieldPathMap.get(j+1);
					for (int k = 0; paths!=null&&k < paths.length; k++) {
						String path = ((String[])paths[k/3])[k++%3];
						String conversionMethod = ((String[])paths[k/3])[++k%3];
						if (path.startsWith("Accesses"))
							theHelper.setDesiredNewElement(-1);
						if (!path.contains("PhoneNumbersList"))
							path = path.replaceAll(".Existing", ".Configured");

						if (conversionMethod != null) {
							Object converted = theHelper.invokeMethod(theHelper,
									conversionMethod, numberFields[j], String.class);
							if (converted!=null)
								theHelper.setAttributeForPath(orderPosition, "set",path, converted);
						} else {
							theHelper.setAttributeForPath(orderPosition, "set",path, numberFields[j]);
						}
					}
				}
			}
		}
	}

	private String populateTableData(GetCcbOpenOrderDAO dao, String servSubsId,
			String serviceCode, OrderPositionType orderPosition, boolean bConfigured, 
			String scenario, String usageMode, String parentServiceCode, 
			BksHelper theHelper, ArrayList<HashMap<String,Object>> bundleItems,
			ArrayList<String> contractNumbers) throws Exception {
		ArrayList<HashMap<String,Object>> contract = dao.getContractData(servSubsId);
		
		String productCode = null;

		if (contract != null && contract.size() > 0) {
			productCode = contract.get(0).get("PRODUCT_CODE").toString();
			if (contractNumbers != null)
				contractNumbers.add(contract.get(0).get("CONTRACT_NUMBER").toString());
			HashMap<String,ArrayList<String[]>> tableMap = null;
			if (parentServiceCode==null) 
				tableMap = BksRefDataCacheHandler.getSomTableColumnMap().get(productCode + ";" + serviceCode);
			else
				tableMap = BksRefDataCacheHandler.getSomTableColumnMap().get(productCode + ";" + serviceCode+ ";" + parentServiceCode);

			if (tableMap == null)
				tableMap = BksRefDataCacheHandler.getSomTableColumnMap().get("-;" + serviceCode);
			if (tableMap == null)
				return productCode;
			Set<String> keys = tableMap.keySet();
			Iterator<String> keyiter = keys.iterator();
			while (keyiter.hasNext()){
				String columnName = keyiter.next();
				if (columnName.equals("-")){
					for (int i = 0; i < tableMap.get(columnName).size(); i++) {
						String path = (String) tableMap.get(columnName).get(i)[0];
						if (bConfigured)
							path = path.replaceAll(".Existing", ".Configured");
						theHelper.setAttributeForPath(orderPosition, "set", path, tableMap.get(columnName).get(i)[1].toString());
					}
					continue;
				}
				for (int i = 0; i < tableMap.get(columnName).size(); i++) {
					if (columnName.equals("CONTRACT_END_DATE")){
						String path = (String) tableMap.get(columnName).get(i)[0];
						Object startDate = contract.get(0).get("MIN_PERIOD_START_DATE");
						String minUnit = (String)contract.get(0).get("MIN_PERIOD_DUR_UNIT_RD");
						Integer minValue = null;
						if (contract.get(0).get("MIN_PERIOD_DUR_VALUE") != null) 
							minValue = ((BigDecimal) contract.get(0).get("MIN_PERIOD_DUR_VALUE")).intValue(); 
						String durationUnit = (String)contract.get(0).get("AUTO_EXTENT_DUR_UNIT_RD");
						Integer extValue = null;
						if (contract.get(0).get("AUTO_EXTENT_DUR_VALUE") != null)
							extValue = ((BigDecimal)contract.get(0).get("AUTO_EXTENT_DUR_VALUE")).intValue();
						boolean bAutoExtend = false;
						if (contract.get(0).get("AUTO_EXTENSION_IND") != null)
							bAutoExtend = ((String)contract.get(0).get("AUTO_EXTENSION_IND")).equals("Y");
						Object converted = 
							theHelper.getContractEndDate(startDate, minUnit, minValue, durationUnit, extValue, bAutoExtend);
						if (bConfigured)
							path = path.replaceAll(".Existing", ".Configured");
						theHelper.setAttributeForPath(orderPosition, "set", path, converted);
					} else {
						String columnValue = null;
						if (tableMap.get(columnName).get(i)[3] != null) {
							columnValue = getMainBundleAccess(columnName,
									tableMap.get(columnName).get(i)[3],bundleItems);
						} else if (contract.get(0).get(columnName)!=null)
							columnValue = contract.get(0).get(columnName).toString();
						if (columnValue == null)
							break;
						if (tableMap.get(columnName).get(i)[2] != null) {
							columnValue = tableMap.get(columnName).get(i)[2] + columnValue;
						}
						String conversionMethod = tableMap.get(columnName).get(i)[1];
						String path = tableMap.get(columnName).get(i)[0];
						if (bConfigured) {
							if (path.startsWith("Accesses") && path.endsWith(".ID") &&
									isAccessIdAlreadyCreated(orderPosition,serviceCode))
								continue;
							path = path.replaceAll(".Existing", ".Configured");
						}
						if (path.contains("RefAccessID") && usageMode.equals("1") &&
							(scenario.equals("LineChange")||scenario.equals("Relocation"))){
							if (!doesAccessExist(orderPosition,columnValue))
								continue;
							path = path.replaceAll("RefAccessID", "TargetAccessID");
						}
						if (conversionMethod != null) {
							Object converted = theHelper.invokeMethod(theHelper,
									conversionMethod, columnValue, String.class);
							theHelper.setAttributeForPath(orderPosition, "set", path, converted);
						} else {
							theHelper.setAttributeForPath(orderPosition, "set", path, columnValue);
						}
					}
				}
			}
		}
		return productCode;
	}

	private boolean isAccessIdAlreadyCreated(OrderPositionType orderPosition,
			String serviceCode) {
	    String accType = BksRefDataCacheHandler.getAccess().get(serviceCode);  
	    if (accType == null || orderPosition.getAccesses() == null)
	    	return false;

		if (accType.equals("isdn"))
			return (orderPosition.getAccesses().getIsdn()!=null &&
					orderPosition.getAccesses().getIsdn().size() >0 &&
					orderPosition.getAccesses().getIsdn().get(0).getID() != null);
		if (accType.equals("dslr"))
			return (orderPosition.getAccesses().getDslr()!=null &&
					orderPosition.getAccesses().getDslr().size() >0 &&
					orderPosition.getAccesses().getDslr().get(0).getID() != null);
		if (accType.equals("ngn"))
			return (orderPosition.getAccesses().getNgn()!=null &&
					orderPosition.getAccesses().getNgn().size() >0 &&
					orderPosition.getAccesses().getNgn().get(0).getID() != null);
		if (accType.equals("ipBitstream"))
			return (orderPosition.getAccesses().getIpBitstream()!=null &&
					orderPosition.getAccesses().getIpBitstream().size() >0 &&
					orderPosition.getAccesses().getIpBitstream().get(0).getID() != null);
		if (accType.equals("sdsl"))
			return (orderPosition.getAccesses().getBusinessDSL()!=null &&
					orderPosition.getAccesses().getBusinessDSL().size() >0 &&
					orderPosition.getAccesses().getBusinessDSL().get(0).getID() != null);
		if (accType.equals("adsl"))
			return (orderPosition.getAccesses().getBusinessDSL()!=null &&
					orderPosition.getAccesses().getBusinessDSL().size() >0 &&
					orderPosition.getAccesses().getBusinessDSL().get(0).getID() != null);
		if (accType.equals("ipCentrex"))
			return (orderPosition.getAccesses().getIpCentrex()!=null &&
					orderPosition.getAccesses().getIpCentrex().size() >0 &&
					orderPosition.getAccesses().getIpCentrex().get(0).getID() != null);
		return false;
	}
	private boolean doesAccessExist(OrderPositionType orderPosition, String columnValue) {
		ArrayList<Access> accesses = new ArrayList<Access>();
		consolidateAccesses(orderPosition,accesses);
		for (int j = 0; j < accesses.size(); j++) {
			if (accesses.get(j).getID().equals(columnValue))
				return true;
		}
		return false;
	}
	private void consolidateAccesses(OrderPositionType orderPosition,
			ArrayList<Access> consAccesses) {
		consAccesses.addAll(orderPosition.getAccesses().getBusinessDSL());
		consAccesses.addAll(orderPosition.getAccesses().getCompanyNet());
		consAccesses.addAll(orderPosition.getAccesses().getDslr());
		consAccesses.addAll(orderPosition.getAccesses().getIpBitstream());
		consAccesses.addAll(orderPosition.getAccesses().getIpCentrex());
		consAccesses.addAll(orderPosition.getAccesses().getIsdn());
		consAccesses.addAll(orderPosition.getAccesses().getLte());
		consAccesses.addAll(orderPosition.getAccesses().getMobile());
		consAccesses.addAll(orderPosition.getAccesses().getNgn());
		consAccesses.addAll(orderPosition.getAccesses().getPreselect());
		consAccesses.addAll(orderPosition.getAccesses().getSip());
	}
	private Order getCustomerData(ArrayList<String> accounts, ArrayList<String> sdContractNumbers, String customerNumber) throws Exception{
		GetSomMasterDataHandler mdh = new GetSomMasterDataHandler();
		Order order = mdh.getOrder(customerNumber,null,accounts,sdContractNumbers,true,true);
		return order;
	}

	private String DetermainScenario(GetCcbOpenOrderDAO dao,
			ArrayList<HashMap<String, Object>> openOrders, ArrayList<HashMap<String, Object>> stps, String parentBarCode) throws Exception {
		String scenario = customerChangeScenario(dao,openOrders,parentBarCode);
		if (scenario != null)
			return scenario;
		else {
			if (isLineCreation(dao,stps))
				return "LineCreation";
			else if (isRelocation(dao,stps))
				return "Relocation";
			else if (isTermination(dao,stps))
				return "Termination";
			else
				return "LineChange";
		}
	}

	private ArrayList<HashMap<String, Object>> getTickets(
			GetCcbOpenOrderDAO dao,	ArrayList<HashMap<String, Object>> openOrders) throws Exception {
		ArrayList<String> orderIds = new ArrayList<String>();
		for (int i = 0;openOrders!=null && i < openOrders.size(); i++) 
		   orderIds.add(openOrders.get(i).get("CUSTOMER_ORDER_ID").toString());
		ArrayList<HashMap<String, Object>> stps = dao.getServiceTickets(orderIds);
		return stps;
	}

	private String customerChangeScenario(GetCcbOpenOrderDAO dao,
			ArrayList<HashMap<String, Object>> openOrders, String parentBarCode) throws Exception {
		for (int i = 0;openOrders!=null && i < openOrders.size(); i++) {
			String orderId = openOrders.get(i).get("CUSTOMER_ORDER_ID").toString();
			ArrayList<HashMap<String, Object>> providerLog = dao.getProviderLog(orderId);
			if (providerLog != null && providerLog.size() > 0){
				parentBarCode = (String)providerLog.get(0).get("ACT_CUSTOMER_ORDER_ID");
				return (String)providerLog.get(0).get("REASON_RD");
			}
		}
		return null;
	}

	private boolean isTermination(GetCcbOpenOrderDAO dao,
			ArrayList<HashMap<String, Object>> stps) {
		ArrayList<String> servSubIds = new ArrayList<String>();
		for (int i = 0;stps!=null && i < stps.size(); i++) {
			String usageMode = stps.get(i).get("USAGE_MODE_VALUE_RD").toString();
			String servSubId = stps.get(i).get("SERVICE_SUBSCRIPTION_ID").toString();
			String serviceType = stps.get(i).get("SERVICE_TYPE").toString();
			if (usageMode.equals("4") && serviceType.equals("MAIN_ACCESS"))
				servSubIds.add(servSubId);
			if ((usageMode.equals("1") && !serviceType.equals("ONETIME_CHARGE"))
				|| usageMode.equals("5") || usageMode.equals("6"))
				return false;
		}
		for (int i = 0;stps!=null && i < stps.size(); i++) {
			String usageMode = stps.get(i).get("USAGE_MODE_VALUE_RD").toString();
			String servSubId = stps.get(i).get("SERVICE_SUBSCRIPTION_ID").toString();
			if (usageMode.equals("2") && !servSubIds.contains(servSubId))
				return false;
		}
		return true;
	}

	private boolean isRelocation(GetCcbOpenOrderDAO dao,
			ArrayList<HashMap<String, Object>> stps) {
		for (int i = 0;stps!=null && i < stps.size(); i++) {
			String reason = stps.get(i).get("REASON_RD").toString();
			if (reason.equals("RELOCATION"))
				return true;
		}
		return false;
	}

	private boolean isLineCreation(GetCcbOpenOrderDAO dao,
			ArrayList<HashMap<String, Object>> stps) {
		ArrayList<String> servSubIds = new ArrayList<String>();
		for (int i = 0;stps!=null && i < stps.size(); i++) {
			String usageMode = stps.get(i).get("USAGE_MODE_VALUE_RD").toString();
			String servSubId = stps.get(i).get("SERVICE_SUBSCRIPTION_ID").toString();
			if (usageMode.equals("1") || usageMode.equals("5")|| usageMode.equals("6"))
				servSubIds.add(servSubId);
			if (usageMode.equals("4") || usageMode.equals("7"))
				return false;
		}
		for (int i = 0;stps!=null && i < stps.size(); i++) {
			String usageMode = stps.get(i).get("USAGE_MODE_VALUE_RD").toString();
			String servSubId = stps.get(i).get("SERVICE_SUBSCRIPTION_ID").toString();
			if (usageMode.equals("2") && !servSubIds.contains(servSubId))
				return false;
		}
		return true;
	}

	private String populateOutputObject(Order order, String errorCode, String errorText
			, BksHelper theHelper, StringBuilder unvalidatedXml) throws Exception {
		String returnXml = null;
		GetCcbOpenOrderOutput output = new GetCcbOpenOrderOutput();
		if (order==null){
			output.setResult(false);
			output.setErrorText(errorText);
			output.setErrorCode(errorCode);
		} else {
			output.setResult(true);
			((GetCcbOpenOrderOutput)output).setOrder(order);
		}
		try{
			returnXml = theHelper.serialize(output,"net.arcor.ccm.epsm_bks_getccbopenorder_001","schema/EPSM-BKS-GetCcbOpenOrder-001.xsd" ,"GetCcbOpenOrder");
		} catch (XMLException e) {
			if (unvalidatedXml != null)
				unvalidatedXml.append(theHelper.serialize(output, "net.arcor.ccm.epsm_bks_getccbopenorder_001", null, "GetCcbOpenOrder"));
			throw e;
		}
		return returnXml;
	}

	private String getMainBundleAccess(String columnName, String matchingTypes, ArrayList<HashMap<String,Object>> bundleItems) {
		for (int i =0;bundleItems!=null&&i<bundleItems.size();i++) {
			String bundleItemType = bundleItems.get(i).get("BUNDLE_ITEM_TYPE_RD").toString();
			String[] types = matchingTypes.split(";");
			for (int j = 0; j < types.length; j++) {
				if (bundleItemType.equals(types[j]))
					return bundleItems.get(i).get(columnName).toString();
			}
		}
		return null;
	}

	private void populateErrorLogParams(GeneralDAO dao,String correlationId,GetCcbOpenOrderInput indata, 
			String unvalidatedXml,String serviceName) throws Exception {
		dao.createErrorLogParam(correlationId,"BarCode", indata.getBarCode(),null,0);
		if (unvalidatedXml != null){
			dao.createErrorLogParam(correlationId, "XML", null, unvalidatedXml,1);
		}
	}
}