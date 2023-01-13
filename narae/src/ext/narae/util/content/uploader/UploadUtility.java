package ext.narae.util.content.uploader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.Date;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import wt.util.WTProperties;

/**
 * Utility Class
 */
public class UploadUtility {
	public InputStream theInputStream;

	public static int fileSeq = 0;

	boolean notFinished = true;

	String tempdir = null;

	public UploadUtility() {
		try {
			WTProperties wtproperties = WTProperties.getLocalProperties();
			tempdir = wtproperties.getProperty("wt.temp", "c:\\tmp");
		} catch (IOException ioe) {
		}
	}

	/**
	 * @roseuid 39FCDAE400B7
	 */
	public FileUploader fillContents(FileUploader uploader, HttpServletRequest request)
			throws IOException, CannotReadFileException, NoFileContentException {
		int contentLength = request.getContentLength();
		InputStream is = request.getInputStream();
		return fillContents(uploader, is, contentLength);
	}

	public FileUploader fillContents(FileUploader uploader, InputStream is, int contentLength)
			throws CannotReadFileException, NoFileContentException {
		Vector fileList = new Vector();
		Hashtable fileTable = new Hashtable();
		notFinished = true;
		is = new PushbackInputStream(is, 100);

		//
		// content stream 의 내용을 byte 배열로 변환함.
		//

		//
		// 파일객체를 생성하고 내용을 채운다.
		//
		try {
			String boundary = null;
			while (notFinished) {
				// 바운더리를 읽는다.
				boundary = getBoundary(is);
				if ((boundary == null) || (boundary.length() == 0)) {
					return null;
				}
				if (!notFinished)
					break;
				if (boundary.charAt(boundary.length() - 1) == '-')
					break;

				String headerString = getHeaderData(is);
				DataHeader dataHeader = new DataHeader(headerString);

				if (dataHeader.isFile()) {
					String contStr = getHeaderData(is);
					headerString += "\n\r" + contStr;
					dataHeader = new DataHeader(headerString);
				}

				for (int i = 0; i < 2; i++)
					is.read();

				WBFile wFile = null;
				wFile = new WBFile(dataHeader);
				if (!dataHeader.isFile()) {
					String strData = getStrData(boundary, is);
					strData = strData.trim();
					// System.out.print(headerString);
					// System.out.println("strData = -" +strData +"!-");
					uploader.putFormParameter(dataHeader.getFieldName(), strData);
					continue;
				} else {
					String fileFormName = dataHeader.getFieldName();
					Vector formFileVec = null;
					if (fileTable.containsKey(fileFormName)) {
						formFileVec = (Vector) (fileTable.get(fileFormName));
					} else {
						formFileVec = new Vector();
					}
					// get content
					String filename = wFile.getName();
					// System.out.println("filename = " +filename );
					if (filename.length() < 1) {
						is.read();
					} else {
						filename = filename + "_" + (new Date()).getTime();
						File newFile = createFile(boundary, filename, is, contentLength);

						wFile.setFile(newFile);
						wFile.setSize((int) newFile.length());
						fileList.addElement(wFile);
						formFileVec.addElement(wFile);
					}
					fileTable.put(fileFormName, formFileVec);
				}
			} // end of While
		} catch (Exception e) {
			e.printStackTrace();
		}
		uploader.setFiles(fileList);
		uploader.setFiles(fileTable);
		return uploader;
	}

