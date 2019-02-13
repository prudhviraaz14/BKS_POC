/*
    $Header:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/PrefetchProductBundlesHandler.java-arc   1.35   Jun 11 2015 08:41:32   makuier  $

    $Log:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/PrefetchProductBundlesHandler.java-arc  $
 * 
 *    Rev 1.35   Jun 11 2015 08:41:32   makuier
 * Populate OneNetId
 * 
 *    Rev 1.34   Jun 27 2013 13:02:58   makuier
 * Added support for VI020
 * 
 *    Rev 1.33   Oct 19 2012 14:55:58   makuier
 * search by Site id added.
 * 
 *    Rev 1.32   Jul 24 2012 13:54:04   makuier
 * Populate dial-in account name.
 * 
 *    Rev 1.31   Apr 30 2012 13:56:32   makuier
 * Set site type for PBX product
 * 
 *    Rev 1.30   Mar 29 2012 15:10:30   makuier
 * last chunk populated
 * 
 *    Rev 1.29   Mar 22 2012 12:38:06   makuier
 * Set last chunk correctly.
 * 
 *    Rev 1.28   Mar 07 2012 11:52:42   makuier
 * get one group id from VI086.
 * 
 *    Rev 1.27   Feb 24 2012 13:43:06   makuier
 * Populate One group id
 * 
 *    Rev 1.26   Feb 20 2012 16:06:36   makuier
 * Return chunk information in output only if chunking is requested in input
 * 
 *    Rev 1.25   Feb 17 2012 15:35:44   makuier
 * Default the start loop and endloop correctly.
 * 
 *    Rev 1.24   Feb 13 2012 17:02:54   makuier
 * Introduced chunking
 * 
 *    Rev 1.23   Feb 06 2012 17:39:12   makuier
 * Use characteristic to fetch the one group.
 * 
 *    Rev 1.22   Oct 21 2011 15:38:48   makuier
 * superCustomerTrackingId added to the input.
 * 
 *    Rev 1.21   Oct 10 2011 12:54:34   makuier
 * Do not search for configured values in SEATs.
 * 
 *    Rev 1.20   Sep 23 2011 10:11:42   makuier
 * Print bundle node if no future item exists
 * 
 *    Rev 1.19   Aug 10 2011 17:15:50   makuier
 * bug fix.
 * 
 *    Rev 1.18   Jun 24 2011 15:47:22   makuier
 * SIP trunk added.
 * 
 *    Rev 1.17   May 12 2011 10:02:40   makuier
 * print both old and new bundle only when bundle has a site.
 * 
 *    Rev 1.16   Dec 03 2010 17:50:46   makuier
 * Adapted to SOM XSD changes
 * 
 *    Rev 1.15   Nov 29 2010 15:54:56   makuier
 * Adapted to PSM changes to SALES_PACKET table.
 * 
 *    Rev 1.14   Nov 19 2010 16:15:16   makuier
 * Do not try line creation when input is One Group id 
 * 
 *    Rev 1.13   Sep 06 2010 11:07:44   makuier
 * Handle future bundle items.
 * 
 *    Rev 1.12   Aug 20 2010 16:47:50   makuier
 * do not fail if location address is not populated.
 * 
 *    Rev 1.11   Aug 11 2010 18:29:58   makuier
 * populate AO flag.
 * 
 *    Rev 1.10   Aug 04 2010 18:14:48   makuier
 * support for access number range added.
 * 
 *    Rev 1.9   Apr 14 2010 18:29:50   makuier
 * siteType Added
 * 
 *    Rev 1.8   Mar 12 2010 16:45:28   makuier
 * Trucate the exception message if it exceeds the max. length of 255.
 * 
 *    Rev 1.7   Feb 23 2010 12:38:38   makuier
 * Let execute method catch and handle parsing errors.
 * 
 *    Rev 1.6   Feb 04 2010 11:50:48   makuier
 * Raise error if location address is not found.
 * 
 *    Rev 1.5   Jan 28 2010 16:50:34   makuier
 * Cache the exception and return an error XML.
 * 
 *    Rev 1.4   Dec 02 2009 15:36:22   makuier
 * changed the type of input.
 * 
 *    Rev 1.3   Nov 23 2009 17:14:14   makuier
 * adapted to schema changes.
 * 
 *    Rev 1.2   Nov 17 2009 18:57:04   makuier
 * Fetch by skeleton contract added.
 * 
 *    Rev 1.1   Oct 29 2009 11:41:24   makuier
 * Name and organization type added to the output.
 * 
 *    Rev 1.0   Aug 31 2009 11:21:30   makuier
 * Initial revision.
*/
package net.arcor.bks.requesthandler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import net.arcor.bks.client.DatabaseClient;
import net.arcor.bks.db.PrefetchProductBundlesDAO;
import net.arcor.epsm_basetypes_001.InputData;
import net.arcor.orderschema.Address;
import net.arcor.orderschema.CcbIdB;
import de.arcor.aaw.kernAAW.bks.services.PrefetchProductBundlesInput;
import de.arcor.aaw.kernAAW.bks.services.PrefetchProductBundlesOutput;
import de.arcor.aaw.kernAAW.bks.services.ProductBundlePreview;
import de.arcor.aaw.kernAAW.bks.services.ProductBundlePreviewList;

