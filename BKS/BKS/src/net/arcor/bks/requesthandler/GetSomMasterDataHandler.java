package net.arcor.bks.requesthandler;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.Map;

import net.arcor.bks.client.DatabaseClient;
import net.arcor.bks.common.BksDataException;
import net.arcor.bks.common.BksHelper;
import net.arcor.bks.common.BksRefDataCacheHandler;
import net.arcor.bks.db.GetSomMasterDataDAO;
import net.arcor.epsm_basetypes_001.InputData;
import net.arcor.orderschema.AssociatedObjectReference;
import net.arcor.orderschema.BillingAccount;
import net.arcor.orderschema.CcbIdAny;
import net.arcor.orderschema.CcbIdR;
import net.arcor.orderschema.ContactRole;
import net.arcor.orderschema.ContactRoleRefIdList;
import net.arcor.orderschema.Customer;
import net.arcor.orderschema.ExistConfMandate;
import net.arcor.orderschema.ExistConfRefIdWithListAction;
import net.arcor.orderschema.ExistConfString1;
import net.arcor.orderschema.ExistConfString16;
import net.arcor.orderschema.ExistConfString2;
import net.arcor.orderschema.ExistConfTariffCodeList;
import net.arcor.orderschema.InvoiceDelivery;
import net.arcor.orderschema.NonTypedCcbId;
import net.arcor.orderschema.OneGroup;
import net.arcor.orderschema.Order;
import net.arcor.orderschema.SalesOrganisation;
import net.arcor.orderschema.SalesOrganisationNumber;
import net.arcor.orderschema.ServiceDeliveryContract;
import net.arcor.orderschema.SkeletonContract;
import net.arcor.orderschema.SkeletonContractItem;
import de.arcor.aaw.kernAAW.bks.services.GetSomMasterDataInput;
import de.arcor.aaw.kernAAW.bks.services.GetSomMasterDataOutput;
import de.vodafone_arcor.linkdb.epsm_whsvf_commontypes_001.ComplexPhoneNumber;
import de.vodafone_arcor.linkdb.epsm_whsvf_commontypes_001.PhoneNumbers;
import edu.emory.mathcs.backport.java.util.Arrays;

public class GetSomMasterDataHandler extends BaseServiceHandler {
	
	
	
	private String invDeliveryType;
	private String itemizedType;
	private String dialInAccountName = null;
	final static String[] validStatus = {"NEW","IN_PROGRESS","FAILED","SUCCESS"};

	public GetSomMasterDataHandler() {
		super();
	}

	
	public String execute() throws Exception {
		String returnXml = null;
		String accsessNumber = null;
		String clientSomReleaseVersion =  ((GetSomMasterDataInput)input).getClientSomReleaseVersion();
        if(clientSomReleaseVersion!=null && !clientSomReleaseVersion.trim().isEmpty())
             setClientSchemaVersion(clientSomReleaseVersion);
        
        GetSomMasterDataDAO dao  = (GetSomMasterDataDAO)DatabaseClient.getBksDataAccessObject(null,"GetSomMasterDataDAO");
		
        try {
			if(customerNumber==null){
				
	        	
	        	dialInAccountName = ((GetSomMasterDataInput)input).getDialInAccountName();
	        	String phoneAccessNumber = null;
	        	if(dialInAccountName==null){
					de.vodafone.esb.schema.common.commontypes_esb_001.ComplexPhoneNumber c = ((GetSomMasterDataInput)input).getAccessNumber();
					String countryCode = c.getCountryCode();
					String localAreaCode = c.getLocalAreaCode();
					de.vodafone.esb.schema.common.commontypes_esb_001.PhoneNumbers p=c.getPhoneNumbers();
					ArrayList<String> phoneNumberList= (ArrayList<String>) p.getPhoneNumber();
					String phoneNumber = null;
					if(!phoneNumberList.isEmpty())
					phoneNumber = phoneNumberList.get(0);
					phoneAccessNumber = countryCode+localAreaCode+phoneNumber;
				}
				
				if(dialInAccountName!=null)
					accsessNumber = dialInAccountName;
				else
					accsessNumber = phoneAccessNumber;
				
			
				
				
				
			}
	        
			customerNumber = populatedCustomerNUmber(accsessNumber,dao);
			List<String> accountNumbers = ((GetSomMasterDataInput)input).getKontonummer();
			List<String> contractNumbers = ((GetSomMasterDataInput)input).getVertragsnummer();
			Order order = getCustomerData(dao,customerNumber,null,accountNumbers,contractNumbers,false,false);
			returnXml = populateOutputObject(order);
		} catch (BksDataException e) {
			logger.error(e.getMessage());
			serviceStatus = Status.PRECONDITION_ERROR;
			errorCode = e.getCode();
			errorText = e.getText();
			returnXml = populateOutputObject(null);
		} catch (Exception e) {
			logger.error(e.getMessage());
			serviceStatus = Status.PRECONDITION_ERROR;
			errorCode = "BKS0008";
			if (e.getMessage() != null && e.getMessage().length() > 255)
				errorText = e.getMessage().substring(0, 255);
			else
				errorText = e.getMessage();
			returnXml = populateOutputObject(null);
		}
		finally {
			dao.closeConnection();
		}
		return returnXml;
	}
	
	private String populatedCustomerNUmber(String accessNumber, GetSomMasterDataDAO dao) throws Exception{
		
		 
			if(dialInAccountName!=null){
				ArrayList<HashMap<String, Object>> dialInAccountCustomer = null;
				
					dialInAccountCustomer = dao.getCustomerNumber(accessNumber);
					if(dialInAccountCustomer.isEmpty())
						throw new BksDataException("BKS0000","No Customer Found For"+" "+dialInAccountName+" "+ "dialInAccountName");
						
					else
						customerNumber = (String)dialInAccountCustomer.get(0).get("CUSTOMER_NUMBER");
					
			}
			
			else{
			
				ArrayList<HashMap<String,Object>> accessNumberCustomer = null;
				accessNumberCustomer = dao.getCustomerNumber(accessNumber);
				if(!accessNumberCustomer.isEmpty())	
					customerNumber = (String)accessNumberCustomer.get(0).get("CUSTOMER_NUMBER");
				
				else{
				accessNumberCustomer = dao.getAccessNumber(accessNumber);
					if(!accessNumberCustomer.isEmpty()){
						customerNumber = (String)accessNumberCustomer.get(0).get("CUSTOMER_NUMBER");
					}
				}
			
					if(customerNumber==null)
						throw new BksDataException("BKS0000","No Customer Found For"+" "+accessNumber+" "+ "Access_Number");
					
			}
			return customerNumber;
	}
	
	
	
