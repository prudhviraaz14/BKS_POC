package net.arcor.bks.db;

import java.util.ArrayList;
import java.util.HashMap;

public interface GetFNPAddressByIdDAO  extends BksDataAccessObject{

	ArrayList<HashMap<String, Object>> getAddressesForBundle(String bundleId) throws Exception;

	ArrayList<HashMap<String, Object>> getAddressesForCustomer(String customerNumber) throws Exception;

}
