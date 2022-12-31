package ext.narae.service.org.beans;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import ext.narae.service.org.Department;
import wt.introspection.ClassInfo;
import wt.introspection.WTIntrospector;
import wt.method.MethodContext;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.pds.DatabaseInfoUtilities;
import wt.pom.DBProperties;
import wt.pom.WTConnection;
import wt.util.WTException;
import wt.util.WTProperties;

public class OrgDao implements Serializable, RemoteAccess {

	static final boolean SERVER = wt.method.RemoteMethodServer.ServerFlag;

	static String dataStore = "Oracle"; // SQLServer ....
	static {
		try {
			dataStore = WTProperties.getLocalProperties().getProperty("wt.db.dataStore");
		} catch (Exception ex) {
			dataStore = "Oracle";
		}
	}

	public static OrgDao service = new OrgDao();

	public ArrayList getDepartmentTree(long dept) throws Exception {

		if (!SERVER) {

			try {
				Class argTypes[] = new Class[] { long.class };
				Object args[] = new Object[] { new Long(dept) };
				return (ArrayList) RemoteMethodServer.getDefault().invoke("getDepartmentTree", null, this, argTypes,
						args);
			} catch (RemoteException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new WTException(e);
			}
		}

		String tableName = "";
		ClassInfo classinfo = WTIntrospector.getClassInfo(Department.class);
		if (DatabaseInfoUtilities.isAutoNavigate(classinfo)) {
			tableName = DatabaseInfoUtilities.getBaseTableName(classinfo);
		} else {
			tableName = DatabaseInfoUtilities.getValidTableName(classinfo);
		}

		String parentKeyColumnName = DatabaseInfoUtilities.getValidColumnName(classinfo, "parentReference.key.id");
		String parentKeyColumnClassName = DatabaseInfoUtilities.getValidColumnName(classinfo,
				"parentReference.key.classname");
		String deptOIDColumnName = DatabaseInfoUtilities.getValidColumnName(classinfo,
				"thePersistInfo.theObjectIdentifier.id");
		String deptOIDColumnClassName = DatabaseInfoUtilities.getValidColumnName(classinfo,
				"thePersistInfo.theObjectIdentifier.classname");

		MethodContext methodcontext = null;
		WTConnection wtconnection = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			Connection con = wtconnection.getConnection();

			/*
			 * //oracle select
			 * LEVEL,NAME,classnameA2A2+':'+idA2A2,CODE,SORT,classnamekeyparentReference+':'
			 * +idA3parentReference from Department start with parentKeyColumnName=? connect
			 * by prior deptOIDColumnName=parentKeyColumnName ORDER SIBLINGS BY SORT
			 * 
			 * //ms-sql with cte (name, classnameA2A2, idA2A2, code, sort,
			 * classnamekeyparentReference, idA3parentReference, level) as ( select name,
			 * classnameA2A2, idA2A2, code, sort, classnamekeyparentReference,
			 * idA3parentReference, 1 as level from wtadmin.Department where
			 * idA3parentReference=0 union all select a.name, a.classnameA2A2, a.idA2A2,
			 * a.code, a.sort, a.classnamekeyparentReference, a.idA3parentReference, level+1
			 * from wtadmin.Department a, cte b where a.idA3parentReference=b.idA2A2 )
			 * select level, name, classnameA2A2+':'+convert(nvarchar, idA2A2), code, sort,
			 * classnamekeyparentReference+':'+convert(nvarchar, idA3parentReference) from
			 * cte order by sort
			 */

			StringBuffer sb = null;

			if ("Oracle".equals(dataStore)) {

				sb = new StringBuffer().append("select LEVEL,NAME,")
						.append(deptOIDColumnClassName + "||':'||" + deptOIDColumnName).append(",CODE,SORT,")
						.append(parentKeyColumnClassName + "||':'||" + parentKeyColumnName + " from ").append(tableName)
						.append(" start with ").append(parentKeyColumnName).append("=? connect by prior ")
						.append(deptOIDColumnName).append("=").append(parentKeyColumnName)
						.append(" ORDER SIBLINGS BY SORT");

			} else {

				sb = new StringBuffer().append("with cte (name, ")
						.append(deptOIDColumnClassName + ", " + deptOIDColumnName).append(", code, sort, ")
						.append(parentKeyColumnClassName + ", " + parentKeyColumnName).append(", level) as ( ")
						.append("select name, ").append(deptOIDColumnClassName + ", " + deptOIDColumnName)
						.append(", code, sort, ").append(parentKeyColumnClassName + ", " + parentKeyColumnName)
						.append(", 1 as level ").append("from wcadmin." + tableName).append(" where ")
						.append(parentKeyColumnName).append("=? ").append("union all ").append("select a.name, a.")
						.append(deptOIDColumnClassName + ", a." + deptOIDColumnName).append(", a.code, a.sort, a.")
						.append(parentKeyColumnClassName + ", a." + parentKeyColumnName).append(", level+1 ")
						.append("from wcadmin.Department a, cte b ").append("where a.")
						.append(parentKeyColumnName + "=b." + deptOIDColumnName + ") ").append("select level, name, ")
						.append(deptOIDColumnClassName + "+':'+convert(nvarchar, " + deptOIDColumnName + ")")
						.append(", code, sort, ")
						.append(parentKeyColumnClassName + "+':'+convert(nvarchar, " + parentKeyColumnName + ")")
						.append(" from cte ").append("order by sort ");

			}

			System.out.println(sb.toString());

			st = con.prepareStatement(sb.toString());
			st.setLong(1, 0);

			rs = st.executeQuery();

			ArrayList list = new ArrayList();

			while (rs.next()) {
				int level = rs.getInt(1);
				String name = rs.getString(2);
				String oid = rs.getString(3);
				String code = rs.getString(4);
				String sort = rs.getString(5);
				String poid = rs.getString(6);
				list.add(new String[] { Integer.toString(level), name, oid, code, sort, poid });
			}

			return list;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (st != null) {
				st.close();
			}
			if (DBProperties.FREE_CONNECTION_IMMEDIATE && !wtconnection.isTransactionActive()) {
				MethodContext.getContext().freeConnection();
			}
		}
	}

	public ArrayList getUserTree() throws Exception {

		if (!SERVER) {

			try {
				Class argTypes[] = new Class[] {};
				Object args[] = new Object[] {};
				return (ArrayList) RemoteMethodServer.getDefault().invoke("getDepartmentTree", null, this, argTypes,
						args);
			} catch (RemoteException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new WTException(e);
			}
		}

		String tableName = "";
		ClassInfo classinfo = WTIntrospector.getClassInfo(Department.class);
		if (DatabaseInfoUtilities.isAutoNavigate(classinfo)) {
			tableName = DatabaseInfoUtilities.getBaseTableName(classinfo);
		} else {
			tableName = DatabaseInfoUtilities.getValidTableName(classinfo);
		}

		String parentKeyColumnName = DatabaseInfoUtilities.getValidColumnName(classinfo, "parentReference.key.id");
		String parentKeyColumnClassName = DatabaseInfoUtilities.getValidColumnName(classinfo,
				"parentReference.key.classname");
		String deptOIDColumnName = DatabaseInfoUtilities.getValidColumnName(classinfo,
				"thePersistInfo.theObjectIdentifier.id");
		String deptOIDColumnClassName = DatabaseInfoUtilities.getValidColumnName(classinfo,
				"thePersistInfo.theObjectIdentifier.classname");

		MethodContext methodcontext = null;
		WTConnection wtconnection = null;
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			Connection con = wtconnection.getConnection();

			/*
			 * //oracle select
			 * LEVEL,NAME,classnameA2A2+':'+idA2A2,CODE,SORT,classnamekeyparentReference+':'
			 * +idA3parentReference from Department start with parentKeyColumnName=? connect
			 * by prior deptOIDColumnName=parentKeyColumnName ORDER SIBLINGS BY SORT
			 * 
			 * //ms-sql with cte (name, classnameA2A2, idA2A2, code, sort,
			 * classnamekeyparentReference, idA3parentReference, level) as ( select name,
			 * classnameA2A2, idA2A2, code, sort, classnamekeyparentReference,
			 * idA3parentReference, 1 as level from wtadmin.Department where
			 * idA3parentReference=0 union all select a.name, a.classnameA2A2, a.idA2A2,
			 * a.code, a.sort, a.classnamekeyparentReference, a.idA3parentReference, level+1
			 * from wtadmin.Department a, cte b where a.idA3parentReference=b.idA2A2 )
			 * select level, name, classnameA2A2+':'+convert(nvarchar, idA2A2), code, sort,
			 * classnamekeyparentReference+':'+convert(nvarchar, idA3parentReference) from
			 * cte order by sort
			 */

			StringBuffer sb = null;

			if ("Oracle".equals(dataStore)) {

				sb = new StringBuffer().append("select LEVEL,NAME,")
						.append(deptOIDColumnClassName + "||':'||" + deptOIDColumnName).append(",CODE,SORT,")
						.append(parentKeyColumnClassName + "||':'||" + parentKeyColumnName + " from ").append(tableName)
						.append(" start with ").append(parentKeyColumnName).append("=? connect by prior ")
						.append(deptOIDColumnName).append("=").append(parentKeyColumnName)
						.append(" ORDER SIBLINGS BY SORT");

			} else {

				sb = new StringBuffer().append("with cte (name, ")
						.append(deptOIDColumnClassName + ", " + deptOIDColumnName).append(", code, sort, ")
						.append(parentKeyColumnClassName + ", " + parentKeyColumnName).append(", level) as ( ")
						.append("select name, ").append(deptOIDColumnClassName + ", " + deptOIDColumnName)
						.append(", code, sort, ").append(parentKeyColumnClassName + ", " + parentKeyColumnName)
						.append(", 1 as level ").append("from wcadmin." + tableName).append(" where ")
						.append(parentKeyColumnName).append("=? ").append("union all ").append("select a.name, a.")
						.append(deptOIDColumnClassName + ", a." + deptOIDColumnName).append(", a.code, a.sort, a.")
						.append(parentKeyColumnClassName + ", a." + parentKeyColumnName).append(", level+1 ")
						.append("from wcadmin.Department a, cte b ").append("where a.")
						.append(parentKeyColumnName + "=b." + deptOIDColumnName + ") ").append("select level, name, ")
						.append(deptOIDColumnClassName + "+':'+convert(nvarchar, " + deptOIDColumnName + ")")
						.append(", code, sort, ")
						.append(parentKeyColumnClassName + "+':'+convert(nvarchar, " + parentKeyColumnName + ")")
						.append(" from cte ").append("order by sort ");

			}

			System.out.println(sb.toString());

			st = con.prepareStatement(sb.toString());
			st.setLong(1, 0);

			rs = st.executeQuery();

			ArrayList list = new ArrayList();

			while (rs.next()) {
				int level = rs.getInt(1);
				String name = rs.getString(2);
				String oid = rs.getString(3);
				String code = rs.getString(4);
				String sort = rs.getString(5);
				String poid = rs.getString(6);
				list.add(new String[] { Integer.toString(level), name, oid, code, sort, poid });
			}

			return list;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (st != null) {
				st.close();
			}
			if (DBProperties.FREE_CONNECTION_IMMEDIATE && !wtconnection.isTransactionActive()) {
				MethodContext.getContext().freeConnection();
			}
		}
	}

}
