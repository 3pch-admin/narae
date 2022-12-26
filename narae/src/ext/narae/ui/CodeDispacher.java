package ext.narae.ui;

import ext.narae.service.ObjectCodeHelper;
import ext.narae.util.CommonUtil;
import ext.narae.util.code.NumberCode;
import ext.narae.util.code.NumberCode2;
import ext.narae.util.code.beans.NumberCodeHelper;
import wt.fc.QueryResult;
import wt.util.WTContext;
import wt.util.WTMessage;

public class CodeDispacher {
	private static String RESOURCE = "ext.narae.ui.common.resource.NaraeCommonRB";
	
	private static String TAG_START = "<";
	private static String TAG_END = ">";
	private static String BLANK = " ";
	private static String SLASH = "/";
	private static String SINGLE_QUATATION = "'";
	private static String EQUAL = "=";
	private static String TAG_COMMAND_OPTION = "OPTION";
	private static String TAG_ENV_VALUE = "value";
	private static String TAG_ENV_SELECTED = "selected";
	
	private static String AJAX_BLOCK = "$block$";
	private static String AJAX_ITEM = "$item$";
	private static String AJAX_ATTR = "$attr$";
	
	
	public static String getCadCreatorList() {
		QueryResult cadcreator = ObjectCodeHelper.getCode("CADCREATOR");
		
		StringBuffer buffer = new StringBuffer();
		NumberCode oneCode = null;
		
//		buffer.append("<OPTION>" + 
//				WTMessage.getLocalizedMessage(RESOURCE , "SELECT", new Object[]{}, WTContext.getContext().getLocale()) +
//				"</OPTION>");
		while(cadcreator.hasMoreElements()) {
			oneCode = (NumberCode)cadcreator.nextElement();
			buffer.append(TAG_START);
			buffer.append(TAG_COMMAND_OPTION + BLANK);
			if( oneCode.getCode().equals("N") ) 
				buffer.append(TAG_ENV_VALUE + EQUAL + SINGLE_QUATATION + oneCode.getPersistInfo().getObjectIdentifier().toString() + "," + oneCode.getCode() + SINGLE_QUATATION + BLANK + TAG_ENV_SELECTED + TAG_END);
			else
				buffer.append(TAG_ENV_VALUE + EQUAL + SINGLE_QUATATION + oneCode.getPersistInfo().getObjectIdentifier().toString() + "," + oneCode.getCode() + SINGLE_QUATATION + TAG_END);
			buffer.append(oneCode.getName() + "[" + oneCode.getCode() + "]");
			buffer.append(TAG_START + SLASH + TAG_COMMAND_OPTION + TAG_END);
			buffer.append("\n");
		}
		
		return buffer.toString();
	}
	
	public static String getCadCreatorList2() {
		QueryResult cadcreator = ObjectCodeHelper.getCode2("CADCREATOR");
		
		StringBuffer buffer = new StringBuffer();
		NumberCode2 oneCode = null;
		
//		buffer.append("<OPTION>" + 
//				WTMessage.getLocalizedMessage(RESOURCE , "SELECT", new Object[]{}, WTContext.getContext().getLocale()) +
//				"</OPTION>");
		while(cadcreator.hasMoreElements()) {
			oneCode = (NumberCode2)cadcreator.nextElement();
			buffer.append(TAG_START);
			buffer.append(TAG_COMMAND_OPTION + BLANK);
			if( oneCode.getCode().equals("N") ) 
				buffer.append(TAG_ENV_VALUE + EQUAL + SINGLE_QUATATION + oneCode.getPersistInfo().getObjectIdentifier().toString() + "," + oneCode.getCode() + SINGLE_QUATATION + BLANK + TAG_ENV_SELECTED + TAG_END);
			else
				buffer.append(TAG_ENV_VALUE + EQUAL + SINGLE_QUATATION + oneCode.getPersistInfo().getObjectIdentifier().toString() + "," + oneCode.getCode() + SINGLE_QUATATION + TAG_END);
			buffer.append(oneCode.getName() + "[" + oneCode.getCode() + "]");
			buffer.append(TAG_START + SLASH + TAG_COMMAND_OPTION + TAG_END);
			buffer.append("\n");
		}
		
		return buffer.toString();
	}
	
