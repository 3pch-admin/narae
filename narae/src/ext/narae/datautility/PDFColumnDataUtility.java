package ext.narae.datautility;

import java.util.List;

import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeRequest2;
import wt.epm.EPMDocument;
import wt.part.WTPart;
import wt.util.WTException;

import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.core.components.factory.dataUtilities.AttributeDataUtilityHelper;
import com.ptc.core.components.rendering.guicomponents.Label;
import com.ptc.core.components.rendering.guicomponents.UrlDisplayComponent;

import ext.narae.service.drawing.beans.DrawingHelper2;

public class PDFColumnDataUtility extends AbstractDataUtility {
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
        String buffer = "&nbsp;";
        
//        WTPart part = (WTPart)datum;
//        EPMDocument modelEpm = DrawingHelper2.getEPMDocument(part);
//        List<EPMDocument> drawingEpm = null;
//        if( modelEpm != null ) {
//        	EPMDocument one2D = DrawingHelper2.getRelational2DCad(modelEpm);
//        	if( one2D != null ) {
//        		buffer = buffer + DrawingHelper2.getPDFFile(one2D, modelEpm.getNumber());
//        	}
//        	
//        }
//        
//        nameComponent.setValue(buffer);
        
        String url = "";
        WTPart part = (WTPart)datum;
        EPMDocument modelEpm = DrawingHelper2.getEPMDocument(part);
        List<EPMDocument> drawingEpm = null;
        if( modelEpm != null ) {
        	EPMDocument one2D = DrawingHelper2.getRelational2DCad(modelEpm);
        	if( one2D != null ) {
        		url = DrawingHelper2.getPDFFileURL(one2D, modelEpm.getNumber());
        	}
        }
        if("".equals(url)) {
        	number = "X";
        }else {
        	number = "O";
        }
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
