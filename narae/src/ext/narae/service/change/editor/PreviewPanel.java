package ext.narae.service.change.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.util.EncodingConverter;
import wt.xml.XMLLob;

public class PreviewPanel extends JPanel {

	public static int CREATE = 0;
	public static int ADD = 1;
	public static int REMOVE = 2;
	public static int CHANGE = 3;
	public static int QUANTITY = 4;
	public static int UNIT = 5;
	public static int BASE_QUANTITY = 6;

	Progress progress;
	boolean command = false;
	Vector temp = new Vector();
	XMLTempModel model;

	JTextArea textArea = new JTextArea();
	JTextArea xmlArea = new JTextArea();
	JTextArea errorArea = new JTextArea();
	JTextArea sapArea = new JTextArea();

	Document docs;
	Element root;

	String error = "";
	Vector errorVector = new Vector();

	JScrollPane scroll2 = null;
	JScrollPane scroll3 = null;
	JScrollPane errorScroll = null;
	EncodingConverter converter = new EncodingConverter();
	DocumentBuilder documentbuilder = null;
	Hashtable editParts = new Hashtable();

	private static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

	public PreviewPanel() {

		setLayout(new BorderLayout());
		progress = BEContext.progress;

		JPanel main = new JPanel();
		JPanel xmlPanel = new JPanel();
		main.setLayout(new BorderLayout());
		xmlPanel.setLayout(new BorderLayout());
		textArea.setBackground(Color.white);
		main.add(textArea);
		xmlPanel.add(xmlArea);

		JPanel sapPanel = new JPanel();
		sapPanel.setLayout(new BorderLayout());
		sapArea.setBackground(Color.white);
		sapPanel.add(sapArea);

		JScrollPane scroll = new JScrollPane();
		scroll.getViewport().add(main);
		errorScroll = new JScrollPane();
		errorScroll.getViewport().add(errorArea);
		errorArea.setDisabledTextColor(Color.red);

		JSplitPane m_pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scroll, errorScroll);
		m_pane.setDividerSize(2);
		m_pane.setDividerLocation(350);

		scroll2 = new JScrollPane();
		scroll2.getViewport().add(xmlPanel);

		scroll3 = new JScrollPane();
		scroll3.getViewport().add(sapPanel);

		JTabbedPane tabPane = new JTabbedPane();
		tabPane.add("Simple View", scroll);
		tabPane.add("XML View", scroll2);
//		tabPane.add("SAP View",scroll3);

		tabPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		add(BorderLayout.CENTER, tabPane);

		textArea.setEditable(false);
		xmlArea.setEditable(false);
		sapArea.setEditable(false);
		errorArea.setEditable(false);

