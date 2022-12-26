package ext.narae.service.erp.beans;

import java.io.File;

import ext.narae.util.DateUtil;
import ext.narae.util.jdf.config.ConfigImpl;

public class ERPUtil {

	/* ERPHistory result */
	public static final String HISTORY_TYPE_CONFIRM = "CONFIRM";
	public static final String HISTORY_TYPE_COMPLETE = "COMPLETE";

	public static final String HISTORY_ECO_TYPE = "ECO";
	public static final String HISTORY_ECR_TYPE = "ECR";

	public static final String HISTORY_STATE_SUCCESS = "SUCCESS";
	public static final String HISTORY_STATE_FAILE = "FAILE";

	public static final String ECO_SEND_SUCCESS = HISTORY_STATE_SUCCESS;
	public static final String ECO_SEND_FAILE = HISTORY_STATE_FAILE;

	public static final String ECR_SEND_SUCCESS = HISTORY_STATE_SUCCESS;
	public static final String ECR_SEND_FAILE = HISTORY_STATE_FAILE;

	public static final String PART_SEND_SUCCESS = HISTORY_STATE_SUCCESS;
	public static final String PART_SEND_FAILE = HISTORY_STATE_FAILE;
	public static final String PART_SEND_NO = "NOT EXIST";

	public static final String BOM_SEND_SUCCESS = HISTORY_STATE_SUCCESS;
	public static final String BOM_SEND_FAILE = HISTORY_STATE_FAILE;
	public static final String BOM_SEND_NO = "NOT EXIST";

	public static final String PDF_SEND_SUCCESS = HISTORY_STATE_SUCCESS;
	public static final String PDF_SEND_FAILE = HISTORY_STATE_FAILE;
	public static final String PDF_SEND_NO = "NOT EXIST";
	public static final String PDF_SEND_WAITING = "WAITING";
	/* ERPPartLinkR ,ERPPartLinkO result : ECR */
	public static final String LINK_PART_RESULT_SUCCESS = HISTORY_STATE_SUCCESS;
	public static final String LINK_PART_RESULT_FAILE = HISTORY_STATE_FAILE;

	/* ERPEulLink result : 을지 */
	public static final String LINK_BOM_RESULT_SUCCESS = HISTORY_STATE_SUCCESS;
	public static final String LINK_BOM_RESULT_FAILE = HISTORY_STATE_FAILE;

	/* ERP */
	public static String Dateformatfull = "yyyyMMddHHmmSS";
	public static String Dateformat = "yyyy/MM/dd";
	public static final String ERP = "erp";
	public static final String MASTER_WORKING = "승인됨";
	public static final String MASTER_COMPLETE = "완료됨";

	// ::::::::::::::::::::::::: ERP TABLE ::::::::::::::::::::::::::::::

	/* 기준 코드 */
	public static final String PDM01 = "PDM01"; // 품목 구성 분류
	public static final String PDM02 = "PDM02"; // 품목 유형 분류
	public static final String PDM03 = "PDM03"; // 제품-장비분류
	public static final String PDM04 = "PDM04"; // 제품-사업군
	public static final String PDM05 = "PDM05"; // 제품-고객사
	public static final String PDM06 = "PDM06"; // 제품-세대
	public static final String PDM06R = "PDM06R"; // 제품-예비코드
	public static final String PDM07 = "PDM07"; // 반제품-Unit
	public static final String PDM08 = "PDM08"; // 반제품-Assy
	public static final String PDM09 = "PDM09"; // 구매-제품군
	public static final String PDM10 = "PDM10"; // 구매-제품기능
	public static final String PDM10R = "PDM10R"; // 구매-예비코드
	public static final String PDM11 = "PDM11"; // 가공-방법
	public static final String PDM12 = "PDM12"; // 가공-분류
	public static final String PDM12R = "PDM12R"; // 가공-예비
	public static final String PDM13 = "PDM13"; // 규격
	public static final String PDM16 = "PDM16"; // 요청유형
	public static final String PDM17 = "PDM17"; // 설변구분

