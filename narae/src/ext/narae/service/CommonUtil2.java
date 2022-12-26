package ext.narae.service;

import wt.fc.Persistable;
import wt.fc.ReferenceFactory;
import wt.fc.WTReference;
import wt.util.WTException;

public class CommonUtil2 {

	public static Persistable getInstance(String oid) throws WTException {
		ReferenceFactory referencefactory = new ReferenceFactory();
		WTReference wtreference = referencefactory.getReference( oid ); 
		return (Persistable)wtreference.getObject(); 
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String aa = "VR:wt.part.WTPart:246319$$$PTC$$$VR:wt.part.WTPart:246311$$$PTC$$$VR:wt.part.WTPart:246311";
		System.out.println(aa.split("$$$PTC$$$")[0]);
		System.out.println(aa.replace("$$$PTC$$$", "||").split("[|][|]")[0]);
	}

}
