<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="ext.narae.util.query.NaraeSimplePageQueryBroker"%>
<%@page import="ext.narae.util.query.NaraePageQueryBroker"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="wt.query.ClassAttribute"%>
<%@page import="wt.query.OrderBy"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="wt.query.SearchCondition"%>
<%@page import="ext.narae.service.org.People"%>
<%@page import="wt.query.QuerySpec"%>
<%@page import="ext.narae.service.org.beans.PeopleData"%>
<%@page import="ext.narae.service.org.Department"%>
<%@page import="ext.narae.util.ParamUtil"%>
<%@page import="ext.narae.util.WCUtil"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>

<%@ page import="wt.util.*, wt.org.*, wt.session.*, wt.inf.container.*, ext.narae.ui.*, java.util.*" %>
<%
String RESOURCE = "ext.narae.ui.common.resource.NaraeCommonRB";
String UNITRB = "ext.narae.ui.common.resource.NareUnitRB";
Locale locale = WTContext.getContext().getLocale();
WTContainerRef containerRef = WCUtil.getWTContainerRefForDrawing();
WTContainerRef partContainerRef = WCUtil.getWTContainerRefForPart();
String ORG_1095 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_1095", new Object[]{}, locale);
String ORG_570 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_570", new Object[]{}, locale);
String ORG_400 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_400", new Object[]{}, locale);
String ORG_1098 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_1098", new Object[]{}, locale);
String ORG_576 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_576", new Object[]{}, locale);
String ORG_936 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_936", new Object[]{}, locale);
String ORG_215 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_215", new Object[]{}, locale);
String ORG_718 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_718", new Object[]{}, locale);
String ORG_390 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_390", new Object[]{}, locale);


//String inputLabelName = request.getParameter("inputLabelName");
//String inputObjName = request.getParameter("inputObjName");


/*
out.println("ORG_1095=" + ORG_1095 + "<BR>");
out.println("ORG_570=" + ORG_570 + "<BR>");
out.println("ORG_215=" + ORG_215 + "<BR>");
out.println("ORG_390=" + ORG_390 + "<BR>");
*/
%>

<script language=JavaScript  src="/Windchill/netmarkets/jsp/narae/js/common.js"></script>
<link rel="stylesheet" href="/Windchill/netmarkets/jsp/narae/css/e3ps.css">
<%
	String mode = request.getParameter("mode");
	String chief = ParamUtil.checkStrParameter(request.getParameter("chief"));
	String inputObj = ParamUtil.checkStrParameter(request.getParameter("inputObj"));
	String inputLabel = ParamUtil.checkStrParameter(request.getParameter("inputLabel"));
	String command = request.getParameter("command");
	
	
	boolean isMultiSelect = false;
	if(mode==null)mode="";
	if ( mode.equalsIgnoreCase("m") ) isMultiSelect = true;

	String doid = request.getParameter("soid");
	ReferenceFactory rf = new ReferenceFactory();
	Department dept = null;
	String deptname = "Narae";
	
	if(doid!=null && doid.length()>0 && !doid.equals("null") && !doid.equals("root")){
		dept = (Department)rf.getReference(doid).getObject();
		deptname = dept.getName();
	}else if( !"search".equals(command) ){
		PeopleData data = new PeopleData((WTUser)SessionHelper.manager.getPrincipal());
		dept = data.department;
		deptname = data.departmentName;;
	}
	

    String key = request.getParameter("key");
    String keyvalue = request.getParameter("keyvalue");
    
    String sortKey = request.getParameter("sortKey");
    String sortType = request.getParameter("sortType");
    if(sortType == null || sortType.length() == 0) sortType = "false";
    
	QuerySpec qs = new QuerySpec(People.class);
	int ii = qs.addClassList(People.class,true);
	int jj = qs.addClassList(WTUser.class,true);
	int kk = qs.addClassList(Department.class,true);

	qs.appendWhere(new SearchCondition(People.class,"isDisable",SearchCondition.IS_FALSE),new int[]{ii});

    qs.appendAnd();
	qs.appendWhere(new SearchCondition(People.class,"departmentReference.key.id",Department.class,"thePersistInfo.theObjectIdentifier.id"),new int[]{ii,kk});

	qs.appendAnd();
    qs.appendWhere(new SearchCondition(People.class,"userReference.key.id",WTUser.class,"thePersistInfo.theObjectIdentifier.id"),new int[]{ii,jj});

	if(dept!=null){
		if (qs.getConditionCount() > 0)
        	qs.appendAnd();
		qs.appendWhere(new SearchCondition(Department.class,"thePersistInfo.theObjectIdentifier.id","=",dept.getPersistInfo().getObjectIdentifier().getId()),new int[]{kk});
	}

    if (keyvalue != null && !keyvalue.equals(""))
    {
        if (qs.getConditionCount() > 0)
        	qs.appendAnd();
        qs.appendWhere(new SearchCondition(People.class, key, SearchCondition.LIKE, "%" + keyvalue + "%"),
                         new int[] { ii });
    }
    
    if(chief.equals("true")){
    	if (qs.getConditionCount() > 0)
        	qs.appendAnd();
    
    	qs.appendWhere(new SearchCondition(People.class,"chief",SearchCondition.IS_TRUE));
    	
    }
    
    if (sortKey != null && !sortKey.equals(""))
    {
    	qs.appendOrderBy(new OrderBy(new ClassAttribute(People.class,sortKey),Boolean.getBoolean(sortType)),new int[]{ii});
    }
    else
    {
    	qs.appendOrderBy(new OrderBy(new ClassAttribute(People.class,"dutyCode"),false),new int[]{ii});
    }

	NaraePageQueryBroker broker = new NaraeSimplePageQueryBroker(request,qs);
	broker.setPsize(10);
	QueryResult qr = broker.search();
