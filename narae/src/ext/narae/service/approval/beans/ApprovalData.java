package ext.narae.service.approval.beans;

import java.util.ArrayList;

import ext.narae.service.approval.ApprovalLine;
import ext.narae.service.approval.ApprovalMaster;
import ext.narae.service.approval.ApprovalObjectLink;
import ext.narae.service.approval.CommonActivity;
import ext.narae.service.approval.MultiApproval;
import ext.narae.service.change.EChangeActivity;
import ext.narae.service.change.EChangeOrder2;
import ext.narae.service.change.EChangeRequest2;
import ext.narae.service.change.EOEul;
import ext.narae.service.change.beans.ChangeECOHelper;
import ext.narae.service.change.beans.ChangeHelper;
import ext.narae.service.erp.ERPHistory;
import ext.narae.service.erp.beans.ERPECOHelper;
import ext.narae.service.erp.beans.ERPUtil;
import ext.narae.util.CommonUtil;
import wt.doc.WTDocument;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;
import wt.query.SearchCondition;

public class ApprovalData {
	public Persistable obj;
	public ApprovalMaster master;
	public ArrayList line = new ArrayList();
	public ArrayList tempLine = new ArrayList();

	public ApprovalData(final Persistable per) throws Exception {
		this.obj = per;
		this.master = ApprovalHelper.manager.getApprovalMaster(per);

		setLine();
	}

	public ApprovalData(final ApprovalMaster master) throws Exception {

		this(master, true);
	}

	public ApprovalData(final ApprovalMaster master, final boolean setLine) throws Exception {

		this.master = master;
		// QueryResult qr =
		// PersistenceHelper.manager.navigate(master,"obj",ApprovalObjectLink.class);
		QuerySpec qs = new QuerySpec(ApprovalObjectLink.class);
		qs.appendWhere(new SearchCondition(ApprovalObjectLink.class, "roleBObjectRef.key.id", "=",
				CommonUtil.getOIDLongValue(master)), new int[] { 0 });

		QueryResult qr = PersistenceHelper.manager.find(qs);

		if (qr.hasMoreElements()) {
			Object ob = (Object) qr.nextElement();
			System.out.println("====ob =" + ob);
			ApprovalObjectLink aolink = (ApprovalObjectLink) ob;

			obj = aolink.getRoleAObject();
			// obj = (Persistable)qr.nextElement();
		}

		if (setLine) {
			setLine();
		}
	}

	public void setLine() throws Exception {
		if (master != null) {
			QueryResult qr = ApprovalHelper.manager.getApprovalLine(master);
			while (qr.hasMoreElements()) {
				ApprovalLine al = (ApprovalLine) qr.nextElement();
				if (ApprovalHelper.WORKING_TEMP.equals(al.getName())) {
					tempLine.add(al);
				} else {
					line.add(al);
				}
			}
		}
	}

	public String getGubun() {

		if (CommonUtil.isUSLocale()) {
			if (obj instanceof WTDocument) {
				return "Document";
			} else if (obj instanceof EChangeRequest2) {
				return "ECR";
			} else if (obj instanceof EChangeOrder2) {
				return "EO";
			} else if (obj instanceof EChangeActivity) {
				return "EO Activity";
			} else if (obj instanceof CommonActivity) {
				String gubun = ((CommonActivity) obj).getGubun();
				return ChangeHelper.manager.getLocaleString(gubun);
			} else if (obj instanceof EOEul) {
				return "BOM Approve";
			} else if (obj instanceof MultiApproval) {
				return "Part/Drawing";
			}
			return "Common";
		} else {
			if (obj instanceof WTDocument) {
				return "문서";
			} else if (obj instanceof EChangeRequest2) {
				return "ECR";
			} else if (obj instanceof EChangeOrder2) {
				return "ECO";
			} else if (obj instanceof EChangeActivity) {
				return "ECO활동";
			} else if (obj instanceof CommonActivity) {
				return ((CommonActivity) obj).getGubun();
			} else if (obj instanceof EOEul) {
				return "BOM승인";
			} else if (obj instanceof MultiApproval) {
				return "자재/도면";
			}
			return "일반";
		}

	}

