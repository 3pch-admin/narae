<%@page import="ext.narae.util.CommonUtil"%>
<%@page import="ext.narae.service.iba.beans.AttributeHelper"%>
<%@page import="ext.narae.service.workflow.beans.WorkflowHelper2"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>

<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf" %>
<%@ taglib prefix="mvc" uri="http://www.ptc.com/windchill/taglib/jcaMvc"%>
<%@ taglib prefix="wrap" uri="http://www.ptc.com/windchill/taglib/wrappers"%>
<%@ taglib prefix="narae" uri="http://www.ptc.com/windchill/taglib/narae"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="wctags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="attachments" uri="http://www.ptc.com/windchill/taglib/attachments" %>
<link rel="stylesheet" href="/Windchill/netmarkets/jsp/narae/css/e3ps.css">
<%@ page import="ext.narae.service.change.*,java.sql.*,java.text.*, wt.util.*, wt.org.*, wt.session.*, wt.inf.container.*, ext.narae.ui.*, java.util.*, wt.change2.*, ext.narae.service.*, ext.narae.service.iba.*" %>
<%@ page import="ext.narae.ui.common.resource.*, ext.narae.service.*,wt.content.*,wt.query.*,wt.fc.*, ext.narae.service.workflow.*, wt.workflow.work.*,ext.narae.component.*" %>
<link rel="stylesheet" href="/Windchill/netmarkets/jsp/narae/css/e3ps.css">
<script language="JavaScript" src="/Windchill/netmarkets/jsp/narae/js/common.js"></script>
						
<%
String TEST_SERVER = "wc10.ptc.com";
String RESOURCE = "ext.narae.ui.common.resource.NaraeCommonRB";
String TYPERB = "ext.narae.ui.common.resource.RequestTypeRB";
String STOCKRB = "ext.narae.ui.common.resource.StockControlRB";
String ECOTYPERB = "ext.narae.ui.common.resource.ECOTypeRB";
Locale locale = WTContext.getContext().getLocale();
System.out.println("======> locale:" + locale);
String M001 = WTMessage.getLocalizedMessage(RESOURCE , "M001", new Object[]{}, WTContext.getContext().getLocale());

WTUser user = (WTUser)SessionHelper.manager.getPrincipal();
boolean isAdmin = false;


String oid = request.getParameter("oid");
String from = request.getParameter("from");
if( from == null || from.length() == 0 ) {
// 	from = "normal";
	from = "worklist";
}
from = "worklist";
System.out.println("ECO=====> from:" + from);

