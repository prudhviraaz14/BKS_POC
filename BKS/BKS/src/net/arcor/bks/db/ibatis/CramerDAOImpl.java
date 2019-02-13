package net.arcor.bks.db.ibatis;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.sql.PooledConnection;

import net.arcor.bks.client.DatabaseClientConfig;
import net.arcor.bks.common.BksDataException;
import net.arcor.bks.common.Cryptography;
import net.arcor.bks.db.CramerDAO;
import oracle.jdbc.pool.OracleConnectionPoolDataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CramerDAOImpl implements CramerDAO{

//	private String number = null;
//	private String startRange = null;
//	private String endRange = null;
	private static final String SCHEMA = "DIM_INT_FAULT_MGMT";
	protected final Log logger = LogFactory.getLog(getClass());
	final static HashMap<String , String> relevantFields = new HashMap<String , String>() {
		private static final long serialVersionUID = 1L;
		{ 
			put("Service-SubscriptionID","ServiceSubscriptionId");
			put("TASI","V0152");
			put("Carrier-Typ","V0138");
			put("ASB","V0934");
			put("LINE_ID","I1020");
			put("Downstream-Bandbreite","bandwidth");
			put("Upstream-Bandbreite","UpstreamBandwidth");
			put("Customer Number","customerNumber");
			put("TAL-Vertragsnummer","DtagContractNumber");
			put("TAL-Leitungsbezeichnung","LSZ");
			put("Rufnummern","AccessNumbers");
		}
	};	
	public Connection initDatabase() throws Exception
	{   	  
		Connection connectionToCramer = null;
		String driver = null;
		String connectString = null;
		String user = null;
		String password = null;
		int ConnectionTimeOut=0;
		try {
			driver = DatabaseClientConfig.getSetting("databaseclient.Driver");
			connectString = DatabaseClientConfig.getSetting("databaseclient.cramer.ConnectString");
			user = DatabaseClientConfig.getSetting("databaseclient.cramer.User");
			password = DatabaseClientConfig.getSetting("databaseclient.cramer.Password");
			String encryptionKey = DatabaseClientConfig.getSetting("databaseclient.EncryptionKey");
			password = Cryptography.ccbDecrypt(encryptionKey, password,true);
		    
		} catch (Exception e1) {
			logger.error(e1);
			throw new BksDataException("BKS0008","Fetching Cramer properties value failed :"+e1.getMessage());
		}
   	    try{
   	    	ConnectionTimeOut = Integer.parseInt(DatabaseClientConfig.getSetting("databaseclient.ConnectionTimeOut"));
   	    }catch(Exception e){
   	    
   	    }
    	
		OracleConnectionPoolDataSource ocpds = new OracleConnectionPoolDataSource();
    	try{
    		
    		Class.forName(driver).newInstance();           
    	    ocpds.setURL(connectString);
    	    ocpds.setUser(user);
    	    ocpds.setPassword(password);
    	    ocpds.setLoginTimeout(ConnectionTimeOut);
    	}
    	
    	catch (Exception e)
        {
        	throw e;
        }
   
    	try
    	{    
    		PooledConnection pc = ocpds.getPooledConnection();
    	    connectionToCramer = pc.getConnection();
    	}
        catch (SQLException e)
        {
        	logger.error("Cannot connect to Cramer database.");
        	throw e;
        }
    	return connectionToCramer;
   	}
	private List<Object[]> reformatCramerQueryResult(ResultSet oResultSet) throws SQLException 
	{
		List<Object[]>rsl = new ArrayList<Object[]>();
		if (oResultSet.next())
		{
			do
			{   
				Struct obj = (Struct)oResultSet.getObject(2);
				rsl.add(obj.getAttributes());
			} while (oResultSet.next());
		}
		return rsl;
	}
	private String changeToCcmFormat(String oNumberStart, String oNumberEnd)
	{
		if(oNumberEnd == null || oNumberStart.equals(oNumberEnd))
			return oNumberStart;
		else
		{
			String anr = "";
			int i = 0;
			while (oNumberStart.length()>i && oNumberEnd.length()>i && 
				  oNumberStart.charAt(i)==oNumberEnd.charAt(i)) {
				anr += oNumberStart.charAt(i++);
			}
			return anr + ";" + oNumberStart.substring(i, oNumberStart.length())
					+ ";" + oNumberEnd.substring(i, oNumberEnd.length());
		}
	}
	private CallableStatement retrieveAccessNumber(Connection connection, String areaCode,String number) throws SQLException
	{
		CallableStatement cstmt = null;

		try
		{
			cstmt = connection.prepareCall("{call " + SCHEMA + ".pkg_int_net_info.get_by_phone_number( ?, ?, ?, ?, ?, ?, ?, ?, ?) " + " }");

			cstmt.setString(1, "BKS");
			cstmt.setString(2, "JDBC-Test");
			cstmt.setString(3, areaCode);
			cstmt.setString(4, number);
			cstmt.setNull(5, java.sql.Types.VARCHAR);

			// Register the out parameters
			cstmt.registerOutParameter(6, java.sql.Types.ARRAY, SCHEMA + ".T_NET_INFO");
			cstmt.registerOutParameter(7, java.sql.Types.ARRAY, SCHEMA + ".T_NET_INFO");
			cstmt.registerOutParameter(8, java.sql.Types.VARCHAR);
			cstmt.registerOutParameter(9, java.sql.Types.VARCHAR);
		}
		catch(Throwable ex)
		{
			if(cstmt != null)
				cstmt.close();

			cstmt = null;
		}

		return cstmt;
	}

	private CallableStatement retrieveAccessNumberRange(Connection connection, String areaCode,String start,String end) throws SQLException
	{
		CallableStatement cstmt = null;

		try
		{
			cstmt = connection.prepareCall("{call " + SCHEMA + ".pkg_int_net_info.get_by_phone_number_block( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " + " }");

			cstmt.setString(1, "BKS");
			cstmt.setString(2, "JDBC-Test");
			cstmt.setString(3, areaCode);

			while(start.length() < end.length())
				start += "0";

			cstmt.setString(4, start);
			cstmt.setString(5, end);
			cstmt.setNull(6, java.sql.Types.VARCHAR);

			// Register the out parameters
			cstmt.registerOutParameter(7, java.sql.Types.ARRAY, SCHEMA + ".T_NET_INFO");
			cstmt.registerOutParameter(8, java.sql.Types.ARRAY, SCHEMA + ".T_NET_INFO");
			cstmt.registerOutParameter(9, java.sql.Types.VARCHAR);
			cstmt.registerOutParameter(10, java.sql.Types.VARCHAR);
		}
		catch(Throwable ex)
		{
			if(cstmt != null)
				cstmt.close();

			cstmt = null;
			throw new NullPointerException(ex.getMessage());
		}

		return cstmt;
	}

	public ArrayList<HashMap<String, Object>> getCramerDataByCustomerNumber(
			Connection connection,String customerNumber, ArrayList<String[]> internalErrorList) {
		String oReturnCode      = null;
		String oErrorText       = null;
		CallableStatement cstmt = null;
		ArrayList<HashMap<String, Object>> cramerList = new ArrayList<HashMap<String,Object>>();
		try
		{
			cstmt = connection.prepareCall(
					"{call " + SCHEMA + ".pkg_int_net_info_query.get_by_customer_number(?, ?, ?, ?, ?, ?) }");

			// Set the in parameters
			cstmt.setString(1, "JDBC-Test");
			cstmt.setNull(2,java.sql.Types.DOUBLE);
			cstmt.setString(3, customerNumber);

			// Register the out parameters
			cstmt.registerOutParameter(4, java.sql.Types.ARRAY, SCHEMA + ".T_NET_INFO_QUERY");
			cstmt.registerOutParameter(5, java.sql.Types.VARCHAR);
			cstmt.registerOutParameter(6, java.sql.Types.VARCHAR);

			// Execute the stored procedure
			cstmt.execute();

			// Get the returned values
			oReturnCode = cstmt.getString(5);
			oErrorText  = cstmt.getString(6);

			if (oReturnCode != null)
			{
				internalErrorList.add(new String[] {"Cramer",customerNumber,oReturnCode,oErrorText});
				return null;
			}
			List<Object[]>rsl = reformatCramerQueryResult(cstmt.getArray(4).getResultSet());
			ArrayList<String> handledNumbers=new ArrayList<String>();
			for(Object[] row : rsl)
			{
				if (handledNumbers.contains((String)row[0]+(String)row[1]+(String)row[2]))
					continue;
				handledNumbers.add((String)row[0]+(String)row[1]+(String)row[2]);
				getCramerDataByPhoneNumber(connection,(String)row[0],(String)row[1],(String)row[2],cramerList,internalErrorList);
			}
		}
		catch (SQLException ex)
		{
			logger.error(ex.getMessage());
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
		finally
		{
			if(cstmt != null)
			{
				try
				{
					cstmt.close();
				}
				catch (SQLException e)
				{
				}
			}
		}

		return cramerList;
	}
	public void getCramerDataByPhoneNumber(Connection connection, String areaCode,String start,String end, ArrayList<HashMap<String,Object>> cramerList, ArrayList<String[]> internalErrorList)
	{
		String oReturnCode      = null;
		String oErrorText       = null;
		CallableStatement cstmt = null;
		if (areaCode==null || start==null)
			return;
		try
		{
				// This is the starting index where the out parameters
				// are starting. This is assuming the following layout:
				// 0 = oFixTab
				// 1 = oVarTab		
				// 2 = return code
				// 3 = error text
				// If the return parameters are different, it must be
				// be handled completely on it's own. 
				String ccmAccessNumber = "49;"+areaCode.substring(1)+";"+
				                         changeToCcmFormat(start,end);
				int base = 6;
				if(end==null||end.equals(start))
				{
					logger.info("Query by accessnumber: 49;"+areaCode+";"+start);
					cstmt = retrieveAccessNumber(connection,areaCode,start);
				}
				else
				{
					logger.info("Query by accessnumber range: 49;"+areaCode+";"+start+";"+end);
					base = 7;
					cstmt = retrieveAccessNumberRange(connection,areaCode,start,end);
				}

				// Execute the stored procedure
				cstmt.execute();

				// Get the returned values
				oReturnCode = cstmt.getString(base+2);
				oErrorText  = cstmt.getString(base+3);

				if (oReturnCode != null || oErrorText!=null)
				{
					internalErrorList.add(new String[] {"Cramer",ccmAccessNumber,oReturnCode,oErrorText});
					return;
				}

				HashMap<String, Object> oItem = new HashMap<String, Object>();
				ArrayList<String> accnumList = new ArrayList<String>();
				accnumList.add(ccmAccessNumber);
				oItem.put("AccessNumbers", accnumList);
				// FixTab
				List<Object[]>rsl = reformatCramerQueryResult(cstmt.getArray(base+0).getResultSet());
				updateDataItem(rsl, oItem);

				// VarTab
				rsl = reformatCramerQueryResult( cstmt.getArray(base+1).getResultSet());
				updateDataItem(rsl, oItem);
				if (!isTasiAlreadyHandled(oItem,cramerList))
					cramerList.add(oItem);
					
		}
		catch (SQLException ex)
		{
			logger.error(ex.getMessage());
			throw new RuntimeException(ex);
		}
		finally
		{
			if(cstmt != null)
			{
				try
				{
					cstmt.close();
				}
				catch (SQLException e)
				{
				}
			}
		}

		return;
	}
	private boolean isTasiAlreadyHandled(HashMap<String, Object> oItem,
			ArrayList<HashMap<String, Object>> cramerList) {
		for (int i = 0; i < cramerList.size(); i++) {
			Object serSuId = cramerList.get(i).get("V0152");
			if (serSuId!=null && oItem.get("V0152") != null && 
				((String)serSuId).equals((String)oItem.get("V0152"))){
				ArrayList<String> existAccnumList = (ArrayList<String>) cramerList.get(i).get("AccessNumbers");
				String accnum = ((ArrayList<String>)oItem.get("AccessNumbers")).get(0);
				if (existAccnumList==null){
					existAccnumList = new ArrayList<String>();
					cramerList.get(i).put("AccessNumbers",existAccnumList);
				}
					
				if (!existAccnumList.contains(accnum))
					existAccnumList.add(accnum);
				return true;
			}
		}
		return false;
	}
	private void updateDataItem(List<Object[]> rsl,	HashMap<String,Object> oItem) {
		boolean isDslLine=false;
		for(Object[] row : rsl)
		{
			String parName = (String)row[1];
			String parValue = (String)row[2];
			logger.debug("name "+parName+" value "+parValue);
			if (relevantFields.keySet().contains(parName)) {
				if (parName.equals("TAL-Leitungsbezeichnung") && parValue!=null)
					isDslLine=parValue.startsWith("96W");
				if (parName.equals("TAL-Vertragsnummer")){
					if (parValue!=null) {
						if (oItem.get(relevantFields.get(parName)) == null)
							oItem.put(relevantFields.get(parName),new ArrayList<String>());
						ArrayList<String> l = (ArrayList<String>) oItem.get(relevantFields.get(parName));
						if (isDslLine)
							l.add(0, parValue);
						else
							l.add(parValue);
						isDslLine = false;
					}
                } else if (parName.equals("Rufnummern")){
                    if (parValue!=null) {
                          ArrayList<String> accNumList = new ArrayList<String>();
                          String[] cramerNums = parValue.split("\\$\\$\\$");
                          for(int i=0;i<cramerNums.length;i++){
                                 int separateAreaInd=cramerNums[i].indexOf("/");
                                 int separateRangeInd=cramerNums[i].indexOf("-");
                                 String areaCode = cramerNums[i].substring(1,separateAreaInd);
                                 if (separateRangeInd>=0){
                                        String startNum = cramerNums[i].substring(separateAreaInd+1,separateRangeInd);
                                        String endNum = cramerNums[i].substring(separateRangeInd+1);
                                        int j=0;
                                        for (;j<startNum.length()&&startNum.charAt(j)==endNum.charAt(j);j++);
                                        String range = "49;"+areaCode+";"+startNum.substring(0,j)+";"+startNum.substring(j)+";"+endNum.substring(j);
                                        accNumList.add(range);
                                 } else {
                                        String pilot = cramerNums[i].substring(separateAreaInd+1);
                                        accNumList.add("49;"+areaCode+";"+pilot);
                                 }
                          }
                          oItem.put(relevantFields.get(parName),accNumList);
                    }
				} else if (parValue!=null)
					oItem.put (relevantFields.get(parName),parValue);
			}
		}
	}
	public void closeConnection(Connection connection) throws Exception {
		connection.close();
	}
	public void getCramerDataByTasi(Connection connection, String tasi,
			HashMap<String, Object> cramerData,
			ArrayList<String[]> internalErrorList) throws Exception {
		String oReturnCode      = null;
		String oErrorText       = null;
		CallableStatement cstmt = null;
		if (tasi==null)
			return;
		try
		{
			// This is the starting index where the out parameters
			// are starting. This is assuming the following layout:
			// 0 = oFixTab
			// 1 = oVarTab		
			// 2 = return code
			// 3 = error text
			// If the return parameters are different, it must be
			// be handled completely on it's own. 

			cstmt = connection.prepareCall("{call " + SCHEMA + ".pkg_int_net_info.get_by_tasi(?, ?, ?, ?, ?, ?, ?, ?) " + " }");

			cstmt.setString(1, "BKS");
			cstmt.setString(2, "JDBC-Test");
			cstmt.setString(3, tasi);
			cstmt.setNull(4, java.sql.Types.VARCHAR);

			// Register the out parameters
			cstmt.registerOutParameter(5, java.sql.Types.ARRAY, SCHEMA + ".T_NET_INFO");
			cstmt.registerOutParameter(6, java.sql.Types.ARRAY, SCHEMA + ".T_NET_INFO");
			cstmt.registerOutParameter(7, java.sql.Types.VARCHAR);
			cstmt.registerOutParameter(8, java.sql.Types.VARCHAR);
			// Execute the stored procedure
			cstmt.execute();

			// Get the returned values
			oReturnCode = cstmt.getString(7);
			oErrorText  = cstmt.getString(8);

			if (oReturnCode != null || oErrorText!=null)
			{
				internalErrorList.add(new String[] {"Cramer",tasi,oReturnCode,oErrorText});
				return;
			}

			// FixTab
			List<Object[]>rsl = reformatCramerQueryResult(cstmt.getArray(5).getResultSet());
			updateDataItem(rsl, cramerData);

			// VarTab
			rsl = reformatCramerQueryResult( cstmt.getArray(6).getResultSet());
			updateDataItem(rsl, cramerData);
		}
		catch (SQLException ex)
		{
			logger.error(ex.getMessage());
			throw new RuntimeException(ex);
		}
		finally
		{
			if(cstmt != null)
			{
				try
				{
					cstmt.close();
				}
				catch (SQLException e)
				{
				}
			}
		}

		return;
	}	

}
