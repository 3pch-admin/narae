<%@page import="ext.narae.service.iba.beans.AttributeHelper"%>
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
<%@ page import="wt.util.*, wt.org.*, wt.session.*, wt.inf.container.*, ext.narae.util.*, ext.narae.ui.*, java.util.*, wt.change2.*, ext.narae.service.*, ext.narae.service.iba.*" %>
<%@ page import="ext.narae.ui.common.resource.*, ext.narae.service.*" %>
<link rel="stylesheet" href="/Windchill/netmarkets/jsp/narae/css/e3ps.css">
<script language="JavaScript" src="/Windchill/netmarkets/jsp/narae/js/common.js"></script>
						
<%
String TEST_SERVER = "wc10.ptc.com";
String RESOURCE = "ext.narae.ui.common.resource.NaraeCommonRB";
String TYPERB = "ext.narae.ui.common.resource.RequestTypeRB";
String STOCKRB = "ext.narae.ui.common.resource.StockControlRB";
String ECOTYPERB = "ext.narae.ui.common.resource.ECOTypeRB";
Locale locale = WTContext.getContext().getLocale();
String M001 = WTMessage.getLocalizedMessage(RESOURCE , "M001", new Object[]{}, WTContext.getContext().getLocale());

WTUser user = (WTUser)SessionHelper.manager.getPrincipal();
String oid = request.getParameter("oid");
WTChangeOrder2 change = (WTChangeOrder2)CommonUtil2.getInstance(oid);
String state = change.getState().getState().getStringValue();
String creator = ((WTUser)change.getCreator().getObject()).getAuthenticationName();
String buttonType = "ONLY_VIEW";

if( !ServerConfigHelper.getServerHostName().equals(TEST_SERVER) ) {
	if( state.equals("wt.lifecycle.State.INWORK") || state.equals("wt.lifecycle.State.RETURN") ) {
		if( creator.equals(user.getAuthenticationName()) ) {
			buttonType = "EDIT_SUBMIT";
		} else {
			buttonType = "ONLY_VIEW";
		}
	} else if( state.equals("wt.lifecycle.State.CHECKWAIT") || state.equals("wt.lifecycle.State.APPROVALWAIT") ) {
		if( creator.equals(user.getAuthenticationName()) ) {
			// 만약 본인이 결재자면 결재 화면과 버튼이 보여야 함.
			buttonType = "CANCEL";
			//buttonType = "CANCEL_APPROVE"
		} else {
			buttonType = "ONLY_VIEW";
			//buttonType = "VIEW_APPROVE"
		}
	}
} else {
	// FOR TESTING
	if( state.equals("wt.lifecycle.State.INWORK") || state.equals("wt.lifecycle.State.RETURN") ) {
		if( creator.equals(user.getAuthenticationName()) ) {
			buttonType = "EDIT_SUBMIT";
		} else {
			buttonType = "ONLY_VIEW";
		}
	} else if( state.equals("wt.lifecycle.State.REVIEWED") || state.equals("wt.lifecycle.State.ACCEPTED") ) {
		if( creator.equals(user.getAuthenticationName()) ) {
			// 만약 본인이 결재자면 결재 화면과 버튼이 보여야 함.
			buttonType = "CANCEL";
			//buttonType = "CANCEL_APPROVE"
		} else {
			buttonType = "ONLY_VIEW";
			//buttonType = "VIEW_APPROVE"
		}
	}
}

// Get IBA
List<String> ibaKeys = new ArrayList<String>();
ibaKeys.add("EC_Reason");
ibaKeys.add("description2");
ibaKeys.add("Project");
HashMap<String,Object> iba = AttributeHelper.service.getValue(change,ibaKeys);
%>

<script>
function projectSearch(){
	var str="/Windchill/netmarkets/jsp/narae/erp/erpProjectList.jsp";
    var opts = "toolbar=0,loca/tion=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=no,";
    var popWidth = 1000;
    var popHeight = 600;
    var leftpos = (screen.width - popWidth)/ 2;
    var toppos = (screen.height - popHeight) / 2 ;
    var rest = "width="+popWidth+",height="+popHeight+",left=" + leftpos + ',top=' + toppos;

    var newwin = window.open( str , "selectEpm", opts+rest);
    newwin.focus();  
}
</script>


