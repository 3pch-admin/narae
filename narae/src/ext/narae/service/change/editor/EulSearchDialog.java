package ext.narae.service.change.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import ext.narae.service.approval.beans.ApprovalHelper;
import ext.narae.service.change.EOEul;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.session.SessionHelper;
import wt.vc.views.View;

public class EulSearchDialog extends JDialog {
	SelectEulPanel table_p;
	Progress progress;
	JDialog dialog;
	String mode;
	boolean command = false;

	public EulSearchDialog(Component c, String name, View view) {
		super((Frame) c, name, true);
		getContentPane().setLayout(new BorderLayout());
		progress = new Progress();
		table_p = new SelectEulPanel(progress, view);
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
				EOEulData edata = table_p.m_data.getData(row);
				if (ApprovalHelper.MASTER_APPROVED.equals(edata.state)
						|| ApprovalHelper.MASTER_APPROVING.equals(edata.state)) {
					return;
				}

				boolean isCreator = true;

				try {
					WTPrincipalReference wpr = edata.eul.getOwner();
					String sname = SessionHelper.manager.getPrincipal().getName();
					if (wpr != null) {
						WTUser creator = (WTUser) wpr.getObject();
						if (creator != null) {
							isCreator = sname.equals(creator.getName());
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				if (!isCreator)
					return;

				final EOEul eul = table_p.getEul(row);

				if (eul.getBaseline() != null) {
					return;
				}
				Thread thread = new Thread() {
					public void run() {
						try {
							if (BEContext.model.getRoot() != null) {
								int i = JOptionPane.showOptionDialog(dialog, "작성된 내용이 사라집니다.", "경고", -1, 2, null,
										new String[] { "Yes", "No" }, "No");
								if (i == 1)
									return;
							}

							progress.run("Loading...");
							command = true;
							BEContext.println("load Eul");
							BEContext.loadEul(eul);
							close();
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
	}
}
