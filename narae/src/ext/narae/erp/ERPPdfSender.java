package ext.narae.erp;

import java.beans.PropertyVetoException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.ptc.wvs.server.util.PublishUtils;

import ext.narae.service.drawing.beans.DrawingHelper2;
import ext.narae.service.erp.beans.ERPUtil;
import ext.narae.util.obj.ObjectUtil;
import wt.change2.WTChangeOrder2;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.enterprise.Master;
import wt.epm.EPMAuthoringAppType;
import wt.epm.EPMDocument;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.part.WTPart;
import wt.representation.Representation;
import wt.util.FileUtil;
import wt.util.WTException;

public class ERPPdfSender {
	public static boolean sendPdf(WTObject obj) {
		boolean result = true;
		WTChangeOrder2 eco = (WTChangeOrder2) obj;
		String target = ERPUtil.PDF_FOLDER + File.separator + getTargetFolder(eco.getPersistInfo().getUpdateStamp());
		String dwgpath = "D:\\temp\\dwgtopdf" + File.separator + eco.getNumber();
		System.out.println("============> Pdf Interface : " + eco.getNumber());
		System.out.println("파트표현식 체크전");
		WTPart[] parts = ERPInterface.getPartList(eco);
		if (parts != null) {
			System.out.println("연관 부품 건수 : " + parts.length);
		}
		for (int i = 0; (parts != null) && (i < parts.length); i++) {
			WTPart part = parts[i];

			System.out.println("파트표현식 체크전");
			if (!part.getNumber().toUpperCase().matches("^[A-Za-z]{2}-[A-Za-z0-9]{2}-.{10}$"))
				continue;
			System.out.println("Part Number : " + part.getNumber());

			System.out.println("파트표현식 체크후");

			File dir = new File(target);
			if (!dir.isDirectory()) {
				dir.mkdirs();
			}
			System.out.println("Target Path : " + target);
			try {
				EPMDocument epm2d = null;
				EPMDocument epm3d = DrawingHelper2.getEPMDocument(part);
				if (epm3d == null)
					System.out.println("3d파트가 없다");

				if (epm3d == null)
					continue;
				HashMap<String, ApplicationData> ads = new HashMap<String, ApplicationData>();

				EPMAuthoringAppType epmtype = epm3d.getAuthoringApplication();
				if ("PROE".equals(epm3d.getAuthoringApplication().toString())) {
					System.out.println("");
					epm2d = epm3d == null ? null : DrawingHelper2.getRelational2DCad(epm3d);
					System.out.println("Exists 2D EMPDocument : " + (epm2d != null ? "Exists" : "Not Exists"));
					if (epm2d == null)
						continue;
					ads = getDrawingFiles(epm2d);
				} else if ("ACAD".equals(epm3d.getAuthoringApplication().toString())) {
					ads = getACADFiles(epm3d);
				}

				if ((ads == null) || (ads.size() <= 0)) {
					continue;
				}
				for (Map.Entry entry : ads.entrySet()) {
					String name = (String) entry.getKey();
					ApplicationData ad = (ApplicationData) entry.getValue();
					EPMDocument epm2dLst = (EPMDocument) ObjectUtil.getLatestObject((Master) epm2d.getMaster());

					String epm2dVersion = epm2dLst.getVersionIdentifier().getValue() + "."
							+ epm2dLst.getIterationIdentifier().getValue();
					String adFileName = ad.getFileName();
					String adFileVer = "";
					if (FileUtil.getExtension(adFileName).equalsIgnoreCase("zip")) {
						adFileVer = epm2dVersion;
					} else {
						adFileVer = adFileName.substring(adFileName.indexOf(".") + 1, adFileName.lastIndexOf("."));
					}

					System.out.println("<br>Target adFileName : " + adFileName + "\tadFileVer : " + adFileVer
							+ "\tepm2dVersion=" + epm2dVersion);
					if (!adFileVer.equals(epm2dVersion)) {
						System.out.println("<br>Target continue adFileName : " + adFileName + "\tadFileVer : "
								+ adFileVer + "\tepm2dVersion:" + epm2dVersion
								+ epm2d.getIterationInfo().getIdentifier().getValue());
						continue;
					}
					String filename = setVersion(part.getNumber(), name.substring(name.lastIndexOf(".") + 1),
							part.getVersionInfo().getIdentifier().getValue() + "."
									+ part.getIterationInfo().getIdentifier().getValue());
					System.out.println("Target Name : " + filename);

					if (!sendFile(ad, target + File.separator + filename))
						result = false;

					if (("ACAD".equals(epm3d.getAuthoringApplication().toString()))
							&& (filename.toUpperCase().matches(".*.DWG$"))) {
						File file = new File(dwgpath);

						System.out.println("&&&&&&&&&&&&&&&&&&&& dwgpath : " + dwgpath);
						if (!file.isDirectory()) {
							file.mkdirs();
						}
						sendFile(ad, dwgpath + File.separator + filename);
					}

				}

			} catch (WTException e) {
				result = false;
				System.out.println("Error Occured : " + e.getMessage());
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}

		}

		return result;
	}

