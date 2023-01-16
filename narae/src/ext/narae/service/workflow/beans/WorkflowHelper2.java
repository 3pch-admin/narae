package ext.narae.service.workflow.beans;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.logging.log4j.Logger;

import com.ptc.wvs.common.ui.Publisher;

import ext.narae.component.ApprovalLineVO;
import ext.narae.service.CommonUtil2;
import ext.narae.service.approval.beans.ApprovalHelper2;
import ext.narae.service.drawing.beans.DrawingHelper2;
import ext.narae.util.iba.IBAUtil;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeOrder2;
import wt.epm.EPMDocument;
import wt.fc.ObjectReference;
import wt.fc.ObjectVector;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistentReference;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
import wt.log4j.LogR;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.pds.StatementSpec;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.team.TeamManaged;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;
import wt.vc.VersionControlHelper;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfEventHelper;
import wt.workflow.engine.WfVotingEventAudit;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WorkItem;
import wt.workflow.work.WorkflowHelper;

public class WorkflowHelper2 implements RemoteAccess, Serializable {
	private static final Logger log = LogR.getLoggerInternal(WorkflowHelper2.class.getName());

	public static QueryResult getWorkItemForChange(WTPrincipal principal) throws WTException {
		QuerySpec spec = new QuerySpec(WorkItem.class);
		SearchCondition cond = null;

		long userId = principal.getPersistInfo().getObjectIdentifier().getId();
		cond = new SearchCondition(WorkItem.class, "ownership.owner.key.id", SearchCondition.EQUAL,
				Long.valueOf(userId));
		spec.appendWhere(cond);

		spec.appendAnd();

		cond = new SearchCondition(WorkItem.class, "status", SearchCondition.EQUAL, "POTENTIAL");
		spec.appendWhere(cond);

		spec.appendAnd();

		spec.appendOpenParen();
		cond = new SearchCondition(WorkItem.class, "primaryBusinessObject.key.classname", SearchCondition.LIKE,
				"%wt.change2.WTChangeRequest2%");
		spec.appendWhere(cond);
		spec.appendOr();
		cond = new SearchCondition(WorkItem.class, "primaryBusinessObject.key.classname", SearchCondition.LIKE,
				"%wt.change2.WTChangeOrder2%");
		spec.appendWhere(cond);
		spec.appendCloseParen();

		ClassAttribute rg1 = new ClassAttribute(WorkItem.class, "thePersistInfo.createStamp");
		spec.appendOrderBy(new OrderBy(rg1, true));

		return makeWorkitemListForTable(PersistenceHelper.manager.find(spec));
	}

	private static QueryResult makeWorkitemListForTable(QueryResult result) {
		System.out.println(result.size());
		QueryResult finalResult = new QueryResult();
		Vector tempResult = new Vector();
		WorkItem item = null;
		PersistentReference ref = null;
		Persistable persist = null;
		while (result.hasMoreElements()) {
			item = (WorkItem) result.nextElement();
			ref = item.getPrimaryBusinessObject();
			persist = null;
			try {
				persist = ref.getObject();
				tempResult.add(item.getPrimaryBusinessObject().getObject());
			} catch (WTRuntimeException e) {
			}
		}

		if (tempResult.size() > 0) {
			ObjectVector vIfc = new ObjectVector(tempResult);
			finalResult.append(vIfc);
		}

		return finalResult;
	}

	public static WorkItem getTargetWorkItem(WTPrincipal principal, LifeCycleManaged object) throws WTException {
		QuerySpec spec = new QuerySpec(WorkItem.class);
		SearchCondition cond = null;

		long userId = principal.getPersistInfo().getObjectIdentifier().getId();
		cond = new SearchCondition(WorkItem.class, "ownership.owner.key.id", SearchCondition.EQUAL,
				Long.valueOf(userId));
		spec.appendWhere(cond);

		spec.appendAnd();

		cond = new SearchCondition(WorkItem.class, "status", SearchCondition.EQUAL, "POTENTIAL");
		spec.appendWhere(cond);

		spec.appendAnd();

		String oid = (new ReferenceFactory()).getReferenceString((Persistable) object);
		cond = new SearchCondition(WorkItem.class, "primaryBusinessObject.key.classname", SearchCondition.EQUAL, oid);
		spec.appendWhere(cond);

		QueryResult result = PersistenceHelper.manager.find(spec);

		if (result.size() > 0) {
			return (WorkItem) result.nextElement();
		} else {
			return null;
		}
	}

