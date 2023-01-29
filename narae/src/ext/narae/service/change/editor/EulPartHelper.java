/*
 * @(#) EulPartHelper.java  Create on 2006. 7. 3.
 * Copyright (c) e3ps. All rights reserverd
 */
package ext.narae.service.change.editor;

import java.beans.PropertyDescriptor;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Vector;

import ext.narae.service.approval.ApprovalMaster;
import ext.narae.service.approval.ApprovalObjectLink;
import ext.narae.service.approval.beans.ApprovalHelper;
import ext.narae.service.org.People;
import ext.narae.service.org.beans.UserHelper;
import ext.narae.service.part.beans.PartHelper;
import ext.narae.util.DateUtil;
import wt.access.NotAuthorizedException;
import wt.enterprise.RevisionControlled;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.iba.definition.IBADefinitionException;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.StringValue;
import wt.introspection.ClassInfo;
import wt.introspection.WTIntrospector;
import wt.lifecycle.State;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartConfigSpec;
import wt.part.WTPartMaster;
import wt.part.WTPartStandardConfigSpec;
import wt.part.WTPartUsageLink;
import wt.query.ClassAttribute;
import wt.query.ColumnExpression;
import wt.query.ConstantExpression;
import wt.query.OrderBy;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.RelationalExpression;
import wt.query.SQLFunction;
import wt.query.SearchCondition;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.ControlBranch;
import wt.vc.VersionControlHelper;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;

/**
 * 占쏙옙占쏙옙占쏙옙占쏙옙占싶울옙占쏙옙 占쏙옙품占쏙옙占쏙옙 클占쏙옙占쏙옙
 * 
 * @author Choi Seunghwan, skyprda@e3ps.com
 * @version 1.00, 2006. 7. 3.
 * @since 1.4
 */
public class EulPartHelper {
	public static EulPartHelper manager = new EulPartHelper();

	AttributeDefDefaultView aview;
	AttributeDefDefaultView sview;

	private EulPartHelper() {
	}

	public WTPart getPart(String partNumber, String version)
			throws WTException, RemoteException, WTPropertyVetoException {
		if (partNumber == null || partNumber.length() == 0)
			return null;
		System.out.println("getPart : " + partNumber + " - " + version);
		QuerySpec query = new QuerySpec();
		int j = query.addClassList(WTPart.class, true);
		query.appendWhere(new SearchCondition(WTPart.class, "master>number", "=", partNumber), new int[] { 0 });
		query.appendAnd();
		query.appendWhere(VersionControlHelper.getSearchCondition(WTPart.class, true), new int[] { 0 });
		query.appendAnd();
		query.appendWhere(new SearchCondition(WTPart.class, "versionInfo.identifier.versionId", "=", version),
				new int[] { 0 });

		// 占쏙옙 占쏙옙占쏙옙占쏙옙 占쌩곤옙占실몌옙 占쏙옙占신뱄옙占쏙옙占쏙옙 占쏙옙품占쏙옙 占싯삼옙占쏙옙 占쏙옙 占쏙옙
		// addLastVersionCondition(query, j);

		query.appendOrderBy(new OrderBy(new ClassAttribute(WTPart.class, "thePersistInfo.modifyStamp"), true),
				new int[] { j });

		QueryResult qr = PersistenceHelper.manager.find(query);

		if (qr.hasMoreElements()) {
			Object[] o = (Object[]) qr.nextElement();
			return (WTPart) o[0];
		}
		return null;
	}

	public WTPart getPart(WTPart part, View view) throws RemoteException, WTPropertyVetoException, WTException {
		return getPart(part.getNumber(), view);
	}

	public WTPart getPart(String number, View view) throws WTException, RemoteException, WTPropertyVetoException {
		QuerySpec spec = new QuerySpec();
		int j = spec.addClassList(WTPart.class, true);
		spec.appendWhere(new SearchCondition(WTPart.class, "master>number", "=", number), new int[] { j });
		spec.appendAnd();
		spec.appendWhere(new SearchCondition(WTPart.class, "iterationInfo.latest", SearchCondition.IS_TRUE, true),
				new int[] { j });

		spec.appendAnd();
		spec.appendWhere(new SearchCondition(WTPart.class, "view.key.id", "=",
				view.getPersistInfo().getObjectIdentifier().getId()), new int[] { j });

		addLastVersionCondition(spec, j);

		spec.appendOrderBy(new OrderBy(new ClassAttribute(WTPart.class, "thePersistInfo.modifyStamp"), true),
				new int[] { j });
		QueryResult rr = PersistenceHelper.manager.find(spec);
		if (rr.hasMoreElements()) {
			Object[] o = (Object[]) rr.nextElement();
			return (WTPart) o[0];
		}
		return null;
	}

