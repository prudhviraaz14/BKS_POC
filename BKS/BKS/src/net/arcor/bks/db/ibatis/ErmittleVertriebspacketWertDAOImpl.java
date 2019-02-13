/*
    $Header:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/db/ibatis/ErmittleVertriebspacketWertDAOImpl.java-arc   1.5   Mar 05 2009 14:27:06   makuier  $

    $Log:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/db/ibatis/ErmittleVertriebspacketWertDAOImpl.java-arc  $
 * 
 *    Rev 1.5   Mar 05 2009 14:27:06   makuier
 * Removed the char code argument from hardware select.
 * 
 *    Rev 1.4   Feb 19 2009 12:48:46   makuier
 * getBundleItems added.
 * 
 *    Rev 1.3   Nov 14 2008 14:26:32   makuier
 * getServiceSubscription added
 * 
 *    Rev 1.2   Jul 02 2008 10:05:54   makuier
 * Handle canceled renegotation scenario.
 * 
 *    Rev 1.1   Jun 16 2008 08:09:38   makuier
 * Corrected hardware service function.
 * 
 *    Rev 1.0   May 13 2008 13:11:10   makuier
 * Initial revision.
 * 
 *    Rev 1.1   Mar 12 2008 15:57:08   makuier
 * get main access and get hardware added.
 *
 *    Rev 1.0   Feb 06 2008 17:40:40   makuier
 * Initial revision.
*/
package net.arcor.bks.db.ibatis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.orm.ibatis.SqlMapClientTemplate;

import com.ibatis.sqlmap.client.SqlMapException;

import net.arcor.bks.db.ErmittleVertriebspacketWertDAO;

public class ErmittleVertriebspacketWertDAOImpl extends BksDataAccessObjectImpl
implements ErmittleVertriebspacketWertDAO {


	public ArrayList<HashMap<String, Object>> getBandwidthService(
			String servSubId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList(dataSourceName + ".RetrieveBandwidthService",servSubId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getCondition(String servSubId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList(dataSourceName + ".RetrieveCondition",servSubId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getExactAccessNumber(
			String countryCode, String cityCode, String localNumber)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        String accessnumber = countryCode.substring(2)+cityCode.substring(1)+localNumber;
        List list = template.queryForList(dataSourceName + ".RetrieveExactAccessNumber",accessnumber);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getAccessNumber(
			String countryCode, String cityCode, String localNumber)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,String> params = new HashMap<String, String>();
        params.put("shortnumber", countryCode.substring(2)+cityCode.substring(1)+localNumber.substring(0,3));
        params.put("accessnumber", countryCode.substring(2)+cityCode.substring(1)+localNumber);
        List list = template.queryForList(dataSourceName + ".RetrieveAccessNumberPW",params);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getOFContractData(
			String servSubId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList(dataSourceName + ".RetrieveOFContractDataPW",servSubId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getSDContractData(
			String servSubId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList(dataSourceName + ".RetrieveSDContractDataPW",servSubId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getMainAccessService(String servSubsId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList(dataSourceName + ".RetrieveMainAccessService",servSubsId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getHardwareCharacteristic(
			String prodSubsId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        HashMap<String,String> params = new HashMap<String, String>();
        params.put("prodSubId", prodSubsId);
        List list = template.queryForList(dataSourceName + ".RetrieveHardwareCharacteristic",params);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getSignedOFContractData(
			String servSubId) throws Exception {
	        SqlMapClientTemplate template = getSqlMapClientTemplate();
	        try {
				List list = template.queryForList(dataSourceName + ".RetrieveSignedOFContractData",servSubId);
				return (ArrayList<HashMap<String, Object>>) list;
			} catch (SqlMapException e) {
				// ignore
			}
			return null;
	}

	public ArrayList<HashMap<String, Object>> getSignedSDContractData(
			String servSubId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        try {
			List list = template.queryForList(dataSourceName + ".RetrieveSignedSDContractData",servSubId);
			return (ArrayList<HashMap<String, Object>>) list;
		} catch (SqlMapException e) {
			// Ignore
		}
		return null;
	}

	public ArrayList<HashMap<String, Object>> getServiceSubscription(
			String serviceSubscrId) throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList(dataSourceName + ".RetrieveServiceSubscription",serviceSubscrId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

	public ArrayList<HashMap<String, Object>> getBundleItems(String servSubId)
			throws Exception {
        SqlMapClientTemplate template = getSqlMapClientTemplate();
        List list = template.queryForList("ErmittleVertriebspacketWert."+dataSourceName + ".RetrieveBundleItems",servSubId);
        return (ArrayList<HashMap<String, Object>>) list;
	}

}
