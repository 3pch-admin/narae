package ext.narae.service;

import java.util.Locale;

import ext.narae.util.CommonUtil;
import ext.narae.util.code.NumberCode;
import ext.narae.util.code.NumberCode2;
import ext.narae.util.code.beans.CodeHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTContext;

public class ObjectCodeHelper {
	static final boolean SERVER = wt.method.RemoteMethodServer.ServerFlag;

	public static CodeHelper manager = new CodeHelper();

	public static QueryResult getCode(String key) {

		System.out.println("key=" + key);
		try {
			QuerySpec query = new QuerySpec(ext.narae.util.code.NumberCode.class);
			query.appendWhere(new SearchCondition(NumberCode.class, NumberCode.CODE_TYPE, "=", key), new int[] { 0 });
//			query.appendAnd();
//			query.appendWhere(new SearchCondition(NumberCode.class, NumberCode.DISABLED, SearchCondition.IS_FALSE),
//					new int[] { 0 });

			query.appendOrderBy(new OrderBy(new ClassAttribute(NumberCode.class, NumberCode.CODE), false),
					new int[] { 0 });
			System.out.println("q=" + query);
			return PersistenceHelper.manager.find(query);
		} catch (Exception ex) {
			ex.printStackTrace();
			return new QueryResult();
		}
	}

