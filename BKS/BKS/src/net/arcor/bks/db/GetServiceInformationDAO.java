package net.arcor.bks.db;

import java.util.ArrayList;
import java.util.HashMap;

public interface GetServiceInformationDAO extends BksDataAccessObject  {

	ArrayList<HashMap<String, Object>> getServiceTicket(String serviceSubscriptionId,String usageMode, String orderId)throws Exception;
}
