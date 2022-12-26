package ext.narae.service.change.editor;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import ext.narae.service.change.EChangeOrder2;
import ext.narae.service.change.EOEul;
import ext.narae.service.change.EulPartLink;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.pom.Transaction;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.xml.XMLLob;

public class EditorServerHelper implements wt.method.RemoteAccess, java.io.Serializable {

	static final boolean SERVER = wt.method.RemoteMethodServer.ServerFlag;
	public static EditorServerHelper manager = new EditorServerHelper();

	public EOEul saveEul(EChangeOrder2 eo, String poid, XMLLob lob, ArrayList editParts, EOEul eul) throws Exception {
		if (!SERVER) {
			Class argTypes[] = new Class[] { EChangeOrder2.class, String.class, XMLLob.class, ArrayList.class,
					EOEul.class };
			Object args[] = new Object[] { eo, poid, lob, editParts, eul };
			try {
				return (EOEul) wt.method.RemoteMethodServer.getDefault().invoke("saveEul", null, this, argTypes, args);
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

			if (eul != null && PersistenceHelper.isPersistent(eul)) {
				eul = (EOEul) PersistenceHelper.manager.refresh(eul);
				eul.setTopAssyOid(poid);
				eul.setXml(lob);
				System.out.println("eul save");
				eul = (EOEul) PersistenceHelper.manager.save(eul);
				removeAllEditPartsLink(eul);
			} else {
				eul = EOEul.newEOEul();
				eul.setTopAssyOid(poid);
				eul.setXml(lob);
				eul.setEco(eo);
				eul.setOwner(SessionHelper.manager.getPrincipalReference());
				eul = (EOEul) wt.fc.PersistenceHelper.manager.save(eul);
			}

			addEditPartsLink(eul, editParts);

			trx.commit();
			trx = null;

			return eul;

		} catch (Exception e) {
			throw e;
		} finally {
			if (trx != null) {
				trx.rollback();
			}
		}

	}

	public static void removeAllEditPartsLink(EOEul eul) throws Exception {
		QueryResult qr = PersistenceHelper.manager.navigate(eul, "part", EulPartLink.class, false);
		while (qr.hasMoreElements()) {
			EulPartLink link = (EulPartLink) qr.nextElement();
			link.setDisabled(true);
			PersistenceHelper.manager.modify(link);
			// PersistenceHelper.manager.delete(link);
		}
	}

	public static void addEditPartsLink(EOEul eul, ArrayList co) throws Exception {
		for (int i = 0; i < co.size(); i++) {
			WTPart part = (WTPart) co.get(i);
			EulPartLink link = EulPartLink.newEulPartLink(part, eul);
			PersistenceHelper.manager.save(link);
		}
	}

	public String getSapString(Document document) throws Exception {
		Hashtable hash = getSapData(document);

		Enumeration en = hash.keys();

		StringBuffer result = new StringBuffer();

		while (en.hasMoreElements()) {
			String key = (String) en.nextElement();

			EOActionTempAssyData eta = (EOActionTempAssyData) hash.get(key);

			result.append(eta);
			result.append("--------------------------------------------------------------------------------------\n");
		}

		return result.toString();
	}

	public Hashtable getSapData(Document document) throws Exception {

		Element root = (Element) document.getFirstChild();
		NodeList list = root.getElementsByTagName("EOStructure");

		ArrayList assyList = new ArrayList();
		Hashtable hash = new Hashtable();

		for (int i = 0; i < list.getLength(); i++) {
			Element node = (Element) list.item(i);
			NodeList newElement = node.getElementsByTagName("NEW");
			NodeList oldElement = node.getElementsByTagName("OLD");
			NodeList assyElement = node.getElementsByTagName("NextAssy");

			EOActionTempAssyData adata = null;

			if (assyElement != null && assyElement.getLength() > 0) {
				Element ele = (Element) assyElement.item(0);
				NodeList nodelist4 = ele.getChildNodes();

				String assyPart = nodelist4.item(0).getNodeValue().trim();
				String nextAssyVersion = ele.getAttribute("NextAssyVersion");

				String stdQuantity = ele.getAttribute("StdQuantity");
				String orgStdQuantity = ele.getAttribute("OrgStdQuantity");

				String key = assyPart + ":" + nextAssyVersion;

				if (assyList.contains(key)) {
					adata = (EOActionTempAssyData) hash.get(key);
				} else {
					adata = new EOActionTempAssyData(assyPart, nextAssyVersion, orgStdQuantity, stdQuantity);
					assyList.add(key);
				}

				hash.put(key, adata);
			}

			EOActionTempItemData data = null;

			if (newElement != null && newElement.getLength() > 0) {
				Element ele = (Element) newElement.item(0);
				NodeList nodelist1 = ele.getChildNodes();

				data = new EOActionTempItemData();
				data.newPart = nodelist1.item(0).getNodeValue().trim();
				data.newQuantity = ele.getAttribute("Quantity");
				data.newVersion = ele.getAttribute("Version");
				data.newUnit = ele.getAttribute("Unit");
				data.newItemSeq = ele.getAttribute("ItemSeq");

				adata.itemList.add(data);

			}
			if (oldElement != null && oldElement.getLength() > 0) {
				Element ele = (Element) oldElement.item(0);
				NodeList nodelist2 = ele.getChildNodes();
				String oldPart = nodelist2.item(0).getNodeValue().trim();
				String oldVersion = ele.getAttribute("Version");
				String oldItemSeq = ele.getAttribute("ItemSeq");
				if (data == null) {
					data = new EOActionTempItemData();
					data.editType = "D";
					adata.itemList.add(data);
				} else {
					data.editType = "C";
				}
				data.oldPart = oldPart;
				data.oldVersion = oldVersion;
				data.oldQuantity = ele.getAttribute("Quantity");
				data.oldUnit = ele.getAttribute("Unit");
				data.oldItemSeq = oldItemSeq;
			}
		}

		return hash;
	}

	public EChangeOrder2 saveChangeData(EChangeOrder2 eco, Hashtable hash) throws Exception {
		Enumeration en = hash.keys();

		while (en.hasMoreElements()) {
			String key = (String) en.nextElement();
			EOActionTempAssyData adata = (EOActionTempAssyData) hash.get(key);
			eco = saveChangeData(eco, adata);
		}
		return eco;
	}

	public EChangeOrder2 saveChangeData(EChangeOrder2 eco, EOActionTempAssyData adata) throws Exception {
		if (!SERVER) {
			Class argTypes[] = new Class[] { EChangeOrder2.class, EOActionTempAssyData.class };
			Object args[] = new Object[] { eco, adata };
			try {
				return (EChangeOrder2) wt.method.RemoteMethodServer.getDefault().invoke("saveChangeData", null, this,
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

			WTPart assy = EulPartHelper.manager.getPart(adata.assyPart);

			for (int i = 0; i < adata.itemList.size(); i++) {
				EOActionTempItemData data = (EOActionTempItemData) adata.itemList.get(i);

				if ("I".equals(data.editType)) {
					WTPart item = EulPartHelper.manager.getPart(data.newPart);

				} else if ("D".equals(data.editType)) {
					WTPart item = EulPartHelper.manager.getPart(data.oldPart);

				} else if ("C".equals(data.editType)) {
					WTPart newItem = EulPartHelper.manager.getPart(data.newPart);
					WTPart oldItem = EulPartHelper.manager.getPart(data.oldPart);

				}
			}

			trx.commit();
			trx = null;

			return eco;

		} catch (Exception e) {
			throw e;
		} finally {
			if (trx != null) {
				trx.rollback();
			}
		}
	}

}
