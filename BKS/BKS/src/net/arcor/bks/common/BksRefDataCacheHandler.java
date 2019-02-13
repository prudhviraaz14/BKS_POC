/*
    $Header:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/common/BksRefDataCacheHandler.java-arc   1.43   Jun 14 2017 16:22:02   makuier  $

    $Log:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/common/BksRefDataCacheHandler.java-arc  $
 * 
 *    Rev 1.43   Jun 14 2017 16:22:02   makuier
 * default value , dep char and fixed value added to access numbers
 * 
 *    Rev 1.42   Oct 24 2016 10:37:58   makuier
 * Added introVersion to service refdata
 * 
 *    Rev 1.41   Sep 21 2016 07:27:38   makuier
 * Consider product code in address key
 * 
 *    Rev 1.40   Oct 29 2015 08:41:08   makuier
 * Set the encoding to ISO-8859
 * 
 *    Rev 1.39   Sep 15 2015 15:22:20   makuier
 * load Pkat added.
 * 
 *    Rev 1.38   Jul 03 2015 14:28:24   makuier
 * Bug fix.
 * 
 *    Rev 1.37   May 27 2015 13:13:02   makuier
 * Source sys cached.
 * 
 *    Rev 1.36   May 13 2015 11:36:10   makuier
 * Bug fix.
 * 
 *    Rev 1.35   May 11 2015 13:17:56   makuier
 * refactor for HTTP client
 * 
 *    Rev 1.34   Apr 24 2013 16:56:34   makuier
 * Intro version is added to ccbsommap
 * 
 *    Rev 1.33   Apr 19 2012 09:33:06   makuier
 * Send convertionMethod to field maps
 * 
 *    Rev 1.32   Oct 21 2011 15:13:58   makuier
 * Load the blocking intention ref data.
 * 
 *    Rev 1.31   Aug 10 2011 17:09:08   makuier
 * defaultValue added to the table map.
 * 
 *    Rev 1.30   Jun 14 2011 12:48:34   makuier
 * Introduction version added.
 * 
 *    Rev 1.29   Jan 03 2011 14:24:18   makuier
 * Support multiple entries in existance map.
 * 
 *    Rev 1.28   Sep 14 2010 14:50:06   makuier
 * Added the field number to the ccb som mapping.
 * 
 *    Rev 1.27   Aug 19 2010 15:50:04   makuier
 * Use product code as key in charMap
 * 
 *    Rev 1.26   Aug 05 2010 11:14:20   makuier
 * Added prefix to access numbers
 * 
 *    Rev 1.25   Jun 23 2010 15:24:04   makuier
 * Maps for open order added.
 * 
 *    Rev 1.24   May 17 2010 16:59:46   makuier
 * CPCOM 1b
 * 
 *    Rev 1.23   Apr 30 2010 14:56:58   makuier
 * Cache the SID reference data.
 * 
 *    Rev 1.22   Mar 17 2010 17:55:46   makuier
 * Main bundle access added.
 * 
 *    Rev 1.21   Dec 18 2009 16:27:34   makuier
 * parent service code and characteristic dependency added. 
 * 
 *    Rev 1.20   Nov 23 2009 17:10:34   makuier
 * Field number for configured values.
 * 
 *    Rev 1.19   Nov 09 2009 11:09:30   makuier
 * Extended the generic cache for SOM mapping
 * 
 *    Rev 1.18   Oct 29 2009 11:18:50   makuier
 * New ref data for IP centrex added.
 * 
 *    Rev 1.17   Sep 22 2009 18:58:44   makuier
 * Added parent service code and contract type to generic conf.
 * 
 *    Rev 1.16   Aug 31 2009 11:42:56   makuier
 * new cross reference group for service code <-> access type added.
 * 
 *    Rev 1.15   May 19 2009 10:10:28   makuier
 * made serviceColumnMap support multiple source for same target.
 * 
 *    Rev 1.14   Mar 19 2009 17:51:16   wlazlow
 * IT-k-25454
 * 
 *    Rev 1.13   Mar 05 2009 14:24:52   makuier
 * Added conversion method for street number.
 * 
 *    Rev 1.12   Nov 25 2008 14:15:18   makuier
 * Generic mapping activated.
 * 
 *    Rev 1.11   Nov 19 2008 17:12:20   wlazlow
 * SPN-BKS-000079790
 * 
 *    Rev 1.10   Oct 29 2008 09:26:00   wlazlow
 * IT-23530
 * 
 *    Rev 1.9   May 30 2008 13:39:14   makuier
 * Some improvements
 * 
 *    Rev 1.8   Apr 22 2008 16:45:54   makuier
 * voicePrioritymap added.
 * 
 *    Rev 1.7   Apr 07 2008 15:17:18   makuier
 * voice online mapping added.
 * 
 *    Rev 1.6   Feb 27 2008 11:25:12   makuier
 * refactoring.
 * 
 *    Rev 1.5   Dec 14 2007 11:52:40   makuier
 * Caches the vodafone customer classifications
 * 
 *    Rev 1.4   Dec 11 2007 19:04:56   makuier
 * Changed the container to list
 * 
 *    Rev 1.3   Dec 06 2007 15:08:12   makuier
 * Use the spring to get the dao.
 * 
 *    Rev 1.2   Nov 28 2007 18:02:40   makuier
 * Catch the dao exception inside dao.
 * 
 *    Rev 1.1   Nov 27 2007 13:59:02   makuier
 * Bug Fixes
 * 
 *    Rev 1.0   Nov 06 2007 17:35:18   makuier
 * Initial revision.
*/
package net.arcor.bks.common;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.arcor.bks.client.DatabaseClientConfig;
import net.arcor.bks.db.GeneralDAO;
import net.arcor.mcf2.exception.base.MCFException;

import org.springframework.context.ApplicationContext;

import de.arcor.aaw.rds.client.ReferenzdatenServiceFacade;
import de.arcor.aaw.rds.client.model.Referenzdaten;
import de.arcor.aaw.rds.client.model.ReferenzdatenContainer;


public class BksRefDataCacheHandler {
    private static String refDataSource = null;
    private static boolean refdataInitialized = false;
    private static HashMap<String,ArrayList<String>> generalData = new HashMap<String,ArrayList<String>>();
	private static HashMap<String,MasterMapObject> masterDataMap = new HashMap<String,MasterMapObject>(); 
	private static HashMap<String,RefdataMapObject> refdataMap = new HashMap<String,RefdataMapObject>(); 
	private static HashMap<String,ArrayList<PrefetchReferenceObject>> prefetchMap = new HashMap<String,ArrayList<PrefetchReferenceObject>>(); 
	private static HashMap<String,ArrayList<String>> nextServiceMap = new HashMap<String,ArrayList<String>>(); 
	private static HashMap<String,ArrayList<String>> voiceOnlineMap = new HashMap<String,ArrayList<String>>(); 
	private static HashMap<String,String> bankNameMap = new HashMap<String,String>(); 
	private static HashMap<String,String> classProviderMap = new HashMap<String,String>(); 
	private static HashMap<String,String> providerCode = new HashMap<String,String>(); 
	private static HashMap<String,String> function = new HashMap<String,String>(); 
	private static HashMap<String,String> access = new HashMap<String,String>(); 
	private static HashMap<String,String> variant = new HashMap<String,String>(); 
	private static HashMap<String,String> ccbAawMap = new HashMap<String,String>(); 

