<%@page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>

<%@page import="java.util.*,wt.content.*,wt.fc.*,wt.query.*,wt.org.*,wt.session.*" %>

<%@page import="ext.narae.component.*"%>
<%@page import="ext.narae.service.approval.*"%>
<%@page import="wt.team.*"%>

<%@ page import="wt.util.*, wt.org.*, wt.session.*, wt.inf.container.*, ext.narae.util.*, ext.narae.ui.*, java.util.*" %>
<%@page import="ext.narae.service.org.beans.*" %>
<%@page import="ext.narae.service.approval.beans.*" %>
<%
String RESOURCE = "ext.narae.ui.common.resource.NaraeCommonRB";
Locale locale = WTContext.getContext().getLocale();
String M001 = WTMessage.getLocalizedMessage(RESOURCE , "M001", new Object[]{}, WTContext.getContext().getLocale());

String ORG_32 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_32", new Object[]{}, WTContext.getContext().getLocale());
String ORG_38 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_38", new Object[]{}, WTContext.getContext().getLocale());
String ORG_1068 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_1068", new Object[]{}, WTContext.getContext().getLocale());
String ORG_520 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_520", new Object[]{}, WTContext.getContext().getLocale());
String ORG_996 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_996", new Object[]{}, WTContext.getContext().getLocale());
String ORG_718 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_718", new Object[]{}, WTContext.getContext().getLocale());
String ORG_400 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_400", new Object[]{}, WTContext.getContext().getLocale());
String ORG_225 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_225", new Object[]{}, WTContext.getContext().getLocale());
String ORG_1091 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_1091", new Object[]{}, WTContext.getContext().getLocale());
String ECO_RECEIVE = WTMessage.getLocalizedMessage(RESOURCE , "ECO_RECEIVE", new Object[]{}, WTContext.getContext().getLocale());
String APPROVAL_COMMENT = WTMessage.getLocalizedMessage(RESOURCE , "APPROVAL_COMMENT", new Object[]{}, WTContext.getContext().getLocale());
String APPROVAL_DATE = WTMessage.getLocalizedMessage(RESOURCE , "APPROVAL_DATE", new Object[]{}, WTContext.getContext().getLocale());
%>
<%
WTUser user = (WTUser)SessionHelper.manager.getPrincipal();
String orderType = "";
String objOid = StringUtil.checkNull( request.getParameter("oid") );
String viewType = (request.getParameter("viewType") != null )?request.getParameter("viewType").trim():"";
System.out.println("viewType=" + viewType);

if( objOid == null || objOid.equals("null") ) {
	objOid = "";
} else {
	if( objOid.contains("wt.org.WTUser") ) {
		objOid = "";
	}
}
System.out.println("objOid = " + objOid);
String form = request.getParameter("form");
if(form==null)
	form = "";
System.out.println("formName = " + form);
String command = request.getParameter("command");
if(command==null)command = "";

%>

<LINK REL="stylesheet" TYPE="text/css" HREF="/Windchill/netmarkets/jsp/narae/css/css.css">
<script language="JavaScript" type="text/JavaScript">
function MM_preloadImages() { //v3.0
  var d=document; if(d.images){ if(!d.MM_p) d.MM_p=new Array();
    var i,j=d.MM_p.length,a=MM_preloadImages.arguments; for(i=0; i<a.length; i++)
    if (a[i].indexOf("#")!=0){ d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];}}
}

function MM_swapImgRestore() { //v3.0
  var i,x,a=document.MM_sr; for(i=0;a&&i<a.length&&(x=a[i])&&x.oSrc;i++) x.src=x.oSrc;
}

function MM_findObj(n, d) { //v4.01
  var p,i,x;  if(!d) d=document; if((p=n.indexOf("?"))>0&&parent.frames.length) {
    d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}
  if(!(x=d[n])&&d.all) x=d.all[n]; for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];
  for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=MM_findObj(n,d.layers[i].document);
  if(!x && d.getElementById) x=d.getElementById(n); return x;
}

