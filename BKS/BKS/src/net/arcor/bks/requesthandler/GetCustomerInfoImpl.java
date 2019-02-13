/*
    $Header:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/GetCustomerInfoImpl.java-arc   1.4   Jun 06 2011 17:35:56   makuier  $

    $Log:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/GetCustomerInfoImpl.java-arc  $
 * 
 *    Rev 1.4   Jun 06 2011 17:35:56   makuier
 * Handle oracle date casting.
 * 
 *    Rev 1.3   May 25 2011 15:08:52   makuier
 * Ibatis maps date to timestamp.
 * 
 *    Rev 1.2   Apr 14 2010 18:05:04   makuier
 * Adapted to changes in MCF.
 * Changes for CPCOM 1a
 * 
 *    Rev 1.1   Mar 19 2010 15:49:14   makuier
 * duplicated the code as different name spaces are used.
 * 
 *    Rev 1.0   Feb 04 2010 11:49:28   makuier
 * Initial revision.
*/
package net.arcor.bks.requesthandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import net.arcor.bks.client.DatabaseClient;
import net.arcor.bks.client.DatabaseClientConfig;
import net.arcor.bks.common.BksDataException;
import net.arcor.bks.common.BksRefDataCacheHandler;
import net.arcor.bks.db.GetCustomerDataCCMDAO;
import net.arcor.mcf2.model.ServiceObjectEndpoint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.domainlanguage.time.CalendarDate;
import com.vodafone.mcf2.ws.exception.FunctionalException;
import com.vodafone.mcf2.ws.exception.FunctionalRuntimeException;
import com.vodafone.mcf2.ws.model.impl.ServiceResponseSoap;
import com.vodafone.mcf2.ws.service.SoapEndpoint;
import com.vodafone.mcf2.ws.service.SoapOperation;

import de.vodafone.esb.schema.common.commontypes_esb_001.ComplexPhoneNumber;
import de.vodafone.esb.schema.common.commontypes_esb_001.ErrorType;
import de.vodafone.esb.schema.common.commontypes_esb_001.PhoneNumberRange;
import de.vodafone.esb.schema.common.commontypes_esb_001.PhoneNumberRanges;
import de.vodafone.esb.schema.common.commontypes_esb_001.PhoneNumbers;
import de.vodafone.esb.service.sbus.bks.bks_001.GetCustomerInfoRequest;
import de.vodafone.esb.service.sbus.bks.bks_001.GetCustomerInfoResponse;
import de.vodafone.linkdb.epsm_bkstypes_001.Address;
import de.vodafone.linkdb.epsm_bkstypes_001.ArcorCustomerOrigin;
import de.vodafone.linkdb.epsm_bkstypes_001.CommercialRegister;
import de.vodafone.linkdb.epsm_bkstypes_001.Company;
import de.vodafone.linkdb.epsm_bkstypes_001.CompanyDataQueryInput;
import de.vodafone.linkdb.epsm_bkstypes_001.Contact;
import de.vodafone.linkdb.epsm_bkstypes_001.CustomerData;
import de.vodafone.linkdb.epsm_bkstypes_001.Person;
import de.vodafone.linkdb.epsm_bkstypes_001.PersonDataQueryInput;

@SoapEndpoint(soapAction = "/BKS-001/GetCustomerInfo", context = "de.vodafone.esb.service.sbus.bks.bks_001", schema = "classpath:schema/BKS-001.xsd")
public class GetCustomerInfoImpl implements SoapOperation<GetCustomerInfoRequest, GetCustomerInfoResponse> {

    /** The logger. */
    private final static Log log = LogFactory.getLog(GetCustomerInfoImpl.class);
	private String errorText;
	private String errorCode;
	private String customerNumber = new String();
	private boolean isIndividual = true;