	public static WorkItem getTargetWorkItem(WTPrincipal principal, LifeCycleManaged object, String wotkItemState,
			String roleName) throws WTException {
		QuerySpec spec = new QuerySpec(WorkItem.class);
		SearchCondition cond = null;

		long userId = principal.getPersistInfo().getObjectIdentifier().getId();
		cond = new SearchCondition(WorkItem.class, "ownership.owner.key.id", SearchCondition.EQUAL,
				Long.valueOf(userId));
		spec.appendWhere(cond);

		spec.appendAnd();

		spec.appendOpenParen();
		cond = new SearchCondition(WorkItem.class, "status", SearchCondition.EQUAL, wotkItemState);
		spec.appendWhere(cond);
//		spec.appendOr();
//		cond = new SearchCondition(WorkItem.class, "status", SearchCondition.EQUAL, "POTENTIAL");
//		spec.appendWhere(cond);
		spec.appendCloseParen();

		spec.appendAnd();

		String oid = (new ReferenceFactory()).getReferenceString((Persistable) object);
		cond = new SearchCondition(WorkItem.class, "primaryBusinessObject.key.classname", SearchCondition.EQUAL, oid);
		spec.appendWhere(cond);

		spec.appendAnd();
		cond = new SearchCondition(WorkItem.class, "role", SearchCondition.EQUAL, roleName);
		spec.appendWhere(cond);

		QueryResult result = PersistenceHelper.manager.find(spec);

		if (result.size() > 0) {
			return (WorkItem) result.nextElement();
		} else {
			return null;
		}
	}

	public static String approveWorkItem(String workitemOid, String comment) {
		if (!RemoteMethodServer.ServerFlag) {
			Class argTypes[] = new Class[] { String.class, String.class };
			Object args[] = new Object[] { workitemOid, comment };
			try {
				return (String) wt.method.RemoteMethodServer.getDefault().invoke("approveWorkItem",
						WorkflowHelper2.class.getName(), null, argTypes, args);
			} catch (RemoteException e) {
				e.printStackTrace();
				return e.getLocalizedMessage();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				return e.getLocalizedMessage();
			} catch (Exception e) {
				e.printStackTrace();
				return e.getLocalizedMessage();
			}
		} else {
			try {
				WorkItem workitem = (WorkItem) CommonUtil2.getInstance(workitemOid);
				Vector result = new Vector();
				result.add("OK");

				WorkflowHelper.service.workComplete(workitem, SessionHelper.manager.getPrincipalReference(), result);
				if (comment != null && comment.trim().length() > 0) {
					String commentStr = (comment != null) ? URLDecoder.decode(comment, "utf-8") : "";
					WfEventHelper.createVotingEvent(null, (WfAssignedActivity) workitem.getSource().getObject(),
							workitem.getRole(),
							WTPrincipalReference.newWTPrincipalReference(SessionHelper.manager.getPrincipal()),
							commentStr, result, false, workitem.isRequired());
				}
			} catch (Exception e) {
				e.printStackTrace();
				return e.getLocalizedMessage();
			}

			return "";
		}
	}

	public static String approveWorkItemNoEvent(String workitemOid, String comment) {
		if (!RemoteMethodServer.ServerFlag) {
			Class argTypes[] = new Class[] { String.class, String.class };
			Object args[] = new Object[] { workitemOid, comment };
			try {
				return (String) wt.method.RemoteMethodServer.getDefault().invoke("approveWorkItem",
						WorkflowHelper2.class.getName(), null, argTypes, args);
			} catch (RemoteException e) {
				e.printStackTrace();
				return e.getLocalizedMessage();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				return e.getLocalizedMessage();
			} catch (Exception e) {
				e.printStackTrace();
				return e.getLocalizedMessage();
			}
		} else {
			try {
				System.out.println("====== WORK ITEM COMPLETE ======");
				WorkItem workitem = (WorkItem) CommonUtil2.getInstance(workitemOid);
				System.out.println("====== Workitem:" + workitem);

				WorkflowHelper.service.workComplete(workitem, SessionHelper.manager.getPrincipalReference(), null);
				System.out.println("====== Complete" + workitem);
			} catch (Exception e) {
				e.printStackTrace();
				return e.getLocalizedMessage();
			}

			return "";
		}
	}

