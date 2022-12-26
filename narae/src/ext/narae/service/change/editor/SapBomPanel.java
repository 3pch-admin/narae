package ext.narae.service.change.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SapBomPanel extends JPanel {
	JTextField field;
	PartData pd;

	public SapBomPanel() {
		setPreferredSize(new Dimension(100, 30));
		setBorder(BorderFactory.createBevelBorder(1));

		JLabel label = new JLabel("기준 수량");
		field = new JTextField(10);

		add(label);
		add(field);

		field.addKeyListener(new SapKeyListener());
	}

	public void setBaseQuantity(BETreeNode node) {

		int count = node.getChildCount();

		if (count == 0) {
			field.setText("");
			field.setEnabled(false);
			return;
		}

		BETreeNode cnode = (BETreeNode) node.getChildAt(0);
		PartData dd = cnode.getData();

		if (dd == null) {
			field.setText("");
			field.setEnabled(false);
			return;
		}

		pd = node.getData();

		if (pd != null && !pd.isEditable) {
			field.setText(pd.baseQuantity);
			field.setEnabled(false);
			return;
		}

		field.setEnabled(true);

		if (pd != null) {
			field.setText(pd.baseQuantity);
		}
	}

	class SapKeyListener implements KeyListener {

		public void keyTyped(KeyEvent e) {

		}

		public void keyPressed(KeyEvent e) {
		}

		public void keyReleased(KeyEvent e) {
			setData();
		}

		public void setData() {

			String o = field.getText();
			if (o.length() == 0)
				o = "1";
			try {
				double dd = Double.parseDouble(o);
				o = BEContext.quantityFormat.format(dd);
			} catch (Exception e) {
				return;
			}
			BEContext.handler.setBaseQuantity(o, pd);
		}
	}
}