/**
 * @author MAKUIER
 *
 */
public class PrefetchProductBundlesHandler  extends BaseServiceHandler{

	private Integer totalBundleSize = null;
	private Boolean lastChunk=null;
	private ArrayList<HashMap<String, Object>> bundles = null;
	private ArrayList<HashMap<String, Object>>	pendingLineCreations = null;

	/** 
	 * This method executes the data retrieval queries and returns the result in output object.
	 * The result is also cached in JCS cache to speed up the retrieval in case anything goes
	 * wrong.
	 */
	public String execute() throws Exception {
		String returnXml = null;
		errorText = null;
		errorCode = null;
		lastChunk=false;
		PrefetchProductBundlesDAO dao= 
			(PrefetchProductBundlesDAO) DatabaseClient.getBksDataAccessObject(null,"PrefetchProductBundlesDAO");

		try {
			BigDecimal chunkSize = ((PrefetchProductBundlesInput)input).getChunkSize();
			BigDecimal chunkNumber = ((PrefetchProductBundlesInput)input).getChunkNumber();
			String oneGroupId = ((PrefetchProductBundlesInput)input).getOneGroupId();
			String siteId = ((PrefetchProductBundlesInput)input).getSiteId();
			String supertrackingId = ((PrefetchProductBundlesInput)input).getSuperCustomerTrackingId();
			bundles = null;
			if (customerNumber!=null) 
				bundles  = dao.getCustomerBundles(customerNumber);
			else if (oneGroupId!=null)
				bundles = dao.getBundlesForAccNumChar(oneGroupId,"VI086");
			else if (supertrackingId!=null)
				bundles = dao.getBundlesForEnvelopId(supertrackingId);
			else if (siteId!=null)
				bundles = dao.getBundlesForAccNumChar(siteId,"VI078");
			pendingLineCreations = dao.getPendingOrders(customerNumber);
			ProductBundlePreviewList bundleList = getBundlesInfo(dao,bundles,oneGroupId,chunkSize,chunkNumber);
			if (totalBundleSize!=null||chunkSize==null)
				addLineCreationOrders(dao,pendingLineCreations,bundleList, chunkSize, chunkNumber);

			returnXml = populateOutputObject(bundleList,chunkNumber);
		} catch (Exception e) {
			logger.error(e.getMessage());
			serviceStatus = Status.PRECONDITION_ERROR;
			errorCode = "BKS0008";
			if (e.getMessage() != null && e.getMessage().length() > 255)
				errorText = e.getMessage().substring(0, 255);
			else
				errorText = e.getMessage();
			returnXml = populateOutputObject(null,null);
		}
		finally {
			dao.closeConnection();
		}
		return returnXml;
	}

	public ArrayList<HashMap<String, Object>> getBundles() {
		return bundles;
	}

	public void setBundles(ArrayList<HashMap<String, Object>> bundles) {
		this.bundles = bundles;
	}

	public ArrayList<HashMap<String, Object>> getPendingLineCreations() {
		return pendingLineCreations;
	}

	public void setPendingLineCreations(
			ArrayList<HashMap<String, Object>> pendingLineCreations) {
		this.pendingLineCreations = pendingLineCreations;
	}