	public static void ConvertDwgToPdf(WTObject obj) {
		// eco 일 경우에만 실행됨
		if (obj instanceof WTChangeOrder2) {
			WTChangeOrder2 eco = (WTChangeOrder2) obj;
			System.out.println("===== Start Convert PDF =====");
			String target = ERPUtil.PDF_FOLDER + File.separator
					+ getTargetFolder(eco.getPersistInfo().getUpdateStamp());
			String dwgpath = "D:\\temp\\dwgtopdf" + File.separator + eco.getNumber();

			System.out.println(">>> target = " + target);
			System.out.println(">>> dwgpath = " + dwgpath);

			String result = "";
			String exepath = "C:\\Program Files (x86)\\AutoDWG\\AutoDWG DWG to PDF Converter";
			File command = new File(exepath + File.separator + "d2p.exe");
			String in_param = "/InFolder";
			String out_param = "/OutFolder ";
			String opt1_param = "/IncSubFolder";
			String opt2_param = "/InConfigFile";
			File config = new File(exepath + File.separator + "AutoDWGPdf.ddp");
			File dwgdir = new File(dwgpath);

			if (dwgdir.isDirectory())
				try {
					String batchpath = makeBatch(dwgdir.getAbsolutePath());

					System.out.println("batchpath : " + batchpath);
					File batch = new File(batchpath);
					if (!batch.isFile()) {
						return;
					}

					int cnt = dwgdir.list().length - 1;

					Runnable run = new MakePdfRunnable(batch.getAbsolutePath());
					Thread th_run = new Thread(run);
					th_run.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
	}

	public static void CopyPdfFiles(WTObject obj) {
		// eco 일 경우에만 실행됨
		if (obj instanceof WTChangeOrder2) {
			WTChangeOrder2 eco = (WTChangeOrder2) obj;
			String target = ERPUtil.PDF_FOLDER + File.separator
					+ getTargetFolder(eco.getPersistInfo().getUpdateStamp());
			String dwgpath = "D:\\temp\\dwgtopdf" + File.separator + eco.getNumber();

			Runnable copy = new CopyPdfRunnable(new String[] { dwgpath, target });
			Thread th_copy = new Thread(copy);
			th_copy.start();
		}
	}

	public static HashMap<String, ApplicationData> getACADFiles(EPMDocument epm)
			throws WTException, PropertyVetoException {
		HashMap apps = new HashMap();
		QueryResult qr = ContentHelper.service.getContentsByRole(epm, ContentRoleType.PRIMARY);
		while (qr.hasMoreElements()) {
			ContentItem item = (ContentItem) qr.nextElement();
			if ((item instanceof ApplicationData)) {
				ApplicationData appdata = (ApplicationData) item;
				System.out.println("AutoCAD Application Data : " + epm.getCADName());
				apps.put(epm.getCADName(), appdata);
			}
		}

		return apps;
	}

	public static HashMap<String, ApplicationData> getDrawingFiles(EPMDocument epm)
			throws WTException, PropertyVetoException {
		String oid = epm.getPersistInfo().getObjectIdentifier().getStringValue();

		HashMap apps = new HashMap();

		System.out.println("epm.getAuthoringApplication().toString() = " + epm.getAuthoringApplication().toString());
		System.out.println("epm.getDocType().toString() = " + epm.getDocType().toString());

		if (("PROE".equals(epm.getAuthoringApplication().toString()))
				&& ("CADDRAWING".equals(epm.getDocType().toString()))) {
			/*
			 * Representation representation = PublishUtils.getRepresentation(epm);
			 * System.out.println("representation" + representation); if (representation ==
			 * null) { QueryResult qr = ContentHelper.service.getContentsByRole(epm,
			 * ContentRoleType.SECONDARY); while (qr.hasMoreElements()) { ContentItem item =
			 * (ContentItem)qr.nextElement(); if ((item instanceof ApplicationData)) {
			 * ApplicationData ad = (ApplicationData)item; if
			 * ((ad.getRole().toString().equals("SECONDARY")) &&
			 * (ad.getFileName().toLowerCase().matches("^.*.(pdf|dwg){1}$"))) {
			 * apps.put(ad.getFileName(), ad); } } } } if (representation != null) {
			 * representation =
			 * (Representation)ContentHelper.service.getContents(representation); Vector
			 * contentList = ContentHelper.getContentList(representation); for (int l = 0; l
			 * < contentList.size(); l++) { ContentItem contentitem =
			 * (ContentItem)contentList.elementAt(l); if ((contentitem instanceof
			 * ApplicationData)) { ApplicationData drawAppData =
			 * (ApplicationData)contentitem;
			 * 
			 * if ((drawAppData.getRole().toString().equals("SECONDARY")) &&
			 * (drawAppData.getFileName().toLowerCase().matches("^.*.(pdf|dwg){1}$"))) {
			 * apps.put(drawAppData.getFileName(), drawAppData); } } } }
			 */
			QueryResult qr = ContentHelper.service.getContentsByRole(epm, ContentRoleType.SECONDARY);
			while (qr.hasMoreElements()) {
				ContentItem item = (ContentItem) qr.nextElement();

				if (item != null) {
					ApplicationData data = (ApplicationData) item;
					if (data.getFileName().toLowerCase().matches("^.*.(pdf|dwg){1}$")) {
						System.out.println("ADD data.getFileName()=" + data.getFileName());
						apps.put(data.getFileName(), data);
					}
				}
			}

			Representation representation = PublishUtils.getRepresentation(epm);
			if (representation != null) {
				QueryResult result = ContentHelper.service.getContentsByRole(representation,
						ContentRoleType.ADDITIONAL_FILES);
				while (result.hasMoreElements()) {
					ContentItem item = (ContentItem) result.nextElement();
					if (item instanceof ApplicationData) {
						ApplicationData data = (ApplicationData) item;
						System.out.println("zip=" + data.getFileName());
						if (FileUtil.getExtension(data.getFileName()).equalsIgnoreCase("zip")) {
							System.out.println("ADD ZIP data.getFileName()=" + data.getFileName());
							apps.put(data.getFileName(), data);
						}
					}
				}
			}

		}
		return apps;
	}

	public static boolean sendFile(ApplicationData adata, String filepath) {
		boolean result = false;
		try {
			System.out.println("//====> Save File Path : " + filepath);
			ContentServerHelper.service.writeContentStream(adata, filepath);
			result = true;
		} catch (IOException e) {
			System.out.println("Error Occured : " + e.getMessage());
		} catch (WTException e) {
			System.out.println("Error Occured : " + e.getMessage());
		}

		return result;
	}

	public static String getTargetFolder() {
		DateFormat sdFormat = new SimpleDateFormat("yyyy" + File.separator + "MMdd");
		Date nowDate = new Date();
		return sdFormat.format(nowDate);
	}

	public static String getTargetFolder(Date dt) {
		DateFormat sdFormat = new SimpleDateFormat("yyyy" + File.separator + "MMdd");
		return sdFormat.format(dt);
	}

	public static String setVersion(String partNum, String ext, String ver) {
		return partNum + "." + ver + "." + ext;
	}

	private static String makeBatch(String dwgpath) {
		String batch = dwgpath + File.separator + "makepdf.bat";
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(batch));

			String s = "\"C:\\Program Files (x86)\\AutoDWG\\AutoDWG DWG to PDF Converter\\d2p.exe\" /InFolder "
					+ dwgpath + "\\ /OutFolder " + dwgpath + "\\ /IncSubFolder ";

			System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% s : " + s);
			out.write(s);
			out.newLine();
			out.flush();
			out.close();
		} catch (IOException e) {
			System.err.println(e);
			System.exit(1);
		}
		return batch;
	}

