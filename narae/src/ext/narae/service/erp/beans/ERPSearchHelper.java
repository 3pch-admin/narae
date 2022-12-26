package ext.narae.service.erp.beans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import ext.narae.service.erp.ERPHistory;
import ext.narae.service.erp.ERPPartLinkO;
import ext.narae.util.CommonUtil;
import ext.narae.util.StringUtil;
import ext.narae.util.code.NumberCode;
import ext.narae.util.code.beans.CodeHelper;
import ext.narae.util.db.DBConnectionManager;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;
import wt.query.SearchCondition;

public class ERPSearchHelper {

	public static ERPSearchHelper manager = new ERPSearchHelper();
	public static final String ERP = ERPUtil.ERP;

	// MAKER
	public ResultSet getErpMaker(HashMap map) throws Exception {

		DBConnectionManager dbmanager = null;
		Connection con = null;
		Vector<HashMap> vec = new Vector();
		ResultSet rs = null;
		//
		try {

			String makerCode = StringUtil.checkNull((String) map.get("makerCode"));
			String makerName = StringUtil.checkNull((String) map.get("makerName"));
			String searchType = StringUtil.checkNull((String) map.get("searchType"));

			dbmanager = DBConnectionManager.getInstance();
			con = dbmanager.getConnection(ERP);

			StringBuffer sql = new StringBuffer();
			if (searchType.equals("equals")) {// equals Search
				sql.append("select * from " + ERPUtil.ERPMAKER);
				sql.append(" where 1=1");
				if (makerCode.length() > 0)
					sql.append(" and Maker = ?");
				if (makerName.length() > 0)
					sql.append(" and MkrName = ?");
			} else { // like Search
				sql.append("select * from " + ERPUtil.ERPMAKER);
				sql.append(" where 1=1");
				if (makerCode.length() > 0) {
					makerCode = "%" + makerCode + "%";
					sql.append(" and Maker like ?");
				}
				if (makerName.length() > 0) {
					makerName = "%" + makerName + "%";
					sql.append(" and MkrName like ?");
				}
			}

			sql.append(" order by MkrName");

			// System.out.println(sql.toString());
			PreparedStatement st = con.prepareStatement(sql.toString());
			int idx = 0;
			if (makerCode.length() > 0)
				st.setString(++idx, makerCode);
			if (makerName.length() > 0)
				st.setString(++idx, makerName);
			// System.out.println(">>>>>>>>>>makerName"+makerName);
			rs = st.executeQuery();
			/*
			 * while(rs.next()){ HashMap mapMaker = new HashMap(); mapMaker.put("makerCode",
			 * rs.getObject("Maker")); mapMaker.put("makerName", rs.getObject("MkrName"));
			 * vec.add(mapMaker); }
			 */

		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
			if (dbmanager != null) {
				dbmanager.freeConnection(ERP, con);
			}
		}

		return rs;
	}

	public ResultSet getErpProject(HashMap map) throws Exception {

		DBConnectionManager dbmanager = null;
		Connection con = null;
		Vector<HashMap> vec = new Vector();
		ResultSet rs = null;
		try {
			String custname = (String) map.get("CustName");
			String sodtfr = (String) map.get("sodtfr");
			if (sodtfr != null && sodtfr.length() == 0)
				sodtfr = "0000.00";
			String sodtto = (String) map.get("sodtto");
			if (sodtto != null && sodtto.length() == 0)
				sodtto = "9999.99";
			String so_no = (String) map.get("so_no");
			String soname = (String) map.get("so_name");
			String PrjSeqNo = (String) map.get("PrjSeqNo");

			dbmanager = DBConnectionManager.getInstance();
			con = dbmanager.getConnection(ERPUtil.ERP);

			StringBuffer sql = new StringBuffer();
			sql.append("exec usp_select_sales_order ?,?,?,?");

			PreparedStatement st = con.prepareStatement(sql.toString());
			st.setString(1, custname);
			st.setString(2, so_no);
			st.setString(3, soname);
			st.setString(4, PrjSeqNo);
//			st.setString(5, soname);
			rs = st.executeQuery();
			/*
			 * while(rs.next()){ HashMap mapProject = new HashMap(); mapProject.put("PrjNo",
			 * rs.getString("PrjNo")); mapProject.put("PrjSeqNo", rs.getString("PrjSeqNo"));
			 * mapProject.put("CustCode", rs.getString("CustCode"));
			 * mapProject.put("CustName", rs.getString("CustName"));
			 * mapProject.put("ObtOrdDate", rs.getString("ObtOrdDate"));
			 * mapProject.put("UnitCode", rs.getString("UnitCode")); vec.add(mapProject); }
			 */

		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
			if (dbmanager != null) {
				dbmanager.freeConnection(ERP, con);
			}
		}

		return rs;
	}

