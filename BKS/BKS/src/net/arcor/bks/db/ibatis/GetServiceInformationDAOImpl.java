package net.arcor.bks.db.ibatis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.orm.ibatis.SqlMapClientTemplate;

import net.arcor.bks.db.GetServiceInformationDAO;

public class GetServiceInformationDAOImpl extends BksDataAccessObjectImpl
		implements GetServiceInformationDAO {

	public ArrayList<HashMap<String, Object>> getServiceTicket(
			String serviceSubscriptionId,String usageMode,String orderId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,String> params = new HashMap<String, String>();
        params.put("ServiceSubscriptionId", serviceSubscriptionId);
        params.put("UsageMode", usageMode);
        params.put("OrderId", orderId);
        List list = template.queryForList("GetServiceInformation."+dataSourceName + ".RetrieveServiceTicket",params);
        return (ArrayList<HashMap<String, Object>>) list;
	}

}
