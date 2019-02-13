/*
    $Header:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/GetFNPCustomerHierarchyNGCSDataImpl.java-arc   1.6   Jul 31 2014 12:32:08   makuier  $

    $Log:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/GetFNPCustomerHierarchyNGCSDataImpl.java-arc  $
 * 
 *    Rev 1.6   Jul 31 2014 12:32:08   makuier
 * Filter out the customers which cause parsing error.
 * 
 *    Rev 1.5   Oct 12 2012 13:58:38   makuier
 * Added V0012 to the list of services.
 * 
 *    Rev 1.4   Apr 19 2012 10:24:30   makuier
 * Wild card match in address.
 * 
 *    Rev 1.3   Mar 14 2012 15:59:46   makuier
 *  Raise error when all customers are filtered out.
 * 
 *    Rev 1.2   Mar 07 2012 11:53:36   makuier
 * Adapt to XSD change.
 * 
 *    Rev 1.1   Feb 23 2012 09:29:38   makuier
 * performance tuning
 * 
 *    Rev 1.0   Feb 22 2012 15:03:48   makuier
 * Initial revision.
*/
package net.arcor.bks.requesthandler;

import java.util.ArrayList;
import java.util.HashMap;

import net.arcor.bks.client.DatabaseClient;
import net.arcor.bks.client.DatabaseClientConfig;
import net.arcor.bks.common.BksDataException;
import net.arcor.bks.common.BksHelper;
import net.arcor.bks.common.BksRefDataCacheHandler;
import net.arcor.bks.common.FNPInformationHolder;
import net.arcor.bks.db.GetFNPCustomerHierarchyNGCSDataDAO;
import net.arcor.mcf2.model.ServiceObjectEndpoint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.vodafone.mcf2.ws.exception.FunctionalException;
import com.vodafone.mcf2.ws.exception.FunctionalRuntimeException;
import com.vodafone.mcf2.ws.exception.TechnicalException;
import com.vodafone.mcf2.ws.model.impl.ServiceResponseSoap;
import com.vodafone.mcf2.ws.service.SoapEndpoint;
import com.vodafone.mcf2.ws.service.SoapOperation;

import de.vodafone.esb.schema.common.sidcom_getcustomerinfo_001.CustomerInformation;
import de.vodafone.esb.service.sbus.bks.bks_006.GetFNPCustomerHierarchyNGCSDataRequest;
import de.vodafone.esb.service.sbus.bks.bks_006.GetFNPCustomerHierarchyNGCSDataResponse;

@SoapEndpoint(soapAction = "/BKS-006/GetFNPCustomerHierarchyNGCSData", context = "de.vodafone.esb.service.sbus.bks.bks_006", schema = "classpath:schema/BKS-006.xsd")
public class GetFNPCustomerHierarchyNGCSDataImpl implements SoapOperation<GetFNPCustomerHierarchyNGCSDataRequest, GetFNPCustomerHierarchyNGCSDataResponse> {

    /** The logger. */
    private final static Log log = LogFactory.getLog(GetFNPCustomerHierarchyNGCSDataImpl.class);

	/**
	 * The operation "GetFNPCustomerHierarchyNGCSData" of the service "GetFNPCustomerHierarchyNGCSData".
	 */
	public ServiceResponseSoap<GetFNPCustomerHierarchyNGCSDataResponse> invoke(ServiceObjectEndpoint<GetFNPCustomerHierarchyNGCSDataRequest> serviceObject) 
			throws TechnicalException,FunctionalException,FunctionalRuntimeException {
		int tries =0;
		int maxTries = 10;
		BksHelper theHelper = new BksHelper();
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
		GetFNPCustomerHierarchyNGCSDataDAO dao = (GetFNPCustomerHierarchyNGCSDataDAO) DatabaseClient.getBksDataAccessObject(null, "GetFNPCustomerHierarchyNGCSDataDAO");
		GetFNPCustomerHierarchyNGCSDataRequest request = serviceObject.getPayload();
		GetFNPCustomerHierarchyNGCSDataResponse output = new GetFNPCustomerHierarchyNGCSDataResponse();
		try {
			output = getSIDCustomerData(dao,request, theHelper);
		} catch (BksDataException e) {
			log.error(e.getText());
			throw new FunctionalException(e.getCode(),e.getText());
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new TechnicalException("BKSCOM-ERROR-000008","Precondition check error",e);
		} finally {
			try {
				dao.closeConnection();
			} catch (Exception e) {
			}
		}

		
		return new ServiceResponseSoap<GetFNPCustomerHierarchyNGCSDataResponse>(output);
	}

