<%@page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<%-- <%@ include file="/netmarkets/jsp/util/begin.jspf"%> --%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf" %>
<%@ taglib prefix="mvc" uri="http://www.ptc.com/windchill/taglib/jcaMvc"%>
<%@ taglib prefix="wrap" uri="http://www.ptc.com/windchill/taglib/wrappers"%>
<%@ taglib prefix="narae" uri="http://www.ptc.com/windchill/taglib/narae"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="wctags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="attachments" uri="http://www.ptc.com/windchill/taglib/attachments" %>
<link rel="stylesheet" href="/Windchill/netmarkets/jsp/narae/css/e3ps.css">
<%@ page import="wt.util.*, wt.org.*, wt.session.*, wt.inf.container.*, ext.narae.util.*, ext.narae.ui.*, java.util.*,ext.narae.service.*" %>

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
WTContainerRef containerRef = WCUtil.getWTContainerRefForDrawing();
WTContainerRef partContainerRef = WCUtil.getWTContainerRefForPart();
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

<script>
document.title = "<%=WTMessage.getLocalizedMessage(RESOURCE , "CHANGE_MNG_CREATE_ECO_TITLE", new Object[]{}, locale)%>";
</script>

<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
<form name=mainform id=mainform method=post enctype="multipart/form-data">
<table border="0" cellpadding="0" cellspacing="10" width="100%"> <!--//여백 테이블-->
<tbody><tr height="5"><td>

