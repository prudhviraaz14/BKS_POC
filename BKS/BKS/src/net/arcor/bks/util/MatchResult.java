/*******************************************************************************
* HEADER       : $Header:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/util/MatchResult.java-arc   1.0   Dec 17 2010 17:04:26   makuier  $   
* REVISION     : $Revision:   1.0  $
* MODULENAME   : $Workfile:   MatchResult.java  $
* PROJECT      : WOE
* AUTHOR       :
*
* DESCRIPTION  :
*
* HISTORY      : $Log:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/util/MatchResult.java-arc  $
 * 
 *    Rev 1.0   Dec 17 2010 17:04:26   makuier
 * Initial revision.
 * 
 *    Rev 1.1   Oct 20 2004 09:03:30   Blaschkp
 * join WOE and WOE2
 * 
 *    Rev 1.0   Oct 07 2004 19:21:06   GRADO
 * Initial revision.
 * 
 *    Rev 1.0   Aug 22 2003 16:21:02   mazdakr
 * Initial revision.
*
********************************************************************************/

package net.arcor.bks.util;

/**
 * @author Maz Rashid
 */
public class MatchResult
{
	public String sConversionString = "";
	public int iNumConsumedChars = 0;
	
	public MatchResult(String sConv, int iNum)
	{
		sConversionString = sConv;
		iNumConsumedChars=iNum;
	}
}
