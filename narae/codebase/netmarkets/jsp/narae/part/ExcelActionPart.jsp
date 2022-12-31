<%@page import="ext.narae.util.ParamUtil"%>
<%@page import="ext.narae.service.part.beans.PartExcelLoader"%>
<%@page import="ext.narae.util.content.multipart.UploadFile"%>
<%@page import="ext.narae.util.content.multipart.MultipartHelperImpl"%>
<%@page import="java.io.PrintWriter"%>
<%@page import="java.io.FileWriter"%>
<%@page import="wt.util.WTProperties"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>

<%@page import="java.io.File,
						java.util.Hashtable,
						java.util.Vector"%>

<%
	String cmd = "";
	Hashtable param = null;
	File newfile = null;
	Vector vec = new Vector();
	
	if(request.getContentType() != null && request.getContentType().startsWith("multipart/form-data")) {
		MultipartHelperImpl multi = new MultipartHelperImpl(request);
	 	UploadFile f = multi.getFile("excelFile");
	 	newfile = f.getFile();
	 	param = multi.getParams();
	 	cmd =(String)param.get("cmd");
	}
	
	if("excelLoad".equals(cmd))   vec = PartExcelLoader.upload(newfile,param);
%>

<link rel="stylesheet" type="text/css" href="/Windchill/netmarkets/jsp/narae/css/e3ps.css" />
<table width="100%" border="0" cellpadding="0" cellspacing="3" >
    <tr align="center">
        <td valign="top" style="padding:0px 0px 0px 0px">
       
            <table width="100%" border="0" cellpadding="1" cellspacing="1" bgcolor="#9CAEC8" align="center">
                <tr>
                    <td height="1" width="100%"></td>
                </tr>
            </table>
            <div style="width:760px;height:300px;poisition:relative;overflow:auto">
            <table  width="1800" border="0" cellpadding="0" cellspacing="0" align="center"
                   style="border-left-width: 1px;border-left-style: solid;border-left-color: #e6e6e6;">
                <tr>
                    <td class="tdblueM" width="30">No</td>
                    <td class="tdblueM" width="100">구코드</td>
                    <td class="tdblueM" width="100">경로</td>
                    <td class="tdblueM" width="50">단위</td>
                    <td class="tdblueM" width="100">maker</td>
                    <td class="tdblueM" width="100">규격</td>
                    <td class="tdblueM" width="60">도면유무</td>
                    <td class="tdblueM" width="50">작성자</td>
                    <td class="tdblueM" width="50">GROUP</td>
                    <td class="tdblueM" width="50">Type</td>
                    <td class="tdblueM" width="50">Unit</td>
                    <td class="tdblueM" width="50">Class1</td>
                    <td class="tdblueM" width="50">Class2</td>
                    <td class="tdblueM" width="50">Class3</td>
                    <td class="tdblueM" width="50">Class4</td>
                    <td class="tdblueM" width="150">New Number</td>
                    <td class="tdblueM" width="100">상태</td>
                    <td class="tdblueM" width="80">결과</td>
                    <td class="tdblueM" width="580">결과정보</td>
                </tr>
            <%
            int h =1; 
            String wt_home = WTProperties.getServerProperties().getProperty("wt.home");
            String logFile = wt_home +
           						File.separator + "loadFiles" + 
           						File.separator + "narae" + 
           						File.separator + "Part.log";
            FileWriter writer = new FileWriter(new File(logFile));
    		PrintWriter log = new PrintWriter(writer);
            for( int i = 0 ; i<vec.size() ; i++) {
            	String bgcolor ="#ffffff";
            	Hashtable hash = (Hashtable)vec.get(i);
            		
            	String oldNumber =(String)hash.get("oldNumber");
            	String location =(String)hash.get("location");
            	location = location.replace("/Default/Part","");
            	String quantityunit =(String)hash.get("quantityunit");
            	String maker =(String)hash.get("maker");
            	String spec =(String)hash.get("spec");
            	String isDrawing =(String)hash.get("isDrawing");
            	String userId =(String)hash.get("designed1");
            	String group =(String)hash.get("group");
            	String type =(String)hash.get("type");
            	String unit =(String)hash.get("unit");
            	String class1 =(String)hash.get("class1");
            	String class2 =(String)hash.get("class2");
            	String class3 =(String)hash.get("class3");
            	String class4 =(String)hash.get("class4");
            	String rslt =(String)hash.get("rslt");
            	String msg =(String)hash.get("msg");
            	if(msg == null) msg = "";
            	String newNumber =ParamUtil.checkStrParameter((String)hash.get("newNumber"));
            	String state = ParamUtil.checkStrParameter((String)hash.get("state"));
            	
            	if("F".equals(rslt)) {
            		bgcolor="#33FF00";
            	}
            	
            	String logResult = "S".equals(rslt)? "등록성공" : "등록실패";
            	if("F".equals(rslt)){
            		System.out.println(oldNumber+":::::: "+msg);
        			log.println(oldNumber+":" + msg );
            	}
            	
        	
            %>
                <tr>
                    <td class="tdwhiteM" style="background-color:<%=bgcolor%>;"><%=h++ %>&nbsp;</td>
                    <td class="tdwhiteM" style="background-color:<%=bgcolor%>"><%=oldNumber %>&nbsp;</td>
                    <td class="tdwhiteL" style="background-color:<%=bgcolor%>"><%=location%>&nbsp;</td>
                    <td class="tdwhiteM" style="background-color:<%=bgcolor%>"><%=quantityunit%>&nbsp;</td>
                    <td class="tdwhiteM" style="background-color:<%=bgcolor%>"><%=maker%>&nbsp;</td>
                    <td class="tdwhiteM" style="background-color:<%=bgcolor%>"><%=spec %>&nbsp;</td>
                    <td class="tdwhiteM" style="background-color:<%=bgcolor%>"><%=isDrawing %>&nbsp;</td>
                    <td class="tdwhiteM" style="background-color:<%=bgcolor%>"><%=userId %>&nbsp;</td>
                    <td class="tdwhiteM" style="background-color:<%=bgcolor%>"><%=group%>&nbsp;</td>
                    <td class="tdwhiteM" style="background-color:<%=bgcolor%>"><%=type%>&nbsp;</td>
                    <td class="tdwhiteM" style="background-color:<%=bgcolor%>"><%=unit%>&nbsp;</td>
                    <td class="tdwhiteM" style="background-color:<%=bgcolor%>"><%=class1%>&nbsp;</td>
                    <td class="tdwhiteM" style="background-color:<%=bgcolor%>"><%=class2 %>&nbsp;</td>
                    <td class="tdwhiteM" style="background-color:<%=bgcolor%>"><%=class3 %>&nbsp;</td>
                    <td class="tdwhiteM" style="background-color:<%=bgcolor%>"><%=class4 %>&nbsp;</td>
                    <td class="tdwhiteM" style="background-color:<%=bgcolor%>"><%=newNumber %>&nbsp;</td>
                    <td class="tdwhiteM" style="background-color:<%=bgcolor%>"><%=state %>&nbsp;</td>
                    <td class="tdwhiteM" style="background-color:<%=bgcolor%>"><%="S".equals(rslt)? "등록성공" : "등록실패"%>&nbsp;</td>
                    <td class="tdwhiteM" style="background-color:<%=bgcolor%>"><%=msg %>&nbsp;</td>
                </tr>
             <%}
            log.close();
            %>
            
            </table>
        </div>
        </td>
    </tr>
</table>
<script>

<%if("excelLoad".equals(cmd)){ %>
	
	var div1 = parent.document.getElementById('div1');
	var div2 = parent.document.getElementById('div2');
	
	div1.style.display = "none";
	div2.style.display = "none";
	
<%} %>
</script>
