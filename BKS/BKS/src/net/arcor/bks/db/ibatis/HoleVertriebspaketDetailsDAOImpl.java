package net.arcor.bks.db.ibatis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.orm.ibatis.SqlMapClientTemplate;

import net.arcor.bks.db.HoleVertriebspaketDetailsDAO;

public class HoleVertriebspaketDetailsDAOImpl extends BksDataAccessObjectImpl
		implements HoleVertriebspaketDetailsDAO {

	public ArrayList<HashMap<String, Object>> getCustomerData(
			String customerNumber) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("HoleVertriebspaketDetails."+dataSourceName + ".RetrieveCustomerData",customerNumber);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getRootCustomer(
			String customerNumber) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("HoleVertriebspaketDetails."+dataSourceName + ".RetrieveRootCustomer",customerNumber);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getAddresses(String servSubsId)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("HoleVertriebspaketDetails."+dataSourceName + ".RetrieveAddresses",servSubsId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getAccessNumberChars(
			String servSubsId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("HoleVertriebspaketDetails."+dataSourceName + ".RetrieveAccessNumberChars",servSubsId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getConfiguredValues(
			String servSubsId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("HoleVertriebspaketDetails."+dataSourceName + ".RetrieveConfiguredValues",servSubsId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getAccessNumber(
			String countryCode, String cityCode, String localNumber)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,String> params = new HashMap<String, String>();
        params.put("shortnumber", countryCode.substring(2)+cityCode.substring(1)+localNumber.substring(0,3));
        params.put("accessnumber", countryCode.substring(2)+cityCode.substring(1)+localNumber);
        List list = template.queryForList("HoleVertriebspaketDetails."+dataSourceName + ".RetrieveAccessNumber",params);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getBandwidthService(
			String servSubId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("HoleVertriebspaketDetails."+dataSourceName + ".RetrieveBandwidthService",servSubId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getChildServices(String servSubsId)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("HoleVertriebspaketDetails."+dataSourceName + ".RetrieveChildServices",servSubsId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getCondition(String servSubId)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("HoleVertriebspaketDetails."+dataSourceName + ".RetrieveCondition",servSubId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getBundleItems(
			String servSubId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("HoleVertriebspaketDetails."+dataSourceName + ".RetrieveBundleItems",servSubId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getExactAccessNumber(
			String countryCode, String cityCode, String localNumber)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        String accessnumber = countryCode.substring(2)+cityCode.substring(1)+localNumber;
        List list = template.queryForList("HoleVertriebspaketDetails."+dataSourceName + ".RetrieveExactAccessNumber",accessnumber);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getOFContractData(String servSubId)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("HoleVertriebspaketDetails."+dataSourceName + ".RetrieveOFContractData",servSubId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getSDContractData(String servSubId)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("HoleVertriebspaketDetails."+dataSourceName + ".RetrieveSDContractData",servSubId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getServiceByCustomer(
			String customerNumber) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("HoleVertriebspaketDetails."+dataSourceName + ".RetrieveServiceByCustomer",customerNumber);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getCustomerBundles(
			String customerNumber) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("HoleVertriebspaketDetails."+dataSourceName + ".RetrieveCustomerBundles",customerNumber);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getHardwareCharacteristic(
			String prodSubsId) throws Exception {
	       SqlMapClientTemplate template = getSqlMapClientTemplate();
	        HashMap<String,String> params = new HashMap<String, String>();
	        params.put("prodSubId", prodSubsId);
	        List list = template.queryForList("HoleVertriebspaketDetails."+dataSourceName + ".RetrieveHardwareCharacteristic",params);
	        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getMainAccessService(String servSubsId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("HoleVertriebspaketDetails."+dataSourceName + ".RetrieveMainAccessService",servSubsId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getUnbundledServiceByCustomer(
			String customerNumber) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("HoleVertriebspaketDetails."+dataSourceName + ".RetrieveUnbundledServiceByCustomer",customerNumber);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getAllChildServices(
			String servSubsId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("HoleVertriebspaketDetails."+dataSourceName + ".RetrieveAllChildServices",servSubsId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

}
