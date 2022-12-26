package ext.narae.util.mail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import ext.narae.util.jdf.config.ConfigEx;
import ext.narae.util.jdf.config.ConfigExImpl;

/**
 * <pre>
*
*  JAVA 로 Mail 을 전송하는 Utility Class.
*  컴파일 및 실행을 위해서는 JAVA mail 패키지 ( mail.jar ) 와 
*  JAVA Activatiion Framework 의 패키지( activation.jar )가 필요하다. 
*
*  사용 방법 
* 
* SendMail mail = new SendMail() ;
*          
*  mail.setFromMailAddress("aaa@bbb.com","보내는사람이름");          
*          
*  mail.setToMailAddress("ccc@ddd.com", "TO로받는사람");
*  //mail.setCcMailAddress("eee@fff.com", "CC로받는사람");
*  //mail.setBccMailAddress("ggg@hhh.com", "BCC로받는사람");          
*     
*   mail.setSubject(" MAIL 제목을 넣으면 됩니다.");          
*          
*   String message = " Text 메일 메시지 내용 " ; 
*   String htmlMessage = "<html><font color=
'red'> HTML 메일 메시지 내용</font></html>" ; 	   
*   String[] fileNames = { "c:/attachFile1.zip","c:/attachFile2.txt"   } ;
*
*   mail.setHtmlAndFile(htmlMessage,fileNames);
*   //mail.setHtml(htmlMessage);
*   //mail.setText(message);  
*
*    mail.send();  // 메일 전송
 *
 * </pre>
 * 
 * @author 차종호
 * @version 1.0, 01/01/03
 */

public class SendMail {

	Message msg = null;
	Multipart mp = new MimeMultipart();

	// 사용시 이 파일의 설정 내용을 수정한후 컴파일 하거나
	// 별도의 Mail Configuration Class 로 관리하여도 좋다.

	private void setDefaultFromAddress() throws Exception {

		java.util.Properties props = new java.util.Properties();
		ConfigExImpl conf = ConfigEx.getInstance("eSolution");
		String sender = conf.getString("email.admin.mailTo");

		setFromMailAddress(sender, SendMailConfig.DEFAULT_SENDER_NAME);
	}

	private void setMimeMessage(Session session) throws Exception {
		msg = new MimeMessage(session);
		msg.setSentDate(new Date());
	}

	private Session getSession(boolean debug) throws Exception {
		java.util.Properties props = new java.util.Properties();
		// XXX - could use Session.getTransport() and Transport.connect()
		// XXX - assume we're using SMTP

		ConfigExImpl conf = ConfigEx.getInstance("eSolution");
		String host = conf.getString("mail.smtp.host");

		props.put("mail.smtp.host", host);
		// props.put("mail.smtp.host", SendMailConfig.MAIL_HOST);

		// Get a Session object
		Session session = null;
		String auth = conf.getString("mail.smtp.auth");
		if ("true".equals(auth)) {
			props.put("mail.smtp.auth", auth);
			String id = conf.getString("email.admin.name");
			String pw = conf.getString("email.admin.pw");
			MyAuthenticator authenticator = new MyAuthenticator(id, pw);
			session = Session.getDefaultInstance(props, authenticator);
		} else {
			session = Session.getDefaultInstance(props, null);
		}
		session.setDebug(debug);
		return session;
	}

	/**
	 * Constructor
	 */

	public SendMail() throws SendMailException {
		try {
			setMimeMessage(getSession(SendMailConfig.SESSION_DEBUG_MESSAGE_FLAG));
			setDefaultFromAddress();
		} catch (MessagingException e) {
			throw new SendMailException("[SendMail][constructor]" + e.getMessage(), e);
		} catch (Exception e) {
			throw new SendMailException("[SendMail][constructor]" + e.getMessage(), e);
		}
	}

	/**
	 *
	 * <pre>
	*   보내는 사람의 Mail 주소를 설정 한다.
	 * </pre>
	 * 
	 * @param mailAddress aaa@bbb.com 형식의 internet mail address
	 * @param name        보내는 사람 이름
	 * @return None
	 * @exception SendMailException
	 */

	public void setFromMailAddress(String mailAddress, String name) throws SendMailException {
		try {
			InternetAddress sender = new InternetAddress(mailAddress, name, "utf-8");
			msg.setFrom(sender);
		} catch (Exception e) {
			throw new SendMailException("[SendMail][setFromMailAddress]" + e.getMessage(), e);
		}
	}

