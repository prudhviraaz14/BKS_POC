package net.arcor.bks.db;

import java.util.ArrayList;
import java.util.HashMap;

public interface ConsolidateSubscriptionDataDAO extends BksDataAccessObject {
	ArrayList<HashMap<String, Object>> getDataByBundleId(String bundleId) throws Exception;
	ArrayList<HashMap<String, Object>> getCharValuesForBundle(String bundleId) throws Exception;
	ArrayList<HashMap<String, Object>>  getServiceCodesForCharCode(String charCode) throws Exception;
	ArrayList<HashMap<String, Object>> getLeadingCharInfoOtherBundles(String bundleId, String customerNumber) throws Exception;
	ArrayList<HashMap<String, Object>> getDataBySerSu(String serSuId) throws Exception;
	ArrayList<HashMap<String, Object>> getAccessNumber(String zarAccnum) throws Exception;
	ArrayList<HashMap<String, Object>> getOpenOrders(String serSuId,String orderId)throws Exception;
	HashMap<String, Object> GetZarAccNumInfo(String custNumber, String areaCode, String accessNumber) throws Exception;
	ArrayList<HashMap<String, Object>> getAddressCharacteristic(String serSuId, String charCode) throws Exception;
	ArrayList<HashMap<String, Object>> getCharValuesForChildServices(String bundleId) throws Exception;
}
