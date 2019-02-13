package net.arcor.bks.db.ibatis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.orm.ibatis.SqlMapClientTemplate;


import net.arcor.bks.db.PrefetchVertriebspaketDetailsDAO;

public class PrefetchVertriebspaketDetailsDAOImpl extends BksDataAccessObjectImpl
		implements PrefetchVertriebspaketDetailsDAO {

	

	

	public ArrayList<HashMap<String, Object>> getAccessNumbers(String customerNumber)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("PrefetchVertriebspaketDetails."+dataSourceName + ".RetrieveAccessNumbers",customerNumber);
        return (ArrayList<HashMap<String, Object>>) list;
	}



}
