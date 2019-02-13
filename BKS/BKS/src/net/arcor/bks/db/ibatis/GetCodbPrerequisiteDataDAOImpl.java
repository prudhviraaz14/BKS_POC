package net.arcor.bks.db.ibatis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.orm.ibatis.SqlMapClientTemplate;

import com.ibatis.sqlmap.client.SqlMapException;

import net.arcor.bks.db.GetCodbPrerequisiteDataDAO;

public class GetCodbPrerequisiteDataDAOImpl extends BksDataAccessObjectImpl
		implements GetCodbPrerequisiteDataDAO {

	public ArrayList<HashMap<String, Object>> getContractData(
			String serviceSubscriptionId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetCodbPrerequisiteData."+dataSourceName + ".RetrieveContractData",serviceSubscriptionId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getSignedContractData(
			String serviceSubscriptionId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        try {
			List list = template.queryForList("GetCodbPrerequisiteData."+dataSourceName + ".RetrieveSignedContractData",serviceSubscriptionId);
			return (ArrayList<HashMap<String, Object>>) list;
		} catch (SqlMapException e) {
			// ignore
		}
		return null;
	}

	public ArrayList<HashMap<String, Object>> getTariffOptions(
			String serviceSubscriptionId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetCodbPrerequisiteData."+dataSourceName + ".RetrieveTariffOptions",serviceSubscriptionId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getAccessNumbers(
			String serviceSubscriptionId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetCodbPrerequisiteData."+dataSourceName + ".RetrieveAccessNumbers",serviceSubscriptionId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

}
