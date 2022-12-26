/**
 * @(#) ObjectUtil.java Copyright (c) e3ps. All rights reserverd
 * @version 1.00
 * @since jdk 1.4.02
 * @createdate 2005. 4. 14..
 * @author Cho Sung Ok, jerred@e3ps.com
 * @desc
 */

package ext.narae.util.obj;

import ext.narae.util.CommonUtil;
import wt.access.WTAclEntry;
import wt.enterprise.Master;
import wt.enterprise.RevisionControlled;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.series.MultilevelSeries;
import wt.series.Series;
import wt.series.SeriesIncrementInvalidException;
import wt.team.Team;
import wt.team.TeamException;
import wt.team.TeamHelper;
import wt.team.TeamManaged;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.Iterated;
import wt.vc.IterationIdentifier;
import wt.vc.Mastered;
import wt.vc.VersionControlException;
import wt.vc.VersionControlHelper;
import wt.vc.VersionIdentifier;
import wt.vc.Versioned;
import wt.vc.views.ViewManageable;
import wt.vc.wip.CheckoutLink;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;

// import e3ps.common.workflow.WorkflowUtil;
// import e3ps.common.workflow.beans.WFItemHelper;

public class ObjectUtil
{
    public static Workable checkin(Workable workable) throws Exception
    {
        return WorkInProgressHelper.service.checkin(workable, "");
    }

    public static Workable checkout(Workable workable) throws Exception
    {
        CheckoutLink checkOutLink = null;
        Workable workingCopy = null;

        if (!WorkInProgressHelper.isWorkingCopy(workable))
        {
            if (!WorkInProgressHelper.isCheckedOut(workable))
            {
                wt.folder.Folder folder = WorkInProgressHelper.service.getCheckoutFolder();
                checkOutLink = (CheckoutLink) WorkInProgressHelper.service.checkout(workable, folder, "");
                workingCopy = checkOutLink.getWorkingCopy();
            }
            else if (WorkInProgressHelper.isCheckedOut(workable))
            {
                workingCopy = WorkInProgressHelper.service.workingCopyOf(workable);
            }
        }
        else
        {
            workingCopy = workable;
        }

        return workingCopy;
    }

    public static Workable undoCheckout(Workable workable) throws Exception
    {
        workable = WorkInProgressHelper.service.undoCheckout(workable);
        return workable;
    }

    public static void deletePersistable(Persistable persist) throws Exception
    {
        // WorkflowUtil.deleteWFHistories ( persist );
        PersistenceHelper.manager.delete(persist);
    }

    public static RevisionControlled getLatestObject(Master master) throws WTException
    {
        return getLatestObject(master, null);
    }
    
    public static RevisionControlled getLatestObject(Master master, String _viewName) throws WTException
    {
        RevisionControlled rc = null;
        QueryResult qr = wt.vc.VersionControlHelper.service.allVersionsOf(master);

        while (qr.hasMoreElements())
        {
            RevisionControlled obj = ((RevisionControlled) qr.nextElement());
            
            if(_viewName != null)
            {
                if(!_viewName.equals(((ViewManageable)obj).getViewName()))
                    continue;
            }
            
            if (rc == null || obj.getVersionIdentifier().getSeries().greaterThan(rc.getVersionIdentifier().getSeries()))
            {
                rc = obj;
            }
        }
        if (rc != null)
            return (RevisionControlled) VersionControlHelper.getLatestIteration(rc, false);
        else
            return rc;
    }

    public static RevisionControlled getLatestVersion(RevisionControlled object) throws WTException
    {
        return getLatestObject((Master)object.getMaster(), null);
    }

    /**
     * 현재 객체의 다음 버전의 객체를 반환한다.
     * 
     * @param object
     * @return 없을시 null 리턴
     */
    public static RevisionControlled getNextVersion(RevisionControlled rc)
    {
        byte[] b = rc.getVersionIdentifier().getValue().getBytes();
        b[b.length-1] += 1;
        try
        {
            rc = getVersionObject((Master) rc.getMaster(), new String(b));
        }
        catch (WTException e)
        {
            e.printStackTrace();
            rc = null;
        }
        return rc;
    }
    
    /**
     * 현재 객체의 이전 버전의 객체를 반환한다.
     * 
     * @param object
     * @return 없을시 null 리턴
     */
    public static RevisionControlled getPreviousVersion(RevisionControlled rc)
    {
        byte[] b = rc.getVersionIdentifier().getValue().getBytes();
        b[b.length-1] -= 1;
        try
        {
            rc = getVersionObject((Master) rc.getMaster(), new String(b));
        }
        catch (WTException e)
        {
            e.printStackTrace();
            rc = null;
        }
        return rc;
    }

