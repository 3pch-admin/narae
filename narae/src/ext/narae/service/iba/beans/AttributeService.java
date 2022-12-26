package ext.narae.service.iba.beans;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import wt.auth.SimpleAuthenticator;
import wt.enterprise.RevisionControlled;
import wt.fc.Persistable;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerTemplate;
import wt.method.MethodContext;
import wt.method.MethodServerException;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.pom.UnsupportedPDSException;
import wt.pom.WTConnection;
import wt.util.WTException;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;

import com.ptc.core.lwc.server.LWCNormalizedObject;

public class AttributeService implements Serializable, RemoteAccess {
	private static final String SERVER_CLASS = AttributeService.class.getName();
	
	public static Object getValue(Persistable persistable, String attrName) throws WTException {
        
		if (RemoteMethodServer.ServerFlag) {
            try {
                LWCNormalizedObject obj = new LWCNormalizedObject(persistable, null, Locale.getDefault(), null);
                obj.load(attrName);
                
                Object objValue = obj.get(attrName); 
                
                return objValue;
            } catch(WTException wte) {
            	// TODO: Write Log4J
                return null;
            }
        } else {
            Class[] argTypes = { Persistable.class, String.class};
            Object[] args = { persistable, attrName };
            try {
                return (Object)RemoteMethodServer.getDefault().invoke("getValue",SERVER_CLASS, null, argTypes, args);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                return null;
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
        }
    }
	
	public static HashMap<String, Object> getValue(Persistable persistable, List<String> attributes) throws WTException {
        
		if (RemoteMethodServer.ServerFlag) {
            try {
                LWCNormalizedObject obj = new LWCNormalizedObject(persistable, null, Locale.getDefault(), null);
                obj.load(attributes);
                
                HashMap<String, Object> returnValue = new HashMap<String, Object>();
                for( int index=0; index < attributes.size(); index++ ) {
                	returnValue.put(attributes.get(index), obj.get(attributes.get(index)));
                }
                
                return returnValue;
            } catch(WTException wte) {
            	// TODO: Write Log4J
                return null;
            }
        } else {
            Class[] argTypes = { Persistable.class, List.class};
            Object[] args = { persistable, attributes };
            try {
                return (HashMap<String, Object>)RemoteMethodServer.getDefault().invoke("getValue",SERVER_CLASS, null, argTypes, args);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                return null;
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
        }
    }
	
	public static boolean setValue(Persistable persistable, String attrName, Object value) throws WTException {
        boolean returnValue = false;
        
        if (RemoteMethodServer.ServerFlag) {
            try {
//                RevisionControlled workingCopy = null;
//                if ( persistable instanceof Workable ) {
//                    if ( !WorkInProgressHelper.isWorkingCopy((Workable)persistable) ) {
//                        if ( WorkInProgressHelper.isCheckedOut((Workable)persistable) ) {
//                            persistable = WorkInProgressHelper.service.workingCopyOf((Workable)persistable);
//                        } else {
//                        	// TODO: Set log4j and return value
//                        	// Set log4j
//                        	// return false;
//                            throw new WTException("The inputed object was not checked out for updating attribute.");
//                        }
//                    } 
//                }
            
                LWCNormalizedObject obj = new LWCNormalizedObject(persistable, null, Locale.getDefault(), null);
                obj.load(attrName);
                obj.set(attrName, value);
                obj.persist();
                
                obj.load(attrName);
                
                return true;
            } catch(WTException wte) {
                return false;
            }
        } else {
            Class[] argTypes = { Persistable.class, String.class, Object.class};
            Object[] args = { persistable, attrName, value };
            try {
                return ((Boolean)RemoteMethodServer.getDefault().invoke("setValue",SERVER_CLASS, null, argTypes, args)).booleanValue();
            } catch (RemoteException e) {
                return false;
            } catch (InvocationTargetException e) {
                return false;
            }
        }
    }
	
	public static boolean setValue(Persistable persistable, HashMap<String, Object> attributes) throws WTException {
        boolean returnValue = false;
        
        if (RemoteMethodServer.ServerFlag) {
            try {
//                RevisionControlled workingCopy = null;
//                if ( persistable instanceof Workable ) {
//                    if ( !WorkInProgressHelper.isWorkingCopy((Workable)persistable) ) {
//                        if ( WorkInProgressHelper.isCheckedOut((Workable)persistable) ) {
//                            persistable = WorkInProgressHelper.service.workingCopyOf((Workable)persistable);
//                        } else {
//                        	// TODO: Set log4j and return value
//                        	// Set log4j
//                        	// return false;
//                            throw new WTException("The inputed object was not checked out for updating attribute.");
//                        }
//                    } 
//                }
            
                LWCNormalizedObject obj = new LWCNormalizedObject(persistable, null, Locale.getDefault(), null);
                
                Set<String> keys = attributes.keySet();
                Iterator<String> arrayKeys = keys.iterator();
                obj.load(keys);
                
                String oneKey = null;
                while( arrayKeys.hasNext() ) {
                	oneKey = arrayKeys.next();
	                obj.set(oneKey, attributes.get(oneKey));
	                
                }
                obj.persist();
                obj.load(keys);
                
                return true;
            } catch(WTException wte) {
                return false;
            }
        } else {
            Class[] argTypes = { Persistable.class, HashMap.class};
            Object[] args = { persistable, attributes };
            try {
                return ((Boolean)RemoteMethodServer.getDefault().invoke("setValue",SERVER_CLASS, null, argTypes, args)).booleanValue();
            } catch (RemoteException e) {
                return false;
            } catch (InvocationTargetException e) {
                return false;
            }
        }
    }
	
	public static boolean changeContainerTemplate(WTContainer container, WTContainerTemplate template) {
		boolean returnValue = false;
		if( container == null || template == null) {
			return false;
		}
        
        if (RemoteMethodServer.ServerFlag) {
            try {
            	String containerOid = String.valueOf(container.getPersistInfo().getObjectIdentifier().getId());
            	String templateOid = String.valueOf(template.getPersistInfo().getObjectIdentifier().getId());
                saveTemplateToDB(containerOid, templateOid);               
                return true;
            } catch(WTException wte) {
                return false;
            } catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
        } else {
            Class[] argTypes = { WTContainer.class, WTContainerTemplate.class};
            Object[] args = { container, template };
            try {
                return ((Boolean)RemoteMethodServer.getDefault().invoke("changeContainerTemplate",SERVER_CLASS, null, argTypes, args)).booleanValue();
            } catch (RemoteException e) {
                return false;
            } catch (InvocationTargetException e) {
                return false;
            }
        }
	}
	
	private static void saveTemplateToDB(String containerId, String templateId) throws UnsupportedPDSException, UnknownHostException, SQLException {
		String sql = "update project2 set IDA3CONTAINERTEMPLATEREFEREN=" + templateId + " where ida2a2=" + containerId;
		
		WTConnection wtconnection = null;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		MethodContext methodcontext = getMethodContext();
		try {
			if (RemoteMethodServer.ServerFlag) {
				wtconnection = (WTConnection)methodcontext.getConnection();
				pstmt = wtconnection.prepareStatement(sql);
				
				pstmt.executeUpdate();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if ( rs != null ) {
				rs.close();
			}
			
			if ( pstmt != null ) {
				pstmt.close();
			}
		}
	}
	
	public static MethodContext getMethodContext() throws UnsupportedPDSException, UnknownHostException {
		MethodContext methodcontext = null;
		try {
			methodcontext = MethodContext.getContext();
		} catch ( MethodServerException methodserverexception) {
			RemoteMethodServer.ServerFlag = true;
			InetAddress inetaddress = InetAddress.getLocalHost();
			String s = inetaddress.getHostName();
			if ( s == null)
				s = inetaddress.getHostAddress();
			SimpleAuthenticator simpleauthenticator = new SimpleAuthenticator();
			methodcontext = new MethodContext(s, simpleauthenticator);
			methodcontext.setThread(Thread.currentThread());
			wt.pds.PDSIfc pdsifc = wt.pom.DataServicesRegistry.getDefault().getPdsFor("DEFAULT");
		}
		return methodcontext;
 	}

}
