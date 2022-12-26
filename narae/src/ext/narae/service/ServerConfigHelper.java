package ext.narae.service;

import wt.util.WTProperties;

public class ServerConfigHelper {
	private static String CODEBASE = null;
	private static String HOST = null;
	static {
		try {
			WTProperties wtproperties = WTProperties.getLocalProperties();
			CODEBASE = wtproperties.getProperty("wt.server.codebase", "");
			HOST = wtproperties.getProperty("java.rmi.server.hostname", "");
		} catch(Exception wte) {
			wte.printStackTrace();
		}
	}

	public static String getServerHostName() {
		return HOST;
	}
}
