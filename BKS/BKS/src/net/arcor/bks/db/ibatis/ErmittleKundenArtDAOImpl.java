package net.arcor.bks.db.ibatis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.orm.ibatis.SqlMapClientTemplate;

import net.arcor.bks.db.ErmittleKundenArtDAO;

public class ErmittleKundenArtDAOImpl extends BksDataAccessObjectImpl implements ErmittleKundenArtDAO {


	public ArrayList<HashMap<String, Object>> getCustomer(String customerNumber) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList(dataSourceName + ".RetrieveCustomer",customerNumber);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getRootCustomer(String customerNumber) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList(dataSourceName + ".RetrieveRootCustomer",customerNumber);
        return (ArrayList<HashMap<String, Object>>) list;
	}
}
