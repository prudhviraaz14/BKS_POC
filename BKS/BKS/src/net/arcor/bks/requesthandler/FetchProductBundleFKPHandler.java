/*
    $Header:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/FetchProductBundleFKPHandler.java-arc   1.22   Jun 09 2017 07:56:10   makuier  $

    $Log:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/FetchProductBundleFKPHandler.java-arc  $
 * 
 *    Rev 1.22   Jun 09 2017 07:56:10   makuier
 * Filter out obsolete nodes for SOM version.
 * 
 *    Rev 1.21   Apr 11 2017 15:24:44   makuier
 * Only consider deviating customer number which are BKS relevant.
 * 
 *    Rev 1.20   Jan 20 2017 08:57:02   gaurav.verma
 * PPM 113765_169790 VF Business Broker + Hierarchy Bundle
 * 
 *    Rev 1.19   Oct 24 2016 10:40:44   makuier
 * Service versioning for service refdata
 * 
 *    Rev 1.18   Sep 21 2016 07:24:48   makuier
 * Consider product code in the address key.
 * 
 *    Rev 1.17   Oct 27 2015 13:16:28   makuier
 * Return V0854 and V0855 for purchased hw as well.
 * 
 *    Rev 1.16   Sep 15 2015 15:42:16   makuier
 * Support for rented hardware added.
 * 
 *    Rev 1.15   Jul 15 2015 15:57:00   makuier
 * Pass returnOrdered.
 * 
 *    Rev 1.14   Dec 16 2013 14:19:48   makuier
 * Pass the version to the master data.
 * 
 *    Rev 1.13   Nov 28 2013 14:33:50   makuier
 * Send the Som version to COM if the corresponding JMS property is populated by client.
 * 
 *    Rev 1.12   Nov 07 2013 12:46:04   makuier
 * ResellerId added to the signature.
 * 
 *    Rev 1.11   Jun 12 2013 11:14:10   makuier
 * Changed the serializer
 * 
 *    Rev 1.10   Apr 24 2013 16:53:32   makuier
 * disable cache if the clientversion is set.
 * 
 *    Rev 1.9   Apr 24 2013 16:41:58   makuier
 * som versioning added.
 * 
 *    Rev 1.8   May 10 2012 08:27:28   makuier
 * In DE do not populate address if postal code is missing.
 * 
 *    Rev 1.7   Apr 30 2012 13:51:16   makuier
 * Handle Extra numbers
 * 
 *    Rev 1.6   Apr 24 2012 17:45:54   makuier
 * Adapted to new cache lay out.
 * 
 *    Rev 1.5   Apr 19 2012 09:48:00   makuier
 * Allow conversion method for access numbers.
 * 
 *    Rev 1.4   Mar 29 2012 15:26:28   makuier
 * put barcode in open orders
 * 
 *    Rev 1.3   Mar 26 2012 12:26:08   makuier
 * - Changed the making unique node id algorithm
 * 
 *    Rev 1.2   Mar 20 2012 15:51:36   makuier
 *  handle failed COM call with order id.
 * 
 *    Rev 1.1   Feb 24 2012 13:44:30   makuier
 * set last chunk if there is no seats in the bundle.
 * 
 *    Rev 1.0   Feb 13 2012 17:00:26   makuier
 * Initial revision.
*/
package net.arcor.bks.requesthandler;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.arcor.bks.client.DatabaseClient;
import net.arcor.bks.client.DatabaseClientConfig;
import net.arcor.bks.common.BksDataException;
import net.arcor.bks.common.BksFunctionalException;
import net.arcor.bks.common.BksRefDataCacheHandler;
import net.arcor.bks.db.FetchProductBundleDAO;
import net.arcor.bks.db.GetSomMasterDataDAO;
import net.arcor.epsm_basetypes_001.InputData;
import net.arcor.mcf2.exception.base.MCFException;
import net.arcor.mcf2.exception.internal.XMLException;
import net.arcor.mcf2.model.ServiceResponse;
import net.arcor.mcf2.sender.MessageSender;
import net.arcor.mcf2.xml.XmlProcessor;
import net.arcor.orderschema.Access;
import net.arcor.orderschema.Accesses;
import net.arcor.orderschema.AssociatedObjectReference;
import net.arcor.orderschema.Beneficiary;
import net.arcor.orderschema.CcbIdAny;
import net.arcor.orderschema.CcbIdB;
import net.arcor.orderschema.CcbIdC;
import net.arcor.orderschema.CcbIdS;
import net.arcor.orderschema.ContactRole;
import net.arcor.orderschema.ContactRoleRefIdList;
import net.arcor.orderschema.Customer;
import net.arcor.orderschema.CustomerData;
import net.arcor.orderschema.DirectoryEntry;
import net.arcor.orderschema.ExistConfAddress;
import net.arcor.orderschema.ExistConfBoolean;
import net.arcor.orderschema.ExistConfString100;
import net.arcor.orderschema.ExistConfStringId;
import net.arcor.orderschema.Functions;
import net.arcor.orderschema.Internet;
import net.arcor.orderschema.Iptv;
import net.arcor.orderschema.LineChange;
import net.arcor.orderschema.LineCreation;
import net.arcor.orderschema.Order;
import net.arcor.orderschema.OrderPositionType;
import net.arcor.orderschema.OtherAccess;
import net.arcor.orderschema.OtherFunction;
import net.arcor.orderschema.PayerAllCharges;
import net.arcor.orderschema.ProductBundle;
import net.arcor.orderschema.ProviderChange;
import net.arcor.orderschema.Relocation;
import net.arcor.orderschema.Seat;
import net.arcor.orderschema.Termination;
import net.arcor.orderschema.TvCenter;
import net.arcor.orderschema.Vod;
import net.arcor.orderschema.Voice;

import org.springframework.context.ApplicationContext;
import org.w3c.dom.Node;

import com.vodafone.mcf2.ws.model.impl.ServiceRequestSoap;

import de.arcor.aaw.kernAAW.bks.services.ContractPartnerChangePacket;
import de.arcor.aaw.kernAAW.bks.services.FetchProductBundleFKPInput;
import de.arcor.aaw.kernAAW.bks.services.FetchProductBundleFKPOutput;
import de.arcor.aaw.kernAAW.bks.services.LineChangePacket;
import de.arcor.aaw.kernAAW.bks.services.LineCreationPacket;
import de.arcor.aaw.kernAAW.bks.services.RelocationPacket;
import de.arcor.aaw.kernAAW.bks.services.TerminationPacket;
import de.vodafone.esb.service.sbus.com.com_prov_001.GetOrderDetailsRequest;
import de.vodafone.esb.service.sbus.com.com_prov_001.GetOrderDetailsResponse;
import de.vodafone.esb.service.sbus.com.com_prov_001.OrderOutputFormat;
import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * @author MAKUIER
 *
 */
public class FetchProductBundleFKPHandler  extends BaseServiceHandler{


	private MessageSender messageSender;

	ArrayList<String> subIds = new ArrayList<String>();
	Order order = null;
	ArrayList<HashMap<String, Object>> bundleItems = null;
	Integer suffix=0;
	boolean bFetchFuture = false;
	private boolean bFirstAccess;
	private boolean bFirstFunction;
	private BigDecimal chunkSize = null;
	private BigDecimal chunkNumber = null;
	private int totalseats = 0;
	private boolean lastchunk = false;
	private String hierarchyIndicator = null;
	private ArrayList<String> deviatingCustomerNumbers = new ArrayList<String>();

