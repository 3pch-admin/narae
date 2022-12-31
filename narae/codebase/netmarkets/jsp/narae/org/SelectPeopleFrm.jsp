<%@page import="ext.narae.util.ParamUtil"%>
<title></title>
<%
String mode = request.getParameter("mode");
String chief = ParamUtil.checkStrParameter(request.getParameter("chief"));
String inputObj = ParamUtil.checkStrParameter(request.getParameter("inputObj"));
String inputLabel = ParamUtil.checkStrParameter(request.getParameter("inputLabel"));
%>
<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
<iframe width=100% height=100% src="/Windchill/netmarkets/jsp/narae/org/SelectPeople.jsp?mode=<%=mode%>&chief=<%=chief%>&inputObj=<%=inputObj%>&inputLabel=<%=inputLabel%>">	
