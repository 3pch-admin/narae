<jsp:useBean id="wtcontext" class="wt.httpgw.WTContextBean" scope="session" />
<jsp:setProperty name="wtcontext" property="request" value="<%=request%>" />
<%-- e3ps --%>

<%
	wt.org.WTUser checkUser = (wt.org.WTUser) wt.session.SessionHelper.manager.getPrincipal();
	String orgName = checkUser.getOrganizationName();
	if("dist".equals(orgName)) {
%>
	<script>
		alert("이 페이지에 접근할 권한이 없습니다.");
		document.href = history.go(-1);
	</script>
<%
	}
%>