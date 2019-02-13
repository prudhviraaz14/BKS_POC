/*
    $Header:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/PrefetchVertriebspaketDetailsHandler.java-arc   1.3   Jan 28 2010 16:51:22   makuier  $

    $Log:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/PrefetchVertriebspaketDetailsHandler.java-arc  $
 * 
 *    Rev 1.3   Jan 28 2010 16:51:22   makuier
 * Cache the exception and return an error XML.
 * 
 *    Rev 1.2   Jan 14 2009 16:19:42   wlazlow
 * SPN-BKS-000081684
 * 
 *    Rev 1.1   Dec 04 2008 12:13:28   makuier
 * Adapted to new AAW
 * 
 *    Rev 1.0   Nov 27 2008 17:50:18   wlazlow
 * Initial revision.
 * 
 *    Rev 1.0   Nov 25 2008 14:21:32   makuier
 * Initial revision.
 * 
 */
package net.arcor.bks.requesthandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import net.arcor.bks.client.DatabaseClient;
import net.arcor.bks.common.BksDataException;
import net.arcor.bks.db.PrefetchVertriebspaketDetailsDAO;
import net.arcor.epsm_commontypes_kontaktrufnummer_001.Kontaktrufnummer;
import de.arcor.aaw.kernAAW.bks.services.HoleVertriebspaketDetailsInput;
import de.arcor.aaw.kernAAW.bks.services.PrefetchVertriebspaketDetailsInput;

/**
 * @author MAKUIER
 * 
 */
public class PrefetchVertriebspaketDetailsHandler extends BaseServiceHandler {

	HashMap<String, Object> returnList = new HashMap<String, Object>();
	String[] errorParams = null;
	boolean isServiceRelevant = true;

	public PrefetchVertriebspaketDetailsHandler() {
		super();
	}

	public String execute() throws Exception {
		String returnXml = null;
		PrefetchVertriebspaketDetailsDAO dao = (PrefetchVertriebspaketDetailsDAO) DatabaseClient
				.getBksDataAccessObject(null,
						"PrefetchVertriebspaketDetailsDAO");

		try {
			String customerNumber = ((PrefetchVertriebspaketDetailsInput) input)
					.getKundennummer();

			ArrayList<HashMap<String, Object>> accesNumList = null;

			if (customerNumber != null)
				accesNumList = dao.getAccessNumbers(customerNumber);

			if (accesNumList != null && accesNumList.size() > 0) {
				Iterator anIter = accesNumList.iterator();

				while (anIter.hasNext()) {
					HashMap<String, Object> anRow = null;
					anRow = (HashMap<String, Object>) anIter.next();
					
					String tmpAccessNumber = null;
					if(anRow.get("ACCESS_NUMBER") != null)						
					     tmpAccessNumber = anRow.get("ACCESS_NUMBER").toString();
					
					if (tmpAccessNumber != null) {
						String[] tmp = tmpAccessNumber.split(";");
						if (tmp.length >= 3) {
							String countryCode = "00" + tmp[0];
							String cityCode = "0" + tmp[1];
							String localNumber = tmp[2];

							Kontaktrufnummer krn = new Kontaktrufnummer();
							krn.setLaenderkennzeichen(countryCode);
							krn.setVorwahl(cityCode);
							krn.setRufnummer(localNumber);
							HoleVertriebspaketDetailsHandler holeVeDetailsHand = new HoleVertriebspaketDetailsHandler();
							HoleVertriebspaketDetailsInput holeVeDetailsInput = new HoleVertriebspaketDetailsInput();
							holeVeDetailsInput.setRufnummer(krn);
							holeVeDetailsHand.setInput(holeVeDetailsInput);
							holeVeDetailsHand.execute();
						}
					}
				}
			} else {
				serviceStatus = Status.FUNCTIONAL_ERROR;
				errorText = "No customer found";
				errorCode = "BKS0000";
			}

		} catch (BksDataException e) {
			logger.error(e.getMessage());
			serviceStatus = Status.PRECONDITION_ERROR;
			errorCode = "BKS0008";
			errorText = e.getMessage();

		} catch (Exception e) {
			logger.error(e.getMessage());
			serviceStatus = Status.PRECONDITION_ERROR;
			errorCode = "BKS0008";
			errorText = e.getMessage();
		} finally {
			dao.closeConnection();
		}
		return null;
	}

}