	/**
	 * @roseuid 3A761463009B
	 */
	public String htmlEsc2String(String escString) {
		String convertString = new String();
		StringTokenizer st = new StringTokenizer(escString, "&;");
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (token.startsWith("#")) {
				convertString += (char) Integer.parseInt(token.substring(2));
			} else {
				convertString += token;
			}
		}
		return convertString;
	}

	private String getBoundary(InputStream is) throws IOException {
		java.io.ByteArrayOutputStream outStr = new java.io.ByteArrayOutputStream();
		int b;
		while (true) {
			b = is.read();
			if (b == -1) {
				notFinished = false;
				break;
			}
			if (b == 13) {
				is.read();
				break;
			}
			outStr.write(b);
		}

		outStr.flush();
		byte[] retVal = outStr.toByteArray();
		return new String(retVal);
	}

	private String getHeaderData(InputStream is) throws IOException {
		java.io.ByteArrayOutputStream outStr = new java.io.ByteArrayOutputStream();
		int b;
		while (true) {
			b = is.read();
			// System.out.print((char)b);
			if (b == -1) {
				notFinished = false;
				return null;
				// break;
			}
			if (b == 13) {
				is.read();
				break;
			}
			outStr.write(b);
		}

		outStr.flush();
		byte[] retVal = outStr.toByteArray();

		String dataHeaderStr = new String(retVal, "KSC5601");
		return dataHeaderStr;
	}

	private String getStrData(String boundary, InputStream is) throws IOException {
		int maxBufferSize = 1000 * 1024;
		String returnString = "";

		int b;
		boolean binaryEndFound = false;
		int boundaryKeyPosition = 0;
		byte[] bArr = new byte[boundary.length()];
		int totalLen = 0;
		byte[] block = new byte[maxBufferSize + boundary.length()];
		int curBlockSize = 0;
		while (!binaryEndFound) {
			b = is.read();

			bArr[boundaryKeyPosition] = (byte) b;
			if ((byte) b == boundary.charAt(boundaryKeyPosition)) {

				if (boundaryKeyPosition == (boundary.length() - 1)) {
					binaryEndFound = true;
					// System.out.println("curBlockSize =" + curBlockSize);
					// if( curBlockSize>2 ) {
					// curBlockSize -= 2;
					returnString += (new String(block, 0, curBlockSize));
					// }

					((PushbackInputStream) is).unread(bArr);
				} else
					boundaryKeyPosition++;
			} else {
				System.arraycopy(bArr, 0, block, curBlockSize, boundaryKeyPosition + 1);
				curBlockSize += (boundaryKeyPosition + 1);
				if (curBlockSize > maxBufferSize) {
					returnString += (new String(block, 0, curBlockSize));
					curBlockSize = 0;
				}

				totalLen += (boundaryKeyPosition + 1);
				boundaryKeyPosition = 0;
			}
		}
		// System.out.println("totalLen =" + totalLen);

		return returnString;
	}

	private File createFile(String boundary, String fileName, InputStream is, int cotentLength) throws IOException {
		// System.out.println("Boundary Length : " + boundary.length());
		String uploadPath = tempdir;

		if (boundary.startsWith("\n")) {
			boundary = boundary.substring(1);
		}
		// boundary = "\r"+ boundary;
		int maxBufferSize = 1000 * 1024;

		File outputFile = new File(uploadPath + System.getProperty("file.separator") + fileName);
		FileOutputStream out = new FileOutputStream(outputFile);

		int b;
		boolean binaryEndFound = false;
		int boundaryKeyPosition = 0;
		byte[] bArr = new byte[boundary.length()];
		int totalLen = 0;
		byte[] block = new byte[maxBufferSize + boundary.length()];
		int curBlockSize = 0;
		while (!binaryEndFound) {
			b = is.read();
			// System.out.print(b);

			bArr[boundaryKeyPosition] = (byte) b;
			if ((byte) b == boundary.charAt(boundaryKeyPosition)) {

				if (boundaryKeyPosition == boundary.length() - 1) {
					binaryEndFound = true;
					// System.out.println("curBlockSize =" + curBlockSize);
					if (curBlockSize > 2) {
						curBlockSize -= 2;
						out.write(block, 0, curBlockSize);
					}

					((PushbackInputStream) is).unread(bArr);
				} else
					boundaryKeyPosition++;
			} else {
				System.arraycopy(bArr, 0, block, curBlockSize, boundaryKeyPosition + 1);
				curBlockSize += (boundaryKeyPosition + 1);
				if (curBlockSize > maxBufferSize) {
					out.write(block, 0, curBlockSize);
					curBlockSize = 0;
					if (totalLen > cotentLength) {
						out.close();
						outputFile.delete();
						return null;
					}
				}

				totalLen += (boundaryKeyPosition + 1);
				boundaryKeyPosition = 0;
			}
		}
		// System.out.println("totalLen =" + totalLen);
		out.close();
		return outputFile;
	}
}
