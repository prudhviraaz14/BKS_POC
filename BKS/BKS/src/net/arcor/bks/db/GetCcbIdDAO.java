package net.arcor.bks.db;

import java.util.ArrayList;
import java.util.HashMap;

import net.arcor.bks.db.BksDataAccessObject;

public interface GetCcbIdDAO extends BksDataAccessObject {

	ArrayList<HashMap<String, Object>> getExactAccessNumber(String countryCode,
			String cityCode, String localNumber)throws Exception;

}
