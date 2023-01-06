<%@page import="wt.util.WTMessage"%>
<%@page import="ext.narae.service.iba.beans.AttributeHelper"%>
<%@page import="ext.narae.service.workflow.beans.WorkflowHelper2"%>
<%@page import="wt.util.WTContext"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>

<%@ taglib prefix="mvc" uri="http://www.ptc.com/windchill/taglib/jcaMvc"%>
<%@ taglib prefix="wrap" uri="http://www.ptc.com/windchill/taglib/wrappers"%>
<%@ taglib prefix="narae" uri="http://www.ptc.com/windchill/taglib/narae"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="wctags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="attachments" uri="http://www.ptc.com/windchill/taglib/attachments" %>
<link rel="stylesheet" href="/Windchill/netmarkets/jsp/narae/css/e3ps.css">

<%@page import ="wt.fc.*,
				wt.folder.*,
				wt.org.*,
				wt.pdmlink.*,
				wt.query.*,
				wt.change2.*,
				ext.narae.component.*,
				ext.narae.service.*,
				ext.narae.service.iba.*,
				ext.narae.service.workflow.*,
				ext.narae.ui.common.resource.*,
				wt.workflow.work.*,
				java.util.*,
				wt.access.*,
				wt.content.*,
				wt.session.*"%>

<%
String TEST_SERVER = "wc10.ptc.com";
String RESOURCE = "ext.narae.ui.common.resource.NaraeCommonRB";
String TYPERB = "ext.narae.ui.common.resource.RequestTypeRB";
String STOCKRB = "ext.narae.ui.common.resource.StockControlRB";
String ECOTYPERB = "ext.narae.ui.common.resource.ECOTypeRB";
Locale locale = WTContext.getContext().getLocale();

WTUser user = (WTUser)SessionHelper.manager.getPrincipal();
String oid = request.getParameter("oid");
String from = request.getParameter("from");
if( from == null || from.length() == 0 ) {
	from = "normal";
}

System.out.println("from===================" + from);
WTChangeRequest2 change = (WTChangeRequest2)CommonUtil2.getInstance(oid);
String state = change.getState().getState().getStringValue();
String creator = ((WTUser)change.getCreator().getObject()).getAuthenticationName();
WorkItem workitem = null;
String buttonType = "ONLY_VIEW";
WorkItemVO workitemBean = null;
String workitemOid = "";

System.out.println("ECR=====> state:" + state);
if( !ServerConfigHelper.getServerHostName().equals(TEST_SERVER) ) {
	if( state.equals("wt.lifecycle.State.INWORK") || state.equals("wt.lifecycle.State.RETURN")) {
		if( creator.equals(user.getAuthenticationName()) ) {
			buttonType = "EDIT_SUBMIT";
		} else {
			buttonType = "ONLY_VIEW";
		}
	} else if( state.equals("wt.lifecycle.State.CHECKWAIT") || state.equals("wt.lifecycle.State.APPROVEWAIT") ) {
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
	} else if( state.equals("wt.lifecycle.State.APPROVED") || state.equals("wt.lifecycle.State.SENT_ERP") ) { 
		if( from.equals("worklist") ) {
			workitem = WorkflowHelper2.getTargetWorkItem(user, change);
			System.out.println("ECR=====> workitem:" + workitem);
			if( workitem != null ) {
				workitemBean = new WorkItemVO(workitem);
				workitemOid = workitemBean.getOid();
				System.out.println("ECR=====> workitemBean.getTaskName():" + workitemBean.getTaskName());
				if( workitemBean.getTaskKey().equals("RECEIVE") ) {
					WorkflowHelper2.approveWorkItem(workitemOid,"");
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
						buttonType = "RECEIVE";
					}
				}
			}
		}
	}
}

if( buttonType.equals("RECEIVE") ) {
	// Auto Accept
	WorkflowHelper2.approveWorkItem(workitemOid,"");
}

// Get IBA
List<String> ibaKeys = new ArrayList<String>();
ibaKeys.add("EC_Reason");
ibaKeys.add("description2");
ibaKeys.add("supporter");
ibaKeys.add("Project");
HashMap<String,Object> iba = AttributeHelper.service.getValue(change,ibaKeys);

%>

<script>
document.title = "<%=WTMessage.getLocalizedMessage(RESOURCE , "CHANGE_MNG_DETAIL_ECR_TITLE", new Object[]{}, locale)%>: <%=change.getNumber()%>";
</script>
<table border="0" cellpadding="0" cellspacing="10" width="100%">
<tbody><tr height="5"><td>

<table id="creatingDrawing" style="width: 800px;">
	<tr>
		<td class="attributePanel-asterisk" align=left>
			<H2><%=WTMessage.getLocalizedMessage(RESOURCE , "CHANGE_MNG_DETAIL_ECR", new Object[]{}, locale)%></H2>
		</td>
	</tr>
