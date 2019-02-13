package net.arcor.bks.requesthandler;

import java.util.ArrayList;
import java.util.HashMap;

import net.arcor.bks.client.DatabaseClient;
import net.arcor.bks.db.GetCcbIdDAO;
import net.arcor.epsm_basetypes_001.InputData;
import de.arcor.aaw.kernAAW.bks.services.GetCcbIdInput;
import de.arcor.aaw.kernAAW.bks.services.GetCcbIdOutput;

public class GetCcbIdHandler extends BaseServiceHandler {

	public GetCcbIdHandler() {
		super();
	}

	public String execute() throws Exception {
		String returnXml = null;
		GetCcbIdDAO dao = 
			(GetCcbIdDAO)DatabaseClient.getBksDataAccessObject(null,"GetCcbIdDAO");
		try {
			String [] outData = retreieveCcbIds(dao,(GetCcbIdInput)input);
			returnXml = populateMasterData(outData);
		} catch (Exception e) {
			logger.error(e.getMessage());
			serviceStatus = Status.PRECONDITION_ERROR;
			errorCode = "BKS0008";
			errorText = e.getMessage();
			returnXml = populateMasterData(null);
		}
		finally {
			dao.closeConnection();
		}
		return returnXml;
	}

	private String populateMasterData(String[] outData) throws Exception {
		output = new GetCcbIdOutput();
		String returnXml = null;
		if (serviceStatus == Status.SUCCESS && outData != null){
			output.setResult(true);
			((GetCcbIdOutput)output).setCustomerNumber(outData[0]);
			((GetCcbIdOutput)output).setServiceSubscriptionId(outData[1]);
			((GetCcbIdOutput)output).setBundleId(outData[2]);
			((GetCcbIdOutput)output).setAccountNumber(outData[3]);
		} else {
			output.setResult(false);
			output.setErrorCode(errorCode);
			output.setErrorText(errorText);
		}
		try {
			returnXml = theHelper.serializeForMcf(output, customerNumber,accessNumber,"de.arcor.aaw.kernAAW.bks.services" ,"GetCcbId");
			if (serviceStatus == Status.SUCCESS && accessNumber != null)
				theHelper.loadCache(accessNumber+";GetCcbId",returnXml);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return returnXml;
	}

	private String[] retreieveCcbIds(GetCcbIdDAO dao, GetCcbIdInput input) throws Exception {
		String countryCode = input.getRufnummer().getLaenderkennzeichen();
		String cityCode = input.getRufnummer().getVorwahl();
		String localNumber = input.getRufnummer().getRufnummer();
		ArrayList<HashMap<String, Object>> theList = dao.getExactAccessNumber(countryCode,cityCode,localNumber);
		if ((theList != null) && (theList.size() != 0)){
			customerNumber = theList.get(0).get("CUSTOMER_NUMBER").toString();
			String serviceSubscrId = theList.get(0).get("SERVICE_SUBSCRIPTION_ID").toString();
			String bundleId = theList.get(0).get("BUNDLE_ID").toString();
			String accnum = theList.get(0).get("ACCOUNT_NUMBER").toString();
			return new String[] {customerNumber,serviceSubscrId,bundleId,accnum};
		}
		serviceStatus = Status.FUNCTIONAL_ERROR;
		errorText = "No Access number found";
		errorCode = "BKS0000";
		return null;

	}

	public void setInput(InputData input) {
		super.setInput(input);
		String countryCode = ((GetCcbIdInput)input).getRufnummer().getLaenderkennzeichen();
		String cityCode = ((GetCcbIdInput)input).getRufnummer().getVorwahl();
		String localNumber = ((GetCcbIdInput)input).getRufnummer().getRufnummer();
		accessNumber = countryCode.substring(2)+cityCode.substring(1)+localNumber;
	}
}
