/*
    $Header:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/common/RefdataMapObject.java-arc   1.0   Nov 27 2007 14:39:32   makuier  $

    $Log:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/common/RefdataMapObject.java-arc  $
 * 
 *    Rev 1.0   Nov 27 2007 14:39:32   makuier
 * Initial revision.
 * 
 *    Rev 1.0   Nov 06 2007 17:35:20   makuier
 * Initial revision.
*/
package net.arcor.bks.common;

import java.util.HashMap;

public class RefdataMapObject {
	private String rdsID = null;
	private String rdsCategory = null;
	private String rdsOutput = null;
	private String attributePath = null;
	private String targetType = null;
	private HashMap<String,String> filter = new HashMap<String, String>();

	public String getAttributePath() {
		return attributePath;
	}
	public void setAttributePath(String attributePath) {
		this.attributePath = attributePath;
	}
	public HashMap<String, String> getFilter() {
		return filter;
	}
	public void setFilter(HashMap<String, String> filter) {
		this.filter = filter;
	}
	public String getRdsCategory() {
		return rdsCategory;
	}
	public void setRdsCategory(String rdsCategory) {
		this.rdsCategory = rdsCategory;
	}
	public String getRdsID() {
		return rdsID;
	}
	public void setRdsID(String rdsID) {
		this.rdsID = rdsID;
	}
	public String getRdsOutput() {
		return rdsOutput;
	}
	public void setRdsOutput(String rdsOutput) {
		this.rdsOutput = rdsOutput;
	}
	public String getTargetType() {
		return targetType;
	}
	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}
}
