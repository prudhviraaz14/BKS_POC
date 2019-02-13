/*******************************************************************************
* HEADER       : $Header:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/util/FudickarPhoneticConverter.java-arc   1.0   Dec 17 2010 17:04:22   makuier  $   
* REVISION     : $Revision:   1.0  $
* MODULENAME   : $Workfile:   FudickarPhoneticConverter.java  $
* PROJECT      : WOE
* AUTHOR       :
*
* DESCRIPTION  :
*
* HISTORY      : $Log:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/util/FudickarPhoneticConverter.java-arc  $
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
 * This class is an implementation of the phonetic converter according to the 
 * rules of the thesis of Stefanie Göttling based on an article in the
 * journal c't 10/1988
 * The basis PL/SQL-Storedprocedure was developed by Jens Fudickar.
 * 
 * @author Maz Rashid
 */
public class FudickarPhoneticConverter extends GenericPhoneticConverter
{

	protected void initPatterns()
	{
		/* E */
		addPattern(new Pattern("E(I,Y)","AY"));
		addPattern(new Pattern("EU","OY"));
		addPattern(new Pattern("^E","E"));
		addPattern(new Pattern("E$",""));
		addPattern(new Pattern("-(!,H)E","E"));
		
		// Not implemented
		//addPattern(new Pattern("ER$","A"));
		
		/* R */
		addPattern(new Pattern("R","R"));
		
		/* A */
		addPattern(new Pattern("^AE","E"));
		addPattern(new Pattern("-(!,H)AE","E"));
		addPattern(new Pattern("AE",""));
		addPattern(new Pattern("AR$","R"));
		addPattern(new Pattern("AR!","R",2));
		addPattern(new Pattern("-!A!","R",2));
		addPattern(new Pattern("A","A"));
		
		/* N,L */
		addPattern(new Pattern("N","N"));
		addPattern(new Pattern("L","L"));
		
		
		/* H */
		// This s a bad idea, as 
		// following rules has constructs like (!,H)x and will not match
		addPattern(new Pattern("H",""));
		
		/* S */
		addPattern(new Pattern("S(C,Z)","C"));
		addPattern(new Pattern("S","S"));
		
		/* T */
		addPattern(new Pattern("T(S,Z)","C"));
		addPattern(new Pattern("TT(S,Z)$","C"));
		addPattern(new Pattern("TT(S,Z)!","C",3));
		addPattern(new Pattern("T","D"));
		
		/* C */
		addPattern(new Pattern("CZ","C"));
		addPattern(new Pattern("CHS$","X"));
		addPattern(new Pattern("C","C"));
		
		/* K,Q */
		addPattern(new Pattern("KS","X"));
		addPattern(new Pattern("QU","KV"));
		addPattern(new Pattern("K","C"));
		addPattern(new Pattern("Q","C"));
		
		/* M */
		addPattern(new Pattern("M","N"));

		/* O,U,Ö */
		//addPattern(new Pattern("OE","Ö"));		
		addPattern(new Pattern("OE","O")); // TODO ERROR in Proc
		addPattern(new Pattern("UE","Y"));
		addPattern(new Pattern("O","O"));
		addPattern(new Pattern("U$","Y")); // TODO ERROR in Proc
		addPattern(new Pattern("U","U"));
		//addPattern(new Pattern("Ö","Ö")); // TODO Error in Proc
		
		/* D */
		addPattern(new Pattern("DS","C"));
		addPattern(new Pattern("DT(S,Z)$","C"));
		addPattern(new Pattern("DT(S,Z)!","C",3));
		addPattern(new Pattern("D","D"));
		
		/* B */
		addPattern(new Pattern("B","B"));
		
		/* G */
		addPattern(new Pattern("G","C"));
		
		/* P */
		addPattern(new Pattern("P(F,H)","V"));
		addPattern(new Pattern("P","B"));
		
		/* F,W,V */
		addPattern(new Pattern("(F,W,V)","V"));
		
		/* Z */
		addPattern(new Pattern("Z","C"));
		
		/* Ü,I,Y,J */
		//addPattern(new Pattern("(Ü,I,Y,J)","Y"));
		addPattern(new Pattern("(U,I,Y,J)","Y")); // TODO Error in Proc
		
		/* ß, SS */
		addPattern(new Pattern("SS","S"));
		
		/* Ä */
		//addPattern(new Pattern("^Ä","E"));  // TODO Error in Proc
		//addPattern(new Pattern("-(!,H)Ä","E"));  // TODO Error in Proc
		
		/* X */
		addPattern(new Pattern("X","X"));
	}
	
	public String convert(String s)
	{
		// The Fudickar-PLSQL-Implementation has an Bug regarding ß
		s = s.replace('ß',' ');
		return super.convert(s);
	}

	public boolean isPermittedChar(char c)
	{
		//return (c >= 'A' && c <= 'Z') || c=='Ä' || c=='Ö' || c=='Ü' || c=='ß';
		return (c >= 'A' && c <= 'Z');
	}

}
