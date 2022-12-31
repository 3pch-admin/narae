package ext.narae.mvc.document;

import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.CHANGE_STATUS_FAMILY;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.CREATED_BY;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.GENERAL_STATUS_FAMILY;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.ICON;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.INFO_ACTION;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.LAST_MODIFIED;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.NAME;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.NM_ACTIONS;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.NUMBER;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.ORG_ID;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.SHARE_STATUS_FAMILY;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.STATE;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.VERSION;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.logging.log4j.Logger;

import com.ptc.jca.mvc.components.JcaTableConfig;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.ds.DataSourceMode;

import ext.narae.mvc.drawing.NaraeDrawingTableBuilder;
import ext.narae.service.CommonUtil2;
import ext.narae.service.folder.beans.CommonFolderHelper;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.IteratedFolderMemberLink;
import wt.log4j.LogR;
import wt.org.WTUser;
import wt.query.ClassAttribute;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.StringSearch;
import wt.util.WTContext;
import wt.util.WTException;
import wt.util.WTMessage;
import wt.vc.VersionControlHelper;
import wt.vc.config.ConfigHelper;
import wt.vc.config.LatestConfigSpec;

@ComponentBuilder("ext.narae.document.DocumentList")
public class NaraeDocumentTableBuilder extends AbstractComponentBuilder {
	private static final Logger log = LogR.getLoggerInternal(NaraeDrawingTableBuilder.class.getName());
	private static String RESOURCE = "ext.narae.ui.common.resource.NaraeCommonRB";

	@Override
	public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
		// TODO Auto-generated method stub
		String first = params.getParameter("first") != null ? (String) params.getParameter("first") : "";
		// String partFolder =
		// params.getParameter("selectedFolderFromFolderContext")!=null?(String)
		// params.getParameter("selectedFolderFromFolderContext"):"";
		String partFolder = params.getParameter("selectedFolderFromFolderContext") != null
				? (String) params.getParameter("selectedFolderFromFolderContext")
				: "";
		String name = params.getParameter("name") != null ? (String) params.getParameter("name") : "";
		String number = params.getParameter("number") != null ? (String) params.getParameter("number") : "";
		String version = params.getParameter("islastversion") != null ? (String) params.getParameter("islastversion")
				: "";
		String startdate = params.getParameter("predate") != null ? (String) params.getParameter("predate") : "";
		String enddate = params.getParameter("postdate") != null ? (String) params.getParameter("postdate") : "";
		String description = params.getParameter("description") != null ? (String) params.getParameter("description")
				: "";
		String creator = params.getParameter("creator") != null ? (String) params.getParameter("creator") : "";

		QuerySpec query = null;
		QueryResult qr = null;

		query = new QuerySpec();
		int idx = query.addClassList(WTDocument.class, true);

		if (partFolder != null && partFolder.trim().length() > 0) {
			Folder folder = (Folder) CommonUtil2.getInstance(partFolder);

			if (query.getConditionCount() > 0)
				query.appendAnd();

			int folder_idx = query.addClassList(IteratedFolderMemberLink.class, false);
			SearchCondition sc1 = new SearchCondition(
					new ClassAttribute(IteratedFolderMemberLink.class, "roleBObjectRef.key.branchId"), "=",
					new ClassAttribute(WTDocument.class, "iterationInfo.branchId"));
			sc1.setFromIndicies(new int[] { folder_idx, idx }, 0);
			sc1.setOuterJoin(0);
			query.appendWhere(sc1, new int[] { folder_idx, idx });

			query.appendAnd();
			ArrayList folders = CommonFolderHelper.getFolderTree(folder);
			// folders.add(folder);

			query.appendOpenParen();

			query.appendWhere(new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id",
					SearchCondition.EQUAL, folder.getPersistInfo().getObjectIdentifier().getId()),
					new int[] { folder_idx });

