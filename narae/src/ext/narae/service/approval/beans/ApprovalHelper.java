package ext.narae.service.approval.beans;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import ext.narae.service.approval.ApprovalLine;
import ext.narae.service.approval.ApprovalLineTemplate;
import ext.narae.service.approval.ApprovalLineTemplate2;
import ext.narae.service.approval.ApprovalMaster;
import ext.narae.service.approval.ApprovalObjectLink;
import ext.narae.service.approval.ApprovalToObjectLink;
import ext.narae.service.approval.CommonActivity;
import ext.narae.service.approval.MultiApproval;
import ext.narae.service.change.EChangeActivity;
import ext.narae.service.change.EChangeRequest2;
import ext.narae.service.erp.ERPHistory;
import ext.narae.service.erp.beans.ERPECRHelper;
import ext.narae.service.erp.beans.ERPUtil;
import ext.narae.service.org.People;
import ext.narae.service.org.beans.UserHelper;
import ext.narae.util.CommonUtil;
import ext.narae.util.DateUtil;
import ext.narae.util.OwnPersistable;
import ext.narae.util.StringUtil;
import ext.narae.util.WCUtil;
import ext.narae.util.mail.MailUtil;
import wt.doc.WTDocument;
import wt.enterprise.Managed;
import wt.epm.EPMDocument;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.LifeCycleTemplate;
import wt.lifecycle.State;
import wt.method.MethodContext;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.OrganizationServicesHelper;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.pdmlink.PDMLinkProduct;
import wt.pom.DBProperties;
import wt.pom.Transaction;
import wt.pom.WTConnection;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.vc.Versioned;

/*
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpClientConnection;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.protocol.RequestConnControl;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestExpectContinue;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.apache.http.util.EntityUtils;
*/

public class ApprovalHelper implements Serializable, RemoteAccess {
	public static final String MASTER_WORKING = "작업중";
	public static final String MASTER_APPROVING = "결재중";
	public static final String MASTER_APPROVED = "결재완료";
	public static final String MASTER_REJECTED = "반려";
	public static final String MASTER_WITHDRAWAL = "회수";
	public static final String MASTER_REWORKING = "재작성중";
	public static final String MASTER_INWORK = "작성중";
	public static final String LINE_REQUEST = "결재요청";
	public static final String LINE_STANDING = "대기";
	public static final String LINE_APPROVING = "결재중";
	public static final String LINE_COMMIT = "결재 완료";
	public static final String LINE_REJECTED = "반려";
	public static final String LINE_STANDING_CANCEL = "대기-회수";
	public static final String LINE_WITHDRAWAL = "회수";
	public static final String LINE_TEMP = "수신";
	public static final String LINE_WORKING = "작업중";
	public static final String LINE_REWORKING = "재작성중";
	public static final String LINE_INWORKING = "작성중";
	public static final String LINE_DISCUSSING = "수신";
	public static final String LINE_DISCUSSING_AGREE = "협의";
	public static final String LINE_DISCUSSING_REJECT = "반대";
	public static final String LINE_COMPLETE = "완료됨";
	public static final String APPROVE_REQUEST = "작성";
	public static final String APPROVE_PREAPPROVE = "협의전결재";
	public static final String APPROVE_DISCUSS = "협의";
	public static final String APPROVE_POSTAPPROVE = "결정";
	public static final String APPROVE_NOTIFICATE = "수신";
	public static final String WORKING_REPORTER = "제출자";
	public static final String WORKING_REVIEWER = "승인자";
	public static final String WORKING_DISCUSSER = "협의자";
	public static final String WORKING_WORKING = "작업자";
	public static final String WORKING_TEMP = "수신자";
	public static final String WORKING_READY = "등록전";
	public static final String CENCEL_WORK = "반려통지";

	public static final String GROUPWARE_GET_PASSWORD_URL = "/lgchem/access.common.approvalpwd.lgc?s_user=";

	static final boolean SERVER = wt.method.RemoteMethodServer.ServerFlag;

	public static ApprovalHelper manager = new ApprovalHelper();

	public boolean isAccessModify(Persistable per) throws Exception {

		String creatorName = getCreatorName(per);

		return isAccessModify(per, creatorName);
	}

	public boolean isAccessModify(Persistable per, String creatorName) throws Exception {

		if (!CommonUtil.isAdmin()) {

			String suser = SessionHelper.manager.getPrincipal().getName();

			if (creatorName == null) {
				throw new WTException("�ۼ��ڸ� ã�� �� �����ϴ�.");
			}

			if (!suser.equals(creatorName)) {
				return false;
			}
		}

		return isModify(per);
	}

	public boolean isModify(Persistable per) throws Exception {

		ApprovalMaster appMaster = ApprovalHelper.manager.getApprovalMaster(per);
		if (appMaster == null)
			return true;

		String state = appMaster.getState();

		return ApprovalHelper.MASTER_INWORK.equals(state);
//        ||    ApprovalHelper.MASTER_REJECTED.equals(state)
//        ||    ApprovalHelper.MASTER_REWORKING.equals(state)
//        ||    ApprovalHelper.MASTER_WITHDRAWAL.equals(state);
	}

	public boolean isAccessRevise(Persistable per) throws Exception {

		if (!CommonUtil.isAdmin()) {

			String suser = SessionHelper.manager.getPrincipal().getName();

			String creatorName = getCreatorName(per);

			if (creatorName == null) {
				throw new WTException("�ۼ��ڸ� ã�� �� �����ϴ�.");
			}

			if (!suser.equals(creatorName)) {
				return false;
			}
		}

		ApprovalMaster appMaster = ApprovalHelper.manager.getApprovalMaster(per);
		if (appMaster == null)
			return true;

		String state = appMaster.getState();

		return ApprovalHelper.MASTER_APPROVED.equals(state);
	}

	public String getLocaleState(Persistable per) throws Exception {
		ApprovalMaster appMaster = ApprovalHelper.manager.getApprovalMaster(per);

		if (appMaster == null) {
			if (!CommonUtil.isUSLocale()) {
				return MASTER_INWORK;
			} else {
				return "InWork";
			}
		}
		return appMaster.getLocaleState();
	}

	public String getState(Persistable per) throws Exception {
		ApprovalMaster appMaster = ApprovalHelper.manager.getApprovalMaster(per);

		if (appMaster == null) {
			return MASTER_INWORK;
		}
		return appMaster.getState();
	}

	public void registApproval(Persistable per, String[] approveUser, String[] tempUser) throws Exception {
		registApproval(per, approveUser, tempUser, false);
	}

	public void registApproval(Persistable per, String[] approveUser, String[] tempUser, boolean isWorkItem)
			throws Exception {

//		System.out.println("====registApproval=======");
		if (!SERVER) {
			Class argTypes[] = new Class[] { Persistable.class, String[].class, String[].class, boolean.class };
			Object args[] = new Object[] { per, approveUser, tempUser, new Boolean(isWorkItem) };
			try {
				RemoteMethodServer.getDefault().invoke("registApproval", null, this, argTypes, args);
				return;
			} catch (RemoteException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new WTException(e);
			}
		}

		Transaction trx = new Transaction();
		try {
			trx.start();

			ReferenceFactory rf = new ReferenceFactory();

			ApprovalMaster am = ApprovalMaster.newApprovalMaster();

			if (isWorkItem) {
				am.setState(MASTER_WORKING);
				setPersistableState(per, "WORKING");
			} else {
				am.setState(MASTER_APPROVING);
				// setPersistableState(per, "UNDERREVIEW");
				setPersistableState(per, "APPROVEING");
			}

			am.setOwner(SessionHelper.manager.getPrincipal().getName());
			am.setActive(0);
			am = (ApprovalMaster) PersistenceHelper.manager.save(am);

			ApprovalObjectLink link = ApprovalObjectLink.newApprovalObjectLink(per, am);
			PersistenceHelper.manager.save(link);
			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
			ApprovalLineTemplate template = getApprovalTemplate(user);
			if (template == null) {
				template = ApprovalLineTemplate.newApprovalLineTemplate();
				template.setOwner(user.getName());
			}

			ArrayList approveList = new ArrayList();
			ApprovalLine line = ApprovalLine.newApprovalLine();
			line.setName(WORKING_REPORTER);
			line.setState(LINE_STANDING);
			line.setSeq(0);
			line.setOwner(user.getName());
			line.setReadCheck(true);
			line.setMaster(am);
			line = (ApprovalLine) PersistenceHelper.manager.save(line);

			for (int i = 0; approveUser != null && i < approveUser.length; i++) {
				line = ApprovalLine.newApprovalLine();
				if (isWorkItem) {
					line.setName(WORKING_WORKING);
				} else {
					line.setName(WORKING_REVIEWER);
				}
				line.setState(LINE_STANDING);
				line.setSeq(i + 1);
				user = (WTUser) rf.getReference(approveUser[i]).getObject();
				line.setOwner(user.getName());
				line.setReadCheck(false);
				line.setMaster(am);
				line = (ApprovalLine) PersistenceHelper.manager.save(line);
				approveList.add(approveUser[i]);
			}

			ArrayList tempList = new ArrayList();

			for (int i = 0; tempUser != null && i < tempUser.length; i++) {
				line = ApprovalLine.newApprovalLine();
				line.setName(WORKING_TEMP);
				line.setState(LINE_STANDING);
				line.setSeq(100);
				user = (WTUser) rf.getReference(tempUser[i]).getObject();
				line.setOwner(user.getName());
				line.setReadCheck(false);
				line.setMaster(am);
				line = (ApprovalLine) PersistenceHelper.manager.save(line);
				tempList.add(tempUser[i]);
			}
			template.setApproveList(approveList);
			template.setTempList(tempList);
			PersistenceHelper.manager.save(template);

			request(am);

			trx.commit();
			trx = null;

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new WTException(ex);
		} finally {
			if (trx != null)
				trx.rollback();
		}

	}