	public static QueryResult getCode2(String key) {
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
			QuerySpec query = new QuerySpec(NumberCode.class);

			query.appendWhere(new SearchCondition(NumberCode.class, "codeType", "=", key), new int[] { 0 });
			query.appendAnd();
			query.appendWhere(new SearchCondition(NumberCode.class, "code", "=", code), new int[] { 0 });
			query.appendAnd();
			query.appendWhere(new SearchCondition(NumberCode.class, "disabled", SearchCondition.IS_FALSE),
					new int[] { 0 });

			query.appendOrderBy(new OrderBy(new ClassAttribute(NumberCode.class, NumberCode.CODE), false),
					new int[] { 0 });
			QueryResult qr = PersistenceHelper.manager.find(query);

			if (qr.hasMoreElements()) {
				NumberCode cc = (NumberCode) qr.nextElement();
				return cc.getName();
			}
			return "";
		} catch (Exception ex) {
			return "";
		}
	}

	public String getCode(String key, String name) {
		try {
			QuerySpec query = new QuerySpec(NumberCode.class);

			query.appendWhere(new SearchCondition(NumberCode.class, "codeType", "=", key), new int[] { 0 });
			query.appendAnd();
			query.appendWhere(new SearchCondition(NumberCode.class, "name", "=", name), new int[] { 0 });
			// query.appendAnd();
			// query.appendWhere(new SearchCondition(NumberCode.class, "disabled",
			// SearchCondition.IS_FALSE), new int[] { 0 });

			query.appendOrderBy(new OrderBy(new ClassAttribute(NumberCode.class, NumberCode.CODE), false),
					new int[] { 0 });
			QueryResult qr = PersistenceHelper.manager.find(query);

			if (qr.hasMoreElements()) {
				NumberCode cc = (NumberCode) qr.nextElement();
				return cc.getCode();
			}
			return "";
		} catch (Exception ex) {
			return "";
		}
	}

	public static QueryResult getChildCode(String key, String parentoid) {
		System.out.println("parentoid=" + parentoid);
		if (parentoid != null && parentoid.trim().length() > 0) {
			try {
				QuerySpec query = new QuerySpec(NumberCode.class);
				query.appendWhere(new SearchCondition(NumberCode.class, "codeType", "=", key), new int[] { 0 });
				query.appendAnd();
				query.appendWhere(new SearchCondition(NumberCode.class, "disabled", SearchCondition.IS_FALSE),
						new int[] { 0 });
				query.appendAnd();
				query.appendWhere(new SearchCondition(NumberCode.class, "parentReference.key.id", SearchCondition.EQUAL,
						CommonUtil.getOIDLongValue(parentoid)), new int[] { 0 });
				query.appendOrderBy(new OrderBy(new ClassAttribute(NumberCode.class, NumberCode.CODE), false),
						new int[] { 0 });
				System.out.println("q2=" + query);
				return PersistenceHelper.manager.find(query);
			} catch (Exception ex) {
				ex.printStackTrace();
				return new QueryResult();
			}
		} else {
			return new QueryResult();
		}

	}

	public static QueryResult getChildCode2(String key, String parentoid) {

		if (parentoid != null && parentoid.trim().length() > 0) {
			try {
				QuerySpec query = new QuerySpec(NumberCode2.class);
				query.appendWhere(new SearchCondition(NumberCode2.class, "codeType", "=", key), new int[] { 0 });
				query.appendAnd();
				query.appendWhere(new SearchCondition(NumberCode2.class, "disabled", SearchCondition.IS_FALSE),
						new int[] { 0 });
				query.appendAnd();
				query.appendWhere(new SearchCondition(NumberCode2.class, "parentReference.key.id",
						SearchCondition.EQUAL, CommonUtil.getOIDLongValue(parentoid)), new int[] { 0 });
				query.appendOrderBy(new OrderBy(new ClassAttribute(NumberCode2.class, NumberCode2.CODE), false),
						new int[] { 0 });
				System.out.println(query);
				return PersistenceHelper.manager.find(query);
			} catch (Exception ex) {
				ex.printStackTrace();
				return new QueryResult();
			}
		} else {
			return new QueryResult();
		}

	}

	public static QueryResult getChildCode2(String key) {
		try {
			QuerySpec query = new QuerySpec(NumberCode2.class);
			query.appendWhere(new SearchCondition(NumberCode2.class, "codeType", "=", key), new int[] { 0 });
			query.appendAnd();
			query.appendWhere(new SearchCondition(NumberCode2.class, "disabled", SearchCondition.IS_FALSE),
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

	// 1Levle code
	public static QueryResult getTopCode(String key) {
		Locale aa = WTContext.getContext().getLocale();
		try {
			QuerySpec query = new QuerySpec(NumberCode.class);
			query.appendWhere(new SearchCondition(NumberCode.class, "codeType", "=", key), new int[] { 0 });
			query.appendAnd();
			query.appendWhere(new SearchCondition(NumberCode.class, "disabled", SearchCondition.IS_FALSE),
					new int[] { 0 });
			query.appendAnd();
			query.appendWhere(
					new SearchCondition(NumberCode.class, "parentReference.key.id", SearchCondition.EQUAL, (long) 0),
					new int[] { 0 });
			query.appendOrderBy(new OrderBy(new ClassAttribute(NumberCode.class, NumberCode.CODE), false),
					new int[] { 0 });
			System.out.println(query);
			return PersistenceHelper.manager.find(query);
		} catch (Exception ex) {
			ex.printStackTrace();
			return new QueryResult();
		}

	}

	public static QueryResult getTopCode2(String key) {
		Locale aa = WTContext.getContext().getLocale();
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

	public static NumberCode getTopCodeAssyPart() {
		Locale aa = WTContext.getContext().getLocale();
		try {
			QuerySpec query = new QuerySpec(NumberCode.class);
			query.appendWhere(new SearchCondition(NumberCode.class, "codeType", "=", "CADATTRIBUTE"), new int[] { 0 });
			query.appendAnd();
			query.appendWhere(new SearchCondition(NumberCode.class, "disabled", SearchCondition.IS_FALSE),
					new int[] { 0 });
			query.appendAnd();
			query.appendWhere(new SearchCondition(NumberCode.class, NumberCode.CODE, SearchCondition.EQUAL, "A"),
					new int[] { 0 });
			// query.appendWhere(new SearchCondition(NumberCode.class, NumberCode.CODE,
			// SearchCondition.EQUAL,"P"), new int[] { 0 });
			query.appendAnd();
			query.appendWhere(
					new SearchCondition(NumberCode.class, "parentReference.key.id", SearchCondition.EQUAL, (long) 0),
					new int[] { 0 });
			query.appendOrderBy(new OrderBy(new ClassAttribute(NumberCode.class, NumberCode.CODE), false),
					new int[] { 0 });
			System.out.println(query);

			QueryResult result = PersistenceHelper.manager.find(query);
			return (NumberCode) result.nextElement();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}

	}

	public static NumberCode2 getTopCodeAssyPart2() {
		Locale aa = WTContext.getContext().getLocale();
		try {
			QuerySpec query = new QuerySpec(NumberCode2.class);
			query.appendWhere(new SearchCondition(NumberCode2.class, "codeType", "=", "CADATTRIBUTE"), new int[] { 0 });
			query.appendAnd();
			query.appendWhere(new SearchCondition(NumberCode2.class, "disabled", SearchCondition.IS_FALSE),
					new int[] { 0 });
			query.appendAnd();
			query.appendWhere(new SearchCondition(NumberCode2.class, NumberCode2.CODE, SearchCondition.EQUAL, "A"),
					new int[] { 0 });
			// query.appendWhere(new SearchCondition(NumberCode.class, NumberCode.CODE,
			// SearchCondition.EQUAL,"P"), new int[] { 0 });
			query.appendAnd();
			query.appendWhere(
					new SearchCondition(NumberCode2.class, "parentReference.key.id", SearchCondition.EQUAL, (long) 0),
					new int[] { 0 });
			query.appendOrderBy(new OrderBy(new ClassAttribute(NumberCode2.class, NumberCode2.CODE), false),
					new int[] { 0 });
			System.out.println(query);

			QueryResult result = PersistenceHelper.manager.find(query);
			return (NumberCode2) result.nextElement();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}

	}

//	public boolean isUseCheck(NumberCode code){
//		
//		boolean isUse = false;
//		try{
//			if(code.getCodeType().equals("EOTYPE")){					//��������(ECO)
//			
//				QuerySpec qs =  new QuerySpec(EChangeOrder2.class);
//				qs.appendWhere(new SearchCondition(EChangeOrder2.class,"ecoType",SearchCondition.EQUAL,code.getCodeType().toString()));
//				QueryResult rt = PersistenceHelper.manager.find(qs);
//				if(rt.size() >0 ); isUse =true;
//			
//			}else if(code.getCodeType().equals("CHANGEPURPOSE")){		//��û����,��������,�������(ECO,ECR)
//			
//				/*ECO*/
//				QuerySpec qs =  new QuerySpec(EChangeOrder2.class);
//				qs.appendWhere(new SearchCondition(EChangeOrder2.class,"purpose",SearchCondition.LIKE,"%"+code.getCodeType().toString()+"%"));
//				QueryResult rt = PersistenceHelper.manager.find(qs);
//				if(rt.size() >0 ); isUse =true;
//				
//				/*ECR*/
//				qs =  new QuerySpec(EChangeRequest2.class);
//				qs.appendWhere(new SearchCondition(EChangeRequest2.class,"purpose",SearchCondition.LIKE,"%"+code.getCodeType().toString()+"%"));
//				rt = PersistenceHelper.manager.find(qs);
//				if(rt.size() >0 ); isUse =true;
//			}else if(code.getCodeType().equals("STOCKMANAGEMENT")){		//���� (ECO)
//				
//				/*ECO*/
//				QuerySpec qs =  new QuerySpec(EChangeOrder2.class);
//				qs.appendWhere(new SearchCondition(EChangeOrder2.class,"stockPart",SearchCondition.LIKE,"%"+code.getCodeType().toString()+"%"));
//				QueryResult rt = PersistenceHelper.manager.find(qs);
//				if(rt.size() >0 ); isUse =true;
//			}
//		}catch(Exception e){;
//			e.printStackTrace();
//		}
//		return isUse;
//	}

	public NumberCode getTopParent(NumberCode code) {

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
}
