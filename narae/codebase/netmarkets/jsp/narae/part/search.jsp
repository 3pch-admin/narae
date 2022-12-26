<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ taglib prefix="mvc" uri="http://www.ptc.com/windchill/taglib/jcaMvc"%>
<%@ taglib prefix="wrap" uri="http://www.ptc.com/windchill/taglib/wrappers"%>
<%@ taglib prefix="narae" uri="http://www.ptc.com/windchill/taglib/narae"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="wctags"%>
<link rel="stylesheet" href="/Windchill/netmarkets/jsp/narae/css/e3ps.css">
<%@ include file="/netmarkets/jsp/narae/code/code1.jspf"%>

<%@ page import="wt.util.*, wt.org.*, wt.session.*, wt.inf.container.*, ext.narae.util.*, ext.narae.ui.*, java.util.*" %>
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
<form name="partSrchForm" method="post" style="padding:0px;margin:0px">

    <table width="100%" border="0" cellpadding="0" cellspacing="0" > <!--//여백 테이블-->
        <tr align="center" height="5">
            <td>
            	<table id="creatingDrawing" style="width: 800px;" align=left>
					<tr>
						<td class="attributePanel-asterisk" align=left>
							<H2><%=WTMessage.getLocalizedMessage(RESOURCE , "PART_MNG_SEARCH_PART", new Object[]{}, locale)%></H2>
						</td>
				
					</tr>
				</table>
                <table width="100%" border="0" cellpadding="0" cellspacing="3" >
                    <tr align="center">
                        <td valign="top" style="padding:0px 0px 0px 0px">

                                <table width="100%" border="0" cellpadding="1" cellspacing="1" bgcolor="#9CAEC8" align="center">
                                    <tr>
                                        <td height="1" width=100%></td>
                                    </tr>
                                </table>
                                <table width="100%" border="0" cellpadding="0" cellspacing="0" align="center"
                                       style="border-left-width: 1px;border-left-style: solid;border-left-color: #e6e6e6;">
                                    <col width="10%"><col width="40%"><col width="10%"><col width="40%">
                                    <tr>
                                        <td class="tdblueM"><%=WTMessage.getLocalizedMessage(RESOURCE , "PART_TYPE", new Object[]{}, locale)%></td>
                                        <td class="tdwhiteL">
                                        	<input class="txt_field" type="text" value="" id="partFolder" name="partFolder" size=40 readOnly></input>
											<input id="LocationPicker___old" name="partFolder___old" value="/PART" size="25/" type="hidden">
											<a id="newlocation_loc_img" href="javascript:launchFolderPicker ('/Windchill/servlet/WindchillAuthGW/wt.enterprise.URLProcessor/invokeAction?action=cadxBrowseLocations&amp;oid=<%=partContainerRef.getObjectId().getStringValue()%>&amp;containerVisibilityMask=<%=partContainerRef.getObjectId().getStringValue()%>&amp;accessPermission=modify&amp;displayHotlinks=false&amp;displayCreateFolder=true',document.getElementsByName('partFolder')[0],'selectedFolderFromFolderContext')">
											<img border="0" src="/Windchill/netmarkets/jsp/narae/portal/images/s_search.gif"></img>
											</a>
											<input name="selectedFolderFromFolderContext" id="selectedFolderFromFolderContext" value="" type="hidden"><input name="defaultFolderFromRulesEngine" value="" type="hidden"><input name="FormProcessorDelegate" value="com.ptc.windchill.enterprise.folder.LocationPropertyProcessor" type="hidden"> 
                                        </td>
                                        <td class="tdblueM"><%=WTMessage.getLocalizedMessage(RESOURCE , "STATUS", new Object[]{}, locale)%></td>
                                        <td class="tdwhiteL">
	                                        <select name="state" id="state">
	                                        	<OPTION value=""><%=WTMessage.getLocalizedMessage(RESOURCE , "ALL", new Object[]{}, locale)%></OPTION>
												<OPTION value="INWORK"><%=WTMessage.getLocalizedMessage(RESOURCE , "WORKIN", new Object[]{}, locale)%></OPTION>
												<OPTION value="RELEASED"><%=WTMessage.getLocalizedMessage(RESOURCE , "RELEASED", new Object[]{}, locale)%></OPTION>
												<option value="CHECKWAIT"><%=WTMessage.getLocalizedMessage(RESOURCE , "CHECKWAIT", new Object[]{}, locale)%></option>
						                        <option value="APPROVEWAIT"><%=WTMessage.getLocalizedMessage(RESOURCE , "APPROVEWAIT", new Object[]{}, locale)%></option>
						                        <option value="SENT_ERP"><%=WTMessage.getLocalizedMessage(RESOURCE , "SENT_ERP", new Object[]{}, locale)%></option>
						                        <option value="RETURN"><%=WTMessage.getLocalizedMessage(RESOURCE , "RETURN", new Object[]{}, locale)%></option>
	                                    	</select>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="tdblueM"><%=WTMessage.getLocalizedMessage(RESOURCE , "PART_NUMBER", new Object[]{}, locale)%></td>
                                        <td class="tdwhiteL">
                                            <input name="number" id="number" class="txt_field" size="30" >
                                        </td>
                                        <td class="tdblueM"><%=WTMessage.getLocalizedMessage(RESOURCE , "PART_NAME", new Object[]{}, locale)%></td>
                                        <td class="tdwhiteL">
                                            <input name="name" id="name" class="txt_field" size="30">
                                        </td>
                                    </tr>
                                        <tr>
											<td class="tdblueM"><%=WTMessage.getLocalizedMessage(RESOURCE , "STANDARD", new Object[]{}, locale)%></td>
											<td class="tdwhiteL">
												<input name="spec" id="spec" class="txt_field" size="45" engnum="engnum" style="width:60%" >
											</td>
                                        	<td class="tdblueM"><%=WTMessage.getLocalizedMessage(RESOURCE , "MAKER", new Object[]{}, locale)%></td>
											<td class="tdwhiteL">
												<input name="MAKER" id="MAKER" class="txt_field" size="45" engnum="engnum" style="width:60%">
												<a href="#" onclick="codeSearch('MAKER');"><img src="/Windchill/netmarkets/jsp/narae/portal/images/s_search.gif" border=0></a>
												<a href="#" onclick="clearText('MAKER');"><img src="/Windchill/netmarkets/jsp/narae/portal/images/x.gif" border="0" /></a>
											</td>											
                                        </tr>
                                        <tr>
                                            <td class="tdblueM"><%=WTMessage.getLocalizedMessage(RESOURCE , "CREATED_ON", new Object[]{}, locale)%></td>
                                            <td class="tdwhiteL">
                                                <input name="predate" id="predate" class="txt_field" size="12"  maxlength="15" readonly="readonly">
                                                <a href="#" onclick="openCal('predate');"><img src="/Windchill/netmarkets/jsp/narae/portal/images/calendar_icon.gif" border="0" /></a>
                                                <a href="#" onclick="clearText('predate');"><img src="/Windchill/netmarkets/jsp/narae/portal/images/x.gif" border="0" /></a>
                                                ~
                                                <input name="postdate" id="postdate" class="txt_field" size="12"  maxlength="15" readonly="readonly">
                                                <a href="#" onclick="openCal('postdate');"><img src="/Windchill/netmarkets/jsp/narae/portal/images/calendar_icon.gif" border="0" /></a>
                                                <a href="#" onclick="clearText('postdate');"><img src="/Windchill/netmarkets/jsp/narae/portal/images/x.gif" border="0" /></a>
                                            </td>
										    <td class="tdblueM"><%=ORG_326%></td>
                                            <td class="tdwhiteL">
                                                <input TYPE="hidden" name="creator" id="creator" >
												<input type=text  name='tempcreator' id='tempcreator' onkeyup="javascript:inputAjax();" onFocus="javascript:initAjax(this,document.documentForm.creator,'/Windchill/netmarkets/jsp/narae/org/AjaxSearchUser.jsp',document.documentForm,150,7)" onkeydown="javascript:pressKeyAjax(this)" value="<%=tempcreator%>" readOnly>
												
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

											<td class="tdblueM"><%=WTMessage.getLocalizedMessage(RESOURCE , "PART_DESC", new Object[]{}, locale)%></td>
											<td class="tdwhiteL">
												<input name="description" id="description" class="txt_field" engnum="engnum" style="width:98%">
											</td>
                                        </tr>
                                </table>
                        </td>
                    </tr>
                    <tr height="35">
                        <td align="right">
                            <table border="0" cellpadding="0" cellspacing="4" align="right">
                                <tr>
                                	<td id="registBtn"> 
										<a onclick="submitDocSearch();" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="60"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center"><%=ORG_1098%></div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
						            <td id="registBtn"> 
										<a onclick="resetData();" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="60"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center"><%=ORG_172%></div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
						            </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</form>
