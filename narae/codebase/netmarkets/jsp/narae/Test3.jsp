<%@page import="wt.folder.Cabinet"%>
<%@page import="java.util.ArrayList"%>
<%@page import="wt.query.SearchCondition"%>
<%@page import="wt.part.WTPartMasterIdentity"%>
<%@page import="wt.part.WTPartMaster"%>
<%@page import="wt.part.WTPart"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="wt.fc.collections.WTKeyedMap"%>
<%@page import="wt.fc.IdentityHelper"%>
<%@page import="wt.epm.EPMDocumentMasterIdentity"%>
<%@page import="wt.epm.EPMDocumentMaster"%>
<%@page import="wt.fc.Identified"%>
<%@page import="wt.fc.collections.WTKeyedHashMap"%>
<%@page import="wt.vc.wip.WorkInProgressHelper"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="wt.epm.EPMDocument"%>
<%@page import="wt.query.OrderBy"%>
<%@page import="wt.query.ClassAttribute"%>
<%@page import="wt.query.QuerySpec"%>
<%@page import="wt.epm.EPMDocumentHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ArrayList list = new ArrayList();
QuerySpec query = new QuerySpec();
int idx = query.appendClassList(EPMDocument.class, true);

ClassAttribute ca = new ClassAttribute(EPMDocument.class, EPMDocument.CREATE_TIMESTAMP);
OrderBy by = new OrderBy(ca, true);
query.appendOrderBy(by, new int[] { idx });

QueryResult result = PersistenceHelper.manager.find(query);
System.out.println("result=" + result.size());
while (result.hasMoreElements()) {
	Object[] obj = (Object[]) result.nextElement();
	EPMDocument epm = (EPMDocument) obj[0];
	String cadName = epm.getCADName();
	// 	System.out.println(cadName);

	if (cadName.contains("_2d")) {
		int k = cadName.indexOf("_2d");
		if (epm.getDocType().toString().equals("CADDRAWING")) {
	String rr = cadName.substring(0, k); // NP-00-000
	String newCadName = rr.toLowerCase() + ".drw";

	if(epm.getNumber().equals("np-00-wess-10148.drw")) {
		continue;
	}
	if(epm.getNumber().equals("np-00-mass-10085.drw")) {
		continue;
	}
	if(epm.getNumber().equals("np-00-wess-10275.drw")) {
		continue;
	}
	
	
	
	if (WorkInProgressHelper.isCheckedOut(epm)) {
		continue;
	}
	
	QuerySpec spec = new QuerySpec();
	int jj = spec.appendClassList(EPMDocument.class, true);
	SearchCondition sc = new SearchCondition(EPMDocument.class, EPMDocument.CADNAME, "=", newCadName);
	spec.appendWhere(sc, new int[] { jj });
	QueryResult r = PersistenceHelper.manager.find(spec);
	if (r.hasMoreElements()) {
		Object[] o = (Object[]) r.nextElement();
		EPMDocument e = (EPMDocument) o[0];
		list.add(e);
		continue;
	}

	System.out.println("변경전 :  " + cadName + ", 변경후 : " + newCadName);

	EPMDocumentMaster master = (EPMDocumentMaster) epm.getMaster();
	WTKeyedMap map = new WTKeyedHashMap();
	map.put(master, newCadName);
	EPMDocumentHelper.service.changeCADName(map);
		}
	}
}
System.out.println("종료!");

for (int i = 0; i < list.size(); i++) {
	EPMDocument e = (EPMDocument) list.get(i);
	if (e.getNumber().equals("NP-00-SMST-06033.DRW") || e.getNumber().equals("NA-IP-SIH000-001.DRW")
	|| e.getNumber().equals("NP-00-PFAL-10003.DRW") || e.getNumber().equals("NP-00-WESS-10148.DRW")
	|| e.getNumber().equals("NP-00-WESS-10142.DRW") || e.getNumber().equals("NP-05-0302-00651.DRW")
	|| e.getNumber().equals("NP-02-2000-00519.DRW") || e.getNumber().equals("np-02-2000-00519.drw")
	|| e.getNumber().equals("NP-02-1700-00273.DRW") || e.getNumber().equals("NP-03-0100-01411.DRW")
	|| e.getNumber().equals("NP-02-0700-00809.DRW") || e.getNumber().equals("NP-02-2000-00520.DRW")
	|| e.getNumber().equals("NP-02-0900-00580.DRW") || e.getNumber().equals("NP-02-2000-00518.DRW")
	|| e.getNumber().equals("NP-02-0900-00585.DRW") || e.getNumber().equals("NP-02-0100-06327.DRW")) {
		continue;
	}
	PersistenceHelper.manager.delete(e);
	System.out.println(
	"= 작업공간.." + e.getPersistInfo().getObjectIdentifier().getStringValue() + ", number = " + e.getNumber());
}
System.out.println("===" + list.size());
System.out.println("종료@!");
%>