	/**
	 *
	 * @param per             ��ü Object
	 * @param oldPer          ���� Iteration ��ü Object
	 * @param preDiscussUser  ���� �� ���� ����Ʈ
	 * @param discussUser     ���� ����Ʈ
	 * @param postDiscussUser ���� ����Ʈ
	 * @param reportUser      �뺸�� ����Ʈ
	 * @param isWorkItem      EO, ECR�� Task�̿ܿ��� false
	 * @param actionType      create, update,(delete)
	 * @param discussType     sequence, parallel
	 * @param isInWork        false(���簡 �����), true(�ӽ��������)
	 * @throws Exception
	 */
	public void registApproval(Persistable per, Persistable oldPer, String[] preDiscussUser, String[] discussUser,
			String[] postDiscussUser, String[] tempUser, boolean isWorkItem, String actionType, String discussType,
			boolean isInWork) throws Exception {

		if (!SERVER) {
			Class argTypes[] = new Class[] { Persistable.class, Persistable.class, String[].class, String[].class,
					String[].class, String[].class, boolean.class, String.class, String.class, boolean.class };
			Object args[] = new Object[] { per, oldPer, preDiscussUser, discussUser, postDiscussUser, tempUser,
					new Boolean(isWorkItem), actionType, discussType, new Boolean(isInWork) };
			try {
				RemoteMethodServer.getDefault().invoke("registApproval", null, this, argTypes, args);
				return;
			} catch (RemoteException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new WTException(e);
			}
		}

		Transaction trx = new Transaction();
		try {
			trx.start();

			ReferenceFactory rf = new ReferenceFactory();

			if ((preDiscussUser == null || preDiscussUser.length == 0)
					&& (discussUser == null || discussUser.length == 0)
					&& (postDiscussUser == null || postDiscussUser.length == 0)
					&& (tempUser == null || tempUser.length == 0) && !actionType.equals("update")) {

				ApprovalMaster am = ApprovalMaster.newApprovalMaster();

				am.setState(MASTER_INWORK);
				setPersistableState(per, "INWORK");

				am.setOwner(SessionHelper.manager.getPrincipal().getName());
				am.setActive(0);
				am.setOrderType(discussType);
				am = (ApprovalMaster) PersistenceHelper.manager.save(am);

				ApprovalObjectLink link = ApprovalObjectLink.newApprovalObjectLink(per, am);
				PersistenceHelper.manager.save(link);
				WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
				int seq = 0;

				ApprovalLine line = ApprovalLine.newApprovalLine();
				line.setName(WORKING_REPORTER);
				if (isInWork) {
					line.setState(LINE_INWORKING);
				} else {
					line.setState(LINE_REQUEST);
				}
				line.setSeq(seq);
				line.setOwner(user.getName());
				line.setReadCheck(true);
				line.setMaster(am);
				line.setStepName(APPROVE_REQUEST);
				line = (ApprovalLine) PersistenceHelper.manager.save(line);

				trx.commit();
				trx = null;
				return;
			}

			if (actionType.equals("create")) {

				ApprovalMaster am = ApprovalMaster.newApprovalMaster();

				if (isInWork) {
					// if(isWorkItem){
					// am.setState(MASTER_WORKING);
					// }else{
					am.setState(MASTER_INWORK);
					setPersistableState(per, "INWORK");
					// }
				} else {
					if (isWorkItem) {
						am.setState(MASTER_WORKING);
						setPersistableState(per, "WORKING");
					} else {
						am.setState(MASTER_APPROVING);
						// setPersistableState(per, "UNDERREVIEW");
						setPersistableState(per, "APPROVEING");
					}
				}

				am.setOwner(SessionHelper.manager.getPrincipal().getName());
				am.setActive(0);
				am.setOrderType(discussType);
				am = (ApprovalMaster) PersistenceHelper.manager.save(am);

				ApprovalObjectLink link = ApprovalObjectLink.newApprovalObjectLink(per, am);
				PersistenceHelper.manager.save(link);
				WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
				ApprovalLineTemplate template = getApprovalTemplate(user);
				if (template == null) {
					template = ApprovalLineTemplate.newApprovalLineTemplate();
					template.setOwner(user.getName());
				}
				template.setOrderType(discussType);
				int seq = 0;

				ArrayList approveList = new ArrayList();
				ApprovalLine line = ApprovalLine.newApprovalLine();
				line.setName(WORKING_REPORTER);
				if (isInWork) {
					line.setState(LINE_INWORKING);
				} else {
					line.setState(LINE_REQUEST);
				}
				line.setSeq(seq);
				line.setOwner(user.getName());
				line.setReadCheck(true);
				line.setMaster(am);
				line.setStepName(APPROVE_REQUEST);
				line = (ApprovalLine) PersistenceHelper.manager.save(line);

				for (int i = 0; preDiscussUser != null && i < preDiscussUser.length; i++) {
					line = ApprovalLine.newApprovalLine();
					if (isWorkItem) {
						line.setName(WORKING_WORKING);
					} else {
						line.setName(WORKING_REVIEWER);
					}
					line.setState(LINE_STANDING);
					seq += 1;
					line.setSeq(seq);
					user = (WTUser) rf.getReference(preDiscussUser[i]).getObject();
					line.setOwner(user.getName());
					line.setReadCheck(false);
					line.setMaster(am);
					line.setStepName(APPROVE_PREAPPROVE);
					line = (ApprovalLine) PersistenceHelper.manager.save(line);
					approveList.add(preDiscussUser[i]);
				}
				template.setPreDiscussList(approveList);
				approveList = new ArrayList();

				boolean isFirst = true;
				for (int i = 0; discussUser != null && i < discussUser.length; i++) {
					line = ApprovalLine.newApprovalLine();
					if (isWorkItem) {
						line.setName(WORKING_WORKING);
					} else {
						line.setName(WORKING_DISCUSSER);
					}
					line.setState(LINE_STANDING);

					if (isFirst) {
						if (discussType.equals("parallel"))
							seq += 1;
						isFirst = false;
					}

					if (discussType.equals("parallel")) {
						line.setSeq(seq);
					} else {
						seq += 1;
						line.setSeq(seq);
					}

					user = (WTUser) rf.getReference(discussUser[i]).getObject();
					line.setOwner(user.getName());
//                    System.out.println("user.getName()------->>>"+user.getName());
					line.setReadCheck(false);
					line.setMaster(am);
					line.setStepName(APPROVE_DISCUSS);
					line = (ApprovalLine) PersistenceHelper.manager.save(line);
					approveList.add(discussUser[i]);
				}
				template.setDiscussList(approveList);
				approveList = new ArrayList();

				for (int i = 0; postDiscussUser != null && i < postDiscussUser.length; i++) {
					line = ApprovalLine.newApprovalLine();
					if (isWorkItem) {
						line.setName(WORKING_WORKING);
					} else {
						line.setName(WORKING_REVIEWER);
					}
					line.setState(LINE_STANDING);
					if (isFirst) {
						seq += 1;
						isFirst = false;
					} else if (i > 0)
						seq += 1;
					line.setSeq(seq);
					user = (WTUser) rf.getReference(postDiscussUser[i]).getObject();
					line.setOwner(user.getName());
					line.setReadCheck(false);
					line.setMaster(am);
					line.setStepName(APPROVE_POSTAPPROVE);
					line = (ApprovalLine) PersistenceHelper.manager.save(line);
					approveList.add(postDiscussUser[i]);
				}
				template.setPostDiscussList(approveList);
				approveList = new ArrayList();

				for (int i = 0; tempUser != null && i < tempUser.length; i++) {
					line = ApprovalLine.newApprovalLine();
					line.setName(WORKING_TEMP);
					line.setState(LINE_STANDING);
					line.setSeq(100);
					user = (WTUser) rf.getReference(tempUser[i]).getObject();
					line.setOwner(user.getName());
					line.setReadCheck(false);
					line.setMaster(am);
					line.setStepName(APPROVE_NOTIFICATE);
					line = (ApprovalLine) PersistenceHelper.manager.save(line);
					approveList.add(tempUser[i]);
				}
				template.setTempList(approveList);
				PersistenceHelper.manager.save(template);

				// ���� ��û�϶��� ȣ��
				if (!isInWork)
					request(am);

			} // end create
			else if (actionType.equals("update")) {

				if (oldPer == null) {
					System.out.println("ERROR: Not found Old Persistable");

					trx.commit();
					trx = null;
					return;
				}

				ApprovalMaster am = getApprovalMaster(oldPer);
				if (am == null) {
					System.out.println("ERROR: Not found ApprovalMaster :" + per);

					trx.commit();
					trx = null;
					return;
				}

				// ���� ��ũ ����
				deleteApprovalMasterLink(oldPer);

				/**
				 * @Todo
				 *
				 *       @�����϶� ������ ���� ����Ʈ�� �����ϰ� �ٽ� �߰��Ѵ�.
				 *
				 *       @�ݷ��� ���� ���ۼ��϶� ���ʷ� ���� �����̱� ������ �������� �߰��� ���縮��Ʈ��
				 *       �߰��Ѵ�. �ݷ��� ���� ���ۼ� ���´� master�� �ݷ� �϶��� üũ
				 *
				 *       @�ݷ��� ���� ���ۼ� �϶� ������ ���� ����Ʈ�� �����ϰ� �ٽ� �߰��Ѵ�.
				 **/

				// �뺸�ڴ� ������ ������ ���߰���
				deleteNotiLine(am);

				// �ݷ� ������ ���� ���Žÿ� �ߺ��� �߻��Ҽ� �־�
				// ��⸦ ���-ȸ�� ���·� �����Ѵ�.
				// changeStandingLine(am);

				int seq = 0;
				int maxSeq = maxSeq(am);
				boolean isRejectedState = false;
				boolean haveRejected = checkRejectLine(am);
				// System.out.println("am.getState() = " + am.getState());
				// System.out.println("haveRejected = " + haveRejected);
				// System.out.println("seq = " + seq);

				// �ݷ��� �ִ��� üũ
				if (haveRejected) {
					// ���� �ݷ��� �������� üũ
					if (am.getState().equals(MASTER_REJECTED)) {
						isRejectedState = true;
						// �ݷ����� ���� ���ۼ��̱� ������ �缳�� �ʿ���
						// System.out.println("am.setActive() = " + am.getActive());
						seq = maxSeq + 1;
						if (seq == 1)
							seq = 0;
						am.setActive(seq);
						// System.out.println("am.setActive() = " + am.getActive());

					} else {
						isRejectedState = false;
						deleteLatestApprovalLine(am);
						maxSeq = maxSeq(am);
						seq = maxSeq + 1;
						if (seq == 1)
							seq = 0;
						am.setActive(seq);
						// �ݷ����� ���� ���ۼ��� �ƴϱ� ������ ���� �ʿ����
						// am.setActive( maxSeq(am)+1 );
					}
				} else {
					// ��������, �ݷ�����
					deleteLatestApprovalLine(am);
					maxSeq = maxSeq(am);
					seq = maxSeq + 1;
					if (seq == 1)
						seq = 0;
					am.setActive(seq);
					// am.setActive �� �ʿ� ����
				}

				if (isWorkItem) {
					am.setState(MASTER_WORKING);
					setPersistableState(per, "WORKING");
				} else if (!isInWork) {
					// ���� ��û ����
					if (isRejectedState) {
						// ���� �ݷ��� ���¸� �����û
						am.setState(MASTER_APPROVING);
						// setPersistableState(per, "UNDERREVIEW");
						setPersistableState(per, "APPROVEING");

					} else if (haveRejected) {
						// �ݷ� �̷��� �ְ�, �����Ͽ� ���� ��û
						// if(am.getState().equals(MASTER_REJECTED) ){
						am.setState(MASTER_APPROVING);
						// setPersistableState(per, "UNDERREVIEW");
						setPersistableState(per, "APPROVEING");
						// }else if(am.getState().equals(MASTER_WITHDRAWAL) ){
						// am.setState(MASTER_APPROVING);
						// }else{
						// am.setState(MASTER_APPROVING);
						// }

					} else {
						// �ݷ��� ���� ���� ��û
						am.setState(MASTER_APPROVING);
						// setPersistableState(per, "UNDERREVIEW");
						setPersistableState(per, "APPROVEING");
					}

				} else {
					// �ӽ� ���� ����
					if (isRejectedState) {
						// ���� �ݷ��� ���¿��� ���ۼ�
						am.setState(MASTER_REWORKING);
//                        setPersistableState(per, "REWORK");
						setPersistableState(per, "APPROVEING");

					} else if (haveRejected) {
						// �ݷ� �̷��� �ְ�, �����Ͽ� ���� ��û
						// if(am.getState().equals(MASTER_REJECTED) ){
						am.setState(MASTER_REWORKING);
//                        setPersistableState(per, "REWORK");
						setPersistableState(per, "APPROVEING");
						// }else if(am.getState().equals(MASTER_WITHDRAWAL) ){
						// am.setState(MASTER_APPROVING);
						// }else{
						// am.setState(MASTER_APPROVING);
						// }

					} else {
						// �ݷ��� ���� �ӽ�����
						am.setState(MASTER_INWORK);
//                        setPersistableState(per, "INWORK");
						setPersistableState(per, "APPROVEING");
					}

				}
				// am.setActive( maxSeq(am)+1 );

				am.setOwner(SessionHelper.manager.getPrincipal().getName());
				am.setOrderType(discussType);
				am = (ApprovalMaster) PersistenceHelper.manager.save(am);

				ApprovalObjectLink link = ApprovalObjectLink.newApprovalObjectLink(per, am);
				PersistenceHelper.manager.save(link);
				WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
				ApprovalLineTemplate template = getApprovalTemplate(user);

				if (template == null) {
					template = ApprovalLineTemplate.newApprovalLineTemplate();
					template.setOwner(user.getName());
				}
				template.setOrderType(discussType);

				// �ݷ�/ȸ�� ���� ��� ���۾� ���¸� �߰� �ؾ��Ѵ�.
				ArrayList approveList = new ArrayList();
				ApprovalLine line = null;
				// if( checkRejectLine(am) ) {
				line = ApprovalLine.newApprovalLine();
				line.setName(WORKING_REPORTER);
				if (isInWork) {
					if (haveRejected) {
						line.setState(LINE_REWORKING);
					} else {
						line.setState(LINE_INWORKING);
					}
				} else {
					line.setState(LINE_REQUEST);
				}
				line.setSeq(seq);
				line.setOwner(user.getName());
				line.setReadCheck(true);
				line.setMaster(am);
				line.setStepName(APPROVE_REQUEST);
				line = (ApprovalLine) PersistenceHelper.manager.save(line);
				// }

				for (int i = 0; preDiscussUser != null && i < preDiscussUser.length; i++) {
					line = ApprovalLine.newApprovalLine();
					if (isWorkItem) {
						line.setName(WORKING_WORKING);
					} else {
						line.setName(WORKING_REVIEWER);
					}
					line.setState(LINE_STANDING);
					seq += 1;
					line.setSeq(seq);
					user = (WTUser) rf.getReference(preDiscussUser[i]).getObject();
					line.setOwner(user.getName());
					line.setReadCheck(false);
					line.setMaster(am);
					line.setStepName(APPROVE_PREAPPROVE);
					line = (ApprovalLine) PersistenceHelper.manager.save(line);
					approveList.add(preDiscussUser[i]);
				}
				template.setPreDiscussList(approveList);
				approveList = new ArrayList();

				boolean isFirst = true;
				for (int i = 0; discussUser != null && i < discussUser.length; i++) {
					line = ApprovalLine.newApprovalLine();
					if (isWorkItem) {
						line.setName(WORKING_WORKING);
					} else {
						line.setName(WORKING_DISCUSSER);
					}
					line.setState(LINE_STANDING);

					if (isFirst) {
						if (discussType.equals("parallel"))
							seq += 1;
						isFirst = false;
					}

					if (discussType.equals("parallel")) {
						line.setSeq(seq);
					} else {
						seq += 1;
						line.setSeq(seq);
					}

					user = (WTUser) rf.getReference(discussUser[i]).getObject();
					line.setOwner(user.getName());
					line.setReadCheck(false);
					line.setMaster(am);
					line.setStepName(APPROVE_DISCUSS);
					line = (ApprovalLine) PersistenceHelper.manager.save(line);
					approveList.add(discussUser[i]);
				}
				template.setDiscussList(approveList);
				approveList = new ArrayList();

				for (int i = 0; postDiscussUser != null && i < postDiscussUser.length; i++) {
					line = ApprovalLine.newApprovalLine();
					if (isWorkItem) {
						line.setName(WORKING_WORKING);
					} else {
						line.setName(WORKING_REVIEWER);
					}
					line.setState(LINE_STANDING);
					if (isFirst) {
						seq += 1;
						isFirst = false;
					} else if (i > 0)
						seq += 1;
					line.setSeq(seq);
					user = (WTUser) rf.getReference(postDiscussUser[i]).getObject();
					line.setOwner(user.getName());
					line.setReadCheck(false);
					line.setMaster(am);
					line.setStepName(APPROVE_POSTAPPROVE);
					line = (ApprovalLine) PersistenceHelper.manager.save(line);
					approveList.add(postDiscussUser[i]);
				}
				template.setPostDiscussList(approveList);
				approveList = new ArrayList();

				for (int i = 0; tempUser != null && i < tempUser.length; i++) {
					line = ApprovalLine.newApprovalLine();
					line.setName(WORKING_TEMP);
					line.setState(LINE_STANDING);
					line.setSeq(100);
					user = (WTUser) rf.getReference(tempUser[i]).getObject();
					line.setOwner(user.getName());
					line.setReadCheck(false);
					line.setMaster(am);
					line.setStepName(APPROVE_NOTIFICATE);
					line = (ApprovalLine) PersistenceHelper.manager.save(line);
					approveList.add(tempUser[i]);
				}
				template.setTempList(approveList);
				PersistenceHelper.manager.save(template);

				// ���� ��û�϶��� ȣ��
				if (!isInWork)
					request(am);

			}

			trx.commit();
			trx = null;

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new WTException(ex);
		} finally {
			if (trx != null)
				trx.rollback();
		}
	}

