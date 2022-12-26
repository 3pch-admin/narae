package ext.narae.util;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Locale;
import java.util.TreeMap;
import java.util.Vector;

import javax.servlet.ServletRequest;

import ext.narae.util.jdf.config.ConfigEx;
import ext.narae.util.jdf.config.ConfigExImpl;
//import sun.security.action.GetPropertyAction;
import wt.content.ApplicationData;
import wt.content.ContentItem;
import wt.content.URLData;
import wt.fc.IconDelegate;
import wt.fc.IconDelegateFactory;
import wt.fc.ObjectIdentifier;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.fc.WTReference;
import wt.httpgw.URLFactory;
import wt.org.OrganizationServicesHelper;
import wt.org.WTUser;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.util.IconSelector;
import wt.util.WTContext;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.vc.VersionForeignKey;
import wt.vc.VersionReference;


public class CommonUtil  implements wt.method.RemoteAccess, java.io.Serializable {

	private static ReferenceFactory rf = null;
	static final boolean SERVER = wt.method.RemoteMethodServer.ServerFlag;
	
	public static String getObjectIconImageTag(WTObject object) throws Exception{

		if(!SERVER) {
			Class argTypes[] = new Class[]{WTObject.class};
			Object args[] = new Object[]{object};
			try {
				return (String)wt.method.RemoteMethodServer.getDefault().invoke(
						"getObjectIconImageTag",
						"com.e3ps.common.util.CommonUtil",
						null,
						argTypes,
						args);
			}
			catch(RemoteException e) {
				e.printStackTrace();
				throw new WTException(e);
			}
			catch(InvocationTargetException e) {
				e.printStackTrace();
				throw new WTException(e);
			}
			catch(Exception e){
				e.printStackTrace();
				throw new WTException(e);
			}
		}

		return wt.enterprise.BasicTemplateProcessor.getObjectIconImgTag(object);
	}

