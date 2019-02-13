package net.arcor.bks.db.ibatis;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import javax.sql.PooledConnection;

import net.arcor.bks.client.DatabaseClientConfig;
import net.arcor.bks.common.BksDataException;
import net.arcor.bks.common.Cryptography;
import net.arcor.bks.db.AidaDAO;
import oracle.jdbc.pool.OracleConnectionPoolDataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AidaDAOImpl implements AidaDAO{

	protected final Log logger = LogFactory.getLog(getClass());
	private String schema="twh.";

	public Connection initDatabase(int retries,String databaseTag) throws Exception
	{   	  
		String driver = null;
		String connectString = null;
		String user = null;
		String password = null;
		Integer maxAidaConnectRetries = null;
		int ConnectionTimeOut=0;
		try {
			if (databaseTag.equals("mock"))
				schema="";
			driver = DatabaseClientConfig.getSetting("databaseclient.Driver");
			connectString = DatabaseClientConfig.getSetting("databaseclient."+databaseTag+".ConnectString");
			user = DatabaseClientConfig.getSetting("databaseclient."+databaseTag+".User");
			password = DatabaseClientConfig.getSetting("databaseclient."+databaseTag+".Password");
			String encryptionKey = DatabaseClientConfig.getSetting("databaseclient.EncryptionKey");
			password = Cryptography.ccbDecrypt(encryptionKey, password,true);
			maxAidaConnectRetries = 
				Integer.parseInt(DatabaseClientConfig.getSetting("databaseclient.maxAidaConnectRetries"));
			
		} catch (Exception e1) {
			logger.error(e1);
			throw new BksDataException("BKS0008","Fetching AIDA properties value failed :"+e1.getMessage());
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
        catch (Throwable e)
        {
        	logger.error("Oracle driver not avaliable");
        	return null;
        }
    	
    	Connection connection = null;
		try
    	{    
    		PooledConnection pc = ocpds.getPooledConnection();
    	    connection = pc.getConnection();
    	}
        catch (SQLException ex)           
        {
        	logger.error(ex.getMessage());
            if(retries >= maxAidaConnectRetries)
        		throw ex;
        }    	
        if(connection==null && retries ++ < maxAidaConnectRetries)
    		return initDatabase(retries,databaseTag);
        return connection;
   	}
	public HashMap<String, Object> getTechServRecByTSG(Connection connection, String technicalservicegroupid) throws SQLException {
		String sql = null;
		
		Statement st = null;
		HashMap<String, Object> aidaData = new HashMap<String, Object>();
		try
		{
			sql = "select tsg.customernumber, \n" +
			"    tsg.servicesubscription, \n" + 
			"    tsg.technicalservicegroupid \n" + 
			"from "+schema+"aidassctsg tsg \n" + 
			"where tsg.technicalservicegroupid='" + technicalservicegroupid+"'"; 
			st = connection.createStatement();
			ResultSet rs = st.executeQuery(sql);

			if (rs.next()) {
				do {   
					aidaData.put("ServiceSubscriptionId",rs.getString("servicesubscription"));
					aidaData.put("customerNumber",rs.getString("customernumber"));
					aidaData.put("V0154",rs.getString("technicalservicegroupid")); 
				} while (rs.next());   
				st.close();
				st = null;
			}
			return aidaData;
		}
		finally
		{
			if(st != null)
				st.close();
		}
	}
	public ArrayList<HashMap<String, Object>> getTechServRecByCustomer(Connection connection,String customerNumber) throws SQLException
	{
		String sql = null;
		
		Statement st = null;
		ArrayList<HashMap<String, Object>> aidaList=new ArrayList<HashMap<String,Object>>();
		try
		{
			sql = "select tsg.customernumber, \n" +
			"    tsg.servicesubscription, \n" + 
			"    tsg.technicalservicegroupid \n" + 
			"from "+schema+"aidassctsg tsg \n" + 
			"where tsg.customernumber='" + customerNumber+"'";
			st = connection.createStatement();
			ResultSet rs = st.executeQuery(sql);

			if (rs.next()) {
				do {   
					HashMap<String, Object> aidaData = new HashMap<String, Object>();
					aidaData.put("ServiceSubscriptionId",rs.getString("servicesubscription"));
					aidaData.put("customerNumber",rs.getString("customernumber"));
					aidaData.put("V0154",rs.getString("technicalservicegroupid")); 
					aidaList.add(aidaData);
				} while (rs.next());   
				st.close();
				st = null;
			}
			return aidaList;
		}
		finally
		{
			if(st != null)
				st.close();
		}
	}	
	
	public void getIpAddressInfo(Connection connection,String TSG,HashMap<String, Object> aidaData) throws SQLException
	{
		String sql = null;
		
		Statement st = null;
		try
		{
			sql = "select aaa.aaausername,\n" + 
			"    aaa.IPADDRESS " +
			"from "+schema+"aidasscaaa aaa where  aaa.technicalservicegroupid='" + TSG +"'"; 
			st = connection.createStatement();
			ResultSet rs = st.executeQuery(sql);

//			ArrayList<String> aaaUserList = new ArrayList<String>();
			if (rs.next()) {
				do {  
					if (rs.getString("IPADDRESS") != null){
						aidaData.put("I905B",rs.getString("IPADDRESS"));
						aidaData.put("I905A",rs.getString("aaausername"));
//						aaaUserList.add("Stat;"+rs.getString("aaausername"));
					} else {
						aidaData.put("I9058",rs.getString("aaausername"));
//						aaaUserList.add("Dyna;"+rs.getString("aaausername"));
					}
//					aidaData.put("I9058", aaaUserList);
				} while (rs.next());   
				st.close();
				st = null;
			}
		}
		finally
		{
			if(st != null)
				st.close();
		}
	}	
	
	public void getDeviceInfo(Connection connection,String TSG,HashMap<String, Object> aidaData) throws SQLException
	{
		String sql = null;
		
		Statement st = null;
		try
		{
			sql = "select dev.deviceid " +
			"from "+schema+"aidasscdevice dev where  dev.technicalservicegroupid='" + TSG+"'";
			st = connection.createStatement();
			ResultSet rs = st.executeQuery(sql);

			if (rs.next()) {
				do {   
					aidaData.put("V0153",rs.getString("deviceid"));
				} while (rs.next());   
				st.close();
				st = null;
			}
		}
		finally
		{
			if(st != null)
				st.close();
		}
	}	
	
	public void getIpTvInfo(Connection connection,String TSG,HashMap<String, Object> aidaData) throws SQLException
	{
		String sql = null;
		
		Statement st = null;
		try
		{
			sql = "select iptv.iptvaccountid " +
			"from "+schema+"aidassciptv iptv where iptv.technicalservicegroupid= '" + TSG+"'";
			st = connection.createStatement();
			ResultSet rs = st.executeQuery(sql);

			if (rs.next()) {
				do {   
					aidaData.put("I1330",rs.getString("iptvaccountid"));
				} while (rs.next());   
				st.close();
				st = null;
			}
		}
		finally
		{
			if(st != null)
				st.close();
		}
	}	
	
	public void getSipInfo(Connection connection,String TSG,HashMap<String, Object> aidaData) throws SQLException
	{
		String sql = null;
		
		Statement st = null;
		try
		{
			sql = "select sip.sipusername, \n" +
			"    sip.sipdomainname, \n" +
			"    case when sip.directorynumber is not null   or sip.localareacode is not null \n" +
			"      then (sip.countrycode||';'||sip.localareacode ||';'||sip.directorynumber)  \n" +   
			"      else null    \n" +
			"    end as accessnumber \n" +   
			"from "+schema+"aidasscsip sip where sip.technicalservicegroupid='" + TSG+"'";
			st = connection.createStatement();
			ResultSet rs = st.executeQuery(sql);

			ArrayList<String> accnumList = new ArrayList<String>();
			ArrayList<String> sipList = new ArrayList<String>();
			if (rs.next()) {
				do {   
					if (rs.getString("accessnumber")!= null){
						accnumList.add(rs.getString("accessnumber"));
					}
					aidaData.put("AccessNumbers", accnumList);
					if (rs.getString("sipusername")!=null && rs.getString("sipdomainname")!=null){
						sipList.add(rs.getString("sipusername")+"@"+rs.getString("sipdomainname"));
					}
					aidaData.put("VoipAccounts", sipList);					
				} while (rs.next());   
				st.close();
				st = null;
			}
		}
		finally
		{
			if(st != null)
				st.close();
		}
	}	
	
	
	public ArrayList<HashMap<String, Object>> loadAIDACustomer(Connection connection,String customerNumber) throws SQLException
	{
		String sql = null;
		
		Statement st = null;
		ArrayList<HashMap<String, Object>> aidaList=new ArrayList<HashMap<String,Object>>();
		try
		{
			sql = "select tsg.customernumber, \n" +
			"    tsg.servicesubscription, \n" + 
			"    tsg.technicalservicegroupid,\n" + 
			"    aaa.aaausername,\n" + 
			"    aaa.IPADDRESS,\n" + 
			"    dev.deviceid, \n" +
			"    iptv.iptvaccountid, \n" +
			"    sip.sipusername, \n" +
			"    sip.sipdomainname, \n" +
			"    case when sip.directorynumber is not null   or sip.localareacode is not null \n" +
			"      then (sip.countrycode||';'||sip.localareacode ||';'||sip.directorynumber)  \n" +   
			"      else null    \n" +
			"    end as accessnumber \n" +   
			//              "    sip||sip.localareacode ||';'||sip.directorynumber   as accessnumber \n" +   
			"from "+schema+"aidassctsg tsg \n" + 
			"     left join "+schema+"aidasscaaa aaa on aaa.technicalservicegroupid=tsg.technicalservicegroupid \n" + 
			"     left join "+schema+"aidasscdevice dev on dev.technicalservicegroupid=tsg.technicalservicegroupid \n" + 
			"     left join "+schema+"aidassciptv iptv on iptv.technicalservicegroupid=tsg.technicalservicegroupid \n" + 
			"     left join "+schema+"aidasscsip sip on sip.technicalservicegroupid=tsg.technicalservicegroupid \n" +               
			"where tsg.customernumber='" + customerNumber+"'\n" +
		    "and (aaa.technicalservicegroupid is null \n" + 
  		    "	     or aaa.indate = (select max(aaa1.indate) from "+schema+"aidasscaaa aaa1 \n" + 
  		    "	        where aaa1.technicalservicegroupid = aaa.technicalservicegroupid)) \n" + 
  		    "	and (iptv.technicalservicegroupid is null  \n" + 
  		    "	     or iptv.indate = (select max(iptv1.indate) from "+schema+"aidassciptv iptv1 \n" + 
  		    "	        where iptv1.technicalservicegroupid = iptv.technicalservicegroupid)) \n" + 
  		    "	and (dev.technicalservicegroupid is null  \n" + 
  		    "	     or dev.indate = (select max(dev1.indate) from "+schema+"aidasscdevice dev1 \n" + 
  		    "	        where dev1.technicalservicegroupid = dev.technicalservicegroupid))"; // \n" + 
			st = connection.createStatement();
			ResultSet rs = st.executeQuery(sql);

			if (rs.next()) {
				do {   
					HashMap<String, Object> aidaData = new HashMap<String, Object>();
					aidaData.put("ServiceSubscriptionId",rs.getString("servicesubscription"));
					aidaData.put("V0154",rs.getString("technicalservicegroupid")); 
					ArrayList<String> aaaUserList = new ArrayList<String>();
					if (rs.getString("aaausername")!= null){
						aaaUserList.add(rs.getString("aaausername"));
					}
					aidaData.put("I9058", aaaUserList);
//					aidaData.put("I9058",rs.getString("aaausername"));
					aidaData.put("I905B",rs.getString("IPADDRESS"));
					aidaData.put("V0153",rs.getString("deviceid"));
					aidaData.put("I1330",rs.getString("iptvaccountid"));
					ArrayList<String> accnumList = new ArrayList<String>();
					if (rs.getString("accessnumber")!= null){
						accnumList.add(rs.getString("accessnumber"));
					}
					aidaData.put("AccessNumbers", accnumList);
					ArrayList<String> sipList = new ArrayList<String>();
					if (rs.getString("sipusername")!=null && rs.getString("sipdomainname")!=null){
						sipList.add(rs.getString("sipusername")+"@"+rs.getString("sipdomainname"));
					}
					aidaData.put("VI011", sipList);					
					//		              aidaData.put("",rs.getString("sipdomainname"));
					if (!isTsgAlreadyHandled(aidaData,aidaList))
						aidaList.add(aidaData);
				} while (rs.next());   
				st.close();
				st = null;
			}
			return aidaList;
		}
		finally
		{
			if(st != null)
				st.close();
		}
	}	
	
	
	private boolean isTsgAlreadyHandled(HashMap<String, Object> oItem,
			ArrayList<HashMap<String, Object>> aidaList) {
		for (int i = 0; i < aidaList.size(); i++) {
			Object tsg = aidaList.get(i).get("V0154");
			if (tsg!=null && oItem.get("V0154") != null && ((String)tsg).equals((String)oItem.get("V0154"))){
				ArrayList<String> existAccnumList = (ArrayList<String>) aidaList.get(i).get("AccessNumbers");
				existAccnumList.addAll((ArrayList<String>) oItem.get("AccessNumbers"));
				ArrayList<String> existSipList = (ArrayList<String>) aidaList.get(i).get("VI011");
				existSipList.addAll((ArrayList<String>) oItem.get("VI011"));
				ArrayList<String> aaaUserList = (ArrayList<String>) aidaList.get(i).get("I9058");
				aaaUserList.addAll((ArrayList<String>) oItem.get("I9058"));
				return true;
			}
		}
		return false;
	}
	// loads record from AIDA using value from CCM	
	public String loadValueFromAIDA( Connection connection,String paramName,String paramValue) throws Exception
	{
	    if (paramName == null||paramValue==null)
	    	return null;
	
	    Statement st = null;
	    try
	    {
			String sql = null;
			String technicalservicegroupid = null;		
		    String tsgOut=null;
			
		    if(paramName.equals("SIP")){
				sql = "select sip.sipusername, sip.technicalservicegroupid, sip.serviceprovider from "+schema+"aidasscsip sip \n" +
					  " where sip.sipusername = '"+ paramValue + "'";
		    }else if(paramName.equals("IPTV")){
		        sql = "select iptv.iptvaccountid,iptv.technicalservicegroupid,iptv.serviceprovider from "+schema+"aidassciptv iptv \n" +
			          " where iptv.iptvaccountid = '" + paramValue + "'";
		    }else if(paramName.equals("DEVICEID")){
		        sql = "select dev.deviceid,dev.technicalservicegroupid,dev.serviceprovider from "+schema+"aidasscdevice dev \n" +
			          " where dev.deviceid = '" + paramValue + "'";
            }else if(paramName.equals("AAA")){
            	sql = "select aaa.aaausername,aaa.technicalservicegroupid,aaa.serviceprovider from "+schema+"aidasscaaa aaa \n" +
        	          " where aaa.aaausername= '" + paramValue + "'";
		    }else if(paramName.equals("TSG")){
			    sql = "select tsg.technicalservicegroupid,tsg.customernumber from "+schema+"aidassctsg tsg \n" +
				      "where tsg.technicalservicegroupid='" + paramValue+ "'";		
		    }
		    int i=0;
	
	        st = connection.createStatement();
	        ResultSet rs = st.executeQuery(sql);
	     
	        if (rs.next()) {   	             	
	         do {   
	        	 technicalservicegroupid = rs.getString("technicalservicegroupid");	
	        	 if(i==0 && technicalservicegroupid==null)			        		 
	        	     tsgOut = "";
	        	 else if(i==0 && technicalservicegroupid!=null)
	        	     tsgOut = technicalservicegroupid;
	        	 else
	        		 tsgOut = tsgOut + " " + technicalservicegroupid; 
	        	 i++;
	          } while (rs.next());   
	      } 
	      st.close();
	      st = null;
	      return tsgOut;
	    }
	    finally
	    {
	    	if(st != null)
	    		st.close();
	    }
	}
	public void closeConnection(Connection connection) throws Exception {
		connection.close();
	}	
}