	public WTPart getPart(String number) throws WTException, RemoteException, WTPropertyVetoException {
		QuerySpec spec = new QuerySpec();
		int j = spec.addClassList(WTPart.class, true);

		ClassAttribute ca = new ClassAttribute(WTPart.class, WTPart.NUMBER);
		ColumnExpression ce = ConstantExpression.newExpression(number.toUpperCase());
		SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
		SearchCondition sc = new SearchCondition(function, SearchCondition.EQUAL, ce);
		spec.appendWhere(sc, new int[] { j });
		spec.appendAnd();
//		spec.appendWhere(new SearchCondition(WTPart.class, "master>number", "=", number), new int[] { j });
//		spec.appendAnd();
		spec.appendWhere(new SearchCondition(WTPart.class, "iterationInfo.latest", SearchCondition.IS_TRUE, true),
				new int[] { j });
		spec.appendAnd();
		spec.appendWhere(new SearchCondition(WTPart.class, WTPart.PART_TYPE, "=", "separable"), new int[] { j });

//		addLastVersionCondition(spec, j);

		int branchIdx = spec.appendClassList(ControlBranch.class, false);
		int childBranchIdx = spec.appendClassList(ControlBranch.class, false);

		if (spec.getConditionCount() > 0)
			spec.appendAnd();
		spec.appendWhere(new SearchCondition(WTPart.class, RevisionControlled.BRANCH_IDENTIFIER, ControlBranch.class,
				WTAttributeNameIfc.ID_NAME), new int[] { j, branchIdx });

		if (spec.getConditionCount() > 0)
			spec.appendAnd();
		SearchCondition outerJoinSc = new SearchCondition(ControlBranch.class, WTAttributeNameIfc.ID_NAME,
				ControlBranch.class, "predecessorReference.key.id");
		outerJoinSc.setOuterJoin(SearchCondition.RIGHT_OUTER_JOIN);
		spec.appendWhere(outerJoinSc, new int[] { branchIdx, childBranchIdx });

		ClassAttribute childBranchIdNameCa = new ClassAttribute(ControlBranch.class, WTAttributeNameIfc.ID_NAME);
		spec.appendSelect(childBranchIdNameCa, new int[] { childBranchIdx }, false);

		if (spec.getConditionCount() > 0)
			spec.appendAnd();
		spec.appendWhere(new SearchCondition(childBranchIdNameCa, SearchCondition.IS_NULL),
				new int[] { childBranchIdx });

		spec.appendOrderBy(new OrderBy(new ClassAttribute(WTPart.class, "thePersistInfo.modifyStamp"), true),
				new int[] { j });
		QueryResult rr = PersistenceHelper.manager.find(spec);
		System.out.println("spec=" + spec);
		if (rr.hasMoreElements()) {
			Object[] o = (Object[]) rr.nextElement();
			return (WTPart) o[0];
		}
		return null;
	}

	public WTPart getPlantPart(String number, String plant)
			throws WTException, RemoteException, WTPropertyVetoException {
		return getPart(number + "_" + plant);
	}

	public QueryResult getViews(WTPart part) throws Exception {
		QuerySpec spec = new QuerySpec();
		int index1 = spec.addClassList(View.class, true);
		int index2 = spec.addClassList(WTPart.class, false);
		SearchCondition sc = new SearchCondition(
				new ClassAttribute(View.class, "thePersistInfo.theObjectIdentifier.id"), "=",
				new ClassAttribute(WTPart.class, "view.key.id"));
		sc.setFromIndicies(new int[] { index1, index2 }, 0);
		sc.setOuterJoin(0);
		spec.appendWhere(sc, new int[] { index1, index2 });
		spec.appendAnd();
		spec.appendWhere(new SearchCondition(WTPart.class, "master>number", "=", part.getNumber()),
				new int[] { index2 });
		spec.setDistinct(true);
		spec.appendAnd();
		spec.appendWhere(new SearchCondition(WTPart.class, "checkoutInfo.state", SearchCondition.NOT_LIKE, "c/o"),
				new int[] { index2 });
		spec.appendAnd();
		spec.appendWhere(new SearchCondition(WTPart.class, "iterationInfo.latest", SearchCondition.IS_TRUE, true),
				new int[] { index2 });
		return PersistenceHelper.manager.find(spec);
	}

