<%@page import="ext.narae.service.erp.beans.ERPSearchHelper"%>
<%@page import="ext.narae.util.StringUtil"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@page import="java.util.HashMap"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="org.json.simple.*"%> 

<jsp:useBean id="wtcontext" class="wt.httpgw.WTContextBean" scope="session" />
<jsp:setProperty name="wtcontext" property="request" value="<%=request%>" />

<%
	String codetype = StringUtil.checkNull(request.getParameter("codetype"));
    String command = StringUtil.checkNull(request.getParameter("command"));
    String so_no =StringUtil.checkNull(request.getParameter("so_no"));
    String CustName =StringUtil.checkNull(request.getParameter("CustName"));
    String startDate =StringUtil.checkNull(request.getParameter("startDate"));
    startDate = startDate.replace("-",".");
    String endDate =StringUtil.checkNull(request.getParameter("endDate"));
    endDate = endDate.replace("-",".");
    String so_name =StringUtil.checkNull(request.getParameter("so_name"));
    String PrjSeqNo =StringUtil.checkNull(request.getParameter("PrjSeqNo"));  

    ResultSet rs = null;
	HashMap map = null;

	JSONArray jsonArray = null;
	JSONObject jsonObj = null;
    
    if(command.equals("SEARCH")) {
    	map = new HashMap();
    	map.put("so_no",so_no);  				// PrjNo
    	map.put("CustName",CustName); 		// 거래처명
    	map.put("sodtfr",startDate);			// 수주번호 startDate
    	map.put("sodtto",endDate);			// 수주번호 endDate
    	map.put("so_name",so_name);			// 프로젝트명
    	map.put("PrjSeqNo",PrjSeqNo);  		// PrjNo
    	rs = ERPSearchHelper.manager.getErpProject(map);
    } else {
    	map = new HashMap();
    	map.put("so_no",so_no);  				// PrjNo
    	map.put("CustName",CustName); 		// 거래처명
    	map.put("sodtfr",startDate);			// 수주번호 startDate
    	map.put("sodtto",endDate);			// 수주번호 endDate
    	map.put("so_name",so_name);			// 프로젝트명
    	map.put("PrjSeqNo",PrjSeqNo);  		// PrjNo
    	rs = ERPSearchHelper.manager.getErpProject(map);
	}
	System.out.println();
	System.out.println("# PROJECT SEARCHING");
	System.out.println();
%>

<link rel="stylesheet" type="text/css" media="screen" href="/Windchill/netmarkets/jsp/narae/js/jquery-ui-1.8.16.custom/css/redmond/jquery-ui-1.8.16.custom.css" />
<link rel="stylesheet" type="text/css" media="screen" href="/Windchill/netmarkets/jsp/narae/js/jquery.jqGrid-4.2.0/css/ui.jqgrid.css" />
<link rel="stylesheet" type="text/css" media="screen" href="/Windchill/netmarkets/jsp/narae/js/jquery.jqGrid-4.2.0/src/css/ui.multiselect.css" />

<link rel="stylesheet" href="/Windchill/netmarkets/jsp/narae/css/e3ps.css" type="text/css">
<link rel="stylesheet" href="/Windchill/netmarkets/jsp/narae/css/css.css" type="text/css">
<link rel="stylesheet" href="/Windchill/netmarkets/jsp/narae/css/default.css" type="text/css">

<script language=JavaScript  src="/Windchill/netmarkets/jsp/narae/js/common.js"></script>

<script src="/Windchill/netmarkets/jsp/narae/js/jquery-1.7.1.js"></script>
<script src="/Windchill/netmarkets/jsp/narae/js/jquery.jqGrid-4.2.0/js/i18n/grid.locale-en.js" type="text/javascript"></script>
<script src="/Windchill/netmarkets/jsp/narae/js/jquery.jqGrid-4.2.0/js/jquery.jqGrid.min.js" type="text/javascript"></script>

