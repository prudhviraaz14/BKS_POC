package net.arcor.bks.db.ibatis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.arcor.bks.db.GetSomMasterDataDAO;

import org.springframework.orm.ibatis.SqlMapClientTemplate;

public class GetSomMasterDataDAOImpl extends BksDataAccessObjectImpl
		implements GetSomMasterDataDAO { 

	public ArrayList<HashMap<String, Object>> getCustomerInfo(
			String customerNumber) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetSomMasterData."+dataSourceName + ".RetrieveCustomerInfo",customerNumber);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getEntityInfo(
			String entityId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetSomMasterData."+dataSourceName + ".RetrieveEntityInfo",entityId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getAccountData(
			String accountNo) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetSomMasterData."+dataSourceName + ".RetrieveAccountData",accountNo);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getServiceDeliveryContract(
			String contractNumber) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetSomMasterData."+dataSourceName + ".RetrieveServiceDeliveryContract",contractNumber);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getAccessInfo(String accessInfoId)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetSomMasterData."+dataSourceName + ".RetrieveAccessInfo",accessInfoId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getAddressInfo(String addressId)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetSomMasterData."+dataSourceName + ".RetrieveAddressInfo",addressId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getSkeletonContracts(
			String customerNumber) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetSomMasterData."+dataSourceName + ".RetrieveSkeletonContracts",customerNumber);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getSkeletonItems(String contractNumber)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetSomMasterData."+dataSourceName + ".RetrieveSkeletonItems",contractNumber);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getTariffService(
			String productCode, String productVersionCode, String serviceCode,
			String tariffCode) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,String> params = new HashMap<String, String>();
        params.put("productCode", productCode);
        params.put("productVersionCode", productVersionCode);
        params.put("serviceCode", serviceCode);
        params.put("tariffCode", tariffCode);
        List list = template.queryForList("GetSomMasterData."+dataSourceName + ".RetrieveTariffService",params);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getRootCustomer(
			String customerNumber) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetSomMasterData."+dataSourceName + ".RetrieveRootCustomer",customerNumber);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getContactRole(String objectId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetSomMasterData."+dataSourceName + ".RetrieveContactRole",objectId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getSkeletonContractByNumber(
			String contractNumber) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetSomMasterData."+dataSourceName + ".RetrieveSkeletonContractByNumber",contractNumber);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getBankData(String bankAccId,String customerNumber)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,String> params = new HashMap<String, String>();
        params.put("bankAccountId", bankAccId);
        params.put("customerNumber", customerNumber);
        List list = template.queryForList("GetSomMasterData."+dataSourceName + ".RetrieveBankData",params);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getChampionId(
			String customerNumber) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetSomMasterData."+dataSourceName + ".RetrieveChampionId",customerNumber);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getDocumentRecipient(
			String mailingId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetSomMasterData."+dataSourceName + ".RetrieveDocumentRecipient",mailingId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getAccountsForCustomer(
			String customerNumber) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetSomMasterData."+dataSourceName + ".RetrieveAccountsForCustomer",customerNumber);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getDocumentPattern(
			String accountNo) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetSomMasterData."+dataSourceName + ".RetrieveDocumentPattern",accountNo);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getContactRoleEntity(
			String customerNumber) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetSomMasterData."+dataSourceName + ".RetrieveContactRoleEntity",customerNumber);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getLegacyContactRole(
			String customerNumber) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetSomMasterData."+dataSourceName + ".RetrieveLegacyContactRole",customerNumber);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getAddressForEntity(
			String entityId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetSomMasterData."+dataSourceName + ".RetrieveAddressForEntity",entityId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getAffinityGroup(
			String customerNumber) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetSomMasterData."+dataSourceName + ".RetrieveAffinityGroup",customerNumber);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getOneGroup(String customerNumber)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetSomMasterData."+dataSourceName + ".RetrieveOneGroup",customerNumber);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getEmailValidation(String validationId) 
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetSomMasterData."+dataSourceName + ".RetrieveEmailValidation",validationId);
        return (ArrayList<HashMap<String, Object>>) list;
	}
	
	public ArrayList<HashMap<String, Object>> getCustomerPermissionInfo(
			String customerNumber) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetSomMasterData."+dataSourceName + ".RetrieveCustomerPermissionInfo",customerNumber);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getCustomerAncestor(
			String customerNumber) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetSomMasterData."+dataSourceName + ".RetrieveCustomerAncestor",customerNumber);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getAccountsForCustList(
			List<String> hierarchycustNumbers) {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetSomMasterData."+dataSourceName + ".RetrieveAccountsForCustList",hierarchycustNumbers);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getSDLContractsForCustList(
			List<String> hierarchycustNumbers) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetSomMasterData."+dataSourceName + ".RetrieveSDLContractsForCustList",hierarchycustNumbers);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getRebatesForAccountList(
			List<String> accountNumbers) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetSomMasterData."+dataSourceName + ".RetrieveRebatesForAccountList",accountNumbers);
        return (ArrayList<HashMap<String, Object>>) list;
	}
	
	public ArrayList<HashMap<String, Object>> getCustomerNumber(
			String dialInAccountName) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetSomMasterData."+dataSourceName + ".RetrieveCustomerNumber",dialInAccountName);
        return (ArrayList<HashMap<String, Object>>) list;
	}
	
	public ArrayList<HashMap<String, Object>> getAccessNumber(
			String phoneAccessNumber) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetSomMasterData."+dataSourceName + ".RetrieveAccessNumberCustomer",phoneAccessNumber);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	
	
}