WTChangeOrder2 change = (WTChangeOrder2)CommonUtil2.getInstance(oid);
String state = change.getState().getState().getStringValue();
String creator = ((WTUser)change.getCreator().getObject()).getAuthenticationName();
WorkItem workitem = null;
String buttonType = "ONLY_VIEW";
WorkItemVO workitemBean = null;
String workitemOid = "";
if(user.getFullName().equals("wcadmin") ){
	System.out.println("user : "+user.getFullName());
	System.out.println("state : "+state);
	isAdmin = true;
}
System.out.println("ECO=====> state:" + state);
if( !ServerConfigHelper.getServerHostName().equals(TEST_SERVER) ) {
	if( state.equals("wt.lifecycle.State.INWORK") || state.equals("wt.lifecycle.State.RETURN")) {
		if( creator.equals(user.getAuthenticationName()) ||isAdmin) {
			buttonType = "EDIT_SUBMIT";
		} else {
			buttonType = "ONLY_VIEW";
		}
	} else if( state.equals("wt.lifecycle.State.CHECKWAIT") || state.equals("wt.lifecycle.State.APPROVEWAIT") ) {
		if( creator.equals(user.getAuthenticationName())|| isAdmin ) {
			// 만약 본인이 결재자면 결재 화면과 버튼이 보여야 함.
			// 결재완료된 것은 보이지 않아야 함.
			buttonType = "CANCEL";
			if( from.equals("worklist") ) {
				//buttonType = "CANCEL_APPROVE"
				workitem = WorkflowHelper2.getTargetWorkItem(user, change);
				if( workitem != null ) {
					workitemBean = new WorkItemVO(workitem);
					workitemOid = workitemBean.getOid();
					if( !workitemBean.getTaskName().equals("RECEIVE") ) { 
						buttonType = "CANCEL_APPROVE";
					} else {
						buttonType = "RECEIVE";
					}
				}
			}
		} else {
			buttonType = "ONLY_VIEW";
			//buttonType = "VIEW_APPROVE"
			if( from.equals("worklist") ) {
				workitem = WorkflowHelper2.getTargetWorkItem(user, change);
				if( workitem != null ) {
					workitemBean = new WorkItemVO(workitem);
					workitemOid = workitemBean.getOid();
					System.out.println("ECO=====> workitemBean.getTaskName():" + workitemBean.getTaskName());
					if( !workitemBean.getTaskKey().equals("RECEIVE") ) {
						buttonType = "VIEW_APPROVE";
					} else {
						buttonType = "RECEIVE";
					}
				}
			}
		}
	} else if( state.equals("wt.lifecycle.State.APPROVED") || state.equals("wt.lifecycle.State.SENT_ERP") ) { 
		buttonType = "ONLY_VIEW";
		//모든 사용자 ERP재전송
		buttonType = "RE_SUBMIT_ERP";
		if( from.equals("worklist") ) {
			workitem = WorkflowHelper2.getTargetWorkItem(user, change);
			System.out.println("ECO=====> workitem:" + workitem);
			if( workitem != null ) {
				workitemBean = new WorkItemVO(workitem);
				workitemOid = workitemBean.getOid();
				if( workitemBean.getTaskKey().equals("RECEIVE")  ) {
					WorkflowHelper2.approveWorkItem(workitemOid,"");
				} else if( workitemBean.getTaskKey().equals("ERPREWORK") ||isAdmin ) {
					buttonType = "RE_SUBMIT_ERP";
				} 
			}
		} else {
			WTPrincipal creatorPrincipal = (WTPrincipal)change.getCreator().getObject();
			System.out.println("ECO=====> No worklist creator:" + ((WTUser)creatorPrincipal).getFullName());
			workitem = WorkflowHelper2.getTargetWorkItem(creatorPrincipal, change);
			System.out.println("ECO=====> No worklist workitem:" + workitem);
			if( workitem != null ) {
				workitemBean = new WorkItemVO(workitem);
				workitemOid = workitemBean.getOid();
				WTPrincipal sessionUser = SessionHelper.manager.getPrincipal();
				WTGroup adminGroup = OrganizationServicesHelper.manager.getGroup("Administrators");
				System.out.println("ECO=====> No worklist key:" + workitemBean.getTaskKey());
				System.out.println("ECO=====> No worklist is admin:" + OrganizationServicesHelper.manager.isMember(adminGroup, sessionUser));
				if( workitemBean.getTaskKey().equals("ERPREWORK") && OrganizationServicesHelper.manager.isMember(adminGroup, sessionUser)  ) {
					buttonType = "RE_SUBMIT_ERP";
				}
			}
		}
	} else {
		
	}
} else {
	// FOR TESTING - NEED TO DELETE THIS BLOCK ERIC
	if( state.equals("wt.lifecycle.State.INWORK") || state.equals("wt.lifecycle.State.RETURN")) {
		if( creator.equals(user.getAuthenticationName()) ) {
			buttonType = "EDIT_SUBMIT";
		} else {
			buttonType = "ONLY_VIEW";
		}
	} else if( state.equals("wt.lifecycle.State.REVIEWED") || state.equals("wt.lifecycle.State.ACCEPTED") ) {
		if( creator.equals(user.getAuthenticationName()) ) {
			// 만약 본인이 결재자면 결재 화면과 버튼이 보여야 함.
			// 결재완료된 것은 보이지 않아야 함.
			buttonType = "CANCEL";
			if( from.equals("worklist") ) {
				//buttonType = "CANCEL_APPROVE"
				workitem = WorkflowHelper2.getTargetWorkItem(user, change);
				if( workitem != null ) {
					workitemBean = new WorkItemVO(workitem);
					workitemOid = workitemBean.getOid();
					if( !workitemBean.getTaskName().equals("RECEIVE") ) { 
						buttonType = "CANCEL_APPROVE";
					} else {
						buttonType = "RECEIVE";
					}
				}
			}
		} else {
			buttonType = "ONLY_VIEW";
			//buttonType = "VIEW_APPROVE"
			if( from.equals("worklist") ) {
				workitem = WorkflowHelper2.getTargetWorkItem(user, change);
				if( workitem != null ) {
					workitemBean = new WorkItemVO(workitem);
					workitemOid = workitemBean.getOid();
					if( !workitemBean.getTaskName().equals("RECEIVE") ) {
						buttonType = "VIEW_APPROVE";
					} else {
						WorkflowHelper2.approveWorkItem(workitemOid,"");
					}
				}
			}
		}
	} else if ( state.equals("wt.lifecycle.State.RECEIVED") ) {
		buttonType = "RECEIVED";
	}
}

