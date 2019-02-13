package net.arcor.bks.db;

import java.util.ArrayList;
import java.util.HashMap;

public interface PrefetchProductBundlesDAO extends BksDataAccessObject {

	ArrayList<HashMap<String, Object>> getCustomerBundles(String customerNumber) throws Exception;

	ArrayList<HashMap<String, Object>> getBundleItems(String bundleId) throws Exception;

	ArrayList<HashMap<String, Object>> getMainAccessNumber(String servSubsId, String charCode) throws Exception;

	ArrayList<HashMap<String, Object>> getAddress(String servSubsId) throws Exception;

	ArrayList<HashMap<String, Object>> getConfiguredValue(String servSubsId,
			String charCode) throws Exception;

	ArrayList<HashMap<String, Object>> getSalesPackageInfo(String bundleId) throws Exception;

	ArrayList<HashMap<String, Object>> getBundlesForAccNumChar(String charValue, String charCode) throws Exception;

	ArrayList<HashMap<String, Object>> getPendingOrders(String customerNumber) throws Exception;

	ArrayList<HashMap<String, Object>> getBundlesForEnvelopId(String supertrackingId) throws Exception;

	ArrayList<HashMap<String, Object>>  getOnenetForBundle(String bundleId) throws Exception;
}

