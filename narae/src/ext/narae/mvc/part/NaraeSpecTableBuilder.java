package ext.narae.mvc.part;

import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.CREATED;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.CREATED_BY;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.ICON;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.INFO_ACTION;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.LAST_MODIFIED;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.NAME;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.NM_ACTIONS;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.NUMBER;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.ORG_ID;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.STATE;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.VERSION;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.StringValue;
import wt.org.OrganizationServicesHelper;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.query.ClassAttribute;
import wt.query.ColumnExpression;
import wt.query.ConstantExpression;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.StringSearch;
import wt.util.WTContext;
import wt.util.WTException;
import wt.util.WTMessage;
import wt.vc.VersionControlHelper;

import com.ptc.jca.mvc.components.JcaTableConfig;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.ds.DataSourceMode;

import ext.narae.util.code.NumberCode;

@ComponentBuilder("ext.narae.part.SpecList")
public class NaraeSpecTableBuilder extends AbstractComponentBuilder {
	private static String RESOURCE = "ext.narae.ui.common.resource.NaraeCommonRB";

	@Override
	public Object buildComponentData(ComponentConfig config, ComponentParams params)
			throws Exception {
		// TODO Auto-generated method stub
		String name = (String) params.getParameter("name");
		String description =  (String) params.getParameter("description");

        if ( name != null && name.length() > 0 )  {
        	name = name.trim();
        } else {
        	name = null;
        }
        
        if ( description != null && description.length() > 0 )  {
        	description = description.trim();
        } else {
        	description = null;
        }
        
        QuerySpec query = null;
		QueryResult qr = null;

		query = new QuerySpec(NumberCode.class);
		query.appendWhere(new SearchCondition(NumberCode.class, "codeType", SearchCondition.EQUAL, "SPEC"));
   
		//상태
		if(name != null ) {
		    if(query.getConditionCount()>0) { query.appendAnd(); }
		    query.appendWhere(new SearchCondition(NumberCode.class, "name", SearchCondition.LIKE, "%"+name.toUpperCase()+"%"));
		}
		
		if(description != null ) {
		    if(query.getConditionCount()>0) { query.appendAnd(); }
		    query.appendWhere(new SearchCondition(NumberCode.class, "description", SearchCondition.LIKE, "%"+description.toUpperCase()+"%"));
		}
		
		QueryResult result = PersistenceHelper.manager.find(query);
		return result;
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams arg0)
			throws WTException {
		// TODO Auto-generated method stub
		ComponentConfigFactory factory = getComponentConfigFactory();

        JcaTableConfig table = (JcaTableConfig)factory.newTableConfig();
        table.setLabel(WTMessage.getLocalizedMessage(RESOURCE , "PART_MNG_SEARCH_SPEC_001", new Object[]{}, WTContext.getContext().getLocale()));
        table.setDataSourceMode(DataSourceMode.ASYNCHRONOUS);
        table.setSelectable(true);
        table.setAutoGenerateRowId(true);
        table.setShowCount(true);
        
        ColumnConfig numberCol = factory.newColumnConfig("code", true);
        numberCol.setLabel(WTMessage.getLocalizedMessage(RESOURCE , "CODE", new Object[]{}, WTContext.getContext().getLocale()));
        table.addComponent(numberCol);
        
        ColumnConfig nameCol = factory.newColumnConfig("name", true);
        nameCol.setLabel(WTMessage.getLocalizedMessage(RESOURCE , "BUY_SPEC", new Object[]{}, WTContext.getContext().getLocale()));
        nameCol.setWidth(60);
        table.addComponent(nameCol);
        
        ColumnConfig descCol = factory.newColumnConfig("description", true);
        descCol.setLabel(WTMessage.getLocalizedMessage(RESOURCE , "MATERIAL_NAME", new Object[]{}, WTContext.getContext().getLocale()));
        descCol.setWidth(200);
        table.addComponent(descCol);
        
        ColumnConfig disableCol = factory.newColumnConfig("disable", true);
        disableCol.setLabel(WTMessage.getLocalizedMessage(RESOURCE , "SPEC_ACTIVE", new Object[]{}, WTContext.getContext().getLocale()));
        disableCol.setDataUtilityId("numberCodeDisableDataUtility");
        table.addComponent(disableCol);
               

        return table;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
