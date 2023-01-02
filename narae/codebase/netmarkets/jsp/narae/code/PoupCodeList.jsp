<%@page import="ext.narae.util.CommonUtil"%>
<%@page import="ext.narae.util.code.NumberCode"%>
<%@page import="ext.narae.util.query.NaraePageQueryBroker"%>
<%@page import="ext.narae.util.code.NumberCodeType"%>
<%@page import="ext.narae.util.StringUtil"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>

<%@page import="wt.fc.QueryResult,
						wt.query.StringSearch,
						wt.query.ClassAttribute,
						wt.query.OrderBy,
						wt.query.QuerySpec,
						wt.query.SearchCondition"%>

<jsp:useBean id="wtcontext" class="wt.httpgw.WTContextBean" scope="session" />
<jsp:setProperty name="wtcontext" property="request" value="<%=request%>" />

<%
    String command = StringUtil.checkNull(request.getParameter("command"));
    String codetype = request.getParameter("codetype");
    
    String codeName = StringUtil.checkNull(request.getParameter("codeName"));
    String specCode = StringUtil.checkNull(request.getParameter("specCode"));
    String detailCodeName = StringUtil.checkNull(request.getParameter("detailCodeName"));
    
    String codeNameHidden = StringUtil.checkNull(request.getParameter("codeNameHidden"));
    String specCodeHidden = StringUtil.checkNull(request.getParameter("specCodeHidden"));
    String detailCodeNameHidden = StringUtil.checkNull(request.getParameter("detailCodeNameHidden"));
    
    NumberCodeType ctype = NumberCodeType.toNumberCodeType(codetype);
	
    QueryResult qr = null;
    NaraePageQueryBroker broker;
    
	QuerySpec query = new QuerySpec();
	int idx = query.addClassList(NumberCode.class, true);
		
	query.appendWhere(new SearchCondition(NumberCode.class, "codeType", "=", codetype), new int[] { 0 });

	if(codeName != null && codeName.trim().length() > 0) {		//규격으로 검색
		if(query.getConditionCount() > 0)
			query.appendAnd();
//	    StringSearch stringsearch = new StringSearch("name");
//		stringsearch.setValue("%"+codeName.trim()+"%");		
//		query.appendWhere(stringsearch.getSearchCondition(NumberCode.class),new int[]{0});
       	query.appendWhere(new SearchCondition(NumberCode.class, "name", "LIKE", "%" + codeName.trim() + "%"), new int[] { 0 });
	} else codeName="";
	
	if(specCode != null && specCode.trim().length() > 0) {		//코드로 검색
		if(query.getConditionCount() > 0)
			query.appendAnd();
	    StringSearch stringsearch = new StringSearch("code");
		stringsearch.setValue("%"+specCode.trim()+"%");		
		query.appendWhere(stringsearch.getSearchCondition(NumberCode.class),new int[]{0});
	} else specCode="";
	
	if(detailCodeName != null && detailCodeName.trim().length() > 0) {		//품명으로 검색
		if(query.getConditionCount() > 0)
			query.appendAnd();
	    StringSearch stringsearch = new StringSearch("description");
		stringsearch.setValue("%"+detailCodeName.trim()+"%");		
		query.appendWhere(stringsearch.getSearchCondition(NumberCode.class),new int[]{0});
	} else detailCodeName="";
	
	query.appendOrderBy(new OrderBy(new ClassAttribute(NumberCode.class,NumberCode.CODE),false),new int[]{0});
	     
   	broker = new NaraePageQueryBroker(request,query);
   	if(codeName.length() > 0 && codeNameHidden.length() == 0) {
   		broker.setSessionid(0);
   	}
   	if(specCode.length() > 0 && specCodeHidden.length() == 0) {
   		broker.setSessionid(0);
   	}
   	if(detailCodeName.length() > 0 && detailCodeNameHidden.length() == 0) {
   		broker.setSessionid(0);
   	}
   	
	qr = broker.search();
	System.out.println(query.toString());
%>

