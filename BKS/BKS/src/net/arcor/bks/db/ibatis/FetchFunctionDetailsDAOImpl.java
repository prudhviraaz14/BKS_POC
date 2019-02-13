package net.arcor.bks.db.ibatis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.orm.ibatis.SqlMapClientTemplate;

import net.arcor.bks.db.FetchFunctionDetailsDAO;

public class FetchFunctionDetailsDAOImpl extends BksDataAccessObjectImpl
		implements FetchFunctionDetailsDAO {

	public ArrayList<HashMap<String, Object>> getAccessNumberChars(
			String servSubsId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("FetchFunctionDetails."+dataSourceName + ".RetrieveAccessNumberChars",servSubsId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getConfiguredValues(
			String servSubsId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("FetchFunctionDetails."+dataSourceName + ".RetrieveConfiguredValues",servSubsId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getServiceSubscription(
			String servSubsId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("FetchFunctionDetails."+dataSourceName + ".RetrieveServiceSubscription",servSubsId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getChildServices(
			String servSubsId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("FetchFunctionDetails."+dataSourceName + ".RetrieveChildServices",servSubsId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getHardwareDepService(
			String servSubsId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("FetchFunctionDetails."+dataSourceName + ".RetrieveHardwareDepService",servSubsId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getAddress(String servSubsId)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("FetchFunctionDetails."+dataSourceName + ".RetrieveAddress",servSubsId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getServiceSubscriptionSDC(
			String servSubsId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("FetchFunctionDetails."+dataSourceName + ".RetrieveServiceSubscriptionSDC",servSubsId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getServiceForAccessNumber(
			String accnum) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("FetchFunctionDetails."+dataSourceName + ".RetrieveServiceForAccessNumber",accnum);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getInitialTicketForService(
			String servSubsId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("FetchFunctionDetails."+dataSourceName + ".RetrieveInitialTicketForService",servSubsId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getConfigCharacteristics(
			String servSubsId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("FetchProductBundle."+dataSourceName + ".RetrieveConfigCharacteristics",servSubsId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getCharValues(
			ArrayList<String> cscIdList) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("FetchProductBundle."+dataSourceName + ".RetrieveCharValues",cscIdList);
        return (ArrayList<HashMap<String, Object>>) list;
	} 

}