	final static String[] seatServices = {"VI011","VI012","VI013"};
	final static String[] depFunctionServices = {"VI061","VI062","VI076","VI077","V0014"};
	/** 
	 * This method executes the data retrieval queries and returns the result in output object.
	 * The result is also cached in JCS cache to speed up the retrieval in case anything goes
	 * wrong.
	 */
	public String execute() throws Exception {
		String returnXml = null;
		String bundleRoleRd = null;
		errorText = null;
		errorCode = null;
		bFetchFuture = false;
		ProductBundle bundleData = null;
		FetchProductBundleDAO dao = null;
		totalseats = 0;

		try {
			boolean aoFlag = false;
			String orderId = ((FetchProductBundleFKPInput)input).getOrderId();
			Integer orderPosNo = ((FetchProductBundleFKPInput)input).getOrderPositionNumber();
			CcbIdB bundleId = ((FetchProductBundleFKPInput)input).getCcbId();
			CcbIdS inputServId = ((FetchProductBundleFKPInput)input).getServiceSubscriptionId();
			String rootCustNo = ((FetchProductBundleFKPInput)input).getCustomerHierarchyNumber();
			chunkSize = ((FetchProductBundleFKPInput)input).getChunkSize();
			chunkNumber = ((FetchProductBundleFKPInput)input).getChunkNumber();
			boolean bUseCache = true;
			if (((FetchProductBundleFKPInput)input).isDisableCache() != null)
				bUseCache = !((FetchProductBundleFKPInput)input).isDisableCache();
			
			if (bUseCache)
				dao = (FetchProductBundleDAO) DatabaseClient.getBksDataAccessObject(null,"FetchProductBundleDAO");
			else
				dao = (FetchProductBundleDAO) DatabaseClient.getBksDaoForTargetDb("onl","FetchProductBundleDAO");
			String servSubsId = null; 
			ArrayList<HashMap<String, Object>> services = null;
			HashMap<String, Order> openOrders = new HashMap<String, Order>();
			if (orderId != null && orderPosNo != null) {
				ArrayList<HashMap<String, Object>> 
				compOrders = dao.getCompletedOrder(orderId,orderPosNo);
				if (compOrders != null && compOrders.size() > 0) {
					String closeReason = (String) compOrders.get(0).get("CLOSE_REASON_RD");
					if (closeReason!=null) {
						inputServId = new CcbIdS();
						inputServId.setExisting(compOrders.get(0).get("SUPPORTED_OBJECT_ID").toString());
					} else if (originator==null || !originator.equals("COM")) {
						lastchunk = true;
						getOpenOrdersForOrderId(dao,orderId,orderPosNo,openOrders);
					} 
				} else {
					ArrayList<HashMap<String, Object>> 
					custOrderBundle = dao.getCustomerOrderBundle(orderId);
					if (custOrderBundle != null && custOrderBundle.size() == 1) {
						bundleId = new CcbIdB();
						bundleId.setExisting(custOrderBundle.get(0).get("BUNDLE_ID").toString());
					} else if (custOrderBundle != null && custOrderBundle.size() > 1) {
						throw new BksFunctionalException("More than one bundle found for order id " + orderId);
					}
				}
			}
			if (bundleId != null || inputServId != null) {
				if (bundleId != null) {
					if (bUseCache && (chunkSize==null||chunkNumber==null) &&
							clientSchemaVersion==null) {
						returnXml = (String) theHelper.fetchFromCache(
								"FetchProductBundleFKP", bundleId.getExisting(), null);
						if (returnXml!=null)
							return returnXml;
					} else if (bUseCache && chunkNumber!=null) {
						returnXml = (String) theHelper.fetchFromCache(
								"FetchProductBundleFKP", bundleId.getExisting()+chunkNumber.intValue(), null);
						if (returnXml!=null)
							return returnXml;
					}
					Boolean bFutureInd = ((FetchProductBundleFKPInput)input).isFutureIndicator();
					if (bFutureInd != null && bFutureInd)
						bFetchFuture = bFutureInd;
					bundleItems = dao.getBundleItems(bundleId.getExisting());
					if (bundleItems == null ||  bundleItems.size() == 0)
						throw new BksFunctionalException("No bundle found for ID " + bundleId.getExisting());
					customerNumber = (String)bundleItems.get(0).get("CUSTOMER_NUMBER");
					bundleRoleRd = (String) bundleItems.get(0).get("BUNDLE_ROLE_RD");
					populateDeviatingCustomerInfo(bundleItems);
					int lastIndex = bundleItems.size() -1;
					servSubsId = (String)bundleItems.get(lastIndex).get("SUPPORTED_OBJECT_ID");
					String aoFlagString = (String)bundleItems.get(0).get("AOSTATUS");
					if (aoFlagString!=null)
						aoFlag = (aoFlagString.equals("Y"))?true:false;
					services = dao.getServiceSubscription(servSubsId);
				} else if (inputServId != null) {
					if (inputServId.getExisting().equals("NOT_AVAILABLE")) {
						returnXml = populateOutputObject(null,null,null);
						return returnXml;
					}
					if (bUseCache && (chunkSize==null||chunkNumber==null) &&
							clientSchemaVersion==null) {
						returnXml = (String) theHelper.fetchFromCache(
								"FetchProductBundleFKP", inputServId.getExisting(), null);
						if (returnXml!=null)
							return returnXml;
					} else if (bUseCache && chunkNumber!=null) {
						returnXml = (String) theHelper.fetchFromCache(
								"FetchProductBundleFKP", inputServId.getExisting()+chunkNumber.intValue(), null);
						if (returnXml!=null)
							return returnXml;
					}
					bundleItems = dao.getBundleItemsByServSubs(inputServId.getExisting());
					if (bundleItems == null ||  bundleItems.size() == 0) {
						servSubsId = inputServId.getExisting();
						services = dao.getUnbundledServiceSubscription(servSubsId);
						if (services != null && services.size() > 0){
							customerNumber = (String)services.get(0).get("CUSTOMER_NUMBER");
						} else {
							ArrayList<HashMap<String, Object>> allocatedId = dao.getAllocatedId(servSubsId);
							if (allocatedId!=null && allocatedId.size() >0){
								logger.warn("The service subscription "+servSubsId+" does not exist but is allocated by front-end");
								returnXml = populateOutputObject(null,null,null);
								return returnXml;
							}
						}
					} else {
						bundleId = new CcbIdB();
						bundleId.setExisting((String)bundleItems.get(0).get("BUNDLE_ID"));
						bundleId.setType("B");
						bFetchFuture = getFutureFlagForService(inputServId.getExisting());
						String aoFlagString = (String)bundleItems.get(0).get("AOSTATUS");
						if (aoFlagString!=null)
							aoFlag = (aoFlagString.equals("Y"))?true:false;
						customerNumber = (String)bundleItems.get(0).get("CUSTOMER_NUMBER");
						bundleRoleRd = (String) bundleItems.get(0).get("BUNDLE_ROLE_RD");
						populateDeviatingCustomerInfo(bundleItems);
						int lastIndex = bundleItems.size() -1;
						servSubsId = (String)bundleItems.get(lastIndex).get("SUPPORTED_OBJECT_ID");
						services = dao.getServiceSubscription(servSubsId);
					}
				}
				validateHierarchy(rootCustNo);
				if (services==null || services.size() == 0)
					throw new BksFunctionalException("No service Subscription / guiding rule found "+servSubsId);
				ArrayList<String> accounts = new ArrayList<String>();
				accounts.add(services.get(0).get("ACCOUNT_NUMBER").toString());
				ArrayList<String> sdContractNumbers = new ArrayList<String>();
				if (bundleItems == null ||  bundleItems.size() == 0) {
					ArrayList<HashMap<String, Object>> sdcs = dao.getSDContractData(servSubsId);
					if (sdcs != null && sdcs.size() > 0)
						sdContractNumbers.add(sdcs.get(0).get("CONTRACT_NUMBER").toString());
				} else {
					for (int i = 0; i<bundleItems.size(); i++) {
						String itemType = (String)bundleItems.get(i).get("BUNDLE_ITEM_TYPE_RD");
						String tmpServSubsId = (String)bundleItems.get(i).get("SUPPORTED_OBJECT_ID");
						if (subIds.contains(tmpServSubsId))
							continue;
						if (itemType.equals("IPCENTREX_SEAT")){
							subIds.add(tmpServSubsId);
							totalseats ++;
							continue;
						}
						subIds.add(tmpServSubsId);
						ArrayList<HashMap<String, Object>> sdcs = dao.getSDContractData(tmpServSubsId);
						if (sdcs != null && sdcs.size() > 0 && !sdContractNumbers.contains(sdcs.get(0).get("CONTRACT_NUMBER").toString()))
							sdContractNumbers.add(sdcs.get(0).get("CONTRACT_NUMBER").toString());
					}
					if (totalseats==0)
						lastchunk=true;
				}
				subIds.clear();
				if(hierarchyIndicator !=null && hierarchyIndicator.equals("Y")){
					if(deviatingCustomerNumbers.size()>1) {
						String errorText="Bundle " + bundleId.getExisting() + " is a hierarchy bundle with different deviating customer numbers";
						throw new BksDataException("BKS0043",errorText);
					}
					if(deviatingCustomerNumbers.size()==0) {
                        String errorText="Bundle " + bundleId.getExisting() + " is a hierarchy bundle with no deviating customer numbers.";
                        throw new BksDataException("BKS0045",errorText);
                    }

					setCustomerNumber(deviatingCustomerNumbers.get(0));
				}
				
               if(bundleRoleRd != null && !bundleRoleRd.equals("DEFAULT")){
					
					String errorText="Bundle " + bundleId.getExisting() + " has not supported bundle Role";
			           throw new BksDataException("BKS0044",errorText);
				}
               
				String resellerId = ((FetchProductBundleFKPInput)input).getResellerId();
				order=getCustomerData(dao,resellerId,accounts,sdContractNumbers);
				ExistConfString100 provCode = new ExistConfString100();
				provCode.setExisting(order.getCustomerData().get(0).getCustomer().getProviderCode().getExisting());
				if (bundleItems!=null && bundleItems.size() > 0)
					bundleData = getProductBundlesInfo(dao,bundleItems,bundleId,aoFlag,accounts.get(0));
				else
					bundleData = getUnbundledProductInfo(dao,services);
				bundleData.setProviderCode(provCode);
				
				//populateAdjustments(dao,accounts,bundleData);
				QName qName = new QName("http://www.arcor.net/orderSchema", "ProductBundle");
				JAXBElement<ProductBundle> jaxbObject = 
					new JAXBElement<ProductBundle>(qName,ProductBundle.class , bundleData);
				order.getOrderPosition().add(jaxbObject);
				if (originator==null || !originator.equals("COM"))
					getOpenOrdersForBundle(dao,subIds,customerNumber,openOrders);
			} 
			
			CustomerData customerData = null;
			if (order!=null && order.getCustomerData() != null && order.getCustomerData().size() > 0)
				customerData = order.getCustomerData().get(0);
			
			returnXml = populateOutputObject(customerData,bundleData,openOrders);

			if (chunkSize!=null && chunkNumber!=null && 
				chunkNumber.intValue()==1 &&
			    (bundleId!=null||inputServId!=null))
				prefetchNextChunks(rootCustNo,bundleId,inputServId,chunkSize,totalseats);
		} catch (BksDataException e) {
			logger.error(e.getMessage());
			serviceStatus = Status.PRECONDITION_ERROR;
			errorCode = e.getCode();
			errorText = e.getText();
			returnXml = populateOutputObject(null,null,null);
		} catch (BksFunctionalException e) {
			logger.error(e.getMessage());
			serviceStatus = Status.PRECONDITION_ERROR;
			errorCode = "BKS0008";
			errorText = e.getMessage();
			returnXml = populateOutputObject(null,null,null);
		} catch (XMLException e) {
			serviceStatus = Status.PRECONDITION_ERROR;
			errorCode = "BKS0023";
			errorText = e.getMessage();
			if (errorText != null){
			    int start = errorText.indexOf("[")+1;
			    int end = errorText.indexOf("]");
			    if (start >= 0 && end > start)
			    	errorText = errorText.substring(start, end).trim();
			}
			if (errorText.contains("Attribute 'ID' must appear on element")){
				String funcName = errorText.substring(errorText.lastIndexOf(" "), errorText.length());
				errorText = "ID for "+funcName+"-Function could not be retrieved. Verify bundle object";
			} 
			if (errorText != null && errorText.length() > 255)
				errorText = errorText.substring(0, 255);
			logger.error(errorText);
			returnXml = populateOutputObject(null,null,null);
		} catch (Exception e) {
			logger.error(e.getMessage());
			serviceStatus = Status.PRECONDITION_ERROR;
			errorCode = "BKS0008";
			logger.debug(e.getClass().toString());
			logger.debug("exception",e);
			if (e.getMessage() != null && e.getMessage().length() > 255)
				errorText = e.getMessage().substring(0, 255);
			else
				errorText = e.getMessage();
			e.printStackTrace();
			returnXml = populateOutputObject(null,null,null);
		}
		finally {
			if (dao != null)
				dao.closeConnection();
		}
		return returnXml;
	}
	
