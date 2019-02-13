/*
    $Header:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/common/BksHelper.java-arc   1.114   Sep 17 2018 14:25:04   makuier  $

    $Log:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/common/BksHelper.java-arc  $
 * 
 *    Rev 1.114   Sep 17 2018 14:25:04   makuier
 * Mapping for Fibre bandwidths added,
 * 
 *    Rev 1.113   Jun 20 2018 14:14:20   vikas.s
 * Supported for Business VOIP
 * 
 *    Rev 1.112   Mar 13 2018 09:37:42   makuier
 * 2 New bandwidths added to the converter
 * 
 *    Rev 1.111   Oct 05 2017 08:02:52   makuier
 * Extend the logging
 * 
 *    Rev 1.110   Jul 11 2017 11:51:24   vikas.s
 * Set a Configurable Login Timeout on Connection Object
 * 
 *    Rev 1.109   Jun 13 2017 13:50:00   makuier
 * - Recurssive getter returns null when it hits the first null object.
 * 
 *    Rev 1.108   Jun 09 2017 07:58:16   makuier
 * Return false if introversion is empty.
 * 
 *    Rev 1.107   Mar 14 2017 10:31:14   makuier
 * Get account balance from BAT.
 * 
 *    Rev 1.106   Sep 05 2016 16:48:36   makuier
 * get the value for a xpath.
 * 
 *    Rev 1.105   Jun 30 2016 07:19:38   makuier
 * Mapping for 1&1 bandwidths added. 
 * 
 *    Rev 1.104   May 24 2016 15:09:30   lejam
 * PPM-196371 RMS-152735 DSL Portfolio 2016
 * 
 *    Rev 1.103   Apr 27 2016 19:16:08   lejam
 * SPN-BKS-000131232 Added new convertBandwidth functions for BDSL
 * 
 *    Rev 1.102   Jun 03 2015 08:46:00   makuier
 * Bug fix
 * 
 *    Rev 1.101   May 26 2015 08:42:16   makuier
 * PermNameMap added.
 * 
 *    Rev 1.100   May 13 2015 11:36:12   makuier
 * Bug fix.
 * 
 *    Rev 1.99   May 11 2015 13:16:52   makuier
 * Refactor for HTTP interface
 * 
 *    Rev 1.98   Dec 05 2014 14:32:00   makuier
 * Calculate the end date considering notice period.
 * 
 *    Rev 1.97   Oct 30 2014 08:33:52   makuier
 * getSalesPacketCode added.
 * 
 *    Rev 1.96   Mar 25 2014 16:59:56   wlazlow
 * IT-32500 Conversion of a date format  for V0947 characteristic  from  DD.MM.YYYY  to  YYYY-MM-DD
 * 
 *    Rev 1.95   Mar 21 2014 11:59:26   wlazlow
 * IT-32500 Conversion of a date format  for V0947 characteristic  from  DD.MM.YYYY  to  YYYY-MM-DD
 * 
 *    Rev 1.94   Mar 17 2014 09:53:22   makuier
 * Corrected the typo.
 * 
 *    Rev 1.93   Mar 06 2014 09:09:22   makuier
 * convertValidationResult added.
 * 
 *    Rev 1.92   Feb 19 2014 14:32:26   makuier
 * Ref data support for mock values added.
 * 
 *    Rev 1.91   Feb 05 2014 12:31:54   makuier
 * - Map DSL 1000 for SDSL
 * 
 *    Rev 1.90   Jan 08 2014 10:03:28   makuier
 * Change access technology for 1&1
 * 
 *    Rev 1.89   Dec 20 2013 12:22:52   makuier
 * Added 1&1 to technology mapping
 * 
 *    Rev 1.88   Nov 07 2013 12:38:54   makuier
 * Wbci technology mapping.
 * 
 *    Rev 1.87   Apr 24 2013 16:57:24   makuier
 * new methods added.
 * 
 *    Rev 1.86   Mar 20 2013 10:46:10   makuier
 * DSL 1500 added to mapping.
 * 
 *    Rev 1.85   Mar 14 2013 12:22:18   makuier
 * handle DE with no telephone number.
 * 
 *    Rev 1.84   Aug 30 2012 13:10:34   makuier
 * Omit hyphens in billing accounts
 * 
 *    Rev 1.83   Aug 01 2012 10:09:08   wlazlow
 * SPN-BKS-000121766,The integer overflow while calculating the autoextension period corrected.
 * 
 *    Rev 1.82   May 21 2012 12:30:10   makuier
 * truncateString30 added.
 * 
 *    Rev 1.81   May 15 2012 14:32:30   makuier
 * Cover additional DE phone number format.
 * 
 *    Rev 1.80   Apr 24 2012 17:48:38   makuier
 * handle additional access number forrmats.
 * 
 *    Rev 1.79   Apr 19 2012 09:31:14   makuier
 * truncateStringNimeric15 added
 * 
 *    Rev 1.78   Apr 12 2012 10:26:34   makuier
 * Filter out access info phone numbers if it cannot be normalized
 * 
 *    Rev 1.77   Mar 29 2012 15:19:24   makuier
 * handle not supported phone number formats
 * 
 *    Rev 1.76   Mar 26 2012 12:22:26   makuier
 * normalize phone number function added.
 * 
 *    Rev 1.75   Mar 13 2012 15:37:42   makuier
 * convertServiceName added
 * 
 *    Rev 1.74   Feb 22 2012 14:54:34   makuier
 * mapChargeType added
 * 
 *    Rev 1.73   Feb 15 2012 11:14:50   makuier
 * MapToTrueFalse added
 * 
 *    Rev 1.72   Jan 31 2012 14:28:56   makuier
 * convertToPosBigDec added.
 * 
 *    Rev 1.71   Jan 24 2012 15:13:52   makuier
 * normalizePhoneNumber added.
 * 
 *    Rev 1.70   Jan 12 2012 14:50:20   makuier
 * throw xml exception. 
 * 
 *    Rev 1.69   Dec 01 2011 12:24:28   makuier
 * Make the compair of MFH case insensetive
 * 
 *    Rev 1.68   Nov 25 2011 13:50:30   makuier
 * truncateString15 added
 * 
 *    Rev 1.67   Oct 28 2011 17:24:52   makuier
 * truncateString360 added
 * 
 *    Rev 1.66   Oct 24 2011 14:15:24   makuier
 * true added to convertToBoolean
 * 
 *    Rev 1.65   Oct 17 2011 16:41:32   makuier
 * do not log parsing error in helper. It will be printed in calling class.
 * 
 *    Rev 1.64   Oct 12 2011 15:16:52   makuier
 * Put messge in debug.
 * 
 *    Rev 1.63   Oct 06 2011 09:20:30   makuier
 * truncate60 added
 * 
 *    Rev 1.62   Sep 30 2011 11:03:12   makuier
 * extended the camel to underscore
 * 
 *    Rev 1.61   Jul 22 2011 15:54:06   makuier
 * getTvOptionType added.
 * 
 *    Rev 1.60   Jul 21 2011 20:04:24   wlazlow
 * SPN-CCB-000114220, The SalutationDescription for HardwareConfiguration can have only following values: "Frau", "Herr" and "Firma". In case of all other values we default to "Firma".
 * 
 *    Rev 1.59   Jul 05 2011 17:48:06   makuier
 * truncateString50 added.
 * 
 *    Rev 1.58   Jun 07 2011 10:39:08   makuier
 * fetchRefData added
 * 
 *    Rev 1.57   Jun 03 2011 16:17:28   makuier
 * Support date concversion for taimestamp as well.
 * 
 *    Rev 1.56   May 30 2011 13:48:54   makuier
 * truncateString100 added.
 * 
 *    Rev 1.55   May 16 2011 17:42:26   makuier
 * trimProductName .. added.
 * 
 *    Rev 1.54   May 13 2011 14:14:16   makuier
 * Covers bandwidth 1500
 * 
 *    Rev 1.53   May 05 2011 18:10:36   makuier
 * new converters added.
 * 
 *    Rev 1.52   Feb 17 2011 17:06:08   makuier
 * handleInvalidAddress added.
 * 
 *    Rev 1.51   Feb 09 2011 14:38:12   makuier
 * convertCcbToDate Added.
 * 
 *    Rev 1.50   Jan 06 2011 16:33:32   makuier
 * convertCorpType added.
 * 
 *    Rev 1.49   Dec 28 2010 16:25:58   makuier
 * Added support for more elements in ASK list.
 * 
 *    Rev 1.48   Dec 03 2010 17:49:22   makuier
 * ConvertConditionName added.
 * 
 *    Rev 1.47   Oct 25 2010 15:55:16   makuier
 * Handle international addresses.
 * 
 *    Rev 1.46   Oct 15 2010 15:37:20   makuier
 * serialize method added.
 * 
 *    Rev 1.45   Oct 04 2010 12:53:28   makuier
 * Truncate the detached house > 50
 * 
 *    Rev 1.44   Sep 27 2010 16:14:44   makuier
 * Added Fsk18Plus to convertToBoolean.
 * 
 *    Rev 1.43   Sep 23 2010 18:18:38   makuier
 * DSL 100000 added.
 * 
 *    Rev 1.42   Aug 20 2010 15:31:14   makuier
 * convertAccountState added.
 * 
 *    Rev 1.41   Aug 11 2010 18:03:14   makuier
 * Trim floor in TAE.
 * 
 *    Rev 1.40   Aug 05 2010 11:12:08   makuier
 * checkAreaCode added
 * 
 *    Rev 1.39   Jul 28 2010 17:23:50   makuier
 * new convert methods added.
 * 
 *    Rev 1.38   Jul 16 2010 15:44:58   makuier
 * convertToTimePoint added.
 * 
 *    Rev 1.37   Jun 08 2010 16:26:02   makuier
 * Corrected the end date calculation.
 * 
 *    Rev 1.36   Jun 04 2010 15:07:52   makuier
 * Additional bandwidth added to convert method.
 * 
 *    Rev 1.35   May 28 2010 14:06:40   makuier
 * decryptCustPasswd added.
 * 
 *    Rev 1.34   May 17 2010 17:00:52   makuier
 * CPCOM 1b
 * 
 *    Rev 1.33   Apr 30 2010 14:52:56   makuier
 * Package name added for new sid to identify the class correctly.
 * 
 *    Rev 1.32   Apr 14 2010 17:52:34   makuier
 * ConvertConnection added.
 * 
 *    Rev 1.31   Mar 18 2010 14:34:32   makuier
 * Change camel case to underscore upper in enumaration as xjc does the same.
 * 
 *    Rev 1.30   Feb 12 2010 11:43:10   makuier
 * made new element integer to handle multiple lists in the path.
 * 
 *    Rev 1.29   Jan 28 2010 16:20:22   makuier
 * Clone the date when calculating the contract end date.
 * 
 *    Rev 1.28   Nov 23 2009 17:03:44   makuier
 * 1 added to convertToBoolean.
 * 
 *    Rev 1.27   Nov 17 2009 18:49:56   makuier
 * convertBandwidth added.
 * 
 *    Rev 1.26   Nov 09 2009 11:01:10   makuier
 * Additional changes for IP Centrex.
 * 
 *    Rev 1.25   Oct 29 2009 11:31:52   makuier
 * Mapping functions added for IP centrex.
 * 
 *    Rev 1.24   Sep 22 2009 18:12:02   makuier
 * Moved the end date calculation to helper.
 * 
 *    Rev 1.23   Aug 14 2009 11:24:06   makuier
 * Use the xml processor bean instead of instantiating.
 * 
 *    Rev 1.22   May 27 2009 15:59:38   makuier
 * MCF2 adaption
 * 
 *    Rev 1.21   May 11 2009 18:37:08   wlazlow
 * SPN-BKS-000086407
 * 
 *    Rev 1.20   Apr 06 2009 11:34:40   makuier
 * Support for Tariff options added.
 * 
 *    Rev 1.19   Mar 12 2009 14:50:46   makuier
 * Changed the cache instantiation.
 * 
 *    Rev 1.18   Mar 05 2009 14:22:40   makuier
 * Adapted to the new version of AAW (130.01.00.00)
 * 
 *    Rev 1.17   Dec 17 2008 14:13:36   makuier
 * handleFunctions added.
 * 
 *    Rev 1.16   Dec 09 2008 13:17:10   makuier
 * Populate aaw version .. as they are mandatory now.
 * 
 *    Rev 1.15   Dec 04 2008 11:18:22   makuier
 * Adapted to AAW version 129.00.02.00
 * 
 *    Rev 1.14   Nov 25 2008 14:18:24   makuier
 * Refactoring function handling.
 * 
 *    Rev 1.13   May 30 2008 13:40:14   makuier
 * Some refactoring for 127.
 * 
 *    Rev 1.12   May 13 2008 11:45:18   makuier
 * Cache under access number.
 * 
 *    Rev 1.11   Mar 12 2008 13:00:30   makuier
 * Use the non depricated method for setting the leistungsmerkmal.
 * 
 *    Rev 1.10   Mar 07 2008 14:49:08   makuier
 * handleFunctions aaded.
 * 
 *    Rev 1.9   Feb 26 2008 14:10:16   makuier
 * Populate null on filter when searching for reference data
 * 
 *    Rev 1.8   Jan 28 2008 12:22:14   huptasch
 * SPN-BKS-000066294
 * SPN-BKS-000066387
 * 
 *    Rev 1.7   Jan 22 2008 16:41:08   makuier
 * Additional information added to the log message when No reference data found
 * 
 *    Rev 1.6   Jan 10 2008 11:27:12   makuier
 * Generate a Bestand instead of an Auftrag.
 * 
 *    Rev 1.5   Dec 11 2007 18:58:48   makuier
 * Pass parameters to error ref data.
 * 
 *    Rev 1.4   Dec 06 2007 18:01:12   makuier
 * Some pointer checks added.
 * 
 *    Rev 1.3   Nov 28 2007 17:59:16   makuier
 * Cache only the successful responses.
 * 
 *    Rev 1.2   Nov 27 2007 13:59:02   makuier
 * Bug Fixes
 * 
 *    Rev 1.1   Nov 12 2007 18:46:38   makuier
 * changes for 1.25
 * 
 *    Rev 1.0   Nov 06 2007 17:35:18   makuier
 * Initial revision.
*/
package net.arcor.bks.common;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Pattern;

