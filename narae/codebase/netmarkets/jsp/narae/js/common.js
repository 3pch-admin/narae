// <SCRIPT language=JavaScript src="/Windchill/portal/js/common.js"></SCRIPT>
 /**
 * @(#)	common.js
 * Copyright (c) e3ps. All rights reserverd
 * 
 * @author Seung-hwan Choi, skyprda@e3ps.com
 */
function disabledAllBtn()
{
	var f = document.forms[0];
    for(var i=0 ; i<f.length ; i++){
		if(f[i].type=="button")
			f[i].disabled = true;
	}
	f = document.getElementsByTagName('A');
	for(var i=0 ; i<f.length ; i++){
		f[i].disabled = true;
		//f[i].href = '#';
	}
}
 
function enabledAllBtn()
{
	var f = document.forms[0];
    for(var i=0 ; i<f.length ; i++){
		if(f[i].type=="button")
			f[i].disabled = false;
	}
	f = document.getElementsByTagName('A');
	for(var i=0 ; i<f.length ; i++){
		f[i].disabled = false;
		//f[i].href = '#';
	}
} 
 
 
function isNullData(str)
{
	if(str.length == 0)
		return true;
	for(var i=0;i<str.length;i++)
		if(str.charCodeAt(i) != 32)
			return false;
	return true;
}

function checkField(obj, fieldName)
{
	if(isNullData(obj.value))
	{
		alert( fieldName + unescape("%uC744%28%uB97C%29%20%uC785%uB825%uD558%uC138%uC694"));
		obj.focus();
		return true;
	}
	return false;
}

function checkFieldLength(obj, limit, fieldName)
{
	if(!isNullData(obj.value))
	{
		if(obj.value.length > limit)
		{
			alert( fieldName + " " +limit + unescape("%uC790 %uAE4C%uC9C0%20%uC785%uB825%uAC00%uB2A5%uD569%uB2C8%uB2E4"));
			obj.createTextRange();
			obj.select();
			return true;
		}
	}
	return false;
}

function openWindow(url, name, width, height) 
{ 
	getOpenWindow(url, name, width, height);
}

function openWindow2(url, name, width, height) 
{ 
	getOpenWindow2(url, name, width, height);
}

function getOpenWindow(url, name, width, height)
{
	var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
	if(width == 'full')
	{
//		rest = "width=" + (screen.availWidth-10) + ",height=" + (screen.availHeight-60)+',left=0,top=0';
		
		leftpos = (screen.availWidth - screen.availWidth *0.9 )/ 2; 
		toppos = (screen.availHeight - screen.availHeight *0.9 - 30 ) / 2 ; 

		rest = "width=" + (screen.availWidth * 0.9 ) + ",height=" + (screen.availHeight * 0.9 )+',left=' + leftpos + ',top=' + toppos;
	}
	else
	{
		leftpos = (screen.availWidth - width)/ 2; 
		toppos = (screen.availHeight - 60 - height) / 2 ; 

		rest = "width=" + width + ",height=" + height+',left=' + leftpos + ',top=' + toppos;
	}
	
	var newwin = open( url , name, opts+rest);
	newwin.focus();
	return newwin;
}

function getOpenWindow2(url, name, width, height)
{
	var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=0,resizable=0,";
	if(width == 'full')
	{
//		rest = "width=" + (screen.availWidth-10) + ",height=" + (screen.availHeight-60)+',left=0,top=0';
		
		leftpos = (screen.availWidth - screen.availWidth *0.9 )/ 2; 
		toppos = (screen.availHeight - screen.availHeight *0.9 - 30 ) / 2 ; 

		rest = "width=" + (screen.availWidth * 0.9 ) + ",height=" + (screen.availHeight * 0.9 )+',left=' + leftpos + ',top=' + toppos;
	}
	else
	{
		leftpos = (screen.availWidth - width)/ 2; 
		toppos = (screen.availHeight - 60 - height) / 2 ; 

		rest = "width=" + width + ",height=" + height+',left=' + leftpos + ',top=' + toppos;
	}
	
	var newwin = open( url , name, opts+rest);
	newwin.focus();
	return newwin;
}


function reSubmit(){
	 document.forms[0].submit();
}

function oneClick(check)
{
	if ( !check.checked ) return;
	var chk=document.forms[0];
	str = check.value;
	for ( var i = 0 ; i < chk.length ; i++ ) {
		if ( chk[i].type == "checkbox" ) 
			if ( str != chk[i].value ) 
				chk[i].checked=false;
	}
}

