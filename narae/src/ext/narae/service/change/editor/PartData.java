package ext.narae.service.change.editor;

import java.awt.Color;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import ext.narae.service.part.beans.PartHelper;
import ext.narae.util.iba.IBAUtil;
import wt.fc.PersistenceHelper;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.util.WTException;
import wt.vc.views.View;
import wt.vc.wip.WorkInProgressHelper;

public class PartData {
	public WTPart part;
	public WTPartUsageLink link;
	public WTPart basePart;
	public String name;
	public String version;
	public String state;
	public String type;
	public String lastUpdated;
	public String lvl;
	public String seq;
	public String variant;
	public String remarks;
	public String quantity;
	public PartData change;
	public String number;
	public String partNumber;
	public String unit;
	public ImageIcon icon;
	public String originalPrefix;
	public String originalSuffix;
	public boolean isRemove = false;
	public boolean isEditable = false;
	public static ImageIcon icon1;
	public static ImageIcon icon2;
	public static ImageIcon icon3;

	public String itemSeq = "";
	public String orgItemSeq = "";
	public String baseQuantity = "";
	public String orgBaseQuantity = "";

	public Color color;
	public ArrayList nodeList = new ArrayList();

	static {
		try {
			icon1 = new ImageIcon(new URL(BEContext.host + "com/e3ps/change/editor/images/part.gif"));
			icon2 = new ImageIcon(new URL(BEContext.host + "com/e3ps/change/editor/images/partcheckoutcopy.gif"));
			icon3 = new ImageIcon(new URL(BEContext.host + "com/e3ps/change/editor/images/partcheckout.gif"));
		} catch (Exception ex) {
		}
	}

	public PartData(WTPart part, WTPartUsageLink link) {
		this(part, link, true);
	}

	public PartData(WTPart part, WTPartUsageLink link, boolean flag) {
		this.part = part;
		this.link = link;
		if (flag)
			init();
	}

	public void init() {
		icon = icon1;
		try {
			if (WorkInProgressHelper.isCheckedOut(part)) {
				if (WorkInProgressHelper.isWorkingCopy(part)) {
					icon = icon2;
				} else {
					icon = icon3;
				}
			}

			// version=
			// VersionControlHelper.getQualifiedIdentifier(part).getSeries().getValue();
			version = part.getVersionInfo().getIdentifier().getValue();

		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(BEContext.top, ((Throwable) ex).getLocalizedMessage());
		}

		if (PersistenceHelper.isPersistent(part)) {
			state = part.getLifeCycleState().getDisplay(Locale.KOREA);
		}

		type = part.getType();
		String viewname = part.getViewName();
		if (viewname == null)
			viewname = "";
		lastUpdated = viewname;

		number = part.getIdentity();
		partNumber = part.getNumber();
		name = part.getName();
		try {
			name = name + " ( " + IBAUtil.getAttrValue(part, "Spec") + " )";
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (name == null)
			name = "";
		remarks = "remarks";

		if (link != null) {
			double qs = (double) link.getQuantity().getAmount();
			unit = link.getQuantity().getUnit().toString();
			itemSeq = BEContext.getItemSeq(link);
			if (itemSeq == null)
				itemSeq = "";
			orgItemSeq = itemSeq;
			setQuantity(BEContext.quantityFormat.format(qs));
		} else {
			setQuantity("1");
			unit = ((WTPartMaster) part.getMaster()).getDefaultUnit().toString();
		}

		isEditable = BEContext.isEditable(part);

		orgBaseQuantity = BEContext.getBaseQuantity(part);
		baseQuantity = orgBaseQuantity;

		System.out.println("orgBaseQuantity" + orgBaseQuantity);

		cellSet();
	}

	public void addNode(BETreeNode node) {
		nodeList.add(node);
	}

	public int getNodeLength() {
		return nodeList.size();
	}

	public BETreeNode getNode(int i) {
		return (BETreeNode) nodeList.get(i);
	}

	public PartData(WTPart part) {
		this.part = part;
		init();
	}

	public WTPart getPart() {
		return part;
	}

	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append("<html>");
		sb.append("<table cellpadding=2 cellspacing=0 ><tr><td><font color=blue>");
		sb.append(itemSeq.length() > 0 ? itemSeq : "");
		sb.append("</font></td><td><b><font size=3>");
		sb.append(partNumber.indexOf("_") > -1 ? partNumber.substring(0, partNumber.lastIndexOf("_")) : partNumber);
		sb.append("</font></b></td><td>");
		sb.append(name);
		sb.append("</td><td><font color=green>");
		sb.append(quantity != null ? quantity.toString() : "");
		sb.append("</font></td><td><font color=green>");
		sb.append(unit);
		sb.append("</font></td></tr></table>");
		sb.append("</html>");

		return sb.toString();
	}

	public String editString() {
		StringBuffer sb = new StringBuffer();
		sb.append("<html>");
		sb.append("<table cellpadding=2 cellspacing=0 ><tr><td>");
		sb.append("<b><font size=3>");
		sb.append(partNumber.indexOf("_") > -1 ? partNumber.substring(0, partNumber.lastIndexOf("_")) : partNumber);
		sb.append("</font></b></td><td>");
		sb.append(name);
		sb.append("</td></tr></table>");
		sb.append("</html>");

		return sb.toString();
	}

	public void setLvl(int l) {
		lvl = Integer.toString(l);
	}

	public void setQuantity(String q) {
		quantity = q;
		cellSet();
	}

	public void setUnit(String u) {
		unit = u;
		cellSet();
	}

