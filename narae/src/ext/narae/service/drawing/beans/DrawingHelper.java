package ext.narae.service.drawing.beans;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;

import com.ptc.wvs.server.util.FileHelper;
import com.ptc.wvs.server.util.PublishUtils;

import ext.narae.service.drawing.REFDWGLink;
import ext.narae.service.drawing.WTDocEPMDocLink;
import ext.narae.service.org.beans.UserHelper;
import ext.narae.service.part.beans.PartHelper;
import ext.narae.service.part.beans.PartSearchHelper;
import ext.narae.util.CommonUtil;
import ext.narae.util.SequenceDao;
import ext.narae.util.StringUtil;
import ext.narae.util.WCUtil;
import ext.narae.util.code.NumberCode;
import ext.narae.util.code.beans.NumberCodeHelper;
import ext.narae.util.content.CommonContentHelper;
import ext.narae.util.iba.IBAUtil;
import wt.build.BuildHelper;
import wt.clients.folder.FolderTaskLogic;
import wt.clients.vc.CheckInOutTaskLogic;
import wt.content.ApplicationData;
import wt.content.ContentDownloadAccessHelper;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.doc.WTDocument;
import wt.epm.EPMApplicationType;
import wt.epm.EPMAuthoringAppType;
import wt.epm.EPMContextHelper;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.EPMDocumentType;
import wt.epm.build.EPMBuildHistory;
import wt.epm.build.EPMBuildRule;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.LifeCycleTemplate;
import wt.lifecycle.State;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.pdmlink.PDMLinkProduct;
import wt.pom.Transaction;
import wt.query.ClassAttribute;
import wt.query.ColumnExpression;
import wt.query.ConstantExpression;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SQLFunction;
import wt.query.SearchCondition;
import wt.representation.Representable;
import wt.representation.Representation;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;
import wt.vc.VersionControlHelper;
import wt.vc.Versioned;
import wt.vc.wip.CheckoutLink;
import wt.vc.wip.WorkInProgressException;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;

public class DrawingHelper implements wt.method.RemoteAccess, java.io.Serializable {

	static final boolean SERVER = wt.method.RemoteMethodServer.ServerFlag;
	static final String BLANK_IMG = "/plm/netmarkets/images/blank24x16.gif";
	public static final String ROOTLOCATION = "/Default/Drawing";

	public static DrawingHelper manager = new DrawingHelper();

	public String getThumbnailSmallTag(EPMDocument epm) throws Exception {

		String eoid = "";

		ContentHolder holder = null;
		if (epm == null) {
			return "<img src='" + BLANK_IMG + "'  border=0 >";
		} else {
			eoid = epm.getPersistInfo().getObjectIdentifier().toString();
			holder = PublishUtils.findRepresentable(epm);
		}
		String thum = FileHelper.getViewContentURLForType(PublishUtils.findRepresentable(epm),
				ContentRoleType.THUMBNAIL);

		String thum_mini = FileHelper.getViewContentURLForType(holder, ContentRoleType.THUMBNAIL_SMALL);

		if (thum_mini == null)
			thum_mini = BLANK_IMG;
		Representable representable = PublishUtils.findRepresentable(epm);
		Representation representation = PublishUtils.getRepresentation(representable, true, null, false);
		String copyTag = "";
		if (representation != null) {
			copyTag = PublishUtils.getRefFromObject(representation);
		}

		String result = "<img src='" + thum_mini + "'  border=0  ";
		if (thum != null) {
			// result += "onmouseover=\"showThum(this,'" + epm.getNumber()
			// +"','"+thum+"','"+eoid+"','"+copyTag+"')\"";
		}
		return result += ">";
	}

	public EPMDocument getEPMDocument(WTPart _part) throws Exception {
		if (_part == null) {
			return null;
		}
		QueryResult qr = null;
		if (VersionControlHelper.isLatestIteration(_part))
			qr = PersistenceHelper.manager.navigate(_part, "buildSource", EPMBuildRule.class);
		else
			qr = PersistenceHelper.manager.navigate(_part, "builtBy", EPMBuildHistory.class);
		while (qr != null && qr.hasMoreElements())
			return (EPMDocument) qr.nextElement();
		return null;
	}

	public WTPart getWTPart(EPMDocument _epm) throws Exception {
		if (_epm == null) {
			return null;
		}
		QueryResult qr = null;
		if (VersionControlHelper.isLatestIteration(_epm))
			qr = PersistenceHelper.manager.navigate(_epm, "buildTarget", EPMBuildRule.class);
		else
			qr = PersistenceHelper.manager.navigate(_epm, "built", EPMBuildHistory.class);
		while (qr != null && qr.hasMoreElements())
			return (WTPart) qr.nextElement();

		return null;
	}

