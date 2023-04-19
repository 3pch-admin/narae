package ext.narae.service.part.beans;

import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.ResourceBundle;

import com.ptc.core.foundation.type.server.impl.TypeHelper;
import com.ptc.core.meta.common.TypeIdentifier;

import ext.narae.service.CommonUtil2;
import ext.narae.service.approval.ApprovalToObjectLink;
import ext.narae.service.approval.MultiApproval;
import ext.narae.service.approval.beans.ApprovalHelper;
import ext.narae.service.drawing.GPartEPMLink;
import ext.narae.service.drawing.beans.CadInfoChange;
import ext.narae.service.drawing.beans.DrawingHelper;
import ext.narae.service.drawing.beans.EpmPublishUtil;
import ext.narae.service.drawing.beans.EpmSearchHelper;
import ext.narae.service.drawing.beans.EpmUtil;
import ext.narae.service.erp.beans.ERPSearchHelper;
import ext.narae.service.folder.beans.FolderHelper2;
import ext.narae.service.iba.beans.AttributeHelper;
import ext.narae.service.org.beans.UserHelper;
import ext.narae.util.CommonUtil;
import ext.narae.util.EventVersionManager;
import ext.narae.util.ParamUtil;
import ext.narae.util.StringUtil;
import ext.narae.util.WCUtil;
import ext.narae.util.code.NumberCode;
import ext.narae.util.code.beans.NumberCodeHelper;
import ext.narae.util.iba.IBAUtil;
import ext.narae.util.query.SearchUtil;
import wt.build.BuildHelper;
import wt.clients.vc.CheckInOutTaskLogic;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.build.EPMBuildHistory;
import wt.epm.build.EPMBuildRule;
import wt.fc.IdentityHelper;
import wt.fc.Persistable;
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
import wt.method.RemoteMethodServer;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.PartType;
import wt.part.QuantityUnit;
import wt.part.Source;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartMasterIdentity;
import wt.part.WTPartReferenceLink;
import wt.part.WTPartStandardConfigSpec;
import wt.part.WTPartUsageLink;
import wt.pdmlink.PDMLinkProduct;
import wt.pom.Transaction;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.VersionControlHelper;
import wt.vc.Versioned;
import wt.vc.views.ViewHelper;
import wt.vc.wip.CheckoutLink;
import wt.vc.wip.WorkInProgressException;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;

public class PartHelper {
	public static final PartHelper manager = new PartHelper();
	public static final String ATT_PART_FOLDER = "selectedFolderFromFolderContext";
	public static final String ATT_PART_FOLDER_PATH = "partFolderValue";
	public static final String ATT_NUMBER = "number";
	public static final String ATT_NAME = "name";
	public static final String ATT_MAKER = "maker1";
	public static final String ATT_QUANTITYUNIT = "quantityunit";
	public static final String ATT_SPEC = "standard";
	public static final String ATT_EXIST_DRAWING = "isDrawing";
	public static final String ATT_DOC_OID = "docListOid";
	public static final String ATT_DESCRIPTION = "partdescription";

	public static final String ATT_GROUP = "group1";
	public static final String ATT_TYPE = "type";
	public static final String ATT_UNIT1 = "unit1";
	public static final String ATT_UNIT2 = "unit2";
	public static final String ATT_CLASS1 = "class1";
	public static final String ATT_CLASS2 = "class2";
	public static final String ATT_CLASS3 = "class3";
	public static final String ATT_CLASS4 = "class4";

	public static final String ATT_WTPARTTYPE = "wtPartType";
	public static final String ATT_SOURCE = "source";
	public static final String ATT_LIFECYCLE = "lifecycle";
	public static final String ATT_VIEW = "view";

	public static final String ATT_GPART = "gPart";

	public static final String IBA_MAKER = "Maker";
	public static final String IBA_DESCRIPOTION = "Description";
	public static final String IBA_SPEC = "Spec";
	public static final String IBA_IS_DRAWING = "IsDrawing";

	public static final String ATT_EPM_OID = "epmOidValue";

	static final boolean SERVER = wt.method.RemoteMethodServer.ServerFlag;

