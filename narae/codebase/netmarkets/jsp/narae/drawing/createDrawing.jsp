<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<%-- <%@ include file="/netmarkets/jsp/components/beginWizard.jspf" %> --%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf" %>
<%@ taglib prefix="mvc" uri="http://www.ptc.com/windchill/taglib/jcaMvc"%>
<%@ taglib prefix="wrap" uri="http://www.ptc.com/windchill/taglib/wrappers"%>
<%@ taglib prefix="narae" uri="http://www.ptc.com/windchill/taglib/narae"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="wctags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="attachments" uri="http://www.ptc.com/windchill/taglib/attachments" %>

<%@ include file="/netmarkets/jsp/narae/code/code1.jspf"%>

<link rel="stylesheet" href="/Windchill/netmarkets/jsp/narae/css/e3ps.css">
<%@ page import="wt.part.*,wt.util.*, wt.org.*, wt.session.*, wt.inf.container.*, ext.narae.util.*, ext.narae.ui.*, java.util.*" %>

<%
String RESOURCE = "ext.narae.ui.common.resource.NaraeCommonRB";
String UNITRB = "ext.narae.ui.common.resource.NareUnitRB";
Locale locale = WTContext.getContext().getLocale();
String M001 = WTMessage.getLocalizedMessage(RESOURCE , "M001", new Object[]{}, WTContext.getContext().getLocale());
WTContainerRef containerRef = WCUtil.getWTContainerRefForDrawing();
WTContainerRef partContainerRef = WCUtil.getWTContainerRefForPart();

String ORG_938 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_938", new Object[]{}, locale);
String ORG_549 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_549", new Object[]{}, locale);
String ORG_244 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_244", new Object[]{}, locale);
String ORG_576 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_576", new Object[]{}, locale);
String ORG_890 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_890", new Object[]{}, locale);

String DRAWING_CATEGORY = WTMessage.getLocalizedMessage(RESOURCE , "DRAWING_CATEGORY", new Object[]{}, locale);
String PART_REALTIONSHIP = WTMessage.getLocalizedMessage(RESOURCE , "PART_REALTIONSHIP", new Object[]{}, locale);
String RELATE_PART = WTMessage.getLocalizedMessage(RESOURCE , "RELATE_PART", new Object[]{}, locale);
String NEW_PART = WTMessage.getLocalizedMessage(RESOURCE , "NEW_PART", new Object[]{}, locale);
String JUST_CREATE_DRAWING = WTMessage.getLocalizedMessage(RESOURCE , "JUST_CREATE_DRAWING", new Object[]{}, locale);
String SELECT_FOLDER = WTMessage.getLocalizedMessage(RESOURCE , "SELECT_FOLDER", new Object[]{}, locale);
String STANDARD = WTMessage.getLocalizedMessage(RESOURCE , "STANDARD", new Object[]{}, locale);
String EXIST_DRAWING = WTMessage.getLocalizedMessage(RESOURCE , "EXIST_DRAWING", new Object[]{}, locale);
String MATERIAL = WTMessage.getLocalizedMessage(RESOURCE , "MATERIAL", new Object[]{}, locale);
String USE_TYPE = WTMessage.getLocalizedMessage(RESOURCE , "USE_TYPE", new Object[]{}, locale);
String WEIGHT = WTMessage.getLocalizedMessage(RESOURCE , "WEIGHT", new Object[]{}, locale);
String REF_MODEL = WTMessage.getLocalizedMessage(RESOURCE , "REF_MODEL", new Object[]{}, locale);
String MAKING_DRAWING = WTMessage.getLocalizedMessage(RESOURCE , "MAKING_DRAWING", new Object[]{}, locale);
String DRAWING_2D = WTMessage.getLocalizedMessage(RESOURCE , "DRAWING_2D", new Object[]{}, locale);
String DRAWING_TYPE = WTMessage.getLocalizedMessage(RESOURCE , "DRAWING_TYPE", new Object[]{}, locale);
String SECONDARY_ATTACHMENT = WTMessage.getLocalizedMessage(RESOURCE , "SECONDARY_ATTACHMENT", new Object[]{}, locale);
String PROCESSING_DRAWING = WTMessage.getLocalizedMessage(RESOURCE , "PROCESSING_DRAWING", new Object[]{}, locale);
String NO_SELECT_DRAWING_TYPE_CLASS3 = WTMessage.getLocalizedMessage(RESOURCE , "NO_SELECT_DRAWING_TYPE_CLASS3", new Object[]{}, locale);
String NO_COMPLETE_DRAWING_CODE = WTMessage.getLocalizedMessage(RESOURCE , "NO_COMPLETE_DRAWING_CODE", new Object[]{}, locale);
%>

