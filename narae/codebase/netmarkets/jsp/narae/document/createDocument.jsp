<%@page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf" %>
<%-- <%@ include file="/netmarkets/jsp/components/includeWizBean.jspf" %> --%>
<%@ taglib prefix="mvc" uri="http://www.ptc.com/windchill/taglib/jcaMvc"%>
<%@ taglib prefix="wrap" uri="http://www.ptc.com/windchill/taglib/wrappers"%>
<%@ taglib prefix="narae" uri="http://www.ptc.com/windchill/taglib/narae"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="wctags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="attachments" uri="http://www.ptc.com/windchill/taglib/attachments" %>

<link rel="stylesheet" href="/Windchill/netmarkets/jsp/narae/css/e3ps.css">

<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.Comparator"%>
<%@page import="java.util.Vector"%>

<%@page import="wt.fc.QueryResult"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="wt.folder.Folder"%>
<%@page import="wt.folder.FolderHelper"%>
<%@page import="wt.part.QuantityUnit"%>
<%@page import="wt.session.SessionHelper"%>

<%@ page import="wt.folder.*, ext.narae.service.*, wt.util.*, wt.org.*, wt.session.*, wt.inf.container.*, ext.narae.util.*, ext.narae.ui.*, java.util.*, ext.narae.service.part.*" %>
<%
String TEST_SERVER = "wc10.ptc.com";
String RESOURCE = "ext.narae.ui.common.resource.NaraeCommonRB";
String UNITRB = "ext.narae.ui.common.resource.NareUnitRB";
Locale locale = WTContext.getContext().getLocale();
String M001 = WTMessage.getLocalizedMessage(RESOURCE , "M001", new Object[]{}, WTContext.getContext().getLocale());
WTContainerRef containerRef = WCUtil.getWTContainerRefForDrawing();
WTContainerRef partContainerRef = WCUtil.getWTContainerRefForPart();

Folder folder = FolderHelper.service.getFolder("/Default/DOCUMENT", containerRef);

String ORG_802 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_802", new Object[]{}, locale);
String ORG_1406 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_1406", new Object[]{}, locale);
String ORG_244 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_244", new Object[]{}, locale);
String ORG_176 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_176", new Object[]{}, locale);
String ORG_1091 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_1091", new Object[]{}, locale);
String ORG_890 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_890", new Object[]{}, locale);

String SELECT_FOLDER = WTMessage.getLocalizedMessage(RESOURCE , "SELECT_FOLDER", new Object[]{}, locale);
String STANDARD = WTMessage.getLocalizedMessage(RESOURCE , "STANDARD", new Object[]{}, locale);
String EXIST_DRAWING = WTMessage.getLocalizedMessage(RESOURCE , "EXIST_DRAWING", new Object[]{}, locale);
String DESCRIPTION = WTMessage.getLocalizedMessage(RESOURCE , "DESCRIPTION", new Object[]{}, locale);
%>

<script>
document.title = "<%=WTMessage.getLocalizedMessage(RESOURCE , "DOC_MNG_CREATE_DOC_TITLE", new Object[]{}, locale)%>";
</script>

<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
<form name=documentCreateForm method=post enctype="multipart/form-data">
<input type=hidden name=cmd value='save'>
<input type=hidden name=lifecycle value='Narae_LC'>
<input type=hidden name=docType value='NaraeDoc'>
<input type=hidden name=menu>
<input type=hidden name=fid value="">
<input type=hidden name=location value="">
<input type=hidden name=module value="">
<input type=hidden name=subMenu value="">
<input type=hidden name=approval>
<input type=hidden name=isLast value="">

<table width="100%" border="0" cellpadding="0" cellspacing="10" > <!--//여백 테이블-->
<tr  height=5><td>

<table id="creatingDrawing" style="width: 800px;">
	<tr>
		<td class="attributePanel-asterisk" align=left>
			<H2><%=WTMessage.getLocalizedMessage(RESOURCE , "DOC_MNG_CREATE_DOC", new Object[]{}, locale)%></H2>
		</td>
	</tr>
</table>