	PrefetchProductBundlesOutput populateBundleList(String customerNumber,
				String oneGroupId, String supertrackingId, BigDecimal chunkSize, BigDecimal chunkNumber) throws Exception {

		PrefetchProductBundlesOutput output = new PrefetchProductBundlesOutput();
		lastChunk=false;
		PrefetchProductBundlesDAO dao= 
			(PrefetchProductBundlesDAO) DatabaseClient.getBksDataAccessObject(null,"PrefetchProductBundlesDAO");
		if (customerNumber!=null) 
			bundles  = dao.getCustomerBundles(customerNumber);
		else if (oneGroupId!=null)
			bundles = dao.getBundlesForAccNumChar(oneGroupId,"VI086");
		else if (supertrackingId!=null)
			bundles = dao.getBundlesForEnvelopId(supertrackingId);
		pendingLineCreations = dao.getPendingOrders(customerNumber);
		ProductBundlePreviewList bundleList = getBundlesInfo(dao,bundles,oneGroupId,chunkSize,chunkNumber);
		if (totalBundleSize!=null||chunkSize==null) {
			addLineCreationOrders(dao,pendingLineCreations,bundleList,chunkSize,chunkNumber);
		}
		output = new PrefetchProductBundlesOutput();
		output.setProductBundlePreviewList(bundleList);
		output.setChunkNumber(chunkNumber);
		if (chunkNumber!=null&&chunkSize!=null)
			output.setLastChunk(lastChunk);
		return output;
	 }

	private void addLineCreationOrders(PrefetchProductBundlesDAO dao,ArrayList<HashMap<String, Object>>	pendingLineCreations,
			 ProductBundlePreviewList bundleList, BigDecimal chunkSize, BigDecimal chunkNumber) throws Exception {
		int startloop=0;
		int endloop=pendingLineCreations.size();
		if (chunkSize!=null&&chunkNumber!=null) {
			startloop = (chunkNumber.intValue()-1)*chunkSize.intValue()-totalBundleSize;
			endloop = startloop + chunkSize.intValue();
			startloop = (startloop > 0) ? startloop : 0;
		}
		lastChunk = endloop>=pendingLineCreations.size();
		endloop=(!lastChunk)?endloop:pendingLineCreations.size();
		for (int i = startloop; i < endloop; i++) {
			String orderId = (String) pendingLineCreations.get(i).get("ORDER_ID");
			Integer orderPosNum = ((BigDecimal) pendingLineCreations.get(i).get("ORDER_POSITION_NUMBER")).intValue();
			ProductBundlePreview aBundle = new ProductBundlePreview();
			aBundle.setOrderId(orderId);
			aBundle.setOrderPositionNumber(orderPosNum);
			String custNo = (String) pendingLineCreations.get(i).get("CUSTOMER_NUMBER");
			aBundle.setCustomerNumber(custNo);
			setCustomerNumber(custNo);
			aBundle.setCustomerName((String) pendingLineCreations.get(i).get("NAME"));
			aBundle.setCustomerForename((String) pendingLineCreations.get(i).get("FORENAME"));
			aBundle.setOrganizationType((String) pendingLineCreations.get(i).get("ORGANIZATION_TYPE_RD"));
			bundleList.getProductBundlePreview().add(aBundle);
		}
	}

	private String populateOutputObject(ProductBundlePreviewList bundleList, BigDecimal chunkNumber) throws Exception {
		String returnXml = null;
		output = new PrefetchProductBundlesOutput();
		if (serviceStatus != Status.SUCCESS){
			output.setResult(false);
			output.setErrorCode(errorCode);
			output.setErrorText(errorText);
		}else {
			output.setResult(true);
			((PrefetchProductBundlesOutput)output).setProductBundlePreviewList(bundleList);
			if(chunkNumber!=null){
				((PrefetchProductBundlesOutput)output).setChunkNumber(chunkNumber);
				((PrefetchProductBundlesOutput)output).setLastChunk(lastChunk);
			}
		}
		returnXml = theHelper.serializeForMcf(output, customerNumber,accessNumber,"de.arcor.aaw.kernAAW.bks.services" ,"PrefetchProductBundles");
		return returnXml;
	}

