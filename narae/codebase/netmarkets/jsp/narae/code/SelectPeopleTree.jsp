<%@page import="ext.narae.service.org.beans.OrgDao"%>
<%@page import="ext.narae.util.ParamUtil"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>

<%@page import="java.util.*" %>

<%
	String mode = request.getParameter("mode");
	String chief = ParamUtil.checkStrParameter(request.getParameter("chief"));
	String inputObj = ParamUtil.checkStrParameter(request.getParameter("inputObj"));
	String inputLabel = ParamUtil.checkStrParameter(request.getParameter("inputLabel"));	
	if(mode==null)mode="";
%>

<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
<form name=departmentTreeForm method=post>
<input type="hidden" name="command">
<input type="hidden" name="soid">
<input type="hidden" name="mode" value="<%=mode%>">
<input type="hidden" name="chief" value="<%=chief%>">
<input type="hidden" name="inputObj" value="<%=inputObj%>">
<input type="hidden" name="inputLabel" value="<%=inputLabel%>">
	<link rel="StyleSheet" href="/Windchill/netmarkets/jsp/narae/css/dtree.css" type="text/css"/>
	<script type="text/javascript" src="/Windchill/netmarkets/jsp/narae/js/dtree.js"></script>

	<table width=170  height="100%"><tr><td valign=top>
	<script type="text/javascript">

		function setDepartment(oidValue) {
			document.departmentTreeForm.action = "SelectPeople.jsp";
			document.departmentTreeForm.soid.value = oidValue;
			document.departmentTreeForm.submit();
		}

		var departmentTree = new dTree('departmentTree','/Windchill/netmarkets/jsp/narae/org/images/tree');
//		departmentTree.add(0,-1,'ROOT',"JavaScript:setDepartment('root');");
		departmentTree.add(0,-1,'ROOT',"");
		
<%
		ArrayList list = OrgDao.service.getDepartmentTree(0L);

		for(int i=0; i< list.size(); i++){
			String[] node = (String[])list.get(i);
			String levels = node[0];
			int level = Integer.parseInt(levels);
			
			String tempId = node[2];
			tempId = tempId.substring ( tempId.lastIndexOf ( ":" ) + 1 );
			int id = Integer.parseInt ( tempId );
			     
			int parentId = 0;
			if(level > 1) {
				String tempoid = node[5];
				tempoid = tempoid.substring ( tempoid.lastIndexOf ( ":" ) + 1 );
				parentId = Integer.parseInt ( tempoid );
			}
%>
			departmentTree.add(<%=id%>,<%=parentId%>,'<%=node[1]%>',"JavaScript:setDepartment('<%=node[2]%>');","","","/Windchill/netmarkets/jsp/narae/org/images/tree/opened_default_folder.png","/Windchill/netmarkets/jsp/narae/org/images/tree/opened_default_folder.png");
<%
		}
%>
		document.write(departmentTree);
	</script>
</td><td width=7  background="/Windchill/netmarkets/jsp/narae/portal/images/barFrame_bg.gif"></td></tr></table>
</form>