			for (int fi = 0; fi < folders.size(); fi++) {
				String[] s = (String[]) folders.get(fi);
				Folder sf = (Folder) CommonUtil2.getInstance(s[2]);
				log.debug(sf.getFolderPath());
				// if(fi > 0) {
				query.appendOr();
				// }
				query.appendWhere(
						new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id",
								SearchCondition.EQUAL, sf.getPersistInfo().getObjectIdentifier().getId()),
						new int[] { folder_idx });
			}
			query.appendCloseParen();
		}

		if (number != null && number.trim().length() > 0) {
			if (query.getConditionCount() > 0)
				query.appendAnd();
			StringSearch stringsearch = new StringSearch("number");
			stringsearch.setValue("%" + number.trim() + "%");
			query.appendWhere(stringsearch.getSearchCondition(WTDocument.class));
		} else
			number = "";

		if (name != null && name.trim().length() > 0) {
			if (query.getConditionCount() > 0)
				query.appendAnd();
			StringSearch stringsearch = new StringSearch("name");
			stringsearch.setValue("%" + name.trim() + "%");
			query.appendWhere(stringsearch.getSearchCondition(WTDocument.class));
		} else
			name = "";

		if (description != null && description.trim().length() > 0) {
			if (query.getConditionCount() > 0)
				query.appendAnd();
			StringSearch stringsearch = new StringSearch("description");
			stringsearch.setValue("%" + description.trim() + "%");
			query.appendWhere(stringsearch.getSearchCondition(WTDocument.class));
		} else
			description = "";

		if (creator != null && creator.length() > 0) {
//			People people = (People)CommonUtil2.getInstance(creator);
//			WTUser user = people.getUser();
			WTUser user = (WTUser) CommonUtil2.getInstance(creator);

			if (query.getConditionCount() > 0)
				query.appendAnd();
			query.appendWhere(new SearchCondition(WTDocument.class, "iterationInfo.creator.key", "=",
					PersistenceHelper.getObjectIdentifier(user)), new int[] { idx });
		} else
			creator = "";

		if ((startdate != null && startdate.length() > 0) || (enddate != null && enddate.length() > 0)) {
			log.debug("1");
			if (query.getConditionCount() > 0)
				query.appendAnd();

			if ((startdate != null && startdate.length() > 0) && (enddate == null || enddate.length() == 0)) {
				log.debug("2");
				query.appendWhere(new SearchCondition(WTDocument.class, "thePersistInfo.createStamp",
						SearchCondition.GREATER_THAN_OR_EQUAL, Timestamp.valueOf(startdate + " 00:00:00")));
			} else if ((startdate == null || startdate.length() == 0) && (enddate != null && enddate.length() > 0)) {
				log.debug("3");
				query.appendWhere(new SearchCondition(WTDocument.class, "thePersistInfo.createStamp",
						SearchCondition.LESS_THAN_OR_EQUAL, Timestamp.valueOf(enddate + " 23:59:59")));
			} else {
				log.debug("4");
				query.appendOpenParen();
				query.appendWhere(new SearchCondition(WTDocument.class, "thePersistInfo.createStamp",
						SearchCondition.GREATER_THAN_OR_EQUAL, Timestamp.valueOf(startdate + " 00:00:00")));
				query.appendAnd();
				query.appendWhere(new SearchCondition(WTDocument.class, "thePersistInfo.createStamp",
						SearchCondition.LESS_THAN_OR_EQUAL, Timestamp.valueOf(enddate + " 23:59:59")));
				query.appendCloseParen();
			}
		}

		if (query.getConditionCount() > 0) {
			if (query.getConditionCount() > 0)
				query.appendAnd();

			query.appendOpenParen();
			query.appendWhere(
					new SearchCondition(WTDocument.class, "checkoutInfo.state", SearchCondition.EQUAL, "c/i"));
			query.appendOr();
			query.appendWhere(
					new SearchCondition(WTDocument.class, "checkoutInfo.state", SearchCondition.EQUAL, "c/o"));
			query.appendCloseParen();

			// 최신 이터레이션
			query.appendAnd();
			query.appendWhere(VersionControlHelper.getSearchCondition(WTDocument.class, true));

			// Except SW Document
			query.appendAnd();
			query.appendWhere(
					new SearchCondition(WTDocument.class, "docType", SearchCondition.NOT_EQUAL, "$$SWDocument"));

			QueryResult result = null;
			if (version != null) {
				if ("true".equals(version)) {
					log.debug("==================> Find Latest Version");
					LatestConfigSpec latestCSpec = new LatestConfigSpec();
					result = ConfigHelper.service.queryIterations(query, latestCSpec);
				} else {
					log.debug("==================> Find ALL Version");
//					LatestConfigSpec latestCSpec = new LatestConfigSpec();
					result = ConfigHelper.service.queryIterations(query, null);
				}
			}

			System.out.println("qery=" + query);

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
		table.setLabel(WTMessage.getLocalizedMessage(RESOURCE, "DOC_MNG_SEARCH_DOC_TITLE", new Object[] {},
				WTContext.getContext().getLocale()));
		table.setDataSourceMode(DataSourceMode.ASYNCHRONOUS);
		table.setSelectable(true);
		table.setAutoGenerateRowId(true);
		table.setShowCount(true);
		table.setView("/narae/searchDocumentView.jsp");
		// set the actionModel that comes in the TableToolBar
		// table.setActionModel("drawing_search_action");

		// add columns
		table.addComponent(factory.newColumnConfig(ICON, true));

		// thumbnail
		ColumnConfig thumb = factory.newColumnConfig("smallThumbnail", true);
		table.addComponent(thumb);

		ColumnConfig numberCol = factory.newColumnConfig(NUMBER, true);
		numberCol.setLabel(WTMessage.getLocalizedMessage(RESOURCE, "DOC_NUMBER", new Object[] {},
				WTContext.getContext().getLocale()));
		numberCol.setWidth(100);
		table.addComponent(numberCol);
		// table.addComponent(factory.newColumnConfig(FORMAT_ICON, false));
		ColumnConfig nameCol = factory.newColumnConfig(NAME, true);
		nameCol.setLabel(WTMessage.getLocalizedMessage(RESOURCE, "DOC_NAME", new Object[] {},
				WTContext.getContext().getLocale()));
		nameCol.setWidth(200);
		table.addComponent(nameCol);

		// mark this column as hidden(try to make this column visible in the UI)

		table.addComponent(factory.newColumnConfig(SHARE_STATUS_FAMILY, false));
		table.addComponent(factory.newColumnConfig(GENERAL_STATUS_FAMILY, false));
		table.addComponent(factory.newColumnConfig(CHANGE_STATUS_FAMILY, false));

		table.addComponent(factory.newColumnConfig(ORG_ID, false));
		table.addComponent(factory.newColumnConfig(INFO_ACTION, false));
		ColumnConfig nmActionsCol = factory.newColumnConfig(NM_ACTIONS, false);
		// specify the actionModel for the action column
//        ((JcaColumnConfig) nmActionsCol)
//                .setActionModel("search_sample01_action");
//        table.addComponent(nmActionsCol);
//

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
		versionColumn.setWidth(30);
		table.addComponent(versionColumn);

		ColumnConfig stateColumn = factory.newColumnConfig(STATE, true);
		// stateColumn.setDataStoreOnly(true);
		stateColumn.setWidth(50);
		table.addComponent(stateColumn);

		ColumnConfig creatorCol = factory.newColumnConfig(CREATED_BY, true);
		creatorCol.setWidth(50);
		creatorCol.setDataUtilityId("userNameDataUtility");
		table.addComponent(creatorCol);

		ColumnConfig modifiedCol = factory.newColumnConfig(LAST_MODIFIED, true);
		modifiedCol.setWidth(50);
		table.addComponent(modifiedCol);

		return table;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