	public static String rejectWorkItem(String workitemOid, String comment) {
		if (!RemoteMethodServer.ServerFlag) {
			Class argTypes[] = new Class[] { String.class, String.class };
			Object args[] = new Object[] { workitemOid, comment };
			try {
				return (String) wt.method.RemoteMethodServer.getDefault().invoke("rejectWorkItem",
						WorkflowHelper2.class.getName(), null, argTypes, args);
			} catch (RemoteException e) {
				e.printStackTrace();
				return e.getLocalizedMessage();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				return e.getLocalizedMessage();
			} catch (Exception e) {
				e.printStackTrace();
				return e.getLocalizedMessage();
			}
		} else {
			try {
				WorkItem workitem = (WorkItem) CommonUtil2.getInstance(workitemOid);
				Vector result = new Vector();
				result.add("NOT_OK");

				WorkflowHelper.service.workComplete(workitem, SessionHelper.manager.getPrincipalReference(), result);
				if (comment != null && comment.trim().length() > 0) {
					String commentStr = (comment != null) ? URLDecoder.decode(comment, "utf-8") : "";
					WfEventHelper.createVotingEvent(null, (WfAssignedActivity) workitem.getSource().getObject(),
							workitem.getRole(),
							WTPrincipalReference.newWTPrincipalReference(SessionHelper.manager.getPrincipal()),
							commentStr, result, false, workitem.isRequired());
				}
			} catch (Exception e) {
				e.printStackTrace();
				return e.getLocalizedMessage();
			}
			return "";
		}
	}

	private void recordComment(WorkItem workitem, String comments, Vector eventList) throws WTException {
		if (!RemoteMethodServer.ServerFlag) {
			Class argTypes[] = new Class[] { WorkItem.class, String.class, Vector.class };
			Object args[] = new Object[] { workitem, comments, eventList };
			try {
				wt.method.RemoteMethodServer.getDefault().invoke("recordComment", WorkflowHelper2.class.getName(), null,
						argTypes, args);
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
//			WTPrincipalReference currentUserRef = WTPrincipalReference.newWTPrincipalReference(SessionHelper.manager.getPrincipal());
//			WfAssignedActivity wa = (WfAssignedActivity)((WfAssignment)workitem.getParentWA().getObject()).getSource().getObject();
//			boolean isSigningRequired = (((WfAssignedActivityTemplate)wa.getTemplateReference().getObject()).isSigningRequired());
//			
//			int tryCount = wa.getTripCount();
//			
//			ArrayList assigneerAudit = new ArrayList();
//			QueryResult result = WorkflowUtil.getVotingEventsForActivity(wa.true);
//			while( result.hasMoreElements()) {
//				WfVotingEventAudit audit = (WfVotingEventAudit)result.nextElement();
//				if( workitem.getRole().equals(audit.getRole()) && currentUserRef.equals(audit.getAssigneeRef())) {
//					assigneerAudit.add(audit);
//				}
//			}
//			SessionServerHelper.manager.setAccessEnforced(false);
//			if( tryCount > assigneerAudit.size() ) {
//				WfEventHelper.createVotingEvent(null, wa, workitem.getRole(), currentUserRef, comments, eventList, isSigningRequired, workitem.isRequired());
//			} else {
//				WfVotingEventAudit audit = null;
//				for( int i=0; i < assigneerAudit.size(); i++) {
//					if(audit == null) {
//						audit = (WfVotingEventAudit)assigneerAudit.get(i);
//					} else if(audit.getCreateTimestamp().before(((WfVotingEventAudit)assigneerAudit.get(i)).getCreateTimestamp())) {
//						audit = (WfVotingEventAudit)assigneerAudit.get(i);
//					}
//				}
//				
//				if(audit != null) {
//					audit.setUserComment(comments);
//					PersistenceHelper.manager.save(audit);
//				}
//			}
//			SessionServerHelper.manager.setAccessEnforced(true);
		}
	}

	public static QueryResult getVotingEventsForActivity(WfAssignedActivity activity, boolean isAll, boolean order)
			throws WTException, WTPropertyVetoException {
		QuerySpec qs = new QuerySpec();
		int votingIndex = qs.appendClassList(WfVotingEventAudit.class, true);
		int activityIndex = qs.appendClassList(WfAssignedActivity.class, false);

		qs.appendWhere(new SearchCondition(new ClassAttribute(WfVotingEventAudit.class, "activityKey"), "=",
				new ClassAttribute(WfAssignedActivity.class, "key")), new int[] { votingIndex, activityIndex });

		if (activity != null) {
			if (!isAll) {
				qs.appendAnd();
				int tripCount = activity.getTripCount();
				qs.appendWhere(new SearchCondition(WfVotingEventAudit.class, "tripCount", "=", tripCount),
						new int[] { votingIndex });
			}

			qs.appendAnd();
			long id = activity.getPersistInfo().getObjectIdentifier().getId();
			qs.appendWhere(new SearchCondition(WfActivity.class, WTAttributeNameIfc.ID_NAME, "=", id),
					new int[] { activityIndex });
		}
		qs.setQuerySet(false);
		OrderBy orderBy = new OrderBy(
				new ClassAttribute(WfVotingEventAudit.class, WTAttributeNameIfc.UPDATE_STAMP_NAME), order);
		qs.appendOrderBy(orderBy, new int[] { votingIndex });

		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);

		return qr;
	}

