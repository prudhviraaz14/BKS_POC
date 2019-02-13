/*******************************************************************************
* HEADER       : $Header:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/util/GenericPhoneticConverter.java-arc   1.0   Dec 17 2010 17:04:22   makuier  $   
* REVISION     : $Revision:   1.0  $
* MODULENAME   : $Workfile:   GenericPhoneticConverter.java  $
* PROJECT      : WOE
* AUTHOR       :
*
* DESCRIPTION  :
*
* HISTORY      : $Log:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/util/GenericPhoneticConverter.java-arc  $
 * 
 *    Rev 1.0   Dec 17 2010 17:04:22   makuier
 * Initial revision.
 * 
 *    Rev 1.2   Oct 20 2004 09:03:28   Blaschkp
 * join WOE and WOE2
 * 
 *    Rev 1.0   Oct 07 2004 19:21:04   GRADO
 * Initial revision.
 * 
 *    Rev 1.1   Aug 25 2003 17:33:32   mazdakr
 * Bugfix and redesign for Factory-Facade
 * 
 *    Rev 1.0   Aug 22 2003 16:21:00   mazdakr
 * Initial revision.
*
********************************************************************************/

package net.arcor.bks.util;

/**
 * This class is an abstract implementation of a converter. It is helpfull to 
 * implement different converting implementation with different patterns but using 
 * the same basis.
 * 
 * @author Maz Rashid
 *
 */
public abstract class GenericPhoneticConverter
{
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(GenericPhoneticConverter.class);
	
	abstract protected void initPatterns();
	
	protected boolean isDeleteDoubles()
	{
		return true;
	}
	
	private java.util.List lPatterns = new java.util.ArrayList(); 
	protected void addPattern(IF_Pattern p)
	{
		log.debug("insert Pattern: " + p);
		lPatterns.add(p);
	}
	
	public GenericPhoneticConverter()
	{
		try
		{
			initPatterns();
			log.info("initialized " + lPatterns.size() + " Patterns.");
		}
		catch(Exception e)
		{
			log.error("Could not initialize the patterns",e);
		}
	}
	
	public String convert(String sInp)
	{
		log.debug("convert " + sInp);
		
		String sUpperInp = sInp.toUpperCase();
		
		PatternInput pinp = new PatternInput(this,sUpperInp);
		StringBuffer sbOut= new StringBuffer(); 
		
		while(pinp.size()>0)
		{
			//log.debug("InpChar: '" + pinp.getCharAt(0) + "' size:" + pinp.size() + ", Offs:" + pinp.getOffset());
			
			int iIncStep = 1;
			for(int patnr=0; patnr < lPatterns.size(); patnr++)
			{
				IF_Pattern pat = (IF_Pattern)lPatterns.get(patnr);
				//log.debug("Pattern " + patnr + ": " + pat);
				
				MatchResult mr = null;
				
				// call the pattern-match
				try
				{
					mr = pat.match(pinp);
				}
				catch(Exception e)
				{
					log.error("Error while matching pattern: " + pat);
				}
				
				if(mr!=null)
				{
					// matched
					log.debug("Matched by " + pat);
					iIncStep=mr.iNumConsumedChars;
					sbOut.append(mr.sConversionString);
					break;
				}
			}
			
			pinp.incOffset(iIncStep<=0?1:iIncStep);
		}
		
		if(isDeleteDoubles())
		{
			log.debug("Delete double chars");
			for(int i=1; i< sbOut.length(); i++)
			{
				char c0 = sbOut.charAt(i-1);
				char c1 = sbOut.charAt(i);
				if(c0==c1)
					sbOut.deleteCharAt(i--);
			}
		}
		
		String sOut = sbOut.toString();
		if(sOut.trim().equals(""))
			sOut = sInp;
			
		log.debug("converted: " + sInp + " -> " + sOut);
		return sOut;
	}
	
	public boolean isPermittedChar(char c)
	{
		return (c >= 'A' && c <= 'Z') || c=='Ä' || c=='Ö' || c=='Ü' || c=='ß';
	}

}
