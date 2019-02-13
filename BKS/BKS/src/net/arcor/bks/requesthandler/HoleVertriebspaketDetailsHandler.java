/*
    $Header:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/HoleVertriebspaketDetailsHandler.java-arc   1.35   Apr 24 2012 17:45:14   makuier  $

    $Log:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/HoleVertriebspaketDetailsHandler.java-arc  $
 * 
 *    Rev 1.35   Apr 24 2012 17:45:14   makuier
 * Adapted to new cache lay out.
 * 
 *    Rev 1.34   Jun 06 2011 17:35:56   makuier
 * Handle oracle date casting.
 * 
 *    Rev 1.33   May 25 2011 15:08:52   makuier
 * Ibatis maps date to timestamp.
 * 
 *    Rev 1.32   Mar 07 2011 11:48:32   makuier
 * Handle tariff name 
 * 
 *    Rev 1.31   Feb 18 2011 13:01:30   makuier
 * - Handle profile for non supported functions
 * 
 *    Rev 1.30   Jan 03 2011 14:14:50   makuier
 * Adapted to changes in ref data cache.
 * 
 *    Rev 1.29   Dec 28 2010 16:32:52   makuier
 * Added support for more elements in ASK list.
 * 
 *    Rev 1.28   Nov 05 2010 15:55:36   makuier
 * trim the configured values.
 * 
 *    Rev 1.27   Sep 27 2010 16:25:14   makuier
 * Support for post ident added.
 * 
 *    Rev 1.26   Sep 14 2010 14:56:18   makuier
 * Support for IAD added.
 * 
 *    Rev 1.25   Aug 05 2010 11:18:52   makuier
 * Added support for prefixed access numbers.
 * 
 *    Rev 1.24   May 25 2010 14:29:28   makuier
 * nullify the tv center product code as default to avoid ambiguity.
 * 
 *    Rev 1.23   May 05 2010 11:28:12   makuier
 * Added support for multiple paths for same access number.
 * 
 *    Rev 1.22   Jan 28 2010 16:48:40   makuier
 * Cache the exception and return an error XML.
 * 
 *    Rev 1.21   Nov 23 2009 17:13:20   makuier
 * Adapted to ref data structure change.
 * 
 *    Rev 1.20   Nov 09 2009 11:20:50   makuier
 * Adapted to new cache data types.
 * 
 *    Rev 1.19   Sep 22 2009 19:02:24   makuier
 * Changes for IT-25142
 * 
 *    Rev 1.18   Aug 28 2009 14:48:02   makuier
 * Populate address only once.
 * 
 *    Rev 1.17   Aug 18 2009 12:54:36   makuier
 * Ignore the null configured values.
 * 
 *    Rev 1.16   Jul 09 2009 13:30:38   makuier
 * Ignore white spaces and empty strings.
 * 
 *    Rev 1.15   May 27 2009 16:05:56   makuier
 * MCF2 adaption
 * 
 *    Rev 1.14   May 19 2009 10:08:28   makuier
 * support for street number range added.
 * 
 *    Rev 1.13   Apr 06 2009 11:39:34   makuier
 * Support for Tariff options added.
 * 
 *    Rev 1.12   Mar 16 2009 16:23:48   makuier
 * Handle unsupported hardware types.
 * 
 *    Rev 1.11   Mar 05 2009 14:28:06   makuier
 * Handle new hardware definition.
 * 
 *    Rev 1.10   Feb 25 2009 09:48:24   makuier
 * pointer check added.
 * 
 *    Rev 1.9   Feb 05 2009 11:51:28   makuier
 * Pointer check added.
 * 
 *    Rev 1.8   Jan 23 2009 14:36:02   makuier
 * Support customers which do not have any bundle.
 * 
 *    Rev 1.7   Jan 21 2009 12:56:50   makuier
 * Check the non bundled main services as wel for bandwidth (DSL-R)
 * 
 *    Rev 1.6   Jan 12 2009 12:49:40   makuier
 * Support bandwidth for DSL-R
 * 
 *    Rev 1.5   Jan 06 2009 13:52:56   makuier
 * Enhanced the hardware function.
 * 
 *    Rev 1.4   Dec 17 2008 14:49:52   makuier
 * - Hardware added.
 * - Profile added.
 * 
 *    Rev 1.3   Dec 12 2008 11:47:54   makuier
 * Handle irrelevant services correctly.
 * 
 *    Rev 1.2   Dec 09 2008 13:18:36   makuier
 *  Raise an exception if no data found for customer number.
 * 
 *    Rev 1.1   Dec 01 2008 09:10:16   makuier
 * retrieval by customer number added.
 * 
 *    Rev 1.0   Nov 25 2008 14:21:32   makuier
 * Initial revision.
 * 
*/
package net.arcor.bks.requesthandler;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.arcor.bks.client.DatabaseClient;
import net.arcor.bks.common.BksDataException;
import net.arcor.bks.common.BksFunctionalException;
import net.arcor.bks.common.BksRefDataCacheHandler;
import net.arcor.bks.db.HoleVertriebspaketDetailsDAO;
import net.arcor.epsm_basetypes_001.InputData;

import com.domainlanguage.time.CalendarDate;

import de.arcor.aaw.auftragsmodell.Auftragsmodell;
import de.arcor.aaw.auftragsmodell.ask.Auftragsposition;
import de.arcor.aaw.auftragsmodell.ask.Bestand;
import de.arcor.aaw.kernAAW.bks.services.HoleVertriebspaketDetailsInput;
import de.arcor.aaw.kernAAW.bks.services.HoleVertriebspaketDetailsOutput;

