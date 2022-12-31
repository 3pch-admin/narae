<%@page import="wt.folder.Folder"%>
<%@page import="wt.folder.FolderHelper"%>
<%@page import="ext.narae.util.WCUtil"%>
<%@page import="wt.util.WTMessage"%>
<%@page import="wt.inf.container.WTContainerRef"%>
<%@page import="java.util.Locale"%>
<%@page import="wt.util.WTContext"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>

<%@ taglib prefix="mvc" uri="http://www.ptc.com/windchill/taglib/jcaMvc"%>
<%@ taglib prefix="wrap" uri="http://www.ptc.com/windchill/taglib/wrappers"%>
<%@ taglib prefix="narae" uri="http://www.ptc.com/windchill/taglib/narae"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="wctags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<link rel="stylesheet" href="/Windchill/netmarkets/jsp/narae/css/e3ps.css">
<%
String RESOURCE = "ext.narae.ui.common.resource.NaraeCommonRB";
String UNITRB = "ext.narae.ui.common.resource.NareUnitRB";
Locale locale = WTContext.getContext().getLocale();
String M001 = WTMessage.getLocalizedMessage(RESOURCE , "M001", new Object[]{}, WTContext.getContext().getLocale());
WTContainerRef containerRef = WCUtil.getWTContainerRefForDrawing();
WTContainerRef partContainerRef = WCUtil.getWTContainerRefForPart();

Folder	 folder = FolderHelper.service.getFolder("/Default/SOFTWARE", containerRef);

String ORG_812 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_812", new Object[]{}, locale);
String ORG_749 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_749", new Object[]{}, locale);
String ORG_163 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_163", new Object[]{}, locale);
String ORG_1144 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_1144", new Object[]{}, locale);
String ORG_802 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_802", new Object[]{}, locale);
String ORG_326 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_326", new Object[]{}, locale);
String ORG_1098 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_1098", new Object[]{}, locale);
String ORG_172 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_172", new Object[]{}, locale);
String ORG_607 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_607", new Object[]{}, locale);
String FOLDER = WTMessage.getLocalizedMessage(RESOURCE , "FOLDER", new Object[]{}, locale);
String DOC_NAME = WTMessage.getLocalizedMessage(RESOURCE , "DOC_NAME", new Object[]{}, locale);
String LAST_APPROVAL_DATE = WTMessage.getLocalizedMessage(RESOURCE , "LAST_APPROVAL_DATE", new Object[]{}, locale);

String detailCheck = "false";
String number = "";
String nameValue = "";
String islastversion = "true";
String predate = "";
String postdate = "";
String description = "";
String creator = "";
String tempcreator = "";
%>


<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
<form name=softwareForm method=post >

<table width="100%" border="0" cellpadding="0" cellspacing="0" > <!--//여백 테이블-->
<tr  align=center height=5><td>
<table id="creatingDrawing" style="width: 800px;" align=left>
				<tr>
					<td class="attributePanel-asterisk" align=left>
						<H2><%=WTMessage.getLocalizedMessage(RESOURCE , "DOC_MNG_SEARCH_SW", new Object[]{}, locale)%></H2>
					</td>
				</tr>
</table>

