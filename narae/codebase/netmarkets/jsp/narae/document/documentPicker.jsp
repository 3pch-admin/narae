<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ taglib prefix="mvc" uri="http://www.ptc.com/windchill/taglib/jcaMvc"%>
<%@ taglib prefix="wrap" uri="http://www.ptc.com/windchill/taglib/wrappers"%>
<%@ taglib prefix="narae" uri="http://www.ptc.com/windchill/taglib/narae"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="wctags"%>

<link rel="stylesheet" href="/Windchill/netmarkets/jsp/narae/css/e3ps.css">
<%@ page import="wt.util.*, wt.org.*, wt.session.*, wt.inf.container.*, ext.narae.util.*, ext.narae.ui.*, java.util.*" %>
<%
String RESOURCE = "ext.narae.ui.common.resource.NaraeCommonRB";
String UNITRB = "ext.narae.ui.common.resource.NareUnitRB";
Locale locale = WTContext.getContext().getLocale();
String M001 = WTMessage.getLocalizedMessage(RESOURCE , "M001", new Object[]{}, WTContext.getContext().getLocale());
WTContainerRef containerRef = WCUtil.getWTContainerRefForDrawing();
WTContainerRef partContainerRef = WCUtil.getWTContainerRefForPart();

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
<script type="text/javascript">
	if(self.name != 'reload'){
		self.name = 'reload';
		self.location.reload(true);
	}
	else self.name='';
</script>
<table width="100%" border="0" cellpadding="0" cellspacing="0" >
<tr  align=center height=5><td>

<input type="hidden" name="wtPartType" id="wtPartType" value="separable"/>
<input type="hidden" name="source" id="source" value="make"            />
<input type="hidden" name="view" id="view" value="Design"          />
<input type="hidden" name="lifecycle" id="lifecycle"  value="Narae_LC" />
<input class="jca" name="openerActionMethod" value="execute" type="hidden"/>
<input class="jca" name="openerExecuteLocation" value="inbegin" type="hidden"/>
<input class="jca" name="executeLocation" value="inbegin" type="hidden"/>
<input class="jca" name="popupExecuteLocation" value="inbegin" type="hidden"/>
<input id="validateName_LOCATION" name="validateName_LOCATION" type="hidden"/>


<script>
document.title = "<%=WTMessage.getLocalizedMessage(RESOURCE , "DOC_MNG_SEARCH_DOC_TITLE", new Object[]{}, locale)%>";
</script>

<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
<table id="creatingDrawing" style="width:98%;">
	<tr>
		<td class="attributePanel-asterisk" align=left>
			<H2><%=WTMessage.getLocalizedMessage(RESOURCE , "DOC_MNG_SEARCH_DOC", new Object[]{}, locale)%></H2>
		</td>
	</tr>
</table>

