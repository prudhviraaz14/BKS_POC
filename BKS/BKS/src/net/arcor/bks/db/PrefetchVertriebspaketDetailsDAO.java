package net.arcor.bks.db;

import java.util.ArrayList;
import java.util.HashMap;

public interface PrefetchVertriebspaketDetailsDAO extends BksDataAccessObject {
	
	ArrayList<HashMap<String, Object>> getAccessNumbers(String customerNumber) throws Exception;
	
}
