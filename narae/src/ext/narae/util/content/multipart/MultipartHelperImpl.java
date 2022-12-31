/**
 * @(#) FileRenameCommandImpl.java Copyright (c) jerred. All rights reserverd
 * @version 1.00
 * @since jdk 1.4.02
 * @createdate 2004. 3. 3.
 * @author Cho Sung Ok, jerred@bcline.com
 * @desc
 */

package ext.narae.util.content.multipart;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

import ext.narae.util.CommonUtil;
import ext.narae.util.jdf.config.ConfigImpl;

/**
 * 이 소스는 까오기 보드(http://www.kkaok.pe.kr/)의 소스를 참고로 만들어 졌음을 밝힙니다.
 */
public class MultipartHelperImpl implements MultipartHelper {
	MultipartRequest multi = null;
	private Hashtable paramsFile = new Hashtable(); // name - UploadFile
	private Hashtable params = new Hashtable(); // Parameter : Value

	public MultipartHelperImpl(HttpServletRequest _request) {
		this(_request, ConfigImpl.getInstance().getString("upload.temp.dir", CommonUtil.getTempDir()),
				ConfigImpl.getInstance().getInt("upload.file.maxsize", 52428800));
	}

	public MultipartHelperImpl(HttpServletRequest request, String saveDir, int maxUploadSize) {
		// String encoding = "MS949";
		/**
		 * 첨부화일 업로드
		 */
		try {
			multi = new MultipartRequest(request, saveDir, maxUploadSize, "MS949", new DefaultFileRenamePolicy());

			// 파라미터를 Hashtable로 저정한다.
			setParams(multi);

			/**
			 * form값 처리
			 */
			String fileParam = "";
			String original = "";
			String rename = "";
			File fSourceFile = null;

			/**
			 * 파일이름들만 Enumeration 에 담는다.
			 */
			Enumeration files = multi.getFileNames();
			while (files.hasMoreElements()) {

				fileParam = (String) files.nextElement();
				original = multi.getOriginalFileName(fileParam);
				if (original == null)
					continue;
				System.out.println("fileParam =======" + fileParam);
				rename = multi.getFilesystemName(fileParam);
				fSourceFile = new File(saveDir + File.separator + original);
				paramsFile.put(fileParam, new UploadFile(saveDir, original, rename, fSourceFile.length(), fileParam));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setParams(MultipartRequest _mulit) {
		Enumeration eu = _mulit.getParameterNames();
		String key = null;
		String[] value = null;
		while (eu.hasMoreElements()) {
			key = (String) eu.nextElement();

			value = _mulit.getParameterValues(key);
			if (value == null) {
				params.put(key, "");
			} else if (value.length == 1) {
				params.put(key, value[0]);
			} else {
				params.put(key, value);
			}
		}
	}

	public Hashtable getParams() {
		return params;
	}

	public String getParameter(String name) {
		return multi.getParameter(name);
	}

	public String[] getParameterValues(String name) {
		return multi.getParameterValues(name);
	}

	/**
	 * 파라미터명이 _paramName인 UploadFile 반환
	 */
	public UploadFile getFile(String _paramName) {
		try {
			UploadFile file = (UploadFile) paramsFile.get(_paramName);
			return file; // may be null
		} catch (Exception e) {
			return null;
		}
	}

	public Hashtable getFileHash() {
		return paramsFile;
	}

	public Vector getFileList() {
		Vector list = new Vector();
		Iterator it = paramsFile.values().iterator();
		while (it.hasNext()) {
			Object obj = it.next();
			if (obj != null)
				list.addElement(obj);
		}
		return list;
	}
}
/*
 * $Log: MultipartHelperImpl.java,v $ Revision 1.2 2011/05/23 05:51:10 thhwang
 *
 * Committed on the Free edition of March Hare Software CVSNT Server. Upgrade to
 * CVS Suite for more features and support: http://march-hare.com/cvsnt/
 *
 * Revision 1.1 2011/04/21 03:51:01 thhwang *** empty log message ***
 *
 * Revision 1.1 2011/03/16 23:49:20 hyun
 *
 * Committed on the Free edition of March Hare Software CVSNT Server. Upgrade to
 * CVS Suite for more features and support: http://march-hare.com/cvsnt/
 *
 * Revision 1.3 2008/02/19 10:04:27 hyun 협력업체 도면발송
 *
 * Revision 1.2 2007/11/22 07:21:26 hjkim *** empty log message ***
 *
 * Revision 1.1 2007/07/11 11:34:15 plmadmin *** empty log message ***
 *
 * Revision 1.1 2007/07/11 10:26:14 plmadmin *** empty log message ***
 *
 * Revision 1.1 2007/07/09 08:59:34 plmadmin *** empty log message ***
 *
 * Revision 1.4 2006/06/16 05:41:26 shchoi file이 null 경우는 UploadFile 추가하지 않음
 *
 * Revision 1.3 2006/06/16 04:46:41 shchoi Exception을 내부에서 처리하게 변경 /* Revision
 * 1.2 2006/06/15 06:36:09 shchoi /* 파라미터 및 파일 처리 관련 기능 추가 /*
 */
