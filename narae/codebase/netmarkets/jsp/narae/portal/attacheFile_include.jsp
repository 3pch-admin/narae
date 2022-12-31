<%@page import="wt.epm.EPMDocument"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@page import="java.util.*,wt.content.*,wt.fc.*,wt.query.*" %>
<script language=JavaScript  src="/netmarkets/jsp/narae/js/common.js"></script>
<%@ page import="wt.util.*, wt.org.*, wt.session.*, wt.inf.container.*, ext.narae.util.*, ext.narae.ui.*, java.util.*" %>
<%
String RESOURCE = "ext.narae.ui.common.resource.NaraeCommonRB";
Locale locale = WTContext.getContext().getLocale();
String ORG_150 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_150", new Object[]{}, WTContext.getContext().getLocale());
String ORG_641 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_641", new Object[]{}, WTContext.getContext().getLocale());
String ORG_1035 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_1035", new Object[]{}, WTContext.getContext().getLocale());
String ORG_549 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_549", new Object[]{}, WTContext.getContext().getLocale());
String ORG_112 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_112", new Object[]{}, WTContext.getContext().getLocale());
String ORG_113 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_113", new Object[]{}, WTContext.getContext().getLocale());

/**
out.println("ORG_150=" + ORG_150 + "<br>");
out.println("ORG_641=" + ORG_641 + "<br>");
out.println("ORG_1035=" + ORG_1035 + "<br>");
out.println("ORG_549=" + ORG_549 + "<br>");
out.println("ORG_112=" + ORG_112 + "<br>");
out.println("ORG_113=" + ORG_113 + "<br>");
**/
%>
<%
	// command : 첨부파일 Action type 
	// insert : 신규 등록
	// update : 수정
	
	String form = "";
	String command = "";
	String oid = "";
	String type = "";
	String ac = "";
	String viewType = "";

	form = request.getParameter("form");
	command = request.getParameter("command");
	oid = request.getParameter("oid");
	type = request.getParameter("type");
	ac = request.getParameter("count");
	viewType = request.getParameter("viewType");
	if( viewType == null ) viewType = "";
	
	String isWG = request.getParameter("isWG");
	
	// command 가 update시에 update 할 ContentHolder OID

	// type : 첨부파일 타입
	// p or primary : (Primary)주요문서 파일
	// s or secondary : (Secondary)참조문서 파일
	if ( "p".equalsIgnoreCase(type) || "primary".equalsIgnoreCase(type)) type = "primary";
	else type = "secondary";
	
	// (Secondary)참조문서 파일일 경우 첨부할수 있는 갯수
	int attacheCount = 0;

	if(ac!=null && ac.length()>0){
		 attacheCount = Integer.parseInt(ac);
	}
	
	// (Secondary)참조문서 파일일 경우 설명 사용여부
	String desc = request.getParameter("desc");
	boolean canDesc = false;
	if ( "t".equalsIgnoreCase(desc) || "true".equalsIgnoreCase(desc) ) canDesc = true;

	ContentItem primaryFile = null;
	ContentHolder holder = null;
	if ( "primary".equals(type) && "update".equalsIgnoreCase(command) ) {
		ReferenceFactory rf = new ReferenceFactory();
		holder = (ContentHolder)rf.getReference(oid).getObject();
		if ( holder instanceof FormatContentHolder ) {
			QueryResult result = ContentHelper.service.getContentsByRole (holder ,ContentRoleType.PRIMARY );
			if (result.hasMoreElements ()) {
				primaryFile = (ContentItem) result.nextElement ();
			}
		} else if ( holder instanceof ContentHolder ) {	//경쟁 제품 용(ContentHolder 일 경우)
			QueryResult result = ContentHelper.service.getContentsByRole (holder ,ContentRoleType.PRIMARY );
			if (result.hasMoreElements ()) {
				primaryFile = (ContentItem) result.nextElement ();
			}
		}
	}

	QueryResult secondaryFiles =null;
	int deleteFileCnt = 0;
	if ( "secondary".equals(type) && "update".equalsIgnoreCase(command) ) {
		ReferenceFactory rf = new ReferenceFactory();
		holder = (ContentHolder)rf.getReference(oid).getObject();
		secondaryFiles = ContentHelper.service.getContentsByRole (holder ,ContentRoleType.SECONDARY );
	}
