package net.arcor.bks.db.ibatis;

import java.sql.Timestamp;


public class BksTransactionLog {
	String correlationId = null;
	String serviceName = null;
	String status = null;
	String customerNumber = null;
	String accessNumber = null;
	Timestamp startTime = null;
	Timestamp endTime = null;
	String cacheHit = null;
	String error = null;

	public BksTransactionLog(String correlationId, String serviceName,
			String status, String customerNumber, String accessNumber,
			Timestamp startTime, Timestamp endTime, String cacheHit, String error) {
		this.correlationId = correlationId;
		this.serviceName = serviceName;
		this.status = status;
		this.customerNumber = customerNumber;
		this.accessNumber = accessNumber;
		this.startTime = startTime;
		this.endTime = endTime;
		this.cacheHit = cacheHit;
		this.error = error;
	}

	public String getCorrelationId() {
		return correlationId;
	}

	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCustomerNumber() {
		return customerNumber;
	}

	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}

	public String getAccessNumber() {
		return accessNumber;
	}

	public void setAccessNumber(String accessNumber) {
		this.accessNumber = accessNumber;
	}

	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public Timestamp getEndTime() {
		return endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	public String getCacheHit() {
		return cacheHit;
	}

	public void setCacheHit(String cacheHit) {
		this.cacheHit = cacheHit;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

}