</table>

<table border="0" cellpadding="10" cellspacing="3" width="100%">

	<tbody><tr align="center">
		<td style="padding:0px 0px 0px 0px" valign="top">

			<table align="center" bgcolor="#9CAEC8" border="0" cellpadding="1" cellspacing="1" width="100%">
				<tbody><tr><td height="1" width="100%"></td></tr>
			</tbody></table>
			<table align="center" border="0" cellpadding="0" cellspacing="0" width="100%">
			<colgroup><col width="100"><col><col width="100"><col>

				</colgroup><tbody>
				<tr height="35" bgcolor="ffffff">
					<td class="tdblueM">ECR 번호</td>
					<td class="tdwhiteL"><%=change.getNumber()%></td>
				</tr>
				
				<tr height="35" bgcolor="ffffff">
					<td class="tdblueM">ECR 제목</td>
					<td class="tdwhiteL">
					<%=change.getName()%>
					</td>
				</tr>
				<tr height="35" bgcolor="ffffff">
					<td class="tdblueM">요청자</td>
					<td class="tdwhiteL">
						<%=change.getCreatorFullName()%>
					</td>
				</tr>
				
				<tr height="35" bgcolor="ffffff">
					<td class="tdblueM">결재상태</td>
					<td class="tdwhiteL"><font color=red><%=change.getState().getState().getDisplay(locale)%></font></td>
				</tr>
				
				<tr height="35" bgcolor="ffffff">
					<td class="tdblueM">최종수정일자</td>
					<%
						java.text.SimpleDateFormat formatter =
			            new java.text.SimpleDateFormat ("yyyy/MM/dd (E) a hh:mm:ss", Locale.KOREAN);
						TimeZone tz = TimeZone.getTimeZone("GMT+09:00");
						formatter.setTimeZone(tz);
						String dateString = formatter.format(new java.util.Date(change.getModifyTimestamp().getTime()));
					%>
					<td class="tdwhiteL"><%=dateString%></td>
				</tr>
				
				<tr height="35" bgcolor="ffffff">
					<td class="tdblueM">요청유형</td>
					<td class="tdwhiteL">
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
                    <td class="tdblueM">담당자</td>
                    <td class="tdwhiteL">
                        <%=(String)iba.get("supporter")%>
                    </td>
                </tr>
				<tr height="35" bgcolor="ffffff">
                    <td class="tdblueM">프로젝트</td>
                    <td class="tdwhiteL">
                       <%=(String)iba.get("Project")%>
                    </td>
                </tr>
				
				<tr height="35" bgcolor="ffffff">
					<td class="tdblueM">대상품목</td>
					<td class="tdwhiteL">
						<jsp:include page="/netmarkets/jsp/narae/part/select_include.jsp" flush="true">
							<jsp:param name="formName" value="mainform"/>
							<jsp:param name="viewType" value="view"/>
							<jsp:param name="oid" value="<%=oid%>"/>
						</jsp:include>
					</td>
				</tr>
				
				
				
				<tr height="35" bgcolor="ffffff">
					<td class="tdblueM">문제내용</td>
					<td class="tdwhiteL" height="100">
						<%=change.getDescription().replace("$$$None$$$", "").replace("\t","<BR>").replace("\n","<BR>")%>
					</td>
				</tr>
				<tr height="35" bgcolor="ffffff" height="100">
					<td class="tdblueM">대응방안</td>
					<td class="tdwhiteL" height="100">
						<%=((String)iba.get("description2")).replace("\t","<BR>").replace("\n","<BR>")%>
					</td>
				</tr>
				<tr height="35" bgcolor="ffffff">
					<td class="tdblueM">참조파일</td>
					<td class="tdwhiteL">
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

				<%//if( !ServerConfigHelper.getServerHostName().equals(TEST_SERVER) ) { %>
				<tr height="35" bgcolor="ffffff">
					<td class="tdblueM">결재</td>
					<td class="tdwhiteL">
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

					</td>
				</tr>
				<%//} %>
				
				<% if( state.equals("wt.lifecycle.State.CHECKWAIT") || state.equals("wt.lifecycle.State.APPROVEWAIT") ) { %>
				<tr id="approvalBlock" height="35" bgcolor="ffffff">
					<td class="tdblueM">결재의견</td>
					<td class="tdwhiteL">
						<textarea name="approvalDesc" cols="80" rows="5" class="fm_area" style="width: 98%;" id="approvalDesc"></textarea>
					</td>
				</tr>
				<%} %>
				
				<script>
				var aBlock = document.getElementById("approvalBlock");
				aBlock.style.display = "none";
				</script>
				
			</tbody></table>
		</td>
	</tr>
	<tr>
		<td colspan="2" align="center">
		              <table align="center" border="0" cellpadding="0" cellspacing="4">
                            <tbody><tr>
                                <%if( !ServerConfigHelper.getServerHostName().equals(TEST_SERVER) ) { %>
	                                <%if( buttonType.equals("EDIT_SUBMIT") ) { %>
										<td id="startEditBtn"> 
											<a onclick="gotoEdit()" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="80"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center">수정</div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
										<td id="submitBtn">
											<a onclick="submitECR()" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="80"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center">결재제출</div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
	                            	<%} else if( buttonType.equals("CANCEL_APPROVE") ) { %>
										<td id="withdrawBtn"> 
											<a onclick="withdrawECR()" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="80"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center">결재취소</div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
										<td id="approveBtn"> 
											<a onclick="approveECR()" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="50"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center">승인</div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
										<td id="rejectBtn"> 
											<a onclick="rejectECR()" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="50"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center">반려</div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
										<script>
										var approvalBlock = document.getElementById("approvalBlock");
										approvalBlock.style.display = ""
										</script>
									<%} else if( buttonType.equals("VIEW_APPROVE") ) { %>
										<td id="approveBtn"> 
											<a onclick="approveECR()" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="50"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center">승인</div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
										<td id="rejectBtn"> 
											<a onclick="rejectECR()" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="50"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center">반려</div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
										<script>
											var approvalBlock = document.getElementById("approvalBlock");
											approvalBlock.style.display = ""
										</script>
									<%} else if( buttonType.equals("CANCEL") ) { %>
										<td id="withdrawBtn"> 
											<a onclick="withdrawECR()" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="80"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center">결재취소</div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
									<%} %>
                            	<% } else { %>
                            		<%out.println(ServerConfigHelper.getServerHostName() + ":"); %>
                            		<%out.println("buttonType=" + buttonType); %>
                            			<td id="registBtn"> 
											<a onclick="gotoEdit()" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="80"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center">수정</div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
										<td id="registBtn">
											<a onclick="submitECR()" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="80"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center">결재제출</div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
	                            		<td id="registBtn"> 
											<a onclick="withdrawECR()" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="80"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center">결재취소</div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
										<td id="approveBtn"> 
											<a onclick="approveECR()" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="50"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center">승인</div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
										<td id="rejectBtn"> 
											<a onclick="rejectECR()" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="50"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center">반려</div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
										<script>
										var aBlock = document.getElementById("approvalBlock");
										aBlock.style.display = "";
										</script>
                            	<%} %>
                            </tr>
                        </tbody></table>
		</td>
	</tr>

