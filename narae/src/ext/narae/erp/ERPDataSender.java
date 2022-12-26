package ext.narae.erp;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import ext.narae.schedule.E3PSScheduleJobs;
import ext.narae.service.drawing.beans.DrawingHelper2;
import ext.narae.service.iba.beans.AttributeService;
import ext.narae.service.part.beans.PartHelper;
import ext.narae.service.workflow.beans.WorkflowHelper2;
import ext.narae.util.db.DBConnectionManager;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeRequest2;
import wt.epm.EPMDocument;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.log4j.LogR;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.util.WTContext;
import wt.util.WTException;

public class ERPDataSender {
	private static final Logger log = LogR.getLoggerInternal(ERPDataSender.class.getName());
	Locale locale = null;
	String[] querys;
	HashMap<String, String> cols = new HashMap();
	HashMap<String, String> param = new HashMap();

	public ERPDataSender() {
		this.locale = WTContext.getContext().getLocale();
	}

	public boolean sendECO(WTObject obj) throws Exception {
		boolean result = true;

		WTChangeOrder2 eco = (WTChangeOrder2) obj;

		String _now = eco.getModifyTimestamp().toString().replace("-", "/");
		// 20200708
		this.cols.clear();
		String value = getIBAValue(AttributeService.getValue(eco, "Project"));
		String projectNo = value.substring(0, value.indexOf("-"));
		String projectSeqNo = value.substring(value.indexOf("-") + 1, value.indexOf("_"));
		this.cols.put("PrjNo", projectNo);
		// this.cols.put("PrjSeqNo", getIBAValue(AttributeService.getValue(eco,
		// "Project")).substring(8, 11));
		this.cols.put("PrjSeqNo", projectSeqNo);
		this.cols.put("EcoNo", eco.getNumber());
		QueryResult ecrs = ChangeHelper2.service.getChangeRequest(eco);
		if (ecrs.size() > 0) {
			WTChangeRequest2 ecr = (WTChangeRequest2) ecrs.getEnumeration().nextElement();
			this.cols.put("EcrNo", ecr.getNumber());
		} else {
			this.cols.put("EcrNo", "");
		}
		this.cols.put("Title", ERPInterface.HtmlEncode(eco.getName()));
		this.cols.put("Type", getIBAValue(AttributeService.getValue(eco, "EC_Reason")));
		this.cols.put("WorkDate", "");
		this.cols.put("State", eco.getState().getState().getDisplay(this.locale));
		this.cols.put("ConfirmDate", _now.substring(0, 10));
		this.cols.put("UnitCode", "");
		this.cols.put("Process", ecrs.size() > 0 ? "ECR_EXIST" : "ECR_NO");
		this.querys = QueryBuilder.getQueryString("PDMECO", this.cols, new String[] { "PrjNo", "PrjSeqNo", "EcoNo" },
				QueryType.INSERT_OR_UPDATE);
		if (!ExecuteQuery(this.querys))
			result = false;

		WTPart[] parts = ERPInterface.getPartList(eco);
		String drawno;
		for (int i = 0; (parts != null) && (i < parts.length); i++) {
			WTPart part = parts[i];

			if (!part.getNumber().toUpperCase().matches("^[A-Za-z]{2}-[A-Za-z0-9]{2}-.{10}$")) {
				System.out.println("=== [ERPDataSender:sendECO] Part 전송 시 채번 룰에 적합하지 않은 Part 목록 ===");
				System.out.println("part.getName() : " + part.getName());
				System.out.println("part.getNumber() : " + part.getNumber());
				System.out.println("part.getVersion() : " + part.getVersionDisplayIdentity());
				continue;
			}
			this.cols.clear();
			this.cols.put("Div", "ECO");
			this.cols.put("EONo", eco.getNumber());
			this.cols.put("ItemCode", part.getNumber().replaceAll("'", ""));
			this.cols.put("ItemVer", part.getVersionInfo().getIdentifier().getValue() + "."
					+ part.getIterationInfo().getIdentifier().getValue());
			this.querys = QueryBuilder.getQueryString("PDMEOITEM", this.cols,
					new String[] { "Div", "EONo", "ItemCode", "ItemVer" }, QueryType.INSERT_OR_UPDATE);
			if (!ExecuteQuery(this.querys))
				result = false;

			this.cols.clear();
			this.cols.put("PDMWorkTime", _now.replaceAll("/|-| |:|\\..", ""));
			this.cols.put("ItemCode", part.getNumber().replaceAll("'", ""));
			this.cols.put("ItemVer", part.getVersionInfo().getIdentifier().getValue() + "."
					+ part.getIterationInfo().getIdentifier().getValue());
			this.cols.put("ItemName", part.getName());
			this.cols.put("BaseUnit", "ea");
			System.out.println("Spec :: " + getIBAValue(AttributeService.getValue(part, "Spec")));
			System.out.println("Maker :: " + getIBAValue(AttributeService.getValue(part, "Maker")));
			System.out.println("Treatment :: " + getIBAValue(AttributeService.getValue(part, "Treatment")));
			System.out.println("material :: " + getIBAValue(AttributeService.getValue(part, "material")));
//      this.cols.put("ItemSpec", getIBAValue(AttributeService.getValue(part, "Spec") == null ? "" : getIBAValue(AttributeService.getValue(part, "Spec"))));
//      this.cols.put("Maker", getIBAValue(AttributeService.getValue(part, "Maker") == null ? "" : getIBAValue(AttributeService.getValue(part, "Maker"))));
//      this.cols.put("Treatment", getIBAValue(AttributeService.getValue(part, "Treatment") == null ? "" : getIBAValue(AttributeService.getValue(part, "Treatment"))));
//      this.cols.put("material", getIBAValue(AttributeService.getValue(part, "material") == null ? "" : getIBAValue(AttributeService.getValue(part, "material"))));
			this.cols.put("ApplyDate", _now.substring(0, 10));
			this.cols.put("ERPWorkTime", _now.substring(0, 10));
			this.cols.put("ERPWorkDiv", "");
			this.cols.put("ItemCode2", "");
			this.cols.put("ItemVer2", "");
			this.cols.put("EONo", eco.getNumber());

			EPMDocument epm3d = DrawingHelper2.getEPMDocument(parts[i]);
			EPMDocument epm2d = null;
			if (epm3d != null) {
				this.cols.put("ItemSpec", getIBAValue(AttributeService.getValue(epm3d, "Spec") == null ? ""
						: getIBAValue(AttributeService.getValue(epm3d, "Spec"))));
				this.cols.put("Maker", getIBAValue(AttributeService.getValue(epm3d, "Maker") == null ? ""
						: getIBAValue(AttributeService.getValue(epm3d, "Maker"))));
				this.cols.put("Treatment", getIBAValue(AttributeService.getValue(epm3d, "Treatment") == null ? ""
						: getIBAValue(AttributeService.getValue(epm3d, "Treatment"))));
				this.cols.put("material", getIBAValue(AttributeService.getValue(epm3d, "material") == null ? ""
						: getIBAValue(AttributeService.getValue(epm3d, "material"))));

			} else {
				this.cols.put("ItemSpec", getIBAValue(AttributeService.getValue(part, "Spec") == null ? ""
						: getIBAValue(AttributeService.getValue(part, "Spec"))));
				this.cols.put("Maker", getIBAValue(AttributeService.getValue(part, "Maker") == null ? ""
						: getIBAValue(AttributeService.getValue(part, "Maker"))));
				this.cols.put("Treatment", getIBAValue(AttributeService.getValue(part, "Treatment") == null ? ""
						: getIBAValue(AttributeService.getValue(part, "Treatment"))));
				this.cols.put("material", getIBAValue(AttributeService.getValue(part, "material") == null ? ""
						: getIBAValue(AttributeService.getValue(part, "material"))));
			}

			drawno = "";
			String drawver = "";
			String folder = "";
			System.out.println("=== [ERPDataSender:sendECO] Part 관련 3D/2D ===");
			if (epm3d == null) {
				System.out.println("==========> 도면이 존재하지 않습니다. : " + parts[i].getNumber());
			} else if ("PROE".equals(epm3d.getAuthoringApplication().toString())) {
				System.out.println(
						"epm3d.getAuthoringApplication().toString() = " + epm3d.getAuthoringApplication().toString());
				System.out.println("epm3d.getName() = " + epm3d.getName());
				System.out.println("epm3d.getCADName() = " + epm3d.getCADName());
				System.out.println("epm3d.getNumber() = " + epm3d.getNumber());
				System.out.println("epm3d.getVersionDisplayIdentity() = " + epm3d.getVersionDisplayIdentity());
				epm2d = DrawingHelper2.getRelational2DCad(epm3d);
				if (epm2d != null) {
					drawver = epm2d.getVersionInfo().getIdentifier().getValue() + "."
							+ epm2d.getIterationInfo().getIdentifier().getValue();
					folder = epm2d == null ? "" : ERPPdfSender.getTargetFolder(eco.getPersistInfo().getUpdateStamp());
					System.out.println("epm2d.getName() = " + epm2d.getName());
					System.out.println("epm2d.getNumber() = " + epm2d.getNumber());
					System.out.println("epm2d.getVersionDisplayIdentity() = " + epm2d.getVersionDisplayIdentity());
				}
				drawno = "";
			} else if ("ACAD".equals(epm3d.getAuthoringApplication().toString())) {
				drawno = epm3d.getCADName();
				drawver = "";
				folder = ERPPdfSender.getTargetFolder(eco.getPersistInfo().getUpdateStamp());

				System.out.println(
						"epm3d.getAuthoringApplication().toString() = " + epm3d.getAuthoringApplication().toString());
				System.out.println("epm3d.getName() = " + epm3d.getName());
				System.out.println("epm3d.getCADName() = " + epm3d.getCADName());
				System.out.println("epm3d.getNumber() = " + epm3d.getNumber());
				System.out.println("epm3d.getVersionDisplayIdentity() = " + epm3d.getVersionDisplayIdentity());
			}
			this.cols.put("DrawNo", drawno);
			this.cols.put("DrawVer", drawver);
			this.cols.put("Folder", folder);
			this.querys = QueryBuilder.getQueryString("PDM00", this.cols, new String[] { "ItemCode", "ItemVer" },
					QueryType.INSERT_OR_UPDATE);
			if (!ExecuteQuery(this.querys))
				result = false;
//      IBAUtil.changeIBAValue(part, "Spec", (String)this.cols.get("ItemSpec"));
//      IBAUtil.changeIBAValue(part, "Spec", (String)this.cols.get("Maker"));
//      IBAUtil.changeIBAValue(part, "Spec", (String)this.cols.get("Treatment"));
//      IBAUtil.changeIBAValue(part, "Spec", (String)this.cols.get("material"));
			/*
			 * this.param.clear(); this.param.put("@ItemCode",
			 * (String)this.cols.get("ItemCode")); this.param.put("@ItemVer",
			 * (String)this.cols.get("ItemVer")); this.param.put("@ItemName",
			 * (String)this.cols.get("ItemName")); this.param.put("@ItemSpecName",
			 * (String)this.cols.get("ItemSpec")); this.param.put("@Treatment",
			 * (String)this.cols.get("Treatment")); this.param.put("@material",
			 * (String)this.cols.get("material")); this.param.put("@BaseUnit",
			 * (String)this.cols.get("BaseUnit")); this.param.put("@ApplyDate",
			 * _now.substring(0, 10)); this.param.put("@Maker",
			 * (String)this.cols.get("Maker")); this.param.put("@DrawNo",
			 * (String)this.cols.get("DrawNo")); this.param.put("@DrawVer",
			 * (String)this.cols.get("DrawVer")); this.param.put("@Folder",
			 * (String)this.cols.get("Folder")); this.param.put("@UserID",
			 * (String)this.cols.get("9999999")); this.querys =
			 * QueryBuilder.getQueryString("[dbo].[CD0102]", this.param, new String[0],
			 * QueryType.PROCEDURE);
			 */

			this.param.clear();
			this.param.put("@ItemCode", (String) this.cols.get("ItemCode"));
			this.param.put("@ItemVer", (String) this.cols.get("ItemVer"));
			this.param.put("@ItemName", (String) this.cols.get("ItemName"));
			this.param.put("@ItemSpec", (String) this.cols.get("ItemSpec"));
			this.param.put("@treatment", (String) this.cols.get("Treatment"));
			this.param.put("@material", (String) this.cols.get("material"));
			this.param.put("@BaseUnit", (String) this.cols.get("BaseUnit"));
			this.param.put("@ApplyDate", _now.substring(0, 10));
			this.param.put("@Maker", (String) this.cols.get("Maker"));
			this.param.put("@DrawNo", (String) this.cols.get("DrawNo"));
			this.param.put("@DrawVer", (String) this.cols.get("DrawVer"));
			this.param.put("@Folder", (String) this.cols.get("Folder"));
			this.param.put("@UserID", (String) this.cols.get("9999999"));
			this.querys = QueryBuilder.getQueryString("[dbo].[usp_merge_bba010t]", this.param, new String[0],
					QueryType.PROCEDURE);

			if (ExecuteQuery(this.querys)) {
				WorkflowHelper2.changeState2(part, "SENT_ERP");
				System.out.println("===========> PART STATUS CHANGES : " + part.getNumber());
			} else {
				result = false;
				System.out.println("===========> ERP SEND ERROR : " + part.getNumber());
			}

		}

		for (int i = 0; (parts != null) && (i < parts.length); i++) {
			WTPart part = parts[i];

			if (!part.getNumber().toUpperCase().matches("^[A-Za-z]{2}-[A-Za-z0-9]{2}-.{10}$")) {
				System.out.println("=== [ERPDataSender:sendECO] BOM 전송 시 채번 룰에 적합하지 않은 Part 목록 ===");
				System.out.println("part.getName() : " + part.getName());
				System.out.println("part.getNumber() : " + part.getNumber());
				System.out.println("part.getVersion() : " + part.getVersionDisplayIdentity());
				continue;
			}
			HashMap<WTPart, Double> children = PartHelper.getChild(part);
			if ((children != null) && (children.size() > 0)) {
				for (Map.Entry entry : children.entrySet()) {
					WTPart child = (WTPart) entry.getKey();
					Double childCnt = (Double) entry.getValue();
					WTPartMaster master = child.getMaster();
					WTPartUsageLink link = PartHelper.getLinktoBOM(part, master);
					if ((!child.getNumber().toUpperCase().matches("^[A-Za-z]{2}-[A-Za-z0-9]{2}-.{10}$"))
							|| (part.getNumber().equals(child.getNumber()))) {
						System.out.println("=== [ERPDataSender:sendECO] BOM 전송 시 채번 룰에 적합하지 않은 Child 목록 ===");
						System.out.println("child.getName() : " + child.getName());
						System.out.println("child.getNumber() : " + child.getNumber());
						System.out.println("child.getVersion() : " + child.getVersionDisplayIdentity());
						continue;
					}
					this.cols.clear();
					this.cols.put("PDMWorkTime", _now.replaceAll("/|-| |:|\\..", ""));
					this.cols.put("ParentItemCode", part.getNumber().replaceAll("'", ""));
					this.cols.put("ParentItemVer", part.getVersionInfo().getIdentifier().getValue() + "."
							+ part.getIterationInfo().getIdentifier().getValue());
					this.cols.put("ItemCode", child.getNumber().replaceAll("'", ""));
					this.cols.put("ItemVer", child.getVersionInfo().getIdentifier().getValue() + "."
							+ child.getIterationInfo().getIdentifier().getValue());
					this.cols.put("MkuQty", Double.toString(childCnt.doubleValue()));
					double a = E3PSScheduleJobs.getFloatValue(child, "COST_PDM");
					String FCost = Double.toString(a);
					this.cols.put("FCost", FCost);
					this.cols.put("EoNo", eco.getNumber());
					this.querys = QueryBuilder.getQueryString("PDMBOM", this.cols,
							new String[] { "ParentItemCode", "ParentItemVer", "ItemCode", "ItemVer" },
							QueryType.INSERT_OR_UPDATE);
					if (!ExecuteQuery(this.querys))
						result = false;
					/*
					 * this.param.clear(); this.param.put("@ParentItemCode",
					 * (String)this.cols.get("ParentItemCode")); this.param.put("@ParentItemVer",
					 * (String)this.cols.get("ParentItemVer")); this.param.put("@CItemCode",
					 * (String)this.cols.get("ItemCode")); this.param.put("@MkuQty",
					 * (String)this.cols.get("MkuQty")); this.param.put("@FCost",
					 * (String)this.cols.get("FCost")); this.param.put("@UserID", "9999999");
					 * this.querys = QueryBuilder.getQueryString("[dbo].[CD0103]", this.param, new
					 * String[0], QueryType.PROCEDURE);
					 */
					this.param.clear();
					this.param.put("@pitemcode", (String) this.cols.get("ParentItemCode"));
					this.param.put("@pitemver", (String) this.cols.get("ParentItemVer"));
					this.param.put("@citemcode", (String) this.cols.get("ItemCode"));
					this.param.put("@unitqty", (String) this.cols.get("MkuQty"));
					this.param.put("@fcost", (String) this.cols.get("FCost"));
					this.param.put("@userid", "9999999");
					this.querys = QueryBuilder.getQueryString("[dbo].[usp_merge_bba020t]", this.param, new String[0],
							QueryType.PROCEDURE);

					if (ExecuteQuery(this.querys))
						continue;
					result = false;
				}

			}
		}
		QueryResult aResult = ChangeHelper2.service.getChangeActivities((WTChangeOrder2) obj);
		QueryResult resultTops = ChangeHelper2.service.getChangeablesBefore((WTChangeActivity2) aResult.nextElement());
		while (resultTops.hasMoreElements()) {
			WTPart part = (WTPart) resultTops.nextElement();
			this.param.clear();
			this.cols.put("ParentItemCode", part.getNumber().replaceAll("'", ""));
			this.cols.put("ParentItemVer", part.getVersionInfo().getIdentifier().getValue() + "."
					+ part.getIterationInfo().getIdentifier().getValue());
			this.param.put("@pitemcode", (String) this.cols.get("ParentItemCode"));
			this.param.put("@pitemver", (String) this.cols.get("ParentItemVer"));
			this.param.put("@UserID", "9999999");
			this.querys = QueryBuilder.getQueryString("[dbo].[usp_merge_bba020t_after]", this.param, new String[0],
					QueryType.PROCEDURE);
			if (ExecuteQuery(this.querys))
				continue;
			result = false;
		}

		System.out.println("newErp OBJ ::: " + obj.toString());
//    boolean newErpResult = sendECO3(obj);
		// System.out.println("newErp ::: "+newErpResult);
		return result;
	}

