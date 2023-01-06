package ext.narae.datautility;

import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeRequest2;
import wt.enterprise.Managed;
import wt.enterprise.RevisionControlled;
import wt.org.WTUser;
import wt.util.WTException;

import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.core.components.factory.dataUtilities.AttributeDataUtilityHelper;
import com.ptc.core.components.rendering.guicomponents.Label;

public class UserNameDataUtility extends AbstractDataUtility {

	@Override
	public Object getDataValue(String component_id, Object datum, ModelContext mc)
			throws WTException {
		// TODO Auto-generated method stub
		Label nameComponent = new Label("");
		 
        nameComponent.setColumnName(AttributeDataUtilityHelper.getColumnName(component_id, datum, mc));
        nameComponent.setId(component_id);
        
        if( datum instanceof WTChangeRequest2 ) {
        	WTChangeRequest2 request = (WTChangeRequest2)datum;
        	WTUser user = (WTUser)request.getCreator().getObject();
        	nameComponent.setValue(user.getFullName() );
        } else if ( datum instanceof WTChangeOrder2 ) {
        	WTChangeOrder2 order = (WTChangeOrder2)datum;
        	WTUser user = (WTUser)order.getCreator().getObject();
        	nameComponent.setValue(user.getFullName() );
        } else if ( datum instanceof RevisionControlled ) {
        	RevisionControlled order = (RevisionControlled)datum;
        	WTUser user = (WTUser)order.getCreator().getObject();
        	nameComponent.setValue(user.getFullName() );
        } else if ( datum instanceof Managed ) {
        	Managed order = (Managed)datum;
        	WTUser user = (WTUser)order.getCreator().getObject();
        	nameComponent.setValue(user.getFullName() );
        }
        
        return nameComponent;
	}

}
