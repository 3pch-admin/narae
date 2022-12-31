<%@page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf" %>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf" %>
<%@ taglib prefix="mvc" uri="http://www.ptc.com/windchill/taglib/jcaMvc"%>
<%@ taglib prefix="wrap" uri="http://www.ptc.com/windchill/taglib/wrappers"%>
<%@ taglib prefix="narae" uri="http://www.ptc.com/windchill/taglib/narae"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="wctags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="attachments" uri="http://www.ptc.com/windchill/taglib/attachments" %>

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


<%@ page import="wt.util.*, wt.org.*, wt.session.*, wt.inf.container.*, ext.narae.util.*, ext.narae.ui.*, java.util.*, ext.narae.service.part.*" %>
<%
String RESOURCE = "ext.narae.ui.common.resource.NaraeCommonRB";
String UNITRB = "ext.narae.ui.common.resource.NareUnitRB";
Locale locale = WTContext.getContext().getLocale();
String M001 = WTMessage.getLocalizedMessage(RESOURCE , "M001", new Object[]{}, WTContext.getContext().getLocale());
WTContainerRef containerRef = WCUtil.getWTContainerRefForDrawing();
WTContainerRef partContainerRef = WCUtil.getWTContainerRefForPart();

String ORG_938 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_938", new Object[]{}, locale);
String ORG_1013 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_1013", new Object[]{}, locale);
String ORG_150 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_150", new Object[]{}, locale);
String ORG_641 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_641", new Object[]{}, locale);
String ORG_743 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_743", new Object[]{}, locale);
String ORG_400 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_400", new Object[]{}, locale);
String ORG_749 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_749", new Object[]{}, locale);
String ORG_890 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_890", new Object[]{}, locale);

String SELECT_FOLDER = WTMessage.getLocalizedMessage(RESOURCE , "SELECT_FOLDER", new Object[]{}, locale);
String STANDARD = WTMessage.getLocalizedMessage(RESOURCE , "STANDARD", new Object[]{}, locale);
String EXIST_DRAWING = WTMessage.getLocalizedMessage(RESOURCE , "EXIST_DRAWING", new Object[]{}, locale);
String DESCRIPTION = WTMessage.getLocalizedMessage(RESOURCE , "DESCRIPTION", new Object[]{}, locale);
%>

<script>
document.title = "<%=WTMessage.getLocalizedMessage(RESOURCE , "PART_MNG_CREATE_PART_TITLE", new Object[]{}, locale)%>";
</script>

<table border="0" cellpadding="0" cellspacing="10" width="100%">
<tbody><tr height="5"><td>

<%@ include file="/netmarkets/jsp/narae/code/code1.jspf"%>

<table id="creatingDrawing" style="width: 800px;">
	<tr>
		<td class="attributePanel-asterisk" align=left>
			<H2><%=WTMessage.getLocalizedMessage(RESOURCE , "PART_MNG_CREATE_PART", new Object[]{}, locale)%></H2>
		</td>
	</tr>
</table>

