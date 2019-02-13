/*
    $Header:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/PruefeBestandskundeHandler.java-arc   1.17   Jan 28 2010 16:51:40   makuier  $

    $Log:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/PruefeBestandskundeHandler.java-arc  $
 * 
 *    Rev 1.17   Jan 28 2010 16:51:40   makuier
 * Cache the exception and return an error XML.
 * 
 *    Rev 1.16   May 27 2009 16:05:54   makuier
 * MCF2 adaption
 * 
 *    Rev 1.15   Jul 23 2008 14:53:52   schwarje
 * SPN-BKS-000073952: disabled eh-cache
 * 
 *    Rev 1.14   Jul 10 2008 15:14:22   makuier
 * handle the query time out exception.
 * 
 *    Rev 1.13   May 13 2008 12:02:24   makuier
 * pass access number for caching.
 * 
 *    Rev 1.12   Feb 28 2008 10:14:42   makuier
 * bug fix
 * 
 *    Rev 1.11   Feb 27 2008 10:40:40   makuier
 * refactoring
 * 
 *    Rev 1.10   Feb 15 2008 15:31:08   makuier
 * populate access number variable for logging.
 * 
 *    Rev 1.9   Feb 05 2008 14:06:46   makuier
 * Check if all access number field are provided.
 * 
 *    Rev 1.8   Jan 31 2008 14:18:28   makuier
 * Handle the empty number suffix.
 * 
 *    Rev 1.7   Jan 28 2008 12:23:50   huptasch
 * SPN-BKS-00006638
 * set customer number if 1 customer found
 * 
 *    Rev 1.6   Dec 11 2007 19:31:28   makuier
 * Use prepared statement.
 * 
 *    Rev 1.5   Dec 06 2007 18:07:04   makuier
 * Changed the instantiation of dao.
 * 
 *    Rev 1.4   Nov 28 2007 18:09:04   makuier
 * Only cache the successful results
 * 
 *    Rev 1.3   Nov 27 2007 14:05:48   makuier
 * Bug Fixes.
 * 
 *    Rev 1.2   Nov 12 2007 17:06:58   makuier
 * Changes for 1.25
 * 
 *    Rev 1.1   Nov 06 2007 17:54:16   makuier
 * Changes for 1.25
*/
package net.arcor.bks.requesthandler;

import java.util.ArrayList;
import java.util.HashMap;

import net.arcor.bks.client.DatabaseClient;
import net.arcor.bks.db.PruefeBestandskundeDAO;
import net.arcor.epsm_basetypes_001.InputData;

import org.springframework.jdbc.UncategorizedSQLException;

import de.arcor.aaw.kernAAW.bks.services.KundennummernListe;
import de.arcor.aaw.kernAAW.bks.services.PruefeBestandskundeInput;
import de.arcor.aaw.kernAAW.bks.services.PruefeBestandskundeOutput;

/**
 * @author MAKUIER
 *
 */
public class PruefeBestandskundeHandler  extends BaseServiceHandler{


