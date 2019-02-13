package net.arcor.bks.db.ibatis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.arcor.bks.db.FetchProductBundleDAO;

import org.springframework.orm.ibatis.SqlMapClientTemplate;

public class FetchProductBundleDAOImpl extends BksDataAccessObjectImpl
		implements FetchProductBundleDAO {

	public ArrayList<HashMap<String, Object>> getConfiguredValues(String servSubsId, String returnOrdered) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,String> params = new HashMap<String, String>();
        params.put("servSubsId", servSubsId);
        params.put("returnOrdered", returnOrdered);
        List list = template.queryForList("FetchProductBundle."+dataSourceName + ".RetrieveConfiguredValue",params);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getSalesPackageInfo(
			String bundleId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("FetchProductBundle."+dataSourceName + ".RetrieveSalesPackageInfo",bundleId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getAddress(String servSubsId)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("FetchProductBundle."+dataSourceName + ".RetrieveAddress",servSubsId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getBundleItems(String bundleId)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("FetchProductBundle."+dataSourceName + ".RetrieveBundleItems",bundleId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getContractData(
			String servSubsId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("FetchProductBundle."+dataSourceName + ".RetrieveContractData",servSubsId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getSDContractData(
			String servSubsId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("FetchProductBundle."+dataSourceName + ".RetrieveSDContractData",servSubsId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getServiceSubscription(
			String servSubsId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("FetchProductBundle."+dataSourceName + ".RetrieveServiceSubscription",servSubsId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getServiceLocation(
			String servSubsId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("FetchProductBundle."+dataSourceName + ".RetrieveServiceLocation",servSubsId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getTariffService(
			String productCode,	String productVersionCode,String serviceCode, String tariffCode) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,String> params = new HashMap<String, String>();
        params.put("productCode", productCode);
        params.put("productVersionCode", productVersionCode);
        params.put("serviceCode", serviceCode);
        params.put("tariffCode", tariffCode);
        List list = template.queryForList("FetchProductBundle."+dataSourceName + ".RetrieveTariffService",params);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getAccessNumberChars(
			String servSubsId, String servCharCode, String returnOrdered) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,String> params = new HashMap<String, String>();
        params.put("servSubsId", servSubsId);
        params.put("servCharCode", servCharCode);
        params.put("returnOrdered", returnOrdered);
        List list = template.queryForList("FetchProductBundle."+dataSourceName + ".RetrieveAccessNumberChars",params);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getChildServices(
			String servSubsId, String returnOrdered) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,String> params = new HashMap<String, String>();
        params.put("servSubsId", servSubsId);
        params.put("returnOrdered", returnOrdered);
        List list = template.queryForList("FetchProductBundle."+dataSourceName + ".RetrieveChildServices",params);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getHardwareDepService(
			String servSubsId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("FetchProductBundle."+dataSourceName + ".RetrieveHardwareDepService",servSubsId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getOpenOrdersForService(
			String servSubsId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("FetchProductBundle."+dataSourceName + ".RetrieveOpenOrdersForService",servSubsId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getConfigCharacteristics(
			String servSubsId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("FetchProductBundle."+dataSourceName + ".RetrieveConfigCharacteristics",servSubsId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getBundleItemsByServSubs(
			String servSubsId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("FetchProductBundle."+dataSourceName + ".RetrieveBundleItemsByServSubs",servSubsId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getUnbundledServiceSubscription(
			String servSubsId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("FetchProductBundle."+dataSourceName + ".RetrieveUnbundledServiceSubscription",servSubsId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getCompletedOrder(String orderId,
			Integer orderPosNo) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,Object> params = new HashMap<String, Object>();
        params.put("orderId", orderId);
        params.put("orderPosNum", orderPosNo);
        List list = template.queryForList("FetchProductBundle."+dataSourceName + ".RetrieveCompletedOrder",params);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getCustomerOrderBundle(
			String orderId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("FetchProductBundle."+dataSourceName + ".RetrieveCustomerOrderBundle",orderId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getAdjustments(String accNum)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("FetchProductBundle."+dataSourceName + ".RetrieveAdjustments",accNum);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getSelectedDestinations(
			String prodSubsId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("FetchProductBundle."+dataSourceName + ".RetrieveSelectedDestinations",prodSubsId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getBundleItemsByTasi(String tasi)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("FetchProductBundle."+dataSourceName + ".RetrieveBundleItemsByTasi",tasi);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getAllocatedId(String servSubsId)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("FetchProductBundle."+dataSourceName + ".RetrieveAllocatedId",servSubsId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getDialupSelectedDestination(
			String servSubsId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("FetchProductBundle."+dataSourceName + ".RetrieveDialupSelectedDestination",servSubsId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getOnlineService(
			String customerNumber, String bundleId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,Object> params = new HashMap<String, Object>();
        params.put("customerNumber", customerNumber);
        params.put("bundleId", bundleId);
        List list = template.queryForList("FetchProductBundle."+dataSourceName + ".RetrieveOnlineService",params);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getCharValues(ArrayList<String> cscIdList) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("FetchProductBundle."+dataSourceName + ".RetrieveCharValues",cscIdList);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getSalesPackageInfoByCode(
			String spCode) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("FetchProductBundle."+dataSourceName + ".RetrieveSalesPackageInfoByCode",spCode);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getOnenetForBundle(String bundleId)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("FetchProductBundle."+dataSourceName + ".RetrieveOnenetForBundle",bundleId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getDefaultLicences(
			String serviceType, String servSubsId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,Object> params = new HashMap<String, Object>();
        params.put("serviceType", serviceType);
        params.put("parentSerSuId", servSubsId);
        List list = template.queryForList("FetchProductBundle."+dataSourceName + ".RetrieveDefaultLicences",params);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getInitialTicketForService(
			String servSubsId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("FetchProductBundle."+dataSourceName + ".RetrieveInitialTicketForService",servSubsId);
        return (ArrayList<HashMap<String, Object>>) list;
	}
}
