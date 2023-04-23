<%@page import="wt.util.WTMessage"%>
<%@page import="ext.narae.service.iba.beans.AttributeHelper"%>
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
				ext.narae.service.org.beans.*,
				ext.narae.service.org.*,
				ext.narae.service.*,
				ext.narae.service.iba.*,
				ext.narae.ui.common.resource.*,
				java.util.*,
				wt.access.*,
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
WTChangeRequest2 change = (WTChangeRequest2)CommonUtil2.getInstance(oid);
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
ibaKeys.add("supporter");
ibaKeys.add("Project");
HashMap<String,Object> iba = AttributeHelper.service.getValue(change,ibaKeys);

%>
<script>

	function callBackSelectedPart(selectedParts) {
		var selectedPartList = '';
		for( var i=0; i < selectedParts.length;i++) {
			if( i == 0 ) selectedPartList = selectedParts[i].value.split('comp$ext$$')[1].split('!*')[0]
			else selectedPartList = selectedPartList + '$$$PTC$$$' + selectedParts[i].value.split('comp$ext$$')[1].split('!*')[0];
		}

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
				var reValueArray = reValue.split("$$$PTC$$$");
				var returnArray = new Array(reValueArray.length);
				for( var index =0 ; index < reValueArray.length; index++) {
					var itemArray = reValueArray[index].split("$$$item$$$");
					returnArray[index] = itemArray;
				}
				addPart(returnArray);
			}
		}
		
		xmlHttp1.open("GET","/Windchill/netmarkets/jsp/narae/part/action/generatePartList.jsp?parentOid=" +selectedPartList,true);

		xmlHttp1.setRequestHeader("If-Modified-Since","0");
		xmlHttp1.send(null);
	}

	function selectUser(inputObjName, inputLabelName) {
		var inputObj = document.getElementById(inputObjName );
		var inputLabel = document.getElementById(inputLabelName );
		var url = "/Windchill/netmarkets/jsp/narae/org/SelectPeopleFrm.jsp?mode=s";//&chief=true";
		
		attache = window.showModalDialog(url,window,"help=no; scroll=no; resizable=yes; dialogWidth=750px; dialogHeight:450px; center:yes");
		if(typeof attache == "undefined" || attache == null) {
			return;
		}

		addList(attache, inputObj, inputLabel);
	}

	function addList(arrObj, inputObj, inputLabel) {
		if(arrObj.length == 0) {
			return;
		}

		var peopleOid;//
		var userName;//

		for(var i = 0; i < arrObj.length; i++) {
			subarr = arrObj[i];
			peopleOid = subarr[1];//
			userName = subarr[5] + " (" + subarr[4] + ")";//

			inputObj.value = peopleOid;
			inputLabel.value = userName;    
		}
	}

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
	
	function projectSelect(prjName,prjNo,prjSeqNo,unitCode){
		//prjNo prjSeqNo unitCode
		var pForm = document.mainfrom;
		
		document.getElementById("prjName").value = prjSeqNo + "_" + prjName;
		document.getElementById("prjNo").value = prjNo;
    	document.getElementById("prjSeqNo").value = prjSeqNo;
    	document.getElementById("unitCode").value = unitCode;
    
    }
	
	function getInfoUserLast2() {
		return document.mainform.infoUserLast2;
	}
</script>
<table border="0" cellpadding="0" cellspacing="10" width="100%">
<tbody><tr height="5"><td>

