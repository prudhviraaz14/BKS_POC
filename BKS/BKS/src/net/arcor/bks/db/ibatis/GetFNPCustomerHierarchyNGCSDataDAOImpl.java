package net.arcor.bks.db.ibatis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.orm.ibatis.SqlMapClientTemplate;

import net.arcor.bks.db.GetFNPCustomerHierarchyNGCSDataDAO;
public class GetFNPCustomerHierarchyNGCSDataDAOImpl extends BksDataAccessObjectImpl implements GetFNPCustomerHierarchyNGCSDataDAO {

	public ArrayList<HashMap<String, Object>> getChildCustomers(
			String customerNumber, Long nestingLevel) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,Object> params = new HashMap<String, Object>();
        params.put("customerNumber", customerNumber);
        params.put("nestingLevel", nestingLevel);
        List list = template.queryForList("GetFNPCustomerHierarchyNGCSData."+dataSourceName + ".RetrieveChildCustomers",params);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getPrimaryAddress(String custNo)
			throws Exception {
		SqlMapClientTemplate template = getSqlMapClientTemplate();
		List list = template.queryForList("GetFNPCustomerHierarchyNGCSData."+dataSourceName + ".RetrievePrimaryAddress",custNo);
		return (ArrayList<HashMap<String, Object>>) list;
	}
}
