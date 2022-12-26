/**
 * @(#) MyAuthenticator.java
 * Copyright (c) e3ps. All rights reserverd
 * 
 * @version 1.00
 * @since jdk 1.3
 * @author Cho Sung Ok, jerred@e3ps.com
 */
package ext.narae.util.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * 메일서버에 인증을 하기위한 Class
 */
public final class MyAuthenticator extends Authenticator {
	private String id;
	private String pw;

	/**
	 * @param id     <code>java.lang.String</code> 메일 서버 계정 ID
	 * @param passwd <code>java.lang.String</code> 메일 서버 계정 PASSWORD
	 */
	public MyAuthenticator(String id, String pw) {
		this.id = id;
		this.pw = pw;
	}

	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(id, pw);
	}
}
