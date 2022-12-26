/**
 * @(#)	StringUtil
 * Copyright (c) jerred. All rights reserverd
 * 
 *	@version 1.00
 *	@since jdk 1.4.02
 *	@createdate 2004. 3. 3.
 *	@author Cho Sung Ok, jerred@bcline.com
 *	@desc	
 */

package ext.narae.util;

import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;

public final class StringUtil {
	/**
	* 객체 생성을 방지하기 위해서 디폴트 생성자를 Private로 선언
	*/    
	private StringUtil() {}

/*
 * 금잔디(frogx)님의 블로그에서 발췌
 */	
	
	 //UTF-8은 최대 4바이트를 사용하고 ASCII는 1바이트 그외의 문자들은 2~3바이트 까지 조합하여 사용한다.
	 //즉, 어느 나라 문자이냐에 따라서 몇 바이트를 사용하는지 모르기 때문에 하나의 charater가 몇 바이트 대역에
	 //있는지 조사하여 한문자의 바이트를 조사... 이를 더해 나가면 문자 단위로 몇 바이트를 차지 하는지 정확하게 조사할 수 있다.
	 public static int ONE_BYTE_MIN = 0x0000;
	 public static int ONE_BYTE_MAX = 0x007F;
	 
	 public static int TWO_BYTE_MIN = 0x0800;
	 public static int TWO_BYTE_MAX = 0x07FF;
	 
	 
	 public static int THREE_BYTE_MIN = 0x0800;
	 public static int THREE_BYTE_MAX = 0xFFFF;
	 
	 public static int SURROGATE_MIN = 0x10000;
	 public static int SURROGATE_MAX = 0x10FFFF;
/*
 * UTF-8 한글이 포함된 문자열의 byte수를 반환
 * str 문자열
 */	 
	 public static int getBytes(String str)
	 {
		 int currByte = 0;
		 if(str==null)
			 return 0;
		 else if(str.length()==0)
			 return 0;
		 else
		 {
			 for(int i=0; i<str.length();i++)
			 {
				 char ch = str.charAt(i);
				 currByte = currByte+availibleByteNum(ch);
			 }
		 }
		 return currByte;
	 }
/*
 * 문자의 byte 크기를 반환
 * 
 */	 
	 public static int availibleByteNum(char c) {
	  int digit = (int)c;
	  
	  if (ONE_BYTE_MIN <= digit && digit <= ONE_BYTE_MAX)
	   return 1;  
	  else if (TWO_BYTE_MIN <= digit && digit <= TWO_BYTE_MAX)
	   return 2;
	  else if (THREE_BYTE_MIN <= digit && digit <= THREE_BYTE_MAX)
	   return 3;
	  else if (SURROGATE_MIN <= digit && digit <= SURROGATE_MAX)
	   return 4;
	  
	  return -1;
	 }
	 
/*문자열을 받아서 max사이즈를 넘기는 부분은 trail로 변환해서 반환하는 메소드
 * str 대상문자열
 * maxByteSize 문자열의 총길이(byte)
 * trail 변환할 문자열
 */	 
	 public static String cutOffUTF8String(String str, int maxByteSize, String trail) throws UnsupportedEncodingException 
	 {
		  //널일 경우에는 그냥 리턴
		  if (str == null)
		   return null;
		  
		  if (str.length() == 0)
		   return str;
		  
		  byte strByte[] = str.getBytes("UTF-8");
		  
		  if (strByte.length <= maxByteSize)
		   return str;
		  
		  //마지막 줄임말
		  int trailByteSize = 0;
		  
		  //줄임말의 바이트 수 계산
		  if (trail != null)  
		   trailByteSize = trail.getBytes("UTF-8").length;
		  
		  //실질적으로 포함되는 최대 바이트 수는 trailByte를 뺀 것이다.
		  maxByteSize = maxByteSize - trailByteSize;
		  
		  //마지막 바이트 위치
		  int endPos = 0;
		  //현재까지 조사한 바이트 수
		  int currByte = 0;
		    
		  for (int i = 0; i < str.length(); i++) 
		  {
			   //순차적으로 문자들을 가져옴.
			   char ch = str.charAt(i);
			   
			   //이 문자가 몇 바이트로 구성된 UTF-8 코드인지를 검사하여 currByte에 누적 시킨다.
			   currByte = currByte + availibleByteNum(ch);
			   
			   //현재까지 조사된 바이트가 maxSize를 넘는다면 이전 단계 까지 누적된 바이트 까지를 유효한 바이트로 간주한다. 
			   if (currByte > maxByteSize) {
			    endPos = currByte - availibleByteNum(ch);
			    break;
			   }
		   
		  }
		  
		  //원래 문자열을 바이트로 가져와서 유효한 바이트 까지 배열 복사를 한다.
		  
		  
		  byte newStrByte[] = new byte[endPos];
		  
		  System.arraycopy(strByte, 0, newStrByte, 0, endPos);
		  
		  String newStr = new String(newStrByte, "UTF-8");
		  System.out.println(newStr.getBytes("UTF-8").length+ " " + newStr);
		  newStr += trail;
		  
		  return newStr;
	 }
/////////여기까지 금잔디님의 코드
	 
	 
	 
