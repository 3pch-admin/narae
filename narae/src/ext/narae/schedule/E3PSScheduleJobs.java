package ext.narae.schedule;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import ext.narae.service.erp.beans.ERPUtil;
import ext.narae.util.db.DBConnectionManager;
import ext.narae.util.iba.IBAUtil;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.iba.definition.FloatDefinition;
import wt.iba.value.FloatValue;
import wt.iba.value.IBAHolder;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.pds.StatementSpec;
import wt.pom.Transaction;
import wt.query.ClassAttribute;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.queue.QueueEntry;
import wt.queue.StatusInfo;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class E3PSScheduleJobs {
	public static final String ERP = ERPUtil.ERP;

	public static void erpReceiveStateDailyBatch() {
		boolean result = false;
		try {
			System.out.println("***** [START] erpReceiveStateDailyBatch *****");
			result = getCostAndModify();
			System.out.println("***** [END] erpReceiveStateDailyBatch *****");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void resetQueueDailyBatch() {
		int total = 0;
		try {
			System.out.println("***** [START] resetQueueDailyBatch *****");
			total = resetQueue();
			System.out.println("***** [resetQueue] total : " + total + " *****");
			System.out.println("***** [END] resetQueueDailyBatch *****");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean getCostAndModify() throws Exception {

		DBConnectionManager dbmanager = null;
		Connection con = null;
		ResultSet rs = null;
		boolean isExist = false;
		boolean result = false;
		String WorkDate = "";
		String ItemCode = "";
		String ItemVer = "";
		String Cost = "";
		String Flag = "";
		String version = "";
		try {
			dbmanager = DBConnectionManager.getInstance();
			con = dbmanager.getConnection(ERP);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT * FROM pdm14 WHERE Flag = ?");
			PreparedStatement st = con.prepareStatement(sql.toString());
			st.setString(1, "N");
			rs = st.executeQuery();
			while (rs.next()) {
				WorkDate = (String) rs.getString("WorkDate");
				ItemCode = (String) rs.getString("ItemCode");
				ItemVer = (String) rs.getString("ItemVer");
				version = ItemVer;
				Cost = (String) rs.getString("Cost");
				Flag = (String) rs.getString("Flag");
				if (ItemVer.contains(".")) {
					ItemVer = ItemVer.replace(".", "_");
					ItemVer = ItemVer.split("_")[0];
				}
				System.out.println("ItemVer ********************* :" + ItemVer);
				if ("Y".equals(Flag)) {

				} else {
					result = setCostERP(ItemCode, ItemVer, Cost);
					System.out.println("result : " + result);
					if (result) {
						System.out.println("UPDATE COST *********************");
						System.out.println("ItemCode :" + ItemCode);
						System.out.println("ItemVer :" + version);
						System.out.println("Cost :" + Cost);
						System.out.println("Flag :" + Flag);
						StringBuffer sql2 = new StringBuffer();
						sql2.append("Update pdm14  ").append(" set Flag = ?").append(" Where itemCode = ?")
								.append(" and itemVer =?");
						PreparedStatement st2 = con.prepareStatement(sql2.toString());
						int idx = 1;
						st2.setString(idx, "Y");
						st2.setString(++idx, ItemCode);
						st2.setString(++idx, version);
						st2.execute();
					}
				}

			}
		} catch (Exception ex) {
			ex.printStackTrace();
			result = false;
			throw ex;
		} finally {
			if (dbmanager != null) {
				dbmanager.freeConnection(ERP, con);
			}
		}

		return isExist;
	}

	public static int resetQueue() throws WTException {
		int cnt = 0;
		long failCount = 1;
		// long oid = 1856585410;
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(QueueEntry.class, true);
		SearchCondition sc = new SearchCondition(QueueEntry.class, "statusInfo.code", "=", "NONEVENTFAILED");
		query.appendWhere(sc, new int[] { idx });

		// query.appendAnd();
		// sc = new SearchCondition(QueueEntry.class,
		// "thePersistInfo.theObjectIdentifier.id", "=", oid);
		// query.appendWhere(sc, new int[] { idx });

		QueryResult result = PersistenceHelper.manager.find(query);
		QueueEntry e = null;
		while (result.hasMoreElements()) {
			cnt++;
			Object[] obj = (Object[]) result.nextElement();
			e = (QueueEntry) obj[0];
//			System.out.println("info : "+e.getNumber());
//			System.out.println("info2 : "+e.getStatusInfo().getCode());
//			System.out.println("info3 : "+e.getStatusInfo().getMessage());
			if (null == e.getFailureCount()) {
				e.setFailureCount(failCount);
				StatusInfo info = new StatusInfo();
				info.setCode("READY");
				info.setMessage(e.getStatusInfo().getMessage());
				e.setStatusInfo(info);
				e = (QueueEntry) PersistenceHelper.manager.save(e);
			} else {
				if (3 <= e.getFailureCount()) {
					StatusInfo info = new StatusInfo();
					info.setCode("FAILED");
					info.setMessage(e.getStatusInfo().getMessage());
					e.setStatusInfo(info);
					e = (QueueEntry) PersistenceHelper.manager.save(e);
				} else {
					failCount = e.getFailureCount() + 1;
					e.setFailureCount(failCount);
					StatusInfo info = new StatusInfo();
					info.setCode("READY");
					info.setMessage(e.getStatusInfo().getMessage());
					e.setStatusInfo(info);
					e = (QueueEntry) PersistenceHelper.manager.save(e);
				}
			}
//			e.setFailureCount(failCount);
//			StatusInfo info = new StatusInfo();
//			info.setCode("READY");
//			info.setMessage(e.getStatusInfo().getMessage());
//			e.setStatusInfo(info);
//			e = (QueueEntry) PersistenceHelper.manager.save(e);
		}

		return cnt;

	}

	public static boolean setCostERP(String number, String version, String cost)
			throws WTException, NumberFormatException, RemoteException, WTPropertyVetoException {
		System.out.println("number : " + number + "///" + "version : " + version + "///" + "cost : " + cost);
		boolean bol = true;
		WTPart part = null;
		QuerySpec query = new QuerySpec();
		Transaction trs = new Transaction();
		try {
			trs.start();
			SearchCondition sc = null;
			int idx_m = query.appendClassList(WTPartMaster.class, false);
			int idx_p = query.appendClassList(WTPart.class, true);
			query.appendWhere(new SearchCondition(WTPartMaster.class, "thePersistInfo.theObjectIdentifier.id",
					WTPart.class, "masterReference.key.id"), new int[] { idx_m, idx_p });
			query.appendAnd();
			sc = new SearchCondition(WTPart.class, WTPart.NUMBER, "=", number);
			query.appendWhere(sc, new int[] { idx_p });
			query.appendAnd();
			sc = new SearchCondition(WTPart.class, "versionInfo.identifier.versionId", "=", version);
			query.appendWhere(sc, new int[] { idx_p });
			QueryResult result = PersistenceHelper.manager.find(query);
			System.out.println("qr :: " + result.size());
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				part = (WTPart) obj[0];
				IBAUtil.changeIBAValue(part, "COST_ERP", cost);
			}
			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			bol = false;
		} finally {
			if (trs != null)
				trs.rollback();
		}

		return bol;
	}

	public static double getFloatValue(IBAHolder holder, String attrName) throws Exception {
		if (holder == null) {
			return 0;
		}

		QuerySpec query = new QuerySpec();

		int ii = query.appendClassList(holder.getClass(), false);
		int jj = query.appendClassList(FloatValue.class, false);
		int kk = query.appendClassList(FloatDefinition.class, false);

		long key = ((Persistable) holder).getPersistInfo().getObjectIdentifier().getId();
		SearchCondition sc = new SearchCondition(holder.getClass(), "thePersistInfo.theObjectIdentifier.id", "=", key);
		query.appendWhere(sc, new int[] { ii });
		query.appendAnd();

		ClassAttribute ca = new ClassAttribute(FloatValue.class, "value");
		query.appendSelect(ca, new int[] { jj }, false);

		ca = new ClassAttribute(FloatValue.class, "theIBAHolderReference.key.id");
		sc = new SearchCondition(ca, "=",
				new ClassAttribute(holder.getClass(), "thePersistInfo.theObjectIdentifier.id"));
		query.appendWhere(sc, new int[] { jj, ii });
		query.appendAnd();

		ca = new ClassAttribute(FloatValue.class, "definitionReference.hierarchyID");
		sc = new SearchCondition(ca, "=", new ClassAttribute(FloatDefinition.class, "hierarchyID"));
		query.appendWhere(sc, new int[] { jj, kk });
		query.appendAnd();

		sc = new SearchCondition(FloatDefinition.class, "name", "=", attrName);
		query.appendWhere(sc, new int[] { kk });

		QueryResult result = PersistenceHelper.manager.find((StatementSpec) query);
		int value = 0;
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			value = ((BigDecimal) obj[0]).intValue();
		}
		return value;
	}
}
