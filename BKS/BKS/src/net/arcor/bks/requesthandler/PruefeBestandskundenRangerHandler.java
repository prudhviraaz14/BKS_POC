/*
    $Header:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/PruefeBestandskundenRangerHandler.java-arc   1.20   Jan 28 2010 16:51:58   makuier  $

    $Log:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/PruefeBestandskundenRangerHandler.java-arc  $
 * 
 *    Rev 1.20   Jan 28 2010 16:51:58   makuier
 * Cache the exception and return an error XML.
 * 
 *    Rev 1.19   May 27 2009 16:05:54   makuier
 * MCF2 adaption
 * 
 *    Rev 1.18   Jan 12 2009 12:51:34   makuier
 * matching the name correctly
 * 
 *    Rev 1.17   May 30 2008 11:43:44   makuier
 * Use cross reference to determine
 * 
 *    Rev 1.16   May 13 2008 12:09:04   makuier
 * pass access number for caching
 * 
 *    Rev 1.15   Apr 18 2008 11:20:54   makuier
 * Pointer check
 * 
 *    Rev 1.14   Apr 07 2008 15:20:14   makuier
 * Use voice online mapping to find bundled service.
 * 
 *    Rev 1.13   Apr 01 2008 06:49:08   makuier
 * Choose a corrected bundled service.
 * 
 *    Rev 1.12   Feb 28 2008 10:15:32   makuier
 * removed pricing structure name.
 * 
 *    Rev 1.11   Feb 27 2008 10:38:50   makuier
 * Refactoring
 * 
 *    Rev 1.10   Feb 15 2008 15:31:10   makuier
 * populate access number variable for logging.
 * 
 *    Rev 1.9   Feb 05 2008 14:09:56   makuier
 * Check if all access number field are provided.
 * Use pc number for SDC when calculating the end date.
 * 
 *    Rev 1.8   Jan 31 2008 14:15:50   makuier
 * Match the customer number and the access number.
 * 
 *    Rev 1.7   Jan 22 2008 16:46:44   makuier
 * Raise a precondition error if the customer root data is corrupted.
 * 
 *    Rev 1.6   Dec 14 2007 12:37:00   makuier
 * Populate Vodafone service provider code.
 * 
 *    Rev 1.5   Dec 11 2007 19:32:40   makuier
 * use prepared statement.
 * 
 *    Rev 1.4   Dec 06 2007 18:07:04   makuier
 * Changed the instantiation of dao.
 * 
 *    Rev 1.3   Nov 28 2007 18:09:36   makuier
 * Only cache the successful results
 * 
 *    Rev 1.2   Nov 27 2007 14:05:46   makuier
 * Bug Fixes.
 * 
 *    Rev 1.1   Nov 12 2007 17:06:58   makuier
 * Changes for 1.25
 * 
 *    Rev 1.0   Nov 06 2007 17:40:40   makuier
 * Initial revision.
*/
package net.arcor.bks.requesthandler;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;

import net.arcor.bks.client.DatabaseClient;
import net.arcor.bks.common.BksDataException;
import net.arcor.bks.common.BksRefDataCacheHandler;
import net.arcor.bks.db.PruefeBestandskundenRangerDAO;
import net.arcor.epsm_basetypes_001.InputData;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.domainlanguage.time.CalendarDate;

import de.arcor.aaw.kernAAW.bks.services.PruefeBestandskundenRangerInput;
import de.arcor.aaw.kernAAW.bks.services.PruefeBestandskundenRangerOutput;

/**
 * @author MAKUIER
 *
 */
public class PruefeBestandskundenRangerHandler extends BaseServiceHandler {

	HashMap<String, Object> returnList = new HashMap<String, Object>();
	protected final Log logger = LogFactory.getLog(getClass());
	boolean bVoiceFound = false;
	boolean bInternetFound = false;
	String[] errorParams = null;

	public PruefeBestandskundenRangerHandler() {
		super();
	}

