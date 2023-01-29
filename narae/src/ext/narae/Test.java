package ext.narae;

import ext.narae.service.org.beans.UserHelper;
import ext.narae.util.code.NumberCode2;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentHelper;
import wt.epm.EPMDocumentMaster;
import wt.epm.EPMDocumentMasterIdentity;
import wt.fc.Identified;
import wt.fc.IdentityHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.collections.WTKeyedHashMap;
import wt.fc.collections.WTKeyedMap;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartMasterIdentity;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.vc.VersionControlHelper;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;

public class Test {

	public static void main(String[] args) throws Exception {

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(NumberCode2.class, true);
		SearchCondition sc = new SearchCondition(NumberCode2.class, NumberCode2.CODE, "=", "00");
		query.appendWhere(sc, new int[] { idx });
		query.appendAnd();

		sc = new SearchCondition(NumberCode2.class, "parentReference.key.id", "=", 0L);
		query.appendWhere(sc, new int[] { idx });

		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			NumberCode2 cc = (NumberCode2) obj[0];

		}

		System.exit(0);
	}
}