package net.arcor.bks.requesthandler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import net.arcor.bks.client.DatabaseClient;
import net.arcor.bks.common.BksDataException;
import net.arcor.bks.db.GetSOMCustomerHierarchyDataFKPDAO;
import net.arcor.bks.db.GetSomMasterDataDAO;
import net.arcor.epsm_basetypes_001.InputData;
import net.arcor.orderschema.Customer;
import net.arcor.orderschema.OwnerCcbId;
import de.arcor.aaw.kernAAW.bks.services.GetSOMCustomerHierarchyDataFKPInput;
import de.arcor.aaw.kernAAW.bks.services.GetSOMCustomerHierarchyDataFKPOutput;


public class GetSOMCustomerHierarchyDataFKPHandler extends BaseServiceHandler {
	
	
	private ArrayList<String[]> custNumberList;
	boolean lastChunk=false;
	public GetSOMCustomerHierarchyDataFKPHandler() {
		super();
	}

	public String execute() throws Exception {
		String returnXml = null;
		GetSOMCustomerHierarchyDataFKPDAO dao = 
			(GetSOMCustomerHierarchyDataFKPDAO)DatabaseClient.getBksDataAccessObject(null,"GetSOMCustomerHierarchyDataFKPDAO");

		try {
			BigDecimal chunkSize = ((GetSOMCustomerHierarchyDataFKPInput)input).getChunkSize();
			BigDecimal chunkNumber = ((GetSOMCustomerHierarchyDataFKPInput)input).getChunkNumber();
			lastChunk=false;
			validateRootCustomer(customerNumber);
			String cacheKey = customerNumber;
			if (chunkNumber!=null)
				cacheKey+=chunkNumber.intValue();
			returnXml = (String) theHelper.fetchFromCache("GetSOMCustomerHierarchyDataFKP",cacheKey,null);
			if (returnXml!=null)
				return returnXml;
			custNumberList = getCustomerNumbers(dao,customerNumber);
			ArrayList<Customer> custList = getCustomerInformation(custNumberList,chunkSize,chunkNumber);
			returnXml = populateOutputObject(custList,chunkNumber);
			if (chunkSize!=null && chunkNumber!=null && chunkNumber.intValue()==1)
				prefetchNextChunks(customerNumber,chunkSize);
		} catch (BksDataException e) {
			logger.error(e.getMessage());
			serviceStatus = Status.PRECONDITION_ERROR;
			errorCode = e.getCode();
			errorText = e.getText();
			returnXml = populateOutputObject(null,null);
		} catch (Exception e) {
			logger.error(e.getMessage());
			serviceStatus = Status.PRECONDITION_ERROR;
			errorCode = "BKS0008";
			if (e.getMessage() != null && e.getMessage().length() > 255)
				errorText = e.getMessage().substring(0, 255);
			else
				errorText = e.getMessage();
			returnXml = populateOutputObject(null,null);
		}
		finally {
			dao.closeConnection();
		}
		return returnXml;
	}

	private void validateRootCustomer(String customerNumber) throws Exception{
		GetSomMasterDataDAO dao = 
			(GetSomMasterDataDAO)DatabaseClient.getBksDataAccessObject(null,"GetSomMasterDataDAO");

		ArrayList<HashMap<String, Object>> rootCustomer = dao.getRootCustomer(customerNumber);
		String rootCustNo = null;
		if (rootCustomer != null && rootCustomer.size() > 0) {
			rootCustNo = (String) rootCustomer.get(0).get("ROOT_CUSTOMER_NUMBER");
		}
		String inputRoot = ((GetSOMCustomerHierarchyDataFKPInput)input).getCustomerHierarchyNumber();
		if (rootCustNo==null || !rootCustNo.equals(inputRoot)){
			String text = "Customer "+customerNumber+" does not belong to hierarchy of "+inputRoot;
			throw new BksDataException("BKS0024",text);
		}

	}
	private ArrayList<Customer> getCustomerInformation(
			ArrayList<String[]> custNumberList, BigDecimal chunkSize,
			BigDecimal chunkNumber) throws Exception {
		ArrayList<Customer> custNodeList = new ArrayList<Customer>();
		int startloop = 0;
		int endloop = custNumberList.size();
		if (chunkSize!=null && chunkNumber!=null){
			startloop=(chunkNumber.intValue()-1)*chunkSize.intValue();
			endloop=startloop+chunkSize.intValue();
			if (endloop>=custNumberList.size())
				lastChunk=true;
			endloop=(endloop<custNumberList.size())?endloop:custNumberList.size();
		}
			
		for (int i=startloop;i<endloop;i++){
			String custNo = custNumberList.get(i)[0];
			String parCustNo = custNumberList.get(i)[1];
			GetSomMasterDataHandler mdh = new GetSomMasterDataHandler();
			Customer aCustomer = mdh.getCustomerNode(custNo);
			OwnerCcbId value = new OwnerCcbId();
			value.setExisting(parCustNo);
			aCustomer.setOwnerCcbId(value);
			custNodeList.add(aCustomer);
		}
		return custNodeList;
	}


