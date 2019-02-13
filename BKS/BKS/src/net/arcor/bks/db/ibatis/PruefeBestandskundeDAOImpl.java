package net.arcor.bks.db.ibatis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.orm.ibatis.SqlMapClientTemplate;

import net.arcor.bks.db.PruefeBestandskundeDAO;

public class PruefeBestandskundeDAOImpl extends BksDataAccessObjectImpl
		implements PruefeBestandskundeDAO {


	public ArrayList<HashMap<String, Object>> getCustomerByAccessNumber(
			String countryCode, String cityCode, String localNumber)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,String> params = new HashMap<String, String>();
        params.put("shortnumber", countryCode.substring(2)+cityCode.substring(1)+localNumber.substring(0,3));
        params.put("accessnumber", countryCode.substring(2)+cityCode.substring(1)+localNumber);
        List list = template.queryForList(dataSourceName + ".RetrieveAccessNumber",params);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getCustomerByAddressName(
			String postalCode, String street, String streetNum,
			String numberSuffix, String rechtForm, String firmname)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,String> params = new HashMap<String, String>();
        params.put("postalCode", postalCode);
        params.put("street", street);
        params.put("streetNum", streetNum);
        params.put("numberSuffix", numberSuffix);
        params.put("rechtForm", rechtForm);
        params.put("firmname", firmname);
        List list = template.queryForList(dataSourceName + ".RetrieveAddressNameCustomer",params);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getCustomerByAddress(
			String postalCode, String street, String streetNum,
			String numberSuffix,String custNumList) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,String> params = new HashMap<String, String>();
        params.put("postalCode", postalCode);
        params.put("street", street);
        params.put("streetNum", streetNum);
        params.put("numberSuffix", numberSuffix);
        params.put("custNumList", custNumList);
        List list = template.queryForList(dataSourceName + ".RetrieveAddressCustomer",params);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getCustomerByName(
			String rechtForm, String firmname, String custNumList)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,String> params = new HashMap<String, String>();
        params.put("rechtForm", rechtForm);
        params.put("firmname", firmname);
        params.put("custNumList", custNumList);
        List list = template.queryForList(dataSourceName + ".RetrieveNameCustomer",params);
        return (ArrayList<HashMap<String, Object>>) list;
	}

}