	public static String getContentIconStr(ContentItem item) throws WTException {
		URLFactory urlFac = new URLFactory ();
		String iconStr = "";
		String fileiconpath = "jsp/portal/images/icon/fileicon/";
		String filename = "";
		if (item instanceof URLData) {
			iconStr = urlFac.getBaseURL ().getPath () + fileiconpath + "link.gif";
		} else if (item instanceof ApplicationData) {
			ApplicationData data = (ApplicationData) item;

			String extStr = "";
			String tempFileName = data.getFileName ();
			filename = tempFileName;
			int dot = tempFileName.lastIndexOf ( "." );
			if (dot != -1) extStr = tempFileName.substring ( dot + 1 ); // includes
																									// "."

			if (extStr.equalsIgnoreCase ( "cc" )) iconStr = urlFac.getBaseURL ().getPath () + fileiconpath + "ed.gif";
			else if (extStr.equalsIgnoreCase ( "exe" )) iconStr = urlFac.getBaseURL ().getPath () + fileiconpath + "exe.gif";
			else if (extStr.equalsIgnoreCase ( "doc" )) iconStr = urlFac.getBaseURL ().getPath () + fileiconpath + "doc.gif";
			else if (extStr.equalsIgnoreCase ( "ppt" )) iconStr = urlFac.getBaseURL ().getPath () + fileiconpath + "ppt.gif";
			else if (extStr.equalsIgnoreCase ( "xls" )) iconStr = urlFac.getBaseURL ().getPath () + fileiconpath + "xls.gif";
			else if (extStr.equalsIgnoreCase ( "csv" )) iconStr = urlFac.getBaseURL ().getPath () + fileiconpath + "xls.gif";
			else if (extStr.equalsIgnoreCase ( "txt" )) iconStr = urlFac.getBaseURL ().getPath () + fileiconpath + "notepad.gif";
			else if (extStr.equalsIgnoreCase ( "mpp" )) iconStr = urlFac.getBaseURL ().getPath () + fileiconpath + "mpp.gif";
			else if (extStr.equalsIgnoreCase ( "pdf" )) iconStr = urlFac.getBaseURL ().getPath () + fileiconpath + "pdf.gif";
			else if (extStr.equalsIgnoreCase ( "tif" )) iconStr = urlFac.getBaseURL ().getPath () + fileiconpath + "tif.gif";
			else if (extStr.equalsIgnoreCase ( "gif" )) iconStr = urlFac.getBaseURL ().getPath () + fileiconpath + "gif.gif";
			else if (extStr.equalsIgnoreCase ( "jpg" )) iconStr = urlFac.getBaseURL ().getPath () + fileiconpath + "jpg.gif";
			else if (extStr.equalsIgnoreCase ( "ed" )) iconStr = urlFac.getBaseURL ().getPath () + fileiconpath + "ed.gif";
			else if (extStr.equalsIgnoreCase ( "zip" )) iconStr = urlFac.getBaseURL ().getPath () + fileiconpath + "zip.gif";
			else if (extStr.equalsIgnoreCase ( "tar" )) iconStr = urlFac.getBaseURL ().getPath () + fileiconpath + "zip.gif";
			else if (extStr.equalsIgnoreCase ( "rar" )) iconStr = urlFac.getBaseURL ().getPath () + fileiconpath + "zip.gif";
			else if (extStr.equalsIgnoreCase ( "jar" )) iconStr = urlFac.getBaseURL ().getPath () + fileiconpath + "zip.gif";
			else if (extStr.equalsIgnoreCase ( "igs" )) iconStr = urlFac.getBaseURL ().getPath () + fileiconpath + "epmall.gif";
			else if (extStr.equalsIgnoreCase ( "pcb" )) iconStr = urlFac.getBaseURL ().getPath () + fileiconpath + "epmall.gif";
			else if (extStr.equalsIgnoreCase ( "asc" )) iconStr = urlFac.getBaseURL ().getPath () + fileiconpath + "epmall.gif";
			else if (extStr.equalsIgnoreCase ( "dwg" )) iconStr = urlFac.getBaseURL ().getPath () + fileiconpath + "epmall.gif";
			else if (extStr.equalsIgnoreCase ( "dxf" )) iconStr = urlFac.getBaseURL ().getPath () + fileiconpath + "epmall.gif";
			else if (extStr.equalsIgnoreCase ( "sch" )) iconStr = urlFac.getBaseURL ().getPath () + fileiconpath + "epmall.gif";
			else if (extStr.equalsIgnoreCase ( "html" )) iconStr = urlFac.getBaseURL ().getPath () + fileiconpath + "htm.gif";
			else if (extStr.equalsIgnoreCase ( "htm" )) iconStr = urlFac.getBaseURL ().getPath () + fileiconpath + "htm.gif";
			else if (extStr.equalsIgnoreCase ( "docx" )) iconStr = urlFac.getBaseURL ().getPath () + fileiconpath + "doc.gif";
			else if (extStr.equalsIgnoreCase ( "pptx" )) iconStr = urlFac.getBaseURL ().getPath () + fileiconpath + "ppt.gif";
			else if (extStr.equalsIgnoreCase ( "xlsx" )) iconStr = urlFac.getBaseURL ().getPath () + fileiconpath + "xls.gif";
			else if (extStr.equalsIgnoreCase ( "bmp" )) iconStr = urlFac.getBaseURL ().getPath () + fileiconpath + "bmp.gif";
			else iconStr = urlFac.getBaseURL ().getPath () + fileiconpath + "generic.gif";
		}
        else
        {
            return null;
        }
		iconStr = "<img src='" + iconStr + "' border=0 alt='" + filename + "'>";
		return iconStr;
	}
	
	/**
	 * 객체 생성을 방지하기 위해서 디폴트 생성자를 Private로 선언
	 */
	private CommonUtil() {
	}

	/**
	 * 파라미터로 들어온 Persistable 객체의 OID 를 리턴하는 Method <br>
	 */
	public static String getOIDString(Persistable per) {
		if (per == null) return null;
		return PersistenceHelper.getObjectIdentifier ( per ).getStringValue ();
	}
	
	public static String getFullOIDString(Persistable persistable) {
        try {
            if (rf == null) rf = new ReferenceFactory();
            return rf.getReferenceString(rf.getReference(persistable));
        } catch (Exception e) {
            return null;
        }
    }
	
	public static long getOIDLongValue(String oid) {
		String tempoid = oid;
		tempoid = tempoid.substring ( tempoid.lastIndexOf ( ":" ) + 1 );
		return Long.parseLong ( tempoid );
	}

	public static long getOIDLongValue(Persistable per) {
		String tempoid = getOIDString ( per );
		tempoid = tempoid.substring ( tempoid.lastIndexOf ( ":" ) + 1 );
		return Long.parseLong ( tempoid );
	}
	
