package ext.narae.service.change.editor;

import java.awt.BorderLayout;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

public class SupportTreePanel extends JPanel {

	int windowCount = 0;
	JDesktopPane desktop = null;
	int width = 400;
	int height = 600;
	Integer layer = new Integer(2);
	Icon icon = null;

	public SupportTreePanel() {
		setLayout(new BorderLayout());
		desktop = new JDesktopPane();
		desktop.setBackground(java.awt.Color.lightGray);
		try {
			icon = new ImageIcon(new URL(BEContext.host + "com/e3ps/change/editor/images/ref_icon.gif"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		add(desktop);
	}

	public JInternalFrame createInternalFrame(JPanel panel, String title) {
		return createInternalFrame(panel, title, null);
	}

	public JInternalFrame createInternalFrame(JPanel panel, String title, java.awt.Color bgcolor) {
		JInternalFrame jif = new JInternalFrame(title, true, true, true, true);
		jif.setBounds(20 * (windowCount % 10), 20 * (windowCount % 10), width, height);
		jif.getContentPane().add(panel);
		jif.setFrameIcon(icon);
		if (bgcolor != null) {
			jif.setBackground(bgcolor);
		}
		windowCount++;
		desktop.add(jif, layer);
		try {
			jif.setSelected(true);
		} catch (java.beans.PropertyVetoException e2) {
			e2.printStackTrace();
		}
		jif.show();
		return jif;
	}

	public ReferenceTreePanel getSelectedTree() {
		JInternalFrame frame = desktop.getSelectedFrame();
		if (frame == null)
			return null;
		return (ReferenceTreePanel) frame.getContentPane().getComponent(0);
	}

}
