package ext.narae;

import ext.narae.util.CommonUtil;
import wt.fc.IdentityHelper;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartMasterIdentity;

public class Test4 {

	public static void main(String[] args) throws Exception {
		String oid = "wt.part.WTPart:113003289";
		WTPart part = (WTPart) CommonUtil.getObject(oid);
		WTPartMaster m = (WTPartMaster) part.getMaster();
		WTPartMasterIdentity identity = (WTPartMasterIdentity) m.getIdentificationObject();
		identity.setNumber("NB-05-0500-10002");
		IdentityHelper.service.changeIdentity(m, identity);
	}

}