<script type="text/javascript">
<!--

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
    	
    	form = document.forms[0];
    	var arr = new Array();
    	
		if(form.code) {
			var chkLen = form.code.length;
			if(chkLen) {
				for(var i = 0; i < chkLen; i++) {
					var subarr = new Array();
					if(form.code[i].checked){
						
						prjNo = form.code[i].prjNo;
						prjSeqno = form.code[i].prjSeqno;
						unitCode = form.code[i].unitCode;
						prjName = form.code[i].prjName;
						
					}					
					
				}
			}else{
				if(form.code.checked){
					prjNo = form.code.prjNo;
					prjSeqno = form.code.prjSeqno;
					unitCode = form.code.unitCode;
					prjName = form.code.prjName;
					
				}
			}
			
			opener.projectSelect(prjName,prjNo,prjSeqno,unitCode);
			self.close();
		}
    }
	
	function searchCode(){
		pForm = document.codeForm;		
		pForm.submit();
	}
	
 	function openCal(variableName) {
        var str="/Windchill/netmarkets/jsp/narae/common/calendar.jsp?form=codeForm&obj="+variableName;
        var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=0,resizable=0,";
        var leftpos = (screen.width - 224)/ 2;
        var toppos = (screen.height - 230) / 2 ;
        var rest = "width=224,height=230,left=" + leftpos + ',top=' + toppos;

        var newwin = window.open( str , "calendar", opts+rest);
        newwin.focus();
    }

    function clearText(str) {
        var pForm = document.codeForm;
        pForm[str].value="";
    }
	
//-->
</script>

<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
<form name=codeForm method=post >
    <input type=hidden name=codetype value="<%=codetype%>">
    <input type=hidden name=command value="SEARCH">
   
	
    <table width="100%" border="0" cellpadding="0" cellspacing="10" > <!--//여백 테이블-->
        <tr align=center height=5>
            <td>
                <table width="100%" border="0" cellpadding="10" cellspacing="3" >
                    <tr>
						<table width="100%" border="0" cellpadding="1" cellspacing="1" bgcolor=8f2436 align=center style="padding-bottom:10px">
							<tr> 
								<td height=30 width=93% align=center><B><font color=white>프로젝트 검색</td>
							</tr>
						</table>
                    </tr>
                    
                    <tr align=left>
						<td>
							<table width="100%" border="0" cellpadding="0" cellspacing="0">
				        		<tr align="center">
									<td valign="top" style="padding:0px 0px 0px 0px">
						                <table width="100%" border="0" cellpadding="1" cellspacing="1" bgcolor=#752e41 align="center">
						                    <tr>
						                        <td height=1 width=100%></td>
						                    </tr>
						                </table>                                        
                                        <table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" style="border-left-width: 1px;border-left-style: solid;border-left-color: #e6e6e6;">
                                            <tr>
                                                <td class="tdblueM" width="150">수주번호</td>
                                                <td class="tdwhiteL">
                                                    <input type=text name=so_no value="<%=so_no%>" size=15 onKeyDown="javascript: if(event.keyCode==13){searchCode();}">
                                                </td>
                                                <td class="tdblueM" width="150">수주명</td>
                                                <td class="tdwhiteL">
                                                    <input type=text name=so_name value="<%=so_name%>" size=40 onKeyDown="javascript: if(event.keyCode==13){searchCode();}">
                                                </td>                                                
                                            </tr>
											<tr>
                                                <td class="tdblueM" width="150">수주일자</td>
                                                <td class="tdwhiteL">
                                                    <input name="startDate" class="txt_field" size="15"  maxlength="15" readonly="readonly" onclick="openCal('startDate');return false;" value="<%=startDate%>" />&nbsp;<a href="#" onclick="openCal('startDate');return false;"><img src="/Windchill/netmarkets/jsp/narae/portal/images/calendar_icon.gif" border="0" /></a>
													<a href="#" onclick="clearText('startDate');return false;"><img src="/Windchill/netmarkets/jsp/narae/portal/images/x.gif" border="0" />
													~ <input name="endDate" class="txt_field" size="15"  maxlength="15" readonly="readonly" onclick="openCal('endDate');return false;" value="<%=endDate%>" />&nbsp;<a href="#" onclick="openCal('endDate');return false;"><img src="/Windchill/netmarkets/jsp/narae/portal/images/calendar_icon.gif" border="0" /></a>
													<a href="#" onclick="clearText('endDate');return false;"><img src="/Windchill/netmarkets/jsp/narae/portal/images/x.gif" border="0" />
                                                </td>   
                                                <td class="tdblueM" width="150">거래처명</td>
                                                <td class="tdwhiteL">
                                                    <input type=text name=CustName value="<%=CustName%>" size=40 onKeyDown="javascript: if(event.keyCode==13){searchCode();}">
                                                </td>												
                                            </tr>
                                        </table>
			                    	</td>
			                    </tr>
			                    <tr>
			                    	<td>
			                    	<table  border="0" cellpadding="0" cellspacing="3" align=right>
			                    		<tr>
			                    			<td>
												<a style="FONT-SIZE: 8pt;" onclick="javascript:searchCode();"><table class="" border="0" cellpadding="0" cellspacing="0" width="60"><tbody><tr><td width="7"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left.gif" alt="" height="20" width="7"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center">검색</div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right.gif" alt="" height="20" width="12"></td></tr></tbody></table></a>
											</td>
