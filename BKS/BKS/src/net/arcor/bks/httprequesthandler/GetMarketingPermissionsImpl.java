/*
    $Header:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/httprequesthandler/GetMarketingPermissionsImpl.java-arc   1.7   Feb 05 2019 09:07:30   makuier  $

    $Log:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/httprequesthandler/GetMarketingPermissionsImpl.java-arc  $
 * 
 *    Rev 1.7   Feb 05 2019 09:07:30   makuier
 * Support for hybrid version added. GDPR
 * 
 *    Rev 1.6   Nov 16 2015 10:09:56   makuier
 * Pass the action to be used for performance logging.
 * 
 *    Rev 1.5   Nov 02 2015 13:58:12   makuier
 * Put Mutex on marshaller.
 * 
 *    Rev 1.4   Sep 08 2015 16:16:18   makuier 
 * Consider permission definition with expire date  in the future active.
 * 
 *    Rev 1.3   Jun 30 2015 06:43:50   makuier
 * Performance refactor
 * 
 *    Rev 1.2   Jun 26 2015 14:41:44   makuier
 * If no perm/serv found in ref data filter the permission out
 * 
 *    Rev 1.1   May 26 2015 09:51:02   makuier
 * Functionality added.
 * 
 *    Rev 1.0   May 11 2015 13:34:26   makuier
 * Initial revision.
 */
package net.arcor.bks.httprequesthandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import net.arcor.bks.client.EaiClient;
import net.arcor.bks.common.BksDataException;
import net.arcor.bks.common.BksHelper;
import net.arcor.bks.common.BksHttpServiceHandler;
import net.arcor.bks.common.BksRefDataCacheHandler;
import net.arcor.bks.db.GetMarketingPermissionsDAO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.time.TimePoint;

import de.vodafone.esb.service.eai.customer.customerinteraction_002.GetMarketingPermissionsRequest;
import de.vodafone.esb.service.eai.customer.customerinteraction_002.GetMarketingPermissionsResponse;
import de.vodafone.esb.service.eai.customer.customerinteraction_002.GetMarketingPermissionsResponse.PermissionBEWReferenceDetailsList;
import de.vodafone.esb.service.eai.customer.customerinteraction_002.GetMarketingPermissionsResponse.PermissionBEWReferenceDetailsList.PermissionBEWReferenceDetails;
import de.vodafone.esb.service.eai.customer.customerinteraction_002.GetMarketingPermissionsResponse.PermissionDetailsList;
import de.vodafone.esb.service.eai.customer.customerinteraction_002.GetMarketingPermissionsResponse.PermissionDetailsList.PermissionDetails;
import de.vodafone.esb.service.eai.customer.customerinteraction_002.GetMarketingPermissionsResponse.PermissionReferenceDetailsList;
import de.vodafone.esb.service.eai.customer.customerinteraction_002.GetMarketingPermissionsResponse.PermissionReferenceDetailsList.PermissionReferenceDetails;

/**
 * @author makuier
 *
 */
public class GetMarketingPermissionsImpl implements BksHttpServiceHandler {

    /** The logger. */
    private final static Log log = LogFactory.getLog(GetMarketingPermissionsImpl.class);
    
