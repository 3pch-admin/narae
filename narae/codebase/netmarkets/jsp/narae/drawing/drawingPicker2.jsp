<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ taglib prefix="mvc" uri="http://www.ptc.com/windchill/taglib/jcaMvc"%>
<%@ taglib prefix="wrap" uri="http://www.ptc.com/windchill/taglib/wrappers"%>
<%@ taglib prefix="narae" uri="http://www.ptc.com/windchill/taglib/narae"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="wctags"%>
<link rel="stylesheet" href="/Windchill/netmarkets/jsp/narae/css/e3ps.css">

<%@page import="ext.narae.util.WCUtil"%>
<%@ page import="wt.util.*, wt.org.*, wt.session.*, wt.inf.container.*, ext.narae.ui.*, java.util.*" %>
<%
String RESOURCE = "ext.narae.ui.common.resource.NaraeCommonRB";
String UNITRB = "ext.narae.ui.common.resource.NareUnitRB";
Locale locale = WTContext.getContext().getLocale();
String M001 = WTMessage.getLocalizedMessage(RESOURCE , "M001", new Object[]{}, WTContext.getContext().getLocale());
WTContainerRef containerRef = WCUtil.getWTContainerRefForDrawing();
WTContainerRef partContainerRef = WCUtil.getWTContainerRefForPart();

String ORG_914 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_914", new Object[]{}, locale);
String ORG_909 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_909", new Object[]{}, locale);
String ORG_908 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_908", new Object[]{}, locale);
String ORG_749 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_749", new Object[]{}, locale);
String ORG_163 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_163", new Object[]{}, locale);
String ORG_1144 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_1144", new Object[]{}, locale);
//String ORG_327 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_327", new Object[]{}, locale);
//String ORG_903 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_903", new Object[]{}, locale);
String ORG_1098 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_1098", new Object[]{}, locale);
String ORG_172 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_172", new Object[]{}, locale);
String ORG_607 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_607", new Object[]{}, locale);
String ORG_549 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_549", new Object[]{}, locale);
String ORG_576 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_576", new Object[]{}, locale);
String ORG_326 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_326", new Object[]{}, locale);

String ORG_598 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_598", new Object[]{}, locale);
String ALL = WTMessage.getLocalizedMessage(RESOURCE , "ALL", new Object[]{}, locale);
String WORKIN = WTMessage.getLocalizedMessage(RESOURCE , "WORKIN", new Object[]{}, locale);
String RELEASED = WTMessage.getLocalizedMessage(RESOURCE , "RELEASED", new Object[]{}, locale);
String DRAWING_TYPE_ENG = WTMessage.getLocalizedMessage(RESOURCE , "DRAWING_TYPE_ENG", new Object[]{}, locale);
String GAGONG_DRW = WTMessage.getLocalizedMessage(RESOURCE , "GAGONG_DRW", new Object[]{}, locale);
String SOJAE_DWR = WTMessage.getLocalizedMessage(RESOURCE , "SOJAE_DWR", new Object[]{}, locale);
String MATERIAL = WTMessage.getLocalizedMessage(RESOURCE , "MATERIAL", new Object[]{}, locale);
String USE_TYPE = WTMessage.getLocalizedMessage(RESOURCE , "USE_TYPE", new Object[]{}, locale);
String CREATED_ON = WTMessage.getLocalizedMessage(RESOURCE , "CREATED_ON", new Object[]{}, locale);
String DRAWING_TYPE = WTMessage.getLocalizedMessage(RESOURCE , "DRAWING_TYPE", new Object[]{}, locale);

String mode = request.getParameter("mode");
System.out.println("===============> mode:" + mode);

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
<input type=hidden name=oid value="">
<input type=hidden name=doid>
<input type=hidden name=menu value="">
<input type=hidden name=module value="">
<input type=hidden name=fid value="">
<input type=hidden name=detailCheck value="">
<input name="docListOid" id="docListOid" type="hidden">
<input type="hidden" name="wtPartType" id="wtPartType" value="separable">
<input type="hidden" name="source" id="source" value="make">
<input type="hidden" name="view" id="view" value="Design">
<input type="hidden" name="lifecycle" id="lifecycle"  value="Narae_LC">
<input class="jca" name="openerActionMethod" value="execute" type="hidden">
<input class="jca" name="openerExecuteLocation" value="inbegin" type="hidden">
<input class="jca" name="executeLocation" value="inbegin" type="hidden">
<input class="jca" name="popupExecuteLocation" value="inbegin" type="hidden">
<input id="validateName_LOCATION" name="validateName_LOCATION" type="hidden">

