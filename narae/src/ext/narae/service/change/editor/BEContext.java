package ext.narae.service.change.editor;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import ext.narae.service.change.EChangeOrder2;
import ext.narae.service.change.EOEul;
import ext.narae.service.change.EulPartLink;
import ext.narae.util.WCUtil;
import ext.narae.util.iba.IBAUtil;
import wt.clients.folder.FolderTaskLogic;
import wt.clients.iba.container.NewValueCreator;
import wt.clients.vc.CheckInOutTaskLogic;
import wt.fc.ObjectToObjectLink;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.iba.definition.StringDefinition;
import wt.iba.value.DefaultAttributeContainer;
import wt.iba.value.IBAHolder;
import wt.iba.value.StringValue;
import wt.iba.value.litevalue.AbstractValueView;
import wt.iba.value.litevalue.FloatValueDefaultView;
import wt.iba.value.litevalue.StringValueDefaultView;
import wt.iba.value.litevalue.UnitValueDefaultView;
import wt.iba.value.service.IBAValueHelper;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleTemplate;
import wt.org.WTUser;
import wt.part.Quantity;
import wt.part.QuantityUnit;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.RelationalExpression;
import wt.query.SQLFunction;
import wt.query.SearchCondition;
import wt.series.MultilevelSeries;
import wt.session.SessionHelper;
import wt.team.TeamHelper;
import wt.team.TeamTemplate;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;
import wt.vc.VersionControlHelper;
import wt.vc.VersionIdentifier;
import wt.vc.baseline.BaselineHelper;
import wt.vc.baseline.ManagedBaseline;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;
import wt.vc.wip.CheckoutLink;
import wt.vc.wip.WorkInProgressHelper;
import wt.xml.XMLLob;

public class BEContext {
	public static boolean isUseView = true; // true 일 경우는 View 사용

	public static JFrame top;
	public static String host;
	public static WTUser user;
	public static BEStateBar state;
	public static BOMManager manager;
	public static DataModel model;
	public static EditTreePanel editor;
	public static Progress progress;
	public static SupportPanel support;
	public static EditHandler handler;

	public static Stack undoStack;
	public static Stack redoStack;
	public static DataModel undoTemp;
	public static EChangeOrder2 eo;
	public static EOEul eulb;

	public static String version = "0.1"; // Program version
	public static boolean DEBUG = true; // Debug mode

	public static DecimalFormat quantityFormat = new DecimalFormat("0.###");

	public static DecimalFormat itemSeqFormat = new DecimalFormat("0000");

	public static StringDefinition itemSeqDefinition;

