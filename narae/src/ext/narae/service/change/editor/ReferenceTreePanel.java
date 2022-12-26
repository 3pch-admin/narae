package ext.narae.service.change.editor;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Enumeration;

import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import wt.part.WTPart;
import wt.util.WTContext;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;
import wt.vc.views.ViewReference;

public class ReferenceTreePanel extends JPanel implements DragGestureListener {

	protected DefaultTreeModel model;
	public JTree tree;
	JPopupMenu popup;

	JComboBox box;
	JComboBox viewBox;
	JComboBox expandBox;

	public boolean isEditTree = false;

	public ReferenceTreePanel() {

		BETreeNode node = new BETreeNode(new IconData(null, "e3ps"));
		model = new DefaultTreeModel(node);
		tree = new JTree(model);
		setLayout(new BorderLayout());

		IconCellRenderer renderer = new IconCellRenderer();

		tree.setCellRenderer(renderer);
		tree.setEditable(false);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addTreeExpansionListener(new PartExpansionListener());
		tree.addMouseListener(new MouseClick());
		JScrollPane s = new JScrollPane();
		s.getViewport().add(tree);
		add(s, BorderLayout.CENTER);

		Progress progress = new Progress();
		add(progress, BorderLayout.SOUTH);

		JPanel cpanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		box = new JComboBox();
		box.addItem("Expand");
		box.addItem("1Level");
		box.addItem("2Level");
		box.addItem("3Level");
		box.addItem("All");
		box.addItemListener(new LevelChange());

		viewBox = new JComboBox();
		try {
			View[] vlist = ViewHelper.service.getAllViews();
			for (int i = 0; i < vlist.length; i++) {
				viewBox.addItem(vlist[i].getName());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		viewBox.setVisible(BEContext.isUseView);
		viewBox.addItemListener(new ViewChange());

		expandBox = new JComboBox();
		expandBox.addItem("정전개");
		expandBox.addItem("역전개");
		expandBox.addItemListener(new expandChange());

		cpanel.add(box);
		// cpanel.add(viewBox);
		cpanel.add(expandBox);

		add(cpanel, BorderLayout.NORTH);

		setPopup();

		DragSource dragsource = DragSource.getDefaultDragSource();
		dragsource.createDefaultDragGestureRecognizer(tree, 3, this);
	}

	public void setPopup() {
		popup = new JPopupMenu();
		PopupListener pl = new PopupListener();
		JMenuItem menu = new JMenuItem("정전개 새창으로 띄우기");
		menu.setActionCommand("descent");
		menu.addActionListener(pl);
		popup.add(menu);
		menu = new JMenuItem("역전개 새창으로 띄우기");
		menu.setActionCommand("ancestor");
		menu.addActionListener(pl);
		popup.add(menu);
		popup.addSeparator();
		menu = new JMenuItem("View Part Info");
		menu.setActionCommand("view");
		menu.addActionListener(pl);
		popup.add(menu);
	}

	public PartData getPartNode(BETreeNode node) {
		if (node == null)
			return null;
		Object obj = node.getUserObject();
		if (obj instanceof IconData)
			return null;
		if (obj instanceof PartData)
			return (PartData) obj;
		else
			return null;
	}

	public View getView() {
		try {
			String ss = (String) viewBox.getSelectedItem();
			return ViewHelper.service.getView(ss);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public void setTree(WTPart part) {
		PartData data = new PartData(part);
		ViewReference viewr = part.getView();
		if (viewr != null) {
			viewBox.setSelectedItem(viewr.getName());
		}
		BETreeNode root = new BETreeNode(data);
		root.add(new BETreeNode(new Boolean(true)));
		String s = (String) expandBox.getSelectedItem();
		data.expand(root, getView(), "정전개".equals(s));
		model.setRoot(root);
		data.cellSet();
		tree.repaint();
	}

	public void setTree(BETreeNode root) { // 작업중 임시 참고 BOM
		viewBox.setEnabled(false);
		expandBox.setEnabled(false);
		model.setRoot(root);
		tree.repaint();
	}

	BETreeNode getTreeNode(TreePath path) {
		return (BETreeNode) (path.getLastPathComponent());
	}

	public PartData getSelectedPart() {
		TreePath selPath = tree.getSelectionPath();
		if (selPath == null) {
			return null;
		}
		BETreeNode node = getTreeNode(selPath);
		if (node == null) {
			return null;
		}
		return getPartNode(node);
	}

	public BETreeNode getSelectedNode() {
		TreePath selPath = tree.getSelectionPath();
		if (selPath == null) {
			return null;
		}
		return getTreeNode(selPath);
	}

	public class MouseClick extends MouseAdapter {
		public void mouseReleased(MouseEvent e) {
			if (e.getModifiers() == InputEvent.BUTTON3_MASK) {
				TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
				if (selPath == null) {
					return;
				}
				BETreeNode n = getTreeNode(selPath);
				if (n == null)
					return;
				PartData pnode = getPartNode(n);
				if (pnode == null)
					return;
				tree.setSelectionPath(selPath);
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	class PartExpansionListener implements TreeExpansionListener {
		public void treeExpanded(TreeExpansionEvent event) {
			final BETreeNode node = getTreeNode(event.getPath());
			final PartData pnode = getPartNode(node);

			Thread runner = new Thread() {
				public void run() {
					String s = (String) expandBox.getSelectedItem();

					if (pnode != null && pnode.expand(node, getView(), "정전개".equals(s))) {
						Runnable runnable = new Runnable() {
							public void run() {
								model.reload(node);
							}
						};
						SwingUtilities.invokeLater(runnable);
					}
				}
			};
			runner.start();
		}

		public void treeCollapsed(TreeExpansionEvent event) {
		}
	}

	public void dragGestureRecognized(DragGestureEvent draggestureevent) {
		String s = (String) expandBox.getSelectedItem();
		String ss = BEContext.getDragMode();
		if ("역전개".equals(s) && "Base Copy Mode".equals(ss)) {
			BEContext.state.setWarning("역전개 모드에서 BaseCopy를 할수 없습니다.");
			return;
		}

		Point point = draggestureevent.getDragOrigin();
		TreePath treepath = tree.getPathForLocation(point.x, point.y);
		if (treepath == null)
			return;
		Rectangle rectangle = tree.getPathBounds(treepath);
		Point _ptOffset = new Point();
		_ptOffset.setLocation(point.x - rectangle.x, point.y - rectangle.y);
		JLabel jlabel = (JLabel) tree.getCellRenderer().getTreeCellRendererComponent(tree,
				treepath.getLastPathComponent(), false, tree.isExpanded(treepath),
				tree.getModel().isLeaf(treepath.getLastPathComponent()), 0, false);
		jlabel.setSize((int) rectangle.getWidth(), (int) rectangle.getHeight());
		BufferedImage _imgGhost = new BufferedImage((int) rectangle.getWidth(), (int) rectangle.getHeight(), 3);
		Graphics2D graphics2d = _imgGhost.createGraphics();
		graphics2d.setComposite(AlphaComposite.getInstance(2, 0.5F));
		jlabel.paint(graphics2d);
		Icon icon = jlabel.getIcon();
		int i = icon != null ? icon.getIconWidth() + jlabel.getIconTextGap() : 0;
		graphics2d.setComposite(AlphaComposite.getInstance(4, 0.5F));
		graphics2d.setPaint(
				new GradientPaint(i, 0.0F, SystemColor.controlShadow, getWidth(), 0.0F, new Color(255, 255, 255, 0)));
		graphics2d.fillRect(i, 0, getWidth(), _imgGhost.getHeight());
		graphics2d.dispose();
		tree.setSelectionPath(treepath);

		CTransferableTreePath ctransferabletreepath = new CTransferableTreePath(treepath);
		BEContext.editor.tree.setImage(_imgGhost);
		draggestureevent.startDrag(null, _imgGhost, new Point(5, 5), ctransferabletreepath, BEContext.editor.tree);
	}

	class PopupListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			if (cmd.equals("ancestor")) {
				PartData pd = getSelectedPart();
				ReferenceTreePanel panel = new ReferenceTreePanel();
				panel.expandBox.setSelectedItem("역전개");
				panel.setTree(pd.part);
				BEContext.support.rtree.createInternalFrame(panel,
						pd.part.getNumber() + " (" + pd.part.getName() + ")");
			} else if (cmd.equals("descent")) {
				PartData pd = getSelectedPart();
				ReferenceTreePanel panel = new ReferenceTreePanel();
				panel.setTree(pd.part);
				BEContext.support.rtree.createInternalFrame(panel,
						pd.part.getNumber() + " (" + pd.part.getName() + ")");
			} else if (cmd.equals("view")) {
				try {
					PartData pd = getSelectedPart();
					String url = BEContext.getPartViewURL(pd.part);
					WTContext.getContext().showDocument(new URL(BEContext.host + url), "_Blank");
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
	}

	class LevelChange implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			if (ItemEvent.SELECTED != e.getStateChange())
				return;
			String level = (String) box.getModel().getSelectedItem();
			if ("1Level".equals(level)) {
				expand(1);
			} else if ("2Level".equals(level)) {
				expand(2);
			} else if ("3Level".equals(level)) {
				expand(3);
			} else if ("All".equals(level)) {
				expand(30);
			}

			box.setSelectedIndex(0);
		}
	};

	class ViewChange implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			if (ItemEvent.SELECTED != e.getStateChange())
				return;
			try {
				View view = getView();
				BETreeNode node = (BETreeNode) model.getRoot();
				PartData pd = node.getData();
				if (view.getName().equals(pd.part.getView().getName()))
					return;
				WTPart part = EulPartHelper.manager.getPart(pd.part, view);
				if (part == null) {
					JOptionPane.showMessageDialog(BEContext.getTop(), view.getName() + " 이 없는 부품입니다.");
					viewBox.setSelectedItem(pd.part.getView().getName());
					return;
				}
				setTree(part);

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	};

	class expandChange implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			if (ItemEvent.SELECTED != e.getStateChange())
				return;
			try {
				BETreeNode node = (BETreeNode) model.getRoot();
				PartData pd = node.getData();
				if (pd == null)
					return;
				setTree(pd.part);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	};

	public void expand(int level) {
		DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
		BETreeNode bn = (BETreeNode) model.getRoot();
		expand(bn, level);
	}

	public void expand(BETreeNode node, int level) {
		if (node.getLevel() < level) {
			((PartData) node.getUserObject()).expand(node, getView());
			tree.expandPath(new TreePath(node.getPath()));
			Enumeration en = node.children();
			while (en.hasMoreElements()) {
				BETreeNode child = (BETreeNode) en.nextElement();
				expand(child, level);
			}
		} else {
			tree.collapsePath(new TreePath(node.getPath()));
		}
	}
}