<table width="100%" border="0" cellpadding="0" cellspacing="0" >
<tr  align=center height=5><td>

<script>
document.title = "<%=WTMessage.getLocalizedMessage(RESOURCE , "DRAWING_MNG_SEARCH_DRW_TITLE", new Object[]{}, locale)%>";
</script>

<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
<table width="95%" border="0" cellpadding="0" cellspacing="3" bgColor="#ffffff" >
<tr><td>

<table id="creatingDrawing" style="width:98%;">
	<tr>
		<td class="attributePanel-asterisk" align=left>
			<H2><%=WTMessage.getLocalizedMessage(RESOURCE , "DRAWING_MNG_SEARCH_DRW", new Object[]{}, locale)%></H2>
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
					<td class="tdblueM"><%=ORG_914%></td>
					<td class="tdwhiteL">
						<input class="txt_field" type="text" value="" id="partFolder" name="partFolder" size=30 readOnly />
						<input id="LocationPicker___old" name="partFolder___old" value="/PART" size="25/" type="hidden" />
						<a id="newlocation_loc_img" href="javascript:launchFolderPicker ('/Windchill/servlet/WindchillAuthGW/wt.enterprise.URLProcessor/invokeAction?action=cadxBrowseLocations&amp;oid=<%=containerRef.getObjectId().getStringValue()%>&amp;containerVisibilityMask=<%=partContainerRef.getObjectId().getStringValue()%>&amp;accessPermission=modify&amp;displayHotlinks=false&amp;displayCreateFolder=true',document.getElementsByName('partFolder')[0],'selectedFolderFromFolderContext')">
						<img border="0" src="/Windchill/netmarkets/jsp/narae/portal/images/s_search.gif"></img>
						</a>
						<input name="selectedFolderFromFolderContext" value="" type="hidden"><input name="defaultFolderFromRulesEngine" value="" type="hidden"><input name="FormProcessorDelegate" value="com.ptc.windchill.enterprise.folder.LocationPropertyProcessor" type="hidden"> 
					</td>
                    <td class="tdblueM"><%=ORG_598%></td>
                    <td class="tdwhiteL">
	                	<select name="state" id="state">
	                    	<option value=""><%=ALL%></option>
	                        <option value="INWORK"><%=WORKIN%></option>
	                        <option value="APPROVED"><%=RELEASED%></option>
	                    </select>
                    </td>				
				</tr>
				<tr >
					<td class="tdblueM"><%=ORG_908%></td>
					<td class="tdwhiteL">
						<input name="number" id="number" class="txt_field" size="30" engnum="engnum" value="<%=number%>" onKeyDown="javascript: if(event.keyCode==13){doSubmit();}"/>
					</td>
					<td class="tdblueM"><%=ORG_909%></td>
					<td class="tdwhiteL">
						<input name="name" id="name" class="txt_field" size="30" engnum="engnum" value="<%=nameValue%>" onKeyDown="javascript: if(event.keyCode==13){doSubmit();}"/>
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
					<td class="tdblueM"><%=DRAWING_TYPE_ENG%></td>
					<td class="tdwhiteL">
						<input name="drawingType" id="drawingType" value="" type="radio" checked><%=WTMessage.getLocalizedMessage(RESOURCE , "ALL", new Object[]{}, WTContext.getContext().getLocale())%> 
						<input name="drawingType" id="drawingType" value='GAGONG_DRW' type="radio"><%=WTMessage.getLocalizedMessage(RESOURCE , "GAGONG_DRW", new Object[]{}, WTContext.getContext().getLocale())%>
						<input name="drawingType" id="drawingType" value='SOJAE_DWR' type="radio"><%=WTMessage.getLocalizedMessage(RESOURCE , "SOJAE_DWR", new Object[]{}, WTContext.getContext().getLocale())%>
					</td>
				</tr>
				<tr >
					<td class="tdblueM"><%=MATERIAL%></td>
					<td class="tdwhiteL">
						<input name="material" id="material"  class="txt_field" size="45" engnum="engnum" style="width:98%" onKeyDown="javascript: if(event.keyCode==13){doSubmit();}"/>
					</td>
					<td class="tdblueM"><%=USE_TYPE%></td>
					<td class="tdwhiteL">
						<input name="treatment" id="treatment" class="txt_field" size="45" engnum="engnum" style="width:98%" onKeyDown="javascript: if(event.keyCode==13){doSubmit();}"/>
					</td>
				</tr>
			</table>
			
			<div <%if("false".equals(detailCheck)){ %> style="display:none" <%}else{  %>  style="display:"  <%}%> id = 'SearchDetailProject' >
			<table width="100%" border="0" cellpadding="0" cellspacing="0" align=center>
			<col width='10%'><col width='40%'><col width='10%'><col width='40%'>				
				<tr >
					<td class="tdblueM"><%=ORG_549%></td>
					<td class="tdwhiteL">
						<input name="description" id="description" class="txt_field" size="45" engnum="engnum" style="width:98%" value="<%=description%>" onKeyDown="javascript: if(event.keyCode==13){doSubmit();}"/>
					</td>
					<td class="tdblueM"><%=CREATED_ON%></td>
					<td class="tdwhiteL">
						<input name="predate" id="predate" class="txt_field" size="12" engnum="engnum" maxlength=15 readonly onclick="javascript:openCal('predate');" value="<%=predate%>"/>
						<a href="JavaScript:openCal('predate')"><img src="/Windchill/netmarkets/jsp/narae/portal/images/calendar_icon.gif" border=0></a>
						<a href="JavaScript:clearText('predate')"><img src="/Windchill/netmarkets/jsp/narae/portal/images/x.gif" border=0></a>
						~
						<input name="postdate" id="postdate" class="txt_field" size="12" engnum="engnum" maxlength=15 readonly onclick="javascript:openCal('postdate');" value="<%=postdate%>"/>
						<a href="JavaScript:openCal('postdate')"><img src="/Windchill/netmarkets/jsp/narae/portal/images/calendar_icon.gif" border=0></a>
						<a href="JavaScript:clearText('postdate')"><img src="/Windchill/netmarkets/jsp/narae/portal/images/x.gif" border=0></a>
					</td>
				</tr>
				<tr>
					<td class="tdblueM"><%=DRAWING_TYPE%></td>
					<td class="tdwhiteL" >
						 <SELECT name='authoringType' id='authoringType'>
                            <OPTION value=''><%=ORG_576%></OPTION>
                            <OPTION value='ACAD'>AutoCAD</OPTION>
                            <OPTION value='PDF'>PDF</OPTION>
                            <OPTION value='OTHER'>ETC</OPTION>
                        </SELECT>
					</td>
					<td class="tdblueM"><%=ORG_326%></td>
                    <td class="tdwhiteL">
                        <input TYPE="hidden" name="creator" id="creator"  >
                        <input type=text  name='tempcreator' id="tempcreator" readOnly >

                        <a href="JavaScript:selectUser('creator','tempcreator')"><img src="/Windchill/netmarkets/jsp/narae/portal/images/s_search.gif" border="0"></a>
                        <a href="JavaScript:clearText('creator');clearText('tempcreator')"><img src="/Windchill/netmarkets/jsp/narae/portal/images/x.gif" border=0></a>
                    </td>
				</tr>
                <tr>
                	<td colspan="4">
					</td>
                </tr>
			</table>
			</div>
		</td>
	</tr>
	<tr>
		<td align="right" bgcolor=#FFFFFF>
			<table><tr>
			<td id="registBtn"> 
				<a onclick="javascript:doSubmit();" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="60"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center"><%=ORG_1098%></div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
            <td id="registBtn"> 
				<a onclick="javascript:resetData();" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="60"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center"><%=ORG_172%></div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
            <td id="registBtn"> 
				<a onclick="javascript:detailSearch();" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="80"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center">+<%=ORG_607%></div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
            <td id="registBtn"> 
				<a onclick="javascript:selectObject();" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="60"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center"><%=WTMessage.getLocalizedMessage(RESOURCE , "SELECT", new Object[]{}, locale)%></div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
            <td id="registBtn"> 
				<a onclick="javascript:cancelWindow();" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="60"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center"><%=WTMessage.getLocalizedMessage(RESOURCE , "CLOSE", new Object[]{}, locale)%></div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
                            
            </tr></table>
		</td>
	</tr>
