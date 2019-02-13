package net.arcor.bks.db.ibatis;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.orm.ibatis.SqlMapClientTemplate;

import net.arcor.bks.db.GetFNPCustomerSomDataDAO;

import com.domainlanguage.time.CalendarDate;

public class GetFNPCustomerSomDataDAOImpl extends BksDataAccessObjectImpl implements GetFNPCustomerSomDataDAO {

	public ArrayList<HashMap<String, Object>> getCustomerExactAccessNumberCustData(
			String countryCode, String cityCode, String localNumber)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        String accessnumber = countryCode+cityCode+localNumber;
        List list = template.queryForList("GetFNPCustomerData."+dataSourceName + ".RetrieveCustomerExactAccessNumberCustData",accessnumber);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getCustomerAccessNumberCustData(
			String countryCode, String cityCode, String localNumber)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,String> params = new HashMap<String, String>();
        params.put("shortnumber", countryCode+cityCode+localNumber.substring(0,3));
        params.put("accessnumber", countryCode+cityCode+localNumber);
        List list = template.queryForList("GetFNPCustomerData."+dataSourceName + ".RetrieveCustomerAccessNumberCustData",params);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getCustomerForCustNumber(
			String customerNumber) throws Exception {
		SqlMapClientTemplate template = getSqlMapClientTemplate();
		List list = template.queryForList("GetFNPCustomerData."+dataSourceName + ".RetrieveCustomerForCustNumber",customerNumber);
		return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getCustomerForBarCode(
			String barcode) throws Exception {
		SqlMapClientTemplate template = getSqlMapClientTemplate();
		List list = template.queryForList("GetFNPCustomerData."+dataSourceName + ".RetrieveCustomerForBarCode",barcode);
		return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getCustomerPersonData(
			String familyName, String zipCode, String street, String streetNo,
			String streetNoSuf, String firstName, CalendarDate dateOfBirth)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,Object> params = new HashMap<String, Object>();
        params.put("familyName", familyName);
        params.put("zipCode", zipCode);
        params.put("street", street);
        if (streetNo!=null)
        	params.put("streetNumber", streetNo+"%");
        else
        	params.put("streetNumber", streetNo);
        params.put("streetNoSuffix", streetNoSuf);
        params.put("firstName", firstName);
        params.put("firstName2", firstName);
        Date sqlDate = (dateOfBirth==null)?null:Date.valueOf(dateOfBirth.toString());
        params.put("dateOfBirth", sqlDate);
        params.put("dateOfBirth2", sqlDate);
        List list = template.queryForList("GetFNPCustomerData."+dataSourceName + ".RetrieveCustomerPersonData",params);
 	    return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getCustomerCompanyData(
			String name, String zipCode, String street, String streetNo,
			String streetNoSuf, String orgSuf, String regNo) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,Object> params = new HashMap<String, Object>();
        String concatName = name+"%";
        if (orgSuf!=null && dataSourceName.equals("effonl"))
        	concatName += orgSuf;
        params.put("name", concatName);
        params.put("zipCode", zipCode);
        params.put("street", street);
        if (streetNo!=null)
        	params.put("streetNumber", streetNo+"%");
        else
        	params.put("streetNumber", streetNo);
        params.put("streetNoSuffix", streetNoSuf);
        params.put("orgSufixName", orgSuf);
        params.put("registrationNumber", regNo);
        List list = template.queryForList("GetFNPCustomerData."+dataSourceName + ".RetrieveCustomerCompanyData",params);
 	    return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getOwnerContact(String customerNo)
			throws Exception {
		SqlMapClientTemplate template = getSqlMapClientTemplate();
		List list = template.queryForList("GetFNPCustomerData."+dataSourceName + ".RetrieveOwnerContact",customerNo);
		return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getExternalOrders(
			String customerNumber) throws Exception {
		SqlMapClientTemplate template = getSqlMapClientTemplate();
		List list = template.queryForList("GetFNPCustomerData."+dataSourceName + ".RetrieveExternalOrders",customerNumber);
		return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getBundlesForCustomer(
			String customerNumber) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetFNPCustomerData."+dataSourceName + ".RetrieveBundlesForCustomer",customerNumber);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getAccountsForBundle(
			String bundleId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetFNPCustomerSomData."+dataSourceName + ".RetrieveAccountsForBundle",bundleId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getBundleItems(String bundleId)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("FetchProductBundle."+dataSourceName + ".RetrieveBundleItems",bundleId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getSDContractData(
			String servSubsId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("FetchProductBundle."+dataSourceName + ".RetrieveSDContractData",servSubsId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getOFContractData(
			String servSubsId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("FetchProductBundle."+dataSourceName + ".RetrieveContractData",servSubsId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getOrderTickets(String custOrderId)
			throws Exception {
		SqlMapClientTemplate template = getSqlMapClientTemplate();
		List list = template.queryForList("GetFNPCustomerData."+dataSourceName + ".RetrieveOrderTickets",custOrderId);
		return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getCcmOpenCustOrders(
			String customerNumber) throws Exception {
		SqlMapClientTemplate template = getSqlMapClientTemplate();
		List list = template.queryForList("GetFNPCustomerData."+dataSourceName + ".RetrieveOpenOrders",customerNumber);
		return (ArrayList<HashMap<String, Object>>) list;
	}

}
