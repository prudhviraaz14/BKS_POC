package net.arcor.bks.db;

import java.util.ArrayList;
import java.util.HashMap;

import com.domainlanguage.time.CalendarDate;

public interface GetFNPCustomerSomDataDAO  extends BksDataAccessObject{

	ArrayList<HashMap<String, Object>> getCustomerExactAccessNumberCustData(String countryCode, String cityCode, String localNumber) throws Exception;

	ArrayList<HashMap<String, Object>> getCustomerAccessNumberCustData(	String countryCode, String cityCode, String localNumber) throws Exception;

	ArrayList<HashMap<String, Object>> getCustomerForCustNumber(String customerNumber)throws Exception;

	ArrayList<HashMap<String, Object>> getCustomerForBarCode(String barcode) throws Exception;

	ArrayList<HashMap<String, Object>> getCustomerPersonData(String familyName,
			String zipCode, String street, String streetNo, String streetNoSuf,
			String firstName, CalendarDate dateOfBirth)throws Exception;

	ArrayList<HashMap<String, Object>> getCustomerCompanyData(String name,
			String zipCode, String street, String streetNo, String streetNoSuf,
			String orgSuf, String regNo)throws Exception;

	ArrayList<HashMap<String, Object>> getOwnerContact(String customerNo)throws Exception;

	ArrayList<HashMap<String, Object>> getExternalOrders(String customerNumber)throws Exception;

	ArrayList<HashMap<String, Object>> getBundlesForCustomer(String customerNumber)throws Exception;

	ArrayList<HashMap<String, Object>> getAccountsForBundle(String bundleId)throws Exception;

	ArrayList<HashMap<String, Object>> getBundleItems(String bundleId)throws Exception;

	ArrayList<HashMap<String, Object>> getSDContractData(String servSubsId)throws Exception;

	ArrayList<HashMap<String, Object>> getOFContractData(String servSubsId)throws Exception;

	ArrayList<HashMap<String, Object>> getOrderTickets(String custOrderId)throws Exception;

	ArrayList<HashMap<String, Object>> getCcmOpenCustOrders(String customerNumber) throws Exception;

}
