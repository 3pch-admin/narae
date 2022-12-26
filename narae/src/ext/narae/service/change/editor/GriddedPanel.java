package ext.narae.service.change.editor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class GriddedPanel extends JPanel {

	public GriddedPanel() {
		this(new Insets(2, 2, 2, 2));
	}

	public GriddedPanel(Insets insets) {
		super(new GridBagLayout());
		constraints = new GridBagConstraints();
		constraints.anchor = 17;
		constraints.insets = insets;
	}

	public void addComponent(JComponent jcomponent, int i, int j) {
		addComponent(jcomponent, i, j, 1, 1, 17, 0);
	}

	public void addComponent(JComponent jcomponent, int i, int j, int k, int l) {
		addComponent(jcomponent, i, j, k, l, 17, 0);
	}

	public void addAnchoredComponent(JComponent jcomponent, int i, int j, int k) {
		addComponent(jcomponent, i, j, 1, 1, k, 0);
	}

	public void addAnchoredComponent(JComponent jcomponent, int i, int j, int k, int l, int i1) {
		addComponent(jcomponent, i, j, k, l, i1, 0);
	}

	public void addFilledComponent(JComponent jcomponent, int i, int j) {
		addComponent(jcomponent, i, j, 1, 1, 17, 2);
	}

	public void addFilledComponent(JComponent jcomponent, int i, int j, int k) {
		addComponent(jcomponent, i, j, 1, 1, 17, k);
	}

	public void addFilledComponent(JComponent jcomponent, int i, int j, int k, int l, int i1) {
		addComponent(jcomponent, i, j, k, l, 17, i1);
	}

	public void addComponent(JComponent jcomponent, int i, int j, int k, int l, int i1, int j1) {
		constraints.gridx = j;
		constraints.gridy = i;
		constraints.gridwidth = k;
		constraints.gridheight = l;
		constraints.anchor = i1;
		double d = 0.0D;
		double d1 = 0.0D;
		if (k > 1)
			d = 1.0D;
		if (l > 1)
			d1 = 1.0D;
		switch (j1) {
		case 2: // '\002'
			constraints.weightx = d;
			constraints.weighty = 0.0D;
			break;

		case 3: // '\003'
			constraints.weighty = d1;
			constraints.weightx = 0.0D;
			break;

		case 1: // '\001'
			constraints.weightx = d;
			constraints.weighty = d1;
			break;

		case 0: // '\0'
			constraints.weightx = 0.0D;
			constraints.weighty = 0.0D;
			break;
		}
		constraints.fill = j1;
		add(jcomponent, constraints);
	}

	private GridBagConstraints constraints;
	private static final int C_HORZ = 2;
	private static final int C_NONE = 0;
	private static final int C_WEST = 17;
	private static final int C_WIDTH = 1;
	private static final int C_HEIGHT = 1;
}
