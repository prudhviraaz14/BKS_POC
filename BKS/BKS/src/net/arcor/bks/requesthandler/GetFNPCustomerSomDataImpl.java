/*
    $Header:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/GetFNPCustomerSomDataImpl.java-arc   1.5   Feb 13 2018 07:10:40   makuier  $

    $Log:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/GetFNPCustomerSomDataImpl.java-arc  $
 * 
 *    Rev 1.5   Feb 13 2018 07:10:40   makuier
 * Open Orders SOM versioning
 * 
 *    Rev 1.4   Apr 03 2017 09:36:54   makuier
 * Pass null to the thread when the client version not presented in the input.
 * 
 *    Rev 1.3   Mar 14 2017 11:30:36   gaurav.verma
 * Populate the dunning status
 * 
 *    Rev 1.2   Jan 19 2017 15:08:04   makuier
 * Validate provider customer
 * 
 *    Rev 1.1   Sep 21 2016 15:30:42   makuier
 * Changed the tag name for multithreading.
 * 
 *    Rev 1.0   Jun 30 2016 07:49:20   makuier
 * Initial revision.
 * 
*/
package net.arcor.bks.requesthandler;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;

import javax.xml.bind.JAXBElement;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.arcor.bks.client.DatabaseClient;
import net.arcor.bks.client.DatabaseClientConfig;
import net.arcor.bks.common.BksDataException;
import net.arcor.bks.common.BksHelper;
import net.arcor.bks.common.BksRefDataCacheHandler;
import net.arcor.bks.common.FNPInformationHolder;
import net.arcor.bks.db.GetFNPCustomerSomDataDAO;
import net.arcor.mcf2.exception.base.MCFException;
import net.arcor.mcf2.model.ServiceObjectEndpoint;
import net.arcor.mcf2.model.ServiceResponse;
import net.arcor.mcf2.sender.MessageSender;
import net.arcor.mcf2.xml.XmlProcessor;
import net.arcor.orderschema.BillingAccount;
import net.arcor.orderschema.CustomerData;
import net.arcor.orderschema.LineChange;
import net.arcor.orderschema.LineCreation;
import net.arcor.orderschema.Order;
import net.arcor.orderschema.OrderPositionType;
import net.arcor.orderschema.ProductBundle;
import net.arcor.orderschema.ProviderChange;
import net.arcor.orderschema.Relocation;
import net.arcor.orderschema.Termination;

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

import de.vodafone.ccm.bks_types_001.ContractPartnerChangePacket;
import de.vodafone.ccm.bks_types_001.LineChangePacket;
import de.vodafone.ccm.bks_types_001.LineCreationPacket;
import de.vodafone.ccm.bks_types_001.RelocationPacket;
import de.vodafone.ccm.bks_types_001.TerminationPacket;
import de.vodafone.esb.schema.common.commontypes_esb_001.ComplexPhoneNumber;
import de.vodafone.esb.service.sbus.bks.bks_osf_001.GetFNPCustomerSomDataRequest;
import de.vodafone.esb.service.sbus.bks.bks_osf_001.GetFNPCustomerSomDataResponse;
import de.vodafone.esb.service.sbus.com.com_prov_001.GetOrderDetailsRequest;
import de.vodafone.esb.service.sbus.com.com_prov_001.GetOrderDetailsResponse;
import de.vodafone.esb.service.sbus.com.com_prov_001.OrderOutputFormat;
import de.vodafone.linkdb.epsm_bkstypes_001.CompanyDataQueryInput;
import de.vodafone.linkdb.epsm_bkstypes_001.PersonDataQueryInput;
import edu.emory.mathcs.backport.java.util.Arrays;

@SoapEndpoint(soapAction = "/BKS-001/GetFNPCustomerSomData", context = "de.vodafone.esb.service.sbus.bks.bks_osf_001", schema = "classpath:schema/BKS-OSF-001.xsd")
public class GetFNPCustomerSomDataImpl implements SoapOperation<GetFNPCustomerSomDataRequest, GetFNPCustomerSomDataResponse> {

