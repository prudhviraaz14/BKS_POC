/*
    $Header:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/common/MasterMapObject.java-arc   1.1   Nov 27 2007 13:59:02   makuier  $

    $Log:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/common/MasterMapObject.java-arc  $
 * 
 *    Rev 1.1   Nov 27 2007 13:59:02   makuier
 * Bug Fixes
 * 
 *    Rev 1.0   Nov 06 2007 17:35:20   makuier
 * Initial revision.
*/
package net.arcor.bks.common;

public class MasterMapObject {
	private String identifier = null;
	private String targetAttributePath = null;

	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	public String getTargetAttributePath() {
		return targetAttributePath;
	}
	public void setTargetAttributePath(String targetAttributePath) {
		this.targetAttributePath = targetAttributePath;
	}

}
