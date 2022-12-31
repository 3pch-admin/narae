<%@page import="ext.narae.service.org.beans.OrgDao"%>
<%@page import="wt.query.ClassAttribute"%>
<%@page import="wt.query.OrderBy"%>
<%@page import="wt.query.SearchCondition"%>
<%@page import="ext.narae.service.org.Department"%>
<%@page import="wt.query.QuerySpec"%>
<%@page import="ext.narae.service.org.beans.UserHelper"%>
<%@page import="ext.narae.service.org.People"%>
<%@page import="wt.folder.Folder"%>
<%@page import="wt.clients.folder.FolderTaskLogic"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>

<%@page import ="java.util.*" %>
<%@page import ="wt.fc.*" %>
<%@page import ="wt.org.*" %>
<%@ page import="wt.util.*, wt.org.*, wt.session.*, wt.inf.container.*, ext.narae.util.*, ext.narae.ui.*, java.util.*" %>

<%


String RESOURCE = "ext.narae.ui.common.resource.NaraeCommonRB";
String UNITRB = "ext.narae.ui.common.resource.NareUnitRB";
Locale locale = WTContext.getContext().getLocale();
WTContainerRef containerRef = WCUtil.getWTContainerRefForDrawing();
WTContainerRef partContainerRef = WCUtil.getWTContainerRefForPart();
String ORG_400 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_400", new Object[]{}, locale);
String ORG_325 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_325", new Object[]{}, locale);
String ORG_32 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_32", new Object[]{}, locale);
String ORG_399 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_399", new Object[]{}, locale);
String ORG_38 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_38", new Object[]{}, locale);
String ORG_1091 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_1091", new Object[]{}, locale);
String ORG_120 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_120", new Object[]{}, locale);
String ORG_1113 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_1113", new Object[]{}, locale);
String ORG_1097 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_1097", new Object[]{}, locale);
String ORG_637 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_637", new Object[]{}, locale);
String ORG_410 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_410", new Object[]{}, locale);
String ORG_1112 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_1112", new Object[]{}, locale);
String ORG_409 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_409", new Object[]{}, locale);
String ORG_1116 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_1116", new Object[]{}, locale);
String ORG_1071 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_1071", new Object[]{}, locale);
String ORG_1062 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_1062", new Object[]{}, locale);
String ORG_1255 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_1255", new Object[]{}, locale);
String ORG_1098 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_1098", new Object[]{}, locale);
String ORG_1070 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_1070", new Object[]{}, locale);
String ORG_150 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_150", new Object[]{}, locale);
String ORG_641 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_641", new Object[]{}, locale);
String ORG_844 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_844", new Object[]{}, locale);
String ORG_35 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_35", new Object[]{}, locale);
String ORG_520 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_520", new Object[]{}, locale);
String ORG_996 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_996", new Object[]{}, locale);
String ORG_718 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_718", new Object[]{}, locale);
String ORG_225 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_225", new Object[]{}, locale);
String ORG_890 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_890", new Object[]{}, locale);
String ORG_141 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_141", new Object[]{}, locale);
%>

<%
	String userKey = request.getParameter("userKey");
	String form = request.getParameter("form");
	String approveType = StringUtil.checkNull(request.getParameter("approveType"));
	WTUser loginUser = (WTUser)SessionHelper.manager.getPrincipal();
	People loginPeople = UserHelper.service.getPeople(loginUser);
	String chief = "false";
	if ( loginPeople.getChief() != null && loginPeople.getChief().booleanValue()) {
		chief = "true";
	}
	if(userKey==null)
		userKey = ""; 
	if(form==null)
		form = "";
%>
<html>
<head>
<title><%=ORG_1062%></title>
<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
<form name=departmentTreeForm method=post onSubmit="return windEdit_Enter(1)">
<input type="hidden" name="title" id="title" value="">
<link rel="StyleSheet" href="/Windchill/netmarkets/jsp/narae/css/dtree.css" type="text/css"/>
<LINK REL="stylesheet" TYPE="text/css" HREF="/Windchill/netmarkets/jsp/narae/css/e3ps.css">
<LINK REL="stylesheet" TYPE="text/css" HREF="/Windchill/netmarkets/jsp/narae/css/css.css">
<script type="text/javascript" src="/Windchill/netmarkets/jsp/narae/js/dtree.js"></script>
<script type="text/javascript" src="/Windchill/netmarkets/jsp/narae/js/common.js"></script>
<script language="JavaScript" type="text/JavaScript">
var approveType ="<%=approveType%>"

function windEdit_Enter(arg) {
	if(arg == 1) return false;

	if(arg == 2) keySearch();
	else if(arg == 3) inputLine();
	else if(arg == 4) deleteLine();
	else if(arg == 5) getLine();
	else if(arg == 6) viewLine();
}

var searchView = "leftSearch";

function leftView(id) {
    if( id == "leftSearch") {
      leftSearch.style.display = "block";
		  leftTree.style.display = "none";
		  leftLine.style.display = "none";

		  searchView = "leftSearch";
    } else if( id == "leftTree") {
      leftSearch.style.display = "none";
		  leftTree.style.display = "block";
		  leftLine.style.display = "none";

		  searchView = "leftTree";
    } else if( id == "leftLine") {
      leftSearch.style.display = "none";
		  leftTree.style.display = "none";
		  leftLine.style.display = "block";

		  searchView = "leftLine";
    }
}

///////////////////////////////////////////////////
// ?ъ슜??異붽?/??젣

var treeUser = "";
var searchUser = "";
var loginUser = "<%=loginUser.getName()%>";
var isChief = "<%=chief%>";

function add() {
	var appType = currentAppType();

	if( appType == "" || appType == "undefined" ) {
		alert("Please, Select approval type!");
		return;
	}
	// alert(appType);
	var userInfo = getUser();
	if( userInfo == "" || userInfo == "undefined" ) {
		alert("Please, Select user!");
		return;
	}
	// alert("addInfo = " + userInfo);
	
	if( appType == "app1" ) {
		addApp1(userInfo);
	} else if( appType == "app2" ) {
		addApp2(userInfo);
	} else if( appType == "app3" ) {
		addApp3(userInfo);
	} else if( appType == "app4" ) {
		addApp4(userInfo);
	}
}

function del() {
	var appType = currentAppType();

	if( appType == "" || appType == "undefined" ) {
		alert("Please, Select approval type!");
		return;
	}

	if( appType == "app1" ) {
		delApp1();
	} else if( appType == "app2" ) {
		delApp2();
	} else if( appType == "app3" ) {
		delApp3();
	} else if( appType == "app4" ) {
		delApp4();
	}
}

function deltype() {
	var appType = currentAppType();

		delApp1();
		delApp2();
		delApp3();
		delApp4();
}

function delall() {
	var appType = currentAppType();

	if( appType == "" || appType == "undefined" ) {
		alert("Please, Select approval type!");
		return;
	}

	if( appType == "app1" ) {
		delAllApp1();
	} else if( appType == "app2" ) {
		delAllApp2();
	} else if( appType == "app3" ) {
		delAllApp3();
	} else if( appType == "app4" ) {
		delAllApp4();
	}
}

function delalltype() {
	var appType = currentAppType();

		delAllApp1();
		delAllApp2();
		delAllApp3();
		delAllApp4();
}

function getUser() {
	if( searchView == "leftSearch" ) {
		getSearchUser();
		return searchUser;
		
	} else if( searchView == "leftTree" ) {
		return treeUser;
	} else 
		return "";
}

function currentAppType() {
	var form = document.forms[0];
	   var memberInfo = "";
	   for(i=0; i< form.appType.length; i++) {
	        if (form.appType[i].checked == true) {
	             memberInfo = form.appType[i].value;
				 return memberInfo;
	        }
	   }
	   return "";
}

function getSearchUser() {
	var form = document.forms[0];
	for (i=0;i< form.userList.length;i++){
        if (form.userList[i].selected == true) {
			searchUser = form.userList[i].value;
        }
	}
        //alert("searchUser = " + searchUser);
}

function setTreeUser(useroid, peopleoid, deptoid, id, name, departmentname, duty, dutycode, email, temp) {
	treeUser = useroid + "," + peopleoid + "," + deptoid + "," + id + "," + name + "," + 
		departmentname + "," + duty + "," + dutycode + "," + email + "," + temp; 
	//alert("treeUser = " + treeUser);
}

///////////////////////////////////////////////////////////////////////
// ?묒쓽??寃곗옱 - add app1
function addApp1(userInfo) {
	var publish = document.forms[0];

	var userInfoArr = userInfo.split(",");
//	for(i=0; i<userInfoArr.length; i++) { 
		//alert(i + " = " + userInfoArr[i]);
//	}
	var count = 1;
	var userId = userInfoArr[3];
	//var checkDup  = checkDupApp(userInfo, userId);
	//if( checkDup == "true" ) {
        //alert(userInfoArr[3] + "??以묐났?⑸땲??");
    //    return;
	//}
        if( publish.chkApp1 != null ) {
            if( publish.chkApp1.length ) {
                count = publish.chkApp1.length+1;
            } else {
                count = 2;
            }
        }

// 		if( userId == loginUser ) {
<%-- 			alert("<%=ORG_325%>!"); --%>
// 			return;
// 		}
        if( publish.chkApp1 != null ) {
            // 異붽??섍린 ?꾩뿉 以묐났?섎뒗寃껋씠 ?덈뒗吏 ?뺤씤
            if( publish.chkApp1.length ) {
                for(var k=0; k < publish.chkApp1.length; k++) {
                   if( userInfo == publish.chkApp1[k].value ) {
                        alert("[<%=ORG_32%>] " + userId + "<%=ORG_399%>!");
                        return;
                    }
                }
            } else {
                if( userInfo == publish.chkApp1.value ) {
                    alert("[<%=ORG_32%>] " + userId + "<%=ORG_399%>!");
                    return;
                }
            }
        }
        if( publish.chkApp2 != null ) {
            // 異붽??섍린 ?꾩뿉 以묐났?섎뒗寃껋씠 ?덈뒗吏 ?뺤씤
            if( publish.chkApp2.length ) {
                for(var k=0; k < publish.chkApp2.length; k++) {
                   if( userInfo == publish.chkApp2[k].value ) {
                        alert("[<%=ORG_38%>] " + userId + "<%=ORG_399%>!");
                        return;
                    }
                }
            } else {
                if( userInfo == publish.chkApp2.value ) {
                    alert("[<%=ORG_38%>] " + userId + "<%=ORG_399%>!");
                    return;
                }
            }
        }
        if( publish.chkApp3 != null ) {
            // 異붽??섍린 ?꾩뿉 以묐났?섎뒗寃껋씠 ?덈뒗吏 ?뺤씤
            if( publish.chkApp3.length ) {
                for(var k=0; k < publish.chkApp3.length; k++) {
                   if( userInfo == publish.chkApp3[k].value ) {
                        alert("[<%=ORG_1091%>] " + userId + "<%=ORG_399%>!");
                        return;
                    }
                }
            } else {
                if( userInfo == publish.chkApp3.value ) {
                    alert("[<%=ORG_1091%>] " + userId + "<%=ORG_399%>!");
                    return;
                }
            }
        }

        var userRow = app1.insertRow();
            for(var k = 0; k<7; k++) {
                userCell = userRow.insertCell();
                //userCell.align = "center";
                //userCell.height = "23";
                userCell.className = "tdwhiteM";
                
                if(k == 0) {
                    userCell.innerHTML = "<input type='checkbox' name='chkApp1' class='chkbox' value='" + userInfo + "'>"+ "<input type=hidden name=approveUser2 value='"+userInfoArr[0]+"'>";
                    //userCell.title = userInfo;
                } else if(k == 1) {
                    userCell.innerHTML = count;
                } else if(k == 2) {
                    userCell.innerHTML = "Pre-Approver";
                } else if(k == 3) {
                    userCell.innerHTML = userInfoArr[5];
                    userCell.title = userInfoArr[5];
                } else if(k == 4) {
                    userCell.innerHTML = userInfoArr[9];
                    userCell.title = userInfoArr[9];
                } else if(k == 5) {
                    userCell.innerHTML = userInfoArr[3];
                    userCell.title = userInfoArr[3];
                } else if(k == 6) {
                    userCell.innerHTML = userInfoArr[6];
                    userCell.title = userInfoArr[6];
                } 
            }
}