	  /**
     * VR oid를 리턴한다.
     * 
     * @param oid
     * @return
     */
    public static String getVROID(String oid)
    {
        Object obj = getObject(oid);
        if (obj == null)
            return null;
        return getVROID((Persistable) getObject(oid));
    }

    private static String getVRString(WTReference wtRef) throws WTException
    {
        VersionReference verRef = (VersionReference) wtRef;
        VersionForeignKey verForeignKey = (VersionForeignKey) verRef.getKey();
        return "VR:" + verRef.getKey().getClassname() + ":" + verForeignKey.getBranchId();
    }
    
    /**
     * VR oid를 리턴한다.
     * 
     * @param persistable
     * @return
     */
    public static String getVROID(Persistable persistable)
    {

        if (persistable == null)
            return null;
        try
        {
            if (rf == null)
                rf = new ReferenceFactory();
            return getVRString(rf.getReference(persistable));
        }
        catch (Exception e)
        {
            return null;
        }
    }
	/**
	 * OID로 객체를 찾아 리턴 한다 <br>
	 */
	public static Persistable getObject(String oid) {
		if (oid == null) return null;
		try {
			if (rf == null) rf = new ReferenceFactory ();
			return rf.getReference ( oid ).getObject ();
		} catch (Exception e) {
			//e.printStackTrace();
			return null;
		}
	}

	/**
	 * 접속한 계정이 Admin 그룹에 포함 되어 있는지를 알아낸다 <br>
	 */
	public static boolean isCheckPW() throws Exception {
		ConfigExImpl conf = ConfigEx.getInstance("eSolution");
		boolean enableCheckPW = conf.getBoolean("e3ps.checkpw.enable", true);
		
		return enableCheckPW;
	}
	
	/**
	 * 접속한 계정이 Admin 그룹에 포함 되어 있는지를 알아낸다 <br>
	 */
	public static boolean isAdmin() throws Exception {
		return isMember ( "Administrators" );
	}

	/**
	 * 접속한 계정이 Parameter로 넘어온 group 명의 그룹에 포함 되어 있는지를 알아낸다 <br>
	 */
	public static boolean isMember(String group) throws Exception {
		WTUser user = (wt.org.WTUser) SessionHelper.manager.getPrincipal();
		return isMember(group, user);
	}
    
    public static boolean isMember(String group, WTUser user) throws Exception {
        Enumeration en = user.parentGroupNames ();
        while (en.hasMoreElements ()) {
            String st = (String) en.nextElement ();
            if (st.equals ( group )) return true;
        }
        return false;
    }

	/**
	 * 시스템이 사용하는 임시 디렉토리의 절대 경로를 리턴하는 Method <br>
	 * 
	 * @return <code>java.lang.String</code> 시스템 임시 디렉토리
	 */
	public static String getTempDir() {
		//GetPropertyAction getpropertyaction = new GetPropertyAction ( "java.io.tmpdir" );
		//String tmpdir = (String) AccessController.doPrivileged ( getpropertyaction );
		String tmpdir = System.getProperty("java.io.tmpdir");
		return tmpdir;
	}
    