import javax.sql.PooledConnection;
import javax.xml.bind.JAXBElement;

import net.arcor.bks.client.DatabaseClientConfig;
import net.arcor.bks.db.GeneralDAO;
import net.arcor.epsm_basetypes_001.InputData;
import net.arcor.epsm_basetypes_001.OutputData;
import net.arcor.mcf2.exception.base.MCFException;
import net.arcor.mcf2.exception.internal.XMLException;
import net.arcor.mcf2.xml.XmlProcessor;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import oracle.jdbc.pool.OracleConnectionPoolDataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.time.TimePoint;

import de.arcor.aaw.ask.metamodel.ASKRepositoryFactory;
import de.arcor.aaw.auftragsmodell.Auftragsmodell;
import de.arcor.aaw.auftragsmodell.Version;
import de.arcor.aaw.auftragsmodell.ask.Auftraggeber;
import de.arcor.aaw.auftragsmodell.ask.Auftragsposition;
import de.arcor.aaw.auftragsmodell.ask.Bestand;
import de.arcor.aaw.auftragsmodell.ask.DynamicElement;
import de.arcor.aaw.auftragsmodell.ask.Funktion;
import de.arcor.aaw.auftragsmodell.ask.JuristischePerson;
import de.arcor.aaw.auftragsmodell.ask.NatuerlichePerson;
import de.arcor.aaw.auftragsmodell.metainformationen.Meldung;
import de.arcor.aaw.auftragsmodell.metainformationen.MeldungsTypen;
import de.arcor.aaw.auftragsmodell.util.AawSpracheUtil;
import de.arcor.aaw.auftragsmodell.util.AawXMLUtil;
import de.arcor.aaw.auftragsmodell.util.XMLUtil;
import de.arcor.aaw.rds.client.ReferenzdatenServiceFacade;
import de.arcor.aaw.rds.client.model.ReferenzdatenContainer;

public class BksHelper {
	
	private static Cache theCache = null;
	protected final Log logger = LogFactory.getLog(getClass());
	private int desiredNewElement = -1;
	private int orderListNo = 0;
	private String customerEncKey = null;

    final static HashMap<String , String> wbciTechnologyMap = new HashMap<String , String>() {
    	private static final long serialVersionUID = 1L;
    	{ 
    		put("ISDN_ONLY","001 TAL ISDN");
    	    put("ISDN_DSL","002 TAL DSL");
    	    put("NGN_ADSL","002 TAL DSL");
    	    put("BUSINESS_DSL","002 TAL DSL");
    	    put("ONE_AND_ONE","002 TAL DSL");
    	    put("NGN_VDSL","003 TAL VDSL");
    	    put("BIT_ANNEXJ","004 ADSL SA Annex J");
    	    put("BIT_ANNEXB","005 ADSL SA Annex B");
    	    put("DSLR","007 ADSL SH");
    	    put("BIT_VDSL","009 VDSL SA");
    	    put("LTE","011 LTE");
//    	    put("","012 FTTC");
    	    put("FTTB","013 FTTB");
    	    put("FTTH","014 FTTH");
    	    put("VOIP 2nd","007 ADSL SH");
    	    put("FTTX_OTHER","021 Sonstiges");
    	    put("IP_CENTREX","021 Sonstiges");
    	    put("SIP_TRUNK","021 Sonstiges");
    	    put("NOT_SUPPORTED","021 Sonstiges");
    	    put("BUSINESS_VOIP","021 Sonstiges");
    	    
    	    
    	}
    }; 

    final static HashMap<String , String> permNameMap = new HashMap<String , String>() {
    	private static final long serialVersionUID = 1L;
    	{ 
    		put("DEV;PDEV","PERSONAL_DATA_INDICATOR");
    	    put("DEV;CDEV","PERSONAL_DATA_INDICATOR");
    	    put("ADV;MAIL","CUSTOMER_DATA_INDICATOR");
    	    put("ADV;EMAL","CUSTOMER_DATA_INDICATOR");
    	    put("ADV;PHON","MARKETING_PHONE_INDICATOR");
    	    put("ADV;FAX","MARKETING_FAX_INDICATOR");
    	}
    }; 

    final static HashMap<String , String> externalStateMap = new HashMap<String , String>() {
    	private static final long serialVersionUID = 1L;
    	{ 
    		put("PRECOMPLETED","ORDER_POSITON_IN_PROGRESS");
    		put("ORDERED","ORDER_POSITON_IN_PROGRESS");
    		put("CANCEL_WAIT","ORDER_POSITON_IN_PROGRESS");
    		put("COMPLETED_NO_DE","ORDER_POSITON_IN_PROGRESS");
    		put("NEW","ORDER_POSITON_IN_PROGRESS");
    		put("WAITING","ORDER_POSITON_IN_PROGRESS");
    		put("SUSPENDED","ORDER_POSITON_IN_PROGRESS");
    	}
    }; 
    
	final static HashMap<String , String> BandwidthMap = new HashMap<String , String>() {
		private static final long serialVersionUID = 1L;
		{ 
			put("DSL 1000","I1224"); 
			put("DSL 2000","I1220");
			put("DSL 4000","I1221"); 
			put("DSL 6000","I1217");
			put("DSL 16000","I1218");
		}
	}; 

