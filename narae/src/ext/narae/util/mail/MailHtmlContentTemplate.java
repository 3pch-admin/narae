/**
 * @(#) MailHtmlContentTemplate.java
 * Copyright (c) e3ps. All rights reserverd
 * 
 *	@version 1.00
 *	@since jdk 1.4.02
 *	@createdate 2005. 11. 22.
 *	@author park, jai-sik
 *	@desc	
 */
package ext.narae.util.mail;

import java.util.Hashtable;

import ext.narae.util.jdf.config.ConfigEx;
import ext.narae.util.jdf.config.ConfigExImpl;

public class MailHtmlContentTemplate {
	protected java.util.Hashtable args = new java.util.Hashtable();
	private String htmlTemplateSource = null;

	private static MailHtmlContentTemplate instance = null;

	public static MailHtmlContentTemplate getInstance() {
		if (instance == null) {
			instance = new MailHtmlContentTemplate();
		}

		return instance;
	}

	public String htmlContent(Hashtable hash, String template) {
		String htmlContent = "";
		try {
			args = hash;

			if (template == null || template.equals("")) {
				template = "mail_notice.html";
			}
			setHtmlTemplate(template);

			if (htmlTemplateSource != null) {
				htmlContent = parseTemplate(htmlTemplateSource);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return htmlContent;
	}

	public void setHtmlTemplate(String template) {
		java.io.BufferedReader in = null;
		ConfigExImpl conf = ConfigEx.getInstance("eSolution");
		String templatePath = conf.getString("mail.template.path");
		try {

			java.io.File file = new java.io.File(templatePath + template);
			System.out.println("html template dir : " + file.getAbsolutePath());

			in = new java.io.BufferedReader(new java.io.FileReader(file));
			StringBuffer buf = new StringBuffer();
			String line;
			while ((line = in.readLine()) != null) {
				buf.append(line + "\n");
			}

			htmlTemplateSource = buf.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (Exception e) {

			}
		}
	}

	/**
	 * @return java.lang.String
	 * @param s java.lang.String
	 */
	private String parseTemplate(String s) {
		StringBuffer content = new StringBuffer();
		try {
			while (s.length() > 0) {
				int position = s.indexOf("<@");
				if (position == -1) {
					content.append(s);
					break;
				}

				if (position != 0)
					content.append(s.substring(0, position));

				if (s.length() == position + 2)
					break;
				String remainder = s.substring(position + 2);

				int markEndPos = remainder.indexOf(">");
				if (markEndPos == -1)
					break;

				String argname = remainder.substring(0, markEndPos).trim();
				String value = (String) args.get(argname);
				if (value != null)
					content.append(value);
				if (remainder.length() == markEndPos + 1)
					break;

				s = remainder.substring(markEndPos + 1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content.toString();
	}

	public void setArg(String name, String value) {
		args.put(name, value);
	}
}
