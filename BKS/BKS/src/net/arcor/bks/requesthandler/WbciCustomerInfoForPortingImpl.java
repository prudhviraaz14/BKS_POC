/*
    $Header:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/WbciCustomerInfoForPortingImpl.java-arc   1.45   Sep 17 2018 14:26:32   makuier  $

    $Log:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/WbciCustomerInfoForPortingImpl.java-arc  $
 * 
 *    Rev 1.45   Sep 17 2018 14:26:32   makuier
 * Support for Fibre services added
 * 
 *    Rev 1.44   Jun 20 2018 14:17:08   vikas.s
 * Supported for Business VOIP
 * 
 *    Rev 1.43   Apr 20 2018 09:20:20   makuier
 * Search by access_number insted of start_range_string.
 * 
 *    Rev 1.42   Jan 12 2018 14:14:28   makuier
 * Adapted to signature changes in Cramer/Adia DAO. ITK 33956
 * 
 *    Rev 1.41   May 12 2017 07:24:46   makuier
 * Support NGN ADSL-AnnexJ
 * 
 *    Rev 1.40   Nov 16 2016 08:21:36   makuier
 * Made comparisons case insensitive
 * 
 *    Rev 1.39   Jul 01 2016 14:41:20   makuier
 * Raise BKS0066 if the access number is CCB but not in ZAR.
 * 
 *    Rev 1.38   Mar 23 2016 09:57:20   wlazlow
 * SPM-BKS-000131801, Cramer mock for negative testing
 * 
 *    Rev 1.37   Jan 18 2016 13:52:22   makuier
 * Raise an error if no dtag number found and the technology requires it.
 * 
 *    Rev 1.36   Jan 08 2016 14:57:54   gaurav.verma
 * 
 *  RMS147902_PPM100281 VDSL 2.0 - Phase 4 - 
 *   NGABLineID has to be added to this service response
 * 
 *    
 *    Rev 1.34   Jun 18 2015 14:37:22   makuier
 * OnenetBusiness added.
 * 
 *    Rev 1.33   Feb 26 2015 15:53:00   makuier
 * Validate blocking external orders
 * 
 *    Rev 1.32   Feb 13 2015 08:50:22   makuier
 * Filter the phone numbers when ZAR does not return any PKI. 
 * 
 *    Rev 1.31   Jan 29 2015 10:35:08   makuier
 * Do not match simple input with access number range.
 * 
 *    Rev 1.30   Jan 28 2015 09:09:52   makuier
 * Handle individual<->company in input
 * 
 *    Rev 1.29   Jan 12 2015 14:21:40   makuier
 * raise BKS0067 when access number search fails.
 * 
 *    Rev 1.28   Jan 07 2015 13:42:08   makuier
 * Corrected the company name comparison.
 * 
 *    Rev 1.27   Jan 06 2015 08:03:04   makuier
 * Consider additional owner in name comparison.
 * 
 *    Rev 1.26   Dec 05 2014 14:26:28   makuier
 * New Algorithm for name comparison.
 * 
 *    Rev 1.25   Nov 25 2014 13:44:14   makuier
 * raise error for non supported products
 * 
 *    Rev 1.24   Nov 04 2014 09:51:50   makuier
 * - Disregard from the value of  service characteristic V0016. Copy the abfragestelle from input.
 * 
 *    Rev 1.23   Oct 07 2014 09:36:22   makuier
 * Make a pre-phonetic for name comparison.
 * 
 *    Rev 1.22   Aug 01 2014 14:04:18   makuier
 * PPM_64612_142884
 * 
 *    Rev 1.21   Jul 29 2014 08:46:10   makuier
 * Handle the CCB deact number according to new instructions.
 * 
 *    Rev 1.20   Jul 15 2014 09:39:58   makuier
 * - Populate address on outout when doing name search.
 * - Abfragestelle from input if corresp. characteristic is not set.
 * 
 *    Rev 1.19   Jun 12 2014 10:46:24   makuier
 * get the barcode when the termaination date is populated on contract.
 * 
 *    Rev 1.18   May 27 2014 17:18:10   makuier
 * Company name for deact numbers
 * 
 *    Rev 1.17   May 21 2014 15:50:34   makuier
 * Technical exception for connection failure to external databases.
 * 
 *    Rev 1.16   May 16 2014 14:38:46   makuier
 * Set the success indicator correctly.
 * 
 *    Rev 1.15   Apr 29 2014 15:30:34   makuier
 * Set abfragestelle
 * 
 *    Rev 1.14   Apr 07 2014 11:34:48   makuier
 * Cover extended ranges.
 * 
 *    Rev 1.13   Apr 02 2014 09:24:26   makuier
 * Added SIP trunk and IP Centrex to list of valid voice functions.
 * 
 *    Rev 1.12   Mar 28 2014 12:37:08   makuier
 *  - Get the tasi information from bundled business DSL.
 * 
 *    Rev 1.11   Mar 06 2014 12:17:38   makuier
 * getPortIdentifier added.
 * 
 *    Rev 1.10   Feb 19 2014 15:00:20   makuier
 * Implementation of WBCI phase 2.
 * 
 *    Rev 1.9   Feb 12 2014 15:00:10   makuier
 * populate expiration date if no termaination date is populated.
 * 
 *    Rev 1.8   Jan 31 2014 12:44:20   makuier
 * Corrected the error code.
 * 
 *    Rev 1.7   Jan 27 2014 11:41:50   makuier
 * Use concatinated nase for companies in case of name search
 * 
 *    Rev 1.6   Jan 16 2014 13:57:32   makuier
 * Default the port type
 * 
 *    Rev 1.5   Jan 08 2014 10:41:16   makuier
 * Populate customer information in case of deact access number.
 * 
 *    Rev 1.4   Jan 06 2014 14:12:06   makuier
 * Improve company name verification.
 * 
 *    Rev 1.3   Dec 20 2013 12:27:54   makuier
 * Populate CCB data even in case of error.
 * 
 *    Rev 1.2   Nov 26 2013 09:13:32   makuier
 * Make phonetic conversion in the query instead.
 * 
 *    Rev 1.1   Nov 22 2013 13:21:24   makuier
 * - Validate address only when it's populated in the input.
  * 
  *    Rev 1.0   Nov 07 2013 13:14:38   makuier
  * Initial revision.
 */
package net.arcor.bks.requesthandler;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import net.arcor.bks.client.DatabaseClient;
import net.arcor.bks.client.DatabaseClientConfig;
import net.arcor.bks.common.BksDataException;
import net.arcor.bks.common.BksHelper;
import net.arcor.bks.common.BksRefDataCacheHandler;
import net.arcor.bks.db.CramerDAO;
import net.arcor.bks.db.WbciCustomerInfoForPortingDAO;
import net.arcor.bks.db.ZarDAO;
import net.arcor.bks.db.ibatis.CramerDAOImpl;
import net.arcor.bks.db.ibatis.ZarDAOImpl;
import net.arcor.bks.util.FudickarPhoneticConverter;
import net.arcor.mcf2.model.ServiceObjectEndpoint;
import net.arcor.mcf2.sender.MessageSender;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.domainlanguage.time.CalendarDate;
import com.vodafone.mcf2.ws.exception.FunctionalException;
import com.vodafone.mcf2.ws.exception.FunctionalRuntimeException;
import com.vodafone.mcf2.ws.exception.TechnicalException;
import com.vodafone.mcf2.ws.model.impl.ServiceResponseSoap;
import com.vodafone.mcf2.ws.service.SoapEndpoint;
import com.vodafone.mcf2.ws.service.SoapOperation;

