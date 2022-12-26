package ext.narae.service.change.beans;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import com.ptc.wvs.server.util.PublishUtils;

import ext.narae.service.approval.ApprovalLine;
import ext.narae.service.approval.ApprovalMaster;
import ext.narae.service.approval.CommonActivity;
import ext.narae.service.approval.beans.ApprovalData;
import ext.narae.service.approval.beans.ApprovalHelper;
import ext.narae.service.change.ApplyChangeState;
import ext.narae.service.change.ApplyHistory;
import ext.narae.service.change.ECOReviseObject;
import ext.narae.service.change.EChangeActivity;
import ext.narae.service.change.EChangeActivityDefinition;
import ext.narae.service.change.EChangeOrder2;
import ext.narae.service.change.EChangeRequest2;
import ext.narae.service.change.EOEul;
import ext.narae.service.change.EcoPartLink;
import ext.narae.service.change.EulBaselineLink;
import ext.narae.service.change.EulPartLink;
import ext.narae.service.change.OrderActivityLink;
import ext.narae.service.change.PartReviseObjectLink;
import ext.narae.service.change.RequestOrderLink;
import ext.narae.service.change.editor.BEContext;
import ext.narae.service.change.editor.EOActionTempAssyData;
import ext.narae.service.change.editor.EOActionTempItemData;
import ext.narae.service.change.editor.EditorServerHelper;
import ext.narae.service.change.editor.EulPartHelper;
import ext.narae.service.drawing.beans.DrawingHelper;
import ext.narae.service.drawing.beans.EpmPublishUtil;
import ext.narae.service.drawing.beans.EpmSearchHelper;
import ext.narae.service.drawing.beans.EpmUtil;
import ext.narae.service.erp.EPMPDFLink;
import ext.narae.service.erp.ERPHistory;
import ext.narae.service.erp.beans.ERPECOHelper;
import ext.narae.service.erp.beans.ERPUtil;
import ext.narae.service.org.People;
import ext.narae.service.org.beans.UserHelper;
import ext.narae.service.part.beans.PartHelper;
import ext.narae.service.part.beans.PartSearchHelper;
import ext.narae.util.CommonUtil;
import ext.narae.util.DateUtil;
import ext.narae.util.SequenceDao;
import ext.narae.util.WCUtil;
import ext.narae.util.content.CommonContentHelper;
import ext.narae.util.content.FileDown;
import ext.narae.util.iba.IBAUtil;
import ext.narae.util.mail.MailUtil;
import wt.clients.folder.FolderTaskLogic;
import wt.clients.vc.CheckInOutTaskLogic;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.build.EPMBuildHistory;
import wt.epm.build.EPMBuildRule;
import wt.fc.ObjectReference;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.LifeCycleTemplate;
import wt.lifecycle.State;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.Quantity;
import wt.part.QuantityUnit;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.pom.Transaction;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.representation.Representation;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.vc.VersionControlHelper;
import wt.vc.baseline.BaselineHelper;
import wt.vc.baseline.ManagedBaseline;
import wt.vc.wip.CheckoutLink;
import wt.vc.wip.WorkInProgressHelper;

public class ChangeECOHelper implements wt.method.RemoteAccess, java.io.Serializable {

	public static final String ORDER_REGIST = "등록";
	public static final String ORDER_WORK = "진행";
	public static final String ORDER_COMPLETE = "완료";

	public static final String ECO_BEFORE_APPROVING = "사전결재중";
	public static final String ECO_ECA_WORKING = "ECA 활동중";
	public static final String ECO_AFTER_APPROVING = "최종결재중";
	public static final String ECO_COMPLETE = "완료됨";
	public static final String ECO_REJECTED = "반려됨";
	public static final String ECO_WORKING = "작업중";

	public static final String ACTIVITY_STANDBY = "대기중";
	public static final String ACTIVITY_WORKING = "작업중";
	public static final String ACTIVITY_APPROVING = "승인중";
	public static final String ACTIVITY_CANCELED = "반려됨";
	public static final String ACTIVITY_APPROVED = "작업완료";

	public static final String ACTIVE_WORK_LAST_APPROVAL = "EO최종결재";
	public static final String ACTIVE_WORK_COMPLETE = "EO완료";
	public static final String ACTIVE_WORK_REGIST = "EO등록";

	public static final String ACTIVE_WORK_REGIST_TITLE = "신규 EO 를 등록해 주십시오";
	public static final String ACTIVE_WORK_COMPLETE_TITLE = "EO활동이 모두 완료 되었습니다.";

	public static final String ACTIVE_ROLE_CHIEF = "CHIEF";
	public static final String ACTIVE_ROLE_WORKING = "WORKING";

	public static final String ECR_NO = "ECR_NO";
	public static final String ECR_EXIST = "ECR_EXIST";

	public static final String PART_TYPE_PART = "PART";
	public static final String PART_TYPE_TOP = "TOP";

	static final boolean SERVER = wt.method.RemoteMethodServer.ServerFlag;
	public static ChangeECOHelper manager = new ChangeECOHelper();

