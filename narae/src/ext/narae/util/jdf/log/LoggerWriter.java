/**
 * @(#) LoggerWriter.java
 * Copyright (c) jerred. All rights reserverd
 * 
 *	@version 1.00
 *	@since jdk 1.4.02
 *	@createdate 2004. 3. 3.
 *	@author Cho Sung Ok, jerred@bcline.com
 *	@desc	
 */
 
package ext.narae.util.jdf.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ext.narae.util.jdf.config.Config;
import ext.narae.util.jdf.config.ConfigImpl;


/**
 *	<b>이 소스는 <a href="http://www.javaservice.net/">JavaService Net</a>의 <br> Java Development Framework(JDF) ver1.0.0을 기초로 만들어졌음을 밝힙니다.</b>b>
 * <p>
 * <code>LoggerWriter</code> 클래스는 로그파일을 관리하고 실제로 로그를 
 * 기록하는 역할을 하는 클래스이다. 이 클래스에는 각각의 모드가 있는데 이를 이용해서
 * 기록하고자 하는 형태를 선택할 수 있다.
 * 로그 기록 모드는 Configuration File에서 관리되는데 File에서 로그와 관련된 부분을 보면
 *	<p><blockquote><pre>
 * ############################################################################
 * # Log Writer Configuration
 * ############################################################################
 * # NOTE: 이것을 수정하고 반영되기를 원하면 JVM를 새로 기동해야 한다.
 * # 로그를 기록할 시에 자동 기록 여부를 설정하는 부분이다.
 * e3ps.log.autoflush = true
 *
 * # 로그 파일이 남는 디렉토리를 설정해 주는 부분이다.
 * e3ps.log.dir = log
 * 
 * # 로그 파일 기록 형태
 * # 0 : 사용안함
 * # 1 : 일자별로 기록
 * # 2 : 주별로 기록
 * # 3 : 월별로 기록
 * # 4 : 년별로 기록
 * log.filetype = 1
 * 
 * # Logging Trace Level ( FATAL, WARN, INFO, DEBUG )
 * # 개발자의 Code에 Logger.xxx.println()에 의해 Log를 남기는 부분에서
 * # 각 flag가 true일 경우만 실질적인 Log로 남게 된다.
 * e3ps.log.fatal.trace = true
 * e3ps.log.warn.trace = true
 * e3ps.log.info.trace = true
 * e3ps.log.debug.trace = true
 *	</pre></blockquote><p>
 * 각각 로그파일의 속성,로그파일의 위치,그리고 로그를 남길 코드를 설정하는 부분이다.
 *
 *	@see		e3ps.log.DownloadLoggerWriter#FATAL
 *	@see		e3ps.log.DownloadLoggerWriter#INFO
 *	@see		e3ps.log.DownloadLoggerWriter#DEBUG
 *	@see		e3ps.log.DownloadLoggerWriter#DBWRAP
 */

public class LoggerWriter {	
	private int mode;		
	private static String checkday = null;
	private static boolean newLined = true;	
	private static PrintWriter writer = null;
	private static SimpleDateFormat form = new SimpleDateFormat ("yyyyMMdd HH:mm:ss", Locale.KOREA);
	private final static Object lock = new Object();
	
	/**	 
	* 일어나면 안될 심각한 상황의 에러로그.
	*/
	public final static int ERR = 0;	
	/**
	* 일어나면 안되지만 그리  심각하지 않은 상황의 에러로그.
	* 그러나 반드시 차후에 이러한 상황에 대하여 확인 단계를 거쳐야 할 로그.
	*/
	public final static int INFO = 1;
	/**
	* 개발시에 보기 위한 것으로, 혹은 향후 어떤 문제가 생겼을 때,
	* 모든 로그 TRACE 를 보고자 할 때 남을 수 있는 상황의 로그.
	*/
	public final static int USER = 2;
	/**
	* DBPool과 관련된 로그를 보기 위한 것으로, 혹은 향후 어떤 문제가 생겼을 때,
	* 모든 로그 TRACE 를 보고자 할 때 남을 수 있는 상황의 로그.
	*/
	public final static int SQL = 3;
	
	
	/**	 
	* 생성자
	* @param mode <code>int</code> LoggerWriter 객체의 기록 모드
	*/
	public LoggerWriter(int mode) {
		this.mode = mode;
		synchronized ( lock ) {
			checkDate();
		}
	}

