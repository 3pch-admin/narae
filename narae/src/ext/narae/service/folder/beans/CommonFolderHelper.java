package ext.narae.service.folder.beans;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import ext.narae.util.StringUtil;
import wt.folder.Folder;
import wt.method.MethodContext;
import wt.pom.DBProperties;
import wt.pom.WTConnection;
import wt.util.WTException;
import wt.util.WTProperties;

public class CommonFolderHelper implements wt.method.RemoteAccess, java.io.Serializable
{

    static final boolean SERVER = wt.method.RemoteMethodServer.ServerFlag;

    static String dataStore = "Oracle"; //SQLServer ....
    static {
        try {
            dataStore = WTProperties.getLocalProperties().getProperty("wt.db.dataStore");
        }
        catch ( Exception ex ) {
            dataStore = "Oracle";
        }
    }

    public static CommonFolderHelper manager = new CommonFolderHelper();

    public ArrayList getFolderSortTree(final Folder obj) throws Exception
    {

        ArrayList list = CommonFolderHelper.manager.getFolderTree(obj);
        HashMap temp = new HashMap();

        TempNode root = new TempNode(null, null);
        temp.put("0", root);

        for ( int i = 0; i < list.size(); i++ ) {
            String[] s = (String[]) list.get(i);
            String level = s[0];
            int depth = Integer.parseInt(level);
            TempNode parent = (TempNode) temp.get(Integer.toString(depth - 1));
            TempNode newNode = new TempNode(s, parent);
            temp.put(level, newNode);
        }

        ArrayList result = new ArrayList();
        root.getList(result);
        return result;
    }

    class TempNode
    {
        public String[] obj;
        public TempNode parent;
        public ArrayList children = new ArrayList();

        public TempNode(final String[] obj, final TempNode parent) throws Exception {
            this.obj = obj;
            this.parent = parent;
            if ( parent != null ) {
                boolean flag = true;
                for ( int i = 0; i < parent.children.size(); i++ ) {
                    TempNode node = (TempNode) parent.children.get(i);
                    String name = node.obj[1];
                    if ( name.compareTo(obj[1]) > 0 ) {
                        flag = false;
                        parent.children.add(i, this);
                        break;
                    }
                }
                if ( flag ) {
                    parent.children.add(this);
                }
            }
        }

        public void getList(final ArrayList result) throws Exception
        {
            for ( int i = 0; i < children.size(); i++ ) {
                TempNode node = (TempNode) children.get(i);
                result.add(node.obj);
                node.getList(result);
            }
        }
    };

    public static ArrayList getFolderTree(final Folder obj) throws WTException
    {

        if ( !SERVER ) {
            Class argTypes[] = new Class[] { Folder.class };
            Object args[] = new Object[] { obj };
            try {
                return (ArrayList) wt.method.RemoteMethodServer.getDefault()
                        .invoke("getFolderTree", "com.e3ps.common.folder.beans.CommonFolderHelper", null, argTypes, args);
            }
            catch ( RemoteException e ) {
                e.printStackTrace();
                throw new WTException(e);
            }
            catch ( InvocationTargetException e ) {
                e.printStackTrace();
                throw new WTException(e);
            }
        }

        MethodContext methodcontext = null;
        WTConnection wtconnection = null;

        PreparedStatement st = null;
        ResultSet rs = null;
        
        try {

            ArrayList list = new ArrayList();

            methodcontext = MethodContext.getContext();
            wtconnection = (WTConnection) methodcontext.getConnection();
            Connection con = wtconnection.getConnection();

            StringBuffer sql = null;

            if ( "Oracle".equals(dataStore) ) {

                /*
                 * 폴더트리 select t.l,s.name,s.CLASSNAMEA2A2||':'||s.ida2a2 from
                 * subfolder s,( select level l,ida3b5 id from subfolderlink
                 * start with ida3a5=7843 connect by prior ida3b5=ida3a5 ) t
                 * where t.id=s.ida2a2
                 */

                sql = new StringBuffer().append("SELECT T.L,S.NAME,S.CLASSNAMEA2A2||':'||S.IDA2A2 FROM SUBFOLDER S,( ")
                        .append("SELECT LEVEL L,IDA3B5 ID FROM SUBFOLDERLINK ").append("START WITH IDA3A5=?  ")
                        .append("connect by prior ida3b5=ida3a5 ").append(") t where  t.id=s.ida2a2");

            } else {

                /*
                 * 폴더트리-mssql with cte (idA3B5, level) as ( select idA3B5, 1 as
                 * level from SubFolderLink where idA3A5=7843 union all select
                 * a.idA3B5, level+1 from SubFolderLink a, cte b where
                 * a.idA3A5=b.idA3B5 ) select c.level, s.name,
                 * s.classnameA2A2+':'+str(s.idA2A2) from SubFolder s, cte c
                 * where c.idA3B5=s.idA2A2
                 */

                sql = new StringBuffer()
                        .append("with cte (idA3B5, level) as ( ")
                        .append("select idA3B5, 1 as level ")
                        .append("from SubFolderLink ")
                        .append("where idA3A5=? ")
                        .append("union all ")
                        .append("select a.idA3B5, level+1 ")
                        .append("from SubFolderLink a, cte b ")
                        .append("where a.idA3A5=b.idA3B5 ) ")
                        .append("select c.level, s.name, s.classnameA2A2+':'+convert(varchar, s.idA2A2) from SubFolder s, cte c ")
                        .append("where c.idA3B5=s.idA2A2 order by s.name asc");

            }

            st = con.prepareStatement(sql.toString());
            st.setLong(1, obj.getPersistInfo().getObjectIdentifier().getId());

            rs = st.executeQuery();

            while ( rs.next() ) {
                String level = rs.getString(1);
                String name = rs.getString(2);
                String oid = rs.getString(3);
                oid = oid.trim();

                list.add(new String[] { level, name, oid });

            }

            return list;
        }
        catch ( Exception e ) {
            e.printStackTrace();
            throw new WTException(e);
        }
        finally {
            try {
                if ( rs != null ) {
                    rs.close();
                }
                if ( st != null ) {
                    st.close();
                }
            } catch(Exception e) {
                throw new WTException(e);
            }
            if ( DBProperties.FREE_CONNECTION_IMMEDIATE && !wtconnection.isTransactionActive() ) {
                MethodContext.getContext().freeConnection();
            }
        }

    }

