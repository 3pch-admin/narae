/**
 * @(#) MessageException.java
 * Copyright (c) jerred. All rights reserverd
 * 
 *	@version 1.00
 *	@since jdk 1.4.02
 *	@createdate 2004. 3. 3.
 *	@author Cho Sung Ok, jerred@bcline.com
 *	@desc	
 */

package ext.narae.util.jdf.message;

/**
 *	<b>이 소스는 <a href="http://www.javaservice.net/">JavaService Net</a>의 <br> Java Development Framework(JDF) ver1.0.0을 기초로 만들어졌음을 밝힙니다.</b>
 * <p>
 * Message Definition File을 찾을수 없거나 Load를 할수 없을때 발생하는 Exception
 */

public class MessageException extends RuntimeException {
	/**
	 * Default 생성자
	 */
	public MessageException() {
		super();
	}

	/**
	 * @param str <code>java.lang.String</cdoe> Exception 메세지
	 */
	public MessageException(String str) {
		super(str);
	}	
}
