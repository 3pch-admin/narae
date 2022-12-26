package ext.narae.service.change.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import wt.part.WTPart;
import wt.util.WTContext;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;
import wt.vc.views.ViewReference;

public class EditTreePanel extends JPanel {

	public CTree tree;
	JPopupMenu popup;

	BETreeNode currentNode;
	PartData selectPart;

	QuantityTreeCellEditor celleditor;

	JComboBox box;
	JComboBox viewBox;

	SapBomPanel sapPanel;

	public EditTreePanel() {
		BETreeNode node = new BETreeNode(new IconData(null, "e3ps"));
		DefaultTreeModel model = new DefaultTreeModel(node);
		tree = new CTree(model, this);
		setLayout(new BorderLayout());

		IconCellRenderer renderer = new IconCellRenderer();
		tree.setCellRenderer(renderer);
		tree.setEditable(true);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addTreeExpansionListener(new PartExpansionListener());
		tree.addMouseListener(new MouseClick());
		tree.addTreeSelectionListener(new EditTreeSelectionListener());
		JScrollPane s = new JScrollPane();
		s.getViewport().add(tree);
		add(s, BorderLayout.CENTER);

		sapPanel = new SapBomPanel();
		Progress progress = new Progress();

		JPanel southPanel = new JPanel(new BorderLayout());
		southPanel.add(sapPanel, BorderLayout.CENTER);
		southPanel.add(progress, BorderLayout.SOUTH);
		add(southPanel, BorderLayout.SOUTH);

		JPanel cpanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		box = new JComboBox();
		box.addItem("Expand");
		box.addItem("1Level");
		box.addItem("2Level");
		box.addItem("3Level");
		box.addItem("All");
		cpanel.add(box);
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
		viewBox.addItemListener(new ViewChange());
		viewBox.setVisible(BEContext.isUseView);
		// cpanel.add(viewBox);

		add(cpanel, BorderLayout.NORTH);

		BEContext.setProgress(progress);

		celleditor = new QuantityTreeCellEditor();
		DefaultTreeCellEditor editor = new DefaultTreeCellEditor((JTree) tree, (DefaultTreeCellRenderer) renderer,
				(TreeCellEditor) celleditor);
		tree.setCellEditor(editor);

		setPopup();

	}

