<%@page import="ext.narae.service.drawing.beans.DrawingHelper2"%>
<%@page import="ext.narae.service.drawing.beans.DrawingHelper"%>
<%@page import="wt.part.WTPartMaster"%>
<%@page import="wt.part.PartDocHelper"%>
<%@page import="wt.doc.WTDocument"%>
<%@page import="wt.part.WTPart"%>
<%@page import="java.util.Vector"%>
<%@page import="wt.epm.EPMDocument"%>
<%@page import="ext.narae.service.drawing.*"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="wt.fc.QueryResult"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>

<link rel="stylesheet" href="/Windchill/netmarkets/jsp/narae/css/e3ps.css" />
<%@page import="wt.change2.*, wt.query.*"%>

<%@ page import="wt.util.*, wt.org.*, wt.session.*, wt.inf.container.*, ext.narae.util.*, ext.narae.ui.*, java.util.*" %>
<%
WTUser user = (WTUser)SessionHelper.manager.getPrincipal();
String RESOURCE = "ext.narae.ui.common.resource.NaraeCommonRB";
Locale locale = WTContext.getContext().getLocale();
String M001 = WTMessage.getLocalizedMessage(RESOURCE , "M001", new Object[]{}, WTContext.getContext().getLocale());
String ORG_150 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_150", new Object[]{}, WTContext.getContext().getLocale());
String ORG_641 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_641", new Object[]{}, WTContext.getContext().getLocale());
String ORG_743 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_743", new Object[]{}, WTContext.getContext().getLocale());
String ORG_400 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_400", new Object[]{}, WTContext.getContext().getLocale());
String ORG_749 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_749", new Object[]{}, WTContext.getContext().getLocale());
String CANCEL = WTMessage.getLocalizedMessage(RESOURCE , "CANCEL", new Object[]{}, WTContext.getContext().getLocale());
String PRIVATE_CONSTANT_004  = WTMessage.getLocalizedMessage(RESOURCE , "STATUS", new Object[]{}, WTContext.getContext().getLocale());
%>

<%
    String formName = request.getParameter("formName");
	String viewType = request.getParameter("viewType");
    if(formName==null) formName = "forms[0]";
    
    String oid = StringUtil.checkNull(request.getParameter("oid"));
    String moudleType = StringUtil.checkReplaceStr(request.getParameter("moudleType"), ""); //ecr //eco //epm //link 
    String mode = StringUtil.checkReplaceStr(request.getParameter("mode"), "single"); 		//single,mutil
    
    //System.out.println("########### " + oid +":" + moudleType);
    
    Vector vecPart = new Vector();
    QueryResult rt = null;
    String tempNumber 	= ""; 
    String tempName		= "";
    String tempVersion 	= "";
    String tempOid 		= "";
    if(oid.length()>0){
    	Object obj = (Object)CommonUtil.getObject(oid);
    	if(obj instanceof WTChangeRequest2 ){
    		QueryResult result = ChangeHelper2.service.getChangeables((WTChangeRequest2)obj);
    		while( result.hasMoreElements()){
    			WTPart part = (WTPart)result.nextElement();
    			vecPart.add(part);
    		}
    	}else if(moudleType.equals("eco")){
    		QueryResult aResult = ChangeHelper2.service.getChangeActivities((WTChangeOrder2)obj);
    		QueryResult result = ChangeHelper2.service.getChangeablesBefore((WTChangeActivity2)aResult.nextElement());
    		while( result.hasMoreElements()){
    			WTPart part = (WTPart)result.nextElement();
    			vecPart.add(part);
    		}
    	}else if(moudleType.equals("epm")){
    		EPMDocument epm = (EPMDocument)obj;
    		WTPart part = DrawingHelper.manager.getWTPart(epm);
    		if(part!=null){
    			tempNumber = part.getNumber();
        		tempName = part.getName();
        		tempVersion = part.getVersionIdentifier().getValue();
        		tempOid = CommonUtil.getOIDString(part);
    			vecPart.add(part);
    		}
    		
    	}else if(moudleType.equals("link")){
    		
    	}else if(moudleType.equals("doc")){
    		//System.out.println(">>>>>>>>>>>>><<<<<<<<<<<<");
    		WTDocument doc = (WTDocument)obj;
    		
    		rt = PartDocHelper.service.getAssociatedParts(doc);
    		//System.out.println("size === " + rt.size());
    		while( rt.hasMoreElements()){
    			WTPart part = (WTPart)rt.nextElement();
    			//System.out.println("part ==== " + part);
    			vecPart.add(part);
    		}
    		
    	}
    	
    }
%>