	public boolean sendECR(WTObject obj) throws IOException, SQLException, WTException {
		boolean result = true;

		WTChangeRequest2 ecr = (WTChangeRequest2) obj;

		String _now = ecr.getModifyTimestamp().toString().replace("-", "/");
		String value = getIBAValue(AttributeService.getValue(ecr, "Project"));
		String projectNo = value.substring(0, value.indexOf("-"));
		String projectSeqNo = value.substring(value.indexOf("-") + 1, value.indexOf("_"));
		this.cols.clear();
		this.cols.put("PrjNo", projectNo);
		// this.cols.put("PrjSeqNo", getIBAValue(AttributeService.getValue(eco,
		// "Project")).substring(8, 11));
		this.cols.put("PrjSeqNo", projectSeqNo);

		this.cols.put("UnitCode", "");
		this.cols.put("EcrNo", ecr.getNumber());
		this.cols.put("Title", ecr.getName());
		this.cols.put("Type", getIBAValue(AttributeService.getValue(ecr, "EC_Reason").toString()));
		this.cols.put("State", ecr.getState().getState().getDisplay(this.locale));
		this.cols.put("ConfirmDate", _now.substring(0, 10));
		this.cols.put("Worker", ecr.getIterationInfo().getCreator().getDisplayName());
		this.cols.put("WorkDate", ecr.getPersistInfo().getCreateStamp().toString().replace("-", "/").substring(0, 10));
		this.querys = QueryBuilder.getQueryString("PDMECR", this.cols, new String[] { "PrjNo", "PrjSeqNo", "EcrNo" },
				QueryType.INSERT_OR_UPDATE);
		if (!ExecuteQuery(this.querys))
			result = false;

		WTPart[] parts = ERPInterface.getPartList(ecr);
		for (int i = 0; (parts != null) && (i < parts.length); i++) {
			WTPart part = parts[i];
			System.out.println("=== [ERPDataSender:sendECR] ECR Part 목록 ===");
			System.out.println("part.getName() : " + part.getName());
			System.out.println("part.getNumber() : " + part.getNumber());
			System.out.println("part.getVersion() : " + part.getVersionDisplayIdentity());

			this.cols.clear();
			this.cols.put("Div", "ECR");
			this.cols.put("EONo", ecr.getNumber());
			this.cols.put("ItemCode", part.getNumber().replaceAll("'", ""));
			this.cols.put("ItemVer", part.getVersionInfo().getIdentifier().getValue() + "."
					+ part.getIterationInfo().getIdentifier().getValue());
			this.querys = QueryBuilder.getQueryString("PDMEOITEM", this.cols,
					new String[] { "Div", "EONo", "ItemCode", "ItemVer" }, QueryType.INSERT_OR_UPDATE);
			if (ExecuteQuery(this.querys))
				continue;
			result = false;
		}

		return result;
	}