	public String createEco(Hashtable hash) throws Exception {

		if (!SERVER) {
			Class argTypes[] = new Class[] { Hashtable.class };
			Object args[] = new Object[] { hash };
			try {
				return (String) wt.method.RemoteMethodServer.getDefault().invoke("createEco", null, this, argTypes,
						args);
			} catch (RemoteException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}

		Transaction trx = new Transaction();
		try {
			trx.start();

			String name = (String) hash.get("name");
			String[] ecrOid = (String[]) hash.get("ecrOid");
			String[] purpose = (String[]) hash.get("purpose");
			String[] partOids = (String[]) hash.get("partOids");
			String[] partTopOids = (String[]) hash.get("partTopOids");
			String product = (String) hash.get("product");
			String customerEoNo = (String) hash.get("customerEoNo");
			String ecotype = (String) hash.get("ecotype");
			String devlevel = (String) hash.get("devlevel");
			String customer = (String) hash.get("customer");
			String applayDate = (String) hash.get("applayDate");
			String bulletpart = (String) hash.get("bulletpart");
			String diechangecomp = (String) hash.get("diechangecomp");
			String designcostpart = (String) hash.get("designcostpart");
			String wtchangepart = (String) hash.get("wtchangepart");
			String stockpart = (String) hash.get("stockpart");
			String description = (String) hash.get("description");
			String measures = (String) hash.get("measures");
			String prjNo = (String) hash.get("prjNo");
			String prjSeqNo = (String) hash.get("prjSeqNo");
			String unitCode = (String) hash.get("unitCode");
			String process = (String) hash.get("process");

			String[] approveUser = (String[]) hash.get("approveUser");
			String[] tempUser = (String[]) hash.get("tempUser");
			String[] files = (String[]) hash.get("files");
			String[] activity = (String[]) hash.get("activity");

			String[] approveUser2 = (String[]) hash.get("approveUser2");
			String[] approveUser3 = (String[]) hash.get("approveUser3");
			String[] approveUser4 = (String[]) hash.get("approveUser4");
			String[] approveUser5 = (String[]) hash.get("approveUser5");
			String discussType = (String) hash.get("discussType");
			String approval = (String) hash.get("approval");

			String[] approveUserLast2 = (String[]) hash.get("approveUserLast2");
			String[] approveUserLast3 = (String[]) hash.get("approveUserLast3");
			String[] approveUserLast4 = (String[]) hash.get("approveUserLast4");
			String[] approveUserLast5 = (String[]) hash.get("approveUserLast5");

			Hashtable lastHash = new Hashtable();

			if (approveUserLast2 != null)
				lastHash.put("approveUser2", approveUserLast2);
			if (approveUserLast3 != null)
				lastHash.put("approveUser3", approveUserLast3);
			if (approveUserLast4 != null)
				lastHash.put("approveUser4", approveUserLast4);
			if (approveUserLast5 != null)
				lastHash.put("approveUser5", approveUserLast5);
			if (discussType != null)
				lastHash.put("discussType", discussType);
			lastHash.put("approval", "true");

			EChangeOrder2 eco = EChangeOrder2.newEChangeOrder2();

			String number = "ECO-" + DateUtil.getCurrentDateString("month") + "-";
			String seqNo = SequenceDao.manager.getSeqNo(number, "0000", "EChangeOrder2", "orderNumber");
			number = number + seqNo;
			eco.setOrderNumber(number);

			eco.setName(name);
			if (ECR_NO.equals(process)) {
				if (approveUser4 == null) {
					eco.setOrderState(ECO_WORKING);
				} else {
					eco.setOrderState(ECO_ECA_WORKING);
				}

			} else {
				if (approveUser4 == null || approveUserLast4 == null) {
					eco.setOrderState(ECO_WORKING);
				} else {
					eco.setOrderState(ECO_BEFORE_APPROVING);
				}
			}

			eco.setContainer(WCUtil.getPDMLinkProduct());

			ReferenceFactory rf = new ReferenceFactory();

			String pp = "";
			for (int i = 0; i < purpose.length; i++) {
				pp += purpose[i] + ",";
			}
			eco.setPurpose(pp);

			eco.setEcoType(ecotype);
			eco.setApplyDate(applayDate);
			eco.setStockPart(stockpart);
			eco.setDescription(description);
			eco.setCustomerEoNo(customerEoNo);
			eco.setMeasures(measures);
			eco.setPrjNo(prjNo);
			eco.setPrjSeqNo(prjSeqNo);
			eco.setUnitCode(unitCode);
			eco.setProcess(process);

			eco.setOwner(SessionHelper.manager.getPrincipalReference());

			eco = (EChangeOrder2) PersistenceHelper.manager.save(eco);

			for (int i = 0; ecrOid != null && i < ecrOid.length; i++) {
				EChangeRequest2 ecr = (EChangeRequest2) rf.getReference(ecrOid[i]).getObject();
				RequestOrderLink link = RequestOrderLink.newRequestOrderLink(eco, ecr);
				PersistenceHelper.manager.save(link);
			}

			if (files != null) {
				for (int i = 0; i < files.length; i++) {
					CommonContentHelper.service.attach(eco, files[i]);
				}
			}
			boolean isApproval = "true".equals(approval);

//			System.out.println("::::::::::::: isApproval : " + isApproval);
			String activeType = ACTIVE_ROLE_CHIEF;
			if (ECR_NO.equals(process)) {
				activeType = ACTIVE_ROLE_WORKING;
				ChangeECOHelper.manager.registCompleteApproval(eco, hash);

			} else {
				if (approveUserLast4 != null) {
					isApproval = false;
				} else {
					isApproval = true;
				}
				ApprovalHelper.manager.registApproval(eco, null, approveUser2, approveUser3, approveUser4, tempUser,
						false, "create", discussType, isApproval);
				ChangeECOHelper.manager.registCompleteApproval(eco, lastHash);
			}

			/* 설변 품목 */
			for (int i = 0; partOids != null && i < partOids.length; i++) {

				WTPart part = (WTPart) rf.getReference(partOids[i]).getObject();
				String version = VersionControlHelper.getVersionIdentifier(part).getSeries().getValue();

				EcoPartLink link = EcoPartLink.newEcoPartLink((WTPartMaster) part.getMaster(), eco);
				link.setVersion(version);
				link.setPartType(PART_TYPE_PART);
				PersistenceHelper.manager.save(link);
			}

			/* TOP 품목 */
			for (int i = 0; partTopOids != null && i < partTopOids.length; i++) {

				WTPart part = (WTPart) rf.getReference(partTopOids[i]).getObject();
				String version = VersionControlHelper.getVersionIdentifier(part).getSeries().getValue();

				EcoPartLink link = EcoPartLink.newEcoPartLink((WTPartMaster) part.getMaster(), eco);
				link.setVersion(version);
				link.setPartType(PART_TYPE_TOP);
				PersistenceHelper.manager.save(link);
			}

			// activity set
			for (int i = 0; i < activity.length; i++) {
				String act = activity[i];
				EChangeActivityDefinition ead = (EChangeActivityDefinition) rf.getReference(act).getObject();

				String[] actData = (String[]) hash.get(act);
				String poid = actData[0];
				String finishDate = actData[1];

				People people = (People) rf.getReference(poid).getObject();
				EChangeActivity changeActivity = EChangeActivity.newEChangeActivity();
				changeActivity.setActiveState(ACTIVITY_STANDBY);
				changeActivity.setContainer(WCUtil.getPDMLinkProduct());
				changeActivity.setDefinition(ead);
				changeActivity.setOrder(eco);
				changeActivity.setFinishDate(DateUtil.convertDate(finishDate));
				changeActivity.setActiveType(activeType);
				WTUser user = people.getUser();
				WTPrincipalReference ref = WTPrincipalReference.newWTPrincipalReference(user);
				changeActivity.setOwner(ref);
				changeActivity = (EChangeActivity) PersistenceHelper.manager.save(changeActivity);

			}

			if (ECR_NO.equals(process)) {
				if (approveUser4 != null)
					this.startActivity(eco);

			}

			// 반려된 ECO의 EOEul가져오기
			String oldEcoOid = (String) hash.get("oldEcoOid");
			if (oldEcoOid != null && oldEcoOid.length() > 0) {

				QuerySpec qs = new QuerySpec(EOEul.class);
				qs.appendSearchCondition(new SearchCondition(EOEul.class, "ecoReference.key.id", "=",
						CommonUtil.getOIDLongValue(oldEcoOid)));

				QueryResult result = PersistenceHelper.manager.find(qs);
				while (result.hasMoreElements()) {
					EOEul oldEul = (EOEul) result.nextElement();

					EOEul eul = EOEul.newEOEul();
					eul.setTopAssyOid(oldEul.getTopAssyOid());
					eul.setXml(oldEul.getXml());
					eul.setEco(eco);
					eul.setOwner(SessionHelper.manager.getPrincipalReference());
					eul = (EOEul) wt.fc.PersistenceHelper.manager.save(eul);

					QueryResult qr = PersistenceHelper.manager.navigate(oldEul, "part", EulPartLink.class, false);
					while (qr.hasMoreElements()) {
						EulPartLink oldLink = (EulPartLink) qr.nextElement();
						WTPart part = oldLink.getPart();

						EulPartLink link = EulPartLink.newEulPartLink(part, eul);
						PersistenceHelper.manager.save(link);
					}
				}

			}

			trx.commit();
			trx = null;
		} catch (Exception e) {
			throw e;
		} finally {
			if (trx != null) {
				trx.rollback();
			}
		}
		return "등록 되었습니다.";
	}

	public String deleteEco(String oid) throws Exception {

		if (!SERVER) {
			Class argTypes[] = new Class[] { String.class };
			Object args[] = new Object[] { oid };
			try {
				return (String) wt.method.RemoteMethodServer.getDefault().invoke("deleteEco", null, this, argTypes,
						args);
			} catch (RemoteException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}

		Transaction trx = new Transaction();
		try {
			trx.start();
			if (oid != null) {
				ReferenceFactory f = new ReferenceFactory();
				EChangeOrder2 changeEco = (EChangeOrder2) f.getReference(oid).getObject();
				ApprovalHelper.manager.removeProcess(changeEco);
				PersistenceHelper.manager.delete(changeEco);
			}
			trx.commit();
			trx = null;
		} catch (Exception e) {
			throw e;
		} finally {
			if (trx != null) {
				trx.rollback();
			}
		}
		return "삭제 되었습니다.";
	}

	public String modifyEco(Hashtable hash) throws Exception {
		if (!SERVER) {
			Class argTypes[] = new Class[] { Hashtable.class };
			Object args[] = new Object[] { hash };
			try {
				return (String) wt.method.RemoteMethodServer.getDefault().invoke("modifyEco", null, this, argTypes,
						args);
			} catch (RemoteException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}

		Transaction trx = new Transaction();
		try {
			trx.start();

			String oid = (String) hash.get("oid");
			String name = (String) hash.get("name");
			String[] ecrOid = (String[]) hash.get("ecrOid");
			String[] purpose = (String[]) hash.get("purpose");
			String[] partOids = (String[]) hash.get("partOids");
			String[] partTopOids = (String[]) hash.get("partTopOids");
			String product = (String) hash.get("product");
			String customerEoNo = (String) hash.get("customerEoNo");
			String ecotype = (String) hash.get("ecotype");
			String devlevel = (String) hash.get("devlevel");
			String customer = (String) hash.get("customer");
			String applayDate = (String) hash.get("applayDate");
			String bulletpart = (String) hash.get("bulletpart");
			String diechangecomp = (String) hash.get("diechangecomp");
			String designcostpart = (String) hash.get("designcostpart");
			String wtchangepart = (String) hash.get("wtchangepart");
			String stockpart = (String) hash.get("stockpart");
			String description = (String) hash.get("description");
			String measures = (String) hash.get("measures");
			String prjNo = (String) hash.get("prjNo");
			String prjSeqNo = (String) hash.get("prjSeqNo");
			String unitCode = (String) hash.get("unitCode");
			String process = (String) hash.get("process");

			String[] approveUser = (String[]) hash.get("approveUser");
			String[] tempUser = (String[]) hash.get("tempUser");
			String[] files = (String[]) hash.get("files");
			String[] activity = (String[]) hash.get("activity");
			String[] secondaryDelFile = (String[]) hash.get("secondaryDelFile");

			String[] approveUser2 = (String[]) hash.get("approveUser2");
			String[] approveUser3 = (String[]) hash.get("approveUser3");
			String[] approveUser4 = (String[]) hash.get("approveUser4");
			String[] approveUser5 = (String[]) hash.get("approveUser5");
			String discussType = (String) hash.get("discussType");
			String approval = (String) hash.get("approval");

			String[] approveUserLast2 = (String[]) hash.get("approveUserLast2");
			String[] approveUserLast3 = (String[]) hash.get("approveUserLast3");
			String[] approveUserLast4 = (String[]) hash.get("approveUserLast4");
			String[] approveUserLast5 = (String[]) hash.get("approveUserLast5");

			Hashtable lastHash = new Hashtable();

			if (approveUserLast2 != null)
				lastHash.put("approveUser2", approveUserLast2);
			if (approveUserLast3 != null)
				lastHash.put("approveUser3", approveUserLast3);
			if (approveUserLast4 != null)
				lastHash.put("approveUser4", approveUserLast4);
			if (approveUserLast5 != null)
				lastHash.put("approveUser5", approveUserLast5);
			if (discussType != null)
				lastHash.put("discussType", discussType);
			lastHash.put("approval", "true");

			ReferenceFactory rf = new ReferenceFactory();

			EChangeOrder2 eco = (EChangeOrder2) rf.getReference(oid).getObject();

			EChangeOrder2 oldEco = eco;

			eco.setName(name);

			if (ECR_NO.equals(process)) {
				if (approveUser4 != null) {
					eco.setOrderState(ECO_ECA_WORKING);
				}

			} else {
				if (approveUser4 != null && approveUserLast4 != null) {
					eco.setOrderState(ECO_BEFORE_APPROVING);
				}
			}

			String pp = "";
			for (int i = 0; i < purpose.length; i++) {
				pp += purpose[i] + ",";
			}
			eco.setPurpose(pp);
			eco.setEcoType(ecotype);
			eco.setApplyDate(applayDate);
			eco.setStockPart(stockpart);
			eco.setDescription(description);
			eco.setMeasures(measures);
			eco.setCustomerEoNo(customerEoNo);
			eco.setPrjNo(prjNo);
			eco.setPrjSeqNo(prjSeqNo);
			eco.setUnitCode(unitCode);
			eco = (EChangeOrder2) PersistenceHelper.manager.modify(eco);

			// ecr
			QueryResult qr = PersistenceHelper.manager.navigate(eco, "request", RequestOrderLink.class, false);
			while (qr.hasMoreElements()) {
				RequestOrderLink doc = (RequestOrderLink) qr.nextElement();
				PersistenceHelper.manager.delete(doc);
			}
			for (int i = 0; ecrOid != null && i < ecrOid.length; i++) {
				EChangeRequest2 ecr = (EChangeRequest2) rf.getReference(ecrOid[i]).getObject();
				RequestOrderLink link = RequestOrderLink.newRequestOrderLink(eco, ecr);
				PersistenceHelper.manager.save(link);
			}

			// 첨부
			ContentHolder holder = ContentHelper.service.getContents(eco);
			Vector ofiles = ContentHelper.getApplicationData(holder);
			if (ofiles != null) {
				for (int i = 0; i < ofiles.size(); i++) {
					ApplicationData oad = (ApplicationData) ofiles.get(i);
					boolean flag = false;

					for (int j = 0; j < secondaryDelFile.length; j++) {
						String noid = secondaryDelFile[j];
						if (noid.equals(oad.getPersistInfo().getObjectIdentifier().toString())) {
							flag = true;
							break;
						}
					}

					if (!flag) {
						holder = CommonContentHelper.service.delete(holder, oad);
					}
				}
			}

			if (files != null) {
				for (int i = 0; i < files.length; i++) {
					CommonContentHelper.service.attach(eco, files[i]);
				}
			}

			QueryResult pqr = PersistenceHelper.manager.navigate(eco, "part", EcoPartLink.class, false);
			while (pqr.hasMoreElements()) {
				EcoPartLink link = (EcoPartLink) pqr.nextElement();
				PersistenceHelper.manager.delete(link);
			}

			/* 설변 품목 */
			for (int i = 0; partOids != null && i < partOids.length; i++) {

				WTPart part = (WTPart) rf.getReference(partOids[i]).getObject();
				String version = VersionControlHelper.getVersionIdentifier(part).getSeries().getValue();

				EcoPartLink link = EcoPartLink.newEcoPartLink((WTPartMaster) part.getMaster(), eco);
				link.setVersion(version);
				link.setPartType(PART_TYPE_PART);
				PersistenceHelper.manager.save(link);
			}

			/* TOP 품목 */
			for (int i = 0; partTopOids != null && i < partTopOids.length; i++) {

				WTPart part = (WTPart) rf.getReference(partTopOids[i]).getObject();
				String version = VersionControlHelper.getVersionIdentifier(part).getSeries().getValue();

				EcoPartLink link = EcoPartLink.newEcoPartLink((WTPartMaster) part.getMaster(), eco);
				link.setVersion(version);
				link.setPartType(PART_TYPE_TOP);
				PersistenceHelper.manager.save(link);
			}
			// activity set

			QueryResult aqr = PersistenceHelper.manager.navigate(eco, "activity", OrderActivityLink.class);

			while (aqr.hasMoreElements()) {
				EChangeActivity act = (EChangeActivity) aqr.nextElement();
				EChangeActivityDefinition def = act.getDefinition();
				PersistenceHelper.manager.delete(act);
			}
			boolean isApproval = "true".equals(approval);
			String activeType = ACTIVE_ROLE_CHIEF;

			if (ECR_NO.equals(process)) {
				activeType = ACTIVE_ROLE_WORKING;
				ChangeECOHelper.manager.registCompleteApproval(eco, hash);

			} else {
				if ( /* approveUser != null && */ approveUserLast4 != null) {
					isApproval = false;
				} else {
					isApproval = true;
				}
				ApprovalHelper.manager.registApproval(eco, null, approveUser2, approveUser3, approveUser4, tempUser,
						false, "create", discussType, isApproval);
				ChangeECOHelper.manager.registCompleteApproval(eco, lastHash);
			}

			for (int i = 0; i < activity.length; i++) {
				String act = activity[i];
				EChangeActivityDefinition ead = (EChangeActivityDefinition) rf.getReference(act).getObject();

				String[] actData = (String[]) hash.get(act);
				String poid = actData[0];
				String finishDate = actData[1];
				People people = (People) rf.getReference(poid).getObject();
				EChangeActivity changeActivity = EChangeActivity.newEChangeActivity();
				changeActivity.setActiveState(ACTIVITY_STANDBY);
				changeActivity.setContainer(WCUtil.getPDMLinkProduct());
				changeActivity.setDefinition(ead);
				changeActivity.setOrder(eco);
				changeActivity.setFinishDate(DateUtil.convertDate(finishDate));
				changeActivity.setActiveType(activeType);
				WTUser user = people.getUser();
				WTPrincipalReference ref = WTPrincipalReference.newWTPrincipalReference(user);
				changeActivity.setOwner(ref);
				changeActivity = (EChangeActivity) PersistenceHelper.manager.save(changeActivity);

			}

			if (ECR_NO.equals(process)) {
				if (approveUser4 != null /* && approveUserLast4 != null */ )
					this.startActivity(eco);
			}

			// boolean isApproval = "true".equals(approval);
			// ApprovalHelper.manager.registApproval(eco, oldEco, approveUser2,
			// approveUser3, approveUser4, tempUser, false, "update", discussType,
			// isApproval);
			// ApprovalHelper.manager.registApproval(doc, olddoc, approveUser2,
			// approveUser3, approveUser4, tempUser, false, "update", discussType,
			// tempFlag);
			trx.commit();
			trx = null;
		} catch (Exception e) {
			throw e;
		} finally {
			if (trx != null) {
				trx.rollback();
			}
		}
		return "수정 되었습니다.";
	}

	public void startActivity(EChangeOrder2 eco) throws Exception {
		if (!SERVER) {
			Class argTypes[] = new Class[] { EChangeOrder2.class };
			Object args[] = new Object[] { eco };
			try {
				wt.method.RemoteMethodServer.getDefault().invoke("startActivity", null, this, argTypes, args);
				return;
			} catch (RemoteException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}

		Transaction trx = new Transaction();
		try {
			trx.start();

			eco.setOrderState(ECO_ECA_WORKING);
			eco = (EChangeOrder2) PersistenceHelper.manager.modify(eco);

			ArrayList list = getFirstActivity(eco);

			if (list != null) {

				for (int i = 0; i < list.size(); i++) {

					Object[] o = (Object[]) list.get(i);

					EChangeActivityDefinition def = (EChangeActivityDefinition) o[0];
					EChangeActivity act = (EChangeActivity) o[1];

					act.setActiveState(ACTIVITY_WORKING);
					act = (EChangeActivity) PersistenceHelper.manager.modify(act);

					String[] activityUser = new String[1];
					activityUser[0] = act.getOwner().getPrincipal().getPersistInfo().getObjectIdentifier().toString();
					;

					ApprovalHelper.manager.registApproval(act, activityUser, null, true);
				}
			}

			trx.commit();
			trx = null;
		} catch (Exception e) {
			throw e;
		} finally {
			if (trx != null) {
				trx.rollback();
			}
		}
	}

	public void commitActivityParallel(EChangeActivity activity, String state) throws Exception {

		if (!SERVER) {
			Class argTypes[] = new Class[] { EChangeActivity.class, String.class };
			Object args[] = new Object[] { activity, state };
			try {
				wt.method.RemoteMethodServer.getDefault().invoke("commitActivityParallel", null, this, argTypes, args);
				return;
			} catch (RemoteException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}

		Transaction trx = new Transaction();
		try {
			trx.start();
			/* Activity 상태 Change */
			if (ApprovalHelper.MASTER_APPROVED.equals(state)) {
				activity.setActiveState(ChangeECOHelper.ACTIVITY_APPROVED);
			} else {
				activity.setActiveState(ChangeECOHelper.ACTIVITY_CANCELED);
			}

			activity = (EChangeActivity) PersistenceHelper.manager.modify(activity);

			/* ECR_NO, ECR_EXIST */
			EChangeOrder2 eco = activity.getOrder();
			String activeType = activity.getActiveType();

			if (eco.getProcess().equals(ECR_NO)) {

				ArrayList list = this.getWorkingActivity(eco, activeType);
				if (list.size() == 0) {
					ChangeECOHelper.manager.startECOApprove(eco);
				}
			} else {

				if (activity.getActiveType().equals(ChangeECOHelper.ACTIVE_ROLE_CHIEF)) { // 팀장 인 경우 Working Setup

					EChangeActivity ecaWorking = ChangeECOHelper.manager.getECAWorking(activity);
					String[] activityUser = new String[1];
					activityUser[0] = ecaWorking.getOwner().getPrincipal().getPersistInfo().getObjectIdentifier()
							.toString();
					;
					ApprovalHelper.manager.registApproval(ecaWorking, activityUser, null, true);

					ecaWorking.setActiveState(ChangeECOHelper.ACTIVITY_WORKING);
					ecaWorking = (EChangeActivity) PersistenceHelper.manager.modify(ecaWorking);

				} else {
					/* 작업자인 경우 팀장이 작업완료 이고 작업자들도 모두 작업완료인 경우 최종 결재 START */
					ArrayList chiefList = this.getWorkingActivity(eco, ChangeECOHelper.ACTIVE_ROLE_CHIEF);

					if (chiefList.size() == 0) {
						ArrayList workingList = this.getWorkingActivity(eco, ChangeECOHelper.ACTIVE_ROLE_WORKING);
						if (workingList.size() == 0)
							ChangeECOHelper.manager.startECOApprove(eco);
					}
				}

			}

			trx.commit();
			trx = null;
		} catch (Exception e) {
			throw e;
		} finally {
			if (trx != null) {
				trx.rollback();
			}
		}

	}

	public void commitActivity(EChangeActivity activity, String state) throws Exception {
		if (!SERVER) {
			Class argTypes[] = new Class[] { EChangeActivity.class, String.class };
			Object args[] = new Object[] { activity, state };
			try {
				wt.method.RemoteMethodServer.getDefault().invoke("commitActivity", null, this, argTypes, args);
				return;
			} catch (RemoteException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}

		Transaction trx = new Transaction();
		try {
			trx.start();

			if (ApprovalHelper.MASTER_APPROVED.equals(state)) {
				activity.setActiveState(ChangeECOHelper.ACTIVITY_APPROVED);
			} else {
				activity.setActiveState(ChangeECOHelper.ACTIVITY_CANCELED);
			}

			activity = (EChangeActivity) PersistenceHelper.manager.modify(activity);

			if (checkStepComplete(activity)) {

				ArrayList list = getNextActivity(activity);

				if (list != null && list.size() > 0) {

					for (int i = 0; i < list.size(); i++) {
						Object[] o = (Object[]) list.get(i);

						EChangeActivityDefinition def = (EChangeActivityDefinition) o[0];
						EChangeActivity act = (EChangeActivity) o[1];

						act.setActiveState(ACTIVITY_WORKING);
						act = (EChangeActivity) PersistenceHelper.manager.modify(act);

						String[] activityUser = new String[1];
						activityUser[0] = act.getOwner().getPrincipal().getPersistInfo().getObjectIdentifier()
								.toString();
						;

						ApprovalHelper.manager.registApproval(act, activityUser, null, true);
					}
				} else {
					EChangeOrder2 eco = activity.getOrder();

					CommonActivity ca = CommonActivity.newCommonActivity();
					ca.setGubun(ACTIVE_WORK_COMPLETE);
					ca.setTitle(ACTIVE_WORK_COMPLETE_TITLE);
					ca.setOwner(SessionHelper.manager.getPrincipalReference());
					ca = (CommonActivity) PersistenceHelper.manager.save(ca);

					String[] activityUser = new String[1];
					activityUser[0] = eco.getOwner().getPrincipal().getPersistInfo().getObjectIdentifier().toString();
					// ApprovalHelper.manager.registApproval(ca, null, null, null, null, null,
					// false, "create", "parallel", true);
					ApprovalHelper.manager.registApproval(ca, activityUser, null, true);

					eco.setCompleteWork(ca);
					eco = (EChangeOrder2) PersistenceHelper.manager.modify(eco);

				}
			}
			trx.commit();
			trx = null;
		} catch (Exception e) {
			throw e;
		} finally {
			if (trx != null) {
				trx.rollback();
			}
		}
	}

	public boolean checkStepComplete(EChangeActivity activity) throws Exception {

		String step = activity.getDefinition().getStep();

		QuerySpec qs = new QuerySpec();
		int ii = qs.addClassList(EChangeActivityDefinition.class, false);
		int jj = qs.addClassList(EChangeActivity.class, false);
		qs.appendSelect(new ClassAttribute(EChangeActivity.class, "thePersistInfo.theObjectIdentifier.id"),
				new int[] { jj }, false);
		qs.appendWhere(new SearchCondition(EChangeActivityDefinition.class, "thePersistInfo.theObjectIdentifier.id",
				EChangeActivity.class, "definitionReference.key.id"), new int[] { ii, jj });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(EChangeActivity.class, "orderReference.key.id", "=",
				activity.getOrder().getPersistInfo().getObjectIdentifier().getId()), new int[] { jj });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(EChangeActivityDefinition.class, "step", "=", step), new int[] { ii });
		qs.appendAnd();
		qs.appendOpenParen();
		qs.appendWhere(new SearchCondition(EChangeActivity.class, EChangeActivity.ACTIVE_STATE, "=",
				ChangeECOHelper.ACTIVITY_APPROVING), new int[] { jj });
		qs.appendOr();
		qs.appendWhere(new SearchCondition(EChangeActivity.class, EChangeActivity.ACTIVE_STATE, "=",
				ChangeECOHelper.ACTIVITY_STANDBY), new int[] { jj });
		qs.appendOr();
		qs.appendWhere(new SearchCondition(EChangeActivity.class, EChangeActivity.ACTIVE_STATE, "=",
				ChangeECOHelper.ACTIVITY_WORKING), new int[] { jj });
		qs.appendCloseParen();

		QueryResult result = PersistenceHelper.manager.find(qs);

		return result.size() == 0;

	}

	public ArrayList getNextActivity(EChangeActivity activity) throws Exception {
		QuerySpec qs = new QuerySpec();
		int ii = qs.addClassList(EChangeActivityDefinition.class, true);
		int jj = qs.addClassList(EChangeActivity.class, true);

		qs.appendWhere(new SearchCondition(EChangeActivityDefinition.class, "thePersistInfo.theObjectIdentifier.id",
				EChangeActivity.class, "definitionReference.key.id"), new int[] { ii, jj });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(EChangeActivity.class, "orderReference.key.id", "=",
				activity.getOrder().getPersistInfo().getObjectIdentifier().getId()), new int[] { jj });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(EChangeActivityDefinition.class, "sortNumber", ">",
				activity.getDefinition().getSortNumber()), new int[] { ii });
		qs.appendAnd();
		qs.appendWhere(
				new SearchCondition(EChangeActivityDefinition.class, "step", "<>", activity.getDefinition().getStep()),
				new int[] { ii });

		qs.appendOrderBy(new OrderBy(new ClassAttribute(EChangeActivityDefinition.class, "sortNumber"), false),
				new int[] { ii });

		QueryResult result = PersistenceHelper.manager.find(qs);

		ArrayList list = new ArrayList();
		String firstStep = null;
		while (result.hasMoreElements()) {
			Object[] o = (Object[]) result.nextElement();
			EChangeActivityDefinition ed = (EChangeActivityDefinition) o[0];
			String step = ed.getStep();
			if (firstStep == null) {
				firstStep = step;
			}
			if (step.equals(firstStep)) {
				list.add(o);
			} else {
				break;
			}
		}
		return list;
	}

	public ArrayList getWorkingActivity(EChangeOrder2 eco, String activeType) throws Exception {

		QuerySpec qs = new QuerySpec(EChangeActivity.class);

		qs.appendWhere(new SearchCondition(EChangeActivity.class, "orderReference.key.id", "=",
				eco.getPersistInfo().getObjectIdentifier().getId()), new int[] { 0 });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(EChangeActivity.class, "activeState", "=", ACTIVITY_WORKING),
				new int[] { 0 });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(EChangeActivity.class, "activeType", "=", activeType), new int[] { 0 });

		QueryResult result = PersistenceHelper.manager.find(qs);
		ArrayList list = new ArrayList();
		while (result.hasMoreElements()) {

			EChangeActivity activity = (EChangeActivity) result.nextElement();

			list.add(activity);
		}

		return list;
	}