if( buttonType.equals("RECEIVE") ) {
	// Auto Accept
	WorkflowHelper2.approveWorkItemNoEvent(workitemOid,"");
}

// Get IBA
List<String> ibaKeys = new ArrayList<String>();
ibaKeys.add("EC_Reason");
ibaKeys.add("description2");
ibaKeys.add("Project");
HashMap<String,Object> iba = AttributeHelper.service.getValue(change,ibaKeys);
%>
<script>
document.title = "<%=WTMessage.getLocalizedMessage(RESOURCE , "CHANGE_MNG_DETAIL_ECO_TITLE", new Object[]{}, locale)%>: <%=change.getNumber()%>";
</script>

<table border="0" cellpadding="0" cellspacing="10" width="100%">
<tbody><tr height="5"><td>

<table id="creatingDrawing" style="width: 800px;">
	<tr>
		<td class="attributePanel-asterisk" align=left>
			<H2><%=WTMessage.getLocalizedMessage(RESOURCE , "CHANGE_MNG_DETAIL_ECO", new Object[]{}, locale)%></H2>
		</td>
	</tr>
</table>

<table border="0" cellpadding="10" cellspacing="3" width="100%">
<tbody>
<tr align="center">
	<td style="padding:0px 0px 0px 0px" valign="top">
		<table align="center" bgcolor="#9CAEC8" border="0" cellpadding="1" cellspacing="1" width="100%">
		<tbody>
		<tr>
			<td height="1" width="100%">
			</td>
		</tr>
		</tbody>
		</table>
		<table align="center" border="0" cellpadding="0" cellspacing="0" width="100%">
		<colgroup>
			<col width="15%"><col width="35%"><col width="15%"><col width="35%">
		</colgroup>
		<tbody>
		<tr height="35" bgcolor="ffffff">
			<td class="tdblueM"><%=WTMessage.getLocalizedMessage(RESOURCE , "ECO_NUMBER", new Object[]{}, locale)%></td>
			<td class="tdwhiteL"  colspan="3"><%=change.getNumber()%></td>
		</tr>
				
		<tr height="35" bgcolor="ffffff">
			<td class="tdblueM"><%=WTMessage.getLocalizedMessage(RESOURCE , "ECO_NAME", new Object[]{}, locale)%></td>
			<td class="tdwhiteL" colspan="3">
				<%=change.getName()%>
			</td>
		</tr>
		
		<tr height="35" bgcolor="ffffff">
			<td class="tdblueM"><%=WTMessage.getLocalizedMessage(RESOURCE , "APPROVAL_STATE", new Object[]{}, locale)%> <span class="style1"></td>
			<td class="tdwhiteL" colspan="3"><font color=red><%=change.getState().getState().getDisplay(locale)%></font></td>
		</tr>
		
		<tr height="35" bgcolor="ffffff">
			<td class="tdblueM"><%=WTMessage.getLocalizedMessage(RESOURCE , "CREATOR", new Object[]{}, locale)%> <span class="style1"></td>
			<td class="tdwhiteL" colspan="3"><%=change.getCreatorFullName()%></td>
		</tr>
				
		<tr height="35" bgcolor="ffffff">
			<td class="tdblueM"><%=WTMessage.getLocalizedMessage(RESOURCE , "UPDATE_DATE", new Object[]{}, locale)%></td>
			<%
				java.text.SimpleDateFormat formatter =
	            new java.text.SimpleDateFormat ("yyyy/MM/dd (E)a hh:mm:ss", Locale.KOREAN);
				TimeZone tz = TimeZone.getTimeZone("GMT+09:00");
				formatter.setTimeZone(tz);
				String dateString = formatter.format(new java.util.Date(change.getModifyTimestamp().getTime()));
			%>
			<td class="tdwhiteL" colspan="3"><%=dateString%></td>
		</tr>
		
		<tr height="35" bgcolor="ffffff">
			<td class="tdblueM"><%=WTMessage.getLocalizedMessage(RESOURCE , "ECO_ECR", new Object[]{}, locale)%> </td>
			<td class="tdwhiteL" colspan="3">
				<jsp:include page="/netmarkets/jsp/narae/change/select_ecr_include.jsp" flush="true">
                	<jsp:param name="formName" value="mainform"/>
                	<jsp:param name="viewType" value="view"/>
                	<jsp:param name="oid" value="<%=oid %>"/>
                </jsp:include>
                
			</td>
		</tr>
		<!--
		<tr height="35" bgcolor="ffffff">
        	<td class="tdblueM"><%=WTMessage.getLocalizedMessage(RESOURCE , "ECO_TYPE", new Object[]{}, locale)%> <span class="style1">*</span></td>
            <td class="tdwhiteL" colspan="3">
				<input name="ecotype" value="A" checked="" onclick="eoType(this.value)" type="radio"><%=WTMessage.getLocalizedMessage(ECOTYPERB , "A", new Object[]{}, locale)%>
				<input name="ecotype" value="B" onclick="eoType(this.value)" type="radio"><%=WTMessage.getLocalizedMessage(ECOTYPERB , "B", new Object[]{}, locale)%>
				<input name="ecotype" value="C" onclick="eoType(this.value)" type="radio"><%=WTMessage.getLocalizedMessage(ECOTYPERB , "C", new Object[]{}, locale)%>
			</td>
		</tr>
		-->
		<tr height="35" bgcolor="ffffff">
			<td class="tdblueM"><%=WTMessage.getLocalizedMessage(RESOURCE , "ECO_PURPOSE", new Object[]{}, locale)%></td>
			<td class="tdwhiteL" colspan="3">
				<%
						RequestTypeRB rType = new RequestTypeRB();
						Set<String> rKeys = rType.keySet();
						String purpose = (String)iba.get("EC_Reason");
						int index = 0 ;
						for( String oneKey : rKeys) {
							if( purpose != null || purpose.length() > 0 ) {
								if( purpose.contains(oneKey) ) {
									if (index == 0)
										out.println(WTMessage.getLocalizedMessage(TYPERB , oneKey, new Object[]{}, locale));
									else
										out.println(", " + WTMessage.getLocalizedMessage(TYPERB , oneKey, new Object[]{}, locale));
									
									index++;
								}
							}
						}
				%>
			</td>
		</tr>
		<tr height="35" bgcolor="ffffff">
			<td class="tdblueM"><%=WTMessage.getLocalizedMessage(RESOURCE , "ECR_PROJECT", new Object[]{}, locale)%> <span class="style1">*</span></td>
			<td class="tdwhiteL" colspan="3">
				<%=(String)iba.get("Project")%>
			</td>
		</tr>
		<tr height="35" bgcolor="ffffff">
			<td class="tdblueM"><%=WTMessage.getLocalizedMessage(RESOURCE , "ECO_TOP", new Object[]{}, locale)%></td>
			<td class="tdwhiteL" colspan="3">
				<jsp:include page="/netmarkets/jsp/narae/part/select_Top_include.jsp" flush="true">
                            <jsp:param name="formName" value="mainform"/>
                            <jsp:param name="mode" value="single"/>
                            <jsp:param name="viewType" value="view"/>
                            <jsp:param name="oid" value="<%=oid %>"/>
                            <jsp:param name="moudleType" value="eco"/>
                </jsp:include>    
    		</td>
		</tr>
     	<tr height="35" bgcolor="ffffff">
			<td class="tdblueM"><%=WTMessage.getLocalizedMessage(RESOURCE , "ECO_CHANGE_PART", new Object[]{}, locale)%></td>
			<td class="tdwhiteL" colspan="3">
				<jsp:include page="/netmarkets/jsp/narae/part/select_include.jsp" flush="true">
                     <jsp:param name="formName" value="mainform"/>
                     <jsp:param name="mode" value="multi"/>
                     <jsp:param name="viewType" value="view"/>
                     <jsp:param name="oid" value="<%=oid %>"/>
                     <jsp:param name="moudleType" value="eco"/>
                </jsp:include>
            </td>
		</tr>
				<!--
				<tr height="35" bgcolor="ffffff">
					<td class="tdblueM"><%=WTMessage.getLocalizedMessage(RESOURCE , "ECO_STOCK_MNG", new Object[]{}, locale)%></td>
					<td class="tdwhiteL" colspan="3">
						<input name="stockpart" value="A" type="checkbox"><%=WTMessage.getLocalizedMessage(STOCKRB , "A", new Object[]{}, locale)%>
	                	<input name="stockpart" value="B" type="checkbox"><%=WTMessage.getLocalizedMessage(STOCKRB , "B", new Object[]{}, locale)%>
                		<input name="stockpart" value="C" type="checkbox"><%=WTMessage.getLocalizedMessage(STOCKRB , "C", new Object[]{}, locale)%>
                		<input name="stockpart" value="D" type="checkbox"><%=WTMessage.getLocalizedMessage(STOCKRB , "D", new Object[]{}, locale)%>
                	</td>
				</tr>
				-->
				<!-- 적용요구시점 
				<tr height="35" bgcolor="ffffff">
                    <td class="tdblueM"><%=WTMessage.getLocalizedMessage(RESOURCE , "ECO_USE_TIME", new Object[]{}, locale)%> <span class="style1">*</span></td>
                    <td class="tdwhiteL">
                    	<input name="applayDate" maxlength="15" readonly="" onclick="javascript:openCal('applayDate');" value="2013-11-24">
                    	<a href="JavaScript:openCal('applayDate')"><img src="/Windchill/netmarkets/jsp/narae/portal/images/calendar_icon.gif" border="0"></a>
                    	<a href="JavaScript:clearText('applayDate')"><img src="/Windchill/netmarkets/jsp/narae/portal/images/x.gif" border="0"></a>
                    </td>
                    <td class="tdblueM"><%=WTMessage.getLocalizedMessage(RESOURCE , "ECO_CUSTOMER_ECO", new Object[]{}, locale)%></td>
                    <td class="tdwhiteL">
                        <input name="customerEoNo" type="text">
                    </td>
                </tr>
                -->
				
				<tr height="35" bgcolor="ffffff">
					<td class="tdblueM"><%=WTMessage.getLocalizedMessage(RESOURCE , "ECO_REASON", new Object[]{}, locale)%></td>
					<td class="tdwhiteL" colspan="3">
						<%=change.getDescription().replace("$$$None$$$", "").replace("\t","<BR>").replace("\n","<BR>")%>						
					</td>
				</tr>
				<tr height="35" bgcolor="ffffff">
					<td class="tdblueM"><%=WTMessage.getLocalizedMessage(RESOURCE , "ECO_DESIGN_PLAN", new Object[]{}, locale)%></td>
					<td class="tdwhiteL" colspan="3">
						<%=(((String)iba.get("description2")) != null?((String)iba.get("description2")).replace("\t","<BR>").replace("\n","<BR>"):"")%>								
					</td>
				</tr>
				<tr height="35" bgcolor="ffffff">
					<td class="tdblueM"><%=WTMessage.getLocalizedMessage(RESOURCE , "ECO_ATTACHMENT", new Object[]{}, locale)%></td>
					<td class="tdwhiteL" colspan="3">
						<%
						QueryResult result = ContentHelper.service.getContentsByRole (change ,ContentRoleType.SECONDARY );
						if( result.size() > 0 ) {
							out.println("<table border=0 cellpadding=\"0\" cellspacing=2 align=\"left\">");
							while( result.hasMoreElements() ) {
								ApplicationData data = (ApplicationData)result.nextElement();
								out.print("<tr>");
								out.print("<td>");
								out.print("<a target=\"ContentFormatIconPopup\" href=\"/Windchill/servlet/AttachmentsDownloadDirectionServlet?oid=");
								out.print(oid);
								out.print("&cioids=");
								out.print(data.getPersistInfo().getObjectIdentifier().getStringValue());
								out.print("&role=SECONDARY\">");
								out.println(data.getFileName() + "</a></td>");
								out.println("</tr>");
							}
							out.println("</table>");
						}
						%>
					</td>
				</tr>
			
			
				<tr height="35" bgcolor="ffffff">
					<td class="tdblueM"><%=WTMessage.getLocalizedMessage(RESOURCE , "ECO_APPROVAL", new Object[]{}, locale)%></td>
					<td class="tdwhiteL" colspan="3">
						<%if( !ServerConfigHelper.getServerHostName().equals(TEST_SERVER) ) { %>
						<% if( state.equals("wt.lifecycle.State.CHECKWAIT") || state.equals("wt.lifecycle.State.APPROVEWAIT") ) { %>
						<jsp:include page="/netmarkets/jsp/narae/workspace/approval/approver_include.jsp" flush="true">
							<jsp:param name="form" value="mainform"/>
							<jsp:param name="command" value="view"/>
							<jsp:param name="viewType" value="approval"/>
							<jsp:param name="oid" value="<%=oid%>"/>
						</jsp:include>
						<%} else { %>
						<jsp:include page="/netmarkets/jsp/narae/workspace/approval/approver_include.jsp" flush="true">
							<jsp:param name="form" value="mainform"/>
							<jsp:param name="command" value="view"/>
							<jsp:param name="oid" value="<%=oid%>"/>
						</jsp:include>
						<%} %>
						<%} %>
					</td>
				</tr>
				
				<% if( state.equals("wt.lifecycle.State.CHECKWAIT") || state.equals("wt.lifecycle.State.APPROVEWAIT") ) { %>
				<tr id="approvalBlock" height="35" bgcolor="ffffff">
					<td class="tdblueM"><%=WTMessage.getLocalizedMessage(RESOURCE , "APPROVAL_COMMENT", new Object[]{}, locale)%></td>
					<td class="tdwhiteL" colspan="3">
						<textarea name="approvalDesc" cols="" rows="5" class="fm_area" style="width: 98%;" id="approvalDesc"></textarea>
					</td>
				</tr>
				<%} %>
				</tbody>
				</table>
			</td>
		</tr>
		<tr>
			<td colspan="2" align="center">
				<table align="center" border="0" cellpadding="0" cellspacing="4">
				<tbody>
				<tr>
				<%
					if(CommonUtil.isAdmin()) {
				%>
				<td id="submitBtn">
								<a onclick="submitECO()" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="80"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center">결재제출</div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
								<%
					}
								%>
					<%if( !ServerConfigHelper.getServerHostName().equals(TEST_SERVER) ) { %>
                         <%if( buttonType.equals("EDIT_SUBMIT") ) { %>
							<td id="startEditBtn"> 
								<a onclick="gotoEdit()" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="80"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center">수정</div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
							<td id="submitBtn">
								<a onclick="submitECO()" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="80"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center">결재제출</div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
                       	<%} else if( buttonType.equals("CANCEL_APPROVE") ) { %>
							<td id="withdrawBtn"> 
								<a onclick="withdrawECO()" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="80"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center">결재취소</div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
							<td id="approveBtn"> 
								<a onclick="approveECO()" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="50"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center">승인</div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
							<td id="rejectBtn"> 
								<a onclick="rejectECO()" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="50"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center">반려</div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
							<script>
							var approvalBlock = document.getElementById("approvalBlock");
							approvalBlock.style.display = ""
							</script>
						<%} else if( buttonType.equals("VIEW_APPROVE") ) { %>
							<td id="approveBtn"> 
								<a onclick="approveECO()" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="50"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center">승인</div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
							<td id="rejectBtn"> 
								<a onclick="rejectECO()" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="50"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center">반려</div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
							<script>
								var approvalBlock = document.getElementById("approvalBlock");
								approvalBlock.style.display = ""
							</script>
						<%} else if( buttonType.equals("RE_SUBMIT_ERP") ) { %>
							<td id="withdrawBtn"> 
								<a href="/Windchill/netmarkets/jsp/narae/reSendERP.jsp?oid=<%=oid %>" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="80"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center">ERP재전송</div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
						<%} else if( buttonType.equals("CANCEL") ) { %>
							<td id="withdrawBtn"> 
								<a onclick="withdrawECO()" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="80"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center">결재취소</div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
						<%} %>
                   	<% } else { %>
                   		<%out.println(ServerConfigHelper.getServerHostName() + ":"); %>
                   		<%out.println("buttonType=" + buttonType); %>
                   			<td id="registBtn"> 
								<a onclick="gotoEdit()" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="80"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center">수정</div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
							<td id="registBtn">
								<a onclick="submitECO()" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="80"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center">결재제출</div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
                       		<td id="registBtn"> 
								<a onclick="withdrawECO()" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="80"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center">결재취소</div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
							<td id="approveBtn"> 
								<a onclick="approveECO()" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="50"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center">승인</div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
							<td id="rejectBtn"> 
								<a onclick="rejectECO()" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="50"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center">반려</div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
							<script>
							var aBlock = document.getElementById("approvalBlock");
							aBlock.style.display = "";
							</script>
                  	<%} %>
                  	<%
                  	if(isAdmin){
                  		%>
<!--                   		<td id="withdrawBtn">  -->
<%-- 								<a href="/Windchill/netmarkets/jsp/narae/reSendERP.jsp?oid=<%=oid %>" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="80"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center">ERP재전송</div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td> --%>
                  		<%
                  	}
                  	%>
				</tr>
				</tbody>
				</table>
			</td>
		</tr>
		</tbody>
		</table>
	</td>
