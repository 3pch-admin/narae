<%@page import="java.util.Locale"%>
<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ taglib prefix="mvc" uri="http://www.ptc.com/windchill/taglib/jcaMvc"%>
<%@ taglib prefix="wrap" uri="http://www.ptc.com/windchill/taglib/wrappers"%>
<%@ taglib prefix="narae" uri="http://www.ptc.com/windchill/taglib/narae"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="wctags"%>

<%@ page import="wt.util.*, wt.org.*, wt.session.*, wt.inf.container.*, ext.narae.util.*, ext.narae.ui.*" %>

<%
// ERP Maker를 동기화 한다. (==> 페이지를 열 때마다)
/*
Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
String url = "jdbc:sqlserver://10.0.2.8:1433;database=eBizXR2";
String user = "sa";
String pwd = "2066sb";
Connection conn = DriverManager.getConnection(url, user, pwd);

Statement stmt = conn.createStatement();

// Maker 코드 리스트업
String sql = "SELECT * FROM tcb09";	
ResultSet rs = stmt.executeQuery(sql);

while(rs.Next()){

}
*/

// ERP Maker 동기화 완료



String RESOURCE = "ext.narae.ui.common.resource.NaraeCommonRB2";
String M001 = WTMessage.getLocalizedMessage(RESOURCE , "M001", new Object[]{}, WTContext.getContext().getLocale());
WTContainerRef containerRef = WCUtil.getWTContainerRefForDrawing();
Locale locale = WTContext.getContext().getLocale();

%>
<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
<input type="hidden" name="null___containerOid___textbox" id="containerOid" maxlength="30" size="10" value="<%=containerRef.getObjectId().getStringValue()%>"/>

<table id="creatingDrawing" style="width: 800px;">
	<tr>
		<td class="attributePanel-asterisk" align=left>
			<H2><%=WTMessage.getLocalizedMessage(RESOURCE , "PART_MNG_SEARCH_MAKER", new Object[]{}, locale)%></H2>
		</td>
	</tr>
</table>

<fieldset class="x-fieldset x-form-label-left" id="Visualization_and_Attributes" style="width: 96%;">
    <table border="0">
	<tr>
		<td scope="row" class="attributePanel-label" align="right"><%=WTMessage.getLocalizedMessage(RESOURCE , "MAKER_ID", new Object[]{}, locale)%>:</td>
	    <td class="attributePanel-value" align="left">&nbsp;
	        <input class="txt_field" type="text" value="" name="name" id="name" size=40></input>
	    </td>
	    <td scope="row" class="attributePanel-label" align="right" width="100"><%=WTMessage.getLocalizedMessage(RESOURCE , "MAKER_NAME", new Object[]{}, locale)%>:</td>
	    <td class="attributePanel-value" align="left">&nbsp;
	        <input class="txt_field" type="text" value="" name="description" id="description" size=40></input>
	    </td>
	</tr>
	<tr>
		<td colspan=2 align=left>
			<wrap:button name="search" value='<%=WTMessage.getLocalizedMessage(RESOURCE , "SAVE", new Object[]{}, locale)%>' onclick="doRegist();" />
		</td>
		<td colspan=2 align=right>
			<wrap:button name="search" value='<%=WTMessage.getLocalizedMessage(RESOURCE , "SEARCH_MAKER", new Object[]{}, locale)%>' onclick="submitDocSearch();" />
			<wrap:button name="Init" value='<%=WTMessage.getLocalizedMessage(RESOURCE , "INITIALIZE", new Object[]{}, locale)%>' onclick="initialize();"/>
		</td>
	</tr>
	</table>
</fieldset>
<input id="first" name="first" type="hidden" value="true">

<mvc:tableContainer compId="ext.narae.part.MakerList" height="500"></mvc:tableContainer>

<script>
	function doRegist() {
// 		var url = "/Windchill/netmarkets/jsp/narae/part/registMaker.jsp";
// 		attache = window.showModalDialog(url, self, "help=no; scroll=no; resizable=no; dialogWidth=500px; dialogHeight:220px; center:yes");
		
		var pForm = document.makerSrchForm;
    	var url = "/Windchill/netmarkets/jsp/narae/part/registMaker.jsp";
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
					mkrCode : name,
					mkrName : description
		};

		PTC.jca.table.Utils.reload('ext.narae.part.MakerList', params, true);
	}

	function initialize(){
		document.getElementById('description').value = "";
		document.getElementById('name').value = "";
	}
	
</script>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>