    public static String getWCTempDir(){
        String tmpdir = "";
        try {
            WTProperties properties = WTProperties.getLocalProperties();
            tmpdir = properties.getProperty("wt.temp");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tmpdir;
    }

	/**
	 * @param :
	 *                  String
	 * @return : String
	 * @author : PTC KOREA Yang Kyu
	 * @since : 2004.01
	 */
	public static WTUser findUserID(String userID) throws WTException {
		WTUser wtuser = (WTUser) OrganizationServicesHelper.manager.getAuthenticatedUser ( userID );
		return wtuser;
	}

	public static String getUserIDFromSession() throws WTException {
		return ( (WTUser) SessionHelper.manager.getPrincipal () ).getName ();
	}
	
	public static String getUserNameFromOid( String ida2a2 ) throws WTException {
		String oid = "wt.org.WTUser:" +ida2a2;
		WTUser wtuser = (WTUser)getObject(oid);
		
		if ( wtuser != null ){
			return findUserName(wtuser.getName());
		} else {
			
			return "";
		}
		
	}

	public static String getUsernameFromSession() throws WTException {
		return ( (WTUser) SessionHelper.manager.getPrincipal () )
				.getFullName ();
	}

	
	public static String findUserName(String userID) throws WTException {
		String userName = "";
		WTUser wtuser = (WTUser) OrganizationServicesHelper.manager.getAuthenticatedUser ( userID );
		userName = wtuser.getFullName ();
		return userName;
	}

	public static TreeMap getUsers() throws WTException {
		QuerySpec query = new QuerySpec ( WTUser.class );
		query.appendWhere ( new SearchCondition ( WTUser.class , "disabled" , "FALSE" ) );
		
		QueryResult result = PersistenceHelper.manager.find ( query );

		TreeMap userTree = new TreeMap ();
		while (result.hasMoreElements ()) {
			wt.org.WTUser wtuser = (wt.org.WTUser) result.nextElement ();
			userTree.put ( wtuser.getFullName () , wtuser.getName () );
		}
		return userTree;
	}

	public static Vector removeDuplicate(Vector duplicateVector)
			throws Exception {
		HashSet hashset = new HashSet ();
		Vector vec1 = new Vector ();

		for (int i = 0; i < duplicateVector.size (); i++) {
			Persistable persistable = (Persistable) duplicateVector.get ( i );
			ObjectIdentifier objectidentifier = PersistenceHelper	.getObjectIdentifier ( persistable );

			if (!hashset.contains ( objectidentifier )) {
				hashset.add ( objectidentifier );
				vec1.addElement ( persistable );
			}
		}
		return vec1;
	}

	public static String getIconResource(WTObject wtobject) throws Exception {
		String s = null;
		IconDelegateFactory icondelegatefactory = new IconDelegateFactory ();
		IconDelegate icondelegate = icondelegatefactory.getIconDelegate ( wtobject );
		IconSelector iconselector;
		for (iconselector = icondelegate.getStandardIconSelector (); !iconselector.isResourceKey (); iconselector = icondelegate.getStandardIconSelector ())
			icondelegate = icondelegate.resolveSelector ( iconselector );

		s = "/" + iconselector.getIconKey ();
		return s;
	}

    public static void printlnParamValues(ServletRequest request){
        
        Enumeration en = request.getParameterNames();
        Vector enVec = new Vector();
        while(en.hasMoreElements()){
            enVec.addElement(en.nextElement());
        }
        Collections.sort(enVec, ORDER);
        en = enVec.elements();
        while(en.hasMoreElements()){ 
            Object obj = en.nextElement();
            System.out.println( obj.toString() +"    -    "+request.getParameter(obj.toString()) );
        }
    }
    
    public static WTUser getWTUserFromID(String userID){
        if ( userID == null ){
            return null;
        } 
        try {
            return (WTUser) OrganizationServicesHelper.manager.getPrincipal(userID, OrganizationServicesHelper.manager.getDefaultDirectoryService());
        } catch (WTException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static final Comparator ORDER = new Comparator() {
          public int compare(Object obj1, Object obj2) {
              int ret = ( obj1.toString() ).compareTo( obj2.toString() );
              return ret;
          }
    };

    public static String ViewState(String state) {
        if( "작업 중".equals(state) || "INWORK".equals(state) || "APPROVING".equals(state) || "승인중".equals(state) || "CHANGE".equals(state) ) {
            return "INWORK";
        }else if( "DEVRELEASED".equals(state) ) {
            return "DEVRELEASED";
        }else if( "APPROVED".equals(state) || "승인됨".equals(state) || "RELEASED".equals(state) ) {
            return "APPROVED";
        }else {
            return state;
        }
    }
    
    public static String zeroFill( int value, int size ) throws WTException {
        String convert="";
        int maxSize = (int)Math.pow(10,size); //size=5 ??maxSize=100,000
        if(value >= maxSize) { 
            convert = Integer.toString(maxSize-1);
        }
        else {
            int seqnoSize = (Integer.toString(value)).length();
            for(int i=0;i<(size-seqnoSize);i++){
                convert +="0";
            }
            convert = convert + value;
        }// end if
        return convert;
    }
	
	
    public static boolean isUSLocale(){
    	try{
	    	Locale userLocale = SessionHelper.manager.getLocale();
	        if(userLocale == null) {
	            userLocale = WTContext.getContext().getLocale();
	        }
	        if(userLocale.equals(Locale.US)) {
	        	return true;
	        }
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    	return false;
    }
}
