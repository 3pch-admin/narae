package ext.narae.erp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

import ext.narae.service.part.beans.BomBroker;
import ext.narae.service.part.beans.PartTreeData;
import wt.change2.ChangeException2;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeRequest2;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.method.RemoteAccess;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.util.WTException;

public class ERPInterface
  implements RemoteAccess, Serializable
{
  static final long serialVersionUID = -7046722787051872965L;
  public static final String CONST_EXREG_PART_NO = "^[A-Za-z]{2}-[A-Za-z0-9]{2}-.{10}$";

  public static boolean send(WTObject wtobj) throws Exception
  {
    System.out.println("//=======================================//");
    System.out.println("//       ERP Data Interface 시작");
    System.out.println("//=======================================//");
    boolean result = false;
    ERPDataSender sender = new ERPDataSender();
    try
    {
      if (wtobj.getClass().equals(WTChangeOrder2.class)) {
    	  WTChangeOrder2 eco = (WTChangeOrder2) wtobj;
    	  System.out.println("//===== Interface Start ECO Number = " + eco.getNumber() + " ======//");
    	  result = sender.sendECO(wtobj);
      } else if (wtobj.getClass().equals(WTChangeRequest2.class)) {
    	  WTChangeRequest2 ecr = (WTChangeRequest2) wtobj;
    	  System.out.println("//===== not Interface Start ECR Number = " + ecr.getNumber() + " ======//");
				/* result = sender.sendECR(wtobj); */
      }
    }
    catch (IOException e) {
      System.out.println(e.getStackTrace());
    } catch (SQLException e) {
      System.out.println(e.getStackTrace());
    } catch (WTException e) {
      System.out.println(e.getStackTrace());
    }

    if (result) {
      System.out.println("//=======================================//");
      System.out.println("//       ERP Data Interface 성공");
      System.out.println("//=======================================//");
    } else {
      System.out.println("//=======================================//");
      System.out.println("//       ERP Data Interface 실패");
      System.out.println("//=======================================//");
    }

    return result;
  }

  public static boolean sendPdf(WTObject wtobj)
  {
    System.out.println("//=======================================//");
    System.out.println("//       ERP PDF Interface 시작");
    System.out.println("//=======================================//");
    boolean result = false;
    try
    {
      if (wtobj.getClass().equals(WTChangeOrder2.class))
        result = ERPPdfSender.sendPdf(wtobj);
      else if (wtobj.getClass().equals(WTChangeRequest2.class))
        result = true;
    }
    catch (Exception e) {
      System.out.println(e.getStackTrace());
    }

    if (result) {
      System.out.println("//=======================================//");
      System.out.println("//       ERP PDF Interface 성공");
      System.out.println("//=======================================//");
    } else {
      System.out.println("//=======================================//");
      System.out.println("//       ERP PDF Interface 실패");
      System.out.println("//=======================================//");
    } 

    return result;
  }

  
  //워크 플로우에서 호출할 것으로 예상됨 로직 확인후 업무에 맞게 수정 필요
  public static void chkOrdered()
  {
    try
    {
      ERPOrderChecker.check();
    }
    catch (Exception localException)
    {
    }
  }

  public static WTPart[] getParentPart(WTPart part) {
    ArrayList _parents = new ArrayList();
    try {
      Enumeration e = WTPartHelper.service.getUsedByWTParts((WTPartMaster)part.getMaster()).getEnumeration();
      while (e.hasMoreElements()) {
        WTPart parent = (WTPart)e.nextElement();
        _parents.add(parent);
      }
    } catch (WTException e) {
      System.out.println(e.getStackTrace());
    }

    return (WTPart[])_parents.toArray(new WTPart[_parents.size()]);
  }

  public static WTPart[] getPartList(WTChangeOrder2 eco) {
    ArrayList _parts = new ArrayList();
    try
    {
      QueryResult qr = ChangeHelper2.service.getChangeActivities(eco);
      System.out.println("qr:" + qr.size());
      
      while(qr.hasMoreElements()) {
    	  WTChangeActivity2 act = (WTChangeActivity2)qr.nextElement();
    	  QueryResult qr2 = ChangeHelper2.service.getChangeablesAfter(act);
	        System.out.println("qr2:" + qr2.size());
	        System.out.println("WTChangeActivity2 id : "  + act.getPersistInfo().getObjectIdentifier().getId());
    	  while(qr2.hasMoreElements() ) {
    		  WTPart part = (WTPart)qr2.nextElement();
              System.out.println("part.getName() : "  + part.getName());
              System.out.println("part.getNumber() : "  + part.getNumber());
              System.out.println("part.getVersion() : "  + part.getVersionDisplayIdentity());
              _parts.add(part);
    	  }
      }
      
    }
    catch (ChangeException2 e)
    {
      _parts.clear();
      System.out.println(e.getStackTrace());
    }
    catch (WTException e) {
      _parts.clear();
      System.out.println(e.getStackTrace());
    }
    if (_parts.size() <= 0) {
      return null;
    }

    return (WTPart[])_parts.toArray(new WTPart[_parts.size()]);
  }

  public static WTPart[] getPartList(WTChangeRequest2 ecr) {
    ArrayList _parts = new ArrayList();
    try
    {
      QueryResult qr = ChangeHelper2.service.getChangeables(ecr);
      for (Enumeration parts = qr.getEnumeration(); parts.hasMoreElements(); ) {
        WTPart part = (WTPart)parts.nextElement();
        _parts.add(part);
      }

    }
    catch (ChangeException2 e)
    {
      _parts.clear();
      System.out.println(e.getStackTrace());
    } catch (WTException e) {
      _parts.clear();
      System.out.println(e.getStackTrace());
    }
    if (_parts.size() <= 0) {
      return null;
    }

    return (WTPart[])_parts.toArray(new WTPart[_parts.size()]);
  }

  public static HashMap<WTPart, Double> getChildren(WTPart part) {
    HashMap result = new HashMap();
    BomBroker broker = new BomBroker();
    PartTreeData root = null;
    try {
      root = broker.getTree(part, false, null);
      ArrayList children = new ArrayList();
      broker.setHtmlForm(root, children);
      for (int i = 0; i < children.size(); i++) {
        PartTreeData child = (PartTreeData)children.get(i);
        if (!child.part.getNumber().equals(part.getNumber()))
          result.put(child.part, Double.valueOf(child.quantity));
      }
    } catch (WTException e) {
      System.out.println(e.getStackTrace());
    }
    return result;
  }

  public static int PartCount(WTPart parent, WTPart child) {
    int cnt = 0;
    try {
      QueryResult links = WTPartHelper.service.getUsesWTPartMasters(parent);
      for (Enumeration e = links.getEnumeration(); e.hasMoreElements(); ) {
        WTPartUsageLink link = (WTPartUsageLink)e.nextElement();
        WTPartMaster part = (WTPartMaster)link.getPersistable();
        if (!part.getNumber().equals(child.getNumber())) continue; cnt++;
      }
    } catch (WTException e) {
      System.out.println(e.getStackTrace());
    }
    return cnt;
  }

  public static String HtmlEncode(String str) {
    String restr = str;
    restr = restr.replace("'", "&#39;");
    restr = restr.replace("<", "&lt;");
    restr = restr.replace(">", "&gt;");
    restr = restr.replace("\"", "&quot;");
    restr = restr.replace(" ", "&nbsp;");
    restr = restr.replace("&", "&amp;");
    return restr;
  }

  public static String executeCommand(String[] command, String dir, int cnt)
    throws IOException, InterruptedException
  {
    StringBuffer output = new StringBuffer();

    Process p = Runtime.getRuntime().exec(command);
    p.waitFor();
    BufferedReader reader = 
      new BufferedReader(new InputStreamReader(p.getInputStream()));
    String line = "";
    while ((line = reader.readLine()) != null) {
      output.append(line + "\n");
    }

    return output.toString();
  }

  public static void copyFile2(String inFileName, String outFileName)
    throws IOException
  {
    FileInputStream fis = new FileInputStream(inFileName);
    FileOutputStream fos = new FileOutputStream(outFileName);

    int data = 0;
    while ((data = fis.read()) != -1) {
      fos.write(data);
    }
    fis.close();
    fos.close();
  }

  public static void copyFile(String srFile, String dtFile)
    throws IOException
  {
    File f1 = new File(srFile);
    File f2 = new File(dtFile);
    InputStream in = new FileInputStream(f1);

    OutputStream out = new FileOutputStream(f2);

    byte[] buf = new byte[1024];
    int len;
    while ((len = in.read(buf)) > 0)
    {
      out.write(buf, 0, len);
    }
    in.close();
    out.close();
  }
}

