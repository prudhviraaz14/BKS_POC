package net.arcor.bks.db;

import java.util.ArrayList;
import java.util.HashMap;

public interface HoleVertriebspaketDetailsDAO extends BksDataAccessObject {

	ArrayList<HashMap<String, Object>> getCustomerData(String customerNumber) throws Exception;

	ArrayList<HashMap<String, Object>> getRootCustomer(String customerNumber) throws Exception;

	ArrayList<HashMap<String, Object>> getAddresses(String servSubsId) throws Exception;

	ArrayList<HashMap<String, Object>> getAccessNumberChars(String servSubsId) throws Exception;

	ArrayList<HashMap<String, Object>> getConfiguredValues(String servSubsId) throws Exception;

	ArrayList<HashMap<String, Object>> getChildServices(String servSubsId) throws Exception;

	ArrayList<HashMap<String, Object>> getBundleItems(String servSubId) throws Exception;

	ArrayList<HashMap<String, Object>> getBandwidthService(String servSubId) throws Exception;

	ArrayList<HashMap<String, Object>> getCondition(String servSubId) throws Exception;

	ArrayList<HashMap<String, Object>> getServiceByCustomer(String customerNumber) throws Exception;

	ArrayList<HashMap<String, Object>> getCustomerBundles(String customerNumber) throws Exception;

	ArrayList<HashMap<String, Object>> getExactAccessNumber(String countryCode,
			String cityCode, String localNumber) throws Exception;

	ArrayList<HashMap<String, Object>> getAccessNumber(String countryCode,
			String cityCode, String localNumber) throws Exception;

	ArrayList<HashMap<String, Object>> getOFContractData(String servSubId) throws Exception;

	ArrayList<HashMap<String, Object>> getSDContractData(String servSubId) throws Exception;

	ArrayList<HashMap<String, Object>> getHardwareCharacteristic(String prodSubsId) throws Exception;

	ArrayList<HashMap<String, Object>> getMainAccessService(String servSubsId) throws Exception;

	ArrayList<HashMap<String, Object>> getUnbundledServiceByCustomer(String customerNumber) throws Exception;

	ArrayList<HashMap<String, Object>> getAllChildServices(String servSubsId) throws Exception;

}
