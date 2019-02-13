package net.arcor.bks.db.ibatis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.arcor.bks.db.WbciCustomerInfoForPortingDAO;

import org.springframework.orm.ibatis.SqlMapClientTemplate;
public class WbciCustomerInfoForPortingDAOImpl extends BksDataAccessObjectImpl implements WbciCustomerInfoForPortingDAO {


	public ArrayList<HashMap<String, Object>> getAccessNumberForSerSu(String serSuId,String status) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,String> params = new HashMap<String, String>();
        params.put("SerSuId", serSuId);
        params.put("Status", status);
        List list = template.queryForList("WbciCustomerInfoForPorting."+dataSourceName + ".RetrieveAccessNumberForSerSu",params);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getAddressForSerSu(String serSuId)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("WbciCustomerInfoForPorting."+dataSourceName + ".RetrieveAddressForSerSu",serSuId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getBundleData(String serSuId)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("WbciCustomerInfoForPorting."+dataSourceName + ".RetrieveBundleData",serSuId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getConfigValueForSerSu(
			String serSuId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("WbciCustomerInfoForPorting."+dataSourceName + ".RetrieveConfigValueForSerSu",serSuId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getCustomerDataByAccessNumber(
			String accessNumber) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("WbciCustomerInfoForPorting."+dataSourceName + ".RetrieveCustomerDataByAccessNumber",accessNumber);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getExternalOrder(String serSuId,
			String intention) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,String> params = new HashMap<String, String>();
        params.put("servSubsId", serSuId);
        params.put("intention", intention);
        List list = template.queryForList("WbciCustomerInfoForPorting."+dataSourceName + ".RetrieveExternalOrder",params);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getInternalOrder(String serSuId,
			String usageMode) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,String> params = new HashMap<String, String>();
        params.put("servSubsId", serSuId);
        params.put("usageMode", usageMode);
        List list = template.queryForList("WbciCustomerInfoForPorting."+dataSourceName + ".RetrieveInternalOrder",params);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getRootCustomer(String custNum)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("WbciCustomerInfoForPorting."+dataSourceName + ".RetrieveRootCustomer",custNum);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getOrderFormData(String serSuId)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("WbciCustomerInfoForPorting."+dataSourceName + ".RetrieveOrderFormData",serSuId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getServDeliveContData(
			String serSuId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("WbciCustomerInfoForPorting."+dataSourceName + ".RetrieveServDeliveContData",serSuId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getCustomerByName(String name1,String name2,String forename1,String forename2,
												String postalCode,String street1,String street2,
												String city1,String city2) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,String> params = new HashMap<String, String>();
        params.put("name1", name1);
        params.put("name2", name2);
        params.put("forename1", forename1);
        params.put("forename2", forename2);
        params.put("postalCode", postalCode);
        params.put("street1", street1);
        params.put("street2", street2);
        params.put("city1", city1);
        params.put("city2", city2);
        List list = template.queryForList("WbciCustomerInfoForPorting."+dataSourceName + ".RetrieveCustomerByName",params);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getBundleDataByBundleId(
			String bundleId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("WbciCustomerInfoForPorting."+dataSourceName + ".RetrieveBundleDataByBundleId",bundleId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getBundleInfoForCustomer(
			String custNum) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("WbciCustomerInfoForPorting."+dataSourceName + ".RetrieveBundleInfoForCustomer",custNum);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getCompanyByName(String name1,
			String name2, String companySuffix1, String companySuffix2,
			String postalCode, String street1, String street2,String city1,String city2) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,String> params = new HashMap<String, String>();
        params.put("name1", name1);
        params.put("name2", name2);
        params.put("companySuffix1", companySuffix1);
        params.put("companySuffix2", companySuffix2);
        params.put("postalCode", postalCode);
        params.put("street1", street1);
        params.put("street2", street2);
        params.put("city1", city1);
        params.put("city2", city2);
        List list = template.queryForList("WbciCustomerInfoForPorting."+dataSourceName + ".RetrieveCompanyByName",params);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getChildService(String serSuId)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("WbciCustomerInfoForPorting."+dataSourceName + ".RetrieveChildService",serSuId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getDeactRecByAccessNumber(
			String accNum) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("WbciCustomerInfoForPorting."+dataSourceName + ".RetrieveDeactRecByAccessNumber",accNum);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public String getZarStatus(String custNumber, String areaCode, String accessNumber) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,Object> params = new HashMap<String, Object>();
        params.put("customerNumberIn", custNumber);
        params.put("areaCodeIn", areaCode);
        params.put("accessNumber", accessNumber);
        template.queryForObject("WbciCustomerInfoForPorting.GetZarStatus", params);
        Integer physicalStatus = (Integer) params.get("physicalStatus");
        if (physicalStatus==null)
        	return null;
        String prefix = (physicalStatus<10)?"P0":"P";
        String zarStatus = prefix+physicalStatus+",L"+(Integer) params.get("logicalStatus");
        return zarStatus;
	}

}