<table width="98%" border="0" cellpadding="0" cellspacing="3" bgcolor=#ffffff>
	<tr  align=center>
		<td valign="top" style="padding:0px 0px 0px 0px">

			<table width="100%" border="0" cellpadding="1" cellspacing="1" bgcolor=#9CAEC8 align=center>
				<tr><td height=1 width=100%></td></tr>
			</table>
			<table width="100%" border="0" cellpadding="0" cellspacing="0" align=center>
			<col width='10%'><col width='40%'><col width='10%'><col width='40%'>
				<tr >
					<td class="tdblueM"><%=FOLDER%></td>
					<td class="tdwhiteL" colspan="3">
						<input class="txt_field" type="text" value="" id="partFolder" name="partFolder" size=60 readOnly></input>
						<input id="LocationPicker___old" name="partFolder___old" value="/PART" size="25/" type="hidden">
						<a id="newlocation_loc_img" href="javascript:launchFolderPicker ('/Windchill/servlet/WindchillAuthGW/wt.enterprise.URLProcessor/invokeAction?action=cadxBrowseLocations&amp;oid=<%=containerRef.getObjectId().getStringValue()%>&amp;containerVisibilityMask=<%=partContainerRef.getObjectId().getStringValue()%>&amp;accessPermission=modify&amp;displayHotlinks=false&amp;displayCreateFolder=true',document.getElementsByName('partFolder')[0],'selectedFolderFromFolderContext')">
						<img border="0" src="/Windchill/netmarkets/jsp/narae/portal/images/s_search.gif"></img>
						</a>
						<input name="selectedFolderFromFolderContext" value="" type="hidden"><input name="defaultFolderFromRulesEngine" value="" type="hidden"><input name="FormProcessorDelegate" value="com.ptc.windchill.enterprise.folder.LocationPropertyProcessor" type="hidden"> 
					</td>
				</tr>
				<tr >
					<td class="tdblueM"><%=ORG_812%></td>
					<td class="tdwhiteL">
						<input name="number" id="number" class="txt_field" size="30"  value="<%=number%>" onKeyDown="javascript: if(event.keyCode==13){doSubmit();}"/>
					</td>
					<td class="tdblueM"><%=DOC_NAME%></td>
					<td class="tdwhiteL">
						<input name="name" id="name" class="txt_field" size="30" value="<%=nameValue%>" onKeyDown="javascript: if(event.keyCode==13){doSubmit();}"/>
					</td>
				</tr>
			</table>
			
			<div <%if("false".equals(detailCheck)){ %> style="display:none" <%}else{  %>  style="display:"  <%}%> id = 'SearchDetailProject' >
			<table width="100%" border="0" cellpadding="0" cellspacing="0" align=center>
			<col width='10%'><col width='40%'><col width='10%'><col width='40%'>
				<tr >
					<td class="tdblueM"><%=ORG_749%></td>
					<td class="tdwhiteL">
					<input name="islastversion" id="islastversion" type="radio" class="Checkbox" value="true" <%if("true".equals(islastversion)) {%> checked <%}%>>
					<%=ORG_163%>
					<input name="islastversion" id="islastversion" type="radio" class="Checkbox" value="false" <%if("false".equals(islastversion)) {%> checked <%}%>>
					<%=ORG_1144%>
					</td>
					<td class="tdblueM"><%=LAST_APPROVAL_DATE %></td>
					<td class="tdwhiteL">
						<input name="predate" id="predate" class="txt_field" size="12"  maxlength=15 readonly onclick="javascript:openCal('predate');" value="<%=predate%>"/>
						<a href="JavaScript:openCal('predate')"><img src="/Windchill/netmarkets/jsp/narae/portal/images/calendar_icon.gif" border=0></a>
						<a href="JavaScript:clearText('predate')"><img src="/Windchill/netmarkets/jsp/narae/portal/images/x.gif" border=0></a>
						~
						<input name="postdate" id="postdate" class="txt_field" size="12"  maxlength=15 readonly onclick="javascript:openCal('postdate');" value="<%=postdate%>"/>
						<a href="JavaScript:openCal('postdate')"><img src="/Windchill/netmarkets/jsp/narae/portal/images/calendar_icon.gif" border=0></a>
						<a href="JavaScript:clearText('postdate')"><img src="/Windchill/netmarkets/jsp/narae/portal/images/x.gif" border=0></a>
					</td>
				</tr>
				<tr >
					<td class="tdblueM"><%=ORG_802%></td>
					<td class="tdwhiteL" colspan="3">
						<input name="description" id="description" class="txt_field" size="45"  style="width:98%" value="<%=description%>" onKeyDown="javascript: if(event.keyCode==13){doSubmit();}"/>
					</td>
				</tr>
				<tr>
					<td class="tdblueM"><%=ORG_326%></td>
					<td class="tdwhiteL" colspan="3">
						<input TYPE="hidden" name="creator" id="creator" value="<%=creator%>">
						<input type=text  name='tempcreator' id='tempcreator' onkeyup="javascript:inputAjax();" onFocus="javascript:initAjax(this,document.documentForm.creator,'/Windchill/netmarkets/jsp/narae/org/AjaxSearchUser.jsp',document.documentForm,150,7)" onkeydown="javascript:pressKeyAjax(this)" value="<%=tempcreator%>" readOnly>
						
						<a href="JavaScript:selectUser('creator', 'tempcreator')"><img src="/Windchill/netmarkets/jsp/narae/portal/images/s_search.gif" border=0></a>
						<a href="JavaScript:clearText('creator');clearText('tempcreator')"><img src="/Windchill/netmarkets/jsp/narae/portal/images/x.gif" border=0></a>
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
                            	<td id="registBtn"> 
									<a onclick="selectObject();" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="60"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center"><%=WTMessage.getLocalizedMessage(RESOURCE , "SELECT", new Object[]{}, locale)%></div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
                            	<td id="registBtn"> 
									<a onclick="cancelWindow();" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="60"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center"><%=WTMessage.getLocalizedMessage(RESOURCE , "CLOSE", new Object[]{}, locale)%></div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
                            
                            </tr>
                        </table>
		</td>
	</tr>
</table>


</td></tr>
</table>
<input id="first" name="first" type="hidden" value="true">

<input name="detailFlag" id="detailFlag" type="hidden">

<mvc:tableContainer compId="ext.narae.document.DocumentList" height="450"></mvc:tableContainer>

<script>
var isCh = navigator.appVersion.indexOf("Chrome") >= 0;
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
// 	var url = "/Windchill/netmarkets/jsp/narae/org/SelectPeopleFrm.jsp?mode=s";
	
// 	attache = window.showModalDialog(url,window,"help=no; scroll=no; resizable=yes; dialogWidth=750px; dialogHeight:450px; center:yes");
// 	if(typeof attache == "undefined" || attache == null) {
// 		return;
// 	}

// 	addList(attache, inputObj, inputLabel);
	var url = "/Windchill/netmarkets/jsp/narae/org/SelectPeopleFrm.jsp?mode=s&inputObj="+inputObj+"&inputLabel="+inputLabel;
	var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=no,";
    var popWidth = 1000;
    var popHeight = 600;
    var leftpos = (screen.width - popWidth)/ 2;
    var toppos = (screen.height - popHeight) / 2 ;
    var rest = "width="+popWidth+",height="+popHeight+",left=" + leftpos + ',top=' + toppos;
    
    var newwin = window.open( url, "selectUser", opts+rest);
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
	var tempcreator = document.getElementById('tempcreator').value;
	
	if( partFolder == "" && name == "" && number == "" && predate == "" && postdate == "" &&
			description == "" && creator == "" && tempcreator == "") {
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
				tempcreator : tempcreator,
				first : ""
		};

		PTC.jca.table.Utils.reload('ext.narae.document.DocumentList', params, true);
	}
}

function cancelWindow() {
	self.close();
}



function selectObject() {
	var selectedParts = document.getElementsByName('pjl_selPJLsa1__1');
	if( selectedParts == undefined || selectedParts == null || selectedParts.length == 0 ) {
		alert("You didn't selected item.");
		return;
	}
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

// 				window.returnValue= returnArray;
				opener.addDoc(returnArray);
				window.self.close();
		}
	}
	
	xmlHttp1.open("GET","/Windchill/netmarkets/jsp/narae/document/action/generateDocumentList.jsp?parentOid=" +selectedPartList,true);

	xmlHttp1.setRequestHeader("If-Modified-Since","0");
	xmlHttp1.send(null);

	 
}
</script>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>