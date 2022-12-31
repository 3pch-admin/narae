<%@page import="ext.narae.service.erp.beans.ERPSearchHelper"%>
<%@page import="ext.narae.util.StringUtil"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@page import="java.util.HashMap"%>
<%@page import="java.sql.ResultSet"%>

<jsp:useBean id="wtcontext" class="wt.httpgw.WTContextBean" scope="session" />
<jsp:setProperty name="wtcontext" property="request" value="<%=request%>" />

<%
	String codetype = StringUtil.checkNull(request.getParameter("codetype"));
    String command = StringUtil.checkNull(request.getParameter("command"));
    String makerCode =StringUtil.checkNull(request.getParameter("makerCode"));
    String makerName =StringUtil.checkNull(request.getParameter("makerName"));
    
    ResultSet rs = null;
    
    if(command.equals("SEARCH")){
    	HashMap map = new HashMap();
    	map.put("makerCode",makerCode);
    	map.put("makerName",makerName);
    	rs = ERPSearchHelper.manager.getErpMaker(map);
    	System.out.println("rs = " + rs);
    }
%>

<link rel="stylesheet" type="text/css" href="/Windchill/netmarkets/jsp/narae/css/e3ps.css" />
<script type="text/javascript">
var isCh = navigator.appVersion.indexOf("Chrome") >= 0;
<!--
	function setButtonTag3D(_name, _width, _script, _class) {
		var sb = "";
		var rwidth = _name.length * 8;
		if (rwidth > _width) _width = rwidth;
		
		sb = "<a onclick=\"" + _script + "\" style='cursor:hand;' onMouseOver=\"all._text.style.color='#0393c8'\" onMouseOut=\"all._text.style.color='#4E4E4E'\"><table width='"+_width+"' border='0' cellspacing='0' cellpadding='0' class='"+_class+"'>";
		sb += "<tr>";
		sb += "<td width='8'><img src='/Windchill/netmarkets/jsp/narae/portal/img/btn_left_01.gif' alt='' width='8' height='22'></td>";
		sb += "<td valign='middle' background='/Windchill/netmarkets/jsp/narae/portal/img/btn_mid_01.gif'>";
		sb += "<table align='center' border='0' cellspacing='0' cellpadding='0'>";
		sb += "<tr>";
		sb += "<td><div id='_text' align='center'>" + _name + "</div></td>";
		sb += "</tr>";
		sb += "</table>";
		sb += "</td>";
		sb += "<td width='12'><img src='/Windchill/netmarkets/jsp/narae/portal/img/btn_right_01.gif' alt='' width='12' height='22'></td>";
		sb += "</tr>";
		sb += "</table></a>";
		document.write(sb);
	}

    function selectNode(arg){
	
		form = document.forms[0];
		if(form.code) {
			var chkLen = form.code.length;
			if(chkLen) {
				for(var i = 0; i < chkLen; i++) {
					
					if(form.code[i] != arg){
						form.code[i].checked = false;
					}					
					
				}
			}else{
				if(form.code != arg){
					form.code.checked = false;
				}
			}
		}
	
    }
    
	function selectCode(){
		 var agent = navigator.userAgent.toLowerCase();
   	form = document.forms[0];
   	var arr = new Array();
		if(form.code) {
			var chkLen = form.code.length;
			if(chkLen) {
				for(var i = 0; i < chkLen; i++) {
					var subarr = new Array();
					
					if(form.code[i].checked){
						var obj = form.code[i];
						var key = obj.getAttribute( 'codeKey' );
						var codeName = obj.getAttribute( 'codeName' );
						console.log(key);
							codekey = key;//codekey
							codename = codeName;//codename
					}					
				}
			}else{
				if(form.code.checked){
					var obj = form.code;
					var key = obj.getAttribute( 'codeKey' );
					var codeName = obj.getAttribute( 'codeName' );
						codekey = key;//codekey
						codename = codeName;//codename
				}
			}
		 try{
		        if(parent.window.opener != null && !parent.window.opener.closed)
		        {
		        	parent.window.opener.codeSelect('<%=codetype%>','',codekey,codename);
				    	self.close();    
		        }
		    }catch(e){ }
		}
//    	if (agent.indexOf("trident") != -1) {
//    		console.log("익스플로러");
// 			if(form.code) {
// 				var chkLen = form.code.length;
// 				if(chkLen) {
// 					for(var i = 0; i < chkLen; i++) {
// 						var subarr = new Array();
// 						if(form.code[i].checked){
// 								codekey = form.code[i].codeKey;//codekey
// 								codename = form.code[i].codeName;//codename
// 						}					

// 					}
// 				}else{
// 					if(form.code.checked){
// 						codekey = form.code.codeKey;
// 						codename = form.code.codeName;
// 					}
// 				}
				
<%-- 				parent.window.opener.codeSelect('<%=codetype%>','',codekey,codename);	 --%>
// 				 self.close();     
// 			}
//    	}
				
   }
	
	function searchCode(){
		pForm = document.codeForm;
		
		pForm.submit();
	}
	