	private boolean ExecuteQuery(String[] querys) {
		DBConnectionManager db = null;
		Connection con = null;
		Statement stmt = null;
		boolean result = true;
		try {
			db = DBConnectionManager.getInstance();
			con = db.getConnection("erp");
			stmt = con.createStatement();
			int q = 0;
			do {
				System.out.print("QUERY : " + querys[q]);
				try {
					stmt.execute(querys[q]);
					System.out.println(" ==> SUCCEED.");
				} catch (SQLException e) {
					System.out.println(" ==> FAILED.");
					System.out.println("여기 에러 발생해버리나?");
					result = false;
				}
				q++;
				if (querys == null)
					break;
			} while (q < querys.length);
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				stmt.close();
				con.close();
				db.freeConnection("erp", con);
			} catch (SQLException localSQLException1) {
				localSQLException1.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
			try {
				stmt.close();
				con.close();
				db.freeConnection("erp", con);
			} catch (SQLException localSQLException2) {
				localSQLException2.printStackTrace();
			}
		} finally {
			try {
				stmt.close();
				con.close();
				db.freeConnection("erp", con);
			} catch (SQLException localSQLException3) {
			}
		}
		return result;
	}

	private static String getIBAValue(Object obj) {
		if ((obj instanceof String))
			return obj.toString();
		if ((obj instanceof String[])) {
			return ((String[]) obj)[0];
		}
		return "";
	}