	/**
	 * This method will read the DEVIATING_CUSTOMER_NUMBER from the database and 
	 * add it to deviatingCustomerNumbers list.
	 */
	
	private void populateDeviatingCustomerInfo(ArrayList<HashMap<String, Object>> bundleItems2) {
		hierarchyIndicator = (String)bundleItems.get(0).get("HIERARCHY_INDICATOR");
		for(int i=0;i<bundleItems.size(); i++){
			String deviatCustomer = (String)bundleItems.get(i).get("DEVIATING_CUSTOMER_NUMBER");
			String serviceCode= (String)bundleItems.get(i).get("SERVICE_CODE");
			if (!isItemBKSRelevant(serviceCode))
				continue;
			if (!deviatingCustomerNumbers.contains(deviatCustomer))
				deviatingCustomerNumbers.add(deviatCustomer);
		}
	}

	private boolean isItemBKSRelevant(String serviceCode) {
		HashMap<String, HashMap<String, ArrayList<String[]>>> columnMap = BksRefDataCacheHandler.getSomTableColumnMap();
		Set<String> keys = columnMap.keySet();
		Iterator<String> keyiter = keys.iterator();
		while (keyiter.hasNext()) {
			String key = keyiter.next();
			if (key.endsWith(";"+serviceCode))
				return true;
		}
		return false;
	}
	
	private void validateHierarchy(String inputRoot) throws Exception {
		GetSomMasterDataDAO dao = 
			(GetSomMasterDataDAO)DatabaseClient.getBksDataAccessObject(null,"GetSomMasterDataDAO");
		if (customerNumber == null)
			return;
		ArrayList<HashMap<String, Object>> rootCustomer = dao.getRootCustomer(customerNumber);
		String rootCustNo = null;
		if (rootCustomer != null && rootCustomer.size() > 0) {
			rootCustNo  = (String) rootCustomer.get(0).get("ROOT_CUSTOMER_NUMBER");
		}
		if (rootCustNo==null || !rootCustNo.equals(inputRoot)){
			String text = "Customer "+customerNumber+" does not belong to hierarchy of "+inputRoot;
			throw new BksDataException("BKS0024",text);
		}
	}

	private boolean getFutureFlagForService(String inputServSubId) {
		for (int i =0;i<bundleItems.size();i++) {
			String servSubsId = bundleItems.get(i).get("SUPPORTED_OBJECT_ID").toString();
			if (servSubsId.equals(inputServSubId)) {
				String fInd = (String) bundleItems.get(i).get("FUTURE_INDICATOR");
				if (fInd == null || !fInd.equals("Y"))
					return false;
				else
					return true;
			}
		}
		return false;
	}

	private ProductBundle getUnbundledProductInfo(FetchProductBundleDAO dao,
			ArrayList<HashMap<String, Object>> services) throws Exception {
		ProductBundle bundleData = new ProductBundle();
		bundleData.setOrderPositionNumber(0);
		String servSubsId = services.get(0).get("SERVICE_SUBSCRIPTION_ID").toString();
		String serviceCode = services.get(0).get("SERVICE_CODE").toString();
		theHelper.setDesiredNewElement(0);
		String productCode = populateTableData(dao,servSubsId,serviceCode,bundleData);
		populateAccessNumbers(dao,servSubsId,serviceCode,null,bundleData);
		populateConfiguredValues(dao,servSubsId,serviceCode,null,bundleData,false);
		populateAddresses(dao,servSubsId,productCode,serviceCode,bundleData,false);
		populateServiceLocation(dao,servSubsId,serviceCode,bundleData);
		populateChildServiceList(dao,productCode,servSubsId,serviceCode,bundleData);
		populateDepHardwareList(dao,productCode,servSubsId,serviceCode,bundleData);
		
		
		
		if(bundleData.getAccesses() == null){						
			Accesses access = new Accesses(); 
			bundleData.setAccesses(access );					
		    OtherAccess oa = new OtherAccess();
			access.getOtherAccess().add(oa);
			oa.setID("A"+servSubsId);					
			CcbIdS value= new CcbIdS();
			value.setType("S");
			oa.setCcbId(value);
			value.setExisting(servSubsId);
			
			 // No access ref: DirectoryEntry, ExtraNumbers, Hardware, InstallationSvc, InstantAccess
		    // Access ref: Internet, Iptv, OtherFunction, Seat, TvCenter, Vod, Voice		   
		    // SafetyPackage - ContractFunction, no access ref.		    
			if(bundleData.getFunctions() != null ){
		        Functions funcs = bundleData.getFunctions();

		        if(funcs.getInternet() != null && 
		        		funcs.getInternet().size() > 0 ){
		        	List<Internet> internets = null;
		        	internets = bundleData.getFunctions().getInternet();
		        	for (int i=0;internets!=null&&i<internets.size();i++){
		        		Internet internet = internets.get(i);
		        		internet.setRefAccessID("A"+servSubsId);
		        	}	
		        }

		        if(funcs.getIptv() != null && 
		        		funcs.getIptv().size() > 0 ){
		        	List<Iptv> iptvs = null;
		        	iptvs = bundleData.getFunctions().getIptv();
		        	for (int i=0;iptvs!=null&&i<iptvs.size();i++){
		        		Iptv iptv = iptvs.get(i);
		        		iptv.setRefAccessID("A"+servSubsId);
		        	}	
		        }
		        
		        if(funcs.getOtherFunction() != null && 
		        		funcs.getOtherFunction().size() > 0 ){
		        	List<OtherFunction> otherFunctions = null;
		        	otherFunctions = bundleData.getFunctions().getOtherFunction();
		        	for (int i=0;otherFunctions!=null&&i<otherFunctions.size();i++){
		        		OtherFunction otherFunction = otherFunctions.get(i);
		        		otherFunction.setRefAccessID("A"+servSubsId);
		        	}	
		        }
		        
		        if(funcs.getSeat() != null && 
		        		funcs.getSeat().size() > 0 ){
		        	List<Seat> seats = null;
		        	seats = bundleData.getFunctions().getSeat();
		        	for (int i=0;seats!=null&&i<seats.size();i++){
		        		Seat seat = seats.get(i);
		        		seat.setRefAccessID("A"+servSubsId);
		        	}	
		        }
		    
		        if(funcs.getTvCenter() != null && 
		        		funcs.getTvCenter().size() > 0 ){
		        	List<TvCenter> tvCenters = null;
		        	tvCenters = bundleData.getFunctions().getTvCenter();
		        	for (int i=0;tvCenters!=null&&i<tvCenters.size();i++){
		        		TvCenter tvCenter = tvCenters.get(i);
		        		tvCenter.setRefAccessID("A"+servSubsId);
		        	}	
		        }		        
		        
		        if(funcs.getVod() != null && 
		        		funcs.getVod().size() > 0 ){
		        	List<Vod> vods = null;
		        	vods = bundleData.getFunctions().getVod();
		        	for (int i=0;vods!=null&&i<vods.size();i++){
		        		Vod vod = vods.get(i);
		        		vod.setRefAccessID("A"+servSubsId);
		        	}	
		        }
	      
		        if(funcs.getVoice() != null && 
		        		funcs.getVoice().size() > 0 ){
		        	List<Voice> voices = null;
		        	voices = bundleData.getFunctions().getVoice();
		        	for (int i=0;voices!=null&&i<voices.size();i++){
		        		Voice voice = voices.get(i);
		        		voice.setRefAccessID("A"+servSubsId);
		        	}	
		        }
			}										
		}
		
		
		return bundleData;
	}

