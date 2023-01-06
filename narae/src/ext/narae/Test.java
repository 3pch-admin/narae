package ext.narae;

import ext.narae.util.code.NumberCode;
import wt.change2.WTChangeRequest2;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.method.RemoteMethodServer;
import wt.query.QuerySpec;
import wt.query.SearchCondition;

public class Test {

	public static void main(String[] args) throws Exception {

//		RemoteMethodServer.getDefault().setUserName("wcadmin");
//		RemoteMethodServer.getDefault().setPassword("n@r@epdm");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTChangeRequest2.class, true);

		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WTChangeRequest2 req = (WTChangeRequest2) obj[0];
			System.out.println("name = " + req.getNumber());
		}

		System.exit(0);
	}

}
