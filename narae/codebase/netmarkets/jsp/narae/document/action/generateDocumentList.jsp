<%@page contentType="text/html; charset=utf-8"%>
<%@ page import="wt.util.*, wt.org.*, wt.session.*, wt.inf.container.*, ext.narae.ui.*, java.util.*" %>
<%@ page import="ext.narae.service.part.*" %>
<%@ page import="wt.doc.*" %>
<%@ page import="ext.narae.service.*" %>

<jsp:useBean id="wtcontext" class="wt.httpgw.WTContextBean" scope="request" />
<jsp:setProperty name="wtcontext" property="request" value="<%= request %>" />


<%
String partList = request.getParameter("parentOid");

String[] partListArray = partList.replace("$$$PTC$$$", "||").split("[|][|]");
String name = null;
String number = null;
String version = null;
String returnString = "";
for( int index=0; index < partListArray.length; index++) {
	WTDocument part = (WTDocument)CommonUtil2.getInstance(partListArray[index]);
	number = part.getNumber();
	name = part.getName();
	version = part.getVersionIdentifier().getValue();
	if( index == 0 ) {
		returnString = part.getPersistInfo().getObjectIdentifier().toString() + "$$$item$$$" + number + "$$$item$$$" + name + "$$$item$$$" + version;
	} else {
		returnString = returnString + "$$$PTC$$$" + part.getPersistInfo().getObjectIdentifier().toString() + "$$$item$$$" + number + "$$$item$$$" + name + "$$$item$$$" + version;
	}
}


out.println(returnString);


%>
