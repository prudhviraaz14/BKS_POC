package net.arcor.bks.db;

import java.util.ArrayList;
import java.util.HashMap;

public interface GetCcbOpenOrderDAO  extends BksDataAccessObject {

	ArrayList<HashMap<String, Object>> getOpenOrdersForBarcode(String barcode) throws Exception;

	ArrayList<HashMap<String, Object>> getProviderLog(String orderId) throws Exception;

	ArrayList<HashMap<String, Object>> getServiceTickets(ArrayList<String> orderIds) throws Exception;

	ArrayList<HashMap<String, Object>> getCustomerDataForBarcode(String barcode) throws Exception;

	ArrayList<HashMap<String, Object>> getContractData(String servSubsId) throws Exception;

	ArrayList<HashMap<String, Object>> getMainAccessService(String servSubsId) throws Exception;

	ArrayList<HashMap<String, Object>> getConfiguredValues(String ticketId) throws Exception;

	ArrayList<HashMap<String, Object>> getAccessNumbers(String ticketId) throws Exception;

	ArrayList<HashMap<String, Object>> getAddress(String ticketId) throws Exception;

	ArrayList<HashMap<String, Object>> getBundleItems(String string) throws Exception;

	ArrayList<HashMap<String, Object>> getSalesOrgNumber(ArrayList<String> contractNumbers) throws Exception;

	ArrayList<HashMap<String, Object>> getTerminatedConfigValues(String servSubsId) throws Exception;

}