<table id="creatingDrawing" style="width: 800px;">
	<tr>
		<td class="attributePanel-asterisk" align=left>
			<H2><%=WTMessage.getLocalizedMessage(RESOURCE , "CHANGE_MNG_CREATE_ECO", new Object[]{}, locale)%></H2>
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
			<td class="tdblueM"><%=WTMessage.getLocalizedMessage(RESOURCE , "ECO_NAME", new Object[]{}, locale)%> <span class="style1">*</span></td>
			<td class="tdwhiteL" colspan="3">
				<input name="name1" id="name1" class="txt_field" size="85" style="width:80%">
				<input name="name" id="name" type="hidden">
			</td>
		</tr>
		
		<tr height="35" bgcolor="ffffff">
			<td class="tdblueM"><%=WTMessage.getLocalizedMessage(RESOURCE , "ECO_ECR", new Object[]{}, locale)%> </td>
			<td class="tdwhiteL" colspan="3">
				<link rel="stylesheet" href="/Windchill/netmarkets/jsp/narae/css/e3ps.css">
				<script>
				function ecoSearch(){
					var str="/Windchill/netmarkets/jsp/narae/change/SelectEco.jsp?process=ECR_EXIST";
			        var opts = "toolbar=0,loca/tion=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=0,";
			        var popWidth = 800;
			        var popHeight = 550;
			        var leftpos = (screen.width - popWidth)/ 2;
			        var toppos = (screen.height - popHeight) / 2 ;
			        var rest = "width="+popWidth+",height="+popHeight+",left=" + leftpos + ',top=' + toppos;

			        var newwin = window.open( str , "selectEpm", opts+rest);
			        newwin.focus(); 
				}
				
				//select ecr
				function setEcrHtml( tt, data){
					tt.innerHTML = data;
				}

				function ViewEcrPopup2(oid){
				    var str="/Windchill/netmarkets/jsp/narae/change/EcrInfoView.jsp?oid="+oid;
				    var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=0,resizable=0,";
				    leftpos = (screen.width - 700)/ 2; 
				    toppos = (screen.height - 500) / 2 ; 
				    rest = "width=700,height=500,left=" + leftpos + ',top=' + toppos;
				    
				    var newwin = window.open( str , "ViewEcr", opts+rest);
				    newwin.focus();
				}
	
				function selectEcr() {
					//change url
					var url = "/Windchill/netmarkets/jsp/narae/change/searchECRPicker.jsp";
					var opts = "toolbar=0,loca/tion=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=0,";
				    var popWidth = 800;
				    var popHeight = 800;
				    var leftpos = (screen.width - popWidth)/ 2;
				    var toppos = (screen.height - popHeight) / 2 ;
				    var rest = "width="+popWidth+",height="+popHeight+",left=" + leftpos + ',top=' + toppos; 
				    var newwin = window.open( url , "selectfolder", opts+rest);
// 					attache = window.showModalDialog(url,window,"help=no; scroll=yes; resizable=yes; dialogWidth=800px; dialogHeight:550px; center:yes");
					
// 					if(typeof attache == "undefined" || attache == null) {
// 						return;
// 					}
// 					addEcr(attache);
				}
				
				function addEcr(arrObj) {
					if(arrObj.length == 0) {
						return;
					}
				
					var ecrOid;//
					var ecrNumber;//
					var ecrName;//
					var ecrCreator;//
					var docCheck = true;
					
					for(var i = 0; i < arrObj.length; i++) {
						docCheck = true;
						
						subarr = arrObj[i];
						ecrOid = subarr[0];//
						ecrNumber = subarr[1];//
						ecrName = subarr[2];//
						ecrCreator = subarr[3];//
						prjName	= subarr[4];//
						
				/*		if(document.changeCreateForm.prjNo.value.length>0){
							
							if(document.changeCreateForm.prjNo.value == prjNo) continue;
							
						}
				*/		var ecrOidFrm = document.getElementsByName("prjName");
						if(ecrOidFrm != undefined) {
							if(ecrOidFrm.length) {
								for(var j = 0; j < ecrOidFrm.length; j++) {
									if(ecrOidFrm[j].value == ecrOid) docCheck = false;
								}
							}else {
								if(ecrOidFrm.value == ecrOid) docCheck = false;
							}
						}
				
						if(docCheck) {
							var userRow1 = ecrTable.children[0].appendChild(ecrInnerTempTable.rows[0].cloneNode(true));
							onecell1 = userRow1.childNodes[1];
							setEcrHtml(onecell1, "<input type=\"checkbox\" name=\"ecrDelete\"><input type=hidden name=ecrOid value=\""+ecrOid+"\">");
							onecell2 = userRow1.childNodes[3];
							setEcrHtml(onecell2, ecrNumber);
							onecell3 = userRow1.childNodes[5];
							setEcrHtml(onecell3, "<nobr><a href=\"JavaScript:ViewEcrPopup2('"+ecrOid+"')\">"+ecrName+"</a></nobr>");
							onecell4 = userRow1.childNodes[7];
							setEcrHtml(onecell4, ecrCreator);
						}
						
						projectSelect1(prjName);
					}
				}
				
				function projectSelect1(prjName){
					//prjNo prjSeqNo unitCode
					document.getElementById("prjName").value = prjName;
			    }
				
				function projectSelect(prjName,prjNo,prjSeqNo,unitCode){
					
					//alert("ssss");
					//prjNo prjSeqNo unitCode
					var pForm = document.mainfrom;
					
					document.getElementById("prjName").value = prjNo+ "-"+ prjSeqNo + "_" + prjName;
					document.getElementById("prjNo").value = prjNo;
			    	document.getElementById("prjSeqNo").value = prjSeqNo;
			    	document.getElementById("unitCode").value = unitCode;
			    
			    }

				function deleteEcr() {
					var ecrDelete = document.getElementsByName("ecrDelete");
					if(ecrDelete.length) {
						index = ecrDelete.length-1;
					
						for(i=index; i>=0; i--) {
							if(ecrDelete[i].checked == true) ecrTable.deleteRow(i+1);
						}
					}else {
						if(ecrDelete.checked == true) ecrTable.deleteRow(1);
					}
				}

				</script>

				<table id="ecrInnerTempTable" style="display:none">
				<tbody>
				<tr>
					<td class="tdwhiteM"></td>
					<td class="tdwhiteM"></td>
					<td class="tdwhiteL"></td>
					<td class="tdwhiteM"></td>
				</tr>
				</tbody>
				</table>

				<table cellpadding="0" cellspacing="0" width="100%">
				<tbody>
				<tr>
					<td>
						<table align="left" border="0" cellpadding="0" cellspacing="4">
						<tbody>
						<tr>
   							<td>
   								<a style="FONT-SIZE: 8pt;" onclick="selectEcr()">
   								<table class="" border="0" cellpadding="0" cellspacing="0" width="60">
   								<tbody>
   								<tr>
   									<td width="7"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left.gif" alt="" height="20" width="7"></td>
   									<td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid.gif" valign="middle">
   										<table align="center" border="0" cellpadding="0" cellspacing="0">
   										<tbody>
   										<tr>
   											<td>
   												<div id="_text" align="center"><%=WTMessage.getLocalizedMessage(RESOURCE , "ADD", new Object[]{}, locale)%></div>
   											</td>
   										</tr>
   										</tbody>
   										</table>
   									</td>
   									<td width="12">
   										<img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right.gif" alt="" height="20" width="12">
   									</td>
   								</tr>
   								</tbody>
   								</table>
   								</a>
   							</td>
   							<td>
   								<a style="FONT-SIZE: 8pt;" onclick="deleteEcr()" >
   								<table class="" border="0" cellpadding="0" cellspacing="0" width="60">
   								<tbody>
   								<tr>
   									<td width="7"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left.gif" alt="" height="20" width="7"></td>
   									<td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid.gif" valign="middle">
   										<table align="center" border="0" cellpadding="0" cellspacing="0">
   										<tbody>
   										<tr>
   											<td><div id="_text" align="center"><%=WTMessage.getLocalizedMessage(RESOURCE , "DELETE", new Object[]{}, locale)%></div></td>
   										</tr>
   										</tbody>
   										</table>
   									</td>
   									<td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right.gif" alt="" height="20" width="12"></td>
   								</tr>
   								</tbody>
   								</table>
   								</a>
   							</td>
						</tr>
						</tbody>
						</table>
					</td>
				</tr>
				<tr>
					<td>
						<div style="width:99%;overflow-x:hidden;overflow-y:auto;border:1px;border-style:solid;border-color:#5F9EA0;padding:0px;margin:1px 1px 5px;">
						<table id="ecrTable" align="center" border="0" cellpadding="1" cellspacing="0" width="100%">
						<tbody>
						<tr>
							<td class="tdblueM" colspan="2" height="22" width="40%"><%=WTMessage.getLocalizedMessage(RESOURCE , "NUMBER", new Object[]{}, locale)%></td>
							<td class="tdblueM" height="22" width="50%"><%=WTMessage.getLocalizedMessage(RESOURCE , "NAME", new Object[]{}, locale)%></td>
							<td class="tdblueM" height="22" width="10%"><%=WTMessage.getLocalizedMessage(RESOURCE , "ECO_REGISTER", new Object[]{}, locale)%></td>
						</tr>
						</tbody>
						</table>
						</div>
					</td>
				</tr>
				</tbody>
				</table>
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
				<input name="purpose1" id="purpose1" value="A" type="checkbox"><%=WTMessage.getLocalizedMessage(TYPERB , "A", new Object[]{}, locale)%>
				<input name="purpose1" id="purpose1" value="B" type="checkbox"><%=WTMessage.getLocalizedMessage(TYPERB , "B", new Object[]{}, locale)%>
				<input name="purpose1" id="purpose1" value="D" type="checkbox"><%=WTMessage.getLocalizedMessage(TYPERB , "D", new Object[]{}, locale)%>
				<input name="purpose1" id="purpose1" value="E" type="checkbox"><%=WTMessage.getLocalizedMessage(TYPERB , "E", new Object[]{}, locale)%>
				<input name="purpose1" id="purpose1" value="F" type="checkbox"><%=WTMessage.getLocalizedMessage(TYPERB , "F", new Object[]{}, locale)%>
				<input name="purpose1" id="purpose1" value="H" type="checkbox"><%=WTMessage.getLocalizedMessage(TYPERB , "H", new Object[]{}, locale)%>
				<input name="purpose1" id="purpose1" value="J" type="checkbox"><%=WTMessage.getLocalizedMessage(TYPERB , "J", new Object[]{}, locale)%>
				<input name="purpose1" id="purpose1" value="K" type="checkbox"><%=WTMessage.getLocalizedMessage(TYPERB , "K", new Object[]{}, locale)%>
				<input name="purpose1" id="purpose1" value="L" type="checkbox"><%=WTMessage.getLocalizedMessage(TYPERB , "L", new Object[]{}, locale)%>
				<input name="purpose1" id="purpose1" value="M" type="checkbox"><%=WTMessage.getLocalizedMessage(TYPERB , "M", new Object[]{}, locale)%>
				<input name="purpose1" id="purpose1" value="N" type="checkbox"><%=WTMessage.getLocalizedMessage(TYPERB , "N", new Object[]{}, locale)%>
				<input name="purpose1" id="purpose1" value="O" type="checkbox"><%=WTMessage.getLocalizedMessage(TYPERB , "O", new Object[]{}, locale)%>
				<input name="purpose1" id="purpose1" value="P" type="checkbox"><%=WTMessage.getLocalizedMessage(TYPERB , "P", new Object[]{}, locale)%>
				<input name="purpose1" id="purpose1" value="Q" type="checkbox"><%=WTMessage.getLocalizedMessage(TYPERB , "Q", new Object[]{}, locale)%>
				<input name="purpose1" id="purpose1" value="R" type="checkbox"><%=WTMessage.getLocalizedMessage(TYPERB , "R", new Object[]{}, locale)%>
				<input name="purpose1" id="purpose1" value="S" type="checkbox"><%=WTMessage.getLocalizedMessage(TYPERB , "S", new Object[]{}, locale)%>
				<input name="purpose1" id="purpose1" value="T" type="checkbox"><%=WTMessage.getLocalizedMessage(TYPERB , "T", new Object[]{}, locale)%>
				<input name="purpose1" id="purpose1" value="U" type="checkbox"><%=WTMessage.getLocalizedMessage(TYPERB , "U", new Object[]{}, locale)%>
				<input name="purpose" id="purpose" type="hidden">
			</td>
		</tr>
		<tr height="35" bgcolor="ffffff">
			<td class="tdblueM"><%=WTMessage.getLocalizedMessage(RESOURCE , "ECR_PROJECT", new Object[]{}, locale)%> <span class="style1">*</span></td>
			<td class="tdwhiteL" colspan="3">
				<!-- input name="prjName" id="prjName" style="width:300px;" readonly="" type="text" -->
				<input name="prjName" id="prjName" style="width:300px;" type="text" readonly="">  
				<a href="#" onclick="projectSearch();">
				<img src="/Windchill/netmarkets/jsp/narae/portal/images/s_search.gif" border="0">
				</a>
                <input name="prjNo" id="prjNo" type="hidden"> 
                <input name="prjSeqNo" id="prjSeqNo" type="hidden">
                <input name="unitCode" id="unitCode" type="hidden">
                <input name="projectName" id="projectName" type="hidden">
			</td>
		</tr>
		<tr height="35" bgcolor="ffffff">
			<td class="tdblueM"><%=WTMessage.getLocalizedMessage(RESOURCE , "ECO_TOP", new Object[]{}, locale)%></td>
			<td class="tdwhiteL" colspan="3">
				<jsp:include page="/netmarkets/jsp/narae/part/select_Top_include.jsp" flush="true">
                            <jsp:param name="formName" value="mainform"/>
                            <jsp:param name="mode" value="multi"/>
                            <jsp:param name="viewType" value="eco"/>
                </jsp:include>
    		</td>
			</tr>
		    <tr height="35" bgcolor="ffffff">
					<td class="tdblueM"><%=WTMessage.getLocalizedMessage(RESOURCE , "ECO_CHANGE_PART", new Object[]{}, locale)%></td>
					<td class="tdwhiteL" colspan="3">
						<jsp:include page="/netmarkets/jsp/narae/part/select_include.jsp" flush="true">
		                     <jsp:param name="formName" value="mainform"/>
		                     <jsp:param name="mode" value="multi"/>
		                     <jsp:param name="module" value="eco"/>
		                     <jsp:param name="searchType" value="eco"/>
		                     <jsp:param name="viewType" value="eco"/>
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
						<textarea name="reason1" id="reason1" cols="80" rows="10" class="fm_area" style="width: 98%;"></textarea>	
						<textarea name="reason" id="reason" style="visibility:hidden;height:0px"></textarea>						
					</td>
				</tr>
				<tr height="35" bgcolor="ffffff">
					<td class="tdblueM"><%=WTMessage.getLocalizedMessage(RESOURCE , "ECO_DESIGN_PLAN", new Object[]{}, locale)%></td>
					<td class="tdwhiteL" colspan="3">
						<textarea name="measures1" id="measures1" cols="80" rows="10" class="fm_area" style="width: 98%; " id="i2"></textarea>
						<textarea name="measures" id="measures" style="visibility:hidden;height:0px"></textarea>								
					</td>
				</tr>
				<tr height="35" bgcolor="ffffff">
					<td class="tdblueM"><%=WTMessage.getLocalizedMessage(RESOURCE , "ECO_ATTACHMENT", new Object[]{}, locale)%></td>
					<td class="tdwhiteL" colspan="3">
						

						<!-- // 파일 첨부 시작 //-->
						
						<script language="JavaScript">
						var isCh = navigator.appVersion.indexOf("Chrome") >= 0;
						
						function insertFilesecondary() {
						
							index = fileTablesecondary.rows.length;
							if(index >= 2) {
								if(fileTableRowsecondary.style != null)
									fileTableRowsecondary.style.display = '';
								else
									fileTableRowsecondary[0].style.display = '';
							}
							
							trObj = fileTablesecondary.insertRow(index);
							if(isCh){ // 크롬 일경우
								trObj.replaceWith(fileTablesecondary.rows[1].cloneNode(true));	
							
							}else{ // 크롬 외 브라우저 일 경우
								trObj.replaceNode(fileTablesecondary.rows[1].cloneNode(true));
							}
							
						
							fileTableRowsecondary[0].style.display = 'none';
							
						}
						function deleteFilesecondary() {
							index = document.mainform.fileDeletesecondary.length-1;
							
							for(i=index; i>=1; i--) {
								if(document.mainform.fileDeletesecondary[i].checked == true){ 
									fileTablesecondary.deleteRow(i+1);
								}
							}
						}
						</script>
						<table align="center" width="100%">
						<tbody>
						<tr> 
							<td height="25">
								<table align="left" border="0" cellpadding="0" cellspacing="2">
                            	<tbody>
                            	<tr>
                                	<td>
                                		<a style="FONT-SIZE: 8pt;" onclick="insertFilesecondary()" >
                                		<table class="" border="0" cellpadding="0" cellspacing="0" width="50">
                                		<tbody>
                                		<tr>
                                			<td width="7">
                                				<img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left.gif" alt="" height="20" width="7">
                                			</td>
                                			<td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid.gif" valign="middle">
                                				<table align="center" border="0" cellpadding="0" cellspacing="0">
                                				<tbody>
                                				<tr>
                                					<td>
                                						<div id="_text" align="center"><%=WTMessage.getLocalizedMessage(RESOURCE , "ADD", new Object[]{}, locale)%></div>
                                					</td>
                                				</tr>
                                				</tbody>
                                				</table>
                                			</td>
                                			<td width="12">
                                				<img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right.gif" alt="" height="20" width="12">
                                			</td>
                                		</tr>
                                		</tbody>
                                		</table>
                                		</a>
                                	</td>
                                	<td> 
                                		<a style="FONT-SIZE: 8pt;" onclick="deleteFilesecondary()" >
                                		<table class="" border="0" cellpadding="0" cellspacing="0" width="50">
                                		<tbody>
                                		<tr>
                                			<td width="7">
                                				<img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left.gif" alt="" height="20" width="7">
                                			</td>
	                                		<td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid.gif" valign="middle">
	                                			<table align="center" border="0" cellpadding="0" cellspacing="0">
	                                			<tbody>
	                                			<tr>
	                                				<td>
	                                					<div id="_text" align="center"><%=WTMessage.getLocalizedMessage(RESOURCE , "DELETE", new Object[]{}, locale)%></div>
	                                				</td>
	                                			</tr>
	                                			</tbody>
	                                			</table>
	                                		</td>
	                                		<td width="12">
	                                			<img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right.gif" alt="" height="20" width="12">
	                                		</td>
                                		</tr>
                                		</tbody>
                                		</table>
                                		</a>
                                	</td>
                            	</tr>
                        		</tbody>
                        		</table>		
							</td>											
						</tr>
						</tbody>
						</table>
						
						<table class="tb1" id="fileTablesecondary" align="center" border="0" cellpadding="1" cellspacing="0" width="100%">
						<tbody>
						<tr align="center" bgcolor="#f1f1f1">
							<td colspan="2" id="tb_inner" height="22" width="100%"><%=WTMessage.getLocalizedMessage(RESOURCE , "PATH", new Object[]{}, locale)%></td>
						</tr>
						<tr id="fileTableRowsecondary" style="display:NONE" align="center" bgcolor="#FFFFFF"> 
							<td id="tb_gray" height="22" width="3%">
								<input name="fileDeletesecondary" type="checkbox">
							</td>
							<td id="tb_gray" width="97%">
								<input name="secondary" id="secondary" style="width:99%" type="file">
							</td>
						</tr>
						</tbody>
						</table>
						<!-- // 파일 첨부 끝 //-->	
					</td>
				</tr>
			
			
			<tr height="35" bgcolor="ffffff">
				<td class="tdblueM"><%=WTMessage.getLocalizedMessage(RESOURCE , "ECO_APPROVAL", new Object[]{}, locale)%></td>
				<td class="tdwhiteL" colspan="3">
					<jsp:include page="/netmarkets/jsp/narae/workspace/approval/approver_include.jsp" flush="true">
						<jsp:param name="form" value="mainform"/>
						<jsp:param name="command" value="update"/>
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
</form>
<iframe id="frmIEAction" name="frmIEAction" style="width:0px;height:0px;visibility:hidden"></iframe>