	/**
	 *
	 * <pre>
	*   보내는 사람의 Mail 주소를 설정 한다.
	 * </pre>
	 * 
	 * @param mailAddress aaa@bbb.com 형식의 internet mail address
	 * @return None
	 * @exception SendMailException
	 */

	public void setFromMailAddress(String mailAddress) throws SendMailException {
		try {
			InternetAddress sender = new InternetAddress(mailAddress);
			msg.setFrom(sender);
		} catch (Exception e) {
			throw new SendMailException("[SendMail][setFromMailAddress]" + e.getMessage(), e);
		}
	}

	/**
	 *
	 * <pre>
	*   받는 사람(To) 의 Mail 주소를 설정 한다.
	 * </pre>
	 * 
	 * @param mailAddress aaa@bbb.com 형식의 internet mail address
	 * @param name        받는 사람 이름
	 * @return None
	 * @exception SendMailException
	 */
	public void setToMailAddress(String mailAddress, String name) throws SendMailException {
		setMailAddress(mailAddress, name, Message.RecipientType.TO);
	}

	/**
	 *
	 * <pre>
	*   받는 사람(Cc - 참조) 의 Mail 주소를 설정 한다.
	 * </pre>
	 * 
	 * @param mailAddress aaa@bbb.com 형식의 internet mail address
	 * @param name        받는 사람 이름
	 * @return None
	 * @exception SendMailException
	 */
	public void setCcMailAddress(String mailAddress, String name) throws SendMailException {
		setMailAddress(mailAddress, name, Message.RecipientType.CC);
	}

	/**
	 *
	 * <pre>
	*   받는 사람(Bcc - 비밀참조) 의 Mail 주소를 설정 한다.
	 * </pre>
	 * 
	 * @param mailAddress aaa@bbb.com 형식의 internet mail address
	 * @param name        받는 사람 이름
	 * @return None
	 * @exception SendMailException
	 */
	public void setBccMailAddress(String mailAddress, String name) throws SendMailException {
		setMailAddress(mailAddress, name, Message.RecipientType.BCC);
	}

	private void setMailAddress(String mailAddress, String name, Message.RecipientType type) throws SendMailException {
		try {
			InternetAddress[] recipients = { new InternetAddress(mailAddress, name, "utf-8") };
			msg.setRecipients(type, recipients);
		} catch (MessagingException e) {
			throw new SendMailException("[SendMail][setMailAddress]" + e.getMessage(), e);
		} catch (Exception e) {
			throw new SendMailException("[SendMail][setMailAddress]" + e.getMessage(), e);
		}
	}

	/**
	 *
	 * <pre>
	*   받는 사람(To) 의 Mail 주소를 설정 한다.
	 * </pre>
	 * 
	 * @param mailAddress aaa@bbb.com 형식의 internet mail address String Array
	 * @param name        받는 사람 이름의 String Array
	 * @return None
	 * @exception SendMailException
	 */

	public void setToMailAddress(String[] mailAddress, String[] name) throws SendMailException {
		setMailAddress(mailAddress, name, Message.RecipientType.TO);
	}

	/**
	 *
	 * <pre>
	*   받는 사람(Cc - 참조) 의 Mail 주소를 설정 한다.
	 * </pre>
	 * 
	 * @param mailAddress aaa@bbb.com 형식의 internet mail address String Array
	 * @param name        받는 사람 이름의 String Array
	 * @return None
	 * @exception SendMailException
	 */

	public void setCcMailAddress(String[] mailAddress, String[] name) throws SendMailException {
		setMailAddress(mailAddress, name, Message.RecipientType.CC);
	}

	/**
	 *
	 * <pre>
	*   받는 사람(Bcc - 비밀참조) 의 Mail 주소를 설정 한다.
	 * </pre>
	 * 
	 * @param mailAddress aaa@bbb.com 형식의 internet mail address String Array
	 * @param name        받는 사람 이름의 String Array
	 * @return None
	 * @exception SendMailException
	 */

	public void setBccMailAddress(String[] mailAddress, String[] name) throws SendMailException {
		setMailAddress(mailAddress, name, Message.RecipientType.BCC);
	}