<script type="text/javascript">
    //select part
    function setTopPartHtml( tt, data){
        tt.innerHTML = data;
       
    }

    function selectTopPart() { 
        var url = "/Windchill/netmarkets/jsp/narae/part/searchPicker_top.jsp?mode=<%=mode%>&moudleType=<%=moudleType%>";

//         attache = window.showModalDialog(url,window,"help=no; scroll=yes; resizable=yes; dialogWidth=1000px; dialogHeight:650px; center:yes");
       
       var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=0,";
       var popWidth = 900;
       var popHeight = 800;
       var leftpos = (screen.width - popWidth)/ 2;
       var toppos = (screen.height - popHeight) / 2 ;
       var rest = "width="+popWidth+",height="+popHeight+",left=" + leftpos + ',top=' + toppos; 
       var newwin = window.open( url , "selectfolder", opts+rest);
//        if(typeof attache == "undefined" || attache == null) {
//             return;
//         }
		
//         addTopPart(attache);
    }
    function addTopPart(arrObj) {
    	
    	
        var incForm = document.<%=formName%>;
		var mode = "<%=mode%>";
		var moudleType = "<%=moudleType%>"
		
		if(mode == "signle"){
			deleteAllPart();
		}
		
        if(!arrObj.length) {
            return;
        }

        var partOid;//
        var partNumber;//
        var partName;//
        var partState;//
        var partVersion;//
        var pdf;
        var docCheck = true;
		
        for(var i = 0; i < arrObj.length; i++) {
            docCheck = true;
		
            subarr = arrObj[i];
            partOid = subarr[0];//
            partNumber = subarr[1];//
            partName = subarr[2];//
            partVersion = subarr[3];///
            partState = subarr[4];//
            pdf = subarr[5];//
            
            if(incForm.partTopOid) {
                if(incForm.partTopOid.length) {
                    for(var j = 0; j < incForm.partTopOid.length; j++) {
                        if(incForm.partTopOid[j].value == partOid) docCheck = false;
                    }
                }else {
                    if(incForm.partTopOid.value == partOid) docCheck = false;
                }
            }

            if(docCheck) {
                var userRow1 = partTopTable.children[0].appendChild(partTopInnerTempTable.rows[0].cloneNode(true));

                onecell1 = userRow1.childNodes[1];
                setTopPartHtml(onecell1, "<input type=\"checkbox\" name=\"PartTopDelete\"><input type=hidden name=partTopOid value=\""+partOid+"\">");
                onecell2 = userRow1.childNodes[3];
                setTopPartHtml(onecell2, "<a href=\"JavaScript:viewPartPopup('"+partOid+"')\">" +partNumber+"</a>");
                onecell3 = userRow1.childNodes[5];
                setTopPartHtml(onecell3, "<nobr>"+partName+"</nobr>");
                onecell4 = userRow1.childNodes[7];
                setPartHtml(onecell4, partVersion);
                onecell5 = userRow1.childNodes[9];
                setPartHtml(onecell5, partState);
                onecell6 = userRow1.childNodes[11];
                setPartHtml(onecell6, pdf);
               
			    addButton.disabled = true;

                if(mode == "signle"){
        			incForm.pdmNumber.value = partNumber;
        			incForm.pdmName.value = partName;
        		}
            }
        }
    }
    
    function viewPartPopup(oid){
        var str="/Windchill/app/#ptc1/tcomp/infoPage?oid=OR:"+oid;
        var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=0,resizable=0,";
        leftpos = (screen.width - 1000)/ 2; 
        toppos = (screen.height - 600) / 2 ; 
        rest = "width=1000,height=600,left=" + leftpos + ',top=' + toppos;
        
        var newwin = window.open( str , "ViewEcr", opts+rest);
        newwin.focus();
    }

    function deleteTopPart() {
        var incForm = document.<%=formName%>;
        
		var len = document.getElementsByName("PartTopDelete").length-1;
		for(var i=len; i>=0; i--) {
			var node = document.getElementsByName("PartTopDelete")[i];
			if(node.checked) {
				partTopTable.deleteRow(i+1);
			}
		}
		addButton.disabled = false;
}
    
//         if(incForm.PartTopDelete) {
//             if(incForm.PartTopDelete.length) {
//                 index = incForm.PartTopDelete.length-1;