		try {
			documentbuilder = factory.newDocumentBuilder();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public Document getChangeXml() {
		scanChangeData();
		return docs;
	}

	public String getChagneXmlString() throws Exception {
		scanChangeData();
		return xmlArea.getText();
	}

	public Enumeration getEditParts() {
		return editParts.elements();
	}

	public XMLLob getXMLLob() throws Exception {
		XMLLob lob = XMLLob.newXMLLob();
		lob.setContents(getChagneXmlString());
		return lob;
	}

	public void scanChangeData() {

		try {
			progress.run("Scan...");
			command = true;
			docs = documentbuilder.newDocument();
			root = docs.createElement("EULB");
			docs.appendChild(root);
			model = new XMLTempModel();

			textArea.setText("");
			sapArea.setText("");
			xmlArea.setText("");
			errorArea.setText("");

			Element eonoElement = docs.createElement("EONO");
			eonoElement.appendChild(docs.createTextNode(converter.encode(BEContext.getEONo())));
			root.appendChild(eonoElement);
			error = "";
			BETreeNode root = BEContext.model.getRoot();
			if (root == null)
				return;
			Enumeration clist = root.children();
			scan(clist);
			model.setting();
			print();

			sapArea.setText(EditorServerHelper.manager.getSapString(docs));

		} catch (Exception eo) {
			eo.printStackTrace();
			JOptionPane.showMessageDialog(BEContext.top, ((Throwable) eo).getLocalizedMessage());
		} finally {
			progress.stop();
			command = false;
		}

	}

	public void print() {
		editParts.clear();
		try {
			for (int i = 0; i < model.size(); i++) {
				XMLTempData data = model.get(i);
				append(data.simple);
				root.appendChild(data.xml);
				editParts.put(data.parent.getNumber(), data.parent);
			}
			printNode(root, "  ");
			if (error.length() > 0) {
				errorArea.append("--------------- warning -------------\n" + error);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void scan(Enumeration list) {

		Enumeration children = null;

		while (list.hasMoreElements()) {
			BETreeNode pnode = (BETreeNode) list.nextElement();
			scan(pnode);
			children = pnode.children();
			if (children != null)
				scan(children);
		}
	}

	public void scan(BETreeNode pnode) {
		PartData pd = pnode.getData();
		if (pd == null)
			return;

		String orgQuantity = "1";
		if (pd.link != null)
			orgQuantity = BEContext.quantityFormat.format(pd.link.getQuantity().getAmount());

		String orgUnit = "";
		if (pd.link != null)
			orgUnit = pd.link.getQuantity().getUnit().toString();

		BETreeNode parent = (BETreeNode) pnode.getParent();
		PartData pp = parent.getData();

		String key = pp.getKey() + ";" + pd.getKey();

		if (pd.isRemove) {

			key += "-" + pd.orgItemSeq;

			if (model.contains(REMOVE, key))
				return;
			if (model.contains(CHANGE, key))
				return;
			String ss = "품목삭제 : " + pp.number + " - " + pd.number;
			XMLTempData xdata = new XMLTempData(REMOVE);
			xdata.key = key;
			xdata.xml = getRemoveXml(pp, pd);
			xdata.simple = ss;
			xdata.parent = pp.part;
			model.add(xdata);
		} else if (pd.change != null) {

			key += "-" + pd.orgItemSeq;

			if (model.contains(CHANGE, key))
				return;

			PartData cdd = pd.change;
			String ss = "품목교체 : " + pp.number + " - " + pd.change.number + " - " + pd.number;
			XMLTempData xdata = new XMLTempData(CHANGE);
			xdata.key = key;
			xdata.rKey = pp.getKey() + ";" + pd.change.getKey() + "-" + pd.change.orgItemSeq;
			xdata.xml = getChangeXml(pp, pd);
			xdata.simple = ss;
			xdata.parent = pp.part;
			model.add(xdata);
		} else if (pd.link == null) {

			key += "-" + pd.itemSeq;

			BEContext.println("신규연결");
			if (model.contains(ADD, key))
				return;
			String ss = "신규연결 : " + pp.number + " - " + pd.number;
			XMLTempData xdata = new XMLTempData(ADD);
			xdata.key = key;
			xdata.xml = getAddXml(pp, pd);
			xdata.simple = ss;
			xdata.parent = pp.part;
			model.add(xdata);
		} else if (pd.quantity != null) {
			key += "-" + pd.orgItemSeq;

			if ((!orgQuantity.equals(pd.quantity)) || (!orgUnit.equals(pd.unit))
					|| (!pd.orgItemSeq.equals(pd.itemSeq))) {

				if (model.contains(QUANTITY, key))
					return;

				String ss = "속성변경 : " + pp.number + " - " + pd.number;
				XMLTempData xdata = new XMLTempData(QUANTITY);
				xdata.key = key;
				xdata.xml = getQuantityXml(pp, pd);
				xdata.simple = ss;
				xdata.parent = pp.part;
				model.add(xdata);
			}
		}

		if (pp.baseQuantity != null && !(pp.baseQuantity.equals(pp.orgBaseQuantity))) {
			if (model.contains(BASE_QUANTITY, pp.getKey()))
				return;

			String ss = "기준수량 변경 : " + pp.number;
			XMLTempData xdata = new XMLTempData(BASE_QUANTITY);
			xdata.key = pp.getKey();
			xdata.xml = getBaseQuantityXml(pp);
			xdata.simple = ss;
			xdata.parent = pp.part;
			model.add(xdata);
		}
	}

	private void append(String text) {
		textArea.append("\n" + text);
	}

	private Element getRemoveXml(PartData parent, PartData child) {
		Element ele = docs.createElement("EOStructure");
		WTPart basePart = parent.basePart;
		String bb = "";
		String bv = "";
		if (basePart != null) {
			bb = ((WTPartMaster) basePart.getMaster()).getNumber();
			bv = basePart.getVersionIdentifier().getValue();
		}
		createElement(docs, ele, "OLD", ((WTPartMaster) child.part.getMaster()).getNumber(),
				new String[] { "Quantity", "Version", "Unit", "ItemSeq" },
				new String[] { BEContext.quantityFormat.format(child.link.getQuantity().getAmount()),
						child.part.getVersionIdentifier().getValue(), child.link.getQuantity().getUnit().toString(),
						child.orgItemSeq });
		createElement(docs, ele, "NextAssy", ((WTPartMaster) parent.part.getMaster()).getNumber(),
				new String[] { "BasePart", "BasePartVersion", "NextAssyVersion", "OrgStdQuantity", "StdQuantity" },
				new String[] { bb, bv, parent.version, parent.orgBaseQuantity, parent.baseQuantity });
		return ele;
	}

	private Element getAddXml(PartData parent, PartData child) {
		Element ele = docs.createElement("EOStructure");
		WTPart basePart = parent.basePart;
		String bb = "";
		String bv = "";
		if (basePart != null) {
			bb = ((WTPartMaster) basePart.getMaster()).getNumber();
			bv = basePart.getVersionIdentifier().getValue();
		}
		String quantity = (String) child.quantity;
		createElement(docs, ele, "NEW", ((WTPartMaster) child.part.getMaster()).getNumber(),
				new String[] { "Quantity", "Version", "Unit", "ItemSeq" },
				new String[] { quantity, child.version, child.unit, child.itemSeq });
		createElement(docs, ele, "NextAssy", ((WTPartMaster) parent.part.getMaster()).getNumber(),
				new String[] { "BasePart", "BasePartVersion", "NextAssyVersion", "OrgStdQuantity", "StdQuantity" },
				new String[] { bb, bv, parent.version, parent.orgBaseQuantity, parent.baseQuantity });
		return ele;
	}

	private Element getChangeXml(PartData parent, PartData child) {
		Element ele = docs.createElement("EOStructure");
		WTPart basePart = parent.basePart;
		String bb = "";
		String bv = "";
		if (basePart != null) {
			bb = ((WTPartMaster) basePart.getMaster()).getNumber();
			bv = basePart.getVersionIdentifier().getValue();
		}
		String quantity = (String) child.quantity;
		createElement(docs, ele, "NEW", ((WTPartMaster) child.part.getMaster()).getNumber(),
				new String[] { "Quantity", "Version", "Unit", "ItemSeq" },
				new String[] { quantity, child.version, child.unit, child.itemSeq });
		createElement(docs, ele, "OLD", ((WTPartMaster) child.change.part.getMaster()).getNumber(),
				new String[] { "Quantity", "Version", "Unit", "ItemSeq" },
				new String[] { BEContext.quantityFormat.format(child.change.link.getQuantity().getAmount()),
						child.change.version, child.change.link.getQuantity().getUnit().toString(), child.orgItemSeq });
		createElement(docs, ele, "NextAssy", ((WTPartMaster) parent.part.getMaster()).getNumber(),
				new String[] { "BasePart", "BasePartVersion", "NextAssyVersion", "OrgStdQuantity", "StdQuantity" },
				new String[] { bb, bv, parent.version, parent.orgBaseQuantity, parent.baseQuantity });

		return ele;
	}

	private Element getQuantityXml(PartData parent, PartData child) {
		Element ele = docs.createElement("EOStructure");
		WTPart basePart = parent.basePart;
		String bb = "";
		String bv = "";
		if (basePart != null) {
			bb = ((WTPartMaster) basePart.getMaster()).getNumber();
			bv = basePart.getVersionIdentifier().getValue();
		}
		String quantity = (String) child.quantity;
		createElement(docs, ele, "NEW", ((WTPartMaster) child.part.getMaster()).getNumber(),
				new String[] { "Quantity", "Version", "Unit", "ItemSeq" },
				new String[] { quantity, child.version, child.unit, child.itemSeq });
		createElement(docs, ele, "OLD", ((WTPartMaster) child.part.getMaster()).getNumber(),
				new String[] { "Quantity", "Version", "Unit", "ItemSeq" },
				new String[] { BEContext.quantityFormat.format(child.link.getQuantity().getAmount()), child.version,
						child.link.getQuantity().getUnit().toString(), child.orgItemSeq });
		createElement(docs, ele, "NextAssy", ((WTPartMaster) parent.part.getMaster()).getNumber(),
				new String[] { "BasePart", "BasePartVersion", "NextAssyVersion", "OrgStdQuantity", "StdQuantity" },
				new String[] { bb, bv, parent.version, parent.orgBaseQuantity, parent.baseQuantity });

		return ele;
	}

	private Element getBaseQuantityXml(PartData parent) {
		Element ele = docs.createElement("EOStructure");
		WTPart basePart = parent.basePart;
		String bb = "";
		String bv = "";
		if (basePart != null) {
			bb = ((WTPartMaster) basePart.getMaster()).getNumber();
			bv = basePart.getVersionIdentifier().getValue();
		}
		createElement(docs, ele, "NextAssy", ((WTPartMaster) parent.part.getMaster()).getNumber(),
				new String[] { "BasePart", "BasePartVersion", "NextAssyVersion", "OrgStdQuantity", "StdQuantity" },
				new String[] { bb, bv, parent.version, parent.orgBaseQuantity, parent.baseQuantity });

		return ele;
	}

	public Element createElement(Document document, Element element, String elementName, String elementValue,
			String[] attName, String[] attValue) {
		Element element1 = document.createElement(elementName);
		if (attName.length == attValue.length) {
			if (attName.length != 0) {
				for (int i = 0; i < attName.length; i++) {
					element1.setAttribute(attName[i], converter.encode(attValue[i]));
				}
			}
		}

		element1.appendChild(document.createTextNode(converter.encode(elementValue)));
		element.appendChild(element1);
		return element1;
	}

	public void printNode(Node node, String indent) {
		int type = node.getNodeType();
		boolean flag = false;
		switch (type) {
		case Node.DOCUMENT_NODE:
			String name_d = node.getNodeName();
			xmlArea.append(indent + "<" + name_d + ">\n");
			NodeList nl_d = node.getChildNodes();
			if (nl_d.getLength() != 0) {
				textArea.append("\n");
				for (int i = 0; i < nl_d.getLength(); i++) {
					printNode(nl_d.item(i), indent + "   ");
				}
			}
			xmlArea.append(indent + "</" + name_d + ">\n");
			break;
		case Node.ELEMENT_NODE:
			xmlArea.append("\n");
			xmlArea.append(indent + '<');
			xmlArea.append(node.getNodeName());
			Attr attrs[] = sortAttributes(node.getAttributes());
			for (int i = 0; i < attrs.length; i++) {
				Attr attr = attrs[i];
				xmlArea.append("   ");
				xmlArea.append(attr.getNodeName());
				xmlArea.append("=\"");
				xmlArea.append(converter.decode(attr.getNodeValue()));
				xmlArea.append("\"");
			}
			xmlArea.append(">");
			NodeList children = node.getChildNodes();
			if (children != null) {
				int len = children.getLength();
				for (int i = 0; i < len; i++) {
					if (children.item(i).getNodeType() == Node.ELEMENT_NODE)
						flag = true;
					printNode(children.item(i), indent + "   ");
				}
			}
			break;
		case Node.COMMENT_NODE:
		case Node.TEXT_NODE:
			xmlArea.append(converter.decode(node.getNodeValue()));
			break;
		case Node.CDATA_SECTION_NODE:
			xmlArea.append(node.getNodeValue());
			break;
		}

		if (type == Node.ELEMENT_NODE) {
			if (flag) {
				xmlArea.append("\n" + indent + "</");
			} else {
				xmlArea.append("</");
			}
			xmlArea.append(node.getNodeName());
			xmlArea.append(">");
		}
	}

	private Attr[] sortAttributes(NamedNodeMap attrs) {
		int len = (attrs != null) ? attrs.getLength() : 0;
		Attr array[] = new Attr[len];
		for (int i = 0; i < len; i++) {
			array[i] = (Attr) attrs.item(i);
		}
		for (int i = 0; i < len - 1; i++) {
			String name = array[i].getNodeName();
			int index = i;
			for (int j = i + 1; j < len; j++) {
				String curName = array[j].getNodeName();
				if (curName.compareTo(name) < 0) {
					name = curName;
					index = j;
				}
			}
			if (index != i) {
				Attr temp = array[i];
				array[i] = array[index];
				array[index] = temp;
			}
		}
		return (array);
	}

	public class XMLTempModel {
		ArrayList list = new ArrayList();

		public XMLTempModel() {
		}

		public void delete(int state, String key) {

			for (int i = 0; i < list.size(); i++) {
				XMLTempData data = (XMLTempData) list.get(i);

				if (data.key.equals(key) && data.state == state) {
					list.remove(i);
					return;
				}
			}

		}

		public boolean contains(int state, String key) {
			for (int i = 0; i < list.size(); i++) {
				XMLTempData data = (XMLTempData) list.get(i);

				if (data.key.equals(key) && data.state == state) {
					return true;
				}
			}
			return false;
		}

		public boolean isChange(String key) {
			for (int i = 0; i < list.size(); i++) {
				XMLTempData data = (XMLTempData) list.get(i);
				if (key.equals(data.rKey) && data.state == CHANGE) {
					return true;
				}
			}
			return false;
		}

		public XMLTempData get(int i) {
			return (XMLTempData) list.get(i);
		}

		public void add(XMLTempData data) {
			list.add(data);
		}

		public int size() {
			return list.size();
		}

		public void setting() {
			for (int i = list.size() - 1; i >= 0; i--) {
				XMLTempData data = (XMLTempData) list.get(i);
				if (data.state == REMOVE) {
					if (isChange(data.key)) {
						list.remove(i);
						continue;
					}
					;
				}
			}
		}
	};

	public class XMLTempData {
		public String key;
		public int state;
		public Element xml;
		public String simple;
		public String rKey;
		public WTPart parent;

		public XMLTempData(int state) {
			this.state = state;
		}
	};
};