function delApp1() {
	try {
	 var publish = document.forms[0];
	 var myElement = document.getElementById("app1");
	
	 if (publish.chkApp1 != null) {
	     // 2媛??댁긽 ?쇰븣
	     if (publish.chkApp1.length) {
	         // 泥댄겕??由ъ뒪?몃? ??젣?쒕떎.
	         for (var i = publish.chkApp1.length-1; i >= 0 ; i--) {
	             if (i !=0) {
	                 //alert(i);
	                 //alert(document.publish.chkAppendFile[i].checked);
	                 if (publish.chkApp1[i].checked) {
	                     myElement.deleteRow(i);
	                 }
	             } else {
	                 if (myElement.rows.length > 1) {
	                     //alert(i);
	                     //alert(document.publish.chkAppendFile[i].checked);
	                     if (publish.chkApp1[i].checked) {
	                         myElement.deleteRow(i);
	                     }
	                 } else {
	                     if (publish.chkApp1[i].checked) {
	                     	myElement.deleteRow(0);
	                     }
	                 }
	             }
	         }
	     }
	     // 1媛??쇰븣
	     else if (publish.chkApp1) {
	         // 泥댄겕??由ъ뒪?몃? ??젣?쒕떎.
	         if (publish.chkApp1.checked) {
	             myElement.deleteRow(0);
	         }
	     }
	 }

		// ?쒕쾲???ㅼ떆 ?앹꽦?쒕떎.
		var countIndex = 1;
		var oRow;
		var userInfo;
		
	    if (publish.chkApp1.length) {
			for (var curr_row = 0; curr_row < myElement.rows.length; curr_row++) {
				oRow = myElement.rows[curr_row];
	
				userInfo = publish.chkApp1[curr_row].value;
			    oRow.cells[1].innerHTML = countIndex;
			    
			    countIndex += 1;
			}
	    } else if (publish.chkApp1) {
			for (var curr_row = 0; curr_row < myElement.rows.length; curr_row++) {
				oRow = myElement.rows[curr_row];
	
				userInfo = publish.chkApp1.value;
			    oRow.cells[1].innerHTML = countIndex;
			    
			    countIndex += 1;
			}
	    }
	}catch(e){}	    
}

function delAllApp1(){
	try {
	 var publish = document.forms[0];
    var myElement = document.getElementById("app1");

    if (publish.chkApp1 != null) {
        // 2媛??댁긽 ?쇰븣
        if (publish.chkApp1.length) {
            // 泥댄겕??由ъ뒪?몃? ??젣?쒕떎.
            for (var i = publish.chkApp1.length-1; i >= 0 ; i--) {
                if (i !=0) {
                    myElement.deleteRow(i);
                } else {
                    if (myElement.rows.length > 1) {
                        myElement.deleteRow(i);
                    } else {
                        var bName = navigator.appName;
                        if (bName == "Netscape") {
                            myElement.deleteRow(0);
                        } else {
                            myElement.deleteRow(0);
                        }
                    }
                }
            }
        }
        // 1媛??쇰븣
        else if (publish.chkApp1) {
            // 泥댄겕??由ъ뒪?몃? ??젣?쒕떎.
            myElement.deleteRow(0);
        }
    }
	}catch(e){}	    
}

///////////////////////////////////////////////////////////////////////
//?묒쓽??寃곗옱 - add app2
function addApp2(userInfo) {
	var publish = document.forms[0];
	var userInfoArr = userInfo.split(",");
//	for(i=0; i<userInfoArr.length; i++) { 
		//alert(i + " = " + userInfoArr[i]);
//	}
	var count = 1;
	var userId = userInfoArr[3];
	//var checkDup  = checkDupApp(userInfo, userId);
	//if( checkDup == "true" ) {
     //alert(userInfoArr[3] + "??以묐났?⑸땲??");
 //    return;
	//}
     if( publish.chkApp2 != null ) {
         if( publish.chkApp2.length ) {
             count = publish.chkApp2.length+1;
         } else {
             count = 2;
         }
     }

     	
// 		if( userId == loginUser) {
<%-- 			alert("<%=ORG_325%>!"); --%>
// 			return;
// 		}
     if( publish.chkApp1 != null ) {
         // 異붽??섍린 ?꾩뿉 以묐났?섎뒗寃껋씠 ?덈뒗吏 ?뺤씤
         if( publish.chkApp1.length ) {
             for(var k=0; k < publish.chkApp1.length; k++) {
                if( userInfo == publish.chkApp1[k].value ) {
                     alert("[<%=ORG_32%>] " + userId + "<%=ORG_399%>!");
                     return;
                 }
             }
         } else {
             if( userInfo == publish.chkApp1.value ) {
                 alert("[<%=ORG_32%>] " + userId + "<%=ORG_399%>!");
                 return;
             }
         }
     }
     if( publish.chkApp2 != null ) {
         // 異붽??섍린 ?꾩뿉 以묐났?섎뒗寃껋씠 ?덈뒗吏 ?뺤씤
         if( publish.chkApp2.length ) {
             for(var k=0; k < publish.chkApp2.length; k++) {
                if( userInfo == publish.chkApp2[k].value ) {
                     alert("[<%=ORG_38%>] " + userId + "<%=ORG_399%>!");
                     return;
                 }
             }
         } else {
             if( userInfo == publish.chkApp2.value ) {
                 alert("[<%=ORG_38%>] " + userId + "<%=ORG_399%>!");
                 return;
             }
         }
     }
     if( publish.chkApp3 != null ) {
         // 異붽??섍린 ?꾩뿉 以묐났?섎뒗寃껋씠 ?덈뒗吏 ?뺤씤
         if( publish.chkApp3.length ) {
             for(var k=0; k < publish.chkApp3.length; k++) {
                if( userInfo == publish.chkApp3[k].value ) {
                     alert("[<%=ORG_1091%>] " + userId + "<%=ORG_399%>!");
                     return;
                 }
             }
         } else {
             if( userInfo == publish.chkApp3.value ) {
                 alert("[<%=ORG_1091%>] " + userId + "<%=ORG_399%>!");
                 return;
             }
         }
     }
     
     
     var userRow = app2.insertRow();
         for(var k = 0; k<7; k++) {
             userCell = userRow.insertCell();
             //userCell.align = "center";
             //userCell.height = "23";
             userCell.className = "tdwhiteM";
             
             if(k == 0) {
                 userCell.innerHTML = "<input type='checkbox' name='chkApp2' class='chkbox' value='" + userInfo + "'><input type=hidden name=approveUser3 value='"+userInfoArr[0]+"'>";
                 //userCell.title = userInfo;
             } else if(k == 1) {
                 userCell.innerHTML = count;
             } else if(k == 2) {
                 userCell.innerHTML = "Agree";
             } else if(k == 3) {
                 userCell.innerHTML = userInfoArr[5];
                 userCell.title = userInfoArr[5];
             } else if(k == 4) {
                 userCell.innerHTML = userInfoArr[9];
                 userCell.title = userInfoArr[9];
             } else if(k == 5) {
                 userCell.innerHTML = userInfoArr[3];
                 userCell.title = userInfoArr[3];
             } else if(k == 6) {
                 userCell.innerHTML = userInfoArr[6];
                 userCell.title = userInfoArr[6];
             } 
         }
}


//?뚰듃留곹겕 ??젣
function delApp2() {
	try {
	 var publish = document.forms[0];
	 var myElement = document.getElementById("app2");
	
	 if (publish.chkApp2 != null) {
	     // 2媛??댁긽 ?쇰븣
	     if (publish.chkApp2.length) {
	         // 泥댄겕??由ъ뒪?몃? ??젣?쒕떎.
	         for (var i = publish.chkApp2.length-1; i >= 0 ; i--) {
	             if (i !=0) {
	                 //alert(i);
	                 //alert(document.publish.chkAppendFile[i].checked);
	                 if (publish.chkApp2[i].checked) {
	                     myElement.deleteRow(i);
	                 }
	             } else {
	                 if (myElement.rows.length > 1) {
	                     //alert(i);
	                     //alert(document.publish.chkAppendFile[i].checked);
	                     if (publish.chkApp2[i].checked) {
	                         myElement.deleteRow(i);
	                     }
	                 } else {
	                     if (publish.chkApp2[i].checked) {
	                     	myElement.deleteRow(0);
	                     }
	                 }
	             }
	         }
	     }
	     // 1媛??쇰븣
	     else if (publish.chkApp2) {
	         // 泥댄겕??由ъ뒪?몃? ??젣?쒕떎.
	         if (publish.chkApp2.checked) {
	             myElement.deleteRow(0);
	         }
	     }
	 }

		// ?쒕쾲???ㅼ떆 ?앹꽦?쒕떎.
		var countIndex = 1;
		var oRow;
		var userInfo;
	    if (publish.chkApp2.length) {
			for (var curr_row = 0; curr_row < myElement.rows.length; curr_row++) {
				oRow = myElement.rows[curr_row];
	
				userInfo = publish.chkApp2[curr_row].value;
			    oRow.cells[1].innerHTML = countIndex;
			    
			    countIndex += 1;
			}
	    } else if (publish.chkApp2) {
			for (var curr_row = 0; curr_row < myElement.rows.length; curr_row++) {
				oRow = myElement.rows[curr_row];
	
				userInfo = publish.chkApp2.value;
			    oRow.cells[1].innerHTML = countIndex;
			    
			    countIndex += 1;
			}
	    }
	}catch(e){}	    
}

