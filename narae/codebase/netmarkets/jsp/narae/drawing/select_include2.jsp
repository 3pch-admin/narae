<%@page import="ext.narae.service.part.beans.PartSearchHelper"%>
<%@page import="ext.narae.service.drawing.beans.EpmSearchHelper"%>
<%@page import="ext.narae.service.drawing.beans.DrawingHelper"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@page contentType="text/html; charset=UTF-8"%>

<%@page import="java.util.Vector"%>
<%@page import="wt.epm.EPMDocument,
						wt.epm.EPMDocumentMaster,
						wt.fc.QueryResult,
						wt.part.WTPart"%>

<link rel="stylesheet" href="/Windchill/netmarkets/jsp/narae/css/e3ps.css" />
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
%>

<input type="hidden" name="title" id="title" value="">

<%
    String formName = request.getParameter("formName");
    if(formName==null) formName = "forms[0]";
    String mode = StringUtil.checkReplaceStr(request.getParameter("mode"), "multi");
    String partchk = StringUtil.checkReplaceStr(request.getParameter("partchk"), "false");
    String oid = StringUtil.checkNull(request.getParameter("oid"));
    String type = StringUtil.checkReplaceStr(request.getParameter("type"),"reference");
    String isWG = StringUtil.checkReplaceStr(request.getParameter("isWG"), "false");
    String REFDWGchk = StringUtil.checkReplaceStr(request.getParameter("REFDWGchk"), "false");
	String gpartchk = StringUtil.checkReplaceStr(request.getParameter("gpartchk"), "");
	
	String epmchk = "false";
	String moudleType = "epm";
	String viewType = "epm";
    Vector epmVec = new Vector();
//    System.out.println("oid :" +oid);
   System.out.println("type :" + type);
    
    String tempNumber 	= ""; 
    String tempName		= "";
    String tempVersion 	= "";
    String tempOid 		= "";
    if(oid.length()>0){
    	
    	if(type.equals("reference")){
/*    		QueryResult rt = EpmSearchHelper.manager.getWTDocEpmlink(CommonUtil.getObject(oid));	
    		while(rt.hasMoreElements()){
    			
    			EPMDocumentMaster master = (EPMDocumentMaster)rt.nextElement();
    			EPMDocument epm = EpmSearchHelper.manager.getLastEPMDocument(master);
    			epmVec.add(epm);
    		}
*/    		
    	}else if(type.equals("buildRole")){
    		WTPart part = (WTPart)CommonUtil.getObject(oid);
    		EPMDocument epm = DrawingHelper.manager.getEPMDocument(part);
    		if(epm != null){
    			tempNumber = epm.getNumber();
        		tempName = epm.getName();
        		tempVersion = epm.getVersionIdentifier().getValue();
        		tempOid = CommonUtil.getOIDString(epm);
        		epmVec.add(epm);
    		}
    	}else if(type.equals("REFDWG")){
    		EPMDocument epm = (EPMDocument)CommonUtil.getObject(oid);
    		if(epm != null){
    			EPMDocument REFDWG = EpmSearchHelper.manager.getREFDWG(epm);
    			if(REFDWG != null) {
	    			tempNumber = REFDWG.getNumber();
	        		tempName = REFDWG.getName();
	        		tempVersion = REFDWG.getVersionIdentifier().getValue();
	        		tempOid = CommonUtil.getOIDString(REFDWG);
	        		epmVec.add(REFDWG);
	        	}
    		}
    	}else if(type.equals("gPart")){
    		WTPart part = (WTPart)CommonUtil.getObject(oid);
			PartSearchHelper searchHelper = new PartSearchHelper();
		    EPMDocument gPartEpm = searchHelper.getGPartDWG(part);

    		if(gPartEpm != null){
    			tempNumber = gPartEpm.getNumber();
        		tempName = gPartEpm.getName();
        		tempVersion = gPartEpm.getVersionIdentifier().getValue();
        		tempOid = CommonUtil.getOIDString(gPartEpm);
        		epmVec.add(gPartEpm);
    		}
    	}
    	
    }
