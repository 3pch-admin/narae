package ext.narae.service.change.editor;

import javax.swing.Icon;

public class IconData {
	protected Icon m_icon;
	protected Icon m_expandedIcon;
	protected Object m_data;

	public IconData(Icon icon, Object data) {
		m_icon = icon;
		m_data = data;
	}

	public IconData(Icon icon, Icon expendedIcon, Object data) {
		m_icon = icon;
		m_data = data;
		m_expandedIcon = expendedIcon;
	}

	public Icon getIcon() {
		return m_icon;
	}

	public Icon getExpandedIcon() {
		return m_expandedIcon;
	}

	public Object getObject() {
		return m_data;
	}

	public String toString() {
		return m_data.toString();
	}
}
