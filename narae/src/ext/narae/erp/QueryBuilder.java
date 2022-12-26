package ext.narae.erp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;

public class QueryBuilder{

	/**
	 * @param tableName 테이블명
	 * @param cols 컬럼 / 값 keypair
	 * @param keys 키 목록
	 * @param type 동작정의
	 * @return 쿼리문 배열
	 */
	public static String[] getQueryString(String tableName, HashMap cols, String[] keys, QueryType type){
		ArrayList<String> _querys = new ArrayList<String>();
		String qry = "";
		if(type == QueryType.INSERT){
			qry = "INSERT %s (%s) VALUES (%s)";
			_querys.add(String.format(qry, tableName, getQueryColumns(cols), getQueryValues(cols)));
		} else if (type == QueryType.INSERT_OR_UPDATE){
			// when record exsits to update
			qry = "UPDATE %s SET %s WHERE %s";
			_querys.add(String.format(qry, tableName, getQuerySetFields(cols), getQueryWhere(cols, keys)));
			// when record not exists to insert
			qry = "INSERT %s (%s) SELECT %s WHERE NOT EXISTS (SELECT TOP 1 %s FROM %s WHERE %s)";
			_querys.add(String.format(qry, tableName, getQueryColumns(cols), getQueryValues(cols), keys[0], tableName, getQueryWhere(cols, keys)));
		} else if (type == QueryType.UPDATE){
			qry = "UPDATE %s SET %s WHERE %s";
			_querys.add(String.format(qry, tableName, getQuerySetFields(cols), getQueryWhere(cols, keys)));
		} else if (type == QueryType.PROCEDURE){
			qry = "EXEC %s %s";
			_querys.add(String.format(qry, tableName, getQuerySetFields(cols)));
		} else if (type == QueryType.SELECT){
			qry = "SELECT %s FROM %s WHERE %s"; 
			_querys.add(String.format(qry, getQueryColumns(cols), tableName, getQueryWhere(cols, keys)));
		}
		//System.out.println("//====  ERP Interface Query ===============================//");
		//for(int q=0; q<_querys.size(); q++){
		//	System.out.println(_querys.get(q));
		//}
		//System.out.println("//====  ERP Interface Query ===============================//");
		return (String[])_querys.toArray(new String[_querys.size()]);
	}

	private static String getQueryWhere(HashMap cols, String[] keys){
		StringBuilder str = new StringBuilder();
		Iterator<String> iterator = cols.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			if(Arrays.asList(keys).indexOf(key)<0) continue;
			str.append("and [" + key + "] = '"+cols.get(key)+ "' ");
		}
		return str.toString().substring(4);
	}

	private static String getQueryColumns(HashMap cols){
		return "[" + StringUtils.join(cols.keySet().toArray(), "], [") + "]";
	}

	private static String getQueryValues(HashMap cols){
		return "'" + StringUtils.join(cols.values().toArray(), "', '") + "'";
	}

	private static String getQuerySetFields(HashMap cols){
		StringBuilder str = new StringBuilder();
		Iterator<String> iterator = cols.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			if(key.startsWith("@")){
				str.append(", " + key + " = '"+cols.get(key)+ "' ");
			}else{
				str.append(", [" + key + "] = '"+cols.get(key)+ "' ");
			}
		}
		return str.toString().substring(2);
	}

}