function getSelect()
{
    var chk=document.forms[0];
    var str=""; 
    for(var i=0;i<chk.length;i++){
		if(chk[i].type=="checkbox"){
			if(chk[i].checked==true){
			   str = chk[i].value;
			   break;
			}
		}
	}
	return str;
 }

function closeWindow()
{
	if( opener != null ) self.close();
	else history.back();
}

function isNotNumData(str) {
	if(str.length == 0)
		return true;
	for(var i=0;i<str.length;i++){
		var sss = str.charCodeAt(i);
		if(48 > sss || 57 < sss  ){
			return true;
		}
	}
	return false;
}

function parseInt2(str){
	var result = 0;
	var level = 1;
	for(var i=0; i<str.length-1; i++)
		level *= 10;
	for(var i=0;i<str.length;i++){
		var sss = str.charCodeAt(i)-48;
		result += (level * sss);
		level = level / 10;
	}
	return result;
}

var screenWidth = screen.availWidth/2-150;
var screenHeight = screen.availHeight/2-75;

function openSameName(url,width,height,state){
	var opt = launchCenter(width,height);
	if ( state.length > 0 ) opt = opt + ", " + state
	var windowWin = window.open(url,"newwindow",opt);
	windowWin.resizeTo(width,height);
	windowWin.focus();
}

function openOtherName(url,name,width,height,state){
	var opt = launchCenter(width,height);
	if ( state.length > 0 ) opt = opt + ", " + state
	var windowWin = window.open(url,name,opt);
	windowWin.resizeTo(width,height);
	windowWin.focus();
}

function launchCenter(width,height) {
  var str = "height=" + height + ",innerHeight=" + height;
  str += ",width=" + width + ",innerWidth=" + width;
  if (window.screen) {
    var ah = screen.availHeight - 30;
    var aw = screen.availWidth - 10;

    var xc = (aw - width) / 2;
    var yc = (ah - height) / 2;

    str += ",left=" + xc + ",screenX=" + xc;
    str += ",top=" + yc + ",screenY=" + yc;
  }
  return str;
}
function addBgColorEvent()
{
	var f = document.forms[0];
	for (var i=f.length-1 ; i>-1 ; i-- )
	{
		f[i].onfocus = changeColor1;
		f[i].onblur = changeColor2;
	}
}
function changeColor1()
{
	event.srcElement.style.backgroundColor='#efefef';
}
function changeColor2()
{
	event.srcElement.style.backgroundColor='#ffffff';
}

function printTitle(_title)
{
	document.write("<table border=0 cellpadding=0 cellspacing=0 >");
	document.write("<tr>");
	document.write("<td><img src=/plm/jsp/portal/images/title2_left.gif></td>");
	document.write("<td background=/plm/jsp/portal/images/title_back.gif>");
	document.write(_title);
	document.write("</td>");
	document.write("<td><img src=/plm/jsp/portal/images/title2_right.gif></td>");
	document.write("</tr>");
	document.write("</table>");
}

// get length of String
function getBytes(_value)
{
	var count = 0;
	var tmpStr = new String(_value);
	
	var onechar;
	for (var i=tmpStr.length-1 ; i>-1 ; i-- )
	{
    	onechar = tmpStr.charAt(i);
    	if (escape(onechar).length > 4) count += 3;
	    else count += 1;
  	}
	return count;
}

function selectOptionTrue(obj, val)
{
	if(obj == null || val == null || val.length==0) return;
	
	for(var i=obj.length-1 ; i>-1 ; i--)
	{
		if(obj[i].value == val) 
		{
			obj[i].selected = true;
			break;
		}
	}
}

function selectCheckTrue(obj, val)
{
	if(obj == null || val == null || val.length==0) return;
	
	if(obj.length == null)
	{
		obj.checked = true;
	}
	else
	{
		for(var i=obj.length-1 ; i>-1 ; i--)
		{
			if(obj[i].value == val) 
			{
				obj[i].checked = true;
				break;
			}
		}
	}
}

function COMMON_openPopup(nw,nh) {
	var opt,args = COMMON_openPopup.arguments;

	opt = "width="+nw+",height="+nh+",scrollbars=yes,resizable=yes"+
		( (args.length>2)?",left="+args[2]:"")+
		( (args.length>3)?"top="+args[3]:"");
	return window.open("about:blank","",opt);
}

