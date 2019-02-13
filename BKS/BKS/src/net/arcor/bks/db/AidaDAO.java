package net.arcor.bks.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public interface AidaDAO {
	Connection initDatabase(int retries,String databaseTag) throws Exception;
	ArrayList<HashMap<String, Object>> loadAIDACustomer(Connection connection,String customerNumber) throws SQLException;
	String loadValueFromAIDA( Connection connection,String paramName,String paramValue) throws Exception;
	void closeConnection(Connection connection) throws Exception;
//	ArrayList<HashMap<String, Object>> mockAida();
	HashMap<String, Object> getTechServRecByTSG(Connection connection,String tSG) throws Exception;
	ArrayList<HashMap<String, Object>> getTechServRecByCustomer(Connection connection, String customerNumber) throws Exception;
	void getIpAddressInfo(Connection connection,String tSG,HashMap<String, Object> aidaData) throws Exception;
	void getDeviceInfo(Connection connection,String tSG,HashMap<String, Object> aidaData) throws Exception;
	void getIpTvInfo(Connection connection,String tSG,HashMap<String, Object> aidaData) throws Exception;
	void getSipInfo(Connection connection,String tSG,HashMap<String, Object> aidaData) throws Exception;
}
