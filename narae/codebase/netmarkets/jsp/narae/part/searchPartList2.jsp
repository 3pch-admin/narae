<%@page import="ext.narae.service.change.editor.EulPartHelper"%>
<%@page import="ext.narae.service.part.beans.BomBroker"%>
<%@page import="ext.narae.util.content.ContentUtil"%>
<%@page import="ext.narae.service.drawing.beans.DrawingHelper2"%>
<%@page import="ext.narae.util.CommonUtil"%>
<%@page import="ext.narae.service.part.beans.PartData"%>
<%@page import="ext.narae.service.part.beans.PartTreeData"%>
<%@page import="ext.narae.util.StringUtil"%>
<%@page import="ext.narae.util.WCUtil"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>

<%@page import="java.util.ArrayList"%>

<%@page import="wt.clients.folder.FolderTaskLogic"%>
<%@page import="wt.epm.EPMDocument"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="wt.folder.Folder"%>
<%@page import="wt.folder.FolderHelper"%>
<%@page import="wt.folder.IteratedFolderMemberLink"%>
<%@page
	import="wt.iba.definition.litedefinition.AttributeDefDefaultView"%>
<%@page import="wt.iba.definition.service.IBADefinitionHelper"%>
<%@page import="wt.iba.value.StringValue"%>
<%@page import="wt.lifecycle.State"%>
<%@page import="wt.org.WTUser"%>
<%@page import="wt.part.WTPart"%>
<%@page import="wt.query.ClassAttribute"%>
<%@page import="wt.query.OrderBy"%>
<%@page import="wt.query.QuerySpec"%>
<%@page import="wt.query.StringSearch"%>
<%@page import="wt.query.SearchCondition"%>
<%@page import="wt.vc.VersionControlHelper"%>
<%@page import="wt.vc.baseline.*"%>


<%@ page import="wt.util.*, wt.org.*, wt.session.*, wt.inf.container.*,  ext.narae.ui.*, java.util.*" %>
<%
String RESOURCE = "ext.narae.ui.common.resource.NaraeCommonRB";
Locale locale = WTContext.getContext().getLocale();
WTContainerRef containerRef = WCUtil.getWTContainerRefForDrawing();
WTContainerRef partContainerRef = WCUtil.getWTContainerRefForPart();
String ORG_749 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_749", new Object[]{}, locale);
String ORG_598 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_598", new Object[]{}, locale);
String ORG_326 = WTMessage.getLocalizedMessage(RESOURCE , "ORG_326", new Object[]{}, locale);
/*
out.println("ORG_749=" + ORG_749);
out.println("ORG_598=" + ORG_598);
out.println("ORG_326=" + ORG_326);
*/
%>


<%
	String location = "/Default";
	String oid = request.getParameter("oid");
	String menu = request.getParameter("menu");
	String module = request.getParameter("module");

	String moudleType = StringUtil.checkReplaceStr(request
			.getParameter("moudleType"), ""); //ecr //eco //epm //link 
	String mode = StringUtil.checkReplaceStr(request
			.getParameter("mode"), "mutil"); //single,mutil

	String foid = StringUtil.checkNull(request.getParameter("fid"));
	String name = "";

	ReferenceFactory rf = new ReferenceFactory();
	Folder folder = null;

	String number = StringUtil
			.checkNull(request.getParameter("number")).trim();
	//System.out.println(number);
	String nameValue = StringUtil.checkNull(
			request.getParameter("name")).trim();
	String creator = StringUtil.checkNull(
			request.getParameter("creator")).trim();
	String description = StringUtil.checkNull(
			request.getParameter("description")).trim();
	String predate = StringUtil.checkNull(
			request.getParameter("predate")).trim();
	String postdate = StringUtil.checkNull(
			request.getParameter("postdate")).trim();
	String selectProduct = StringUtil.checkNull(
			request.getParameter("product")).trim();
	String plmOnly = StringUtil.checkReplaceStr(request
			.getParameter("plmOnly"), "false");

	String baseline = request.getParameter("baseline");
	String desc = request.getParameter("desc");

	if (StringUtil.checkString(foid)) {
		folder = (Folder) rf.getReference(foid).getObject();
		location = FolderHelper.getFolderPath(folder);
		name = folder.getName();
	} else {
		folder = FolderTaskLogic.getFolder(location, WCUtil
				.getWTContainerRefForPart());
		foid = "";
	}

	String disabled = "";
	if (mode.equals("signle")) {
		disabled = "disabled";// = true";
	} else {
		disabled = "";//disabled = false";
	}

	Baseline bsobj = null;
	if (baseline != null && baseline.length() > 0) {
		bsobj = (Baseline) rf.getReference(baseline).getObject();
	}
%>


<link rel="stylesheet" href="/Windchill/netmarkets/jsp/narae/css/e3ps.css" type="text/css">
<link rel="stylesheet" href="/Windchill/netmarkets/jsp/narae/css/css.css" type="text/css">
<link rel="StyleSheet" href="/Windchill/netmarkets/jsp/narae/css/dtree.css" type="text/css" />