function common_CheckStrLength(obj, maxmsglen){

	var temp;
	var f = obj.value.length;
	var msglen = maxmsglen; //�ִ� ����
	var tmpstr = "";
	var strlen;

// �ʱ� �ִ���̸� �ؽ�Ʈ �ڽ��� �ѷ��ش�.
  for(k=0;k<f;k++){
	  temp = obj.value.charAt(k);
	  if(escape(temp).length > 4)
		  msglen -= 1;
	  else
		  msglen--;
	  if(msglen < 0){
		  alert("�� ���� / �ѱ� "+maxmsglen+"�� ���� ���� �� �ֽ��ϴ�.");
		  obj.value = tmpstr;
		  break;
	  }
	  else{
		  tmpstr += temp;
	  }
  }
}

/*=============================================================================* 
 * �Է°��� ���������� Ȯ���Ѵ�. 
 * param : obj �Է���
 * style='IME-MODE: disabled' : �Է�â�� �ѱ��Է¹���
 *============================================================================*/
function SetNum(obj){
	val=obj.value;
	re=/[^0-9]/gi;
	obj.value=val.replace(re,"");
}

/**
*
*/
function viewProcessHistoryInfo(oid){
	openWindow("/plm/jsp/groupware/workprocess/ProcessHistoryInfo.jsp?oid="+oid+"&isPopup=true", "ProcessHistoryInfo", 845, 245);
}

function setButtonTag(_name, _width, _script, _class, _color)
{
	var sb = "";
    var rwidth = _name.length * 8;
    if (rwidth > _width) _width = rwidth;
    
    if(_color==null)_color = "4E4E4E";

    sb = "<a style='FONT-SIZE: 8pt;' onclick=\"" + _script + "\" style='cursor:hand;color="+_color+"' onMouseOver=\"all._text.style.color='#0393c8'\" onMouseOut=\"all._text.style.color='"+_color+"'\"><table width='"+_width+"' border='0' cellspacing='0' cellpadding='0' class='"+_class+"'>";
    sb += "<tr>";
    sb += "<td width='7'><img src='/Windchill/netmarkets/jsp/narae/portal/img/btn_left.gif' alt='' width='7' height='20'></td>";
    sb += "<td valign='middle' background='/Windchill/netmarkets/jsp/narae/portal/img/btn_mid.gif'>";
    sb += "<table align='center' border='0' cellspacing='0' cellpadding='0'>";
    sb += "<tr>";
    sb += "<td><div id='_text' align='center'>" + _name + "</div></td>";
    sb += "</tr>";
    sb += "</table>";
    sb += "</td>";
    sb += "<td width='12'><img src='/Windchill/netmarkets/jsp/narae/portal/img/btn_right.gif' alt='' width='12' height='20'></td>";
    sb += "</tr>";
    sb += "</table></a>";
    document.write(sb);
}

function setButtonTagForScript(_name, _width, _script, _class)
{
    var sb = "";
    var rwidth = _name.length * 8;
    if (rwidth > _width) _width = rwidth;

    sb = "<a style='FONT-SIZE: 8pt;' onclick=\"" + _script + "\" style='cursor:hand;' onMouseOver=\"all._text.style.color='#0393c8'\" onMouseOut=\"all._text.style.color='#4E4E4E'\"><table width='"+_width+"' border='0' cellspacing='0' cellpadding='0' class='"+_class+"'>";
    sb += "<tr>";
    sb += "<td width='7'><img src='/plm/jsp/portal/img/btn_left.gif' alt='' width='7' height='20'></td>";
    sb += "<td valign='middle' background='/plm/jsp/portal/img/btn_mid.gif'>";
    sb += "<table align='center' border='0' cellspacing='0' cellpadding='0'>";
    sb += "<tr>";
    sb += "<td><div id='_text' align='center'>" + _name + "</div></td>";
    sb += "</tr>";
    sb += "</table>";
    sb += "</td>";
    sb += "<td width='12'><img src='/plm/jsp/portal/img/btn_right.gif' alt='' width='12' height='20'></td>";
    sb += "</tr>";
    sb += "</table></a>";
    return sb;
}

