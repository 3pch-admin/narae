/*
 * @(#) NumberCodeHelper.java  Create on 2005. 5. 10.
 * Copyright (c) e3ps. All rights reserverd
 */
package ext.narae.util.code.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import ext.narae.util.CommonUtil;
import ext.narae.util.code.NCodeNCodeLink;
import ext.narae.util.code.NumberCode;
import ext.narae.util.code.NumberCode2;
import ext.narae.util.code.NumberCodeType;
import wt.fc.BinaryLink;
import wt.fc.PagingQueryResult;
import wt.fc.PagingSessionHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.StringSearch;
import wt.util.WTException;

/**
 * 
 * @author Choi Seunghwan, skyprda@e3ps.com
 * @version 1.00, 2005. 5. 10.
 * @since 1.4
 */
public class NumberCodeHelper {
	public static NumberCodeHelper manager = new NumberCodeHelper();

	private NumberCodeHelper() {
	}

	public String getValue(String codeType, String code) throws Exception {
		NumberCode nc = getNumberCode(codeType, code);
		if (nc == null) {
			return null;
		}
		return nc.getName();
	}

	/**
	 * 특占쏙옙 占쌘듸옙 타占쌉울옙 占쌘드가 code占쏙옙 NumberCode占쏙옙 占쏙옙환
	 * 
	 * @param codeType
	 * @param code
	 * @return
	 */
	public NumberCode getNumberCode(String codeType, String code) {

		try {
			return getNumberCode(codeType, code, false);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public NumberCode2 getNumberCode2(String codeType, String code) {

		try {
			return getNumberCode2(codeType, code, false);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 특占쏙옙 占쌘듸옙 타占쌉울옙 占쌘드가 code占쏙옙 NumberCode占쏙옙 占쏙옙환
	 * 
	 * @param codeType
	 * @param code
	 * @return
	 */
	public NumberCode getNumberCode(String codeType, String code, boolean isTop) {
		if (code == null) {
			return null;
		}
		try {
			long longOid = 0;
			QuerySpec select = new QuerySpec(NumberCode.class);
			select.appendWhere(new SearchCondition(NumberCode.class, "codeType", "=", codeType), new int[] { 0 });
			select.appendAnd();
			select.appendWhere(new SearchCondition(NumberCode.class, "code", "=", code), new int[] { 0 });
			if (isTop) {
				select.appendAnd();
				select.appendWhere(new SearchCondition(NumberCode.class, "parentReference.key.id", "=", longOid),
						new int[] { 0 });
			}
			QueryResult result = PersistenceHelper.manager.find(select);
			while (result.hasMoreElements()) {
				return (NumberCode) result.nextElement();
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public NumberCode2 getNumberCode2(String codeType, String code, boolean isTop) {
		if (code == null) {
			return null;
		}
		try {
			long longOid = 0;
			QuerySpec select = new QuerySpec(NumberCode2.class);
			select.appendWhere(new SearchCondition(NumberCode2.class, "codeType", "=", codeType), new int[] { 0 });
			select.appendAnd();
			select.appendWhere(new SearchCondition(NumberCode2.class, "code", "=", code), new int[] { 0 });
			if (isTop) {
				select.appendAnd();
				select.appendWhere(new SearchCondition(NumberCode2.class, "parentReference.key.id", "=", longOid),
						new int[] { 0 });
			}
			System.out.println("query=" + select);
			QueryResult result = PersistenceHelper.manager.find(select);
			while (result.hasMoreElements()) {
				return (NumberCode2) result.nextElement();
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public NumberCode2 getNumberCode2(String codeType, String code, NumberCode2 parent) {
		if (code == null) {
			return null;
		}
		try {
			long longOid = 0;
			QuerySpec select = new QuerySpec(NumberCode2.class);
			select.appendWhere(new SearchCondition(NumberCode2.class, "codeType", "=", codeType), new int[] { 0 });
			select.appendAnd();
			select.appendWhere(new SearchCondition(NumberCode2.class, "code", "=", code), new int[] { 0 });
			if (null != parent) {
				select.appendAnd();
				select.appendWhere(new SearchCondition(NumberCode2.class, "parentReference.key.id", "=",
						CommonUtil.getOIDLongValue(parent)), new int[] { 0 });
			}
			QueryResult result = PersistenceHelper.manager.find(select);
			while (result.hasMoreElements()) {
				return (NumberCode2) result.nextElement();
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 특占쏙옙 占쌘듸옙 타占쌉울옙 占쌘드가 name占쏙옙 NumberCode占쏙옙 占쏙옙환
	 * 
	 * @param codeType
	 * @param name
	 * @return
	 */
	public NumberCode getNumberCodeName(String codeType, String name) {
		if (name == null) {
			return null;
		}
		try {
			QuerySpec select = new QuerySpec(NumberCode.class);
			select.appendWhere(new SearchCondition(NumberCode.class, "codeType", "=", codeType), new int[] { 0 });
			select.appendAnd();
			// select.appendWhere(new SearchCondition(NumberCode.class, "name", "=", name),
			// new int[] { 0 });
			StringSearch stringsearch = new StringSearch("name");
			stringsearch.setValue(name.trim());
			select.appendWhere(stringsearch.getSearchCondition(NumberCode.class), new int[] { 0 });

			QueryResult result = PersistenceHelper.manager.find(select);

			while (result.hasMoreElements()) {
				return (NumberCode) result.nextElement();
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public NumberCode getNumberCodeNameEquals(String codeType, String name) {
		if (name == null) {
			return null;
		}
		try {
			QuerySpec select = new QuerySpec(NumberCode.class);
			select.appendWhere(new SearchCondition(NumberCode.class, "codeType", "=", codeType), new int[] { 0 });
			select.appendAnd();
			select.appendWhere(new SearchCondition(NumberCode.class, "name", "=", name), new int[] { 0 });

			QueryResult result = PersistenceHelper.manager.find(select);

			while (result.hasMoreElements()) {
				return (NumberCode) result.nextElement();
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * DSCodeList.jsp 占쏙옙占쏙옙 占쏙옙占?
	 * 
	 * @param codeType
	 * @return
	 */
	public Vector getNumberCodeForQuery(String codeType) {
		try {
			QuerySpec select = new QuerySpec(NumberCode.class);
			select.appendWhere(new SearchCondition(NumberCode.class, "codeType", "=", codeType), new int[] { 0 });
			QueryResult result = PersistenceHelper.manager.find(select);
			Vector vec = new Vector();
			int i = 0;
			while (result.hasMoreElements()) {
				NumberCode tempCode = (NumberCode) result.nextElement();
				vec.add(i, tempCode);
				i++;
			}
			return vec;
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * NumberCode占쏙옙 HashMap 占쏙옙占쏙옙 占쏙옙환
	 * 
	 * @param codeType
	 * @return key:code,value:code_name
	 */
	public HashMap getNumberCode(String codeType) {
		HashMap map = new HashMap();
		try {
			QuerySpec select = new QuerySpec(NumberCode.class);
			select.appendWhere(new SearchCondition(NumberCode.class, "codeType", "=", codeType), new int[] { 0 });
			QueryResult result = PersistenceHelper.manager.find(select);

			NumberCode code = null;
			while (result.hasMoreElements()) {
				code = (NumberCode) result.nextElement();
				map.put(code.getCode(), code.getName());
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return map;
	}

	public QueryResult getQueryResult(String codeType, String sortType) {
		if (sortType == null)
			sortType = "name";
		try {
			QuerySpec spec = new QuerySpec(NumberCode.class);
			spec.appendWhere(new SearchCondition(NumberCode.class, "codeType", "=", codeType), new int[] { 0 });

			spec.appendAnd();
			spec.appendOpenParen();
			spec.appendWhere(new SearchCondition(NumberCode.class, "disabled", SearchCondition.IS_FALSE),
					new int[] { 0 });
			spec.appendOr();
			spec.appendWhere(new SearchCondition(NumberCode.class, "disabled", SearchCondition.IS_NULL),
					new int[] { 0 });
			spec.appendCloseParen();

			spec.appendOrderBy(new OrderBy(new ClassAttribute(NumberCode.class, sortType), false), new int[] { 0 });
			return PersistenceHelper.manager.find(spec);
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public QueryResult getQueryResult(String codeType, String sortType, boolean isValidate) {
		return getQueryResult(codeType, sortType, isValidate, false);
	}

	public QueryResult getQueryResult(String codeType, String sortType, boolean isValidate, boolean desc) {
		if (sortType == null)
			sortType = "name";

		try {
			QuerySpec spec = new QuerySpec(NumberCode.class);
			spec.appendWhere(new SearchCondition(NumberCode.class, "codeType", "=", codeType), new int[] { 0 });

			if (isValidate) {
				if (spec.getConditionCount() > 0)
					spec.appendAnd();

				spec.appendOpenParen();
				spec.appendWhere(new SearchCondition(NumberCode.class, "disabled", SearchCondition.IS_FALSE),
						new int[] { 0 });
				spec.appendOr();
				spec.appendWhere(new SearchCondition(NumberCode.class, "disabled", SearchCondition.IS_NULL),
						new int[] { 0 });
				spec.appendCloseParen();
			}

			// order by
			spec.appendOrderBy(new OrderBy(new ClassAttribute(NumberCode.class, sortType), desc), new int[] { 0 });

			return PersistenceHelper.manager.find(spec);
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 1Level NumberCode
	 * 
	 * @param type
	 * @return QueryResult
	 */
	public static ArrayList getTopNumberCode(NumberCodeType type) throws WTException {
		ArrayList list = new ArrayList();
		try {
			if (type == null)
				return list;

			HashMap map = new HashMap();
			map.put("type", type.toString());
			map.put("isParent", "false");

			QueryResult qr = PersistenceHelper.manager.find(getCodeQuerySpec(map));

			Object obj[] = null;
			while (qr.hasMoreElements()) {
				obj = (Object[]) qr.nextElement();
				list.add((NumberCode) obj[0]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static ArrayList getChildNumberCode(NumberCode numberCode) throws WTException {
		ArrayList list = new ArrayList();
		try {
			if (numberCode == null)
				return list;

			HashMap map = new HashMap();
			map.put("parent", numberCode);

			QueryResult qr = PersistenceHelper.manager.find(getCodeQuerySpec(map));
			Object obj[] = null;
			while (qr.hasMoreElements()) {
				obj = (Object[]) qr.nextElement();
				list.add((NumberCode) obj[0]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static ArrayList getNumberCode(HashMap map) throws WTException {
		ArrayList list = new ArrayList();
		try {
			QueryResult qr = PersistenceHelper.manager.find(getCodeQuerySpec(map));

			Object obj[] = null;
			while (qr.hasMoreElements()) {
				obj = (Object[]) qr.nextElement();
				list.add((NumberCode) obj[0]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static PagingQueryResult openPagingSession(HashMap map, int start, int size) throws WTException {
		PagingQueryResult result = null;
		try {
			result = openPagingSession(getCodeQuerySpec(map), start, size);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static PagingQueryResult openPagingSession(QuerySpec spec, int start, int size) throws WTException {
		PagingQueryResult result = null;
		try {
			result = PagingSessionHelper.openPagingSession(start, size, spec);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static PagingQueryResult fetchPagingSession(int start, int size, long sessionId) throws WTException {
		PagingQueryResult result = null;
		try {
			result = PagingSessionHelper.fetchPagingSession(start, size, sessionId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// 占쏙옙占쏙옙 占쌘듸옙
	public static ArrayList ancestorNumberCode(NumberCode child) throws WTException {
		ArrayList list = new ArrayList();
		try {
			searchAncestorNumberCode(child, list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static void searchAncestorNumberCode(NumberCode child, ArrayList list) throws WTException {
		try {
			if (list == null) {
				list = new ArrayList();
			}

			if (child.getParent() != null) {
				list.add(0, child.getParent());
				searchAncestorNumberCode(child.getParent(), list);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 占쏙옙占쏙옙 占쌘듸옙
	public static ArrayList descendantsNumberCode(NumberCode parent) throws WTException {
		return null;
	}

	public static NumberCode saveNumberCode(HashMap map) throws WTException {
		NumberCode numberCode = null;
		try {
			ReferenceFactory rf = new ReferenceFactory();

			String oid = map.get("oid") == null ? "" : ((String) map.get("oid")).trim();
			String code = map.get("code") == null ? "" : ((String) map.get("code")).trim();
			String name = map.get("name") == null ? "" : ((String) map.get("name")).trim();
			String description = map.get("description") == null ? "" : ((String) map.get("description")).trim();
			String type = map.get("type") == null ? "" : ((String) map.get("type")).trim();
			String parentOid = map.get("parentOid") == null ? "" : ((String) map.get("parentOid")).trim();

			if (oid.length() > 0) {
				numberCode = (NumberCode) rf.getReference(oid).getObject();
			}

			if (numberCode == null) {
				numberCode = NumberCode.newNumberCode();
			}

			if (code.length() > 0) {
				numberCode.setCode(code.toUpperCase());
			}

			if (name.length() > 0) {
				numberCode.setName(name);
			}

			numberCode.setDescription(description);

			if (oid.length() == 0 && type.length() > 0) {
				numberCode.setCodeType(NumberCodeType.toNumberCodeType(type));
			}
			/*
			 * NumberCode parent = null; if((parentOid.trim()).length() > 0) { parent =
			 * (NumberCode)rf.getReference(parentOid).getObject(); }
			 * numberCode.setParent(parent);
			 */
			numberCode = (NumberCode) PersistenceHelper.manager.save(numberCode);

			if (numberCode.getParent() != null) {
				QueryResult qr = PersistenceHelper.manager.navigate(numberCode, "parent", NCodeNCodeLink.class, false);
				while (qr.hasMoreElements()) {
					NCodeNCodeLink link = (NCodeNCodeLink) qr.nextElement();
					PersistenceHelper.manager.delete(link);
				}
			}

			numberCode = (NumberCode) PersistenceHelper.manager.refresh(numberCode);

			NumberCode parent = null;
			if ((parentOid.trim()).length() > 0) {
				parent = (NumberCode) rf.getReference(parentOid).getObject();
				numberCode.setParent(parent);
				numberCode = (NumberCode) PersistenceHelper.manager.save(numberCode);
				// NCodeNCodeLink link = NCodeNCodeLink.newNCodeNCodeLink(parent, numberCode);
				// link = (NCodeNCodeLink)PersistenceHelper.manager.save(link);
			}

			// ##### ERP 占쏙옙占쏙옙 - 占쏙옙체 占쏙옙占쏙옙 占쏙옙... SAP占쏙옙占쏙옙 占쏙옙체 占쏙옙占쏙옙 占쏙옙 占쏙옙占쏙옙.
			/*
			 * if("PROCESSDIVISIONCODE".equals(numberCode.getCodeType().toString())) {
			 * sendStdInfoToERP(null, numberCode.getCodeType().toString()); }
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
		return numberCode;
	}

	public static boolean deleteNumberCode(NumberCode code) throws WTException {
		try {
			ArrayList childs = getChildNumberCode(code);
			if (childs.size() > 0) {
				return false;
			}

			QueryResult qr = PersistenceHelper.manager.navigate(code, "ALL", BinaryLink.class, false);
			if (qr.size() == 0)
				PersistenceHelper.manager.delete(code);
			else {
				while (qr.hasMoreElements()) {
					Object obj = (Object) qr.nextElement();
					if (!(obj instanceof NCodeNCodeLink)) {
						return false;
					}
				}
			}

			PersistenceHelper.manager.delete(code);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean checkNumberCode(HashMap map) throws WTException {
		try {
			String code = (String) map.get("code");
			String type = (String) map.get("type");
			String parentOid = (String) map.get("parentOid");

			if (code == null)
				code = "";
			if (type == null)
				type = "";
			if (parentOid == null)
				parentOid = "";

			if (code.length() == 0)
				return false;

			if (type.length() == 0)
				return false;

			HashMap smap = new HashMap();
			smap.put("code", code);
			smap.put("type", type);
			if (parentOid.length() > 0) {
				ReferenceFactory rf = new ReferenceFactory();
				NumberCode parent = (NumberCode) rf.getReference(parentOid).getObject();
				smap.put("parent", parent);
			}

			QuerySpec qs = getCodeQuerySpec(smap);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			if (qr.hasMoreElements()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/*
	 * public static boolean sendStdInfoToERP(NumberCode code, String type) throws
	 * WTException { try { ArrayList list = getSubCodeTree(code, type); if(code !=
	 * null) { list.add(0, code); }
	 * 
	 * ArrayList r_list = new ArrayList(); Object objArr[] = null;//[0]:level,
	 * [1]:code, [2]:name
	 * 
	 * HashMap c_Map = new HashMap(); HashMap l_Map = new HashMap();
	 * 
	 * String tCode = null; Integer lvl = null;
	 * 
	 * NumberCode child = null; NumberCode parent = null; for(int i = 0; i <
	 * list.size(); i++) { child = (NumberCode)list.get(i); parent =
	 * child.getParent();
	 * 
	 * if(parent == null) { lvl = new Integer(0); tCode = child.getCode(); } else {
	 * lvl = (Integer)l_Map.get(parent.getCode()); tCode = (String)c_Map.get(lvl);
	 * tCode += child.getCode();
	 * 
	 * lvl = new Integer(lvl.intValue()+1); }
	 * 
	 * objArr = new Object[3]; objArr[0] = String.valueOf(lvl.intValue()); objArr[1]
	 * = tCode; objArr[2] = child.getName(); r_list.add(objArr);
	 * 
	 * l_Map.put(child.getCode(), lvl); c_Map.put(lvl, tCode); }
	 * 
	 * //System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"); //for(int i = 0; i <
	 * r_list.size(); i++) { //objArr = (Object[])r_list.get(i);
	 * //System.out.println((String)objArr[0] + '\t' + (String)objArr[1] + '\t' +
	 * (String)objArr[2]); //}
	 * //System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
	 * 
	 * StdInfoInterface iter = new StdInfoInterface(); boolean flag =
	 * iter.sendProcessInterface(r_list); iter.close();
	 * 
	 * return flag; } catch(Exception e) { e.printStackTrace(); return false; } }
	 * public static boolean sendStdInfoToERPOne(NumberCode code, String type)
	 * throws WTException { try { ArrayList list = getSubCodeTree(code, type);
	 * if(code != null) { list.add(0, code); }
	 * 
	 * ArrayList r_list = new ArrayList(); Object objArr[] = null;//[0]:level,
	 * [1]:code, [2]:name
	 * 
	 * HashMap c_Map = new HashMap(); HashMap l_Map = new HashMap();
	 * 
	 * String tCode = null; Integer lvl = null;
	 * 
	 * NumberCode child = null; NumberCode parent = null; for(int i = 0; i <
	 * list.size(); i++) { child = (NumberCode)list.get(i); parent =
	 * child.getParent();
	 * 
	 * if(parent == null) { lvl = new Integer(0); tCode = child.getCode(); } else {
	 * lvl = (Integer)l_Map.get(parent.getCode()); tCode = (String)c_Map.get(lvl);
	 * tCode += child.getCode();
	 * 
	 * lvl = new Integer(lvl.intValue()+1); }
	 * 
	 * objArr = new Object[3]; objArr[0] = String.valueOf(lvl.intValue()); objArr[1]
	 * = tCode; objArr[2] = child.getName(); r_list.add(objArr);
	 * 
	 * l_Map.put(child.getCode(), lvl); c_Map.put(lvl, tCode); }
	 * 
	 * //System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"); //for(int i = 0; i <
	 * r_list.size(); i++) { //objArr = (Object[])r_list.get(i);
	 * //System.out.println((String)objArr[0] + '\t' + (String)objArr[1] + '\t' +
	 * (String)objArr[2]); //}
	 * //System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
	 * 
	 * StdInfoInterface iter = new StdInfoInterface(); boolean flag =
	 * iter.sendProcessInterface(r_list); iter.close();
	 * 
	 * return flag; } catch(Exception e) { e.printStackTrace(); return false; } }
	 */
	public static ArrayList getSubCodeTree(NumberCode parent, String type) throws WTException {
		ArrayList treeList = new ArrayList();
		try {
			ArrayList list = null;
			if (parent == null) {
				list = NumberCodeHelper.getTopNumberCode(NumberCodeType.toNumberCodeType(type));
			} else {
				list = NumberCodeHelper.getChildNumberCode(parent);
			}

			if (list != null && list.size() > 0) {
				NumberCode numberCode = null;
				for (int i = 0; i < list.size(); i++) {
					numberCode = (NumberCode) list.get(i);

					makeCodeTree(numberCode, treeList);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return treeList;
	}

	public static void makeCodeTree(NumberCode parent, ArrayList list) throws WTException {
		try {
			if (list == null) {
				list = new ArrayList();
			}

			list.add(parent);

			ArrayList childs = NumberCodeHelper.getChildNumberCode(parent);
			NumberCode child = null;
			for (int i = 0; i < childs.size(); i++) {
				child = (NumberCode) childs.get(i);

				makeCodeTree(child, list);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static QuerySpec getCodeQuerySpec2(HashMap map) throws WTException {

		QuerySpec qs = null;
		try {
			String type = (String) map.get("type");
			String name = (String) map.get("name");
			String code = (String) map.get("code");
			String description = (String) map.get("description");
			NumberCode parent = (NumberCode) map.get("parent");
			String isParent = (String) map.get("isParent");
			qs = new QuerySpec();
			int i = qs.addClassList(NumberCode2.class, true);

			SearchCondition sc = null;
			if (type != null && type.length() > 0) {
				sc = new SearchCondition(NumberCode2.class, "codeType", SearchCondition.EQUAL,
						NumberCodeType.toNumberCodeType(type));
				qs.appendWhere(sc, new int[] { i });
			}

			if (name != null && name.length() > 0) {
				if (qs.getConditionCount() > 0)
					qs.appendAnd();

				sc = new SearchCondition(NumberCode2.class, "name", SearchCondition.LIKE, "%" + name + "%");
				qs.appendWhere(sc, new int[] { i });
			}

			if (code != null && code.length() > 0) {
				if (qs.getConditionCount() > 0)
					qs.appendAnd();

				sc = new SearchCondition(NumberCode2.class, "code", SearchCondition.EQUAL, code);
				qs.appendWhere(sc, new int[] { i });
			}

			if (description != null && description.length() > 0) {
				if (qs.getConditionCount() > 0)
					qs.appendAnd();

				sc = new SearchCondition(NumberCode2.class, "description", SearchCondition.LIKE,
						"%" + description + "%");
				qs.appendWhere(sc, new int[] { i });
			}

			if (isParent != null && "false".equals(isParent.toLowerCase())) {
				if (qs.getConditionCount() > 0)
					qs.appendAnd();

				sc = new SearchCondition(NumberCode2.class, "parentReference.key.classname", true);
				qs.appendWhere(sc, new int[] { i });
			} else {
				if (parent != null) {
					if (qs.getConditionCount() > 0)
						qs.appendAnd();

					sc = new SearchCondition(NumberCode2.class, "parentReference.key.id", SearchCondition.EQUAL,
							parent.getPersistInfo().getObjectIdentifier().getId());
					qs.appendWhere(sc, new int[] { i });
				}
			}

			ClassAttribute ca = new ClassAttribute(NumberCode2.class, "code");
			ca.setColumnAlias("wtsort" + String.valueOf(0));
			qs.appendSelect(ca, new int[] { i }, false);
			OrderBy orderby = new OrderBy(ca, false, null);
			qs.appendOrderBy(orderby, new int[] { i });
		} catch (Exception e) {
			e.printStackTrace();
		}
		return qs;
	}

	public static QuerySpec getCodeQuerySpec(HashMap map) throws WTException {

		QuerySpec qs = null;
		try {
			String type = (String) map.get("type");
			String name = (String) map.get("name");
			String code = (String) map.get("code");
			String description = (String) map.get("description");
			NumberCode parent = (NumberCode) map.get("parent");
			String isParent = (String) map.get("isParent");
			qs = new QuerySpec();
			int i = qs.addClassList(NumberCode.class, true);

			SearchCondition sc = null;
			if (type != null && type.length() > 0) {
				sc = new SearchCondition(NumberCode.class, "codeType", SearchCondition.EQUAL,
						NumberCodeType.toNumberCodeType(type));
				qs.appendWhere(sc, new int[] { i });
			}

			if (name != null && name.length() > 0) {
				if (qs.getConditionCount() > 0)
					qs.appendAnd();

				sc = new SearchCondition(NumberCode.class, "name", SearchCondition.LIKE, "%" + name + "%");
				qs.appendWhere(sc, new int[] { i });
			}

			if (code != null && code.length() > 0) {
				if (qs.getConditionCount() > 0)
					qs.appendAnd();

				sc = new SearchCondition(NumberCode.class, "code", SearchCondition.EQUAL, code);
				qs.appendWhere(sc, new int[] { i });
			}

			if (description != null && description.length() > 0) {
				if (qs.getConditionCount() > 0)
					qs.appendAnd();

				sc = new SearchCondition(NumberCode.class, "description", SearchCondition.LIKE,
						"%" + description + "%");
				qs.appendWhere(sc, new int[] { i });
			}

			if (isParent != null && "false".equals(isParent.toLowerCase())) {
				if (qs.getConditionCount() > 0)
					qs.appendAnd();

				sc = new SearchCondition(NumberCode.class, "parentReference.key.classname", true);
				qs.appendWhere(sc, new int[] { i });
			} else {
				if (parent != null) {
					if (qs.getConditionCount() > 0)
						qs.appendAnd();

					sc = new SearchCondition(NumberCode.class, "parentReference.key.id", SearchCondition.EQUAL,
							parent.getPersistInfo().getObjectIdentifier().getId());
					qs.appendWhere(sc, new int[] { i });
				}
			}

			ClassAttribute ca = new ClassAttribute(NumberCode.class, "code");
			ca.setColumnAlias("wtsort" + String.valueOf(0));
			qs.appendSelect(ca, new int[] { i }, false);
			OrderBy orderby = new OrderBy(ca, false, null);
			qs.appendOrderBy(orderby, new int[] { i });
		} catch (Exception e) {
			e.printStackTrace();
		}
		return qs;
	}

	public ArrayList<NumberCode2> getNumberCode2List(String codeType, String code) {
		ArrayList<NumberCode2> list = new ArrayList<NumberCode2>();
		if (code == null) {
			return list;
		}

		try {
			QuerySpec select = new QuerySpec(NumberCode2.class);
			select.appendWhere(new SearchCondition(NumberCode2.class, "codeType", "=", codeType), new int[] { 0 });
			select.appendAnd();
			select.appendWhere(new SearchCondition(NumberCode2.class, "code", "=", code), new int[] { 0 });

			QueryResult result = PersistenceHelper.manager.find(select);
			while (result.hasMoreElements()) {
				list.add((NumberCode2) result.nextElement());
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public NumberCode getNumberCode(String codeType, String code, String name) {
		if (code == null) {
			return null;
		}
		try {
			long longOid = 0;
			QuerySpec select = new QuerySpec(NumberCode.class);
			select.appendWhere(new SearchCondition(NumberCode.class, "codeType", "=", codeType), new int[] { 0 });
			select.appendAnd();
			select.appendWhere(new SearchCondition(NumberCode.class, "code", "=", code), new int[] { 0 });
			select.appendAnd();
			select.appendWhere(new SearchCondition(NumberCode.class, NumberCode.NAME, "=", name), new int[] { 0 });
			QueryResult result = PersistenceHelper.manager.find(select);
			while (result.hasMoreElements()) {
				return (NumberCode) result.nextElement();
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public NumberCode2 getNumberCode2(String codeType, String code, String name) {
		if (code == null) {
			return null;
		}
		try {
			long longOid = 0;
			QuerySpec select = new QuerySpec(NumberCode2.class);
			select.appendWhere(new SearchCondition(NumberCode2.class, "codeType", "=", codeType), new int[] { 0 });
			select.appendAnd();
			select.appendWhere(new SearchCondition(NumberCode2.class, "code", "=", code), new int[] { 0 });
			select.appendAnd();
			select.appendWhere(new SearchCondition(NumberCode2.class, NumberCode2.NAME, "=", name), new int[] { 0 });
			QueryResult result = PersistenceHelper.manager.find(select);
			while (result.hasMoreElements()) {
				return (NumberCode2) result.nextElement();
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}

/*******************************************************************************
 * $Log: NumberCodeHelper.java,v $ Revision 1.3 2011/06/12 03:51:04 tsuam
 *
 * Committed on the Free edition of March Hare Software CVSNT Server. Upgrade to
 * CVS Suite for more features and support: http://march-hare.com/cvsnt/
 *
 * Revision 1.2 2011/05/23 05:51:11 thhwang
 *
 * Committed on the Free edition of March Hare Software CVSNT Server. Upgrade to
 * CVS Suite for more features and support: http://march-hare.com/cvsnt/
 *
 * Revision 1.1 2011/04/21 03:50:59 thhwang *** empty log message ***
 *
 * Revision 1.1 2011/03/08 01:50:03 thhwang 占쏙옙占? Committed on the Free edition
 * of March Hare Software CVSNT Server. Upgrade to CVS Suite for more features
 * and support: http://march-hare.com/cvsnt/
 *
 * Revision 1.5 2010/09/28 00:26:40 yhjang
 *
 * Committed on the Free edition of March Hare Software CVSNT Server. Upgrade to
 * CVS Suite for more features and support: http://march-hare.com/cvsnt/
 *
 * Revision 1.4 2010/07/29 02:05:42 yhjang
 *
 * Committed on the Free edition of March Hare Software CVSNT Server. Upgrade to
 * CVS Suite for more features and support: http://march-hare.com/cvsnt/
 *
 * Revision 1.3 2010/07/19 07:57:57 yhjang
 *
 * Committed on the Free edition of March Hare Software CVSNT Server. Upgrade to
 * CVS Suite for more features and support: http://march-hare.com/cvsnt/
 *
 * Revision 1.2 2010/06/07 02:52:53 hyun
 *
 * Committed on the Free edition of March Hare Software CVSNT Server. Upgrade to
 * CVS Suite for more features and support: http://march-hare.com/cvsnt/
 *
 * Revision 1.2 2009/08/26 17:15:16 smkim
 *
 * Committed on the Free edition of March Hare Software CVSNT Server. Upgrade to
 * CVS Suite for more features and support: http://march-hare.com/cvsnt/
 *
 * Revision 1.1.1.1 2009/07/01 07:07:43 administrator no message
 *
 * Committed on the Free edition of March Hare Software CVSNT Server. Upgrade to
 * CVS Suite for more features and support: http://march-hare.com/cvsnt/
 *
 * Revision 1.1 2009/02/25 01:26:32 smkim 占쏙옙占쏙옙 占쌜쇽옙 Committed on the Free
 * edition of March Hare Software CVSNT Server. Upgrade to CVS Suite for more
 * features and support: http://march-hare.com/cvsnt/
 *
 * Revision 1.10 2008/12/23 08:44:35 jspark *** empty log message ***
 *
 * Revision 1.9 2008/11/13 10:31:26 jspark *** empty log message ***
 *
 * Revision 1.8 2008/10/13 10:26:21 smkim *** empty log message ***
 *
 * Revision 1.7 2008/09/05 02:00:13 jspark *** empty log message ***
 *
 * Revision 1.6 2008/08/21 08:05:02 jspark *** empty log message ***
 *
 * Revision 1.5 2008/07/29 01:22:55 jspark *** empty log message ***
 *
 * Revision 1.4 2008/04/15 11:14:09 jspark *** empty log message ***
 *
 * Revision 1.3 2008/03/17 00:32:38 jspark *** empty log message ***
 *
 * Revision 1.2 2008/03/13 12:33:24 jspark *** empty log message ***
 *
 * Revision 1.1 2008/01/29 06:25:03 sjhan 占쌍쇽옙 占썩본 占쏙옙키占쏙옙 占쏙옙占쏙옙占쌜억옙 占싹뤄옙
 *
 * Revision 1.1 2008/01/23 09:51:53 sjhan e3ps package 占쏙옙占쏙옙 占싹료본 jsp 占쌀쏙옙占쏙옙
 * 확占쏙옙 占쏙옙 占쏙옙占쏙옙占쌔억옙 占쏙옙 占십울옙 占쏙옙占쏙옙
 *
 * Revision 1.1 2007/09/27 01:43:28 khchoi [20070927 占쌍곤옙占쏙옙] *.java Source
 * 占쏙옙占쏙옙
 *
 * Revision 1.1.1.1 2007/04/10 06:40:18 administrator no message
 *
 * Revision 1.1.1.1 2007/02/14 07:53:56 administrator no message
 *
 * Revision 1.3 2006/09/19 06:19:36 shchoi getQueryResult 占쌩곤옙
 *
 * Revision 1.2 2006/06/27 05:16:15 shchoi 占쌘듸옙 占쏙옙占쏙옙
 *
 * Revision 1.1 2006/05/09 02:35:03 shchoi *** empty log message ***
 *
 * Revision 1.1 2006/05/09 01:23:59 shchoi *** empty log message ***
 *
 * Revision 1.3 2006/03/15 05:09:07 shchoi *** empty log message ***
 *
 * Revision 1.2 2006/02/24 02:29:14 khchoi *** empty log message ***
 *
 * Revision 1.1 2005/12/09 12:20:32 shchoi *** empty log message ***
 *
 * Revision 1.4 2005/12/06 12:25:15 shchoi null 체크 /* Revision 1.3 2005/12/06
 * 01:04:54 shchoi /* getNumberCode(String codeType) 占쌩곤옙 /
 ******************************************************************************/
