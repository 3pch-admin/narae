/**
 * @(#) MailUtil.java
 * Copyright (c) e3ps. All rights reserverd
 * 
 *	@version 1.00
 *	@since jdk 1.4.02
 *	@createdate 2005. 3. 3..
 *	@author Cho Sung Ok, jerred@e3ps.com
 *	@desc	
 */

package ext.narae.util.mail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import ext.narae.schedule.EMailScheduler;
import ext.narae.service.approval.ApprovalLine;
import ext.narae.service.approval.beans.ApprovalData;
import ext.narae.util.jdf.config.ConfigEx;
import ext.narae.util.jdf.config.ConfigExImpl;
import ext.narae.util.jdf.config.ConfigImpl;
import wt.content.ApplicationData;
import wt.content.ContentHolder;
import wt.content.StreamData;
import wt.content.Streamed;
import wt.fc.LobLocator;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.org.OrganizationServicesHelper;
import wt.org.WTUser;
import wt.session.SessionHelper;
import wt.util.WTProperties;

public class MailUtil {
	public static final MailUtil manager = new MailUtil();
	static final boolean VERBOSE = ConfigImpl.getInstance().getBoolean("develop.verbose", false);

	protected MailUtil() {
	}

	/**
	 * host, sendId, sendPass 설정하기...
	 * 
	 * @param hash
	 * @return
	 */
	public static boolean sendMail2(Hashtable hash) throws Exception {
		ConfigExImpl conf = ConfigEx.getInstance("eSolution");
		String host = conf.getString("mail.smtp.host");
		boolean enableMail = conf.getBoolean("e3ps.mail.enable", true);
//		System.out.println("host Mail : " + host);

		if (enableMail) {
			HashMap to = (HashMap) hash.get("TO");
			HashMap from = (HashMap) hash.get("FROM");
			String subject = (String) hash.get("SUBJECT");
			String content = (String) hash.get("CONTENT");
			Vector attache = (Vector) hash.get("ATTACHE");

			try {
				SendMail mail = new SendMail();
				// WTUser from = (WTUser)SessionHelper.manager.getPrincipal();
				// System.out.println("Sender : " + from.getFullName() + "," + from.getEMail());
				mail.setFromMailAddress((String) from.get("EMAIL"), (String) from.get("NAME"));

				if (to != null && to.size() > 0) {
					Object[] objArr = to.keySet().toArray();
					String emails = "";
					String toname = "";
					for (int i = 0; i < objArr.length; i++) {
						emails = (String) objArr[i];
						toname = (String) to.get(emails);
//						System.out.println("To Mail :" + emails);
//						System.out.println("To name :" + toname);

						if (emails.indexOf("@") < 0)
							continue;

						mail.setToMailAddress(emails, toname);
					}

				} else {
					throw new MailException("받는 사람 설정에러");
				}

				mail.setSubject(subject);

				String message = " Text 메일 메시지 내용 ";
				String htmlMessage = "<html><font color='red'> HTML 메일 메시지 내용</font></html>";
				// String[] fileNames = { "c:/attachFile1.zip","c:/attachFile2.txt" } ;
				String[] fileNames = {};

				if (content != null) {
					mail.setHtmlAndFile(content, fileNames);
				} else {
					mail.setHtmlAndFile(htmlMessage, fileNames);
				}
				// mail.setHtml(htmlMessage);
				// mail.setText(message);

				/**
				 * @Todo 개인 서버에서 주석처리함.
				 */
				mail.send(); // 메일 전송

				return true;
			} catch (Exception e) {
				throw e;
				// return false;
			}
		} else {
			return false;
		}
	}

