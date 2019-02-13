package net.arcor.bks.db.ibatis;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.orm.ibatis.SqlMapClientTemplate;

import net.arcor.bks.db.PruefeBestandskundenRangerDAO;

public class PruefeBestandskundenRangerDAOImpl extends BksDataAccessObjectImpl
		implements PruefeBestandskundenRangerDAO {

	public ArrayList<HashMap<String, Object>> getAccessNumber(String countryCode,
											String cityCode, String localNumber) throws Exception{
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,String> params = new HashMap<String, String>();
        params.put("shortnumber", countryCode.substring(2)+cityCode.substring(1)+localNumber.substring(0,3));
        params.put("accessnumber", countryCode.substring(2)+cityCode.substring(1)+localNumber);
        List list = template.queryForList(dataSourceName + ".RetrieveAccessNumberRanger",params);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getCustomer(String firstName,
			String lastName, String cscIdList, String custNumList) throws Exception{
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,String> params = new HashMap<String, String>();
        params.put("firstName", firstName);
        params.put("lastName", lastName);
        params.put("cscIdList", cscIdList);
        params.put("custNumList", custNumList);
        List list = template.queryForList(dataSourceName + ".RetrieveCustomerRanger",params);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getRootCustomer(
			String customerNumber) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList(dataSourceName + ".RetrieveRootCustomerRanger",customerNumber);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getOFContractData(String servSubId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList(dataSourceName + ".RetrieveOFContractData",servSubId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getSDContractData(String servSubId)  throws Exception{
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList(dataSourceName + ".RetrieveSDContractData",servSubId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public Date getOFEndDate(String cntNumber) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,Object> params = new HashMap<String, Object>();
        params.put("result", null);
        params.put("contractNumber", cntNumber);
        template.queryForObject(dataSourceName + ".GetEndDate_OF", params);
        return (Date) params.get("result");
	}

	public Date getSDEndDate(String cntNumber) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,Object> params = new HashMap<String, Object>();
        params.put("result", null);
        params.put("contractNumber", cntNumber);
        template.queryForObject(dataSourceName + ".GetEndDate_SD", params);
        return (Date) params.get("result");
	}

	public ArrayList<HashMap<String, Object>> getBundledService(
			String servSubId, String serviceCodes) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,String> params = new HashMap<String, String>();
        params.put("servSubId", servSubId);
        params.put("serviceCodes", serviceCodes);
        List list = template.queryForList(dataSourceName + ".RetrieveBundledService",params);
        return (ArrayList<HashMap<String, Object>>) list;
	}
}
