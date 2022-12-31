<%@page import="wt.session.SessionHelper"%>
<%@page import="ext.narae.ui.CodeDispacher"%>
<%
	String key = request.getParameter("key");
	String parentOid =  request.getParameter("parentOid");
	String responseText = "";
	responseText = "$$$PTC_AJAX$$$" + CodeDispacher.getAjaxProductAssyCode(key,parentOid) + "$$$PTC_AJAX$$$";

	out.println(responseText);
%>