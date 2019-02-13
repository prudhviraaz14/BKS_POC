/*
    $Header:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/GetFNPAddressByIdImpl.java-arc   1.1   Aug 05 2010 11:34:50   makuier  $

    $Log:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/GetFNPAddressByIdImpl.java-arc  $
 * 
 *    Rev 1.1   Aug 05 2010 11:34:50   makuier
 * Raise error if no address found.
 * 
 *    Rev 1.0   Jul 01 2010 16:14:32   makuier
 * Initial revision.
 * 
 *    Rev 1.8   Jun 25 2010 14:11:02   makuier
 * Access numbers for open order supported.
 * 
 *    Rev 1.7   Jun 23 2010 16:50:14   makuier
 * Support for open orders added.
 * 
 *    Rev 1.6   Jun 08 2010 16:32:28   makuier
 * Handle the case when article number does not have a type.
 * 
 *    Rev 1.5   Jun 04 2010 15:12:48   makuier
 * DSL-R added.
 * 
 *    Rev 1.4   Jun 02 2010 13:24:16   makuier
 * Added support for V0004
 * 
 *    Rev 1.3   May 28 2010 14:12:08   makuier
 * Corrected the access number fetch
 * 
 *    Rev 1.2   May 25 2010 14:36:34   makuier
 * Adapted to new configuration for 1b
 * 
 *    Rev 1.1   May 17 2010 17:03:48   makuier
 * CPCOM 1b
 * 
 *    Rev 1.0   Apr 30 2010 15:07:12   makuier
 * Initial revision.
 * 
 *    Rev 1.2   Apr 14 2010 18:05:04   makuier
 * Adapted to changes in MCF.
 * Changes for CPCOM 1a
 * 
 *    Rev 1.1   Mar 19 2010 15:49:14   makuier
 * duplicated the code as different name spaces are used.
 * 
 *    Rev 1.0   Feb 04 2010 11:49:28   makuier
 * Initial revision.
*/
package net.arcor.bks.requesthandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.arcor.bks.client.DatabaseClient;
import net.arcor.bks.client.DatabaseClientConfig;
import net.arcor.bks.common.BksDataException;
import net.arcor.bks.common.BksHelper;
import net.arcor.bks.common.BksRefDataCacheHandler;
import net.arcor.bks.db.GetFNPAddressByIdDAO;
import net.arcor.mcf2.model.ServiceObjectEndpoint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.vodafone.mcf2.ws.exception.FunctionalException;
import com.vodafone.mcf2.ws.exception.FunctionalRuntimeException;
import com.vodafone.mcf2.ws.exception.TechnicalException;
import com.vodafone.mcf2.ws.model.impl.ServiceResponseSoap;
import com.vodafone.mcf2.ws.service.SoapEndpoint;
import com.vodafone.mcf2.ws.service.SoapOperation;

import de.vodafone.esb.schema.common.sidcom_masterdata_001.Address;
import de.vodafone.esb.service.sbus.bks.bks_003.GetFNPAddressByIdRequest;
import de.vodafone.esb.service.sbus.bks.bks_003.GetFNPAddressByIdResponse;

@SoapEndpoint(soapAction = "/BKS-003/GetFNPAddressById", context = "de.vodafone.esb.service.sbus.bks.bks_003", schema = "classpath:schema/BKS-003.xsd")
public class GetFNPAddressByIdImpl implements SoapOperation<GetFNPAddressByIdRequest, GetFNPAddressByIdResponse> {

