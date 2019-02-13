package net.arcor.bks.db.ibatis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.orm.ibatis.SqlMapClientTemplate;

import net.arcor.bks.db.ConsolidateSubscriptionDataDAO;

public class ConsolidateSubscriptionDataDAOImpl  extends BksDataAccessObjectImpl implements
		ConsolidateSubscriptionDataDAO {

	public ArrayList<HashMap<String, Object>> getDataByBundleId(String bundleId)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("ConsolidateSubscriptionData."+dataSourceName + ".RetrieveDataByBundleId",bundleId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getCharValuesForBundle(
			String bundleId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("ConsolidateSubscriptionData."+dataSourceName + ".RetrieveCharValuesForBundle",bundleId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getServiceCodesForCharCode(
			String charCode) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("ConsolidateSubscriptionData."+dataSourceName + ".RetrieveServiceCodesForCharCode",charCode);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getLeadingCharInfoOtherBundles(
			String bundleId, String customerNumber) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,String> params = new HashMap<String, String>();
        params.put("bundleId", bundleId);
        params.put("customerNumber", customerNumber);
        List list = template.queryForList("ConsolidateSubscriptionData."+dataSourceName + ".RetrieveLeadingCharInfoOtherBundles",params);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getDataBySerSu(String serSuId)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("ConsolidateSubscriptionData."+dataSourceName + ".RetrieveDataBySerSu",serSuId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getAccessNumber(String zarAccnum)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("ConsolidateSubscriptionData."+dataSourceName + ".RetrieveAccessNumber",zarAccnum);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getOpenOrders(String serSuId,
			String orderId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,String> params = new HashMap<String, String>();
        params.put("serSuId", serSuId);
        params.put("orderId", orderId);
        List list = template.queryForList("ConsolidateSubscriptionData."+dataSourceName + ".RetrieveOpenOrders",params);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public HashMap<String, Object> GetZarAccNumInfo(String custNumber, String areaCode, String accessNumber) throws Exception {
		HashMap<String, Object> accNumInfo = null;
		
		SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,Object> params = new HashMap<String, Object>();
        params.put("customerNumberIn", custNumber);
        params.put("areaCodeIn", areaCode);
        params.put("accessNumber", accessNumber);
        template.queryForObject("ConsolidateSubscriptionData.GetZarAccNumInfo", params);
        Integer physicalStatus = (Integer) params.get("physicalStatus");
        Integer logicalStatus = (Integer) params.get("logicalStatus");
        if (isStatusValid(physicalStatus,logicalStatus)){
        	accNumInfo = new HashMap<String, Object>();
        	accNumInfo.put("groupId", params.get("accessId"));
        	accNumInfo.put("customerNumber", params.get("customerNumberOut"));
        }
        
        return accNumInfo;
	}

	private boolean isStatusValid(Integer physicalStatus, Integer logicalStatus) {
		if (physicalStatus==null || logicalStatus==null)
			return false;
		if (physicalStatus == 3 && logicalStatus !=14&& logicalStatus !=20)
			return true;
		if (physicalStatus == 4 && (logicalStatus == 13 || logicalStatus == 14 || logicalStatus == 21))
			return true;
        if (physicalStatus == 7 && (logicalStatus == 17 || logicalStatus == 24 || logicalStatus == 25))
			return true;
        if (physicalStatus == 8 && (logicalStatus == 17 || logicalStatus == 18 || logicalStatus == 25))
			return true;
		return false;
	}


	public ArrayList<HashMap<String, Object>> getAddressCharacteristic(String serSuId, String charCode) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,String> params = new HashMap<String, String>();
        params.put("serSuId", serSuId);
        params.put("charCode", charCode);
        List list = template.queryForList("ConsolidateSubscriptionData."+dataSourceName + ".RetrieveAddressCharacteristic",params);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getCharValuesForChildServices(
			String bundleId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("ConsolidateSubscriptionData."+dataSourceName + ".RetrieveCharValuesForChildServices",bundleId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

}
