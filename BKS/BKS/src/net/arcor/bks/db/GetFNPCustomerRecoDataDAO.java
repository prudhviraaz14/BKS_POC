package net.arcor.bks.db;

import java.util.ArrayList;
import java.util.HashMap;

import com.domainlanguage.time.CalendarDate;

public interface GetFNPCustomerRecoDataDAO extends BksDataAccessObject {

	ArrayList<HashMap<String, Object>> getBundleForBundleId(String bundleId)throws Exception;

	ArrayList<HashMap<String, Object>> getCustomersForDateInterval(
			CalendarDate sd, CalendarDate ed)throws Exception;

}
