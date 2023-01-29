package ext.narae;

import wt.epm.EPMDocument;
import wt.fc.PersistenceHelper;
import wt.fc.ReferenceFactory;

public class Test3 {

	public static void main(String[] args) throws Exception {

		String oid = "wt.epm.EPMDocument:28678890";
		ReferenceFactory rf = new ReferenceFactory();
		EPMDocument e = (EPMDocument) rf.getReference(oid).getObject();
		PersistenceHelper.manager.delete(e);
		System.out.println("종료..");

	}

}
