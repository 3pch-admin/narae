package ext.narae.datautility;

import wt.change2.ChangeHelper2;
import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeRequest2;
import wt.fc.QueryResult;
import wt.util.WTException;

import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.core.components.factory.dataUtilities.AttributeDataUtilityHelper;
import com.ptc.core.components.rendering.guicomponents.Label;

public class RelatedECODataUtility extends AbstractDataUtility {
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
        
        WTChangeRequest2 change = (WTChangeRequest2)datum;
        QueryResult result = ChangeHelper2.service.getChangeOrders(change);
	    
        String display = "";
        if( result.size() > 0 ) {
        	//display = display + "<div style='word-wrap:break-word'>";
        	WTChangeOrder2 oneReq = null;
        	while( result.hasMoreElements() ) {
        		oneReq = (WTChangeOrder2)result.nextElement();
        		display = display + "<a id='ECOECO' href='' ext:qtip='" + oneReq.getNumber() + "' class='linkfont'>";
        		display = display + oneReq.getNumber();
        		display = display + "</a>,&nbsp;";
        	}
        	//display = display + "</div>";
        }
        nameComponent.setValue(display);
        
        return nameComponent;
	}

}
