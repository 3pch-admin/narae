package ext.narae.service.change.editor;

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.tree.TreePath;

import wt.fc.ReferenceFactory;
import wt.part.WTPart;
import wt.vc.views.View;

public class EditHandler {
	public void setBaseQuantity(String bb, PartData pd) {

		try {

			if (pd != null) {
				if (pd.isEditable) {
					pd.baseQuantity = bb;

					pd.cellSet();

					BEContext.editor.repaint();
					BEContext.support.resetPreview();
					BEContext.action();

				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void drop(TreePath treepath, Object o) {

		String ss = BEContext.getDragMode();

		if (o instanceof TreePath) {
			TreePath path = (TreePath) o;
			BETreeNode node = (BETreeNode) path.getLastPathComponent();
			PartData data = node.getData();
			if ("Add Child Mode".equals(ss)) {
				BEContext.handler.addPart(treepath, data);
			} else if ("Change Mode".equals(ss)) {
				BEContext.handler.change(treepath, data);
			} else if ("Base Copy Mode".equals(ss)) {
				BEContext.handler.baseCopy(treepath, node);
			}
		} else if (o instanceof String) {
			String oid = (String) o;
			if ("Add Child Mode".equals(ss)) {
				BEContext.handler.addPart(treepath, oid);
			} else if ("Change Mode".equals(ss)) {
				BEContext.handler.change(treepath, oid);
			} else if ("Base Copy Mode".equals(ss)) {
				BEContext.handler.baseCopy(treepath, oid);
			}
		}
	}

	public void addPart(WTPart part) {
		BETreeNode node = BEContext.editor.getSelectedNode();
		addPart(node, part);
	}

	public void addPart(TreePath path, String oid) {
		try {
			BETreeNode node = (BETreeNode) path.getLastPathComponent();

			System.out.println("node == " + node);
			ReferenceFactory rf = new ReferenceFactory();
			WTPart part = (WTPart) rf.getReference(oid).getObject();
			addPart(node, part);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void addPart(TreePath path, PartData tdata) {
		try {
			BETreeNode node = (BETreeNode) path.getLastPathComponent();
			addPart(node, tdata.part, tdata, true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void addPart(BETreeNode node, WTPart part) {
		addPart(node, part, null, true);
	}

	public void addPart(BETreeNode node, WTPart part, PartData tdata, boolean isAction) {
		try {

			PartData pd = node.getData();

			System.out.println("pd !! == " + pd);

			pd.expand(node, BEContext.getView());

			if (!"작업 중".equals(pd.state)) {
				BEContext.state.setWarning(pd.partNumber + "의 상태가 작업 중이 아니므로 품목을 수정할 수 없습니다.");
				return;
			}
			if (!pd.isEditable) {
				BEContext.state.setWarning("수정할 수 없는 품목입니다.");
				return;
			}
			if (!addCheck(node, part)) {
				BEContext.state.setWarning("상위 노드에 존재하는 품목입니다.");
				return;
			}
			part = ViewCheck(part);
			if (part == null) {
				BEContext.state.setWarning("추가하고자 하는 품목의 View가 존재 하지 않습니다.");
				return;
			}

			Enumeration en = node.children();
			int seq = 0;
			while (en.hasMoreElements()) {
				BETreeNode nod = (BETreeNode) en.nextElement();
				PartData cpd = nod.getData();
				String itemseq = cpd.itemSeq;
				if (itemseq == null || itemseq.length() == 0)
					itemseq = "0";
				int ss = Integer.parseInt(itemseq);
				if (seq < ss)
					seq = ss;
			}

			seq = (seq + 10) / 10 * 10;
			String itemSeq = BEContext.itemSeqFormat.format(seq);

			Vector list = BEContext.model.getSamePartNode(pd.part);
			for (int i = 0; i < list.size(); i++) {
				BETreeNode n = (BETreeNode) list.get(i);
				PartData pdata = n.getData();
				if (pdata == null)
					continue;
				BETreeNode newNode = BEContext.model.getNewNode(part, pd.part, null);
				n.add(newNode);
				PartData ppd = newNode.getData();
				if (tdata != null) { // 링크 정보 입력
					ppd.setQuantity(tdata.quantity);
				}

				ppd.itemSeq = itemSeq;

				ppd.cellSet();
			}
			if (isAction) {
				BEContext.model.reload(node);
				BEContext.action();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void remove(TreePath path) {
		BETreeNode node = (BETreeNode) path.getLastPathComponent();
		remove(node, true);
	}

	public void remove(BETreeNode node, boolean isAction) {
		try {
			if (node == null)
				return;
			PartData pd = node.getData();
			if (pd == null)
				return;

			BETreeNode parent = (BETreeNode) node.getParent();
			Vector list = BEContext.model.getSamePartNode(parent);

			for (int i = 0; i < list.size(); i++) {
				BETreeNode nn = (BETreeNode) list.get(i);
				PartData pdd = nn.getData();
				if (pdd == null)
					continue;

				if (!"작업 중".equals(pdd.state)) {
					BEContext.state.setWarning(pdd.partNumber + "의 상태가 작업 중이 아니므로 품목을 수정할 수 없습니다.");
					continue;
				}
				if (!pdd.isEditable) {
					BEContext.state.setWarning("수정할 수 없는 품목입니다.");
					continue;
				}

				Enumeration children = nn.children();
				while (children.hasMoreElements()) {
					BETreeNode ctn = (BETreeNode) children.nextElement();
					PartData cpd = ctn.getData();
					if (cpd == null)
						continue;
					if (cpd.getKey().equals(pd.getKey())) {
						if (cpd.link == null) {
							nn.remove(ctn);
							BEContext.model.reload(nn);
						} else {
							cpd.isRemove = true;
							cpd.cellSet();
						}
					}
				}
			}
			if (isAction) {
				BEContext.editor.tree.repaint();
				BEContext.action();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void restore(TreePath path) {
		try {
			BETreeNode node = (BETreeNode) path.getLastPathComponent();
			if (node == null)
				return;
			PartData pd = node.getData();
			if (pd == null)
				return;

			BETreeNode parent = (BETreeNode) node.getParent();
			Vector list = BEContext.model.getSamePartNode(parent);

			for (int i = 0; i < list.size(); i++) {
				BETreeNode nn = (BETreeNode) list.get(i);
				PartData pdd = nn.getData();
				if (pdd == null)
					continue;

				Enumeration children = nn.children();
				while (children.hasMoreElements()) {
					BETreeNode ctn = (BETreeNode) children.nextElement();
					PartData cpd = ctn.getData();
					if (cpd == null)
						continue;
					if (cpd.getKey().equals(pd.getKey())) {
						if (cpd.link == null) {
						} else {
							cpd.isRemove = false;
							cpd.cellSet();
						}
					}
				}
			}
			BEContext.editor.tree.repaint();
			BEContext.action();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void change(WTPart part) {
		BETreeNode node = BEContext.editor.getSelectedNode();
		change(node, part, null);
	}

	public void change(TreePath path, String oid) {
		try {
			BETreeNode node = (BETreeNode) path.getLastPathComponent();
			ReferenceFactory rf = new ReferenceFactory();
			WTPart part = (WTPart) rf.getReference(oid).getObject();
			change(node, part, null);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void change(TreePath path, PartData tdata) {
		try {
			BETreeNode node = (BETreeNode) path.getLastPathComponent();
			change(node, tdata.part, tdata);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void change(BETreeNode nn, WTPart part, PartData tdata) {
		try {
			BETreeNode node = (BETreeNode) nn.getParent();
			if (node == null)
				return;
			PartData pd = node.getData();
			if (pd == null)
				return;

			if (!"작업 중".equals(pd.state)) {
				BEContext.state.setWarning(pd.partNumber + "의 상태가 작업 중이 아니므로 품목을 수정할 수 없습니다.");
				return;
			}
			if (!pd.isEditable) {
				BEContext.state.setWarning("수정할 수 없는 품목입니다.");
				return;
			}
			if (!addCheck(node, part)) {
				BEContext.state.setWarning("상위 노드에 존재하는 품목입니다.");
				return;
			}
			part = ViewCheck(part);
			if (part == null) {
				BEContext.state.setWarning("교체하고자 하는 품목의 View가 존재 하지 않습니다.");
				return;
			}

			PartData ipd = nn.getData();
			if (ipd.part.getNumber().equals(part.getNumber()))
				return;
			remove(nn, false);
			pd.expand(node, BEContext.getView());
			Vector list = BEContext.model.getSamePartNode(pd.part);
			for (int i = 0; i < list.size(); i++) {
				BETreeNode n = (BETreeNode) list.get(i);
				PartData pdata = n.getData();
				if (pdata == null)
					continue;
				BETreeNode newNode = BEContext.model.getNewNode(part, pd.part, null);
				PartData pdo = newNode.getData();
				if (ipd.isRemove) {
					pdo.change = ipd;
				}
				pdo.itemSeq = ipd.itemSeq;
				pdo.setQuantity(ipd.quantity);

				n.add(newNode);

				if (tdata != null) { // 링크 정보 입력
					// pdo.setQuantity(tdata.quantity);
				}
				pdo.cellSet();
			}
			BEContext.model.reload(node);
			BEContext.action();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void baseCopy(TreePath path, String oid) {
		try {
			BETreeNode node = (BETreeNode) path.getLastPathComponent();
			ReferenceFactory rf = new ReferenceFactory();
			WTPart part = (WTPart) rf.getReference(oid).getObject();
			baseCopy(node, part, null);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void baseCopy(TreePath path, BETreeNode tnode) {
		try {
			BETreeNode node = (BETreeNode) path.getLastPathComponent();
			PartData tdata = tnode.getData();
			baseCopy(node, tdata.part, tnode);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void baseCopy(BETreeNode nn, WTPart part, BETreeNode tnode) {
		try {
			part = EulPartHelper.manager.getPart(part, BEContext.getView());
			PartData pd = nn.getData();
			if (pd != null && !pd.isEditable) {
				BEContext.state.setWarning("수정할 수 없는 품목입니다.");
				return;
			}
			if (tnode == null) {
				Vector children = EulPartHelper.manager.descentLastPart(part, BEContext.getView());
				if (children.size() == 0) {
					BEContext.state.setWarning("복사할 하위 품목이 존재하지 않습니다.");
					return;
				}
				for (int i = 0; i < children.size(); i++) {
					Object[] o = (Object[]) children.get(i);
					addPart(nn, EulPartHelper.manager.getPart((WTPart) o[1], BEContext.getView()), null, false);
				}
			} else {
				Enumeration children = tnode.children();
				boolean flag = true;
				while (children.hasMoreElements()) {
					BETreeNode child = (BETreeNode) children.nextElement();
					PartData cdata = child.getData();
					if (cdata == null)
						continue;
					flag = false;
					addPart(nn, EulPartHelper.manager.getPart(cdata.part, BEContext.getView()), cdata, false);
				}
				if (flag) {
					BEContext.state.setWarning("복사할 하위 품목이 존재하지 않습니다.");
					return;
				}
			}
			nn.getData().basePart = part;
			BEContext.model.reload(nn);
			BEContext.action();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public boolean addCheck(BETreeNode node, WTPart part) {

		/*
		 * 동일 부품 체크 삭제 Enumeration en = node.children(); while(en.hasMoreElements()){
		 * BETreeNode nod = (BETreeNode)en.nextElement(); PartData pd = nod.getData();
		 * if(pd==null)return false; if(pd.getKey().equals(BEContext.getKey(part))){
		 * return false; } }
		 */

		PartData pd = node.getData();
		Vector list = BEContext.model.getSamePartNode(pd.part);
		BETreeNode last = null;
		int depth = 0;
		for (int i = 0; i < list.size(); i++) {
			BETreeNode nn = (BETreeNode) list.get(i);
			int k = nn.getDepth();
			if (i == 0 || k > depth) {
				last = nn;
				depth = k;
			}
		}
		return isNotParent(last, part);
	}

	public boolean isNotParent(BETreeNode node, WTPart part) {
		PartData pd = node.getData();
		if (pd.getKey().equals(BEContext.getKey(part))) {
			return false;
		}
		BETreeNode parent = (BETreeNode) node.getParent();
		if (parent != null) {
			return isNotParent(parent, part);
		}
		return true;
	}

	public WTPart ViewCheck(WTPart part) {
		try {
			View view = BEContext.getView();
			String vv = part.getView().getName();
			if (view.getName().equals(vv)) {
				return part;
			}
			return EulPartHelper.manager.getPart(part, view);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
}