	/**
	 * The operation "GetCustomerInfo" of the service "GetCustomerInfo".
	 */
	public ServiceResponseSoap<GetCustomerInfoResponse> invoke(ServiceObjectEndpoint<GetCustomerInfoRequest> serviceObject) throws FunctionalException,
	FunctionalRuntimeException {
		int tries =0;
		int maxTries = 10;
		try {
			maxTries = Integer.parseInt(DatabaseClientConfig.getSetting("databaseclient.MaxRetries"));
		} catch (Exception e1) {
			// Ignore (Use Default)
		}
        while (!BksRefDataCacheHandler.isRefdataInitialized() && tries < maxTries){
        	tries++;
        	try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
        }
        if (!BksRefDataCacheHandler.isRefdataInitialized()){
        	log.error("The application is not initialized after max. number of tries ( "+ tries +").");
        	log.error("Please increase the value of databaseclient.MaxRetries and restart the application.");
        	System.exit(0);
        }
		GetCustomerDataCCMDAO dao = (GetCustomerDataCCMDAO) DatabaseClient.getBksDataAccessObject(null, "GetCustomerDataCCMDAO");
		GetCustomerInfoRequest request = serviceObject.getPayload();
		GetCustomerInfoResponse output = new GetCustomerInfoResponse();
		try {
			customerNumber = request.getCustomerNumber();
			output = getCustomerData(dao,request);
		} catch (BksDataException e) {
			log.error(e.getMessage());
			output.setStatus("FAILED");
			ErrorType et = new ErrorType();
			et.setErrorCode(errorCode);
			et.setErrorDescription(errorText);
			output.setErrorDetails(et);
		} catch (Exception e) {
			log.error(e.getMessage());
			output.setStatus("FAILED");
			ErrorType et = new ErrorType();
			et.setErrorCode("BKS0008");
			et.setErrorDescription(e.getMessage());
			output.setErrorDetails(et);
		} finally {
			try {
				dao.closeConnection();
			} catch (Exception e) {
			}
		}

		
		return new ServiceResponseSoap<GetCustomerInfoResponse>(output);
	}

	private GetCustomerInfoResponse getCustomerData(GetCustomerDataCCMDAO dao, GetCustomerInfoRequest request) throws Exception{
		GetCustomerInfoResponse resp = new GetCustomerInfoResponse();
		resp.setStatus("SUCCESS");
		ArrayList<HashMap<String, Object>> custList = null;
		String barcode = request.getBarcode();


		PersonDataQueryInput person = request.getPersonData();
		CompanyDataQueryInput company = request.getCompanyData();
		ComplexPhoneNumber accnum = request.getAccessNumber();
		if (person != null) {
			custList = getCustomerByPersonData(dao,person);
		}else if (company != null){
			custList = getCustomerByCompanyData(dao,company);
		}else if (accnum != null) {
			custList = getCustomerByAccessNumber(dao,accnum);
		}else if (customerNumber  != null) {
			custList = dao.getCustomerCustData(customerNumber);
		}else if (barcode != null) {
			custList = dao.getCustomerForBarCode(barcode);
		}  

		if (resp.getStatus().equals("SUCCESS"))
			if ((custList == null) || (custList.size() < 1)) {
				errorText = "No customer found";
				errorCode = "BKS0000";
				throw new BksDataException();
			}

		HashMap<String, Object> custRow = null;
		int counter = 0;
		for  (int i = 0;custList != null && i < custList.size();i++) {
			custRow = (HashMap<String, Object>) custList.get(i);

			customerNumber = custRow.get("CUSTOMER_NUMBER").toString();
			isIndividual = custRow.get("ENTITY_TYPE").toString().equals("I");
			String custCategory = custRow.get("CATEGORY_RD").toString();
			String custClass = custRow.get("CLASSIFICATION_RD").toString();
			String providerCode = getProviderCode(dao, customerNumber,custClass);
			CustomerData custData = null;
			ArrayList<String> gciList = BksRefDataCacheHandler.getGeneralData().get("CLDBSVPBKS");
			if (gciList != null && gciList.contains(providerCode)) {
				counter++;
				if (counter <= 20) {
					custData = new CustomerData();
					if (isIndividual) {
						Person personVf = populatePersonData(dao,customerNumber);
						if (personVf != null)
							custData.setPerson(personVf);
					} else {
						Company companyVf = populateCompanyData(dao,customerNumber);
						if (companyVf != null)
							custData.setCompany(companyVf);
					}

					Address addressVf = populateAddressData(dao,customerNumber);
					if (addressVf != null)
						custData.setAddress(addressVf);
					ComplexPhoneNumber phoneNumber = populatePhoneNumberData(dao, customerNumber);
					if (phoneNumber != null)
						custData.setPhoneNumber(phoneNumber);

					custData.setCustomerNumber(customerNumber);
					if (providerCode.substring(0, 1).equals("V"))
						custData.setArcorCustomerOrigin(ArcorCustomerOrigin.V);
					else
						custData.setArcorCustomerOrigin(ArcorCustomerOrigin.A);
				} else {
					break;
				}
			}

			if (custData != null)
				resp.getCustomers().add(custData);
		} // end of loop over customers

		if (resp.getStatus().equals("SUCCESS"))
			if (counter == 0) {
				errorText = "No customer found";
				errorCode = "BKS0000";
				throw new BksDataException();
			} else if (counter > 20) {
				errorText = "Too many customers found for given search criteria.";
				errorCode = "BKS0015";
				resp.getCustomers().clear();
				throw new BksDataException();
			}

		return resp;
	}