%>
<script type="text/javascript">
    function setPartHtml( tt, data){
        tt.innerHTML = data;
    }

    function searchPart() {		//하위품목검색 팝업
<%--     	var url = "/Windchill/netmarkets/jsp/narae/part/searchPart2.jsp?mode=<%=mode%>&moudleType=<%=moudleType%>&epmchk=<%=epmchk%>"; --%>
//         attache = window.showModalDialog(url,window,"help=no; scroll=no; resizable=no; dialogWidth=800px; dialogHeight:600px; center:yes");
      
//         if(typeof attache == "undefined" || attache == null) {
//             return;
//         }
//         addEpm(attache);
        
    	var url = "/Windchill/netmarkets/jsp/narae/part/searchPart2.jsp?mode=<%=mode%>&moudleType=<%=moudleType%>&epmchk=<%=epmchk%>";
    	var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=no,";
        var popWidth = 1000;
        var popHeight = 600;
        var leftpos = (screen.width - popWidth)/ 2;
        var toppos = (screen.height - popHeight) / 2 ;
        var rest = "width="+popWidth+",height="+popHeight+",left=" + leftpos + ',top=' + toppos;
        
        var newwin = window.open(url, "searchPart", opts+rest);
        newwin.focus();
        
        addEpm(newwin);
    }
    
    function selectPart() { 
    	<%if(viewType.equals("eco") || viewType.equals("ecr") ) { %>
        var url = "/Windchill/netmarkets/jsp/narae/part/searchPicker2.jsp?mode=<%=mode%>&moudleType=<%=moudleType%>&epmchk=<%=epmchk%>&viewType=<%=viewType%>";
        <%} else {%>
        var url = "/Windchill/netmarkets/jsp/narae/part/searchPicker2.jsp?mode=<%=mode%>&moudleType=<%=moudleType%>&epmchk=<%=epmchk%>";
    	<%}%>
//         if(typeof attache == "undefined" || attache == null) {
//             return;
//         }
    	var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=no,";
        var popWidth = 1000;
        var popHeight = 600;
        var leftpos = (screen.width - popWidth)/ 2;
        var toppos = (screen.height - popHeight) / 2 ;
        var rest = "width="+popWidth+",height="+popHeight+",left=" + leftpos + ',top=' + toppos;
        
        var newwin = window.open(url, "selectPart", opts+rest);
        newwin.focus();

//        addEpm(newwin);
    }
    //select Epm
    var partCheck = "<%=partchk%>"
    function setEpmHtml( tt, data1){
        tt.innerHTML = data1;
    }

    function selectEpm() {
      var url = "/Windchill/netmarkets/jsp/narae/drawing/drawingPicker2.jsp?mode=<%=mode%>&partchk=<%=partchk%>&REFDWGchk=<%=REFDWGchk%>&gpartchk=<%=gpartchk%>";

//         attache = window.showModalDialog(url,window,"help=no; scroll=yes; resizable=yes; dialogWidth=1300px; dialogHeight:650px; center:yes");
       // console.log(attache);
       var opts = "toolbar=0,loca/tion=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=0,";
       var popWidth = 1400;
       var popHeight = 800;
       var leftpos = (screen.width - popWidth)/ 2;
       var toppos = (screen.height - popHeight) / 2 ;
       var rest = "width="+popWidth+",height="+popHeight+",left=" + leftpos + ',top=' + toppos; 
       var newwin = window.open( url , "selectfolder", opts+rest);
       
//        if(typeof attache == "undefined" || attache == null) {
//             return;
//         }
//         addEpm(attache);
    }
    function addEpm(arrObj) {
        
    	if(partCheck=="true"){
    		
    		numberDisplay('add');
    		
    	}
    	var incForm = document.<%=formName%>;
    	var fName = "<%=formName%>";
		var mode = "<%=mode%>";
		
		if(mode == "signle"){
			deleteAllEpm();
		}
		
        if(!arrObj.length) {
            return;
        }

        var epmOid;//
        var epmNumber;//
        var epmName;//
        var epmVersion;//
        var dwg;
        var pdf;
        var t1;
        var t2;
        var chkbox;
        var docCheck = true;
		
        for(var i = 0; i < arrObj.length; i++) {
        	
            docCheck = true;
            subarr = arrObj[i];
            epmOid = subarr[0].trim();//
            epmNumber = subarr[1].trim();//
            epmName = subarr[2];//
            epmVersion = subarr[3];//
            dwg = subarr[4];//
            pdf = subarr[5];//
            dwg = dwg.trim();
            pdf = pdf.trim();
            
          //  alert('dwg :'+dwg+'/ pdf :'+pdf);
//             chkbox = "<input type=\"checkbox\" name=\"EpmDelete\" value=\""+epmOid+"\"><input type=\"hidden\" id=\"test11\" name=\"test11\" value=\"\">";
            chkbox = "<input type=\"checkbox\" name=\"EpmDelete\"><input type=\"hidden\" id=\"epmOid\" name=\"epmOid\" value=\""+epmOid+"\">";
            if(dwg == "null" || dwg == ""){
            	t1 = "X";
            }else{
            	t1 =  "<a href=\"" + dwg + "\">Download</a>";
            }
            if(pdf == "null" || pdf == ""){
            	t2 = "X";
//             	chkbox="<input type=\"checkbox\" type=\"checkbox\" name=\"EpmDelete\" value=\"false\"><input type=\"hidden\" id=\"epmOid\" name=\"epmOid\" value=\""+epmOid+"\">";
            	chkbox="<input type=\"checkbox\" type=\"checkbox\" name=\"EpmDelete\" value=\"false\"><input type=\"hidden\" id=\"epmOid\" name=\"epmOid\" value=\""+epmOid+"\">";
            }else{
            	t2 =  "<a href=\"" + pdf + "\">Download</a>";
            }
            
            // 중복체크
//             if(incForm.epmOid) {
//                 if(incForm.epmOid.length) {
//                 	for(var j = 0; j < incForm.epmOid.length; j++) {
//                         if(incForm.epmOid[j].value == epmOid) docCheck = false;
//                     }
//                 }else {
//                     if(incForm.epmOid.value == epmOid) docCheck = false;
//                 }
//             }
            
            if(docCheck) {
                var userRow1 = epmTable.children[0].appendChild(epmInnerTempTable.rows[0].cloneNode(true));
                onecell1 = userRow1.childNodes[1];
                setEpmHtml(onecell1, chkbox);
                onecell2 = userRow1.childNodes[3];
                setEpmHtml(onecell2, epmNumber);
                onecell3 = userRow1.childNodes[5];
                setEpmHtml(onecell3, "<nobr>"+epmName+"</nobr>");
				onecell4 = userRow1.childNodes[7];
                setEpmHtml(onecell4, epmVersion);
                onecell5 = userRow1.childNodes[9];
                setEpmHtml(onecell5, t1);
                onecell6 = userRow1.childNodes[11];
                setEpmHtml(onecell6, t2);
                
               // alert(epmNumber+epmName+epmVersion);
                if(mode == "signle"){
                	if("<%=REFDWGchk%>" == "false" & "<%=gpartchk%>" == "") {
	        			incForm.pdmNumber.value = epmNumber;
	        			incForm.pdmName.value = epmName;
	        			setTitleText(document.all.pdmNumber2 , epmNumber);
        			}
        		}
            }
        }
    }

    function deleteEpm() {
        var incForm = document.<%=formName%>;
        if(partCheck=="true"){
          	 numberDisplay('delete');
           }	

var len = document.getElementsByName("EpmDelete").length-1;
			for(var i=len; i>=0; i--) {
				var node = document.getElementsByName("EpmDelete")[i];
				if(node.checked) {
					epmTable.deleteRow(i+1);
				}
			}
    }
    
    function deleteAllEpm(){
	   	var incForm = document.<%=formName%>;
	       if(incForm.EpmDelete) {
	           if(incForm.EpmDelete.length) {
	               index = incForm.EpmDelete.length-1;
	
	               for(i=index; i>=0; i--) {
	            		   epmTable.deleteRow(i+1);
	            	   }
	       }
    }
    }
    
