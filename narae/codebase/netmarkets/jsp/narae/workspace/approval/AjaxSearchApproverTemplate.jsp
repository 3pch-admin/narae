<%@page import="ext.narae.util.StringUtil"%>
<%@page import="wt.query.SearchCondition"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="wt.query.QuerySpec"%>
<%@page import="ext.narae.service.approval.ApprovalLineTemplate"%>
<%@page import="ext.narae.service.approval.ApprovalLineTemplate2"%>
<%@page contentType ="text/xml; charset=utf-8" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@page import ="java.util.*" %>
<%@page import ="wt.fc.*" %>
<%@page import ="wt.org.*" %>
<models>
<%
			request.setCharacterEncoding("utf-8");
			ReferenceFactory rf = new ReferenceFactory();

			QuerySpec qs = new QuerySpec(ApprovalLineTemplate2.class);
			WTUser user = (WTUser)SessionHelper.manager.getPrincipal();
			qs.appendWhere(new SearchCondition(ApprovalLineTemplate.class,"owner","=", user.getName()),new int[]{0});

			QueryResult qr = PersistenceHelper.manager.find(qs);
			//System.out.println("@@@ qs = " + qs);
			 
			String title = "";
			ApprovalLineTemplate2 template = null;
			
			if( qr != null ){ 
				//System.out.println("@@@ people size = " + qr.size());
				while(qr.hasMoreElements()){
					template = (ApprovalLineTemplate2)qr.nextElement();
					title = template.getTitle();
%>
<template>
<title><%=StringUtil.checkNull( title )%></title>
<oid><%=StringUtil.checkNull( PersistenceHelper.getObjectIdentifier ( template ).getStringValue())%></oid>
</template>
<%                    
				}
			}
%>
</models>
