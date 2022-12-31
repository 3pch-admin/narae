package ext.narae.util.code.beans;

import java.io.File;
import java.util.Hashtable;
import java.util.StringTokenizer;

import ext.narae.util.JExcelUtil;
import ext.narae.util.StringUtil;
import ext.narae.util.code.NumberCode;
import ext.narae.util.code.NumberCodeType;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.method.RemoteMethodServer;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;

public class CodeLoader {

	/**
	 * @param args
	 */
	public static void main(final String[] args) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("Initializing...");
		if ((args == null) || (args.length < 3)) {
			System.out.println("CodeLoader version 1.0\n Usage [Excel File Path] [User Name] [User Password]]");
			System.exit(0);
		}

		setUser(args[1], args[2]);
		new CodeLoader().loadCode(args[0]);
	}

	public static void setUser(final String id, final String pw) {
		RemoteMethodServer.getDefault().setUserName(id);
		RemoteMethodServer.getDefault().setPassword(pw);
	}

	public void loadCode(final String filePath) throws Exception {

		// String sWtHome =
		// wt.util.WTProperties.getLocalProperties().getProperty("wt.home", "");
		// String sFilePath = sWtHome + "/loadFiles/e3ps/ProjectTemplate.xls" ;
		// String sFilePath = sWtHome + "/loadFiles/" + filePath ;

		File newfile = new File(filePath);
		Workbook wb = JExcelUtil.getWorkbook(newfile);
		Sheet[] sheets = wb.getSheets();

		for (int i = 0; i < sheets.length; i++) {
			int rows = sheets[i].getRows();

			Cell[] cell = sheets[i].getRow(1);

			NumberCodeType ctype = null;
			NumberCodeType cparenttype = null;
			Hashtable hash = new Hashtable();

			for (int j = 1; j < rows; j++) {
				String codeType = "";
				String codeID = "";
				String codeName = "";
				String codeDesc = "";
				String codeEngName = "";
				String parentCode = "";
				String parentcodeType = "";
				String key = "저장";
				cell = sheets[i].getRow(j);
				codeType = JExcelUtil.getContent(cell, 0).trim();
				codeID = JExcelUtil.getContent(cell, 1).trim();
				codeName = JExcelUtil.getContent(cell, 2).trim();
				codeEngName = JExcelUtil.getContent(cell, 3).trim();
				codeDesc = JExcelUtil.getContent(cell, 4).trim();
				parentCode = JExcelUtil.getContent(cell, 5).trim();
				parentcodeType = JExcelUtil.getContent(cell, 6).trim();
				if (!codeType.equals("")) {
					ctype = NumberCodeType.toNumberCodeType(codeType);
				}
				if (!parentcodeType.equals("")) {
					cparenttype = NumberCodeType.toNumberCodeType(parentcodeType);
				}

				if (ctype != null) {
					NumberCode nCode = getPartCode(codeID, ctype.toString(), parentCode);

					if (nCode == null) {
						nCode = NumberCode.newNumberCode();
						nCode.setCodeType(ctype);
						key = " 생성";
					} else {
						hash.remove(nCode.getPersistInfo().getObjectIdentifier().getStringValue());
					}
					nCode.setCode(codeID);
					nCode.setName(codeName);
					nCode.setEngName(StringUtil.checkNull(codeEngName));
					nCode.setDescription(codeDesc);
					nCode.setDisabled(false);
					if (null != cparenttype) {
						NumberCode pCode = partParentCode(cparenttype.toString(), parentCode);
						// if(parentCode.equals("B") && codeID.equals("01"))
						// System.out.println("pCode------->>>"+pCode.getName());
						if (pCode != null)
							nCode.setParent(pCode);
					}
					PersistenceHelper.manager.save(nCode);

					System.out.println(">>" + codeID + key + " 되었습니다.");
				} else {
					System.out
							.println("NumberCodeRB 에 등록된 코드가 아닙니다. CodeType:" + ctype.toString() + ", Code:" + codeID);
				}

			} // for j

		} // for i

		System.out.println("###########################");
		System.out.println("Code 데이터 로딩에 성공하였습니다.");
		System.out.println("###########################");

	}// class

	public NumberCode partParentCode(String codeType, String parentLocation) {
		try {
			if (parentLocation != null && parentLocation.length() > 0) {
				String pCode = "";
				int pCodeSize = 0;
				if (parentLocation.indexOf("/") > -1) {
					StringTokenizer pCodeToken = new StringTokenizer(parentLocation, "/");
					pCodeSize = pCodeToken.countTokens() - 1;
					while (pCodeToken.hasMoreTokens()) {
						pCode = pCodeToken.nextToken();
					}
				} else {
					pCode = parentLocation;
				}

				QuerySpec select = new QuerySpec(NumberCode.class);
				select.appendWhere(new SearchCondition(NumberCode.class, "codeType", "=", codeType), new int[] { 0 });
				select.appendAnd();
				select.appendWhere(new SearchCondition(NumberCode.class, "code", "=", pCode), new int[] { 0 });
				QueryResult result = PersistenceHelper.manager.find(select);

				while (result.hasMoreElements()) {
					NumberCode parentCode = (NumberCode) result.nextElement();
					String codeLocation = parentCode.getCode();
					NumberCode tempParentCode = parentCode;
					for (int i = 0; i < pCodeSize; i++) {
						if (tempParentCode.getParent() != null) {
							tempParentCode = tempParentCode.getParent();
							codeLocation = tempParentCode.getCode() + "/" + codeLocation;
						}
					}

					if (pCodeSize == 0) {
						if (tempParentCode.getParent() == null) {
							if (parentLocation.equals(codeLocation))
								return parentCode;
						}
					} else {
						if (parentLocation.equals(codeLocation))
							return parentCode;
					}
				}
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}

		return null;
	}

	public NumberCode getPartCode(String codeID, String codeType, String parentLocation) {
		if (parentLocation != null && parentLocation.length() > 0) {
			return partParentCode(codeType, parentLocation + "/" + codeID);
		} else {
			try {
				QuerySpec query = new QuerySpec(NumberCode.class);
				query.appendWhere(new SearchCondition(NumberCode.class, "codeType", "=", codeType), new int[] { 0 });
				query.appendAnd();
				query.appendWhere(new SearchCondition(NumberCode.class, "disabled", SearchCondition.IS_FALSE),
						new int[] { 0 });
				query.appendAnd();
				query.appendWhere(new SearchCondition(NumberCode.class, "parentReference.key.id",
						SearchCondition.EQUAL, (long) 0), new int[] { 0 });
				query.appendAnd();
				query.appendWhere(new SearchCondition(NumberCode.class, "code", "=", codeID), new int[] { 0 });
				QueryResult result = PersistenceHelper.manager.find(query);

				while (result.hasMoreElements()) {
					NumberCode partCode = (NumberCode) result.nextElement();
					return partCode;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
			return null;
		}
	}

}
