package net.arcor.bks.db.ibatis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.orm.ibatis.SqlMapClientTemplate;

import net.arcor.bks.db.PrefetchProductBundlesDAO;

public class PrefetchProductBundlesDAOImpl extends BksDataAccessObjectImpl
		implements PrefetchProductBundlesDAO {

	public ArrayList<HashMap<String, Object>> getAddress(String servSubsId)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("PrefetchProductBundles."+dataSourceName + ".RetrieveAddress",servSubsId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getBundleItems(String bundleId)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("PrefetchProductBundles."+dataSourceName + ".RetrieveBundleItems",bundleId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getCustomerBundles(
			String customerNumber) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("PrefetchProductBundles."+dataSourceName + ".RetrieveCustomerBundles",customerNumber);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getMainAccessNumber(
			String servSubsId, String charCode) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,String> params = new HashMap<String, String>();
        params.put("servSubsId", servSubsId);
        params.put("charCode", charCode);
        List list = template.queryForList("PrefetchProductBundles."+dataSourceName + ".RetrieveMainAccessNumber",params);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getConfiguredValue(
			String servSubsId, String charCode) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,String> params = new HashMap<String, String>();
        params.put("servSubsId", servSubsId);
        params.put("charCode", charCode);
        List list = template.queryForList("PrefetchProductBundles."+dataSourceName + ".RetrieveConfiguredValue",params);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getSalesPackageInfo(
			String bundleId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("PrefetchProductBundles."+dataSourceName + ".RetrieveSalesPackageInfo",bundleId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getBundlesForAccNumChar(
			String charValue, String charCode) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,String> params = new HashMap<String, String>();
        params.put("charValue", charValue);
        params.put("charCode", charCode);
        List list = template.queryForList("PrefetchProductBundles."+dataSourceName + ".RetrieveBundlesForAccNumChar",params);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getPendingOrders(
			String customerNumber) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("PrefetchProductBundles."+dataSourceName + ".RetrievePendingOrders",customerNumber);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getBundlesForEnvelopId(
			String supertrackingId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("PrefetchProductBundles."+dataSourceName + ".RetrieveBundlesForEnvelopId",supertrackingId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getOnenetForBundle(String bundleId)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("PrefetchProductBundles."+dataSourceName + ".RetrieveOnenetForBundle",bundleId);
        return (ArrayList<HashMap<String, Object>>) list;
	}
}