<table width="100%" border="0" cellpadding="10" cellspacing="3">

	<tr  align=center>
		<td valign="top" style="padding:0px 0px 0px 0px">

			<table width="100%" border="0" cellpadding="1" cellspacing="1" bgcolor=#9CAEC8 align=center>
				<tr><td height=1 width=100%></td></tr>
			</table>
			<table width="100%" border="0" cellpadding="0" cellspacing="0" align=center>
			<col width='100'><col><col width='100'><col>

				<tr bgcolor="ffffff" height=35>
					<td class="tdblueM">문서분류 <span class="style1">*</span></td>
					<td class="tdwhiteL">
						<input class="txt_field" type="text" value="<%=folder.getFolderPath().replace("Default", "CAD_DOC") %>" id="partFolder" name="partFolder" size=60 readOnly></input>
						<input id="LocationPicker___old" name="partFolder___old" value="/PART" size="25/" type="hidden">
						<a id="newlocation_loc_img" href="javascript:launchFolderPicker ('/Windchill/servlet/WindchillAuthGW/wt.enterprise.URLProcessor/invokeAction?action=cadxBrowseLocations&amp;oid=<%=folder.getPersistInfo().getObjectIdentifier().toString()%>&amp;containerVisibilityMask=<%=folder.getPersistInfo().getObjectIdentifier().toString()%>&amp;accessPermission=modify&amp;displayHotlinks=false&amp;displayCreateFolder=true',document.getElementsByName('partFolder')[0],'selectedFolderFromFolderContext')">
						<img border="0" src="/Windchill/netmarkets/jsp/narae/portal/images/s_search.gif"></img>
						</a>
						<input name="selectedFolderFromFolderContext" value="" type="hidden"><input name="defaultFolderFromRulesEngine" value="" type="hidden"><input name="FormProcessorDelegate" value="com.ptc.windchill.enterprise.folder.LocationPropertyProcessor" type="hidden"> 
					</td>
				</tr>
				
				<tr bgcolor="ffffff" height=35>
					<td class="tdblueM">문서명 <span class="style1">*</span></td>
					<td class="tdwhiteL">
						<input name="name" id="name" class="txt_field" size="85" style="width:90%"/>
					</td>
				</tr>
				<tr bgcolor="ffffff" height=35>
					<td class="tdblueM"><%=ORG_802%></td>
					<td class="tdwhiteL">
						<textarea name="description" id="description" cols="10" rows="5" class="fm_area" style="width:98%"></textarea>
					</td>
				</tr>

				<tr bgcolor="ffffff" height=35>
					<td class="tdblueM"><%=ORG_244%> <span class="style1">*</span></td>
					<td class="tdwhiteL"><input name="primary" id="primary" type="file"  class="txt_field" size="80" border="0"></td>
				</tr>
				<tr bgcolor="ffffff" height=35>
					<td class="tdblueM"><%=ORG_176%></td>
					<td class="tdwhiteL">
						<jsp:include page="/netmarkets/jsp/narae/portal/attacheFile_include.jsp" flush="true">
							<jsp:param name="form" value="mainform"/>
							<jsp:param name="command" value="insert"/>
						</jsp:include>
					</td>
				</tr>
				<tr bgcolor="ffffff" height=35>
                    <td class="tdblueM">프로젝트 <span class="style1">*</span></td>
                    <td class="tdwhiteL">
                       <%if( !ServerConfigHelper.getServerHostName().equals(TEST_SERVER) ) { %>
<!-- 					   <input name="prjName" id="prjName" style="width:300px;" type="text" readonly=""> -->
					   <input name="prjName" id="prjName" style="width:300px;" type="text">    
					   <%} else { %>
                        <input name="prjName" id="prjName" style="width:300px;" type="text">
                        <%} %>
					   <a href="#" onclick="projectSearch();">
					   <img src="/Windchill/netmarkets/jsp/narae/portal/images/s_search.gif" border="0"></a>
                         <input name="prjNo" id="prjNo" type="hidden"> <input name="prjSeqNo" id="prjSeqNo" type="hidden">
						 <input name="unitCode" id="unitCode" type="hidden">
                       <input name="projectName" id="projectName"" type="hidden">
                    </td>
                </tr>
				<tr bgcolor="ffffff" height=35>
					<td class="tdblueM">관련품목</td>
					<td class="tdwhiteL">
						<input type=hidden name=product>
						<jsp:include page="/netmarkets/jsp/narae/part/select_include.jsp" flush="true">
                       <jsp:param value="mainform" name="formName"/>
                       </jsp:include>
                    </td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td align="center" colspan=2>

			<table border="0" cellpadding="0" cellspacing="4" align="center">
                            <tr>
                            	<td>
                                    <a onclick="saveDocument();" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="80"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center"><%=ORG_890%></div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
                                </td>
                            </tr>
           </table>
		</td>
	</tr>

</table>
		</td>
	</tr>
</table>

<input id="partFolderValue" name="partFolderValue" type="hidden">
<input id="nameValue" name="nameValue" type="hidden">
<input id="descriptionValue" name="descriptionValue" type="hidden">
<input id="projectValue" name="projectValue" type="hidden">
<input id="partOidList" name="partOidList" type="hidden">

</form>
<!-- Initialize -->

<iframe id="frmIEAction" name="frmIEAction" style="width:0px;height:0px;visibility:hidden"></iframe>