	/**
	 * @Todo �ϰ� ���� ��û
	 * @param appList ���� ��û�� ����Ʈ
	 */
	public void requestMultiApproval(ArrayList appList, String title, String[] preDiscussUser, String[] discussUser,
			String[] postDiscussUser, String[] tempUser, boolean isWorkItem, String actionType, String discussType,
			boolean isInWork) throws Exception {

		if (!SERVER) {
			Class argTypes[] = new Class[] { ArrayList.class, String.class, String[].class, String[].class,
					String[].class, String[].class, boolean.class, String.class, String.class, boolean.class };
			Object args[] = new Object[] { appList, title, preDiscussUser, discussUser, postDiscussUser, tempUser,
					new Boolean(isWorkItem), actionType, discussType, new Boolean(isInWork) };
			try {
				RemoteMethodServer.getDefault().invoke("requestMultiApproval", null, this, argTypes, args);
				return;
			} catch (RemoteException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new WTException(e);
			}
		}

		Transaction trx = new Transaction();
		try {
			trx.start();

			// 1. �ϰ����� ��ü ����
			MultiApproval mapp = MultiApproval.newMultiApproval();
			mapp.setTitle(title);

			// 2. ����� ��ǰ, ������ ��ũ ����
			if (appList != null) {
				Persistable obj = null;
				String objOid = "";
				ApprovalToObjectLink appLink = null;
				mapp = (MultiApproval) PersistenceHelper.manager.save(mapp);

				for (int i = 0; appList.size() > i; i++) {
					objOid = (String) appList.get(i);
					obj = WCUtil.getPersistable(objOid);

					appLink = ApprovalToObjectLink.newApprovalToObjectLink(mapp, obj);
					PersistenceHelper.manager.save(appLink);
				}

				ApprovalHelper.manager.registApproval(mapp, null, preDiscussUser, discussUser, postDiscussUser,
						tempUser, isWorkItem, "create", discussType, isInWork);
			}

			trx.commit();
			trx = null;

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new WTException(ex);
		} finally {
			if (trx != null)
				trx.rollback();
		}
	}

	public void request(ApprovalMaster master) throws Exception {
		// System.out.println("@@ request(master) = " + master + " , oid =" +
		// master.getPersistInfo().getObjectIdentifier().getId());
		commit(master, LINE_REQUEST);
	}

	public void commit(ApprovalMaster master, String line_state) throws Exception {
		// System.out.println("@@111 commit(master, line_state) = " + master + ", " +
		// line_state + " , oid =" +
		// master.getPersistInfo().getObjectIdentifier().getId());

		QueryResult qr = activeLine(master);
		ApprovalLine line = null;

		if (qr != null) {
			// System.out.println("@@111 qr = " + qr.size());
			if (qr.hasMoreElements()) {
				line = (ApprovalLine) qr.nextElement();

				// System.out.println("@@111 line = " + line + " , oid =" +
				// line.getPersistInfo().getObjectIdentifier().getId());
				commit(line, line_state);
			}
		} else {
			// System.out.println("@@111 qr = NULL");
		}
	}

	public void commit(ApprovalLine line, String line_state) throws Exception {

		if (!SERVER) {
			Class argTypes[] = new Class[] { ApprovalLine.class, String.class };
			Object args[] = new Object[] { line, line_state };
			try {
				RemoteMethodServer.getDefault().invoke("commit", null, this, argTypes, args);
				return;
			} catch (RemoteException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new WTException(e);
			}
		}

		Transaction trx = new Transaction();
		try {
			trx.start();

			// System.out.println("@@222 commit(line, line_state) = " + line + ", " +
			// line_state + " , oid =" +
			// line.getPersistInfo().getObjectIdentifier().getId());

			line.setState(line_state);
			Timestamp time = new Timestamp(new java.util.Date().getTime());
			line.setApproveDate(time);

			line = (ApprovalLine) PersistenceHelper.manager.modify(line);

			// System.out.println("@@ line = " + line.toString());
			ApprovalMaster master = line.getMaster();
			// System.out.println("@@ master = " + master.toString());
			Persistable per = getApprovalObject(master);
			WTUser notiUser = null;
			ApprovalData data = new ApprovalData(master);
			// sendModifyWorkItem(per, master, line);

			String gubun = "";
			try {
				if (data != null) {
					if (data.getGubun().length() > 0) {
						gubun = "[" + data.getGubun() + "]";
					}
				}
			} catch (Exception e) {
			}

			if (LINE_REJECTED.equals(line_state)) {
				cancel(line);

				// �ݷ� ������ ���� ���Žÿ� �ߺ��� �߻��Ҽ� �־�
				// ��⸦ ���-ȸ�� ���·� �����Ѵ�.
				changeStandingLine(line.getMaster());

			} else if (LINE_WITHDRAWAL.equals(line_state)) {
				withdrawal(line);
			} else {

				// ���� ������ ������� ���� ���ǰ� �ִ��� üũ
				// ���� �ȵ� ���ǰ� ������ ���� �������� �ѱ��� ����
				if (endActiveStep(line.getMaster())) {

					QueryResult qr = nextLine(line);
					ApprovalLine nextLine = null;
					int seq = -1;

					if (qr != null) {
						// System.out.println("@@222 qr = " + qr.size());
						if (qr.hasMoreElements()) {
							nextLine = (ApprovalLine) qr.nextElement();
							seq = nextLine.getSeq();
							// System.out.println("@@222 nextLine = " + nextLine + " , oid =" +
							// nextLine.getPersistInfo().getObjectIdentifier().getId());

							if (nextLine == null) {
								// System.out.println("@@222 nextLine = NULL");
								complete(line);
							} else {
								if (true) {
									// System.out.println("@@222 nextLine NOT NULL");
									if (WORKING_WORKING.equals(nextLine.getName())) {
										nextLine.setState(LINE_WORKING);
										nextLine.setStartTaskDate(time);
									} else {
										nextLine.setState(LINE_APPROVING);
										nextLine.setStartTaskDate(time);
									}

//                                    System.out.println("nextLine.getOwner() : " + nextLine.getOwner());
									notiUser = CommonUtil.findUserID(nextLine.getOwner());
//                                    System.out.println("notiUser : " + notiUser);

									// System.out.println("@@ Noti = " + notiUser.getEMail() + " , fullName = " +
									// notiUser.getFullName());

									HashMap toHash = new HashMap();
									toHash.put(notiUser.getEMail(), notiUser.getFullName());

									String creatorName = getCreatorFullName(per);

									toHash.put("createDate", getCreateTime(per));
									toHash.put("creater", creatorName);
									toHash.put("title", data.getTitle());
									toHash.put("eMail", notiUser.getEMail());
									toHash.put("fullName", notiUser.getFullName());

									if (WORKING_WORKING.equals(nextLine.getName())) {
//                                    	MailUtilmailObjMailSendSetting(nextLine, (Object)per, toHash,
//                                                "[PDM ���ڰ���]"+gubun+" �۾� ��û�� �����Ǿ����ϴ�.", "WorkCenter���� Ȯ���ϼ���.", "requestApproval",true);

									} else {

//                                    	MailUtilmailObjMailSendSetting(nextLine, (Object)per, toHash,
//                                            "[PDM ���ڰ���]"+gubun+" ���� ��û�� �����Ǿ����ϴ�.", "WorkCenter���� Ȯ���ϼ���.", "requestApproval");
									}
									nextLine = (ApprovalLine) PersistenceHelper.manager.modify(nextLine);

									boolean isDiscuss = nextLine.getName().equals(WORKING_DISCUSSER);
									boolean isFirst = line.getName().equals(WORKING_REPORTER);
									String discusserList = "";

									if (isDiscuss) {
										discusserList = StringUtil.checkNull(getUserNo(nextLine.getOwner()));

									} else {
										if (isFirst) {
											sendCreateWorkItem(per, master, line, nextLine);
										} else {
											sendModifyWorkItem(per, master, line, nextLine);
										}
									}

									// ���� �۾� ������ ���� ó��
									while (qr.hasMoreElements()) {
										nextLine = (ApprovalLine) qr.nextElement();

										if (seq == nextLine.getSeq()) {
											if (WORKING_WORKING.equals(nextLine.getName())) {
												if (nextLine.getState().equals(LINE_STANDING)) {
													nextLine.setState(LINE_WORKING);
													nextLine.setStartTaskDate(time);
												}
											} else {
												if (nextLine.getState().equals(LINE_STANDING)) {
													nextLine.setState(LINE_APPROVING);
													nextLine.setStartTaskDate(time);
												}
											}

											notiUser = CommonUtil.findUserID(nextLine.getOwner());

											// System.out.println("@@ Noti = " + notiUser.getEMail() + " , fullName = "
											// + notiUser.getFullName());

											toHash = new HashMap();
											toHash.put(notiUser.getEMail(), notiUser.getFullName());

											creatorName = getCreatorFullName(per);

											toHash.put("createDate", getCreateTime(per));
											toHash.put("creater", creatorName);
											toHash.put("title", data.getTitle());
											toHash.put("eMail", notiUser.getEMail());
											toHash.put("fullName", notiUser.getFullName());

											if (WORKING_WORKING.equals(nextLine.getName())) {
//                                            	MailUtilmailObjMailSendSetting(nextLine, (Object)per, toHash,
//                                                        "[PDM ���ڰ���]"+gubun+" �۾� ��û�� �����Ǿ����ϴ�.", "WorkCenter���� Ȯ���ϼ���.", "requestApproval",true);

											} else {
//                                            	MailUtilmailObjMailSendSetting(nextLine, (Object)per, toHash,
//                                                    "[PDM ���ڰ���]"+gubun+" ���� ��û�� �����Ǿ����ϴ�.", "WorkCenter���� Ȯ���ϼ���.", "requestApproval");
											}
											nextLine = (ApprovalLine) PersistenceHelper.manager.modify(nextLine);

											if (isDiscuss) {
												discusserList = discusserList + "|"
														+ StringUtil.checkNull(getUserNo(nextLine.getOwner()));
											}
										}
									}

									if (isDiscuss) {
										if (isFirst) {
											sendCreateDiscussWorkItem(per, master, line, nextLine, discusserList);
										} else {
											sendModifyDiscussWorkItem(per, master, line, nextLine, discusserList);
										}
									}

									master.setActive(nextLine.getSeq());
									master = (ApprovalMaster) PersistenceHelper.manager.modify(master);
								}
							}

						} // end while
					} else {
//                    	if(per instanceof EChangeRequest2) {
//                    		EChangeRequest2 ecr = (EChangeRequest2)per;
//                    		if(ecr.getTermicateType() == null || !"lineSave".equals(ecr.getTermicateType()) ) {
//	                    		ApprovalMaster am = line.getMaster();
//	                    		seq = line.getSeq();
//	                    		String line2 = line.getOwner();
//	                    		
//	                            line = ApprovalLine.newApprovalLine();
//	                            line.setName(WORKING_REVIEWER);
//	                            line.setState(LINE_APPROVING);
//	                            seq += 1;
//	                            line.setSeq(seq);
//	                            WTUser user = ecr.getWorker();
//	                            line.setOwner(user.getName());
//	                            line.setReadCheck(false);
//	                            line.setMaster(am);
//	                            line.setStepName(APPROVE_POSTAPPROVE);
//	                            line = (ApprovalLine)PersistenceHelper.manager.save(line);
//	                            am.setActive(seq);
//	                            PersistenceHelper.manager.save(am);
//	
//	                            line = ApprovalLine.newApprovalLine();
//	                            line.setName(WORKING_REVIEWER);
//		                        line.setState(LINE_STANDING);
//		                        seq += 1;
//		                        line.setSeq(seq);
//		                        line.setOwner(line2);
//		                        line.setReadCheck(false);
//		                        line.setMaster(am);
//		                        line.setStepName(APPROVE_POSTAPPROVE);
//		                        line = (ApprovalLine)PersistenceHelper.manager.save(line);
//		                            
//		                        ecr.setTermicateType("lineSave");
//		                        PersistenceHelper.manager.save(ecr);
//		                    }else {
//		                    	complete(line);
//		                    }
//                    	}else {
						// System.out.println("@@222 qr = NULL");
						complete(line);
//                    	}
					}
				} // 978line
			}

			// ���� ��ó�� ����

			if (line.getSeq() == 0) {

				ReferenceFactory rf = new ReferenceFactory();
				Persistable pbo = (Persistable) rf
						.getReference(data.obj.getPersistInfo().getObjectIdentifier().toString()).getObject();
				WTUser puser = (WTUser) OrganizationServicesHelper.manager.getAuthenticatedUser(data.master.getOwner());

				HashMap toHash = new HashMap();
				toHash.put(puser.getEMail(), puser.getFullName());

				String creatorName = getCreatorFullName(pbo);

				toHash.put("eMail", puser.getEMail());
				toHash.put("fullName", puser.getFullName());
				toHash.put("createDate", getCreateTime(pbo));
				toHash.put("creater", creatorName);
				toHash.put("title", data.getTitle());

				MailUtil.mailObjMailSendSetting(line, (Object) pbo, toHash,
						"[PDM ���ڰ���]" + gubun + "  " + line_state + " �Ǿ����ϴ�.",
						"[PDM ���ڰ���]" + gubun + " " + line_state + " �Ǿ����ϴ�.", "create");

				QueryResult notiLines = getNotifier(line);
				ApprovalLine notiLine = null;
				WTUser notiUser2 = null;

				if (notiLines != null) {
					while (notiLines.hasMoreElements()) {
						notiLine = (ApprovalLine) notiLines.nextElement();
						notiUser2 = CommonUtil.findUserID(notiLine.getOwner());

						toHash = new HashMap();
						toHash.put(notiUser2.getEMail(), notiUser2.getFullName());

						toHash.put("eMail", notiUser2.getEMail());
						toHash.put("fullName", notiUser2.getFullName());
						toHash.put("createDate", getCreateTime(pbo));
						toHash.put("creater", creatorName);
						toHash.put("title", data.getTitle());

						MailUtil.mailObjMailSendSetting(line, (Object) pbo, toHash,
								"[PDM ���ڰ���]" + gubun + "  " + line_state + " �Ǿ����ϴ�.",
								"[PDM ���ڰ���]" + gubun + " " + line_state + " �Ǿ����ϴ�.", "create");
					}
				}

			}
			trx.commit();
			trx = null;

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new WTException(ex);
		} finally {
			if (trx != null)
				trx.rollback();
		}
	}