	public static String getComment(WorkItem workItem) throws Exception {
		QueryResult result = WorkflowHelper2
				.getVotingEventsForActivity((WfAssignedActivity) workItem.getSource().getObject(), false, true);
		String comment = null;
		if (result.size() == 1) {
			WfVotingEventAudit voting = (WfVotingEventAudit) result.nextElement();

			comment = (voting.getUserComment() == null || voting.getUserComment().trim().equals("")) ? ""
					: voting.getUserComment();
		} else {
			comment = "";
		}
		return comment;
	}

	public static List<String> getApprovalInfo(WorkItem workItem) throws Exception {
		List<String> returnResult = new ArrayList<String>();

		QueryResult result = WorkflowHelper2
				.getVotingEventsForActivity((WfAssignedActivity) workItem.getSource().getObject(), false, true);
		String comment = null;
		String approveTime = null;
		if (result.size() == 1) {
			WfVotingEventAudit voting = (WfVotingEventAudit) result.nextElement();
			comment = (voting.getUserComment() == null || voting.getUserComment().trim().equals("")) ? ""
					: voting.getUserComment();
			approveTime = voting.getModifyTimestamp().toLocaleString();
		} else {
			comment = "";
			approveTime = "";
		}
		returnResult.add(comment);
		returnResult.add(approveTime);

		return returnResult;
	}

