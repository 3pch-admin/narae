package ext.narae.service.change.editor;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.Autoscroll;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class CTree extends JTree implements DragSourceListener, DragGestureListener, Autoscroll, TreeModelListener {
	private TreePath _pathSource;
	private BufferedImage _imgGhost;
	private Point _ptOffset;
	CTree ntree;
	boolean drag;
	EditTreePanel ptree;
	private static final int AUTOSCROLL_MARGIN = 12;

	public CTree(DefaultTreeModel defaulttreemodel) {
		super(defaulttreemodel);
		_ptOffset = new Point();
		putClientProperty("JTree.lineStyle", "Angled");
		DragSource dragsource = DragSource.getDefaultDragSource();
		dragsource.createDefaultDragGestureRecognizer(this, 3, this);
		DropTarget droptarget = new DropTarget(this, new CDropTargetListener());
		droptarget.setDefaultActions(3);
	}

	public CTree(DefaultTreeModel defaulttreemodel, EditTreePanel ptree) {
		this(defaulttreemodel);
		drag = true;
		this.ptree = ptree;
	}

	public void setImage(BufferedImage bufferedimage) {
		_imgGhost = bufferedimage;
	}

	public void dragGestureRecognized(DragGestureEvent draggestureevent) {
		if (drag)
			return;
		Point point = draggestureevent.getDragOrigin();
		TreePath treepath = getPathForLocation(point.x, point.y);
		if (treepath == null)
			return;
		Rectangle rectangle = getPathBounds(treepath);
		_ptOffset.setLocation(point.x - rectangle.x, point.y - rectangle.y);
		JLabel jlabel = (JLabel) getCellRenderer().getTreeCellRendererComponent(this, treepath.getLastPathComponent(),
				false, isExpanded(treepath), getModel().isLeaf(treepath.getLastPathComponent()), 0, false);
		jlabel.setSize((int) rectangle.getWidth(), (int) rectangle.getHeight());
		_imgGhost = new BufferedImage((int) rectangle.getWidth(), (int) rectangle.getHeight(), 3);
		Graphics2D graphics2d = _imgGhost.createGraphics();
		graphics2d.setComposite(AlphaComposite.getInstance(2, 0.5F));
		jlabel.paint(graphics2d);
		Icon icon = jlabel.getIcon();
		int i = icon != null ? icon.getIconWidth() + jlabel.getIconTextGap() : 0;
		graphics2d.setComposite(AlphaComposite.getInstance(4, 0.5F));
		graphics2d.setPaint(
				new GradientPaint(i, 0.0F, SystemColor.controlShadow, getWidth(), 0.0F, new Color(255, 255, 255, 0)));
		graphics2d.fillRect(i, 0, getWidth(), _imgGhost.getHeight());
		graphics2d.dispose();
		setSelectionPath(treepath);

		DefaultMutableTreeNode defaultmutabletreenode = (DefaultMutableTreeNode) treepath.getLastPathComponent();
		Object obj = defaultmutabletreenode.getUserObject();
		String s = "";
		if (obj instanceof PartData) {
			s = ((PartData) obj).part.getPersistInfo().getObjectIdentifier().toString();
		} else {
			return;
		}

		CTransferableTreePath ctransferabletreepath = new CTransferableTreePath(s);
		_pathSource = treepath;
		if (ntree != null)
			ntree.setImage(_imgGhost);
		draggestureevent.startDrag(null, _imgGhost, new Point(5, 5), ctransferabletreepath, this);
	}

	public void setTree(CTree ctree) {
		ntree = ctree;
	}

	public void dragEnter(DragSourceDragEvent dragsourcedragevent) {
	}

	public void dragOver(DragSourceDragEvent dragsourcedragevent) {
	}

	public void dragExit(DragSourceEvent dragsourceevent) {
	}

	public void dropActionChanged(DragSourceDragEvent dragsourcedragevent) {
	}

	public void dragDropEnd(DragSourceDropEvent dragsourcedropevent) {
		if (dragsourcedropevent.getDropSuccess()) {
			int i = dragsourcedropevent.getDropAction();
			if (i == 2)
				_pathSource = null;
		}
	}

	public void autoscroll(Point point) {
		int i = getRowForLocation(point.x, point.y);
		if (i < 0) {
			return;
		} else {
			Rectangle rectangle = getBounds();
			i = point.y + rectangle.y > 12 ? i >= getRowCount() - 1 ? i : i + 1 : i > 0 ? i - 1 : 0;
			scrollRowToVisible(i);
			return;
		}
	}

	public Insets getAutoscrollInsets() {
		Rectangle rectangle = getBounds();
		Rectangle rectangle1 = getParent().getBounds();
		return new Insets((rectangle1.y - rectangle.y) + 12, (rectangle1.x - rectangle.x) + 12,
				(rectangle.height - rectangle1.height - rectangle1.y) + rectangle.y + 12,
				(rectangle.width - rectangle1.width - rectangle1.x) + rectangle.x + 12);
	}

	public void treeNodesChanged(TreeModelEvent treemodelevent) {
		sayWhat(treemodelevent);
	}

	public void treeNodesInserted(TreeModelEvent treemodelevent) {
		sayWhat(treemodelevent);
		int i = treemodelevent.getChildIndices()[0];
		TreePath treepath = treemodelevent.getTreePath();
		setSelectionPath(getChildPath(treepath, i));
	}

	public void treeNodesRemoved(TreeModelEvent treemodelevent) {
		sayWhat(treemodelevent);
	}

	public void treeStructureChanged(TreeModelEvent treemodelevent) {
		sayWhat(treemodelevent);
	}

	private TreePath getChildPath(TreePath treepath, int i) {
		TreeModel treemodel = getModel();
		return treepath.pathByAddingChild(treemodel.getChild(treepath.getLastPathComponent(), i));
	}

	private boolean isRootPath(TreePath treepath) {
		return isRootVisible() && getRowForPath(treepath) == 0;
	}

	private void sayWhat(TreeModelEvent treemodelevent) {
		System.out.println(treemodelevent.getTreePath().getLastPathComponent());
		int ai[] = treemodelevent.getChildIndices();
		for (int i = 0; i < ai.length; i++)
			System.out.println(i + ". " + ai[i]);

	}

	class CDropTargetListener implements DropTargetListener {

		private TreePath _pathLast;
		private Rectangle2D _raCueLine;
		private Rectangle2D _raGhost;
		private Color _colorCueLine;
		private Point _ptLast;
		private int _nLeftRight;
		private BufferedImage _imgRight;
		private BufferedImage _imgLeft;
		private int _nShift;

		public CDropTargetListener() {
			_pathLast = null;
			_raCueLine = new java.awt.geom.Rectangle2D.Float();
			_raGhost = new java.awt.geom.Rectangle2D.Float();
			_ptLast = new Point();
			_nLeftRight = 0;
			_imgRight = new CArrowImage(15, 15, 3);
			_imgLeft = new CArrowImage(15, 15, 2);
			_nShift = 0;
			_colorCueLine = new Color(SystemColor.controlShadow.getRed(), SystemColor.controlShadow.getGreen(),
					SystemColor.controlShadow.getBlue(), 64);
		}

		public void dragEnter(DropTargetDragEvent droptargetdragevent) {
			if (!isDragAcceptable(droptargetdragevent))
				droptargetdragevent.rejectDrag();
			else
				droptargetdragevent.acceptDrag(droptargetdragevent.getDropAction());
		}

		public void dragExit(DropTargetEvent droptargetevent) {
			if (!DragSource.isDragImageSupported())
				repaint(_raGhost.getBounds());
		}

		public void dragOver(DropTargetDragEvent droptargetdragevent) {
			Point point = droptargetdragevent.getLocation();
			if (point.equals(_ptLast))
				return;
			int i = point.x - _ptLast.x;
			if (_nLeftRight > 0 && i < 0 || _nLeftRight < 0 && i > 0)
				_nLeftRight = 0;
			_nLeftRight += i;
			_ptLast = point;
			Graphics2D graphics2d = (Graphics2D) getGraphics();
			if (!DragSource.isDragImageSupported()) {
				paintImmediately(_raGhost.getBounds());
				_raGhost.setRect(point.x - _ptOffset.x, point.y - _ptOffset.y, _imgGhost.getWidth(),
						_imgGhost.getHeight());
				graphics2d.drawImage(_imgGhost, AffineTransform.getTranslateInstance(_raGhost.getX(), _raGhost.getY()),
						null);
			} else {
				paintImmediately(_raCueLine.getBounds());
			}
			TreePath treepath = getClosestPathForLocation(point.x, point.y);
			if (treepath != _pathLast) {
				_nLeftRight = 0;
				_pathLast = treepath;
			}
			Rectangle rectangle = getPathBounds(treepath);
			_raCueLine.setRect(0.0D, rectangle.y + (int) rectangle.getHeight(), getWidth(), 2D);
			graphics2d.setColor(_colorCueLine);
			graphics2d.fill(_raCueLine);
			if (_nLeftRight > 20) {
				graphics2d.drawImage(_imgRight,
						AffineTransform.getTranslateInstance(point.x - _ptOffset.x, point.y - _ptOffset.y), null);
				_nShift = 1;
			} else if (_nLeftRight < -20) {
				graphics2d.drawImage(_imgLeft,
						AffineTransform.getTranslateInstance(point.x - _ptOffset.x, point.y - _ptOffset.y), null);
				_nShift = -1;
			} else {
				_nShift = 0;
			}
			_raGhost = _raGhost.createUnion(_raCueLine);
		}

		public void dropActionChanged(DropTargetDragEvent droptargetdragevent) {
			if (!isDragAcceptable(droptargetdragevent))
				droptargetdragevent.rejectDrag();
			else
				droptargetdragevent.acceptDrag(droptargetdragevent.getDropAction());
		}

		public void drop(DropTargetDropEvent droptargetdropevent) {
			if (drag) {
				if (!isDropAcceptable(droptargetdropevent)) {
					droptargetdropevent.rejectDrop();
					return;
				}
				droptargetdropevent.acceptDrop(droptargetdropevent.getDropAction());
				Transferable transferable = droptargetdropevent.getTransferable();
				DataFlavor adataflavor[] = transferable.getTransferDataFlavors();
				for (int i = 0; i < adataflavor.length; i++) {
					DataFlavor dataflavor = adataflavor[i];
					if (dataflavor.isMimeTypeEqual("application/x-java-jvm-local-objectref"))
						try {
							Point point = droptargetdropevent.getLocation();
							TreePath treepath = getClosestPathForLocation(point.x, point.y);
							if (treepath == null)
								return;
							Object o = transferable.getTransferData(dataflavor);
							BEContext.handler.drop(treepath, o);
						} catch (UnsupportedFlavorException unsupportedflavorexception) {
							droptargetdropevent.dropComplete(false);
							return;
						} catch (IOException ioexception) {
							droptargetdropevent.dropComplete(false);
							return;
						}
				}

				droptargetdropevent.dropComplete(true);
			}
		}

		public boolean isDragAcceptable(DropTargetDragEvent droptargetdragevent) {
			if ((droptargetdragevent.getDropAction() & 0x3) == 0)
				return false;
			return droptargetdragevent.isDataFlavorSupported(CTransferableTreePath.TREEPATH_FLAVOR);
		}

		public boolean isDropAcceptable(DropTargetDropEvent droptargetdropevent) {
			if ((droptargetdropevent.getDropAction() & 0x3) == 0)
				return false;
			return droptargetdropevent.isDataFlavorSupported(CTransferableTreePath.TREEPATH_FLAVOR);
		}
	}
}
