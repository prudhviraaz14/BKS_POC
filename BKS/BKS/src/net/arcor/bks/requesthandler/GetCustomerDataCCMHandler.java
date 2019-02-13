/*
    $Header:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/GetCustomerDataCCMHandler.java-arc   1.13   Nov 22 2011 12:31:48   makuier  $

    $Log:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/GetCustomerDataCCMHandler.java-arc  $
 * 
 *    Rev 1.13   Nov 22 2011 12:31:48   makuier
 * Default salutaion.
 * 
 *    Rev 1.12   Oct 12 2011 15:20:16   makuier
 * Filter too short and too long phone numbers out.
 * 
 *    Rev 1.11   Nov 05 2010 15:54:56   makuier
 * do not add null to the list.
 * 
 *    Rev 1.10   Aug 05 2010 11:40:02   makuier
 * Remove leading zeros fro country code and area code.
 * 
 *    Rev 1.9   Mar 19 2010 15:47:52   makuier
 * Rolled back to revision 1.7
 * 
 *    Rev 1.8   Feb 04 2010 17:51:28   makuier
 * Changed the package
 * 
 *    Rev 1.7   Jan 28 2010 16:46:08   makuier
 * Some refactoring. Cache the exception and return an error XML.
 * 
 *    Rev 1.6   May 27 2009 16:05:56   makuier
 * MCF2 adaption
 * 
 *    Rev 1.5   Mar 19 2009 17:01:52   wlazlow
 * IT-k-25454
 * 
 *    Rev 1.4   Feb 10 2009 11:13:04   wlazlow
 * IT-k-24919, SPN-BKS-000081616
 * 
 *    Rev 1.3   Nov 19 2008 17:07:10   wlazlow
 * SPN-BKS-00079790
 * 
 *    Rev 1.2   Oct 30 2008 20:05:56   wlazlow
 * IT-23530, version with correct group code VFSERCLAS
 * 
 *    Rev 1.1   Oct 29 2008 14:29:56   wlazlow
 * IT-23530
 * 
 *    Rev 1.0   Oct 28 2008 16:14:22   wlazlow
 * Initial revision.
 * 
 *
 */
package net.arcor.bks.requesthandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import net.arcor.bks.client.DatabaseClient;
import net.arcor.bks.common.BksDataException;
import net.arcor.bks.common.BksRefDataCacheHandler;
import net.arcor.bks.db.GetCustomerDataCCMDAO;
import net.arcor.epsm_basetypes_001.InputData;

import com.domainlanguage.time.CalendarDate;

import de.arcor.aaw.kernAAW.bks.services.GetCustomerDataCCMInput;
import de.arcor.aaw.kernAAW.bks.services.GetCustomerDataCCMOutput;
import de.vodafone_arcor.linkdb.epsm_whsvf_commontypes_001.Address;
import de.vodafone_arcor.linkdb.epsm_whsvf_commontypes_001.ArcorCustomerOrigin;
import de.vodafone_arcor.linkdb.epsm_whsvf_commontypes_001.CommercialRegister;
import de.vodafone_arcor.linkdb.epsm_whsvf_commontypes_001.Company;
import de.vodafone_arcor.linkdb.epsm_whsvf_commontypes_001.ComplexPhoneNumber;
import de.vodafone_arcor.linkdb.epsm_whsvf_commontypes_001.Contact;
import de.vodafone_arcor.linkdb.epsm_whsvf_commontypes_001.CustomerData;
import de.vodafone_arcor.linkdb.epsm_whsvf_commontypes_001.ErrorType;
import de.vodafone_arcor.linkdb.epsm_whsvf_commontypes_001.Person;
import de.vodafone_arcor.linkdb.epsm_whsvf_commontypes_001.PersonDataQueryInput;
import de.vodafone_arcor.linkdb.epsm_whsvf_commontypes_001.PhoneNumberRange;
import de.vodafone_arcor.linkdb.epsm_whsvf_commontypes_001.PhoneNumberRanges;
import de.vodafone_arcor.linkdb.epsm_whsvf_commontypes_001.PhoneNumbers;
import de.vodafone_arcor.linkdb.epsm_whsvf_wsdl_services.CustomerDataCCMRequest;
import de.vodafone_arcor.linkdb.epsm_whsvf_wsdl_services.CustomerDataCCMResponse;

/**
 * @author MAKUIER
 * 
 */
