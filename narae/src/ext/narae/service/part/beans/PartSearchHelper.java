package ext.narae.service.part.beans;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import ext.narae.service.change.EChangeOrder2;
import ext.narae.service.change.EcoPartLink;
import ext.narae.service.change.beans.ChangeECOHelper;
import ext.narae.service.drawing.GPartEPMLink;
import ext.narae.util.CommonUtil;
import ext.narae.util.StringUtil;
import ext.narae.util.code.beans.CodeHelper;
import wt.epm.EPMDocument;
import wt.epm.build.EPMBuildHistory;
import wt.epm.build.EPMBuildRule;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.method.MethodContext;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.pom.DBProperties;
import wt.pom.WTConnection;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.StringSearch;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;

public class PartSearchHelper implements wt.method.RemoteAccess {
	
	static final boolean SERVER = wt.method.RemoteMethodServer.ServerFlag;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Hashtable hash = new Hashtable();
		hash.put("number","NS-P1-3010");
		hash.put("maker","(주)트라이텍코퍼레이션");
		hash.put("spec","GP-1 0.35MM");
		hash.put("quantityUnit","ea");
		try {
			duplicationNumber(hash);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
	public static Hashtable duplicationNumber(Hashtable hash) throws Exception
    {
		;
        if ( !SERVER ) {
            Class argTypes[] = new Class[] { Hashtable.class };
            Object args[] = new Object[] { hash };
            try {
                return (Hashtable) wt.method.RemoteMethodServer.getDefault().invoke("duplicationNumber", PartSearchHelper.class.getName(), null, argTypes, args);
            }
            catch ( RemoteException e ) {
                e.printStackTrace();
                throw new WTException(e);
            }
            catch ( InvocationTargetException e ) {
                e.printStackTrace();
                throw new WTException(e);
            }
            catch ( Exception e ) {
                e.printStackTrace();
                throw e;
            }
        }
        
        Hashtable rst = new Hashtable();
        
        try{
        	QuerySpec query = new QuerySpec();
            QueryResult qr = null;
            StringSearch stringsearch = null;
            SearchCondition sc = null;
            
            String number 		= (String)hash.get("number");
            String maker 		= (String)hash.get("maker");
            String spec 		= (String)hash.get("spec");
            String quantityUnit = (String)hash.get("quantityunit");
            String dubleCheck = "false";
            Class  stClass =wt.iba.value.StringValue.class;
            int idx = query.addClassList(WTPart.class, true);
            
           //System.out.println("number :" + number);
            //System.out.println("maker :" + maker);
            //System.out.println("spec :" + spec);
            //System.out.println("quantityUnit :" + quantityUnit);
            
            //최신 이터레이션
            query.appendWhere(VersionControlHelper.getSearchCondition(WTPart.class, true), new int[] { idx });
            
            //자재번호
            if(StringUtil.checkString(number)) {
                if (query.getConditionCount() > 0) { query.appendAnd(); }
                stringsearch = new StringSearch("number");
                stringsearch.setValue(number.trim()+"%");
                query.appendWhere(stringsearch.getSearchCondition(WTPart.class), new int[]{idx});
            }
            //단위 defaultUnit
            /*
            if (query.getConditionCount() > 0) { query.appendAnd(); }
            query.appendWhere(new SearchCondition(WTPart.class,WTPart.DEFAULT_UNIT,SearchCondition.EQUAL,quantityUnit), new int[] {idx});
           */
            //최신 버전
            AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath("LatestVersionFlag");
            if (aview != null) {
                if (query.getConditionCount() > 0) { query.appendAnd(); }
                int _idx = query.appendClassList(stClass, false);
                query.appendOpenParen();
                query.appendWhere(new SearchCondition(stClass, "theIBAHolderReference.key.id",
                                                      WTPart.class, "thePersistInfo.theObjectIdentifier.id"),
                                  new int[] { _idx, idx });
                query.appendAnd();
                query.appendWhere(new SearchCondition(stClass, "definitionReference.hierarchyID",
                                                      SearchCondition.EQUAL, aview.getHierarchyID()),
                                  new int[] { _idx });
                query.appendAnd();
                query.appendWhere(new SearchCondition(stClass, "value", SearchCondition.EQUAL, "TRUE"),
                                  new int[] { _idx });
                query.appendCloseParen();
            }
            //maker 
            /*
            if(StringUtil.checkString(maker)) {
                aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath("Maker");
                if (aview != null) {
                    if (query.getConditionCount() > 0) { query.appendAnd(); }
                    int _idx = query.appendClassList(stClass, false);
                    query.appendOpenParen();
                    query.appendWhere(new SearchCondition(stClass, "theIBAHolderReference.key.id",
                            WTPart.class, "thePersistInfo.theObjectIdentifier.id"),
					        new int[] { _idx, idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(stClass, "definitionReference.hierarchyID",
					                            SearchCondition.EQUAL, aview.getHierarchyID()),
					        new int[] { _idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(stClass, "value", SearchCondition.EQUAL, maker),
					        new int[] { _idx });
					query.appendCloseParen();
                }
			 }
            */
            //SPEC
            if(StringUtil.checkString(spec)) {
                 aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath("Spec");
                if (aview != null) {
                    if (query.getConditionCount() > 0) { query.appendAnd(); }
                   
                    int _idx = query.appendClassList(stClass, false);
                    query.appendOpenParen();
                    query.appendWhere(new SearchCondition(stClass, "theIBAHolderReference.key.id",
                            WTPart.class, "thePersistInfo.theObjectIdentifier.id"),
					        new int[] { _idx, idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(stClass, "definitionReference.hierarchyID",
					                            SearchCondition.EQUAL, aview.getHierarchyID()),
					        new int[] { _idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(stClass, "value", SearchCondition.EQUAL, spec),
					        new int[] { _idx });
					query.appendCloseParen();
                }
               
            }
            
            //System.out.println(query);
            
            QueryResult rt = PersistenceHelper.manager.find(query);
            
            //System.out.println(">>>>>>>>>>>>>> rt.size :" +rt.size());
           
            String oid ="";
            while(rt.hasMoreElements()){
            	dubleCheck = "true";
            	Object[] oo = (Object[])rt.nextElement();
            	WTPart part = (WTPart)oo[0];
            	oid = CommonUtil.getOIDString(part);
            }
            
            rst.put("return", dubleCheck);
            rst.put("oid", oid);
            
            
        }catch(Exception e){
        	e.printStackTrace();
        	rst.put("return", "false");
            rst.put("oid", "");
        }
        return rst;

    }
	
	public static  EPMBuildHistory getBuildHistory(WTPart wtpart, EPMDocument epmdocument) throws WTException
    {
        QueryResult qr = PersistenceHelper.manager.find(EPMBuildHistory.class, wtpart, "built", epmdocument);
        return qr.hasMoreElements() ? (EPMBuildHistory) qr.nextElement() : null;
    }
	
	public static  EPMBuildRule getBuildRule(Object obj) throws WTException
    {
		QueryResult qr = null;
		if(obj instanceof WTPart){
			WTPart part =(WTPart)obj;
			qr = PersistenceHelper.manager.navigate(part, "buildSource", EPMBuildRule.class, false);
		}else{
			EPMDocument epm =(EPMDocument)obj;
			qr = PersistenceHelper.manager.navigate(epm, "buildTarget", EPMBuildRule.class, false);
		}
      
        while (qr.hasMoreElements())
        {
            EPMBuildRule ebr = (EPMBuildRule) qr.nextElement();
            if (!WorkInProgressHelper.isWorkingCopy((Workable) ebr.getBuildSource()))
                return ebr;
        }

        return null;
    }
	
	public static Vector<EChangeOrder2> getPartEo(WTPart part){
		Vector<EChangeOrder2> vec = new Vector();
		try{
			QueryResult eolinkQr = PersistenceHelper.manager.navigate(part.getMaster(), "eco", EcoPartLink.class);
	 	    EChangeOrder2 eco = null;
	 	    //System.out.println("PartSearchHelper ::::::::::: getPartEo :" +eolinkQr.size());
	 	    if(eolinkQr != null && eolinkQr.size() > 0) {
	 	        while(eolinkQr.hasMoreElements()) {
	 	            eco = (EChangeOrder2)eolinkQr.nextElement();
	 	            vec.add(eco);
	 	        }
	 	    }
		}catch(Exception e){
			e.printStackTrace();
		}
		
 	    
 	    return vec;
	}
	
	public static Vector<EChangeOrder2> getPartEoWorking(WTPart part){
		Vector<EChangeOrder2> vec = new Vector();
		try{
			QueryResult eolinkQr = PersistenceHelper.manager.navigate(part.getMaster(), "eco", EcoPartLink.class);
	 	    EChangeOrder2 eco = null;
	 	    //System.out.println("PartSearchHelper ::::::::::: getPartEo :" +eolinkQr.size());
	 	    if(eolinkQr != null && eolinkQr.size() > 0) {
	 	        while(eolinkQr.hasMoreElements()) {
	 	            eco = (EChangeOrder2)eolinkQr.nextElement();
	 	            if(eco.getOrderState().equals(ChangeECOHelper.ECO_COMPLETE) || eco.getOrderState().equals(ChangeECOHelper.ECO_REJECTED) ) continue;
	 	            vec.add(eco);
	 	        }
	 	    }
		}catch(Exception e){
			e.printStackTrace();
		}
		
 	    
 	    return vec;
	}
	
	public static WTPart getLastWTPart(WTPartMaster master) throws Exception {
		
		 WTPart part = null;
		 long longoid = CommonUtil.getOIDLongValue(master);
		 Class class1 = WTPart.class;
		
		 QuerySpec qs = new QuerySpec();
		 int i = qs.appendClassList(class1, true);
		
		 qs.appendWhere(new SearchCondition(class1, "iterationInfo.latest", SearchCondition.IS_TRUE,true), new int[] { i });

		 qs.appendAnd();
		 qs.appendWhere(new SearchCondition(class1,  "checkoutInfo.state", "<>", "wrk"),new int[]{i});

		 qs.appendAnd();
		 qs.appendWhere(new SearchCondition(class1, "masterReference.key.id", SearchCondition.EQUAL,longoid), new int[] { i });
		 
		 //최신 이터레이션
		 qs.appendAnd();
		 qs.appendWhere(VersionControlHelper.getSearchCondition(WTPart.class, true), new int[] { i });
		 
		 // 최신 버젼
 		 AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath("LatestVersionFlag");
 		 if (aview != null)
		 {
			if (qs.getConditionCount() > 0) qs.appendAnd();

			int _idx = qs.appendClassList(wt.iba.value.StringValue.class, false);
			SearchCondition sc = new SearchCondition(wt.iba.value.StringValue.class, "theIBAHolderReference.key.id", WTPart.class, "thePersistInfo.theObjectIdentifier.id");
			qs.appendWhere(sc, new int[] { _idx, i });
			qs.appendAnd();
			sc = new SearchCondition(wt.iba.value.StringValue.class, "definitionReference.hierarchyID", "=", aview.getHierarchyID());
			qs.appendWhere(sc, new int[] { _idx });
			qs.appendAnd();
			sc = new SearchCondition(wt.iba.value.StringValue.class, "value", "=", "TRUE");
			qs.appendWhere(sc, new int[] { _idx });
		 }

		
		
		 QueryResult qr = PersistenceHelper.manager.find(qs);
		 while(qr.hasMoreElements()){
			 
			 Object obj[] = (Object[])qr.nextElement();
			 part = (WTPart)obj[0];
			 
		 }
		 return part;
	 }
	
	 public static String getLastECO(WTPartMaster master) throws SQLException {
		 
		 if (!SERVER) {

				try {
					Class argTypes[] = new Class[]{WTPartMaster.class,String.class,String.class,String.class};
					Object args[] = new Object[]{master};
					return (String)RemoteMethodServer.getDefault().invoke("getLastECO",PartSearchHelper.class.getName(), null,  argTypes, args);
				} catch (RemoteException e) {
					e.printStackTrace();
					
				} catch (InvocationTargetException e) {
					e.printStackTrace();
					
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
				select top(1) purpose,A0.idA2A2 from wcadmin.EChangeOrder2 A0,wcadmin.EcoPartLink A1
				where A1.idA3B5 = A0.idA2A2 
				and  A1.idA3A5 =?
				order by A0.createStampA2 desc
			*/
				long longoid = CommonUtil.getOIDLongValue(master);
				StringBuffer sb = null;

				sb = new StringBuffer()
				.append("select top(1) purpose,A0.idA2A2 from wcadmin.EChangeOrder2 A0,wcadmin.EcoPartLink A1")
				.append(" where A1.idA3B5 = A0.idA2A2 ")
				.append(" and  A1.idA3A5 ="+longoid)
				.append(" order by A0.createStampA2 desc");
				//System.out.println(sb.toString());
				st = con.prepareStatement(sb.toString());
				rs = st.executeQuery();
				
				String purpose = "";
				while (rs.next()) {
					purpose = rs.getString(1);
				}

				purpose = purpose.trim();
				String value = "";
				StringTokenizer tokens = new StringTokenizer(purpose,",");
				while(tokens.hasMoreTokens()){
					String pp = (String)tokens.nextToken();
					value =  value+","+CodeHelper.manager.getName("CHANGEPURPOSE",pp);
				}
				if(value.length()>0){
					value = value.substring(1,value.length());
				}
				return value;
			} catch (Exception e) {
				e.printStackTrace();
				
			} finally {
	            if ( rs != null ) {
	                rs.close();
	            }
	            if ( st != null ) {
	                st.close();
	            }
				if (DBProperties.FREE_CONNECTION_IMMEDIATE
						&& !wtconnection.isTransactionActive()) {
					MethodContext.getContext().freeConnection();
				}
			}
			
			return "";
		 
	 }
	 
	public EPMDocument getGPartDWG(WTPart part){
		EPMDocument returnEPM = null;
		try{
			QueryResult linkQr = PersistenceHelper.manager.navigate((WTPartMaster)part.getMaster(), "epmDoc", GPartEPMLink.class);
	    	if(linkQr.hasMoreElements()) {
	    		returnEPM = (EPMDocument)linkQr.nextElement();
	    	}
		}catch(Exception e){
			e.printStackTrace();
		}
		return returnEPM;
	}

	public static WTPart getWTPart(String partNumber) throws Exception {
		
		 WTPart part = null;
//		 long longoid = CommonUtil.getOIDLongValue(master);
		 Class class1 = WTPart.class;
		
		 QuerySpec qs = new QuerySpec();
		 int i = qs.appendClassList(class1, true);
		
		 qs.appendWhere(new SearchCondition(class1, "iterationInfo.latest", SearchCondition.IS_TRUE,true), new int[] { i });

		 qs.appendAnd();
		 qs.appendWhere(new SearchCondition(class1,  "checkoutInfo.state", "<>", "wrk"),new int[]{i});

		 qs.appendAnd();
		 qs.appendWhere(new SearchCondition(class1, "master>number", SearchCondition.EQUAL, partNumber), new int[] { i });
		 
		 //최신 이터레이션
		 qs.appendAnd();
		 qs.appendWhere(VersionControlHelper.getSearchCondition(WTPart.class, true), new int[] { i });
		 
		 // 최신 버젼
		 AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath("LatestVersionFlag");
		 if (aview != null)
		 {
			if (qs.getConditionCount() > 0) qs.appendAnd();

			int _idx = qs.appendClassList(wt.iba.value.StringValue.class, false);
			SearchCondition sc = new SearchCondition(wt.iba.value.StringValue.class, "theIBAHolderReference.key.id", WTPart.class, "thePersistInfo.theObjectIdentifier.id");
			qs.appendWhere(sc, new int[] { _idx, i });
			qs.appendAnd();
			sc = new SearchCondition(wt.iba.value.StringValue.class, "definitionReference.hierarchyID", "=", aview.getHierarchyID());
			qs.appendWhere(sc, new int[] { _idx });
			qs.appendAnd();
			sc = new SearchCondition(wt.iba.value.StringValue.class, "value", "=", "TRUE");
			qs.appendWhere(sc, new int[] { _idx });
		 }
		
		 QueryResult qr = PersistenceHelper.manager.find(qs);
		 while(qr.hasMoreElements()){
			 
			 Object obj[] = (Object[])qr.nextElement();
			 part = (WTPart)obj[0];
			 
		 }
		 return part;
	 }

}
