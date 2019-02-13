package net.arcor.bks.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface GetSomMasterDataDAO extends BksDataAccessObject {

	ArrayList<HashMap<String, Object>> getCustomerInfo(String customerNumber) throws Exception;

	ArrayList<HashMap<String, Object>> getEntityInfo(String entityId) throws Exception;

	ArrayList<HashMap<String, Object>> getAccountData(String accountNo) throws Exception;

	ArrayList<HashMap<String, Object>> getServiceDeliveryContract(String contractNumber) throws Exception;

	ArrayList<HashMap<String, Object>> getContactRole(String objectId) throws Exception;

	ArrayList<HashMap<String, Object>> getAddressInfo(String string) throws Exception;

	ArrayList<HashMap<String, Object>> getAccessInfo(String string) throws Exception;

	ArrayList<HashMap<String, Object>> getSkeletonContracts(String customerNumber) throws Exception;

	ArrayList<HashMap<String, Object>> getSkeletonItems(String value) throws Exception;

	ArrayList<HashMap<String, Object>> getTariffService(String productCode,
			String productVersionCode, String serviceCode, String tariffCode) throws Exception;

	ArrayList<HashMap<String, Object>> getRootCustomer(String customerNumber) throws Exception;

	ArrayList<HashMap<String, Object>> getSkeletonContractByNumber(String contractNumber) throws Exception;

	ArrayList<HashMap<String, Object>> getBankData(String bankAccId, String customerNumber) throws Exception;

	ArrayList<HashMap<String, Object>> getAccountsForCustomer(String customerNumber) throws Exception;

	ArrayList<HashMap<String, Object>> getChampionId(String customerNumber) throws Exception;

	ArrayList<HashMap<String, Object>> getDocumentRecipient(String mailingId) throws Exception;

	ArrayList<HashMap<String, Object>> getDocumentPattern(String accountNo) throws Exception;

	ArrayList<HashMap<String, Object>> getContactRoleEntity(String customerNumber) throws Exception;

	ArrayList<HashMap<String, Object>> getLegacyContactRole(String customerNumber)throws Exception;

	ArrayList<HashMap<String, Object>> getAddressForEntity(String entityId)throws Exception;

	ArrayList<HashMap<String, Object>> getAffinityGroup(String customerNumber)throws Exception;

	ArrayList<HashMap<String, Object>> getOneGroup(String customerNumber)throws Exception;

	ArrayList<HashMap<String, Object>> getEmailValidation(String validationId) throws Exception;
	
	ArrayList<HashMap<String, Object>> getCustomerPermissionInfo(String customerNumber) throws Exception;

	ArrayList<HashMap<String, Object>>  getCustomerAncestor(String customerNumber) throws Exception;

	ArrayList<HashMap<String, Object>> getAccountsForCustList(List<String> hierarchycustNumbers) throws Exception;

	ArrayList<HashMap<String, Object>> getSDLContractsForCustList(List<String> hierarchycustNumbers) throws Exception;

	ArrayList<HashMap<String, Object>> getRebatesForAccountList(List<String> accountNumbers) throws Exception;
	
	ArrayList<HashMap<String, Object>> getCustomerNumber(String dialInAccountName) throws Exception;
	
	ArrayList<HashMap<String, Object>> getAccessNumber(String phoneAccessNumber) throws Exception;
	
	
	
	
	
	
}
