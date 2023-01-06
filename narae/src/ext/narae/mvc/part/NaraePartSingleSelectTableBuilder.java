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

import wt.change2.WTChangeRequest2;
import wt.epm.EPMDocument;
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
import wt.session.SessionHelper;
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

import ext.narae.service.CommonUtil2;
import ext.narae.service.org.People;

@ComponentBuilder("ext.narae.part.PartList.SingleSelect")
public class NaraePartSingleSelectTableBuilder extends AbstractComponentBuilder {
	private static String RESOURCE = "ext.narae.ui.common.resource.NaraeCommonRB";

	@Override
	public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
		// TODO Auto-generated method stub
		String state = (String) params.getParameter("state");
		String name = (String) params.getParameter("name");
		String number = (String) params.getParameter("number");
		String version = (String) params.getParameter("version");
		String spec = (String) params.getParameter("spec");
		String maker = (String) params.getParameter("maker");
		String startdate = (String) params.getParameter("startdate");
		String enddate = (String) params.getParameter("enddate");
		String creator = (String) params.getParameter("creator");
		String desc = (String) params.getParameter("desc");

		if (state != null && state.length() > 0) {
			state = state.trim();
		} else {
			state = null;
		}

		if (name != null && name.length() > 0) {
			name = name.trim();
		} else {
			name = null;
		}

		if (number != null && number.length() > 0) {
			number = number.trim();
		} else {
			number = null;
		}

		if (version != null && version.length() > 0) {
			version = version.trim();
		} else {
			version = null;
		}

		if (spec != null && spec.length() > 0) {
			spec = spec.trim();
		} else {
			spec = null;
		}

		if (maker != null && maker.length() > 0) {
			maker = maker.trim();
		} else {
			maker = null;
		}

		if (startdate != null && startdate.length() > 0) {
			startdate = startdate.trim();
		} else {
			startdate = null;
		}

		if (enddate != null && enddate.length() > 0) {
			enddate = enddate.trim();
		} else {
			enddate = null;
		}

		if (creator != null && creator.length() > 0) {
			creator = creator.trim();
		} else {
			creator = null;
		}

		if (desc != null && desc.length() > 0) {
			desc = desc.trim();
		} else {
			desc = null;
		}

		QuerySpec query = null;
		QueryResult qr = null;

		query = new QuerySpec(WTPart.class);
		int idx = query.addClassList(WTPart.class, true);
		// Default c/i c/o
		query.appendOpenParen();
		query.appendWhere(new SearchCondition(WTPart.class, "checkoutInfo.state", SearchCondition.EQUAL, "c/i"));
		query.appendOr();
		query.appendWhere(new SearchCondition(WTPart.class, "checkoutInfo.state", SearchCondition.EQUAL, "c/o"));
		query.appendCloseParen();

		// 상태
		if (state != null) {
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			query.appendWhere(new SearchCondition(WTPart.class, "state.state", SearchCondition.EQUAL, state),
					new int[] { idx });
		}

		if (number != null && number.trim().length() > 0) {
			if (query.getConditionCount() > 0)
				query.appendAnd();
//				query.appendWhere(new SearchCondition(WTPart.class, "master>number", SearchCondition.LIKE, "%"+number.trim()+"%"), new int[]{idx});
			StringSearch stringsearch = new StringSearch("number");
			stringsearch.setValue("%" + number.trim() + "%");
			query.appendWhere(stringsearch.getSearchCondition(WTPart.class), new int[] { idx });
		} else
			number = "";

		if (name != null && name.trim().length() > 0) {
			if (query.getConditionCount() > 0)
				query.appendAnd();
//				query.appendWhere(new SearchCondition(WTPart.class, "master>name", SearchCondition.LIKE, "%"+name.trim()+"%"), new int[]{idx});
			StringSearch stringsearch = new StringSearch("name");
			stringsearch.setValue("%" + name.trim() + "%");
			query.appendWhere(stringsearch.getSearchCondition(WTPart.class), new int[] { idx });
		} else
			name = "";

