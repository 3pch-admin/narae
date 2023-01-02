/**
 * @(#) FileUtil.java Copyright (c) jerred. All rights reserverd
 * @version 1.00
 * @since jdk 1.4.02
 * @createdate 2004. 3. 22.
 * @author Cho Sung Ok, jerred@bcline.com
 * @desc
 */

package ext.narae.util;

import java.io.File;
import java.text.DecimalFormat;

/**
 * 
 */
public class FileUtil {
	public static void checkDir(String dir) {
		File createDir = new File(dir);
		if (!createDir.exists())
			createDir.mkdir();
		if (!createDir.exists())
			createDir.mkdirs();
	}

	public static void checkDir(File dir) {
		if (!dir.exists())
			dir.mkdir();
		if (!dir.exists())
			dir.mkdirs();
	}

	public static String getFileSizeStr(long filesize) {
		DecimalFormat df = new DecimalFormat(".#");
		String fSize = "";
		if ((filesize > 1024) && (filesize < 1024 * 1024)) {
			fSize = df.format((float) filesize / 1024).toString() + " KB";
		} else if (filesize >= 1024 * 1024) {
			fSize = df.format((float) filesize / (1024 * 1024)).toString() + " MB";
		} else if (filesize < 1024 && filesize > 1) {
			fSize = "1 KB";
		} else {
			fSize = "0 Bytes";
		}
		return fSize;
	}

}
