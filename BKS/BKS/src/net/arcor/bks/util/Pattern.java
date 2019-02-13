/*******************************************************************************
* HEADER       : $Header:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/util/Pattern.java-arc   1.0   Dec 17 2010 17:04:26   makuier  $   
* REVISION     : $Revision:   1.0  $
* MODULENAME   : $Workfile:   Pattern.java  $
* PROJECT      : WOE
* AUTHOR       :
*
* DESCRIPTION  :
*
* HISTORY      : $Log:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/util/Pattern.java-arc  $
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
 * This class represents a pattern for a phonetic conversion. A pattern will 
 * be constructed by a pattern-String and it's conversion-String.
 * The Pattern-String may contain the following meta data:
 * - ^ means the beginning of the word
 * - $ menas the end of the word
 * - - at the beginning means match with one char in prev
 * - ! means not permitted char
 * - + means peritted char
 * - . means any char
  * - (x,c,..) means different variants
 * 
 * The whole pattern is converted to Uppercase.
 * 
 * Samples:
 *  ^E(I,Y) matches all words beginning with EI or EY
 *  (!,H)E matches all E with a previous not permitted chrachter or H
 * 
 * To check the match-criteria the matches-method is called periodicaly.
 * The matches method returns the conversion string and the amount of
 * charachters that should be consumed 
 *  
 * @author Maz Rashid
 *
 */
public class Pattern implements IF_Pattern
{
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Pattern.class);
	
	protected String sPatternName = "";
	protected String sPattern = "";
	protected String sReplStr = "";
	
	protected boolean matchesSOL=false;
	protected boolean matchesEOL=false;
	protected boolean matchesPrev=false;
	protected int iPatternLen = 0;
	protected java.util.List lPatterns = new java.util.ArrayList(); 	
	protected java.util.List lPatternsCharArray = new java.util.ArrayList();

	protected MatchResult matchedResult= null;
	
	public Pattern(String sP, String sReplace, int ixPatlen)
	{
		sReplStr=sReplace.toUpperCase();
		
		// parse the pattern
		parse(sP.toUpperCase());
		
		// determine pattern len
		iPatternLen = ixPatlen>0?ixPatlen:lPatterns.get(0).toString().length()-(matchesPrev?1:0);
		
		//setup only one matchResult and use it for all matches
		matchedResult= new MatchResult(sReplStr,iPatternLen);
		
		// create CharArray
		for(int i=0; i<lPatterns.size(); i++)
			lPatternsCharArray.add(lPatterns.get(i).toString().toCharArray());
		
		if(log.isDebugEnabled())
		{
			log.debug("Pattern '" + sP + "' expanded to " + lPatterns.size() + " subpatterns");
			for(int i=0; i<lPatterns.size(); i++)
				log.debug("Subpattern: " + i + ": " + lPatterns.get(i) + " -> " + sReplStr);
			log.debug("isSOL: " + matchesSOL + ", isEOL: " + matchesEOL + ", patlen: " + iPatternLen);
		}
	}
	
	public Pattern(String sP, String sReplace)
	{
		this(sP,sReplace,0);
	}


	private void parse(String sP)
	{
		sPattern=sP;
		sPatternName=sP + " -> " + sReplStr;
		
		if(sP.equals(""))
			throw new IllegalArgumentException("A Pattern must have a valid Pattern. The given pattern is empty.");
		
		// is SOL, EOL?
		if(sP.startsWith("^"))
			matchesSOL=true;
		if(sP.endsWith("$"))
			matchesEOL=true;
		if(sP.startsWith("-"))
			matchesPrev=true;
		if(matchesSOL || matchesEOL || matchesPrev)
			sP = sP.substring(matchesSOL||matchesPrev?1:0,matchesEOL?sP.length()-1:sP.length());
			
		if(sP.equals(""))
			throw new IllegalArgumentException("It is not enough to define Start or End Matcher.");
		
		lPatterns.add(sP);
		
		//expand the Patterns 
		if(sP.indexOf("(")>=0)
		{
			for(int i=0; i< lPatterns.size(); i++)
			{
				String sxP = lPatterns.get(i).toString();
				log.debug("Expand " + i + ": " + sxP);
				
				int idx1 = sxP.indexOf("(");
				int idx2 = sxP.indexOf(")");
				if((idx1>=0&&idx2<0) || (idx1<0&&idx2>=0))
					throw new IllegalArgumentException("Pattern '" + sP + "' has unmatched bracket.");
					
				if(idx1<0)
					continue;
				
				// remove pattern
				lPatterns.remove(i--);
				
				String sVariants = sxP.substring(idx1+1,idx2);
				String sxpStart=sxP.substring(0,idx1);
				String sxpEnd=(idx2+1)<sxP.length()?sxP.substring(idx2+1,sxP.length()):"";
				
				java.util.List lVariants=StrUtil.split(sVariants,",");
				for(int j=0; j< lVariants.size(); j++)
				{
					String sResP = sxpStart + lVariants.get(j) + sxpEnd;
					log.debug("Expanding " + sxP + " to " + sResP);
					lPatterns.add(sResP);
				}
			}
		}
	}

	public String toString()
	{
		return sPatternName;
	}	

	public MatchResult match(IF_PatternInput inp)
	{
		if(matchesPrev && inp.isSOL())
			return null;
			
		if(matchesSOL && !inp.isSOL())
			return null;
			
		if(matchesEOL && !inp.isEOL(iPatternLen))
			return null;
			
		if(inp.size() < iPatternLen)
			return null;

		// check patterns
		for(int patnr=0; patnr< lPatternsCharArray.size(); patnr++)
		{
			char[] pc = (char[])lPatternsCharArray.get(patnr);
			boolean isMatch = true;
			 
			
			//log.debug("check with pattern " + patnr + ": " + lPatterns.get(patnr));
			
			for(int i=0; i< pc.length; i++)
			{
				char cpat = pc[i];
				char cinp = inp.getCharAt(i+(matchesPrev?-1:0));
				
				if(cinp==0)
					return null;
					
				boolean isCharMatch = cpat==cinp || cpat=='.' || (cpat=='!' && !inp.isPermittedChar(cinp)) ||  (cpat=='+' && inp.isPermittedChar(cinp));
				//log.debug("  cpat='" + cpat + "' cinp='" + cinp + "' matched: " + isCharMatch);
				if(!isCharMatch)
				{
					isMatch=false;
					break;
				}
			}
			
			// We got it
			if(isMatch)
			{
				if(log.isDebugEnabled())
				{
					String s = "";
					for(int i=0; i< pc.length;i++)
						s+=inp.getCharAt(i);
					log.debug("matched '" + s + "' by patnr " + patnr + " from " + toString());
				}
				return matchedResult;
			}
		}			
			
		return null;
	}
}