	public static WTPart createPart(HashMap<String, Object> params) throws Exception {
		if (!RemoteMethodServer.ServerFlag) {
			Class argTypes[] = new Class[] { HashMap.class };
			Object args[] = new Object[] { params };
			try {
				return (WTPart) wt.method.RemoteMethodServer.getDefault().invoke("createPart",
						PartHelper.class.getName(), null, argTypes, args);
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
		WTPart part = null;
		try {
			trx.start();

			String partNumber = (params.get(ATT_NUMBER) != null) ? (String) params.get(ATT_NUMBER) : "";
			String partName = (params.get(ATT_NAME) != null) ? URLDecoder.decode((String) params.get(ATT_NAME), "utf-8")
					: "";// params.get(ATT_NAME);
			String partFolder = (params.get(ATT_PART_FOLDER) != null) ? (String) params.get(ATT_PART_FOLDER) : "";
			String partFolderPath = (params.get(ATT_PART_FOLDER_PATH) != null)
					? URLDecoder.decode((String) params.get(ATT_PART_FOLDER_PATH), "utf-8")
					: "";
			String maker = (params.get(ATT_MAKER) != null) ? URLDecoder.decode((String) params.get(ATT_MAKER), "utf-8")
					: "";
			String quantityunit = (params.get(ATT_QUANTITYUNIT) != null) ? (String) params.get(ATT_QUANTITYUNIT) : "";
			String spec = (params.get(ATT_SPEC) != null) ? URLDecoder.decode((String) params.get(ATT_SPEC), "utf-8")
					: "";
			String isDrawing = (params.get(ATT_EXIST_DRAWING) != null) ? (String) params.get(ATT_EXIST_DRAWING) : "";
			String partdescription = (params.get(ATT_DESCRIPTION) != null)
					? URLDecoder.decode((String) params.get(ATT_DESCRIPTION), "utf-8")
					: "";
			String docListOid = (params.get(ATT_DOC_OID) != null)
					? URLDecoder.decode((String) params.get(ATT_DOC_OID), "utf-8")
					: "";

			String wtPartType = (params.get(ATT_WTPARTTYPE) != null)
					? URLDecoder.decode((String) params.get(ATT_WTPARTTYPE), "utf-8")
					: "";
			String source = (params.get(ATT_SOURCE) != null)
					? URLDecoder.decode((String) params.get(ATT_SOURCE), "utf-8")
					: "";
			String view = (params.get(ATT_VIEW) != null) ? URLDecoder.decode((String) params.get(ATT_VIEW), "utf-8")
					: "";
			String lifecycle = (params.get(ATT_LIFECYCLE) != null)
					? URLDecoder.decode((String) params.get(ATT_LIFECYCLE), "utf-8")
					: "";

			String epmOid = (params.get(ATT_EPM_OID) != null)
					? URLDecoder.decode((String) params.get(ATT_EPM_OID), "utf-8")
					: "";
			String gPart = (params.get(ATT_GPART) != null) ? URLDecoder.decode((String) params.get(ATT_GPART), "utf-8")
					: "";

			// GET NUMBER SERIAL
			if (epmOid != null && epmOid.trim().length() > 0) {
				if ((gPart != null && gPart.trim().length() > 0) && "gPart".equals(gPart)) {
					String serialNum = EpmUtil.getPdmSerialNumber(partNumber);
					partNumber = partNumber + "-" + serialNum;
				} else {
					EPMDocument epm = (EPMDocument) CommonUtil2.getInstance(epmOid);
					partNumber = epm.getNumber();
					partName = epm.getName();
				}
			} else {
				if (partNumber.length() > 15) {
					// System.out.println("partNumber ====== " + partNumber);
					partNumber = partNumber;
				} else {
					String serialNum = EpmUtil.getPdmSerialNumber(partNumber);// EpmUtil.getPdmSerialNumber(partNumber);
					partNumber = partNumber + "-" + serialNum;
				}
			}

			// Create Part
			if (epmOid != null && epmOid.trim().length() > 0) {
				if ((gPart != null && gPart.trim().length() > 0) && "gPart".equals(gPart)) {
					TypeIdentifier objType = TypeHelper.getTypeIdentifier("WCTYPE|wt.part.WTPart|com.naraenano.GPart");
					part = (WTPart) TypeHelper.newInstance(objType);
				} else {
					part = WTPart.newWTPart();
				}
			} else {
				part = WTPart.newWTPart();
			}
			// Set parameter value
			part.setNumber(partNumber);
			part.setName(partName.trim());
			if (quantityunit != null && quantityunit.length() > 0)
				part.setDefaultUnit(QuantityUnit.toQuantityUnit(quantityunit));
			else
				part.setDefaultUnit(QuantityUnit.getQuantityUnitDefault());

			part.setPartType(PartType.toPartType(wtPartType));
			part.setSource(Source.toSource(source));

			// Set Internal value
			PDMLinkProduct product = WCUtil.getPDMLinkProductForPart();
			WTContainerRef wtContainerRef = WTContainerRef.newWTContainerRef(product);
			part.setContainer(product);

			ViewHelper.assignToView(part, ViewHelper.service.getView(view));

//	        PathHelper.checkFolder(partFolder.replace(product.getName(), "Default"), wtContainerRef);
//	        Folder folder = FolderHelper.service.getFolder(partFolder.replace(product.getName(), "Default"), wtContainerRef);
			if (partFolder != null && partFolder.trim().length() > 0) {
				Folder folder = (Folder) CommonUtil2.getInstance(partFolder);
				FolderHelper.assignLocation((FolderEntry) part, folder);
			} else if ((partFolder == null || partFolder.trim().length() == 0)
					&& (partFolderPath != null && partFolderPath.trim().length() > 0)) {
				System.out.println("Received part folder path=" + partFolderPath);
				Folder folder = FolderHelper2.checkFolder(partFolderPath, wtContainerRef);
				FolderHelper.assignLocation((FolderEntry) part, folder);
			}

			LifeCycleTemplate tmpLifeCycle = LifeCycleHelper.service.getLifeCycleTemplate("Narae_LC", wtContainerRef);
			part = (WTPart) LifeCycleHelper.setLifeCycle(part, tmpLifeCycle);

			if (params.get("designed1") != null) {
				WTUser wtdevUser = UserHelper.service.getUser((String) params.get("designed1"));
				if (wtdevUser != null)
					part.setOrganizationReference(WTPrincipalReference.newWTPrincipalReference(wtdevUser));
			}

			part = (WTPart) PersistenceHelper.manager.save(part);

			// Set IBA maker, spec, existingdrawing
			HashMap<String, Object> attributes = new HashMap<String, Object>();
			if (maker != null && maker.length() > 0) {
				attributes.put(IBA_MAKER, maker);
			}
			if (partdescription != null && partdescription.length() > 0) {
				attributes.put(IBA_DESCRIPOTION, partdescription);
			}
			if (isDrawing != null && isDrawing.length() > 0)
				attributes.put(IBA_IS_DRAWING, isDrawing);
			if (spec != null && spec.length() > 0) {
				attributes.put(IBA_SPEC, spec);

				NumberCode specCode = NumberCodeHelper.manager.getNumberCodeName("SPEC", spec);
				// if(specCode != null)
				// IBAUtil.changeIBAValue(part, "Class4", specCode.getDescription());
			}

			if (attributes.size() > 0) {
				AttributeHelper.service.setValue(part, attributes);
			}

			if (docListOid != null && docListOid.length() > 0) {
				setRelevantData(part, docListOid);
			}

			if (epmOid != null && epmOid.trim().length() > 0) {
				EPMDocument epm = (EPMDocument) CommonUtil2.getInstance(epmOid);
				if ((gPart != null && gPart.trim().length() > 0) && "gPart".equals(gPart)) {
					GPartEPMLink link = GPartEPMLink.newGPartEPMLink(epm, (WTPartMaster) part.getMaster());
					link = (GPartEPMLink) PersistenceHelper.manager.save(link);
				}
			}
			trx.commit();
			trx = null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (trx != null) {
				trx.rollback();
			}
		}

		return part;
	}

	private static void setRelevantData(WTPart part, String docOids) throws WTException {
		clearRelevantData(part);

		String partOidList[] = docOids.split("[,]");
		if (partOidList.length > 0) {
			WTDocument oneObject = null;
			for (String oneOid : partOidList) {
				oneObject = (WTDocument) CommonUtil2.getInstance(oneOid.trim());
				setRelevantData(part, oneObject);
			}
		}
	}

	private static void setRelevantData(WTPart part, WTDocument doc) throws WTException {
		WTPartReferenceLink link = WTPartReferenceLink.newWTPartReferenceLink(part, (WTDocumentMaster) doc.getMaster());
		PersistenceServerHelper.manager.insert(link);
	}

	private static void clearRelevantData(WTPart part) throws WTException {
		String oid = (new ReferenceFactory()).getReferenceString(part);
		String[] oidArr = oid.split("[:]");
		long modelId = Long.valueOf(oidArr[2]).longValue();

		QuerySpec spec = new QuerySpec(WTPartReferenceLink.class);
		SearchCondition condition = new SearchCondition(WTPartReferenceLink.class, "roleAObjectRef.key.id",
				SearchCondition.EQUAL, modelId);
		spec.appendWhere(condition);

		QueryResult result = PersistenceHelper.manager.find(spec);

		if (result != null && result.size() > 0) {
			WTPartReferenceLink one = null;
			while (result.hasMoreElements()) {
				one = (WTPartReferenceLink) result.nextElement();
				PersistenceServerHelper.manager.remove(one);
			}
		}
	}

	public static HashMap<WTPart, Double> getChild(WTPart part) throws WTException {
		// List<HashMap<WTPart,Double>> children = new
		// ArrayList<HashMap<WTPart,Double>>();
		HashMap<WTPart, Double> bomValue = new HashMap<WTPart, Double>();
		WTPartStandardConfigSpec configSpec = WTPartStandardConfigSpec
				.newWTPartStandardConfigSpec(ViewHelper.service.getView("Design"), null);
		QueryResult result = WTPartHelper.service.getUsesWTParts(part, configSpec);
		System.out.println("@@@@@@@@parent:" + part.getNumber());

		while (result.hasMoreElements()) {
			Persistable[] persist = (Persistable[]) result.nextElement();
			if (!(persist[1] instanceof WTPartMaster)) {
				System.out.println("======child:" + ((WTPart) persist[1]).getNumber());
				bomValue.put((WTPart) persist[1],
						Double.valueOf(((WTPartUsageLink) persist[0]).getQuantity().getAmount()));
			} else {
				System.out.println("------Not Reansfer child:" + ((WTPartMaster) persist[1]).getNumber());
			}
		}
		return bomValue;
	}

	public static WTPartUsageLink getLinktoBOM(WTPart part, WTPartMaster master) throws WTException {
		WTPartUsageLink link = null;
		long partOid = CommonUtil.getOIDLongValue(part);
		long masterOid = CommonUtil.getOIDLongValue(master);
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTPartUsageLink.class, true);
		SearchCondition sc = new SearchCondition(WTPartUsageLink.class, "roleAObjectRef.key.id", "=", partOid);
		query.appendWhere(sc, new int[] { idx });
		query.appendAnd();
		sc = new SearchCondition(WTPartUsageLink.class, "roleBObjectRef.key.id", "=", masterOid);
		query.appendWhere(sc, new int[] { idx });
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			link = (WTPartUsageLink) obj[0];
		}

		return link;

	}