    private final static String context = "de.vodafone.esb.service.eai.customer.customerinteraction_002";
    private final static String schemaName = "schema/CustomerInteraction-002.xsd";
    private final static String soapAction = "/CustomerInteraction002/GetMarketingPermission";
	private static Marshaller marshaller = null;
	private static Unmarshaller unmarshaller = null; 
	/**
	 * The operation "GetMarketingPermissions" of the service "GetMarketingPermissions".
	 */
    public String execute(SOAPMessage requestXml,String action) throws Exception{
    	BksHelper theHelper = new BksHelper();
    	String outputXml=null;
    	if (marshaller==null || unmarshaller==null){
    		createMarshaller();
    	}
    	GetMarketingPermissionsDAO dao = (GetMarketingPermissionsDAO) EaiClient.getBksDataAccessObject("onl", "GetMarketingPermissionsDAO");
    	long startTime = System.currentTimeMillis();
    	try {
    		GetMarketingPermissionsRequest request = deserialize(requestXml);
    		GetMarketingPermissionsResponse output = getPermissionData(dao,request,theHelper);
    		outputXml=serialize(output);
    		long endTime = System.currentTimeMillis();
    		log.info("Duration: "+(endTime-startTime));
    	} finally {
    		try {
    			dao.closeConnection();
    		} catch (Exception e) {
    		}
    	}

    	return outputXml;
    }
	/**
	 * @throws JAXBException
	 * @throws SAXException
	 */
	private void createMarshaller() throws JAXBException, SAXException {
		JAXBContext jbContext = JAXBContext.newInstance(context);
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		ClassLoader b = GetMarketingPermissionsImpl.class.getClassLoader();
		URL r = b.getResource(schemaName);
 		Schema schema = schemaFactory.newSchema(r);
	    marshaller = jbContext.createMarshaller();
        marshaller.setSchema(schema); 
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        unmarshaller = jbContext.createUnmarshaller();
        unmarshaller.setSchema(schema); 
	}
	private String serialize(GetMarketingPermissionsResponse output) throws SOAPException, JAXBException, IOException {
		SOAPMessage mes = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createMessage();
		SOAPBody body = mes.getSOAPBody();
		synchronized (marshaller) {
			marshaller.marshal(output, body);
		}
		mes.saveChanges();
        mes.setProperty(SOAPMessage.WRITE_XML_DECLARATION, "true");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		mes.writeTo(out);
		return new String(out.toByteArray(),"UTF-8");
	}
	private GetMarketingPermissionsRequest deserialize(SOAPMessage mes) throws IOException, SOAPException, JAXBException {
        SOAPBody b = mes.getSOAPBody();
        Document rawXml = b.extractContentAsDocument();
		GetMarketingPermissionsRequest req = null;
		synchronized (unmarshaller) {
			req = (GetMarketingPermissionsRequest) unmarshaller.unmarshal(rawXml);
		}
		return req;
	}
	private GetMarketingPermissionsResponse getPermissionData(
			GetMarketingPermissionsDAO dao,
			GetMarketingPermissionsRequest request, BksHelper theHelper)throws Exception{
		GetMarketingPermissionsResponse output = new GetMarketingPermissionsResponse();
		ArrayList<HashMap<String, Object>> permList = getPermissionDateInfo(dao,request,output);
		HashMap<String,BigDecimal> lastCustBewVersion=new HashMap<String, BigDecimal>();
		lastCustBewVersion.put("DEV", new BigDecimal(1));
		lastCustBewVersion.put("ADV", new BigDecimal(1));
		if (permList==null || permList.size()==0)
			populateLegacyCustPerms(dao,request,output);
		else {
			populateCustomerPermissionDetails(dao,request,output,permList,lastCustBewVersion);
		}

		populatePermissionDefinitions(request,output,lastCustBewVersion);
		populatePermissionBews(request,output,lastCustBewVersion);
		return output;
	}
    private void populateLegacyCustPerms(GetMarketingPermissionsDAO dao,
			GetMarketingPermissionsRequest request,
			GetMarketingPermissionsResponse output) throws Exception {
		PermissionDetailsList pdl = new PermissionDetailsList();
		output.setPermissionDetailsList(pdl);
		String customerNumber=request.getCustomerNumber();
		String role = BksRefDataCacheHandler.getSourceSysRefDataMap().get(request.getSourceSystemId());
		if (role==null){
			throw new BksDataException("BKS0070","The source sytem is not in the ref data. "+request.getSourceSystemId());
		}
		String fEndGr=request.getFrontendGroup();
		if (request.getReturnPermissionIndicator()==null || 
				!request.getReturnPermissionIndicator().equals("C") && 
				!request.getReturnPermissionIndicator().equals("H"))
			return;

		ArrayList<HashMap<String, Object>> permLeg = dao.getCustLegacyPermissions(customerNumber);
		if (permLeg==null||permLeg.size()==0){
			throw new BksDataException("BKS0000","No customer found "+customerNumber);
		} else {
			HashMap<String, Object> custPerm = permLeg.get(0);
			Timestamp dbDate = (Timestamp)custPerm.get("MARKETING_AUTHORIZATION_DATE");
			if(dbDate==null)
				dbDate = (Timestamp)custPerm.get("EFFECTIVE_DATE");
			TimePoint effDate=TimePoint.from(dbDate);
			Set<String> refkeys = BksHelper.getPermnamemap().keySet();
			Iterator<String> refkeyiter = refkeys.iterator();
			while (refkeyiter.hasNext()){
				String permServId = refkeyiter.next();
				String[] permSrv = permServId.split(";");
				if(!isValidForRoleAndFrontGrp(role,fEndGr,permSrv[0],permSrv[1]))
					continue;
				String value=(String) custPerm.get(BksHelper.getPermnamemap().get(permServId));
				if (value==null)
					value="N";
				insertNewPermDetailInList(pdl,permSrv[0],permSrv[1],new BigDecimal(1),effDate,value,"CCB.CCM");
			}
//			insertNewPermDetailInList(pdl,"BEW","NONE",new BigDecimal(1),effDate,null,"CCB.CCM");
		}
	}
	private void insertNewPermDetailInList(PermissionDetailsList pdl, String permId,String srvID,
			BigDecimal bewVersion,  TimePoint effDate, String value, String sourceSys) {
		PermissionDetails pd=new PermissionDetails();
		pd.setPermissionId(permId);
		pd.setServiceId(srvID);
		pd.setBewVersionNumber(bewVersion);
		pd.setSourceSystemId(sourceSys);
		pd.setEffectiveTime(effDate);
		pd.setPermissionValue(value);
		pdl.getPermissionDetails().add(pd);	
	}
	private boolean isValidForRoleAndFrontGrp(String role, String fEndGr,
			String prmId, String srvId) {
		BigDecimal lastRefBewVersion = BksRefDataCacheHandler.getLastDefVersion();
		ArrayList<HashMap<String, Object>> lastDefs = BksRefDataCacheHandler.getPermDef().get(lastRefBewVersion);
		Date current = new Date();
		for (int j = 0; j < lastDefs.size(); j++) {
			HashMap<String, Object> def = lastDefs.get(j);
			Date expireDate=(Date) def.get("EXPIRATION_DATE");
			if (prmId.equals(def.get("PRM_ID")) && srvId.equals(def.get("SRV_ID"))
	                && (expireDate==null || expireDate.getTime()>current.getTime())){				
				String getRoles = (String)def.get("GET_ROLE_LIST");
				if (fEndGr!=null && !fEndGr.equals(def.get("FRONTEND_GRP")))
					return false;
				if (getRoles!=null && !getRoles.contains(role))
					return false;
				return true;
			}
		}
		return false;
	}
	private ArrayList<HashMap<String,Object>> getPermissionDateInfo(
			GetMarketingPermissionsDAO dao,
			GetMarketingPermissionsRequest request,
			GetMarketingPermissionsResponse output) throws Exception {
		String customerNumber=request.getCustomerNumber();
		Date today = new Date();
		ArrayList<HashMap<String, Object>> perm = dao.getPermissionStatus(customerNumber);
		if (perm==null || perm.size()==0)
			return null;
		if (request.getCheckInitialStatusIndicator() != null &&
			request.getCheckInitialStatusIndicator().equals("Y")) {
			long sixWeeksAgo = today.getTime() - (long) 42 * 24L * 60L * 60L * 1000L;
			if (perm.size() > 1
					|| ((Date) perm.get(0).get("EFFECTIVE_DATE")).getTime() < sixWeeksAgo)
				output.setPermissionStatus("C");
			else
				output.setPermissionStatus("I");
		}
		return perm;
	}
	private void populatePermissionBews(GetMarketingPermissionsRequest request,
			GetMarketingPermissionsResponse output, HashMap<String, BigDecimal> lastCustBewVersion) {
		BigDecimal lastRefBewVersion = BksRefDataCacheHandler.getLastDefVersion();
		PermissionBEWReferenceDetailsList bwl = new PermissionBEWReferenceDetailsList();
		output.setPermissionBEWReferenceDetailsList(bwl );
		if(request.getReferenceDataIndicator().equals("A")){
			Set<BigDecimal> refkeys = BksRefDataCacheHandler.getPermBew().keySet();
			Iterator<BigDecimal> refkeyiter = refkeys.iterator();
			while (refkeyiter.hasNext()){
				BigDecimal vers = refkeyiter.next();
				ArrayList<HashMap<String, Object>> bewForVersion = BksRefDataCacheHandler.getPermBew().get(vers);
				populateBewList(bewForVersion,bwl,null);
			}
		} else if(request.getReferenceDataIndicator().equals("L")){
			ArrayList<HashMap<String, Object>> bewForVersion = BksRefDataCacheHandler.getPermBew().get(lastRefBewVersion);
			populateBewList(bewForVersion,bwl,null);
		} else if(request.getReferenceDataIndicator().equals("2")){
			ArrayList<HashMap<String, Object>> bewForVersion = BksRefDataCacheHandler.getPermBew().get(lastRefBewVersion);
			populateBewList(bewForVersion,bwl,null);
			for (String prmId : lastCustBewVersion.keySet()) {
				if(!lastRefBewVersion.equals(lastCustBewVersion.get(prmId))){
					bewForVersion = BksRefDataCacheHandler.getPermBew().get(lastCustBewVersion.get(prmId));
					populateBewList(bewForVersion,bwl,prmId);
				}
			}
		}
	}
	private void populateBewList(
			ArrayList<HashMap<String, Object>> bewForVersion,
			PermissionBEWReferenceDetailsList bwl, String prmId) {
		for (int j = 0; j < bewForVersion.size(); j++) {
			HashMap<String, Object> refmap = bewForVersion.get(j);
			if (prmId!=null && !prmId.equals((String) refmap.get("PRM_ID")))
				continue;
			PermissionBEWReferenceDetails bw = new PermissionBEWReferenceDetails();
			bw.setPermissionId((String) refmap.get("PRM_ID"));
			bw.setServiceId((String) refmap.get("SRV_ID"));
			bw.setBewVersionNumber((BigDecimal) refmap.get("BEW_VERSION_NR"));
			String dateAsStr=((Date)refmap.get("EFFECTIVE_DATE")).toString();
			bw.setEffectiveDate(CalendarDate.from(dateAsStr,"yyyy-MM-dd"));
			bw.setBewText((String) refmap.get("BEW_TEXT"));
			if (refmap.get("EXPIRATION_DATE")!=null) {
				dateAsStr = ((Date) refmap.get("EXPIRATION_DATE")).toString();
				bw.setExpirationDate(CalendarDate.from(dateAsStr, "yyyy-MM-dd"));
			}
			bwl.getPermissionBEWReferenceDetails().add(bw);
		}
		
	}
	private void populatePermissionDefinitions(
			GetMarketingPermissionsRequest request,
			GetMarketingPermissionsResponse output, HashMap<String, BigDecimal> lastCustBewVersion) {
		BigDecimal lastRefBewVersion = BksRefDataCacheHandler.getLastDefVersion();
		PermissionReferenceDetailsList prl = new PermissionReferenceDetailsList();
		output.setPermissionReferenceDetailsList(prl);
		if(request.getReferenceDataIndicator().equals("A")){
			Set<BigDecimal> refkeys = BksRefDataCacheHandler.getPermDef().keySet();
			Iterator<BigDecimal> refkeyiter = refkeys.iterator();
			while (refkeyiter.hasNext()){
				BigDecimal vers = refkeyiter.next();
				ArrayList<HashMap<String, Object>> defForVersion = BksRefDataCacheHandler.getPermDef().get(vers);
				populateRefList(defForVersion,prl,null);
			}
		} else if(request.getReferenceDataIndicator().equals("L")){
			ArrayList<HashMap<String, Object>> defForVersion = BksRefDataCacheHandler.getPermDef().get(lastRefBewVersion);
			populateRefList(defForVersion,prl,null);
		} else if(request.getReferenceDataIndicator().equals("2")){
			ArrayList<HashMap<String, Object>> defForVersion = BksRefDataCacheHandler.getPermDef().get(lastRefBewVersion);
			populateRefList(defForVersion,prl,null);
			for (String prmId : lastCustBewVersion.keySet()) {
				if (!lastRefBewVersion.equals(lastCustBewVersion.get(prmId))) {
					defForVersion = BksRefDataCacheHandler.getPermDef().get(lastCustBewVersion.get(prmId));
					populateRefList(defForVersion, prl,prmId);
				}
			}
		}
	}
	private void populateRefList(
			ArrayList<HashMap<String, Object>> defForVersion,
			PermissionReferenceDetailsList prl, String prmId) {
		for (int j = 0; j < defForVersion.size(); j++) {
			HashMap<String, Object> refmap = defForVersion.get(j);
			if (prmId!=null && !prmId.equals((String) refmap.get("PRM_ID")))
				continue;
			PermissionReferenceDetails pr = new PermissionReferenceDetails();
			pr.setPermissionId((String) refmap.get("PRM_ID"));
			pr.setServiceId((String) refmap.get("SRV_ID"));
			pr.setBewVersionNumber((BigDecimal) refmap.get("BEW_VERSION_NR"));
			String dateAsStr=((Date)refmap.get("EFFECTIVE_DATE")).toString();
			pr.setEffectiveDate(CalendarDate.from(dateAsStr,"yyyy-MM-dd"));
			pr.setPermissionPreferenceType((String) refmap.get("PERM_PREF_TYPE"));
			pr.setSetRoleList((String) refmap.get("SET_ROLE_LIST"));
			pr.setGetRoleList((String) refmap.get("GET_ROLE_LIST"));
			pr.setDefaultValue((String) refmap.get("DEFAULT_VALUE"));
			pr.setPositiveValueList((String) refmap.get("POSITIVE_VAL_LIST"));
			pr.setNegativeValueList((String) refmap.get("NEGATIVE_VAL_LIST"));
			pr.setMmcRelevantIndicator((String) refmap.get("MMC_RELEVANT_IND"));
			pr.setMmoRelevantIndicator((String) refmap.get("MMO_RELEVANT_IND"));
			pr.setGuiObjectType((String) refmap.get("GUI_OBJECT_TYPE"));
			pr.setFrontendGrp((String) refmap.get("FRONTEND_GRP"));
			pr.setGuiOrderNumber((BigDecimal) refmap.get("GUI_ORDER_NR"));
			pr.setSpecialPurposeIndicator((String) refmap.get("SPEC_PURPOSE_IND"));
			pr.setShortDescriptionDE((String) refmap.get("SHORT_DESCRIPTION_DE"));
			pr.setShortDescriptionEN((String) refmap.get("SHORT_DESCRIPTION_EN"));
			pr.setLongDescriptionDE((String) refmap.get("LONG_DESCRIPTION_DE"));
			pr.setLongDescriptionEN((String) refmap.get("LONG_DESCRIPTION_EN"));
			if (refmap.get("EXPIRATION_DATE")!=null) {
				dateAsStr = ((Date) refmap.get("EXPIRATION_DATE")).toString();
				pr.setExpirationDate(CalendarDate.from(dateAsStr, "yyyy-MM-dd"));
			}
			prl.getPermissionReferenceDetails().add(pr );
		}
		
	}
	private void populateCustomerPermissionDetails(GetMarketingPermissionsDAO dao,
			GetMarketingPermissionsRequest request, GetMarketingPermissionsResponse output, ArrayList<HashMap<String,Object>> permList, HashMap<String, BigDecimal> lastCustBewVersion) throws Exception {
		PermissionDetailsList pdl = new PermissionDetailsList();
		output.setPermissionDetailsList(pdl);
		String customerNumber=request.getCustomerNumber();
		String fEndGr=request.getFrontendGroup();
		String role = BksRefDataCacheHandler.getSourceSysRefDataMap().get(request.getSourceSystemId());
		if (role==null){
			throw new BksDataException("BKS0070","The source sytem is not in the ref data. "+request.getSourceSystemId());
		}

		int historyCnt=-1;
		if (request.getReturnPermissionIndicator()!=null && request.getReturnPermissionIndicator().equals("C"))
			historyCnt=2;
		else if (request.getReturnPermissionIndicator()==null || !request.getReturnPermissionIndicator().equals("H"))
			return;
        populatePermisionList(dao,permList,pdl,customerNumber, fEndGr, role,historyCnt,lastCustBewVersion);
		ArrayList<HashMap<String, Object>> defPerms = dao
				.getDefaultPermissions(customerNumber, fEndGr, role);
		for (int i = 0; i < defPerms.size(); i++) {
			String permId = (String) defPerms.get(i).get("PERMISSION_ID");
			String srvId = (String) defPerms.get(i).get("SERVICE_ID");
			insertNewPermDetailInList(pdl, permId, srvId, null, TimePoint.from((Timestamp)defPerms.get(i).get("EFFECTIVE_DATE")),
					(String) defPerms.get(i).get("PERMISSION_VALUE"), null);
		}
	}
	private void populatePermisionList(GetMarketingPermissionsDAO dao, ArrayList<HashMap<String,Object>> permList, 
			PermissionDetailsList pdl, String customerNumber, String fEndGr, String role, int historyCnt, HashMap<String, BigDecimal> lastCustBewVersion) throws Exception {
		HashMap<String,TimePoint> expDateMap=new HashMap<String, TimePoint>();
		HashMap<String,Integer> histCntMap=new HashMap<String, Integer>();
		
		for (int j = 0; j < permList.size(); j++) {
			BigDecimal bewVersion = (BigDecimal) permList.get(j).get("BEW_VERSION");
			String sourceSys = (String) permList.get(j).get("SOURCE_SYSTEM");
			TimePoint effdate = TimePoint.from((Timestamp)permList.get(j).get("EFFECTIVE_DATE"));
			ArrayList<HashMap<String, Object>> perms = dao
					.getCustomerPermissions(customerNumber, fEndGr, role, bewVersion,effdate);
			for (int i = 0; i < perms.size(); i++) {
				String permId = (String) perms.get(i).get("PERMISSION_ID");
				String srvId = (String) perms.get(i).get("SERVICE_ID");
				if (perms.get(i).get("BEW_VERSION") != null)
					bewVersion = (BigDecimal) perms.get(i).get("BEW_VERSION");
				if (lastCustBewVersion.get(permId).compareTo(bewVersion) < 0 )
					lastCustBewVersion.put(permId, bewVersion);
				if (historyCnt>0 && histCntMap.get(permId+";"+srvId)!=null && histCntMap.get(permId+";"+srvId) >= historyCnt)
					continue;
				TimePoint expireDate = expDateMap.get(permId+";"+srvId);
				insertNewPermDetailInList(pdl, permId, srvId, bewVersion, effdate, (String) perms.get(i).get("PERMISSION_VALUE"),sourceSys);				
				if (historyCnt>0){
					Integer currentCnt=histCntMap.get(permId+";"+srvId);
					if (currentCnt==null)
						histCntMap.put(permId+";"+srvId,new Integer(1));
					else
						histCntMap.put(permId+";"+srvId,new Integer(currentCnt.intValue()+1));
				}
				expDateMap.put(permId+";"+srvId,effdate);
			}
		}
		populateGenericPerm(pdl, lastCustBewVersion);
	}
	private void populateGenericPerm(PermissionDetailsList pdl, HashMap<String, BigDecimal> lastCustBewVersion) {
		for (String permId : lastCustBewVersion.keySet()) {
			TimePoint effdate= null;
			String genValue = "Y";
			String sourceSys = null;
			List<PermissionDetails> list = pdl.getPermissionDetails();
			for (int i=0;i<list.size();i++){
				PermissionDetails detail = list.get(i);
				if (detail.getPermissionId().equals(permId)&&detail.getBewVersionNumber().equals(lastCustBewVersion.get(permId))){
					effdate= detail.getEffectiveTime();
					if (!isValuePositive(detail.getPermissionValue(),detail.getPermissionId(),detail.getServiceId(),lastCustBewVersion.get(permId)))
							genValue = "N";
					sourceSys = detail.getSourceSystemId();
				}
			}
			insertNewPermDetailInList(pdl, permId, "GENE", lastCustBewVersion.get(permId), effdate, genValue ,sourceSys);
		}
	}

	private boolean isValuePositive(String permissionValue,
			String prmId, String srvId, BigDecimal lastCustVersion) {
		ArrayList<HashMap<String, Object>> defForVersion = BksRefDataCacheHandler.getPermDef().get(lastCustVersion);
		for (int i=0;i<defForVersion.size();i++){
			HashMap<String, Object> defItem = defForVersion.get(i);
			if (prmId.equals((String) defItem.get("PRM_ID")) && srvId.equals((String) defItem.get("SRV_ID"))){
				String positiveValues = (String) defItem.get("POSITIVE_VAL_LIST");
				String[] positiveArray = positiveValues.split(",");
				if(Arrays.asList(positiveArray).contains(permissionValue))
					return true;
			}
		}
		return false;
	}
	public String getSoapaction() {
		return soapAction;
	}
}