    /** The logger. */
    private final static Log log = LogFactory.getLog(GetFNPCustomerSomDataImpl.class);
	final static String[] supportedProductCodes = {"I1201","I1203","I1204","I1208","V0001","V0002","V8000"};
	final static String[] supportedServiceCodes = {"I1040","I1043","I1210","I1213", "I121z",
            "I1290","I1305","V0001","V0002","V0003","V0004","V0010","V0011","V8000","Wh003","Wh004"};

    @Autowired
	private MessageSender messageSender;
	/**
	 * The operation "GetFNPCustomerSomData" of the service "GetFNPCustomerSomData".
	 */
	public ServiceResponseSoap<GetFNPCustomerSomDataResponse> invoke(ServiceObjectEndpoint<GetFNPCustomerSomDataRequest> serviceObject) 
			throws TechnicalException,FunctionalException,FunctionalRuntimeException {
		BksHelper theHelper = new BksHelper();
		waitForInitialization();
		GetFNPCustomerSomDataDAO dao = (GetFNPCustomerSomDataDAO) DatabaseClient.getBksDataAccessObject(null, "GetFNPCustomerSomDataDAO");
		GetFNPCustomerSomDataRequest request = serviceObject.getPayload();
		GetFNPCustomerSomDataResponse output = new GetFNPCustomerSomDataResponse();
        long startTime = System.currentTimeMillis();
		try {
			output = getSOMCustomerData(dao,request, theHelper);
			long endTime = System.currentTimeMillis();
			log.info("Call duration : " +(endTime-startTime)+" ms");
		} catch (BksDataException e) {
			log.error(e.getText());
			throw new FunctionalException(e.getCode(),e.getText());
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new TechnicalException("BKSCOM-ERROR-000008","Precondition check error",e);
		} finally {
			try {
				dao.closeConnection();
			} catch (Exception e) {
			}
		}

		return new ServiceResponseSoap<GetFNPCustomerSomDataResponse>(output);
	}

	private void waitForInitialization() {
		int tries =0;
		int maxTries = getIntegerProperty("databaseclient.MaxRetries", 10);
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
	}

	private GetFNPCustomerSomDataResponse getSOMCustomerData(
			GetFNPCustomerSomDataDAO dao, GetFNPCustomerSomDataRequest request, 
			 BksHelper theHelper) throws Exception {
		GetFNPCustomerSomDataResponse resp = new GetFNPCustomerSomDataResponse();
	    String requestType = request.getRequestType();
		FNPInformationHolder infoHolder = new FNPInformationHolder();
		ArrayList<HashMap<String, Object>> custList = getCustomerList(dao, request, requestType, infoHolder, theHelper);
		int maxReturnedCustomer = 1;
		String clientSchemaVersion = request.getClientSomReleaseVersion();
		if (requestType.equals("CID"))
			maxReturnedCustomer = getIntegerProperty("databaseclient.MaxReturnedCustomer", 10);
		if (maxReturnedCustomer<custList.size())
			throw new BksDataException("BKSCOM-ERROR-000029","Too many customers returned for the search criteria. Please use more specific search.");
		for (int j = 0; j < custList.size(); j++) {
			String customerNumber = (String) custList.get(j).get("CUSTOMER_NUMBER");
			ArrayList<HashMap<String, Object>> bundles = null;
			ArrayList<String> sdcNumbers = new ArrayList<String>();
			ArrayList<String> skNumbers = new ArrayList<String>();
			HashMap<String,String> bundleAccount = new HashMap<String, String>();
			if (!requestType.equals("CID")){
				bundles = dao.getBundlesForCustomer(customerNumber);
				getAccountsAndSdcs(dao,bundles,sdcNumbers,skNumbers,bundleAccount);
			}
			CustomerData customerData = getCustomerData(customerNumber,bundleAccount,sdcNumbers,clientSchemaVersion);
			addSkeletonCntToMasterData(skNumbers,customerData,clientSchemaVersion);
			HashMap<String, Order> openOrders = new HashMap<String, Order>();
			ArrayList<ProductBundle> bundleData = new ArrayList<ProductBundle>();
			HashMap<String,String> orderStatus = new HashMap<String, String>();
			if (!requestType.equals("CID")){
				bundleData=populateProductBundlesForCustomer(dao,customerData,bundles,bundleAccount,customerNumber,clientSchemaVersion);
				if (requestType.equals("CPD"))
					openOrders=handleOpenOrders(dao, customerNumber,theHelper,orderStatus,clientSchemaVersion);
			}
			validateMultipleAccounts(customerData);
			populateOutputObject(resp,customerData, bundleData, openOrders,orderStatus);
		}
		return resp;
	}