		if (desc != null && desc.trim().length() > 0) {

			AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath("description");

			if (aview != null) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				int _idx = query.appendClassList(StringValue.class, false);
				query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", WTPart.class,
						"thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
				query.appendAnd();
				query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID",
						SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
				query.appendAnd();
				query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE,
						("%" + desc.trim() + "%").toUpperCase()), new int[] { _idx });
			}
		}

		// Spec
		if (spec != null && spec.trim().length() > 0) {

			AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath("Spec");

			if (aview != null) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				int _idx = query.appendClassList(StringValue.class, false);
				query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", WTPart.class,
						"thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
				query.appendAnd();
				query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID",
						SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
				query.appendAnd();
				query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE,
						("%" + spec.trim() + "%").toUpperCase()), new int[] { _idx });
			}
		} else
			spec = "";

		// Maker
		if (maker != null && maker.trim().length() > 0) {

			AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath("Maker");

			if (aview != null) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				int _idx = query.appendClassList(StringValue.class, false);
				query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", WTPart.class,
						"thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
				query.appendAnd();
				query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID",
						SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
				query.appendAnd();
				query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE,
						("%" + maker.trim() + "%").toUpperCase()), new int[] { _idx });
			}
		} else
			maker = "";

		// Creator
		if (creator != null && creator.length() > 0) {
			People people = (People) CommonUtil2.getInstance(creator);
			WTUser user = people.getUser();

			if (query.getConditionCount() > 0)
				query.appendAnd();
			query.appendWhere(new SearchCondition(WTPart.class, "iterationInfo.creator.key", "=",
					PersistenceHelper.getObjectIdentifier(user)), new int[] { idx });
		}