	//Maps for AAW mapping
	private static HashMap<String,HashMap<String,Object[]>> serviceExistMap = new HashMap<String,HashMap<String,Object[]>>(); 
	private static HashMap<String,HashMap<String,ArrayList<String[]>>> tableColumnMap = new HashMap<String,HashMap<String,ArrayList<String[]>>>(); 
	private static HashMap<String,HashMap<String,ArrayList<String[]>>> serviceCharMap = new HashMap<String,HashMap<String,ArrayList<String[]>>>(); 
	private static HashMap<String,HashMap<String,HashMap<Integer,Object[]>>> 
	               	serviceFieldMap = new HashMap<String,HashMap<String,HashMap<Integer,Object[]>>>(); 
	private static HashMap<String,HashMap<String,HashMap<String,Object>>> 
    				serviceColumnMap = new HashMap<String,HashMap<String,HashMap<String,Object>>>(); 
	//Maps for SOM mapping
	private static HashMap<String,HashMap<String,Object[]>> somServiceExistMap = new HashMap<String,HashMap<String,Object[]>>(); 
	private static HashMap<String,HashMap<String,ArrayList<String[]>>> somTableColumnMap = new HashMap<String,HashMap<String,ArrayList<String[]>>>(); 
	private static HashMap<String,HashMap<String,ArrayList<String[]>>> somServiceCharMap = new HashMap<String,HashMap<String,ArrayList<String[]>>>(); 
	private static HashMap<String,HashMap<String,HashMap<Integer,Object[]>>> 
	               	somServiceFieldMap = new HashMap<String,HashMap<String,HashMap<Integer,Object[]>>>(); 
	private static HashMap<String,HashMap<String,HashMap<String,Object>>> 
    				somServiceColumnMap = new HashMap<String,HashMap<String,HashMap<String,Object>>>(); 
	private static HashMap<String, HashMap<String, Object[]>> ccbSomMap = new HashMap<String,HashMap<String,Object[]>>(); 

	//Maps for SID mapping
	private static HashMap<String,HashMap<String,Object[]>> sidServiceExistMap = new HashMap<String,HashMap<String,Object[]>>(); 
	private static HashMap<String,HashMap<String,ArrayList<String[]>>> sidTableColumnMap = new HashMap<String,HashMap<String,ArrayList<String[]>>>(); 
	private static HashMap<String,HashMap<String,ArrayList<String[]>>> sidServiceCharMap = new HashMap<String,HashMap<String,ArrayList<String[]>>>(); 
	private static HashMap<String,String[]> sidAccessName = new HashMap<String, String[]>();
	private static HashMap<String,HashMap<String,HashMap<Integer,Object[]>>> 
	               	sidServiceFieldMap = new HashMap<String,HashMap<String,HashMap<Integer,Object[]>>>(); 
	private static HashMap<String,HashMap<String,HashMap<String,Object>>> 
    				sidServiceColumnMap = new HashMap<String,HashMap<String,HashMap<String,Object>>>(); 
	private static HashMap<String, HashMap<String, Object[]>> ccbSidMap = new HashMap<String,HashMap<String,Object[]>>(); 

	//Maps for SID open order mapping
	private static HashMap<String,HashMap<String,Object[]>> sidOpenServiceExistMap = new HashMap<String,HashMap<String,Object[]>>(); 
	private static HashMap<String,HashMap<String,ArrayList<String[]>>> sidOpenTableColumnMap = new HashMap<String,HashMap<String,ArrayList<String[]>>>(); 
	private static HashMap<String,HashMap<String,ArrayList<String[]>>> sidOpenServiceCharMap = new HashMap<String,HashMap<String,ArrayList<String[]>>>(); 
	private static HashMap<String,String[]> sidOpenAccessName = new HashMap<String, String[]>();
	private static HashMap<String,HashMap<String,HashMap<Integer,Object[]>>> 
	               	sidOpenServiceFieldMap = new HashMap<String,HashMap<String,HashMap<Integer,Object[]>>>(); 
	private static HashMap<String,HashMap<String,HashMap<String,Object>>> 
    				sidOpenServiceColumnMap = new HashMap<String,HashMap<String,HashMap<String,Object>>>(); 

	private static HashMap<BigDecimal,ArrayList<HashMap<String,Object>>> permDef = new HashMap<BigDecimal,ArrayList<HashMap<String,Object>>>(); 
	private static HashMap<BigDecimal,ArrayList<HashMap<String,Object>>> permBew = new HashMap<BigDecimal,ArrayList<HashMap<String,Object>>>();
	private static BigDecimal lastDefVersion=null;
	private static HashMap<String,String> sourceSysRefDataMap = new HashMap<String, String>();
    final static HashMap<String , String> pkatHwMap = new HashMap<String , String>() {
    	private static final long serialVersionUID = 1L;
    	{ 
    		put("Bezeichnung","description");
    	    put("typ","type");
    	    put("RouterKlasse","RouterClass");
    	    put("istMietbareHardware","IsRentalHardware");
    	}
    }; 

	public static void loadAllRefData() throws Exception{
		try {
			refDataSource = DatabaseClientConfig.getSetting("db.RefDataSource");
		} catch (Exception e) {
			// ignore
		}
		GeneralDAO dao=null;
		try {
			dao = (GeneralDAO)BksStaticContainer.getBksDataAccessObject(refDataSource,"GeneralDAO");
			loadPrefetchRefData(dao);
			loadProviderCodes(dao);
			loadFunctions(dao);
			loadAccesses(dao);
			loadVariants(dao);
			loadVoiceOnlineMap(dao);
			loadBankNameMap(dao);
			loadCcbAawMap(dao);
			loadCcbSomMap(dao);
			loadMasterDataMap(dao);
			loadRefdataMap(dao);
			loadClassProviders(dao);
			loadGenericDataMap(dao);
			loadVodafoneClass(dao);
			loadBlockingCustIntention(dao);
			loadPkatHardware(dao);
			refdataInitialized = true;
		} finally {
			if (dao != null)
				dao.closeConnection();
		}	
	}
	
	public static void loadWebRefData() throws Exception{
		try {
			refDataSource = DatabaseClientConfig.getSetting("db.RefDataSource");
		} catch (Exception e) {
			// ignore
		}
		GeneralDAO dao=null;
		try {
			dao = (GeneralDAO)BksStaticContainer.getBksDataAccessObject(refDataSource,"GeneralDAO");
			loadPermissionDefs(dao);
			refdataInitialized = true;
		} finally {
			if (dao != null)
				dao.closeConnection();
		}	
	}
	

