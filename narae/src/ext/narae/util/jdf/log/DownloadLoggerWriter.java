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
import wt.content.ApplicationData;
import wt.content.ContentHolder;
import wt.org.WTUser;


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

public class DownloadLoggerWriter {	
	private static String checkday = null;
	private static boolean newLined = true;	
	private static PrintWriter writer = null;
	private static SimpleDateFormat form = new SimpleDateFormat ("yyyyMMdd HH:mm:ss", Locale.KOREA);
	private final static Object lock = new Object();
	
	/**	 
	* 생성자
	* @param mode <code>int</code> LoggerWriter 객체의 기록 모드
	*/
	public DownloadLoggerWriter() {
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
			String logname = "eSolution-Download" + checkday + ".log" ;
			
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
		writer.flush();
	}
	
	public void println(String type, WTUser user, ContentHolder holder , ApplicationData data) {
		synchronized ( lock ) {
			if ( newLined ) printTime();
			writer.print("[" + type + "] ");
			writer.print("[ UserName : " + user.getFullName() + "(" + user.getName() + ") ] ");
			writer.print("[ ContentHolder : " + holder.getPersistInfo().getObjectIdentifier().toString() + " ] ");
			writer.println("[ FileName : " + data.getFileName() + "(" + data.getFileSize() + " Bytes)] ");
			newLined = true;
		}
	}

	/**
	* LoggerWriter 객체의 모드와 시간을 개행 없이 로그에 기록한다.
	* 기록형태는 모드(Error/Info/Debug/DBWrap) [HH:mm:ss] 이다
	*/
	private void printTime() {
		checkDate();
		String serverty = "[D] ";
		writer.write(serverty + " [" + form.format(new Date())+"] ");
	}	
}
