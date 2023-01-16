package ext.narae;

import ext.narae.util.code.NumberCode;
import ext.narae.util.code.beans.NumberCodeHelper;
import wt.vc.VersionControlHelper;

public class Test {

	public static void main(String[] args) throws Exception {

//		RemoteMethodServer.getDefault().setUserName("wcadmin");
//		RemoteMethodServer.getDefault().setPassword("n@r@epdm");

		String s = "NP-MAAL-01-00001";

		int idx = s.lastIndexOf("-");
		s = s.substring(idx + 1);
		
		System.out.println(s);

		System.exit(0);
	}

}