/**
 * @author MAKUIER
 *
 */
public class HoleVertriebspaketDetailsHandler extends BaseServiceHandler {

	HashMap<String, Object> returnList = new HashMap<String, Object>();
	String productSubscriptionId = null;
	HashMap<String,Object> functionList= new HashMap<String,Object>();
	HashSet<String> dynamicPaths = new HashSet<String>();
	String[] errorParams = null;
	boolean isServiceRelevant = true;
	boolean isAddressPopulated = false;
	
	public HoleVertriebspaketDetailsHandler() {
		super();
	}

	public String execute() throws Exception {
		String returnXml = null;
		HoleVertriebspaketDetailsDAO dao = 
			(HoleVertriebspaketDetailsDAO)DatabaseClient.getBksDataAccessObject(null,"HoleVertriebspaketDetailsDAO");

		try {
			String[] serviceData = retrieveMasterData(dao,(HoleVertriebspaketDetailsInput)input);
			Auftragsmodell am = theHelper.createApe(returnList, null, true);
			Bestand bestand = (Bestand) am.getInhalt().get(0);
			HashMap<String,Object> pathValueMap=new HashMap<String,Object>();
			HashMap<String,Object> refList=new HashMap<String,Object>();
			refList.put("P_PRODUCT_COMMITMENT;PRODUCT_CODE;TVCenter", null);
			String productCode = getProductInfo(dao, serviceData[0], serviceData[1], refList,pathValueMap);
			if (isServiceRelevant)
				populateFunctionData(dao,refList,pathValueMap,productCode,serviceData[0],serviceData[1]);
			ArrayList<HashMap<String, Object>> bundleItems = dao.getBundleItems(serviceData[0]);
			Auftragsposition ape = new de.arcor.aaw.auftragsmodell.ask.ObjectFactory().createAuftragsposition();
			bestand.getAuftragsposition().add(ape);
			for (int i=0;i<bundleItems.size();i++){
				String servSubsId = (String)bundleItems.get(i).get("SERVICE_SUBSCRIPTION_ID");
				String serviceCode = (String)bundleItems.get(i).get("SERVICE_CODE");
				productSubscriptionId = bundleItems.get(i).get("PRODUCT_SUBSCRIPTION_ID").toString();
				productCode = getProductInfo(dao, servSubsId, serviceCode, refList,pathValueMap);
				if (!isServiceRelevant)
					continue;
				populateFunctionData(dao,refList,pathValueMap,productCode,servSubsId,serviceCode);
			}
			if (pathValueMap.size() > 0)
				theHelper.handleFunctions(ape,pathValueMap);
			String[] categties = {"PaketPreis","PaketID","PaketName","PaketInternerName"};
			//(String[]) categoryList.toArray(new String[0]);
			HashMap<String,Object> refValue = populateReferenceData(refList,categties);
			if (refValue!= null){
				Set<String> refkeys = refValue.keySet();
				Iterator<String> refkeyiter = refkeys.iterator();
				while (refkeyiter.hasNext()){
					String path = refkeyiter.next();
					ape.setAttributeByPath(path,refValue.get(path) );
				}
			}
			for(Integer i = 1;i<ape.getFunktion().size()+1;i++){
				HashMap<String,Integer> apefunctype = new HashMap<String, Integer>();
				Integer apenumber = 1;
				String funcType = ape.getFunktion().get(i-1).getFunktionType();
				if (apefunctype.get(funcType) != null)
					apenumber = ((Integer)apefunctype.get(funcType))+1;
				apefunctype.put(funcType,apenumber);
				if (ape.getFunktion().get(i-1).getApeNummer() == null)
					ape.getFunktion().get(i-1).setApeNummer(apenumber);
			}
			returnXml = populateMasterData(am);
			if (serviceStatus == Status.SUCCESS)
				theHelper.prefetch(customerNumber,accessNumber,"HoleVertriebspaketDetails",input,output);
		}catch (BksFunctionalException e) {
			returnXml = populateMasterData(null);
		}catch (BksDataException e) {
			logger.error(e.getMessage());
			serviceStatus = Status.PRECONDITION_ERROR;
			errorCode = "BKS0008";
			errorText = e.getMessage();
			returnXml = populateMasterData(null);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			serviceStatus = Status.PRECONDITION_ERROR;
			errorCode = "BKS0008";
			errorText = e.getMessage();
			returnXml = populateMasterData(null);
		}
		finally {
			dao.closeConnection();
		}
		return returnXml;
	}
	
	private String getProductInfo(HoleVertriebspaketDetailsDAO dao, String servSubsId, 
			 String serviceCode,HashMap<String, Object> refList, HashMap<String,Object> pathValueMap) throws Exception {
		String productCode  = null;
		isServiceRelevant = true;
		productCode = retrieveContractData(dao,servSubsId,serviceCode,refList,pathValueMap,true);
		if (!isServiceRelevant)
			return null;

		if (productCode == null && serviceStatus == Status.SUCCESS)
			productCode = retrieveContractData(dao,servSubsId,serviceCode,refList,pathValueMap,false);
		if (productCode == null  && serviceStatus == Status.SUCCESS){
			serviceStatus = Status.FUNCTIONAL_ERROR;
			errorText = "No signed contract found for Service Subscription " + servSubsId;
			errorCode = "BKS0018";
			errorParams = new String [] {servSubsId};
			throw new BksFunctionalException();
		} else if (productCode != null ){
			retrieveBandwidthAndCondition(dao,servSubsId,refList);
			retrieveHardwareArticle(dao,productSubscriptionId);
		}
		if (refList.get("ServiceCodeBandwidth") == null) 
			getBandwitdthAndConditionForProd(dao,servSubsId,refList);

		return productCode;
	}

