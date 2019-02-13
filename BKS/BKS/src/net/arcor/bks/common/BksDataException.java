package net.arcor.bks.common;

public class BksDataException extends Exception {

	String text;
	String code;
	
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public BksDataException() {
	}

	public BksDataException(String arg0) {
		super(arg0);
	}

	public BksDataException(String code,String text) {
		this.code=code;
		this.text=text;
	}

	public BksDataException(Throwable arg0) {
		super(arg0);
	}

	public BksDataException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
