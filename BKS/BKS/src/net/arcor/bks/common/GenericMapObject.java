/*
    $Header:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/common/GenericMapObject.java-arc   1.0   Nov 06 2007 17:35:20   makuier  $

    $Log:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/common/GenericMapObject.java-arc  $
 * 
 *    Rev 1.0   Nov 06 2007 17:35:20   makuier
 * Initial revision.
*/
package net.arcor.bks.common;

public class GenericMapObject {
	private String sourceServiceCharCode = null;
	private String sourceServiceCode = null;
	private String sourceColumn = null;
	private String sourceFieldNumber = null;
	private String targetFunction = null;
	private String targetAttribute = null;
	private String targetPath = null;
	private String defaultValue = null;
	private String MultipleOccurences = null;
	private String rdsCategory = null;

	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public String getMultipleOccurences() {
		return MultipleOccurences;
	}
	public void setMultipleOccurences(String multipleOccurences) {
		MultipleOccurences = multipleOccurences;
	}
	public String getRdsCategory() {
		return rdsCategory;
	}
	public void setRdsCategory(String rdsCategory) {
		this.rdsCategory = rdsCategory;
	}
	public String getSourceColumn() {
		return sourceColumn;
	}
	public void setSourceColumn(String sourceColumn) {
		this.sourceColumn = sourceColumn;
	}
	public String getSourceFieldNumber() {
		return sourceFieldNumber;
	}
	public void setSourceFieldNumber(String sourceFieldNumber) {
		this.sourceFieldNumber = sourceFieldNumber;
	}
	public String getSourceServiceCharCode() {
		return sourceServiceCharCode;
	}
	public void setSourceServiceCharCode(String sourceServiceCharCode) {
		this.sourceServiceCharCode = sourceServiceCharCode;
	}
	public String getSourceServiceCode() {
		return sourceServiceCode;
	}
	public void setSourceServiceCode(String sourceServiceCode) {
		this.sourceServiceCode = sourceServiceCode;
	}
	public String getTargetAttribute() {
		return targetAttribute;
	}
	public void setTargetAttribute(String targetAttribute) {
		this.targetAttribute = targetAttribute;
	}
	public String getTargetFunction() {
		return targetFunction;
	}
	public void setTargetFunction(String targetFunction) {
		this.targetFunction = targetFunction;
	}
	public String getTargetPath() {
		return targetPath;
	}
	public void setTargetPath(String targetPath) {
		this.targetPath = targetPath;
	}
}