	final static HashMap<String , String> BandwidthDSLMap = new HashMap<String , String>() {
		private static final long serialVersionUID = 1L;
		{ 
			put("Standard","V0133"); 
			put("384","V0133");
			put("Premium","V0116"); 
			put("768","V0116");
			put("Gold","V0117");
			put("1500","V0117"); 
			put("DSL 1500","V0117");
			put("DSL 1000","V0118"); 
			put("1000","V0118");
			put("DSL 2000","V0174"); 
			put("2000","V0174");
			put("DSL 3000","V0175"); 
			put("3000","V0175");
			put("DSL 4000", "V0176");
			put("DSL 5000","V0177");
			put("DSL 6000","V0178"); 
			put("6000","V0178");
			put("DSL 500","V0179");
			put("DSL 8000","V0180");
			put("DSL 10000","V018A");
			put("DSL 12000","V018B");
			put("DSL 16000","V018C");
			put("16000","V018C");
			put("DSL 20000","V018D");
			put("DSL 25000","V018G");
			put("DSL 50000","V018H");
			put("DSL 100000","V018N");
			put("DSL 175000","V018U");
			put("DSL 250000","V018V");
			put("LTE 1200","V017E");
			put("LTE 3600","V017J");
			put("LTE 7200","V017K");
			put("LTE 21600","V017L");
			put("LTE 50000","V017M");
			put("LTE 1200 Outdoor","V018J");
			put("LTE 3600 Outdoor","V018O");
			put("LTE 7200 Outdoor","V018P");
			put("LTE 21600 Outdoor","V018Q");
			put("LTE 50000 Outdoor","V018R");
			put("Glasfaser 50","IG001");
			put("Glasfaser 100","IG002");
			put("Glasfaser 250","IG003");
			put("Glasfaser 500","IG004");
			put("Glasfaser 750","IG005");
			put("Glasfaser 1000","IG006");
			put("Glasfaser 1500","IG007");
			put("Glasfaser 2000","IG008");
		}
	}; 

	public static HashMap<String, String> getExternalstatemap() {
		return externalStateMap;
	}

	public static HashMap<String, String> getPermnamemap() {
		return permNameMap;
	}

	public String getCustomerEncKey() {
		return customerEncKey;
	}

	public void setCustomerEncKey(String customerEncKey) {
		this.customerEncKey = customerEncKey;
	}

	public BksHelper() {
		ApplicationContext ac = BksStaticContainer.getAc();
		if (ac == null)
			throw new MCFException("Application context has not been initialized.");
		theCache = (Cache) ac.getBean("ehCache");
	}

	public String serialazeForAaw(OutputData output, String customerNumber, String accessNumber,String packageName,
			String serviceName) throws Exception {
		String returnXml = (String) fetchFromCache(serviceName, customerNumber,accessNumber);
		if (returnXml == null) {
			Object of = Class.forName(packageName + ".ObjectFactory").newInstance();
			Object outService = Class.forName(
					packageName + "." + serviceName + "Service").newInstance();
			invokeMethod(outService, "set" + serviceName + "Output", output,
					output.getClass());
			// create the jaxb object for output and put it in the cache.
			JAXBElement<?> returnObject = (JAXBElement<?>)invokeMethod(of, "create" + serviceName
					+ "Service", outService, outService.getClass());
			String contextName = XMLUtil.getAawJaxbContext()+":de.arcor.aaw.kernAAW.bks.services";;
			String schemaName = DatabaseClientConfig.getSetting("databaseclient.SchemaName");
	        long startTime = System.currentTimeMillis();
			try {
				AawXMLUtil xmlProcessor = new AawXMLUtil();
				returnXml = xmlProcessor.serialize(returnObject,schemaName,contextName);
			} catch (Throwable e) {
				logger.error(e.getMessage());
				e.printStackTrace();
				throw new Exception(e);
			}
			long endTime = System.currentTimeMillis();
			long duration = endTime - startTime;
			logger.info("AAW Serialization duration : "+duration);
		}
		return  returnXml;
	}

