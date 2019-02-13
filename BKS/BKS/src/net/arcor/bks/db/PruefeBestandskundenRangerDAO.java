package net.arcor.bks.db;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;

public interface PruefeBestandskundenRangerDAO extends BksDataAccessObject {

	ArrayList<HashMap<String, Object>> getAccessNumber(String countryCode,
			String cityCode, String localNumber) throws Exception;

	ArrayList<HashMap<String, Object>> getCustomer(String firstName,
			String lastName, String cscIdList, String custNumList) throws Exception;

	ArrayList<HashMap<String, Object>> getRootCustomer(String customerNumber) throws Exception;

	ArrayList<HashMap<String, Object>> getOFContractData(String servSubId) throws Exception;
	ArrayList<HashMap<String, Object>> getSDContractData(String servSubId) throws Exception;

	Date getOFEndDate(String cntNumber) throws Exception;

	Date getSDEndDate(String cntNumber) throws Exception;

	ArrayList<HashMap<String, Object>> getBundledService(String servSubId,	String serviceCodes) throws Exception;
}
