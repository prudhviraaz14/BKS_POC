/*
    $Header:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/GetCodbPrerequisiteDataHandler.java-arc   1.10   Jan 28 2010 16:45:18   makuier  $

    $Log:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/GetCodbPrerequisiteDataHandler.java-arc  $
 * 
 *    Rev 1.10   Jan 28 2010 16:45:18   makuier
 * Cache the exception and return an error XML.
 * 
 *    Rev 1.9   Oct 16 2009 14:25:48   makuier
 * Account number added.
 * 
 *    Rev 1.8   Jun 30 2009 10:51:32   makuier
 * Service code added to the output
 * 
 *    Rev 1.7   May 27 2009 16:06:22   makuier
 * MCF2 adaption
 * 
 *    Rev 1.5   May 26 2009 16:31:02   makuier
 * Added support for mobile and range.
*/
package net.arcor.bks.requesthandler;

import java.util.ArrayList;
import java.util.HashMap;

import net.arcor.bks.client.DatabaseClient;
import net.arcor.bks.db.GetCodbPrerequisiteDataDAO;
import net.arcor.epsm_basetypes_001.InputData;
import de.arcor.aaw.kernAAW.bks.services.AccessNumberType;
import de.arcor.aaw.kernAAW.bks.services.GetCodbPrerequisiteDataInput;
import de.arcor.aaw.kernAAW.bks.services.GetCodbPrerequisiteDataOutput;

public class GetCodbPrerequisiteDataHandler extends BaseServiceHandler {
	
	public GetCodbPrerequisiteDataHandler() {
		super();
	}

	public String execute() throws Exception {
		String returnXml = null;
		GetCodbPrerequisiteDataDAO dao = 
			(GetCodbPrerequisiteDataDAO)DatabaseClient.getBksDataAccessObject(null,"GetCodbPrerequisiteDataDAO");

		try {
			HashMap<String,Object> contractData = getContractData(dao,(GetCodbPrerequisiteDataInput)input);
			ArrayList<String> tariffOptions = getTariffOptions(dao,(GetCodbPrerequisiteDataInput)input);
			ArrayList<AccessNumberType> accessNumbers = getAccessNumbers(dao,(GetCodbPrerequisiteDataInput)input);
			returnXml = populateOutputObject(contractData,tariffOptions,accessNumbers);
		} catch (Exception e) {
			logger.error(e.getMessage());
			serviceStatus = Status.PRECONDITION_ERROR;
			errorCode = "BKS0008";
			errorText = e.getMessage();
			returnXml = populateOutputObject(null,null,null);
		}
		finally {
			dao.closeConnection();
		}
		return returnXml;
	}

	private ArrayList<AccessNumberType> getAccessNumbers(
			GetCodbPrerequisiteDataDAO dao, GetCodbPrerequisiteDataInput input) throws Exception {
		ArrayList<AccessNumberType> accessNumbers = new ArrayList<AccessNumberType>();
		ArrayList<HashMap<String, Object>> list = dao.getAccessNumbers(input.getServiceSubscriptionId());
		for (int i=0;list != null&&i<list.size();i++){
			String number = list.get(i).get("ACCESS_NUMBER").toString();
			String[] numberParts = number.split(";");
			if (numberParts != null && numberParts.length >= 3){
				AccessNumberType rufnummer = new AccessNumberType();
				rufnummer.setCountryCode("00"+numberParts[0]);
				rufnummer.setCityCode("0"+numberParts[1]);
				rufnummer.setBaseNumber(numberParts[2]);
				rufnummer.setStartExtension((numberParts.length>=4)?numberParts[3]:null);
				rufnummer.setEndExtension((numberParts.length>=5)?numberParts[4]:null);
				accessNumbers.add(rufnummer);
			}
		}

 		return accessNumbers;
	}

	private ArrayList<String> getTariffOptions(GetCodbPrerequisiteDataDAO dao,
			GetCodbPrerequisiteDataInput input) throws Exception {
		ArrayList<String> tariffOptions = new ArrayList<String>();
		ArrayList<HashMap<String, Object>> list = dao.getTariffOptions(input.getServiceSubscriptionId());
		for (int i=0;list != null&&i<list.size();i++){
			String option = list.get(i).get("NAME").toString();
			tariffOptions.add(option);
		}

 		return tariffOptions;
	}

	protected String populateOutputObject(HashMap<String,Object> contractData, ArrayList<String> tariffOptions, ArrayList<AccessNumberType> accessNumbers) throws Exception {
		String returnXml = null;
		output = new GetCodbPrerequisiteDataOutput();
		if (serviceStatus != Status.SUCCESS){
			output.setResult(false);
			output.setErrorCode(errorCode);
			output.setErrorText(errorText);
		}else {
			output.setResult(true);
			String classification = contractData.get("CLASSIFICATION_RD").toString();
			
			((GetCodbPrerequisiteDataOutput)output).setProductCode(contractData.get("PRODUCT_CODE").toString());
			if (classification.equals("VC") || classification.equals("VE"))
				((GetCodbPrerequisiteDataOutput)output).setProductName(contractData.get("ALT_PRODUCT_NAME").toString());
			else
				((GetCodbPrerequisiteDataOutput)output).setProductName(contractData.get("PRODUCT_NAME").toString());
			((GetCodbPrerequisiteDataOutput)output).setPricingStructureCode(contractData.get("PRICING_STRUCTURE_CODE").toString());
			((GetCodbPrerequisiteDataOutput)output).setPricingStructureName(contractData.get("PRICING_STRUCTURE_NAME").toString());
			((GetCodbPrerequisiteDataOutput)output).setProductVersionCode(contractData.get("PRODUCT_VERSION_CODE").toString());
			((GetCodbPrerequisiteDataOutput)output).setContractNumber(contractData.get("CONTRACT_NUMBER").toString());
			((GetCodbPrerequisiteDataOutput)output).setAccountNumber(contractData.get("ACCOUNT_NUMBER").toString());
			((GetCodbPrerequisiteDataOutput)output).setServiceCode(contractData.get("SERVICE_CODE").toString());
			((GetCodbPrerequisiteDataOutput)output).getTariffOptions().addAll(tariffOptions);
			((GetCodbPrerequisiteDataOutput)output).getAccessNumbers().addAll(accessNumbers);
		}
		try {
			returnXml = theHelper.serializeForMcf(output, customerNumber,accessNumber,"de.arcor.aaw.kernAAW.bks.services" ,"GetCodbPrerequisiteData");
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return returnXml;
	}

	private HashMap<String,Object> getContractData(GetCodbPrerequisiteDataDAO dao,
			                               GetCodbPrerequisiteDataInput input) throws Exception {
		ArrayList<HashMap<String, Object>> list = dao.getContractData(input.getServiceSubscriptionId());
		if ((list == null) || (list.size() == 0)){
			list = dao.getSignedContractData(input.getServiceSubscriptionId());
		}
		if ((list == null) || (list.size() == 0)){
			serviceStatus = Status.FUNCTIONAL_ERROR;
			errorText = "No contract has been found for service subscription "+input.getServiceSubscriptionId();
			errorCode = "BKS0101";
			return null;
		}

		return list.get(0);
	}

	public void setInput(InputData input) {
		super.setInput(input);
	}
}
