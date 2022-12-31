<%@page import="wt.session.SessionHelper"%>
<%@page import="wt.fc.*, com.e3ps.common.code.NumberCode"%>
<%
	String oid = request.getParameter("oid");
	ReferenceFactory referencefactory = new ReferenceFactory();
	WTReference wtreference = referencefactory.getReference( oid );
	NumberCode code = (NumberCode)wtreference.getObject();

	out.println(code.getName());
%>