function delAllApp2(){
	try {
	 var publish = document.forms[0];
 var myElement = document.getElementById("app2");

 if (publish.chkApp2 != null) {
     // 2媛??댁긽 ?쇰븣
     if (publish.chkApp2.length) {
         // 泥댄겕??由ъ뒪?몃? ??젣?쒕떎.
         for (var i = publish.chkApp2.length-1; i >= 0 ; i--) {
             if (i !=0) {
                 myElement.deleteRow(i);
             } else {
                 if (myElement.rows.length > 1) {
                     myElement.deleteRow(i);
                 } else {
                     var bName = navigator.appName;
                     if (bName == "Netscape") {
                         myElement.deleteRow(0);
                     } else {
                         myElement.deleteRow(0);
                     }
                 }
             }
         }
     }
     // 1媛??쇰븣
     else if (publish.chkApp2) {
         // 泥댄겕??由ъ뒪?몃? ??젣?쒕떎.
         myElement.deleteRow(0);
     }
 }
	}catch(e){}	    
}

///////////////////////////////////////////////////////////////////////
//?묒쓽??寃곗옱 - add app3
function addApp3(userInfo) {
	var publish = document.forms[0];

	var userInfoArr = userInfo.split(",");
//	for(i=0; i<userInfoArr.length; i++) { 
		//alert(i + " = " + userInfoArr[i]);
//	}
	var count = 1;
	var userId = userInfoArr[3];
	//var checkDup  = checkDupApp(userInfo, userId);
	//if( checkDup == "true" ) {
     //alert(userInfoArr[3] + "??以묐났?⑸땲??");
 //    return;
	//}
     if( publish.chkApp3 != null ) {
         if( publish.chkApp3.length ) {
             count = publish.chkApp3.length+1;
         } else {
             count = 2;
         }
     }

// 		if( userId == loginUser && isChief == 'false' ) {
<%-- 			alert("<%=ORG_325%>!"); --%>
// 			return;
// 		}
// if( isChief == 'false' ) {
<%-- 	alert("<%=ORG_325%>!"); --%>
// 	return;
// }
     if( publish.chkApp1 != null ) {
         // 異붽??섍린 ?꾩뿉 以묐났?섎뒗寃껋씠 ?덈뒗吏 ?뺤씤
         if( publish.chkApp1.length ) {
             for(var k=0; k < publish.chkApp1.length; k++) {
                if( userInfo == publish.chkApp1[k].value ) {
                     alert("[<%=ORG_32%>] " + userId + "<%=ORG_399%>!");
                     return;
                 }
             }
         } else {
             if( userInfo == publish.chkApp1.value ) {
                 alert("[<%=ORG_32%>] " + userId + "<%=ORG_399%>!");
                 return;
             }
         }
     }
     if( publish.chkApp2 != null ) {
         // 異붽??섍린 ?꾩뿉 以묐났?섎뒗寃껋씠 ?덈뒗吏 ?뺤씤
         if( publish.chkApp2.length ) {
             for(var k=0; k < publish.chkApp2.length; k++) {
                if( userInfo == publish.chkApp2[k].value ) {
                     alert("[<%=ORG_38%>] " + userId + "<%=ORG_399%>!");
                     return;
                 }
             }
         } else {
             if( userInfo == publish.chkApp2.value ) {
                 alert("[<%=ORG_38%>] " + userId + "<%=ORG_399%>!");
                 return;
             }
         }
     }
     if( publish.chkApp3 != null ) {
         // 異붽??섍린 ?꾩뿉 以묐났?섎뒗寃껋씠 ?덈뒗吏 ?뺤씤
         if( publish.chkApp3.length ) {
             for(var k=0; k < publish.chkApp3.length; k++) {
                if( userInfo == publish.chkApp3[k].value ) {
                     alert("[<%=ORG_1091%>] " + userId + "<%=ORG_399%>!");
                     return;
                 }
             }
         } else {
             if( userInfo == publish.chkApp3.value ) {
                 alert("[<%=ORG_1091%>] " + userId + "<%=ORG_399%>!");
                 return;
             }
         }
     }
     
     
     var userRow = app3.insertRow();
         for(var k = 0; k<7; k++) {
             userCell = userRow.insertCell();
             //userCell.align = "center";
             //userCell.height = "23";
             userCell.className = "tdwhiteM";
             
             if(k == 0) {
                 userCell.innerHTML = "<input type='checkbox' name='chkApp3' class='chkbox' value='" + userInfo + "'><input type=hidden name=approveUser4 value='"+userInfoArr[0]+"'>";
                 //userCell.title = userInfo;
             } else if(k == 1) {
                 userCell.innerHTML = count;
             } else if(k == 2) {
                 userCell.innerHTML = "Approver";
             } else if(k == 3) {
                 userCell.innerHTML = userInfoArr[5];
                 userCell.title = userInfoArr[5];
             } else if(k == 4) {
                 userCell.innerHTML = userInfoArr[9];
                 userCell.title = userInfoArr[9];
             } else if(k == 5) {
                 userCell.innerHTML = userInfoArr[3];
                 userCell.title = userInfoArr[3];
             } else if(k == 6) {
                 userCell.innerHTML = userInfoArr[6];
                 userCell.title = userInfoArr[6];
             } 
         }
}


//?뚰듃留곹겕 ??젣
function delApp3() {
	try {
	 var publish = document.forms[0];
	 var myElement = document.getElementById("app3");
	
	 if (publish.chkApp3 != null) {
	     // 2媛??댁긽 ?쇰븣
	     if (publish.chkApp3.length) {
	         // 泥댄겕??由ъ뒪?몃? ??젣?쒕떎.
	         for (var i = publish.chkApp3.length-1; i >= 0 ; i--) {
	             if (i !=0) {
	                 //alert(i);
	                 //alert(document.publish.chkAppendFile[i].checked);
	                 if (publish.chkApp3[i].checked) {
	                     myElement.deleteRow(i);
	                 }
	             } else {
	                 if (myElement.rows.length > 1) {
	                     //alert(i);
	                     //alert(document.publish.chkAppendFile[i].checked);
	                     if (publish.chkApp3[i].checked) {
	                         myElement.deleteRow(i);
	                     }
	                 } else {
	                     if (publish.chkApp3[i].checked) {
	                     	myElement.deleteRow(0);
	                     }
	                 }
	             }
	         }
	     }
	     // 1媛??쇰븣
	     else if (publish.chkApp3) {
	         // 泥댄겕??由ъ뒪?몃? ??젣?쒕떎.
	         if (publish.chkApp3.checked) {
	             myElement.deleteRow(0);
	         }
	     }
	 }

		// ?쒕쾲???ㅼ떆 ?앹꽦?쒕떎.
		var countIndex = 1;
		var oRow;
		var userInfo;
	    if (publish.chkApp3.length) {
			for (var curr_row = 0; curr_row < myElement.rows.length; curr_row++) {
				oRow = myElement.rows[curr_row];
	
				userInfo = publish.chkApp3[curr_row].value;
			    oRow.cells[1].innerHTML = countIndex;
			    
			    countIndex += 1;
			}
	    } else if (publish.chkApp3) {
			for (var curr_row = 0; curr_row < myElement.rows.length; curr_row++) {
				oRow = myElement.rows[curr_row];
	
				userInfo = publish.chkApp3.value;
			    oRow.cells[1].innerHTML = countIndex;
			    
			    countIndex += 1;
			}
	    }
	}catch(e){}	    
}

function delAllApp3(){
	try {
		 var publish = document.forms[0];
		 var myElement = document.getElementById("app3");
		
		 if (publish.chkApp3 != null) {
		     // 2媛??댁긽 ?쇰븣
		     if (publish.chkApp3.length) {
		         // 泥댄겕??由ъ뒪?몃? ??젣?쒕떎.
		         for (var i = publish.chkApp3.length-1; i >= 0 ; i--) {
		             if (i !=0) {
		                 myElement.deleteRow(i);
		             } else {
		                 if (myElement.rows.length > 1) {
		                     myElement.deleteRow(i);
		                 } else {
		                     var bName = navigator.appName;
		                     if (bName == "Netscape") {
		                         myElement.deleteRow(0);
		                     } else {
		                         myElement.deleteRow(0);
		                     }
		                 }
		             }
		         }
		     }
		     // 1媛??쇰븣
		     else if (publish.chkApp3) {
		         // 泥댄겕??由ъ뒪?몃? ??젣?쒕떎.
		         myElement.deleteRow(0);
		     }
		 }
	}catch(e){}
}

///////////////////////////////////////////////////////////////////////
// ?듬낫 - add app4
function addApp4(userInfo) {
	var publish = document.forms[0];

	var userInfoArr = userInfo.split(",");
//	for(i=0; i<userInfoArr.length; i++) { 
		//alert(i + " = " + userInfoArr[i]);
//	}
	var count = 1;
	var userId = userInfoArr[3];
	//var checkDup  = checkDupApp(userInfo, userId);
	//if( checkDup == "true" ) {
   //alert(userInfoArr[3] + "??以묐났?⑸땲??");
//    return;
	//}
   if( publish.chkApp4 != null ) {
       if( publish.chkApp4.length ) {
           count = publish.chkApp4.length+1;
       } else {
           count = 2;
       }
   }

   if( publish.chkApp4 != null ) {
       // 異붽??섍린 ?꾩뿉 以묐났?섎뒗寃껋씠 ?덈뒗吏 ?뺤씤
       if( publish.chkApp4.length ) {
           for(var k=0; k < publish.chkApp4.length; k++) {
              if( userInfo == publish.chkApp4[k].value ) {
                   alert("[<%=ORG_120%>] " + userId + "<%=ORG_399%>!");
                   return;
               }
           }
       } else {
           if( userInfo == publish.chkApp4.value ) {
               alert("[<%=ORG_120%>] " + userId + "<%=ORG_399%>!");
               return;
           }
       }
   }   
   
   var userRow = app4.insertRow();
       for(var k = 0; k<7; k++) {
           userCell = userRow.insertCell();
           //userCell.align = "center";
           //userCell.height = "23";
           userCell.className = "tdwhiteM";
           
           if(k == 0) {
               userCell.innerHTML = "<input type='checkbox' name='chkApp4' class='chkbox' value='" + userInfo + "'><input type=hidden name=tempUser value='"+userInfoArr[0]+"'>";
               //userCell.title = userInfo;
           } else if(k == 1) {
               userCell.innerHTML = count;
           } else if(k == 2) {
               userCell.innerHTML = "Report";
           } else if(k == 3) {
               userCell.innerHTML = userInfoArr[5];
               userCell.title = userInfoArr[5];
           } else if(k == 4) {
               userCell.innerHTML = userInfoArr[9];
               userCell.title = userInfoArr[9];
           } else if(k == 5) {
               userCell.innerHTML = userInfoArr[3];
               userCell.title = userInfoArr[3];
           } else if(k == 6) {
               userCell.innerHTML = userInfoArr[6];
               userCell.title = userInfoArr[6];
           } 
       }
}