function MM_swapImage() { //v3.0
  var i,j=0,x,a=MM_swapImage.arguments; document.MM_sr=new Array; for(i=0;i<(a.length-2);i+=3)
   if ((x=MM_findObj(a[i]))!=null){document.MM_sr[j++]=x; if(!x.oSrc) x.oSrc=x.src; x.src=a[i+2];}
}

var userSearchPop;
var thisForm;// = document.<%=form%>;

function insertAppUserMember(){
	//thisForm = getForm;
	var url = "/Windchill/netmarkets/jsp/narae/org/searchUser.jsp?mode=m";

	var popWidth = "1100";
	var popHeight = "800";
	var userScreenWidth = screen.availWidth / 2 - popWidth / 2;
	var userScreenHeight = screen.availHeight / 2 - popHeight / 2;
	
	//window.showModalDialog
	//'toolbar=yes,location=no,status=yes,menubar=yes,scrollbars=yes,resizable=yes,width=600,height=400,top=100,left=100'
	//attache = window.showModelessDialog(url,"mainForm","help=no; scroll=no; resizable=no; dialogWidth=900px; dialogHeight:570px; center:yes");
	//attache = window.showDialog(url,"mainForm","toolbar=no,location=no,status=no,menubar=no,scrollbars=no,resizable=no,width=900px,height=570px");
	
	var styles = "titlebar=no,toolbar=no,location=no,status=no,menubar=no,scrollbars=no,resizable=no,width=1100,height=800,left=" + userScreenWidth + ", top=" + userScreenHeight;
	
	userSearchPop = window.open(url,"mainForm",styles);
	userSearchPop.focus();
	//if(typeof attache == "undefined" || attache == null) {
	//	return;
	//}

	//addAppUserMember(attache);
	
}

function getDDD() {
	//return document.<%=form%>.approveUser2.value;
	alert( checkRequired() );
}

function addAppUserMember(arrObj){
	if(arrObj.length == 0) {
		return;
	}
	
	var userType;//
	var userOid;//
	var userName;//

	for(var i = 0; i < arrObj.length; i++) {
		subarr = arrObj[i];
		userOid = subarr[0];//
		userName = subarr[4];//
		userType = subarr[9];//
		
		if( userType == '2')
			addApprovalLine2(userOid,userName);
		else if( userType == '3')
			addApprovalLine3(userOid,userName);
		else if( userType == '4')
			addApprovalLine4(userOid,userName);
		else if( userType == '5')
			addTempLine(userOid,userName);
	}
}

var indexNo = 0;

