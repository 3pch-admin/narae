package ext.narae;

import ext.narae.util.CommonUtil;
import ext.narae.util.code.NumberCode;
import ext.narae.util.code.NumberCodeType;
import ext.narae.util.code.beans.NumberCodeHelper;
import wt.fc.PersistenceHelper;

public class Test6 {

	public static void main(String[] args) throws Exception {

		NumberCode parent = (NumberCode) CommonUtil.getObject("ext.narae.util.code.NumberCode:854824");

		String[] c = new String[] { "AF", "SC", "RC", "VC", "HV", "LA", "IP", "OV", "HC", "DV", "CL", "FA", "SM", "PC",
				"PT", "TC", "SI", "TO" };
		String[] n = new String[] { "Air Floating Coater", "Slit Die Coater", "R2R Coater", "Vaccum Chamber Dryer",
				"High Temp. Vacuum Chamber Dryer", "Laminator", "Inkjet Printer", "Oven", "HPCP", "Developer",
				"Cleaner", "물류 (Factory Automation)", "Stacking Machine", "Pre Cure", "Pre Top Cure", "Top Cure",
				"Side Cure", "Turn Over" };

		for (int i = 0; i < c.length; i++) {
			NumberCode nn = NumberCodeHelper.manager.getNumberCode("CADATTRIBUTE", c[i]);
			if (nn == null) {
				NumberCode numberCode = NumberCode.newNumberCode();
				numberCode.setCodeType(NumberCodeType.toNumberCodeType("CADATTRIBUTE"));
				numberCode.setParent(parent);
				numberCode.setCode(c[i]);
				numberCode.setName(n[i]);
				numberCode.setDescription(c[i]);
				PersistenceHelper.manager.save(numberCode);
			}
		}
		System.exit(0);
	}
}