</tr>
</tbody>
</table>
<input name="ecrListOid" id="ecrListOid"" type="hidden">
<input name="partTopListOid" id="partTopListOid"" type="hidden">
<input name="partListOid" id="partListOid"" type="hidden">
<input name="agree" id="agree" type="hidden">
<input name="approve" id="approve" type="hidden">
<input name="receive" id="receive" type="hidden">
<iframe id="frmIEAction" name="frmIEAction" style="width:0px;height:0px;visibility:hidden"></iframe>

<script>
function gotoEdit() {
	window.location = "/Windchill/app/#ptc1/narae/change/updateECO?oid=<%=oid%>";
}

function submitECO() {
	<%if( !ServerConfigHelper.getServerHostName().equals(TEST_SERVER) ) {%>
	if(!checkRequired()) {
		alert("<%=WTMessage.getLocalizedMessage(RESOURCE , "APPROVAL_LINE_PROBLEM", new Object[]{}, locale)%>!");
		return false;
	}
	<%}%>
	
	if (!confirm("<%=WTMessage.getLocalizedMessage(RESOURCE , "ASK_SUBMIT", new Object[]{}, locale)%>")) return;
	
	xmlHttp1 = null;
	try{
		xmlHttp1 = new XMLHttpRequest();				
	}catch(e){
		try{
			xmlHttp1 = new ActiveXObject("Msxml2.XMLHTTP");
		}catch(e){
			try{
				xmlHttp1 = new ActiveXObject("Microsoft.XMLHTTP");
			}catch(e){
				alert("Your browser does not support Ajax!");
				return false;
			}
		}
	}

	xmlHttp1.onreadystatechange=function(){
		if(xmlHttp1.readyState==4){
			var reValue = xmlHttp1.responseText;
			if( reValue.trim().length > 0 ) {
				alert("<%=WTMessage.getLocalizedMessage(RESOURCE , "SUBMIT_FAILED", new Object[]{}, locale)%>: " + reValue);
			} else {
				alert("<%=WTMessage.getLocalizedMessage(RESOURCE , "SUCCESS_SUBMIT", new Object[]{}, locale)%>!");
				window.location = "/Windchill/app/#ptc1/narae/change/searchECO";
			}
		}
	}
	
	xmlHttp1.open("GET","/Windchill/netmarkets/jsp/narae/change/action/submitChangeAction.jsp?oid=<%=oid%>",true);

	xmlHttp1.setRequestHeader("If-Modified-Since","0");
	xmlHttp1.send(null);
}

