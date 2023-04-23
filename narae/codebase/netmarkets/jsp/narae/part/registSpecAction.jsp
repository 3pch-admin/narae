<%@page import="wt.pom.DBProperties"%>
<%@page import="wt.pom.WTConnection"%>
<%@page import="wt.method.MethodContext"%>
<%@page import="java.util.HashMap"%>
<%@page import="ext.narae.service.erp.beans.ERPUtil"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="ext.narae.erp.ERPHelper"%>
<%@page import="ext.narae.util.code.NumberCode"%>
<%@page import="ext.narae.util.code.NumberCodeType"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.DriverManager"%>
<%@page import="java.sql.Statement"%>
<%@page import="java.sql.Connection"%>
<%@page import="ext.narae.util.StringUtil"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%
	//if(name != "" || name != null) {
		String codetype = "SPEC";
		String msg = "";
		MethodContext methodcontext = null;
		WTConnection wtconnection = null;
		ResultSet rs = null;
		Statement stmt = null;
		Connection conn = null;
		try {
		
			String name = StringUtil.checkNull(request.getParameter("name"));
			String engName = StringUtil.checkNull(request.getParameter("engname"));
			String description = StringUtil.checkNull(request.getParameter("description"));
			String disabled = StringUtil.checkNull(request.getParameter("disabled"));
	
				methodcontext = MethodContext.getContext();
				wtconnection = (WTConnection) methodcontext.getConnection();
				 conn = wtconnection.getConnection();
				System.out.println("conn="+conn);
			 stmt = conn.createStatement();
	
			// 규격코드 생성
			String sql = "SELECT max(code) FROM NumberCode WHERE codeType='SPEC'";	
			 rs = stmt.executeQuery(sql);
			
			String code = null;
			while(rs.next()) {
				code = rs.getString(1);
			}
			int maxcode = Integer.parseInt(code)+1;
			String newCode = String.format("%06d", maxcode);
		
	
			// 규격중복 여부
			String sql2 = "SELECT name FROM NumberCode WHERE codeType='SPEC' AND name='"+name+"'";
			ResultSet rs2 = stmt.executeQuery(sql2);
	
			String dupName = null;
			while(rs2.next()) {
				dupName = rs2.getString(1);
			}
		
		// DB 연결종료


			NumberCodeType ctype = NumberCodeType.toNumberCodeType(codetype);
	
	
			// 규격등록
			if(dupName == "" || dupName == null) {
				NumberCode nCode = NumberCode.newNumberCode();
				nCode.setName(name);
				nCode.setEngName(engName);
				nCode.setCode(newCode);
				nCode.setDescription(description);
				nCode.setCodeType(ctype);
				nCode.setDisabled("true".equals(disabled));
				nCode = (NumberCode) PersistenceHelper.manager.save(nCode);
						
				HashMap map = ERPHelper.manager.erpCodeSend(nCode,ERPUtil.SQLCREATE);
						 
				msg =  (String)map.get("message");
			} else {
				msg="입력하신 규격이 이미 등록되어 있습니다. 다시 확인 후 등록해 주세요.";
			}
		}catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null){
				rs.close();
			}
			if(stmt != null) {
				stmt.close();
			}
			if(conn != null) {
				conn.close();
			}
			if (DBProperties.FREE_CONNECTION_IMMEDIATE && !wtconnection.isTransactionActive()) {
				MethodContext.getContext().freeConnection();
			}
		}
%>
<form name=registSpecActionForm method="post"></form>
<script type="text/javascript">
<!--
	var msg = "<%=msg%>";
	alert(msg);
	//document.registSpecActionForm.method = "post";
	//document.registSpecActionForm.action= "/plm/jsp/part/registSpec.jsp";
	//document.registSpecActionForm.submit();
	//history.go(-1);
	//history.go(0);
	//location.href=history.back();
	window.close();
</script>