%>

<script language="javascript">
var isCh = navigator.appVersion.indexOf("Chrome") >= 0;
<!--
	function sorting(value) {
		document.selectPeopleForm.sortKey.value=value;
		document.selectPeopleForm.sessionid.value='';
		document.selectPeopleForm.command.value="search";
//		document.selectPeopleForm.action="/Windchill/netmarkets/jsp/narae/org/selectPeopleForm.jsp";
		document.selectPeopleForm.submit();
	}

	function disabledAllBtn()
	{
		var f = document.selectPeopleForm;
	    for(var i=0 ; i<f.length ; i++){
			if(f[i].type=="button")
				f[i].disabled = true;
		}
		f = document.getElementsByTagName('A');
		for(var i=0 ; i<f.length ; i++){
			f[i].disabled = true;
			f[i].href = '#';
		}
	}

	function search() {
		if(document.selectPeopleForm.keyvalue.value.length == 0) {
			alert("<%=ORG_1095%>.");
			document.selectPeopleForm.keyvalue.focus();
			return;
		}
		showProcessing();
		disabledAllBtn();
		document.selectPeopleForm.soid.value='';
		document.selectPeopleForm.sessionid.value='';
		document.selectPeopleForm.command.value = "search";
//		document.selectPeopleForm.submit();
	}

	function search2() {
	        if(document.selectPeopleForm.keyvalue.value.length == 0) {
	            alert("<%=ORG_1095%>.");
	            document.selectPeopleForm.keyvalue.focus();
	            return;
	        }
	        showProcessing();
	        disabledAllBtn();
	        document.selectPeopleForm.soid.value='';
	        document.selectPeopleForm.sessionid.value='';
	        document.selectPeopleForm.command.value = "search";
	        document.selectPeopleForm.submit();
	    }

	function onKeyPress2() { 
	    
		if (window.event) {
			if (window.event.keyCode == 13) search();
		} else return;
	}


	function isCheckedCheckBox() {
		form = document.selectPeopleForm;
		if(form.check == null) {
			return false;
		}
	
		len = form.check.length;
		if(len) {
			for(var i = 0; i < len;i++) {
				if(form.check[i].checked == true) {
					return true;
				}
			}
		}
		else {
			if(form.check.checked == true) {
				return true;
			}
		}
	
		return false;
	
	}

	function checkList() {
		form = document.selectPeopleForm;

		var arr = new Array();
		var subarr = new Array();
		if(!isCheckedCheckBox()) {
			return arr;
		}
	
		len = form.check.length;
		
		var idx = 0;
		if(isCh){
		if(len) {
			for(var i = 0; i < len; i++) {			
				if(form.check[i].checked == true) {
					
					var value = form.check[i].value;
					var s = value.split("&");
					subarr = new Array();
					
					subarr[0] = s[0];//WTUser OID
					subarr[1] = s[1];//People OID
					subarr[2] = s[2];//dept OID
					subarr[3] = s[3];//id
					subarr[4] = s[4];//이름
					subarr[5] = s[5];//department name
					subarr[6] = s[6];//직위
					subarr[7] = s[7];//직위
					subarr[8] = s[8];//email name
					
					arr[idx++] = subarr;
				}
			}
		} else {
			if(form.check.checked == true) {
				
				var value = form.check.value;
				var s = value.split("&");
				subarr = new Array();
				
					subarr[0] = s[0];//WTUser OID
					subarr[1] = s[1];//People OID
					subarr[2] = s[2];//dept OID
					subarr[3] = s[3];//id
					subarr[4] = s[4];//이름
					subarr[5] = s[5];//department name
					subarr[6] = s[6];//직위
					subarr[7] = s[7];//직위
					subarr[8] = s[8];//email name
					
				arr[idx++] = subarr;
			}
		}
		}else{ // 크롬 외 브라우저
			if(len) {
				for(var i = 0; i < len; i++) {			
					if(form.check[i].checked == true) {
// 						subarr = new Array();
// 						subarr[0] = form.check[i].value;//WTUser OID
// 						subarr[1] = form.check[i].poid;//People OID
// 						subarr[2] = form.check[i].doid;//dept OID
// 						subarr[3] = form.check[i].uid;//id
// 						subarr[4] = form.check[i].sname;//이름
// 						subarr[5] = form.check[i].dname;//department name
// 						subarr[6] = form.check[i].duty;//직위
// 						subarr[7] = form.check[i].dutycode;//직위
// 						subarr[8] = form.check[i].email;//email name

						var value = form.check[i].value;
						var s = value.split("&");
						subarr = new Array();

						subarr[0] = s[0];//WTUser OID
						subarr[1] = s[1];//People OID
						subarr[2] = s[2];//dept OID
						subarr[3] = s[3];//id
						subarr[4] = s[4];//이름
						subarr[5] = s[5];//department name
						subarr[6] = s[6];//직위
						subarr[7] = s[7];//직위
						subarr[8] = s[8];//email name

						arr[idx++] = subarr;
					}
				}
			} else {
				if (form.check.checked == true) {
					// 					subarr = new Array();
					// 						subarr[0] = form.check.value;//WTUser OID
					// 						subarr[1] = form.check.poid;//People OID
					// 						subarr[2] = form.check.doid;//dept OID
					// 						subarr[3] = form.check.uid;//id
					// 						subarr[4] = form.check.sname;//이름
					// 						subarr[5] = form.check.dname;//department name
					// 						subarr[6] = form.check.duty;//직위
					// 						subarr[7] = form.check.dutycode;//직위
					// 						subarr[8] = form.check.email;//email name

					var value = form.check.value;
					var s = value.split("&");
					subarr = new Array();

					subarr[0] = s[0];//WTUser OID
					subarr[1] = s[1];//People OID
					subarr[2] = s[2];//dept OID
					subarr[3] = s[3];//id
					subarr[4] = s[4];//이름
					subarr[5] = s[5];//department name
					subarr[6] = s[6];//직위
					subarr[7] = s[7];//직위
					subarr[8] = s[8];//email name

					arr[idx++] = subarr;
				}
			}
		}
		return arr;
	}

	function onSelect2() {

		//$("#creator")
		var creator = parent.opener.document.getElementById('<%=inputObj%>');
		var tempcreator = parent.opener.document.getElementById('<%=inputLabel%>');
		
		form = document.selectPeopleForm;
		var arr = checkList();
		if(arr.length == 0) {
			alert("<%=ORG_570%>.");
			return;
		}
		
		creator.value = arr[0][0];
		tempcreator.value = arr[0][4];
		if(isCh){
			var value = form.check.value;
		var s = value.split("&");
		subarr = new Array();
		
		subarr[0] = s[0];//WTUser OID
		subarr[1] = s[1];//People OID
		subarr[2] = s[2];//dept OID
		subarr[3] = s[3];//id
		subarr[4] = s[4];//이름
		subarr[5] = s[5];//department name
		subarr[6] = s[6];//직위
		subarr[7] = s[7];//직위
		subarr[8] = s[8];//email name
		
//		window.returnValue = arr;
		parent.self.close();
		
		}else{
		console.log("익스");
		window.returnValue = arr;
		parent.window.close();
		}
	}

	function checkCbox(cbox)
	{
		if(cbox==null) 
			len=0;
		else
		{
			len=cbox.length
			if(''+len == 'undefined') len = 1;
		}
		return len;
	}
	
	function selectAll(cboxAll, cbox) 
	{	
		var len = checkCbox(cbox);
		if(cbox != null)
			if(len > 1){
				for(var i=0 ; i<len ; i++) 
				{
					if ( cboxAll.checked == true ) cbox[i].checked=true;
					else	cbox[i].checked=false;
				}
			}else{
				if ( cboxAll.checked == true ) cbox.checked=true;
				else cbox.checked=false;
			}
	} 
	
	function selectAllUnChecked(cboxAll, cbox)
	{
		if(cboxAll == null) return;
		var len = checkCbox(cbox);
		if(cbox != null) {
			if (len > 1){
				if ( cboxAll.checked == true) cboxAll.checked = false;
			} else{
				if ( cboxAll.checked == true) cboxAll.checked = false;
				else cboxAll.checked = true;
			}
	
			for (var i=0 ; i<len ; i++ )
			{
				if(len>1) {
					if( cbox[i].checked != true) break;
					if ( (i+1) == len) cboxAll.checked = true;
				}
			}
		}
	}

	function clickThis(param) {
		if ( !param.checked ) return;
	
		var len = <%=qr.size()%>;
		var checkStr = param.value;
	
		var objArr = document.selectPeopleForm;
		if (len > 1) {
			for ( var i = 0 ; i < objArr.length ; i++ ) {
				if ( objArr[i].type == "checkbox" ) {
					if ( checkStr != objArr[i].value ) {
						objArr[i].checked = false;
					}
				}
			}
		}	
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
	
//-->
</script>

<SCRIPT LANGUAGE="JavaScript">
<!--
	function showProcessing()
	{
		var div1 = document.getElementById('div1');
		var div2 = document.getElementById('div2');
	
		div1.style.left = (document.body.offsetWidth / 2 - 160)+"px";
		div1.style.top = (document.body.offsetHeight / 2 - 100)+"px";
		div1.style.display = "block";
	
		div2.style.width = div1.offsetWidth;
		div2.style.height = div1.offsetHeight;
		div2.style.top = div1.style.top;
		div2.style.left = div1.style.left;
		div2.style.zIndex = div1.style.zIndex - 1;
		div2.style.display = "block";
	}
//-->
</SCRIPT>
<DIV ID="div1" style='POSITION:absolute;Z-INDEX:10;display:none'>
	<img src="/Windchill/netmarkets/jsp/narae/portal/icon/processing.gif" border="0">
</DIV>
<iframe
  id="div2"
  src="javascript:false;"
  scrolling="no"
  frameborder="0"
  style="position:absolute; top:0px; left:0px; display:none;">
</iframe>

<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
<form method="post" name="selectPeopleForm">
<input type="hidden" name="sortKey">
<input type="hidden" name="sortType" value="true">
<input type="hidden" name="oid" value="root">
<input type="hidden" name="mode" value="<%=mode%>">
<input type="hidden" name="command">
<input type="hidden" name='soid' value="<%=doid==null?"":doid %>">
<input type="hidden" name="chief" value="<%=chief%>">
<input type="hidden" name="inputLabel" value="<%=inputLabel%>">
<input type="hidden" name="inputObj" value="<%=inputObj%>">

<table width=100% ><tr><td>

<table width=95% height=40 align=center border=0>
	<tr>
		<td>
			<table border=0 cellpadding=0 cellspacing=0 >
				<tr>
					<td>[</td>
					<td nowrap><b><%=deptname%></b></td>
					<td>]</td>
				</tr>
			</table>
		</td>
		<input type="hidden" name="key" value="name">
		<td align="right">
			<table border=0 cellpadding="0" cellspacing=0 align="right">
                            <tr>
                            <td>
                            <%=ORG_400%>&nbsp;:&nbsp;<input type="text"   onkeypress = "onKeyPress2()" name="keyvalue" size="15" id=i style="ime-mode:active;" value="<%=keyvalue==null?"":keyvalue %>">
                            </td>
                                <td>
			             <table border=0 cellpadding="0" cellspacing=2 align="right">
                            <tr>
                                <td> <script>setButtonTagNarae("<%=ORG_1098%>","50","search2()","");</script></td>
                                <td> <script>setButtonTagNarae("<%=ORG_576%>","50","onSelect2()","");</script></td>
                                <td> <script>setButtonTagNarae("<%=ORG_936%>","50","parent.self.close()","");</script></td>
                            </tr>
                        </table>        
              </td>
                            </tr>
                        </table>  
		</td>
	</tr>
</table>

</td></tr><tr><td>

			<table width="95%" border="0" cellpadding="1" cellspacing="1" bgcolor=#752e41 align=center>
				<tr><td height=1 width=100%></td>
			</tr>
			</table>
<table width="95%" border="0" cellpadding="0" cellspacing="0" align=center>			
	<tr >
		<td class="tdblueM" width="20" align=center>
			<%if(isMultiSelect){%>
			<input type="checkbox" name="checkboxAll" onClick="javascript:selectAll(this, document.selectPeopleForm.check)">
			<%}else{%>&nbsp;<%}%>
		</td>
		<td class="tdblueM"  width=60><a href="javascript:sorting('<%=People.NAME%>');"><%=ORG_400%></A></td>
		<td class="tdblueM"><a href="javascript:sorting('<%=People.DUTY_CODE%>');"><%=ORG_215%></A></td>
		<td class="tdblueM"><%=ORG_718%></td>
		<td class="tdblueM"><%=ORG_390%></td>
		<td class="tdblueM">부서장</td>
	</tr>
<%
	while(qr.hasMoreElements()){
		Object[] o = (Object[])qr.nextElement();
		PeopleData pd = new PeopleData(o);
%>
		

	<tr  onMouseover="this.style.backgroundColor='#efefef'" onMouseout="this.style.backgroundColor='#ffffff'"> 
		<td class="tdwhiteL" width="20">
			<input type="checkbox" name="check" 
			value="<%=pd.wtuserOID%>&<%=pd.peopleOID%>&<%=pd.department!=null?PersistenceHelper.getObjectIdentifier(pd.department).getStringValue ():"" %>&<%=pd.id%>&<%=pd.name%>&<%=pd.departmentName%>&<%=pd.duty%>&<%=pd.dutycode%>&<%=pd.email%>"
			poid="<%=pd.peopleOID%>" 
			email="<%=pd.email%>" 
			dname="<%=pd.departmentName%>" 
			duty="<%=pd.duty%>" 
			uid="<%=pd.id%>" 
			sname="<%=pd.name%>" 
			doid="<%=pd.department!=null?PersistenceHelper.getObjectIdentifier(pd.department).getStringValue ():"" %>" 
			dutycode="<%=pd.dutycode%>" 
			<%if(!isMultiSelect)out.print("onclick='javascript:clickThis(this)'");%>></td>
		<td class="tdwhiteL"><%=pd.name%></td>
		<td class="tdwhiteL"><%=pd.duty%>&nbsp;</td>
		<td class="tdwhiteL"><%=pd.departmentName%>&nbsp;</td>
		<td class="tdwhiteL" title="<%=pd.email%>"><div style="width:150;border:0;padding:0;margin:0;text-overflow:ellipsis;overflow:hidden;">
						<nobr>
                    <%=pd.email%>
                		</nobr>
                	</div></td>
        <td class="tdwhiteM"><%=pd.getChief()==true?"O":"&nbsp;" %> </td>    
	</tr>
<%}%>
</table>

</td></tr><tr><td>

<table width="95%" border="0" cellpadding="0" cellspacing="0" align=center valign=top>
	<tr height=25>
	<td>

	<%=broker.getHtml("selectPeopleForm")%>
	
	</td></tr>

	
</table>

</td></tr></table>
</form>
