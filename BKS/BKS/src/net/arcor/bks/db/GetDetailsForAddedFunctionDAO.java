package net.arcor.bks.db;

import java.util.ArrayList;
import java.util.HashMap;

public interface GetDetailsForAddedFunctionDAO extends BksDataAccessObject  {

	ArrayList<HashMap<String, Object>> getProductInfo(String functionName,String accessName,
												String variantName,String phoneSysType, 
												String tariffCode)throws Exception;

	ArrayList<HashMap<String, Object>> getTariffOptions(String serviceCode)throws Exception;
	ArrayList<HashMap<String, Object>> getSalesPackageInfo(String salesPacketCode)throws Exception;
}