	private GetFNPCustomerHierarchyNGCSDataResponse getSIDCustomerData(
			GetFNPCustomerHierarchyNGCSDataDAO dao, GetFNPCustomerHierarchyNGCSDataRequest request,BksHelper theHelper) throws Exception {
		GetFNPCustomerHierarchyNGCSDataResponse resp = new GetFNPCustomerHierarchyNGCSDataResponse();
		resp.setSidcomVersion("1.0");
	    String requestType = request.getRequestType();
		FNPInformationHolder infoHolder = new FNPInformationHolder();
		ArrayList<HashMap<String, Object>> custList = getHierarchyCustomerNumbers(dao, request,infoHolder,requestType, theHelper);
		for (int j = 0; j < custList.size(); j++) {
			GetFNPCustomerNGCSData6Impl gcdHnd = new GetFNPCustomerNGCSData6Impl();
			String custNo = (String)custList.get(j).get("CUSTOMER_NUMBER");
			String parentCustNo = (String)custList.get(j).get("ASSOCIATED_CUSTOMER_NUMBER");
			boolean bValid=true;
			ArrayList<HashMap<String, Object>> addresses;
			if(request.getPersonData()!=null){
				addresses = dao.getPrimaryAddress(custNo);
				HashMap<String, Object> addr = null;
				if (addresses.size()>0)
						addr=addresses.get(0);
				bValid=checkAddress(addr,request.getPersonData().getZipCode(),
						request.getPersonData().getStreet(),
						request.getPersonData().getHousenumber(),
						request.getPersonData().getHousenumberSuffix());
			}
			if (request.getCompanyData()!=null){
				addresses = dao.getPrimaryAddress(custNo);
				HashMap<String, Object> addr = null;
				if (addresses.size()>0)
						addr=addresses.get(0);
				bValid=checkAddress(addr,request.getCompanyData().getZipCode(),
						request.getCompanyData().getStreet(),
						request.getCompanyData().getHousenumber(),
						request.getCompanyData().getHousenumberSuffix());
			}
			if (bValid){
				CustomerInformation ci = gcdHnd.getSidForCustomerNumber(custNo,request.getRequestType());
				if (ci != null) {
					ci.getCustomerInformationData().setCustomerNumber(custNo);
					ci.getCustomerInformationData().setParentCustomerNumber(parentCustNo);
					ci.setCustomerInformationStatus(null);
					resp.getSid().add(ci);
				}
			}
		}
		
		if (resp.getSid().size() < 1) {
			String errorText = "No customer found";
			String errorCode = "BKSCOM-ERROR-000002";
			throw new BksDataException(errorCode,errorText);
		}
		
		return resp;
	}
	private boolean checkAddress(HashMap<String, Object> address,String postalCode,
			String street,String streetNo,String numberSuffix) {
		String dbPostalCode = (String) address.get("POSTAL_CODE");
		String dbStreet = (String) address.get("STREET_NAME");
		if (street!=null && dbStreet==null)
			return false;
		dbStreet = dbStreet.toUpperCase();
		if (street!=null && street.endsWith("*"))
			street = street.substring(0,street.length()-1);
		if ((postalCode == null || dbPostalCode.startsWith(postalCode)) &&
			(street == null || dbStreet.startsWith(street.toUpperCase())) &&
			(streetNo == null || streetNo.equals(address.get("STREET_NUMBER"))) &&
			(numberSuffix == null || numberSuffix.equals(address.get("STREET_NUMBER_SUFFIX"))))
			return true;
		return false;
	}

	ArrayList<HashMap<String, Object>> getHierarchyCustomerNumbers(GetFNPCustomerHierarchyNGCSDataDAO dao,GetFNPCustomerHierarchyNGCSDataRequest request, FNPInformationHolder infoHolder, String requestType, BksHelper theHelper) throws Exception{
		ArrayList<HashMap<String, Object>> custList = null;
		if (request.getCustomerNumber() != null) {
			custList = dao.getChildCustomers(request.getCustomerNumber(),request.getNestingLevel());
		}else {
			String errorText = "Wrong input setting";
			String errorCode = "BKSCOM-ERROR-000002";
			throw new BksDataException(errorCode,errorText);
		}  

		if ((custList == null) || (custList.size() < 1)) {
			String errorText = "No customer found";
			String errorCode = "BKSCOM-ERROR-000002";
			throw new BksDataException(errorCode,errorText);
		}
		
		ArrayList<String> custNumberList = new ArrayList<String>();
		for (int i = 0; i < custList.size(); i++) {
			String custNum = (String) custList.get(i).get("CUSTOMER_NUMBER");
			if (custNumberList.contains(custNum)){
				custList.remove(i);
				i--;
				continue;
			} else
				custNumberList.add(custNum);
		}

		return custList;
	}
}
