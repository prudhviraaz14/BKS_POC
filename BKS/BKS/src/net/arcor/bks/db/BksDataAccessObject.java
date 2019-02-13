/*
    $Header:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/db/BksDataAccessObject.java-arc   1.8   Oct 17 2011 16:44:16   makuier  $

    $Log:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/db/BksDataAccessObject.java-arc  $
 * 
 *    Rev 1.8   Oct 17 2011 16:44:16   makuier
 * getDaoFortargetDB added.
 * 
 *    Rev 1.7   Dec 17 2010 16:24:42   makuier
 * getDataSourceName added
 * 
 *    Rev 1.6   May 27 2009 16:00:10   makuier
 * MCF2 adaption
 * 
 *    Rev 1.5   Feb 27 2008 11:24:02   makuier
 * refactoring.
 * 
 *    Rev 1.4   Feb 15 2008 15:27:22   makuier
 * InsertLog added.
 * 
 *    Rev 1.3   Dec 11 2007 19:06:44   makuier
 * Execute prepared query added.
 * 
 *    Rev 1.2   Dec 06 2007 18:03:20   makuier
 * connect functuion added
 * 
 *    Rev 1.1   Nov 27 2007 14:21:50   makuier
 * Closeconnetion added.
 * 
 *    Rev 1.0   Nov 06 2007 17:36:48   makuier
 * Initial revision.
*/
package net.arcor.bks.db;

import net.arcor.mcf2.exception.base.MCFException;

public interface BksDataAccessObject {
	public void connect(String preferedDataSource) throws MCFException;
	public void connectToTargetDb(String preferedDataSource) throws MCFException;
	void closeConnection() throws Exception;
	String getDataSourceName();
}