<table border="0" cellpadding="0" cellspacing="10" width="100%">
<tbody><tr height="5"><td>

<table id="creatingDrawing" style="width: 800px;">
	<tr>
		<td class="attributePanel-asterisk" align=left>
			<H2><%=WTMessage.getLocalizedMessage(RESOURCE , "CHANGE_MNG_EDIT_ECO", new Object[]{}, locale)%></H2>
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
			<td class="tdblueM"><%=WTMessage.getLocalizedMessage(RESOURCE , "ECO_NAME", new Object[]{}, locale)%> <span class="style1">*</span></td>
			<td class="tdwhiteL" colspan="3">
				<input name="name1" id="name1" class="txt_field" size="85" style="width:80%" value="<%=change.getName()%>">
				<input name="name" id="name" type="hidden">
			</td>
		</tr>
		
		<tr height="35" bgcolor="ffffff">
			<td class="tdblueM"><%=WTMessage.getLocalizedMessage(RESOURCE , "ECO_ECR", new Object[]{}, locale)%> </td>
			<td class="tdwhiteL" colspan="3">
				<jsp:include page="/netmarkets/jsp/narae/change/select_ecr_include.jsp" flush="true">
                	<jsp:param name="formName" value="mainform"/>
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
			<td class="tdblueM"><%=WTMessage.getLocalizedMessage(RESOURCE , "ECO_PURPOSE", new Object[]{}, locale)%> <span class="style1">*</span></td>
			<td class="tdwhiteL" colspan="3">
				<%
						RequestTypeRB rType = new RequestTypeRB();
						Set<String> rKeys = rType.keySet();
						String purpose = (String)iba.get("EC_Reason");
						for( String oneKey : rKeys) {
							if( purpose != null || purpose.length() > 0 ) {
								out.print("<input name='purpose1' id='purpose1' value='" + oneKey + "' type='checkbox' ");
								if( purpose.contains(oneKey) ) {
									out.println("checked>");
								} else {
									out.println(">");
								}
								out.println(WTMessage.getLocalizedMessage(TYPERB , oneKey, new Object[]{}, locale));
							} else {
								out.print("<input name='purpose1' id='purpose1' value='" + oneKey + "' type='checkbox'>");
								out.println(WTMessage.getLocalizedMessage(TYPERB , oneKey, new Object[]{}, locale));
							}
						}
						
				%>
				<input name="purpose" id="purpose" type="hidden">
			</td>
		</tr>
		<tr height="35" bgcolor="ffffff">
			<td class="tdblueM"><%=WTMessage.getLocalizedMessage(RESOURCE , "ECR_PROJECT", new Object[]{}, locale)%> <span class="style1">*</span></td>
			<td class="tdwhiteL" colspan="3">
				<!-- input name="prjName" id="prjName" style="width:300px;" readonly="" type="text" -->
				<%if( !ServerConfigHelper.getServerHostName().equals(TEST_SERVER) ) { %>
				<input name="prjName" id="prjName" style="width:300px;" type="text" value='<%=(String)iba.get("Project")%>' readonly="">  
				<%} else { %>
				<input name="prjName" id="prjName" style="width:300px;" type="text" value='<%=(String)iba.get("Project")%>'>  
				<%} %>
				<a href="#" onclick="projectSearch();">
				<img src="/Windchill/netmarkets/jsp/narae/portal/images/s_search.gif" border="0">
				</a>
                <input name="prjNo" id="prjNo" type="hidden"> 
                <input name="prjSeqNo" id="prjSeqNo" type="hidden">
                <input name="unitCode" id="unitCode" type="hidden">
                <input name="projectName" id="projectName"" type="hidden">
			</td>
		</tr>
		<tr height="35" bgcolor="ffffff">
			<td class="tdblueM"><%=WTMessage.getLocalizedMessage(RESOURCE , "ECO_TOP", new Object[]{}, locale)%></td>
			<td class="tdwhiteL" colspan="3">
				<jsp:include page="/netmarkets/jsp/narae/part/select_Top_include.jsp" flush="true">
                            <jsp:param name="formName" value="mainform"/>
                            <jsp:param name="mode" value="multi"/>
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
						<textarea name="reason1" id="reason1" cols="80" rows="10" class="fm_area" style="width: 98%;"><%=change.getDescription().replace("$$$None$$$", "")%></textarea>	
						<textarea name="reason" id="reason" style="visibility:hidden;height:0px"></textarea>						
					</td>
				</tr>
				<tr height="35" bgcolor="ffffff">
					<td class="tdblueM"><%=WTMessage.getLocalizedMessage(RESOURCE , "ECO_DESIGN_PLAN", new Object[]{}, locale)%></td>
					<td class="tdwhiteL" colspan="3">
						<textarea name="measures1" id="measures1" cols="80" rows="10" class="fm_area" style="width: 98%; " id="i2"><%=(String)iba.get("description2")%></textarea>
						<textarea name="measures" id="measures" style="visibility:hidden;height:0px"></textarea>								
					</td>
				</tr>
				<tr height="35" bgcolor="ffffff">
					<td class="tdblueM"><%=WTMessage.getLocalizedMessage(RESOURCE , "ECO_ATTACHMENT", new Object[]{}, locale)%></td>
					<td class="tdwhiteL" colspan="3">
						<jsp:include page="/netmarkets/jsp/narae/portal/attacheFile_include.jsp" flush="true">
							<jsp:param name="form" value="mainform"/>
							<jsp:param name="command" value="update"/>
							<jsp:param name="type" value="secondary"/>
							<jsp:param name="oid" value="<%=oid%>"/>
						</jsp:include>	
					</td>
				</tr>
			
			
				<tr height="35" bgcolor="ffffff">
					<td class="tdblueM"><%=WTMessage.getLocalizedMessage(RESOURCE , "ECO_APPROVAL", new Object[]{}, locale)%></td>
					<td class="tdwhiteL" colspan="3">
						<jsp:include page="/netmarkets/jsp/narae/workspace/approval/approver_include.jsp" flush="true">
							<jsp:param name="form" value="mainform"/>
							<jsp:param name="command" value="update"/>
							<jsp:param name="oid" value="<%=oid%>"/>
						</jsp:include>
					</td>
				</tr>
				</tbody>
				</table>
			</td>
		</tr>
		<tr>
			<td colspan="2" align="center">
				<table align="center" border="0" cellpadding="0" cellspacing="4">
				<tbody>
				<tr>
					<td id="registBtn"> 
						<a onclick="saveECO('temp')" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="80"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center"><%=WTMessage.getLocalizedMessage(RESOURCE , "TEMP_REGISTER", new Object[]{}, locale)%></div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
					<td id="registBtn">
						<a onclick="saveECO('submit')" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="80"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center"><%=WTMessage.getLocalizedMessage(RESOURCE , "REGISTER", new Object[]{}, locale)%></div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
					<td id="registBtn">
						<a onclick="javascript:history.back(-1);" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="80"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center"><%=WTMessage.getLocalizedMessage(RESOURCE , "CANCEL", new Object[]{}, locale)%></div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
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
<input name="oid" id="oid" value="<%=oid%>" type="hidden">
<input name="ecrListOid" id="ecrListOid"" type="hidden">
<input name="partTopListOid" id="partTopListOid"" type="hidden">
<input name="partListOid" id="partListOid"" type="hidden">
<input name="agree" id="agree" type="hidden">
<input name="approve" id="approve" type="hidden">
<input name="receive" id="receive" type="hidden">
<iframe id="frmIEAction" name="frmIEAction" style="width:0px;height:0px;visibility:hidden"></iframe>

