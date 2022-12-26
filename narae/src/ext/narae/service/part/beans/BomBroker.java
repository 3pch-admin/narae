/*
 * @(#) PartData.java  Create on 2004. 12. 9.
 * Copyright (c) e3ps. All rights reserverd
 */
package ext.narae.service.part.beans;

import java.beans.PropertyDescriptor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import ext.narae.util.query.SearchUtil;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.introspection.ClassInfo;
import wt.introspection.WTIntrospector;
import wt.lifecycle.State;
import wt.part.WTPart;
import wt.part.WTPartBaselineConfigSpec;
import wt.part.WTPartConfigSpec;
import wt.part.WTPartMaster;
import wt.part.WTPartStandardConfigSpec;
import wt.part.WTPartUsageLink;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.vc.baseline.Baseline;
import wt.vc.baseline.BaselineMember;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;

public class BomBroker {

	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

	public void compareBom(PartTreeData data, PartTreeData data2, ArrayList result) {

		result.add(new PartTreeData[] { data, data2 });

		for (int i = 0; i < data.children.size(); i++) {
			PartTreeData cd = (PartTreeData) data.children.get(i);

			boolean flag = false;

			for (int j = 0; j < data2.children.size(); j++) {
				PartTreeData cd2 = (PartTreeData) data2.children.get(j);

				if (cd.equals(cd2)) {
					data2.children.remove(j);
					compareBom(cd, cd2, result);
					flag = true;
					break;
				}
			}

			if (!flag) {
				setHtmlForm(cd, null, result);
			}
		}

		for (int j = 0; j < data2.children.size(); j++) {
			PartTreeData cd2 = (PartTreeData) data2.children.get(j);
			setHtmlForm(null, cd2, result);
		}
	}

	public void sort(PartTreeData data) {

		ArrayList temp = new ArrayList();

		for (int i = 0; i < data.children.size(); i++) {
			PartTreeData o = (PartTreeData) data.children.get(i);

			sort(o);

			boolean flag2 = true;

			for (int l = 0; l < temp.size(); l++) {

				PartTreeData ptd = (PartTreeData) temp.get(l);

				if (o.itemSeq.compareTo(ptd.itemSeq) < 0) {
					temp.add(l, o);
					flag2 = false;
					break;
				}
			}
			if (flag2) {
				temp.add(o);
			}
		}

		data.children = temp;
	}

	public void setHtmlForm(PartTreeData data1, PartTreeData data2, ArrayList result) {

		PartTreeData[] pd = new PartTreeData[] { data1, data2 };
		result.add(pd);

		PartTreeData data = data1;
		if (data == null)
			data = data2;

		for (int i = 0; i < data.children.size(); i++) {
			PartTreeData cd = (PartTreeData) data.children.get(i);
			if (data1 == null) {
				setHtmlForm(null, cd, result);
			} else {
				setHtmlForm(cd, null, result);
			}

			if (data.children.size() > i + 1) {
				cd.lineImg = "join";
			}
		}
	}

	public void setHtmlForm(PartTreeData data, ArrayList result) {

		result.add(data);

		for (int i = 0; i < data.children.size(); i++) {
			PartTreeData cd = (PartTreeData) data.children.get(i);
			setHtmlForm(cd, result);

			if (data.children.size() > i + 1) {
				cd.lineImg = "join";
			}
		}
	}

	public View getView() throws WTException {
		return ViewHelper.service.getView("Design");
	}

	public PartTreeData getTree(WTPart part, boolean desc, Baseline baseline) throws WTException {

		PartTreeData root = getTree(part, null, 0, desc, baseline);
		sort(root);
		return root;
	}

