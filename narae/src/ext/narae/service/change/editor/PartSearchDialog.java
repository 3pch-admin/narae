package ext.narae.service.change.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import wt.part.WTPart;
import wt.vc.views.View;

public class PartSearchDialog extends JDialog {
	SelectPartPanel table_p;
	Progress progress;
	JDialog dialog;
	String mode;
	boolean command = false;

	public PartSearchDialog(Component c, String name, View view) {
		super((Frame) c, name, true);
		getContentPane().setLayout(new BorderLayout());
		progress = new Progress();
		table_p = new SelectPartPanel(progress, view);
		table_p.m_table.addMouseListener(new MouseHandle());
		table_p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		getContentPane().add(BorderLayout.CENTER, table_p);
		getContentPane().add(BorderLayout.SOUTH, progress);
		dialog = this;
	}

	public JDialog createDialog() {
		// dialog =super.createDialog(c,name);
		dialog.setSize(600, 400);
		dialog.setLocation(200, 50);
		dialog.setVisible(true);
		dialog.setResizable(true);
		return dialog;
	}

	public void close() {
		dialog.dispose();
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public class MouseHandle extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				if (command)
					return;
				int row = table_p.m_table.getSelectedRow();
				if (row < 0)
					return;
				final WTPart epm = table_p.getEPM(row);
				Thread thread = new Thread() {
					public void run() {
						try {
							progress.run("Loading...");
							command = true;
							if ("change".equals(mode)) {
								BEContext.println("change part");
								BEContext.handler.change(epm);
								close();
							} else if ("append".equals(mode)) {
								BEContext.println("append part");
								BEContext.handler.addPart(epm);
								close();
							}
						} catch (Exception eo) {
							progress.stop();
							command = false;
							eo.printStackTrace();
							JOptionPane.showMessageDialog(dialog, ((Throwable) eo).getLocalizedMessage());
						}
						progress.stop();
					}
				};
				thread.start();
			}
		}
	};
};