<script>
function cancelECO() {
	
}

function projectSelect(prjName,prjNo,prjSeqNo,unitCode){
	//prjNo prjSeqNo unitCode
	var pForm = document.mainfrom;
	
	document.getElementById("prjName").value = prjSeqNo + "_" + prjName;
	document.getElementById("prjNo").value = prjNo;
	document.getElementById("prjSeqNo").value = prjSeqNo;
	document.getElementById("unitCode").value = unitCode;

}

function saveECO(saveType) {
		
	var name = encodeURIComponent(document.getElementById("name1").value);
	var projectName = encodeURIComponent(document.getElementById("prjName").value);
	var reason = encodeURIComponent(document.getElementById("reason1").value);
	var measures = encodeURIComponent(document.getElementById("measures1").value); 
	
	
	// Related ECR
	var ecrOid = document.getElementsByName("ecrOid");
	var ecrOidSelectedCount = 0;
	var selectedecrOid = "";
	var tempEcrOid = "";
	if( ecrOid != null ) {
		for( var index=0; index < ecrOid.length; index++) {
			tempEcrOid = ecrOid[index].value.replace(/(^\s*)|(\s*$)/gi,"");
				if( ecrOidSelectedCount == 0 ) {
					selectedecrOid = selectedecrOid + tempEcrOid;
					
				} else {
					selectedecrOid = selectedecrOid + "," + tempEcrOid;
				}

				ecrOidSelectedCount = ecrOidSelectedCount + 1;
		}
	}
	
	//Before
	var partTopOid = document.getElementsByName("partTopOid");
	var partTopOidSelectedCount = 0;
	var selectedPartTopOid = "";
	var tempTopOid = "";
	if( partTopOid != null ) {
		for( var index=0; index < partTopOid.length; index++) {
			tempTopOid = partTopOid[index].value.replace(/(^\s*)|(\s*$)/gi,"");
				if( partTopOidSelectedCount == 0 ) {
					selectedPartTopOid = selectedPartTopOid + tempTopOid;
					
				} else {
					selectedPartTopOid = selectedPartTopOid + "," + tempTopOid;
				}

				partTopOidSelectedCount = partTopOidSelectedCount + 1;
		}
	}
	
	//After
	var partOid = document.getElementsByName("partOid");
	var partOidSelectedCount = 0;
	var selectedpartOid = "";
	var tempOid = "";
	if( partOid != null ) {
		for( var index=0; index < partOid.length; index++) {
			tempOid = partOid[index].value.replace(/(^\s*)|(\s*$)/gi,"");
				if( partOidSelectedCount == 0 ) {
					selectedpartOid = selectedpartOid + tempOid;
					
				} else {
					selectedpartOid = selectedpartOid + "," + tempOid;
				}

				partOidSelectedCount = partOidSelectedCount + 1;
		}
	}

	var purpose = document.getElementsByName("purpose1");
	var purposeSelectedCount = 0;
	var selectedPurpose = "";
	for( var index=0; index < purpose.length; index++) {
		if( purpose[index].checked ) {
			if( purposeSelectedCount == 0 ) {
				selectedPurpose = selectedPurpose + purpose[index].value;
			} else {
				selectedPurpose = selectedPurpose + "," + purpose[index].value;
			}

			purposeSelectedCount = purposeSelectedCount + 1;
		}
	}
	
	if( (name == null || name == "") ||
		(projectName == null || projectName == "") ||
		(purpose == null || purpose.length == 0 ) ||
		(purposeSelectedCount == 0) 
		
	) {
		alert("<%=WTMessage.getLocalizedMessage(RESOURCE , "NO_REQUIRED_INPUT", new Object[]{}, locale)%>!");
		return false; 
	}
	
	<%if( !ServerConfigHelper.getServerHostName().equals(TEST_SERVER) ) { %>
	if( saveType == "submit" ) {
		if(!checkRequired()) {
			alert("<%=WTMessage.getLocalizedMessage(RESOURCE , "APPROVAL_LINE_PROBLEM", new Object[]{}, locale)%>!");
			return false;
		}
	}
	<%}%>
	
	if( saveType == 'submit' ) {
		if (!confirm("<%=WTMessage.getLocalizedMessage(RESOURCE , "ASK_SAVE_SUBMIT", new Object[]{}, locale)%>")) return false;
	} else {
		if (!confirm("<%=WTMessage.getLocalizedMessage(RESOURCE , "ASK_SAVE", new Object[]{}, locale)%>")) return false;
	}
	
	// WTUser return
	//협의 approveUser3
	var agreeOid = document.getElementsByName("approveUser3");
	var agreeOidSelectedCount = 0;
	var selectedagreeOid = "";
	var tempagreeOid = "";
	if( agreeOid != null ) {
		for( var index=0; index < agreeOid.length; index++) {
			tempagreeOid = agreeOid[index].value;
				if( agreeOidSelectedCount == 0 ) {
					selectedagreeOid = selectedagreeOid + tempagreeOid;
					
				} else {
					selectedagreeOid = selectedagreeOid + "," + tempagreeOid;
				}

				agreeOidSelectedCount = agreeOidSelectedCount + 1;
		}
	}
	//결재 approveUser4
	var approveOid = document.getElementsByName("approveUser4");
	var approveOidSelectedCount = 0;
	var selectedApproveOid = "";
	var tempApproveOid = "";
	if( approveOid != null ) {
		for( var index=0; index < approveOid.length; index++) {
			tempApproveOid = approveOid[index].value;
				if( approveOidSelectedCount == 0 ) {
					selectedApproveOid = selectedApproveOid + tempApproveOid;
					
				} else {
					selectedApproveOid = selectedApproveOid + "," + tempApproveOid;
				}

				approveOidSelectedCount = approveOidSelectedCount + 1;
		}
	}
	//수신 tempUser
	var receiveOid = document.getElementsByName("tempUser");
	var receiveOidSelectedCount = 0;
	var selectedReceiveOid = "";
	var tempReceiveOid = "";
	if( receiveOid != null ) {
		for( var index=0; index < receiveOid.length; index++) {
			tempReceiveOid = receiveOid[index].value;
				if( receiveOidSelectedCount == 0 ) {
					selectedReceiveOid = selectedReceiveOid + tempReceiveOid;
					
				} else {
					selectedReceiveOid = selectedReceiveOid + "," + tempReceiveOid;
				}

				receiveOidSelectedCount = receiveOidSelectedCount + 1;
		}
	}
	
	document.getElementById("name").value = name;
	document.getElementById("ecrListOid").value = selectedecrOid;
	document.getElementById("purpose").value = selectedPurpose;
	document.getElementById("projectName").value = projectName;
	document.getElementById("partTopListOid").value = selectedPartTopOid;
	document.getElementById("partListOid").value = selectedpartOid;
	document.getElementById("reason").value = reason;
	
	document.getElementById("measures").value = measures;
	if( reason == null || reason.length == 0 ) {
		reason = "$$$None$$$";
		document.getElementById("reason").value = reason;
	}
	
	document.getElementById("agree").value = selectedagreeOid;
	document.getElementById("approve").value = selectedApproveOid;
	document.getElementById("receive").value = selectedReceiveOid;

	var url="/Windchill/netmarkets/jsp/narae/change/action/updateECOAction.jsp";
	if( saveType == "submit" ) {
		url="/Windchill/netmarkets/jsp/narae/change/action/updateSubmitECOAction.jsp";
	}
	
	var frm = document.getElementsByName("mainform")[0];
	frm.encoding = "multipart/form-data";
	frm.method = "POST";
	frm.action = url;
	frm.target = "frmIEAction";
	frm.submit();	
}

function showFinished(saveType) {
	if(saveType == "submit") {
		alert("<%=WTMessage.getLocalizedMessage(RESOURCE , "COMPLETE_SUBMIT", new Object[]{}, locale)%>!");
	} else {
		alert("<%=WTMessage.getLocalizedMessage(RESOURCE , "COMPLETE_SAVE", new Object[]{}, locale)%>!");
	}
	window.location = "/Windchill/app/#ptc1/narae/change/detailECO?oid=<%=oid%>";
}
</script>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>
