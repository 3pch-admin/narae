<%@page import="wt.session.SessionHelper"%>
<%@page import="ext.narae.ui.CodeDispacher"%>
<%
	String key = request.getParameter("key");
	String parentOid =  request.getParameter("parentOid");
	String responseText = "";
	if( key.equals("CADATTRIBUTE") ) {
		System.out.println("222222=="+parentOid);
		responseText = "$$$PTC_AJAX$$$" + CodeDispacher.getAjaxSubTypes(key,parentOid) + "$$$PTC_AJAX$$$";
	} else {
		System.out.println("gggg=="+key);
		responseText = "$$$PTC_AJAX$$$" + CodeDispacher.getAjaxSubTypes(key) + "$$$PTC_AJAX$$$";
	}

	out.println(responseText);
%>