	private static void loadPermissionDefs(GeneralDAO dao) throws Exception{
		ArrayList<HashMap<String, Object>> dbDefs = dao.getRefData("databaseclient.PermissionDefinition");
		for(int i=0;i<dbDefs.size();i++){
			BigDecimal bewVersion = (BigDecimal) dbDefs.get(i).get("BEW_VERSION_NR");
			if (permDef.get(bewVersion)==null)
				permDef.put(bewVersion, new ArrayList<HashMap<String,Object>>());
			ArrayList<HashMap<String, Object>> list = permDef.get(bewVersion);
			list.add(dbDefs.get(i));
		}
		ArrayList<HashMap<String, Object>> dbBews = dao.getRefData("databaseclient.PermissionBew");
		lastDefVersion = (BigDecimal) dbBews.get(0).get("BEW_VERSION_NR");
		for(int i=0;i<dbBews.size();i++){
			BigDecimal bewVersion = (BigDecimal) dbBews.get(i).get("BEW_VERSION_NR");
			if (permBew.get(bewVersion)==null)
				permBew.put(bewVersion, new ArrayList<HashMap<String,Object>>());
			ArrayList<HashMap<String, Object>> list = permBew.get(bewVersion);
			list.add(dbBews.get(i));
		}
		ArrayList<HashMap<String, Object>> sourceSys = dao.getRefData("databaseclient.retrieveSourceSystemRefDetails");
		for(int i=0;i<sourceSys.size();i++){
			sourceSysRefDataMap.put((String)sourceSys.get(i).get("SOURCE_SYS_ID"), (String)sourceSys.get(i).get("DEFAULT_ROLE_ID"));
		}
	}

	public static HashMap<String, String> getSourceSysRefDataMap() {
		return sourceSysRefDataMap;
	}

	public static void setSourceSysRefDataMap(
			HashMap<String, String> sourceSysRefDataMap) {
		BksRefDataCacheHandler.sourceSysRefDataMap = sourceSysRefDataMap;
	}

	public static BigDecimal getLastDefVersion() {
		return lastDefVersion;
	}

	public static void setLastDefVersion(BigDecimal lastDefVersion) {
		BksRefDataCacheHandler.lastDefVersion = lastDefVersion;
	}

	public static HashMap<BigDecimal,ArrayList<HashMap<String, Object>>> getPermDef() {
		return permDef;
	}

	public static void setPermDef(HashMap<BigDecimal,ArrayList<HashMap<String, Object>>> permDef) {
		BksRefDataCacheHandler.permDef = permDef;
	}

	public static HashMap<BigDecimal,ArrayList<HashMap<String, Object>>> getPermBew() {
		return permBew;
	}

	public static void setPermBew(HashMap<BigDecimal,ArrayList<HashMap<String, Object>>> permBew) {
		BksRefDataCacheHandler.permBew = permBew;
	}

	private static void loadClassProviders(GeneralDAO dao) throws Exception{
		ArrayList<HashMap<String, Object>> classProviders = dao.getReferenceData("databaseclient.CrossRefData","CLASS_PROV");
		for (int i = 0;classProviders!= null && i < classProviders.size(); i++) {
			String key = classProviders.get(i).get("PRIMARY_VALUE").toString();
			classProviderMap.put(key, classProviders.get(i).get("SECONDARY_VALUE").toString());
		}
	}

	private static void loadVodafoneClass(GeneralDAO dao) throws Exception{
		ArrayList<HashMap<String, Object>> classVodafone = dao.getReferenceData("databaseclient.GeneralCodeData","CLDBSVPBKS");
		ArrayList<String> vodafoneClass = new ArrayList<String>();
		for (int i = 0;classVodafone!= null && i < classVodafone.size(); i++) {
			vodafoneClass.add(classVodafone.get(i).get("VALUE").toString());
		}
		generalData.put("CLDBSVPBKS", vodafoneClass);
	}

	private static void loadBlockingCustIntention(GeneralDAO dao) throws Exception{
		ArrayList<HashMap<String, Object>> custIntention = dao.getReferenceData("databaseclient.GeneralCodeData","BLOCK_INTE");
		ArrayList<String> vodafoneClass = new ArrayList<String>();
		for (int i = 0;custIntention!= null && i < custIntention.size(); i++) {
			vodafoneClass.add(custIntention.get(i).get("VALUE").toString());
		}
		generalData.put("BLOCK_INTE", vodafoneClass);
	}

	private static void loadRefdataMap(GeneralDAO dao) throws Exception {

		ArrayList<HashMap<String, Object>> theRefDataList = dao.getRefData("databaseclient.RefdataMap");
		for (int i = 0; theRefDataList!= null && i < theRefDataList.size(); i++) {
			String identifier = 
				theRefDataList.get(i).get("RDS_ID").toString();
			RefdataMapObject refdataMapRow = refdataMap.get(identifier);
			if (refdataMapRow == null){
				refdataMapRow = new RefdataMapObject();
				refdataMapRow.setRdsID(identifier);
				refdataMapRow.setRdsCategory(theRefDataList.get(i).get("RDS_CATEGORY").toString());
				refdataMapRow.setRdsOutput(theRefDataList.get(i).get("RDS_OUTPUT").toString());
				refdataMapRow.setAttributePath(theRefDataList.get(i).get("TARGET_ATTRIBUTE_PATH").toString());
				if (theRefDataList.get(i).get("TARGET_TYPE") != null)
					refdataMapRow.setTargetType(theRefDataList.get(i).get("TARGET_TYPE").toString());
				refdataMap.put(identifier, refdataMapRow);
			}
			HashMap<String,String> filter = refdataMapRow.getFilter();
			String key = theRefDataList.get(i).get("RDS_KEY").toString();
			String value = theRefDataList.get(i).get("SOURCE_VALUE_ID").toString();
			filter.put(key, value);
		}
	}

	private static void loadPrefetchRefData(GeneralDAO dao) throws Exception {
		ArrayList<HashMap<String, Object>> theRefDataList = dao.getRefData("databaseclient.PrefetchRefData");
		for (int i = 0; theRefDataList!= null && i < theRefDataList.size(); i++) {
			String sourceService = 
				theRefDataList.get(i).get("SOURCE_SERVICE_NAME").toString();
			String targetService = 
				theRefDataList.get(i).get("TARGET_SERVICE_NAME").toString();
			ArrayList<String> targetServList = nextServiceMap.get(sourceService);
			if (targetServList == null){
				targetServList = new ArrayList<String>();
			}
			if (!isAlreadyInList(targetServList,targetService)) {
				targetServList.add(targetService);
				nextServiceMap.put(sourceService, targetServList);
			}
			PrefetchReferenceObject prefetchRef = new PrefetchReferenceObject();
			prefetchRef.setSourceServiceName(sourceService);
			prefetchRef.setTargetServiceName(targetService);
			prefetchRef.setSourceDataTag(theRefDataList.get(i).get("SOURCE_DATA_TAG").toString());
			prefetchRef.setSourceDataObject(theRefDataList.get(i).get("SOURCE_OBJECT").toString());
			prefetchRef.setTargetDataTag(theRefDataList.get(i).get("TARGET_DATA_TAG").toString());
			prefetchRef.setTargetPackage(theRefDataList.get(i).get("TARGET_PACKAGE").toString());
			String key = sourceService+";"+targetService;
			ArrayList<PrefetchReferenceObject> theList = prefetchMap.get(key);
			if (theList == null){
				theList = new ArrayList<PrefetchReferenceObject>();
			}
			theList.add(prefetchRef);
			prefetchMap.put(key, theList);
		}
	}

	private static void loadProviderCodes(GeneralDAO dao) throws Exception {
		ArrayList<HashMap<String, Object>> providerCodes = dao.getReferenceData("databaseclient.CrossRefData","PROVID_CD");
		for (int i = 0; providerCodes!= null && i < providerCodes.size(); i++) {
			String key = providerCodes.get(i).get("PRIMARY_VALUE").toString();
			providerCode.put(key, providerCodes.get(i).get("SECONDARY_VALUE").toString());
		}
	}