	public boolean sendPdf2(WTChangeOrder2 eco2) {
		boolean result = true;
		WTChangeOrder2 eco = eco2;
		String target = ERPUtil.PDF_FOLDER + File.separator + getTargetFolder(eco.getPersistInfo().getUpdateStamp());
		String dwgpath = "D:\\temp\\dwgtopdf" + File.separator + eco.getNumber();
		System.out.println("============> Pdf Interface : " + eco.getNumber());
		System.out.println("파트표현식 체크전");
		WTPart[] parts = ERPInterface.getPartList(eco);
		if (parts != null) {
			System.out.println("연관 부품 건수 : " + parts.length);
			for (int i = 0; (parts != null) && (i < parts.length); i++) {
				WTPart part = parts[i];

				System.out.println("파트표현식 체크전");
				if (!part.getNumber().toUpperCase().matches("^[A-Za-z]{2}-[A-Za-z0-9]{2}-.{10}$"))
					continue;
				System.out.println("Part Number : " + part.getNumber());

				System.out.println("파트표현식 체크후");

				File dir = new File(target);
				if (!dir.isDirectory()) {
					dir.mkdirs();
				}
				System.out.println("Target Path : " + target);
				try {
					EPMDocument epm2d = null;
					EPMDocument epm3d = DrawingHelper2.getEPMDocument(part);
					if (epm3d == null)
						System.out.println("3d파트가 없다");

					if (epm3d == null)
						continue;
					HashMap<String, ApplicationData> ads = new HashMap<String, ApplicationData>();

					EPMAuthoringAppType epmtype = epm3d.getAuthoringApplication();
					if ("PROE".equals(epm3d.getAuthoringApplication().toString())) {
						System.out.println("");
						epm2d = epm3d == null ? null : DrawingHelper2.getRelational2DCad(epm3d);
						System.out.println("Exists 2D EMPDocument : " + (epm2d != null ? "Exists" : "Not Exists"));
						if (epm2d == null)
							continue;
						ads = getDrawingFiles(epm2d);
					} else if ("ACAD".equals(epm3d.getAuthoringApplication().toString())) {
						ads = getACADFiles(epm3d);
					}

					if ((ads == null) || (ads.size() <= 0)) {
						continue;
					}
					for (Map.Entry entry : ads.entrySet()) {
						String name = (String) entry.getKey();
						ApplicationData ad = (ApplicationData) entry.getValue();
						EPMDocument epm2dLst = (EPMDocument) ObjectUtil.getLatestObject((Master) epm2d.getMaster());

						String epm2dVersion = epm2dLst.getVersionIdentifier().getValue() + "."
								+ epm2dLst.getIterationIdentifier().getValue();
						String adFileName = ad.getFileName();
						String adFileVer = adFileName.substring(adFileName.indexOf(".") + 1,
								adFileName.lastIndexOf("."));

						System.out.println("<br>Target adFileName : " + adFileName + "\tadFileVer : " + adFileVer
								+ "\tepm2dVersion=" + epm2dVersion);
						if (!adFileVer.equals(epm2dVersion)) {
							System.out.println("<br>Target continue adFileName : " + adFileName + "\tadFileVer : "
									+ adFileVer + "\tepm2dVersion:" + epm2dVersion
									+ epm2d.getIterationInfo().getIdentifier().getValue());
							continue;
						}
						String filename = setVersion(part.getNumber(), name.substring(name.lastIndexOf(".") + 1),
								part.getVersionInfo().getIdentifier().getValue() + "."
										+ part.getIterationInfo().getIdentifier().getValue());
						System.out.println("Target Name : " + filename);

						if (!sendFile(ad, target + File.separator + filename))
							result = false;

						if (("ACAD".equals(epm3d.getAuthoringApplication().toString()))
								&& (filename.toUpperCase().matches(".*.DWG$"))) {
							File file = new File(dwgpath);

							System.out.println("&&&&&&&&&&&&&&&&&&&& dwgpath : " + dwgpath);
							if (!file.isDirectory()) {
								file.mkdirs();
							}
							sendFile(ad, dwgpath + File.separator + filename);
						}

					}

				} catch (WTException e) {
					result = false;
					System.out.println("Error Occured : " + e.getMessage());
				} catch (PropertyVetoException e) {
					e.printStackTrace();
				}

			}
		}

		return result;
	}

	public static void main(String[] args) {
		if ((args == null) || (args.length <= 0))
			return;
	}
}