	/* 설변 관련 */
	public static final String ERPPART = "PDM00"; // 품목 코드
	public static final String ERPBOM = "PDMBOM"; // 표준BOM
	public static final String ERPECR = "PDMECR"; // ECR
	public static final String ERPLinkPART = "PDMECRITEM"; // ECR_ECO_대상 품목
	public static final String ERPECO = "PDMECO"; // ECO

	/* ERP 조회 */
	public static final String ERPPROJECT = "CD0107A"; // 프로젝트 (SP) exec CD0107A '','','','',''
	public static final String ERPMAKER = "TCB09"; // Maker(TABLE)

	public static final String SQLCREATE = "CREATE";
	public static final String SQLUPDATE = "UPDATe";
	public static final String SQLDELETE = "DELETE";
	public static final String SQLCHECK = "CHECK";

	/* PDF FILE PATH */
	public static final String PDF_FOLDER = ConfigImpl.getInstance().getString("pdf.folder");
	/* ERP 전송 여부 */
	public static final String ERP_SEND = ConfigImpl.getInstance().getString("erp.send");

	public static String getFolderPath() {

		String folderPath = PDF_FOLDER + File.separator;
		String toDay = DateUtil.getToDay("yyyyMMdd");
		String toYear = toDay.substring(0, 4);
		String toMonth = toDay.substring(4, 8);
		folderPath = folderPath + File.separator + toYear + File.separator + toMonth;

		return folderPath;
	}

	// ::::::::::::::::::::: ERP TABLE QUERY :::::::::::::::::::::

	/* PDM01- 품목 구성 분류 :::: CADCREATOR(도면구성 분류) */
	public static StringBuffer getSQLPDM01(String sqlType) {
		StringBuffer sql = new StringBuffer();

		if (sqlType.equals(SQLCREATE)) {
			sql.append("INSERT INTO " + PDM01).append("(ItemTGubn,ItemTGubnName)").append(" VALUES (?,?)");
		} else if (sqlType.equals(SQLUPDATE)) {
			sql.append("UPDATE  " + PDM01).append("SET ItemTGubnName = ?").append(" WHERE ItemTGubn = ?");
		} else if (sqlType.equals(SQLDELETE)) {
			sql.append("DELETE  " + PDM01).append(" WHERE ItemTGubn = ?");
		} else if (sqlType.equals(SQLCHECK)) {
			sql.append("SELECT * FROM  " + PDM01).append(" WHERE ItemTGubn = ?");
		}

		return sql;
	}

	/* PDM02- 품목 유형 분류 :::: CADATTRIBUTE(도면 분류 체계)- 1Level */
	public static StringBuffer getSQLPDM02(String sqlType) {

		StringBuffer sql = new StringBuffer();

		if (sqlType.equals(SQLCREATE)) {
			sql.append("INSERT INTO " + PDM02).append("(ItemType,ItemTypeName)").append(" VALUES (?,?)");
		} else if (sqlType.equals(SQLUPDATE)) {
			sql.append("UPDATE  " + PDM02).append(" SET ItemTypeName = ?").append(" WHERE ItemType = ?");
		} else if (sqlType.equals(SQLDELETE)) {
			sql.append("DELETE  " + PDM02).append(" WHERE ItemType = ?");
		} else if (sqlType.equals(SQLCHECK)) {
			sql.append("SELECT * FROM  " + PDM02).append(" WHERE ItemType = ?");
		}

		return sql;
	}

