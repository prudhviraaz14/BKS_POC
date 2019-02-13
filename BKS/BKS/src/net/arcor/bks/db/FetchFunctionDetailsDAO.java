package net.arcor.bks.db;

import java.util.ArrayList;
import java.util.HashMap;

public interface FetchFunctionDetailsDAO  extends BksDataAccessObject {

	ArrayList<HashMap<String, Object>> getServiceSubscription(String servSubsId) throws Exception;

	ArrayList<HashMap<String, Object>> getConfiguredValues(String servSubsId) throws Exception;

	ArrayList<HashMap<String, Object>> getAccessNumberChars(String servSubsId) throws Exception;

	ArrayList<HashMap<String, Object>> getChildServices(String servSubsId) throws Exception;

	ArrayList<HashMap<String, Object>> getHardwareDepService(String parentServSubsId) throws Exception;

	ArrayList<HashMap<String, Object>> getAddress(String servSubsId) throws Exception;

	ArrayList<HashMap<String, Object>> getServiceSubscriptionSDC(String servSubsId) throws Exception;

	ArrayList<HashMap<String, Object>> getServiceForAccessNumber(String accnum) throws Exception;

	ArrayList<HashMap<String, Object>> getInitialTicketForService(String servSubsId) throws Exception;

	ArrayList<HashMap<String, Object>> getConfigCharacteristics(String servSubsId) throws Exception;

	ArrayList<HashMap<String, Object>> getCharValues(ArrayList<String> cscIdList) throws Exception;

}