	private void getAccountsAndSdcs(GetFNPCustomerSomDataDAO dao,
			ArrayList<HashMap<String, Object>> bundles,
			ArrayList<String> sdcNumbers, ArrayList<String> skNumbers, HashMap<String, String> bundleAccount) throws Exception {
		for (int i = 0; bundles!=null && i < bundles.size(); i++) {
			String bundleId = (String)bundles.get(i).get("BUNDLE_ID");
			ArrayList<HashMap<String, Object>> bundleItems = dao.getBundleItems(bundleId);
			boolean bSupportedBundle=false;
			if (bundleItems == null || bundleItems.size() == 0)
				throw new BksDataException("BKSCOM-ERROR-000028","No bundle found for ID "+ bundleId);
			for (int j = 0; j<bundleItems.size(); j++) {
				String serviceCode = (String)bundleItems.get(j).get("SERVICE_CODE");
				String tmpServSubsId = (String)bundleItems.get(j).get("SUPPORTED_OBJECT_ID");
				if (Arrays.asList(supportedServiceCodes).contains(serviceCode) ){
					bSupportedBundle=true;	
					ArrayList<HashMap<String, Object>> contract = dao.getOFContractData(tmpServSubsId);
					String ofSkNumber = null;
					if (contract!=null && contract.size() > 0){
						ofSkNumber = (String) contract.get(0).get("ASSOC_SKELETON_CONTRACT_NUMBER");
						if (ofSkNumber!=null && !skNumbers.contains(ofSkNumber))
							skNumbers.add(ofSkNumber);
					} else {
						ArrayList<HashMap<String, Object>> sdcs = dao.getSDContractData(tmpServSubsId);
						if (sdcs != null && sdcs.size() > 0 && !sdcNumbers.contains(sdcs.get(0).get("CONTRACT_NUMBER").toString()))
							sdcNumbers.add(sdcs.get(0).get("CONTRACT_NUMBER").toString());
					}
				}
			}
			if (!bSupportedBundle){
				log.debug("The bundle "+bundleId+" is not supported. It will be filtered out");
				bundles.remove(i);
				i--;
				continue;
			}

			String accountNo = getAccountNumberForBundle(dao,bundleId);
			if (accountNo!=null)
				bundleAccount.put(bundleId, accountNo);
		}
	}

	private void validateMultipleAccounts(CustomerData customerData) throws BksDataException {
		List<BillingAccount> accountNodes = customerData.getBillingAccount();
		if (accountNodes!=null && accountNodes.size() > 1)
			throw new BksDataException("BKSCOM-ERROR-000025","Customer with more than one billing account.");
	}

	private ArrayList<ProductBundle> populateProductBundlesForCustomer(GetFNPCustomerSomDataDAO dao, CustomerData customerData, 
			ArrayList<HashMap<String, Object>> bundles, HashMap<String, String> bundleAccount, String customerNumber, String clientSchemaVersion) throws Exception {
		final ArrayList<ProductBundle> bundleNodes=new ArrayList<ProductBundle>();
		final Order order = new Order();
		order.getCustomerData().add(customerData);
		ArrayList<Thread> thList = new ArrayList<Thread>();
		int noOfThreads=getIntegerProperty("databaseclient.GetFNPCustomerSomDataParallel", 5);
		final Semaphore s = new Semaphore(noOfThreads);
		for (int i = 0;bundles!=null&& i < bundles.size(); i++) {
			s.acquire();
			final String bundleId = (String) bundles.get(i).get("BUNDLE_ID");
			final String accountNo = bundleAccount.get(bundleId);
			final String customerNo = new String (customerNumber);
			final String clientVersion = (clientSchemaVersion==null)?null:new String (clientSchemaVersion);
			 Thread th = new Thread(){
				public void run(){
					try {
						FetchProductBundleHandler fph = new FetchProductBundleHandler();
						fph.setClientSchemaVersion(clientVersion);
						ProductBundle bundleNode=new ProductBundle();
						bundleNode = fph.getProductBundleInfoById(order,bundleId,accountNo,customerNo);
						synchronized (bundleNodes) {
							bundleNodes.add(bundleNode);
						}
					} catch (BksDataException e) {
						log.warn("The bundle "+bundleId+" caused problem: "+e.getCode()+" "+e.getText());
					} catch (Exception e) {
						log.warn("The bundle "+bundleId+" caused problem: "+e.getMessage());
						e.printStackTrace();
					} finally {
						s.release();
					}
				}
			};
			th.start();	
			thList.add(th);
		}
		for (int i=0;i<thList.size();i++)
			thList.get(i).join();
		return bundleNodes;
	}

