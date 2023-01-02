package ext.narae.component;

import java.io.Serializable;
import java.util.Vector;

import wt.fc.Persistable;
import wt.util.WTContext;
import wt.util.WTMessage;
import wt.workflow.engine.WfActivity;
import wt.workflow.work.WorkItem;

public class WorkItemVO implements Serializable {
	private static String RESOURCE = "ext.narae.ui.common.resource.NaraeCommonRB";

	private String taskName = null;
	private WfActivity activity = null;
	private Persistable pbo = null;
	private String pboOid = null;
	private String number = null;
	private Object event[] = null;
	private String oid = null;

	public WorkItemVO() {
	}

	public WorkItemVO(WorkItem item) {
		oid = item.getPersistInfo().getObjectIdentifier().getStringValue();
		activity = (WfActivity) item.getSource().getObject();
		taskName = activity.getName();
		pbo = item.getPrimaryBusinessObject().getObject();
		pboOid = pbo.getPersistInfo().getObjectIdentifier().getStringValue();

		Vector userEventVector = activity.getUserEventList().toVector();
		event = userEventVector.toArray();
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getTaskName() {
		try {
			return WTMessage.getLocalizedMessage(RESOURCE, taskName, new Object[] {},
					WTContext.getContext().getLocale());
		} catch (Exception e) {
			return taskName;
		}
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public WfActivity getActivity() {
		return activity;
	}

	public void setActivity(WfActivity activity) {
		this.activity = activity;
	}

	public Persistable getPbo() {
		return pbo;
	}

	public void setPbo(Persistable pbo) {
		this.pbo = pbo;
	}

	public String getPboOid() {
		return pboOid;
	}

	public void setPboOid(String pboOid) {
		this.pboOid = pboOid;
	}

	public Object[] getEvent() {
		return event;
	}

	public void setEvent(Object[] event) {
		this.event = event;
	}

	public String getTaskKey() {
		return taskName;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