	private static void loadFunctions(GeneralDAO dao) throws Exception {
		ArrayList<HashMap<String, Object>> functions = dao.getReferenceData("databaseclient.CrossRefData","SERV_FU_MA");
		for (int i = 0; functions!= null && i < functions.size(); i++) {
			String key = functions.get(i).get("PRIMARY_VALUE").toString();
			function.put(key, functions.get(i).get("SECONDARY_VALUE").toString());
		}
	}

	private static void loadAccesses(GeneralDAO dao) throws Exception {
		ArrayList<HashMap<String, Object>> accesses = dao.getReferenceData("databaseclient.CrossRefData","SERV_ACCES");
		for (int i = 0; accesses!= null && i < accesses.size(); i++) {
			String key = accesses.get(i).get("PRIMARY_VALUE").toString();
			access.put(key, accesses.get(i).get("SECONDARY_VALUE").toString());
		}
	}

	private static void loadVariants(GeneralDAO dao) throws Exception {
		ArrayList<HashMap<String, Object>> Variants = dao.getReferenceData("databaseclient.CrossRefData","SERV_VARIN");
		for (int i = 0; Variants!= null && i < Variants.size(); i++) {
			String key = Variants.get(i).get("PRIMARY_VALUE").toString();
			variant.put(key, Variants.get(i).get("SECONDARY_VALUE").toString());
		}
	}

	private static void loadVoiceOnlineMap(GeneralDAO dao) throws Exception {
		ArrayList<HashMap<String, Object>> voiceOnlineList = dao.getReferenceData("databaseclient.CrossRefData","VOICE_ONL");
		for (int i = 0; voiceOnlineList!= null && i < voiceOnlineList.size(); i++) {
			String key = voiceOnlineList.get(i).get("PRIMARY_VALUE").toString();
			ArrayList<String> theList = voiceOnlineMap.get(key);
			if (theList == null){
				theList = new ArrayList<String>();
			}
			theList.add(voiceOnlineList.get(i).get("SECONDARY_VALUE").toString());
			voiceOnlineMap.put(key, theList);
		}
	}

	private static void loadBankNameMap(GeneralDAO dao) throws Exception {
		ArrayList<HashMap<String, Object>> bankNames = dao.getReferenceData("databaseclient.GeneralCodeData","BANK_NAME");
		HashMap<String, String> nameMap = new HashMap<String, String>();
		for (int i = 0; bankNames!= null && i < bankNames.size(); i++) {
			String key = bankNames.get(i).get("VALUE").toString();
			String value = bankNames.get(i).get("DESCRIPTION").toString();
			nameMap.put(key, value);
		}

		ArrayList<HashMap<String, Object>> bankClearing = dao.getReferenceData("databaseclient.CrossRefData","CLEAR_BANK");
		for (int i = 0; bankClearing!= null && i < bankClearing.size(); i++) {
			String key = bankClearing.get(i).get("PRIMARY_VALUE").toString();
			String value = bankClearing.get(i).get("SECONDARY_VALUE").toString();	
			String bankName = nameMap.get(value);
			bankNameMap.put(key, bankName);
		}
	}

	private static void loadCcbAawMap(GeneralDAO dao)  throws Exception {
		ArrayList<HashMap<String, Object>> ccbAawList = dao.getReferenceData("databaseclient.CrossRefData","CCB_AAW_MP");
		for (int i = 0; ccbAawList!= null && i < ccbAawList.size(); i++) {
			String key = ccbAawList.get(i).get("PRIMARY_VALUE").toString();
			ccbAawMap.put(key, ccbAawList.get(i).get("SECONDARY_VALUE").toString());
		}
	}
	private static boolean isAlreadyInList(ArrayList<String> targetServList, String targetService) {
		for (int i=0;i<targetServList.size();i++){
			if (targetServList.get(i).equals(targetService))
				return true;
		}
		return false;
	}

	private static void loadMasterDataMap(GeneralDAO dao) throws Exception {
		ArrayList<HashMap<String, Object>> theRefDataList = dao.getRefData("databaseclient.MasterDataMap");
		for (int i = 0; theRefDataList!= null && i < theRefDataList.size(); i++) {
			String key = 
				theRefDataList.get(i).get("VALUE_ID").toString();

			MasterMapObject masterData = new MasterMapObject();
			masterData.setIdentifier(key);
			masterData.setTargetAttributePath(theRefDataList.get(i).get("TARGET_ATTRIBUTE_PATH").toString());
			masterDataMap.put(key, masterData);
		}
	}

	private static void loadCcbSomMap(GeneralDAO dao) throws Exception {
		ArrayList<HashMap<String, Object>> refDataList = dao.getRefData("databaseclient.CcbSomMap");
		for (int i = 0; refDataList!= null && i < refDataList.size(); i++) {
			String tableName = refDataList.get(i).get("TABLE_NAME").toString();
			String targetObject = (String)refDataList.get(i).get("TARGET_OBJECT");
			String column = refDataList.get(i).get("COLUMN_NAME").toString();
			String targetPath = (String)refDataList.get(i).get("TARGET_ATTRIBUTE_PATH");
			String sidPath = (String)refDataList.get(i).get("SID_ATTRIBUTE_PATH");
			String applyMethod = (String)refDataList.get(i).get("APPLY_METHOD");
			String defaultValue = (String)refDataList.get(i).get("DEFAULT_VALUE");
			BigDecimal decSrcFld = (BigDecimal)refDataList.get(i).get("SOURCE_FIELD_NUMBER");
			String fieldNumberString = (decSrcFld==null)?null:decSrcFld.toString();
			String introVersion = (String)refDataList.get(i).get("INTRODUCTION_VERSION");
			if (targetPath!= null){
				HashMap<String,Object[]> colMap = ccbSomMap.get(targetObject+";"+tableName);
				if (colMap == null){
					colMap=new HashMap<String, Object[]>();
					ccbSomMap.put(targetObject+";"+tableName,colMap);
				} 

				if (column.equals("-"))
					colMap.put(column,new String[] {targetPath,defaultValue,introVersion});
				else {
					if (colMap.get(column) == null){
						colMap.put(column,new String[] {targetPath,applyMethod,introVersion});
					}else{
						ArrayList<Object> currentList = new ArrayList<Object>();
						for (Object value:colMap.get(column))
							currentList.add((String)value);
						currentList.add(targetPath);
						currentList.add(applyMethod);
						currentList.add(introVersion);
						colMap.put(column,currentList.toArray());
					}
				}
			} else {
				HashMap<String,Object[]> colMap = ccbSidMap.get(targetObject+";"+tableName);
				if (colMap == null){
					colMap=new HashMap<String, Object[]>();
					ccbSidMap.put(targetObject+";"+tableName,colMap);
				} 

				if (column.equals("-"))
					colMap.put(column,new String[] {sidPath,defaultValue});
				else {
					if (colMap.get(column) == null){
						colMap.put(column,new String[] {sidPath,applyMethod,defaultValue,fieldNumberString,introVersion});
					}else{				
						ArrayList<Object> currentList = new ArrayList<Object>();
						for (Object value:colMap.get(column))
							currentList.add((String)value);
						currentList.add(sidPath);
						currentList.add(applyMethod);
						currentList.add(defaultValue);
						currentList.add(fieldNumberString);
						currentList.add(introVersion);
						colMap.put(column,currentList.toArray());
					}
				}
			}
		}
	}
	public static HashMap<String, HashMap<String, Object[]>> getCcbSidMap() {
		return ccbSidMap;
	}