function setButtonTag3D(_name, _width, _script, _class)
{
    var sb = "";
    var rwidth = _name.length * 8;
    if (rwidth > _width) _width = rwidth;
    
    sb = "<a onclick=\"" + _script + "\" style='cursor:hand;''\"><table width='"+_width+"' border='0' cellspacing='0' cellpadding='0' class='"+_class+"'>";
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

function SWStart(UserID, SWnUrl, drmApplyCheck, filename) {
	if (drmApplyCheck = true){
		var authkey = "ELCT-EYOE-TUIT-QDOQ";

		/***** 1. SWorkGetStateOfSWInstall *****/
		// -- S-WORK ��ġ ���� üũ
		// -- 0: ��ġ/���� ����, -1:���۾���, -2:��ġ �ȵ�.
		var ret = SWELCtrl.SWorkGetStateOfSWInstall();
		if (ret == 0){
			/***** 2. SWorkGetDetailStatus *****/
	 	    // -- 0:�α����ϼ���, 1:�α����ϼ���, 2:��� �� �ٽ� �α����ϼ���, 3:����
		    // -- ������ : ��� �� �ٽ� �α��� �ϼ���.
			var ret1 = SWELCtrl.SWorkGetDetailStatus(authkey);

			if (ret1 == 0 || ret1 == 1){
				//alert("�α����ϼ���");
				DSXRATC1.RequestS6 ("DSG_START_LOGIN",UserID,"","",1,1);
			}else if (ret1 == 2){
				alert("�α��� ���Դϴ�. ��� �� �ٽ� �α����ϼ���");
			}else if (ret1 == 3){
				/***** 3. SWUserInfo *****/
				// -- ����� ������ Ȯ�� �� ������ ��ġ�ϸ� �����ܰ��
				var ret2 = SWELCtrl.SWorkGetUserInfo(authkey);
				var SWid = SWELCtrl.UserID;

				if (SWid == UserID){  // - S-Work ��ġ �� �Է��� ����� ID �� PLM �α��� ID�� ���Ͽ��� �Ǵ�.
				    /***** 4. SWorkGetDriveLetterString *****/
					// -- ���� ����̺� ���� Ȯ��(Z:)
					// ????? ���� ����̺� ������ Ȯ���� �ȵǾ��� ��� ����? pass??
					var ret3 = SWELCtrl.SWorkGetDriveLetterString(authkey);
					var drive = SWELCtrl.OutBuf;

					/***** 5. SWorkSetProcessEx *****/
					// -- IE ���� ����
					var ret4 = SWELCtrl.SWorkSetProcessEx(authkey, 2, 1);
					if (ret4 == "0"){
						if (SWnUrl != "") DSDown11.Down3("http://"+document.location.host+"/"+SWnUrl, filename, true, false);
					}else{
						alert ("IE ���� ��� ���� �Ͽ����ϴ�.");
					}
				/***** 3. SWUserInfo *****/
				// 3. ����� ������ ��ġ���� ����.
				}else{				
					alert ("����� ������ ��ġ���� �ʽ��ϴ�. \n ����� ���� Ȯ�� �� �ٽ� �õ��Ͻñ� �ٶ�ϴ�.");
				}
			/***** 2. SWorkGetDetailStatus *****/
			}else if (ret1 == 111){	
				alert("Ű ������ �����Ͽ����ϴ�. ����ڿ��� Ȯ���ϼ���.");
			}else if (ret1 == 4){
	         alert("���� �������� �α��� ���� �Դϴ�.");
			}else if (ret1 == 7){
	         alert("���� �α׾ƿ� ������� �Դϴ�.");
			}
		/***** 1. SWorkGetStateOfSWInstall *****/
		}else if (ret == -1){
			alert("S-Work�� �α��� �մϴ�.");
			DSXRATC1.RequestS6 ("DSG_START_LOGIN",UserID,"","",1,1);
		}else{
	     alert("S-Work�� ��ġ���� �ʾҽ��ϴ�. \n ��ġ �� �ٽ� �̿��Ͻñ� �ٶ�ϴ�.");
	     alert("S-Work�� ��ġ ���� �ʾҽ��ϴ�. \n S-Work �ٿ�ε� �������� �̵��մϴ�.");
		 var url = "http://150.150.103.25/download_Inside.html";
		 var win = window.open(url , '', 'width=800, height=600, top=150,left=300, scrollbars=yes, resizable=yes, location=no');
		 win.focus();  // ���� �������� ��Ŀ���� �ش�. ������ÿ� ��������
		}
	}else{
		if (SWnUrl != "") DSDown11.Down3("http://"+document.location.host+"/"+SWnUrl, filename, true, false);
	}
}

function SWCall(downUrl){
  var dUrl = downUrl;
  document.SWCallForm.action = dUrl;
  document.SWCallForm.submit();
}

function SWRelease() {
	var authkey = "ELCT-EYOE-TUIT-QDOQ";
	var statuscheck = SWELCtrl.SWorkGetDetailStatus(authkey);
	if (statuscheck == 3){
		var SWorkRelease = "";
		SWorkRelease = SWELCtrl.SWorkReleaseProcess(authkey, 2);
		//alert("IE ���� ���� ������ ���������� �̷�� �����ϴ�.");
	}
}