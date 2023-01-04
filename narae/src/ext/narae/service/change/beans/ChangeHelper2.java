package ext.narae.service.change.beans;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import com.ptc.core.foundation.type.server.impl.TypeHelper;
import com.ptc.core.meta.common.TypeIdentifier;

import ext.narae.component.ApprovalLineVO;
import ext.narae.component.SerializableInputStream;
import ext.narae.service.CommonUtil2;
import ext.narae.service.ServerConfigHelper;
import ext.narae.service.approval.beans.ApprovalHelper2;
import ext.narae.service.iba.beans.AttributeHelper;
import ext.narae.service.org.People;
import ext.narae.service.workflow.beans.WorkflowHelper2;
import ext.narae.util.CommonUtil;
import ext.narae.util.DateUtil;
import ext.narae.util.SequenceDao;
import ext.narae.util.WCUtil;
import wt.admin.DomainAdministeredHelper;
import wt.change2.AddressedBy2;
import wt.change2.AffectedActivityData;
import wt.change2.ChangeNoticeComplexity;
import wt.change2.ChangeRecord2;
import wt.change2.Changeable2;
import wt.change2.RelevantRequestData2;
import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeOrder2Master;
import wt.change2.WTChangeOrder2MasterIdentity;
import wt.change2.WTChangeRequest2;
import wt.change2.WTChangeRequest2Master;
import wt.change2.WTChangeRequest2MasterIdentity;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.fc.EnumeratedTypeUtil;
import wt.fc.IdentityHelper;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.fc.WTReference;
import wt.inf.container.WTContained;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
import wt.log4j.LogR;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.pom.Transaction;
import wt.project.Role;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.team.RolePrincipalLink;
import wt.team.RolePrincipalMap;
import wt.team.Team;
import wt.team.TeamHelper;
import wt.team.TeamManaged;
import wt.team.TeamReference;
import wt.team.WTRoleHolder2;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.workflow.definer.ProcessDataInfo;
import wt.workflow.definer.WfDefinerHelper;
import wt.workflow.definer.WfProcessDefinition;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfState;

public class ChangeHelper2 implements Serializable, RemoteAccess {
	private static final Logger log = LogR.getLoggerInternal(ChangeHelper2.class.getName());
	private static String TEST_SERVER = "wc10.ptc.com";
	

