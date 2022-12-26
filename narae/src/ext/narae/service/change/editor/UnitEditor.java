package ext.narae.service.change.editor;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;

import wt.part.*;

public class UnitEditor extends JComboBox implements CellEditor {
	String value = "";
	Vector listeners = new Vector();

	QuantityUnit[] units;

	public UnitEditor() {

	}

	public void setOptions(PartData pd) {

		this.removeAllItems();

		if (pd == null)
			return;

		if (units == null) {
			units = QuantityUnit.getQuantityUnitSet();
		}

		QuantityUnit dunit = pd.part.getDefaultUnit();
		String category = dunit.getLongDescription();
		if (category == null)
			category = "";

		if ("AAAADL".equals(category)) {
			this.addItem(dunit);
			return;
		}

		for (int i = 0; i < units.length; i++) {
			if (category.equals(units[i].getLongDescription())) {

				this.addItem(units[i]);

				if (units[i].toString().equals(pd.unit)) {
					this.setSelectedItem(units[i]);

				}
			}
		}
	}

	public void cancelCellEditing() {

	}

	// Stop editing only if the user entered a valid value.
	public boolean stopCellEditing() {

		System.out.println("stopCellEditing");

		if (BEContext.editor == null) {
			return true;
		}
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

		QuantityUnit unit = (QuantityUnit) this.getSelectedItem();
		if (unit == null)
			return true;

		String u = unit.toString();
		System.out.println(u + ":" + pd.unit);

		if (u.equals(pd.unit))
			return true;
		pd.setUnit(u);
		System.out.println(u);
		BEContext.action();
		return true;
	}

	public Object getCellEditorValue() {
		return "";
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