public class GetCustomerDataCCMHandler extends BaseServiceHandler {

	boolean isIndividual = true;
	String[] errorParams = null;

	public String execute() throws Exception {
		String returnXml = null;

		CustomerDataCCMResponse resp = null;
		GetCustomerDataCCMDAO dao = (GetCustomerDataCCMDAO) DatabaseClient
				.getBksDataAccessObject(null, "GetCustomerDataCCMDAO");

		try {
			resp = getCustomerData(dao,((GetCustomerDataCCMInput) input).getCustomerDataCCMRequest());
			returnXml = populateMasterData(resp);

		} catch (BksDataException e) {
			logger.error(e.getMessage());
			serviceStatus = Status.PRECONDITION_ERROR;
			errorCode = "BKS0008";
			errorText = e.getMessage();
			returnXml = populateMasterData(null);
		} catch (Exception e) {
			logger.error(e.getMessage());
			serviceStatus = Status.PRECONDITION_ERROR;
			errorCode = "BKS0008";
			errorText = e.getMessage();
			returnXml = populateMasterData(null);
		} finally {
			dao.closeConnection();
		}
		return returnXml;
	}

	CustomerDataCCMResponse getCustomerData(CustomerDataCCMRequest request) throws Exception{
		CustomerDataCCMResponse resp = null;
		GetCustomerDataCCMDAO dao = (GetCustomerDataCCMDAO) DatabaseClient
				.getBksDataAccessObject(null, "GetCustomerDataCCMDAO");

		try {
			setCustomerNumber(request.getCustomerNumber());
			resp = getCustomerData(dao,request);
		}
		finally {
			dao.closeConnection();
		}
		return resp;
	}

