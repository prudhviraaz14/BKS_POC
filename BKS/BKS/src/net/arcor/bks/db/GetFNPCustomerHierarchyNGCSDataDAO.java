package net.arcor.bks.db;

import java.util.ArrayList;
import java.util.HashMap;

public interface GetFNPCustomerHierarchyNGCSDataDAO extends BksDataAccessObject {

	ArrayList<HashMap<String, Object>> getChildCustomers(String customerNumber, Long nestingLevel)throws Exception;

	ArrayList<HashMap<String, Object>> getPrimaryAddress(String custNo)throws Exception;
}
