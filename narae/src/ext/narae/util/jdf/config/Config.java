/**
* @(#) Config.java
* Copyright (c) jerred. All rights reserverd
* 
 *	@version 1.00
 *	@since jdk 1.4.02
 *	@createdate 2004. 3. 3.
 *	@author Cho Sung Ok, jerred@bcline.com
 *	@desc	
 */
 
package ext.narae.util.jdf.config;

import java.io.Serializable;
/**
* <b>이 소스는 <a href="http://www.javaservice.net/">JavaService Net</a>의 <br> Java Development Framework(JDF) ver1.0.0을 기초로 만들어졌음을 밝힙니다.</b>
* <p>
* <code>Config</code> 인테페이스는 <br>
* e3ps.config package의 주요 기능의 Method를 정의 하고 있다.
*/ 
public interface Config extends Serializable{
	/**
	*	key를 받아서 key에 해당하는 값을 <code>boolean</code>형태로 리턴하는 Method
	* 		@return 			<code>boolean</code>
	* 		@param 	key 	<code>java.lang.String</code>
	*/
	public boolean getBoolean(String key);
    
    public boolean getBoolean(String key, boolean _default);

	/**
	*	key를 받아서 key에 해당하는 값을 <code>int</code>형태로 리턴하는 Method
	* 		@return 			<code>int</code>
	* 		@param 	key 	<code>java.lang.String</code>
	*/
	public int getInt(String key);
    
    /**
     * key를 받아서 key에 해당하는 값을 <code>int</code>형태로 리턴하는 Method
     * key의 값이 없을 경우 _default 로 리턴한다.
     * @param key
     * @param _default
     * @return
     */
    public int getInt(String key, int _default);

	/**
	*	key를 받아서 key에 해당하는 값을 <code>java.lang.String</code>형태로 리턴하는 Method
	* 		@return 			<code>java.lang.String</code>
	* 		@param 	key 	<code>java.lang.String</code>
	*/
	public String getString(String key);
    
    /**
     * key를 받아서 key에 해당하는 값을 <code>java.lang.String</code>형태로 리턴하는 Method
     * key의 값이 없을 경우 _default 로 리턴한다.
     * @param _key
     * @param _default
     * @return
     */
    public String getString(String _key, String _default);
	
	/**
	*	key를 받아서 key에 해당하는 값을 <code>long</code>형태로 리턴하는 Method
	* 		@return 			<code>long</code>
	* 		@param 	key 	<code>java.lang.String</code>
	*/
	public long getLong(String key);
    
    /**
     * key를 받아서 key에 해당하는 값을 <code>long</code>형태로 리턴하는 Method
     * key의 값이 없을 경우 _default 로 리턴한다.
     * @param key
     * @param _default
     * @return
     */
    public long getLong(String key, long _default);
    
    /**
     * 주어진 key의 값이 ,로 구성되어있을 경우 배열로 리턴한다.
     * @param key
     * @return 
     */
    public String[] getArray(String key);
}
