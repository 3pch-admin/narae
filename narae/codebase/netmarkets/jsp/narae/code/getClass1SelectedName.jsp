<%@page import="wt.fc.WTReference"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="ext.narae.util.code.NumberCode"%>
<%@page import="wt.session.SessionHelper"%>
<%
	String oid = request.getParameter("oid");
	ReferenceFactory referencefactory = new ReferenceFactory();
	WTReference wtreference = referencefactory.getReference( oid );
	NumberCode code = (NumberCode)wtreference.getObject();

	out.println(code.getName());
%>