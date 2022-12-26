/**
 * @(#) SearchUtil.java Copyright (c) e3ps. All rights reserverd
 * @version 1.00
 * @since jdk 1.4.02
 * @createdate 2005. 4. 7..
 * @author Cho Sung Ok, jerred@e3ps.com
 * @desc setOrderBy 추가(Choi Seunghwan,2005.10.06)
 */

package ext.narae.util.query;

import java.rmi.RemoteException;
import java.util.Locale;

import ext.narae.util.CommonUtil;
import ext.narae.util.StringUtil;
import wt.access.NotAuthorizedException;
import wt.clients.folder.FolderTaskLogic;
import wt.fc.Persistable;
import wt.folder.Folder;
import wt.iba.definition.IBADefinitionException;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.StringValue;
import wt.introspection.ClassInfo;
import wt.introspection.LinkInfo;
import wt.introspection.WTIntrospectionException;
import wt.introspection.WTIntrospector;
import wt.pds.ClassJoinCondition;
import wt.pds.QuerySpecStatementBuilder;
import wt.query.AttributeSearchSpecification;
import wt.query.ClassAttribute;
import wt.query.ClassTableExpression;
import wt.query.ClassViewExpression;
import wt.query.ColumnExpression;
import wt.query.ConstantExpression;
import wt.query.ExistsExpression;
import wt.query.NegatedExpression;
import wt.query.OrderBy;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SQLFunction;
import wt.query.SearchCondition;
import wt.query.SearchTask;
import wt.query.StringSearch;
import wt.query.TableExpression;
import wt.util.SortedEnumeration;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.Iterated;

public class SearchUtil {
	public static QuerySpec makeJoinQuerySpec(Class target, Class sideTarget, Class linkClass, String targetRole,
			String sideTargeRole, Persistable sideTargetPersistable) throws Exception {
		QuerySpec query = new QuerySpec();
		int targetIndex = query.appendClassList(target, true);
		int sideTagetIndex = query.appendClassList(sideTarget, false);
		int linkIndex = query.appendClassList(linkClass, true);

		query.appendJoin(linkIndex, targetRole, targetIndex);
		query.appendJoin(linkIndex, sideTargeRole, sideTagetIndex);

		long lperistable = CommonUtil.getOIDLongValue(sideTargetPersistable);
		query.appendWhere(new SearchCondition(sideTarget, "thePersistInfo.theObjectIdentifier.id", "=", lperistable),
				sideTagetIndex);

		if (target.equals(Iterated.class)) {
			query.appendAnd();
			query.appendWhere(new SearchCondition(target, "iterationInfo.latest", "TRUE"), targetIndex);
		}
		return query;
	}

	/**
	 * appendLinkJoin
	 */
	public static QuerySpec appendLinkJoin(QuerySpec spec, int roleAClassPos, int roleBClassPos, String linkClassName,
			boolean isLinkOuterJoin, boolean isTargetOuterJoin)
			throws ClassNotFoundException, WTPropertyVetoException, WTIntrospectionException {
		QuerySpecStatementBuilder builder = (QuerySpecStatementBuilder) spec.getStatementBuilder();
		LinkInfo linkinfo = WTIntrospector.getLinkInfo(linkClassName);
		Class linkInfoClass = linkinfo.getBusinessClass();
		int k = spec.appendFrom(((wt.query.TableExpression) (linkinfo.isConcrete()
				? ((wt.query.TableExpression) (new ClassViewExpression(linkInfoClass)))
				: ((wt.query.TableExpression) (new ClassTableExpression(linkInfoClass))))));

		ClassJoinCondition classjoincondition = new ClassJoinCondition(k, linkinfo.getRoleA().getName(), roleAClassPos);
		classjoincondition.setLinkOuterJoin(isLinkOuterJoin);
		classjoincondition.setTargetOuterJoin(isTargetOuterJoin);
		builder.appendJoin(classjoincondition);
		classjoincondition = new ClassJoinCondition(k, linkinfo.getRoleB().getName(), roleBClassPos);
		classjoincondition.setLinkOuterJoin(isTargetOuterJoin);
		classjoincondition.setTargetOuterJoin(isLinkOuterJoin);
		builder.appendJoin(classjoincondition);
		QuerySpec returnSpec = builder.getQuerySpec();
		return returnSpec;
	}

