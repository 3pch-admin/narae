<%@page import="wt.session.SessionHelper"%>
<%@page import="ext.narae.ui.CodeDispacher"%>
<%
	String key = request.getParameter("key");
	String parentOid =  request.getParameter("parentOid");
	String responseText = "";
	if( key.equals("CADATTRIBUTE") ) {
		System.out.println("=="+parentOid);
		responseText = "$$$PTC_AJAX$$$" + CodeDispacher.getAjaxSubTypes(key,parentOid) + "$$$PTC_AJAX$$$";
	} else {
		System.out.println("=="+key);
		responseText = "$$$PTC_AJAX$$$" + CodeDispacher.getAjaxSubTypes(key) + "$$$PTC_AJAX$$$";
	}

	out.println(responseText);
%>