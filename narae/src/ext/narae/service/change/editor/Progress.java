package ext.narae.service.change.editor;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.SoftBevelBorder;

public class Progress extends JComponent {
	class Running extends JComponent {
		public void run() {
			if (timer != null)
				stop();
			Dimension dimension = getSize();
			isRun = true;
			timer = new Timer(true);
			timer.scheduleAtFixedRate(new TimerTask() {

				public void run() {
					x += 3;
					repaint();
				}

				boolean back;

				{
					back = true;
				}
			}, 0L, 30L);
		}

		public void stop() {
			if (timer != null)
				timer.cancel();
			x = 0;
			isRun = false;
			repaint();
		}

		public void paintComponent(Graphics g) {
			if (isRun)
				runPaint((Graphics2D) g);
		}

		public void runPaint(Graphics2D graphics2d) {
			Dimension dimension = getSize();
			java.awt.geom.Point2D.Float float1 = new java.awt.geom.Point2D.Float(x, 0.0F);
			java.awt.geom.Point2D.Float float2 = new java.awt.geom.Point2D.Float(x + 150, 0.0F);
			GradientPaint gradientpaint = new GradientPaint(float1, c1, float2, c2, true);
			java.awt.geom.Rectangle2D.Float float3 = new java.awt.geom.Rectangle2D.Float(0.0F, 0.0F,
					(int) dimension.getWidth(), 16F);
			graphics2d.setPaint(gradientpaint);
			graphics2d.fill(float3);
		}

		final int barWidth = 250;
		boolean isRun;
		int x;
		Timer timer;
		Color c1;
		Color c2;

		public Running() {
			isRun = false;
			x = 0;
			c1 = new Color(124, 125, 49);
			c2 = Color.yellow;
			setBackground(Color.white);
			setPreferredSize(new Dimension(300, 22));
		}
	}

	public static void main(String args[]) {
		JFrame jframe = new JFrame();
		Container container = jframe.getContentPane();
		final Progress p = new Progress();
		container.setLayout(new FlowLayout());
		container.add(p);
		jframe.pack();
		jframe.setVisible(true);
		JButton jbutton = new JButton("run");
		JButton jbutton1 = new JButton("stop");
		jbutton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent actionevent) {
				p.run("run");
			}

		});
		jbutton1.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent actionevent) {
				p.stop();
			}

		});
		container.add(jbutton);
		container.add(jbutton1);
	}

	public static Progress getProgress() {
		if (progress == null)
			progress = new Progress();
		return progress;
	}

	public static Progress startProgress() {
		if (progress == null)
			progress = new Progress();
		return progress;
	}

	public Progress() {
		state = new JLabel();
		run = new Running();
		setLayout(new BoxLayout(this, 0));
		setPreferredSize(new Dimension(900, 22));
		state.setPreferredSize(new Dimension(80, 22));
		state.setBackground(Color.white);
		setBorder(new SoftBevelBorder(1));
		add(state);
		add(run);
	}

	public void setLable(String s) {
		state.setText(s);
	}

	public void run(String s) {
		state.setText(s);
		run.run();
	}

	public void stop() {
		state.setText("");
		run.stop();
	}

	static Progress progress = null;
	JLabel state;
	Running run;
}
