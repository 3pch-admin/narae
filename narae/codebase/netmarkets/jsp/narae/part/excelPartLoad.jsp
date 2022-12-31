<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ page import="wt.util.*, wt.org.*, wt.session.*, wt.inf.container.*, ext.narae.util.*, ext.narae.ui.*, java.util.*" %>
<%
String RESOURCE = "ext.narae.ui.common.resource.NaraeCommonRB";
Locale locale = WTContext.getContext().getLocale();
WTContainerRef partContainerRef = WCUtil.getWTContainerRefForPart();
%>

<SCRIPT LANGUAGE=JavaScript>
function save(action){
	var pForm = document.excelCreateForm;
	
	if(pForm.excelFile.value ==''){
		alert("Please input excel file.");
		return;
	}

	if ( document.getElementById("folderLocation").value == "" ) {
		alert("Please select folder location.");
		return;
	}

	//disabledAllBtn();
	//showProcessing();
	pForm.target = "list";
	pForm.action = "/Windchill/netmarkets/jsp/narae/part/ExcelActionPart.jsp";
	pForm.submit();
	pForm.saveButton.disabled = true;
	
}

function inputFile(FileName){
	var pForm = document.excelCreateForm;

   var lain = FileName.lastIndexOf('.');
	
    if(lain > 0){
    	var excelType = FileName.substring(lain+1);
    	if(excelType != 'xls' && excelType != 'xlsx') {
			pForm.excelFile.value = "";
    		alert("It just needs to input excel file.");
    		
    		return;
    	}
        //document.drawingCreateForm.name.value = fvalue.substring(lain+1);
    }else{
    	alert("It just needs to input excel file.");
    	pForm.excelFile.value = "";
        return;
    }
}

function showProcessing()
{
	var div1 = document.getElementById('div1');
	var div2 = document.getElementById('div2');

	div1.style.left = (document.body.offsetWidth / 2 - 160)+"px";
	div1.style.top = (document.body.offsetHeight / 2 - 100)+"px";
	div1.style.display = "block";

	div2.style.width = div1.offsetWidth;
	div2.style.height = div1.offsetHeight;
	div2.style.top = div1.style.top;
	div2.style.left = div1.style.left;
	div2.style.zIndex = div1.style.zIndex - 1;
	div2.style.display = "block";
}

</SCRIPT>

<input class="jca" name="openerActionMethod" value="execute" type="hidden"/>
<input class="jca" name="openerExecuteLocation" value="inbegin" type="hidden"/>
<input class="jca" name="executeLocation" value="inbegin" type="hidden"/>
<input class="jca" name="popupExecuteLocation" value="inbegin" type="hidden"/>
<input id="validateName_LOCATION" name="validateName_LOCATION" type="hidden"/>

<%@include file="/netmarkets/jsp/narae/portal/ajax/ajaxProgress.html"%>


<form name=excelCreateForm method=post enctype="multipart/form-data">
<input type="hidden" name="cmd"  value="excelLoad"            />
<input type="hidden" name="fid"  value=""            />
<table width="100%" border="0" cellpadding="0" cellspacing="10" valign="top">
	<tr height=5 valign="top">
		<td>
			<table id="creatingDrawing" style="width: 750px;">
				<tr>
					<td class="attributePanel-asterisk" align=left>
						<H2><%=WTMessage.getLocalizedMessage(RESOURCE , "PART_MNG_MULTI_PART", new Object[]{}, locale)%></H2>
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
			            <col width='15%'><col width='35%'><col width='15%'><col width='35%'>
			                 <tr bgcolor="ffffff" height=35>
				                <td class="tdblueM" align=left> <%=WTMessage.getLocalizedMessage(RESOURCE , "SELECT_FOLDER", new Object[]{}, locale)%>:</td>
				                <td class="tdwhiteL" align=left>&nbsp;<input type="text" class="txt_field" name="folderLocation" id="folderLocation" readonly="readonly"  value="" size="35"/>
									<input id="folderLocation___old" name="folderLocation___old" value="/PART" size="25/" type="hidden">
									<a id="newlocation_loc_img" href="javascript:launchFolderPicker ('/Windchill/servlet/WindchillAuthGW/wt.enterprise.URLProcessor/invokeAction?action=cadxBrowseLocations&amp;oid=<%=partContainerRef.getObjectId().getStringValue()%>&amp;containerVisibilityMask=<%=partContainerRef.getObjectId().getStringValue()%>&amp;accessPermission=modify&amp;displayHotlinks=false&amp;displayCreateFolder=true',document.getElementsByName('folderLocation')[0],'selectedFolderFromFolderContext')">
									<img border="0" src="/Windchill/netmarkets/jsp/narae/portal/images/s_search.gif"></img>
									</a>
									<input name="selectedFolderFromFolderContext" value="" type="hidden"><input name="defaultFolderFromRulesEngine" value="" type="hidden"><input name="FormProcessorDelegate" value="com.ptc.windchill.enterprise.folder.LocationPropertyProcessor" type="hidden">
				                <td class="tdblueM" align=right> <%=WTMessage.getLocalizedMessage(RESOURCE , "REGIST_TYPE", new Object[]{}, locale)%><span style="COLOR: red;">*:</span></td>
				                <td class="tdwhiteL" align=left>&nbsp;&nbsp;&nbsp;<input type="radio" name="createType" value="old"><%=WTMessage.getLocalizedMessage(RESOURCE , "OLD_DATA", new Object[]{}, locale)%> <input type="radio" name="createType" value="new" checked><%=WTMessage.getLocalizedMessage(RESOURCE , "NEW_DATA", new Object[]{}, locale)%>
				                </td>
				            </tr>
			                <tr bgcolor="ffffff" height=35>
				                <td class="tdblueM" align=left> <%=WTMessage.getLocalizedMessage(RESOURCE , "EXCEL_UPLOAD", new Object[]{}, locale)%><span style="COLOR: red;">*:</span></td>
				                <td class="tdwhiteL" colspan="3" align=left><input type="file" name="excelFile" class="txt_field" size="60" border="0" onchange="inputFile(this.value)">&nbsp;
				                <input type=button name="saveButton" id="input_2" value='<%=WTMessage.getLocalizedMessage(RESOURCE , "SAVE", new Object[]{}, locale)%>' onclick="javascript:save('/Windchill/netmarkets/jsp/narae/drawing/ActionDrawing.jsp')" style="cursor:hand">&nbsp;
				                </td>
				            </tr>
		           		 </table>
			        </td>
			    </tr>
			</table>
			<table border="0" cellpadding="0" cellspacing="0" width="100%">
                <tr bgcolor="#ffffff">
                    <td>
                        <iframe src="/Windchill/netmarkets/jsp/narae/part/ExcelActionPart.jsp"
                                id="list" name="list" frameborder="0" width="100%" height="310" scrolling="no">
                        </iframe>
                    </td>
                </tr>
            </table>	
        </td>
    </tr>
</table>

</form>