	private void addSkeletonContractNode(Order order,ArrayList<HashMap<String, Object>> contracts,String tag) throws Exception {
		String skContractNumber = null;
		if (contracts!=null && contracts.size() > 0)
			skContractNumber=(String)contracts.get(0).get(tag);
		if (skContractNumber== null)
			return;
		GetSomMasterDataHandler mdh = new GetSomMasterDataHandler();
		mdh.populateSkeletonOnOrder(order, skContractNumber);
	}

	private void getOpenOrdersForOrderId(FetchProductBundleDAO dao,
			String orderId, int orderPosNo, HashMap<String,Order> openOrders) throws Exception {
		order = getOpenOrders(orderId,new BigDecimal(orderPosNo));
		
		if (order!=null && 0 < order.getOrderPosition().size()) {
			OrderPositionType opt = order.getOrderPosition().get(0).getValue();
			if (opt instanceof LineCreation && opt.getOrderPositionNumber() == orderPosNo){
				if (openOrders.get(orderId)==null)
					openOrders.put(orderId,new Order());
				openOrders.get(orderId).getOrderPosition().add(order.getOrderPosition().get(0));
				openOrders.get(orderId).setBarcode(order.getBarcode());
			}
		} else {
			throw new BksFunctionalException("Error fetching order "+orderId+" from COM.");
		}
	}

	private void getOpenOrdersForBundle(FetchProductBundleDAO dao, ArrayList<String> subIds,
			String customerNumber, HashMap<String,Order> openOrders) throws Exception {
		ArrayList<String> orderIds = new ArrayList<String>();
		for (int i = 0; i < subIds.size(); i++) {
			ArrayList<HashMap<String, Object>> orders = dao.getOpenOrdersForService(subIds.get(i));
			if (orders == null || orders.size() == 0)
				continue;
			for (int j = 0; j < orders.size(); j++) {
				String orderId = (String)orders.get(j).get("ORDER_ID");
				BigDecimal orderPosNo = (BigDecimal)orders.get(j).get("ORDER_POSITION_NUMBER");
				if (orderIds.contains(orderId+";"+orderPosNo))
					continue;
				orderIds.add(orderId+";"+orderPosNo);
				Order openOrder = getOpenOrders(orderId,orderPosNo);
				if  (openOrder != null && 0 < openOrder.getOrderPosition().size() && 
					openOrder.getOrderPosition().get(0) != null) {
					OrderPositionType opt = openOrder.getOrderPosition().get(0).getValue();
					if (opt instanceof LineChange || opt instanceof Termination ||
						opt instanceof Relocation || opt instanceof ProviderChange ||
						opt instanceof LineCreation){
						if (openOrders.get(orderId)==null)
							openOrders.put(orderId,new Order());
						openOrders.get(orderId).getOrderPosition().add(openOrder.getOrderPosition().get(0));
						openOrders.get(orderId).setBarcode(openOrder.getBarcode());
					}
				}
			}
		}
	}

	private Order getOpenOrders(String orderId,BigDecimal orderPosId) throws Exception{
		ApplicationContext ac = DatabaseClient.getAc();
		if (ac == null)
			throw new MCFException("Application context has not been initialized.");
		Object order = null;
		int timeToLive = 10000;
		int timeOut = 100000;
		try {
			timeToLive = Integer.parseInt(DatabaseClientConfig.getSetting("servicebusclient.TimeToLive"));
			timeOut = Integer.parseInt(DatabaseClientConfig.getSetting("servicebusclient.TimeOut"));
		} catch (Exception e1) {
			// Ignore (Use Default)
		}
		try {
			BksServiceHandler bksHandler = (BksServiceHandler)ac.getBean("bksService");
			messageSender = bksHandler.getMessageSender();
			GetOrderDetailsRequest request = new GetOrderDetailsRequest();
			request.setOrderID(orderId);
			request.setOrderPositionNumber(orderPosId);
			request.setOrderOutputFormat(OrderOutputFormat.SOM);
			request.setReturnUnenrichedOrder(true);
			if (clientSchemaVersion != null){
				//Map BSS release to SOM release
				String[] parts = clientSchemaVersion.split("\\.");
				Double bssRelease = Double.valueOf(parts[0]);
				Double somRelease = ((bssRelease+30.0)/10.0);
				request.setSomVersion(somRelease.toString());
			}
			ServiceRequestSoap<GetOrderDetailsRequest> serviceRequest =
				new ServiceRequestSoap<GetOrderDetailsRequest>(request,
						"classpath:schema/COM-PROV-001.xsd",
				"de.vodafone.esb.service.sbus.com.com_prov_001");
			serviceRequest.setOperationName("/COM-001/GetOrderDetails");		
			serviceRequest.setTimeToLive(timeToLive);
			ServiceResponse<GetOrderDetailsResponse> respServ = messageSender.sendSyncSoapRequest(serviceRequest, timeOut);
			GetOrderDetailsResponse resp = respServ.getPayload();
			Node xml = (Node)resp.getOrderDetails().getAny();
//			DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
//			DOMImplementationLS impl = 
//			    (DOMImplementationLS)registry.getDOMImplementation("LS");
//			LSSerializer writer = impl.createLSSerializer();
//			String text = writer.writeToString(xml);
			TransformerFactory transFactory = TransformerFactory.newInstance();
			Transformer transformer = transFactory.newTransformer();
			StringWriter buffer = new StringWriter();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.transform(new DOMSource(xml),
			      new StreamResult(buffer));
			String text = buffer.toString();
			XmlProcessor xmlProcessor = (XmlProcessor) ac.getBean("mcf2XmlProcessor");
			text = makeNodeIdsUnique(text);
			Object order1 = xmlProcessor.deserialize(text, "schema/som-order.xsd", "net.arcor.orderschema");
			if (order1!=null)
				order = ((JAXBElement)order1).getValue();
		} catch (Exception e) {
			logger.error("Call to Com failed. Message: "+e.getMessage());
			e.printStackTrace();
		}
		return (Order)order;

	}
	
	private String makeNodeIdsUnique(String text) {
		int index = text.indexOf("orderPositionNumber=\"",0);
		while ((index=text.indexOf(" ID=\"",index)) > 0){
			index = index+" ID=\"".length();
			int endIndex = text.indexOf("\"",index);
			String source = text.substring(index,endIndex);
			
			String replace;
			replace = "UNI_" + suffix;
			text = text.replaceAll("\""+source+"\"", "\""+replace+"\"");
			text = text.replaceAll(">"+source+"<", ">"+replace+"<");
			index = endIndex +1 ;
			suffix++;
		}
		return text;
	}

	private Order getCustomerData(FetchProductBundleDAO dao, 
			String resellerId, ArrayList<String> accounts, ArrayList<String> sdContractNumbers) throws Exception{
		GetSomMasterDataHandler mdh = new GetSomMasterDataHandler();
		mdh.setClientSchemaVersion(clientSchemaVersion);
		Order order = null;
		   order = mdh.getOrder(customerNumber,resellerId,accounts,sdContractNumbers,true,false);
	   return order;
	}

	private ProductBundle getProductBundlesInfo(FetchProductBundleDAO dao,
			ArrayList<HashMap<String,Object>> bundleItems, CcbIdB bundleId, boolean aoFlag, String accountNo) throws Exception {
		ProductBundle bundleData = new ProductBundle();
		bundleId.setType("B");
		bundleData.setCcbId(bundleId);
		
		bundleData.setAoMastered(new ExistConfBoolean());
		bundleData.getAoMastered().setExisting(aoFlag);
		if (bundleItems != null &&  bundleItems.size() > 0){
			String bundleType = (String)  bundleItems.get(0).get("BUNDLE_TYPE_RD");
			bundleData.setOrderPositionNumber(0);
			bundleData.setProductBundleType(bundleType);
			//bundleData.setLocationAddress(getLocationAddress(dao,bundleItems.get(0)));
			populateSalesPackageInfo(dao,bundleId.getExisting(),bundleData);
			Beneficiary benef = new Beneficiary();
			CcbIdC custNum = new CcbIdC();
			custNum.setExisting(customerNumber);
			benef.setCcbId(custNum);
			benef.setCustomerNodeRefID("C"+customerNumber);
			PayerAllCharges account = new PayerAllCharges();
			account.setBillingAccountNodeRefID("AC"+accountNo);
			bundleData.setPayerAllCharges(account);
			//benef.setClassification((String)bundleItems.get(0).get("CLASSIFICATION_RD"));
			bundleData.setBeneficiary(benef);
			//bundleData.setProviderCode(value);
			populateFunctiosAndAccesses(dao,bundleItems,bundleData);
			
			GetSomMasterDataHandler mdh = new GetSomMasterDataHandler();
			ArrayList<Access> consAccesses = new ArrayList<Access>();
			consolidateAccesses(bundleData,consAccesses);
			for (int j = 0; j < consAccesses.size(); j++) {
				Access access = consAccesses.get(j);
				addInstallContactRoleRef(access.getID());
				List<ContactRole> contactRole = order.getCustomerData().get(0).getContactRole();
				access.setContactRoleRefIdList(new ContactRoleRefIdList());
				mdh.associateRelevantContactIds(access.getID(), contactRole,
						access.getContactRoleRefIdList().getContactRoleRef());
			}

		}
		return bundleData;
	}
	void addInstallContactRoleRef(String supportedNodeId){
		List<ContactRole> contactRole = order.getCustomerData().get(0).getContactRole();
		ContactRole installCr = null;
		for (int i= 0;contactRole!=null&&i<contactRole.size();i++){
			if (contactRole.get(i).getID().equals("INSTALLATION")){
				installCr = contactRole.get(i);
				break;
			}
		}
			
		if (installCr != null) {
			AssociatedObjectReference value = new AssociatedObjectReference();
			CcbIdAny ccbId = new CcbIdAny();
			ccbId.setExisting(supportedNodeId.substring(1));
			ccbId.setType("S");
			value.setCcbId(ccbId);
			value.setNodeRefId(supportedNodeId);
			installCr.getAssociatedObjectReference().add(value);
		}
	}

