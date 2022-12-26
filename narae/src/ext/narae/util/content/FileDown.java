package ext.narae.util.content;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import ext.narae.service.erp.beans.ERPUtil;
import ext.narae.util.CommonUtil;
import ext.narae.util.StringUtil;
import wt.content.ApplicationData;
import wt.content.ContentItem;
import wt.content.ContentServerHelper;
import wt.util.WTException;
import wt.util.WTProperties;

public class FileDown implements wt.method.RemoteAccess{

	/**
	 * @param args
	 */
	static final boolean SERVER = wt.method.RemoteMethodServer.ServerFlag;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String oid = args[0];
		try {
			HashMap map = new HashMap();
			map.put("oid", oid);
			pdfDown(map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static HashMap pdfDown( HashMap  map ) throws Exception{
		
		if(!SERVER) {
			Class argTypes[] = new Class[]{HashMap.class};
			Object args[] = new Object[]{map};
			try {
				return (HashMap)wt.method.RemoteMethodServer.getDefault().invoke("pdfDown",FileDown.class.getName(),null,argTypes,args);
			}
			catch(RemoteException e) {
				e.printStackTrace();
				throw new WTException(e);
			}
			catch(InvocationTargetException e) {
				e.printStackTrace();
				throw new WTException(e);
			}
			catch(Exception e){
				e.printStackTrace();
				throw e;
			}
		}
		
		HashMap mapRe = new HashMap();
		try{
			String tempPath = "";
			String oid 			= (String)map.get("oid");
			String tempDir 		= (String)map.get("tempDir");
			String pdfFileName 	= (String)map.get("pdfFileName");
			String epmType		= StringUtil.checkNull((String)map.get("epmType"));
			if(tempDir == null){
				tempDir = WTProperties.getLocalProperties().getProperty("wt.temp");
			}
			
			File tempFolder = new File(tempDir);
			if(!tempFolder.isDirectory()){
				tempFolder.mkdirs();
			}
			
			ApplicationData adata = (ApplicationData)CommonUtil.getObject(oid);
			
			byte[] buffer = new byte[1024];
			    InputStream is = null;
			    if(epmType.equals("AutoCad")){
			    	
			    	//is = ContentServerHelper.service.findContentStream(adata);
			    	is = ContentServerHelper.service.findLocalContentStream(adata);
			    	//System.out.println("::::::::::::::::::::: InputStream is : " + is);
			    	//InputStream is2 = ContentServerHelper.service.findLocalContentStream(adata);
			    	//System.out.println("::::::::::::::::::::: InputStream is2 : " + is2);
			    }else{
			    	is = ContentServerHelper.service.findLocalContentStream(adata);
			    }
			    
			   
			    if(pdfFileName == null ) pdfFileName = adata.getFileName();
		        File tempfile = new File(tempDir + File.separator + pdfFileName);
		        FileOutputStream fos = new FileOutputStream(tempfile);
		        tempPath = tempfile.getPath();
		        
		        int j = 0;
		        while ((j = is.read(buffer, 0, 1024)) > 0){
		        	
		        	fos.write(buffer, 0, j);
		        }
		            
		        
		        fos.close();
		        is.close();
		        
		        mapRe.put("tempPath", tempPath);
		        mapRe.put("result", ERPUtil.PDF_SEND_SUCCESS);
		        mapRe.put("message", "성공");
		}catch(Exception e){
			mapRe.put("tempPath", "");
	        mapRe.put("result", ERPUtil.PDF_SEND_FAILE);
	        mapRe.put("message", e.getMessage());
			e.printStackTrace();
		}
		
		return mapRe;
		
	}
	
	public static void pdfd( HashMap map,HttpServletResponse response ) throws Exception{
		
		if(!SERVER) {
			Class argTypes[] = new Class[]{HashMap.class,HttpServletResponse.class};
			Object args[] = new Object[]{map,response};
			try {
				wt.method.RemoteMethodServer.getDefault().invoke("pdfd",FileDown.class.getName(),null,argTypes,args);
			}
			catch(RemoteException e) {
				e.printStackTrace();
				throw new WTException(e);
			}
			catch(InvocationTargetException e) {
				e.printStackTrace();
				throw new WTException(e);
			}
			catch(Exception e){
				e.printStackTrace();
				throw e;
			}
			
			return;
		}
		
		try{
			String oid =(String)map.get("oid");
			String tempDir = WTProperties.getLocalProperties().getProperty("wt.temp");
			ContentItem item = null;
			
			
			ApplicationData adata = (ApplicationData)CommonUtil.getObject(oid);
			System.out.println("ApplicationData :" + adata.getFileName());
	        byte[] buffer = new byte[1024];
			    InputStream in = ContentServerHelper.service.findLocalContentStream(adata);
			   
			    BufferedInputStream fin = null;
                fin = new BufferedInputStream(in);
                
                BufferedOutputStream outs = new BufferedOutputStream(response.getOutputStream());
			  
		        int read = 0;
                byte b[] = new byte[4096];
                try {
                    while ((read = fin.read(b)) != -1){
                        outs.write(b,0,read);
                    }
                    outs.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if(outs != null)
                            outs.close();
                        if(in != null)
                            fin.close();
                    }
                    catch(IOException e) {
                        e.printStackTrace();
                    }
                }
		        
		       
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

}
