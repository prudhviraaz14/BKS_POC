/*
    $Header:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/FetchProductBundleHandler.java-arc   1.140   Apr 11 2018 13:44:32   makuier  $

    $Log:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/FetchProductBundleHandler.java-arc  $
 * 
 *    Rev 1.140   Apr 11 2018 13:44:32   makuier
 * Bug fix for FNP customer Som
 * 
 *    Rev 1.139   Apr 11 2018 07:25:50   makuier
 * Handle additionalInfo for service VI295
 * 
 *    Rev 1.138   Feb 13 2018 07:10:38   makuier
 * Open Orders SOM versioning
 * 
 *    Rev 1.137   Aug 08 2017 13:10:10   makuier
 * Bug fix
 * 
 *    Rev 1.136   Jul 12 2017 14:25:18   makuier
 * consider individual AN fields when defaulting 
 * 
 *    Rev 1.135   Jun 14 2017 16:16:32   makuier
 * Default access numbers
 * 
 *    Rev 1.134   Jun 09 2017 07:56:12   makuier
 * Filter out obsolete nodes for SOM version.
 * 
 *    Rev 1.133   Apr 11 2017 15:24:44   makuier
 * Only consider deviating customer number which are BKS relevant.
 * 
 *    Rev 1.132   Mar 27 2017 11:30:48   makuier
 * Added second coice for BAN (V1002)
 * 
 *    Rev 1.131   Jan 20 2017 08:53:58   gaurav.verma
 * PPM 113765_169790 VF Business Broker + Hierarchy Bundle
 * 
 *    Rev 1.130   Oct 24 2016 10:40:44   makuier
 * Service versioning for service refdata
 * 
 *    Rev 1.129   Oct 09 2016 12:23:04   makuier
 * Do not populate pkatPacketId when client version is lower than 20.
 * 
 *    Rev 1.128   Sep 21 2016 07:25:44   makuier
 * Consider product code in address key
 * 
 *    Rev 1.127   Sep 05 2016 16:47:18   makuier
 * Prioritize the population of congured values,
 * 
 *    Rev 1.126   Jul 28 2016 14:57:58   makuier
 * Populate pkatPacketId.
 * 
 *    Rev 1.125   Jun 30 2016 14:34:38   lejam
 * RMS-152753 PPM-196371 DSL Portfolio 2016 - Changes for TariffOptionList and TvCenterOptionList
 * 
 *    Rev 1.124   Jun 30 2016 07:45:40   makuier
 * Refatored to expose functions to external calls
 * 
 *    Rev 1.123   May 24 2016 15:13:24   lejam
 * PPM-196371 RMS-152735 DSL Portfolio 2016
 * 
 *    Rev 1.122   Feb 25 2016 14:03:38   makuier
 * Jump over the rented hardware if the client uses SOM versions older than 18
 * 
 *    Rev 1.121   Feb 12 2016 15:00:56   makuier
 * Changed the way access number refdata is fetched.
 * 
 *    Rev 1.120   Jan 07 2016 10:48:54   makuier
 * Raise proper error message if the input serSuId is not a main access.
 * 
 *    Rev 1.119   Oct 27 2015 13:16:30   makuier
 * Return V0854 and V0855 for purchased hw as well.
 * 
 *    Rev 1.118   Oct 12 2015 14:54:38   makuier
 * Filter out OneNetId from older version of SOM
 * 
 *    Rev 1.117   Sep 15 2015 15:42:16   makuier
 * Support for rented hardware added.
 * 
 *    Rev 1.116   Aug 24 2015 14:03:24   makuier
 * Handle empty function list.
 * 
 *    Rev 1.115   Jul 15 2015 15:55:54   makuier
 * Consider ORDERED for OnenetBusiness
 * 
 *    Rev 1.114   Jun 11 2015 08:58:00   makuier
 * Populate onenetid and default licences
 * 
 *    Rev 1.113   Feb 04 2015 14:37:00   makuier
 * Use fixed value to check the value of the dependent characteristic.
 * 
 *    Rev 1.112   Nov 25 2014 08:12:40   makuier
 * Use serviceCode+PricingStructureCode to fetch sales packet
 * 
 *    Rev 1.111   Oct 30 2014 08:47:02   makuier
 * Alternative sales packet population.
 * 
 *    Rev 1.110   Jul 04 2014 11:43:30   makuier
 * Som versioning corrected.
 * 
 *    Rev 1.109   Mar 25 2014 13:57:22   makuier
 * Som versioning for configured values
 * 
 *    Rev 1.108   Mar 07 2014 08:46:08   makuier
 * Support key/value list
 * 
 *    Rev 1.107   Dec 16 2013 14:19:48   makuier
 * Pass the version to the master data.
 * 
 *    Rev 1.106   Nov 28 2013 14:33:50   makuier
 * Send the Som version to COM if the corresponding JMS property is populated by client.
 * 
 *    Rev 1.105   Nov 07 2013 12:46:04   makuier
 * ResellerId added to the signature.
 * 
 *    Rev 1.104   Oct 09 2013 08:52:04   makuier
 * Ignor empty values in address population.
 * 
 *    Rev 1.103   Jun 12 2013 10:52:52   makuier
 * AIX adaption
 * 
 *    Rev 1.103   May 16 2013 16:09:22   makuier
 * Adapt to AIX
 * 
 *    Rev 1.102   Apr 24 2013 16:55:00   makuier
 * Som versioning supported.
 * 
 *    Rev 1.101   Jan 04 2013 14:44:00   makuier
 * handle existing empty cscs in characteristic dependencies.
 * 
 *    Rev 1.100   Oct 26 2012 16:08:34   makuier
 * Default when dependent char does not exist.
 * 
 *    Rev 1.99   Sep 21 2012 13:49:14   makuier
 * Default ONKZ from access number when characteristic value = '0'
 * 
 *    Rev 1.98   Aug 22 2012 16:13:50   makuier
 * - Raise error if no external or internal order found for order id.
 * 
 *    Rev 1.97   Jun 14 2012 13:56:04   makuier
 * Do not apply future indicator filter on OP products.
 * 
 *    Rev 1.96   Jun 08 2012 13:03:08   makuier
 * Default the hw recipient name to customer.
 * 
 *    Rev 1.95   May 24 2012 12:31:12   makuier
 * Populate LocalAreaCode from access number if char V0124 is empty.
 * 
 *    Rev 1.94   May 10 2012 08:27:28   makuier
 * In DE do not populate address if postal code is missing.
 * 
 *    Rev 1.93   Apr 30 2012 13:51:16   makuier
 * Handle Extra numbers
 * 
 *    Rev 1.92   Apr 24 2012 17:45:54   makuier
 * Adapted to new cache lay out.
 * 
 *    Rev 1.91   Apr 19 2012 09:48:00   makuier
 * Allow conversion method for access numbers.
 * 
 *    Rev 1.90   Mar 29 2012 15:26:30   makuier
 * put barcode in open orders
 * 
 *    Rev 1.89   Mar 20 2012 15:51:36   makuier
 *  handle failed COM call with order id.
 * 
 *    Rev 1.88   Mar 19 2012 10:58:30   makuier
 * - Changed the making unique node id algorithm
 * 
 *    Rev 1.87   Mar 09 2012 15:49:38   makuier
 * Use bundle item type to identify affected bundles.
 * 
 *    Rev 1.86   Mar 07 2012 11:46:54   makuier
 * Pointer check.
 * 
 *    Rev 1.85   Feb 29 2012 17:07:32   makuier
 * Differentiate the error text
 * 
 *    Rev 1.84   Feb 24 2012 13:44:30   makuier
 * set last chunk if there is no seats in the bundle.
 * 
 *    Rev 1.83   Feb 17 2012 15:34:48   makuier
 * Check order
 * 
 *    Rev 1.81   Jan 31 2012 14:22:32   makuier
 *  runs open external order first before look into customer order when receiving order id as input.
 * 
 *    Rev 1.80   Jan 25 2012 13:09:30   makuier
 * Craete fake installation contact role only when call comes from GetCCBOpenOrder.
 * 
 *    Rev 1.79   Jan 20 2012 15:47:28   makuier
 * Dummy installation contact role added.
 * 
 *    Rev 1.78   Jan 11 2012 17:43:22   banania
 * SPN-BKS-000117549: BKS calls COM through a service bus request if the originator ! = COM only!
 * 
 *    Rev 1.77   Dec 09 2011 14:05:52   makuier
 * Parse without XSD validation for logging
 * 
 *    Rev 1.76   Dec 01 2011 12:30:26   makuier
 * get instant access variant from parent service
 * 
 *    Rev 1.75   Nov 21 2011 13:48:32   makuier
 * Support relocation and partner change open orders.
 * 
 *    Rev 1.74   Nov 15 2011 11:46:04   makuier
 * Bug fix
 * 
 *    Rev 1.73   Nov 11 2011 10:09:04   makuier
 * trim configured value
 * 
 *    Rev 1.72   Nov 04 2011 10:47:26   makuier
 * Jump over  when the part configured value is null
 * 
 *    Rev 1.71   Oct 27 2011 16:53:16   makuier
 * Selected destination Ref ID populated.
 * allocated ccb id considered.
 * 
 *    Rev 1.70   Oct 21 2011 15:56:56   makuier
 * Call SBUS service to retrieve open orders.
 * 
 *    Rev 1.69   Oct 17 2011 16:43:14   makuier
 * Do not fall back to next database if disable cache is set.
 * 
 *    Rev 1.68   Oct 12 2011 15:17:52   makuier
 * Changed the error message
 * 
 *    Rev 1.67   Oct 06 2011 09:22:24   makuier
 * Some improvements
 * 
 *    Rev 1.66   Sep 27 2011 14:22:40   wlazlow
 * SPN-BKS-000115517, refAccessID added for functions in case of the otherAccesses.
 * 
 *    Rev 1.65   Aug 25 2011 15:13:08   makuier
 * - Disregard fromduplicate service_subscription when counting no of seats.
 * 
 *    Rev 1.64   Aug 19 2011 12:46:58   makuier
 * IT-30480 chunking and filtering added.
 * 
 *    Rev 1.63   Aug 18 2008 13:41:08   makuier
 * Line owner for LTE populated.
 * 
 *    Rev 1.62   Aug 17 2011 10:59:46   makuier
 * send fetch product flag to get customer data.
 * 
 *    Rev 1.61   Aug 11 2011 18:17:14   wlazlow
 * SPN-BKS-000114375
 * 
 *    Rev 1.60   Aug 10 2011 17:12:24   makuier
 * corrected the unique node ids
 * 
 *    Rev 1.59   Jul 26 2011 12:19:20   wlazlow
 * IT-k-000030910
 * 
 *    Rev 1.58   Jul 25 2011 19:12:06   wlazlow
 * SPN-BKS-000114272
 * 
 *    Rev 1.57   Jul 21 2011 15:36:22   makuier
 * Default upstream bandwidth to standard.
 * 
 *    Rev 1.56   Jul 18 2011 17:50:28   makuier
 * Table map with - product supported.
 * 
 *    Rev 1.55   Jun 22 2011 15:38:58   makuier
 * Add type to the ccbid
 * 
 *    Rev 1.54   Jun 09 2011 13:24:44   makuier
 * Handle empty open orders from COM.
 * 
 *    Rev 1.53   Jun 06 2011 17:37:40   makuier
 * return hardware type. handle non-nummeric article numbers
 * 
 *    Rev 1.52   May 25 2011 15:08:52   makuier
 * Ibatis maps date to timestamp.
 * 
 *    Rev 1.51   May 12 2011 09:29:20   makuier
 * Adapt the new tri-state future indicator. 
 * 
 *    Rev 1.50   Apr 29 2011 13:07:10   makuier
 * handle several char dependencies.
 * 
 *    Rev 1.49   Apr 01 2011 12:07:42   makuier
 * Allow multiple access only for mobile.
 * 
 *    Rev 1.48   Mar 30 2011 17:40:00   makuier
 * Support multiple accesses with same name
 * 
 *    Rev 1.47   Mar 22 2011 13:17:06   makuier
 * Handle the second level hierarchy
 * 
 *    Rev 1.46   Mar 14 2011 16:42:44   makuier
 * Handle selected destinations correctly.
 * 
 *    Rev 1.45   Mar 04 2011 16:32:44   makuier
 * Contact role references added.
 * 
 *    Rev 1.44   Feb 28 2011 14:16:04   makuier
 * - If the service subscription id in fetch product bundle is NOT_AVAILABLE, return an empty positive reply.
 * 
 *    Rev 1.43   Feb 17 2011 16:53:08   makuier
 * Selected destination populated
 * 
 *    Rev 1.42   Feb 10 2011 16:35:46   makuier
 * - Create a SDC node for all SDCs in the bundle.
 * 
 *    Rev 1.41   Feb 01 2011 16:05:04   makuier
 * - If no external order found for the barcode, try with customer orders.
 * 
 *    Rev 1.40   Jan 24 2011 17:37:42   makuier
 * Use ONL if cache is desabled.
 * 
 *    Rev 1.39   Jan 19 2011 15:58:38   makuier
 * When populating accesses do not add to the list.
 * 
 *    Rev 1.38   Jan 06 2011 16:36:06   makuier
 * Accept order id and order position for closed orders
 * 
 *    Rev 1.37   Jan 03 2011 14:14:48   makuier
 * Adapted to changes in ref data cache.
 * 
 *    Rev 1.36   Dec 13 2010 16:08:44   makuier
 * Disable the cache when corresponding flag is set. 
 * 
 *    Rev 1.35   Dec 03 2010 17:55:20   makuier
 * Adapted to SOM XSD changes.
 * 
 *    Rev 1.34   Nov 29 2010 15:54:56   makuier
 * Adapted to PSM changes to SALES_PACKET table.
 * 
 *    Rev 1.33   Nov 17 2010 10:49:24   makuier
 * Fixed the cache.
 * 
 *    Rev 1.32   Nov 12 2010 15:39:48   makuier
 * Allow COM open order to process.
 * 
 *    Rev 1.31   Oct 07 2010 16:54:24   makuier
 * handle more than one extra number services.
 * 
 *    Rev 1.30   Sep 27 2010 16:24:08   makuier
 * Adapted the name to new SOM.
 * 
 *    Rev 1.29   Sep 20 2010 16:39:00   makuier
 * handle more than one hardware.
 * 
 *    Rev 1.28   Sep 06 2010 11:07:44   makuier
 * Handle future bundle items.
 * 
 *    Rev 1.27   Aug 17 2010 14:35:58   makuier
 * Handle open orders correctly.
 * 
 *    Rev 1.26   Aug 05 2010 11:31:18   makuier
 * Use last bundle item to get SDC.
 * 
 *    Rev 1.25   Jul 16 2010 16:57:06   makuier
 * Populate AOFlag.
 * 
 *    Rev 1.24   May 26 2010 15:33:06   makuier
 * Bug fix
 * 
 *    Rev 1.23   May 25 2010 14:31:56   makuier
 * Handle unbundled services
 * 
 *    Rev 1.22   May 17 2010 17:03:14   makuier
 * removed the package name
 * 
 *    Rev 1.21   Apr 30 2010 15:03:50   makuier
 * Added package name to setAttributeForPath
 * 
 *    Rev 1.20   Apr 22 2010 14:05:56   makuier
 * Added DSLONL_SERVICE to aviod duplicating the internet node.
 * 
 *    Rev 1.19   Apr 14 2010 18:00:32   makuier
 * Adapted to the last XSD changes
 * 
 *    Rev 1.18   Mar 18 2010 14:57:50   makuier
 * Added support for ISDN, NGN and bit stream
 * 
 *    Rev 1.17   Mar 08 2010 15:43:12   makuier
 * enable open orders
 * 
 *    Rev 1.16   Mar 04 2010 11:00:50   makuier
 * Check the field size of split
 * 
 *    Rev 1.15   Feb 23 2010 10:52:46   makuier
 * IPCentrex Mobile added.
 * 
 *    Rev 1.14   Feb 12 2010 12:04:20   makuier
 * Truncate exception messages.
 * 
 *    Rev 1.13   Feb 09 2010 17:33:56   makuier
 * Added support for service subscription id as input
 * 
 *    Rev 1.12   Feb 05 2010 15:57:48   makuier
 * Handle fixed values.
 * 
 *    Rev 1.11   Feb 04 2010 11:50:18   makuier
 * Send the error text in exception.
 * 
 *    Rev 1.10   Jan 28 2010 16:56:36   makuier
 * Additional data added to the output.
 * 
 *    Rev 1.9   Jan 15 2010 15:45:34   makuier
 * Gaurd against corrupted data added.
 * 
 *    Rev 1.8   Jan 06 2010 11:58:22   makuier
 * Support for open orders added.
 * 
 *    Rev 1.7   Dec 21 2009 09:45:02   makuier
 * Bug fix
 * 
 *    Rev 1.6   Dec 18 2009 16:37:16   makuier
 * Some improvements.
 * 
 *    Rev 1.5   Dec 02 2009 15:35:20   makuier
 * support for open orders added.
 * 
 *    Rev 1.4   Nov 23 2009 17:15:56   makuier
 * Misc. improvements.
 * 
 *    Rev 1.3   Nov 17 2009 18:59:30   makuier
 * refactoring. Use som as output.
 * 
 *    Rev 1.2   Oct 29 2009 11:40:30   makuier
 * ADSL added.
 * 
 *    Rev 1.1   Sep 22 2009 18:10:24   makuier
 * Handle MOS, PC backup and safety pacjkages.
 * 
 *    Rev 1.0   Aug 31 2009 11:21:30   makuier
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
import net.arcor.orderschema.ExistConfHardwareRecipient;
import net.arcor.orderschema.ExistConfString100;
import net.arcor.orderschema.ExistConfString12;
import net.arcor.orderschema.ExistConfStringId;
import net.arcor.orderschema.Functions;
import net.arcor.orderschema.Hardware;
import net.arcor.orderschema.HardwareRecipient;
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
import de.arcor.aaw.kernAAW.bks.services.FetchProductBundleInput;
import de.arcor.aaw.kernAAW.bks.services.FetchProductBundleOutput;
import de.arcor.aaw.kernAAW.bks.services.FilterList;
import de.arcor.aaw.kernAAW.bks.services.KeyValue;
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
public class FetchProductBundleHandler  extends BaseServiceHandler{


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

	private String areaCode;

	private boolean isOpBundle = false;

	final static String[] seatServices = {"VI011","VI012","VI013"};
	final static String[] depFunctionServices = {"VI061","VI062","VI076","VI077","V0014","V0277"};
	final static String[] opMainServices = {"V0003","V0010","I1210","I121z","I1290"};
	final static HashMap<String,ArrayList<String>> charMap2ndChoice = new HashMap<String , ArrayList<String>>() {
		private static final long serialVersionUID = 1L;
		{
			put("V0154",new ArrayList<String>() {{add("I1213");}});
			put("V1002",new ArrayList<String>() {{add("I1213");add("I1210");add("I1290");add("I1040");}});
		}
	};
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
		areaCode = null;
		bFetchFuture = false;
		isOpBundle = false;
		boolean isIsdnBundle = false;
		ProductBundle bundleData = null;
		FetchProductBundleDAO dao = null;
		totalseats = 0;

		try {
			boolean aoFlag = false;
			String orderId = ((FetchProductBundleInput)input).getOrderId();
			Integer orderPosNo = ((FetchProductBundleInput)input).getOrderPositionNumber();
			CcbIdB bundleId = ((FetchProductBundleInput)input).getCcbId();
			CcbIdS inputServId = ((FetchProductBundleInput)input).getServiceSubscriptionId();
			String tasi = ((FetchProductBundleInput)input).getTasi();
			chunkSize = ((FetchProductBundleInput)input).getChunkSize();
			chunkNumber = ((FetchProductBundleInput)input).getChunkNumber();
			FilterList filters = ((FetchProductBundleInput)input).getFilterList();
			boolean bUseCache = true;
			if (((FetchProductBundleInput)input).isDisableCache() != null)
				bUseCache = !((FetchProductBundleInput)input).isDisableCache();
			
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
					} else {
						throw new BksFunctionalException("No order found for order id " + orderId);
					}
				}
			}
			if (bundleId != null || inputServId != null || tasi != null) {
				if (bundleId != null) {
					if (bUseCache && (chunkSize==null||chunkNumber==null) &&
							clientSchemaVersion==null) {
						returnXml = (String) theHelper.fetchFromCache(
								"FetchProductBundle", bundleId.getExisting(), null);
						if (returnXml!=null)
							return returnXml;
					}
					Boolean bFutureInd = ((FetchProductBundleInput)input).isFutureIndicator();
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
								"FetchProductBundle", inputServId.getExisting(), null);
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
				} else {
					bundleItems = dao.getBundleItemsByTasi(tasi);
					if (bundleItems != null &&  bundleItems.size() > 0) {
						bundleId = new CcbIdB();
						bundleId.setExisting((String)bundleItems.get(0).get("BUNDLE_ID"));
						bundleId.setType("B");
						if (bundleItems.get(0).get("FUTURE_INDICATOR") != null)
							bFetchFuture = ((String)bundleItems.get(0).get("FUTURE_INDICATOR")).equals("Y");
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
						String tmpServCode = (String)bundleItems.get(i).get("SERVICE_CODE");
						if (subIds.contains(tmpServSubsId))
							continue;
						if (itemType.equals("IPCENTREX_SEAT")){
							if (!isSuitableSeat(dao,tmpServSubsId,filters)){
								bundleItems.remove(i);
								i--;
								continue;
							}	
							subIds.add(tmpServSubsId);
							totalseats ++;
							continue;
						}
						if (itemType.equals("VOICE_SERVICE")){
							isIsdnBundle=true;
						}
						if (Arrays.asList(opMainServices).contains(tmpServCode)){
							isOpBundle =true;
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
				if(hierarchyIndicator!=null && hierarchyIndicator.equals("Y")){
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
                
				order=getCustomerData(dao,accounts,sdContractNumbers);
				ExistConfString100 provCode = new ExistConfString100();
				provCode.setExisting(order.getCustomerData().get(0).getCustomer().getProviderCode().getExisting());
				if (bundleItems!=null && bundleItems.size() > 0)
					bundleData = getProductBundlesInfo(dao,bundleItems,bundleId,aoFlag,accounts.get(0),false);
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
			if (order!=null && order.getCustomerData() != null && order.getCustomerData().size() > 0){
				customerData = order.getCustomerData().get(0);
				defaultHardwareRecipient(customerData,bundleData);
			}
			returnXml = populateOutputObject(customerData,bundleData,openOrders);

		} catch (BksDataException e) {
			logger.error(e.getText());
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
				if (funcName.contains("internet") && isIsdnBundle) {
					String altText = generateAltErrorText(dao, bundleData.getCcbId().getExisting());
					if (altText != null) {
						errorCode = "BKS0025";
						errorText = altText;
					}
				}
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

	private void defaultHardwareRecipient(CustomerData customerData,
			ProductBundle bundleData) {
		Customer customer = customerData.getCustomer();
		String salutaion = null;
		String forname = null;
		String name = null;
		if (customer!=null&& customer.getIndividual()!=null){
			if (customer.getIndividual().getSalutation()!=null)
				salutaion = customer.getIndividual().getSalutation().getExisting();
			if (customer.getIndividual().getForename()!=null)
				forname = customer.getIndividual().getForename().getExisting();
			if (customer.getIndividual().getName()!=null)
				name = customer.getIndividual().getName().getExisting();
		} else if (customer!=null&& customer.getOrganization()!=null){
			if (customer.getOrganization().getSalutation()!=null)
				salutaion = customer.getOrganization().getSalutation().getExisting();
			if (customer.getOrganization().getName()!=null)
				name = customer.getOrganization().getName().getExisting();
		} else {
			return;
		}
		List<Hardware> hardwares = null;
		if (bundleData!=null && bundleData.getFunctions() != null)
			hardwares = bundleData.getFunctions().getHardware();
		for (int i=0;hardwares!=null&&i<hardwares.size();i++){
			Hardware hardware = hardwares.get(i);
			ExistConfHardwareRecipient hrRecipt = null;
			if (hardware.getHardwareConfiguration()!=null){
				hrRecipt = hardware.getHardwareConfiguration().getHardwareRecipient();
				if (hrRecipt==null){
					hrRecipt = new ExistConfHardwareRecipient();
					hardware.getHardwareConfiguration().setHardwareRecipient(hrRecipt);
					HardwareRecipient hrr= new HardwareRecipient();
					hrRecipt.setExisting(hrr);
					hrr.setSalutationDescription(salutaion);
					hrr.setForename(forname);
					hrr.setName(name);
				}
			} else if (hardware.getRentedHardwareConfiguration()!=null){
				hrRecipt = hardware.getRentedHardwareConfiguration().getHardwareRecipient();
				if (hrRecipt==null){
					hrRecipt = new ExistConfHardwareRecipient();
					hardware.getRentedHardwareConfiguration().setHardwareRecipient(hrRecipt);
					HardwareRecipient hrr= new HardwareRecipient();
					hrRecipt.setExisting(hrr);
					hrr.setSalutationDescription(salutaion);
					hrr.setForename(forname);
					hrr.setName(name);
				}
			}
		}	
	}

	private String generateAltErrorText(FetchProductBundleDAO dao, String bundleId) {
		ArrayList<HashMap<String, Object>> onlineService = null;
		try {
			onlineService = dao.getOnlineService(customerNumber,bundleId);
		} catch (Exception e) {
			return null;
		}
		if (onlineService==null||onlineService.size()==0)
			return null;
		String onlineServId = (String) onlineService.get(0).get("SERVICE_SUBSCRIPTION_ID");

		return "The online service subscription "+onlineServId+" needs to be added to the bundle";
	}

	private boolean isSuitableSeat(FetchProductBundleDAO dao, String seatServSubs, FilterList filters) throws Exception {
		if (filters == null || filters.getFilter().size() ==0 )
			return true;
		KeyValue filterCriteria = filters.getFilter().get(0);
		if (!filterCriteria.getKey().equals("seatStatus"))
			return true;
		ArrayList<HashMap<String, Object>> accessNumbers = dao.getAccessNumberChars(seatServSubs,"V0001",null);		
		if (filterCriteria.getValue().equals("unconfigured") && (accessNumbers.size() == 0 ||
				accessNumbers.get(0).get("ACCESS_NUMBER") == null))
			return true;
		if (filterCriteria.getValue().equals("configured") && accessNumbers.size() > 0 &&
				accessNumbers.get(0).get("ACCESS_NUMBER") != null)
			return true;
			
		return false;
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
		String serviceType = null;
		if (services.get(0).get("SERVICE_TYPE")!=null)
			serviceType = services.get(0).get("SERVICE_TYPE").toString();
		if(serviceType!= null && !serviceType.equals("MAIN_ACCESS"))
			throw new BksDataException("BKS0028", "The service subscription, "+servSubsId+", has to be a main access service");
		theHelper.setDesiredNewElement(0);
		String productCode = populateTableData(dao,servSubsId,serviceCode,bundleData,null,false);
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
				if (theHelper.isClientVersLowerThanIntro("16.0",clientSchemaVersion)) {
					//Map BSS release to SOM release
					String[] parts = clientSchemaVersion.split("\\.");
					Double bssRelease = Double.valueOf(parts[0]);
					Double somRelease = ((bssRelease+30.0)/10.0);
					request.setSomVersion(somRelease.toString());
				} else {
					request.setSomVersion(clientSchemaVersion);
				}
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
			ArrayList<String> accounts, ArrayList<String> sdContractNumbers) throws Exception{
		GetSomMasterDataHandler mdh = new GetSomMasterDataHandler();
		mdh.setClientSchemaVersion(clientSchemaVersion);
		Order order = null;
		   order = mdh.getOrder(customerNumber,null,accounts,sdContractNumbers,true,false);
	   return order;
	}

	ProductBundle getProductBundleInfoById(Order order, String bundleId, String accountNo, String customerNo) throws Exception {
		FetchProductBundleDAO dao = null;
		ProductBundle prodBundle;
		try {
			dao= (FetchProductBundleDAO) DatabaseClient.getBksDataAccessObject(null,"FetchProductBundleDAO");
			bundleItems = dao.getBundleItems(bundleId);
			setCustomerNumber(customerNo);
			this.order=order;
			CcbIdB bundleNode = new CcbIdB();
			bundleNode.setExisting(bundleId);
			bundleNode.setType("B");
			boolean aoFlag = false;
			String aoFlagString = (String)bundleItems.get(0).get("AOSTATUS");
			if (aoFlagString!=null)
				aoFlag = (aoFlagString.equals("Y"))?true:false;		
			prodBundle = getProductBundlesInfo(dao, bundleItems, bundleNode, aoFlag, accountNo,true);
		} finally {
			if (dao != null)
				dao.closeConnection();
		}
		return prodBundle;
	}
	
	private ProductBundle getProductBundlesInfo(FetchProductBundleDAO dao,
			ArrayList<HashMap<String,Object>> bundleItems, CcbIdB bundleId, boolean aoFlag, String accountNo,boolean bHandleSkExternal) throws Exception {
		ProductBundle bundleData = new ProductBundle();
		bundleId.setType("B");
		bundleData.setCcbId(bundleId);
		if (!theHelper.isClientVersLowerThanIntro("17.0",clientSchemaVersion)) {
			ExistConfString12 onenetId = new ExistConfString12();
			onenetId.setExisting(getOnenetId(dao, bundleId.getExisting()));
			bundleData.setOneNetId(onenetId);
		}
		bundleData.setAoMastered(new ExistConfBoolean());
		bundleData.getAoMastered().setExisting(aoFlag);
		if (bundleItems != null &&  bundleItems.size() > 0){
			String bundleType = (String)  bundleItems.get(0).get("BUNDLE_TYPE_RD");
			bundleData.setOrderPositionNumber(0);
			bundleData.setProductBundleType(bundleType);
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
			ArrayList<String> tarifList = new ArrayList<String>();
			populateFunctiosAndAccesses(dao,bundleItems,bundleData,tarifList,bHandleSkExternal);
			populateSalesPackageInfo(dao,bundleId.getExisting(),bundleData,tarifList);
			
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
	private String getOnenetId(FetchProductBundleDAO dao,
			String bundleId) throws Exception {
		ArrayList<HashMap<String, Object>> onenet = dao.getOnenetForBundle(bundleId);
		if (onenet!=null&&onenet.size()>0)
			return (String) onenet.get(0).get("ONENET_ID");
		return null;
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
			if(bundleData.getFunctions() != null&&bundleData.getFunctions().getInternet() != null && bundleData.getFunctions().getInternet().size()>0){
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
			ArrayList<HashMap<String, Object>> bundleItems,	ProductBundle bundleData, ArrayList<String> tarifList, boolean bHandleSkExternal) throws Exception {
		String siteServiceId=null;
		int currentSeat=0;
		String voiceServiceId = null;
		for (int i =0;i<bundleItems.size();i++) {
			String fInd = (String) bundleItems.get(i).get("FUTURE_INDICATOR");
			if (!isOpBundle && ((fInd != null && fInd.equals("Y") && !bFetchFuture) ||
				(fInd != null && fInd.equals("T") && bFetchFuture)))
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
			String productCode = populateTableData(dao,servSubsId,serviceCode,bundleData,tarifList,bHandleSkExternal);
			if (bOnlineService)
				populateSelectedDestinationRef(dao,servSubsId,voiceServiceId,bundleData);
			populateAccessNumbers(dao,servSubsId,serviceCode,null,bundleData);
			if (!Arrays.asList(seatServices).contains(serviceCode)){
				populateConfiguredValues(dao,servSubsId,serviceCode,null,bundleData,false);
				populateAddresses(dao,servSubsId,productCode,serviceCode,bundleData,false);
				populateServiceLocation(dao,servSubsId,serviceCode,bundleData);
				populateChildServiceList(dao,productCode,servSubsId,serviceCode,bundleData);
				if (serviceCode.equals("VI250")){
					populateDefaultLicences(dao,servSubsId,serviceCode,bundleData);
				}
				populateDepHardwareList(dao,productCode,servSubsId,serviceCode,bundleData);
			} 
		}
 
		Internet internet = null;
	    if(bundleData.getFunctions() != null&&bundleData.getFunctions().getInternet()!=null && bundleData.getFunctions().getInternet().size()>0){
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
	    if(bundleData.getFunctions() != null&&bundleData.getFunctions().getDirectoryEntry()!=null && bundleData.getFunctions().getDirectoryEntry().size()>0){
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

	private void populateDefaultLicences(FetchProductBundleDAO dao,
			String servSubsId, String serviceCode, ProductBundle bundleData) throws Exception {
		ArrayList<HashMap<String, Object>> defLicences = dao.getDefaultLicences("UC_LICENCE",servSubsId);
		for (int i = 0; defLicences!=null && i < defLicences.size(); i++) {
			HashMap<String, Object> licence = defLicences.get(i);
			theHelper.setDesiredNewElement(1);
			theHelper.setAttributeForPath(bundleData, "set",
					"Functions.Voice.VoiceONBConfiguration.LicencesList.Existing.Licence.ServiceCode", licence.get("SERVICE_CODE"));
			theHelper.setAttributeForPath(bundleData, "set",
					"Functions.Voice.VoiceONBConfiguration.LicencesList.Existing.Licence.Name", licence.get("NAME"));
			theHelper.setAttributeForPath(bundleData, "set",
					"Functions.Voice.VoiceONBConfiguration.LicencesList.Existing.Licence.Quantity", licence.get("QUANTITY"));
		}
		ArrayList<HashMap<String, Object>> defOptions = dao.getDefaultLicences("UC_OPTION",servSubsId);
		for (int i = 0; defOptions!=null && i < defOptions.size(); i++) {
			HashMap<String, Object> option = defOptions.get(i);
			theHelper.setDesiredNewElement(1);
			theHelper.setAttributeForPath(bundleData, "set",
					"Functions.Voice.VoiceONBConfiguration.LicenceOptionsList.Existing.LicenceOption.ServiceCode", option.get("SERVICE_CODE"));
			theHelper.setAttributeForPath(bundleData, "set",
					"Functions.Voice.VoiceONBConfiguration.LicenceOptionsList.Existing.LicenceOption.Name", option.get("NAME"));
			theHelper.setAttributeForPath(bundleData, "set",
					"Functions.Voice.VoiceONBConfiguration.LicenceOptionsList.Existing.LicenceOption.Quantity", option.get("QUANTITY"));
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
		String returnOrdered=null;
		if (serviceCode!=null && serviceCode.equals("VI250")||
			parentServiceCode!=null && parentServiceCode.equals("VI250"))
			 returnOrdered="Y";
		ArrayList<HashMap<String, Object>> accessNumbers = dao.getAccessNumberChars(servSubsId,null,returnOrdered);
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
			theHelper.setDesiredNewElement(1);
			if (charMap.get(serviceCharCode) != null){
				HashMap<Integer,Object[]> fieldPathMap = charMap.get(serviceCharCode);
				if (number != null) {
					String[] numberFields = number.split(";");
					for(Integer j=0;j<numberFields.length;j++){
						Object[] paths = fieldPathMap.get(j+1);
						populateAccNumFields(dao,bundleData,cscs,paths,numberFields[j],j);
					}
				} else {
					Object[] paths = fieldPathMap.get(1);
					populateAccNumFields(dao,bundleData,cscs,paths,null,null);					
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
			Object value = theHelper.setAttributeForPath(bundleData, "get",path, null); 
			if (value==null) {
				theHelper.setDesiredNewElement(0);
				theHelper.setAttributeForPath(bundleData, "set",path, defaultValue);
			}
		}
	}

	private void populateAccNumFields(FetchProductBundleDAO dao, ProductBundle bundleData, ArrayList<HashMap<String, Object>> cscs, 
			Object[] paths, String numberField, Integer fieldNo) throws Exception {
		for (int k = 0; paths!=null&&k < paths.length; k++) {
			String[] fields = (String[]) paths[k];
			String path = fields[0];
			String conversionMethod = fields[2];
			String defaultValue = fields[3];
			String depCharCode = fields[4];
			String fixedValue = fields[5];
			if (numberField==null || !isDependentCharPopulate(dao, cscs,depCharCode,fixedValue,fieldNo))
				numberField=defaultValue;
			if (numberField==null )
				continue;
			if (path.startsWith("Accesses"))
				theHelper.setDesiredNewElement(-1);
			if (path.contains("AccessNumberRange1.Existing.LocalAreaCode") ||
					path.contains("AccessNumber1.Existing.LocalAreaCode"))
				areaCode = numberField;
			if (numberField.startsWith("DEL"))
				theHelper.setAttributeForPath(bundleData, "set",
						path, numberField.substring(3));
			else {
				if (conversionMethod != null) {
					Object converted = theHelper.invokeMethod(theHelper,
							conversionMethod, numberField, String.class);
					if (converted!=null)
						theHelper.setAttributeForPath(bundleData, "set",path, converted);
				} else {
					theHelper.setAttributeForPath(bundleData, "set",path, numberField);
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
				String introVersion = charPath.get("-").get(j)[7];
				if (introVersion != null && clientSchemaVersion != null &&
					theHelper.isClientVersLowerThanIntro(introVersion,clientSchemaVersion))
					continue;
				String[] values = null;
				if (defaultValue!=null)
					values = defaultValue.split(";");
				String path=charPath.get("-").get(j)[0];
				if (bIsRented)
					path=path.replaceAll("HardwareConfiguration", "RentedHardwareConfiguration");
				if (!isDependentCharPopulate(dao, cscs,depCharCode,null,null)){
					if (values==null||values.length <= 1)
						continue;
					if (conversionMethod == null)
						theHelper.setAttributeForPath(bundleData, "set",path, values[1]);
					else if (conversionMethod != null) {
						Object converted = theHelper.invokeMethod(theHelper,
								conversionMethod, values[1], String.class);
						theHelper.setAttributeForPath(bundleData, "set",path, converted);
					}

				} else if (values!=null){
					if (conversionMethod == null)
						theHelper.setAttributeForPath(bundleData, "set",path, values[0]);
					else if (conversionMethod != null) {
						String charValue = values[0];
						Object converted = null;
						if(!conversionMethod.equals("getContractEndDate")){
							converted = theHelper.invokeMethod(theHelper,
									conversionMethod, charValue, String.class);
						}else{
							String depCharValue = getDependentCharValue(dao, cscs,"V0200");
							String durValue = getDependentCharValue(dao, cscs,"V0231");
							if(durValue == null || durValue.equals(""))
								durValue = "0";
							String durUnit = charValue;
							Object startDateTmp = theHelper.convertCcbToDate(depCharValue);
							Object startDate = theHelper.convertCalendarToSqlDate(startDateTmp);
							converted = 
									theHelper.getContractEndDate(startDate, durUnit, Integer.parseInt(durValue), null, null, false);
						}
						theHelper.setAttributeForPath(bundleData, "set",path, converted);
					}
				}
			}
		}
		
		String returnOrdered=null;
		if (serviceCode!=null && serviceCode.equals("VI250")||
			parentServiceCode!=null && parentServiceCode.equals("VI250"))
			 returnOrdered="Y";
		ArrayList<HashMap<String, Object>> configValues = dao.getConfiguredValues(servSubsId,returnOrdered);
		if (configValues == null || charPath == null )
			return;
		Set<String> keys = charPath.keySet();
		Iterator<String> keyiter = keys.iterator();
		while (keyiter.hasNext()){
			String refCharCode = keyiter.next();
			if (refCharCode.equals("V0946") || refCharCode.equals("V0947"))
				theHelper.setDesiredNewElement(1);
			if (serviceCode.equals("VI295"))
				theHelper.setDesiredNewElement(2);
			String columnValue = null;
			String serviceCharCode = null;
			if (refCharCode.equals("-"))
				continue;
			for (int i = 0;i < configValues.size() && columnValue == null; i++) {
				serviceCharCode = (String) configValues.get(i).get("SERVICE_CHARACTERISTIC_CODE");
				if (refCharCode.equals(serviceCharCode))
					columnValue = (String) configValues.get(i).get("CONFIGURED_VALUE_STRING");
			}
			if ((columnValue == null || columnValue.equals("0")) && 
				areaCode != null && refCharCode.equals("V0124"))
				columnValue = "0"+areaCode;
			if (charPath.get(refCharCode) != null) {
				for (int j = 0; j < charPath.get(refCharCode).size(); j++) {
					String path = charPath.get(refCharCode).get(j)[0];
					String rdsId = charPath.get(refCharCode).get(j)[1];
					String conversionMethod = charPath.get(refCharCode).get(j)[2];
					String fieldNumber = charPath.get(refCharCode).get(j)[3];
					String depCharCode = charPath.get(refCharCode).get(j)[4];
					String defaultValue = charPath.get(refCharCode).get(j)[5];
					String fixedValue = charPath.get(refCharCode).get(j)[6];
					String introVersion = charPath.get(refCharCode).get(j)[7];
					if (isFirstChoiceAlreadyPopulated(bundleData,refCharCode,serviceCode,path))
						continue;
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
					if (columnValue == null || (refCharCode.equals("V0112") && !isNummeric(columnValue)))
						columnValue=defaultValue;

					if (columnValue == null || !isDependentCharPopulate(dao, cscs,depCharCode,fixedValue,null))
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

	private boolean isFirstChoiceAlreadyPopulated(ProductBundle bundleData, String charCode,
			String serviceCode, String path) throws Exception {
		ArrayList<String> secPrioService=charMap2ndChoice.get(charCode);
		if (secPrioService == null || !secPrioService.contains(serviceCode))
			return false;
		Object value = theHelper.setAttributeForPath(bundleData, "get",path, null); 
		return (value != null);
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
			FetchProductBundleDAO dao, ArrayList<HashMap<String, Object>> cscs, String depCharCode, String fixedValue, Integer fieldNo) throws Exception {
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

	private boolean isCorrespondingValuesPopulated(FetchProductBundleDAO dao, ArrayList<String> cscIdList, String fixedValue, Integer fieldNo) throws Exception {
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
	
	private String getDependentCharValue(FetchProductBundleDAO dao, ArrayList<HashMap<String, Object>> cscs, String depCharCode) throws Exception {
		if (depCharCode==null)
			return null;

		ArrayList<String> cscIdList = new ArrayList<String>();
		for (int i = 0;i < cscs.size(); i++) {
			String serviceCharCode = (String) cscs.get(i).get("SERVICE_CHARACTERISTIC_CODE");
			String cscid = (String) cscs.get(i).get("CONFIG_SERVICE_CHAR_ID");
			if(serviceCharCode.equals(depCharCode))
				cscIdList.add(cscid);
		}
		if (cscIdList.size() == 0)
			return null;
		ArrayList<HashMap<String, Object>> charValues = dao.getCharValues(cscIdList);
		if (charValues.size()==0)
			return null;
		for (int i =0;i<charValues.size();i++) {
			String charValue = (String) charValues.get(i).get("VALUE");
			if ((charValue==null || charValue.trim().length()==0))
				return null;
			else
				return charValue;
		}
		return null;
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
						if (columnValue == null || !isDependentCharPopulate(dao,cscs,depCharCode,null,null))
							continue;
						populateSingleColumn((String[])valueObj,columnValue,bundleData,bIsRented);
					} else {
						ArrayList<String[]> list = (ArrayList<String[]>)valueObj;
						for (int j = 0; list != null && j < list.size(); j++) {
							String[] item = list.get(j);
							String depCharCode = item[3];
							if (columnValue == null || !isDependentCharPopulate(dao,cscs,depCharCode,null,null))
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
			if (columnValue.length()==0)
				return;
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

	private String populateTableData(FetchProductBundleDAO dao, String servSubsId, String serviceCode, ProductBundle bundleData, ArrayList<String> tarifList, boolean bHandleSkExternal) throws Exception {
		ArrayList<HashMap<String,Object>> contract = dao.getContractData(servSubsId);
		String productCode = null;
		if (contract == null || contract.size() == 0){
			contract = dao.getSDContractData(servSubsId);
			if (!bHandleSkExternal)
				addSkeletonContractNode(order,contract,"SERV_DELIV_SKELETON_NUMBER");
		} else if (!bHandleSkExternal) {
			addSkeletonContractNode(order,contract,"ASSOC_SKELETON_CONTRACT_NUMBER");
		}
		if (contract != null && contract.size() > 0) {
			if (tarifList!=null)
				tarifList.add(serviceCode+";"+contract.get(0).get("PRICING_STRUCTURE_CODE").toString());
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
						if (path.startsWith("Accesses") && bFirstAccess){
							theHelper.setDesiredNewElement(0);
							bFirstAccess=false;
						}
						if (path.startsWith("Functions") && bFirstFunction){
							theHelper.setDesiredNewElement(0);
							bFirstFunction=false;
						}
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
		String returnOrdered=null;
		if (productCode!=null && productCode.equals("VI207"))
			 returnOrdered="Y";
		ArrayList<HashMap<String, Object>> services = dao.getChildServices(parentServSubsId,returnOrdered);
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
			String parentServiceCode, ProductBundle productBundle, boolean bIsHardware,String parentServSubsId,boolean bIsParentRented) throws Exception {
		for (int i =0;i<services.size();i++) {
			String serviceCode = services.get(i).get("SERVICE_CODE").toString();
			String servSubsId = services.get(i).get("SERVICE_SUBSCRIPTION_ID").toString();
			boolean bIsRented=false;
			if (bIsHardware){
				bIsRented=isHardwareRented(dao,servSubsId);
				if (bIsRented && clientSchemaVersion != null &&
					theHelper.isClientVersLowerThanIntro("18.0",clientSchemaVersion))
					continue;
			}
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
						String path = tableColMap.get(columnName).get(j)[0];
						if (bIsRented||bIsParentRented)
							path=path.replaceAll("HardwareConfiguration", "RentedHardwareConfiguration");
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
			populateConfiguredValues(dao,servSubsId,serviceCode,parentServiceCode,productBundle,bIsRented||bIsParentRented);
			if (serviceCode.equals("V8042"))
				populateConfiguredValues(dao,parentServSubsId,serviceCode,parentServiceCode,productBundle,bIsRented);
			populateAddresses(dao,servSubsId,productCode,serviceCode,productBundle,bIsRented);
			populateAccessNumbers(dao,servSubsId,serviceCode,parentServiceCode,productBundle);
			String returnOrdered=null;
			if (parentServiceCode!=null && parentServiceCode.equals("VI250"))
				 returnOrdered="Y";
			if (bIsHardware) {
				ArrayList<HashMap<String, Object>> hwChildServs = dao.getChildServices(servSubsId,returnOrdered);
				if (hwChildServs != null && hwChildServs.size() > 0){
					populateDependentFunctions(dao,hwChildServs,"-",serviceCode,productBundle,false,null,bIsRented);
				}
			} else {
				ArrayList<HashMap<String, Object>> grChildServs = dao.getChildServices(servSubsId,returnOrdered);
				if (grChildServs != null && grChildServs.size() > 0)
					populateSubscriptionExistance(dao,grChildServs,productCode,serviceCode,productBundle);
			}
		}
	}

	private boolean isHardwareRented(FetchProductBundleDAO dao, String servSubsId) throws Exception {
		ArrayList<HashMap<String, Object>> initialTicket = dao.getInitialTicketForService(servSubsId);
		if (initialTicket!=null && initialTicket.size() > 0) {
			String usageMode = (String) initialTicket.get(0).get("USAGE_MODE_VALUE_RD");
			if (usageMode.equals("6"))
				return true;
		}
		return false;
	}

	private void populateSalesPackageInfo(FetchProductBundleDAO dao,
			String bundleId, ProductBundle aBundle, ArrayList<String> tarifList) throws Exception  {
		ArrayList<HashMap<String, Object>> salesPacketInfo = dao.getSalesPackageInfo(bundleId);
		if (salesPacketInfo==null || salesPacketInfo.size() == 0) {
			for (int i = 0; i < tarifList.size(); i++) {
				String tariffCode = tarifList.get(i);
				String salesPacketCode = theHelper.getSalesPacketCode(tariffCode);
				salesPacketInfo = dao.getSalesPackageInfoByCode(salesPacketCode);
				if (salesPacketInfo!=null&&salesPacketInfo.size()==1)
					break;
			}
		}
		if (salesPacketInfo != null && salesPacketInfo.size() > 0) {
			ExistConfString100 price = new ExistConfString100();
			price.setExisting(((BigDecimal) salesPacketInfo.get(0).get("BILLING_NAME_BASE_CHARGE")).toString());
			aBundle.setSalesPacketBasePrice(price);
			ExistConfString100 id = new ExistConfString100();
			id.setExisting((String) salesPacketInfo.get(0).get("SALES_PACKET_ID"));
			aBundle.setSalesPacketId(id);
			ExistConfString100 name = new ExistConfString100();
			name.setExisting((String) salesPacketInfo.get(0).get("NAME"));
			aBundle.setSalesPackageName(name);
			ExistConfString100 code = new ExistConfString100();
			code.setExisting((String) salesPacketInfo.get(0).get("SALES_PACKET_CODE"));
			aBundle.setSalesPacketCode(code);
			if (!theHelper.isClientVersLowerThanIntro("20.0",clientSchemaVersion)) {
				ExistConfString100 pkatId = new ExistConfString100();
				pkatId.setExisting((String) salesPacketInfo.get(0).get("AAW_PACKET_ID"));
				aBundle.setPkatPacketId(pkatId);
			}
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
		output = new FetchProductBundleOutput();
		if (serviceStatus != Status.SUCCESS){
			output.setResult(false);
			output.setErrorCode(errorCode);
			output.setErrorText(errorText);
		}else {
			output.setResult(true);
			if (custData != null)
				((FetchProductBundleOutput)output).setCustomerData(custData);
			if (bundleData != null)
				((FetchProductBundleOutput)output).setProductBundleData(bundleData);
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
							((FetchProductBundleOutput)output).getLineChange().add(packet);
						} else if (opt instanceof Termination){
							TerminationPacket packet = new TerminationPacket();
							packet.setOrderID(orderId);
							packet.setBarcode(barcode);
							packet.setTermination((Termination)opt);
							((FetchProductBundleOutput)output).getTermination().add(packet);
						} else if (opt instanceof LineCreation){
							LineCreationPacket packet = new LineCreationPacket();
							packet.setOrderID(orderId);
							packet.setBarcode(barcode);
							packet.setLineCreation((LineCreation)opt);
							((FetchProductBundleOutput)output).getLineCreation().add(packet);
						} else if (opt instanceof Relocation){
							RelocationPacket packet = new RelocationPacket();
							packet.setOrderID(orderId);
							packet.setBarcode(barcode);
							packet.setRelocation((Relocation)opt);
							((FetchProductBundleOutput)output).getRelocation().add(packet);
						} else if (opt instanceof ProviderChange){
							ContractPartnerChangePacket packet = new ContractPartnerChangePacket();
							packet.setOrderID(orderId);
							packet.setBarcode(barcode);
							packet.setContractPartnerChange((ProviderChange)opt);
							((FetchProductBundleOutput)output).getContractPartnerChange().add(packet);
						}
					}
				}
			}
		}
		if (chunkSize!=null&&chunkNumber!=null){
			((FetchProductBundleOutput)output).setChunkNumber(chunkNumber);
			((FetchProductBundleOutput)output).setLastChunk(lastchunk);
		}
		try {
			returnXml = theHelper.serializeForMcf(output, customerNumber,accessNumber,"de.arcor.aaw.kernAAW.bks.services" ,"FetchProductBundle");
		} catch (XMLException e) {
			unvalidatedXml = theHelper.serialize(output, "de.arcor.aaw.kernAAW.bks.services", null, "FetchProductBundle");
			logger.debug("unvalidated xml :" + unvalidatedXml);
			throw e;
		}
		if (serviceStatus == Status.SUCCESS &&	clientSchemaVersion==null && 
				((FetchProductBundleInput)input).getCcbId() != null)
			theHelper.loadCache(((FetchProductBundleInput)input).getCcbId().getExisting()+";FetchProductBundle",returnXml);
		if (serviceStatus == Status.SUCCESS && clientSchemaVersion==null && 
				((FetchProductBundleInput)input).getServiceSubscriptionId() != null)
			theHelper.loadCache(((FetchProductBundleInput)input).getServiceSubscriptionId().getExisting()+";FetchProductBundle",returnXml);

		return returnXml;
	}

	public void setInput(InputData input) {
		super.setInput(input);
	}
}