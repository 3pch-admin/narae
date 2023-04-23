<%@page import="ext.narae.service.erp.beans.ERPUtil"%>
<%@page import="ext.narae.util.db.DBConnectionManager"%>
<%@page import="ext.narae.util.StringUtil"%>
<%@page import="wt.folder.SubFolder"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>

<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Hashtable"%>
<%@page import="java.util.StringTokenizer"%>
<%@page import="java.util.Vector"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="wt.folder.FolderHelper"%>
<%@page import="wt.folder.Folder"%>

<%@page import="wt.query.ClassAttribute"%>
<%@page import="wt.query.OrderBy"%>
<%@page import="wt.query.QuerySpec"%>
<%@page import="wt.query.SearchCondition"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="wt.pom.WTConnection"%>
<%@page import="wt.pom.DBProperties"%>
<%@page import="wt.method.MethodContext"%>
<%@page import="java.rmi.RemoteException"%>

<%@page import="java.sql.*"%>

<%@page import="wt.query.KeywordExpression"%>

<%
		String msg = "";

	//if(name != "" || name != null) {
		String codetype = "MAKER";
		
		String mkrCode = StringUtil.checkNull(request.getParameter("mkrCode"));
		String mkrName = StringUtil.checkNull(request.getParameter("mkrName"));
		String description = StringUtil.checkNull(request.getParameter("description"));
		String disabled = StringUtil.checkNull(request.getParameter("disabled"));

		if(mkrName.length()>0) {
			
			// DB Connection
			DBConnectionManager db = DBConnectionManager.getInstance();
	        Connection con = db.getConnection(ERPUtil.ERP);
	        
			StringBuilder sql = new StringBuilder();
			sql.append("insert tcb09 (Maker, MkrName, OldCode, LstSvEpCode, LstSvDate) ");
			sql.append("select top 1 Maker+1 Maker, '"+mkrName+"' MkrName, OldCode, LstSvEpCode, LstSvDate " );
			sql.append("from tcb09 " );
			sql.append("where not exists( select Maker from tcb09 where MkrName = '"+mkrName+"') " );
			sql.append("order by maker desc" );
		
			PreparedStatement ps = con.prepareStatement(sql.toString());

			// 실행 실패
			if(ps.executeUpdate()>0){
				msg = "성공적으로 저장 했습니다.";
			} else {
				msg = "저장 도중 오류가 발생했습니다. \\n\\n잠시 후 다시 시도해주십시오.";
			}
			
			// 연결 해제
			ps.close();
			db.freeConnection(ERPUtil.ERP, con);
			
		} else {
			msg = "저장할 값이 잘못되었습니다.";
		}
		

%>
<form name=registSpecActionForm method="post">
</form>
<script type="text/javascript">
<!--
	var msg = "<%=msg%>";
	alert(msg);
	var opener = window.dialogArguments;
	opener.submitDocSearch();
	self.close();
</script>