	public String getCreatorFullName(Object pbo) throws Exception {
		String creatorName = "";

		if (pbo instanceof EChangeActivity) {
			creatorName = ((WTUser) SessionHelper.manager.getPrincipal()).getFullName();
		}
		if (pbo instanceof OwnPersistable) {
			creatorName = ((OwnPersistable) pbo).getOwner().getFullName();
		} else if (pbo instanceof Versioned) {
			creatorName = ((Versioned) pbo).getCreator().getFullName();
		} else if (pbo instanceof Managed) {
			creatorName = ((Managed) pbo).getCreator().getFullName();
		}
		return creatorName;
	}

	public String getCreatorName(Object pbo) throws Exception {
		String creatorName = "";

		if (pbo instanceof OwnPersistable) {
			creatorName = ((OwnPersistable) pbo).getOwner().getName();
		} else if (pbo instanceof Versioned) {
			creatorName = ((Versioned) pbo).getCreator().getName();
		} else if (pbo instanceof Managed) {
			creatorName = ((Managed) pbo).getCreator().getName();
		}
		return creatorName;
	}

	public boolean endActiveStep(ApprovalMaster master) throws Exception {
		QueryResult qr = activeLine(master);
		ApprovalLine line = null;
		boolean isEnd = true;
		Timestamp isReadCheck = null;

		if (qr != null) {
			while (qr.hasMoreElements()) {
				line = (ApprovalLine) qr.nextElement();

				if (APPROVE_POSTAPPROVE.equals(line.getStepName())) {
					// ���� ���� ���� step�� �ִ��� üũ
					isReadCheck = line.getApproveDate();
					// System.out.println("@@ isReadCheck = " + isReadCheck + " ,, line = " + line +
					// " , oid =" + line.getPersistInfo().getObjectIdentifier().getId());
					if (isReadCheck == null) {
						isEnd = false;
					}
				}
			}
		}

		// System.out.println("@@ endActiveStep() = " + isEnd);
		return isEnd;
	}

	public void removeProcess(Persistable per) throws Exception {
		ApprovalMaster master = getApprovalMaster(per);
		if (master == null)
			return;
		remove(master);
	}

	public void remove(ApprovalMaster master) throws Exception {

		if (!SERVER) {
			Class argTypes[] = new Class[] { ApprovalMaster.class };
			Object args[] = new Object[] { master };
			try {
				RemoteMethodServer.getDefault().invoke("remove", null, this, argTypes, args);
				return;
			} catch (RemoteException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new WTException(e);
			}
		}

		Transaction trx = new Transaction();
		try {
			trx.start();

			QueryResult qr = getApprovalLine(master);

			if (qr != null) {
				while (qr.hasMoreElements()) {
					ApprovalLine line = (ApprovalLine) qr.nextElement();

					sendDeleteWorkItem((Persistable) getApprovalObject(master), master, line, line);
					PersistenceHelper.manager.delete(line);
				}

				PersistenceHelper.manager.delete(master);
			}

			trx.commit();
			trx = null;

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new WTException(ex);
		} finally {
			if (trx != null)
				trx.rollback();
		}
	}

	public void remove(ApprovalLine line) throws Exception {
		Transaction trx = new Transaction();
		try {
			trx.start();

			sendDeleteWorkItem(getApprovalObject(line.getMaster()), line.getMaster(), line, line);

			PersistenceHelper.manager.delete(line);

			trx.commit();
			trx = null;

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new WTException(ex);
		} finally {
			if (trx != null)
				trx.rollback();
		}
	}

	public void complete(ApprovalLine line) throws Exception {

		ApprovalMaster master = line.getMaster();
		Persistable per = getApprovalObject(master);

		master.setState(MASTER_APPROVED);
		// setPersistableState( per, "RELEASED");
		setPersistableState(per, "APPROVED");
		master = (ApprovalMaster) PersistenceHelper.manager.modify(master);

		ApprovalData data = new ApprovalData(master);
		data.completeAction(MASTER_APPROVED);

		String gubun = "";
		try {
			if (data != null) {
				if (data.getGubun().length() > 0) {
					gubun = "[" + data.getGubun() + "]";
				}
			}
		} catch (Exception e) {
		}

		/**
		 * @Todo
		 *
		 *       ������ ���缱���� ������ ���� �����ڿ��� ���� ���� queryLastApprovalLine()
		 *
		 */

//        QueryResult notiLines = getNotifier(line);
		QueryResult notiLines = getApprovalLine(line.getMaster());
		ApprovalLine notiLine = null;
		WTUser notiUser = null;
		ReferenceFactory rf = new ReferenceFactory();

		if (notiLines != null) {
			while (notiLines.hasMoreElements()) {
				notiLine = (ApprovalLine) notiLines.nextElement();
				notiUser = CommonUtil.findUserID(notiLine.getOwner());

				System.out.println("@@ Noti = " + notiUser.getEMail() + " , fullName = " + notiUser.getFullName());

				HashMap toHash = new HashMap();
				toHash.put(notiUser.getEMail(), notiUser.getFullName());

				String creatorName = getCreatorFullName(per);

				toHash.put("createDate", getCreateTime(per));
				toHash.put("creater", creatorName);
				toHash.put("title", data.getTitle());
				toHash.put("eMail", notiUser.getEMail());
				toHash.put("fullName", notiUser.getFullName());

				MailUtil.mailObjMailSendSetting(notiLine, (Object) per, toHash,
						"[PDM ���ڰ���]" + gubun + " ���簡 �Ϸ�Ǿ����ϴ�.", "PDM System���� Ȯ���ϼ���.", "notify");

			}
		}

		// GW�� ���� ���� ����
		sendFinishWorkItem(per, master, line, line);

		if (per instanceof EChangeRequest2) {
			/* ERPHistory Create */
			ERPHistory history = ERPHistory.newERPHistory();
			history.setEo(ObjectReference.newObjectReference((EChangeRequest2) per));
			PersistenceHelper.manager.save(history);

			ERPECRHelper.manager.erpECR((EChangeRequest2) per, ERPUtil.HISTORY_TYPE_COMPLETE, history);
		}
	}

	public String getCreateTime(Persistable per) {

		Timestamp create = per.getPersistInfo().getCreateStamp();
		Timestamp tt = new Timestamp(create.getTime() + (60 * 60 * 1000 * 9));

		return DateUtil.getDateString(tt, "a");
	}

	/*
	 * �ݷ� ó��
	 */
	public void cancel(ApprovalLine line) throws Exception {
		ApprovalMaster master = line.getMaster();
		master.setState(MASTER_REJECTED);
		setPersistableState(getApprovalObject(master), "RETURN");
		master = (ApprovalMaster) PersistenceHelper.manager.modify(master);

		ApprovalData data = new ApprovalData(master);
		data.completeAction(MASTER_REJECTED);

		// �ݷ� �۾��ڿ��� �˸�

		String oid = PersistenceHelper.getObjectIdentifier(data.obj).getStringValue();

		CommonActivity ca = CommonActivity.newCommonActivity();
		ca.setGubun(CENCEL_WORK);
		ca.setTitle(data.getGubun() + " ���簡 �ݷ� �Ǿ����ϴ�.-" + oid);

		ca = (CommonActivity) PersistenceHelper.manager.save(ca);

		ApprovalLine line2 = (ApprovalLine) data.line.get(0);
		WTUser puser = (WTUser) OrganizationServicesHelper.manager.getAuthenticatedUser(line2.getOwner());

		String[] activityUser = new String[1];
		activityUser[0] = puser.getPersistInfo().getObjectIdentifier().toString();
		// ApprovalHelper.manager.registApproval(ca, activityUser,null,true);

		sendRejectWorkItem(getApprovalObject(master), master, line, line);

		QueryResult notiLines = getApprovalLine(line.getMaster());
		ApprovalLine notiLine = null;
		WTUser notiUser = null;
		ReferenceFactory rf = new ReferenceFactory();

		if (notiLines != null) {
			while (notiLines.hasMoreElements()) {
				notiLine = (ApprovalLine) notiLines.nextElement();
				notiUser = CommonUtil.findUserID(notiLine.getOwner());

				// System.out.println("@@ Noti = " + notiUser.getEMail() + " , fullName = " +
				// notiUser.getFullName());

				HashMap toHash = new HashMap();
				toHash.put(notiUser.getEMail(), notiUser.getFullName());

				String creatorName = getCreatorFullName(getApprovalObject(master));

				toHash.put("createDate", getCreateTime(getApprovalObject(master)));
				toHash.put("creater", creatorName);
				toHash.put("title", data.getTitle());
				toHash.put("eMail", notiUser.getEMail());
				toHash.put("fullName", notiUser.getFullName());

				String gubun = "";
				try {
					if (data != null) {
						if (data.getGubun().length() > 0) {
							gubun = "[" + data.getGubun() + "]";
						}
					}
				} catch (Exception e) {
				}

				MailUtil.mailObjMailSendSetting(notiLine, (Object) getApprovalObject(master), toHash,
						"[PDM ���ڰ���]" + gubun + " ���簡 �ݷ��Ǿ����ϴ�.", "PDM System���� Ȯ���ϼ���.", "notify");

			}
		}

	}

	/*
	 * ȸ�� ó��
	 */
	public void withdrawal(ApprovalLine line) throws Exception {
		/**
		 * @Todo ȸ�� ������ Object���� üũ
		 */
		ApprovalMaster master = line.getMaster();

		if (checkWithdrawal(master)) {
			master.setState(MASTER_WITHDRAWAL);
			// setPersistableState( getApprovalObject(master), "WITHDRAWN");

			master = (ApprovalMaster) PersistenceHelper.manager.modify(master);

			ApprovalData data = new ApprovalData(master);
			data.completeAction(MASTER_WITHDRAWAL);

			sendRejectWorkItem(getApprovalObject(master), master, line, line);

		} else {
			WTException wte = new WTException("This Object can not withdrawal!");
			throw new WTException(wte);

		}
	}

	public QueryResult activeLine(ApprovalMaster master) throws Exception {
		QuerySpec qs = new QuerySpec(ApprovalLine.class);
		qs.appendWhere(new SearchCondition(ApprovalLine.class, "masterReference.key.id", "=",
				master.getPersistInfo().getObjectIdentifier().getId()), new int[] { 0 });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(ApprovalLine.class, "seq", "=", master.getActive()), new int[] { 0 });
		QueryResult qr = PersistenceHelper.manager.find(qs);
		// if(qr.hasMoreElements()){
		// ApprovalLine activeLine = (ApprovalLine)qr.nextElement();
		// return activeLine;
		// }
		// return null;

		if (qr == null || qr.size() == 0) {
			return null;
		}
		return qr;
	}

	public QueryResult nextLine(ApprovalLine line) throws Exception {
		QuerySpec qs = new QuerySpec(ApprovalLine.class);
		qs.appendWhere(new SearchCondition(ApprovalLine.class, "masterReference.key.id", "=",
				line.getMaster().getPersistInfo().getObjectIdentifier().getId()), new int[] { 0 });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(ApprovalLine.class, "seq", "=", line.getSeq() + 1), new int[] { 0 });
		qs.appendAnd();
		qs.appendOpenParen();
		qs.appendWhere(new SearchCondition(ApprovalLine.class, "name", "=", WORKING_REVIEWER), new int[] { 0 });
		qs.appendOr();
		qs.appendWhere(new SearchCondition(ApprovalLine.class, "name", "=", WORKING_DISCUSSER), new int[] { 0 });
		qs.appendOr();
		qs.appendWhere(new SearchCondition(ApprovalLine.class, "name", "=", WORKING_WORKING), new int[] { 0 });
		qs.appendCloseParen();
		QueryResult qr = PersistenceHelper.manager.find(qs);
		// if(qr.hasMoreElements()){
		// ApprovalLine nextLine = (ApprovalLine)qr.nextElement();
		// return nextLine;
		// }
		// return null;

		if (qr == null || qr.size() == 0) {
			return null;
		}
		return qr;
	}

	public String preLineWorkingState(ApprovalLine line) throws Exception {
		QueryResult qr = preLine(line);
		String states = "";

		ApprovalLine lines = null;
		if (qr != null)
			if (qr.hasMoreElements()) {
				lines = (ApprovalLine) qr.nextElement();
				states = lines.getName();
			}

		return states;
	}

	public QueryResult preLine(ApprovalLine line) throws Exception {
		QuerySpec qs = new QuerySpec(ApprovalLine.class);
		qs.appendWhere(new SearchCondition(ApprovalLine.class, "masterReference.key.id", "=",
				line.getMaster().getPersistInfo().getObjectIdentifier().getId()), new int[] { 0 });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(ApprovalLine.class, "seq", "=", line.getSeq() - 1), new int[] { 0 });