	private ProductBundlePreviewList getBundlesInfo(PrefetchProductBundlesDAO dao,ArrayList<HashMap<String, Object>> bundles, 
			String oneGroupId, BigDecimal chunkSize, BigDecimal chunkNumber) throws Exception {
		ProductBundlePreviewList bundleList = new ProductBundlePreviewList();
		Integer startNumber=0;
		Integer endNumber=bundles.size();
		if (chunkSize!=null&&chunkNumber!=null){
			startNumber = (chunkNumber.intValue()-1)*chunkSize.intValue();
			endNumber = startNumber+chunkSize.intValue();
		}
		if (chunkSize!=null&&chunkNumber!=null&&bundles.size() <= endNumber){
			totalBundleSize  = bundles.size();
			endNumber=totalBundleSize;
		}

		if (bundles != null && bundles.size() > 0) {
			for (int i=startNumber;i<endNumber;i++) {
				ProductBundlePreview aBundle = new ProductBundlePreview();
				String bundleType = (String) bundles.get(i).get("BUNDLE_TYPE_RD");
				CcbIdB bundleId = new CcbIdB();
				bundleId.setExisting((String) bundles.get(i).get("BUNDLE_ID"));
				aBundle.setCcbId(bundleId);
				String aoFlagString = (String)bundles.get(i).get("AOSTATUS");
				boolean aoFlag = false;
				if (aoFlagString!=null)
					aoFlag = (aoFlagString.equals("Y"))?true:false;
				aBundle.setAoMastered(aoFlag);
				String custNo = (String) bundles.get(i).get("CUSTOMER_NUMBER");
				aBundle.setCustomerNumber(custNo);
				setCustomerNumber(custNo);
				aBundle.setCustomerName((String) bundles.get(i).get("NAME"));
				aBundle.setCustomerForename((String) bundles.get(i).get("FORENAME"));
				aBundle.setOrganizationType((String) bundles.get(i).get("ORGANIZATION_TYPE_RD"));
				ArrayList<HashMap<String, Object>> bundleItems = dao.getBundleItems(bundleId.getExisting());
				aBundle.setDialInAccountName(getAccessNumberValue(dao,bundleItems,"I9058",false));
				Address locAddresss = getLocationAddress(dao,bundleItems,false);
				if (locAddresss != null)
					aBundle.setLocationAddress(locAddresss);

				String mainAccessNumber = getMainAccessNumber(dao,bundleItems,false);
				if (mainAccessNumber != null)
					aBundle.setMainAccessNumber(mainAccessNumber); 
				else
					aBundle.setMainAccessNumber(getRangeBaseNumber(dao,bundleItems,false)); 
				aBundle.setProductBundleType(bundleType);
				aBundle.setOneGroupId(getAccessNumberValue(dao,bundleItems,"VI086",false));
				aBundle.setOneNetId(getOnenetId(dao,bundleId.getExisting()));
				aBundle.setSiteId(getAccessNumberValue(dao,bundleItems,"VI078",false));
				populateSalesPackageInfo(dao,bundleId.getExisting(),aBundle);
				aBundle.setTechnicalAccessServiceId(getAccessNumberValue(dao,bundleItems,"VI080",false));
				if (aBundle.getTechnicalAccessServiceId() == null)
					aBundle.setTechnicalAccessServiceId(getConfigValue(dao,bundleItems,"V0152",false));
				aBundle.setSiteType(fetchSiteType(dao,bundleItems,false));
				aBundle.setFutureIndicator(false);
				boolean bFutureExist = doesFutureItemsExist(bundleItems);
				boolean bIPSSiteBundle = isIPCentrexSite(bundleItems);
				if ((!bFutureExist || bIPSSiteBundle))
					bundleList.getProductBundlePreview().add(aBundle);

				if (bFutureExist){
					ProductBundlePreview futureBundle = new ProductBundlePreview();
					futureBundle.setCcbId(aBundle.getCcbId());
					String futureType = (String) bundles.get(i).get("FUTURE_BUNDLE_TYPE");
					if (futureType!=null)
						futureBundle.setProductBundleType(futureType);
					else 
						futureBundle.setProductBundleType("UNDEFINED");
					futureBundle.setAoMastered(aBundle.isAoMastered());
					futureBundle.setCustomerNumber(aBundle.getCustomerNumber());
					futureBundle.setCustomerName(aBundle.getCustomerName());
					futureBundle.setCustomerForename(aBundle.getCustomerForename());
					futureBundle.setOrganizationType(aBundle.getOrganizationType());
					futureBundle.setDialInAccountName(getAccessNumberValue(dao,bundleItems,"I9058",true));
					locAddresss = getLocationAddress(dao,bundleItems,true);
					if (locAddresss != null)
						futureBundle.setLocationAddress(locAddresss);

					mainAccessNumber = getMainAccessNumber(dao,bundleItems,true);
					if (mainAccessNumber != null)
						futureBundle.setMainAccessNumber(mainAccessNumber); 
					else
						futureBundle.setMainAccessNumber(getRangeBaseNumber(dao,bundleItems,true)); 
					futureBundle.setTechnicalAccessServiceId(getAccessNumberValue(dao,bundleItems,"VI080",true));
					if (futureBundle.getTechnicalAccessServiceId() == null)
						futureBundle.setTechnicalAccessServiceId(getConfigValue(dao,bundleItems,"V0152",true));
					futureBundle.setSiteType(fetchSiteType(dao,bundleItems,true));
					futureBundle.setFutureIndicator(true);
					if (oneGroupId == null) 
						bundleList.getProductBundlePreview().add(futureBundle);
					if (oneGroupId != null) {
						String onegr = getConfigValue(dao,bundleItems,"VI071",true);
						if (bIPSSiteBundle && onegr.equals(oneGroupId) )
							bundleList.getProductBundlePreview().add(futureBundle);
					}
					addFutureNode(dao,bundleItems,futureBundle);
				}
			}
		}
		return bundleList;
	}

