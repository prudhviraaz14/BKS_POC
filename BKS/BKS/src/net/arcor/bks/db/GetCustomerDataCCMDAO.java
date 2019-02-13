package net.arcor.bks.db;

import java.util.ArrayList;
import java.util.HashMap;

import com.domainlanguage.time.CalendarDate;



public interface GetCustomerDataCCMDAO extends BksDataAccessObject{
	ArrayList<HashMap<String, Object>> getCustomerCustData(String customerNumber) throws Exception;
	ArrayList<HashMap<String, Object>> getCustomerExactAccessNumberCustData(String countryCode,String cityCode, String localNumber) throws Exception;
	ArrayList<HashMap<String, Object>> getCustomerAccessNumberCustData(String countryCode,String cityCode, String localNumber) throws Exception;
	ArrayList<HashMap<String, Object>> getCustomerForBarCode(String barCode) throws Exception;
	ArrayList<HashMap<String, Object>> getCustomerPersonData(String familyName, String zipCode,String street,  String firstName,CalendarDate dateOfBirth) throws Exception;
	ArrayList<HashMap<String, Object>> getRootCustomerCustData(String customerNumber) throws Exception;
	ArrayList<HashMap<String, Object>> getPersonData(String customerNumber) throws Exception;				
	ArrayList<HashMap<String, Object>> getAddressData(String customerNumber) throws Exception;		
	ArrayList<HashMap<String, Object>> getEmailData(String customerNumber) throws Exception;	
	ArrayList<HashMap<String, Object>> getPhoneNumberData(String customerNumber) throws Exception;
	ArrayList<HashMap<String, Object>> getCompanyData(String customerNumber) throws Exception;				
	ArrayList<HashMap<String, Object>> getContactData(String customerNumber) throws Exception;
	ArrayList<HashMap<String, Object>> getCustomerCompanyData(String name,String zipCode, 
			String street, String streetNo, String streetNoSuf,	String orgSuf, String regNo) throws Exception;
	ArrayList<HashMap<String, Object>> getOwnerContact(String customerNo) throws Exception;
}