	private ArrayList<String[]> getCustomerNumbers(GetSOMCustomerHierarchyDataFKPDAO dao, String customerNumber) throws Exception {
		ArrayList<String[]> custNoList = new ArrayList<String[]>();
		ArrayList<HashMap<String, Object>> custList = dao.getChildCustomers(customerNumber);
		if (custList==null || custList.size()==0)
			return null;
		for (int i = 0; i < custList.size(); i++) {
			String childCustNo = (String) custList.get(i).get("CUSTOMER_NUMBER");
			String parCustNo = (String) custList.get(i).get("ASSOCIATED_CUSTOMER_NUMBER");
			custNoList.add(new String[] {childCustNo,parCustNo});
		}
		return custNoList;
	}

	protected String populateOutputObject(ArrayList<Customer> custList, BigDecimal chunkNumber) throws Exception {
		String returnXml = null;
		output = new GetSOMCustomerHierarchyDataFKPOutput();
		if (serviceStatus != Status.SUCCESS){
			output.setResult(false);
			output.setErrorCode(errorCode);
			output.setErrorText(errorText);
		}else {
			output.setResult(true);
 			((GetSOMCustomerHierarchyDataFKPOutput)output).getCustomerList().addAll(custList);
 			if (chunkNumber!=null) {
				((GetSOMCustomerHierarchyDataFKPOutput) output).setLastChunk(lastChunk);
				((GetSOMCustomerHierarchyDataFKPOutput) output).setChunkNumber(chunkNumber);
			}
		}
		returnXml = theHelper.serializeForMcf(output, customerNumber,accessNumber,"de.arcor.aaw.kernAAW.bks.services" ,"GetSOMCustomerHierarchyDataFKP");
		if (serviceStatus == Status.SUCCESS){
			String cacheKey = ((GetSOMCustomerHierarchyDataFKPInput)input).getCustomerNumber()+chunkNumber;
			theHelper.loadCache(cacheKey+";GetSOMCustomerHierarchyDataFKP",returnXml);
		}
		return returnXml;
	}

	private void prefetchNextChunks(String customerNumber, BigDecimal chunkSize){
		try {
			int loopNumber = custNumberList.size() / chunkSize.intValue();
			int chunkNo = 2;
			for (int i = 0; i < loopNumber; i++) {
				if (theHelper.fetchFromCache("GetSOMCustomerHierarchyDataFKP", customerNumber+chunkNo,null) == null) {
					GetSOMCustomerHierarchyDataFKPHandler newHnd = new GetSOMCustomerHierarchyDataFKPHandler();
					GetSOMCustomerHierarchyDataFKPInput newInput = new GetSOMCustomerHierarchyDataFKPInput();
					newInput.setCustomerNumber(customerNumber);
					newInput.setChunkSize(chunkSize);
					newInput.setChunkNumber(BigDecimal.valueOf(chunkNo++));
					newHnd.setInput(newInput);
					newHnd.setCustNumberList(custNumberList);
					newHnd.start();
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

	}
	@Override
	public void run() {
		// Call the execute function and cache the result.
		try {
			BigDecimal chunkSize = ((GetSOMCustomerHierarchyDataFKPInput)input).getChunkSize();
			BigDecimal chunkNumber = ((GetSOMCustomerHierarchyDataFKPInput)input).getChunkNumber();
			lastChunk=false;
			ArrayList<Customer> custList = getCustomerInformation(custNumberList,chunkSize,chunkNumber);
			populateOutputObject(custList,chunkNumber);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void setInput(InputData input) {
		super.setInput(input);
		setCustomerNumber(((GetSOMCustomerHierarchyDataFKPInput)input).getCustomerNumber());
	}
	public ArrayList<String[]> getCustNumberList() {
		return custNumberList;
	}

	public void setCustNumberList(ArrayList<String[]> custNumberList) {
		this.custNumberList = custNumberList;
	}

}