//-->
</script>

<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
<form name="codeForm" method="post" >
    <input type=hidden name=codetype value="<%=codetype%>">
    <input type=hidden name=command value="SEARCH">
   
	
    <table width="100%" border="0" cellpadding="0" cellspacing="10" > <!--//여백 테이블-->
        <tr align=center height=5>
            <td>
                <table width="100%" border="0" cellpadding="10" cellspacing="3" >
                    <tr>
						<table width="100%" border="0" cellpadding="1" cellspacing="1" bgcolor=8f2436 align=center style="padding-bottom:10px">
							<tr> 
								<td height=30 width=93% align=center><B><font color=white>[Maker]</td>
							</tr>
						</table>
                    </tr>
                    <tr align=center>
						<td>
							<table  border="0" cellpadding="0" cellspacing="1" align=right>
				        		<tr>
									<td align=right>
										이름 : <input type=text name=makerName value="<%=makerName %>" size=20>
			                    		코드 : <input type=text name=makerCode value="<%=makerCode %>" size=10>
			                    	</td>
                                    <td align=right><script>setButtonTag3D("검색","60","javascript:searchCode();","menu A:link");</script></td>
                                    <td align=right><script>setButtonTag3D("선택","60","javascript:selectCode();","menu A:link");</script></td>
                                    <td align="right"><script>setButtonTag3D("닫기","60","javascript:self.close();","menu A:link");</script></td>
								</tr>
							</table>
						</td>
					</tr>
                    <tr  align=center>
                        <td valign="top" style="padding:0px 0px 0px 0px">
                            <table width="100%" border="0" cellpadding="1" cellspacing="1" bgcolor=#752e41 align=center >
								<tr><td height=1 width=100%></td></tr>
						    </table>
						    <table width="100%" border="0" cellpadding="5" cellspacing="1" align=center>
                                <tr bgcolor="ffffff" align=center>
                                	<td class="tdblueM">선택</td>
                                    <td class="tdblueM">MakerName</td>
                                    <td class="tdblueM">MakerCode</td>
                                   </tr>
                                   
<%
   		if(rs!= null){
	    	while(rs.next()){
			
				String mCode = (String)rs.getObject("Maker");
				String mName = (String)rs.getObject("MkrName");
				System.out.println("mCode = " + mCode);
				System.out.println("mName = " + mName);
%>
	                                <tr bgcolor="ffffff" >
	                                	<td class="tdwhiteM"><input type="checkbox" name="code" id="code" value='' 
	                                	codeKey="<%=mCode%>";
	                                	codeName="<%=mName%>";	onclick="selectNode(this)"></td>
	                                	
	                                    <td class="tdwhiteL"><%=mName%></td>
	                                    <td class="tdwhiteL"><%=mCode%></td>
	                                </tr>
<%
	    	}
		}
%>
                            </table>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</form>
