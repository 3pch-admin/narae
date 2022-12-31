<%@page import="ext.narae.util.WCUtil"%>
<%@page import="java.util.Locale"%>
<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ taglib prefix="mvc" uri="http://www.ptc.com/windchill/taglib/jcaMvc"%>
<%@ taglib prefix="wrap" uri="http://www.ptc.com/windchill/taglib/wrappers"%>
<%@ taglib prefix="narae" uri="http://www.ptc.com/windchill/taglib/narae"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="wctags"%>

<%@ page import="wt.util.*, wt.org.*, wt.session.*, wt.inf.container.*,  ext.narae.ui.*" %>

<%@ include file="/netmarkets/jsp/narae/code/code.jspf"%>

<%
String RESOURCE = "ext.narae.ui.common.resource.NaraeCommonRB";
String M001 = WTMessage.getLocalizedMessage(RESOURCE , "M001", new Object[]{}, WTContext.getContext().getLocale());
WTContainerRef containerRef = WCUtil.getWTContainerRefForDrawing();
Locale locale = WTContext.getContext().getLocale();

String viewType = request.getParameter("viewType");
if(viewType == null) viewType = "";
String mode = request.getParameter("mode");
if(mode == null) mode = "";
String tableId = "ext.narae.part.PartList";
if( mode.trim().equals("single") ) {
	tableId = "ext.narae.part.PartList.SingleSelect";
}
%>
<script type="text/javascript">	
	if(self.name != 'reload'){
		self.name = 'reload';
		self.location.reload(true);
	}
	else self.name='';
</script>
<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
<input type="hidden" name="null___containerOid___textbox" id="containerOid" maxlength="30" size="10" value="<%=containerRef.getObjectId().getStringValue()%>"/>

<table id="creatingDrawing" style="width: 800px;">
	<tr>
		<td class="attributePanel-asterisk" align=left>
			<H2><%=WTMessage.getLocalizedMessage(RESOURCE , "PART_MNG_SEARCH_PART", new Object[]{}, locale)%></H2>
		</td>

	</tr>
</table>