	public static String getType() {
		QueryResult cadcreator = ObjectCodeHelper.getTopCode("CADATTRIBUTE");
		
		StringBuffer buffer = new StringBuffer();
		NumberCode oneCode = null;
		
		buffer.append("<OPTION>" + 
				WTMessage.getLocalizedMessage(RESOURCE , "SELECT", new Object[]{}, WTContext.getContext().getLocale()) +
				"</OPTION>");
		while(cadcreator.hasMoreElements()) {
			oneCode = (NumberCode)cadcreator.nextElement();
			buffer.append(TAG_START);
			buffer.append(TAG_COMMAND_OPTION + BLANK);
			buffer.append(TAG_ENV_VALUE + EQUAL + SINGLE_QUATATION + oneCode.getPersistInfo().getObjectIdentifier().toString() + "," + oneCode.getCode() + SINGLE_QUATATION + TAG_END);
			buffer.append(oneCode.getName() + "[" + oneCode.getCode() + "]");
			buffer.append(TAG_START + SLASH + TAG_COMMAND_OPTION + TAG_END);
			buffer.append("\n");
		}
		
		return buffer.toString();
	}
	
	public static String getType2() {
		QueryResult cadcreator = ObjectCodeHelper.getTopCode2("CADTYPE");
		
		StringBuffer buffer = new StringBuffer();
		NumberCode2 oneCode = null;
		
		buffer.append("<OPTION>" + 
				WTMessage.getLocalizedMessage(RESOURCE , "SELECT", new Object[]{}, WTContext.getContext().getLocale()) +
				"</OPTION>");
		while(cadcreator.hasMoreElements()) {
			oneCode = (NumberCode2)cadcreator.nextElement();
			buffer.append(TAG_START);
			buffer.append(TAG_COMMAND_OPTION + BLANK);
			buffer.append(TAG_ENV_VALUE + EQUAL + SINGLE_QUATATION + oneCode.getPersistInfo().getObjectIdentifier().toString() + "," + oneCode.getCode() + SINGLE_QUATATION + TAG_END);
			buffer.append(oneCode.getName() + "[" + oneCode.getCode() + "]");
			buffer.append(TAG_START + SLASH + TAG_COMMAND_OPTION + TAG_END);
			buffer.append("\n");
		}
		
		return buffer.toString();
	}
	
	public static String getAjaxSubTypes(String key, String parentOid) {
		QueryResult cadcreator = ObjectCodeHelper.getChildCode(key, parentOid);
		
		StringBuffer buffer = new StringBuffer();
		NumberCode oneCode = null;
		
		while(cadcreator.hasMoreElements()) {
			oneCode = (NumberCode)cadcreator.nextElement();
			buffer.append(oneCode.getName() + BLANK + "[" + oneCode.getCode() + "]");
			buffer.append(AJAX_ATTR);
			buffer.append(oneCode.getPersistInfo().getObjectIdentifier().toString() + "," + oneCode.getCode());
			buffer.append(AJAX_ITEM);
		}
		
		return buffer.toString();
	}
	public static String getAjaxSubTypes2(String key, String parentOid) {
		QueryResult cadcreator = ObjectCodeHelper.getChildCode2(key, parentOid);
		
		StringBuffer buffer = new StringBuffer();
		NumberCode2 oneCode = null;
		
		while(cadcreator.hasMoreElements()) {
			oneCode = (NumberCode2)cadcreator.nextElement();
			buffer.append(oneCode.getName() + BLANK + "[" + oneCode.getCode() + "]");
			buffer.append(AJAX_ATTR);
			buffer.append(oneCode.getPersistInfo().getObjectIdentifier().toString() + "," + oneCode.getCode());
			buffer.append(AJAX_ITEM);
		}
		
		return buffer.toString();
	}
	
	public static String getAjaxSubTypes(String key) {
		QueryResult cadcreator = ObjectCodeHelper.getCode(key);
		
		StringBuffer buffer = new StringBuffer();
		NumberCode oneCode = null;
		
		while(cadcreator.hasMoreElements()) {
			oneCode = (NumberCode)cadcreator.nextElement();
			buffer.append(oneCode.getName() + BLANK + "[" + oneCode.getCode() + "]");
			buffer.append(AJAX_ATTR);
			buffer.append(oneCode.getPersistInfo().getObjectIdentifier().toString() + "," + oneCode.getCode());
			buffer.append(AJAX_ITEM);
		}
		
		return buffer.toString();
	}
	
