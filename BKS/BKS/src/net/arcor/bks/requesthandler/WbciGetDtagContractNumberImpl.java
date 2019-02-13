/*
    $Header:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/WbciGetDtagContractNumberImpl.java-arc   1.8   Jan 12 2018 14:14:28   makuier  $

    $Log:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/WbciGetDtagContractNumberImpl.java-arc  $
 * 
 *    Rev 1.8   Jan 12 2018 14:14:28   makuier
 * Adapted to signature changes in Cramer/Adia DAO. ITK 33956
 * 
 *    Rev 1.7   Jul 28 2014 15:11:06   makuier
 * Accept customers with no access numbers from 1&1
 * 
 *    Rev 1.6   Mar 11 2014 08:58:20   makuier
 * Zar connection.
 * 
 *    Rev 1.5   Feb 20 2014 07:15:38   makuier
 * Wbci phase 2.
 * 
 *    Rev 1.4   Jan 31 2014 12:45:16   makuier
 * Made reseller matching case insensitive.
 * 
 *    Rev 1.3   Jan 10 2014 12:10:14   makuier
 * retrieve tasi for 1&1.
 * 
 *    Rev 1.2   Jan 08 2014 10:40:34   makuier
 * Call Cramer for 1&1 product.
 * 
 *    Rev 1.1   Dec 20 2013 12:38:56   makuier
 * Added 1&1 to technology 
 * 
 *    Rev 1.0   Nov 07 2013 13:14:38   makuier
 * Initial revision.
*/
package net.arcor.bks.requesthandler;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import net.arcor.bks.client.DatabaseClient;
import net.arcor.bks.client.DatabaseClientConfig;
import net.arcor.bks.common.BksDataException;
import net.arcor.bks.common.BksHelper;
import net.arcor.bks.common.BksRefDataCacheHandler;
import net.arcor.bks.db.CramerDAO;
import net.arcor.bks.db.WbciGetDtagContractNumberDAO;
import net.arcor.bks.db.ZarDAO;
import net.arcor.bks.db.ibatis.CramerDAOImpl;
import net.arcor.bks.db.ibatis.ZarDAOImpl;
import net.arcor.mcf2.model.ServiceObjectEndpoint;
import net.arcor.mcf2.sender.MessageSender;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vodafone.mcf2.ws.exception.FunctionalException;
import com.vodafone.mcf2.ws.exception.FunctionalRuntimeException;
import com.vodafone.mcf2.ws.exception.TechnicalException;
import com.vodafone.mcf2.ws.model.impl.ServiceResponseSoap;
import com.vodafone.mcf2.ws.service.SoapEndpoint;
import com.vodafone.mcf2.ws.service.SoapOperation;

import de.vodafone.esb.service.sbus.bks.bks_wbci_001.AccessNumberType;
import de.vodafone.esb.service.sbus.bks.bks_wbci_001.PkiType;
import de.vodafone.esb.service.sbus.bks.bks_wbci_001.WbciGetDtagContractNumberRequest;
import de.vodafone.esb.service.sbus.bks.bks_wbci_001.WbciGetDtagContractNumberResponse;
import edu.emory.mathcs.backport.java.util.Arrays;


@SoapEndpoint(soapAction = "/BKS-001/WbciGetDtagContractNumber", context = "de.vodafone.esb.service.sbus.bks.bks_wbci_001", schema = "classpath:schema/BKS-WBCI-001.xsd")
public class WbciGetDtagContractNumberImpl implements SoapOperation<WbciGetDtagContractNumberRequest, WbciGetDtagContractNumberResponse> {

    /** The logger. */
    private final static Log log = LogFactory.getLog(WbciGetDtagContractNumberImpl.class);

    final static HashMap<String , String> charNameMap = new HashMap<String , String>() {
    	private static final long serialVersionUID = 1L;
    	{ 
    		put("I1325","DtagContractNumber");
    		put("V0152","Tasi");
    		put("V0188","Tasi");
    		put("V009C","AccessTechnology");
    		put("V0001","AccNum");
    		put("V0070","AccNum2");
    		put("V0071","AccNum3");
    		put("V0072","AccNum4");
    		put("V0073","AccNum5");
    		put("V0074","AccNum6");
    		put("V0075","AccNum7");
    		put("V0076","AccNum8");
    		put("V0077","AccNum9");
    		put("V0078","AccNum10");
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
    		put("BKS0042","Technical error");
    		put("BKS0050","Access Number does not exist.");
    		put("BKS0051","Access Number / tasi does not belong to reseller.");
    		put("BKS0052","Access number already deactivated.");
    		put("BKS0053","No dtag contractNumber found, but should exist for this product.");
    		put("BKS0054","Tasi does not exist.");
    		put("BKS0055","Tasi already deactivated.");
    		put("BKS0064","Technical errors during ZAR call.");
    		put("BKS0065","Wrong access number format according to ZAR.");
    	}
    }; 
	final static String[] talServices = {"V0003","V0010","V0011","VI002","VI003","VI006","VI009","VI201"};
	final static String[] cramerServices = {"V0003","V0010","V0011","VI002","VI003","I1210","I121z","VI201","Wh003","Wh010"};
	final static String[] additionalPhoneNumbers = {"AccNum2","AccNum3","AccNum4","AccNum5","AccNum6","AccNum7","AccNum8","AccNum9","AccNum10"};
    @Autowired
	private MessageSender messageSender;

