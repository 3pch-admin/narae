<%@page import="ext.narae.service.drawing.beans.EpmPublishUtil"%>
<%@page import="ext.narae.util.CommonUtil"%>
<%@page import="ext.narae.util.StringUtil"%>
<%@page import="wt.epm.EPMDocument"%>
<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%
	String oid = StringUtil.checkNull(request.getParameter("oid"));
	try{
		if(oid.length()>0){
			Object obj = CommonUtil.getObject(oid);
			if(null!=obj && obj instanceof EPMDocument){
				EPMDocument epm = (EPMDocument) obj;
				EpmPublishUtil.publish(epm);
			}
		}
	}catch(Exception e){}

%>