<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
<form name="partCreateForm" method="post" action="">
<input name="docListOid" id="docListOid" type="hidden">
<input name="partFolderValue" id="partFolderValue" type="hidden">
<input type="hidden" name="wtPartType" id="wtPartType" value="separable">
<input type="hidden" name="source" id="source" value="make">
<input type="hidden" name="view" id="view" value="Design">
<input type="hidden" name="lifecycle" id="lifecycle"  value="Narae_LC">
<input class="jca" name="openerActionMethod" value="execute" type="hidden">
<input class="jca" name="openerExecuteLocation" value="inbegin" type="hidden">
<input class="jca" name="executeLocation" value="inbegin" type="hidden">
<input class="jca" name="popupExecuteLocation" value="inbegin" type="hidden">
<input id="validateName_LOCATION" name="validateName_LOCATION" type="hidden">

    <table width="100%" border="0" cellpadding="0" cellspacing="0" > <!--//여백 테이블-->
        <tr>
            <td>
                <table width="100%" border="0" cellpadding="0" cellspacing="3">
                    <tr align="center">
                        <td valign="top" style="padding:0px 0px 0px 0px">
                            <table width="100%" border="0" cellpadding="1" cellspacing="1" bgcolor="#9CAEC8" align="center">
                                <tr>
                                    <td height="1" width="100%"></td>
                                </tr>
                            </table>
                            <table width="100%" border="0" cellpadding="0" cellspacing="0" align="center"
                                   style="border-left-width: 1px;border-left-style: solid;border-left-color: #e6e6e6;">
                                <tr>
					            	<td width="15%"></td>
					            	<td width="35%"></td>
					            	<td width="15%"></td>
					            	<td width="35%"></td>
					            </tr>
                                <tr>
                                    <td class="tdblueM"><%=SELECT_FOLDER %> <span style="color:red;">*</span></td>
                                    <td class="tdwhiteL" colspan="3">
                                        <input class="txt_field" type="text" value="" id="partFolder" name="partFolder" size=60 readOnly></input>
										<input id="LocationPicker___old" name="partFolder___old" value="/PART" size="25/" type="hidden">
										<a id="newlocation_loc_img" href="javascript:launchFolderPicker ('/Windchill/servlet/WindchillAuthGW/wt.enterprise.URLProcessor/invokeAction?action=cadxBrowseLocations&amp;oid=<%=partContainerRef.getObjectId().getStringValue()%>&amp;containerVisibilityMask=<%=partContainerRef.getObjectId().getStringValue()%>&amp;accessPermission=modify&amp;displayHotlinks=false&amp;displayCreateFolder=true',document.getElementsByName('partFolder')[0],'selectedFolderFromFolderContext')">
										<img border="0" src="/Windchill/netmarkets/jsp/narae/portal/images/s_search.gif"></img>
										</a>
										<input name="selectedFolderFromFolderContext" value="" type="hidden"><input name="defaultFolderFromRulesEngine" value="" type="hidden"><input name="FormProcessorDelegate" value="com.ptc.windchill.enterprise.folder.LocationPropertyProcessor" type="hidden"> 
                                    </td>
                                </tr>
                                <!-- Number Select -->
                                <tr>
                                	<td colspan="4">
	                                	<jsp:include page="/netmarkets/jsp/narae/part/numberSelect.jsp" flush="true">
												<jsp:param name="formName" value="partCreateForm"/>
												<jsp:param name="firstColumnSize" value="15"/>
												<jsp:param name="nameType" value="part"/>
										</jsp:include>
									</td>
                                </tr>
                                <tr>
                                	<td class="tdblueM">MAKER</td>
                                    <td class="tdwhiteL">
                                        <input class="txt_field" type="text" value="" name="MAKER" id="MAKER" size=30 readonly></input>
                                        <input type="hidden" name="maker1" id="maker1"></input>
										<a onclick="codeSearch('MAKER');">
											<img border="0" src="/Windchill/netmarkets/jsp/narae/portal/images/s_search.gif"></img>
										</a>
                                    </td>
                                    <td class="tdblueM"><%=ORG_938%> <span style="color:red;">*</span></td>
                                    <td class="tdwhiteL">
                                        <select name="quantityunit">
                                       
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
                                    <td class="tdblueM"><%=STANDARD %> <span style="color:red;">*</span></td>
                                    <td class="tdwhiteL" >
                                        <input class="txt_field" type="text" value="" id="SPEC" name="SPEC" class="standard" size=35 engnum="engnum" style="width:60%" readOnly></input>
										<input type="hidden" name="standard" id="standard"></input>
										<a onclick="codeSearch('SPEC');">
											<img border="0" src="/Windchill/netmarkets/jsp/narae/portal/images/s_search.gif"></img></a>
                                    </td>
                                    <td class="tdblueM"><%=EXIST_DRAWING %></td>
                                    <td class="tdwhiteL">
                                    	<select name="isDrawing">
                                    		<option value="Y">Y</option>
                                    		<option value="N" selected>N</option>
                                    	</select>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="tdblueM"><%=DESCRIPTION %></td>
                                    <td class="tdwhiteL" colspan="3" >
                                        <textarea name="description" id="description" cols="80" rows="5" class="fm_area" style="width:100%"></textarea>
                                        <textarea name="partdescription" id="partdescription" style="visibility:hidden;height:0px"></textarea>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="tdblueM"><%=ORG_1013%></td>
                                    <td class="tdwhiteL" colspan="3">
                                        <table id="innerTempTable" style="display:none">
                                            <tr>
                                                <td class="tdwhiteM"></td>
                                                <td class="tdwhiteM"></td>
                                                <td class="tdwhiteM"></td>
                                                <td class="tdwhiteM"></td>
                                            </tr>
                                        </table>
                                        <table width="100%" border="0" cellpadding="0" cellspacing="0">
                                            <tr>
                                                <td align="left">
                                                    <table border="0" cellpadding="0" cellspacing="2">
                                                        <tr>
                                                            <td>
                                                            <a onclick="selectDoc();" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="80"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center"><%=ORG_150%></div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
                                                            </td>
                                                            <td>
                                                            <a onclick="deleteDoc();" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="80"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center"><%=ORG_641%></div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
                                                            </td>
                                                        </tr>
                                                    </table>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td>
                                                    <div style="width:99%;overflow-x:hidden;overflow-y:auto;border:1px;border-style:solid;border-color:#5F9EA0;padding:0px;margin:1px 1px 5px;">
                                                        <table width="100%" cellspacing="0" cellpadding="1" border="0" id="docTable" align="center">
                                                            <tbody>
                                                                <tr>
                                                                <td class="tdblueM"  width="4%" ></td>
                                                                    <td class="tdblueM"  width="40%"><%=ORG_743%></td>
                                                                    <td class="tdblueM"  width="50%"><%=ORG_400%></td>
                                                                    <td class="tdblueM0" width="15%"><%=ORG_749%></td>
                                                                </tr>
                                                            </tbody>
                                                        </table>
                                                    </div>
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                    <tr>
                        <td align="center" colspan="2">
                            <table border="0" cellpadding="0" cellspacing="4" align="center">
                                <tr>
                                    <td>
                                    <a onclick="savePart();" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="80"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center"><%=ORG_890%></div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</form>

	</td></tr>