</tbody></table>
		</td>
	</tr>
</tbody></table>
<input name="oid" id="oid" value="<%=oid%>" type="hidden">
<input name="agree" id="agree" type="hidden">
<input name="approve" id="approve" type="hidden">
<input name="receive" id="receive" type="hidden">
<input name="purpose" id="purpose" type="hidden">
<input name="partListOid" id="partListOid" type="hidden">
<input name="supporter" id="supporter" type="hidden">
<iframe id="frmIEAction" name="frmIEAction" style="width:0px;height:0px;visibility:hidden"></iframe>

<script>
function gotoEdit() {
	window.location = "/Windchill/app/#ptc1/narae/change/updateECR?oid=<%=oid%>";
}

function submitECR() {
	<%if( !ServerConfigHelper.getServerHostName().equals(TEST_SERVER) ) {%>
	if(!checkRequired()) {
		alert("<%=WTMessage.getLocalizedMessage(RESOURCE , "APPROVAL_LINE_PROBLEM", new Object[]{}, locale)%>!");
		return false;
	}
	<%}%>
	
	if (!confirm("<%=WTMessage.getLocalizedMessage(RESOURCE , "ASK_SUBMIT", new Object[]{}, locale)%>")) return false;
	
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
				window.location = "/Windchill/app/#ptc1/narae/change/searchECR";
			}
		}
	}
	
	xmlHttp1.open("GET","/Windchill/netmarkets/jsp/narae/change/action/submitChangeAction.jsp?oid=<%=oid%>",true);

	xmlHttp1.setRequestHeader("If-Modified-Since","0");
	xmlHttp1.send(null);
}

function withdrawECR() {
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


function approveECR() {
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
	xmlHttp1.open("GET","/Windchill/netmarkets/jsp/narae/change/action/approveChangeAction.jsp?oid=<%=workitemOid%>&approvalDesc=" + approvalDesc ,true);

	xmlHttp1.setRequestHeader("If-Modified-Since","0");
	xmlHttp1.send(null);
}

function rejectECR() {
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