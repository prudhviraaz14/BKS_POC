/*
    $Header:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/ErmittleVertriebspacketWertHandler.java-arc   1.32   Sep 20 2010 10:52:58   makuier  $

    $Log:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/ErmittleVertriebspacketWertHandler.java-arc  $
 * 
 *    Rev 1.32   Sep 20 2010 10:52:58   makuier
 * Support for IAD added.
 * 
 *    Rev 1.31   May 25 2010 14:28:20   makuier
 * nullify the tv center product code as default to avoid ambiguity.
 * 
 *    Rev 1.30   Jan 28 2010 16:28:36   makuier
 * Cache the exception and return an error XML.
 * 
 *    Rev 1.29   May 27 2009 16:06:22   makuier
 * MCF2 adaption
 * 
 *    Rev 1.28   Mar 16 2009 16:23:48   makuier
 * Handle unsupported hardware types.
 * 
 *    Rev 1.27   Mar 05 2009 14:28:06   makuier
 * Handle new hardware definition.
 * 
 *    Rev 1.26   Feb 19 2009 12:50:44   makuier
 * refactored
 * 
 *    Rev 1.25   Dec 17 2008 14:51:18   makuier
 * Reverted the last revision changes.
 * 
 *    Rev 1.23   Nov 14 2008 14:06:12   makuier
 * Get by service subscription id added.
 * 
 *    Rev 1.22   Aug 29 2008 12:38:44   makuier
 * Adapted to changes in AAW library for 128.
 * 
 *    Rev 1.21   Jul 02 2008 10:02:08   makuier
 * Handle canceled renegotation scenario.
 * 
 *    Rev 1.20   Jun 10 2008 14:33:00   makuier
 * if no hardware service found return.
 * 
 *    Rev 1.19   May 13 2008 11:59:46   makuier
 * pass access number for caching.
 * 
 *    Rev 1.18   Apr 22 2008 16:27:30   makuier
 * Added a voice service priority.
 * 
 *    Rev 1.17   Apr 18 2008 11:39:18   makuier
 * Try to find an exact match for access number. If no match found try the range.
 * 
 *    Rev 1.16   Apr 16 2008 17:29:36   makuier
 * IT-k-000022583
 * 
 *    Rev 1.15   Apr 07 2008 15:29:02   makuier
 * Use vioce online mapping to find the bundled service.
 * 
 *    Rev 1.14   Mar 12 2008 15:49:58   makuier
 * Hardware service added.
 * 
 *    Rev 1.13   Feb 27 2008 10:43:42   makuier
 * refactoring.
 * 
 *    Rev 1.12   Feb 15 2008 15:31:10   makuier
 * populate access number variable for logging.
 * 
 *    Rev 1.11   Feb 12 2008 11:04:34   makuier
 * If no ref data found with condition try to find the ref data disregarding from condition.
 * 
 *    Rev 1.10   Feb 05 2008 10:48:12   makuier
 * Check if all access number fields are provided.
 * 
 *    Rev 1.9   Jan 09 2008 13:06:32   makuier
 * If no package value found, the default value is returned.
 * 
 *    Rev 1.8   Dec 21 2007 11:25:38   makuier
 * Bug fix.
 * 
 *    Rev 1.7   Dec 19 2007 17:58:02   makuier
 * InternerName added to the category list.
 * 
 *    Rev 1.6   Dec 11 2007 19:10:12   makuier
 * Use prepared statement.
 * 
 *    Rev 1.5   Dec 06 2007 18:09:56   makuier
 * Changed instantiation of dao + bug fix
 * 
 *    Rev 1.4   Nov 28 2007 18:08:32   makuier
 * Only cache the successful results
 * 
 *    Rev 1.3   Nov 27 2007 14:05:46   makuier
 * Bug Fixes.
 * 
 *    Rev 1.2   Nov 12 2007 17:20:08   makuier
 * Use auftragsmodell as return type.
 * 
 *    Rev 1.1   Nov 12 2007 17:06:56   makuier
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
import net.arcor.bks.common.BksFunctionalException;
import net.arcor.bks.common.BksRefDataCacheHandler;
import net.arcor.bks.db.ErmittleVertriebspacketWertDAO;
import net.arcor.epsm_basetypes_001.InputData;
import de.arcor.aaw.auftragsmodell.Auftragsmodell;
import de.arcor.aaw.kernAAW.bks.services.ErmittleVertriebspacketWertInput;
import de.arcor.aaw.kernAAW.bks.services.ErmittleVertriebspacketWertOutput;

/**
 * @author MAKUIER
 *
 */
