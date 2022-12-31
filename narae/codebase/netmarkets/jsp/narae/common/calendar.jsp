<%@page import="ext.narae.util.CommonUtil"%>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@page import ="java.util.*,java.text.*"%>
<%@page import ="wt.session.*"%>
<jsp:useBean id="wtcontext" class="wt.httpgw.WTContextBean" scope="request" />
<jsp:setProperty name="wtcontext" property="request" value="<%=request%>" />
<%
	String title = "달력";//ParamUtil.getStrParameter(request.getParameter("title"),"달력");
	String formname = request.getParameter("form");
	if(formname == null || formname.length() < 1) formname = "forms[0]";
	String objname = request.getParameter("obj");
	if(objname == null || objname.length() < 1) objname = "objects[0]";
	String chageObj = request.getParameter("chageObj");
	if(chageObj == null || chageObj.length() < 1) chageObj = "";
	
	if(chageObj == null){
		chageObj = "";
	}
	
	String function = request.getParameter("function");
	if(function == null || function.length() < 1) function = "";
//	String date = ParamUtil.getStrParameter(request.getParameter("date"));
	String mode = request.getParameter("MODE");
	if(mode == null || mode.length() < 1) mode = "perMonth";

	
	
        SimpleDateFormat format = new SimpleDateFormat("EEE",SessionHelper.manager.getLocale());
        
        Calendar ca = java.util.Calendar.getInstance();
        ca.set(Calendar.DAY_OF_WEEK,1);
        String w1 = format.format(ca.getTime());
        ca.add(Calendar.DATE,1);
        String w2 = format.format(ca.getTime());
        ca.add(Calendar.DATE,1);
        String w3 = format.format(ca.getTime());
        ca.add(Calendar.DATE,1);
        String w4 = format.format(ca.getTime());
        ca.add(Calendar.DATE,1);
        String w5 = format.format(ca.getTime());
        ca.add(Calendar.DATE,1);
        String w6 = format.format(ca.getTime());
        ca.add(Calendar.DATE,1);
        String w7 = format.format(ca.getTime());
        
        String yearString = "년";
        if(CommonUtil.isUSLocale()){
        	yearString = " . ";
        }
        
        SimpleDateFormat format2 = new SimpleDateFormat("MMM",SessionHelper.manager.getLocale());
        
        %>
        <script>
        var monthArry = new Array();
        <%
        ca.set(Calendar.MONTH,0);
        for(int i=1; i<= 12; i++){
            String m = format2.format(ca.getTime());
          
            %>
            monthArry[<%=i%>] = "<%=m%>";  
            <%
        	ca.add(Calendar.MONTH,1);
       }
       %>
    </script>


