package net.arcor.bks.requesthandler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.arcor.bks.client.DatabaseClient;
import net.arcor.bks.client.DatabaseClientConfig;
import net.arcor.bks.db.GetFNPCustomerRecoDataDAO;
import net.arcor.mcf2.sender.MessageSender;

import org.springframework.beans.factory.annotation.Autowired;

import com.domainlanguage.time.CalendarDate;
import com.vodafone.mcf2.ws.model.impl.ServiceRequestSoap;

import de.vodafone.esb.schema.common.basetypes_esb_001.AppMonDetailsType;
import de.vodafone.esb.schema.common.sidcom_getcustomerinfo_001.CustomerInformation;
import de.vodafone.esb.service.sbus.callback.getfnpcustomerrecodatacallback_004.GetFNPCustomerRecoDataCallbackRequest;
import de.vodafone.esb.service.sbus.callback.getfnpcustomerrecodatacallback_004.TraceIdentifier;

public class GetFNPCustomerRecoDataCallbackImpl extends Thread {

	final static private String SID_VERSION = "1";

	@Autowired
	private MessageSender messageSender;

	List<String> serialNumberList;
	List<String> customerNumberList;
	CalendarDate startDate;
	CalendarDate endDate;
	String sbusCorrId;
	ArrayList<CustomerInformation> sid  = new ArrayList<CustomerInformation>();
	String boId;
	String bpId;
	String bpName;
	String initiator;

