package net.arcor.bks.db;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import com.domainlanguage.time.TimePoint;

public interface GetMarketingPermissionsDAO extends BksDataAccessObject{

	public ArrayList<HashMap<String, Object>> getCustomerPermissions(String customerNumber, String fEndGr, 
			                         String role,BigDecimal bewVersion, TimePoint effdate) throws Exception;

	public ArrayList<HashMap<String, Object>> getPermissionStatus(String customerNumber) throws Exception;

	public ArrayList<HashMap<String, Object>> getCustLegacyPermissions(String customerNumber) throws Exception;

	public ArrayList<HashMap<String, Object>> getDefaultPermissions(String customerNumber, String fEndGr, String role)throws Exception;
	
}
