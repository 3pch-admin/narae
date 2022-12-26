package ext.narae.service.drawing.beans;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import com.ptc.wvs.server.util.PublishUtils;

import ext.narae.service.drawing.REFDWGLink;
import ext.narae.service.drawing.WTDocEPMDocLink;
import ext.narae.service.folder.beans.CommonFolderHelper;
import ext.narae.service.org.People;
import ext.narae.util.CommonUtil;
import ext.narae.util.DateUtil;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.structure.EPMReferenceLink;
import wt.epm.structure.EPMStructureHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.IteratedFolderMemberLink;
import wt.httpgw.URLFactory;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.StringSearch;
import wt.representation.Representation;
import wt.util.WTException;
import wt.vc.VersionControlHelper;

public class EpmSearchHelper implements wt.method.RemoteAccess {

	static final boolean SERVER = wt.method.RemoteMethodServer.ServerFlag;
	public static EpmSearchHelper manager = new EpmSearchHelper();

	public QueryResult getSearchEpm(HashMap map) throws Exception {

		if (!SERVER) {
			Class argTypes[] = new Class[] { HashMap.class, };
			Object args[] = new Object[] { map };
			try {
				return (QueryResult) wt.method.RemoteMethodServer.getDefault().invoke("getSearchEpm", null, this,
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

		QuerySpec query = null;
		QueryResult qr = null;
		String number = "";
		String nameValue = "";
		String description = "";
		String creator = "";
		String userName = "";
		String predate = "";
		String postdate = "";
		Folder folder = null;
		try {
			query = new QuerySpec();
			int idx = query.addClassList(EPMDocument.class, true);

			// 최신 이터레이션
			query.appendWhere(VersionControlHelper.getSearchCondition(EPMDocument.class, true), new int[] { idx });

			if (number != null && number.trim().length() > 0) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				StringSearch stringsearch = new StringSearch("number");
				stringsearch.setValue("%" + number.trim() + "%");
				query.appendWhere(stringsearch.getSearchCondition(EPMDocument.class), new int[] { idx });
			} else
				number = "";

			if (nameValue != null && nameValue.trim().length() > 0) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				StringSearch stringsearch = new StringSearch("name");
				stringsearch.setValue("%" + nameValue.trim() + "%");
				query.appendWhere(stringsearch.getSearchCondition(EPMDocument.class), new int[] { idx });
			} else
				nameValue = "";

			if (description != null && description.trim().length() > 0) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				StringSearch stringsearch = new StringSearch("description");
				stringsearch.setValue("%" + description.trim() + "%");
				query.appendWhere(stringsearch.getSearchCondition(EPMDocument.class), new int[] { idx });
			} else
				description = "";

			if (creator != null && creator.length() > 0) {
				People people = (People) CommonUtil.getObject(creator);
				WTUser user = people.getUser();
				userName = user.getFullName();

				if (query.getConditionCount() > 0)
					query.appendAnd();
				query.appendWhere(new SearchCondition(EPMDocument.class, "iterationInfo.creator.key", "=",
						PersistenceHelper.getObjectIdentifier(user)), new int[] { idx });
			}

			if (predate != null && predate.length() > 0) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				query.appendWhere(new SearchCondition(EPMDocument.class, "thePersistInfo.createStamp", ">",
						DateUtil.convertStartDate(predate.trim())), new int[] { idx });
			} else
				predate = "";
			if (postdate != null && postdate.length() > 0) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				query.appendWhere(new SearchCondition(EPMDocument.class, "thePersistInfo.createStamp", "<",
						DateUtil.convertEndDate(postdate.trim())), new int[] { idx });
			} else
				postdate = "";

			// folder search
			if (folder != null) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				int folder_idx = query.addClassList(IteratedFolderMemberLink.class, false);
				SearchCondition sc1 = new SearchCondition(
						new ClassAttribute(IteratedFolderMemberLink.class, "roleBObjectRef.key.branchId"), "=",
						new ClassAttribute(EPMDocument.class, "iterationInfo.branchId"));
				sc1.setFromIndicies(new int[] { folder_idx, idx }, 0);
				sc1.setOuterJoin(0);
				query.appendWhere(sc1, new int[] { folder_idx, idx });

