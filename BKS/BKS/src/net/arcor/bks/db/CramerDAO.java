package net.arcor.bks.db;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

public interface CramerDAO {
	Connection initDatabase() throws Exception;
	ArrayList<HashMap<String, Object>> getCramerDataByCustomerNumber(Connection connection,String customerNumber, ArrayList<String[]> internalErrorList);
	void closeConnection(Connection connection) throws Exception;
	void getCramerDataByPhoneNumber(Connection connection, String localAreaCode, 
			String pilotNumber,String string, ArrayList<HashMap<String, Object>> cramerData,
			ArrayList<String[]> internalErrorList) throws Exception;
	void getCramerDataByTasi(Connection connection, String tasi,
			HashMap<String, Object> cramerData,
			ArrayList<String[]> internalErrorList) throws Exception;
}
