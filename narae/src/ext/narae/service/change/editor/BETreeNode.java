package ext.narae.service.change.editor;

import javax.swing.tree.DefaultMutableTreeNode;

import wt.part.WTPart;

public class BETreeNode extends DefaultMutableTreeNode {
	String location;

	public BETreeNode(Object o) {
		super(o);
		if (getUserObject() instanceof PartData) {
			PartData pn = (PartData) getUserObject();
			pn.addNode(this);
		}
	}

	public String getLocation() {
		if (location == null) {
			if (getUserObject() instanceof PartData) {
				PartData pn = (PartData) getUserObject();
				if (pn.getPart() instanceof WTPart) {
					setLocation((WTPart) pn.getPart());
				}
			}
		}
		return location;
	}

	private void setLocation(WTPart part) {
		long l = part.getMaster().getPersistInfo().getObjectIdentifier().getId();

		if (isRoot()) {
			location = Long.toString(l);
		} else {
			BETreeNode parent = (BETreeNode) getParent();
			location = parent.getLocation() + "/" + l;
		}
	}

	public PartData getData() {
		Object o = getUserObject();
		if (o instanceof PartData) {
			return (PartData) o;
		}
		return null;
	}
}
