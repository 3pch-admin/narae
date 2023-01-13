/* $Header : $*/

/* bcwti
 *
 * Copyright (c) 2000 Webers. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Webers.
 * You shall not disclose such confidential information and
 * shall use it only in accordance with the terms of the license agreement
 *
 * ecwti
 */

package ext.narae.util.content.uploader;

import java.util.Vector;
import java.util.Hashtable;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

public class FileUploader {
	// 첨부 파일 저장 Attribute
	// files 와 fileTable로 두개의 Attr을 둔 이유는
	// 첨부파일의 formName 이 다른 경우를 구분하기 위해서..
	// files 는 formName 에 구분 없이 무조건 저장하고,
	// fileTable 는 formName,Vector 구조로 저장한다.
	private Vector files;
	private Hashtable fileTable;
	private Hashtable formParameters;

	/**
	 * @roseuid 39FCDCF80211
	 */
	protected FileUploader() {
		this.files = null;
		this.fileTable = new Hashtable();
		this.formParameters = new Hashtable();
	}

	/**
	 * @roseuid 39F79FB002E2
	 */
	public static FileUploader newFileUploader(ServletContext context, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			FileUploader fileUploader = new FileUploader();
			UploadUtility uploadUtil = new UploadUtility();
			return uploadUtil.fillContents(fileUploader, request);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return null;
		} catch (NoFileContentException nfcf) {
			nfcf.printStackTrace();
			return null;
		} catch (CannotReadFileException crfe) {
			crfe.printStackTrace();
			return null;
		}
	}

	/**
	 * @roseuid 39F79FB002E2
	 */
	public static FileUploader newFileUploader(HttpServletRequest request) {
		try {
			FileUploader fileUploader = new FileUploader();
			UploadUtility uploadUtil = new UploadUtility();
			return uploadUtil.fillContents(fileUploader, request);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return null;
		} catch (NoFileContentException nfcf) {
			nfcf.printStackTrace();
			return null;
		} catch (CannotReadFileException crfe) {
			crfe.printStackTrace();
			return null;
		}
	}

	/**
	 * @roseuid 39FCDD110130
	 */
	public void putFormParameter(String name, String value) {
		Vector parameters = (Vector) this.formParameters.get(name);

		if (parameters == null) {
			parameters = new Vector();
			parameters.addElement(value);
			this.formParameters.put(name, parameters);
		} else {
			parameters.addElement(value);
		}
	}

	/**
	 * @roseuid 39FCDD4B02F6
	 */
	public String getFormParameter(String name) {
		Vector parameters = (Vector) this.formParameters.get(name);
		if (parameters == null) {
			return null;
		}
		return (String) parameters.firstElement();
	}

	public String getNCFormParameter(String name) {
		Vector parameters = (Vector) this.formParameters.get(name);
		if (parameters == null)
			return "";
		return (String) parameters.firstElement();
	}

	public String[] getFormParameters(String name) {
		Vector parameters = (Vector) this.formParameters.get(name);
		String[] paramValues;
		if (parameters == null) {
			return null;
		}

		paramValues = new String[parameters.size()];
		for (int i = 0; i < parameters.size(); i++) {
			paramValues[i] = (String) parameters.elementAt(i);
		}
		return paramValues;
	}

	/**
	 * Access method for the files property.
	 * 
	 * @return the current value of the files property
	 * @roseuid
	 */
	public Vector getFiles() {
		return this.files;
	}

	public Vector getFiles(String key) {
		return (Vector) this.fileTable.get(key);
	}

	/**
	 * Sets the value of the files property.
	 * 
	 * @param aFiles the new value of the files property
	 * @roseuid
	 */
	public void setFiles(Vector aFiles) {
		files = aFiles;
	}

	public void setFiles(Hashtable _table) {
		this.fileTable = _table;
	}
}