	public static boolean changeState(WTObject pbo, String state) throws WTException {
		log.debug("..............................................");
		log.debug("0. 관련객체 상태 변경 Process1");
		log.debug("1. 관련된 부품과 도면의 상태를 바꿀것입니다........" + state);
		if (pbo instanceof WTChangeOrder2) {
			WTChangeOrder2 change = (WTChangeOrder2) pbo;
			log.debug("2. 대상 ECO......" + change.getNumber());
			QueryResult result = ChangeHelper2.service.getChangeablesAfter(change);
			log.debug("3. ECO에 연계된 변경대상부품의 수는......" + result.size() + "개");
			WTPart part = null;
			EPMDocument epm3d = null;
			EPMDocument epm2d = null;
			while (result.hasMoreElements()) {
				log.debug("~~~~~~~~~~~");
				part = (WTPart) result.nextElement();

				if (!part.getState().getState().toString().trim().equals("APPROVED")
						&& !part.getState().getState().toString().trim().equals("SENT_ERP")) {
					log.debug("4. 부품번호/버젼......" + part.getNumber() + "/"
							+ part.getVersionInfo().getIdentifier().getValue() + "."
							+ part.getIterationInfo().getIdentifier().getValue());
					log.debug("4-1. 체크 최신 이터레이션");
					if (!VersionControlHelper.isLatestIteration(part)) {
						log.debug("4-2. 연결된 부품은 최신 이터레이션이 아님. 최신 이터레이션 가져오기");
						part = (WTPart) VersionControlHelper.service.getLatestIteration(part, false);
						log.debug("4-3. 최신 이터레이션......" + part.getNumber() + "/"
								+ part.getVersionInfo().getIdentifier().getValue() + "."
								+ part.getIterationInfo().getIdentifier().getValue());
					}
					log.debug("4-3. 해당 부품의 상태 바꾸기......" + state);
					LifeCycleHelper.service.setLifeCycleState(part, State.toState(state));
					log.debug("4-4. 부품 상태 바꾸기 성공");

					// Find 3D
					log.debug("5. 3D 캐드 가져오기");
					epm3d = DrawingHelper2.getEPMDocument(part);
					if (epm3d != null) {
						log.debug("5-1. 해당 부품에 연결된 3D는....." + epm3d.getNumber() + "/"
								+ epm3d.getVersionInfo().getIdentifier().getValue() + "."
								+ epm3d.getIterationInfo().getIdentifier().getValue());
						log.debug("5-2. 해당 부품에 연결된 3D 상태 바꾸기......" + state);
						LifeCycleHelper.service.setLifeCycleState(epm3d, State.toState(state));
						log.debug("5-3. 해당 부품에 연결된 3D 상태 바꾸기 성공");

						// Find 2D and change state
						log.debug("6. 2D 캐드 가져오기");
						epm2d = DrawingHelper2.getRelational2DCad(epm3d);
						if (epm2d != null) {
							log.debug("6-1. 해당 3D에 연결된 2D는....." + epm2d.getNumber() + "/"
									+ epm2d.getVersionInfo().getIdentifier().getValue() + "."
									+ epm2d.getIterationInfo().getIdentifier().getValue());
							log.debug("6-2. 해당 3D에 연결된 2D 상태 바꾸기......" + state);
							LifeCycleHelper.service.setLifeCycleState(epm2d, State.toState(state));
							log.debug("6-3. 해당 3D에 연결된 2D 상태 바꾸기 성공");
						} else {
							log.debug("6-99. 해당 3D에 연결된 2D가 없습니다.<=======");
						}
					} else {
						log.debug("5-99. 해당 부품에 연결된 3D가 없습니다.<======");
					}
				}
			}

			log.debug("6. 상태 바꾸기 종료!!!!!!!!!!!!!!!!!!!!");
		} else if (pbo instanceof WTPart) {
			EPMDocument epm3d = null;
			EPMDocument epm2d = null;
			WTPart part = (WTPart) pbo;

			if (!part.getState().getState().toString().trim().equals("APPROVED")
					&& !part.getState().getState().toString().trim().equals("SENT_ERP")) {
				log.debug("4. 부품번호/버젼......" + part.getNumber() + "/" + part.getVersionInfo().getIdentifier().getValue()
						+ "." + part.getIterationInfo().getIdentifier().getValue());
				log.debug("4-1. 체크 최신 이터레이션");
				if (!VersionControlHelper.isLatestIteration(part)) {
					log.debug("4-2. 연결된 부품은 최신 이터레이션이 아님. 최신 이터레이션 가져오기");
					part = (WTPart) VersionControlHelper.service.getLatestIteration(part, false);
					log.debug("4-3. 최신 이터레이션......" + part.getNumber() + "/"
							+ part.getVersionInfo().getIdentifier().getValue() + "."
							+ part.getIterationInfo().getIdentifier().getValue());
				}
				log.debug("4-3. 해당 부품의 상태 바꾸기......" + state);
				LifeCycleHelper.service.setLifeCycleState(part, State.toState(state));
				log.debug("4-4. 부품 상태 바꾸기 성공");

				// Find 3D
				log.debug("5. 3D 캐드 가져오기");
				epm3d = DrawingHelper2.getEPMDocument(part);
				if (epm3d != null) {
					log.debug("5-1. 해당 부품에 연결된 3D는....." + epm3d.getNumber() + "/"
							+ epm3d.getVersionInfo().getIdentifier().getValue() + "."
							+ epm3d.getIterationInfo().getIdentifier().getValue());
					log.debug("5-2. 해당 부품에 연결된 3D 상태 바꾸기......" + state);
					LifeCycleHelper.service.setLifeCycleState(epm3d, State.toState(state));
					log.debug("5-3. 해당 부품에 연결된 3D 상태 바꾸기 성공");

					// Find 2D and change state
					log.debug("6. 2D 캐드 가져오기");
					epm2d = DrawingHelper2.getRelational2DCad(epm3d);
					if (epm2d != null) {
						log.debug("6-1. 해당 3D에 연결된 2D는....." + epm2d.getNumber() + "/"
								+ epm2d.getVersionInfo().getIdentifier().getValue() + "."
								+ epm2d.getIterationInfo().getIdentifier().getValue());
						log.debug("6-2. 해당 3D에 연결된 2D 상태 바꾸기......" + state);
						LifeCycleHelper.service.setLifeCycleState(epm2d, State.toState(state));
						log.debug("6-3. 해당 3D에 연결된 2D 상태 바꾸기 성공");
					} else {
						log.debug("6-99. 해당 3D에 연결된 2D가 없습니다.<=======");
					}
				} else {
					log.debug("5-99. 해당 부품에 연결된 3D가 없습니다.<======");
				}
			}
		} else {
			log.debug("99. 대상객체는 ECO 객체가 아니므로 관련객체에는 아무런 영향이 없을 것입니다.");
		}
		return true;
	}

