package ext.narae.service.drawing.beans;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import ext.narae.util.DateUtil;
import ext.narae.util.jdf.config.Config;
import ext.narae.util.jdf.config.ConfigImpl;



public class EPMReNameLog {
	
	public final static PrintWriter out;
	public final static String EPM3D ="EPM3D";
	public final static String EPM2D ="EPM2D";
	public final static String PART ="PART";
	
    static {
    	
    	Config conf = ConfigImpl.getInstance();
        String filePath = conf.getString("epm.rename.log.url");
        //System.out.println(">>>>>>>>>>>>>>> filePath = " + filePath);
        File logDirectory = new File(filePath);
        if(!logDirectory.isDirectory()){
        	logDirectory.mkdirs();
        }
        	
        String toDay = DateUtil.getToDay();
        toDay= toDay.replace("/", "_");
        String logFileName = toDay.concat("_EPMReName.log");
    	String logFilePath = filePath.concat(File.separator).concat(logFileName);
        File file = new File( logFilePath );
        FileWriter fw = null;
        try {
            fw = new FileWriter( file, true );
        } catch ( IOException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        out = new PrintWriter( new BufferedWriter( fw ), true );
    }
    
    public static void errLog(String message){
    	String logTime=DateUtil.getCurrentDateString("a");
    	message ="[ERROR.] "+logTime +" : " +message;
    	out.println(message);
    }
    
    public static void infoLog(String message){
    	String logTime=DateUtil.getCurrentDateString("a");
    	message ="[INFO..] "+logTime +" : " +message;
    	out.println(message);
    }
    
    public static void sucessLog(String message){
    	String logTime=DateUtil.getCurrentDateString("a");
    	message ="[SUCESS] "+logTime +" : " +message;
    	out.println(message);
    }
    
    public static void crateLog(String message){
    	String logTime=DateUtil.getCurrentDateString("a");
    	message ="[CREATE] "+logTime +" : " +message;
    	out.println(message);
    }
    
    public static void updateLog(String message){
    	String logTime=DateUtil.getCurrentDateString("a");
    	message ="[UPDATE] "+logTime +" : " +message;
    	out.println(message);
    }
    
    public static void exceptionLog(String message){
    	String logTime=DateUtil.getCurrentDateString("a");
    	message ="[EXCEPTION.] "+logTime +" : " +message;
    	out.println(message);
    }
}