<fieldset class="x-fieldset x-form-label-left" id="Visualization_and_Attributes" style="width: 96%;">

    <table border="0">
	<tr>
		<td scope="row" class="attributePanel-label" align="right" width="70px">&nbsp;&nbsp;&nbsp;&nbsp;<%=WTMessage.getLocalizedMessage(RESOURCE , "PART_TYPE", new Object[]{}, locale)%>:</td>
	    <td class="attributePanel-value" align="left" >&nbsp;&nbsp;Part
	    </td>
		<td scope="row" class="attributePanel-label" align="right" width="100"><%=WTMessage.getLocalizedMessage(RESOURCE , "STATUS", new Object[]{}, locale)%>:</td>
	    <td class="attributePanel-value" align="left">&nbsp;
	    	<% if(viewType != null & viewType.equals("eco")) {%>
	    	<%=WTMessage.getLocalizedMessage(RESOURCE , "WORKIN", new Object[]{}, locale)%>,<%=WTMessage.getLocalizedMessage(RESOURCE , "RETURN", new Object[]{}, locale)%>
	    	<input name="state" id="state" type="hidden" value="ECO_PICKER_SEARCH">
	    	<%} else if(viewType != null & viewType.equals("ecr")) {%>
	    	<%=WTMessage.getLocalizedMessage(RESOURCE , "RELEASED", new Object[]{}, locale)%>,<%=WTMessage.getLocalizedMessage(RESOURCE , "SENT_ERP", new Object[]{}, locale)%>,<%=WTMessage.getLocalizedMessage(RESOURCE , "PUR_ORDER_APPROVAL", new Object[]{}, locale)%>
	    	<input name="state" id="state" type="hidden" value="ECR_PICKER_SEARCH">
	    	<%} else { %>
	        <SELECT id="state" name="state">
				<OPTION value=""><%=WTMessage.getLocalizedMessage(RESOURCE , "ALL", new Object[]{}, locale)%></OPTION>
				<OPTION value="INWORK"><%=WTMessage.getLocalizedMessage(RESOURCE , "WORKIN", new Object[]{}, locale)%></OPTION>
				<OPTION value="RELEASED"><%=WTMessage.getLocalizedMessage(RESOURCE , "RELEASED", new Object[]{}, locale)%></OPTION>
			</SELECT>
			<%} %>
	    </td>
	</tr>
	<tr>
		<td scope="row" class="attributePanel-label" align="right"><%=WTMessage.getLocalizedMessage(RESOURCE , "PART_NUMBER", new Object[]{}, locale)%>:</td>
	    <td class="attributePanel-value" align="left">&nbsp;
	        <input class="txt_field" type="text" value="" name="number" id="number" size=40></input>
	    </td>
	    <td scope="row" class="attributePanel-label" align="right" width="100"><%=WTMessage.getLocalizedMessage(RESOURCE , "PART_NAME", new Object[]{}, locale)%>:</td>
	    <td class="attributePanel-value" align="left">&nbsp;
	        <input class="txt_field" type="text" value="" name="name" id="name" size=40></input>
	    </td>
	</tr>
	<tr>
		<td scope="row" class="attributePanel-label" align="right"><%=WTMessage.getLocalizedMessage(RESOURCE , "STANDARD", new Object[]{}, locale)%>:</td>
	    <td class="attributePanel-value" align="left">&nbsp;
	        <input class="txt_field" type="text" value="" name="spec" id="spec" size=40></input>
	    </td>
	    <td scope="row" class="attributePanel-label" align="right" width="100"><%=WTMessage.getLocalizedMessage(RESOURCE , "MAKER", new Object[]{}, locale)%>:</td>
	    <td class="attributePanel-value" align="left">&nbsp;
	        <input class="txt_field" type="text" value="" id="MAKER" name="MAKER" class="MAKER" size=35 engnum="engnum" style="width:60%"></input>
			<a onclick="codeSearch('MAKER');">
				<img border="0" src="/Windchill/netmarkets/jsp/narae/portal/images/s_search.gif"></img>
	    </td>
	</tr>
	<tr>
		<td scope="row" class="attributePanel-label" align="right" width="40"><%=WTMessage.getLocalizedMessage(RESOURCE , "REGISTER_DATE_LONG", new Object[]{}, locale)%>:</td>
	    <td class="attributePanel-value" align="left">&nbsp;
	        <wrap:dateInputComponent id="startdate" name="startdate" required="false" dateValueType="DATE_ONLY" label=""/> ~ 
			<wrap:dateInputComponent id="enddate" name="finishdate" required="false" dateValueType="DATE_ONLY"/>
			<script>
			document.getElementById('startdate').value = "";
			document.getElementById('enddate').value = "";
			</script>
	    </td>
	    <td scope="row" class="attributePanel-label" align="right"><%=WTMessage.getLocalizedMessage(RESOURCE , "CREATOR", new Object[]{}, locale)%>:</td>
		<%
		WTUser user = (WTUser)SessionHelper.manager.getPrincipal();
		%>
	    <td class="tabledatafont" align="left">&nbsp;
	        <!--wrap:textBox name="creator" id="creator" maxlength="30" size="30" value='<%=user.getAuthenticationName()%>' /-->
			<wrap:suggestTextBox name="creator" id="creator" serviceKey="userSuggest" maxlength="30" size="27" minChars="2"/>
	    </td>
	</tr>
	
	<tr>
		<td scope="row" class="attributePanel-label" align="right"><%=WTMessage.getLocalizedMessage(RESOURCE , "VERSION", new Object[]{}, locale)%>:</td>
	    <td class="attributePanel-value" align="left">&nbsp;
	        <wrap:radioButton label='<%=WTMessage.getLocalizedMessage(RESOURCE , "LATEST_VERSION", new Object[]{}, locale)%>' value="true" name="version" id="version" checked="true"/>&nbsp;&nbsp; 
			<wrap:radioButton label='<%=WTMessage.getLocalizedMessage(RESOURCE , "ALL_VERSION", new Object[]{}, locale)%>' value="false" name="version" id="version" checked="false"/>
	    </td>
	    <td scope="row" class="attributePanel-label" align="right"><%=WTMessage.getLocalizedMessage(RESOURCE , "PART_DESC", new Object[]{}, locale)%>:</td>
	    <td class="tabledatafont" align="left">&nbsp;
	        <input class="txt_field" type="text" value="" name="desc" id="desc" size=40></input>
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

