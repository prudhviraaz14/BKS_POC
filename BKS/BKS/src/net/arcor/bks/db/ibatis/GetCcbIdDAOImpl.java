package net.arcor.bks.db.ibatis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.orm.ibatis.SqlMapClientTemplate;

import net.arcor.bks.db.GetCcbIdDAO;

public class GetCcbIdDAOImpl extends BksDataAccessObjectImpl implements
		GetCcbIdDAO {

	public ArrayList<HashMap<String, Object>> getExactAccessNumber(
			String countryCode, String cityCode, String localNumber)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        String accessnumber = countryCode.substring(2)+cityCode.substring(1)+localNumber;
        List list = template.queryForList("GetCcbId."+dataSourceName + ".RetrieveExactAccessNumber",accessnumber);
        return (ArrayList<HashMap<String, Object>>) list;
	}

}