	public static String getAjaxSubTypes2(String key) {
		QueryResult cadcreator = ObjectCodeHelper.getCode2(key);
		
		StringBuffer buffer = new StringBuffer();
		NumberCode2 oneCode = null;
		
		while(cadcreator.hasMoreElements()) {
			oneCode = (NumberCode2)cadcreator.nextElement();
			buffer.append(oneCode.getName() + BLANK + "[" + oneCode.getCode() + "]");
			buffer.append(AJAX_ATTR);
			buffer.append(oneCode.getPersistInfo().getObjectIdentifier().toString() + "," + oneCode.getCode());
			buffer.append(AJAX_ITEM);
		}
		
		return buffer.toString();
	}
	
	public static String getAjaxProductCode() {
		
		
		StringBuffer buffer = new StringBuffer();
		NumberCode oneCode = null;
		
		QueryResult cadcreator = ObjectCodeHelper.getCode("SBUSINESS");
		while(cadcreator.hasMoreElements()) {
			oneCode = (NumberCode)cadcreator.nextElement();
			buffer.append(oneCode.getName() + BLANK + "[" + oneCode.getCode() + "]");
			buffer.append(AJAX_ATTR);
			buffer.append(oneCode.getPersistInfo().getObjectIdentifier().toString() + "," + oneCode.getCode());
			buffer.append(AJAX_ITEM);
		}
		buffer.append(AJAX_BLOCK);
		
		cadcreator = ObjectCodeHelper.getCode("SCUSTOMER");
		while(cadcreator.hasMoreElements()) {
			oneCode = (NumberCode)cadcreator.nextElement();
			buffer.append(oneCode.getName() + BLANK + "[" + oneCode.getCode() + "]");
			buffer.append(AJAX_ATTR);
			buffer.append(oneCode.getPersistInfo().getObjectIdentifier().toString() + "," + oneCode.getCode());
			buffer.append(AJAX_ITEM);
		}
		buffer.append(AJAX_BLOCK);
		
		cadcreator = ObjectCodeHelper.getCode("SCLASS1");
		while(cadcreator.hasMoreElements()) {
			oneCode = (NumberCode)cadcreator.nextElement();
			buffer.append(oneCode.getName() + BLANK + "[" + oneCode.getCode() + "]");
			buffer.append(AJAX_ATTR);
			buffer.append(oneCode.getPersistInfo().getObjectIdentifier().toString() + "," + oneCode.getCode());
			buffer.append(AJAX_ITEM);
		}
		buffer.append(AJAX_BLOCK);
		
		cadcreator = ObjectCodeHelper.getCode("SCLASS2");
		while(cadcreator.hasMoreElements()) {
			oneCode = (NumberCode)cadcreator.nextElement();
			buffer.append(oneCode.getName() + BLANK + "[" + oneCode.getCode() + "]");
			buffer.append(AJAX_ATTR);
			buffer.append(oneCode.getPersistInfo().getObjectIdentifier().toString() + "," + oneCode.getCode());
			buffer.append(AJAX_ITEM);
		}
		buffer.append(AJAX_BLOCK);
		
		cadcreator = ObjectCodeHelper.getCode("SCLASS3");
		while(cadcreator.hasMoreElements()) {
			oneCode = (NumberCode)cadcreator.nextElement();
			buffer.append(oneCode.getName() + BLANK + "[" + oneCode.getCode() + "]");
			buffer.append(AJAX_ATTR);
			buffer.append(oneCode.getPersistInfo().getObjectIdentifier().toString() + "," + oneCode.getCode());
			buffer.append(AJAX_ITEM);
		}
		buffer.append(AJAX_BLOCK);
		
		return buffer.toString();
	}
	
