/* bcwti
 *
 * Copyright (c) 2008 Parametric Technology Corporation (PTC). All Rights
 * Reserved.
 *
 * This software is the confidential and proprietary information of PTC
 * and is subject to the terms of a software license agreement. You shall
 * not disclose such confidential information and shall use it only in accordance
 * with the terms of the license agreement.
 *
 * ecwti
 */

package ext.narae.service.org;

import java.io.Serializable;

import com.infoengine.SAK.Task;
//import com.ptc.windchill.enterprise.util.PartManagementHelper;
//import com.ptc.windchill.uwgm.soap.uwgmdb.LifeCycleManaged;
import com.ptc.wvs.server.publish.PublishServiceEvent;
//import com.sun.xml.ws.transport.tcp.pool.LifeCycle;

import ext.narae.service.org.beans.UserHelper;
import ext.narae.util.EventVersionManager;
import wt.epm.listeners.EPMEventServiceEvent;
import wt.events.KeyedEvent; // Preserved unmodeled dependency
import wt.events.KeyedEventListener; // Preserved unmodeled dependency
import wt.fc.PersistenceManagerEvent; // Preserved unmodeled dependency
import wt.federation.PrincipalManager.DirContext;
import wt.org.OrganizationServicesEvent; // Preserved unmodeled dependency
import wt.org.WTUser; // Preserved unmodeled dependency
import wt.services.ManagerException;
import wt.services.ServiceEventListenerAdapter; // Preserved unmodeled dependency
import wt.services.StandardManager;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.vc.VersionControlServiceEvent;
import wt.vc.wip.WorkInProgressServiceEvent; // Preserved unmodeled dependency
import wt.workflow.engine.WfEngineServiceEvent; // Preserved unmodeled dependency
import wt.workflow.engine.WfEventAuditType; // Preserved unmodeled dependency

/**
 *
 * <p>
 * Use the <code>newStandardE3psCompanyService</code> static factory method(s),
 * not the <code>StandardE3psCompanyService</code> constructor, to construct
 * instances of this class. Instances must be constructed using the static
 * factory(s), in order to ensure proper initialization of the instance.
 * <p>
 *
 *
 * @version 1.0
 **/

public class StandardE3psCompanyService extends StandardManager implements E3psCompanyService, Serializable {

	private static final String RESOURCE = "com.e3ps.org.orgResource";
	private static final String CLASSNAME = StandardE3psCompanyService.class.getName();

	private static String userAdapter;
	private static String userDirectory;
	private static String userSearch;

