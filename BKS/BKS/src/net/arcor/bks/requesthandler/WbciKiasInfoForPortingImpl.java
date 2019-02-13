/*
    $Header:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/WbciKiasInfoForPortingImpl.java-arc   1.12   Jan 08 2016 13:43:36   makuier  $

    $Log:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/WbciKiasInfoForPortingImpl.java-arc  $
 * 
 *    Rev 1.12   Jan 08 2016 13:43:36   makuier
 * In case of Kias exception throw BKS0042.
 * 
 *    Rev 1.11   Jul 17 2015 13:53:06   makuier
 * Handle the scenario when KIAS phone number bwlongs to a company but inpu is an Individual;
 * 
 *    Rev 1.10   Dec 05 2014 14:23:52   makuier
 * New algorithm for name comparison.
 * 
 *    Rev 1.9   Aug 20 2014 10:54:32   makuier
 * set the bpId unique pattern.
 * 
 *    Rev 1.8   Jul 03 2014 10:25:20   makuier
 * Interpret KIAS codes.
 * 
 *    Rev 1.7   May 16 2014 14:39:52   makuier
 * Forward the  Kias error code to the caller.
 * 
 *    Rev 1.6   Apr 29 2014 15:27:26   makuier
 * Set the active flag
 * 
 *    Rev 1.5   Apr 28 2014 10:31:56   makuier
 * Bug fix
 * 
 *    Rev 1.4   Apr 23 2014 07:48:42   makuier
 * Catch Soap exception. Phonetic match.
 * 
 *    Rev 1.3   Mar 28 2014 12:36:24   makuier
 * - Put  necessary information in the header for KIAS call
 * 
 *    Rev 1.2   Mar 11 2014 08:54:44   makuier
 * Name and address compare.
 * 
 *    Rev 1.1   Mar 06 2014 12:19:10   makuier
 * put termination date correctly.
 * 
 *    Rev 1.0   Feb 20 2014 07:16:50   makuier
 * Initial revision.
 */
package net.arcor.bks.requesthandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import net.arcor.bks.client.DatabaseClient;
import net.arcor.bks.client.DatabaseClientConfig;
import net.arcor.bks.common.BksDataException;
import net.arcor.bks.common.BksHelper;
import net.arcor.bks.common.BksRefDataCacheHandler;
import net.arcor.bks.util.FudickarPhoneticConverter;
import net.arcor.mcf2.exception.base.MCFException;
import net.arcor.mcf2.model.ServiceObjectEndpoint;
import net.arcor.mcf2.model.ServiceResponse;
import net.arcor.mcf2.sender.MessageSender;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.domainlanguage.time.CalendarDate;
import com.vodafone.mcf2.ws.exception.FunctionalException;
import com.vodafone.mcf2.ws.exception.FunctionalRuntimeException;
import com.vodafone.mcf2.ws.exception.TechnicalException;
import com.vodafone.mcf2.ws.model.impl.ServiceRequestSoap;
import com.vodafone.mcf2.ws.model.impl.ServiceResponseSoap;
import com.vodafone.mcf2.ws.service.SoapEndpoint;
import com.vodafone.mcf2.ws.service.SoapOperation;
import com.vodafone.mcf2.ws.utils.ESBExceptionUtils;