<script>
document.title = "<%=WTMessage.getLocalizedMessage(RESOURCE , "DRAWING_MNG_CREATE_PART_TITLE", new Object[]{}, locale)%>";
</script>

<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
<form name=mainform id=mainform method=post enctype="multipart/form-data">
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
<input type=hidden name=cmd value='save'>
<input type=hidden name=menu>
<input type=hidden name=fid value="">
<input type=hidden name=partfid value="">
<input type=hidden name=location value="">
<input type=hidden name=documentType value="EPM">

<table width="100%" border="0" cellpadding="0" cellspacing="10" > <!--//ì¬ë°± íì´ë¸-->
<tr  height=5><td>
<table id="creatingDrawing" style="width: 800px;">
	<tr>
		<td class="attributePanel-asterisk" align=left>
			<H2><%=WTMessage.getLocalizedMessage(RESOURCE , "DRAWING_MNG_CREATE_PART", new Object[]{}, locale)%></H2>
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
	            <tr>
	            	<td width="15%"></td>
	            	<td width="35%"></td>
	            	<td width="15%"></td>
	            	<td width="35%"></td>
	            </tr>
	            <tr bgcolor="ffffff" height=35>
	                <td class="tdblueM"><%=SELECT_FOLDER %> <span style="COLOR: red;">*</span></td>
	                <td class="tdwhiteL" colspan="3">
	                	<input class="txt_field" type="text" value="" id="partFolder" name="partFolder" size=60 readOnly></input>
						<input id="LocationPicker___old" name="partFolder___old" value="/PART" size="25/" type="hidden">
						<a id="newlocation_loc_img" href="javascript:launchFolderPicker ('/Windchill/servlet/WindchillAuthGW/wt.enterprise.URLProcessor/invokeAction?action=cadxBrowseLocations&amp;oid=<%=containerRef.getObjectId().getStringValue()%>&amp;containerVisibilityMask=<%=partContainerRef.getObjectId().getStringValue()%>&amp;accessPermission=modify&amp;displayHotlinks=false&amp;displayCreateFolder=true',document.getElementsByName('partFolder')[0],'selectedFolderFromFolderContext')">
						<img border="0" src="/Windchill/netmarkets/jsp/narae/portal/images/s_search.gif"></img>
						</a>
						<input name="selectedFolderFromFolderContext" id="selectedFolderFromFolderContext" value="" type="hidden"><input name="defaultFolderFromRulesEngine" value="" type="hidden"><input name="FormProcessorDelegate" value="com.ptc.windchill.enterprise.folder.LocationPropertyProcessor" type="hidden"> 
	                </td>
	            </tr>
	            <tr bgcolor="ffffff" height=35>
                    <td class="tdblueM"><%=PART_REALTIONSHIP %></td>
                    <td class="tdwhiteL" colspan="3">
                    	<input type="radio" name="buildRole" id="buildRole" onClick='partCheck1("link");' value="link">
							<%=RELATE_PART %> 
						<input type="radio" name="buildRole" id="buildRole" onClick='partCheck1("new");' value="new" checked>
							<%=NEW_PART %>
						<!--
						<input type="radio" name="buildRole" id="buildRole" onClick='partCheck1("none");' checked value="none">
							<%=JUST_CREATE_DRAWING %>
						-->
                    </td>
                </tr>
	            <tr bgcolor="ffffff" height=35 id="partLink" style="display:none">
                    <td class="tdblueM"><%=RELATE_PART %></td>
					<td class="tdwhiteL" colspan="3">
						<jsp:include page="/netmarkets/jsp/narae/part/select_include.jsp" flush="true">
							<jsp:param name="formName" value="mainform"/>
							<jsp:param name="mode" value="single"/>
						</jsp:include>
					</td>
                </tr>
                <tr bgcolor="ffffff" height=35 id="partNew" style="display:none">
	                 <td class="tdblueM" ><%=NEW_PART %></td>
	                 <td class="tdwhiteL" colspan="3">
	                 	<table  width="100%" border="0" cellpadding="0" cellspacing="0">
	                 		<!--  tr>
                             	<td class="tdblueM" ><%=SELECT_FOLDER %><span style="color:red;">*</span></td>
                                <td class="tdwhiteL" colspan="3">
                                	folder
                                </td>
                            </tr -->
							<tr bgcolor="ffffff" height=35>
			                	<td class="tdblueM" width="15%">MAKER</td>
			                    <td class="tdwhiteL" width="35%">
			                        <input name="MAKER" id="MAKER" class="txt_field" size="200" value="" style="width:150px;" readOnly/>
			                        <a href="#" onclick="codeSearch('MAKER');"><img src="/Windchill/netmarkets/jsp/narae/portal/images/s_search.gif" border=0></a>
			                    </td>
			                    <td class="tdblueM" width="15%"><%=ORG_938%> <span style="color:red;">*</span></td>
			                    <td class="tdwhiteL" width="35%">
			                        <select name="quantityunit" id="quantityunit" style="width:60%">
									<%
									QuantityUnit[] qa = QuantityUnit.getQuantityUnitSet();
									
									for(int i=0; i< qa.length; i++ ){
									    
										if(qa[i].toString().equals("l") || qa[i].toString().equals("as_needed") 
												|| qa[i].toString().equals("sq_m") || qa[i].toString().equals("cu_m")) continue;
									%>
			                            <option value="<%=qa[i].toString()%>"><%=qa[i].getDisplay(request.getLocale())%></option>
									<%
									}
									%>
			                        </select>
			                    </td>
			                </tr>
			                <tr>
			                    <td class="tdblueM" ><%=STANDARD %></td>
			                    <td class="tdwhiteL">
			                        <input name="SPEC" id="SPEC" class="txt_field" size="200" value="" style="width:150px;" readOnly/>
			                        <a href="#" onclick="codeSearch('SPEC');"><img src="/Windchill/netmarkets/jsp/narae/portal/images/s_search.gif" border=0></a>
			                    </td>
			                    <td class="tdblueM" ><%=EXIST_DRAWING %></td>
			                    <td class="tdwhiteL" colspan="3" >
                                	<select name="isDrawing" id="isDrawing" style="width:60%">
                                		<option value="Y">Y</option>
                                		<option value="N" selected>N</option>
                                	</select>
			                    </td>
			                </tr>
			            </table>
		          	</td>
		        </tr>
	            <!-- Number Select -->
                <tr id="numberSelect">
                	<td colspan="4">
                 	<jsp:include page="/netmarkets/jsp/narae/part/numberSelect.jsp" flush="true">
							<jsp:param name="formName" value="mainform"/>
							<jsp:param name="firstColumnSize" value="15"/>
					</jsp:include>
					</td>
                </tr>
                <tr bgcolor="ffffff" height=35>
                    <td class="tdblueM"><%=MATERIAL %></td>
                    <td class="tdwhiteL">
                        <input name="MATERIAL" id="MATERIAL" class="txt_field" size="85" engnum="engnum" style="width:60%" readOnly/ >
                        <a href="#" onclick="codeSearch('MATERIAL');"><img src="/Windchill/netmarkets/jsp/narae/portal/images/s_search.gif" border=0></a>
                    </td>
                    <td class="tdblueM" ><%=USE_TYPE %></td>
                    <td class="tdwhiteL">
                        <input name="Treatment" id="Treatment" class="txt_field" size="85" engnum="engnum" style="width:60%"/>
                    </td>
                </tr>
                <tr bgcolor="ffffff" height=35>
                    <td class="tdblueM"><%=WEIGHT %></td>
                    <td class="tdwhiteL">
                        <input name="Weight" id="Weight" class="txt_field" size="85" engnum="engnum" style="width:60%" onkeyup="javascript:if(!checkNumberFormat(this.value)) { alert('ì«ìë§ ìë ¥ê°ë¥í©ëë¤.'); this.value=''; }"/>
                    </td>
                    <td class="tdblueM">SHEET</td>
                    <td class="tdwhiteL">
                        <input name="Sheet" id="Sheet" class="txt_field" size="85" engnum="engnum" style="width:60%"/>
                    </td>
                </tr>
                <tr bgcolor="ffffff" height=35>
                    <td class="tdblueM"><%=REF_MODEL %></td>
                    <td class="tdwhiteL">
                        <input name="Ref_Model_no" id="Ref_Model_no" class="txt_field" size="85" engnum="engnum" style="width:60%"/>
                    </td>
                    <td class="tdblueM" ><%=MAKING_DRAWING %></td>
                    <td class="tdwhiteL">
                        <input name="DRW_type" id="DRW_type" class="txt_field" size="85" engnum="engnum" style="width:60%"/>
                    </td>
                </tr>
                
                <tr bgcolor="ffffff" height=35>
                    <td class="tdblueM"><%=DRAWING_2D %> <%=ORG_549%></td>
                    <td class="tdwhiteL" colspan="3">
                        <textarea name="description" id="description" cols="80" rows="2" class="fm_area" style="width:90%"></textarea>
                    </td>
                </tr>
				<tr bgcolor="ffffff" height=35>
					<td class="tdblueM"><%=ORG_244%> <span style="COLOR: red;">*</span></td>
					<td class="tdwhiteL" colspan="3"><input name="primary" id="primary" type="file"  class="txt_field" size="60" border="0" onchange="inputFile(this)"></td>
				</tr>
                <tr bgcolor="ffffff" height=35>
                    <td class="tdblueM"><%=DRAWING_TYPE %> <span style="COLOR: red;">*</span></td>
                    <td class="tdwhiteL" colspan="3">
                        <SELECT name='authoringType' id='authoringType'>
                            <OPTION value=''><%=ORG_576%></OPTION>
                            <OPTION value='ACAD'>AutoCAD</OPTION>
                            <OPTION value='PDF'>PDF</OPTION>
                            <OPTION value='OTHER'>ETC</OPTION>
                        </SELECT>
                    </td>
                </tr>
				<tr bgcolor="ffffff" height=35>
					<td class="tdblueM"><%=SECONDARY_ATTACHMENT %></td>
					<td class="tdwhiteL" colspan="3">
						<jsp:include page="/netmarkets/jsp/narae/portal/attacheFile_include.jsp" flush="true">
							<jsp:param name="form" value="mainform"/>
							<jsp:param name="command" value="insert"/>
							<jsp:param name="type" value="secondary"/>
							<jsp:param name="oid" value=""/>
						</jsp:include>
					</td>
				</tr>