	private void populateFunctionData(
			HoleVertriebspaketDetailsDAO dao, HashMap<String,Object> refList, HashMap<String,Object> pathValueMap, String productCode,
			String servSubsId, String serviceCode) throws Exception {
		HashMap<String,String> functions =BksRefDataCacheHandler.getFunction();
		String ape = functions.get(serviceCode);
        String category = "Profil"+ape;
		populateProfile(refList,pathValueMap,serviceCode,category);
        category = "Tarifname"+ape;
		populateProfile(refList,pathValueMap,serviceCode,category);
		dynamicPaths.clear();
		populateSubscriptionExistance(dao,pathValueMap,productCode,servSubsId);
		ArrayList<HashMap<String, Object>> otherMainServices = dao.getMainAccessService(servSubsId);
		for (int i=0;otherMainServices!=null&&i<otherMainServices.size();i++){
			String serviceId = otherMainServices.get(i).get("SERVICE_SUBSCRIPTION_ID").toString();
			populateSubscriptionExistance(dao,pathValueMap,productCode,serviceId);		
		}
		populateAccessNumbers(dao,pathValueMap,servSubsId,serviceCode);
		populateConfigValues(dao,pathValueMap,servSubsId,serviceCode);
		populateAddresses(dao,pathValueMap,servSubsId,serviceCode);
	}

	private void populateProfile(HashMap<String,Object> reflist, HashMap<String, Object> pathValueMap, String serviceCode, String category) throws Exception {
        HashMap<String, Object> profileMap = 
        	theHelper.populateRefdataApe(reflist, new String [] {category});
        Iterator<String> iter = profileMap.keySet().iterator();
        if (iter.hasNext()){
        	String key = iter.next();
        	if (pathValueMap.containsKey(key)) {
        		Object currentValue = profileMap.get(key);
        		Object value = pathValueMap.get(key);
        		if (value instanceof ArrayList) 
        			((ArrayList)value).add(currentValue);
        		else {
        			ArrayList<Object> tmpValue = new ArrayList<Object>();
        			tmpValue.add(value);
        			tmpValue.add(currentValue);
        			pathValueMap.put(key,tmpValue);
        		}
        	} else
        		pathValueMap.putAll(profileMap);
        }
	}

	private void populateAddresses(HoleVertriebspaketDetailsDAO dao,
			HashMap<String, Object> pathValueMap, String servSubsId,
			String serviceCode) throws Exception {
		ArrayList<HashMap<String, Object>> addresses = dao.getAddresses(servSubsId);
		if (addresses == null)
			return;
		HashMap<String,HashMap<String,Object>> charMap = BksRefDataCacheHandler.getServiceColumnMap().get(serviceCode);
		for (int i =0;charMap!=null&&i<addresses.size();i++) {
			String serviceCharCode = (String) addresses.get(i).get("SERVICE_CHARACTERISTIC_CODE");
			if (charMap.get(serviceCharCode) != null && !isAddressPopulated){
				Set<String> keys = charMap.get(serviceCharCode).keySet();
				Iterator<String> keyiter = keys.iterator();
				while (keyiter.hasNext()){
					String columnName = keyiter.next();
					String columnValue = (String) addresses.get(i).get(columnName);
					Object valueObj = charMap.get(serviceCharCode).get(columnName);
					if (!(valueObj instanceof ArrayList)){
						populateSingleColumn((String[])valueObj,columnValue,pathValueMap);
					} else {
						ArrayList<String[]> list = (ArrayList<String[]>)valueObj;
						for (int j = 0; list != null && j < list.size(); j++) {
							String[] item = list.get(j);
							populateSingleColumn(item,columnValue,pathValueMap);
						}
					}
				}
				isAddressPopulated = true;
			}
		}
	}

	private void populateSingleColumn(String[] valueObj, String columnValue,
			HashMap<String, Object> pathValueMap) throws Exception {
		String path = valueObj[0];
		String conversionMethod = valueObj[1];
		String fieldNumber = valueObj[2];
		if (path != null && columnValue != null){
			if (fieldNumber != null)
				columnValue = getPartValue(columnValue,Integer.parseInt(fieldNumber));
			if (conversionMethod == null) {
				String value = (String) pathValueMap.get(path);
				if (value == null)
					pathValueMap.put(path,columnValue);
				else{
					Pattern p = Pattern.compile("[a-zA-Z]*");
					Matcher m = p.matcher(value);
					boolean b = m.matches();
					if (b)
						value = columnValue + value;
					else
						value = value + columnValue;
					pathValueMap.put(path,value);
				}
			} else {
				if (columnValue.trim().length() != 0){
					Object converted = theHelper.invokeMethod(theHelper, conversionMethod, columnValue, String.class);
					pathValueMap.put(path,converted);
				}
			}
		}
	}

	private String getPartValue(String columnValue, int fieldNumber) {
		int i=0;
        for (;i<columnValue.length()&&Character.isDigit(columnValue.charAt(i));i++);
        if (fieldNumber == 1 && i<=columnValue.length())
        	return columnValue.substring(0,i);
        if (fieldNumber != 1 && i<columnValue.length())
        	return columnValue.substring(i,columnValue.length());
		return "";
	}

