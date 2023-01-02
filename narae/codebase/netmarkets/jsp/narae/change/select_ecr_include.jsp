<%@page import="ext.narae.util.CommonUtil"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@page import="java.util.*,wt.content.*,wt.fc.*,wt.query.*,wt.org.*,wt.session.*" %>
<link rel="stylesheet" href="/Windchill/netmarkets/jsp/narae/css/e3ps.css">

<%@ page import="wt.util.*, wt.org.*, wt.session.*, wt.inf.container.*, ext.narae.ui.*, java.util.*, wt.change2.*,ext.narae.service.iba.*" %>
<%
WTUser user = (WTUser)SessionHelper.manager.getPrincipal();
String RESOURCE = "ext.narae.ui.common.resource.NaraeCommonRB";
Locale locale = WTContext.getContext().getLocale();
String M001 = WTMessage.getLocalizedMessage(RESOURCE , "M001", new Object[]{}, WTContext.getContext().getLocale());
String ORG_150 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_150", new Object[]{}, WTContext.getContext().getLocale());
String ORG_641 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_641", new Object[]{}, WTContext.getContext().getLocale());
String ORG_743 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_743", new Object[]{}, WTContext.getContext().getLocale());
String ORG_283 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_283", new Object[]{}, WTContext.getContext().getLocale());
String ORG_869 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_869", new Object[]{}, WTContext.getContext().getLocale());
%>

<%
String formName = request.getParameter("formName");
String viewType = request.getParameter("viewType");
String oid = request.getParameter("oid");
if(formName==null)formName = "forms[0]";
%>
<script>

//select ecr
function setEcrHtml( tt, data){
	tt.innerHTML = data;
}

function ViewEcrPopup2(oid){
    var str="/Windchill/app/#ptc1/narae/change/detailECR?form=normal&oid="+oid;
    var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=0,resizable=0,";
    leftpos = (screen.width - 700)/ 2; 
    toppos = (screen.height - 500) / 2 ; 
    rest = "width=1000,height=600,left=" + leftpos + ',top=' + toppos;
    
    var newwin = window.open( str , "ViewEcr", opts+rest);
    newwin.focus();
}
	
function selectEcr() {
	var url = "/Windchill/netmarkets/jsp/narae/change/searchECRPicker.jsp?";
	
	attache = window.showModalDialog(url,window,"help=no; scroll=yes; resizable=yes; dialogWidth=800px; dialogHeight:550px; center:yes");
	if(typeof attache == "undefined" || attache == null) {
		return;
	}

	addEcr(attache);
}
function addEcr(arrObj) {
	if(arrObj.length == 0) {
		return;
	}

	var ecrOid;//
	var ecrNumber;//
	var ecrName;//
	var ecrCreator;//
	var docCheck = true;
	
	for(var i = 0; i < arrObj.length; i++) {
		docCheck = true;
		
		subarr = arrObj[i];
		ecrOid = subarr[0];//
		ecrNumber = subarr[1];//
		ecrName = subarr[2];//
		ecrCreator = subarr[3];//
		prjName	= subarr[4];//
		
/*		if(document.<%=formName%>.prjNo.value.length>0){
			
			if(document.<%=formName%>.prjNo.value == prjNo) continue;
			
		}
*/		
		var ecrOidFrm = document.getElementsByName("prjName");
		if(ecrOidFrm != undefined) {
			if(ecrOidFrm.length) {
				for(var j = 0; j < ecrOidFrm.length; j++) {
					if(ecrOidFrm[j].value == ecrOid) docCheck = false;
				}
			}else {
				if(ecrOidFrm.value == ecrOid) docCheck = false;
			}
		}

		if(docCheck) {
			var userRow1 = ecrTable.children[0].appendChild(ecrInnerTempTable.rows[0].cloneNode(true));
			onecell1 = userRow1.childNodes[1];
			setEcrHtml(onecell1, "<input type=\"checkbox\" name=\"ecrDelete\"><input type=hidden name=ecrOid value=\""+ecrOid+"\">");
			onecell2 = userRow1.childNodes[3];
			setEcrHtml(onecell2, "<a href=\"JavaScript:ViewEcrPopup2('"+ecrOid+"')\">"+ecrNumber+"</a>");
			onecell3 = userRow1.childNodes[5];
			setEcrHtml(onecell3, "<nobr>"+ecrName+"</nobr>");
			onecell4 = userRow1.childNodes[7];
			setEcrHtml(onecell4, ecrCreator);
		}
		
		projectSelect1(prjName);
		
		
	}
}

