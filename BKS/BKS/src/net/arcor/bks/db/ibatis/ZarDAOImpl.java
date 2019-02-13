package net.arcor.bks.db.ibatis;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;

import javax.sql.PooledConnection;

import net.arcor.bks.client.DatabaseClientConfig;
import net.arcor.bks.common.BksDataException;
import net.arcor.bks.common.Cryptography;
import net.arcor.bks.db.ZarDAO;
import oracle.jdbc.pool.OracleConnectionPoolDataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ZarDAOImpl implements ZarDAO{

    final static HashMap<Integer , String> zarErrorMap = new HashMap<Integer , String>() {
    	private static final long serialVersionUID = 1L;
    	{ 
     		put(30014,"BKS0064");
     		put(30025,"BKS0064");
     		put(30023,"BKS0064");
     		put(30027,"BKS0064");
     		put(1537,"BKS0065");
     		put(1511,"BKS0065");
     		put(1513,"BKS0065");
     		put(1514,"BKS0065");
     		put(1531,"BKS0066");
     		put(1542,"BKS0065");
     		put(1500,"BKS0042");
    	}
    }; 
	protected final Log logger = LogFactory.getLog(getClass());
	public Connection initDatabase() throws Exception
	{   	  
		String driver = null;
		String connectString = null;
		String user = null;
		String password = null;
		int ConnectionTimeOut=0;
		try {
			driver = DatabaseClientConfig.getSetting("databaseclient.Driver");
			connectString = DatabaseClientConfig.getSetting("databaseclient.zar.ConnectString");
			user = DatabaseClientConfig.getSetting("databaseclient.zar.User");
			password = DatabaseClientConfig.getSetting("databaseclient.zar.Password");
			String encryptionKey = DatabaseClientConfig.getSetting("databaseclient.EncryptionKey");
			password = Cryptography.ccbDecrypt(encryptionKey, password,true);
			
		} catch (Exception e1) {
			logger.error(e1);
			throw new BksDataException("BKS0008","Fetching Zar properties value failed :"+e1.getMessage());
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
    	
    	Connection connectionToZar=null;
		try
    	{    
    		PooledConnection pc = ocpds.getPooledConnection();
    	    connectionToZar= pc.getConnection();
    	}
        catch (SQLException e)
        {
        	logger.error("Cannot connect to Zar database.");
        	throw e;
        }
        return connectionToZar;
   	}
	public HashMap<String, ArrayList<String>> loadZARCustomer(Connection connection,String customerNumber) throws SQLException
	{
		String sql = null;
		
		Statement st = null;
		HashMap<String, ArrayList<String>> zarCustomerMap=new HashMap<String,ArrayList<String>>();
		try
		{
			sql = "select *  \n" +
                  "from zaradm_2.tab_ruf t \n" +
                  "where t.col_ruf_kundennummer = '" + customerNumber+"' \n" +
                  "and \n" +
                  "( \n" +
                  "	( \n" +
                  "		t.col_ruf_status = '3' \n" +
                  "		and t.col_ruf_logical_status not in ('14', '20') \n" +
                  "	) \n" +
                  "	or \n" +
                  "	( \n" +
                  "		t.col_ruf_status = '4' \n" +
                  "		and t.col_ruf_logical_status in ('13', '14', '21') \n" +
                  "	) \n" +
                  "	or \n" +
                  "	( \n" +
                  "		t.col_ruf_status = '7' \n" +
                  "		and t.col_ruf_logical_status in ('17', '24', '25') \n" +
                  "	) \n" +
                  "	or \n" +
                  "	( \n" +
                  "		t.col_ruf_status = '8' \n" +
                  "		and t.col_ruf_logical_status in ('17', '18', '25') \n" +
                  "	) \n" +
                  ") \n";

			st = connection.createStatement();
			ResultSet rs = st.executeQuery(sql);

			if (rs.next()) {
				do {   
					String groupId = rs.getString("COL_RUF_ISDN_BASIS");
					if (groupId==null)
						groupId="UNIQUE_NULL_REPLACEMENT";
					if (!zarCustomerMap.containsKey(groupId))
						zarCustomerMap.put(groupId,new ArrayList<String>());
					parseZARNumber(rs,zarCustomerMap.get(groupId));
				} while (rs.next());   
				st.close();
				st = null;
			}
			return zarCustomerMap;
		}
		finally
		{
			if(st != null)
				st.close();
		}
	}	
	public ArrayList<String> getZARAccessNumber(Connection connection,String customerNumber,String areaCode,String localNumber) throws SQLException
	{
		String sql = null;
		
		Statement st = null;
		ArrayList<String> zarAccNumCustList=new ArrayList<String>();
		try
		{
			sql = "select distinct t.col_ruf_kundennummer, t.col_ruf_status, t.col_ruf_logical_status, \n" +
				  "t.col_ruf_isdn_basis as access_number_group_id\n"+
			      "from zaradm_2.tab_ruf t\n"+
				  "where t.col_onb_vorwahl = '0" + areaCode+"'\n"+
				  "and t.col_rnb_nummer_start||t.col_ruf_start like '" + localNumber +"'\n"+
				  "and (t.col_ruf_kundennummer is null\n"+
				  "     or t.col_ruf_kundennummer != '" + customerNumber+"')\n" +
                  "and \n" +
                  "( \n" +
                  "	( \n" +
                  "		t.col_ruf_status = '3' \n" +
                  "		and t.col_ruf_logical_status not in ('14', '20') \n" +
                  "	) \n" +
                  "	or \n" +
                  "	( \n" +
                  "		t.col_ruf_status = '4' \n" +
                  "		and t.col_ruf_logical_status in ('13', '14', '21') \n" +
                  "	) \n" +
                  "	or \n" +
                  "	( \n" +
                  "		t.col_ruf_status = '7' \n" +
                  "		and t.col_ruf_logical_status in ('17', '24', '25') \n" +
                  "	) \n" +
                  "	or \n" +
                  "	( \n" +
                  "		t.col_ruf_status = '8' \n" +
                  "		and t.col_ruf_logical_status in ('17', '18', '25') \n" +
                  "	) \n" +
                  ") \n";

			st = connection.createStatement();
			ResultSet rs = st.executeQuery(sql);

			if (rs.next()) {
				do {   
					String custNo = rs.getString("col_ruf_kundennummer");
					if (!zarAccNumCustList.contains(custNo))
						zarAccNumCustList.add(custNo);
					
				} while (rs.next());   
				st.close();
				st = null;
			}
			return zarAccNumCustList;
		}
		finally
		{
			if(st != null)
				st.close();
		}
	}	
	private void parseZARNumber(ResultSet rs, ArrayList<String> accNumList) throws SQLException
	{
		String accessNumber="49;";
		String start = rs.getString("COL_ONB_VORWAHL");
		accessNumber+=start.substring(1, start.length());

		// Now we retrieve the base number
		start = rs.getString("COL_RUF_START");
		String end = rs.getString("COL_RUF_ENDE");
		String rnb_start = rs.getString("COL_RNB_NUMMER_START");
		String rnb_end = rs.getString("COL_RNB_NUMMER_ENDE");

		if(rnb_start != null)
		{
			start = rnb_start+start;
			end = rnb_end+end;
		}

		accessNumber +=";"+changeToCcmFormat(start,end);
		logger.info("zar access Number : "+accessNumber);
		accNumList.add(accessNumber);
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
	public String retrievePortIdentifier(Connection connection, String areaCode,String start,String end) throws BksDataException
	{
		CallableStatement cstmt = null;
		Integer oReturnCode = 0;
		String oPortIdent = null;
		String oErrorText = null;
		try
		{
			cstmt = connection.prepareCall("{ ? = call zaradm_2.pkg_fe.retrieve_ruf_holder_fe( ?, ?, ?, ?, ?, ?, ?, ?, ?)  }");

			cstmt.registerOutParameter(1,Types.INTEGER);
			cstmt.setString(2, "OPM");
			cstmt.setString(3, "0"+areaCode);
			cstmt.setString(4, start);
			cstmt.setString(5, end);
			cstmt.setString(6, "2");
			cstmt.setString(7, "1");

			// Register the out parameters
			cstmt.registerOutParameter(8, Types.VARCHAR);
			cstmt.registerOutParameter(9, Types.DATE);
			cstmt.registerOutParameter(10, Types.VARCHAR);
			
			cstmt.execute();
			
			oReturnCode = cstmt.getInt(1);
			oPortIdent = cstmt.getString(8);
			oErrorText = cstmt.getString(10);
			
		}
		catch (SQLException e)
		{
			throw new BksDataException("BKS0044","Not possible to reach ZAR");
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

		if (oReturnCode!=0 && oErrorText != null){
			String code = (zarErrorMap.get(oReturnCode)!=null)?zarErrorMap.get(oReturnCode):"BKS0064";
			throw new BksDataException(code,oErrorText);
		}
		return oPortIdent;
	}

	public void closeConnection(Connection connection) throws Exception {
		connection.close();
	}	
}