	/**
	 * appendLinkRoleJoin
	 */
	public static QuerySpec appendLinkRoleJoin(QuerySpec spec, int targetClassPos, int linkClassPos, String rolename)
			throws ClassNotFoundException, WTPropertyVetoException {
		QuerySpecStatementBuilder builder = (QuerySpecStatementBuilder) spec.getStatementBuilder();
		ClassJoinCondition classjoincondition = new ClassJoinCondition(linkClassPos, rolename, targetClassPos);
		classjoincondition.setLinkOuterJoin(false);
		classjoincondition.setTargetOuterJoin(false);
		builder.appendJoin(classjoincondition);
		QuerySpec returnSpec = builder.getQuerySpec();
		return returnSpec;
	}

	/**
	 * getSearchCondition param count: 4
	 */
	public static SearchCondition getSearchCondition(Class targetClass, String fieldName, String searchValue)
			throws WTException {
		return getSearchCondition(targetClass, WTIntrospector.getClassInfo(targetClass), fieldName, searchValue,
				Locale.KOREA);
	}

	/**
	 * getSearchCondition param count: 5
	 */
	public static SearchCondition getSearchCondition(Class class1, ClassInfo classinfo, String s, String s1,
			Locale locale) throws WTException {
		Object obj = SearchTask.getSearchSpecification(s, s1, classinfo, locale);
		String s2 = ((AttributeSearchSpecification) obj).getSearchExpression();

		if (s2.equals(" LIKE ")) {
			try {
				String s3 = (String) ((AttributeSearchSpecification) obj).getValue();
				if (!AttributeSearchSpecification.areThereDBWildCards(s3)) {
					obj = new StringSearch(s, "=");
					((AttributeSearchSpecification) obj).setValue(s1);
				}
			} catch (WTPropertyVetoException wtpropertyvetoexception) {
				Object aobj[] = { class1.toString() + "->" + s };
				throw new WTException(wtpropertyvetoexception, "wt.query.queryResource", "26", aobj);
			}
		}

		try {
			SearchCondition searchcondition = ((AttributeSearchSpecification) obj).getSearchCondition(class1);
			if (searchcondition.getOperator().equals(" LIKE "))
				searchcondition.setOption("escape '/'");
			return searchcondition;
		} catch (WTPropertyVetoException wtpropertyvetoexception1) {
			Object aobj1[] = { class1.toString() + "->" + s };
			throw new WTException(wtpropertyvetoexception1, "wt.query.queryResource", "26", aobj1);
		} catch (QueryException queryexception) {
			Object aobj2[] = { class1.toString() + "->" + s };
			throw new WTException(queryexception, "wt.query.queryResource", "26", aobj2);
		}
	}

	/**
	 * 정렬
	 * 
	 * @param spec
	 * @param sortingClass
	 * @param tableNo
	 * @param field
	 * @param sortingFlag  Sorting 방법 ( true : 내림차순, false : 오름차순 )
	 * @throws Exception
	 */
	public static void setOrderBy(QuerySpec spec, Class sortingClass, int tableNo, String field, boolean sortingFlag)
			throws WTPropertyVetoException, QueryException, WTIntrospectionException {
		// ClassAttribute classattribute = new ClassAttribute(sortingClass,
		// field);
		// classattribute.setColumnAlias("wtsort" + String.valueOf(0));
		// int[] fieldNoArr = { fieldNo };
		// spec.appendSelect(classattribute, fieldNoArr, false);
		// OrderBy orderby = new OrderBy(classattribute, sortingFlag, null);
		// spec.appendOrderBy(orderby, fieldNoArr);
		spec.appendOrderBy(new OrderBy(new ClassAttribute(sortingClass, field), sortingFlag), new int[] { tableNo });
	}

	/**
	 * 정렬
	 * 
	 * @param spec
	 * @param sortingClass
	 * @param tableNo
	 * @param field        정렬할 필드
	 * @param sort         desc(default)|asc
	 * @throws WTPropertyVetoException
	 * @throws QueryException
	 * @throws WTIntrospectionException
	 */
	public static void setOrderBy(QuerySpec spec, Class sortingClass, int tableNo, String field, String sort)
			throws WTPropertyVetoException, QueryException, WTIntrospectionException {
		boolean isDescSort = (sort == null || "desc".equalsIgnoreCase(sort)) ? true : false;
		spec.appendOrderBy(new OrderBy(new ClassAttribute(sortingClass, field), isDescSort), new int[] { tableNo });
	}

