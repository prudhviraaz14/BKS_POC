/*
    $Header:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/GetFNPCustomerNGCSData6Impl.java-arc   1.29   May 23 2018 09:55:32   makuier  $

    $Log:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/GetFNPCustomerNGCSData6Impl.java-arc  $
 * 
 *    Rev 1.29   May 23 2018 09:55:32   makuier
 * pointer check
 * 
 *    Rev 1.28   May 23 2018 08:41:48   makuier
 * -Ignore the open orders which do not have any supported main access service
 * 
 *    Rev 1.27   Jul 27 2017 12:48:22   makuier
 * Use service billing name fro volume cap
 * 
 *    Rev 1.26   Jun 08 2017 07:50:46   makuier
 * Return volume caps
 * 
 *    Rev 1.25   Mar 22 2017 15:32:02   makuier
 * Do not populate the HW child service if the HW is not supported.
 * 
 *    Rev 1.24   Jan 19 2017 15:51:10   makuier
 * Skip Intro for SID services
 * 
 *    Rev 1.23   Oct 28 2016 18:42:32   makuier
 * - trigger generic ref data for HW
 * 
 *    Rev 1.22   Oct 13 2016 13:38:38   makuier
 * Raise appropiate error when getCustomerDataByTasi times out.
 * 
 *    Rev 1.21   Sep 21 2016 07:24:48   makuier
 * Consider product code in the address key.
 * 
 *    Rev 1.20   Jul 29 2016 07:29:20   gaurav.verma
 * Site-Id Enhancement for RMS-156695_PCR-226216 Add_Site_ID for Anschlusskennung_to_Produkt
 * 
 *    Rev 1.19   Jun 24 2015 13:33:40   makuier
 * Ignore corrupted bundles without raising any error
 * 
 *    Rev 1.18   Jun 10 2015 12:08:48   makuier
 * GetCustomerPermission added
 * 
 *    Rev 1.17   Jan 05 2015 15:09:42   makuier
 * Removed business DSL from supported products.
 * 
 *    Rev 1.16   Dec 01 2014 13:05:28   makuier
 * Support all products. 
 * 
 *    Rev 1.15   Jul 31 2014 12:32:06   makuier
 * Filter out the customers which cause parsing error.
 * 
 *    Rev 1.14   Apr 02 2014 09:28:34   makuier
 * Check the validation row.
 * 
 *    Rev 1.13   Mar 17 2014 10:02:20   makuier
 * Separate email validation.
 * 
 *    Rev 1.12   Mar 06 2014 09:22:44   makuier
 * Do not populate EmailValidationResult if any mandatory sub node is missing
 * 
 *    Rev 1.11   Jan 22 2014 12:02:40   makuier
 * Populate mandate node only  if both mandatory sub-nodes are populated.
 * 
 *    Rev 1.10   Jun 12 2013 11:18:50   makuier
 * Changed the serializer
 * 
 *    Rev 1.9   Oct 12 2012 13:58:38   makuier
 * Added V0012 to the list of services.
 * 
 *    Rev 1.8   Apr 19 2012 10:23:32   makuier
 * Allow conversion method for access numbers.
 * 
 *    Rev 1.7   Apr 13 2012 11:11:04   makuier
 * I1351 added to the list of TV add-ons
 * 
 *    Rev 1.6   Apr 12 2012 10:18:00   makuier
 * Com Open order supported
 * 
 *    Rev 1.5   Mar 29 2012 15:13:12   makuier
 * - tasi search.
 * 
 *    Rev 1.4   Mar 26 2012 12:24:08   makuier
 * Guiding rule for unsupported bundles
 * 
 *    Rev 1.3   Mar 21 2012 13:26:16   makuier
 * Handle termination of DE in ngcs
 * 
 *    Rev 1.2   Mar 07 2012 14:41:54   makuier
 * Do not print account information.
 * 
 *    Rev 1.1   Feb 23 2012 09:27:44   makuier
 * Corrected the Contact person id
 * 
 *    Rev 1.0   Feb 22 2012 15:03:48   makuier
 * Initial revision.
 * 
*/
package net.arcor.bks.requesthandler;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBElement;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.arcor.bks.client.DatabaseClient;
import net.arcor.bks.client.DatabaseClientConfig;
import net.arcor.bks.common.BksDataException;
import net.arcor.bks.common.BksHelper;
import net.arcor.bks.common.BksRefDataCacheHandler;
import net.arcor.bks.common.FNPInformationHolder;
import net.arcor.bks.db.GetFNPCustomerNGCSDataDAO;
import net.arcor.bks.util.FudickarPhoneticConverter;
import net.arcor.mcf2.exception.base.MCFException;
import net.arcor.mcf2.exception.internal.XMLException;
import net.arcor.mcf2.model.ServiceObjectEndpoint;
import net.arcor.mcf2.model.ServiceResponse;
import net.arcor.mcf2.sender.MessageSender;
import net.arcor.mcf2.xml.ExperimentalXmlProcessor;
import net.arcor.mcf2.xml.XmlProcessor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.UncategorizedSQLException;
import org.w3c.dom.Node;

import com.domainlanguage.time.CalendarDate;
import com.vodafone.mcf2.ws.exception.FunctionalException;
import com.vodafone.mcf2.ws.exception.FunctionalRuntimeException;
import com.vodafone.mcf2.ws.exception.TechnicalException;
import com.vodafone.mcf2.ws.model.impl.ServiceRequestSoap;
import com.vodafone.mcf2.ws.model.impl.ServiceResponseSoap;
import com.vodafone.mcf2.ws.service.SoapEndpoint;
import com.vodafone.mcf2.ws.service.SoapOperation;

import de.vodafone.esb.schema.common.commontypes_esb_001.ComplexPhoneNumber;
import de.vodafone.esb.schema.common.sidcom_fnporder_001.BackendOrder;
import de.vodafone.esb.schema.common.sidcom_fnporder_001.OrderItem;
import de.vodafone.esb.schema.common.sidcom_getcustomerinfo_001.CustomerInformation;
import de.vodafone.esb.schema.common.sidcom_getcustomerinfo_001.CustomerInformation.OpenOrder;
import de.vodafone.esb.schema.common.sidcom_getcustomerinfo_001.CustomerInformation.ProductOfferingSubscription;
import de.vodafone.esb.schema.common.sidcom_masterdata_001.AccessOwner;
import de.vodafone.esb.schema.common.sidcom_masterdata_001.Address;
import de.vodafone.esb.schema.common.sidcom_masterdata_001.BillingAccount;
import de.vodafone.esb.schema.common.sidcom_masterdata_001.ContactPerson;
import de.vodafone.esb.schema.common.sidcom_masterdata_001.Customer;
import de.vodafone.esb.schema.common.sidcom_masterdata_001.DocumentRecipient;
import de.vodafone.esb.schema.common.sidcom_masterdata_001.FixedNetPhoneNumber;
import de.vodafone.esb.schema.common.sidcom_masterdata_001.IdCard;
import de.vodafone.esb.schema.common.sidcom_masterdata_001.Individual;
import de.vodafone.esb.schema.common.sidcom_masterdata_001.InvoiceDelivery;
import de.vodafone.esb.schema.common.sidcom_masterdata_001.Mandate;
import de.vodafone.esb.schema.common.sidcom_masterdata_001.MethodOfPayment;
import de.vodafone.esb.schema.common.sidcom_masterdata_001.MobilePhoneNumber;
import de.vodafone.esb.schema.common.sidcom_masterdata_001.Organization;
import de.vodafone.esb.schema.common.sidcom_productdata_001.CharacteristicValue;
import de.vodafone.esb.schema.common.sidcom_productdata_001.DetachedHouse;
import de.vodafone.esb.schema.common.sidcom_productdata_001.GermanPropertyAddress;
import de.vodafone.esb.schema.common.sidcom_productdata_001.Product;
import de.vodafone.esb.schema.common.sidcom_productdata_001.ProductBundle;
import de.vodafone.esb.schema.common.sidcom_productdata_001.ProductOfferingOccurrence;
import de.vodafone.esb.schema.common.sidcom_productdata_001.ProductOfferingOccurrence.ContractStartDate;
import de.vodafone.esb.schema.common.sidcom_productdata_001.ProductOfferingOccurrence.MinimumContractDuration;
import de.vodafone.esb.schema.common.sidcom_productdata_001.ProductPrice;
import de.vodafone.esb.schema.common.sidcom_productdata_001.TerminationData;
import de.vodafone.esb.service.sbus.bks.bks_006.GetFNPCustomerNGCSDataRequest;
import de.vodafone.esb.service.sbus.bks.bks_006.GetFNPCustomerNGCSDataResponse;
import de.vodafone.esb.service.sbus.com.com_prov_001.GetOrderDetailsRequest;
import de.vodafone.esb.service.sbus.com.com_prov_001.GetOrderDetailsResponse;
import de.vodafone.esb.service.sbus.com.com_prov_001.OrderOutputFormat;
import de.vodafone.linkdb.epsm_bkstypes_001.CompanyDataQueryInput;
import de.vodafone.linkdb.epsm_bkstypes_001.PersonDataQueryInput;
import edu.emory.mathcs.backport.java.util.Arrays;

@SoapEndpoint(soapAction = "/BKS-006/GetFNPCustomerNGCSData", context = "de.vodafone.esb.service.sbus.bks.bks_006", schema = "classpath:schema/BKS-006.xsd")
public class GetFNPCustomerNGCSData6Impl implements SoapOperation<GetFNPCustomerNGCSDataRequest, GetFNPCustomerNGCSDataResponse> {

    /** The logger. */
    private final static Log log = LogFactory.getLog(GetFNPCustomerNGCSData6Impl.class);

    @Autowired
	private MessageSender messageSender;

