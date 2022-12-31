/*
 * @(#) JExcelUtil.java  Create on 2005. 2. 21.
 * Copyright (c) e3ps. All rights reserverd
 */
package ext.narae.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import jxl.Cell;
import jxl.Workbook;
import jxl.read.biff.BiffException;

/**
 * 
 * @author Choi Seunghwan, skyprda@e3ps.com
 * @version 1.00, 2005. 2. 21.
 * @since 1.4
 */
public class JExcelUtil {
	public static Workbook getWorkbook(byte[] bytes) {
		Workbook wb = null;
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			wb = Workbook.getWorkbook(bais);
		} catch (BiffException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return wb;
	}

	public static Workbook getWorkbook(File file) {
		if (file == null)
			return null;
		try {
			if (!file.getName().endsWith(".xls"))
				return null;
		} catch (Exception e) {
			return null;
		}

		Workbook wb = null;
		try {
			wb = Workbook.getWorkbook(file);
		} catch (BiffException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return wb;
	}

	public static String getContent(Cell[] cell, int idx) {
		try {
			String val = cell[idx].getContents();
			if (val == null)
				return "";
			return val.trim();
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		return "";
	}

	/**
	 * Excel에 있는 날짜형태의 값을 Timestamp로 변환한다.
	 * 
	 * @param _cell
	 * @param _idx
	 * @return
	 */
	public static Timestamp getTimestamp(Cell[] _cell, int _idx) {
		try {
			String val = _cell[_idx].getContents();
			val = val == null ? "" : val.trim();
			SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy:HH-mm-ss", Locale.KOREA);
			return new Timestamp(format.parse(val + ":12-00-00").getTime());
		} catch (ArrayIndexOutOfBoundsException e) {
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean checkLine(Cell[] cell) {
		String value = null;
		try {
			value = cell[1].getContents().trim();
		} catch (ArrayIndexOutOfBoundsException e) {
			e.getMessage();
			return false;
		}
		if (value == null || value.length() == 0)
			return false;
		return true;
	}

	public static boolean checkLine(Cell[] cell, int line) {
		String value = null;
		try {
			value = cell[line].getContents().trim();
		} catch (ArrayIndexOutOfBoundsException e) {
			e.getMessage();
			return false;
		}
		if (value == null || value.length() == 0)
			return false;
		return true;
	}
}
/*
 * $Log: JExcelUtil.java,v $ /* Revision 1.2 2011/05/23 05:51:10 thhwang /* /*
 * Committed on the Free edition of March Hare Software CVSNT Server. /* Upgrade
 * to CVS Suite for more features and support: /* http://march-hare.com/cvsnt/
 * /* /* Revision 1.1 2011/04/21 03:50:57 thhwang /* *** empty log message ***
 * /* /* Revision 1.1 2011/03/08 01:50:09 thhwang /* 등록 /* Committed on the Free
 * edition of March Hare Software CVSNT Server. /* Upgrade to CVS Suite for more
 * features and support: /* http://march-hare.com/cvsnt/ /* /* Revision 1.2
 * 2010/06/07 02:52:52 hyun /* /* Committed on the Free edition of March Hare
 * Software CVSNT Server. /* Upgrade to CVS Suite for more features and support:
 * /* http://march-hare.com/cvsnt/ /* /* Revision 1.2 2009/09/03 00:43:24
 * administrator /* /* Committed on the Free edition of March Hare Software
 * CVSNT Server. /* Upgrade to CVS Suite for more features and support: /*
 * http://march-hare.com/cvsnt/ /* /* Revision 1.1 2008/03/06 04:36:33 hjkim /*
 * 1 /* /* Revision 1.4 2007/10/10 11:16:33 ljh /* *** empty log message *** /*
 * /* Revision 1.3 2007/08/29 12:01:48 hjkim /* *** empty log message *** /* /*
 * Revision 1.2 2007/08/28 02:51:05 ljh /* *** empty log message *** /* /*
 * Revision 1.1 2007/07/11 12:17:12 plmadmin /* *** empty log message *** /* /*
 * Revision 1.1 2007/07/11 10:26:15 plmadmin /* *** empty log message *** /* /*
 * Revision 1.1 2007/07/09 08:58:35 plmadmin /* *** empty log message *** /* /*
 * Revision 1.1 2006/05/09 02:35:06 shchoi /* *** empty log message *** /* /*
 * Revision 1.2 2006/03/10 04:53:22 shchoi /* getTimestamp 추가 /
 **/