    public static boolean isLatestVersion(RevisionControlled obj) throws Exception
    {
        String viewName = null;
        if(obj instanceof ViewManageable)
            viewName = ((ViewManageable)obj).getViewName();
        
        RevisionControlled lastObject = (RevisionControlled) getLatestObject((Master) obj.getMaster(), viewName);
        if (lastObject.getVersionIdentifier().getSeries().greaterThan(obj.getVersionIdentifier().getSeries())) { return false; }

        return true;
    }

    /**
     * Get all Iterated Object's history
     * 
     * @param :
     *            iteration
     * @return : QueryResult
     * @author : PTC KOREA Yang Kyu
     * @since : 2004.01
     */
    public static QueryResult getAllIterations(Iterated iteration) throws Exception
    {
        QueryResult result = VersionControlHelper.service.allIterationsFrom(iteration);
        return result;
    }

    public static void delete(Persistable persistable)
    {
        if (persistable instanceof LifeCycleManaged)
        {
            // WorkflowUtil.deleteWfProcess((LifeCycleManaged)persistable);
        }

        try
        {
            // WFItemHelper.manager.deleteWFItem((WTObject)persistable);
            // WorkflowUtil.deleteWFHistories(persistable);
            deleteAclObject((WTObject) persistable);
            PersistenceHelper.manager.delete(persistable);
        }
        catch (WTException e)
        {
            e.printStackTrace();
        }
    }

    public static void deleteAclObject(WTObject obj)
    {
        try
        {
            QuerySpec query = new QuerySpec(WTAclEntry.class);
            if (obj instanceof TeamManaged)
            {
                Team team = TeamHelper.service.getTeam((TeamManaged) obj);
                query.appendWhere(new SearchCondition(WTAclEntry.class, "aclReference.key.id", "=", CommonUtil
                        .getOIDLongValue(team)), new int[] { 0 });
                query.appendOr();
            }
            query.appendWhere(new SearchCondition(WTAclEntry.class, "aclReference.key.id", "=", CommonUtil
                    .getOIDLongValue(obj)), new int[] { 0 });

            QueryResult qr = PersistenceHelper.manager.find(query);

            while (qr.hasMoreElements())
            {
                WTAclEntry element = (WTAclEntry) qr.nextElement();
                PersistenceHelper.manager.delete(element);
            }
            // PersistenceHelper.manager.delete(obj);
        }
        catch (QueryException e)
        {
            e.printStackTrace();
        }
        catch (TeamException e)
        {
            e.printStackTrace();
        }
        catch (WTException e)
        {
            e.printStackTrace();
        }
    }

    public static RevisionControlled getVersionObject(Master master, String version) throws WTException
    {
        RevisionControlled rc = null;
        QueryResult qr = VersionControlHelper.service.allVersionsOf(master);
        while (qr.hasMoreElements())
        {
            RevisionControlled obj = ((RevisionControlled) qr.nextElement());
            if (obj.getVersionIdentifier().getSeries().getValue().equals(version))
                rc = obj;
        }
        if (rc != null)
            return (RevisionControlled) VersionControlHelper.getLatestIteration(rc, false);

        return rc;
    }

    /**
     * 최신 버전 객체를 반환한다.
     * 
     * @param targetClass
     * @param master
     * @return
     */
    public static WTObject getNewestIteration(Class targetClass, Mastered master)
    {
        try
        {
            QuerySpec query = new QuerySpec(targetClass);
            query.appendWhere(VersionControlHelper.getSearchCondition(targetClass, master), new int[] { 0 });
            query.appendAnd();
            query.appendWhere(VersionControlHelper.getSearchCondition(targetClass, true), new int[] { 0 });

            QueryResult result = PersistenceHelper.manager.find(query);
            while (result.hasMoreElements())
                return (WTObject) result.nextElement();
        }
        catch (QueryException e)
        {
            e.printStackTrace();
        }
        catch (VersionControlException e)
        {
            e.printStackTrace();
        }
        catch (WTException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Major와 Minor 버전을 모두 출력한다.
     * 
     * @param obj
     * @return
     * @throws Exception
     */
    public static String getVersion(RevisionControlled obj) throws Exception
    {
        return getMajorVersion(obj) + "." + VersionControlHelper.getIterationIdentifier(obj).getSeries().getValue();
    }

    /**
     * Major 버전만을 출력한다.
     * 
     * @param obj
     * @return
     * @throws Exception
     */
    public static String getMajorVersion(RevisionControlled obj) throws Exception
    {
        return VersionControlHelper.getVersionIdentifier(obj).getSeries().getValue();
    }

    public static void setVersion(Versioned versioned, String ver) throws SeriesIncrementInvalidException,
            VersionControlException, WTPropertyVetoException, WTException
    {
        //VersionControlHelper.getQualifiedIdentifier(versioned);
        MultilevelSeries multilevelseries = MultilevelSeries.newMultilevelSeries("wt.vc.VersionIdentifier", ver);
        VersionIdentifier versionidentifier = VersionIdentifier.newVersionIdentifier(multilevelseries);
        VersionControlHelper.setVersionIdentifier(versioned, versionidentifier);

        Series series = Series.newSeries("wt.vc.IterationIdentifier", "1");
        IterationIdentifier iterationidentifier = IterationIdentifier.newIterationIdentifier(series);
        VersionControlHelper.setIterationIdentifier(versioned, iterationidentifier);
    }

    public static Versioned revise(Versioned obj, String ver, String state) throws VersionControlException,
            WTPropertyVetoException, WTException
    {
        String lifecycle = ((LifeCycleManaged) obj).getLifeCycleName();
        Folder folder = FolderHelper.service.getFolder((FolderEntry) obj);
        MultilevelSeries multilevelseries = MultilevelSeries.newMultilevelSeries("wt.vc.VersionIdentifier", ver);
        VersionIdentifier vi = VersionIdentifier.newVersionIdentifier(multilevelseries);

        obj = VersionControlHelper.service.newVersion(obj, vi, VersionControlHelper.firstIterationId(obj));
        FolderHelper.assignLocation((FolderEntry) obj, folder);
        LifeCycleHelper.setLifeCycle((LifeCycleManaged) obj, LifeCycleHelper.service.getLifeCycleTemplate(lifecycle));
        obj = (Versioned) PersistenceHelper.manager.save((Persistable) obj);

        if (state != null)
        {
            LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) obj, State.toState(state), false);
        }
        return obj;
    }