<!--                 <tr height="35" bgcolor="ffffff"> -->
<!-- 					<td class="tdblueM">DRAWING_2D <span style="COLOR: red;">*</span></td> -->
<!-- 					<td class="tdwhiteL" colspan="3"> -->
<%-- 					<jsp:include page="/netmarkets/jsp/narae/drawing/select_include2.jsp" flush="true"> --%>
<%-- 							<jsp:param name="formName" value="mainform"/> --%>
<%-- 							<jsp:param name="viewType" value="view"/> --%>
<%-- 					</jsp:include> --%>
<!-- 					</td> -->
<!-- 				</tr> -->
            </table>
        </td>
    </tr>
    <tr>
        <td align="center" colspan=2>
            <table border="0" cellpadding="0" cellspacing="4" align="center">
                <tr>
                    <td>
                        <a onclick="saveDrawing();" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="80"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center"><%=ORG_890%></div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
                    </td>
                </tr>
            </table>
        </td>

    </tr>

</table>
        </td>
    </tr>
</table>
<input id="buildRoleValue" name="buildRoleValue" type="hidden">
<input id="partOidValue" name="partOidValue" type="hidden">
<input id="makerValue" name="makerValue" type="hidden">
<input id="groupValue" name="groupValue" type="hidden">
<input id="typeValue" name="typeValue" type="hidden">
<input id="unitValue" name="unitValue" type="hidden">
<input id="class1Value" name="class1Value" type="hidden">
<input id="class2Value" name="class2Value" type="hidden">
<input id="class3Value" name="class3Value" type="hidden">
<input id="nameValue" name="nameValue" type="hidden">
<input id="materialValue" name="materialValue" type="hidden">
<input id="treatmentValue" name="treatmentValue" type="hidden">
<input id="weightValue" name="weightValue" type="hidden">
<input id="sheetValue" name="sheetValue" type="hidden">
<input id="refModelNoValue" name="refModelNoValue" type="hidden">
<input id="drwTypeValue" name="drwTypeValue" type="hidden">
<input id="descriptionValue" name="descriptionValue" type="hidden">
<input id="authoringTypeValue" name="authoringTypeValue" type="hidden">
<input id="partFolderValue" name="partFolderValue" type="hidden">
<input id="standard" name="standard" type="hidden">
<iframe id="frmIEAction" name="frmIEAction" style="width:0px;height:0px;visibility:hidden"></iframe>
</form>
<!-- InnoAP upload DIV -->
<div id="rsltDiv" style="display:none;"></div>

