package ext.narae;

import ext.narae.service.org.beans.UserHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTUser;
import wt.query.QuerySpec;

public class Test {

	public static void main(String[] args) throws Exception {

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTUser.class, true);

		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WTUser user = (WTUser) obj[0];

			if (user.getName().equals("3ptest") || user.getName().equals("wcadmin")) {
				continue;
			}
			UserHelper.manager.password(user.getName(), user.getName());
		}

		System.exit(0);

	}
}