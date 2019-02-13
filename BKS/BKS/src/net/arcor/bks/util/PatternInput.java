/*******************************************************************************
* HEADER       : $Header:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/util/PatternInput.java-arc   1.0   Dec 17 2010 17:04:26   makuier  $   
* REVISION     : $Revision:   1.0  $
* MODULENAME   : $Workfile:   PatternInput.java  $
* PROJECT      : WOE
* AUTHOR       :
*
* DESCRIPTION  :
*
* HISTORY      : $Log:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/util/PatternInput.java-arc  $
 * 
 *    Rev 1.0   Dec 17 2010 17:04:26   makuier
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
 *    Rev 1.0   Aug 22 2003 16:21:04   mazdakr
 * Initial revision.
*
********************************************************************************/

package net.arcor.bks.util;

/**
 * This class is a implementation of IF_PatternInput. 
 * 
 * @author Maz Rashid
 */
public class PatternInput implements IF_PatternInput
{
	private char[] inpchars = null;
	private int iOffset = 0;
	private GenericPhoneticConverter pc = null;

	/* IF_PatternInput */
	public PatternInput(GenericPhoneticConverter pc, String sInp)
	{
		this.pc = pc;
		inpchars = sInp.toCharArray();
	}
	public boolean isSOL()
	{
		return iOffset==0;
	}
	public boolean isEOL(int patlen)
	{
		return size()==patlen;
	}
	public int size()
	{
		return inpchars==null?0:inpchars.length-iOffset;
	}
	public char getCharAt(int relpos)
	{
		char c = 0;
		int iAbsPos = iOffset+relpos;
		if(iAbsPos>=0 && iAbsPos<inpchars.length)
			c=inpchars[iAbsPos];
		return c;
	}
	
	public boolean isPermittedChar(char c)
	{
		return pc==null?false:pc.isPermittedChar(c);
	}
	/* End OF IF_PatternInput */
	
	public int getOffset()
	{
		return iOffset;
	}
	
	public void incOffset(int iD)
	{
		iOffset+=iD;
		if(iOffset<0)
			iOffset=0;
		if(iOffset>inpchars.length)
			iOffset=inpchars.length;
	}
}
