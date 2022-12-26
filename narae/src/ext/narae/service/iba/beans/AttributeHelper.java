package ext.narae.service.iba.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import wt.doc.WTDocument;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.fc.WTReference;
import wt.util.WTException;

public class AttributeHelper {
	public static final AttributeService service = new AttributeService();
	
	public static WTObject getInstance(String oid) throws WTException {
		ReferenceFactory referencefactory = new ReferenceFactory();
		WTReference wtreference = referencefactory.getReference( oid ); 
		return (WTObject)wtreference.getObject();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String oid = "OR:wt.doc.WTDocument:172165"; 
		try {
			WTDocument activity = (WTDocument)AttributeHelper.getInstance(oid);
			
			// Test1
			Object value = AttributeHelper.service.getValue(activity, "IBA1");
			System.out.println(value);
			value = AttributeHelper.service.getValue(activity, "description");
			System.out.println(value);
			
			// Test2
			List<String> attributes = new ArrayList<String>();
			attributes.add("IBA1");
			attributes.add("description");
			HashMap<String,Object> values = AttributeHelper.service.getValue(activity, attributes);
			System.out.println(values);
			
			values.put("IBA1", "CCCC2");
			values.put("description", "CCCC1");
			AttributeHelper.service.setValue(activity, values);
			values = AttributeHelper.service.getValue(activity, attributes);
			System.out.println(values);
			
			/**
			HuaweiAttributeHelper.service.setValue(activity, "TEST_STRING", "22222222222");
			value = HuaweiAttributeHelper.service.getValue(activity, "TEST_STRING");
			System.out.println(value);
			**/ 
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
