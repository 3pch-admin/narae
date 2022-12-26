package ext.narae.service.change.editor;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SupportPanel extends JPanel {

	public SupportTreePanel rtree;
	public SupportPartSearchPanel list;
	public PreviewPanel preview;
	JTabbedPane tab;

	public SupportPanel() {
		tab = new JTabbedPane(JTabbedPane.BOTTOM);
		tab.addChangeListener(new SupportChangeListener());
		setLayout(new BorderLayout());
		add(tab, BorderLayout.CENTER);
		rtree = new SupportTreePanel();
		list = new SupportPartSearchPanel(BEContext.getView());
		preview = new PreviewPanel();
		tab.addTab("부품 리스트", list);
		tab.addTab("참고 BOM", rtree);
		tab.addTab("변경사항 미리보기", preview);
		BEContext.setSupport(this);
	}

	public void setTreePanel() {
		tab.setSelectedComponent(rtree);
	}

	public void setList() {
		tab.setSelectedComponent(list);
	}

	public void setPreview() {
		tab.setSelectedComponent(preview);
	}

	public class SupportChangeListener implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			JTabbedPane tab = (JTabbedPane) e.getSource();
			Component co = tab.getSelectedComponent();
			if (co == preview) {
				preview.scanChangeData();
			}
		}
	}

	public void resetPreview() {
		if (tab.getSelectedComponent() == preview) {
			preview.scanChangeData();
		}
	}
}