	public void cellSet() {
		try {
			if (!isEditable) {
				color = new Color(255, 81, 85);
				return;
			}

			boolean isRoot = false;

			if (nodeList != null && nodeList.size() > 0) {
				BETreeNode node = (BETreeNode) nodeList.get(0);
				if (node.isRoot()) {
					isRoot = true;
				}
			}
			if (isRemove) {
				color = new Color(168, 168, 168);
				return;
			}
			if (!PersistenceHelper.isPersistent(part)) {
				color = new Color(239, 202, 138);
				return;
			}

			String q = "";
			if (link != null) {
				double dd = link.getQuantity().getAmount();
				q = BEContext.quantityFormat.format(dd);
			}

			if (!isRoot && change != null) {
				color = new Color(166, 252, 95);
			} else if (!isRoot && link == null) {
				color = Color.green;
			} else if (!isRoot && !q.equals(quantity)) {
				color = Color.yellow;
			} else if (!isRoot && !unit.equals(link.getQuantity().getUnit().toString())) {
				color = Color.yellow;
			} else if (!isRoot && !itemSeq.equals(orgItemSeq)) {
				color = Color.yellow;
			} else if (baseQuantity != null && !baseQuantity.equals(orgBaseQuantity)) {
				color = Color.yellow;
			} else {
				color = Color.white;
			}
		} catch (NumberFormatException ne) {
			if (link == null) {
				color = Color.yellow;
			} else {
				color = Color.white;
			}
		}
	}

	public boolean expand(BETreeNode parent, View view) {
		return expand(parent, view, true);
	}

	public boolean expand(BETreeNode parent, View view, boolean descent) {
		int count = parent.getChildCount();
		if (count == 0)
			return false;

		BETreeNode flag = (BETreeNode) parent.getFirstChild();
		if (flag == null) // No flag
			return false;
		Object obj = flag.getUserObject();
		if (!(obj instanceof Boolean))
			return false;

		parent.removeAllChildren();
		Vector partList = null;

		boolean isEdit = false;
		if (parent.getRoot() == BEContext.model.getRoot()) {
			isEdit = true;
		}

		if (isEdit) {
			Vector list = BEContext.model.getSamePartNode(part);
			for (int i = 0; i < list.size(); i++) {
				BETreeNode bn = (BETreeNode) list.get(i);
				if (0 < bn.getChildCount()) {
					BETreeNode ff = (BETreeNode) bn.getFirstChild();
					PartData fpd = ff.getData();
					if (fpd != null) {
						Enumeration cen = bn.children();
						while (cen.hasMoreElements()) {
							BETreeNode nn = (BETreeNode) cen.nextElement();
							BETreeNode bb = new BETreeNode(nn.getUserObject());
							bb.add(new BETreeNode(new Boolean(true)));
							parent.add(bb);
						}
						return true;
					}
				}
			}
		}
		try {
			if (descent)
				partList = EulPartHelper.manager.descentLastPart(part, view);
			else
				partList = EulPartHelper.manager.ancestorPart(part, view);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		ArrayList temp = new ArrayList();
		for (int k = 0; k < partList.size(); k++) {
			Object obj2 = partList.get(k);
			if (obj2 instanceof Object[]) {
				Object[] o = (Object[]) partList.get(k);
				WTPart p = (WTPart) o[1];
				WTPartUsageLink link = (WTPartUsageLink) o[0];

				BETreeNode node = null;
				if (isEdit) {
					node = BEContext.model.getNewNode(p, part, link);
				} else {
					node = new BETreeNode(new PartData(p, link));
					node.add(new BETreeNode(new Boolean(true)));
				}

				PartData cpd = node.getData();
				String itemSeq = cpd.itemSeq;
				boolean flag2 = true;
				for (int l = 0; l < temp.size(); l++) {

					BETreeNode nn = (BETreeNode) temp.get(l);
					PartData pp = nn.getData();
					if (itemSeq.compareTo(pp.itemSeq) < 0) {
						temp.add(l, node);
						flag2 = false;
						break;
					}
				}

				if (flag2) {
					temp.add(node);
				}
			} else if (obj2 instanceof WTPartUsageLink) {
				try {
					WTPartUsageLink link = (WTPartUsageLink) partList.get(k);
					// WTPart part2 = (WTPart)usageLink.getRoleAObject();
					WTPartMaster master = (WTPartMaster) link.getRoleBObject();

					// WTPart childPart = PartHelper.manager.getPart(master.getNumber());
					WTPart p = PartHelper.manager.getPart(master.getNumber());

					BETreeNode node = null;
					if (isEdit) {
						node = BEContext.model.getNewNode(p, part, link);
					} else {
						node = new BETreeNode(new PartData(p, link));
						node.add(new BETreeNode(new Boolean(true)));
					}

					PartData cpd = node.getData();
					String itemSeq = cpd.itemSeq;
					boolean flag2 = true;
					for (int l = 0; l < temp.size(); l++) {

						BETreeNode nn = (BETreeNode) temp.get(l);
						PartData pp = nn.getData();
						if (itemSeq.compareTo(pp.itemSeq) < 0) {
							temp.add(l, node);
							flag2 = false;
							break;
						}
					}
					if (flag2) {
						temp.add(node);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		String itemMaxSeq = "";
		for (int k = 0; k < temp.size(); k++) {
			BETreeNode node = (BETreeNode) temp.get(k);
			parent.add(node);
		}

		return true;
	}

	public String getKey() {

		return BEContext.getKey(part);
	}
}