	Order getOrder(String custNo, String resellerId, List<String> accountNumbers, List<String> contractNumbers
			, boolean bFetchProductInfo, boolean bFakeInstallCr) throws Exception {
		Order order = null;
		GetSomMasterDataDAO dao = 
			(GetSomMasterDataDAO)DatabaseClient.getBksDataAccessObject(null,"GetSomMasterDataDAO");

		try {
			customerNumber = custNo;
			order = getCustomerData(dao,custNo,resellerId,accountNumbers,contractNumbers,bFetchProductInfo,bFakeInstallCr);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			throw e;
		}
		finally {
			dao.closeConnection();
		}
		return order;
	}

	Customer getCustomerNode(String custNo) throws Exception {
		Customer customerNode = null;
		GetSomMasterDataDAO dao = 
			(GetSomMasterDataDAO)DatabaseClient.getBksDataAccessObject(null,"GetSomMasterDataDAO");

		try {
			customerNumber = custNo;
			ArrayList<HashMap<String, Object>> customer = dao.getCustomerInfo(customerNumber);
			if (customer.size() > 0){
				String encryptKey = (String) customer.get(0).get("ENCRYPTION_KEY");
				theHelper.setCustomerEncKey(encryptKey);
				HashMap<String,Object[]> colPathMap = 
					BksRefDataCacheHandler.getCcbSomMap().get("CUSTOMER;CUSTOMER");

				ArrayList<HashMap<String, Object>> rootCust = dao.getRootCustomer(customerNumber);
				customer.get(0).putAll(rootCust.get(0));
				theHelper.setDesiredNewElement(-1);
				Order order =  new Order();
				populateOrderNode(colPathMap,customer.get(0),order);
				order.getCustomerData().get(0).getCustomer().setID("C"+customerNumber);
				String entityId = customer.get(0).get("ENTITY_ID").toString();
				populateEntity(dao,entityId,"CUSTOMER",order,customerNumber);
				customerNode = order.getCustomerData().get(0).getCustomer();
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			throw e;
		}
		finally {
			dao.closeConnection();
		}
		return customerNode;
	}
	void populateSkeletonOnOrder(Order order,String contractNumber) throws Exception {
		if (isSkeletonNodeAlreadyCreated(order,contractNumber))
			return;
		GetSomMasterDataDAO dao = 
			(GetSomMasterDataDAO)DatabaseClient.getBksDataAccessObject(null,"GetSomMasterDataDAO");

		try {
			ArrayList<HashMap<String, Object>> skContract = dao.getSkeletonContractByNumber(contractNumber);
			if (skContract!=null&&skContract.size()>0) {
				HashMap<String,Object[]> colPathMap = BksRefDataCacheHandler.getCcbSomMap().get("SKELETON_CONTRACT;SKELETON_CONTRACT");
				theHelper.setDesiredNewElement(1);
				populateOrderNode(colPathMap,skContract.get(0),order);
				int i = order.getCustomerData().get(0).getSkeletonContract().size() - 1;
				SkeletonContract skNode = order.getCustomerData().get(0).getSkeletonContract().get(i);
				skNode.setID("SK"+contractNumber);
				List<ContactRole> contactRole = order.getCustomerData().get(0).getContactRole();
				skNode.setContactRoleRefIdList(new ContactRoleRefIdList());
				associateRelevantContactIds(skNode.getID(),contactRole,skNode.getContactRoleRefIdList().getContactRoleRef());
				populateSkeletonItems(dao,contractNumber,order.getCustomerData().get(0).getSkeletonContract().get(i));
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			throw e;
		}
		finally {
			dao.closeConnection();
		}
		return;
	}

	void addAccountToCustomerData(Order order,String accountNumber) throws Exception {
		ArrayList<String> accountNumbers = new ArrayList<String>();
		accountNumbers.add(accountNumber);
		GetSomMasterDataDAO dao = null; 
		try {
			dao = (GetSomMasterDataDAO)DatabaseClient.getBksDataAccessObject(null,"GetSomMasterDataDAO");
			getAccountInfo(dao,accountNumbers,order);
		} finally {
			if (dao != null)
				dao.closeConnection();
		}
	}

	private boolean isSkeletonNodeAlreadyCreated(Order order,
			String contractNumber) {
		List<SkeletonContract> skeletonList = order.getCustomerData().get(0).getSkeletonContract();
		for (int i=0;skeletonList!=null&&i<skeletonList.size();i++){
			if (skeletonList.get(i).getCcbId().getExisting().equals(contractNumber))
				return true;
		}
		return false;
	}

	protected String populateOutputObject(Order order) throws Exception {
		String returnXml = null;
		output = new GetSomMasterDataOutput();
		if (serviceStatus != Status.SUCCESS){
			output.setResult(false);
			output.setErrorCode(errorCode);
			output.setErrorText(errorText);
		}else {
			output.setResult(true);
 			((GetSomMasterDataOutput)output).setOrder(order);
		}
		try {
			returnXml = theHelper.serializeForMcf(output, customerNumber,accessNumber,"de.arcor.aaw.kernAAW.bks.services" ,"GetSomMasterData");
		} catch (Exception e) {
			unvalidatedXml = theHelper.serialize(output, "de.arcor.aaw.kernAAW.bks.services", null, "GetSomMasterData");
			throw e;
		}
		return returnXml;
	}

	private Order getCustomerData(GetSomMasterDataDAO dao,String customerNumber, String resellerId, 
			List<String> accountNumbers, List<String> contractNumbers, 
			boolean bFetchProductInfo, boolean bFakeInstallCr) throws Exception {
		Order order = new Order();
		getCustomerInfo(dao,customerNumber,order,bFakeInstallCr);
		Customer cust = order.getCustomerData().get(0).getCustomer();
		if (resellerId!=null&&cust!=null&& cust.getProviderCode()!=null&&
				!resellerId.equals(cust.getProviderCode().getExisting()))
			throw new BksDataException("BKS0051", "Customer does not belong to reseller. ");
		if (accountNumbers==null)
			accountNumbers = new ArrayList<String>();
		if (accountNumbers.size() == 0){
			ArrayList<HashMap<String, Object>> custAccounts = dao.getAccountsForCustomer(customerNumber);
			if (custAccounts==null || 0 == custAccounts.size() && bFetchProductInfo) 
				throw new BksDataException("BKS0022", "No account found for customer "+customerNumber);
			for (int i = 0;custAccounts!=null && i < custAccounts.size(); i++) {
				accountNumbers.add(custAccounts.get(i).get("ACCOUNT_NUMBER").toString());
			}
		}

		getAccountInfo(dao,accountNumbers,order);
		getServiceDeliveryContract(dao,customerNumber,contractNumbers,order);
		getOneGroup(dao, customerNumber, order);
		populateSalesOrgNumber(order);
 		return order;
	}

	private void populateSalesOrgNumber(Order order) {
		SalesOrganisation salesOrg = new SalesOrganisation();
		SalesOrganisationNumber salesOrgNum = new SalesOrganisationNumber();
		salesOrgNum.setContent("0");
		salesOrgNum.setType("A");
		salesOrg.getSalesOrganisationNumber().add(salesOrgNum);
		order.setSalesOrganisation(salesOrg);
	}
	private void getServiceDeliveryContract(GetSomMasterDataDAO dao, String customerNumber,
			List<String> contractNumbers, Order order) throws Exception {
		for (int i=0;contractNumbers!=null&&i<contractNumbers.size();i++){
			String contractNo= contractNumbers.get(i);
		    ArrayList<HashMap<String, Object>> contract = dao.getServiceDeliveryContract(contractNo);
		    if (contract == null || contract.size() == 0)
				throw new BksDataException("BKS0019","No contract found "+contractNo);

		    HashMap<String,Object[]> colPathMap = BksRefDataCacheHandler.getCcbSomMap().get("SERVICE_DELIVERY_CONTRACT;SERVICE_DELIVERY_CONTRACT");
			theHelper.setDesiredNewElement(1);
			populateOrderNode(colPathMap,contract.get(0),order);
			String skeletonNumber = (String)contract.get(0).get("ASSOC_SKELETON_CONTRACT_NUMBER");
			ServiceDeliveryContract sdcNode = order.getCustomerData().get(0).getServiceDeliveryContract().get(i);
			sdcNode.setID("SDC"+contractNo);
			sdcNode.setOwnerNodeRefID("C"+customerNumber);
			List<ContactRole> contactRole = order.getCustomerData().get(0).getContactRole();
			sdcNode.setContactRoleRefIdList(new ContactRoleRefIdList());
			associateRelevantContactIds(sdcNode.getID(),contactRole,sdcNode.getContactRoleRefIdList().getContactRoleRef());
			if (skeletonNumber!=null){
				sdcNode.setSkeletonContractNodeRefID("SK"+skeletonNumber);
				populateSkeletonOnOrder(order, skeletonNumber);
			}
		}	
	}

	private void getOneGroup(GetSomMasterDataDAO dao, String customerNumber, Order order) throws Exception {
		ArrayList<HashMap<String, Object>> oneGroups = dao.getOneGroup(customerNumber);
		if (oneGroups == null || oneGroups.size() == 0)
			return;

		for (int i = 0; i < oneGroups.size(); i++) {
			HashMap<String, Object[]> colPathMap = BksRefDataCacheHandler.getCcbSomMap().get("ONE_GROUP;ONE_GROUP");
			theHelper.setDesiredNewElement(1);
			String nodeId = (String)oneGroups.get(i).get("ONE_GROUP_ID");
			populateOrderNode(colPathMap, oneGroups.get(i), order);
			OneGroup ogNode = order.getCustomerData().get(0).getOneGroup().get(i);
			ogNode.setID("OG" + nodeId);
			List<ContactRole> contactRole = order.getCustomerData().get(0).getContactRole();
			ogNode.setContactRoleRefIdList(new ContactRoleRefIdList());
			associateRelevantContactIds(ogNode.getID(), contactRole, ogNode.getContactRoleRefIdList().getContactRoleRef());
		}

	}

	private void getAccountInfo(GetSomMasterDataDAO dao,
			List<String> accountNumbers, Order order) throws Exception {
		for (int i=0;i<accountNumbers.size();i++){
			String accountNo= accountNumbers.get(i);
			ArrayList<HashMap<String, Object>> accountData = dao.getAccountData(accountNo);
			if (accountData==null || 0 == accountData.size()) 
				throw new BksDataException("BKS0022", "No account found for account number"+accountNo);
			if (accountData!=null && accountData.size() > 0){
				HashMap<String,Object[]> colPathMap = BksRefDataCacheHandler.getCcbSomMap().get("ACCOUNT;ACCOUNT");
				theHelper.setDesiredNewElement(1);
				populateOrderNode(colPathMap,accountData.get(0),order);
				ExistConfString1 value = (ExistConfString1) 
						theHelper.setAttributeForPath(order, "get","CustomerData.BillingAccount.DunningStatus", null);
				if (value!=null && value.getExisting()!=null && !value.getExisting().equals("0"))
					theHelper.setAttributeForPath(order, "set", "CustomerData.BillingAccount.Balance", 
							theHelper.getAccountBalance(accountNo));
				BillingAccount accountNode = order.getCustomerData().get(0).getBillingAccount().get(i);
				accountNode.setID("AC"+accountNo);
				String accountOwner = (String) accountData.get(0).get("CUSTOMER_NUMBER");
				if (customerNumber!=null && customerNumber.equals(accountOwner))
					accountNode.setOwnerNodeRefID("C"+customerNumber);
				ExistConfMandate mandate = accountNode.getMethodOfPayment().getMandate();
				if (mandate!=null&&(mandate.getExisting().isRecurringIndicator()==null ||
						mandate.getExisting().getSignatureDate()==null))
					accountNode.getMethodOfPayment().setMandate(null);
				List<ContactRole> contactRole = order.getCustomerData().get(0).getContactRole();
				accountNode.setContactRoleRefIdList(new ContactRoleRefIdList());
				associateRelevantContactIds(accountNode.getID(),contactRole,accountNode.getContactRoleRefIdList().getContactRoleRef());
				String bankAccId = (String)accountData.get(0).get("BANK_ACCOUNT_ID");
				if (bankAccId != null){
					ArrayList<HashMap<String, Object>> bankData = dao.getBankData(bankAccId,customerNumber);
					if (bankData!=null && bankData.size() > 0)
						populateOrderNode(colPathMap,bankData.get(0),order);
				}
				String mailingId = (String)accountData.get(0).get("MAILING_ID");
				if (mailingId != null)
					populateDocumentRecipient(dao,mailingId,accountNo,order);
			}
	        invDeliveryType = "LETTER";
        	itemizedType="none";
			ArrayList<HashMap<String, Object>> docPattern = dao.getDocumentPattern(accountNo);
			for (int j = 0;docPattern!=null&& j < docPattern.size(); j++) {
				String documentTempl = (String)docPattern.get(j).get("DOCUMENT_TEMPLATE_NAME");
				if (getInvoiceDelivery(documentTempl))
					break;
			}
			InvoiceDelivery invDeliv = 
				order.getCustomerData().get(0).getBillingAccount().get(i).getInvoiceDelivery();
			if (invDeliv != null) {
				invDeliv.setInvoiceDeliveryType(new ExistConfString16());
				invDeliv.getInvoiceDeliveryType().setExisting(invDeliveryType);
				invDeliv.setEnableItemizedBill(new ExistConfString16());
				invDeliv.getEnableItemizedBill().setExisting(itemizedType);
			}
		}
	}

	private boolean getInvoiceDelivery(String documentTempl) {
        if (documentTempl.equals("webBill mit EGN")){
        	invDeliveryType = "WEBBILL";
        	itemizedType="electronic";
        	return true;
        } else if (documentTempl.equals("webBill ohne EGN")){
        	invDeliveryType = "WEBBILL";
        	itemizedType="none";
        	return true;
        } else if (documentTempl.equals("Vodafone webBill mit EGN")){
        	invDeliveryType = "WEBBILL";
        	itemizedType="electronic";
        	return true;
        } else if (documentTempl.equals("Vodafone webBill ohne EGN")){
        	invDeliveryType = "WEBBILL";
        	itemizedType="none";
        	return true;
        } else if (documentTempl.equals("Rechnung per E-Mail")){
        	invDeliveryType = "EMAIL";
        } else if (documentTempl.equals("Privatkunden EVN")){
        	itemizedType="paper";
        }
 		return false;
	}

	private void populateDocumentRecipient(GetSomMasterDataDAO dao,
			String mailingId,String accountNo, Order order) throws Exception {
		ArrayList<HashMap<String, Object>> docRec = dao.getDocumentRecipient(mailingId);
		if (docRec==null || docRec.size() == 0) 
			return;

		String target = null;
		String idPrefix=null;
		if (docRec.get(0).get("ENTITY_TYPE").toString().equals("I")){
			target = "ACCOUNT;INDIVIDUAL";
			idPrefix="In";
		} else if (docRec.get(0).get("ENTITY_TYPE").toString().equals("O")) {
			target = "ACCOUNT;ORGANIZATION";
			idPrefix="Or";
		} 
		if (target == null)
			throw new BksDataException("BKS0008","Entity type unknown : "+docRec.get(0).get("ENTITY_TYPE").toString());

		HashMap<String,Object[]> colPathMap = BksRefDataCacheHandler.getCcbSomMap().get(target);
		populateOrderNode(colPathMap,docRec.get(0),order);
		int lastIndex = order.getCustomerData().get(0).getBillingAccount().size() - 1; 
		BillingAccount sourceObj = order.getCustomerData().get(0).getBillingAccount().get(lastIndex);
			if (sourceObj.getOrganization() != null)
				sourceObj.getOrganization().setID(idPrefix+accountNo);
			if (sourceObj.getIndividual() != null)
				sourceObj.getIndividual().setID(idPrefix+accountNo);

		if (docRec.get(0).get("ADDRESS_ID") != null){
			ArrayList<HashMap<String, Object>> address = 
				dao.getAddressInfo((String)docRec.get(0).get("ADDRESS_ID"));
			if (address == null || address.size() == 0)
				throw new BksDataException("BKS0008","Document recipient address does not exist for mailing "+mailingId);
			colPathMap = BksRefDataCacheHandler.getCcbSomMap().get(target+";ADDRESS");
			populateOrderNode(colPathMap,address.get(0),order);
		}
		if (docRec.get(0).get("ACCESS_INFORMATION_ID") != null){
			ArrayList<HashMap<String, Object>> access = 
				dao.getAccessInfo((String)docRec.get(0).get("ACCESS_INFORMATION_ID"));
			if (access == null || access.size() == 0)
				throw new BksDataException("BKS0008","Document recipient access information does not exist for mailing "+mailingId);
			colPathMap = BksRefDataCacheHandler.getCcbSomMap().get(target+";ACCESS_INFORMATION");
			populateOrderNode(colPathMap,access.get(0),order);
			String validationId = (String)access.get(0).get("EMAIL_VALIDATION_ID");
			if (validationId!=null){
				colPathMap = BksRefDataCacheHandler.getCcbSomMap().get(target+";EMAIL_VALIDATION");
				ArrayList<HashMap<String, Object>> valid = dao.getEmailValidation(validationId);
				if (valid!=null && valid.size()>0)
					populateOrderNode(colPathMap,valid.get(0),order);
			}
		}
	}

	private void getCustomerInfo(GetSomMasterDataDAO dao,
			String customerNumber, Order order, boolean bFakeInstallCr) throws Exception {
		boolean bInstallCr = populateContactRole(dao,customerNumber,order);
		ArrayList<HashMap<String, Object>> customer = dao.getCustomerInfo(customerNumber);
		if (customer.size() > 0){
			String encryptKey = (String) customer.get(0).get("ENCRYPTION_KEY");
			theHelper.setCustomerEncKey(encryptKey);
			HashMap<String,Object[]> colPathMap = 
				BksRefDataCacheHandler.getCcbSomMap().get("CUSTOMER;CUSTOMER");

			ArrayList<HashMap<String, Object>> rootCust = dao.getRootCustomer(customerNumber);
			customer.get(0).putAll(rootCust.get(0));
			theHelper.setDesiredNewElement(-1);
			populateOrderNode(colPathMap,customer.get(0),order);
			order.getCustomerData().get(0).getCustomer().setID("C"+customerNumber);
			String entityId = customer.get(0).get("ENTITY_ID").toString();
			populateEntity(dao,entityId,"CUSTOMER",order,customerNumber);
	    	if (!bInstallCr && bFakeInstallCr)
	    		createPrimaryContactRole(dao, entityId, order,"INSTALLATION");
			List<ContactRole> contactRole = order.getCustomerData().get(0).getContactRole();
			Customer customerNode = order.getCustomerData().get(0).getCustomer();
			customerNode.setContactRoleRefIdList(new ContactRoleRefIdList());
			associateRelevantContactIds(customerNode.getID(),contactRole,customerNode.getContactRoleRefIdList().getContactRoleRef());
			populateAffinityGroup(dao,customerNumber,order);
			populateCustomerPermissions(dao,customerNumber,order,colPathMap);
			
		} else {
			throw new BksDataException("BKS0000","No customer found "+customerNumber);
		}
		ArrayList<HashMap<String, Object>> params = dao.getChampionId(customerNumber);
		if (params.size() > 0){
			String champId = (String)params.get(0).get("CHAMPION_ID");
			ExistConfString2 value = new ExistConfString2();
			value.setExisting(champId);
			order.getCustomerData().get(0).getCustomer().setChampionID(value);
		}
	}

	private void populateCustomerPermissions(GetSomMasterDataDAO dao,
			String customerNumber, Order order,
			HashMap<String, Object[]> colPathMap) throws Exception {
		ArrayList<HashMap<String, Object>> perms = dao
				.getCustomerPermissionInfo(customerNumber);
		if (perms == null || perms.size() == 0)
			return;
		HashMap<String, Boolean> indicatorData = new HashMap<String, Boolean>();
		indicatorData.put("CUSTOMER_DATA_INDICATOR", false);
		indicatorData.put("PERSONAL_DATA_INDICATOR", false);
		indicatorData.put("MARKETING_PHONE_INDICATOR", false);
		indicatorData.put("MARKETING_FAX_INDICATOR", false);
		for (int i = 0; perms != null && i < perms.size(); i++) {
			String permId = (String) perms.get(i).get("PERMISSION_ID");
			String srvId = (String) perms.get(i).get("SERVICE_ID");
			String value = (String) perms.get(i).get("PERMISSION_VALUE");
			Boolean boolVal = theHelper.convertToBoolean(value);
			String indicatorName = BksHelper.getPermnamemap().get(
					permId + ";" + srvId);
			if (indicatorData.get(indicatorName) != null)
				indicatorData.put(indicatorName,
						boolVal || indicatorData.get(indicatorName));
		}
		HashMap<String, Object> ccbData = new HashMap<String, Object>();
		for (String key : indicatorData.keySet())
			ccbData.put(key, (indicatorData.get(key)) ? "Y" : "N");
		populateOrderNode(colPathMap, ccbData, order);

	}

	private void populateAffinityGroup(GetSomMasterDataDAO dao,
			String customerNumber, Order order) throws Exception {
		ArrayList<HashMap<String, Object>> affinityGroup = dao.getAffinityGroup(customerNumber);
		for (int i = 0;affinityGroup!=null && i < affinityGroup.size(); i++) {
			String value = (String) affinityGroup.get(i).get("AFFINITY_GROUP_RD");
			theHelper.setDesiredNewElement(1);
			theHelper.setAttributeForPath(order, "set", 
					"CustomerData.Customer.AffinityGroupList.Existing.AffinityGroup", value);
		}
	}

	void associateRelevantContactIds(String id,
			List<ContactRole> contactRole, List<ExistConfRefIdWithListAction> list) {
		
		for (int i=0;contactRole!=null&&i<contactRole.size();i++){
			List<AssociatedObjectReference> refList = contactRole.get(i).getAssociatedObjectReference();
			for (int j=0;refList!=null&&j<refList.size();j++){
				if (refList.get(j).getNodeRefId().equals(id)){
					ExistConfRefIdWithListAction value = new ExistConfRefIdWithListAction();
					value.setExisting(contactRole.get(i).getID());
					list.add(value);
					break;
				}
			}
		}
	}

	private boolean populateContactRole(GetSomMasterDataDAO dao,
			String customerNumber, Order order) throws Exception {
	    HashMap<String,Object[]> colPathMap = BksRefDataCacheHandler.getCcbSomMap().get("CONTACT_ROLE;CONTACT_ROLE");
		ArrayList<HashMap<String, Object>> contactRoleEntity = dao.getContactRoleEntity(customerNumber);
	    boolean bInstallCr = false;
	    for (int i=0;contactRoleEntity != null && contactRoleEntity.size() > i;i++) {
	    	theHelper.setDesiredNewElement(1);
	    	populateOrderNode(colPathMap,contactRoleEntity.get(i),order);
			List<ContactRole> contRoleNodes = order.getCustomerData().get(0).getContactRole();
			int lastIndex = contRoleNodes.size()-1;
	    	ContactRole creNode = contRoleNodes.get(lastIndex);
	    	String contactRoleEntityId = contactRoleEntity.get(i).get("CONTACT_ROLE_ID").toString();
	    	creNode.setID("CE"+contactRoleEntityId);
	    	NonTypedCcbId owner = new NonTypedCcbId();
	    	owner.setExisting(customerNumber);
	    	creNode.setOwnerCcbId(owner);
	    	String crType = contactRoleEntity.get(i).get("CONTACT_ROLE_TYPE_RD").toString();
	    	if (crType.equals("INSTALLATION"))
	    		bInstallCr = true;
	    	String entityId = contactRoleEntity.get(i).get("ENTITY_ID").toString();
	    	try {
				populateEntity(dao,entityId,"CONTACT_ROLE",order,contactRoleEntityId);
			} catch (BksDataException e) {
				contRoleNodes = order.getCustomerData().get(0).getContactRole();
				lastIndex = contRoleNodes.size()-1;
				if (lastIndex >= 0)
					contRoleNodes.remove(lastIndex);
				logger.warn(e.getText());
				continue;
			}
	    	ArrayList<HashMap<String, Object>> contactRole = dao.getContactRole(contactRoleEntityId);
	    	for (int j=0;contactRole != null && contactRole.size() > j;j++) {
	    		String type = contactRole.get(j).get("SUPPORTED_OBJECT_TYPE_RD").toString();
	    		String idPrefix = getIdPrefix(type);
	    		if (idPrefix==null)
	    			continue;
	    		String supportedId = contactRole.get(j).get("SUPPORTED_OBJECT_ID").toString();
	    		AssociatedObjectReference value = new AssociatedObjectReference();
	    		CcbIdAny ccbId = new CcbIdAny();
	    		ccbId.setExisting(supportedId);
	    		ccbId.setType(getSomType(type));
	    		value.setCcbId(ccbId);
	    		value.setNodeRefId(idPrefix+supportedId);
	    		creNode.getAssociatedObjectReference().add(value);
	    	}
	    }
	    
	    // Cover the legacy contact roles
		ArrayList<HashMap<String, Object>> contactRole = dao.getLegacyContactRole(customerNumber);
		for (int i=0;contactRole != null && contactRole.size() > i;i++) {
	    	theHelper.setDesiredNewElement(1);
	    	populateOrderNode(colPathMap,contactRole.get(i),order);
	    	int lastIndex =  order.getCustomerData().get(0).getContactRole().size() -1;
	    	ContactRole creNode = order.getCustomerData().get(0).getContactRole().get(lastIndex);
	    	String crType = contactRole.get(i).get("CONTACT_ROLE_TYPE_RD").toString();
	    	if (crType.equals("INSTALLATION"))
	    		bInstallCr = true;
	    	String contactRoleId = contactRole.get(i).get("CONTACT_ROLE_ID").toString();
	    	creNode.setID("CR"+contactRoleId);
	    	NonTypedCcbId owner = new NonTypedCcbId();
	    	owner.setExisting(customerNumber);
	    	creNode.setOwnerCcbId(owner);
	    	String entityId = contactRole.get(i).get("ENTITY_ID").toString();
	    	populateEntity(dao,entityId,"CONTACT_ROLE",order,contactRoleId);
    		String type = contactRole.get(i).get("SUPPORTED_OBJECT_TYPE_RD").toString();
    		String idPrefix = getIdPrefix(type);
    		if (idPrefix==null)
    			continue;
    		String supportedId = contactRole.get(i).get("SUPPORTED_OBJECT_ID").toString();
    		AssociatedObjectReference value = new AssociatedObjectReference();
    		CcbIdAny ccbId = new CcbIdAny();
    		ccbId.setExisting(supportedId);
    		ccbId.setType(getSomType(type));
    		value.setCcbId(ccbId);
    		value.setNodeRefId(idPrefix+supportedId);
    		creNode.getAssociatedObjectReference().add(value);
	    }
		return bInstallCr;
	}

	private String getSomType(String type) {
		if (type.equals("ACCOUNT"))
			return "A";
		if (type.equals("CUSTOMER"))
			return "C";
		if (type.equals("SERVDLVRY"))
			return "D";
		if (type.equals("SERVICE_SUBS"))
			return "S";
		if (type.equals("SKELCNTR"))
			return "K";
		if (type.equals("ONE_GROUP"))
			return "O";

		return null;
	}

	private String getIdPrefix(String type) {
		if (type.equals("ACCOUNT"))
			return "AC";
		if (type.equals("CUSTOMER"))
			return "C";
		if (type.equals("SERVDLVRY"))
			return "SDC";
		if (type.equals("SERVICE_SUBS"))
			return "A";
		if (type.equals("SKELCNTR"))
			return "SK";
		if (type.equals("ONE_GROUP"))
			return "OG";
		return null;
	}

	private void populateEntity(GetSomMasterDataDAO dao, String entityId, String targetObject, Order order, String nodeIdSuffix) throws Exception {
		ArrayList<HashMap<String, Object>> entity = dao.getEntityInfo(entityId);
		String target = null;
		String idPrefix=null;
		if (entity.size() == 0)
			throw new BksDataException("BKS0008","No entity found for ID "+entityId);
		if (entity.get(0).get("ENTITY_TYPE").toString().equals("I")){
			target = targetObject + ";INDIVIDUAL";
			idPrefix="In";
		} else if (entity.get(0).get("ENTITY_TYPE").toString().equals("O")) {
			target = targetObject + ";ORGANIZATION";
			idPrefix="Or";
		} 
		if (target == null)
			throw new BksDataException("BKS0008","Entity type unknown : "+entity.get(0).get("ENTITY_TYPE").toString());

		HashMap<String,Object[]> colPathMap = BksRefDataCacheHandler.getCcbSomMap().get(target);
		populateOrderNode(colPathMap,entity.get(0),order);
		if (targetObject.equals("CUSTOMER")){
			Customer sourceObj = order.getCustomerData().get(0).getCustomer();
			if (sourceObj.getOrganization() != null)
				sourceObj.getOrganization().setID(idPrefix+nodeIdSuffix);
			if (sourceObj.getIndividual() != null)
				sourceObj.getIndividual().setID(idPrefix+nodeIdSuffix);
		} else if (targetObject.equals("CONTACT_ROLE")){
			int lastIndex = order.getCustomerData().get(0).getContactRole().size() -1;
			ContactRole sourceObj = order.getCustomerData().get(0).getContactRole().get(lastIndex);
			if (sourceObj.getOrganization() != null)
				sourceObj.getOrganization().setID(idPrefix+nodeIdSuffix);
			if (sourceObj.getIndividual() != null)
				sourceObj.getIndividual().setID(idPrefix+nodeIdSuffix);
		}

		if (entity.get(0).get("PRIMARY_ADDRESS_ID") != null){
			ArrayList<HashMap<String, Object>> address = 
				dao.getAddressInfo((String)entity.get(0).get("PRIMARY_ADDRESS_ID"));
			if (address == null || address.size() == 0)
				throw new BksDataException("BKS0008","Primary address does not exist for entity "+entityId);
			colPathMap = BksRefDataCacheHandler.getCcbSomMap().get(target+";ADDRESS");
			populateOrderNode(colPathMap,address.get(0),order);
		} else {
			ArrayList<HashMap<String, Object>> address = dao.getAddressForEntity(entityId);
			if (address!=null && address.size() == 1) {
				colPathMap = BksRefDataCacheHandler.getCcbSomMap().get(target+";ADDRESS");
				populateOrderNode(colPathMap,address.get(0),order);
			}
		}
		if (entity.get(0).get("PRIMARY_ACCESS_INFORMATION_ID") != null){
			ArrayList<HashMap<String, Object>> access = 
				dao.getAccessInfo((String)entity.get(0).get("PRIMARY_ACCESS_INFORMATION_ID"));
			if (access != null && access.size() >0) {
				if (targetObject.equals("CUSTOMER"))
					createPrimaryContactRole(dao,entityId,order,"PRIMARY");
				colPathMap = BksRefDataCacheHandler.getCcbSomMap().get(target+";ACCESS_INFORMATION");
				populateOrderNode(colPathMap,access.get(0),order);
				String validationId = (String)access.get(0).get("EMAIL_VALIDATION_ID");
				if (validationId!=null){
					colPathMap = BksRefDataCacheHandler.getCcbSomMap().get(target+";EMAIL_VALIDATION");
					ArrayList<HashMap<String, Object>> valid = dao.getEmailValidation(validationId);
					if (valid!=null && valid.size()>0)
						populateOrderNode(colPathMap,valid.get(0),order);
				}
			}
		}
	}

	private void createPrimaryContactRole(GetSomMasterDataDAO dao,
			String entityId, Order order, String nodeId) throws Exception {
		List<ContactRole> contRoleList = order.getCustomerData().get(0).getContactRole();
		contRoleList.add(new ContactRole());
    	int lastIndex =  contRoleList.size() -1;
    	ContactRole creNode = order.getCustomerData().get(0).getContactRole().get(lastIndex);
    	creNode.setID(nodeId);
    	creNode.setCcbId(new CcbIdR());
    	creNode.getCcbId().setExisting(nodeId);
    	creNode.getCcbId().setType("R");
    	NonTypedCcbId owner = new NonTypedCcbId();
    	owner.setExisting(customerNumber);
    	creNode.setOwnerCcbId(owner);
    	creNode.setContactRoleTypeRd(new ExistConfString16());
    	creNode.getContactRoleTypeRd().setExisting(nodeId);
    	populateEntity(dao,entityId,"CONTACT_ROLE",order,nodeId);
    	if (nodeId.equals("PRIMARY")) {
    		AssociatedObjectReference value = new AssociatedObjectReference();
    		CcbIdAny ccbId = new CcbIdAny();
    		ccbId.setExisting(customerNumber);
    		ccbId.setType("C");
    		value.setCcbId(ccbId);
    		value.setNodeRefId("C"+customerNumber);
    		creNode.getAssociatedObjectReference().add(value);
    	}
	}

	private void populateOrderNode(HashMap<String, Object[]> colPathMap, 
			HashMap<String,Object> ccbData, Object order) throws Exception {
		
		Set<String> keys = colPathMap.keySet();
		Iterator<String> keyiter = keys.iterator();
		while (keyiter.hasNext()){
			String column = keyiter.next();
			int index = 0;
			if (column.equals("-")){
				String path = (String) colPathMap.get(column)[0];
				theHelper.setAttributeForPath(order, "set", path, colPathMap.get(column)[1].toString());
			} else if (column.equals("PROVIDER_CODE")){
				String path = (String) colPathMap.get(column)[0];
				String classification = (String)ccbData.get("CLASSIFICATION_RD");
				String rootCustomer = (String)ccbData.get("ROOT_CUSTOMER_NUMBER");
				String converted = theHelper.mapClassificationToProvider(classification,rootCustomer);
				theHelper.setAttributeForPath(order, "set", path, converted);
			} else if (column.equals("STREET_NUMBER")){
				String path = (String) colPathMap.get(column)[0];
				String streetNumber = (String)ccbData.get(column);
				if (streetNumber!=null) {
					theHelper.setAttributeForPath(order, "set", path,
							getPartValue(streetNumber, 1));
					path = (String) colPathMap.get("STREET_NUMBER_SUFFIX")[0];
					if (path.contains("InvalidAddressIndicator"))
						path = (String) colPathMap.get("STREET_NUMBER_SUFFIX")[3];
					String suf = getPartValue(streetNumber, 2);
					if (suf!=null && suf.length()>0)
						theHelper.setAttributeForPath(order, "set", path,suf);
				}
			} else if (column.equals("VALIDATION_DATE")){
				String path = (String) colPathMap.get(column)[0];
				String applyMethod = (String) colPathMap.get(column)[1];
				String introVersion = (String) colPathMap.get(column)[2];
				Timestamp value = (Timestamp)ccbData.get(column);
				String status = (String)ccbData.get("VALIDATION_STATUS");
				if (!theHelper.isClientVersLowerThanIntro(introVersion,clientSchemaVersion)&&
					Arrays.asList(validStatus).contains(status)){
					Object converted = theHelper.invokeMethod(theHelper,applyMethod, value.toString().trim(), String.class);
					theHelper.setAttributeForPath(order, "set", path, converted);
				}
			} else if (column.equals("ERROR_CODE")){
				String path = (String) colPathMap.get(column)[0];
				String value = (String)ccbData.get(column);
				String status = (String)ccbData.get("VALIDATION_STATUS");
				String introVersion = (String) colPathMap.get(column)[2];
				if (!theHelper.isClientVersLowerThanIntro(introVersion,clientSchemaVersion)&&
						(status.equals("IN_PROGRESS")||status.equals("FAILED")))
					theHelper.setAttributeForPath(order, "set", path, value);
			} else {
				while (index < colPathMap.get(column).length) {
					String path = (String) colPathMap.get(column)[index++];
					Object value = ccbData.get(column);
					String applyMethod = (String) colPathMap.get(column)[index++];
					if (value  != null) {
						String introVersion = (String) colPathMap.get(column)[index];
						if (introVersion != null && clientSchemaVersion != null &&
							theHelper.isClientVersLowerThanIntro(introVersion,clientSchemaVersion)){
							index++;
							continue;
						}
						if (applyMethod == null)
							theHelper.setAttributeForPath(order, "set", path, value.toString().trim());
						else {
							Object converted = theHelper.invokeMethod(theHelper,applyMethod, value.toString().trim(), String.class);
							if (converted != null) 
								theHelper.setAttributeForPath(order, "set", path, converted);
						}
					} else if (column.equals("STREET_NUMBER_SUFFIX") && path.contains("InvalidAddressIndicator")) {
							theHelper.setAttributeForPath(order, "set", path, false);
					}
					index++;
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

	private void populateSkeletonItems(GetSomMasterDataDAO dao, String ContractNumber, SkeletonContract skeletonContract) throws Exception {
		HashMap<String,String> functions =BksRefDataCacheHandler.getFunction();
		ArrayList<HashMap<String, Object>> skItems = dao.getSkeletonItems(ContractNumber);
		for (int i=0;skItems!=null&&i<skItems.size();i++) {
			HashMap<String,Object[]> colPathMap = BksRefDataCacheHandler.getCcbSomMap().get("SKELETON_ITEM;SKELETON_ITEM");
			String serviceCode = skItems.get(i).get("SERVICE_CODE").toString();
			if (functions.get(serviceCode) == null)
				continue;
			if (!isItemAleadyAdded(serviceCode,skeletonContract))
				theHelper.setDesiredNewElement(0);
			populateOrderNode(colPathMap,skItems.get(i),skeletonContract);
			String productCode = skItems.get(i).get("PRODUCT_CODE").toString();
			String productVersionCode = skItems.get(i).get("PRODUCT_VERSION_CODE").toString();
			String tariffCode = skItems.get(i).get("PRICING_STRUCTURE_CODE").toString();
            if(!isPricingDefinedForService(dao, productCode, productVersionCode, serviceCode, tariffCode))
                continue;
            if(isItemAleadyAdded(serviceCode, skeletonContract))
                theHelper.setDesiredNewElement(1);
            List<SkeletonContractItem> skItemList = skeletonContract.getSkeletonContractItem();
            SkeletonContractItem item = skItemList.get(skItemList.size()-1);
            if (item.getTariffCodeList() == null){
            	ExistConfTariffCodeList tariffList = new ExistConfTariffCodeList();
            	tariffList.setExisting(new net.arcor.orderschema.TariffCodeList());
            	item.setTariffCodeList(tariffList);
            }
            item.getTariffCodeList().getExisting().getTariffCode().add(tariffCode);
            if(!item.getTariffCodeList().getExisting().getTariffCode().contains(tariffCode))
                item.getTariffCodeList().getExisting().getTariffCode().add(tariffCode);
		}
		List<SkeletonContractItem> currentList = skeletonContract.getSkeletonContractItem();
		for (int i=0;currentList!=null&&i<currentList.size();i++){
			if (currentList.get(i).getTariffCodeList() == null){
				currentList.remove(i);
				i--;
			}
		}
		if (currentList.isEmpty())
			throw new BksDataException("BKS0008","No valid tariff code found in skeleton contract");
	}


	private boolean isItemAleadyAdded(String serviceCode,
			SkeletonContract skeletonContract) {
		HashMap<String,String> functions =BksRefDataCacheHandler.getFunction();
		HashMap<String,String> accesses =BksRefDataCacheHandler.getAccess();
		HashMap<String,String> variants =BksRefDataCacheHandler.getVariant();
		String func = functions.get(serviceCode);
		String access = accesses.get(serviceCode);
		String variant = variants.get(serviceCode);
		List<SkeletonContractItem> currentList = skeletonContract.getSkeletonContractItem();
		for (int i=0;currentList!=null&&i<currentList.size();i++){
			if (func.equals(currentList.get(i).getFunctionName())){
				boolean bAccessMatch = false;
				boolean bVariantMatch = false;
				if (access==null)
					bAccessMatch = (access==currentList.get(i).getAccessName());
				else
					bAccessMatch = access.equals(currentList.get(i).getAccessName());

				if (variant==null)
					bVariantMatch = (variant==currentList.get(i).getVariantName());
				else
					bVariantMatch = variant.equals(currentList.get(i).getVariantName());
				
				if (bAccessMatch&&bVariantMatch)
					return true;
			}
		}
		return false;
	}

	private boolean isPricingDefinedForService(GetSomMasterDataDAO dao, String productCode, String productVersionCode, 
			String serviceCode, String tariffCode) throws Exception {
		ArrayList<HashMap<String, Object>> priceServ = 
			dao.getTariffService(productCode,productVersionCode,serviceCode,tariffCode);
		if (priceServ.size() > 0)
			return true;
		return false;
	}

	public void setInput(InputData input) {
		super.setInput(input);
		setCustomerNumber(((GetSomMasterDataInput)input).getKundennummer());
			
	}
}