//?뚰듃留곹겕 ??젣
function delApp4() {

	try {
	 var publish = document.forms[0];
	 var myElement = document.getElementById("app4");
	
	 if (publish.chkApp4 != null) {
	     // 2媛??댁긽 ?쇰븣
	     if (publish.chkApp4.length) {
	         // 泥댄겕??由ъ뒪?몃? ??젣?쒕떎.
	         for (var i = publish.chkApp4.length-1; i >= 0 ; i--) {
	             if (i !=0) {
	                 //alert(i);
	                 //alert(document.publish.chkAppendFile[i].checked);
	                 if (publish.chkApp4[i].checked) {
	                     myElement.deleteRow(i);
	                 }
	             } else {
	                 if (myElement.rows.length > 1) {
	                     //alert(i);
	                     //alert(document.publish.chkAppendFile[i].checked);
	                     if (publish.chkApp4[i].checked) {
	                         myElement.deleteRow(i);
	                     }
	                 } else {
	                     if (publish.chkApp4[i].checked) {
	                     	myElement.deleteRow(0);
	                     }
	                 }
	             }
	         }
	     }
	     // 1媛??쇰븣
	     else if (publish.chkApp4) {
	         // 泥댄겕??由ъ뒪?몃? ??젣?쒕떎.
	         if (publish.chkApp4.checked) {
	             myElement.deleteRow(0);
	         }
	     }
	 }

		// ?쒕쾲???ㅼ떆 ?앹꽦?쒕떎.
		var countIndex = 1;
		var oRow;
		var userInfo;
	    if (publish.chkApp4.length) {
			for (var curr_row = 0; curr_row < myElement.rows.length; curr_row++) {
				oRow = myElement.rows[curr_row];
	
				userInfo = publish.chkApp4[curr_row].value;
			    oRow.cells[1].innerHTML = countIndex;
			    
			    countIndex += 1;
			}
	    } else if (publish.chkApp4) {
			for (var curr_row = 0; curr_row < myElement.rows.length; curr_row++) {
				oRow = myElement.rows[curr_row];
	
				userInfo = publish.chkApp4.value;
			    oRow.cells[1].innerHTML = countIndex;
			    
			    countIndex += 1;
			}
	    }
	}catch(e){}	    
}

function delAllApp4(){

		try {	
			 var publish = document.forms[0];
			var myElement = document.getElementById("app4");
			
			if (publish.chkApp4 != null) {
			   // 2媛??댁긽 ?쇰븣
			   if (publish.chkApp4.length) {
			       // 泥댄겕??由ъ뒪?몃? ??젣?쒕떎.
			       for (var i = publish.chkApp4.length-1; i >= 0 ; i--) {
			           if (i !=0) {
			               myElement.deleteRow(i);
			           } else {
			               if (myElement.rows.length > 1) {
			                   myElement.deleteRow(i);
			               } else {
			                   var bName = navigator.appName;
			                   if (bName == "Netscape") {
			                       myElement.deleteRow(0);
			                   } else {
			                       myElement.deleteRow(0);
			                   }
			               }
			           }
			       }
			   }
			   // 1媛??쇰븣
			   else if (publish.chkApp4) {
			       // 泥댄겕??由ъ뒪?몃? ??젣?쒕떎.
			       myElement.deleteRow(0);
			   }
			}
		}catch(e){}

}

function deleteAllType() {
	delAllApp1();
	delAllApp2();
	delAllApp3();
	delAllApp4();	
}

///////////////////////////////////////////////////////////////////////
// Ajax - User Search

var req = null;

function getObject(){
	 if(window.ActiveXObject){
	  return new ActiveXObject("Microsoft.XMLHTTP");
	 }else if(window.XMLHttpRequest){
	  return new XMLHttpRequest();
 	}else return null;
}

function keySearch() {
	  var inputVal = document.forms[0].userKey.value;
	  
	  if(inputVal.length < 2){
	  	alert("<%=ORG_1113%>!");
	  	return;
	  }

	  var params = ajaxFormAction();
	  req = getObject();
	  if(req){
	   	req.onreadystatechange = processUserListChange;   
	   	//req.open("GET", "/Windchill/netmarkets/jsp/narae/workspace/approval/AjaxSearchUser.jsp?userKey=" + inputVal, true);
	   	//req.send(null);

	   	req.open("POST", "/Windchill/netmarkets/jsp/narae/workspace/approval/AjaxSearchUser.jsp", true);
		req.setRequestHeader("Content-type", "application/x-www-form-urlencoded;");
//  		req.setRequestHeader("Content-length", params.length); 
//  		req.setRequestHeader("Connection", "close"); 
		req.send(params);
		
	  } 
}

/////////////////////////////////////////////////////////////////////////////// 크롬 selectSingleNode 활성화

// check for XPath implementation
if (document.implementation.hasFeature("XPath", "3.0")) {

    // prototying the XMLDocument.selectNodes
    XMLDocument.prototype.selectNodes = function(cXPathString, xNode) {
        if (!xNode) { xNode = this; }

        var oNSResolver = document.createNSResolver(this.ownerDocument == null ? this.documentElement : this.ownerDocument.documentElement);
        function resolver() {
            return 'http://schemas.saarchitect.net/ajax/2008/09/user';
        }

        var aItems = this.evaluate(cXPathString, xNode, resolver, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null);
        var aResult = [];
        for (var i = 0; i < aItems.snapshotLength; i++) {
            aResult[i] = aItems.snapshotItem(i);
        }
        return aResult;
    }

    // prototying the Element
    Element.prototype.selectNodes = function(cXPathString) {
        if (this.ownerDocument.selectNodes) {
            return this.ownerDocument.selectNodes(cXPathString, this);
        }
        else { throw "For XML Elements Only"; }
    }

    // prototying the XMLDocument.selectSingleNode
    XMLDocument.prototype.selectSingleNode = function(cXPathString, xNode) {
        if (!xNode) { xNode = this; }
        var xItems = this.selectNodes(cXPathString, xNode);
        if (xItems.length > 0) {
            return xItems[0];
        }
        else {
            return null;
        }
    }

    // prototying the Element
    Element.prototype.selectSingleNode = function(cXPathString) {
        if (this.ownerDocument.selectSingleNode) {
            return this.ownerDocument.selectSingleNode(cXPathString, this);
        }
        else { throw "For XML Elements Only"; }
    }
};
///////////////////////////////////////////////////////////////////////////////

function processUserListChange(){
	 if(req.readyState == 4){
		  if(req.status == 200){
			  console.log(req);
			  var xmlDoc = req.responseXML;
			  var users = xmlDoc.getElementsByTagName("user");
			   FillNodes(users);    
		  }else{
		   	alert("Error : " + req.statusText);
		  }
	 }  
} 
function FillNodes(nodes){
	 clearModelsList();

		var useroid = "";
		var peopleoid = "";
		var deptoid = "";
		var id = "";
		var name = "";

		var departmentname = "";
		var duty = "";
		var dutycode = "";
		var email = "";
		var temp = "";

	    var userList = document.getElementById("userList");

	    if( nodes.length == 0 ) {
			alert("<%=ORG_1097%>!");
			return;
	    }
	    
	 for(var i = 0; i < nodes.length; i++){
		 
//		Chrome에서 작동 X
// 		useroid = nodes[i].selectSingleNode("useroid").text;
// 		peopleoid = nodes[i].selectSingleNode("peopleoid").text;
// 		deptoid = nodes[i].selectSingleNode("deptoid").text;
// 		id = nodes[i].selectSingleNode("id").text;
// 		name = nodes[i].selectSingleNode("name").text;

// 		departmentname = nodes[i].selectSingleNode("departmentname").text;
// 		duty = nodes[i].selectSingleNode("duty").text;
// 		dutycode = nodes[i].selectSingleNode("dutycode").text;
// 		email = nodes[i].selectSingleNode("email").text;
// 		temp = nodes[i].selectSingleNode("temp").text;

		useroid = nodes[i].getElementsByTagName("useroid")[0].childNodes[0].nodeValue;
		peopleoid = nodes[i].getElementsByTagName("peopleoid")[0].childNodes[0].nodeValue;
		deptoid = nodes[i].getElementsByTagName("deptoid")[0].childNodes[0].nodeValue;
		id = nodes[i].getElementsByTagName("id")[0].childNodes[0].nodeValue;
		name = nodes[i].getElementsByTagName("name")[0].childNodes[0].nodeValue;

		departmentname = nodes[i].getElementsByTagName("departmentname")[0].childNodes[0].nodeValue;
		if(departmentname == "퇴사인원"){
			continue;
		}
		duty = nodes[i].getElementsByTagName("duty")[0].childNodes[0].nodeValue;
		dutycode = nodes[i].getElementsByTagName("dutycode")[0].childNodes[0].nodeValue;
		console.log(nodes[i].getElementsByTagName("email")[0]);
		email = nodes[i].getElementsByTagName("email")[0].childNodes[0].nodeValue;
		temp = nodes[i].getElementsByTagName("temp")[0].childNodes[0].nodeValue;
		
        var values = useroid + "," + peopleoid + "," + deptoid + "," + id + "," + name + "," + 
        					departmentname + "," + duty + "," + dutycode + "," + email + "," + temp;
        console.log(values);
        var tests = temp + "/" + duty + "/" + departmentname;
        
        var newOpt=document.createElement("OPTION");
        newOpt.text=tests;
        newOpt.name=values;
        newOpt.value=values;
        newOpt.title = tests;
        userList.add(newOpt);
	 }
	 return;
}

function clearModelsList() {
    var models = document.getElementById("userList");
    while(models.childNodes.length > 0) {
        models.removeChild(models.childNodes[0]);
    }
}

function inputLine() {
// 		var url = "/Windchill/netmarkets/jsp/narae/workspace/approval/InputApprovalLine.jsp";
// 		var attache = window.showModalDialog(url,"InputApprovalLine","help=no; scroll=no; resizable=no; dialogWidth=360px; dialogHeight:200px;");
// 		return attache

	var url = "/Windchill/netmarkets/jsp/narae/workspace/approval/InputApprovalLine.jsp";
	var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=no,";
    var popWidth = 600;
    var popHeight = 300;
    var leftpos = (screen.width - popWidth) / 2;
    var toppos = (screen.height - popHeight) / 2;
    var rest = "width="+popWidth+",height="+popHeight+",left=" + leftpos + ',top=' + toppos;
    
    var newwin = window.open(url, "", opts+rest);
    newwin.focus();
    
//     console.log("newwin 값 = " + newwin);
//     return newwin;
}

function createLine() {
	//var lineTitle = inputLine();
	var lineTitle = document.getElementById("title").value;
	console.log("createLine = " + lineTitle);
	if(lineTitle == null || lineTitle == "" || lineTitle == "cancel") 
		return;
	//alert("title1 = " + lineTitle);
	document.forms[0].title.value = lineTitle;
	//alert("title2 = " + document.forms[0].title.value);
	
	var params = ajaxFormAction();
	
	  req = getObject();
	  if(req){
		  console.log("params = " + params);
	   	req.onreadystatechange = processCreateLine;
	   	req.open("POST", "/Windchill/netmarkets/jsp/narae/workspace/approval/AjaxCreateLine.jsp", true);
		req.setRequestHeader("Content-type", "application/x-www-form-urlencoded;");
// 		req.setRequestHeader("Content-length", params.length); 
// 		req.setRequestHeader("Connection", "close"); 
		req.send(params);
		
	  } 
}

