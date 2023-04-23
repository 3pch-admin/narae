package ext.narae;

import ext.narae.service.org.Department;
import ext.narae.service.org.People;
import ext.narae.util.CommonUtil;
import wt.epm.EPMDocumentHelper;
import wt.epm.EPMDocumentMaster;
import wt.epm.EPMDocumentMasterIdentity;
import wt.fc.Identified;
import wt.fc.IdentityHelper;
import wt.fc.PersistenceHelper;
import wt.fc.collections.WTKeyedHashMap;
import wt.fc.collections.WTKeyedMap;
import wt.part.WTPartMaster;
import wt.part.WTPartMasterIdentity;

public class Test5 {

	public static void main(String[] args) throws Exception {

		String oid = "wt.part.WTPartMaster:139706240";
		WTPartMaster master = (WTPartMaster)CommonUtil.getObject(oid);

		WTPartMasterIdentity epmdocumentmasteridentity = (WTPartMasterIdentity) master
				.getIdentificationObject();
		
		//np-00-mass-10146-l001.prt
		
		epmdocumentmasteridentity.setNumber("np-00-mass-10147".toUpperCase());
		epmdocumentmasteridentity.setName("PCW BLOCK");
		IdentityHelper.service.changeIdentity((Identified) master, epmdocumentmasteridentity);
		
//		WTKeyedMap map = new WTKeyedHashMap();
//
//		map.put(master, "np-00-smss-95234.prt");
//		EPMDocumentHelper.service.changeCADName(map);
		
		System.exit(0);
	}

}