	private void consolidateAccesses(ProductBundle bundleData,
			ArrayList<Access> consAccesses) {
		if(bundleData.getAccesses()!=null){
			consAccesses.addAll(bundleData.getAccesses().getBusinessDSL());
			consAccesses.addAll(bundleData.getAccesses().getCompanyNet());
			consAccesses.addAll(bundleData.getAccesses().getDslr());
			consAccesses.addAll(bundleData.getAccesses().getIpBitstream());
			consAccesses.addAll(bundleData.getAccesses().getIpCentrex());
			consAccesses.addAll(bundleData.getAccesses().getIsdn());
			consAccesses.addAll(bundleData.getAccesses().getLte());
			consAccesses.addAll(bundleData.getAccesses().getMobile());
			consAccesses.addAll(bundleData.getAccesses().getNgn());
			consAccesses.addAll(bundleData.getAccesses().getPreselect());
			consAccesses.addAll(bundleData.getAccesses().getSip());
		}else{
			Accesses access = new Accesses(); 
			bundleData.setAccesses(access );
			if(bundleData.getFunctions().getInternet() != null && bundleData.getFunctions().getInternet().size()>0){
				Internet internet = bundleData.getFunctions().getInternet().get(0);
				String id=internet.getID();
				if(!id.equals("")){
					internet.setRefAccessID("A"+id.substring(1));
					OtherAccess oa = new OtherAccess();
					access.getOtherAccess().add(oa);
					oa.setID(internet.getRefAccessID());					
					CcbIdS value= new CcbIdS();
					value.setType("S");
					oa.setCcbId(value);
					value.setExisting(id.substring(1));
				}				
			}
		}
	}

	private void populateFunctiosAndAccesses(FetchProductBundleDAO dao,
			ArrayList<HashMap<String, Object>> bundleItems,	ProductBundle bundleData) throws Exception {
		String siteServiceId=null;
		int currentSeat=0;
		String voiceServiceId = null;
		for (int i =0;i<bundleItems.size();i++) {
			String fInd = (String) bundleItems.get(i).get("FUTURE_INDICATOR");
			if ((fInd != null && fInd.equals("Y") && !bFetchFuture) ||
				(fInd != null && fInd.equals("T") && bFetchFuture))
				continue;

			String servSubsId = bundleItems.get(i).get("SUPPORTED_OBJECT_ID").toString();
			String bundleItemType = bundleItems.get(i).get("BUNDLE_ITEM_TYPE_RD").toString();
			if (subIds.contains(servSubsId))
				continue;
			if (bundleItemType.equals("IPCENTREX_SITE"))
				siteServiceId=servSubsId;
			subIds.add(servSubsId);
			String serviceCode = bundleItems.get(i).get("SERVICE_CODE").toString();
			if (serviceCode.equals("V0003") || serviceCode.equals("V0010"))
				voiceServiceId = servSubsId;
			if (chunkSize!=null&&chunkNumber!=null&&
				Arrays.asList(seatServices).contains(serviceCode)){
				if(currentSeat<(chunkNumber.intValue()-1)*chunkSize.intValue() || 
				   currentSeat>=chunkNumber.intValue()*chunkSize.intValue()){
					currentSeat++;
					continue;
				}
				currentSeat++;
				lastchunk =(currentSeat>=totalseats);
			}
			theHelper.setDesiredNewElement(0);
			boolean bOnlineService = bundleItemType.equals("ONLINE_SERVICE") || bundleItemType.equals("DSLONL_SERVICE");
			if (bOnlineService)
				theHelper.setDesiredNewElement(-1);
			if (bundleItemType.equals("MOBILE_SERVICE")) {
				bFirstAccess = true;
				bFirstFunction = true;
			} else {
				bFirstAccess = false;
				bFirstFunction = false;
			}
			String productCode = populateTableData(dao,servSubsId,serviceCode,bundleData);
			if (bOnlineService)
				populateSelectedDestinationRef(dao,servSubsId,voiceServiceId,bundleData);
			populateAccessNumbers(dao,servSubsId,serviceCode,null,bundleData);
			if (!Arrays.asList(seatServices).contains(serviceCode)){
				populateConfiguredValues(dao,servSubsId,serviceCode,null,bundleData,false);
				populateAddresses(dao,servSubsId,productCode,serviceCode,bundleData,false);
				populateServiceLocation(dao,servSubsId,serviceCode,bundleData);
				populateChildServiceList(dao,productCode,servSubsId,serviceCode,bundleData);
				populateDepHardwareList(dao,productCode,servSubsId,serviceCode,bundleData);
			} 
		}
 
		Internet internet = null;
	    if(bundleData.getFunctions().getInternet()!=null && bundleData.getFunctions().getInternet().size()>0){
	    	internet = bundleData.getFunctions().getInternet().get(0);
	    }
	    	
		
		if (internet != null){
			if (internet.getAdslInternetConfiguration() != null){
			    if (internet.getAdslInternetConfiguration().getUpstreamBandwidth() == null)	 {
			    	ExistConfString100 standard = new ExistConfString100();
			    	standard.setExisting("Standard");
					internet.getAdslInternetConfiguration().setUpstreamBandwidth(standard);
			    }
			}
		}
		
		DirectoryEntry de = null;
	    if(bundleData.getFunctions().getDirectoryEntry()!=null && bundleData.getFunctions().getDirectoryEntry().size()>0){
	    	de = bundleData.getFunctions().getDirectoryEntry().get(0);
	    	if (de.getDirectoryEntryConfiguration()!=null){
	    		ExistConfAddress la = de.getDirectoryEntryConfiguration().getListedAddress();
	    		if (la!=null&&la.getExisting()!=null&&
		    		(la.getExisting().getPostalCode()==null|| la.getExisting().getPostalCode().length() == 0))
	    			de.getDirectoryEntryConfiguration().setListedAddress(null);
	    	}
	    }

		List<Seat> seats = null;
		if (bundleData.getFunctions() != null)
			seats = bundleData.getFunctions().getSeat();
		for (int i=0;seats!=null&&i<seats.size();i++){
			Seat seat = seats.get(i);
			seat.setRefAccessID("A"+siteServiceId);
		}	
	}

	private void populateSelectedDestinationRef(FetchProductBundleDAO dao,
			String servSubsId, String voiceServiceId, ProductBundle bundleData) throws Exception {
		ArrayList<HashMap<String, Object>> dialupSds = dao.getDialupSelectedDestination(servSubsId);
		List<Internet> internet = bundleData.getFunctions().getInternet();
		if (dialupSds!=null && dialupSds.size()>0) {
			String targetVoice = (String) dialupSds.get(0).get("SERVICE_SUBSCRIPTION_ID");
			ExistConfStringId value = new ExistConfStringId();
			if (targetVoice!=null && targetVoice.equals(voiceServiceId) && internet.size() > 0){
				value.setExisting("F"+voiceServiceId);
				internet.get(0).getAdslInternetConfiguration().setSelectedDestinationsRefId(value);
			}
		}
			
		
	}