	private ArrayList<HashMap<String, Object>> getCustomerByAccessNumber(
			GetCustomerDataCCMDAO dao, ComplexPhoneNumber accnum) throws Exception {
		ArrayList<HashMap<String, Object>> custList = null;
		String countryCode = accnum.getCountryCode();
		String cityCode = accnum.getLocalAreaCode();
		String localNumber = accnum.getPhoneNumbers().getPhoneNumber().get(0);
		custList = dao.getCustomerExactAccessNumberCustData(
				countryCode, cityCode, localNumber);
		if (custList == null || custList.size() == 0) {
			custList = dao.getCustomerAccessNumberCustData(countryCode,
					cityCode, localNumber);
		}
		return custList;
	}

	private ArrayList<HashMap<String, Object>> getCustomerByCompanyData(
			GetCustomerDataCCMDAO dao, CompanyDataQueryInput company) throws Exception {
		ArrayList<HashMap<String, Object>> custList = null;
		String name = company.getCompanyName();
		String zipCode = company.getZipCode();
		String street = company.getStreet();
		String streetNo = company.getHousenumber();
		String streetNoSuf = company.getHousenumberSuffix();
		String orgSuf = company.getCompanySuffixName();
		String regNo = company.getCommercialRegisterNumber();
		CalendarDate dateOfBirth = null;
		if (company.getContact() != null)
			dateOfBirth = company.getContact().getDateOfBirth();

		if (name != null && zipCode != null && street != null) {
			custList = dao.getCustomerCompanyData(name, zipCode,
					street, streetNo,streetNoSuf,orgSuf,regNo);
		}
		if (custList!= null && custList.size() > 0) {
			if (dateOfBirth != null){
				String customerNo = custList.get(0).get("CUSTOMER_NUMBER").toString();
				ArrayList<HashMap<String, Object>> contacts = dao.getOwnerContact(customerNo);
				if (contacts!= null && contacts.size() > 0 ){
					Object bd = contacts.get(0).get("BIRTH_DATE");
					if (!dateOfBirth.equals(CalendarDate.from(bd.toString(), "yyyy-MM-dd"))){
						errorText = "No customer found";
						errorCode = "BKS0000";
						throw new BksDataException();
					}
				}
			} else if (regNo != null) {
				String register = (String)custList.get(0).get("INCORPORATION_NUMBER_ID");
				if (!regNo.equals(register)){
					errorText = "No customer found";
					errorCode = "BKS0000";
					throw new BksDataException();
				}
			} else {
				String corpType = (String)custList.get(0).get("INCORPORATION_TYPE_RD");
				if (corpType != null && !corpType.equals("UNREGISTERED") && !corpType.equals("NOT")){
					errorText = "No customer found";
					errorCode = "BKS0000";
					throw new BksDataException();
				}
			}
		}
		return custList;
	}