</table>
		</td>
	</tr>
</table>
<input id="first" name="first" type="hidden" value="true">
<input name="detailFlag" id="detailFlag" type="hidden" value="false">

<%if( mode != null && mode.trim().equals("single") ) { %>
<mvc:tableContainer compId="ext.narae.drawing.DrawingListSingle" height="500"></mvc:tableContainer>
<%} else { %>
<mvc:tableContainer compId="ext.narae.drawing.DrawingList" height="500"></mvc:tableContainer>
<%}%>
</td></tr>
</table>

<script>
document.title = "<%=WTMessage.getLocalizedMessage(RESOURCE , "DRAWING_MNG_SEARCH_DRW_TITLE", new Object[]{}, locale)%>";
</script>

<script>
function resetData() {
	document.mainform.reset();
	document.getElementById('tempcreator').value = "";
}

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

function selectUser(inputObjName, inputLabelName) {
	var inputObj = document.getElementById(inputObjName );
	var inputLabel = document.getElementById(inputLabelName );
// 	var url = "/Windchill/netmarkets/jsp/narae/org/SelectPeopleFrm.jsp?mode=s";//&chief=true";
	
// 	attache = window.showModalDialog(url,window,"help=no; scroll=no; resizable=yes; dialogWidth=750px; dialogHeight:450px; center:yes");
// 	if(typeof attache == "undefined" || attache == null) {
// 		return;
// 	}

// 	addList(attache, inputObj, inputLabel);
	
// 	var url = "/Windchill/netmarkets/jsp/narae/org/SelectPeopleFrm.jsp?mode=s";
var url = "/Windchill/netmarkets/jsp/narae/org/SelectPeopleFrm.jsp?mode=s&inputObj="+inputObjName+"&inputLabel="+inputLabelName;
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
		userName = subarr[5] + " (" + subarr[4] + ")";//

		inputObj.value = peopleOid;
		inputLabel.value = userName;    
	}
}