function resubmitECR(oid) {
	
	if (!confirm("<%=WTMessage.getLocalizedMessage(RESOURCE , "ASK_RESUBMIT", new Object[]{}, locale)%>")) return;
	
	alert("start");
	xmlHttp1 = null;
	try{
		xmlHttp1 = new XMLHttpRequest();				
	}catch(e){
		try{
			xmlHttp1 = new ActiveXObject("Msxml2.XMLHTTP");
		}catch(e){
			try{
				xmlHttp1 = new ActiveXObject("Microsoft.XMLHTTP");
			}catch(e){
				alert("Your browser does not support Ajax!");
				return false;
			}
		}
	}

	xmlHttp1.onreadystatechange=function(){
		if(xmlHttp1.readyState==4){
			var reValue = xmlHttp1.responseText;
			if( reValue.trim().length > 0 ) {
				alert("<%=WTMessage.getLocalizedMessage(RESOURCE , "FAIL_APPROVAL", new Object[]{}, locale)%>: " + reValue);
			} else {
				alert("<%=WTMessage.getLocalizedMessage(RESOURCE , "SUCCESS_APPROVAL", new Object[]{}, locale)%>!");
				window.location = "/Windchill/app/#ptc1/narae/approval/approvalList";
			}
		}
	}
	
	alert("send");
	xmlHttp1.open("GET","/Windchill/netmarkets/jsp/narae/change/action/approveResubmitAction.jsp?oid=<%=workitemOid%>" ,true);

	xmlHttp1.setRequestHeader("If-Modified-Since","0");
	xmlHttp1.send(null);
}

