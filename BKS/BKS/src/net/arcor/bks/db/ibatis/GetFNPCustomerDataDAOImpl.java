package net.arcor.bks.db.ibatis;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.arcor.bks.db.GetFNPCustomerDataDAO;

import org.springframework.orm.ibatis.SqlMapClientTemplate;

import com.domainlanguage.time.CalendarDate;

import de.vodafone.esb.schema.common.commontypes_esb_001.ComplexPhoneNumber;
public class GetFNPCustomerDataDAOImpl extends BksDataAccessObjectImpl implements GetFNPCustomerDataDAO {


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
		
	public ArrayList<HashMap<String, Object>> getCustomerPersonData(String familyName, String zipCode,
			String street, String streetNumber, String streetNoSuf,String firstName,CalendarDate dateOfBirth) throws Exception{
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,Object> params = new HashMap<String, Object>();
        params.put("familyName", familyName);
        params.put("zipCode", zipCode);
        params.put("street", street);
        if (streetNumber!=null)
        	params.put("streetNumber", streetNumber+"%");
        else
        	params.put("streetNumber", streetNumber);
        params.put("streetNoSuffix", streetNoSuf);
        params.put("firstName", firstName);
        params.put("firstName2", firstName);
        Date sqlDate = (dateOfBirth==null)?null:Date.valueOf(dateOfBirth.toString());
        params.put("dateOfBirth", sqlDate);
        params.put("dateOfBirth2", sqlDate);
        List list = template.queryForList("GetFNPCustomerData."+dataSourceName + ".RetrieveCustomerPersonData",params);
 	    return (ArrayList<HashMap<String, Object>>) list;
	}
			
	public ArrayList<HashMap<String, Object>> getCustomerFuzzyPerson(String phonFirstName, String phonName,  
			String zipCode, String street, String streetNumber, String streetNoSuf, CalendarDate dateOfBirth) throws Exception{
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,Object> params = new HashMap<String, Object>();
        params.put("phonName", phonName);
        params.put("zipCode", zipCode);
        params.put("street", street);
        if (streetNumber!=null)
        	params.put("streetNumber", streetNumber+"%");
        else
        	params.put("streetNumber", streetNumber);
        params.put("streetNoSuffix", streetNoSuf);
        params.put("phonFirstName", phonFirstName);
        Date sqlDate = (dateOfBirth==null)?null:Date.valueOf(dateOfBirth.toString());
        params.put("dateOfBirth", sqlDate);
        List list = template.queryForList("GetFNPCustomerData."+dataSourceName + ".RetrieveCustomerFuzzyPerson",params);
 	    return (ArrayList<HashMap<String, Object>>) list;
	}
			
