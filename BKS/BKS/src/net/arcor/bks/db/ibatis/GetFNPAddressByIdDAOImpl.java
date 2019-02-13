package net.arcor.bks.db.ibatis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.orm.ibatis.SqlMapClientTemplate;

import net.arcor.bks.db.GetFNPAddressByIdDAO;

public class GetFNPAddressByIdDAOImpl extends BksDataAccessObjectImpl implements GetFNPAddressByIdDAO {

	public ArrayList<HashMap<String, Object>> getAddressesForBundle(
			String bundleId) throws Exception {
		SqlMapClientTemplate template = getSqlMapClientTemplate();
		List list = template.queryForList("GetFNPAddressById."+dataSourceName + ".RetrieveAddressesForBundle",bundleId);
		return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getAddressesForCustomer(
			String customerNumber) throws Exception {
		SqlMapClientTemplate template = getSqlMapClientTemplate();
		List list = template.queryForList("GetFNPAddressById."+dataSourceName + ".RetrieveAddressesForCustomer",customerNumber);
		return (ArrayList<HashMap<String, Object>>) list;
	}
}