public class ErmittleVertriebspacketWertHandler extends BaseServiceHandler {

	HashMap<String, Object> returnList = new HashMap<String, Object>();
	HashMap<String,ArrayList<HashMap<String,Object>>> functionList=new HashMap<String,ArrayList<HashMap<String,Object>>>();
	String[] errorParams = null;
	String productSubscriptionId = null;
	boolean isServiceRelevant = true;
	public ErmittleVertriebspacketWertHandler() {
		super();
	}

	public String execute() throws Exception {
		String returnXml = null;
		ErmittleVertriebspacketWertDAO dao = 
			(ErmittleVertriebspacketWertDAO)DatabaseClient.getBksDataAccessObject(null,"ErmittleVertriebspacketWertDAO");

		try {
			returnList.put("P_PRODUCT_COMMITMENT;PRODUCT_CODE;TVCenter", null);
			String [] serviceData = retrieveMasterData(dao,(ErmittleVertriebspacketWertInput)input);
			if (serviceData != null && serviceData.length == 2){
				getProductInfo(dao, serviceData[0], serviceData[1]);
				ArrayList<HashMap<String, Object>> bundleItems = dao.getBundleItems(serviceData[0]);
				for (int i=0;i<bundleItems.size();i++){
					String servSubsId = (String)bundleItems.get(i).get("SERVICE_SUBSCRIPTION_ID");
					String serviceCode = (String)bundleItems.get(i).get("SERVICE_CODE");
					productSubscriptionId = bundleItems.get(i).get("PRODUCT_SUBSCRIPTION_ID").toString();
					getProductInfo(dao, servSubsId, serviceCode);
				}
			}	
			returnXml = populateMasterData();
			if (serviceStatus == Status.SUCCESS)
				theHelper.prefetch(customerNumber,accessNumber,"ErmittleVertriebspacketWert",input,output);
		} catch (BksDataException e) {
			logger.error(e.getMessage());
			serviceStatus = Status.PRECONDITION_ERROR;
			errorCode = "BKS0008";
			errorText = e.getMessage();
			returnXml = populateMasterData();
		}catch (BksFunctionalException e) {
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
	
	private void getProductInfo(ErmittleVertriebspacketWertDAO dao, String servSubsId,String serviceCode) throws Exception {
		boolean found  = false;
		isServiceRelevant = true;
		found = retrieveContractData(dao,servSubsId,serviceCode,true);
		if (!isServiceRelevant)
			return;

		if (!found && serviceStatus == Status.SUCCESS)
			found = retrieveContractData(dao,servSubsId,serviceCode,false);
		if (!found && serviceStatus == Status.SUCCESS){
			serviceStatus = Status.FUNCTIONAL_ERROR;
			errorText = "No signed contract found for Service Subscription " + servSubsId;
			errorCode = "BKS0018";
			errorParams = new String [] {servSubsId};
			throw new BksFunctionalException();
		} else if (found){
			retrieveBandwidthAndCondition(dao,servSubsId);
			retrieveHardwareArticle(dao,productSubscriptionId);
		}
		if (returnList.get("ServiceCodeBandwidth") == null) 
			getBandwitdthAndConditionForProd(dao,servSubsId);

		return;
	}

	private void getBandwitdthAndConditionForProd(ErmittleVertriebspacketWertDAO dao, String servSubsId) throws Exception {
		if (serviceStatus != Status.SUCCESS)
			return;
		ArrayList<HashMap<String, Object>> theList = dao.getMainAccessService(servSubsId);
		for (int i=0;theList!=null&&i<theList.size();i++){
			retrieveBandwidthAndCondition(dao,theList.get(i).get("SERVICE_SUBSCRIPTION_ID").toString());
			if (returnList.get("ServiceCodeBandwidth") != null)
				break;
		}
	}

	private void retrieveBandwidthAndCondition(ErmittleVertriebspacketWertDAO dao, String servSubId) throws Exception {
		if (serviceStatus != Status.SUCCESS)
			return;
		ArrayList<HashMap<String, Object>> bwList = dao.getBandwidthService(servSubId);
		if (bwList.size() == 1){
			String serviceCode = bwList.get(0).get("SERVICE_CODE").toString();
			returnList.put("ServiceCodeBandwidth",serviceCode);
		}
		ArrayList<HashMap<String, Object>> theCondList = dao.getCondition(servSubId);
		if (theCondList.size() == 1){
			String configuredValue = theCondList.get(0).get("CONFIGURED_VALUE_STRING").toString();
			returnList.put("ActionOffer",configuredValue);
		}
		return;
	}

	private String [] retrieveMasterData(ErmittleVertriebspacketWertDAO dao, ErmittleVertriebspacketWertInput input) throws Exception {
		if (serviceStatus != Status.SUCCESS)
			return null;
		if (input.getRufnummer()!=null) {
			String countryCode = input.getRufnummer().getLaenderkennzeichen();
			String cityCode = input.getRufnummer().getVorwahl();
			String localNumber = input.getRufnummer().getRufnummer();
			if (countryCode == null || cityCode == null || localNumber == null  ){
				serviceStatus = Status.PRECONDITION_ERROR;
				errorCode = "BKS0013";
				errorText = "All access number fields (country code, city code and local number) have to be provided.";
				return null;
			}
			boolean cntyCodeMatch = theHelper.matchInput(countryCode);
			boolean cityCodeMatch = theHelper.matchInput(cityCode);
			boolean numMatch = theHelper.matchInput(localNumber);		
			if (!cntyCodeMatch || !cityCodeMatch || !numMatch){
				serviceStatus = Status.FUNCTIONAL_ERROR;
				errorText = "The input is not correctly formatted.";
				errorCode = "BKS0006";
				if (!cntyCodeMatch)
					errorParams = new String [] {countryCode};
				if (!cityCodeMatch)
					errorParams = new String [] {cityCode};
				if (!numMatch)
					errorParams = new String [] {localNumber};
				return null;
			}
			ArrayList<HashMap<String, Object>> theList = dao.getExactAccessNumber(countryCode,cityCode,localNumber);
			if ((theList == null) || (theList.size() == 0))
				theList = dao.getAccessNumber(countryCode,cityCode,localNumber);
			if ((theList != null) && (theList.size() != 0)){
				customerNumber = theList.get(0).get("CUSTOMER_NUMBER").toString();
				String serviceSubscrId = theList.get(0).get("SERVICE_SUBSCRIPTION_ID").toString();
				String serviceCode = theList.get(0).get("SERVICE_CODE").toString();
				productSubscriptionId = theList.get(0).get("PRODUCT_SUBSCRIPTION_ID").toString();
				returnList.put("CUSTOMER;CUSTOMER_NUMBER;NatuerlichePerson", customerNumber);
				return new String[] {serviceSubscrId,serviceCode};
			}
		} else if (input.getServiceSubscriptionId() != null){
			String serviceSubscrId = input.getServiceSubscriptionId();
			ArrayList<HashMap<String, Object>> theList = dao.getServiceSubscription(serviceSubscrId);
			if ((theList != null) && (theList.size() != 0)){
				customerNumber = theList.get(0).get("CUSTOMER_NUMBER").toString();
				String serviceCode = theList.get(0).get("SERVICE_CODE").toString();
				productSubscriptionId = theList.get(0).get("PRODUCT_SUBSCRIPTION_ID").toString();
				returnList.put("CUSTOMER;CUSTOMER_NUMBER;NatuerlichePerson", customerNumber);
				return new String[] {serviceSubscrId,serviceCode};
			}
		}
        
		serviceStatus = Status.FUNCTIONAL_ERROR;
		errorText = "No customer found";
		errorCode = "BKS0000";
		return null;
	}

	private boolean retrieveContractData(ErmittleVertriebspacketWertDAO dao, String servSubId, String servCode,boolean isOrderForm) throws Exception{
		if (serviceStatus != Status.SUCCESS)
			return false;

		HashMap<String,String> functions =BksRefDataCacheHandler.getFunction();
		String ape = functions.get(servCode);
		if (ape == null){
			isServiceRelevant = false;
			return false;
		}
		if (ape.equals("Sprache"))
			returnList.put("ServiceCodeVoice",servCode);

		ArrayList<HashMap<String, Object>> cntrList = null;
		if (isOrderForm){
			cntrList = dao.getOFContractData(servSubId);
			if (cntrList == null || cntrList.size() == 0)
				cntrList = dao.getSignedOFContractData(servSubId);
		}else{
			cntrList = dao.getSDContractData(servSubId);
			if (cntrList == null || cntrList.size() == 0)
				cntrList = dao.getSignedSDContractData(servSubId);
		}
		if ((cntrList == null) || (cntrList.size() == 0)){
			return false;
		} else if (cntrList.size() != 1){
			serviceStatus = Status.FUNCTIONAL_ERROR;
			errorText = "Call the Hotline";
			errorCode = "BKS0001";
			return false;
		}
		returnList.put("P_PRODUCT_COMMITMENT;PRODUCT_CODE;"+ape, cntrList.get(0).get("PRODUCT_CODE").toString());
		returnList.put("P_PRODUCT_COMMITMENT;PRICING_STRUCTURE_CODE;"+ape, cntrList.get(0).get("PRICING_STRUCTURE_CODE").toString());
		return true;
	}

	private String populateMasterData() throws Exception {
		output = new ErmittleVertriebspacketWertOutput();
		String returnXml = null;
		populateBaseOutput();
		String[] categoryList = {"PaketPreis","PaketID","PaketName","PaketInternerName"};
		if (returnList.size() > 0 && serviceStatus == Status.SUCCESS){
			Auftragsmodell am = createAuftragsmodell(categoryList);
			if (functionList.size() > 0)
				theHelper.handleFunctions("Hardware",am,functionList);
			((ErmittleVertriebspacketWertOutput)output).setAuftragsmodell(am);
		} else if (serviceStatus == Status.FUNCTIONAL_ERROR)
			((ErmittleVertriebspacketWertOutput)output).setAuftragsmodell(theHelper.createErrorAm(errorCode,errorText,errorParams));
			
		try {
			returnXml = theHelper.serialazeForAaw(output, customerNumber,accessNumber,"de.arcor.aaw.kernAAW.bks.services" ,"ErmittleVertriebspacketWert");
			if (serviceStatus == Status.SUCCESS && customerNumber != null)
				theHelper.loadCache(customerNumber+";ErmittleVertriebspacketWert",returnXml);
			if (serviceStatus == Status.SUCCESS && accessNumber != null)
				theHelper.loadCache(accessNumber+";ErmittleVertriebspacketWert",returnXml);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return returnXml;
	}

	private Auftragsmodell createAuftragsmodell(String[] categoryList) throws Exception {
		Auftragsmodell am = null;
		try {
			am = theHelper.createApe(returnList,categoryList,true);
		} catch (BksDataException e) {
			logger.warn("No Specific Package value found for :"+e.getMessage());
			if(returnList.get("ActionOffer") != null){
				returnList.remove("ActionOffer");
			    am = createAuftragsmodell(categoryList);
			    return am;
			}
			else if(returnList.get("ServiceCodeVoice") != null){
				returnList.remove("ServiceCodeVoice");
			    am = createAuftragsmodell(categoryList);
			    return am;
			}
			HashMap<String, Object> defaultList = new HashMap<String, Object>();
			defaultList.put("CUSTOMER;CUSTOMER_NUMBER;NatuerlichePerson", customerNumber);
			defaultList.put("ServiceCodeVoice","DEFAULT");
			defaultList.put("P_PRODUCT_COMMITMENT;PRODUCT_CODE;Sprache","DEFAULT");
			defaultList.put("P_PRODUCT_COMMITMENT;PRICING_STRUCTURE_CODE;Sprache", "DEFAULT");
            defaultList.put("ServiceCodeBandwidth","DEFAULT");
			logger.info("No Specific Package value found. Default package will be returned.");
			am = theHelper.createApe(defaultList,categoryList,true);
		}
		return am;
	}

	private void retrieveHardwareArticle(ErmittleVertriebspacketWertDAO dao, String prodSubsId) throws Exception {
		ArrayList<HashMap<String, Object>> theHwList = dao.getHardwareCharacteristic(prodSubsId);
		HashMap<String,Object>  hardwareArticle = null;
		String path = null;
		int maxSize=0;
		for (int i =0;theHwList!=null&&i<theHwList.size(); i++){
			String configuredValue = theHwList.get(i).get("CONFIGURED_VALUE_STRING").toString();
			String charCode = theHwList.get(i).get("SERVICE_CHARACTERISTIC_CODE").toString();
			if (charCode.equals("V0112")) {
				HashMap<String,String>  articleFilter = new HashMap<String, String>();
				articleFilter.put("Artikelnummer",configuredValue);
				String hardwareType = theHelper.getRefdataValue(articleFilter, "Hardwareartikel", "Typ");
				if (hardwareType == null){
					path = null;
					continue;
				}
				String hardwareDesc = theHelper.getRefdataValue(articleFilter, "Hardwareartikel", "Bezeichnung");
				HashMap<String,String>  pathFilter = new HashMap<String, String>();
				pathFilter.put("HardwareTyp", hardwareType);
				//path = theHelper.getRefdataValue(pathFilter, "Mapping_Hardwareartikel_Bestand", "AAWKnoten");
				//maxSize = theHelper.getRefdataValue(pathFilter, "Mapping_Hardwareartikel_Bestand", "Kardinalitaet");
				hardwareArticle = new HashMap<String, Object>();
				hardwareArticle.put("artikelnummer",configuredValue);
				hardwareArticle.put("typ",hardwareType);
				hardwareArticle.put("bezeichnung",hardwareDesc);
				if (hardwareType.equals("M") || hardwareType.equals("IAD")){
					path = "Modem";
					maxSize = 4;
				}else if (hardwareType.equals("EG")){
					path = "Endgeraet";
					maxSize = 8;
				}else if (hardwareType.equals("STB")){
					path = "Settopbox";
					maxSize = 2;
				}else if (hardwareType.equals("MM")){
					path = "Multimediageraet";
					maxSize = 4;
				}else if (hardwareType.equals("SIM")){
					path = "Sim";
					maxSize = 8;
				} else {
					path = null;
				}
			} else if (charCode.equals("V0114") && hardwareArticle != null) {
				hardwareArticle.put("subventioniertErhalten",new Boolean (configuredValue.equals("1")));
				if (functionList.containsKey(path)) {
					ArrayList<HashMap<String, Object>> list = functionList.get(path);
					if (list.size() < maxSize)
						list.add(hardwareArticle);
				}else {
					ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String,Object>>();
					list.add(hardwareArticle);
					if (path != null)
						functionList.put(path,list);
				}
			}
		}
	}

	public void setInput(InputData input) {
		super.setInput(input);
		if (((ErmittleVertriebspacketWertInput)input).getRufnummer() != null){
			String countryCode = ((ErmittleVertriebspacketWertInput)input).getRufnummer().getLaenderkennzeichen();
			String cityCode = ((ErmittleVertriebspacketWertInput)input).getRufnummer().getVorwahl();
			String localNumber = ((ErmittleVertriebspacketWertInput)input).getRufnummer().getRufnummer();
			accessNumber = countryCode.substring(2)+cityCode.substring(1)+localNumber;
		}
	}
}
