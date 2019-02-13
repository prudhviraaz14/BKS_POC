/*******************************************************************************
* HEADER       : $Header:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/util/IF_Pattern.java-arc   1.0   Dec 17 2010 17:04:24   makuier  $   
* REVISION     : $Revision:   1.0  $
* MODULENAME   : $Workfile:   IF_Pattern.java  $
* PROJECT      : WOE
* AUTHOR       :
*
* DESCRIPTION  :
*
* HISTORY      : $Log:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/util/IF_Pattern.java-arc  $
 * 
 *    Rev 1.0   Dec 17 2010 17:04:24   makuier
 * Initial revision.
 * 
 *    Rev 1.1   Oct 20 2004 09:03:28   Blaschkp
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
 * This interface represents a Pattern for the pattern matcher.
 * As patterns resolver could be implemented in different kinds
 * 
 * @author Rashid
 *
 */
public interface IF_Pattern
{
	/*
	 * tries to match the input to the given input and only i nthis case return the 
	 * replacement string as part of the result. If no math is given a null 
	 * will be returned.
	 */
	public MatchResult match(IF_PatternInput inp);

}
