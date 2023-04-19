package ext.narae.service.drawing.beans;

import ext.narae.util.CommonUtil;
import wt.epm.EPMCADNamespace;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentHelper;
import wt.epm.EPMDocumentMaster;
import wt.epm.EPMDocumentMasterIdentity;
import wt.fc.Identified;
import wt.fc.IdentityHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.collections.WTKeyedHashMap;
import wt.fc.collections.WTKeyedMap;
import wt.part.WTPart;
import wt.part.WTPartMasterIdentity;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class CadInfoChange {

	public CadInfoChange() {
	}

	public boolean epmCadInfoChange(EPMDocument epmdocument, String s, String s1, String s2)
			throws WTException, WTPropertyVetoException {
		System.out.println(
				"\uBC88\uD638 \uC774\uB984 cad \uC18D\uC131 \uBCC0\uACBD \uD074\uB798\uC2A4 \uC2DC\uC791 : E3PSCadInfoChange");
		boolean flag = false;
		boolean flag1 = false;
		try {
			System.out.println((new StringBuilder()).append(epmdocument.getNumber()).append("||").append(s)
					.append("\tcheck-").append(!epmdocument.getNumber().equals("NA-1K-L33000-015_2D")).toString());
			if (!epmdocument.getNumber().equals("NA-1K-L33000-015_2D")) {
				System.out.println(
						(new StringBuilder()).append(epmdocument.getName()).append("||").append(s1).toString());
				System.out.println(
						(new StringBuilder()).append(epmdocument.getCADName()).append("||").append(s2).toString());
				if (!epmdocument.getNumber().equals(s) || !epmdocument.getName().equals(s1)
						|| epmdocument.getCADName().equals(s2))
					flag = true;
				System.out.println((new StringBuilder())
						.append("epmCadInfoChange isChange \uC774\uB984 \uBCC0\uACBD \uC548\uD568. ================== ")
						.append(flag).toString());
				if (flag) {
					EPMDocumentMaster master = (EPMDocumentMaster) epmdocument.getMaster();
					EPMDocumentMasterIdentity epmdocumentmasteridentity = (EPMDocumentMasterIdentity) master
							.getIdentificationObject();
					epmdocumentmasteridentity.setNumber(s.trim());
					epmdocumentmasteridentity.setName(s1.trim());
					IdentityHelper.service.changeIdentity((Identified) master, epmdocumentmasteridentity);
					master = (EPMDocumentMaster) PersistenceHelper.manager.refresh(master);
					System.out.println(
							(new StringBuilder()).append("number ===================== ").append(s).toString());
					System.out
							.println((new StringBuilder()).append("name ===================== ").append(s1).toString());
//					epmdocumentmasteridentity.setCADName(s2.toLowerCase());
//					IdentityHelper.service.changeIdentity(identified, epmdocumentmasteridentity);
//					epmdocument = PersistenceHelper.manager.refresh(epmdocument);
					WTKeyedMap map = new WTKeyedHashMap();

					map.put(master, s2.toLowerCase());
					EPMDocumentHelper.service.changeCADName(map);
					cadNameSpaceChange(epmdocument);
				}
				flag1 = true;
				System.out.println(
						(new StringBuilder()).append("isNumber ===================== ").append(flag1).toString());
				System.out.println(
						"\uBC88\uD638 \uC774\uB984 cad \uC18D\uC131 \uBCC0\uACBD \uD074\uB798\uC2A4 \uC885\uB8CC : E3PSCadInfoChange");
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			flag1 = false;
		}
		return flag1;
	}

	public boolean epmInfoChange(EPMDocument epmdocument, String s, String s1)
			throws WTException, WTPropertyVetoException {
		boolean flag = false;
		boolean flag1 = false;
		try {
			if (!epmdocument.getNumber().equals(s) || !epmdocument.getName().equals(s1))
				flag = true;
			System.out.println("epmInfoCl98" + "hange \uC774\uB984 \uBCC0\uACBD \uC548\uD568 ");
			if (flag) {
				System.out.println((new StringBuilder()).append("epm ::: > ").append(epmdocument.getNumber())
						.append("epm.getName=").append(epmdocument.getName()).append("\tnumber=").append(s)
						.append("\tname=").append(s1).toString());

				epmdocument = (EPMDocument) PersistenceHelper.manager.refresh(epmdocument);

				Identified identified = (Identified) epmdocument.getMaster();
				EPMDocumentMasterIdentity epmdocumentmasteridentity = (EPMDocumentMasterIdentity) identified
						.getIdentificationObject();
				epmdocumentmasteridentity.setNumber(s.trim());
				epmdocumentmasteridentity.setName(s1.trim());
				IdentityHelper.service.changeIdentity(identified, epmdocumentmasteridentity);
				PersistenceHelper.manager.refresh(epmdocument);
			}
			flag1 = true;
		} catch (Exception exception) {
			exception.printStackTrace();
			flag1 = false;
		}
		return flag1;
	}

	public boolean partInfoChange(WTPart wtpart, String s, String s1) throws WTException, WTPropertyVetoException {
		boolean flag = false;
		boolean flag1 = false;
		try {
			if (!wtpart.getNumber().equals(s) || !wtpart.getName().equals(s1))
				flag = true;
			System.out.println("epmInfoChange \uC774\uB984 \uBCC0\uACBD \uC548\uD568 ");
			if (flag) {
				wtpart = (WTPart) PersistenceHelper.manager.refresh(wtpart);
				wt.part.WTPartMaster wtpartmaster = wtpart.getMaster();
				System.out.print("wtpartmaster = " + wtpartmaster);
				WTPartMasterIdentity wtpartmasteridentity = (WTPartMasterIdentity) wtpartmaster
						.getIdentificationObject();
				wtpartmasteridentity.setNumber(s.trim());
				wtpartmasteridentity.setName(s1.trim());
				IdentityHelper.service.changeIdentity(wtpartmaster, wtpartmasteridentity);
				PersistenceHelper.manager.refresh(wtpart);
			}
			flag1 = true;
		} catch (Exception exception) {
			flag1 = false;
		}
		return flag1;
	}

//	public void cadNameChange(EPMDocument epmdocument, String s) throws WTException, WTPropertyVetoException {
//		String s1 = epmdocument.getCADName();
//		boolean flag = false;
//		if (!s1.equals(s))
//			flag = true;
//		if (flag) {
//			EPMDocumentMaster master = (EPMDocumentMaster) epmdocument.getMaster();
////			Identified identified = (Identified) epmdocument.getMaster();
//			EPMDocumentMasterIdentity epmdocumentmasteridentity = (EPMDocumentMasterIdentity) master
//					.getIdentificationObject();
//			epmdocumentmasteridentity.setCADName(s.toLowerCase());
////			IdentityHelper.service.changeIdentity(identified, epmdocumentmasteridentity);
////			PersistenceHelper.manager.refresh(epmdocument);
//			WTKeyedMap map = new WTKeyedHashMap();
//			map.put(m, s.toLowerCase());
//			EPMDocumentHelper.service.changeCADName(map);
//		}
//	}

	private void cadNameSpaceChange(EPMDocument epmdocument) {
		EPMDocumentMaster epmdocumentmaster = (EPMDocumentMaster) epmdocument.getMaster();
		long l = CommonUtil.getOIDLongValue(epmdocumentmaster);
		try {
			QuerySpec queryspec = new QuerySpec(EPMCADNamespace.class);
			queryspec.appendWhere(new SearchCondition(EPMCADNamespace.class, "masterReference.key.id", "=", l));
			for (QueryResult queryresult = PersistenceHelper.manager.find(queryspec); queryresult.hasMoreElements();) {
				EPMCADNamespace epmcadnamespace = (EPMCADNamespace) queryresult.nextElement();
				epmcadnamespace.setCADName(epmdocument.getCADName());
				epmcadnamespace = (EPMCADNamespace) PersistenceHelper.manager.modify(epmcadnamespace);
			}

		} catch (WTException wtexception) {
			wtexception.printStackTrace();
		} catch (WTPropertyVetoException wtpropertyvetoexception) {
			wtpropertyvetoexception.printStackTrace();
		}
	}

	public static CadInfoChange manager = new CadInfoChange();

}
