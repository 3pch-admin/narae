package ext.narae.service.change.editor;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.tree.*;

import wt.part.QuantityUnit;

public class QuantityTreeCellEditor implements TreeCellEditor {

  QuantityEditor currentEditor;
  UnitEditor unitEditor;
  ItemSeqEditor itemSeqEditor;
    public QuantityTreeCellEditor() {
       currentEditor = new QuantityEditor();
       unitEditor = new UnitEditor();
       itemSeqEditor = new ItemSeqEditor();
       currentEditor.setMargin(new Insets(0, 0, 0, 0));
       itemSeqEditor.setMargin(new Insets(0, 0, 0, 0));
    }

    public Component getTreeCellEditorComponent(JTree tree, Object value,
                                                boolean isSelected,
                                                boolean expanded,
                                                boolean leaf, int row) {

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		BETreeNode node = BEContext.editor.getSelectedNode();
		PartData pd = (PartData)node.getUserObject();
		if(pd!=null){
			JLabel l = new JLabel(pd.editString());
			l.setBackground(Color.white);
			currentEditor.setText(pd.quantity);
			
			itemSeqEditor.setText(pd.itemSeq);
			
			unitEditor.setOptions(pd);
			
			

			panel.add(l,BorderLayout.CENTER);
			if(!node.isRoot()){
				JPanel editPanel = new JPanel(new BorderLayout());

				editPanel.add(currentEditor,BorderLayout.CENTER);
				editPanel.add(unitEditor,BorderLayout.EAST);
				panel.add(editPanel,BorderLayout.EAST);
				
				panel.add(itemSeqEditor,BorderLayout.WEST);
			}
		}else{
			cancelCellEditing();
			return panel;
		}
        return panel;
    }

    public Object getCellEditorValue() {
      return currentEditor.getCellEditorValue();
    }

    // All cells are editable in this example...
    public boolean isCellEditable(EventObject event) {
      return true;
    }

    public boolean shouldSelectCell(EventObject event) {
      return currentEditor.shouldSelectCell(event) && itemSeqEditor.stopCellEditing();
    }

    public boolean stopCellEditing() {
      return currentEditor.stopCellEditing() && itemSeqEditor.stopCellEditing() && unitEditor.stopCellEditing();
    }

    public void cancelCellEditing() {
      currentEditor.stopCellEditing();
      itemSeqEditor.stopCellEditing();
      unitEditor.stopCellEditing();
    }

    public void addCellEditorListener(CellEditorListener l) {
      currentEditor.addCellEditorListener(l);
      itemSeqEditor.addCellEditorListener(l);
      unitEditor.addCellEditorListener(l);
    }

    public void removeCellEditorListener(CellEditorListener l) {
      currentEditor.removeCellEditorListener(l);
      itemSeqEditor.removeCellEditorListener(l);
      unitEditor.removeCellEditorListener(l);
    }
}
