package ext.narae.service.change.editor;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Properties;

public class HttpMessage {

	URL servlet;
	String args;

	public HttpMessage(URL url) {
		servlet = null;
		args = null;
		servlet = url;
	}

	public InputStream sendGetMessage() throws IOException {
		return sendGetMessage(null);
	}

	public InputStream sendGetMessage(Properties properties) throws IOException {
		String s = "";
		if (properties != null)
			s = "?" + toEncodedString(properties);
		URL url = new URL(servlet.toExternalForm() + s);
		BEContext.println(url.toString());
		URLConnection urlconnection = url.openConnection();
		urlconnection.setUseCaches(false);
		return urlconnection.getInputStream();
	}

	public InputStream sendPostMessage() throws IOException {
		return sendPostMessage(null);
	}

	public InputStream sendPostMessage(Properties properties) throws IOException {
		String s = "";
		if (properties != null)
			s = toEncodedString(properties);
		URLConnection urlconnection = servlet.openConnection();
		urlconnection.setDoInput(true);
		urlconnection.setDoOutput(true);
		urlconnection.setUseCaches(false);
		DataOutputStream dataoutputstream = new DataOutputStream(urlconnection.getOutputStream());
		dataoutputstream.writeBytes(s);
		BEContext.println(s);
		dataoutputstream.flush();
		BEContext.println("ok");
		dataoutputstream.close();
		return urlconnection.getInputStream();
	}

	private String toEncodedString(Properties properties) {
		StringBuffer stringbuffer = new StringBuffer();
		for (Enumeration enumeration = properties.propertyNames(); enumeration.hasMoreElements();) {
			String s = (String) enumeration.nextElement();
			String s1 = properties.getProperty(s);
			stringbuffer.append(URLEncoder.encode(s) + "=" + URLEncoder.encode(s1));
			if (enumeration.hasMoreElements())
				stringbuffer.append("&");
		}
		return stringbuffer.toString();
	}
}
