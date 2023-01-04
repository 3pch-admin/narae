package ext.narae.loader;

import com.ptc.enterprise.definer.utils.OrgHelper;

import ext.narae.service.org.Department;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.method.RemoteMethodServer;
import wt.query.QuerySpec;
import wt.query.SearchCondition;

public class DepartmentLoaderMain {

	public static void main(String[] args) throws Exception {
		RemoteMethodServer.getDefault().setUserName("wcadmin");
		RemoteMethodServer.getDefault().setPassword("n@r@epdm");

		String name = "자재파트";
		String code = "FORCE_DEPT13";
		int sort = 0;
		String pcode = "FORCE_DEPT22";

		Department dept = Department.newDepartment();
		dept.setName(name);
		dept.setCode(code);
		dept.setSort(sort);

		Department parent = getParent(pcode);
		if (parent != null) {
			dept.setParent(parent);
		}

		PersistenceHelper.manager.save(dept);

		System.out.println("Dept = " + dept.getName());

		System.exit(0);

	}

	private static Department getParent(String code) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Department.class, true);
		SearchCondition sc = new SearchCondition(Department.class, Department.CODE, "=", code.trim());
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
