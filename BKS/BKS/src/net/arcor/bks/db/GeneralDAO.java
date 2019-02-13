package net.arcor.bks.db;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

public interface GeneralDAO extends BksDataAccessObject {

	ArrayList<HashMap<String, Object>> getRefData(String queryKey) throws Exception;

	ArrayList<HashMap<String, Object>> getReferenceData(String queryKey,String groupCode)throws Exception;

	public void insertLog(String errorCode,String errorText, 
			String status, String customerNumber,
			String accessNumber,
            String correlationId, String serviceName,
            String cacheHit ,Timestamp startTime,
            Timestamp endTime);

	public void insertPkatLog(String itemType,String state, Timestamp createDate,Timestamp stateDate,String xmlString, BigDecimal updateNumber);
	
	ArrayList<HashMap<String, Object>> getReferenceDataForValue(String groupCode,String primaryValue)throws Exception;

	ArrayList<HashMap<String, Object>> getServiceName(String serviceCode)throws Exception;

	void createErrorLogParam(String correlationId,String paramName, String paramValue, String data,Integer seqNumber)throws Exception;
}
