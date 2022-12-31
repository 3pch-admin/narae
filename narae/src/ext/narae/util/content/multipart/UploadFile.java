/**
 * @(#) UploadFile.java Copyright (c) jerred. All rights reserverd
 * @version 1.00
 * @since jdk 1.4.02
 * @createdate 2004. 3. 3.
 * @author Cho Sung Ok, jerred@bcline.com
 * @desc
 */

package ext.narae.util.content.multipart;

import java.io.File;
import java.io.Serializable;

/**
 * 이 소스는 까오기 보드(http://www.kkaok.pe.kr/)의 소스를 참고로 만들어 졌음을 밝힙니다.
 */
public class UploadFile implements Serializable {
	private String saveDir;
	private String rename;
	private String originalName;
	private String fieldName; // input type=file 의 name값
	private long fileSize;

	public UploadFile(String saveDir, String original, String rename, long fileSize) {
		this(saveDir, original, rename, fileSize, null);
	}

	public UploadFile(String _saveDir, String _original, String _rename, long _fileSize, String _fieldName) {
		saveDir = _saveDir;
		originalName = _original;
		rename = _rename;
		fileSize = _fileSize;
		fieldName = _fieldName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getSaveDir() {
		return saveDir;
	}

	public String getRenameFileName() {
		return rename;
	}

	public String getOriginalFileName() {
		return originalName;
	}

	public long getFileSize() {
		return fileSize;
	}

	public File getFile() {
		if (saveDir == null || rename == null)
			return null;
		else
			return new File(saveDir + File.separator + rename);
	}
}

/*
 * $Log: UploadFile.java,v $ /* Revision 1.2 2011/05/23 05:51:10 thhwang /* /*
 * Committed on the Free edition of March Hare Software CVSNT Server. /* Upgrade
 * to CVS Suite for more features and support: /* http://march-hare.com/cvsnt/
 * /* /* Revision 1.1 2011/04/21 03:51:01 thhwang /* *** empty log message ***
 * /* /* Revision 1.1 2011/03/16 23:49:20 hyun /* /* Committed on the Free
 * edition of March Hare Software CVSNT Server. /* Upgrade to CVS Suite for more
 * features and support: /* http://march-hare.com/cvsnt/ /* /* Revision 1.3
 * 2008/02/19 10:04:27 hyun /* 협력업체 도면발송 /* /* Revision 1.1 2007/07/11 11:34:15
 * plmadmin /* *** empty log message *** /* /* Revision 1.1 2007/07/11 10:26:15
 * plmadmin /* *** empty log message *** /* /* Revision 1.1 2007/07/09 08:59:34
 * plmadmin /* *** empty log message *** /* /* Revision 1.3 2006/08/11 08:33:13
 * shchoi /* *** empty log message *** /* /* Revision 1.2 2006/06/15 05:39:36
 * shchoi /* fieldname 추가 /*
 */
