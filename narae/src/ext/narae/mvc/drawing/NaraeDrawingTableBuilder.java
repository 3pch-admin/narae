package ext.narae.mvc.drawing;

import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.CHANGE_STATUS_FAMILY;
import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.CREATED;
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

import ext.narae.service.CommonUtil2;
import ext.narae.service.folder.beans.CommonFolderHelper;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.IteratedFolderMemberLink;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.StringValue;
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

@ComponentBuilder("ext.narae.drawing.DrawingList")
public class NaraeDrawingTableBuilder extends AbstractComponentBuilder {
//	private static Logger log = Logger.getLogger(NaraeDrawingTableBuilder.class.getName());
	private static final Logger log = LogR.getLoggerInternal(NaraeDrawingTableBuilder.class.getName());
	private static String RESOURCE = "ext.narae.ui.common.resource.NaraeCommonRB";

	@Override
	public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
		// TODO Auto-generated method stub
		String partFolder = params.getParameter("selectedFolderFromFolderContext") != null
				? (String) params.getParameter("selectedFolderFromFolderContext")
				: "";
		String state = params.getParameter("state") != null ? (String) params.getParameter("state") : "";
		String name = params.getParameter("name") != null ? (String) params.getParameter("name") : "";
		String number = params.getParameter("number") != null ? (String) params.getParameter("number") : "";
		String islastversion = params.getParameter("islastversion") != null
				? (String) params.getParameter("islastversion")
				: "";
		String drawingType = params.getParameter("drawingType") != null ? (String) params.getParameter("drawingType")
				: "";
		String material = params.getParameter("material") != null ? (String) params.getParameter("material") : "";
		String treatment = params.getParameter("treatment") != null ? (String) params.getParameter("treatment") : "";

		String detailFlag = params.getParameter("detailFlag") != null ? (String) params.getParameter("detailFlag") : "";

		String description = params.getParameter("description") != null ? (String) params.getParameter("description")
				: "";
		String predate = params.getParameter("predate") != null ? (String) params.getParameter("predate") : "";
		String postdate = params.getParameter("postdate") != null ? (String) params.getParameter("postdate") : "";
		String authoringType = params.getParameter("authoringType") != null
				? (String) params.getParameter("authoringType")
				: "";
		log.debug("=====> parameter first:|" + params.getParameter("first") + "|");
		String creator = params.getParameter("creator") != null ? (String) params.getParameter("creator") : "true";

		String first = params.getParameter("first") != null ? (String) params.getParameter("first") : "";
		String docType = params.getParameter("docType") != null ? (String) params.getParameter("docType") : "";

		log.debug("=====> first: " + first);
		if (first.trim().equals("true"))
			return new QueryResult();

		log.debug("detailFlag=" + detailFlag);

		QuerySpec query = null;
		QueryResult qr = null;
		boolean ibaStringAppended = false;
		int idx3 = 0;

		query = new QuerySpec();
		int idx = query.addClassList(EPMDocument.class, true);
		int idx2 = query.addClassList(EPMDocumentMaster.class, false);

		// Join
		query.appendWhere(new SearchCondition(EPMDocumentMaster.class, "thePersistInfo.theObjectIdentifier.id",
				EPMDocument.class, "masterReference.key.id"), new int[] { idx2, idx });

		// Default c/i c/o
		query.appendAnd();
		query.appendOpenParen();
		query.appendWhere(new SearchCondition(EPMDocument.class, "checkoutInfo.state", SearchCondition.EQUAL, "c/i"),
				new int[] { idx });
		query.appendOr();
		query.appendWhere(new SearchCondition(EPMDocument.class, "checkoutInfo.state", SearchCondition.EQUAL, "c/o"),
				new int[] { idx });
		query.appendCloseParen();

