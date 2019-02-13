/*
    $Header:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/GetFNPCustomerRecoDataImpl.java-arc   1.5   Nov 08 2011 13:10:06   makuier  $

    $Log:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/GetFNPCustomerRecoDataImpl.java-arc  $
 * 
 *    Rev 1.5   Nov 08 2011 13:10:06   makuier
 * Refactorto support 2 endpoints
 * 
 *    Rev 1.4   Oct 28 2011 14:14:16   makuier
 * Use the new end point for Reco service
 * 
 *    Rev 1.3   Jul 07 2011 11:57:56   makuier
 * Perform initial selects.
 * 
 *    Rev 1.2   May 04 2011 15:08:18   makuier
 * populate Mon details.
 * 
 *    Rev 1.1   Apr 29 2011 16:05:04   makuier
 * Corrected the date boundries
 * 
 *    Rev 1.0   Apr 21 2011 14:21:52   makuier
 * Initial revision.
 * 
*/
package net.arcor.bks.requesthandler;

import javax.jms.TextMessage;

import net.arcor.bks.client.DatabaseClient;
import net.arcor.bks.client.DatabaseClientConfig;
import net.arcor.bks.common.BksHelper;
import net.arcor.bks.common.BksRefDataCacheHandler;
import net.arcor.mcf2.model.ServiceObjectEndpoint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.vodafone.mcf2.ws.exception.FunctionalException;
import com.vodafone.mcf2.ws.exception.FunctionalRuntimeException;
import com.vodafone.mcf2.ws.exception.TechnicalException;
import com.vodafone.mcf2.ws.model.impl.ServiceResponseSoap;
import com.vodafone.mcf2.ws.service.SoapEndpoint;
import com.vodafone.mcf2.ws.service.SoapOperation;

import de.vodafone.esb.schema.common.commontypes_esb_001.ErrorType;
import de.vodafone.esb.service.sbus.bksreco.bksreco_001.GetFNPCustomerRecoDataRequest;
import de.vodafone.esb.service.sbus.bksreco.bksreco_001.GetFNPCustomerRecoDataResponse;

@SoapEndpoint(soapAction = "/BKSReco-001/GetFNPCustomerRecoData", context = "de.vodafone.esb.service.sbus.bksreco.bksreco_001", schema = "classpath:schema/BKSReco-001.xsd")
public class GetFNPCustomerRecoDataImpl implements SoapOperation<GetFNPCustomerRecoDataRequest, GetFNPCustomerRecoDataResponse> {

    /** The logger. */
    private final static Log log = LogFactory.getLog(GetFNPCustomerRecoDataImpl.class);
	BksHelper theHelper = null;
	/**
	 * The operation "GetFNPCustomerRecoData" of the service "GetFNPCustomerRecoData".
	 */
	public ServiceResponseSoap<GetFNPCustomerRecoDataResponse> invoke(ServiceObjectEndpoint<GetFNPCustomerRecoDataRequest> serviceObject) 
			throws TechnicalException,FunctionalException,FunctionalRuntimeException {
		int tries =0;
		int maxTries = 10;
		theHelper = new BksHelper();
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
		GetFNPCustomerRecoDataRequest request = serviceObject.getPayload();
		GetFNPCustomerRecoDataResponse output = new GetFNPCustomerRecoDataResponse();
		try {
			GetFNPCustomerRecoDataCallbackImpl callbackImpl = 
				(GetFNPCustomerRecoDataCallbackImpl)DatabaseClient.getAc().getBean("soapGetFNPCustomerRecoDataCallback");
			callbackImpl.setSerialNumberList(request.getSerialNumber());
			callbackImpl.setCustomerNumberList(request.getCustomerNumber());
			if (request.getQueryDateInterval()!=null) {
				callbackImpl.setStartDate(request.getQueryDateInterval().getStartDate());
				callbackImpl.setEndDate(request.getQueryDateInterval().getEndDate());
			}
			String sbusCorrId = serviceObject.getSbusCorrelationID();
			TextMessage textMessage = (TextMessage) serviceObject.getIncomingMessage();
			String boid = textMessage.getStringProperty("EAI_BOID");
			String bpid = textMessage.getStringProperty("EAI_BPID");
			String bpname = textMessage.getStringProperty("EAI_BPName");
			String intiator = textMessage.getStringProperty("EAI_Initiator");
			callbackImpl.setSbusCorrId(sbusCorrId);
			callbackImpl.setBoId(boid);
			callbackImpl.setBpId(bpid);
			callbackImpl.setBpName(bpname);
			callbackImpl.setInitiator(intiator);
			callbackImpl.start();
			output.setStatus("SUCCESS");
		} catch (Exception e) {
			log.error(e.getMessage());
			output.setStatus("FAILURE");
			ErrorType et = new ErrorType();
			et.setErrorDescription(e.getMessage());
			output.setErrorDetails(et);
		}

		return new ServiceResponseSoap<GetFNPCustomerRecoDataResponse>(output);
	}

}