	private void setMailAddress(String[] mailAddress, String[] name, Message.RecipientType type)
			throws SendMailException {
		try {

			InternetAddress[] recipients = new InternetAddress[mailAddress.length];
			for (int i = 0; i < mailAddress.length; i++) {
				recipients[i] = new InternetAddress(mailAddress[i], name[i]);
			}

			msg.setRecipients(type, recipients);

		} catch (MessagingException e) {
			throw new SendMailException("[SendMail][setMailAddress]" + e.getMessage(), e);
		} catch (Exception e) {
			throw new SendMailException("[SendMail][setMailAddress]" + e.getMessage(), e);
		}
	}

	/**
	 *
	 * <pre>
	*   받는 사람(To) 의 Mail 주소를 설정 한다.
	 * </pre>
	 * 
	 * @param mailAddress aaa@bbb.com 형식의 internet mail address
	 * @return None
	 * @exception SendMailException
	 */

	public void setToMailAddress(String mailAddress) throws SendMailException {
		setMailAddress(mailAddress, Message.RecipientType.TO);
	}

	/**
	 *
	 * <pre>
	*   받는 사람(Cc - 참조) 의 Mail 주소를 설정 한다.
	 * </pre>
	 * 
	 * @param mailAddress aaa@bbb.com 형식의 internet mail address
	 * @return None
	 * @exception SendMailException
	 */

	public void setCcMailAddress(String mailAddress) throws SendMailException {
		setMailAddress(mailAddress, Message.RecipientType.CC);
	}

	/**
	 *
	 * <pre>
	*   받는 사람(Bcc - 비밀참조) 의 Mail 주소를 설정 한다.
	 * </pre>
	 * 
	 * @param mailAddress aaa@bbb.com 형식의 internet mail address
	 * @return None
	 * @exception SendMailException
	 */
	public void setBccMailAddress(String mailAddress) throws SendMailException {
		setMailAddress(mailAddress, Message.RecipientType.BCC);
	}

	private void setMailAddress(String mailAddress, Message.RecipientType type) throws SendMailException {
		try {
			InternetAddress[] recipients = { new InternetAddress(mailAddress) };
			msg.setRecipients(type, recipients);
		} catch (MessagingException e) {
			throw new SendMailException("[SendMail][setMailAddress]" + e.getMessage(), e);
		} catch (Exception e) {
			throw new SendMailException("[SendMail][setMailAddress]" + e.getMessage(), e);
		}
	}

	/**
	 *
	 * <pre>
	*   받는 사람(To) 의 Mail 주소를 설정 한다.
	 * </pre>
	 * 
	 * @param mailAddress aaa@bbb.com 형식의 internet mail address String Array
	 * @return None
	 * @exception SendMailException
	 */

	public void setToMailAddress(String[] mailAddress) throws SendMailException {
		setMailAddress(mailAddress, Message.RecipientType.TO);
	}

	/**
	 *
	 * <pre>
	*   받는 사람(Cc - 참조) 의 Mail 주소를 설정 한다.
	 * </pre>
	 * 
	 * @param mailAddress aaa@bbb.com 형식의 internet mail address String Array
	 * @return None
	 * @exception SendMailException
	 */
	public void setCcMailAddress(String[] mailAddress) throws SendMailException {
		setMailAddress(mailAddress, Message.RecipientType.CC);
	}

	/**
	 *
	 * <pre>
	*   받는 사람(Bcc - 비밀참조) 의 Mail 주소를 설정 한다.
	 * </pre>
	 * 
	 * @param mailAddress aaa@bbb.com 형식의 internet mail address String Array
	 * @return None
	 * @exception SendMailException
	 */
	public void setBccMailAddress(String[] mailAddress) throws SendMailException {
		setMailAddress(mailAddress, Message.RecipientType.BCC);
	}

	private void setMailAddress(String[] mailAddress, Message.RecipientType type) throws SendMailException {
		try {

			InternetAddress[] recipients = new InternetAddress[mailAddress.length];
			for (int i = 0; i < mailAddress.length; i++) {
				recipients[i] = new InternetAddress(mailAddress[i]);
			}
			msg.setRecipients(type, recipients);

		} catch (MessagingException e) {
			throw new SendMailException("[SendMail][setMailAddress]" + e.getMessage(), e);
		} catch (Exception e) {
			throw new SendMailException("[SendMail][setMailAddress]" + e.getMessage(), e);
		}
	}

