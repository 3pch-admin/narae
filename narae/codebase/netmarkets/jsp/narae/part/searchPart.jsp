<%@page import="ext.narae.service.folder.beans.CommonFolderHelper"%>
<%@page import="wt.folder.SubFolder"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>

<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="wt.folder.FolderHelper"%>
<%@page import="wt.folder.Folder"%>

<%@ page import="wt.util.*, wt.org.*, wt.session.*, wt.inf.container.*, ext.narae.util.*, ext.narae.ui.*, java.util.*" %>
<%
String RESOURCE = "ext.narae.ui.common.resource.NaraeCommonRB";
String UNITRB = "ext.narae.ui.common.resource.NareUnitRB";
Locale locale = WTContext.getContext().getLocale();
WTContainerRef containerRef = WCUtil.getWTContainerRefForDrawing();
WTContainerRef partContainerRef = WCUtil.getWTContainerRefForPart();
String ORG_1151 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_1151", new Object[]{}, locale);
String ORG_48 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_48", new Object[]{}, locale);
String ORG_172 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_172", new Object[]{}, locale);
String ORG_576 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_576", new Object[]{}, locale);
String ORG_936 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_936", new Object[]{}, locale);
String ORG_1098 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_1098", new Object[]{}, locale);
/*
out.println("ORG_1151=" + ORG_1151);
out.println("ORG_48=" + ORG_48);
out.println("ORG_172=" + ORG_172);
out.println("ORG_576=" + ORG_576);
out.println("ORG_936=" + ORG_936);
*/
%>

<title>하위품목 검색</title>
<base target="_self"/>

<%
    String module = request.getParameter("module");
    String menu = request.getParameter("menu");
    String foid = request.getParameter("fid");
    String folderoid = request.getParameter("folder");
    String oid = request.getParameter("oid");
    String searchCheck = request.getParameter("searchCheck");
    
	
    String moudleType = StringUtil.checkReplaceStr(request.getParameter("moudleType"), ""); //ecr //eco //epm //link 
    String mode = StringUtil.checkReplaceStr(request.getParameter("mode"), "mutil"); 		//single,mutil
    
    
    String location = "/Default";
    String name = "";
    String bgcolor = "white";

    Folder cadd = FolderHelper.service.getFolder("Default",WCUtil.getWTContainerRefForPart());
    ArrayList flist = CommonFolderHelper.getFolderTree(cadd);
    String coid = cadd.getPersistInfo().getObjectIdentifier().toString();
    if(folderoid==null)folderoid = coid;

    ReferenceFactory rf = new ReferenceFactory();
    Folder folder = null;

    if(foid!=null && foid.length()>0){
        folder = (Folder)rf.getReference(foid).getObject();
        location = FolderHelper.getFolderPath( folder);
        name = folder.getName();
    }else{
        foid= CommonUtil.getOIDString(cadd);
    }

    //search attribute
    String number = StringUtil.checkNull(request.getParameter("number"));
    String nameValue = StringUtil.checkNull(request.getParameter("name"));
    String creator = StringUtil.checkNull(request.getParameter("creator"));
    String tempcreator = StringUtil.checkNull(request.getParameter("tempcreator"));
    String description = StringUtil.checkNull(request.getParameter("description"));
    String predate = StringUtil.checkNull(request.getParameter("predate"));
    String postdate = StringUtil.checkNull(request.getParameter("postdate"));
    String islastversion = StringUtil.checkReplaceStr(request.getParameter("islastversion"), "true");
    String productName = StringUtil.checkNull(request.getParameter("productName"));
    String product = StringUtil.checkNull(request.getParameter("product"));
    String maxcnt = StringUtil.checkReplaceStr(request.getParameter("maxcnt"), "0");
    String plmOnly = StringUtil.checkReplaceStr(request.getParameter("plmOnly"), "false");
  
%>