<link rel="stylesheet" type="text/css" href="/Windchill/netmarkets/jsp/narae/css/e3ps.css" />
<script type="text/javascript">
<!--
    function selectNode(arg) {
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
    
	function selectCode() {
    	form = document.forms[0];
    	var arr = new Array();
    	
		if(form.code) {
			var chkLen = form.code.length;
			if(chkLen) {
				for(var i = 0; i < chkLen; i++) {
					var subarr = new Array();
					if(form.code[i].checked){
						oid = form.code[i].value.split('|')[0]; //oid
						codekey = form.code[i].value.split('|')[1];//codekey
						codename = form.code[i].value.split('|')[2];//codename
					}					
				}
			}else{
				if(form.code.checked){
					oid = form.code.value.split('|')[0]; //oid
					codekey = form.code.value.split('|')[1];//codekey
					codename = form.code.value.split('|')[2];//codename
				}
			}
			
			opener.codeSelect('<%=codetype%>',oid,codekey,codename);
			window.self.close();
		}
		
    }
	
	function searchCode() {
		var form = document.codeForm;
		form.submit();	
	}

	function setButtonTag3D(_name, _width, _script, _class)
	{
		var sb = "";
		var rwidth = _name.length * 8;
		if (rwidth > _width) _width = rwidth;
		
		sb = "<a onclick=\"" + _script + "\" style='cursor:hand;'\"><table width='"+_width+"' border='0' cellspacing='0' cellpadding='0' class='"+_class+"'>";
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
//-->
</script>

<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
<form name=codeForm method=post >
    <input type=hidden name=codetype value="<%=codetype%>">
    <input type=hidden name=command value="search">
    
    <input type=hidden name=codeNameHidden value="<%=codeName%>">
    <input type=hidden name=specCodeHidden value="<%=specCode%>">
    <input type=hidden name=detailCodeNameHidden value="<%=detailCodeName%>">
	
    <table width="100%" border="0" cellpadding="0" cellspacing="10" > <!--//여백 테이블-->
        <tr align=center height=5>
            <td>
                <table width="100%" border="0" cellpadding="10" cellspacing="3" >
                    <tr>
						<table width="100%" border="0" cellpadding="1" cellspacing="1" bgcolor=8f2436 align=center style="padding-bottom:10px">
							<tr> 
								<td height=30 width=93% align=center><B><font color=white>[<%=ctype.getDisplay(request.getLocale())%>]</td>
							</tr>
						</table>
                    </tr>
                    <tr align=center>
						<td>
							<table  border="0" cellpadding="0" cellspacing="1" align=right>
				        		<tr>
				        			<%if("SPEC".equals(codetype)) {%>
									<td align=right>규격 : <input type="text" size="15" name="codeName" value="<%=codeName%>"></td>
									<%} else {%>
									<td align=right>재질명 : <input type="text" size="15" name="codeName" value="<%=codeName%>"></td>
									<%} %>
									<td align=right>코드 : <input type="text" size="15" name="specCode" value="<%=specCode%>"></td>
									<%if("SPEC".equals(codetype)) {%>
									<td align=right>품명 : <input type="text" size="15" name="detailCodeName" value="<%=detailCodeName%>"></td>
									<%} else {%>
									<td align=right>설명 : <input type="text" size="15" name="detailCodeName" value="<%=detailCodeName%>"></td>
									<%} %>
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
                                	<td class="tdblueM" width="8%">선택</td>
                                    <td class="tdblueM" width="23%">
                                    	<%if("SPEC".equals(codetype)) {%>
                                    	규격
                                    	<%}else {%>
                                    	이름(국문)
                                    	<%}%>
                                    </td>
                                    <td class="tdblueM" width="23%">코드</td>
                                    <td class="tdblueM" width="23%">
                                    	<%if("SPEC".equals(codetype)) {%>
                                    	품명
                                    	<%}else {%>
                                    	설명
                                    	<%}%>
                                    </td>
                                </tr>
<% 
	if( qr != null){
    while(qr.hasMoreElements()){
    	Object[] o = (Object[]) qr.nextElement();
		NumberCode ncode = (NumberCode)o[0];
		String codeOid = CommonUtil.getOIDString(ncode);
%>
                                <tr bgcolor="ffffff" >
                                	<td class="tdwhiteM" align="center"><input type="checkbox" name='code' value='<%=codeOid%>|<%=ncode.getCode() %>|<%=ncode.getKorName()%>' 
                                										   codeKey='<%=ncode.getCode() %>'
                                										   codeName='<%=ncode.getKorName()%>' 	onclick="selectNode(this)"></td>
                                    <td class="tdwhiteL"><%=ncode.getKorName()%></a></td>
                                    <!--td class="tdwhiteL"><%=StringUtil.checkReplaceStr(ncode.getEngName(), "&nbsp;")%></td-->
                                    <td class="tdwhiteL"><%=ncode.getCode()%></td>
                                    <td class="tdwhiteL"><%=ncode.getDescription()%></td>
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
      <table width="100%" border="0" cellpadding="0" cellspacing="10" > <!--//여백 테이블-->
       <tr>
    		<td>
    			<%=broker.getHtml("codeForm")%>
    		</td>
    	</tr>
    </table>
</form>

<script>

</script>