	 /**
	 *	주어진 파라미터에서 특정 구분자로 문자열을 분리시키고<br>
	 *	&lt;br&gt; 태그로 변환시켜주는 Method<br>
	 *	예) convertDelimToBR("1.2.3.4.5.6.7",".",4) => "1.2.3.4<br>5.6.7"
	 * 	@param 	str 		<code>java.lang.String</code>	Target String	  
	 * 	@param 	delim 	<code>java.lang.String</code>	구분자
	 * 	@param 	count 	<code>int</code>	허용 token수
	 * 	@return 					<code>java.lang.String</code>	변환된 data
	 */
	public static String convertDelimToBR(String str,String delim,int count ) {
		if (str == null) return "";

		String returnData = "";
		StringTokenizer st;
		if (delim != null) st = new StringTokenizer(str, delim);
		else st = new StringTokenizer(str);

		int subCount = 0;
		while (st.hasMoreTokens()) {
			if (subCount < count) {
				subCount++;
				returnData = returnData + st.nextToken() + ", ";
			} else {
				subCount = 0;
				returnData = returnData + st.nextToken() + "<br>";
			}
		}

		return returnData;
	}
		
	/**
	 *	파라미터로 주어진 str String 객체에서 from 문자를 to 문자로 대치한다.
	 *
	 * @param	str		<code>java.lang.String</code>	target String
	 * @param	from		<code>java.lang.String</code>	변경시킬 문자
	 * @param	to			<code>java.lang.String</code>	변경할 문자
	 *	@return				<code>java.lang.String</code>	변환된 스트링
	 */
	public static String changeString(String str, String from, String to) {
		if ( str == null ) return "";
		int s = 0;
		int e = 0;
		StringBuffer result = new StringBuffer();

		while ((e = str.indexOf(from, s)) >= 0) {
			result.append(str.substring(s, e));
			result.append(to);
			s = e + from.length();
		}
		result.append(str.substring(s));		
		return result.toString();	
	}	

	/**
	 * 문자열의 왼쪽으로부터 지정된 수의 문자를 String값으로 반환합니다.
	 * @param string 해당 문자열입니다.
	 * @param length 반환할 문자 수
	 */
	public static String getLeft(String string, int length) {
		if ( string.length() > (length-1) ) return string.substring(0, length)+"...";
		else return string;
	}
	
	/**
	 * 한 문자열에서 지정된 수의 문자를 String 값으로 반환합니다.
	 * @param string 해당 문자열입니다.
	 * @param start 시작 인덱스입니다.
	 * @param length 반환할 문자 수
	 */
	public static String getMid(String string, int start, int length) {
		if (string.length() <= (length - start)) {
			length = string.length() - start;
		}
		return string.substring(start, start + length);
	}
	
	/**
	 * 문자열의 오른쪽으로부터 지정된 수의 문자를 String값으로 반환합니다.
	 * @param string 해당 문자열입니다.
	 * @param length 반환할 문자 수
	 */
	public static String getRight(String string, int length) {
		return string.substring(string.length() - length, string.length());
	}