	public static boolean changeState2(WTObject pbo, String state) throws WTException {
		log.debug("..............................................");
		log.debug("0. 관련객체 상태 변경 Process2");
		log.debug("1. 관련된 부품과 도면의 상태를 바꿀것입니다........" + state);
		if (pbo instanceof WTChangeOrder2) {
			WTChangeOrder2 change = (WTChangeOrder2) pbo;
			log.debug("2. 대상 ECO......" + change.getNumber());
			QueryResult result = ChangeHelper2.service.getChangeablesAfter(change);
			log.debug("3. ECO에 연계된 변경대상부품의 수는......" + result.size() + "개");
			WTPart part = null;
			EPMDocument epm3d = null;
			EPMDocument epm2d = null;
			while (result.hasMoreElements()) {
				log.debug("~~~~~~~~~~~");
				part = (WTPart) result.nextElement();

				if (!part.getState().getState().toString().trim().equals("SENT_ERP")) {
					log.debug("4. 부품번호/버젼......" + part.getNumber() + "/"
							+ part.getVersionInfo().getIdentifier().getValue() + "."
							+ part.getIterationInfo().getIdentifier().getValue());
					log.debug("4-1. 체크 최신 이터레이션");
					if (!VersionControlHelper.isLatestIteration(part)) {
						log.debug("4-2. 연결된 부품은 최신 이터레이션이 아님. 최신 이터레이션 가져오기");
						part = (WTPart) VersionControlHelper.service.getLatestIteration(part, false);
						log.debug("4-3. 최신 이터레이션......" + part.getNumber() + "/"
								+ part.getVersionInfo().getIdentifier().getValue() + "."
								+ part.getIterationInfo().getIdentifier().getValue());
					}
					log.debug("4-3. 해당 부품의 상태 바꾸기......" + state);
					LifeCycleHelper.service.setLifeCycleState(part, State.toState(state));
					log.debug("4-4. 부품 상태 바꾸기 성공");

					// Find 3D
					log.debug("5. 3D 캐드 가져오기");
					epm3d = DrawingHelper2.getEPMDocument(part);
					if (epm3d != null) {
						log.debug("5-1. 해당 부품에 연결된 3D는....." + epm3d.getNumber() + "/"
								+ epm3d.getVersionInfo().getIdentifier().getValue() + "."
								+ epm3d.getIterationInfo().getIdentifier().getValue());
						log.debug("5-2. 해당 부품에 연결된 3D 상태 바꾸기......" + state);
						LifeCycleHelper.service.setLifeCycleState(epm3d, State.toState(state));
						log.debug("5-3. 해당 부품에 연결된 3D 상태 바꾸기 성공");

						// Find 2D and change state
						log.debug("6. 2D 캐드 가져오기");
						epm2d = DrawingHelper2.getRelational2DCad(epm3d);
						if (epm2d != null) {
							log.debug("6-1. 해당 3D에 연결된 2D는....." + epm2d.getNumber() + "/"
									+ epm2d.getVersionInfo().getIdentifier().getValue() + "."
									+ epm2d.getIterationInfo().getIdentifier().getValue());
							log.debug("6-2. 해당 3D에 연결된 2D 상태 바꾸기......" + state);
							LifeCycleHelper.service.setLifeCycleState(epm2d, State.toState(state));
							log.debug("6-3. 해당 3D에 연결된 2D 상태 바꾸기 성공");
						} else {
							log.debug("6-99. 해당 3D에 연결된 2D가 없습니다.<=======");
						}
					} else {
						log.debug("5-99. 해당 부품에 연결된 3D가 없습니다.<======");
					}
				}
			}

			log.debug("6. 상태 바꾸기 종료!!!!!!!!!!!!!!!!!!!!");
		} else if (pbo instanceof WTPart) {
			EPMDocument epm3d = null;
			EPMDocument epm2d = null;
			WTPart part = (WTPart) pbo;

			if (!part.getState().getState().toString().trim().equals("SENT_ERP")) {
				log.debug("4. 부품번호/버젼......" + part.getNumber() + "/" + part.getVersionInfo().getIdentifier().getValue()
						+ "." + part.getIterationInfo().getIdentifier().getValue());
				log.debug("4-1. 체크 최신 이터레이션");
				if (!VersionControlHelper.isLatestIteration(part)) {
					log.debug("4-2. 연결된 부품은 최신 이터레이션이 아님. 최신 이터레이션 가져오기");
					part = (WTPart) VersionControlHelper.service.getLatestIteration(part, false);
					log.debug("4-3. 최신 이터레이션......" + part.getNumber() + "/"
							+ part.getVersionInfo().getIdentifier().getValue() + "."
							+ part.getIterationInfo().getIdentifier().getValue());
				}
				log.debug("4-3. 해당 부품의 상태 바꾸기......" + state);
				LifeCycleHelper.service.setLifeCycleState(part, State.toState(state));
				log.debug("4-4. 부품 상태 바꾸기 성공");

				// Find 3D
				log.debug("5. 3D 캐드 가져오기");
				epm3d = DrawingHelper2.getEPMDocument(part);
				if (epm3d != null) {
					log.debug("5-1. 해당 부품에 연결된 3D는....." + epm3d.getNumber() + "/"
							+ epm3d.getVersionInfo().getIdentifier().getValue() + "."
							+ epm3d.getIterationInfo().getIdentifier().getValue());
					log.debug("5-2. 해당 부품에 연결된 3D 상태 바꾸기......" + state);
					LifeCycleHelper.service.setLifeCycleState(epm3d, State.toState(state));
					log.debug("5-3. 해당 부품에 연결된 3D 상태 바꾸기 성공");

					// Find 2D and change state
					log.debug("6. 2D 캐드 가져오기");
					epm2d = DrawingHelper2.getRelational2DCad(epm3d);
					if (epm2d != null) {
						log.debug("6-1. 해당 3D에 연결된 2D는....." + epm2d.getNumber() + "/"
								+ epm2d.getVersionInfo().getIdentifier().getValue() + "."
								+ epm2d.getIterationInfo().getIdentifier().getValue());
						log.debug("6-2. 해당 3D에 연결된 2D 상태 바꾸기......" + state);
						LifeCycleHelper.service.setLifeCycleState(epm2d, State.toState(state));
						log.debug("6-3. 해당 3D에 연결된 2D 상태 바꾸기 성공");
					} else {
						log.debug("6-99. 해당 3D에 연결된 2D가 없습니다.<=======");
					}
				} else {
					log.debug("5-99. 해당 부품에 연결된 3D가 없습니다.<======");
				}
			}
		} else {
			log.debug("99. 대상객체는 ECO 객체가 아니므로 관련객체에는 아무런 영향이 없을 것입니다.");
		}
		return true;
	}