	public Hashtable create(Hashtable hash, String[] loc) throws Exception {

		if (!SERVER) {
			Class argTypes[] = new Class[] { Hashtable.class, String[].class };
			Object args[] = new Object[] { hash, loc };
			try {
				return (Hashtable) wt.method.RemoteMethodServer.getDefault().invoke("create", null, this, argTypes,
						args);
			} catch (RemoteException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}

		Hashtable rtnVal = new Hashtable();
		Transaction trx = new Transaction();
		try {
			trx.start();

			EPMDocument epm = EPMDocument.newEPMDocument();
			// 자재코드 채번정보
			String number = "";
			String name = "";
			String partAttribute = (String) hash.get("type"); // 품목분류
			String attribute1 = (String) hash.get("unit"); // 대분류1

			WTPart part = null;
			/* PART BuildRole */
			String buildRole = "";
			if (hash.get("buildRole") != null)
				buildRole = (String) hash.get("buildRole");

			if (buildRole.equals("link")) {
				String partOid = (String) hash.get("partOid");
				part = (WTPart) CommonUtil.getObject(partOid);
				number = part.getNumber();
				name = part.getName();

				partAttribute = number.substring(1, 2);
				if ("S".equals(partAttribute))
					attribute1 = number.substring(3, 4);
				else
					attribute1 = number.substring(3, 5);
			} else {
				String partType = (String) hash.get("group"); // 품목 종류

				String attribute2 = (String) hash.get("class1"); // 대분류2
				String attribute3 = (String) hash.get("class2"); // 중분류1
				String attribute4 = (String) hash.get("class3"); // 중분류2
				String attribute5 = (String) hash.get("class4"); // 중분류3
				name = (String) hash.get("name");

				String tempnumber = (String) hash.get("number");

				String serialNum = "";
				String epmSerialNum = SequenceDao.manager.getSeqNo(tempnumber + "-", "00000", "EPMDocumentMaster",
						"documentNumber");
				String partSerialNum = SequenceDao.manager.getSeqNo(tempnumber + "-", "00000", "WTPartMaster",
						"WTPartNumber");
				if (Integer.parseInt(epmSerialNum) > Integer.parseInt(partSerialNum))
					serialNum = epmSerialNum;
				else
					serialNum = partSerialNum;

				number = tempnumber + "-" + serialNum;

				if (buildRole.equals("new")) {

					String partfid = (String) hash.get("partfid");
					String maker = (String) hash.get("maker"); // Maker
					String spec = (String) hash.get("spec"); // 규격
					String isDrawing = (String) hash.get("isDrawing"); // 도면유무
					String partdescription = (String) hash.get("partdescription"); // 설명
					String quantityunit = (String) hash.get("quantityunit"); // 단위

					Hashtable partHash = new Hashtable();
					partHash.put("fid", partfid); // 폴더
					partHash.put("number", number); // 품번
					partHash.put("name", name); // 품명
					partHash.put("maker", maker); // Maker
					partHash.put("quantityunit", quantityunit); // 단위
					partHash.put("spec", spec); // 규격
					if (isDrawing != null)
						partHash.put("isDrawing", isDrawing); // 도면유무
					if (partdescription != null)
						partHash.put("partdescription", partdescription); // 설명
					partHash.put("lifecycle", (String) hash.get("lifecycle"));
					partHash.put("source", "make");
					partHash.put("view", "Design");
					partHash.put("wtPartType", "separable");
					partHash.put("epmCreate", "true");
					if (hash.get("designed1") != null)
						partHash.put("designed1", (String) hash.get("designed1"));
					if (hash.get("oldNumber") != null)
						partHash.put("oldNumber", (String) hash.get("oldNumber"));

					Hashtable retHas = PartHelper.manager.create(partHash);

					String partOid = (String) retHas.get("oid");
					part = (WTPart) CommonUtil.getObject(partOid);
				}
			}

			epm.setNumber(number);
			epm.setName(name);
			epm.setDescription((String) hash.get("description"));

			String authoringType = (String) hash.get("authoringType");
			String primaryFile = (String) hash.get("PRIMARY");
			String applicationType = "MANUAL";
			if (primaryFile == null)
				primaryFile = "";

			EPMDocumentMaster epmMaster = (EPMDocumentMaster) epm.getMaster();
			EPMContextHelper.setApplication(EPMApplicationType.toEPMApplicationType(applicationType));
			epmMaster.setOwnerApplication(EPMContextHelper.getApplication());
			EPMAuthoringAppType appType = EPMAuthoringAppType.toEPMAuthoringAppType(authoringType);
			epmMaster.setAuthoringApplication(appType);

			// Folder && LifeCycle Setting
			ReferenceFactory rf = new ReferenceFactory();
			Folder folder = (Folder) rf.getReference((String) hash.get("fid")).getObject();
			FolderHelper.assignLocation((FolderEntry) epm, folder);

			PDMLinkProduct e3psProduct = WCUtil.getPDMLinkProduct();
			WTContainerRef wtContainerRef = WTContainerRef.newWTContainerRef(e3psProduct);
			epm.setContainer(e3psProduct);
			LifeCycleHelper.setLifeCycle(epm,
					LifeCycleHelper.service.getLifeCycleTemplate((String) hash.get("lifecycle"), wtContainerRef)); // Lifecycle

			String newFileName = "";
			String orgFileName = "";
			String fileName = "";
			String fileDir = "";
			File file = null;
			File rfile = null;
			if (!primaryFile.equals("")) {
				file = new File((String) hash.get("PRIMARY"));
				orgFileName = file.getAbsolutePath();
				fileDir = file.getParent();
				fileName = file.getName();
				int lastIndex = fileName.lastIndexOf(".");
				String fileEnd = fileName.substring(lastIndex).toLowerCase();
				fileName = number + fileEnd;
				newFileName = fileDir + File.separator + fileName;
				rfile = new File(newFileName);
				file.renameTo(rfile);
				file = rfile;

				epm.setCADName(fileName);

				EPMDocumentType docType = getEPMDocumentType(fileEnd);// EPMDocumentType.toEPMDocumentType(type);
				epm.setDocType(docType);
			}

			if (hash.get("designed1") != null) {
				WTUser wtdevUser = UserHelper.service.getUser((String) hash.get("designed1"));
				if (wtdevUser != null)
					epm.setOrganizationReference(WTPrincipalReference.newWTPrincipalReference(wtdevUser));
			}

			epm = (EPMDocument) PersistenceHelper.manager.save(epm);

			if ((String) hash.get("FDate") != null) {
				IBAUtil.createIba(epm, "string", "FDate", (String) hash.get("FDate"));
				IBAUtil.createIba(epm, "string", "LDate", (String) hash.get("FDate"));
			}
			if ((String) hash.get("checked") != null)
				IBAUtil.createIba(epm, "string", "Checked", (String) hash.get("checked"));
			if ((String) hash.get("approved") != null)
				IBAUtil.createIba(epm, "string", "Approved", (String) hash.get("approved"));
			IBAUtil.createIba(epm, "string", "Mat", (String) hash.get("Material"));
			IBAUtil.createIba(epm, "string", "Treatment", (String) hash.get("Treatment"));
			String tempWeight = (String) hash.get("Weight");
			if (tempWeight != null && tempWeight.length() > 0) {
				IBAUtil.createIba(epm, "float", "Weight_f", (String) hash.get("Weight"));
			}

			IBAUtil.createIba(epm, "string", "Sheet", (String) hash.get("Sheet"));
			IBAUtil.createIba(epm, "string", "Ref_Model_no", (String) hash.get("Ref_Model_no"));
			IBAUtil.createIba(epm, "string", "DRW_type", (String) hash.get("DRW_type"));
			IBAUtil.createIba(epm, "string", "Match_part", (String) hash.get("Match_part"));

			// 자재코드 채번정보
			Hashtable rtnNum = PartHelper.getNumberAttribute(number);//

			IBAUtil.changeIBAValue(epm, "Group", (String) rtnNum.get("group")); // 품목 종류
			IBAUtil.changeIBAValue(epm, "Type", (String) rtnNum.get("type")); // 품목분류
			IBAUtil.changeIBAValue(epm, "Unit", (String) rtnNum.get("unit")); // 대분류1
			IBAUtil.changeIBAValue(epm, "Class1", (String) rtnNum.get("class1")); // 대분류2
			IBAUtil.changeIBAValue(epm, "Class2", (String) rtnNum.get("class2")); // 중분류1
			IBAUtil.changeIBAValue(epm, "Class3", (String) rtnNum.get("class3")); // 중분류2
			IBAUtil.changeIBAValue(epm, "Class4", (String) rtnNum.get("class4")); // 중분류3

			IBAUtil.changeIBAValue(epm, "autoNumber", "TRUE");

			if ("S".equals((String) rtnNum.get("type"))) {
				NumberCode unitCode = NumberCodeHelper.manager.getNumberCode("SBUSINESS", (String) rtnNum.get("unit"));
				IBAUtil.changeIBAValue(epm, "Group_Name", unitCode.getName());
			} else if ("P".equals((String) rtnNum.get("type"))) {
				NumberCode paCode = NumberCodeHelper.manager.getNumberCode("CADATTRIBUTE", (String) rtnNum.get("unit"),
						true);
				HashMap map = new HashMap();
				map.put("code", (String) rtnNum.get("class1"));
				map.put("type", "CADATTRIBUTE");
				map.put("parent", paCode);
				QuerySpec qs = NumberCodeHelper.getCodeQuerySpec(map);
				QueryResult qr = PersistenceHelper.manager.find(qs);
				if (qr.hasMoreElements()) {
					Object[] obj = (Object[]) qr.nextElement();
					NumberCode unitCode = (NumberCode) obj[0];
					IBAUtil.changeIBAValue(epm, "Group_Name", unitCode.getName());
				}
			} else {
				NumberCode paCode = NumberCodeHelper.manager.getNumberCode("CADATTRIBUTE", (String) rtnNum.get("type"));
				HashMap map = new HashMap();
				map.put("code", (String) rtnNum.get("unit"));
				map.put("type", "CADATTRIBUTE");
				map.put("parent", paCode);

				QuerySpec qs = NumberCodeHelper.getCodeQuerySpec(map);
				QueryResult qr = PersistenceHelper.manager.find(qs);
				if (qr.hasMoreElements()) {
					Object[] obj = (Object[]) qr.nextElement();
					NumberCode unitCode = (NumberCode) obj[0];
					IBAUtil.changeIBAValue(epm, "Group_Name", unitCode.getName());
				}
			}

			IBAUtil.createIba(epm, "string", "P_Name", name);

			// 주첨부 파일
			if (!newFileName.equals("")) {
				CommonContentHelper.service.attachPrimary(epm, newFileName);
			}
			// 첨부 파일
			if (loc != null) {
				for (int i = 0; i < loc.length; i++) {
					// CommonContentHelper.service.attach(epm, loc[i]);
					CommonContentHelper.service.attach(epm, loc[i], "N");
				}
			}

			// 소재도(가공도)/MI
			ReferenceFactory f = new ReferenceFactory();
			String refDWGOid = (String) hash.get("epmOid");
			if (refDWGOid != null) {
				EPMDocument refDWG = (EPMDocument) f.getReference(refDWGOid).getObject();
				REFDWGLink link = REFDWGLink.newREFDWGLink(epmMaster, (EPMDocumentMaster) refDWG.getMaster());
				link = (REFDWGLink) PersistenceHelper.manager.save(link);
				IBAUtil.changeIBAValue(epm, "Match_part", refDWG.getNumber());
			}

			// 관련 문서
			String[] docOids = (String[]) hash.get("docOids");
			if (docOids != null) {
				for (int i = 0; i < docOids.length; i++) {
					WTDocument doc = (WTDocument) f.getReference(docOids[i]).getObject();
					WTDocEPMDocLink link = WTDocEPMDocLink.newWTDocEPMDocLink(doc, (EPMDocumentMaster) epm.getMaster());
					link = (WTDocEPMDocLink) PersistenceHelper.manager.save(link);
				}
			}

			if (part != null) {
				EPMBuildRule link = EPMBuildRule.newEPMBuildRule(epm, part);
				PersistenceServerHelper.manager.insert(link);

				// this manages the BuildHistory
				BuildHelper.service.buildTargetsForSource(epm);

				// part 속성 입력
				part = (WTPart) link.getBuildTarget();
				part = (WTPart) CommonUtil.getObject((CommonUtil.getVROID(part)));
				IBAUtil.changeIBAValue(part, "Description", (String) hash.get("partdescription"));// 설명
				IBAUtil.changeIBAValue(part, "IsDrawing", "Y"); // 도면 유무
				IBAUtil.changeIBAValue(part, "Maker", (String) hash.get("maker")); // Maker
//                IBAUtil.changeIBAValue(part, "Spec", (String)hash.get("spec"));  //규격
				IBAUtil.changeIBAValue(part, "quantityunit", (String) hash.get("quantityunit")); // 단위
				IBAUtil.changeIBAValue(part, "autoNumber", "TRUE");

				IBAUtil.changeIBAValue(epm, "IsDrawing", "Y"); // 도면 유무
				IBAUtil.changeIBAValue(epm, "Maker", (String) hash.get("maker")); // Maker
				if (hash.get("spec") != null && ((String) hash.get("spec")).length() > 0) {
					IBAUtil.changeIBAValue(part, "Spec", (String) hash.get("spec")); // 규격
					IBAUtil.changeIBAValue(epm, "Spec", (String) hash.get("spec")); // 규격

					NumberCode specCode = NumberCodeHelper.manager.getNumberCodeName("SPEC", (String) hash.get("spec"));
					if (specCode != null) {
						IBAUtil.changeIBAValue(part, "Class4", specCode.getDescription());
						IBAUtil.changeIBAValue(epm, "Class4", specCode.getDescription());
					}
				}
				IBAUtil.changeIBAValue(epm, "quantityunit", (String) hash.get("quantityunit")); // 단위
			}

//			if(fileName.endsWith(".DWG") || fileName.endsWith(".dwg")){
//				EpmPublishUtil.pdfPublish(epm, rfile);
//			}

			if (!orgFileName.equals("")) {
				EpmPublishUtil.publish(epm); // 공표작업

				File orgfile = new File(orgFileName);
				file.renameTo(orgfile);
			}

			rtnVal.put("rslt", "S");
			rtnVal.put("msg", "등록 되었습니다.");
			rtnVal.put("oid", CommonUtil.getOIDString(epm));

			trx.commit();
			trx = null;
		} catch (Exception e) {
			e.printStackTrace();

			rtnVal.put("rslt", "F");
			rtnVal.put("msg", "등록 중 오류가 발생하였습니다. \\n" + e);
			rtnVal.put("oid", "");
		} finally {
			if (trx != null) {
				trx.rollback();
			}
		}
		return rtnVal;
	}

	public String delete(String oid) {
		try {
			if (oid != null) {
				ReferenceFactory f = new ReferenceFactory();
				EPMDocument epm = (EPMDocument) f.getReference(oid).getObject();

				if (WorkInProgressHelper.isCheckedOut(epm))
					return "체크아웃되어 있어서 삭제하실 수 없습니다.";

				Vector vecRef = EpmSearchHelper.manager.getRef(oid);
				if (vecRef.size() > 0)
					return "참조 도면이 있어서 삭제 할수 없습니다.";

				Vector vecRefBy = EpmSearchHelper.manager.getRefBy(oid);
				if (vecRef.size() > 0)
					return "참조항목 도면이 있어서 삭제 할수 없습니다.";

				Vector vecDoc = EpmSearchHelper.manager.getWTDocumentLink(oid);
				if (vecRef.size() > 0)
					return "관련 문서가 있어서 삭제 할수 없습니다";

				PersistenceHelper.manager.delete(epm);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "삭제 중 오류가 발생하였습니다. \\n" + e.getLocalizedMessage();
		}
		return "삭제 되었습니다.";
	}

	public Hashtable modify(Hashtable hash, String[] loc, String[] deloc) throws Exception {
		if (!SERVER) {
			Class argTypes[] = new Class[] { Hashtable.class, String[].class, String[].class };
			Object args[] = new Object[] { hash, loc, deloc };
			try {
				return (Hashtable) wt.method.RemoteMethodServer.getDefault().invoke("modify", null, this, argTypes,
						args);
			} catch (RemoteException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}

		Hashtable rtnVal = new Hashtable();

		Transaction trx = new Transaction();
		try {
			trx.start();

			String oid = (String) hash.get("oid");
			ReferenceFactory f = new ReferenceFactory();
			EPMDocument epm = (EPMDocument) f.getReference(oid).getObject();

			// Working Copy
			epm = (EPMDocument) getWorkingCopy(epm);
			rtnVal = modifyEpm(epm, hash, loc, deloc);

			if ("F".equals(rtnVal.get("rslt"))) {
				rtnVal.put("oid", oid);
				trx.rollback();
				return rtnVal;
			}

			rtnVal.put("rslt", "S");
			rtnVal.put("msg", "수정 되었습니다.");
			if (((String) rtnVal.get("oid")) == null || ((String) rtnVal.get("oid")).length() == 0) {
				rtnVal.put("oid", hash.get("oid"));
			}

			trx.commit();
			trx = null;
		} catch (Exception e) {
			e.printStackTrace();

			rtnVal.put("rslt", "F");
			rtnVal.put("oid", hash.get("oid"));
			rtnVal.put("msg", "수정 중 오류가 발생하였습니다. \\n" + e.getLocalizedMessage());
			// throw e;
		} finally {
			if (trx != null) {
				trx.rollback();
			}
		}
		return rtnVal;
	}

	public String reviseUpdate(Hashtable hash, String[] loc, String[] deloc) throws Exception {
		if (!SERVER) {
			Class argTypes[] = new Class[] { Hashtable.class, String[].class, String[].class };
			Object args[] = new Object[] { hash, loc, deloc };
			try {
				return (String) wt.method.RemoteMethodServer.getDefault().invoke("reviseUpdate", null, this, argTypes,
						args);
			} catch (RemoteException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}

		Hashtable rtnVal = new Hashtable();
		Transaction trx = new Transaction();
		try {
			trx.start();

			String oid = (String) hash.get("oid");
			ReferenceFactory f = new ReferenceFactory();
			if (oid != null) {
				Versioned newVs = null;
				Versioned vs = (Versioned) f.getReference(oid).getObject();

				String lifecycle = ((LifeCycleManaged) vs).getLifeCycleName();
				Folder folder = FolderHelper.service.getFolder((FolderEntry) vs);

				hash.put("fid", folder.getPersistInfo().getObjectIdentifier().toString());

				newVs = VersionControlHelper.service.newVersion(vs);
//				if(_note != null)
//					VersionControlHelper.setNote(obj, _note);

				folder = FolderHelper.service.getPersonalCabinet(SessionHelper.manager.getPrincipal());
				FolderHelper.assignLocation((FolderEntry) newVs, folder);

				EPMDocument epm = (EPMDocument) newVs;

				PDMLinkProduct e3psProduct = WCUtil.getPDMLinkProduct();
				epm.setContainer(e3psProduct);
				WTContainerRef wtContainerRef = WTContainerRef.newWTContainerRef(e3psProduct);
				LifeCycleHelper.setLifeCycle(epm,
						LifeCycleHelper.service.getLifeCycleTemplate(lifecycle, wtContainerRef)); // Lifecycle
				epm = (EPMDocument) PersistenceHelper.manager.save(epm);

				rtnVal = modifyEpm(epm, hash, loc, deloc);

				epm = (EPMDocument) CommonUtil.getObject((String) rtnVal.get("oid"));

				// 부품에 대한 개정을 추가한다.
				WTPart part = PartHelper.manager.getPart(epm.getNumber());

				WTPart newpart = PartHelper.manager.reviseUpdate(part);
				if (newpart != null) {
					QuerySpec query = new QuerySpec();
					int idx = query.addClassList(EPMBuildRule.class, true);
					query.appendWhere(new SearchCondition(EPMBuildRule.class, "roleBObjectRef.key.branchId", "=",
							newpart.getBranchIdentifier()), new int[] { idx });
					QueryResult qr = PersistenceHelper.manager.find(query);
					while (qr.hasMoreElements()) {
						Object[] o = (Object[]) qr.nextElement();
						EPMBuildRule link = (EPMBuildRule) o[0];
						PersistenceHelper.manager.delete(link);
					}

					EPMBuildRule link = EPMBuildRule.newEPMBuildRule(epm, newpart);
					PersistenceServerHelper.manager.insert(link);

					// this manages the BuildHistory
					BuildHelper.service.buildTargetsForSource(epm);

					LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) newpart, State.RELEASED);
				}

				LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) epm, State.RELEASED);

				EpmPublishUtil.publish(epm);
			}

			trx.commit();
			trx = null;
		} catch (Exception e) {
			// throw e;
			e.printStackTrace();
			return "개정 중 오류가 발생하였습니다. \\n" + e.getLocalizedMessage();
		} finally {
			if (trx != null) {
				trx.rollback();
			}
		}
		return "개정 되었습니다.";
	}

