package ext.narae.mvc.approval;

import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.CREATED;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.CREATED_BY;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.ICON;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.NAME;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.NUMBER;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.ORG_ID;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.STATE;

import com.ptc.jca.mvc.components.JcaTableConfig;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.ds.DataSourceMode;

import ext.narae.service.workflow.beans.WorkflowHelper2;
import wt.org.WTPrincipal;
import wt.session.SessionHelper;
import wt.util.WTContext;
import wt.util.WTException;
import wt.util.WTMessage;

@ComponentBuilder(value = "ext.narae.approval.workitem")
public class WorkitemTableBuilder extends AbstractComponentBuilder {
	private static String RESOURCE = "ext.narae.ui.common.resource.NaraeCommonRB";

	@Override
	public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
		// TODO Auto-generated method stub
		WTPrincipal principal = SessionHelper.manager.getPrincipal();

		return WorkflowHelper2.getWorkItemForChange(principal);
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams arg0) throws WTException {
		// TODO Auto-generated method stub
		ComponentConfigFactory factory = getComponentConfigFactory();

		JcaTableConfig table = (JcaTableConfig) factory.newTableConfig();
		table.setLabel(WTMessage.getLocalizedMessage(RESOURCE, "APPROVAL_APPROVAL", new Object[] {},
				WTContext.getContext().getLocale()));
		table.setDataSourceMode(DataSourceMode.SYNCHRONOUS);
		table.setSelectable(true);
		table.setAutoGenerateRowId(true);
		table.setShowCount(true);
		// set the actionModel that comes in the TableToolBar
		// table.setActionModel("search_sample01_action");

		ColumnConfig taskColumn = factory.newColumnConfig("task", true);
		taskColumn.setLabel(WTMessage.getLocalizedMessage(RESOURCE, "WORKTASK", new Object[] {},
				WTContext.getContext().getLocale()));
		taskColumn.setDataUtilityId("changeWorkflowTaskDataUtility");
		// stateColumn.setDataStoreOnly(true);
		table.addComponent(taskColumn);

		// add columns
		table.addComponent(factory.newColumnConfig(ICON, true));

		// thumbnail
//        ColumnConfig thumb = factory.newColumnConfig("smallThumbnail", true);
//        table.addComponent(thumb);

		ColumnConfig stateColumn = factory.newColumnConfig(STATE, true);
		// stateColumn.setDataStoreOnly(true);
		table.addComponent(stateColumn);

		ColumnConfig numberCol = factory.newColumnConfig(NUMBER, true);
		numberCol.setDataUtilityId("changeNumberOnWorkItemDataUtility");
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

		ColumnConfig last_modified = factory.newColumnConfig(CREATED, true);
		last_modified.setDefaultSort(true);
		last_modified.setAscending(false);
		table.addComponent(last_modified);

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
