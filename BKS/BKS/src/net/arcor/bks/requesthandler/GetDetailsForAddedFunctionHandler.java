/*
    $Header:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/GetDetailsForAddedFunctionHandler.java-arc   1.11   Jul 17 2017 15:21:50   makuier  $

    $Log:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/GetDetailsForAddedFunctionHandler.java-arc  $
 * 
 *    Rev 1.11   Jul 17 2017 15:21:50   makuier
 * Use service billing name instead of service name for VolumeCap
 * 
 *    Rev 1.10   Jun 08 2017 07:38:14   makuier
 * Handling Volume cap
 * 
 *    Rev 1.9   Oct 20 2016 06:58:44   gaurav.verma
 * RMS 160046 - "IT-K 33599 BKS  „GetDetailsForAddedFunction“ of tariffOptions must be SERVICE.BILLING_NAME_2
 * 
 *    Rev 1.8   Apr 10 2015 14:35:48   sibani.panda
 * SPN-BKS-000129868 string length of pricingStructureName is truncated to 60 chars
 * 
 *    Rev 1.8   Apr 10 2015 08:12:40   sibanipa
 * SPN-BKS-000129868 string length of pricingStructureName is truncated to 60 chars
 *    
 *    Rev 1.7   Nov 25 2014 08:12:40   makuier
 * Use serviceCode+PricingStructureCode to fetch sales packet
 * 
 *    Rev 1.6   Oct 30 2014 08:48:40   makuier
 * SalesPacket info returned.
 * 
 *    Rev 1.5   Jun 27 2013 12:56:06   makuier
 * Support for phoneSystemType added.
 * 
 *    Rev 1.4   Aug 17 2011 15:23:04   makuier
 * TvCenterOptions added.
 * 
 *    Rev 1.3   May 05 2011 18:04:16   makuier
 * truncate names if exceeding the max length.
 * 
 *    Rev 1.2   Jan 28 2010 16:47:08   makuier
 * Cache the exception and return an error XML.
 * 
 *    Rev 1.1   Jan 08 2010 16:43:30   makuier
 * check if tariff option code list is empty.
 * 
 *    Rev 1.0   Dec 18 2009 16:38:32   makuier
 * Initial revision.
*/
package net.arcor.bks.requesthandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.arcor.bks.client.DatabaseClient;
import net.arcor.bks.common.BksDataException;
import net.arcor.bks.db.GetDetailsForAddedFunctionDAO;
import net.arcor.epsm_basetypes_001.InputData;
import net.arcor.orderschema.Service;
import net.arcor.orderschema.TVCenterOption;
import net.arcor.orderschema.TariffOption;
import de.arcor.aaw.kernAAW.bks.services.GetDetailsForAddedFunctionInput;
import de.arcor.aaw.kernAAW.bks.services.GetDetailsForAddedFunctionOutput;
import de.arcor.aaw.kernAAW.bks.services.SomReferenceContainer;

public class GetDetailsForAddedFunctionHandler extends BaseServiceHandler {
	
	public GetDetailsForAddedFunctionHandler() {
		super();
	}

	public String execute() throws Exception {
		String returnXml = null;
		GetDetailsForAddedFunctionDAO dao = 
			(GetDetailsForAddedFunctionDAO)DatabaseClient.getBksDataAccessObject(null,"GetDetailsForAddedFunctionDAO");

		List<SomReferenceContainer> outputList =  ((GetDetailsForAddedFunctionInput)input).getReferenceInput();
		try {
			outputList = getProductInfo(dao,outputList);
			returnXml = populateOutputObject(outputList);
		} catch (BksDataException e) {
			logger.error(e.getText());
			serviceStatus = Status.PRECONDITION_ERROR;
			errorCode = e.getCode();
			errorText = e.getText();
			returnXml = populateOutputObject(outputList);
		} catch (Exception e) {
			logger.error(e.getMessage());
			serviceStatus = Status.PRECONDITION_ERROR;
			errorCode = "BKS0008";
			errorText = e.getMessage();
			returnXml = populateOutputObject(outputList);
		}
		finally {
			dao.closeConnection();
		}
		return returnXml;
	}