    /****
     * 화면단의 DTree가 정상적으로 그려지지 않아서 추가함. (ORACLE만 대응)
     * @param obj
     * @return
     * @throws WTException
     */
    public static ArrayList getFolderDTree(final Folder obj) throws WTException
    {

        if ( !SERVER ) {
            Class argTypes[] = new Class[] { Folder.class };
            Object args[] = new Object[] { obj };
            try {
                return (ArrayList) wt.method.RemoteMethodServer.getDefault()
                        .invoke("getFolderDTree", "com.e3ps.common.folder.beans.CommonFolderHelper", null, argTypes, args);
            }
            catch ( RemoteException e ) {
                e.printStackTrace();
                throw new WTException(e);
            }
            catch ( InvocationTargetException e ) {
                e.printStackTrace();
                throw new WTException(e);
            }
        }

        MethodContext methodcontext = null;
        WTConnection wtconnection = null;

        PreparedStatement st = null;
        ResultSet rs = null;
        
        try {

            ArrayList list = new ArrayList();

            methodcontext = MethodContext.getContext();
            wtconnection = (WTConnection) methodcontext.getConnection();
            Connection con = wtconnection.getConnection();

            StringBuffer sql = null;

            sql = new StringBuffer();
            sql.append(" SELECT T.RN ID, NVL(S.RN, 0) PID, T.OID, T.NAME, T.L  ");
            sql.append("   FROM (                                       ");
            sql.append("          SELECT ROWNUM RN,                     ");
            sql.append("                 LEVEL L,                       ");
            sql.append("                 TB.NAME NAME,                  ");
            sql.append("                 TB.CLASSNAMEA2A2||':'||TB.IDA2A2 OID, ");
            sql.append("                 TB.IDA2A2 ID,                  ");
            sql.append("                 TA.IDA3A5 PID                  ");
            sql.append("            FROM SUBFOLDERLINK TA, SUBFOLDER TB ");
            sql.append("           WHERE TA.IDA3B5 = TB.IDA2A2          ");
            sql.append("           START WITH TA.IDA3A5 = ?             ");
            sql.append("         CONNECT BY PRIOR TA.IDA3B5 = TA.IDA3A5 ");
            sql.append("           ORDER SIBLINGS BY TB.NAME ASC        ");
            sql.append("        ) T,                                    ");
            sql.append("        (                                       ");
            sql.append("          SELECT ROWNUM RN,                     ");
            sql.append("                 SB.IDA2A2 ID                   ");
            sql.append("            FROM SUBFOLDERLINK SA, SUBFOLDER SB ");
            sql.append("           WHERE SA.IDA3B5 = SB.IDA2A2          ");
            sql.append("           START WITH SA.IDA3A5 = ?             ");
            sql.append("         CONNECT BY PRIOR SA.IDA3B5=SA.IDA3A5   ");
            sql.append("           ORDER SIBLINGS BY SB.NAME ASC        ");
            sql.append("        ) S                                     ");
            sql.append("  WHERE T.PID = S.ID(+)                         ");
            sql.append("  ORDER BY T.RN                                 ");

            st = con.prepareStatement(sql.toString());
            st.setLong(1, obj.getPersistInfo().getObjectIdentifier().getId());
            st.setLong(2, obj.getPersistInfo().getObjectIdentifier().getId());

            rs = st.executeQuery();

            while ( rs.next() ) {
                list.add(new String[] { rs.getString(1), rs.getString(2),   // DTree용 ID, DTree용 PID
                                        StringUtil.checkNull(rs.getString(3)).trim(),   //OID
                                        rs.getString(4), rs.getString(5) }); // 폴더명, Level

            }

            return list;
        }
        catch ( Exception e ) {
            e.printStackTrace();
            throw new WTException(e);
        }
        finally {
            try {
                if ( rs != null ) {
                    rs.close();
                }
                if ( st != null ) {
                    st.close();
                }
            } catch(Exception e) {
                throw new WTException(e);
            }
            if ( DBProperties.FREE_CONNECTION_IMMEDIATE && !wtconnection.isTransactionActive() ) {
                MethodContext.getContext().freeConnection();
            }
        }

    }
};
