/*
    $Header:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/BaseServiceHandler.java-arc   1.11   Apr 24 2013 16:40:38   makuier  $

    $Log:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/BaseServiceHandler.java-arc  $
 * 
 *    Rev 1.11   Apr 24 2013 16:40:38   makuier
 * ClientSchemaVersion added
 * 
 *    Rev 1.10   Dec 09 2011 14:06:44   makuier
 * Unvalidated XML added.
 * 
 *    Rev 1.9   Oct 21 2011 15:58:24   makuier
 * originator added.
 * 
 *    Rev 1.8   Nov 09 2009 11:19:46   makuier
 * Adaption to new BksHelper.
 * 
 *    Rev 1.7   May 27 2009 16:06:24   makuier
 * MCF2 adaption
*/
package net.arcor.bks.requesthandler;


import net.arcor.bks.common.BksHelper;
import net.arcor.epsm_basetypes_001.InputData;
import net.arcor.epsm_basetypes_001.OutputData;
import net.arcor.mcf2.exception.base.MCFException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BaseServiceHandler extends Thread implements
		BksServiceHandlerInterface {
    public enum Status { SUCCESS, PRECONDITION_ERROR, FUNCTIONAL_ERROR }
	protected Status serviceStatus = Status.SUCCESS;
	protected String errorText = null;
	protected String errorCode = null;
	protected String unvalidatedXml = null;
	protected String customerNumber = null;
	protected String accessNumber = null;
	protected OutputData output = null;
	protected InputData input = null;
	protected BksHelper theHelper = null;
	protected final Log logger = LogFactory.getLog(getClass());
	protected String originator = null;
	protected String clientSchemaVersion = null;

	public String getClientSchemaVersion() {
		return clientSchemaVersion;
	}

	public void setClientSchemaVersion(String clientSchemaVersion) {
		this.clientSchemaVersion = clientSchemaVersion;
	}

	public String getUnvalidatedXml() {
		return unvalidatedXml;
	}

	public void setUnvalidatedXml(String unvalidatedXml) {
		this.unvalidatedXml = unvalidatedXml;
	}

	public String getOriginator() {
		return originator;
	}

	public void setOriginator(String originator) {
		this.originator = originator;
	}

	public BaseServiceHandler() {
		super();
		try {
			theHelper = new BksHelper();
		} catch (MCFException e) {
			//Ignore 
		}
	}

	public String execute() throws Exception {
		return null;
	}

	@Override
	public void run() {
		// Call the execute function and cache the result.
		try {
			execute();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	protected void populateBaseOutput() throws Exception{
		try {
			if (serviceStatus == Status.PRECONDITION_ERROR){
				output.setResult(false);
				output.setErrorCode(errorCode);
				output.setErrorText(errorText);
			} else
				output.setResult(true);
		} catch (Throwable e) {
			e.printStackTrace();
			throw new Exception(e);
		}
	}

	public InputData getInput() {
		return input;
	}

	public void setInput(InputData input) {
		this.input = input;
	}

	public String getCustomerNumber() {
		return customerNumber;
	}

	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}

	public OutputData getOutput() {
		return output;
	}

	public void setOutput(OutputData output) {
		this.output = output;
	}

	public Status getServiceStatus() {
		return serviceStatus;
	}

	public void setServiceStatus(Status serviceStatus) {
		this.serviceStatus = serviceStatus;
	}

	public String getErrorText() {
		return errorText;
	}

	public void setErrorText(String errorText) {
		this.errorText = errorText;
	}

	public String getAccessNumber() {
		return accessNumber;
	}

	public void setAccessNumber(String accessNumber) {
		this.accessNumber = accessNumber;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
}