	private ArrayList<HashMap<String, Object>> getCustomerByPersonData(
			GetCustomerDataCCMDAO dao, PersonDataQueryInput person) throws Exception {
		ArrayList<HashMap<String, Object>> custList = null;
		String familyName = person.getFamilyName();
		String zipCode = person.getZipCode();
		String street = person.getStreet();
		String firstName = person.getFirstName();
		CalendarDate dateOfBirth = person.getDateOfBirth();

		if(firstName != null){				
			if (!testWildcardFormat(firstName)) {
				errorText = "Wrong usage of wildcards.";
				errorCode = "BKS0021";
				throw new BksDataException();
			} else {					
				if (firstName != null)
					firstName = firstName.replaceAll( "\\*", "%");
			}
		}	

		if (!testWildcardFormat(familyName)|| !testWildcardFormat(street)) {
			errorText = "Wrong usage of wildcards.";
			errorCode = "BKS0021";
			throw new BksDataException();
		} else {
			if (familyName != null)
				familyName = familyName.replaceAll( "\\*", "%");
			if (street != null)
				street = street.replaceAll( "\\*", "%");
		}					
		if (familyName != null && zipCode != null && street != null) {
			custList = dao.getCustomerPersonData(familyName, zipCode,
					street, firstName, dateOfBirth);
		}
		return custList;
	}

	private ComplexPhoneNumber populatePhoneNumberData(
			GetCustomerDataCCMDAO dao, String customerNumber) throws Exception {

		ArrayList<HashMap<String, Object>> phoneDataList = dao
				.getPhoneNumberData(customerNumber);
		ComplexPhoneNumber phoneNumber = null;
		PhoneNumbers phoneNumbers = null;
		PhoneNumberRanges phoneNumberRanges = null;

		if (phoneDataList != null && phoneDataList.size() > 0) {
			phoneNumber = new ComplexPhoneNumber();
			HashMap<String, Object> phoneRow = null;
			Iterator phoneIter = phoneDataList.iterator();
			String firstTypeRd = null;
			while (phoneIter.hasNext()) {
				phoneRow = (HashMap<String, Object>) phoneIter.next();

				String anStr = null;
				String anType = null;
				String[] anArray;
				if (phoneRow.get("ACCESS_NUMBER") != null)
					anStr = phoneRow.get("ACCESS_NUMBER").toString();
				if (phoneRow.get("TYPE_RD") != null)
					anType = phoneRow.get("TYPE_RD").toString();
				if (anType != null && anStr != null) {
					anArray = anStr.split(";");
					phoneNumber.setCountryCode(anArray[0]);
					phoneNumber.setLocalAreaCode(anArray[1]);
					if (anType.equals("MAIN_ACCESS_NUM")
							&& (firstTypeRd == null || firstTypeRd
									.equals("MAIN_ACCESS_NUM"))) {
						if (firstTypeRd == null)
							phoneNumbers = new PhoneNumbers();
						phoneNumbers.getPhoneNumber().add(anArray[2]);

						phoneNumber.setPhoneNumbers(phoneNumbers);
						firstTypeRd = "MAIN_ACCESS_NUM";
					} else if (anType.equals("ACC_NUM_RANGE")
							&& (firstTypeRd == null || firstTypeRd
									.equals("ACC_NUM_RANGE"))) {
						if (firstTypeRd == null)
							phoneNumberRanges = new PhoneNumberRanges();

						phoneNumberRanges.setPilotNumber(anArray[2]);
						PhoneNumberRange phoneNumberRange = new PhoneNumberRange();
						phoneNumberRange.setStartRange(anArray[3]);
						phoneNumberRange.setEndRange(anArray[4]);
						phoneNumberRanges.getPhoneNumberRange().add(
								phoneNumberRange);

						phoneNumber.setPhoneNumberRanges(phoneNumberRanges);
						firstTypeRd = "ACC_NUM_RANGE";
					} else if (anType.equals("MOBIL_ACCESS_NUM")
							&& (firstTypeRd == null || firstTypeRd
									.equals("MOBIL_ACCESS_NUM"))) {
						if (firstTypeRd == null)
							phoneNumbers = new PhoneNumbers();

						phoneNumbers.getPhoneNumber().add(anArray[2]);
						phoneNumber.setPhoneNumbers(phoneNumbers);
						firstTypeRd = "MOBIL_ACCESS_NUM";
					}

				}

			}
		}
		return phoneNumber;
	}