	private String getOnenetId(PrefetchProductBundlesDAO dao, String bundleId) throws Exception {
		ArrayList<HashMap<String, Object>> onenet = dao.getOnenetForBundle(bundleId);
		if (onenet!=null&&onenet.size()>0)
			return (String) onenet.get(0).get("ONENET_ID");
		return null;
	}

	private String fetchSiteType(PrefetchProductBundlesDAO dao,
			ArrayList<HashMap<String, Object>> bundleItems, boolean futureInd) throws Exception {
		String siteType = getConfigValue(dao,bundleItems,"VI072",futureInd);
		if (siteType == null){
			for (int i=0;bundleItems!=null && i<bundleItems.size();i++) {
				String validityPeriod = (String) bundleItems.get(i).get("FUTURE_INDICATOR");
				String serviceCode = (String) bundleItems.get(i).get("SERVICE_CODE");
				if (!serviceCode.equals("V0011") && !serviceCode.equals("VI020"))
					continue;
				if ((validityPeriod == null && futureInd) ||
						(validityPeriod != null && validityPeriod.equals("Y") && !futureInd) ||
						(validityPeriod != null && !validityPeriod.equals("Y") && futureInd))
						continue;
				if (serviceCode.equals("V0011"))
					return "isdnP2P";
				else 
					return "Standard";
			}			
		}
		return siteType;
	}

	private boolean isIPCentrexSite(ArrayList<HashMap<String, Object>> bundleItems) {
		boolean bCurrentSiteItem = false;
		boolean bFutureSiteItem = false;
		for (int i=0;bundleItems!=null && i<bundleItems.size();i++) {
			String validityPeriod = (String) bundleItems.get(i).get("FUTURE_INDICATOR");
			String bundleItemType = (String) bundleItems.get(i).get("BUNDLE_ITEM_TYPE_RD");
			if (validityPeriod!=null && validityPeriod.equals("Y") && 
				(bundleItemType.equals("IPCENTREX_SITE") || bundleItemType.equals("SIPTRUNK_SITE")))
				bFutureSiteItem = true;
			if (validityPeriod!=null && validityPeriod.equals("T") && 
				(bundleItemType.equals("IPCENTREX_SITE") || bundleItemType.equals("SIPTRUNK_SITE")))
				bCurrentSiteItem = true;
		}
		return bCurrentSiteItem&&bFutureSiteItem;
	}