</tbody></table>
<!-- Initialize -->
<script>
	document.getElementById('partFolder').value = '/<%=partContainerRef.getName()%>';
</script>

<iframe id="frmIEAction" name="frmIEAction" style="width:0px;height:0px;visibility:hidden"></iframe>

<script>
function selectDoc() {
//     var url = "/Windchill/netmarkets/jsp/narae/document/documentPicker.jsp?module=document&mode=selectDoc";
//     var attache = window.showModalDialog(url,window,"help=no; scroll=yes; resizable=yes; dialogWidth=800px; dialogHeight:650px; center:yes");

//     if(typeof attache == "undefined" || attache == null) return;

//     addDoc(attache);
    
	var url = "/Windchill/netmarkets/jsp/narae/document/documentPicker.jsp?module=document&mode=selectDoc";
	var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=no,";
    var popWidth = 1000;
    var popHeight = 600;
    var leftpos = (screen.width - popWidth)/ 2;
    var toppos = (screen.height - popHeight) / 2 ;
    var rest = "width="+popWidth+",height="+popHeight+",left=" + leftpos + ',top=' + toppos;
    
    var newwin = window.open(url, "selectDoc", opts+rest);
    newwin.focus();
    
//     addDoc(newwin);
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
        docOid = subarr[0].trim();//
        docNumber = subarr[1];//
        docName = subarr[2];//
        docVersion = subarr[3];//
        
        
        // 중복체크
        if(pForm['docOid']) {
            if(pForm['docOid'].length) {
                for(var j = 0; j < pForm['docOid'].length; j++) {
                    if(pForm['docOid'][j].value == docOid) docCheck = false;
                }
            } else {
                if(pForm['docOid'].value == docOid) docCheck = false;
            }
        }

        if(docCheck) {
        	var userRow1 = docTable.children[0].appendChild(innerTempTable.rows[0].cloneNode(true));
            onecell1 = userRow1.childNodes[1];
            setHtml(onecell1 , "<input type=\"checkbox\" name=\"DocDelete\"><input type=\"hidden\" name=\"docOid\" value=\""+docOid+"\">");
            onecell2 = userRow1.childNodes[3];
            setHtml(onecell2 , docNumber);
            console.log(docNumber);
            onecell3 = userRow1.childNodes[5];
            setHtml(onecell3 , "<nobr>"+docName+"</nobr>");
            onecell4 = userRow1.childNodes[7];
            setHtml(onecell4 , docVersion);
        }
    }
}

