package ext.narae.service.document.beans;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import org.apache.logging.log4j.Logger;

import ext.narae.component.SerializableInputStream;
import ext.narae.service.CommonUtil2;
import ext.narae.util.DateUtil;
import ext.narae.util.SequenceDao;
import ext.narae.util.WCUtil;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.DocumentType;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.enterprise.TemplateInfo;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleHelper;
import wt.log4j.LogR;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.pom.Transaction;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.util.WTException;

public class DocumentHelper2 {
	public static final DocumentHelper2 manager = new DocumentHelper2();
	private static final Logger log = LogR.getLoggerInternal(DocumentHelper2.class.getName());
	private static String TEST_SERVER = "wc10.ptc.com";

	public static String create(HashMap<String, Object> hash) throws Exception {
		if (!RemoteMethodServer.ServerFlag) {
			Class argTypes[] = new Class[] { Hashtable.class };
			Object args[] = new Object[] { hash };
			try {
				return (String) wt.method.RemoteMethodServer.getDefault().invoke("create",
						DocumentHelper2.class.getName(), null, argTypes, args);
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
		} else {
			Transaction trx = new Transaction();
			trx.start();
			try {
				log.debug("========= Start Creating Document ========");

				WTContainerRef containerRef = WCUtil.getWTContainerRefForDrawing();
				log.debug("CAD container=" + containerRef.getName());

				String pdmNumber = ((String) hash.get("number") != null)
						? URLDecoder.decode((String) hash.get("number"), "utf-8")
						: "";
				String name = ((String) hash.get("nameValue") != null)
						? URLDecoder.decode((String) hash.get("nameValue"), "utf-8")
						: "";
				String description = ((String) hash.get("descriptionValue") != null)
						? URLDecoder.decode((String) hash.get("descriptionValue"), "utf-8")
						: "";
				String project = ((String) hash.get("projectValue") != null)
						? URLDecoder.decode((String) hash.get("projectValue"), "utf-8")
						: "";
				String partOidList = ((String) hash.get("partOidList") != null) ? (String) hash.get("partOidList") : "";
				String docType = ((String) hash.get("docType") != null) ? (String) hash.get("docType") : "";
				String selectedFolderFromFolderContext = ((String) hash.get("selectedFolderFromFolderContext") != null)
						? URLDecoder.decode((String) hash.get("selectedFolderFromFolderContext"), "utf-8")
						: "";

				String primaryFileName = ((String) hash.get("primaryFileName") != null)
						? URLDecoder.decode((String) hash.get("primaryFileName"), "utf-8")
						: "";
				SerializableInputStream primary = (hash.get("primary") != null)
						? (SerializableInputStream) hash.get("primary")
						: null;
				List<String> secondaryFileName = (hash.get("secondaryFileName") != null)
						? (List<String>) hash.get("secondaryFileName")
						: new ArrayList<String>();
				List<SerializableInputStream> secondary = (hash.get("secondary") != null)
						? (List<SerializableInputStream>) hash.get("secondary")
						: new ArrayList<SerializableInputStream>();
				HashMap<String, String> secondaryDelFile = (hash.get("secondaryDelFile") != null)
						? (HashMap<String, String>) hash.get("secondaryDelFile")
						: new HashMap<String, String>();

				String lifecycle = ((String) hash.get("lifecycle") != null) ? (String) hash.get("lifecycle") : "";

				WTDocument doc = null;

				doc = WTDocument.newWTDocument();
				doc.setName(name);
				doc.setDescription(description);

				doc.setTitle(project);
				// Folder && LifeCycle Setting
				if (selectedFolderFromFolderContext != null && selectedFolderFromFolderContext.length() > 0) {
					Folder folder = (Folder) CommonUtil2.getInstance(selectedFolderFromFolderContext);
					FolderHelper.assignLocation((FolderEntry) doc, folder);
					log.debug("Complete to set folder=" + folder.getFolderPath());
				} else {
					Folder folder = FolderHelper.service.getFolder("/Default/DOCUMENT", containerRef);
					FolderHelper.assignLocation((FolderEntry) doc, folder);
					log.debug("Complete to set folder=" + folder.getFolderPath());
				}

				doc.setContainerReference(containerRef);

				log.debug("Set lifecycle=" + lifecycle);
				LifeCycleHelper.setLifeCycle(doc,
						LifeCycleHelper.service.getLifeCycleTemplate(lifecycle, containerRef)); // Lifecycle
				log.debug("Complete set lifecycle=" + lifecycle);

				String number = "";
				String noFormat = "";
				if ("$$SWDocument".equals(docType)) {
					DocumentType documentType = DocumentType.toDocumentType(docType);
					doc.setDocType(documentType);

					number = pdmNumber;
					noFormat = "00000";
				} else {
					number = "ND-" + DateUtil.getCurrentDateString("year");
					noFormat = "0000";
				}

				String seqNo = SequenceDao.manager.getSeqNo(number + "-", noFormat, "WTDocumentMaster",
						"WTDocumentNumber");
				// System.out.println("number == " + number);
				// System.out.println("seqNo == " + seqNo);
				number = number + "-" + seqNo;
				doc.setNumber(number);

				// System.out.println("number == " + number);

				/* Set value for Document Template */
				String template = (String) hash.get("Template");
				// System.out.println("template === " + template);
				if (template != null && template.equals("true")) {
					number = (String) hash.get("number");
					doc.setNumber(number);

					TemplateInfo doctemplate = TemplateInfo.newTemplateInfo();
					doctemplate.setEnabled(true);
					doctemplate.setTemplated(true);
					doc.setTemplate(doctemplate);
				}

				doc = (WTDocument) PersistenceHelper.manager.save(doc);

				// Primary
				log.debug("Checking primary file............."
						+ primaryFileName.substring(primaryFileName.lastIndexOf(File.separator) + 1));
				if (primary != null) {
					ApplicationData applicationData = ApplicationData.newApplicationData(doc);
					applicationData
							.setFileName(primaryFileName.substring(primaryFileName.lastIndexOf(File.separator) + 1));
					applicationData.setRole(ContentRoleType.PRIMARY);
					applicationData.setCreatedBy(SessionHelper.manager.getPrincipalReference());
					try {
						ContentServerHelper.service.updateContent(doc, applicationData, primary);
					} catch (Exception e) {

					}
					log.debug("Complete Primary file.........................");
				}

				log.debug("Checking secondary file size=" + secondaryFileName.size());
				// Secondary Attach
				if (secondaryFileName.size() > 0) {
					log.debug("Exist attachment:" + secondaryFileName.size());
					attachSecondary(doc, secondaryFileName, secondary, secondaryDelFile);
					log.debug("Complete secondary attachment............");
				}

				if (partOidList != null && partOidList.trim().length() > 0) {
					log.debug("Make link related part.............");
					setRelevantData(doc, partOidList);
					log.debug("Complete to Link to part.............");
				}

				trx.commit();
				log.debug("========= Complete Creating Document ========");
			} catch (Exception e) {
				trx.rollback();
				e.getStackTrace();
				return e.getLocalizedMessage();
			}

			return "";
		}
	}

	private static void setRelevantData(WTDocument doc, String docOids) throws WTException {
		clearRelevantData(doc);

		String partOidList[] = docOids.split("[,]");
		if (partOidList.length > 0) {
			WTPart oneObject = null;
			for (String oneOid : partOidList) {
				oneObject = (WTPart) CommonUtil2.getInstance(oneOid.trim());
				setRelevantData(doc, oneObject);
			}
		}
	}

	private static void setRelevantData(WTDocument doc, WTPart part) throws WTException {
		WTPartDescribeLink link = WTPartDescribeLink.newWTPartDescribeLink(part, doc);
		PersistenceServerHelper.manager.insert(link);
	}

	private static void clearRelevantData(WTDocument doc) throws WTException {
		String oid = (new ReferenceFactory()).getReferenceString(doc);
		String[] oidArr = oid.split("[:]");
		long modelId = Long.valueOf(oidArr[2]).longValue();

		QuerySpec spec = new QuerySpec(WTPartDescribeLink.class);
		SearchCondition condition = new SearchCondition(WTPartDescribeLink.class, "roleBObjectRef.key.id",
				SearchCondition.EQUAL, modelId);
		spec.appendWhere(condition);

		QueryResult result = PersistenceHelper.manager.find(spec);

		if (result != null && result.size() > 0) {
			WTPartDescribeLink one = null;
			while (result.hasMoreElements()) {
				one = (WTPartDescribeLink) result.nextElement();
				PersistenceServerHelper.manager.remove(one);
			}
		}
	}

	public static void attachSecondary(ContentHolder holder, List<String> fileName, List<SerializableInputStream> file,
			HashMap<String, String> secondaryDelFile)
			throws WTException, FileNotFoundException, PropertyVetoException, IOException {
		if (secondaryDelFile.size() > 0) {
			QueryResult result = ContentHelper.service.getContentsByRole(holder, ContentRoleType.SECONDARY);
			ApplicationData data = null;
			while (result.hasMoreElements()) {
				data = (ApplicationData) result.nextElement();
				if (secondaryDelFile.get(data.getPersistInfo().getObjectIdentifier().getStringValue()) == null) {
					ContentServerHelper.service.deleteContent(holder, data);
				}
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
			is = file.get(index);
			applicationData = ApplicationData.newApplicationData(holder);
			sFileName = fileName.get(index);
			applicationData.setFileName(sFileName.substring(sFileName.lastIndexOf(File.separator) + 1));
			applicationData.setRole(ContentRoleType.SECONDARY);
			ContentServerHelper.service.updateContent(holder, applicationData, is);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fileName = "d:\\aaaaa";
		System.out.println(fileName.substring(fileName.lastIndexOf(File.separator)));
	}

	// TODO Auto-generated method stub
	public String getNextNumber(String number) throws Exception {
		DecimalFormat df = new DecimalFormat("00000");
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTDocumentMaster.class, true);

		SearchCondition sc = new SearchCondition(WTDocumentMaster.class, WTDocumentMaster.NUMBER, "LIKE",
				"%" + number + "%");
		query.appendWhere(sc, new int[] { idx });

		ClassAttribute ca = new ClassAttribute(WTDocumentMaster.class, WTDocumentMaster.NUMBER);
		OrderBy by = new OrderBy(ca, true);
		query.appendOrderBy(by, new int[] { idx });

		QueryResult result = PersistenceHelper.manager.find(query);
		System.out.println(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WTDocumentMaster master = (WTDocumentMaster) obj[0];
			String n = master.getNumber();
			int _idx = n.lastIndexOf("-");
			n = n.substring(_idx + 1);
			int reValue = Integer.parseInt(n) + 1;
			return df.format(reValue);
		}
		return "00000";
	}
}