	private void addFutureNode(PrefetchProductBundlesDAO dao,ArrayList<HashMap<String, Object>> bundleItems,
								ProductBundlePreview futureBundle) throws Exception {
		futureBundle.setDialInAccountName(getAccessNumberValue(dao,bundleItems,"I9058",true));
		Address locAddresss = getLocationAddress(dao,bundleItems,true);
		if (locAddresss != null)
			futureBundle.setLocationAddress(locAddresss);

		String mainAccessNumber = getMainAccessNumber(dao,bundleItems,true);
		if (mainAccessNumber != null)
			futureBundle.setMainAccessNumber(mainAccessNumber); 
		else
			futureBundle.setMainAccessNumber(getRangeBaseNumber(dao,bundleItems,true)); 
		futureBundle.setOneGroupId(getAccessNumberValue(dao,bundleItems,"VI086",true));
		futureBundle.setTechnicalAccessServiceId(getAccessNumberValue(dao,bundleItems,"VI080",true));
		if (futureBundle.getTechnicalAccessServiceId() == null)
			futureBundle.setTechnicalAccessServiceId(getConfigValue(dao,bundleItems,"V0152",true));
		futureBundle.setSiteType(fetchSiteType(dao,bundleItems,true));
	}

	private boolean doesFutureItemsExist(ArrayList<HashMap<String, Object>> bundleItems) {
		for (int i=0;bundleItems!=null && i<bundleItems.size();i++) {
			String validityPeriod = (String) bundleItems.get(i).get("FUTURE_INDICATOR");
			if (validityPeriod!=null && validityPeriod.equals("Y"))
				return true;
		}
		return false;
	}

	private String getRangeBaseNumber(PrefetchProductBundlesDAO dao,
			ArrayList<HashMap<String, Object>> bundleItems, boolean futureInd) throws Exception {
		if (bundleItems != null && bundleItems.size() > 0) {
			for (int i=0;i<bundleItems.size();i++) {
				String servSubsId = (String) bundleItems.get(i).get("SUPPORTED_OBJECT_ID");
				String validityPeriod = (String) bundleItems.get(i).get("FUTURE_INDICATOR");
				if ((validityPeriod == null && futureInd) ||
					(validityPeriod != null && validityPeriod.equals("Y") && !futureInd) ||
					(validityPeriod != null && !validityPeriod.equals("Y") && futureInd))
					continue;
				ArrayList<HashMap<String,Object>> accessNums = dao.getMainAccessNumber(servSubsId,"V0002");
				if (accessNums != null && accessNums.size() > 0){
					String range = (String)accessNums.get(0).get("ACCESS_NUMBER");
					if (range != null) {
						String[] numberFields = range.split(";");
						return numberFields[0]+numberFields[1]+numberFields[2];
					}
				}
			}
		}
		return null;
	}

	private void populateSalesPackageInfo(PrefetchProductBundlesDAO dao,
			String bundleId, ProductBundlePreview aBundle)  throws Exception {
		ArrayList<HashMap<String, Object>> salesPackectInfo = dao.getSalesPackageInfo(bundleId);
		if (salesPackectInfo != null && salesPackectInfo.size() > 0) {
			aBundle.setSalesPacketBasePrice(((BigDecimal) salesPackectInfo.get(0).get("BILLING_NAME_BASE_CHARGE")).toString());
			aBundle.setSalesPacketId((String) salesPackectInfo.get(0).get("SALES_PACKET_ID"));
			aBundle.setSalesPacketName((String) salesPackectInfo.get(0).get("SALES_PACKET_NAME"));
		}
	}

	private String getConfigValue(PrefetchProductBundlesDAO dao,
			ArrayList<HashMap<String, Object>> bundleItems, String charCode, boolean futureInd)  throws Exception {
		if (bundleItems != null && bundleItems.size() > 0) {
			for (int i=0;i<bundleItems.size();i++) {
				String bundleItemType = (String) bundleItems.get(i).get("BUNDLE_ITEM_TYPE_RD");
				String servSubsId = (String) bundleItems.get(i).get("SUPPORTED_OBJECT_ID");
				String validityPeriod = (String) bundleItems.get(i).get("FUTURE_INDICATOR");
				if ((validityPeriod == null && futureInd) ||
					(validityPeriod != null && validityPeriod.equals("Y") && !futureInd) ||
					(validityPeriod != null && !validityPeriod.equals("Y") && futureInd))
					continue;
				if (bundleItemType.equals("IPCENTREX_SEAT"))
					continue;
				ArrayList<HashMap<String,Object>> configValue = dao.getConfiguredValue(servSubsId,charCode);
				if (configValue != null && configValue.size() > 0){
					return (String)configValue.get(0).get("CONFIGURED_VALUE_STRING");
				}
			}
		}
		return null;
	}

