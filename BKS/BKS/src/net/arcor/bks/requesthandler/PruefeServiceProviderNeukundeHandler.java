/*
    $Header:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/PruefeServiceProviderNeukundeHandler.java-arc   1.12   Sep 28 2012 15:43:36   makuier  $

    $Log:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/PruefeServiceProviderNeukundeHandler.java-arc  $
 * 
 *    Rev 1.12   Sep 28 2012 15:43:36   makuier
 * Check external orders for pending termination as well
 * 
 *    Rev 1.11   Mar 12 2012 15:15:10   makuier
 * populate offeneKuendigung.
 * 
 *    Rev 1.10   Jun 06 2011 17:35:56   makuier
 * Handle oracle date casting.
 * 
 *    Rev 1.9   May 25 2011 15:08:52   makuier
 * Ibatis maps date to timestamp.
 * 
 *    Rev 1.8   Jan 28 2010 16:52:46   makuier
 * Cache the exception and return an error XML.
 * 
 *    Rev 1.7   Sep 22 2009 19:01:02   makuier
 * moved the end date calculation to helper
 * 
 *    Rev 1.6   Aug 13 2009 11:46:20   makuier
 * Pointer chek for uncategorized services.
 * 
 *    Rev 1.5   Aug 10 2009 10:51:06   makuier
 * Additional pointer check added.
 * 
 *    Rev 1.4   Aug 06 2009 13:28:44   makuier
 * Added some pointer checks.
 * 
 *    Rev 1.3   Jun 29 2009 15:25:38   makuier
 * Additional information returned.
 * IT-24821
 * 
 *    Rev 1.2   May 27 2009 16:05:54   makuier
 * MCF2 adaption
*/
package net.arcor.bks.requesthandler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import net.arcor.bks.client.DatabaseClient;
import net.arcor.bks.common.BksRefDataCacheHandler;
import net.arcor.bks.db.PruefeServiceProviderNeukundeDAO;
import net.arcor.epsm_basetypes_001.InputData;
import de.arcor.aaw.kernAAW.bks.services.AccessNumberItem;
import de.arcor.aaw.kernAAW.bks.services.Person;
import de.arcor.aaw.kernAAW.bks.services.PruefeServiceProviderNeukundeInput;
import de.arcor.aaw.kernAAW.bks.services.PruefeServiceProviderNeukundeOutput;
import de.arcor.aaw.kernAAW.bks.services.Servicedata;

public class PruefeServiceProviderNeukundeHandler extends BaseServiceHandler {
	
	public PruefeServiceProviderNeukundeHandler() {
		super();
	}