//                 for(i=index; i>=0; i--) {
//                     if(incForm.PartTopDelete[i].checked) partTopTable.deleteRow(i+1);
//                 }
//             } else {
//                 if(incForm.PartTopDelete.checked) partTopTable.deleteRow(1);
//             }
//         }
// 		addButton.disabled = false;
//     }
    
    function deleteTopAllPart(){
	   	var incForm = document.<%=formName%>;
	       if(incForm.PartTopDelete) {
	           if(incForm.PartTopDelete.length) {
	               index = incForm.PartTopDelete.length-1;
	
	               for(i=index; i>=0; i--) {
	                  partTopTable.deleteRow(i+1);
	               }
	           } else {
	                 partTopTable.deleteRow(1);
	           }
	       }
    }

	window.onLoad = function(){
		return true;
	}
	
	function cancelTopPart(){
		var arrObj = new Array();
		var subarr = new Array();
		subarr[0] = "<%=tempOid%>";
		subarr[1] = "<%=tempNumber%>"
		subarr[2] = "<%=tempName%>"
		subarr[3] = "<%=tempVersion%>"
		arrObj[0] = subarr;
		
		addTopPart(arrObj);
	}
	
	function viewBom(oid){
	        var str="/Windchill/netmarkets/jsp/narae/part/bom/PartTree.jsp?oid="+oid;
	        var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
	        leftpos = (screen.width - 1000)/ 2;
	        toppos = (screen.height - 600) / 2 ;
	        rest = "width=1000,height=600,left=" + leftpos + ',top=' + toppos;
	        var newwin = window.open( str , "viewBOM", opts+rest);
	        newwin.focus();
	}
</script>

<table id="partTopInnerTempTable" style="display:none">
    <tr>
        <td class="tdwhiteM"></td>
        <td class="tdwhiteM"></td>
        <td class="tdwhiteM"></td>
        <td class="tdwhiteM"></td>
        <td class="tdwhiteM"></td>
        <td class="tdwhiteM"></td>
    </tr>
</table>

<table border="0" cellpadding="0" cellspacing="2">
    <tr>
    	<% 
		if (viewType == null ) viewType = "";
		if( !viewType.equals("view") ) { 
		%>
        <td id="addButton">
        	<a style="FONT-SIZE: 8pt;" onclick="selectTopPart();">
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
        	<a style="FONT-SIZE: 8pt;" onclick="deleteTopPart();">
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
         <%if(moudleType.equals("epm")){ %>
        <td>
        	<a style="FONT-SIZE: 8pt;" onclick="cancelTopPart();">
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
								<div id="_text" align="center"><%=CANCEL%></div>
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
         <%} %>
      <%} %>
    </tr>
</table>
<!-- div style="width:99%;overflow-x:hidden;overflow-y:auto;border:1px;border-style:solid;border-color:#5F9EA0;padding:0px;margin:1px 1px 5px;"-->
    <table width="100%" cellspacing="0" cellpadding="1" border="0" id="partTopTable" align="center">
        <tbody>
            <tr>
                <td class="tdblueM" width="30%" colspan="2"><%=ORG_743%></td>
                <td class="tdblueM" width="40%"><%=ORG_400%></td>
                <td class="tdblueM" width="10%"><%=ORG_749%></td>
                <td class="tdblueM" width="10%"><%=PRIVATE_CONSTANT_004%></td>
                <td class="tdblueM" width="10%">PDF</td>

            </tr>
            <%for(int i =0 ; i<vecPart.size(); i++){ 
            	WTPart part = (WTPart)vecPart.get(i);
            	//System.out.println(partTopTable);
            	
            	String buffer = "";
                EPMDocument modelEpm = DrawingHelper2.getEPMDocument(part);
                List<EPMDocument> drawingEpm = null;
                if( modelEpm != null ) {
                	EPMDocument one2D = DrawingHelper2.getRelational2DCad(modelEpm);
                	if( one2D != null ) {
                		buffer = buffer + DrawingHelper2.getPDFFile(one2D, modelEpm.getNumber());
                	}
                	
                }
            %>
            <tr>
            	<%if(viewType.equals("view")) {%>
            	<td class="tdwhiteM" colspan=2><a href="JavaScript:viewPartPopup('<%=part.getPersistInfo().getObjectIdentifier().toString()%>')"><%=part.getNumber() %></a></td>
            	<%} else { %>
            	<td class="tdwhiteM"><input type="checkbox" name="PartTopDelete"><input type=hidden name=partTopOid value=<%=CommonUtil.getOIDString(part) %>></td>
            	<td class="tdwhiteM"><a href="JavaScript:viewPartPopup('<%=part.getPersistInfo().getObjectIdentifier().toString()%>')"><%=part.getNumber() %></a></td>
            	<%} %>
            	
            	<td class="tdwhiteM" width="40%"><%=part.getName() %></td>
            	<td class="tdwhiteM" width="10%"><%=part.getVersionIdentifier().getValue() %></td>
            	<td class="tdwhiteM" width="10%"><%=part.getState().getState().getDisplay(locale) %></td>
            	<td class="tdwhiteM" width="10%"><%=buffer %></td>
            </tr>
            <%} %>
        </tbody>
    </table>
<!-- /div-->