	private void populateAccessNumbers(HoleVertriebspaketDetailsDAO dao,
			HashMap<String, Object> pathValueMap, String servSubsId,
			String serviceCode) throws Exception {
		ArrayList<HashMap<String, Object>> accessNumbers = dao.getAccessNumberChars(servSubsId);
		if (accessNumbers == null)
			return;
		HashMap<String,HashMap<Integer,Object[]>> charMap = BksRefDataCacheHandler.getServiceFieldMap().get(serviceCode);
		for (int i =0;charMap!=null&&i<accessNumbers.size();i++) {
			String serviceCharCode = (String) accessNumbers.get(i).get("SERVICE_CHARACTERISTIC_CODE");
			String number = (String) accessNumbers.get(i).get("ACCESS_NUMBER");
			if (number != null && charMap.get(serviceCharCode) != null){
				HashMap<Integer,Object[]> fieldPathMap = charMap.get(serviceCharCode);
				String[] numberFields = number.split(";");
				for(Integer j=0;j<numberFields.length;j++){
					Object[] paths = fieldPathMap.get(j+1);
					for (int k = 0;paths!=null&& k < paths.length; k++) {
						if (paths[k] instanceof String[]){
							String[] fields = (String[]) paths[k];
							if (fields[1]==null)
								pathValueMap.put((String)fields[0],numberFields[j]);
							else
								pathValueMap.put((String)fields[0],(String)fields[1] + numberFields[j]);
						}
					}
				}
			}
		}
	}

	private void populateConfigValues(HoleVertriebspaketDetailsDAO dao,
			HashMap<String, Object> pathValueMap, String servSubsId, String serviceCode) throws Exception  {

		ArrayList<String[]> subscriptions = new ArrayList<String[]>();
		subscriptions.add(new String[] {servSubsId,serviceCode,null});
		getAllChildServices(dao,servSubsId,subscriptions);
		/*
		ArrayList<HashMap<String, Object>> services = dao.getAllChildServices(servSubsId);
		for (int i=0;services!=null&&i<services.size();i++){
			String servCode = (String) services.get(i).get("SERVICE_CODE");
			String parentServCode = (String) services.get(i).get("PARENT_SERVICE_CODE");
			String servId = (String) services.get(i).get("SERVICE_SUBSCRIPTION_ID");
			subscriptions.add(new String[] {servId,servCode,parentServCode});
		}
	    */
		
		for (int j = 0; j < subscriptions.size(); j++) {
			String mapKey = 
				((subscriptions.get(j)[2]==null)?subscriptions.get(j)[1]:subscriptions.get(j)[1]+";"+subscriptions.get(j)[2]);
			HashMap<String,ArrayList<String[]>> charPath = BksRefDataCacheHandler.getServiceCharMap().get(mapKey);
			if (charPath == null )
				continue;
			ArrayList<HashMap<String, Object>> configValues = dao
					.getConfiguredValues(subscriptions.get(j)[0]);
			if (configValues == null)
				continue;
			HashMap<String, String> ccbAawMap = BksRefDataCacheHandler
					.getCcbAawMap();
			for (int i = 0; charPath != null && i < configValues.size(); i++) {
				String serviceCharCode = (String) configValues.get(i).get(
						"SERVICE_CHARACTERISTIC_CODE");
				String charValue = (String) configValues.get(i).get(
						"CONFIGURED_VALUE_STRING");
				if (charValue == null)
					continue;
				if (ccbAawMap.get(charValue) != null)
					charValue = ccbAawMap.get(charValue);
				if (charPath.get(serviceCharCode) != null) {
					String rdsId = charPath.get(serviceCharCode).get(0)[1];
					String conversionMethod = charPath.get(serviceCharCode).get(0)[2];
					if (rdsId == null && conversionMethod == null)
						if (pathValueMap.containsKey(charPath.get(serviceCharCode).get(0)[0])) {
							String currentValue = charValue.trim();
							Object value = pathValueMap.get(charPath.get(serviceCharCode).get(0)[0]);
							if (value instanceof ArrayList) 
								((ArrayList)value).add(currentValue);
							else {
								ArrayList<Object> tmpValue = new ArrayList<Object>();
								tmpValue.add(value);
								tmpValue.add(currentValue);
								pathValueMap.put(charPath.get(serviceCharCode).get(0)[0],tmpValue);
							}
						} else 
							pathValueMap.put(charPath.get(serviceCharCode).get(0)[0],charValue.trim());
					else if (rdsId != null) {
						HashMap<String, Object> charList = new HashMap<String, Object>();
						charList.put("CHARACTERISTIC;" + serviceCharCode,
								charValue);
						pathValueMap.putAll(theHelper.populateRefdataApe(
								charList, new String[] { rdsId }));
					} else if (conversionMethod != null) {
						Object converted = theHelper.invokeMethod(theHelper,
								conversionMethod, charValue, String.class);
						if (converted != null)
							pathValueMap.put(charPath.get(serviceCharCode).get(0)[0],
								converted);
					}
				}
			}
		}
		
	}

	private void getAllChildServices(HoleVertriebspaketDetailsDAO dao, String servSubsId, ArrayList<String[]> subscriptions) throws Exception {
		ArrayList<HashMap<String, Object>> services = dao.getAllChildServices(servSubsId);
		for (int i=0;services!=null&&i<services.size();i++){
			String servCode = (String) services.get(i).get("SERVICE_CODE");
			String parentServCode = (String) services.get(i).get("PARENT_SERVICE_CODE");
			String servId = (String) services.get(i).get("SERVICE_SUBSCRIPTION_ID");
			subscriptions.add(new String[] {servId,servCode,parentServCode});
			ArrayList<HashMap<String, Object>> services2 = dao.getAllChildServices(servId);
			for (int j=0;services2!=null&&j<services2.size();j++){
				String servCode2 = (String) services2.get(j).get("SERVICE_CODE");
				String parentServCode2 = (String) services2.get(j).get("PARENT_SERVICE_CODE");
				String servId2 = (String) services2.get(j).get("SERVICE_SUBSCRIPTION_ID");
				subscriptions.add(new String[] {servId2,servCode2,parentServCode2});
			}
		}
	}

