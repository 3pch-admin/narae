package ext.narae.mvc.approval;

import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.CREATED_BY;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.ICON;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.LAST_MODIFIED;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.NAME;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.NM_ACTIONS;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.NUMBER;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.ORG_ID;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.STATE;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.ptc.jca.mvc.components.JcaColumnConfig;
import com.ptc.jca.mvc.components.JcaTableConfig;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.ds.DataSourceMode;

import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeRequest2;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.OrganizationServicesHelper;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.query.ClassAttribute;
import wt.query.ColumnExpression;
import wt.query.ConstantExpression;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.util.WTContext;
import wt.util.WTException;
import wt.util.WTMessage;

@ComponentBuilder(value = "ext.narae.approval.MyWork")
public class NaraeMyWorkTableBuilder extends AbstractComponentBuilder {
	private static String RESOURCE = "ext.narae.ui.common.resource.NaraeCommonRB";

	@Override
	public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
		// TODO Auto-generated method stub
		String first = (String) params.getParameter("first");
		String name = (String) params.getParameter("name");
		String startdate = (String) params.getParameter("startdate");
		String enddate = (String) params.getParameter("enddate");
		String creator = (String) params.getParameter("creator");
		String state = (String) params.getParameter("state");
		Enumeration principals = null;
		if (name != null && name.length() > 0) {
			name = name.trim();
		} else {
			name = null;
		}

		if (startdate != null && startdate.length() > 0)
			startdate = startdate.trim();
		else
			startdate = null;

		if (enddate != null && enddate.length() > 0)
			enddate = enddate.trim();
		else
			enddate = null;

		List tempUserList = new ArrayList();
		if (creator != null && creator.length() > 0) {
			creator = creator.trim();
			principals = OrganizationServicesHelper.manager.findUser(WTUser.AUTHENTICATION_NAME, "*" + creator + "*");
			while (principals.hasMoreElements())
				tempUserList.add(principals.nextElement());
		} else {
			if (first != null) {
				if (first.equals("true")) {
					WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
					principals = OrganizationServicesHelper.manager.findUser(WTUser.AUTHENTICATION_NAME,
							"*" + user.getAuthenticationName() + "*");
					while (principals.hasMoreElements())
						tempUserList.add(principals.nextElement());
				} else {
					creator = null;
				}
			} else {
				creator = null;
			}

		}

		if (state != null && state.length() > 0) {
			state = state.trim();
		} else {
			// 200305
//        	if(first.equals("true")) {
//        		state = "INWORK";
//        	} else {
//        		state = null;
//        	}
		}

		SearchCondition cond = null;
		boolean startAnd = false;

//        QuerySpec spec = new QuerySpec(VersionableChangeItem.class);
//
//        spec.appendOpenParen();
//		spec.appendWhere(new SearchCondition(VersionableChangeItem.class, "thePersistInfo.theObjectIdentifier.classname"
//				, SearchCondition.EQUAL,
//				"wt.change2.WTChangeRequest2"));
//		
//		spec.appendOr();
//		
//		spec.appendWhere(new SearchCondition(VersionableChangeItem.class, "thePersistInfo.theObjectIdentifier.classname"
//				, SearchCondition.EQUAL,
//				"wt.change2.WTChangeOrder2"));
//		spec.appendOpenParen();

		QuerySpec spec = new QuerySpec(WTChangeRequest2.class);

		if (name != null) {
			startAnd = true;
			if (name.contains("*")) {
				spec.appendWhere(new SearchCondition(WTChangeRequest2.class, "master>name", SearchCondition.LIKE,
						name.replace("*", "%")));
			} else {
				spec.appendWhere(
						new SearchCondition(WTChangeRequest2.class, "master>name", SearchCondition.EQUAL, name));
			}
		}

		if (startdate != null || enddate != null) {
			if (spec.getConditionCount() > 0)
				spec.appendAnd();

			if (startdate != null && enddate == null) {
				spec.appendWhere(new SearchCondition(WTChangeRequest2.class, "thePersistInfo.createStamp",
						SearchCondition.GREATER_THAN_OR_EQUAL, Timestamp.valueOf(startdate + " 00:00:00")));
			} else if (startdate == null && enddate != null) {
				spec.appendWhere(new SearchCondition(WTChangeRequest2.class, "thePersistInfo.createStamp",
						SearchCondition.LESS_THAN_OR_EQUAL, Timestamp.valueOf(enddate + " 23:59:59")));
			} else {
				spec.appendOpenParen();
				spec.appendWhere(new SearchCondition(WTChangeRequest2.class, "thePersistInfo.createStamp",
						SearchCondition.GREATER_THAN_OR_EQUAL, Timestamp.valueOf(startdate + " 00:00:00")));
				spec.appendAnd();
				spec.appendWhere(new SearchCondition(WTChangeRequest2.class, "thePersistInfo.createStamp",
						SearchCondition.LESS_THAN_OR_EQUAL, Timestamp.valueOf(enddate + " 23:59:59")));
				spec.appendCloseParen();
			}
		}

