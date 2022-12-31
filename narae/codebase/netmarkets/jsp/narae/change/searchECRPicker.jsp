<%@page import="java.util.Locale"%>
<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ taglib prefix="mvc" uri="http://www.ptc.com/windchill/taglib/jcaMvc"%>
<%@ taglib prefix="wrap" uri="http://www.ptc.com/windchill/taglib/wrappers"%>
<%@ taglib prefix="narae" uri="http://www.ptc.com/windchill/taglib/narae"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="wctags"%>

<%@ page import="wt.util.*, wt.org.*, wt.session.*, wt.inf.container.*, ext.narae.util.*, ext.narae.ui.*" %>

<%@ include file="/netmarkets/jsp/narae/code/code.jspf"%>

<%
String RESOURCE = "ext.narae.ui.common.resource.NaraeCommonRB";
String M001 = WTMessage.getLocalizedMessage(RESOURCE , "M001", new Object[]{}, WTContext.getContext().getLocale());
WTContainerRef containerRef = WCUtil.getWTContainerRefForDrawing();
Locale locale = WTContext.getContext().getLocale();
%>
<script type="text/javascript">
	if(self.name != 'reload'){
		self.name = 'reload';
		self.location.reload(true);
	}
	else self.name='';
</script>
<input type="hidden" name="null___containerOid___textbox" id="containerOid" maxlength="30" size="10" value="<%=containerRef.getObjectId().getStringValue()%>"/>

<table id="creatingDrawing" style="width: 800px;">
	<tr>
		<td class="attributePanel-asterisk" align=left>
			<H2><%=WTMessage.getLocalizedMessage(RESOURCE , "CHANGE_MNG_SEARCH_SCR", new Object[]{}, locale)%></H2>
		</td>

	</tr>
</table>

<fieldset class="x-fieldset x-form-label-left" id="Visualization_and_Attributes" style="width: 96%;">

    <table border="0">
	<tr>
		<td scope="row" class="attributePanel-label" align="right"><%=WTMessage.getLocalizedMessage(RESOURCE , "ECR_NUMBER", new Object[]{}, locale)%>:</td>
	    <td class="attributePanel-value" align="left">&nbsp;
	        <input class="txt_field" type="text" value="" name="number" id="number" size=30></input>
	    </td>
	    <td scope="row" class="attributePanel-label" align="right" width="100"><%=WTMessage.getLocalizedMessage(RESOURCE , "ECR_NAME", new Object[]{}, locale)%>:</td>
	    <td class="attributePanel-value" align="left">&nbsp;
	        <input class="txt_field" type="text" value="" name="name" id="name" size=30></input>
	    </td>
	</tr>
	<tr>
		<td scope="row" class="attributePanel-label" align="right" width="100"><%=WTMessage.getLocalizedMessage(RESOURCE , "ECR_PROJECT", new Object[]{}, locale)%>:</td>
	    <td class="attributePanel-value" align="left">&nbsp;
	        <input name="prjName" id="prjName" style="width:200px;" type="text">  <a onclick="projectSearch();">
			<img src="/Windchill/netmarkets/jsp/narae/portal/images/s_search.gif" border="0"></a>
            <input name="prjNo" id="prjNo" type="hidden"> <input name="prjSeqNo" id="prjSeqNo" type="hidden">
			<input name="unitCode" id="unitCode" type="hidden">
	    </td>
		<td scope="row" class="attributePanel-label" align="right"><%=WTMessage.getLocalizedMessage(RESOURCE , "ECR_REQUESTOR", new Object[]{}, locale)%>:</td>
	    <td class="attributePanel-value" align="left">&nbsp;
	        <input class="txt_field" type="text" value="" name="requestor" id="requestor" size=30></input>
	    </td>
	    
	</tr>
	
	<tr>
  	    <td colspan="4" scope="row" class="attributePanel-label" align="right">
	        <wrap:button name="search" value='<%=WTMessage.getLocalizedMessage(RESOURCE , "SEARCH", new Object[]{}, locale)%>' onclick="submitDocSearch();" />
			<wrap:button name="Init" value='<%=WTMessage.getLocalizedMessage(RESOURCE , "INITIALIZE", new Object[]{}, locale)%>' onclick="initialize();" />
			<wrap:button name="Init" value='<%=WTMessage.getLocalizedMessage(RESOURCE , "SELECT", new Object[]{}, locale)%>' onclick="selectObject();" />
			<wrap:button name="Init" value='<%=WTMessage.getLocalizedMessage(RESOURCE , "CLOSE", new Object[]{}, locale)%>' onclick="cancelWindow();" />
	    </td>
	</tr>
</table>
</fieldset>
<input id="first" name="first" type="hidden" value="true">
<input id="page" name="page" type="hidden" value="picker">

<mvc:tableContainer compId="ext.narae.change.ECRList" height="500"></mvc:tableContainer>

<script>

	function submitDocSearch(){
		var name = document.getElementById('name').value;
		var number = document.getElementById('number').value;
		var project = document.getElementById('prjName').value;
		var requestor = document.getElementById('requestor').value;
		
		if( name == "" && number == "" && project == "" && requestor == "" ) {
			alert("<%=M001%>");
		} else {
			var params = {
					name : name,
					number : number,
					project : project,
					requestor : requestor,
					first : ""
			};

			PTC.jca.table.Utils.reload('ext.narae.change.ECRList', params, true);
		}
	}

	function initialize(){
		document.getElementById('name').value = "";
		document.getElementById('number').value = "";
		document.getElementById('prjName').value = "";
		document.getElementById('prjNo').value = "";
		document.getElementById('prjSeqNo').value = "";
		document.getElementById('unitCode').value = "";
		document.getElementById('requestor').value = "";
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
// 					window.returnValue= returnArray;
					opener.addEcr(returnArray);
					window.self.close();
				} else {
// 					window.returnValue= returnArray;
					opener.addEcr(returnArray);
					window.self.close();
				}
			}
		}
		
		xmlHttp1.open("GET","/Windchill/netmarkets/jsp/narae/change/action/generateECRList.jsp?parentOid=" +selectedPartList,true);

		xmlHttp1.setRequestHeader("If-Modified-Since","0");
		xmlHttp1.send(null);
	}
	
	function cancelWindow() {
		self.close();
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
	
</script>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>