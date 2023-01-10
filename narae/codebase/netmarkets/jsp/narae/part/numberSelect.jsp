<%@page import="ext.narae.util.WCUtil"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>

<%@page import="java.util.HashMap"%>
<%@page import="wt.doc.WTDocument,
						wt.epm.EPMDocument,
						wt.fc.Persistable,
						wt.fc.QueryResult,
						wt.part.WTPart"%>

<%@ page import="wt.util.*, wt.org.*, wt.session.*, wt.inf.container.*, ext.narae.ui.*, java.util.*" %>
<%
String RESOURCE = "ext.narae.ui.common.resource.NaraeCommonRB";
String UNITRB = "ext.narae.ui.common.resource.NareUnitRB";
Locale locale = WTContext.getContext().getLocale();
String M001 = WTMessage.getLocalizedMessage(RESOURCE , "M001", new Object[]{}, WTContext.getContext().getLocale());
WTContainerRef containerRef = WCUtil.getWTContainerRefForDrawing();
WTContainerRef partContainerRef = WCUtil.getWTContainerRefForPart();
String size = request.getParameter("firstColumnSize");
String nameType = request.getParameter("nameType");
if( nameType == null || nameType.trim().equals("") ) nameType= "drawing";
System.out.println("Size=" + size);

int sizeValue = 15;
int orgSizeValue = 15;
if( size == null || size.trim().length() == 0 ) { 
	sizeValue = 15;
} else {
	if( nameType.equals("sw") ) {
		orgSizeValue = Integer.valueOf(size).intValue();
		sizeValue = (orgSizeValue*100)/86;
		
		orgSizeValue = orgSizeValue -4;
	} else {
		orgSizeValue = Integer.valueOf(size).intValue();
		sizeValue = (orgSizeValue*100)/65;
	}
}
%>

<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center"
                                   style="border-left-width: 1px;border-left-style: solid;border-left-color: #e6e6e6;">
<tr>
	<td>
		<table width="100%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td width="65%">
					<table width="100%" border="0" cellpadding="0" cellspacing="0">
						<tr>
                            <td class="tdblueM" width="<%=sizeValue%>%">GROUP <span style="color:red;">*</span></td>
                            <td class="tdwhiteL" >
                            	<select onchange="numberSet('group1', null)" name="group1" id="group1">
									<%=CodeDispacher.getCadCreatorList()%>
								</select>
                            </td>
                        </tr>
                        <tr> 
                            <td class="tdblueM" width="<%=sizeValue%>%">TYPE <span style="color:red;">*</span></td>
                            <td class="tdwhiteL">
                               <select onchange="updateTypeUi('type','unit1')" name="type" id="type">
									<%=CodeDispacher.getType()%>
							   </select>   
                            </td>
                        </tr>
                        <tr>
                            <td class="tdblueM" width="<%=sizeValue%>%">UNIT <span style="color:red;">*</span></td>
                            <td class="tdwhiteL">
                                <select onchange="numberSet('unit1','class1')" name="unit1" id="unit1">
									<option value="" selected><%=WTMessage.getLocalizedMessage(RESOURCE , "SELECT", new Object[]{}, locale)%></option>
								</select>
								<select onchange="numberSet('unit2', 'class1')" name="unit2" id="unit2">
									<option value="" selected><%=WTMessage.getLocalizedMessage(RESOURCE , "SELECT", new Object[]{}, locale)%></option>
								</select>
                           </td>
                        </tr>
                        <tr>
                            <td class="tdblueM" width="<%=sizeValue%>%">CLASS <span style="color:red;">*</span></td>
                            <td class="tdwhiteL">
                               	<select onchange="numberSet('class1','class2')" name="class1" id="class1">
									<option value="" selected><%=WTMessage.getLocalizedMessage(RESOURCE , "SELECT", new Object[]{}, locale)%></option>
								</select>
								<select onchange="numberSet('class2','class3')" name="class2" id="class2">
									<option value="" selected><%=WTMessage.getLocalizedMessage(RESOURCE , "SELECT", new Object[]{}, locale)%></option>
								</select>
								<select onchange="numberSet('class3',null)" name="class3" id="class3">
									<option value="" selected><%=WTMessage.getLocalizedMessage(RESOURCE , "SELECT", new Object[]{}, locale)%></option>
								</select>
								<input class="txt_field" type="text" value="" size="5" name="class4" id="class4" onchange="class4Action()" onKeyPress="onlyNumber()" style="IME-MODE:disabled;" readOnly></input>
                            </td>
                        </tr>
					</table>
				</td>
				<td  class="tdwhiteL" style="background-color:#EEEEEE" align=center valign=middle>&nbsp;
					<span id="pdmNumber" style="width:35%; line-height:170%; font-size:25px; font-weight:bold"></span><br>
					<input type="hidden" name="number" id="number" value="">

				 <%-- <input type="text" class="txt_field" name="pdmNumber" size="80" style="width:250px; font-size:25; font-weight:bold" readonly="readonly" value="<%=pdmNumber%>" /> --%>
				 <%-- <input type="text" class="txt_field" name="pdmNumber" size="80" style="width:200px" readonly="readonly" value="<%=pdmNumber%>" /> --%>
				</td>
			</tr>
		</table>
		<table  width="100%" border="0" cellpadding="0" cellspacing="0">
			<tr>
			    <td class="tdblueM" width="<%=orgSizeValue%>%">
			    	<%if( nameType.equals("part") ) {%>
			    		<%=WTMessage.getLocalizedMessage(RESOURCE,"PART_NAME",new Object[]{},locale)%>
			    	<%} else if( nameType.equals("sw") ) {%>
			    		<%=WTMessage.getLocalizedMessage(RESOURCE,"SOFTWARE_NAME",new Object[]{},locale)%>
			    	<%} else { %>
			    		<%=WTMessage.getLocalizedMessage(RESOURCE,"DRAWING_NAME",new Object[]{},locale)%>
			    	<%} %> 
			    	<span style="color:red;">*</span>
			    </td>
			    <td class="tdwhiteL">
			       	<input class="txt_field" type="text" value="" size="26" name="name1" id='name1'></input>
					<input class="txt_field" type="text" value="" size="30" name="name2" id='name2'></input>
					<input type="hidden" name="name" id='name'></input>
			    </td>
			</tr>
		</table>
	</td>
</tr>

</table>                           

