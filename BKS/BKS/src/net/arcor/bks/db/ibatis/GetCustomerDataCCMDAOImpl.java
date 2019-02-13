package net.arcor.bks.db.ibatis;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.arcor.bks.db.GetCustomerDataCCMDAO;

import org.springframework.orm.ibatis.SqlMapClientTemplate;

import com.domainlanguage.time.CalendarDate;

public class GetCustomerDataCCMDAOImpl extends BksDataAccessObjectImpl implements GetCustomerDataCCMDAO {


	public ArrayList<HashMap<String, Object>> getCustomerCustData(String customerNumber) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList(dataSourceName + ".RetrieveCustomerCustData",customerNumber);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getCustomerExactAccessNumberCustData(
			String countryCode, String cityCode, String localNumber)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        String accessnumber = countryCode+cityCode+localNumber;
        List list = template.queryForList(dataSourceName + ".RetrieveCustomerExactAccessNumberCustData",accessnumber);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getCustomerAccessNumberCustData(
			String countryCode, String cityCode, String localNumber)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,String> params = new HashMap<String, String>();
        params.put("shortnumber", countryCode+cityCode+localNumber.substring(0,3));
        params.put("accessnumber", countryCode+cityCode+localNumber);
        List list = template.queryForList(dataSourceName + ".RetrieveCustomerAccessNumberCustData",params);
        return (ArrayList<HashMap<String, Object>>) list;
	}
		
	public ArrayList<HashMap<String, Object>> getCustomerForBarCode(String barCode)
           throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();        
        List list = template.queryForList(dataSourceName + ".RetrieveCustomerForBarCode",barCode);
        return (ArrayList<HashMap<String, Object>>) list;
    }

	public ArrayList<HashMap<String, Object>> getCustomerPersonData(String familyName, String zipCode,
			String street,  String firstName,CalendarDate dateOfBirth) throws Exception{
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,Object> params = new HashMap<String, Object>();
        params.put("familyName", familyName);
        params.put("zipCode", zipCode);
        params.put("street", street);
        params.put("firstName", firstName);
        params.put("firstName2", firstName);
        Date sqlDate = (dateOfBirth==null)?null:Date.valueOf(dateOfBirth.toString());
        params.put("dateOfBirth", sqlDate);
        params.put("dateOfBirth2", sqlDate);
        List list = template.queryForList(dataSourceName + ".RetrieveCustomerPesonData",params);
 	    return (ArrayList<HashMap<String, Object>>) list;
	}
			
	public ArrayList<HashMap<String, Object>> getRootCustomerCustData(String customerNumber) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList(dataSourceName + ".RetrieveRootCustomer",customerNumber);
        return (ArrayList<HashMap<String, Object>>) list;
	}
	
	public ArrayList<HashMap<String, Object>> getPersonData(String customerNumber) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList(dataSourceName + ".RetrievePersonData",customerNumber);
        return (ArrayList<HashMap<String, Object>>) list;
	}		
	public ArrayList<HashMap<String, Object>> getAddressData(String customerNumber) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList(dataSourceName + ".RetrieveAddressData",customerNumber);
        return (ArrayList<HashMap<String, Object>>) list;
	}		
	public ArrayList<HashMap<String, Object>> getEmailData(String customerNumber) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList(dataSourceName + ".RetrieveEmailData",customerNumber);
        return (ArrayList<HashMap<String, Object>>) list;
	}	
	public ArrayList<HashMap<String, Object>> getPhoneNumberData(String customerNumber) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList(dataSourceName + ".RetrievePhoneNumberData",customerNumber);
        return (ArrayList<HashMap<String, Object>>) list;
	}	
	public ArrayList<HashMap<String, Object>> getCompanyData(String customerNumber) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList(dataSourceName + ".RetrieveCompanyData",customerNumber);
        return (ArrayList<HashMap<String, Object>>) list;
	}	
	public ArrayList<HashMap<String, Object>> getContactData(String customerNumber) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList(dataSourceName + ".RetrieveContactData",customerNumber);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getCustomerCompanyData(
			String name, String zipCode, String street, String streetNo,
			String streetNoSuf, String orgSuf, String regNo) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,Object> params = new HashMap<String, Object>();
        String concatName = name+"%";
        if (orgSuf!=null)
        	concatName += orgSuf;
        params.put("name", concatName);
        params.put("zipCode", zipCode);
        params.put("street", street);
        params.put("streetNo", streetNo);
        params.put("streetNoSuffix", streetNoSuf);
        params.put("orgSufixName", orgSuf);
        params.put("registrationNumber", regNo);
        List list = template.queryForList(dataSourceName + ".RetrieveCustomerCompanyData",params);
 	    return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getOwnerContact(String customerNo)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList(dataSourceName + ".RetrieveOwnerContact",customerNo);
        return (ArrayList<HashMap<String, Object>>) list;
	}	
	
}