<script>
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
	
// 	var pdfChk = document.getElementsByName("pdfChk");
// 	var pdfChkVal = "";
// 	for( var index=0; index < pdfChk.length; index++) {
// 		pdfChkVal=pdfChk[index].value;
// 		if(pdfChkVal != "false"){
			
// 		}else{
// 			alert("2D not exist !!");
// 			return false; 
// 		}
// 	}
	
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
		if (!confirm("<%=WTMessage.getLocalizedMessage(RESOURCE , "ASK_CREATE_SUBMIT", new Object[]{}, locale)%>")) return false;
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
	
	if( reason == null || reason.length == 0 ) {
		reason = "$$$None$$$";
	}
	
	document.getElementById("name").value = name;
	document.getElementById("ecrListOid").value = selectedecrOid;
	document.getElementById("purpose").value = selectedPurpose;
	document.getElementById("projectName").value = projectName;
	document.getElementById("partTopListOid").value = selectedPartTopOid;
	document.getElementById("partListOid").value = selectedpartOid;
	document.getElementById("reason").value = reason;
	document.getElementById("measures").value = measures;
	
	document.getElementById("agree").value = selectedagreeOid;
	document.getElementById("approve").value = selectedApproveOid;
	document.getElementById("receive").value = selectedReceiveOid;

	var url="/Windchill/netmarkets/jsp/narae/change/action/createECOAction.jsp";
	if( saveType == "submit" ) {
		url="/Windchill/netmarkets/jsp/narae/change/action/createSubmitECOAction.jsp";
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
		window.location = "/Windchill/app/#ptc1/narae/change/searchECO";
	} else {
		alert("<%=WTMessage.getLocalizedMessage(RESOURCE , "COMPLETE_SAVE", new Object[]{}, locale)%>!");
		window.location = "/Windchill/app/#ptc1/narae/approval/mywork";
	}
}
</script>

<%-- <%@ include file="/netmarkets/jsp/util/end.jspf"%> --%>