//     function deleteAllEpm(){
<%-- 	   	var incForm = document.<%=formName%>; --%>
// 	       if(incForm.EpmDelete) {
// 	           if(incForm.EpmDelete.length) {
// 	               index = incForm.EpmDelete.length-1;
	
// 	               for(i=index; i>=0; i--) {
// 	                  epmTable.deleteRow(i+1);
// 	               }
// 	           } else {
// 	                 epmTable.deleteRow(1);
// 	           }
// 	       }
//     }

	window.onLoad = function(){
		return true;
	}
	
	function cancelEpm(){
		var arrObj = new Array();
		var subarr = new Array();
		subarr[0] = "<%=tempOid%>";
		subarr[1] = "<%=tempNumber%>";
		subarr[2] = "<%=tempName%>";
		subarr[3] = "<%=tempVersion%>";
		arrObj[0] = subarr;
		
		addEpm(arrObj);
	}
	
	function AllCheck() {
		var incForm = document.<%=formName%>;
		if(incForm.AllCheck.checked){
			for(i=0; i < incForm.EpmDelete.length; i++) {
	    		incForm.EpmDelete[i+1].checked = true;
	    	}
		}else{
			for(i=0; i < incForm.EpmDelete.length; i++) {
	    		incForm.EpmDelete[i].checked = false;
	    	}
		}
	}