function withdrawECO() {
	if (!confirm("<%=WTMessage.getLocalizedMessage(RESOURCE , "ASK_WITHDRAW", new Object[]{}, locale)%>")) return;
	
	xmlHttp1 = null;
	try{
		xmlHttp1 = new XMLHttpRequest();				
	}catch(e){
		try{
			xmlHttp1 = new ActiveXObject("Msxml2.XMLHTTP");
		}catch(e){
			try{
				xmlHttp1 = new ActiveXObject("Microsoft.XMLHTTP");
			}catch(e){
				alert("Your browser does not support Ajax!");
				return false;
			}
		}
	}

	xmlHttp1.onreadystatechange=function(){
		if(xmlHttp1.readyState==4){
			var reValue = xmlHttp1.responseText;
			if( reValue.trim().length > 0 ) {
				alert("<%=WTMessage.getLocalizedMessage(RESOURCE , "WITHDRAW_FAILED", new Object[]{}, locale)%>: " + reValue);
			} else {
				alert("<%=WTMessage.getLocalizedMessage(RESOURCE , "SUCCESS_WITHDRAW", new Object[]{}, locale)%>!");
				window.location = "/Windchill/app/#ptc1/narae/approval/mywork";
			}
		}
	}
	
	xmlHttp1.open("GET","/Windchill/netmarkets/jsp/narae/change/action/withdrawChangeAction.jsp?oid=<%=oid%>",true);

	xmlHttp1.setRequestHeader("If-Modified-Since","0");
	xmlHttp1.send(null);
}

