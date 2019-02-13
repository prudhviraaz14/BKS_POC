package net.arcor.bks.db.ibatis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.orm.ibatis.SqlMapClientTemplate;
import net.arcor.bks.db.GetDetailsForAddedFunctionDAO;

public class GetDetailsForAddedFunctionDAOImpl extends BksDataAccessObjectImpl
		implements GetDetailsForAddedFunctionDAO {

	public ArrayList<HashMap<String, Object>> getProductInfo(String functionName,String accessName,
									String variantName,String phoneSysType,String tariffCode) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,String> params = new HashMap<String, String>();
        params.put("functionName", functionName);
        params.put("accessName", accessName);
        params.put("variantName", variantName);
        params.put("phoneSysType", phoneSysType);
        params.put("tariffCode", tariffCode);
        List list = template.queryForList("GetDetailsForAddedFunction."+dataSourceName + ".RetrieveProductInfo",params);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getTariffOptions(
			String serviceCode) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetDetailsForAddedFunction."+dataSourceName + ".RetrieveTariffOptions",serviceCode);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getSalesPackageInfo(
			String salesPacketCode) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetDetailsForAddedFunction."+dataSourceName + ".RetrieveSalesPackageInfo",salesPacketCode);
        return (ArrayList<HashMap<String, Object>>) list;
	}

}