    /** The logger. */
    private final static Log log = LogFactory.getLog(GetFNPAddressByIdImpl.class);
	private String errorText;
	private String errorCode;
	private String customerNumber = new String();
	BksHelper theHelper = null;
	String category = null;
	/**
	 * The operation "GetFNPAddressById" of the service "GetFNPAddressById".
	 */
	public ServiceResponseSoap<GetFNPAddressByIdResponse> invoke(ServiceObjectEndpoint<GetFNPAddressByIdRequest> serviceObject) 
			throws TechnicalException,FunctionalException,FunctionalRuntimeException {
		int tries =0;
		int maxTries = 10;
		theHelper = new BksHelper();
		try {
			maxTries = Integer.parseInt(DatabaseClientConfig.getSetting("databaseclient.MaxRetries"));
		} catch (Exception e1) {
			// Ignore (Use Default)
		}
        while (!BksRefDataCacheHandler.isRefdataInitialized() && tries < maxTries){
        	tries++;
        	try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
        }
        if (!BksRefDataCacheHandler.isRefdataInitialized()){
        	log.error("The application is not initialized after max. number of tries ( "+ tries +").");
        	log.error("Please increase the value of databaseclient.MaxRetries and restart the application.");
        	System.exit(0);
        }
		GetFNPAddressByIdDAO dao = (GetFNPAddressByIdDAO) DatabaseClient.getBksDataAccessObject(null, "GetFNPAddressByIdDAO");
		GetFNPAddressByIdRequest request = serviceObject.getPayload();
		GetFNPAddressByIdResponse output = new GetFNPAddressByIdResponse();
		try {
			customerNumber = request.getCustomerNumber();
			String bundleId = request.getBundleId();
			if(customerNumber != null)
				output = getAddressesForCustomer(dao,customerNumber);
			else if (bundleId != null)
				output = getAddressesForBundle(dao,bundleId);
		} catch (BksDataException e) {
			log.error(e.getMessage());
			throw new FunctionalException(errorCode,errorText);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new TechnicalException("BKSCOM-ERROR-000008",e.getMessage(),e);
		} finally {
			try {
				dao.closeConnection();
			} catch (Exception e) {
			}
		}
		return new ServiceResponseSoap<GetFNPAddressByIdResponse>(output);
	}
	private GetFNPAddressByIdResponse getAddressesForBundle(GetFNPAddressByIdDAO dao, String bundleId) throws Exception {
		GetFNPAddressByIdResponse resp = new GetFNPAddressByIdResponse();
		ArrayList<HashMap<String, Object>> addrRows =  dao.getAddressesForBundle(bundleId);
		if (addrRows==null || addrRows.size() == 0){
			errorText = "No address found for bundle "+bundleId;
			errorCode = "BKSCOM-ERROR-000008";
			throw new BksDataException();
		}
		populateAddressList(addrRows,resp);
		return resp;
	}
	private GetFNPAddressByIdResponse getAddressesForCustomer(GetFNPAddressByIdDAO dao, String customerNumber) throws Exception {
		GetFNPAddressByIdResponse resp = new GetFNPAddressByIdResponse();
		ArrayList<HashMap<String, Object>> addrRows =  dao.getAddressesForCustomer(customerNumber);
		if (addrRows==null || addrRows.size() == 0){
			errorText = "No address found for customer "+customerNumber;
			errorCode = "BKSCOM-ERROR-000008";
			throw new BksDataException();
		}
		populateAddressList(addrRows,resp);
		return resp;
	}
	private void populateAddressList(ArrayList<HashMap<String, Object>> addrRows,
									GetFNPAddressByIdResponse resp) {
		List<Address> addresses = resp.getAddresses();
		for (int i=0;addrRows!=null&&i<addrRows.size();i++){
			Address address = new Address();
			address.setCity((String)addrRows.get(i).get("CITY_NAME"));
			address.setCountryCode((String)addrRows.get(i).get("COUNTRY_RD"));
			address.setStreetNumber((String)addrRows.get(i).get("STREET_NUMBER"));
			address.setStreetNumberSuffix((String)addrRows.get(i).get("STREET_NUMBER_SUFFIX"));
			address.setAdditionalAddressDescription((String)addrRows.get(i).get("ADDITIONAL_ADDRESS_DESCRIPTION"));
			address.setSecondAdditionalAddressDescription((String)addrRows.get(i).get("SECOND_ADD_ADDRESS_DESCRIPTION"));
			address.setCitySuffixName((String)addrRows.get(i).get("CITY_SUFFIX_NAME"));
			address.setPostalCode((String)addrRows.get(i).get("POSTAL_CODE"));
			//address.setDistrict((String)addrRows.get(i).get("PROVINCE_NAME"));
			address.setStreet((String)addrRows.get(i).get("STREET_NAME"));
			address.setPostOfficeBox((String)addrRows.get(i).get("POST_OFFICE_BOX"));

			addresses.add(address);
		}
	}

}