function approveECO() {
	if (!confirm("<%=WTMessage.getLocalizedMessage(RESOURCE , "ASK_APPROVE", new Object[]{}, locale)%>")) return;
	xmlHttp1 = null;
	try{
		xmlHttp1 = new XMLHttpRequest();				
	}catch(e){
		try{
			xmlHttp1 = new ActiveXObject("Msxml2.XMLHTTP");
		}catch(e){
			try{
				xmlHttp1 = new ActiveXObject("Microsoft.XMLHTTP");
			}catch(e){
				alert("Your browser does not support Ajax!");
				return false;
			}
		}
	}

	xmlHttp1.onreadystatechange=function(){
		if(xmlHttp1.readyState==4){
			var reValue = xmlHttp1.responseText;
			if( reValue.trim().length > 0 ) {
				alert("<%=WTMessage.getLocalizedMessage(RESOURCE , "FAIL_APPROVAL", new Object[]{}, locale)%>: " + reValue);
			} else {
				alert("<%=WTMessage.getLocalizedMessage(RESOURCE , "SUCCESS_APPROVAL", new Object[]{}, locale)%>!");
				window.location = "/Windchill/app/#ptc1/narae/approval/approvalList";
			}
		}
	}
	
	var approvalDesc = encodeURIComponent(document.getElementById("approvalDesc").value);
	alert(approvalDesc);
	xmlHttp1.open("GET","/Windchill/netmarkets/jsp/narae/change/action/approveChangeAction.jsp?oid=<%=workitemOid%>&approvalDesc=" + approvalDesc ,true);

	xmlHttp1.setRequestHeader("If-Modified-Since","0");
	xmlHttp1.send(document.mainform);
}

