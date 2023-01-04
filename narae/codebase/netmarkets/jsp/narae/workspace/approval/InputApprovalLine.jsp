<%@page import="ext.narae.util.WCUtil"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>

<%@page import="wt.org.WTUser"%>
<%@page import="wt.session.SessionHelper"%>
<%@ page import="wt.util.*, wt.org.*, wt.session.*, wt.inf.container.*, ext.narae.ui.*, java.util.*" %>
<%
String RESOURCE = "ext.narae.ui.common.resource.NaraeCommonRB";
String UNITRB = "ext.narae.ui.common.resource.NareUnitRB";
Locale locale = WTContext.getContext().getLocale();
WTContainerRef containerRef = WCUtil.getWTContainerRefForDrawing();
WTContainerRef partContainerRef = WCUtil.getWTContainerRefForPart();
String ORG_1088 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_1088", new Object[]{}, locale);
String ORG_1066 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_1066", new Object[]{}, locale);
String ORG_1063 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_1063", new Object[]{}, locale);
%>

<html>
<title><%=ORG_1088%></title>

<LINK rel="stylesheet" type="text/css" href="/Windchill/netmarkets/jsp/narae/css/e3ps.css">
<LINK REL="stylesheet" TYPE="text/css" HREF="/Windchill/netmarkets/jsp/narae/css/css.css">
<script language="javascript">


function checkLineName(inputType) {

	var inputName = document.publishForm.lineName.value;
	inputName = inputName.trim();
	
	opener.document.getElementById("title").value = inputName;
	opener.createLine();
	self.close();
	
// 	opener.setChildValue(inputName);
// 	alert(inputName);
	
// 	if( inputType == "cancel" ) {
// 		window.returnValue = "cancel";   
// 		window.close();
		
// 	} else  if( inputName == "" ) {
<%-- 		alert("<%=ORG_1063%>!"); --%>
// 		return;
		
// 	} else if( inputName != null || inputName != "" ) {
// 		window.parent.returnValue = inputName;
// 		opener.document.getElementById("lineList").value = inputName;
// 		console.log("inputName = " + inputName);
// // 		window.close();

// 	} else {
<%-- 		alert("<%=ORG_1063%>!"); --%>
// 		return;
// 	}
	///Windchill/netmarkets/jsp/narae/workspace/approval/AjaxCreateLine.jsp
}

String.prototype.ltrim = function() {
    var re = /\s*((\S+\s*)*)/;
    return this.replace(re, "$1");
   }
 
   String.prototype.rtrim = function() {
    var re = /((\s*\S+)*)\s*/;
    return this.replace(re, "$1");
   }
 
   String.prototype.trim = function() {
    return this.ltrim().rtrim();
   }


   function windEdit_Enter(arg) {
   	if(arg == 1) {
   		return false;
   	} else 
   		if(arg == 2){
   			checkLineName('');
   		}
   }
//-->
</script>
<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<form method=post name="publishForm"  method=post onSubmit="return windEdit_Enter(1)">
<table width="366" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td width="366" height="28"><br>
      <table width="366" height="22" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td width="333" background="/Windchill/netmarkets/jsp/narae/img/pop_title_bar.gif"><table width="300" border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td width="50">&nbsp;</td>
                <td><%=ORG_1088%> </td>
              </tr>
            </table></td>
          <td>&nbsp;</td>
        </tr>
      </table> </td>
  </tr>
  <tr>
    <td><br>
      <table width="300" border="0" align="center" cellpadding="0" cellspacing="1" bgcolor=#cfcfcf class=9pt>
        <tr>
          <td height="40"  class="a_con_05">            <div align="center">
              <input type="text" name="lineName"  id="lineName" OnKeyDown="if(event.keyCode==13) windEdit_Enter(2);" >
            
            </div></TD>
        </TR>
      </table>
      <table width="300" height="40" border="0" align="center">
        <tr>
          <td><table width="150" border="0" align="center" cellpadding="0" cellspacing="0">
              <tr>
                <td><div align="center"><a href="javascript:checkLineName('');"><img src="/Windchill/netmarkets/jsp/narae/img/check.gif" width="50" height="18"></a></div></td>
                <!--td><div align="center"><a href="javascript:checkLineName('cancel');"><img src="/Windchill/netmarkets/jsp/narae/img/close.gif" width="50" height="18"></div></a></td-->
              </tr>
            </table></td>
        </tr>
      </table></td>
  </tr>
  <tr> 
    <td>
      <table width="366" height="8" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td></td>
        </tr>
      </table> </td>
  </tr>
  <tr>
    <td></td>
  </tr>
</table>
</body>
