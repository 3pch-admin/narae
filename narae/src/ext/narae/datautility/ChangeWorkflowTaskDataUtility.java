// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ChangeWorkflowTaskDataUtility.java

package ext.narae.datautility;

import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.core.components.factory.dataUtilities.AttributeDataUtilityHelper;
import com.ptc.core.components.rendering.guicomponents.Label;
import com.ptc.core.components.rendering.guicomponents.UrlDisplayComponent;

import ext.narae.component.WorkItemVO;
import ext.narae.service.change.editor.BEContext;
import ext.narae.service.workflow.beans.WorkflowHelper2;
import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeRequest2;
import wt.lifecycle.LifeCycleManaged;
import wt.org.WTPrincipal;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.workflow.work.WorkItem;

public class ChangeWorkflowTaskDataUtility extends AbstractDataUtility
{

    public ChangeWorkflowTaskDataUtility()
    {
    }

    public Object getDataValue(String component_id, Object datum, ModelContext mc)
        throws WTException
    {
        Label nameComponent = new Label("");
        nameComponent.setColumnName(AttributeDataUtilityHelper.getColumnName(component_id, datum, mc));
        nameComponent.setId(component_id);
        WorkItem item = null;
        String oid = null;
        WorkItemVO itemVO = null;
        WTPrincipal principal = SessionHelper.manager.getPrincipal();
        if(datum instanceof LifeCycleManaged)
        {
            LifeCycleManaged change = (LifeCycleManaged)datum;
            item = WorkflowHelper2.getTargetWorkItem(principal, change);
            itemVO = new WorkItemVO(item);
        }
        String url = "";
        if(datum instanceof WTChangeRequest2)
            url = (new StringBuilder(String.valueOf(BEContext.host))).append("app/#ptc1/narae/change/detailECR?from=worklist&oid=").append(itemVO.getPboOid()).toString();
        else
        if(datum instanceof WTChangeOrder2)
            url = (new StringBuilder(String.valueOf(BEContext.host))).append("app/#ptc1/narae/change/detailECO?from=worklist&oid=").append(itemVO.getPboOid()).toString();
        String nameob = "Help";
        UrlDisplayComponent udc = new UrlDisplayComponent(nameob, url);
        udc.setLabelForTheLink("Help");
        udc.setTarget("URLLocationPopup");
        udc.setToolTip(itemVO.getTaskName());
        udc.setId(itemVO.getTaskName());
        udc.setLink(url);
        udc.setLabelForTheLink(itemVO.getTaskName());
        udc.setCheckXSS(false);
        return udc;
    }
}