	/* PDM03- 제품-장비분류 :::: SCLASS1(제품 중분류1) */
	public static StringBuffer getSQLPDM03(String sqlType) {

		StringBuffer sql = new StringBuffer();

		if (sqlType.equals(SQLCREATE)) {
			sql.append("INSERT INTO " + PDM03).append("(ItemDev,ItemDevName)").append(" VALUES (?,?)");
		} else if (sqlType.equals(SQLUPDATE)) {
			sql.append("UPDATE  " + PDM03).append(" SET ItemDevName = ?").append(" WHERE ItemDev = ?");
		} else if (sqlType.equals(SQLDELETE)) {
			sql.append("DELETE  " + PDM03).append(" WHERE ItemDev = ?");
		} else if (sqlType.equals(SQLCHECK)) {
			sql.append("SELECT * FROM  " + PDM03).append(" WHERE ItemDev = ?");
		}

		return sql;
	}

	/* PDM04- 제품-사업군 :::: SBUSINESS(제품사업부) */
	public static StringBuffer getSQLPDM04(String sqlType) {

		StringBuffer sql = new StringBuffer();

		if (sqlType.equals(SQLCREATE)) {
			sql.append("INSERT INTO " + PDM04).append("(ItemBsn,ItemBsnName)").append(" VALUES (?,?)");
		} else if (sqlType.equals(SQLUPDATE)) {
			sql.append("UPDATE  " + PDM04).append(" SET ItemBsnName = ?").append(" WHERE ItemBsn = ?");
		} else if (sqlType.equals(SQLDELETE)) {
			sql.append("DELETE  " + PDM04).append(" WHERE ItemBsn = ?");
		} else if (sqlType.equals(SQLCHECK)) {
			sql.append("SELECT * FROM  " + PDM04).append(" WHERE ItemBsn = ?");
		}

		return sql;
	}

	/* PDM05- 제품-고객사 :::: SCUSTOMER(제품고객사) */
	public static StringBuffer getSQLPDM05(String sqlType) {

		StringBuffer sql = new StringBuffer();

		if (sqlType.equals(SQLCREATE)) {
			sql.append("INSERT INTO " + PDM05).append("(ItemCstm,ItemCstmName)").append(" VALUES (?,?)");
		} else if (sqlType.equals(SQLUPDATE)) {
			sql.append("UPDATE  " + PDM05).append(" SET ItemCstmName = ?").append(" WHERE ItemCstm = ?");
		} else if (sqlType.equals(SQLDELETE)) {
			sql.append("DELETE  " + PDM05).append(" WHERE ItemCstm = ?");
		} else if (sqlType.equals(SQLCHECK)) {
			sql.append("SELECT * FROM  " + PDM05).append(" WHERE ItemCstm = ?");
		}

		return sql;
	}

	/* PDM06- 제품-세대 :::: SCLASS2(제품중분류2) */
	public static StringBuffer getSQLPDM06(String sqlType) {

		StringBuffer sql = new StringBuffer();

		if (sqlType.equals(SQLCREATE)) {
			sql.append("INSERT INTO " + PDM06).append("(ItemGen,ItemGenName)").append(" VALUES (?,?)");
		} else if (sqlType.equals(SQLUPDATE)) {
			sql.append("UPDATE  " + PDM06).append(" SET ItemGenName = ?").append(" WHERE ItemGen = ?");
		} else if (sqlType.equals(SQLDELETE)) {
			sql.append("DELETE  " + PDM06).append(" WHERE ItemGen = ?");
		} else if (sqlType.equals(SQLCHECK)) {
			sql.append("SELECT * FROM  " + PDM06).append(" WHERE ItemGen = ?");
		}

		return sql;
	}

	/* PDM06R- 제품-예비 :::: SCLASS3(제품중분류3) */
	public static StringBuffer getSQLPDM06R(String sqlType) {

		StringBuffer sql = new StringBuffer();

		if (sqlType.equals(SQLCREATE)) {
			sql.append("INSERT INTO " + PDM06R).append("(ItemReserve,ItemReserveName)").append(" VALUES (?,?)");
		} else if (sqlType.equals(SQLUPDATE)) {
			sql.append("UPDATE  " + PDM06R).append(" SET ItemReserveName = ?").append(" WHERE ItemReserve = ?");
		} else if (sqlType.equals(SQLDELETE)) {
			sql.append("DELETE  " + PDM06R).append(" WHERE ItemReserve = ?");
		} else if (sqlType.equals(SQLCHECK)) {
			sql.append("SELECT * FROM  " + PDM06R).append(" WHERE ItemReserve = ?");
		}

		return sql;
	}

