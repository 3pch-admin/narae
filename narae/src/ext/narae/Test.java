package ext.narae;

import ext.narae.util.code.NumberCode;
import ext.narae.util.code.beans.NumberCodeHelper;

public class Test {

	public static void main(String[] args) throws Exception {

//		RemoteMethodServer.getDefault().setUserName("wcadmin");
//		RemoteMethodServer.getDefault().setPassword("n@r@epdm");

		
		NumberCode code = NumberCodeHelper.manager.getNumberCode("CADATTRIBUTE", "04");
		System.out.println(code.getName());
		

		System.exit(0);
	}

}