	private void addSkeletonCntToMasterData(ArrayList<String> skNumbers, CustomerData customerData, String clientSchemaVersion) throws Exception {
		GetSomMasterDataHandler mdh = new GetSomMasterDataHandler();
		mdh.setClientSchemaVersion(clientSchemaVersion);		
		Order order = new Order();
		order.getCustomerData().add(customerData);
		for (int i = 0; i < skNumbers.size(); i++) {
			mdh.populateSkeletonOnOrder(order, skNumbers.get(i));
		}
	}

	private String getAccountNumberForBundle(GetFNPCustomerSomDataDAO dao,
			String bundleId) throws Exception {
		ArrayList<HashMap<String, Object>> accounts = dao.getAccountsForBundle(bundleId);
		if (accounts!=null && accounts.size()>0)
			return (String) accounts.get(0).get("ACCOUNT_NUMBER");
		return null;
	}

	private CustomerData getCustomerData(String customerNumber, HashMap<String, String> bundleAccount, ArrayList<String> sdcNumbers, String clientSchemaVersion) throws Exception{
		ArrayList<String> accountNums = null;
		if (bundleAccount!=null && bundleAccount.size()>0){
			accountNums = new ArrayList<String>();
			for (String value : bundleAccount.values()) {
			    if (!accountNums.contains(value))
			    	accountNums.add(value);
			}
			
		}
		GetSomMasterDataHandler mdh = new GetSomMasterDataHandler();
		mdh.setClientSchemaVersion(clientSchemaVersion);
		Order order = null;
		   order = mdh.getOrder(customerNumber,null,accountNums,sdcNumbers,true,false);
	   return order.getCustomerData().get(0);
	}
	
	private ArrayList<HashMap<String, Object>> getCustomerByAccessNumber(
			GetFNPCustomerSomDataDAO dao, ComplexPhoneNumber accnum) throws Exception {
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
		return custList;
	}

	ArrayList<HashMap<String, Object>> getCustomerList(GetFNPCustomerSomDataDAO dao,GetFNPCustomerSomDataRequest request, String requestType, FNPInformationHolder infoHolder, BksHelper theHelper) throws Exception{
		ArrayList<HashMap<String, Object>> custList = null;
		String barcode = request.getBarcode();
		PersonDataQueryInput person = request.getPersonData();
		CompanyDataQueryInput company = request.getCompanyData();
		ComplexPhoneNumber accnum = request.getAccessNumber();
		if (accnum != null) {
			custList = getCustomerByAccessNumber(dao,accnum);
			if (custList!=null && custList.size() > 1)
				throw new BksDataException("BKSCOM-ERROR-000007","Found access number more than one time.");
			
			if (custList!=null && custList.size() > 0) 
			    validateCustomerAttributes(custList.get(0),request.getCustomerNumber(),person,theHelper);
		}else if (request.getCustomerNumber() != null) {
			custList = dao.getCustomerForCustNumber(request.getCustomerNumber());
		}else if (barcode != null) {
			custList = dao.getCustomerForBarCode(barcode);
		}else if (person != null) {
			if (request.getRequestType().equals("CPD"))
				throw new BksDataException("BKSCOM-ERROR-000008","The input parameter (person data) does not match request type CPD.");
			custList = getCustomerByPersonData(dao,person, infoHolder, requestType);
		}else if (company != null){
			if (request.getRequestType().equals("CPD"))
				throw new BksDataException("BKSCOM-ERROR-000008","The input parameter (company data) does not match request type CPD.");
			custList = getCustomerByCompanyData(dao,company, infoHolder,requestType);
		}else 
			throw new BksDataException("BKSCOM-ERROR-000002","Wrong input setting");  

		removeDuplicateCustomers(custList);
		if (!requestType.equals("COM"))
			validateProviderCustomer(custList);
		return custList;
	}