	static {
		try {
			URL url = WTProperties.getServerCodebase();
			host = url.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static JFrame getTop() {
		return top;
	}

	public static void clear() {
		try {
			user = (WTUser) SessionHelper.manager.getPrincipal();
			top = null;
			eo = null;
			eulb = null;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void setEChangeOrder(String oid) {
		try {
			ReferenceFactory rf = new ReferenceFactory();
			EChangeOrder2 e = (EChangeOrder2) rf.getReference(oid).getObject();
			eo = e;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static String getEONo() {
		if (eo == null)
			return "";
		return eo.getOrderNumber();
	}

	public static void println(Object debug) {
		if (DEBUG)
			System.out.println(debug);
	}

	public static void setTop(JFrame frame) {
		top = frame;

		top.setTitle("  ECO BOM Editor     접속자 : " + user.getFullName() + " [" + getEONo() + "]");
	}

	public static void setStateBar(BEStateBar bar) {
		state = bar;
	}

	public static void setPanel(BOMManager mm) {
		manager = mm;
		editor = mm.editPanel;
	}

	public static void setModel(DataModel m) {
		model = m;
	}

	public static void setProgress(Progress p) {
		progress = p;
	}

	public static void setSupport(SupportPanel s) {
		support = s;
	}

	public static void runProgress(String message) {
		progress.run(message);
	}

	public static void stopProgress() {
		progress.stop();
	}

	public static View getView() {
		try {
			if (editor == null)
				return ViewHelper.service.getView("Design");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return editor.getView();
	}

	public static String getDragMode() {
		Enumeration en = manager.group.getElements();
		while (en.hasMoreElements()) {
			JRadioButton box = (JRadioButton) en.nextElement();
			if (box.isSelected()) {
				return box.getText();
			}
		}
		return null;
	}

	public static String getPartViewURL(WTPart part) {
		String poid = getOIDString(part);
		return "jsp/part/ViewListPart.jsp?module=part&oid=" + poid;
		// return
		// "servlet/WindchillAuthGW/wt.enterprise.URLProcessor/URLTemplateAction?u8&oid="+poid+"&action=ObjProps";
	}

	public static String getEulBViewURL() {
		if (eulb == null)
			return null;
		String poid = getOIDString(eulb);
		return "jsp/change/EOEulB.jsp?oid=" + poid;
	}

	public static void action() {
		if (undoStack == null)
			undoStack = new Stack();
		if (redoStack == null)
			redoStack = new Stack();
		try {
			if (undoTemp != null)
				undoStack.push(undoTemp);
			undoTemp = copy(model);

			BEContext.println("action " + undoStack.size());
			if (!redoStack.empty())
				redoStack.removeAllElements();
			support.resetPreview();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void setUndoStart() {
		if (undoStack == null)
			undoStack = new Stack();
		else
			undoStack.removeAllElements();

		if (redoStack == null)
			redoStack = new Stack();
		else
			redoStack.removeAllElements();

		try {
			undoTemp = copy(model);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void undo() {
		try {
			if (undoStack == null || undoStack.empty())
				return;
			BEContext.println("undo " + undoStack.size());
			DataModel um = (DataModel) undoStack.pop();
			redoStack.push(model);
			setModel(um);
			undoTemp = copy(um);
			editor.setTree(um);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void redo() {
		try {
			if (redoStack == null || redoStack.empty())
				return;
			BEContext.println("redo " + redoStack.size());
			DataModel um = (DataModel) redoStack.pop();
			undoStack.push(model);
			setModel(um);
			undoTemp = copy(um);
			editor.setTree(um);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void rollback() {
		try {
			setModel(undoTemp);
			editor.setTree(undoTemp);
			undoTemp = copy(undoTemp);
			support.resetPreview();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static DataModel copy(DataModel old) throws Exception {
		Hashtable temp = new Hashtable();
		BETreeNode ptn = (BETreeNode) old.getRoot();
		BETreeNode newPtn = copy(ptn, temp);
		DataModel newModel = new DataModel();
		newModel.init(newPtn);
		return newModel;
	}

	public static BETreeNode copy(BETreeNode oldNode, Hashtable temp) {
		Object o = oldNode.getUserObject();
		if (!(o instanceof PartData)) {
			return new BETreeNode(new Boolean(true));
		}
		PartData old = (PartData) o;
		PartData pd = (PartData) temp.get(old);
		BETreeNode newNode = null;
		if (pd == null) {
			pd = new PartData(old.part, old.link, false);
			pd.basePart = old.basePart;
			pd.name = old.name;
			pd.version = old.version;
			pd.state = old.state;
			pd.type = old.type;
			pd.lastUpdated = old.lastUpdated;
			pd.lvl = old.lvl;
			pd.seq = old.seq;
			pd.variant = old.variant;
			pd.remarks = old.remarks;
			pd.quantity = old.quantity;
			pd.number = old.number;
			pd.icon = old.icon;
			pd.isRemove = old.isRemove;
			pd.color = old.color;
			pd.isEditable = old.isEditable;
			pd.unit = old.unit;
			pd.itemSeq = old.itemSeq;
			pd.orgItemSeq = old.orgItemSeq;
			pd.partNumber = old.partNumber;
			pd.baseQuantity = old.baseQuantity;
			pd.orgBaseQuantity = old.orgBaseQuantity;

			if (old.change != null) {
				PartData newChange = (PartData) temp.get(old.change);
				if (newChange == null) {
					try {
						BETreeNode nn = (BETreeNode) old.change.nodeList.get(0);
						BETreeNode xx = copy(nn, temp);
						pd.change = (PartData) xx.getUserObject();
						temp.put(old.change, newChange);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					pd.change = (PartData) newChange;
				}
			} else {
				pd.change = null;
			}
			temp.put(old, pd);
		}
		newNode = new BETreeNode(pd);
		Enumeration en = oldNode.children();
		while (en.hasMoreElements()) {
			BETreeNode pn = (BETreeNode) en.nextElement();
			BETreeNode newPn = copy(pn, temp);
			newNode.add(newPn);
		}
		return newNode;
	}

	public static String getOIDString(Persistable per) {
		try {
			ReferenceFactory rf = new ReferenceFactory();
			return rf.getQueryString(rf.getReference(per));
		} catch (Exception e) {
			return "";
		}
	}

	public static void saveAction(Document document) throws WTException, WTPropertyVetoException, RemoteException {
		saveAction(document, null);
	}

	/**
	 * 모부품과 자부품이 연결되어 있는지 체크한다.
	 * 
	 * @param parent
	 * @param child
	 * @return 연결되었을 경우 true
	 */
	public static boolean isRelationParentAndChild(WTPart parent, WTPart child) {
		try {
			QuerySpec query = new QuerySpec();
			query.addClassList(WTPartMaster.class, true);
			int idx1 = query.addClassList(WTPartUsageLink.class, true);

			query.appendWhere(new SearchCondition(WTPartUsageLink.class, "roleBObjectRef.key.id", "=",
					child.getMaster().getPersistInfo().getObjectIdentifier().getId()), new int[] { idx1 });

			QueryResult qr = PersistenceHelper.manager.navigate(parent, "uses", query);
			return qr.hasMoreElements();
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static String saveAction(Document document, EOEul eul)
			throws WTException, WTPropertyVetoException, RemoteException {
		if (eul == null)
			eul = eulb;

		Element root = (Element) document.getFirstChild();
		NodeList list = root.getElementsByTagName("EOStructure");

		String errorString = "";
		ArrayList checkoutList = new ArrayList();

		for (int i = 0; i < list.getLength(); i++) {
			Element node = (Element) list.item(i);
			NodeList newElement = node.getElementsByTagName("NEW");
			NodeList oldElement = node.getElementsByTagName("OLD");
			NodeList assyElement = node.getElementsByTagName("NextAssy");

			String newPart = null;
			String newVersion = null;
			String newQuantity = null;
			String basePart = null;
			String oldPart = null;
			String oldVersion = null;
			String oldQuantity = null;
			String assyPart = null;
			String basePartVersion = null;
			String nextAssyVersion = null;
			String newUnit = null;
			String oldUnit = null;
			String newItemSeq = null;
			String oldItemSeq = null;

			WTPart nPart = null;
			WTPart oPart = null;
			WTPart aPart = null;
			WTPart bPart = null;

			if (newElement != null && newElement.getLength() > 0) {
				Element ele = (Element) newElement.item(0);
				NodeList nodelist1 = ele.getChildNodes();
				newPart = nodelist1.item(0).getNodeValue().trim();
				newQuantity = ele.getAttribute("Quantity");
				newVersion = ele.getAttribute("Version");
				newUnit = ele.getAttribute("Unit");
				newItemSeq = ele.getAttribute("ItemSeq");
				nPart = EulPartHelper.manager.getPart(newPart, newVersion);
			}
			if (oldElement != null && oldElement.getLength() > 0) {
				Element ele = (Element) oldElement.item(0);
				NodeList nodelist2 = ele.getChildNodes();
				oldPart = nodelist2.item(0).getNodeValue().trim();
				oldQuantity = ele.getAttribute("Quantity");
				oldVersion = ele.getAttribute("Version");
				oldUnit = ele.getAttribute("Unit");
				oldItemSeq = ele.getAttribute("ItemSeq");
				oPart = EulPartHelper.manager.getPart(oldPart, oldVersion);
			}
			if (assyElement != null && assyElement.getLength() > 0) {
				Element ele = (Element) assyElement.item(0);
				NodeList nodelist4 = ele.getChildNodes();
				assyPart = nodelist4.item(0).getNodeValue().trim();
				basePart = ele.getAttribute("BasePart");
				basePartVersion = ele.getAttribute("BasePartVersion");
				nextAssyVersion = ele.getAttribute("NextAssyVersion");
				System.out.println("assyPart = " + assyPart + ", nextAssyVersion=" + nextAssyVersion);
				aPart = EulPartHelper.manager.getPart(assyPart, nextAssyVersion);
				bPart = EulPartHelper.manager.getPart(basePart, basePartVersion);
			}

			boolean errorflag = false;
			if (newPart != null && newPart.length() > 0 && nPart == null) {
				errorString += "Line " + (i + 1) + " New " + newPart + " 을 찾을 수 없습니다. 해당 라인을 무시 하였습니다.\n";
				errorflag = true;
			}
			if (oldPart != null && oldPart.length() > 0 && oPart == null) {
				errorString += "Line " + (i + 1) + " Old " + oldPart + " 을 찾을 수 없습니다. 해당 라인을 무시 하였습니다.\n";
				errorflag = true;
			}
			if (assyPart != null && assyPart.length() > 0 && aPart == null) {
				errorString += "Line " + (i + 1) + " NextAssy " + assyPart + " 을 찾을 수 없습니다. 해당 라인을 무시 하였습니다.\n";
				errorflag = true;
			}
			if (basePart != null && basePart.length() > 0 && bPart == null) {
				errorString += "Line " + (i + 1) + " BasePart " + basePart + " 을 찾을 수 없습니다.\n";
			}
			if (errorflag)
				continue;

			if (newPart == null) { // Remove link
				BEContext.println("Remove Link");
				// 연결이 되어있지 않으면 아무런 작업없이 skip
				if (!isRelationParentAndChild(aPart, oPart))
					continue;

				if (!WorkInProgressHelper.isCheckedOut(aPart)) {
					BEContext.println("checkout " + aPart);
					Folder folder = WorkInProgressHelper.service.getCheckoutFolder();
					CheckoutLink link = CheckInOutTaskLogic.checkOutObject(aPart, folder, "");
					aPart = (WTPart) link.getWorkingCopy();
				}
				checkoutList.add(assyPart + ";" + nextAssyVersion);
				WTPartUsageLink link = getLink(oPart, aPart, oldItemSeq);
				PersistenceHelper.manager.delete(link);
			} else if (oldPart == null) { // add link
				BEContext.println("Add Link");
				// 연결이 되어있으면 아무런 작업없이 skip
				if (isRelationParentAndChild(aPart, nPart))
					continue;

				if (!WorkInProgressHelper.isCheckedOut(aPart)) {
					BEContext.println("checkout " + aPart);
					Folder folder = WorkInProgressHelper.service.getCheckoutFolder();
					CheckoutLink link = CheckInOutTaskLogic.checkOutObject(aPart, folder, "");
					aPart = (WTPart) link.getWorkingCopy();
				}
				System.out.println("Add Link : assy Part = " + aPart);
				checkoutList.add(assyPart + ";" + nextAssyVersion);
				WTPartUsageLink link = WTPartUsageLink.newWTPartUsageLink(aPart, (WTPartMaster) nPart.getMaster());
				link.setQuantity(
						Quantity.newQuantity(Double.parseDouble(newQuantity), QuantityUnit.toQuantityUnit(newUnit)));
				link = (WTPartUsageLink) PersistenceHelper.manager.save(link);
				IBAUtil.changeIBAValue(link, "ItemSeq", newItemSeq);
			} else if ((newPart + ";" + newVersion).equals(oldPart + ";" + oldVersion)) { // change quantity
				BEContext.println("Change Quantity");
				if (!WorkInProgressHelper.isCheckedOut(aPart)) {
					BEContext.println("checkout " + aPart);
					Folder folder = WorkInProgressHelper.service.getCheckoutFolder();
					CheckoutLink link = CheckInOutTaskLogic.checkOutObject(aPart, folder, "");
					aPart = (WTPart) link.getWorkingCopy();
				}
				checkoutList.add(assyPart + ";" + nextAssyVersion);
				WTPartUsageLink link = getLink(oPart, aPart, oldItemSeq);
				if (link != null) {
					Quantity quan = link.getQuantity();
					link.setQuantity(
							Quantity.newQuantity(Integer.parseInt(newQuantity), QuantityUnit.toQuantityUnit(newUnit)));

					link = (WTPartUsageLink) PersistenceHelper.manager.modify(link);
					IBAUtil.changeIBAValue(link, "ItemSeq", newItemSeq);
				}
			} else { // change link
				BEContext.println("Change Link");
				if (!WorkInProgressHelper.isCheckedOut(aPart)) {
					BEContext.println("checkout " + aPart);
					Folder folder = WorkInProgressHelper.service.getCheckoutFolder();
					CheckoutLink link = CheckInOutTaskLogic.checkOutObject(aPart, folder, "");
					aPart = (WTPart) link.getWorkingCopy();
				}
				checkoutList.add(assyPart + ";" + nextAssyVersion);
				WTPartUsageLink link = getLink(oPart, aPart, oldItemSeq);

				if (link != null) {
					Quantity quan = link.getQuantity();
					PersistenceHelper.manager.delete(link);
				}
				link = WTPartUsageLink.newWTPartUsageLink(aPart, (WTPartMaster) nPart.getMaster());
				link.setQuantity(
						Quantity.newQuantity(Double.parseDouble(newQuantity), QuantityUnit.toQuantityUnit(newUnit)));
				link = (WTPartUsageLink) PersistenceHelper.manager.save(link);
				IBAUtil.changeIBAValue(link, "ItemSeq", newItemSeq);
			}
		}
		for (int i = 0; i < checkoutList.size(); i++) {
			String ps = (String) checkoutList.get(i);
			StringTokenizer tokens = new StringTokenizer(ps, ";");
			WTPart pp = EulPartHelper.manager.getPart(tokens.nextToken(), tokens.nextToken());
			if (WorkInProgressHelper.isCheckedOut(pp)) {
				pp = (WTPart) WorkInProgressHelper.service.checkin(pp, "EO Editor 어드민 툴에 의해 변경됨");
				if (eul != null) {
					EulPartLink link = EulPartLink.newEulPartLink(pp, eul);
					link.setDisabled(true);
					link.setLinkType(1);
					PersistenceHelper.manager.save(link);
				}
			}
		}
//        if (errorString.length() > 0)JOptionPane.showMessageDialog(getTop(), errorString);
		return errorString;
	}

	public static void saveAction() {
		try {
			Document document = support.preview.getChangeXml();
			Element root = (Element) document.getFirstChild();
			NodeList list = root.getElementsByTagName("EOStructure");
			if (list.getLength() > 0) {
				saveAction(document);
				BETreeNode rr = (BETreeNode) model.getRoot();
				PartData pd = rr.getData();
				editor.setTree(EulPartHelper.manager.getPart(pd.part.getNumber(), pd.version));
				JOptionPane.showMessageDialog(getTop(), "변경사항이 적용 되었습니다");
				support.resetPreview();
			} else {
				JOptionPane.showMessageDialog(getTop(), "적용시킬 변경 사항이 없습니다");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(getTop(), ex.getMessage());
		}
	}

	public static void bomViewCopy() {
		try {
			BETreeNode root = (BETreeNode) model.getRoot();
			PartData data = root.getData();
			View view = getView();
			if (view.getName().equals("Design")) {
				if (bomViewCopy(data.part))
					JOptionPane.showMessageDialog(getTop(), "생산 BOM 이 생성 되었습니다.");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(getTop(), ex.getMessage());
		}
	}

	public static void createBaseline() {
		try {
			BETreeNode root = (BETreeNode) model.getRoot();
			PartData data = root.getData();
			View view = getView();
			if (view.getName().equals("Design")) {
				if (createBaseline(data.part) != null)
					JOptionPane.showMessageDialog(getTop(), "Baseline 이 생성 되었습니다.");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(getTop(), ex.getMessage());
		}
	}

	public static void loadEul(EOEul neweulb) throws Exception {
		eulb = neweulb;
		String toid = neweulb.getTopAssyOid();
		ReferenceFactory rf = new ReferenceFactory();
		WTPart assy = (WTPart) rf.getReference(toid).getObject();

		assy = EulPartHelper.manager.getPart(assy.getNumber());

		editor.setTree(assy);

		XMLLob xml = eulb.getXml();
		if (xml == null) {
			return;
		}
		String content = xml.getContents();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		ByteArrayInputStream bis = new ByteArrayInputStream(content.getBytes());
		InputStream inStream = new BufferedInputStream(bis);
		factory.setValidating(false);
		DocumentBuilder documentbuilder = factory.newDocumentBuilder();
		Document document = documentbuilder.parse(inStream);
		inStream.close();

		Element root = (Element) document.getFirstChild();
		NodeList list = root.getElementsByTagName("EOStructure");

		String errorString = "";

		if (list.getLength() > 0) {
			BEContext.println("expand All");
			editor.expand(30);
		}

		for (int i = 0; i < list.getLength(); i++) {
			Element node = (Element) list.item(i);
			NodeList n1 = node.getElementsByTagName("NEW");
			NodeList n2 = node.getElementsByTagName("OLD");
			NodeList n4 = node.getElementsByTagName("NextAssy");

			String value1 = null;
			String quantity1 = null;
			String basePart = null;
			String value2 = null;
			String quantity2 = null;
			String value4 = null;
			String newVersion = null;
			String oldVersion = null;
			String nextAssyVersion = null;
			String basePartVersion = null;
			String newUnit = null;
			String newItemSeq = null;
			String oldUnit = null;
			String oldItemSeq = null;
			String stdQuantity = null;
			String orgStdQuantity = null;

			WTPart apart = null;
			WTPart bpart = null;
			WTPart cpart = null;
			WTPart dpart = null;

			if (n1 != null && n1.getLength() > 0) {
				Element ele = (Element) n1.item(0);
				NodeList nodelist1 = ele.getChildNodes();
				value1 = nodelist1.item(0).getNodeValue().trim();
				quantity1 = ele.getAttribute("Quantity");
				newVersion = ele.getAttribute("Version");
				newUnit = ele.getAttribute("Unit");
				newItemSeq = ele.getAttribute("ItemSeq");
				apart = EulPartHelper.manager.getPart(value1, newVersion);
			}
			if (n2 != null && n2.getLength() > 0) {
				Element ele = (Element) n2.item(0);
				NodeList nodelist2 = ele.getChildNodes();
				value2 = nodelist2.item(0).getNodeValue().trim();
				quantity2 = ele.getAttribute("Quantity");
				oldVersion = ele.getAttribute("Version");
				oldUnit = ele.getAttribute("Unit");
				oldItemSeq = ele.getAttribute("ItemSeq");
				bpart = EulPartHelper.manager.getPart(value2, oldVersion);
			}
			if (n4 != null && n4.getLength() > 0) {
				Element ele = (Element) n4.item(0);
				NodeList nodelist4 = ele.getChildNodes();
				value4 = nodelist4.item(0).getNodeValue().trim();
				basePart = ele.getAttribute("BasePart");
				nextAssyVersion = ele.getAttribute("NextAssyVersion");
				basePartVersion = ele.getAttribute("BasePartVersion");
				stdQuantity = ele.getAttribute("StdQuantity");
				orgStdQuantity = ele.getAttribute("OrgStdQuantity");

				cpart = EulPartHelper.manager.getPart(value4, nextAssyVersion);
				dpart = EulPartHelper.manager.getPart(basePart, basePartVersion);
			}

			boolean errorflag = false;
			if (value1 != null && value1.length() > 0 && apart == null) {
				errorString += "Line " + (i + 1) + " New " + value1 + " " + newVersion
						+ "을 찾을 수 없습니다. 해당 라인을 무시 하였습니다.\n";
				errorflag = true;
			}
			if (value2 != null && value2.length() > 0 && bpart == null) {
				errorString += "Line " + (i + 1) + " Old " + value2 + " " + oldVersion
						+ "을 찾을 수 없습니다. 해당 라인을 무시 하였습니다.\n";
				errorflag = true;
			}
			if (value4 != null && value4.length() > 0 && cpart == null) {
				errorString += "Line " + (i + 1) + " NextAssy " + value4 + " " + nextAssyVersion
						+ "을 찾을 수 없습니다. 해당 라인을 무시 하였습니다.\n";
				errorflag = true;
			}
			if (basePart != null && basePart.length() > 0 && dpart == null) {
				errorString += "Line " + (i + 1) + " BasePart " + basePart + " " + basePartVersion + "을 찾을 수 없습니다.\n";
			}
			if (errorflag)
				continue;

			if (value1 == null && value2 == null) {
				BEContext.println("baseQuantity edit");

				WTPart parentPart = cpart;

				Vector plist = model.getSamePartNode(parentPart);
				for (int j = 0; j < plist.size(); j++) {
					BETreeNode pppp1 = (BETreeNode) plist.get(j);
					if (basePart != null) {
						PartData ppd = (PartData) pppp1.getUserObject();
						ppd.baseQuantity = stdQuantity;
						ppd.cellSet();
					}
				}

			} else

			if (value1 == null) { // Remove link
				BEContext.println("Remove Link");

				WTPart removePart = bpart;
				WTPart parentPart = cpart;

				Vector plist = model.getSamePartNode(parentPart);
				for (int j = 0; j < plist.size(); j++) {
					BETreeNode pppp1 = (BETreeNode) plist.get(j);
					if (basePart != null) {
						PartData ppd = (PartData) pppp1.getUserObject();
						ppd.basePart = dpart;
					}
					Enumeration clist = pppp1.children();
					while (clist.hasMoreElements()) {
						BETreeNode cnode = (BETreeNode) clist.nextElement();
						PartData cpd = (PartData) cnode.getUserObject();
						if (cpd.getKey().equals(BEContext.getKey(removePart))) {
							cpd.isRemove = true;
							cpd.cellSet();
							break;
						}
					}
					BEContext.model.reload(pppp1);
				}
			} else if (value2 == null) { // add link
				BEContext.println("Add Link");
				WTPart addPart = apart;
				WTPart parentPart = cpart;
				Vector plist = model.getSamePartNode(parentPart);

				for (int j = 0; j < plist.size(); j++) {
					BETreeNode pppp1 = (BETreeNode) plist.get(j);
					PartData ppd = (PartData) pppp1.getUserObject();
					if (basePart != null) {
						ppd.basePart = dpart;
					}
					BETreeNode newNode = BEContext.model.getNewNode(addPart, ppd.part, null);
					pppp1.add(newNode);
					PartData cpd = newNode.getData();
					cpd.expand(newNode, getView());
					cpd.setQuantity(quantity1);
					cpd.setUnit(newUnit);
					cpd.itemSeq = newItemSeq;
					cpd.cellSet();
					BEContext.model.reload(pppp1);
				}
			} else if ((value1 + ";" + newVersion).equals(value2 + ";" + oldVersion)) { // change quantity prefix suffix
				BEContext.println("Change Quantity Or Prefix");
				WTPart addPart = apart;
				WTPart parentPart = cpart;
				Vector plist = model.getSamePartNode(parentPart);
				for (int j = 0; j < plist.size(); j++) {
					BETreeNode pppp1 = (BETreeNode) plist.get(j);

					PartData ppd = (PartData) pppp1.getUserObject();
					if (basePart != null) {
						ppd.basePart = dpart;
					}

					ppd.expand(pppp1, BEContext.getView());

					Enumeration clist = pppp1.children();
					while (clist.hasMoreElements()) {
						BETreeNode cnode = (BETreeNode) clist.nextElement();
						PartData cpd = (PartData) cnode.getUserObject();
						if (cpd.getKey().equals(BEContext.getKey(addPart))) {
							cpd.setQuantity(quantity1);
							cpd.setUnit(newUnit);
							cpd.itemSeq = newItemSeq;
							cpd.cellSet();
							break;
						}
					}
				}
			} else { // change link
				BEContext.println("Change Link");
				WTPart addPart = apart;
				WTPart removePart = bpart;
				WTPart parentPart = cpart;
				Vector plist = model.getSamePartNode(parentPart);
				for (int j = 0; j < plist.size(); j++) {
					BETreeNode pppp1 = (BETreeNode) plist.get(j);
					PartData ppd = (PartData) pppp1.getUserObject();
					if (basePart != null) {
						ppd.basePart = dpart;
					}
					PartData rdata = null;
					Enumeration clist = pppp1.children();
					while (clist.hasMoreElements()) {
						BETreeNode cnode = (BETreeNode) clist.nextElement();
						PartData cpd = (PartData) cnode.getUserObject();
						if (cpd.getKey().equals(BEContext.getKey(removePart))) {
							cpd.isRemove = true;
							cpd.cellSet();
							rdata = cpd;
							break;
						}
					}
					BETreeNode newNode = BEContext.model.getNewNode(addPart, ppd.part, null);
					pppp1.add(newNode);
					PartData cpd = newNode.getData();
					cpd.expand(newNode, getView());
					cpd.setQuantity(quantity1);
					cpd.setUnit(newUnit);
					cpd.itemSeq = newItemSeq;
					cpd.change = rdata;
					cpd.cellSet();

					BEContext.model.reload(pppp1);
				}
			}
		}

		if (errorString.length() > 0) {
			JOptionPane.showMessageDialog(getTop(), errorString);
		}
		if (list.getLength() > 0) {
			action();
			editor.setTree();
		}
	}

	public static void saveEul() throws Exception {
		eo = (EChangeOrder2) PersistenceHelper.manager.refresh(eo);
		PartData pd = model.getRoot().getData();
		String poid = pd.part.getPersistInfo().getObjectIdentifier().toString();

		XMLLob lob = support.preview.getXMLLob();

		if (eulb != null) {
			eulb = (EOEul) PersistenceHelper.manager.refresh(eulb);
		}

		Enumeration ep = support.preview.getEditParts();
		ArrayList editParts = new ArrayList();
		while (ep.hasMoreElements()) {
			editParts.add(ep.nextElement());
		}

		eulb = EditorServerHelper.manager.saveEul(eo, poid, lob, editParts, eulb);

	}

	public static boolean bomViewCopy(WTPart part) throws Exception {
		return bomViewCopy(part, null);
	}

	public static boolean bomViewCopy(WTPart part, EOEul eul) throws Exception {
		if (eul == null)
			eul = eulb;

		View view = getView();
		View aview[] = ViewHelper.service.getAllChildren(view);
		if (aview == null || aview.length == 0)
			return false;
		View view1 = aview[0];

		WTProperties wtproperties = WTProperties.getLocalProperties();
		String s = wtproperties.getProperty("part.location");
		String s2 = wtproperties.getProperty("part.lifecycle");

		Folder folder = FolderHelper.service.getFolder(part.getLocation());
		if (folder == null)
			folder = FolderHelper.service.getFolder(s);

		LifeCycleTemplate lifecycletemplate = null;
		if (s2 != null)
			lifecycletemplate = LifeCycleHelper.service.getLifeCycleTemplate(s2);
		else
			lifecycletemplate = (LifeCycleTemplate) part.getLifeCycleTemplate().getObject();

		Vector vector = new Vector();
		vector.add(part);
		getTotalParts(part, vector);
		boolean result = false;
		for (int i = 0; i < vector.size(); i++) {
			WTPart wtpart2 = (WTPart) vector.get(i);
			boolean b = review(wtpart2, view1, folder, lifecycletemplate, eul);
			if (result == false)
				result = true;
		}
		return result;
	}

	public static void getTotalParts(WTPart part, Vector vector) throws Exception {
		Vector vector1 = EulPartHelper.manager.descentLastPart(part, (View) part.getView().getObject());
		for (int i = 0; i < vector1.size(); i++) {
			Object[] o = (Object[]) vector1.get(i);
			WTPart wtpart1 = (WTPart) o[1];
			vector.add(wtpart1);
			getTotalParts(wtpart1, vector);
		}
	}

	public static boolean review(WTPart wtpart, View view, Folder folder, LifeCycleTemplate lifecycletemplate,
			EOEul eul) throws Exception {
		WTPart wtpart1 = EulPartHelper.manager.getPart(wtpart, view);
		if (wtpart1 == null) {
			WTPart wtpart2 = (WTPart) ViewHelper.service.newBranchForView(wtpart, view);
			// wtpart2.setAttributeContainer(settingContainer(wtpart));
			wtpart2 = (WTPart) LifeCycleHelper.setLifeCycle(wtpart2, lifecycletemplate);
			FolderHelper.assignLocation((FolderEntry) wtpart2, wtpart.getLocation());
			wtpart2 = (WTPart) TeamHelper.setTeamTemplate(wtpart2, (TeamTemplate) null);

			// EPart의 버전과 MPart의 버전을 동일하게 관리한다.
			String newBranchVersion = wtpart.getVersionIdentifier().getValue() + "."
					+ wtpart.getVersionIdentifier().getValue();
			MultilevelSeries mv = MultilevelSeries.newMultilevelSeries("wt.vc.VersionIdentifier", newBranchVersion);
			VersionIdentifier vi = VersionIdentifier.newVersionIdentifier(mv);
			VersionControlHelper.setVersionIdentifier(wtpart2, vi);

			WTPart wtpart3 = (WTPart) PersistenceHelper.manager.save(wtpart2);
			// LifeCycleHelper.service.setLifeCycleState(wtpart3,
			// State.toState("APPROVED"), false);
			if (eul != null) {
				EulPartLink link = EulPartLink.newEulPartLink(wtpart3, eul);
				link.setDisabled(true);
				link.setLinkType(1);
				PersistenceHelper.manager.save(link);
			}
			return true;
		}
		return false;
	}

	public static boolean review(WTPart part, View view) throws Exception {
		WTProperties wtproperties = WTProperties.getLocalProperties();
		String s = wtproperties.getProperty("part.location");
		String s2 = wtproperties.getProperty("part.lifecycle");
		Folder folder = null;
		LifeCycleTemplate lifecycletemplate = null;
		if (s != null)
			folder = FolderHelper.service.getFolder(s);
		else
			folder = FolderHelper.service.getFolder(part.getLocation());
		if (s2 != null)
			lifecycletemplate = LifeCycleHelper.service.getLifeCycleTemplate(s2);
		else
			lifecycletemplate = (LifeCycleTemplate) part.getLifeCycleTemplate().getObject();
		return review(part, view, folder, lifecycletemplate, null);
	}

	public static DefaultAttributeContainer settingContainer(IBAHolder ibaholder) throws Exception {
		ibaholder = IBAValueHelper.service.refreshAttributeContainer(ibaholder, null, null, null);
		DefaultAttributeContainer defaultattributecontainer = (DefaultAttributeContainer) ibaholder
				.getAttributeContainer();
		DefaultAttributeContainer defaultattributecontainer1 = new DefaultAttributeContainer();
		wt.iba.definition.litedefinition.AttributeDefDefaultView aattributedefdefaultview[] = defaultattributecontainer
				.getAttributeDefinitions();
		for (int i = 0; i < aattributedefdefaultview.length; i++) {
			AbstractValueView aabstractvalueview[] = defaultattributecontainer
					.getAttributeValues(aattributedefdefaultview[i]);
			AbstractValueView abstractvalueview = (AbstractValueView) NewValueCreator
					.createNewValueObject(aattributedefdefaultview[i]);
			if (aabstractvalueview[0] instanceof StringValueDefaultView) {
				StringValueDefaultView stringvaluedefaultview = (StringValueDefaultView) aabstractvalueview[0];
				((StringValueDefaultView) abstractvalueview).setValue(stringvaluedefaultview.getValue());
			} else if (aabstractvalueview[0] instanceof FloatValueDefaultView) {
				FloatValueDefaultView floatvaluedefaultview = (FloatValueDefaultView) aabstractvalueview[0];
				((FloatValueDefaultView) abstractvalueview).setValue(floatvaluedefaultview.getValue());
			} else if (aabstractvalueview[0] instanceof UnitValueDefaultView) {
				UnitValueDefaultView unitvaluedefaultview = (UnitValueDefaultView) aabstractvalueview[0];
				((UnitValueDefaultView) abstractvalueview).setValue(unitvaluedefaultview.getValue());
			}
			defaultattributecontainer1.addAttributeValue(abstractvalueview);
		}

		return defaultattributecontainer1;
	}

	public static ManagedBaseline createBaseline(WTPart wtpart) throws Exception {
		Date date = new Date();
		String baselineName = "EO Baseline : " + wtpart.getNumber() + " : " + date;

		WTProperties wtproperties = WTProperties.getLocalProperties();
		String s = "/Default/Change";// wtproperties.getProperty("baseline.location");
		String s2 = wtproperties.getProperty("baseline.lifecycle");

		Folder folder = null;
		LifeCycleTemplate lifecycletemplate = null;

		if (s != null)
			folder = FolderHelper.service.getFolder(s, WCUtil.getWTContainerRef());
		// else folder = FolderHelper.service.getFolder(wtpart.getLocation());
		else
			folder = FolderTaskLogic.getFolder(wtpart.getLocation(), WCUtil.getWTContainerRef());
		// if (s2 != null) lifecycletemplate =
		// LifeCycleHelper.service.getLifeCycleTemplate(s2);

		if (s2 != null)
			lifecycletemplate = LifeCycleHelper.service.getLifeCycleTemplate(s2, WCUtil.getWTContainerRef());
		else
			lifecycletemplate = (LifeCycleTemplate) wtpart.getLifeCycleTemplate().getObject();

		ManagedBaseline mb = createBaseline(wtpart, baselineName, folder, lifecycletemplate);

		if (eulb != null) {
			eulb = (EOEul) PersistenceHelper.manager.refresh(eulb);
			eulb.setBaseline(mb);
			eulb = (EOEul) PersistenceHelper.manager.modify(eulb);
		}
		return mb;
	}

	public static ManagedBaseline createBaseline(WTPart wtpart, String name, Folder folder,
			LifeCycleTemplate lifecycletemplate) throws Exception {
		System.out.println("1 " + wtpart);
		Vector vector = new Vector();
		wtpart = EulPartHelper.manager.getPart(wtpart, (View) wtpart.getView().getObject());
		System.out.println("2 " + wtpart);
		vector.add(wtpart);
		getTotalParts(wtpart, vector);
		ManagedBaseline managedbaseline = ManagedBaseline.newManagedBaseline();
		managedbaseline.setName(name);
		managedbaseline.setDescription("");
		managedbaseline = (ManagedBaseline) LifeCycleHelper.setLifeCycle(managedbaseline, lifecycletemplate);
		FolderHelper.assignLocation((FolderEntry) managedbaseline, folder);
		managedbaseline = (ManagedBaseline) PersistenceHelper.manager.save(managedbaseline);
		System.out.println("> BOM Part List ");
		for (int i = 0; i < vector.size(); i++) {
			System.out.println("> " + i + " : " + vector.get(i));
		}
		managedbaseline = (ManagedBaseline) BaselineHelper.service.addToBaseline(vector, managedbaseline);
		return managedbaseline;
	}

	public static WTPartUsageLink getLink(WTPart child, WTPart parent, String itemSeq) throws WTException {
		return (WTPartUsageLink) getLinkObject((WTPartMaster) child.getMaster(), parent, WTPartUsageLink.class,
				itemSeq);
	}

	public static ObjectToObjectLink getLinkObject(WTObject roleA, WTObject roleB, Class linkClz, String itemSeq)
			throws WTException {
		QuerySpec query = new QuerySpec();
		int linkIndex = query.appendClassList(linkClz, true);
		SearchCondition cond1 = new SearchCondition(linkClz, WTAttributeNameIfc.ROLEB_OBJECT_ID, "=",
				new Long(PersistenceHelper.getObjectIdentifier(roleA).getId()));
		SearchCondition cond2 = new SearchCondition(linkClz, WTAttributeNameIfc.ROLEA_OBJECT_ID, "=",
				new Long(PersistenceHelper.getObjectIdentifier(roleB).getId()));
		query.appendWhere(cond1, new int[] { linkIndex });
		query.appendAnd();
		query.appendWhere(cond2, new int[] { linkIndex });

		if (itemSeq != null && itemSeq.trim().length() > 0) {
			query.appendAnd();
			int vindex = query.addClassList(StringValue.class, true);
			query.appendWhere(new SearchCondition(StringValue.class, StringValue.VALUE, "=", itemSeq),
					new int[] { vindex });
			query.appendAnd();
			query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", linkClz,
					"thePersistInfo.theObjectIdentifier.id"), new int[] { vindex, linkIndex });
			query.appendAnd();
			long did = getItemSeqDefinitionId();
			SearchCondition sc = new SearchCondition(StringValue.class, "definitionReference.key.id", "=", did);
			query.appendWhere(sc, new int[] { vindex });
		}

		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.size() == 0)
			return null;
		Object[] obj = (Object[]) result.nextElement();
		return (ObjectToObjectLink) obj[0];
	}

	public static long getItemSeqDefinitionId() {

		if (itemSeqDefinition == null) {
			try {
				QuerySpec select = new QuerySpec(StringDefinition.class);
				select.appendWhere(new SearchCondition(StringDefinition.class, "name", "=", "ItemSeq"),
						new int[] { 0 });
				QueryResult re = PersistenceHelper.manager.find(select);
				while (re.hasMoreElements()) {
					itemSeqDefinition = (StringDefinition) re.nextElement();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		if (itemSeqDefinition != null) {
			return itemSeqDefinition.getPersistInfo().getObjectIdentifier().getId();
		}
		return 0L;
	}

	public static boolean isAdmin() {
		try {
			Enumeration en = user.parentGroupNames();
			while (en.hasMoreElements()) {
				String st = (String) en.nextElement();
				if (st.equals("Administrators"))
					return true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	public static boolean isEditable(WTPart part) {
		try {
			QuerySpec qs = new QuerySpec(EulPartLink.class);
			qs.appendWhere(new SearchCondition(EulPartLink.class, "roleAObjectRef.key.id", "=",
					part.getPersistInfo().getObjectIdentifier().getId()), new int[] { 0 });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(EulPartLink.class, "disabled", SearchCondition.IS_FALSE),
					new int[] { 0 });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(EulPartLink.class, "linkType", "=", 0), new int[] { 0 });
			if (eulb != null) {
				qs.appendAnd();
				qs.appendWhere(new SearchCondition(EulPartLink.class, "roleBObjectRef.key.id", "<>",
						eulb.getPersistInfo().getObjectIdentifier().getId()), new int[] { 0 });
			}
			QueryResult qr = PersistenceHelper.manager.find(qs);
			if (qr.size() > 0)
				return false;
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public static String getKey(WTPart part) {
		String number = part.getNumber();
		try {
//            String version = VersionControlHelper.getQualifiedIdentifier(part).getSeries().getValue();
			return number + ":" + version;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return number;
	}

	public static void main(String[] args) throws Exception {
		wt.method.RemoteMethodServer methodServer = wt.method.RemoteMethodServer.getDefault();
		if (methodServer.getUserName() == null) {
			methodServer.setUserName("wcadmin");
			methodServer.setPassword("wcadmin1");
		}

		Class class1 = WTPart.class;

		QuerySpec sp = new QuerySpec();
		int j = sp.addClassList(class1, true);
		SearchCondition sc = VersionControlHelper.getSearchCondition(class1, true);
		sp.appendWhere(sc, new int[] { j });
		String number = "";
		String name = "";

		String views = "Manufacturing";
		if (views != null && views.length() > 0) {
			if (sp.getConditionCount() > 0)
				sp.appendAnd();
			View view = ViewHelper.service.getView(views);
			sp.appendWhere(new SearchCondition(class1, "view.key.id", "=",
					view.getPersistInfo().getObjectIdentifier().getId()), new int[] { j });
		}

		if (number.length() > 0) {
			if (sp.getConditionCount() > 0)
				sp.appendAnd();
			number = number.toLowerCase() + "%";
			ClassAttribute ca = new ClassAttribute(class1, WTPart.NUMBER);
			SQLFunction sf = SQLFunction.newSQLFunction("LOWER");
			sf.setArgumentAt((RelationalExpression) ca, 0);
			RelationalExpression expression = new wt.query.ConstantExpression(number);
			SearchCondition sc3 = new SearchCondition((RelationalExpression) sf, SearchCondition.LIKE, expression);
			sp.appendSearchCondition(sc3);
		}
		if (name.length() > 0) {
			if (sp.getConditionCount() > 0)
				sp.appendAnd();
			name = name.toLowerCase() + "%";
			ClassAttribute ca = new ClassAttribute(class1, WTPart.NAME);
			SQLFunction sf = SQLFunction.newSQLFunction("LOWER");
			sf.setArgumentAt((RelationalExpression) ca, 0);
			RelationalExpression expression = new wt.query.ConstantExpression(name);
			SearchCondition sc3 = new SearchCondition((RelationalExpression) sf, SearchCondition.LIKE, expression);
			sp.appendSearchCondition(sc3);
		}
		sp.appendOrderBy(new OrderBy(new ClassAttribute(class1, "master>number"), false));

		QueryResult qr = PersistenceHelper.manager.find(sp);
		while (qr.hasMoreElements()) {
			Object[] o = (Object[]) qr.nextElement();
			WTPart part = (WTPart) o[0];
			System.out.println(part.getNumber() + " - " + part.getVersionIdentifier().getValue());
		}
	}

	public static String getItemSeq(WTPartUsageLink link) {
		try {
			return IBAUtil.getAttrValue(link, "ItemSeq");
		} catch (Exception ex) {
			return "";
		}
	}

	public static String getBaseQuantity(WTPart part) {
		try {
			String sq = IBAUtil.getAttrValue(part, "STD_QUANTITY");
			if (sq == null || sq.length() == 0)
				return "1";
			return sq;
		} catch (Exception ex) {
			return "1";
		}
	}
}
