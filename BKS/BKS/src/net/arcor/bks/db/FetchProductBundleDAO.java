package net.arcor.bks.db;

import java.util.ArrayList;
import java.util.HashMap;

public interface FetchProductBundleDAO extends BksDataAccessObject {

	ArrayList<HashMap<String, Object>> getBundleItems(String bundleId) throws Exception;

	ArrayList<HashMap<String, Object>> getAddress(String servSubsId) throws Exception;

	ArrayList<HashMap<String, Object>> getSalesPackageInfo(String bundleId) throws Exception;

	ArrayList<HashMap<String, Object>> getSalesPackageInfoByCode(String spCode) throws Exception;

	ArrayList<HashMap<String, Object>> getConfiguredValues(String servSubsId, String returnOrdered) throws Exception;

	ArrayList<HashMap<String, Object>> getServiceSubscription(String servSubsId) throws Exception;

	ArrayList<HashMap<String, Object>> getContractData(String servSubsId) throws Exception;

	ArrayList<HashMap<String, Object>> getSDContractData(String servSubsId) throws Exception;

	ArrayList<HashMap<String, Object>> getServiceLocation(String servSubsId) throws Exception;

	ArrayList<HashMap<String, Object>> getAccessNumberChars(String servSubsId, String servCharCode, String returnOrdered) throws Exception;

	ArrayList<HashMap<String, Object>> getChildServices(String servSubsId, String returnOrdered) throws Exception;

	ArrayList<HashMap<String, Object>> getHardwareDepService(String servSubsId) throws Exception;

	ArrayList<HashMap<String, Object>> getOpenOrdersForService(String servSubsId) throws Exception;

	ArrayList<HashMap<String, Object>> getConfigCharacteristics(String servSubsId) throws Exception;

	ArrayList<HashMap<String, Object>> getBundleItemsByServSubs(String servSubsId) throws Exception;

	ArrayList<HashMap<String, Object>> getUnbundledServiceSubscription(String servSubsId) throws Exception;

	ArrayList<HashMap<String, Object>> getCompletedOrder(String orderId,Integer orderPosNo) throws Exception;

	ArrayList<HashMap<String, Object>> getCustomerOrderBundle(String orderId) throws Exception;

	ArrayList<HashMap<String, Object>> getAdjustments(String accNum) throws Exception;

	ArrayList<HashMap<String, Object>> getSelectedDestinations(String prodSubsId) throws Exception;

	ArrayList<HashMap<String, Object>> getBundleItemsByTasi(String tasi) throws Exception;

	ArrayList<HashMap<String, Object>> getAllocatedId(String servSubsId) throws Exception;

	ArrayList<HashMap<String, Object>> getDialupSelectedDestination(String servSubsId) throws Exception;

	ArrayList<HashMap<String, Object>> getOnlineService(String customerNumber, String bundleId) throws Exception;

	ArrayList<HashMap<String, Object>> getCharValues(ArrayList<String> cscIdList) throws Exception;

	ArrayList<HashMap<String, Object>> getOnenetForBundle(String bundleId) throws Exception;

	ArrayList<HashMap<String, Object>> getDefaultLicences(String serviceType,String servSubsId) throws Exception;

	ArrayList<HashMap<String, Object>> getInitialTicketForService(String servSubsId) throws Exception;

	//ArrayList<HashMap<String, Object>> getTariffService(String productCode,	String productVersionCode, String serviceCode, String tariffCode) throws Exception;

}