	/* PDM07- 반제품-Unit :::: CADATTRIBUTE( parent A ,2level ) */
	public static StringBuffer getSQLPDM07(String sqlType) {

		StringBuffer sql = new StringBuffer();

		if (sqlType.equals(SQLCREATE)) {
			sql.append("INSERT INTO " + PDM07).append("(ItemUnit,ItemUnitName)").append(" VALUES (?,?)");
		} else if (sqlType.equals(SQLUPDATE)) {
			sql.append("UPDATE  " + PDM07).append(" SET ItemUnitName = ?").append(" WHERE ItemUnit = ?");
		} else if (sqlType.equals(SQLDELETE)) {
			sql.append("DELETE  " + PDM07).append(" WHERE ItemUnit = ?");
		} else if (sqlType.equals(SQLCHECK)) {
			sql.append("SELECT * FROM  " + PDM07).append(" WHERE ItemUnit = ?");
		}
		System.out.println(sql);
		return sql;
	}

	/* PDM08- 반제품-Assy :::: CADATTRIBUTE( parent A ,3level ) */
	public static StringBuffer getSQLPDM08(String sqlType) {

		StringBuffer sql = new StringBuffer();

		if (sqlType.equals(SQLCREATE)) {
			sql.append("INSERT INTO " + PDM08).append("(ItemUnit,ItemAss2,ItemAss2Name)").append(" VALUES (?,?,?)");
		} else if (sqlType.equals(SQLUPDATE)) {
			sql.append("UPDATE  " + PDM08).append(" SET ItemUnitName = ?").append(" WHERE ItemUnit = ?")
					.append(" AND ItemAss2 = ?");
		} else if (sqlType.equals(SQLDELETE)) {
			sql.append("DELETE  " + PDM08).append(" WHERE ItemUnit = ?").append(" AND ItemAss2 = ?");
		} else if (sqlType.equals(SQLCHECK)) {
			sql.append("SELECT * FROM  " + PDM08).append(" WHERE ItemUnit = ?").append(" AND ItemAss2 = ?");
		}

		return sql;
	}

	/* PDM09- 구매-제품군 :::: CADATTRIBUTE( parent B ,2level ) */
	public static StringBuffer getSQLPDM09(String sqlType) {

		StringBuffer sql = new StringBuffer();

		if (sqlType.equals(SQLCREATE)) {
			sql.append("INSERT INTO " + PDM09).append("(ItemBuy,ItemBuyName)").append(" VALUES (?,?)");
		} else if (sqlType.equals(SQLUPDATE)) {
			sql.append("UPDATE  " + PDM09).append(" SET ItemBuyName = ?").append(" WHERE ItemBuy = ?");
		} else if (sqlType.equals(SQLDELETE)) {
			sql.append("DELETE  " + PDM09).append(" WHERE ItemBuy = ?");
		} else if (sqlType.equals(SQLCHECK)) {
			sql.append("SELECT * FROM  " + PDM09).append(" WHERE ItemBuy = ?");
		}

		return sql;
	}