	public static boolean hasAgree(WTObject object) throws Exception {
		ApprovalLineVO approvalLine = ApprovalHelper2.getApprovalLine((TeamManaged) object);
		List<WTUser> alist3 = approvalLine.getChangManager1();
		if (alist3.size() > 0)
			return true;
		else
			return false;
	}

	public static boolean approval2DPublish(WTObject pbo) throws Exception {
		if (pbo instanceof WTChangeOrder2) {
			WTChangeOrder2 order = (WTChangeOrder2) pbo;
			// Get ECO approval line
			// Get reviewer and approver
			// Get Part from ECO
			// Get 3D from ECO
			// Set IBA to 3D CHECKED / APPROVED / P_NAME / GROUP_NAME / L_DATE
			// Publish
		} else {

		}
		log.debug(".................approval2DPublish............................");
		log.debug("0. 도면 Publish start");
		log.debug("1. 결재가 종료되었습니다. 결재 대상이 ECO일 경우 도면을 Publish합니다.");
		if (pbo instanceof WTChangeOrder2) {
			WTChangeOrder2 change = (WTChangeOrder2) pbo;
			log.debug("2. 대상 ECO......" + change.getNumber());
			QueryResult result = ChangeHelper2.service.getChangeablesAfter(change);
			log.debug("3. ECO에 연계된 변경대상부품의 수는......" + result.size() + "개");
			WTPart part = null;
			EPMDocument epm3d = null;
			EPMDocument epm2d = null;
			ApprovalLineVO approvalLine = null;
			String checked = "";
			String approved = "";
			String pName = "";
			String groupName = "";
			String lDate = "";
			while (result.hasMoreElements()) {
				log.debug("~~~~~~~~~~~");
				part = (WTPart) result.nextElement();
				log.debug("4. 부품번호/버젼......" + part.getNumber() + "/" + part.getVersionInfo().getIdentifier().getValue()
						+ "." + part.getIterationInfo().getIdentifier().getValue());
				log.debug("4-1. 체크 최신 이터레이션");
				if (!VersionControlHelper.isLatestIteration(part)) {
					log.debug("4-2. 연결된 부품은 최신 이터레이션이 아님. 최신 이터레이션 가져오기");
					part = (WTPart) VersionControlHelper.service.getLatestIteration(part, false);
					log.debug("4-3. 최신 이터레이션......" + part.getNumber() + "/"
							+ part.getVersionInfo().getIdentifier().getValue() + "."
							+ part.getIterationInfo().getIdentifier().getValue());
				}

				// Find 3D
				log.debug("5. 3D 캐드 가져오기");
				epm3d = DrawingHelper2.getEPMDocument(part);
				if (epm3d != null) {
					log.debug("5-1. 해당 부품에 연결된 3D는....." + epm3d.getNumber() + "/"
							+ epm3d.getVersionInfo().getIdentifier().getValue() + "."
							+ epm3d.getIterationInfo().getIdentifier().getValue());
					log.debug("5-2. 해당 부품에 연결된 3D 속성추가 및 업데이트......");
					if (!VersionControlHelper.isLatestIteration(epm3d)) {
						epm3d = (EPMDocument) VersionControlHelper.service.getLatestIteration(epm3d, false);
					}

					approvalLine = ApprovalHelper2.getApprovalLine(change);
					if (approvalLine.getChangManager1() != null && approvalLine.getChangManager1().size() > 0) {
						checked = approvalLine.getChangManager1().get(0).getFullName();
					}
					if (approvalLine.getChangManager2() != null && approvalLine.getChangManager2().size() > 0) {
						approved = approvalLine.getChangManager2().get(0).getFullName();
					}
					if (approvalLine.getChangeManagerInfo2() != null
							&& approvalLine.getChangeManagerInfo2().size() > 0) {
						lDate = approvalLine.getChangeManagerInfo2().get(0).get(0);
					}

					String[] tempDate = lDate.split(" ");
					lDate = tempDate[0].replace(".", "/") + tempDate[1].replace(".", "/") + tempDate[2];
					log.debug("5-3. 협의자=" + checked);
					log.debug("5-4. 승인자=" + approved);
					log.debug("5-5. 승인일시=" + lDate);
					HashMap<String, Object> attribute = new HashMap<String, Object>();
					IBAUtil.changeIBAValue(epm3d, "Checked", checked);
					IBAUtil.changeIBAValue(epm3d, "Approved", approved);
					IBAUtil.changeIBAValue(epm3d, "LDate", lDate);
//					attribute.put("Checked", checked);
//					attribute.put("Approved", approved);
//					attribute.put("LDate", lDate);
//					AttributeHelper.service.setValue(epm3d, attribute);
					log.debug("5-6. 해당 부품에 연결된 3D 속성추가 및 업데이트 성공");

					// Find 2D and change state
					log.debug("6. 2D 캐드 가져오기");
					epm2d = DrawingHelper2.getRelational2DCad(epm3d);
					if (epm2d != null) {
						log.debug("6-1. 해당 3D에 연결된 2D는....." + epm2d.getNumber() + "/"
								+ epm2d.getVersionInfo().getIdentifier().getValue() + "."
								+ epm2d.getIterationInfo().getIdentifier().getValue());
						log.debug("6-2. 해당 3D에 연결된 2D의 Publish 이벤트 생성......"
								+ epm2d.getPersistInfo().getObjectIdentifier().toString());
						Publisher pub = new Publisher();
						if (!VersionControlHelper.isLatestIteration(epm2d)) {
							epm2d = (EPMDocument) VersionControlHelper.service.getLatestIteration(epm2d, false);
						}
						String objRef = ObjectReference.newObjectReference(epm2d).toString();
						// pub.doPublish(false, true, objRef, (ConfigSpec)null, (ConfigSpec)null, true,
						// null, null, Publisher.EPM, null, 0);
						log.debug("6-3. 해당 3D에 연결된 2D의 Publish 이벤트 생성 성공");
					} else {
						log.debug("6-99. 해당 3D에 연결된 2D가 없습니다.<=======");
					}
				} else {
					log.debug("5-99. 해당 부품에 연결된 3D가 없습니다.<======");
				}
			}

			log.debug("6. 도면 Publish 종료!!!!!!!!!!!!!!!!!!!!");
		} else {
			log.debug("99. 대상객체는 ECO 객체가 아니므로 도면 Publish를 진행하지 않았습니다.");
		}
		return true;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			WTChangeOrder2 item = (WTChangeOrder2) CommonUtil2.getInstance("wt.change2.WTChangeOrder2:288559");
			System.out.println(item.getModifyTimestamp());

		} catch (WTException e) {
		} catch (WTRuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
