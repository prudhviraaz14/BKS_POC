package net.arcor.bks.db;

import java.util.ArrayList;
import java.util.HashMap;

public interface GetCodbPrerequisiteDataDAO extends BksDataAccessObject  {

	ArrayList<HashMap<String, Object>> getContractData(String serviceSubscriptionId)throws Exception;

	ArrayList<HashMap<String, Object>> getSignedContractData(String serviceSubscriptionId)throws Exception;

	ArrayList<HashMap<String, Object>> getTariffOptions(String serviceSubscriptionId)throws Exception;

	ArrayList<HashMap<String, Object>> getAccessNumbers(String serviceSubscriptionId)throws Exception;
}