	public static void setCcbSidMap(
			HashMap<String, HashMap<String, Object[]>> ccbSidMap) {
		BksRefDataCacheHandler.ccbSidMap = ccbSidMap;
	}


	private static void loadGenericDataMap(GeneralDAO dao) throws Exception {
		ArrayList<HashMap<String, Object>> refDataList = dao.getRefData("databaseclient.GenericDataMap");
		for (int i = 0; refDataList!= null && i < refDataList.size(); i++) {
			String targetPath = (String)refDataList.get(i).get("TARGET_ATTRIBUTE_PATH");
			String somPath = (String)refDataList.get(i).get("SOM_ATTRIBUTE_PATH");
			String sidPath = (String)refDataList.get(i).get("SID_ATTRIBUTE_PATH");
			populateGenericMap(refDataList.get(i),targetPath,somPath,sidPath);
		}
	}

	private static void populateGenericMap(HashMap<String,Object> genericRow, String targetPath, String somPath, String sidPath) {
		String productCode = genericRow.get("SOURCE_PRODUCT_CODE").toString();
		String serviceCharCode = (String)genericRow.get("SOURCE_SERVICE_CHAR_CODE");
		String serviceCode = genericRow.get("SOURCE_SERVICE_CODE").toString();
		String defaultValue = (String)genericRow.get("DEFAULT_VALUE");
		String sourceColumn = (String)genericRow.get("SOURCE_COLUMN");
		BigDecimal decSrcFld = (BigDecimal)genericRow.get("SOURCE_FIELD_NUMBER");
		Integer sourceFieldNumber = (decSrcFld!=null)?decSrcFld.intValue():null;
		String rdsId = (String)genericRow.get("RDS_ID");
		String valueType = (String)genericRow.get("VALUE_TYPE");
		String conversionMethod = (String)genericRow.get("APPLY_METHOD");
		String parentServiceCode = (String)genericRow.get("PARENT_SERVICE_CODE");
		String prefix = (String)genericRow.get("PREFIX");
		String dependentCharCode = (String)genericRow.get("DEPENDENT_CHAR_CODE");
		String mainBundleAccess = (String)genericRow.get("MAIN_BUNDLE_ACCESS");
		String openOrderInd = (String)genericRow.get("OPEN_ORDER_INDICATOR");
		String fixedValue = (String)genericRow.get("FIXED_VALUE");
		String introVersion = (String)genericRow.get("INTRODUCTION_VERSION");

		if (valueType.equals("SERVICE")) {
			if (targetPath!=null)
				populateServiceExistMap(serviceExistMap,productCode,serviceCode,
						parentServiceCode,targetPath,defaultValue,conversionMethod,introVersion);
			if (somPath!=null)
				populateServiceExistMap(somServiceExistMap,productCode,serviceCode,
						parentServiceCode,somPath,defaultValue,conversionMethod,introVersion);
			if (sidPath!=null && openOrderInd == null)
				populateServiceExistMap(sidServiceExistMap,productCode,serviceCode,
						parentServiceCode,sidPath,defaultValue,conversionMethod,introVersion);
			else if (sidPath!=null && openOrderInd.equals("Y"))
				populateServiceExistMap(sidOpenServiceExistMap,productCode,serviceCode,
						parentServiceCode,sidPath,defaultValue,conversionMethod,introVersion);
				
		} else if (valueType.equals("CONTRACT")) {
			if (targetPath!=null)
				populateTableColumnMap(tableColumnMap,productCode,serviceCode,parentServiceCode,
						targetPath,sourceColumn,conversionMethod,defaultValue,prefix,mainBundleAccess,introVersion);
			if (somPath!=null)
				populateTableColumnMap(somTableColumnMap,productCode,serviceCode,parentServiceCode,
						somPath,sourceColumn,conversionMethod,defaultValue,prefix,mainBundleAccess,introVersion);
			if (sidPath!=null && openOrderInd == null)
				populateTableColumnMap(sidTableColumnMap,productCode,serviceCode,parentServiceCode,
						sidPath,sourceColumn,conversionMethod,defaultValue,prefix,mainBundleAccess,introVersion);
			else if (sidPath!=null && openOrderInd.equals("Y"))
				populateTableColumnMap(sidOpenTableColumnMap,productCode,serviceCode,parentServiceCode,
						sidPath,sourceColumn,conversionMethod,defaultValue,prefix,mainBundleAccess,introVersion);
		} else if (valueType.equals("CONFIGURED_VALUE")){
			String fieldnum = (sourceFieldNumber==null)?null:sourceFieldNumber.toString();
			if (targetPath!=null)
				populateCharMap(serviceCharMap,null,serviceCode,serviceCharCode,
						parentServiceCode,targetPath,rdsId,conversionMethod,
						fieldnum,dependentCharCode,defaultValue,fixedValue,introVersion);
			if (somPath!=null)
				populateCharMap(somServiceCharMap,null,serviceCode,serviceCharCode,
						parentServiceCode,somPath,rdsId,conversionMethod,
						fieldnum,dependentCharCode,defaultValue,fixedValue,introVersion);
			if (sidPath!=null && openOrderInd == null)
				populateCharMap(sidServiceCharMap,productCode,serviceCode,serviceCharCode,
						parentServiceCode,sidPath,rdsId,conversionMethod,
						fieldnum,dependentCharCode,defaultValue,fixedValue,introVersion);
			else if (sidPath!=null && openOrderInd.equals("Y"))
				populateCharMap(sidOpenServiceCharMap,productCode,serviceCode,serviceCharCode,
					parentServiceCode,sidPath,rdsId,conversionMethod,
					fieldnum,dependentCharCode,defaultValue,fixedValue,introVersion);
		} else if (valueType.equals("ACCESS_NUMBER")){
			if (targetPath!=null)
				populateFieldMap(serviceFieldMap,serviceCode,serviceCharCode,
						sourceFieldNumber,parentServiceCode,targetPath,prefix,conversionMethod,defaultValue,dependentCharCode,fixedValue);
			if (somPath!=null)
				populateFieldMap(somServiceFieldMap,serviceCode,serviceCharCode,
						sourceFieldNumber,parentServiceCode,somPath,null,conversionMethod,defaultValue,dependentCharCode,fixedValue);
			if (sidPath!=null && openOrderInd == null){
				populateFieldMap(sidServiceFieldMap,serviceCode,serviceCharCode,
						sourceFieldNumber,parentServiceCode,sidPath,null,conversionMethod,defaultValue,dependentCharCode,fixedValue);
				if (fixedValue != null) {
					sidAccessName.put(serviceCode + ";" + serviceCharCode,
							new String[] { sidPath, fixedValue });
				}
			} else if (sidPath!=null && openOrderInd.equals("Y")){
				populateFieldMap(sidOpenServiceFieldMap,serviceCode,serviceCharCode,
						sourceFieldNumber,parentServiceCode,sidPath,null,conversionMethod,defaultValue,dependentCharCode,fixedValue);
				if (fixedValue != null) {
					sidOpenAccessName.put(serviceCode + ";" + serviceCharCode,
							new String[] { sidPath, fixedValue });
				}
			}
		} else if (sourceColumn != null){
			if (targetPath!=null)
				populateColumnMap(serviceColumnMap,productCode,serviceCode,serviceCharCode,
						sourceFieldNumber,sourceColumn,targetPath,conversionMethod,dependentCharCode);
			if (somPath!=null)
				populateColumnMap(somServiceColumnMap,productCode,serviceCode,serviceCharCode,
						sourceFieldNumber,sourceColumn,somPath,conversionMethod,dependentCharCode);
			if (sidPath!=null && openOrderInd == null)
				populateColumnMap(sidServiceColumnMap,productCode,serviceCode,serviceCharCode,
						sourceFieldNumber,sourceColumn,sidPath,conversionMethod,dependentCharCode);
			else if (sidPath!=null && openOrderInd.equals("Y"))
				populateColumnMap(sidOpenServiceColumnMap,productCode,serviceCode,serviceCharCode,
						sourceFieldNumber,sourceColumn,sidPath,conversionMethod,dependentCharCode);
		}
	}


