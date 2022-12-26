<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ taglib prefix="mvc" uri="http://www.ptc.com/windchill/taglib/jcaMvc"%>
<%@ taglib prefix="wrap" uri="http://www.ptc.com/windchill/taglib/wrappers"%>
<%@ taglib prefix="narae" uri="http://www.ptc.com/windchill/taglib/narae"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="wctags"%>

<%@ page import="wt.util.*, wt.org.*, wt.session.*" %>

<%
String RESOURCE = "ext.narae.ui.common.resource.NaraeCommonRB";
String M001 = WTMessage.getLocalizedMessage(RESOURCE , "M001", new Object[]{}, WTContext.getContext().getLocale());
%>
<script type="text/javascript">
	if(self.name != 'reload'){
		self.name = 'reload';
		self.location.reload(true);
	}
	else self.name='';
</script>
<narae:searchPopulator name="ext.narae.populator.MyWorkPopulator"/>
<table id="creatingDrawing" style="width: 800px;">
	<tr>
		<td class="attributePanel-asterisk" align=left>
			<H2><%=WTMessage.getLocalizedMessage(RESOURCE , "APPROVAL_MYWORK", new Object[]{}, WTContext.getContext().getLocale())%></H2>
		</td>

	</tr>
</table>
<fieldset class="x-fieldset x-form-label-left" id="Visualization_and_Attributes" style="width: 96%;">

    <table>
	<tr>
		
	    <td scope="row" class="tableColumnHeaderfont" align="left"><%=WTMessage.getLocalizedMessage(RESOURCE , "NAME", new Object[]{}, WTContext.getContext().getLocale())%>:</td>
	    <td class="tabledatafont" align="left">&nbsp;
	        <wrap:textBox name="name" id="name" maxlength="30" size="30"/>
	    </td>

	    <td scope="row" class="tableColumnHeaderfont" align="left">&nbsp;&nbsp;&nbsp;&nbsp;<%=WTMessage.getLocalizedMessage(RESOURCE , "CREATED_ON", new Object[]{}, WTContext.getContext().getLocale())%>:</td>
	    <td class="tabledatafont" align="left">&nbsp;
	        <wrap:dateInputComponent id="startdate" name="startdate" required="false" dateValueType="DATE_ONLY"/> ~ 
			<wrap:dateInputComponent id="enddate" name="finishdate" required="false" dateValueType="DATE_ONLY"/>
	    </td>
		<script>
			document.getElementById('startdate').value = "";
			document.getElementById('enddate').value = "";
		</script>

	</tr>

	<tr>
	    <td scope="row" class="tableColumnHeaderfont" align="left"><%=WTMessage.getLocalizedMessage(RESOURCE , "CREATOR", new Object[]{}, WTContext.getContext().getLocale())%>:</td>
		<%
		WTUser user = (WTUser)SessionHelper.manager.getPrincipal();
		%>
	    <td class="tabledatafont" align="left">&nbsp;
	        <!--wrap:textBox name="creator" id="creator" maxlength="30" size="30" value='<%=user.getAuthenticationName()%>' /-->
			<wrap:suggestTextBox name="creator" id="creator" serviceKey="userSuggest" maxlength="30" size="27" minChars="2"/>
			<script>
				document.getElementById('creator').value = '<%=user.getAuthenticationName()%>';
			</script>
	    </td>

	    <td scope="row" class="tableColumnHeaderfont" align="left">&nbsp;&nbsp;&nbsp;&nbsp;<%=WTMessage.getLocalizedMessage(RESOURCE , "STATUS", new Object[]{}, WTContext.getContext().getLocale())%>:</td>
	    <td class="tabledatafont" align="left">&nbsp;
			<wrap:comboBox id="state" name="state" multiSelect="false" size="1" 
			        selectedValues="${selectedKey}"
					displayValues="${stateDisplay}"
					internalValues="${stateKey}" />
	    </td>

	</tr>
	
	<tr>
  	    <td colspan="4" scope="row" class="tableColumnHeaderfont" align="right">
	        <wrap:button name="search" value='<%=WTMessage.getLocalizedMessage(RESOURCE , "SEARCH", new Object[]{}, WTContext.getContext().getLocale())%>' onclick="submitDocSearch();" />
			<wrap:button name="Init" value='<%=WTMessage.getLocalizedMessage(RESOURCE , "INITIALIZE", new Object[]{}, WTContext.getContext().getLocale())%>' onclick="initialize();" />
	    </td>
	</tr>
        </table>
</fieldset>
<input id="first" name="first" type="hidden" value="true">
<mvc:tableContainer compId="ext.narae.approval.MyWork" height="500">
</mvc:tableContainer>
<script>

	function submitDocSearch(){
		var name = document.getElementById('name').value;
		var startdate = document.getElementById('startdate').value;
		var enddate = document.getElementById('enddate').value;
		var creator = document.getElementById('creator').value;
		var state = document.getElementById('state').value;
		
		if( name == "" && creator == "" && startdate == "" && enddate == "" && state == "" ) {
			alert("<%=M001%>");
		} else {
			var params = {
					name : name,
					startdate : startdate,
					enddate : enddate,
					creator : creator,
					state : state,
					first : ""
			};

			PTC.jca.table.Utils.reload('ext.narae.approval.MyWork', params, true);
		}
	}

	function initialize(){
		document.getElementById('name').value = "";
		document.getElementById('startdate').value = "";
		document.getElementById('enddate').value = "";
		document.getElementById('creator').value = "<%=user.getAuthenticationName()%>";
		document.getElementById('state').value = "INWORK";
	}
	
</script>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>