<table width="100%" border="0" cellpadding="0" cellspacing="3" >
	<tr  align=center>
		<td valign="top" style="padding:0px 0px 0px 0px">
			

			<table width="100%" border="0" cellpadding="1" cellspacing="1" bgcolor=#9CAEC8 align=center>
				<tr><td height=1 width=100%></td></tr>
			</table>
			<table width="100%" border="0" cellpadding="0" cellspacing="0" align=center>
			<col width='10%'><col width='40%'><col width='10%'><col width='40%'>
				<tr >
					<td class="tdblueM">SW 분류</td>
					<td class="tdwhiteL" colspan="3">
						<input class="txt_field" type="text" value="<%=(folder.getFolderPath()).replace("Default", "CAD_DOC") %>" id="partFolder" name="partFolder" size=60 readOnly></input>
						<input id="LocationPicker___old" name="partFolder___old" value="/PART" size="25/" type="hidden">
						<a id="newlocation_loc_img" href="javascript:launchFolderPicker ('/Windchill/servlet/WindchillAuthGW/wt.enterprise.URLProcessor/invokeAction?action=cadxBrowseLocations&amp;oid=<%=folder.getPersistInfo().getObjectIdentifier().toString()%>&amp;containerVisibilityMask=<%=folder.getPersistInfo().getObjectIdentifier().toString()%>&amp;accessPermission=modify&amp;displayHotlinks=false&amp;displayCreateFolder=true',document.getElementsByName('partFolder')[0],'selectedFolderFromFolderContext')">
						<img border="0" src="/Windchill/netmarkets/jsp/narae/portal/images/s_search.gif"></img>
						</a>
						<input name="selectedFolderFromFolderContext" value="" type="hidden"><input name="defaultFolderFromRulesEngine" value="" type="hidden"><input name="FormProcessorDelegate" value="com.ptc.windchill.enterprise.folder.LocationPropertyProcessor" type="hidden"> 

					</td>
					
				</tr>
				<tr >
					<td class="tdblueM">SW 번호</td>
					<td class="tdwhiteL">
						<input name="number" id="number" class="txt_field" size="30"  value="<%=number%>" onKeyDown="javascript: if(event.keyCode==13){doSubmit();}"/>
					</td>
					<td class="tdblueM">SW 명</td>
					<td class="tdwhiteL">
						<input name="name" id="name" class="txt_field" size="30" value="<%=nameValue%>" onKeyDown="javascript: if(event.keyCode==13){doSubmit();}"/>
					</td>
				</tr>
				<tr>
					<td class="tdblueM">프로젝트</td>
                    <td class="tdwhiteL" >
                       <input name="prjName" id="prjName" style="width:300px;" type="text" readonly="">  
						   <a href="#" onclick="projectSearch();">
						   <img src="/Windchill/netmarkets/jsp/narae/portal/images/s_search.gif" border="0"></a>
	                         <input name="prjNo" id="prjNo" type="hidden"> <input name="prjSeqNo" id="prjSeqNo" type="hidden">
							 <input name="unitCode" id="unitCode" type="hidden">
	                   <input name="projectName" id="projectName"" type="hidden"></td>                    
					<td class="tdblueM"><%=ORG_326%></td>
					<td class="tdwhiteL">
						<input type="hidden" name="creator" id="creator" value="">
						<input type=text  name='tempcreator' id='tempcreator'   onkeyup="javascript:inputAjax();" onFocus="javascript:initAjax(this,document.softwareForm.creator,'/Windchill/netmarkets/jsp/narae/org/AjaxSearchUser.jsp',document.softwareForm,150,7)" onkeydown="javascript:pressKeyAjax(this)" value="<%=tempcreator%>" readonly>
						<a href="JavaScript:selectUser('creator', 'tempcreator')"><img src="/Windchill/netmarkets/jsp/narae/portal/images/s_search.gif" border=0></a>
						<a href="JavaScript:clearText('creator');clearText('tempcreator')"><img src="/Windchill/netmarkets/jsp/narae/portal/images/x.gif" border=0></a>
					</td>
				</tr>
				<tr >
					<td class="tdblueM"><%=ORG_749%></td>
					<td class="tdwhiteL">
						<input name="islastversion" id="islastversion" type="radio" class="Checkbox" value="true" checked>
						<%=ORG_163%>
						<input name="islastversion" id="islastversion" type="radio" class="Checkbox" value="false">
						<%=ORG_1144%>
					</td>
					<td class="tdblueM">SW 설명</td>
					<td class="tdwhiteL">
						<input name="description" id="description" class="txt_field" size="45"  style="width:98%">
					</td>
				</tr>
							
			</table>
			
			<div style="display:none" id = 'SearchDetailProject' >
			<table width="100%" border="0" cellpadding="0" cellspacing="0" align=center>
			<col width='10%'><col width='90%'>
					
				<tr>
					<td class="tdblueM">최종승인일자</td>
					<td class="tdwhiteL" >
						<input name="predate" id="predate" class="txt_field" size="12"  maxlength=15 readonly onclick="javascript:openCal('predate');" value="<%=predate%>"/>
						<a href="JavaScript:openCal('predate')"><img src="/Windchill/netmarkets/jsp/narae/portal/images/calendar_icon.gif" border=0></a>
						<a href="JavaScript:clearText('predate')"><img src="/Windchill/netmarkets/jsp/narae/portal/images/x.gif" border=0></a>
						~
						<input name="postdate" id="postdate" class="txt_field" size="12"  maxlength=15 readonly onclick="javascript:openCal('postdate');" value="<%=postdate%>"/>
						<a href="JavaScript:openCal('postdate')"><img src="/Windchill/netmarkets/jsp/narae/portal/images/calendar_icon.gif" border=0></a>
						<a href="JavaScript:clearText('postdate')"><img src="/Windchill/netmarkets/jsp/narae/portal/images/x.gif" border=0></a>
					</td>					
				</tr>									
			</table>
			
			<%
			String keyword = request.getParameter("keyword");
			%>
			</div>
		</td>
	</tr>
	<tr height=35>
		<td align="right">
			
			<table border="0" cellpadding="0" cellspacing="4" align="right">
                            <tr>
                                <td id="registBtn"> 
									<a onclick="doSubmit();" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="60"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center"><%=ORG_1098%></div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
                                <td id="registBtn"> 
									<a onclick="document.mainform.reset();" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="60"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center"><%=ORG_172%></div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
                                <td id="registBtn"> 
									<a onclick="detailSearch();" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="80"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center">+<%=ORG_607%></div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
                            </tr>
                        </table>
		</td>
	</tr>
