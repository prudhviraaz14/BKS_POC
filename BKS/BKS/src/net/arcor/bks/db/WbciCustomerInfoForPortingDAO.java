package net.arcor.bks.db;

import java.util.ArrayList;
import java.util.HashMap;

public interface WbciCustomerInfoForPortingDAO extends BksDataAccessObject{

	ArrayList<HashMap<String, Object>> getCustomerDataByAccessNumber(String accessNumber)throws Exception;

	ArrayList<HashMap<String, Object>> getAccessNumberForSerSu(String serSuId, String status)throws Exception;

	ArrayList<HashMap<String, Object>> getConfigValueForSerSu(String serSuId)throws Exception;

	ArrayList<HashMap<String, Object>> getAddressForSerSu(String serSuId)throws Exception;

	ArrayList<HashMap<String, Object>> getRootCustomer(String custNum)throws Exception;

	ArrayList<HashMap<String, Object>> getExternalOrder(String serSuId,String intention)throws Exception;

	ArrayList<HashMap<String, Object>> getInternalOrder(String serSuId,String usageMode)throws Exception;

	ArrayList<HashMap<String, Object>> getBundleData(String serSuId)throws Exception;

	ArrayList<HashMap<String, Object>> getOrderFormData(String serSuId)throws Exception;

	ArrayList<HashMap<String, Object>> getServDeliveContData(String serSuId)throws Exception;

	ArrayList<HashMap<String, Object>> getCustomerByName(String name1,String name2,String forename1,String forename2,
														String postalCode,String street1,String street2, String city1, String city2)throws Exception;

	ArrayList<HashMap<String, Object>> getBundleInfoForCustomer(String custNum)throws Exception;

	ArrayList<HashMap<String, Object>> getBundleDataByBundleId(String bundleId)throws Exception;

	ArrayList<HashMap<String, Object>> getCompanyByName(String name1,String name2, String companySuffix1, String companySuffix2,
												String postalCode, String street1, String street2, 
												String city1, String city2)throws Exception;

	ArrayList<HashMap<String, Object>> getChildService(String serSuId)throws Exception;

	ArrayList<HashMap<String, Object>> getDeactRecByAccessNumber(String accNum)throws Exception;

	String getZarStatus(String custNumber, String areaCode, String accessNumber) throws Exception;
}