import de.vodafone.esb.service.sbus.bks.bks_wbci_vf_002.WbciCustomerInfoForPortingRequest;
import de.vodafone.esb.service.sbus.bks.bks_wbci_vf_002.WbciCustomerInfoForPortingRequest.WeitereAnschlussinhaber;
import de.vodafone.esb.service.sbus.bks.bks_wbci_vf_002.WbciCustomerInfoForPortingResponse;
import de.vodafone.esb.service.sbus.bks.bks_wbci_vf_002.WbciCustomerInfoForPortingRequest.Standort;
import de.vodafone.esb.service.sbus.bks.bks_wbci_vf_002.WbciCustomerInfoForPortingRequest.Endkunde.Firma;
import de.vodafone.esb.service.sbus.bks.bks_wbci_vf_002.WbciCustomerInfoForPortingRequest.Endkunde.Person;
import de.vodafone.esb.service.sbus.bks.bks_wbci_vf_002.WbciCustomerInfoForPortingRequest.Rufnummernportierung.Anlagenanschluss;
import de.vodafone.esb.service.sbus.bks.bks_wbci_vf_002.WbciCustomerInfoForPortingRequest.Rufnummernportierung.Anlagenanschluss.ZuPortierenderRufnummernblock;
import de.vodafone.esb.service.sbus.bks.bks_wbci_vf_002.WbciCustomerInfoForPortingRequest.Rufnummernportierung.Einzelanschluss.Rufnummernliste.ZuPortierendeOnkzRnr;
import de.vodafone.esb.service.sbus.bks.bks_wbci_vf_002.WbciCustomerInfoForPortingRequest.Standort.Strasse;
import de.vodafone.esb.service.sbus.bks.bks_wbci_vf_002.WbciCustomerInfoForPortingResponse.Adressvergleich;
import de.vodafone.esb.service.sbus.bks.bks_wbci_vf_002.WbciCustomerInfoForPortingResponse.Kundendaten;
import de.vodafone.esb.service.sbus.bks.bks_wbci_vf_002.WbciCustomerInfoForPortingResponse.NamensVergleich;
import de.vodafone.esb.service.sbus.bks.bks_wbci_vf_002.WbciCustomerInfoForPortingResponse.Rufnummernvergleich;
import de.vodafone.esb.service.sbus.bks.bks_wbci_vf_002.WbciCustomerInfoForPortingResponse.Adressvergleich.AdresseAbweichend;
import de.vodafone.esb.service.sbus.bks.bks_wbci_vf_002.WbciCustomerInfoForPortingResponse.Kundendaten.Anschluesse;
import de.vodafone.esb.service.sbus.bks.bks_wbci_vf_002.WbciCustomerInfoForPortingResponse.Kundendaten.ExternerAuftrag;
import de.vodafone.esb.service.sbus.bks.bks_wbci_vf_002.WbciCustomerInfoForPortingResponse.Rufnummernvergleich.Einzelrufnummern;
import de.vodafone.esb.service.sbus.bks.bks_wbci_vf_002.WbciCustomerInfoForPortingResponse.Rufnummernvergleich.Rufnummernblock;
import de.vodafone.esb.service.sbus.bks.bks_wbci_vf_002.WbciCustomerInfoForPortingResponse.Rufnummernvergleich.Einzelrufnummern.Rufnummern;
import de.vodafone.esb.service.sbus.bks.bks_wbci_vf_002.WbciCustomerInfoForPortingResponse.Rufnummernvergleich.Rufnummernblock.OnkzDurchwahlAbfragestelle;
import de.vodafone.esb.service.sbus.bks.bks_wbci_vf_002.WbciCustomerInfoForPortingResponse.Rufnummernvergleich.Rufnummernblock.RufnummernBloecke;
import de.vodafone.esb.service.sbus.bks.bks_wbci_vf_simpletypes_002.KundentypType;
import edu.emory.mathcs.backport.java.util.Arrays;

@SoapEndpoint(soapAction = "/BKS-002/WbciCustomerInfoForPorting", context = "de.vodafone.esb.service.sbus.bks.bks_wbci_vf_002", schema = "classpath:schema/BKS-WBCI-VF-002.xsd")
public class WbciCustomerInfoForPortingImpl implements SoapOperation<WbciCustomerInfoForPortingRequest, WbciCustomerInfoForPortingResponse> {

    /** The logger. */
    private final static Log log = LogFactory.getLog(WbciCustomerInfoForPortingImpl.class);
    final static String[] vodafoneProvider = {"ARCO","VODA","VFAI"};
    final static String[] dtagTechnologies = {"001 TAL ISDN","002 TAL DSL","003 TAL VDSL"};
    final static HashMap<String , String> charNameMap = new HashMap<String , String>() {
    	private static final long serialVersionUID = 1L;
    	{ 
    		put("V0127","Owner1 name");
    		put("V0128","Owner1 forename");
    		put("V0129","Owner2 name");
    		put("V0130","Owner2 forename");
    		put("I1325","DTAGContractNumber");
    		put("V0152","Tasi");
    		put("V009C","AccessTechnology");
    		put("V0105","SalesSegment");
    		put("I1020","NgabLineID");
    	}
    }; 
    final static HashMap<String , String> zarErrorMap = new HashMap<String , String>() {
    	private static final long serialVersionUID = 1L;
    	{ 
     		put("30014","BKS0064");
     		put("30025","BKS0064");
     		put("30023","BKS0064");
     		put("30027","BKS0064");
     		put("1537","BKS0065");
     		put("1511","BKS0065");
     		put("1513","BKS0065");
     		put("1514","BKS0065");
     		put("1531","BKS0066");
     		put("1542","BKS0065");
     		put("1500","BKS0042");
    	}
    }; 
    final static HashMap<String , String> errorMap = new HashMap<String , String>() {
    	private static final long serialVersionUID = 1L;
    	{ 
    		put("BKS0032","Not possible to identify customer.");
    		put("BKS0033","Customer has blocking open external orders");
    		put("BKS0034","Product not supported yet.");
    		put("BKS0035","Wrong serviceProvider (not Vodafone).");
    		put("BKS0036","Access numbers already deactivated.");
    		put("BKS0037","AccessnumberRanges not supported yet.");
    		put("BKS0038","Provided numbers belongs to Voice and VoIP 2nd line.");
    		put("BKS0039","Access numbers belong to different access/technology.");
    		put("BKS0040","More than one customer found.");
    		put("BKS0041","Cannot identify a unique bundle for customer.");
    		put("BKS0052","Access number already deactivated.");
    		put("BKS0056","Found more than one customer for the seach criteria.");
    		put("BKS0061","Wrong format of accessnumberranges");
    		put("BKS0064","Technical errors during ZAR or Cramer call.");
    		put("BKS0065","Wrong access number format according to ZAR.");
    		put("BKS0066","Access number not listed in ZAR.");
    		put("BKS0067","WBCI error RNG, no matching access number found");
    	}
    }; 
    final static HashMap<String , String> zarStatusMap = new HashMap<String , String>() {
    	private static final long serialVersionUID = 1L;
    	{ 
    		put("P02,L12","QUARANTAENE");
    		put("P02,L20","QUARANTAENE");
    		put("P02,L27","QUARANTAENE");
    		put("P07,L27","RUECKFALL");
    		put("P08,L27","RUECKFALL");
    	}
    }; 
	final static String[] voiceServices = {"V0010","V0003","V0004","V0002","V0001","V0011","V0012","VI002","VI003","VI006","VI009","VI018","VI019","VI010","VI020","VI021","VI201"};
	final static String[] cramerServices = {"V0003","V0010","V0011","I1210","I121z","I1215","I1216","VI201"};
    @Autowired
	private MessageSender messageSender;