	public void setPopup() {
		popup = new JPopupMenu();
		PopupListener pl = new PopupListener();
		JMenuItem menu = new JMenuItem("Add Child Part");
		menu.setActionCommand("append");
		menu.addActionListener(pl);
		popup.add(menu);
		menu = new JMenuItem("Change other Part");
		menu.setActionCommand("change");
		menu.addActionListener(pl);
		popup.add(menu);
		menu = new JMenuItem("Remove Part");
		menu.setActionCommand("remove");
		menu.addActionListener(pl);
		popup.add(menu);
		menu = new JMenuItem("Restore Part");
		menu.setActionCommand("restore");
		menu.addActionListener(pl);
		popup.add(menu);
		popup.addSeparator();
		menu = new JMenuItem("참고 BOM 으로 보내기");
		menu.setActionCommand("goReference");
		menu.addActionListener(pl);
		popup.add(menu);
		// popup.addSeparator();
		// menu=new JMenuItem("선택부품 생산부품의 생성");
		// menu.setActionCommand("createMPart");
		// menu.addActionListener(pl);
		// popup.add(menu);
		// menu=new JMenuItem("선택부품 이하 모든 부품의 생산부품 생성");
		// menu.setActionCommand("createMPartAll");
		// menu.addActionListener(pl);
		// popup.add(menu);
		popup.addSeparator();
		menu = new JMenuItem("부품정보 보기");
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

	public void setTree(WTPart part) {
		ViewReference viewr = part.getView();
		if (viewr != null) {
			viewBox.setSelectedItem(viewr.getName());
		}
		BEContext.model.setPart(part);
		tree.setModel(BEContext.model.model);
		BEContext.support.resetPreview();
		BEContext.setUndoStart();
		tree.repaint();
	}

	public void setTree(DataModel model) {
		tree.setModel(model.model);
		initExpand(BEContext.model.getRoot());
		BEContext.support.resetPreview();
		tree.repaint();
	}

	public void setTree() {
		initExpand(BEContext.model.getRoot());
		BEContext.support.resetPreview();
		tree.repaint();
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

	public void initExpand(BETreeNode node) {
		int count = node.getChildCount();
		if (count > 0) {
			Object o = ((BETreeNode) node.getFirstChild()).getUserObject();
			if (o instanceof PartData) {
				tree.expandPath(new TreePath(node.getPath()));
				Enumeration en = node.children();
				while (en.hasMoreElements()) {
					BETreeNode child = (BETreeNode) en.nextElement();
					initExpand(child);
				}
			}
		}
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

	class EditTreeSelectionListener implements TreeSelectionListener {
		public void valueChanged(TreeSelectionEvent e) {
			final BETreeNode node = getTreeNode(e.getPath());
			sapPanel.setBaseQuantity(node);
			//
		}
	};

	class PartExpansionListener implements TreeExpansionListener {
		public void treeExpanded(TreeExpansionEvent event) {
			final BETreeNode node = getTreeNode(event.getPath());
			final PartData pnode = getPartNode(node);
			System.out.println("###########PartExpansionListener############");
			Thread runner = new Thread() {
				public void run() {
					if (pnode != null && pnode.expand(node, getView())) {
						Runnable runnable = new Runnable() {
							public void run() {
								BEContext.model.reload(node);

								sapPanel.setBaseQuantity(node);
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

	class PopupListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			if (cmd.equals("change")) {
				// PartData pd = getSelectedPart();
				PartSearchDialog spenel = new PartSearchDialog(BEContext.getTop(), "Append Child", BEContext.getView());
				spenel.setMode(cmd);
				spenel.createDialog();
			} else if (cmd.equals("remove")) {
				TreePath path = tree.getSelectionPath();
				BEContext.handler.remove(path);
			} else if (cmd.equals("restore")) {
				TreePath path = tree.getSelectionPath();
				BEContext.handler.restore(path);
			} else if (cmd.equals("append")) {
				// PartData pd = getSelectedPart();
				PartSearchDialog spenel = new PartSearchDialog(BEContext.getTop(), "Append Child", BEContext.getView());
				spenel.setMode(cmd);
				spenel.createDialog();
			} else if (cmd.equals("view")) {
				try {
					PartData pd = getSelectedPart();
					String url = BEContext.getPartViewURL(pd.part);
					WTContext.getContext().showDocument(new URL(BEContext.host + url), "_Blank");
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			} else if (cmd.equals("goReference")) {
				try {
					ReferenceTreePanel panel = new ReferenceTreePanel();
					BETreeNode node = getSelectedNode();
					BETreeNode temp = BEContext.copy(node, new Hashtable());
					panel.setTree(temp);
					panel.isEditTree = true;
					BEContext.support.rtree.createInternalFrame(panel, "작업중 참고 BOM ", Color.red);
					BEContext.support.setTreePanel();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			} else if (cmd.equals("createMPart")) {
				try {
					View mview = ViewHelper.service.getView("Manufacturing");
					BETreeNode node = getSelectedNode();
					PartData data = node.getData();
					if (data == null)
						return;
					String result = createMPart(data.part, mview);
					if (result != null)
						JOptionPane.showMessageDialog(BEContext.getTop(), result);
				} catch (Exception e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(BEContext.getTop(), e1.getMessage());
				}
			} else if (cmd.equals("createMPartAll")) {
				try {
					View mview = ViewHelper.service.getView("Manufacturing");
					BETreeNode node = getSelectedNode();
					String result = createMPartAll(node, mview);
					if (result != null)
						JOptionPane.showMessageDialog(BEContext.getTop(), result);
				} catch (Exception e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(BEContext.getTop(), e1.getMessage());
				}
			}
		}
	}

	public String createMPartAll(BETreeNode node, View view) {
		PartData data = node.getData();
		if (data == null)
			return null;
		String result = createMPart(data.part, view);
		if (!node.isLeaf()) {
			result += "\n";
			Enumeration children = node.children();
			while (children.hasMoreElements()) {
				BETreeNode cnode = (BETreeNode) children.nextElement();
				String ss = createMPartAll(cnode, view);
				if (ss != null) {
					result += ss + "\n";
				}
			}
		}
		return result;
	}

	public String createMPart(WTPart part, View view) {
		try {
			if ("Design".equals(part.getView().getName())) {
				if (BEContext.review(part, view)) {
					return part.getNumber() + " 생산 부품이 생성 되었습니다.";
				} else {
					return part.getNumber() + "생산 부품이 존재 하는 부품입니다.";
				}
			} else {
				return part.getNumber() + " 생산 부품 입니다.";
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(BEContext.getTop(), e1.getMessage());
		}
		return null;
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
				BETreeNode node = BEContext.model.getRoot();
				if (node == null)
					return;
				PartData pd = node.getData();
				if (pd == null)
					return;
				if (view.getName().equals(pd.part.getView().getName()))
					return;
				WTPart part = EulPartHelper.manager.getPart(pd.part, view);
				if (part == null) {
					JOptionPane.showMessageDialog(BEContext.getTop(), view.getName() + " 이 없는 부품입니다.");
					viewBox.setSelectedItem(pd.part.getView().getName());
					return;
				}
				if (JOptionPane.showConfirmDialog(BEContext.getTop(), "작성된 내용이 사라집니다.", "Reload",
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					setTree(part);
					BEContext.eulb = null;
				}

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
			PartData pd = node.getData();
			if (pd == null)
				return;
			pd.expand(node, getView());
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
