/**
 * @(#) CharUtil.java Copyright (c) jerred. All rights reserverd
 * @version 1.00
 * @since jdk 1.4.02
 * @createdate 2004. 3. 3.
 * @author Cho Sung Ok, jerred@bcline.com
 * @desc
 */

package ext.narae.util;

import java.io.UnsupportedEncodingException;

/**
 * 한글 CharSet 정보 참조
 * <p>
 * KS_C_5601-1987 , 5601 , KSC5601 , KSC5601-1987 , KSC5601_1987, <br>
 * KSC_5601 , EUCKR , EUC_KR , EUC-KR 모두 동일한 완성형 한글 코드임 <br>
 * <br>
 * WINDOW-949 , MS949 모두 동일한 확장 완성형 한글 코드 <br>
 * <br>
 * KSC5601_1992 , KSC5601-1992 , MS1361 , JOHAB 모두 동일한 조합형 한글 코드 <br>
 */
public final class CharUtil {
	/**
	 * 객체 생성을 방지하기 위해서 디폴트 생성자를 Private로 선언
	 */
	private CharUtil() {
	}

	/**
	 * 기본적인 CharSet 변환 Method ( KSC5601 => 8859_1 )
	 * 
	 * @param korean
	 *                  <code>java.lang.String</code> KSC5601 의 CharSet String
	 * @return <code>java.lang.String</code> 8859_1 변환된 String
	 */
	public static synchronized String K2E(String korean) {
		if (korean == null) return "";
		String english = null;
		try {
			english = new String ( new String ( korean.getBytes ( "KSC5601" ) , "8859_1" ) );
		} catch (UnsupportedEncodingException e) {
			english = new String ( korean );
		}
		return english;
	}

	/**
	 * 기본적인 CharSet 변환 Method ( 8859_1 => KSC5601 )
	 * 
	 * @param english
	 *                  <code>java.lang.String</code> 8859_1의 CharSet String
	 * @return <code>java.lang.String</code> KSC5601 변환된 String
	 */
	public static synchronized String E2K(String english) {
		if (english == null) return "";
		String korean = null;
		try {
			korean = new String ( new String ( english.getBytes ( "8859_1" ) , "KSC5601" ) );
		} catch (UnsupportedEncodingException e) {
			korean = new String ( english );
		}
		return korean;
	}

	/**
	 * 확장된 디코딩 Method ( Some CharSet => KSC5601 )
	 * 
	 * @param str
	 *                  <code>java.lang.String</code> parameter charSet의 CharSet
	 *                  String
	 * @param charSet
	 *                  <code>java.lang.String</code> parameter str의 CharSet
	 * @return <code>java.lang.String</code> KSC5601로 변환된 String
	 */
	public static synchronized String S2K(String str, String charSet) {
		if (str == null) return "";
		try {
			boolean bConvert = false;
			if (charSet.equalsIgnoreCase ( "8859_1" )) bConvert = true;
			else if (charSet.equalsIgnoreCase ( "MS949" )) bConvert = true;
			else if (charSet.equalsIgnoreCase ( "WINDOW-949" )) bConvert = true;
			else if (charSet.equalsIgnoreCase ( "KS_C_5601-1987" )) bConvert = false;
			else if (charSet.equalsIgnoreCase ( "KSC5601" )) bConvert = false;
			else if (charSet.equalsIgnoreCase ( "5601" )) bConvert = false;
			else if (charSet.equalsIgnoreCase ( "KSC5601-1987" )) bConvert = false;
			else if (charSet.equalsIgnoreCase ( "KSC5601_1987" )) bConvert = false;
			else if (charSet.equalsIgnoreCase ( "KSC_5601" )) bConvert = false;
			else if (charSet.equalsIgnoreCase ( "EUC-KR" )) bConvert = false;
			else if (charSet.equalsIgnoreCase ( "EUC_KR" )) bConvert = false;
			else if (charSet.equalsIgnoreCase ( "EUCKR" )) bConvert = false;
			else if (charSet.equalsIgnoreCase ( "UTF8" )) bConvert = false;
			else {
				charSet = "8859_1";
				bConvert = true;
			}

			if (bConvert) return new String ( str.getBytes ( charSet ) , "KSC5601" );
			else return str;
		} catch (UnsupportedEncodingException e) {
			return str;
		}
	}

	/**
	 * 파라미터 String 객체를 code2로 encoding해서 리턴한다.
	 * 
	 * @param str
	 *                  <code>java.lang.String</code> code1로 encoding된 String 객체
	 * @param code1
	 *                  <code>java.lang.String</code> decoding할 CharSet String 코드
	 * @param cdoe2
	 *                  <code>java.lang.String</code> encoding할 CharSet String 코드
	 * @return <code>java.lang.String</code> code2로 encoding된 String 객체
	 * @see java.lang.String#getBytes(String enc)
	 */
	public static synchronized String S2S(String str, String decodeCharSet, String encodeCharSet) {
		if (str == null) return null;
		String returnData = null;
		try {
			returnData = new String ( new String ( str.getBytes ( decodeCharSet ) , encodeCharSet ) );
		} catch (UnsupportedEncodingException e) {
			returnData = new String ( str );
		}
		return returnData;
	}
}
