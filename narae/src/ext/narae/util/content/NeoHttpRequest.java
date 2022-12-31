package ext.narae.util.content;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import ext.narae.util.DateUtil;

public class NeoHttpRequest {

	private HttpServletRequest _request = null;
	private File _uploadDir = new File("D:\\temp");
	private List<FileItem> _items = null;
	private List<NeoHttpRequestField> _fields = null;
	private long _fileLimit = 20 * 1024 * 1024; // 업로드 가능한 파일의 용량은 기본 5메가
	private long _requestLimit = _fileLimit * 10; // 한번에 업로드 용량은 기본 100메가

	public NeoHttpRequest(HttpServletRequest request) {

		// _request = request;
		_fields = new ArrayList<NeoHttpRequestField>();
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if (!isMultipart) {
			// 일반 폼 정보 전달

		} else {
			// multipart/form-data 형식 전달
			File tmpDir = new File("D:\\temp");
			if (!tmpDir.isDirectory()) {
				tmpDir.mkdirs();
			}

			// 임시저장공간 생성
			DiskFileItemFactory factory = new DiskFileItemFactory();
			factory.setSizeThreshold(1024); // 메모리에 저장할 최대 size
			factory.setRepository(_uploadDir); // 임시 저장할 위치
			// 업로드 핸들러 생성
			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setSizeMax(_requestLimit); // Set overall request size constraint

			try {
				_items = upload.parseRequest(request);
			} catch (FileUploadException e) {
				System.out.println(e.getStackTrace());
			} finally {
				Date now = new Date();
				for (File file : tmpDir.listFiles()) {
					if (file.isFile()) {
						// 1일이 지난 파일은 삭제
//						if (now.compareTo(getFileDate(file.getAbsolutePath())) < 1) {
//
//						}
//						if (!DateUtil.isToday(getFileDate(file.getAbsolutePath()))) {
//							file.delete();
//						}
					}
				}
			}

			// 파일 분류
			for (Iterator itr = _items.iterator(); itr.hasNext();) {
				FileItem item = (FileItem) itr.next();
				System.out.println("fil=" + item);
				_fields.add(new NeoHttpRequestField(item));
//				InputStream stream = null;
//				try {
//					stream = item.getInputStream();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				_datamap.put(item.getFieldName(), stream );
			}
		}
	}

	private Date getFileDate(String path) {
		String datestr = "";
		Date date = null;
		DateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Process proc = Runtime.getRuntime().exec("cmd /c dir " + path + " /tc");
			BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			String data = "";
			// it's quite stupid but work
			for (int i = 0; i < 6; i++) {
				data = br.readLine();
			}
			// split by space
			StringTokenizer st = new StringTokenizer(data);
			datestr = st.nextToken().trim();// Get date
			date = sdFormat.parse(datestr);
		} catch (IOException e) {

		} catch (ParseException e) {

		}
		return date;
	}

	public List<NeoHttpRequestField> getFields() {
		return _fields;
	}

	static void ConsoleWrite(String msg) {
		System.out.println(" ==========================> " + msg);
	}
}
