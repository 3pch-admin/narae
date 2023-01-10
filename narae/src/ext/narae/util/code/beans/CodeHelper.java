package ext.narae.util.code.beans;

import ext.narae.service.change.EChangeOrder2;
import ext.narae.service.change.EChangeRequest2;
import ext.narae.util.CommonUtil;
import ext.narae.util.code.NumberCode;
import ext.narae.util.code.NumberCode2;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;

public class CodeHelper implements wt.method.RemoteAccess, java.io.Serializable {

	static final boolean SERVER = wt.method.RemoteMethodServer.ServerFlag;

	public static CodeHelper manager = new CodeHelper();

	public QueryResult getCode(String key) {
		try {
			QuerySpec query = new QuerySpec(NumberCode2.class);
			query.appendWhere(new SearchCondition(NumberCode2.class, "codeType", "=", key), new int[] { 0 });
			query.appendAnd();
			query.appendWhere(new SearchCondition(NumberCode2.class, "disabled", SearchCondition.IS_FALSE),
					new int[] { 0 });

			query.appendOrderBy(new OrderBy(new ClassAttribute(NumberCode2.class, NumberCode2.CODE), false),
					new int[] { 0 });
			return PersistenceHelper.manager.find(query);
		} catch (Exception ex) {
			ex.printStackTrace();
			return new QueryResult();
		}
	}

	public String getName(String key, String code) {
		try {
			QuerySpec query = new QuerySpec(NumberCode2.class);

			query.appendWhere(new SearchCondition(NumberCode2.class, "codeType", "=", key), new int[] { 0 });
			query.appendAnd();
			query.appendWhere(new SearchCondition(NumberCode2.class, "code", "=", code), new int[] { 0 });
			query.appendAnd();
			query.appendWhere(new SearchCondition(NumberCode2.class, "disabled", SearchCondition.IS_FALSE),
					new int[] { 0 });

			query.appendOrderBy(new OrderBy(new ClassAttribute(NumberCode2.class, NumberCode2.CODE), false),
					new int[] { 0 });
			QueryResult qr = PersistenceHelper.manager.find(query);

			if (qr.hasMoreElements()) {
				NumberCode2 cc = (NumberCode2) qr.nextElement();
				return cc.getName();
			}
			return "";
		} catch (Exception ex) {
			return "";
		}
	}

	public String getCode(String key, String name) {
		try {
			QuerySpec query = new QuerySpec(NumberCode2.class);

			query.appendWhere(new SearchCondition(NumberCode2.class, "codeType", "=", key), new int[] { 0 });
			query.appendAnd();
			query.appendWhere(new SearchCondition(NumberCode2.class, "name", "=", name), new int[] { 0 });
			// query.appendAnd();
			// query.appendWhere(new SearchCondition(NumberCode2.class, "disabled",
			// SearchCondition.IS_FALSE), new int[] { 0 });

			query.appendOrderBy(new OrderBy(new ClassAttribute(NumberCode2.class, NumberCode2.CODE), false),
					new int[] { 0 });
			QueryResult qr = PersistenceHelper.manager.find(query);

			if (qr.hasMoreElements()) {
				NumberCode2 cc = (NumberCode2) qr.nextElement();
				return cc.getCode();
			}
			return "";
		} catch (Exception ex) {
			return "";
		}
	}

	public QueryResult getChildCode(String key, String parentoid) {

		try {
			QuerySpec query = new QuerySpec(NumberCode2.class);
			query.appendWhere(new SearchCondition(NumberCode2.class, "codeType", "=", key), new int[] { 0 });
			query.appendAnd();
			query.appendWhere(new SearchCondition(NumberCode2.class, "disabled", SearchCondition.IS_FALSE),
					new int[] { 0 });
			query.appendAnd();
			query.appendWhere(new SearchCondition(NumberCode2.class, "parentReference.key.id", SearchCondition.EQUAL,
					CommonUtil.getOIDLongValue(parentoid)), new int[] { 0 });
			query.appendOrderBy(new OrderBy(new ClassAttribute(NumberCode2.class, NumberCode2.CODE), false),
					new int[] { 0 });
			return PersistenceHelper.manager.find(query);
		} catch (Exception ex) {
			ex.printStackTrace();
			return new QueryResult();
		}

	}

