/*
    $Header:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/BksDataContainerObject.java-arc   1.0   Oct 29 2009 11:57:32   makuier  $

    $Log:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/BksDataContainerObject.java-arc  $
 * 
 *    Rev 1.0   Oct 29 2009 11:57:32   makuier
 * Initial revision.
 * 
 *    Rev 1.0   Nov 06 2007 17:40:38   makuier
 * Initial revision.
*/
package net.arcor.bks.requesthandler;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author MAKUIER
 *
 */
public class BksDataContainerObject {
	ArrayList<HashMap<String, Object>> theMapList;

	public BksDataContainerObject() {
		theMapList = new ArrayList<HashMap<String,Object>>();
	}

	public void putMap(HashMap<String, Object> theMap){
		theMapList.add(theMap);
	}
	public HashMap<String, Object> getMap(int i){
		return theMapList.get(i);
	}
	public int size(){
		return theMapList.size();
	}
}