//AJAX POST SEND
var ajaxFormProcessing = false;
var ajaxProcessing = false;
var ajaxXmlHttp = null;
var ajaxForm = null;

function ajaxFormAction() { 
	ajaxForm = document.forms[0]; 
	ajaxFormProcessing = true;
	var inputObjs = ajaxForm.getElementsByTagName("input");
	var param = ""; 
	for(var i=0;i<inputObjs.length;i++) {
		var inputObj = inputObjs[i];  
		if (inputObj.name && inputObj.value) {
			if (inputObj.type == "text" || inputObj.type == "hidden" || inputObj.type == "password") {
				param += inputObj.name + "=" + encodeURIComponent(inputObj.value) + "&";   
			} else if (inputObj.type == "radio" || inputObj.type == "checkbox") {
				if (inputObj.checked)
					param += inputObj.name + "=" + encodeURIComponent(inputObj.value) + "&";   
			}  
		} 
	}
		var selectObjs = ajaxForm.getElementsByTagName("select"); 
		for(var i=0;i<selectObjs.length;i++) {
			var selectObj = selectObjs[i];  
			if (selectObj.name && selectObj.value) {
				param += selectObj.name + "=" + encodeURIComponent(selectObj.value) + "&";  
			}
		}
		var textAreaObjs = ajaxForm.getElementsByTagName("textarea"); 
		for(var i=0;i<textAreaObjs.length;i++) {
			if (textAreaObjs[i].name && textAreaObjs[i].value) {   
				param += textAreaObjs[i].name + "=" + encodeURIComponent(textAreaObjs[i].value) + "&";  
			}
		}
		param += "1=1";

		return param;
}

function processCreateLine(){
	 if(req.readyState == 4){
		  if(req.status == 200){
			  var xmlDoc = req.responseXML;
			  var messages = xmlDoc.getElementsByTagName("template");

			  if( messages.length == 0 ) {
				    alert("messages.length == 0");
					return;
			    }
			  //var message = messages[0].selectSingleNode("message").text;
 			  var message = messages[0].getElementsByTagName("message")[0].childNodes[0].nodeValue;

			  alert( message );
			  refreshLine();			  
		  }else{
		   	alert("Error : " + req.statusText);
		  }
	 }
} 