<!--		                                    <td><script>setButtonTag3D("선택","60","javascript:selectCode();","menu A:link");</script></td>-->
		                                    <td>
												<a style="FONT-SIZE: 8pt;" onclick="javascript:self.close();"><table class="" border="0" cellpadding="0" cellspacing="0" width="60"><tbody><tr><td width="7"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_left.gif" alt="" height="20" width="7"></td><td background="/Windchill/netmarkets/jsp/narae/portal/img/btn_mid.gif" valign="middle"><table align="center" border="0" cellpadding="0" cellspacing="0"><tbody><tr><td><div id="_text" align="center">닫기</div></td></tr></tbody></table></td><td width="12"><img src="/Windchill/netmarkets/jsp/narae/portal/img/btn_right.gif" alt="" height="20" width="12"></td></tr></tbody></table></a>
											</td>
			                    		</tr>
			                    	</table>
                                    </td>
								</tr>
							</table>
						</td>
					</tr>
                    <tr  align=center>
                        <td valign="top" style="padding:0px 0px 0px 0px">
                            <table width="100%" border="0" cellpadding="1" cellspacing="1" bgcolor=#752e41 align=center >
								<tr><td height=1 width=100%></td></tr>
						    </table>
<%
		jsonArray = new JSONArray();	

   		if(rs!= null){
	    	while(rs.next()){
			
				String so_no2 = (String)rs.getObject("so_no"); //수주번호
				String pjt_no = (String)rs.getObject("pjt_no"); //프로젝트번호
				String so_name2 = (String)rs.getObject("so_name"); //수주명
				String cust_cd = (String)rs.getObject("cust_cd"); //거래처코드
				String cust_name = (String)rs.getObject("cust_name"); //거래처명
				String so_dt = (String)rs.getObject("so_dt"); //수주일자
				String item_cd = (String)rs.getObject("item_cd"); //품목코드
				String item_nm = (String)rs.getObject("item_nm"); //품목명
				String p_item_cd = (String)rs.getObject("p_item_cd"); //상위품목코드
				String p_item_nm = (String)rs.getObject("p_item_nm"); //상위품목명

				jsonObj = new JSONObject();

				jsonObj.put("so_no", so_no2);
				jsonObj.put("pjt_no", pjt_no);
				jsonObj.put("so_name", so_name2);
				jsonObj.put("cust_cd", cust_cd);
				jsonObj.put("cust_name", cust_name);
				jsonObj.put("so_dt", so_dt);
				jsonObj.put("item_cd", item_cd);
				jsonObj.put("item_nm", item_nm);
				jsonObj.put("p_item_cd", p_item_cd);
				jsonObj.put("p_item_nm", p_item_nm);

				jsonArray.add(jsonObj);
%>
<table id="gridList"></table>
<div id="pager2"></div>
<%
	    	}
		}
%>
<script type="text/javascript">
	$(document).ready(function() {

		var mydata = eval(<%=jsonArray%>);

		jQuery("#gridList").jqGrid( {
			datatype: "local",
			data: mydata,
			height: 390,
			autowidth: true,
			colNames:
				['수주번호','프로젝트번호','수주명','거래처코드','거래처명','수주일자','품목코드','품목명','상위품목코드','상위품목명'],
			colModel:[
				{name:'so_no',index:'so_no',align:'center',width:70},
				{name:'pjt_no',index:'pjt_no',width:130},
				{name:'so_name',index:'so_name',align:'center',width:280},
				{name:'cust_cd',index:'cust_cd',align:'center',width:70},
				{name:'cust_name',index:'cust_name',width:80},
				{name:'so_dt',index:'so_dt',align:'center',width:100},
				{name:'item_cd',index:'item_cd',align:"center",width:60},
				{name:'item_nm',index:'item_nm',align:'center',width:100},
				{name:'p_item_cd',index:'p_item_cd',align:'center',width:60},
				{name:'p_item_nm',index:'p_item_nm',align:'center',width:60}
			],			
			rowNum: 20,
			rowList: [20,30,50,100],
			pager: '#pager2',
			emptyrecords: "No Records.",
			sortname: "pName",
			viewrecords: true,
			sortorder: "desc",
			rownumbers: true,
			rownumWidth: 40,
			forceFit:false,
			shrinkToFit: false,
			gridview: true,
			multiselect: false,
			ondblClickRow: function(rowid, iCol) {
				var list = jQuery("#gridList").getRowData(rowid);
				opener.projectSelect(list.so_name,list.so_no,list.pjt_no,'');
				self.close();
			},
            localReader: {
	            repeatitems: false,
                cell: "",
	            no: 0
            }
		});
	})
</script>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</form>