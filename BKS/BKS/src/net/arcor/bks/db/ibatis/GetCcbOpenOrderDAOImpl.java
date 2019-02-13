package net.arcor.bks.db.ibatis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.orm.ibatis.SqlMapClientTemplate;
import net.arcor.bks.db.GetCcbOpenOrderDAO;

public class GetCcbOpenOrderDAOImpl extends BksDataAccessObjectImpl implements
GetCcbOpenOrderDAO {

	public ArrayList<HashMap<String, Object>> getCustomerDataForBarcode(
			String barcode) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetCcbOpenOrder."+dataSourceName + ".RetrieveCustomerDataForBarcode",barcode);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getMainAccessService(
			String servSubsId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetCcbOpenOrder."+dataSourceName + ".RetrieveMainAccessService",servSubsId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getOpenOrdersForBarcode(
			String barcode) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetCcbOpenOrder."+dataSourceName + ".RetrieveOpenOrdersForBarcode",barcode);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getProviderLog(String orderId)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetCcbOpenOrder."+dataSourceName + ".RetrieveProviderLog",orderId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getContractData(
			String servSubsId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetCcbOpenOrder."+dataSourceName + ".RetrieveContractData",servSubsId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getServiceTickets(ArrayList<String> orderIds)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetCcbOpenOrder."+dataSourceName + ".RetrieveServiceTickets",orderIds);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getConfiguredValues(
			String ticketId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetCcbOpenOrder."+dataSourceName + ".RetrieveConfiguredValues",ticketId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getAddress(String ticketId)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetCcbOpenOrder."+dataSourceName + ".RetrieveAddress",ticketId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getBundleItems(String servSubsId)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetCcbOpenOrder."+dataSourceName + ".RetrieveBundleItems",servSubsId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getAccessNumbers(String ticketId)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetCcbOpenOrder."+dataSourceName + ".RetrieveAccessNumbers",ticketId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getSalesOrgNumber(
			ArrayList<String> contractNumbers) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetCcbOpenOrder."+dataSourceName + ".RetrieveSalesOrgNumber",contractNumbers);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getTerminatedConfigValues(
			String servSubsId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("GetCcbOpenOrder."+dataSourceName + ".RetrieveTerminatedConfigValues",servSubsId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

}