</table>
		</td>
	</tr>

		</td>
	</tr>
</table>
</form>

<input id="first" name="first" type="hidden" value="true">

<input name="detailFlag" id="detailFlag" type="hidden">

<mvc:tableContainer compId="ext.narae.document.SoftwareList" height="900"></mvc:tableContainer>

<script>
document.title = "<%=WTMessage.getLocalizedMessage(RESOURCE , "DOC_MNG_SEARCH_SW_TITLE", new Object[]{}, locale)%>";
</script>

<script>
function detailSearch() {
	if( document.getElementById("detailFlag").value == "true" ) {
		document.getElementById("detailFlag").value = "false";
		document.getElementById("SearchDetailProject").style.display = "none";
	} else {
		document.getElementById("detailFlag").value = "true";
		document.getElementById("SearchDetailProject").style.display = "";
	}
}

function openCal(variableName) {
	var str="/Windchill/netmarkets/jsp/narae/common/calendar.jsp?form=mainform&obj="+variableName;
	var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=0,resizable=0,";
	leftpos = (screen.width - 224)/ 2; 
	toppos = (screen.height - 230) / 2 ; 
	rest = "width=224,height=230,left=" + leftpos + ',top=' + toppos;
	
	var newwin = window.open( str , "calendar", opts+rest);
	newwin.focus();
}

function clearText(str) {
	var tartxt = document.getElementById(str);
	tartxt.value = "";
}

function selectUser(inputObj, inputLabel) {
//    	var url = "/Windchill/netmarkets/jsp/narae/org/SelectPeopleFrm.jsp?mode=s";
var url = "/Windchill/netmarkets/jsp/narae/org/SelectPeopleFrm.jsp?mode=s&inputObj="+inputObj+"&inputLabel="+inputLabel;
	var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=no,";
    var popWidth = 1000;
    var popHeight = 600;
    var leftpos = (screen.width - popWidth)/ 2;
    var toppos = (screen.height - popHeight) / 2 ;
    var rest = "width="+popWidth+",height="+popHeight+",left=" + leftpos + ',top=' + toppos;
    
    var newwin = window.open(url, "selectUser", opts+rest);
    newwin.focus();
    
	addList(newwin, inputObj, inputLabel);
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
		userName = subarr[4];//

		inputObj.value = peopleOid;
		inputLabel.value = userName;	
	}
}

function doSubmit(){
	var partFolder = document.getElementById('partFolder').value;
	var name = document.getElementById('name').value;
	var number = document.getElementById('number').value;
	var islastversion = document.getElementById('islastversion').checked;
	var predate = document.getElementById('predate').value;
	var postdate = document.getElementById('postdate').value;
	var description = document.getElementById('description').value;
	var creator = document.getElementById('creator').value;
	
	if( partFolder == "" && name == "" && number == "" && predate == "" && postdate == "" &&
			description == "" && creator == "") {
		alert("<%=M001%>");
	} else {
		var params = {
				partFolder : partFolder,
				name : name,
				number : number,
				islastversion : islastversion,
				predate : predate,
				postdate : postdate,
				description : description,
				creator : creator,
				first : ""
		};

		PTC.jca.table.Utils.reload('ext.narae.document.SoftwareList', params, true);
	}
}
</script>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>