function loadApproveUser(viewType){
	

	<%
			if( objOid == null || objOid.equals("") ) {
				
				
			} else {				
				Persistable wtObj = WCUtil.getObject(objOid);
				
				ApprovalLineVO approvalLine = null;
				System.out.println("wtObj = " + wtObj);
				approvalLine = ApprovalHelper2.getApprovalLine((TeamManaged)wtObj);
				
				if( approvalLine != null ){
					ArrayList alist2 = new ArrayList();
					List<WTUser> alist3 = approvalLine.getChangManager1();
					List<List<String>> alist33 = approvalLine.getChangeManagerInfo1();
					List<WTUser> alist4 = approvalLine.getChangManager2();	
					List<List<String>> alist44 = approvalLine.getChangeManagerInfo2();
					List<WTUser> alist5 = approvalLine.getChangManager3();
					List<List<String>> alist55 = approvalLine.getChangeManagerInfo3();
										
					//ArrayList tlist = template.getTempList();
					ReferenceFactory rf = new ReferenceFactory();
					for(int i=0; alist2!=null && alist2.size()>i; i++){
						WTUser auser = (WTUser)alist2.get(i);
						String userString = auser.getPersistInfo().getObjectIdentifier().getClassname()+":"+ auser.getPersistInfo().getObjectIdentifier().getId();
						String[] userInfo = UserHelper.service.getUserInfo(auser);
						%>
							if( viewType == 'approval' ) {
								addApprovalLine22('<%=userString%>','<%=auser.getFullName()%>', '<%=userInfo[0]%>', '<%=userInfo[1]%>', '<%=userInfo[2]%>', '<%=userInfo[3]%>' ,'','');
							} else {
								addApprovalLine2('<%=userString%>','<%=auser.getFullName()%>', '<%=userInfo[0]%>', '<%=userInfo[1]%>', '<%=userInfo[2]%>', '<%=userInfo[3]%>');
							}
						<%
					}		
					for(int i=0; alist3!=null && alist3.size()>i; i++){
						WTUser auser = (WTUser)alist3.get(i);
						List<String> approvalInfo = alist33.get(i);
						String userString = auser.getPersistInfo().getObjectIdentifier().getClassname()+":"+ auser.getPersistInfo().getObjectIdentifier().getId();
						String[] userInfo = UserHelper.service.getUserInfo(auser);
						%>
						if( viewType == 'approval' ) {
							addApprovalLine33('<%=userString%>','<%=auser.getFullName()%>', '<%=userInfo[0]%>', '<%=userInfo[1]%>', '<%=userInfo[2]%>', '<%=userInfo[3]%>' ,'<%=approvalInfo.get(0)%>','<%=approvalInfo.get(1)%>');
						} else {
							addApprovalLine3('<%=userString%>','<%=auser.getFullName()%>', '<%=userInfo[0]%>', '<%=userInfo[1]%>', '<%=userInfo[2]%>', '<%=userInfo[3]%>');
						}
						<%
					}		
					for(int i=0; alist4!=null && alist4.size()>i; i++){
						WTUser auser = (WTUser)alist4.get(i);
						List<String> approvalInfo = alist44.get(i);
						String userString = auser.getPersistInfo().getObjectIdentifier().getClassname()+":"+ auser.getPersistInfo().getObjectIdentifier().getId();
						String[] userInfo = UserHelper.service.getUserInfo(auser);
						%>
						if( viewType == 'approval' ) {
							addApprovalLine44('<%=userString%>','<%=auser.getFullName()%>', '<%=userInfo[0]%>', '<%=userInfo[1]%>', '<%=userInfo[2]%>', '<%=userInfo[3]%>' ,'<%=approvalInfo.get(0)%>','<%=approvalInfo.get(1)%>');
						} else {
							addApprovalLine4('<%=userString%>','<%=auser.getFullName()%>', '<%=userInfo[0]%>', '<%=userInfo[1]%>', '<%=userInfo[2]%>', '<%=userInfo[3]%>');
						}
						<%
					}		
					for(int i=0; alist5!=null && alist5.size()>i; i++){
						WTUser auser = (WTUser)alist5.get(i);
						List<String> approvalInfo = alist55.get(i);
						String userString = auser.getPersistInfo().getObjectIdentifier().getClassname()+":"+ auser.getPersistInfo().getObjectIdentifier().getId();
						String[] userInfo = UserHelper.service.getUserInfo(auser);
						%>
						if( viewType == 'approval' ) {
							addTempLine11('<%=userString%>','<%=auser.getFullName()%>', '<%=userInfo[0]%>', '<%=userInfo[1]%>', '<%=userInfo[2]%>', '<%=userInfo[3]%>' ,'<%=approvalInfo.get(0)%>','<%=approvalInfo.get(1)%>');
						} else {
							addTempLine('<%=userString%>','<%=auser.getFullName()%>', '<%=userInfo[0]%>', '<%=userInfo[1]%>', '<%=userInfo[2]%>', '<%=userInfo[3]%>');
						}
						<%
					}
				}
			}
	%>
}

function getLength() {
	var incForm = document.forms[0];

	if(document.getElementById("approverLink")!=null)
	{
		if(document.getElementById("approverLink").length!=undefined)
		{
			return document.getElementById("approverLink").length;
		} else {
			return 0;
		}
	} else {
		return 0;
	}
}