	public String serializeForMcf(Object output, String customerNumber, String accessNumber, String packageName,
			String serviceName) throws Exception {
		String returnXml = (String) fetchFromCache(serviceName, customerNumber,accessNumber);
		if (returnXml == null) {
			Object of = Class.forName(packageName + ".ObjectFactory").newInstance();
			Object outService = Class.forName(
					packageName + "." + serviceName + "Service").newInstance();
			invokeMethod(outService, "set" + serviceName + "Output", output,
					output.getClass());
			// create the jaxb object for output and put it in the cache.
			Object returnObject = invokeMethod(of, "create" + serviceName
					+ "Service", outService, outService.getClass());
			String contextName = XMLUtil.getAawJaxbContext()+":de.arcor.aaw.kernAAW.bks.services";
			String schemaName = DatabaseClientConfig.getSetting("databaseclient.SchemaName");
	        long startTime = System.currentTimeMillis();
			try {
				ApplicationContext ac = BksStaticContainer.getAc();
				if (ac == null)
					throw new MCFException("Application context has not been initialized.");
				XmlProcessor xmlProcessor = (XmlProcessor) ac.getBean("mcf2XmlProcessor");
				returnXml = xmlProcessor.serialize((JAXBElement<?>) returnObject,schemaName,contextName);
			} catch (XMLException e) {
				throw e;
			} catch (Throwable e) {
				logger.error(e.getMessage());
				e.printStackTrace();
				throw new Exception(e);
			}
			long endTime = System.currentTimeMillis();
			long duration = endTime - startTime;
			logger.info("MCF Serialization duration : "+duration);
		}
		return  returnXml;
	}
	public String serialize(Object output,String packageName, String schemaName,
			String serviceName) throws Exception {
		String returnXml = null;
		Object of = Class.forName(packageName + ".ObjectFactory").newInstance();
		Object outService = Class.forName(
					packageName + "." + serviceName + "Service").newInstance();
		invokeMethod(outService, "set" + serviceName + "Output", output,
					output.getClass());
			// create the jaxb object for output and put it in the cache.
		Object returnObject = invokeMethod(of, "create" + serviceName
					+ "Service", outService, outService.getClass());
		String contextName = XMLUtil.getAawJaxbContext()+":"+packageName;
        long startTime = System.currentTimeMillis();
        try {
        	ApplicationContext ac = BksStaticContainer.getAc();
        	if (ac == null)
        		throw new MCFException("Application context has not been initialized.");
        	XmlProcessor xmlProcessor = (XmlProcessor) ac.getBean("mcf2XmlProcessor");
        	returnXml = xmlProcessor.serialize((JAXBElement<?>) returnObject,schemaName,contextName);
		} catch (XMLException e) {
			throw e;
        } catch (Throwable e) {
        	logger.error(e.getMessage());
        	e.printStackTrace();
        	throw new Exception(e);
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        logger.info("MCF Serialization duration : "+duration);
		return  returnXml;
	}
	public Object invokeMethod(Object service, String methodName, Object value,
			Class<?> argumentType) throws Exception {
		Object result;
		if (value == null) {
			Method method = service.getClass().getMethod(methodName);
			result = method.invoke(service, (Object[]) null);
		} else {
			Method method = service.getClass().getMethod(methodName,
					new Class[] { argumentType });
			result = method.invoke(service, new Object[] { value });
		}
		return result;
	}

	public boolean matchInput(String inputString) throws Exception {
		if (inputString == null)
			return true;
		String pattern = DatabaseClientConfig.getSetting("databaseclient.InputPattern");
		return Pattern.matches(pattern, inputString);
	}

	public Object fetchFromCache(String serviceName, String customerNumber, String accessNumber) {
		Object returnObject = null;
		if (serviceName != null && accessNumber != null) {
			Element chacheElement = theCache.get(accessNumber + ";" + serviceName);
			if (chacheElement != null)
				returnObject = chacheElement.getObjectValue();
		}
		if (serviceName != null && customerNumber != null && returnObject == null) {
			Element chacheElement = theCache.get(customerNumber + ";" + serviceName);
			if (chacheElement != null)
				returnObject = chacheElement.getObjectValue();
		}
		return returnObject;
	}

	public Auftragsmodell createApe(HashMap<String, Object> valueList,String[] refTargetList,boolean isIndividual) throws Exception{
		Auftragsmodell am = new de.arcor.aaw.auftragsmodell.ObjectFactory().createAuftragsmodell();
		try {
			String mdtsVersion = "DefaultVersion";
			ApplicationContext ac = BksStaticContainer.getAc();
			if (ac == null)
				throw new MCFException("Application context has not been initialized.");
			ReferenzdatenServiceFacade rdsService = 
				(ReferenzdatenServiceFacade) ac.getBean("referenzdatenServiceFacade");
			ReferenzdatenContainer referenzdatenContainer  = rdsService.getReferenzdaten("MDTSVersion", null);
			if (referenzdatenContainer != null && referenzdatenContainer.getReferenzdaten().size() >0)
				mdtsVersion = referenzdatenContainer.getReferenzdaten().get(0).getValue("MDTSVersion");
			Bestand ape = new de.arcor.aaw.auftragsmodell.ask.ObjectFactory().createBestand(mdtsVersion);
			ape.setApeNummer(1);
			am.getInhalt().add(ape);
			if (isIndividual){
				NatuerlichePerson customer = new NatuerlichePerson();
				customer.setApeNummer(1);
				Auftraggeber ag = new Auftraggeber();
				ag.setNatuerlichePerson(customer);
				ape.setAuftraggeber(ag);
			} else {
				JuristischePerson customer = new JuristischePerson();
				customer.setApeNummer(1);
				Auftraggeber ag = new Auftraggeber();
				ag.setJuristischePerson(customer);
				ape.setAuftraggeber(ag);
			}
			Set<String> keys = valueList.keySet();
			Iterator<String> keyiter = keys.iterator();
			while (keyiter.hasNext()){
				String key = keyiter.next();
				Object value = valueList.get(key);
				HashMap<String,MasterMapObject> masterMap = BksRefDataCacheHandler.getMasterDataMap();
				if (masterMap.get(key) != null) {
					String path = masterMap.get(key).getTargetAttributePath();
					ape.setAttributeByPath(path,value);
				}
			}

			HashMap<String,Object> refValue = populateRefdataApe(valueList,refTargetList);
			if (refValue!= null){
				Set<String> refkeys = refValue.keySet();
				Iterator<String> refkeyiter = refkeys.iterator();
				while (refkeyiter.hasNext()){
					String path = refkeyiter.next();
					ape.setAttributeByPath(path,refValue.get(path) );
				}
			}
		} catch (BksDataException e) {
			throw e;
		} catch (Throwable e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			throw new Exception(e);
		}
		return am;
	}

    public String fetchRefData(String input, String rdsId){
    	RefdataMapObject refMapping = BksRefDataCacheHandler.getRefdataMap().get(rdsId);
    	HashMap<String,String>  articleFilter = new HashMap<String, String>();
		Iterator<?> iterator = refMapping.getFilter().keySet().iterator();		 
		if (iterator.hasNext())
			articleFilter.put((String)iterator.next(),input);
		String refValue = null;
		try {
			refValue = getRefdataValue(articleFilter, refMapping.getRdsCategory(), refMapping.getRdsOutput());
		} catch (Exception e) {
            return null;
		}
		return refValue;
    }

    public String getRefdataValue(HashMap<String, String> filter, String category,String outputTag) throws Exception {
		ApplicationContext ac = BksStaticContainer.getAc();
		if (ac == null)
			throw new MCFException("Application context has not been initialized.");

		ReferenzdatenServiceFacade rdsService = 
			(ReferenzdatenServiceFacade) ac.getBean("referenzdatenServiceFacade");
		if (rdsService == null)
			throw new MCFException("Could not get Bean referenzdatenServiceFacade from application context.");

		if (filter != null && filter.size() >0){
			String filterText="";
			Iterator<?> iterator = filter.keySet().iterator();		 
			while (iterator.hasNext()){
				String filterKey = (String)iterator.next();
				String filterValue = filter.get(filterKey);
				if (filterValue != null) {
					filterText += "Key : " + filterKey + " Value : " + filterValue + "\n";
				}
			}
			logger.debug("Getting ref data for : \n"+filterText);
			ReferenzdatenContainer refContainer = 
				rdsService.getReferenzdaten(category, filter, null);
			if (refContainer != null && refContainer.getReferenzdaten().size() > 0)
				return refContainer.getReferenzdaten().get(0).getValue(outputTag);
		}
		return null;
	}

	public HashMap<String,Object> populateRefdataApe(HashMap<String, Object> valueList, String[] refTargetList) throws Exception {
		HashMap<String,Object> returnValue = new HashMap<String, Object>();
		if (refTargetList == null)
			return returnValue;
		ApplicationContext ac = BksStaticContainer.getAc();
		if (ac == null)
			throw new MCFException("Application context has not been initialized.");

		ReferenzdatenServiceFacade rdsService = 
			(ReferenzdatenServiceFacade) ac.getBean("referenzdatenServiceFacade");
		if (rdsService == null)
			throw new MCFException("Could not get Bean referenzdatenServiceFacade from application context.");

		for (int i=0; i<refTargetList.length;i++){
			String key = refTargetList[i];
			RefdataMapObject refdata = BksRefDataCacheHandler.getRefdataMap().get(key);
			if (refdata==null)
				continue;
			HashMap<String, String> filter = new HashMap<String, String>();
			Set<String> keys = refdata.getFilter().keySet();
			Iterator<String> keyiter = keys.iterator();
			while (keyiter.hasNext()){
				String filterKey = keyiter.next();
				String valueKey = refdata.getFilter().get(filterKey);
				Object ccbValue = valueList.get(valueKey);
				if (ccbValue != null)
					filter.put(filterKey, ccbValue.toString());
				else
					filter.put(filterKey, null);
			}
			if (filter != null && filter.size() >0){
				String filterText="";
				Iterator<?> iterator = filter.keySet().iterator();		 
				while (iterator.hasNext()){
					String filterKey = (String)iterator.next();
					String filterValue = filter.get(filterKey);
					if (filterValue != null) {
						filterText += "Key : " + filterKey + " Value : " + filterValue + "\n";
					}
				}
				logger.debug("Getting ref data for : \n"+filterText);
				ReferenzdatenContainer refContainer = 
					rdsService.getReferenzdaten(refdata.getRdsCategory(), filter, null);
				if (refContainer != null && refContainer.getReferenzdaten().size() > 0){
					String value = refContainer.getReferenzdaten().get(0).getValue(refdata.getRdsOutput());
					if (value != null && refdata.getTargetType() == null)
						returnValue.put(refdata.getAttributePath(), value);
					else if (value != null){
						Class<?> targetClass = Class.forName(refdata.getTargetType());
						Object typedValue = 
							targetClass.getConstructor(new Class[] { String.class }).newInstance(value.replace(',', '.'));
						returnValue.put(refdata.getAttributePath(), typedValue);
					}
				} else {
					String message = "No reference data found with rule : " + refdata.getRdsCategory() + " for : \n";
					message += filterText;
					throw new BksDataException(message);
				}
			}
		}
		return returnValue;
	}

	/**
	 * This method prefetches data, which might be needed in the next service
	 * request, and cach for speed up the future service requests.
	 * 
	 * @param customerNumber
	 *            Customer Number for prefetching.
	 * @param sourceServiceName
	 *            Name of the triggering service.
	 * @param input
	 *            Input object of the triggering service.
	 * @param output
	 *            Output object of the triggering service.
	 */
	public void prefetch(String customerNumber, String accessNumber, String sourceServiceName,
			InputData input, OutputData output) {
		try {
			ArrayList<String> nextServices = BksRefDataCacheHandler
					.getNextServiceMap().get(sourceServiceName);

			for (int i = 0; nextServices != null && i < nextServices.size(); i++) {
				String serviceName = nextServices.get(i);

				if (fetchFromCache(serviceName, customerNumber,accessNumber) == null) {
					Object serviceHnd = Class.forName(
							"net.arcor.bks.requesthandler." + serviceName
									+ "Handler").newInstance();
					InputData prefetchInput = fillInputForService(
							sourceServiceName, serviceName, input, output);
					invokeMethod(serviceHnd, "setInput", prefetchInput,
							InputData.class);
					invokeMethod(serviceHnd, "setCustomerNumber", customerNumber,
							String.class);
					invokeMethod(serviceHnd, "start", null, null);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public int getDesiredNewElement() {
		return desiredNewElement;
	}

	public void setDesiredNewElement(int desiredNewElement) {
		this.desiredNewElement = desiredNewElement;
	}

	public int getOrderListNo() {
		return orderListNo;
	}

	public void setOrderListNo(int orderListNo) {
		this.orderListNo = orderListNo;
	}

	/**
	 * This method instantiate and fill the input object for the service being
	 * prefetched using the input and output object of the current service
	 * according to ref data specifications.
	 * 
	 * @param sourceServName
	 *            Name of the triggering service.
	 * @param targetServName
	 *            Name of the service being prefetched.
	 * @param input
	 *            Input object of the triggering service.
	 * @param output
	 *            Output object of the triggering service.
	 * 
	 * @return input object of the service being prefetched.
	 */
	private InputData fillInputForService(String sourceServName,
			String targetServName, InputData input, OutputData output)
			throws Exception {
		ArrayList<PrefetchReferenceObject> theList = BksRefDataCacheHandler
				.getPrefetchMap().get(sourceServName + ";" + targetServName);
		String packageName = theList.get(0).getTargetPackage();
		InputData prefetchInput;
		prefetchInput = (InputData) Class.forName(
				packageName + "." + targetServName + "Input").newInstance();
		for (int i = 0; i < theList.size(); i++) {
			String sourceTag = theList.get(i).getSourceDataTag();
			String sourceObject = theList.get(i).getSourceDataObject();
			String targetTag = theList.get(i).getTargetDataTag();
			Object sourceValue = null;
			if (sourceObject.equals("Output"))
				sourceValue = getSourceValue(output, sourceTag);
			if (sourceObject.equals("Input"))
				sourceValue = getSourceValue(input, sourceTag);
			populateTargetInput(prefetchInput, targetTag, sourceValue,packageName);
		}
		return prefetchInput;
	}

	/**
	 * This method populates an specific field in the target input object with
	 * value. If the targetTag contains ; the function recusively goes to the
	 * next level until it finds the actual target field.
	 * 
	 * @param prefetchInput
	 *            Input object being populated.
	 * @param targetTag
	 *            Tagname to identify the field being populated.
	 * @param value
	 *            The value.
	 * @param packageName 
	 */
	private void populateTargetInput(Object prefetchInput, String targetTag,
			Object value, String packageName ) throws Exception {
		int index = targetTag.indexOf(";");
		if (index > 0) {
			String parentFieldName = targetTag.substring(0, index);
			String cascadedFiledName = targetTag.substring(index + 1);
			Object anObject = findObjectByTypeName(prefetchInput,
					parentFieldName,null);
			populateTargetInput(anObject, cascadedFiledName, value,packageName);
		} else {
			invokeMethod(prefetchInput, "set" + targetTag, value, value
					.getClass());
		}
	}

	/**
	 * This method returns the value of the field identified by sourceTag.
	 * 
	 * @param sourceObject
	 *            The object containing the value. This is either input or
	 *            output object of the current service.
	 * @param sourceTag
	 *            Tagname to identify the field being fetched.
	 * 
	 * @return value The value of the field.
	 */
	private Object getSourceValue(Object sourceObject, String sourceTag)
			throws Exception {
		int index = sourceTag.indexOf(";");
		if (index > 0) {
			String parentFieldName = sourceTag.substring(0, index);
			String cascadedFiledName = sourceTag.substring(index + 1);
			Object anObject = invokeMethod(sourceObject, "get"
					+ parentFieldName, null, null);
			return getSourceValue(anObject, cascadedFiledName);
		} else {
			Object anObject = invokeMethod(sourceObject, "get" + sourceTag,
					null, null);
			return anObject;
		}
	}

	public Auftragsmodell createErrorAm(String errorCode, String errorText,String[] params) throws Exception {
		Auftragsmodell am = new de.arcor.aaw.auftragsmodell.ObjectFactory().createAuftragsmodell();
		am.setAawSpracheVersion(Version.getVersion());
		am.setAdonisModellVersion(
				ASKRepositoryFactory.getAskRepository().getAdonisModellVersion());
		String mdtsVersion = "DefaultVersion";
		ApplicationContext ac = BksStaticContainer.getAc();
		if (ac == null)
			throw new MCFException("Application context has not been initialized.");
		ReferenzdatenServiceFacade rdsService = 
			(ReferenzdatenServiceFacade) ac.getBean("referenzdatenServiceFacade");
		ReferenzdatenContainer referenzdatenContainer  = rdsService.getReferenzdaten("MDTSVersion", null);
		if (referenzdatenContainer != null && referenzdatenContainer.getReferenzdaten().size() >0)
			mdtsVersion = referenzdatenContainer.getReferenzdaten().get(0).getValue("MDTSVersion");
		am.setMdtsVersion(mdtsVersion);
		try {
			Meldung message = new Meldung();
			message.setTyp(MeldungsTypen.FEHLER);
			message.setCode(errorCode);
			String[] texts = getRefErrorText(errorCode,params);
			if (texts != null && texts[0] != null)
				message.setStandardtext(texts[0]);
			else
				message.setStandardtext(errorText);
			if (texts != null && texts[1] != null)
				message.setStandardanzeigetext(texts[1]);
			else
				message.setStandardanzeigetext(errorText);
			message.setZeitstempel(TimePoint.from(System.currentTimeMillis()));
			am.getMeldungen().add(message);
		} catch (Throwable e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			throw new Exception(e);
		}
		return am;
	}

	private String[] getRefErrorText(String errorCode,String[] params) {
		ApplicationContext ac = BksStaticContainer.getAc();
		if (ac != null){
			ReferenzdatenServiceFacade rdsService = 
				(ReferenzdatenServiceFacade) ac.getBean("referenzdatenServiceFacade");
			if (rdsService != null){
				String refText = rdsService.getDisplayText(errorCode, "BKS", params);
				String adviceText = rdsService.getAdviceText(errorCode);
				return new String [] {refText,adviceText};
			}
		}
		return null;
	}

	public void loadCache(String key, String value) {
		Element cacheElement = new Element(key,value);
		theCache.put(cacheElement);
	}

	public void handleFunctions( Auftragsposition ap,HashMap<String,Object> functionList) {
		Set<String> keys = functionList.keySet();
		Iterator<String> keyiter = keys.iterator();
		while (keyiter.hasNext()){
			String path = keyiter.next();
			Object value = functionList.get(path);
			if (value instanceof ArrayList<?>) {
				
				for (Integer i = 1; i <= ((ArrayList<?>)value).size(); i++) {
					Object subValue = ((ArrayList<?>)value).get(i-1);
					String tmpPath = path.replaceAll("\\?", i.toString());
					ap.setAttributeByPath(tmpPath, subValue);
				}
			} else {
				path = path.replaceAll("\\?", "1");
				ap.setAttributeByPath(path,value);
			}
		}
	}

	public void handleFunctions(String functionName, Auftragsmodell am,
			HashMap<String,ArrayList<HashMap<String,Object>>> functionList) {
		Bestand bestand = (Bestand) am.getInhalt().get(0);
		Auftragsposition ap = bestand.getAuftragsposition().get(0);

		List<Funktion> flist = ap.getFunktion();
		Funktion hardware = null;
		for (int i=0;i<flist.size();i++){
			Funktion current=flist.get(i);
			if(current.getFunktionName().equalsIgnoreCase(functionName)){
				hardware = current;
				break;
			}
		}
		if (hardware == null){
			hardware = new Funktion();
			ap.getFunktion().add(hardware);
			hardware.setApeNummer(1);
			
			hardware.setFunktionName(functionName);
			hardware.setFunktionType(functionName);
		}	

		Set<String> typeKeys = functionList.keySet();
		Iterator<String> typeKeyiter = typeKeys.iterator();
		while (typeKeyiter.hasNext()){
			int counter = 1;
			String typeKey = typeKeyiter.next();
			ArrayList<HashMap<String, Object>> typedList = functionList.get(typeKey);
			for(int i=0;i<typedList.size();i++){
				Set<String> keys = typedList.get(i).keySet();
				Iterator<String> keyiter = keys.iterator();
				DynamicElement clir = new DynamicElement();
				clir.setDynamicElementName(typeKey);
				clir.setDynamicElementType(typeKey);
				clir.setApeNummer(counter++);

				while (keyiter.hasNext()){
					String key = keyiter.next();
					Object value = typedList.get(i).get(key);
					clir.setDynamicElementAttribute(AawSpracheUtil.firstCharacterLowerCase(key),value);		
				}
				hardware.getDynamicElements().add(clir);
			}
		}
	}

	public String mapToTrueFalse(String charValue){
		if(convertToBoolean(charValue))
			return "true";
		return "false";
	}
	public Boolean convertToBoolean(String charValue){
		if (charValue.equalsIgnoreCase("Y") || charValue.equalsIgnoreCase("YES") ||
			charValue.equalsIgnoreCase("J") || charValue.equalsIgnoreCase("Ja")  ||
			charValue.equalsIgnoreCase("1") || charValue.equalsIgnoreCase("true")  ||
			charValue.equalsIgnoreCase("T")||charValue.equalsIgnoreCase("FSK18Plus"))
			return true;
		return false;
	}

	public String convertValidationResult(String state){
		if(state.equalsIgnoreCase("NEW"))
			return "IN_VALIDATION";
		if(state.equalsIgnoreCase("IN_PROGRESS"))
			return "IN_VALIDATION";
		if(state.equalsIgnoreCase("FAILED"))
			return "BOUNCED";
		if(state.equalsIgnoreCase("SUCCESS"))
			return "VALIDATED";
		return null;
	}

	public String mapChargeType(String chargeType){
		if(chargeType.equalsIgnoreCase("ONE-TIME"))
			return "oneTime";
		if(chargeType.equalsIgnoreCase("RECURRING"))
			return "recurring";
		if(chargeType.equalsIgnoreCase("USAGE"))
			return "usage";
		return null;
	}

	public String mapAccessTech(String accessTech){
		if (accessTech == null)
			return null;
		if(accessTech.equalsIgnoreCase("ADSL-AnnexJ"))
			return "ADSL";
		return accessTech;
	}

	public String getTvOptionType(String serviceCode){
		if(serviceCode.startsWith("I60"))
			return "PREMIUMTV";
		if(serviceCode.startsWith("I61"))
			return "SVOD";
		if(serviceCode.startsWith("I620"))
			return "THEMBUNDLE";
		if(serviceCode.startsWith("I621"))
			return "THEMBUNDLEHD";
		return null;
	}
	public String truncateString360(String charValue){
		return truncateString(charValue,360);
	}

	public String truncateString100(String charValue){
		return truncateString(charValue,100);
	}

	public String truncateString50(String charValue){
		return truncateString(charValue,50);
	}

	public String truncateString60(String charValue){
		return truncateString(charValue,60);
	}

	public String truncateStringNumeric15(String charValue){
		charValue = removeNonNumerics(charValue);
		return truncateString(charValue,15);
	}

	public String truncateString15(String charValue){
		return truncateString(charValue,15);
	}

	public String truncateString30(String charValue){
		return truncateString(charValue,30);
	}

	private String truncateString(String charValue, int maxSize) {
		if (charValue.length()>maxSize)
			return charValue.substring(0, maxSize);
		return charValue;
	}

	public Boolean handleInvalidAddress(String charValue){
		if (charValue.equals("###"))
			return true;
		return false;
	}

	public String clearInvalidIndicator(String charValue){
		if (charValue.equals("###"))
			return null;
		return charValue;
	}
	
	public String convertConditionName(String charValue){
		if (charValue.equalsIgnoreCase("Ja"))
			return "Monatspreis mit Einmalpreisbefreiung";
		return "Monatspreis ohne Einmalpreisbefreiung";
	}

	public Boolean convertAccountState(String charValue){
		if (charValue.equals("SUSPENDED") || charValue.equals("PENDING_SUSPEND"))
			return true;
		return false;
	}

	public BigDecimal convertToBigDecimal(String charValue){
		return new BigDecimal(charValue.replace(',', '.'));
	}

	public BigDecimal convertToPosBigDec(String charValue){
		BigDecimal value = new BigDecimal(charValue.replace(',', '.'));
		if (value.intValue()<1)
			return new BigDecimal(1);
		return value;
	}

	public BigDecimal convertByRefdataToBigDecimal(String primaryValue) throws Exception{
		HashMap<String,String> ccbAawMap =BksRefDataCacheHandler.getCcbAawMap();
		return new BigDecimal(ccbAawMap.get(primaryValue));
	}

	public String trimProductName(String charValue){
		String result = charValue.trim();
		if (result.startsWith("Arcor"))
			result = result.substring(5, result.length()).trim();
		if (result.startsWith("-"))
			result = result.substring(1, result.length()).trim();
		return  result;
	}

	public String getDECountryCode(String charValue){
		if (charValue==null)
			return null;
		int i=0;
		while(i<charValue.length()&&!isNumeric(charValue.charAt(i++)));
		if (i<charValue.length())
			charValue=charValue.substring(i-1,charValue.length());
		else
			return null;
		return normCountryCode(charValue);	
	}

	public String getDEAreaCode(String charValue){
		if (charValue==null)
			return null;
		int i=0;
		while(i<charValue.length()&&!isNumeric(charValue.charAt(i++)));
		if (i<charValue.length())
			charValue=charValue.substring(i-1,charValue.length());
		else
			return null;
		return normCityCode(charValue);	
	}

	public String getDELocalNumber(String charValue){
		if (charValue==null)
			return null;
		int i=0;
		while(i<charValue.length()&&!isNumeric(charValue.charAt(i++)));
		if (i<charValue.length())
			charValue=charValue.substring(i-1,charValue.length());
		else
			return null;
		return normLocalNumber(charValue);
	}

	public Integer convertToInteger(String charValue) throws BksFunctionalException{
		Integer result=0;
		try {
			result = Integer.parseInt(charValue);
		} catch (NumberFormatException e) {
			throw new BksFunctionalException("Cannot convert "+charValue+" to integer.");
		}
		return  result;
	}

	public CalendarDate convertToDate(String charValue) throws BksFunctionalException{
		CalendarDate date;
		try {
			date = CalendarDate.from(charValue, "yyyy-MM-dd");
		} catch (Exception e) {
			throw new BksFunctionalException("Cannot convert "+charValue+" to date.");
		}
		return date;
	}
	
	public TimePoint convertToTimePoint(String charValue) throws BksFunctionalException{
		TimePoint date;
		try {
			TimeZone cet = TimeZone.getDefault();
			date = TimePoint.parseFrom(charValue, "yyyy-MM-dd",cet);
		} catch (Exception e) {
			throw new BksFunctionalException("Cannot convert "+charValue+" to date.");
		}
		return date;
	}
	
	public CalendarDate calcDueDate(String charValue) throws BksFunctionalException{
		java.util.Date date;
		try {
			java.text.SimpleDateFormat sdf = 
			      new java.text.SimpleDateFormat("yyyy-MM-dd");
			java.util.Date today = new java.util.Date();
			date = sdf.parse(charValue);
			if (date.getTime() > today.getTime())
				return CalendarDate.from(date.toString(), "yyyy-MM-dd");
			Calendar c1 = Calendar.getInstance();
			c1.setTime(date);
			c1.add(Calendar.MONTH, 1);
			if (c1.getTime().getTime() > today.getTime())
				return CalendarDate.from(c1.getTime().toString(), "yyyy-MM-dd");
			else
				throw new BksFunctionalException("The cycle due date is way in the past.");
		} catch (Exception e) {
			throw new BksFunctionalException("Cannot convert "+charValue+" to date.");
		}
	}
	
	public CalendarDate convertOpmToDate(String charValue) throws BksFunctionalException{
		// What should the termination date be after?
		CalendarDate date;
		try {
			date = CalendarDate.from(charValue, "dd.MM.yyyy");
		} catch (Exception e) {
			throw new BksFunctionalException("Cannot convert "+charValue+" to date.");
		}
		return date;
	}
	
	public CalendarDate convertCcbToDate(String charValue) throws BksFunctionalException{
		// What should the termination date be after?
		CalendarDate date;
		try {
			if (charValue.length() > 10)
				charValue = charValue.substring(0,10);
			date = CalendarDate.from(charValue, "yyyy.MM.dd");
		} catch (Exception e) {
			try {
				date = CalendarDate.from(charValue, "yyyy-MM-dd");
			} catch (Exception e1) {
				logger.warn("The "+charValue+" could not be converted to a date. A dummy date of 1999.01.01 returned.");
				date = CalendarDate.from("1999-01-01", "yyyy-MM-dd");
			}
		}
		return date;
	}
	
	public String prefixZero(String charValue){
		return  "0"+charValue;
	}

    private ArrayList<String> getCrossRefData(String key,String groupCode) throws Exception{
		String refDataSource = null;
		try {
			refDataSource = DatabaseClientConfig.getSetting("db.RefDataSource");
		} catch (Exception e) {
			// ignore
		}
		GeneralDAO dao=null;
		try {
			dao = (GeneralDAO)BksStaticContainer.getBksDataAccessObject(refDataSource,"GeneralDAO");
			ArrayList<HashMap<String, Object>> crossRef = dao.getReferenceDataForValue(groupCode,key);
			if (crossRef == null || crossRef.size() == 0)
				return null;
			
			ArrayList<String> secList = new ArrayList<String>();
			for (int i=0;i<crossRef.size();i++){
				secList.add((String)crossRef.get(i).get("SECONDARY_VALUE"));
			}
			return secList;
		} finally {
			if (dao != null)
				dao.closeConnection();
		}	
	}
    public ArrayList<String> getGeneralRefData(String groupCode) throws Exception{
		String refDataSource = null;
		try {
			refDataSource = DatabaseClientConfig.getSetting("db.RefDataSource");
		} catch (Exception e) {
			// ignore
		}
		GeneralDAO dao=null;
		try {
			dao = (GeneralDAO)BksStaticContainer.getBksDataAccessObject(refDataSource,"GeneralDAO");
			ArrayList<HashMap<String, Object>> genRef = dao.getReferenceData("databaseclient.GeneralCodeData",groupCode);
			if (genRef == null || genRef.size() == 0)
				return null;
			
			ArrayList<String> genList = new ArrayList<String>();
			for (int i = 0;i < genRef.size(); i++) {
				genList.add(genRef.get(i).get("VALUE").toString());
			}
			return genList;
		} finally {
			if (dao != null)
				dao.closeConnection();
		}	
	}
	public String getSalesPacketCode(String key) throws Exception{
		if (getCrossRefData(key,"PRIC_SALES")==null)
			return null;
		return  getCrossRefData(key,"PRIC_SALES").get(0);
	}
	public String mapRegion(String key) throws Exception{
		if (getCrossRefData(key,"POST_REG")==null)
			return null;
		return  getCrossRefData(key,"POST_REG").get(0);
	}
	public String zarPortIdenMock(String key) throws Exception{
		ArrayList<String> result = getCrossRefData(key,"MOC_ZAR_PI");
		return (result!=null&&result.size()>0)?result.get(0):"D009";
	}
	public String zarStatusMock(String key) throws Exception{
		ArrayList<String> result =  getCrossRefData(key,"MOC_ZAR_ST");
		return (result!=null&&result.size()>0)?result.get(0):"P02,L12";
	}
	public ArrayList<String> cramerMock(String key) throws Exception{
		ArrayList<String> result = getCrossRefData(key,"MOC_CRAMER");
		return result;
	}

	public String mapClassificationToProvider(String classification, String rootCustomer){
		HashMap<String,String> classProv =	BksRefDataCacheHandler.getClassProviderMap();
		String providerCode = mapCrossReference(classProv,classification);
		if (providerCode == null){
			HashMap<String,String> providerCodes =BksRefDataCacheHandler.getProviderCode();
			if (providerCodes.get(rootCustomer) != null)
				providerCode = providerCodes.get(rootCustomer);
			else
				providerCode = "ARCO";
		}
		return providerCode;
	}

	public String getWbciTechnology(String productType){
		return wbciTechnologyMap.get(productType);
	}

	public String convertCorpType(String key){
		if (key==null)
			return null;
		if (key.equals("UNREGISTERED"))
			return "kein Eintrag";
		if (key.equals("GEWE"))
			return "GEW";
		if (key.equals("GENR"))
			return "GR";
		if (key.equals("KOER"))
			return "KOE";
		if (key.equals("PART"))
			return "PAR";
		if (key.equals("PERR"))
			return "PR";
		if (key.equals("STAA"))
			return "STA";
		if (key.equals("STIF"))
			return "STI";
		if (key.equals("VERR"))
			return "VR";
		return key;
	}

	public String convertBandwidth(String key){
		return BandwidthMap.get(key);
	}

	public String convertBandwidthWHDSL(String key){
		if (key.equals("WHE LTE 3600"))
			return "Wh00A";
		if (key.equals("WHE LTE 7200"))
			return "Wh00B";
		if (key.equals("WHE LTE 21600"))
			return "Wh00J";
		if (key.equals("WHE LTE 50000"))
			return "Wh00D";
		return null;
	}

	public String convertBandwidthDSL(String key){
		return BandwidthDSLMap.get(key);
	}

	public String convertBandwidthBDSLADSL(String key){
		if (key.equals("DSL 1000") || key.equals("1000"))				
			return "V0118";			
		if (key.equals("DSL 2000") || key.equals("2000"))				
			return "V0174";			
		if (key.equals("DSL 6000") || key.equals("6000"))				
			return "I1217";			
		if (key.equals("DSL 16000") || key.equals("16000"))				
			return "I1218";			
		return null;
	}

	public String convertBandwidthBDSLVDSL(String key){
		if (key.equals("DSL 16000") || key.equals("16000"))				
			return "V018C";			
		if (key.equals("DSL 25000"))				
			return "V018G";			
		if (key.equals("DSL 50000"))				
			return "V018H";			
		if (key.equals("DSL 100000"))				
			return "V018N";			
		return null;
	}

	public String convertBandwidthName(String key){
		String serviceCode = convertBandwidthDSL(key);
		return convertServiceName(serviceCode);
	}
			
	public String convertServiceName(String serviceCode){
		String refDataSource = null;
		try {
			refDataSource = DatabaseClientConfig.getSetting("db.RefDataSource");
		} catch (Exception e) {
			// ignore
		}
		GeneralDAO dao=null;
		
		try {
			if (serviceCode != null){
				dao = (GeneralDAO)BksStaticContainer.getBksDataAccessObject(refDataSource,"GeneralDAO");
				ArrayList<HashMap<String, Object>> services = dao.getServiceName(serviceCode);
				if (services != null && services.size() > 0)
					return (String) services.get(0).get("NAME");

			}
		} catch (Exception e) {
		} finally {
			if (dao != null)
				try {
					dao.closeConnection();
				} catch (Exception e) {
				}
		}	

		return null;
	}
			
	public String convertDetachedHouse(String key){
		if (key == null)
			return " ";
		String[] fields = key.split(",");
		if (!key.trim().toUpperCase().startsWith("MFH") || fields.length < 4 ||
			(!fields[1].trim().equalsIgnoreCase("VH") && !fields[1].trim().equalsIgnoreCase("HH")) ||
			(!fields[3].trim().equalsIgnoreCase("li") && !fields[3].trim().equalsIgnoreCase("mi") && 
					!fields[3].trim().equalsIgnoreCase("re"))){
			if (key.length() > 50)
				key=key.substring(0,50);
			return key;
		}		
		return null;
	}

	public String checkAreaCode(String key){
		if (key == null)
			return null;
		if (Pattern.matches("0[1-9]{1}[0-9]{1,4}", key))
			return key;
		return null;
	}

	public String checkAppartmentHouse(String key){
		if (key == null)
			return null;
		String[] fields = key.split(",");
		if (fields[0].trim().equalsIgnoreCase("MFH") && fields.length >= 4) { 
			if (fields[1].trim().equalsIgnoreCase("VH"))
				fields[1]="frontBuilding";
			else if (fields[1].trim().equalsIgnoreCase("HH"))
				fields[1]="rearBuilding";
			else
				return null;
			if (fields[2].trim().length() >3)
				fields[2] = fields[2].trim().substring(0,3);
			else
				fields[2] = fields[2].trim();
				
			if (fields[3].trim().equalsIgnoreCase("li"))
				fields[3]="left";
			else if (fields[3].trim().equalsIgnoreCase("mi"))
				fields[3]="middle";
			else if (fields[3].trim().equalsIgnoreCase("re"))
				fields[3]="right";
			else
				return null;
			String retVal = fields[0];
			for (int j = 1; j < fields.length; j++) {
				retVal += "," + fields[j];
			}
			return retVal;
		}
		return null;
	}
	
	public String convertDirectoryEntryType(String key){
		if (key.equals("Einzeleintrag"))
			return "Standard";
		if (key.equals("Anlage"))
			return "Extended";
		return null;
	}
	public String convertConnection(String key){
		if (key.equals("keiner"))
			return "NEW";
		if (key.equals("analog"))
			return "ANALOG";
		if (key.equals("digital"))
			return "DIGITAL";
		if (key.equals("Mehrgeräteanschluss"))
			return "P2MP";
		if (key.equals("Anlagenanschluss"))
			return "P2P";
		if (key.equals("Primärmultiplexanschluss"))
			return "PRI";
		return null;
	}
	public String convertMoP(String mop){
		if (mop.equals("DIRECT_DEBIT"))
			return mop;
		return "MANUAL";
	}
	public String mapFunction(String key){
		HashMap<String,String> functions =BksRefDataCacheHandler.getFunction();
		return mapCrossReference(functions,key);
	}
	public String mapAccess(String key){
		HashMap<String,String> accesses =BksRefDataCacheHandler.getAccess();
		return mapCrossReference(accesses,key);
	}
	public String mapVariant(String key){
		HashMap<String,String> variants =BksRefDataCacheHandler.getVariant();
		return mapCrossReference(variants,key);
	}
	public String mapBankName(String key){
		HashMap<String,String> bankNames =BksRefDataCacheHandler.getBankNameMap();
		String bankName = mapCrossReference(bankNames,key);
		if (bankName!= null && bankName.length() > 58)
			bankName = bankName.substring(0,58);
		return bankName;
	}
	public String defaultBankAccount(String key){
		key = removeNonNumerics(key);
		if (key.length() > 10) 
			return "0000000000";
		return key;
	}
	public String defaultBankCode(String key){
		 if (key.length() > 8) 
			 return "00000000";
		 return key;
	}
	private String mapCrossReference(HashMap<String, String> functions,
			String key) {
		return functions.get(key);
	}

	public CalendarDate getContractEndDate(Object startDate,String minDurUnit,Integer minDurValue,
			String extendUnit,Integer extendValue,boolean bAutoExtend){
		if (startDate == null || (!(startDate instanceof Date) && !(startDate instanceof Timestamp)))
			return null;
		   // What should the termination date be after?
		Date dt = (Date)startDate;
		Date tmpDate = (Date) dt.clone();
		Date   minDate = addDuration(tmpDate,minDurUnit,minDurValue);
		java.util.Date today = new java.util.Date();
		if (minDate.getTime() > today.getTime() || !bAutoExtend)
			return convertSqlDateToCalendar(minDate);
		else {
			Date endDate = calcAutoExtContractDate(minDate,extendUnit,extendValue,null,null);
			return convertSqlDateToCalendar(endDate);
		}
	}

	public CalendarDate getContractEndDateWithNoticePeriod(Object startDate,String minDurUnit,Integer minDurValue,
			String extendUnit,Integer extendValue,String noticeUnit,Integer noticeValue,boolean bAutoExtend){
		if (startDate == null || (!(startDate instanceof Date) && !(startDate instanceof Timestamp)))
			return null;

		if (noticeUnit==null || noticeValue==null)
			return getContractEndDate(startDate, minDurUnit, minDurValue, extendUnit, extendValue, bAutoExtend);
		Date dt = (Date)startDate;
		Date tmpDate = (Date) dt.clone();
		Date   minDate = addDuration(tmpDate,minDurUnit,minDurValue);
		java.util.Date today = new java.util.Date();
		if (minDate.getTime() > addDuration(today,noticeUnit,noticeValue).getTime() || !bAutoExtend)
			return convertSqlDateToCalendar(minDate);
		else {
			Date endDate = calcAutoExtContractDate(minDate,extendUnit,extendValue,noticeUnit,noticeValue);
			return convertSqlDateToCalendar(endDate);
		}
	}

	public CalendarDate convertSqlDateToCalendar(Object date){
		if (date == null || (!(date instanceof Date) && !(date instanceof Timestamp)))
			return null;
		java.text.SimpleDateFormat sdf = 
		      new java.text.SimpleDateFormat("yyyy-MM-dd");
		return CalendarDate.from(sdf.format(date), "yyyy-MM-dd");
	}

	public Date convertCalendarToSqlDate(Object calendarDate){
		if (calendarDate == null || !(calendarDate instanceof CalendarDate))
			return null;
		CalendarDate calDate = (CalendarDate)calendarDate;
		Calendar c1 = Calendar.getInstance();
		c1.clear();
		c1.set(calDate.breachEncapsulationOf_year(),calDate.breachEncapsulationOf_month()-1,calDate.breachEncapsulationOf_day());
		java.util.Date retDate = new java.util.Date();
		retDate = c1.getTime();
		return retDate;
	}

	private Date calcAutoExtContractDate(Date startDate,String durationUnit,Integer durationValue,String noticeUnit,Integer noticeValue)
	{
		// What should the termination date be after?
		Date derivedDate = startDate;
		java.util.Date today = new java.util.Date();
		long compareTime = today.getTime();
		if (noticeUnit!=null&&noticeValue!=null)
		   compareTime =  addDuration(today,noticeUnit,noticeValue).getTime();
		while (derivedDate.getTime() < compareTime)
		{
			derivedDate.setTime(derivedDate.getTime()+24*60*60*1000);
			derivedDate = addDuration(derivedDate,durationUnit,durationValue);
		}
		return derivedDate;
	}

	private Date addDuration(Date oBaseDate,
			String oDurationUnitRd,
			Integer lMultiplier)
	{
		Date retVal = oBaseDate;

		Calendar c1 = Calendar.getInstance();
		c1.setTime(oBaseDate);
		if(oDurationUnitRd.equals("MONTH"))
		{
			c1.add(Calendar.MONTH, lMultiplier);
			c1.add(Calendar.DAY_OF_MONTH, -1);
			retVal = c1.getTime();
		}
		else if(oDurationUnitRd.equals("YEAR"))
		{
			c1.add(Calendar.YEAR, lMultiplier);
			c1.add(Calendar.DAY_OF_MONTH, -1);
			retVal = c1.getTime();
		}
		else if(oDurationUnitRd.equals("DAY"))
		{
			//c1.add(Calendar.DATE, lMultiplier);
			//retVal = c1.getTime();
			retVal.setTime(oBaseDate.getTime() + (long)lMultiplier*24L*60L*60L*1000L);	
		}
		return retVal;
	}
	public Object setAttributeForPath(Object anObject, String prefix,
    		String methodName, Object methodValue) throws Exception {
    	Object returnObject = null;
    	logger.debug(methodName+"->"+methodValue);
    	if (prefix == null)
    		prefix = "";
    	int index = methodName.indexOf(".");
    	// call this Java method recursively in case the method name is nested
    	if (index >0){
    		String parentTypeName = methodName.substring(0, index);
    		String cascadedMethodName = methodName.substring(index+1);
    		Object cascadedObject = findObjectByTypeName(anObject,parentTypeName,prefix);
    		if (cascadedObject==null)
    			return null;
    		returnObject = setAttributeForPath(cascadedObject,prefix,cascadedMethodName,methodValue);
    	}
    	// otherwise call the desired method directly
    	else {
    		orderListNo = 0;
    		desiredNewElement = -1;
        	try {
				Method m = anObject.getClass().getMethod("get" + methodName);
				Object returnObj = m.invoke(anObject, (Object[]) null);
				if(returnObj instanceof List<?>){
					((List)returnObj).add(methodValue);
					return null;
				}
			} catch (Exception e) {
				//ignore
			} 

    		Method[] methods = anObject.getClass().getMethods();
    		Method theMethod = null;
    		// find the right method
    		for (int i=0;i<methods.length;i++) {
    			if (methods[i].getName().equals(prefix + methodName)){
    				theMethod = methods[i];
    				break;
    			}
    		}
    		// raise an exception if the method is not found
    		if (theMethod == null)
    			throw new NoSuchMethodException("Method " + prefix + methodName + " could not be found.");
    		Class[] types = theMethod.getParameterTypes();
    		// if the method has parameters, handle dates and numbers accordingly, 
    		// and treat all other inputs as strings
    		if (types.length > 0) {
    			if (types[0].isEnum()) {
    				String value = camelToUnderscore((String)methodValue);
    				returnObject = theMethod.invoke(anObject, Enum.valueOf(types[0], value));
    			} else if (types[0].getName().equals("java.lang.Integer"))
    				returnObject = theMethod.invoke(anObject, Integer.parseInt((String)methodValue));
    			else if (types[0].getName().equals("java.lang.Long"))
    				returnObject = theMethod.invoke(anObject, Long.parseLong((String)methodValue));
    			else if (types[0].getName().equals("int"))
    				returnObject = theMethod.invoke(anObject, Integer.parseInt((String)methodValue));
    			else if (types[0].getName().equals("long"))
    				returnObject = theMethod.invoke(anObject, Long.parseLong((String)methodValue));
    			else 
    				returnObject = theMethod.invoke(anObject, methodValue);
    		}
    		// otherwise call method with empty parameter list
    		else {
    			returnObject = theMethod.invoke(anObject, (Object[]) null);
    		}
    	}
    	return returnObject;
    }

    private String camelToUnderscore(String methodValue) {
        String result = "";
        boolean bContainsDigit = methodValue.matches(".*[0-9]+.*");
        if (methodValue == null || (methodValue.equals(methodValue.toUpperCase()) && !bContainsDigit))
        	return methodValue;
        char[] array = methodValue.toCharArray();
        for (int i = 0; i < array.length; i++) {
			if (i!=0 && ((Character.isUpperCase(array[i])&& !Character.isUpperCase(array[i-1])) 
				|| Character.isDigit(array[i])))
				result+="_";
			result+=array[i];
		}
		return result.toUpperCase();
	}

	private Object findObjectByTypeName(Object anObject, String parentTypeName, String prefix)
    throws Exception {
    	Method m = anObject.getClass().getMethod("get" + parentTypeName);
    	Object returnObj = m.invoke(anObject, (Object[]) null);
    	if (returnObj == null) {
    		if (prefix!=null&&prefix.equals("get"))
    			return null;
    		Class theType = m.getReturnType();
    		returnObj = theType.newInstance();
    		Method m1 = anObject.getClass().getMethod(
    				"set" + parentTypeName, theType);
    		m1.invoke(anObject, returnObj);
    	} else if (returnObj instanceof List<?> && (((List)returnObj).size() == 0 || desiredNewElement==orderListNo)){
    		Type t = m.getGenericReturnType();
    		String className = null;
    		if(t instanceof ParameterizedType) { 
    			ParameterizedType pt = (ParameterizedType) t; 
    			Type myitemclass = pt.getActualTypeArguments()[0];
    			className = myitemclass.toString().split(" ")[1];
    			Class itemType = Class.forName(className);
    			Object item = itemType.newInstance();
    			((List)returnObj).add(item);
    			return item;
    		}
    	} else if (returnObj instanceof List<?> && ((List)returnObj).size() != 0){
    		orderListNo++;
        	Method m1 = returnObj.getClass().getMethod("get",int.class);
        	int lastIndex = ((List<?>)returnObj).size()-1;
        	Object item = m1.invoke(returnObj,lastIndex);
    		return item;
    	}
    	return returnObj;
    }

	public String decryptCustPasswd(String encryptedpw) throws Exception {
		String decryptPasswd = Cryptography.ccbDecrypt(customerEncKey, encryptedpw,false);		
		return decryptPasswd;
	}

	public String defaultSalutation(String methodValue) {	     
	    if (methodValue.equals("Frau"))
	        	return methodValue;
	    else if(methodValue.equals("Herr"))
	        	return methodValue;	    
	    return "Firma";	        
    }
	
	public String normCountryCode(String input){
		try {
			String normNumber = normalizePhoneNumber(input);
			if (normNumber==null || !normNumber.contains("/"))
				return null;
			if (fetchCityCode(normNumber).length()>6 || fetchCityCode(normNumber).length()<2)
				return null;
			if (normNumber.startsWith("+"))
				return normNumber.substring(1,normNumber.indexOf("0"));
			return "49";
		} catch (Exception e) {
			return null;
		}
	}
	private String fetchCityCode(String normNumber) {
		if (normNumber.startsWith("+"))
			return normNumber.substring(normNumber.indexOf("0")+1,normNumber.indexOf("/"));
		return normNumber.substring(1,normNumber.indexOf("/"));
	}

	public String normCityCode(String input){
		try {
			String normNumber = normalizePhoneNumber(input);
			if (normNumber==null || !normNumber.contains("/"))
				return null;
			if (fetchCityCode(normNumber).length()>6 || fetchCityCode(normNumber).length()<2)
				return null;
			return fetchCityCode(normNumber);
		} catch (Exception e) {
			return null;
		}
	}
	public String normLocalNumber(String input){
		try {
			String normNumber = normalizePhoneNumber(input);
			if (normNumber==null || !normNumber.contains("/"))
				return null;
			if (fetchCityCode(normNumber).length()>6 || fetchCityCode(normNumber).length()<2)
				return null;
			String localNum = normNumber.substring(normNumber.indexOf("/")+1,normNumber.length());
			return truncateString15(localNum);
		} catch (Exception e) {
			return null;
		}
	}
	/**
     * normalizePhoneNumber Algorithm:
     * 1.) trim leading and trailing whitespace
     * 2.) leading '00' is replaced by '+'
     * 3.) if (str[0] is '+' and there is a non-numeric character at str[3]) then delete str[3]
     * 4.) find the first occurence of a non-numeric character in str (starting at str[1]) and replace it with a '/'
     * 5.) delete all other occurences of non-numeric characters in str (starting at str[1])
     * 6.) if (str contains '/' and (str[0] is not '+' and str[0] is not '0')) then str = '0' + str
     */
    public static String normalizePhoneNumber(String input) {
		String result= null;
		try {
        // step 1:
			result = input.trim();
			// step 1b: delete '(0)', '.0.', '(' and ')':
			result = result.replace("(0)", "");
			result = result.replace(".0.", "");
			result = result.replace("(", "");
			result = result.replace(")", " ");
			
			// deal with leading "49 "
			if (result.startsWith("49") && !isNumeric(result.charAt(2))) {
			    result = "+" + result;
			}
			
			//steb 1c:
			if (result.charAt(0) == '0'  && !isNumeric(result.charAt(1))) {
			    result = deleteCharAt(result, 1);
			}
			
			// step 2:
			if (result.startsWith("00")) {
			    result = '+' + result.substring(2, result.length());
			}
			// step 3:
			if (result.charAt(0) == '+') {
			    // remove superfluous blank:
			    if (!isNumeric(result.charAt(1))) {
			        result = deleteCharAt(result, 1);
			    }
			    while (!isNumeric(result.charAt(3))) {
			    	result = deleteCharAt(result, 3);
			    }
			}
			// step 4 + 5:
			int posFirstNonNumeric;
			if (result.startsWith("+")) {
			    posFirstNonNumeric = 1 + indexOfFirstNonnumeric(result.substring(1));
			}
			else {
			    posFirstNonNumeric = indexOfFirstNonnumeric(result);
			}
			if (posFirstNonNumeric > 0) {
				if (result.startsWith("+")&&result.charAt(3)!='0'){
					result=result.substring(0, 3)+"0"+result.substring(3,result.length());
					posFirstNonNumeric++;
				}
			    result =
			            result.substring(0, posFirstNonNumeric) + '/'
			                    + removeNonNumerics(result.substring(posFirstNonNumeric + 1, result.length()));
			}
			// step 6:
			if (result.contains("/") && !(result.charAt(0) == '+' || result.charAt(0) == '0')) {
			    result = '0' + result; //.trim();
			}
		} catch (Exception e) {
			return null;
		}
        return result;
    }

    /**
     * remove all non-numeric characters from a String 
     * @param input the String to be cleansed
     * @return the resulting String
     */
    private static String removeNonNumerics(String input) {
        String result = "";
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (isNumeric(c)) {
                result += c;
            }
        }
        return result;
    }

    /**
     * Delete a character at a given position from a String
     * @param input the String under consideration
     * @param position the position to be deleted
     * @return the resulting String
     */
    protected static String deleteCharAt(String input, int position) {
        return input.substring(0, position) + input.substring(position + 1, input.length());
    }

    /**
     * checks whether a char is numeric 
     * @param x the char to test
     * @return (x >= '0' && x <= '9')
     */
    static boolean isNumeric(char x) {
        return (x >= '0' && x <= '9');
    }

    /**
     * returns the index of the first non-numeric character in a String
     * @param input the String being examined
     * @return the position of the first non-numeric char, or -1 if no non-numeric char is found.
     */
    static int indexOfFirstNonnumeric(String input) {
        for (int i = 0; i < input.length(); i++) {
            if (!isNumeric(input.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    public boolean isClientVersLowerThanIntro(String introVersion,
    		String clientSchemaVersion) throws BksDataException {
    	// Assuming <number>.<number>.<number> format.
    	if (introVersion==null||introVersion.trim().length()==0 ||clientSchemaVersion==null||clientSchemaVersion.trim().length()==0)
    		return false;
    	String [] introList=introVersion.split("\\.");
    	String [] clientList=clientSchemaVersion.split("\\.");
    	int introLen  = introList.length;
    	int clientLen  = clientList.length;
    	try {
    		for (int i = 0; i < ((introLen > clientLen) ? introLen : clientLen); i++) {
    			if (Integer.parseInt(introList[i]) > Integer.parseInt(clientList[i]))
    				return true;
    			else if (Integer.parseInt(introList[i]) < Integer.parseInt(clientList[i]))
    				return false;
    		}
    	} catch (NumberFormatException e) {
    		throw new BksDataException("BKSXXXX","SBUS_SOMRelease "+ clientSchemaVersion +"has wrong format");
    	} catch (Exception e) {
    		return (clientLen < introLen);
    	}
    	return false;
    }
    
    public String ccbDateStringToSomDateString(String ccbDateString) {          

    	String somDateString="";
    	if(ccbDateString == null || ccbDateString.equals(""))
    		return somDateString;
    	String [] tmp = ccbDateString.split("\\.");
    	if(tmp.length == 3){
    		if(tmp[0].length()==1) tmp[0]="0"+tmp[0];
    		if(tmp[1].length()==1) tmp[1]="0"+tmp[1];
    		somDateString = tmp[2]+"-"+tmp[1]+"-"+tmp[0];
    	}

    	return somDateString;
    }    
 
	private Connection createBatConnection() throws Exception
	{   	  
		Connection connection = null;
		String driver = null;
		String connectString = null;
		String user = null;
		String password = null;
		int ConnectionTimeOut=0;
		try {
			driver = DatabaseClientConfig.getSetting("databaseclient.Driver");
			connectString = DatabaseClientConfig.getSetting("databaseclient.bat.ConnectString");
			user = DatabaseClientConfig.getSetting("databaseclient.bat.User");
			password = DatabaseClientConfig.getSetting("databaseclient.bat.Password");
			String encryptionKey = DatabaseClientConfig.getSetting("databaseclient.EncryptionKey");
			password = Cryptography.ccbDecrypt(encryptionKey, password,true);
			
		} catch (Exception e1) {
			logger.error(e1);
			throw new BksDataException("BKS0008","Fetching BAT properties value failed :"+e1.getMessage());
		}
		try{
   	    	ConnectionTimeOut = Integer.parseInt(DatabaseClientConfig.getSetting("databaseclient.ConnectionTimeOut"));
   	    }catch(Exception e){
   	    	
   	    }
    	
    	
		OracleConnectionPoolDataSource ocpds = new OracleConnectionPoolDataSource();
    	try{
    		Class.forName(driver).newInstance();           
    	    ocpds.setURL(connectString);
    	    ocpds.setUser(user);
    	    ocpds.setPassword(password);
    	    ocpds.setLoginTimeout(ConnectionTimeOut);
    	}	
        catch (Exception e)
        {
        	
        	
        }   
         
    	
    	try
    	{    
    		PooledConnection pc = ocpds.getPooledConnection();
    	    connection = pc.getConnection();
    	}
        catch (SQLException e)
        {
        	logger.error("Cannot connect to BAT database.");
        	throw e;
        }
        return connection;
   	}
	
	public Float getAccountBalance(String accountNumber) throws Exception{
		Connection connection = createBatConnection();
		Float accountBalance=null;
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("select BALANCE_AMOUNT_EUR from ACCOUNT_BALANCE where ACCOUNT_NUMBER = ? ");
			preparedStatement.setString(1, accountNumber);
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()){
				accountBalance = resultSet.getFloat("BALANCE_AMOUNT_EUR");	
			}
		}finally {
			connection.close();
		}

		return accountBalance;
	}
}
