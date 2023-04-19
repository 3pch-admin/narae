<%@page import="ext.narae.erp.ERPPdfSender"%>
<%@page import="ext.narae.service.erp.beans.ERPUtil"%>
<%@page import="ext.narae.service.drawing.beans.DrawingHelper2"%>
<%@page import="ext.narae.service.drawing.beans.DrawingHelper"%>
<%@page import="wt.epm.EPMDocumentMaster"%>
<%@page import="wt.epm.structure.EPMReferenceLink"%>
<%@page import="wt.epm.structure.EPMStructureHelper"%>
<%@page import="wt.vc.config.LatestConfigSpec"%>
<%@page import="com.ptc.wvs.server.ui.UIHelper"%>
<%@page import="wt.content.ApplicationData"%>
<%@page import="wt.content.ContentItem"%>
<%@page import="wt.content.ContentHelper"%>
<%@page import="wt.content.ContentRoleType"%>
<%@page import="com.ptc.wvs.server.util.PublishUtils"%>
<%@page import="wt.representation.Representation"%>
<%@page import="java.io.File"%>
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

<%@page import="wt.change2.*, wt.query.*"%>

<%@ page import="wt.util.*, wt.org.*, wt.session.*, wt.inf.container.*, ext.narae.util.*, ext.narae.ui.*, java.util.*" %>
<%
WTUser user = (WTUser)SessionHelper.manager.getPrincipal();
String RESOURCE = "ext.narae.ui.common.resource.NaraeCommonRB";
String TYPERB = "ext.narae.ui.common.resource.RequestTypeRB";
String STOCKRB = "ext.narae.ui.common.resource.StockControlRB";
String ECOTYPERB = "ext.narae.ui.common.resource.ECOTypeRB";
Locale locale = WTContext.getContext().getLocale();
String M001 = WTMessage.getLocalizedMessage(RESOURCE , "M001", new Object[]{}, WTContext.getContext().getLocale());
String ORG_150 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_150", new Object[]{}, WTContext.getContext().getLocale());
String ORG_641 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_641", new Object[]{}, WTContext.getContext().getLocale());
String ORG_743 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_743", new Object[]{}, WTContext.getContext().getLocale());
String ORG_400 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_400", new Object[]{}, WTContext.getContext().getLocale());
String ORG_749 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_749", new Object[]{}, WTContext.getContext().getLocale());
String PRIVATE_CONSTANT_004  = WTMessage.getLocalizedMessage(RESOURCE , "STATUS", new Object[]{}, WTContext.getContext().getLocale());
%>

<input type="hidden" name="title" id="title" value="">