<link rel="stylesheet" href="/Windchill/netmarkets/jsp/narae/css/e3ps.css" type="text/css">
<link rel="stylesheet" href="/Windchill/netmarkets/jsp/narae/css/css.css" type="text/css">
<link rel="StyleSheet" href="/Windchill/netmarkets/jsp/narae/css/dtree.css" type="text/css" />

<script type="text/javascript" src="/Windchill/netmarkets/jsp/narae/js/common.js"></script>
<script type="text/javascript" src="/Windchill/netmarkets/jsp/narae/js/dtree.js"></script>
<script type="text/javascript">
<!--
    function initSwitch(){
        var smenu = document.getElementById("subMenu");
        var vv = getCookie("subMenuSwitch2");
        if(vv==null)return;
        smenu.style.display = getCookie("subMenuSwitch2");
    }

    function switchMenu(){
        var smenu = document.getElementById("subMenu");
        if('none' == smenu.style.display){
            smenu.style.display="block";
        }else{
            smenu.style.display="none";
        }
        setCookie("subMenuSwitch2",smenu.style.display,null,'/Windchill/netmarkets/jsp/narae/<%=module%>/');
    }

    function getCookie( name ) {
        var start = document.cookie.indexOf( name + "=" );
        var len = start + name.length + 1;
        if ( ( !start ) && ( name != document.cookie.substring( 0, name.length ) ) ) {
            return null;
        }
        if ( start == -1 ) return null;
        var end = document.cookie.indexOf( ";", len );
        if ( end == -1 ) end = document.cookie.length;
        return unescape( document.cookie.substring( len, end ) );
    }

    function setCookie( name, value, expires, path, domain, secure ) {
        var today = new Date();
        today.setTime( today.getTime() );
        if ( expires ) {
            expires = expires * 1000 * 60 * 60 * 24;
        }
        var expires_date = new Date( today.getTime() + (expires) );
        document.cookie = name+"="+escape( value ) +
            ( ( expires ) ? ";expires="+expires_date.toGMTString() : "" ) + //expires.toGMTString()
            ( ( path ) ? ";path=" + path : "" ) +
            ( ( domain ) ? ";domain=" + domain : "" ) +
            ( ( secure ) ? ";secure" : "" );
    }

    function deleteCookie( name, path, domain ) {
        if ( getCookie( name ) ) document.cookie = name + "=" +
                ( ( path ) ? ";path=" + path : "") +
                ( ( domain ) ? ";domain=" + domain : "" ) +
                ";expires=Thu, 01-Jan-1970 00:00:01 GMT";
    }

    function setLocation(foid){
        document.menuForm.fid.value = foid;
        document.menuForm.submit();
    }

    function gotoMenu(a,m){
        document.menuForm.action = a;
        document.menuForm.menu.value = m;
        document.menuForm.submit();
    }
//-->
</script>

