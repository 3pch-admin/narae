package ext.narae.service.approval.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.logging.log4j.Logger;

import ext.narae.component.ApprovalLineVO;
import ext.narae.service.workflow.beans.WorkflowHelper2;
import wt.lifecycle.LifeCycleManaged;
import wt.log4j.LogR;
import wt.method.RemoteAccess;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.project.Role;
import wt.team.Team;
import wt.team.TeamHelper;
import wt.team.TeamManaged;
import wt.util.WTContext;
import wt.util.WTMessage;
import wt.workflow.work.WorkItem;

public class ApprovalHelper2 implements RemoteAccess, Serializable{
	private static final Logger log = LogR.getLoggerInternal(ApprovalHelper2.class.getName());
	private static String RESOURCE = "ext.narae.ui.common.resource.NaraeCommonRB";
	
	public static ApprovalLineVO getApprovalLine(TeamManaged managed) throws Exception {
		ApprovalLineVO approvalLine = new ApprovalLineVO();
		List<WTUser> users = new ArrayList<WTUser>();
		List<List<String>> approvalInfo = new ArrayList<List<String>>();
		List<String> oneApprovalInfo = new ArrayList<String>();
		boolean exist = false;
		
		Team team = TeamHelper.service.getTeam(managed);
		System.out.println("Team=" + team);
		
		Enumeration enumeration = team.getPrincipalTarget(Role.toRole("CHANGE ADMINISTRATOR I"));
		if( enumeration != null)
		{
			System.out.println("CHANGE ADMINISTRATOR I has user!");
			exist = true;
			WorkItem workitem = null;
		    while( enumeration.hasMoreElements() )
		    {
		    	WTPrincipalReference r = (WTPrincipalReference)enumeration.nextElement();
		    	users.add((WTUser)r.getPrincipal());
		    	// Find workitem
		    	workitem = WorkflowHelper2.getTargetWorkItem(r.getPrincipal(), (LifeCycleManaged)managed, "COMPLETED", "CHANGE ADMINISTRATOR I");
		    	if(workitem != null) {
		    		oneApprovalInfo.add(workitem.getModifyTimestamp().toLocaleString());
		    		// Get comment & set Comment
		    		oneApprovalInfo.add(WorkflowHelper2.getComment(workitem));
		    	} else {
		    		workitem = WorkflowHelper2.getTargetWorkItem(r.getPrincipal(), (LifeCycleManaged)managed, "POTENTIAL", "CHANGE ADMINISTRATOR I");
		    		if( workitem != null ) {
		    			oneApprovalInfo.add(WTMessage.getLocalizedMessage(RESOURCE , "WAIT_REVIEW", new Object[]{}, WTContext.getContext().getLocale()));
		    			oneApprovalInfo.add("");
		    		} else {
		    			oneApprovalInfo.add("");
		    			oneApprovalInfo.add("");
		    		}
		    	}
		    	approvalInfo.add(oneApprovalInfo);
		    	System.out.println("CHANGE ADMINISTRATOR I: " + r.getFullName());
		    }
		    approvalLine.setChangManager1(users);
		    approvalLine.setChangeManagerInfo1(approvalInfo);
		}
		
		users = new ArrayList<WTUser>();
		approvalInfo = new ArrayList<List<String>>();
		oneApprovalInfo = new ArrayList<String>();
		enumeration = team.getPrincipalTarget(Role.toRole("CHANGE ADMINISTRATOR II"));
		if( enumeration != null)
		{
			System.out.println("CHANGE ADMINISTRATOR II has user!");
			exist = true;
			WorkItem workitem = null;
		    while( enumeration.hasMoreElements() )
		    {
		    	WTPrincipalReference r = (WTPrincipalReference)enumeration.nextElement();
		    	users.add((WTUser)r.getPrincipal());
		    	// Find workitem
		    	workitem = WorkflowHelper2.getTargetWorkItem(r.getPrincipal(), (LifeCycleManaged)managed, "COMPLETED", "CHANGE ADMINISTRATOR II");
		    	if(workitem != null) {
		    		oneApprovalInfo.add(workitem.getModifyTimestamp().toLocaleString());
		    		// Get comment & set Comment
		    		oneApprovalInfo.add(WorkflowHelper2.getComment(workitem));
		    	} else {
		    		workitem = WorkflowHelper2.getTargetWorkItem(r.getPrincipal(), (LifeCycleManaged)managed, "POTENTIAL", "CHANGE ADMINISTRATOR II");
		    		if( workitem != null ) {
		    			oneApprovalInfo.add(WTMessage.getLocalizedMessage(RESOURCE , "WAIT_APPROVE", new Object[]{}, WTContext.getContext().getLocale()));
		    			oneApprovalInfo.add("");
		    		} else {
		    			oneApprovalInfo.add("");
		    			oneApprovalInfo.add("");
		    		}
		    	}
		    	approvalInfo.add(oneApprovalInfo);
		    	System.out.println("CHANGE ADMINISTRATOR II: " + r.getFullName());
		    }
		    approvalLine.setChangManager2(users);
		    approvalLine.setChangeManagerInfo2(approvalInfo);
		}
		
		users = new ArrayList<WTUser>();
		approvalInfo = new ArrayList<List<String>>();
		oneApprovalInfo = new ArrayList<String>();
		enumeration = team.getPrincipalTarget(Role.toRole("CHANGE ADMINISTRATOR III"));
		if( enumeration != null)
		{
			System.out.println("CHANGE ADMINISTRATOR III has user!");
			exist = true;
			WorkItem workitem = null;
		    while( enumeration.hasMoreElements() )
		    {
		    	WTPrincipalReference r = (WTPrincipalReference)enumeration.nextElement();
		    	users.add((WTUser)r.getPrincipal());
		    	// Find workitem
		    	workitem = WorkflowHelper2.getTargetWorkItem(r.getPrincipal(), (LifeCycleManaged)managed, "COMPLETED", "CHANGE ADMINISTRATOR III");
		    	if(workitem != null) {
		    		oneApprovalInfo.add(workitem.getModifyTimestamp().toLocaleString());
		    		// Get comment & set Comment
		    		oneApprovalInfo.add(WorkflowHelper2.getComment(workitem));
		    	} else {
		    		workitem = WorkflowHelper2.getTargetWorkItem(r.getPrincipal(), (LifeCycleManaged)managed, "POTENTIAL", "CHANGE ADMINISTRATOR III");
		    		if( workitem != null ) {
		    			oneApprovalInfo.add(WTMessage.getLocalizedMessage(RESOURCE , "WAIT_RECEIVE", new Object[]{}, WTContext.getContext().getLocale()));
		    			oneApprovalInfo.add("");
		    		} else {
		    			oneApprovalInfo.add("");
		    			oneApprovalInfo.add("");
		    		}
		    	}
		    	approvalInfo.add(oneApprovalInfo);
		    	System.out.println("CHANGE ADMINISTRATOR III: " + r.getFullName());
		    }
		    approvalLine.setChangManager3(users);
		    approvalLine.setChangeManagerInfo3(approvalInfo);
		}
		
		if( exist )
			return approvalLine;
		else
			return null;
	}
	
