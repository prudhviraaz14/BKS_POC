package net.arcor.bks.db;

import java.util.ArrayList;
import java.util.HashMap;

public interface WbciGetDtagContractNumberDAO  extends BksDataAccessObject{

	ArrayList<HashMap<String, Object>> getDataByAccessNumber(String CountryCode,String onkz, String rufnummer)throws Exception;

	ArrayList<HashMap<String, Object>> getRootCustomer(String custNum)throws Exception;

	ArrayList<HashMap<String, Object>> getBundleData(String serSuId)throws Exception;

	ArrayList<HashMap<String, Object>> getConfigValueForSerSu(String serSuId)throws Exception;

	ArrayList<HashMap<String, Object>> getDeactRecByAccessNumber(String accNum)throws Exception;

	ArrayList<HashMap<String, Object>> getDataByTasi(String serSuId)throws Exception;

	ArrayList<HashMap<String, Object>> getChildService(String serSuId)throws Exception;

	ArrayList<HashMap<String, Object>> getAccNumForSerSu(String serSuId)throws Exception;
}