	public String execute() throws Exception {
		String returnXml = null;
		PruefeBestandskundenRangerDAO dao = 
			(PruefeBestandskundenRangerDAO)DatabaseClient.getBksDataAccessObject(null,"PruefeBestandskundenRangerDAO");

		try {
			String [] serviceData = retrieveMasterData(dao,(PruefeBestandskundenRangerInput)input);
			if (serviceData != null && serviceData.length == 2){
				boolean found = false;
				found = retrieveContractData(dao,serviceData[0],serviceData[1],true);
				if (!found && serviceStatus == Status.SUCCESS)
					found = retrieveContractData(dao,serviceData[0],serviceData[1],false);
				if (!found && serviceStatus == Status.SUCCESS){
					serviceStatus = Status.FUNCTIONAL_ERROR;
					errorText = "Call the Hotline";
					errorCode = "BKS0001";
				}
				String [] bundledServData = null;
				if (serviceStatus == Status.SUCCESS)
					bundledServData = retrieveBundledServSub(dao,serviceData[0],serviceData[1]);
				found = false;
				if (bundledServData != null && bundledServData.length == 2) {
					found = retrieveContractData(dao,bundledServData[0],bundledServData[1],true);
					if (!found && serviceStatus == Status.SUCCESS)
						found = retrieveContractData(dao,bundledServData[0],bundledServData[1],false);
				}
			}
			returnXml = populateMasterData();
			if (serviceStatus == Status.SUCCESS)
				theHelper.prefetch(customerNumber,accessNumber,"PruefeBestandskundenRanger",input,output);
		} catch (BksDataException e) {
			logger.error(e.getMessage());
			serviceStatus = Status.PRECONDITION_ERROR;
			errorCode = "BKS0008";
			errorText = e.getMessage();
			returnXml = populateMasterData();
		} catch (Exception e) {
			logger.error(e.getMessage());
			serviceStatus = Status.PRECONDITION_ERROR;
			errorCode = "BKS0008";
			errorText = e.getMessage();
			returnXml = populateMasterData();
		}
		finally {
			dao.closeConnection();
		}
		return returnXml;
	}

	private String [] retrieveBundledServSub(PruefeBestandskundenRangerDAO dao, String servSubId,String serviceCode) throws Exception{
		if (serviceStatus != Status.SUCCESS)
			return null;
		ArrayList<String> serviceCodeList =BksRefDataCacheHandler.getVoiceOnlineMap().get(serviceCode);
		String serviceCodes="";
		for (int i =0;serviceCodeList!= null&&i<serviceCodeList.size(); i++){
			serviceCodes += ","+serviceCodeList.get(i);
		}
		ArrayList<HashMap<String, Object>> theList = dao.getBundledService(servSubId,serviceCodes);
		if ((theList != null) && (theList.size() != 0)){
			String bundledServSubId = theList.get(0).get("SERVICE_SUBSCRIPTION_ID").toString();
			String bundledServCode = theList.get(0).get("SERVICE_CODE").toString();
			return new String[] {bundledServSubId,bundledServCode};
		}
		return null;
	}