	public boolean sendECO2(WTObject obj) throws Exception {
		boolean result = true;

		WTChangeOrder2 eco = (WTChangeOrder2) obj;

		String _now = eco.getModifyTimestamp().toString().replace("-", "/");

		this.cols.clear();
		this.cols.put("PrjNo", getIBAValue(AttributeService.getValue(eco, "Project")).substring(0, 7));
		// this.cols.put("PrjSeqNo", getIBAValue(AttributeService.getValue(eco,
		// "Project")).substring(8, 11));
		this.cols.put("PrjSeqNo", getIBAValue(AttributeService.getValue(eco, "Project")).substring(8, 20));
		this.cols.put("EcoNo", eco.getNumber());
		QueryResult ecrs = ChangeHelper2.service.getChangeRequest(eco);
		if (ecrs.size() > 0) {
			WTChangeRequest2 ecr = (WTChangeRequest2) ecrs.getEnumeration().nextElement();
			this.cols.put("EcrNo", ecr.getNumber());
		} else {
			this.cols.put("EcrNo", "");
		}
		this.cols.put("Title", ERPInterface.HtmlEncode(eco.getName()));
		this.cols.put("Type", getIBAValue(AttributeService.getValue(eco, "EC_Reason")));
		this.cols.put("WorkDate", "");
		this.cols.put("State", eco.getState().getState().getDisplay(this.locale));
		this.cols.put("ConfirmDate", _now.substring(0, 10));
		this.cols.put("UnitCode", "");
		this.cols.put("Process", ecrs.size() > 0 ? "ECR_EXIST" : "ECR_NO");
		this.querys = QueryBuilder.getQueryString("PDMECO", this.cols, new String[] { "PrjNo", "PrjSeqNo", "EcoNo" },
				QueryType.INSERT_OR_UPDATE);
		if (!ExecuteQuery(this.querys)) {
			System.out.println("에러 됨..");
			result = false;
		}

		WTPart[] parts = ERPInterface.getPartList(eco);
		String drawno;
		for (int i = 0; (parts != null) && (i < parts.length); i++) {
			WTPart part = parts[i];

			if (!part.getNumber().toUpperCase().matches("^[A-Za-z]{2}-[A-Za-z0-9]{2}-.{10}$")) {
				System.out.println("=== [ERPDataSender:sendECO] Part 전송 시 채번 룰에 적합하지 않은 Part 목록 ===");
				System.out.println("part.getName() : " + part.getName());
				System.out.println("part.getNumber() : " + part.getNumber());
				System.out.println("part.getVersion() : " + part.getVersionDisplayIdentity());
				continue;
			}
			this.cols.clear();
			this.cols.put("Div", "ECO");
			this.cols.put("EONo", eco.getNumber());
			this.cols.put("ItemCode", part.getNumber().replaceAll("'", ""));
			this.cols.put("ItemVer", part.getVersionInfo().getIdentifier().getValue() + "."
					+ part.getIterationInfo().getIdentifier().getValue());
			this.querys = QueryBuilder.getQueryString("PDMEOITEM", this.cols,
					new String[] { "Div", "EONo", "ItemCode", "ItemVer" }, QueryType.INSERT_OR_UPDATE);
			if (!ExecuteQuery(this.querys)) {
				result = false;
			}

			this.cols.clear();
			this.cols.put("PDMWorkTime", _now.replaceAll("/|-| |:|\\..", ""));
			this.cols.put("ItemCode", part.getNumber().replaceAll("'", ""));
			this.cols.put("ItemVer", part.getVersionInfo().getIdentifier().getValue() + "."
					+ part.getIterationInfo().getIdentifier().getValue());
			this.cols.put("ItemName", part.getName());
			this.cols.put("BaseUnit", "ea");
			System.out.println("Spec :: " + getIBAValue(AttributeService.getValue(part, "Spec")));
			System.out.println("Maker :: " + getIBAValue(AttributeService.getValue(part, "Maker")));
			System.out.println("Treatment :: " + getIBAValue(AttributeService.getValue(part, "Treatment")));
			System.out.println("material :: " + getIBAValue(AttributeService.getValue(part, "material")));
//	            this.cols.put("ItemSpec", getIBAValue(AttributeService.getValue(part, "Spec") == null ? "" : getIBAValue(AttributeService.getValue(part, "Spec"))));
//	            this.cols.put("Maker", getIBAValue(AttributeService.getValue(part, "Maker") == null ? "" : getIBAValue(AttributeService.getValue(part, "Maker"))));
//	            this.cols.put("Treatment", getIBAValue(AttributeService.getValue(part, "Treatment") == null ? "" : getIBAValue(AttributeService.getValue(part, "Treatment"))));
//	            this.cols.put("material", getIBAValue(AttributeService.getValue(part, "material") == null ? "" : getIBAValue(AttributeService.getValue(part, "material"))));
			this.cols.put("ApplyDate", _now.substring(0, 10));
			this.cols.put("ERPWorkTime", _now.substring(0, 10));
			this.cols.put("ERPWorkDiv", "");
			this.cols.put("ItemCode2", "");
			this.cols.put("ItemVer2", "");
			this.cols.put("EONo", eco.getNumber());

			EPMDocument epm3d = DrawingHelper2.getEPMDocument(parts[i]);
			EPMDocument epm2d = null;
			if (epm3d != null) {
				this.cols.put("ItemSpec", getIBAValue(AttributeService.getValue(epm3d, "Spec") == null ? ""
						: getIBAValue(AttributeService.getValue(epm3d, "Spec"))));
				this.cols.put("Maker", getIBAValue(AttributeService.getValue(epm3d, "Maker") == null ? ""
						: getIBAValue(AttributeService.getValue(epm3d, "Maker"))));
				this.cols.put("Treatment", getIBAValue(AttributeService.getValue(epm3d, "Treatment") == null ? ""
						: getIBAValue(AttributeService.getValue(epm3d, "Treatment"))));
				this.cols.put("material", getIBAValue(AttributeService.getValue(epm3d, "material") == null ? ""
						: getIBAValue(AttributeService.getValue(epm3d, "material"))));
			} else {
				this.cols.put("ItemSpec", getIBAValue(AttributeService.getValue(part, "Spec") == null ? ""
						: getIBAValue(AttributeService.getValue(part, "Spec"))));
				this.cols.put("Maker", getIBAValue(AttributeService.getValue(part, "Maker") == null ? ""
						: getIBAValue(AttributeService.getValue(part, "Maker"))));
				this.cols.put("Treatment", getIBAValue(AttributeService.getValue(part, "Treatment") == null ? ""
						: getIBAValue(AttributeService.getValue(part, "Treatment"))));
				this.cols.put("material", getIBAValue(AttributeService.getValue(part, "material") == null ? ""
						: getIBAValue(AttributeService.getValue(part, "material"))));
			}
			drawno = "";
			String drawver = "";
			String folder = "";
			System.out.println("=== [ERPDataSender:sendECO] Part 관련 3D/2D ===");
			if (epm3d == null) {
				System.out.println("==========> 도면이 존재하지 않습니다. : " + parts[i].getNumber());
			} else if ("PROE".equals(epm3d.getAuthoringApplication().toString())) {
				System.out.println(
						"epm3d.getAuthoringApplication().toString() = " + epm3d.getAuthoringApplication().toString());
				System.out.println("epm3d.getName() = " + epm3d.getName());
				System.out.println("epm3d.getCADName() = " + epm3d.getCADName());
				System.out.println("epm3d.getNumber() = " + epm3d.getNumber());
				System.out.println("epm3d.getVersionDisplayIdentity() = " + epm3d.getVersionDisplayIdentity());
				System.out.println("Spec2 :: " + getIBAValue(AttributeService.getValue(epm3d, "Spec")));
				System.out.println("Maker2 :: " + getIBAValue(AttributeService.getValue(epm3d, "Maker")));
				System.out.println("Treatment2 :: " + getIBAValue(AttributeService.getValue(epm3d, "Treatment")));
				System.out.println("material2 :: " + getIBAValue(AttributeService.getValue(epm3d, "material")));
				epm2d = DrawingHelper2.getRelational2DCad(epm3d);
				if (epm2d != null) {
					drawver = epm2d.getVersionInfo().getIdentifier().getValue() + "."
							+ epm2d.getIterationInfo().getIdentifier().getValue();
					folder = epm2d == null ? "" : ERPPdfSender.getTargetFolder(eco.getPersistInfo().getUpdateStamp());
					System.out.println("epm2d.getName() = " + epm2d.getName());
					System.out.println("epm2d.getNumber() = " + epm2d.getNumber());
					System.out.println("epm2d.getVersionDisplayIdentity() = " + epm2d.getVersionDisplayIdentity());
				}
				drawno = "";
			} else if ("ACAD".equals(epm3d.getAuthoringApplication().toString())) {
				drawno = epm3d.getCADName();
				drawver = "";
				folder = ERPPdfSender.getTargetFolder(eco.getPersistInfo().getUpdateStamp());

				System.out.println(
						"epm3d.getAuthoringApplication().toString() = " + epm3d.getAuthoringApplication().toString());
				System.out.println("epm3d.getName() = " + epm3d.getName());
				System.out.println("epm3d.getCADName() = " + epm3d.getCADName());
				System.out.println("epm3d.getNumber() = " + epm3d.getNumber());
				System.out.println("epm3d.getVersionDisplayIdentity() = " + epm3d.getVersionDisplayIdentity());
			}
			this.cols.put("DrawNo", drawno);
			this.cols.put("DrawVer", drawver);
			this.cols.put("Folder", folder);
			this.querys = QueryBuilder.getQueryString("PDM00", this.cols, new String[] { "ItemCode", "ItemVer" },
					QueryType.INSERT_OR_UPDATE);
			if (!ExecuteQuery(this.querys)) {
				result = false;
			}
			/*
			 * this.param.clear(); this.param.put("@ItemCode",
			 * (String)this.cols.get("ItemCode")); this.param.put("@ItemVer",
			 * (String)this.cols.get("ItemVer")); this.param.put("@ItemName",
			 * (String)this.cols.get("ItemName")); this.param.put("@ItemSpecName",
			 * (String)this.cols.get("ItemSpec")); this.param.put("@Treatment",
			 * (String)this.cols.get("Treatment")); this.param.put("@material",
			 * (String)this.cols.get("material")); this.param.put("@BaseUnit",
			 * (String)this.cols.get("BaseUnit")); this.param.put("@ApplyDate",
			 * _now.substring(0, 10)); this.param.put("@Maker",
			 * (String)this.cols.get("Maker")); this.param.put("@DrawNo",
			 * (String)this.cols.get("DrawNo")); this.param.put("@DrawVer",
			 * (String)this.cols.get("DrawVer")); this.param.put("@Folder",
			 * (String)this.cols.get("Folder")); this.param.put("@UserID",
			 * (String)this.cols.get("9999999")); this.querys =
			 * QueryBuilder.getQueryString("[dbo].[usp_merge_bba010t]", this.param, new
			 * String[0], QueryType.PROCEDURE);
			 */

			this.param.clear();
			this.param.put("@ItemCode", (String) this.cols.get("ItemCode"));
			this.param.put("@ItemVer", (String) this.cols.get("ItemVer"));
			this.param.put("@ItemName", (String) this.cols.get("ItemName"));
			this.param.put("@ItemSpec", (String) this.cols.get("ItemSpec"));
			this.param.put("@treatment", (String) this.cols.get("Treatment"));
			this.param.put("@material", (String) this.cols.get("material"));
			this.param.put("@BaseUnit", (String) this.cols.get("BaseUnit"));
			this.param.put("@ApplyDate", _now.substring(0, 10));
			this.param.put("@Maker", (String) this.cols.get("Maker"));
			this.param.put("@DrawNo", (String) this.cols.get("DrawNo"));
			this.param.put("@DrawVer", (String) this.cols.get("DrawVer"));
			this.param.put("@Folder", (String) this.cols.get("Folder"));
			this.param.put("@UserID", (String) this.cols.get("9999999"));
			this.querys = QueryBuilder.getQueryString("[dbo].[usp_merge_bba010t]", this.param, new String[0],
					QueryType.PROCEDURE);

			if (ExecuteQuery(this.querys)) {
				WorkflowHelper2.changeState2(part, "SENT_ERP");
				System.out.println("===========> PART STATUS CHANGES : " + part.getNumber());
			} else {
				result = false;
				System.out.println("===========> ERP SEND ERROR : " + part.getNumber());
			}

		}

		for (int i = 0; (parts != null) && (i < parts.length); i++) {
			WTPart part = parts[i];

			if (!part.getNumber().toUpperCase().matches("^[A-Za-z]{2}-[A-Za-z0-9]{2}-.{10}$")) {
				System.out.println("=== [ERPDataSender:sendECO] BOM 전송 시 채번 룰에 적합하지 않은 Part 목록 ===");
				System.out.println("part.getName() : " + part.getName());
				System.out.println("part.getNumber() : " + part.getNumber());
				System.out.println("part.getVersion() : " + part.getVersionDisplayIdentity());
				continue;
			}
			HashMap<WTPart, Double> children = PartHelper.getChild(part);
			if ((children != null) && (children.size() > 0)) {
				for (Map.Entry entry : children.entrySet()) {
					WTPart child = (WTPart) entry.getKey();
					Double childCnt = (Double) entry.getValue();
					WTPartMaster master = child.getMaster();
					WTPartUsageLink link = PartHelper.getLinktoBOM(part, master);
					if ((!child.getNumber().toUpperCase().matches("^[A-Za-z]{2}-[A-Za-z0-9]{2}-.{10}$"))
							|| (part.getNumber().equals(child.getNumber()))) {
						System.out.println("=== [ERPDataSender:sendECO] BOM 전송 시 채번 룰에 적합하지 않은 Child 목록 ===");
						System.out.println("child.getName() : " + child.getName());
						System.out.println("child.getNumber() : " + child.getNumber());
						System.out.println("child.getVersion() : " + child.getVersionDisplayIdentity());
						continue;
					}
					this.cols.clear();
					this.cols.put("PDMWorkTime", _now.replaceAll("/|-| |:|\\..", ""));
					this.cols.put("ParentItemCode", part.getNumber().replaceAll("'", ""));
					this.cols.put("ParentItemVer", part.getVersionInfo().getIdentifier().getValue() + "."
							+ part.getIterationInfo().getIdentifier().getValue());
					this.cols.put("ItemCode", child.getNumber().replaceAll("'", ""));
					this.cols.put("ItemVer", child.getVersionInfo().getIdentifier().getValue() + "."
							+ child.getIterationInfo().getIdentifier().getValue());
					this.cols.put("MkuQty", Double.toString(childCnt.doubleValue()));
					double a = E3PSScheduleJobs.getFloatValue(child, "COST_PDM");
					String FCost = Double.toString(a);
					this.cols.put("FCost", FCost);
					this.cols.put("EoNo", eco.getNumber());
					this.querys = QueryBuilder.getQueryString("PDMBOM", this.cols,
							new String[] { "ParentItemCode", "ParentItemVer", "ItemCode", "ItemVer" },
							QueryType.INSERT_OR_UPDATE);
					if (!ExecuteQuery(this.querys)) {
						result = false;
					}
					/*
					 * this.param.clear(); this.param.put("@ParentItemCode",
					 * (String)this.cols.get("ParentItemCode")); this.param.put("@ParentItemVer",
					 * (String)this.cols.get("ParentItemVer")); this.param.put("@CItemCode",
					 * (String)this.cols.get("ItemCode")); this.param.put("@MkuQty",
					 * (String)this.cols.get("MkuQty")); this.param.put("@FCost",
					 * (String)this.cols.get("FCost")); this.param.put("@UserID", "9999999");
					 * this.querys = QueryBuilder.getQueryString("[dbo].[CD0103]", this.param, new
					 * String[0], QueryType.PROCEDURE);
					 */

					this.param.clear();
					this.param.put("@pitemcode", (String) this.cols.get("ParentItemCode"));
					this.param.put("@pitemver", (String) this.cols.get("ParentItemVer"));
					this.param.put("@citemcode", (String) this.cols.get("ItemCode"));
					this.param.put("@unitqty", (String) this.cols.get("MkuQty"));
					this.param.put("@fcost", (String) this.cols.get("FCost"));
					this.param.put("@userid", "9999999");
					this.querys = QueryBuilder.getQueryString("[dbo].[usp_merge_bba020t]", this.param, new String[0],
							QueryType.PROCEDURE);

					if (ExecuteQuery(this.querys)) {
						continue;
					}
					result = false;
				}

			}

		}

		return result;
	}