	public ArrayList getFirstActivity(EChangeOrder2 eco) throws Exception {
		QuerySpec qs = new QuerySpec();
		int ii = qs.addClassList(EChangeActivityDefinition.class, true);
		int jj = qs.addClassList(EChangeActivity.class, true);

		qs.appendWhere(new SearchCondition(EChangeActivityDefinition.class, "thePersistInfo.theObjectIdentifier.id",
				EChangeActivity.class, "definitionReference.key.id"), new int[] { ii, jj });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(EChangeActivity.class, "orderReference.key.id", "=",
				eco.getPersistInfo().getObjectIdentifier().getId()), new int[] { jj });

		qs.appendOrderBy(new OrderBy(new ClassAttribute(EChangeActivityDefinition.class, "sortNumber"), false),
				new int[] { 0 });
		QueryResult result = PersistenceHelper.manager.find(qs);

		ArrayList list = new ArrayList();
		String firstStep = null;
		while (result.hasMoreElements()) {
			Object[] o = (Object[]) result.nextElement();
			EChangeActivityDefinition ed = (EChangeActivityDefinition) o[0];
			String step = ed.getStep();
			if (firstStep == null) {
				firstStep = step;
			}
			if (step.equals(firstStep)) {
				list.add(o);
			} else {
				break;
			}
		}
		return list;
	}

	public EChangeOrder2 registCompleteApproval(EChangeOrder2 eco, Hashtable hash) throws Exception {
		if (!SERVER) {
			Class argTypes[] = new Class[] { EChangeOrder2.class, Hashtable.class };
			Object args[] = new Object[] { eco, hash };
			try {
				return (EChangeOrder2) wt.method.RemoteMethodServer.getDefault().invoke("registCompleteApproval", null,
						this, argTypes, args);
			} catch (RemoteException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}

		Transaction trx = new Transaction();
		try {
			trx.start();

			String[] approveUser2 = (String[]) hash.get("approveUser2");
			String[] approveUser3 = (String[]) hash.get("approveUser3");
			String[] approveUser4 = (String[]) hash.get("approveUser4");
			String[] approveUser5 = (String[]) hash.get("approveUser5");
			String[] tempUser = (String[]) hash.get("tempUser");
			String discussType = (String) hash.get("discussType");
			String approval = (String) hash.get("approval");

			CommonActivity ca = CommonActivity.newCommonActivity();
			ca.setGubun(ACTIVE_WORK_LAST_APPROVAL);
			ca.setTitle(eco.getName());
			ca.setOwner(SessionHelper.manager.getPrincipalReference());
			ca = (CommonActivity) PersistenceHelper.manager.save(ca);

			eco.setComplete(ca);
			eco = (EChangeOrder2) PersistenceHelper.manager.modify(eco);

			boolean isApproval = "true".equals(approval);

			ApprovalHelper.manager.registApproval(ca, null, approveUser2, approveUser3, approveUser4, tempUser, false,
					"create", discussType, isApproval);

			trx.commit();
			trx = null;

			return eco;

		} catch (Exception e) {
			throw e;
		} finally {
			if (trx != null) {
				trx.rollback();
			}
		}
	}

	public EChangeOrder2 updateCompleteApproval(EChangeOrder2 eco, Hashtable hash) throws Exception {
		if (!SERVER) {
			Class argTypes[] = new Class[] { EChangeOrder2.class, Hashtable.class };
			Object args[] = new Object[] { eco, hash };
			try {
				return (EChangeOrder2) wt.method.RemoteMethodServer.getDefault().invoke("updateCompleteApproval", null,
						this, argTypes, args);
			} catch (RemoteException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}

		Transaction trx = new Transaction();
		try {
			trx.start();

			String[] approveUser2 = (String[]) hash.get("approveUser2");
			String[] approveUser3 = (String[]) hash.get("approveUser3");
			String[] approveUser4 = (String[]) hash.get("approveUser4");
			String[] approveUser5 = (String[]) hash.get("approveUser5");
			String[] tempUser = (String[]) hash.get("tempUser");
			String discussType = (String) hash.get("discussType");
			String approval = (String) hash.get("approval");

			CommonActivity ca = eco.getComplete();

			CommonActivity oldCa = ca;

			ca.setGubun(ACTIVE_WORK_LAST_APPROVAL);
			ca.setTitle(eco.getName());
			ca.setOwner(SessionHelper.manager.getPrincipalReference());
			ca = (CommonActivity) PersistenceHelper.manager.save(ca);

			eco.setOrderState(ORDER_COMPLETE);
			eco = (EChangeOrder2) PersistenceHelper.manager.modify(eco);

			boolean isApproval = "true".equals(approval);
			ApprovalHelper.manager.registApproval(ca, oldCa, approveUser2, approveUser3, approveUser4, tempUser, false,
					"update", discussType, isApproval);

			trx.commit();
			trx = null;

			return eco;

		} catch (Exception e) {
			throw e;
		} finally {
			if (trx != null) {
				trx.rollback();
			}
		}
	}

	public String approveEul(Hashtable hash) throws Exception {

		if (!SERVER) {
			Class argTypes[] = new Class[] { Hashtable.class };

			Object args[] = new Object[] { hash };
			try {
				return (String) wt.method.RemoteMethodServer.getDefault().invoke("approveEul", null, this, argTypes,
						args);
			} catch (RemoteException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}

		Transaction trx = new Transaction();
		try {
			trx.start();

			String oid = (String) hash.get("oid");

			String[] approveUser2 = (String[]) hash.get("approveUser2");
			String[] approveUser3 = (String[]) hash.get("approveUser3");
			String[] approveUser4 = (String[]) hash.get("approveUser4");
			String[] tempUser = (String[]) hash.get("tempUser");
			String discussType = (String) hash.get("discussType");
			String approval = (String) hash.get("approval");

			ReferenceFactory rf = new ReferenceFactory();
			EOEul eul = (EOEul) rf.getReference(oid).getObject();

			boolean isApproval = "true".equals(approval);

			String gubun = "create";
			ApprovalMaster appMaster = ApprovalHelper.manager.getApprovalMaster(eul);
			if (appMaster != null) {
				gubun = "update";
			}

			ApprovalHelper.manager.registApproval(eul, eul, approveUser2, approveUser3, approveUser4, tempUser, false,
					gubun, discussType, isApproval);

			trx.commit();
			trx = null;
		} catch (Exception e) {
			throw e;
		} finally {
			if (trx != null) {
				trx.rollback();
			}
		}
		return "제출 되었습니다.";
	}

	public void commitEOEul(EOEul eul, String state) throws Exception {
		// if(ApprovalHelper.MASTER_APPROVED.equals(state)){
		saveEulData(eul);
		// }
	}

	private InputStream stringToInputStream(String xml) {
		ByteArrayInputStream bis = new ByteArrayInputStream(xml.getBytes());
		InputStream ins = new BufferedInputStream(bis);
		return ins;
	}

	public Hashtable getSapData(EOEul eul) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		DocumentBuilder documentbuilder = factory.newDocumentBuilder();
		InputStream inStream = stringToInputStream(eul.getXml().getContents());
		Document document = documentbuilder.parse(inStream);
		inStream.close();

		return EditorServerHelper.manager.getSapData(document);
	}

	public void saveEulData(EOEul eul) throws Exception {

		try {

			Hashtable hash = getSapData(eul);

			EChangeOrder2 order = eul.getEco();
			String purpose = order.getPurpose();
			// boolean revision = purpose.indexOf("BOM") < 0;
			boolean revision = false;
			saveChangeData(hash, revision, eul);

		} catch (Exception ex) {
			throw new WTException(ex);
		}
	}

	public void saveChangeData(Hashtable hash, boolean revision, EOEul eul) {

		ApplyHistory history = null;

		try {

			history = ApplyHistory.newApplyHistory();
			history.setOwner(SessionHelper.manager.getPrincipalReference());
			if (eul != null) {
				history.setEul(eul);
			}
			history = (ApplyHistory) PersistenceHelper.manager.save(history);

//		    System.out.println("==설계변경 적용 시작==");
//		    System.out.println("revision : " + revision);

			Enumeration en = hash.keys();

			boolean flag = true;

			if (eul != null) {
				ReferenceFactory rf = new ReferenceFactory();
				WTPart part = (WTPart) rf.getReference(eul.getTopAssyOid()).getObject();
				createBaseline(part, eul);
			}

			while (en.hasMoreElements()) {
				String key = (String) en.nextElement();
				EOActionTempAssyData eta = (EOActionTempAssyData) hash.get(key);
				if (!applyChangeData(eul, eta, revision, history)) {
					flag = false;
				}
			}

			if (!flag) {
				history.setStatus("ERROR");
				history.setError("Item Error");
			} else {
				history.setStatus("SUCCESS");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			try {
				history.setStatus("ERROR");
				history.setError(ex.getLocalizedMessage());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} finally {
			if (history != null) {
				try {
					PersistenceHelper.manager.modify(history);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

//	 public SapEcmBean getSapEoNumber(EChangeOrder2 eco)throws Exception{
//		 
//		 SAPDelegate del = new SAPDelegate();
//		 return del.getEoNumber(eco.getOrderNumber());
//	 }
	public boolean applyChangeData(EOEul eul, EOActionTempAssyData data, boolean revision, ApplyHistory history)
			throws Exception {

		ApplyChangeState changeState = null;
		WTUser user = null;
		try {
			changeState = ApplyChangeState.newApplyChangeState();
			changeState.setHistory(history);
			changeState.setAssyNumber(data.assyPart);
			changeState.setAssyVersion(data.nextAssyVersion);
			changeState = (ApplyChangeState) PersistenceHelper.manager.save(changeState);

			user = (WTUser) SessionHelper.manager.getPrincipal();
			SessionHelper.manager.setPrincipal("wcadmin");
			appleyChangeData(eul, data, revision);

			changeState.setStatus("SUCCESS");
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			changeState.setStatus("ERROR");
			changeState.setError(e.getLocalizedMessage());
			return false;
		} finally {
			if (changeState != null) {
				try {
					PersistenceHelper.manager.modify(changeState);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			if (user != null) {
				SessionHelper.manager.setPrincipal(user.getName());
			}
		}
	}

	public ApplyChangeState applyChangeData(EOEul eul, EOActionTempAssyData data, boolean revision,
			ApplyChangeState changeState) throws Exception {

		if (!SERVER) {
			Class argTypes[] = new Class[] { EOEul.class, EOActionTempAssyData.class, boolean.class,
					ApplyChangeState.class };

			Object args[] = new Object[] { eul, data, new Boolean(revision), changeState };
			try {
				return (ApplyChangeState) wt.method.RemoteMethodServer.getDefault().invoke("applyChangeData", null,
						this, argTypes, args);
			} catch (RemoteException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}

		WTUser user = null;

		try {
			changeState.setAssyNumber(data.assyPart);
			changeState.setAssyVersion(data.nextAssyVersion);

			user = (WTUser) SessionHelper.manager.getPrincipal();
			SessionHelper.manager.setPrincipal("wcadmin");

			WTPart part = appleyChangeData(eul, data, revision);

			changeState.setStatus("SUCCESS");
			changeState.setError("");

			try {
				ManagedBaseline baseline = (ManagedBaseline) eul.getBaseline();
				BaselineHelper.service.addToBaseline(part, baseline);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			changeState.setStatus("ERROR");
			changeState.setError(e.getLocalizedMessage());

		} finally {
			if (changeState != null) {
				try {
					changeState = (ApplyChangeState) PersistenceHelper.manager.modify(changeState);

					ApplyHistory ah = changeState.getHistory();
					if (isAllSuccess(ah)) {
						ah.setStatus("SUCCESS");
						PersistenceHelper.manager.modify(ah);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			if (user != null) {
				SessionHelper.manager.setPrincipal(user.getName());
			}
		}

		return changeState;
	}

	public boolean isAllSuccess(ApplyHistory ah) throws Exception {
		QuerySpec qs = new QuerySpec(ApplyChangeState.class);
		qs.appendWhere(new SearchCondition(ApplyChangeState.class, "historyReference.key.id", "=",
				ah.getPersistInfo().getObjectIdentifier().getId()), new int[] { 0 });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(ApplyChangeState.class, "status", "=", "ERROR"), new int[] { 0 });
		QueryResult qr = PersistenceHelper.manager.find(qs);
		return qr.size() == 0;
	}

	public void cancelChangeData(EOEul eul, ApplyChangeState acs) throws Exception {
		if (!SERVER) {
			Class argTypes[] = new Class[] { EOEul.class, ApplyChangeState.class };

			Object args[] = new Object[] { eul, acs };
			try {
				wt.method.RemoteMethodServer.getDefault().invoke("cancelChangeData", null, this, argTypes, args);
				return;
			} catch (RemoteException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}

		WTUser user = null;

		Transaction trx = new Transaction();
		try {
			trx.start();

			QueryResult qr = PersistenceHelper.manager.navigate(eul, "part", EulPartLink.class, false);

			while (qr.hasMoreElements()) {
				EulPartLink link = (EulPartLink) qr.nextElement();
				link.setLinkType(1);
				PersistenceHelper.manager.modify(link);
			}

			acs.setStatus("CANCEL");
			acs = (ApplyChangeState) PersistenceHelper.manager.modify(acs);

			ApplyHistory ah = acs.getHistory();
			if (isAllSuccess(ah)) {
				ah.setStatus("SUCCESS");
				PersistenceHelper.manager.modify(ah);
			}

			trx.commit();
			trx = null;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (trx != null) {
				trx.rollback();
			}
		}
	}

	public void reApply(EOEul eul, ApplyChangeState acs) throws Exception {
		if (!SERVER) {
			Class argTypes[] = new Class[] { EOEul.class, ApplyChangeState.class };

			Object args[] = new Object[] { eul, acs };
			try {
				wt.method.RemoteMethodServer.getDefault().invoke("reApply", null, this, argTypes, args);
				return;
			} catch (RemoteException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}

		WTUser user = null;

		Transaction trx = new Transaction();
		try {
			trx.start();

			QueryResult qr = PersistenceHelper.manager.navigate(eul, "part", EulPartLink.class, false);

			while (qr.hasMoreElements()) {
				EulPartLink link = (EulPartLink) qr.nextElement();
				link.setLinkType(0);
				PersistenceHelper.manager.modify(link);
			}

			acs.setStatus("ERROR");
			acs = (ApplyChangeState) PersistenceHelper.manager.modify(acs);

			ApplyHistory ah = acs.getHistory();
			ah.setStatus("ERROR");
			PersistenceHelper.manager.modify(ah);

			trx.commit();
			trx = null;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (trx != null) {
				trx.rollback();
			}
		}
	}

	public WTPart appleyChangeData(EOEul eul, EOActionTempAssyData data, boolean revision) throws Exception {

		ArrayList list = data.itemList;

		WTPart assy = (WTPart) EulPartHelper.manager.getPart(data.assyPart);

		boolean action = false;

		try {

			Folder folder = FolderHelper.service.getFolder(assy);

			if (revision) {
				assy = (WTPart) VersionControlHelper.service.newVersion(assy);
				Folder pfolder = FolderHelper.service.getPersonalCabinet(SessionHelper.manager.getPrincipal());
				FolderHelper.assignLocation((FolderEntry) assy, pfolder);
				assy = (WTPart) PersistenceHelper.manager.save(assy);
				action = true;
			} else {
				if (!WorkInProgressHelper.isCheckedOut(assy)) {
					Folder cfolder = WorkInProgressHelper.service.getCheckoutFolder();
					CheckoutLink link = CheckInOutTaskLogic.checkOutObject(assy, cfolder, "");
					assy = (WTPart) link.getWorkingCopy();
				} else {
					assy = (WTPart) WorkInProgressHelper.service.workingCopyOf(assy);
				}
				action = true;
			}

			if (data.stdQuantity != null && !data.stdQuantity.equals(data.orgStdQuantity)) {
				IBAUtil.changeIBAValue(assy, "STD_QUANTITY", data.stdQuantity);
			}

			for (int i = 0; i < list.size(); i++) {
				EOActionTempItemData idata = (EOActionTempItemData) list.get(i);

				if ("I".equals(idata.editType)) {

//			    			System.out.println("I >> " + idata.newPart);
					WTPart item = (WTPart) EulPartHelper.manager.getPart(idata.newPart);
					WTPartUsageLink link = WTPartUsageLink.newWTPartUsageLink(assy, (WTPartMaster) item.getMaster());
					link.setQuantity(Quantity.newQuantity(Double.parseDouble(idata.newQuantity),
							QuantityUnit.toQuantityUnit(idata.newUnit)));
					link = (WTPartUsageLink) PersistenceHelper.manager.save(link);
					// IBAUtil.changeIBAValue(link, "ItemSeq", idata.newItemSeq);
				} else if ("D".equals(idata.editType)) {

//			    			System.out.println("D >> " + idata.oldPart);
					WTPart item = (WTPart) EulPartHelper.manager.getPart(idata.oldPart);

					// System.out.println(item+","+assy+","+idata.oldItemSeq);
					WTPartUsageLink link = BEContext.getLink(item, assy, idata.oldItemSeq);
					if (link == null)
						continue;
//			    			System.out.println(link);

					PersistenceHelper.manager.delete(link);
				} else if ("C".equals(idata.editType)) {

//			    			System.out.println("C >> " + idata.oldPart + ">" + idata.newPart);
					WTPart oitem = (WTPart) EulPartHelper.manager.getPart(idata.oldPart);
					WTPartUsageLink link = BEContext.getLink(oitem, assy, idata.oldItemSeq);

					WTPart item = (WTPart) EulPartHelper.manager.getPart(idata.newPart);
					link.setUses(item.getMaster());
					link.setQuantity(Quantity.newQuantity(Double.parseDouble(idata.newQuantity),
							QuantityUnit.toQuantityUnit(idata.newUnit)));
					link = (WTPartUsageLink) PersistenceHelper.manager.modify(link);
					// IBAUtil.changeIBAValue(link, "ItemSeq", idata.newItemSeq);
				}
			}

			// sendSap(eul,data);

			if (revision) {
				assy = (WTPart) FolderHelper.service.changeFolder(assy, folder);
				action = false;
			} else {
				assy = (WTPart) WorkInProgressHelper.service.checkin(assy, "");
				action = false;
			}
			return assy;

		} catch (Exception ex) {
			throw new WTException(ex);
		} finally {
			if (action) {
				if (revision) {
					PersistenceHelper.manager.delete(assy);
				} else {
					WorkInProgressHelper.service.undoCheckout(assy);
				}
			}
		}
	}

	public void sendSap(EOEul eul, EOActionTempAssyData data) throws Exception {

		EChangeOrder2 eco = eul.getEco();
		// SAPDelegate sAPDelegate = new SAPDelegate();
		// sAPDelegate.createModifyBOM(eco, data);

	}

	public ManagedBaseline createBaseline(WTPart wtpart, EOEul eulb) throws Exception {
		Date date = new Date();
		String baselineName = "EO Baseline : " + wtpart.getNumber() + " : " + date;

		WTProperties wtproperties = WTProperties.getLocalProperties();
		String s = "/Default/Baseline";
		String s2 = wtproperties.getProperty("baseline.lifecycle");

		Folder folder = null;
		LifeCycleTemplate lifecycletemplate = null;

		if (s != null)
			folder = FolderHelper.service.getFolder(s, WCUtil.getWTContainerRef());
		else
			folder = FolderTaskLogic.getFolder(wtpart.getLocation(), WCUtil.getWTContainerRef());

		if (s2 != null)
			lifecycletemplate = LifeCycleHelper.service.getLifeCycleTemplate(s2, WCUtil.getWTContainerRef());
		else
			lifecycletemplate = (LifeCycleTemplate) wtpart.getLifeCycleTemplate().getObject();

		ManagedBaseline mb = BEContext.createBaseline(wtpart, baselineName, folder, lifecycletemplate);

		if (eulb != null) {
			eulb = (EOEul) PersistenceHelper.manager.refresh(eulb);
			eulb.setBaseline(mb);
			eulb = (EOEul) PersistenceHelper.manager.modify(eulb);
		}
		return mb;
	}

	public EChangeOrder2 getEO(ManagedBaseline bl) throws Exception {
		QueryResult qr = PersistenceHelper.manager.navigate(bl, "eul", EulBaselineLink.class);
		if (qr.hasMoreElements()) {
			EOEul eul = (EOEul) qr.nextElement();
			return eul.getEco();
		}
		return null;
	}

	public String rework(String oid) throws Exception {
		if (!SERVER) {
			Class argTypes[] = new Class[] { String.class };
			Object args[] = new Object[] { oid };
			try {
				return (String) wt.method.RemoteMethodServer.getDefault().invoke("rework", null, this, argTypes, args);
			} catch (RemoteException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}

		Transaction trx = new Transaction();
		try {
			trx.start();
			if (oid != null) {
				ReferenceFactory f = new ReferenceFactory();
				EChangeOrder2 changeEco = (EChangeOrder2) f.getReference(oid).getObject();

				if (!ApprovalHelper.manager.isAccessModify(changeEco.getComplete(), changeEco.getOwner().getName())) {
					throw new WTException("복구 할 수 없는 상태 입니다");
				}
				changeEco.setOrderState(ORDER_WORK);
				PersistenceHelper.manager.modify(changeEco);
			}
			trx.commit();
			trx = null;
		} catch (Exception e) {
			throw e;
		} finally {
			if (trx != null) {
				trx.rollback();
			}
		}
		return "작업단계로 복구 되었습니다";
	}

	public String getLocaleString(String ss) {

		if (CommonUtil.isUSLocale()) {
			if (ORDER_REGIST.equals(ss))
				ss = "Regist"; // 등록
			else if (ORDER_WORK.equals(ss))
				ss = "Working"; // 진행
			else if (ORDER_COMPLETE.equals(ss))
				ss = "Complete"; // 완료

			else if (ACTIVITY_STANDBY.equals(ss))
				ss = "Standing"; // 대기중
			else if (ACTIVITY_WORKING.equals(ss))
				ss = "Working"; // 작업중
			else if (ACTIVITY_APPROVING.equals(ss))
				ss = "Review"; // 승인중
			else if (ACTIVITY_CANCELED.equals(ss))
				ss = "Rejected"; // 반려됨
			else if (ACTIVITY_APPROVED.equals(ss))
				ss = "Complete"; // 작업완료

			else if (ACTIVE_WORK_LAST_APPROVAL.equals(ss))
				ss = "Approve EO"; // EO최종결재
			else if (ACTIVE_WORK_COMPLETE.equals(ss))
				ss = "Complete EO"; // EO완료
			else if (ACTIVE_WORK_REGIST.equals(ss))
				ss = "Regist EO"; // EO등록

			else if (ACTIVE_WORK_REGIST_TITLE.equals(ss))
				ss = "Please register a new EO"; // 신규 EO 를 등록해 주십시오
			else if (ACTIVE_WORK_COMPLETE_TITLE.equals(ss))
				ss = "All EO activities have been completed"; // EO활동이 모두 완료 되었습니다

			else if (ApprovalHelper.CENCEL_WORK.equals(ss))
				ss = "Rejected/Rework";

		}
		return ss;
	}

	/*********************************************
	 * Narae ADD
	 *************************************************/
	// ECO ECA에 working add
	public boolean addWorkingActive(HashMap map) {

		String eadOid = (String) map.get("eadOid");
		String ecoOid = (String) map.get("ecoOid");
		String ecaOid = (String) map.get("ecaOid");
		String poid = (String) map.get("poid");
		String finishDate = (String) map.get("finishDate");
		String eaOid = "";
		EChangeActivity ea = (EChangeActivity) CommonUtil.getObject(ecaOid);

		boolean isAdd = false;
		try {

			// EChangeActivity ADD
			EChangeActivityDefinition ead = (EChangeActivityDefinition) CommonUtil.getObject(eadOid);
			EChangeOrder2 eco = (EChangeOrder2) CommonUtil.getObject(ecoOid);
			People people = (People) CommonUtil.getObject(poid);

			EChangeActivity changeActivity = EChangeActivity.newEChangeActivity();
			changeActivity.setActiveState(ACTIVITY_STANDBY);// "대기중",ACTIVITY_STANDBY
			changeActivity.setContainer(WCUtil.getPDMLinkProduct());
			changeActivity.setDefinition(ead);
			changeActivity.setOrder(eco);
			changeActivity.setFinishDate(DateUtil.convertDate(finishDate));
			changeActivity.setActiveType(ACTIVE_ROLE_WORKING); // "WORKING" ACTIVE_TYPE_CHIEF
			WTUser user = people.getUser();
			WTPrincipalReference ref = WTPrincipalReference.newWTPrincipalReference(user);
			changeActivity.setOwner(ref);

			changeActivity = (EChangeActivity) PersistenceHelper.manager.save(changeActivity);
//				System.out.println(">>>>>>>>>>> changeActivity1" + changeActivity);
			isAdd = true;
			// ApprovalLine Add

			/*
			 * if(changeActivity != null){ System.out.println(">>>>>>>>>>> changeActivity2"
			 * + changeActivity); ApprovalMaster appMaster =
			 * ApprovalHelper.manager.searchApprovalMaster((Object)ea);//getApprovalMaster(
			 * changeActivity); System.out.println(">>>>>>>>>>> appMaster1" + appMaster);
			 * 
			 * ApprovalHelper.manager.addApprovalLine(appMaster,
			 * ApprovalHelper.WORKING_WORKING, user.getName(), ApprovalHelper.LINE_STANDING,
			 * 2); System.out.println(">>>>>>>>>>> appMaster2" + appMaster); return true; }
			 * 
			 */
			// eaOid = CommonUtil.getOIDString(changeActivity);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return isAdd;
	}

	public boolean delWorkingActive(String eaOid) {
		EChangeActivity act = (EChangeActivity) CommonUtil.getObject(eaOid);
		boolean isDel = false;
		try {
			PersistenceHelper.manager.delete(act);

			isDel = true;
		} catch (WTException e) {
			isDel = false;
			e.printStackTrace();
		}
		return isDel;
	}

	public EChangeActivity getECAWorking(EChangeActivity activity) {

		EChangeOrder2 eco = activity.getOrder();
		EChangeActivityDefinition ead = activity.getDefinition();
		long tid = CommonUtil.getOIDLongValue(ead);

		EChangeActivity ecaWorking = null;
		try {
			QuerySpec qs2 = new QuerySpec();
			int iii = qs2.addClassList(EChangeActivityDefinition.class, false);
			int jjj = qs2.addClassList(EChangeActivity.class, true);

			qs2.appendWhere(new SearchCondition(EChangeActivityDefinition.class,
					"thePersistInfo.theObjectIdentifier.id", EChangeActivity.class, "definitionReference.key.id"),
					new int[] { iii, jjj });

			qs2.appendAnd();
			qs2.appendWhere(new SearchCondition(EChangeActivity.class, "orderReference.key.id", "=",
					eco.getPersistInfo().getObjectIdentifier().getId()), new int[] { jjj });

			qs2.appendAnd();
			qs2.appendWhere(new SearchCondition(EChangeActivity.class, "definitionReference.key.id", "=", tid),
					new int[] { jjj });

			qs2.appendAnd();
			qs2.appendWhere(new SearchCondition(EChangeActivity.class, "activeType", "=", "WORKING"),
					new int[] { jjj });

			QueryResult result2 = PersistenceHelper.manager.find(qs2);

			while (result2.hasMoreElements()) {
				Object[] o = (Object[]) result2.nextElement();
				ecaWorking = (EChangeActivity) o[0];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ecaWorking;
	}

	public EChangeOrder2 getEO(CommonActivity com) throws Exception {

		QuerySpec qs = new QuerySpec(EChangeOrder2.class);
		qs.appendWhere(new SearchCondition(EChangeOrder2.class, "completeReference.key.id", SearchCondition.EQUAL,
				CommonUtil.getOIDLongValue(com)), new int[] { 0 });
		QueryResult qr = PersistenceHelper.manager.find(qs);

		if (qr.hasMoreElements()) {
			EChangeOrder2 eco = (EChangeOrder2) qr.nextElement();
			return eco;
		}
		return null;
	}

	public Vector getEoEul(CommonActivity com) {
		Vector vec = new Vector();
		try {
			QuerySpec qs = new QuerySpec();
			int eulInt = qs.addClassList(EOEul.class, true);
			int ecaInt = qs.addClassList(EChangeOrder2.class, false);

			qs.appendWhere(new SearchCondition(EOEul.class, "ecoReference.key.id", EChangeOrder2.class,
					"thePersistInfo.theObjectIdentifier.id"), new int[] { eulInt, ecaInt });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(EChangeOrder2.class, "completeWorkReference.key.id",
					SearchCondition.EQUAL, CommonUtil.getOIDLongValue(com)), new int[] { ecaInt });

			QueryResult qr = PersistenceHelper.manager.find(qs);

			while (qr.hasMoreElements()) {

				Object[] oo = (Object[]) qr.nextElement();
				EOEul eul = (EOEul) oo[0];
				vec.add(eul);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return vec;
	}

	public Vector getEoEul(EChangeOrder2 eco) {
		Vector vec = new Vector();
		if (eco == null)
			return vec;
		try {
			QuerySpec qs = new QuerySpec();
			int eulInt = qs.addClassList(EOEul.class, true);

			qs.appendWhere(new SearchCondition(EOEul.class, "ecoReference.key.id", SearchCondition.EQUAL,
					CommonUtil.getOIDLongValue(eco)), new int[] { eulInt });

			QueryResult qr = PersistenceHelper.manager.find(qs);

			while (qr.hasMoreElements()) {

				Object[] oo = (Object[]) qr.nextElement();
				EOEul eul = (EOEul) oo[0];
				vec.add(eul);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return vec;
	}

	public Vector getEcoPartList(EChangeOrder2 eco) {
		Vector vec = new Vector();
		try {
			QueryResult qr = this.getEcoPartLink(eco);// QueryResult qr =
														// PersistenceHelper.manager.navigate(eco,"part",EcoPartLink.class,false);
			while (qr.hasMoreElements()) {
				EcoPartLink link = (EcoPartLink) qr.nextElement();
				String version = link.getVersion();
				WTPartMaster master = (WTPartMaster) link.getPart();
				WTPart part = PartHelper.manager.getPart(master.getNumber(), version);
				vec.add(part);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return vec;
		// PersistenceHelper.manager.navigate(arg0, arg1, arg2)
	}

	public QueryResult getEcoPartLink(EChangeOrder2 eco) {
		QueryResult qr = null;
		try {
			qr = PersistenceHelper.manager.navigate(eco, "part", EcoPartLink.class, false);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return qr;

	}

	public void commitECO(CommonActivity com, String state, EChangeOrder2 eco) throws Exception {

		/* ::::::::::::: ECO 상태 변경 ::::::::::::::::: */
		eco.setOrderState(ChangeECOHelper.ECO_COMPLETE);
		PersistenceHelper.manager.modify(eco);

		/* :::::::::::::ERPHistory Create ::::::::::::::::: */
		ERPHistory history = ERPHistory.newERPHistory();
		history.setEo(ObjectReference.newObjectReference(eco));
		PersistenceHelper.manager.save(history);

		/* ::::::::::::: EoEul(BOM) 적용 ::::::::::::::::: */
		Vector vecEul = this.getEoEul(eco);
		for (int i = 0; i < vecEul.size(); i++) {
			EOEul eul = (EOEul) vecEul.get(i);
			this.commitEOEul(eul, state);
		}

		/* ::::::::::::: ERP 전송 ::::::::::::::::: */
		ERPECOHelper.manager.erpECO(eco, ERPUtil.HISTORY_TYPE_COMPLETE, history);

		/* ::::::::::::: 수신자 ::::::::::::::::::: */
		this.sendNotice(eco);

	}

	public void commitPartStateChange(Vector vecPart) throws Exception {

		for (int i = 0; i < vecPart.size(); i++) {
			/* Part change */
			WTPart part = (WTPart) vecPart.get(i);
			// commitPartStateChange(part);
		}
	}

	public boolean commitPartStateChange(ERPHistory history, WTPart part) throws Exception {

		boolean isPdf = false;
		try {

			/* Part change */
			if (part.getLifeCycleState().toString().equals("APPROVED"))
				return true;
			LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) part, State.toState("APPROVED"));
			String pdfFileName = part.getNumber() + "." + part.getVersionIdentifier().getSeries() + "."
					+ part.getIterationIdentifier().getSeries() + ".pdf";

			/* 3D EPM Change */
			EPMDocument epm = DrawingHelper.manager.getEPMDocument(part);

			/* 결재 checked,approved */
			EChangeOrder2 eco = (EChangeOrder2) history.getEo().getObject();
			CommonActivity activity = eco.getComplete();
			ApprovalMaster master = ApprovalHelper.manager.getApprovalMaster(activity);
			ArrayList approveList = ApprovalHelper.manager.getApprover(master);

			String approved = (String) approveList.get(0);
			String checked = "";

			if (approveList.size() > 1) {
				checked = (String) approveList.get(1);
			}

			if (epm != null) {
				LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) epm, State.toState("APPROVED"));

				IBAUtil.changeIBAValue(epm, "Approved", approved);
				IBAUtil.changeIBAValue(epm, "Checked", checked);
				IBAUtil.changeIBAValue(epm, "LDate", DateUtil.getToDay(ERPUtil.Dateformat));
				if (epm.getAuthoringApplication().toString().equals("ACAD")) {
					this.createPDFLink(epm, history, pdfFileName);
					this.autoCadPdfPSend(epm);
				}

				/* 2D EPM Change && publish */
				EPMDocument epm2D = EpmSearchHelper.manager.getEPM2D((EPMDocumentMaster) epm.getMaster());
				if (epm2D != null) {
					LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) epm2D, State.toState("APPROVED"));
					EpmPublishUtil.publish(epm2D);
					this.createPDFLink(epm2D, history, pdfFileName);
					isPdf = true;
				}
			} else {
				// this.gPartPdfSend(part);
			}
		} catch (Exception e) {
			System.out.println(" >>>>>>>>>>>>> PART CHANGE ERROR <<<<<<<<<<<<<");
			e.printStackTrace();
		}

		return isPdf;
	}

	public void reviseObject(Vector vec) throws Exception {

		if (!SERVER) {
			Class argTypes[] = new Class[] { Vector.class };
			Object args[] = new Object[] { vec };
			try {
				wt.method.RemoteMethodServer.getDefault().invoke("reviseObject", null, this, argTypes, args);
			} catch (RemoteException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}

			return;
		}

		try {

			for (int i = 0; i < vec.size(); i++) {

				String[] partoids = (String[]) vec.get(i);
				Transaction trx = new Transaction();
				trx.start();
				try {

					String oid = partoids[0];
					String linkOid = partoids[1];
					EcoPartLink link = (EcoPartLink) CommonUtil.getObject(linkOid);

					/********************** PART Revise *********************************/
					WTPart part = (WTPart) CommonUtil.getObject(oid);

					Folder folder = FolderHelper.service.getFolder((FolderEntry) part);
					WTPart newPart = (WTPart) VersionControlHelper.service.newVersion(part);
					Folder pfolder = FolderHelper.service.getPersonalCabinet(SessionHelper.manager.getPrincipal());
					FolderHelper.assignLocation((FolderEntry) newPart, pfolder);

					newPart = (WTPart) PersistenceHelper.manager.save(newPart);

					/* 관련 문서 link */
					QueryResult linkQr = PersistenceHelper.manager.navigate(part, "describedBy",
							WTPartDescribeLink.class);

					WTPartDescribeLink dlink = null;
					if ((linkQr != null) && (linkQr.size() > 0)) {
						while (linkQr.hasMoreElements()) {
							WTDocument doc = (WTDocument) linkQr.nextElement();
							dlink = WTPartDescribeLink.newWTPartDescribeLink(newPart, doc);
							dlink = (WTPartDescribeLink) PersistenceHelper.manager.save(dlink);
						}
					}

					newPart = (WTPart) FolderHelper.service.changeFolder(newPart, folder);

					/* Revision History */
					ECOReviseObject reviseObject = ECOReviseObject.newECOReviseObject();
					reviseObject.setEcoPart(link);
					reviseObject.setGubun("PART");
					reviseObject.setVersion(newPart.getVersionIdentifier().getSeries().getValue());
					reviseObject.setOid(CommonUtil.getOIDString(newPart));
					reviseObject.setReviseObject(ObjectReference.newObjectReference(newPart));
					PersistenceHelper.manager.save(reviseObject);

					/********************** EPM Revise *********************************/
					EPMDocument newEPM = null;
					EPMDocument newEPM2D = null;
					EPMDocument epm = DrawingHelper.manager.getEPMDocument(newPart);
					if (epm != null) {

						folder = FolderHelper.service.getFolder((FolderEntry) epm);
						newEPM = (EPMDocument) VersionControlHelper.service.newVersion(epm);
						pfolder = FolderHelper.service.getPersonalCabinet(SessionHelper.manager.getPrincipal());
						FolderHelper.assignLocation((FolderEntry) newEPM, pfolder);

						newEPM = (EPMDocument) PersistenceHelper.manager.save(newEPM);

						newEPM = (EPMDocument) FolderHelper.service.changeFolder(newEPM, folder);

						/* Revision History */
						reviseObject = ECOReviseObject.newECOReviseObject();
						reviseObject.setEcoPart(link);
						reviseObject.setGubun("EPM");
						reviseObject.setVersion(newEPM.getVersionIdentifier().getSeries().getValue());
						reviseObject.setOid(CommonUtil.getOIDString(newEPM));
						reviseObject.setReviseObject(ObjectReference.newObjectReference(newEPM));
						PersistenceHelper.manager.save(reviseObject);

						/* 주부품 연결 */
						EPMBuildRule newEbr = PartSearchHelper.getBuildRule(newPart);
						newEbr.setBuildSource(newEPM);
						PersistenceServerHelper.manager.update(newEbr);
						EPMBuildHistory ebh = PartSearchHelper.getBuildHistory(newPart, newEPM);
						if (ebh != null) {
							ebh.setBuiltBy(newEPM);
							PersistenceServerHelper.manager.update(ebh);
						}

						/* publish 실행 */
						newEPM = (EPMDocument) PersistenceHelper.manager.refresh(newEPM);
						EpmPublishUtil.publish(newEPM);
						/********************** EPM 2DRevise *********************************/

						EPMDocument epm2D = EpmSearchHelper.manager.getEPM2D((EPMDocumentMaster) newEPM.getMaster());
						if (epm2D != null) {

							folder = FolderHelper.service.getFolder((FolderEntry) epm2D);
							newEPM2D = (EPMDocument) VersionControlHelper.service.newVersion(epm2D);
							pfolder = FolderHelper.service.getPersonalCabinet(SessionHelper.manager.getPrincipal());
							FolderHelper.assignLocation((FolderEntry) newEPM2D, pfolder);

							// LifeCycleTemplate lct =
							// LifeCycleHelper.service.getLifeCycleTemplate("DefaultLC",
							// WCUtil.getWTContainerRef());
							// LifeCycleHelper.setLifeCycle((LifeCycleManaged)newEPM, lct);

							newEPM2D = (EPMDocument) PersistenceHelper.manager.save(newEPM2D);

							newEPM2D = (EPMDocument) FolderHelper.service.changeFolder(newEPM2D, folder);
							/* publish 실행 */
							EpmPublishUtil.publish(newEPM2D);

							/* Revision History */
							reviseObject = ECOReviseObject.newECOReviseObject();
							reviseObject.setEcoPart(link);
							reviseObject.setGubun("EPM2D");
							reviseObject.setVersion(newEPM2D.getVersionIdentifier().getSeries().getValue());
							reviseObject.setOid(CommonUtil.getOIDString(newEPM2D));
							reviseObject.setReviseObject(ObjectReference.newObjectReference(newEPM2D));
							PersistenceHelper.manager.save(reviseObject);
						}
					}
					trx.commit();
					trx = null;
				} catch (Exception e) {
					System.out.println(":::::::::::::::::::::::::::: ERROR2 ::::::::::::::::::::::::::::::");
					e.printStackTrace();
					throw e;
				} finally {
					if (trx != null) {
						trx.rollback();
					}
				}

			}

		} catch (Exception e) {
			System.out.println(":::::::::::::::::::::::::::: ERROR3 ::::::::::::::::::::::::::::::");
			e.printStackTrace();
		}

		// assy = (WTPart)VersionControlHelper.service.newVersion(assy);
		// Folder pfolder =
		// FolderHelper.service.getPersonalCabinet(SessionHelper.manager.getPrincipal());
		// FolderHelper.assignLocation((FolderEntry)assy, pfolder);
		// assy = (WTPart)PersistenceHelper.manager.save(assy);
	}

	public Vector getEoReviseHistory(EcoPartLink link) {

		Vector vec = new Vector();
		try {

			QueryResult rt = PersistenceHelper.manager.navigate(link, "reviseObject", PartReviseObjectLink.class, true);

			while (rt.hasMoreElements()) {

				ECOReviseObject reObject = (ECOReviseObject) rt.nextElement();

				vec.add(reObject);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return vec;
	}

	public void reviseDelete(String[] linkOid, String delType) throws WTException {

		for (int i = 0; i < linkOid.length; i++) {
			EcoPartLink link = (EcoPartLink) CommonUtil.getObject(linkOid[i]);

			Vector reVec = ChangeECOHelper.manager.getEoReviseHistory(link);
			for (int h = 0; h < reVec.size(); h++) {
				ECOReviseObject reObject = (ECOReviseObject) reVec.get(h);
				if (delType.equals("PART_DELETE")) {
					if (reObject.getGubun().equals("PART")) {

						try {
							WTPart part = (WTPart) reObject.getReviseObject().getObject();
							if (part != null)
								PersistenceHelper.manager.delete(part);
							PersistenceHelper.manager.delete(reObject);
						} catch (Exception e) {
							e.printStackTrace();
						}

					} else if (reObject.getGubun().equals("EPM")) {

						try {
							EPMDocument epm = (EPMDocument) reObject.getReviseObject().getObject();
							if (epm != null)
								PersistenceHelper.manager.delete(epm);
							PersistenceHelper.manager.delete(reObject);
						} catch (Exception e) {
							e.printStackTrace();
						}

					} else {
						EPMDocument epm2D = (EPMDocument) reObject.getReviseObject().getObject();
						if (epm2D != null)
							PersistenceHelper.manager.delete(epm2D);
						PersistenceHelper.manager.delete(reObject);
					}
				} else {

					try {
						EPMDocument epm2D = (EPMDocument) reObject.getReviseObject().getObject();
						if (epm2D != null)
							PersistenceHelper.manager.delete(epm2D);
						PersistenceHelper.manager.delete(reObject);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}

			}
		}

	}

	public QueryResult getRequestOrderLink(EChangeOrder2 eco) {
		QueryResult qr = null;
		try {
			qr = PersistenceHelper.manager.navigate(eco, "request", RequestOrderLink.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return qr;
		// while(qr.hasMoreElements()){
		// EChangeRequest2 doc = (EChangeRequest2) qr.nextElement();
	}

	public void createPDFLink(EPMDocument epm, ERPHistory history, String pdfFileName) {
		/* PDF History */
		try {
			EPMPDFLink pdf = EPMPDFLink.newEPMPDFLink(epm, history);
			pdf.setResult(ERPUtil.PDF_SEND_WAITING);
			pdf.setMessage(ERPUtil.PDF_SEND_WAITING);
			pdf.setFileName(pdfFileName);
			pdf.setFolder(ERPUtil.getFolderPath());

			PersistenceHelper.manager.save(pdf);
		} catch (Exception e) {

			e.printStackTrace();
			// TODO: handle exception
		}

	}

	public QueryResult searchECOPartLink(EChangeOrder2 eco, String partType) {

		QueryResult rt = null;
		try {
			QuerySpec qs = new QuerySpec(EcoPartLink.class);
			// int idxA = qs.addClassList(EcoPartLink.class, true);
			qs.appendWhere(new SearchCondition(EcoPartLink.class, "roleBObjectRef.key.id", SearchCondition.EQUAL,
					CommonUtil.getOIDLongValue(eco)), new int[] { 0 });

			if (partType != null) {
				qs.appendAnd();
				qs.appendWhere(new SearchCondition(EcoPartLink.class, "partType", SearchCondition.EQUAL, partType),
						new int[] { 0 });
			}

			// System.out.println(qs);
			rt = PersistenceHelper.manager.find(qs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rt;
	}

	/* ECA 완료 후 ECO 최종결재 START */
	public void startECOApprove(EChangeOrder2 eco) {

		// System.out.println(":::::::::::::::::::::: startECOApprove ::::::::::::::");
		try {
			CommonActivity activity = eco.getComplete();
			ApprovalMaster master = ApprovalHelper.manager.getApprovalMaster(activity);
			ApprovalData data = new ApprovalData(master);
			int active = 0;
			WTUser notiUser = null;
			if (master != null) {
				QueryResult qr = ApprovalHelper.manager.getApprovalLine(master);
				ApprovalLine al = null;

				while (qr.hasMoreElements()) {

					String gubun = "EO 최종결재";
					al = (ApprovalLine) qr.nextElement();
					if (al.getName().equals(ApprovalHelper.WORKING_REVIEWER) && al.getSeq() > 1)
						continue;

					if (al.getName().equals(ApprovalHelper.WORKING_DISCUSSER)
							|| al.getName().equals(ApprovalHelper.WORKING_REVIEWER)) {
						al.setState(ApprovalHelper.LINE_APPROVING);

						active = al.getSeq();
						PersistenceHelper.manager.modify(al);

						if (al.getName().equals(ApprovalHelper.WORKING_DISCUSSER))
							gubun = gubun + " 협의 ";
						if (al.getName().equals(ApprovalHelper.WORKING_REVIEWER))
							gubun = gubun + "승인 ";
						gubun = "[" + gubun + "]";

						notiUser = UserHelper.getWTUser(al.getOwner());
						HashMap toHash = new HashMap();
						toHash.put(notiUser.getEMail(), notiUser.getFullName());

						String creatorName = ApprovalHelper.manager.getCreatorFullName(eco);

						toHash.put("createDate", ApprovalHelper.manager.getCreateTime(eco));
						toHash.put("creater", creatorName);
						toHash.put("title", data.getTitle());
						toHash.put("eMail", notiUser.getEMail());
						toHash.put("fullName", notiUser.getFullName());

						MailUtil.mailObjMailSendSetting(al, (Object) activity, toHash,
								"[PLM 전자결재]" + gubun + " 결재 요청이 접수되었습니다.", "WorkCenter에서 확인하세요.", "requestApproval");
					}

					if (al.getName().equals(ApprovalHelper.WORKING_REPORTER)) {

						al.setState(ApprovalHelper.LINE_REQUEST);
						Timestamp time = new Timestamp(new java.util.Date().getTime());
						al.setApproveDate(time);
						PersistenceHelper.manager.modify(al);
					}

				}

				/* ApprovalMaster 상태 */
				master.setActive(active);
				master.setState(ApprovalHelper.MASTER_APPROVING);
				PersistenceHelper.manager.modify(master);

				/* ECO STATE */
				eco.setOrderState(ECO_AFTER_APPROVING);
				PersistenceHelper.manager.modify(eco);
			}

			// LINE_APPROVING
		} catch (Exception e) {
			System.out.println("::::::::::::::::::: startECOApprove ERROR ::::::::::::::::");
			e.printStackTrace();
		}

	}

	public void sendNotice(EChangeOrder2 eco) throws Exception {

		System.out.println(":::::::::::::::::::::::::::::: sendNotice START :::::::::::::::::::::");
		ArrayList allList = new ArrayList();
		QuerySpec qs = new QuerySpec();
		CommonActivity activity = eco.getComplete();

		if (activity == null)
			return;

		// 사후 결재자
		ApprovalMaster lastMaster = ApprovalHelper.manager.getApprovalMaster(activity);

		if (lastMaster == null)
			return;

		// 사전 결재자
		ApprovalMaster ecoMaster = ApprovalHelper.manager.getApprovalMaster(eco);
		if (ecoMaster != null) {
			QueryResult rt = ApprovalHelper.manager.getApprovalLine(ecoMaster);
			while (rt.hasMoreElements()) {
				ApprovalLine line = (ApprovalLine) rt.nextElement();
				if (line.getName().equals(ApprovalHelper.WORKING_REPORTER))
					continue;
				this.registerNotice(lastMaster, line.getOwner());
			}
		}

		/* ECA */
		ArrayList list = getECOActivity(eco);
		for (int i = 0; i < list.size(); i++) {
			EChangeActivity eca = (EChangeActivity) list.get(i);
			this.registerNotice(lastMaster, eca.getOwner().getName());
		}

		QueryResult rt = ApprovalHelper.manager.getApprovalLine(lastMaster);
		int idx = rt.size();
		while (rt.hasMoreElements()) {
			ApprovalLine line = (ApprovalLine) rt.nextElement();
			if (line.getName().equals(ApprovalHelper.WORKING_TEMP))
				continue;
			allList.add(line.getOwner());
			this.registerNotice(lastMaster, line.getOwner());
		}

		QueryResult qr = PersistenceHelper.manager.navigate(eco, "request", RequestOrderLink.class);
		while (qr.hasMoreElements()) {
			EChangeRequest2 ecr = (EChangeRequest2) qr.nextElement();
			this.registerNotice(lastMaster, ecr.getOwner().getName());
		}

//			WTGroup group = UserHelper.service.getWTGroup("Administrators");
//	        Enumeration en = OrganizationServicesHelper.manager.members(group);
//	        while ( en.hasMoreElements() ) {
//	            WTPrincipal pp = (WTPrincipal) en.nextElement();
//	            this.registerNotice(lastMaster, pp.getName());
//	        }

		System.out.println(":::::::::::::::::::::::::::::: sendNotice END :::::::::::::::::::::");

	}

	public ArrayList getECOActivityMaster(EChangeOrder2 eco) throws Exception {

		QuerySpec qs = new QuerySpec(EChangeActivity.class);

		qs.appendWhere(new SearchCondition(EChangeActivity.class, "orderReference.key.id", "=",
				eco.getPersistInfo().getObjectIdentifier().getId()), new int[] { 0 });

		QueryResult result = PersistenceHelper.manager.find(qs);
		ArrayList list = new ArrayList();
		while (result.hasMoreElements()) {

			EChangeActivity activity = (EChangeActivity) result.nextElement();
			ApprovalMaster master = ApprovalHelper.manager.getApprovalMaster(activity);
			list.add(master);
		}

		return list;
	}

	public ArrayList getECOActivity(EChangeOrder2 eco) throws Exception {

		QuerySpec qs = new QuerySpec(EChangeActivity.class);
		qs.appendWhere(new SearchCondition(EChangeActivity.class, "orderReference.key.id", "=",
				eco.getPersistInfo().getObjectIdentifier().getId()), new int[] { 0 });

		QueryResult result = PersistenceHelper.manager.find(qs);
		ArrayList list = new ArrayList();
		while (result.hasMoreElements()) {

			EChangeActivity activity = (EChangeActivity) result.nextElement();

			list.add(activity);
		}

		return list;
	}

	public void registerNotice(ApprovalMaster master, String user) {

		try {
//				 System.out.println(":::::::::: NOTICE user : " + user);
			ApprovalLine line = ApprovalLine.newApprovalLine();
			line.setName(ApprovalHelper.WORKING_TEMP);
			line.setState(ApprovalHelper.LINE_STANDING);
			line.setSeq(100);
			line.setOwner(user);
			line.setReadCheck(false);
			line.setMaster(master);
			line.setStepName(ApprovalHelper.APPROVE_NOTIFICATE);
			line = (ApprovalLine) PersistenceHelper.manager.save(line);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void autoCadPdfPSend(EPMDocument epm) {

		System.out.println(">>>>>>>>>>>>>>>>> autoCadPdfPSend <<<<<<<<<<<<<<<<<<<<<<");
		try {
			String tempDir = WTProperties.getLocalProperties().getProperty("wt.temp");
			ContentItem item = null;

			byte[] buffer = new byte[1024];

			QueryResult result = ContentHelper.service.getContentsByRole((ContentHolder) epm,
					ContentRoleType.SECONDARY);
			while (result.hasMoreElements()) {
				item = (ContentItem) result.nextElement();
				ApplicationData pAppData = (ApplicationData) item;
			}
			ApplicationData adata = (ApplicationData) item;

			EPMPDFLink link = EpmUtil.getPDFSendList(epm);
			HashMap map = new HashMap();
//				System.out.println(":::::::::::::: AutoCad adata :" + adata);
			if (link != null) {
				map.put("oid", CommonUtil.getOIDString(adata));
				map.put("tempDir", link.getFolder());
				map.put("pdfFileName", link.getFileName());
				map.put("epmType", "AutoCad");

				HashMap mapRe = FileDown.pdfDown(map);

				String result1 = (String) mapRe.get("result");
				String message = (String) mapRe.get("message");

				link.setResult(result1);
				link.setMessage(message);
				PersistenceHelper.manager.modify(link);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void gPartPdfSend(WTPart part) {
		PartSearchHelper search = new PartSearchHelper();
		EPMDocument epm = search.getGPartDWG(part);

	}

	public void rejectEco(EChangeOrder2 eco) {

		System.out.println("::::::::::::::::::: ECO rejectECO ::::::::::::::::::::");
		ArrayList allList = new ArrayList();
		try {

			/* eco 상태 변경 */
			eco.setOrderState(ChangeECOHelper.ECO_REJECTED);
			PersistenceHelper.manager.modify(eco);

			/* ERP eco 상태 변경 */
			if (eco.getProcess().equals(this.ECR_EXIST)) {

				ERPECOHelper.manager.upDatePDMECO(eco);
			}

			/* 반려 Email */
			CommonActivity activity = eco.getComplete();
			String gubun = "[EO 최종결재]";
			String process = eco.getProcess();
			WTUser notiUser = null;
			if (activity == null)
				return;
			// 사후 결재자
			ApprovalMaster lastMaster = ApprovalHelper.manager.getApprovalMaster(activity);
			ApprovalData data = new ApprovalData(lastMaster);
			if (lastMaster == null)
				return;

			if (process.equals(ChangeECOHelper.ECR_EXIST)) {
				// 사전 결재자
				ApprovalMaster ecoMaster = ApprovalHelper.manager.getApprovalMaster(eco);

				if (ecoMaster != null) {
					QueryResult rt = ApprovalHelper.manager.getApprovalLine(ecoMaster);
					while (rt.hasMoreElements()) {
						ApprovalLine line = (ApprovalLine) rt.nextElement();
						if (line.getName().equals(ApprovalHelper.WORKING_REPORTER))
							continue;

						notiUser = UserHelper.getWTUser(line.getOwner());
						HashMap toHash = new HashMap();
						toHash.put(notiUser.getEMail(), notiUser.getFullName());

						String creatorName = ApprovalHelper.manager.getCreatorFullName(eco);
//                            System.out.println("::::::::::: ecoMaster : " +notiUser.getFullName() );
						toHash.put("createDate", ApprovalHelper.manager.getCreateTime(eco));
						toHash.put("creater", creatorName);
						toHash.put("title", data.getTitle());
						toHash.put("eMail", notiUser.getEMail());
						toHash.put("fullName", notiUser.getFullName());
						MailUtil.mailObjMailSendSetting(line,
								(Object) ApprovalHelper.manager.getApprovalObject(ecoMaster), toHash,
								"[PLM 전자결재]" + gubun + " 결재가 반려되었습니다.", "PDM System에서 확인하세요.", "notify");
					}
				}
			}

			/* ECA */
			ArrayList list = getECOActivity(eco);
			for (int i = 0; i < list.size(); i++) {
				EChangeActivity eca = (EChangeActivity) list.get(i);

				ApprovalMaster ecaMaster = ApprovalHelper.manager.getApprovalMaster(eca);

				if (ecaMaster != null) {
					QueryResult rt = ApprovalHelper.manager.getApprovalLine(ecaMaster);
					while (rt.hasMoreElements()) {
						ApprovalLine line = (ApprovalLine) rt.nextElement();
						if (line.getName().equals(ApprovalHelper.WORKING_REPORTER))
							continue;

						notiUser = UserHelper.getWTUser(line.getOwner());
						HashMap toHash = new HashMap();
						toHash.put(notiUser.getEMail(), notiUser.getFullName());

						String creatorName = ApprovalHelper.manager.getCreatorFullName(eco);
//                            System.out.println("::::::::::: ECA : " +notiUser.getFullName() );
						toHash.put("createDate", ApprovalHelper.manager.getCreateTime(eco));
						toHash.put("creater", creatorName);
						toHash.put("title", data.getTitle());
						toHash.put("eMail", notiUser.getEMail());
						toHash.put("fullName", notiUser.getFullName());
						MailUtil.mailObjMailSendSetting(line,
								(Object) ApprovalHelper.manager.getApprovalObject(ecaMaster), toHash,
								"[PLM 전자결재]" + gubun + " 결재가 반려되었습니다.", "PDM System에서 확인하세요.", "notify");
					}
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public ArrayList getActiveDefinition() {
		ArrayList list = new ArrayList();
		try {
			QuerySpec qs = new QuerySpec();
			int ii = qs.addClassList(EChangeActivityDefinition.class, true);
			qs.appendOrderBy(new OrderBy(new ClassAttribute(EChangeActivityDefinition.class, "sortNumber"), false),
					new int[] { 0 });
			QueryResult result = PersistenceHelper.manager.find(qs);

			while (result.hasMoreElements()) {
				Object[] o = (Object[]) result.nextElement();
				EChangeActivityDefinition def = (EChangeActivityDefinition) o[0];

				// Department dept = (Department)o[1];
				String doid = def.getPersistInfo().getObjectIdentifier().toString();

				list.add(doid);
			}
		} catch (Exception e) {

		}

		return list;
	}

	public void publishDelete(EPMDocument epm) {

		try {

			Representation representation = PublishUtils.getRepresentation(epm);
			if (representation == null)
				return;
			representation = (Representation) ContentHelper.service.getContents(representation);
			Vector contentList = ContentHelper.getContentList(representation);
			for (int l = 0; l < contentList.size(); l++) {
				ContentItem contentitem = (ContentItem) contentList.elementAt(l);
				if (contentitem instanceof ApplicationData) {
					ApplicationData drawAppData = (ApplicationData) contentitem;

					if (drawAppData.getRole().toString().equals("SECONDARY")
							&& drawAppData.getFileName().lastIndexOf("pdf") > 0) {
						PersistenceHelper.manager.delete(drawAppData);
					}

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