	public static String getAjaxProductCode2() {
		
		
		StringBuffer buffer = new StringBuffer();
		NumberCode2 oneCode = null;
		NumberCode2 parentCode = NumberCodeHelper.manager.getNumberCode2("GUBUN", "NS");
		String parentoid = "";
		if(null!=parentCode)
			parentoid =""+CommonUtil.getOIDLongValue(parentCode);
		QueryResult cadcreator = ObjectCodeHelper.getChildCode2("SBUSINESS",parentoid);
		while(cadcreator.hasMoreElements()) {
			oneCode = (NumberCode2)cadcreator.nextElement();
			buffer.append(oneCode.getName() + BLANK + "[" + oneCode.getCode() + "]");
			buffer.append(AJAX_ATTR);
			buffer.append(oneCode.getPersistInfo().getObjectIdentifier().toString() + "," + oneCode.getCode());
			buffer.append(AJAX_ITEM);
		}
		buffer.append(AJAX_BLOCK);
		
		cadcreator = ObjectCodeHelper.getChildCode2("SCUSTOMER",parentoid);
		while(cadcreator.hasMoreElements()) {
			oneCode = (NumberCode2)cadcreator.nextElement();
			buffer.append(oneCode.getName() + BLANK + "[" + oneCode.getCode() + "]");
			buffer.append(AJAX_ATTR);
			buffer.append(oneCode.getPersistInfo().getObjectIdentifier().toString() + "," + oneCode.getCode());
			buffer.append(AJAX_ITEM);
		}
		buffer.append(AJAX_BLOCK);
		
		cadcreator = ObjectCodeHelper.getChildCode2("SCLASS1",parentoid);
		while(cadcreator.hasMoreElements()) {
			oneCode = (NumberCode2)cadcreator.nextElement();
			buffer.append(oneCode.getName() + BLANK + "[" + oneCode.getCode() + "]");
			buffer.append(AJAX_ATTR);
			buffer.append(oneCode.getPersistInfo().getObjectIdentifier().toString() + "," + oneCode.getCode());
			buffer.append(AJAX_ITEM);
		}
		buffer.append(AJAX_BLOCK);
		
		cadcreator = ObjectCodeHelper.getChildCode2("SCLASS2",parentoid);
		while(cadcreator.hasMoreElements()) {
			oneCode = (NumberCode2)cadcreator.nextElement();
			buffer.append(oneCode.getName() + BLANK + "[" + oneCode.getCode() + "]");
			buffer.append(AJAX_ATTR);
			buffer.append(oneCode.getPersistInfo().getObjectIdentifier().toString() + "," + oneCode.getCode());
			buffer.append(AJAX_ITEM);
		}
		buffer.append(AJAX_BLOCK);
		
		cadcreator = ObjectCodeHelper.getChildCode2("SCLASS3",parentoid);
		while(cadcreator.hasMoreElements()) {
			oneCode = (NumberCode2)cadcreator.nextElement();
			buffer.append(oneCode.getName() + BLANK + "[" + oneCode.getCode() + "]");
			buffer.append(AJAX_ATTR);
			buffer.append(oneCode.getPersistInfo().getObjectIdentifier().toString() + "," + oneCode.getCode());
			buffer.append(AJAX_ITEM);
		}
		buffer.append(AJAX_BLOCK);
		
		return buffer.toString();
	}
	
	public static String getAjaxProductAssyCode(String key, String parentOid) {
		
		
		StringBuffer buffer = new StringBuffer();
		NumberCode oneCode = null;
				
		QueryResult cadcreator = ObjectCodeHelper.getCode("SCLASS1");
		while(cadcreator.hasMoreElements()) {
			oneCode = (NumberCode)cadcreator.nextElement();
			buffer.append(oneCode.getName() + BLANK + "[" + oneCode.getCode() + "]");
			buffer.append(AJAX_ATTR);
			buffer.append(oneCode.getPersistInfo().getObjectIdentifier().toString() + "," + oneCode.getCode());
			buffer.append(AJAX_ITEM);
		}
		buffer.append(AJAX_BLOCK);;
		
		cadcreator = ObjectCodeHelper.getChildCode(key, parentOid);
		while(cadcreator.hasMoreElements()) {
			oneCode = (NumberCode)cadcreator.nextElement();
			buffer.append(oneCode.getName() + BLANK + "[" + oneCode.getCode() + "]");
			buffer.append(AJAX_ATTR);
			buffer.append(oneCode.getPersistInfo().getObjectIdentifier().toString() + "," + oneCode.getCode());
			buffer.append(AJAX_ITEM);
		}
		buffer.append(AJAX_BLOCK);
		
		return buffer.toString();
	}
	
