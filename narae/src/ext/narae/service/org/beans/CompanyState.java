/**
 * @(#)	CompanyState.java
 * Copyright (c) e3ps. All rights reserverd
 * 
 * @version 1.00
 * @since jdk 1.4.2
 * @author Cho Sung Ok, jerred@e3ps.com
 */

package ext.narae.service.org.beans;

import java.net.URL;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import wt.util.WTProperties;

public class CompanyState {
	// private final static Config config = ConfigImpl.getInstance();
	public static Hashtable dutyTable = new Hashtable();
	public static Hashtable nameTable = new Hashtable();
	public static Vector dutyNameList = new Vector();
	public static Vector dutyCodeList = new Vector();
	public static URL defautlURL;
	public static String companyName;
	public static String ldapAdapter;
	public static String ldapUser;
	public static String ldapPassword;
	public static String ldapDirectoryInfo;
	static {
		try {
			defautlURL = new URL(WTProperties.getServerCodebase().toString() + "lgchem/portal/images/icon/noimage.gif");
		} catch (Exception e) {
		}

		companyName = "나래나노텍";
		String dutyNameStr = "대표이사;상무;전무;이사;부장;차장;과장;대리;주임;사원;";
		String dutyCodeStr = "DC_01;DC_02;DC_03;DC_04;DC_05;DC_06;DC_07;DC_08;DC_09;DC_10;";

		StringTokenizer nameToken = new StringTokenizer(dutyNameStr, ";");
		StringTokenizer codeToken = new StringTokenizer(dutyCodeStr, ";");
		while (nameToken.hasMoreTokens()) {
			String name = (String) nameToken.nextElement();
			String code = (String) codeToken.nextElement();
			dutyTable.put(code, name);
			nameTable.put(name, code);
			dutyNameList.add(name);
			dutyCodeList.add(code);
		}
		// ou=people,cn=AdministrativeLdap,cn=Windchill_9.1,o=e3ps
		// ou=people,cn=EnterpriseLdap,cn=Windchill_9.1,o=e3ps

		ldapAdapter = "com.naraenano.Ldap";
		ldapUser = "cn=Manager";
		ldapPassword = "ldapadmin";
		ldapDirectoryInfo = "ou=people,cn=AdministrativeLdap,cn=Windchill_9.1,o=ptc";
	}
}
