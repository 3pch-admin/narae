package ext.narae.loader.service;

import java.util.Hashtable;
import java.util.Vector;

import wt.load.LoadUser;
import wt.org.OrganizationServicesMgr;
import wt.org.WTUser;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardLoadService extends StandardManager implements LoadService {

	public static StandardLoadService newStandardLoadService() throws WTException {
		StandardLoadService instance = new StandardLoadService();
		instance.initialize();
		return instance;
	}

	@Override
	public void loadUserFromExcel(Hashtable hash) throws Exception {
		String id = (String) hash.get("newUser");
		Transaction trs = new Transaction();
		try {
			trs.start();

			WTUser user = OrganizationServicesMgr.getUser(id);
			if (user == null) {
				LoadUser.createUser(hash, new Hashtable(), new Vector());
			} else {

			}

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}
}