				query.appendAnd();
				ArrayList folders = CommonFolderHelper.getFolderTree(folder);
				// folders.add(folder);

				query.appendOpenParen();

				query.appendWhere(
						new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id",
								SearchCondition.EQUAL, folder.getPersistInfo().getObjectIdentifier().getId()),
						new int[] { folder_idx });

				for (int fi = 0; fi < folders.size(); fi++) {
					String[] s = (String[]) folders.get(fi);
					Folder sf = (Folder) CommonUtil.getObject(s[0]);
					// if(fi > 0) {
					query.appendOr();
					// }
					query.appendWhere(
							new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id",
									SearchCondition.EQUAL, sf.getPersistInfo().getObjectIdentifier().getId()),
							new int[] { folder_idx });
				}
				query.appendCloseParen();
			}

			String islastversion = ""; // (String)request.getParameter("islastversion");
			if (islastversion == null || islastversion.length() < 1)
				islastversion = "true";

			if ("true".equals(islastversion)) {
				AttributeDefDefaultView aview = IBADefinitionHelper.service
						.getAttributeDefDefaultViewByPath("LatestVersionFlag");

				if (aview != null) {
					if (query.getConditionCount() > 0)
						query.appendAnd();

					int _idx = query.appendClassList(wt.iba.value.StringValue.class, false);
					SearchCondition sc = new SearchCondition(wt.iba.value.StringValue.class,
							"theIBAHolderReference.key.id", EPMDocument.class, "thePersistInfo.theObjectIdentifier.id");
					query.appendWhere(sc, new int[] { _idx, idx });
					query.appendAnd();
					sc = new SearchCondition(wt.iba.value.StringValue.class, "definitionReference.hierarchyID", "=",
							aview.getHierarchyID());
					query.appendWhere(sc, new int[] { _idx });
					query.appendAnd();
					sc = new SearchCondition(wt.iba.value.StringValue.class, "value", "=", "TRUE");
					query.appendWhere(sc, new int[] { _idx });
				}
			}
			String sortValue = "";
			String sortCheck = "";
			if (sortValue != null && sortValue.length() > 0) {
				if ("true".equals(sortCheck))
					query.appendOrderBy(new OrderBy(new ClassAttribute(EPMDocument.class, sortValue), true),
							new int[] { idx });
				else
					query.appendOrderBy(new OrderBy(new ClassAttribute(EPMDocument.class, sortValue), false),
							new int[] { idx });
			} else
				query.appendOrderBy(
						new OrderBy(new ClassAttribute(EPMDocument.class, "thePersistInfo.createStamp"), true),
						new int[] { idx });
		} catch (Exception e) {
			e.printStackTrace();
		}
		return qr;

	}

	public QueryResult getWTDocEpmlink(Object obj) {
		QueryResult rt = null;
		try {
			if (obj instanceof WTDocument) {
				WTDocument doc = (WTDocument) obj;
				rt = PersistenceHelper.manager.navigate(doc, "wtDoc", WTDocEPMDocLink.class);
			} else {
				EPMDocument epm = (EPMDocument) obj;
				EPMDocumentMaster master = (EPMDocumentMaster) epm.getMaster();

				rt = PersistenceHelper.manager.navigate(master, "epmDoc", WTDocEPMDocLink.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return rt;
	}

	public EPMDocument getLastEPMDocument(EPMDocumentMaster master) throws Exception {

		EPMDocument epm = null;
		long longoid = CommonUtil.getOIDLongValue(master);
		Class class1 = EPMDocument.class;

		QuerySpec qs = new QuerySpec();
		int i = qs.appendClassList(class1, true);

		qs.appendWhere(new SearchCondition(class1, "iterationInfo.latest", SearchCondition.IS_TRUE, true),
				new int[] { i });

		qs.appendAnd();
		qs.appendWhere(new SearchCondition(class1, "checkoutInfo.state", "<>", "wrk"), new int[] { i });

		qs.appendAnd();
		qs.appendWhere(new SearchCondition(class1, "masterReference.key.id", SearchCondition.EQUAL, longoid),
				new int[] { i });

		qs.appendAnd();
		qs.appendWhere(VersionControlHelper.getSearchCondition(EPMDocument.class, true), new int[] { i });

		QueryResult qr = PersistenceHelper.manager.find(qs);
		while (qr.hasMoreElements()) {

			Object obj[] = (Object[]) qr.nextElement();
			epm = (EPMDocument) obj[0];

		}
		return epm;
	}

	public Vector getReferenceDependency(EPMDocument doc, String role) {
		Vector references = new Vector();
		try {
			QueryResult queryReferences = null;
			if (role.equals("referencedBy")) // 참조항목
				queryReferences = EPMStructureHelper.service.navigateReferencedBy((EPMDocumentMaster) doc.getMaster(),
						null, false);
			else // 참조
				queryReferences = EPMStructureHelper.service.navigateReferences(doc, null, false);

			EPMReferenceLink referenceLink = null;
			while (queryReferences.hasMoreElements()) {
				referenceLink = (EPMReferenceLink) queryReferences.nextElement();
				references.add(referenceLink);

			}
		} catch (Exception e) {
			System.out.println("Error getting the Instance Type");
			e.printStackTrace();
			return new Vector();
		}
		return references;
	}

	// 참조
	public Vector getRef(String oid) {
		EPMDocument epm = (EPMDocument) CommonUtil.getObject(oid);
		Vector vec = EpmSearchHelper.manager.getReferenceDependency(epm, "references");
		Vector vecRefby = new Vector();
		for (int i = 0; i < vec.size(); i++) {

			EPMReferenceLink link = (EPMReferenceLink) vec.get(i);
			vecRefby.add(link.getReferences());

		}

		return vecRefby;

	}

	// 참조 항목(EPMDocumentMaster)
	public Vector getRefBy(String oid) {
		EPMDocument epm = (EPMDocument) CommonUtil.getObject(oid);
		Vector vec = EpmSearchHelper.manager.getReferenceDependency(epm, "referencedBy");
		Vector vecRefby = new Vector();
		for (int i = 0; i < vec.size(); i++) {

			EPMReferenceLink link = (EPMReferenceLink) vec.get(i);
			vecRefby.add(link.getReferencedBy());

		}
		return vecRefby;
	}

	// 관련 문서 (EPMDocument)
	public Vector getWTDocumentLink(String oid) {
		EPMDocument epm = (EPMDocument) CommonUtil.getObject(oid);
		Vector vecDoc = new Vector();
		try {
			QueryResult linkQr = PersistenceHelper.manager.navigate((EPMDocumentMaster) epm.getMaster(), "wtDoc",
					WTDocEPMDocLink.class);
			while (linkQr.hasMoreElements()) {
				vecDoc.add(linkQr.nextElement());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vecDoc;
	}

	public EPMDocument getEPM2D(EPMDocument epm, EPMDocumentMaster master) {

		EPMDocument epm2D = null;

		try {
			QuerySpec qs = new QuerySpec();
			int idxA = qs.addClassList(EPMReferenceLink.class, false);
			int idxB = qs.addClassList(EPMDocument.class, true);

			// Join
			qs.appendWhere(new SearchCondition(EPMReferenceLink.class, "roleAObjectRef.key.id", EPMDocument.class,
					"thePersistInfo.theObjectIdentifier.id"), new int[] { idxA, idxB });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(EPMReferenceLink.class, "roleAObjectRef.key.id", SearchCondition.EQUAL,
					CommonUtil.getOIDLongValue(epm)), new int[] { idxA });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(EPMReferenceLink.class, "roleBObjectRef.key.id", SearchCondition.EQUAL,
					CommonUtil.getOIDLongValue(master)), new int[] { idxA });

			qs.appendAnd();
			qs.appendWhere(
					new SearchCondition(EPMReferenceLink.class, "referenceType", SearchCondition.EQUAL, "DRAWING"),
					new int[] { idxA }); // DRAWING

			// 최신 이터레이션
			qs.appendAnd();
			qs.appendWhere(VersionControlHelper.getSearchCondition(EPMDocument.class, true), new int[] { idxB });

			AttributeDefDefaultView aview = IBADefinitionHelper.service
					.getAttributeDefDefaultViewByPath("LatestVersionFlag");
			// 최신 버젼
			if (aview != null) {
				if (qs.getConditionCount() > 0)
					qs.appendAnd();

				int _idx = qs.appendClassList(wt.iba.value.StringValue.class, false);
				SearchCondition sc = new SearchCondition(wt.iba.value.StringValue.class, "theIBAHolderReference.key.id",
						EPMDocument.class, "thePersistInfo.theObjectIdentifier.id");
				qs.appendWhere(sc, new int[] { _idx, idxB });
				qs.appendAnd();
				sc = new SearchCondition(wt.iba.value.StringValue.class, "definitionReference.hierarchyID", "=",
						aview.getHierarchyID());
				qs.appendWhere(sc, new int[] { _idx });
				qs.appendAnd();
				sc = new SearchCondition(wt.iba.value.StringValue.class, "value", "=", "TRUE");
				qs.appendWhere(sc, new int[] { _idx });
			}

			// System.out.println(qs);
			QueryResult rt = PersistenceHelper.manager.find(qs);
			// System.out.println("size : " + rt.size());
			while (rt.hasMoreElements()) {

				Object[] oo = (Object[]) rt.nextElement();
				epm2D = (EPMDocument) oo[0];

//				System.out.println("EPMDocument 2d:" + epm2D.getNumber()+":" + epm2D.getVersionIdentifier().getSeries().getValue()+"."+epm2D.getIterationIdentifier().getSeries().getValue());

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return epm2D;
	}

	public EPMDocument getEPM2D(EPMDocumentMaster master) {

		EPMDocument epm2D = null;

		try {
			QuerySpec qs = new QuerySpec();
			int idxA = qs.addClassList(EPMReferenceLink.class, false);
			int idxB = qs.addClassList(EPMDocument.class, true);

			// Join
			qs.appendWhere(new SearchCondition(EPMReferenceLink.class, "roleAObjectRef.key.id", EPMDocument.class,
					"thePersistInfo.theObjectIdentifier.id"), new int[] { idxA, idxB });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(EPMReferenceLink.class, "roleBObjectRef.key.id", SearchCondition.EQUAL,
					CommonUtil.getOIDLongValue(master)), new int[] { idxA });
			qs.appendAnd();
			qs.appendWhere(
					new SearchCondition(EPMReferenceLink.class, "referenceType", SearchCondition.EQUAL, "DRAWING"),
					new int[] { idxA }); // DRAWING

			// 최신 이터레이션
			qs.appendAnd();
			qs.appendWhere(VersionControlHelper.getSearchCondition(EPMDocument.class, true), new int[] { idxB });

			AttributeDefDefaultView aview = IBADefinitionHelper.service
					.getAttributeDefDefaultViewByPath("LatestVersionFlag");
			// 최신 버젼
			if (aview != null) {
				if (qs.getConditionCount() > 0)
					qs.appendAnd();

				int _idx = qs.appendClassList(wt.iba.value.StringValue.class, false);
				SearchCondition sc = new SearchCondition(wt.iba.value.StringValue.class, "theIBAHolderReference.key.id",
						EPMDocument.class, "thePersistInfo.theObjectIdentifier.id");
				qs.appendWhere(sc, new int[] { _idx, idxB });
				qs.appendAnd();
				sc = new SearchCondition(wt.iba.value.StringValue.class, "definitionReference.hierarchyID", "=",
						aview.getHierarchyID());
				qs.appendWhere(sc, new int[] { _idx });
				qs.appendAnd();
				sc = new SearchCondition(wt.iba.value.StringValue.class, "value", "=", "TRUE");
				qs.appendWhere(sc, new int[] { _idx });
			}

			// System.out.println(qs);
			QueryResult rt = PersistenceHelper.manager.find(qs);
			// System.out.println("size : " + rt.size());
			while (rt.hasMoreElements()) {

				Object[] oo = (Object[]) rt.nextElement();
				epm2D = (EPMDocument) oo[0];

//				System.out.println("EPMDocument 2d:" + epm2D.getNumber()+":" + epm2D.getVersionIdentifier().getSeries().getValue()+"."+epm2D.getIterationIdentifier().getSeries().getValue());

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return epm2D;
	}

	public EPMDocumentMaster getEPM3D(EPMDocument epm2D) {

		EPMDocumentMaster epm3D = null;

		try {
			QuerySpec qs = new QuerySpec();
			int idxA = qs.addClassList(EPMReferenceLink.class, true);
			int idxB = qs.addClassList(EPMDocument.class, false);

			// Join
			qs.appendWhere(new SearchCondition(EPMReferenceLink.class, "roleAObjectRef.key.id", EPMDocument.class,
					"thePersistInfo.theObjectIdentifier.id"), new int[] { idxA, idxB });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(EPMReferenceLink.class, "roleAObjectRef.key.id", SearchCondition.EQUAL,
					CommonUtil.getOIDLongValue(epm2D)), new int[] { idxA });
			qs.appendAnd();
			qs.appendWhere(
					new SearchCondition(EPMReferenceLink.class, "referenceType", SearchCondition.EQUAL, "DRAWING"),
					new int[] { idxA }); // DRAWING

			// 최신 이터레이션
			qs.appendAnd();
			qs.appendWhere(VersionControlHelper.getSearchCondition(EPMDocument.class, true), new int[] { idxB });

			AttributeDefDefaultView aview = IBADefinitionHelper.service
					.getAttributeDefDefaultViewByPath("LatestVersionFlag");
			// 최신 버젼
			if (aview != null) {
				if (qs.getConditionCount() > 0)
					qs.appendAnd();

				int _idx = qs.appendClassList(wt.iba.value.StringValue.class, false);
				SearchCondition sc = new SearchCondition(wt.iba.value.StringValue.class, "theIBAHolderReference.key.id",
						EPMDocument.class, "thePersistInfo.theObjectIdentifier.id");
				qs.appendWhere(sc, new int[] { _idx, idxB });
				qs.appendAnd();
				sc = new SearchCondition(wt.iba.value.StringValue.class, "definitionReference.hierarchyID", "=",
						aview.getHierarchyID());
				qs.appendWhere(sc, new int[] { _idx });
				qs.appendAnd();
				sc = new SearchCondition(wt.iba.value.StringValue.class, "value", "=", "TRUE");
				qs.appendWhere(sc, new int[] { _idx });
			}

			// System.out.println(qs);
			QueryResult rt = PersistenceHelper.manager.find(qs);
			// System.out.println("rt.size :" +rt.size());
			while (rt.hasMoreElements()) {

				Object[] oo = (Object[]) rt.nextElement();
				EPMReferenceLink link = (EPMReferenceLink) oo[0];
				epm3D = (EPMDocumentMaster) link.getReferences();

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return epm3D;
	}

	public EPMDocumentMaster getEPMFrm(EPMDocument epm2D) {

		EPMDocumentMaster epmFrm = null;

		try {
			QuerySpec qs = new QuerySpec();
			int idxA = qs.addClassList(EPMReferenceLink.class, true);
			int idxB = qs.addClassList(EPMDocument.class, false);

			// Join
			qs.appendWhere(new SearchCondition(EPMReferenceLink.class, "roleAObjectRef.key.id", EPMDocument.class,
					"thePersistInfo.theObjectIdentifier.id"), new int[] { idxA, idxB });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(EPMReferenceLink.class, "roleAObjectRef.key.id", SearchCondition.EQUAL,
					CommonUtil.getOIDLongValue(epm2D)), new int[] { idxA });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(EPMReferenceLink.class, "referenceType", SearchCondition.EQUAL,
					"DRAWING_FORMAT"), new int[] { idxA }); // DRAWING

			// 최신 이터레이션
			qs.appendAnd();
			qs.appendWhere(VersionControlHelper.getSearchCondition(EPMDocument.class, true), new int[] { idxB });

			AttributeDefDefaultView aview = IBADefinitionHelper.service
					.getAttributeDefDefaultViewByPath("LatestVersionFlag");
			// 최신 버젼
			if (aview != null) {
				if (qs.getConditionCount() > 0)
					qs.appendAnd();

				int _idx = qs.appendClassList(wt.iba.value.StringValue.class, false);
				SearchCondition sc = new SearchCondition(wt.iba.value.StringValue.class, "theIBAHolderReference.key.id",
						EPMDocument.class, "thePersistInfo.theObjectIdentifier.id");
				qs.appendWhere(sc, new int[] { _idx, idxB });
				qs.appendAnd();
				sc = new SearchCondition(wt.iba.value.StringValue.class, "definitionReference.hierarchyID", "=",
						aview.getHierarchyID());
				qs.appendWhere(sc, new int[] { _idx });
				qs.appendAnd();
				sc = new SearchCondition(wt.iba.value.StringValue.class, "value", "=", "TRUE");
				qs.appendWhere(sc, new int[] { _idx });
			}

			// System.out.println(qs);
			QueryResult rt = PersistenceHelper.manager.find(qs);
			// System.out.println("rt.size :" +rt.size());
			while (rt.hasMoreElements()) {

				Object[] oo = (Object[]) rt.nextElement();
				EPMReferenceLink link = (EPMReferenceLink) oo[0];
				epmFrm = (EPMDocumentMaster) link.getReferences();

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return epmFrm;
	}

	public EPMDocument getREFDWG(EPMDocument epm) {
		EPMDocument returnEPM = null;
		try {
			QueryResult linkQr = PersistenceHelper.manager.navigate((EPMDocumentMaster) epm.getMaster(), "toEPM",
					REFDWGLink.class);
			if (linkQr.hasMoreElements()) {
				EPMDocumentMaster master = (EPMDocumentMaster) linkQr.nextElement();
				returnEPM = getLastEPMDocument(master);
			} else {
				linkQr = PersistenceHelper.manager.navigate((EPMDocumentMaster) epm.getMaster(), "fromEPM",
						REFDWGLink.class);
				if (linkQr.hasMoreElements()) {
					EPMDocumentMaster master = (EPMDocumentMaster) linkQr.nextElement();
					returnEPM = getLastEPMDocument(master);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnEPM;
	}

	/**
	 * Pro PDF URL Icon
	 * 
	 * @param part
	 * @return
	 */
	public String geEPMDocumentPDF(WTPart part) {

		String pdfFileUrl = "&nbsp;";
		/*
		 * try{ URLFactory urlFac = new URLFactory (); String iconStr = ""; String
		 * fileiconpath = "jsp/portal/images/icon/fileicon/"; EPMDocument epm =
		 * DrawingHelper.manager.getEPMDocument(part); String
		 * urlImg=urlFac.getBaseURL().getPath()+fileiconpath+"pdf.gif";
		 * //urlFac.getBaseURL ().getPath () + fileiconpath + "pdf.gif"; if(epm !=
		 * null){
		 * 
		 * EPMDocument epm2D =
		 * EpmSearchHelper.manager.getEPM2D((EPMDocumentMaster)epm.getMaster());
		 * if(epm2D != null){ QueryResult qr =
		 * ContentHelper.service.getContentsByRole(epm2D, ContentRoleType.SECONDARY);
		 * while(qr.hasMoreElements()) { ContentItem item =
		 * (ContentItem)qr.nextElement(); if(item instanceof ApplicationData) {
		 * ApplicationData ad = (ApplicationData)item;
		 * if(ad.getRole().toString().equals("SECONDARY") &&
		 * ad.getFileName().lastIndexOf("pdf")>0){ String
		 * nUrl="/plm/jsp/common/DownloadPDF.jsp?&appOid="+CommonUtil.getOIDString(ad)+
		 * "&appType=PDF"; pdfFileUrl = "<a href='"+nUrl+"'>&nbsp;"+urlImg+"</a>"; } } }
		 * }
		 * 
		 * }
		 * 
		 * }catch(Exception e){ e.printStackTrace(); }
		 */
		try {

			URLFactory urlFac = new URLFactory();
			String iconStr = "";
			String fileiconpath = "jsp/portal/images/icon/fileicon/";
			String urlImg = urlFac.getBaseURL().getPath() + fileiconpath + "pdf.gif";
			urlImg = "<img src=" + urlImg + " border=0>";
			EPMDocument epm = DrawingHelper.manager.getEPMDocument(part);

			if (epm == null) {
				return pdfFileUrl;
			}
			System.out.println("3D ========" + epm.getNumber());
			EPMDocument epmDRW = EpmSearchHelper.manager.getEPM2D((EPMDocumentMaster) epm.getMaster());

			if (epmDRW == null) {
				return pdfFileUrl;
			}
			System.out.println("2D ========" + epmDRW.getCADName());
			if ("PROE".equals(epmDRW.getAuthoringApplication().toString())
					&& "CADDRAWING".equals(epmDRW.getDocType().toString())) {
				Representation representation = PublishUtils.getRepresentation(epmDRW);
				System.out.println("representation  ======== " + representation);
				if (representation == null) {
					QueryResult qr = ContentHelper.service.getContentsByRole(epmDRW, ContentRoleType.SECONDARY);
					while (qr.hasMoreElements()) {
						ContentItem item = (ContentItem) qr.nextElement();
						if (item instanceof ApplicationData) {
							ApplicationData ad = (ApplicationData) item;
							if (ad.getRole().toString().equals("SECONDARY")
									&& ad.getFileName().lastIndexOf("pdf") > 0) {
								String nUrl = "/plm/jsp/common/DownloadPDF.jsp?&appOid=" + CommonUtil.getOIDString(ad)
										+ "&appType=PDF";
								pdfFileUrl = "<a href='" + nUrl + "'>&nbsp;" + urlImg + "</a>";
							}
						}
					}
				}
				if (representation != null) {
					representation = (Representation) ContentHelper.service.getContents(representation);
					Vector contentList = ContentHelper.getContentList(representation);
					for (int l = 0; l < contentList.size(); l++) {
						ContentItem contentitem = (ContentItem) contentList.elementAt(l);
						if (contentitem instanceof ApplicationData) {
							ApplicationData drawAppData = (ApplicationData) contentitem;

							// System.out.println("drawAppData.getRole().toString()" +
							// drawAppData.getRole().toString());
							// System.out.println("drawAppData.getFileName()" + drawAppData.getFileName());

							if (drawAppData.getRole().toString().equals("SECONDARY")
									&& drawAppData.getFileName().lastIndexOf("pdf") > 0) {
								String nUrl = "/plm/jsp/common/DownloadPDF.jsp?&appOid="
										+ CommonUtil.getOIDString(drawAppData) + "&appType=PDF";
								pdfFileUrl = "<a href='" + nUrl + "'>&nbsp;" + urlImg + "</a>";
							}
						}
					}
				}
			} else {
				System.out.println("PROE && !CADDRAWING" + epmDRW.getCADName());

				// QueryResult result = ContentHelper.service.getContentsByRole
				// ((ContentHolder)epmDRW ,ContentRoleType.SECONDARY );
				QueryResult result = ContentHelper.service.getContentsByRole((ContentHolder) epmDRW,
						ContentRoleType.PRIMARY);
				while (result.hasMoreElements()) {
					ContentItem tempitem = (ContentItem) result.nextElement();
					ApplicationData pAppData = (ApplicationData) tempitem;

					if (pAppData.getDescription() == null || pAppData.getDescription().equals("N"))
						continue;
					String nUrl = "/plm/servlet/DownloadGW?holderOid=" + CommonUtil.getOIDString(epmDRW) + "&appOid="
							+ CommonUtil.getOIDString(pAppData);
					pdfFileUrl = "<a href='" + nUrl + "'>&nbsp;" + urlImg + "</a>";
					return pdfFileUrl;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return pdfFileUrl;

	}

}
