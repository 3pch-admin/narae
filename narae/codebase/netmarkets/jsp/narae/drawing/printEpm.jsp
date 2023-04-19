<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf" %>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf" %>
<%@ taglib prefix="mvc" uri="http://www.ptc.com/windchill/taglib/jcaMvc"%>
<%@ taglib prefix="wrap" uri="http://www.ptc.com/windchill/taglib/wrappers"%>
<%@ taglib prefix="narae" uri="http://www.ptc.com/windchill/taglib/narae"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="wctags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="attachments" uri="http://www.ptc.com/windchill/taglib/attachments" %>
<%@ include file="/netmarkets/jsp/narae/code/code1.jspf"%>

<link rel="stylesheet" href="/Windchill/netmarkets/jsp/narae/css/e3ps.css">
<%@ page import="wt.part.*,wt.util.*, wt.org.*, wt.session.*, wt.inf.container.*, ext.narae.util.*, ext.narae.ui.*, java.util.*" %>

<%
String RESOURCE = "ext.narae.ui.common.resource.NaraeCommonRB";
String UNITRB = "ext.narae.ui.common.resource.NareUnitRB";
Locale locale = WTContext.getContext().getLocale();
String M001 = WTMessage.getLocalizedMessage(RESOURCE , "M001", new Object[]{}, WTContext.getContext().getLocale());
WTContainerRef containerRef = WCUtil.getWTContainerRefForDrawing();
WTContainerRef partContainerRef = WCUtil.getWTContainerRefForPart();

String ORG_938 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_938", new Object[]{}, locale);
String ORG_549 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_549", new Object[]{}, locale);
String ORG_244 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_244", new Object[]{}, locale);
String ORG_576 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_576", new Object[]{}, locale);
String ORG_890 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_890", new Object[]{}, locale);


%>
<script type="text/javascript">
	if(self.name != 'reload'){
		self.name = 'reload';
		self.location.reload(true);
	}
	else self.name='';
</script>
<script>
document.title = "<%=WTMessage.getLocalizedMessage(RESOURCE , "Print_Drawing", new Object[]{}, locale)%>";
</script>

<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
<form name=drawingPrintForm id=drawingPrintForm method=post enctype="multipart/form-data">
<table width="100%" border="0" cellpadding="0" cellspacing="10" > <!--//ì¬ë°± íì´ë¸-->
<tr  height=5><td>
<table id="creatingDrawing" style="width: 800px;">
	<tr>
		<td class="attributePanel-asterisk" align=left>
			<H2><%=WTMessage.getLocalizedMessage(RESOURCE , "Print_Drawing", new Object[]{}, locale)%></H2>
		</td>
	</tr>
</table>

<table width="100%" border="0" cellpadding="0" cellspacing="10" >
	<tr bgcolor="ffffff">
		<td class="tdwhiteL" colspan="4">
<%-- 		<jsp:include page="/netmarkets/jsp/narae/drawing/select_include2.jsp" flush="true"> --%>
		<jsp:include page="/netmarkets/jsp/narae/drawing/select_include2.jsp" flush="true">
		<jsp:param name="formName" value="mainform"/>
		<jsp:param name="viewType" value="view"/>
		</jsp:include>
		</td>
	</tr>
</table>
</td>
</tr>
</table>
</form>
<script>
// function deleteNoPdf() {
// 	var incForm = document.mainform;
// 	 if(incForm.EpmDelete) {
// 	        if(incForm.EpmDelete.length) {
// 	            index = incForm.EpmDelete.length-1;
// 	            for(i=index; i>=0; i--) {
// 	                	var value = incForm.EpmDelete[i].value;
// 	                	if(value == "false"){
// 	                		epmTable.deleteRow(i+1);
// 	                	}
// 	            }
// 	        }
// 	    }
// }

// function deleteEpm() {
//     var incForm = document.mainform;
//     if(incForm.EpmDelete) {
//         if(incForm.EpmDelete.length) {
//             index = incForm.EpmDelete.length-1;

//             for(i=index; i>=0; i--) {
//                 if(incForm.EpmDelete[i].checked)epmTable.deleteRow(i+1);
//             }
//         } else {
//             if(incForm.EpmDelete.checked) epmTable.deleteRow(1);
//         }
//     }
// }

function printEpm() {
	var items = "";
	 var incForm = document.mainform;
	
    if(partCheck=="true"){
   	 numberDisplay('delete');
    }
    if(incForm.EpmDelete) {
        if(incForm.EpmDelete.length) {
            index = incForm.EpmDelete.length-1;
            
            for(i=index; i>=0; i--) {
               if(incForm.EpmDelete[i].checked){
                	//alert(incForm.EpmDelete[i].value);
                	
                	var value = incForm.epmOid[i].value;
                	
                	if(!value){
                		alert("There are drawings that have not been converted. (X)");
                		return false;
                	}
					items += value + ",";
               }
            }
        } else {
           // if(incForm.EpmDelete.checked) alert(incForm.EpmDelete.value);
            var value = incForm.epmOid.value;
			items += value + ",";
        }
    }
    
	items = items.substring(0, items.length - 1);
	var itemsInput = document.getElementById("items");
	var test11 = document.getElementById("test11");
	 if(incForm.test11) {
	        if(incForm.test11.length) {
	            index = incForm.test11.length-1;
	            for(i=index; i>=0; i--) {
	            	test11.value[i] = items;
	            }
	    }
	 }
	test11.value = items;
	itemsInput.setAttribute("value", items);
	var url = "/Windchill/netmarkets/jsp/narae/drawing/printClipboard.jsp";
	var title = "batchPrint";
	var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
	leftpos = (screen.width - 1000) / 2;
	toppos = (screen.height - 600) / 2;
	rest = "width=1000,height=600,left=" + leftpos + ',top=' + toppos;
	var newwin = window.open("", title, opts + rest);
	
	incForm.setAttribute("target", title);
	incForm.setAttribute("action", url);
	incForm.setAttribute("method", "post");
	incForm.submit();
	
}
</script>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>