	/**
	 *
	 * <pre>
	*   설정된 메일을 전송한다.
	 * </pre>
	 * 
	 * @param None
	 * @return None
	 * @exception SendMailException
	 */
	public void send() throws SendMailException {
		try {
			Transport.send(msg);
		} catch (MessagingException e) {
			System.out.println("[SendMail][send] Error : " + e.getMessage());
			throw new SendMailException("[SendMail][send]" + e.getMessage(), e);
			// e.printStackTrace();
		}
	}

	/**
	 *
	 * <pre>
	*   메일 제목을 설정한다.
	 * </pre>
	 * 
	 * @param subject 메일 제목
	 * @return None
	 * @exception SendMailException
	 */
	public void setSubject(String subject) throws SendMailException {
		try {
			((MimeMessage) msg).setSubject(subject, "euc-kr");
		} catch (MessagingException e) {
			throw new SendMailException("[SendMail][setSubject]" + e.getMessage(), e);
		}
	}

	/**
	 *
	 * <pre>
	*   text/plain 의 메일 메시지 내용을 설정 한다.
	 * </pre>
	 * 
	 * @param textMessage 메일 내용
	 * @return None
	 * @exception SendMailException
	 */
	public void setText(String textMessage) throws SendMailException {
		try {
			msg.setContent(textMessage, SendMailConfig.PLAIN_CONTENT_TYPE);
			msg.setHeader(SendMailConfig.CONTENT_TRANSFER_ENCODING, SendMailConfig.CONTENT_TYPE);
			msg.saveChanges();
		} catch (MessagingException e) {
			throw new SendMailException("[SendMail][setText]" + e.getMessage(), e);
		} catch (Exception e) {
			throw new SendMailException("[SendMail][setText]" + e.getMessage(), e);
		}
	}

	/**
	 *
	 * <pre>
	*   text/html 의 메일 메시지 내용을 설정 한다.
	 * </pre>
	 * 
	 * @param htmlMessage 메일 내용
	 * @return None
	 * @exception SendMailException
	 */
	public void setHtml(String htmlMessage) throws SendMailException {
		try {
			msg.setDataHandler(
					new DataHandler(new ByteArrayDataSource2(htmlMessage, SendMailConfig.HTML_CONTENT_TYPE)));
			msg.setHeader(SendMailConfig.CONTENT_TRANSFER_ENCODING, SendMailConfig.CONTENT_TYPE);
			msg.saveChanges();
		} catch (MessagingException e) {
			throw new SendMailException("[SendMail][setHtml]" + e.getMessage(), e);
		} catch (Exception e) {
			throw new SendMailException("[SendMail][setHtml]" + e.getMessage(), e);
		}
	}

	private void attachFileSourceArray(MimeMultipart multiPart, String[] fileNames) throws Exception {
		MimeBodyPart[] fileBodyPartArray = null;

		if (fileNames == null) {
			fileBodyPartArray = new MimeBodyPart[0];
		} else {
			fileBodyPartArray = new MimeBodyPart[fileNames.length];
		}

		for (int i = 0; i < fileBodyPartArray.length; i++) {

			fileBodyPartArray[i] = new MimeBodyPart();
			FileDataSource fileSource = new FileDataSource(fileNames[i]);
			fileBodyPartArray[i].setDataHandler(new DataHandler(fileSource));
			fileBodyPartArray[i].setFileName(fileSource.getName());
			multiPart.addBodyPart(fileBodyPartArray[i]);
		}

	}

	/**
	 *
	 * <pre>
	*   text/plain 의 메일 메시지 내용과 첨부파일을  설정 한다.
	 * </pre>
	 * 
	 * @param textMessage 메일 내용
	 * @param fileName    첨부파일 이름
	 * @return None
	 * @exception SendMailException
	 */
	public void setTextAndFile(String textMessage, String fileName) throws Exception {
		setTextAndFile(textMessage, new String[] { fileName });
	}

