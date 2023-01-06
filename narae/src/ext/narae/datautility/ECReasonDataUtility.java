package ext.narae.datautility;

import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeRequest2;
import wt.util.WTException;
import wt.util.WTMessage;

import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.core.components.factory.dataUtilities.AttributeDataUtilityHelper;
import com.ptc.core.components.rendering.guicomponents.Label;

import ext.narae.service.iba.beans.AttributeHelper;

public class ECReasonDataUtility extends AbstractDataUtility {
	private static String RESOURCE = "ext.narae.ui.common.resource.RequestTypeRB";

	@Override
	public Object getDataValue(String component_id, Object datum, ModelContext mc)
			throws WTException {
		// TODO Auto-generated method stub
//		System.out.println("component_id=" + component_id);
//		System.out.println("datum=" + datum);
//		System.out.println("mc=" + mc.getLocale());
		Label nameComponent = new Label("");
		 
        nameComponent.setColumnName(AttributeDataUtilityHelper.getColumnName(component_id, datum, mc));
        nameComponent.setId(component_id);
        
        String reason = "";
        if( datum instanceof WTChangeRequest2) {
	        WTChangeRequest2 change = (WTChangeRequest2)datum;
	        reason = ((String)AttributeHelper.service.getValue(change, "EC_Reason")).trim();
        } else if( datum instanceof WTChangeOrder2) {
        	WTChangeOrder2 change = (WTChangeOrder2)datum;
	        reason = ((String)AttributeHelper.service.getValue(change, "EC_Reason")).trim();
        }
        
        if( reason != null && reason.length() > 0) {
	        String[] reasons = reason.split("[,]");
	        
	        String display = "";
	        
	        for( int i=0; i < reasons.length; i++) {
	        	if( i == 0 ) {
	        		display = WTMessage.getLocalizedMessage(RESOURCE , reasons[i], new Object[]{}, mc.getLocale());
	        	} else {
	        		display = display + ", " + WTMessage.getLocalizedMessage(RESOURCE , reasons[i], new Object[]{}, mc.getLocale());
	        	}
	        }
	        
	        nameComponent.setValue(display);
        }
        
        return nameComponent;
	}

}