	public List<String> getSerialNumberList() {
		return serialNumberList;
	}
	public void setSerialNumberList(List<String> serialNumberList) {
		this.serialNumberList = serialNumberList;
	}
	public List<String> getCustomerNumberList() {
		return customerNumberList;
	}
	public void setCustomerNumberList(List<String> customerNumberList) {
		this.customerNumberList = customerNumberList;
	}
	public CalendarDate getStartDate() {
		return startDate;
	}
	public void setStartDate(CalendarDate startDate) {
		this.startDate = startDate;
	}
	public CalendarDate getEndDate() {
		return endDate;
	}
	public void setEndDate(CalendarDate endDate) {
		this.endDate = endDate;
	}
	public String getSbusCorrId() {
		return sbusCorrId;
	}
	public void setSbusCorrId(String sbusCorrId) {
		this.sbusCorrId = sbusCorrId;
	}
	public String getBoId() {
		return boId;
	}
	public void setBoId(String boId) {
		this.boId = boId;
	}
	public String getBpId() {
		return bpId;
	}
	public void setBpId(String bpId) {
		this.bpId = bpId;
	}
	public String getBpName() {
		return bpName;
	}
	public void setBpName(String bpName) {
		this.bpName = bpName;
	}
	public String getInitiator() {
		return initiator;
	}
	public void setInitiator(String initiator) {
		this.initiator = initiator;
	}
	@Override
	public void run() {
		int maxCustomersPerMsg = 500;
		try {
			maxCustomersPerMsg = Integer.parseInt(DatabaseClientConfig.getSetting("databaseclient.MaxNoOfProducts"));
		} catch (Exception e1) {
			// Ignore (Use Default)
		}
		GetFNPCustomerRecoDataDAO dao = null;
		try {
			dao = (GetFNPCustomerRecoDataDAO) DatabaseClient.getBksDataAccessObject(null, "GetFNPCustomerRecoDataDAO");
			if (serialNumberList == null || serialNumberList.size() == 0)
				processCustomerList(dao,maxCustomersPerMsg);
			else
				processBundleList(maxCustomersPerMsg);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (dao != null)
					dao.closeConnection();
			} catch (Exception e) {
			}
		}
	}
 
	private void launchCallbackMessage(
			GetFNPCustomerRecoDataCallbackRequest callbackRequest) {
		ServiceRequestSoap<GetFNPCustomerRecoDataCallbackRequest> serviceRequest =
			new ServiceRequestSoap<GetFNPCustomerRecoDataCallbackRequest>(callbackRequest,
					"classpath:schema/GetFNPCustomerRecoDataCallback-004.xsd",
			"de.vodafone.esb.service.sbus.callback.getfnpcustomerrecodatacallback_004");
		serviceRequest.setOperationName("/GetFNPCustomerRecoDataCallback-004/GetFNPCustomerRecoDataCallbackEAI");		
		serviceRequest.setSbusCorrelationID(sbusCorrId+"-"+callbackRequest.getTraceIdentifier().getCallbackMessageNumber());
		if (callbackRequest.getTraceIdentifier().isLastCallbackMessage())
			serviceRequest.setSbusCorrelationID(serviceRequest.getSbusCorrelationID()+"-LAST");
		AppMonDetailsType amdt = new AppMonDetailsType();
		amdt.setCallingApp("BKS");
		amdt.setBoId(boId);
		amdt.setBpId(bpId);
		amdt.setBpName(bpName);
		amdt.setInitiator(initiator);
		serviceRequest.setAppMonDetails(amdt);
		int timeToLive = 3600000;
		try {
			timeToLive = Integer.parseInt(DatabaseClientConfig.getSetting("servicebusclient.TimeToLive"));
		} catch (Exception e1) {
			// Ignore (Use Default)
		}		
		serviceRequest.setTimeToLive(timeToLive);
		messageSender.sendOnewaySoapRequest(serviceRequest);
	}

	private void processBundleList(int maxCustomersPerMsg) {
		int imsgCount = 1;
		int validCustCnt = 0;
		GetFNPCustomerData3Impl gcdImpl= new GetFNPCustomerData3Impl();
		for (int i=0;i<serialNumberList.size();i++){
			String bundleId = serialNumberList.get(i);
			try {
				CustomerInformation ci = gcdImpl.getCustomerInfoByBundle(bundleId,"RCO");
				sid.add(ci);
				validCustCnt++;
			} catch (Exception e) {
				// ignore the Customer
				continue;
			}
			if (validCustCnt%maxCustomersPerMsg == 0) {
				GetFNPCustomerRecoDataCallbackRequest callbackRequest =
					new GetFNPCustomerRecoDataCallbackRequest();
				TraceIdentifier ti = new TraceIdentifier();
				ti.setCallbackMessageNumber(BigDecimal.valueOf(imsgCount));
				if (i==serialNumberList.size()-1)
					ti.setLastCallbackMessage(true);
				else
					ti.setLastCallbackMessage(false);
				callbackRequest.setTraceIdentifier(ti);
				callbackRequest.setSidcomVersion(SID_VERSION);
				callbackRequest.getSid().addAll(sid);
				launchCallbackMessage(callbackRequest);
				sid.clear();
				imsgCount++;
			}
		}
		if (sid.size() >0 || (sid.size() == 0 && imsgCount == 1)){
			GetFNPCustomerRecoDataCallbackRequest callbackRequest =
				new GetFNPCustomerRecoDataCallbackRequest();
			TraceIdentifier ti = new TraceIdentifier();
			ti.setCallbackMessageNumber(BigDecimal.valueOf(imsgCount));
			ti.setLastCallbackMessage(true);
			callbackRequest.setTraceIdentifier(ti);
			callbackRequest.setSidcomVersion(SID_VERSION);
			callbackRequest.getSid().addAll(sid);
			launchCallbackMessage(callbackRequest);
		}
	}

	private void processCustomerList(GetFNPCustomerRecoDataDAO dao, int maxCustomersPerMsg) throws Exception {
		int imsgCount = 1;
		int validCustCnt = 0;
		ArrayList<String> customerNumberList = getCustomerNumbersList(dao);
		GetFNPCustomerData3Impl gcdImpl= new GetFNPCustomerData3Impl();
		for (int i=0;i<customerNumberList.size();i++){
			String customerNumber = customerNumberList.get(i);
			try {
				CustomerInformation ci = gcdImpl.getCustomerInfo(customerNumber,"RCO");
				sid.add(ci);
				validCustCnt++;
			} catch (Exception e) {
				// ignore the Customer
				continue;
			}
			if (validCustCnt%maxCustomersPerMsg == 0) {
				GetFNPCustomerRecoDataCallbackRequest callbackRequest =
					new GetFNPCustomerRecoDataCallbackRequest();
				TraceIdentifier ti = new TraceIdentifier();
				ti.setCallbackMessageNumber(BigDecimal.valueOf(imsgCount));
				if (i==customerNumberList.size()-1)
					ti.setLastCallbackMessage(true);
				else
					ti.setLastCallbackMessage(false);
				callbackRequest.setTraceIdentifier(ti);
				callbackRequest.setSidcomVersion(SID_VERSION);
				callbackRequest.getSid().addAll(sid);
				launchCallbackMessage(callbackRequest);
				sid.clear();
				imsgCount++;
			}
		}
		if (sid.size() >0 || (sid.size() == 0 && imsgCount == 1)){
			GetFNPCustomerRecoDataCallbackRequest callbackRequest =
				new GetFNPCustomerRecoDataCallbackRequest();
			TraceIdentifier ti = new TraceIdentifier();
			ti.setCallbackMessageNumber(BigDecimal.valueOf(imsgCount));
			ti.setLastCallbackMessage(true);
			callbackRequest.setTraceIdentifier(ti);
			callbackRequest.setSidcomVersion(SID_VERSION);
			callbackRequest.getSid().addAll(sid);
			launchCallbackMessage(callbackRequest);
		}

	}
	private ArrayList<String> getCustomerNumbersList(
			GetFNPCustomerRecoDataDAO dao) throws Exception{
		ArrayList<String> custNoList = new ArrayList<String>();
		if (customerNumberList.size() > 0) {
			custNoList.addAll(customerNumberList);
		} else {
			ArrayList<HashMap<String, Object>> custs = dao.getCustomersForDateInterval(startDate,endDate.nextDay());
			for (int i = 0; i < custs.size(); i++) {
				custNoList.add((String)custs.get(i).get("CUSTOMER_NUMBER"));
			}
		}
		return custNoList;
	}
}