	/**
	 *
	 * <pre>
	*   text/plain 의 메일 메시지 내용과 첨부파일을  설정 한다.
	 * </pre>
	 * 
	 * @param textMessage 메일 내용
	 * @param fileName    첨부파일 이름 String Array
	 * @return None
	 * @exception SendMailException
	 */
	public void setTextAndFile(String textMessage, String[] fileNames) throws SendMailException {
		try {

			MimeBodyPart textPart = new MimeBodyPart();
			textPart.setText(textMessage, "utf-8");

			MimeMultipart multiPart = new MimeMultipart();
			multiPart.addBodyPart(textPart);

			attachFileSourceArray(multiPart, fileNames);

			multiPart.setSubType("mixed");
			msg.setContent(multiPart);
			msg.saveChanges();

		} catch (MessagingException e) {
			throw new SendMailException("[SendMail][setTextAndFile]" + e.getMessage(), e);
		} catch (Exception e) {
			throw new SendMailException("[SendMail][setTextAndFile]" + e.getMessage(), e);
		}
	}

	/**
	 *
	 * <pre>
	*   text/html 의 메일 메시지 내용과 첨부파일을  설정 한다.
	 * </pre>
	 * 
	 * @param htmlMessage 메일 내용
	 * @param fileName    첨부파일 이름
	 * @return None
	 * @exception SendMailException
	 */
	public void setHtmlAndFile(String htmlMessage, String fileName) throws Exception {
		setHtmlAndFile(htmlMessage, new String[] { fileName });
	}

	/**
	 *
	 * <pre>
	*   text/html 의 메일 메시지 내용과 첨부파일을  설정 한다.
	 * </pre>
	 * 
	 * @param htmlMessage 메일 내용
	 * @param fileName    첨부파일 이름 String Array
	 * @return None
	 * @exception SendMailException
	 */
	public void setHtmlAndFile(String htmlMessage, String[] fileNames) throws SendMailException {
		try {

			MimeBodyPart htmlPart = new MimeBodyPart();
			htmlPart.setDataHandler(
					new DataHandler(new ByteArrayDataSource2(htmlMessage, SendMailConfig.HTML_CONTENT_TYPE)));
			htmlPart.setHeader(SendMailConfig.CONTENT_TRANSFER_ENCODING, SendMailConfig.CONTENT_TYPE);

			MimeMultipart multiPart = new MimeMultipart();
			multiPart.addBodyPart(htmlPart);

			attachFileSourceArray(multiPart, fileNames);

			multiPart.setSubType("mixed");
			msg.setContent(multiPart);
			msg.saveChanges();

		} catch (MessagingException e) {
			throw new SendMailException("[SendMail][setHtmlAndFile]" + e.getMessage(), e);
		} catch (Exception e) {
			throw new SendMailException("[SendMail][setHtmlAndFile]" + e.getMessage(), e);
		}
	}

}

// Java Mail demo  중의 	ByteArrayDataSource.java 의 소스 
/**
 * A simple DataSource for demonstration purposes. This class implements a
 * DataSource from: an InputStream a byte array a String
 *
 * @author John Mani
 * @author Bill Shannon
 * @author Max Spivak
 */
class ByteArrayDataSource2 implements DataSource {

	private byte[] data; // data
	private String type; // content-type

	/* Create a DataSource from an input stream */
	public ByteArrayDataSource2(InputStream is, String type) {
		this.type = type;
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			int ch;

			while ((ch = is.read()) != -1)
				// XXX - must be made more efficient by
				// doing buffered reads, rather than one byte reads
				os.write(ch);
			data = os.toByteArray();

		} catch (IOException ioex) {
		}
	}

	/* Create a DataSource from a byte array */
	public ByteArrayDataSource2(byte[] data, String type) {
		this.data = data;
		this.type = type;
	}

	/* Create a DataSource from a String */
	public ByteArrayDataSource2(String data, String type) {
		try {
			// Assumption that the string contains only ASCII
			// characters! Otherwise just pass a charset into this
			// constructor and use it in getBytes()
			// this.data = data.getBytes("iso-8859-1");

			this.data = data.getBytes("utf-8"); // 한글로 Encoding

		} catch (UnsupportedEncodingException uex) {
		}
		this.type = type;
	}

	/**
	 * Return an InputStream for the data. Note - a new stream must be returned each
	 * time.
	 */
	public InputStream getInputStream() throws IOException {
		if (data == null)
			throw new IOException("no data");
		return new ByteArrayInputStream(data);
	}

	public OutputStream getOutputStream() throws IOException {
		throw new IOException("cannot do this");
	}

	public String getContentType() {
		return type;
	}

	public String getName() {
		return "dummy";
	}

}