	/*
	 * public boolean sendTEST(WTObject obj) throws Exception { boolean result =
	 * true; WTChangeOrder2 eco = (WTChangeOrder2)obj; String _now =
	 * eco.getModifyTimestamp().toString().replace("-", "/"); WTPart[] parts =
	 * ERPInterface.getPartList(eco); for (int i = 0; (parts != null) && (i <
	 * parts.length); i++) { WTPart part = parts[i];
	 * 
	 * // if
	 * (!part.getNumber().toUpperCase().matches("^[A-Za-z]{2}-[A-Za-z0-9]{2}-.{10}$"
	 * )) { // System.out.
	 * println("=== [ERPDataSender:sendECO] BOM 전송 시 채번 룰에 적합하지 않은 Part 목록 ==="); //
	 * System.out.println("part.getName() : " + part.getName()); //
	 * System.out.println("part.getNumber() : " + part.getNumber()); //
	 * System.out.println("part.getVersion() : " +
	 * part.getVersionDisplayIdentity()); // continue; // } HashMap<WTPart, Double>
	 * children = PartHelper.getChild(part); if ((children != null) &&
	 * (children.size() > 0)) { for (Map.Entry entry : children.entrySet()) { WTPart
	 * child = (WTPart)entry.getKey(); Double childCnt = (Double)entry.getValue();
	 * WTPartMaster master = child.getMaster(); WTPartUsageLink link =
	 * PartHelper.getLinktoBOM(part, master); // if
	 * ((!child.getNumber().toUpperCase().matches(
	 * "^[A-Za-z]{2}-[A-Za-z0-9]{2}-.{10}$")) || //
	 * (part.getNumber().equals(child.getNumber()))) { // System.out.
	 * println("=== [ERPDataSender:sendECO] BOM 전송 시 채번 룰에 적합하지 않은 Child 목록 ===");
	 * // System.out.println("child.getName() : " + child.getName()); //
	 * System.out.println("child.getNumber() : " + child.getNumber()); //
	 * System.out.println("child.getVersion() : " +
	 * child.getVersionDisplayIdentity()); // continue; // } this.cols.clear();
	 * this.cols.put("PDMWorkTime", _now.replaceAll("/|-| |:|\\..", ""));
	 * this.cols.put("ParentItemCode", part.getNumber());
	 * this.cols.put("ParentItemVer",
	 * part.getVersionInfo().getIdentifier().getValue() + "." +
	 * part.getIterationInfo().getIdentifier().getValue());
	 * this.cols.put("ItemCode", child.getNumber()); this.cols.put("ItemVer",
	 * child.getVersionInfo().getIdentifier().getValue() + "." +
	 * child.getIterationInfo().getIdentifier().getValue()); this.cols.put("MkuQty",
	 * Double.toString(childCnt.doubleValue())); double a =
	 * E3PSScheduleJobs.getFloatValue(child, "COST_PDM"); String FCost =
	 * Double.toString(a); this.cols.put("FCost", FCost); this.cols.put("EoNo",
	 * eco.getNumber()); this.querys = QueryBuilder.getQueryString("PDMBOM_test",
	 * this.cols, new String[] { "ParentItemCode", "ParentItemVer", "ItemCode",
	 * "ItemVer" }, QueryType.INSERT_OR_UPDATE); if (!ExecuteQuery(this.querys))
	 * result = false;
	 * 
	 * this.param.clear(); this.param.put("@ParentItemCode",
	 * (String)this.cols.get("ParentItemCode")); this.param.put("@ParentItemVer",
	 * (String)this.cols.get("ParentItemVer")); this.param.put("@CItemCode",
	 * (String)this.cols.get("ItemCode")); this.param.put("@MkuQty",
	 * (String)this.cols.get("MkuQty")); this.param.put("@FCost",
	 * (String)this.cols.get("FCost")); this.param.put("@UserID", "9999999");
	 * this.querys = QueryBuilder.getQueryString("[dbo].[CD0103_test]", this.param,
	 * new String[0], QueryType.PROCEDURE); if (ExecuteQuery(this.querys)) continue;
	 * result = false; }
	 * 
	 * }
	 * 
	 * }
	 * 
	 * return result; }
	 */
	private boolean ExecuteQuery2(String[] querys) {
		DBConnectionManager db = null;
		Connection con = null;
		Statement stmt = null;
		boolean result = true;
		try {
			db = DBConnectionManager.getInstance();
			con = db.getConnection("erp2");
			stmt = con.createStatement();
			int q = 0;
			do {
				System.out.print("QUERY : " + querys[q]);
				try {
					stmt.execute(querys[q]);
					System.out.println(" ==> SUCCEED.");
				} catch (SQLException e) {
					System.out.println(" ==> FAILED.");
					result = false;
				}
				q++;
				if (querys == null)
					break;
			} while (q < querys.length);
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				stmt.close();
				con.close();
				db.freeConnection("erp2", con);
			} catch (SQLException localSQLException1) {
			}
		} catch (IOException e) {
			e.printStackTrace();
			try {
				stmt.close();
				con.close();
				db.freeConnection("erp2", con);
			} catch (SQLException localSQLException2) {
			}
		} finally {
			try {
				stmt.close();
				con.close();
				db.freeConnection("erp2", con);
			} catch (SQLException localSQLException3) {
			}
		}
		return result;
	}

	public boolean sendECO3(WTObject obj) throws Exception {
		boolean result = true;

		WTChangeOrder2 eco = (WTChangeOrder2) obj;

		String _now = eco.getModifyTimestamp().toString().replace("-", "/");

		this.cols.clear();
		this.cols.put("PrjNo", getIBAValue(AttributeService.getValue(eco, "Project")).substring(0, 7));
		// this.cols.put("PrjSeqNo", getIBAValue(AttributeService.getValue(eco,
		// "Project")).substring(8, 11));
		this.cols.put("PrjSeqNo", getIBAValue(AttributeService.getValue(eco, "Project")).substring(8, 20));
		this.cols.put("EcoNo", eco.getNumber());
		QueryResult ecrs = ChangeHelper2.service.getChangeRequest(eco);
		if (ecrs.size() > 0) {
			WTChangeRequest2 ecr = (WTChangeRequest2) ecrs.getEnumeration().nextElement();
			this.cols.put("EcrNo", ecr.getNumber());
		} else {
			this.cols.put("EcrNo", "");
		}
		this.cols.put("Title", ERPInterface.HtmlEncode(eco.getName()));
		this.cols.put("Type", getIBAValue(AttributeService.getValue(eco, "EC_Reason")));
		this.cols.put("WorkDate", "");
		this.cols.put("State", eco.getState().getState().getDisplay(this.locale));
		this.cols.put("ConfirmDate", _now.substring(0, 10));
		this.cols.put("UnitCode", "");
		this.cols.put("Process", ecrs.size() > 0 ? "ECR_EXIST" : "ECR_NO");
		this.querys = QueryBuilder.getQueryString("PDMECO", this.cols, new String[] { "PrjNo", "PrjSeqNo", "EcoNo" },
				QueryType.INSERT_OR_UPDATE);
		if (!ExecuteQuery2(this.querys))
			result = false;

		WTPart[] parts = ERPInterface.getPartList(eco);
		String drawno;
		for (int i = 0; (parts != null) && (i < parts.length); i++) {
			WTPart part = parts[i];
			if (!part.getNumber().toUpperCase().matches("^[A-Za-z]{2}-[A-Za-z0-9]{2}-.{10}$")) {
				System.out.println("=== [ERPDataSender:sendECO] Part 전송 시 채번 룰에 적합하지 않은 Part 목록 ===");
				System.out.println("part.getName() : " + part.getName());
				System.out.println("part.getNumber() : " + part.getNumber());
				System.out.println("part.getVersion() : " + part.getVersionDisplayIdentity());
				continue;
			}
			this.cols.clear();
			this.cols.put("Div", "ECO");
			this.cols.put("EONo", eco.getNumber());
			this.cols.put("ItemCode", part.getNumber().replaceAll("'", ""));
			this.cols.put("ItemVer", part.getVersionInfo().getIdentifier().getValue() + "."
					+ part.getIterationInfo().getIdentifier().getValue());
			this.querys = QueryBuilder.getQueryString("PDMEOITEM", this.cols,
					new String[] { "Div", "EONo", "ItemCode", "ItemVer" }, QueryType.INSERT_OR_UPDATE);
			if (!ExecuteQuery2(this.querys))
				result = false;

			this.cols.clear();
			this.cols.put("PDMWorkTime", _now.replaceAll("/|-| |:|\\..", ""));
			this.cols.put("ItemCode", part.getNumber().replaceAll("'", ""));
			this.cols.put("ItemVer", part.getVersionInfo().getIdentifier().getValue() + "."
					+ part.getIterationInfo().getIdentifier().getValue());
			this.cols.put("ItemName", part.getName());
			this.cols.put("BaseUnit", "ea");
			System.out.println("Spec :: " + getIBAValue(AttributeService.getValue(part, "Spec")));
			System.out.println("Maker :: " + getIBAValue(AttributeService.getValue(part, "Maker")));
			System.out.println("Treatment :: " + getIBAValue(AttributeService.getValue(part, "Treatment")));
			System.out.println("material :: " + getIBAValue(AttributeService.getValue(part, "material")));
//	            this.cols.put("ItemSpec", getIBAValue(AttributeService.getValue(part, "Spec") == null ? "" : getIBAValue(AttributeService.getValue(part, "Spec"))));
//	            this.cols.put("Maker", getIBAValue(AttributeService.getValue(part, "Maker") == null ? "" : getIBAValue(AttributeService.getValue(part, "Maker"))));
//	            this.cols.put("Treatment", getIBAValue(AttributeService.getValue(part, "Treatment") == null ? "" : getIBAValue(AttributeService.getValue(part, "Treatment"))));
//	            this.cols.put("material", getIBAValue(AttributeService.getValue(part, "material") == null ? "" : getIBAValue(AttributeService.getValue(part, "material"))));
			this.cols.put("ApplyDate", _now.substring(0, 10));
			this.cols.put("ERPWorkTime", _now.substring(0, 10));
			this.cols.put("ERPWorkDiv", "");
			this.cols.put("ItemCode2", "");
			this.cols.put("ItemVer2", "");
			this.cols.put("EONo", eco.getNumber());

			EPMDocument epm3d = DrawingHelper2.getEPMDocument(parts[i]);
			EPMDocument epm2d = null;
			if (epm3d != null) {
				this.cols.put("ItemSpec", getIBAValue(AttributeService.getValue(epm3d, "Spec") == null ? ""
						: getIBAValue(AttributeService.getValue(epm3d, "Spec"))));
				this.cols.put("Maker", getIBAValue(AttributeService.getValue(epm3d, "Maker") == null ? ""
						: getIBAValue(AttributeService.getValue(epm3d, "Maker"))));
				this.cols.put("Treatment", getIBAValue(AttributeService.getValue(epm3d, "Treatment") == null ? ""
						: getIBAValue(AttributeService.getValue(epm3d, "Treatment"))));
				this.cols.put("material", getIBAValue(AttributeService.getValue(epm3d, "material") == null ? ""
						: getIBAValue(AttributeService.getValue(epm3d, "material"))));
			} else {
				this.cols.put("ItemSpec", getIBAValue(AttributeService.getValue(part, "Spec") == null ? ""
						: getIBAValue(AttributeService.getValue(part, "Spec"))));
				this.cols.put("Maker", getIBAValue(AttributeService.getValue(part, "Maker") == null ? ""
						: getIBAValue(AttributeService.getValue(part, "Maker"))));
				this.cols.put("Treatment", getIBAValue(AttributeService.getValue(part, "Treatment") == null ? ""
						: getIBAValue(AttributeService.getValue(part, "Treatment"))));
				this.cols.put("material", getIBAValue(AttributeService.getValue(part, "material") == null ? ""
						: getIBAValue(AttributeService.getValue(part, "material"))));
			}
			drawno = "";
			String drawver = "";
			String folder = "";
			System.out.println("=== [ERPDataSender:sendECO] Part 관련 3D/2D ===");
			if (epm3d == null) {
				System.out.println("==========> 도면이 존재하지 않습니다. : " + parts[i].getNumber());
			} else if ("PROE".equals(epm3d.getAuthoringApplication().toString())) {
				System.out.println(
						"epm3d.getAuthoringApplication().toString() = " + epm3d.getAuthoringApplication().toString());
				System.out.println("epm3d.getName() = " + epm3d.getName());
				System.out.println("epm3d.getCADName() = " + epm3d.getCADName());
				System.out.println("epm3d.getNumber() = " + epm3d.getNumber());
				System.out.println("epm3d.getVersionDisplayIdentity() = " + epm3d.getVersionDisplayIdentity());
				System.out.println("Spec2 :: " + getIBAValue(AttributeService.getValue(epm3d, "Spec")));
				System.out.println("Maker2 :: " + getIBAValue(AttributeService.getValue(epm3d, "Maker")));
				System.out.println("Treatment2 :: " + getIBAValue(AttributeService.getValue(epm3d, "Treatment")));
				System.out.println("material2 :: " + getIBAValue(AttributeService.getValue(epm3d, "material")));
				epm2d = DrawingHelper2.getRelational2DCad(epm3d);
				if (epm2d != null) {
					drawver = epm2d.getVersionInfo().getIdentifier().getValue() + "."
							+ epm2d.getIterationInfo().getIdentifier().getValue();
					folder = epm2d == null ? "" : ERPPdfSender.getTargetFolder(eco.getPersistInfo().getUpdateStamp());
					System.out.println("epm2d.getName() = " + epm2d.getName());
					System.out.println("epm2d.getNumber() = " + epm2d.getNumber());
					System.out.println("epm2d.getVersionDisplayIdentity() = " + epm2d.getVersionDisplayIdentity());
				}
				drawno = "";
			} else if ("ACAD".equals(epm3d.getAuthoringApplication().toString())) {
				drawno = epm3d.getCADName();
				drawver = "";
				folder = ERPPdfSender.getTargetFolder(eco.getPersistInfo().getUpdateStamp());

				System.out.println(
						"epm3d.getAuthoringApplication().toString() = " + epm3d.getAuthoringApplication().toString());
				System.out.println("epm3d.getName() = " + epm3d.getName());
				System.out.println("epm3d.getCADName() = " + epm3d.getCADName());
				System.out.println("epm3d.getNumber() = " + epm3d.getNumber());
				System.out.println("epm3d.getVersionDisplayIdentity() = " + epm3d.getVersionDisplayIdentity());
			}
			this.cols.put("DrawNo", drawno);
			this.cols.put("DrawVer", drawver);
			this.cols.put("Folder", folder);
			this.querys = QueryBuilder.getQueryString("PDM00", this.cols, new String[] { "ItemCode", "ItemVer" },
					QueryType.INSERT_OR_UPDATE);
			if (!ExecuteQuery2(this.querys))
				result = false;
			/*
			 * this.param.clear(); this.param.put("@ItemCode",
			 * (String)this.cols.get("ItemCode")); this.param.put("@ItemVer",
			 * (String)this.cols.get("ItemVer")); this.param.put("@ItemName",
			 * (String)this.cols.get("ItemName")); this.param.put("@ItemSpecName",
			 * (String)this.cols.get("ItemSpec")); this.param.put("@Treatment",
			 * (String)this.cols.get("Treatment")); this.param.put("@material",
			 * (String)this.cols.get("material")); this.param.put("@BaseUnit",
			 * (String)this.cols.get("BaseUnit")); this.param.put("@ApplyDate",
			 * _now.substring(0, 10)); this.param.put("@Maker",
			 * (String)this.cols.get("Maker")); this.param.put("@DrawNo",
			 * (String)this.cols.get("DrawNo")); this.param.put("@DrawVer",
			 * (String)this.cols.get("DrawVer")); this.param.put("@Folder",
			 * (String)this.cols.get("Folder")); this.param.put("@UserID",
			 * (String)this.cols.get("9999999")); this.querys =
			 * QueryBuilder.getQueryString("[dbo].[CD0102]", this.param, new String[0],
			 * QueryType.PROCEDURE);
			 */

			this.param.clear();
			this.param.put("@ItemCode", (String) this.cols.get("ItemCode"));
			this.param.put("@ItemVer", (String) this.cols.get("ItemVer"));
			this.param.put("@ItemName", (String) this.cols.get("ItemName"));
			this.param.put("@ItemSpec", (String) this.cols.get("ItemSpec"));
			this.param.put("@treatment", (String) this.cols.get("Treatment"));
			this.param.put("@material", (String) this.cols.get("material"));
			this.param.put("@BaseUnit", (String) this.cols.get("BaseUnit"));
			this.param.put("@ApplyDate", _now.substring(0, 10));
			this.param.put("@Maker", (String) this.cols.get("Maker"));
			this.param.put("@DrawNo", (String) this.cols.get("DrawNo"));
			this.param.put("@DrawVer", (String) this.cols.get("DrawVer"));
			this.param.put("@Folder", (String) this.cols.get("Folder"));
			this.param.put("@UserID", (String) this.cols.get("9999999"));
			this.querys = QueryBuilder.getQueryString("[dbo].[usp_merge_bba010t]", this.param, new String[0],
					QueryType.PROCEDURE);

			if (ExecuteQuery2(this.querys)) {
				WorkflowHelper2.changeState2(part, "SENT_ERP");
				System.out.println("===========> PART STATUS CHANGES : " + part.getNumber());
			} else {
				result = false;
				System.out.println("===========> ERP SEND ERROR : " + part.getNumber());
			}

		}

		for (int i = 0; (parts != null) && (i < parts.length); i++) {
			WTPart part = parts[i];

			if (!part.getNumber().toUpperCase().matches("^[A-Za-z]{2}-[A-Za-z0-9]{2}-.{10}$")) {
				System.out.println("=== [ERPDataSender:sendECO] BOM 전송 시 채번 룰에 적합하지 않은 Part 목록 ===");
				System.out.println("part.getName() : " + part.getName());
				System.out.println("part.getNumber() : " + part.getNumber());
				System.out.println("part.getVersion() : " + part.getVersionDisplayIdentity());
				continue;
			}
			HashMap<WTPart, Double> children = PartHelper.getChild(part);
			if ((children != null) && (children.size() > 0)) {
				for (Map.Entry entry : children.entrySet()) {
					WTPart child = (WTPart) entry.getKey();
					Double childCnt = (Double) entry.getValue();
					WTPartMaster master = child.getMaster();
					WTPartUsageLink link = PartHelper.getLinktoBOM(part, master);
					if ((!child.getNumber().toUpperCase().matches("^[A-Za-z]{2}-[A-Za-z0-9]{2}-.{10}$"))
							|| (part.getNumber().equals(child.getNumber()))) {
						System.out.println("=== [ERPDataSender:sendECO] BOM 전송 시 채번 룰에 적합하지 않은 Child 목록 ===");
						System.out.println("child.getName() : " + child.getName());
						System.out.println("child.getNumber() : " + child.getNumber());
						System.out.println("child.getVersion() : " + child.getVersionDisplayIdentity());
						continue;
					}
					this.cols.clear();
					this.cols.put("PDMWorkTime", _now.replaceAll("/|-| |:|\\..", ""));
					this.cols.put("ParentItemCode", part.getNumber().replaceAll("'", ""));
					this.cols.put("ParentItemVer", part.getVersionInfo().getIdentifier().getValue() + "."
							+ part.getIterationInfo().getIdentifier().getValue());
					this.cols.put("ItemCode", child.getNumber().replaceAll("'", ""));
					this.cols.put("ItemVer", child.getVersionInfo().getIdentifier().getValue() + "."
							+ child.getIterationInfo().getIdentifier().getValue());
					this.cols.put("MkuQty", Double.toString(childCnt.doubleValue()));
					double a = E3PSScheduleJobs.getFloatValue(child, "COST_PDM");
					String FCost = Double.toString(a);
					this.cols.put("FCost", FCost);
					this.cols.put("EoNo", eco.getNumber());
					this.querys = QueryBuilder.getQueryString("PDMBOM", this.cols,
							new String[] { "ParentItemCode", "ParentItemVer", "ItemCode", "ItemVer" },
							QueryType.INSERT_OR_UPDATE);
					if (!ExecuteQuery2(this.querys))
						result = false;
					/*
					 * this.param.clear(); this.param.put("@ParentItemCode",
					 * (String)this.cols.get("ParentItemCode")); this.param.put("@ParentItemVer",
					 * (String)this.cols.get("ParentItemVer")); this.param.put("@CItemCode",
					 * (String)this.cols.get("ItemCode")); this.param.put("@MkuQty",
					 * (String)this.cols.get("MkuQty")); this.param.put("@FCost",
					 * (String)this.cols.get("FCost")); this.param.put("@UserID", "9999999");
					 * this.querys = QueryBuilder.getQueryString("[dbo].[CD0103]", this.param, new
					 * String[0], QueryType.PROCEDURE);
					 */

					this.param.clear();
					this.param.put("@pitemcode", (String) this.cols.get("ParentItemCode"));
					this.param.put("@pitemver", (String) this.cols.get("ParentItemVer"));
					this.param.put("@citemcode", (String) this.cols.get("ItemCode"));
					this.param.put("@unitqty", (String) this.cols.get("MkuQty"));
					this.param.put("@fcost", (String) this.cols.get("FCost"));
					this.param.put("@userid", "9999999");
					this.querys = QueryBuilder.getQueryString("[dbo].[usp_merge_bba020t]", this.param, new String[0],
							QueryType.PROCEDURE);

					if (ExecuteQuery2(this.querys))
						continue;
					result = false;
				}

			}

			QueryResult aResult = ChangeHelper2.service.getChangeActivities((WTChangeOrder2) obj);
			QueryResult resultPPart = ChangeHelper2.service
					.getChangeablesBefore((WTChangeActivity2) aResult.nextElement());
			while (resultPPart.hasMoreElements()) {
				WTPart p = (WTPart) resultPPart.nextElement();
				String partNumber = p.getNumber();
				if (partNumber.startsWith("NA")) {

					if (!ExecuteQuery(this.querys))
						result = false;
					this.cols.clear();
					this.cols.put("ParentItemCode", p.getNumber().replaceAll("'", ""));
					this.cols.put("ParentItemVer", p.getVersionInfo().getIdentifier().getValue() + "."
							+ p.getIterationInfo().getIdentifier().getValue());
					this.param.put("@pitemcode", (String) this.cols.get("ParentItemCode"));
					this.param.put("@pitemver", (String) this.cols.get("ParentItemVer"));
					this.param.put("@UserID", "9999999");
					this.querys = QueryBuilder.getQueryString("[dbo].[usp_merge_bba020t_after]", this.param,
							new String[0], QueryType.PROCEDURE);

				} else {
					if (ExecuteQuery(this.querys))
						continue;
					result = false;
				}

			}
		}

		return result;
	}

	public static void main(String[] args) {
		String partno = (args != null) && (args.length > 0) ? args[0] : "NP-01-0099388783";
		System.out.println(Boolean.toString(partno.matches("^[A-Za-z]{2}-[A-Za-z0-9]{2}-.{10}$")));
	}
}
