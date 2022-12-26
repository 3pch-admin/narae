package ext.narae.service.change.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;

import wt.part.WTPart;
import wt.vc.wip.WorkInProgressHelper;

public class IconCellRenderer extends DefaultTreeCellRenderer {
	static ImageIcon partImage1;
	static ImageIcon partImage2;
	static ImageIcon partImage3;

	static ImageIcon levelIcon1;
	static ImageIcon levelIcon2;
	static ImageIcon levelIcon3;

	Color bkColor = null;

	static {
		try {
			partImage1 = new ImageIcon(new URL(BEContext.host + "com/e3ps/change/editor/images/part.gif"));
			partImage2 = new ImageIcon(new URL(BEContext.host + "com/e3ps/change/editor/images/partcheckoutcopy.gif"));
			partImage3 = new ImageIcon(new URL(BEContext.host + "com/e3ps/change/editor/images/partcheckout.gif"));

			levelIcon1 = new ImageIcon(new URL(BEContext.host + "com/e3ps/change/editor/images/levelIcon1.gif"));
			levelIcon2 = new ImageIcon(new URL(BEContext.host + "com/e3ps/change/editor/images/levelIcon2.gif"));
			levelIcon3 = new ImageIcon(new URL(BEContext.host + "com/e3ps/change/editor/images/levelIcon3.gif"));

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public IconCellRenderer() {
		setOpenIcon(partImage1);
		setClosedIcon(partImage1);
		setLeafIcon(partImage1);
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
			int row, boolean hasFocus) {

		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

		BETreeNode node = (BETreeNode) value;
		Object obj = node.getUserObject();

		setText(obj.toString());

		if (obj instanceof Boolean)
			setText("Retrieving data...");
		if (obj instanceof IconData) {
			IconData idata = (IconData) obj;
			if (expanded)
				setIcon(idata.getExpandedIcon());
			else
				setIcon(idata.getIcon());
		}
		if (obj instanceof PartData) {
			PartData no = (PartData) obj;

			// setIcon(partImage1);
			setIcon(getLevelIcon(node));

			WTPart part = (WTPart) no.getPart();
			try {
				if (WorkInProgressHelper.isCheckedOut(part)) {
					if (WorkInProgressHelper.isWorkingCopy(part)) {
						setIcon(partImage2);
					} else {
						setIcon(partImage3);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			bkColor = no.color;
		}
		if (!sel && bkColor != null) {
			setBackground(bkColor);
		}
		return this;
	}

	public void paintComponent(Graphics g) {
		Color bColor = getBackground();
		Icon icon = getIcon();

		if (bkColor != null && bkColor != Color.white) {
			g.setColor(bkColor);
			int offset = 0;
			if (icon != null && getText() != null)
				offset = (icon.getIconWidth() + getIconTextGap());
			g.fillRect(offset, 1, getWidth() - 1 - offset, getHeight() - 2);
		}

		g.setColor(bColor);
		super.paintComponent(g);
	}

	public ImageIcon getLevelIcon(TreeNode node) {
		int level = getLevel(node);

		switch (level) {
		case 0:
			return levelIcon1;
		case 1:
			return levelIcon2;
		case 2:
			return levelIcon3;
		}
		return partImage1;
	}

	public int getLevel(TreeNode node) {
		int level = 0;

		TreeNode parent = node.getParent();
		while (parent != null) {
			parent = parent.getParent();
			level++;
		}

		return level;
	}
}