function setHtml(tt, data) {
	tt.innerHTML = data;
}

function deleteDoc() {
    var pForm = document.mainform;

    if(pForm['DocDelete'] && pForm['DocDelete'].length) {
        for(var i=pForm['DocDelete'].length-1; i>=0; i--) {
            if(pForm['DocDelete'][i].checked) docTable.deleteRow(i+1);
        }
    }else {
        if(pForm['DocDelete'].checked) docTable.deleteRow(1);
    }
}

function savePart() {
	if ( document.getElementById("partFolder").value == null || document.getElementById("partFolder").value == "" ) {
		alert("<%=WTMessage.getLocalizedMessage(RESOURCE , "DIDNT_SELECT_FOLDER", new Object[]{}, locale)%>");
		return;
	}

	if( document.getElementById("group1").selectedIndex == 0 ) {
		alert("<%=WTMessage.getLocalizedMessage(RESOURCE , "DIDNT_COMPLETE_NUMBER_CODE", new Object[]{}, locale)%>");
		return;
	} else {
		if( document.getElementById("type").selectedIndex == 0 ) {
			alert("<%=WTMessage.getLocalizedMessage(RESOURCE , "DIDNT_COMPLETE_NUMBER_CODE", new Object[]{}, locale)%>");
			return;
		} else {
			var index = document.getElementById("type").selectedIndex;
			if( document.getElementById("type")[index].value.split(',')[1] == 'A') {
				if( document.getElementById("unit1").selectedIndex == 0 ||
					document.getElementById("class1").selectedIndex == 0 ||
					document.getElementById("class2").selectedIndex == 0 ||
					document.getElementById("class4").value == "" ) {
					alert("<%=WTMessage.getLocalizedMessage(RESOURCE , "DIDNT_COMPLETE_NUMBER_CODE", new Object[]{}, locale)%>");
					return;
				}
			} else if ( document.getElementById("type")[index].value.split(',')[1] == 'B') {
				if( document.getElementById("unit1").selectedIndex == 0 ||
					document.getElementById("class1").selectedIndex == 0 ||
					document.getElementById("class2").selectedIndex == 0 ) {
					alert("<%=WTMessage.getLocalizedMessage(RESOURCE , "DIDNT_COMPLETE_NUMBER_CODE", new Object[]{}, locale)%>");
					return;
				}
			} else if ( document.getElementById("type")[index].value.split(',')[1] == 'P') {
				if( document.getElementById("unit1").selectedIndex == 0 ||
					document.getElementById("class1").selectedIndex == 0 ||
					document.getElementById("class2").selectedIndex == 0 ) {
					alert("<%=WTMessage.getLocalizedMessage(RESOURCE , "DIDNT_COMPLETE_NUMBER_CODE", new Object[]{}, locale)%>");
					return;
				}
			} else if ( document.getElementById("type")[index].value.split(',')[1] == 'S') {
				if( document.getElementById("unit1").selectedIndex == 0 ||
					document.getElementById("unit2").selectedIndex == 0 ||
					document.getElementById("class1").selectedIndex == 0 ||
					document.getElementById("class3").selectedIndex == 0 ||
					document.getElementById("class3").selectedIndex == 0 ) {
					alert("<%=WTMessage.getLocalizedMessage(RESOURCE , "DIDNT_COMPLETE_NUMBER_CODE", new Object[]{}, locale)%>");
					return;
				}
			}
		}
	}

	if ( document.getElementById("SPEC").value == null || document.getElementById("SPEC").value == "" ) {
		alert("<%=WTMessage.getLocalizedMessage(RESOURCE , "DIDNT_INPUT_SPEC", new Object[]{}, locale)%>");
		return;
	}
		
	if (!confirm("<%=WTMessage.getLocalizedMessage(RESOURCE , "ASK_CREATE_PART", new Object[]{}, locale)%>")) return;		
	
	
	var partFolder = encodeURIComponent(document.getElementById("partFolder").value);
	document.getElementById("partFolderValue").value = encodeURIComponent(partFolder);
	var number = encodeURIComponent(document.getElementById("number").value);
	// Get group value
	var selectedClass = document.getElementById("type");
	var selectedClassValue = selectedClass[selectedClass.selectedIndex].value;
	var selectedType = selectedClassValue.split(",")[1];
	var nameTemp = null;
	// Check type of name style
	if( selectedType == "A" ) {
		nameTemp = document.getElementById("name1").value;
	} else if( selectedType == "B" ) {
		nameTemp = document.getElementById("name1").value + document.getElementById("name2").value;
	} else if( selectedType == "P" ) {
		nameTemp = document.getElementById("name1").value + document.getElementById("name2").value;
	} else if ( selectedType == "S" ) {
		nameTemp = document.getElementById("name1").value;
	}
	document.getElementById("name").value = encodeURIComponent(nameTemp);
	document.getElementById("maker1").value = encodeURIComponent(document.getElementById("MAKER").value);
	//var quantity = document.getElementById("quantityunit");
	//var quantityunit= encodeURIComponent(quantity[quantity.selectedIndex].value);
	document.getElementById("standard").value =  encodeURIComponent(document.getElementById("SPEC").value);
	//var exist = document.getElementById("isDrawing");
	//var isDrawing= encodeURIComponent(exist[exist.selectedIndex].value);
	document.getElementById("partdescription").value = encodeURIComponent(document.getElementById("description").value);
	
	var partOid = document.getElementsByName("docOid");
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
	
	document.getElementById("docListOid").value = selectedpartOid;
	
	// Number Generation Value
	//var group1 = document.getElementById("group1");
	//formData.append("group1",encodeURIComponent(group1[group1.selectedIndex].value));
	//var type = document.getElementById("type");
	//formData.append("type",encodeURIComponent(type[type.selectedIndex].value));
	//var unit1 = document.getElementById("unit1");
	//formData.append("unit1",encodeURIComponent(unit1[unit1.selectedIndex].value));
	//var unit2 = document.getElementById("unit2");
	//formData.append("unit2",encodeURIComponent(unit2[unit2.selectedIndex].value));
	//var class1 = document.getElementById("class1");
	//formData.append("class1",encodeURIComponent(class1[class1.selectedIndex].value));
	//var class2 = document.getElementById("class2");
	//formData.append("class2",encodeURIComponent(class2[class2.selectedIndex].value));
	//var class3 = document.getElementById("class3");
	//formData.append("class3",encodeURIComponent(class3[class3.selectedIndex].value));
	//formData.append("class4",encodeURIComponent(document.getElementById("class4").value));

	
	// Hidden Values
	//formData.append("wtPartType",encodeURIComponent(document.getElementById("wtPartType").value));
	//formData.append("source",encodeURIComponent(document.getElementById("source").value));
	//formData.append("view",encodeURIComponent(document.getElementById("view").value));
	//formData.append("lifecycle",encodeURIComponent(document.getElementById("lifecycle").value));
	
	
	var url="/Windchill/netmarkets/jsp/narae/part/action/createPartAction.jsp";
	var frm = document.getElementsByName("mainform")[0];
	frm.encoding = "multipart/form-data";
	frm.method = "POST";
	frm.action = url;
	frm.target = "frmIEAction";
	frm.submit();
		
}

function showFinished(saveType, oid) {
	if( oid != null && oid.trim() != "" ) {
		alert("<%=WTMessage.getLocalizedMessage(RESOURCE , "COMPLETE_SUBMIT", new Object[]{}, locale)%>!");
		window.location = "/Windchill/app/#ptc1/tcomp/infoPage?oid=OR:" + oid;
	} else {
		alert("<%=WTMessage.getLocalizedMessage(RESOURCE , "FAILED_CREATE_PART", new Object[]{}, locale)%>!");
	}
	
	
}
</script>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>
