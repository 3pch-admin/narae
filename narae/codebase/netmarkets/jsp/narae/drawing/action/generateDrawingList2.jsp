<%@page import="ext.narae.util.content.ContentUtil"%>
<%@page contentType="text/html; charset=utf-8"%>
<%@ page import="wt.util.*, wt.org.*, wt.session.*, wt.inf.container.*, ext.narae.util.*, ext.narae.ui.*, java.util.*" %>
<%@ page import="ext.narae.service.part.*" %>
<%@ page import="wt.epm.*" %>
<%@ page import="ext.narae.service.*" %>

<jsp:useBean id="wtcontext" class="wt.httpgw.WTContextBean" scope="request" />
<jsp:setProperty name="wtcontext" property="request" value="<%= request %>" />


<%
String partList = request.getParameter("parentOid");

System.out.println("---------------------------" + partList);
String[] partListArray = partList.replace("$$$PTC$$$", "||").split("[|][|]");
System.out.println("--------------------------" + partListArray[0]);
String name = null;
String number = null;
String version = null;
String returnString = "";
String test = "";
String[] dwg;
String[] pdf;
String dwgURL = "";
String pdfURL = "";
for( int index=0; index < partListArray.length; index++) {
	EPMDocument part = (EPMDocument)CommonUtil2.getInstance(partListArray[index]);
	number = part.getNumber();
	name = part.getName();
	test = CommonUtil.getOIDString(part);
	version = part.getVersionIdentifier().getValue()+"."+part.getIterationIdentifier().getValue();
	dwg = ContentUtil.getDWG(part);
	pdf = ContentUtil.getPDF(part);
	dwgURL = dwg[5];
	pdfURL = pdf[5];
	System.out.println("number ::: "+number+"   name :::  "+name+"  dwg ::: "+dwgURL+"  test ::: "+test);
	if( index == 0 ) {
		returnString = part.getPersistInfo().getObjectIdentifier().toString() + "$$$item$$$" + number + "$$$item$$$" + name + "$$$item$$$" + version+ "$$$item$$$" + dwgURL+ "$$$item$$$" + pdfURL;
	} else {
		returnString = returnString + "$$$PTC$$$" + part.getPersistInfo().getObjectIdentifier().toString() + "$$$item$$$" + number + "$$$item$$$" + name + "$$$item$$$" + version+ "$$$item$$$" + dwgURL+ "$$$item$$$" + pdfURL;
	}
}


out.println(returnString);


%>
