package net.arcor.bks.requesthandler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import net.arcor.bks.client.DatabaseClient;
import net.arcor.bks.common.BksDataException;
import net.arcor.bks.db.GetSomMasterDataDAO;
import net.arcor.epsm_basetypes_001.InputData;
import de.arcor.aaw.kernAAW.bks.services.PrefetchProductBundlesFKPInput;
import de.arcor.aaw.kernAAW.bks.services.PrefetchProductBundlesFKPOutput;
import de.arcor.aaw.kernAAW.bks.services.PrefetchProductBundlesOutput;


public class PrefetchProductBundlesFKPHandler extends BaseServiceHandler {
	
	private int outputListSize = 0;
	
	public PrefetchProductBundlesFKPHandler() {
		super();
	}

	public String execute() throws Exception {
		String returnXml = null;
		GetSomMasterDataDAO dao = 
			(GetSomMasterDataDAO)DatabaseClient.getBksDataAccessObject(null,"GetSomMasterDataDAO");
		try {
			ArrayList<HashMap<String, Object>> rootCustomer = dao.getRootCustomer(customerNumber);
			String rootCustNo = null;
			if (rootCustomer != null && rootCustomer.size() > 0) {
				rootCustNo = (String) rootCustomer.get(0).get("ROOT_CUSTOMER_NUMBER");
			}
			String inputRoot = ((PrefetchProductBundlesFKPInput)input).getCustomerHierarchyNumber();
			if (rootCustNo==null || !rootCustNo.equals(inputRoot)){
				String text = "Customer "+customerNumber+" does not belong to hierarchy of "+inputRoot;
				throw new BksDataException("BKS0024",text);
			}
			BigDecimal chunkSize = ((PrefetchProductBundlesFKPInput)input).getChunkSize();
			BigDecimal chunkNumber = ((PrefetchProductBundlesFKPInput)input).getChunkNumber();
			String oneGrpId = ((PrefetchProductBundlesFKPInput)input).getOneGroupId();
			String superCustTrackId= ((PrefetchProductBundlesFKPInput)input).getSuperCustomerTrackingId();
			String key=null;
			if (chunkNumber!=null){
				key = customerNumber+chunkNumber.intValue();
				if (oneGrpId!=null)
					key  = oneGrpId+chunkNumber.intValue();
				else if (superCustTrackId!=null)
					key  = superCustTrackId+chunkNumber.intValue();
			}
			returnXml = (String) theHelper.fetchFromCache("PrefetchProductBundlesFKP",key, null);
			if (returnXml!=null)
				return returnXml;
			PrefetchProductBundlesOutput output = getBundle(oneGrpId, superCustTrackId,chunkSize,chunkNumber);		
			returnXml = populateOutputObject(output);
			if (chunkNumber!=null){
				theHelper.loadCache(key+";PrefetchProductBundlesFKP", returnXml);
			}
			if (chunkSize!=null && chunkNumber!=null && chunkNumber.intValue()==1)
					prefetchNextChunks(rootCustNo,customerNumber,oneGrpId,superCustTrackId,inputRoot,chunkSize);
		} catch (BksDataException e) {
			logger.error(e.getMessage());
			serviceStatus = Status.PRECONDITION_ERROR;
			errorCode = e.getCode();
			errorText = e.getText();
			returnXml = populateOutputObject(null);
		} catch (Exception e) {
			logger.error(e.getMessage());
			serviceStatus = Status.PRECONDITION_ERROR;
			errorCode = "BKS0008";
			if (e.getMessage() != null && e.getMessage().length() > 255)
				errorText = e.getMessage().substring(0, 255);
			else
				errorText = e.getMessage();
			returnXml = populateOutputObject(null);
		}
		finally {
			dao.closeConnection();
		}
		return returnXml;
	}

	private void prefetchNextChunks(String rootCustNo, String customerNumber,
			String oneGrpId, String superCustTrackId, String hierarchyNumber, BigDecimal chunkSize) {
		try {
			int loopNumber = outputListSize / chunkSize.intValue();
			if (outputListSize%chunkSize.intValue() == 0)
				loopNumber--;
			
			int chunkNo = 2;
			String key=null;
			if (customerNumber!=null) 
				key  = customerNumber+chunkNo;
			else if (oneGrpId!=null)
				key  = oneGrpId+chunkNo;
			else if (superCustTrackId!=null)
				key  = superCustTrackId+chunkNo;
			for (int i = 0; i < loopNumber; i++) {
				if (theHelper.fetchFromCache("PrefetchProductBundlesFKP", key,null) == null) {
					PrefetchProductBundlesFKPHandler newHnd = new PrefetchProductBundlesFKPHandler();
					PrefetchProductBundlesFKPInput newInput = new PrefetchProductBundlesFKPInput();
					newInput.setCustomerNumber(customerNumber);
					newInput.setOneGroupId(oneGrpId);
					newInput.setSuperCustomerTrackingId(superCustTrackId);
					newInput.setChunkSize(chunkSize);
					newInput.setCustomerHierarchyNumber(hierarchyNumber);
					newInput.setChunkNumber(BigDecimal.valueOf(chunkNo++));
					newHnd.setInput(newInput);
					newHnd.start();
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	private String populateOutputObject(PrefetchProductBundlesOutput prefOutput) throws Exception {
		String returnXml = null;
		output = new PrefetchProductBundlesFKPOutput();
		if (serviceStatus != Status.SUCCESS){
			output.setResult(false);
			output.setErrorCode(errorCode);
			output.setErrorText(errorText);
		}else {
			output.setResult(true);
			((PrefetchProductBundlesFKPOutput)output).
		    setProductBundlePreviewList(prefOutput.getProductBundlePreviewList());
			((PrefetchProductBundlesFKPOutput)output).setChunkNumber(prefOutput.getChunkNumber());
			((PrefetchProductBundlesFKPOutput)output).setLastChunk(prefOutput.isLastChunk());
		}
		returnXml = theHelper.serializeForMcf(output, customerNumber,accessNumber,"de.arcor.aaw.kernAAW.bks.services" ,"PrefetchProductBundlesFKP");
		return returnXml;
	}

	private PrefetchProductBundlesOutput getBundle(String oneGrpId, String superCustTrackId, BigDecimal chunkSize, BigDecimal chunkNumber) throws Exception{
		PrefetchProductBundlesHandler mdh = new PrefetchProductBundlesHandler();
		PrefetchProductBundlesOutput output = mdh.populateBundleList(customerNumber,oneGrpId,superCustTrackId, chunkSize, chunkNumber);
		outputListSize = mdh.getBundles().size()+mdh.getPendingLineCreations().size();
	    return output;
	}
	public void setInput(InputData input) {
		super.setInput(input);
		setCustomerNumber(((PrefetchProductBundlesFKPInput)input).getCustomerNumber());
	}
}