<table border="0" width="100%" height="100%" cellpadding="10" cellspacing="3">
    <tr>                
        <td valign="top" width="100%">        
            <script type="text/javascript">initSwitch();</script>
            <script type="text/javascript" src="/Windchill/netmarkets/jsp/narae/js/ajax.js"></script>
            <script type="text/javascript">
            <!--
                function doSubmit() {	
					form = document.selectPartForm;

					if(form.number.value ==''){
						alert("검색할 TOP품목을 입력해 주세요.");
						return;
					}
                
                    form.method = "post";
                    form.action= "/Windchill/netmarkets/jsp/narae/part/searchPartList.jsp";
                    form.target = "list";
                    form.submit();
                }

                function openCal(variableName)
                {
                    var str="/Windchill/netmarkets/jsp/narae/common/calendar.jsp?form=selectPartForm&obj="+variableName;
                    var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=0,resizable=0,";
                    leftpos = (screen.width - 224) / 2;
                    toppos = (screen.height - 230) / 2;
                    rest = "width=224,height=230,left=" + leftpos + ',top=' + toppos;

                    var newwin = window.open( str , "calendar", opts+rest);
                    newwin.focus();
                }

                function clearText(str) {
                        var tartxt = document.getElementById(str);
                        tartxt.value = "";
                }

                //select user
                function selectUser(inputObj, inputLabel) {

//                     var url = "/Windchill/netmarkets/jsp/narae/org/SelectPeopleFrm.jsp?mode=s";

//                     attache = window.showModalDialog(url,window,"help=no; scroll=no; resizable=yes; dialogWidth=750px; dialogHeight:450px; center:yes");
//                     if(typeof attache == "undefined" || attache == null) {
//                         return;
//                     }

                    var url = "/Windchill/netmarkets/jsp/narae/org/SelectPeopleFrm.jsp?mode=s";
                	var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=no,";
                    var popWidth = 1000;
                    var popHeight = 600;
                    var leftpos = (screen.width - popWidth)/ 2;
                    var toppos = (screen.height - popHeight) / 2 ;
                    var rest = "width="+popWidth+",height="+popHeight+",left=" + leftpos + ',top=' + toppos;
                    
                    var newwin = window.open(url, "selectUser", opts+rest);
                    newwin.focus();

                    addList(newwin, inputObj, inputLabel);
                }

                function addList(arrObj, inputObj, inputLabel) {
                    if(arrObj.length == 0) {
                        return;
                    }

                    var peopleOid;//
                    var userName;//

                    for(var i = 0; i < arrObj.length; i++) {
                        subarr = arrObj[i];
                        peopleOid = subarr[1];//
                        userName = subarr[4];//

                        inputObj.value = peopleOid;
                        inputLabel.value = userName;
                    }
                }

                //select product
                function openSelectProduct(){
                    var str="/Windchill/netmarkets/jsp/narae/product/selectProduct.jsp?returnFunction=setProduct";
                    var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
                    leftpos = (screen.width - 260)/ 2;
                    toppos = (screen.height - 400) / 2 ;
                    rest = "width=260,height=400,left=" + leftpos + ',top=' + toppos;

                    var newwin = window.open( str , "selectProduct", opts+rest);
                    newwin.focus();
                }
                function setProduct(oid,name,lvl){
                    document.selectPartForm.product.value=oid;
                    document.selectPartForm.productName.value=name;
                }
                function deleteProduct(){
                    document.selectPartForm.product.value='';
                    document.selectPartForm.productName.value='';
                }
                // #######################    선택된 LINK 객체들의 값을 부모의 ModalDialog으로 값을 리턴
                function onSelect() {
                    form = document.selectPartForm;
                    // target 값을 체크
                    var arr = list.checkList();
                    
                    var maxcnt = "<%=maxcnt%>";
                    if(arr.length == 0) {
                        alert("<%=ORG_1151%>.");
                        return;
                    }
                    if(maxcnt == 1 && arr.length > 1) {
                        alert("<%=ORG_48%>.");
                        return;
                    }
                    selectModalDialog(arr);
                }

                function selectModalDialog(arrObj) {
                	
//                 	window.returnValue = arrObj;
					opener.document.getElementById("title").value = arrObj;
					opener.addPart(arrObj);
                    window.close();
                }
                // #######################  END

            //-->
            </script>
            <%@include file="/netmarkets/jsp/narae/portal/ajax/SearchListAjax.html"%>

