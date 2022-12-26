package ext.narae.service.change.editor;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.SystemColor;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class CArrowImage extends BufferedImage {
	public CArrowImage(int i) {
		this(15, 9, i);
	}

	public CArrowImage(int i, int j, int k) {
		super(i, j, 3);
		_pathArrow = new GeneralPath();
		HashMap hashmap = new HashMap();
		hashmap.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		hashmap.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		RenderingHints renderinghints = new RenderingHints(hashmap);
		Graphics2D graphics2d = createGraphics();
		graphics2d.setRenderingHints(renderinghints);
		float f = getHeight();
		float f1 = getWidth();
		float f2 = f1 / 3F;
		float f3 = f1 / 2.0F;
		float f4 = (f1 * 2.0F) / 3F;
		float f5 = f / 3F;
		float f6 = f / 2.0F;
		float f7 = (f * 2.0F) / 3F;
		switch (k) {
		case 0: // '\0'
			_pathArrow.moveTo(f3, f6);
			_pathArrow.lineTo(f3, 0.0F);
			_pathArrow.lineTo(f1, f - 1.0F);
			_pathArrow.lineTo(0.0F, f - 1.0F);
			_pathArrow.closePath();
			graphics2d.setPaint(
					new GradientPaint(f2, f5, SystemColor.controlLtHighlight, f1, f - 1.0F, SystemColor.controlShadow));
			graphics2d.fill(_pathArrow);
			graphics2d.setColor(SystemColor.controlDkShadow);
			graphics2d.draw(new java.awt.geom.Line2D.Float(0.0F, f - 1.0F, f1, f - 1.0F));
			graphics2d.setColor(SystemColor.controlShadow);
			graphics2d.draw(new java.awt.geom.Line2D.Float(f3, 0.0F, f1, f - 1.0F));
			graphics2d.setColor(SystemColor.controlLtHighlight);
			graphics2d.draw(new java.awt.geom.Line2D.Float(0.0F, f - 1.0F, f3, 0.0F));
			break;

		case 1: // '\001'
			_pathArrow.moveTo(f3, f6);
			_pathArrow.lineTo(f1, 0.0F);
			_pathArrow.lineTo(f3, f - 1.0F);
			_pathArrow.closePath();
			graphics2d.setPaint(
					new GradientPaint(0.0F, 0.0F, SystemColor.controlLtHighlight, f4, f7, SystemColor.controlShadow));
			graphics2d.fill(_pathArrow);
			graphics2d.setColor(SystemColor.controlDkShadow);
			graphics2d.draw(new java.awt.geom.Line2D.Float(f1, 0.0F, f3, f - 1.0F));
			graphics2d.setColor(SystemColor.controlShadow);
			graphics2d.draw(new java.awt.geom.Line2D.Float(f3, f - 1.0F, 0.0F, 0.0F));
			graphics2d.setColor(SystemColor.controlLtHighlight);
			graphics2d.draw(new java.awt.geom.Line2D.Float(0.0F, 0.0F, f1, 0.0F));
			break;

		case 2: // '\002'
			_pathArrow.moveTo(f1 - 1.0F, f5);
			_pathArrow.lineTo(f2, f5);
			_pathArrow.lineTo(f2, 0.0F);
			_pathArrow.lineTo(0.0F, f6);
			_pathArrow.lineTo(f2, f - 1.0F);
			_pathArrow.lineTo(f2, f7);
			_pathArrow.lineTo(f1 - 1.0F, f7);
			_pathArrow.closePath();
			graphics2d.setPaint(new GradientPaint(0.0F, 0.0F, Color.white, 0.0F, f, SystemColor.controlShadow));
			graphics2d.fill(_pathArrow);
			_pathArrow.reset();
			_pathArrow.moveTo(f2, 0.0F);
			_pathArrow.lineTo(f2, f5);
			_pathArrow.moveTo(f1 - 1.0F, f5);
			_pathArrow.lineTo(f1 - 1.0F, f7);
			_pathArrow.lineTo(f2, f7);
			_pathArrow.lineTo(f2, f - 1.0F);
			graphics2d.setColor(SystemColor.controlDkShadow);
			graphics2d.draw(_pathArrow);
			graphics2d.setColor(SystemColor.controlShadow);
			graphics2d.draw(new java.awt.geom.Line2D.Float(0.0F, f6, f2, f - 1.0F));
			_pathArrow.reset();
			_pathArrow.moveTo(0.0F, f6);
			_pathArrow.lineTo(f2, 0.0F);
			_pathArrow.moveTo(f2, f5);
			_pathArrow.lineTo(f1 - 1.0F, f5);
			graphics2d.setColor(SystemColor.controlLtHighlight);
			graphics2d.draw(_pathArrow);
			break;

		case 3: // '\003'
		default:
			_pathArrow.moveTo(0.0F, f5);
			_pathArrow.lineTo(f4, f5);
			_pathArrow.lineTo(f4, 0.0F);
			_pathArrow.lineTo(f1 - 1.0F, f6);
			_pathArrow.lineTo(f4, f - 1.0F);
			_pathArrow.lineTo(f4, f7);
			_pathArrow.lineTo(0.0F, f7);
			_pathArrow.closePath();
			graphics2d.setPaint(new GradientPaint(0.0F, 0.0F, Color.white, 0.0F, f, SystemColor.controlShadow));
			graphics2d.fill(_pathArrow);
			_pathArrow.reset();
			_pathArrow.moveTo(0.0F, f7);
			_pathArrow.lineTo(f4, f7);
			_pathArrow.moveTo(f4, f - 1.0F);
			_pathArrow.lineTo(f1 - 1.0F, f6);
			graphics2d.setColor(SystemColor.controlDkShadow);
			graphics2d.draw(_pathArrow);
			graphics2d.setColor(SystemColor.controlShadow);
			graphics2d.draw(new java.awt.geom.Line2D.Float(f1 - 1.0F, f6, f4, 0.0F));
			_pathArrow.reset();
			_pathArrow.moveTo(f4, 0.0F);
			_pathArrow.lineTo(f4, f5);
			_pathArrow.lineTo(0.0F, f5);
			_pathArrow.lineTo(0.0F, f7);
			_pathArrow.moveTo(f4, f7);
			_pathArrow.lineTo(f4, f - 1.0F);
			graphics2d.setColor(SystemColor.controlLtHighlight);
			graphics2d.draw(_pathArrow);
			break;
		}
	}

	public static final int ARROW_UP = 0;
	public static final int ARROW_DOWN = 1;
	public static final int ARROW_LEFT = 2;
	public static final int ARROW_RIGHT = 3;
	private GeneralPath _pathArrow;
}