<!-- Initialize -->
<script>
	var linkPart1 = document.getElementById('partLink');
	var newPart = document.getElementById('partNew');
	linkPart1.style.display = 'none';
	newPart.style.display = '';
	document.getElementById('partFolder').value = '/<%=containerRef.getName()%>';
</script>

<!-- Function Script -->
<script>
function partCheck1(check){
	
	if(check == "link"){
		document.getElementById("partLink").style.display ='';
		document.getElementById("partNew").style.display ='none';
		codeDisplay(check);
	}else if(check == "new"){
		document.getElementById("partLink").style.display ='none';
		document.getElementById("partNew").style.display ='';
		codeDisplay(check);
	}else {
		document.getElementById("partLink").style.display ='none';
		document.getElementById("partNew").style.display ='none';
		codeDisplay(check);
	}
}

function codeDisplay(arg){
	
	if(arg =="link" ){
		document.getElementById('group1').disabled = 1;
		document.getElementById('type').disabled = 1;
		document.getElementById('unit1').disabled = 1;
		document.getElementById('unit2').disabled = 1;
		document.getElementById('class1').disabled = 1;
		document.getElementById('class2').disabled = 1;
		document.getElementById('class3').disabled = 1;
		document.getElementById('class4').disabled = 1;
		document.getElementById('name1').disabled = 1;
		document.getElementById('name2').disabled = 1;
	}else{
		document.getElementById('group1').disabled = 0;
		document.getElementById('type').disabled = 0;
		document.getElementById('unit1').disabled = 0;
		document.getElementById('unit2').disabled = 0;
		document.getElementById('class1').disabled = 0;
		document.getElementById('class2').disabled = 0;
		document.getElementById('class3').disabled = 0;
		document.getElementById('class4').disabled = 0;
		document.getElementById('name1').disabled = 0;
		document.getElementById('name2').disabled = 0;
	}
	
}

