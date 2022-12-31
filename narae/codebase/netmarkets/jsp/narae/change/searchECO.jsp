<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
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

<narae:searchPopulator name="ext.narae.populator.MyWorkPopulator"/>

<input type="hidden" name="null___containerOid___textbox" id="containerOid" maxlength="30" size="10" value="<%=containerRef.getObjectId().getStringValue()%>"/>

<table id="creatingDrawing" style="width: 800px;">
	<tr>
		<td class="attributePanel-asterisk" align=left>
			<H2><%=WTMessage.getLocalizedMessage(RESOURCE , "CHANGE_MNG_SEARCH_ECO", new Object[]{}, locale)%></H2>
		</td>

	</tr>
</table>

<fieldset class="x-fieldset x-form-label-left" id="Visualization_and_Attributes" style="width: 96%;">

    <table border="0">
	<tr>
		<td scope="row" class="attributePanel-label" align="right"><%=WTMessage.getLocalizedMessage(RESOURCE , "ECO_NUMBER", new Object[]{}, locale)%>:</td>
	    <td class="attributePanel-value" align="left">&nbsp;
	        <input class="txt_field" type="text" value="" name="number" id="number" size=30></input>
	    </td>
	    <td scope="row" class="attributePanel-label" align="right" width="100"><%=WTMessage.getLocalizedMessage(RESOURCE , "ECO_NAME", new Object[]{}, locale)%>:</td>
	    <td class="attributePanel-value" align="left">&nbsp;
	        <input class="txt_field" type="text" value="" name="name" id="name" size=30></input>
	    </td>
	</tr>
	<tr>
		<td scope="row" class="attributePanel-label" align="right" width="100"><%=WTMessage.getLocalizedMessage(RESOURCE , "ECR_PROJECT", new Object[]{}, locale)%>:</td>
	    <td class="attributePanel-value" align="left">&nbsp;
	        <input name="prjName" id="prjName" style="width:200px;" type="text">  <a href="#" onclick="projectSearch();">
			<img src="/Windchill/netmarkets/jsp/narae/portal/images/s_search.gif" border="0"></a>
            <input name="prjNo" id="prjNo" type="hidden"> <input name="prjSeqNo" id="prjSeqNo" type="hidden"><input name="unitCode" id="unitCode" type="hidden">
	    </td>
		<td scope="row" class="attributePanel-label" align="right"><%=WTMessage.getLocalizedMessage(RESOURCE , "STATUS", new Object[]{}, locale)%>:</td>
	    <td class="attributePanel-value" align="left">&nbsp;
	        <wrap:comboBox id="state" name="state" multiSelect="false" size="1" 
			        selectedValues="${selectedKey}"
					displayValues="${stateDisplay}"
					internalValues="${stateKey}" />
	    </td>
	    
	</tr>
	
	<tr>
  	    <td colspan="4" scope="row" class="attributePanel-label" align="right">
	        <wrap:button name="search" value='<%=WTMessage.getLocalizedMessage(RESOURCE , "SEARCH", new Object[]{}, locale)%>' onclick="submitDocSearch();" />
			<wrap:button name="Init" value='<%=WTMessage.getLocalizedMessage(RESOURCE , "INITIALIZE", new Object[]{}, locale)%>' onclick="initialize();" />
	    </td>
	</tr>
</table>
</fieldset>
<input id="first" name="first" type="hidden" value="true">

<mvc:tableContainer compId="ext.narae.change.ECOList" height="1000"></mvc:tableContainer>

<script>

	function submitDocSearch(){
		var name = document.getElementById('name').value;
		var number = document.getElementById('number').value;
		var project = document.getElementById('prjName').value;
		var state = document.getElementById('state').value;
		
		if( name == "" && number == "" && project == "" && state == "" ) {
			alert("<%=M001%>");
		} else {
			var params = {
					name : name,
					number : number,
					project : project,
					state : state,
					first : ""
			};

			PTC.jca.table.Utils.reload('ext.narae.change.ECOList', params, true);
		}
	}

	function initialize(){
		document.getElementById('name').value = "";
		document.getElementById('number').value = "";
		document.getElementById('prjName').value = "";
		document.getElementById('state').value = "";
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
	
</script>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>
