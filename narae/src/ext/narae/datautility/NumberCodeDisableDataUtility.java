package ext.narae.datautility;

import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.core.components.factory.dataUtilities.AttributeDataUtilityHelper;
import com.ptc.core.components.rendering.guicomponents.Label;

import ext.narae.util.code.NumberCode;
import wt.util.WTException;

public class NumberCodeDisableDataUtility extends AbstractDataUtility {

	@Override
	public Object getDataValue(String component_id, Object datum, ModelContext mc)
			throws WTException {
		// TODO Auto-generated method stub
		Label nameComponent = new Label("");
		 
        nameComponent.setColumnName(AttributeDataUtilityHelper.getColumnName(component_id, datum, mc));
        nameComponent.setId(component_id);
        
        NumberCode request = (NumberCode)datum;
        if( request.isDisabled() ) {
        	nameComponent.setValue("false" );
        } else {
        	nameComponent.setValue("true" );
        }
        
        
        return nameComponent;
	}

}
