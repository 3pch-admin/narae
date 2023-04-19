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
	String number = epm.getNumber();
	int ext = number.lastIndexOf(".");

	if (number.length() > 30) {
		continue;
	}

	if (ext > -1) {
		if (number.contains("EXSIDE_MIRROR_R") || number.contains("LH250000AN-2") || number.contains("P1")
		|| number.contains("NB-10-0600-00010") || number.contains("NB-14-1800-00048")
		|| number.contains("MONITOR-ARM") || number.contains("NB-01-0600-01011")
		|| number.contains("__tmp__name__") || number.contains("NB-01-0200-00093")
		|| number.contains("NB-28-0100-00005") || number.contains("QUP-08")
		|| number.contains("2202008-SC01-A01-F01-WC1-P04-A") || number.contains("NB-22-0400-00029")
		|| number.contains("140688-NB-02-0200-00150-0616_2D.DRW") || number.contains("NP-00-WESS-02719")
		|| number.contains("NP-00-WESS-01228") || number.contains("NP-00-WESS-01227")
		|| number.contains("NP-00-WESS-01231") || number.contains("1NB-02-0200-0043")
		|| number.contains("NB-02-0100-01220") || number.contains("NP-00-WESS-02728")
		|| number.contains("NP-CM-0301-00073") || number.contains("NP-11-0224-00080")
		|| number.contains("SILICON-75X75-1") || number.contains("NEPES-75X75-1")
		|| number.contains("2202008-SC01-A01-F01-WC1-P04-B_2D") || number.contains("LGD_DF3030_015_DOOR-01")
		|| number.contains("FH-NCS90-150-L1510---E3-09") || number.contains("UP-06_170206")
		|| number.contains("HOLDER003") || number.contains("NPT01-00-X4211") || number.contains("R-201203-57")
		|| number.contains("JS-PIPE_2D") || number.contains("cp_2107001-nowpak-box-cover-3_2d.drw")
		|| number.contains("cp_2107001-nowpak-box-cover-5_2d.drw") || number.contains("NB-08-0400-002")
		|| number.contains("NB-08-0700-00022")) {
	continue;
		}
		// .prt

		String rNumber = number.substring(0, ext);
		String t = epm.getDocType().toString();

		if (!t.equals("CADDRAWING") && !t.equals("CADCOMPONENT") && !t.equals("CADASSEMBLY")) {
	continue;
		}

		if (t.equals("CADDRAWING")) {
	if (rNumber.lastIndexOf("_2D") <= -1) {
		rNumber = rNumber + "_2D";

		QuerySpec spec = new QuerySpec();
		int i = spec.appendClassList(EPMDocument.class, true);
		SearchCondition ss = new SearchCondition(EPMDocument.class, EPMDocument.NUMBER, "=", rNumber);
		spec.appendWhere(ss, new int[] { i });

		QueryResult rr = PersistenceHelper.manager.find(spec);
		if (rr.size() > 0) {
			continue;
		}
	}
		} else {
	QuerySpec spec = new QuerySpec();
	int i = spec.appendClassList(EPMDocument.class, true);
	SearchCondition ss = new SearchCondition(EPMDocument.class, EPMDocument.NUMBER, "=", rNumber);
	spec.appendWhere(ss, new int[] { i });

	QueryResult rr = PersistenceHelper.manager.find(spec);
	if (rr.size() > 0) {
		continue;
	}
		}
		 
		if(epm.getNumber().equals("ncwna-sc-cch170-001.asm")) {
			continue;
		}
		
		
		
		if (WorkInProgressHelper.isCheckedOut(epm)) {
	continue;
		}

		System.out.println("기존 번호 : " + number + ", 변경 번호 :  " + rNumber);

		EPMDocumentMaster master = (EPMDocumentMaster) epm.getMaster();
		EPMDocumentMasterIdentity identity = (EPMDocumentMasterIdentity) master.getIdentificationObject();
		identity.setNumber(rNumber);
		IdentityHelper.service.changeIdentity((Identified) master, identity);

		WTKeyedMap map = new WTKeyedHashMap();
		if (epm.getDocType().toString().equals("CADCOMPONENT")) {
	if (epm.getNumber().indexOf(".PRT") <= -1) {
		map.put(master, epm.getNumber().toLowerCase() + ".prt");
	} else {
		map.put(master, epm.getNumber().toLowerCase());
	}
		} else if (epm.getDocType().toString().equals("CADDRAWING")) {
	if (epm.getNumber().indexOf(".DRW") <= -1) {
		map.put(master, epm.getNumber().toLowerCase() + ".drw");
	} else {
		map.put(master, epm.getNumber().toLowerCase());
	}
		} else if (epm.getDocType().toString().equals("CADASSEMBLY")) {
	if (epm.getNumber().indexOf(".ASM") <= -1) {
		map.put(master, epm.getNumber().toLowerCase() + ".asm");
	} else {
		map.put(master, epm.getNumber().toLowerCase());
	}
		}
		//		epm = (EPMDocument) PersistenceHelper.manager.refresh(epm);
		EPMDocumentHelper.service.changeCADName(map);
	}
}
System.out.println("종료!");
%>