		if (tempUserList.size() > 0) {
			if (spec.getConditionCount() > 0)
				spec.appendAnd();

			if (tempUserList.size() > 1)
				spec.appendOpenParen();

			for (int i = 0; i < tempUserList.size(); i++) {
				WTPrincipal principal = (WTPrincipal) tempUserList.get(i);
				ClassAttribute classAttribute = new ClassAttribute(WTChangeRequest2.class, "creator.key.id");
				ColumnExpression expression = ConstantExpression.newExpression(
						new Long(PersistenceHelper.getObjectIdentifier(principal).getId()),
						classAttribute.getColumnDescriptor().getJavaType());
				spec.appendWhere(new SearchCondition(classAttribute, SearchCondition.EQUAL, expression));
				if (tempUserList.size() > 1 && (tempUserList.size() - 1) != i)
					spec.appendOr();
			}

			if (tempUserList.size() > 1)
				spec.appendCloseParen();
		} else if (creator != null && tempUserList.size() == 0) {
			if (spec.getConditionCount() > 0)
				spec.appendAnd();

			ClassAttribute classAttribute = new ClassAttribute(WTChangeRequest2.class, "creator.key.id");
			ColumnExpression expression = ConstantExpression.newExpression(new Long(0),
					classAttribute.getColumnDescriptor().getJavaType());
			spec.appendWhere(new SearchCondition(classAttribute, SearchCondition.EQUAL, expression));
		}

		if (state != null) {
			if (spec.getConditionCount() > 0)
				spec.appendAnd();

			if (first.equals("true")) {
				spec.appendOpenParen();
				spec.appendWhere(
						new SearchCondition(WTChangeRequest2.class, "state.state", SearchCondition.EQUAL, "INWORK"));
				spec.appendOr();
				spec.appendWhere(
						new SearchCondition(WTChangeRequest2.class, "state.state", SearchCondition.EQUAL, "RETURN"));
				spec.appendCloseParen();
			} else {
				spec.appendWhere(
						new SearchCondition(WTChangeRequest2.class, "state.state", SearchCondition.EQUAL, state));
			}
		}

		QueryResult resultRequest = PersistenceHelper.manager.find(spec);

		startAnd = false;
		spec = new QuerySpec(WTChangeOrder2.class);

		if (name != null) {
			startAnd = true;
			if (name.contains("*")) {
				spec.appendWhere(new SearchCondition(WTChangeOrder2.class, "master>name", SearchCondition.LIKE,
						name.replace("*", "%")));
			} else {
				spec.appendWhere(new SearchCondition(WTChangeOrder2.class, "master>name", SearchCondition.EQUAL, name));
			}
		}

		if (startdate != null || enddate != null) {
			if (spec.getConditionCount() > 0)
				spec.appendAnd();

			if (startdate != null && enddate == null) {
				spec.appendWhere(new SearchCondition(WTChangeOrder2.class, "thePersistInfo.createStamp",
						SearchCondition.GREATER_THAN_OR_EQUAL, Timestamp.valueOf(startdate + " 00:00:00")));
			} else if (startdate == null && enddate != null) {
				spec.appendWhere(new SearchCondition(WTChangeOrder2.class, "thePersistInfo.createStamp",
						SearchCondition.LESS_THAN_OR_EQUAL, Timestamp.valueOf(enddate + " 23:59:59")));
			} else {
				spec.appendOpenParen();
				spec.appendWhere(new SearchCondition(WTChangeOrder2.class, "thePersistInfo.createStamp",
						SearchCondition.GREATER_THAN_OR_EQUAL, Timestamp.valueOf(startdate + " 00:00:00")));
				spec.appendAnd();
				spec.appendWhere(new SearchCondition(WTChangeOrder2.class, "thePersistInfo.createStamp",
						SearchCondition.LESS_THAN_OR_EQUAL, Timestamp.valueOf(enddate + " 23:59:59")));
				spec.appendCloseParen();
			}
		}

		if (tempUserList.size() > 0) {
			if (spec.getConditionCount() > 0)
				spec.appendAnd();

			if (tempUserList.size() > 1)
				spec.appendOpenParen();

			for (int i = 0; i < tempUserList.size(); i++) {
				WTPrincipal principal = (WTPrincipal) tempUserList.get(i);
				ClassAttribute classAttribute = new ClassAttribute(WTChangeOrder2.class, "creator.key.id");
				ColumnExpression expression = ConstantExpression.newExpression(
						new Long(PersistenceHelper.getObjectIdentifier(principal).getId()),
						classAttribute.getColumnDescriptor().getJavaType());
				spec.appendWhere(new SearchCondition(classAttribute, SearchCondition.EQUAL, expression));
				if (tempUserList.size() > 1 && (tempUserList.size() - 1) != i)
					spec.appendOr();
			}

			if (tempUserList.size() > 1)
				spec.appendCloseParen();
		} else if (creator != null && tempUserList.size() == 0) {
			if (spec.getConditionCount() > 0)
				spec.appendAnd();

			ClassAttribute classAttribute = new ClassAttribute(WTChangeOrder2.class, "creator.key.id");
			ColumnExpression expression = ConstantExpression.newExpression(new Long(0),
					classAttribute.getColumnDescriptor().getJavaType());
			spec.appendWhere(new SearchCondition(classAttribute, SearchCondition.EQUAL, expression));
		}

