/**
 * @(#) Logger.java
 * Copyright (c) jerred. All rights reserverd
 * 
 *	@version 1.00
 *	@since jdk 1.4.02
 *	@createdate 2004. 3. 3.
 *	@author Cho Sung Ok, jerred@bcline.com
 *	@desc	
 */

package ext.narae.util.jdf.log;


/**
 *	<b>이 소스는 <a href="http://www.javaservice.net/">JavaService Net</a>의 <br> Java Development Framework(JDF) ver1.0.0을 기초로 만들어졌음을 밝힙니다.</b>
 * <p>
 * <code>Logger</code>클래스는 <code>LoggerWriter</code> 클래스를 wrap하고 있는 클래스 이다.
 * 로그 파일을 관리하고 로그를 기록하는 부분을 <code>LoggerWriter</code>가 담당한다면
 * 이 <code>LoggerWriter</code>를 관리하는 클래스가 이 <code>Logger</cdoe>클래스 이다.
 *
 * 이 클래스에는 네가지 모드(ERR/INFO/DEBUG/DBWRAP)의 LoggerWriter객체를 Field로 가지고 있다.
 * 이 객체를 이용해서 실제 로그기록 작업을 하게 된다.
 * 사용예를 보면
 *	<p><blockquote><pre>
 * //FATAL에 해당하는 로그를 기록하는 경우
 * Logger.fatal.println("....");
 * //INFO 해당하는 로그를 기록하는 경우
 * Logger.info.println("....");
 * //DEBUG 해당하는 로그를 기록하는 경우
 * Logger.debug.println("....");
 *	</blockquote></pre><p>
 * //DBWRAP 해당하는 로그를 기록하는 경우
 * Logger.dbwrap.println("....");
 *	</blockquote></pre><p>
 *
 *	@see		e3ps.log.DownloadLoggerWriter#ERR
 *	@see		e3ps.log.DownloadLoggerWriter#INFO
 *	@see		e3ps.log.DownloadLoggerWriter#USER
 *	@see		e3ps.log.DownloadLoggerWriter#SQL 
 *	@see		e3ps.log.DownloadLoggerWriter#println()
 *	@see		e3ps.log.DownloadLoggerWriter#println(Object)
 *	@see		e3ps.log.DownloadLoggerWriter#println(Object,Object)
 *	@see		e3ps.log.DownloadLoggerWriter#println(String) 
 */

public final class Logger {
	/**
	 * FATAL 모드를 가지고 있는 <code>LoggerWriter</code>클래스 객체
	 */		
	public final static LoggerWriter err = getLoggerWriter(LoggerWriter.ERR);
	/**
	 * INFO 모드를 가지고 있는 <code>LoggerWriter</code>클래스 객체
	 */
	public final static LoggerWriter info = getLoggerWriter(LoggerWriter.INFO);
	/**
	 * DEBUG 모드를 가지고 있는 <code>LoggerWriter</code>클래스 객체
	 */
	public final static LoggerWriter user = getLoggerWriter(LoggerWriter.USER);
	/**
	 * DBWRAP 모드를 가지고 있는 <code>LoggerWriter</code>클래스 객체
	 */
	public final static LoggerWriter sql = getLoggerWriter(LoggerWriter.SQL);
	
	/**
	 * 웹상에서 Download Action을 기록하기 위한  <code>DownloadLoggerWriter</code>클래스 객체
	 */
	public final static DownloadLoggerWriter down = new DownloadLoggerWriter();

	/**
	 * 객체 생성을 방지하기 위해서 디폴트 생성자를 Private로 선언
	 */
	private Logger() {}
	
	/**
	 * Field를 초기화 하는데 사용한다.
	 * @param	serverty	<code>int</code>	LoggerWriter 객체의 모드 
	 * @return 				<code>e3ps.log.LoggerWriter</code> serverty의 모드에 해당하는 LoggerWriter 객체
	 */
	private static LoggerWriter getLoggerWriter(int serverty) {
		LoggerWriter  logger = null;
		try {
			logger = new LoggerWriter(serverty);
		} catch(Exception e) {			
			System.err.println("e3ps.jdf.log.LoggerWriter initialization fail : " + e.getMessage());
			e.printStackTrace();
		}
		return logger;
	}			
}