function saveDoc() {
	var mainform = document.getElementById("mainform");
	mainform.submit();
}

function saveDrawing() {
	var buildRole1 = document.getElementsByName("buildRole");
	var buildRole = "";
	if( buildRole1 != null ) {
		for( var index=0; index < buildRole1.length; index++) {
			if( buildRole1[index].checked ) buildRole = buildRole1[index].value;
		}
	}
	document.getElementById("buildRoleValue").value = buildRole;
	//alert("buildRoleValue=" + buildRole);
	
	if ( document.getElementById("authoringType").value == null || document.getElementById("authoringType").value == "" ) {
		alert("<%=WTMessage.getLocalizedMessage(RESOURCE , "NO_SELECT_DRAWING_TYPE", new Object[]{}, locale)%>");
		return;
	}
	if ( buildRole == "new" || buildRole == "none" ) {
		if( document.getElementById("group1").selectedIndex == 0 ) {
			alert("<%=WTMessage.getLocalizedMessage(RESOURCE , "NO_COMPLETE_DRAWING_CODE", new Object[]{}, locale)%>");
			return;
		} else {
			if( document.getElementById("type").selectedIndex == 0 ) {
				alert("<%=WTMessage.getLocalizedMessage(RESOURCE , "NO_COMPLETE_DRAWING_CODE", new Object[]{}, locale)%>");
				return;
			} else {
				var index = document.getElementById("type").selectedIndex;
				if( document.getElementById("type")[index].value.split(',')[1] == 'A') {
					if( document.getElementById("unit1").selectedIndex == 0 ||
						document.getElementById("class1").selectedIndex == 0 ||
						document.getElementById("class2").selectedIndex == 0 ||
						document.getElementById("class4").value == "" ) {
						alert("<%=NO_COMPLETE_DRAWING_CODE%>");
						return;
					}
				} else if ( document.getElementById("type")[index].value.split(',')[1] == 'B') {
					if( document.getElementById("unit1").selectedIndex == 0 ||
						document.getElementById("class1").selectedIndex == 0 ||
						document.getElementById("class2").selectedIndex == 0 ||
						(document.getElementById("name2").value == null || document.getElementById("name2").value.length == 0) ) {
						if( document.getElementById("name2").value == null || document.getElementById("name2").value.length == 0 ) {
							alert("<%=NO_SELECT_DRAWING_TYPE_CLASS3%>");
						} else {
							alert("<%=NO_COMPLETE_DRAWING_CODE%>");
						}
						
						return;
					}
				} else if ( document.getElementById("type")[index].value.split(',')[1] == 'P') {
					if( document.getElementById("unit1").selectedIndex == 0 ||
						document.getElementById("class1").selectedIndex == 0 ||
						document.getElementById("class2").selectedIndex == 0 ||
						(document.getElementById("name2").value == null || document.getElementById("name2").value.length == 0) ) {
						if( document.getElementById("name2").value == null || document.getElementById("name2").value.length == 0 ) {
							alert("<%=NO_SELECT_DRAWING_TYPE_CLASS3%>");
						} else {
							alert("<%=NO_COMPLETE_DRAWING_CODE%>");
						}
						return;
					}
				} else if ( document.getElementById("type")[index].value.split(',')[1] == 'S') {
					if( document.getElementById("unit1").selectedIndex == 0 ||
						document.getElementById("unit2").selectedIndex == 0 ||
						document.getElementById("class1").selectedIndex == 0 ||
						document.getElementById("class3").selectedIndex == 0 ||
						document.getElementById("class3").selectedIndex == 0 ) {
						alert("<%=NO_COMPLETE_DRAWING_CODE%>");
						return;
					}
				}
			}
		}

		if( document.getElementById("primary").value == '' ) {
			alert("<%=WTMessage.getLocalizedMessage(RESOURCE , "NO_COMPLETE_DRAWING_PRIMARY", new Object[]{}, locale)%>");
			return;
		}
	} else {
		// Part Oid
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
		document.getElementById("partOidValue").value = selectedpartOid;
		
		if( document.getElementById("partOidValue").value == '' ) {
			alert("<%=WTMessage.getLocalizedMessage(RESOURCE , "NO_COMPLETE_RELATED_PART", new Object[]{}, locale)%>");
			return;
		}
	}
	
	if (!confirm("<%=WTMessage.getLocalizedMessage(RESOURCE , "ASK_CREATE_DRAWING", new Object[]{}, locale)%>")) return false;
	//selectedFolderFromFolderContext 
	//alert("selectedFolderFromFolderContext=" + document.getElementById("selectedFolderFromFolderContext").value);
	
	//alert("partOidValue=" + selectedpartOid);
	//document.getElementById("MAKER").value;
	//alert("MAKER=" + document.getElementById("MAKER").value);
	document.getElementById("makerValue").value = encodeURIComponent(document.getElementById("MAKER").value);
	document.getElementById("standard").value = encodeURIComponent(document.getElementById("SPEC").value);
	//alert("MAKERVALUE=" + document.getElementById("makerValue").value);
	//alert("quantityunit=" + document.getElementById("quantityunit").value);
	//alert("SPEC=" + document.getElementById("SPEC").value);
	//alert("isDrawing=" + document.getElementById("isDrawing").value);
	//alert("name1=" + document.getElementById("name1").value);
	
	
	var index = document.getElementById("type").selectedIndex;
	if( document.getElementById("type")[index].value.split(',')[1] == 'A') {
		document.getElementById("groupValue").value = encodeURIComponent(document.getElementById("group1")[document.getElementById("group1").selectedIndex].value.split(',')[1]);
		document.getElementById("typeValue").value = encodeURIComponent(document.getElementById("type")[document.getElementById("type").selectedIndex].value.split(',')[1]);
		document.getElementById("unitValue").value = encodeURIComponent(document.getElementById("unit1")[document.getElementById("unit1").selectedIndex].value.split(',')[1]);
		document.getElementById("class1Value").value = encodeURIComponent(document.getElementById("class1")[document.getElementById("class1").selectedIndex].value.split(',')[1]);
		document.getElementById("class2Value").value = encodeURIComponent(document.getElementById("class2")[document.getElementById("class2").selectedIndex].value.split(',')[1]);
		document.getElementById("class3Value").value = encodeURIComponent(document.getElementById("class4").value);
		document.getElementById("nameValue").value = encodeURIComponent(document.getElementById("name1").value);
	} else if( document.getElementById("type")[index].value.split(',')[1] == 'B') {
		document.getElementById("groupValue").value = encodeURIComponent(document.getElementById("group1")[document.getElementById("group1").selectedIndex].value.split(',')[1]);
		document.getElementById("typeValue").value = encodeURIComponent(document.getElementById("type")[document.getElementById("type").selectedIndex].value.split(',')[1]);
		document.getElementById("unitValue").value = encodeURIComponent(document.getElementById("unit1")[document.getElementById("unit1").selectedIndex].value.split(',')[1]);
		document.getElementById("class1Value").value = encodeURIComponent(document.getElementById("class1")[document.getElementById("class1").selectedIndex].value.split(',')[1]);
		document.getElementById("class2Value").value = encodeURIComponent(document.getElementById("class2")[document.getElementById("class2").selectedIndex].value.split(',')[1]);
		document.getElementById("class3Value").value = encodeURIComponent(document.getElementById("name2").value);
		document.getElementById("nameValue").value = encodeURIComponent(document.getElementById("name1").value) +
													 document.getElementById("class3Value").value;
	} else if( document.getElementById("type")[index].value.split(',')[1] == 'P') {
		document.getElementById("groupValue").value = encodeURIComponent(document.getElementById("group1")[document.getElementById("group1").selectedIndex].value.split(',')[1]);
		document.getElementById("typeValue").value = encodeURIComponent(document.getElementById("type")[document.getElementById("type").selectedIndex].value.split(',')[1]);
		document.getElementById("unitValue").value = encodeURIComponent(document.getElementById("unit1")[document.getElementById("unit1").selectedIndex].value.split(',')[1]);
		document.getElementById("class1Value").value = encodeURIComponent(document.getElementById("class1")[document.getElementById("class1").selectedIndex].value.split(',')[1]);
		document.getElementById("class2Value").value = encodeURIComponent(document.getElementById("class2")[document.getElementById("class2").selectedIndex].value.split(',')[1]);
		document.getElementById("class3Value").value = encodeURIComponent(document.getElementById("name2").value);
		document.getElementById("nameValue").value = encodeURIComponent(document.getElementById("name1").value) +
													 document.getElementById("class3Value").value;
	} else if( document.getElementById("type")[index].value.split(',')[1] == 'S') {
		document.getElementById("groupValue").value = encodeURIComponent(document.getElementById("group1")[document.getElementById("group1").selectedIndex].value.split(',')[1]);
		document.getElementById("typeValue").value = encodeURIComponent(document.getElementById("type")[document.getElementById("type").selectedIndex].value.split(',')[1]);
		document.getElementById("unitValue").value = encodeURIComponent(document.getElementById("unit1")[document.getElementById("unit1").selectedIndex].value.split(',')[1]) + 
													 encodeURIComponent(document.getElementById("unit2")[document.getElementById("unit2").selectedIndex].value.split(',')[1]);
		document.getElementById("class1Value").value = encodeURIComponent(document.getElementById("class1")[document.getElementById("class1").selectedIndex].value.split(',')[1]);
		document.getElementById("class2Value").value = encodeURIComponent(document.getElementById("class2")[document.getElementById("class2").selectedIndex].value.split(',')[1]);
		document.getElementById("class3Value").value = encodeURIComponent(document.getElementById("class3")[document.getElementById("class3").selectedIndex].value.split(',')[1]);
		document.getElementById("nameValue").value = encodeURIComponent(document.getElementById("name1").value);
	}
	//alert("Group=" + document.getElementById("groupValue").value);
	//alert("Type=" + document.getElementById("typeValue").value);
	//alert("Unit=" + document.getElementById("unitValue").value);
	//alert("Class1=" + document.getElementById("class1Value").value);
	//alert("Class2=" + document.getElementById("class2Value").value);
	//alert("Class3=" + document.getElementById("class3Value").value);
	//alert("nameValue=" + document.getElementById("nameValue").value);
	
	document.getElementById("materialValue").value = encodeURIComponent(document.getElementById("MATERIAL").value);
	document.getElementById("treatmentValue").value = encodeURIComponent(document.getElementById("Treatment").value);
	document.getElementById("weightValue").value = encodeURIComponent(document.getElementById("Weight").value);
	document.getElementById("sheetValue").value = encodeURIComponent(document.getElementById("Sheet").value);
	document.getElementById("refModelNoValue").value = encodeURIComponent(document.getElementById("Ref_Model_no").value);
	document.getElementById("drwTypeValue").value = encodeURIComponent(document.getElementById("DRW_type").value);
	
	//alert(document.getElementById("treatmentValue").value);
	//alert(document.getElementById("weightValue").value);
	//alert(document.getElementById("sheetValue").value);
	//alert(document.getElementById("refModelNoValue").value);
	//alert(document.getElementById("drwTypeValue").value);
	
	document.getElementById("descriptionValue").value = encodeURIComponent(document.getElementById("description").value);
	//alert("descriptionValue=" + document.getElementById("descriptionValue").value);
	
	document.getElementById("authoringTypeValue").value = document.getElementById("authoringType")[document.getElementById("authoringType").selectedIndex].value;
	//alert("authoringTypeValue="+document.getElementById("authoringTypeValue").value);
	
	document.getElementById("partFolderValue").value = encodeURIComponent(document.getElementById("partFolder").value);
	
	var url="/Windchill/netmarkets/jsp/narae/drawing/action/createDrawingAction.jsp";
	var frm = document.getElementsByName("mainform")[0];
	frm.encoding = "multipart/form-data";
	frm.method = "POST";
	frm.action = url;
	frm.target = "frmIEAction";
	frm.submit();
		
}

function showFinished(saveType) { 
	if(saveType != null && saveType.trim().length != 0 ) {
		alert("<%=WTMessage.getLocalizedMessage(RESOURCE , "COMPLETE_SUBMIT", new Object[]{}, locale)%>!");
		window.location = "/Windchill/app/#ptc1/tcomp/infoPage?oid=OR:" + saveType;
	} else {
		alert("<%=WTMessage.getLocalizedMessage(RESOURCE , "FAILED_CREATE_DRAWING", new Object[]{}, locale)%>!\n");
	}
	
}

</script>

<%-- <%@ include file="/netmarkets/jsp/util/end.jspf"%> --%>