<!-- New Design -->
<input id="first" name="first" type="hidden" value="true">

<mvc:tableContainer compId="ext.narae.part.PartList" height="500"></mvc:tableContainer>

<script>
function resetData() {
	document.mainform.reset();
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

function submitDocSearch(){
		var selectedFolderFromFolderContext = document.getElementById('selectedFolderFromFolderContext').value;
		var state = document.getElementById('state').value;
		var name = document.getElementById('name').value;
		var number = document.getElementById('number').value;
		var islastversion1 = document.getElementsByName("islastversion");
		var version = "";
		if( islastversion1 != null ) {
			for( var index=0; index < islastversion1.length; index++) {
				if( islastversion1[index].checked ) version = islastversion1[index].value;
			}
		}
		var spec = document.getElementById('spec').value;
		var maker = document.getElementById('MAKER').value;
		var startdate = document.getElementById('predate').value;
		var enddate = document.getElementById('postdate').value;
		var creator = document.getElementById('creator').value;
		var desc = document.getElementById('description').value;
		
		if( selectedFolderFromFolderContext == "" && name == "" && number == "" && spec == "" && state == "" &&
			maker == "" && creator == "" && startdate == "" && enddate == "" && desc == "" ) {
			alert("<%=M001%>");
		} else {
			var params = {
					selectedFolderFromFolderContext : selectedFolderFromFolderContext,
					state : state,
					name : name,
					number : number,
					version : version,
					spec : spec,
					maker : maker,
					startdate : startdate,
					enddate : enddate,
					creator : creator,
					desc : desc,
					first : ""
			};

			PTC.jca.table.Utils.reload('ext.narae.part.PartList', params, true);
		}
	}

	function initialize(){
		document.getElementById('state').value = "";
		document.getElementById('name').value = "";
		document.getElementById('number').value = "";
		document.getElementById('version').value = "true";
		document.getElementById('spec').value = "";
		document.getElementById('maker').value = "";
		document.getElementById('startdate').value = "";
		document.getElementById('enddate').value = "";
		document.getElementById('creator').value = "";
		document.getElementById('desc').value = "";
	}
	
</script>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>
