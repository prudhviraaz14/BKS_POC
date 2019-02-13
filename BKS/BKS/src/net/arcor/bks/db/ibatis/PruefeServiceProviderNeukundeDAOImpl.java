package net.arcor.bks.db.ibatis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.orm.ibatis.SqlMapClientTemplate;

import net.arcor.bks.db.PruefeServiceProviderNeukundeDAO;

public class PruefeServiceProviderNeukundeDAOImpl extends
		BksDataAccessObjectImpl implements PruefeServiceProviderNeukundeDAO {

	public ArrayList<HashMap<String, Object>> getAccessNumber(
			String countryCode, String cityCode, String localNumber)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,String> params = new HashMap<String, String>();
        params.put("shortnumber", countryCode.substring(2)+cityCode.substring(1)+localNumber.substring(0,3));
        params.put("accessnumber", countryCode.substring(2)+cityCode.substring(1)+localNumber);
        List list = template.queryForList("PruefeServiceProviderNeukunde."+dataSourceName + ".RetrieveAccessNumber",params);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getExactAccessNumber(
			String countryCode, String cityCode, String localNumber)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        String accessnumber = countryCode.substring(2)+cityCode.substring(1)+localNumber;
        List list = template.queryForList("PruefeServiceProviderNeukunde."+dataSourceName + ".RetrieveExactAccessNumber",accessnumber);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getRootCustomer(
			String retrievedCustomer) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("PruefeServiceProviderNeukunde."+dataSourceName + ".RetrieveRootCustomer",retrievedCustomer);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getTechServiceId(
			String serviceSubsId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("PruefeServiceProviderNeukunde."+dataSourceName + ".RetrieveTechServiceId",serviceSubsId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getContractData(String servSubsId)throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("PruefeServiceProviderNeukunde."+dataSourceName + ".RetrieveContractData",servSubsId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getEntityData(
			String customerNumber) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("PruefeServiceProviderNeukunde."+dataSourceName + ".RetrieveEntityData",customerNumber);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getServiceTicket(
			String servSubsId, String usageMode) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,String> params = new HashMap<String, String>();
        params.put("ServiceSubscriptionId", servSubsId);
        params.put("UsageMode", usageMode);
        List list = template.queryForList("PruefeServiceProviderNeukunde."+dataSourceName + ".RetrieveServiceTicket",params);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getExternalOrders(
			String servSubsId, String CustIntention) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,String> params = new HashMap<String, String>();
        params.put("ServiceSubscriptionId", servSubsId);
        params.put("CustIntention", CustIntention);
        List list = template.queryForList("PruefeServiceProviderNeukunde."+dataSourceName + ".RetrieveExternalOrders",params);
        return (ArrayList<HashMap<String, Object>>) list;
	}

}
