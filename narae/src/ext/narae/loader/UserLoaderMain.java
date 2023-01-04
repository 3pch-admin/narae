package ext.narae.loader;

import java.io.File;
import java.util.Hashtable;
import java.util.Vector;

import ext.narae.loader.service.LoadHelper;
import ext.narae.service.org.Department;
import ext.narae.service.org.People;
import ext.narae.util.JExcelUtil;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.load.LoadUser;
import wt.method.RemoteMethodServer;
import wt.org.OrganizationServicesHelper;
import wt.org.OrganizationServicesServerHelper;
import wt.org.WTUser;
import wt.query.QuerySpec;
import wt.query.SearchCondition;

public class UserLoaderMain {

	public static void main(String[] args) throws Exception {

		RemoteMethodServer.getDefault().setUserName("wcadmin");
		RemoteMethodServer.getDefault().setPassword("n@r@epdm");

		if (args.length == 0) {
			System.out.println("Excel File Not Set..");
			System.exit(0);
		}

		UserLoaderMain main = new UserLoaderMain();
		main.load(args[0]);
		System.exit(0);
	}

	private void load(String path) throws Exception {
		File excel = null;
		Workbook wb = null;
		try {
			excel = new File(path);
			wb = JExcelUtil.getWorkbook(excel);
			Sheet[] sheets = wb.getSheets();

			for (int i = 0; i < sheets.length; i++) {
				int rows = sheets[i].getRows();
				for (int j = 1; j < rows; j++) {
					Cell[] cell = sheets[i].getRow(j);

					String name = JExcelUtil.getContent(cell, 0);
					String id = JExcelUtil.getContent(cell, 1);
					String email = JExcelUtil.getContent(cell, 2);
					String pw = JExcelUtil.getContent(cell, 3);
					String deptName = JExcelUtil.getContent(cell, 4);

					Department dept = getDept(deptName);
					System.out.println("dept = " + dept);

					Hashtable hash = new Hashtable<>();
					hash.put("newUser", id);
					hash.put("webServerID", id);
					hash.put("fullName", name);
					hash.put("Email", email);
					hash.put("password", pw);
					hash.put("Locale", "KO");
					hash.put("Organization", "naraenano");
					hash.put("ignore", "x");

					LoadHelper.service.loadUserFromExcel(hash);

					WTUser wtUser = OrganizationServicesHelper.manager.getUser(id);

					if (wtUser != null) {
						People p = People.newPeople();
						p.setName(name);
						p.setUser(wtUser);
						p.setDepartment(dept);

						PersistenceHelper.manager.save(p);

						System.out.println("User Create Complete = " + p.getName());
					}

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (wb != null)
				wb.close();
		}
	}

	private static Department getDept(String name) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Department.class, true);
		SearchCondition sc = new SearchCondition(Department.class, Department.NAME, "=", name.trim());
		query.appendWhere(sc, new int[] { idx });
		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Department parent = (Department) obj[0];
			return parent;
		}

		return null;
	}
}