	public static void main(String args[]) {
		try {
			// Approve test
//			CommonUtil2.getInstance("OR:wt.workflow.work.WorkItem:298170");
//			WorkflowHelper2.approveWorkItem("OR:wt.workflow.work.WorkItem:298170", "Commant Test001");
			
			// Add Team Member
//			WTChangeOrder2 request = (WTChangeOrder2)CommonUtil2.getInstance("OR:wt.change2.WTChangeOrder2:318260");
//			Team team = TeamHelper.service.getTeam(request);
//			System.out.println(team.getName());
//			Role role = Role.toRole("CHANGE ADMINISTRATOR I");
//			WTPrincipal principal = SessionHelper.manager.getPrincipal();
//			team.addPrincipal(role, principal);
//			role = Role.toRole("CHANGE ADMINISTRATOR II");
//			team.addPrincipal(role, principal);
//			role = Role.toRole("CHANGE ADMINISTRATOR III");
//			team.addPrincipal(role, principal);
			
			// Get approval line test
//			WTChangeOrder2 request = (WTChangeOrder2)CommonUtil2.getInstance("OR:wt.change2.WTChangeOrder2:318260");
//			ApprovalHelper2.getApprovalLine((TeamManaged)request);
			
			// Approve one step
//			WorkflowHelper2.approveWorkItem("wt.workflow.work.WorkItem:318329", "Test Comment");
			
			// Reject test
			WorkflowHelper2.rejectWorkItem("wt.workflow.work.WorkItem:318366", "Reject Comment");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
