package ext.narae;

import wt.epm.EPMDocumentMaster;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;

public class Test2 {

	public static void main(String[] args) throws Exception {

		String a = "ASDSD_2D.drw";
		
		int i = a.indexOf("_2D");

		System.out.println(a.substring(0, i));

	}
}