	public String getTitle() {
		if (obj instanceof WTDocument) {
			return ((WTDocument) obj).getName();
		} else if (obj instanceof EChangeRequest2) {
			return ((EChangeRequest2) obj).getName();
		} else if (obj instanceof EChangeOrder2) {
			return ((EChangeOrder2) obj).getName();
		} else if (obj instanceof EChangeActivity) {
			return CommonUtil.isUSLocale() ? ((EChangeActivity) obj).getDefinition().getName_eng()
					: ((EChangeActivity) obj).getDefinition().getName();
		} else if (obj instanceof CommonActivity) {
			String ctitle = ((CommonActivity) obj).getTitle();
			return ChangeHelper.manager.getLocaleString(ctitle);
		} else if (obj instanceof EOEul) {
			EChangeOrder2 eo = ((EOEul) obj).getEco();
			return eo.getName();
		} else if (obj instanceof MultiApproval) {
			return ((MultiApproval) obj).getTitle();
		}
		if (CommonUtil.isUSLocale()) {
			return "Unknown";
		} else {
			return "제목 없음";
		}
	}

	public String getOwner() {
		if (ApprovalHelper.MASTER_APPROVING.equals(master.getState())) {
			ApprovalLine al = (ApprovalLine) line.get(master.getActive());
			if (ApprovalHelper.LINE_APPROVING.equals(al.getState())) {
				return al.getOwner();
			}
		}
		return "기타";
	}

	public void completeAction(final String state) throws Exception {

		if (obj instanceof EChangeOrder2) {

			System.out.println("*******ECO 승인*******");
			EChangeOrder2 eco = (EChangeOrder2) obj;
			if (ApprovalHelper.MASTER_APPROVED.equals(state)) { // ECO 승인 됐을경우

				ChangeECOHelper.manager.startActivity(eco);
				/* ERP 전송 */
				ERPHistory history = ERPHistory.newERPHistory();
				history.setEo(ObjectReference.newObjectReference(eco));
				PersistenceHelper.manager.save(history);

				ERPECOHelper.manager.erpECO(eco, ERPUtil.HISTORY_TYPE_CONFIRM, history);

			} else {
				eco.setOrderState(ChangeECOHelper.ECO_REJECTED);
				PersistenceHelper.manager.modify(eco);
			}

		}
		if (obj instanceof EChangeActivity) {

			ChangeECOHelper.manager.commitActivityParallel((EChangeActivity) obj, state);

		}
		if (obj instanceof EChangeRequest2) {
			EChangeRequest2 ecr = (EChangeRequest2) obj;
			ChangeHelper.manager.commitRequest(ecr, state);
		}
		if (obj instanceof WTDocument) {
			if (ApprovalHelper.MASTER_APPROVED.equals(state)) {
				WTDocument doc = (WTDocument) obj;
				// SecurityHelper.manager.commitRequestDoc(doc,state);
				// DocumentHelper.manager.commitOutputDocumnet(doc);
			}
		}
		if (obj instanceof EOEul) {
			EOEul eul = (EOEul) obj;
			ChangeHelper.manager.commitEOEul(eul, state);
		}
		if (obj instanceof MultiApproval) {
			if (ApprovalHelper.MASTER_APPROVED.equals(state)) {
				MultiApproval ma = (MultiApproval) obj;
//                SecurityHelper.manager.commitRequestPart(ma,state);
			}
		}
		if (obj instanceof CommonActivity) {

			System.out.println("*******CommonActivity 승인*******");
			CommonActivity com = (CommonActivity) obj;
			EChangeOrder2 eco = ChangeECOHelper.manager.getEO(com);

			if (eco != null) {
				if (ApprovalHelper.MASTER_APPROVED.equals(state)) {

					if (ChangeHelper.ACTIVE_WORK_LAST_APPROVAL.equals(com.getGubun())) // ECO 최종 결재시에만
					{
						/* PDM && ERP */
						ChangeECOHelper.manager.commitECO(com, state, eco);

					}

				} else if (ApprovalHelper.MASTER_REJECTED.equals(state)) {
					/* 반려 */
					ChangeECOHelper.manager.rejectEco(eco);

				}
			}

		}
	}
}
