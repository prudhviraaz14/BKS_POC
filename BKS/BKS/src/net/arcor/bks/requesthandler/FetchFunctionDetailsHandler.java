/*
    $Header:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/FetchFunctionDetailsHandler.java-arc   1.31   Aug 08 2017 13:10:06   makuier  $

    $Log:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/FetchFunctionDetailsHandler.java-arc  $
 * 
 *    Rev 1.31   Aug 08 2017 13:10:06   makuier
 * Bug fix
 * 
 *    Rev 1.30   Jul 12 2017 14:25:18   makuier
 * consider individual AN fields when defaulting 
 * 
 *    Rev 1.29   Jun 09 2017 07:56:08   makuier
 * Filter out obsolete nodes for SOM version.
 * 
 *    Rev 1.28   Oct 24 2016 10:40:42   makuier
 * Service versioning for service refdata
 * 
 *    Rev 1.27   Sep 21 2016 07:24:48   makuier
 * Consider product code in the address key.
 * 
 *    Rev 1.26   Jun 20 2016 09:29:06   makuier
 * Corrected SOM versioning.
 * 
 *    Rev 1.25   Mar 09 2016 12:21:16   sibani.panda
 * SPN-BKS-000131985 RMS 147970: Fixes for fixedValue usage and passing product code in populateDependentFunctions and populateSubscriptionExistance methods
 * 
 *    Rev 1.24   Feb 12 2016 15:00:54   makuier
 * Changed the way access number refdata is fetched.
 * 
 *    Rev 1.23   Sep 15 2015 15:42:16   makuier
 * Support for rented hardware added.
 * 
 *    Rev 1.22   Dec 16 2013 14:19:46   makuier
 * Pass the version to the master data.
 * 
 *    Rev 1.21   Nov 07 2013 12:46:04   makuier
 * ResellerId added to the signature.
 * 
 *    Rev 1.20   Mar 14 2013 14:51:34   makuier
 * Hardware with no parent code
 * 
 *    Rev 1.19   Jan 29 2013 18:11:48   makuier
 * Master data populated
 * 
 *    Rev 1.18   Dec 17 2012 12:02:08   makuier
 * Support for access number search added.
*/
package net.arcor.bks.requesthandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.arcor.bks.client.DatabaseClient;
import net.arcor.bks.common.BksDataException;
import net.arcor.bks.common.BksRefDataCacheHandler;
import net.arcor.bks.db.FetchFunctionDetailsDAO;
import net.arcor.epsm_basetypes_001.InputData;
import net.arcor.orderschema.CustomerData;
import net.arcor.orderschema.Order;
import net.arcor.orderschema.ProductBundle;
import de.arcor.aaw.kernAAW.bks.services.FetchFunctionDetailsInput;
import de.arcor.aaw.kernAAW.bks.services.FetchFunctionDetailsOutput;
import edu.emory.mathcs.backport.java.util.Arrays;

public class FetchFunctionDetailsHandler extends BaseServiceHandler {
	
	final static String[] depFunctionServices = {"VI061","VI062","VI076","VI077","V0014","V0277"};
	public FetchFunctionDetailsHandler() {
		super();
	}