function rejectECO() {
	if (!confirm("<%=WTMessage.getLocalizedMessage(RESOURCE , "ASK_REJECT", new Object[]{}, locale)%>")) return;
	
	xmlHttp1 = null;
	try{
		xmlHttp1 = new XMLHttpRequest();				
	}catch(e){
		try{
			xmlHttp1 = new ActiveXObject("Msxml2.XMLHTTP");
		}catch(e){
			try{
				xmlHttp1 = new ActiveXObject("Microsoft.XMLHTTP");
			}catch(e){
				alert("Your browser does not support Ajax!");
				return false;
			}
		}
	}

	xmlHttp1.onreadystatechange=function(){
		if(xmlHttp1.readyState==4){
			var reValue = xmlHttp1.responseText;
			if( reValue.trim().length > 0 ) {
				alert("<%=WTMessage.getLocalizedMessage(RESOURCE , "FAIL_REJECT", new Object[]{}, locale)%>: " + reValue);
			} else {
				alert("<%=WTMessage.getLocalizedMessage(RESOURCE , "SUCCESS_REJECT", new Object[]{}, locale)%>!");
				window.location = "/Windchill/app/#ptc1/narae/approval/approvalList";
			}
		}
	}
	
	xmlHttp1.open("GET","/Windchill/netmarkets/jsp/narae/change/action/rejectChangeAction.jsp?oid=<%=workitemOid%>",true);

	xmlHttp1.setRequestHeader("If-Modified-Since","0");
	xmlHttp1.send(null);
}

</script>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>