function doSubmit(){
	var partFolder = document.getElementById("partFolder").value;
	var state = document.getElementById("state").value;
	var number = document.getElementById("number").value;
	var name = document.getElementById("name").value;
	var islastversion1 = document.getElementsByName("islastversion");
	var islastversion = "";
	if( islastversion1 != null ) {
		for( var index=0; index < islastversion1.length; index++) {
			if( islastversion1[index].checked ) islastversion = islastversion1[index].value;
		}
	}
	var drwType1 = document.getElementsByName("drawingType");
	var drawingType = "";
	if( drwType1 != null ) {
		for( var index=0; index < drwType1.length; index++) {
			if( drwType1[index].checked ) drawingType = drwType1[index].value;
		}
	}
	var material = document.getElementById('material').value;
	var treatment = document.getElementById('treatment').value;
	var description = document.getElementById('description').value;
	var predate = document.getElementById('predate').value;
	var postdate = document.getElementById('postdate').value;
	var authoringType = document.getElementById('authoringType').value;
	var creator = document.getElementById('creator').value;	
	var detailFlag = document.getElementById('detailFlag').value;	
		
	if( partFolder == "" && state == "" && number == "" && name == "" && material == "" && treatment == "" && description == "" && creator == "" && predate == "" && postdate == "" && authoringType == "") {
		alert("<%=M001%>");
	} else {
		var params = {
				partFolder : partFolder,
				state : state,
				number : number,
				name : name,
				islastversion : "true",
				drawingType : drawingType,
				material : material,
				treatment : treatment,
				description : description,
				predate : predate,
				postdate : postdate,
				authoringType : authoringType,
				creator : creator,
				detailFlag : detailFlag,
				first : "",
				docType : "CADDRAWING"
		};

		<%if( mode != null && mode.trim().equals("single") ) { %>
		PTC.jca.table.Utils.reload('ext.narae.drawing.DrawingListSingle', params, true);
		<%} else { %>
		PTC.jca.table.Utils.reload('ext.narae.drawing.DrawingList', params, true);
		<%}%>
		
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

			if (navigator.appVersion.indexOf("MSIE 7.0") >= 0 || navigator.appVersion.indexOf("MSIE 8.0") >= 0 || navigator.appVersion.indexOf("MSIE 9.0")) {
// 				window.returnValue= returnArray;
				opener.addEpm(returnArray);
				window.self.close();
			} else {
// 				window.returnValue= returnArray;
				opener.addEpm(returnArray);
				window.self.close();
				
			}
		}
	}
	
	xmlHttp1.open("GET","/Windchill/netmarkets/jsp/narae/drawing/action/generateDrawingList2.jsp?parentOid=" +selectedPartList,true);

	xmlHttp1.setRequestHeader("If-Modified-Since","0");
	xmlHttp1.send(null);	 
}
	
</script>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>