	public String execute() throws Exception {
		String returnXml = null;
		FetchFunctionDetailsDAO dao = null;

		try {
			String servSubsId = ((FetchFunctionDetailsInput)input).getServiceSubscriptionId();
			String geoNumber = ((FetchFunctionDetailsInput)input).getGeonumber();
			String msIsdn = ((FetchFunctionDetailsInput)input).getMSISDN();
			Boolean fetchMasterData = ((FetchFunctionDetailsInput)input).isFetchMasterData();
			String servSubsId2 = null;
			String cacheKey = null;
			boolean bUseCache = true;
			if (((FetchFunctionDetailsInput)input).isDisableCache() != null)
				bUseCache = !((FetchFunctionDetailsInput)input).isDisableCache();			
			if (bUseCache) {
				dao = (FetchFunctionDetailsDAO) DatabaseClient.getBksDataAccessObject(null,"FetchFunctionDetailsDAO");
			} else {
				dao = (FetchFunctionDetailsDAO) DatabaseClient.getBksDataAccessObject("onl","FetchFunctionDetailsDAO");
			}

			if (servSubsId!=null){
				cacheKey = servSubsId;
			}else if (geoNumber!=null&&msIsdn==null){
				cacheKey = geoNumber;
				servSubsId = getServiceSubsIdForAccNum(dao,geoNumber);
			}else if (msIsdn!=null&&geoNumber==null){
				cacheKey = msIsdn;
				servSubsId = getServiceSubsIdForAccNum(dao,msIsdn);
			}else if (geoNumber!=null&&msIsdn!=null){
				cacheKey = geoNumber+";"+msIsdn;
				servSubsId = getServiceSubsIdForAccNum(dao,geoNumber);
				servSubsId2 = getServiceSubsIdForAccNum(dao,msIsdn);
			}
			ProductBundle pb = new ProductBundle();

			if (bUseCache&&clientSchemaVersion==null) {
				returnXml = (String) theHelper.fetchFromCache("FetchFunctionDetails", cacheKey, null);
				if (returnXml != null)
					return returnXml;
			}
			getProductBundle(dao, servSubsId, pb);
			if (servSubsId2!=null&&!servSubsId2.equals(servSubsId)) 
				getProductBundle(dao, servSubsId2, pb);
			CustomerData cust = null;
			if (fetchMasterData!=null && fetchMasterData)
				cust = getCustomerData(dao, null, null);
			returnXml = populateOutputObject(pb,cust,cacheKey);
		} catch (BksDataException e) {
			logger.error(e.getMessage());
			serviceStatus = Status.PRECONDITION_ERROR;
			errorCode = "BKS0008";
			errorText = e.getMessage();
			returnXml = populateOutputObject(null,null,null);
		} catch (Exception e) {
			logger.error(e.getMessage());
			serviceStatus = Status.PRECONDITION_ERROR;
			errorCode = "BKS0008";
			if (e.getMessage() != null && e.getMessage().length() > 255)
				errorText = e.getMessage().substring(0, 255);
			else
				errorText = e.getMessage();
			returnXml = populateOutputObject(null,null,null);
		}
		finally {
			dao.closeConnection();
		}
		return returnXml;
	}

	private CustomerData getCustomerData(FetchFunctionDetailsDAO dao, 
			ArrayList<String> accounts, ArrayList<String> sdContractNumbers) throws Exception{
		GetSomMasterDataHandler mdh = new GetSomMasterDataHandler();
		mdh.setClientSchemaVersion(clientSchemaVersion);
		Order order = null;
		   order = mdh.getOrder(customerNumber,null,accounts,sdContractNumbers,true,false);
	   return order.getCustomerData().get(0);
	}
	private String getServiceSubsIdForAccNum(FetchFunctionDetailsDAO dao,String accnum) throws Exception {
		String serSu = null;
		ArrayList<HashMap<String, Object>> services = dao.getServiceForAccessNumber(accnum);
		if (services != null&&0<services.size())
			serSu = (String) services.get(0).get("SERVICE_SUBSCRIPTION_ID");
		return serSu;
	}