<table id="creatingDrawing" style="width: 800px;">
	<tr>
		<td class="attributePanel-asterisk" align=left>
			<H2><%=WTMessage.getLocalizedMessage(RESOURCE , "CHANGE_MNG_EDIT_ECR", new Object[]{}, locale)%></H2>
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
					<td class="tdblueM">ECR 제목 <span class="style1">*</span></td>
					<td class="tdwhiteL">
					<input name="name1" id="name1" class="txt_field" size="85" style="width:90%" value="<%=change.getName()%>">
					<input name="name" id="name" type="hidden">
					</td>
				</tr>
				<tr height="35" bgcolor="ffffff">
					<td class="tdblueM">요청자</td>
					<td class="tdwhiteL">
						<%=change.getCreatorFullName()%>
						<input name="requestor1" id="requestor1" value="<%=change.getCreatorFullName()%>" class="txt_field" size="30" style="width:20%" readonly="" type="hidden">
					</td>
				</tr>
				
				</colgroup><tbody><tr height="35" bgcolor="ffffff">
					<td class="tdblueM">결재상태</td>
					<td class="tdwhiteL"><font color=red><%=change.getState().getState().getDisplay(locale)%></font></td>
				</tr>
				
				</colgroup><tbody><tr height="35" bgcolor="ffffff">
					<td class="tdblueM">생성/수정일자</td>
					<td class="tdwhiteL"><%=change.getCreateTimestamp().toLocaleString()%> / <%=change.getModifyTimestamp().toLocaleString()%></td>
				</tr>
				
				<tr height="35" bgcolor="ffffff">
					<td class="tdblueM">요청유형 <span class="style1">*</span></td>
					<td class="tdwhiteL">
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
					</td>
				</tr>
				<tr height="35" bgcolor="ffffff">
                    <td class="tdblueM">담당자 <span class="style1">*</span></td>
                    <td class="tdwhiteL">
                        <input name="worker" id="worker" type="hidden">
                        <%if( !ServerConfigHelper.getServerHostName().equals(TEST_SERVER) ) { %>
                        <input name="tempworker" id="tempworker" type="text" value='<%=(String)iba.get("supporter")%>' readonly="">
						<%} else { %>
						<input name="tempworker" id="tempworker" type="text" value='<%=(String)iba.get("supporter")%>'>
						<%} %>
						<!--input disabled="true" name="tempworker" id="tempworker" onkeyup="javascript:inputAjax();" onfocus="javascript:initAjax(this,document.changeCreateForm.worker,'/Windchill/netmarkets/jsp/narae/org/AjaxSearchUser.jsp',document.changeCreateForm,150,7)" onkeydown="javascript:pressKeyAjax(this)" type="text"-->

                        <a href="JavaScript:selectUser('worker','tempworker')"><img src="/Windchill/netmarkets/jsp/narae/portal/images/s_search.gif" border="0"></a>
                    </td>
                </tr>
				<tr height="35" bgcolor="ffffff">
                    <td class="tdblueM">프로젝트 <span class="style1">*</span></td>
                    <td class="tdwhiteL">
                       <!--input disabled="true" name="prjName" id="prjName" style="width:300px;" readonly="" type="text">  <a href="#" onclick="projectSearch();"-->
					   <%if( !ServerConfigHelper.getServerHostName().equals(TEST_SERVER) ) { %>
					   <input name="prjName" id="prjName" style="width:300px;" type="text" value='<%=(String)iba.get("Project")%>' readonly="">  
					   <%} else { %>
					   <input name="prjName" id="prjName" style="width:300px;" type="text" value='<%=(String)iba.get("Project")%>'>  
					   <%} %>
					   <a href="#" onclick="projectSearch();">
					   <img src="/Windchill/netmarkets/jsp/narae/portal/images/s_search.gif" border="0"></a>
                         <input name="prjNo" id="prjNo" type="hidden"> <input name="prjSeqNo" id="prjSeqNo" type="hidden">
						 <input name="unitCode" id="unitCode" type="hidden">
                       <input name="projectName" id="projectName"" type="hidden">
                    </td>
                </tr>
				
				<tr height="35" bgcolor="ffffff">
					<td class="tdblueM">대상품목 <span class="style1">*</span></td>
					<td class="tdwhiteL">
						<jsp:include page="/netmarkets/jsp/narae/part/select_include.jsp" flush="true">
							<jsp:param name="formName" value="mainform"/>
							<jsp:param name="oid" value="<%=oid%>"/>
						</jsp:include>
					</td>
				</tr>
				
				
				
				<tr height="35" bgcolor="ffffff">
					<td class="tdblueM">문제내용</td>
					<td class="tdwhiteL">
						<textarea name="problem1" cols="80" rows="5" class="fm_area" style="width: 98%;" id="problem1"><%=change.getDescription().replace("$$$None$$$", "")%></textarea>
						<textarea name="problem" id="problem" style="visibility:hidden;height:0px"></textarea>
					</td>
				</tr>
				<tr height="35" bgcolor="ffffff">
					<td class="tdblueM">대응방안</td>
					<td class="tdwhiteL">
						<textarea name="solution1" cols="80" rows="5" class="fm_area" style="width: 98%;" id="solution1"><%=(String)iba.get("description2")%></textarea>
						<textarea name="solution" id="solution" style="visibility:hidden;height:0px"></textarea>					
					</td>
				</tr>
				<tr height="35" bgcolor="ffffff">
					<td class="tdblueM">참조파일</td>
					<td class="tdwhiteL">
						<jsp:include page="/netmarkets/jsp/narae/portal/attacheFile_include.jsp" flush="true">
							<jsp:param name="form" value="mainform"/>
							<jsp:param name="command" value="update"/>
							<jsp:param name="type" value="secondary"/>
							<jsp:param name="oid" value="<%=oid%>"/>
						</jsp:include>
					</td>
				</tr>

				<tr height="35" bgcolor="ffffff">
					<td class="tdblueM">결재</td>
					<td class="tdwhiteL">
						<jsp:include page="/netmarkets/jsp/narae/workspace/approval/approver_include.jsp" flush="true">
							<jsp:param name="form" value="mainform"/>
							<jsp:param name="command" value="update"/>
							<jsp:param name="oid" value="<%=oid%>"/>
						</jsp:include>

					</td>
				</tr>
			</tbody></table>
		</td>
	</tr>
	<tr>
		<td colspan="2" align="center">
		              <table align="center" border="0" cellpadding="0" cellspacing="4">
                            <tbody><tr>
                                <!--td> <script>setButtonTag3D("임시저장","80","saveECR('/Windchill/netmarkets/jsp/narae/change/ActionECR.jsp', true)","");</script></td-->
                                
                                <%if( !ServerConfigHelper.getServerHostName().equals("TEST_SERVER") ) { %>
	                                <%
	                                if( buttonType.equals("EDIT_SUBMIT") ) {
	                                %>
										<td id="registBtn"> 
											<a onclick="saveECR('temp')" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="80"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center">저장</div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
										<td id="registBtn">
											<a onclick="saveECR('submit')" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="80"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center">등록</div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
	                            		<td id="registBtn">
											<a onclick="javascript:history.back(-1);" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="80"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center"><%=WTMessage.getLocalizedMessage(RESOURCE , "CANCEL", new Object[]{}, locale)%></div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
				
	                            	<%
	                                } else if( buttonType.equals("CANCEL") ) {
	                                %>
										<td id="registBtn"> 
											<a onclick="saveECR('temp')" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="80"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center">결재취소</div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
									<%	
	                                }
	                            	%>
	                            <% } else { %>
	                            	<%out.println(ServerConfigHelper.getServerHostName() + ":"); %>
                            		<%out.println("buttonType=" + buttonType); %>
	                            		<td id="registBtn"> 
											<a onclick="saveECR('temp')" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="80"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center">저장</div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
										<td id="registBtn">
											<a onclick="saveECR('submit')" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="80"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center">등록</div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
		                            	<td id="registBtn"> 
											<a onclick="saveECR('temp')" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="80"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center">결재취소</div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
									
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
function saveECR(saveType) {
	
	var name = encodeURIComponent(document.getElementById("name1").value);
	var realCreator = encodeURIComponent(document.getElementById("requestor1").value);
	var worker = encodeURIComponent(document.getElementById("worker").value);
	var tempworker = encodeURIComponent(document.getElementById("tempworker").value);
	var projectName = encodeURIComponent(document.getElementById("prjName").value);
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
		(realCreator == null || realCreator == "") ||
		(tempworker == null || tempworker == "") ||
		(projectName == null || projectName == "") ||
		(partOid == null || partOid.length == 0 ) ||
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
	
	var problem = encodeURIComponent(document.getElementById("problem1").value);
	var solution = encodeURIComponent(document.getElementById("solution1").value);
	
	if( problem == null || problem.length == 0 ) {
		problem = "$$$None$$$";
	}
	
	document.getElementById("purpose").value = selectedPurpose;
	document.getElementById("agree").value = selectedagreeOid;
	document.getElementById("partListOid").value = selectedpartOid;
	document.getElementById("name").value = name;
	document.getElementById("problem").value = problem;
	document.getElementById("supporter").value = tempworker;
	
	document.getElementById("projectName").value = projectName;
	document.getElementById("solution").value = solution;
	document.getElementById("agree").value = selectedagreeOid;
	document.getElementById("approve").value = selectedApproveOid;
	document.getElementById("receive").value = selectedReceiveOid;
	
	var url="/Windchill/netmarkets/jsp/narae/change/action/updateECRAction.jsp";
	if( saveType == "submit" ) {
		url="/Windchill/netmarkets/jsp/narae/change/action/updateSubmitECRAction.jsp";
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
	window.location = "/Windchill/app/#ptc1/narae/change/detailECR?oid=<%=oid%>";
}



</script>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>