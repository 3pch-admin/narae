<%@page import="wt.folder.SubFolder"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>

<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Hashtable"%>
<%@page import="java.util.StringTokenizer"%>
<%@page import="java.util.Vector"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="wt.folder.FolderHelper"%>
<%@page import="wt.folder.Folder"%>

<%@page import="wt.query.ClassAttribute"%>
<%@page import="wt.query.OrderBy"%>
<%@page import="wt.query.QuerySpec"%>
<%@page import="wt.query.SearchCondition"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="wt.pom.WTConnection"%>
<%@page import="wt.pom.DBProperties"%>
<%@page import="wt.method.MethodContext"%>
<%@page import="java.rmi.RemoteException"%>

<%@page import="java.sql.*"%>
<%@page import="wt.query.KeywordExpression"%>

<%
	// Last Maker Code Number
	
	//
%>

<title>Maker정보 등록</title>
<base target="_self"/>


<link rel="stylesheet" href="/Windchill/netmarkets/jsp/narae/css/e3ps.css" type="text/css">
<link rel="stylesheet" href="/Windchill/netmarkets/jsp/narae/css/css.css" type="text/css">
<link rel="StyleSheet" href="/Windchill/netmarkets/jsp/narae/css/dtree.css" type="text/css" />

<script type="text/javascript" src="/Windchill/netmarkets/jsp/narae/js/common.js"></script>
<script type="text/javascript" src="/Windchill/netmarkets/jsp/narae/js/dtree.js"></script>
<script type="text/javascript" src="/Windchill/netmarkets/jsp/narae/js/ajax.js"></script>
            <script type="text/javascript">
			
            <!--

        	function doSubmit() {
        		var form = document.registMakerForm;
        		form.method = "post";
        		form.action = "/Windchill/netmarkets/jsp/narae/part/registMakerAction.jsp";
        		form.submit();
        	}

        	function clearText(str) {
        		var tartxt = document.getElementById(str);
        		tartxt.value = "";
        	}

        	function checkName() {
        		var txtbox = document.getElementsByName("mkrName")[0];
        		if (event.keyCode == 32) {
        			alert("공백은 입력하실 수 없습니다.");
        			txtbox.value = txtbox.value;
        			txtbox.value = txtbox.value.substring(0, txtbox.value.length - 1);
        		} else if (event.keyCode == 42 || event.keyCode == 56 || event.keyCode == 106) {
        			alert("별표(*)는 시스템 예약 문자이므로 입력하실 수 없습니다.");
        			event.returnValue = false;
        			txtbox.value = txtbox.value.substring(0, txtbox.value.length - 1);
        		}
        	}

        	function checkDescription() {
        		var form = document.registMakerForm;
        		form.description.value = form.description.value.toUpperCase();
        		if (event.keyCode == 32) {
        			alert("공백은 입력하실 수 없습니다.");
        			event.returnValue = false;
        		} else if (event.keyCode == 42) {
        			alert("별표(*)는 시스템 예약 문자이므로 입력하실 수 없습니다.");
        			event.returnValue = false;
        		}
        		return false;
        	}

        	function registMaker() {
        		var form = document.registMakerForm;

        		if (form.mkrName.value.length <= 0) {
        			alert("Maker명을 입력해 주세요.");
        			form.mkrname.focus();
        			return;
        		}

        		var con = confirm("Maker를 등록하시겠습니까?");
        		if (con == true) {
        			doSubmit();
        			//window.close();
        		} else {
        			return false;
        		}
        	}

        	function setTextColor(objname, color) {
        		var obj = document.getElementById(objname);
        		obj.style.color = color;
        	}

        	function setButtonTag3D(_name, _width, _script, _class) {
        		var sb = "";
        		var rwidth = _name.length * 8;
        		if (rwidth > _width) _width = rwidth;
        		var id = '_text' + Math.floor(Math.random() * 10) + 1;
        		sb = "<a onclick=\"" + _script + "\" style='cursor:hand;' onMouseOver=\"setTextColor('" + id + "','#0393c8');\" onMouseOut=\"setTextColor('" + id + "','#4E4E4E');\"><table width='" + _width + "' border='0' cellspacing='0' cellpadding='0' class='" + _class + "'>";
        		sb += "<tr>";
        		sb += "<td width='8'><img src='/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif' alt='' width='8' height='22'></td>";
        		sb += "<td valign='middle' background='/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif'>";
        		sb += "<table align='center' border='0' cellspacing='0' cellpadding='0'>";
        		sb += "<tr>";
        		sb += "<td><div id='" + id + "' align='center'>" + _name + "</div></td>";
        		sb += "</tr>";
        		sb += "</table>";
        		sb += "</td>";
        		sb += "<td width='12'><img src='/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif' alt='' width='12' height='22'></td>";
        		sb += "</tr>";
        		sb += "</table></a>";
        		document.write(sb);
        	}
        	// #######################  END


                
            //-->
            </script>