	private String [] retrieveMasterData(PruefeBestandskundenRangerDAO dao, PruefeBestandskundenRangerInput input) throws Exception {
		if (serviceStatus != Status.SUCCESS)
			return null;
		String countryCode = input.getRufnummer().getLaenderkennzeichen();
		String cityCode = input.getRufnummer().getVorwahl();
		String localNumber = input.getRufnummer().getRufnummer();
		if (countryCode == null || cityCode == null || localNumber == null  ){
			serviceStatus = Status.PRECONDITION_ERROR;
			errorCode = "BKS0013";
			errorText = "All access number fields (country code, city code and local number) have to be provided.";
			return null;
		}
		boolean forenameMatch = theHelper.matchInput(input.getVorname());
		boolean lastnameMatch = theHelper.matchInput(input.getNachname());
		if (!forenameMatch||!lastnameMatch){
				serviceStatus = Status.PRECONDITION_ERROR;
				errorText = "The input is not correctly formatted.";
				errorCode = "BKS0006";
				if (!forenameMatch){
					errorText += "(" + input.getVorname() + ")";
					errorParams = new String [] {input.getVorname()};
				}
				if (!lastnameMatch){
					errorText += "(" + input.getNachname() + ")";
					errorParams = new String [] {input.getNachname()};
				}
				return null;
		}
		ArrayList<HashMap<String, Object>> accNumList = dao.getAccessNumber(countryCode,cityCode,localNumber);
		if ((accNumList != null) && (accNumList.size() != 0)){
			String custNumList = "";
			String cscIdList = "";
			for (int i = 0; i < accNumList.size(); i++) {
				custNumList += ","+accNumList.get(i).get("CUSTOMER_NUMBER").toString();
				cscIdList += ","+accNumList.get(i).get("CONFIG_SERVICE_CHAR_ID").toString();
			}
            String firstName = input.getVorname();
            String lastName = input.getNachname();
			ArrayList<HashMap<String, Object>> custList = dao.getCustomer(firstName,lastName,cscIdList,custNumList);
			if (custList == null || custList.size() == 0){
				serviceStatus = Status.FUNCTIONAL_ERROR;
				errorText = "Access number does not match the name";
				errorCode = "BKS0005";
				errorParams = new String [] {countryCode+" "+cityCode+" "+localNumber,
											input.getVorname()+" "+input.getNachname()};
				return null;
			}
			if (customerNumber == null){
				customerNumber = custList.get(0).get("CUSTOMER_NUMBER").toString();
			} else if (!customerNumber.equals(custList.get(0).get("CUSTOMER_NUMBER").toString())){
				serviceStatus = Status.PRECONDITION_ERROR;
				errorCode = "BKS0011";
				errorText = "The access number "+countryCode+"/"+cityCode+"/"+localNumber+
				            " does not belong to  the customer "+customerNumber+".";
				return null;
			}
			String serviceSubscrId = custList.get(0).get("SERVICE_SUBSCRIPTION_ID").toString();
			String serviceCode = custList.get(0).get("SERVICE_CODE").toString();
			String category = custList.get(0).get("CATEGORY_RD").toString();
			if (category.equals("BUSINESS"))
				category = "Geschäftskunde";
			else if (category.equals("RESIDENTIAL"))
				category = "Privatkunde";
			else if (category.equals("SERVICE_PROVIDER"))
				category = "Service Provider";
			String classification = custList.get(0).get("CLASSIFICATION_RD").toString();

			String providerCode = getProviderCode(dao,customerNumber,classification);
			returnList.put("CUSTOMER;CUSTOMER_NUMBER;NatuerlichePerson", customerNumber);
			returnList.put("CUSTOMER;P_SERVICE_PROVIDER_CODE;NatuerlichePerson", providerCode);
			returnList.put("CUSTOMER;CATEGORY_RD;NatuerlichePerson", category);
			returnList.put("CUSTOMER;CLASSIFICATION_RD;NatuerlichePerson", classification);
			return new String[] {serviceSubscrId,serviceCode};
		} else {
			serviceStatus = Status.FUNCTIONAL_ERROR;
			errorText = "No customer found";
			errorCode = "BKS0000";
		}
		return null;
	}

	
	private String getProviderCode(PruefeBestandskundenRangerDAO dao,
			String customerNumber, String classification) throws Exception {
		String providerCode = BksRefDataCacheHandler.getClassProviderMap().get(classification);
		if (providerCode == null){
			ArrayList<HashMap<String, Object>> theRootList = dao.getRootCustomer(customerNumber);
			if (theRootList == null || theRootList.size() == 0) {
				serviceStatus = Status.PRECONDITION_ERROR;
				errorCode = "BKS0010";
				errorText = "The data for the customer "+customerNumber+" is corrupted.";
				return null;
			}
			String rootNumber = theRootList.get(0).get("CUSTOMER_NUMBER").toString();
			HashMap<String,String> providerCodes =BksRefDataCacheHandler.getProviderCode();
			if (providerCodes.get(rootNumber) != null)
				providerCode = providerCodes.get(rootNumber);
			else
				providerCode = "ARCO";
		}
		return providerCode;
	}

