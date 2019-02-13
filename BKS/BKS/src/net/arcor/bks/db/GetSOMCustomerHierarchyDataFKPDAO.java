package net.arcor.bks.db;

import java.util.ArrayList;
import java.util.HashMap;

public interface GetSOMCustomerHierarchyDataFKPDAO extends BksDataAccessObject {

	ArrayList<HashMap<String, Object>> getChildCustomers(String customerNumber) throws Exception;

}