	/**	 
	* 기록할 파일의 날짜를 체크한다.
	* 만약 기록할 파일의 날짜가 지난 날짜이면 오늘날짜의 새로운 로그파일을 생성한 후
	* 그 파일에 로그기록을 남긴다.
	*/
	private static void checkDate() {
		Config conf = ConfigImpl.getInstance();
		SimpleDateFormat fileForm = null;
		String day = "";
		int logfiletype = conf.getInt("log.filetype");
		if ( logfiletype == 1 ) {			// 일별
			fileForm = new SimpleDateFormat ("yyyy년MM월dd일", Locale.KOREA);			
			day = "-"+fileForm.format(new Date());
		} else if ( logfiletype == 2 ) {	//주별
			Calendar cal = Calendar.getInstance(Locale.KOREA);
			fileForm = new SimpleDateFormat ("yyyy년MM월", Locale.KOREA);			
			day = "-"+fileForm.format(new Date())+cal.get(Calendar.WEEK_OF_MONTH)+"주";
		} else if ( logfiletype == 3 ) {	//월별
			fileForm = new SimpleDateFormat ("yyyy년MM월", Locale.KOREA);			
			day = "-"+fileForm.format(new Date());
		} else if ( logfiletype == 4 ) {	//년별
			fileForm = new SimpleDateFormat ("yyyy년", Locale.KOREA);			
			day = "-"+fileForm.format(new Date());
		}						
		if ( day.equals(checkday) ) return;

		try {
			if ( writer != null ) {
				try { 
					writer.close();
				} catch(Exception e) {}
				writer = null;
			}
			checkday = day;
			String logname = "eSolution" + checkday + ".log" ;
			
			String root = ConfigImpl.getRoot();
			String directory = conf.getString("log.dir");
			File rootDir = new File(root + File.separator + directory);
			//FileUtil.checkDir(rootDir); // 디렉토리 체크		
			if (!rootDir.exists ()) rootDir.mkdir ();
			if (!rootDir.exists ()) rootDir.mkdirs ();

			File file = new File(rootDir,logname);
			String filename = file.getAbsolutePath();			
			FileWriter fw =  new FileWriter(filename, true);														// APPEND MODE
			writer = new PrintWriter(new BufferedWriter(fw),conf.getBoolean("log.autoflush")); // AUTO Flush
		} catch(Exception e){
			writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)), true);
			writer.println("Can't open log file : " + e.getMessage());
			writer.println("Log will be printed into System.out");
		}		
	}

	/**	 
	* Garbage Collection 될때 불리우는 Method
	*/
	public void finalize() {
		try {
			if ( writer != null ) writer.flush();
		} catch(Exception e){}
	}

	/**	 
	* 호출될때 버퍼에 남아있는 스트림을 로그파일로 기록한다.
	*/
	public void flush() {
		if ( isPrintMode() ) writer.flush();
	}
	
	/**
	* Object 객체를 받아서 로그에 기록할 String형태로 변환해 준다.
	* 리턴 String 형태는
	* [o.classname : o.classcode] 이다 
	* @param	o	<code>java.lang.Object</code> 로그에 기록할 Object 객체
	* @return 		<code>java.lang.String</code>
	*/	
	protected String getPrefixInfo(Object o) {
		StringBuffer info = new StringBuffer();
		info.append('[');

		if ( o == null ) {
			info.append("null");
		} else {
			Class c = o.getClass();
			String fullname = c.getName();
			String name = null;
			int index = fullname.lastIndexOf('.');
			if ( index == -1 ) name = fullname;
			else name = fullname.substring(index+1);
			info.append(name);
		}

		if ( o == null ) info.append("] ");
		else info.append(":" + o.hashCode() + "] ");
			
		return info.toString();
	}

	/**
	* LoggerWriter 객체의 모드에 따라서 Configuration File에 설정된 기록여부를 리턴하는 Method
	* @return 		<code>boolean</code> LoggerWriter 객체의 모드에 따른 로그기록 여부
	*/
	private boolean isPrintMode() {
		boolean isPrintable = true;
		try {
			Config conf = ConfigImpl.getInstance();
			switch ( mode ) {				
				case ERR:
					isPrintable = conf.getBoolean("log.err.trace");
					break;
				case INFO:
					isPrintable = conf.getBoolean("log.info.trace");
					break;
				case USER:
					isPrintable = conf.getBoolean("log.user.trace");
					break;					
				case SQL:
					isPrintable = conf.getBoolean("log.sql.trace");
					break;		
			}
		} catch(Exception e) {}
		return isPrintable;
	}
	
	/**	 
	* 로그파일에 개행문자를 기록한다.
	*/	
	public void println() {
		if ( ! isPrintMode() ) return;
	    synchronized (lock) {
			if ( newLined ) printTime();
			writer.println();
			newLined = true;
	    }
	}

	/** 
	* Object 객체를 받아서 로그파일에 기록한다.
	* 이 Method에선 Object객체의 .toString()를 이용해서 기록한다. 
	* @param 	x	<code>java.lang.Object</code>	로그에 기록할 객체
	*
	* @see 	java.lang.Object#toString()
	*/
	public void println(Object x) {
		if ( ! isPrintMode() ) return;
	    synchronized (lock) {
			if ( newLined ) printTime();
			writer.println(x);
			newLined = true;
	    }
	}

	/** 
	* 두개의 Object객체 p,x를 받아서 로그에 기록한다.
	* @param	p	<code>java.lang.Object</code> 속성을 기록하기 위한 Object
	* @param	x	<code>java.lang.Object</code> Object p에 대한 Description
	*/
	public void println(Object p, Object x) {
		if ( ! isPrintMode() ) return;
		synchronized ( lock ) {
			if ( newLined ) printTime();
			writer.print(getPrefixInfo(p));
			writer.println(x);
			newLined = true;
		}
	}
	
	/** 
	* 두개의 Object객체 p,x를 받아서 로그에 기록한다.
	* @param	p	<code>java.lang.Object</code> 속성을 기록하기 위한 Object
	* @param	m	<code>java.lang.Object</code> Object의 메소드 명
	* @param	x	<code>java.lang.Object</code> Object p에 대한 Description
	*/
	public void println(Object p, String m , String x) {
		if ( ! isPrintMode() ) return;
		synchronized ( lock ) {
			if ( newLined ) printTime();
			writer.print(getPrefixInfo(p));
			writer.print(" [ method : " + m + " ] ");
			writer.println(x);
			newLined = true;
		}
	}

	/**
	* String 객체를 로그에 기록한다.
	* @param 	x	<code>java.lang.String</code> 로그에 기록하기 위한 String
	*/
	public void println(String x) {
		if ( ! isPrintMode() ) return;
	    synchronized (lock) {
			if ( newLined ) printTime();
			writer.println(x);
			newLined = true;
	    }
	}	

	/**
	* LoggerWriter 객체의 모드와 시간을 개행 없이 로그에 기록한다.
	* 기록형태는 모드(Error/Info/Debug/DBWrap) [HH:mm:ss] 이다
	*/
	private void printTime() {
		checkDate();
		String serverty = " ";
		switch ( mode ) {			
			case ERR:
				serverty = "[F] ";
				break;		
			case INFO:
				serverty = "[I] ";
				break;
			case USER:
				serverty = "[U] ";
				break;
			case SQL:
				serverty = "[S] ";			
		}
		
		writer.write(serverty + " [" + form.format(new Date())+"] ");
	}	
}