	private String getAccessNumberValue(PrefetchProductBundlesDAO dao,
			ArrayList<HashMap<String, Object>> bundleItems, String charCode, boolean futureInd)  throws Exception {
		if (bundleItems != null && bundleItems.size() > 0) {
			for (int i=0;i<bundleItems.size();i++) {
				String bundleItemType = (String) bundleItems.get(i).get("BUNDLE_ITEM_TYPE_RD");
				String servSubsId = (String) bundleItems.get(i).get("SUPPORTED_OBJECT_ID");
				String validityPeriod = (String) bundleItems.get(i).get("FUTURE_INDICATOR");
				if ((validityPeriod == null && futureInd) ||
					(validityPeriod != null && validityPeriod.equals("Y") && !futureInd) ||
					(validityPeriod != null && !validityPeriod.equals("Y") && futureInd))
					continue;
				if (bundleItemType.equals("IPCENTREX_SEAT"))
					continue;
				ArrayList<HashMap<String,Object>> configValue = dao.getMainAccessNumber(servSubsId,charCode);
				if (configValue != null && configValue.size() > 0){
					return (String)configValue.get(0).get("ACCESS_NUMBER");
				}
			}
		}
		return null;
	}

	private String getMainAccessNumber(PrefetchProductBundlesDAO dao,
			ArrayList<HashMap<String,Object>> bundleItems, boolean futureInd) throws Exception {
		if (bundleItems != null && bundleItems.size() > 0) {
			for (int i=0;i<bundleItems.size();i++) {
				String bundleItemType = (String) bundleItems.get(i).get("BUNDLE_ITEM_TYPE_RD");
				String validityPeriod = (String) bundleItems.get(i).get("FUTURE_INDICATOR");
				if ((validityPeriod==null && futureInd) ||
					(validityPeriod!=null && validityPeriod.equals("Y") && !futureInd) ||
					(validityPeriod!=null && !validityPeriod.equals("Y") && futureInd))
					continue;
				if (bundleItemType.equals("IPCENTREX_SEAT"))
					continue;
				String servSubsId = (String) bundleItems.get(i).get("SUPPORTED_OBJECT_ID");
				ArrayList<HashMap<String,Object>> accessNums = dao.getMainAccessNumber(servSubsId,"V0001");
				if (accessNums != null && accessNums.size() > 0){
					return (String)accessNums.get(0).get("START_RANGE_STRING");
				}
			}
		}
		return null;
	}

	private Address getLocationAddress(PrefetchProductBundlesDAO dao,
			ArrayList<HashMap<String,Object>> bundleItems, boolean futureInd) throws Exception {
		if (bundleItems != null && bundleItems.size() > 0) {
			for (int i=0;i<bundleItems.size();i++) {
				String servSubsId = (String) bundleItems.get(i).get("SUPPORTED_OBJECT_ID");
				String validityPeriod = (String) bundleItems.get(i).get("FUTURE_INDICATOR");
				if ((validityPeriod==null && futureInd) ||
					(validityPeriod!=null && validityPeriod.equals("Y") && !futureInd) ||
					(validityPeriod!=null && !validityPeriod.equals("Y") && futureInd))
					continue;
				ArrayList<HashMap<String,Object>> address = dao.getAddress(servSubsId);
				if (address != null && address.size() > 0){
					Address locAddr = new Address();
					locAddr.setCity((String) address.get(0).get("CITY_NAME"));
					locAddr.setCitySuffix((String) address.get(0).get("CITY_SUFFIX_NAME"));
					locAddr.setCountry((String) address.get(0).get("COUNTRY_RD"));
					locAddr.setPostalCode((String) address.get(0).get("POSTAL_CODE"));
					locAddr.setStreet((String) address.get(0).get("STREET_NAME"));
					locAddr.setStreetNumber((String) address.get(0).get("STREET_NUMBER"));
					locAddr.setStreetNumberSuffix((String) address.get(0).get("STREET_NUMBER_SUFFIX"));
					return locAddr;
				}
			}
		}
		return null;
	}

	public void setInput(InputData input) {
		super.setInput(input);
		if (((PrefetchProductBundlesInput)input).getCustomerNumber() != null)
			customerNumber = ((PrefetchProductBundlesInput)input).getCustomerNumber();
	}
}