		if (state != null) {
			if (spec.getConditionCount() > 0)
				spec.appendAnd();

			if (first.equals("true")) {
				spec.appendOpenParen();
				spec.appendWhere(
						new SearchCondition(WTChangeOrder2.class, "state.state", SearchCondition.EQUAL, "INWORK"));
				spec.appendOr();
				spec.appendWhere(
						new SearchCondition(WTChangeOrder2.class, "state.state", SearchCondition.EQUAL, "RETURN"));
				spec.appendCloseParen();
			} else {
				spec.appendWhere(
						new SearchCondition(WTChangeOrder2.class, "state.state", SearchCondition.EQUAL, state));
			}
		}

		QueryResult resultOrder = PersistenceHelper.manager.find(spec);
		resultOrder.append(resultRequest.getObjectVectorIfc());

		return resultOrder;
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams arg0) throws WTException {
		// TODO Auto-generated method stub
		ComponentConfigFactory factory = getComponentConfigFactory();

		JcaTableConfig table = (JcaTableConfig) factory.newTableConfig();
		table.setLabel(WTMessage.getLocalizedMessage(RESOURCE, "MY_WORK", new Object[] {},
				WTContext.getContext().getLocale()));
		table.setDataSourceMode(DataSourceMode.SYNCHRONOUS);
		table.setSelectable(true);
		table.setAutoGenerateRowId(true);
		table.setShowCount(true);
		table.setActionModel("mywork_list");
		// set the actionModel that comes in the TableToolBar
		// table.setActionModel("search_sample01_action");

		// add columns
		table.addComponent(factory.newColumnConfig(ICON, true));

		// thumbnail
//        ColumnConfig thumb = factory.newColumnConfig("smallThumbnail", true);
//        table.addComponent(thumb);

		ColumnConfig numberCol = factory.newColumnConfig(NUMBER, true);
		numberCol.setDataUtilityId("changeNumberDataUtility");
		numberCol.setLabel(
				WTMessage.getLocalizedMessage(RESOURCE, "NUMBER", new Object[] {}, WTContext.getContext().getLocale()));
		table.addComponent(numberCol);
		// table.addComponent(factory.newColumnConfig(FORMAT_ICON, false));
		ColumnConfig nameCol = factory.newColumnConfig(NAME, true);
		nameCol.setLabel(
				WTMessage.getLocalizedMessage(RESOURCE, "TITLE", new Object[] {}, WTContext.getContext().getLocale()));
		table.addComponent(nameCol);

		// mark this column as hidden(try to make this column visible in the UI)

		table.addComponent(factory.newColumnConfig(ORG_ID, false));
		// table.addComponent(factory.newColumnConfig(INFO_ACTION, false));
		ColumnConfig nmActionsCol = factory.newColumnConfig(NM_ACTIONS, false);
		// specify the actionModel for the action column
		((JcaColumnConfig) nmActionsCol).setActionModel("mywork_list");
		table.addComponent(nmActionsCol);
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
		ColumnConfig creatorCol = factory.newColumnConfig(CREATED_BY, true);
		creatorCol.setDataUtilityId("userNameDataUtility");
		table.addComponent(creatorCol);

		ColumnConfig last_modified = factory.newColumnConfig(LAST_MODIFIED, true);
		last_modified.setDefaultSort(true);
		last_modified.setAscending(false);
		table.addComponent(last_modified);

		ColumnConfig stateColumn = factory.newColumnConfig(STATE, true);
		// stateColumn.setDataStoreOnly(true);
		table.addComponent(stateColumn);

		// table.addComponent(factory.newColumnConfig(CONTAINER_NAME, false));

		// nonSelectable column handling : "nonSelectableColumn" should be the ID as the
		// default DataUtility is mapped
		// to this
//        ColumnConfig nonSelectableColumn = factory.newColumnConfig(NON_SELECTABLE_COLUMN, false);
//        nonSelectableColumn.setDataStoreOnly(true);
//        // map it to endItem attribute
//        nonSelectableColumn.setNeed("endItem");
//        table.addComponent(nonSelectableColumn);
//        table.setNonSelectableColumn(nonSelectableColumn);

		return table;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
