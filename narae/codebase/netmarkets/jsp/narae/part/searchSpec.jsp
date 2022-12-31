<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ taglib prefix="mvc" uri="http://www.ptc.com/windchill/taglib/jcaMvc"%>
<%@ taglib prefix="wrap" uri="http://www.ptc.com/windchill/taglib/wrappers"%>
<%@ taglib prefix="narae" uri="http://www.ptc.com/windchill/taglib/narae"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="wctags"%>

<%@ page import="wt.util.*, wt.org.*, wt.session.*, wt.inf.container.*, ext.narae.util.*, ext.narae.ui.*" %>

<%
String RESOURCE = "ext.narae.ui.common.resource.NaraeCommonRB";
String M001 = WTMessage.getLocalizedMessage(RESOURCE , "M001", new Object[]{}, WTContext.getContext().getLocale());
WTContainerRef containerRef = WCUtil.getWTContainerRefForDrawing();
Locale locale = WTContext.getContext().getLocale();
%>
<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
<input type="hidden" name="null___containerOid___textbox" id="containerOid" maxlength="30" size="10" value="<%=containerRef.getObjectId().getStringValue()%>"/>

<table id="creatingDrawing" style="width: 800px;">
	<tr>
		<td class="attributePanel-asterisk" align=left>
			<H2><%=WTMessage.getLocalizedMessage(RESOURCE , "PART_MNG_SEARCH_SPEC", new Object[]{}, locale)%></H2>
		</td>

	</tr>
</table>

<fieldset class="x-fieldset x-form-label-left" id="Visualization_and_Attributes" style="width: 96%;">

    <table border="0">
	<tr>
		<td scope="row" class="attributePanel-label" align="right"><%=WTMessage.getLocalizedMessage(RESOURCE , "STANDARD", new Object[]{}, locale)%>:</td>
	    <td class="attributePanel-value" align="left">&nbsp;
	        <input class="txt_field" type="text" value="" name="name" id="name" size=40></input>
	    </td>
	    <td scope="row" class="attributePanel-label" align="right" width="100"><%=WTMessage.getLocalizedMessage(RESOURCE , "MATERIAL_NAME", new Object[]{}, locale)%>:</td>
	    <td class="attributePanel-value" align="left">&nbsp;
	        <input class="txt_field" type="text" value="" name="description" id="description" size=40></input>
	    </td>
	</tr>
	<tr>
		<td colspan=2 align=left>
			<wrap:button name="search" value='<%=WTMessage.getLocalizedMessage(RESOURCE , "SAVE", new Object[]{}, locale)%>' onclick="doRegist();" />
		</td>
		<td colspan=2 align=right>
			<wrap:button name="search" value='<%=WTMessage.getLocalizedMessage(RESOURCE , "SEARCH", new Object[]{}, locale)%>' onclick="submitDocSearch();" />
			<wrap:button name="Init" value='<%=WTMessage.getLocalizedMessage(RESOURCE , "INITIALIZE", new Object[]{}, locale)%>' onclick="initialize();"/>
		</td>
	</tr>
	</table>
</fieldset>
<input id="first" name="first" type="hidden" value="true">

<mvc:tableContainer compId="ext.narae.part.SpecList" height="500"></mvc:tableContainer>

<script>
	function doRegist() {
// 		var url = "/Windchill/netmarkets/jsp/narae/part/registSpec2.jsp";
// 		attache = window.showModalDialog(url, window, "help=no; scroll=no; resizable=no; dialogWidth=500px; dialogHeight:220px; center:yes");
		
		var pForm = document.specSrchForm;
    	var url = "/Windchill/netmarkets/jsp/narae/part/registSpec2.jsp";
    	var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=no,";
        var popWidth = 1000;
        var popHeight = 600;
        var leftpos = (screen.width - popWidth)/ 2;
        var toppos = (screen.height - popHeight) / 2 ;
        var rest = "width="+popWidth+",height="+popHeight+",left=" + leftpos + ',top=' + toppos;
        
        var newwin = window.open(url, "doRegist", opts+rest);
        newwin.focus();
	}

	function submitDocSearch(){
		var name = document.getElementById('name').value;
		var description = document.getElementById('description').value;
		
		var params = {
					name : name,
					description : description
		};

		PTC.jca.table.Utils.reload('ext.narae.part.SpecList', params, true);
	}

	function initialize(){
		document.getElementById('description').value = "";
		document.getElementById('name').value = "";
	}
	
</script>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>
