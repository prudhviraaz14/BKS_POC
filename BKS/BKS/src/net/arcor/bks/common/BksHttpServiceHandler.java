package net.arcor.bks.common;

import javax.xml.soap.SOAPMessage;


public interface BksHttpServiceHandler {
	public String execute(SOAPMessage mes, String soapAction) throws Exception;
	public String getSoapaction();
}