	private boolean retrieveContractData(PruefeBestandskundenRangerDAO dao, String servSubId, String servCode,boolean isOrderForm) throws Exception{
		if (serviceStatus != Status.SUCCESS)
			return false;

		HashMap<String,String> functions =BksRefDataCacheHandler.getFunction();
		String ape = functions.get(servCode);
		if (ape == null){
			serviceStatus = Status.FUNCTIONAL_ERROR;
			errorText = "No Function defined for the main access service code "+servCode;
			errorCode = "BKS0009";
			errorParams = new String [] {servCode};
			return false;
		}

		ArrayList<HashMap<String, Object>> cntrList = null;
		if (isOrderForm)
			cntrList = dao.getOFContractData(servSubId);
		else
			cntrList = dao.getSDContractData(servSubId);

		if ((cntrList == null) || (cntrList.size() == 0)){
			return false;
		} else if (cntrList.size() != 1){
			serviceStatus = Status.FUNCTIONAL_ERROR;
			errorText = "Call the Hotline";
			errorCode = "BKS0001";
			return false;
		}
		returnList.put("P_PRODUCT_COMMITMENT;PRODUCT_CODE;"+ape, cntrList.get(0).get("PRODUCT_CODE").toString());
		returnList.put("P_PRODUCT_COMMITMENT;PRODUCT_VERSION_CODE;"+ape, cntrList.get(0).get("PRODUCT_VERSION_CODE").toString());
		returnList.put("P_PRODUCT_COMMITMENT;PRICING_STRUCTURE_CODE;"+ape, cntrList.get(0).get("PRICING_STRUCTURE_CODE").toString());
		returnList.put("COMMISSIONING_INFORMATION;CIO_DATA;"+ape, cntrList.get(0).get("CIO_DATA").toString());
		Date date = null;
		String cntNumber = null;
		if (isOrderForm){
			cntNumber = cntrList.get(0).get("CONTRACT_NUMBER").toString();
			date = dao.getOFEndDate(cntNumber);
		} else {
			cntNumber = cntrList.get(0).get("PRODUCT_COMMITMENT_NUMBER").toString();
			date = dao.getSDEndDate(cntNumber);
		}
		if (date == null) {
			serviceStatus = Status.PRECONDITION_ERROR;
			errorCode = "BKS0012";
			errorText = "No end date could be calculated for Contact/Product Commitment" + cntNumber;
			return false;
		}
		CalendarDate cd = CalendarDate.from(date.toString(), "yyyy-MM-dd");
		returnList.put("P_PRODUCT_COMMITMENT;P_TERM_END_DATE;"+ape, cd);
		logger.info("Returned  end date : '" + cd.toString() + "'.");
		if(ape.equals("Sprache"))
			bVoiceFound = true;
		else if(ape.equals("Internet"))
			bInternetFound = true;
		return true;
	}

	private String populateMasterData() throws Exception {
		output = new PruefeBestandskundenRangerOutput();
		String returnXml = null;
		populateBaseOutput();
		String[] categoryList = null;
		if (bVoiceFound && bInternetFound)
			categoryList = new String[]{"ProfilInternet","ProfilSprache","TarifnameInternet","TarifnameSprache"};
		else if (bInternetFound)
			categoryList = new String[]{"ProfilInternet","TarifnameInternet"};
		else if (bVoiceFound)
			categoryList = new String[]{"ProfilSprache","TarifnameSprache"};
		if (returnList.size() > 0 && serviceStatus == Status.SUCCESS)
			((PruefeBestandskundenRangerOutput)output).setAuftragsmodell(theHelper.createApe(returnList,categoryList,true));
		else if (serviceStatus == Status.FUNCTIONAL_ERROR)
			((PruefeBestandskundenRangerOutput)output).setAuftragsmodell(theHelper.createErrorAm(errorCode,errorText,errorParams));
		try {
			returnXml = theHelper.serialazeForAaw(output, customerNumber,accessNumber,"de.arcor.aaw.kernAAW.bks.services" ,"PruefeBestandskundenRanger");
			if (serviceStatus == Status.SUCCESS && customerNumber != null)
				theHelper.loadCache(customerNumber+";PruefeBestandskundenRanger",returnXml);
			if (serviceStatus == Status.SUCCESS && accessNumber != null)
				theHelper.loadCache(accessNumber+";PruefeBestandskundenRanger",returnXml);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return returnXml;
	}
	public void setInput(InputData input) {
		super.setInput(input);
		setCustomerNumber(((PruefeBestandskundenRangerInput)input).getKundennummer());
		String countryCode = ((PruefeBestandskundenRangerInput)input).getRufnummer().getLaenderkennzeichen();
		String cityCode = ((PruefeBestandskundenRangerInput)input).getRufnummer().getVorwahl();
		String localNumber = ((PruefeBestandskundenRangerInput)input).getRufnummer().getRufnummer();
		accessNumber = countryCode.substring(2)+cityCode.substring(1)+localNumber;
	}
}
