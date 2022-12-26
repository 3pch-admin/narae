package ext.narae.erp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import ext.narae.util.db.DBConnectionManager;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.State;
import wt.part.WTPart;

public class ERPOrderChecker {

	public static void check() throws Exception {

		HashMap param = new HashMap();
		String tblName = "[wcadmin].[ERP_Request_Confirm]";

		String sql = "SELECT [ByDftNo] ,[ItemCode] ,[ItemVer] ,[OkGubn] ,[OkDate] ,[SvDate] ,[SyncDate] FROM " + tblName
				+ " WHERE SyncDate is null";

		// DB Connection
		DBConnectionManager db = DBConnectionManager.getInstance();
		Connection con = db.getConnection("plm");

		// Select
		PreparedStatement ps = con.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			WTPart part = null;
			if (part == null)
				continue;
			LifeCycleHelper.service.setLifeCycleState(part, State.toState("PUR_ORDER_APPROVAL"));
			System.out.println(String.format("[%s] [%s]", rs.getString("ItemCode"), rs.getString("ItemVer"),
					" ==> PUR_ORDER_APPROVAL"));
			ps.execute(String.format(
					"UPDATE %s SET SyncDate = getdate() WHERE [ByDftNo] = '%s' AND [ItemCode] = '%s' AND [SyncDate] IS NULL ",
					tblName, rs.getString("ByDftNo"), rs.getString("ItemCode")));
		}

	}
}