	public static HashMap<String, HashMap<String, Object[]>> getSidOpenServiceExistMap() {
		return sidOpenServiceExistMap;
	}


	public static void setSidOpenServiceExistMap(
			HashMap<String, HashMap<String, Object[]>> sidOpenServiceExistMap) {
		BksRefDataCacheHandler.sidOpenServiceExistMap = sidOpenServiceExistMap;
	}


	public static HashMap<String, HashMap<String, ArrayList<String[]>>> getSidOpenTableColumnMap() {
		return sidOpenTableColumnMap;
	}


	public static void setSidOpenTableColumnMap(
			HashMap<String, HashMap<String, ArrayList<String[]>>> sidOpenTableColumnMap) {
		BksRefDataCacheHandler.sidOpenTableColumnMap = sidOpenTableColumnMap;
	}


	public static HashMap<String, HashMap<String, ArrayList<String[]>>> getSidOpenServiceCharMap() {
		return sidOpenServiceCharMap;
	}


	public static void setSidOpenServiceCharMap(
			HashMap<String, HashMap<String, ArrayList<String[]>>> sidOpenServiceCharMap) {
		BksRefDataCacheHandler.sidOpenServiceCharMap = sidOpenServiceCharMap;
	}


	public static HashMap<String, String[]> getSidOpenAccessName() {
		return sidOpenAccessName;
	}


	public static void setSidOpenAccessName(
			HashMap<String, String[]> sidOpenAccessName) {
		BksRefDataCacheHandler.sidOpenAccessName = sidOpenAccessName;
	}


	public static HashMap<String, HashMap<String, HashMap<Integer, Object[]>>> getSidOpenServiceFieldMap() {
		return sidOpenServiceFieldMap;
	}


	public static void setSidOpenServiceFieldMap(
			HashMap<String, HashMap<String, HashMap<Integer, Object[]>>> sidOpenServiceFieldMap) {
		BksRefDataCacheHandler.sidOpenServiceFieldMap = sidOpenServiceFieldMap;
	}


	public static HashMap<String, HashMap<String, HashMap<String, Object>>> getSidOpenServiceColumnMap() {
		return sidOpenServiceColumnMap;
	}


	public static void setSidOpenServiceColumnMap(
			HashMap<String, HashMap<String, HashMap<String, Object>>> sidOpenServiceColumnMap) {
		BksRefDataCacheHandler.sidOpenServiceColumnMap = sidOpenServiceColumnMap;
	}


	public static HashMap<String, String[]> getSidAccessName() {
		return sidAccessName;
	}


	public static void setSidAccessName(HashMap<String, String[]> sidAccessName) {
		BksRefDataCacheHandler.sidAccessName = sidAccessName;
	}


	public static HashMap<String, HashMap<String, Object[]>> getSidServiceExistMap() {
		return sidServiceExistMap;
	}


	public static void setSidServiceExistMap(
			HashMap<String, HashMap<String, Object[]>> sidServiceExistMap) {
		BksRefDataCacheHandler.sidServiceExistMap = sidServiceExistMap;
	}


	public static HashMap<String, HashMap<String, ArrayList<String[]>>> getSidTableColumnMap() {
		return sidTableColumnMap;
	}


	public static void setSidTableColumnMap(
			HashMap<String, HashMap<String, ArrayList<String[]>>> sidTableColumnMap) {
		BksRefDataCacheHandler.sidTableColumnMap = sidTableColumnMap;
	}


	public static HashMap<String, HashMap<String, ArrayList<String[]>>> getSidServiceCharMap() {
		return sidServiceCharMap;
	}


	public static void setSidServiceCharMap(
			HashMap<String, HashMap<String, ArrayList<String[]>>> sidServiceCharMap) {
		BksRefDataCacheHandler.sidServiceCharMap = sidServiceCharMap;
	}


	public static HashMap<String, HashMap<String, HashMap<Integer, Object[]>>> getSidServiceFieldMap() {
		return sidServiceFieldMap;
	}


	public static void setSidServiceFieldMap(
			HashMap<String, HashMap<String, HashMap<Integer, Object[]>>> sidServiceFieldMap) {
		BksRefDataCacheHandler.sidServiceFieldMap = sidServiceFieldMap;
	}


	public static HashMap<String, HashMap<String, HashMap<String, Object>>> getSidServiceColumnMap() {
		return sidServiceColumnMap;
	}


	public static void setSidServiceColumnMap(
			HashMap<String, HashMap<String, HashMap<String, Object>>> sidServiceColumnMap) {
		BksRefDataCacheHandler.sidServiceColumnMap = sidServiceColumnMap;
	}


	private static void populateColumnMap(HashMap<String, HashMap<String, HashMap<String, Object>>> columnMap,
			String productCode,	String serviceCode, String serviceCharCode, Integer sourceFieldNumber, String sourceColumn,
			 String path,String conversionMethod, String dependentCharCode) {
		HashMap<String,HashMap<String,Object>> columnCharMap = columnMap.get(productCode+";"+serviceCode);
		if (columnCharMap == null)
			columnCharMap = new HashMap<String, HashMap<String,Object>>();
		columnMap.put(productCode+";"+serviceCode,columnCharMap);
		HashMap<String,Object> columnPathMap = columnCharMap.get(serviceCharCode);
		if (columnPathMap == null)
			columnPathMap = new HashMap<String, Object>();
		String sourceFieldPosition=null;
		if (sourceFieldNumber != null)
			sourceFieldPosition=sourceFieldNumber.toString();
		if (columnPathMap.get(sourceColumn) != null) {
			Object targetValue = columnPathMap.get(sourceColumn);
			if (targetValue instanceof ArrayList<?>)
				((ArrayList<String[]>)targetValue).add(new String[] {path,conversionMethod,sourceFieldPosition,dependentCharCode});
			else{
				ArrayList<String[]> list = new ArrayList<String[]>();
				list.add((String[])targetValue);
				list.add(new String[] {path,conversionMethod,sourceFieldPosition,dependentCharCode});
				columnPathMap.put(sourceColumn,list);
			}
		} else
			columnPathMap.put(sourceColumn,new String[] {path,conversionMethod,sourceFieldPosition,dependentCharCode});
		columnCharMap.put(serviceCharCode,columnPathMap);
	}


