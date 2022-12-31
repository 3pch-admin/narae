<%@page import="ext.narae.util.ParamUtil"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>

<link rel="stylesheet" href="/Windchill/netmarkets/jsp/narae/css/e3ps.css" type="text/css">
<%
String mode = request.getParameter("mode");
String chief = ParamUtil.checkStrParameter(request.getParameter("chief"));
String inputObj = ParamUtil.checkStrParameter(request.getParameter("inputObj"));
String inputLabel = ParamUtil.checkStrParameter(request.getParameter("inputLabel"));
String listLoc = "/netmarkets/jsp/narae/org/SelectPeopleMain.jsp?mode="+mode+"&chief="+chief+"&inputObj=" +inputObj + "&inputLabel="+inputLabel;

%>	
<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
<table border="0" width="100%" height="100%" cellpadding="0" cellspacing="0">
	<tr>
		<td valign=top width=180 background="/Windchill/netmarkets/jsp/narae/portal/images/ds_sub.gif" bgcolor=ffffff >
			<jsp:include page="/netmarkets/jsp/narae/org/SelectPeopleTree.jsp" flush="true"/>	
		</td>
		<td valign=top>
			<jsp:include page="<%=listLoc%>" flush="true"/>	
		</td>
	</tr>
</table>				
