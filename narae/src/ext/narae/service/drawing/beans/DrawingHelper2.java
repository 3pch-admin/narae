package ext.narae.service.drawing.beans;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.logging.log4j.Logger;

import com.ptc.core.foundation.type.server.impl.TypeHelper;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.wvs.server.util.PublishUtils;

import ext.narae.component.SerializableInputStream;
import ext.narae.service.CommonUtil2;
import ext.narae.service.ServerConfigHelper;
import ext.narae.service.iba.beans.AttributeService;
import ext.narae.service.part.beans.PartHelper;
import ext.narae.util.CommonUtil;
import ext.narae.util.SequenceDao;
import ext.narae.util.WCUtil;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.epm.EPMApplicationType;
import wt.epm.EPMAuthoringAppType;
import wt.epm.EPMContextHelper;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.EPMDocumentType;
import wt.epm.build.EPMBuildHistory;
import wt.epm.build.EPMBuildRule;
import wt.epm.structure.EPMReferenceLink;
import wt.epm.structure.EPMStructureHelper;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleHelper;
import wt.log4j.LogR;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.representation.Representation;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.vc.config.LatestConfigSpec;

public class DrawingHelper2 implements RemoteAccess, Serializable {
	private static final Logger log = LogR.getLoggerInternal(DrawingHelper2.class.getName());
	private static String TEST_SERVER = "wc10.ptc.com";