<link rel="stylesheet" href="/Windchill/netmarkets/jsp/narae/css/e3ps.css" />
<%
    String formName = request.getParameter("formName");
	String viewType = request.getParameter("viewType");
	if(viewType == null) viewType="";
    if(formName==null) formName = "forms[0]";
    
    String epmchk = StringUtil.checkReplaceStr(request.getParameter("epmchk"), "false");
    String oid = StringUtil.checkNull(request.getParameter("oid"));
    String moudleType = StringUtil.checkReplaceStr(request.getParameter("moudleType"), ""); //ecr //eco //epm //link
    String searchType = StringUtil.checkReplaceStr(request.getParameter("searchType"), ""); //ecr //eco //epm //link
    String mode = StringUtil.checkReplaceStr(request.getParameter("mode"), "mutil"); 		//single,mutil
    String isWG = StringUtil.checkReplaceStr(request.getParameter("isWG"), "false");
    //System.out.println("########### " + oid +":" + moudleType);
    
    Vector vecPart = new Vector();
    QueryResult rt = null;
    String tempNumber 	= ""; 
    String tempName		= "";
    String tempVersion 	= "";
    String tempOid 		= "";
    Object obj = null;
    if(oid.length()>0){
    	 obj = (Object)CommonUtil.getObject(oid);
    	if(obj instanceof WTChangeRequest2 ){
    		QueryResult result = ChangeHelper2.service.getChangeables((WTChangeRequest2)obj);
    		while( result.hasMoreElements()){
    			WTPart part = (WTPart)result.nextElement();
    			vecPart.add(part);
    		}
    	}else if(moudleType.equals("eco")){
    		QueryResult aResult = ChangeHelper2.service.getChangeActivities((WTChangeOrder2)obj);
    		QueryResult result = ChangeHelper2.service.getChangeablesAfter((WTChangeActivity2)aResult.nextElement());
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

<script type="text/javascript" >
    //select part
    var epmCheck = "<%=epmchk%>" 
    function setPartHtml( tt, data){
        tt.innerHTML = data;
    }

    function searchPart() {		//하위품목검색 팝업
    	var url = "/Windchill/netmarkets/jsp/narae/part/searchPart.jsp?mode=<%=mode%>&moudleType=<%=moudleType%>&epmchk=<%=epmchk%>&searchType=<%=searchType%>";
    	var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=no,";
        var popWidth = 1000;
        var popHeight = 600;
        var leftpos = (screen.width - popWidth) / 2;
        var toppos = (screen.height - popHeight) / 2;
        var rest = "width="+popWidth+",height="+popHeight+",left=" + leftpos + ',top=' + toppos;
        
        var newwin = window.open(url, "searchPart", opts+rest);
        newwin.focus();
        
//         addPart(newwin);
    }
    
    function selectPart() { 
    	<%if(viewType.equals("eco") || viewType.equals("ecr") ) { %>
        var url = "/Windchill/netmarkets/jsp/narae/part/searchPicker.jsp?mode=<%=mode%>&moudleType=<%=moudleType%>&epmchk=<%=epmchk%>&viewType=<%=viewType%>";
        <%} else {%>
        var url = "/Windchill/netmarkets/jsp/narae/part/searchPicker.jsp?mode=<%=mode%>&moudleType=<%=moudleType%>&epmchk=<%=epmchk%>";
    	<%}%>
    	
       var opts = "toolbar=0,loca/tion=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=0,";
       var popWidth = 1000;
       var popHeight = 800;
       var leftpos = (screen.width - popWidth) / 2;
       var toppos = (screen.height - popHeight) / 2;
       var rest = "width="+popWidth+",height="+popHeight+",left=" + leftpos + ',top=' + toppos; 
       var newwin = window.open( url , "selectPart", opts+rest);
       
//        if (newwin == undefined){
//     	   newwin = window.returnValue;
//        }
//       addPart(newwin);
    }
    function addPart(arrObj) {

    	if(epmCheck=="true"){
    		
    		numberDisplay('add');
    		
    	}
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
            partOid = subarr[0].trim();//
            partNumber = subarr[1];//
            partName = subarr[2];//
            partVersion = subarr[3];//
            partState = subarr[4];//
            pdf = subarr[5];//
            
            // 중복체크
            if(incForm.partOid) { 
                if(incForm.partOid.length) { 
                    for(var j = 0; j < incForm.partOid.length; j++) { 
                    	if(incForm.partOid[j].value == partOid) docCheck = false;
                    }
                }else {
                    if(incForm.partOid.value == partOid) docCheck = false;
                }
            }
			
            
            if(docCheck) {
            	var userRow1 = partTable.children[0].appendChild(partInnerTempTable.rows[0].cloneNode(true));
                onecell1 = userRow1.childNodes[1];
                setPartHtml(onecell1, "<input type=\"checkbox\" name=\"PartDelete\"><input type=hidden name=partOid value=\""+partOid+"\">");
                onecell2 = userRow1.childNodes[3];
                setPartHtml(onecell2, "<a href=\"JavaScript:viewPartPopup('"+partOid+"')\">" +partNumber+"</a>");
                onecell3 = userRow1.childNodes[5];
                setPartHtml(onecell3, "<nobr>"+partName+"</nobr>");
                onecell4 = userRow1.childNodes[7];
                setPartHtml(onecell4, partVersion);
                onecell5 = userRow1.childNodes[9];
                setPartHtml(onecell5, partState);
                onecell6 = userRow1.childNodes[11];
                //if (pdf.indexOf("Windchill/servlet/AttachmentsDownloadDirectionServlet") != -1) {
                if(pdf != null) {
//                 	 console.log("pdf :: "+pdf);
					pdf = "<a href=" + pdf + ">PDF다운로드</a>";
                }else{
                	pdf = "<a href=\"JavaScript:viewPartPopup2('"+partOid+"')\">재변환 요청</a><input type=\"hidden\" name=\"pdfChk\"  value=\"false\">";
                }
                setPartHtml(onecell6, pdf);
                
                if(mode == "signle"){
                	incForm.pdmNumber.value = partNumber;
        			incForm.pdmName.value = partName;
        			setTitleText(document.all.pdmNumber2 , partNumber);
        		}
            }
        }
    }
        
    function viewPartPopup2(oid){
        var str="/Windchill/netmarkets/jsp/narae/rePublish2D.jsp?oid="+oid;
        var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=0,resizable=0,";
        leftpos = (screen.width - 1000)/ 2; 
        toppos = (screen.height - 600) / 2 ; 
        rest = "width=200,height=200,left=" + leftpos + ',top=' + toppos;
        var newwin = window.open( str , "ViewEcr", opts+rest);
        newwin.focus();
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

    function createQueryStringURL(baseURL, paramObj) {

        var url = baseURL;

        var queryString = "";
        if (paramObj != null) {
                  for (var attrName in paramObj) {
                             if (queryString != "") {
                                       queryString += "&";
                             }
                             if (Object.prototype.toString.call(paramObj[attrName]) == "[object Array]") {
                                       var arrayQueryString = "";
                                       for (var i = 0; i < paramObj[attrName].length; i++) {
                                                  if (arrayQueryString != "") {
                                                             arrayQueryString += "&";
                                                  }
                                                  arrayQueryString += attrName + "=" + encodeURIComponent(paramObj[attrName][i]);
                                       }
                                       queryString += arrayQueryString;
                             } else {
                                       queryString += attrName + "=" + encodeURIComponent(getPopupParamValue(paramObj[attrName]));
                             }
                  }
        }

        if (queryString != "") {
                  if (url.indexOf("?") < 0) {
                             url += "?" + queryString;
                  } else {
                             url += "&" + queryString;
                  }
        }

        return url;
    }
    
    function openWindow(url, name, width, height) 
    { 
    	getOpenWindow(url, name, width, height);
    }
    
    function getOpenWindow(url, name, width, height)
    {
    	var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
    	if(width == 'full')
    	{
//    		rest = "width=" + (screen.availWidth-10) + ",height=" + (screen.availHeight-60)+',left=0,top=0';
    		
    		leftpos = (screen.availWidth - screen.availWidth *0.9 )/ 2; 
    		toppos = (screen.availHeight - screen.availHeight *0.9 - 30 ) / 2 ; 

    		rest = "width=" + (screen.availWidth * 0.9 ) + ",height=" + (screen.availHeight * 0.9 )+',left=' + leftpos + ',top=' + toppos;
    	}
    	else
    	{
    		leftpos = (screen.availWidth - width)/ 2; 
    		toppos = (screen.availHeight - 60 - height) / 2 ; 

    		rest = "width=" + width + ",height=" + height+',left=' + leftpos + ',top=' + toppos;
    	}
    	
    	var newwin = open( url , name, opts+rest);
    	newwin.focus();
    	return newwin;
    }
    function deletePart() {
        var incForm = document.<%=formName%>;
        if(epmCheck=="true"){
          	 numberDisplay('delete');
           }	

var len = document.getElementsByName("PartDelete").length-1;
			for(var i=len; i>=0; i--) {
				var node = document.getElementsByName("PartDelete")[i];
				if(node.checked) {
					partTable.deleteRow(i+1);
				}
			}
    }

// console.log(incForm.PartDelete);
// 		if(incForm.PartDelete) {
// 			var len = incForm.PartDelete.length;
// 			for(var i=len; i>=0; i--) {
//         		if(incForm.PartDelete[i-1]) {
//         			var checked = incForm.PartDelete[i-1].checked;
// 		        	if(checked) {
// 		        		partTable.deleteRow(i);
// 		        	}
//         		}
// 			}
// 		} else {
// 			console.log("단일삭제");
//     		if(incForm.PartDelete.checked) partTable.deleteRow(1);
// 		}
        
        
//         if(incForm.PartDelete) {
//             if(incForm.PartDelete.length) {
//                 index = incForm.PartDelete.length;

//                 for(i=index; i>=0; i--) {
//                     console.log("ㅇㄳ");
//                     console.log("partdelete[" + i + "] = " + incForm.PartDelete[i]);
//                 	if(incForm.PartDelete[i].checked) partTable.deleteRow(i+1);
//                 	console.log("ㅇㄺ");
//                 }	
//             } else {
//             	if(incForm.PartDelete.checked) partTable.deleteRow(1);
                
//             }
//         }
    
    function deleteAllPart(){
	   	var incForm = document.<%=formName%>;
	       if(incForm.PartDelete) {
	           if(incForm.PartDelete.length) {
	               index = incForm.PartDelete.length-1;
	
	               for(i=index; i>=0; i--) {
	                  partTable.deleteRow(i+1);
	               }
	           } else {
	                 partTable.deleteRow(1);
	           }
	       }
    }

	window.onLoad = function(){
		return true;
	}
	
	function cancelPart(){
		var arrObj = new Array();
		var subarr = new Array();
		subarr[0] = "<%=tempOid%>";
		subarr[1] = "<%=tempNumber%>"
		subarr[2] = "<%=tempName%>"
		subarr[3] = "<%=tempVersion%>"
		arrObj[0] = subarr;
		
		addPart(arrObj);
	}
	function republish(oid){
		var sURL = "/Windchill/netmarkets/jsp/republish.jsp?oid=" + oid;    	
  	
    	var sName = "";
    	var nWidth = 10;
    	var nHeight = 10;
    	var bMoveCenter = true;
    	var bStatus = true;
    	var bScrollbars = true;
    	var bResizable = true;

    	return openWindow(sURL, sName, nWidth, nHeight, bMoveCenter, bStatus, bScrollbars, bResizable);
	}
	function republishPDF(changeOid,drawingOid){
		var sURL = "/Windchill/jsp/republishPDF.jsp?changeOid=" + changeOid+"&drawingOid="+drawingOid;
    	
  	
    	var sName = "";
    	var nWidth = 10;
    	var nHeight = 10;
    	var bMoveCenter = true;
    	var bStatus = true;
    	var bScrollbars = true;
    	var bResizable = true;

    	return openWindow(sURL, sName, nWidth, nHeight, bMoveCenter, bStatus, bScrollbars, bResizable);
	}
	
	function setButtonTagNarae(name, size, javaAction, a_Flag) {
		var returnStr = "<a style='FONT-SIZE: 8pt;' onclick='" + 
			javaAction +
			"'><table class='' border='0' cellpadding='0' cellspacing='0' width='"+
			size +
			"'><tbody><tr><td width='7'><img src='/Windchill/netmarkets/jsp/narae/portal/img/btn_left.gif' alt='' height='20' width='7'></td><td background='/Windchill/netmarkets/jsp/narae/portal/img/btn_mid.gif' valign='middle'><table align='center' border='0' cellpadding='0' cellspacing='0'><tbody><tr><td><div id='_text' align='center'>" +
			name + 
			"</div></td></tr></tbody></table></td><td width='12'><img src='/Windchill/netmarkets/jsp/narae/portal/img/btn_right.gif' alt='' height=20 width=12></td></tr></tbody></table></a>";
		document.write(returnStr);
	}
</script>

<table id="partInnerTempTable" style="display:none">
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
    <%if("false".equals(isWG)){ %>
    <tr>
	    <% 
		if (viewType == null ) viewType = "";
		if( !viewType.equals("view") ) { 
		%>
        <td>
        	<a onclick="selectPart();">
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
        	<a style="FONT-SIZE: 8pt;" onclick="deletePart();">
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
        <td>
        	<a style="FONT-SIZE: 8pt;" onclick="searchPart();">
				<table class="" border="0" cellpadding="0" cellspacing="0" width="100">
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
								<div id="_text" align="center"><%=WTMessage.getLocalizedMessage(RESOURCE , "ADD_CHILD", new Object[]{}, locale)%></div>
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
        	<a style="FONT-SIZE: 8pt;" onclick="cancelPart();">
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
								<div id="_text" align="center">취소</div>
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
    </tr>
    <%} %>
<% } %>
</table>
<!-- div style="width:99%;overflow-x:hidden;overflow-y:auto;border:1px;border-style:solid;border-color:#5F9EA0;padding:0px;margin:1px 1px 5px;"-->
    <table width="100%" cellspacing="0" cellpadding="1" border="0" id="partTable" align="center">
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
            	
            	String buffer = "";
                EPMDocument modelEpm = DrawingHelper2.getEPMDocument(part);
                List<EPMDocument> drawingEpm = null;
                
                
                if( modelEpm != null ) {
//                 	System.out.println("modelEpm.getNumber()="+modelEpm.getNumber()+"\tver ="+modelEpm.getVersionIdentifier().getValue()+"."+modelEpm.getIterationIdentifier().getValue());
                	EPMDocument one2D = null;
					try{
						one2D = getRelational2DCad(modelEpm,modelEpm.getVersionIdentifier().getValue());
					}catch(NullPointerException e){
						one2D = DrawingHelper2.getRelational2DCad(modelEpm);
					}
                	if( one2D != null ) {
//                 		System.out.println("2d modelEpm.getNumber()="+one2D.getNumber()+"\tver ="+one2D.getVersionIdentifier().getValue()+"."+one2D.getIterationIdentifier().getValue());
                		boolean isPublish = false;
                		Representation representation = PublishUtils.getRepresentation(one2D);
//                         System.out.println("representation" + representation);
                        if(null==representation){
							String publishURL = "Javascript:republish('"+CommonUtil.getOIDString(one2D)+"');";
                        	buffer = buffer + "<br><a target=\"ContentFormatIconPopup\"  onclick=\""+publishURL+"\">&nbsp;"+"(변환 요청.)</a>";
                        }else{
                        	boolean isExist2DPDFFile = false;
                    		String oldPublishpdfFileURL = DrawingHelper2.getPDFFile(one2D, modelEpm.getNumber());
                    		String pdfFileURL = getPDFFile(one2D, modelEpm.getNumber());
                    		String pdfFileName = getPDFFileName(one2D, modelEpm.getNumber());
    						buffer = buffer +"구 변환된 버전 : "+ oldPublishpdfFileURL;
    						if(null!=pdfFileName&&pdfFileName.split("[.]").length==4){
    							buffer = "신규 변환된 버전 : "+pdfFileURL;
    							isPublish = true;
    						}else{
    							isPublish = false;
    							String publishURL = "Javascript:republish('"+CommonUtil.getOIDString(one2D)+"');";
                            	buffer = buffer + "<br><a target=\"ContentFormatIconPopup\"  onclick=\""+publishURL+"\">&nbsp;"+"(변환 요청.)</a>";
    						}
                    		String nm = "-";
                    		//String target = ERPUtil.PDF_FOLDER + File.separator + getTargetFolder(eco.getPersistInfo().getUpdateStamp());
                    	    //String dwgpath = "D:\\temp\\dwgtopdf" + File.separator + eco.getNumber();
                    	    if(obj instanceof WTChangeOrder2){
                    	    	WTChangeOrder2 eco = (WTChangeOrder2)obj;
                    			String target = ERPUtil.PDF_FOLDER + File.separator + ERPPdfSender.getTargetFolder(eco.getPersistInfo().getUpdateStamp());
                    			File dir = new File(target);
                    			File[] files =  dir.listFiles();
                    			
                    			if(null!=files && files.length>0){
                    				for(int idx =0 ;idx<files.length;idx++){
                    					File file = files[idx];
                    					//NA-A1-243500-007.A.1.pdf
                    					String fileName = file.getName().toUpperCase();
//                     					System.out.println("target list Check :::: fileName = "+ fileName+"\tpdfFileName="+pdfFileName.toUpperCase()+"\tcheck = "+pdfFileName.toUpperCase().contains(fileName)+"<br>");
                    					if(null!=file && pdfFileName.toUpperCase().contains(fileName)){
                    						if(CommonUtil.isAdmin()){
//                 								System.out.println("Success!!!!! target list Check :::: fileName = "+ fileName+"\tpdfFileName="+pdfFileName.toUpperCase()+"\tcheck = "+pdfFileName.toUpperCase().contains(fileName)+"<br>");
                							}
                    						isExist2DPDFFile = true;
                    					}
                    				}
                    			}
                    	    }
    						if(!isExist2DPDFFile){
    							//String publishURL = "/Windchill/wtcore/jsp/wvs/edrview.jsp?&viewIfPublished=1&sendToPublisher="+one2D.getPersistInfo().getObjectIdentifier().getStringValue();
    							String publishPDFURL = "Javascript:republishPDF('"+oid+"','"+CommonUtil.getOIDString(one2D)+"');";
    							buffer = buffer + "<br>SAP Not Send File. SAP Network 2D Not Drive exites";
    							if(isPublish){
    								buffer = buffer + "<br><a target=\"ContentFormatIconPopup\"  onclick=\""+publishPDFURL+"\">&nbsp;"+"(PDF SAP 재전송 요청.)</a>";
    							}
    							
    						}
                        }
                		
                	}else{
                		buffer = "2D Drawing Not exits.";
                	}
                	
                }else{
                	buffer = "3D Drawing Not exits.";
                }
            	
            %>
            <tr>
            	<%if(viewType.equals("view")) {%>
            	<td class="tdwhiteM" width="30%" colspan="2"><a href="JavaScript:viewPartPopup('<%=part.getPersistInfo().getObjectIdentifier().toString()%>')"><%=part.getNumber() %></a></td>
            	<%} else { %>
            	<td class="tdwhiteM"><input type="checkbox" name="PartDelete"><input type=hidden name=partOid value=<%=CommonUtil.getOIDString(part) %>></td>
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
<%!
public String stripHtml(String html) {
	String strRegEx = "<[^>]*>";
	return html.replaceAll(strRegEx, ""); 
}
public static String getPDFFileName(EPMDocument epm, String modelNumber) {
    String oid = epm.getPersistInfo().getObjectIdentifier().getStringValue();
    String pdfFile = "&nbsp;";
    try
    {
    	 if (("PROE".equals(epm.getAuthoringApplication().toString())) && ("CADDRAWING".equals(epm.getDocType().toString()))) {
       	  QueryResult qr = ContentHelper.service.getContentsByRole (epm ,ContentRoleType.SECONDARY );
   	      	while(qr.hasMoreElements()) {
   	  			ContentItem item = (ContentItem) qr.nextElement ();
   	  			
   	  			if(item != null) {
   	  				ApplicationData data = (ApplicationData)item;
   	  				if(data.getFileName().toLowerCase().matches("^.*.(pdf){1}$")){
   	  					System.out.println("ADD data.getFileName()="+data.getFileName());
   	  					String nUrl = "/Windchill/servlet/AttachmentsDownloadDirectionServlet?oid=" + oid + 
   	  		                  "&cioids=" + data.getPersistInfo().getObjectIdentifier().getStringValue() + "&role=SECONDARY";
     		                pdfFile = data.getFileName();
   	  				}
   	  			}
   	  		}
         }
         else
         {
           QueryResult result = ContentHelper.service.getContentsByRole(epm, ContentRoleType.SECONDARY);
           ContentItem tempitem = null;
           while (result.hasMoreElements()) {
             tempitem = (ContentItem)result.nextElement();
             ApplicationData pAppData = (ApplicationData)tempitem;

             if ((pAppData.getDescription() == null) || (pAppData.getDescription().equals("N")))
               continue;
             String nUrl = "/Windchill/servlet/AttachmentsDownloadDirectionServlet?oid=" + epm.getPersistInfo().getObjectIdentifier().getStringValue() + 
               "&cioids=" + pAppData.getPersistInfo().getObjectIdentifier().getStringValue();
             String cadFile =  pAppData.getFileName() ;
             return cadFile;
           }
         }

    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return pdfFile;
  }

public static String getPDFFile(EPMDocument epm, String modelNumber) {
    String oid = epm.getPersistInfo().getObjectIdentifier().getStringValue();
    String pdfFile = "&nbsp;";
    try
    {
      if (("PROE".equals(epm.getAuthoringApplication().toString())) && ("CADDRAWING".equals(epm.getDocType().toString()))) {
    	  QueryResult qr = ContentHelper.service.getContentsByRole (epm ,ContentRoleType.SECONDARY );
	      	while(qr.hasMoreElements()) {
	  			ContentItem item = (ContentItem) qr.nextElement ();
	  			
	  			if(item != null) {
	  				ApplicationData data = (ApplicationData)item;
	  				if(data.getFileName().toLowerCase().matches("^.*.(pdf){1}$")){
// 	  					System.out.println("ADD data.getFileName()="+data.getFileName());
	  					String nUrl = "/Windchill/servlet/AttachmentsDownloadDirectionServlet?oid=" + oid + 
	  		                  "&cioids=" + data.getPersistInfo().getObjectIdentifier().getStringValue() + "&role=SECONDARY";
	  					pdfFile = "<a target=\"ContentFormatIconPopup\" href='" + nUrl + "' ext:qtip=\"" + data.getFileName() + "\"><img src=\"/Windchill/netmarkets/jsp/narae/portal/img/pdf.gif\" border=\"0\"></a>";
	  				}
	  			}
	  		}
      }
      else
      {
        QueryResult result = ContentHelper.service.getContentsByRole(epm, ContentRoleType.SECONDARY);
        ContentItem tempitem = null;
        while (result.hasMoreElements()) {
          tempitem = (ContentItem)result.nextElement();
          ApplicationData pAppData = (ApplicationData)tempitem;

          if ((pAppData.getDescription() == null) || (pAppData.getDescription().equals("N")))
            continue;
          String nUrl = "/Windchill/servlet/AttachmentsDownloadDirectionServlet?oid=" + epm.getPersistInfo().getObjectIdentifier().getStringValue() + 
            "&cioids=" + pAppData.getPersistInfo().getObjectIdentifier().getStringValue();
          String cadFile = "<a target=\"ContentFormatIconPopup\" href='" + nUrl + "'>&nbsp;" + pAppData.getFileName() + "</a>";
          return cadFile;
        }
      }

    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

    return pdfFile;
  }
public EPMDocument getRelational2DCad(EPMDocument epm,String ver) throws Exception {
    EPMDocument epm2d = null;
//	System.out.println("sssssssssssssssssfafadsfsadfasfsafdasdf");
    if ((epm != null) && ((epm instanceof EPMDocument)) && 
      (!checkDrawing(epm))) {
//	  System.out.println("111111111111111");
      QuerySpec spec = new QuerySpec(EPMDocument.class);
      LatestConfigSpec lSpec = new LatestConfigSpec();
      spec = lSpec.appendSearchCriteria(spec);
      QueryResult result = EPMStructureHelper.service.navigateReferencedBy((EPMDocumentMaster)epm.getMaster(), spec, false);

      EPMReferenceLink object = null;
      EPMDocument doc = null;
      String version = null;
      HashMap aHash = new HashMap();
      while (result.hasMoreElements()) {
	  
	  
	//	System.out.println("1222222222222222222");
        object = (EPMReferenceLink)result.nextElement();
        doc = object.getReferencedBy();
	//	System.out.println("11111111111      doc Number ="+doc.getNumber()+"\tversion"+version+"<br>ver = "+ ver);
        if (object.getDepType() == 4) {
          version = doc.getVersionInfo().getIdentifier().getValue();
    //     System.out.println("ss sssssssssssss         doc Number ="+doc.getNumber()+"\tversion"+version+"<br>ver = "+ ver);
          if(!doc.getNumber().contains(epm.getNumber())) continue;
          for (int index = version.length(); index < 10; index++) version = "0" + version;
	//	  System.out.println("sSDFSDFSDFSFSDFSDFSDF       doc Number ="+doc.getNumber()+"\tversion"+version+"<br>ver = "+ ver);
          aHash.put(version, doc);
        }

      }

      if (aHash.size() > 0) {
        //String hashKey = getLatestVersion(aHash);
		String version1 = version.substring(0,version.length()-1);
// 		System.out.println("sSDFSDFSDFSFSDFSDFSDF       doc Number ="+doc.getNumber()+"\tversion1 = "+version1);
        epm2d = (EPMDocument)aHash.get(version1+ver);
//         System.out.println("sssssssssssssssss doc Number ="+epm2d.getNumber()+"\t"+ver+"<br>");
      }
    }

    return epm2d;
  }
public boolean checkDrawing(EPMDocument epm) throws Exception
{
  return epm.getDocType().getStringValue().substring(epm.getDocType().getStringValue().lastIndexOf(".") + 1).equals("CADDRAWING");
}
%>