    public static Versioned revise(Versioned obj)
    {
        String lifecycle = ((LifeCycleManaged) obj).getLifeCycleName();
        Folder folder = null;
        try
        {
            folder = FolderHelper.service.getFolder((FolderEntry) obj);
        }
        catch (WTException e)
        {
            e.printStackTrace();
        }
        return revise(obj, lifecycle, folder);
    }
    
    /**
     * 개정
     * @param obj 개정할 객체
     * @param lifecycle 라이프사이클 이름
     * @param folder 저장 폴더
     * @return
     */
    public static Versioned revise(Versioned obj, String lifecycle, Folder folder)
    {
        return revise(obj, lifecycle, folder, null);
    }
    
    /**
     * 
     * @param obj
     * @param lifecycle
     * @param folder
     * @param _note 개정사유
     * @return
     */
    public static Versioned revise(Versioned obj, String lifecycle, Folder folder, String _note)
    {
        try
        {
            obj = VersionControlHelper.service.newVersion(obj);
            if(_note != null)
                VersionControlHelper.setNote(obj, _note);
            FolderHelper.assignLocation((FolderEntry) obj, folder);
            LifeCycleHelper.setLifeCycle((LifeCycleManaged) obj, LifeCycleHelper.service
                    .getLifeCycleTemplate(lifecycle));
            return (Versioned) PersistenceHelper.manager.save((Persistable) obj);
        }
        catch (WTException e)
        {
            e.printStackTrace();
        }
        catch (WTPropertyVetoException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static Versioned revise(Versioned obj, String lifecycle)
    {
        if (lifecycle == null)
            lifecycle = ((LifeCycleManaged) obj).getLifeCycleName();
        Folder folder = null;
        try
        {
            folder = FolderHelper.service.getFolder((FolderEntry) obj);
        }
        catch (WTException e)
        {
            e.printStackTrace();
        }
        
        return revise(obj, lifecycle, folder);
    }

    public static Versioned revise(Versioned obj, String ver, String lifecycle, String state)
            throws VersionControlException, WTPropertyVetoException, WTException
    {
        System.out.println("VersionUtil revise()-------------------------------------------------");
        System.out.println("arg lifecycle - " + lifecycle);
        if (lifecycle == null)
            lifecycle = ((LifeCycleManaged) obj).getLifeCycleName();
        wt.folder.Folder folder = FolderHelper.service.getFolder((FolderEntry) obj);
        MultilevelSeries multilevelseries = MultilevelSeries.newMultilevelSeries("wt.vc.VersionIdentifier", ver);
        VersionIdentifier vi = VersionIdentifier.newVersionIdentifier(multilevelseries);
        obj = VersionControlHelper.service.newVersion(obj, vi, VersionControlHelper.firstIterationId(obj));
        FolderHelper.assignLocation((FolderEntry) obj, folder);
        System.out.println("LifeCycleHelper.service.getLifeCycleTemplate--"
                + (LifeCycleHelper.service.getLifeCycleTemplate(lifecycle)).getName());
        LifeCycleHelper.setLifeCycle((LifeCycleManaged) obj, LifeCycleHelper.service.getLifeCycleTemplate(lifecycle));
        obj = (Versioned) PersistenceHelper.manager.save(obj);
        if (state != null)
            LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) obj, State.toState(state), false);
        return obj;
    }
}