	public static WTChangeRequest2 createECR(HashMap<String,Object> hash, boolean submitFlag)throws Exception{
		if(!RemoteMethodServer.ServerFlag) {
			Class argTypes[] = new Class[]{HashMap.class, boolean.class};
			Object args[] = new Object[]{hash, submitFlag};
			try {
				return (WTChangeRequest2)wt.method.RemoteMethodServer.getDefault().invoke(
						"createECR",
						ChangeHelper2.class.getName(),
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
				throw e;
			}
		}
		
		log.debug("===== Srart createECR: =====");
		Transaction trx = new Transaction();
		WTChangeRequest2 change = null;
		try {
           trx.start();
           
           WTContainerRef containerRef = WCUtil.getWTContainerRefForPart();
//           change = WTChangeRequest2.newWTChangeRequest2();
//           TypeIdentifier objType = TypeHelper.getTypeIdentifier("WCTYE|wt.change2.WTChangeRequest2|ext.narae.ECR");
//           System.out.println("=================>" + objType);
//           TypeHelper.setType(change, objType);
           
           TypeIdentifier objType = TypeHelper.getTypeIdentifier("WCTYPE|wt.change2.WTChangeRequest2|ext.narae.ECR");
           change = (WTChangeRequest2)TypeHelper.newInstance(objType);
			
           String name =  ((String)hash.get("name") != null)?URLDecoder.decode((String)hash.get("name"), "utf-8"):"";
           String requestType =  ((String)hash.get("purpose") != null)?URLDecoder.decode((String)hash.get("purpose"), "utf-8"):"";
           String requestor =  ((String)hash.get("requestor") != null)?URLDecoder.decode((String)hash.get("requestor"), "utf-8"):"";
           String supportor =  ((String)hash.get("supporter") != null)?URLDecoder.decode((String)hash.get("supporter"), "utf-8"):"";
           String projectName =  ((String)hash.get("projectName") != null)?URLDecoder.decode((String)hash.get("projectName"), "utf-8"):"";
           String problem =  ((String)hash.get("problem") != null)?URLDecoder.decode((String)hash.get("problem"), "utf-8"):"";
           String solution =  ((String)hash.get("solution") != null)?URLDecoder.decode((String)hash.get("solution"), "utf-8"):"";
           String projectNumber = ((String)hash.get("prjNo") != null)?(String)hash.get("partListOid"):"";
           String partOid = ((String)hash.get("partListOid") != null)?URLDecoder.decode((String)hash.get("partListOid"), "utf-8"):"";
           String worker = ((String)hash.get("worker") != null)?(String)hash.get("worker"):"";
           
           String agree = ((String)hash.get("agree") != null)?(String)hash.get("agree"):"";
           String approve = ((String)hash.get("approve") != null)?(String)hash.get("approve"):"";
           String receive = ((String)hash.get("receive") != null)?(String)hash.get("receive"):"";
           
           List<String> secondaryFileName = (hash.get("secondaryFileName") != null)?(List<String>)hash.get("secondaryFileName"):new ArrayList<String>();
           List<SerializableInputStream> secondary = (hash.get("secondary") != null)?(List<SerializableInputStream>)hash.get("secondary"):new ArrayList<SerializableInputStream>();
           HashMap<String,String> secondaryDelFile = (hash.get("secondaryDelFile") != null)?(HashMap<String,String>)hash.get("secondaryDelFile"):new HashMap<String,String>();
           
           /***** TEST PRINT *****/
           System.out.println("name = " + name);
           System.out.println("requestType = " + requestType);
           System.out.println("requestor = " + requestor);
           System.out.println("supportor = " + supportor);
           System.out.println("projectName = " + projectName);
           System.out.println("solution = " + solution);
           System.out.println("projectNumber = " + projectNumber);
           System.out.println("partOid = " + partOid);
           System.out.println("problem = " + problem);
           System.out.println("agree = " + agree);
           System.out.println("approve = " + approve);
           System.out.println("receive = " + receive);
            
           String number = "ECR-" + DateUtil.getCurrentDateString("m") + "-";
           String seqNo = SequenceDao.manager.getSeqNo(number, "0000", "WTChangeRequest2Master", "WTCHGREQUESTNUMBER");
           number = number + seqNo;
			
           change.setNumber(number);
           change.setName(name);
           change.setDescription(problem);
           //change.setProposedSolution(solution);
           change.setContainerReference(containerRef);	
			
           // Set Team
           Team team = Team.newTeam();
           DomainAdministeredHelper.setAdminDomain(team, ((WTContainer)containerRef.getObject()).getSystemDomainReference());
           team.setName(number+"_Team");
           team.setEnabled(true);
           team = (Team)PersistenceHelper.manager.save(team);
           
           if( agree != null && agree.length() > 0 ) {
        	   makeRoleMap(team, "CHANGE ADMINISTRATOR I", agree); //AGREE APPROVER RECIPIENT
           }
           if( approve != null && approve.length() > 0 ) {
        	   makeRoleMap(team, "CHANGE ADMINISTRATOR II", approve); //AGREE APPROVER RECIPIENT
           }
           if( receive != null && receive.length() > 0 ) {
        	   makeRoleMap(team, "CHANGE ADMINISTRATOR III", receive); //AGREE APPROVER RECIPIENT
           }
           
           // worker
           if( worker != null && worker.trim().length() > 0 ) {
//	           People workerPeople = (People)CommonUtil2.getInstance(worker);
//	           WTUser workerUser = workerPeople.getUser();
        	   WTUser workerUser = (WTUser)CommonUtil2.getInstance(worker);
	           makeRoleMap(team, "SUPPORT ENGINEER", workerUser.getPersistInfo().getObjectIdentifier().getStringValue());
           }
           
	       change = (WTChangeRequest2)TeamHelper.setTeam(change, team);
           
           // Set LC
           LifeCycleHelper.setLifeCycle(change, LifeCycleHelper.service.getLifeCycleTemplate("Narae_ECM_LC", containerRef)); //Lifecycle
			
           // Set Location
           //FolderHelper.assignLocation(change, cabinet);
			
           change = (WTChangeRequest2)PersistenceHelper.manager.save(change);
           change = (WTChangeRequest2)PersistenceHelper.manager.refresh(change);
			
           HashMap<String,Object> iba = new HashMap<String,Object>();
           iba.put("EC_Reason", requestType);
           iba.put("supporter", supportor);
           iba.put("Project", projectName);
           iba.put("description2", solution);
			
           // Set IBA
           AttributeHelper.service.setValue(change, iba);
			
           // Set releated Part
           setRelevantData(change,partOid);
			
           //Attachment
           if( secondaryFileName.size() > 0 ) {
        	   attachSecondary(change, secondaryFileName, secondary, secondaryDelFile);
           }
			
           // Start Workflow
           if( submitFlag == true ) {
        	   submitECR(change);
        	   // change state
        	   if( !ServerConfigHelper.getServerHostName().equals(TEST_SERVER) ) {
        		   if( agree != null && agree.trim().length() > 0 ) {
        			   LifeCycleHelper.service.setLifeCycleState(change, State.toState("CHECKWAIT"));
        		   } else {
        			   LifeCycleHelper.service.setLifeCycleState(change, State.toState("APPROVEWAIT"));
        		   }
        	   } else {
        		   LifeCycleHelper.service.setLifeCycleState(change, State.toState("REVIEWED"));
        	   }
           }
		     
           trx.commit();
           trx = null;
       } catch(Exception e) {
    	   e.printStackTrace();
           return null;
       } finally {
           if(trx!=null){
				trx.rollback();
		   }
       }
		return change;
	}
	
	public static WTChangeRequest2 updateECR(HashMap<String,Object> hash, boolean submitFlag)throws Exception {
		if(!RemoteMethodServer.ServerFlag) {
			Class argTypes[] = new Class[]{HashMap.class, boolean.class};
			Object args[] = new Object[]{hash, submitFlag};
			try {
				return (WTChangeRequest2)wt.method.RemoteMethodServer.getDefault().invoke(
						"createECR",
						ChangeHelper2.class.getName(),
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
				throw e;
			}
		}
		
		log.debug("===== Srart updateECR: =====");
		Transaction trx = new Transaction();
		WTChangeRequest2 targetObject = null;
		try {
			trx.start();
			log.debug("트랜젝션이 시작이 되었습니다." );
           
			log.debug("업데이트 타겟 Oid=" + (String)hash.get("oid"));
			targetObject = (WTChangeRequest2)CommonUtil2.getInstance((String)hash.get("oid"));
			log.debug("수정할 오브젝트: " + targetObject);
			
           String oid =  ((String)hash.get("oid") != null)?(String)hash.get("oid"):"";
           String name =  ((String)hash.get("name") != null)?URLDecoder.decode((String)hash.get("name"), "utf-8"):"";
           String requestType =  ((String)hash.get("purpose") != null)?URLDecoder.decode((String)hash.get("purpose"), "utf-8"):"";
           String requestor =  ((String)hash.get("requestor") != null)?URLDecoder.decode((String)hash.get("requestor"), "utf-8"):"";
           String supportor =  ((String)hash.get("supporter") != null)?URLDecoder.decode((String)hash.get("supporter"), "utf-8"):"";
           String projectName =  ((String)hash.get("projectName") != null)?URLDecoder.decode((String)hash.get("projectName"), "utf-8"):"";
           String problem =  ((String)hash.get("problem") != null)?URLDecoder.decode((String)hash.get("problem"), "utf-8"):"";
           String solution =  ((String)hash.get("solution") != null)?URLDecoder.decode((String)hash.get("solution"), "utf-8"):"";
           String projectNumber = ((String)hash.get("prjNo") != null)?(String)hash.get("partListOid"):"";
           String partOid = ((String)hash.get("partListOid") != null)?URLDecoder.decode((String)hash.get("partListOid"), "utf-8"):"";
           String worker = ((String)hash.get("worker") != null)?(String)hash.get("worker"):"";
           
           String agree = ((String)hash.get("agree") != null)?(String)hash.get("agree"):"";
           String approve = ((String)hash.get("approve") != null)?(String)hash.get("approve"):"";
           String receive = ((String)hash.get("receive") != null)?(String)hash.get("receive"):"";
           
           List<String> secondaryFileName = (hash.get("secondaryFileName") != null)?(List<String>)hash.get("secondaryFileName"):new ArrayList<String>();
           List<SerializableInputStream> secondary = (hash.get("secondary") != null)?(List<SerializableInputStream>)hash.get("secondary"):new ArrayList<SerializableInputStream>();
           HashMap<String,String> secondaryDelFile = (hash.get("secondaryDelFile") != null)?(HashMap<String,String>)hash.get("secondaryDelFile"):new HashMap<String,String>();
           
           /***** TEST PRINT *****/
           System.out.println("oid = " + oid);
           System.out.println("name = " + name);
           System.out.println("requestType = " + requestType);
           System.out.println("requestor = " + requestor);
           System.out.println("supportor = " + supportor);
           System.out.println("projectName = " + projectName);
           System.out.println("solution = " + solution);
           System.out.println("projectNumber = " + projectNumber);
           System.out.println("partOid = " + partOid);
           System.out.println("problem = " + problem);
           System.out.println("agree = " + agree);
           System.out.println("approve = " + approve);
           System.out.println("receive = " + receive);
           
           boolean changed = false;
           if( !name.equals(targetObject.getName())) {
        	   WTChangeRequest2Master master = (WTChangeRequest2Master)(targetObject.getMaster());

        	   WTChangeRequest2MasterIdentity identity = (WTChangeRequest2MasterIdentity)master.getIdentificationObject();
        	   identity.setName(name);
        	   IdentityHelper.service.changeIdentity(master, identity);
        	   changed = true;
           }
           if( !problem.equals(targetObject.getDescription())) {
        	   targetObject.setDescription(problem);
        	   changed = true;
           }
           //change.setProposedSolution(solution);
           
           if( changed ) {
        	   PersistenceServerHelper.manager.update(targetObject);
        	   targetObject = (WTChangeRequest2)PersistenceHelper.manager.refresh(targetObject);
           }
			
           // Set Team
           ApprovalLineVO approvalLine = ApprovalHelper2.getApprovalLine((TeamManaged)targetObject);
           Team team = TeamHelper.service.getTeam(targetObject);
           
           if( approvalLine != null ){
        	   ArrayList alist2 = new ArrayList();
        	   List<WTUser> alist3 = approvalLine.getChangManager1();
        	   List<WTUser> alist4 = approvalLine.getChangManager2();			
        	   List<WTUser> alist5 = approvalLine.getChangManager3();
        	   
        	   Map roleMaps = team.getRolePrincipalMap();
        	   
        	   String managerUsers = "";
        	   List<WTUser> addUser = null;
        	   List<WTUser> deleteUser = null;
        	   Role targetRole = null;
        	 //if( alist3 != null && alist3.size() > 0 ) {
    		   System.out.println("-----------Check CHANGE ADMINISTRATOR I");
    		   targetRole = Role.toRole("CHANGE ADMINISTRATOR I");
    		   if( alist3 != null && alist3.size() > 0 ) {
        		   deleteUser = checkDeleteUser(alist3, agree);
        		   System.out.println("-----------deleted:" + ((deleteUser == null)?0:deleteUser.size()) );
        		   if( deleteUser != null && deleteUser.size() > 0 ) {
        			   for( WTUser oneUser:deleteUser ) {
        				   team.deletePrincipalTarget(targetRole, oneUser);
        			   }
        		   }
    		   }
    		   if( agree != null && agree.length() > 0 ) {
        		   addUser = checkAddUser(alist3, agree);
        		   System.out.println("-----------added:" + ((addUser == null)?0:addUser.size()));
        		   if( addUser != null && addUser.size() > 0 ) {
        			   for( WTUser oneUser:addUser ) {
        				   team.addPrincipal(targetRole, oneUser);
        			   }
        		   }
    		   }
    	   //}
    	   
    	   //if( alist4 != null && alist4.size() > 0 ) {
    		   targetRole = Role.toRole("CHANGE ADMINISTRATOR II");
    		   if( alist4 != null && alist4.size() > 0 ) {
        		   deleteUser = checkDeleteUser(alist4, approve);
        		   if( deleteUser != null && deleteUser.size() > 0 ) {
        			   for( WTUser oneUser:deleteUser ) {
        				   team.deletePrincipalTarget(targetRole, oneUser);
        			   }
        		   }
    		   }
    		   if( approve != null && approve.length() > 0 ) {
        		   addUser = checkAddUser(alist4, approve);
        		   if( addUser != null && addUser.size() > 0 ) {
        			   for( WTUser oneUser:addUser ) {
        				   team.addPrincipal(targetRole, oneUser);
        			   }
        		   }
    		   }
    	   //}
    	   
    	   //if( alist5 != null && alist5.size() > 0 ) {
    		   targetRole = Role.toRole("CHANGE ADMINISTRATOR III");
    		   if( alist5 != null && alist5.size() > 0 ) {
        		   deleteUser = checkDeleteUser(alist5, receive);
        		   if( deleteUser != null && deleteUser.size() > 0 ) {
        			   for( WTUser oneUser:deleteUser ) {
        				   team.deletePrincipalTarget(targetRole, oneUser);
        			   }
        		   }
    		   }
    		   if( receive != null && receive.length() > 0 ) {
        		   addUser = checkAddUser(alist5, receive);
        		   if( addUser != null && addUser.size() > 0 ) {
        			   for( WTUser oneUser:addUser ) {
        				   team.addPrincipal(targetRole, oneUser);
        			   }
        		   }
    		   }
    	   //}
           }
           
           // worker
           if( worker != null && worker.trim().length() > 0 ) {
	           team.deleteRole(Role.toRole("SUPPORT ENGINEER"));
	           People workerPeople = (People)CommonUtil2.getInstance(worker);
	           WTUser workerUser = workerPeople.getUser();
	           makeRoleMap(team, "SUPPORT ENGINEER", workerUser.getPersistInfo().getObjectIdentifier().getStringValue());
           }
           
           List<String> iba = new ArrayList<String>();
           iba.add("EC_Reason");
           iba.add("supporter");
           iba.add("Project");
           iba.add("description2");
           HashMap<String,Object> settingIBA = new HashMap<String,Object>();
			
			// Set IBA
			HashMap<String,Object> originalValue = AttributeHelper.service.getValue(targetObject, iba);
			String checkValue = null;
			
			checkValue = (String)originalValue.get("EC_Reason");
			if( checkValue != null ) {
				if( !requestType.equals(checkValue)) {
					settingIBA.put("EC_Reason", requestType);
				}
			}
			
			checkValue = (String)originalValue.get("supporter");
			if( checkValue != null ) {
				if( !supportor.equals(checkValue)) {
					settingIBA.put("supporter", supportor);
				}
			}
			
			checkValue = (String)originalValue.get("Project");
			if( checkValue != null ) {
				if( !projectName.equals(checkValue)) {
					settingIBA.put("Project", projectName);
				}
			}
			
			checkValue = (String)originalValue.get("description2");
			if( checkValue != null ) {
				if( !solution.equals(checkValue)) {
					settingIBA.put("description2", solution);
				}
			}
			
			if( settingIBA.size() > 0  ) {
				AttributeHelper.service.setValue(targetObject, settingIBA);
			}
			
			// Set releated Part
			setRelevantData(targetObject,partOid);
			
			//Attachment
			attachSecondary(targetObject, secondaryFileName, secondary, secondaryDelFile);
			
			// Start Workflow
			if( submitFlag == true ) {
				submitECR(targetObject);
				// change state
				if( !ServerConfigHelper.getServerHostName().equals(TEST_SERVER) ) {
					if( agree != null && agree.trim().length() > 0 ) {
						LifeCycleHelper.service.setLifeCycleState(targetObject, State.toState("CHECKWAIT"));
					} else {
						LifeCycleHelper.service.setLifeCycleState(targetObject, State.toState("APPROVEWAIT"));
					}
				} else {
					LifeCycleHelper.service.setLifeCycleState(targetObject, State.toState("REVIEWED"));
				}
			}
		     
			trx.commit();
			trx = null;
       } catch(Exception e) {
    	   e.printStackTrace();
           return null;
       } finally {
           if(trx!=null){
				trx.rollback();
		   }
       }
		return targetObject;
	}
	
	public static WTChangeOrder2 createECO(HashMap<String,Object> hash, boolean submitFlag)throws Exception{
		/**** TEST ****
		hash = new HashMap<String,Object>();
		hash.put("name", URLEncoder.encode("Test001", "UTF-8"));
		hash.put("changeType", URLEncoder.encode("A,B", "UTF-8"));
		hash.put("projectName", URLEncoder.encode("123456", "UTF-8"));
		hash.put("problem", URLEncoder.encode("AAAAAAAAA", "UTF-8"));
		hash.put("solution", URLEncoder.encode("BBBBBBBB", "UTF-8"));
		hash.put("relatedECR", URLEncoder.encode("", "UTF-8"));
		hash.put("beforeOid", URLEncoder.encode("VR:wt.part.WTPart:191526,VR:wt.part.WTPart:214028", "UTF-8"));
		hash.put("afterOid", URLEncoder.encode("VR:wt.part.WTPart:191526,VR:wt.part.WTPart:214028", "UTF-8"));
		**** TEST ****/
		
		if(!RemoteMethodServer.ServerFlag) {
			Class argTypes[] = new Class[]{HashMap.class, boolean.class};
			Object args[] = new Object[]{hash, submitFlag};
			try {
				return (WTChangeOrder2)wt.method.RemoteMethodServer.getDefault().invoke(
						"createECO",
						ChangeHelper2.class.getName(),
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
				throw e;
			}
		}
		
		System.out.println("===== Srart createECO =====");
		log.debug("===== Srart createECO =====");
		Transaction trx = new Transaction();
		WTChangeOrder2 change = null;
		try {
			trx.start();
           
			WTContainerRef containerRef = WCUtil.getWTContainerRefForPart();
           
			TypeIdentifier objType = TypeHelper.getTypeIdentifier("WCTYPE|wt.change2.WTChangeOrder2|ext.narae.ECO");
			change = (WTChangeOrder2)TypeHelper.newInstance(objType);
			
			String name =  ((String)hash.get("name") != null)?URLDecoder.decode((String)hash.get("name"), "utf-8"):"";
	        String purpose =  ((String)hash.get("purpose") != null)?(String)hash.get("purpose"):"";
	        String projectName =  ((String)hash.get("projectName") != null)?URLDecoder.decode((String)hash.get("projectName"), "utf-8"):"";
	        String reason =  ((String)hash.get("reason") != null)?URLDecoder.decode((String)hash.get("reason"), "utf-8"):" ";
	        String measures =  ((String)hash.get("measures") != null)?URLDecoder.decode((String)hash.get("measures"), "utf-8"):"";
	           
	        String ecrListOid = ((String)hash.get("ecrListOid") != null)?(String)hash.get("ecrListOid"):"";
	        String beforeOid = ((String)hash.get("partTopListOid") != null)?(String)hash.get("partTopListOid"):"";
	        String afterOid = ((String)hash.get("partListOid") != null)?(String)hash.get("partListOid"):"";
	        String agree = ((String)hash.get("agree") != null)?(String)hash.get("agree"):"";
	        String approve = ((String)hash.get("approve") != null)?(String)hash.get("approve"):"";
	        String receive = ((String)hash.get("receive") != null)?(String)hash.get("receive"):"";
           
            List<String> secondaryFileName = (hash.get("secondaryFileName") != null)?(List<String>)hash.get("secondaryFileName"):new ArrayList<String>();
            List<SerializableInputStream> secondary = (hash.get("secondary") != null)?(List<SerializableInputStream>)hash.get("secondary"):new ArrayList<SerializableInputStream>();
            HashMap<String,String> secondaryDelFile = (hash.get("secondaryDelFile") != null)?(HashMap<String,String>)hash.get("secondaryDelFile"):new HashMap<String,String>();
            log.debug("1. Complete getting parameter");
			
            String number = "ECO-" + DateUtil.getCurrentDateString("m") + "-";
			String seqNo = SequenceDao.manager.getSeqNo(number, "0000", "WTChangeOrder2Master", "WTCHGORDERNUMBER");
			number = number + seqNo;
			
			log.debug("2. Complete get ECO Number:" + number);
			change.setNumber(number);
			change.setName(name);
			change.setDescription(reason);
			change.setContainerReference(containerRef);
			
			/***** TEST PRINT *****/
			log.debug("agree = " + agree);
			log.debug("approve = " + approve);
			log.debug("receive = " + receive);
			System.out.println("agree = " + agree);
			System.out.println("approve = " + approve);
			System.out.println("receive = " + receive);
	           
			// Internal Default
			Calendar ca = Calendar.getInstance();
	        java.util.Date today = ca.getTime();
	        change.setNeedDate(new Timestamp(today.getTime()));
	        change.setChangeNoticeComplexity(ChangeNoticeComplexity.BASIC);
	        log.debug("3. Complete Initial setting");
	        
			// Set Team
	        Team team = Team.newTeam();
	        DomainAdministeredHelper.setAdminDomain(team, ((WTContainer)containerRef.getObject()).getSystemDomainReference());
	        team.setName(number+"_Team");
	        team.setEnabled(true);
	        team = (Team)PersistenceHelper.manager.save(team);
	        log.debug("3-1. Complete create new team:" + number+"_Team");
	        
	        if( agree != null && agree.length() > 0 ) {
	        	log.debug("3-2. Exist & set agree:" + agree);
	        	makeRoleMap(team, "CHANGE ADMINISTRATOR I", agree); //AGREE APPROVER RECIPIENT
	        	log.debug("3-3. Complete set agree:" + agree);
	        }
	        if( approve != null && approve.length() > 0 ) {
	        	log.debug("3-4. Exist & set approve:" + approve);
	        	makeRoleMap(team, "CHANGE ADMINISTRATOR II", approve); //AGREE APPROVER RECIPIENT
	        	log.debug("3-5. Complete set approve:" + approve);
	        }
	        if( receive != null && receive.length() > 0 ) {
	        	log.debug("3-6. Exist & set receive:" + receive);
	        	makeRoleMap(team, "CHANGE ADMINISTRATOR III", receive); //AGREE APPROVER RECIPIENT
	        	log.debug("3-7. Complete set receive:" + receive);
	        }
	        log.debug("4. Complete team initializw");
	           
	        change = (WTChangeOrder2)TeamHelper.setTeam(change, team);
	        log.debug("5. Complete team setting");
			
			// Set LC
			LifeCycleHelper.setLifeCycle(change, LifeCycleHelper.service.getLifeCycleTemplate("Narae_ECM_LC", containerRef)); //Lifecycle
			log.debug("6. Complete Lifecycle setting: Narae_ECM_LC");
			
			change = (WTChangeOrder2)PersistenceHelper.manager.save(change);
			change = (WTChangeOrder2)PersistenceHelper.manager.refresh(change);
			log.debug("7. Complete create ECO: " + change);
			
			HashMap<String,Object> iba = new HashMap<String,Object>();
			iba.put("EC_Reason", purpose);
			iba.put("Project", projectName);
			iba.put("description2", measures);
			
			// Set IBA
			AttributeHelper.service.setValue(change, iba);
			log.debug("8. Complete set IBA [EC_Reason,Project,description2]:" + purpose + "," + projectName + "," + measures);
			
			// Set releated ECR
			if( ecrListOid != null && ecrListOid.length() > 0 ) {
				log.debug("9-1. Exist related ECR:" + ecrListOid);
				setRelevantECR(change,ecrListOid);
				log.debug("9-2. Complete related ECR:" + ecrListOid);
			}
			
			// Create ECA
			log.debug("10-1. Start to Create ECA");
			WTChangeActivity2 activity = WTChangeActivity2.newWTChangeActivity2();
			
			String ecaNumber = "ECA-" + DateUtil.getCurrentDateString("m") + "-";
			ecaNumber = ecaNumber + seqNo;
			activity.setNumber(ecaNumber);
			activity.setName(name);
			activity.setContainerReference(containerRef);
			LifeCycleHelper.setLifeCycle(activity, LifeCycleHelper.service.getLifeCycleTemplate("Narae_ECM_LC", containerRef)); //Lifecycle
			
			activity = (WTChangeActivity2)wt.change2.ChangeHelper2.service.saveChangeActivity(change, activity);;
			activity = (WTChangeActivity2)PersistenceHelper.manager.refresh(activity);
			log.debug("10-2. Complete Create ECA:" + ecaNumber);
			
			// Create related parts
			if( beforeOid != null && beforeOid.length() > 0 ) {
				log.debug("11-1. Exist top part:" + beforeOid);
				setBeforePart(activity,beforeOid);
				log.debug("11-2. Complete connect top part:" + beforeOid);
			}
			if( afterOid != null && afterOid.length() > 0 ) {
				log.debug("12-1. Exist part:" + afterOid);
				setAfterPart(activity,afterOid);
				log.debug("12-2. Complete connect part:" + afterOid);
			}
			
			// Attachment
			if( secondaryFileName.size() > 0 ) {
				log.debug("13-1. Exist attachment:" + secondaryFileName.size());
				attachSecondary(change, secondaryFileName, secondary, secondaryDelFile);
				log.debug("13-2. Complete attachment");
			}
			
			// Start Workflow
			if( submitFlag == true ) {
				// change state
				if( !ServerConfigHelper.getServerHostName().equals(TEST_SERVER) ) {
					if( agree != null && agree.trim().length() > 0 ) {
						log.debug("14. Change State: CHECKWAIT");
						LifeCycleHelper.service.setLifeCycleState(change, State.toState("CHECKWAIT"));
					} else {
						log.debug("14. Change State: APPROVEWAIT");
						LifeCycleHelper.service.setLifeCycleState(change, State.toState("APPROVEWAIT"));
					}
				} else {
					LifeCycleHelper.service.setLifeCycleState(change, State.toState("REVIEWED"));
				}
				log.debug("15. Submit ~~~~~~~~~~~~~~: Narae_Workflow");
				submitECO(change);
			}
			
			log.debug("16. COMPLETE CREATE ECO");
		     
			trx.commit();
			log.debug("17. Complte Submit ECO");
			trx = null;
       } catch(Exception e) {
    	   e.printStackTrace();
    	   trx.rollback();
    	   log.debug("99. ROLL BACK");
           return null;
       } finally {
           if(trx!=null){
				trx.rollback();
		   }
       }
		return change;
	}
	
	public static WTChangeOrder2 updateECO(HashMap<String,Object> hash, boolean submitFlag)throws Exception{
		/**** TEST ****
		hash = new HashMap<String,Object>();
		hash.put("name", URLEncoder.encode("Test001", "UTF-8"));
		hash.put("changeType", URLEncoder.encode("A,B", "UTF-8"));
		hash.put("projectName", URLEncoder.encode("123456", "UTF-8"));
		hash.put("problem", URLEncoder.encode("AAAAAAAAA", "UTF-8"));
		hash.put("solution", URLEncoder.encode("BBBBBBBB", "UTF-8"));
		hash.put("relatedECR", URLEncoder.encode("", "UTF-8"));
		hash.put("beforeOid", URLEncoder.encode("VR:wt.part.WTPart:191526,VR:wt.part.WTPart:214028", "UTF-8"));
		hash.put("afterOid", URLEncoder.encode("VR:wt.part.WTPart:191526,VR:wt.part.WTPart:214028", "UTF-8"));
		**** TEST ****/
		
		if(!RemoteMethodServer.ServerFlag) {
			Class argTypes[] = new Class[]{HashMap.class, boolean.class};
			Object args[] = new Object[]{hash, submitFlag};
			try {
				return (WTChangeOrder2)wt.method.RemoteMethodServer.getDefault().invoke(
						"updateECO",
						ChangeHelper2.class.getName(),
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
				throw e;
			}
		}
		
		Transaction trx = new Transaction();
		WTChangeOrder2 targetObject = null;
		try {
           trx.start();
           
           targetObject = (WTChangeOrder2)CommonUtil2.getInstance((String)hash.get("oid"));
			
           String oid =  ((String)hash.get("oid") != null)?(String)hash.get("oid"):"";
           String name =  ((String)hash.get("name") != null)?URLDecoder.decode((String)hash.get("name"), "utf-8"):"";
           String purpose =  ((String)hash.get("purpose") != null)?(String)hash.get("purpose"):"";
           String projectName =  ((String)hash.get("projectName") != null)?URLDecoder.decode((String)hash.get("projectName"), "utf-8"):"";
           String reason =  ((String)hash.get("reason") != null)?URLDecoder.decode((String)hash.get("reason"), "utf-8"):" ";
           String measures =  ((String)hash.get("measures") != null)?URLDecoder.decode((String)hash.get("measures"), "utf-8"):"";
	       
           String ecrListOid = ((String)hash.get("ecrListOid") != null)?(String)hash.get("ecrListOid"):"";
           String beforeOid = ((String)hash.get("partTopListOid") != null)?(String)hash.get("partTopListOid"):"";
           String afterOid = ((String)hash.get("partListOid") != null)?(String)hash.get("partListOid"):"";
           String agree = ((String)hash.get("agree") != null)?(String)hash.get("agree"):"";
           String approve = ((String)hash.get("approve") != null)?(String)hash.get("approve"):"";
           String receive = ((String)hash.get("receive") != null)?(String)hash.get("receive"):"";
           
           List<String> secondaryFileName = (hash.get("secondaryFileName") != null)?(List<String>)hash.get("secondaryFileName"):new ArrayList<String>();
           List<SerializableInputStream> secondary = (hash.get("secondary") != null)?(List<SerializableInputStream>)hash.get("secondary"):new ArrayList<SerializableInputStream>();
           HashMap<String,String> secondaryDelFile = (hash.get("secondaryDelFile") != null)?(HashMap<String,String>)hash.get("secondaryDelFile"):new HashMap<String,String>();
            
           boolean changed = false;
           if( !name.equals(targetObject.getName())) {
        	   WTChangeOrder2Master master = (WTChangeOrder2Master)(targetObject.getMaster());

        	   WTChangeOrder2MasterIdentity identity = (WTChangeOrder2MasterIdentity)master.getIdentificationObject();
        	   identity.setName(name);
        	   IdentityHelper.service.changeIdentity(master, identity);
        	   changed = true;
           }
           if( !reason.equals(targetObject.getDescription())) {
        	   targetObject.setDescription(reason);
        	   changed = true;
           }
           //change.setProposedSolution(solution);
           
           if( changed ) {
        	   PersistenceServerHelper.manager.update(targetObject);
        	   targetObject = (WTChangeOrder2)PersistenceHelper.manager.refresh(targetObject);
           }
           
           System.out.println("agree = " + agree);
           System.out.println("approve = " + approve);
           System.out.println("receive = " + receive);
           // Set Team
           ApprovalLineVO approvalLine = ApprovalHelper2.getApprovalLine((TeamManaged)targetObject);
           if( approvalLine != null ){
        	   ArrayList alist2 = new ArrayList();
        	   List<WTUser> alist3 = approvalLine.getChangManager1();
        	   List<WTUser> alist4 = approvalLine.getChangManager2();			
        	   List<WTUser> alist5 = approvalLine.getChangManager3();
        	   
        	   Team team = TeamHelper.service.getTeam(targetObject);
        	   Map roleMaps = team.getRolePrincipalMap();
        	   
        	   String managerUsers = "";
        	   List<WTUser> addUser = null;
        	   List<WTUser> deleteUser = null;
        	   Role targetRole = null;
        	   //if( alist3 != null && alist3.size() > 0 ) {
        		   System.out.println("-----------Check CHANGE ADMINISTRATOR I");
        		   targetRole = Role.toRole("CHANGE ADMINISTRATOR I");
        		   if( alist3 != null && alist3.size() > 0 ) {
	        		   deleteUser = checkDeleteUser(alist3, agree);
	        		   System.out.println("-----------deleted:" + ((deleteUser == null)?0:deleteUser.size()) );
	        		   if( deleteUser != null && deleteUser.size() > 0 ) {
	        			   for( WTUser oneUser:deleteUser ) {
	        				   team.deletePrincipalTarget(targetRole, oneUser);
	        			   }
	        		   }
        		   }
        		   if( agree != null && agree.length() > 0 ) {
	        		   addUser = checkAddUser(alist3, agree);
	        		   System.out.println("-----------added:" + ((addUser == null)?0:addUser.size()));
	        		   if( addUser != null && addUser.size() > 0 ) {
	        			   for( WTUser oneUser:addUser ) {
	        				   team.addPrincipal(targetRole, oneUser);
	        			   }
	        		   }
        		   }
        	   //}
        	   
        	   //if( alist4 != null && alist4.size() > 0 ) {
        		   targetRole = Role.toRole("CHANGE ADMINISTRATOR II");
        		   if( alist4 != null && alist4.size() > 0 ) {
	        		   deleteUser = checkDeleteUser(alist4, approve);
	        		   if( deleteUser != null && deleteUser.size() > 0 ) {
	        			   for( WTUser oneUser:deleteUser ) {
	        				   team.deletePrincipalTarget(targetRole, oneUser);
	        			   }
	        		   }
        		   }
        		   if( approve != null && approve.length() > 0 ) {
	        		   addUser = checkAddUser(alist4, approve);
	        		   if( addUser != null && addUser.size() > 0 ) {
	        			   for( WTUser oneUser:addUser ) {
	        				   team.addPrincipal(targetRole, oneUser);
	        			   }
	        		   }
        		   }
        	   //}
        	   
        	   //if( alist5 != null && alist5.size() > 0 ) {
        		   targetRole = Role.toRole("CHANGE ADMINISTRATOR III");
        		   if( alist5 != null && alist5.size() > 0 ) {
	        		   deleteUser = checkDeleteUser(alist5, receive);
	        		   if( deleteUser != null && deleteUser.size() > 0 ) {
	        			   for( WTUser oneUser:deleteUser ) {
	        				   team.deletePrincipalTarget(targetRole, oneUser);
	        			   }
	        		   }
        		   }
        		   if( receive != null && receive.length() > 0 ) {
	        		   addUser = checkAddUser(alist5, receive);
	        		   if( addUser != null && addUser.size() > 0 ) {
	        			   for( WTUser oneUser:addUser ) {
	        				   team.addPrincipal(targetRole, oneUser);
	        			   }
	        		   }
        		   }
        	   //}
           }
           
           List<String> iba = new ArrayList<String>();
           iba.add("EC_Reason");
           iba.add("Project");
           iba.add("description2");
           HashMap<String,Object> settingIBA = new HashMap<String,Object>();
			
           // Set IBA
           HashMap<String,Object> originalValue = AttributeHelper.service.getValue(targetObject, iba);
           String checkValue = null;
			
           checkValue = (String)originalValue.get("EC_Reason");
           if( checkValue != null ) {
        	   if( !purpose.equals(checkValue)) {
					settingIBA.put("EC_Reason", purpose);
				}
           }
			
           checkValue = (String)originalValue.get("Project");
           if( checkValue != null ) {
				if( !projectName.equals(checkValue)) {
					settingIBA.put("Project", projectName);
				}
           }
			
           checkValue = (String)originalValue.get("description2");
           if( checkValue != null ) {
				if( !measures.equals(checkValue)) {
					settingIBA.put("description2", measures);
				}
           }
			
           if( settingIBA.size() > 0  ) {
				AttributeHelper.service.setValue(targetObject, settingIBA);
           }
			
           // Set releated ECR
           if( ecrListOid != null && ecrListOid.length() > 0 ) {
        	   setRelevantECR(targetObject,ecrListOid);
           }
			
           // GEt ECA
           QueryResult result = wt.change2.ChangeHelper2.service.getChangeActivities(targetObject);
           WTChangeActivity2 activity = (WTChangeActivity2)result.nextElement();
			
           // Create related parts
           if( beforeOid != null && beforeOid.length() > 0 ) {
        	   setBeforePart(activity,beforeOid);
           }
           if( afterOid != null && afterOid.length() > 0 ) {
        	   setAfterPart(activity,afterOid);
           }
			
           // Attachment
           if( secondaryFileName.size() > 0 ) {
        	   attachSecondary(targetObject, secondaryFileName, secondary, secondaryDelFile);
           }
			
           // Start Workflow
           if( submitFlag == true ) {
				submitECO(targetObject);
				// change state
				if( !ServerConfigHelper.getServerHostName().equals(TEST_SERVER) ) {
					if( agree != null && agree.trim().length() > 0 ) {
						LifeCycleHelper.service.setLifeCycleState(targetObject, State.toState("CHECKWAIT"));
					} else {
						LifeCycleHelper.service.setLifeCycleState(targetObject, State.toState("APPROVEWAIT"));
					}
				} else {
					LifeCycleHelper.service.setLifeCycleState(targetObject, State.toState("REVIEWED"));
				}
           }
		     
           trx.commit();
           trx = null;
       } catch(Exception e) {
    	   e.printStackTrace();
    	   trx.rollback();
           return null;
       } finally {
           if(trx!=null){
				trx.rollback();
		   }
       }
		return targetObject;
	}
	
	public static String submitChangeObject(String oid) {
		if(!RemoteMethodServer.ServerFlag) {
			Class argTypes[] = new Class[]{String.class};
			Object args[] = new Object[]{oid};
			try {
				return (String)wt.method.RemoteMethodServer.getDefault().invoke(
						"submitChangeObject",
						ChangeHelper2.class.getName(),
						null,
						argTypes,
						args);
			} catch(Exception e){
				e.printStackTrace();
				return e.getMessage();
			}
		}
		
		try {
			clearAllProcess(oid);
			
			WTObject change = (WTObject)CommonUtil2.getInstance(oid);
			if( change instanceof WTChangeOrder2 ) {
				submitECO((WTChangeOrder2)change);
				// change state
				if( !ServerConfigHelper.getServerHostName().equals(TEST_SERVER) ) {
					LifeCycleHelper.service.setLifeCycleState((WTChangeOrder2)change, State.toState("CHECKWAIT"));
				} else {
					LifeCycleHelper.service.setLifeCycleState((WTChangeOrder2)change, State.toState("REVIEWED"));
				}
			} else if ( change instanceof WTChangeRequest2 ) {
				submitECR((WTChangeRequest2)change);
				// change state
				if( !ServerConfigHelper.getServerHostName().equals(TEST_SERVER) ) {
					LifeCycleHelper.service.setLifeCycleState((WTChangeRequest2)change, State.toState("CHECKWAIT"));
				} else {
					LifeCycleHelper.service.setLifeCycleState((WTChangeRequest2)change, State.toState("REVIEWED"));
				}
			}
		} catch(WTException e) {
			return e.getLocalizedMessage();
		} catch (WTPropertyVetoException e) {
			// TODO Auto-generated catch block
			return e.getMessage();
		}
		
		return "";
	}
	
	public static String withdrawChangeObject(String oid) {
		if(!RemoteMethodServer.ServerFlag) {
			Class argTypes[] = new Class[]{String.class};
			Object args[] = new Object[]{oid};
			try {
				return (String)wt.method.RemoteMethodServer.getDefault().invoke(
						"withdrawChangeObject",
						ChangeHelper2.class.getName(),
						null,
						argTypes,
						args);
			} catch(Exception e){
				e.printStackTrace();
				return e.getMessage();
			}
		}
		
		WTPrincipal currentUser = null;
		try {
			currentUser = SessionHelper.manager.getPrincipal();
			SessionHelper.manager.setAdministrator();
			
			WTContained change = (WTContained)CommonUtil2.getInstance(oid);
			//Get WfPRocess & Delete WfProcess
			Enumeration process = WfEngineHelper.service.getAssociatedProcesses(change, WfState.OPEN, change.getContainerReference());
			if( process != null ) {
				WfProcess oneProcess = null;
				while( process.hasMoreElements() ) {
					oneProcess = (WfProcess)process.nextElement();
					System.out.println("Withdraw:"+oneProcess);
					PersistenceHelper.manager.delete(oneProcess);
				}
			}
			// Set State
			LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)change, State.toState("INWORK"));
			
			// State Change Part
			WorkflowHelper2.changeState((WTObject)change, "INWORK");
			
		} catch(WTException e) {
			e.printStackTrace();
			return e.getMessage();
		} finally {
			try {
				SessionHelper.manager.setPrincipal(currentUser.getName());
			} catch (WTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return "";
	}
	
	private static String clearAllProcess(String oid) {
		if(!RemoteMethodServer.ServerFlag) {
			Class argTypes[] = new Class[]{String.class};
			Object args[] = new Object[]{oid};
			try {
				return (String)wt.method.RemoteMethodServer.getDefault().invoke(
						"clearAllProcess",
						ChangeHelper2.class.getName(),
						null,
						argTypes,
						args);
			} catch(Exception e){
				e.printStackTrace();
				return e.getMessage();
			}
		}
		
		WTPrincipal currentUser = null;
		try {
			currentUser = SessionHelper.manager.getPrincipal();
			SessionHelper.manager.setAdministrator();
			
			WTContained change = (WTContained)CommonUtil2.getInstance(oid);
			//Get WfPRocess & Delete WfProcess
			Enumeration process = WfEngineHelper.service.getAssociatedProcesses(change, WfState.CLOSED_COMPLETED_EXECUTED, change.getContainerReference());
			if( process != null ) {
				WfProcess oneProcess = null;
				while( process.hasMoreElements() ) {
					oneProcess = (WfProcess)process.nextElement();
					PersistenceHelper.manager.delete(oneProcess);
				}
			}
			// Set State
			LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)change, State.toState("INWORK"));
			
		} catch(WTException e) {
			e.printStackTrace();
			return e.getMessage();
		} finally {
			try {
				SessionHelper.manager.setPrincipal(currentUser.getName());
			} catch (WTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return "";
		
	}
	
	private static void setRelevantData(WTChangeRequest2 request, String partOids) throws WTException {
		clearRelevantData(request);
		
		String partOidList[] = partOids.split("[,]");
		if( partOidList.length > 0 ) {
			Changeable2 oneObject = null;
			for( String oneOid: partOidList) {
				oneObject = (Changeable2)getInstance(oneOid);
				setRelevantData(request, oneObject);
			}
		}
	}
	
	private static void setRelevantData(WTChangeRequest2 request, Changeable2 changeable) throws WTException {
		RelevantRequestData2 link = RelevantRequestData2.newRelevantRequestData2(changeable, request);
		PersistenceServerHelper.manager.insert(link);
	}
	
	private static void clearRelevantData(WTChangeRequest2 request) throws WTException {
		String oid = (new ReferenceFactory()).getReferenceString(request);
		String[] oidArr = oid.split("[:]");
		long modelId= Long.valueOf(oidArr[2]).longValue();				
				
		QuerySpec spec = new QuerySpec(RelevantRequestData2.class);
		SearchCondition condition = new SearchCondition(RelevantRequestData2.class, "roleAObjectRef.key.branchId", SearchCondition.EQUAL, modelId);
		spec.appendWhere(condition);
		
		QueryResult result = PersistenceHelper.manager.find(spec);
		
		if( result != null && result.size() > 0 ) {
			RelevantRequestData2 one = null;
			while(result.hasMoreElements()) {
				one = (RelevantRequestData2)result.nextElement();
				PersistenceServerHelper.manager.remove(one);
			}
		}
	}
	
	private static void setRelevantECR(WTChangeOrder2 change, String oid) throws WTException {
		clearRelevantECR(change);
		
		String partOidList[] = oid.split("[,]");
		if( partOidList.length > 0 ) {
			WTChangeRequest2 oneObject = null;
			for( String oneOid: partOidList) {
				oneObject = (WTChangeRequest2)getInstance(oneOid);
				setRelevantECR(change, oneObject);
			}
		}
	}
	
	private static void clearRelevantECR(WTChangeOrder2 order) throws WTException {
		String oid = (new ReferenceFactory()).getReferenceString(order);
		String[] oidArr = oid.split("[:]");
		long modelId= Long.valueOf(oidArr[2]).longValue();				
				
		QuerySpec spec = new QuerySpec(AddressedBy2.class);
		SearchCondition condition = new SearchCondition(AddressedBy2.class, "roleBObjectRef.key.branchId", SearchCondition.EQUAL, modelId);
		spec.appendWhere(condition);
		
		QueryResult result = PersistenceHelper.manager.find(spec);
		
		if( result != null && result.size() > 0 ) {
			AddressedBy2 one = null;
			while(result.hasMoreElements()) {
				one = (AddressedBy2)result.nextElement();
				PersistenceServerHelper.manager.remove(one);
			}
		}
	}
	
	private static void setRelevantECR(WTChangeOrder2 order, WTChangeRequest2 request) throws WTException {
		AddressedBy2 link = AddressedBy2.newAddressedBy2(request, order);
		PersistenceServerHelper.manager.insert(link);
	}
	
	private static void setBeforePart(WTChangeActivity2 change, String oid) throws WTException {
		clearBeforePart(change);
		
		String partOidList[] = oid.split("[,]");
		if( partOidList.length > 0 ) {
			WTPart oneObject = null;
			for( String oneOid: partOidList) {
				oneObject = (WTPart)getInstance(oneOid);
				setBeforePart(change, oneObject);
			}
		}
	}
	
	private static void setBeforePart(WTChangeActivity2 order, WTPart part) throws WTException {
		AffectedActivityData link = AffectedActivityData.newAffectedActivityData(part, order);
		PersistenceServerHelper.manager.insert(link);
	}
	
	private static void clearBeforePart(WTChangeActivity2 activity) throws WTException {
		String oid = (new ReferenceFactory()).getReferenceString(activity);
		String[] oidArr = oid.split("[:]");
		long modelId= Long.valueOf(oidArr[2]).longValue();				
				
		QuerySpec spec = new QuerySpec(AffectedActivityData.class);
		SearchCondition condition = new SearchCondition(AffectedActivityData.class, "roleAObjectRef.key.branchId", SearchCondition.EQUAL, modelId);
		spec.appendWhere(condition);
		
		QueryResult result = PersistenceHelper.manager.find(spec);
		
		if( result != null && result.size() > 0 ) {
			AffectedActivityData one = null;
			while(result.hasMoreElements()) {
				one = (AffectedActivityData)result.nextElement();
				PersistenceServerHelper.manager.remove(one);
			}
		}
	}
	
	private static void setAfterPart(WTChangeActivity2 change, String oid) throws WTException {
		clearAfterPart(change);
		
		String partOidList[] = oid.split("[,]");
		if( partOidList.length > 0 ) {
			WTPart oneObject = null;
			for( String oneOid: partOidList) {
				oneObject = (WTPart)getInstance(oneOid);
				setAfterPart(change, oneObject);
			}
		}
	}
	
	private static void setAfterPart(WTChangeActivity2 order, WTPart part) throws WTException {
		ChangeRecord2 link = ChangeRecord2.newChangeRecord2(part, order);
		PersistenceServerHelper.manager.insert(link);
	}
	
	private static void clearAfterPart(WTChangeActivity2 activity) throws WTException {
		String oid = (new ReferenceFactory()).getReferenceString(activity);
		String[] oidArr = oid.split("[:]");
		long modelId= Long.valueOf(oidArr[2]).longValue();				
				
		QuerySpec spec = new QuerySpec(ChangeRecord2.class);
		SearchCondition condition = new SearchCondition(ChangeRecord2.class, "roleAObjectRef.key.branchId", SearchCondition.EQUAL, modelId);
		spec.appendWhere(condition);
		
		QueryResult result = PersistenceHelper.manager.find(spec);
		
		if( result != null && result.size() > 0 ) {
			ChangeRecord2 one = null;
			while(result.hasMoreElements()) {
				one = (ChangeRecord2)result.nextElement();
				PersistenceServerHelper.manager.remove(one);
			}
		}
	}
	
	private static void makeRoleMap(Team team, String roleName, String roler) throws WTPropertyVetoException, WTException {
		String partOidList[] = roler.split("[,]");
		if( partOidList.length > 0 ) {
			WTPrincipalReference refer = null;
			WTUser oneUser = null;
			RolePrincipalMap roleMap = RolePrincipalMap.newRolePrincipalMap();
			Role role = (Role)(EnumeratedTypeUtil.toEnumeratedType(EnumeratedTypeUtil.getStringValue(Role.class.getName(), roleName)));
			
			
			for( String oneOid: partOidList) {
				roleMap = RolePrincipalMap.newRolePrincipalMap();
				roleMap.setRole(role);
				oneUser = (WTUser)getInstance(oneOid);
				refer = WTPrincipalReference.newWTPrincipalReference(oneUser);
				roleMap.setPrincipalParticipant(refer);
				roleMap = (RolePrincipalMap)PersistenceHelper.manager.save(roleMap);
				RolePrincipalLink roleLink = RolePrincipalLink.newRolePrincipalLink(roleMap, (WTRoleHolder2)team);
		        roleLink = (RolePrincipalLink)PersistenceHelper.manager.save(roleLink);
			}
		}
	}
	
	public static void attachSecondary(ContentHolder holder, List<String> fileName, List<SerializableInputStream> file, HashMap<String,String> secondaryDelFile) throws WTException, FileNotFoundException, PropertyVetoException, IOException {
		if( secondaryDelFile.size() > 0 ) {
			QueryResult result = ContentHelper.service.getContentsByRole(holder, ContentRoleType.SECONDARY);
			ApplicationData data = null;
			while( result.hasMoreElements() ) {
				data = (ApplicationData)result.nextElement();
				if( secondaryDelFile.get(data.getPersistInfo().getObjectIdentifier().getStringValue()) == null ) {
					ContentServerHelper.service.deleteContent(holder, data);
				}
			}
		} else {
			QueryResult result = ContentHelper.service.getContentsByRole(holder, ContentRoleType.SECONDARY);
			ApplicationData data = null;
			while( result.hasMoreElements() ) {
				data = (ApplicationData)result.nextElement();
				ContentServerHelper.service.deleteContent(holder, data);
			}
		}
		
		InputStream is = null;
        ApplicationData applicationData = null;
        String sFileName = null;
        
        for( int index=0; index < fileName.size(); index++) {
        	is = file.get(index);
        	applicationData = ApplicationData.newApplicationData(holder);
        	sFileName = fileName.get(index);
        	applicationData.setFileName(sFileName.substring(sFileName.lastIndexOf(File.separator)+1));
        	applicationData.setRole(ContentRoleType.SECONDARY);
        	ContentServerHelper.service.updateContent(holder, applicationData, is);
        }
	}
	
	public static WfProcess submitECR(WTChangeRequest2 request) throws WTPropertyVetoException, WTException {
		String prefix = request.getNumber();
		WfProcess process = startProcess(request, "Narae_Workflow", prefix);
		
		return process;
	}
	
	public static WfProcess submitECO(WTChangeOrder2 order) throws WTPropertyVetoException, WTException {
		String prefix = order.getNumber();
		WfProcess process = startProcess(order, "Narae_Workflow", prefix);
		
		return process;
	}
	
	public static WfProcess startProcess(WTObject pbo, String workflowTemplateName, String processNamePrefix) throws WTException, WTPropertyVetoException {
		WfProcessDefinition pd = WfDefinerHelper.service.getProcessDefinition(workflowTemplateName);
		WTContainerRef containerRef = null;
		TeamReference teamRef = null;
		
		if( pbo instanceof WTContained ) {
			containerRef = ((WTContained)pbo).getContainerReference();
		}
		if( pbo instanceof TeamManaged ) {
			teamRef = ((TeamManaged)pbo).getTeamId();
		}
		
		WfProcess process = null;
		if( containerRef != null)
			process = WfEngineHelper.service.createProcess(pd, teamRef, containerRef);
		else
			process = WfEngineHelper.service.createProcess(pd, teamRef);
		
		String processName = null;
		if( processNamePrefix != null )
			processName = processNamePrefix + "_" + pbo.getPersistInfo().getObjectIdentifier().toString() + "_narae";
		else
			processName = pbo.getPersistInfo().getObjectIdentifier().toString() + "_narae";
		
		process.setName(processName);
		process.setCreator(SessionHelper.manager.getPrincipalReference());
		ProcessData context = process.getContext();
		if( context == null ) {
			process.setContext(ProcessData.newProcessData(ProcessDataInfo.newProcessDataInfo()));
		}
		
		ReferenceFactory rf = new ReferenceFactory();
		context.setValue("primaryBusinessObject", pbo);
		
		process = process.start(context, true, WTContainerHelper.getExchangeRef());
		
		return process;
	}
	
	private static List<WTUser> checkDeleteUser(List<WTUser> original, String changed) {
		List<WTUser> result = new ArrayList<WTUser>();
		
		if( changed == null || changed.length() == 0) {
			// All deleted
			return original;
		} else {
			for( WTUser oneUser : original ) {
				if( !changed.contains(oneUser.getPersistInfo().getObjectIdentifier().getStringValue()) ) {
					result.add(oneUser);
				}
			}
		}
		
		if( result.size() > 0 ) return result;
		else return null;
	}
	
	private static List<WTUser> checkAddUser(List<WTUser> original, String changed) {
		List<WTUser> result = new ArrayList<WTUser>();
		
		if( changed == null || changed.length() == 0 ) {
			// No add
			return null;
		} else {
			String originalList = "";
			WTUser oneUser = null;
			for(  int index=0; index < original.size(); index++ ) {
				oneUser = original.get(index);
				if( index == 0 ) {
					originalList = oneUser.getPersistInfo().getObjectIdentifier().getStringValue();
				} else {
					originalList = originalList + "," + oneUser.getPersistInfo().getObjectIdentifier().getStringValue();
				}
			}
			
			String[] changedList = changed.split("[,]");
			for( String oneChanged : changedList ) {
				if( !originalList.contains(oneChanged) ) {
					result.add((WTUser)CommonUtil.getObject(oneChanged));
				}
			}
		}
		
		if( result.size() > 0 ) return result;
		else return null;
	}
	
	public static WTObject getInstance(String oid) throws WTException {
		ReferenceFactory referencefactory = new ReferenceFactory();
		WTReference wtreference = referencefactory.getReference( oid ); 
		return (WTObject)wtreference.getObject();
	}
	
	public static String getModifyTime(WTChangeOrder2 change) {
		return change.getModifyTimestamp().toLocaleString();
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			//ChangeHelper.createECR(new HashMap<String,Object>(), false);
			//String aa ="ECR-1311-";
			String aa = "VR:wew.ewe.we.we:12345";
			System.out.println(aa.split("[:]")[2]);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