<%@page import="java.util.Date"%><html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
<TITLE><%=title%></TITLE>
<style type="text/css">
<!--
	A:link, A:visited	{font-size:10pt;font-family:arial;font-weight:bold;color:#4271ad;text-decoration:none;}
	A:active			{font-size:10pt;font-family:arial;font-weight:bold;color:#4271ad;text-decoration:none;}
	A:hover  			{font-size:10pt;font-family:arial;font-weight:bold;color:#828282;text-decoration:none;}
	td					{font-family:arial;font-size:9pt;color:black;margin:0 0 0 0}
	body				{font-family:arial;font-size:9pt;color:black; line-height:14pt; font-color:black; color:black}
	inputline 			{Font-Family:arial;font-Size: 9pt; Background-Color:#efefef; Color:black; Border:1x SOLID BLACK}
	selectline 			{Font-Family:arial;font-Size: 9pt; Background-Color:#efefef; Color:black; Border:1x SOLID BLACK}
	inputbutton 		{Font-Family:arial;font-Size: 9pt; Background-Color:#efefef; Color:black; Border:1x SOLID BLACK}
	line14				{line-height:14pt; font-size:9pt ; font-family:arial; font-size:9pt;  font-family:arial ; }
	line15				{line-height:15pt; font-size:9pt ; font-family:arial; font-size:9pt;  font-family:arial ; }
//-->
</style>
</head>

<body leftmargin="0" topmargin="0">
<form>
<input type="hidden" name="checkParentDate">
<script language="javascript">
<!--
	var to_year;
	var to_month;
	var global_year;
	var global_month;
	//mode = 'perMonth' or 'perYear'
	var mode = '<%=mode%>';

///////////////////////////////////////////////
	function chng(date)
	{
		var show_year = global_year;
		var show_month = global_month;//기본값이 10미만일 경우 length가 "undifinded"
		var show_date = date;

		if(show_month < 10)
		{
			if(show_month.length == undefined || show_month.length == 1)
				show_month = "0"+show_month;
			else
				show_month = show_month;
		}
		else
		{
			show_month = show_month;
		}
//		else if(show_month < 10 && show_month.length >= 2) show_month = show_month;

		if(show_date <10)
		{
			if(show_date.length == undefined || show_date.length == 1)
				show_date = "0"+show_date;
			else
				show_date = show_date;
		}
		else show_date=show_date;

<%	if ( !function.equals("") && function.length() > 0 )	{	%>
		opener.<%=function%>('<%=objname%>',show_year+"-"+show_month+"-"+show_date);
<%	} else if ( !objname.equals("") && objname.length() > 0 )	{	%>
		opener.document.<%=formname%>.<%=objname%>.value = show_year+"-"+show_month+"-"+show_date;
<%	} else {	%>
		opener.document.forms[0].date.value = show_year+"-"+show_month+"-"+show_date;
		opener.insertDate();
<%	} %>

<% 
	if("true".equals(chageObj)){
%>
<% 		if(objname.trim().equals("cstartdate")){%>		
		opener.isChangeDuration();
<%		}else if(objname.trim().equals("cenddate")){%>
		opener.isChangeDuration2();
<%		}%>	
<%	} %>

		self.close();


	}

	function chang(date,dal)
	{
		var show_year = global_year;
		var show_month = dal;//기본값이 10미만일 경우 length가 "undifinded"
		var show_date = date;

		if(show_month < 10){
			if(show_month.length == undefined || show_month.length == 1)
			{
				show_month = "0"+show_month;
			}
			else
				show_month = show_month;
		}

//		else if(show_month < 10 && show_month.length >= 2) show_month = show_month;
		else show_month = show_month;


		if(show_date < 10 )
		{
			if(show_date.length == undefined || show_date.length == 1)
				show_date = "0"+show_date;
			else
				show_date = show_date;
		}
		else show_date=show_date;

<%	if ( !function.equals("") && function.length() > 0 )	{	%>
		opener.<%=function%>('<%=objname%>',show_year+"-"+show_month+"-"+show_date);
<%	} else if ( !objname.equals("") && objname.length() > 0 )	{	%>
		opener.document.<%=formname%>.<%=objname%>.value = show_year+"-"+show_month+"-"+show_date;
<%	} else {	%>
		opener.document.forms[0].date.value = show_year+"-"+show_month+"-"+show_date;
		opener.insertDate();
<%	} %>
		self.close();
	}


	function getDateArray()
	{
<%
		String checkParentDate = request.getParameter("checkParentDate");
		if( checkParentDate == null ) checkParentDate = "true";
%>
		if("<%=checkParentDate%>" == "false") {
			return "";
		} else
			document.forms[0].checkParentDate.value = "false";

		var parentDateValue = opener.document.<%=formname%>.<%=objname%>.value;
		if(parentDateValue.length == 0) return "";


		var dateArray = "";
		if(parentDateValue.split("-").length > 2)
			dateArray = parentDateValue.split("-");
		else if(parentDateValue.split("-").length > 2)
			dateArray = parentDateValue.split("-");

		return dateArray;
	}

////////////////////////	 mouse over
	function tdOverHandler(tdObj)
	{
		tdObj.style.backgroundColor = "#EFEFD6";
		tdObj.style.color = "#4271AD";
		tdObj.style.fontSize = "14pt";
		tdObj.style.fontWeight = "bold";
	}
/////////////////////////////            out of focus
	function tdOutHandler(tdObj)
	{
		tdObj.style.backgroundColor = "#EFF3F4";
		tdObj.style.color = "#4271AD";
		tdObj.style.fontSize = "9pt";
		tdObj.style.fontWeight = "bold";
	}
///////////////////
	function changeYearHandler(num)
	{
		document.forms[0].checkParentDate.value = "false";
		document.forms[0].year.value = eval(global_year)+eval(num);
		document.forms[0].month.value = global_month;
		document.forms[0].submit();
	}
////////////////////
	function changeMonthHandler(num)
	{
		if((eval(global_month) + eval(num)) < 1) {
			global_month = 12;
			changeYearHandler(num);
		} else if((eval(global_month) + eval(num)) > 12) {
			global_month = 1;
			changeYearHandler(num);
		} else {
			document.forms[0].checkParentDate.value = "false";
			document.forms[0].year.value = global_year;
			document.forms[0].month.value = eval(global_month) + eval(num);
			document.forms[0].submit();
		}
	}
/////////////////////////
	function changeMode(mode,_year)
	{
		document.forms[0].checkParentDate.value = "true";
		document.forms[0].year.value=_year;
		if(mode == 'perMonth')
		{
			window.resizeTo(224, 260);
			window.moveTo( (screen.width - 224)/ 2,(screen.height - 260) / 2 ) ;
			document.forms[0].MODE.value = 'perMonth';
		}
		else if(mode == 'perYear')
		{
			window.moveTo( (screen.width - 824)/ 2,(screen.height - 800) / 2 ) ;
			window.resizeTo(824, 750);
			document.forms[0].MODE.value = 'perYear';
		}	
		document.forms[0].submit();
	}
////////////////////////////////////////////////
	function changeToMonth(mode,_year,_month)
	{
		window.resizeTo(224, 270);
		window.moveTo( (screen.width - 224)/ 2,(screen.height - 270) / 2 ) ;
		document.forms[0].checkParentDate.value="true";
		document.forms[0].year.value=_year;
		document.forms[0].month.value=_month;
		document.forms[0].MODE.value = 'perMonth';
		document.forms[0].submit();
	}
////////////////////////////
	function showMCalendar()
	{


		var now=new Date();
		var year=now.getFullYear();
		to_year = year;
		to_month = now.getMonth()+1;

		if(location.search){
			var arr = location.search.split("&");
			var year = "";
			var month = "";
			for ( var i = 0 ; i < arr.length ; i++ ) {
				var paramStr = arr[i];
				var paramArr = paramStr.split("=");
				if ( paramArr.length == 2 ) {
					var key = paramArr[0];
					var value = paramArr[1];

					if ( key == "year" || key == "?year" ) year = value;
					if ( key == "month" || key == "?month" ) month = value;
				}
			}
			if ( year == "" ) year=now.getFullYear();
			if ( month == "" ) month=now.getMonth() + 1;
		}
		else
		{
			var year=now.getFullYear();
			var month=now.getMonth() + 1;
		}

		var dateArray = getDateArray();

		if(dateArray != "")
		{
			year = dateArray[0];
			month = Number(dateArray[1]);
		}

		global_year = year;
		global_month = month;
		

		var date=now.getDate();
		var day=now.getDay();
		var firstDate = new Date(year, month-1, 1);
		var firstDay = firstDate.getDay();

		if((year%4 == 0 && year%100 != 0) || (year%400==0)){
			kk=29
		}else{
			kk=28;
		}
		var mon_len=new Array(31,kk,31,30,31,30,31,31,30,31,30,31);

		document.write("<table cellpadding='0' cellspacing='0' border='0' bgcolor='#FFFFFF' width='200' align='center'><tr><td align='center'>");
		document.write("<table cellpadding='1' cellspacing='1' border='0' width='200' align='center'>");
		document.write("<tr bgcolor='#FFFFFF'><td>");
		document.write("<table cellpadding='0' cellspacing='0' border='0' bgcolor='#000000'><tr><td>");
		document.write("<table cellpadding='2' cellspacing='1' border='0'>");
		document.write("<tr height='26' bgcolor='#EFF3F4'>");
		document.write("<td colspan='7' align='center'>");

		document.write("<table cellpadding=0 cellspacing=0 border=0 width='200'><tr>");
		document.write("<td align=center width='10%'>");
		document.write("<a href=javascript:changeYearHandler('-1')>☜</a></td>");
		document.write("<td align=center width='10%'>");
		document.write("<a href=javascript:changeMonthHandler('-1')>◀</td>");
		document.write("<td align='center'><font style='font-size:12pt; font-weight:bold; color:navy'>");
		document.write("<a href=javascript:changeMode('perYear',"+year+")>"+year+"<%=yearString%> </a>"+monthArry[month]);
		document.write("</font></td>");
		document.write("<td align='right' width='10%'>");
		document.write("<a href=javascript:changeMonthHandler('1')>▶</a></td>");
		document.write("<td align=right width='10%'>");
		document.write("<a href=javascript:changeYearHandler('1')>☞</a></td>");
		document.write("</tr></table>");

		document.write("</td></tr>");
		document.write("<tr height='26' bgcolor='#EFF3F4'>");


		var weekDay = new Array("<%=w1%>", "<%=w2%>", "<%=w3%>", "<%=w4%>", "<%=w5%>", "<%=w6%>", "<%=w7%>");

		for (var dayNum = 0; dayNum < 7; ++dayNum) {
			document.write("<td valign='middle' align='center'>");
			if(dayNum == 0)
				document.write("<font style='font-size:9pt; color:#FF0000; font-weight:bold'>");
			else
				document.write("<font style='font-size:9pt; color:#C3467D; font-weight:bold'>");

			document.write(weekDay[dayNum]);
			document.write("</font></td>");
		}
		document.write("</tr>");

		////////////////// 실제 달력 스트링을 얻는 곳 ///////////////////
		if(firstDay != 0) document.write("<tr height='26' bgcolor='#FFFFFF'>");

		var col=0;
		for(i=0;i<firstDay;i++){
			document.write("<td bgcolor='#EFF3F4'>&nbsp</td>");
			col++;
		}
		//Font Color & change size when is focused
		var cell_cnt=col;
		for(j=1;j<mon_len[month-1]+1;j++){
			if(col % 7==0){
				document.write("<tr>");
			}
			if(to_year == year && to_month == month && date == j) {
				cell_cnt++;
				document.write("<td width='26' height='26' valign='middle' align='right' bgcolor='EFF3F4' style='cursor:hand; font-size:9pt; color:#4271ad; font-weight:bold' onClick='chng("+j+")' onMouseOver='tdOverHandler(this)' onMouseOut='tdOutHandler(this)'><span style=color:purple>" + j + "</span></td>");
			} else if(col % 7==0){
				cell_cnt++;
				document.write("<td width='26' height='26' valign='middle' align='right' bgcolor='EFF3F4' style='cursor:hand; font-size:9pt; color:#4271ad; font-weight:bold' onClick='chng("+j+")' onMouseOver='tdOverHandler(this)' onMouseOut='tdOutHandler(this)'><span style=color:red>" + j + "</span></td>");
			} else{
				cell_cnt++;
				document.write("<td width='26' height='26' valign='middle' align='right' bgcolor='EFF3F4' style='cursor:hand; font-size:9pt; color:#4271ad; font-weight:bold' onClick='chng("+j+")' onClick='chng(this)' onMouseOver='tdOverHandler(this)' onMouseOut='tdOutHandler(this)'>" + j + "</td>");
			}
			col++;
			if(cell_cnt == 7) cell_cnt = 0;
		}
		for(i=0; cell_cnt != 0 && i<7-cell_cnt; i++) {
			document.write("<td bgcolor='#EFF3F4'>&nbsp</td>");
		}
		cell_cnt = 0;
		document.write("</table></td></tr></table>");
		document.write("</td></tr></table></td></tr></table>");
		document.write("</td></tr></table>");
	}
////////////////////////////////////////////////////////////////
	function showYCalendar()
	{




		//////////////
		var now = new Date();
		console.log(now);
//		var date=new Date();
//		var tempYear = date.getYear();
//		var now=new Date(tempYear, 0, 1);
// 		var year=now.getFullYear();
		var year=now.getFullYear();
		console.log(year);
		to_year = year;
		to_month = now.getMonth()+1;

		if(location.search){
			var arr = location.search.split("&");
			var year = "";
			var month = "";
			for ( var i = 0 ; i < arr.length ; i++ ) {
				var paramStr = arr[i];
				var paramArr = paramStr.split("=");
				if ( paramArr.length == 2 ) {
					var key = paramArr[0];
					var value = paramArr[1];

					if ( key == "year" || key == "?year" ) year = value;
					if ( key == "month" || key == "?month" ) month = value;
				}
			}
			if ( year == "" ) year=now.getFullYear();
			if ( month == "" ) month=now.getMonth() + 1;
		}
		else{
			var year=now.getFullYear();
			var month=now.getMonth() + 1;
		}
		
		var dateArray = getDateArray();
		if(dateArray != "")
		{
			year = dateArray[0];
			month = dateArray[1];
		}

		global_year = year;
		global_month = month;

		var date=now.getDate();
		var day=now.getDay();

		if((year%4 == 0 && year%100 != 0) || (year%400==0)){
			kk=29
		}else{
			kk=28;
		}
		var mon_len=new Array(31,kk,31,30,31,30,31,31,30,31,30,31);
		
		var weekDay = new Array("<%=w1%>", "<%=w2%>", "<%=w3%>", "<%=w4%>", "<%=w5%>", "<%=w6%>", "<%=w7%>");
		
		var col=0;
		var dal='';
		document.write("<table cellpadding='0' cellspacing='1' style='border:1px solid black:background-color:#EFF3F4' width='820' >");//전체 table
		document.write("<tr><td align='center' width='820' height='50' bgcolor='#EFF3F4'><a href=javascript:changeYearHandler('-1') onFocus='this.blur' style='cursor:hand'><font style='font-size:21pt; font-weight:bold'>◀</font></a><a href=javascript:changeMode('perMonth',"+year+")><font style='font-size:21pt; font-weight:bold'>"+year+"<%=yearString%></font></a><a href=javascript:changeYearHandler('1') onFocus='this.blur' style='cursor:hand'><font style='font-size:21pt; font-weight:bold'>▶</font></a></td></tr>");
		for(var i= 0; i<3;i++)
		{
			document.write("<tr><td align='center' width='800' height='200'><table align='center' cellpadding='1' cellspacing='0' style='border:1px solid gray' bgcolor='#EFF3F4'>");//tr table
			document.write("<tr>");
			for(var j = 0 ; j<4 ; j++)
			{
				dal = ((i*4)+j+1);
				var firstDate = new Date(year, dal-1, 1);
				var firstDay = firstDate.getDay();
				document.write("<td align='center' valign='top'><table cellpadding='0' cellspacing='0' border='1' width='200' bgcolor='#EFF3F4'>");
				document.write("<tr><td width='200' align='center' height='30' colspan='7' bgcolor='#EFF3F4'><a href=javascript:changeToMonth('perMonth',"+year+","+dal+") onFocus='this.blur' style='cursor:hand'><font style='font-size:12pt; font-weight:bold; color:navy'>"+monthArry[dal]+"</font></td></tr>");//매월 title
				for (var dayNum = 0; dayNum < 7; ++dayNum) {
					document.write("<td valign='middle' align='center' height='26' bgcolor='#EFF3F4'>");
					if(dayNum == 0)
						document.write("<font style='font-size:9pt; color:#FF0000; font-weight:bold'>");
					else
						document.write("<font style='font-size:9pt; color:#C3467D; font-weight:bold'>");

					document.write(weekDay[dayNum]);
					document.write("</font></td>");
				}
				document.write("</tr>");
				if(firstDay != 0) document.write("<tr height='26' bgcolor='#FFFFFF'>");
				col=0;
				for(c=0;c<firstDay;c++){
					document.write("<td bgcolor='#EFF3F4'>&nbsp</td>");
					col++;
				}
				//Font Color & change size when is focused
				var cell_cnt=col;
				for(k=1;k<mon_len[dal-1]+1;k++){
					if(col % 7==0){
						document.write("<tr>");
					}
					if(to_year == year && to_month == dal && date == k) {
						cell_cnt++;
						document.write("<td width='26' height='26' valign='middle' align='right' bgcolor='EFF3F4' style='cursor:hand; font-size:9pt; color:#4271ad; font-weight:bold' onClick='chang("+k+","+dal+")' onMouseOver='tdOverHandler(this)' onMouseOut='tdOutHandler(this)'><span style=color:purple>" + k + "</span></td>");
					} else if(col % 7==0){
						cell_cnt++;
						document.write("<td width='26' height='26' valign='middle' align='right' bgcolor='EFF3F4' style='cursor:hand; font-size:9pt; color:#4271ad; font-weight:bold' onClick='chang("+k+","+dal+")' onMouseOver='tdOverHandler(this)' onMouseOut='tdOutHandler(this)'><span style=color:red>" + k + "</span></td>");
					} else{
						cell_cnt++;
						document.write("<td width='26' height='26' valign='middle' align='right' bgcolor='EFF3F4' style='cursor:hand; font-size:9pt; color:#4271ad; font-weight:bold' onClick='chang("+k+","+dal+")' onClick='chng(this)' onMouseOver='tdOverHandler(this)' onMouseOut='tdOutHandler(this)'>" + k + "</td>");
					}
					col++;
					if(cell_cnt == 7) cell_cnt = 0;
				}//end for k
				for(k=0; cell_cnt != 0 && k<7-cell_cnt; k++) {
					document.write("<td bgcolor='#EFF3F4'>&nbsp</td>");
				}//end for k
				document.write("</tr></table></td>");
			}//end for j
			document.write("</tr></table></td></tr>");
		}//end for i
		document.write("</table>");
	}

	if(mode == 'perMonth')showMCalendar();
	else if(mode == 'perYear')showYCalendar();

//-->
</script>
<input type="hidden" name="MODE" value="<%=mode%>">
<input type="hidden" name="year">
<input type="hidden" name="month">
<input type="hidden" name="title" value="<%=title%>">
<input type="hidden" name="form" value="<%=formname%>">
<input type="hidden" name="obj" value="<%=objname%>">
<input type="hidden" name="function" value="<%=function%>">
<input type="hidden" name="chageObj" value="<%=chageObj%>">
</form>
</body>
</html>