	/* PDM10- 구매-제품기능 :::: CADATTRIBUTE( parent B ,3level ) */
	public static StringBuffer getSQLPDM10(String sqlType) {

		StringBuffer sql = new StringBuffer();

		if (sqlType.equals(SQLCREATE)) {
			sql.append("INSERT INTO " + PDM10).append("(ItemBuy,ItemBuyFn,ItemBuyFnName)").append(" VALUES (?,?,?)");
		} else if (sqlType.equals(SQLUPDATE)) {
			sql.append("UPDATE  " + PDM10).append(" SET ItemBuyFnName = ?").append(" WHERE ItemBuy = ?")
					.append(" AND ItemBuyFn = ?");
		} else if (sqlType.equals(SQLDELETE)) {
			sql.append("DELETE  " + PDM10).append(" WHERE ItemBuy = ?").append(" AND ItemBuyFn = ?");
		} else if (sqlType.equals(SQLCHECK)) {
			sql.append("SELECT * FROM  " + PDM10).append(" WHERE ItemBuy = ?").append(" AND ItemBuyFn = ?");
		}

		return sql;
	}

	/* PDM10- 구매-예비 :::: CADATTRIBUTE( parent B ,4level ) */
	public static StringBuffer getSQLPDM10R(String sqlType) {

		StringBuffer sql = new StringBuffer();

		if (sqlType.equals(SQLCREATE)) {
			sql.append("INSERT INTO " + PDM10R).append("(ItemBuy,ItemBuyFn,ItemBuyReserve,ItemBuyReserveName)")
					.append(" VALUES (?,?,?,?)");
		} else if (sqlType.equals(SQLUPDATE)) {
			sql.append("UPDATE  " + PDM10R).append(" SET ItemBuyReserveName = ?").append(" WHERE ItemBuy = ?")
					.append(" AND ItemBuyFn = ?").append(" AND ItemBuyReserve = ?");
		} else if (sqlType.equals(SQLDELETE)) {
			sql.append("DELETE  " + PDM10R).append(" WHERE ItemBuy = ?").append(" AND ItemBuyFn = ?")
					.append(" AND ItemBuyReserve = ?");
		} else if (sqlType.equals(SQLCHECK)) {
			sql.append("SELECT * FROM  " + PDM10R).append(" WHERE ItemBuy = ?").append(" AND ItemBuyFn = ?")
					.append(" AND ItemBuyReserve = ?");
		}

		return sql;
	}

	/* PDM11- 가공-방법 :::: CADATTRIBUTE( parent P ,2level ) */
	public static StringBuffer getSQLPDM11(String sqlType) {

		StringBuffer sql = new StringBuffer();

		if (sqlType.equals(SQLCREATE)) {
			sql.append("INSERT INTO " + PDM11).append("(ItemAdd,ItemAddName)").append(" VALUES (?,?)");
		} else if (sqlType.equals(SQLUPDATE)) {
			sql.append("UPDATE  " + PDM11).append(" SET ItemAddName = ?").append(" WHERE ItemAdd = ?");
		} else if (sqlType.equals(SQLDELETE)) {
			sql.append("DELETE  " + PDM11).append(" WHERE ItemAdd = ?");
		} else if (sqlType.equals(SQLCHECK)) {
			sql.append("SELECT * FROM  " + PDM11).append(" WHERE ItemAdd = ?");
		}

		return sql;
	}

	/* PDM12- 가공-분류 :::: CADATTRIBUTE( parent P ,3level ) */
	public static StringBuffer getSQLPDM12(String sqlType) {

		StringBuffer sql = new StringBuffer();

		if (sqlType.equals(SQLCREATE)) {
			sql.append("INSERT INTO " + PDM12).append("(ItemAdd,ItemAddType,ItemAddTypeName)")
					.append(" VALUES (?,?,?)");
		} else if (sqlType.equals(SQLUPDATE)) {
			sql.append("UPDATE  " + PDM12).append(" SET ItemAddTypeName = ?").append(" WHERE ItemAdd = ?")
					.append(" AND ItemAddType = ?");
		} else if (sqlType.equals(SQLDELETE)) {
			sql.append("DELETE  " + PDM12).append(" WHERE ItemAdd = ?").append(" AND ItemAddType = ?");
		} else if (sqlType.equals(SQLCHECK)) {
			sql.append("SELECT * FROM  " + PDM12).append(" WHERE ItemAdd = ?").append(" AND ItemAddType = ?");
		}

		return sql;
	}

