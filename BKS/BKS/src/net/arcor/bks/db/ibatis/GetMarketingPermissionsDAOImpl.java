package net.arcor.bks.db.ibatis;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.arcor.bks.db.GetMarketingPermissionsDAO;

import org.springframework.orm.ibatis.SqlMapClientTemplate;

import com.domainlanguage.time.TimePoint;

public class GetMarketingPermissionsDAOImpl extends BksDataAccessObjectImpl implements GetMarketingPermissionsDAO {


	public ArrayList<HashMap<String, Object>> getCustomerPermissions(String customerNumber, String fEndGr, 
			            String role, BigDecimal bewVersion,TimePoint effDate) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,Object> params = new HashMap<String, Object>();
        params.put("customerNumber", customerNumber);
        params.put("frontendGroup", fEndGr);
        params.put("role", "%"+role+"%");
        params.put("bewVersion", bewVersion);
        Timestamp date = new Timestamp(effDate.asJavaUtilDate().getTime());
        params.put("date", date);
        List  list = template.queryForList("GetMarketingPermissions."+dataSourceName + ".RetrieveCustomerPermissions",params);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getPermissionStatus(
			String customerNumber) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List  list = template.queryForList("GetMarketingPermissions."+dataSourceName + ".RetrievePermissionStatus",customerNumber);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getCustLegacyPermissions(
			String customerNumber) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List  list = template.queryForList("GetMarketingPermissions."+dataSourceName + ".RetrieveCustLegacyPermissions",customerNumber);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getDefaultPermissions(
			String customerNumber, String fEndGr, String role) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,Object> params = new HashMap<String, Object>();
        params.put("customerNumber", customerNumber);
        params.put("frontendGroup", fEndGr);
        params.put("role", "%"+role+"%");
        List  list = template.queryForList("GetMarketingPermissions."+dataSourceName + ".RetrieveDefaultPermissions",params);
        return (ArrayList<HashMap<String, Object>>) list;
	}
}
