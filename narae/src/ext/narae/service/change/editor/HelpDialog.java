package ext.narae.service.change.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class HelpDialog extends JDialog {

	JDialog dialog;
	JLabel textArea = new JLabel();
	JButton close_bt;

	public HelpDialog(Component c, String name) {
		super((Frame) c, name, false);
		getContentPane().setLayout(new BorderLayout());
		JPanel b_Panel = new JPanel();
		b_Panel.setLayout(new FlowLayout());
		b_Panel.add(close_bt = new JButton("Close"));
		close_bt.addActionListener(new PreButtonAction());
		getContentPane().add(BorderLayout.NORTH, b_Panel);

		JPanel main = new JPanel();
		main.setLayout(new BorderLayout());

		textArea.setBackground(Color.white);
		main.add(textArea);
		main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		JScrollPane scroll = new JScrollPane();
		scroll.getViewport().add(main);

		getContentPane().add(BorderLayout.CENTER, scroll);
		dialog = this;

		textArea.setVerticalAlignment(JLabel.TOP);
		textArea.setHorizontalAlignment(JLabel.LEFT);
		textArea.setBackground(Color.white);
		textArea.setText(getHelpMessage());
	}

	public JDialog createDialog() {
		dialog.setSize(550, 360);
		dialog.setLocation(200, 50);
		dialog.setVisible(true);
		dialog.setResizable(true);
		return dialog;
	}

	public String getHelpMessage() {
		String result = "";
		try {
			HttpMessage httpmessage = new HttpMessage(new java.net.URL(BEContext.host + "jsp/change/help.jsp"));
			BufferedReader reader = new BufferedReader(new InputStreamReader(httpmessage.sendGetMessage()));
			for (String temp = ""; temp != null; temp = reader.readLine()) {
				result += temp;
			}
		} catch (Exception ex) {
			result = "<html>도움말을 읽어들이는데 실패 했습니다.<br>" + ex.getMessage() + "</html>";
		}
		return result;
	}

	public void close() {
		dialog.dispose();
	}

	public class PreButtonAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Object o = e.getSource();
			if (o == close_bt) {
				close();
			}
		}
	};
};