	/**
	 * Alias를 지정하여 정렬한다.
	 * 
	 * @param spec
	 * @param sortingClass
	 * @param sortField
	 * @param tableNo
	 * @param sortingFlag  Sorting 방법 ( true : 내림차순, false : 오름차순 )
	 * @throws Exception
	 */
	public static void setOrderBy(QuerySpec spec, Class sortingClass, int tableNo, String field, String aliasName,
			boolean sortingFlag) throws WTPropertyVetoException, QueryException, WTIntrospectionException {
		ClassAttribute classattribute = new ClassAttribute(sortingClass, field);
		classattribute.setColumnAlias(aliasName + String.valueOf(0));
		int[] fieldNoArr = { tableNo };
		spec.appendSelect(classattribute, fieldNoArr, false);
		OrderBy orderby = new OrderBy(classattribute, sortingFlag, null);
		spec.appendOrderBy(orderby, tableNo);

		// spec.appendOrderBy(new OrderBy(new ClassAttribute(sortingClass,
		// field), sortingFlag), new int[] { fieldNo });

	}

	public static void searchInFolder(Folder folder, QuerySpec spec, Class targetClass, int targetClassPos,
			boolean isFirst) throws Exception {
		if (!isFirst)
			spec.appendOr();

		long longFolder = CommonUtil.getOIDLongValue(folder);
		spec.appendWhere(
				new SearchCondition(targetClass, "thePersistInfo.theObjectIdentifier.id", " LIKE ", longFolder),
				targetClassPos);
		SortedEnumeration en = FolderTaskLogic.getSubFolders(folder);
		while (en.hasMoreElements()) {
			Folder subFolder = (Folder) en.nextElement();
			searchInFolder(subFolder, spec, targetClass, targetClassPos, false);
		}
	}

	public static void searchInOnlyFolder(Folder folder, QuerySpec spec, Class targetClass, int targetClassPos)
			throws Exception {
		if (spec.getConditionCount() > 0)
			spec.appendAnd();
		long longFolder = CommonUtil.getOIDLongValue(folder);
		spec.appendWhere(
				new SearchCondition(targetClass, "thePersistInfo.theObjectIdentifier.id", " LIKE ", longFolder),
				targetClassPos);
	}

	/**
	 * Outer Join QuerySpec
	 * 
	 * @return : Vector
	 * @author : PTC KOREA Yang Kyu
	 * @since : 2004.04
	 */
	public static QuerySpec makeOuterJoinQuerySpec(Class targetClass, Class linkClass, boolean isRole_A)
			throws Exception {
		QuerySpec mainQuery = new QuerySpec();

		int classIndex = mainQuery.appendClassList(targetClass, true);

		QuerySpec subQuery = new QuerySpec();
		subQuery.getFromClause().setAliasPrefix("B");
		int linkIndex = subQuery.appendClassList(linkClass, false);
		subQuery.appendSelectAttribute(WTAttributeNameIfc.ID_NAME, linkIndex, true);

		TableExpression[] tables = new TableExpression[2];
		String[] aliases = new String[2];
		tables[0] = mainQuery.getFromClause().getTableExpressionAt(classIndex);
		aliases[0] = mainQuery.getFromClause().getAliasAt(classIndex);
		tables[1] = subQuery.getFromClause().getTableExpressionAt(linkIndex);
		aliases[1] = subQuery.getFromClause().getAliasAt(linkIndex);

		SearchCondition correlatedJoin = null;

		if (isRole_A) {
			correlatedJoin = new SearchCondition(targetClass, WTAttributeNameIfc.ID_NAME, linkClass,
					WTAttributeNameIfc.ROLEA_OBJECT_ID);
		} else {
			correlatedJoin = new SearchCondition(targetClass, WTAttributeNameIfc.ID_NAME, linkClass,
					WTAttributeNameIfc.ROLEB_OBJECT_ID);
		}
		subQuery.appendWhere(correlatedJoin, tables, aliases);
		mainQuery.appendWhere(new NegatedExpression(new ExistsExpression(subQuery)));

		// outer join query
		if (validatedIteration(targetClass)) {
			mainQuery.appendAnd();
			mainQuery.appendWhere(new SearchCondition(targetClass, "iterationInfo.latest", "TRUE"), classIndex);
		}

		return mainQuery;
	}

	private static boolean validatedIteration(Class targetClass) throws Exception {
		try {
			Iterated iterated = (Iterated) targetClass.newInstance();
			return true;
		} catch (ClassCastException e) {
			return false;
		}
	}

