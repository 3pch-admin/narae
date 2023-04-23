<%@page import="ext.narae.service.change.beans.ChangeHelper2"%>
<%@page import="ext.narae.service.change.*"%>
<%
	out.println(ChangeHelper2.submitChangeObject(request.getParameter("oid")));
%>