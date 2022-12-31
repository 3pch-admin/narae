<%@page import="wt.session.SessionHelper"%>
<%@page import="ext.narae.ui.CodeDispacher"%>
<%
	String responseText = "";
	responseText = "$$$PTC_AJAX$$$" + CodeDispacher.getAjaxProductCode() + "$$$PTC_AJAX$$$";

	out.println(responseText);
%>