	public boolean sendMail(Hashtable hash) {

		ConfigExImpl conf = ConfigEx.getInstance("eSolution");
		String host = conf.getString("mail.smtp.host");
		boolean enableMail = conf.getBoolean("e3ps.mail.enable", true);
//		System.out.println("host Mail : " + host);

		if (enableMail) {
			HashMap to = (HashMap) hash.get("TO");
			String subject = (String) hash.get("SUBJECT");
			String content = (String) hash.get("CONTENT");
			Vector attache = (Vector) hash.get("ATTACHE");

			try {
				SendMail mail = new SendMail();
				WTUser from = (WTUser) SessionHelper.manager.getPrincipal();

				mail.setFromMailAddress("pdmsystem@naraenano.com", "pdmsystem");

				if (to != null && to.size() > 0) {
					Object[] objArr = to.keySet().toArray();
					String emails = "";
					String toname = "";
					for (int i = 0; i < objArr.length; i++) {
						emails = (String) objArr[i];
						toname = (String) to.get(emails);
//						System.out.println("To Mail :" + emails);
//						System.out.println("To name :" + toname);

						if (emails.indexOf("@") < 0)
							continue;

						mail.setToMailAddress(emails, toname);
					}

				} else {
					throw new MailException("받는 사람 설정에러");
				}

				mail.setSubject(subject);

				String message = " Text 메일 메시지 내용 ";
				String htmlMessage = "<html><font color='red'> HTML 메일 메시지 내용</font></html>";
				// String[] fileNames = { "c:/attachFile1.zip","c:/attachFile2.txt" } ;
				String[] fileNames = {};

				if (content != null) {
					mail.setHtmlAndFile(content, fileNames);
				} else {
					mail.setHtmlAndFile(htmlMessage, fileNames);
				}
				// mail.setHtml(htmlMessage);
				// mail.setText(message);

				/**
				 * @Todo 개인 서버에서 주석처리함.
				 */
				mail.send(); // 메일 전송

				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		} else {
			return false;
		}
	}

	private File getFile(ContentHolder contentholder, ApplicationData applicationdata) throws Exception {
		// ContentHolder contentholder =
		// applicationdata.getHolderLink().getContentHolder();
		Streamed streamed = (Streamed) PersistenceHelper.manager.refresh(applicationdata.getStreamData().getObjectId());
		LobLocator loblocator = null;
		if (streamed instanceof StreamData) {
			applicationdata = (ApplicationData) PersistenceHelper.manager.refresh(applicationdata);
			streamed = (Streamed) PersistenceHelper.manager.refresh(applicationdata.getStreamData().getObjectId());
			try {
				loblocator.setObjectIdentifier(((ObjectReference) streamed).getObjectId());
				((StreamData) streamed).setLobLoc(loblocator);
			} catch (Exception exception) {
			}
		}

		String tempDir = System.getProperty("java.io.tmpdir");
		InputStream in = streamed.retrieveStream();
		File attachfile = new File(tempDir + File.separator + applicationdata.getFileName()); // 파일 저장 위치
		FileOutputStream fileOut = new FileOutputStream(attachfile);
		byte[] buffer = new byte[1024];
		int c;
		while ((c = in.read(buffer)) != -1)
			fileOut.write(buffer, 0, c);
		fileOut.close();

		return attachfile;
	}

	public static void sendSimpleMail(String subject, String content, String url, String[] toId) {

		try {

			ConfigExImpl conf = ConfigEx.getInstance("eSolution");
			boolean enableMail = conf.getBoolean("e3ps.mail.enable", true);

			if (!enableMail)
				return;

//			System.out.println("@@ call sendMailFtp()  title = " + subject);

			HashMap fromHash = new HashMap();
			fromHash.put("EMAIL", "pdmsystem@naraenano.com");
			fromHash.put("NAME", "PDM 관리자");

			HashMap toHash = new HashMap();
			for (int i = 0; i < toId.length; i++) {
				WTUser toUser = OrganizationServicesHelper.manager.getAuthenticatedUser(toId[i]);
				if (toUser.getEMail() == null)
					continue;
				toHash.put(toUser.getEMail(), toUser.getFullName());
			}

			Hashtable hash = new Hashtable();
			hash.put("SUBJECT", subject);
			hash.put("CONTENT", content);
			if (url != null) {
				url = WTProperties.getServerCodebase().toString() + url;
				url = URLEncoder.encode(url);
				hash.put("URL", url);
			}

			MailHtmlContentTemplate template = MailHtmlContentTemplate.getInstance();
			content = template.htmlContent(hash, "CommonMail.html");

			Hashtable mailHash = new Hashtable();
			mailHash.put("FROM", fromHash);
			mailHash.put("TO", toHash);
			mailHash.put("SUBJECT", subject);
			mailHash.put("CONTENT", content);
			EMailScheduler.createProcessItem(mailHash);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void mailObjMailSendSetting(ApprovalLine nextLine, Object object, HashMap toHash, String title,
			String msg, String mailType) throws Exception {
		mailObjMailSendSetting(nextLine, object, toHash, title, msg, mailType, false);
	}

	public static void mailObjMailSendSetting(ApprovalLine nextLine, Object object, HashMap toHash, String title,
			String msg, String mailType, boolean isWork) throws Exception {

		ConfigExImpl conf = ConfigEx.getInstance("eSolution");
		boolean enableMail = conf.getBoolean("e3ps.mail.enable", true);

		if (!enableMail)
			return;

		String subject = "";
		String number = "";

//		System.out.println("@@ call mailObjMailSendSetting()  title = " + title);
//		System.out.println("@@ call mailObjMailSendSetting()  msg = " + msg);
//		System.out.println("@@ call mailObjMailSendSetting() " + object.toString());
		ApprovalData appData = new ApprovalData((Persistable) object);

		subject = title;

		String hostName = WTProperties.getLocalProperties().getProperty("wt.httpgw.hostname");
		WTProperties.getLocalProperties().getProperty("wt.httpgw.hostname");
		String host = "http://" + hostName;

		String link = host;
		if (nextLine != null) {
			link += "/plm/jsp/workspace/approval/ViewListWork.jsp?view=toApprove&module=workspace&oid="
					+ nextLine.getPersistInfo().getObjectIdentifier().toString();
		} else {
			link += "/plm/jsp/workspace/approval/ViewListWork.jsp?view=toApprove&module=workspace&oid="
					+ ((Persistable) object).getPersistInfo().getObjectIdentifier().toString();
		}

//		System.out.println("host = " + host);
//		System.out.println("link = " + link);

		// 설명
		String text = msg;
		// setting mail content
		String content = getHtmlTemplate(mailType, link, subject, (String) toHash.get("fullName"),
				(String) toHash.get("title"), (String) toHash.get("createDate"), (String) toHash.get("creater"),
				isWork);
		Hashtable mailHash = new Hashtable();

		/*
		 * WTUser from = (WTUser)SessionHelper.manager.getPrincipal(); HashMap fromHash
		 * = new HashMap(); fromHash.put("EMAIL", from.getEMail()); fromHash.put("NAME",
		 * from.getFullName());
		 */

		HashMap fromHash = new HashMap();
		fromHash.put("EMAIL", "icubeadmin@dongbu.com");
		fromHash.put("NAME", "아이큐브 관리자");

		mailHash.put("FROM", fromHash);
		mailHash.put("TO", toHash);
		mailHash.put("SUBJECT", subject);
		mailHash.put("CONTENT", content);

		if (link != null) {
			link = URLEncoder.encode(link);
			mailHash.put("URL", link);
		}

//		EMailScheduler.createProcessItem(mailHash);

		boolean result = MailUtil.manager.sendMail(mailHash);
//		System.out.println("Mail 발송 : @@@@@@@@@@@@@@@@@@@@@@@@@@");
//		System.out.println("Mail 발송 : " + result);
	}

	public static String getHtmlTemplate(String mailType, String url, String subject, String author, String title,
			String startDate, String creator) throws Exception {
		return getHtmlTemplate(mailType, url, subject, author, title, startDate, creator, false);
	}

	public static String getHtmlTemplate(String mailType, String url, String subject, String author, String title,
			String startDate, String creator, boolean work) throws Exception {

		/**
		 * type : ApprovalMail <@url> <@subject> <@author> <@title> <@startDate>
		 * <@creator>
		 */

		MailHtmlContentTemplate template = MailHtmlContentTemplate.getInstance();

		StringBuffer content = new StringBuffer();

		String approve = "결재가";
		String approve2 = "결재";

		if (work) {
			approve = "작업이";
			approve2 = "작업";
		}

		if (mailType.equals("pressingApproval")) {
			content.append(approve + " 지연되고 있습니다.");
			content.append(" <BR> " + approve2 + " 요청에 대한 빠른 처리 바랍니다.");
		} else if (mailType.equals("requestApproval")) {
			content.append(approve + " 요청되었습니다.");
			content.append(" <BR> " + approve2 + " 요청에 대한 처리 바랍니다.");
		} else if (mailType.equals("create")) {
			content.append(approve + " 등록되었습니다.");
			content.append(" <BR> " + approve2 + "에 대한 확인 바랍니다.");
		} else {
			content.append(approve + " 완료 되었습니다.");
			content.append(" <BR> " + approve2 + " 완료에 대한 확인 바랍니다.");
		}

		Hashtable hash = new Hashtable();

		hash.put("SUBJECT", subject);
		hash.put("TO", author);
		hash.put("CONTENT", content.toString());
		hash.put("TITLE", title);
		hash.put("STARTDATE", startDate);
		hash.put("CREATOR", creator);
		if (url != null) {
			url = URLEncoder.encode(url);
			hash.put("URL", url);
		}

		return template.htmlContent(hash, "ApprovalMail.html");
	}

}