	public static EPMDocument create(HashMap<String, Object> hash) throws Exception {
		if (!RemoteMethodServer.ServerFlag) {
			Class[] argTypes = { Hashtable.class };
			Object[] args = { hash };
			try {
				return (EPMDocument) RemoteMethodServer.getDefault().invoke("create", DrawingHelper2.class.getName(),
						null, argTypes, args);
			} catch (RemoteException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}

		log.debug("=========== Start creating drawing ===========");
		Transaction trx = new Transaction();
		EPMDocument epm = null;
		try {
			trx.start();

			WTContainerRef containerRef = WCUtil.getWTContainerRefForDrawing();
			log.debug("CAD container=" + containerRef.getName());

			if (!ServerConfigHelper.getServerHostName().equals(TEST_SERVER)) {
				log.debug("Get frawing type=WCTYPE|wt.epm.EPMDocument|com.naraenano.DefaultEPMDocument");
				TypeIdentifier objType = TypeHelper
						.getTypeIdentifier("WCTYPE|wt.epm.EPMDocument|com.naraenano.DefaultEPMDocument");
				epm = (EPMDocument) TypeHelper.newInstance(objType);
				log.debug("Complete to get type.................");
			} else {
				log.debug("Get frawing type=WCTYPE|wt.epm.EPMDocument|com.ptc.wc10.DefaultEPMDocument");
				TypeIdentifier objType = TypeHelper
						.getTypeIdentifier("WCTYPE|wt.epm.EPMDocument|com.ptc.wc10.DefaultEPMDocument");
				epm = (EPMDocument) TypeHelper.newInstance(objType);
				log.debug("Complete to get type.................");
			}

			String lifecycle = (String) hash.get("lifecycle") != null
					? URLDecoder.decode((String) hash.get("lifecycle"), "utf-8")
					: "";
			String documentType = (String) hash.get("documentType") != null
					? URLDecoder.decode((String) hash.get("documentType"), "utf-8")
					: "";
			String partFolder = (String) hash.get("partFolderValue") != null
					? URLDecoder.decode((String) hash.get("partFolderValue"), "utf-8")
					: "";
			String selectedFolderFromFolderContext = (String) hash.get("selectedFolderFromFolderContext") != null
					? URLDecoder.decode((String) hash.get("selectedFolderFromFolderContext"), "utf-8")
					: "";
			String quantityunit = (String) hash.get("quantityunit") != null
					? URLDecoder.decode((String) hash.get("quantityunit"), "utf-8")
					: "";
			String spec = (String) hash.get("standard") != null
					? URLDecoder.decode((String) hash.get("standard"), "utf-8")
					: "";
			String isDrawing = (String) hash.get("isDrawing") != null
					? URLDecoder.decode((String) hash.get("isDrawing"), "utf-8")
					: "";
			String tempnumber = (String) hash.get("number") != null
					? URLDecoder.decode((String) hash.get("number"), "utf-8")
					: "";
			String buildRoleValue = (String) hash.get("buildRoleValue") != null
					? URLDecoder.decode((String) hash.get("buildRoleValue"), "utf-8")
					: "";
			String partOidValue = (String) hash.get("partOidValue") != null
					? URLDecoder.decode((String) hash.get("partOidValue"), "utf-8")
					: "";
			String makerValue = (String) hash.get("makerValue") != null
					? URLDecoder.decode((String) hash.get("makerValue"), "utf-8")
					: "";
			String groupValue = (String) hash.get("groupValue") != null
					? URLDecoder.decode((String) hash.get("groupValue"), "utf-8")
					: "";
			String typeValue = (String) hash.get("typeValue") != null
					? URLDecoder.decode((String) hash.get("typeValue"), "utf-8")
					: "";
			String unitValue = (String) hash.get("unitValue") != null
					? URLDecoder.decode((String) hash.get("unitValue"), "utf-8")
					: "";
			String class1Value = (String) hash.get("class1Value") != null
					? URLDecoder.decode((String) hash.get("class1Value"), "utf-8")
					: "";
			String class2Value = (String) hash.get("class2Value") != null
					? URLDecoder.decode((String) hash.get("class2Value"), "utf-8")
					: "";
			String class3Value = (String) hash.get("class3Value") != null
					? URLDecoder.decode((String) hash.get("class3Value"), "utf-8")
					: "";
			String nameValue = (String) hash.get("nameValue") != null
					? URLDecoder.decode((String) hash.get("nameValue"), "utf-8")
					: "";
			String materialValue = (String) hash.get("materialValue") != null
					? URLDecoder.decode((String) hash.get("materialValue"), "utf-8")
					: "";
			String treatmentValue = (String) hash.get("treatmentValue") != null
					? URLDecoder.decode((String) hash.get("treatmentValue"), "utf-8")
					: "";
			String weightValue = (String) hash.get("weightValue") != null
					? URLDecoder.decode((String) hash.get("weightValue"), "utf-8")
					: "";
			String sheetValue = (String) hash.get("sheetValue") != null
					? URLDecoder.decode((String) hash.get("sheetValue"), "utf-8")
					: "";
			String refModelNoValue = (String) hash.get("refModelNoValue") != null
					? URLDecoder.decode((String) hash.get("refModelNoValue"), "utf-8")
					: "";
			String drwTypeValue = (String) hash.get("drwTypeValue") != null
					? URLDecoder.decode((String) hash.get("drwTypeValue"), "utf-8")
					: "";
			String descriptionValue = (String) hash.get("descriptionValue") != null
					? URLDecoder.decode((String) hash.get("descriptionValue"), "utf-8")
					: "";
			String authoringTypeValue = (String) hash.get("authoringTypeValue") != null
					? URLDecoder.decode((String) hash.get("authoringTypeValue"), "utf-8")
					: "";

			String primaryFileName = (String) hash.get("primaryFileName") != null
					? URLDecoder.decode((String) hash.get("primaryFileName"), "utf-8")
					: "";
			SerializableInputStream primary = hash.get("primary") != null
					? (SerializableInputStream) hash.get("primary")
					: null;
			List secondaryFileName = hash.get("secondaryFileName") != null ? (List) hash.get("secondaryFileName")
					: new ArrayList();
			List secondary = hash.get("secondary") != null ? (List) hash.get("secondary") : new ArrayList();
			HashMap secondaryDelFile = hash.get("secondaryDelFile") != null ? (HashMap) hash.get("secondaryDelFile")
					: new HashMap();

			log.debug("...... Checking parameters .....");
			log.debug("lifecycle=" + lifecycle);
			log.debug("documentType=" + documentType);
			log.debug("partFolder=" + partFolder);
			log.debug("selectedFolderFromFolderContext=" + selectedFolderFromFolderContext);
			log.debug("quantityunit=" + quantityunit);
			log.debug("spec=" + spec);
			log.debug("isDrawing=" + isDrawing);
			log.debug("tempnumber=" + tempnumber);
			log.debug("buildRoleValue=" + buildRoleValue);
			log.debug("partOidValue=" + partOidValue);
			log.debug("makerValue=" + makerValue);
			log.debug("groupValue=" + groupValue);
			log.debug("typeValue=" + typeValue);
			log.debug("unitValue=" + unitValue);
			log.debug("class1Value=" + class1Value);
			log.debug("class2Value=" + class2Value);
			log.debug("class3Value=" + class3Value);
			log.debug("nameValue=" + nameValue);
			log.debug("materialValue=" + materialValue);
			log.debug("treatmentValue=" + treatmentValue);
			log.debug("weightValue=" + weightValue);
			log.debug("sheetValue=" + sheetValue);
			log.debug("refModelNoValue=" + refModelNoValue);
			log.debug("drwTypeValue=" + drwTypeValue);
			log.debug("descriptionValue=" + descriptionValue);
			log.debug("authoringTypeValue=" + authoringTypeValue);
			log.debug("primaryFileName=" + primaryFileName);
			log.debug("primary=" + primary);
			log.debug("secondaryFileName=" + secondaryFileName);
			log.debug("secondary=" + secondary);
			log.debug("secondaryDelFile=" + secondaryDelFile);
			log.debug("...... End Checking parameters .....");

			WTPart part = null;

			String number = "";
			if (buildRoleValue.equals("link")) {
				log.debug("Selected link with part, so get inputted part .....");
				log.debug("Link processing .....");

				part = (WTPart) CommonUtil.getObject(partOidValue);
				number = part.getNumber();
				nameValue = part.getName();
				log.debug("Selected part=" + number + "|" + nameValue);
				makerValue = (String) AttributeService.getValue(part, "Maker");
				log.debug("Selected part[maker]=" + makerValue);
				spec = (String) AttributeService.getValue(part, "Spec");
				log.debug("Selected part[spec]=" + spec);
				quantityunit = part.getDefaultUnit().getStringValue();
				log.debug("Selected part[quantityunit]=" + quantityunit);
				System.out.println("============================ GET PART INFORMATION =======================");
			} else {
				log.debug("New and None Procession .....");

				log.debug("Make fixed part number........");
				String serialNum = "";
				if (tempnumber.substring(1, 2).equals("A")) {
					String epmSerialNum = SequenceDao.manager.getSeqNo(tempnumber + "-", "000", "EPMDocumentMaster",
							"documentNumber");
					log.debug("Complete get epm serial number = " + epmSerialNum);
					String partSerialNum = SequenceDao.manager.getSeqNo(tempnumber + "-", "000", "WTPartMaster",
							"WTPartNumber");
					log.debug("Complete get part serial number = " + partSerialNum);
					if (Integer.parseInt(epmSerialNum) > Integer.parseInt(partSerialNum))
						serialNum = epmSerialNum;
					else
						serialNum = partSerialNum;
				} else {
					String epmSerialNum = SequenceDao.manager.getSeqNo(tempnumber + "-", "00000", "EPMDocumentMaster",
							"documentNumber");
					log.debug("Complete get epm serial number = " + epmSerialNum);
					String partSerialNum = SequenceDao.manager.getSeqNo(tempnumber + "-", "00000", "WTPartMaster",
							"WTPartNumber");
					log.debug("Complete get part serial number = " + partSerialNum);
					if (Integer.parseInt(epmSerialNum) > Integer.parseInt(partSerialNum))
						serialNum = epmSerialNum;
					else {
						serialNum = partSerialNum;
					}
				}
				number = tempnumber + "-" + serialNum;
				log.debug("Final result number = " + number);

				if (buildRoleValue.equals("new")) {
					log.debug("Make new part processing..............");

					HashMap partHash = new HashMap();
					if ((partFolder == null) || (partFolder.trim().length() == 0)) {
						partHash.put("partFolderValue", "/Default");
					} else {
						System.out.println("Current Folder Path=" + partFolder);
						System.out.println("Send Folder path encoding=" + URLEncoder.encode(partFolder, "utf-8"));
						partHash.put("partFolderValue", URLEncoder.encode(partFolder, "utf-8"));
					}
					partHash.put("number", number);
					partHash.put("name", nameValue);
					partHash.put("maker1", makerValue);
					partHash.put("quantityunit", quantityunit);
					partHash.put("standard", spec);
					partHash.put("isDrawing", isDrawing);
					partHash.put("partdescription", descriptionValue);
					partHash.put("lifecycle", lifecycle);
					partHash.put("source", "make");
					partHash.put("view", "Design");
					partHash.put("wtPartType", "separable");

					log.debug("Creating part................");
					part = PartHelper.createPart(partHash);
					log.debug("Complete to create part=" + part);

					if (part == null) {
						log.debug("Failed to create part<==========XXXX");

						throw new WTException("Failed to create part!!!!");
					}
				}
			}

			log.debug("Starting create Drawing.........................");
			epm.setNumber(number);
			epm.setName(nameValue);
			epm.setDescription(descriptionValue);
			log.debug("Complete to set model attribute=" + number + "|" + nameValue);

			EPMDocumentMaster epmMaster = (EPMDocumentMaster) epm.getMaster();
			EPMContextHelper.setApplication(EPMApplicationType.toEPMApplicationType(documentType));
			epmMaster.setOwnerApplication(EPMContextHelper.getApplication());
			EPMAuthoringAppType appType = EPMAuthoringAppType.toEPMAuthoringAppType(authoringTypeValue);
			epmMaster.setAuthoringApplication(appType);
			log.debug("Complete to set master attribute=" + documentType + "|" + authoringTypeValue);

			if ((selectedFolderFromFolderContext != null) && (selectedFolderFromFolderContext.length() > 0)) {
				Folder folder = (Folder) CommonUtil2.getInstance(selectedFolderFromFolderContext);
				FolderHelper.assignLocation(epm, folder);
				log.debug("Complete to set folder=" + folder.getFolderPath());
			} else {
				Folder folder = FolderHelper.service.getFolder("/Default", containerRef);
				FolderHelper.assignLocation(epm, folder);
				log.debug("Complete to set folder=" + folder.getFolderPath());
			}

			log.debug("Set lifecycle=" + lifecycle);
			LifeCycleHelper.setLifeCycle(epm, LifeCycleHelper.service.getLifeCycleTemplate(lifecycle, containerRef));
			log.debug("Complete set lifecycle=" + lifecycle);

			log.debug("Set primary file information........");
			String newFileName = "";
			String fileName = "";
			if (primary != null) {
				int lastIndex = primaryFileName.lastIndexOf(".");
				String fileEnd = primaryFileName.substring(lastIndex).toLowerCase();
				fileName = number + fileEnd;
				log.debug("Cad file name=" + fileName);

				epm.setCADName(fileName);
				newFileName = fileName;
				EPMDocumentType docType = getEPMDocumentType(fileEnd);
				log.debug("Cad DocType=" + docType);
				epm.setDocType(docType);
			}
			log.debug("Complete primary file information........");

			log.debug("Starting to save EPMDocument.........................");
			epm = (EPMDocument) PersistenceHelper.manager.save(epm);
			log.debug("Complte to save........................." + epm);

			log.debug("Checking primary file............."
					+ primaryFileName.substring(primaryFileName.lastIndexOf(File.separator) + 1));
			if ((newFileName != null) && (!newFileName.equals(""))) {
				log.debug("Starting add Primary file=" + newFileName);
				ApplicationData applicationData = ApplicationData.newApplicationData(epm);
				applicationData.setFileName(primaryFileName.substring(primaryFileName.lastIndexOf(File.separator) + 1));
				applicationData.setRole(ContentRoleType.PRIMARY);
				applicationData.setCreatedBy(SessionHelper.manager.getPrincipalReference());
				try {
					ContentServerHelper.service.updateContent(epm, applicationData, primary);
				} catch (Exception localException1) {
				}
				log.debug("Complete Primary file.........................");
			}

			log.debug("Checking secondary file size=" + secondaryFileName.size());

			if (secondaryFileName.size() > 0) {
				log.debug("Exist attachment:" + secondaryFileName.size());
				attachSecondary(epm, secondaryFileName, secondary, secondaryDelFile);
				log.debug("Complete secondary attachment");
			}

			log.debug("Starting to save IBA.........................");

			HashMap attrs = new HashMap();
			attrs.put("Group", groupValue);
			attrs.put("Type", typeValue);
			attrs.put("Unit", unitValue);
			attrs.put("Class1", class1Value);
			attrs.put("Class2", class2Value);
			attrs.put("Class3", class3Value);
			attrs.put("Material", materialValue);
			attrs.put("Treatment", treatmentValue);
			attrs.put("Weight_f", weightValue);
			attrs.put("Sheet", sheetValue);
			attrs.put("Ref_Model_no", refModelNoValue);
			attrs.put("DRW_type", drwTypeValue);

			AttributeService.setValue(epm, attrs);
			log.debug("Complete to save IBA.........................");

			log.debug("Checking exist part............." + part);

			if (part != null) {
				log.debug("Starting to link PART.........................");
				EPMBuildRule link = EPMBuildRule.newEPMBuildRule(epm, part);

				PersistenceServerHelper.manager.insert(link);
				log.debug("Complete link PART=" + link);

				part = (WTPart) link.getBuildTarget();

				part = (WTPart) CommonUtil.getObject(CommonUtil.getVROID(part));

				log.debug("Change Part information");
				AttributeService.setValue(part, "partdescription", descriptionValue);

				AttributeService.setValue(part, "IsDrawing", "Y");

				log.debug("Update drawing information");
				AttributeService.setValue(epm, "Maker", makerValue);

				AttributeService.setValue(epm, "quantityunit", quantityunit);

				AttributeService.setValue(epm, "Spec", spec);

				log.debug("Complete to link PART.........................");
			}

			if (primary != null) {
				log.debug("Starting publish drawing.........................");
				EpmPublishUtil.publish(epm);
				log.debug("Complete publish drawing.........................");
			}
			log.debug("return EPMDocument=" + epm);

			trx.commit();
			trx = null;
			log.debug("=========== End creating drawing ===========");
		} catch (Exception e) {
			e.printStackTrace();
			trx.rollback();
			return null;
		} finally {
			if (trx != null) {
				trx.rollback();
			}
		}

		return epm;
	}

	public static void attachSecondary(ContentHolder holder, List<String> fileName, List<SerializableInputStream> file,
			HashMap<String, String> secondaryDelFile)
			throws WTException, FileNotFoundException, PropertyVetoException, IOException {
		if (secondaryDelFile.size() > 0) {
			QueryResult result = ContentHelper.service.getContentsByRole(holder, ContentRoleType.SECONDARY);
			ApplicationData data = null;
			while (result.hasMoreElements()) {
				data = (ApplicationData) result.nextElement();
				if (secondaryDelFile.get(data.getPersistInfo().getObjectIdentifier().getStringValue()) == null)
					ContentServerHelper.service.deleteContent(holder, data);
			}
		} else {
			QueryResult result = ContentHelper.service.getContentsByRole(holder, ContentRoleType.SECONDARY);
			ApplicationData data = null;
			while (result.hasMoreElements()) {
				data = (ApplicationData) result.nextElement();
				ContentServerHelper.service.deleteContent(holder, data);
			}
		}

		InputStream is = null;
		ApplicationData applicationData = null;
		String sFileName = null;

		for (int index = 0; index < fileName.size(); index++) {
			is = (InputStream) file.get(index);
			applicationData = ApplicationData.newApplicationData(holder);
			sFileName = (String) fileName.get(index);
			applicationData.setFileName(sFileName.substring(sFileName.lastIndexOf(File.separator) + 1));
			applicationData.setRole(ContentRoleType.SECONDARY);
			ContentServerHelper.service.updateContent(holder, applicationData, is);
		}
	}

	public static EPMDocumentType getEPMDocumentType(String fileName) {
		String type = "OTHER";

		if (".drw".equals(fileName)) {
			type = "CADDRAWING";
		} else if (".prt".equals(fileName)) {
			type = "CADCOMPONENT";
		} else if (".asm".equals(fileName)) {
			type = "CADASSEMBLY";
		} else if (".frm".equals(fileName)) {
			type = "FORMAT";
		} else if (".dwg".equals(fileName)) {
			type = "CADCOMPONENT";
		} else if (".igs".equals(fileName)) {
			type = "IGES";
		} else if (".iges".equals(fileName)) {
			type = "IGES";
		} else if (".gif".equals(fileName)) {
			type = "PUB_GRAPHIC";
		} else if (".jpg".equals(fileName)) {
			type = "PUB_GRAPHIC";
		} else if (".zip".equals(fileName)) {
			type = "ZIP";
		}

		return EPMDocumentType.toEPMDocumentType(type);
	}

	public static EPMDocument getEPMDocument(WTPart part) throws WTException {
		if (part == null)
			return null;

		QueryResult qr = null;
		if (VersionControlHelper.isLatestIteration(part))
			qr = PersistenceHelper.manager.navigate(part, "buildSource", EPMBuildRule.class);
		else {
			qr = PersistenceHelper.manager.navigate(part, "builtBy", EPMBuildHistory.class);
		}
		if ((qr != null) && (qr.hasMoreElements())) {
			return (EPMDocument) qr.nextElement();
		}
		return null;
	}

	public static WTPart getWTPart(EPMDocument epm) throws WTException {
		if (epm == null)
			return null;
		QueryResult qr = null;
		if (VersionControlHelper.isLatestIteration(epm))
			qr = PersistenceHelper.manager.navigate(epm, "buildTarget", EPMBuildRule.class);
		else
			qr = PersistenceHelper.manager.navigate(epm, "built", EPMBuildHistory.class);
		if ((qr != null) && (qr.hasMoreElements())) {
			return (WTPart) qr.nextElement();
		}
		return null;
	}

	public static EPMDocument getRelational2DCad(EPMDocument epm) throws WTException {
		EPMDocument epm2d = null;
		if ((epm != null) && ((epm instanceof EPMDocument)) && (!checkDrawing(epm))) {
			QuerySpec spec = new QuerySpec(EPMDocument.class);
			LatestConfigSpec lSpec = new LatestConfigSpec();
			spec = lSpec.appendSearchCriteria(spec);
			QueryResult result = EPMStructureHelper.service.navigateReferencedBy((EPMDocumentMaster) epm.getMaster(),
					spec, false);

			EPMReferenceLink object = null;
			EPMDocument doc = null;
			String version = null;
			HashMap aHash = new HashMap();
			while (result.hasMoreElements()) {
				object = (EPMReferenceLink) result.nextElement();
				doc = object.getReferencedBy();

				if (object.getDepType() == 4) {
					version = doc.getVersionInfo().getIdentifier().getValue();
//          System.out.println("doc Number ="+doc.getNumber()+"\t"+doc.getName()+"<br>");
					if (!doc.getNumber().contains(epm.getNumber()))
						continue;
					for (int index = version.length(); index < 10; index++)
						version = "0" + version;
					aHash.put(version, doc);
				}

			}

			if (aHash.size() > 0) {
				String hashKey = getLatestVersion(aHash);
				epm2d = (EPMDocument) aHash.get(hashKey);
			}
		}

		return epm2d;
	}

	public static String getPDFFileURL(EPMDocument epm, String modelNumber) {
		String url = "";
		String oid = epm.getPersistInfo().getObjectIdentifier().getStringValue();
		String pdfFile = "&nbsp;";
		try {
			if (("PROE".equals(epm.getAuthoringApplication().toString()))
					&& ("CADDRAWING".equals(epm.getDocType().toString()))) {
				Representation representation = PublishUtils.getRepresentation(epm);
				System.out.println("representation" + representation);
				if (representation == null) {
					QueryResult qr = ContentHelper.service.getContentsByRole(epm, ContentRoleType.SECONDARY);
					while (qr.hasMoreElements()) {
						ContentItem item = (ContentItem) qr.nextElement();
						if ((item instanceof ApplicationData)) {
							ApplicationData ad = (ApplicationData) item;

							if ((ad.getRole().toString().equals("SECONDARY"))
									&& (ad.getFileName().lastIndexOf("pdf") > 0)) {
								String nUrl = "/Windchill/servlet/AttachmentsDownloadDirectionServlet?oid=" + oid
										+ "&cioids=" + ad.getPersistInfo().getObjectIdentifier().getStringValue()
										+ "&role=SECONDARY";
								url = nUrl;
								pdfFile = "<a target=\"ContentFormatIconPopup\" href='" + nUrl + "'>&nbsp;"
										+ ad.getFileName() + "</a>";
							}
						}
					}
				}
				if (representation != null) {
					representation = (Representation) ContentHelper.service.getContents(representation);
					Vector contentList = ContentHelper.getContentList(representation);
					for (int l = 0; l < contentList.size(); l++) {
						ContentItem contentitem = (ContentItem) contentList.elementAt(l);
						if ((contentitem instanceof ApplicationData)) {
							ApplicationData drawAppData = (ApplicationData) contentitem;

							if ((!drawAppData.getRole().toString().equals("SECONDARY"))
									|| (drawAppData.getFileName().lastIndexOf("pdf") <= 0))
								continue;
							String fileName = drawAppData.getFileName().trim().split("[.]")[0];

							if (fileName.toUpperCase().contains(modelNumber.toUpperCase())) {
								String nUrl = "/Windchill/servlet/AttachmentsDownloadDirectionServlet?oid="
										+ representation.getPersistInfo().getObjectIdentifier().getStringValue()
										+ "&cioids="
										+ drawAppData.getPersistInfo().getObjectIdentifier().getStringValue()
										+ "&role=SECONDARY";
								url = nUrl;
								pdfFile = "<a target=\"ContentFormatIconPopup\" href='" + nUrl + "' ext:qtip=\""
										+ drawAppData.getFileName()
										+ "\"><img src=\"/Windchill/netmarkets/jsp/narae/portal/img/pdf.gif\" border=\"0\"></a>";
							}
						}
					}
				}
			} else {
				QueryResult result = ContentHelper.service.getContentsByRole(epm, ContentRoleType.SECONDARY);
				ContentItem tempitem = null;
				while (result.hasMoreElements()) {
					tempitem = (ContentItem) result.nextElement();
					ApplicationData pAppData = (ApplicationData) tempitem;

					if ((pAppData.getDescription() == null) || (pAppData.getDescription().equals("N")))
						continue;
					String nUrl = "/Windchill/servlet/AttachmentsDownloadDirectionServlet?oid="
							+ epm.getPersistInfo().getObjectIdentifier().getStringValue() + "&cioids="
							+ pAppData.getPersistInfo().getObjectIdentifier().getStringValue();
					url = nUrl;
					String cadFile = "<a target=\"ContentFormatIconPopup\" href='" + nUrl + "'>&nbsp;"
							+ pAppData.getFileName() + "</a>";
					return url;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return url;
	}

	public static String getPDFFile(EPMDocument epm, String modelNumber) {
		String oid = epm.getPersistInfo().getObjectIdentifier().getStringValue();
		String pdfFile = "&nbsp;";
		try {
			if (("PROE".equals(epm.getAuthoringApplication().toString()))
					&& ("CADDRAWING".equals(epm.getDocType().toString()))) {
				Representation representation = PublishUtils.getRepresentation(epm);
				System.out.println("representation" + representation);
				if (representation == null) {
					QueryResult qr = ContentHelper.service.getContentsByRole(epm, ContentRoleType.SECONDARY);
					while (qr.hasMoreElements()) {
						ContentItem item = (ContentItem) qr.nextElement();
						if ((item instanceof ApplicationData)) {
							ApplicationData ad = (ApplicationData) item;

							if ((ad.getRole().toString().equals("SECONDARY"))
									&& (ad.getFileName().lastIndexOf("pdf") > 0)) {
								String nUrl = "/Windchill/servlet/AttachmentsDownloadDirectionServlet?oid=" + oid
										+ "&cioids=" + ad.getPersistInfo().getObjectIdentifier().getStringValue()
										+ "&role=SECONDARY";

								pdfFile = "<a target=\"ContentFormatIconPopup\" href='" + nUrl + "'>&nbsp;"
										+ ad.getFileName() + "</a>";
							}
						}
					}
				}
				if (representation != null) {
					representation = (Representation) ContentHelper.service.getContents(representation);
					Vector contentList = ContentHelper.getContentList(representation);
					for (int l = 0; l < contentList.size(); l++) {
						ContentItem contentitem = (ContentItem) contentList.elementAt(l);
						if ((contentitem instanceof ApplicationData)) {
							ApplicationData drawAppData = (ApplicationData) contentitem;

							if ((!drawAppData.getRole().toString().equals("SECONDARY"))
									|| (drawAppData.getFileName().lastIndexOf("pdf") <= 0))
								continue;
							String fileName = drawAppData.getFileName().trim().split("[.]")[0];

							if (fileName.toUpperCase().contains(modelNumber.toUpperCase())) {
								String nUrl = "/Windchill/servlet/AttachmentsDownloadDirectionServlet?oid="
										+ representation.getPersistInfo().getObjectIdentifier().getStringValue()
										+ "&cioids="
										+ drawAppData.getPersistInfo().getObjectIdentifier().getStringValue()
										+ "&role=SECONDARY";

								pdfFile = "<a target=\"ContentFormatIconPopup\" href='" + nUrl + "' ext:qtip=\""
										+ drawAppData.getFileName()
										+ "\"><img src=\"/Windchill/netmarkets/jsp/narae/portal/img/pdf.gif\" border=\"0\"></a>";
							}
						}
					}
				}
			} else {
				QueryResult result = ContentHelper.service.getContentsByRole(epm, ContentRoleType.SECONDARY);
				ContentItem tempitem = null;
				while (result.hasMoreElements()) {
					tempitem = (ContentItem) result.nextElement();
					ApplicationData pAppData = (ApplicationData) tempitem;

					if ((pAppData.getDescription() == null) || (pAppData.getDescription().equals("N")))
						continue;
					String nUrl = "/Windchill/servlet/AttachmentsDownloadDirectionServlet?oid="
							+ epm.getPersistInfo().getObjectIdentifier().getStringValue() + "&cioids="
							+ pAppData.getPersistInfo().getObjectIdentifier().getStringValue();
					String cadFile = "<a target=\"ContentFormatIconPopup\" href='" + nUrl + "'>&nbsp;"
							+ pAppData.getFileName() + "</a>";
					return cadFile;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return pdfFile;
	}

	public static boolean checkDrawing(EPMDocument epm) throws WTException {
		return epm.getDocType().getStringValue().substring(epm.getDocType().getStringValue().lastIndexOf(".") + 1)
				.equals("CADDRAWING");
	}

	private static String getLatestVersion(HashMap<String, EPMDocument> versions) {
		Set<String> dKey = versions.keySet();
		Vector v = new Vector();
		for (String dStr : dKey) {
			v.add(dStr);
		}
		Collections.sort(v);

		return (String) v.get(v.size() - 1);
	}

	public static void main(String[] args) {
		try {
			EPMDocument doc = (EPMDocument) CommonUtil2.getInstance(args[0]);
			getPDFFile(doc, args[1]);
		} catch (WTException e) {
			e.printStackTrace();
		}
	}

	public static EPMDocument byPartNumber(String number, String version) throws Exception {

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(EPMDocument.class, true);

		SearchCondition sc = new SearchCondition(EPMDocument.class, EPMDocument.DOC_TYPE, "=", "CADDRAWING");
		query.appendWhere(sc, new int[] { idx });
		query.appendAnd();

		sc = new SearchCondition(EPMDocument.class, EPMDocument.NUMBER, "=", number);
		query.appendWhere(sc, new int[] { idx });
		query.appendAnd();

		sc = VersionControlHelper.getSearchCondition(EPMDocument.class, true);
		query.appendWhere(sc, new int[] { idx });
		query.appendAnd();
		sc = new SearchCondition(EPMDocument.class, "versionInfo.identifier.versionId", "=", version);
		query.appendWhere(sc, new int[] { idx });
		QueryResult result = PersistenceHelper.manager.find(query);
		EPMDocument doc = null;
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			doc = (EPMDocument) obj[0];
		}
		return doc;
	}
}