	private void populateSubscriptionExistance(	HoleVertriebspaketDetailsDAO dao, 
			HashMap<String,Object> pathValueMap, String productCode, String servSubsId) throws Exception {
		ArrayList<HashMap<String, Object>> services = dao.getChildServices(servSubsId);
		if (services == null)
			return;
		HashMap<String,Object[]> existSubs = BksRefDataCacheHandler.getServiceExistMap().get(productCode);
		for (int i =0;i<services.size();i++) {
			String serviceCode = (String) services.get(i).get("SERVICE_CODE");
			if (existSubs != null && existSubs.get(serviceCode) != null){
				String targetPath = (String)existSubs.get(serviceCode)[0];
				String defaultValue = (String)existSubs.get(serviceCode)[1];
				String conversionMethod = (String)existSubs.get(serviceCode)[2];

				if( conversionMethod == null){
					if (pathValueMap.containsKey(targetPath) || dynamicPaths.contains(targetPath)){
						String[] nodes = targetPath.split("\\.");
						if (nodes.length == 3){
							String previousValue = (String) pathValueMap.remove(targetPath);
							dynamicPaths.add(targetPath);
							if (previousValue != null)
								populateDynymicList(nodes,previousValue);
							populateDynymicList(nodes,defaultValue);
						}
					}else 
						pathValueMap.put(targetPath,defaultValue);
				} else {
					Object converted = theHelper.invokeMethod(theHelper, conversionMethod, defaultValue, String.class);
					pathValueMap.put(targetPath,converted);
				}
			}
		}
	}

	private void populateDynymicList(String[] nodes, String value) {
		HashMap<String,Object> function = (HashMap<String,Object>)functionList.get(nodes[0]);
		if (function == null){
			function = new HashMap<String,Object>();
			functionList.put(nodes[0],function);
		}
		HashMap<String,Object> leaf = new HashMap<String, Object>();
		leaf.put(nodes[2], value);
		if (function.containsKey(nodes[1])) {
			ArrayList<Object> list = (ArrayList<Object>)function.get(nodes[1]);
			list.add(leaf);
		}else {
			ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String,Object>>();
			list.add(leaf);
			if (nodes[1] != null)
				function.put(nodes[1],list);
		}
	}

	private void retrieveBandwidthAndCondition(HoleVertriebspaketDetailsDAO dao, String servSubId, HashMap<String,Object> refList) throws Exception {
		if (serviceStatus != Status.SUCCESS)
			return;
		ArrayList<HashMap<String, Object>> bwList = dao.getBandwidthService(servSubId);
		if (bwList!= null && bwList.size() == 1){
			String serviceCode = bwList.get(0).get("SERVICE_CODE").toString();
			refList.put("ServiceCodeBandwidth",serviceCode);
		}
		ArrayList<HashMap<String, Object>> condList = dao.getCondition(servSubId);
		if (condList!= null && condList.size() == 1){
			String configuredValue = condList.get(0).get("CONFIGURED_VALUE_STRING").toString();
			refList.put("ActionOffer",configuredValue);
		}
		return;
	}

	private String[] retrieveMasterData(HoleVertriebspaketDetailsDAO dao, HoleVertriebspaketDetailsInput input) throws Exception {
		if (serviceStatus != Status.SUCCESS)
			return null;
		
		String[] serviceData = null;
		if (input.getKundennummer() != null && 
			input.getRufnummer() == null && 
			input.getVertragsnummer() == null) {
			ArrayList<HashMap<String, Object>> bundles = dao.getCustomerBundles(input.getKundennummer());
			if ((bundles != null) && (bundles.size() > 1)) {
				serviceStatus = Status.FUNCTIONAL_ERROR;
				errorText = "Multiple bundles found for the customer "+ input.getKundennummer();
				errorCode = "BKS0016";
				throw new BksFunctionalException();
			}
			if ((bundles == null) || (bundles.size() == 0))
				serviceData = getUnbundledServiceByCustomer(dao,input);
			else
				serviceData = getServiceByCustomer(dao,input);
		} else
			serviceData = getCustomerByAccessNumber(dao,input);

		if (serviceData == null){
			serviceStatus = Status.FUNCTIONAL_ERROR;
			String param = null;
			if (input.getRufnummer() != null)
				param = input.getRufnummer().getVorwahl()+"-"+input.getRufnummer().getRufnummer();
			else 
				param = customerNumber;
			errorText = "No service subscription found for customer/access number ("+param+").";
			errorCode = "BKS0017";
			errorParams = new String [] {param};
			throw new BksFunctionalException();	
		}

		ArrayList<HashMap<String, Object>> customerData = dao.getCustomerData(customerNumber);
		if ((customerData == null) || (customerData.size() == 0)) {
			serviceStatus = Status.FUNCTIONAL_ERROR;
			errorText = "No customer found";
			errorCode = "BKS0000";
			throw new BksFunctionalException();
		} else {
			boolean isIndividual = customerData.get(0).get("ENTITY_TYPE").equals("I");
			returnList.put("CUSTOMER;CUSTOMER_NUMBER;NatuerlichePerson", customerNumber);
			String classification = customerData.get(0).get("CLASSIFICATION_RD").toString();
			String providerCode = getProviderCode(dao,customerNumber,classification);
			returnList.put("CUSTOMER;CUSTOMER_NUMBER;NatuerlichePerson", customerNumber);
			returnList.put("CUSTOMER;P_SERVICE_PROVIDER_CODE;NatuerlichePerson", providerCode);
			returnList.put("CUSTOMER;USER_PASSWORD;NatuerlichePerson",customerData.get(0).get("USER_PASSWORD").toString());
			returnList.put("CUSTOMER;CATEGORY_RD;NatuerlichePerson",customerData.get(0).get("CATEGORY_RD").toString());
			returnList.put("CUSTOMER;CLASSIFICATION_RD;NatuerlichePerson", classification);
			boolean postIdent = false;
			String postIdentString = (String) customerData.get(0).get("POST_IDENT_INDICATOR");
			if (postIdentString!=null && postIdentString.equals("Y"))
				postIdent = true;
			returnList.put("CUSTOMER;POSTIDENT;NatuerlichePerson",postIdent);
			if (isIndividual)
				populateIndividual(customerData);
			else
				populateOrganization(customerData);
			returnList.put("ADDRESS;STREET_NAME", customerData.get(0).get("STREET_NAME").toString());
			returnList.put("ADDRESS;STREET_NUMBER", customerData.get(0).get("STREET_NUMBER").toString());
			if (customerData.get(0).get("STREET_NUMBER_SUFFIX") != null)
				returnList.put("ADDRESS;STREET_NUMBER_SUFFIX", customerData.get(0).get("STREET_NUMBER_SUFFIX").toString());
			returnList.put("ADDRESS;POSTAL_CODE", customerData.get(0).get("POSTAL_CODE").toString());
			returnList.put("ADDRESS;CITY_NAME", customerData.get(0).get("CITY_NAME").toString());
			if (customerData.get(0).get("CITY_SUFFIX_NAME") != null)
				returnList.put("ADDRESS;CITY_SUFFIX_NAME", customerData.get(0).get("CITY_SUFFIX_NAME").toString());
			returnList.put("ACCOUNT;ACCOUNT_NUMBER", customerData.get(0).get("ACCOUNT_NUMBER").toString());
		}
		return serviceData;
	}

