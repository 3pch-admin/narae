<%@page import="ext.narae.service.drawing.beans.DrawingHelper2"%>
<%@page import="wt.content.ContentRoleType"%>
<%@page import="wt.util.FileUtil"%>
<%@page import="wt.content.ApplicationData"%>
<%@page import="wt.content.ContentItem"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="wt.content.ContentHelper"%>
<%@page import="com.ptc.wvs.server.util.PublishUtils"%>
<%@page import="wt.representation.Representation"%>
<%@page contentType="text/html; charset=utf-8"%>
<%@ page
	import="wt.util.*, wt.org.*, wt.session.*, wt.inf.container.*, ext.narae.util.*, ext.narae.ui.*, java.util.*"%>
<%@ page import="ext.narae.service.part.*"%>
<%@ page import="ext.narae.service.drawing.*"%>
<%@ page import="wt.part.*"%>
<%@ page import="wt.epm.*"%>
<%@ page import="ext.narae.service.*"%>

<jsp:useBean id="wtcontext" class="wt.httpgw.WTContextBean"
	scope="request" />
<jsp:setProperty name="wtcontext" property="request"
	value="<%=request%>" />


<%
	String partList = request.getParameter("parentOid");

	String[] partListArray = partList.replace("$$$PTC$$$", "||").split("[|][|]");
	String name = null;
	String number = null;
	String version = null;
	String state = null;
	String pdf = "";
	String returnString = "";
	for (int index = 0; index < partListArray.length; index++) {
		WTPart part = (WTPart) CommonUtil2.getInstance(partListArray[index]);
		number = part.getNumber();
		name = part.getName();
		version = part.getVersionIdentifier().getValue();
		state = part.getState().getState().getDisplay(WTContext.getContext().getLocale());
		pdf = "";
		// 	EPMDocument modelEpm = DrawingHelper2.getEPMDocument(part);
		//     List<EPMDocument> drawingEpm = null;

		//     if( modelEpm != null ) {
		//     	EPMDocument one2D = DrawingHelper2.getRelational2DCad(modelEpm);
		EPMDocument one2D = DrawingHelper2.byPartNumber(part.getNumber().toUpperCase() + "_2D",
				part.getVersionIdentifier().getSeries().getValue());
		System.out.println("one2D=" + one2D);
		if (one2D != null) {
			pdf = DrawingHelper2.getPDFFile(one2D, number.toUpperCase());
			System.out.println(pdf);
			if (pdf.length() > 30) {
				int start = pdf.indexOf("/Windchill");
				System.out.println("start=" + start);
				int end = pdf.lastIndexOf("&role=SECONDARY");
				pdf = pdf.substring(start, end) + "&role=SECONDARY target=ContentFormatIconPopup";
				pdf = pdf.replaceAll("'", "");
				System.out.println("===========" + pdf);
			}
		}

		if (index == 0) {
			returnString = part.getPersistInfo().getObjectIdentifier().toString() + "$$$item$$$" + number
					+ "$$$item$$$" + name + "$$$item$$$" + version + "$$$item$$$" + state + "$$$item$$$" + pdf;
		} else {
			returnString = returnString + "$$$PTC$$$" + part.getPersistInfo().getObjectIdentifier().toString()
					+ "$$$item$$$" + number + "$$$item$$$" + name + "$$$item$$$" + version + "$$$item$$$"
					+ state + "$$$item$$$" + pdf;
		}
	}

	out.println(returnString);
%>
