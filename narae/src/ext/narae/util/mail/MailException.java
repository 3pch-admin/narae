/**
 * @(#)	MailException.java
 * Copyright (c) e3ps. All rights reserverd
 * 
 * @version 1.00
 * @since jdk 1.3
 * @author Cho Sung Ok, jerred@e3ps.com
 */

package ext.narae.util.mail;

/**
 * e3ps.office.webmail Package에서 사용자 에러가 뜰경우에 생성되는 Exception
 */
public class MailException extends javax.mail.MessagingException {
	public MailException() {
		super();
	}

	public MailException(String message) {
		super(message);
	}
}