import de.vodafone.esb.schema.common.basetypes_esb_001.AppMonDetailsType;
import de.vodafone.esb.schema.common.basetypes_esb_001.ESBException;
import de.vodafone.esb.schema.common.basetypes_esb_001.ErrorDetailsType;
import de.vodafone.esb.service.eai.customer.customer.vfzhcustomer.vfzhcustomercommontypes_001.AddressType;
import de.vodafone.esb.service.eai.customer.customer.vfzhcustomer.vfzhcustomercommontypes_001.FnnDetailedInfoType;
import de.vodafone.esb.service.eai.customer.customer.vfzhcustomer.vfzhcustomercommontypes_001.FnnListType;
import de.vodafone.esb.service.eai.customer.customer.vfzhcustomer.vfzhcustomercommontypes_001.FnnType;
import de.vodafone.esb.service.eai.customer.customer.vfzhcustomer.vfzhcustomercommontypes_001.LegalNameType;
import de.vodafone.esb.service.eai.customer.vfzhcustomer.vfzhcustomer_001.SearchByFNNRequest;
import de.vodafone.esb.service.eai.customer.vfzhcustomer.vfzhcustomer_001.SearchByFNNResponse;
import de.vodafone.esb.service.sbus.bks.bks_wbci_vf_kias_001.WbciKiasInfoForPortingRequest;
import de.vodafone.esb.service.sbus.bks.bks_wbci_vf_kias_001.WbciKiasInfoForPortingRequest.Rufnummernportierung.Einzelanschluss.Rufnummernliste.ZuPortierendeOnkzRnr;
import de.vodafone.esb.service.sbus.bks.bks_wbci_vf_kias_001.WbciKiasInfoForPortingResponse;
import de.vodafone.esb.service.sbus.bks.bks_wbci_vf_kias_001.WbciKiasInfoForPortingResponse.Adressvergleich;
import de.vodafone.esb.service.sbus.bks.bks_wbci_vf_kias_001.WbciKiasInfoForPortingResponse.Adressvergleich.AdresseAbweichend;
import de.vodafone.esb.service.sbus.bks.bks_wbci_vf_kias_001.WbciKiasInfoForPortingResponse.Kundendaten;
import de.vodafone.esb.service.sbus.bks.bks_wbci_vf_kias_001.WbciKiasInfoForPortingResponse.NamensVergleich;
import de.vodafone.esb.service.sbus.bks.bks_wbci_vf_kias_001.WbciKiasInfoForPortingResponse.Rufnummernvergleich;
import de.vodafone.esb.service.sbus.bks.bks_wbci_vf_kias_001.WbciKiasInfoForPortingResponse.Rufnummernvergleich.Einzelrufnummern;
import de.vodafone.esb.service.sbus.bks.bks_wbci_vf_kias_001.WbciKiasInfoForPortingResponse.Rufnummernvergleich.Einzelrufnummern.Rufnummern;
import de.vodafone.esb.service.sbus.bks.bks_wbci_vf_simpletypes_002.KundentypType;

@SoapEndpoint(soapAction = "/BKS-001/WbciKIASInfoForPorting", context = "de.vodafone.esb.service.sbus.bks.bks_wbci_vf_kias_001", schema = "classpath:schema/BKS-WBCI-VF-KIAS-001.xsd")
public class WbciKiasInfoForPortingImpl implements SoapOperation<WbciKiasInfoForPortingRequest, WbciKiasInfoForPortingResponse> {

    final static HashMap<String , String> accCustMap = new HashMap<String , String>() {
    	private static final long serialVersionUID = 1L;
    	{ 
    		put("I","PK");
    		put("A","GK");
    		put("B","GK");
    		put("S","GK");
    	}
    }; 
    final static HashMap<String , String> errorMap = new HashMap<String , String>() {
    	private static final long serialVersionUID = 1L;
    	{ 
     		put("BKS0032","Not possible to identify customer.");
     		put("BKS0039","Access numbers belong to different access/technology.");
    		put("BKS0040","More than one customer found.");
    		put("BKS0062","AccessNumber is a WirelessOffice number.");
    		put("BKS0063","Existing FNI Order ongoing.");
    	}
    }; 
    /** The logger. */
    private final static Log log = LogFactory.getLog(WbciKiasInfoForPortingImpl.class);
     @Autowired
	private MessageSender messageSender;