function deleteLine() {
	var selectedLine = "";
	
	var form = document.forms[0];
	for (i=0;i< form.lineList.length;i++){
        if (form.lineList[i].selected == true) {
        	selectedLine = form.lineList[i].value;
        }
	}

	if( selectedLine == "" ) {
		alert("<%=ORG_637%>!");
		return;
	}
	
<%-- 	if (!confirm("[" + selectedLine + "]<%=ORG_410%>?")) { --%>
	if(!confirm("결재선을 삭제하시겠습니까?")) {
		return; 
	}

	selectedLine = "";
	
	for (i=0;i< form.lineList.length;i++){
        if (form.lineList[i].selected == true) {
        	selectedLine = form.lineList[i].value;
        }
	}

	  req = getObject();
	  if(req){
	   	req.onreadystatechange = processDeleteLine;   
	   	req.open("GET", "/Windchill/netmarkets/jsp/narae/workspace/approval/AjaxDeleteApproverTemplate.jsp?delOid=" + selectedLine, true);
	   	req.send(null);
	  } 
}

function processDeleteLine(){
	 if(req.readyState == 4){
		  if(req.status == 200){
			  var xmlDoc = req.responseXML;
			  var messages = xmlDoc.getElementsByTagName("template");

			    if( messages.length == 0 ) {
				    alert("messages.length == 0");
					return;
			    }
// 			  var message = messages[0].selectSingleNode("message").text;
			  var message = messages[0].getElementsByTagName("message")[0].childNodes[0].nodeValue;

			  //alert( message );
			  refreshLine();			  
		  }else{
		   	alert("Error : " + req.statusText);
		  }
	 }
} 

function getLine() {
	//alert("getLine");
	var selectedLine = "";
	
	var form = document.forms[0];
	for (i=0;i< form.lineList.length;i++){
        if (form.lineList[i].selected == true) {
        	selectedLine = form.lineList[i].name;
        }
	}

	if( selectedLine == "" ) {
		alert("<%=ORG_1112%>!");
		return;
	}
	if (!confirm("[" + selectedLine + "]<%=ORG_409%>?\n<%=ORG_1116%>!")) {
		return; 
	}

	selectedLine = "";
	
	for (i=0;i< form.lineList.length;i++){
        if (form.lineList[i].selected == true) {
        	selectedLine = form.lineList[i].value;
        }
	}

	  req = getObject();
	  if(req){
	   	req.onreadystatechange = processGetLine;   
	   	req.open("GET", "/Windchill/netmarkets/jsp/narae/workspace/approval/AjaxGetApproverTemplate.jsp?getOid=" + selectedLine, true);
	   	req.send(null);
	  } 
	
	// deleteAllType();
}


function processGetLine(){
	 if(req.readyState == 4){
		  if(req.status == 200){
			  var xmlDoc = req.responseXML;
			  var users = xmlDoc.getElementsByTagName("user");
			  GetFillNodes(users);    

			  //refreshLine();			  
		  }else{
		   	alert("Error : " + req.statusText);
		  }
	 }
} 


function GetFillNodes(nodes){
	deleteAllType();

		var type = "";
		var userinfo = "";

	    if( nodes.length == 0 ) {
			alert("<%=ORG_1097%>!");
			return;
	    }
	    
	 for(var i = 0; i < nodes.length; i++){
// 		type = nodes[i].selectSingleNode("type").text;
// 		userInfo = nodes[i].selectSingleNode("userino").text;
		//console.log(nodes[i].getElementsByTagName("userinfo")[0]);
		type = nodes[i].getElementsByTagName("type")[0].childNodes[0].nodeValue;
		userInfo = nodes[i].getElementsByTagName("userinfo")[0].childNodes[0].nodeValue;
    	if( type == "pre" ) {
    		addApp1(userInfo);
    	} else if( type == "disc" ) {
    		addApp2(userInfo);
    	} else if( type == "post" ) {
    		addApp3(userInfo);
    	} else if( type == "noti" ) {
    		addApp4(userInfo);
    	}
	 }
	 return;
}

function viewLine() {
	var selectedLine = "";
	
	var form = document.forms[0];
	for (i=0;i< form.lineList.length;i++){
        if (form.lineList[i].selected == true) {
        	selectedLine = form.lineList[i].value;
        }
	}

	if( selectedLine == "" ) {
		alert("<%=ORG_1071%>!");
		return;
	}
	
	var url = "/Windchill/netmarkets/jsp/narae/workspace/approval/ViewApproverTemplate.jsp?oid=" + selectedLine;

	var popWidth = "670";
	var popHeight = "300";
	var userScreenWidth = screen.availWidth / 2 - popWidth / 2;
	var userScreenHeight = screen.availHeight / 2 - popHeight / 2;
	
	var styles = "titlebar=no,toolbar=no,location=no,status=no,menubar=no,scrollbars=yes,resizable=no,width=670,height=300,left=" + userScreenWidth + ", top=" + userScreenHeight;
	
	userSearchPop = window.open(url,"",styles);
	userSearchPop.focus();
}

function refreshLine() {
	  req = getObject();
	  if(req){
	   	req.onreadystatechange = processLineChange;   
	   	req.open("GET", "/Windchill/netmarkets/jsp/narae/workspace/approval/AjaxSearchApproverTemplate.jsp", true);
	   	req.send(null);
	  } 
}

function processLineChange(){
	 if(req.readyState == 4){
		  if(req.status == 200){
			  var xmlDoc = req.responseXML;
			  var users = xmlDoc.getElementsByTagName("template");
			   FillNodesLine(users);    
		  }else{
		   	alert("Error : " + req.statusText);
		  }
	 }  
} 

function FillNodesLine(nodes){
	 clearLineModelsList();

		var title = "";
		var oid = "";

	    var lineList = document.getElementById("lineList");
	 for(var i = 0; i < nodes.length; i++){
// 		title = nodes[i].selectSingleNode("title").text;
// 		oid = nodes[i].selectSingleNode("oid").text;
		
		title = nodes[i].getElementsByTagName("title")[0].childNodes[0].nodeValue;
		oid = nodes[i].getElementsByTagName("oid")[0].childNodes[0].nodeValue;

       var newOpt=document.createElement("OPTION");
      	
       newOpt.text=title;
       newOpt.name=title;
       newOpt.value=oid;
       newOpt.title = title;
       lineList.add(newOpt);
	 }

	 return;
}

function clearLineModelsList() {
    var models = document.getElementById("lineList");
    while(models.childNodes.length > 0) {
        models.removeChild(models.childNodes[0]);
    }
    
}

var parentFrm;

///////////////////////////////////////////////////////////////////////
//	load users

function saveUser() {
	
	
	var publish = document.forms[0];
	//var arr =checkList();
	//window.returnValue = arr;
	
	// ?댁쟾 由ъ뒪??珥덇린??
	if(approveType =="Last"){
		opener.deleteAllLast();
	}else{
		opener.deleteAll();
	}
	

	var row_num = 0;
	var row_data;
	var arr_data;
//?쒖꽌 	援щ텇 	遺??	 ?대쫫 	ID 	吏곴툒 
//function addApprovalLine2(oid, name, department, id, position, userInfo) {
//			oRow = myElement.rows[curr_row];
//			userInfo = publish.chkApp4[curr_row].value;
		if( publish.chkApp1 != null )
		if (publish.chkApp1.length) {
			for (var curr_row = 0; curr_row < publish.chkApp1.length; curr_row++) {
				row_data = publish.chkApp1[curr_row].value;
				arr_data = row_data.split(",");
				if(approveType =="Last"){
					opener.addApprovalLineLast2(arr_data[0], arr_data[4], arr_data[5], arr_data[3], arr_data[6], row_data);
				}else{
					opener.addApprovalLine2(arr_data[0], arr_data[4], arr_data[5], arr_data[3], arr_data[6], row_data);
				}
				
			}
	    } else if (publish.chkApp1) {
			row_data = publish.chkApp1.value;
			arr_data = row_data.split(",");
			if(approveType =="Last"){
				opener.addApprovalLineLast2(arr_data[0], arr_data[4], arr_data[5], arr_data[3], arr_data[6], row_data);
			}else{
				opener.addApprovalLine2(arr_data[0], arr_data[4], arr_data[5], arr_data[3], arr_data[6], row_data);
			}
			
	    }

		if( publish.chkApp2 != null )
		if (publish.chkApp2.length) {
			for (var curr_row = 0; curr_row < publish.chkApp2.length; curr_row++) {
				row_data = publish.chkApp2[curr_row].value;
				arr_data = row_data.split(",");
				if(approveType =="Last"){
					opener.addApprovalLineLast3(arr_data[0], arr_data[4], arr_data[5], arr_data[3], arr_data[6], row_data);
				}else{
					opener.addApprovalLine3(arr_data[0], arr_data[4], arr_data[5], arr_data[3], arr_data[6], row_data);
				}
				
			}
	    } else if (publish.chkApp2) {
			row_data = publish.chkApp2.value;
			arr_data = row_data.split(",");
			if(approveType =="Last"){
				opener.addApprovalLineLast3(arr_data[0], arr_data[4], arr_data[5], arr_data[3], arr_data[6], row_data);
			}else{
				opener.addApprovalLine3(arr_data[0], arr_data[4], arr_data[5], arr_data[3], arr_data[6], row_data);
			}
			
	    }

		if( publish.chkApp3 != null )
		if (publish.chkApp3.length) {
			for (var curr_row = 0; curr_row < publish.chkApp3.length; curr_row++) {
				row_data = publish.chkApp3[curr_row].value;
				arr_data = row_data.split(",");
				if(approveType =="Last"){
					opener.addApprovalLineLast4(arr_data[0], arr_data[4], arr_data[5], arr_data[3], arr_data[6], row_data);
				}else{
					opener.addApprovalLine4(arr_data[0], arr_data[4], arr_data[5], arr_data[3], arr_data[6], row_data);
				}
				
			}
	    } else if (publish.chkApp3) {
			row_data = publish.chkApp3.value;
			arr_data = row_data.split(",");
			if(approveType =="Last"){
				opener.addApprovalLineLast4(arr_data[0], arr_data[4], arr_data[5], arr_data[3], arr_data[6], row_data);
			}else{
				opener.addApprovalLine4(arr_data[0], arr_data[4], arr_data[5], arr_data[3], arr_data[6], row_data);
			}
			
	    }

		if( publish.chkApp4 != null )
		if (publish.chkApp4.length) {
			for (var curr_row = 0; curr_row < publish.chkApp4.length; curr_row++) {
				row_data = publish.chkApp4[curr_row].value;
				arr_data = row_data.split(",");
				if(approveType =="Last"){
					opener.addTempLineLast(arr_data[0], arr_data[4], arr_data[5], arr_data[3], arr_data[6], row_data);
				}else{
					opener.addTempLine(arr_data[0], arr_data[4], arr_data[5], arr_data[3], arr_data[6], row_data);
				}
				
			}
	    } else if (publish.chkApp4) {
			row_data = publish.chkApp4.value;
			arr_data = row_data.split(",");
			if(approveType =="Last"){
				opener.addTempLineLast(arr_data[0], arr_data[4], arr_data[5], arr_data[3], arr_data[6], row_data);
			}else{
				opener.addTempLine(arr_data[0], arr_data[4], arr_data[5], arr_data[3], arr_data[6], row_data);
			}
			
	    }
	    
	window.close();
}

function loadApproveUser(){

		try {
			var user2 = opener.getInfoUser2();
			if (user2 != null)
		    if (user2.length) {
				for (var curr_row = 0; curr_row < user2.length; curr_row++) {
					addApp1(user2[curr_row].value);
				}
		    } else {
		    	addApp1(user2.value);
		    }
		} catch(e){}
		try {
			var user3 = opener.getInfoUser3();
		    if (user3 != null)
		    if (user3.length ) {
				for (var curr_row = 0; curr_row < user3.length; curr_row++) {
					addApp2(user3[curr_row].value);
				}
		    } else {
		    	addApp2(user3.value);
		    }
		} catch(e){}
		try {
			var user4 = opener.getInfoUser4();
		    if (user4 != null)
		        if (user4.length ) {
				for (var curr_row = 0; curr_row < user4.length; curr_row++) {
					addApp3(user4[curr_row].value);
				}
		    } else {
		    	addApp3(user4.value);
		    }
		
		} catch(e){}
		try {
			var user = opener.getInfoUser();
		    if (user != null)
		        if (user.length ) {
				for (var curr_row = 0; curr_row < user.length; curr_row++) {
					addApp4(user[curr_row].value);
				}
		    } else {
		    	addApp4(user.value);
		    }
		} catch(e){}
}

function loadApproveUserLast(){

	try {
		var user2 = opener.getInfoUserLast2();
		if (user2 != null)
	    if (user2.length) {
			for (var curr_row = 0; curr_row < user2.length; curr_row++) {
				addApp1(user2[curr_row].value);
			}
	    } else {
	    	addApp1(user2.value);
	    }
	} catch(e){}
	try {
		var user3 = opener.getInfoUserLast3();
	    if (user3 != null)
	    if (user3.length ) {
			for (var curr_row = 0; curr_row < user3.length; curr_row++) {
				addApp2(user3[curr_row].value);
			}
	    } else {
	    	addApp2(user3.value);
	    }
	} catch(e){}
	try {
		var user4 = opener.getInfoUserLast4();
	    if (user4 != null)
	        if (user4.length ) {
			for (var curr_row = 0; curr_row < user4.length; curr_row++) {
				addApp3(user4[curr_row].value);
			}
	    } else {
	    	addApp3(user4.value);
	    }
	
	} catch(e){}
	try {
		var user = opener.getInfoUserLast();
	    if (user != null)
	        if (user.length ) {
			for (var curr_row = 0; curr_row < user.length; curr_row++) {
				addApp4(user[curr_row].value);
			}
	    } else {
	    	addApp4(user.value);
	    }
	} catch(e){}
}
</script>
</head>

<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" >
<table width="900" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td height="40" align="center"><table width="100%" border="0" align="center" cellpadding="0" cellspacing="1" bgcolor=8f2436 class=9pt>
        <tr> 
          <td height=30 width=93% align=center><B><font color=white><%=ORG_1062%></td>
<%--           <td height="23"   class="a_con_01"><%=ORG_1062%></td> --%>
        </tr>
    </table></td>
  </tr>
  <tr> 
    <td><table width="880" border="0" align="center" cellpadding="0" cellspacing="0">
        <tr> 
          <td width="10" valign="top">&nbsp;</td>
<!-- tab search   -->  
          <td width="310" valign="top"  id=leftSearch style="display: block"><table width="200" border="0" align="center" cellpadding="0" cellspacing="1" bgcolor=#cfcfcf class=9pt>
              <tr>
                <td height="30"   class="border_text_03-1">
<table width="228" height="27" border="0" align="center" cellpadding="0" cellspacing="0">
                    <tr> 
                      <td width="76" align="center" background="/Windchill/netmarkets/jsp/narae/img/k_bt_01_ov.gif"><table width="60" border="0" cellspacing="0" cellpadding="0">
                          <tr> 
                            <td height="23" valign="middle" ><div align="center"><a href="javascript:leftView('leftSearch');"><font color="#FFFFFF"><strong>Search</strong></font></a></div></td>
                          </tr>
                        </table></td>
                      <td width="7">&nbsp;</td>
                      <td width="62" background="/Windchill/netmarkets/jsp/narae/img/k_bt_02.gif"><table width="50" border="0" align="center" cellpadding="0" cellspacing="0">
                          <tr> 
                            <td width="61" height="17"><div align="center"><a href="javascript:leftView('leftTree');" target="_parent"><strong><font color="#FFFFFF">Org</font></strong></a></div></td>
                          </tr>
                        </table></td>
                      <td width="7">&nbsp;</td>
                      <td width="76" background="/Windchill/netmarkets/jsp/narae/img/k_bt_03.gif"><table width="71" border="0" align="center" cellpadding="0" cellspacing="0">
                          <tr> 
                            <td width="71"><div align="center"><a href="javascript:leftView('leftLine');" target="_parent"><strong><font color="#FFFFFF">App.Line</font></strong></a></div></td>
                          </tr>
                        </table></td>
                    </tr>
                  </table> 
                  
                </TD>
              </TR>
              <tr>
                <td height="20"   class="border_text_03-1"><table width="75%" height="25" border="0" align="center" cellpadding="0" cellspacing="0">
                    <tr>
                      <td height="28"><%=ORG_400%> <%=ORG_1255%> ID <%=ORG_1098%></td>
                    </tr>
                    <tr>
                      <td height="30"><table width="200" border="0" cellspacing="0" cellpadding="0">
                          <tr>
                            <td height="32"><input name="userKey" type="text" class="input1" id="userKey" size="18" maxlength="20"  OnKeyDown="if(event.keyCode==13) windEdit_Enter(2);"  value="<%=userKey%>"></td>
                            <td><a href="javascript:keySearch();"> <img src="/Windchill/netmarkets/jsp/narae/img/search_bt.gif" alt="" width="50" height="18" border="0"></a></td>
                          </tr>
                      </table></td>
                    </tr>
                  </table></TD>
              </TR>
              <tr>
                <td   class="border_text_03-1"><table width="230" height="382" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td width="213" height="382"><span style="border:1px;overflow-x:auto;overflow-y:auto; width:230px;height:382px;">
              				<table width="213" border="0" cellpadding="0" cellspacing="1" bgcolor=#cfcfcf class=9pt>
				                  <tr>
                            <td  class="border_text_03-1"> <select name="userList" id="userList" size=20 title="" style="overflow-y:hidden;overflow-x:hidden; width:300px; height:600px;" ><div name="divUser" id="divUser" style="width:253; height:382; overflow-y:hidden;overflow-x:hidden; padding:10px; border:0; border-style:solid; border-color:#EBEBEB"> </DIV> 
                              </select></TD>
	                          </tr>
	                 	</table>
              			</span>
              		</div>
                      </td>
                    </tr>
                </table>   </TD>
              </TR>
            </table>
          </td>
<!-- tab tree   -->  
          <td width="315" valign="top"  id=leftTree style="display: none">
          <table width="200" border="0" align="center" cellpadding="0" cellspacing="1" bgcolor=#cfcfcf class=9pt>
              <tr>
                <td height="30"   class="border_text_03-1"><table width="228" height="27" border="0" align="center" cellpadding="0" cellspacing="0">
                    <tr> 
                      <td width="76" background="/Windchill/netmarkets/jsp/narae/img/k_bt_01.gif"><table width="60" border="0" align="center" cellpadding="0" cellspacing="0">
                          <tr> 
                            <td width="71"><div align="center"><a href="javascript:leftView('leftSearch');" target="_parent"><strong><font color="#FFFFFF">Search</font></strong></a></div></td>
                          </tr>
                        </table></td>
                      <td width="7">&nbsp;</td>
                      <td width="62" background="/Windchill/netmarkets/jsp/narae/img/k_bt_02_ov.gif"><table width="50" border="0" align="center" cellpadding="0" cellspacing="0">
                          <tr> 
                            <td width="61" height="17"><div align="center"><a href="javascript:leftView('leftTree');" target="_parent"><strong><font color="#FFFFFF">Org</font></strong></a></div></td>
                          </tr>
                        </table></td>
                      <td width="7">&nbsp;</td>
                      <td width="76" background="/Windchill/netmarkets/jsp/narae/img/k_bt_03.gif"><table width="71" border="0" align="center" cellpadding="0" cellspacing="0">
                          <tr> 
                            <td width="71"><div align="center"><a href="javascript:leftView('leftLine');" target="_parent"><font color="#FFFFFF"><strong>App.Line</strong></font></a></div></td>
                          </tr>
                        </table></td>
                    </tr>
                  </table>
                  
                </TD>
              </TR>
              <tr>
                <td height="20"   class="border_text_03-1"><table width=230  height=442>
                  <tr>
                    <td valign=top>
                    <DIV style="width:280; height:612; overflow:scroll; padding:10px; border:0; border-style:solid; border-color:#EBEBEB">
                        <script type="text/javascript">
		var departmentTree = new dTree("departmentTree","/Windchill/netmarkets/jsp/narae/org/images/tree");
//		departmentTree.add(0,-1,'ROOT',"JavaScript:setDepartment('root');");
		departmentTree.add(0,-1,"나래","");
<%
		
		

		QuerySpec qs = new QuerySpec(People.class);
		int ii = qs.addClassList(People.class,true);
		int jj = qs.addClassList(WTUser.class,true);
		int kk = qs.addClassList(Department.class,true);
		
		qs.appendWhere(new SearchCondition(People.class,"departmentReference.key.id",
				Department.class,"thePersistInfo.theObjectIdentifier.id"),new int[]{ii,kk});
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(People.class,"userReference.key.id",
				WTUser.class,"thePersistInfo.theObjectIdentifier.id"),new int[]{ii,jj});
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(People.class, People.DUTY_CODE, "<>", "DC_10"),new int[]{ii});
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(People.class, People.DUTY_CODE, "<>", "DC_09"),new int[]{ii});
		qs.appendOrderBy(new OrderBy(new ClassAttribute(People.class,"dutyCode"),false),new int[]{ii});
		
		QueryResult qr = PersistenceHelper.manager.find(qs);

		Object[] o = null;
		People people = null;
		Department dept1 = null;
		WTUser user = null;
		String title = null;
		String duty = null;
		
		if( qr != null ){ // System.out.println("people size = " + qr.size());
		while(qr.hasMoreElements()){
			o = (Object[])qr.nextElement();
			people = (People)o[0];
			dept1 = people.getDepartment();
			user = (WTUser)o[1];
			title = StringUtil.checkNull( people.getTitle() );
			if(title!=null && !title.equals("") && !title.equals("null")){
				title = title + "/";
			} else {
				title = "";
			}
			
			duty = StringUtil.checkNull( people.getDuty() ) ;
			if(duty!=null && !duty.equals("") && !duty.equals("null")){
				duty = duty;
			} else {
				duty = "";
			}
			//System.out.println("user.getName() = " + user.getName());
			//System.out.println("dept1.getName() = " + dept1.getName());
			//System.out.println("people.getName() = " + people.getName());
			//System.out.println("user.getEMail() = " + user.getEMail());
			//System.out.println("dept1 = " + dept1.toString());
			//System.out.println("dept1 id = " + dept1.getPersistInfo().getObjectIdentifier().getId());

//	value=PersistenceHelper.getObjectIdentifier ( user ).getStringValue ()
//	poid=PersistenceHelper.getObjectIdentifier ( people ).getStringValue () 
//	email=user.getEMail()==null?"":user.getEMail()
//	dname=dept1!=null?dept1.getName()
//	duty=people.getDuty()==null?"":people.getDuty() 
//	uid=user.getName()
//	sname=user.getFullName() 
//	doid=dept1!=null?PersistenceHelper.getObjectIdentifier ( dept1 ).getStringValue () 
//	dutycode=people.getDutyCode()==null?"":people.getDutyCode()
//people.getName() ?대쫫
//people.getDuty()=null?"&nbsp;":people.getDuty() 吏곸콉
//dept1!=null?dept1.getName():"&nbsp 遺??
//user.getEMail()==null?"":user.getEMail() ?대찓??

%>
			departmentTree.add("<%=user.getName()%>","<%=dept1.getPersistInfo().getObjectIdentifier().getId()%>",
					"<%=people.getName()%>/<%=title%><%=duty%>",
					"JavaScript:setTreeUser('<%=StringUtil.checkNull( PersistenceHelper.getObjectIdentifier ( user ).getStringValue())%>','<%=StringUtil.checkNull(PersistenceHelper.getObjectIdentifier ( people ).getStringValue())%>','<%=StringUtil.checkNull(PersistenceHelper.getObjectIdentifier ( dept1 ).getStringValue())%>','<%=StringUtil.checkNull(user.getName())%>','<%=StringUtil.checkNull(user.getFullName())%>','<%=StringUtil.checkNull(dept1.getName())%>','<%=StringUtil.checkNull(people.getDuty())%>','<%=StringUtil.checkNull(people.getDutyCode())%>','<%=StringUtil.checkNull(user.getEMail())%>','<%=StringUtil.checkNull(people.getName())%>');",
					"","",
					"/Windchill/netmarkets/jsp/narae/org/images/tree/user.png",
					"/Windchill/netmarkets/jsp/narae/org/images/tree/user.png", true);


<%
		}
		}
