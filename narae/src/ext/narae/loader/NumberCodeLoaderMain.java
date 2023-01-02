package ext.narae.loader;

import java.io.File;

import ext.narae.util.JExcelUtil;
import ext.narae.util.StringUtil;
import ext.narae.util.code.NumberCode;
import ext.narae.util.code.NumberCodeType;
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
			System.out.println("Load NumberCode Excel..");
			System.exit(0);
		}

		NumberCodeLoaderMain main = new NumberCodeLoaderMain();
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
					String code = JExcelUtil.getContent(cell, 0).trim();
					String codeType = JExcelUtil.getContent(cell, 1).trim();
					String name = JExcelUtil.getContent(cell, 2).trim();
					String parent = JExcelUtil.getContent(cell, 3).trim();

//					System.out.println(code);
					if (StringUtil.checkString(code)) {
						NumberCode numberCode = NumberCode.newNumberCode();
						numberCode.setCode(code);
						numberCode.setCodeType(NumberCodeType.toNumberCodeType(codeType));
						numberCode.setEngName(name);
						numberCode.setName(name);
						numberCode.setDescription(name);

						if (StringUtil.checkString(parent)) {

						}

						PersistenceHelper.manager.save(numberCode);

						System.out.println("Load NumberCode Complete = " + numberCode.getName());
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