	public String execute() throws Exception {
		String returnXml = null;
		PruefeServiceProviderNeukundeDAO dao = 
			(PruefeServiceProviderNeukundeDAO)DatabaseClient.getBksDataAccessObject(null,"PruefeServiceProviderNeukundeDAO");

		try {
			String customerClass = getCustomerByAccessNumber(dao,(PruefeServiceProviderNeukundeInput)input);
			String providerCode = getProviderCode(dao,customerNumber,customerClass);
			returnXml = populateOutputObject(providerCode);
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

	protected String populateOutputObject(String providerCode) throws Exception {
		String returnXml = null;
		if (serviceStatus != Status.SUCCESS){
			output.setResult(false);
			output.setErrorCode(errorCode);
			output.setErrorText(errorText);
		}else
			output.setResult(true);

		((PruefeServiceProviderNeukundeOutput)output).setServiceProviderCode(providerCode);
		try {
			returnXml = theHelper.serializeForMcf(output, customerNumber,accessNumber,"de.arcor.aaw.kernAAW.bks.services" ,"PruefeServiceProviderNeukunde");
			if (serviceStatus == Status.SUCCESS && customerNumber!=null)
				theHelper.loadCache(customerNumber+";PruefeBestandskunde",returnXml);
			if (serviceStatus == Status.SUCCESS && accessNumber!=null)
				theHelper.loadCache(accessNumber+";PruefeBestandskunde",returnXml);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return returnXml;
	}

	private String getProviderCode(PruefeServiceProviderNeukundeDAO dao,
			String retrievedCustomer, String custClass) throws Exception {
		if (retrievedCustomer == null || custClass == null)
			return null;
		String providerCode = BksRefDataCacheHandler.getClassProviderMap().get(custClass);
		if (providerCode == null){
			ArrayList<HashMap<String, Object>> rootList = dao.getRootCustomer(retrievedCustomer);
			if(rootList != null && rootList.size() > 0){
				String rootNumber = rootList.get(0).get("CUSTOMER_NUMBER").toString();
				HashMap<String,String> providerCodes =BksRefDataCacheHandler.getProviderCode();
				if (providerCodes.get(rootNumber) != null)
					providerCode = providerCodes.get(rootNumber);
				else
					providerCode = "ARCO";
			}
			else
			{
				serviceStatus = Status.PRECONDITION_ERROR;
				errorCode = "BKS0010";
				errorText = "The data for the customer "+customerNumber+" is corrupted.";
				return null;
			}
		}
		return providerCode;
	}

	private String getCustomerByAccessNumber(PruefeServiceProviderNeukundeDAO dao,
			                               PruefeServiceProviderNeukundeInput input) throws Exception {
		String customerClass = null;
		PruefeServiceProviderNeukundeOutput out = new PruefeServiceProviderNeukundeOutput();
		out.setOffeneKuendigung(false);
		HashMap<String,String> functions =BksRefDataCacheHandler.getFunction();
		for (int i=0;i<input.getRufnummer().size();i++){
			AccessNumberItem ani = new AccessNumberItem();
			ani.setRufnummer(input.getRufnummer().get(i));
			String countryCode = input.getRufnummer().get(i).getLaenderkennzeichen();
			String cityCode = input.getRufnummer().get(i).getVorwahl();
			String localNumber = input.getRufnummer().get(i).getRufnummer();
			if (countryCode == null || cityCode == null || localNumber == null ){
				serviceStatus = Status.PRECONDITION_ERROR;
				errorCode = "BKS0013";
				errorText = "All access number fields (country code, city code and local number) have to be provided.";
				return null;
			}
			ArrayList<HashMap<String, Object>> list = dao.getExactAccessNumber(countryCode,cityCode,localNumber);
			if ((list == null) || (list.size() == 0))
				list = dao.getAccessNumber(countryCode,cityCode,localNumber);

			for (int j=0;list != null&&j<list.size();j++){
				Servicedata servData = new Servicedata();
				servData.setServiceCode(list.get(j).get("SERVICE_CODE").toString());
				String servSubsId = list.get(j).get("SERVICE_SUBSCRIPTION_ID").toString();
				servData.setTechnicalServiceId(getTechServiceId(dao,servSubsId));
				ani.getServicedata().add(servData);
				String serviceType = functions.get(servData.getServiceCode());
				if (serviceType != null && serviceType.equals("Sprache")){
					out.setServiceSubscriptionIDVoice(servSubsId);
					ArrayList<HashMap<String, Object>> cntData = dao.getContractData(servSubsId);
					if (cntData != null&&cntData.size()>0){
						out.setProductCode((String)cntData.get(0).get("PRODUCT_CODE"));
						String minUnit = (String)cntData.get(0).get("MIN_PERIOD_DUR_UNIT_RD"); 
						Integer minValue = null;
						if (cntData.get(0).get("MIN_PERIOD_DUR_VALUE") != null) 
							minValue = ((BigDecimal) cntData.get(0).get("MIN_PERIOD_DUR_VALUE")).intValue(); 
						Object minStartDate = cntData.get(0).get("MIN_PERIOD_START_DATE"); 
						String extUnit = (String)cntData.get(0).get("AUTO_EXTENT_DUR_UNIT_RD"); 
						Integer extValue = null;
						if (cntData.get(0).get("AUTO_EXTENT_DUR_VALUE") != null)
							extValue = ((BigDecimal)cntData.get(0).get("AUTO_EXTENT_DUR_VALUE")).intValue();
						boolean bAutoExtend = false;
						if (cntData.get(0).get("AUTO_EXTENSION_IND") != null)
							bAutoExtend = ((String)cntData.get(0).get("AUTO_EXTENSION_IND")).equals("Y");
						out.setVertragsende(theHelper.getContractEndDate(minStartDate,minUnit,minValue,extUnit,extValue,bAutoExtend));
						out.setOffeneKuendigung(hasPendingTermination(dao,servSubsId));
					}
				}
				if (customerNumber == null){
					customerNumber = list.get(j).get("CUSTOMER_NUMBER").toString();
					out.setKundennummer(customerNumber);
					customerClass = list.get(j).get("CLASSIFICATION_RD").toString();
				}else if (!list.get(j).get("CUSTOMER_NUMBER").toString().equals(customerNumber)){
					serviceStatus = Status.FUNCTIONAL_ERROR;
					errorText = "More than one active customer found for the provided access numbers";
					errorCode = "BKS0101";
					out.setKundennummer(null);
				}
			}
			if (list != null && list.size() != 0){
				out.getRufnummerElement().add(ani);
				ArrayList<HashMap<String, Object>> entity = dao.getEntityData(customerNumber);
				if (entity != null&&entity.size()>0){
					Person cust= new Person();
					cust.setNachname(entity.get(0).get("NAME").toString());
					if (entity.get(0).get("FORENAME") != null)
						cust.setVorname(entity.get(0).get("FORENAME").toString());
					out.setKunde(cust);
				}
			}
		}
		setOutput(out);
		if (customerNumber != null && errorText == null){
			serviceStatus = Status.FUNCTIONAL_ERROR;
			errorText = "One active customer found for the provided access numbers";
			errorCode = "BKS0100";
			return customerClass;
		}
		return null;
	}

	private Boolean hasPendingTermination(PruefeServiceProviderNeukundeDAO dao,
			String servSubsId) throws Exception {
		ArrayList<HashMap<String, Object>> tickets = dao.getServiceTicket(servSubsId, "4");
		if (tickets!=null && tickets.size()>0)
			return true;
		ArrayList<HashMap<String, Object>> externalOrders = dao.getExternalOrders(servSubsId,"Termination");
		if (externalOrders!=null && externalOrders.size()>0)
			return true;
		return false;
	}

	private String getTechServiceId(PruefeServiceProviderNeukundeDAO dao, String ServiceSubsId) throws Exception {
		ArrayList<HashMap<String, Object>> list = dao.getTechServiceId(ServiceSubsId);
		if(list != null && list.size() >0)
			return list.get(0).get("ACCESS_NUMBER").toString();
		return null;
	}

	public void setInput(InputData input) {
		super.setInput(input);
		String countryCode = ((PruefeServiceProviderNeukundeInput)input).getRufnummer().get(0).getLaenderkennzeichen();
		String cityCode = ((PruefeServiceProviderNeukundeInput)input).getRufnummer().get(0).getVorwahl();
		String localNumber = ((PruefeServiceProviderNeukundeInput)input).getRufnummer().get(0).getRufnummer();
		accessNumber = countryCode.substring(2)+cityCode.substring(1)+localNumber;
	}

}