//순서 	구분 	부서 	 이름 	ID 	직급 
function addApprovalLine2(oid, name, department, id, position, userInfo) {
	var userRow = document.getElementById("approverLink").insertRow();
    for(var k = 0; k < 6; k++) {
        userCell = userRow.insertCell();
        userCell.align = "center";
        userCell.className = "border_text_03-1";

    	var hidden = name + "<input type=hidden name=approveUser2 value='"+oid+"'>"+ "<input type=hidden name=infoUser2 value='"+userInfo+"'>";
        
        if(k == 0) {
        	indexNo = indexNo + 1;
            userCell.innerHTML = indexNo;
        } else if(k == 1) {
            userCell.innerHTML = "<%=ORG_32%>";
        } else if(k == 2) {
            userCell.innerHTML = department;
        } else if(k == 3) {
            userCell.innerHTML = hidden;
            userCell.title = hidden;
        } else if(k == 4) {
            userCell.innerHTML = id;
        } else if(k == 5) {
            userCell.innerHTML = position;
        }
    }
}

function addApprovalLine22(oid, name, department, id, position, userInfo, approveDate, comment) {
	var userRow = document.getElementById("approverLink").insertRow();
    for(var k = 0; k < 6; k++) {
        userCell = userRow.insertCell();
        userCell.align = "center";
        userCell.className = "border_text_03-1";

    	var hidden = name + "<input type=hidden name=approveUser2 value='"+oid+"'>"+ "<input type=hidden name=infoUser2 value='"+userInfo+"'>";
        
        if(k == 0) {
        	indexNo = indexNo + 1;
            userCell.innerHTML = indexNo;
        } else if(k == 1) {
            userCell.innerHTML = "<%=ORG_32%>";
        } else if(k == 2) {
            userCell.innerHTML = department;
        } else if(k == 3) {
            userCell.innerHTML = hidden;
            userCell.title = hidden;
        } else if(k == 4) {
            userCell.innerHTML = id;
        } else if(k == 5) {
            userCell.innerHTML = position;
        } else if(k == 6) {
            userCell.innerHTML = approveDate;
        } else if(k == 7) {
            userCell.innerHTML = comment;
        }
    }
}

function getInfoUser2() {
	return document.getElementsByName("infoUser2");
}

//순서 	구분 	부서 	 이름 	ID 	직급 
function addApprovalLine3(oid, name, department, id, position, userInfo) {
	var userRow = document.getElementById("approverLink").insertRow();
    for(var k = 0; k < 6; k++) {
        userCell = userRow.insertCell();
        userCell.align = "center";
        userCell.className = "a_con_05";

        var hidden = name + "<input type=hidden name=approveUser3 value='"+oid+"'>"+ "<input type=hidden name=infoUser3 value='"+userInfo+"'>";

        if(k == 0) {
        	indexNo = indexNo + 1;
            userCell.innerHTML = indexNo;
        } else if(k == 1) {
            userCell.innerHTML = "<%=ORG_38%>";
        } else if(k == 2) {
            userCell.innerHTML = department;
        } else if(k == 3) {
            userCell.innerHTML = hidden;
        } else if(k == 4) {
            userCell.innerHTML = id;
        } else if(k == 5) {
            userCell.innerHTML = position;
        } 
    }
}

function addApprovalLine33(oid, name, department, id, position, userInfo, approvalDate, comment) {
	var userRow = document.getElementById("approverLink").insertRow();
    for(var k = 0; k < 6; k++) {
        userCell = userRow.insertCell();
        userCell.align = "center";
        userCell.className = "a_con_05";

        var hidden = name + "<input type=hidden name=approveUser3 value='"+oid+"'>"+ "<input type=hidden name=infoUser3 value='"+userInfo+"'>";

        if(k == 0) {
        	indexNo = indexNo + 1;
            userCell.innerHTML = indexNo;
        } else if(k == 1) {
            userCell.innerHTML = "<%=ORG_38%>";
        } else if(k == 2) {
            userCell.innerHTML = department;
        } else if(k == 3) {
            userCell.innerHTML = hidden;
        } else if(k == 4) {
            userCell.innerHTML = id;
        } else if(k == 5) {
            userCell.innerHTML = position;
        } else if(k == 6) {
            userCell.innerHTML = approvalDate;
        } else if(k == 7) {
            userCell.innerHTML = comment;
        }
    }
}