	public Hashtable modifyEpm(EPMDocument epm, Hashtable hash, String[] loc, String[] deloc) throws Exception {
		Hashtable rtnVal = new Hashtable();

		if (epm != null) {
			ReferenceFactory f = new ReferenceFactory();
			epm.setDescription((String) hash.get("description"));
			epm = (EPMDocument) PersistenceHelper.manager.modify(epm);

			// 자재코드 채번정보
			Hashtable rtnNum = PartHelper.getNumberAttribute((String) hash.get("number"));//

			String group = (String) rtnNum.get("group"); // 품목 종류
			String type = (String) rtnNum.get("type"); // 품목분류
			String unit = (String) rtnNum.get("unit"); // 대분류1
			String class1 = (String) rtnNum.get("class1"); // 대분류2
			String class2 = (String) rtnNum.get("class2"); // 중분류1
			String class3 = (String) rtnNum.get("class3"); // 중분류2
			String class4 = (String) rtnNum.get("class4"); // 중분류3

			IBAUtil.changeIBAValue(epm, "Mat", (String) hash.get("Material"));
			IBAUtil.changeIBAValue(epm, "Treatment", (String) hash.get("Treatment"));
			IBAUtil.changeIBAValue(epm, "Weight_f", (String) hash.get("Weight"));
			IBAUtil.changeIBAValue(epm, "Sheet", (String) hash.get("Sheet"));
			IBAUtil.changeIBAValue(epm, "Ref_Model_no", (String) hash.get("Ref_Model_no"));
			IBAUtil.changeIBAValue(epm, "DRW_type", (String) hash.get("DRW_type"));
			IBAUtil.changeIBAValue(epm, "Match_part", (String) hash.get("Match_part"));

			IBAUtil.changeIBAValue(epm, "Group", group);
			IBAUtil.changeIBAValue(epm, "Type", type);
			IBAUtil.changeIBAValue(epm, "Unit", unit);
			IBAUtil.changeIBAValue(epm, "Class1", class1);
			IBAUtil.changeIBAValue(epm, "Class2", class2);
			IBAUtil.changeIBAValue(epm, "Class3", class3);
			IBAUtil.changeIBAValue(epm, "Class4", class4);

			if ("S".equals(type)) {
				NumberCode unitCode = NumberCodeHelper.manager.getNumberCode("SBUSINESS", unit);
				IBAUtil.changeIBAValue(epm, "Group_Name", unitCode.getName());
			} else {
				NumberCode paCode = NumberCodeHelper.manager.getNumberCode("CADATTRIBUTE", type);
				HashMap map = new HashMap();
				map.put("code", unit);
				map.put("type", "CADATTRIBUTE");
				map.put("parent", paCode);

				QuerySpec qs = NumberCodeHelper.getCodeQuerySpec(map);
				QueryResult qr = PersistenceHelper.manager.find(qs);
				if (qr.hasMoreElements()) {
					Object[] obj = (Object[]) qr.nextElement();
					NumberCode unitCode = (NumberCode) obj[0];
					IBAUtil.changeIBAValue(epm, "Group_Name", unitCode.getName());
				}
			}

			IBAUtil.changeIBAValue(epm, "P_Name", (String) hash.get("name"));

			if (((String) hash.get("PRIMARY")).length() > 0) {

				ContentItem item = null;
				QueryResult result = ContentHelper.service.getContentsByRole((ContentHolder) epm,
						ContentRoleType.PRIMARY);

				while (result.hasMoreElements()) {
					item = (ContentItem) result.nextElement();
					CommonContentHelper.service.delete(epm, item);
				}

				File file = new File((String) hash.get("PRIMARY"));
				String fileDir = file.getParent();
				String fileName = file.getName();
				int lastIndex = fileName.lastIndexOf(".");
				String fileEnd = fileName.substring(lastIndex).toLowerCase();
				fileName = epm.getNumber() + fileEnd;

				String newFileName = fileDir + File.separator + fileName;
				File rfile = new File(newFileName);
				file.renameTo(rfile);
				file = rfile;

				CommonContentHelper.service.attachPrimary(epm, newFileName);
			}

			CommonContentHelper.service.delete(epm);
			if (deloc != null) {
				for (int j = 0; j < deloc.length; j++) {
					ApplicationData ad = (ApplicationData) f.getReference(deloc[j]).getObject();
					CommonContentHelper.service.attach(epm, ad, false);
				}
			}

			if (loc != null) {
				for (int i = 0; i < loc.length; i++) {
					CommonContentHelper.service.attach(epm, loc[i], "N");
				}
			}

			QuerySpec query = new QuerySpec();
			int idx = query.addClassList(WTDocEPMDocLink.class, true);
			query.appendWhere(new SearchCondition(WTDocEPMDocLink.class, "roleBObjectRef.key", "=",
					epm.getMaster().getPersistInfo().getObjectIdentifier()), new int[] { idx });
			QueryResult qr = PersistenceHelper.manager.find(query);
			while (qr.hasMoreElements()) {
				Object[] o = (Object[]) qr.nextElement();
				WTDocEPMDocLink link = (WTDocEPMDocLink) o[0];
				PersistenceHelper.manager.delete(link);
			}

			// 소재도(가공도)/MI
			EPMDocument oldrefDWG = EpmSearchHelper.manager.getREFDWG(epm);
			if (oldrefDWG != null) {
				QuerySpec spec = new QuerySpec(REFDWGLink.class);
				spec.appendOpenParen();
				spec.appendWhere(new SearchCondition(REFDWGLink.class, "roleAObjectRef.key.id", "=",
						oldrefDWG.getMaster().getPersistInfo().getObjectIdentifier().getId()), new int[] { 0 });
				spec.appendOr();
				spec.appendWhere(new SearchCondition(REFDWGLink.class, "roleBObjectRef.key.id", "=",
						oldrefDWG.getMaster().getPersistInfo().getObjectIdentifier().getId()), new int[] { 0 });
				spec.appendCloseParen();

				spec.appendAnd();

				spec.appendOpenParen();
				spec.appendWhere(new SearchCondition(REFDWGLink.class, "roleAObjectRef.key.id", "=",
						epm.getMaster().getPersistInfo().getObjectIdentifier().getId()), new int[] { 0 });
				spec.appendOr();
				spec.appendWhere(new SearchCondition(REFDWGLink.class, "roleBObjectRef.key.id", "=",
						epm.getMaster().getPersistInfo().getObjectIdentifier().getId()), new int[] { 0 });
				spec.appendCloseParen();

				QueryResult result = PersistenceHelper.manager.find(spec);
				if (result.hasMoreElements()) {
					REFDWGLink oldLink = (REFDWGLink) result.nextElement();
					PersistenceHelper.manager.delete(oldLink);
				}
			}

			String refDWGOid = (String) hash.get("epmOid");
			if (refDWGOid != null) {
				EPMDocument refDWG = (EPMDocument) f.getReference(refDWGOid).getObject();
				REFDWGLink link = REFDWGLink.newREFDWGLink((EPMDocumentMaster) epm.getMaster(),
						(EPMDocumentMaster) refDWG.getMaster());
				link = (REFDWGLink) PersistenceHelper.manager.save(link);
				IBAUtil.changeIBAValue(epm, "Match_part", refDWG.getNumber());
			} else {
				IBAUtil.changeIBAValue(epm, "Match_part", "");
			}

			String[] docOids = (String[]) hash.get("docOids");
			if (docOids != null) {
				for (int i = 0; i < docOids.length; i++) {
					WTDocument doc = (WTDocument) f.getReference(docOids[i]).getObject();
					WTDocEPMDocLink link = WTDocEPMDocLink.newWTDocEPMDocLink(doc, (EPMDocumentMaster) epm.getMaster());
					link = (WTDocEPMDocLink) PersistenceHelper.manager.save(link);
				}
			}

			epm = (EPMDocument) PersistenceHelper.manager.refresh(epm);

			// CheckedOut
			if (WorkInProgressHelper.isCheckedOut(epm)) {
				epm = (EPMDocument) WorkInProgressHelper.service.checkin(epm, "");
			}

			Folder folder = null;
			if (hash.get("fid") != null)
				folder = (Folder) f.getReference((String) hash.get("fid")).getObject();
			else
				folder = FolderTaskLogic.getFolder((String) hash.get("location"), WCUtil.getWTContainerRef());

			epm = (EPMDocument) FolderHelper.service.changeFolder((FolderEntry) epm, folder);

			if (((String) hash.get("PRIMARY")).length() > 0) {
				EpmPublishUtil.publish(epm); // 공표작업
			}

			/* 주부품 연결 */
			String partOid = (String) hash.get("partOid");
			WTPart partOrg = DrawingHelper.manager.getWTPart(epm);
			WTPart partNew = null;
			boolean isChangeEpm = false;
			if (partOid.length() > 0) {

				partNew = (WTPart) CommonUtil.getObject(partOid);
				if (partOrg == null) {
					partNew = (WTPart) getWorkingCopy(partNew);
					if (WorkInProgressHelper.isCheckedOut(partNew)) {
						partNew = (WTPart) WorkInProgressHelper.service.checkin(partNew, "");
					}

					EPMBuildRule link = EPMBuildRule.newEPMBuildRule(epm, partNew);
					PersistenceHelper.manager.save(link);
					BuildHelper.service.buildTargetsForSource(epm);

					isChangeEpm = true;
				} else {
					EPMBuildRule newEbr = PartSearchHelper.getBuildRule(epm);
					newEbr.setBuildTarget(partNew);
					PersistenceServerHelper.manager.update(newEbr);
					EPMBuildHistory ebh = PartSearchHelper.getBuildHistory(partOrg, epm);
					if (ebh != null) {
						ebh.setBuilt(epm);
						PersistenceServerHelper.manager.update(ebh);
					}
				}
			} else {
				if (partOrg != null) {
					partOrg = (WTPart) getWorkingCopy(partOrg);
					if (WorkInProgressHelper.isCheckedOut(partOrg)) {
						partOrg = (WTPart) WorkInProgressHelper.service.checkin(partOrg, "");
					}
					EPMBuildRule newEbr = PartSearchHelper.getBuildRule(epm);
					PersistenceHelper.manager.delete(newEbr);
				}
			}

			/*
			 * :::::::::::::::::::::::::::::: NUMBER CHANGE ::::::::::::::::::::::::::::::
			 */
			if (!epm.getOwnerApplication().toString().equals("EPM")) {

				String tempNumber = (String) hash.get("number");
				String tempName = (String) hash.get("name");
				String tempOldNumber = epm.getNumber();

				if (tempOldNumber.length() > 10) {
					tempOldNumber = tempOldNumber.substring(0, 10);
				}

				String compareNumber = tempNumber.substring(0, 10);

				if (!compareNumber.equals(tempOldNumber)) {
					if (tempNumber.length() > 15) {

					} else {
						String serial = EpmUtil.getPdmSerialNumber(tempNumber);
						tempNumber = tempNumber + "-" + serial;
					}

					String cadName = tempNumber + EpmUtil.getCadExtension(epm.getCADName());

					boolean isAutonumber = CadInfoChange.manager.epmCadInfoChange(epm, tempNumber, tempName, cadName);
					if (isAutonumber) {
						IBAUtil.changeIBAValue(epm, "autoNumber", "TRUE");
					} else {
						IBAUtil.changeIBAValue(epm, "autoNumber", "FALSE");
					}

					WTPart part = DrawingHelper.manager.getWTPart(epm);
					if (part != null) {
						IBAUtil.changeNumber(CommonUtil.getOIDString(part), tempNumber, tempName);
						IBAUtil.changeIBAValue(part, "autoNumber", "TRUE");
					}

				}
				EpmPublishUtil.publish(epm);

				EPMDocument epm2d = EpmSearchHelper.manager.getEPM2D((EPMDocumentMaster) epm.getMaster());
				if (epm2d != null) {
					String cadName = tempNumber + EpmUtil.getCadExtension(epm2d.getCADName());
					if (tempNumber.length() > 15) {
						tempNumber = tempNumber + "_2D";
					} else {
						tempNumber = epm2d.getNumber();
					}
					boolean isNumber2D = CadInfoChange.manager.epmCadInfoChange(epm2d, tempNumber, tempName, cadName);
					if (isNumber2D) {
						IBAUtil.changeIBAValue(epm2d, "autoNumber", "TRUE");
					} else {
						IBAUtil.changeIBAValue(epm2d, "autoNumber", "FALSE");
					}

					if ("S".equals(type)) {
						NumberCode unitCode = NumberCodeHelper.manager.getNumberCode("SBUSINESS", unit);
						IBAUtil.changeIBAValue(epm2d, "Group_Name", unitCode.getName());
					} else {
						NumberCode paCode = NumberCodeHelper.manager.getNumberCode("CADATTRIBUTE", type);
						HashMap map = new HashMap();
						map.put("code", unit);
						map.put("type", "CADATTRIBUTE");
						map.put("parent", paCode);

						QuerySpec qs = NumberCodeHelper.getCodeQuerySpec(map);
						qr = PersistenceHelper.manager.find(qs);
						if (qr.hasMoreElements()) {
							Object[] obj = (Object[]) qr.nextElement();
							NumberCode unitCode = (NumberCode) obj[0];
							IBAUtil.changeIBAValue(epm2d, "Group_Name", unitCode.getName());
						}
					}

					EpmPublishUtil.publish(epm2d);
				}

			}

			rtnVal.put("oid", CommonUtil.getOIDString(epm));
		}

		return rtnVal;
	}

