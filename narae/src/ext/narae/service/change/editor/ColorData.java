package ext.narae.service.change.editor;

import java.awt.Color;

public class ColorData {
	public Color m_color;
	public Color b_color = Color.white;
	public Color e_color;
	public Object m_data;
	public static Color GREEN = new Color(0, 128, 0);
	public static Color BLACK = Color.black;

	public ColorData(Color color) {
		m_color = color;
	}

	public ColorData(Object data, boolean bool) {
		m_color = (bool) ? GREEN : BLACK;
		m_data = data;
	}

	public ColorData(Color color, Object data) {
		m_color = color;
		m_data = data;
	}

	public String toString() {
		if (m_data == null)
			return "-";
		else
			return m_data.toString();
	}
}
