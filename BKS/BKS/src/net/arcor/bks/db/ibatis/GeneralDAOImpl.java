package net.arcor.bks.db.ibatis;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.orm.ibatis.SqlMapClientTemplate;

import net.arcor.bks.db.GeneralDAO;
public class GeneralDAOImpl extends BksDataAccessObjectImpl implements
		GeneralDAO {

	public ArrayList<HashMap<String, Object>> getRefData(String queryKey)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList(queryKey);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getReferenceData(
			String queryKey, String groupCode) throws Exception {
		SqlMapClientTemplate template = getSqlMapClientTemplate();
		List list = template.queryForList(queryKey,groupCode);
		return (ArrayList<HashMap<String, Object>>) list;
	}

	public void insertLog(String errorCode,String errorText, String status, String customerNumber,
			String accessNumber,String correlationId, String serviceName,String cacheHit ,
			Timestamp startTime,Timestamp endTime) {
		try {
	        SqlMapClientTemplate template = getSqlMapClientTemplate();
	        String error = null;
			if (errorCode != null && errorText != null)
				error = errorCode+" "+errorText;
	        BksTransactionLog bksTransactionLog = new BksTransactionLog(correlationId,serviceName,
	        		status,customerNumber,accessNumber,startTime,endTime,cacheHit,error);
			List list = template.queryForList("RetrieveBksLog",correlationId);
			if (list!=null&&list.size()>0)
				template.update("LogUpdate",bksTransactionLog);
			else
				template.insert("LogStatement",bksTransactionLog);
		} catch (Exception e) {
			logger.warn("Cannot insert into the log table.");
			logger.warn(e.getMessage());
		}
	}

	public void insertPkatLog(String itemType,String state, Timestamp createDate,Timestamp stateDate,String xmlString, BigDecimal updateNumber) {
		try {
	        SqlMapClientTemplate template = getSqlMapClientTemplate();
	        HashMap<String,Object> params = new HashMap<String, Object>();
	        params.put("itemType", itemType);
	        params.put("state", state);
	        params.put("createDate", createDate);
	        params.put("stateDate", stateDate);
	        params.put("xmlString", xmlString);
	        params.put("updateNumber", updateNumber);
	        
			template.insert("InsertPkatLog",params);
		} catch (Exception e) {
			logger.warn("Cannot insert into the log table.");
			logger.warn(e.getMessage());
		}
	}

	public ArrayList<HashMap<String, Object>> getReferenceDataForValue(
			String groupCode, String primaryValue) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,String> params = new HashMap<String, String>();
        params.put("groupCode", groupCode);
        params.put("primaryValue", primaryValue);
        List list = template.queryForList("databaseclient.CrossRefDataForValue",params);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getServiceName(String serviceCode)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("databaseclient.retrieveServiceName",serviceCode);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public void createErrorLogParam(String correlationId, String paramName,
			String paramValue, String data, Integer seqNumber) throws Exception {
		try{
			SqlMapClientTemplate template = getSqlMapClientTemplate();
			HashMap<String,Object> params = new HashMap<String, Object>();
			params.put("correlationId", correlationId);
			params.put("seqNumber", seqNumber);
			params.put("param", paramName);
			params.put("value", paramValue);
			params.put("data", data);
			List list = template.queryForList("RetrieveBksLogParam",params);
			if (list==null||list.size()==0)
				template.insert("InsertLogParam",params);
		} catch (Exception e) {
			// ignore
			logger.warn("Cannot insert into the log table.");
			logger.warn(e.getMessage());
		}
	}


}