	/* PDM12R- 가공-예비 :::: CADATTRIBUTE( parent P ,4level ) */
	public static StringBuffer getSQLPDM12R(String sqlType) {

		StringBuffer sql = new StringBuffer();

		if (sqlType.equals(SQLCREATE)) {
			sql.append("INSERT INTO " + PDM12R).append("(ItemAdd,ItemAddType,ItemAddReserve,ItemAddReserveName)")
					.append(" VALUES (?,?,?,?)");
		} else if (sqlType.equals(SQLUPDATE)) {
			sql.append("UPDATE  " + PDM12R).append(" SET ItemAddTypeName = ?").append(" WHERE ItemAdd = ?")
					.append(" AND ItemAddType = ?").append(" AND ItemAddReserve = ?");
		} else if (sqlType.equals(SQLDELETE)) {
			sql.append("DELETE  " + PDM12R).append(" WHERE ItemAdd = ?").append(" AND ItemAddType = ?")
					.append(" AND ItemAddReserve = ?");
		} else if (sqlType.equals(SQLCHECK)) {
			sql.append("SELECT * FROM  " + PDM12R).append(" WHERE ItemAdd = ?").append(" AND ItemAddType = ?")
					.append(" AND ItemAddReserve = ?");
		}

		return sql;
	}

	/* PDM13- 규격 :::: SPEC( 규격) */
	public static StringBuffer getSQLPDM13(String sqlType) {

		StringBuffer sql = new StringBuffer();

		if (sqlType.equals(SQLCREATE)) {
			sql.append("INSERT INTO " + PDM13).append("(ItemSpec,ItemSpecName)").append(" VALUES (?,?)");
		} else if (sqlType.equals(SQLUPDATE)) {
			sql.append("Update " + PDM13).append(" set ItemSpecName = ?").append(" where ItemSpec =? ");
		} else if (sqlType.equals(SQLDELETE)) {
			sql.append("Delete " + PDM13).append(" where ItemSpec =? ");
		} else if (sqlType.equals(SQLCHECK)) {
			sql.append("select * from " + PDM13);
			sql.append(" where ItemSpec = ?");
		}

		return sql;
	}

	/* PDM16- 요청유형 :::: CHANGEPURPOSE(변경사유) */
	public static StringBuffer getSQLPDM16(String sqlType) {

		StringBuffer sql = new StringBuffer();

		if (sqlType.equals(SQLCREATE)) {
			sql.append("INSERT INTO " + PDM16).append("(Type,TypeName)").append(" VALUES (?,?)");
		} else if (sqlType.equals(SQLUPDATE)) {
			sql.append("Update " + PDM16).append(" set TypeName = ?").append(" where Type = ?");
		} else if (sqlType.equals(SQLDELETE)) {
			sql.append("Delete " + PDM16).append(" where Type = ?");
		} else if (sqlType.equals(SQLCHECK)) {
			sql.append("select * from " + PDM16);
			sql.append(" where Type = ?");
		}

		return sql;
	}

	/* PDM17- 설변구분 :::: EOTYPE(설변구분) */
	public static StringBuffer getSQLPDM17(String sqlType) {

		StringBuffer sql = new StringBuffer();

		if (sqlType.equals(SQLCREATE)) {
			sql.append("INSERT INTO " + PDM17).append("(Div,DivName)").append(" VALUES (?,?)");
		} else if (sqlType.equals(SQLUPDATE)) {
			sql.append("Update " + ERPUtil.PDM17).append(" set DivName = ?").append(" where Div = ? ");
		} else if (sqlType.equals(SQLDELETE)) {
			sql.append("Delete " + ERPUtil.PDM17).append(" where Div = ? ");
		} else if (sqlType.equals(SQLCHECK)) {
			sql.append("select * from " + ERPUtil.PDM17);
			sql.append(" where Div = ?");
		}

		return sql;
	}

}