function getInfoUser3() {
	return document.getElementsByName("infoUser3");
}

//순서 	구분 	부서 	 이름 	ID 	직급 
function addApprovalLine4(oid, name, department, id, position, userInfo) {
	var userRow = document.getElementById("approverLink").insertRow();
    for(var k = 0; k < 6; k++) {
        userCell = userRow.insertCell();
        userCell.align = "center";
        userCell.className = "border_text_03-1";

        var hidden = name + "<input type=hidden name=approveUser4 value='"+oid+"'>"+ "<input type=hidden name=infoUser4 value='"+userInfo+"'>";

        if(k == 0) {
        	indexNo = indexNo + 1;
            userCell.innerHTML = indexNo;
        } else if(k == 1) {
            userCell.innerHTML = "<%=ORG_1091%>";
        } else if(k == 2) {
            userCell.innerHTML = department;
        } else if(k == 3) {
            userCell.innerHTML = hidden;
        } else if(k == 4) {
            userCell.innerHTML = id;
        } else if(k == 5) {
            userCell.innerHTML = position;
        }
    }
}

function addApprovalLine44(oid, name, department, id, position, userInfo, approvalDate, comment) {
	var userRow = document.getElementById("approverLink").insertRow();
    for(var k = 0; k < 6; k++) {
        userCell = userRow.insertCell();
        userCell.align = "center";
        userCell.className = "border_text_03-1";

        var hidden = name + "<input type=hidden name=approveUser4 value='"+oid+"'>"+ "<input type=hidden name=infoUser4 value='"+userInfo+"'>";

        if(k == 0) {
        	indexNo = indexNo + 1;
            userCell.innerHTML = indexNo;
        } else if(k == 1) {
            userCell.innerHTML = "<%=ORG_1091%>";
        } else if(k == 2) {
            userCell.innerHTML = department;
        } else if(k == 3) {
            userCell.innerHTML = hidden;
        } else if(k == 4) {
            userCell.innerHTML = id;
        } else if(k == 5) {
            userCell.innerHTML = position;
        } else if(k == 6) {
            userCell.innerHTML = approvalDate;
        } else if(k == 7) {
            userCell.innerHTML = comment;
        }
    }
}

function getInfoUser4() {
	return document.getElementsByName("infoUser4");
}

//순서 	구분 	부서 	 이름 	ID 	직급 
function addTempLine(oid, name, department, id, position, userInfo) {
	var userRow = document.getElementById("approverLink").insertRow();
  for(var k = 0; k < 6; k++) {
      userCell = userRow.insertCell();
      userCell.align = "center";
      userCell.className = "a_con_05";

      var hidden = name + "<input type=hidden name=tempUser value='"+oid+"'>"+ "<input type=hidden name=infoUser value='"+userInfo+"'>";

      if(k == 0) {
      	indexNo = indexNo + 1;
          userCell.innerHTML = indexNo;
      } else if(k == 1) {
          userCell.innerHTML = "<%=ECO_RECEIVE%>";
      } else if(k == 2) {
          userCell.innerHTML = department;
      } else if(k == 3) {
          userCell.innerHTML = hidden;
      } else if(k == 4) {
          userCell.innerHTML = id;
      } else if(k == 5) {
          userCell.innerHTML = position;
      }
  }
}

