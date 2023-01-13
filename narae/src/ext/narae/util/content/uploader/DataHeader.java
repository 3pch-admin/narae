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

import java.io.Serializable;

public class DataHeader implements Serializable {
	private boolean file = false;
	private String fieldName = null;
	private String fullPathName = null;
	private String fileName = null;
	private String fileExtension = null;
	private String contentType = null;
	private String contentDisposition = null;
	private String mimeType = null;
	private String mimeSubType = null;

	public DataHeader() {
	}

	/**
	 * @roseuid 39C1BF690069
	 */
	public DataHeader(String headerString) {
		this.file = headerString.indexOf("filename") > 0;

		// Form Field의 이름을 얻어온다.
		int startPosition = headerString.indexOf("name=\"")+ "name=\"".length();
		int endPosition = headerString.indexOf("\"", startPosition);
		if ((startPosition > 0) && (endPosition > 0)) {
			this.fieldName = headerString.substring(startPosition, endPosition);
		}

		if (isFile()) {
			// Upload될 파일의 Full Path 명을 얻어온다.
			startPosition = headerString.indexOf("filename=\"") + "filename=\"".length();
			endPosition = headerString.indexOf("\"", startPosition);
			if ((startPosition > 0) && (endPosition > 0)) {
				this.fullPathName = headerString.substring(startPosition,endPosition);
			}

			// Content 타입을 얻어온다.
			startPosition = headerString.indexOf("Content-Type: ") + "Content-Type: ".length();
			if (startPosition > 0) {
				this.contentType = headerString.substring(startPosition);
			}

			// Content disposition을 얻어온다.
			startPosition = headerString.indexOf("Content-Disposition: ") + "Content-Disposition: ".length();
			endPosition = headerString.indexOf(";", startPosition);
			this.contentDisposition = headerString.substring(startPosition,endPosition);

			// file 명을 얻어온다.
			if ((startPosition = this.fullPathName.lastIndexOf(47)) > 0) {
				this.fileName = this.fullPathName.substring(startPosition + 1);
			} else if ((startPosition = this.fullPathName.lastIndexOf(92)) > 0) {
				this.fileName = this.fullPathName.substring(startPosition + 1);
			} else {
				this.fileName = this.fullPathName;
			}

			// file의 확장자를 얻는다.
			if ((startPosition = this.fileName.lastIndexOf(46)) > 0) {
				this.fileExtension = this.fileName.substring(startPosition + 1);
			} else {
				this.fileExtension = "";
			}

			// MIME 타입과 MIME 서브 타입을 얻는다.
			if ((startPosition = this.contentType.indexOf("/")) > 0) {
				this.mimeType = this.contentType.substring(0, startPosition);
				this.mimeSubType = this.contentType
						.substring(startPosition + 1);
			} else {
				this.mimeType = this.contentType;
				this.mimeSubType = this.contentType;
			}
		}
	}

	/**
	 * @roseuid 39C1BF8E0370
	 */
	public boolean isFile() {
		return this.file;
	}

	/**
	 * @roseuid 39C1BF9C0185
	 */
	public String getFieldName() {
		return this.fieldName;
	}

	/**
	 * @roseuid 39C1BFAB02C7
	 */
	public String getFullPathName() {
		return this.fullPathName;
	}

	/**
	 * @roseuid 39C1BFDB033E
	 */
	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String name) {
		this.fileName = name;
	}

	/**
	 * @roseuid 39C1BFE703BE
	 */
	public String getFileExtension() {
		return this.fileExtension;
	}

	/**
	 * @roseuid 39C1BFFB02AE
	 */
	public String getContentType() {
		return this.contentType;
	}

	/**
	 * @roseuid 39C1C00E02C0
	 */
	public String getContentDisposition() {
		return this.contentDisposition;
	}

	/**
	 * @roseuid 39C1C0280263
	 */
	public String getMimeType() {
		return this.mimeType;
	}

	/**
	 * @roseuid 39C1C0350121
	 */
	public String getMimeSubType() {
		return this.mimeSubType;
	}
}