	public Vector ancestorPart(WTPart part, View view) throws WTException {
		return ancestorPart(part, view, null);
	}

	public Vector ancestorPart(WTPart part) throws WTException {
		return ancestorPart(part, BEContext.getView(), null);
	}

	public Vector ancestorPart(WTPart part, View view, State state) throws WTException {
		Vector v = new Vector();
		try {
			WTPartMaster master = (WTPartMaster) part.getMaster();
			QuerySpec qs = new QuerySpec();
			int index1 = qs.addClassList(WTPartUsageLink.class, true);
			int index2 = qs.addClassList(WTPart.class, true);
			qs.appendWhere(new SearchCondition(WTPartUsageLink.class, "roleBObjectRef.key.id", "=",
					master.getPersistInfo().getObjectIdentifier().getId()), new int[] { index1 });
			SearchCondition sc = new SearchCondition(new ClassAttribute(WTPartUsageLink.class, "roleAObjectRef.key.id"),
					"=", new ClassAttribute(WTPart.class, "thePersistInfo.theObjectIdentifier.id"));
			sc.setFromIndicies(new int[] { index1, index2 }, 0);
			sc.setOuterJoin(0);
			qs.appendAnd();
			qs.appendWhere(sc, new int[] { index1, index2 });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(WTPart.class, "iterationInfo.latest", SearchCondition.IS_TRUE, true),
					new int[] { index2 });
			if (view != null) {
				qs.appendAnd();
				qs.appendWhere(new SearchCondition(WTPart.class, "view.key.id", "=",
						view.getPersistInfo().getObjectIdentifier().getId()), new int[] { index2 });
			}
			if (state != null) {
				qs.appendAnd();
				qs.appendWhere(new SearchCondition(WTPart.class, "state.state", "=", state.toString()),
						new int[] { index2 });
			}

			addLastVersionCondition(qs, index2);

			ClassInfo classinfo = WTIntrospector.getClassInfo(WTPart.class);
			PropertyDescriptor dd = classinfo.getPropertyDescriptor("number");
			ClassAttribute classattribute = new ClassAttribute(WTPart.class, (String) dd.getValue("QueryName"));
			classattribute.setColumnAlias("wtsort" + String.valueOf(0));
			qs.appendSelect(classattribute, index2, false);
			OrderBy orderby = new OrderBy(classattribute, false, null);
			qs.appendOrderBy(orderby, index2);
			QueryResult re = PersistenceHelper.manager.find(qs);
			while (re.hasMoreElements()) {
				Object oo[] = (Object[]) re.nextElement();
				// WTPart wtpart = (WTPart) oo[1];
				v.add(oo);
			}
		} catch (Exception ex) {
			throw new WTException();
		}
		return v;
	}

	public Vector descentLastPart(WTPart part, View view) throws WTException {
		return descentLastPart(part, view, null);
	}

	public Vector descentLastPart(WTPart part) throws WTException {
		return descentLastPart(part, BEContext.getView(), null);
	}

	public Vector descentLastPart(WTPart part, View view, State state) throws WTException {
		Vector v = new Vector();
		if (!PersistenceHelper.isPersistent(part))
			return v;
		try {
			WTPartConfigSpec configSpec = WTPartConfigSpec
					.newWTPartConfigSpec(WTPartStandardConfigSpec.newWTPartStandardConfigSpec(view, state));
			QueryResult re = wt.part.WTPartHelper.service.getUsesWTParts(part, configSpec);
			while (re.hasMoreElements()) {
				Object obj = re.nextElement();
				// Object oo[] = (Object[]) re.nextElement();
				if (obj instanceof Object[]) {
					Object[] oo = (Object[]) obj;
					if (!(oo[1] instanceof WTPart)) {
						continue;
					}
					WTPart pp = (WTPart) oo[1];
					if (pp.getView() == null) {
						continue;
					}
					if (view != null && view.getName().equals(((View) pp.getView().getObject()).getName())) {
						v.add(oo);
					}
				} else if (obj instanceof WTPartUsageLink) {
					WTPartUsageLink link = (WTPartUsageLink) obj;

					WTPartMaster master = (WTPartMaster) link.getRoleBObject();
					WTPart pp = PartHelper.manager.getPart(master.getNumber());

					System.out.println("pp.getView() === " + pp.getView());
					System.out.println("view.getName() === " + view.getName());
					if (pp.getView() == null) {
						continue;
					}
					if (view != null && view.getName().equals(((View) pp.getView().getObject()).getName())) {
						v.add(link);
					}
					// v.add(link);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new WTException();
		}
		return v;
	}

	public void addLastVersionCondition(QuerySpec query, int idx) throws IBADefinitionException, NotAuthorizedException,
			RemoteException, WTException, QueryException, WTPropertyVetoException {

		addLastVersionCondition2(query, WTPart.class, idx);
		/*
		 * if(aview==null) aview =
		 * IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(
		 * "LatestVersionFlag");
		 * 
		 * if (aview != null) { if (query.getConditionCount() > 0) query.appendAnd();
		 * 
		 * int idx_VFlag = query.appendClassList(StringValue.class, false);
		 * SearchCondition sc = new SearchCondition(new
		 * ClassAttribute(StringValue.class, "theIBAHolderReference.key.id"), "=", new
		 * ClassAttribute( WTPart.class, "thePersistInfo.theObjectIdentifier.id"));
		 * sc.setFromIndicies(new int[] { idx_VFlag, idx }, 0); sc.setOuterJoin(0);
		 * query.appendWhere(sc, new int[] { idx_VFlag, idx }); query.appendAnd(); sc =
		 * new SearchCondition(StringValue.class, "definitionReference.hierarchyID",
		 * "=", aview.getHierarchyID()); query.appendWhere(sc, new int[] { idx_VFlag });
		 * query.appendAnd(); sc = new SearchCondition(StringValue.class, "value", "=",
		 * "TRUE"); query.appendWhere(sc, new int[] { idx_VFlag }); }
		 */
	}

	public static void addLastVersionCondition2(QuerySpec qs, Class targetClass, int idx) throws WTException {
		try {
			int branchIdx = qs.appendClassList(ControlBranch.class, false);
			int childBranchIdx = qs.appendClassList(ControlBranch.class, false);

			// #. 媛앹껜 - Parent ControlBranch 媛?Join
			if (qs.getConditionCount() > 0)
				qs.appendAnd();
			qs.appendWhere(new SearchCondition(targetClass, RevisionControlled.BRANCH_IDENTIFIER, ControlBranch.class,
					WTAttributeNameIfc.ID_NAME), new int[] { idx, branchIdx });

			// #. ControlBranch ??遺紐?- ?먯떇 outer join
			if (qs.getConditionCount() > 0)
				qs.appendAnd();
			SearchCondition outerJoinSc = new SearchCondition(ControlBranch.class, WTAttributeNameIfc.ID_NAME,
					ControlBranch.class, "predecessorReference.key.id");
			outerJoinSc.setOuterJoin(SearchCondition.RIGHT_OUTER_JOIN);
			qs.appendWhere(outerJoinSc, new int[] { branchIdx, childBranchIdx });

			// #. ?먯떇 ControllBranch 媛 null ?대㈃ 理쒖떊 Revision
			ClassAttribute childBranchIdNameCa = new ClassAttribute(ControlBranch.class, WTAttributeNameIfc.ID_NAME);
			qs.appendSelect(childBranchIdNameCa, new int[] { childBranchIdx }, false);

			if (qs.getConditionCount() > 0)
				qs.appendAnd();
			qs.appendOpenParen();
			qs.appendWhere(new SearchCondition(childBranchIdNameCa, SearchCondition.IS_NULL),
					new int[] { childBranchIdx });
			qs.appendOr();
			/*
			 * qs.appendWhere(new SearchCondition(ControlBranch.class, "wipState",
			 * SearchCondition.EQUAL,"to wrk"), new int[] {childBranchIdx});
			 */
			qs.appendWhere(new SearchCondition(ControlBranch.class, "wipState", SearchCondition.LIKE, "%to wrk%"),
					new int[] { childBranchIdx });
			qs.appendCloseParen();
		} catch (WTPropertyVetoException e) {
			throw new WTException(e);
		}

	}

	public void addSapCondition(QuerySpec query, int idx) throws IBADefinitionException, NotAuthorizedException,
			RemoteException, WTException, QueryException, WTPropertyVetoException {

		if (sview == null)
			sview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath("ITEM_TYPE");

		if (sview != null) {
			if (query.getConditionCount() > 0)
				query.appendAnd();

			int idx_VFlag = query.appendClassList(StringValue.class, false);
			SearchCondition sc = new SearchCondition(
					new ClassAttribute(StringValue.class, "theIBAHolderReference.key.id"), "=",
					new ClassAttribute(WTPart.class, "thePersistInfo.theObjectIdentifier.id"));
			sc.setFromIndicies(new int[] { idx_VFlag, idx }, 0);
			sc.setOuterJoin(0);
			query.appendWhere(sc, new int[] { idx_VFlag, idx });
			query.appendAnd();
			sc = new SearchCondition(StringValue.class, "definitionReference.hierarchyID", "=", sview.getHierarchyID());
			query.appendWhere(sc, new int[] { idx_VFlag });
			query.appendAnd();
			sc = new SearchCondition(StringValue.class, "value", "=", "SAP");
			query.appendWhere(sc, new int[] { idx_VFlag });
		}
	}

	public QuerySpec getQuerySpec(String number, String name, boolean isLastest, boolean isMine, boolean isInWork,
			String views) {
		QuerySpec sp = null;
		try {
			sp = new QuerySpec();
			Class class1 = WTPart.class;

			int j = sp.addClassList(class1, true);

			if (isLastest)
				addLastVersionCondition(sp, j);

			// addSapCondition(sp,j);

			if (sp.getConditionCount() > 0)
				sp.appendAnd();
			sp.appendWhere(VersionControlHelper.getSearchCondition(class1, true), new int[] { j });

			// sp.appendWhere(new SearchCondition(WTPart.class,
			// "iterationInfo.latest", SearchCondition.IS_TRUE,
			// true),j);

			if (isMine) {
				if (sp.getConditionCount() > 0)
					sp.appendAnd();
				long uid = BEContext.user.getPersistInfo().getObjectIdentifier().getId();
				sp.appendWhere(new SearchCondition(class1, "iterationInfo.creator.key.id", "=", uid), new int[] { j });
			}
			if (isInWork) {
				// if (sp.getConditionCount() > 0) sp.appendAnd();
				// sp.appendWhere(new SearchCondition(class1, "state.state", "=", "INWORK"), new
				// int[] { j });
			} else {
				if (sp.getConditionCount() > 0)
					sp.appendAnd();

				// sp.appendWhere(new SearchCondition(class1, "state.state", "=", "APPROVED"),
				// new int[] { j });
				int sindex = sp.addClassList(ApprovalMaster.class, false);
				int lindex = sp.addClassList(ApprovalObjectLink.class, false);
				sp.appendWhere(new SearchCondition(class1, "thePersistInfo.theObjectIdentifier.id",
						ApprovalObjectLink.class, "roleAObjectRef.key.id"), new int[] { j, lindex });
				sp.appendAnd();
				sp.appendWhere(new SearchCondition(ApprovalMaster.class, "thePersistInfo.theObjectIdentifier.id",
						ApprovalObjectLink.class, "roleBObjectRef.key.id"), new int[] { sindex, lindex });
				sp.appendAnd();
				sp.appendWhere(new SearchCondition(ApprovalMaster.class, ApprovalMaster.STATE, "=",
						ApprovalHelper.MASTER_APPROVED), new int[] { sindex });
			}

			if (!BEContext.isUseView) {
				views = "Design";
			}
			if (views != null && views.length() > 0) {
				if (sp.getConditionCount() > 0)
					sp.appendAnd();
				View view = ViewHelper.service.getView(views);
				sp.appendWhere(new SearchCondition(class1, "view.key.id", "=",
						view.getPersistInfo().getObjectIdentifier().getId()), new int[] { j });
			}

			if (number.length() > 0) {
				if (sp.getConditionCount() > 0)
					sp.appendAnd();
				number = number.toLowerCase() + "%";
				ClassAttribute ca = new ClassAttribute(class1, WTPart.NUMBER);
				SQLFunction sf = SQLFunction.newSQLFunction("LOWER");
				sf.setArgumentAt((RelationalExpression) ca, 0);
				RelationalExpression expression = new wt.query.ConstantExpression(number);
				SearchCondition sc3 = new SearchCondition((RelationalExpression) sf, SearchCondition.LIKE, expression);
				sp.appendSearchCondition(sc3);
			}
			if (name.length() > 0) {
				if (sp.getConditionCount() > 0)
					sp.appendAnd();
				name = name.toLowerCase() + "%";
				ClassAttribute ca = new ClassAttribute(class1, WTPart.NAME);
				SQLFunction sf = SQLFunction.newSQLFunction("LOWER");
				sf.setArgumentAt((RelationalExpression) ca, 0);
				RelationalExpression expression = new wt.query.ConstantExpression(name);
				SearchCondition sc3 = new SearchCondition((RelationalExpression) sf, SearchCondition.LIKE, expression);
				sp.appendSearchCondition(sc3);
			}

			ClassAttribute classattribute = new ClassAttribute(class1, "master>number");
			classattribute.setColumnAlias("wtsort" + String.valueOf(0));
			sp.appendSelect(classattribute, j, false);
			OrderBy orderby = new OrderBy(classattribute, false, null);
			sp.appendOrderBy(orderby, j);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.out.println("############" + sp);
		return sp;
	}

	public QuerySpec getQuerySpec(String number, String name, boolean isLastest, boolean isMine, boolean isInWork,
			String views, Hashtable searchValue) {
		QuerySpec sp = null;
		try {
			sp = new QuerySpec();
			Class class1 = WTPart.class;

			int j = sp.addClassList(class1, true);

			if (isLastest)
				addLastVersionCondition(sp, j);

			// addSapCondition(sp,j);

			if (sp.getConditionCount() > 0)
				sp.appendAnd();
			sp.appendWhere(VersionControlHelper.getSearchCondition(class1, true), new int[] { j });

			// sp.appendWhere(new SearchCondition(WTPart.class,
			// "iterationInfo.latest", SearchCondition.IS_TRUE,
			// true),j);

			if (isMine) {
				if (sp.getConditionCount() > 0)
					sp.appendAnd();
				long uid = BEContext.user.getPersistInfo().getObjectIdentifier().getId();
				sp.appendWhere(new SearchCondition(class1, "iterationInfo.creator.key.id", "=", uid), new int[] { j });
			}
			if (isInWork) {
				// if (sp.getConditionCount() > 0) sp.appendAnd();
				// sp.appendWhere(new SearchCondition(class1, "state.state", "=", "INWORK"), new
				// int[] { j });
			} else {
				if (sp.getConditionCount() > 0)
					sp.appendAnd();

				// sp.appendWhere(new SearchCondition(class1, "state.state", "=", "APPROVED"),
				// new int[] { j });
				int sindex = sp.addClassList(ApprovalMaster.class, false);
				int lindex = sp.addClassList(ApprovalObjectLink.class, false);
				sp.appendWhere(new SearchCondition(class1, "thePersistInfo.theObjectIdentifier.id",
						ApprovalObjectLink.class, "roleAObjectRef.key.id"), new int[] { j, lindex });
				sp.appendAnd();
				sp.appendWhere(new SearchCondition(ApprovalMaster.class, "thePersistInfo.theObjectIdentifier.id",
						ApprovalObjectLink.class, "roleBObjectRef.key.id"), new int[] { sindex, lindex });
				sp.appendAnd();
				sp.appendWhere(new SearchCondition(ApprovalMaster.class, ApprovalMaster.STATE, "=",
						ApprovalHelper.MASTER_APPROVED), new int[] { sindex });
			}

			if (!BEContext.isUseView) {
				views = "Design";
			}
			if (views != null && views.length() > 0) {
				if (sp.getConditionCount() > 0)
					sp.appendAnd();
				View view = ViewHelper.service.getView(views);
				sp.appendWhere(new SearchCondition(class1, "view.key.id", "=",
						view.getPersistInfo().getObjectIdentifier().getId()), new int[] { j });
			}

			if (number.length() > 0) {
				if (sp.getConditionCount() > 0)
					sp.appendAnd();
				number = number.toLowerCase() + "%";
				ClassAttribute ca = new ClassAttribute(class1, WTPart.NUMBER);
				SQLFunction sf = SQLFunction.newSQLFunction("LOWER");
				sf.setArgumentAt((RelationalExpression) ca, 0);
				RelationalExpression expression = new wt.query.ConstantExpression(number);
				SearchCondition sc3 = new SearchCondition((RelationalExpression) sf, SearchCondition.LIKE, expression);
				sp.appendSearchCondition(sc3);
			}
			if (name.length() > 0) {
				if (sp.getConditionCount() > 0)
					sp.appendAnd();
				name = name.toLowerCase() + "%";
				ClassAttribute ca = new ClassAttribute(class1, WTPart.NAME);
				SQLFunction sf = SQLFunction.newSQLFunction("LOWER");
				sf.setArgumentAt((RelationalExpression) ca, 0);
				RelationalExpression expression = new wt.query.ConstantExpression(name);
				SearchCondition sc3 = new SearchCondition((RelationalExpression) sf, SearchCondition.LIKE, expression);
				sp.appendSearchCondition(sc3);
			}

			String spec = (String) searchValue.get("spec");
			if (spec.length() > 0) {
				AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath("Spec");

				if (aview != null) {
					if (sp.getConditionCount() > 0)
						sp.appendAnd();
					int _idx = sp.appendClassList(StringValue.class, false);
					sp.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", WTPart.class,
							"thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, j });
					sp.appendAnd();
					sp.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID",
							SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
					sp.appendAnd();
					sp.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE,
							("%" + spec.trim() + "%").toUpperCase()), new int[] { _idx });
				}
			}

			String maker = (String) searchValue.get("maker");
			if (maker.length() > 0) {
				AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath("Maker");

				if (aview != null) {
					if (sp.getConditionCount() > 0)
						sp.appendAnd();
					int _idx = sp.appendClassList(StringValue.class, false);
					sp.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", WTPart.class,
							"thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, j });
					sp.appendAnd();
					sp.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID",
							SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
					sp.appendAnd();
					sp.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE,
							("%" + maker.trim() + "%").toUpperCase()), new int[] { _idx });
				}
			}

			String creator = (String) searchValue.get("creator");
			if (creator.length() > 0) {
				QueryResult result = UserHelper.getPeoples(creator);

				boolean queryCheck = false;
				if (result.hasMoreElements()) {
					queryCheck = true;
					if (sp.getConditionCount() > 0) {
						sp.appendAnd();
					}
					sp.appendOpenParen();
				}
				while (result.hasMoreElements()) {
					Object[] obj = (Object[]) result.nextElement();
					People people = (People) obj[0];
					WTUser user = people.getUser();
					sp.appendWhere(new SearchCondition(WTPart.class, "iterationInfo.creator.key", SearchCondition.EQUAL,
							PersistenceHelper.getObjectIdentifier(user)), new int[] { j });
					if (result.hasMoreElements()) {
						sp.appendOr();
					}
				}
				if (queryCheck) {
					sp.appendCloseParen();
				}
			}

			String createdate1 = (String) searchValue.get("createdate1");
			if (createdate1.length() == 10) {
				if (sp.getConditionCount() > 0) {
					sp.appendAnd();
				}
				sp.appendWhere(new SearchCondition(WTPart.class, "thePersistInfo.createStamp",
						SearchCondition.GREATER_THAN, DateUtil.convertStartDate(createdate1.trim())), new int[] { j });
			}

			String createdate2 = (String) searchValue.get("createdate2");
			if (createdate2.length() == 10) {
				if (sp.getConditionCount() > 0) {
					sp.appendAnd();
				}
				sp.appendWhere(new SearchCondition(WTPart.class, "thePersistInfo.createStamp",
						SearchCondition.LESS_THAN, DateUtil.convertStartDate(createdate2.trim())), new int[] { j });
			}

			ClassAttribute classattribute = new ClassAttribute(class1, "master>number");
			classattribute.setColumnAlias("wtsort" + String.valueOf(0));
			sp.appendSelect(classattribute, j, false);
			OrderBy orderby = new OrderBy(classattribute, false, null);
			sp.appendOrderBy(orderby, j);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.out.println("############" + sp);
		return sp;
	}

}
