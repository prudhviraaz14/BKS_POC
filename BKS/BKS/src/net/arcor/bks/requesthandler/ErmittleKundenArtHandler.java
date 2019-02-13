/*
    $Header:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/ErmittleKundenArtHandler.java-arc   1.14   Jan 28 2010 16:28:20   makuier  $

    $Log:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/ErmittleKundenArtHandler.java-arc  $
 * 
 *    Rev 1.14   Jan 28 2010 16:28:20   makuier
 * Cache the exception and return an error XML.
 * 
 *    Rev 1.13   May 27 2009 16:06:22   makuier
 * MCF2 adaption
 * 
 *    Rev 1.12   Sep 29 2008 15:04:02   makuier
 * map the customer category.
 * 
 *    Rev 1.11   May 30 2008 11:43:44   makuier
 * Use cross reference to determine
 * 
 *    Rev 1.10   May 13 2008 12:01:52   makuier
 * pass access number for caching.
 * 
 *    Rev 1.9   Apr 15 2008 10:58:00   makuier
 * Populate output xml in case of error.
 * 
 *    Rev 1.8   Feb 27 2008 11:18:14   makuier
 * refactoring.
 * 
 *    Rev 1.7   Jan 22 2008 16:46:44   makuier
 * Raise a precondition error if the customer root data is corrupted.
 * 
 *    Rev 1.6   Dec 14 2007 11:55:16   makuier
 * Populate Vodafone service provider code.
 * 
 *    Rev 1.5   Dec 11 2007 19:09:08   makuier
 * error parameter added.
 * 
 *    Rev 1.4   Dec 06 2007 18:07:06   makuier
 * Changed the instantiation of dao.
 * 
 *    Rev 1.3   Nov 28 2007 18:08:06   makuier
 * Only cache the successful results
 * 
 *    Rev 1.2   Nov 27 2007 14:05:48   makuier
 * Bug Fixes.
 * 
 *    Rev 1.1   Nov 12 2007 17:06:52   makuier
 * Changes for 1.25
 * 
 *    Rev 1.0   Nov 06 2007 17:40:40   makuier
 * Initial revision.
*/
package net.arcor.bks.requesthandler;

import java.util.ArrayList;
import java.util.HashMap;

import net.arcor.bks.client.DatabaseClient;
import net.arcor.bks.common.BksDataException;
import net.arcor.bks.common.BksRefDataCacheHandler;
import net.arcor.bks.db.ErmittleKundenArtDAO;
import net.arcor.epsm_basetypes_001.InputData;
import de.arcor.aaw.kernAAW.bks.services.ErmittleKundenArtInput;
import de.arcor.aaw.kernAAW.bks.services.ErmittleKundenArtOutput;

/**
 * @author MAKUIER
 *
 */
public class ErmittleKundenArtHandler extends BaseServiceHandler {

	HashMap<String, Object> returnList = new HashMap<String, Object>();
	boolean isIndividual = true;
	String[] errorParams = null;

	public String execute() throws Exception {
		String returnXml = null;
		ErmittleKundenArtDAO dao = 
			(ErmittleKundenArtDAO)DatabaseClient.getBksDataAccessObject(null,"ErmittleKundenArtDAO");

		try {
			ArrayList<HashMap<String, Object>> custList = dao.getCustomer(customerNumber);
			if ((custList == null) || (custList.size() != 1)){
				serviceStatus = Status.FUNCTIONAL_ERROR;
				errorText = "No customer found";
				errorCode = "BKS0000";
				returnXml = populateMasterData();
				return returnXml;
			}
			String custType = null;
			isIndividual = custList.get(0).get("ENTITY_TYPE").toString().equals("I");
			if (isIndividual)
				custType = "NatuerlichePerson";
			else
				custType = "JuristischePerson";
			String custCategory = custList.get(0).get("CATEGORY_RD").toString();
			if (custCategory.equals("BUSINESS"))
				custCategory = "Geschäftskunde";
			else if (custCategory.equals("RESIDENTIAL"))
				custCategory = "Privatkunde";
			else if (custCategory.equals("SERVICE_PROVIDER"))
				custCategory = "Service Provider";
			String custClass = custList.get(0).get("CLASSIFICATION_RD").toString();
			String providerCode = getProviderCode(dao,customerNumber,custClass);
			returnList.put("CUSTOMER;CUSTOMER_NUMBER;"+custType, customerNumber);
			returnList.put("CUSTOMER;P_SERVICE_PROVIDER_CODE;"+custType, providerCode);
			returnList.put("CUSTOMER;CATEGORY_RD;"+custType, custCategory);
			returnList.put("CUSTOMER;CLASSIFICATION_RD;"+custType, custClass);
			returnXml = populateMasterData();
			if (serviceStatus == Status.SUCCESS)
				theHelper.prefetch(customerNumber,accessNumber,"ErmittleKundenArt",input,output);
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

	private String getProviderCode(ErmittleKundenArtDAO dao, String customerNumber, String custClass) throws Exception {
		String providerCode = BksRefDataCacheHandler.getClassProviderMap().get(custClass);
		if (providerCode == null){
			ArrayList<HashMap<String, Object>> rootList = dao.getRootCustomer(customerNumber);
			if(rootList != null && rootList.size() > 0){
				String rootNumber = rootList.get(0).get("CUSTOMER_NUMBER").toString();
				HashMap<String,String> providerCodes =BksRefDataCacheHandler.getProviderCode();
				if (providerCodes.get(rootNumber) != null)
					providerCode = providerCodes.get(rootNumber);
				else
					providerCode = "ARCO";
			}
			else
			{
				serviceStatus = Status.PRECONDITION_ERROR;
				errorCode = "BKS0010";
				errorText = "The data for the customer "+customerNumber+" is corrupted.";
				return null;
			}
		}
		return providerCode;
	}

	private String populateMasterData() throws Exception {
		output = new ErmittleKundenArtOutput();
		String returnXml = null;
		populateBaseOutput();
		if (returnList.size() > 0 && serviceStatus == Status.SUCCESS)
			((ErmittleKundenArtOutput)output).setAuftragsmodell(theHelper.createApe(returnList,null,isIndividual));
		else if (serviceStatus == Status.FUNCTIONAL_ERROR)
			((ErmittleKundenArtOutput)output).setAuftragsmodell(theHelper.createErrorAm(errorCode,errorText,errorParams));
		try {
			returnXml = theHelper.serialazeForAaw(output, customerNumber,accessNumber,"de.arcor.aaw.kernAAW.bks.services" ,"ErmittleKundenArt");
			if (serviceStatus == Status.SUCCESS)
				theHelper.loadCache(customerNumber+";ErmittleKundenArt",returnXml);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return returnXml;
	}
	public void setInput(InputData input) {
		super.setInput(input);
		setCustomerNumber(((ErmittleKundenArtInput)input).getKundennummer());
	}
}