	private void populateAccessNumbers(FetchProductBundleDAO dao,
			String servSubsId,String serviceCode, String parentServiceCode,ProductBundle bundleData) throws Exception {
		ArrayList<HashMap<String, Object>> accessNumbers = dao.getAccessNumberChars(servSubsId,null,null);
		if (accessNumbers == null)
			return;
		HashMap<String,HashMap<Integer,Object[]>> charMap = null;
		if (parentServiceCode == null)
			charMap = BksRefDataCacheHandler.getSomServiceFieldMap().get(serviceCode);
		else
			charMap = BksRefDataCacheHandler.getSomServiceFieldMap().get(serviceCode+";"+parentServiceCode);
		for (int i =0;charMap!=null&&i<accessNumbers.size();i++) {
			String serviceCharCode = (String) accessNumbers.get(i).get("SERVICE_CHARACTERISTIC_CODE");
			String number = (String) accessNumbers.get(i).get("ACCESS_NUMBER");
			theHelper.setDesiredNewElement(1);
			if (number != null && charMap.get(serviceCharCode) != null){
				HashMap<Integer,Object[]> fieldPathMap = charMap.get(serviceCharCode);
				String[] numberFields = number.split(";");
				for(Integer j=0;j<numberFields.length;j++){
					Object[] paths = fieldPathMap.get(j+1);
						for (int k = 0; paths!=null&&k < paths.length; k++) {
							String[] fields = (String[]) paths[k];
							String path = fields[0];
							String conversionMethod = fields[2];
							if (path.startsWith("Accesses"))
								theHelper.setDesiredNewElement(-1);
							if (numberFields[j].startsWith("DEL"))
								theHelper.setAttributeForPath(bundleData, "set",
										path, numberFields[j].substring(3));
							else {
								if (conversionMethod != null) {
									Object converted = theHelper.invokeMethod(theHelper,
											conversionMethod, numberFields[j], String.class);
									if (converted!=null)
										theHelper.setAttributeForPath(bundleData, "set",path, converted);
								} else {
									theHelper.setAttributeForPath(bundleData, "set",path, numberFields[j]);
								}
							}
						}
				}
			}
		}
	}

	private void populateServiceLocation(FetchProductBundleDAO dao,
			String servSubsId, String serviceCode, ProductBundle bundleData) throws Exception {
		ArrayList<HashMap<String, Object>> servLocs = dao.getServiceLocation(servSubsId);
		if (servLocs == null)
			return;
		HashMap<String,HashMap<String,Object>> charMap = BksRefDataCacheHandler.getSomServiceColumnMap().get(serviceCode);
		for (int i =0;charMap!=null&&i<servLocs.size();i++) {
			String serviceCharCode = (String) servLocs.get(i).get("SERVICE_CHARACTERISTIC_CODE");
			if (charMap.get(serviceCharCode) != null){
				Set<String> keys = charMap.get(serviceCharCode).keySet();
				Iterator<String> keyiter = keys.iterator();
				while (keyiter.hasNext()){
					String columnName = keyiter.next();
					String columnValue = (String) servLocs.get(i).get(columnName);
					Object valueObj = charMap.get(serviceCharCode).get(columnName);
					theHelper.setAttributeForPath(bundleData, "set", ((String[])valueObj)[0], columnValue);
				}
			}
		}
	}

