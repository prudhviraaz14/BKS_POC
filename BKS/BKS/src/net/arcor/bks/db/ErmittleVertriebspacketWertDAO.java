package net.arcor.bks.db;

import java.util.ArrayList;
import java.util.HashMap;

public interface ErmittleVertriebspacketWertDAO extends BksDataAccessObject {

	ArrayList<HashMap<String, Object>> getBandwidthService(String servSubId) throws Exception;

	ArrayList<HashMap<String, Object>> getCondition(String servSubId) throws Exception;

	ArrayList<HashMap<String, Object>> getExactAccessNumber(String countryCode,String cityCode, String localNumber) throws Exception;

	ArrayList<HashMap<String, Object>> getAccessNumber(String countryCode,String cityCode, String localNumber) throws Exception;

	ArrayList<HashMap<String, Object>> getOFContractData(String servSubId) throws Exception;

	ArrayList<HashMap<String, Object>> getSDContractData(String servSubId) throws Exception;

	ArrayList<HashMap<String, Object>> getSignedOFContractData(String servSubId) throws Exception;

	ArrayList<HashMap<String, Object>> getSignedSDContractData(String servSubId) throws Exception;

	ArrayList<HashMap<String, Object>> getMainAccessService(String servSubsId) throws Exception;

	ArrayList<HashMap<String, Object>> getHardwareCharacteristic(String prodSubsId) throws Exception;

	ArrayList<HashMap<String, Object>> getServiceSubscription(String serviceSubscrId) throws Exception;

	ArrayList<HashMap<String, Object>> getBundleItems(String servSubId) throws Exception;
}
