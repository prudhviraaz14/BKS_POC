package net.arcor.bks.db;

import java.util.ArrayList;
import java.util.HashMap;

public interface PruefeBestandskundeDAO extends BksDataAccessObject {

	ArrayList<HashMap<String, Object>> getCustomerByAccessNumber(String countryCode,
			String cityCode, String localNumber) throws Exception;

	ArrayList<HashMap<String, Object>> getCustomerByAddressName(
			String postalCode, String street, String streetNum,
			String numberSuffix, String rechtForm, String firmname) throws Exception;

	ArrayList<HashMap<String, Object>> getCustomerByAddress(
			String postalCode, String street, String streetNum,
			String numberSuffix, String theCustNumList) throws Exception;

	ArrayList<HashMap<String, Object>> getCustomerByName(String rechtForm,
			String firmname, String theCustNumList) throws Exception;

}
