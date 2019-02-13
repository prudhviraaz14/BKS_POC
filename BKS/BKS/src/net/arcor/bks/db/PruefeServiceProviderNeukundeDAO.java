package net.arcor.bks.db;

import java.util.ArrayList;
import java.util.HashMap;


public interface PruefeServiceProviderNeukundeDAO extends BksDataAccessObject {

	ArrayList<HashMap<String, Object>> getExactAccessNumber(String countryCode,
			String cityCode, String localNumber)throws Exception;

	ArrayList<HashMap<String, Object>> getAccessNumber(String countryCode,
			String cityCode, String localNumber)throws Exception;

	ArrayList<HashMap<String, Object>> getRootCustomer(String retrievedCustomer) throws Exception;

	ArrayList<HashMap<String, Object>> getTechServiceId(String serviceSubsId) throws Exception;

	ArrayList<HashMap<String, Object>> getContractData(String servSubsId) throws Exception;

	ArrayList<HashMap<String, Object>> getEntityData(String customerNumber) throws Exception;

	ArrayList<HashMap<String, Object>> getServiceTicket(String servSubsId,String usageMode) throws Exception;

	ArrayList<HashMap<String, Object>> getExternalOrders(String servSubsId,	String CustIntention) throws Exception;

}