	/**
	 * The operation "WbciCustomerInfoForPorting" of the service "WbciCustomerInfoForPorting".
	 */
	public ServiceResponseSoap<WbciCustomerInfoForPortingResponse> invoke(ServiceObjectEndpoint<WbciCustomerInfoForPortingRequest> serviceObject) 
			throws TechnicalException,FunctionalException,FunctionalRuntimeException {
		int tries =0;
		int maxTries = 10;
		BksHelper theHelper = new BksHelper();
		try {
			maxTries = Integer.parseInt(DatabaseClientConfig.getSetting("databaseclient.MaxRetries"));
		} catch (Exception e1) {
			// Ignore (Use Default)
		}
        while (!BksRefDataCacheHandler.isRefdataInitialized() && tries < maxTries){
        	tries++;
        	try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
        }
        if (!BksRefDataCacheHandler.isRefdataInitialized()){
        	log.error("The application is not initialized after max. number of tries ( "+ tries +").");
        	log.error("Please increase the value of databaseclient.MaxRetries and restart the application.");
        	System.exit(0);
        }
		WbciCustomerInfoForPortingDAO dao = (WbciCustomerInfoForPortingDAO) DatabaseClient.getBksDataAccessObject(null, "WbciCustomerInfoForPortingDAO");
		WbciCustomerInfoForPortingRequest request = serviceObject.getPayload();
		WbciCustomerInfoForPortingResponse output = new WbciCustomerInfoForPortingResponse();
        long startTime = System.currentTimeMillis();
		try {
			output = getWbciCustomerData(dao,request,theHelper);
	        long endTime = System.currentTimeMillis();
	        log.info("Duration: "+(endTime-startTime));
		} catch (BksDataException e) {
			throw new TechnicalException(e.getCode(),e.getText(),e);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new TechnicalException("BKS0042","Technical error",e);
		} finally {
			try {
				dao.closeConnection();
			} catch (Exception e) {
			}
		}

		return new ServiceResponseSoap<WbciCustomerInfoForPortingResponse>(output);
	}
	private WbciCustomerInfoForPortingResponse getWbciCustomerData(
			WbciCustomerInfoForPortingDAO dao,
			WbciCustomerInfoForPortingRequest request, BksHelper theHelper)throws Exception{
		WbciCustomerInfoForPortingResponse output = new WbciCustomerInfoForPortingResponse();
		HashMap<String,Object> accNumResult = new HashMap<String, Object>();
		Rufnummernvergleich numberComp=new Rufnummernvergleich();
		String errorCode = null;
		boolean isActive=true;
		String abfrageStelle=null;
		if (request.getRufnummernportierung()!=null) {
			ArrayList<String> numberList=new ArrayList<String>();
			if (request.getRufnummernportierung().getEinzelanschluss()!=null){
				List<ZuPortierendeOnkzRnr> accNumList = 
					request.getRufnummernportierung().getEinzelanschluss().getRufnummernliste().getZuPortierendeOnkzRnr();
				for (int i = 0; i < accNumList.size(); i++) {
					ZuPortierendeOnkzRnr accNum = accNumList.get(i);
					numberList.add("49;"+accNum.getONKZ()+";"+accNum.getRufnummer());
				}
			}else if (request.getRufnummernportierung().getAnlagenanschluss()!=null){
				Anlagenanschluss range = request.getRufnummernportierung().getAnlagenanschluss();
				abfrageStelle=range.getOnkzDurchwahlAbfragestelle().getAbfragestelle();
				errorCode = validateAccNumRange(range);
				String baseNumber="49;"+range.getOnkzDurchwahlAbfragestelle().getONKZ()+
				";"+range.getOnkzDurchwahlAbfragestelle().getDurchwahlnummer();
				List<ZuPortierenderRufnummernblock> rangeList = range.getZuPortierenderRufnummernblock();
				for (int i = 0; i < rangeList.size(); i++) {
					ZuPortierenderRufnummernblock block = rangeList.get(i);
					numberList.add(baseNumber+";"+block.getRnrBlockVon()+";"+block.getRnrBlockBis());
				}
			}
			if (errorCode == null )
				errorCode = getProductByAccNum(dao,numberList,accNumResult, theHelper);
			String serSuId = (String) accNumResult.get("VoiceServSubsId");
			if (serSuId!=null)
				getContractInfo(dao, serSuId, accNumResult,theHelper);
			if (errorCode==null) {
				if (accNumResult.isEmpty()) {
					numberComp.setSuccess(false);
				} else {
					numberComp.setSuccess(true);
					if (accNumResult.get("LineStatus")!=null &&  accNumResult.get("LineStatus").equals("DEACT"))
						isActive=false;
					getConfValueForServSubs(dao, serSuId, accNumResult);
					errorCode = getAccessNumbersForServSubs(dao, serSuId, numberComp,theHelper,isActive,abfrageStelle);
					getAddressForServSubs(dao, serSuId, accNumResult);
					populateTerminationState(dao, serSuId, accNumResult,theHelper);
				}
			}
		}
		NamensVergleich namecomp = new NamensVergleich();
		Adressvergleich addrComp = null;
		if (errorCode==null) {
			if (request.getRufnummernportierung() == null|| accNumResult.isEmpty()) {
				boolean isIndividual = (request.getEndkunde().getPerson() != null);
				boolean isAccNumSearch=(request.getRufnummernportierung() != null);
				Standort address = request.getStandort();
				if (isIndividual)
					errorCode = getIndividualData(dao, request.getEndkunde().getPerson(),address, accNumResult, theHelper,isAccNumSearch);
				else
					errorCode = getCompanyData(dao, request.getEndkunde().getFirma(),address, accNumResult, theHelper,isAccNumSearch);
				
				String serSuId = (String) accNumResult.get("VoiceServSubsId");
				if (serSuId != null  && errorCode == null) {
					getContractInfo(dao, serSuId, accNumResult,theHelper);
					if (request.getRufnummernportierung() == null)
						numberComp.setSuccess(true);
					getConfValueForServSubs(dao, serSuId, accNumResult);
					errorCode = getAccessNumbersForServSubs(dao, serSuId, numberComp,theHelper, true,abfrageStelle);
					getAddressForServSubs(dao, serSuId, accNumResult);
					populateTerminationState(dao, serSuId, accNumResult,theHelper);
				}
			} 
			if (errorCode==null){
				verifyNames(accNumResult, request, namecomp, theHelper);
				if (request.getStandort()!=null && isActive){
					addrComp=new Adressvergleich();
					verifyAddress(accNumResult, request, addrComp);
				}
				String serSuId = (String) accNumResult.get("VoiceServSubsId");
				errorCode=checkForBlockingExternalOrders(dao, serSuId,theHelper);
			}
		}
		
		if (errorCode!=null) {
			output.setSucessIndicator(false);
			output.setErrorCode(errorCode);
			output.setErrorText(errorMap.get(errorCode));
			populateOutput(accNumResult,output, theHelper, null, null, null);
		} else {
			String tasi = (String) accNumResult.get("Tasi");
			String serCode = (String) accNumResult.get("AccessServiceCode");
			Rufnummern accNum = null;
			Rufnummernblock accNumRange=null;
			if (numberComp.getEinzelrufnummern()!=null&&
				numberComp.getEinzelrufnummern().getRufnummern().size()>0)
				accNum= numberComp.getEinzelrufnummern().getRufnummern().get(0);
			if (numberComp.getRufnummernblock()!=null&&
				numberComp.getRufnummernblock().getRufnummernBloecke().size()>0)
				accNumRange= numberComp.getRufnummernblock();
			if (Arrays.asList(cramerServices).contains(serCode)) {
				ArrayList<String[]> internalErrorList = new ArrayList<String[]>();
				ArrayList<HashMap<String, Object>> cramerData = new ArrayList<HashMap<String, Object>>();
				loadCramerData(accNum,accNumRange, tasi, cramerData, internalErrorList,theHelper,accNumResult);
				if (cramerData.size() > 0 && cramerData.get(0).get("DtagContractNumber") != null)
					accNumResult.put("DTAGContractNumber",(ArrayList<String>)cramerData.get(0).get("DtagContractNumber"));
			}
			output.setSucessIndicator(true);
			populateOutput(accNumResult,output, theHelper, numberComp, namecomp, addrComp);
		}
		return output;
	}
	private String checkForBlockingExternalOrders(
			WbciCustomerInfoForPortingDAO dao, String serSuId,
			BksHelper theHelper) throws Exception {
		ArrayList<HashMap<String, Object>> extOrd = dao.getExternalOrder(serSuId,null);
		ArrayList<String> blockingInten = theHelper.getGeneralRefData("WBCI_BLOCK");
		for (int i =0;extOrd!=null&&extOrd.size()>i;i++){
			String intention = (String)extOrd.get(0).get("CUSTOMER_INTENTION");
			if (blockingInten!=null && blockingInten.contains(intention))
				return "BKS0033";
		}
		return null;
	}
	private String validateAccNumRange(Anlagenanschluss range) {
		String errorCode = null;
		String localArea = range.getOnkzDurchwahlAbfragestelle().getONKZ();
		String pilotNum = range.getOnkzDurchwahlAbfragestelle().getDurchwahlnummer();
		if (localArea.startsWith("0") || pilotNum.length() > 8)
			errorCode = "BKS0061";
		List<ZuPortierenderRufnummernblock> rangeList = range.getZuPortierenderRufnummernblock();
		for (int i = 0; i < rangeList.size(); i++) {
			ZuPortierenderRufnummernblock block = rangeList.get(i);
			String start = block.getRnrBlockVon();
			String end = block.getRnrBlockBis();
			if (start.length()!=end.length() || 
				!start.matches("[0-9]{1}[0]*") ||
					!end.matches("[1-9]{1}[9]*"))
				errorCode = "BKS0061";
		}		
		return errorCode;
	}
	CalendarDate getExpirationDate(ArrayList<HashMap<String, Object>> contract, BksHelper theHelper){
		String specTermRights = (String)contract.get(0).get("SPECIAL_TERMINATION_RIGHT_RD");
		if (specTermRights!=null&&!specTermRights.equals("NONE")){
			Date inAWeek = new Date();
			inAWeek.setTime(inAWeek.getTime() + (long)7L*24L*60L*60L*1000L);
			return theHelper.convertSqlDateToCalendar(inAWeek);
		}
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

		String noticeUnit = (String)contract.get(0).get("NOTICE_PERIOD_DUR_UNIT_RD");
		Integer noticeValue = null;
		if (contract.get(0).get("NOTICE_PERIOD_DUR_VALUE") != null)
			noticeValue = ((BigDecimal)contract.get(0).get("NOTICE_PERIOD_DUR_VALUE")).intValue();
		CalendarDate exDate = null;
		exDate = theHelper.getContractEndDateWithNoticePeriod(startDate,minUnit,minValue,
				   durationUnit,extValue,noticeUnit,noticeValue, bAutoExtend);

		return exDate;
	}
	private void getContractInfo(WbciCustomerInfoForPortingDAO dao,
			String serSuId, HashMap<String, Object> accNumResult, BksHelper theHelper) throws Exception {
		ArrayList<HashMap<String, Object>> contracts = dao.getOrderFormData(serSuId);
		if (contracts==null || contracts.size()==0)
			contracts = dao.getServDeliveContData(serSuId);
		if (contracts!=null && contracts.size()>0){
			accNumResult.put("ContractNumber",(String) contracts.get(0).get("CONTRACT_NUMBER"));
			accNumResult.put("ExpireDate",getExpirationDate(contracts,theHelper));
			accNumResult.put("ProductCode", (String) contracts.get(0).get("PRODUCT_CODE"));
		}
	}
	@SuppressWarnings("unchecked")
	private void populateOutput(HashMap<String, Object> accNumResult,
			WbciCustomerInfoForPortingResponse output, BksHelper theHelper, 
			Rufnummernvergleich numberComp, NamensVergleich namecomp, Adressvergleich addrComp) {
		String customerNumber = (String) accNumResult.get("CustomerNumber");
		String provider = (String) accNumResult.get("Provider");
		String contractNumber = (String) accNumResult.get("ContractNumber");
		String servSubsId = (String) accNumResult.get("VoiceServSubsId");
		String productType = (String) accNumResult.get("ProductType");
		String productCode = (String) accNumResult.get("ProductCode");
		if (customerNumber == null)
			return;
		output.setKundendaten(new Kundendaten());
		output.getKundendaten().setKundennummer(customerNumber);
		output.getKundendaten().setProviderCode(provider);
		output.getKundendaten().setProductCode(productCode);
		String tasi = (String) accNumResult.get("Tasi");
		String ngabLineId = (String) accNumResult.get("NgabLineID");
		output.getKundendaten().setTASI(tasi);
		output.getKundendaten().setLineID(ngabLineId);
		String salesSegment = (String) accNumResult.get("SalesSegment");
		if (salesSegment!=null && (salesSegment.equals("PK")||salesSegment.equals("GK")))
			output.getKundendaten().setKundentyp(Enum.valueOf(KundentypType.class, salesSegment));
		output.getKundendaten().setServiceSubscriptionId(servSubsId);
		String tech = theHelper.getWbciTechnology(productType);
		output.getKundendaten().setTechnologie(tech);
		if (accNumResult.get("DTAGContractNumber") == null) {
			if (Arrays.asList(dtagTechnologies).contains(tech)){
				output.setSucessIndicator(false);
				output.setErrorCode("BKS0064");
				output.setErrorText(errorMap.get("BKS0064"));
			}
		} else if (accNumResult.get("DTAGContractNumber") instanceof ArrayList<?>) {
			ArrayList<String> dtagNums = (ArrayList<String>) accNumResult.get("DTAGContractNumber");
			for (int i = 0; dtagNums != null && i < dtagNums.size(); i++) {
				Anschluesse con = new Anschluesse();
				con.setDtagVertragsnummer(dtagNums.get(i));
				output.getKundendaten().getAnschluesse().add(con);
			}
		} else if (accNumResult.get("DTAGContractNumber") instanceof String){
			Anschluesse con = new Anschluesse();
			con.setDtagVertragsnummer((String)accNumResult.get("DTAGContractNumber"));
			output.getKundendaten().getAnschluesse().add(con);
		}
		output.getKundendaten().setCCMContractNumber(contractNumber);
		if (numberComp!=null&&(numberComp.getEinzelrufnummern()!=null || numberComp.getRufnummernblock()!=null))
			output.setRufnummernvergleich(numberComp);
		output.setNamensVergleich(namecomp);
		output.setAdressvergleich(addrComp);
		Date termDate = (Date) accNumResult.get("TerminationDate");
		if (termDate!=null&&termDate.after(new Date(0))){
			output.getKundendaten().setEigenkuendigung(true);
			output.getKundendaten().setKuendigungsdatum(theHelper.convertSqlDateToCalendar(termDate));
			if (accNumResult.get("BARCODE")!=null&&accNumResult.get("CUSTOMER_INTENTION")!=null){
				ExternerAuftrag extCont =new ExternerAuftrag();
				extCont.setBarcode((String)accNumResult.get("BARCODE"));
				extCont.setCustomerIntention((String)accNumResult.get("CUSTOMER_INTENTION"));
				output.getKundendaten().setExternerAuftrag(extCont );
			}
		} else {
			output.getKundendaten().setKuendigungsdatum((CalendarDate) accNumResult.get("ExpireDate"));
		}

	}
	private void populateTerminationState(WbciCustomerInfoForPortingDAO dao,
			String serSuId, HashMap<String, Object> accNumResult, BksHelper theHelper) throws Exception {
		ArrayList<HashMap<String, Object>> extOrd = dao.getExternalOrder(serSuId,"Termination");
		if (extOrd!=null&&extOrd.size()>0){
			Date dt = (Date)extOrd.get(0).get("DESIRED_DATE");
			accNumResult.put("TerminationDate",dt);
			accNumResult.put("BARCODE",(String)extOrd.get(0).get("BARCODE"));
			accNumResult.put("CUSTOMER_INTENTION",(String)extOrd.get(0).get("CUSTOMER_INTENTION"));
		} else {
			ArrayList<HashMap<String, Object>> intOrd = dao.getInternalOrder(serSuId,"4");
			if (intOrd!=null&&intOrd.size()>0){
				Date dt = (Date)intOrd.get(0).get("DESIRED_DATE");
				accNumResult.put("TerminationDate",dt);
				accNumResult.put("BARCODE",(String)intOrd.get(0).get("BARCODE"));
				accNumResult.put("CUSTOMER_INTENTION","Termination");
			}
		}
		
	}
	private void getConfValueForServSubs(WbciCustomerInfoForPortingDAO dao,
			String serSuId, HashMap<String, Object> accNumResult) throws Exception {
		ArrayList<HashMap<String, Object>> accInfo = dao.getConfigValueForSerSu(serSuId);
		for (int i = 0; i < accInfo.size(); i++) {
			String charCode = (String) accInfo.get(i).get("SERVICE_CHARACTERISTIC_CODE");
			String name = charNameMap.get(charCode);
			String value = (String) accInfo.get(i).get("CONFIGURED_VALUE_STRING");
			if (name!=null)
				accNumResult.put(name,value);
		}
	}
	private void getAddressForServSubs(WbciCustomerInfoForPortingDAO dao,
			String serSuId, HashMap<String, Object> accNumResult) throws Exception {
		ArrayList<HashMap<String, Object>> accInfo = dao.getAddressForSerSu(serSuId);
		if (accInfo!=null && accInfo.size()>0) {
			Standort address = new Standort();
			address.setOrt((String) accInfo.get(0).get("CITY_NAME"));
			address.setPostleitzahl((String) accInfo.get(0).get("POSTAL_CODE"));
			Strasse str = new Strasse();
			str.setStrassenname((String) accInfo.get(0).get("STREET_NAME"));
			str.setHausnummer((String) accInfo.get(0).get("STREET_NUMBER"));
			address.setStrasse(str);
			accNumResult.put("locationAddress",address);
		}
	}
	private void verifyAddress(HashMap<String, Object> accNumResult,
			WbciCustomerInfoForPortingRequest request, Adressvergleich addrComp) {
		FudickarPhoneticConverter phonPack = new FudickarPhoneticConverter();
		addrComp.setSuccess(false);
		addrComp.setOrtKorrekt(false);
		addrComp.setPlzKorrekt(false);
		addrComp.setStrassennameKorrekt(false);
		addrComp.setHausnummerKorrekt(false);
		Standort address = (Standort) accNumResult.get("locationAddress");
		if (address==null)
			return;
		if (address.getOrt().equalsIgnoreCase(request.getStandort().getOrt()))
			addrComp.setOrtKorrekt(true);
		if (address.getPostleitzahl().equals(request.getStandort().getPostleitzahl()))
			addrComp.setPlzKorrekt(true);
		String dbStrName = prePhonetic(address.getStrasse().getStrassenname());
		String reqStrName = prePhonetic(request.getStandort().getStrasse().getStrassenname());
		if (phonPack.convert(dbStrName).equalsIgnoreCase(phonPack.convert(reqStrName)))
			addrComp.setStrassennameKorrekt(true);
		if (areSteetNumbersEqual(address.getStrasse().getHausnummer(),
				(request.getStandort().getStrasse().getHausnummer())))
			addrComp.setHausnummerKorrekt(true);
		if (addrComp.isOrtKorrekt()&&addrComp.isPlzKorrekt()&&
				addrComp.isStrassennameKorrekt()&&addrComp.isHausnummerKorrekt())
			addrComp.setSuccess(true);
		if (!addrComp.isSuccess()){
			AdresseAbweichend deviatAddr=new AdresseAbweichend();
			deviatAddr.setOrt(address.getOrt());
			deviatAddr.setPostleitzahl(address.getPostleitzahl());
			deviatAddr.setStrassenname(address.getStrasse().getStrassenname());
			deviatAddr.setHausnummer(address.getStrasse().getHausnummer());
			addrComp.setAdresseAbweichend(deviatAddr);
		}
	}
	private boolean areSteetNumbersEqual(String ccbStrNum, String reqStrNum) {
		if (ccbStrNum.equals(reqStrNum))
			return true;
		String[] numRange = reqStrNum.split("-");
		if (numRange.length == 2){
			if (numRange[0].equals(ccbStrNum))
				return true;
			if (numRange[1].equals(ccbStrNum))
				return true;
		}
		return false;
	}
	private String prePhonetic(String name) {
		if (name==null)
			return null;
		String returnValue=new String(name);
		returnValue=returnValue.toUpperCase();
		returnValue=returnValue.replaceAll("Ü", "UE");
		returnValue=returnValue.replaceAll("Ö", "OE");
		returnValue=returnValue.replaceAll("Ä", "AE");
		if (returnValue.endsWith("STR.")||returnValue.endsWith("STR")) {
			returnValue = returnValue.replaceAll("STR", "STRASSE");
		}
		return returnValue;
	}
	private void verifyNames(HashMap<String, Object> accNumResult,
			WbciCustomerInfoForPortingRequest request, NamensVergleich namecomp, BksHelper theHelper) throws Exception {
		boolean isIndividual=(request.getEndkunde().getPerson()!=null);
		String name = null;
		String forename = null;
		namecomp.setSuccess(false);
		namecomp.setNameKorrekt(false);

		if (isIndividual){
			name = request.getEndkunde().getPerson().getNachname();
			forename = request.getEndkunde().getPerson().getVorname();
			String dbName = (String)accNumResult.get("CustomerName");
			String dbForename = (String)accNumResult.get("CustomerForename");
			namecomp.setNameKorrekt(compareNames(name,dbName, theHelper));
			if (forename!=null && dbForename!=null)
					namecomp.setVornameKorrekt(compareNames(forename,dbForename, theHelper));
			
			if (!namecomp.isNameKorrekt()&&namecomp.isVornameKorrekt()!=null && !namecomp.isVornameKorrekt()) {
				if (dbForename!=null)
					namecomp.setNameKorrekt(compareNames(name,dbForename, theHelper));
				if (forename!=null)
					namecomp.setVornameKorrekt(compareNames(forename,dbName, theHelper));
			}
			if (!namecomp.isNameKorrekt()|| namecomp.isVornameKorrekt()!=null && !namecomp.isVornameKorrekt()){
				List<WeitereAnschlussinhaber> additionalOwners = request.getWeitereAnschlussinhaber();
				if (additionalOwners!=null && additionalOwners.size() > 0 && additionalOwners.get(0).getPerson()!=null){
					String addName = additionalOwners.get(0).getPerson().getNachname();
					String addForename = additionalOwners.get(0).getPerson().getVorname();
					if (compareNames(addName,dbName, theHelper) && compareNames(addForename,dbForename, theHelper)){
						namecomp.setNameKorrekt(true);
						namecomp.setVornameKorrekt(true);
					}
				}
			}
		} else {
			name = request.getEndkunde().getFirma().getFirmenname();
			String secPart = request.getEndkunde().getFirma().getFirmennameZweiterTeil();
			String concatinatedFirmName=(String)accNumResult.get("ConcatinatedFirmName1");
			namecomp.setNameKorrekt(checkCompanyName(name,secPart,concatinatedFirmName, theHelper));
			if (!namecomp.isNameKorrekt()){
				concatinatedFirmName=(String)accNumResult.get("ConcatinatedFirmName2");
				namecomp.setNameKorrekt(checkCompanyName(name,secPart,concatinatedFirmName, theHelper));
			}
				
		}
		namecomp.setWAIKorrekt(true);
		if (namecomp.isNameKorrekt() && 
		(namecomp.isVornameKorrekt()==null || namecomp.isVornameKorrekt())&& 
		(namecomp.isWAIKorrekt()==null || namecomp.isWAIKorrekt()))
			namecomp.setSuccess(true);
	}
	private boolean checkCompanyName(String inputName, String secPart,String concatinatedFirmName, BksHelper theHelper) throws Exception {
		if (inputName!=null)
			inputName = inputName.replaceAll("[-|_|\\.|,|\\+|#|!|§’|´|`]", " ");
		if (secPart!=null)
			secPart = secPart.replaceAll("[-|_|\\.|,|\\+|#|!|§’|´|`]", " ");
		if (concatinatedFirmName!=null)
			concatinatedFirmName = concatinatedFirmName.replaceAll("[-|_|\\.|,|\\+|#|!|§’|´|`]", " ");
		
		ArrayList<String> removeList = theHelper.getGeneralRefData("STR_TO_REM");
		inputName= filterString(inputName,removeList);
		secPart= filterString(secPart,removeList);
		concatinatedFirmName= filterString(concatinatedFirmName,removeList);
		if (concatinatedFirmName.toLowerCase().startsWith(inputName.toLowerCase()) && 
			(secPart.length()==0 || concatinatedFirmName.toLowerCase().contains(secPart.toLowerCase())))
			return true;

		return false;
	}
	private String filterString(String inputName, ArrayList<String> removeList) {
		String filteredName="";
		if (inputName==null||inputName.trim().length()==0)
			return filteredName;
		String[] inputNameList=inputName.split("\\s");
		for (int i = 0; i < inputNameList.length; i++) {
			String tmpInputName = inputNameList[i];
			if (removeList!=null && removeList.contains(tmpInputName.toUpperCase()) || tmpInputName.length() == 1)
				continue;
			filteredName+=tmpInputName;
		}
		return filteredName;
	}
	private boolean compareNames(String inputName,String dbName, BksHelper theHelper) throws Exception{
		if (inputName == null || dbName == null)
			return false;
		inputName = inputName.replaceAll("[-|_|\\.|,|\\+|#|!|��|�|`]", " ");
		dbName = dbName.replaceAll("[-|_|\\.|,|\\+|#|!|��|�|`]", " ");
		
		ArrayList<String> removeList = theHelper.getGeneralRefData("STR_TO_REM");
		FudickarPhoneticConverter phonPack = new FudickarPhoneticConverter();
		String[] inputNameList=inputName.split("\\s");
		String[] dbNameList=dbName.split("\\s");
		for (int i = 0; i < inputNameList.length; i++) {
			String tmpInputName = inputNameList[i];
			if (removeList!=null && removeList.contains(tmpInputName.toUpperCase()) || tmpInputName.length() == 1)
				continue;
			String preInputName = prePhonetic(tmpInputName);
			for (int j = 0; j < dbNameList.length; j++) {
				String tmpDbName = dbNameList[j];
				if (removeList!=null && removeList.contains(tmpDbName.toUpperCase()) || tmpDbName.length() == 1)
					continue;
				String preDbName = prePhonetic(tmpDbName);
				if (phonPack.convert(tmpInputName).equalsIgnoreCase(phonPack.convert(tmpDbName))
						|| phonPack.convert(preInputName).equalsIgnoreCase(phonPack.convert(preDbName)))
					return true;
			}
		}
		return false;
	}
	private String getCompanyData(WbciCustomerInfoForPortingDAO dao,Firma firma, Standort address, 
			HashMap<String,Object> accNumResult, BksHelper theHelper, boolean isAccNumSearch)  throws Exception{
		String errorCode = null;
		String name1 = prePhonetic(firma.getFirmenname());
		String name2 = firma.getFirmenname();
		String companySuffix1 = prePhonetic(firma.getFirmennameZweiterTeil());
		String companySuffix2 = firma.getFirmennameZweiterTeil();
		String street1 = (address!=null)?prePhonetic(address.getStrasse().getStrassenname()):null;
		String street2 = (address!=null)?address.getStrasse().getStrassenname():null;
		String postalCode = (address!=null)?address.getPostleitzahl():null;
		String city1 = (address!=null)?prePhonetic(address.getOrt()):null;
		String city2 = (address!=null)?address.getOrt():null;
		ArrayList<HashMap<String, Object>> custdata = 
			dao.getCompanyByName(name1, name2, companySuffix1, companySuffix2, 
					postalCode, street1, street2, city1, city2);
		String custNum=null;
		for (int i=0;custdata!=null&&i<custdata.size();i++){
			if (((String) custdata.get(i).get("CUSTOMER_NUMBER")).equals(custNum))
				continue;
			else if (custNum==null)
				custNum = (String) custdata.get(i).get("CUSTOMER_NUMBER");
			else {
				errorCode="BKS0040";
				break;
			}
			String classification = (String)custdata.get(i).get("CLASSIFICATION_RD");
			accNumResult.put("CustomerNumber", custNum);
			String custName=(String) custdata.get(i).get("NAME");
			accNumResult.put("CustomerName",custName);
			
			String orgType=(String)custdata.get(i).get("ORGANIZATION_TYPE_RD");
			if (orgType==null || orgType.equalsIgnoreCase("BLANK"))
				orgType="";
			String concatinatedFirmName = custName + " " + orgType + " " + (String)custdata.get(i).get("ORGANIZATION_SUFFIX_NAME");
			accNumResult.put("ConcatinatedFirmName1", concatinatedFirmName);
			concatinatedFirmName = custName + " " + (String)custdata.get(i).get("ORGANIZATION_SUFFIX_NAME") + " " + orgType;
			accNumResult.put("ConcatinatedFirmName2", concatinatedFirmName);
			
			Standort dbAddr = new Standort();
			dbAddr.setOrt((String) custdata.get(i).get("CSA_CITY"));
			dbAddr.setPostleitzahl((String) custdata.get(i).get("CSA_POSTAL_CODE"));
			Strasse str = new Strasse();
			str.setStrassenname((String) custdata.get(i).get("CSA_STREET_NAME"));
			str.setHausnummer((String) custdata.get(i).get("CSA_STREET_NUMBER"));
			dbAddr.setStrasse(str);
			accNumResult.put("locationAddress",dbAddr);
			
			if (isAccNumSearch)
				return "BKS0067";
			
			ArrayList<HashMap<String, Object>> rootCust = dao.getRootCustomer(custNum);
			String rootCustomer = (String)rootCust.get(0).get("ROOT_CUSTOMER_NUMBER");
			String provider = theHelper.mapClassificationToProvider(classification,rootCustomer);
			if(!Arrays.asList(vodafoneProvider).contains(provider)){
				errorCode="BKS0035";
			}
			accNumResult.put("Provider", provider);
			ArrayList<HashMap<String, Object>> bundles = dao.getBundleInfoForCustomer(custNum);
			if (bundles==null || bundles.size() != 1 )
				errorCode="BKS0041";
			else {
				String bundleId = (String) bundles.get(0).get("BUNDLE_ID");
				ArrayList<HashMap<String, Object>> bundleInfo = dao.getBundleDataByBundleId(bundleId);
				String prodType = getProductType(dao,bundleInfo,accNumResult);
				if (prodType.equals("NOT_SUPPORTED"))
					errorCode="BKS0034";
				accNumResult.put("ProductType", prodType);
			}
		} 		
		if(custNum==null)
			errorCode="BKS0032";

		return errorCode;
	}
	private String getIndividualData(WbciCustomerInfoForPortingDAO dao,Person person, Standort address, 
			HashMap<String,Object> accNumResult, BksHelper theHelper, boolean isAccNumSearch)  throws Exception{
		String errorCode = null;
		String name1 = prePhonetic(person.getNachname());
		String name2 = person.getNachname();
		String forename1 = prePhonetic(person.getVorname());
		String forename2 = person.getVorname();
		String street1 = (address!=null)?prePhonetic(address.getStrasse().getStrassenname()):null;
		String street2 = (address!=null)?address.getStrasse().getStrassenname():null;
		String postalCode = (address!=null)?address.getPostleitzahl():null;
		String city1 = (address!=null)?prePhonetic(address.getOrt()):null;
		String city2 = (address!=null)?address.getOrt():null;
		ArrayList<HashMap<String, Object>> custdata = 
			dao.getCustomerByName(name1, name2, forename1, forename2, postalCode, 
					street1, street2, city1, city2);
		String custNum=null;
		for (int i=0;custdata!=null&&i<custdata.size();i++){
			if (((String) custdata.get(i).get("CUSTOMER_NUMBER")).equals(custNum))
				continue;
			else if (custNum==null)
				custNum = (String) custdata.get(i).get("CUSTOMER_NUMBER");
			else {
				errorCode="BKS0040";
				break;
			}
			String classification = (String)custdata.get(i).get("CLASSIFICATION_RD");
			accNumResult.put("CustomerNumber", custNum);
			accNumResult.put("CustomerName",(String) custdata.get(i).get("NAME"));
			accNumResult.put("CustomerForename",(String) custdata.get(i).get("FORENAME"));
			
			Standort dbAddr = new Standort();
			dbAddr.setOrt((String) custdata.get(i).get("CSA_CITY"));
			dbAddr.setPostleitzahl((String) custdata.get(i).get("CSA_POSTAL_CODE"));
			Strasse str = new Strasse();
			str.setStrassenname((String) custdata.get(i).get("CSA_STREET_NAME"));
			str.setHausnummer((String) custdata.get(i).get("CSA_STREET_NUMBER"));
			dbAddr.setStrasse(str);
			accNumResult.put("locationAddress",dbAddr);
			
			
			if (isAccNumSearch)
				return "BKS0067";

			ArrayList<HashMap<String, Object>> rootCust = dao.getRootCustomer(custNum);
			String rootCustomer = (String)rootCust.get(0).get("ROOT_CUSTOMER_NUMBER");
			String provider = theHelper.mapClassificationToProvider(classification,rootCustomer);
			if(!Arrays.asList(vodafoneProvider).contains(provider)){
				errorCode="BKS0035";
			}
			accNumResult.put("Provider", provider);
			ArrayList<HashMap<String, Object>> bundles = dao.getBundleInfoForCustomer(custNum);
			if (bundles==null || bundles.size() != 1 )
				errorCode="BKS0041";
			else {
				String bundleId = (String) bundles.get(0).get("BUNDLE_ID");
				ArrayList<HashMap<String, Object>> bundleInfo = dao.getBundleDataByBundleId(bundleId);
				String prodType = getProductType(dao,bundleInfo,accNumResult);
				if (prodType.equals("NOT_SUPPORTED"))
					errorCode="BKS0034";
				accNumResult.put("ProductType", prodType);
			}
		} 
		if(custNum==null)
			errorCode="BKS0032";
		return errorCode;
	}
	private String getAccessNumbersForServSubs(WbciCustomerInfoForPortingDAO dao,
			String serSuId, Rufnummernvergleich numberComp, BksHelper theHelper, boolean isActive, String abfrageStelle) throws Exception {
		ArrayList<HashMap<String, Object>> accInfo = dao.getAccessNumberForSerSu(serSuId,isActive?"ACTIVE":"DEACT");
		String errorCode=null;
		boolean isBaseNumberPopulated=false;
		boolean ccbZarMissmatch=false;
		for (int i = 0; i < accInfo.size(); i++) {
			String access = (String) accInfo.get(i).get("ACCESS_NUMBER");
			if (access == null)
				continue;
			String[] items = access.split(";");
			if (items.length>4){
				String portIdent=null;
				try {
					portIdent=getPortIdentifier(access,theHelper);
					if (portIdent==null){
						ccbZarMissmatch=true;
						continue;
					}
				} catch (BksDataException e) {
					ccbZarMissmatch=true;
					if (!isActive)
						continue;
					if (e.getCode() != null && e.getCode().equals("BKS0044"))
						throw e;
					return e.getCode();
				}
				isBaseNumberPopulated=populateAccNumRAnge(dao,numberComp,access,abfrageStelle,portIdent,isBaseNumberPopulated,isActive,theHelper);
			}else if (items.length>2){
				String portIdent=null;
				try {
					portIdent=getPortIdentifier(access,theHelper);
					if (portIdent==null){
						ccbZarMissmatch=true;
						continue;
					}
				} catch (BksDataException e) {
					ccbZarMissmatch=true;
					if (!isActive)
						continue;
					if (e.getCode() != null && e.getCode().equals("BKS0044"))
						throw e;
					return e.getCode();
				}
				poulateSingleAccNum(dao,numberComp,access,portIdent,isActive,theHelper);
			}
			if (errorCode != null){
				numberComp.setEinzelrufnummern(null);
				numberComp.setRufnummernblock(null);
				return errorCode;
			}
		}
		if (numberComp.getEinzelrufnummern()!=null && 
				numberComp.getEinzelrufnummern().getRufnummern().size()==0)
				numberComp.setEinzelrufnummern(null);
		if (numberComp.getRufnummernblock()!=null && 
				numberComp.getRufnummernblock().getRufnummernBloecke().size()==0)
				numberComp.setRufnummernblock(null);
		if (numberComp.getEinzelrufnummern()==null && numberComp.getRufnummernblock()==null){
			if (ccbZarMissmatch)
				return "BKS0066";
			else
				return "BKS0032";
		}
		return null;
	}
	private void poulateSingleAccNum(WbciCustomerInfoForPortingDAO dao, Rufnummernvergleich numberComp, String access, 
			String portIdent, boolean isActive, BksHelper theHelper) throws Exception {
		String[] items = access.split(";");
		Einzelrufnummern ern = numberComp.getEinzelrufnummern();
		if (ern==null){
			ern=new Einzelrufnummern();
			numberComp.setEinzelrufnummern(ern);
		}
		Rufnummern rn = new Rufnummern();
		rn.setONKZ(items[1]);
		rn.setRufnummer(items[2]);
		rn.setAktiv(isActive);
		rn.setPortingIdentifier(portIdent);
		String zarStatus = (getZarStatus(dao,access, theHelper, isActive));
		rn.setZARStatus(zarStatusMap.get(zarStatus));

		if (isActive || rn.getZARStatus()!=null)
			ern.getRufnummern().add(rn);
	}
	private boolean populateAccNumRAnge(WbciCustomerInfoForPortingDAO dao, Rufnummernvergleich numberComp,String access, String abfrageStelle, 
			String portIdent, boolean isBaseNumberPopulated, boolean isActive, BksHelper theHelper) throws Exception {
		String[] items = access.split(";");
		Rufnummernblock ern = numberComp.getRufnummernblock();
		if (ern==null){
			ern=new Rufnummernblock();
			numberComp.setRufnummernblock(ern);
		}
		if (!isBaseNumberPopulated) {
			OnkzDurchwahlAbfragestelle baseNumber = new OnkzDurchwahlAbfragestelle();
			ern.setOnkzDurchwahlAbfragestelle(baseNumber);
			baseNumber.setONKZ(items[1]);
			baseNumber.setDurchwahlnummer(items[2]);
			baseNumber.setAbfragestelle(abfrageStelle);
			isBaseNumberPopulated=true;
		}
		RufnummernBloecke rnb = new RufnummernBloecke();
		rnb.setRnrBlockVon(items[3]);
		rnb.setRnrBlockBis(items[4]);
		rnb.setAktiv(isActive);

		rnb.setPortingIdentifier(portIdent);
		String zarStatus = getZarStatus(dao,access, theHelper, isActive);
		rnb.setZARStatus(zarStatusMap.get(zarStatus));
		if (isActive || rnb.getZARStatus()!=null)
			ern.getRufnummernBloecke().add(rnb );
		return isBaseNumberPopulated;
	}
	private String getProductByAccNum(WbciCustomerInfoForPortingDAO dao,
			ArrayList<String> numberList,HashMap<String, Object> result, BksHelper theHelper) throws Exception {
		String serSuId = null;
		String custNum = null;
		String classification=null;
		String deactSerSuId = null;
		String deactCustNum = null;
		String deactName=null;
		String deactForename=null;
		String deactClass=null;
		boolean voip2nd=false;
		boolean deactFound=false;
		String errorCode=null;
		String custName=null;
		String custForename=null;
		String concatinatedFirmName1 = null;
		String concatinatedFirmName2 = null;
		String deactOrgType = null;
		String deactOrgSuffix=null;
		for (int i = 0; i < numberList.size(); i++) {
			ArrayList<HashMap<String, Object>> accInfo = getAccessNumberRows(dao,numberList.get(i),true);
			if (accInfo==null||accInfo.size()==0){
				ArrayList<HashMap<String, Object>> deactData = getAccessNumberRows(dao,numberList.get(i),false);
				if (deactData!=null&&deactData.size()>0){
					deactFound=true;
					deactSerSuId = deactData.get(0).get("SERVICE_SUBSCRIPTION_ID").toString();	
					deactCustNum = deactData.get(0).get("CUSTOMER_NUMBER").toString();
					deactName = (String) deactData.get(0).get("NAME");
					deactForename = (String) deactData.get(0).get("FORENAME");
					deactClass=(String)deactData.get(0).get("CLASSIFICATION_RD");
					deactOrgType = (String)deactData.get(0).get("ORGANIZATION_TYPE_RD");
					deactOrgSuffix=(String)deactData.get(0).get("ORGANIZATION_SUFFIX_NAME");
				}
				continue;
			}
			String serviceCode = accInfo.get(0).get("SERVICE_CODE").toString();	
			if (serviceCode.equals("VI201"))
				voip2nd=true;
			if (serSuId==null){
				serSuId = accInfo.get(0).get("SERVICE_SUBSCRIPTION_ID").toString();	
			} else if (!serSuId.equals((String)accInfo.get(0).get("SERVICE_SUBSCRIPTION_ID"))){
				if (voip2nd) 
					errorCode="BKS0038";
				else
					errorCode="BKS0039";
			}
			if (custNum==null){
				custNum = accInfo.get(0).get("CUSTOMER_NUMBER").toString();
				classification=(String)accInfo.get(0).get("CLASSIFICATION_RD");
				custName = accInfo.get(0).get("NAME").toString().trim();
				custForename=(String)accInfo.get(0).get("FORENAME");
				String orgType=(String)accInfo.get(0).get("ORGANIZATION_TYPE_RD");
				if (orgType==null || orgType.equalsIgnoreCase("BLANK"))
					orgType="";
				concatinatedFirmName1 = custName + " " + orgType + " " + (String)accInfo.get(0).get("ORGANIZATION_SUFFIX_NAME");
				concatinatedFirmName2 = custName + " " + (String)accInfo.get(0).get("ORGANIZATION_SUFFIX_NAME") + " " + orgType;
			} else if (!custNum.equals((String)accInfo.get(0).get("CUSTOMER_NUMBER"))){
				errorCode="BKS0040";
			}
		}
		if (custNum!=null){
			result.put("CustomerNumber", custNum);
			result.put("CustomerName", custName);
			result.put("CustomerForename", custForename);
			result.put("ConcatinatedFirmName1", concatinatedFirmName1);
			result.put("ConcatinatedFirmName2", concatinatedFirmName2);
			ArrayList<HashMap<String, Object>> rootCust = dao.getRootCustomer(custNum);
			String rootCustomer = (String)rootCust.get(0).get("ROOT_CUSTOMER_NUMBER");
			String provider = theHelper.mapClassificationToProvider(classification,rootCustomer);
			if(!Arrays.asList(vodafoneProvider).contains(provider)){
				errorCode="BKS0035";
			}
			result.put("Provider", provider);
		}
		if (serSuId!=null){
			result.put("VoiceServSubsId", serSuId);
			ArrayList<HashMap<String, Object>> bundleInfo = dao.getBundleData(serSuId);
			if (!voip2nd){
				String prodType = null;
				prodType = getProductType(dao,bundleInfo,result);
				if (prodType.equals("NOT_SUPPORTED"))
					errorCode="BKS0034";
				result.put("ProductType", prodType);
			} else {
				getProductType(dao,bundleInfo,result);
				result.put("ProductType", "VOIP 2nd");
				errorCode="BKS0034";
			}
		} else if (deactFound){
			result.put("VoiceServSubsId", deactSerSuId);
			result.put("CustomerNumber", deactCustNum);
			result.put("CustomerName", deactName);
			result.put("CustomerForename", deactForename);
			result.put("ProductType", "NOT_SUPPORTED");
			concatinatedFirmName1 = deactName + " " + deactOrgType + " " + deactOrgSuffix;
			concatinatedFirmName2 = deactName + " " + deactOrgSuffix + " " + deactOrgType;
			result.put("ConcatinatedFirmName1", concatinatedFirmName1);
			result.put("ConcatinatedFirmName2", concatinatedFirmName2);
			result.put("LineStatus", "DEACT");
			ArrayList<HashMap<String, Object>> rootCust = dao.getRootCustomer(deactCustNum);
			String rootCustomer = (String)rootCust.get(0).get("ROOT_CUSTOMER_NUMBER");
			String provider = theHelper.mapClassificationToProvider(classification,rootCustomer);
			result.put("Provider", provider);
		}
		return errorCode;
	}
	private ArrayList<HashMap<String, Object>> getAccessNumberRows(WbciCustomerInfoForPortingDAO dao, String number, boolean isActive) throws Exception {
		ArrayList<HashMap<String, Object>> accInfo = null;
		if (isActive)
			accInfo = dao.getCustomerDataByAccessNumber(number);
		else
			accInfo = dao.getDeactRecByAccessNumber(number);
			
		return accInfo;
	}
	private String getProductType(WbciCustomerInfoForPortingDAO dao, ArrayList<HashMap<String, Object>> bundleInfo,
			HashMap<String, Object> result) throws Exception {
		String portType="ADSL";
		String prodType=null;
		for (int i = 0; i < bundleInfo.size(); i++) {
			String serviceCode = (String) bundleInfo.get(i).get("SERVICE_CODE");
			String serSuId = (String) bundleInfo.get(i).get("SERVICE_SUBSCRIPTION_ID");
			if (Arrays.asList(voiceServices).contains(serviceCode)){
				result.put("VoiceServiceCode", serviceCode);
				result.put("VoiceServSubsId", serSuId);
			}
			if (serviceCode.equals("V0010") || serviceCode.equals("V0003") || serviceCode.equals("V0011")){
				result.put("AccessServSubsId", serSuId);
				result.put("AccessServiceCode", serviceCode);
				if (hasDslService(dao,serSuId))
					prodType="ISDN_DSL";
				else
					prodType="ISDN_ONLY";
			} else if (serviceCode.equals("I121z")) { 
				result.put("AccessServSubsId", serSuId);
				result.put("AccessServiceCode", serviceCode);
				getConfValueForServSubs(dao,serSuId,result);
				portType=(String) result.get("AccessTechnology");
				if (portType!=null && (portType.equals("FTTB-Ethernet")||portType.equals("FTTB-VDSL")))
					prodType="FTTB";
				else if (portType!=null && portType.equals("FTTH-Ethernet"))
					prodType="FTTH";
				else
					prodType="FTTX_OTHER";
			} else if (serviceCode.equals("I1209")||serviceCode.equals("I1207")) { 
				result.put("AccessServSubsId", serSuId);
				result.put("AccessServiceCode", serviceCode);
				getConfValueForServSubs(dao,serSuId,result);
				portType=(String) result.get("AccessTechnology");
				if (portType!=null && portType.equals("FTTH-GPON"))
					prodType="FTTH";
				else
					prodType="FTTX_OTHER";
			} else if (serviceCode.equals("I1213")){
				result.put("AccessServSubsId", serSuId);
				result.put("AccessServiceCode", serviceCode);
				getConfValueForServSubs(dao,serSuId,result);
				portType=(String) result.get("AccessTechnology");
				if(portType!=null && portType.equals("ADSL-AnnexJ"))
					prodType="BIT_ANNEXJ";
				else if(portType!=null && portType.equals("VDSL"))
					prodType="BIT_VDSL";
				else if(portType!=null && portType.equals("VDSL-L2"))
					prodType="BIT_VDSL";
				else
					prodType="BIT_ANNEXB";
			} else if (serviceCode.equals("I1210")){ 
				result.put("AccessServSubsId", serSuId);
				result.put("AccessServiceCode", serviceCode);
				getConfValueForServSubs(dao,serSuId,result);
				portType=(String) result.get("AccessTechnology");
				if (portType==null || portType.equals("ADSL") || portType.equals("ADSL-AnnexJ"))
					prodType="NGN_ADSL";
				else if (portType.equals("VDSL"))
					prodType="NGN_VDSL";
			} else if (serviceCode.equals("I1043")){
				getConfValueForServSubs(dao,serSuId,result);
				result.put("AccessServSubsId", serSuId);
				result.put("AccessServiceCode", serviceCode);
				prodType="DSLR";
			} else if (serviceCode.equals("I1290")){
				result.put("AccessServSubsId", serSuId);
				result.put("AccessServiceCode", serviceCode);
				prodType="LTE";
			} else if (serviceCode.equals("I1215")||serviceCode.equals("I1216")){
				result.put("AccessServSubsId", serSuId);
				result.put("AccessServiceCode", serviceCode);
				getConfValueForServSubs(dao,serSuId,result);
				prodType="BUSINESS_DSL";
			} else if (serviceCode.equals("VI010") && prodType==null) {
				result.put("AccessServSubsId", serSuId);
				result.put("AccessServiceCode", serviceCode);
				prodType="NOT_SUPPORTED";
			} else if (serviceCode.equals("VI250")) {
				result.put("AccessServSubsId", serSuId);
				result.put("AccessServiceCode", serviceCode);
				prodType="NOT_SUPPORTED";
			} else if ((serviceCode.equals("VI020")||serviceCode.equals("VI021")) && prodType==null){
				result.put("AccessServSubsId", serSuId);
				result.put("AccessServiceCode", serviceCode);
				prodType="SIP_TRUNK";
			}else if (serviceCode.equals("VI080") && prodType==null){
				result.put("AccessServSubsId", serSuId);
				result.put("AccessServiceCode", serviceCode);
				prodType="BUSINESS_VOIP";
			}
		}
		if (prodType==null)
			return "NOT_SUPPORTED";
		return prodType;
	}
	private boolean hasDslService(WbciCustomerInfoForPortingDAO dao,String serSuId) throws Exception {
		ArrayList<HashMap<String, Object>> childServ = dao.getChildService(serSuId);
		for (int i = 0; i < childServ.size(); i++) {
			String serviceCode = (String) childServ.get(i).get("SERVICE_CODE");
			if (serviceCode.equals("V0113")||serviceCode.equals("V0088"))
				return true;
		}
		return false;
	}
	private void loadCramerData(Rufnummern accNum,Rufnummernblock accNumRange, String tasi ,
			ArrayList<HashMap<String,Object>> cramerData, ArrayList<String[]> internalErrorList, BksHelper theHelper, HashMap<String,Object> accNumResult) throws Exception {
		boolean isCramerMocked=false;
		try {
			isCramerMocked = DatabaseClientConfig.getSetting("USE_CRAMER_MOCK_UP").equals("Y");
		} catch (Exception e1) {
			// Ignore (Use Default)
		}
		
		boolean isCramerMockedError=false;
		try {
			isCramerMockedError = DatabaseClientConfig.getSetting("USE_CRAMER_MOCK_UP").equals("E");
		} catch (Exception e1) {
			// Ignore (Use Default)
		}
		
		if (isCramerMocked){
			cramerData.add(new HashMap<String, Object>());
			ArrayList<String> dtagNumbers = theHelper.cramerMock(tasi);
			if (dtagNumbers==null){
				dtagNumbers = new ArrayList<String>();
				dtagNumbers.add("3004005006");
			}
			cramerData.get(0).put("DtagContractNumber",dtagNumbers);
			return;
		}
		
		if (isCramerMockedError){
			accNumResult.put("DTAGContractNumber",null);
			return;
		}
		CramerDAO cdi = new CramerDAOImpl();
		Connection connection = cdi.initDatabase();
		try {
			cramerData.add(new HashMap<String, Object>());
			HashMap<String, Object> cramerMap = cramerData.get(0);
			cdi.getCramerDataByTasi(connection,tasi,cramerMap,internalErrorList);
			if (cramerData.size()==0 || cramerData.get(0).get("DtagContractNumber")==null) {
				if (accNum!=null) {
					cdi.getCramerDataByPhoneNumber(connection,
							accNum.getONKZ(), accNum.getRufnummer(), null,
							cramerData, internalErrorList);
				}
				if (accNumRange!=null) {
					String areaCode = accNumRange.getOnkzDurchwahlAbfragestelle().getONKZ();
					String start= accNumRange.getOnkzDurchwahlAbfragestelle().getDurchwahlnummer() +
					accNumRange.getRufnummernBloecke().get(0).getRnrBlockVon();
					String end= accNumRange.getOnkzDurchwahlAbfragestelle().getDurchwahlnummer() +
					accNumRange.getRufnummernBloecke().get(0).getRnrBlockBis();
					cdi.getCramerDataByPhoneNumber(connection,areaCode, start, end,	cramerData, internalErrorList);
				}
			}
		}finally {
			cdi.closeConnection(connection);
		}
	}
	private String getZarStatus(WbciCustomerInfoForPortingDAO dao, String access, BksHelper theHelper, boolean isActive) throws Exception {
		boolean isZarMocked=false;
		try {
			isZarMocked = DatabaseClientConfig.getSetting("USE_ZAR_MOCK_UP").equals("Y");
		} catch (Exception e1) {
			// Ignore (Use Default)
		}
		if (isZarMocked){
			if (!isActive){
				String zarStatus = theHelper.zarStatusMock(access.substring(3));
				return (zarStatus!=null)?zarStatus:"P02,L12";
			}
			return null;
		}

		String[] accnumParts= access.split(";");
		String zarStatus = null;
		String number = null;
		if (accnumParts.length == 3)
			number=accnumParts[2];
		if (accnumParts.length > 4)
			number=accnumParts[2]+";"+accnumParts[3]+";"+accnumParts[4];
		if (!isActive&&number!=null)
			zarStatus=dao.getZarStatus(null, "0"+accnumParts[1], number);

		return zarStatus;
	}
	private String getPortIdentifier(String access, BksHelper theHelper) throws Exception{
		boolean isZarMocked=false;
		try {
			isZarMocked = DatabaseClientConfig.getSetting("USE_ZAR_MOCK_UP").equals("Y");
		} catch (Exception e1) {
			// Ignore (Use Default)
		}
		if (isZarMocked){
			String portIdent = theHelper.zarPortIdenMock(access.substring(3));
			if (portIdent!=null&&portIdent.matches("[0-9]+")){
				String errorCode = zarErrorMap.get(portIdent);
				if (errorCode==null) errorCode = "BKS0008";
				throw new BksDataException(errorCode,errorMap.get(errorCode));
			}
			if (portIdent==null)
				portIdent="D009";
			return portIdent;
		}
		ZarDAO cdi = new ZarDAOImpl();
		Connection connection = cdi.initDatabase();
		try {
			String[] accnumParts= access.split(";");
			String  areaCode = null;
			String start = null;
			String end = null;
			if (accnumParts.length>4){
				areaCode = accnumParts[1];
				start = accnumParts[2]+accnumParts[3];
				end = accnumParts[2]+accnumParts[4];
			}else if (accnumParts.length==3){
				areaCode = accnumParts[1];
				start = accnumParts[2];
				end = accnumParts[2];
			}
			return cdi.retrievePortIdentifier(connection, areaCode, start, end);
		}finally {
			cdi.closeConnection(connection);
		}
	}
}