	private Address populateAddressData(GetCustomerDataCCMDAO dao,
			String customerNumber) throws Exception {

		ArrayList<HashMap<String, Object>> addressDataList = dao
				.getAddressData(customerNumber);
		Address addressVf = null;
		if (addressDataList != null && addressDataList.size() > 0) {
			addressVf = new Address();

			if (addressDataList.get(0).get("POSTAL_CODE") != null)
				addressVf.setZipCode(addressDataList.get(0).get("POSTAL_CODE")
						.toString());
			if (addressDataList.get(0).get("CITY_NAME") != null)
				addressVf.setCity(addressDataList.get(0).get("CITY_NAME")
						.toString());
			if (addressDataList.get(0).get("CITY_SUFFIX_NAME") != null)
				addressVf.setCitySuffix(addressDataList.get(0).get(
						"CITY_SUFFIX_NAME").toString());
			if (addressDataList.get(0).get("COUNTRY_RD") != null)
				addressVf.setCountry(addressDataList.get(0).get("COUNTRY_RD")
						.toString());
			if (addressDataList.get(0).get("STREET_NAME") != null)
				addressVf.setStreet(addressDataList.get(0).get("STREET_NAME")
						.toString());
			if (addressDataList.get(0).get("STREET_NUMBER") != null)
				addressVf.setHousenumber(addressDataList.get(0).get(
						"STREET_NUMBER").toString());
			if (addressDataList.get(0).get("STREET_NUMBER_SUFFIX") != null)
				addressVf.setHousenumberSuffix(addressDataList.get(0).get(
						"STREET_NUMBER_SUFFIX").toString());
			if (addressDataList.get(0).get("POST_OFFICE_BOX") != null)
				addressVf.setPostbox(addressDataList.get(0).get(
						"POST_OFFICE_BOX").toString());
		}
		return addressVf;
	}

	private Contact populateContactData(HashMap<String, Object> contactDataRow,
			String customerNumber) throws Exception {

		Contact contactVf = null;
		if (contactDataRow != null) {
			contactVf = new Contact();

			if (contactDataRow.get("CONTACT_ROLE_TYPE_RD") != null)
				contactVf.setPosition(contactDataRow
						.get("CONTACT_ROLE_TYPE_RD").toString());
			if (contactDataRow.get("NAME") != null)
				contactVf.setFamilyName(contactDataRow.get("NAME").toString());
			if (contactDataRow.get("FORENAME") != null)
				contactVf.setFirstName(contactDataRow.get("FORENAME")
						.toString());
			if (contactDataRow.get("SALUTATION_DESCRIPTION") != null)
				contactVf.setSalutation(contactDataRow.get(
						"SALUTATION_DESCRIPTION").toString());
			if (contactDataRow.get("TITLE_DESCRIPTION") != null)
				contactVf.setTitle(contactDataRow.get("TITLE_DESCRIPTION")
						.toString());
			if (contactDataRow.get("SURNAME_PREFIX_DESCRIPTION") != null)
				contactVf.setNameSuffix(contactDataRow.get(
						"SURNAME_PREFIX_DESCRIPTION").toString());
			if (contactDataRow.get("NOBILITY_PREFIX_DESCRIPTION") != null)
				contactVf.setTitleOfNobility(contactDataRow.get(
						"NOBILITY_PREFIX_DESCRIPTION").toString());
			if (contactDataRow.get("BIRTH_DATE") != null)
				contactVf.setDateOfBirth(CalendarDate.from(contactDataRow.get(
						"BIRTH_DATE").toString(), "yyyy-MM-dd"));
		}
		return contactVf;
	}

