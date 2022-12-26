package ext.narae.service.change.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class BEStateBar extends JPanel {
	JLabel label;
	Timer timer;
	boolean isRun;

	public BEStateBar() {
		label = new JLabel();
		setMessage("");
		setLayout(new BorderLayout());
		add(label);
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(70, 20));
		// panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		ImageIcon icon = null;
		try {
			icon = new ImageIcon(new URL(BEContext.host + "com/e3ps/change/editor/images/hy.gif"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		panel.setLayout(new BorderLayout());
		panel.add(new JLabel(icon));
		add(panel, BorderLayout.EAST);
	}

	public void setMessage(String message) {
		label.setText("  " + message);
	}

	public void setWarning(String message) {
		if (timer != null)
			stop();
		isRun = true;
		final String mm = "<html><font color=red>&nbsp;&nbsp;" + message + "</html>";
		timer = new Timer(true);
		timer.scheduleAtFixedRate(new TimerTask() {
			int i = 0;

			public void run() {
				if (i % 2 > 0)
					label.setText(mm);
				else
					label.setText(" ");
				i++;
				if (i > 5)
					stop();
			}
		}, 0L, 400);
	}

	public void stop() {
		if (timer != null)
			timer.cancel();
		isRun = false;
	}
}