	private void validateProviderCustomer(ArrayList<HashMap<String, Object>> custList) throws BksDataException {
		for (int i = 0; custList!=null && i < custList.size(); i++) {
			String group = (String) custList.get(i).get("CUSTOMER_GROUP_RD");
			if (!group.equals("03") && !group.equals("81")
					&& !group.equals("84")) {
				if (custList.size()==1){
					String errorText = "No customer found";
					String errorCode = "BKSCOM-ERROR-000002";
					throw new BksDataException(errorCode,errorText);
				} else {
					custList.remove(i);
					i--;
					continue;
				}
			}
		}
	}

	private void removeDuplicateCustomers(ArrayList<HashMap<String, Object>> custList) throws BksDataException {
		ArrayList<String> custNumberList = new ArrayList<String>();
		for (int i = 0; custList!=null && i < custList.size(); i++) {
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
					throw new BksDataException("BKSCOM-ERROR-000021","Arcor Employee");
				} else {
					custList.remove(i);
					i--;
					continue;
				}
			}

			String classification = (String) custList.get(i).get("CLASSIFICATION_RD");
			if (classification.startsWith("R") || classification.startsWith("Q") || 
					(classification.startsWith("A") || classification.startsWith("B"))) {
				if (custList.size()==1){
					String errorText = "Non supported customer classification";
					String errorCode = "BKSCOM-ERROR-000022";
					throw new BksDataException(errorCode,errorText);
				} else {
					custList.remove(i);
					i--;
					continue;
				}
			}
		}

		if ((custList == null) || (custList.size() < 1)) 
			throw new BksDataException("BKSCOM-ERROR-000002","No customer found");
	}

	private void validateCustomerAttributes(HashMap<String, Object> customer, String reqCustNum, 
			PersonDataQueryInput person, BksHelper theHelper) throws Exception {
		String custNo = (String) customer.get("CUSTOMER_NUMBER");
		Object bd = customer.get("BIRTH_DATE");
		String encryptedpw = (String) customer.get("USER_PASSWORD");
		String encryptKey = (String) customer.get("ENCRYPTION_KEY");
		theHelper.setCustomerEncKey(encryptKey);
		String password = theHelper.decryptCustPasswd(encryptedpw);
		
		if (reqCustNum != null && !reqCustNum.equals(custNo)){
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

	private ArrayList<HashMap<String, Object>> getCustomerByCompanyData(
			GetFNPCustomerSomDataDAO dao, CompanyDataQueryInput company, FNPInformationHolder infoHolder, String requestType) throws Exception {
		ArrayList<HashMap<String, Object>> custList = null;
		String name = company.getCompanyName();
		String zipCode = company.getZipCode();
		String street = company.getStreet();
		String streetNo = company.getHousenumber();
		String streetNoSuf = company.getHousenumberSuffix();
		String orgSuf = company.getCompanySuffixName();
		String regNo = company.getCommercialRegisterNumber();
		CalendarDate dateOfBirth = null;
		if (company.getContact() != null)
			dateOfBirth = company.getContact().getDateOfBirth();

		if (name != null) {
			try {
					custList = dao.getCustomerCompanyData(name, zipCode,
						street, streetNo,streetNoSuf,orgSuf,regNo);
			} catch (UncategorizedSQLException e) {
				if (e.getSQLException().getErrorCode() == 1013)
					throw new BksDataException("BKSCOM-ERROR-000008","Query Timed out.");
				throw e;
			}
		}
		if (custList!= null && custList.size() > 0) {
			validateCompanyData(dao,custList,dateOfBirth,regNo,requestType,infoHolder);
		}
		return custList;
	}

	private void validateCompanyData(GetFNPCustomerSomDataDAO dao, ArrayList<HashMap<String, Object>> custList, CalendarDate dateOfBirth, String regNo, String requestType, FNPInformationHolder infoHolder) throws Exception {
		if (dateOfBirth != null){
			validateSohoCustomer(dao, custList, dateOfBirth, infoHolder);
		} else if (regNo != null) {
			validateRegisteredCompany(custList, regNo, infoHolder);
		} else {
			infoHolder.setCategory("BUSINESS");
			String corpType = (String)custList.get(0).get("INCORPORATION_TYPE_RD");
			if (corpType != null && !corpType.equals("UNREGISTERED") && 
				!corpType.equals("NOT")	&& !requestType.equals("CID")){
				throw new BksDataException("BKSCOM-ERROR-000005","For corp. type "+corpType+" registration number must be provided.");
			}
		}
	}

	private void validateRegisteredCompany(ArrayList<HashMap<String, Object>> custList, String regNo,  FNPInformationHolder infoHolder) throws BksDataException {
		infoHolder.setCategory("BUSINESS");
		for (int i = 0; i < custList.size(); i++) {
			String register = (String) custList.get(i).get("INCORPORATION_NUMBER_ID");
			if (!regNo.equals(register)) {
				if (custList.size() == 1) {
					throw new BksDataException("BKSCOM-ERROR-000005","Provided registration number does not match");
				} else {
					custList.remove(i);
					i--;
					continue;
				}
			}
		}
	}

	private void validateSohoCustomer(GetFNPCustomerSomDataDAO dao, ArrayList<HashMap<String, Object>> custList, 
			CalendarDate dateOfBirth, FNPInformationHolder infoHolder) throws Exception {
		String customerNo = custList.get(0).get("CUSTOMER_NUMBER").toString();
		ArrayList<HashMap<String, Object>> contacts = dao.getOwnerContact(customerNo);
		if (contacts!= null && contacts.size() > 0 ){
			for (int i = 0; i < custList.size(); i++) {
				Object bd = contacts.get(i).get("BIRTH_DATE");
				if (bd != null && !dateOfBirth.equals(CalendarDate.from(bd.toString(), "yyyy-MM-dd"))) {
					if (custList.size() == 1) {
						throw new BksDataException("BKSCOM-ERROR-000005","Provided birth date does not match");
					} else {
						custList.remove(i);
						i--;
						continue;
					}
				}
			}
		}
		infoHolder.setCategory("SOHO");
	}

	private ArrayList<HashMap<String, Object>> getCustomerByPersonData(
			GetFNPCustomerSomDataDAO dao, PersonDataQueryInput person, 
			FNPInformationHolder infoHolder, String requestType) throws Exception {
		ArrayList<HashMap<String, Object>> custList = null;
		String familyName = person.getFamilyName();
		String zipCode = person.getZipCode();
		String street = person.getStreet();
		String streetNo = person.getHousenumber();
		String streetNoSuf = person.getHousenumberSuffix();
		String firstName = person.getFirstName();
		
		CalendarDate dateOfBirth = person.getDateOfBirth();
		infoHolder.setCategory("RESIDENTIAL");

		if (familyName == null) 
			throw new BksDataException("BKSCOM-ERROR-000008","Missing mandatory input (family name).");
		 else if (street == null && !requestType.equals("ICP") && !requestType.equals("CID")) 
			throw new BksDataException("BKSCOM-ERROR-000008","Missing mandatory input (street name).");
		 else {
			if (familyName != null)
				familyName = familyName.replaceAll( "\\*", "%");
			if (street != null)
				street = street.replaceAll( "\\*", "%");
		}
		if (familyName != null) {
			try {
				custList = dao.getCustomerPersonData(familyName, zipCode,
						street,streetNo,streetNoSuf, firstName, dateOfBirth);
			} catch (UncategorizedSQLException e) {
				if (e.getSQLException().getErrorCode() == 1013){
					throw new BksDataException("BKSCOM-ERROR-000008","Query Timed out.");
				}
				throw e;
			}
		}
		return custList;
	}


	private HashMap<String,Order> handleOpenOrders(GetFNPCustomerSomDataDAO dao,String customerNumber, BksHelper theHelper, HashMap<String,String> orderStatus, String clientSchemaVersion) throws Exception {
		HashMap<String,Order> openOrderNodes = new HashMap<String, Order>();
		ArrayList<HashMap<String, Object>> openOrders = dao.getExternalOrders(customerNumber);
		ArrayList<String> orderIdList = new ArrayList<String>();
		for (int i = 0;openOrders!=null && i < openOrders.size(); i++) {
			String orderId = (String) openOrders.get(i).get("ORDER_ID");
			BigDecimal orderPosId = (BigDecimal) openOrders.get(i).get("ORDER_POSITION_NUMBER");
			getOpenOrdersForOrderId(orderId, orderPosId,openOrderNodes,i,clientSchemaVersion);
			String status = (String) openOrders.get(i).get("STATE_RD");
			orderStatus.put(orderId, status );
			orderIdList.add(orderId);
		}
		return openOrderNodes;
	}

	private void getOpenOrdersForOrderId(String orderId, BigDecimal orderPosNo, HashMap<String,Order> openOrderNodes, int baseSuffix, String clientSchemaVersion) throws Exception {
		Order openOrder = getOpenOrders(orderId,orderPosNo,baseSuffix,clientSchemaVersion);

		if (openOrder!=null && 0 < openOrder.getOrderPosition().size() && openOrder.getOrderPosition().get(0) != null) {
			OrderPositionType opt = openOrder.getOrderPosition().get(0).getValue();
			if (opt instanceof LineChange || opt instanceof Termination ||
					opt instanceof Relocation || opt instanceof ProviderChange ||
					opt instanceof LineCreation){
				if (openOrderNodes.get(orderId)==null)
					openOrderNodes.put(orderId,new Order());
				openOrderNodes.get(orderId).getOrderPosition().add(openOrder.getOrderPosition().get(0));
				openOrderNodes.get(orderId).setBarcode(openOrder.getBarcode());
			}
		} 
	}


	private Order getOpenOrders(String orderId,BigDecimal orderPosId, int baseSuffix, String clientSchemaVersion) throws Exception{
		ApplicationContext ac = DatabaseClient.getAc();
		if (ac == null)
			throw new MCFException("Application context has not been initialized.");
		Object order = null;
		int timeToLive = getIntegerProperty("servicebusclient.TimeToLive",10000);
		int timeOut = getIntegerProperty("servicebusclient.TimeOut",100000);
		try {
			BksServiceHandler bksHandler = (BksServiceHandler)ac.getBean("bksService");
			messageSender = bksHandler.getMessageSender();
			ServiceRequestSoap<GetOrderDetailsRequest> serviceRequest = populateSrviceRequest(orderId, orderPosId, timeToLive,clientSchemaVersion);
			ServiceResponse<GetOrderDetailsResponse> respServ = messageSender.sendSyncSoapRequest(serviceRequest, timeOut);
			String text = serializeComResponse(respServ);
			XmlProcessor xmlProcessor = (XmlProcessor) ac.getBean("mcf2XmlProcessor");
			text = makeNodeIdsUnique(text,baseSuffix);
			Object order1 = xmlProcessor.deserialize(text, "schema/som-order.xsd", "net.arcor.orderschema");
			if (order1!=null)
				order = ((JAXBElement<?>)order1).getValue();
		} catch (Exception e) {
			log.error("Call to Com failed. Message: "+e.getMessage());
			e.printStackTrace();
		}
		return (Order)order;
	}
	
	private String serializeComResponse(ServiceResponse<GetOrderDetailsResponse> respServ) throws TransformerException {
		GetOrderDetailsResponse resp = respServ.getPayload();
		Node xml = (Node)resp.getOrderDetails().getAny();
		TransformerFactory transFactory = TransformerFactory.newInstance();
		Transformer transformer = transFactory.newTransformer();
		StringWriter buffer = new StringWriter();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.transform(new DOMSource(xml),new StreamResult(buffer));
		String text = buffer.toString();
		return text;
	}

	private ServiceRequestSoap<GetOrderDetailsRequest> populateSrviceRequest(String orderId, BigDecimal orderPosId, long timeToLive, String clientSchemaVersion){
		GetOrderDetailsRequest request = new GetOrderDetailsRequest();
		request.setOrderID(orderId);
		request.setOrderPositionNumber(orderPosId);
		request.setOrderOutputFormat(OrderOutputFormat.SOM);
		request.setReturnUnenrichedOrder(true);
		if (clientSchemaVersion!=null && !clientSchemaVersion.isEmpty())
			request.setSomVersion(clientSchemaVersion);
		
		ServiceRequestSoap<GetOrderDetailsRequest> serviceRequest =
			new ServiceRequestSoap<GetOrderDetailsRequest>(request,
					"classpath:schema/COM-PROV-001.xsd",
			"de.vodafone.esb.service.sbus.com.com_prov_001");
		serviceRequest.setOperationName("/COM-001/GetOrderDetails");		
		serviceRequest.setTimeToLive(timeToLive);
		return serviceRequest;

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
	
	private String makeNodeIdsUnique(String text, int baseSuffix) {
		int index = text.indexOf("orderPositionNumber=\"",0);
		int suffix=0;
		while ((index=text.indexOf(" ID=\"",index)) > 0){
			index = index+" ID=\"".length();
			int endIndex = text.indexOf("\"",index);
			String source = text.substring(index,endIndex);
			
			String replace;
			replace = "UNI_" +baseSuffix+"_" + suffix;
			text = text.replaceAll("\""+source+"\"", "\""+replace+"\"");
			text = text.replaceAll(">"+source+"<", ">"+replace+"<");
			index = endIndex +1 ;
			suffix++;
		}
		return text;
	}

	private void populateOutputObject(GetFNPCustomerSomDataResponse resp,
			CustomerData custData,ArrayList<ProductBundle> bundleData, HashMap<String,Order> openOrders, HashMap<String,String> orderStatus) throws Exception {
		if (custData != null)
			resp.getCustomerData().add(custData);
		resp.getProductBundleData().addAll(bundleData);
		if (openOrders != null) {
			Set<String> keys = openOrders.keySet();
			Iterator<String> keyiter = keys.iterator();
			while (keyiter.hasNext()){
				String orderId = keyiter.next();
				Order value = openOrders.get(orderId);
				String barcode = value.getBarcode();
				for (int i = 0; i < value.getOrderPosition().size(); i++) {
					OrderPositionType opt = value.getOrderPosition().get(i).getValue();
					String status = BksHelper.getExternalstatemap().get(orderStatus.get(orderId));
					populateOpenOrderOnResponse(resp,opt,orderId,barcode,status);
				}
			}
		}
	}

	private void populateOpenOrderOnResponse(GetFNPCustomerSomDataResponse resp, OrderPositionType opt, String orderId, String barcode, String status) {
		if (opt instanceof LineChange) {
			LineChangePacket packet = new LineChangePacket();
			packet.setOrderID(orderId);
			packet.setBarcode(barcode);
			packet.setStatus(status);
			packet.setLineChange((LineChange) opt);
			resp.getLineChange().add(packet);
		} else if (opt instanceof Termination){
			TerminationPacket packet = new TerminationPacket();
			packet.setOrderID(orderId);
			packet.setBarcode(barcode);
			packet.setStatus(status);
			packet.setTermination((Termination)opt);
			resp.getTermination().add(packet);
		} else if (opt instanceof LineCreation){
			LineCreationPacket packet = new LineCreationPacket();
			packet.setOrderID(orderId);
			packet.setBarcode(barcode);
			packet.setStatus(status);
			packet.setLineCreation((LineCreation)opt);
			resp.getLineCreation().add(packet);
		} else if (opt instanceof Relocation){
			RelocationPacket packet = new RelocationPacket();
			packet.setOrderID(orderId);
			packet.setBarcode(barcode);
			packet.setStatus(status);
			packet.setRelocation((Relocation)opt);
			resp.getRelocation().add(packet);
		} else if (opt instanceof ProviderChange){
			ContractPartnerChangePacket packet = new ContractPartnerChangePacket();
			packet.setOrderID(orderId);
			packet.setBarcode(barcode);
			packet.setStatus(status);
			packet.setContractPartnerChange((ProviderChange)opt);
			resp.getContractPartnerChange().add(packet);
		}
	}
}
