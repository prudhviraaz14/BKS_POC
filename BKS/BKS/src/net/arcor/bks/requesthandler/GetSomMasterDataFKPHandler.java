package net.arcor.bks.requesthandler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import net.arcor.bks.client.DatabaseClient;
import net.arcor.bks.common.BksDataException;
import net.arcor.bks.db.GetSomMasterDataDAO;
import net.arcor.epsm_basetypes_001.InputData;
import net.arcor.orderschema.Order;
import de.arcor.aaw.kernAAW.bks.services.GetSomMasterDataFKPInput;
import de.arcor.aaw.kernAAW.bks.services.GetSomMasterDataFKPOutput;
import de.arcor.aaw.kernAAW.bks.services.ProductCommitment;
import de.arcor.aaw.kernAAW.bks.services.Rebate;
import de.arcor.aaw.kernAAW.bks.services.RebateItem;
import de.arcor.aaw.kernAAW.bks.services.ServiceDeliveryContract;


public class GetSomMasterDataFKPHandler extends BaseServiceHandler {
	
	
	public GetSomMasterDataFKPHandler() {
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
			String inputRoot = ((GetSomMasterDataFKPInput)input).getCustomerHierarchyNumber();
			if (rootCustNo==null || !rootCustNo.equals(inputRoot)){
				String text = "Customer "+customerNumber+" does not belong to hierarchy of "+inputRoot;
				throw new BksDataException("BKS0024",text);
			}
			List<String>  hierarchycustNumbers= new ArrayList<String>();
			List<String> accountNumbers = ((GetSomMasterDataFKPInput)input).getKontonummer();
			getHierarchyAccountsAndCustomers(dao,accountNumbers,hierarchycustNumbers);
			List<String> contractNumbers = ((GetSomMasterDataFKPInput)input).getVertragsnummer();
			Order order = getCustomerData(accountNumbers,contractNumbers);		
			ArrayList<ServiceDeliveryContract> contracts = new ArrayList<ServiceDeliveryContract>();
			populateSDLContracts(dao,hierarchycustNumbers,contracts);
			ArrayList<Rebate> rebates=new ArrayList<Rebate>();
			populateRebatesForAccounts(dao,accountNumbers,rebates);
			returnXml = populateOutputObject(order,contracts,rebates);
		} catch (BksDataException e) {
			logger.error(e.getMessage());
			serviceStatus = Status.PRECONDITION_ERROR;
			errorCode = e.getCode();
			errorText = e.getText();
			returnXml = populateOutputObject(null,null,null);
		} catch (Exception e) {
			logger.error(e.getMessage());
			serviceStatus = Status.PRECONDITION_ERROR;
			errorCode = "BKS0008";
			if (e.getMessage() != null && e.getMessage().length() > 255)
				errorText = e.getMessage().substring(0, 255);
			else
				errorText = e.getMessage();
			returnXml = populateOutputObject(null,null,null);
		}
		finally {
			dao.closeConnection();
		}
		return returnXml;
	}

	private void populateRebatesForAccounts(GetSomMasterDataDAO dao,
			List<String> accountNumbers, ArrayList<Rebate> rebates) throws Exception {
		if (accountNumbers==null || accountNumbers.size()==0)
			return;
		ArrayList<HashMap<String, Object>> dbRebates = dao.getRebatesForAccountList(accountNumbers);
		for(int i=0;i<dbRebates.size();i++) {
			String accountNumber = (String)dbRebates.get(i).get("ACCOUNT_NUMBER");
			Rebate rebate = getRebateForAccountNumber(accountNumber,rebates);
			List<RebateItem> rebateItems = rebate.getRebateItem();
			populateRebateItem(dbRebates.get(i),rebateItems);
		}
		
	}

	private void populateRebateItem(HashMap<String, Object> dbRebate,
			List<RebateItem> rebateItems) {
		RebateItem rebateItem = new RebateItem();
		rebateItems.add(rebateItem);	
		rebateItem.setPricePlanCode((String)dbRebate.get("PRICE_PLAN_CODE"));
		rebateItem.setPricePlanName((String)dbRebate.get("PRICE_PLAN_NAME"));
		rebateItem.setPricePlanOptionValue((String)dbRebate.get("OPTION_VALUE_NAME"));
		rebateItem.setPricingStructureCode((String)dbRebate.get("PRICING_STRUCTURE_CODE"));
		rebateItem.setPricingStructureName((String)dbRebate.get("PRICING_STRUCTURE_NAME"));
		rebateItem.setProductCode((String)dbRebate.get("PRODUCT_CODE"));
		rebateItem.setProductName((String)dbRebate.get("PRODUCT_NAME"));
		rebateItem.setServiceCode((String)dbRebate.get("SERVICE_CODE"));
		rebateItem.setServiceName((String)dbRebate.get("SERVICE_NAME"));
		rebateItem.setServiceSubscriptionId((String)dbRebate.get("SERVICE_SUBSCRIPTION_ID"));
	}

	private Rebate getRebateForAccountNumber(String accountNumber,
			ArrayList<Rebate> rebates) {
		for(int i=0;i<rebates.size();i++) {
			if(accountNumber.equals(rebates.get(i).getAccountNumber()))
			     return rebates.get(i);
		}
		Rebate rebate = new Rebate();
		rebate.setAccountNumber(accountNumber);
		rebates.add(rebate);
		return rebate;
	}

	private void populateSDLContracts(GetSomMasterDataDAO dao,
			List<String> hierarchycustNumbers,
			ArrayList<ServiceDeliveryContract> contracts) throws Exception {
		if (hierarchycustNumbers==null || hierarchycustNumbers.size()==0)
			return;
		ArrayList<HashMap<String, Object>> dbServDelivConts = dao.getSDLContractsForCustList(hierarchycustNumbers);
		ArrayList<String> pcNumbers = new ArrayList<String>();
		for(int i=0;i<dbServDelivConts.size();i++) {
			String contractNumber = (String)dbServDelivConts.get(i).get("CONTRACT_NUMBER");
			ServiceDeliveryContract sdc = getSDCForContractNumber(contractNumber,contracts);
			List<ProductCommitment> pCs = sdc.getProductCommitment();
			String pcNumber=(String)dbServDelivConts.get(i).get("PRODUCT_COMMITMENT_NUMBER");
			if (!pcNumbers.contains(pcNumber)){
				pcNumbers.add(pcNumber);
				populateProdCommit(dbServDelivConts.get(i),pCs);
			}
		}
	}

	private void populateProdCommit(HashMap<String, Object> dbServDelivCont,
			List<ProductCommitment> pCs) {
		ProductCommitment pC = new ProductCommitment();
		pCs.add(pC);	
		pC.setPricingStructureCode((String)dbServDelivCont.get("PRICING_STRUCTURE_CODE"));
		pC.setPricingStructureName((String)dbServDelivCont.get("PRICING_STRUCTURE_NAME"));
		pC.setProductCode((String)dbServDelivCont.get("PRODUCT_CODE"));
		pC.setProductName((String)dbServDelivCont.get("PRODUCT_NAME"));
		pC.setProductCommitmentNumber((String)dbServDelivCont.get("PRODUCT_COMMITMENT_NUMBER"));
		BigDecimal bd = (BigDecimal)dbServDelivCont.get("SALES_ORG_NUMBER");
		pC.setSalesOrganizationNumber(String.valueOf(bd.intValue()));
	}

	private ServiceDeliveryContract getSDCForContractNumber(
			String contractNumber, ArrayList<ServiceDeliveryContract> contracts) {
		for(int i=0;i<contracts.size();i++) {
			if(contractNumber.equals(contracts.get(i).getContractNumber()))
			     return contracts.get(i);
		}
		ServiceDeliveryContract sdc = new ServiceDeliveryContract();
		sdc.setContractNumber(contractNumber);
		contracts.add(sdc);
		return sdc;
	}

	private void getHierarchyAccountsAndCustomers(GetSomMasterDataDAO dao,
			List<String> accountNumbers, List<String> hierarchycustNumbers) throws Exception {
		ArrayList<HashMap<String, Object>> customers = dao.getCustomerAncestor(customerNumber);
		if (customers==null || customers.size()==0)
			return;
		for (int i=0;i<customers.size();i++)
			hierarchycustNumbers.add((String)customers.get(i).get("CUSTOMER_NUMBER"));
	
		ArrayList<HashMap<String, Object>> accounts = dao.getAccountsForCustList(hierarchycustNumbers);
		for (int i=0;i<accounts.size();i++)
			accountNumbers.add((String)accounts.get(i).get("ACCOUNT_NUMBER"));
	}

	protected String populateOutputObject(Order order, ArrayList<ServiceDeliveryContract> contracts, ArrayList<Rebate> rebates) throws Exception {
		String returnXml = null;
		output = new GetSomMasterDataFKPOutput();
		if (serviceStatus != Status.SUCCESS){
			output.setResult(false);
			output.setErrorCode(errorCode);
			output.setErrorText(errorText);
		}else {
			output.setResult(true);
 			((GetSomMasterDataFKPOutput)output).setOrder(order);
 			((GetSomMasterDataFKPOutput)output).getServiceDeliveryContract().addAll(contracts);
 			((GetSomMasterDataFKPOutput)output).getRebate().addAll(rebates);
		}
		returnXml = theHelper.serializeForMcf(output, customerNumber,accessNumber,"de.arcor.aaw.kernAAW.bks.services" ,"GetSomMasterDataFKP");
		return returnXml;
	}

	private Order getCustomerData(List<String> accountNumbers, List<String> contractNumbers) throws Exception{
		GetSomMasterDataHandler mdh = new GetSomMasterDataHandler();
		mdh.setClientSchemaVersion(clientSchemaVersion);
		Order order = null;
		   order = mdh.getOrder(customerNumber,null,accountNumbers,contractNumbers,false,false);
	   return order;
	}
	public void setInput(InputData input) {
		super.setInput(input);
		setCustomerNumber(((GetSomMasterDataFKPInput)input).getKundennummer());
	}
}
