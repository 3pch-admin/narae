package ext.narae.loader;

import java.io.File;

import ext.narae.util.JExcelUtil;
import ext.narae.util.StringUtil;
import ext.narae.util.code.NumberCode;
import ext.narae.util.code.NumberCodeType;
import ext.narae.util.code.beans.NumberCodeHelper;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import wt.fc.PersistenceHelper;
import wt.method.RemoteMethodServer;

public class NumberCodeLoaderMain {

	public static void main(String[] args) throws Exception {

		RemoteMethodServer.getDefault().setUserName("wcadmin");
		RemoteMethodServer.getDefault().setPassword("n@r@epdm");

		if (args.length == 0) {
			System.out.println("Excel File Not Set..");
			System.exit(0);
		}

		NumberCodeLoaderMain main = new NumberCodeLoaderMain();
		main.load(args[0]);
		System.out.println("종료!");
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
					String code = JExcelUtil.getContent(cell, 0).trim();
					String name = JExcelUtil.getContent(cell, 1).trim();
					String codeType = JExcelUtil.getContent(cell, 2).trim();

					if (!StringUtil.checkString(codeType)) {
						continue;
					}

					NumberCode numberCode = NumberCodeHelper.manager.getNumberCode(codeType, code);
					if (numberCode == null) {
						numberCode = NumberCode.newNumberCode();
						numberCode.setCode(code);
						numberCode.setCodeType(NumberCodeType.toNumberCodeType(codeType));
						numberCode.setDescription(name);
						numberCode.setEngName(name);
						numberCode.setName(name);
						numberCode.setParent(null);
						PersistenceHelper.manager.save(numberCode);
					}
				}
			}

			for (int i = 0; i < sheets.length; i++) {
				int rows = sheets[i].getRows();
				for (int j = 1; j < rows; j++) {
					Cell[] cell = sheets[i].getRow(j);
					String code = JExcelUtil.getContent(cell, 0).trim();
					String codeType = JExcelUtil.getContent(cell, 2).trim();
					String parentCode = JExcelUtil.getContent(cell, 3).trim();
					String parentCodeType = JExcelUtil.getContent(cell, 4).trim();

					if (!StringUtil.checkString(codeType)) {
						continue;
					}

					if (StringUtil.checkString(parentCode)) {
						NumberCode numberCode = NumberCodeHelper.manager.getNumberCode(codeType, code);
						NumberCode parent = NumberCodeHelper.manager.getNumberCode(parentCodeType, parentCode);
						numberCode.setParent(parent);
						System.out.println("parent=" + parent.getCode() + ", = " + parent.getName());
						PersistenceHelper.manager.modify(numberCode);
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
}