	public PartTreeData getTree(WTPart part, WTPartUsageLink link, int level, boolean desc, Baseline baseline)
			throws WTException {
		PartTreeData data = new PartTreeData(part, link, level);

		ArrayList list = null;

		if (desc) {
			if (baseline == null) {
				list = descentLastPart(part, getView(), null);
			} else {
				list = descentLastPart(part, baseline, null);
			}
		} else {
			if (baseline == null) {
				list = ancestorPart(part, getView(), null);
			} else {
				list = ancestorPart(part, baseline, null);
			}
		}
		for (int i = 0; i < list.size(); i++) {

			Object obj = list.get(i);
			if (obj instanceof Object[]) {
				Object[] o = (Object[]) list.get(i);
				PartTreeData cd = getTree((WTPart) o[1], (WTPartUsageLink) o[0], level + 1, desc, baseline);
				data.children.add(cd);
				// WTPartUsageLink ADD 2012.12.27 Tsuam
			} else if (obj instanceof WTPartUsageLink) {
				try {
					WTPartUsageLink usageLink = (WTPartUsageLink) obj;
					WTPartMaster master = (WTPartMaster) usageLink.getRoleBObject();

					WTPart childPart = PartHelper.manager.getPart(master.getNumber());
					PartTreeData cd = getTree(childPart, usageLink, level + 1, desc, baseline);
					data.children.add(cd);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		}
		return data;
	}

	public ArrayList descentLastPart(WTPart part, Baseline baseline, State state) throws WTException {
		ArrayList v = new ArrayList();
		if (!PersistenceHelper.isPersistent(part))
			return v;
		try {
			WTPartBaselineConfigSpec configSpec = WTPartBaselineConfigSpec.newWTPartBaselineConfigSpec(baseline);
			QueryResult re = wt.part.WTPartHelper.service.getUsesWTParts(part, configSpec);

			while (re.hasMoreElements()) {
				Object oo[] = (Object[]) re.nextElement();

				if (!(oo[1] instanceof WTPart)) {
					continue;
				}
				v.add(oo);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new WTException();
		}
		return v;
	}

	public ArrayList descentLastPart(WTPart part, View view, State state) throws WTException {
		ArrayList v = new ArrayList();
//		if (!PersistenceHelper.isPersistent(part))
//			return v;

		try {
			WTPartConfigSpec configSpec = WTPartConfigSpec
					.newWTPartConfigSpec(WTPartStandardConfigSpec.newWTPartStandardConfigSpec(view, state));
			QueryResult re = wt.part.WTPartHelper.service.getUsesWTParts(part, configSpec);
			System.out.println("re.size() = " + re.size());
			while (re.hasMoreElements()) {
				Object obj = re.nextElement();
				System.out.println("obj = " + obj);

				if (obj instanceof Object[]) {

					Object oo[] = (Object[]) obj;
					System.out.println("oo[] = " + oo[1]);
					if (!(oo[1] instanceof WTPart)) {
						continue;
					}
					v.add(oo);
					// WTPartUsageLink ADD 2012.12.27 Tsuam
				} else if (obj instanceof WTPartUsageLink) {

					WTPartUsageLink usageLink = (WTPartUsageLink) obj;

					v.add(usageLink);
				}
			}
		} catch (Exception ex) {
			System.out.println("system.ERROR ");
			ex.printStackTrace();

			throw new WTException();
		}
		return v;
	}

	public ArrayList ancestorPart(WTPart part, View view, State state) throws WTException {
		ArrayList v = new ArrayList();
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

			SearchUtil.addLastVersionCondition(qs, WTPart.class, index2);

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
				v.add(oo);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new WTException();
		}
		return v;
	}

	public ArrayList ancestorPart(WTPart part, Baseline baseline, State state) throws WTException {
		ArrayList v = new ArrayList();
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

			if (state != null) {
				qs.appendAnd();
				qs.appendWhere(new SearchCondition(WTPart.class, "state.state", "=", state.toString()),
						new int[] { index2 });
			}

			if (baseline != null) {
				int index3 = qs.addClassList(BaselineMember.class, false);
				qs.appendAnd();
				qs.appendWhere(new SearchCondition(WTPart.class, "thePersistInfo.theObjectIdentifier.id",
						BaselineMember.class, "roleBObjectRef.key.id"), new int[] { index2, index3 });
				qs.appendAnd();
				qs.appendWhere(new SearchCondition(BaselineMember.class, "roleAObjectRef.key.id", "=",
						baseline.getPersistInfo().getObjectIdentifier().getId()), new int[] { index3 });
			}

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
				v.add(oo);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new WTException();
		}
		return v;
	}

}