	/**
	 * QuerySpec에 fromClass의 특정 필드에 LIKE WHERE을 추가한다.
	 * 
	 * @param querySpec
	 * @param fromClass
	 * @param fieldName  검색 필드이름
	 * @param fieldValue 검색값
	 * @param idx        fromClass의 index
	 * @throws QueryException
	 */
	public static void appendLIKE(QuerySpec querySpec, Class fromClass, String fieldName, String fieldValue, int idx)
			throws QueryException {
		if (StringUtil.checkString(fieldValue)) {
			if (querySpec.getConditionCount() > 0)
				querySpec.appendAnd();
			querySpec.appendWhere(new SearchCondition(fromClass, fieldName, " LIKE ", "%" + fieldValue + "%"),
					new int[] { idx });
		}
	}

	/**
	 * QuerySpec에 fromClass의 특정 필드에 EQUAL WHERE절을 추가한다.
	 * 
	 * @param querySpec
	 * @param fromClass
	 * @param fieldName  검색 필드이름
	 * @param fieldValue 검색값
	 * @param idx        fromClass의 index
	 * @throws QueryException
	 */
	public static void appendEQUAL(QuerySpec querySpec, Class fromClass, String fieldName, String fieldValue, int idx)
			throws QueryException {
		if (StringUtil.checkString(fieldValue)) {
			if (querySpec.getConditionCount() > 0)
				querySpec.appendAnd();
			querySpec.appendWhere(new SearchCondition(fromClass, fieldName, "=", fieldValue), new int[] { idx });
		}
	}

	/**
	 * QuerySpec에 fromClass의 특정 필드에 EQUAL WHERE절을 추가한다.
	 * 
	 * @param querySpec
	 * @param fromClass
	 * @param fieldName  검색 필드이름
	 * @param fieldValue 검색값
	 * @param idx        fromClass의 index
	 * @throws QueryException
	 */
	public static void appendEQUAL(QuerySpec querySpec, Class fromClass, String fieldName, long fieldValue, int idx)
			throws QueryException {
		if (querySpec.getConditionCount() > 0)
			querySpec.appendAnd();
		querySpec.appendWhere(new SearchCondition(fromClass, fieldName, "=", fieldValue), new int[] { idx });
	}

	/**
	 * 대소분자 구분없이 쿼리를 할수 있도로 upper나 lower로 처리.
	 * 
	 * (targetClass, column이름, 값(value), UPPER/LOWER, 검색조건) 예)
	 * strCaseCondition(targetClass.class, columnKey, 검색값, "UPPER",
	 * SearchCondition.LIKE)
	 * 
	 * @author : JAI-SIK PARK jspark@e3ps.com
	 * @since : 2005.06.13
	 */
	public static SearchCondition getSCSQLFunction(Class targetClass, String key, String keyValue, String strCase,
			String condition) {
		SearchCondition sc = null;
		try {
			ClassAttribute attribute = new ClassAttribute(targetClass, key);
			SQLFunction function = SQLFunction.newSQLFunction(strCase);
			function.setArgumentAt((ColumnExpression) attribute, 0);
			ConstantExpression expression = new ConstantExpression(keyValue);
			sc = new SearchCondition(function, condition, expression);
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sc;
	}

	/**
	 * 최신버전 조건 쿼리
	 * 
	 * @param _query
	 * @param _target
	 * @param _idx
	 * @throws IBADefinitionException
	 * @throws NotAuthorizedException
	 * @throws RemoteException
	 * @throws WTException
	 * @throws QueryException
	 * @throws WTPropertyVetoException
	 */
	public static void addLastVersionCondition(QuerySpec _query, Class _target, int _idx) throws IBADefinitionException,
			NotAuthorizedException, RemoteException, WTException, QueryException, WTPropertyVetoException {
		AttributeDefDefaultView aview = IBADefinitionHelper.service
				.getAttributeDefDefaultViewByPath("LatestVersionFlag");
		if (aview != null) {
			if (_query.getConditionCount() > 0)
				_query.appendAnd();

			int idx = _query.appendClassList(StringValue.class, false);
			SearchCondition sc = new SearchCondition(
					new ClassAttribute(StringValue.class, "theIBAHolderReference.key.id"), "=",
					new ClassAttribute(_target, "thePersistInfo.theObjectIdentifier.id"));
			sc.setFromIndicies(new int[] { idx, _idx }, 0);
			sc.setOuterJoin(0);
			_query.appendWhere(sc, new int[] { idx, _idx });
			_query.appendAnd();
			sc = new SearchCondition(StringValue.class, "definitionReference.hierarchyID", "=", aview.getHierarchyID());
			_query.appendWhere(sc, new int[] { idx });
			_query.appendAnd();
			sc = new SearchCondition(StringValue.class, "value2", "=", "true");
			_query.appendWhere(sc, new int[] { idx });
		}
	}
}
