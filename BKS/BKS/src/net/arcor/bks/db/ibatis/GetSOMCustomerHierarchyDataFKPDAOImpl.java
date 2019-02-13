package net.arcor.bks.db.ibatis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.orm.ibatis.SqlMapClientTemplate;

import net.arcor.bks.db.GetSOMCustomerHierarchyDataFKPDAO;

public class GetSOMCustomerHierarchyDataFKPDAOImpl extends
		BksDataAccessObjectImpl implements GetSOMCustomerHierarchyDataFKPDAO {

	public ArrayList<HashMap<String, Object>> getChildCustomers(
			String customerNumber) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetSOMCustomerHierarchyDataFKP."+dataSourceName + ".RetrieveChildCustomers",customerNumber);
        return (ArrayList<HashMap<String, Object>>) list;
	}

}