	private Company populateCompanyData(GetCustomerDataCCMDAO dao,
			String customerNumber) throws Exception {

		ArrayList<HashMap<String, Object>> companyDataList = dao
				.getCompanyData(customerNumber);
		Company companyVf = null;
		if (companyDataList != null && companyDataList.size() > 0) {
			companyVf = new Company();
			if (companyDataList.get(0).get("NAME") != null)
				companyVf.setCompanyName(companyDataList.get(0).get("NAME")
						.toString());
			if (companyDataList.get(0).get("ORGANIZATION_TYPE_RD") != null)
				companyVf.setLegalForm(companyDataList.get(0).get(
						"ORGANIZATION_TYPE_RD").toString());

			CommercialRegister commercialReg = new CommercialRegister();
			if (companyDataList.get(0).get("INCORPORATION_NUMBER_ID") != null)
				commercialReg.setCommercialRegisterNumber(companyDataList
						.get(0).get("INCORPORATION_NUMBER_ID").toString());
			if (companyDataList.get(0).get("INCORPORATION_CITY_NAME") != null)
				commercialReg.setCommercialRegisterCity(companyDataList.get(0)
						.get("INCORPORATION_CITY_NAME").toString());
			if (companyDataList.get(0).get("INCORPORATION_TYPE_RD") != null)
				commercialReg.setKindOfCommerRegister(companyDataList.get(0)
						.get("INCORPORATION_TYPE_RD").toString());
			companyVf.setCommercialRegister(commercialReg);

			ArrayList<HashMap<String, Object>> contactDataList = dao
					.getContactData(customerNumber);

			if (contactDataList != null && contactDataList.size() > 0) {
				Iterator conatctIter = contactDataList.iterator();
				while (conatctIter.hasNext()) {
					HashMap<String, Object> contactRow = (HashMap<String, Object>) conatctIter
							.next();
					Contact contactVf = null;
					if (contactRow != null) {
						contactVf = populateContactData(contactRow,
								customerNumber);
					}
					if (companyVf != null && contactVf != null) {
						companyVf.getContact().add(contactVf);
					}

				}
			}
		}

		return companyVf;
	}

	private Person populatePersonData(GetCustomerDataCCMDAO dao,
			String customerNumber) throws Exception {

		ArrayList<HashMap<String, Object>> personDataList = dao
				.getPersonData(customerNumber);
		Person personVf = null;
		if (personDataList != null && personDataList.size() > 0) {
			personVf = new Person();

			if (personDataList.get(0).get("NAME") != null)
				personVf.setFamilyName(personDataList.get(0).get("NAME")
						.toString());
			if (personDataList.get(0).get("FORENAME") != null)
				personVf.setFirstName(personDataList.get(0).get("FORENAME")
						.toString());
			if (personDataList.get(0).get("SALUTATION_DESCRIPTION") != null)
				personVf.setSalutation(personDataList.get(0).get(
						"SALUTATION_DESCRIPTION").toString());
			if (personDataList.get(0).get("TITLE_DESCRIPTION") != null)
				personVf.setTitle(personDataList.get(0)
						.get("TITLE_DESCRIPTION").toString());
			if (personDataList.get(0).get("SURNAME_PREFIX_DESCRIPTION") != null)
				personVf.setNameSuffix(personDataList.get(0).get(
						"SURNAME_PREFIX_DESCRIPTION").toString());
			if (personDataList.get(0).get("NOBILITY_PREFIX_DESCRIPTION") != null)
				personVf.setTitleOfNobility(personDataList.get(0).get(
						"NOBILITY_PREFIX_DESCRIPTION").toString());
			if (personDataList.get(0).get("BIRTH_DATE") != null)
				personVf.setDateOfBirth(CalendarDate.from(personDataList.get(0)
						.get("BIRTH_DATE").toString(), "yyyy-MM-dd"));
		}
		return personVf;
	}

	private String getProviderCode(GetCustomerDataCCMDAO dao,
			String customerNumber, String custClass) throws Exception {
		String providerCode = BksRefDataCacheHandler.getClassProviderMap().get(
				custClass);
		if (providerCode == null) {
			ArrayList<HashMap<String, Object>> rootList = dao
					.getRootCustomerCustData(customerNumber);
			if (rootList != null && rootList.size() > 0) {
				String rootNumber = rootList.get(0).get("CUSTOMER_NUMBER")
						.toString();
				HashMap<String, String> providerCodes = BksRefDataCacheHandler
						.getProviderCode();
				if (providerCodes.get(rootNumber) != null)
					providerCode = providerCodes.get(rootNumber);
				else
					providerCode = "ARCO";
			} else {
				errorCode = "BKS0020";
				errorText = "The data for the customer " + customerNumber
						+ " is corrupted. Can not determine the provider code.";
				throw new BksDataException();
			}
		}
		return providerCode;
	}

	public boolean testWildcardFormat(String str) {
		if(str == null)
			return false;
		int len = str.length();
		if (len == 0)
			return false;
		String star = "*";
		int firstOcur = str.indexOf(star, 0);

		if (firstOcur != -1 && firstOcur != (len - 1))
			return false;

		return true;
	}

}