	/**
	 * 지정된 길이의 반복되는 문자열을 String값으로 반환합니다
	 * @param number 반환되는 문자열의 길이입니다.
	 * @param character 문자를 지정하는 문자 코드나 반환 문자열을 구성하는 첫 문자로 사용되는 문자식
	 */
	public static String getString(int number, String character) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0 ; i < number ;i++ ) {
			sb.append(character);
		}
		return sb.toString();
	}
	
	/**
	 * 인자값이 null 인경우 공백을 리턴한다.
	 * @param str 입력스트링
	 */
	public static String checkNull(String str){ 		
		if(str == null) return "";
		return str;	
	}
	
	/**
	 * 인자값이 null인경우 " "을 리턴한다.
	 */
	public static String checkValue(String str) {
		if(str == null) return " ";
		return str;
	}
	
	/**
	 * 인자값이 null 인경우 defaultStr을 리턴한다.
	 * @param str 입력스트링
	 */
	
	public static String checkReplaceStr(String str, String defaultStr) {
		if (str == null || str.equals ( "" )) return defaultStr;
		else return str.trim ();
	}
	
	/**
	 * 파라미터로 들어온 String을 int로 변환시켜주는 Method<br>
	 * 	@param 	data 			<code>java.lang.String</code>	Target String
	 * 	@param 	defaultData 	<code>int</code>	default value (변환이 불가능한 경우 리턴될 값)
	 * 	@return 						<code>int</code>	변환된 data
	 */
	public static int parseInt(String data, int defaultData) {
		try {
			return Integer.parseInt(data);
		} catch ( Exception e ) {
			return defaultData;
		}
	}
	
	/**
	 * 파라미터로 들어온 String을 boolean 변환시켜주는 Method<br>
	 * 	@param 	data 			<code>java.lang.String</code>	Target String	 
	 * 	@return 						<code>Boolean.getBoolean(data)</code>	변환된 data
	 */
	public static boolean parseBoolean(String data) {		
			return Boolean.getBoolean(data);		
	}
	
	/**
	 *	문자의 공백 문자를 제거 해주는 Method 
	 * 	@param 	str 		<code>java.lang.String</code>	target String
	 * 	@return 				<code>java.lang.String</code>	Tirm된 target String
	 */
	public static String trim(String str) {
		if ( str == null ) return "";
		String tmp = str.trim();
		if ( tmp.toLowerCase().equals("null") ) tmp = "";
		return tmp;        
	}
	
	/**
	 * 주어진 파라미터에서 줄바꿈문자('\n')가 있는 경우<br>
	 *	&lt;br&gt; 태그로 변환시켜주는 Method
	 * 	@param 	data 	<code>java.lang.String</code>	Target String
	 * 	@return 				<code>java.lang.String</code>	변환된 data
	 */
	public static String convertEnterChar(String str) {
		if (str == null) return "";
		str = changeString(str, "\n", "<br>");
		return str;
	}
	
	/**
     * 주어진 문자열이 null이 아니고 문자열 길이도 0보다 큰가를 체크한다
     * @param str
     * @return if true 이상무
     */
    public static boolean checkString(String str) {
        return str != null && str.length() > 0 && !str.equals("null");
    }
    
    public static int getIntParameter(String str, int ifNulltoReplace) {
		try {
			if (str == null || str.equals ( "" )) return ifNulltoReplace;
			else return Integer.parseInt ( str.toString () );
		} catch (NumberFormatException e) {
			return ifNulltoReplace;
		}
	}
    
    public static int getIntParameter(String str) {
		try {
			if (str == null || str.equals ( "" )) return 0;
			else return Integer.parseInt ( str.toString () );
		} catch (NumberFormatException e) {
			return -1;
		}
	}
    
/**
 * 문자열을 입력받아 제한 길이보다 길경우 substring
 * naver asdf123's blog
 * @param str	check할 문자열
 * @param lenth	문자열 제한 길이
 * @return			수정된 문자열
 */
    public static String subByteData(String str, int length)
    {
    	try
    	{
    		return cutOffUTF8String(str,length,"...");
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		return null;
    	}
//    	String returnStr = str;
//    	
//    	if(str==null || str.trim().equals("")) return "";
//
//    	if(str.getBytes().length <=lenth) return str;
//
//    	byte[] oldByte = str.getBytes();
//    	byte[] newByte = new byte[lenth];
//
//    	int start = 0;
//    	for(int j=0; start<lenth;j++)
//    	{
//    				//ASCII  0~127까지
//    		if(oldByte[j] >= 0 && oldByte[j] <= 127)
//    		{
//    			newByte[start] = oldByte[j];
//    			start++;
//    		}
//    		else
//    		{
//    				//2바이트 글자인데 공간이 남은경우 -> 다음글자까지 씀.
//    			if(oldByte[j] < 0 && start+1<lenth)
//    			{
//    				newByte[start] = oldByte[j];
//    				newByte[++start] = oldByte[++j];
//    				start++;
//    			}
//    				//2바이트인데 공간이 없는경우->짜른다.
//    			else if(oldByte[j] < 0 && start+1 >= lenth)
//    			{
//    				j++;
//    				start=start+2;
//    			}
//    			else
//    			{
//    				newByte[start] = oldByte[j];
//    				start++;
//    			}
//    		}
//    	}
//		returnStr = new String(newByte)+"...";
//		System.out.println("\n->StringUtil::subByteData:returnStr = "+returnStr);
//    	return returnStr;
    }
    
    public static String htmlCharEncode(String str) {
    	if( (str == null) || (str.length() == 0)) {
    		return "";
    	}
    	str = str.replaceAll("&", "%26");
    	str = str.replaceAll("'", "%27");
		str = str.replaceAll(",", "%2C");
		str = str.replaceAll("\"", "%22");
		str = str.replaceAll("/", "%2F");
		
    	return str;
    }
    
    public static String htmlCharDecode(String str) {
    	if( (str == null) || (str.length() == 0)) {
    		return "";
    	}
    	
    	str = str.replaceAll("%26","&");
    	str = str.replaceAll("%27","'");
		str = str.replaceAll("%2C",",");
		str = str.replaceAll("%22","\"");
		str = str.replaceAll("%2F","/");
		
    	return str;
    }
    
    public static String getShortString( String orig, int len) {
		byte[] byteString = orig.getBytes();
	
	    if (byteString.length <= len) {
	    	return orig;
	    }
	    else {
	    	int minusByteCount = 0;
	    	for (int i = 0; i < len; i++) {
	    		minusByteCount += (byteString[i] < 0) ? 1 : 0;
	    	}
	    	
	    	if (minusByteCount % 2 != 0) {
	    		len--;
	    	}
	    	
	    	return new String(byteString, 0, len);
	    }
	}
}