<%@include file="/netmarkets/jsp/narae/portal/ajax/SearchListAjax.html"%>
<table border="0" width="100%" height="100%" cellpadding="10" cellspacing="3">
    <tr>                
        <td valign="top" width="100%">
            <form name="registMakerForm" method="post" action="" >
                <body onLoad="javascript:document.registMakerForm.mkrName.focus();">
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
										              <td height=30 width=93% align=center><B><font color=white>Maker정보 등록</td>
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
                                                <td class="tdblueM" width="150"><span style="color:red;">Maker Code</span></td>
                                                <td class="tdwhiteL">
													<input name="mkrCode" readonly style="color: gray; height: 25px; font-size: 15px; padding: 5px 2px 0 2px; padding-top: 2px; padding-bottom: 2px;" class="txt_field" size="34" value="자동입력값입니다." />
                                                    <!-- input name="code" class="txt_field" size="65" onkeypress="checkName()" onblur="checkName()"/ -->
                                                </td>                                            
                                            </tr>    
											<tr>
                                                <td class="tdblueM" width="150">Maker Name</td>
                                                <td class="tdwhiteL">
                                                    <input name="mkrName" style="height:25;font-size:15px;padding:5px 2px 0 2px;padding-top:2px;padding-bottom:2px;" class="txt_field" size="34"  onkeyup="checkName()" onblur="checkName()" />
                                                </td>                                            
                                            </tr>  
											<!-- tr>
                                                <td class="tdblueM" width="150"><span style="color:red;">Maker Name (en)</span></td>
                                                <td class="tdwhiteL">
                                                    <input name="description" class="txt_field" size="65" onkeypress="checkDescription()" onblur="checkDescription()" onpaste="javascript:return false;"/>
                                                </td>                                            
                                            </tr>
											<tr>
                                                <td class="tdblueM" width="150">활성화</td>
                                                <td class="tdwhiteL">                                                
													<input type="radio" name="disabled" value="1" checked="on">TRUE
													<input type="radio" name="disabled" disabled value="0">FALSE
                                                </td>                                            
                                            </tr -->
                                        </table>
                                    </td>
                                </tr>  
								<tr>
									<td height=10 width=100%></td>
                                </tr>
								<tr>
                                    <td align="center">
                                        <table>
                                            <tr>
                                                <td><script>setButtonTag3D("등록","60","javascript:registMaker();","");</script></td>
												<td><script>setButtonTag3D("취소","60","javascript:self.close();","");</script></td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>
                            </table>                          
                        </td>
                    </tr>
                </table>
				</body>
            </form>
        </td>
    </tr>
</table>

<!-- jsp:include page="/Windchill/netmarkets/jsp/narae/portal/bottom.jsp" flush="true"/ -->
<jsp:include page="../portal/bottom.jsp" flush="true"/>