	private String[] getUnbundledServiceByCustomer(HoleVertriebspaketDetailsDAO dao,
			HoleVertriebspaketDetailsInput input)  throws Exception {
		ArrayList<HashMap<String, Object>> serviceData = dao.getUnbundledServiceByCustomer(customerNumber);
		if ((serviceData != null) && (serviceData.size() != 0)) {
			String serviceSubscrId = serviceData.get(0).get("SERVICE_SUBSCRIPTION_ID").toString();
			String serviceCode = serviceData.get(0).get("SERVICE_CODE").toString();
			productSubscriptionId = serviceData.get(0).get("PRODUCT_SUBSCRIPTION_ID").toString();
			return new String[] {serviceSubscrId,serviceCode};
		}
	    return null;
	}

	private String[] getServiceByCustomer(HoleVertriebspaketDetailsDAO dao,
			HoleVertriebspaketDetailsInput input) throws Exception {
		ArrayList<HashMap<String, Object>> serviceData = dao.getServiceByCustomer(customerNumber);
		if ((serviceData != null) && (serviceData.size() != 0)) {
			String serviceSubscrId = serviceData.get(0).get("SERVICE_SUBSCRIPTION_ID").toString();
			String serviceCode = serviceData.get(0).get("SERVICE_CODE").toString();
			productSubscriptionId = serviceData.get(0).get("PRODUCT_SUBSCRIPTION_ID").toString();
			return new String[] {serviceSubscrId,serviceCode};
		}
	    return null;
	}

	private void populateOrganization(
			ArrayList<HashMap<String, Object>> customerData) {
		returnList.put("ENTITY;NAME;JP", customerData.get(0).get("NAME").toString());
		if (customerData.get(0).get("ORGANIZATION_TYPE_RD") != null)
			returnList.put("ENTITY;ORGANIZATION_TYPE_RD;JP", customerData.get(0).get("ORGANIZATION_TYPE_RD").toString());
		if (customerData.get(0).get("INCORPORATION_NUMBER_ID") != null)
			returnList.put("ENTITY;INCORPORATION_NUMBER_ID;JP",customerData.get(0).get("INCORPORATION_NUMBER_ID").toString());
		if (customerData.get(0).get("INCORPORATION_TYPE_RD") != null)
			returnList.put("ENTITY;INCORPORATION_TYPE_RD;JP", customerData.get(0).get("INCORPORATION_TYPE_RD").toString());
		if (customerData.get(0).get("INCORPORATION_CITY_NAME") != null)
			returnList.put("ENTITY;INCORPORATION_CITY_NAME;JP", customerData.get(0).get("INCORPORATION_CITY_NAME").toString());
	}

	private void populateIndividual(ArrayList<HashMap<String, Object>> customerData) {
		returnList.put("ENTITY;NAME;NatuerlichePerson", customerData.get(0).get("NAME").toString());
		if (customerData.get(0).get("FORENAME") != null)
			returnList.put("ENTITY;FORENAME;NatuerlichePerson", customerData.get(0).get("FORENAME").toString());
		CalendarDate cd = CalendarDate.from(customerData.get(0).get("BIRTH_DATE").toString(), "yyyy-MM-dd");
		returnList.put("ENTITY;BIRTH_DATE;NatuerlichePerson", cd);
		returnList.put("ENTITY;SALUTATION_DESCRIPTION;NatuerlichePerson", customerData.get(0).get("SALUTATION_DESCRIPTION").toString());
	}