		// Part Folder
		// folder search
		if (partFolder != null && partFolder.trim().length() > 0) {
			Folder folder = (Folder) CommonUtil2.getInstance(partFolder);

			if (query.getConditionCount() > 0)
				query.appendAnd();

			int folder_idx = query.addClassList(IteratedFolderMemberLink.class, false);
			SearchCondition sc1 = new SearchCondition(
					new ClassAttribute(IteratedFolderMemberLink.class, "roleBObjectRef.key.branchId"), "=",
					new ClassAttribute(EPMDocument.class, "iterationInfo.branchId"));
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

//			for(int fi = 0; fi < folders.size(); fi++) {
//				String[] s = (String[])folders.get(fi);
//				Folder sf = (Folder)CommonUtil2.getInstance(s[2]);
//				log.debug(sf.getFolderPath());
//		//			if(fi > 0) {
//					query.appendOr();
//		//			}
//				query.appendWhere(new SearchCondition(IteratedFolderMemberLink.class, 
//											"roleAObjectRef.key.id",
//											SearchCondition.EQUAL,
//											sf.getPersistInfo().getObjectIdentifier().getId()), new int[]{folder_idx});
//			}
//			query.appendCloseParen();                		
//		}

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

		// docType
		if (docType != null && docType.trim().length() > 0) {
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			query.appendWhere(
					new SearchCondition(EPMDocument.class, EPMDocument.DOC_TYPE, SearchCondition.EQUAL, docType),
					new int[] { idx });
		}

		// State
		if (state != null && state.trim().length() > 0) {
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			query.appendWhere(new SearchCondition(EPMDocument.class, "state.state", SearchCondition.EQUAL, state),
					new int[] { idx });
		}

		// Number
		if (number != null && number.trim().length() > 0) {
			if (query.getConditionCount() > 0)
				query.appendAnd();
			StringSearch stringsearch = new StringSearch("number");
			stringsearch.setValue("%" + number.trim() + "%");
			query.appendWhere(stringsearch.getSearchCondition(EPMDocument.class), new int[] { idx });
		} else
			number = "";

		// Name
		if (name != null && name.trim().length() > 0) {
			if (query.getConditionCount() > 0)
				query.appendAnd();
			StringSearch stringsearch = new StringSearch("name");
			stringsearch.setValue("%" + name.trim() + "%");
			query.appendWhere(stringsearch.getSearchCondition(EPMDocument.class), new int[] { idx });
		} else
			name = "";

//		if(creator!=null && creator.length()>0)
//		{
//			People people = (People)rf.getReference(creator).getObject();
//			WTUser user = people.getUser();
//			userName = user.getFullName();
//
//			if(query.getConditionCount()>0)query.appendAnd();
//			query.appendWhere(new SearchCondition(EPMDocument.class,"iterationInfo.creator.key","=", PersistenceHelper.getObjectIdentifier( user )), new int[]{idx});
//		}

		System.out.println("======1 ==== " + query);
		// 재질

		if (material != null && material.trim().length() > 0) {

			AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath("Material");

			if (aview != null) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				if (ibaStringAppended == false) {
					idx3 = query.addClassList(StringValue.class, false);
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id",
							EPMDocument.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { idx3, idx });
					ibaStringAppended = true;
					query.appendAnd();
				}

				query.appendOpenParen();
				query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID",
						SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { idx3 });
				query.appendAnd();
				query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE,
						("%" + material.trim() + "%").toUpperCase()), new int[] { idx3 });
				query.appendCloseParen();
			}
		} else
			material = "";

		// treatment
		if (treatment != null && treatment.trim().length() > 0) {

			AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath("Treatment");

			if (aview != null) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				if (ibaStringAppended == false) {
					idx3 = query.addClassList(StringValue.class, false);
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id",
							EPMDocument.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { idx3, idx });
					ibaStringAppended = true;

					query.appendAnd();
				}

				query.appendOpenParen();
				query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID",
						SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { idx3 });
				query.appendAnd();
				query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE,
						("%" + treatment.trim() + "%").toUpperCase()), new int[] { idx3 });
				query.appendCloseParen();
			}
		} else
			treatment = "";

		// 도면타입
		if (drawingType != null && drawingType.trim().length() > 0) {

			AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath("DRW_type");

			if (aview != null) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				if (ibaStringAppended == false) {
					idx3 = query.addClassList(StringValue.class, false);
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id",
							EPMDocument.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { idx3, idx });
					ibaStringAppended = true;
					query.appendAnd();
				}

				query.appendOpenParen();
				query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID",
						SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { idx3 });
				query.appendAnd();
				query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE,
						("%" + drawingType.trim() + "%").toUpperCase()), new int[] { idx3 });
				query.appendCloseParen();
			}
		} else
			drawingType = "";

		if (detailFlag.equals("true")) {
			// Description
			if (description != null && description.trim().length() > 0) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				query.appendWhere(new SearchCondition(EPMDocument.class, "description", SearchCondition.LIKE,
						"%" + description.trim() + "%"), new int[] { idx });
			} else
				description = "";

			// Date
			if ((predate != null && predate.trim().length() > 0)
					|| (postdate != null && postdate.trim().length() > 0)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				if ((predate != null && predate.trim().length() > 0)
						&& (postdate == null || postdate.trim().length() == 0)) {
					query.appendWhere(
							new SearchCondition(EPMDocument.class, "thePersistInfo.createStamp",
									SearchCondition.GREATER_THAN_OR_EQUAL, Timestamp.valueOf(predate + " 00:00:00")),
							new int[] { idx });
				} else if ((predate == null || predate.trim().length() == 0)
						&& (postdate != null && postdate.trim().length() > 0)) {
					query.appendWhere(
							new SearchCondition(EPMDocument.class, "thePersistInfo.createStamp",
									SearchCondition.LESS_THAN_OR_EQUAL, Timestamp.valueOf(postdate + " 23:59:59")),
							new int[] { idx });
				} else {
					query.appendOpenParen();
					query.appendWhere(
							new SearchCondition(EPMDocument.class, "thePersistInfo.createStamp",
									SearchCondition.GREATER_THAN_OR_EQUAL, Timestamp.valueOf(predate + " 00:00:00")),
							new int[] { idx });
					query.appendAnd();
					query.appendWhere(
							new SearchCondition(EPMDocument.class, "thePersistInfo.createStamp",
									SearchCondition.LESS_THAN_OR_EQUAL, Timestamp.valueOf(postdate + " 23:59:59")),
							new int[] { idx });
					query.appendCloseParen();
				}
			}

			// authoringType
			if (authoringType != null && authoringType.trim().length() > 0) {

				if (query.getConditionCount() > 0)
					query.appendAnd();
				StringSearch stringsearch = new StringSearch("authoringApplication");
				stringsearch.setValue(authoringType);
				query.appendWhere(stringsearch.getSearchCondition(EPMDocumentMaster.class), new int[] { idx2 });
			}

			// creator
			if (creator != null && creator.length() > 0) {
//				People people = (People)CommonUtil2.getInstance(creator);
//				WTUser user = people.getUser();
				WTUser user = (WTUser) CommonUtil2.getInstance(creator);

				if (query.getConditionCount() > 0)
					query.appendAnd();
				query.appendWhere(new SearchCondition(EPMDocument.class, "iterationInfo.creator.key", "=",
						PersistenceHelper.getObjectIdentifier(user)), new int[] { idx });
			}
		}

		System.out.println(query);

		if (query.getConditionCount() > 0) {
			// 최신 이터레이션
			query.appendAnd();
			query.appendWhere(VersionControlHelper.getSearchCondition(EPMDocument.class, true));

			QueryResult result = null;
			if (islastversion != null) {
				log.debug(query);
				if ("true".equals(islastversion)) {
					log.debug("==================> Find Latest Version");
					LatestConfigSpec latestCSpec = new LatestConfigSpec();
					result = ConfigHelper.service.queryIterations(query, latestCSpec);
				} else {
					log.debug("==================> Find ALL Version");
					LatestConfigSpec latestCSpec = new LatestConfigSpec();
					result = ConfigHelper.service.queryIterations(query, null);
					// result = PersistenceHelper.manager.find((StatementSpec)query);
				}
			}
			log.debug("=======> Query size:" + result.size());
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
		// table.setLabel(WTMessage.getLocalizedMessage(RESOURCE , "SEARCH_DRAWING", new
		// Object[]{}, WTContext.getContext().getLocale()));
		table.setDataSourceMode(DataSourceMode.SYNCHRONOUS);
		table.setSelectable(true);
		table.setAutoGenerateRowId(true);
		table.setShowCount(true);
		table.setView("/narae/searchDrawingView.jsp");
		// set the actionModel that comes in the TableToolBar
		// table.setActionModel("drawing_search_action");

		// add columns
		table.addComponent(factory.newColumnConfig(ICON, true));

		// thumbnail
		ColumnConfig thumb = factory.newColumnConfig("smallThumbnail", true);
		table.addComponent(thumb);

		ColumnConfig numberCol = factory.newColumnConfig(NUMBER, true);
		numberCol.setLabel(WTMessage.getLocalizedMessage(RESOURCE, "DRAWING_NUMBER", new Object[] {},
				WTContext.getContext().getLocale()));
		table.addComponent(numberCol);
		// table.addComponent(factory.newColumnConfig(FORMAT_ICON, false));
		ColumnConfig nameCol = factory.newColumnConfig(NAME, true);
		nameCol.setLabel(WTMessage.getLocalizedMessage(RESOURCE, "DRAWING_NAME", new Object[] {},
				WTContext.getContext().getLocale()));
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

		return table;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
