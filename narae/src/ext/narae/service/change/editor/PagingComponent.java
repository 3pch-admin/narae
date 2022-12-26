package ext.narae.service.change.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JFrame;

import wt.fc.PagingQueryResult;
import wt.fc.PagingSessionHelper;
import wt.query.QuerySpec;

class PagingComponent extends JComponent {

	public static final int DEFAULT = 0;
	public static final int NONE = 1;

	private int buttonType = NONE;
	private Color background;
	private Color fontColor;
	private Color buttonColor;
	private Color selectColor;
	private Color currentColor;
	private final Dimension preferredSize;
	private int psize = 10;
	private int term;
	private QuerySpec spec;
	private PagingQueryResult result;
	private int total;
	private int currentpage;
	private long sessionid;
	private PageButton[] buttons;
	private int current;
	private int button_width = 25;
	private int button_height = 25;
	Vector listener = new Vector();

	public void addListener(QueryNotifier f) {
		listener.add(f);
	}

	public static void main(String[] args) {

		JFrame frame = new JFrame();
		Container container = frame.getContentPane();
		PagingComponent page = null;
		try {
			container.add(BorderLayout.SOUTH, page = new PagingComponent(10, 30));
			frame.setSize(600, 500);
			frame.setVisible(true);
			page.setQuerySpec(new QuerySpec(wt.part.WTPart.class));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(1);
			}
		});
	}

	public PagingComponent(int term, int psize) {
		this(term, psize, null);
	}

	public PagingComponent(int term, int psize, QuerySpec spec) {

		this.term = term;
		this.psize = psize;
		background = Color.white;
		fontColor = Color.black;
		buttonColor = Color.black;
		selectColor = Color.red;
		currentColor = Color.blue;

		preferredSize = new Dimension(300, 50);
		setPreferredSize(preferredSize);

		addMouseListener(new PageMouseHandler());
		addMouseMotionListener(new PageMouseMotionHandler());

		setQuerySpec(spec);
	}

	public void setQuerySpec(QuerySpec spec) { // 입력된 쿼리스펙에 맞도록 새로 초기화 한다.
		this.spec = spec;
		this.sessionid = 0;
		find(1);
	}

	public void setTerm(int term) { // 버튼수 변경
		this.term = term;
		find(currentpage);
	}

	public void find(int page) {

		setQuery(page);

		for (int i = 0; i < listener.size(); i++) {
			QueryNotifier ff = (QueryNotifier) listener.get(i);
			ff.actionPerformed(result);
		}
		/* 이부분에 버튼을 눌렸을경우 다른 컴포넌트와 인터페이스를 연결한다. */

	}

	public void setButtonType(int type) {
		this.buttonType = type;
	}

	class PageMouseHandler extends MouseAdapter {
		public void mousePressed(MouseEvent mouseevent) {
			if (buttons == null)
				return;

			int x = mouseevent.getX();
			int y = mouseevent.getY();

			for (int i = 0; i < buttons.length; i++) {
				if (buttons[i].inCusor(x, y)) {
					find(buttons[i].getPage());
					return;
				}
			}
		}
	}

	class PageMouseMotionHandler extends MouseMotionAdapter {
		public void mouseMoved(MouseEvent mouseevent) {
			try {
				if (buttons == null)
					return;

				int x = mouseevent.getX();
				int y = mouseevent.getY();

				for (int i = 0; i < buttons.length; i++) {
					if (buttons[i].inCusor(x, y)) {
						buttons[i].setColor(selectColor);
					} else {
						buttons[i].setColor(buttonColor);
					}
				}
				buttons[current].setColor(currentColor);
				repaint();
			} catch (Exception ex) {
			}
		}
	}

	private void setQuery(int number) {
		if (spec == null)
			return;

		try {
			if (sessionid <= 0) {
				result = PagingSessionHelper.openPagingSession(0, psize, spec);
			} else {
				result = PagingSessionHelper.fetchPagingSession((number - 1) * psize, psize, sessionid);
			}
			total = result.getTotalSize();
			currentpage = number;
			sessionid = result.getSessionId();
			setButtons();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setButtons() {

		int scope = 0;
		if (currentpage == 1) {
			scope = 0;
		} else {
			scope = (currentpage - 1) / term;
		}
		int startpage = scope * term + 1;
		int totalpage = 0;
		if (total % psize == 0) {
			totalpage = total / psize;
		} else {
			totalpage = total / psize + 1;
		}
		int nostart = (current - 1) * term + 1;
		int endpage = startpage + term - 1;
		if (totalpage < endpage) {
			endpage = totalpage;
		}
		/*
		 * int totalpage = total/psize;
		 * 
		 * //if(total%psize>0)totalpage++;
		 * 
		 * int startpage= currentpage/term; if((currentpage%term)>0)startpage++;
		 * startpage = (startpage-1)*term+1; int endpage=startpage+term-1;
		 * if(endpage>totalpage) endpage=totalpage;
		 */
		int buttonLength = 0;
		if (currentpage > term)
			buttonLength += 2;
		if (totalpage > endpage)
			buttonLength += 2;
		buttonLength += (endpage - startpage + 1);

		buttons = new PageButton[buttonLength];

		int k = 0;
		if (currentpage > term) {
			buttons[k++] = new PageButton(1, "◁◁", buttonColor);
			buttons[k++] = new PageButton(startpage - 1, "◁", buttonColor);
		}
		for (int i = startpage; i <= endpage; i++) {
			if (i == currentpage) {
				current = k;
				buttons[k++] = new PageButton(i, Integer.toString(i), currentColor);
			} else {
				buttons[k++] = new PageButton(i, Integer.toString(i), buttonColor);
			}
		}
		if (totalpage > endpage) {
			buttons[k++] = new PageButton(endpage + 1, "▷", buttonColor);
			buttons[k++] = new PageButton(totalpage, "▷▷", buttonColor);
		}
		repaint();
	}

	public void paint(Graphics g) {

		Dimension size = getSize();
		double xw = size.getWidth();
		double yh = size.getHeight();

		g.setColor(background);
		g.fillRect(0, 0, (int) xw, (int) yh);

		if (buttons == null)
			return;
		double startpointX = (xw - (buttons.length * button_width)) / 2;
		double startpointY = (yh - button_height) / 2;

		for (int i = 0; buttons != null && i < buttons.length; i++) {
			buttons[i].draw(g, (int) startpointX + i * button_width, (int) startpointY);
		}
	}

	class PageButton {

		String mark;
		int x;
		int y;
		Color color;
		int page;

		public PageButton(int page, String mark, Color color) {
			this.page = page;
			this.mark = mark;
			this.color = color;
		}

		public boolean inCusor(int cx, int cy) {
			if (x < cx && cx < (x + button_width) && y < cy && cy < (y + button_height))
				return true;
			return false;
		}

		public int getPage() {
			return page;
		}

		public void setColor(Color color) {
			this.color = color;
		}

		public String toString() {
			return mark;
		}

		public void draw(Graphics g, int x, int y) {
			this.x = x;
			this.y = y;

			java.awt.Font font = g.getFont();
			FontMetrics fontmetrics = g.getFontMetrics(font);
			int s_width = fontmetrics.stringWidth(mark);
			int s_height = fontmetrics.getHeight();
			int d_x = (button_width - s_width) / 2 + x;
			int d_y = (button_height - s_height) / 2 + fontmetrics.getAscent() + y;

			if (buttonType == NONE) {
				g.setColor(color);
				g.drawString(mark, d_x, d_y);
				g.setColor(background);
			} else {
				g.setColor(color);
				g.fill3DRect(x, y, button_width, button_height, true);
				g.setColor(fontColor);
				g.drawString(mark, d_x, d_y);
				g.setColor(background);
			}

		}
	}
}
