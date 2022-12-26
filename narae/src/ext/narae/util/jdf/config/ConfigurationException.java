/**
* @(#) IllegalKeyException.java
* Copyright (c) jerred. All rights reserverd
* 
 *	@version 1.00
 *	@since jdk 1.4.02
 *	@createdate 2004. 3. 3.
 *	@author Cho Sung Ok, jerred@bcline.com
 *	@desc	
 */
 
package ext.narae.util.jdf.config;

/**
* <b>이 소스는 <a href="http://www.javaservice.net/">JavaService Net</a>의 <br> Java Development Framework(JDF) ver1.0.0을 기초로 만들어졌음을 밝힙니다.</b>
* <p>부적절한 key를 이용해서 value를 얻을려고 할거나 잘못된 타입의 값을 얻을려고 할때 발생하는 RuntimeException
*/
public class ConfigurationException extends RuntimeException {
	/**
	* Default 생성
	*/
	public ConfigurationException() {
		super();
	}

	/**
	* @param str <code>java.lang.String<code>	Exception 메세지
	*/
	public ConfigurationException(String str) {
		super(str);
	}
}
