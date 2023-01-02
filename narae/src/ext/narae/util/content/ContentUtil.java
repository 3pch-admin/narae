package ext.narae.util.content;

import java.io.IOException;
import java.net.URL;
import java.util.Vector;

import com.ptc.wvs.server.util.PublishUtils;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.content.FormatContentHolder;
import wt.content.URLData;
import wt.fc.QueryResult;
import wt.httpgw.URLFactory;
import wt.representation.Representation;
import wt.util.FileUtil;
import wt.util.WTException;
import wt.util.WTProperties;

public class ContentUtil {
	/**
	 * Get PrimaryContent from FormatContentHolder
	 * 
	 * @param : FormatContentHolder
	 * @return : ContentInfo
	 * @since : 2005.08
	 */
	public static ContentInfo getPrimaryContent(FormatContentHolder holder) throws Exception {
		ContentInfo info = null;
		try {
			QueryResult result = ContentHelper.service.getContentsByRole(holder, ContentRoleType.PRIMARY);
			while (result.hasMoreElements()) {
				ContentItem item = (ContentItem) result.nextElement();
				info = getContentInfo(holder, item);
				// if ( holder instanceof WTObject ) {
				// info.setIconURLStr(E3PSContentHelper.service.getIconImgTag((WTObject)holder));
				// }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return info;
	}

	/**
	 * Get SecondaryContents from ContentHolder
	 * 
	 * @param : FormatContentHolder
	 * @return : Vector(ContentInfo)
	 * @since : 2005.08
	 */
	public static Vector getSecondaryContents(ContentHolder holder) {
		Vector returnVec = new Vector();
		try {
			QueryResult result = ContentHelper.service.getContentsByRole(holder, ContentRoleType.SECONDARY);
			while (result.hasMoreElements()) {
				ContentItem item = (ContentItem) result.nextElement();
				returnVec.add(getContentInfo(holder, item));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnVec;
	}

	public static ContentInfo getContentInfo(ContentHolder holder, ContentItem item) throws WTException {
		ContentInfo info = null;
		if (item instanceof URLData) {
			URLData url = (URLData) item;
			info = new ContentInfo();
			info.setType("URL");
			info.setContentOid(url.toString());
			info.setName(url.getUrlLocation());
			info.setIconURLStr(getContentIconStr(url));
			info.setDescription(url.getDescription());
		} else if (item instanceof ApplicationData) {
			ApplicationData file = (ApplicationData) item;
			info = new ContentInfo();
			info.setType("FILE");
			info.setContentOid(file.toString());
			info.setName(file.getFileName());
			info.setDescription(file.getDescription());
			String context = "";
			try {
				context = WTProperties.getLocalProperties().getProperty("wt.server.codebase");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			URL url = null;
			try {
				url = ContentHelper.getDownloadURL(holder, file);
				System.out.println("ContentUtil #################> " + url.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}

			info.setDownURL(url);
			info.setIconURLStr(getContentIconStr(file));
			info.setFileSize(file.getFileSize());
			info.setBusinessType(file.getBusinessType());
		}
		return info;
	}

	public static String getContentIconStr(ContentItem item) throws WTException {
		URLFactory urlFac = new URLFactory();
		String iconStr = "";
		if (item instanceof URLData) {
			iconStr = urlFac.getBaseURL().getPath() + "portal/icon/fileicon/link.gif";
		} else if (item instanceof ApplicationData) {
			ApplicationData data = (ApplicationData) item;

			String extStr = "";
			String tempFileName = data.getFileName();
			int dot = tempFileName.lastIndexOf(".");
			if (dot != -1)
				extStr = tempFileName.substring(dot + 1); // includes
															// "."

			if (extStr.equalsIgnoreCase("cc"))
				iconStr = urlFac.getBaseURL().getPath() + "portal/icon/fileicon/ed.gif";
			else if (extStr.equalsIgnoreCase("exe"))
				iconStr = urlFac.getBaseURL().getPath() + "portal/icon/fileicon/exe.gif";
			else if (extStr.equalsIgnoreCase("doc"))
				iconStr = urlFac.getBaseURL().getPath() + "portal/icon/fileicon/doc.gif";
			else if (extStr.equalsIgnoreCase("ppt"))
				iconStr = urlFac.getBaseURL().getPath() + "portal/icon/fileicon/ppt.gif";
			else if (extStr.equalsIgnoreCase("xls"))
				iconStr = urlFac.getBaseURL().getPath() + "portal/icon/fileicon/xls.gif";
			else if (extStr.equalsIgnoreCase("csv"))
				iconStr = urlFac.getBaseURL().getPath() + "portal/icon/fileicon/xls.gif";
			else if (extStr.equalsIgnoreCase("txt"))
				iconStr = urlFac.getBaseURL().getPath() + "portal/icon/fileicon/notepad.gif";
			else if (extStr.equalsIgnoreCase("mpp"))
				iconStr = urlFac.getBaseURL().getPath() + "portal/icon/fileicon/mpp.gif";
			else if (extStr.equalsIgnoreCase("pdf"))
				iconStr = urlFac.getBaseURL().getPath() + "portal/icon/fileicon/pdf.gif";
			else if (extStr.equalsIgnoreCase("tif"))
				iconStr = urlFac.getBaseURL().getPath() + "portal/icon/fileicon/tif.gif";
			else if (extStr.equalsIgnoreCase("gif"))
				iconStr = urlFac.getBaseURL().getPath() + "portal/icon/fileicon/gif.gif";
			else if (extStr.equalsIgnoreCase("jpg"))
				iconStr = urlFac.getBaseURL().getPath() + "portal/icon/fileicon/jpg.gif";
			else if (extStr.equalsIgnoreCase("ed"))
				iconStr = urlFac.getBaseURL().getPath() + "portal/icon/fileicon/ed.gif";
			else if (extStr.equalsIgnoreCase("zip"))
				iconStr = urlFac.getBaseURL().getPath() + "portal/icon/fileicon/zip.gif";
			else if (extStr.equalsIgnoreCase("tar"))
				iconStr = urlFac.getBaseURL().getPath() + "portal/icon/fileicon/zip.gif";
			else if (extStr.equalsIgnoreCase("rar"))
				iconStr = urlFac.getBaseURL().getPath() + "portal/icon/fileicon/zip.gif";
			else if (extStr.equalsIgnoreCase("jar"))
				iconStr = urlFac.getBaseURL().getPath() + "portal/icon/fileicon/zip.gif";
			else if (extStr.equalsIgnoreCase("igs"))
				iconStr = urlFac.getBaseURL().getPath() + "portal/icon/fileicon/epmall.gif";
			else if (extStr.equalsIgnoreCase("pcb"))
				iconStr = urlFac.getBaseURL().getPath() + "portal/icon/fileicon/epmall.gif";
			else if (extStr.equalsIgnoreCase("asc"))
				iconStr = urlFac.getBaseURL().getPath() + "portal/icon/fileicon/epmall.gif";
			else if (extStr.equalsIgnoreCase("dwg"))
				iconStr = urlFac.getBaseURL().getPath() + "portal/icon/fileicon/epmall.gif";
			else if (extStr.equalsIgnoreCase("dxf"))
				iconStr = urlFac.getBaseURL().getPath() + "portal/icon/fileicon/epmall.gif";
			else if (extStr.equalsIgnoreCase("sch"))
				iconStr = urlFac.getBaseURL().getPath() + "portal/icon/fileicon/epmall.gif";
			else if (extStr.equalsIgnoreCase("html"))
				iconStr = urlFac.getBaseURL().getPath() + "portal/icon/fileicon/htm.gif";
			else if (extStr.equalsIgnoreCase("htm"))
				iconStr = urlFac.getBaseURL().getPath() + "portal/icon/fileicon/htm.gif";
			else
				iconStr = urlFac.getBaseURL().getPath() + "portal/icon/fileicon/generic.gif";
		} else {
			return null;
		}
		iconStr = "<img src='" + iconStr + "' border=0>";
		return iconStr;
	}

	public static String[] getPDF(ContentHolder holder) throws Exception {
		String[] pdf = new String[8];

		Representation representation = PublishUtils.getRepresentation(holder);

		if (representation != null) {
			// QueryResult result = ContentHelper.service.getContentsByRole(representation,
			// ContentRoleType.ADDITIONAL_FILES);
			QueryResult result = ContentHelper.service.getContentsByRole(representation, ContentRoleType.SECONDARY);
			while (result.hasMoreElements()) {
				ContentItem item = (ContentItem) result.nextElement();
				if (item instanceof ApplicationData) {
					ApplicationData data = (ApplicationData) item;

					String ext = FileUtil.getExtension(data.getFileName());

					if (!ext.equalsIgnoreCase("pdf")) {
						continue;
					}

					// 0 = holder oid
					pdf[0] = representation.getPersistInfo().getObjectIdentifier().getStringValue();
					// 1 = app oid
					pdf[1] = data.getPersistInfo().getObjectIdentifier().getStringValue();
					// 2 = name
					pdf[2] = data.getFileName();
					// 3 = size
					pdf[3] = data.getFileSizeKB() + " KB";
					// 4 = icon
					pdf[4] = data.getFileName();
					// 5 = down url
					// pdf[5] = WCUtil.getDownloadURL(pdf[0], pdf[1]);
					pdf[5] = ContentHelper.getDownloadURL(representation, data, false, pdf[2]).toString();
					// 6 = file version
					pdf[6] = data.getFileVersion();
					// 7 = file category
					pdf[7] = data.getCategory();
				}
			}
		}
		return pdf;
	}

	public static String[] getDWG(ContentHolder holder) throws Exception {
		String[] dwg = new String[8];

		Representation representation = PublishUtils.getRepresentation(holder);

		if (representation != null) {
			QueryResult result = ContentHelper.service.getContentsByRole(representation, ContentRoleType.SECONDARY);
//			QueryResult result = ContentHelper.service.getContentsByRole(representation,
//					ContentRoleType.ADDITIONAL_FILES);
			while (result.hasMoreElements()) {
				ContentItem item = (ContentItem) result.nextElement();
				if (item instanceof ApplicationData) {
					ApplicationData data = (ApplicationData) item;

					String ext = FileUtil.getExtension(data.getFileName());
					if (!ext.equalsIgnoreCase("dwg")) {
						continue;
					}
					// if (ext.equalsIgnoreCase("dwg")) {
					// 0 = holder oid
					dwg[0] = representation.getPersistInfo().getObjectIdentifier().getStringValue();
					// 1 = app oid
					dwg[1] = data.getPersistInfo().getObjectIdentifier().getStringValue();
					// 2 = name
					dwg[2] = data.getFileName();
					// 3 = size
					dwg[3] = data.getFileSizeKB() + " KB";
					// 4 = icon
					dwg[4] = data.getFileName();
					// 5 = down url
					// dwg[5] = WCUtil.getDownloadURL(dwg[0], dwg[1]);
					dwg[5] = ContentHelper.getDownloadURL(representation, data, false, dwg[2]).toString();
					// 6 = file version
					dwg[6] = data.getFileVersion();
					// 7 = file category
					dwg[7] = data.getCategory();
					// }
				}
			}
		}
		return dwg;
	}
}
