package ext.narae.util;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;

import wt.introspection.ClassInfo;
import wt.introspection.WTIntrospector;
import wt.method.MethodContext;
import wt.method.RemoteMethodServer;
import wt.pds.DatabaseInfoUtilities;
import wt.pom.DBProperties;
import wt.pom.WTConnection;
import wt.util.WTException;
import wt.util.WTProperties;

public class SequenceDao implements wt.method.RemoteAccess, java.io.Serializable {

	static final boolean SERVER = wt.method.RemoteMethodServer.ServerFlag;
	public static final SequenceDao manager = new SequenceDao();

	static String dataStore = "Oracle"; // "SQLServer" ....
	static {
		try {
			dataStore = WTProperties.getLocalProperties().getProperty("wt.db.dataStore");
		} catch (Exception ex) {
			dataStore = "Oracle";
		}
	}

	protected SequenceDao() {
	}

	/**
	 * 현재 sequence중 가장 큰 sequence의 다음값을 반환한다.
	 * 
	 * @param seqName sequence name
	 * @param format  sequence format
	 * @param tabName DB table name
	 * @param colName DB column name
	 * @return
	 */
	public String getSeqNo(String seqName, String format, String tabName, String colName) throws Exception {
		if (!SERVER) {

			try {
				Class argTypes[] = new Class[] { String.class, String.class, String.class, String.class };
				Object args[] = new Object[] { seqName, format, tabName, colName };
				return (String) RemoteMethodServer.getDefault().invoke("getSeqNo", null, this, argTypes, args);
			} catch (RemoteException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new WTException(e);
			}
		}

		MethodContext methodcontext = null;
		WTConnection wtconnection = null;

		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			Connection con = wtconnection.getConnection();

			/*
			 * ORACLE : select to_char( TO_NUMBER(SUBSTR( NVL(
			 * MAX(WTDOCUMENTNUMBER),'DOC-0'),5))+1,'00000000') FROM WTDOCUMENTMASTER WHERE
			 * WTDOCUMENTNUMBER LIKE 'DOC-%'; MSSQL : SELECT convert( bigint, SUBSTRING(
			 * ISNULL( MAX(WTDocumentNumber),'DOC-0' ), 5, len( ISNULL(
			 * MAX(WTDocumentNumber),'DOC-0' ) ) ) )+1 FROM wtadmin.WTDocumentMaster WHERE
			 * WTDocumentNumber LIKE 'DOC-%'
			 * 
			 * SELECT WTPartNumber FROM wcadmin.WTPartMaster WHERE WTPartNumber LIKE
			 * 'NP-05-0302-%' order by createStampA2 desc SELECT (convert( bigint,
			 * SUBSTRING( ISNULL( MAX(WTPartNumber),'NP-05-0302-'),11, len( ISNULL(
			 * MAX(WTPartNumber),'00000') ) ) )*-1)+1 FROM wcadmin.WTPartMaster WHERE
			 * WTPartNumber LIKE 'NP-05-0302%' and len( WTPartNumber)=16 AND
			 * RIGHT(WTPartNumber, 4) <> '.PRT' ; ddd
			 */

			StringBuffer sb = null;

			if ("Oracle".equals(dataStore)) {

				sb = new StringBuffer().append("select to_char(  TO_NUMBER(SUBSTR(   NVL(   MAX(").append(colName)
						.append("),?),?))+1,?) FROM ").append(tabName).append(" WHERE ").append(colName)
						.append(" LIKE ?");

			} else {
				if (!colName.contains("WTPart")) {
					sb = new StringBuffer().append("SELECT convert( bigint, SUBSTRING( ISNULL( MAX(").append(colName)
							.append("),?),?, len( ISNULL( MAX(").append(colName).append("),?) ) )*-1) FROM ")
							.append(tabName).append(" WHERE ").append(colName);

					if (tabName.equals("EPMDocumentMaster")) {
						sb.append(" LIKE ? and len( documentNumber)=16 ");
						sb.append(" AND docType <>'CADDRAWING'");
					} else {
						sb.append(" LIKE ?");
					}
				} else {
					sb = new StringBuffer().append("SELECT (convert( bigint, SUBSTRING( ISNULL( MAX(").append(colName)
							.append("),?),?, len( ISNULL( MAX(").append(colName).append("),?) ) ) )*-1) FROM ")
							.append(tabName).append(" WHERE ").append(colName)
							.append(" LIKE ? and len( WTPartNumber)=16 ")
							.append("AND RIGHT(WTPartNumber, 4) <> '.PRT'");
				}
			}

			System.out.println("query = " + sb.toString());
			st = con.prepareStatement(sb.toString());

			st.setString(1, seqName);
			System.out.println("query 1 in = seqName =" + seqName);
			st.setInt(2, seqName.length() + 1);
			System.out.println("query 2 in = seqName.length()+1 =" + (seqName.length() + 1));
			if ("Oracle".equals(dataStore)) {
				st.setString(3, format);
				st.setString(4, seqName + "%");
			} else {
				System.out.println("query 3 in = seqName =" + seqName);
				st.setString(3, seqName);
				System.out.println("query 4 in = seqName% =" + seqName + "%");
				st.setString(4, seqName + "%");
			}

			rs = st.executeQuery();

			String seqNum = null;
			while (rs.next()) {
				seqNum = rs.getString(1);
				int sss = 0;
				System.out.println("seqNum=" + seqNum);
				if (seqNum == null) {
					seqNum = "000";
				}
				sss = Integer.parseInt(seqNum.replaceAll(" ", ""));
				if (sss < 0)
					sss = sss * -1;
				sss = sss + 1;
				System.out.println("query return seqNum = " + seqNum);
//				seqNum = String.valueOf(sss);
				DecimalFormat decimalformat = new DecimalFormat(format);
				seqNum = decimalformat.format(sss);
				System.out.println("query return change seqNum = " + seqNum);
			}
			if (seqNum == null) {
				DecimalFormat decimalformat = new DecimalFormat(format);
				seqNum = decimalformat.format(Long.parseLong(format) + 1);
			} else if (!"Oracle".equals(dataStore)) {
				DecimalFormat decimalformat = new DecimalFormat(format);
				seqNum = decimalformat.format(Long.parseLong(seqNum));
			}

			seqNum = seqNum.trim();

			return seqNum;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (st != null) {
				st.close();
			}
			if (DBProperties.FREE_CONNECTION_IMMEDIATE && !wtconnection.isTransactionActive()) {
				MethodContext.getContext().freeConnection();
			}
		}
	}

	public static void main(String[] args) throws Exception {

	}
}
