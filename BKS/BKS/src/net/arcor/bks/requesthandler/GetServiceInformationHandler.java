package net.arcor.bks.requesthandler;

import java.util.ArrayList;
import java.util.HashMap;

import net.arcor.bks.client.DatabaseClient;
import net.arcor.bks.db.GetServiceInformationDAO;
import net.arcor.epsm_basetypes_001.InputData;
import de.arcor.aaw.kernAAW.bks.services.GetServiceInformationInput;
import de.arcor.aaw.kernAAW.bks.services.GetServiceInformationOutput;

public class GetServiceInformationHandler extends BaseServiceHandler {
	
	public GetServiceInformationHandler() {
		super();
	}

	public String execute() throws Exception {
		String returnXml = null;
		GetServiceInformationDAO dao = 
			(GetServiceInformationDAO)DatabaseClient.getBksDataAccessObject(null,"GetServiceInformationDAO");

		try {
			ArrayList<HashMap<String,Object>> ticketData = getServiceTicket(dao,(GetServiceInformationInput)input);
			returnXml = populateOutputObject(ticketData);
		} catch (Exception e) {
			logger.error(e.getMessage());
			serviceStatus = Status.PRECONDITION_ERROR;
			errorCode = "BKS0008";
			errorText = e.getMessage();
			returnXml = populateOutputObject(null);
		}
		finally {
			dao.closeConnection();
		}
		return returnXml;
	}

	protected String populateOutputObject(ArrayList<HashMap<String,Object>> ticketData) throws Exception {
		String returnXml = null;
		output = new GetServiceInformationOutput();
		if (serviceStatus != Status.SUCCESS){
			output.setResult(false);
			output.setErrorCode(errorCode);
			output.setErrorText(errorText);
		}else {
			output.setResult(true);

            for (HashMap<String, Object> item : ticketData)
			  ((GetServiceInformationOutput)output).getServiceTicketPositionIds().
			         add(item.get("SERVICE_TICKET_POSITION_ID").toString());
		}
		try {
			returnXml = theHelper.serializeForMcf(output, customerNumber,accessNumber,"de.arcor.aaw.kernAAW.bks.services" ,"GetServiceInformation");
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return returnXml;
	}

	private ArrayList<HashMap<String, Object>> getServiceTicket(GetServiceInformationDAO dao,
			                               GetServiceInformationInput input) throws Exception {
		ArrayList<HashMap<String, Object>> list = 
			dao.getServiceTicket(input.getServiceSubscriptionId(), input.getUsageMode(),input.getOrderId());
		if ((list == null) || (list.size() == 0)){
			serviceStatus = Status.FUNCTIONAL_ERROR;
			errorText = "No contract has been found for service subscription "+input.getServiceSubscriptionId();
			errorCode = "BKS0101";
			return null;
		}

 		return list;
	}

	public void setInput(InputData input) {
		super.setInput(input);
	}
}