<script>
function projectSearch(){
	var str="/Windchill/netmarkets/jsp/narae/erp/erpProjectList.jsp";
    var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=no,";
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

// function selectDoc() {
//     var url = "/Windchill/netmarkets/jsp/narae/document/documentPicker.jsp?module=document&mode=selectDoc";
//     var attache = window.showModalDialog(url,window,"help=no; scroll=yes; resizable=yes; dialogWidth=800px; dialogHeight:650px; center:yes");

//     if(typeof attache == "undefined" || attache == null)
//     	return;

//     addDoc(attache);
// }

function selectDoc() {
	var url = "/Windchill/netmarkets/jsp/narae/document/documentPicker.jsp?module=document&mode=selectDoc";
	var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=no,";
    var popWidth = 1000;
    var popHeight = 600;
    var leftpos = (screen.width - popWidth)/ 2;
    var toppos = (screen.height - popHeight) / 2 ;
    var rest = "width="+popWidth+",height="+popHeight+",left=" + leftpos + ',top=' + toppos;
    
    var newwin = window.open(url, "selectDoc", opts+rest);
    newwin.focus();

    addDoc(newwin);
}

function addDoc(arrObj) {
    var pForm = document.mainform;

    if(!arrObj.length) return;

    var subarr;
    var docOid;//
    var docNumber;//
    var docName;//
    var docVersion;//
    var docCheck = true;

    for(var i = 0; i < arrObj.length; i++) {
        docCheck = true;

        subarr = arrObj[i];
        docOid = subarr[0];//
        docNumber = subarr[1];//
        docName = subarr[2];//
        docVersion = subarr[3];//
        
        if(pForm['docOid']) {
            if(pForm['docOid'].length) {
                for(var j = 0; j < pForm['docOid'].length; j++) {
                    if(pForm['docOid'][j].value == docOid) docCheck = false;
                }
            } else {
                if(pForm['docOid'].value == docOid) docCheck = false;
            }
        }
        
        console.log("docCheck = " + docCheck);

        if(docCheck) {
        	var userRow1 = docTable.children[0].appendChild(innerTempTable.rows[0].cloneNode(true));
            userRow1.childNodes[0].width="8%";
            setHtml(userRow1.childNodes[0], "<input type=\"checkbox\" name=\"DocDelete\"><input type=\"hidden\" name=\"docOid\" value=\""+docOid+"\">");
            setHtml(userRow1.childNodes[1], docNumber);
            setHtml(userRow1.childNodes[2], "<nobr>"+docName+"</nobr>");
            setHtml(userRow1.childNodes[3], docVersion);
        }
    }
}

function setHtml( tt, data) {
    tt.innerHTML = data;
}

function deleteDoc() {
    var pForm = document.mainform;

    if(pForm['DocDelete'] && pForm['DocDelete'].length) {
        for(var i=pForm['DocDelete'].length-1; i>=0; i--) {
            if(pForm['DocDelete'][i].checked) docTable.deleteRow(i+1);
        }
    }else {
        if(pForm['DocDelete'].checked) docTable.deleteRow(1);b
    }
}

function saveDocument() {
	if ( document.getElementById("partFolder").value == null || document.getElementById("partFolder").value == "" ) {
		alert("<%=WTMessage.getLocalizedMessage(RESOURCE , "DIDNT_SELECT_FOLDER", new Object[]{}, locale)%>");
		return;
	}

	if ( document.getElementById("name").value == null || document.getElementById("name").value == "" ) {
		alert("<%=WTMessage.getLocalizedMessage(RESOURCE , "DIDNT_SELECT_DOC_NAME", new Object[]{}, locale)%>");
		return;
	}
	
	if ( document.getElementById("primary").value == null || document.getElementById("primary").value == "" ) {
		alert("<%=WTMessage.getLocalizedMessage(RESOURCE , "DIDNT_SELECT_PRIMARY", new Object[]{}, locale)%>");
		return;
	}
	
	if ( document.getElementById("prjName").value == null || document.getElementById("prjName").value == "" ) {
		alert("<%=WTMessage.getLocalizedMessage(RESOURCE , "DIDNT_SELECT_PROJECT", new Object[]{}, locale)%>");
		return;
	}
		
	if (!confirm("<%=WTMessage.getLocalizedMessage(RESOURCE , "ASK_CREATE_DOCUMENT", new Object[]{}, locale)%>")) return;		
	
	document.getElementById("partFolderValue").value = encodeURIComponent(document.getElementById("partFolder").value);
	document.getElementById("nameValue").value = encodeURIComponent(document.getElementById("name").value);
	document.getElementById("descriptionValue").value = encodeURIComponent(document.getElementById("description").value);
	document.getElementById("projectValue").value = encodeURIComponent(document.getElementById("prjName").value);
	
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
	
	document.getElementById("partOidList").value = selectedpartOid;
	
	var url="/Windchill/netmarkets/jsp/narae/document/action/createDocumentAction.jsp";
	var frm = document.getElementsByName("mainform")[0];
	frm.encoding = "multipart/form-data";
	frm.method = "POST";
	frm.action = url;
	frm.target = "frmIEAction";
	frm.submit();
		
}

function showFinished(saveType) {
	if(saveType != null && saveType.length == 0 ) {
		alert("<%=WTMessage.getLocalizedMessage(RESOURCE , "COMPLETE_CREATE_DOCUMENT", new Object[]{}, locale)%>!");
		window.location = "/Windchill/app/#ptc1/narae/document/searchDocument";
	} else {
		alert("<%=WTMessage.getLocalizedMessage(RESOURCE , "FAILED_CREATE_DOCUMENT", new Object[]{}, locale)%>!\n" + saveType);
	}	
}
</script>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>