<mvc:tableContainer compId="<%=tableId %>" height="500"></mvc:tableContainer>

<script>

	function submitDocSearch(){
		var state = document.getElementById('state').value;
		var name = document.getElementById('name').value;
		var number = document.getElementById('number').value;
		var version = document.getElementById('version').checked;
		var spec = document.getElementById('spec').value;
		var maker = document.getElementById('MAKER').value;
		var startdate = document.getElementById('startdate').value;
		var enddate = document.getElementById('enddate').value;
		var creator = document.getElementById('creator').value;
		var desc = document.getElementById('desc').value;
		
		console.log(state);
		console.log(name);
		console.log(number);
		console.log(version);
		console.log(spec);
		console.log(maker);
		console.log(startdate);
		console.log(enddate);
		console.log(creator);
		console.log(desc);
		
// 		if( name == "" && number == "" && version == "" && spec == "" && state == "" &&
// 			maker == "" && creator == "" && startdate == "" && enddate == "" && desc == "" ) {
<%-- 			alert("<%=M001%>"); --%>
// 		} 
if( name == "" && number == "" &&  spec == "" && maker == "" && creator == "" && startdate == "" && enddate == "" && desc == "" ) {
		alert("<%=M001%>");
	} else {
			var params = {
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
	
			
			PTC.jca.table.Utils.reload('<%=tableId %>', params, true);
		}
	}

	function initialize(){
		document.getElementById('state').value = "";
		document.getElementById('name').value = "";
		document.getElementById('number').value = "";
		document.getElementById('version').value = "true";
		document.getElementById('spec').value = "";
		document.getElementById('MAKER').value = "";	
		document.getElementById('startdate').value = "";
		document.getElementById('enddate').value = "";
		document.getElementById('creator').value = "";
		document.getElementById('desc').value = "";
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
			if( i == 0 ) {
				selectedPartList = selectedParts[i].value.split('comp$ext$$')[1].split('!*')[0]
			}
			
			else {
				selectedPartList = selectedPartList + '$$$PTC$$$' + selectedParts[i].value.split('comp$ext$$')[1].split('!*')[0];
			}
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
				console.log("reValue = " + reValue);
				var reValueArray = reValue.split("$$$PTC$$$");
				console.log("reValueArray = " + reValueArray);
				var returnArray = new Array(reValueArray.length);
				for( var index =0 ; index < reValueArray.length; index++) {
					var itemArray = reValueArray[index].split("$$$item$$$");
					console.log("itemArray = " + itemArray);
					returnArray[index] = itemArray;
				}

				if (navigator.appVersion.indexOf("MSIE 7.0") >= 0 || navigator.appVersion.indexOf("MSIE 8.0") >= 0 || navigator.appVersion.indexOf("MSIE 9.0")) {
// 					window.returnValue= returnArray;
					opener.addPart(returnArray);
					window.self.close();

				} else {
// 					window.returnValue= returnArray;
					opener.addPart(returnArray);
					window.self.close();
				}
			}
		}
		
		xmlHttp1.open("GET","/Windchill/netmarkets/jsp/narae/part/action/generatePartList.jsp?parentOid=" +selectedPartList,true);

		xmlHttp1.setRequestHeader("If-Modified-Since","0");
		xmlHttp1.send(null);

		 
	}
	
</script>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>
