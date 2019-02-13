package net.arcor.bks.db;

import java.util.ArrayList;
import java.util.HashMap;

public interface ErmittleKundenArtDAO extends BksDataAccessObject{
	ArrayList<HashMap<String, Object>> getCustomer(String customerNumber) throws Exception;
	ArrayList<HashMap<String, Object>> getRootCustomer(String customerNumber) throws Exception;

}