	private static void populateFieldMap(HashMap<String, HashMap<String, HashMap<Integer, Object[]>>> fieldMap,
			String serviceCode, String serviceCharCode,Integer sourceFieldNumber, String parentServiceCode, String path, 
			String prefix, String conversionMethod, String defaultValue, String dependentCharCode, String fixedValue) {
		HashMap<String,HashMap<Integer,Object[]>> fieldCharMap = null;
		if (parentServiceCode != null)
			fieldCharMap = fieldMap.get(serviceCode+";"+parentServiceCode);
		else
			fieldCharMap = fieldMap.get(serviceCode);
		if (fieldCharMap == null)
			fieldCharMap = new HashMap<String, HashMap<Integer,Object[]>>();
		if (parentServiceCode != null)
			fieldMap.put(serviceCode+";"+parentServiceCode,fieldCharMap);
		else
			fieldMap.put(serviceCode,fieldCharMap);
		HashMap<Integer,Object[]> fieldPathMap = fieldCharMap.get(serviceCharCode);
		if (fieldPathMap == null)
			fieldPathMap = new HashMap<Integer, Object[]>();

		ArrayList<Object> currentList = new ArrayList<Object>();
		Object[] paths = fieldPathMap.get(sourceFieldNumber);
		for (int i = 0;paths!=null && i < paths.length; i++) {
			currentList.add(paths[i]);
		}
		currentList.add(new String[] {path,prefix,conversionMethod,defaultValue,dependentCharCode,fixedValue});
			
		fieldPathMap.put(sourceFieldNumber,currentList.toArray());
		fieldCharMap.put(serviceCharCode,fieldPathMap);
	}


	private static void populateCharMap(HashMap<String,HashMap<String,ArrayList<String[]>>> serviceCharMap,
			String productCode, String serviceCode, String serviceCharCode, String parentServiceCode,
			String path,String rdsId, String conversionMethod, String sourceFieldNumber, 
			String dependentCharCode, String defaultValue, String fixedValue, String introVersion) {
		HashMap<String,ArrayList<String[]>> charPathMap = null;
		String searchKey = serviceCode;
		if (productCode != null)
			searchKey = productCode+";"+serviceCode;
		if (parentServiceCode != null)
			charPathMap = serviceCharMap.get(searchKey+";"+parentServiceCode);
		else
			charPathMap = serviceCharMap.get(searchKey);
		if (charPathMap == null)
			charPathMap = new HashMap<String, ArrayList<String[]>>();
		ArrayList<String[]> value = charPathMap.get(serviceCharCode);
		if (value==null)
			value = new ArrayList<String[]>();
		value.add(new String[] {path,rdsId,conversionMethod,sourceFieldNumber,dependentCharCode,defaultValue,fixedValue,introVersion});
		charPathMap.put(serviceCharCode, value);
		if (parentServiceCode != null)
			serviceCharMap.put(searchKey+";"+parentServiceCode,charPathMap);
		else
			serviceCharMap.put(searchKey,charPathMap);
	}


	private static void populateTableColumnMap(HashMap<String,HashMap<String,ArrayList<String[]>>> tableColumnMap,
			String productCode, String serviceCode, String parentServiceCode, String path,String sourceColumn, 
			String conversionMethod, String defaultValue, String prefix, String mainBundleAccess, String introVersion) {
		
		HashMap<String,ArrayList<String[]>> servicePathMap = null;
		if (parentServiceCode == null)
			servicePathMap = tableColumnMap.get(productCode+";"+serviceCode);
		else
			servicePathMap = tableColumnMap.get(productCode+";"+serviceCode+";"+parentServiceCode);
		if (servicePathMap == null){
			servicePathMap = new HashMap<String, ArrayList<String[]>>();
		}
		ArrayList<String[]> mapValue = servicePathMap.get(sourceColumn);
		if (mapValue==null)
			mapValue = new ArrayList<String[]>();
		if (!sourceColumn.equals("-")){
			mapValue.add(new String[] {path,conversionMethod,prefix,mainBundleAccess,introVersion,defaultValue});
		}else{
			mapValue.add(new String[]  {path,defaultValue});
		}
		servicePathMap.put(sourceColumn, mapValue);
		if (parentServiceCode == null)
			tableColumnMap.put(productCode+";"+serviceCode,servicePathMap);
		else
			tableColumnMap.put(productCode+";"+serviceCode+";"+parentServiceCode,servicePathMap);
	}


	private static void populateServiceExistMap(HashMap<String, HashMap<String, Object[]>> servExistMap,
			String productCode, String serviceCode, String parentServiceCode, String path, String defaultValue,
			String conversionMethod, String introVersion) {
		HashMap<String,Object[]> servicePathMap = servExistMap.get(productCode);
		if (servicePathMap == null)
			servicePathMap = new HashMap<String, Object[]>();
		String key = null;
		if(parentServiceCode==null)
			key = serviceCode;
		else
			key = serviceCode+";"+parentServiceCode;
		if (servicePathMap.containsKey(key)){
			ArrayList<Object> currentList = new ArrayList<Object>();
			for (Object value:servicePathMap.get(key))
				currentList.add((String)value);
			currentList.add(path);
			currentList.add(defaultValue);
			currentList.add(conversionMethod);
			currentList.add(introVersion);
			servicePathMap.put(key,currentList.toArray());

		} else
			servicePathMap.put(key, new String[] {path,defaultValue,conversionMethod,introVersion});
		servExistMap.put(productCode,servicePathMap);
	}

	private static void loadPkatHardware(GeneralDAO dao){
		HashMap<String,String>  articleFilter = new HashMap<String, String>();
		ApplicationContext ac = BksStaticContainer.getAc();
		ReferenzdatenServiceFacade rdsService = 
				(ReferenzdatenServiceFacade) ac.getBean("referenzdatenServiceFacade");
		if (rdsService == null)
			throw new MCFException("Could not get Bean referenzdatenServiceFacade from application context.");
		ReferenzdatenContainer refContainer = 
				rdsService.getReferenzdaten("Hardwareartikel", articleFilter, null);

		String HwXml="<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>";
		HwXml+="<AllHardwareItems>";
		List<Referenzdaten> hwList = refContainer.getReferenzdaten();
		Date today = new Date();
		for (int i = 0; i < hwList.size(); i++) {
			Referenzdaten hwItem = hwList.get(i);
			Date vf = hwItem.getValidFrom().getTime();
			Date vu = hwItem.getValidUntil().getTime();
			
			if (vf.getTime()>today.getTime() || 
				vu.getTime()<today.getTime())
				continue;
			String articleNo = hwItem.getValue("Artikelnummer");
			if (articleNo==null)
				continue;
			HwXml+="<hardwareItem articleNumber=\""+articleNo+"\">";
			Set<String> refkeys = pkatHwMap.keySet();
			Iterator<String> refkeyiter = refkeys.iterator();
			while (refkeyiter.hasNext()){
				String key = refkeyiter.next();
				if (hwItem.getValue(key)==null)
					continue;
				HwXml+="<"+pkatHwMap.get(key)+">";
				String mappedValue = hwItem.getValue(key).replaceAll("&", "&amp;");
				HwXml+=mappedValue;
				HwXml+="</"+pkatHwMap.get(key)+">";
			}
			HwXml+="</hardwareItem>";
		}
		HwXml+="</AllHardwareItems>";
		
		dao.insertPkatLog("HARDWARE", "NEW", new Timestamp(System.currentTimeMillis()), 
				new Timestamp(System.currentTimeMillis()), HwXml, new BigDecimal(1));
	}