function addTempLine11(oid, name, department, id, position, userInfo, approvalDate, comment) {
	var userRow = document.getElementById("approverLink").insertRow();
  for(var k = 0; k < 6; k++) {
      userCell = userRow.insertCell();
      userCell.align = "center";
      userCell.className = "a_con_05";

      var hidden = name + "<input type=hidden name=tempUser value='"+oid+"'>"+ "<input type=hidden name=infoUser value='"+userInfo+"'>";

      if(k == 0) {
      	indexNo = indexNo + 1;
          userCell.innerHTML = indexNo;
      } else if(k == 1) {
          userCell.innerHTML = "<%=ECO_RECEIVE%>";
      } else if(k == 2) {
          userCell.innerHTML = department;
      } else if(k == 3) {
          userCell.innerHTML = hidden;
      } else if(k == 4) {
          userCell.innerHTML = id;
      } else if(k == 5) {
          userCell.innerHTML = position;
      } else if(k == 6) {
          userCell.innerHTML = approvalDate;
      } else if(k == 7) {
          userCell.innerHTML = comment;
      }
  }
}

function getInfoUser() {
	return document.getElementsByName("infoUser");
}

function deleteAll() {
	try { 
		for(i=indexNo-1; i >= 0 ;i--) {
			document.getElementById("approverLink").deleteRow(i);
		}

		indexNo = 0;	
	}catch(e){alert(e);}
}

function checkRequired() {
	// 마지막에 결재자가 있어야함.
	// 합의가 없으면 합의전 결재만 있어도 됨
	// 합의가 있으면 결재에 존재해야함
	// false일때는 저장 불가 ==> 결재 신청이 불가함
	// true일때는 저장 가능

	if( document.getElementsByName("infoUser3").lengh >  0 ) {	//alert("합의 not null");
		if( document.getElementsByName("infoUser4").length > 0 ){ //alert("결재 not null");
			return true
		} else { //alert("합의전 결재, 결재 null");
			return false;
		}
	} else { //alert("합의 null");
		if( document.getElementsByName("infoUser4").length > 0 ){ //alert("결재 not null");
			return true
		} else { //alert("합의전 결재, 결재  null");
			return false;
		}
	}
}

</script>
 
<table width="650" border="0" align="center" cellpadding="0" cellspacing="1" bgcolor=#ffffff class=9pt>
  <%if( !command.equals("view") ) {%>
  <tr> 
	<td class="border_text_03-1">
	<a style="FONT-SIZE: 8pt;" onclick="javascript:insertAppUserMember();"><table class="" border="0" cellpadding="0" cellspacing="0" width="100"><tbody><tr><td width="7"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left.gif" alt="" height="20" width="7"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center"><%=ORG_1068%></div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right.gif" alt="" height="20" width="12"></td></tr></tbody></table></a>
	</td>
  </tr>
  <%}%>
  <tr>
  	<input type="hidden" name="discussType" value="parallel">
    <td colspan="3" valign="top"  class="border_text_03-1"><br> 
        <div id=list style="height:100%;" width="100%"> 
          <table width="95%" border="0" cellpadding="1" cellspacing="1" bgcolor=#9CAEC8 align=center>
            <tr> 
              <td height=1 width=95%></td>
            </tr>
          </table>
          <table name='approverInfo' id='approverInfo' width="95%" border="0" align="center" cellpadding="0" cellspacing="1" bgcolor=#cfcfcf class=9pt>
            <tr> 
              <td height="23"  class="a_con_01"><%=ORG_520%></TD>
              <td  class="a_con_01"><%=ORG_996%></TD>
              <td height="23"  class="a_con_01"><%=ORG_718%> </TD>
              <td  class="a_con_01"><%=ORG_400%></TD>
              <td height="23"  class="a_con_01">ID</TD>
              <td height="23"  class="a_con_01"><%=ORG_225%></TD>
              <%if( viewType.equals("approval") ) { %>
              <td height="23"  class="a_con_01"><%=APPROVAL_DATE%></TD>
              <td height="23"  class="a_con_01"><%=APPROVAL_COMMENT%></TD>
              <%} %>
            </TR>
                <tbody id="approverLink">
                </tbody>  
          </table>
          <br>
        </div>
      </div></TD>
  </TR>
</table>

<script>
loadApproveUser('<%=viewType%>');
</script>

<script>MM_preloadImages('/Windchill/netmarkets/jsp/narae/img/k_kj_01_ov.gif');</script>
