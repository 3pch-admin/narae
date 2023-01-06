package ext.narae.datautility;

import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeRequest2;
import wt.util.WTException;
import wt.util.WTMessage;

import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.core.components.factory.dataUtilities.AttributeDataUtilityHelper;
import com.ptc.core.components.rendering.guicomponents.Label;
import com.ptc.core.components.rendering.guicomponents.UrlDisplayComponent;

import ext.narae.service.change.editor.BEContext;


public class ChangeNumberOnWorkItemDataUtility extends AbstractDataUtility {
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
        
        String number = null;
        String oid = null;
        if( datum instanceof WTChangeRequest2) {
	        WTChangeRequest2 change = (WTChangeRequest2)datum;
	        number = change.getNumber();
	        oid = change.getPersistInfo().getObjectIdentifier().getStringValue();
        } else if( datum instanceof WTChangeOrder2) {
        	WTChangeOrder2 change = (WTChangeOrder2)datum;
        	number = change.getNumber();
	        oid = change.getPersistInfo().getObjectIdentifier().getStringValue();
        }
        
//        StringBuffer buffer = new StringBuffer();
//        
//        if( datum instanceof WTChangeRequest2 )
//        	buffer.append("<a id='CHANGE_VIEW' href='app/#ptc1/narae/change/detailECR?from=worklist&oid=");
//        else if( datum instanceof WTChangeOrder2)
//        	buffer.append("<a id='CHANGE_VIEW' href='app/#ptc1/narae/change/detailECO?from=worklist&oid=");
//        
//        buffer.append(oid);
//        buffer.append("' ext:qtip='");
//        buffer.append(number);
//        buffer.append("' class='linkfont'>");
//        buffer.append(number);
//        buffer.append("</a>");
//        
//        nameComponent.setValue(buffer.toString());
//        
//        return nameComponent;
        String url = "";
		if( datum instanceof WTChangeRequest2 )
			url = BEContext.host+"app/#ptc1/narae/change/detailECR?from=normal&oid="+oid;
        else if( datum instanceof WTChangeOrder2)
        	url = BEContext.host+"app/#ptc1/narae/change/detailECO?from=normal&oid="+oid;
		
		String nameob = "Help";
		UrlDisplayComponent udc = new UrlDisplayComponent(nameob,url);	
		udc.setLabelForTheLink("Help");
		udc.setTarget("URLLocationPopup");
		udc.setToolTip(number); 
		udc.setId(number); 
		udc.setLink(url);
		udc.setLabelForTheLink(number);
		udc.setCheckXSS(false);
        return udc;
	}

}
