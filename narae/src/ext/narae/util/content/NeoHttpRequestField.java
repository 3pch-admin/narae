package ext.narae.util.content;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.fileupload.FileItem;

public class NeoHttpRequestField {

	FileItem _item = null;

	public NeoHttpRequestField(FileItem item) {
		_item = item;
	}

	public boolean IsFile() {
		return _item.getContentType() != null;
	}

	public String getContentType() {
		return _item.getContentType();
	}

	public String getFieldName() {
		return _item.getFieldName();
	}

	public long getSize() {
		return _item.getSize();
	}

	public String getValue() {
		return this.IsFile() ? "this is file data, only return type is InputStream" : _item.getString();
	}

	public InputStream getInputStream() {
		if (this.IsFile()) {
			try {
				return _item.getInputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	public String getFileName() {
		return this.IsFile() ? _item.getName() : "";
	}
}
