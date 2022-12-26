package ext.narae.util.web;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;

import wt.fc.WTObject;
import wt.util.WTException;

public class CommonWebHelper implements wt.method.RemoteAccess, java.io.Serializable {

	static final boolean SERVER = wt.method.RemoteMethodServer.ServerFlag;

	public static String getIconImgTag(WTObject obj) throws WTException {

		if (!SERVER) {
			Class argTypes[] = new Class[] { WTObject.class };
			Object args[] = new Object[] { obj };
			try {
				return (String) wt.method.RemoteMethodServer.getDefault().invoke("getIconImgTag",
						"com.e3ps.common.web.CommonWebHelper", null, argTypes, args);
			} catch (RemoteException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new WTException(e);
			}
		}

		String returnData = "";
		try {
			returnData = wt.enterprise.BasicTemplateProcessor.getObjectIconImgTag(obj);
		} catch (Exception e) {
		}

		if (returnData != null)
			return returnData;
		else
			return "";
	}
};
