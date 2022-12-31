package ext.narae.mvc.change;

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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.logging.log4j.Logger;

import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeRequest2;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.StringValue;
import wt.log4j.LogR;
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
import wt.session.SessionHelper;
import wt.type.ClientTypedUtility;
import wt.type.TypeDefinitionForeignKey;
import wt.type.TypeDefinitionReference;
import wt.util.WTContext;
import wt.util.WTException;
import wt.util.WTMessage;
import wt.vc.VersionControlHelper;
import wt.vc.config.ConfigHelper;
import wt.vc.config.LatestConfigSpec;

import com.ptc.jca.mvc.components.JcaTableConfig;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.ds.DataSourceMode;

import ext.narae.mvc.drawing.NaraeDrawingTableBuilder;

@ComponentBuilder("ext.narae.change.ECOList")
public class NaraeECOTableBuilder extends AbstractComponentBuilder {
	//private static Logger log = Logger.getLogger(NaraeDrawingTableBuilder.class.getName());
	private static final Logger log = LogR.getLoggerInternal(NaraeDrawingTableBuilder.class.getName());
	private static String RESOURCE = "ext.narae.ui.common.resource.NaraeCommonRB";

	@Override
	public Object buildComponentData(ComponentConfig config, ComponentParams params)
			throws Exception {
		// TODO Auto-generated method stub
		String name = (String) params.getParameter("name");
        String number = (String) params.getParameter("number");
        String project = (String) params.getParameter("project");
        String state = (String) params.getParameter("state");
        String first = (String) params.getParameter("first");
        String page = (String) params.getParameter("page");
        if( page == null ) page = "";
        
        if ( name != null && name.length() > 0 )  {
        	name = name.trim();
        } else {
        	name = null;
        }
        
        if ( number != null && number.length() > 0 )  {
        	number = number.trim();
        } else {
        	number = null;
        }
        
        if ( project != null && project.length() > 0 )  {
        	project = project.trim();
        } else {
        	project = null;
        }
        
        if ( state != null && state.length() > 0 )  {
        	state = state.trim();
        } else {
        	state = null;
        }
        
        QuerySpec query = null;
		QueryResult qr = null;

		query = new QuerySpec(WTChangeOrder2.class);
		int idx = query.addClassList(WTChangeOrder2.class, true);
		// APPEND SOFTTYPE OBJECT IN WHERE
		TypeDefinitionReference tdr1 = ClientTypedUtility.getTypeDefinitionReference("wt.change2.WTChangeOrder2|ext.narae.ECO");

		log.debug(tdr1);
		if (tdr1 != null) {
			query.appendWhere(new SearchCondition(WTChangeOrder2.class,
					WTChangeOrder2.TYPE_DEFINITION_REFERENCE + "."
					+ TypeDefinitionReference.KEY + "."
					+ TypeDefinitionForeignKey.BRANCH_ID,
					SearchCondition.EQUAL, tdr1.getKey().getBranchId()),
					new int[]{0});
		}
			
		if(number != null && number.trim().length() > 0){
				if (query.getConditionCount() > 0)
					query.appendAnd();
				StringSearch stringsearch = new StringSearch("number");
				stringsearch.setValue("%"+number.trim()+"%");
				query.appendWhere(stringsearch.getSearchCondition(WTChangeOrder2.class),new int[]{idx});
		} else number = "";	

		if(name != null && name.trim().length() > 0){
				if (query.getConditionCount() > 0)
					query.appendAnd();
				StringSearch stringsearch = new StringSearch("name");
				stringsearch.setValue("%"+name.trim()+"%");
				query.appendWhere(stringsearch.getSearchCondition(WTChangeOrder2.class),new int[]{idx});
		} else name = "";	
		
		if( state != null) {
			if (query.getConditionCount() > 0)
				query.appendAnd();
			query.appendWhere(
				new SearchCondition(WTChangeOrder2.class, 
						"state.state", 
						SearchCondition.EQUAL, 
						state)
				);
		}
		
		if(project != null && project.trim().length() > 0) {
			System.out.println("Set Project Query");
	        
	        AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath("Project");

	        if (aview != null) {
	            if (query.getConditionCount() > 0) { query.appendAnd(); }
	            int _idx = query.appendClassList(StringValue.class, false);
	            query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id",
	            		WTChangeRequest2.class, "thePersistInfo.theObjectIdentifier.id"),
	                              new int[] { _idx, idx });
	            query.appendAnd();
	            query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID",
	                                                  SearchCondition.EQUAL, aview.getHierarchyID()),
	                              new int[] { _idx });
	            query.appendAnd();
	            query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE, ("%"+project.trim()+"%").toUpperCase()),
	                    new int[] { _idx });
	        }
		}
		
		// First open ECR Search 
		log.debug("------>first:" + first);
		log.debug("------>page:" + page);
        if( first.equals("true") && !page.equals("picker") ) {
        	if (query.getConditionCount() > 0)
        		query.appendAnd();
        	WTPrincipal principal = SessionHelper.manager.getPrincipal();
        	ClassAttribute classAttribute = new ClassAttribute(WTChangeOrder2.class, "iterationInfo.creator.key.id");
		    ColumnExpression expression = ConstantExpression.newExpression(new Long(PersistenceHelper.getObjectIdentifier(principal).getId()), classAttribute.getColumnDescriptor().getJavaType());
		    query.appendWhere(new SearchCondition(classAttribute, SearchCondition.EQUAL, expression));
        }
		
		if( query.getConditionCount() > 0 ) {
			// Except In work
			if (query.getConditionCount() > 0)
        		query.appendAnd();
		    query.appendWhere(new SearchCondition(WTChangeOrder2.class, "state.state", SearchCondition.NOT_EQUAL, "INWORK"));
			//최신 이터레이션
//			query.appendAnd();
//			query.appendWhere(VersionControlHelper.getSearchCondition(WTPart.class, true), new int[] { idx });
//			log.debug(query);
//			QueryResult result = PersistenceHelper.manager.find(query);
//			log.debug("Size=" + result.size());
//			return result;
			//최신 이터레이션
			//query.appendAnd();
			//query.appendWhere(VersionControlHelper.getSearchCondition(WTChangeOrder2.class, true));
			
			log.debug(query);
			//QueryResult result = PersistenceHelper.manager.find(query);
			
			LatestConfigSpec latestCSpec = new LatestConfigSpec();
			QueryResult result = ConfigHelper.service.queryIterations(query, null);
			return result;
		} else {
			return new QueryResult();
		}
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams arg0)
			throws WTException {
		// TODO Auto-generated method stub
		ComponentConfigFactory factory = getComponentConfigFactory();

        JcaTableConfig table = (JcaTableConfig)factory.newTableConfig();
        table.setLabel(WTMessage.getLocalizedMessage(RESOURCE , "ECO_SEARCH", new Object[]{}, WTContext.getContext().getLocale()));
        table.setDataSourceMode(DataSourceMode.ASYNCHRONOUS);
        table.setSelectable(true);
        table.setAutoGenerateRowId(true);
        table.setShowCount(true);
        // set the actionModel that comes in the TableToolBar
        //table.setActionModel("drawing_search_action");

        // add columns
        table.addComponent(factory.newColumnConfig(ICON, true));

        ColumnConfig numberCol = factory.newColumnConfig(NUMBER, true);
        numberCol.setDataUtilityId("changeNumberDataUtility");
        numberCol.setLabel(WTMessage.getLocalizedMessage(RESOURCE , "ECR_NUMBER", new Object[]{}, WTContext.getContext().getLocale()));
        table.addComponent(numberCol);
        //table.addComponent(factory.newColumnConfig(FORMAT_ICON, false));
        ColumnConfig nameCol = factory.newColumnConfig(NAME, true);
        nameCol.setLabel(WTMessage.getLocalizedMessage(RESOURCE , "ECR_NAME", new Object[]{}, WTContext.getContext().getLocale()));
        table.addComponent(nameCol);
        
        table.addComponent(factory.newColumnConfig(ORG_ID, false));
        //table.addComponent(factory.newColumnConfig(INFO_ACTION, false));
        //ColumnConfig nmActionsCol = factory.newColumnConfig(NM_ACTIONS, false);
        
        ColumnConfig reasonCol = factory.newColumnConfig("EC_Reason", true);
        reasonCol.setLabel(WTMessage.getLocalizedMessage(RESOURCE , "ECR_REASON", new Object[]{}, WTContext.getContext().getLocale()));
        reasonCol.setDataUtilityId("ecReasonDataUtility");
        table.addComponent(reasonCol);
        
        ColumnConfig stateColumn = factory.newColumnConfig(STATE, true);
        //stateColumn.setDataStoreOnly(true);
        table.addComponent(stateColumn);
        
        ColumnConfig creatorCol = factory.newColumnConfig(CREATED_BY, true);
        creatorCol.setLabel(WTMessage.getLocalizedMessage(RESOURCE , "ECO_CREATOR", new Object[]{}, WTContext.getContext().getLocale()));
        creatorCol.setDataUtilityId("userNameDataUtility");
        table.addComponent(creatorCol);        
        
        
        ColumnConfig last_modified = factory.newColumnConfig(CREATED, true);
        last_modified.setDefaultSort(true);
        last_modified.setAscending(false);
        table.addComponent(last_modified);
        
        table.addComponent(factory.newColumnConfig(LAST_MODIFIED, true));        

        return table;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