//		Enumeration principals = null;
//		List tempUserList = new ArrayList();
//        if ( creator != null && creator.length() > 0 ) {
//        	creator = creator.trim();
//        	principals = OrganizationServicesHelper.manager.findUser(WTUser.AUTHENTICATION_NAME,"*"+creator+"*");
//        	while( principals.hasMoreElements() ) tempUserList.add(principals.nextElement());
//        } else creator = "";
//        
//        if( tempUserList.size() > 0 ) {
//        	if (query.getConditionCount() > 0)
//        		query.appendAnd();
//			
//		    System.out.println("tempUserList count=" + tempUserList.size());
//											
//			if( tempUserList.size() > 1 ) query.appendOpenParen();
//								
//			for( int i=0; i < tempUserList.size(); i++ )
//			{
//		  	    WTPrincipal principal = (WTPrincipal)tempUserList.get(i);			
//		  	    ClassAttribute classAttribute = new ClassAttribute(WTPart.class, "iterationInfo.creator.key.id");
//			    ColumnExpression expression = ConstantExpression.newExpression(new Long(PersistenceHelper.getObjectIdentifier(principal).getId()), classAttribute.getColumnDescriptor().getJavaType());
//			    query.appendWhere(new SearchCondition(classAttribute, SearchCondition.EQUAL, expression));
//			    if( tempUserList.size() > 1 && (tempUserList.size()-1) != i) query.appendOr();
//			}
//								
//			if( tempUserList.size() > 1 ) query.appendCloseParen();
//		} 

		if (startdate != null || enddate != null) {
			if (query.getConditionCount() > 0)
				query.appendAnd();

			if (startdate != null && enddate == null) {
				query.appendWhere(new SearchCondition(WTPart.class, "thePersistInfo.createStamp",
						SearchCondition.GREATER_THAN_OR_EQUAL, Timestamp.valueOf(startdate + " 00:00:00")));
			} else if (startdate == null && enddate != null) {
				query.appendWhere(new SearchCondition(WTPart.class, "thePersistInfo.createStamp",
						SearchCondition.LESS_THAN_OR_EQUAL, Timestamp.valueOf(enddate + " 23:59:59")));
			} else {
				query.appendOpenParen();
				query.appendWhere(new SearchCondition(WTPart.class, "thePersistInfo.createStamp",
						SearchCondition.GREATER_THAN_OR_EQUAL, Timestamp.valueOf(startdate + " 00:00:00")));
				query.appendAnd();
				query.appendWhere(new SearchCondition(WTPart.class, "thePersistInfo.createStamp",
						SearchCondition.LESS_THAN_OR_EQUAL, Timestamp.valueOf(enddate + " 23:59:59")));
				query.appendCloseParen();
			}
		}

		if (query.getConditionCount() > 0) {
			// 최신 이터레이션
//			query.appendAnd();
//			query.appendWhere(VersionControlHelper.getSearchCondition(WTPart.class, true), new int[] { idx });
//			System.out.println(query);
//			QueryResult result = PersistenceHelper.manager.find(query);
//			System.out.println("Size=" + result.size());
//			return result;
			// 최신 이터레이션
			query.appendAnd();
			query.appendWhere(VersionControlHelper.getSearchCondition(WTPart.class, true));

			QueryResult result = null;
			if (version != null) {
				if ("true".equals(version)) {
					LatestConfigSpec latestCSpec = new LatestConfigSpec();
					System.out.println(query);
					result = ConfigHelper.service.queryIterations(query, latestCSpec);
				} else {
					result = PersistenceHelper.manager.find(query);
				}
			}
			return result;
		} else {
			return new QueryResult();
		}
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams arg0) throws WTException {
		// TODO Auto-generated method stub
		ComponentConfigFactory factory = getComponentConfigFactory();

		JcaTableConfig table = (JcaTableConfig) factory.newTableConfig();
		table.setLabel(WTMessage.getLocalizedMessage(RESOURCE, "SEARCH_PART", new Object[] {},
				WTContext.getContext().getLocale()));
		table.setDataSourceMode(DataSourceMode.ASYNCHRONOUS);
		table.setSelectable(true);
		table.setSingleSelect(true);
		table.setAutoGenerateRowId(true);
		table.setShowCount(true);
		// set the actionModel that comes in the TableToolBar
		// table.setActionModel("drawing_search_action");

		// add columns
		table.addComponent(factory.newColumnConfig(ICON, true));

		// thumbnail
		ColumnConfig thumb = factory.newColumnConfig("smallThumbnail", true);
		table.addComponent(thumb);

		ColumnConfig numberCol = factory.newColumnConfig(NUMBER, true);
		numberCol.setLabel(WTMessage.getLocalizedMessage(RESOURCE, "PART_NUMBER", new Object[] {},
				WTContext.getContext().getLocale()));
		table.addComponent(numberCol);
		// table.addComponent(factory.newColumnConfig(FORMAT_ICON, false));
		ColumnConfig nameCol = factory.newColumnConfig(NAME, true);
		nameCol.setLabel(WTMessage.getLocalizedMessage(RESOURCE, "PART_NAME", new Object[] {},
				WTContext.getContext().getLocale()));
		table.addComponent(nameCol);

		// mark this column as hidden(try to make this column visible in the UI)

		table.addComponent(factory.newColumnConfig(ORG_ID, false));
		table.addComponent(factory.newColumnConfig(INFO_ACTION, false));
		ColumnConfig nmActionsCol = factory.newColumnConfig(NM_ACTIONS, false);
		// specify the actionModel for the action column
//        ((JcaColumnConfig) nmActionsCol)
//                .setActionModel("search_sample01_action");
//        table.addComponent(nmActionsCol);
//
//        table.addComponent(factory.newColumnConfig(SHARE_STATUS_FAMILY, false));
//        table.addComponent(factory.newColumnConfig(GENERAL_STATUS_FAMILY, false));
//        table.addComponent(factory.newColumnConfig(CHANGE_STATUS_FAMILY, false));

		// State column is a DataStore-only column (please check in the UI whether this
		// column is listed)

		// strikeThrough column handling : "strikeThroughRow" should be the ID as the
		// default DataUtility is mapped to
		// this
//        ColumnConfig strikeThroughColumn = factory.newColumnConfig("strikeThroughRow", false);
//        strikeThroughColumn.setDataStoreOnly(true);
//        // map it to endItem attribute
//        strikeThroughColumn.setNeed("endItem");
//        table.addComponent(strikeThroughColumn);
//        table.setStrikeThroughColumn(strikeThroughColumn);

//        table.addComponent(factory.newColumnConfig(VERSION, true));

		ColumnConfig versionColumn = factory.newColumnConfig(VERSION, true);
		table.addComponent(versionColumn);

		ColumnConfig stateColumn = factory.newColumnConfig(STATE, true);
		// stateColumn.setDataStoreOnly(true);
		table.addComponent(stateColumn);

		ColumnConfig creatorCol = factory.newColumnConfig(CREATED_BY, true);
		creatorCol.setDataUtilityId("userNameDataUtility");
		table.addComponent(creatorCol);

		ColumnConfig last_modified = factory.newColumnConfig(CREATED, true);
		last_modified.setDefaultSort(true);
		last_modified.setAscending(false);
		table.addComponent(last_modified);

		table.addComponent(factory.newColumnConfig(LAST_MODIFIED, true));

		ColumnConfig pdfCol = factory.newColumnConfig("PDF", true);
		pdfCol.setDataUtilityId("pdfColumnDataUtility");
		table.addComponent(pdfCol);

		return table;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