	public String getPrjName(String PrjCode) throws Exception {

		DBConnectionManager dbmanager = null;
		Connection con = null;
		Vector<HashMap> vec = new Vector();
		ResultSet rs = null;
		String prjName = "";

		try {

			dbmanager = DBConnectionManager.getInstance();
			con = dbmanager.getConnection(ERP);

			StringBuffer sql = new StringBuffer();
			sql.append("exec usp_select_sales_order ?,?,?,?,?");

			PreparedStatement st = con.prepareStatement(sql.toString());
			st.setString(1, "");
			st.setString(2, "0000.00");
			st.setString(3, "9999.99");
			st.setString(4, PrjCode);
			st.setString(5, "");
			rs = st.executeQuery();

			while (rs.next()) {
				prjName = (String) rs.getString("PrjName");
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
			if (dbmanager != null) {
				dbmanager.freeConnection(ERP, con);
			}
		}

		return prjName;
	}

	public String getErpProjectName(String PrjCode) throws Exception {

		DBConnectionManager dbmanager = null;
		Connection con = null;
		Vector<HashMap> vec = new Vector();
		ResultSet rs = null;
		String prjectName = "";

		try {

			dbmanager = DBConnectionManager.getInstance();
			con = dbmanager.getConnection(ERP);

			StringBuffer sql = new StringBuffer();
			sql.append("exec usp_select_sales_order ?,?,?,?,?");

			PreparedStatement st = con.prepareStatement(sql.toString());
			st.setString(1, "");
			st.setString(2, "0000.00");
			st.setString(3, "9999.99");
			st.setString(4, PrjCode);
			st.setString(5, "");
			rs = st.executeQuery();

			while (rs.next()) {
				prjectName = (String) rs.getString("PrjNo") + "_" + (String) rs.getString("PrjName");
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
			if (dbmanager != null) {
				dbmanager.freeConnection(ERP, con);
			}
		}

		return prjectName;
	}

	public boolean duplicationBOM(ArrayList list) throws Exception {

		DBConnectionManager dbmanager = null;
		Connection con = null;
		Vector<HashMap> vec = new Vector();
		ResultSet rs = null;
		String prjectName = "";
		boolean isBom = false;
		try {
			dbmanager = DBConnectionManager.getInstance();
			con = dbmanager.getConnection(ERP);

			StringBuffer sql = new StringBuffer();
			sql.append("SELECT * FROM PDMBOM WHERE ").append(" (ParentItemCode =? and ParentItemVer = ? )")
					.append(" AND (ItemCode =? and ItemVer = ? )");

			PreparedStatement st = con.prepareStatement(sql.toString());

			st.setString(1, (String) list.get(0)); // ParentItemCode
			st.setString(2, (String) list.get(1)); // ParentItemVer
			st.setString(3, (String) list.get(2)); // ItemCode
			st.setString(4, (String) list.get(3)); // ItemVer

			ResultSet rt = st.executeQuery();

			if (rt.next())
				isBom = true;

		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
			if (dbmanager != null) {
				dbmanager.freeConnection(ERP, con);
			}
		}

		return isBom;
	}

	public boolean duplicationECO(String eoNumber) throws Exception {

		DBConnectionManager dbmanager = null;
		Connection con = null;
		ResultSet rs = null;
		boolean isExist = false;
		try {
			dbmanager = DBConnectionManager.getInstance();
			con = dbmanager.getConnection(ERP);

			StringBuffer sql = new StringBuffer();
			sql.append("SELECT * FROM PDMECO WHERE EcoNo = ?");

			PreparedStatement st = con.prepareStatement(sql.toString());

			st.setString(1, eoNumber);
			ResultSet rt = st.executeQuery();

			if (rt.next())
				isExist = true;

		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
			if (dbmanager != null) {
				dbmanager.freeConnection(ERP, con);
			}
		}

		return isExist;
	}

	public boolean duplicationPart(String number, String version) throws Exception {

		DBConnectionManager dbmanager = null;
		Connection con = null;
		ResultSet rs = null;
		boolean isExist = false;
		try {
			dbmanager = DBConnectionManager.getInstance();
			con = dbmanager.getConnection(ERP);

			StringBuffer sql = new StringBuffer();
			sql.append("SELECT * FROM PDM00 WHERE itemCode = ? AND itemVer=?");

			PreparedStatement st = con.prepareStatement(sql.toString());

			st.setString(1, number);
			st.setString(2, version);
			ResultSet rt = st.executeQuery();

			if (rt.next())
				isExist = true;

		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
			if (dbmanager != null) {
				dbmanager.freeConnection(ERP, con);
			}
		}

		return isExist;
	}

	public boolean duplicationCode(String codeType, String code) throws Exception {
		// System.out.println(">>>>>>>>>>>>>>>>>>> duplicationCode<<<<<<<<<<<<<<<<< ");
		return duplicationCode(codeType, code, "");
	}

	public boolean duplicationCode(String codeType, String code, NumberCode parentcode) throws Exception {
		// System.out.println(">>>>>>>>>>>>>>>>>>> duplicationCode<<<<<<<<<<<<<<<<< ");
		String poid = "";
		if (parentcode != null)
			poid = CommonUtil.getOIDString(parentcode);

		return duplicationCode(codeType, code, poid);
	}

	public boolean duplicationCode(String codeType, String code, String poid) throws Exception {

		// System.out.println("::::::::::::::::::::::::: duplicationCode
		// ::::::::::::::::");
		// System.out.println("::::::::::::::::::::::::: codeType :"+codeType);
		// System.out.println("::::::::::::::::::::::::: code :"+code);
		// System.out.println("::::::::::::::::::::::::: poid :"+poid);
		DBConnectionManager dbmanager = null;
		Connection con = null;
		boolean isExist = false;
		try {

			dbmanager = DBConnectionManager.getInstance();
			con = dbmanager.getConnection(ERP);
			NumberCode parentCode = null;
			poid = StringUtil.checkNull(poid);
			if (poid.length() > 0)
				parentCode = (NumberCode) CommonUtil.getObject(poid);

			if (codeType.equals("CADCREATOR")) { // 도면구성분류 (ERP 품목 구성 분류) : PDM01
				isExist = this.checkPDM01(code, con);
			} else if (codeType.equals("CADATTRIBUTE")) {
				if (parentCode == null) { // 도면 분류 체s계 (ERP 품목 유형 분류) : PDM02
					isExist = this.checkPDM02(code, con);
				} else {
					NumberCode pcode = CodeHelper.manager.getTopParent(parentCode);
					int level = 1;
					level = CodeHelper.manager.getCodelevel(parentCode, level);

					if (pcode.getCode().equals("A")) { // 반제품
						if (level == 1) { // 반제품 - UNIT : PDM07
							isExist = this.checkPDM07(code, con);
						} else if (level == 2) { // 반제품 -Assy : PDM08
							isExist = this.checkPDM08(parentCode, code, con);
						}
					} else if (pcode.getCode().equals("B")) { // 구매품(원래료)
						if (level == 1) { // 구매품 - 제품군 : PDM09
							isExist = this.checkPDM09(code, con);
						} else if (level == 2) { // 구매품 - 제품기능 : PDM10
							isExist = this.checkPDM10(parentCode, code, con);
						} else if (level == 3) { // 구매품 - 예비 : PDM10R
							isExist = this.checkPDM10R(parentCode, code, con);
						}
					} else if (pcode.getCode().equals("P")) { // 가공품(원래료)
						if (level == 1) { // 가공품 - 방법 : PDM11
							isExist = this.checkPDM11(code, con);
						} else if (level == 2) { // 가공품 - 분류 : PDM12
							isExist = this.checkPDM12(parentCode, code, con);
						} else if (level == 3) { // 가공품 -예비 : PDM13
							isExist = this.checkPDM12R(parentCode, code, con);
						}
					}
				}
			} else if (codeType.equals("SBUSINESS")) { // 제품 사업부 (ERP-제품 사업군) : PDM04
				isExist = this.checkPDM04(code, con);
			} else if (codeType.equals("SCUSTOMER")) { // 제품 고객사 (ERP-제품 고객사) : PDM05
				isExist = this.checkPDM05(code, con);
			} else if (codeType.equals("SCLASS1")) { // 제품 중분류1 (ERP-제품 장비 분류) : PDM03
				isExist = this.checkPDM03(code, con);
			} else if (codeType.equals("SCLASS2")) { // 제품 중분류2 (ERP-제품 세대) : PDM06
				isExist = this.checkPDM06(code, con);
			} else if (codeType.equals("SCLASS3")) { // 제품 중분류2 (ERP-제품 예비) : PDM06R
				isExist = this.checkPDM06R(code, con);
			} else if (codeType.equals("CHANGEPURPOSE")) { // 변경사유 ,요청 유형 : PDM16
				isExist = this.checkPDM16(code, con);
			} else if (codeType.equals("EOTYPE")) { // 설변구분 : pDM17
				isExist = this.checkPDM17(code, con);
			} else if (codeType.equals("SPEC")) { // 규격 : PDM13
				System.out.println(">>>>>>>>>>>>>>> codeType :" + codeType + ": code :" + code);
				isExist = this.checkPDM13(code, con);
			} else {
				isExist = false;
			}

		} catch (Exception ex) {
			isExist = true;
			ex.printStackTrace();
			throw ex;
		} finally {
			if (dbmanager != null) {
				dbmanager.freeConnection(ERP, con);
			}
		}

		return isExist;

	}

	public boolean checkPDM01(String code, Connection con) {

		boolean isExist = false;
		try {

			StringBuffer sql = ERPUtil.getSQLPDM01(ERPUtil.SQLCHECK);
			PreparedStatement st = con.prepareStatement(sql.toString());

			st.setString(1, code);
			ResultSet rt = st.executeQuery();

			if (rt.next())
				isExist = true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return isExist;
	}

	public boolean checkPDM02(String code, Connection con) {

		boolean isExist = false;
		try {

			StringBuffer sql = ERPUtil.getSQLPDM02(ERPUtil.SQLCHECK);
			PreparedStatement st = con.prepareStatement(sql.toString());

			st.setString(1, code);
			ResultSet rt = st.executeQuery();

			if (rt.next())
				isExist = true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return isExist;
	}

	public boolean checkPDM03(String code, Connection con) {

		boolean isExist = false;
		try {

			StringBuffer sql = ERPUtil.getSQLPDM03(ERPUtil.SQLCHECK);
			PreparedStatement st = con.prepareStatement(sql.toString());

			st.setString(1, code);
			ResultSet rt = st.executeQuery();

			if (rt.next())
				isExist = true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return isExist;
	}

	public boolean checkPDM04(String code, Connection con) {

		boolean isExist = false;
		try {

			StringBuffer sql = ERPUtil.getSQLPDM04(ERPUtil.SQLCHECK);
			PreparedStatement st = con.prepareStatement(sql.toString());

			st.setString(1, code);
			ResultSet rt = st.executeQuery();

			if (rt.next())
				isExist = true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return isExist;
	}

	public boolean checkPDM05(String code, Connection con) {

		boolean isExist = false;
		try {

			StringBuffer sql = ERPUtil.getSQLPDM05(ERPUtil.SQLCHECK);
			PreparedStatement st = con.prepareStatement(sql.toString());

			st.setString(1, code);
			ResultSet rt = st.executeQuery();

			if (rt.next())
				isExist = true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return isExist;
	}

	public boolean checkPDM06(String code, Connection con) {

		boolean isExist = false;
		try {

			StringBuffer sql = ERPUtil.getSQLPDM06(ERPUtil.SQLCHECK);
			PreparedStatement st = con.prepareStatement(sql.toString());

			st.setString(1, code);
			ResultSet rt = st.executeQuery();

			if (rt.next())
				isExist = true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return isExist;
	}

	public boolean checkPDM06R(String code, Connection con) {

		boolean isExist = false;
		try {

			StringBuffer sql = ERPUtil.getSQLPDM06R(ERPUtil.SQLCHECK);
			PreparedStatement st = con.prepareStatement(sql.toString());

			st.setString(1, code);
			ResultSet rt = st.executeQuery();

			if (rt.next())
				isExist = true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return isExist;
	}

	public boolean checkPDM07(String code, Connection con) {

		boolean isExist = false;
		try {

			StringBuffer sql = ERPUtil.getSQLPDM07(ERPUtil.SQLCHECK);
			PreparedStatement st = con.prepareStatement(sql.toString());

			st.setString(1, code);
			ResultSet rt = st.executeQuery();

			if (rt.next())
				isExist = true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return isExist;
	}

	public boolean checkPDM08(NumberCode parentCode, String code, Connection con) {

		boolean isExist = false;
		try {

			StringBuffer sql = ERPUtil.getSQLPDM08(ERPUtil.SQLCHECK);
			PreparedStatement st = con.prepareStatement(sql.toString());

			st.setString(1, parentCode.getCode());
			st.setString(2, code);
			ResultSet rt = st.executeQuery();

			if (rt.next())
				isExist = true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return isExist;
	}

	public boolean checkPDM09(String code, Connection con) {

		boolean isExist = false;
		try {

			StringBuffer sql = ERPUtil.getSQLPDM09(ERPUtil.SQLCHECK);
			PreparedStatement st = con.prepareStatement(sql.toString());

			st.setString(1, code);
			ResultSet rt = st.executeQuery();

			if (rt.next())
				isExist = true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return isExist;
	}

	public boolean checkPDM10(NumberCode parentCode, String code, Connection con) {

		boolean isExist = false;
		try {

			StringBuffer sql = ERPUtil.getSQLPDM10(ERPUtil.SQLCHECK);
			PreparedStatement st = con.prepareStatement(sql.toString());

			st.setString(1, parentCode.getCode());
			st.setString(2, code);
			ResultSet rt = st.executeQuery();

			if (rt.next())
				isExist = true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return isExist;
	}

	public boolean checkPDM10R(NumberCode parentCode, String code, Connection con) {

		boolean isExist = false;
		try {

			StringBuffer sql = ERPUtil.getSQLPDM10R(ERPUtil.SQLCHECK);
			PreparedStatement st = con.prepareStatement(sql.toString());

			st.setString(1, parentCode.getParent().getCode());
			st.setString(2, parentCode.getCode());
			st.setString(3, code);
			ResultSet rt = st.executeQuery();

			if (rt.next())
				isExist = true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return isExist;
	}

	public boolean checkPDM11(String code, Connection con) {

		boolean isExist = false;
		try {

			StringBuffer sql = ERPUtil.getSQLPDM11(ERPUtil.SQLCHECK);
			PreparedStatement st = con.prepareStatement(sql.toString());

			st.setString(1, code);
			ResultSet rt = st.executeQuery();

			if (rt.next())
				isExist = true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return isExist;
	}

	public boolean checkPDM12(NumberCode parentCode, String code, Connection con) {

		boolean isExist = false;
		try {

			StringBuffer sql = ERPUtil.getSQLPDM12(ERPUtil.SQLCHECK);
			PreparedStatement st = con.prepareStatement(sql.toString());

			st.setString(1, parentCode.getCode());
			st.setString(2, code);
			ResultSet rt = st.executeQuery();

			if (rt.next())
				isExist = true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return isExist;
	}

	public boolean checkPDM12R(NumberCode parentCode, String code, Connection con) {

		boolean isExist = false;
		try {

			StringBuffer sql = ERPUtil.getSQLPDM12R(ERPUtil.SQLCHECK);
			PreparedStatement st = con.prepareStatement(sql.toString());

			st.setString(1, parentCode.getParent().getCode());
			st.setString(2, parentCode.getCode());
			st.setString(3, code);
			ResultSet rt = st.executeQuery();

			if (rt.next())
				isExist = true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return isExist;
	}

	public boolean checkPDM13(String code, Connection con) {

		boolean isExist = false;
		try {

			StringBuffer sql = ERPUtil.getSQLPDM13(ERPUtil.SQLCHECK);
			PreparedStatement st = con.prepareStatement(sql.toString());

			st.setString(1, code);
			ResultSet rt = st.executeQuery();

			if (rt.next())
				isExist = true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return isExist;
	}

	public boolean checkPDM16(String code, Connection con) {

		boolean isExist = false;
		try {

			StringBuffer sql = ERPUtil.getSQLPDM16(ERPUtil.SQLCHECK);
			PreparedStatement st = con.prepareStatement(sql.toString());

			st.setString(1, code);
			ResultSet rt = st.executeQuery();

			if (rt.next())
				isExist = true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return isExist;
	}

	public boolean checkPDM17(String code, Connection con) {

		boolean isExist = false;
		try {

			StringBuffer sql = ERPUtil.getSQLPDM17(ERPUtil.SQLCHECK);
			PreparedStatement st = con.prepareStatement(sql.toString());

			st.setString(1, code);
			ResultSet rt = st.executeQuery();

			if (rt.next())
				isExist = true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return isExist;
	}

	public boolean duplicationECR(String ecrNumber) throws Exception {

		DBConnectionManager dbmanager = null;
		Connection con = null;
		ResultSet rs = null;
		boolean isExist = false;
		try {
			dbmanager = DBConnectionManager.getInstance();
			con = dbmanager.getConnection(ERP);

			StringBuffer sql = new StringBuffer();
			sql.append("SELECT * FROM PDMECR WHERE EcrNo = ?");

			PreparedStatement st = con.prepareStatement(sql.toString());

			st.setString(1, ecrNumber);
			ResultSet rt = st.executeQuery();

			if (rt.next())
				isExist = true;

		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
			if (dbmanager != null) {
				dbmanager.freeConnection(ERP, con);
			}
		}

		return isExist;
	}

	public QueryResult searchPartHistory(ERPHistory history) {

		QueryResult qr = null;
		try {

			QuerySpec qs = new QuerySpec(ERPPartLinkO.class);

			qs.appendWhere(new SearchCondition(ERPPartLinkO.class, "historyReference.key.id", SearchCondition.EQUAL,
					CommonUtil.getOIDLongValue(history)));

			qr = PersistenceHelper.manager.find(qs);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return qr;
	}

	public Vector erpBom(String number, String version, String eoNumber) {

		System.out.println("::::::::::::::::::: ERP BOM START:::::::::::::::::::");
		DBConnectionManager dbmanager = null;
		Connection con = null;
		ResultSet rs = null;
		Vector vec = new Vector();
		try {
			dbmanager = DBConnectionManager.getInstance();
			con = dbmanager.getConnection(ERP);
			vec = erpBomTree(number, version, vec, con, eoNumber);
		} catch (Exception ex) {
			ex.printStackTrace();

		} finally {
			if (dbmanager != null) {
				dbmanager.freeConnection(ERP, con);
			}
		}
		System.out.println("::::::::::::::::::: ERP BOM END:::::::::::::::::::");
		return vec;
	}

	public Vector erpBomTree(String number, String version, Vector vec, Connection con, String eoNumber)
			throws Exception {

		try {

			StringBuffer sql = new StringBuffer();
			sql.append("SELECT * FROM PDMBOM WHERE ParentItemCode = '" + number + "' AND ParentItemVer = '" + version
					+ "'");
			sql.append(" AND EoNo ='" + eoNumber + "'");
			sql.append(" order by PDMWorkTime asc ");
			System.out.println(sql.toString());
			PreparedStatement st = con.prepareStatement(sql.toString());

			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				ArrayList list = new ArrayList();

				list.add(rs.getObject("ParentItemCode"));
				list.add(rs.getObject("ParentItemVer"));
				list.add(rs.getObject("ItemCode"));
				list.add(rs.getObject("ItemVer"));
				list.add(rs.getObject("MkuQty"));
				System.out.println(
						"::::::::::::: " + list.get(0) + ":" + list.get(1) + ":" + list.get(2) + ":" + list.get(3));
				vec.add(list);

				erpBomTree((String) rs.getObject("ItemCode"), (String) rs.getObject("ItemVer"), vec, con, eoNumber);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}

		return vec;

	}

}