	private void populateConfiguredValues(FetchProductBundleDAO dao,
			String servSubsId, String serviceCode, String parentServiceCode, ProductBundle bundleData, boolean bIsRented) throws Exception {
		HashMap<String,ArrayList<String[]>> charPath = null;
		if (parentServiceCode != null)
			charPath = BksRefDataCacheHandler.getSomServiceCharMap().get(serviceCode+";"+parentServiceCode);
		if(charPath == null)
			charPath = BksRefDataCacheHandler.getSomServiceCharMap().get(serviceCode);
		
		ArrayList<HashMap<String, Object>> cscs = dao.getConfigCharacteristics(servSubsId);
		if (charPath!=null && charPath.get("-") != null) {
			for (int j = 0; j < charPath.get("-").size(); j++) {
				String conversionMethod = charPath.get("-").get(j)[2];
				String depCharCode = charPath.get("-").get(j)[4];
				String defaultValue = charPath.get("-").get(j)[5];
				if (!isDependentCharPopulate(cscs,depCharCode))
					continue;
				String path=charPath.get("-").get(j)[0];
				if (bIsRented)
					path=path.replaceAll("HardwareConfiguration", "RentedHardwareConfiguration");
				if (conversionMethod == null)
					theHelper.setAttributeForPath(bundleData, "set",path, defaultValue);
				else if (conversionMethod != null) {
					Object converted = theHelper.invokeMethod(theHelper,
							conversionMethod, defaultValue, String.class);
					theHelper.setAttributeForPath(bundleData, "set",path, converted);
				}
			}
		}
		ArrayList<HashMap<String, Object>> configValues = dao.getConfiguredValues(servSubsId,null);
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
					if (columnValue == null || (serviceCharCode.equals("V0112") && !isNummeric(columnValue)))
						columnValue=defaultValue;
					if (columnValue == null || !isDependentCharPopulate(cscs,depCharCode))
						continue;
					String charValue = columnValue;
					if (fieldNumber != null){
						String[] fields = charValue.split(";");
						if (fields.length >= Integer.parseInt(fieldNumber))
							charValue = fields[Integer.parseInt(fieldNumber)-1];
						else
							continue;
					}
					charValue=charValue.trim();
					String path = charPath.get(serviceCharCode).get(j)[0];
					if (bIsRented)
						path=path.replaceAll("HardwareConfiguration", "RentedHardwareConfiguration");
					if (rdsId == null && conversionMethod == null)
						theHelper.setAttributeForPath(bundleData, "set", path, charValue);
					if (rdsId != null) {
						Object converted = theHelper.fetchRefData(charValue, rdsId);
						theHelper.setAttributeForPath(bundleData, "set", path, converted);
					}
					if (conversionMethod != null) {
						Object converted = theHelper.invokeMethod(theHelper,
								conversionMethod, charValue, String.class);
						if (converted==null)
							converted=defaultValue;
						if (converted!=null)
							theHelper.setAttributeForPath(bundleData, "set", path, converted);
					}
				}
			}
		}
	}

	private boolean isNummeric(String columnValue) {
		try {
			Integer.parseInt(columnValue);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	private boolean isDependentCharPopulate(
			ArrayList<HashMap<String, Object>> cscs, String depCharCode) {
		if (depCharCode==null)
			return true;
		ArrayList<String> charList = new ArrayList<String>();
		for (int i = 0;i < cscs.size(); i++) {
			String serviceCharCode = (String) cscs.get(i).get("SERVICE_CHARACTERISTIC_CODE");
			charList.add(serviceCharCode);
		}
		String[] depCharCodes = depCharCode.split(";");
		for (int i = 0; i < depCharCodes.length; i++) {
			if (!charList.contains(depCharCodes[i])) {
				return false;
			}
		}
		return true;
	}

	private void populateAddresses(FetchProductBundleDAO dao, String servSubsId,
					String productCode, String serviceCode, ProductBundle bundleData, boolean bIsRented) throws Exception {
		HashMap<String,HashMap<String,Object>> charMap = BksRefDataCacheHandler.getSomServiceColumnMap().get(productCode+";"+serviceCode);
		if (charMap==null)
			charMap = BksRefDataCacheHandler.getSomServiceColumnMap().get("-;"+serviceCode);
		if (charMap==null)
			return;
		if (charMap.get("-") != null) {
			Customer customer = order.getCustomerData().get(0).getCustomer();
			if (customer.getIndividual()!=null) {
				String path = ((String[])charMap.get("-").get("ADDRESS"))[0];
				ExistConfAddress addr = customer.getIndividual().getAddress();
				if (addr!=null)
					theHelper.setAttributeForPath(bundleData, "set", path, addr);
				path = ((String[])charMap.get("-").get("NAME"))[0];
				String name = customer.getIndividual().getName().getExisting();
				theHelper.setAttributeForPath(bundleData, "set", path, name);
				path = ((String[])charMap.get("-").get("FORENAME"))[0];
				String forename = null;
				if (customer.getIndividual().getForename()!= null &&
					customer.getIndividual().getForename().getExisting()!=null)
					forename = customer.getIndividual().getForename().getExisting();
				if (forename!=null)
					theHelper.setAttributeForPath(bundleData, "set", path, forename);
			} else if (customer.getOrganization()!=null) {
				String path = ((String[])charMap.get("-").get("ADDRESS"))[0];
				ExistConfAddress addr = customer.getOrganization().getAddress();
				if (addr!=null)
					theHelper.setAttributeForPath(bundleData, "set", path, addr);
				path = ((String[])charMap.get("-").get("NAME"))[0];
				String name = customer.getOrganization().getName().getExisting();
				theHelper.setAttributeForPath(bundleData, "set", path, name);
			}
		}
			
		ArrayList<HashMap<String, Object>> cscs = dao.getConfigCharacteristics(servSubsId);
		ArrayList<HashMap<String, Object>> addresses = dao.getAddress(servSubsId);
		if (addresses == null)
			return;
		for (int i =0;i<addresses.size();i++) {
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
						if (columnValue == null || !isDependentCharPopulate(cscs,depCharCode))
							continue;
						populateSingleColumn((String[])valueObj,columnValue,bundleData,bIsRented);
					} else {
						ArrayList<String[]> list = (ArrayList<String[]>)valueObj;
						for (int j = 0; list != null && j < list.size(); j++) {
							String[] item = list.get(j);
							String depCharCode = item[3];
							if (columnValue == null || !isDependentCharPopulate(cscs,depCharCode))
								continue;
							populateSingleColumn(item,columnValue,bundleData,bIsRented);
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
					if (value.length() > 10)
						theHelper.setAttributeForPath(bundleData, "set", path, value.substring(0,10));
					else
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

	private String populateTableData(FetchProductBundleDAO dao, String servSubsId, String serviceCode, ProductBundle bundleData) throws Exception {
		ArrayList<HashMap<String,Object>> contract = dao.getContractData(servSubsId);
		String productCode = null;
		if (contract == null || contract.size() == 0){
			contract = dao.getSDContractData(servSubsId);
			addSkeletonContractNode(order,contract,"SERV_DELIV_SKELETON_NUMBER");
		} else {
			addSkeletonContractNode(order,contract,"ASSOC_SKELETON_CONTRACT_NUMBER");
		}
		if (contract != null && contract.size() > 0) {
			productCode = contract.get(0).get("PRODUCT_CODE").toString();
			String prodSubsId = contract.get(0).get("PRODUCT_SUBSCRIPTION_ID").toString();
			HashMap<String,ArrayList<String[]>> tableMap = 
				BksRefDataCacheHandler.getSomTableColumnMap().get(productCode+";"+serviceCode);
			if (tableMap == null)
				tableMap = 
					BksRefDataCacheHandler.getSomTableColumnMap().get("-;"+serviceCode);
			if (tableMap == null)
				return productCode;
			Set<String> keys = tableMap.keySet();
			Iterator<String> keyiter = keys.iterator();
			while (keyiter.hasNext()){
				String columnName = keyiter.next();
				if (columnName.equals("-")){
					for (int i = 0; i < tableMap.get(columnName).size(); i++) {
						String path = (String) tableMap.get(columnName).get(i)[0];
						theHelper.setAttributeForPath(bundleData, "set", path, tableMap.get(columnName).get(i)[1].toString());
					}
					continue;
				}
				for (int i = 0; i < tableMap.get(columnName).size(); i++) {
					if (columnName.equals("CONTRACT_END_DATE")){
						String path = (String) tableMap.get(columnName).get(i)[0];
						Object startDate = contract.get(0).get("MIN_PERIOD_START_DATE");
						String minUnit = (String)contract.get(0).get("MIN_PERIOD_DUR_UNIT_RD");
						Integer minValue = null;
						if (contract.get(0).get("MIN_PERIOD_DUR_VALUE") != null) 
							minValue = ((BigDecimal) contract.get(0).get("MIN_PERIOD_DUR_VALUE")).intValue(); 
						String durationUnit = (String)contract.get(0).get("AUTO_EXTENT_DUR_UNIT_RD");
						Integer extValue = null;
						if (contract.get(0).get("AUTO_EXTENT_DUR_VALUE") != null)
							extValue = ((BigDecimal)contract.get(0).get("AUTO_EXTENT_DUR_VALUE")).intValue();
						boolean bAutoExtend = false;
						if (contract.get(0).get("AUTO_EXTENSION_IND") != null)
							bAutoExtend = ((String)contract.get(0).get("AUTO_EXTENSION_IND")).equals("Y");
						Object converted = 
							theHelper.getContractEndDate(startDate, minUnit, minValue, durationUnit, extValue, bAutoExtend);
						theHelper.setAttributeForPath(bundleData, "set", path, converted);
					} else {
						String columnValue = null;
						String defaultValue = tableMap.get(columnName).get(i)[5];
						if (tableMap.get(columnName).get(i)[3] != null){
							columnValue = getMainBundleAccess(columnName,tableMap.get(columnName).get(i)[3]);
						} else if (contract.get(0).get(columnName)!=null)
							columnValue = contract.get(0).get(columnName).toString();
						if (columnValue == null)
							columnValue = defaultValue;
						if (columnValue == null)
							break;
						if (tableMap.get(columnName).get(i)[2] != null) {
							columnValue = tableMap.get(columnName).get(i)[2] + columnValue;
						}
						String conversionMethod = tableMap.get(columnName).get(i)[1];
						String introVersion = tableMap.get(columnName).get(i)[4];
						if (introVersion != null && clientSchemaVersion != null &&
							theHelper.isClientVersLowerThanIntro(introVersion,clientSchemaVersion))
							continue;
						String path = (String) tableMap.get(columnName).get(i)[0];
						if (path.startsWith("Accesses") && bFirstAccess){
							theHelper.setDesiredNewElement(0);
							bFirstAccess=false;
						}
						if (path.startsWith("Functions") && bFirstFunction){
							theHelper.setDesiredNewElement(0);
							bFirstFunction=false;
						}
						if (conversionMethod != null) {
							Object converted = theHelper.invokeMethod(theHelper,
									conversionMethod, columnValue, String.class);
							theHelper.setAttributeForPath(bundleData, "set", path, converted);
						} else {
							theHelper.setAttributeForPath(bundleData, "set", path, columnValue);
						}
					}
				}
			}
			populateSelectedDestination(dao,prodSubsId,serviceCode,bundleData);
		}
		return productCode;
	}

	private void populateSelectedDestination(FetchProductBundleDAO dao,
			String prodSubsId, String serviceCode, ProductBundle bundleData) throws Exception {
		HashMap<String, ArrayList<String[]>> tableMap = BksRefDataCacheHandler.getSomTableColumnMap().get("SDS;"+serviceCode);
		if (tableMap == null)
			return;
		ArrayList<HashMap<String, Object>> sds = dao.getSelectedDestinations(prodSubsId);
		for (int i=0 ; sds != null && sds.size() > i;i++) {
			Set<String> keys = tableMap.keySet();
			Iterator<String> keyiter = keys.iterator();
			while (keyiter.hasNext()) {
				String columnName = keyiter.next();
				for (int j = 0; j < tableMap.get(columnName).size(); j++) {
					String columnValue = null;
					if (tableMap.get(columnName).get(j)[3] != null) {
						columnValue = getMainBundleAccess(columnName,tableMap.get(columnName).get(j)[3]);
					} else if (sds.get(i).get(columnName) != null)
						columnValue = sds.get(i).get(columnName).toString();
					if (columnValue == null)
						break;
					if (tableMap.get(columnName).get(j)[2] != null) {
						columnValue = tableMap.get(columnName).get(j)[2]+ columnValue;
					}
					String conversionMethod = tableMap.get(columnName).get(j)[1];
					if (conversionMethod != null) {
						Object converted = theHelper.invokeMethod(
								theHelper, conversionMethod,
								columnValue, String.class);
						theHelper.setAttributeForPath(bundleData,
								"set",
								tableMap.get(columnName).get(j)[0],
								converted);
					} else {
						theHelper.setAttributeForPath(bundleData,
								"set",
								tableMap.get(columnName).get(j)[0],
								columnValue);
					}
				}
			}
		}
	}

	private String getMainBundleAccess(String columnName, String matchingTypes) {
		for (int i =0;bundleItems!=null&&i<bundleItems.size();i++) {
			String bundleItemType = bundleItems.get(i).get("BUNDLE_ITEM_TYPE_RD").toString();
			String[] types = matchingTypes.split(";");
			for (int j = 0; j < types.length; j++) {
				if (bundleItemType.equals(types[j]))
					return bundleItems.get(i).get(columnName).toString();
			}
		}
		return null;
	}

	private void populateChildServiceList(FetchProductBundleDAO dao,String productCode,
			String parentServSubsId, String parentServiceCode, ProductBundle productBundle) throws Exception {
		ArrayList<HashMap<String, Object>> services = dao.getChildServices(parentServSubsId,null);
		if (services == null || services.size() == 0)
			return;
		populateDependentFunctions(dao,services,productCode,parentServiceCode,productBundle,false,parentServSubsId,false);
		populateSubscriptionExistance(dao,services,productCode,parentServiceCode,productBundle);
	}
	
	private void populateDepHardwareList(FetchProductBundleDAO dao,String productCode,
			String parentServSubsId, String parentServiceCode, ProductBundle productBundle) throws Exception {
		ArrayList<HashMap<String, Object>> services = dao.getHardwareDepService(parentServSubsId);
		if (services == null || services.size() == 0)
			return;
		populateDependentFunctions(dao,services,productCode,parentServiceCode,productBundle,true,null,false);
	}
	

	private void populateDependentFunctions(FetchProductBundleDAO dao,
			ArrayList<HashMap<String, Object>> services, String productCode,
			String parentServiceCode, ProductBundle productBundle, boolean bIsHardware,String parentServSubsId, boolean bIsParentRented) throws Exception {
		for (int i =0;i<services.size();i++) {
			String serviceCode = services.get(i).get("SERVICE_CODE").toString();
			String servSubsId = services.get(i).get("SERVICE_SUBSCRIPTION_ID").toString();
			boolean bIsRented=false;
			if (bIsHardware)
				bIsRented=isHardwareRented(dao,servSubsId);
			theHelper.setDesiredNewElement(1);
			if (bIsHardware || Arrays.asList(depFunctionServices).contains(serviceCode))
				theHelper.setDesiredNewElement(0);
			HashMap<String, ArrayList<String[]>> tableColMap = null;
			tableColMap = BksRefDataCacheHandler.getSomTableColumnMap().get(productCode+";"+serviceCode+";"+parentServiceCode);
			if (tableColMap == null) 
				tableColMap = BksRefDataCacheHandler.getSomTableColumnMap().get(productCode+";"+serviceCode);
			if (tableColMap == null) 
				tableColMap = BksRefDataCacheHandler.getSomTableColumnMap().get("-;"+serviceCode+";"+parentServiceCode);
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
							if (bIsRented||bIsParentRented)
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
						String conversionMethod = tableColMap.get(columnName).get(j)[1];
						String introVersion = tableColMap.get(columnName).get(j)[4];
						if (introVersion != null && clientSchemaVersion != null &&
						theHelper.isClientVersLowerThanIntro(introVersion,clientSchemaVersion))
							continue;
						String path=tableColMap.get(columnName).get(j)[0];
						if (bIsRented||bIsParentRented)
							path=path.replaceAll("HardwareConfiguration", "RentedHardwareConfiguration");
						if (conversionMethod != null) {
							Object converted = theHelper.invokeMethod(
									theHelper, conversionMethod, columnValue,
									String.class);
							theHelper.setAttributeForPath(productBundle, "set",path, converted);
						} else {
							theHelper.setAttributeForPath(productBundle, "set", path, columnValue);
						}
					}
				}
			}
			populateConfiguredValues(dao,servSubsId,serviceCode,parentServiceCode,productBundle,bIsRented||bIsParentRented);
			if (serviceCode.equals("V8042"))
				populateConfiguredValues(dao,parentServSubsId,serviceCode,parentServiceCode,productBundle,bIsRented);
			populateAddresses(dao,servSubsId,productCode,serviceCode,productBundle,bIsRented);
			populateAccessNumbers(dao,servSubsId,serviceCode,parentServiceCode,productBundle);
			if (bIsHardware) {
				ArrayList<HashMap<String, Object>> hwChildServs = dao.getChildServices(servSubsId,null);
				if (hwChildServs != null && hwChildServs.size() > 0) {
					populateDependentFunctions(dao,hwChildServs,"-",serviceCode,productBundle,false,null,bIsRented);
				}
			} else {
				ArrayList<HashMap<String, Object>> grChildServs = dao.getChildServices(servSubsId,null);
				if (grChildServs != null && grChildServs.size() > 0)
					populateSubscriptionExistance(dao,grChildServs,productCode,serviceCode,productBundle);
			}
		}
	}

	private boolean isHardwareRented(FetchProductBundleDAO dao,
			String servSubsId) throws Exception {
		ArrayList<HashMap<String, Object>> initialTicket = dao.getInitialTicketForService(servSubsId);
		if (initialTicket!=null && initialTicket.size() > 0) {
			String usageMode = (String) initialTicket.get(0).get("USAGE_MODE_VALUE_RD");
			if (usageMode.equals("6"))
				return true;
		}
		return false;
	}

	private void populateSalesPackageInfo(FetchProductBundleDAO dao,
			String bundleId, ProductBundle aBundle) throws Exception  {
		ArrayList<HashMap<String, Object>> salesPackectInfo = dao.getSalesPackageInfo(bundleId);
		if (salesPackectInfo != null && salesPackectInfo.size() > 0) {
			ExistConfString100 price = new ExistConfString100();
			price.setExisting(((BigDecimal) salesPackectInfo.get(0).get("BILLING_NAME_BASE_CHARGE")).toString());
			aBundle.setSalesPacketBasePrice(price);
			ExistConfString100 id = new ExistConfString100();
			id.setExisting((String) salesPackectInfo.get(0).get("SALES_PACKET_ID"));
			aBundle.setSalesPacketId(id);
			ExistConfString100 name = new ExistConfString100();
			name.setExisting((String) salesPackectInfo.get(0).get("SALES_PACKET_NAME"));
			aBundle.setSalesPackageName(name);
		}
	}
	
	private void populateSubscriptionExistance(	FetchProductBundleDAO dao, 
			ArrayList<HashMap<String, Object>> services,String productCode,String parentServiceCode, ProductBundle bundleData) throws Exception {
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

	private String populateOutputObject(CustomerData custData,ProductBundle bundleData, HashMap<String,Order> openOrders) throws Exception {
		String returnXml = null;
		output = new FetchProductBundleFKPOutput();
		if (serviceStatus != Status.SUCCESS){
			output.setResult(false);
			output.setErrorCode(errorCode);
			output.setErrorText(errorText);
		}else {
			output.setResult(true);
			if (custData != null)
				((FetchProductBundleFKPOutput)output).setCustomerData(custData);
			if (bundleData != null)
				((FetchProductBundleFKPOutput)output).setProductBundleData(bundleData);
			if (openOrders != null) {
				Set<String> keys = openOrders.keySet();
				Iterator<String> keyiter = keys.iterator();
				while (keyiter.hasNext()){
					String orderId = keyiter.next();
					Order value = openOrders.get(orderId);
					String barcode = value.getBarcode();
					for (int i = 0; i < value.getOrderPosition().size(); i++) {
						OrderPositionType opt = value.getOrderPosition().get(i).getValue();
						if (opt instanceof LineChange) {
							LineChangePacket packet = new LineChangePacket();
							packet.setOrderID(orderId);
							packet.setBarcode(barcode);
							packet.setLineChange((LineChange)opt);
							((FetchProductBundleFKPOutput)output).getLineChange().add(packet);
						} else if (opt instanceof Termination){
							TerminationPacket packet = new TerminationPacket();
							packet.setOrderID(orderId);
							packet.setBarcode(barcode);
							packet.setTermination((Termination)opt);
							((FetchProductBundleFKPOutput)output).getTermination().add(packet);
						} else if (opt instanceof LineCreation){
							LineCreationPacket packet = new LineCreationPacket();
							packet.setOrderID(orderId);
							packet.setBarcode(barcode);
							packet.setLineCreation((LineCreation)opt);
							((FetchProductBundleFKPOutput)output).getLineCreation().add(packet);
						} else if (opt instanceof Relocation){
							RelocationPacket packet = new RelocationPacket();
							packet.setOrderID(orderId);
							packet.setBarcode(barcode);
							packet.setRelocation((Relocation)opt);
							((FetchProductBundleFKPOutput)output).getRelocation().add(packet);
						} else if (opt instanceof ProviderChange){
							ContractPartnerChangePacket packet = new ContractPartnerChangePacket();
							packet.setOrderID(orderId);
							packet.setBarcode(barcode);
							packet.setContractPartnerChange((ProviderChange)opt);
							((FetchProductBundleFKPOutput)output).getContractPartnerChange().add(packet);
						}
					}
				}
			}
		}
		if (chunkSize!=null&&chunkNumber!=null){
			((FetchProductBundleFKPOutput)output).setChunkNumber(chunkNumber);
			((FetchProductBundleFKPOutput)output).setLastChunk(lastchunk);
		}
		try {
			returnXml = theHelper.serializeForMcf(output, customerNumber,accessNumber,"de.arcor.aaw.kernAAW.bks.services" ,"FetchProductBundleFKP");
		} catch (XMLException e) {
			unvalidatedXml = theHelper.serialize(output, "de.arcor.aaw.kernAAW.bks.services", null, "FetchProductBundleFKP");
			throw e;
		}
		if (chunkNumber!=null){
			if (serviceStatus == Status.SUCCESS && clientSchemaVersion==null &&
					((FetchProductBundleFKPInput)input).getCcbId() != null)
				theHelper.loadCache(((FetchProductBundleFKPInput)input).getCcbId().getExisting()+chunkNumber.intValue()+";FetchProductBundleFKP",returnXml);
			if (serviceStatus == Status.SUCCESS && clientSchemaVersion==null &&
					((FetchProductBundleFKPInput)input).getServiceSubscriptionId() != null)
				theHelper.loadCache(((FetchProductBundleFKPInput)input).getServiceSubscriptionId().getExisting()+chunkNumber.intValue()+";FetchProductBundleFKP",returnXml);
		}else{
			if (serviceStatus == Status.SUCCESS && clientSchemaVersion==null &&
					((FetchProductBundleFKPInput)input).getCcbId() != null)
				theHelper.loadCache(((FetchProductBundleFKPInput)input).getCcbId().getExisting()+";FetchProductBundleFKP",returnXml);
			if (serviceStatus == Status.SUCCESS && clientSchemaVersion==null &&
					((FetchProductBundleFKPInput)input).getServiceSubscriptionId() != null)
				theHelper.loadCache(((FetchProductBundleFKPInput)input).getServiceSubscriptionId().getExisting()+";FetchProductBundleFKP",returnXml);
		}
		return returnXml;
	}

	private void prefetchNextChunks(String rootCustNo,CcbIdB bundleId, CcbIdS inputServId, BigDecimal chunkSize, int totalseats){
		try {
			int loopNumber = totalseats / chunkSize.intValue();
			int chunkNo = 2;
			String key = null;
			if (bundleId != null)
				key=bundleId.getExisting()+chunkNo;
			else if(inputServId!=null)
				key=inputServId.getExisting()+chunkNo;
			for (int i = 0; i < loopNumber; i++) {
				if (theHelper.fetchFromCache("FetchProductBundleFKP",key,null) == null) {
					FetchProductBundleFKPHandler newHnd = new FetchProductBundleFKPHandler();
					FetchProductBundleFKPInput newInput = new FetchProductBundleFKPInput();
					newInput.setCcbId(bundleId);
					newInput.setServiceSubscriptionId(inputServId);
					newInput.setChunkSize(chunkSize);
					newInput.setChunkNumber(BigDecimal.valueOf(chunkNo++));
					newInput.setCustomerHierarchyNumber(rootCustNo);
					newHnd.setInput(newInput);
					newHnd.start();
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

	}

	public void setInput(InputData input) {
		super.setInput(input);
	}
}