	static {
		try {
			WTProperties props = WTProperties.getServerProperties();
			String taskRootDirectory = props.getProperty("wt.federation.taskRootDirectory");
			String taskCodebase = props.getProperty("wt.federation.taskCodebase");
			String mapCredentials = props.getProperty("wt.federation.task.mapCredentials");
			String credentialsMapper = System.getProperty("com.infoengine.credentialsMapper");

			userDirectory = props.getProperty("wt.federation.org.defaultDirectoryUser",
					props.getProperty("wt.admin.defaultAdministratorName"));

			String VMName = props.getProperty("wt.federation.ie.VMName");
			if (VMName != null) {
				System.setProperty("com.infoengine.vm.name", VMName);
			}
			if (taskCodebase != null) {
				System.setProperty("com.infoengine.taskProcessor.codebase", taskCodebase);
			}
			if (taskRootDirectory != null) {
				System.setProperty("com.infoengine.taskProcessor.templateRoot", taskRootDirectory);
			}
			if (mapCredentials != null && credentialsMapper == null) {
				System.setProperty("com.infoengine.credentialsMapper", mapCredentials);
			}

			userAdapter = DirContext.getDefaultJNDIAdapter();
			userSearch = DirContext.getJNDIAdapterSearchBase(userAdapter);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the conceptual (modeled) name for the class.
	 *
	 * <BR>
	 * <BR>
	 * <B>Supported API: </B>false
	 *
	 * @deprecated
	 *
	 * @return String
	 **/
	public String getConceptualClassname() {

		return CLASSNAME;
	}

	/**
	 * @exception wt.services.ManagerException
	 **/
	@Override
	protected void performStartupProcess() throws ManagerException {

		super.performStartupProcess();

		KeyedEventListener listener = new WTUserEventListener(this.getConceptualClassname());
		getManagerService().addEventListener(listener, PersistenceManagerEvent.generateEventKey("POST_STORE"));
		getManagerService().addEventListener(listener, PersistenceManagerEvent.generateEventKey("POST_MODIFY"));
		getManagerService().addEventListener(listener, PersistenceManagerEvent.generateEventKey("POST_DELETE"));
		getManagerService().addEventListener(listener, OrganizationServicesEvent.generateEventKey("POST_DISABLE"));
		getManagerService().addEventListener(listener, OrganizationServicesEvent.generateEventKey("POST_ENABLE"));
		getManagerService().addEventListener(listener,
				WfEngineServiceEvent.generateEventKey(WfEventAuditType.ACTIVITY_STATE_CHANGED));

		KeyedEventListener _listener = new E3PSVersionListener(this.getConceptualClassname());

		getManagerService().addEventListener(_listener, PersistenceManagerEvent.generateEventKey("POST_STORE"));
		getManagerService().addEventListener(_listener, PersistenceManagerEvent.generateEventKey("PRE_DELETE"));
		getManagerService().addEventListener(_listener, PersistenceManagerEvent.generateEventKey("UPDATE"));
		getManagerService().addEventListener(_listener, PublishServiceEvent.generateEventKey("PUBLISH_SUCCESSFUL"));
		getManagerService().addEventListener(_listener, WorkInProgressServiceEvent.generateEventKey("POST_CHECKIN"));
		getManagerService().addEventListener(_listener, WorkInProgressServiceEvent.generateEventKey("PRE_CHECKIN"));
		getManagerService().addEventListener(_listener, VersionControlServiceEvent.generateEventKey("NEW_VERSION"));

		
		getManagerService().addEventListener(_listener,
				WorkInProgressServiceEvent.generateEventKey(EPMEventServiceEvent.CHECKIN_COMPLETE));
		// getManagerService().addEventListener(listener,
		// WorkInProgressServiceEvent.generateEventKey("POST_CHECKIN"));

		// System.out.println(KeyedEvent.generateEventKey(WTPart.class, ""));
		// getManagerService().addEventListener(listener,
		// PersistenceManagerEvent.generateEventKey("POST_REFRESH"));
		// getManagerService().addEventListener(listener,
		// PersistenceManagerEvent.generateEventKey("PRE_STORE"));

	}

	/**
	 * Default factory for the class.
	 *
	 * @return StandardE3psCompanyService
	 * @exception wt.util.WTException
	 **/
	public static StandardE3psCompanyService newStandardE3psCompanyService() throws WTException {

		StandardE3psCompanyService instance = new StandardE3psCompanyService();
		instance.initialize();
		return instance;
	}

	class WTUserEventListener extends ServiceEventListenerAdapter {
		public WTUserEventListener(String manager_name) {
			super(manager_name);
		}

		public void notifyVetoableEvent(Object obj) throws WTException {
			if (!(obj instanceof KeyedEvent))
				return;
			KeyedEvent keyEvent = (KeyedEvent) obj;
			Object eventObj = keyEvent.getEventTarget();
			String eventTypeStr = keyEvent.getEventType();

			if (!(eventObj instanceof WTUser))
				return;

			if (eventTypeStr.equals("POST_DISABLE")) {
				UserHelper.service.syncDelete((WTUser) eventObj);
			} else if (eventTypeStr.equals("POST_ENABLE")) {
				// ?? ?? ??...
			} else if (eventTypeStr.equals("POST_DELETE")) {
				UserHelper.service.syncDelete((WTUser) eventObj);
			} else if (eventTypeStr.equals("POST_MODIFY")) {
				UserHelper.service.syncSave((WTUser) eventObj);
			} else if (eventTypeStr.equals("POST_STORE")) {
				UserHelper.service.syncSave((WTUser) eventObj);
			} else if (eventTypeStr.equals("PRE_CHECKIN")) {
				System.out.println("PRE_CHECKIN");

			}
		}
	}

	class E3PSVersionListener extends ServiceEventListenerAdapter {
		public E3PSVersionListener(String manager_name) {
			super(manager_name);
		}

		public void notifyVetoableEvent(Object obj) throws WTException {

			if (!(obj instanceof KeyedEvent))
				return;

			KeyedEvent keyEvent = (KeyedEvent) obj;
			WorkInProgressServiceEvent kk = null;
			// kk.getOriginalCopy()
			Object eventObj = keyEvent.getEventTarget();
			String eventTypeStr = keyEvent.getEventType();

			// System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			// System.out.println(">>>>>>>>>>>>>>>>>>>notifyVetoableEvent : " + eventObj +"
			// : " +eventTypeStr);
			// System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			if (keyEvent instanceof WorkInProgressServiceEvent) {
				WorkInProgressServiceEvent eventO = (WorkInProgressServiceEvent) keyEvent;
				eventObj = eventO.getWorkingCopy();
			}
			EventVersionManager.manager.eventListener(eventObj, eventTypeStr);
		}
	}

	@Override
	public void password(String id, String password) throws Exception {
		String uid = DirContext.getMapping(userAdapter, "user.uniqueIdAttribute",
				DirContext.getMapping(userAdapter, "user.uid"));
		String object = uid + "=" + id + "," + userSearch;

		Task task = new Task("/wt/federation/UpdatePrincipal.xml");
		task.addParam("object", object);
		task.addParam("field", DirContext.getMapping(userAdapter, "user.userPassword") + "=" + password);
		task.addParam("modification", "replace");
		task.addParam("instance", userAdapter);
		task.setUsername(userDirectory);
		task.invoke();
	}

}