	final static String[] voiceServices = {"V0010","V0003","V0004","V0002","V0001","V0011","V0012","VI002","VI003","VI006","VI009","VI201","VI018","VI019","V0100"};
	final static String[] hardwareServices = {"I1223","I1291","I1293","I1294","I1295","I1350"
		,"I1359","V0048","V0107","V0108","V0109","V0110","V0114","V011A","V011C","V0328"
		,"V0330","V0850","VI050","VI051","VI052","VI053","VI054","VI055","VI057"};
	final static String[] supportedProductCodes = {"I1201","I1203","I1204","I1208","V0001","V0002","V8000"};
	final static String[] supportedServiceCodes = {"I1040","I1043","I1210","I1213", "I121z",
            "I1290","I1305","V0001","V0002","V0003","V0004","V0010","V0011","V0012","V8000"};
	private String requestedBundleId;
	final static String[] validStatus = {"NEW","IN_PROGRESS","FAILED","SUCCESS"};
	/**
	 * The operation "GetFNPCustomerNGCSData6" of the service "GetFNPCustomerNGCSData6".
	 */
	public ServiceResponseSoap<GetFNPCustomerNGCSDataResponse> invoke(ServiceObjectEndpoint<GetFNPCustomerNGCSDataRequest> serviceObject) 
			throws TechnicalException,FunctionalException,FunctionalRuntimeException {
		int tries =0;
		int maxTries = 10;
		BksHelper theHelper = new BksHelper();
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
        	log.error("The application is not initialized after max. number of tries ( "+ tries +").");
        	log.error("Please increase the value of databaseclient.MaxRetries and restart the application.");
        	System.exit(0);
        }
		GetFNPCustomerNGCSDataDAO dao = (GetFNPCustomerNGCSDataDAO) DatabaseClient.getBksDataAccessObject(null, "GetFNPCustomerNGCSDataDAO");
		GetFNPCustomerNGCSDataRequest request = serviceObject.getPayload();
		GetFNPCustomerNGCSDataResponse output = new GetFNPCustomerNGCSDataResponse();
		try {
			output = getSIDCustomerData(dao,request, theHelper,true);
			ExperimentalXmlProcessor xmlProc = ExperimentalXmlProcessor.getInstance();
			String b = xmlProc.serialize(output, null, "de.vodafone.esb.service.sbus.bks.bks_006");
			log.debug(b);
			
		} catch (BksDataException e) {
			log.error(e.getText());
			throw new FunctionalException(e.getCode(),e.getText());
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new TechnicalException("BKSCOM-ERROR-000008","Precondition check error",e);
		} finally {
			try {
				dao.closeConnection();
			} catch (Exception e) {
			}
		}

		
		return new ServiceResponseSoap<GetFNPCustomerNGCSDataResponse>(output);
	}

	private GetFNPCustomerNGCSDataResponse getSIDCustomerData(
			GetFNPCustomerNGCSDataDAO dao, GetFNPCustomerNGCSDataRequest request,BksHelper theHelper, boolean populateAccount) throws Exception {
		GetFNPCustomerNGCSDataResponse resp = new GetFNPCustomerNGCSDataResponse();
		resp.setSidcomVersion("1.0");
	    String requestType = request.getRequestType();
		FNPInformationHolder infoHolder = new FNPInformationHolder();
		ArrayList<HashMap<String, Object>> custHieryrchyNumbers=null;
		ArrayList<HashMap<String, Object>> custList = null;
		if (requestType.equals("GIS")||requestType.equals("GPS")){
			custHieryrchyNumbers=dao.getChildCustomers(request.getCustomerNumber());
			custList = new ArrayList<HashMap<String,Object>>();
			for (int i = 0; i < custHieryrchyNumbers.size(); i++) {
				String tmpCustNo = (String) custHieryrchyNumbers.get(i).get("CUSTOMER_NUMBER");
				GetFNPCustomerNGCSDataRequest tmpreq = new GetFNPCustomerNGCSDataRequest();
				tmpreq.setCustomerNumber(tmpCustNo);
				ArrayList<HashMap<String, Object>> cust = getCustomerList(dao, tmpreq,infoHolder,requestType, theHelper);
				if (cust != null && cust.size() > 0)
					custList.addAll(cust);
			}
		} else {
			custList = getCustomerList(dao, request,infoHolder,requestType, theHelper);
		}
		int maxReturnedCustomer = 20;
		try {
			maxReturnedCustomer = Integer.parseInt(DatabaseClientConfig.getSetting("databaseclient.MaxReturnedCustomer"));
		} catch (Exception e)  {
			// ignore
		}
		int maxLoop = (maxReturnedCustomer<custList.size())?maxReturnedCustomer:custList.size();
		for (int j = 0; j < maxLoop; j++) {
			infoHolder.setVoice(false);
			String customerNumber = (String) custList.get(j).get("CUSTOMER_NUMBER");
			String encryptKey = (String) custList.get(j).get("ENCRYPTION_KEY");
			theHelper.setCustomerEncKey(encryptKey);
			resp.getSid().add(new CustomerInformation());
			if (!requestType.equals("GOL") && !requestType.equals("GAP") && !requestType.equals("RCO")) {
				populateCustomerData(custList.get(j), resp.getSid().get(j),infoHolder, theHelper);
				populateCustomerPermissions(dao,customerNumber,resp.getSid().get(j), theHelper);
				String addressId = (String) custList.get(j).get("PRIMARY_ADDRESS_ID");
				String refDataKey = "CUSTOMER;ADDRESS";
				populateAddress(dao, addressId, refDataKey, resp.getSid().get(j), theHelper);
				populateRootContact(dao,customerNumber,resp.getSid().get(j),theHelper);
				String accInfoId = (String) custList.get(j).get("PRIMARY_ACCESS_INFORMATION_ID");
				if (accInfoId != null)
					populateAccessInfo(dao, accInfoId,"CUSTOMER", resp.getSid().get(j), theHelper);
				try {
					if (populateAccount)
						populateBillingAccount(dao, custList.get(j), resp.getSid().get(j), theHelper);
				} catch (BksDataException e) {
					if (custList.size()==1){
						throw new BksDataException(e.getCode(),e.getText());
					}
					custList.remove(j);
					resp.getSid().remove(j);
					j--;
					maxLoop = (maxReturnedCustomer<custList.size())?maxReturnedCustomer:custList.size();
					continue;
				}	
			}
			if (!requestType.equals("GOL") && !requestType.equals("CID") && !requestType.equals("ICH")) {
				ArrayList<HashMap<String, Object>> bundles = null;
				if (request.getBarcode() != null && 
					request.getAccessNumber() == null &&
					request.getCustomerNumber() == null)
					bundles = dao.getBundlesForBarcode(request.getBarcode());
				else if (request.getQueryLevel()== null || 
						request.getQueryLevel().equals("customer")|| 
						request.getAccessNumber() == null) {
					bundles = dao.getBundlesForCustomer(customerNumber);
					if (requestType.equals("RCO")) { 
						filterAoMasteredBundles(bundles,requestedBundleId);
						if (bundles.size() == 0)
							throw new BksDataException("Not Ao Mastered");
					}
				} else
					bundles = dao.getBundlesForAccess(request.getAccessNumber());
				for (int i = 0;bundles!=null&& i < bundles.size(); i++) {
					String bundleId = (String) bundles.get(i).get("BUNDLE_ID");
					if (!isBundleSupported(dao,bundleId)) {
						if (!handleNotSupportedBundles(dao, bundles.get(i), resp.getSid().get(j), infoHolder, theHelper,request)){
							log.debug("The bundle "+bundleId+" is corrupted. It will be filtered out");
							bundles.remove(i);
							i--;
							continue;
						}
					} else {
						infoHolder.setChangeEndDate(true);
						theHelper.setDesiredNewElement(0);
						ArrayList<HashMap<String, Object>> bundleItems = dao.getBundleItems(bundleId);
						if (bundleItems == null || bundleItems.size() == 0) {
							log.debug("The bundle "+bundleId+" is corrupted. It will be filtered out");
							bundles.remove(i);
							i--;
							continue;
						}
						populateSalesPackageInfo(dao, bundleId, resp.getSid().get(j));
						String aoFlagString = (String) bundles.get(i).get("AOSTATUS");
						boolean aoFlag = false;
						if (aoFlagString != null)
							aoFlag = (aoFlagString.equals("Y")) ? true : false;
						resp.getSid().get(j).getProductOfferingSubscription().get(i).setAoMastered(aoFlag);
						resp.getSid().get(j).getProductOfferingSubscription().get(i).setSerialNumber(bundleId);
						boolean bPopulated = populateProductOffering(dao, request, bundleItems, resp.getSid().get(j), infoHolder, theHelper);
//						ProductOfferingSubscription prodNode = resp.getSid().get(j).getProductOfferingSubscription().get(i);
//						log.info("bundle id " + bundleId + "Offeing code "+prodNode.getProductOfferingCode());
						try {
							ExperimentalXmlProcessor xmlProc = ExperimentalXmlProcessor.getInstance();
							xmlProc.serialize(resp, "schema/BKS-006.xsd", "de.vodafone.esb.service.sbus.bks.bks_006");
						} catch (XMLException e) {
							log.warn("The bundle "+bundleId+" is causing parsing error and will be filtered out.",e);
							bPopulated=false;
						}

						if (!bPopulated) {
							resp.getSid().get(j).getProductOfferingSubscription().remove(i);
							bundles.remove(i);
							i--;
							continue;
						}
					}
				}
				populateUnbundledProducts(dao,customerNumber,resp.getSid().get(j),infoHolder,theHelper,request);
			}
			List<String> productCodes = request.getProductCode();
			if (!requestType.equals("CID") && !requestType.equals("GAP") && !requestType.equals("RCO") && !requestType.equals("ICH"))
				handleOpenOrders(dao,customerNumber,resp.getSid().get(j),
						infoHolder,requestType,theHelper,productCodes);
			resp.getSid().get(j).setCustomerNumber(customerNumber);
			resp.getSid().get(j).getCustomerInformationData().setUserPassword("############");
			String parentCustNo = (String)custList.get(j).get("ASSOCIATED_CUSTOMER_NUMBER");
			resp.getSid().get(j).getCustomerInformationData().setCustomerNumber(customerNumber);
			resp.getSid().get(j).getCustomerInformationData().setParentCustomerNumber(parentCustNo);

		}
		return resp;
	}

	private void populateCustomerPermissions(GetFNPCustomerNGCSDataDAO dao,
			String customerNumber, CustomerInformation customerInformation,
			BksHelper theHelper) throws Exception {
		ArrayList<HashMap<String, Object>> perms = dao.getCustomerPermissionInfo(customerNumber);
		if (perms==null|perms.size()==0)
			return;
		HashMap<String,Boolean> indicatorData=new HashMap<String, Boolean>();
		indicatorData.put("CUSTOMER_DATA_INDICATOR",false);
		indicatorData.put("PERSONAL_DATA_INDICATOR",false);
		indicatorData.put("MARKETING_PHONE_INDICATOR",false);
		indicatorData.put("MARKETING_FAX_INDICATOR",false);
		for (int i=0;perms!=null&&i<perms.size();i++){
			String  permId = (String) perms.get(i).get("PERMISSION_ID");
			String  srvId = (String) perms.get(i).get("SERVICE_ID");
			String  value = (String) perms.get(i).get("PERMISSION_VALUE");
			Boolean boolVal = theHelper.convertToBoolean(value);
			String indicatorName = BksHelper.getPermnamemap().get(permId+";"+srvId);
			if (indicatorData.get(indicatorName)!=null)
				indicatorData.put(indicatorName, boolVal||indicatorData.get(indicatorName));
		}
		HashMap<String,Object> ccbData=new HashMap<String, Object>();
		for (String key : indicatorData.keySet())
			ccbData.put(key,(indicatorData.get(key))?"Y":"N");
		
		ccbData.put("MARKETING_AUTHORIZATION_DATE",perms.get(0).get("EFFECTIVE_DATE"));
		HashMap<String,Object[]> colPathMap = 
				BksRefDataCacheHandler.getCcbSidMap().get("CUSTOMER;CUSTOMER");

		theHelper.setDesiredNewElement(-1);
		populateSidNode(colPathMap,ccbData,customerInformation,theHelper);
	}


	private boolean isBundleSupported(GetFNPCustomerNGCSDataDAO dao, String bundleId) throws Exception {
		ArrayList<HashMap<String, Object>> bundleItems = dao.getBundleItems(bundleId);
		for (int i=0;bundleItems!=null&&i<bundleItems.size();i++){
			String serviceCode = (String)bundleItems.get(i).get("SERVICE_CODE");
			if (Arrays.asList(supportedServiceCodes).contains(serviceCode)){
				String serSuId = (String)bundleItems.get(i).get("SUPPORTED_OBJECT_ID");
				ArrayList<HashMap<String, Object>> contract = dao.getContractData(serSuId);
				if (contract == null || contract.size() == 0)
					contract = dao.getSDContractData(serSuId);
				if (contract != null && contract.size() > 0) {
					String productCode = (String)contract.get(0).get("PRODUCT_CODE");
					if (Arrays.asList(supportedProductCodes).contains(productCode))					
					    return true;
				}
			}
		}
		return false;
	}

	CustomerInformation getSidForCustomerNumber(String custNo, String requestType) throws Exception{
		GetFNPCustomerNGCSDataDAO dao = (GetFNPCustomerNGCSDataDAO) DatabaseClient.getBksDataAccessObject(null, "GetFNPCustomerNGCSDataDAO");
		GetFNPCustomerNGCSDataRequest request = new GetFNPCustomerNGCSDataRequest();
		request.setCustomerNumber(custNo);
		request.setRequestType(requestType);
		BksHelper theHelper = new BksHelper();
		GetFNPCustomerNGCSDataResponse ngcsOutput = getSIDCustomerData(dao, request, theHelper,false);
		try {
			ExperimentalXmlProcessor xmlProc = ExperimentalXmlProcessor.getInstance();
			xmlProc.serialize(ngcsOutput, "schema/BKS-006.xsd", "de.vodafone.esb.service.sbus.bks.bks_006");
		} catch (XMLException e) {
			log.warn("The customer "+custNo+" is causing parsing error and will be filtered out.",e);
			ngcsOutput = null;
		}
		if (ngcsOutput!=null&&ngcsOutput.getSid().size()>0)
			return ngcsOutput.getSid().get(0);
		return null;
	}
	
	private boolean handleNotSupportedBundles(GetFNPCustomerNGCSDataDAO dao,
			HashMap<String, Object> bundle, CustomerInformation customerInformation,
			FNPInformationHolder infoHolder, BksHelper theHelper, GetFNPCustomerNGCSDataRequest request) throws Exception
   {
		theHelper.setDesiredNewElement(0);
		String bundleId = (String) bundle.get("BUNDLE_ID");
		ArrayList<HashMap<String, Object>> bundleItems = dao.getBundleItems(bundleId);
		if (bundleItems == null || bundleItems.size() == 0) 
			return false;
		if (!isInastallationAddressOk(dao, bundleItems, request))
			return false;
		ProductOfferingSubscription pos= new ProductOfferingSubscription();
		customerInformation.getProductOfferingSubscription().add(pos);
		pos.setProductOfferingCode("container");
		pos.setProductOfferingName("container");
		String aoFlagString = (String) bundle.get("AOSTATUS");
		boolean aoFlag = false;
		if (aoFlagString != null)
			aoFlag = (aoFlagString.equals("Y")) ? true : false;
		pos.setAoMastered(aoFlag);
		pos.setSerialNumber(bundleId);
		ArrayList<String> subIds = new ArrayList<String>();
		boolean bValid =false;
		for (int i = 0; i < bundleItems.size(); i++) {
			String servSubsId = (String) bundleItems.get(i).get("SUPPORTED_OBJECT_ID");
			if (subIds.contains(servSubsId))
				continue;
			subIds.add(servSubsId);
			ArrayList<HashMap<String, Object>> contract = dao.getContractData(servSubsId);
			if (contract == null || contract.size() == 0)
				contract = dao.getSDContractData(servSubsId);
			if (contract != null && contract.size() > 0) {
				bValid=	populateContainerOfferingSubs(dao,pos,contract.get(0),bundleItems.get(i),theHelper,customerInformation,request,servSubsId,bValid);
			}
		}
		if (!bValid){
			int last = customerInformation.getProductOfferingSubscription().size()-1;
			customerInformation.getProductOfferingSubscription().remove(last);
			return false;
		}
		return true;
	}

	private boolean populateContainerOfferingSubs(GetFNPCustomerNGCSDataDAO dao, ProductOfferingSubscription pos,
			HashMap<String, Object> contract, HashMap<String, Object> bundleItem,
			BksHelper theHelper, CustomerInformation customerInformation, GetFNPCustomerNGCSDataRequest request, String servSubsId, boolean bValid) throws Exception {
		ProductOfferingOccurrence poo = new ProductOfferingOccurrence();
		pos.getProductOfferingOccurrence().add(poo );
		poo.setProductOfferingCode((String) bundleItem.get("BUNDLE_ITEM_TYPE_RD"));
		poo.setProductOfferingName(contract.get("BILLING_NAME").toString());
		ProductPrice pp = new ProductPrice();
		poo.getProductPrice().add(pp);
		pp.setName((String) contract.get("PRICING_STRUCTURE_CODE"));
		pp.setType((String) contract.get("PRODUCT_CODE"));
		pp.setPricingStructureName((String) contract.get("PRICING_STRUCTURE_NAME"));
		pp.setProductName((String) contract.get("PRODUCT_NAME"));
		setContractEndDate(contract,pos,theHelper);
		CalendarDate signDate = 
			theHelper.convertToDate(contract.get("PRIMARY_CUST_SIGN_DATE").toString());
		pos.setSignDate(signDate);
		Product prod = new Product();
		poo.setProductBundle(new ProductBundle());
		poo.getProductBundle().setSpecification("containerProductBundle");
		poo.getProductBundle().getProductBundleOrProduct().add(prod);
		prod.setSerialNumber(servSubsId);
		String serviceCode = (String)contract.get("SERVICE_CODE");
		String serviceBillingName = (String) contract.get("SERVICE_BILLING_NAME");
		prod.setSpecification("containerProductAccess");
		populateCustomerFacing(prod,serviceCode,"Service Name",serviceBillingName,theHelper);
		populateContainerGuidingRule(dao,prod,servSubsId,theHelper);
		if (!request.getRequestType().equals("GIS")) 
			populateNonRefAccessNumbers(dao, servSubsId, prod,theHelper);	
		populateServiceLevel(dao, servSubsId, poo);
		boolean bVal =  bValid || populateNonRefAddressChars(dao,servSubsId,pos,request);
		populateInstallationSite(dao,pos,servSubsId, theHelper);
		populateDepHardwareList(dao,"-",servSubsId,null,customerInformation,theHelper);
		return bVal;
	}

	private void populateContainerGuidingRule(GetFNPCustomerNGCSDataDAO dao, Product prod, String servSubsId, BksHelper theHelper) throws Exception {
		ArrayList<HashMap<String,Object>> guidingRules = dao.getGuidingRules(servSubsId);
		for (int j = 0; j < guidingRules.size(); j++) {
			String accountNumber = (String)guidingRules.get(j).get("ACCOUNT_NUMBER").toString();
			String chargeType = (String)guidingRules.get(j).get("CHARGE_TYPE_RD").toString();
			theHelper.setAttributeForPath(
					prod,"set","BillingAccountPays.ChargeType",
					theHelper.mapChargeType(chargeType));
			theHelper.setAttributeForPath(
					prod,"set","BillingAccountPays.BillingAccountId",
					accountNumber);
		}
	}

	private void populateCustomerFacing(Product prod, String code, String name,
			String value, BksHelper theHelper) throws Exception {
		theHelper
		.setAttributeForPath(
				prod,
				"set",
				"CustomerFacingService.Specification",
				code);
		theHelper
		.setAttributeForPath(
				prod,
				"set",
				"CustomerFacingService.CharacteristicValue.Name",
				name);
		theHelper
		.setAttributeForPath(
				prod,
				"set",
				"CustomerFacingService.CharacteristicValue.StringValue",
				value);	
		}

	private void populateInstallationSite(GetFNPCustomerNGCSDataDAO dao,
			ProductOfferingSubscription pos,String servSubsId, BksHelper theHelper) throws Exception {
		if (pos.getInstallationSite() == null)
			pos.setInstallationSite(new GermanPropertyAddress());
		ArrayList<HashMap<String, Object>> configuredValues = dao.getConfiguredValues(servSubsId);
		for (int i = 0; i < configuredValues.size(); i++) {
			String charCode = (String) configuredValues.get(i).get("SERVICE_CHARACTERISTIC_CODE");
			String value = (String) configuredValues.get(i).get("CONFIGURED_VALUE_STRING");
			if(charCode.equals("V0123")){
				String appHouse = theHelper.checkAppartmentHouse(value);
				if(appHouse != null){
					String[] fields = appHouse.split(",");
					String basePath = "InstallationSite.AppartmentHouse.";
					String[] tailpath ={"EntranceLocation","Floor","AppartmentLocation","Details"};
					for (int j = 1; j < fields.length; j++) {
						theHelper.setAttributeForPath(pos, "set", basePath+ tailpath[j-1], fields[j]);
					}
				} else if(value != null){
					theHelper.setAttributeForPath(pos, "set", "InstallationSite.DetachedHouse.Details", value);
				}
			} else if(charCode.equals("V0127") || charCode.equals("V0129")){
				AccessOwner accessOwner=new AccessOwner();
				pos.getInstallationSite().getAccessOwner().add(accessOwner);
				accessOwner.setIndividual(new Individual());
				accessOwner.getIndividual().setName(value);
			} else if(charCode.equals("V0128") || charCode.equals("V0130")){
				int lastindex = pos.getInstallationSite().getAccessOwner().size()-1;
				pos.getInstallationSite().getAccessOwner().get(lastindex).getIndividual().setForename(value);
			}			
		}
			
	}

	private void populateUnbundledProducts(GetFNPCustomerNGCSDataDAO dao,
			String customerNumber, CustomerInformation customerInformation,
			FNPInformationHolder infoHolder, BksHelper theHelper, GetFNPCustomerNGCSDataRequest request) throws Exception {
		ArrayList<HashMap<String, Object>> unbundledProducts = dao.getUnbundledProducts(customerNumber);
		for (int i = 0; i < unbundledProducts.size(); i++) {
			String servSubsId = (String)unbundledProducts.get(i).get("SERVICE_SUBSCRIPTION_ID");
			String serviceCode = (String)unbundledProducts.get(i).get("SERVICE_CODE");
			String serviceBillingName = (String) unbundledProducts.get(i).get("SERVICE_BILLING_NAME");
			ArrayList<HashMap<String,Object>> contract = dao.getContractData(servSubsId);
			if (contract == null || contract.size() == 0)
				contract = dao.getSDContractData(servSubsId);
			if (contract != null && contract.size() > 0) {
				String productCode = (String) contract.get(0).get("PRODUCT_CODE");
				List<String> productCodes = request.getProductCode();
				if (productCodes!=null&&productCodes.size()>0&&!productCodes.contains(productCode))
					continue;

				ProductOfferingSubscription pos = new ProductOfferingSubscription();
				customerInformation.getProductOfferingSubscription().add(pos);
				pos.setProductOfferingCode("container");
				pos.setProductOfferingName((String) contract.get(0).get("BILLING_NAME"));
				ProductPrice pp = new ProductPrice();
				pos.getProductPrice().add(pp);
				pp.setName((String) contract.get(0).get("PRICING_STRUCTURE_CODE"));
				pp.setType(productCode);
				pp.setPricingStructureName((String) contract.get(0).get("PRICING_STRUCTURE_NAME"));
				pp.setProductName((String) contract.get(0).get("PRODUCT_NAME"));
				setContractEndDate(contract.get(0),pos,theHelper);
				CalendarDate signDate = 
					theHelper.convertToDate(contract.get(0).get("PRIMARY_CUST_SIGN_DATE").toString());
				pos.setSignDate(signDate);
				Product prod = new Product();
				pos.setProductBundle(new ProductBundle());
				pos.getProductBundle().setSpecification("containerProductBundle");
				pos.getProductBundle().getProductBundleOrProduct().add(prod);
				prod.setSerialNumber(servSubsId);
				prod.setSpecification("containerProductAccess");
				
				populateNonRefAccessNumbers(dao, servSubsId, prod, theHelper);
				boolean bValid = populateNonRefAddressChars(dao, servSubsId,pos,request);
				if (!bValid){
					int last = customerInformation.getProductOfferingSubscription().size()-1;
					customerInformation.getProductOfferingSubscription().remove(last);
					continue;
				}
				populateCustomerFacing(prod,serviceCode,"Service Name",serviceBillingName,theHelper);
				populateContainerGuidingRule(dao,prod,servSubsId,theHelper);
				populateServiceLevel(dao, servSubsId, pos);
			}
		}
	}

	private void populateServiceLevel(GetFNPCustomerNGCSDataDAO dao, String servSubsId, ProductOfferingOccurrence pos) throws Exception{
		ArrayList<HashMap<String, Object>> services = dao.getChildServices(servSubsId,"SERV_LEVEL");
		for (int j = 0; j < services.size(); j++) {
			Product childProd = new Product();
			pos.getProductBundle().getProductBundleOrProduct().add(childProd);
			String childId = (String) services.get(j).get("SERVICE_SUBSCRIPTION_ID");
			childProd.setSerialNumber(childId);
			childProd.setSpecification("serviceLevel");
			List<CharacteristicValue> slCharList = childProd.getCharacteristicValue();
			ArrayList<HashMap<String, Object>> cvs = dao.getConfiguredValues(childId);
			for (int k = 0; k < cvs.size(); k++) {
				String value = (String) cvs.get(k).get("CONFIGURED_VALUE_STRING");
				String charCode = (String) cvs.get(k).get("SERVICE_CHARACTERISTIC_CODE");
				if(charCode.equals("S0001") || charCode.equals("S0100")) {
					CharacteristicValue item = new CharacteristicValue();
					slCharList.add(item);			
					item.setName("Service Level Stufe");
					item.setStringValue(value);
				}
				if(charCode.equals("S0002") || charCode.equals("S0101") || charCode.equals("V0007")){
					CharacteristicValue item = new CharacteristicValue();
					slCharList.add(item);
					item.setName("Maximale Entstörzeit");
					item.setStringValue(value);
				}	
			}
		}

	}
	private void populateNonRefAccessNumbers(GetFNPCustomerNGCSDataDAO dao, String servSubsId, Product prod, BksHelper theHelper) throws Exception{
		ArrayList<HashMap<String, Object>> accessNumbers = dao.getAccessNumberChars(servSubsId);
		List<CharacteristicValue> charList = prod.getCharacteristicValue();
		for (int j = 0; j < accessNumbers.size(); j++) {
			String value = (String) accessNumbers.get(j).get("ACCESS_NUMBER");
			String dataType = (String) accessNumbers.get(j).get("DATA_TYPE_RD");
			if (value == null)
				continue;
			CharacteristicValue charItem = new CharacteristicValue();
			charList.add(charItem);
			charItem.setName((String) accessNumbers.get(j).get("NAME"));
			if(dataType.equals("MAIN_ACCESS_NUM") || dataType.equals("ACC_NUM_RANGE")){
				String[] splitValue = value.split(";");
				FixedNetPhoneNumber number = new FixedNetPhoneNumber();
				number.setCountryCode(splitValue[0]);
				number.setAreaCode(splitValue[1]);
				String localNumber = theHelper.truncateStringNumeric15(splitValue[2]);
				number.setLocalNumber(localNumber);
				charItem.setFixedNetPhoneNumber(number);
			}else if(dataType.equals("MOBIL_ACCESS_NUM")){
				String[] splitValue = value.split(";");
				MobilePhoneNumber number = new MobilePhoneNumber();
				number.setCountryCode(splitValue[0]);
				number.setPrefix(splitValue[1]);
				String localNumber = (splitValue[2].length() > 15)?splitValue[2].substring(0,15):splitValue[2];
				number.setLocalNumber(localNumber);
				charItem.setMobilePhoneNumber(number );
			}else{
				charItem.setStringValue(value);
			}
		}
	}
	private void populateRootContact(GetFNPCustomerNGCSDataDAO dao,
			String customerNumber, CustomerInformation customerInformation, BksHelper theHelper) throws Exception {
		ArrayList<HashMap<String, Object>> rootCustomer = dao.getRootCustomer(customerNumber);
		if (rootCustomer == null || rootCustomer.size() == 0) {
			String errorCode = "BKS0010";
			String errorText = "The data for the customer "+customerNumber+" is corrupted.";
			throw new BksDataException(errorCode,errorText);
		}
		String rootNumber = rootCustomer.get(0).get("CUSTOMER_NUMBER").toString();
		ArrayList<HashMap<String, Object>> rootInfo = dao.getCustomerForCustNumber(rootNumber);
		Customer custNode = customerInformation.getCustomerInformationData().getCustomer().get(0);
		ContactPerson contactPerson = new ContactPerson();
		if (custNode.getOrganization() != null) {
			contactPerson.getContactPersonType().add("TOP");
			if (((String)rootInfo.get(0).get("ENTITY_TYPE")).equals("I"))
				contactPerson.setIndividual(getIndividual(customerNumber,rootInfo, theHelper));
			if (((String)rootInfo.get(0).get("ENTITY_TYPE")).equals("O"))
				contactPerson.setOrganization(getOrganization(customerNumber,rootInfo,theHelper));
			custNode.getOrganization().getContactPerson().add(contactPerson);
		}
	}

	private Organization getOrganization(String customerNumber,
			ArrayList<HashMap<String, Object>> rootInfo, BksHelper theHelper ) {
		Organization org = new Organization();
		org.setID("ID_"+customerNumber);
		org.setIncorporation(theHelper.convertCorpType((String)rootInfo.get(0).get("INCORPORATION_TYPE_RD")));
		org.setIncorporationCityName((String)rootInfo.get(0).get("INCORPORATION_CITY_NAME"));
		org.setIncorporationNumber((String)rootInfo.get(0).get("INCORPORATION_NUMBER_ID"));
		org.setSalutation((String)rootInfo.get(0).get("SALUTATION_DESCRIPTION"));
		org.setSector((String)rootInfo.get(0).get("INDUSTRIAL_SECTOR_RD"));
		String name = (String)rootInfo.get(0).get("NAME");
		org.setName((name.length()>50)?name.substring(0,50):name);
		org.setOrganizationSuffixName((String)rootInfo.get(0).get("ORGANIZATION_SUFFIX_NAME"));
		return org;
	}

	private Individual getIndividual(String customerNumber, ArrayList<HashMap<String, Object>> rootInfo, BksHelper theHelper) {
		Individual ind = new Individual();
		ind.setID("ID_"+customerNumber);
		ind.setForename((String)rootInfo.get(0).get("FORENAME"));
		ind.setName((String)rootInfo.get(0).get("NAME"));
		IdCard idCard = new IdCard();
		idCard.setIdCardNumber((String)rootInfo.get(0).get("ID_CARD_NUMBER"));
		idCard.setIdCardCountry((String)rootInfo.get(0).get("ID_CARD_COUNTRY_RD"));
		idCard.setIdCardType((String)rootInfo.get(0).get("ID_CARD_TYPE_RD"));
		idCard.setIdCardExpiryDate(theHelper.convertSqlDateToCalendar(rootInfo.get(0).get("ID_CARD_EXPIRATION_DATE")));
		ind.setIdCard(idCard);
		ind.setBirthDate(theHelper.convertSqlDateToCalendar(rootInfo.get(0).get("BIRTH_DATE")));
		ind.setNobilityPrefix((String)rootInfo.get(0).get("NOBILITY_PREFIX_DESCRIPTION"));
		ind.setProfession((String)rootInfo.get(0).get("PROFESSION_NAME"));
		ind.setSalutation((String)rootInfo.get(0).get("SALUTATION_DESCRIPTION"));
		ind.setSurnamePrefix((String)rootInfo.get(0).get("SURNAME_PREFIX_DESCRIPTION"));
        return ind;
	}

	private void filterAoMasteredBundles(
			ArrayList<HashMap<String, Object>> bundles, String requestedBundleId) {
		for (int j = 0; j < bundles.size(); j++) {
			String aoMastered = (String)bundles.get(j).get("AOSTATUS");
			String bundleId = (String)bundles.get(j).get("BUNDLE_ID");
			if (aoMastered == null || aoMastered.equals("N") ||
				(requestedBundleId!=null && !requestedBundleId.equals(bundleId))){
				bundles.remove(j);
				j--;
				continue;
			}
		}
	}

	private void handleOpenOrders(GetFNPCustomerNGCSDataDAO dao,
			String customerNumber, CustomerInformation customerInformation, 
			FNPInformationHolder infoHolder, 
			String requestType, BksHelper theHelper, List<String> productCodes)  throws Exception  {
		ArrayList<String> orderIds = handleComOpenOrders(dao,customerNumber,customerInformation,infoHolder,theHelper);
		ArrayList<HashMap<String, Object>> openOrders = dao.getOpenOrders(customerNumber);
		for (int i=0;openOrders != null && i<openOrders.size();i++) {
			if (orderIds.contains((String)openOrders.get(i).get("CUSTOMER_TRACKING_ID")))
				continue;
			populateOrderInfo(dao,openOrders.get(i),customerInformation,requestType,infoHolder,theHelper,productCodes);
		}
	}

	private ArrayList<String> handleComOpenOrders(
			GetFNPCustomerNGCSDataDAO dao, String customerNumber,
			CustomerInformation customerInformation,
			FNPInformationHolder infoHolder, BksHelper theHelper) throws Exception {
		ArrayList<String> orderIdList = new ArrayList<String>();
		ArrayList<HashMap<String, Object>> openOrders = dao.getExternalOrders(customerNumber);
		for (int i = 0;openOrders!=null && i < openOrders.size(); i++) {
			String orderId = (String) openOrders.get(i).get("ORDER_ID");
			BigDecimal orderPosId = (BigDecimal) openOrders.get(i).get("ORDER_POSITION_NUMBER");
			OpenOrder oo = getOpenOrders(orderId, orderPosId);
			if (oo != null){
				customerInformation.getOpenOrder().add(oo);
				orderIdList.add(orderId);
			}
		}
		return orderIdList;
	}

	private OpenOrder getOpenOrders(String orderId,BigDecimal orderPosId) throws Exception{
		ApplicationContext ac = DatabaseClient.getAc();
		if (ac == null)
			throw new MCFException("Application context has not been initialized.");
		OpenOrder openOrder = new OpenOrder();
		int timeToLive = 3600000;
		int timeOut = 100000;
		try {
			timeToLive = Integer.parseInt(DatabaseClientConfig.getSetting("servicebusclient.TimeToLive"));
			timeOut = Integer.parseInt(DatabaseClientConfig.getSetting("servicebusclient.TimeOut"));
		} catch (Exception e1) {
			// Ignore (Use Default)
		}
		try {
			GetOrderDetailsRequest request = new GetOrderDetailsRequest();
			request.setOrderID(orderId);
			request.setOrderPositionNumber(orderPosId);
			request.setOrderOutputFormat(OrderOutputFormat.SIDCOM);
			request.setReturnUnenrichedOrder(true);
			ServiceRequestSoap<GetOrderDetailsRequest> serviceRequest =
				new ServiceRequestSoap<GetOrderDetailsRequest>(request,
						"classpath:schema/COM-PROV-001.xsd",
				"de.vodafone.esb.service.sbus.com.com_prov_001");
			serviceRequest.setOperationName("/COM-001/GetOrderDetails");		
			serviceRequest.setTimeToLive(timeToLive);
			ServiceResponse<GetOrderDetailsResponse> respServ = messageSender.sendSyncSoapRequest(serviceRequest, timeOut);
			GetOrderDetailsResponse resp = respServ.getPayload();
			Node xml = (Node)resp.getOrderDetails().getAny();
//			DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
//			DOMImplementationLS impl = 
//			    (DOMImplementationLS)registry.getDOMImplementation("LS");
//			LSSerializer writer = impl.createLSSerializer();
//			String text = writer.writeToString(xml);
			TransformerFactory transFactory = TransformerFactory.newInstance();
			Transformer transformer = transFactory.newTransformer();
			StringWriter buffer = new StringWriter();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.transform(new DOMSource(xml),
			      new StreamResult(buffer));
			String text = buffer.toString();
			XmlProcessor xmlProcessor = (XmlProcessor) ac.getBean("mcf2XmlProcessor");
			Object order1 = xmlProcessor.deserialize(text, "schema/SIDCOM-FNPOrder-001.xsd", "de.vodafone.esb.schema.common.sidcom_fnporder_001");
			Object order = ((JAXBElement)order1).getValue();
			if (((BackendOrder)order).getOrderItem()==null || ((BackendOrder)order).getOrderItem().size() ==0)
				return null;
			OrderItem oi = ((BackendOrder)order).getOrderItem().get(0);
			String custIntention = oi.getCustomerIntention();
			ProductOfferingOccurrence poo = null;
			if( oi.getProductOfferingOccurrenceToBe()==null) {
				if (oi.getProductOfferingOccurrenceAsIs() != null && 
					custIntention.equals("Terminate"))
					poo = oi.getProductOfferingOccurrenceAsIs();
				else
					return null;
			} else 
			   poo = oi.getProductOfferingOccurrenceToBe();
//			ArrayList<String> gciList = BksRefDataCacheHandler.getGeneralData().get("BLOCK_INTE");
//			if (!gciList.contains(custIntention))
//				return null;
			openOrder.setCustomerOrderId("dummydummy");
			openOrder.setOrderId(orderId);
			if (custIntention.equals("Terminate"))
				openOrder.setOrderItemType("Terminate");
			else if (custIntention.equals("CreateNewLine"))
				openOrder.setOrderItemType("Create");
			else
				openOrder.setOrderItemType("Change");
			openOrder.getProductOfferingToBe().add(poo);
		} catch (Exception e) {
			log.warn("Call to Com failed. Message: "+e.getMessage());
			return null;
		}
		return  openOrder;

	}

	private void populateOrderInfo(GetFNPCustomerNGCSDataDAO dao,
			HashMap<String, Object> openOrder, CustomerInformation customerInformation, String requestType, 
			FNPInformationHolder infoHolder, BksHelper theHelper, List<String> productCodes) throws Exception {
		customerInformation.getOpenOrder().add(new OpenOrder());
		int lastIndex = customerInformation.getOpenOrder().size() - 1;
		String custOrderId = (String) openOrder.get("CUSTOMER_ORDER_ID");
		String barCode = (String) openOrder.get("CUSTOMER_TRACKING_ID");
		ArrayList<HashMap<String, Object>> openTickets =dao.getOrderTickets(custOrderId);
		String orderType = "Create";
		boolean isDslr = false;
		for (int i=0;openTickets != null && i<openTickets.size();i++) {
			theHelper.setDesiredNewElement(0);
			String usageMode = (String)openTickets.get(i).get("USAGE_MODE_VALUE_RD");
			if (usageMode.equals("4"))
				orderType = "Terminate";
			if (usageMode.equals("2") && !orderType.equals("Terminate"))
				orderType = "Change";

			theHelper.setDesiredNewElement(1);
			String servSubsId = (String)openTickets.get(i).get("SERVICE_SUBSCRIPTION_ID");
			String serviceCode = (String)openTickets.get(i).get("SERVICE_CODE");
			if (serviceCode.equals("I1043")||serviceCode.equals("I104A"))
				isDslr = true;
			String ticketId = (String)openTickets.get(i).get("SERVICE_TICKET_POSITION_ID");
			if (serviceCode.equals("VI201") && requestType.equals("CPD") && !isDslr){
				String errorText = "Voip 2end line is not supported.";
				String errorCode = "BKSCOM-ERROR-000026";
				throw new BksDataException(errorCode,errorText);
			}
			infoHolder.setVoice(false);
			if (Arrays.asList(voiceServices).contains(serviceCode))
				infoHolder.setVoice(true);
			if (serviceCode.equals("V0100") && doesVoiceNodeExist(customerInformation))
				theHelper.setDesiredNewElement(2);			
			String productCode = populateTableData(dao,servSubsId,serviceCode,customerInformation,true, infoHolder, theHelper,productCodes);
			if (productCode==null)
				continue;
			populateConfiguredValues(dao,null,ticketId,productCode,serviceCode,null,customerInformation,true,infoHolder.isVoice(), theHelper);
			populateAccessNumbers(dao,null,ticketId,serviceCode,null,customerInformation,true,infoHolder.isVoice(), theHelper);
		}
		int poIndex = customerInformation.getOpenOrder().get(lastIndex).getProductOfferingToBe().size();
		if (infoHolder.hasRelevantServices()&& poIndex > 0){
			customerInformation.getOpenOrder().get(lastIndex).setCustomerOrderId(custOrderId);
			customerInformation.getOpenOrder().get(lastIndex).setOrderId(barCode);
			customerInformation.getOpenOrder().get(lastIndex).setOrderItemType(orderType);
			ProductOfferingOccurrence poo = 
				customerInformation.getOpenOrder().get(lastIndex).getProductOfferingToBe().get(poIndex-1);
			if (poo.getProductOfferingCode() == null && infoHolder.isVoice()){
				poo.setProductOfferingCode("voice");
				poo.setProductOfferingName(getVoicePOName(customerInformation));
				poo.getProductBundle().setSpecification("fixedNetVoice");
			}
		} else {
			customerInformation.getOpenOrder().remove(lastIndex);
		}
	}

	private String getVoicePOName(CustomerInformation customerInformation) {
		List<ProductOfferingSubscription> pos = customerInformation.getProductOfferingSubscription();
		for (int i=0;i<pos.size();i++){
			if (pos.get(i).getProductOfferingCode().equals("voice"))
				return pos.get(i).getProductOfferingName();
		}
		return "";
	}

	private boolean doesVoiceNodeExist(CustomerInformation customerInformation) {
		int lastIndex = customerInformation.getOpenOrder().size() - 1;
		int poIndex = customerInformation.getOpenOrder().get(lastIndex).getProductOfferingToBe().size();
		if ( poIndex == 0)
			return false;
		ProductOfferingOccurrence poo = 
			customerInformation.getOpenOrder().get(lastIndex).getProductOfferingToBe().get(poIndex-1);
		if (poo.getProductOfferingCode().equals("voice"))
			return true;
		return false;
	}

	private void populateSalesPackageInfo(GetFNPCustomerNGCSDataDAO dao,
			String bundleId, CustomerInformation customerInformation) throws Exception  {
		ArrayList<HashMap<String, Object>> salesPackectInfo = dao.getSalesPackageInfo(bundleId);
		String packetCode = "none";
		String packetName = "none";
		if (salesPackectInfo != null && salesPackectInfo.size() > 0) {
			packetCode = (String) salesPackectInfo.get(0).get("SALES_PACKET_CODE");
			packetName = (String) salesPackectInfo.get(0).get("SALES_PACKET_NAME");
		}
		CustomerInformation.ProductOfferingSubscription pos = 
			new CustomerInformation.ProductOfferingSubscription();
		pos.setProductOfferingCode(packetCode);
		pos.setProductOfferingName(packetName);
		customerInformation.getProductOfferingSubscription().add(pos);
	}
	private boolean populateProductOffering(GetFNPCustomerNGCSDataDAO dao,
			GetFNPCustomerNGCSDataRequest request, 
                        ArrayList<HashMap<String, Object>>bundleItems, 
                        CustomerInformation customerInformation, FNPInformationHolder infoHolder, BksHelper theHelper) throws Exception {
		ArrayList<String> subIds = new ArrayList<String>();
		String voiceServiceId = null;
		List<String> productCodes = request.getProductCode();
		if (!isInastallationAddressOk(dao, bundleItems, request))
			return false;
		boolean isDslr = false;
		for (int i =0;bundleItems!=null&&i<bundleItems.size();i++) {
			infoHolder.setVoice(false);
			String servSubsId = bundleItems.get(i).get("SUPPORTED_OBJECT_ID").toString();
			String bundleItemType = bundleItems.get(i).get("BUNDLE_ITEM_TYPE_RD").toString();
			if (subIds.contains(servSubsId))
				continue;
			subIds.add(servSubsId);
			String serviceCode = bundleItems.get(i).get("SERVICE_CODE").toString();
			if (serviceCode.equals("I1043")||serviceCode.equals("I104A"))
				isDslr = true;
			theHelper.setDesiredNewElement(1);
			if (Arrays.asList(voiceServices).contains(serviceCode)){
				voiceServiceId = servSubsId;
				infoHolder.setVoice(true);
			}
			String productCode = populateTableData(dao,servSubsId,serviceCode,customerInformation,false,infoHolder, theHelper,productCodes);
			if (productCode==null)
				continue;
			if (productCode.equals("VI201") && request.getRequestType().equals("CPD") && ! isDslr){
				String errorText = "Voip 2end line is not supported.";
				String errorCode = "BKSCOM-ERROR-000026";
				throw new BksDataException(errorCode,errorText);
			}

			PopulateProductPrice(dao,servSubsId,"-",customerInformation,infoHolder.isVoice(),theHelper);
			if (bundleItemType.equals("ONLINE_SERVICE") || bundleItemType.equals("DSLONL_SERVICE")){
				populateChildServiceList(dao,productCode,voiceServiceId,serviceCode,
						customerInformation,infoHolder.isVoice(),theHelper);
			}
			populateGuidingRules(dao,servSubsId,serviceCode,customerInformation,theHelper,infoHolder.isVoice());
			if (!request.getRequestType().equals("GIS")) {
				populateAddressChars(dao, servSubsId, productCode ,serviceCode,customerInformation, theHelper);
				populateAccessNumbers(dao, servSubsId, null, serviceCode, null,
						customerInformation, false, infoHolder.isVoice(),theHelper);
				populateConfiguredValues(dao, servSubsId, null, productCode,
						serviceCode, null, customerInformation, false,infoHolder.isVoice(), theHelper);
			}
			theHelper.setDesiredNewElement(2);
			populateChildServiceList(dao,productCode,servSubsId,null,customerInformation,infoHolder.isVoice(),theHelper);
			if (infoHolder.isVoice()){
				populateSpecialServices(dao,servSubsId,"INTERNATIONAL","country","vodafoneInternational",customerInformation,theHelper);
				populateSpecialServices(dao,servSubsId,"TARIFF_OPTION","tariffOption","chargingOptions",customerInformation,theHelper);
			}
			if (serviceCode.equals("I1305"))
				populateTvCenterTarifOption(dao,servSubsId,customerInformation,theHelper);
			if (serviceCode.equals("I1290"))
				populateVolumeCaps(dao,servSubsId,customerInformation,theHelper);
			theHelper.setDesiredNewElement(1);
			populateDepHardwareList(dao,productCode,servSubsId,null,customerInformation,theHelper);
		}
		return true;
	}

	private void populateVolumeCaps(GetFNPCustomerNGCSDataDAO dao,
			String parentServSubsId, CustomerInformation customerInformation,
			BksHelper theHelper) throws Exception {
		ArrayList<HashMap<String, Object>> services = dao.getChildServices(parentServSubsId,"VOLUME_CAP");
		if (services!=null && services.size() > 0) {
			theHelper.setDesiredNewElement(2);
			String serviceCode = (String) services.get(0).get("SERVICE_CODE");
			String serviceName = (String) services.get(0).get("SERVICE_BILLING_NAME");
			String prefix="ProductOfferingSubscription.ProductOfferingOccurrence.Product.CharacteristicValue.";
			theHelper.setAttributeForPath(customerInformation,"set",prefix+"Name","volumeCap");
			theHelper.setAttributeForPath(customerInformation,"set",prefix+"StringValue",serviceCode);
			theHelper.setAttributeForPath(customerInformation,"set",prefix+"MappedValue",serviceName);
		}
	}

	private void populateGuidingRules(GetFNPCustomerNGCSDataDAO dao,
			String servSubsId, String serviceCode,
			CustomerInformation customerInformation, BksHelper theHelper, boolean isVoice) throws Exception {
		ArrayList<HashMap<String,Object>> guidingRules = dao.getGuidingRules(servSubsId);
		HashMap<String,ArrayList<String[]>> tableMap = null;
		if (guidingRules != null && guidingRules.size() > 0) {
			tableMap = BksRefDataCacheHandler.getSidTableColumnMap().get("GUID;"+serviceCode);
			if (tableMap == null)
				return ;
			for (int i = 0; i < guidingRules.size(); i++) {
				theHelper.setDesiredNewElement(2);
				if (isVoice || serviceCode.equals("I1305"))
					theHelper.setDesiredNewElement(3);
				Set<String> keys = tableMap.keySet();
				Iterator<String> keyiter = keys.iterator();
				while (keyiter.hasNext()){
					String columnName = keyiter.next();
					String columnValue = null;
					if (guidingRules.get(i).get(columnName)!=null)
						columnValue = guidingRules.get(i).get(columnName).toString();
					else
						break;
					String path = tableMap.get(columnName).get(0)[0];
					String conversionMethod = tableMap.get(columnName).get(0)[1];
					String prefix = tableMap.get(columnName).get(0)[2];
					String introVersion = tableMap.get(columnName).get(0)[4];
					if (introVersion != null && introVersion.compareTo("BKS-006") > 0 )
						continue;
					if (prefix != null)
						columnValue = prefix + columnValue;
					if (conversionMethod != null) {
						Object converted = theHelper.invokeMethod(theHelper,
								conversionMethod, columnValue, String.class);
						theHelper.setAttributeForPath(customerInformation, "set", path, converted);
					} else {
						theHelper.setAttributeForPath(customerInformation, "set", path, columnValue);
					}
				}
			}
		}
	}

	private boolean isInastallationAddressOk(GetFNPCustomerNGCSDataDAO dao,
			ArrayList<HashMap<String,Object>> bundleItems, 
			GetFNPCustomerNGCSDataRequest request) throws Exception {
		if (!request.getRequestType().equals("GIS") && !request.getRequestType().equals("GPS"))
			return true;
		boolean bValid= false;
		if (bundleItems != null && bundleItems.size() > 0) {
			for (int i=0;i<bundleItems.size();i++) {
				String servSubsId = (String) bundleItems.get(i).get("SUPPORTED_OBJECT_ID");
				ArrayList<HashMap<String,Object>> address = dao.getAddressChar(servSubsId);
				for (int j=0;address != null && address.size() >j;j++ ){
					if (request.getPersonData() != null)
						bValid = checkAddress(address.get(i), request.getPersonData().getZipCode(),
								request.getPersonData().getStreet(), 
								request.getPersonData().getHousenumber(), 
								request.getPersonData().getHousenumberSuffix());
					else if (request.getCompanyData() != null)
						bValid = checkAddress(address.get(i),request.getCompanyData().getZipCode(), 
								request.getCompanyData().getStreet(), 
								request.getCompanyData().getHousenumber(), 
								request.getCompanyData().getHousenumberSuffix());
					else
						bValid=true;
				}
			}
		}
		return bValid;
	}

	private void PopulateProductPrice(GetFNPCustomerNGCSDataDAO dao,
			String servSubsId, String productCode, CustomerInformation customerInformation, 
			boolean isVoice, BksHelper theHelper) throws Exception {
		ArrayList<HashMap<String, Object>>priceServs = dao.getProductPrice(servSubsId);
		for (int i =0;priceServs!=null&&i<priceServs.size();i++) {
			theHelper.setDesiredNewElement(1);
			String priceServSubsId = priceServs.get(i).get("SERVICE_SUBSCRIPTION_ID").toString();
			String priceServSubsCode = priceServs.get(i).get("SERVICE_CODE").toString();
			HashMap<String, ArrayList<String[]>> tableColMap = 
				BksRefDataCacheHandler.getSidTableColumnMap().get(productCode+";"+priceServSubsCode);
			if (tableColMap != null) {
				Set<String> keys = tableColMap.keySet();
				Iterator<String> keyiter = keys.iterator();
				while (keyiter.hasNext()){
					String columnName = keyiter.next();
					if (columnName.equals("-")){
						for (int j = 0; j < tableColMap.get(columnName).size(); j++) {
							String path = (String) tableColMap.get(columnName).get(j)[0];
							theHelper.setAttributeForPath(customerInformation, "set", path, tableColMap.get(columnName).get(j)[1].toString());
						}
						continue;
					}
					if (priceServs.get(i).get(columnName)==null)
						continue;
					for (int j = 0; j < tableColMap.get(columnName).size(); j++) {
						String columnValue = priceServs.get(i).get(columnName).toString();
						if (tableColMap.get(columnName).get(j)[2] != null)
							columnValue = tableColMap.get(columnName).get(j)[2] + columnValue;
						String conversionMethod = tableColMap.get(columnName).get(j)[1];
						if (conversionMethod != null) {
							Object converted = theHelper.invokeMethod(
									theHelper, conversionMethod, columnValue,
									String.class);
							theHelper.setAttributeForPath(customerInformation, "set", tableColMap
									.get(columnName).get(j)[0], converted);
						} else {
							theHelper.setAttributeForPath(customerInformation, "set", tableColMap
									.get(columnName).get(j)[0], columnValue);
						}
					}
				}
			}
			populateConfiguredValues(dao,priceServSubsId,null,"-",priceServSubsCode,null,customerInformation,false,isVoice, theHelper);
		}
	}

	private void populateAddressChars(GetFNPCustomerNGCSDataDAO dao,
			String servSubsId, String productCode,String serviceCode,
			CustomerInformation customerInformation, BksHelper theHelper) throws Exception {
		ArrayList<HashMap<String, Object>> addresses = dao.getAddressChar(servSubsId);
		if (addresses == null)
			return;
		HashMap<String,HashMap<String,Object>> charMap = BksRefDataCacheHandler.getSidServiceColumnMap().get(productCode+";"+serviceCode);
		if (charMap==null)
			charMap = BksRefDataCacheHandler.getSidServiceColumnMap().get("-;"+serviceCode);
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
						if (columnValue == null)
							continue;
						populateSingleColumn((String[])valueObj,columnValue,customerInformation,theHelper);
					} else {
						ArrayList<String[]> list = (ArrayList<String[]>)valueObj;
						for (int j = 0; list != null && j < list.size(); j++) {
							String[] item = list.get(j);
							if (columnValue == null)
								continue;
							populateSingleColumn(item,columnValue,customerInformation,theHelper);
						}
					}
				}
			}
		}
	}

	private boolean checkAddress(HashMap<String, Object> address,
			String postalCode, String street, String streetNo,
			String numberSuffix) {
		String dbPostalCode = (String) address.get("POSTAL_CODE");
		String dbStreet = ((String) address.get("STREET_NAME")).toUpperCase();
		if (street!=null && street.endsWith("*"))
			street = street.substring(0,street.length()-1);
		if ((postalCode == null || dbPostalCode.startsWith(postalCode)) &&
			(street == null || dbStreet.startsWith(street.toUpperCase())) &&
			(streetNo == null || streetNo.equals(address.get("STREET_NUMBER"))) &&
			(numberSuffix == null || numberSuffix.equals(address.get("STREET_NUMBER_SUFFIX"))))
			return true;
		return false;
	}

	private boolean populateNonRefAddressChars(GetFNPCustomerNGCSDataDAO dao,
			String servSubsId,ProductOfferingSubscription pos, GetFNPCustomerNGCSDataRequest request ) throws Exception {
		ArrayList<HashMap<String, Object>> addresses = dao.getAddressChar(servSubsId);
		if (addresses == null)
			return true;
		boolean bValid = false;
		for (int j = 0;!bValid && j < addresses.size(); j++) {
			String charCode = (String) addresses.get(j).get("SERVICE_CHARACTERISTIC_CODE");
			if (!charCode.equals("V0014"))
				continue;
			if(addresses.get(j).get("POSTAL_CODE")==null) {
				log.warn("addresses" + addresses.get(j));
				continue;
			}
			if (pos.getInstallationSite() == null)
				pos.setInstallationSite(new GermanPropertyAddress());
			pos.getInstallationSite().setAddress(new Address());

			pos.getInstallationSite().getAddress().setCity(
					(String) addresses.get(j).get("CITY_NAME"));
			pos.getInstallationSite().getAddress().setCitySuffixName(
					(String) addresses.get(j).get("CITY_SUFFIX_NAME"));
			pos.getInstallationSite().getAddress().setCountryCode(
					(String) addresses.get(j).get("COUNTRY_RD"));
			pos.getInstallationSite().getAddress().setPostalCode(
					(String) addresses.get(j).get("POSTAL_CODE"));
			pos.getInstallationSite().getAddress().setStreet(
					(String) addresses.get(j).get("STREET_NAME"));
			pos.getInstallationSite().getAddress().setStreetNumber(
					(String) addresses.get(j).get("STREET_NUMBER"));
			pos.getInstallationSite().getAddress().setStreetNumberSuffix(
					(String) addresses.get(j).get("STREET_NUMBER_SUFFIX"));
			if (request.getPersonData() != null)
				bValid = checkAddress(addresses.get(j), request.getPersonData().getZipCode(),
						request.getPersonData().getStreet(), 
						request.getPersonData().getHousenumber(), 
						request.getPersonData().getHousenumberSuffix());
			else if (request.getCompanyData() != null)
				bValid = checkAddress(addresses.get(j),request.getCompanyData().getZipCode(), 
						request.getCompanyData().getStreet(), 
						request.getCompanyData().getHousenumber(), 
						request.getCompanyData().getHousenumberSuffix());
		}
		if (request.getPersonData() == null&&request.getCompanyData() == null)
			return true;
		return bValid;
	}

	private void populateSingleColumn(String[] valueObj, String columnValue,
			CustomerInformation customerInformation, BksHelper theHelper) throws Exception {
		String path = valueObj[0];
		String conversionMethod = valueObj[1];
		String fieldNumber = valueObj[2];
		if (path != null && columnValue != null){
			if (fieldNumber != null)
				columnValue = getPartValue(columnValue,Integer.parseInt(fieldNumber));
			if (conversionMethod == null) {
				String value = (String)	theHelper.setAttributeForPath(customerInformation, "get",path, null);
				if (value == null)
					theHelper.setAttributeForPath(customerInformation, "set", path, columnValue);
				else{
					Pattern p = Pattern.compile("[a-zA-Z]*");
					Matcher m = p.matcher(value);
					boolean b = m.matches();
					if (b)
						value = columnValue + value;
					else
						value = value + columnValue;
					theHelper.setAttributeForPath(customerInformation, "set", path, value);
				}
			} else {
				if (columnValue.trim().length() != 0){
					Object converted = theHelper.invokeMethod(theHelper, conversionMethod, columnValue, String.class);
					theHelper.setAttributeForPath(customerInformation, "set", path, converted);
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

	private void populateDepHardwareList(GetFNPCustomerNGCSDataDAO dao,
			String productCode, String parentServSubsId, String parentServiceCode,
			CustomerInformation customerInformation, BksHelper theHelper) throws Exception {
		ArrayList<HashMap<String, Object>> services = dao.getHardwareDepService(parentServSubsId);
		if (services == null || services.size() == 0)
			return;
		populateDependentFunctions(dao,services,productCode,parentServiceCode,customerInformation,true,false,theHelper);
	}

	private void populateChildServiceList(GetFNPCustomerNGCSDataDAO dao,
			String productCode, String parentServSubsId, String parentServiceCode,
			CustomerInformation customerInformation, boolean isVoice, BksHelper theHelper) throws Exception {
		ArrayList<HashMap<String, Object>> services = dao.getChildServices(parentServSubsId,null);
		if (services == null || services.size() == 0)
			return;
		populateDependentFunctions(dao,services,productCode,parentServiceCode,customerInformation,false,isVoice,theHelper);
	}

	private void populateTvCenterTarifOption(GetFNPCustomerNGCSDataDAO dao,
			String parentServSubsId, CustomerInformation customerInformation, BksHelper theHelper) throws Exception {
		ArrayList<HashMap<String, Object>> services = dao.getChildServices(parentServSubsId,null);
		final String[] tarifServices = {	"I1354","I1355","I6011","I6012","I6013","I6014",
											"I6015","I6016","I6017","I6018","I6019","I6020",
											"I6101","I6102","I6103","I6104","I6105","I6106",
											"I6107","I6108","I6109","I6110","I6201","I6202",
											"I6203","I6204","I6205","I6211","I6212","I6213",
											"I6214","I6215","V0303","I1351"};
		if (services != null && services.size() > 0) {
			theHelper.setDesiredNewElement(2);
			theHelper
					.setAttributeForPath(
							customerInformation,
							"set",
							"ProductOfferingSubscription.ProductOfferingOccurrence.ProductBundle.ProductBundleOrProduct.Specification",
							"chargingOptions");
			for (int i = 0; i < services.size(); i++) {
				theHelper.setDesiredNewElement(3);
				String serviceCode = (String) services.get(i).get("SERVICE_CODE");
				String serviceName = (String) services.get(i).get("SERVICE_NAME");
				if (!Arrays.asList(tarifServices).contains(serviceCode))
					continue;
				theHelper
				.setAttributeForPath(
						customerInformation,
						"set",
						"ProductOfferingSubscription.ProductOfferingOccurrence.ProductBundle.ProductBundleOrProduct.CharacteristicValue.Name",
				"tariffOption");
				theHelper
				.setAttributeForPath(
						customerInformation,
						"set",
						"ProductOfferingSubscription.ProductOfferingOccurrence.ProductBundle.ProductBundleOrProduct.CharacteristicValue.StringValue",
						serviceCode);
				theHelper
				.setAttributeForPath(
						customerInformation,
						"set",
						"ProductOfferingSubscription.ProductOfferingOccurrence.ProductBundle.ProductBundleOrProduct.CharacteristicValue.MappedValue",
						serviceName);
			}
		}
	}

	private void populateSpecialServices(GetFNPCustomerNGCSDataDAO dao,
			String parentServSubsId,String serviceType,
			String charName,String spec,CustomerInformation customerInformation, BksHelper theHelper) throws Exception {
		ArrayList<HashMap<String, Object>> services = dao.getChildServices(parentServSubsId,serviceType);
		if (services != null && services.size() > 0) {
			theHelper.setDesiredNewElement(2);
			theHelper
					.setAttributeForPath(
							customerInformation,
							"set",
							"ProductOfferingSubscription.ProductOfferingOccurrence.ProductBundle.ProductBundleOrProduct.Specification",
							spec);
			for (int i = 0; i < services.size(); i++) {
				theHelper.setDesiredNewElement(3);
				String serviceCode = (String) services.get(i).get("SERVICE_CODE");
				String serviceName = (String) services.get(i).get("SERVICE_NAME");
				theHelper
						.setAttributeForPath(
								customerInformation,
								"set",
								"ProductOfferingSubscription.ProductOfferingOccurrence.ProductBundle.ProductBundleOrProduct.CharacteristicValue.Name",
								charName);
				theHelper
						.setAttributeForPath(
								customerInformation,
								"set",
								"ProductOfferingSubscription.ProductOfferingOccurrence.ProductBundle.ProductBundleOrProduct.CharacteristicValue.StringValue",
								serviceCode);
				theHelper
				.setAttributeForPath(
						customerInformation,
						"set",
						"ProductOfferingSubscription.ProductOfferingOccurrence.ProductBundle.ProductBundleOrProduct.CharacteristicValue.MappedValue",
						serviceName);
			}
		}
	}

	private void populateDependentFunctions(GetFNPCustomerNGCSDataDAO dao,
			ArrayList<HashMap<String, Object>> services, String productCode,
			String parentServiceCode, CustomerInformation customerInformation, 
			boolean bIsHardware,boolean isVoice, BksHelper theHelper) throws Exception {
		for (int i =0;i<services.size();i++) {
			String serviceCode = services.get(i).get("SERVICE_CODE").toString();
			String servSubsId = services.get(i).get("SERVICE_SUBSCRIPTION_ID").toString();
			theHelper.setDesiredNewElement(1);
			if (isVoice)
				theHelper.setDesiredNewElement(2);
			HashMap<String, ArrayList<String[]>> tableColMap = null;
			if (parentServiceCode != null){
				tableColMap = BksRefDataCacheHandler.getSidTableColumnMap().get(productCode+";"+serviceCode+";"+parentServiceCode);
			    if (tableColMap==null)
			    	tableColMap = BksRefDataCacheHandler.getSidTableColumnMap().get("-;"+serviceCode+";"+parentServiceCode);
			} else
				tableColMap = BksRefDataCacheHandler.getSidTableColumnMap().get(((bIsHardware)?"-":productCode)+";"+serviceCode);
				
			if (tableColMap != null) {
				Set<String> keys = tableColMap.keySet();
				Iterator<String> keyiter = keys.iterator();
				while (keyiter.hasNext()){
					String columnName = keyiter.next();
					if (columnName.equals("-")){
						for (int j = 0; j < tableColMap.get(columnName).size(); j++) {
							String path = (String) tableColMap.get(columnName).get(j)[0];
							theHelper.setAttributeForPath(customerInformation, "set", path, tableColMap.get(columnName).get(j)[1].toString());
						}
						continue;
					}
					if (services.get(i).get(columnName)==null)
						continue;
					for (int j = 0; j < tableColMap.get(columnName).size(); j++) {
						String columnValue = services.get(i).get(columnName).toString();
						if (tableColMap.get(columnName).get(j)[2] != null)
							columnValue = tableColMap.get(columnName).get(j)[2] + columnValue;
						String conversionMethod = tableColMap.get(columnName).get(j)[1];
						if (conversionMethod != null) {
							Object converted = theHelper.invokeMethod(
									theHelper, conversionMethod, columnValue,
									String.class);
							theHelper.setAttributeForPath(customerInformation, "set", tableColMap
									.get(columnName).get(j)[0], converted);
						} else {
							theHelper.setAttributeForPath(customerInformation, "set", tableColMap
									.get(columnName).get(j)[0], columnValue);
						}
					}
				}
			}
			if (populateConfiguredValues(dao,servSubsId,null,((bIsHardware)?"-":productCode),
					 serviceCode,parentServiceCode,customerInformation,false,isVoice, theHelper))
				continue;
			populateAddressChars(dao,servSubsId,productCode,serviceCode,customerInformation,theHelper);
			populateAccessNumbers(dao,servSubsId,null,serviceCode,parentServiceCode,customerInformation,false,isVoice, theHelper);
			if (bIsHardware&&tableColMap!=null) {
				ArrayList<HashMap<String, Object>> hwChildServs = dao.getChildServices(servSubsId,null);
				for (int j = 0; j < hwChildServs.size(); j++) {
					{
						String hwChildId = hwChildServs.get(j).get("SERVICE_SUBSCRIPTION_ID").toString();
						String hwChildCode = hwChildServs.get(j).get("SERVICE_CODE").toString();
						if (populateConfiguredValues(dao,hwChildId,null,productCode,
								hwChildCode,null,customerInformation,false,isVoice, theHelper))
								continue;

					}
					populateSubscriptionExistance(hwChildServs,productCode,serviceCode,customerInformation, theHelper);
				}
			}
		}
	}

	private void populateSubscriptionExistance(ArrayList<HashMap<String, Object>> services,String productCode,
			String parentServiceCode, CustomerInformation customerInformation, BksHelper theHelper) throws Exception {
		HashMap<String,Object[]> existSubs = BksRefDataCacheHandler.getSidServiceExistMap().get(productCode);
		for (int i =0;existSubs != null && i<services.size();i++) {
			String key = (String) services.get(i).get("SERVICE_CODE");
			if (existSubs.get(key)== null)
				key += ";"+parentServiceCode;
			if (existSubs.get(key) != null){
				theHelper.setDesiredNewElement(2);
				for (int j = 0; j < existSubs.get(key).length; j++) {
					String targetPath = (String) existSubs.get(key)[j++];
					String defaultValue = (String) existSubs.get(key)[j++];
					String conversionMethod = (String) existSubs.get(key)[j++]; // Skip intro
					if (conversionMethod != null) {
						Object converted = theHelper.invokeMethod(theHelper,
								conversionMethod, defaultValue, String.class);
						theHelper.setAttributeForPath(customerInformation,
								"set", targetPath, converted);
					} else {
						theHelper.setAttributeForPath(customerInformation,
								"set", targetPath, defaultValue);
					}
				}
			}
		}
	}

	private boolean populateConfiguredValues(GetFNPCustomerNGCSDataDAO dao,
			String servSubsId, String ticketId,String productCode,String serviceCode, String parentServiceCode,
			CustomerInformation customerInformation,boolean bOpenOrder, boolean isVoice, BksHelper theHelper) throws Exception {
		HashMap<String,ArrayList<String[]>> charPath = null;
		if (bOpenOrder){
			if (parentServiceCode == null)
				charPath = BksRefDataCacheHandler.getSidOpenServiceCharMap().get(productCode+";"+serviceCode);
			else
				charPath = BksRefDataCacheHandler.getSidOpenServiceCharMap().get(productCode+";"+serviceCode+";"+parentServiceCode);
		} else {
			if (parentServiceCode == null)
				charPath = BksRefDataCacheHandler.getSidServiceCharMap().get(productCode+";"+serviceCode);
			else
				charPath = BksRefDataCacheHandler.getSidServiceCharMap().get(productCode+";"+serviceCode+";"+parentServiceCode);

		}
		if (charPath==null)
			return false;

		ArrayList<HashMap<String, Object>> configValues = null;
		if (servSubsId != null)
			configValues = dao.getConfiguredValues(servSubsId);
		else if (ticketId != null)
			configValues = dao.getConfiguredValuesByStp(ticketId);
		if (configValues == null)
			return false;
		boolean bLocationTAEFound = false;
		Set<String> keys = charPath.keySet();
		Iterator<String> keyiter = keys.iterator();
		while (keyiter.hasNext()){
			String refCharCode = keyiter.next();
			String columnValue = null;
			String serviceCharCode = null;
			for (int i = 0;i < configValues.size() && columnValue == null; i++) {
				serviceCharCode = (String) configValues.get(i).get("SERVICE_CHARACTERISTIC_CODE");
				if (refCharCode.equals(serviceCharCode))
					columnValue = (String) configValues.get(i).get("CONFIGURED_VALUE_STRING");
			}
			boolean bJumpoverChar = false;
			if (columnValue == null) {
				for (int j = 0; j < charPath.get(refCharCode).size(); j++) {
					String defaultValue = charPath.get(refCharCode).get(j)[5];
					String fixedValue = charPath.get(refCharCode).get(j)[6];
					if (fixedValue == null && defaultValue==null)
						bJumpoverChar = true;
				}
			}
			if (bJumpoverChar)
				continue;
			theHelper.setDesiredNewElement(2);
			if (isVoice || serviceCode.equals("I1305"))
				theHelper.setDesiredNewElement(3);
			String[] toplevelChars = {"V0123","V0127","V0128","V0129","V0130"};
			if (Arrays.asList(toplevelChars).contains(refCharCode))
				theHelper.setDesiredNewElement(-1);
			for (int j = 0; j < charPath.get(refCharCode).size(); j++) {
				String rdsId = charPath.get(refCharCode).get(j)[1];
				String conversionMethod = charPath.get(refCharCode).get(j)[2];
				String fieldNumber = charPath.get(refCharCode).get(j)[3];
				String defaultValue = charPath.get(refCharCode).get(j)[5];
				String fixedValue = charPath.get(refCharCode).get(j)[6];
				String introVersion = charPath.get(refCharCode).get(j)[7];
				if (introVersion != null && introVersion.compareTo("BKS-006") > 0 )
					continue;
				String charValue = columnValue;
				if (fixedValue != null)
					charValue=fixedValue;
				else if (columnValue == null)
					charValue=defaultValue;
				if (charValue == null)
					continue;
				if (fieldNumber != null){
					if (conversionMethod != null) {
						String converted = (String)theHelper.invokeMethod(theHelper,
								conversionMethod, charValue, String.class);
						if (converted == null)
							continue;
						conversionMethod = null;
						charValue = converted;
					}
					String[] fields = charValue.split(",");
					if (fields.length >= Integer.parseInt(fieldNumber)){
						charValue = fields[Integer.parseInt(fieldNumber)-1];
					} else
						continue;
				}
				if (rdsId == null && conversionMethod == null)
					theHelper.setAttributeForPath(customerInformation, "set",
							charPath.get(refCharCode).get(j)[0], charValue);
				else if (conversionMethod != null) {
					Object converted = theHelper.invokeMethod(theHelper,
							conversionMethod, charValue, String.class);
					if (converted!=null)
						theHelper.setAttributeForPath(customerInformation, "set",
								charPath.get(refCharCode).get(j)[0], converted);
				}
			}
			if (serviceCharCode!=null && serviceCharCode.equals("V0123") && columnValue != null)
				bLocationTAEFound = true;
			if (serviceCharCode!=null && serviceCharCode.equals("V0112")){
				HashMap<String,String>  articleFilter = new HashMap<String, String>();
				articleFilter.put("Artikelnummer",columnValue);
				String hardwareType = theHelper.getRefdataValue(articleFilter, "Hardwareartikel", "typ");
				String hardwareDesc = theHelper.getRefdataValue(articleFilter, "Hardwareartikel", "Bezeichnung");
				String[] allowedHardwareTypes = {"M","IAD","STB","EG"};
				if (hardwareType==null || !Arrays.asList(allowedHardwareTypes).contains(hardwareType)) {
					log.debug("The hardware service with article number "
							+columnValue+" has type of "+hardwareType+". It will be filtered out");
					if (bOpenOrder) {
						List<OpenOrder> pos = customerInformation.getOpenOrder();
						int lastIndex = pos.size() - 1;
						List<ProductOfferingOccurrence> productOccurances = pos.get(lastIndex).getProductOfferingToBe();
						lastIndex = productOccurances.size() - 1;
						productOccurances.remove(lastIndex);
					} else {
						List<ProductOfferingSubscription> pos = customerInformation.getProductOfferingSubscription();
						int lastIndex = pos.size() - 1;
						List<ProductOfferingOccurrence> productOccurances = pos.get(lastIndex).getProductOfferingOccurrence();
						lastIndex = productOccurances.size() - 1;
						productOccurances.remove(lastIndex);
					}
					return true;
				}
				if(hardwareType.equals("M"))
					hardwareType="Modem";
				if (bOpenOrder){
					List<OpenOrder> pos = customerInformation.getOpenOrder();
					int lastIndex= pos.size()-1;
					CharacteristicValue item = new CharacteristicValue();
					List<ProductOfferingOccurrence> productOffering = pos.get(lastIndex).getProductOfferingToBe();
					lastIndex= productOffering.size()-1;
					item.setName("type");
					item.setStringValue(hardwareType);
					productOffering.get(lastIndex).getProduct().getCharacteristicValue().add(item);
					item = new CharacteristicValue();
					item.setName("name");
					item.setStringValue(hardwareDesc);
					productOffering.get(lastIndex).getProduct().getCharacteristicValue().add(item);
				} else {
					List<ProductOfferingSubscription> pos = customerInformation.getProductOfferingSubscription();
					int lastIndex= pos.size()-1;
					CharacteristicValue item = new CharacteristicValue();
					List<ProductOfferingOccurrence> productOccurances = pos.get(lastIndex).getProductOfferingOccurrence();
					lastIndex= productOccurances.size()-1;
					item.setName("type");
					item.setStringValue(hardwareType);
					productOccurances.get(lastIndex).getProduct().getCharacteristicValue().add(item);
					item = new CharacteristicValue();
					item.setName("name");
					item.setStringValue(hardwareDesc);
					productOccurances.get(lastIndex).getProduct().getCharacteristicValue().add(item);
				}
			}
		}
		String[] accessServices = {"V0003","V0010","V0011","V0012","I1210","I1213","V0004","V0002","V0001","I1043","I1290","I121z"};
		if (!bLocationTAEFound && Arrays.asList(accessServices).contains(serviceCode) && !bOpenOrder){
			List<ProductOfferingSubscription> subList = customerInformation.getProductOfferingSubscription();
			int lastIndex = subList.size() - 1;
			ProductOfferingSubscription sub = subList.get(lastIndex);
			if (sub.getInstallationSite() == null)
				sub.setInstallationSite(new GermanPropertyAddress());
			DetachedHouse detachedHaous = new DetachedHouse();
			detachedHaous.setDetails(" ");
			sub.getInstallationSite().setDetachedHouse(detachedHaous);
		}
		return false;
	}

	private void populateAccessNumbers(GetFNPCustomerNGCSDataDAO dao,
			String servSubsId, String ticketId, String serviceCode, Object object,
			CustomerInformation customerInformation,boolean bOpenOrder, boolean isVoice, BksHelper theHelper) throws Exception {
		HashMap<String,HashMap<Integer,Object[]>> charMap = null;
		HashMap<String,String[]> accessName = null;
		if (bOpenOrder) {
			charMap = BksRefDataCacheHandler.getSidOpenServiceFieldMap().get(
					serviceCode);
			accessName = BksRefDataCacheHandler.getSidOpenAccessName();
		} else {
			charMap = BksRefDataCacheHandler.getSidServiceFieldMap().get(
					serviceCode);
			accessName = BksRefDataCacheHandler.getSidAccessName();
		}
		if (charMap == null || accessName == null)
			return;
		ArrayList<HashMap<String, Object>> accessNumbers=null;
		if (servSubsId != null)
			accessNumbers = dao.getAccessNumberChars(servSubsId);
		else if (ticketId != null)
			accessNumbers = dao.getAccessNumberCharsByStp(ticketId);

		for (int i =0;accessNumbers!=null&&i<accessNumbers.size();i++) {
			theHelper.setDesiredNewElement(2);
			if (isVoice)
				theHelper.setDesiredNewElement(3);
			String serviceCharCode = (String) accessNumbers.get(i).get("SERVICE_CHARACTERISTIC_CODE");
			String number = (String) accessNumbers.get(i).get("ACCESS_NUMBER");
			if (number == null)
				continue;
			String fixedPath;
			String fixedValue;
			if (accessName.containsKey(serviceCode + ";" + serviceCharCode)) {
				fixedPath = accessName.get(serviceCode + ";" + serviceCharCode)[0];
				fixedValue = accessName
						.get(serviceCode + ";" + serviceCharCode)[1];
				if (fixedPath != null && fixedValue != null)
					theHelper.setAttributeForPath(customerInformation, "set",
							fixedPath,fixedValue);
			}
			if (charMap.get(serviceCharCode) != null){
				HashMap<Integer,Object[]> fieldPathMap = charMap.get(serviceCharCode);
				String[] numberFields = number.split(";");
				for(Integer j=0;j<numberFields.length;j++){
					Object[] paths = fieldPathMap.get(j+1);
					for (int k = 0; paths!=null&&k < paths.length; k++) {
						String path = ((String[])paths[k/3])[k++%3];
						String conversionMethod = ((String[])paths[k/3])[++k%3];

						if (conversionMethod != null) {
							Object converted = theHelper.invokeMethod(theHelper,
									conversionMethod, numberFields[j], String.class);
							if (converted!=null)
								theHelper.setAttributeForPath(customerInformation, "set",path, converted);
						} else {
							theHelper.setAttributeForPath(customerInformation, "set",path, numberFields[j]);
						}
					}
				}
			}
		}
	}

	private String populateTableData(GetFNPCustomerNGCSDataDAO dao,
			String servSubsId, String serviceCode,
			CustomerInformation customerInformation,boolean bOpenOrder, 
			FNPInformationHolder infoHolder, BksHelper theHelper, List<String> productCodes) throws Exception {
		ArrayList<HashMap<String,Object>> contract = dao.getContractData(servSubsId);
		if (contract == null || contract.size() == 0)
			contract = dao.getSDContractData(servSubsId);
		String productCode = null;
		if (contract != null && contract.size() > 0) {
			productCode = contract.get(0).get("PRODUCT_CODE").toString();
			if (productCodes!=null&&productCodes.size()>0&&!productCodes.contains(productCode))
				return null;
			if (Arrays.asList(hardwareServices).contains(serviceCode))
				productCode = "-";
			HashMap<String,ArrayList<String[]>> tableMap = null;
			if(bOpenOrder){
				tableMap = BksRefDataCacheHandler.getSidOpenTableColumnMap().get(productCode+";"+serviceCode);
				if (tableMap != null && tableMap.size() > 0 && mapContainsOfferingCode(tableMap))
					infoHolder.setHasRelevantServices(true);
			}else {
				String[] mainServices = {"V0010","V0003","I1210","V0004","V0002","V0001","V0011","V0012","I1043","I1213","I1290","I121z"};
				if (Arrays.asList(mainServices).contains(serviceCode) ||  infoHolder.changeEndDate()){
					if (Arrays.asList(mainServices).contains(serviceCode))
						infoHolder.setChangeEndDate(false);
					int lastIndex = customerInformation.getProductOfferingSubscription().size() -1;
					CalendarDate signDate = 
						theHelper.convertToDate(contract.get(0).get("PRIMARY_CUST_SIGN_DATE").toString());
					customerInformation.getProductOfferingSubscription().get(lastIndex).setSignDate(signDate);
					setContractEndDate(contract.get(0),customerInformation.getProductOfferingSubscription().get(lastIndex), theHelper);
				}
				tableMap = BksRefDataCacheHandler.getSidTableColumnMap().get(productCode+";"+serviceCode);
			}
			if (tableMap == null)
				return productCode;
			Set<String> keys = tableMap.keySet();
			Iterator<String> keyiter = keys.iterator();
			while (keyiter.hasNext()){
				String columnName = keyiter.next();
				if (columnName.equals("-")){
					for (int i = 0; i < tableMap.get(columnName).size(); i++) {
						String path = (String) tableMap.get(columnName).get(i)[0];
						theHelper.setAttributeForPath(customerInformation, "set", path, tableMap.get(columnName).get(i)[1].toString());
					}
					continue;
				}
				for (int i = 0; i < tableMap.get(columnName).size(); i++) {
					String columnValue = null;
					if (contract.get(0).get(columnName)!=null)
						columnValue = contract.get(0).get(columnName).toString();
					else
						break;
					String path = tableMap.get(columnName).get(i)[0];
					String conversionMethod = tableMap.get(columnName).get(i)[1];
					String prefix = tableMap.get(columnName).get(i)[2];
					String introVersion = tableMap.get(columnName).get(i)[4];
					if (introVersion != null && introVersion.compareTo("BKS-006") > 0 )
						continue;
					if (prefix != null)
						columnValue = prefix + columnValue;
					if (conversionMethod != null) {
						Object converted = theHelper.invokeMethod(theHelper,
								conversionMethod, columnValue, String.class);
						theHelper.setAttributeForPath(customerInformation, "set", path, converted);
					} else {
						theHelper.setAttributeForPath(customerInformation, "set", path, columnValue);
					}
				}
			}
		} else {
			throw new BksDataException("BKSCOM-ERROR-000008","No contract found for service subscription "+servSubsId);
		}
		return productCode;
	}

	private boolean mapContainsOfferingCode(
			HashMap<String, ArrayList<String[]>> tableMap) {
		ArrayList<String[]> list = tableMap.get("-");
		for (int j = 0; list !=null && j < list.size(); j++) {
			if (list.get(j)[0].contains("ProductOfferingCode"))
				return true;
		}
		return false;
	}

	private void setContractEndDate(HashMap<String, Object> contract,
			ProductOfferingSubscription productOfferingSubscription, BksHelper theHelper) {
		Object startDate = contract.get("MIN_PERIOD_START_DATE");
		String minUnit = (String)contract.get("MIN_PERIOD_DUR_UNIT_RD");
		Integer minValue = null;
		if (contract.get("MIN_PERIOD_DUR_VALUE") != null) 
			minValue = ((BigDecimal) contract.get("MIN_PERIOD_DUR_VALUE")).intValue(); 
		String durationUnit = (String)contract.get("AUTO_EXTENT_DUR_UNIT_RD");
		Integer extValue = null;
		if (contract.get("AUTO_EXTENT_DUR_VALUE") != null)
			extValue = ((BigDecimal)contract.get("AUTO_EXTENT_DUR_VALUE")).intValue();
		boolean bAutoExtend = false;
		if (contract.get("AUTO_EXTENSION_IND") != null)
			bAutoExtend = ((String)contract.get("AUTO_EXTENSION_IND")).equals("Y");
		CalendarDate endDate = 
			theHelper.getContractEndDate(startDate, minUnit, minValue, durationUnit, extValue, bAutoExtend);
		productOfferingSubscription.setContractDurationEnd(endDate);
		Integer noticeValue = null;
		if (contract.get("NOTICE_PERIOD_DUR_VALUE") != null) {
			noticeValue = ((BigDecimal) contract.get("NOTICE_PERIOD_DUR_VALUE")).intValue(); 
			productOfferingSubscription.setLastNoticeDateToTerminate(endDate.plusMonths(-1*noticeValue));
		}
		ContractStartDate startvalue = new ContractStartDate();
		startvalue.setValue(theHelper.convertSqlDateToCalendar(startDate));
		productOfferingSubscription.setContractStartDate(startvalue );
		MinimumContractDuration value = new MinimumContractDuration();
		value.setValue(BigDecimal.valueOf(minValue));
		productOfferingSubscription.setMinimumContractDuration(value);
		CalendarDate terminationDate = theHelper.convertSqlDateToCalendar(contract.get("TERMINATION_DATE"));
		if (terminationDate != null && !terminationDate.toString().equals("1901-1-1")) {
			productOfferingSubscription.setTerminationData(new TerminationData());
			productOfferingSubscription.getTerminationData().setTerminationDate(terminationDate);
			productOfferingSubscription.getTerminationData().setTerminationEntryDate(
					theHelper.convertSqlDateToCalendar(contract.get("NOTICE_PERIOD_START_DATE")));
			productOfferingSubscription.getTerminationData().setTerminationReason(
					(String)contract.get("TERMINATION_REASON_RD"));
		}
	}

	
	private void populateAddress(GetFNPCustomerNGCSDataDAO dao, String addressId,
			String refDataKey, Object customerInformation, BksHelper theHelper) throws Exception {
		HashMap<String,Object[]> colPathMap = 
			BksRefDataCacheHandler.getCcbSidMap().get(refDataKey);

		ArrayList<HashMap<String, Object>> address = dao.getAddressInfo(addressId);
		if (address !=null && address.size() > 0 )
			populateSidNode(colPathMap, address.get(0), customerInformation, theHelper);
	}

	private void populateAccessInfo(GetFNPCustomerNGCSDataDAO dao, String accessInfoId,
			String refDataKey, Object customerInformation, BksHelper theHelper) throws Exception {
		HashMap<String,Object[]> colPathMap = 
				BksRefDataCacheHandler.getCcbSidMap().get(refDataKey+";ACCESS_INFORMATION");

		ArrayList<HashMap<String, Object>> accessInfo = dao.getAccessInfo(accessInfoId);
		if (accessInfo !=null && accessInfo.size() > 0){
			populateSidNode(colPathMap, accessInfo.get(0), customerInformation, theHelper);
			String validationId = (String)accessInfo.get(0).get("EMAIL_VALIDATION_ID");
			if (validationId!=null){
				colPathMap = BksRefDataCacheHandler.getCcbSidMap().get(refDataKey+";EMAIL_VALIDATION");
				ArrayList<HashMap<String, Object>> valid = dao.getEmailValidation(validationId);
				if (valid!=null && valid.size()>0)
					populateSidNode(colPathMap,valid.get(0), customerInformation, theHelper);
			}
		}
	}

	private void populateBillingAccount(GetFNPCustomerNGCSDataDAO dao,
			HashMap<String, Object> customer, CustomerInformation customerInformation, BksHelper theHelper) throws Exception  {
		String customerNumber = (String) customer.get("CUSTOMER_NUMBER");
		String maskDigits = (String)customer.get("MASKING_DIGITS_RD");
		ArrayList<HashMap<String, Object>> accountData = dao.getAccountData(customerNumber);
		for(int j=0;j<accountData.size(); j++){
			String accountNumber = (String)accountData.get(j).get("ACCOUNT_NUMBER");
			boolean isItemized = false;
			FNPInformationHolder infoholder = new FNPInformationHolder();
			infoholder.setInvDeliveryType("PAPER");
			BillingAccount ba = new BillingAccount();
			ba.setInvoiceDelivery(new InvoiceDelivery());
			ArrayList<HashMap<String, Object>> docPattern = dao.getDocumentPatternForAccount(accountNumber);
			for (int i = 0;docPattern!=null&& i < docPattern.size(); i++) {
				String documentTempl = (String)docPattern.get(i).get("DOCUMENT_TEMPLATE_NAME");
				isItemized = getInvoiceDelivery(documentTempl,infoholder);
			}
			ba.getInvoiceDelivery().setInvoiceDeliveryType(infoholder.getInvDeliveryType());
			if (!isItemized)
				ba.getInvoiceDelivery().setItemizedBill("no");
			else if (isItemized && maskDigits.equals("-1"))
				ba.getInvoiceDelivery().setItemizedBill("complete");
			else
				ba.getInvoiceDelivery().setItemizedBill("shortened");
			HashMap<String,Object[]> colPathMap = 
				BksRefDataCacheHandler.getCcbSidMap().get("ACCOUNT;ACCOUNT");
			customerInformation.getCustomerInformationData().getBillingAccount().add(ba);
			if (docPattern!=null && docPattern.size() > 0)
				populateSidNode(colPathMap, docPattern.get(0), customerInformation, theHelper);
			getAccountInfo(dao,accountNumber,customerInformation, theHelper);
			ArrayList<HashMap<String, Object>> docRec = dao.getDocumentRecipientForAccount(accountNumber);
			if (docRec != null && docRec.size() > 0){
				if (docRec.get(0).get("ENTITY_TYPE").toString().equals("I"))
					colPathMap = 
						BksRefDataCacheHandler.getCcbSidMap().get("ACCOUNT;INDIVIDUAL");
				else
					colPathMap = 
						BksRefDataCacheHandler.getCcbSidMap().get("ACCOUNT;ORGANIZATION");

				populateSidNode(colPathMap, docRec.get(0), customerInformation, theHelper);
				String salutation = docRec.get(0).get("SALUTATION_DESCRIPTION").toString();
				if (!salutation.equals("Herr") && !salutation.equals("Frau") && !salutation.equals("Firma")) {
					DocumentRecipient recipient = ba.getDocumentRecipient();
					if (recipient!=null && docRec.get(0).get("ENTITY_TYPE").toString().equals("I"))
						recipient.getIndividual().setSalutation("Herr");
					if (recipient!=null && !docRec.get(0).get("ENTITY_TYPE").toString().equals("I"))
						recipient.getOrganization().setSalutation("Firma");
				}

				String addressId = (String) docRec.get(0).get("ADDRESS_ID");
				String refDataKey = "ACCOUNT;ADDRESS";
				if (addressId!= null)
					populateAddress(dao,addressId,refDataKey,customerInformation, theHelper);
				String accInfoId = (String) docRec.get(0).get("ACCESS_INFORMATION_ID");
				if (accInfoId != null)
					populateAccessInfo(dao,accInfoId,"ACCOUNT",customerInformation, theHelper);
			}
		}
		List<BillingAccount> account = customerInformation.getCustomerInformationData().getBillingAccount();
		for (int i = 0; i < account.size(); i++) {
			MethodOfPayment mop = account.get(i).getMethodOfPayment();
			if (mop!=null){
				Mandate mandate = mop.getMandate();
				if (mandate!=null&&(mandate.isRecurrentIndicator()==null ||	mandate.getSignatureDate()==null))
					mop.setMandate(null);
			}
		}
	}

	private void getAccountInfo(GetFNPCustomerNGCSDataDAO dao,
			String accountNumber, CustomerInformation customerInformation, BksHelper theHelper) throws Exception {
			ArrayList<HashMap<String, Object>> accountData = dao.getAccountDataByAccountNumber(accountNumber);
			if (accountData!=null && accountData.size() > 0){
				HashMap<String,Object[]> colPathMap = BksRefDataCacheHandler.getCcbSidMap().get("ACCOUNT;ACCOUNT");
				theHelper.setDesiredNewElement(1);
				populateSidNode(colPathMap,accountData.get(0),customerInformation, theHelper);
			}
	}

	private boolean getInvoiceDelivery(String documentTempl,FNPInformationHolder infoholder) {
        boolean isItemized = false;
        if (documentTempl.equals("webBill mit EGN")){
        	infoholder.setInvDeliveryType("WEBBILL");
        	isItemized=true;
        } else if (documentTempl.equals("webBill ohne EGN")){
        	infoholder.setInvDeliveryType("WEBBILL");
        } else if (documentTempl.equals("Vodafone webBill mit EGN")){
        	infoholder.setInvDeliveryType("WEBBILL");
        	isItemized=true;
        } else if (documentTempl.equals("Vodafone webBill ohne EGN")){
        	infoholder.setInvDeliveryType("WEBBILL");
        } else if (documentTempl.equals("Rechnung per E-Mail")){
        	infoholder.setInvDeliveryType("EMAIL");
        } else if (documentTempl.equals("Privatkunden EVN")){
        	isItemized=true;
        }
 		return isItemized;
	}

	private void populateCustomerData(HashMap<String, Object> custList, CustomerInformation customerInformation, FNPInformationHolder infoHolder, BksHelper theHelper) throws Exception {
			HashMap<String,Object[]> colPathMap = 
				BksRefDataCacheHandler.getCcbSidMap().get("CUSTOMER;CUSTOMER");

			theHelper.setDesiredNewElement(-1);
			populateSidNode(colPathMap, custList, customerInformation, theHelper);
			boolean isIndividual = custList.get("ENTITY_TYPE").toString().equals("I");
			if (isIndividual)
				colPathMap = BksRefDataCacheHandler.getCcbSidMap().get("CUSTOMER;INDIVIDUAL");
			else
				colPathMap = BksRefDataCacheHandler.getCcbSidMap().get("CUSTOMER;ORGANIZATION");
			populateSidNode(colPathMap, custList, customerInformation, theHelper);
			customerInformation.getCustomerInformationStatus().getCustomerType().get(0).setCategory(infoHolder.getCategory());
			String salutation = custList.get("SALUTATION_DESCRIPTION").toString();
			if (!salutation.equals("Herr") && !salutation.equals("Frau") && !salutation.equals("Firma")) {
				Customer cust = customerInformation.getCustomerInformationData().getCustomer().get(0);
				if (cust!=null && isIndividual)
					cust.getIndividual().setSalutation("Herr");
				if (cust!=null && !isIndividual)
					cust.getOrganization().setSalutation("Firma");
			}
	}

	private void populateSidNode(HashMap<String, Object[]> colPathMap, 
			HashMap<String,Object> ccbData, Object customerInformation, BksHelper theHelper) throws Exception {
		if (colPathMap == null)
			return;
		Set<String> keys = colPathMap.keySet();
		Iterator<String> keyiter = keys.iterator();
		while (keyiter.hasNext()){
			String column = keyiter.next();
			int index = 0;
			if (column.equals("-")){
				String path = (String) colPathMap.get(column)[0];
				theHelper.setAttributeForPath(customerInformation, "set", path, 
						colPathMap.get(column)[1].toString());
			} else if (column.equals("PROVIDER_CODE")){
				String path = (String) colPathMap.get(column)[0];
				String classification = (String)ccbData.get("CLASSIFICATION_RD");
				String rootCustomer = (String)ccbData.get("ROOT_CUSTOMER_NUMBER");
				String converted = theHelper.mapClassificationToProvider(classification,rootCustomer);
				theHelper.setAttributeForPath(customerInformation, "set", path, 
						converted);
			} else if (column.equals("STREET_NUMBER")){
				String path = (String) colPathMap.get(column)[0];
				String streetNumber = (String)ccbData.get(column);
				if (streetNumber!=null) {
					theHelper.setAttributeForPath(customerInformation, "set", path, getPartValue(streetNumber, 1));
					path = (String) colPathMap.get("STREET_NUMBER_SUFFIX")[0];
					theHelper.setAttributeForPath(customerInformation, "set", path, getPartValue(streetNumber, 2));
				}
			} else if (column.equals("VALIDATION_DATE")){
				String path = (String) colPathMap.get(column)[0];
				String applyMethod = (String) colPathMap.get(column)[1];
				Timestamp value = (Timestamp)ccbData.get(column);
				String status = (String)ccbData.get("VALIDATION_STATUS");
				if (Arrays.asList(validStatus).contains(status)){
					Object converted = theHelper.invokeMethod(theHelper,applyMethod, value.toString().trim(), String.class);
					theHelper.setAttributeForPath(customerInformation, "set", path, converted);
				}
			} else if (column.equals("ERROR_CODE")){
				String path = (String) colPathMap.get(column)[0];
				String value = (String)ccbData.get(column);
				String status = (String)ccbData.get("VALIDATION_STATUS");
				if (status.equals("IN_PROGRESS")||status.equals("FAILED"))
					theHelper.setAttributeForPath(customerInformation, "set", path, value);
			} else {
				while (index < colPathMap.get(column).length) {
					String path = (String) colPathMap.get(column)[index++];
					String convertMethod = (String) colPathMap.get(column)[index++];
					String defaultValue = (String) colPathMap.get(column)[index++];
					String fldNumStr = (String) colPathMap.get(column)[index++];
					String introVersion = (String) colPathMap.get(column)[index++];
					if (introVersion != null && introVersion.compareTo("BKS-006") > 0 )
						continue;
					
					Object value = ccbData.get(column);
					if (value != null) {
						if (convertMethod == null)
							theHelper.setAttributeForPath(customerInformation, "set", path, value);
						else {
							Object converted = theHelper.invokeMethod(theHelper,
									convertMethod, value.toString(), String.class);
							if (converted==null)
								converted=defaultValue;
							if (converted!=null)
								theHelper.setAttributeForPath(customerInformation, "set", path,converted);
						}
					}
				}
			}
		}

	}
	ArrayList<HashMap<String, Object>> getCustomerList(GetFNPCustomerNGCSDataDAO dao,GetFNPCustomerNGCSDataRequest request, FNPInformationHolder infoHolder, String requestType, BksHelper theHelper) throws Exception{
		ArrayList<HashMap<String, Object>> custList = null;
		String barcode = request.getBarcode();
		PersonDataQueryInput person = request.getPersonData();
		CompanyDataQueryInput company = request.getCompanyData();
		String servSubsId = request.getServiceSubscriptionId();
		String tasi = null;
		if (request.getTASI()!=null)
			tasi = request.getTASI();
		if (request.getNetworkAccessID()!=null)
			tasi = request.getNetworkAccessID();

		ComplexPhoneNumber accnum = request.getAccessNumber();
		if (accnum != null) {
			custList = getCustomerByAccessNumber(dao,accnum, infoHolder);
			if (custList!=null && custList.size() > 1){
				String errorText = "Found access number more than one time.";
				String errorCode = "BKSCOM-ERROR-000007";
				throw new BksDataException(errorCode,errorText);
			}
			if (custList!=null && custList.size() > 0) {
				String custNo = (String) custList.get(0).get("CUSTOMER_NUMBER");
				Object bd = custList.get(0).get("BIRTH_DATE");
				String encryptedpw = (String) custList.get(0).get("USER_PASSWORD");
				String encryptKey = (String) custList.get(0).get("ENCRYPTION_KEY");
				theHelper.setCustomerEncKey(encryptKey);
				String password = theHelper.decryptCustPasswd(encryptedpw);
				
				if (request.getCustomerNumber() != null && !request.getCustomerNumber().equals(custNo)){
					String errorText = "customer number on access number found does not match customer number provided";
					String errorCode = "BKSCOM-ERROR-000003";
					throw new BksDataException(errorCode,errorText);
				}
				CalendarDate dateOfBirth = null;
				String inputPw = null;
				if (person != null) {
					dateOfBirth = person.getDateOfBirth();
					inputPw = person.getUserPassword();
				}
				if (dateOfBirth != null && bd != null && !dateOfBirth.equals(CalendarDate.from(bd.toString(), "yyyy-MM-dd"))){
					String errorText = "Provided birth date does not match";
					String errorCode = "BKSCOM-ERROR-000005";
					throw new BksDataException(errorCode,errorText);
				}			
				if (inputPw != null && !inputPw.equalsIgnoreCase(password)){
					String errorText = "Wrong password";
					String errorCode = "BKSCOM-ERROR-000004";
					throw new BksDataException(errorCode,errorText);
				}
			}
		}else if (request.getCustomerNumber() != null) {
			custList = dao.getCustomerForCustNumber(request.getCustomerNumber());
			if (custList != null && custList.size() > 0){
				if (custList.get(0).get("ENTITY_TYPE").toString().equals("I"))
					infoHolder.setCategory("RESIDENTIAL");
				else
					infoHolder.setCategory("BUSINESS");
			}
		}else if (barcode != null) {
			custList = dao.getCustomerForBarCode(barcode);
			if (custList != null && custList.size() > 0){
				if (custList.get(0).get("ENTITY_TYPE").toString().equals("I"))
					infoHolder.setCategory("RESIDENTIAL");
				else
					infoHolder.setCategory("BUSINESS");
			}
		}else if (person != null) {
			if (request.getRequestType().equals("CPD")){
				String errorText = "The input parameter (person data) does not match request type CPD.";
				String errorCode = "BKSCOM-ERROR-000008";
				throw new BksDataException(errorCode,errorText);
			}
			custList = getCustomerByPersonData(dao,person, infoHolder, requestType);
		}else if (company != null){
			if (request.getRequestType().equals("CPD")){
				String errorText = "The input parameter (company data) does not match request type CPD.";
				String errorCode = "BKSCOM-ERROR-000008";
				throw new BksDataException(errorCode,errorText);
			}
			custList = getCustomerByCompanyData(dao,company, infoHolder,requestType);
		}else if (servSubsId != null){
			custList = dao.getCustomerByServiceSubscription(servSubsId);
			if (custList != null && custList.size() > 0){
				if (custList.get(0).get("ENTITY_TYPE").toString().equals("I"))
					infoHolder.setCategory("RESIDENTIAL");
				else
					infoHolder.setCategory("BUSINESS");
			}
		}else if (tasi != null){
			try {
				custList = dao.getCustomerDataByTasi(tasi+"%");
			} catch (UncategorizedSQLException e) {
				if (e.getSQLException().getErrorCode() == 1013){
					String errorCode = "BKSCOM-ERROR-000008";
					String errorText = "The wild card search with "+tasi+"% is taking too much time. Please adjust your search criteria.";
					throw new BksDataException(errorCode,errorText);
				}
				throw e;
			}
			if (custList != null && custList.size() > 0){
				if (custList.get(0).get("ENTITY_TYPE").toString().equals("I"))
					infoHolder.setCategory("RESIDENTIAL");
				else
					infoHolder.setCategory("BUSINESS");
			}
		}else {
			String errorText = "Wrong input setting";
			String errorCode = "BKSCOM-ERROR-000002";
			throw new BksDataException(errorCode,errorText);
		}  

		if ((custList == null) || (custList.size() < 1)) {
			String errorText = "No customer found";
			String errorCode = "BKSCOM-ERROR-000002";
			throw new BksDataException(errorCode,errorText);
		}
		
		ArrayList<String> custNumberList = new ArrayList<String>();
		for (int i = 0; i < custList.size(); i++) {
			String custNum = (String) custList.get(i).get("CUSTOMER_NUMBER");
			if (custNumberList.contains(custNum)){
				custList.remove(i);
				i--;
				continue;
			} else
				custNumberList.add(custNum);
			
			String group = (String) custList.get(i).get("CUSTOMER_GROUP_RD");
			if (group.equals("20") || group.equals("21")) {
				if (custList.size()==1){
					String errorText = "Arcor Employee";
					String errorCode = "BKSCOM-ERROR-000021";
					throw new BksDataException(errorCode,errorText);
				} else {
					custList.remove(i);
					i--;
					continue;
				}
			}
		}

		if ((custList == null) || (custList.size() < 1)) {
			String errorText = "No customer found";
			String errorCode = "BKSCOM-ERROR-000002";
			throw new BksDataException(errorCode,errorText);
		}
		
		return custList;
	}

	private ArrayList<HashMap<String, Object>> getCustomerByAccessNumber(
			GetFNPCustomerNGCSDataDAO dao, ComplexPhoneNumber accnum, FNPInformationHolder infoHolder) throws Exception {
		ArrayList<HashMap<String, Object>> custList = null;
		String countryCode = accnum.getCountryCode();
		String cityCode = accnum.getLocalAreaCode();
		String localNumber = accnum.getPhoneNumbers().getPhoneNumber().get(0);
		custList = dao.getCustomerExactAccessNumberCustData(
				countryCode, cityCode, localNumber);
		if (custList == null || custList.size() == 0) {
			custList = dao.getCustomerAccessNumberCustData(countryCode,
					cityCode, localNumber);
		}
		if (custList != null && custList.size() > 0){
			if (custList.get(0).get("ENTITY_TYPE").toString().equals("I"))
				infoHolder.setCategory("RESIDENTIAL");
			else
				infoHolder.setCategory("BUSINESS");
		}
		return custList;
	}

	private ArrayList<HashMap<String, Object>> getCustomerByCompanyData(
			GetFNPCustomerNGCSDataDAO dao, CompanyDataQueryInput company, FNPInformationHolder infoHolder, String requestType) throws Exception {
		ArrayList<HashMap<String, Object>> custList = null;
		String name = company.getCompanyName();
		String zipCode = company.getZipCode();
		String street = company.getStreet();
		String streetNo = company.getHousenumber();
		String streetNoSuf = company.getHousenumberSuffix();
		String orgSuf = company.getCompanySuffixName();
		String regNo = company.getCommercialRegisterNumber();
		String phonName = (new FudickarPhoneticConverter()).convert(name);
		String phonOrgSuffix = null;
		if (orgSuf != null) 
			phonOrgSuffix = (new FudickarPhoneticConverter()).convert(orgSuf);
		
		CalendarDate dateOfBirth = null;
		if (company.getContact() != null)
			dateOfBirth = company.getContact().getDateOfBirth();

		if (name != null) {
			boolean bFuzzySearch = Boolean.parseBoolean(DatabaseClientConfig.getSetting("databaseclient.EnableFuzzySearch"));
			try {
				if (bFuzzySearch && dao.getDataSourceName().equals("effonl") &&
					(requestType.equals("ICP") || requestType.equals("CID")))
					custList = dao.getCustomerFuzzyCompany(phonName, phonOrgSuffix, zipCode,
							street, streetNo,streetNoSuf,regNo);
				else
					custList = dao.getCustomerCompanyData(name, zipCode,
						street, streetNo,streetNoSuf,orgSuf,regNo);
			} catch (UncategorizedSQLException e) {
				if (e.getSQLException().getErrorCode() == 1013){
					String errorCode = "BKSCOM-ERROR-000008";
					String errorText = "Query Timed out.";
					throw new BksDataException(errorCode,errorText);
				}
				throw e;
			}
		}
		if (custList!= null && custList.size() > 0) {
			if (dateOfBirth != null){
				String customerNo = custList.get(0).get("CUSTOMER_NUMBER").toString();
				ArrayList<HashMap<String, Object>> contacts = dao.getOwnerContact(customerNo);
				if (contacts!= null && contacts.size() > 0 ){
					for (int i = 0; i < custList.size(); i++) {
						Object bd = contacts.get(i).get("BIRTH_DATE");
						if (bd != null && !dateOfBirth.equals(CalendarDate.from(bd.toString(), "yyyy-MM-dd"))) {
							if (custList.size() == 1) {
								String errorText = "Provided birth date does not match";
								String errorCode = "BKSCOM-ERROR-000005";
								throw new BksDataException(errorCode,errorText);
							} else {
								custList.remove(i);
								i--;
								continue;
							}
						}
					}
				}
				infoHolder.setCategory("SOHO");
			} else if (regNo != null) {
				infoHolder.setCategory("BUSINESS");
				for (int i = 0; i < custList.size(); i++) {
					String register = (String) custList.get(i).get("INCORPORATION_NUMBER_ID");
					if (!regNo.equals(register)) {
						if (custList.size() == 1) {
							String errorText = "Provided registration number does not match";
							String errorCode = "BKSCOM-ERROR-000005";
							throw new BksDataException(errorCode,errorText);
						} else {
							custList.remove(i);
							i--;
							continue;
						}
					}
				}
			} else {
				infoHolder.setCategory("BUSINESS");
				String corpType = (String)custList.get(0).get("INCORPORATION_TYPE_RD");
				if (corpType != null && !corpType.equals("UNREGISTERED") && !corpType.equals("NOT")
						&& !requestType.equals("ICP") && !requestType.equals("CID")){
					String errorText = "For corp. type "+corpType+" registration number must be provided.";
					String errorCode = "BKSCOM-ERROR-000005";
					throw new BksDataException(errorCode,errorText);
				}
			}
		}
		return custList;
	}

	private ArrayList<HashMap<String, Object>> getCustomerByPersonData(
			GetFNPCustomerNGCSDataDAO dao, PersonDataQueryInput person, 
			FNPInformationHolder infoHolder, String requestType) throws Exception {
		ArrayList<HashMap<String, Object>> custList = null;
		String familyName = person.getFamilyName();
		String zipCode = person.getZipCode();
		String street = person.getStreet();
		String streetNo = person.getHousenumber();
		String streetNoSuf = person.getHousenumberSuffix();
		String firstName = person.getFirstName();
		String phonName = (new FudickarPhoneticConverter()).convert(familyName);
		String phonFirstName = null;
		if (firstName != null) 
			phonFirstName = (new FudickarPhoneticConverter()).convert(firstName);
		
		CalendarDate dateOfBirth = person.getDateOfBirth();
		infoHolder.setCategory("RESIDENTIAL");

		if (familyName == null) {
			String errorText = "Missing mandatory input (family name).";
			String errorCode = "BKSCOM-ERROR-000008";
			throw new BksDataException(errorCode,errorText);
		} else if (street == null && !requestType.equals("ICP") && !requestType.equals("CID")) {
			String errorText = "Missing mandatory input (street name).";
			String errorCode = "BKSCOM-ERROR-000008";
			throw new BksDataException(errorCode,errorText);
		} else {
			if (familyName != null)
				familyName = familyName.replaceAll( "\\*", "%");
			if (street != null)
				street = street.replaceAll( "\\*", "%");
		}
		if (familyName != null) {
			boolean bFuzzySearch = Boolean.parseBoolean(DatabaseClientConfig.getSetting("databaseclient.EnableFuzzySearch"));
			try {
				if (bFuzzySearch && dao.getDataSourceName().equals("effonl") &&
				   (requestType.equals("ICP") || requestType.equals("CID")))
					custList = dao.getCustomerFuzzyPerson(phonFirstName, phonName, 
							zipCode, street,streetNo,streetNoSuf, dateOfBirth);
				else
					custList = dao.getCustomerPersonData(familyName, zipCode,
						street,streetNo,streetNoSuf, firstName, dateOfBirth);
			} catch (UncategorizedSQLException e) {
				if (e.getSQLException().getErrorCode() == 1013){
					String errorCode = "BKSCOM-ERROR-000008";
					String errorText = "Query Timed out.";
					throw new BksDataException(errorCode,errorText);
				}
				throw e;
			}
		}
		return custList;
	}
}