%>
<%
ArrayList list = OrgDao.service.getDepartmentTree(0L);

for(int i=0; i< list.size(); i++){
	String[] node = (String[])list.get(i);
	String levels = node[0];
	int level = Integer.parseInt(levels);
	
	String tempId = node[2];
	tempId = tempId.substring ( tempId.lastIndexOf ( ":" ) + 1 );
	int id = Integer.parseInt ( tempId );
	     
	int parentId = 0;
	if(level > 1) {
		String tempoid = node[5];
		tempoid = tempoid.substring ( tempoid.lastIndexOf ( ":" ) + 1 );
		parentId = Integer.parseInt ( tempoid );
	}

	//System.out.println("id = " + id);
	//System.out.println("parentId = " + parentId);
	//System.out.println("node[1] = " + node[1]);
	//System.out.println("node[2] = " + node[2]);
%>
	departmentTree.add(<%=id%>,<%=parentId%>,"<%=node[1]%>",
			"","","",
			"/Windchill/netmarkets/jsp/narae/org/images/tree/group.png",
			"/Windchill/netmarkets/jsp/narae/org/images/tree/group.png", false);
<%}%>
		document.write(departmentTree);
</script>
                    </DIV></td>
                    <td width=7  background="/Windchill/netmarkets/jsp/narae/portal/images/barFrame_bg.gif"></td>
                  </tr>
                </table></TD>
              </TR>
            </table>          </td>
<!-- tab Line   -->
<!--           <td width="5" valign="top"  id=leftLine style="display: none"><span class="a_con_05"></span> -->
          <td width="315" valign="top"  id=leftLine style="display: none"><span class="a_con_05"></span>
            <table width="200" border="0" align="center" cellpadding="0" cellspacing="1" bgcolor=#cfcfcf class=9pt>
            <input type=hidden name=title value="">
              <tr>
                <td height="30"   class="border_text_03-1"> 
                  <table width="228" height="27" border="0" align="center" cellpadding="0" cellspacing="0">
                    <tr> 
                      <td width="76" background="/Windchill/netmarkets/jsp/narae/img/k_bt_01.gif"><table width="60" border="0" align="center" cellpadding="0" cellspacing="0">
                          <tr> 
                            <td width="71"><div align="center"><a href="javascript:leftView('leftSearch');" target="_parent"><strong><font color="#FFFFFF">Search</font></strong></a></div></td>
                          </tr>
                        </table></td>
                      <td width="7">&nbsp;</td>
                      <td width="62" background="/Windchill/netmarkets/jsp/narae/img/k_bt_02.gif"><table width="50" border="0" align="center" cellpadding="0" cellspacing="0">
                          <tr> 
                            <td width="61" height="17"><div align="center"><a href="javascript:leftView('leftTree');" target="_parent"><strong><font color="#FFFFFF">Org</font></strong></a></div></td>
                          </tr>
                        </table></td>
                      <td width="7">&nbsp;</td>
                      <td width="76" background="/Windchill/netmarkets/jsp/narae/img/k_bt_03_ov.gif"><table width="71" border="0" align="center" cellpadding="0" cellspacing="0">
                          <tr> 
                            <td width="71"><div align="center"><a href="javascript:leftView('leftLine');" target="_parent"><strong><font color="#FFFFFF">App.Line</font></strong></a></div></td>
                          </tr>
                        </table></td>
                    </tr>
                  </table></TD>
              </TR>
              <tr>
                <td   class="border_text_03-1"><table width="205" height="49" border="0" align="center" cellpadding="0" cellspacing="0">
                    <tr> 
                      <td width="99" height="24" background="/Windchill/netmarkets/jsp/narae/img/k_btg_04_ov.gif"><table width="70" border="0" align="center" cellpadding="0" cellspacing="0">
                          <tr> 
                            <td><div align="center"><a href="javascript:windEdit_Enter(3);"><strong><font color="#FFFFFF">Save</font></strong></a></div></td>
                          </tr>
                        </table></td>
                      <td width="5"></td>
                      <td width="100" height="24" background="/Windchill/netmarkets/jsp/narae/img/k_btg_04_ov.gif"><table width="70" border="0" align="center" cellpadding="0" cellspacing="0">
                          <tr> 
                            <td><div align="center"><a href="javascript:windEdit_Enter(4);"><font color="#FFFFFF"><strong>Delete</strong></font></a></div></td>
                          </tr>
                        </table></td>
                    </tr>
                    <tr> 
                      <td height="5" colspan="3"></td>
                    </tr>
                    <tr> 
                      <td width="99" height="24" background="/Windchill/netmarkets/jsp/narae/img/k_btg_04_ov.gif"><table width="70" border="0" align="center" cellpadding="0" cellspacing="0">
                          <tr> 
                            <td><div align="center"><a href="javascript:windEdit_Enter(5);"><strong><font color="#FFFFFF">Add</font></strong></a></div></td>
                          </tr>
                        </table></td>
                      <td width="5" height="24"></td>
                      <td width="100" height="24" background="/Windchill/netmarkets/jsp/narae/img/k_btg_04_ov.gif"><table width="95" border="0" align="center" cellpadding="0" cellspacing="0">
                          <tr> 
                            <td><div align="center"><a href="javascript:windEdit_Enter(6);"><strong><font color="#FFFFFF">Information</font></strong></a></div></td>
                          </tr>
                        </table></td>
                    </tr>
                  </table>
                </TD>
              </TR>
              <tr>
                <td   class="border_text_03-1"><span style="border:1px;overflow-x:auto;overflow-y:auto; width:230px;height:382px;">
              				<table width="213" border="0" cellpadding="0" cellspacing="1" bgcolor=#cfcfcf class=9pt>
				                  <tr>