function projectSelect1(prjName){
	//prjNo prjSeqNo unitCode
	document.getElementById("prjName").value = prjName;
}

function deleteEcr() {
	var ecrDelete = document.getElementsByName("ecrDelete");
	//if(ecrDelete.ecrDelete.length != undefined ) {
		index = ecrDelete.length-1;
	
		for(i=index; i>=0; i--) {
			if(ecrDelete[i].checked == true) ecrTable.deleteRow(i+1);
		}
	//}else {
	//	alert(ecrDelete.checked);
	//	if(ecrDelete.checked == true) ecrTable.deleteRow(1);
	//}
}

</script>

<table id="ecrInnerTempTable" style="display:none">
	<tr><td class="tdwhiteM"></td><td class="tdwhiteM"></td><td class="tdwhiteL"></td><td class="tdwhiteM"></td></tr>
</table>

<table width=100%  cellpadding="0" cellspacing="0" ><tr><td>
<% 
	if (viewType == null ) viewType = "";
	if( !viewType.equals("view") ) { 
%>
<table border="0" cellpadding="0" cellspacing="4" align="left">
<tr>
	
   	<td> 
   		<a style="FONT-SIZE: 8pt;" onclick="selectEcr();">
				<table class="" border="0" cellpadding="0" cellspacing="0" width="50">
				<tbody>
				<tr>
					<td width="7">
						<img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left.gif" alt="" height="20" width="7">
					</td>
					<td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid.gif" valign="middle">
						<table align="center" border="0" cellpadding="0" cellspacing="0">
						<tbody>
						<tr>
							<td>
								<div id="_text" align="center"><%=ORG_150%></div>
							</td>
						</tr>
						</tbody>
						</table>
					</td>
					<td width="12">
						<img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right.gif" alt="" height="20" width="12">
					</td>
				</tr>
				</tbody>
				</table>
		</a>
   </td>
   <td> 
   		<a style="FONT-SIZE: 8pt;" onclick="deleteEcr();">
				<table class="" border="0" cellpadding="0" cellspacing="0" width="50">
				<tbody>
				<tr>
					<td width="7">
						<img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left.gif" alt="" height="20" width="7">
					</td>
					<td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid.gif" valign="middle">
						<table align="center" border="0" cellpadding="0" cellspacing="0">
						<tbody>
						<tr>
							<td>
								<div id="_text" align="center"><%=ORG_641%></div>
							</td>
						</tr>
						</tbody>
						</table>
					</td>
					<td width="12">
						<img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right.gif" alt="" height="20" width="12">
					</td>
				</tr>
				</tbody>
				</table>
		</a>
	</td>

</tr>
</table>
<%} %>
</td></tr>
<tr><td>
<div style="width:99%;overflow-x:hidden;overflow-y:auto;border:1px;border-style:solid;border-color:#5F9EA0;padding:0px;margin:1px 1px 5px;">
<table width="100%" cellspacing="0" cellpadding="1" border="0" id="ecrTable" align="center"><tbody><tr>
	<td class="tdblueM" width="40%" height="22" colspan="2"><%=ORG_743%></td>
	<td class="tdblueM" width="50%" height="22"><%=ORG_283%></td>
	<td class="tdblueM" width="10%" height="22"><%=ORG_869%></td>
	</tr></tbody>
	<%
	if(oid != null && oid.length() > 0 ) {
		WTChangeOrder2 order = (WTChangeOrder2)CommonUtil.getObject(oid);
		QueryResult result = ChangeHelper2.service.getChangeRequest(order);
		if(result.size() > 0 ) {
			int index = 0;
			while(result.hasMoreElements()) {
				out.println("<tr>");
				WTChangeRequest2 one = (WTChangeRequest2)result.nextElement();
				out.println("<td class=\"tdwhiteM\"><input name=\"ecrDelete\" type=\"checkbox\">" + "<input name=\"ecrOid\" value=\"" + 
								one.getPersistInfo().getObjectIdentifier().toString() + "\" type=\"hidden\"></td>");
				out.println("<td class=\"tdwhiteM\"><a href=\"JavaScript:ViewEcrPopup2('" + one.getPersistInfo().getObjectIdentifier().toString() + "')\">" + one.getNumber() + "</a></td>");
				out.println("<td class=\"tdwhiteL\"><nobr>" +
						one.getName() + "</nobr></td>");
				out.println("<td class=\"tdwhiteM\">" + one.getCreatorFullName() + "</td></tr>");
				index++;
				out.println("</tr>");
			}
		}
	}
	%>
</table>
</div>
</td></tr></table>

