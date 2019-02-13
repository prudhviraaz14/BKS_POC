/*******************************************************************************
* HEADER       : $Header:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/util/StrUtil.java-arc   1.0   Dec 17 2010 17:04:28   makuier  $
* REVISION     : $Revision:   1.0  $
* MODULENAME   : $Workfile:   StrUtil.java  $
* PROJECT      : WOE
* AUTHOR       :
*
* DESCRIPTION  :
*
* HISTORY      : $Log:   //PVCS_BKS/archives/BKS/BKS/src/net/arcor/bks/util/StrUtil.java-arc  $
 * 
 *    Rev 1.0   Dec 17 2010 17:04:28   makuier
 * Initial revision.
 *
 *    Rev 1.5   Oct 20 2004 09:03:34   Blaschkp
 * join WOE and WOE2
 *
 *    Rev 1.0   Oct 07 2004 19:21:04   GRADO
 * Initial revision.
 *
 *    Rev 1.4   Nov 11 2003 16:34:18   BLASCHKP
 * Bug-Fix: split with whitespace as delimiter
 *
 *    Rev 1.3   Aug 27 2003 18:49:52   mazdakr
 * introducing new DBConnectionPool
 *
 *    Rev 1.2   02 May 2003 12:13:00   muellerc
 * Revision von außen verfügbar gemacht
 *
 *    Rev 1.1   Apr 17 2003 18:27:38   mazdakr
 * Introducing of Sorter
 * Check of null input in StrUtil.replace
 *
 *    Rev 1.0   Mar 19 2003 15:41:16   mazdakr
 * Initial revision.
 *
 *    Rev 1.0   Mar 19 2003 15:37:24   mazdakr
 * Initial revision.
*
********************************************************************************/

package net.arcor.bks.util;

/**
 * StrUtil Class is a utility class for String Formatings.
 * It provides common test and formating methods.
 *
 * @author Maz Rashid
 * @version 1.0
 */
public class StrUtil
{
	public static final String VERSION = "$Revision:   1.0  $";

	/**
	 * val checks wether the parameter s is empty or null. In the case of an
	 * empty value the default value will be delivered.
	 * If ist is not empty and a sFrame charachter is provided this will be
	 * used to deliver a result as sFrame + s + sFrame
	 * The default value is not framed.
	 *
	 * @param s
	 * @param sDef
	 * @param sFrame
	 * @return String
	 */
	public static String val(Object o, String sDef, String sFrame)
	{
		String s = o==null?null:o.toString();

		if(s==null||s.equals("")||s.equals("null"))
			return sDef==null?"":sDef.trim();

		if(sFrame!=null)
			s = sFrame + s.trim() + sFrame;

		return s;
	}

	public static String val(Object o, String sDef)
	{
		return val(o,sDef,null);
	}

	public static String val(Object o)
	{
		return val(o,null,null);
	}

	public static boolean booleanValue(String s)
	{
		s = val(s);
		return s.equalsIgnoreCase("1")||s.equalsIgnoreCase("true")||s.equalsIgnoreCase("on");
	}

	public static String replace(String s, String sPat, String sTarget)
	{
		if(s==null||s.equals(""))
			return s;

		StringBuffer sb = new StringBuffer();
		int idx = -1;
		int iPatLen = sPat.length();

		while((idx=s.indexOf(sPat))>=0)
		{
			String sx = s.substring(0,idx);
			sb.append(sx);
			sb.append(sTarget);

			s = s.substring(idx+iPatLen);
		}

		sb.append(s);

		return sb.toString();
	}

	public static String replaceWildCards(String sTempl, java.util.List lItems)
	{
		String sRC = sTempl;
		for(int i=0; i< lItems.size(); i++)
		{
			Object o = lItems.get(i);
			sRC = replace(sRC,"%"+i,o==null?"null":o.toString());
		}

		return sRC;
	}

	public static java.util.List split(String s, String sDelim)
	{
		java.util.List l = new java.util.ArrayList();
		/*
		 java.util.StringTokenizer st = new java.util.StringTokenizer(s,sDelim);
		 while (st.hasMoreTokens())
		 	l.add(st.nextToken());
		 return l;
		*/
		 int idx;
		 while((idx=s.indexOf(sDelim))>=0)
		 {
		 	String sx = s.substring(0,idx).trim();
			l.add(sx);

		 	s = s.substring(idx+sDelim.length());
		 }
//		 if(!s.equals(""))
		 	l.add(s.trim());

		 return l;

	}

	public static String removeInvalidChars(String string) {
		String result = string;
		for (char i = 0x0; i <= 0x8; i++)
			result = result.replace(i, ' ');
		for (char i = 0xB; i <= 0xC; i++)
			result = result.replace(i, ' ');
		for (char i = 0xE; i <= 0x1F; i++)
			result = result.replace(i, ' ');
		for(char i = 0xD800; i<0xE000; i++ )
			result = result.replace(i,' ');
		/*for(char i = 0xFFFE; i<0x10000; i++)
			result = result.replace(i,' ');*/
		return result;
	}

	/**
	 *
	 * @param in input string
	 * @param max maximum length of string
	 * @return new string no longer than max param
	 */
	public static String extractNoLongerStr(String in, int max){
		return in.substring(0, in.length() > max ? max: in.length());
	}

	// Test
	public static void main(String[] args)
	{
		System.out.println(replace("Test 'this'' with 'those'","'","\\'"));
		System.out.println(replace("Test 'this' with 'those'","this","me"));
	}
}