	/**
	 * The operation "WbciGetDtagContractNumber" of the service "WbciGetDtagContractNumber".
	 */
	public ServiceResponseSoap<WbciGetDtagContractNumberResponse> invoke(ServiceObjectEndpoint<WbciGetDtagContractNumberRequest> serviceObject) 
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
		WbciGetDtagContractNumberDAO dao = (WbciGetDtagContractNumberDAO) DatabaseClient.getBksDataAccessObject(null, "WbciGetDtagContractNumberDAO");
		WbciGetDtagContractNumberRequest request = serviceObject.getPayload();
		WbciGetDtagContractNumberResponse output = new WbciGetDtagContractNumberResponse();
        long startTime = System.currentTimeMillis();
		try {
			output = getWbciContractData(dao,request,theHelper);
	        long endTime = System.currentTimeMillis();
	        log.info("Duration: "+(endTime-startTime));
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new TechnicalException("BKS0042","Technical error",e);
		} finally {
			try {
				dao.closeConnection();
			} catch (Exception e) {
			}
		}

		return new ServiceResponseSoap<WbciGetDtagContractNumberResponse>(output);
	}

	private WbciGetDtagContractNumberResponse getWbciContractData(
			WbciGetDtagContractNumberDAO dao,
			WbciGetDtagContractNumberRequest request, BksHelper theHelper) throws Exception {
		WbciGetDtagContractNumberResponse output = new WbciGetDtagContractNumberResponse();
		String tasi = request.getTasi();
		String reseller = request.getResellerId();
		AccessNumberType accessNumber = request.getAccessNumber();
		String errorCode = null;
		if (tasi!=null)
			errorCode = getDataByTasi(dao,tasi,reseller,theHelper,output);
		else if (accessNumber!=null)
			errorCode = getDataByAccessNumber(dao,accessNumber,reseller,theHelper,output);
		if (errorCode!=null) {
			output.setSuccessIndicator(false);
			output.setErrorCode(errorCode);
			output.setErrorText(errorMap.get(errorCode));
		} else {
			output.setSuccessIndicator(true);
		}
		return output;
	}

	private String getDataByAccessNumber(WbciGetDtagContractNumberDAO dao,
			AccessNumberType accessNumber,String reseller, BksHelper theHelper, 
			WbciGetDtagContractNumberResponse output) throws Exception {
		ArrayList<HashMap<String, Object>> accNumData = dao.getDataByAccessNumber(accessNumber.getCountryCode(),
				accessNumber.getLocalAreaCode(),accessNumber.getPilotNumber());
		if (accNumData!=null&&accNumData.size()>0){
			return populateOutput(dao, accNumData, accessNumber, null, reseller, theHelper, output);
		} else {
			ArrayList<HashMap<String, Object>> deactData = 
				dao.getDeactRecByAccessNumber(accessNumber.getCountryCode()+";"+
					accessNumber.getLocalAreaCode()+";"+accessNumber.getPilotNumber());
			if (deactData!=null&&deactData.size()>0)
				return "BKS0052";
			return "BKS0050";
		}
	}

	private String populateOutput(WbciGetDtagContractNumberDAO dao, ArrayList<HashMap<String, Object>> accNumData, 
			AccessNumberType accessNumber,String tasi,String reseller, BksHelper theHelper, WbciGetDtagContractNumberResponse output) throws Exception
	{
		String serCode = (String) accNumData.get(0).get("SERVICE_CODE");
		String serSuId = (String) accNumData.get(0).get("SERVICE_SUBSCRIPTION_ID");
		String classification = (String) accNumData.get(0).get("CLASSIFICATION_RD");
		String custNum = (String) accNumData.get(0).get("CUSTOMER_NUMBER");
		ArrayList<HashMap<String, Object>> rootCust = dao.getRootCustomer(custNum);
		String rootCustomer = (String)rootCust.get(0).get("ROOT_CUSTOMER_NUMBER");
		String provider = theHelper.mapClassificationToProvider(classification,rootCustomer);
		String mappedReseller = null;
		if (reseller.equalsIgnoreCase("DEU.1und1"))
			mappedReseller = "1UN1";
		else
			mappedReseller = reseller;
		if(!provider.equals(mappedReseller))
			return "BKS0051";
		HashMap<String, Object> result = new HashMap<String, Object>();
		String prodType =null;
		ArrayList<HashMap<String, Object>> bundleInfo = dao.getBundleData(serSuId);
		if (!serCode.equals("VI201")) {
			prodType = getProductType(dao, bundleInfo, result);
			if (tasi == null && serCode.equals("Wh010"))
				getAccNumForSerSu(dao,bundleInfo, result);
		} else
			prodType = "VOIP 2nd";
		output.setExistingTechnology(theHelper.getWbciTechnology(prodType));
		String portIdent = null;
		try {
			String access = null;	
			if (accessNumber!=null) {
				access = "49;"+accessNumber.getLocalAreaCode() + ";"+ accessNumber.getPilotNumber();
				if (accessNumber.getStartRange() != null)
					access += ";" + accessNumber.getStartRange();
				if (accessNumber.getEndRange() != null)
					access += ";" + accessNumber.getEndRange();
			} else {
				getAccNumForSerSu(dao,bundleInfo, result);
				access=(String) result.get("AccNum");
			}
			if (access!=null)
				portIdent = getZarPortIdent(access, theHelper);
			for (int i=0;i<additionalPhoneNumbers.length;i++){
				String additionalNumber = (String) result.get(additionalPhoneNumbers[i]);
				if (additionalNumber!=null)
					 getZarPortIdent(additionalNumber, theHelper);
			}
		} catch (BksDataException e){
			return e.getCode();
		}
		if (portIdent != null)
			output.setPki(PkiType.fromValue(portIdent));
		ArrayList<HashMap<String, Object>> cramerData = new ArrayList<HashMap<String,Object>>();
		String dtagNum=(String) result.get("DtagContractNumber");
		if (Arrays.asList(cramerServices).contains(serCode)) {
			ArrayList<String[]> internalErrorList = new ArrayList<String[]>();
			loadCramerData(accessNumber, (tasi==null)?(String)result.get("Tasi"):tasi, 
					cramerData, internalErrorList, theHelper);
			if (cramerData.size() > 0 && cramerData.get(0).get("DtagContractNumber") != null)
				dtagNum = ((ArrayList<String>) cramerData.get(0).get("DtagContractNumber")).get(0);
		}
		output.setDtagContractNumber(dtagNum);
		if (dtagNum==null && Arrays.asList(talServices).contains(serCode))
			return "BKS0053";
		return null;
	}
	private void getAccNumForSerSu(WbciGetDtagContractNumberDAO dao, 
			ArrayList<HashMap<String, Object>> bundleInfo, HashMap<String, Object> result) throws Exception {
		for (int j = 0; j < bundleInfo.size(); j++) {
			String serSuId=(String) bundleInfo.get(j).get("SERVICE_SUBSCRIPTION_ID");
			ArrayList<HashMap<String, Object>> accInfo = dao.getAccNumForSerSu(serSuId);
			for (int i = 0; i < accInfo.size(); i++) {
				String charCode = (String) accInfo.get(i).get("SERVICE_CHARACTERISTIC_CODE");
				String name = charNameMap.get(charCode);
				String value = (String) accInfo.get(i).get("ACCESS_NUMBER");
				if (name != null)
					result.put(name, value);
			}
		}
	}

	private String getDataByTasi(WbciGetDtagContractNumberDAO dao, String tasi,
			String reseller, BksHelper theHelper, WbciGetDtagContractNumberResponse output) throws Exception {
		ArrayList<HashMap<String, Object>> accNumData = dao.getDataByTasi(tasi);
		if (accNumData!=null&&accNumData.size()>0){
			return populateOutput(dao, accNumData, null, tasi, reseller, theHelper, output);
		} else {
			ArrayList<HashMap<String, Object>> deactData = dao.getDeactRecByAccessNumber(tasi);
			if (deactData!=null&&deactData.size()>0)
				return "BKS0055";
			return "BKS0054";
		}
		
	}
	private String getProductType(WbciGetDtagContractNumberDAO dao, 
			ArrayList<HashMap<String, Object>> bundleInfo, HashMap<String, Object> result) throws Exception {
		String portType="ADSL";
		String prodType=null;
		for (int i = 0; i < bundleInfo.size(); i++) {
			String serviceCode = (String) bundleInfo.get(i).get("SERVICE_CODE");
			String serSuId = (String) bundleInfo.get(i).get("SERVICE_SUBSCRIPTION_ID");
			getConfValueForServSubs(dao,serSuId,result);
			if (serviceCode.equals("V0010") || serviceCode.equals("V0003")){
				if (hasServiceDsl(dao,serSuId))
					prodType="ISDN_DSL";
				else
					prodType="ISDN_ONLY";
			} else if (serviceCode.equals("I121z")) { 
				portType=(String) result.get("AccessTechnology");
				if (portType!=null && (portType.equals("FTTB-Ethernet")||portType.equals("FTTB-VDSL")))
					prodType="FTTB";
				else if (portType!=null && portType.equals("FTTH-Ethernet"))
					prodType="FTTH";
				else
					prodType="FTTX_OTHER";
			} else if (serviceCode.equals("I1213")){
				portType=(String) result.get("AccessTechnology");
				if(portType!=null && portType.equals("ADSL-AnnexJ"))
					prodType="BIT_ANNEXJ";
				else if(portType!=null && portType.equals("VDSL"))
					prodType="BIT_VDSL";
				else
					prodType="BIT_ANNEXB";
			} else if (serviceCode.equals("I1210")){ 
				portType=(String) result.get("AccessTechnology");
				if (portType==null || portType.equals("ADSL"))
					prodType="NGN_ADSL";
				else if (portType.equals("VDSL"))
					prodType="NGN_VDSL";
			} else if (serviceCode.equals("I1043")){
				prodType="DSLR";
			} else if (serviceCode.equals("I1290")||serviceCode.equals("Wh004")){
				prodType="LTE";
			} else if ((serviceCode.equals("I1215")||serviceCode.equals("I1216")) && prodType==null)
				prodType="BUSINESS_DSL";
			else if (serviceCode.equals("Wh003"))
				prodType="ONE_AND_ONE";
			else if (serviceCode.equals("VI010")||serviceCode.equals("VI020")||serviceCode.equals("VI021"))
				prodType="NOT_SUPPORTED";
		}
		if (prodType==null)
			return "NOT_SUPPORTED";
		return prodType;
	}

	private boolean hasServiceDsl(WbciGetDtagContractNumberDAO dao,	String serSuId) throws Exception {
		ArrayList<HashMap<String, Object>> childServ = dao.getChildService(serSuId);
		for (int i = 0; i < childServ.size(); i++) {
			String serviceCode = (String) childServ.get(i).get("SERVICE_CODE");
			if (serviceCode.equals("V0113")||serviceCode.equals("V0088"))
				return true;
		}
		return false;
	}

	private void getConfValueForServSubs(WbciGetDtagContractNumberDAO dao,
			String serSuId, HashMap<String, Object> result) throws Exception {
		ArrayList<HashMap<String, Object>> accInfo = dao.getConfigValueForSerSu(serSuId);
		for (int i = 0; i < accInfo.size(); i++) {
			String charCode = (String) accInfo.get(i).get("SERVICE_CHARACTERISTIC_CODE");
			String name = charNameMap.get(charCode);
			String value = (String) accInfo.get(i).get("CONFIGURED_VALUE_STRING");
			if (name!=null)
				result.put(name,value);
		}
	}
	private void loadCramerData(AccessNumberType accNum,String tasi ,ArrayList<HashMap<String,Object>> cramerData, ArrayList<String[]> internalErrorList, BksHelper theHelper) throws Exception {
		boolean isCramerMocked=false;
		try {
			isCramerMocked = DatabaseClientConfig.getSetting("USE_CRAMER_MOCK_UP").equals("Y");
		} catch (Exception e1) {
			// Ignore (Use Default)
		}
		if (isCramerMocked){
			cramerData.add(new HashMap<String, Object>());
			ArrayList<String> dtagNumbers = theHelper.cramerMock(tasi);
			if (dtagNumbers!=null&&dtagNumbers.size()==0)
				dtagNumbers.add("3004005006");

			cramerData.get(0).put("DtagContractNumber",dtagNumbers);
			return;
		}
		CramerDAO cdi = new CramerDAOImpl();
		Connection connection = cdi.initDatabase();
		try {
			if (tasi==null){
				cdi.getCramerDataByPhoneNumber(connection,accNum.getLocalAreaCode(),
						accNum.getPilotNumber(),null,cramerData,internalErrorList);
			} else {
				cramerData.add(new HashMap<String, Object>());
				HashMap<String, Object> cramerMap = cramerData.get(0);
				cdi.getCramerDataByTasi(connection,tasi,cramerMap,internalErrorList);
			}
		}finally {
			cdi.closeConnection(connection);
		}
	}
	private String getZarPortIdent(String access, BksHelper theHelper) throws Exception {
		boolean isZarMocked=false;
		try {
			isZarMocked = DatabaseClientConfig.getSetting("USE_ZAR_MOCK_UP").equals("Y");
		} catch (Exception e1) {
			// Ignore (Use Default)
		}
		if (isZarMocked){
			String portIdent = theHelper.zarPortIdenMock(access.substring(3));
			if (portIdent.matches("[0-9]+")){
				String errorCode = zarErrorMap.get(portIdent);
				if (errorCode==null)
					errorCode = "BKS0042";
				throw new BksDataException(errorCode,errorMap.get(errorCode));			
			}
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