	public ArrayList<HashMap<String, Object>> getCustomerCompanyData(
			String name, String zipCode, String street, String streetNumber,
			String streetNoSuf, String orgSuf, String regNo) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,Object> params = new HashMap<String, Object>();
        String concatName = name+"%";
        if (orgSuf!=null && dataSourceName.equals("effonl"))
        	concatName += orgSuf;
        params.put("name", concatName);
        params.put("zipCode", zipCode);
        params.put("street", street);
        if (streetNumber!=null)
        	params.put("streetNumber", streetNumber+"%");
        else
        	params.put("streetNumber", streetNumber);
        params.put("streetNoSuffix", streetNoSuf);
        params.put("orgSufixName", orgSuf);
        params.put("registrationNumber", regNo);
        List list = template.queryForList("GetFNPCustomerData."+dataSourceName + ".RetrieveCustomerCompanyData",params);
 	    return (ArrayList<HashMap<String, Object>>) list;
	}	

	public ArrayList<HashMap<String, Object>> getCustomerFuzzyCompany(String phonName,
			String phonOrgSuffix, String zipCode, String street,String streetNumber, String streetNoSuf, String regNo) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,Object> params = new HashMap<String, Object>();
        params.put("phonName", phonName);
        params.put("zipCode", zipCode);
        params.put("street", street);
        if (streetNumber!=null)
        	params.put("streetNumber", streetNumber+"%");
        else
        	params.put("streetNumber", streetNumber);
        params.put("streetNoSuffix", streetNoSuf);
        params.put("phonOrgSuffix", phonOrgSuffix);
        params.put("registrationNumber", regNo);
        List list = template.queryForList("GetFNPCustomerData."+dataSourceName + ".RetrieveCustomerFuzzyCompany",params);
 	    return (ArrayList<HashMap<String, Object>>) list;
	}	

	public ArrayList<HashMap<String, Object>> getOwnerContact(String customerNo)
	throws Exception {
		SqlMapClientTemplate template = getSqlMapClientTemplate();
		List list = template.queryForList("GetFNPCustomerData."+dataSourceName + ".RetrieveOwnerContact",customerNo);
		return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getAccountData(String accountNo)
			throws Exception {
		SqlMapClientTemplate template = getSqlMapClientTemplate();
		List list = template.queryForList("GetFNPCustomerData."+dataSourceName + ".RetrieveAccountData",accountNo);
		return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getBankData(String bankAccId,
			String customerNumber) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,String> params = new HashMap<String, String>();
        params.put("bankAccountId", bankAccId);
        params.put("customerNumber", customerNumber);
        List list = template.queryForList("GetFNPCustomerData."+dataSourceName + ".RetrieveBankData",params);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getDocumentPattern(
			String customerNumber) throws Exception {
		SqlMapClientTemplate template = getSqlMapClientTemplate();
		List list = template.queryForList("GetFNPCustomerData."+dataSourceName + ".RetrieveDocumentPattern",customerNumber);
		return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getDocumentRecipient(
			String customerNumber) throws Exception {
		SqlMapClientTemplate template = getSqlMapClientTemplate();
		List list = template.queryForList("GetFNPCustomerData."+dataSourceName + ".RetrieveDocumentRecipient",customerNumber);
		return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getAddressInfo(String addressId)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetFNPCustomerData."+dataSourceName + ".RetrieveAddressInfo",addressId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getBundleItems(String bundleId)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetFNPCustomerData."+dataSourceName + ".RetrieveBundleItems",bundleId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getBundlesForAccess(
			ComplexPhoneNumber accessNumber) throws Exception {
		String number = accessNumber.getCountryCode()+accessNumber.getLocalAreaCode()+
						accessNumber.getPhoneNumbers().getPhoneNumber().get(0);
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetFNPCustomerData."+dataSourceName + ".RetrieveBundlesForAccess",number);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getBundlesForCustomer(
			String customerNumber) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetFNPCustomerData."+dataSourceName + ".RetrieveBundlesForCustomer",customerNumber);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getBundlesForBarcode(
			String barcode) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetFNPCustomerData."+dataSourceName + ".RetrieveBundlesForBarcode",barcode);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getContractData(String servSubsId)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetFNPCustomerData."+dataSourceName + ".RetrieveContractData",servSubsId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getSalesPackageInfo(
			String bundleId) throws Exception {
		SqlMapClientTemplate template = getSqlMapClientTemplate();
		List list = template.queryForList("GetFNPCustomerData."+dataSourceName + ".RetrieveSalesPackageInfo",bundleId);
		return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getConfiguredValues(
			String servSubsId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetFNPCustomerData."+dataSourceName + ".RetrieveConfiguredValue",servSubsId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getConfiguredValuesByStp(
			String ticketId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetFNPCustomerData."+dataSourceName + ".RetrieveConfiguredValueByStp",ticketId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getProductPrice(String servSubsId)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetFNPCustomerData."+dataSourceName + ".RetrieveProductPrice",servSubsId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getAddressChar(String servSubsId)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetFNPCustomerData."+dataSourceName + ".RetrieveAddressChar",servSubsId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getChildServices(
			String servSubsId,String serviceType,String status) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,String> params = new HashMap<String, String>();
        params.put("servSubsId", servSubsId);
        params.put("serviceType", serviceType);
        params.put("status", status);
        List list = template.queryForList("GetFNPCustomerData."+dataSourceName + ".RetrieveChildServices",params);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getHardwareDepService(
			String servSubsId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetFNPCustomerData."+dataSourceName + ".RetrieveHardwareDepService",servSubsId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getAccessNumberChars(
			String servSubsId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetFNPCustomerData."+dataSourceName + ".RetrieveAccessNumberChars",servSubsId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getAccessNumberCharsByStp(
			String ticketId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetFNPCustomerData."+dataSourceName + ".RetrieveAccessNumberCharsByStp",ticketId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getOpenOrders(
			String customerNumber) throws Exception {
		SqlMapClientTemplate template = getSqlMapClientTemplate();
		List list = template.queryForList("GetFNPCustomerData."+dataSourceName + ".RetrieveOpenOrders",customerNumber);
		return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getOrderTickets(String custOrderId)
			throws Exception {
		SqlMapClientTemplate template = getSqlMapClientTemplate();
		List list = template.queryForList("GetFNPCustomerData."+dataSourceName + ".RetrieveOrderTickets",custOrderId);
		return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getCustomerForBarCode(
			String barcode) throws Exception {
		SqlMapClientTemplate template = getSqlMapClientTemplate();
		List list = template.queryForList("GetFNPCustomerData."+dataSourceName + ".RetrieveCustomerForBarCode",barcode);
		return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getAccessInfo(String accessInfoId)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetFNPCustomerData."+dataSourceName + ".RetrieveAccessInfo",accessInfoId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getCustomerForCustNumber(
			String customerNumber) throws Exception {
		SqlMapClientTemplate template = getSqlMapClientTemplate();
		List list = template.queryForList("GetFNPCustomerData."+dataSourceName + ".RetrieveCustomerForCustNumber",customerNumber);
		return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getCustomerSDC(String custNum)
			throws Exception {
		SqlMapClientTemplate template = getSqlMapClientTemplate();
		List list = template.queryForList("GetFNPCustomerData."+dataSourceName + ".RetrieveCustomerSDC",custNum);
		return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getBundlesForBundleId(
			String bundleId) throws Exception {
		SqlMapClientTemplate template = getSqlMapClientTemplate();
		List list = template.queryForList("GetFNPCustomerData."+dataSourceName + ".RetrieveBundlesForBundleId",bundleId);
		return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getCustomerForBundleId(
			String bundleId) throws Exception {
		SqlMapClientTemplate template = getSqlMapClientTemplate();
		List list = template.queryForList("GetFNPCustomerData."+dataSourceName + ".RetrieveCustomerForBundleId",bundleId);
		return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getExternalOrders(
			String customerNumber) throws Exception {
		SqlMapClientTemplate template = getSqlMapClientTemplate();
		List list = template.queryForList("GetFNPCustomerData."+dataSourceName + ".RetrieveExternalOrders",customerNumber);
		return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getExternalOrderByBarcode(
			String barcode) throws Exception {
		SqlMapClientTemplate template = getSqlMapClientTemplate();
		List list = template.queryForList("GetFNPCustomerData."+dataSourceName + ".RetrieveExternalOrderByBarcode",barcode);
		return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getEmailValidation(
			String validationId) throws Exception {
		SqlMapClientTemplate template = getSqlMapClientTemplate();
		List list = template.queryForList("GetFNPCustomerData."+dataSourceName + ".RetrieveEmailValidation",validationId);
		return (ArrayList<HashMap<String, Object>>) list;
	}
	
	public ArrayList<HashMap<String, Object>> getCustomerPermissionInfo(
			String customerNumber) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetFNPCustomerData."+dataSourceName + ".RetrieveCustomerPermissionInfo",customerNumber);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getInitialTicketForService(
			String servSubsId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetFNPCustomerData."+dataSourceName + ".RetrieveInitialTicketForService",servSubsId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

}
