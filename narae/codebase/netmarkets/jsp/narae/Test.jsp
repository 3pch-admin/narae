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
int idx = query.appendClassList(WTPart.class, true);

ClassAttribute ca = new ClassAttribute(WTPart.class, WTPart.CREATE_TIMESTAMP);
OrderBy by = new OrderBy(ca, true);
query.appendOrderBy(by, new int[] { idx });

QueryResult result = PersistenceHelper.manager.find(query);
while (result.hasMoreElements()) {
	Object[] obj = (Object[]) result.nextElement();
	WTPart epm = (WTPart) obj[0];
	String number = epm.getNumber();
	int ext = number.lastIndexOf(".");

	if (ext > -1) {

		if (number.contains("EXSIDE_MIRROR_R") || number.contains("LH250000AN-2") || number.contains("P1")
		|| number.contains("NB-10-0600-00010") || number.contains("NB-14-1800-00048")
		|| number.contains("MONITOR-ARM") || number.contains("NB-01-0600-01011")
		|| number.contains("__tmp__name__") || number.contains("NB-01-0200-00093")
		|| number.contains("NB-28-0100-00005") || number.contains("QUP-08")
		|| number.contains("2202008-SC01-A01-F01-WC1-P04-A") || number.contains("NB-22-0400-00029")
		|| number.contains("140688-NB-02-0200-00150-0616_2D.DRW") || number.contains("NP-00-WESS-02719_2D")
		|| number.contains("NPT01-00-X4211") || number.contains("SILICON-75X75-1")
		|| number.contains("NEPES-75X75-1") || number.contains("2202008-SC01-A01-F01-WC1-P04-B_2D")
		|| number.contains("NPT01-00-X4211") || number.contains("DFH-NCS90-150-L1510---E3-09")
		|| number.contains("UP-06_170206") || number.contains("HOLDER003")
		|| number.contains("LGD_DF3030_015_DOOR-01") || number.contains("R-201203-57")) {
	continue;
		}
		// .prt
		String rNumber = number.substring(0, ext);
		
		QuerySpec spec = new QuerySpec();
		int i = spec.appendClassList(WTPart.class, true);
		SearchCondition ss = new SearchCondition(WTPart.class, WTPart.NUMBER, "=", rNumber);
		spec.appendWhere(ss, new int[] { i });

		QueryResult rr = PersistenceHelper.manager.find(spec);
		if (rr.size() > 0) {
			continue;
		}

		if (WorkInProgressHelper.isCheckedOut(epm)) {
	continue;
		}

		System.out.println("기존 번호 : " + number + ", 변경 번호 :  " + rNumber);

		WTPartMaster master = (WTPartMaster) epm.getMaster();
		WTPartMasterIdentity identity = (WTPartMasterIdentity) master.getIdentificationObject();
		identity.setNumber(rNumber);
		IdentityHelper.service.changeIdentity((Identified) master, identity);

	}
}
System.out.println("종료!");
%>