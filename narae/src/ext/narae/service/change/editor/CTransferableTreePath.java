package ext.narae.service.change.editor;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.util.Arrays;

import javax.swing.tree.TreePath;

class CTransferableTreePath implements Transferable {
	public static final DataFlavor TREEPATH_FLAVOR = new DataFlavor("application/x-java-jvm-local-objectref",
			"TreePath");
	public static final DataFlavor STRING_FLAVOR = new DataFlavor("application/x-java-jvm-local-objectref", "String");
	private TreePath _path;
	private String _oid;
	private DataFlavor _flavors[];

	public CTransferableTreePath(TreePath treepath) {
		_flavors = (new DataFlavor[] { TREEPATH_FLAVOR });
		_path = treepath;
	}

	public CTransferableTreePath(String oid) {
		_flavors = (new DataFlavor[] { STRING_FLAVOR });
		_oid = oid;
	}

	public DataFlavor[] getTransferDataFlavors() {
		return _flavors;
	}

	public boolean isDataFlavorSupported(DataFlavor dataflavor) {
		return Arrays.asList(_flavors).contains(dataflavor);
	}

	public synchronized Object getTransferData(DataFlavor dataflavor) throws UnsupportedFlavorException {
		if (_path != null)
			return _path;
		else if (_oid != null)
			return _oid;
		else
			throw new UnsupportedFlavorException(dataflavor);
	}
}