	/**
	 * The operation "WbciKiasInfoForPorting" of the service "WbciKiasInfoForPorting".
	 */
	public ServiceResponseSoap<WbciKiasInfoForPortingResponse> invoke(ServiceObjectEndpoint<WbciKiasInfoForPortingRequest> serviceObject) 
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
		WbciKiasInfoForPortingRequest request = serviceObject.getPayload();
		WbciKiasInfoForPortingResponse output = new WbciKiasInfoForPortingResponse();
        long startTime = System.currentTimeMillis();
		try {
			output = getWbciKiasCustData(request,theHelper);
	        long endTime = System.currentTimeMillis();
	        log.info("Duration: "+(endTime-startTime));
		} catch (SoapFaultClientException e) {
			output.setSucessIndicator(false);
			ESBException esbException = ESBExceptionUtils.getESBException(e);
			ErrorDetailsType details = esbException.getErrorDetails();
			String errCode = details.getErrorCode();
			log.error(errCode+" "+e.getMessage());
			throw new TechnicalException("BKS0042","Technical error",e);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new TechnicalException("BKS0042","Technical error",e);
		}
		return new ServiceResponseSoap<WbciKiasInfoForPortingResponse>(output);
	}
	private WbciKiasInfoForPortingResponse getWbciKiasCustData(
			WbciKiasInfoForPortingRequest request, BksHelper theHelper)throws Exception{
		List<ZuPortierendeOnkzRnr> accNums = 
			request.getRufnummernportierung().getEinzelanschluss().getRufnummernliste().getZuPortierendeOnkzRnr();
		SearchByFNNResponse kiasResp = getKiasCustomerData(accNums);
		return mapKiasDataToCcb(request,kiasResp,theHelper);
	}
	private WbciKiasInfoForPortingResponse mapKiasDataToCcb(WbciKiasInfoForPortingRequest request, SearchByFNNResponse kiasResp, BksHelper theHelper) throws Exception {
		WbciKiasInfoForPortingResponse output = new WbciKiasInfoForPortingResponse();
		List<FnnDetailedInfoType> custList = kiasResp.getFnnDetailedInfo();
		String ban = null;
		String subscriber = null;
		String errorCode = null;
		for (int i = 0; errorCode ==null && i < custList.size(); i++) {
			FnnDetailedInfoType kiasCust = custList.get(i);
			if (!kiasCust.isFoundIndicator()|| kiasCust.getBan()==null || kiasCust.getSubscriberNumber()==null)
				continue;
			
			output.setSucessIndicator(true);
			if (ban!=null&&!ban.equals(kiasCust.getBan()))
				errorCode= "BKS0040";
			if (subscriber!=null&&!subscriber.equals(kiasCust.getSubscriberNumber()))
				errorCode= "BKS0039";
			ban= kiasCust.getBan();
			subscriber=kiasCust.getSubscriberNumber();
			Kundendaten customer = new Kundendaten();
			customer.setBAN(ban);
			customer.setMSISDN(subscriber);
			customer.setKundentyp(Enum.valueOf(KundentypType.class,accCustMap.get(kiasCust.getAccountType())));
			customer.setAccountSubTyp(kiasCust.getAccountSubType());
			customer.setEarlyTermination(kiasCust.isAllowEarlyOutport()); 
			CalendarDate termDate = (kiasCust.getFutureCanceDate()!=null)?kiasCust.getFutureCanceDate():
				kiasCust.getNextPossibleCancellationDate();
			customer.setKuendigungsdatum(termDate);
			customer.setEigenkuendigung(kiasCust.getFutureCanceDate()!=null);
			output.setKundendaten(customer);
			if (kiasCust.isWirelessOfficeIndicator()!=null&&kiasCust.isWirelessOfficeIndicator())
				errorCode= "BKS0062";
			if (kiasCust.isExistsFNIOrder()!=null&&kiasCust.isExistsFNIOrder())
				errorCode= "BKS0063";
			if (errorCode==null) {
				verifyNames(request, kiasCust, output, theHelper);
				if (request.getStandort() != null)
				compareAddresses(request, kiasCust, output);
				comparePhoneNumbers(kiasCust, output);
			}

		}
		if (output.getKundendaten()==null&&errorCode==null)
			errorCode= "BKS0032";
		if (errorCode != null){
			output.setSucessIndicator(false);
			output.setErrorCode(errorCode);
			output.setErrorText(errorMap.get(errorCode));
		}
		return output;
	}
	private void comparePhoneNumbers(FnnDetailedInfoType kiasCust,
			WbciKiasInfoForPortingResponse output) {
		if (kiasCust.getFnnList()==null)
		   return;
		output.setRufnummernvergleich(new Rufnummernvergleich());
		output.getRufnummernvergleich().setSuccess(true);
		output.getRufnummernvergleich().setEinzelrufnummern(new Einzelrufnummern());
		List<Rufnummern> numList = output.getRufnummernvergleich().getEinzelrufnummern().getRufnummern();
		List<FnnType> nums = kiasCust.getFnnList().getFnn();
		for (int i = 0; i < nums.size(); i++) {
			FnnType kiasNum = nums.get(i);
			Rufnummern rn = new Rufnummern();
			rn.setONKZ(kiasNum.getLac());
			rn.setRufnummer(kiasNum.getTn());
			rn.setAktiv(kiasCust.isIsFnnActive());
			numList.add(rn);
		}
		
	}
	private boolean areSteetNumbersEqual(String ccbStrNum, String reqStrNum) {
		if (ccbStrNum==null || reqStrNum==null)
			return false;
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
		returnValue=returnValue.replaceAll("ß", "SS");
		return returnValue;
	}
	private void compareAddresses(WbciKiasInfoForPortingRequest request,
			FnnDetailedInfoType kiasCust, WbciKiasInfoForPortingResponse output) {
		FudickarPhoneticConverter phonPack = new FudickarPhoneticConverter();
		Adressvergleich addrComp= new Adressvergleich();
		addrComp.setSuccess(false);
		addrComp.setOrtKorrekt(false);
		addrComp.setPlzKorrekt(false);
		addrComp.setStrassennameKorrekt(false);
		addrComp.setHausnummerKorrekt(false);
		AddressType address = kiasCust.getHomeAreaAddress();
		if (address==null)
			return;
		if (address.getCity()!=null&&address.getCity().equals(request.getStandort().getOrt()))
			addrComp.setOrtKorrekt(true);
		if (address.getZip()!=null&&address.getZip().equals(request.getStandort().getPostleitzahl()))
			addrComp.setPlzKorrekt(true);
		String dbStrName = prePhonetic(address.getStreetName());
		String reqStrName = prePhonetic(request.getStandort().getStrasse().getStrassenname());
		if (dbStrName!=null&&reqStrName!=null&&phonPack.convert(dbStrName).equals(phonPack.convert(reqStrName)))
			addrComp.setStrassennameKorrekt(true);
		if (areSteetNumbersEqual(address.getHouseNumber(),
				(request.getStandort().getStrasse().getHausnummer())))
			addrComp.setHausnummerKorrekt(true);
		if (addrComp.isOrtKorrekt()&&addrComp.isPlzKorrekt()&&
				addrComp.isStrassennameKorrekt()&&addrComp.isHausnummerKorrekt())
			addrComp.setSuccess(true);
		if (!addrComp.isSuccess()){
			AdresseAbweichend deviatAddr=new AdresseAbweichend();
			deviatAddr.setOrt(address.getCity());
			deviatAddr.setPostleitzahl(address.getZip());
			deviatAddr.setStrassenname(address.getStreetName());
			deviatAddr.setHausnummer(address.getHouseNumber());
			addrComp.setAdresseAbweichend(deviatAddr);
		}
		output.setAdressvergleich(addrComp);
	}
	private void verifyNames(WbciKiasInfoForPortingRequest request,
			FnnDetailedInfoType kiasCust, WbciKiasInfoForPortingResponse output, BksHelper theHelper) throws Exception{
		NamensVergleich namecomp = new NamensVergleich();
		namecomp.setSuccess(false);
		namecomp.setNameKorrekt(false);
		FudickarPhoneticConverter phonPack = new FudickarPhoneticConverter();
		LegalNameType legalName = kiasCust.getLegalName();
		if (legalName==null)
			return;
		String kiasName = legalName.getLastBusinessName1()+
			((legalName.getLastBusinessName2()!=null)?legalName.getLastBusinessName2():"")+
			((legalName.getLastBusinessName3()!=null)?legalName.getLastBusinessName3():"");
		String kiasForename = legalName.getFirstname();
		if (request.getEndkunde().getFirma()!=null){
			String name = request.getEndkunde().getFirma().getFirmenname();
			String secPart = request.getEndkunde().getFirma().getFirmennameZweiterTeil();
			if (kiasName!=null&&name!=null&&
					phonPack.convert(kiasName).startsWith(phonPack.convert(name)) && 
				(secPart == null || kiasName.toLowerCase().contains(secPart.toLowerCase())))
				namecomp.setNameKorrekt(true);
		} else if (request.getEndkunde().getPerson()!=null){
			String inputName = request.getEndkunde().getPerson().getNachname();
			String inputForename = request.getEndkunde().getPerson().getVorname();
			if (inputName!=null&&kiasName!=null)
				namecomp.setNameKorrekt(compareNames(inputName, kiasName, theHelper));
			if (inputForename!=null && kiasForename!=null)
					namecomp.setVornameKorrekt(compareNames(inputForename, kiasForename, theHelper));
			if (!namecomp.isNameKorrekt() && namecomp.isVornameKorrekt()!=null && !namecomp.isVornameKorrekt()) {
				if (kiasForename!=null)
					namecomp.setNameKorrekt(compareNames(inputName,kiasForename, theHelper));
				if (inputForename!=null)
					namecomp.setVornameKorrekt(compareNames(inputForename,kiasName, theHelper));
			}
			
		}
		if (namecomp.isNameKorrekt() && (namecomp.isVornameKorrekt()==null || namecomp.isVornameKorrekt()))
			namecomp.setSuccess(true);
		output.setNamensVergleich(namecomp);
	}
	private boolean compareNames(String inputName,String dbName, BksHelper theHelper) throws Exception{
		inputName = inputName.replaceAll("[-|_|\\.|,|\\+|#|!|§’|´|`]", " ");
		dbName = dbName.replaceAll("[-|_|\\.|,|\\+|#|!|§’|´|`]", " ");
		
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
				if (phonPack.convert(tmpInputName).equals(phonPack.convert(tmpDbName))
						|| phonPack.convert(preInputName).equals(phonPack.convert(preDbName)))
					return true;
			}
		}
		return false;
	}
	private SearchByFNNResponse getKiasCustomerData(List<ZuPortierendeOnkzRnr> accNums) throws Exception {
		ApplicationContext ac = DatabaseClient.getAc();
		if (ac == null)
			throw new MCFException("Application context has not been initialized.");
		int timeToLive = 3600000;
		int timeOut = 100000;
		try {
			timeToLive = Integer.parseInt(DatabaseClientConfig.getSetting("servicebusclient.TimeToLive"));
			timeOut = Integer.parseInt(DatabaseClientConfig.getSetting("servicebusclient.TimeOut"));
		} catch (Exception e1) {
			// Ignore (Use Default)
		}
		try {
			String firstPhoneNr=accNums.get(0).getONKZ()+accNums.get(0).getRufnummer();
			SearchByFNNRequest request = new SearchByFNNRequest();
			request.setFnnList(new FnnListType());
			List<FnnType> numList = request.getFnnList().getFnn();
			for (int i = 0; i < accNums.size(); i++) {
				FnnType kiasNum = new FnnType();
				kiasNum.setLac(accNums.get(i).getONKZ());
				kiasNum.setTn(accNums.get(i).getRufnummer());
				numList.add(kiasNum);
			}
			ServiceRequestSoap<SearchByFNNRequest> serviceRequest =
				new ServiceRequestSoap<SearchByFNNRequest>(request,
						"classpath:schema/VFZHCustomer-001.xsd",
				"de.vodafone.esb.service.eai.customer.vfzhcustomer.vfzhcustomer_001");
			serviceRequest.setOperationName("/VFZHCustomer-001/SearchByFNN");		
			serviceRequest.setTimeToLive(timeToLive);
			AppMonDetailsType amdt = new AppMonDetailsType();
			amdt.setCallingApp("BKS");
			amdt.setBoId(firstPhoneNr);
			Date current = new Date();
			amdt.setBpId(firstPhoneNr+"_"+current.getTime());
			amdt.setBpName("Kias EAI");
			amdt.setInitiator("SBUSBKS - " + java.net.InetAddress.getLocalHost().getHostName());
			serviceRequest.setAppMonDetails(amdt);
			ServiceResponse<SearchByFNNResponse> respServ = messageSender.sendSyncSoapRequest(serviceRequest, timeOut);
			SearchByFNNResponse resp = respServ.getPayload();
			return  resp;
		} catch (Exception e) {
			log.error("Call to Kias failed. Message: "+e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}
}
