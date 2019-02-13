package net.arcor.bks.db.ibatis;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.arcor.bks.db.GetFNPCustomerRecoDataDAO;

import org.springframework.orm.ibatis.SqlMapClientTemplate;

import com.domainlanguage.time.CalendarDate;
public class GetFNPCustomerRecoDataDAOImpl extends BksDataAccessObjectImpl implements GetFNPCustomerRecoDataDAO {

	public ArrayList<HashMap<String, Object>> getBundleForBundleId(
			String bundleId) throws Exception {
		SqlMapClientTemplate template = getSqlMapClientTemplate();
		List list = template.queryForList("GetFNPCustomerRecoData."+dataSourceName + ".RetrieveBundleForBundleId",bundleId);
		return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getCustomersForDateInterval(
			CalendarDate sd, CalendarDate ed) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,Object> params = new HashMap<String, Object>();
        Date sqlSd = (sd==null)?null:Date.valueOf(sd.toString());
        params.put("startDate", sqlSd);
        Date sqlEd = (ed==null)?null:Date.valueOf(ed.toString());
        sqlEd.setTime(sqlEd.getTime()-1000);
        params.put("endDate", sqlEd);
        List list = template.queryForList("GetFNPCustomerRecoData."+dataSourceName + ".RetrieveCustomersForDateInterval",params);
        return (ArrayList<HashMap<String, Object>>) list;
	}


}
