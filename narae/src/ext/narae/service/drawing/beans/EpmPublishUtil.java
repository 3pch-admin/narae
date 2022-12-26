package ext.narae.service.drawing.beans;

import java.io.File;
import java.lang.reflect.Method;
import java.util.StringTokenizer;

import com.ptc.wvs.common.util.WVSProperties;
import com.ptc.wvs.server.publish.Publish;

import ext.narae.util.content.CommonContentHelper;
import wt.epm.EPMDocument;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.vc.config.ConfigSpec;

public class EpmPublishUtil implements RemoteAccess{
	private static Method filterEPMDocumentPublishMethod;
	
	static {
		try{
			String s2 = WVSProperties.getPropertyValue("publish.service.filterepmdocumentpublishmethod");
            if(s2 != null)
            {
                StringTokenizer stringtokenizer = new StringTokenizer(s2.trim(), "/");
                if(stringtokenizer.countTokens() == 2)
                {
                    Class class1 = Class.forName(stringtokenizer.nextToken());
                    Class aclass[] = {
                        wt.epm.EPMDocument.class
                    };
                    filterEPMDocumentPublishMethod = class1.getMethod(stringtokenizer.nextToken(), aclass);
                   
                }
            }
		}catch(Exception ex){
			
		}
	}	
	
	public static void publish(EPMDocument epm)throws Exception{
		
		//if(true){
			//System.out.println("<<<<<<<<<<<<<<<publishEPM >>>>>>>>>>>");
		//	return;
		//}
        
		if (!RemoteMethodServer.ServerFlag)
        {
            try
            {
                Class argTypes[] = { EPMDocument.class };
                Object argValues[] = { epm };
                RemoteMethodServer.getDefault().invoke("publish", "com.e3ps.drawing.beans.EpmPublishUtil",
                                                                            null, argTypes, argValues);
                return;
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
		
		
		boolean flag1 = true;
		   
        if(filterEPMDocumentPublishMethod != null)
            try
            {
                Object aobj[] = {
                		epm
                };
                flag1 = ((Boolean)filterEPMDocumentPublishMethod.invoke(null, aobj)).booleanValue();
            }
            catch(Exception exception)
            {
                exception.printStackTrace();
                flag1 = true;
            }
        if(flag1)
        {
           /* Object aobj1[] = Publish.doPublish(false
            								, true
            								, epm
            								, null
            								, null
            								, false
            								, null
            								, null
            								, 1
            								, null
            								, 2
            								, null);*/
        	ConfigSpec configspec = null;
			ConfigSpec configspec1 = null;
			Publish.doPublish(false, true, epm, configspec, null, false, null, null, 1, null, 2, null);
            
        }
		
	}
	
	public static void pdfPublish(EPMDocument epm,File rfile){//String dwgFilePath,String pdfFileName){
		
		try{
			
			Runtime rt = Runtime.getRuntime();
			
			String fileName = rfile.getName();
			String fileDir = rfile.getParent();
			String dwgFilePath = fileDir + File.separator +fileName;
			int lastIndex = fileName.lastIndexOf(".");
			String ver = "."+epm.getVersionIdentifier().getSeries().getValue()+"."+epm.getIterationIdentifier().getSeries().getValue();
	      	String pdfFileName = fileDir + File.separator+epm.getNumber()+ver +".pdf";
	      	
			String exec1 = "C:\\Program Files (x86)\\AutoDWG\\AutoDWG DWG to PDF Converter\\d2p.exe /InFile";
			String conFile = "C:\\Program Files (x86)\\AutoDWG\\AutoDWG DWG to PDF Converter\\AutoDWGPdf.ddp";

	      	String exec = exec1 + " " + dwgFilePath + " /OutFile " + pdfFileName + " /InConfigFile " + conFile;
	      	System.out.println(exec);
			
	        //占쏙옙체 占쏙옙占쏙옙 占쏙옙占쏙옙 占쏙옙占쏙옙 
	      
	      	Process pr=rt.exec(exec);
	      
	      	StringBuffer stdMsg = new StringBuffer();
	      	ProcessOutputThread o = new ProcessOutputThread(pr.getInputStream(), stdMsg);
	      	o.start();
	      	StringBuffer errMsg = new StringBuffer();
	      	o = new ProcessOutputThread(pr.getErrorStream(), errMsg);
	      	o.start();
	      	pr.waitFor();
	        CommonContentHelper.service.delete(epm, "PDF");
	      	CommonContentHelper.service.attach(epm, pdfFileName, "PDF");
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
}