	/** 
	 * This method executes the data retrieval queries and returns the result in output object.
	 * The result is also cached in JCS cache to speed up the retrieval in case anything goes
	 * wrong.
	 */
	public String execute() throws Exception {
		String returnXml = null;
		errorText = null;
		errorCode = null;
		PruefeBestandskundeDAO dao = 
			(PruefeBestandskundeDAO) DatabaseClient.getBksDataAccessObject(null,"PruefeBestandskundeDAO");

		String countryCode = ((PruefeBestandskundeInput)input).getRufnummer().getLaenderkennzeichen();
		String cityCode = ((PruefeBestandskundeInput)input).getRufnummer().getVorwahl();
		String localNumber = ((PruefeBestandskundeInput)input).getRufnummer().getRufnummer();
		returnXml = checkPrecondition((PruefeBestandskundeInput)input);
		if (returnXml != null)
			return returnXml;
		try {
			ArrayList<HashMap<String, Object>> accNumList = dao.getCustomerByAccessNumber(countryCode,cityCode,localNumber);
			if ((accNumList == null) || (accNumList.size() == 0)){
				returnXml = findCustomerByAddrName(dao,(PruefeBestandskundeInput)input);
			} else if (accNumList.size() > 10){
				serviceStatus = Status.FUNCTIONAL_ERROR;
				errorText = "Es wurden zu viele Ergebnisse gefunden. Bitte wählen Sie ein genaueres Suchkriterium.";
				errorCode = "BKS0007";
				returnXml = populateOutputObject(null);
			} else {
				String theCustNumList = "";
				for (int i = 0; i < accNumList.size(); i++) {
					Object theValue = accNumList.get(i).get("CUSTOMER_NUMBER");
					theCustNumList += ","+theValue.toString();
				}
				returnXml = findCustomerByAddrOrName(dao,(PruefeBestandskundeInput)input,theCustNumList);
				//if (serviceStatus == Status.SUCCESS)
				//theHelper.prefetch(customerNumber,accessNumber,"PruefeBestandskunde",input,output);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			serviceStatus = Status.PRECONDITION_ERROR;
			errorCode = "BKS0008";
			errorText = e.getMessage();
			returnXml = populateOutputObject(null);
		}
		finally {
			dao.closeConnection();
		}
		return returnXml;
	}

	private String findCustomerByAddrOrName(PruefeBestandskundeDAO dao,
			PruefeBestandskundeInput input, String custNumList) throws Exception {
		String returnXml = null;
		ArrayList<HashMap<String, Object>> addrList = 
			dao.getCustomerByAddress(input.getAdresse().getPLZ(),
				input.getAdresse().getStrasse().toUpperCase(),input.getAdresse().getHausnummer(),
				(input.getAdresse().getHausnummerzusatz() == null || 
				input.getAdresse().getHausnummerzusatz().equals(""))?null:input.getAdresse().getHausnummerzusatz(),
				custNumList);
		if (addrList.size() == 1){
			if (addrList.get(0).get("ENTITY_TYPE").toString().equals("O")){
				serviceStatus = Status.SUCCESS;
			}else {
				serviceStatus = Status.FUNCTIONAL_ERROR;
				errorText = "Kein Geschäftskunde.";
				errorCode = "BKS0003";
			}
			returnXml = populateOutputObject(addrList);
		} else {
			ArrayList<HashMap<String, Object>> nameList = 
				dao.getCustomerByName(input.getRechtsform(),input.getFirmenname()+"%",custNumList);
			if (nameList.size() == 1){
				if (nameList.get(0).get("ENTITY_TYPE").toString().equals("O")){
					serviceStatus = Status.SUCCESS;
				}else {
					serviceStatus = Status.FUNCTIONAL_ERROR;
					errorText = "Kein Geschäftskunde.";
					errorCode = "BKS0003";
				}
			} else {
				serviceStatus = Status.FUNCTIONAL_ERROR;
				errorText = "Bitte wenden Sie sich an die Hotline.";
				errorCode = "BKS0001";
			}
			returnXml = populateOutputObject(nameList);
		}
		return returnXml;
	}

	private String findCustomerByAddrName(PruefeBestandskundeDAO dao, PruefeBestandskundeInput input) throws Exception {
		ArrayList<HashMap<String, Object>> addrList = null;
		try {
			addrList = dao.getCustomerByAddressName(input.getAdresse().getPLZ(),
				input.getAdresse().getStrasse().toUpperCase(),input.getAdresse().getHausnummer(),
				(input.getAdresse().getHausnummerzusatz() == null || 
				input.getAdresse().getHausnummerzusatz().equals(""))?null:input.getAdresse().getHausnummerzusatz(),
				input.getRechtsform(),input.getFirmenname()+"%");
		} catch (UncategorizedSQLException e) {
			if (e.getSQLException().getErrorCode() == 1013){
				serviceStatus = Status.PRECONDITION_ERROR;
				errorCode = "BKS0014";
				errorText = "Query Timed out.";
				return populateOutputObject(addrList);
			}
			throw e;
		}
		if (addrList == null || addrList.size() == 0){
			serviceStatus = Status.FUNCTIONAL_ERROR;
			errorText = "Kein Bestandskunde.";
			errorCode = "BKS0000";
		} else if (addrList.size() == 1){
			if (addrList.get(0).get("ENTITY_TYPE").toString().equals("O")){
				serviceStatus = Status.SUCCESS;
			}else {
				serviceStatus = Status.FUNCTIONAL_ERROR;
				errorText = "Kein Geschäftskunde.";
				errorCode = "BKS0003";
			}
		} else {
			serviceStatus = Status.FUNCTIONAL_ERROR;
			errorText = "Bitte wenden Sie sich an die Hotline.";
			errorCode = "BKS0001";
		}
		return populateOutputObject(addrList);
	}

	private String checkPrecondition(PruefeBestandskundeInput input) throws Exception {
		String returnXml = null;
		if (input.getAdresse().getPLZ() == null || input.getAdresse().getPLZ().length() == 0){
				serviceStatus = Status.PRECONDITION_ERROR;
				errorText = "Die Postleitzahl muss angegeben werden.";
				errorCode = "BKS0002";
				returnXml = populateOutputObject(null);
			} 
		if (input.getRufnummer().getLaenderkennzeichen()== null || 
			input.getRufnummer().getVorwahl() == null || 
			input.getRufnummer().getRufnummer() == null  ){
			serviceStatus = Status.PRECONDITION_ERROR;
			errorCode = "BKS0013";
			errorText = "All access number fields (country code, city code and local number) have to be provided.";
			returnXml = populateOutputObject(null);
		}
		return returnXml;
	}

	protected String populateOutputObject(ArrayList<HashMap<String, Object>> theResultList) throws Exception {
		output = new PruefeBestandskundeOutput();
		String returnXml = null;
		if (serviceStatus != Status.SUCCESS){
			output.setResult(false);
			output.setErrorCode(errorCode);
			output.setErrorText(errorText);
		}else
			output.setResult(true);

		KundennummernListe custNumList = new KundennummernListe();
		ArrayList<String> theList = (ArrayList<String>)custNumList.getKundennummer();
		for (int i = 0; theResultList != null && i < theResultList.size(); i++) {
			theList.add(theResultList.get(i).get("CUSTOMER_NUMBER").toString());
		}

		if (theResultList != null && theResultList.size() == 1) {
			setCustomerNumber(theResultList.get(0).get("CUSTOMER_NUMBER").toString());
		}
		((PruefeBestandskundeOutput)output).setKundennummernListe(custNumList);
		try {
			returnXml = theHelper.serializeForMcf(output, customerNumber,accessNumber,"de.arcor.aaw.kernAAW.bks.services" ,"PruefeBestandskunde");
			// disabled cache
			//if (serviceStatus == Status.SUCCESS && customerNumber!=null)
			//	theHelper.loadCache(customerNumber+";PruefeBestandskunde",returnXml);
			//if (serviceStatus == Status.SUCCESS && accessNumber!=null)
			//	theHelper.loadCache(accessNumber+";PruefeBestandskunde",returnXml);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return returnXml;
	}

	public void setInput(InputData input) {
		super.setInput(input);
		String countryCode = ((PruefeBestandskundeInput)input).getRufnummer().getLaenderkennzeichen();
		String cityCode = ((PruefeBestandskundeInput)input).getRufnummer().getVorwahl();
		String localNumber = ((PruefeBestandskundeInput)input).getRufnummer().getRufnummer();
		accessNumber = countryCode.substring(2)+cityCode.substring(1)+localNumber;
	}
}