	public static String getAjaxProductAssyCode2(String key, String parentOid) {
		
		
		StringBuffer buffer = new StringBuffer();
		NumberCode2 oneCode = null;
				
		QueryResult cadcreator = ObjectCodeHelper.getChildCode2(key, parentOid);
		while(cadcreator.hasMoreElements()) {
			oneCode = (NumberCode2)cadcreator.nextElement();
			buffer.append(oneCode.getName() + BLANK + "[" + oneCode.getCode() + "]");
			buffer.append(AJAX_ATTR);
			buffer.append(oneCode.getPersistInfo().getObjectIdentifier().toString() + "," + oneCode.getCode());
			buffer.append(AJAX_ITEM);
		}
		buffer.append(AJAX_BLOCK);
		
		return buffer.toString();
	}
	
	public static String getAjaxGagongCode(String key, String parentOid) {
		
		
		StringBuffer buffer = new StringBuffer();
		NumberCode oneCode = null;
		
		NumberCode assy = ObjectCodeHelper.getTopCodeAssyPart(); 
				
		QueryResult cadcreator = ObjectCodeHelper.getChildCode("CADATTRIBUTE", assy.getPersistInfo().getObjectIdentifier().toString());
		while(cadcreator.hasMoreElements()) {
			oneCode = (NumberCode)cadcreator.nextElement();
			buffer.append(oneCode.getName() + BLANK + "[" + oneCode.getCode() + "]");
			buffer.append(AJAX_ATTR);
			buffer.append(oneCode.getPersistInfo().getObjectIdentifier().toString() + "," + oneCode.getCode());
			buffer.append(AJAX_ITEM);
		}
		buffer.append(AJAX_BLOCK);;
		
		cadcreator = ObjectCodeHelper.getChildCode(key, parentOid);
		while(cadcreator.hasMoreElements()) {
			oneCode = (NumberCode)cadcreator.nextElement();
			buffer.append(oneCode.getName() + BLANK + "[" + oneCode.getCode() + "]");
			buffer.append(AJAX_ATTR);
			buffer.append(oneCode.getPersistInfo().getObjectIdentifier().toString() + "," + oneCode.getCode());
			buffer.append(AJAX_ITEM);
		}
		buffer.append(AJAX_BLOCK);
		
		return buffer.toString();
	}
	
	public static String getAjaxGagongCode2(String key) {
		
		
		StringBuffer buffer = new StringBuffer();
		NumberCode2 oneCode = null;
		NumberCode2 parentCode = NumberCodeHelper.manager.getNumberCode2("GUBUN", "NP");
		String parentoid = "";
		if(null!=parentCode)
			parentoid =""+CommonUtil.getOIDLongValue(parentCode);
		QueryResult cadcreator = ObjectCodeHelper.getChildCode2(key,parentoid);
		while(cadcreator.hasMoreElements()) {
			oneCode = (NumberCode2)cadcreator.nextElement();
			buffer.append(oneCode.getName() + BLANK + "[" + oneCode.getCode() + "]");
			buffer.append(AJAX_ATTR);
			buffer.append(oneCode.getPersistInfo().getObjectIdentifier().toString() + "," + oneCode.getCode());
			buffer.append(AJAX_ITEM);
		}
		buffer.append(AJAX_BLOCK);
		
		
		cadcreator = ObjectCodeHelper.getChildCode2("CLASS1",parentoid);
		while(cadcreator.hasMoreElements()) {
			oneCode = (NumberCode2)cadcreator.nextElement();
			buffer.append(oneCode.getName() + BLANK + "[" + oneCode.getCode() + "]");
			buffer.append(AJAX_ATTR);
			buffer.append(oneCode.getPersistInfo().getObjectIdentifier().toString() + "," + oneCode.getCode());
			buffer.append(AJAX_ITEM);
		}
		buffer.append(AJAX_BLOCK);
		cadcreator = ObjectCodeHelper.getChildCode2("CLASS2",parentoid);
		while(cadcreator.hasMoreElements()) {
			oneCode = (NumberCode2)cadcreator.nextElement();
			buffer.append(oneCode.getName() + BLANK + "[" + oneCode.getCode() + "]");
			buffer.append(AJAX_ATTR);
			buffer.append(oneCode.getPersistInfo().getObjectIdentifier().toString() + "," + oneCode.getCode());
			buffer.append(AJAX_ITEM);
		}
		buffer.append(AJAX_BLOCK);
		
		return buffer.toString();
	}
	
	public static void main(String args[]) {
		String aa="NA-00012-1212";
		System.out.println(aa.substring(1,2));
		System.out.println(aa.substring(1,2));
	}
}
