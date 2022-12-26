package ext.narae.service.change.editor;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;

public class QuantityEditor extends JTextField implements CellEditor {
	String value = "";
	Vector listeners = new Vector();

	public QuantityEditor() {
		this("", 5);
	}

	public QuantityEditor(String s) {
		this(s, 5);
	}

	public QuantityEditor(int w) {
		this("", w);
	}

	public QuantityEditor(String s, int w) {
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
			double dd = Double.parseDouble(o);
			o = BEContext.quantityFormat.format(dd);
		} catch (Exception e) {
			cancelCellEditing();
			return false;
		}
		if (o.equals(pd.quantity))
			return true;
		pd.setQuantity(o);

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