	public static HashMap<String, HashMap<String, Object[]>> getServiceExistMap() {
		return serviceExistMap;
	}

	public static void setServiceExistMap(
			HashMap<String, HashMap<String, Object[]>> serviceExistMap) {
		BksRefDataCacheHandler.serviceExistMap = serviceExistMap;
	}

	public static HashMap<String, HashMap<String, ArrayList<String[]>>> getTableColumnMap() {
		return tableColumnMap;
	}


	public static void setTableColumnMap(
			HashMap<String, HashMap<String, ArrayList<String[]>>> tableColumnMap) {
		BksRefDataCacheHandler.tableColumnMap = tableColumnMap;
	}

	public static HashMap<String, HashMap<String,  ArrayList<String[]>>> getServiceCharMap() {
		return serviceCharMap;
	}

	public static void setServiceCharMap(
			HashMap<String, HashMap<String,  ArrayList<String[]>>> serviceCharMap) {
		BksRefDataCacheHandler.serviceCharMap = serviceCharMap;
	}

	public static HashMap<String, HashMap<String, HashMap<Integer, Object[]>>> getServiceFieldMap() {
		return serviceFieldMap;
	}

	public static void setServiceFieldMap(
			HashMap<String, HashMap<String, HashMap<Integer, Object[]>>> serviceFieldMap) {
		BksRefDataCacheHandler.serviceFieldMap = serviceFieldMap;
	}

	public static HashMap<String, HashMap<String, HashMap<String, Object>>> getServiceColumnMap() {
		return serviceColumnMap;
	}

	public static void setServiceColumnMap(
			HashMap<String, HashMap<String, HashMap<String, Object>>> serviceColumnMap) {
		BksRefDataCacheHandler.serviceColumnMap = serviceColumnMap;
	}

	public static HashMap<String, HashMap<String, Object[]>> getSomServiceExistMap() {
		return somServiceExistMap;
	}


	public static void setSomServiceExistMap(
			HashMap<String, HashMap<String, Object[]>> somServiceExistMap) {
		BksRefDataCacheHandler.somServiceExistMap = somServiceExistMap;
	}


	public static HashMap<String, HashMap<String, ArrayList<String[]>>> getSomTableColumnMap() {
		return somTableColumnMap;
	}


	public static void setSomTableColumnMap(
			HashMap<String, HashMap<String, ArrayList<String[]>>> somTableColumnMap) {
		BksRefDataCacheHandler.somTableColumnMap = somTableColumnMap;
	}


	public static HashMap<String, HashMap<String,  ArrayList<String[]>>> getSomServiceCharMap() {
		return somServiceCharMap;
	}


	public static void setSomServiceCharMap(
			HashMap<String, HashMap<String,  ArrayList<String[]>>> somServiceCharMap) {
		BksRefDataCacheHandler.somServiceCharMap = somServiceCharMap;
	}


	public static HashMap<String, HashMap<String, HashMap<Integer, Object[]>>> getSomServiceFieldMap() {
		return somServiceFieldMap;
	}


	public static void setSomServiceFieldMap(
			HashMap<String, HashMap<String, HashMap<Integer, Object[]>>> somServiceFieldMap) {
		BksRefDataCacheHandler.somServiceFieldMap = somServiceFieldMap;
	}


	public static HashMap<String, HashMap<String, HashMap<String, Object>>> getSomServiceColumnMap() {
		return somServiceColumnMap;
	}


	public static void setSomServiceColumnMap(
			HashMap<String, HashMap<String, HashMap<String, Object>>> somServiceColumnMap) {
		BksRefDataCacheHandler.somServiceColumnMap = somServiceColumnMap;
	}


	public static HashMap<String, MasterMapObject> getMasterDataMap() {
		return masterDataMap;
	}

	public static void setMasterDataMap(
			HashMap<String, MasterMapObject> masterDataMap) {
		BksRefDataCacheHandler.masterDataMap = masterDataMap;
	}

	public static HashMap<String, ArrayList<String>> getNextServiceMap() {
		return nextServiceMap;
	}

	public static void setNextServiceMap(
			HashMap<String, ArrayList<String>> nextServiceMap) {
		BksRefDataCacheHandler.nextServiceMap = nextServiceMap;
	}

	public static HashMap<String, ArrayList<PrefetchReferenceObject>> getPrefetchMap() {
		return prefetchMap;
	}

	public static void setPrefetchMap(
			HashMap<String, ArrayList<PrefetchReferenceObject>> prefetchMap) {
		BksRefDataCacheHandler.prefetchMap = prefetchMap;
	}

	public static HashMap<String, String> getProviderCode() {
		return providerCode;
	}

	public static void setProviderCode(HashMap<String, String> providerCode) {
		BksRefDataCacheHandler.providerCode = providerCode;
	}

	public static HashMap<String, String> getFunction() {
		return function;
	}

	public static void setFunction(HashMap<String, String> function) {
		BksRefDataCacheHandler.function = function;
	}

	public static HashMap<String, String> getAccess() {
		return access;
	}

	public static void setAccess(HashMap<String, String> access) {
		BksRefDataCacheHandler.access = access;
	}

	public static HashMap<String, String> getVariant() {
		return variant;
	}

	public static void setVariant(HashMap<String, String> variant) {
		BksRefDataCacheHandler.variant = variant;
	}

	public static HashMap<String, RefdataMapObject> getRefdataMap() {
		return refdataMap;
	}

	public static void setRefdataMap(HashMap<String, RefdataMapObject> refdataMap) {
		BksRefDataCacheHandler.refdataMap = refdataMap;
	}

	public static HashMap<String, ArrayList<String>> getVoiceOnlineMap() {
		return voiceOnlineMap;
	}

	public static void setVoiceOnlineMap(
			HashMap<String, ArrayList<String>> voiceOnlineMap) {
		BksRefDataCacheHandler.voiceOnlineMap = voiceOnlineMap;
	}

	public static HashMap<String,String> getBankNameMap() {
		return bankNameMap;
	}

	public static void setBankNameMap(
			HashMap<String,String> bankNameMap) {
		BksRefDataCacheHandler.bankNameMap = bankNameMap;
	}

	public static boolean isRefdataInitialized() {
		return refdataInitialized;
	}

	public static void setRefdataInitialized(boolean redataInitialized) {
		BksRefDataCacheHandler.refdataInitialized = redataInitialized;
	}


	public static HashMap<String, String> getCcbAawMap() {
		return ccbAawMap;
	}


	public static void setCcbAawMap(HashMap<String, String> ccmAawMap) {
		BksRefDataCacheHandler.ccbAawMap = ccmAawMap;
	}


	public static HashMap<String, HashMap<String, Object[]>> getCcbSomMap() {
		return ccbSomMap;
	}


	public static void setCcbSomMap(
			HashMap<String, HashMap<String, Object[]>> ccbSomMap) {
		BksRefDataCacheHandler.ccbSomMap = ccbSomMap;
	}


	public static HashMap<String, String> getClassProviderMap() {
		return classProviderMap;
	}


	public static void setClassProviderMap(HashMap<String, String> classProviderMap) {
		BksRefDataCacheHandler.classProviderMap = classProviderMap;
	}


	public static HashMap<String, ArrayList<String>> getGeneralData() {
		return generalData;
	}


	public static void setGeneralData(HashMap<String, ArrayList<String>> generalData) {
		BksRefDataCacheHandler.generalData = generalData;
	}
}
