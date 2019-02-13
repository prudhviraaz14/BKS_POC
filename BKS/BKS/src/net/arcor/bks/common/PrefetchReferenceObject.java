/*
    $Header:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/common/PrefetchReferenceObject.java-arc   1.0   Nov 06 2007 17:35:20   makuier  $

    $Log:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/common/PrefetchReferenceObject.java-arc  $
 * 
 *    Rev 1.0   Nov 06 2007 17:35:20   makuier
 * Initial revision.
*/
package net.arcor.bks.common;

public class PrefetchReferenceObject {
	private String sourceServiceName = null;
	private String targetServiceName = null;
	private String targetPackage = null;
	private String sourceDataTag = null;
	private String targetDataTag = null;
	private String sourceDataObject = null;

	public String getSourceDataTag() {
		return sourceDataTag;
	}
	public void setSourceDataTag(String sourceDataTag) {
		this.sourceDataTag = sourceDataTag;
	}
	public String getSourceDataObject() {
		return sourceDataObject;
	}
	public void setSourceDataObject(String sourceObject) {
		this.sourceDataObject = sourceObject;
	}
	public String getSourceServiceName() {
		return sourceServiceName;
	}
	public void setSourceServiceName(String sourceServiceName) {
		this.sourceServiceName = sourceServiceName;
	}
	public String getTargetDataTag() {
		return targetDataTag;
	}
	public void setTargetDataTag(String targetDataTag) {
		this.targetDataTag = targetDataTag;
	}
	public String getTargetPackage() {
		return targetPackage;
	}
	public void setTargetPackage(String targetPackage) {
		this.targetPackage = targetPackage;
	}
	public String getTargetServiceName() {
		return targetServiceName;
	}
	public void setTargetServiceName(String targetServiceName) {
		this.targetServiceName = targetServiceName;
	}
}