	public static void main(String args[]) {
		WTPart part;
		try {
			part = (WTPart) CommonUtil2.getInstance(args[0]);
			System.out.println("------>" + part.getState().getState().toString());
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static Hashtable getNumberAttribute(String partNum) {
		Hashtable rtnNum = new Hashtable();

		String group = "";
		String type = "";
		String unit = "";
		String class1 = "";
		String class2 = "";
		String class3 = "";
		String class4 = "";
		if (partNum != null && partNum.length() > 0) {
			group = partNum.substring(0, 1);
			type = partNum.substring(1, 2);

			if ("S".equals(type)) {
				unit = partNum.substring(3, 4);
				class1 = partNum.substring(4, 5);
				class2 = partNum.substring(6, 8);
				class3 = partNum.substring(8, 9);
				class4 = partNum.substring(9, 10);
			} else if ("A".equals(type)) {
				unit = partNum.substring(3, 5);
				class1 = partNum.substring(6, 7);
				class2 = partNum.substring(7, 10);
			} else if ("B".equals(type)) {
				unit = partNum.substring(3, 5);
				class1 = partNum.substring(6, 8);
				class2 = partNum.substring(8, 10);
			} else if ("P".equals(type)) {
				unit = partNum.substring(3, 5);
				class1 = partNum.substring(6, 8);
				class2 = partNum.substring(8, 10);
			}

		}
		rtnNum.put("group", group);
		rtnNum.put("type", type);
		rtnNum.put("unit", unit);
		rtnNum.put("class1", class1);
		rtnNum.put("class2", class2);
		rtnNum.put("class3", class3);
		rtnNum.put("class4", class4);

		return rtnNum;
	}

	public Hashtable create(final Hashtable hash) throws Exception {

		if (!SERVER) {
			Class argTypes[] = new Class[] { Hashtable.class };
			Object args[] = new Object[] { hash };
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

		String epmCreate = ParamUtil.checkStrParameter((String) hash.get("epmCreate"), "false");
		String partNumber = (String) hash.get("number");
		String partName = (String) hash.get("name");
		String quantityUnit = (String) hash.get("quantityunit");
		String wtPartType = (String) hash.get("wtPartType");
		String source = (String) hash.get("source");
		String lifecycle = (String) hash.get("lifecycle");
		String maker = (String) hash.get("maker"); // Maker
		String spec = (String) hash.get("spec"); // 규격
		String isDrawing = (String) hash.get("isDrawing"); // 도면유무
		String description = (String) hash.get("description"); // 설명
		String epmOid = ParamUtil.checkStrParameter((String) hash.get("epmOid"));
		String fid = (String) hash.get("fid");
		String[] docOids = (String[]) hash.get("docOids");
		String oldNumber = ParamUtil.checkStrParameter((String) hash.get("oldNumber"));
		String userOid = ParamUtil.checkStrParameter((String) hash.get("userOid"));

		ResourceBundle bundle = ResourceBundle.getBundle("com.e3ps.MessageResources",
				SessionHelper.manager.getLocale());

		try {
			trx.start();
//            System.out.println(">>>>>>>>>> Method SessionHelper.manager.getPrincipal().getName()1 : " +SessionHelper.manager.getPrincipal().getName());
//            System.out.println(">>>>>>>>>> userId :"+userId);
//            System.out.println(">>>>>>>>>> epmoid :"+epmOid);

			WTPart part = WTPart.newWTPart();
			String number = "";
			// 선택한 분류에 따라 자동채번

			if (epmOid != null && epmOid.length() > 0) {
				if (hash.get("gPart") != null && "gPart".equals((String) hash.get("gPart"))) {
					String serialNum = EpmUtil.getPdmSerialNumber(partNumber);
					number = partNumber + "-" + serialNum;
				} else {
					EPMDocument epm = (EPMDocument) CommonUtil.getObject(epmOid);
					number = epm.getNumber();
					partName = epm.getName();
				}
			} else {
				if ("B".equals(partNumber.substring(1, 2))) { // 구매품(B)일 경우에 체크
					if (hash.get("createType") == null || !"old".equals((String) hash.get("createType"))) {
						Hashtable rtHas = PartSearchHelper.duplicationNumber(hash);
						String dubleCheck = (String) rtHas.get("return");
						String oid = (String) rtHas.get("oid");
						if (dubleCheck.equals("true")) {
							WTPart dublePart = (WTPart) CommonUtil.getObject(oid);

							rtnVal.put("rslt", "F");
							rtnVal.put("msg", dublePart.getNumber() + "와 품목번호와 규격이 동일합니다.");
							rtnVal.put("oid", "");

							return rtnVal;
						}
					}
				}
				if (partNumber.length() > 15) {
					// System.out.println("partNumber ====== " + partNumber);
					number = partNumber;
				} else {
					String serialNum = EpmUtil.getPdmSerialNumber(partNumber);// EpmUtil.getPdmSerialNumber(partNumber);
					number = partNumber + "-" + serialNum;
				}
			}

			part.setNumber(number);

			PDMLinkProduct e3psProduct = WCUtil.getPDMLinkProduct();
			WTContainerRef wtContainerRef = WTContainerRef.newWTContainerRef(e3psProduct);
			part.setContainer(e3psProduct);

			part.setName(partName.trim());

			if (quantityUnit != null && quantityUnit.length() > 0)
				part.setDefaultUnit(QuantityUnit.toQuantityUnit(quantityUnit));
			else
				part.setDefaultUnit(QuantityUnit.getQuantityUnitDefault());

			part.setPartType(PartType.toPartType(wtPartType));
			part.setSource(Source.toSource(source));

			// 뷰 셋팅(Design 고정임)
			ViewHelper.assignToView(part, ViewHelper.service.getView((String) hash.get("view")));

			// 폴더 셋팅
			Folder folder = (Folder) CommonUtil.getObject(fid);
			FolderHelper.assignLocation((FolderEntry) part, folder);

			// 라이프사이클 셋팅(Default 고정임)
			LifeCycleTemplate tmpLifeCycle = LifeCycleHelper.service.getLifeCycleTemplate(lifecycle, wtContainerRef);
			part = (WTPart) LifeCycleHelper.setLifeCycle(part, tmpLifeCycle);

			if (hash.get("designed1") != null) {
				WTUser wtdevUser = UserHelper.service.getUser((String) hash.get("designed1"));
				if (wtdevUser != null)
					part.setOrganizationReference(WTPrincipalReference.newWTPrincipalReference(wtdevUser));
			}

			// 자재 등록
			part = (WTPart) PersistenceHelper.manager.save(part);

			// IBA(코드분류 등)정보 연결
			// EventVersionManager.manager.eventListener(part, "POST_STORE");
			if (isDrawing != null && isDrawing.length() > 0)
				IBAUtil.changeIBAValue(part, "IsDrawing", isDrawing);
			if (maker != null && maker.length() > 0)
				IBAUtil.changeIBAValue(part, "Maker", maker);
			if (spec != null && spec.length() > 0) {
				IBAUtil.changeIBAValue(part, "Spec", spec);

				NumberCode specCode = NumberCodeHelper.manager.getNumberCodeName("SPEC", spec);
				if (specCode != null)
					IBAUtil.changeIBAValue(part, "Class4", specCode.getDescription());
			}
			if (description != null && description.length() > 0) {
				IBAUtil.changeIBAValue(part, "Description", description);
			}
			if (oldNumber.length() > 0)
				IBAUtil.changeIBAValue(part, "OldNumber", oldNumber);
			IBAUtil.changeIBAValue(part, "autoNumber", "TRUE");

			ReferenceFactory rf = new ReferenceFactory();
			// 관련문서 연결
			WTDocument doc = null;
			WTPartDescribeLink dlink = null;
			if (docOids != null) {
				for (int i = 0; i < docOids.length; i++) {
					doc = (WTDocument) rf.getReference(docOids[i]).getObject();
					dlink = WTPartDescribeLink.newWTPartDescribeLink(part, doc);
					dlink = (WTPartDescribeLink) PersistenceHelper.manager.save(dlink);
				}
			}

			// 주도면 연결
			if (epmOid != null && epmOid.length() > 0) {

				EPMDocument epm = (EPMDocument) CommonUtil.getObject(epmOid);

				if (hash.get("gPart") != null && "gPart".equals((String) hash.get("gPart"))) {
					GPartEPMLink link = GPartEPMLink.newGPartEPMLink(epm, (WTPartMaster) part.getMaster());
					link = (GPartEPMLink) PersistenceHelper.manager.save(link);
				} else {

					epm = (EPMDocument) getWorkingCopy(epm);
					if (WorkInProgressHelper.isCheckedOut(epm)) {
						epm = (EPMDocument) WorkInProgressHelper.service.checkin(epm, "");
					}
					if (epm != null) {
						EPMBuildRule link = EPMBuildRule.newEPMBuildRule(epm, part);
						link = (EPMBuildRule) PersistenceHelper.manager.save(link);
						BuildHelper.service.buildTargetsForSource(epm);

						part = (WTPart) link.getBuildTarget();

						part = (WTPart) CommonUtil.getObject((CommonUtil.getVROID(part)));
						if (isDrawing != null && isDrawing.length() > 0)
							IBAUtil.changeIBAValue(part, "IsDrawing", isDrawing);
						if (maker != null && maker.length() > 0)
							IBAUtil.changeIBAValue(part, "Maker", maker);
						if (spec != null && spec.length() > 0)
							IBAUtil.changeIBAValue(part, "Spec", spec);
						if (oldNumber.length() > 0)
							IBAUtil.changeIBAValue(part, "OldNumber", oldNumber);
						IBAUtil.changeIBAValue(part, "autoNumber", "TRUE");
						// 체크인

					}
				}
			} else {
				QueryResult qr = PersistenceHelper.manager.navigate(part, "buildSource", EPMBuildRule.class, true);

				while (qr.hasMoreElements()) {
					System.out.println("buildsource :" + qr.nextElement());
				}
			}
//            System.out.println(">>>>>>>>>> getVROID :" +CommonUtil.getVROID(part));

			rtnVal.put("rslt", "S");
			rtnVal.put("msg", bundle.getString("E3PS.Msg.874"));
			rtnVal.put("oid", CommonUtil.getVROID(part));

			trx.commit();
			trx = null;
		} catch (Exception e) {
			rtnVal.put("rslt", "F");
			rtnVal.put("msg", bundle.getString("E3PS.Msg.873"));
			rtnVal.put("oid", "");
			e.printStackTrace();
			// throw new WTException(e);
		} finally {
			if (trx != null) {
				trx.rollback();
			}
		}

		return rtnVal;
		// ##end createPart%3E48C16400F2.body
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

	public WTPart reviseUpdate(final WTPart befPart) throws Exception {
		if (!SERVER) {
			Class argTypes[] = new Class[] { WTPart.class };
			Object args[] = new Object[] { befPart };
			try {
				return (WTPart) wt.method.RemoteMethodServer.getDefault().invoke("reviseUpdate", null, this, argTypes,
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
		WTPart revisePart = null;

		try {
			trx.start();
			Hashtable hash = new Hashtable();
			// String[] docOids = (String[]) hash.get("docOids");

			if (befPart != null) {
				PartData data = new PartData(befPart);
				hash.put("oid", data.getOid());
				hash.put("name", data.getName());

				hash.put("quantityunit", data.getUnitCode());

				// 관련문서 OID연결
				QueryResult linkQr = PersistenceHelper.manager.navigate(befPart, "describedBy",
						WTPartDescribeLink.class);
				String[] relDOc = null;
				int inxA = 0;
				if ((linkQr != null) && (linkQr.size() > 0)) {
					relDOc = new String[linkQr.size()];
					while (linkQr.hasMoreElements()) {
						relDOc[inxA++] = CommonUtil.getOIDString((WTDocument) linkQr.nextElement());
					}
				}
				if (relDOc != null) {
					hash.put("docOids", relDOc);
				}

				// 개정
				Hashtable rtnHash = reviseUpdate(hash);
				ReferenceFactory rf = new ReferenceFactory();
				revisePart = (WTPart) rf.getReference((String) rtnHash.get("oid")).getObject();
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
		return revisePart;
	}

	/**
	 * 갱신처리
	 * 
	 * @param hash
	 * @return
	 * @throws Exception
	 */
	public Hashtable reviseUpdate(final Hashtable hash) throws Exception {
		if (!SERVER) {
			Class argTypes[] = new Class[] { Hashtable.class };
			Object args[] = new Object[] { hash };
			try {
				return (Hashtable) wt.method.RemoteMethodServer.getDefault().invoke("reviseUpdate", null, this,
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

		Hashtable rtnVal = null;
		Transaction trx = new Transaction();

		String oid = (String) hash.get("oid");

		try {
			trx.start();

			if (StringUtil.checkString(oid)) {
				ReferenceFactory rf = new ReferenceFactory();
				Versioned vs = (Versioned) rf.getReference(oid).getObject();

				// 개정전 셋팅정보 get(lifecycle, folder, view)
				String lifecycle = ((LifeCycleManaged) vs).getLifeCycleName();
				Folder folder = FolderHelper.service.getFolder((FolderEntry) vs);

				WTPart part = (WTPart) VersionControlHelper.service.newVersion(vs);
				PDMLinkProduct e3psProduct = WCUtil.getPDMLinkProduct();
				WTContainerRef wtContainerRef = WTContainerRef.newWTContainerRef(e3psProduct);
				part.setContainer(e3psProduct);

				// 폴더 셋팅
				FolderHelper.assignLocation((FolderEntry) part, folder);

				// 라이프사이클 셋팅(Default 고정임)
				LifeCycleTemplate tmpLifeCycle = LifeCycleHelper.service.getLifeCycleTemplate(lifecycle,
						wtContainerRef);
				part = (WTPart) LifeCycleHelper.setLifeCycle(part, tmpLifeCycle);

				// 저장
				part = (WTPart) PersistenceHelper.manager.save(part);

				hash.put("oid", CommonUtil.getOIDString(part));

				rtnVal = modify(hash, true);
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
		return rtnVal;
	}

	public Hashtable modify(final Hashtable hash, final boolean reviseFlag) throws Exception {
		Hashtable rtnVal = new Hashtable();

		String oid = (String) hash.get("oid");
		String partNumber = (String) hash.get("number");
		String partName = (String) hash.get("name");
		String quantityUnit = (String) hash.get("quantityunit");
		String wtPartType = (String) hash.get("wtPartType");
		String source = (String) hash.get("source");
		String lifecycle = (String) hash.get("lifecycle");

		String isDrawing = (String) hash.get("isDrawing");

		// 자재코드 채번정보
		Hashtable rtnNum = getNumberAttribute(partNumber);//

		String group = (String) rtnNum.get("group"); // 품목 종류
		String type = (String) rtnNum.get("type"); // 품목분류
		String unit = (String) rtnNum.get("unit"); // 대분류1
		String class1 = (String) rtnNum.get("class1"); // 대분류2
		String class2 = (String) rtnNum.get("class2"); // 중분류1
		String class3 = (String) rtnNum.get("class3"); // 중분류2
		String class4 = (String) rtnNum.get("class4"); // 중분류3

		String maker = (String) hash.get("maker"); // Maker
		String spec = (String) hash.get("spec"); // 규격
		String description = (String) hash.get("description"); // 설명

		String epmOid = (String) hash.get("epmOid");
		String fid = (String) hash.get("fid");
		String[] docOids = (String[]) hash.get("docOids");
		WTPart part = null;
		WTPart oldPart = null;
		WTPartMaster partMaster = null;

		try {
			if (StringUtil.checkString(oid)) {
				ReferenceFactory rf = new ReferenceFactory();
				oldPart = (WTPart) rf.getReference(oid).getObject();

				PartData oldData = new PartData(oldPart);
				String oldSpec = oldData.getSpec();

				boolean isChangeSpec = false;

				if (oldSpec != null && oldSpec.length() > 0) {
					if (spec != null && spec.length() > 0) {
						if (!spec.equals(oldSpec)) {
							isChangeSpec = true;
						}
					} else {
						isChangeSpec = true;
					}
				} else if (spec != null && spec.length() > 0) {
					isChangeSpec = true;
				}

				// Working Copy
				part = (WTPart) getWorkingCopy(oldPart);
				part.setDefaultUnit(QuantityUnit.toQuantityUnit(quantityUnit));
				part = (WTPart) PersistenceHelper.manager.modify(part);

				// IBA정보 연결
				EventVersionManager.manager.eventListener(part, "POST_STORE");
				IBAUtil.changeIBAValue(part, "Description", description);// 설명
				IBAUtil.changeIBAValue(part, "IsDrawing", isDrawing); // 도면 유무
				IBAUtil.changeIBAValue(part, "Maker", maker); // Maker
				IBAUtil.changeIBAValue(part, "Spec", spec); // 규격
				IBAUtil.changeIBAValue(part, "quantityunit", quantityUnit); // 단위
				IBAUtil.changeIBAValue(part, "autoNumber", "TRUE");

				String autoNumberValue = "";
				if (maker.length() > 0) {
					HashMap erpMap = new HashMap();
					erpMap.put("makerName", maker);
					erpMap.put("searchType", "equals");
					ResultSet rs = ERPSearchHelper.manager.getErpMaker(erpMap);
					if (!rs.next())
						autoNumberValue = "(" + maker + ")" + "입력하신 Maker의 정보를 찾을 수 없습니다";
				}

				IBAUtil.changeIBAValue(part, "message", autoNumberValue);

				// 나머지 코드 초기화
				IBAUtil.changeIBAValue(part, "Group", "");
				IBAUtil.changeIBAValue(part, "Type", "");
				IBAUtil.changeIBAValue(part, "Unit", "");
				IBAUtil.changeIBAValue(part, "Class1", "");
				IBAUtil.changeIBAValue(part, "Class2", "");
				IBAUtil.changeIBAValue(part, "Class3", "");
				IBAUtil.changeIBAValue(part, "Class4", "");

				IBAUtil.changeIBAValue(part, "Approved", "");
				IBAUtil.changeIBAValue(part, "Checked", "");
				IBAUtil.changeIBAValue(part, "DRW_type", "");
				IBAUtil.changeIBAValue(part, "Group_Name", "");
				IBAUtil.changeIBAValue(part, "LDate", "");
				IBAUtil.changeIBAValue(part, "Match_part", "");
				IBAUtil.changeIBAValue(part, "Material", "");
				IBAUtil.changeIBAValue(part, "P_Name", "");
				IBAUtil.changeIBAValue(part, "Ref_Model_no", "");
				IBAUtil.changeIBAValue(part, "Treatment", "");
				IBAUtil.changeIBAValue(part, "Weight_f", "");

				// 관련문서 연결
				QueryResult results = PersistenceHelper.manager.navigate(part, "describedBy", WTPartDescribeLink.class,
						false);
				while (results.hasMoreElements()) {
					PersistenceServerHelper.manager.remove((WTPartDescribeLink) results.nextElement());
				}

				WTDocument doc = null;
				WTPartDescribeLink dlink = null;
				if (docOids != null) {
					for (int i = 0; i < docOids.length; i++) {
						doc = (WTDocument) rf.getReference(docOids[i]).getObject();
						dlink = WTPartDescribeLink.newWTPartDescribeLink(part, doc);
						dlink = (WTPartDescribeLink) PersistenceHelper.manager.save(dlink);
					}
				}

				// 체크인
				if (WorkInProgressHelper.isCheckedOut(part)) {
					part = (WTPart) WorkInProgressHelper.service.checkin(part, "");
				}
				part = (WTPart) PersistenceHelper.manager.refresh(part);

				// 제품분류에 따라 폴더설정
				if (fid != null && fid.length() > 0) {

					Folder folder = (Folder) CommonUtil.getObject(fid);
					FolderHelper.service.changeFolder((FolderEntry) part, folder);

				}

				// 주도면 연결
				EPMDocument epmOrg = DrawingHelper.manager.getEPMDocument(part);
				EPMDocument epmNew = null;
				boolean isChangeEpm = false;
				String gPart = (String) hash.get("gPart");
				if (epmOid.length() > 0) {
					if (gPart != null && "gPart".equals(gPart)) {
						QuerySpec gPartQs = new QuerySpec(GPartEPMLink.class);
						gPartQs.appendWhere(
								new SearchCondition(GPartEPMLink.class, "roleAObjectRef.key.id", "=",
										part.getMaster().getPersistInfo().getObjectIdentifier().getId()),
								new int[] { 0 });

						QueryResult result = PersistenceHelper.manager.find(gPartQs);
						if (result.hasMoreElements()) {
							GPartEPMLink oldLink = (GPartEPMLink) result.nextElement();
							PersistenceHelper.manager.delete(oldLink);
						}

						epmNew = (EPMDocument) CommonUtil.getObject(epmOid);
						GPartEPMLink link = GPartEPMLink.newGPartEPMLink(epmNew, (WTPartMaster) part.getMaster());
						link = (GPartEPMLink) PersistenceHelper.manager.save(link);

					} else {
						epmNew = (EPMDocument) CommonUtil.getObject(epmOid);

						epmNew = (EPMDocument) getWorkingCopy(epmNew);
						if (WorkInProgressHelper.isCheckedOut(epmNew)) {
							epmNew = (EPMDocument) WorkInProgressHelper.service.checkin(epmNew, "");
						}

						if (epmOrg == null) {
							EPMBuildRule link = EPMBuildRule.newEPMBuildRule(epmNew, part);
							PersistenceHelper.manager.save(link);
							// PersistenceServerHelper.manager.insert(link);
							BuildHelper.service.buildTargetsForSource(epmNew);

							part = (WTPart) link.getBuildTarget();
							part = (WTPart) CommonUtil.getObject((CommonUtil.getVROID(part)));
							IBAUtil.changeIBAValue(part, "Description", description);// 설명
							IBAUtil.changeIBAValue(part, "IsDrawing", isDrawing); // 도면 유무
							IBAUtil.changeIBAValue(part, "Maker", maker); // Maker
							IBAUtil.changeIBAValue(part, "Spec", spec); // 규격
							IBAUtil.changeIBAValue(part, "quantityunit", quantityUnit); // 단위
							IBAUtil.changeIBAValue(part, "autoNumber", "TRUE");

							isChangeEpm = true;
						} else {
							EPMBuildRule newEbr = PartSearchHelper.getBuildRule(part);
							newEbr.setBuildSource(epmNew);
							PersistenceServerHelper.manager.update(newEbr);
							EPMBuildHistory ebh = PartSearchHelper.getBuildHistory(part, epmNew);
							if (ebh != null) {
								ebh.setBuiltBy(epmNew);
								PersistenceServerHelper.manager.update(ebh);
							}
						}
					}
				} else {
					if (gPart != null && "gPart".equals(gPart)) {
						QuerySpec gPartQs = new QuerySpec(GPartEPMLink.class);
						gPartQs.appendWhere(
								new SearchCondition(GPartEPMLink.class, "roleAObjectRef.key.id", "=",
										part.getMaster().getPersistInfo().getObjectIdentifier().getId()),
								new int[] { 0 });

						QueryResult result = PersistenceHelper.manager.find(gPartQs);
						if (result.hasMoreElements()) {
							GPartEPMLink oldLink = (GPartEPMLink) result.nextElement();
							PersistenceHelper.manager.delete(oldLink);
						}
					}
				}

				// 자재명
				partMaster = (WTPartMaster) (part.getMaster());
				String tempNumber = partNumber.substring(0, 10);

				String tempOldNumber = part.getNumber();
				if (tempOldNumber.length() > 10) {
					tempOldNumber = tempOldNumber.substring(0, 10);
				}

				String number = "";
				if (!tempOldNumber.equals(tempNumber)) {

					if ("B".equals(partNumber.substring(1, 2))) { // 구매품(B)일 경우에 체크
//                		System.out.println(">>>> duplicationNumber <<<< ");
						Hashtable rtHas = PartSearchHelper.duplicationNumber(hash);

						String dubleCheck = (String) rtHas.get("return");
						String dubleoid = (String) rtHas.get("oid");

						if (dubleCheck.equals("true")) {
							WTPart dublePart = (WTPart) CommonUtil.getObject(dubleoid);
							if (!(dublePart.getNumber().equals(part.getNumber()))) {
								rtnVal.put("rslt", "F");
								rtnVal.put("msg", dublePart.getNumber() + "의 속성값이 동일합니다.");
								rtnVal.put("oid", "");

								return rtnVal;
							} else {
								NumberCode specCode = NumberCodeHelper.manager.getNumberCodeName("SPEC", spec);
								if (specCode != null)
									IBAUtil.changeIBAValue(part, "Class4", specCode.getDescription());
							}

						} else {
							NumberCode specCode = NumberCodeHelper.manager.getNumberCodeName("SPEC", spec);
							if (specCode != null)
								IBAUtil.changeIBAValue(part, "Class4", specCode.getDescription());
						}
					}

					if (isChangeEpm) {

						WTPartMasterIdentity identity = (WTPartMasterIdentity) partMaster.getIdentificationObject();
						identity.setName(partName);
						identity.setNumber(partNumber);
						partMaster = (WTPartMaster) IdentityHelper.service.changeIdentity(partMaster, identity);
					} else {

						// part number Change

						if (partNumber.length() > 15) {
							// System.out.println("partNumber ====== " + partNumber);
							number = partNumber;
						} else {
							String serialNum = EpmUtil.getPdmSerialNumber(partNumber);// EpmUtil.getPdmSerialNumber(partNumber);
							number = partNumber + "-" + serialNum;
						}

						WTPartMasterIdentity identity = (WTPartMasterIdentity) partMaster.getIdentificationObject();
						identity.setName(partName);
						identity.setNumber(number);
						partMaster = (WTPartMaster) IdentityHelper.service.changeIdentity(partMaster, identity);

						// EPMDocument chabnge
						EPMDocument changEPM = null;
						// if(epmOrg != null) changEPM = epmOrg;
						if (epmNew != null)
							changEPM = epmNew;

						if (changEPM != null) {
							/* 3D Number, Name,CadName Change */
							String cadName = number + EpmUtil.getCadExtension(changEPM.getCADName());
							boolean isNumber3D = false;
							if (epmNew.isGeneric() || epmNew.isInstance()) {
								isNumber3D = CadInfoChange.manager.epmInfoChange(changEPM, number, partName);
								if (isNumber3D) {
									IBAUtil.changeIBAValue(changEPM, "autoNumber", "TRUE");
								} else {
									IBAUtil.changeIBAValue(changEPM, "autoNumber", "FALSE");
								}
							} else {
								isNumber3D = CadInfoChange.manager.epmCadInfoChange(changEPM, number, partName,
										cadName);
								if (isNumber3D) {
									IBAUtil.changeIBAValue(changEPM, "autoNumber", "TRUE");
								} else {
									IBAUtil.changeIBAValue(changEPM, "autoNumber", "FALSE");
								}
							}

							if ("S".equals(type)) {
								NumberCode unitCode = NumberCodeHelper.manager.getNumberCode("SBUSINESS", unit);
								// IBAUtil.createIba(changEPM, "string", "Group_Name", unitCode.getName());
								IBAUtil.changeIBAValue(changEPM, "Group_Name", unitCode.getName());
//                				System.out.println("@@@@@@ Group_Name111 ====   " + unitCode.getName());
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
									// IBAUtil.createIba(changEPM, "string", "Group_Name", unitCode.getName());
									IBAUtil.changeIBAValue(changEPM, "Group_Name", unitCode.getName());
//                					System.out.println("@@@@@@ Group_Name222 ====   " + unitCode.getName());
								}

							}
//                			System.out.println("@@@@@@ pdmName ====   " + partName);
							IBAUtil.changeIBAValue(changEPM, "P_Name", partName);

							// 코드 입력
							IBAUtil.changeIBAValue(changEPM, "Group", group);
							IBAUtil.changeIBAValue(changEPM, "Type", type);
							IBAUtil.changeIBAValue(changEPM, "Unit", unit);
							IBAUtil.changeIBAValue(changEPM, "Class1", class1);
							IBAUtil.changeIBAValue(changEPM, "Class2", class2);
							IBAUtil.changeIBAValue(changEPM, "Class3", class3);
							if (!"B".equals(type))
								IBAUtil.changeIBAValue(changEPM, "Class4", class4);

							IBAUtil.changeIBAValue(changEPM, "IsDrawing", isDrawing); // 도면 유무
							IBAUtil.changeIBAValue(changEPM, "Maker", maker); // Maker
							IBAUtil.changeIBAValue(changEPM, "Spec", spec); // 규격
							IBAUtil.changeIBAValue(changEPM, "quantityunit", quantityUnit); // 단위

							IBAUtil.changeIBAValue(changEPM, "message", autoNumberValue);

							EpmPublishUtil.publish(changEPM);

							/* 2D Drawing Number, Name,CadName Change */
							// Vector refedByVec = EpmSearchHelper.manager.getReferenceDependency(changEPM,
							// "referencedBy");
							EPMDocument epm2d = EpmSearchHelper.manager
									.getEPM2D((EPMDocumentMaster) changEPM.getMaster());
							if (epm2d != null) {
								cadName = number + EpmUtil.getCadExtension(epm2d.getCADName());
								;
//    		    				System.out.println (">>>>>>>> 3D referencedBy epm2d : " + epm2d.getNumber());
								number = number + "_2D";
								boolean isNumber2D = CadInfoChange.manager.epmCadInfoChange(epm2d, number, partName,
										cadName);
								if (isNumber2D) {
									IBAUtil.changeIBAValue(epm2d, "autoNumber", "TRUE");
								} else {
									IBAUtil.changeIBAValue(epm2d, "autoNumber", "FALSE");
								}

								if ("S".equals(type)) {
									NumberCode unitCode = NumberCodeHelper.manager.getNumberCode("SBUSINESS", unit);
									IBAUtil.createIba(epm2d, "string", "Group_Name", unitCode.getName());
//                    				System.out.println("@@@@@@ Group_Name333 ====   " + unitCode.getName());
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
										IBAUtil.createIba(epm2d, "string", "Group_Name", unitCode.getName());
//                    					System.out.println("@@@@@@ Group_Name44 ====   " + unitCode.getName());
									}
								}
//                    			System.out.println("##### Pulish EPM Start #####");
								EpmPublishUtil.publish(epm2d);
//                    			System.out.println("##### Pulish EPM End #####");
							}
							/*
							 * EPMDocument epm2d = null; wt.epm.structure.EPMReferenceLink referenceLink =
							 * null; if(refedByVec.size()>0 ){ int h=0; String nummber2D =""; for(int i=0;
							 * i< refedByVec.size(); i++) { referenceLink =
							 * (wt.epm.structure.EPMReferenceLink)refedByVec.get(i);
							 * 
							 * epm2d = (EPMDocument)referenceLink.getReferencedBy();
							 * 
							 * cadName = number+"_2D"+EpmUtil.getCadExtension(epm2d.getCADName());;
							 * System.out.println (">>>>>>>> 3D referencedBy epm2d : " + epm2d.getNumber());
							 * number = number+"_2D"; boolean isNumber2D
							 * =CadInfoChange.manager.epmCadInfoChange(epm2d, number, partName, cadName);
							 * if(isNumber2D){ IBAUtil.changeIBAValue(epm2d, "autoNumber", "TRUE"); }else{
							 * IBAUtil.changeIBAValue(epm2d, "autoNumber", "FALSE"); } h++;
							 * 
							 * if("S".equals(partAttribute)) { NumberCode unitCode =
							 * NumberCodeHelper.manager.getNumberCode("SBUSINESS", attribute1);
							 * IBAUtil.createIba(epm2d, "string", "Group_Name", unitCode.getName()); }else {
							 * NumberCode paCode = NumberCodeHelper.manager.getNumberCode("CADATTRIBUTE",
							 * partAttribute); HashMap map = new HashMap(); map.put("code", attribute1);
							 * map.put("type", "CADATTRIBUTE"); map.put("parent", paCode);
							 * 
							 * QuerySpec qs = NumberCodeHelper.getCodeQuerySpec(map); QueryResult qr =
							 * PersistenceHelper.manager.find(qs); if(qr.hasMoreElements()) { Object[] obj =
							 * (Object[])qr.nextElement(); NumberCode unitCode = (NumberCode)obj[0];
							 * IBAUtil.createIba(epm2d, "string", "Group_Name", unitCode.getName()); } } } }
							 */
						}
					}
				} else if (isChangeSpec) {
//                	System.out.println("################################");
//                	System.out.println("Test");
//                	System.out.println("################################");
					if ("B".equals(partNumber.substring(1, 2))) { // 구매품(B)일 경우에 체크
//                		System.out.println(">>>> duplicationNumber <<<< ");
						Hashtable rtHas = PartSearchHelper.duplicationNumber(hash);

						String dubleCheck = (String) rtHas.get("return");
						String dubleoid = (String) rtHas.get("oid");

						if (dubleCheck.equals("true")) {
							WTPart dublePart = (WTPart) CommonUtil.getObject(oid);
							if (!(dublePart.getNumber().equals(part.getNumber()))) {
								rtnVal.put("rslt", "F");
								rtnVal.put("msg", dublePart.getNumber() + "의 속성값이 동일합니다.");
								rtnVal.put("oid", "");

//                             System.out.println("asdfasdf = " + rtnVal.get("msg"));
							} else {
								NumberCode specCode = NumberCodeHelper.manager.getNumberCodeName("SPEC", spec);
								if (specCode != null)
									IBAUtil.changeIBAValue(part, "Class4", specCode.getDescription());
							}

						} else {
							NumberCode specCode = NumberCodeHelper.manager.getNumberCodeName("SPEC", spec);
							if (specCode != null)
								IBAUtil.changeIBAValue(part, "Class4", specCode.getDescription());
						}
					}
					// 스펙만 바꼈을시 3d도면에 spec 입력 코드

					EPMDocument epm = DrawingHelper.manager.getEPMDocument(part);
					IBAUtil.changeIBAValue(epm, "IsDrawing", isDrawing); // 도면 유무
					IBAUtil.changeIBAValue(epm, "Maker", maker); // Maker
					IBAUtil.changeIBAValue(epm, "Spec", spec); // 규격
					IBAUtil.changeIBAValue(epm, "quantityunit", quantityUnit); // 단위
					if ("B".equals(epm.getNumber().substring(1, 2))) { // 구매품(B)일 경우에 체크
						NumberCode specCode = NumberCodeHelper.manager.getNumberCodeName("SPEC", spec);
						if (specCode != null)
							IBAUtil.changeIBAValue(part, "Class4", specCode.getDescription());
					}

//                	System.out.println("################################");
//                	System.out.println("Test End");
//                	System.out.println("################################");
				}

				// 단위변경
				if (!(partMaster.getDefaultUnit().toString()).equals(quantityUnit)) {
					partMaster.setDefaultUnit(QuantityUnit.toQuantityUnit(quantityUnit));
					partMaster = (WTPartMaster) PersistenceHelper.manager.modify(partMaster);
				}
			}

			rtnVal.put("oid", CommonUtil.getVROID(part));
		} catch (Exception e) {
			throw new WTException(e);
		}

		return rtnVal;
		// ##end createPart%3E48C16400F2.body
	}

	public WTPart getPart(final String number, final String version) throws Exception {

		QuerySpec qs = new QuerySpec(WTPart.class);

		qs.appendWhere(VersionControlHelper.getSearchCondition(WTPart.class, true), new int[] { 0 });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(WTPart.class, "master>number", "=", number), new int[] { 0 });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(WTPart.class, "versionInfo.identifier.versionId", "=", version),
				new int[] { 0 });
		QueryResult qr = PersistenceHelper.manager.find(qs);
		if (qr.hasMoreElements()) {
			return (WTPart) qr.nextElement();
		}

		return null;
	}

	public WTPart getPart(final String number) throws Exception {

		QuerySpec qs = new QuerySpec(WTPart.class);

		qs.appendWhere(VersionControlHelper.getSearchCondition(WTPart.class, true), new int[] { 0 });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(WTPart.class, "master>number", "=", number), new int[] { 0 });

		SearchUtil.addLastVersionCondition(qs, WTPart.class, 0);

		QueryResult qr = PersistenceHelper.manager.find(qs);
		if (qr.hasMoreElements()) {
			Object o = qr.nextElement();
			if (o instanceof WTPart) {
				return (WTPart) o;
			} else {
				Object[] arry = (Object[]) o;
				return (WTPart) arry[0];
			}
		}

		return null;
	}

	public String getLatestMapprovalState(final WTPart _part) {
		String state = "";

		try {
			MultiApproval mapproval = getLatestApproval(_part);

			if (mapproval != null) {
				state = ApprovalHelper.manager.getState(mapproval);
			}
		} catch (Exception e) {
			state = "Error";
		}

		return state;
	}

	public MultiApproval getLatestApproval(final WTPart _part) throws Exception {
		if (!SERVER) {
			Class argTypes[] = new Class[] { WTPart.class };
			Object args[] = new Object[] { _part };
			try {
				return (MultiApproval) wt.method.RemoteMethodServer.getDefault().invoke("getLatestApproval", null, this,
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

		MultiApproval mapproval = null;

		try {
			QuerySpec qs = new QuerySpec(ApprovalToObjectLink.class);
			qs.appendWhere(new SearchCondition(ApprovalToObjectLink.class, "roleBObjectRef.key.id",
					SearchCondition.EQUAL, _part.getPersistInfo().getObjectIdentifier().getId()), new int[] { 0 });
			// 정렬
			qs.appendOrderBy(
					new OrderBy(new ClassAttribute(ApprovalToObjectLink.class, "thePersistInfo.createStamp"), false),
					new int[] { 0 });
			QueryResult qr = PersistenceHelper.manager.find(qs);

			ApprovalToObjectLink alink = null;

			while (qr.hasMoreElements()) {
				alink = (ApprovalToObjectLink) qr.nextElement();
				mapproval = alink.getApprove();
			}
		} catch (Exception e) {
			throw e;
		}

		return mapproval;
	}

	public String getNextNumber(String number) throws Exception {
		DecimalFormat df = new DecimalFormat("00000");
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTPartMaster.class, true);

		SearchCondition sc = new SearchCondition(WTPartMaster.class, WTPartMaster.NUMBER, "LIKE", number + "%");
		query.appendWhere(sc, new int[] { idx });

		ClassAttribute ca = new ClassAttribute(WTPartMaster.class, WTPartMaster.NUMBER);
		OrderBy by = new OrderBy(ca, true);
		query.appendOrderBy(by, new int[] { idx });

		QueryResult result = PersistenceHelper.manager.find(query);

		int comp = 10000;
		if (number.startsWith("NP-00-SMSS")) {
			comp = 95000;
		} else if (number.startsWith("NA-")) {
			comp = 300;
			df = new DecimalFormat("000");
		} else if (number.startsWith("TA-")) {
			comp = 100;
			df = new DecimalFormat("000");
		}

		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WTPartMaster master = (WTPartMaster) obj[0];
			String n = master.getNumber();
			System.out.println("채번 하기 위한것 부품 = " + n);
			int ext = n.lastIndexOf("-");
			int _ext = n.lastIndexOf(".");
			// 확장자 있을 경우
			if (ext > -1 && _ext > -1) {
				n = n.substring(ext + 1, _ext); // 002....
				int reValue = Integer.parseInt(n);
				System.out.println("re=" + reValue);
				if (reValue <= comp) {
					reValue = comp;
				}
				return df.format(reValue + 1);
			}

			if (ext > -1 && _ext <= -1) {
				int _idx = n.lastIndexOf("-");
				n = n.substring(_idx + 1);
				int reValue = Integer.parseInt(n);
				System.out.println("r1e=" + reValue);
				if (reValue <= comp) {
					reValue = comp;
				}
				return df.format(reValue + 1);
			}
		}
		return String.valueOf(comp);
	}
}
