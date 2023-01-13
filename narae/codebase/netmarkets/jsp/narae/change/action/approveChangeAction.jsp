<%@page import="ext.narae.service.workflow.beans.WorkflowHelper2"%>
<%@page import="ext.narae.service.workflow.*"%>
<%
	out.println(WorkflowHelper2.approveWorkItem(request.getParameter("oid"), request.getParameter("approvalDesc")));
%>