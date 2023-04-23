<%@page import="ext.narae.util.CommonUtil"%>
<%@page import="wt.lifecycle.State"%>
<%@page import="wt.lifecycle.LifeCycleHelper"%>
<%@page import="ext.narae.erp.ERPDataSender"%>
<%@page import="java.util.Map"%>
<%@page import="ext.narae.erp.ERPPdfSender"%>
<%@page import="ext.narae.erp.ERPInterface"%>
<%@page import="wt.change2.WTChangeOrder2"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="wt.epm.workspaces.EPMWorkspace"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="wt.part.WTPart"%>
<%@page import="wt.epm.EPMDocumentMaster"%>
<%@page import="wt.util.WTException"%>
<%@page import="java.util.Collections"%>
<%@page import="java.util.Vector"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.HashMap"%>
<%@page import="wt.epm.structure.EPMReferenceLink"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="wt.epm.structure.EPMStructureHelper"%>
<%@page import="wt.vc.config.LatestConfigSpec"%>
<%@page import="wt.query.QuerySpec"%>
<%@page import="wt.epm.EPMDocument"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%
System.out.println("reSendERP Start ----");
String oid = request.getParameter("oid");
System.out.println("oid : "+oid);
boolean result = false;
boolean result2 = false;
ERPDataSender sender = new ERPDataSender();
 WTChangeOrder2 eco = (WTChangeOrder2)CommonUtil.getObject(oid);
System.out.println("//=====  Start ECO Number = " + eco.getNumber() + " ======//");
 result = sender.sendECO2(eco);
 
 System.out.println("result="+result);
 if (result) {
     System.out.println("//=======================================//");
     System.out.println("//       ERP Data Interface 성공");
     System.out.println("//=======================================//");
   } else {
     System.out.println("//=======================================//");
     System.out.println("//       ERP Data Interface 실패");
     System.out.println("//=======================================//");
   }

 ERPPdfSender sender2 = new ERPPdfSender();
 result2 = sender2.sendPdf2(eco);
 if (result2) {
	 ext.narae.erp.ERPPdfSender.ConvertDwgToPdf(eco);
	 ext.narae.erp.ERPPdfSender.CopyPdfFiles(eco);
     System.out.println("//=======================================//");
     System.out.println("//       ERP PDF Interface 성공");
     System.out.println("//=======================================//");
   } else {
     System.out.println("//=======================================//");
     System.out.println("//       ERP PDF Interface 실패");
     System.out.println("//=======================================//");
   } 
 
 if (result&&result2) {
	 System.out.println("//=======================================//");
     System.out.println("//       changeState");
     System.out.println("//=======================================//");
     LifeCycleHelper.service.setLifeCycleState(eco, State.toState("SENT_ERP"));
// 	 WorkflowHelper2.changeState2(eco, "SENT_ERP");
 }
%>