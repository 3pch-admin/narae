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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.FilenameUtils;

public class WBFile implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5836296113860572378L;
	private int size = 0;
	private byte[] contents;
	private DataHeader dataHeader;
	private File file = null;
	private DiskFileItem item;
	private String contentType;
	private String fieldName;
	private String fullPathName;
	private String name;

	public WBFile() {
	}

	public WBFile(String name, int size, byte[] contents) {
		dataHeader = new DataHeader();
		setName(name);
		setSize(size);
		setContents(contents);
	}

	public WBFile(String name, int size, File file) {
		dataHeader = new DataHeader();
		setName(name);
		setSize(size);
		setFile(file);
	}

	public static WBFile newWBFile(DiskFileItem item) {
		WBFile wbfile = new WBFile();
		wbfile.setItem(item);
		wbfile.setContentType(item.getContentType());
		wbfile.setFieldName(item.getFieldName());
		wbfile.setFile(item.getStoreLocation());
		wbfile.setFullPathName(item.getName());
		wbfile.setName(FilenameUtils.getName(item.getName()));
		wbfile.setSize((int) item.getStoreLocation().length());
		return wbfile;
	}

	/**
	 * @roseuid 39C6E8850262
	 */
	public WBFile(DataHeader dataHeader) {
		this.dataHeader = dataHeader;
	}

	/**
	 * @roseuid 39C6E85C0303
	 */
	public void setDataHeader(DataHeader dataHeader) {
		this.dataHeader = dataHeader;
	}

	/**
	 * @roseuid 39C1814400BE
	 */
	public int getSize() {
		return this.size;
	}

	/**
	 * @roseuid 39C1814400BF
	 */
	public void setSize(int size) {
		this.size = size;
	}

	public void setFile(File file) {
		/***************************************************************
		 * DRM decrypted for FormUploader SJ,Han 2008-06-05
		 ***************************************************************/
		File drmFile = file;
		// if(false){

		// drmFile = E3PSDRMHelper.upload(file,
		// FilenameUtils.getName(file.getAbsolutePath()));
		// }
		this.file = drmFile;
		/***************************************************************
		 * DRM decrypted for FormUploader SJ,Han 2008-06-05
		 ***************************************************************/
	}

	public File getFile() {
		return this.file;
	}

	/**
	 * @roseuid 39C1814400CA
	 */
	public void setContents(byte[] contents) {
		/***************************************************************
		 * DRM decrypted for FormUploader SJ,Han 2008-06-05
		 ***************************************************************/
		this.contents = contents;

		// this.contents = E3PSDRMHelper.upload(contents, getName());
		/***************************************************************
		 * DRM decrypted for FormUploader SJ,Han 2008-06-05
		 ***************************************************************/
//		this.contents = contents;
	}

	/**
	 * @roseuid 39C1814400CC
	 */
	public void setContentByte(int index, byte content) {
		this.contents[index] = content;
	}

	/**
	 * @roseuid 39C1814400B8
	 */
	public String getName() {
		if (dataHeader != null) {
			return this.dataHeader.getFileName();
		} else {
			return this.name;
		}
	}

	public void setName(String name) {
		if (dataHeader != null) {
			this.dataHeader.setFileName(name);
		} else {
			this.name = name;
		}
	}

	public InputStream getInputStream() {
		if (this.contents != null) {
			return (new ByteArrayInputStream(this.contents));
		}
		if (this.file != null) {
			try {
				return (new FileInputStream(this.file));
			} catch (FileNotFoundException fnfe) {
				return null;
			}
		}
		return null;
	}

	/**
	 * @roseuid 39C1814400BB
	 */
	public String getFullPathName() {
		if (dataHeader != null) {
			return this.dataHeader.getFullPathName();
		} else {
			return this.fullPathName;
		}
	}

	/**
	 * @roseuid 39C1814400C1
	 */
	public String getContentType() {
		if (dataHeader != null) {
			return this.dataHeader.getContentType();
		} else {
			return this.contentType;
		}
	}

	/**
	 * @roseuid 39C6D7E402BC
	 */
	public String getContentDisposition() {
		return this.dataHeader.getContentDisposition();
	}

	/**
	 * @roseuid 39C1814400C4
	 */
	public String getMimeType() {
		return this.dataHeader.getMimeType();
	}

	/**
	 * @roseuid 39C6D789009F
	 */
	public String getMimeSubType() {
		return this.dataHeader.getMimeSubType();
	}

	/**
	 * @roseuid 39C957A80085
	 */
	public String getFieldName() {
		if (dataHeader != null) {
			return this.dataHeader.getFieldName();
		} else {
			return this.fieldName;
		}
	}

	/**
	 * @roseuid 39FE854E0062
	 */
	public void readExternal(ObjectInput arg0) throws IOException, ClassNotFoundException {
	}

	/**
	 * @roseuid 39FE854E00E4
	 */
	public void writeExternal(ObjectOutput arg0) throws IOException {
	}

	protected void finalize() {
		this.file.delete();
	}

	public byte[] getContents() {
		return contents;
	}

	public DiskFileItem getItem() {
		return item;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public void setItem(DiskFileItem item) {
		this.item = item;
	}

	public void setFullPathName(String fullPathName) {
		this.fullPathName = fullPathName;
	}

	// Returns the contents of the file in a byte array.
	public static byte[] getBytesFromFile(File file) {

		// Get the size of the file
		long length = file.length();

		// You cannot create an array using a long type.
		// It needs to be an int type.
		// Before converting to an int type, check
		// to ensure that file is not larger than Integer.MAX_VALUE.
		if (length > Integer.MAX_VALUE) {
			// File is too large
		}

		// Create the byte array to hold the data
		byte[] bytes = new byte[(int) length];

		try {
			InputStream is = new FileInputStream(file);
			// Read in the bytes
			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
				offset += numRead;
			}

			// Ensure all the bytes have been read in
			if (offset < bytes.length) {
				throw new IOException("Could not completely read file " + file.getName());
			}

			// Close the input stream and return bytes
			is.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return bytes;
	}
}
