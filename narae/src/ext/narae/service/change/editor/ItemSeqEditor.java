package ext.narae.service.change.editor;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;

public class ItemSeqEditor extends JTextField implements CellEditor {
	String value = "";
	Vector listeners = new Vector();

	public ItemSeqEditor() {
		this("", 5);
	}

	public ItemSeqEditor(String s) {
		this(s, 5);
	}

	public ItemSeqEditor(int w) {
		this("", w);
	}

	public ItemSeqEditor(String s, int w) {
		super(s, w);
		addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (stopCellEditing()) {
					fireEditingStopped();
				}
			}
		});
	}

	public void cancelCellEditing() {
		setText("");
	}

	// Stop editing only if the user entered a valid value.
	public boolean stopCellEditing() {

		BETreeNode node = BEContext.editor.getSelectedNode();
		if (node == null) {
			cancelCellEditing();
			return true;
		}
		PartData pd = (PartData) node.getUserObject();
		BETreeNode parent = (BETreeNode) node.getParent();
		if (parent != null) {
			PartData ppd = parent.getData();
			if (!ppd.isEditable) {
				return true;
			}
		}
		String o = getText();

		try {
			int dd = Integer.parseInt(o);
			o = BEContext.itemSeqFormat.format(dd);
			o = o.substring(0, 4);
		} catch (Exception e) {
			cancelCellEditing();
			return false;
		}
		if (o.equals(pd.itemSeq))
			return true;

		for (int i = 0; i < parent.getChildCount(); i++) {
			BETreeNode child = (BETreeNode) parent.getChildAt(i);
			PartData childData = (PartData) child.getUserObject();
			String citemSeq = childData.itemSeq;
			if (o.equals(citemSeq)) {
				JOptionPane.showMessageDialog(BEContext.getTop(), "Item Seq 값이 중복됩니다.");
				return true;
			}
		}

		pd.itemSeq = o;
		pd.cellSet();

		BEContext.action();
		return true;
	}

	public Object getCellEditorValue() {
		return getText();
	}

	// Start editing when the right mouse button is clicked.
	public boolean isCellEditable(EventObject eo) {
		return true;
	}

	public boolean shouldSelectCell(EventObject eo) {
		return true;

	}

	// Add support for listeners.
	public void addCellEditorListener(CellEditorListener cel) {
		listeners.addElement(cel);
	}

	public void removeCellEditorListener(CellEditorListener cel) {
		listeners.removeElement(cel);
	}

	protected void fireEditingStopped() {
		if (listeners.size() > 0) {
			ChangeEvent ce = new ChangeEvent(this);
			for (int i = listeners.size() - 1; i >= 0; i--) {
				((CellEditorListener) listeners.elementAt(i)).editingCanceled(ce);
			}
		}
	}
}