	private CustomerDataCCMResponse getCustomerData(GetCustomerDataCCMDAO dao, CustomerDataCCMRequest request) throws Exception{
	   CustomerDataCCMResponse resp = new CustomerDataCCMResponse();
	   String barcode = request.getBarcode();

	   String familyName = null;
	   String zipCode = null;
	   String street = null;
	   String firstName = null;
	   CalendarDate dateOfBirth = null;

	   PersonDataQueryInput person = request.getPersonData();
	   if (person != null) {
		   String familyNameTmp = person.getFamilyName();
		   zipCode = person.getZipCode();
		   String streetTmp = person.getStreet();
		   String firstNameTmp = person.getFirstName();
		   dateOfBirth = person.getDateOfBirth();

		   if(firstNameTmp != null){				
			   if (!testWildcardFormat(firstNameTmp)) {
				   serviceStatus = Status.FUNCTIONAL_ERROR;
				   errorText = "Wrong usage of wildcards.";
				   errorCode = "BKS0021";
			   } else {					
				   if (firstNameTmp != null)
					   firstName = replace(firstNameTmp, "*", "%");
			   }
		   }	

		   if (!testWildcardFormat(familyNameTmp)|| !testWildcardFormat(streetTmp)) {
			   serviceStatus = Status.FUNCTIONAL_ERROR;
			   errorText = "Wrong usage of wildcards.";
			   errorCode = "BKS0021";
		   } else {
			   if (familyNameTmp != null)
				   familyName = replace(familyNameTmp, "*", "%");
			   if (streetTmp != null)
				   street = replace(streetTmp, "*", "%");						
		   }					
	   }

	   ComplexPhoneNumber accnum = request.getAccessNumber();

	   String countryCode = null;
	   String cityCode = null;
	   String localNumber = null;

	   if (accnum != null) {
		   if (accnum.getCountryCode().startsWith("00"))
			   countryCode = accnum.getCountryCode().substring(2);
		   else if (accnum.getCountryCode().startsWith("0") || accnum.getCountryCode().startsWith("+"))
			   countryCode = accnum.getCountryCode().substring(1);
		   else
			   countryCode = accnum.getCountryCode();
		   if (accnum.getLocalAreaCode().startsWith("0"))
			   cityCode = accnum.getLocalAreaCode().substring(1);
		   else
			   cityCode = accnum.getLocalAreaCode();
		   localNumber = accnum.getPhoneNumbers().getPhoneNumber().get(0);
	   }

	   ArrayList<HashMap<String, Object>> custList = null;

	   if (customerNumber != null) {
		   custList = dao.getCustomerCustData(customerNumber);
	   } else if (accnum != null) {
		   custList = dao.getCustomerExactAccessNumberCustData(
				   countryCode, cityCode, localNumber);
		   if (custList == null || custList.size() == 0) {
			   custList = dao.getCustomerAccessNumberCustData(countryCode,
					   cityCode, localNumber);
		   }
	   } else if (barcode != null) {
		   custList = dao.getCustomerForBarCode(barcode);
	   } else if (familyName != null && zipCode != null && street != null) {
		   custList = dao.getCustomerPersonData(familyName, zipCode,
				   street, firstName, dateOfBirth);
	   }

	   if (serviceStatus != Status.FUNCTIONAL_ERROR)
		   if ((custList == null) || (custList.size() < 1)) {
			   serviceStatus = Status.FUNCTIONAL_ERROR;
			   errorText = "No customer found";
			   errorCode = "BKS0000";
			   // returnXml = populateMasterData(null);
			   // return returnXml;
		   }

	   HashMap<String, Object> custRow = null;
	   int counter = 0;
	   if (custList != null && custList.size() > 0
			   && serviceStatus != Status.FUNCTIONAL_ERROR) {
		   Iterator custIter = custList.iterator();

		   while (serviceStatus != Status.FUNCTIONAL_ERROR
				   && custList != null && (custList.size() > 0)
				   && custIter.hasNext()) {
			   custRow = (HashMap<String, Object>) custIter.next();

			   customerNumber = custRow.get("CUSTOMER_NUMBER").toString();
			   String custType = null;
			   isIndividual = custRow.get("ENTITY_TYPE").toString()
			   .equals("I");
			   if (isIndividual)
				   custType = "NatuerlichePerson";
			   else
				   custType = "JuristischePerson";
			   String custCategory = custRow.get("CATEGORY_RD").toString();
			   String custClass = custRow.get("CLASSIFICATION_RD")
			   .toString();
			   String providerCode = getProviderCode(dao, customerNumber,
					   custClass);
			   CustomerData custData = null;
			   ArrayList<String> gciList = BksRefDataCacheHandler
			   .getGeneralData().get("CLDBSVPBKS");
			   if (gciList != null && gciList.contains(providerCode)) {
				   counter++;
				   if (counter <= 20) {
					   custData = new CustomerData();
					   if (isIndividual) {
						   Person personVf = populatePersonData(dao,
								   customerNumber);
						   if (personVf != null)
							   custData.setPerson(personVf);
					   } else {
						   Company companyVf = populateCompanyData(dao,
								   customerNumber);
						   if (companyVf != null)
							   custData.setCompany(companyVf);
					   }

					   Address addressVf = populateAddressData(dao,
							   customerNumber);
					   if (addressVf != null)
						   custData.setAddress(addressVf);

					   // String emailVf =
					   // populateEmailData(dao,customerNumber);
					   // if(emailVf != null)
					   // custData.setEmail(emailVf);

					   ComplexPhoneNumber phoneNumber = populatePhoneNumberData(
							   dao, customerNumber);
					   if (phoneNumber != null)
						   custData.setPhoneNumber(phoneNumber);

					   custData.setCustomerNumber(customerNumber);
					   if (providerCode.substring(0, 1).equals("V"))
						   custData
						   .setArcorCustomerOrigin(ArcorCustomerOrigin.V);
					   else
						   custData
						   .setArcorCustomerOrigin(ArcorCustomerOrigin.A);
				   } else {
					   break;
				   }
				   resp.getCustomers().add(custData);
			   }


			   if (serviceStatus == Status.SUCCESS)
				   theHelper.prefetch(customerNumber, accessNumber,
						   "GetCustomerDataCCM", input, output);

		   } // end of loop over customers

	   } // end of if

	   if (serviceStatus != Status.FUNCTIONAL_ERROR)
		   if (counter == 0) {
			   serviceStatus = Status.FUNCTIONAL_ERROR;
			   errorText = "No customer found";
			   errorCode = "BKS0000";
		   } else if (counter > 20) {
			   serviceStatus = Status.FUNCTIONAL_ERROR;
			   errorText = "Too many customers found for given search criteria.";
			   errorCode = "BKS0015";
			   resp.getCustomers().clear();
		   }

	   if (serviceStatus == Status.SUCCESS) {
		   resp.setStatus("SUCCESS");
	   } else {
		   resp.setStatus("FAILED");

		   ErrorType resDetObj = new ErrorType();
		   resDetObj.setErrorCode(errorCode);
		   resDetObj.setErrorDescription(errorText);
		   resp.setErrorDetails(resDetObj);

	   }
	   return resp;
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

	private String populateEmailData(GetCustomerDataCCMDAO dao,
			String customerNumber) throws Exception {

		ArrayList<HashMap<String, Object>> emailDataList = dao
				.getEmailData(customerNumber);
		String emailVf = null;
		if (emailDataList != null && emailDataList.size() != 0) {
			if (emailDataList.get(0).get("EMAIL_ADDRESS") != null)
				emailVf = emailDataList.get(0).get("EMAIL_ADDRESS").toString();
		}
		return emailVf;
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
						if (anArray[2].length()>=3 && anArray[2].length()<= 20) {
						   phoneNumbers.getPhoneNumber().add(anArray[2]);
						   phoneNumber.setPhoneNumbers(phoneNumbers);
						}
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

						if (anArray[2].length()>=3 && anArray[2].length()<= 20) {
							phoneNumbers.getPhoneNumber().add(anArray[2]);
							phoneNumber.setPhoneNumbers(phoneNumbers);
						}
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
			if (personDataList.get(0).get("SALUTATION_DESCRIPTION") != null) {
				String salutation = personDataList.get(0).get("SALUTATION_DESCRIPTION").toString();
				if (!salutation.equals("Herr") && !salutation.equals("Frau") && !salutation.equals("Firma")) {
					if (isIndividual)
						personVf.setSalutation("Herr");
					else
						personVf.setSalutation("Firma");
				} else {
					personVf.setSalutation(personDataList.get(0).get("SALUTATION_DESCRIPTION").toString());
				}
			} 
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
			if (contactDataRow.get("SALUTATION_DESCRIPTION") != null){
				String salutation = contactDataRow.get("SALUTATION_DESCRIPTION").toString();
				if (!salutation.equals("Herr") && !salutation.equals("Frau") && !salutation.equals("Firma")) {
					if (isIndividual)
						contactVf.setSalutation("Herr");
					else
						contactVf.setSalutation("Firma");
				} else {
					contactVf.setSalutation(contactDataRow.get("SALUTATION_DESCRIPTION").toString());
				}
			} 
				
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
				serviceStatus = Status.FUNCTIONAL_ERROR;
				errorCode = "BKS0020";
				errorText = "The data for the customer " + customerNumber
						+ " is corrupted. Can not determine the provider code.";
				return null;
			}
		}
		return providerCode;
	}

	private String populateMasterData(CustomerDataCCMResponse resp)
			throws Exception {
		output = new GetCustomerDataCCMOutput();
		String returnXml = null;
		populateBaseOutput();

		if (serviceStatus == Status.SUCCESS
				|| serviceStatus == Status.FUNCTIONAL_ERROR) {

			((GetCustomerDataCCMOutput) output)
					.setCustomerDataCCMResponse(resp);

		}
		try {
			returnXml = theHelper.serializeForMcf(output, customerNumber,
					accessNumber, "de.arcor.aaw.kernAAW.bks.services",
					"GetCustomerDataCCM");
			if (serviceStatus == Status.SUCCESS && customerNumber != null)
				theHelper.loadCache(customerNumber + ";GetCustomerDataCCM",
						returnXml);
			if (serviceStatus == Status.SUCCESS && accessNumber != null)
				theHelper.loadCache(accessNumber + ";GetCustomerDataCCM",
						returnXml);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return returnXml;
	}

	public void setInput(InputData input) {
		super.setInput(input);
		setCustomerNumber(((GetCustomerDataCCMInput) input)
				.getCustomerDataCCMRequest().getCustomerNumber());
		ComplexPhoneNumber accnum = ((GetCustomerDataCCMInput) input)
				.getCustomerDataCCMRequest().getAccessNumber();
		if (accnum != null) {
			String countryCode = accnum.getCountryCode();
			String cityCode = accnum.getLocalAreaCode();
			String localNumber = accnum.getPhoneNumbers().getPhoneNumber().get(
					0);

			accessNumber = countryCode + cityCode + localNumber;
		}
	}

	public String replace(String str, String pattern, String replace) {
		int s = 0;
		int e = 0;
		StringBuffer result = new StringBuffer();

		while ((e = str.indexOf(pattern, s)) >= 0) {
			result.append(str.substring(s, e));
			result.append(replace);
			s = e + pattern.length();
		}
		result.append(str.substring(s));
		return result.toString();
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