	private Workable getWorkingCopy(Workable _obj) {
		try {
			if (!WorkInProgressHelper.isCheckedOut(_obj)) {

				if (!CheckInOutTaskLogic.isCheckedOut(_obj)) {
					CheckoutLink checkoutlink = WorkInProgressHelper.service.checkout(_obj,
							CheckInOutTaskLogic.getCheckoutFolder(), "");
				}

				_obj = (Workable) WorkInProgressHelper.service.workingCopyOf(_obj);
			} else {
				if (!WorkInProgressHelper.isWorkingCopy(_obj))
					_obj = (Workable) WorkInProgressHelper.service.workingCopyOf(_obj);
			}
		} catch (WorkInProgressException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		}
		return _obj;
	}

	public boolean isDuplicateCADName(String cadName) {
		boolean flag = false;
		if (cadName != null) {
			try {
				QuerySpec query = new QuerySpec(EPMDocumentMaster.class);
				cadName = cadName.toUpperCase();

				ClassAttribute attribute = new ClassAttribute(EPMDocumentMaster.class, EPMDocumentMaster.CADNAME);
				SQLFunction function = SQLFunction.newSQLFunction("UPPER");
				function.setArgumentAt((ColumnExpression) attribute, 0);
				ConstantExpression expression = new ConstantExpression(cadName);
				SearchCondition sc = new SearchCondition(function, SearchCondition.EQUAL, expression);

				query.appendWhere(sc, new int[] { 0 });
				QueryResult qr = PersistenceHelper.manager.find(query);
				return qr.hasMoreElements();
			} catch (QueryException e) {
				e.printStackTrace();
			} catch (WTException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return flag;
	}

	public boolean isDuplicateEndName(String epmDocOid, String fileName) {
		boolean flag = false;
		if (epmDocOid != null && fileName != null) {
			ReferenceFactory f = new ReferenceFactory();
			EPMDocument epm;
			try {
				epm = (EPMDocument) f.getReference(epmDocOid).getObject();

				String CADEnd = epm.getCADName();
				int CADEndIndex = CADEnd.lastIndexOf(".");
				CADEnd = CADEnd.substring(CADEndIndex).toLowerCase();

				int lastIndex = fileName.lastIndexOf(".");
				String fileEnd = fileName.substring(lastIndex).toLowerCase();

				if (CADEnd.equals(fileEnd))
					flag = true;

			} catch (WTRuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return flag;
	}

	public synchronized String createDrawings(Hashtable hash, String[] loc) throws Exception {

		if (!SERVER) {
			Class argTypes[] = new Class[] { Hashtable.class, String[].class };
			Object args[] = new Object[] { hash, loc };
			try {
				return (String) wt.method.RemoteMethodServer.getDefault().invoke("createDrawings", null, this, argTypes,
						args);
			} catch (RemoteException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}

		Transaction trx = new Transaction();
		try {
			trx.start();

			WTDocument doc = WTDocument.newWTDocument();
			doc.setName((String) hash.get("name"));
			doc.setDescription((String) hash.get("description"));

			// Folder && LifeCycle Setting
			Folder folder = FolderTaskLogic.getFolder((String) hash.get("location"), WCUtil.getWTContainerRef());
			FolderHelper.assignLocation((FolderEntry) doc, folder);
			PDMLinkProduct e3psProduct = WCUtil.getPDMLinkProduct();
			WTContainerRef wtContainerRef = WTContainerRef.newWTContainerRef(e3psProduct);
			doc.setContainer(e3psProduct);
			LifeCycleHelper.setLifeCycle(doc,
					LifeCycleHelper.service.getLifeCycleTemplate((String) hash.get("lifecycle"), wtContainerRef)); // Lifecycle

			StringTokenizer st = new StringTokenizer((String) hash.get("location"), "/");
			int count = 0;
			String drawingCodeKey1 = null;
			String drawingCodeKey2 = null;
			while (st.hasMoreTokens()) {
				count++;
				if (count == 3)
					drawingCodeKey1 = st.nextToken();
				else if (count == 4)
					drawingCodeKey2 = st.nextToken();
				else
					st.nextToken();
			}

			String drawingCode1 = null;
			String drawingCode2 = null;
			if (drawingCodeKey1 != null) {
				if ("기타".equals(drawingCodeKey1))
					drawingCodeKey1 = "기타상";
				NumberCode drawingType = NumberCodeHelper.manager.getNumberCodeName("DRAWTYPE", drawingCodeKey1);
				drawingCode1 = drawingType.getCode();
			}
			if (drawingCodeKey2 != null) {
				if ("기타".equals(drawingCodeKey1))
					drawingCodeKey1 = "기타하";
				NumberCode drawingType = NumberCodeHelper.manager.getNumberCodeName("DRAWTYPE", drawingCodeKey2);
				drawingCode2 = drawingType.getCode();
			}

			String createYear = (String) hash.get("createYear");
			String number = createYear + "-";
			String seqNo = "";
			if (drawingCode1 != null && drawingCode2 != null) {
				number = number + drawingCode1 + "-" + drawingCode2 + "-";
				String docSeqNo = SequenceDao.manager.getSeqNo(number, "0000", "WTDocumentMaster", "WTDocumentNumber");
				String epmSeqNo = SequenceDao.manager.getSeqNo(number, "0000", "EPMDocumentMaster", "documentNumber");

				if (Long.parseLong(docSeqNo) > Long.parseLong(epmSeqNo))
					seqNo = docSeqNo;
				else
					seqNo = epmSeqNo;
				doc.setNumber(number + seqNo);
			}

			doc = (WTDocument) PersistenceHelper.manager.save(doc);

			String PRIMARY = (String) hash.get("PRIMARY");
			if (PRIMARY.length() > 0) {
				CommonContentHelper.service.attachPrimary(doc, (String) hash.get("PRIMARY"));

				ArrayList docLoc = (ArrayList) hash.get("docLoc");
				if (docLoc.size() > 0) {
					for (int i = 0; i < docLoc.size(); i++)
						CommonContentHelper.service.attach(doc, (String) docLoc.get(i));
				}
			} else {
				ArrayList docLoc = (ArrayList) hash.get("docLoc");
				if (docLoc.size() > 0)
					CommonContentHelper.service.attachPrimary(doc, (String) docLoc.get(0));
			}

			ArrayList epmLoc = (ArrayList) hash.get("epmLoc");
			if (epmLoc.size() > 0) {
				for (int i = 0; i < epmLoc.size(); i++) {
					File file = new File((String) epmLoc.get(i));
					String orgFileName = file.getAbsolutePath();
					String fileDir = file.getParent();
					String fileName = file.getName();
					int lastIndex = fileName.lastIndexOf(".");
					String fileEnd = fileName.substring(lastIndex).toLowerCase();
					;
					fileName = fileName.substring(0, lastIndex) + fileEnd;
					EPMDocument epm = EPMDocument.newEPMDocument();

					epm.setName(fileName);
					epm.setCADName(fileName);

//					String type = (String)hash.get("documentType");//"CADDRAWING";
					EPMDocumentType docType = getEPMDocumentType(fileEnd);// EPMDocumentType.toEPMDocumentType(type);
					epm.setDocType(docType);

					EPMDocumentMaster epmMaster = (EPMDocumentMaster) epm.getMaster();
					EPMContextHelper.setApplication(EPMApplicationType.toEPMApplicationType("EPM"));
					epmMaster.setOwnerApplication(EPMContextHelper.getApplication());
					EPMAuthoringAppType appType = EPMAuthoringAppType
							.toEPMAuthoringAppType((String) hash.get("applicationtype"));
					epmMaster.setAuthoringApplication(appType);

					// Folder && LifeCycle Setting
					FolderHelper.assignLocation((FolderEntry) epm, folder);
					epm.setContainer(e3psProduct);
					LifeCycleHelper.setLifeCycle(epm, LifeCycleHelper.service
							.getLifeCycleTemplate((String) hash.get("lifecycle"), wtContainerRef)); // Lifecycle

					DecimalFormat decimalformat = new DecimalFormat("0000");
					seqNo = decimalformat.format(Long.parseLong(seqNo) + 1);
					epm.setNumber(number + seqNo);

					epm = (EPMDocument) PersistenceHelper.manager.save(epm);

					String newFileName = fileDir + File.separator + epm.getNumber() + fileEnd;
					File rfile = new File(newFileName);
					file.renameTo(rfile);
					file = rfile;

					CommonContentHelper.service.attachPrimary(epm, newFileName);
					EpmPublishUtil.publish(epm); // 공표작업

					WTDocEPMDocLink link = WTDocEPMDocLink.newWTDocEPMDocLink(doc, (EPMDocumentMaster) epm.getMaster());
					link = (WTDocEPMDocLink) PersistenceHelper.manager.save(link);

					File orgfile = new File(orgFileName);
					file.renameTo(orgfile);
				}
			}

			trx.commit();
			trx = null;
		} catch (Exception e) {
			throw e;
		} finally {
			if (trx != null) {
				trx.rollback();
			}
		}
		return "등록 되었습니다.";
	}

	public String getDrawCode(String fileName) {
		StringTokenizer st = new StringTokenizer(fileName, "-");
		int i = 0;
		String locationStr = null;
		while (st.hasMoreTokens()) {
			i++;
			if (i == 3)
				locationStr = st.nextToken();
			else
				st.nextToken();
		}

		return locationStr;
	}

	public String getLocation(String fileName) {
		String locationStr = getDrawCode(fileName);
		String location = null;

		if (locationStr != null) {
			NumberCode drawType1 = NumberCodeHelper.manager.getNumberCode("DRAWTYPE", locationStr.substring(0, 2));
			NumberCode drawType2 = NumberCodeHelper.manager.getNumberCode("DRAWTYPE", locationStr.substring(2));

			if (drawType1 != null && drawType1.getName() != null && drawType2 != null && drawType2.getName() != null)
				location = "/Default/Drawing/" + drawType1.getName() + "/" + drawType2.getName();
		}

		return location;
	}

	public String deleteDrawings(String oid) {
		try {
			if (oid != null) {
				ReferenceFactory f = new ReferenceFactory();
				WTDocument doc = (WTDocument) f.getReference(oid).getObject();

				if (WorkInProgressHelper.isCheckedOut(doc))
					return "체크아웃되어 있어서 삭제하실 수 없습니다.";

				QueryResult linkQr = PersistenceHelper.manager.navigate(doc, "epmDoc", WTDocEPMDocLink.class);
				while (linkQr.hasMoreElements()) {
					EPMDocumentMaster epmMaster = (EPMDocumentMaster) linkQr.nextElement();

					QueryResult docLinkQr = PersistenceHelper.manager.navigate(epmMaster, "wtDoc",
							WTDocEPMDocLink.class);
					if (docLinkQr.size() < 2)
						PersistenceHelper.manager.delete(epmMaster);
				}
				PersistenceHelper.manager.delete(doc);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "삭제 되었습니다.";
	}

	public String reviseUpdateDrawings(Hashtable hash, String[] loc, String[] deloc, String[] linkepm)
			throws Exception {
		if (!SERVER) {
			Class argTypes[] = new Class[] { Hashtable.class, String[].class, String[].class, String[].class };
			Object args[] = new Object[] { hash, loc, deloc, linkepm };
			try {
				return (String) wt.method.RemoteMethodServer.getDefault().invoke("reviseUpdateDrawings", null, this,
						argTypes, args);
			} catch (RemoteException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}

		Transaction trx = new Transaction();
		try {
			trx.start();

			String oid = (String) hash.get("oid");
			ReferenceFactory f = new ReferenceFactory();
			if (oid != null) {
				Versioned newVs = null;
				Versioned vs = (Versioned) f.getReference(oid).getObject();

				String lifecycle = ((LifeCycleManaged) vs).getLifeCycleName();
//				Folder folder = FolderHelper.service.getFolder((FolderEntry) vs);
				Folder folder = FolderTaskLogic.getFolder((String) hash.get("location"), WCUtil.getWTContainerRef());

				newVs = VersionControlHelper.service.newVersion(vs);
//				if(_note != null)
//					VersionControlHelper.setNote(obj, _note);
				FolderHelper.assignLocation((FolderEntry) newVs, folder);

				WTDocument doc = (WTDocument) newVs;

				PDMLinkProduct e3psProduct = WCUtil.getPDMLinkProduct();
				doc.setContainer(e3psProduct);
				WTContainerRef wtContainerRef = WTContainerRef.newWTContainerRef(e3psProduct);
				LifeCycleHelper.setLifeCycle(doc,
						LifeCycleHelper.service.getLifeCycleTemplate(lifecycle, wtContainerRef)); // Lifecycle
				doc = (WTDocument) PersistenceHelper.manager.save(doc);

//				hash.put("oid", epm.getPersistInfo().getObjectIdentifier().toString());
//				modifyEpm(hash , loc , deloc);			

				// Working Copy
				doc = (WTDocument) getWorkingCopy(doc);

				doc.setDescription((String) hash.get("description"));

				doc = (WTDocument) PersistenceHelper.manager.modify(doc);

				if (((String) hash.get("PRIMARY")).length() > 0) {
					ContentItem item = null;
					QueryResult result = ContentHelper.service.getContentsByRole((ContentHolder) doc,
							ContentRoleType.PRIMARY);
					while (result.hasMoreElements()) {
						item = (ContentItem) result.nextElement();
					}
					CommonContentHelper.service.delete(doc, item);
					CommonContentHelper.service.attachPrimary(doc, (String) hash.get("PRIMARY"));
				}

				CommonContentHelper.service.delete(doc);
				if (deloc != null) {
					for (int j = 0; j < deloc.length; j++) {
						ApplicationData ad = (ApplicationData) f.getReference(deloc[j]).getObject();
						CommonContentHelper.service.attach(doc, ad, false);
					}
				}

				if (loc != null) {
					for (int i = 0; i < loc.length; i++) {
						CommonContentHelper.service.attach(doc, loc[i]);
					}
				}

				ArrayList docLoc = (ArrayList) hash.get("docLoc");
				if (docLoc.size() > 0) {
					for (int i = 0; i < docLoc.size(); i++)
						CommonContentHelper.service.attach(doc, (String) docLoc.get(i));
				}

				doc = (WTDocument) PersistenceHelper.manager.refresh(doc);

				// CheckedOut
				if (WorkInProgressHelper.isCheckedOut(doc)) {
					doc = (WTDocument) WorkInProgressHelper.service.checkin(doc, "");
				}

				if (linkepm != null) {
					for (int j = 0; j < linkepm.length; j++) {
						EPMDocumentMaster epmMaster = (EPMDocumentMaster) f.getReference(linkepm[j]).getObject();
						WTDocEPMDocLink link = WTDocEPMDocLink.newWTDocEPMDocLink(doc, epmMaster);
						link = (WTDocEPMDocLink) PersistenceHelper.manager.save(link);
					}
				}

				ArrayList epmLoc = (ArrayList) hash.get("epmLoc");
				if (epmLoc.size() > 0) {
					for (int i = 0; i < epmLoc.size(); i++) {
						File file = new File((String) epmLoc.get(i));
						String orgFileName = file.getAbsolutePath();
						String fileDir = file.getParent();
						String fileName = file.getName();
						int lastIndex = fileName.lastIndexOf(".");
						String fileEnd = fileName.substring(lastIndex).toLowerCase();
						;
						fileName = fileName.substring(0, lastIndex) + fileEnd;

						EPMDocument epm = EPMDocument.newEPMDocument();

						epm.setName(fileName);
						epm.setCADName(fileName);

						EPMDocumentMaster epmMaster = (EPMDocumentMaster) epm.getMaster();
						EPMContextHelper.setApplication(EPMApplicationType.toEPMApplicationType("EPM"));
						epmMaster.setOwnerApplication(EPMContextHelper.getApplication());
						EPMAuthoringAppType appType = EPMAuthoringAppType
								.toEPMAuthoringAppType((String) hash.get("applicationtype"));
						epmMaster.setAuthoringApplication(appType);

						// Folder && LifeCycle Setting
						FolderHelper.assignLocation((FolderEntry) epm, folder);
						epm.setContainer(e3psProduct);
						LifeCycleHelper.setLifeCycle(epm, LifeCycleHelper.service
								.getLifeCycleTemplate((String) hash.get("lifecycle"), wtContainerRef)); // Lifecycle

						String number = doc.getNumber();
						number = number.substring(0, 10);

						String docSeqNo = SequenceDao.manager.getSeqNo(number, "0000", "WTDocumentMaster",
								"WTDocumentNumber");
						String epmSeqNo = SequenceDao.manager.getSeqNo(number, "0000", "EPMDocumentMaster",
								"documentNumber");
						String seqNo = "";
						if (Long.parseLong(docSeqNo) > Long.parseLong(epmSeqNo))
							seqNo = docSeqNo;
						else
							seqNo = epmSeqNo;
						epm.setNumber(number + seqNo);

						String newFileName = fileDir + File.separator + epm.getNumber() + fileEnd;
						File rfile = new File(newFileName);
						file.renameTo(rfile);
						file = rfile;

						// String type = (String)hash.get("documentType");//"CADDRAWING";
						EPMDocumentType docType = getEPMDocumentType(fileEnd);// EPMDocumentType.toEPMDocumentType(type);
						epm.setDocType(docType);

						epm = (EPMDocument) PersistenceHelper.manager.save(epm);

						CommonContentHelper.service.attachPrimary(epm, newFileName);
						EpmPublishUtil.publish(epm); // 공표작업

						WTDocEPMDocLink link = WTDocEPMDocLink.newWTDocEPMDocLink(doc,
								(EPMDocumentMaster) epm.getMaster());
						link = (WTDocEPMDocLink) PersistenceHelper.manager.save(link);

						File orgfile = new File(orgFileName);
						file.renameTo(orgfile);
					}
				}

			}

			trx.commit();
			trx = null;
		} catch (Exception e) {
			throw e;
		} finally {
			if (trx != null) {
				trx.rollback();
			}
		}
		return "개정 되었습니다.";
	}

	public EPMDocumentType getEPMDocumentType(String fileName) {
		/*
		 * CADASSEMBLY.value=어셈블리 CADCOMPONENT.value=CAD 부품 CADDRAWING.value=드로잉
		 * DIAGRAM.value=다이어그램 ECAD-ASSEMBLY.value=ECAD - 어셈블리 ECAD-BOARD.value=ECAD -
		 * 보드 ECAD-COMPONENT.value=ECAD - 컴포넌트 ECAD-CONTENT.value=ECAD - 컨텐트
		 * ECAD-SCHEMATIC.value=ECAD - 도식 ECAD-SOURCE.value=ECAD - 소스 FORMAT.value=형식
		 * LAYOUT.value=레이아웃 MANUFACTURING.value=제조 MARKUP.value=마크업 OTHER.value=기타
		 * REPORT.value=보고서 SKETCH.value=스케치 UDF.value=사용자 정의 기능 DESIGN.value=설계
		 * RENDERING.value=렌더링 PUB_COMPOUNDTEXT.value=게시 소스 PUB_GRAPHIC.value=게시 그래픽
		 * PUB_CADVIEWABLE.value=보기 가능한 CAD 항목 게시 CADDRAWINGTEMPL.value=드로잉 템플릿
		 * ANALYSIS.value=분석 IGES.value=Iges STEP.value=Step VDA.value=VDA-FS
		 * ACIS.value=ACIS PARASOLID.value=Parasolid ZIP.value=Zip 파일 DXF.value=DXF
		 * NOTE.value=메모 WORKSHEET.value=Mathcad 워크시트 EDA_DIFF_CONFIG.value=ECAD Compare
		 * 구성 데이터 EDA_DIFF_REPORT.value=ECAD Compare IDX 데이터 CALCULATION_DATA.value=계산
		 * 데이터 MANIKIN_POSTURE.value=인체 모형 자세 WORKPLANE.value=작업 평면
		 * WORKPLANE_SET.value=작업 평면 세트 CUTTER_LOCATION.value=커터 위치
		 * MACHINE_CONTROL.value=머신 제어 데이터 INSTANCEDATA.value=인스턴스 데이터
		 * MECHANICARESULTS.value=분석 및 설계 검토 결과 MECHANICAREPORT.value=HTML 결과 보고서
		 */

		String type = "OTHER";

		if (".drw".equals(fileName)) {
			type = "CADDRAWING";
		} else if (".prt".equals(fileName)) {
			type = "CADCOMPONENT";
		} else if (".asm".equals(fileName)) {
			type = "CADASSEMBLY";
		} else if (".frm".equals(fileName)) {
			type = "FORMAT";
		} else if (".dwg".equals(fileName)) {
			type = "CADCOMPONENT";// type = "CADDRAWING";
		} else if (".igs".equals(fileName)) {
			type = "IGES";
		} else if (".iges".equals(fileName)) {
			type = "IGES";
		} else if (".gif".equals(fileName)) {
			type = "PUB_GRAPHIC";
		} else if (".jpg".equals(fileName)) {
			type = "PUB_GRAPHIC";
		} else if (".zip".equals(fileName)) {
			type = "ZIP";
		}

		return EPMDocumentType.toEPMDocumentType(type);
	}

	public boolean checkDownloadAccess(ContentHolder holder, ApplicationData appData) throws Exception {
		if (!SERVER) {
			Class argTypes[] = new Class[] { ContentHolder.class, ApplicationData.class };
			Object args[] = new Object[] { holder, appData };
			try {
				return (Boolean) wt.method.RemoteMethodServer.getDefault().invoke("checkDownloadAccess", null, this,
						argTypes, args);
			} catch (RemoteException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}

		try {
			ContentDownloadAccessHelper.checkDownloadAccess(holder, appData, null);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