	private String getProviderCode(HoleVertriebspaketDetailsDAO dao,
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

	private String[] getCustomerByAccessNumber(HoleVertriebspaketDetailsDAO dao,
			HoleVertriebspaketDetailsInput input) throws Exception {
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
				throw new BksFunctionalException();
		}
		ArrayList<HashMap<String, Object>> theList = dao.getExactAccessNumber(countryCode,cityCode,localNumber);
		if ((theList == null) || (theList.size() == 0))
			theList = dao.getAccessNumber(countryCode,cityCode,localNumber);
		if ((theList != null) && (theList.size() != 0)){
			customerNumber = theList.get(0).get("CUSTOMER_NUMBER").toString();
			String serviceSubscrId = theList.get(0).get("SERVICE_SUBSCRIPTION_ID").toString();
			String serviceCode = theList.get(0).get("SERVICE_CODE").toString();
			productSubscriptionId = theList.get(0).get("PRODUCT_SUBSCRIPTION_ID").toString();
			return new String[] {serviceSubscrId,serviceCode};
		} 
		return null;
	}

	private String retrieveContractData(HoleVertriebspaketDetailsDAO dao, String servSubId, String serviceCode, HashMap<String,Object> refList, HashMap<String,Object> pathValueMap, boolean isOrderForm) throws Exception{
		if (serviceStatus != Status.SUCCESS)
			return null;

		HashMap<String,String> functions =BksRefDataCacheHandler.getFunction();
		String ape = functions.get(serviceCode);
		if (ape == null){
			isServiceRelevant = false;
			return null;
		}

		if (ape.equals("Sprache"))
			refList.put("ServiceCodeVoice",serviceCode);

		ArrayList<HashMap<String, Object>> cntrList = null;
		if (isOrderForm)
			cntrList = dao.getOFContractData(servSubId);
		else
			cntrList = dao.getSDContractData(servSubId);
		if ((cntrList == null) || (cntrList.size() == 0)){
			return null;
		} else if (cntrList.size() != 1){
			serviceStatus = Status.FUNCTIONAL_ERROR;
			errorText = "Multiple signed contract found for Service Subscription " + servSubId;
			errorCode = "BKS0019";
			errorParams = new String [] {servSubId};
			throw new BksFunctionalException();
		}
		if (isOrderForm)
			populateContractData(cntrList.get(0),pathValueMap);

		refList.put("P_PRODUCT_COMMITMENT;PRODUCT_CODE;"+ape, cntrList.get(0).get("PRODUCT_CODE").toString());
		refList.put("P_PRODUCT_COMMITMENT;PRICING_STRUCTURE_CODE;"+ape, cntrList.get(0).get("PRICING_STRUCTURE_CODE").toString());
		refList.put("P_PRODUCT_COMMITMENT;PRODUCT_VERSION_CODE;"+ape, cntrList.get(0).get("PRODUCT_VERSION_CODE").toString());
		return cntrList.get(0).get("PRODUCT_CODE").toString();
	}

	private void populateContractData(HashMap<String,Object> contractRow, HashMap<String,Object> pathValueMap) throws Exception {
		String productCode = contractRow.get("PRODUCT_CODE").toString();
		HashMap<String,ArrayList<String[]>> contractMap = 
			BksRefDataCacheHandler.getTableColumnMap().get(productCode+";-");
		if (contractMap == null)
			return;
		Set<String> keys = contractMap.keySet();
		Iterator<String> keyiter = keys.iterator();
		while (keyiter.hasNext()){
			String columnName = keyiter.next();
			if (columnName.equals("CALCULATED_END_DATE")){
				String minUnit = (String)contractRow.get("MIN_PERIOD_DUR_UNIT_RD"); 
				Integer minValue = null;
				if (contractRow.get("MIN_PERIOD_DUR_VALUE") != null) 
					minValue = ((BigDecimal) contractRow.get("MIN_PERIOD_DUR_VALUE")).intValue(); 
				Object minStartDate = contractRow.get("MIN_PERIOD_START_DATE"); 
				String extUnit = (String)contractRow.get("AUTO_EXTENT_DUR_UNIT_RD"); 
				Integer extValue = null;
				if (contractRow.get("AUTO_EXTENT_DUR_VALUE") != null)
					extValue = ((BigDecimal)contractRow.get("AUTO_EXTENT_DUR_VALUE")).intValue();
				boolean bAutoExtend = false;
				if (contractRow.get("AUTO_EXTENSION_IND") != null)
					bAutoExtend = ((String)contractRow.get("AUTO_EXTENSION_IND")).equals("Y");
				pathValueMap.put(contractMap.get(columnName).get(0)[0],
						theHelper.getContractEndDate(minStartDate,minUnit,minValue,extUnit,extValue,bAutoExtend));
				continue;
			} 
			String columnValue = 
				(contractRow.get(columnName)==null)?null:contractRow.get(columnName).toString();
			if (columnValue == null)
				continue;
			String conversionMethod = contractMap.get(columnName).get(0)[1];
			if (conversionMethod != null) {
				Object converted = theHelper.invokeMethod(theHelper,
						conversionMethod, columnValue, String.class);
				pathValueMap.put(contractMap.get(columnName).get(0)[0],converted);
			} else {
				pathValueMap.put(contractMap.get(columnName).get(0)[0],columnValue);
			}
		}
	}

