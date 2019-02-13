/*
    $Header:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/BksServiceHandlerInterface.java-arc   1.3   May 27 2009 16:06:24   makuier  $

    $Log:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/requesthandler/BksServiceHandlerInterface.java-arc  $
 * 
 *    Rev 1.3   May 27 2009 16:06:24   makuier
 * MCF2 adaption
 * 
 *    Rev 1.2   Nov 27 2007 14:04:26   makuier
 * Correct the name 
 * 
 *    Rev 1.1   Nov 13 2007 16:20:14   makuier
 * Changes for 1.25
 * 
 *    Rev 1.0   Nov 06 2007 17:40:38   makuier
 * Initial revision.
*/
package net.arcor.bks.requesthandler;

import net.arcor.epsm_basetypes_001.InputData;
import net.arcor.epsm_basetypes_001.OutputData;


/**
 * @author MAKUIER
 *
 */
public interface BksServiceHandlerInterface {
	public String execute() throws Exception;
	public InputData getInput();
	public void setInput(InputData input);
	public OutputData getOutput();
	public void setOutput(OutputData output);
	public String getCustomerNumber();
	public void setCustomerNumber(String customerNumber);
}