//        qs.appendAnd();
//        qs.appendOpenParen();
//        qs.appendWhere(new SearchCondition(ApprovalLine.class,"name","=",WORKING_REVIEWER),new int[]{0});
//        qs.appendOr();
//        qs.appendWhere(new SearchCondition(ApprovalLine.class,"name","=",WORKING_DISCUSSER),new int[]{0});
//        qs.appendOr();
//        qs.appendWhere(new SearchCondition(ApprovalLine.class,"name","=",WORKING_WORKING),new int[]{0});
//        qs.appendCloseParen();
		QueryResult qr = PersistenceHelper.manager.find(qs);
		// if(qr.hasMoreElements()){
		// ApprovalLine nextLine = (ApprovalLine)qr.nextElement();
		// return nextLine;
		// }
		// return null;

		if (qr == null || qr.size() == 0) {
			return null;
		}
		return qr;
	}

	public QueryResult getNotifier(ApprovalLine line) throws Exception {
		QuerySpec qs = new QuerySpec(ApprovalLine.class);
		qs.appendWhere(new SearchCondition(ApprovalLine.class, "masterReference.key.id", "=",
				line.getMaster().getPersistInfo().getObjectIdentifier().getId()), new int[] { 0 });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(ApprovalLine.class, "seq", "=", 100), new int[] { 0 });
		QueryResult qr = PersistenceHelper.manager.find(qs);

		if (qr == null || qr.size() == 0) {
			return null;
		}
		return qr;
	}

	public ApprovalLineTemplate getApprovalTemplate(WTUser user) throws Exception {
		QuerySpec qs = new QuerySpec(ApprovalLineTemplate.class);
		qs.appendWhere(new SearchCondition(ApprovalLineTemplate.class, "owner", "=", user.getName()), new int[] { 0 });
		QueryResult qr = PersistenceHelper.manager.find(qs);
		ApprovalLineTemplate template = null;
		if (qr.size() > 0) {
			return (ApprovalLineTemplate) qr.nextElement();
		}
		return null;
	}

	public ApprovalMaster getApprovalMaster(Persistable per) throws Exception {
		return getApprovalMaster(per.getPersistInfo().getObjectIdentifier().getId());
	}

	public ApprovalMaster getApprovalMaster(long id) throws Exception {

		QuerySpec qs = new QuerySpec();
		int ii = qs.addClassList(ApprovalObjectLink.class, false);
		int jj = qs.addClassList(ApprovalMaster.class, true);

		qs.appendWhere(new SearchCondition(ApprovalObjectLink.class, "roleAObjectRef.key.id", "=", id),
				new int[] { ii });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(ApprovalObjectLink.class, "roleBObjectRef.key.id", ApprovalMaster.class,
				"thePersistInfo.theObjectIdentifier.id"), new int[] { ii, jj });

		QueryResult qr = PersistenceHelper.manager.find(qs);
		if (qr.hasMoreElements()) {
			Object[] o = (Object[]) qr.nextElement();
			return (ApprovalMaster) o[0];
		}
		return null;
	}

	public void deleteApprovalMasterLink(Persistable per) throws Exception {
		QueryResult qr = getApprovalObjectLink(per.getPersistInfo().getObjectIdentifier().getId());
		ApprovalObjectLink link = null;
		Object[] o = null;

		if (qr != null)
			while (qr.hasMoreElements()) {
				o = (Object[]) qr.nextElement();
				link = (ApprovalObjectLink) o[0];
				PersistenceHelper.manager.delete(link);
			}

	}

	public QueryResult getApprovalObjectLink(long id) throws Exception {

		QuerySpec qs = new QuerySpec();
		int ii = qs.addClassList(ApprovalObjectLink.class, true);
		int jj = qs.addClassList(ApprovalMaster.class, false);

		qs.appendWhere(new SearchCondition(ApprovalObjectLink.class, "roleAObjectRef.key.id", "=", id),
				new int[] { ii });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(ApprovalObjectLink.class, "roleBObjectRef.key.id", ApprovalMaster.class,
				"thePersistInfo.theObjectIdentifier.id"), new int[] { ii, jj });

		QueryResult qr = PersistenceHelper.manager.find(qs);

		return qr;
	}

	public Persistable getApprovalObject(ApprovalMaster master) throws Exception {
		return getApprovalObject(master.getPersistInfo().getObjectIdentifier().getId());
	}

	public Persistable getApprovalObject(long id) throws Exception {

		QuerySpec qs = new QuerySpec();
		int ii = qs.addClassList(ApprovalObjectLink.class, true);
		int jj = qs.addClassList(ApprovalMaster.class, false);

		qs.appendWhere(new SearchCondition(ApprovalObjectLink.class, "roleBObjectRef.key.id", "=", id),
				new int[] { ii });
		// qs.appendAnd();
		// qs.appendWhere(new
		// SearchCondition(ApprovalObjectLink.class,"roleAObjectRef.key.id",Persistable.class,"thePersistInfo.theObjectIdentifier.id"),new
		// int[]{ii,jj});

		// System.out.println("@ qs = " + qs.toString());
		QueryResult qr = PersistenceHelper.manager.find(qs);
		Object[] o = null;
		if (qr.hasMoreElements()) {
			o = (Object[]) qr.nextElement();
			// System.out.println("@ object = " + o.toString());

			return (Persistable) ((ApprovalObjectLink) o[0]).getObj();
		}
		return null;
	}

	public QueryResult getApprovalLine(ApprovalMaster master) throws Exception {
		QuerySpec qs = new QuerySpec(ApprovalLine.class);
		qs.appendWhere(new SearchCondition(ApprovalLine.class, "masterReference.key.id", "=",
				master.getPersistInfo().getObjectIdentifier().getId()), new int[] { 0 });
		qs.appendOrderBy(new OrderBy(new ClassAttribute(ApprovalLine.class, "seq"), false), 0);
		return PersistenceHelper.manager.find(qs);
	}

	public void deleteAllApprovalLine(ApprovalMaster master) throws Exception {
		QueryResult qr = getApprovalLine(master);
		int seq = master.getActive();
		ApprovalLine al = null;

		while (qr.hasMoreElements()) {
			al = (ApprovalLine) qr.nextElement();

			if (seq < al.getSeq()) {
				remove(al);
			}
		}
		return;
	}

	public void deleteLatestApprovalLine(ApprovalMaster master) throws Exception {
		// System.out.println("@@ call deleteLatestApprovalLine()");
		ArrayList appLine = queryLastApprovalLine(master);
		int seq = master.getActive();
		ApprovalLine al = null;

		if (appLine != null) {
			for (int i = 0; appLine.size() > i; i++) {
				al = (ApprovalLine) appLine.get(i);

				remove(al);
			}
		}
		return;
	}

	public void deleteNotiLine(ApprovalMaster master) throws Exception {
		// System.out.println("@@ call deleteNotiLine()");
		QueryResult qr = getApprovalLine(master);
		ArrayList appLine = new ArrayList();
		ApprovalLine line = null;

		if (qr != null) {
			while (qr.hasMoreElements()) {
				line = (ApprovalLine) qr.nextElement();
				// System.out.println("@@ line.getState() = " + line.getState() + " , seq = " +
				// line.getSeq());
				if (line.getStepName().equals(APPROVE_NOTIFICATE)) {
					remove(line);
				}
			}
		}
	}

	public void changeStandingLine(ApprovalMaster master) throws Exception {
		Transaction trx = new Transaction();
		try {
			trx.start();

			// System.out.println("@@ call changeStandingLine()");
			QueryResult qr = getApprovalLine(master);
			ArrayList appLine = new ArrayList();
			ApprovalLine line = null;

			if (qr != null) {
				while (qr.hasMoreElements()) {
					line = (ApprovalLine) qr.nextElement();
					// System.out.println("@@ line.getState() = " + line.getState() + " , seq = " +
					// line.getSeq());
					if (line.getState().equals(LINE_STANDING)) {
						line.setState(LINE_STANDING_CANCEL);
						PersistenceHelper.manager.save(line);

						sendDeleteWorkItem(getApprovalObject(master), master, line, line);
					}
				}
			}

			trx.commit();
			trx = null;

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new WTException(ex);
		} finally {
			if (trx != null)
				trx.rollback();
		}
	}

	public ArrayList queryLastApprovalLine(ApprovalMaster master) throws Exception {
		// System.out.println("@@ call queryLastApprovalLine()");
		QueryResult qr = getApprovalLine(master);
		ArrayList appLine = new ArrayList();
		ApprovalLine line = null;

		if (qr != null) {
			while (qr.hasMoreElements()) {
				line = (ApprovalLine) qr.nextElement();
				// System.out.println("@@ line.getState() = " + line.getState() + " , seq = " +
				// line.getSeq());

				if (line.getStepName().equals(APPROVE_REQUEST)) {
					appLine = new ArrayList();
				}
				appLine.add(line);
			}
		}

		return appLine;
	}

	public ApprovalLineTemplate getLatestApprovalLine(Persistable obj) throws Exception {
		// System.out.println("@@ call getLatestApprovalLine()");
		ApprovalLineTemplate at = ApprovalLineTemplate.newApprovalLineTemplate();
		ArrayList appLine = queryLastApprovalLine(getApprovalMaster(obj));
		ApprovalLine al = null;
		String uid = "";
		String stepName = "";
		ArrayList preAppList = new ArrayList();
		ArrayList discussList = new ArrayList();
		ArrayList postAppList = new ArrayList();
		ArrayList notiList = new ArrayList();

		if (appLine != null)
			for (int i = 0; appLine.size() > i; i++) {
				al = (ApprovalLine) appLine.get(i);
				stepName = al.getStepName();
				uid = al.getOwner();
				// System.out.println("@@ uid = " + uid + " , stepName = " + stepName);
				if (stepName.equals(APPROVE_PREAPPROVE)) {
					preAppList.add(uid);
				} else if (stepName.equals(APPROVE_DISCUSS)) {
					discussList.add(uid);
				} else if (stepName.equals(APPROVE_POSTAPPROVE)) {
					postAppList.add(uid);
				} else if (stepName.equals(APPROVE_NOTIFICATE)) {
					notiList.add(uid);
				}
			}

		at.setPreDiscussList(preAppList);
		at.setDiscussList(discussList);
		at.setPostDiscussList(postAppList);
		at.setTempList(notiList);
		at.setOrderType(al.getMaster().getOrderType());

		return at;
	}

	public boolean checkRejectLine(ApprovalMaster master) throws Exception {
		QueryResult qr = getApprovalLine(master);
		ApprovalLine line = null;

		if (qr != null) {
			while (qr.hasMoreElements()) {
				line = (ApprovalLine) qr.nextElement();

				if (line.getState().equals(LINE_REJECTED)) {
					return true;
				}
			}
		}

		return false;
	}

	public int maxSeq(ApprovalMaster master) throws Exception {
		QueryResult qr = getApprovalLine(master);
		ApprovalLine line = null;
		int max = master.getActive();

		if (qr != null) {
			while (qr.hasMoreElements()) {
				line = (ApprovalLine) qr.nextElement();

				if ((line.getSeq() >= max) && (line.getSeq() < 100)) {
					max = line.getSeq();
				}
			}
		}

		return max;
	}

	/**
	 * ȸ�� ó�� ������ Object���� üũ return : false�̸� ���簡 ����Ǿ� ȸ�� �Ұ��� true�̸�
	 * ȸ�� ����
	 */
	public boolean checkWithdrawal(ApprovalMaster master) throws Exception {
		// latest ���缱�� �����Ͽ� ����/���� ����Ʈ�� ����Ȱ��� �ִ��� üũ
		// ApprovalMaster ap = getApprovalMaster(obj);
		// System.out.println("@@ call checkWithdrawal!!");
		ArrayList appLine = queryLastApprovalLine(master);
		ApprovalLine al = null;
		String name = null;

		if (appLine != null) {
			// ����ڿ� ���� ����ڰ� ������ üũ
			// ���� ������ false
			al = (ApprovalLine) appLine.get(0);
			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
			if (!al.getOwner().equals(user.getName())) {
				return false;
			}
			// System.out.println("@@ appLine = " + appLine.size());
			// System.out.println("@@ ApprovalLine = " + al.toString() + " , name = " +
			// name);

			for (int i = 1; appLine.size() > i; i++) {
				al = (ApprovalLine) appLine.get(i);
				name = al.getName();
				// System.out.println("@@ ApprovalLine = " + al.toString() + " , name = " +
				// name);

				if (name.equals(WORKING_REVIEWER) || name.equals(WORKING_WORKING)) {
					if (al.getApproveDate() != null)
						return false;
				}
			}
		}

		return true;
	}

	/**
	 * ���缱 ����
	 */
	public void saveApprovalLine(String[] preDiscussUser, String[] discussUser, String[] postDiscussUser,
			String[] tempUser, String discussType, String title) throws Exception {

		if (!SERVER) {
			Class argTypes[] = new Class[] { String[].class, String[].class, String[].class, String[].class,
					String.class, String.class };
			Object args[] = new Object[] { preDiscussUser, discussUser, postDiscussUser, tempUser, discussType, title };
			try {
				RemoteMethodServer.getDefault().invoke("saveApprovalLine", null, this, argTypes, args);
				return;
			} catch (RemoteException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new WTException(e);
			}
		}

		Transaction trx = new Transaction();
		try {
			trx.start();

			ApprovalLineTemplate2 line = ApprovalLineTemplate2.newApprovalLineTemplate2();

			ArrayList preDuscussUsers = new ArrayList();
			ArrayList discussUsers = new ArrayList();
			ArrayList postDiscussUsers = new ArrayList();
			ArrayList tempUsers = new ArrayList();

			line.setOrderType(discussType);
			for (int i = 0; preDiscussUser != null && i < preDiscussUser.length; i++) {
				preDuscussUsers.add(preDiscussUser[i]);
			}
			line.setTempList(tempUsers);
			for (int i = 0; discussUser != null && i < discussUser.length; i++) {
				discussUsers.add(discussUser[i]);
			}
			line.setTempList(tempUsers);
			for (int i = 0; postDiscussUser != null && i < postDiscussUser.length; i++) {
				postDiscussUsers.add(postDiscussUser[i]);
			}
			line.setTempList(tempUsers);
			for (int i = 0; tempUser != null && i < tempUser.length; i++) {
				tempUsers.add(tempUser[i]);
			}
			line.setTempList(tempUsers);

			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
			line.setOwner(user.getName());
			line.setTitle(title);

			line = (ApprovalLineTemplate2) PersistenceHelper.manager.save(line);

			trx.commit();
			trx = null;

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new WTException(ex);
		} finally {
			if (trx != null)
				trx.rollback();
		}
	}

	/**
	 * ���缱 ����
	 */
	public void deleteApprovalLine(String appLineOid) throws Exception {
		Transaction trx = new Transaction();
		try {
			trx.start();

			if (appLineOid != null && !appLineOid.equals("")) {
				ApprovalLineTemplate2 line = (ApprovalLineTemplate2) WCUtil.getPersistable(appLineOid);

				PersistenceHelper.manager.delete(line);
			}

			trx.commit();
			trx = null;

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new WTException(ex);
		} finally {
			if (trx != null)
				trx.rollback();
		}
	}

	/**
	 * ���缱 ����Ʈ ��ȸ �ش� ������� ���缱 ���� ��ȸ
	 */
	public QueryResult searchApprovalLine(WTUser user) throws Exception {
		QuerySpec qs = new QuerySpec(ApprovalLineTemplate2.class);
		qs.appendWhere(new SearchCondition(ApprovalLineTemplate2.class, "owner", "=", user.getName()), new int[] { 0 });
		QueryResult qr = PersistenceHelper.manager.find(qs);

		return qr;
	}

	/**
	 * ���缱 ���� ��ȸ ���õ� ���缱 �ϳ��� ���� ���� ��ȸ
	 */
	public ApprovalLineTemplate2 getApprovalLine(String appLineOid) throws Exception {
		if (appLineOid != null && !appLineOid.equals("")) {
			ApprovalLineTemplate2 line = (ApprovalLineTemplate2) WCUtil.getPersistable(appLineOid);
			return line;
		}
		return null;
	}

	public String getUserNo(String userId) throws Exception {
		String userNo = "";
		try {
			WTUser wtuser = CommonUtil.findUserID(userId);
			People user = UserHelper.service.getPeople(wtuser);

			userNo = user.getAddress();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return userNo;
	}

//    public void sendModifyWorkItem(Persistable per, ApprovalMaster master, ApprovalLine currentLine, ApprovalLine nextLine) throws Exception {System.out.println("1793");
//        if( nextLine.getName().equals(WORKING_REVIEWER) || nextLine.getName().equals(WORKING_DISCUSSER)) {
//            try {System.out.println("1795");
//                sendDeleteWorkItem(per, master, currentLine, nextLine);
//            } catch( Exception e) {
//                e.printStackTrace();
//            }
//
//            try {System.out.println("1801");
//                sendCreateWorkItem(per, master, currentLine, nextLine);
//            } catch(Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public void sendCommitWorkItem(Persistable per, ApprovalMaster master, ApprovalLine currentLine, ApprovalLine nextLine) throws Exception {System.out.println("1809");
//        if( nextLine.getName().equals(WORKING_REVIEWER) || nextLine.getName().equals(WORKING_DISCUSSER)) {
////            try {System.out.println("1811");
////                sendDeleteWorkItem(per, master, nextLine);
////            } catch(Exception e) {
////                e.printStackTrace();
////            }
//            try {System.out.println("1816");
//                sendFinishWorkItem(per, master, currentLine, nextLine);
//            } catch(Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public void sendCancelWorkItem(Persistable per, ApprovalMaster master, ApprovalLine currentLine, ApprovalLine nextLine) throws Exception { System.out.println("1824");
//        if( nextLine.getName().equals(WORKING_REVIEWER) || nextLine.getName().equals(WORKING_DISCUSSER)) {
//            try { System.out.println("1826");
//                sendDeleteWorkItem(per, master, currentLine, nextLine);
//            } catch(Exception e) {
//                e.printStackTrace();
//            }
//            try {System.out.println("1831");
//                sendRejectWorkItem(per, master, currentLine, nextLine);
//            } catch(Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

	public void sendCreateWorkItem(Persistable per, ApprovalMaster master, ApprovalLine currentLine,
			ApprovalLine nextLine) throws Exception {
		if (nextLine.getName().equals(WORKING_REVIEWER) || nextLine.getName().equals(WORKING_DISCUSSER)) {
			ApprovalData data = new ApprovalData(per);

			String rtn = "";
			String AppId = "PLM_" + master.getPersistInfo().getObjectIdentifier().getId();
			String MainStatus = "R";
			String SubStatus = nextLine.getState();

			String hostName = WTProperties.getLocalProperties().getProperty("wt.httpgw.hostname");
			WTProperties.getLocalProperties().getProperty("wt.httpgw.hostname");
			String host = "http://" + hostName;
			String Url = host + "/plm/lgchem/workspace/approval/ViewWork.jsp?isGW=Y&oid="
					+ nextLine.getPersistInfo().getObjectIdentifier().toString();

			String REmpNo = StringUtil.checkNull(getUserNo(currentLine.getOwner()));
			String AEmpNo = StringUtil.checkNull(getUserNo(nextLine.getOwner()));
			String Category = StringUtil.checkNull(data.getGubun());
			String Subject = StringUtil.checkNull(data.getTitle());

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
			String ReqDate = "";
			if (nextLine.getStartTaskDate() != null) {
				ReqDate = sdf.format(nextLine.getStartTaskDate()); // ����Ͻú�
			} else {
				ReqDate = sdf.format(new Timestamp(new java.util.Date().getTime())); // ����Ͻú�
			}

			String NUrl = "";
			String AuthDiv = "";
			String AuthEmp = "";
			String Modify = "";
			String FAgree = "";
			String ExpireDate = "";

			rtn = createWorkItem(AppId, MainStatus, SubStatus, Url, REmpNo, AEmpNo, Category, Subject, ReqDate, NUrl,
					AuthDiv, AuthEmp, Modify, FAgree, ExpireDate);
		}
	}

	public void sendModifyDiscussWorkItem(Persistable per, ApprovalMaster master, ApprovalLine currentLine,
			ApprovalLine nextLine, String discussList) throws Exception {
		if (nextLine.getName().equals(WORKING_REVIEWER) || nextLine.getName().equals(WORKING_DISCUSSER)) {
			ApprovalData data = new ApprovalData(per);

			String rtn = "";
			String AppId = "PLM_" + master.getPersistInfo().getObjectIdentifier().getId();
			String MainStatus = "M";
			String SubStatus = nextLine.getState();

			String hostName = WTProperties.getLocalProperties().getProperty("wt.httpgw.hostname");
			WTProperties.getLocalProperties().getProperty("wt.httpgw.hostname");
			String host = "http://" + hostName;
			String Url = host + "/plm/lgchem/workspace/approval/ViewWork.jsp?isGW=Y&oid="
					+ nextLine.getPersistInfo().getObjectIdentifier().toString() + "&discussList=" + discussList;

			String REmpNo = StringUtil.checkNull(getUserNo(currentLine.getOwner()));
			String AEmpNo = discussList;
			String Category = StringUtil.checkNull(data.getGubun());
			String Subject = StringUtil.checkNull(data.getTitle());

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
			String ReqDate = "";
			if (nextLine.getStartTaskDate() != null) {
				ReqDate = sdf.format(nextLine.getStartTaskDate()); // ����Ͻú�
			} else {
				ReqDate = sdf.format(new Timestamp(new java.util.Date().getTime())); // ����Ͻú�
			}

			String NUrl = "";
			String AuthDiv = "";
			String AuthEmp = "";
			String Modify = "";
			String FAgree = "";
			String ExpireDate = "";

			rtn = createWorkItem(AppId, MainStatus, SubStatus, Url, REmpNo, AEmpNo, Category, Subject, ReqDate, NUrl,
					AuthDiv, AuthEmp, Modify, FAgree, ExpireDate);
		}
	}

	public void sendCreateDiscussWorkItem(Persistable per, ApprovalMaster master, ApprovalLine currentLine,
			ApprovalLine nextLine, String discussList) throws Exception {
		if (nextLine.getName().equals(WORKING_REVIEWER) || nextLine.getName().equals(WORKING_DISCUSSER)) {
			ApprovalData data = new ApprovalData(per);

			String rtn = "";
			String AppId = "PLM_" + master.getPersistInfo().getObjectIdentifier().getId();
			String MainStatus = "R";
			String SubStatus = nextLine.getState();

			String hostName = WTProperties.getLocalProperties().getProperty("wt.httpgw.hostname");
			WTProperties.getLocalProperties().getProperty("wt.httpgw.hostname");
			String host = "http://" + hostName;
			String Url = host + "/plm/lgchem/workspace/approval/ViewWork.jsp?isGW=Y&oid="
					+ nextLine.getPersistInfo().getObjectIdentifier().toString() + "&discussList=" + discussList;

			String REmpNo = StringUtil.checkNull(getUserNo(currentLine.getOwner()));
			String AEmpNo = discussList;
			String Category = StringUtil.checkNull(data.getGubun());
			String Subject = StringUtil.checkNull(data.getTitle());

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
			String ReqDate = "";
			if (nextLine.getStartTaskDate() != null) {
				ReqDate = sdf.format(nextLine.getStartTaskDate()); // ����Ͻú�
			} else {
				ReqDate = sdf.format(new Timestamp(new java.util.Date().getTime())); // ����Ͻú�
			}

			String NUrl = "";
			String AuthDiv = "";
			String AuthEmp = "";
			String Modify = "";
			String FAgree = "";
			String ExpireDate = "";

			rtn = createWorkItem(AppId, MainStatus, SubStatus, Url, REmpNo, AEmpNo, Category, Subject, ReqDate, NUrl,
					AuthDiv, AuthEmp, Modify, FAgree, ExpireDate);
		}
	}

	public void sendModifyWorkItem(Persistable per, ApprovalMaster master, ApprovalLine currentLine,
			ApprovalLine nextLine) throws Exception {
		if (nextLine.getName().equals(WORKING_REVIEWER) || nextLine.getName().equals(WORKING_DISCUSSER)) {
			ApprovalData data = new ApprovalData(per);

			String rtn = "";
			String AppId = "PLM_" + master.getPersistInfo().getObjectIdentifier().getId();
			String MainStatus = "M";
			String SubStatus = nextLine.getState();

			String hostName = WTProperties.getLocalProperties().getProperty("wt.httpgw.hostname");
			WTProperties.getLocalProperties().getProperty("wt.httpgw.hostname");
			String host = "http://" + hostName;
			String Url = host + "/plm/lgchem/workspace/approval/ViewWork.jsp?isGW=Y&oid="
					+ nextLine.getPersistInfo().getObjectIdentifier().toString();

			String REmpNo = StringUtil.checkNull(getUserNo(currentLine.getOwner()));
			String AEmpNo = StringUtil.checkNull(getUserNo(nextLine.getOwner()));
			String Category = StringUtil.checkNull(data.getGubun());
			String Subject = StringUtil.checkNull(data.getTitle());

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
			String ReqDate = "";
			if (nextLine.getStartTaskDate() != null) {
				ReqDate = sdf.format(nextLine.getStartTaskDate()); // ����Ͻú�
			} else {
				ReqDate = sdf.format(new Timestamp(new java.util.Date().getTime())); // ����Ͻú�
			}

			String NUrl = "";
			String AuthDiv = "";
			String AuthEmp = "";
			String Modify = "";
			String FAgree = "";
			String ExpireDate = "";

			rtn = createWorkItem(AppId, MainStatus, SubStatus, Url, REmpNo, AEmpNo, Category, Subject, ReqDate, NUrl,
					AuthDiv, AuthEmp, Modify, FAgree, ExpireDate);
		}
	}

	public void sendFinishWorkItem(Persistable per, ApprovalMaster master, ApprovalLine currentLine,
			ApprovalLine nextLine) throws Exception {
		if (nextLine.getName().equals(WORKING_REVIEWER) || nextLine.getName().equals(WORKING_DISCUSSER)) {
			ApprovalData data = new ApprovalData(per);

			String rtn = "";
			String AppId = "PLM_" + master.getPersistInfo().getObjectIdentifier().getId();
			String MainStatus = "F";
			String SubStatus = nextLine.getState();

			String hostName = WTProperties.getLocalProperties().getProperty("wt.httpgw.hostname");
			WTProperties.getLocalProperties().getProperty("wt.httpgw.hostname");
			String host = "http://" + hostName;
			String Url = host + "/plm/lgchem/workspace/approval/ViewWork.jsp?isGW=Y&oid="
					+ nextLine.getPersistInfo().getObjectIdentifier().toString();

			String REmpNo = StringUtil.checkNull(getUserNo(currentLine.getOwner()));
			String AEmpNo = StringUtil.checkNull(getUserNo(nextLine.getOwner()));
			String Category = StringUtil.checkNull(data.getGubun());
			String Subject = StringUtil.checkNull(data.getTitle());

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
			String ReqDate = "";
			if (nextLine.getStartTaskDate() != null) {
				ReqDate = sdf.format(nextLine.getStartTaskDate()); // ����Ͻú�
			} else {
				ReqDate = sdf.format(new Timestamp(new java.util.Date().getTime())); // ����Ͻú�
			}

			String NUrl = "";
			String AuthDiv = "";
			String AuthEmp = "";
			String Modify = "";
			String FAgree = "";
			String ExpireDate = "";

			rtn = createWorkItem(AppId, MainStatus, SubStatus, Url, REmpNo, AEmpNo, Category, Subject, ReqDate, NUrl,
					AuthDiv, AuthEmp, Modify, FAgree, ExpireDate);
		}
	}

	public void sendRejectWorkItem(Persistable per, ApprovalMaster master, ApprovalLine currentLine,
			ApprovalLine nextLine) throws Exception {
		if (nextLine.getName().equals(WORKING_REVIEWER) || nextLine.getName().equals(WORKING_DISCUSSER)) {
			ApprovalData data = new ApprovalData(per);

			String rtn = "";
			String AppId = "PLM_" + master.getPersistInfo().getObjectIdentifier().getId();
			String MainStatus = "C";
			String SubStatus = nextLine.getState();

			String hostName = WTProperties.getLocalProperties().getProperty("wt.httpgw.hostname");
			WTProperties.getLocalProperties().getProperty("wt.httpgw.hostname");
			String host = "http://" + hostName;
			String Url = host + "/plm/jsp/workspace/approval/ViewWork.jsp?isGW=Y&oid="
					+ nextLine.getPersistInfo().getObjectIdentifier().toString();

			String REmpNo = StringUtil.checkNull(getUserNo(currentLine.getOwner()));
			String AEmpNo = StringUtil.checkNull(getUserNo(nextLine.getOwner()));
			String Category = StringUtil.checkNull(data.getGubun());
			String Subject = StringUtil.checkNull(data.getTitle());

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
			String ReqDate = "";
			if (nextLine.getStartTaskDate() != null) {
				ReqDate = sdf.format(nextLine.getStartTaskDate()); // ����Ͻú�
			} else {
				ReqDate = sdf.format(new Timestamp(new java.util.Date().getTime())); // ����Ͻú�
			}

			String NUrl = "";
			String AuthDiv = "";
			String AuthEmp = "";
			String Modify = "";
			String FAgree = "";
			String ExpireDate = "";

			rtn = createWorkItem(AppId, MainStatus, SubStatus, Url, REmpNo, AEmpNo, Category, Subject, ReqDate, NUrl,
					AuthDiv, AuthEmp, Modify, FAgree, ExpireDate);
		}
	}

	public void sendDeleteWorkItem(Persistable per, ApprovalMaster master, ApprovalLine currentLine,
			ApprovalLine nextLine) throws Exception {
		if (currentLine.getName().equals(WORKING_REVIEWER) || currentLine.getName().equals(WORKING_DISCUSSER)) {
			// ApprovalData data = new ApprovalData(per);

			String rtn = "";
			String AppId = "PLM_" + master.getPersistInfo().getObjectIdentifier().getId();
			String MainStatus = "R";
			String SubStatus = nextLine.getState();

			String host = "http://";
			String Url = host;

			String REmpNo = StringUtil.checkNull(getUserNo(master.getOwner()));
			String AEmpNo = StringUtil.checkNull(getUserNo(nextLine.getOwner()));
			String Category = "";
			String Subject = "";

			String ReqDate = ""; // ����Ͻú�

			String NUrl = "";
			String AuthDiv = "";
			String AuthEmp = "";
			String Modify = "D";
			String FAgree = "";
			String ExpireDate = "";

			rtn = createWorkItem(AppId, MainStatus, SubStatus, Url, REmpNo, AEmpNo, Category, Subject, ReqDate, NUrl,
					AuthDiv, AuthEmp, Modify, FAgree, ExpireDate);
		}
	}

	/**
	 * http://yoonng.ptc.com/plm/lgchem/workspace/approval/ViewWork.jsp?oid=com.e3ps.approval.ApprovalLine:70053
	 *
	 * @param appParam
	 * @throws Exception
	 */

	public String createWorkItem(String AppId, String MainStatus, String SubStatus, String Url, String REmpNo,
			String AEmpNo, String Category, String Subject, String ReqDate, String NUrl, String AuthDiv, String AuthEmp,
			String Modify, String FAgree, String ExpireDate) throws Exception {

		String rtn = "";

		/*
		 * 
		 * //String cmd = request.getParameter("cmd"); //String menu =
		 * request.getParameter("menu"); //String module =
		 * request.getParameter("module"); //String subMenu =
		 * request.getParameter("subMenu");
		 * 
		 * String cmd = "send";
		 * 
		 * // String AppId = request.getParameter("APP_ID"); // String MainStatus =
		 * request.getParameter("MAIN_STATUS"); // String SubStatus =
		 * request.getParameter("SUB_STATUS"); // String Url =
		 * request.getParameter("URL"); // String REmpNo =
		 * request.getParameter("R_EMP_NO"); // String AEmpNo =
		 * request.getParameter("A_EMP_NO"); // // String Category =
		 * request.getParameter("CATEGORY"); // String Subject =
		 * request.getParameter("SUBJECT"); // String ReqDate =
		 * request.getParameter("REQ_DATE"); // String NUrl =
		 * request.getParameter("N_URL"); // String AuthDiv =
		 * request.getParameter("AUTH_DIV"); // String AuthEmp =
		 * request.getParameter("AUTH_EMP"); // String Modify =
		 * request.getParameter("MODIFY"); // String FAgree =
		 * request.getParameter("F_AGREE"); // String ExpireDate =
		 * request.getParameter("EXPIRE_DATE");
		 * 
		 * if(cmd != null && "send".equals(cmd)) { try { ESBAdapter esbAp = new
		 * LGChemESBService("APPINT_ESB" , "prod");
		 * 
		 * Hashtable appParam = new Hashtable(); appParam.put("APP_ID" ,AppId);
		 * appParam.put("MAIN_STATUS" ,MainStatus); appParam.put("SUB_STATUS"
		 * ,SubStatus); appParam.put("URL" ,Url); appParam.put("R_EMP_NO" ,REmpNo);
		 * 
		 * if(!MainStatus.equals("F") && !MainStatus.equals("C")) {
		 * appParam.put("A_EMP_NO" ,AEmpNo); }
		 * 
		 * if (Category != null && Category.length() > 0) appParam.put("CATEGORY"
		 * ,Category); if (Subject != null && Subject.length() > 0)
		 * appParam.put("SUBJECT" ,Subject); if (ReqDate != null && ReqDate.length() >
		 * 0) appParam.put("REQ_DATE" ,ReqDate); if (ExpireDate != null &&
		 * ExpireDate.length() > 0) appParam.put("EXPIRE_DATE" ,ExpireDate); if (NUrl !=
		 * null && NUrl.length() > 0) appParam.put("N_URL" ,NUrl); if (AuthDiv != null
		 * && AuthDiv.length() > 0) appParam.put("AUTH_DIV" ,AuthDiv); if (AuthEmp !=
		 * null && AuthEmp.length() > 0) appParam.put("AUTH_EMP" ,AuthEmp); if (Modify
		 * != null && Modify.length() > 0) appParam.put("MODIFY" ,Modify); if (FAgree !=
		 * null && FAgree.length() > 0) appParam.put("F_AGREE" ,FAgree);
		 * 
		 * 
		 * java.util.Properties props = new java.util.Properties(); ConfigExImpl conf =
		 * ConfigEx.getInstance("eSolution"); boolean enableApproval =
		 * conf.getBoolean("e3ps.gwapproval.enable", true);
		 * 
		 * if( enableApproval) { if(MainStatus != null && MainStatus.length() > 0) {
		 * if(Modify != null && (Modify.equals("D") || Modify.equals("I"))) { rtn =
		 * esbAp.modifyESB(appParam); } else { rtn = esbAp.callESB(appParam); } } } else
		 * { rtn = "MAIN_STATUS is NULL"; }
		 * 
		 * System.out.println("====================================================");
		 * System.out.println("============+++++  GW ���� ��� ����  +++++============="
		 * ); System.out.println("@    AppId = " + AppId);
		 * System.out.println("@    MainStatus = " + MainStatus);
		 * System.out.println("@    SubStatus = " + SubStatus);
		 * System.out.println("@    Url = " + Url); System.out.println("@    REmpNo = "
		 * + REmpNo);
		 * 
		 * System.out.println("@    AEmpNo = " + AEmpNo);
		 * System.out.println("@    Category = " + Category);
		 * System.out.println("@    Subject = " + Subject);
		 * System.out.println("@    ReqDate = " + ReqDate);
		 * System.out.println("@    NUrl = " + NUrl);
		 * 
		 * System.out.println("@    AuthDiv = " + AuthDiv);
		 * System.out.println("@    AuthEmp = " + AuthEmp);
		 * System.out.println("@    Modify = " + Modify);
		 * System.out.println("@    FAgree = " + FAgree);
		 * System.out.println("@    ExpireDate = " + ExpireDate);
		 * System.out.println("====================================================");
		 * 
		 * 
		 * } catch (ESBValidationException eV) { rtn = "ESBValidationException : " +
		 * eV.getMessage(); eV.printStackTrace();
		 * 
		 * } catch (ESBTransferException eT) { rtn = "ESBTransferException : " +
		 * eT.getMessage(); eT.printStackTrace();
		 * 
		 * } catch (Exception e) { rtn = "Exception : " + e.getMessage();
		 * e.printStackTrace(); } }
		 */
		return rtn;
	}

	/**
	 * ���簡 �������̰�, 3�� �̻� ���� �ǰ� �ִ� ���� Task�� ����ڿ��� ������ ���� ������
	 * �����Ѵ�
	 */
	public void sendPressingTaskMail() throws Exception {
		if (!SERVER) {
			Class argTypes[] = new Class[] {};
			Object args[] = new Object[] {};
			try {
				RemoteMethodServer.getDefault().invoke("sendPressingTaskMail", null, this, argTypes, args);
				return;
			} catch (RemoteException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new WTException(e);
			}
		}

		Timestamp justTime = new Timestamp(new java.util.Date().getTime());
		QuerySpec qs = new QuerySpec();
		int ii = qs.addClassList(ApprovalLine.class, true);
		int jj = qs.addClassList(ApprovalMaster.class, true);

		qs.appendWhere(new SearchCondition(ApprovalLine.class, "masterReference.key.id", ApprovalMaster.class,
				"thePersistInfo.theObjectIdentifier.id"), new int[] { ii, jj });

		qs.appendAnd();
		qs.appendWhere(new SearchCondition(ApprovalMaster.class, "state", "=", ApprovalHelper.MASTER_APPROVING),
				new int[] { jj });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(ApprovalLine.class, "seq", ApprovalMaster.class, "active"),
				new int[] { ii, jj });
		qs.appendAnd();
		// qs.appendOpenParen();
		qs.appendWhere(new SearchCondition(ApprovalLine.class, "state", "=", ApprovalHelper.LINE_APPROVING),
				new int[] { ii });
		// qs.appendOr();
		// qs.appendWhere(new
		// SearchCondition(ApprovalLine.class,"startTaskDate",SearchCondition.LESS_THAN_OR_EQUAL,
		// ApprovalHelper.LINE_WORKING),new int[]{ii});
		// qs.appendCloseParen();

		QueryResult qr = PersistenceHelper.manager.find(qs);
		Object[] obj = null;
		ApprovalLine line = null;
		ApprovalMaster master = null;
		ApprovalData data = null;
		WTUser notiUser = null;
		ReferenceFactory rf = new ReferenceFactory();
		String email = "";
		Timestamp lineTime = null;
		int durationDay = 0;
		Persistable per = null;
		String gubun = "";

		while (qr.hasMoreElements()) {
			lineTime = null;
			obj = (Object[]) qr.nextElement();
			line = (ApprovalLine) obj[0];
			master = (ApprovalMaster) obj[1];
			per = getApprovalObject(master);
			data = new ApprovalData(master);
			notiUser = CommonUtil.findUserID(line.getOwner());
			email = notiUser.getEMail();

			try {
				if (data != null) {
					if (data.getGubun().length() > 0) {
						gubun = "[" + data.getGubun() + "]";
					}
				}
			} catch (Exception e) {
			}

			// startTaskDate�� ���� �ð����� 3�� �̻� ����
			try {
				lineTime = line.getStartTaskDate();
			} catch (NullPointerException npe) {
				npe.printStackTrace();
			}

			if (lineTime != null) {
				durationDay = DateUtil.getDuration(lineTime, justTime);

				// System.out.println("@@ sendPressingTaskMail User = " + line.getOwner() + " ,
				// " + email);
				// System.out.println("@@ curr Time =" + justTime + " , lineTime =" + lineTime +
				// " , duration =" + durationDay);

				// ���� ������ �����Ѵ�.
				if (durationDay > 3) {
					// System.out.println("@@ sendPressingTaskMail Obj =" + line.toString() + " ,
					// User = " + line.getOwner() + " , " + email);
					// System.out.println("@@ Noti = " + notiUser.getEMail() + " , fullName = " +
					// notiUser.getFullName());

					String creatorName = getCreatorFullName(per);

					HashMap toHash = new HashMap();
					toHash.put(notiUser.getEMail(), notiUser.getFullName());
					toHash.put("createDate", getCreateTime(per));
					toHash.put("creater", creatorName);
					toHash.put("title", data.getTitle());
					toHash.put("eMail", notiUser.getEMail());
					toHash.put("fullName", notiUser.getFullName());
					// toHash.put("TOName", notiUser.getFullName());

//                    MailUtilmailObjMailSendSetting(line, (Object)per, toHash,
//                            "[PLM ���ڰ���]"+gubun+" ���簡 �����ǰ� �ֽ��ϴ�.", "���簡 �����ǰ� �ֽ��ϴ�.", "pressingApproval");
				}
			}
		}
	}

	public void createApprovalTemplate(String title, String[] preDiscussUser, String[] discussUser,
			String[] postDiscussUser, String[] tempUser) throws Exception {

		if (!SERVER) {
			Class argTypes[] = new Class[] { String.class, String[].class, String[].class, String[].class,
					String[].class };
			Object args[] = new Object[] { title, preDiscussUser, discussUser, postDiscussUser, tempUser };
			try {
				RemoteMethodServer.getDefault().invoke("createApprovalTemplate", null, this, argTypes, args);
				return;
			} catch (RemoteException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new WTException(e);
			}
		}

		Transaction trx = new Transaction();
		try {
			trx.start();

			ReferenceFactory rf = new ReferenceFactory();

			ApprovalLineTemplate2 template = ApprovalLineTemplate2.newApprovalLineTemplate2();
			template.setTitle(title);

			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
			template.setOwner(user.getName());

			ArrayList approveList = new ArrayList();

			for (int i = 0; preDiscussUser != null && i < preDiscussUser.length; i++) {
				approveList.add(preDiscussUser[i]);
			}
			template.setPreDiscussList(approveList);
			approveList = new ArrayList();

			for (int i = 0; discussUser != null && i < discussUser.length; i++) {
				approveList.add(discussUser[i]);
			}
			template.setDiscussList(approveList);
			approveList = new ArrayList();

			for (int i = 0; postDiscussUser != null && i < postDiscussUser.length; i++) {
				approveList.add(postDiscussUser[i]);
			}
			template.setPostDiscussList(approveList);
			approveList = new ArrayList();

			for (int i = 0; tempUser != null && i < tempUser.length; i++) {
				approveList.add(tempUser[i]);
			}
			template.setTempList(approveList);
			PersistenceHelper.manager.save(template);

			trx.commit();
			trx = null;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new WTException(ex);
		} finally {
			if (trx != null)
				trx.rollback();
		}
	}

	public void deleteApprovalTemplate(String oid) throws Exception {

		if (!SERVER) {
			Class argTypes[] = new Class[] { String.class };
			Object args[] = new Object[] { oid };
			try {
				RemoteMethodServer.getDefault().invoke("deleteApprovalTemplate", null, this, argTypes, args);
				return;
			} catch (RemoteException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new WTException(e);
			}
		}

		Transaction trx = new Transaction();
		try {
			trx.start();

			ReferenceFactory rf = new ReferenceFactory();
			ApprovalLineTemplate2 template = (ApprovalLineTemplate2) WCUtil.getObject(oid);
			PersistenceHelper.manager.delete(template);

			trx.commit();
			trx = null;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new WTException(ex);
		} finally {
			if (trx != null)
				trx.rollback();
		}
	}

	public String getGWPW(String userId) throws Exception {
		/*
		 * HttpParams params = new BasicHttpParams();
		 * HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		 * HttpProtocolParams.setContentCharset(params, "UTF-8");
		 * HttpProtocolParams.setUserAgent(params, "HttpComponents/1.1");
		 * HttpProtocolParams.setUseExpectContinue(params, true);
		 * 
		 * BasicHttpProcessor httpproc = new BasicHttpProcessor(); // Required protocol
		 * interceptors httpproc.addInterceptor(new RequestContent());
		 * httpproc.addInterceptor(new RequestTargetHost()); // Recommended protocol
		 * interceptors httpproc.addInterceptor(new RequestConnControl());
		 * httpproc.addInterceptor(new RequestUserAgent()); httpproc.addInterceptor(new
		 * RequestExpectContinue());
		 * 
		 * HttpRequestExecutor httpexecutor = new HttpRequestExecutor();
		 * 
		 * HttpContext context = new BasicHttpContext(null); HttpHost host = new
		 * HttpHost("uapproval.lgchem.com", 7010);
		 * 
		 * DefaultHttpClientConnection conn = new DefaultHttpClientConnection();
		 * ConnectionReuseStrategy connStrategy = new DefaultConnectionReuseStrategy();
		 * 
		 * context.setAttribute(ExecutionContext.HTTP_CONNECTION, conn);
		 * context.setAttribute(ExecutionContext.HTTP_TARGET_HOST, host); String gwpw =
		 * "";
		 * 
		 * try {
		 * 
		 * String targets = GROUPWARE_GET_PASSWORD_URL;
		 * 
		 * if (!conn.isOpen()) { Socket socket = new Socket(host.getHostName(),
		 * host.getPort()); conn.bind(socket, params); } BasicHttpRequest request = new
		 * BasicHttpRequest("GET", targets + userId);
		 * //System.out.println(">> Request URI: " + request.getRequestLine().getUri());
		 * 
		 * request.setParams(params); httpexecutor.preProcess(request, httpproc,
		 * context); HttpResponse response = httpexecutor.execute(request, conn,
		 * context); response.setParams(params); httpexecutor.postProcess(response,
		 * httpproc, context);
		 * 
		 * gwpw = EntityUtils.toString(response.getEntity());
		 * 
		 * //System.out.println("<< Response: " + response.getStatusLine());
		 * //System.out.println(EntityUtils.toString(response.getEntity()));
		 * 
		 * //System.out.println("=============="); if (!connStrategy.keepAlive(response,
		 * context)) { conn.close(); } else {
		 * System.out.println("Connection kept alive..."); }
		 * 
		 * return gwpw; } finally { conn.close(); }
		 */
		return "";
	}

	public void setPersistableState(Persistable per, String state) throws Exception {

		if (per instanceof WTDocument || per instanceof WTPart || per instanceof EPMDocument) {

			WTPrincipal currentUser = SessionHelper.manager.getPrincipal();
			String curUserName = currentUser.getName();
			SessionHelper.manager.setAdministrator();

			LifeCycleTemplate temp = LifeCycleHelper.service.getLifeCycleTemplate((LifeCycleManaged) per);

			if (!"Default".equals(temp.getName())) {
				PDMLinkProduct e3psProduct = WCUtil.getPDMLinkProduct();
				WTContainerRef wtContainerRef = WTContainerRef.newWTContainerRef(e3psProduct);
				// LifeCycleHelper.service.reassign((LifeCycleManaged)per,
				// LifeCycleHelper.service.getLifeCycleTemplateReference("Default",
				// wtContainerRef));
			}

			LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) per, State.toState(state));

			SessionHelper.manager.setPrincipal(curUserName);

		} else if (per instanceof MultiApproval) {
			// query linked object
			QuerySpec qs = new QuerySpec(ApprovalToObjectLink.class);
			qs.appendWhere(new SearchCondition(ApprovalToObjectLink.class, "roleAObjectRef.key.id", "=",
					per.getPersistInfo().getObjectIdentifier().getId()), new int[] { 0 });
			QueryResult qr = PersistenceHelper.manager.find(qs);

			ApprovalToObjectLink oLink = null;
			WTPart wtPart = null;
			EPMDocument epmDocument = null;
			Object pbo = null;
			if (qr != null) {
				while (qr.hasMoreElements()) {
					oLink = (ApprovalToObjectLink) qr.nextElement();
					pbo = oLink.getObj();

					if (pbo instanceof WTPart) {
						wtPart = (WTPart) pbo;

						if (state.equals("REJECTED") || state.equals("WITHDRAWN")) {
							state = "INWORK";
						}
						setPersistableState(wtPart, state);

					} else if (pbo instanceof EPMDocument) {
						epmDocument = (EPMDocument) pbo;

						if (state.equals("REJECTED") || state.equals("WITHDRAWN")) {
							state = "INWORK";
						}
						setPersistableState(epmDocument, state);
					}
				}
			}
		}
	}

	public String getLocaleString(String ss) {

		if (CommonUtil.isUSLocale()) {
			if (MASTER_WORKING.equals(ss))
				ss = "Working"; // �۾���
			else if (MASTER_APPROVING.equals(ss))
				ss = "Review"; // ������
			else if (MASTER_APPROVED.equals(ss))
				ss = "Released"; // ����Ϸ�
			else if (MASTER_REJECTED.equals(ss))
				ss = "Rejected"; // �ݷ�
			else if (MASTER_WITHDRAWAL.equals(ss))
				ss = "Withdrawn"; // ȸ��
			else if (MASTER_REWORKING.equals(ss))
				ss = "Rework"; // ���ۼ���
			else if (MASTER_INWORK.equals(ss))
				ss = "InWork"; // �ۼ���

			else if (LINE_REQUEST.equals(ss))
				ss = "Request"; // �����û
			else if (LINE_STANDING.equals(ss))
				ss = "Standing"; // ���
			else if (LINE_APPROVING.equals(ss))
				ss = "Review"; // ������
			else if (LINE_COMMIT.equals(ss))
				ss = "Commit"; // ����Ϸ�
			else if (LINE_REJECTED.equals(ss))
				ss = "Rejected"; // �ݷ�
			else if (LINE_STANDING_CANCEL.equals(ss))
				ss = "Standing-Withdrawn"; // ���-ȸ��
			else if (LINE_WITHDRAWAL.equals(ss))
				ss = "Withdrawn"; // ȸ��
			else if (LINE_TEMP.equals(ss))
				ss = "Notice"; // ����
			else if (LINE_WORKING.equals(ss))
				ss = "Working"; // �۾���
			else if (LINE_REWORKING.equals(ss))
				ss = "Reworking"; // ���ۼ���
			else if (LINE_INWORKING.equals(ss))
				ss = "Inworking"; // �ۼ���
			else if (LINE_DISCUSSING.equals(ss))
				ss = "Consent"; // ����
			else if (LINE_DISCUSSING_AGREE.equals(ss))
				ss = "Agree"; // ����
			else if (LINE_DISCUSSING_REJECT.equals(ss))
				ss = "Reject"; // �ݴ�
			else if (LINE_COMPLETE.equals(ss))
				ss = "Complete"; // �Ϸ��

			else if (APPROVE_REQUEST.equals(ss))
				ss = "Request"; // �ۼ�
			else if (APPROVE_PREAPPROVE.equals(ss))
				ss = "Be-Approve"; // ������ ����
			else if (APPROVE_DISCUSS.equals(ss))
				ss = "Consent"; // ����
			else if (APPROVE_POSTAPPROVE.equals(ss))
				ss = "Af-Approve"; // ����
			else if (APPROVE_NOTIFICATE.equals(ss))
				ss = "Notice"; // �뺸

			else if (WORKING_REPORTER.equals(ss))
				ss = "Submitter"; // ������
			else if (WORKING_REVIEWER.equals(ss))
				ss = "Approver"; // ������
			else if (WORKING_DISCUSSER.equals(ss))
				ss = "Consentor"; // ������
			else if (WORKING_WORKING.equals(ss))
				ss = "Worker"; // �۾���
			else if (WORKING_TEMP.equals(ss))
				ss = "Recipient"; // �뺸��

			else if (WORKING_READY.equals(ss))
				ss = "Ready"; // �����

		}
		return ss;
	}

	// Narae ADD Tsuam

	public void addApprovalLine(ApprovalMaster appMaster, String appName, String owner, String appState, Integer seq) {

		try {
			ApprovalLine line = ApprovalLine.newApprovalLine();
			// ApprovalMaster master = null;
			// String owner ="";
			line.setMaster(appMaster);
			line.setName(appName);// WORKING_WORKING
			line.setOwner(owner);
			line.setState(appState);// LINE_STANDING
			line.setSeq(seq);

			PersistenceHelper.manager.save(line);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ApprovalMaster searchApprovalMaster(Object obj) {
//    		System.out.println(">>>>>>>>>>>>>>>>>>>>> searchApprovalMaster >>>>>>>>>>>");
//    		System.out.println(": obj : " + CommonUtil.getOIDString((Persistable)obj));
		ApprovalMaster master = null;
		try {

			QueryResult rt = PersistenceHelper.navigate((Persistable) obj, "request", ApprovalObjectLink.class, true);
			while (rt.hasMoreElements()) {
				master = (ApprovalMaster) rt.nextElement();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return master;
	}

	public void approvalActiveStepChange(Object obj, WTUser user) throws Exception {
		ApprovalMaster master = searchApprovalMaster(obj);

		QueryResult qr = activeLine(master);
		ApprovalLine line = null;

		if (qr != null) {
			while (qr.hasMoreElements()) {
				line = (ApprovalLine) qr.nextElement();
				if (APPROVE_POSTAPPROVE.equals(line.getStepName())) {
					line.setOwner(user.getName());
					line = (ApprovalLine) PersistenceHelper.manager.save(line);
				}
			}
		}

	}

	/* checked Approved Search */
	public ArrayList getApprover(ApprovalMaster master) throws Exception {
		if (!SERVER) {

			try {
				Class argTypes[] = new Class[] { ApprovalMaster.class };
				Object args[] = new Object[] { master };
				return (ArrayList) RemoteMethodServer.getDefault().invoke("getApprover", null, this, argTypes, args);
			} catch (RemoteException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new WTException(e);
			}
		}

		MethodContext methodcontext = null;
		WTConnection wtconnection = null;

		PreparedStatement st = null;
		ResultSet rs = null;
		ArrayList list = new ArrayList();
		try {
			methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			Connection con = wtconnection.getConnection();

			StringBuffer sb = new StringBuffer();
			sb.append("SELECT TOP 2 owner FROM ApprovalLine ").append(" WHERE idA3A3 = ?").append(" AND name =?")
					.append(" order by seq desc ");

			// System.out.println("############### sb.toString()"+sb.toString());

			st = con.prepareStatement(sb.toString());
			st.setDouble(1, CommonUtil.getOIDLongValue(master));
			st.setString(2, ApprovalHelper.WORKING_REVIEWER);
			rs = st.executeQuery();

			String seqNum = null;
			while (rs.next()) {
				String id = rs.getString("owner");
				// System.out.println("id :" + id);
				WTUser user = UserHelper.getWTUser(id);
				if (user != null) {
					// System.out.println("user.getFullName() :" + user.getFullName());
					list.add(user.getFullName());
				}

			}

			return list;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (st != null) {
				st.close();
			}
			if (DBProperties.FREE_CONNECTION_IMMEDIATE && !wtconnection.isTransactionActive()) {
				MethodContext.getContext().freeConnection();
			}
		}
	}

}