<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
            <form name="selectPartForm" method="post" action="" >
                <input type="hidden" name="oid"         value="<%=oid%>" />
                <input type="hidden" name="doid"        value="" />
                <input type="hidden" name="menu"        value="<%=menu%>" />
                <input type="hidden" name="module"      value="Design" />
                <input type="hidden" name="fid"         value="<%=foid%>" />
                <input type="hidden" name="searchCheck" value="listView" />
                <input type="hidden" name="plmOnly"     value="<%=plmOnly%>" />
                <input type="hidden" name="moudleType"      value="<%=moudleType%>" />
                <input type="hidden" name="mode"      value="<%=mode%>" />
                
                <table width="100%" border="0" cellpadding="0" cellspacing="0" > <!--//여백 테이블-->
                    <tr align="center" height="5">
                        <td>
                            <table width="100%" border="0" cellpadding="0" cellspacing="0">
                                <tr align="center">
                                    <td valign="top" style="padding:0px 0px 0px 0px">
                                        <table width="100%" border="0" cellpadding="1" cellspacing="1" bgcolor=#752e41 align="center">
                                        	<tr>
												 <table width="100%" border="0" cellpadding="1" cellspacing="1" bgcolor=8f2436 align=center style="padding-bottom:10px">
										              <tr> 
										              <td height=30 width=93% align=center><B><font color=white>하위품목 검색</td>
										              </tr>
										         </table>
											</tr>
                                            <tr>
                                                <td height=10 width=100%></td>
                                            </tr>
                                        </table>
						                <table width="100%" border="0" cellpadding="1" cellspacing="1" bgcolor=#752e41 align="center">
						                    <tr>
						                        <td height=1 width=100%></td>
						                    </tr>
						                </table>                                        
                                        <table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" style="border-left-width: 1px;border-left-style: solid;border-left-color: #e6e6e6;">
                                            <tr>
                                                <td class="tdblueM" width="100">품목번호</td>
                                                <td class="tdwhiteL">
                                                    <input name="number" class="txt_field" size="40"  value="<%=number%>" onblur="this.value=this.value.toUpperCase();return false;"/>
                                                </td>
                                                <td class="tdblueM" width="100">품목명</td>
                                                <td class="tdwhiteL">
                                                    <input name="name" class="txt_field" size="40" value="<%=nameValue%>" disabled="disabled"/>
                                                </td>                                                
                                            </tr>                                                                                        
                                        </table>
                                    </td>
                                </tr>
                                <tr>
                                    <td align="right">
                                        <table>
                                            <tr>
                                                <td>
													<a style="FONT-SIZE: 8pt;" onclick="javascript:doSubmit();"><table class="" border="0" cellpadding="0" cellspacing="0" width="50"><tbody><tr><td width="7"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left.gif" alt="" height="20" width="7"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center"><%=ORG_1098%></div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right.gif" alt="" height="20" width="12"></td></tr></tbody></table></a>
												</td>
                                                <td>
													<a style="FONT-SIZE: 8pt;" onclick="javascript:document.selectPartForm.reset();"><table class="" border="0" cellpadding="0" cellspacing="0" width="70"><tbody><tr><td width="7"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left.gif" alt="" height="20" width="7"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center"><%=ORG_172%></div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right.gif" alt="" height="20" width="12"></td></tr></tbody></table></a>
												</td>
                                                <td>
													<a style="FONT-SIZE: 8pt;" onclick="javascript:onSelect();"><table class="" border="0" cellpadding="0" cellspacing="0" width="50"><tbody><tr><td width="7"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left.gif" alt="" height="20" width="7"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center"><%=ORG_576%></div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right.gif" alt="" height="20" width="12"></td></tr></tbody></table></a>
												</td>
                                                <td>
													<a style="FONT-SIZE: 8pt;" onclick="javascript:self.close();"><table class="" border="0" cellpadding="0" cellspacing="0" width="50"><tbody><tr><td width="7"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left.gif" alt="" height="20" width="7"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center"><%=ORG_936%></div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right.gif" alt="" height="20" width="12"></td></tr></tbody></table></a>
												</td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>
				                <tr bgcolor="#ffffff">
				                    <td align="center" valign="top">
				                        <iframe src="/Windchill/netmarkets/jsp/narae/part/searchPartList.jsp?fid=<%=foid%>&islastversion=true&moudleType=<%=moudleType%>&mode=<%=mode%>&number=<%=number %>"
				                                id="list" name="list" frameborder="0" width="100%" height="500" scrolling="yes">
				                        </iframe>
				                    </td>
				                </tr>                                
                            </table>                          
                        </td>
                    </tr>
                </table>
            </form>

        </td>
    </tr>
</table>

