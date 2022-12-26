package ext.narae.component;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SerializableInputStream extends InputStream implements Serializable {
	static final long serialVersionUID = 3364193722688048342L;
	private byte[] data = null;

	protected byte buf[];
	protected int pos;
	protected int mark = 0;
	protected int count;

	public SerializableInputStream(InputStream ins) throws IOException {
		List byteList = new ArrayList();
		int dat = ins.read();
		while (dat != -1) {
			byteList.add(new Byte((byte)dat));
			dat = ins.read();
		}

		data = new byte[byteList.size()];
		int counter = 0;
		Iterator itr = byteList.iterator();
		while(itr.hasNext()) {
			data[counter++] = ((Byte)itr.next()).byteValue();
		}
		ins.close();
		this.buf = this.data;
		this.pos = 0;
		this.count = this.buf.length;
	}

	
	public synchronized int available() {
		return count - pos;
	}

	public void close() throws IOException {
		System.err.println("close()");
	}

	
	public synchronized void reset() throws IOException {
		System.err.println("reset()");
	}

	public boolean markSupported() {
		System.err.println("markSupported()");
		return false;
	}

	public synchronized void mark(int readlimit) {
		System.err.println("mark(int readlimit)");
	}

	public long skip(long n) throws IOException {
		System.err.println("skip(long n)");
		return 0;
	}

	public int read(byte b[]) throws IOException {
		System.err.println("read(byte b[])");
		return read(b, 0, data.length);
	}

	public synchronized int read(byte b[], int off, int len) {
		if (b == null) {
			throw new NullPointerException();
		} else if ((off < 0) || (off > b.length) || (len < 0) || ((off + len) > b.length) || ((off + len) < 0)) {
			throw new IndexOutOfBoundsException();
		}
		
		if (pos >= count) {
			return -1;
		}
		
		if (pos + len > count) {
			len = count - pos;
		}
		
		if (len <= 0) {
			return 0;
		}
		
		System.arraycopy(buf, pos, b, off, len);
		pos += len;
		return len;
	}
	
	@Override
	public synchronized int read() {
		return (pos < count) ? (buf[pos++] & 0xff) : -1;
	}

}