	private List<SomReferenceContainer> getProductInfo(GetDetailsForAddedFunctionDAO dao,
												List<SomReferenceContainer> inputList) throws Exception {
		
		for (int i = 0; i < inputList.size(); i++) {
			SomReferenceContainer item = inputList.get(i);
			String funcName = item.getFunctionName();
			String accName = item.getAccessName();
			String varName = item.getVariantName();
			String phoneSysType = null;
			if (accName.equals("sipTrunk") && 
			    funcName.equals("voice") && 
			    varName.equals("voicePBX"))
				phoneSysType = item.getPhoneSystemType();

			String tariffCode = item.getPricingStructureCode();
			ArrayList<HashMap<String, Object>> list = 
				dao.getProductInfo(funcName, accName, varName, phoneSysType, tariffCode);
			if (list!=null && list.size() == 1){
				String serviceCode = (String)list.get(0).get("SERVICE_CODE");
				item.setMainAccessServiceCode(serviceCode);
				item.setProductCode((String)list.get(0).get("PRODUCT_CODE"));
				String productName = (String)list.get(0).get("PRODUCT_NAME");
				if (productName.length() > 60)
					item.setProductName(productName.substring(0, 60));
				else
					item.setProductName(productName);

				String tariffName = (String)list.get(0).get("PRICING_STRUCTURE_NAME");
				//String length changed for SPN-BKS-000129868
				if (tariffName.length() > 60)
					item.setPricingStructureName(tariffName.substring(0, 60));
				else
					item.setPricingStructureName(tariffName);

				item.setProductVersionCode((String)list.get(0).get("PRODUCT_VERSION_CODE"));
				
				String salesPacketCode = theHelper.getSalesPacketCode(serviceCode+";"+tariffCode);
				String salesPacketName = null;
				if (salesPacketCode!=null){
					ArrayList<HashMap<String, Object>> salesPacket = dao.getSalesPackageInfo(salesPacketCode);
					if (salesPacket!=null&&salesPacket.size()>0)
						salesPacketName = (String) salesPacket.get(0).get("NAME");
				}
				if (salesPacketCode!=null&&salesPacketName!=null){
					item.setSalesPacketCode(salesPacketCode);
					item.setSalesPacketName(salesPacketName);
				}

			} else if (list!=null && list.size() > 1){
				throw new BksDataException("BKS0027","Ambiguous result. Please send more specific input.");
			}
			if (item.getTariffOptions() != null)
				populateTariffOptions(dao, item.getTariffOptions().getTariffOption());	
			if (item.getTvCenterOptions() != null)
				populateTvCenterOptions(dao, item.getTvCenterOptions().getTvCenterOption());	
			if (item.getVolumeCap() != null)
				populateVolumeCap(dao, item.getVolumeCap());	
		}
		return inputList;
	}

	private void populateVolumeCap(GetDetailsForAddedFunctionDAO dao,
			Service service) throws Exception {
		ArrayList<HashMap<String, Object>> list = dao.getTariffOptions(service.getServiceCode());
		if (list!=null && list.size() > 0) {
			String serviceName = list.get(0).get("BILLING_NAME_2").toString();
			if (serviceName.length() > 100)
				service.setServiceBillingName(serviceName.substring(0, 100));
			else
				service.setServiceBillingName(serviceName);
		}
	}

	private void populateTariffOptions(GetDetailsForAddedFunctionDAO dao,
			List<TariffOption> tariffOptions) throws Exception {
		for (int i = 0;tariffOptions!=null&&i < tariffOptions.size(); i++) {
			String serviceCode = tariffOptions.get(i).getServiceCode();
			ArrayList<HashMap<String, Object>> list = dao.getTariffOptions(serviceCode);
			if (list!=null && list.size() > 0) {
				String serviceName = list.get(0).get("BILLING_NAME_2").toString();
				if (serviceName.length() > 100)
					tariffOptions.get(i).setServiceBillingName(serviceName.substring(0, 100));
				else
					tariffOptions.get(i).setServiceBillingName(serviceName);
			}
		}
	}

	private void populateTvCenterOptions(GetDetailsForAddedFunctionDAO dao,
			List<TVCenterOption> tvCenterOption) throws Exception {
		for (int i = 0;tvCenterOption!=null&&i < tvCenterOption.size(); i++) {
			String serviceCode = tvCenterOption.get(i).getServiceCode();
			ArrayList<HashMap<String, Object>> list = dao.getTariffOptions(serviceCode);
			if (list!=null && list.size() > 0) {
				String serviceName = list.get(0).get("BILLING_NAME_2").toString();
				if (serviceName.length() > 100)
					tvCenterOption.get(i).setServiceBillingName(serviceName.substring(0, 100));
				else
					tvCenterOption.get(i).setServiceBillingName(serviceName);
			}
		}
	}

	protected String populateOutputObject(List<SomReferenceContainer> outputList) throws Exception {
		String returnXml = null;
		output = new GetDetailsForAddedFunctionOutput();
		if (serviceStatus != Status.SUCCESS){
			output.setResult(false);
			output.setErrorCode(errorCode);
			output.setErrorText(errorText);
		}else {
			output.setResult(true);
		}
		((GetDetailsForAddedFunctionOutput)output).getReferenceOutput().addAll(outputList);
		try {
			returnXml = theHelper.serializeForMcf(output, customerNumber,accessNumber,"de.arcor.aaw.kernAAW.bks.services" ,"GetDetailsForAddedFunction");
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return returnXml;
	}

	public void setInput(InputData input) {
		super.setInput(input);
	}
}
