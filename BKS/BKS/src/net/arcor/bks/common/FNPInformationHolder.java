package net.arcor.bks.common;

public class FNPInformationHolder {
	String category;
	String relocationVariant;
	String invDeliveryType;
	boolean hasRelevantServices = false;
	boolean changeEndDate = true;
	boolean voice;
	public String getRelocationVariant() {
		return relocationVariant;
	}
	public void setRelocationVariant(String relocationVariant) {
		this.relocationVariant = relocationVariant;
	}
	public String getCategory() {
		return category;
	}
	public boolean isVoice() {
		return voice;
	}
	public void setVoice(boolean voice) {
		this.voice = voice;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public boolean hasRelevantServices() {
		return hasRelevantServices;
	}
	public void setHasRelevantServices(boolean hasRelevantServices) {
		this.hasRelevantServices = hasRelevantServices;
	}
	public boolean changeEndDate() {
		return changeEndDate;
	}
	public void setChangeEndDate(boolean changeEndDate) {
		this.changeEndDate = changeEndDate;
	}
	public String getInvDeliveryType() {
		return invDeliveryType;
	}
	public void setInvDeliveryType(String invDeliveryType) {
		this.invDeliveryType = invDeliveryType;
	}
	
}
