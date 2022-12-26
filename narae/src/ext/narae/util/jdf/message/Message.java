/**
 * @(#) Message.java
 * Copyright (c) jerred. All rights reserverd
 * 
 *	@version 1.00
 *	@since jdk 1.4.02
 *	@createdate 2004. 3. 3.
 *	@author Cho Sung Ok, jerred@bcline.com
 *	@desc	
 */

package ext.narae.util.jdf.message;

import java.io.Serializable;
/**
 *	<b>이 소스는 <a href="http://www.javaservice.net/">JavaService Net</a>의 <br> Java Development Framework(JDF) ver1.0.0을 기초로 만들어졌음을 밝힙니다.</b>
 * <p>
 * <code>Message</code> 인테페이스는 e3ps.message package의
 * 주요 기능의 Method를 가지고 있다.
 */

public interface Message extends Serializable{
	/**	 
	 * 특정 code에 해당하는 Message를 리턴한다.
	 * @param 	code 	<code>java.lang.String</code>
	 * @return 				<code>java.lang.String</code>
	 */
	public String getMessage(String code);
	
	/**	 
	 * 특정 code에 해당하는 Message를 리턴한다.
	 * @param 	code 	<code>java.lang.String</code>
	 * @return 				<code>java.lang.String</code>
	 */
	public boolean contains(String code);	
}
