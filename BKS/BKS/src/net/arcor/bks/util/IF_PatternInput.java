/*******************************************************************************
* HEADER       : $Header:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/util/IF_PatternInput.java-arc   1.0   Dec 17 2010 17:04:24   makuier  $   
* REVISION     : $Revision:   1.0  $
* MODULENAME   : $Workfile:   IF_PatternInput.java  $
* PROJECT      : WOE
* AUTHOR       :
*
* DESCRIPTION  :
*
* HISTORY      : $Log:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/util/IF_PatternInput.java-arc  $
 * 
 *    Rev 1.0   Dec 17 2010 17:04:24   makuier
 * Initial revision.
 * 
 *    Rev 1.2   Oct 20 2004 09:03:30   Blaschkp
 * join WOE and WOE2
 * 
 *    Rev 1.0   Oct 07 2004 19:21:06   GRADO
 * Initial revision.
 * 
 *    Rev 1.1   Aug 25 2003 17:33:34   mazdakr
 * Bugfix and redesign for Factory-Facade
 * 
 *    Rev 1.0   Aug 22 2003 16:21:02   mazdakr
 * Initial revision.
*
********************************************************************************/

package net.arcor.bks.util;

/**
 * IF_PatternInput is used as an input to if_pattern implementations to determine a match.
 *  
 * @author Rashid
 */
public interface IF_PatternInput
{
	/*
	 * determines if the offset position is at the start of the line
	 */
	public boolean isSOL();
	
	/*
	 * determines if the offset position + the length of the pattern will 
	 * reach exactly the end of the line. 
	 */
	public boolean isEOL(int patlen);

	/*
	 * delivers the remaining size of the input.
	 */
	public int size();	
	
	/*
	 * delivers the char at the position offset + relpos.
	 * if the position exceeds the boudaries of the input the byte value 0 is returned.
	 */
	public char getCharAt(int relpos);
	
	/*
	 * determines if the given char is a permitted one or not 
	 */
	public boolean isPermittedChar(char c);
	
}