	private String populateMasterData(Auftragsmodell am) throws Exception {
		output = new HoleVertriebspaketDetailsOutput();
		String returnXml = null;
		populateBaseOutput();
		if (functionList.size() > 0 && serviceStatus == Status.SUCCESS){
			Set<String> keys = functionList.keySet();
			Iterator<String> keyIter = keys.iterator();
			while (keyIter.hasNext()){
				String func = keyIter.next();
				HashMap<String,ArrayList<HashMap<String, Object>>> funcMap = 
					(HashMap<String,ArrayList<HashMap<String, Object>>>)functionList.get(func);
				theHelper.handleFunctions(func,am,funcMap);
			}
		}
		if (returnList.size() > 0 && serviceStatus == Status.SUCCESS){
			((HoleVertriebspaketDetailsOutput)output).setAuftragsmodell(am);
		} else if (serviceStatus == Status.FUNCTIONAL_ERROR)
			((HoleVertriebspaketDetailsOutput)output).setAuftragsmodell(theHelper.createErrorAm(errorCode,errorText,errorParams));
			
		try {
			returnXml = theHelper.serialazeForAaw(output, customerNumber,accessNumber,"de.arcor.aaw.kernAAW.bks.services" ,"HoleVertriebspaketDetails");
			if (serviceStatus == Status.SUCCESS && customerNumber != null)
				theHelper.loadCache(customerNumber+";HoleVertriebspaketDetails",returnXml);
			if (serviceStatus == Status.SUCCESS && accessNumber != null)
				theHelper.loadCache(accessNumber+";HoleVertriebspaketDetails",returnXml);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return returnXml;
	}

	private HashMap<String,Object>  populateReferenceData(HashMap<String,Object> refList,String[] categoryList) throws Exception {
		try {
			return theHelper.populateRefdataApe(refList,categoryList);
		} catch (BksDataException e) {
			logger.warn("No Specific Package value found for :"+e.getMessage());
			if(refList.get("ActionOffer") != null){
				refList.remove("ActionOffer");
				return populateReferenceData(refList,categoryList);
			}
			else if(refList.get("ServiceCodeVoice") != null){
				refList.remove("ServiceCodeVoice");
				return populateReferenceData(refList,categoryList);
			}
			HashMap<String, Object> defaultList = new HashMap<String, Object>();
			defaultList.put("CUSTOMER;CUSTOMER_NUMBER;NatuerlichePerson", customerNumber);
			defaultList.put("ServiceCodeVoice","DEFAULT");
			defaultList.put("P_PRODUCT_COMMITMENT;PRODUCT_CODE;Sprache","DEFAULT");
			defaultList.put("P_PRODUCT_COMMITMENT;PRICING_STRUCTURE_CODE;Sprache", "DEFAULT");
            defaultList.put("ServiceCodeBandwidth","DEFAULT");
			logger.info("No Specific Package value found. Default package will be returned.");
			return theHelper.populateRefdataApe(defaultList,categoryList);
		}
	}

	public void setInput(InputData input) {
		super.setInput(input);
		if (((HoleVertriebspaketDetailsInput)input).getRufnummer() != null){
			String countryCode = ((HoleVertriebspaketDetailsInput)input).getRufnummer().getLaenderkennzeichen();
			String cityCode = ((HoleVertriebspaketDetailsInput)input).getRufnummer().getVorwahl();
			String localNumber = ((HoleVertriebspaketDetailsInput)input).getRufnummer().getRufnummer();
			accessNumber = countryCode.substring(2)+cityCode.substring(1)+localNumber;
		}
		if (((HoleVertriebspaketDetailsInput)input).getKundennummer() != null)
			customerNumber = ((HoleVertriebspaketDetailsInput)input).getKundennummer();
	}

	private void retrieveHardwareArticle(HoleVertriebspaketDetailsDAO dao, String prodSubsId) throws Exception {
		ArrayList<HashMap<String, Object>> theHwList = dao.getHardwareCharacteristic(prodSubsId);
		HashMap<String,Object>  hardwareArticle = null;
		String path = null;
		int maxSize=0;
		for (int i =0;theHwList!=null&&i<theHwList.size(); i++){
			HashMap hardware = (HashMap)functionList.get("Hardware");
			if (hardware == null){
				hardware = new HashMap<String,Object>();
				functionList.put("Hardware",hardware);
			}
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
				if (hardware != null && hardware.containsKey(path)) {
					ArrayList<Object> list = (ArrayList<Object>)hardware.get(path);
					if (list.size() < maxSize)
						list.add(hardwareArticle);
				}else {
					
					ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String,Object>>();
					list.add(hardwareArticle);
					if (path != null)
						hardware.put(path,list);
				}
			}
		}
/*
		theHwList = dao.getHardwareCharacteristic(prodSubsId,"V0114");
		for (int i =0;theHwList!=null&&i<theHwList.size(); i++){
			String configuredValue = theHwList.get(i).get("CONFIGURED_VALUE_STRING").toString();
			HashMap<String,Object>  hardwareArticle = functionList.get(i+funcListInitialSize);
			if (hardwareArticle != null)
			   hardwareArticle.put("subventioniertErhalten",new Boolean (configuredValue.equals("1")));
		}
*/
		return;
	}

	private void getBandwitdthAndConditionForProd(HoleVertriebspaketDetailsDAO dao, String servSubsId, HashMap<String, Object> refList) throws Exception {
		if (serviceStatus != Status.SUCCESS)
			return;
		ArrayList<HashMap<String, Object>> theList = dao.getMainAccessService(servSubsId);
		for (int i=0;theList!=null&&i<theList.size();i++){
			retrieveBandwidthAndCondition(dao,theList.get(i).get("SERVICE_SUBSCRIPTION_ID").toString(),refList);
			if (refList.get("ServiceCodeBandwidth") != null)
				break;
		}
	}

}