</script>
<input type="hidden" id="items">
<table id="epmInnerTempTable" style="display:none">
    <tr>
        <td class="tdwhiteM">N/D</td>
        <td class="tdwhiteM">N/D</td>
        <td class="tdwhiteM">N/D</td>
        <td class="tdwhiteM">N/D</td>
        <td class="tdwhiteM">N/D</td>
        <td class="tdwhiteM">N/D</td>
    </tr>
</table>
<div style="float: left; width: 49%" >
<table border="0" cellpadding="0" cellspacing="2">
    <tr>
    	<td id="registBtn">
    		<input type="hidden" name="test11" id="test11"> 
			<a onclick="javascript:selectEpm()" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="50"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center"><%=ORG_150%></div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
		<td id="registBtn">
			<a onclick="javascript:deleteEpm()" style="cursor:hand;" ><table class="" border="0" cellpadding="0" cellspacing="0" width="50"><tbody><tr><td width="8"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif" alt="" height="22" width="8"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center"><%=ORG_641%></div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif" alt="" height="22" width="12"></td></tr></tbody></table></a></td>
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
</table>
</div>
<div style="float: right; width: 49%" >
   	<input onclick="printEpm()" type="button" value="프린트" title="프린트" id="printEPM" style="width:50px; height:20px; font-size: medium;">
<!--    	<input onclick="deleteNoPdf()" type="button" value="일괄삭제" title="일괄삭제" style="width:70px; height:20px; font-size: medium;"> -->
   	<input onclick="deleteAllEpm()" type="button" value="일괄삭제" title="일괄삭제" style="width:70px; height:20px; font-size: medium;">
</div>

<div style="width:99%;overflow-x:hidden;overflow-y:auto;border:1px;border-style:solid;border-color:#5F9EA0;padding:0px;margin:1px 1px 5px;">
    <table width="100%" cellspacing="0" cellpadding="1" border="0" id="epmTable" align="center">
        <tbody>
            <tr>
            	<td class="tdblueM" width="5%">
            	<input type="checkbox" name="AllCheck"  id="AllCheck" onclick="AllCheck()">
            	</td>
                <td class="tdblueM" width="30%"><%=ORG_743%></td>
                <td class="tdblueM" width="35%"><%=ORG_400%></td>
                <td class="tdblueM" width="10%"><%=ORG_749%></td>
                <td class="tdblueM" width="10%">DWG</td>
                <td class="tdblueM" width="10%">PDF</td>
            </tr>
            
            <% for(int i = 0 ; i<epmVec.size(); i++){
            	EPMDocument epm = (EPMDocument)epmVec.get(i);
            %>
            <tr>
<%--             	<td><input type=checkbox name="EpmDelete" value="<%=CommonUtil.getOIDString(epm) %>"></td> --%>
            	<td class="tdwhiteM"><input type="checkbox" name="EpmDelete"><input type=hidden name=EpmOid value=<%=CommonUtil.getOIDString(epm) %>></td>
            	<td><%=epm.getNumber() %></td>
            	<td><%=epm.getName() %></td>
            	<td><%=epm.getVersionIdentifier().getValue() %></td>
            </tr>
            <%} %>
        </tbody>
    </table>
</div>
<script>
// function AllCheck() {
<%-- 	var incForm = document.<%=formName%>; --%>
	

// 	if(incForm.AllCheck.checked){
// 		for(i=0; i < incForm.EpmDelete.length; i++) {
//     		incForm.EpmDelete[i+1].checked = true;
//     	}
// 	}else{
// 		for(i=0; i < incForm.EpmDelete.length; i++) {
//     		incForm.EpmDelete[i].checked = false;
//     	}
// 	}
// }
</script>