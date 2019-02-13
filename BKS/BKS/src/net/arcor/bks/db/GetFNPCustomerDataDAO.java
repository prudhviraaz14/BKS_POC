package net.arcor.bks.db;

import java.util.ArrayList;
import java.util.HashMap;

import com.domainlanguage.time.CalendarDate;

import de.vodafone.esb.schema.common.commontypes_esb_001.ComplexPhoneNumber;



public interface GetFNPCustomerDataDAO extends BksDataAccessObject{
	ArrayList<HashMap<String, Object>> getCustomerExactAccessNumberCustData(String countryCode,String cityCode, String localNumber) throws Exception;
	ArrayList<HashMap<String, Object>> getCustomerAccessNumberCustData(String countryCode,String cityCode, String localNumber) throws Exception;
	ArrayList<HashMap<String, Object>> getCustomerPersonData(String familyName, String zipCode,String street, String streetNumber, String streetNoSuf,  String firstName,CalendarDate dateOfBirth) throws Exception;
	ArrayList<HashMap<String, Object>> getCustomerCompanyData(String name,String zipCode, 
			String street, String streetNo, String streetNoSuf,	String orgSuf, String regNo) throws Exception;
	ArrayList<HashMap<String, Object>> getOwnerContact(String customerNo) throws Exception;
	ArrayList<HashMap<String, Object>> getDocumentPattern(String customerNumber) throws Exception;
	ArrayList<HashMap<String, Object>> getAccountData(String accountNo) throws Exception;
	ArrayList<HashMap<String, Object>> getBankData(String bankAccId,String customerNumber)throws Exception;
	ArrayList<HashMap<String, Object>> getDocumentRecipient(String customerNumber)throws Exception;
	ArrayList<HashMap<String, Object>> getAddressInfo(String addressId)throws Exception;
	ArrayList<HashMap<String, Object>> getBundlesForCustomer(String customerNumber)throws Exception;
	ArrayList<HashMap<String, Object>> getBundlesForAccess(	ComplexPhoneNumber accessNumber)throws Exception;
	ArrayList<HashMap<String, Object>> getBundleItems(String bundleId)throws Exception;
	ArrayList<HashMap<String, Object>> getContractData(String servSubsId)throws Exception;
	ArrayList<HashMap<String, Object>> getSalesPackageInfo(String bundleId)throws Exception;
	ArrayList<HashMap<String, Object>> getProductPrice(String servSubsId)throws Exception;
	ArrayList<HashMap<String, Object>> getConfiguredValues(String servSubsId)throws Exception;
	ArrayList<HashMap<String, Object>> getAddressChar(String servSubsId)throws Exception;
	ArrayList<HashMap<String, Object>> getChildServices(String parentServSubsId, String serviceType, String status)throws Exception;
	ArrayList<HashMap<String, Object>> getHardwareDepService(String parentServSubsId)throws Exception;
	ArrayList<HashMap<String, Object>> getAccessNumberChars(String servSubsId)throws Exception;
	ArrayList<HashMap<String, Object>> getOpenOrders(String customerNumber)throws Exception;
	ArrayList<HashMap<String, Object>> getOrderTickets(String custOrderId)throws Exception;
	ArrayList<HashMap<String, Object>> getConfiguredValuesByStp(String ticketId)throws Exception;
	ArrayList<HashMap<String, Object>> getAccessNumberCharsByStp(String ticketId)throws Exception;
	ArrayList<HashMap<String, Object>> getAccessInfo(String accessInfoId)throws Exception;
	ArrayList<HashMap<String, Object>> getCustomerForBarCode(String barcode)throws Exception;
	ArrayList<HashMap<String, Object>> getBundlesForBarcode(String barcode)throws Exception;
	ArrayList<HashMap<String, Object>> getCustomerForCustNumber(String customerNumber)throws Exception;
	ArrayList<HashMap<String, Object>> getCustomerFuzzyPerson(String phonFirstName, String phonName, String zipCode,String street, String streetNumber, String streetNoSuf, CalendarDate dateOfBirth)throws Exception;
	ArrayList<HashMap<String, Object>> getCustomerFuzzyCompany(String phonName,
			String phonOrgSuffix, String zipCode, String street,String streetNo, String streetNoSuf, String regNo)throws Exception;
	ArrayList<HashMap<String, Object>> getCustomerSDC(String custNum)throws Exception;
	ArrayList<HashMap<String, Object>> getBundlesForBundleId(String bundleId)throws Exception;
	ArrayList<HashMap<String, Object>> getCustomerForBundleId(String bundleId)throws Exception;
	ArrayList<HashMap<String, Object>> getExternalOrders(String customerNumber)throws Exception;
	ArrayList<HashMap<String, Object>> getExternalOrderByBarcode(String barcode)throws Exception;
	ArrayList<HashMap<String, Object>> getEmailValidation(String validationId)throws Exception;
	ArrayList<HashMap<String, Object>> getCustomerPermissionInfo(String customerNumber) throws Exception;
	ArrayList<HashMap<String, Object>> getInitialTicketForService(String servSubsId) throws Exception;
}