	protected String populateOutputObject(ProductBundle pb, CustomerData cust, String cacheKey) throws Exception {
		String returnXml = null;
		output = new FetchFunctionDetailsOutput();
		if (serviceStatus != Status.SUCCESS){
			output.setResult(false);
			output.setErrorCode(errorCode);
			output.setErrorText(errorText);
		}else {
			output.setResult(true);
 			((FetchFunctionDetailsOutput)output).setCustomerData(cust);
 			((FetchFunctionDetailsOutput)output).setProductBundle(pb);
		}
		try {
			returnXml = theHelper.serializeForMcf(output, customerNumber,accessNumber,"de.arcor.aaw.kernAAW.bks.services" ,"FetchFunctionDetails");
			if (serviceStatus == Status.SUCCESS &&	clientSchemaVersion==null && cacheKey != null)
				theHelper.loadCache(cacheKey+";FetchFunctionDetails",returnXml);

		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return returnXml;
	}

	private void getProductBundle(FetchFunctionDetailsDAO dao,String servSubsId, ProductBundle productBundle) throws Exception {
		ArrayList<HashMap<String, Object>> services = dao.getServiceSubscription(servSubsId);
		if (services==null || services.size() == 0 )
			services = dao.getServiceSubscriptionSDC(servSubsId);
		if (services.size() == 0)
			throw new BksDataException("No service subscription found "+servSubsId);
		if (services.get(0) != null){
			String custNum = services.get(0).get("CUSTOMER_NUMBER").toString();
			if (customerNumber==null)
				setCustomerNumber(custNum);
			String parentServiceCode = services.get(0).get("SERVICE_CODE").toString();
			String productCode = services.get(0).get("PRODUCT_CODE").toString();
			productBundle.setOrderPositionNumber(0);
			populateTableData(services.get(0),productCode,parentServiceCode,productBundle);
			populateConfiguredValues(dao,servSubsId,parentServiceCode,null,productBundle,false);
			populateAccessNumbers(dao,servSubsId,parentServiceCode,null,productBundle);
			populateChildServiceList(dao,servSubsId,productCode,parentServiceCode,productBundle);
			populateDepHardwareList(dao,servSubsId,parentServiceCode,productBundle);
		}
	}

	private void populateChildServiceList(FetchFunctionDetailsDAO dao,
			String parentServSubsId, String productCode,String parentServiceCode, ProductBundle productBundle) throws Exception {
		ArrayList<HashMap<String, Object>> services = dao.getChildServices(parentServSubsId);
		if (services == null || services.size() == 0)
			return;
		populateDependentFunctions(dao,services,productCode,parentServiceCode,productBundle,false);
		populateSubscriptionExistance(services,productCode,parentServiceCode,productBundle);
	}

	private void populateSubscriptionExistance(ArrayList<HashMap<String, Object>> services,String productCode,
			String parentServiceCode, ProductBundle bundleData) throws Exception {
		HashMap<String,Object[]> existSubs = BksRefDataCacheHandler.getSomServiceExistMap().get(productCode);
		for (int i =0;existSubs != null && i<services.size();i++) {
			String key = (String) services.get(i).get("SERVICE_CODE");
			if (existSubs.get(key)== null)
				key += ";"+parentServiceCode;
			if (existSubs.get(key) != null){
				String targetPath = (String)existSubs.get(key)[0];
				String defaultValue = (String)existSubs.get(key)[1];
				String conversionMethod = (String)existSubs.get(key)[2];
				String introVersion = (String)existSubs.get(key)[3];
				if (introVersion != null && clientSchemaVersion != null &&
					theHelper.isClientVersLowerThanIntro(introVersion,clientSchemaVersion))
					continue;
				if (conversionMethod != null) {
					Object converted = theHelper.invokeMethod(theHelper,
							conversionMethod, defaultValue, String.class);
					theHelper.setAttributeForPath(bundleData, "set", targetPath, converted);
				} else {
					theHelper.setAttributeForPath(bundleData, "set", targetPath, defaultValue);
				}
			}
		}
	}

	private void populateTableData(HashMap<String, Object> row,
			String productCode,String serviceCode, ProductBundle productBundle) throws Exception {
		HashMap<String,ArrayList<String[]>> tableMap = 
			BksRefDataCacheHandler.getSomTableColumnMap().get(productCode+";"+serviceCode);
		if (tableMap == null)
			tableMap = BksRefDataCacheHandler.getSomTableColumnMap().get("-;"+serviceCode);
		if (tableMap == null)
			return;
		Set<String> keys = tableMap.keySet();
		Iterator<String> keyiter = keys.iterator();
		while (keyiter.hasNext()){
			String columnName = keyiter.next();
			if (columnName.equals("-")){
				for (int i = 0; i < tableMap.get(columnName).size(); i++) {
					String path = (String) tableMap.get(columnName).get(i)[0];
					theHelper.setAttributeForPath(productBundle, "set", path, tableMap.get(columnName).get(i)[1].toString());
				}
				continue;
			}
			if (row.get(columnName) == null)
				continue;
			for (int i = 0; i < tableMap.get(columnName).size(); i++) {
				String columnValue = row.get(columnName).toString();
				if (tableMap.get(columnName).get(i)[2] != null)
					columnValue = tableMap.get(columnName).get(i)[2] + columnValue;
				String introVersion = tableMap.get(columnName).get(i)[4];
				if (introVersion != null && clientSchemaVersion != null &&
					theHelper.isClientVersLowerThanIntro(introVersion,clientSchemaVersion))
					continue;
				String conversionMethod = tableMap.get(columnName).get(i)[1];
				if (conversionMethod != null) {
					Object converted = theHelper.invokeMethod(theHelper,
							conversionMethod, columnValue, String.class);
					theHelper.setAttributeForPath(productBundle, "set", tableMap
							.get(columnName).get(i)[0], converted);
				} else {
					theHelper.setAttributeForPath(productBundle, "set", tableMap
							.get(columnName).get(i)[0], columnValue);
				}
			}
		}
	}

	private void populateAccessNumbers(FetchFunctionDetailsDAO dao, String servSubsId,
		String serviceCode,  String parentServiceCode,ProductBundle productBundle) throws Exception {
		ArrayList<HashMap<String, Object>> accessNumbers = dao.getAccessNumberChars(servSubsId);
		if (accessNumbers == null)
			return;
		HashMap<String,HashMap<Integer,Object[]>> charMap = null;
		if (parentServiceCode != null)
			charMap = BksRefDataCacheHandler.getSomServiceFieldMap().get(serviceCode+";"+parentServiceCode);
		if (charMap == null)
			charMap = BksRefDataCacheHandler.getSomServiceFieldMap().get(serviceCode);
		if (charMap == null)
			return;

		ArrayList<HashMap<String, Object>> cscs = dao.getConfigCharacteristics(servSubsId);
		for (int i =0;charMap!=null&&i<accessNumbers.size();i++) {
			String serviceCharCode = (String) accessNumbers.get(i).get("SERVICE_CHARACTERISTIC_CODE");
			String number = (String) accessNumbers.get(i).get("ACCESS_NUMBER");
			if (number != null && charMap.get(serviceCharCode) != null){
				HashMap<Integer,Object[]> fieldPathMap = charMap.get(serviceCharCode);
				String[] numberFields = number.split(";");
				for(Integer j=0;j<numberFields.length;j++){
					Object[] paths = fieldPathMap.get(j+1);
					for (int k = 0; paths != null&&k < paths.length; k++) {
						String[] fields = (String[]) paths[k];
						String path = fields[0];
						String conversionMethod = fields[2];
						String defaultValue = fields[3];
						String depCharCode = fields[4];
						String fixedValue = fields[5];
						if (!isDependentCharPopulate(dao, cscs,depCharCode,fixedValue,j))
							continue;
						if (numberFields[j].startsWith("DEL"))
							theHelper.setAttributeForPath(productBundle, "set",
									path, numberFields[j].substring(3));
						else {
							if (conversionMethod != null) {
								Object converted = theHelper.invokeMethod(theHelper,
										conversionMethod, numberFields[j], String.class);
								if (converted!=null)
									theHelper.setAttributeForPath(productBundle, "set",path, converted);
							} else {
								theHelper.setAttributeForPath(productBundle, "set",path, numberFields[j]);
							}
						}
					}
				}
			}
		}
		// default not populated characteristics
		Set<String> keys = charMap.keySet();
		Iterator<String> keyiter = keys.iterator();
		while (keyiter.hasNext()){
			String refCharCode = keyiter.next();
			HashMap<Integer,Object[]> fieldPathMap = charMap.get(refCharCode);
			Object[] paths = fieldPathMap.get(1);
			if (paths==null||paths.length==0)
				continue;
			String[] fields = (String[]) paths[0];
			String path = fields[0];
			String defaultValue = fields[3];
			if (defaultValue==null)
				continue;
			Object value = theHelper.setAttributeForPath(productBundle, "get",path, null); 
			if (value==null) {
				theHelper.setDesiredNewElement(0);
				theHelper.setAttributeForPath(productBundle, "set",path, defaultValue);
			}
		}

	}
	private boolean isDependentCharPopulate(FetchFunctionDetailsDAO dao,
			ArrayList<HashMap<String, Object>> cscs, String depCharCode,
			String fixedValue, Integer fieldNo) throws Exception {
		if (depCharCode==null)
			return true;
		ArrayList<String> charList = new ArrayList<String>();
		ArrayList<String> cscIdList = new ArrayList<String>();
		for (int i = 0;i < cscs.size(); i++) {
			String serviceCharCode = (String) cscs.get(i).get("SERVICE_CHARACTERISTIC_CODE");
			String cscid = (String) cscs.get(i).get("CONFIG_SERVICE_CHAR_ID");
			charList.add(serviceCharCode);
			if (depCharCode.contains(serviceCharCode))
				cscIdList.add(cscid);
		}
		String[] depCharCodes = depCharCode.split(";");
		for (int i = 0; i < depCharCodes.length; i++) {
			if (!charList.contains(depCharCodes[i]) || !isCorrespondingValuesPopulated(dao,cscIdList,fixedValue,fieldNo)) {
				return false;
			}
		}
		return true;
	}

	private boolean isCorrespondingValuesPopulated(FetchFunctionDetailsDAO dao, ArrayList<String> cscIdList, String fixedValue, Integer fieldNo) throws Exception {
		ArrayList<HashMap<String, Object>> charValues = dao.getCharValues(cscIdList);
		boolean bRevert = false;
		if (fixedValue!=null) {
			bRevert = fixedValue.startsWith(";");
			if (bRevert)
				fixedValue = fixedValue.substring(1);
		}
		if (charValues.size()==0)
			return false;
		for (int i =0;i<charValues.size();i++) {
			String charValue = (String) charValues.get(i).get("VALUE");
			if ((charValue==null || charValue.trim().length()==0) && fixedValue==null)
				return false;
			if (fieldNo!=null){
				String[] charValueFields = charValue.split(";");
				charValue=charValueFields[fieldNo];
			}
			if (fixedValue!=null&&fixedValue.equals(charValue))
				return !bRevert;
			if (fixedValue!=null&&!fixedValue.equals(charValue))
				return bRevert;
		}
		return true;
	}

	private void populateConfiguredValues(FetchFunctionDetailsDAO dao,String servSubsId,
			 String serviceCode, String parentServiceCode,ProductBundle productBundle, boolean bIsRented) throws Exception {
		HashMap<String,  ArrayList<String[]>> charPath = null;
		if (parentServiceCode == null)
			charPath = BksRefDataCacheHandler.getSomServiceCharMap().get(serviceCode);
		else
			charPath = BksRefDataCacheHandler.getSomServiceCharMap().get(serviceCode+";"+parentServiceCode);

		if (charPath!=null && charPath.get("-") != null) {
			for (int j = 0; j < charPath.get("-").size(); j++) {
				String path = charPath.get("-").get(j)[0];
				String introVersion = charPath.get("-").get(j)[7];
				if (introVersion != null && clientSchemaVersion != null &&
					theHelper.isClientVersLowerThanIntro(introVersion,clientSchemaVersion))
					continue;
				if (bIsRented)
					path =path.replaceAll("HardwareConfiguration", "RentedHardwareConfiguration");
				String conversionMethod = charPath.get("-").get(j)[2];
				String defaultValue = charPath.get("-").get(j)[5];
				if (conversionMethod == null)
					theHelper.setAttributeForPath(productBundle, "set",path, defaultValue);
				else if (conversionMethod != null) {
					Object converted = theHelper.invokeMethod(theHelper,
							conversionMethod, defaultValue, String.class);
					theHelper.setAttributeForPath(productBundle, "set",path, converted);
				}
			}
		}
		
		ArrayList<HashMap<String, Object>> configValues = dao.getConfiguredValues(servSubsId);
		if (configValues == null || charPath == null )
			return;
		for (int i = 0;i < configValues.size(); i++) {
			String serviceCharCode = (String) configValues.get(i).get("SERVICE_CHARACTERISTIC_CODE");
			String columnValue = (String) configValues.get(i).get("CONFIGURED_VALUE_STRING");
			if (charPath.get(serviceCharCode) != null) {
				for (int j = 0; j < charPath.get(serviceCharCode).size(); j++) {
					String rdsId = charPath.get(serviceCharCode).get(j)[1];
					String conversionMethod = charPath.get(serviceCharCode).get(j)[2];
					String fieldNumber = charPath.get(serviceCharCode).get(j)[3];
					String depCharCode = charPath.get(serviceCharCode).get(j)[4];
					String defaultValue = charPath.get(serviceCharCode).get(j)[5];
					String fixedValue = charPath.get(serviceCharCode).get(j)[6];
					String introVersion = charPath.get(serviceCharCode).get(j)[7];
					String lastVersion=null;
					if (introVersion!=null&&introVersion.contains(";")){
						String[] versions = introVersion.split(";");
						introVersion=versions[0];
						lastVersion=versions[1];
					}						
					if (introVersion != null && clientSchemaVersion != null &&
						theHelper.isClientVersLowerThanIntro(introVersion,clientSchemaVersion))
						continue;
					if (lastVersion != null && (theHelper.isClientVersLowerThanIntro(clientSchemaVersion,lastVersion) || clientSchemaVersion==null))
						continue;
					if (columnValue == null)
						columnValue=defaultValue;
					if (columnValue == null)
						continue;
					String charValue = columnValue;
					if (fixedValue != null&&depCharCode==null){
						charValue=fixedValue;
					}
					else if (fieldNumber != null){
						String[] fields = charValue.split(";");
						if (fields.length >= Integer.parseInt(fieldNumber))
							charValue = fields[Integer.parseInt(fieldNumber)-1];
						else
							continue;
					}
					charValue=charValue.trim();
					String path=charPath.get(serviceCharCode).get(j)[0];
					if (bIsRented)
						path =path.replaceAll("HardwareConfiguration", "RentedHardwareConfiguration");
					if (rdsId == null && conversionMethod == null)
						theHelper.setAttributeForPath(productBundle, "set",	path, charValue);
					else if (conversionMethod != null) {
						Object converted = theHelper.invokeMethod(theHelper,
								conversionMethod, charValue, String.class);
						theHelper.setAttributeForPath(productBundle, "set",	path, converted);
					}
				}
			}
		}
	}

	private void populateDepHardwareList(FetchFunctionDetailsDAO dao, String parentServSubsId,
			 String parentServiceCode, ProductBundle productBundle) throws Exception {
		ArrayList<HashMap<String, Object>> services = dao.getHardwareDepService(parentServSubsId);
		if (services == null || services.size() == 0)
			return;
		populateDependentFunctions(dao,services,"-",parentServiceCode,productBundle,true);
	}
	

	private void populateDependentFunctions(FetchFunctionDetailsDAO dao,
			ArrayList<HashMap<String, Object>> services,String productCode, String parentServiceCode, 
			ProductBundle productBundle,boolean isHardware) throws Exception {
		for (int i =0;i<services.size();i++) {
			String serviceCode = services.get(i).get("SERVICE_CODE").toString();
			String servSubsId = services.get(i).get("SERVICE_SUBSCRIPTION_ID").toString();
			boolean bIsRented=false;
			if (isHardware){
				bIsRented=isHardwareRented(dao,servSubsId);
				if (bIsRented && clientSchemaVersion != null &&
					theHelper.isClientVersLowerThanIntro("18.0",clientSchemaVersion))
						continue;
			}
			theHelper.setDesiredNewElement(1);
			if (isHardware || Arrays.asList(depFunctionServices).contains(serviceCode))
				theHelper.setDesiredNewElement(0);
			HashMap<String, ArrayList<String[]>> tableColMap = null;
			tableColMap = BksRefDataCacheHandler.getSomTableColumnMap().get(productCode+";"+serviceCode+";"+parentServiceCode);
			if (tableColMap == null) 
				tableColMap = BksRefDataCacheHandler.getSomTableColumnMap().get(productCode+";"+serviceCode);
			if (tableColMap == null) 
				tableColMap = BksRefDataCacheHandler.getSomTableColumnMap().get("-;"+serviceCode);
			if (tableColMap != null) {
				Set<String> keys = tableColMap.keySet();
				Iterator<String> keyiter = keys.iterator();
				while (keyiter.hasNext()){
					String columnName = keyiter.next();
					if (columnName.equals("-")){
						for (int j = 0; j < tableColMap.get(columnName).size(); j++) {
							String path = (String) tableColMap.get(columnName).get(j)[0];
							if (bIsRented)
								path=path.replaceAll("HardwareConfiguration", "RentedHardwareConfiguration");
							theHelper.setAttributeForPath(productBundle, "set", path, tableColMap.get(columnName).get(j)[1].toString());
						}
						continue;
					}
					if (services.get(i).get(columnName)==null)
						continue;
					for (int j = 0; j < tableColMap.get(columnName).size(); j++) {
						String columnValue = services.get(i).get(columnName).toString();
						if (tableColMap.get(columnName).get(j)[2] != null)
							columnValue = tableColMap.get(columnName).get(j)[2] + columnValue;
						String path = tableColMap.get(columnName).get(j)[0];
						if (bIsRented)
							path =path.replaceAll("HardwareConfiguration", "RentedHardwareConfiguration");
						String conversionMethod = tableColMap.get(columnName).get(j)[1];
						String introVersion = tableColMap.get(columnName).get(j)[4];
						if (introVersion != null && clientSchemaVersion != null &&
						theHelper.isClientVersLowerThanIntro(introVersion,clientSchemaVersion))
							continue;
						if (conversionMethod != null) {
							Object converted = theHelper.invokeMethod(
									theHelper, conversionMethod, columnValue,
									String.class);
							theHelper.setAttributeForPath(productBundle, "set", path, converted);
						} else {
							theHelper.setAttributeForPath(productBundle, "set", path, columnValue);
						}
					}
				}
			}
			populateConfiguredValues(dao,servSubsId,serviceCode,parentServiceCode,productBundle,bIsRented);
			populateAddresses(dao,servSubsId,productCode,serviceCode,productBundle,bIsRented);
			populateAccessNumbers(dao,servSubsId,serviceCode,parentServiceCode,productBundle);
		}
	}

	private boolean isHardwareRented(FetchFunctionDetailsDAO dao,
			String servSubsId) throws Exception {
		ArrayList<HashMap<String, Object>> initialTicket = dao.getInitialTicketForService(servSubsId);
		if (initialTicket!=null && initialTicket.size() > 0) {
			String usageMode = (String) initialTicket.get(0).get("USAGE_MODE_VALUE_RD");
			if (usageMode.equals("6"))
				return true;
		}
		return false;
	}

	private void populateAddresses(FetchFunctionDetailsDAO dao,
			String servSubsId, String productCode, String serviceCode, ProductBundle productBundle, boolean bIsRented) throws Exception {
		ArrayList<HashMap<String, Object>> addresses = dao.getAddress(servSubsId);
		if (addresses == null)
			return;
		HashMap<String,HashMap<String,Object>> charMap = BksRefDataCacheHandler.getSomServiceColumnMap().get(productCode+";"+serviceCode);
		if (charMap==null)
			charMap = BksRefDataCacheHandler.getSomServiceColumnMap().get("-;"+serviceCode);
		for (int i =0;charMap!=null&&i<addresses.size();i++) {
			String serviceCharCode = (String) addresses.get(i).get("SERVICE_CHARACTERISTIC_CODE");
			if (charMap.get(serviceCharCode) != null){
				Set<String> keys = charMap.get(serviceCharCode).keySet();
				Iterator<String> keyiter = keys.iterator();
				while (keyiter.hasNext()){
					String columnName = keyiter.next();
					String columnValue = (String) addresses.get(i).get(columnName);
					Object valueObj = charMap.get(serviceCharCode).get(columnName);
					if (!(valueObj instanceof ArrayList)){
						String depCharCode = ((String[])valueObj)[3];
						if (columnValue == null)
							continue;
						populateSingleColumn((String[])valueObj,columnValue,productBundle,bIsRented);
					} else {
						ArrayList<String[]> list = (ArrayList<String[]>)valueObj;
						for (int j = 0; list != null && j < list.size(); j++) {
							String[] item = list.get(j);
							String depCharCode = item[3];
							if (columnValue == null)
								continue;
							populateSingleColumn(item,columnValue,productBundle,bIsRented);
						}
					}
				}
			}
		}
	}

	private void populateSingleColumn(String[] valueObj, String columnValue,
			ProductBundle bundleData, boolean bIsRented) throws Exception {
		String path = valueObj[0];
		if (bIsRented)
			path=path.replaceAll("HardwareConfiguration", "RentedHardwareConfiguration");
		String conversionMethod = valueObj[1];
		String fieldNumber = valueObj[2];
		if (path != null && columnValue != null){
			if (fieldNumber != null)
				columnValue = getPartValue(columnValue,Integer.parseInt(fieldNumber));
			if (conversionMethod == null) {
				String value = (String)	theHelper.setAttributeForPath(bundleData, "get",path, null);
				if (value == null)
					theHelper.setAttributeForPath(bundleData, "set", path, columnValue);
				else{
					Pattern p = Pattern.compile("[a-zA-Z]*");
					Matcher m = p.matcher(value);
					boolean b = m.matches();
					if (b)
						value = columnValue + value;
					else
						value = value + columnValue;
					theHelper.setAttributeForPath(bundleData, "set", path, value);
				}
			} else {
				if (columnValue.trim().length() != 0){
					Object converted = theHelper.invokeMethod(theHelper, conversionMethod, columnValue, String.class);
					theHelper.setAttributeForPath(bundleData, "set", path, converted);
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

	public void setInput(InputData input) {
		super.setInput(input);
	}
}