	// 1Levle code
	public QueryResult getTopCode(String key) {
		try {
			QuerySpec query = new QuerySpec(NumberCode2.class);
			query.appendWhere(new SearchCondition(NumberCode2.class, "codeType", "=", key), new int[] { 0 });
			query.appendAnd();
			query.appendWhere(new SearchCondition(NumberCode2.class, "disabled", SearchCondition.IS_FALSE),
					new int[] { 0 });
			query.appendAnd();
			query.appendWhere(
					new SearchCondition(NumberCode2.class, "parentReference.key.id", SearchCondition.EQUAL, (long) 0),
					new int[] { 0 });
			query.appendOrderBy(new OrderBy(new ClassAttribute(NumberCode2.class, NumberCode2.CODE), false),
					new int[] { 0 });
			System.out.println(query);
			return PersistenceHelper.manager.find(query);
		} catch (Exception ex) {
			ex.printStackTrace();
			return new QueryResult();
		}
	}

	public boolean isUseCheck(NumberCode2 code) {

		boolean isUse = false;
		try {
			if (code.getCodeType().equals("EOTYPE")) { // 설변구분(ECO)

				QuerySpec qs = new QuerySpec(EChangeOrder2.class);
				qs.appendWhere(new SearchCondition(EChangeOrder2.class, "ecoType", SearchCondition.EQUAL,
						code.getCodeType().toString()));
				QueryResult rt = PersistenceHelper.manager.find(qs);
				if (rt.size() > 0)
					;
				isUse = true;

			} else if (code.getCodeType().equals("CHANGEPURPOSE")) { // 요청유형,설변목적,변경사유(ECO,ECR)

				/* ECO */
				QuerySpec qs = new QuerySpec(EChangeOrder2.class);
				qs.appendWhere(new SearchCondition(EChangeOrder2.class, "purpose", SearchCondition.LIKE,
						"%" + code.getCodeType().toString() + "%"));
				QueryResult rt = PersistenceHelper.manager.find(qs);
				if (rt.size() > 0)
					;
				isUse = true;

				/* ECR */
				qs = new QuerySpec(EChangeRequest2.class);
				qs.appendWhere(new SearchCondition(EChangeRequest2.class, "purpose", SearchCondition.LIKE,
						"%" + code.getCodeType().toString() + "%"));
				rt = PersistenceHelper.manager.find(qs);
				if (rt.size() > 0)
					;
				isUse = true;
			} else if (code.getCodeType().equals("STOCKMANAGEMENT")) { // 재고관리 (ECO)

				/* ECO */
				QuerySpec qs = new QuerySpec(EChangeOrder2.class);
				qs.appendWhere(new SearchCondition(EChangeOrder2.class, "stockPart", SearchCondition.LIKE,
						"%" + code.getCodeType().toString() + "%"));
				QueryResult rt = PersistenceHelper.manager.find(qs);
				if (rt.size() > 0)
					;
				isUse = true;
			}
		} catch (Exception e) {
			;
			e.printStackTrace();
		}
		return isUse;
	}

	public NumberCode getTopParent(NumberCode code) {

		if (code.getParent() != null) {

			code = getTopParent(code.getParent());
		}

		return code;
	}

	public NumberCode2 getTopParent(NumberCode2 code) {

		if (code.getParent() != null) {

			code = getTopParent(code.getParent());
		}

		return code;
	}

	public int getCodelevel(NumberCode code, Integer level) {

		if (code.getParent() != null) {
			level = getCodelevel(code.getParent(), ++level);
		}

		return level;
	}

	public int getCodelevel(NumberCode2 code, Integer level) {

		if (code.getParent() != null) {
			level = getCodelevel(code.getParent(), ++level);
		}

		return level;
	}
}
