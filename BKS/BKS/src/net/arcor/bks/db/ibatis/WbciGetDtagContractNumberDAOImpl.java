package net.arcor.bks.db.ibatis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.arcor.bks.db.WbciGetDtagContractNumberDAO;

import org.springframework.orm.ibatis.SqlMapClientTemplate;
public class WbciGetDtagContractNumberDAOImpl extends BksDataAccessObjectImpl implements WbciGetDtagContractNumberDAO {

	public ArrayList<HashMap<String, Object>> getBundleData(String serSuId)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("WbciGetDtagContractNumber."+dataSourceName + ".RetrieveBundleData",serSuId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getConfigValueForSerSu(
			String serSuId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("WbciGetDtagContractNumber."+dataSourceName + ".RetrieveConfigValueForSerSu",serSuId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getDataByAccessNumber(
			String CountryCode, String onkz, String rufnummer) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        String number = CountryCode+onkz+rufnummer;
        List list = template.queryForList("WbciGetDtagContractNumber."+dataSourceName + ".RetrieveDataByAccessNumber",number);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getRootCustomer(String custNum)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("WbciGetDtagContractNumber."+dataSourceName + ".RetrieveRootCustomer",custNum);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getDeactRecByAccessNumber(String accNum)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("WbciGetDtagContractNumber."+dataSourceName + ".RetrieveDeactRecByAccessNumber",accNum);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getDataByTasi(String serSuId)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("WbciGetDtagContractNumber."+dataSourceName + ".RetrieveDataByTasi",serSuId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getChildService(String serSuId)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("WbciGetDtagContractNumber."+dataSourceName + ".RetrieveChildService",serSuId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getAccNumForSerSu(String serSuId)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("WbciGetDtagContractNumber."+dataSourceName + ".RetrieveAccNumForSerSu",serSuId);
        return (ArrayList<HashMap<String, Object>>) list;
	}


}
