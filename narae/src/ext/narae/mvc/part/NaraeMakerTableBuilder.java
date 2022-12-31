package ext.narae.mvc.part;

import com.ptc.jca.mvc.components.JcaTableConfig;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.ds.DataSourceMode;

import ext.narae.component.ErpMakerBean;
import ext.narae.util.db.DBConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import wt.fc.ObjectVector;
import wt.fc.QueryResult;
import wt.util.WTContext;
import wt.util.WTException;
import wt.util.WTMessage;

@ComponentBuilder({ "ext.narae.part.MakerList" })
public class NaraeMakerTableBuilder extends AbstractComponentBuilder {
	private static String RESOURCE = "ext.narae.ui.common.resource.NaraeCommonRB2";

	public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
		String mkrCode = (String) params.getParameter("mkrCode");
		String mkrName = (String) params.getParameter("mkrName");
		if (mkrCode != null && mkrCode.length() > 0) {
			mkrCode = mkrCode.trim();
		} else {
			mkrCode = null;
		}

		if (mkrName != null && mkrName.length() > 0) {
			mkrName = mkrName.trim();
		} else {
			mkrName = null;
		}

		String sql = "SELECT * FROM TCB09 WHERE 1=1";
		if (mkrCode != null) {
			sql = sql + " and maker like '%" + mkrCode + "%'";
		}

		if (mkrName != null) {
			sql = sql + " and mkrName like '%" + mkrName + "%'";
		}

		sql = sql + " ORDER BY maker";
		DBConnectionManager db = DBConnectionManager.getInstance();
		Connection con = db.getConnection("erp");
		PreparedStatement ps = con.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		ObjectVector ov = new ObjectVector();

		while (rs.next()) {
			ov.addElement(new ErpMakerBean(rs.getString("Maker"), rs.getString("MkrName")));
		}

		QueryResult qr = new QueryResult();
		qr.append(ov);
		ps.close();
		db.freeConnection("erp", con);
		return qr;
	}

	public ComponentConfig buildComponentConfig(ComponentParams arg0) throws WTException {
		ComponentConfigFactory factory = this.getComponentConfigFactory();
		JcaTableConfig table = (JcaTableConfig) factory.newTableConfig();
		table.setLabel(WTMessage.getLocalizedMessage(RESOURCE, "PART_MNG_SEARCH_MAKER_001", new Object[0],
				WTContext.getContext().getLocale()));
		table.setDataSourceMode(DataSourceMode.ASYNCHRONOUS);
		table.setSelectable(true);
		table.setAutoGenerateRowId(true);
		table.setShowCount(true);
		ColumnConfig numberCol = factory.newColumnConfig("makerCODE", true);
		numberCol.setLabel(
				WTMessage.getLocalizedMessage(RESOURCE, "CODE", new Object[0], WTContext.getContext().getLocale()));
		table.addComponent(numberCol);
		ColumnConfig nameCol = factory.newColumnConfig("makerName", true);
		nameCol.setLabel(WTMessage.getLocalizedMessage(RESOURCE, "MAKER_NAME", new Object[0],
				WTContext.getContext().getLocale()));
		nameCol.setWidth(60);
		table.addComponent(nameCol);
		return table;
	}

	public static void main(String[] args) {
	}
}