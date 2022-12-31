<%@page import="wt.session.SessionHelper"%>
<%@page import="ext.narae.ui.CodeDispacher"%>
<%
	String key = request.getParameter("key");
	String parentOid =  request.getParameter("parentOid");
	String responseText = "";
	if( key.equals("CADATTRIBUTE") ) {
		responseText = "$$$PTC_AJAX$$$" + CodeDispacher.getAjaxSubTypes(key,parentOid) + "$$$PTC_AJAX$$$";
	} else {
		responseText = "$$$PTC_AJAX$$$" + CodeDispacher.getAjaxSubTypes(key) + "$$$PTC_AJAX$$$";
	}

	out.println(responseText);
%>