<script type="text/javascript" src="/Windchill/netmarkets/jsp/narae/js/common.js"></script>
<script type="text/javascript" src="/Windchill/netmarkets/jsp/narae/js/dtree.js"></script>
<script type="text/javascript">
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

	window.onerror = function(){
		return true;
	}
    function isCheckedCheckBox() {
        form = document.partListForm;
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
        form = document.partListForm;
		
        var arr = new Array();
        var subarr = new Array();
        if(!isCheckedCheckBox()) {
            return arr;
        }

        len = form.check.length;

        var idx = 0;
        if(len) {
            for(var i = 0; i < len; i++) {
                if(form.check[i].checked == true) {
                    arr[idx++] = form.check[i].value.split("†");
                }
            }
        } else {
            if(form.check.checked == true) {
                arr[idx++] = form.check.value.split("†");
            }
        }

        return arr;
    }

    function checkCbox(cbox)
    {
        if(cbox==null)
            len=0;
        else
        {
            len=cbox.length;
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
                    if ( cboxAll.checked && !cbox[i].disabled ) cbox[i].checked=true;
                    else    cbox[i].checked=false;
                }
            }else{
                if ( cboxAll.checked && !cbox.disabled) cbox.checked=true;
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
    
    function signleCheck(mode,arg){
    	var pForm = document.partListForm;
    	
    	var chkLen = pForm.check.length;
    	if(mode == "signle"){
			if(chkLen) {
				for(var i = 0; i < chkLen; i++) {
					
					if(pForm.check[i] != arg){
						pForm.check[i].checked = false;
					}					
					
				}
			}else{
				if(pForm.check != arg){
					pForm.check.checked = false;
				}
			}
		}    	
    }
    
    function viewBom(oid) {
        var str="/Windchill/netmarkets/jsp/narae/part/bom/PartTree.jsp?oid="+oid;
        var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
        leftpos = (screen.width - 1000)/ 2;
        toppos = (screen.height - 600) / 2 ;
        rest = "width=1000,height=600,left=" + leftpos + ',top=' + toppos;
        var newwin = window.open( str , "viewBOM", opts+rest);
        newwin.focus();
    }
</script>
<form name=partListForm method=post><input type="hidden"
	name="oid" value="<%=StringUtil.checkNull(oid)%>" /> <input
	type="hidden" name="menu" value="<%=menu%>" /> <input type="hidden"
	name="module" value="part" /> <input type="hidden" name="fid"
	value="<%=foid%>" />

<table width="100%" border="0" cellpadding="0" cellspacing="3">
	<tr align="center">
		<td valign="top" style="padding: 0px 0px 0px 0px">
		<table width="100%" border="0" cellpadding="1" cellspacing="1"
			bgcolor=#752e41 align="center">
			<tr>
				<td height=1 width=100%></td>
			</tr>
		</table>
		<table width="100%" border="0" cellpadding="0" cellspacing="0"
			align="center"
			style="table-layout: fixed; border-left-width: 1px; border-left-style: solid; border-left-color: #e6e6e6;">
			<tr>
				<td class="tdblueM" width="5%"><input type="checkbox"
					name="checkboxAll"
					onClick="selectAll(this, document.partListForm.check)"
					) <%=disabled%>></td>
				<td class="tdblueM" width="5%">No</td>
				<td class="tdblueM" width="5%">&nbsp;</td>
				<td class="tdblueM" width="17%">품목번호</td>
				<td class="tdblueM" width="24%">품목명</td>
				<td class="tdblueM" width="7%"><%=ORG_749%></td>
				<td class="tdblueM" width="10%">DWG</td>
				<td class="tdblueM" width="10%">PDF</td>
				<td class="tdblueM" width="8%"><%=ORG_598%></td>
				<td class="tdblueM" width="9%"><%=ORG_326%></td>
				
			</tr>
			<%
				if (number == "") {
			%>
			<tr>
				<td class="tdwhiteM0" colspan=11 width="100%">검색할 TOP품목을 입력해
				주세요.</td>
			</tr>

			<%
				} else {
					WTPart part = EulPartHelper.manager.getPart(number);

// 					System.out.println("@@@@@@@@@@@@@@@@@@@@@@@> number = " + number);
// 					System.out.println("@@@@@@@@@@@@@@@@@@@@@@@> part = " + part);
					QuerySpec qs = new QuerySpec();
					QueryResult qr = null;

// 					System.out.println("@@@@@@@@@@@@@@@@@@@@@@@> bsobj = " + bsobj);
					if (bsobj != null) {
						int ii = qs.addClassList(WTPart.class, true);
						int jj = qs.addClassList(BaselineMember.class, false);

						qs.appendWhere(new SearchCondition(BaselineMember.class,
								"roleBObjectRef.key.id", WTPart.class,
								"thePersistInfo.theObjectIdentifier.id"),
								new int[]{jj, ii});
						qs.appendAnd();
						qs.appendWhere(new SearchCondition(BaselineMember.class,
								"roleAObjectRef.key.id", "=", bsobj
										.getPersistInfo().getObjectIdentifier()
										.getId()), new int[]{jj});
						qs.appendAnd();
						qs.appendWhere(new SearchCondition(WTPart.class,
								"masterReference.key.id", "=", part.getMaster()
										.getPersistInfo().getObjectIdentifier()
										.getId()), new int[]{ii});
						//qs.appendOrderBy(new OrderBy(new ClassAttribute(WTPart.class,"thePersistInfo.createStamp"), true), new int[]{ii});
						
						
						qr = PersistenceHelper.manager.find(qs);
						//System.out.println(qs);
						
						System.out.println("@@@@@@@@@@@@@@@@@@@@@@@> qr = " + qr.size());

						if (qr.hasMoreElements()) {
							Object[] o = (Object[]) qr.nextElement();
							part = (WTPart) o[0];
						}
					}
					
					BomBroker broker = new BomBroker();
					
					if( broker != null && part != null) {
// 						System.out.println("~~~~~~~~~~>desc:" + desc);
// 						System.out.println("~~~~~~~~~~>!'false'.equals(desc):" + !"false".equals(desc));
// 						System.out.println("~~~~~~~~~~>bsobj:" + bsobj);
						PartTreeData root = broker.getTree(part, !"false".equals(desc),
								bsobj);
						ArrayList result = new ArrayList();
						broker.setHtmlForm(root, result);					
	
						int total = result.size() - 1;
						String[] dwg;
						String[] pdf;
						String dwgURL = "";
						String pdfURL = "";
						String d = "";
						String p = "";
						String epmOid = "";
						for (int i = 1; i < result.size(); i++) {
							PartTreeData data = (PartTreeData) result.get(i);
							PartData data2 = new PartData(part);
							WTPart parta = (WTPart)CommonUtil.getObject(data.oid);
						    EPMDocument epm = DrawingHelper2.getEPMDocument(parta);
						    if(epm != null){
						    	EPMDocument epm2d = DrawingHelper2.getRelational2DCad(epm);
						    	 if(epm2d != null){
						    		epmOid = epm2d.getPersistInfo().getObjectIdentifier().toString();
						     		dwg = ContentUtil.getDWG(epm2d);
						     		pdf = ContentUtil.getPDF(epm2d);
						     		dwgURL = dwg[5];
						     		pdfURL = pdf[5];
						    	 }
						    }
						   // System.out.println("d ::"+dwgURL+"/ p ::"+pdfURL);
						    if(null == dwgURL || "" == dwgURL){
						    	d = "X";
						    }else{
						    	d = "O";
						    }
						    if(null == pdfURL || "" == pdfURL){
						    	p = "X";
						    }else{
						    	p = "O";
						    }

							//if( data.isCheckout ) disabled = "disabled";
	
							String proudctOid = "";
							String plocation = "";
			%>
			<tr>
			
				<td class="tdwhiteM" align="center"><input type="checkbox"
					name="check" <%=disabled%>
					value="<%=epmOid%>†<%=data.number%>†<%=data.name%>†<%=data.version%>†<%=dwgURL%>†<%=pdfURL%>"
					onclick="signleCheck('<%=mode%>',this)"></td>
				<td class="tdwhiteM"><%=total--%></td>
				<td class="tdwhiteM"><%=data2.getIcon()%></td>
				<td class="tdwhiteL" title="<%=data.number%>">
				<div
					style="width: 250px; border: 0px; padding: 0px; margin: 0px; text-overflow: ellipsis; overflow: hidden;">
				<nobr><%=data.number%></nobr></div>
				</td>
				<td class="tdwhiteL" title="<%=data.name%>">
				<div
					style="width: 250px; border: 0px; padding: 0px; margin: 0px; text-overflow: ellipsis; overflow: hidden;">
				<nobr><a href="#"
					onclick="gotoView('<%=data.oid%>');return false;"><%=data.name%></a></nobr>
				</div>
				</td>
				<td class="tdwhiteM"><%=data.version%>.<%=part.getIterationIdentifier().getSeries()
							.getValue()%></td>
							<td class="tdwhiteM"><%=d%></td>
				<td class="tdwhiteM"><%=p%></td>
				<td class="tdwhiteM"><%=data.lifecycle%></td>
				<td class="tdwhiteM">
				<div
					style="width: 70px; border: 0px; padding: 0px; margin: 0px; text-overflow: ellipsis; overflow: hidden;"><nobr>
				<%=StringUtil
							.checkReplaceStr(data.creator, "&nbsp;")%> </nobr></div>
				</td>
				
				
			</tr>

			</td>
			</tr>
			<%
						}
					}
				}
			%>
		</table>
</form>

