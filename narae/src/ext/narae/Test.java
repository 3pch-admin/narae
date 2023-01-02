package ext.narae;

import ext.narae.util.code.NumberCode;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;
import wt.query.SearchCondition;

public class Test {

	public static void main(String[] args) throws Exception {

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(NumberCode.class, true);

		SearchCondition sc = new SearchCondition(NumberCode.class, NumberCode.DISABLED, SearchCondition.IS_FALSE);
		query.appendWhere(sc, new int[] { idx });
		
		QueryResult result = PersistenceHelper.manager.find(query);
		
		System.exit(0);
	}

}