%>
<!-- // 파일 첨부 시작 //-->
<%	if ( "primary".equals(type) ) {	%>
<SCRIPT LANGUAGE="JavaScript">

<!--
function checkPrimary() {
	if(document.<%=form%>.<%=type%>.value == "") {
		alert("<%=ORG_112%>");
		return false;
	} else {
		return true;
	}
}

// --- 주요파일 변경하기 -----//
function fcFileShowHide() {
	if (document.<%=form%>.ImgFileChange.value == 'Show') {
		FileShow.style.display = '';
		FileHide.style.display = 'none';
		document.<%=form%>.ImgFileChange.value = 'Hide';
		document.<%=form%>.ImgFileChange.src = 'images/img_default/button/board_btn_file_edit_no.gif';
	} else {
		FileShow.style.display = 'none';
		FileHide.style.display = '';
		document.<%=form%>.ImgFileChange.value = 'Show';
		document.<%=form%>.ImgFileChange.src = 'images/img_default/button/board_btn_file_edit.gif';
	}
}
//-->
//-- 파일 첨부 끝
//-->
</SCRIPT>
<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>	
<%	if ( command.equals("insert") ) {	%>
		<td align="center">
			<input type="file" name="<%=type%>" id=input style="width:100%">
		</td>
<%	
	} else {
		if ( primaryFile != null ) {
			
			//String downURL = "";
			String nUrl = "";
			if(primaryFile instanceof ApplicationData){
				//downURL = ContentHelper.getDownloadURL ( holder , (ApplicationData)primaryFile ).toString();
				nUrl = "/plm/servlet/DownloadGW?holderOid="+CommonUtil.getOIDString(holder)+"&appOid="+CommonUtil.getOIDString(primaryFile);
				nUrl = "<a href=" + nUrl + ">&nbsp;";
				
				if("true".equals(isWG)){
					nUrl = nUrl + ((EPMDocument)holder).getCADName() + "</a>";
				}else{
					nUrl = nUrl + ((ApplicationData)primaryFile).getFileName() + "</a>";
				}
			}else{
				nUrl =  ((URLData)primaryFile).getUrlLocation ();
			}
%>
		<td>
			<span id="FileShow" style="display:'none';">
			&nbsp;<input type="file" name="<%=type%>" id=input style="width:100%">
			<input type="hidden" name="<%=type%>Description" value="PRIMARY FILE">
			</span>
			<span id="FileHide" style="display:'';"><%= nUrl %></span>
		</td>
		<%if(!"true".equals(isWG)){ %>
		<td width="100" align="right"><img name="ImgFileChange" value="Show" src="images/img_default/button/board_btn_file_edit.gif" align="absmiddle" onMouseOver="this.style.cursor='hand'" onCLick="fcFileShowHide();"></td>
		<%} %>
<%		} else { %>
		<td>
			<input type="file" name="<%=type%>" id=input style="width:100%" >
			<input type="hidden" name="<%=type%>Description" value="PRIMARY FILE">
		</td>
<%		}	%>
<%	}	%>
	</tr>
</table>
<%	} else {	%>
<SCRIPT LANGUAGE="JavaScript">
var isCh = navigator.appVersion.indexOf("Chrome") >= 0;
function insertFile<%=type%>() {
	index = fileTable<%=type%>.rows.length;

<%		if ( type.equals("secondary") && attacheCount > 0 ) {	%>	
	if(index >= (<%=attacheCount%>+2)) {
		alert("<%=ORG_113%>.(<%=attacheCount%>개)");
		return;
	}
<%		}	%>	
	
	if(index >= 2) {
		if(fileTableRow<%=type%>.style != null)
			fileTableRow<%=type%>.style.display = '';
		else
			fileTableRow<%=type%>[0].style.display = '';
	}
	
	trObj = fileTable<%=type%>.insertRow(index);
	
	if(isCh){ // 크롬 일경우
		trObj.replaceWith(fileTable<%=type%>.rows[1].cloneNode(true));	
	
	}else{ // 크롬 외 브라우저 일 경우
		trObj.replaceNode(fileTable<%=type%>.rows[1].cloneNode(true));
	}
	fileTableRow<%=type%>[0].style.display = 'none';
	
}

function deleteFile<%=type%>() {
	index = document.<%=form%>.fileDelete<%=type%>.length-1;
	
	for(i=index; i>=1; i--) {
		if(document.<%=form%>.fileDelete<%=type%>[i].checked == true) fileTable<%=type%>.deleteRow(i+1);
	}
}
</SCRIPT>
<table width="100%" align="center">
	<tr> 
		<% if( !viewType.equals("view") ) { %>
		<td height="25">

			<table border=0 cellpadding="0" cellspacing=2 align="left">
			
                            <tr>
                                <td>
                                	<a onclick="insertFile<%=type%>();">
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
                                	<a onclick="deleteFile<%=type%>();">
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
                       		
		</td>
		 <%} %>											
		<%if(type.equals("secondary")&&attacheCount>0){%><td align="left"><B><font color="red"><%=ORG_113%>.(<%=attacheCount%>개</font></B></td><%}%>	
	</tr>
</table>
<table width="100%" cellspacing="0" cellpadding="1" border="0" class=tb1 id="fileTable<%=type%>" align="center">
	<tr bgcolor="#f1f1f1"  align=center>
<%		if(canDesc) {	%>
		<td width="60%" height="22" colspan="2" id=tb_inner><%=ORG_1035%></td>
		<td width="40%" height="22" id=tb_inner><%=ORG_549%></td>
<%		} else {	%>
		<td width="100%" height="22" colspan="2" id=tb_inner><%=ORG_1035%></td>
<%		}	%>
	</tr>
	<tr align="center" bgcolor="#FFFFFF" id="fileTableRow<%=type%>" style="display:NONE"> 
<%		if(canDesc) {	%>
		<td id=tb_gray width="3%" height="22"><input type="checkbox" name="fileDelete<%=type%>"></td>
		<td id=tb_gray width="57%"><input type="file" name="<%=type%>" id=input style="width:99%"></td>
		<td id=tb_gray width="40%"><input type="text" name="<%=type+"Desc"%>" id=input style="width:99%"></td>
<%		} else {	%>
		<td id=tb_gray width="3%" height="22"><input type="checkbox" name="fileDelete<%=type%>"></td>
		<td id=tb_gray width="97%"><input type="file" name="<%=type%>" id=input style="width:99%"></td>
<%		}	%>
	</tr>
<%
		while(secondaryFiles!=null && secondaryFiles.hasMoreElements()) {
			ContentItem item = (ContentItem) secondaryFiles.nextElement ();
			String name = "";
			if (item instanceof URLData) {
				URLData url = (URLData) item;
				name = url.getUrlLocation ();
			} else if (item instanceof ApplicationData) {
				ApplicationData file = (ApplicationData) item;
				name = file.getFileName ();
			}
			
			if("PDF".equals(item.getDescription())){
%>
				<input type="hidden" name="secondaryDelFile" value="<%=item.getPersistInfo().getObjectIdentifier().toString()%>">
<%
				continue;
			}
%>
	<tr align="center" bgcolor="#ffffff" align=center>
<%		if(canDesc) {	%>
		<td id=tb_gray width="3%" height="25"><input type="checkbox" name="fileDelete<%=type%>"></td>
		<td id=tb_gray width="57%" align=left><input type="hidden" name="secondaryDelFile" value="<%=item.getPersistInfo().getObjectIdentifier().toString()%>">&nbsp;<%=name%></td>
																										
		<td id=tb_gray width="40%"><input type="text" name="secondaryDelFileDesc" id=input style="width:99%" readonly value="<%=item.getDescription()%>"></td>
<%		} else {	%>
		<td id=tb_gray width="3%" height="25"><input type="checkbox" name="fileDelete<%=type%>"></td>
		<td id=tb_gray width="97%" align=left><input type="hidden" name="secondaryDelFile" value="<%=item.getPersistInfo().getObjectIdentifier().toString()%>">&nbsp;<%=name%></td>
<%		}	%>
	</tr>
<%		}	%>
</table>
<%	}	%>



<!-- // 파일 첨부 끝 //-->	