<!-- 				                      <td  class="border_text_03-1"><select name="lineList" id="lineList" size=20 title="" style="width:400px; height:582px;" > -->
				                      <td  class="border_text_03-1"><select name="lineList" id="lineList" size=20 title="" style="width:308px; height:582px;" multiple="multiple">
				                        <div name="divUser" id="divUser" style="width:253; height:382; overflow-y:scroll;overflow-x:scroll; padding:10px; border:0; border-style:solid; border-color:#EBEBEB" value="">
				                        </DIV>
			                          </select></TD>
	                          </tr>
				               
	                 	</table>
              			</span>
              		</div></TD>
              </TR>
          </table></td>
          <td width="5" valign="top">&nbsp;</td>
          <td width="120" valign="top"><table width="120" border="0" align="center" cellpadding="0" cellspacing="0">
              <tr> 
                <td width="140" valign="top"><table width="140"  border="0" cellpadding="0" cellspacing="1" bgcolor=#B5D1D7 class=9pt>
                    <tr> 
                      <td width="157" height="566" valign="top"  class="a_con_05""> <p>&nbsp; </p>
                        <p>&nbsp;</p>
                        <table width="130" border="0" align="center" cellpadding="0" cellspacing="1" bgcolor=#cfcfcf class=9pt>
                          <tr>
                            <td height="23"  class="a_con_01"><%=ORG_1070%></td>
                          </tr>
                          <tr style="display:none">
                            <td height="23"  class="border_text_03-00"><input type="radio" name="appType" value="app1">
                              <%=ORG_35%></td>
                          </tr>
                          <tr>
                            <td height="23"  class="border_text_03-00"><input type="radio" name="appType" value="app2">
                              <%=ORG_38%></td>
                          </tr>
                          <tr>
                            <td height="23" class="border_text_03-00"><input type="radio" name="appType" value="app3">
                              	결재2</td>
                          </tr>
                          <tr>
                            <td height="23"class="border_text_03-00"><input type="radio" name="appType" value="app4">
                              	수신</td>
                          </tr>
                        </table>
                        <br> 
                        <br>
                        <table width="80" border="0" cellspacing="0" cellpadding="0" align="center">
                          <tr>
                            <td height="25" align="center">
								<a style="FONT-SIZE: 8pt;" onclick="javascript:add();"><table class="" border="0" cellpadding="0" cellspacing="0" width="100"><tbody><tr><td width="7"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left.gif" alt="" height="20" width="7"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center"><%=ORG_150%></div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right.gif" alt="" height="20" width="12"></td></tr></tbody></table></a>
                            </td>
                          </tr>
                          <tr>
                            <td height="25" align="center">
								<a style="FONT-SIZE: 8pt;" onclick="javascript:deltype();"><table class="" border="0" cellpadding="0" cellspacing="0" width="100"><tbody><tr><td width="7"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left.gif" alt="" height="20" width="7"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center"><%=ORG_641%></div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right.gif" alt="" height="20" width="12"></td></tr></tbody></table></a>
                            </td>
                          </tr>
                          <tr>
                            <td height="25" align="center">
								<a style="FONT-SIZE: 8pt;" onclick="javascript:delalltype();"><table class="" border="0" cellpadding="0" cellspacing="0" width="100"><tbody><tr><td width="7"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left.gif" alt="" height="20" width="7"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center"><%=ORG_844%></div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right.gif" alt="" height="20" width="12"></td></tr></tbody></table></a>
                            </td>
                          </tr>
                        </table></td>
                    </tr>
                  </table></td>
              </tr>
            </table></td>
          <td width="10">&nbsp;</td>
          <td valign="top">
            <table width="550" border="0" align="center" cellpadding="0" cellspacing="1" bgcolor=#cfcfcf class=9pt>
              <tr>
                <td width="613"  class="border_text_03-00"><table width="550" border="0" cellspacing="0" cellpadding="0">
                  <tr style="display:none">
                    <td>
                    <table width="549" border="0" cellspacing="0" cellpadding="0">
                      <tr>
                        <td width="549" height="100"  valign="top">
                        <table width="550" border="0" align="center" cellpadding="0" cellspacing="1" bgcolor=#cfcfcf class=9pt>
                          <tr>
                            <td width="473" height="20" bgcolor="#D6E9E6" ><div align="center"><strong><%=ORG_35%></strong></div></TD>
                            </TR>
                          </table>  <table width="550" border="0" cellpadding="1" cellspacing="1" bgcolor=#1591B9 align=center>
                            <tr>
                              <td height=1 width=100%></td>
                              </tr>
                            </table>
                          <div id=list style="height:89;" width="550px" style="position:absolute;left:400;top:66; height:20px; overflow-y:scroll; overflow-x:hidden;"> 
                          <table width="530" border="0"   valign="top" align="center" cellpadding="0" cellspacing="1" bgcolor=#cfcfcf class=9pt>
                            <tr  >
                              <td  class="tdblueM"></TD>
                              <td   class="tdblueM"><%=ORG_520%></TD>
                              <td   width="16%"  class="tdblueM"><%=ORG_996%></TD>
                              <td   class="tdblueM"><%=ORG_718%> </TD>
                              <td   class="tdblueM"><%=ORG_400%></TD>
                              <td   class="tdblueM">ID</TD>
                              <td   class="tdblueM"><%=ORG_225%></TD>
                              </TR>
                          <tbody id="app1"></tbody>
                            </table></div></td>
                      </tr>
                      </table></td>
                  </tr>
                  <tr>
                    <td height="14"></td>
                  </tr>
                  <tr>
                    <td>
                      <table width="549" border="0" align="center" cellpadding="0" cellspacing="0">
                        <tr>
                          <td width="549" height="101" valign="top"><table width="550" border="0" align="center" cellpadding="0" cellspacing="0">
                      <tr>
                        <td><table width="550" border="0" align="center" cellpadding="0" cellspacing="1">
                          <tr>
                            <td width="473" height="20" bgcolor="#efefef" ><div align="center"><strong><%=ORG_38%></strong></div></TD>
                            </TR>
                          </table>
                          <table width="550" border="0" cellpadding="1" cellspacing="1" bgcolor=#752e41 align=center>
                            <tr>
                              <td height=1 width=100%></td>
                              </tr>
                            </table></td>
                      </tr>
                      </table>
                      <div id=list style="height:90;" width="550px"  style="position:absolute;left:400;top:80; height:15px; overflow-y:scroll; overflow-x:hidden;">
                      <table width="530" border="0" align="center" cellpadding="0" cellspacing="1">
                        <tr>
                          <td height="20"  class="tdblueM"></TD>
                          <td height="25"  class="tdblueM"><%=ORG_520%></TD>
                          <td height="25"  class="tdblueM"><%=ORG_996%></TD>
                          <td height="25"  class="tdblueM"><%=ORG_718%> </TD>
                          <td height="25"  class="tdblueM"><%=ORG_400%></TD>
                          <td height="25"  class="tdblueM">ID</TD>
                          <td height="25"  class="tdblueM"><%=ORG_225%></TD>
                        </TR>
                            <tbody id="app2"></tbody>
                     </table></div></td>
                        </tr>
                      </table></td>
                  </tr>
                  <tr>
                    <td height="14"></td>
                  </tr>
                  <tr>
                    <td height="14"><table width="550" border="0" align="center" cellpadding="0" cellspacing="0">
                      <tr>
                        <td><table width="550" border="0" align="center" cellpadding="0" cellspacing="1">
                          <tr>
                            <td width="473" height="20" bgcolor="#efefef" ><div align="center"><strong>결재2</strong></div></TD>
                            </TR>
                          </table>
                          <table width="550" border="0" cellpadding="1" cellspacing="1" bgcolor=#752e41 align=center>
                            <tr>
                              <td height=1 width=100%></td>
                              </tr>
                            </table></td>
                      </tr>
                      </table> <div id=list style="height:90;" width="550px" style="position:absolute;left:400;top:195; height:16px; overflow-y:scroll; overflow-x:hidden;">
                      <table width="530" border="0" align="center" cellpadding="0" cellspacing="1">
                        <tr>
                          <td height="25"  class="tdblueM"></TD>
                          <td height="25"  class="tdblueM"><%=ORG_520%></TD>
                          <td height="25"  class="tdblueM"><%=ORG_996%></TD>
                          <td height="25"  class="tdblueM"><%=ORG_718%> </TD>
                          <td height="25"  class="tdblueM"><%=ORG_400%></TD>
                          <td height="25"  class="tdblueM">ID</TD>
                          <td height="25"  class="tdblueM"><%=ORG_225%></TD>
                        </TR>
                          <tbody id="app3"></tbody>
                  
                      </table></div></td>
                  </tr>
                  <tr>
                    <td height="14"></td>
                  </tr>
                  <tr>
                    <td valign="top">
                      <br>
                      <br>
                      <br>
                      <br>
                      <br>
<table width="550" border="0" align="center" cellpadding="0" cellspacing="0">
                        <tr>
                        <td><table width="550" border="0" align="center" cellpadding="0" cellspacing="1">
                          <tr>
                            <td width="473" height="20" bgcolor="#efefef" ><div align="center"><strong>수	신</strong></div></TD>
                            </TR>
                          </table>
                          <table width="550" border="0" cellpadding="1" cellspacing="1" bgcolor=#752e41 align=center>
                            <tr>
                              <td height=1 width=100%></td>
                              </tr>
                            </table></td>
                      </tr>
                      </table><div id=list style="height:90;" width="550px" style="position:absolute;left:400;top:314; height:10px; overflow-y:scroll; overflow-x:hidden;">
                      <table width="530" border="0" align="center" cellpadding="0" cellspacing="1" >
                        <tr>
                          <td height="25"  class="tdblueM"></TD>
                          <td height="25"  class="tdblueM"><%=ORG_520%></TD>
                          <td height="25"  class="tdblueM"><%=ORG_996%></TD>
                          <td height="25"  class="tdblueM"><%=ORG_718%> </TD>
                          <td height="25"  class="tdblueM"><%=ORG_400%></TD>
                          <td height="25"  class="tdblueM">ID</TD>
                          <td height="25"  class="tdblueM"><%=ORG_225%></TD>
                        </TR>
                          <tbody id="app4"></tbody>
                       
                      </table></div>
                      <br>
                      <br>
                      <br>
                      <br>
                      <br>
<br></td>
                  </tr>
                </table></td>
              </tr>
            </table></td>
        </tr>
      </table></td>
  </tr>
  <tr> 
    <td height="40"><table width="945" border="0" align="center" cellpadding="0" cellspacing="1" bgcolor=#cfcfcf class=9pt>
      <tr>
        <td height="20"   class="border_text_03-1"><table width="150" height="30" border="0" align="right" cellpadding="0" cellspacing="0">
          <tr>
            <td><a style="FONT-SIZE: 8pt;" onclick="saveUser()"><table class="" border="0" cellpadding="0" cellspacing="0" width="50"><tbody><tr><td width="7"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left.gif" alt="" height="20" width="7"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center"><%=ORG_890%></div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right.gif" alt="" height="20" width="12"></td></tr></tbody></table></a></td>
            <td><a style="FONT-SIZE: 8pt;" onclick="javascript:self.close();"><table class="" border="0" cellpadding="0" cellspacing="0" width="50"><tbody><tr><td width="7"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left.gif" alt="" height="20" width="7"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center"><%=ORG_141%></div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right.gif" alt="" height="20" width="12"></td></tr></tbody></table></a></td>
          </tr>
        </table></TD>
      </TR>
    </table></td>
  </tr>
</table>
<p>&nbsp;</p>
</body>
</html>

<script>

if(approveType == "Last"){
	loadApproveUserLast();//refreshLineLast();
}else{
	loadApproveUser();
}
refreshLine();
</script>
