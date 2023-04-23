<%@page import="ext.narae.service.change.beans.ChangeHelper2"%>
<%
	out.println(ChangeHelper2.withdrawChangeObject(request.getParameter("oid")));
%>