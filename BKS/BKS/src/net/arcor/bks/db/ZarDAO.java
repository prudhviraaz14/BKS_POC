package net.arcor.bks.db;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import net.arcor.bks.common.BksDataException;


public interface ZarDAO {
	Connection initDatabase() throws Exception;
	HashMap<String, ArrayList<String>> loadZARCustomer(Connection connection,String customerNumber) throws Exception;
	ArrayList<String> getZARAccessNumber(Connection connection,String customerNumber,String areaCode,String localNumber) throws Exception;
	void closeConnection(Connection connection) throws Exception;
	String retrievePortIdentifier(Connection connection, String areaCode,String start,String end) throws BksDataException;
}
