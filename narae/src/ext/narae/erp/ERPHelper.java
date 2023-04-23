package ext.narae.erp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import ext.narae.service.change.EChangeOrder2;
import ext.narae.service.change.EChangeRequest2;
import ext.narae.service.change.EcoPartLink;
import ext.narae.service.change.beans.ChangeHelper;
import ext.narae.service.erp.ERPHistory;
import ext.narae.service.erp.beans.ERPSearchHelper;
import ext.narae.service.erp.beans.ERPUtil;
import ext.narae.util.code.NumberCode;
import ext.narae.util.code.beans.CodeHelper;
import ext.narae.util.db.DBConnectionManager;
import wt.fc.QueryResult;

public class ERPHelper {
	
	
	static final String ERP 			= ERPUtil.ERP;
	
	public static final String MASTER_WORKING 	= "승인됨";
	public static final String MASTER_COMPLETE 	= "완료됨";
	public static ERPHelper manager = new ERPHelper();
	
	public HashMap erpPartSend(EcoPartLink link,ERPHistory erp){
		
		HashMap mapRe = new HashMap();
	
		return mapRe;
	}

	
	public String getRequestOrderList(EChangeOrder2 eco){
		
		String ecrList ="";
		try{
			 QueryResult rt = ChangeHelper.manager.getRequestOrderLink(eco);
			 
			 while(rt.hasMoreElements()){
				 EChangeRequest2 ecr = (EChangeRequest2)rt.nextElement();
				 
				 ecrList += ","+ecr.getRequestNumber();
			 }
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		return "";
	}
	
	public HashMap erpEcoCompleteSend(EChangeOrder2 eco,Connection con){
		
		HashMap mapRe = new HashMap();
		try{
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return mapRe;
	}
	
	/*
	 * NumberCode 
	 * 변경 사유 : CHANGEPURPOSE
	 * 설변 구분 : EOTYPE
	 * 규격        : SPEC
	 * */
	public void erpCodeSend(QueryResult qr,String sqlType) throws Exception{
		ArrayList list = new ArrayList(); 
		while(qr.hasMoreElements()){
			Object obj = qr.nextElement();
//			System.out.println("obj------->>>"+obj);
			NumberCode ncode = null;
			if(obj instanceof Object[]) {
				Object[] nObj = (Object[])obj;
				ncode = (NumberCode)nObj[0];
			}else {
				ncode = (NumberCode)obj;
			}
		        
		    list.add(ncode);
		 }
		
		this.erpCodeSend(list, sqlType);
	}
	
	public void erpCodeSend(ArrayList<NumberCode> list,String sqlType) throws Exception{
		DBConnectionManager dbmanager = null;
		Connection con = null;
		try{
			dbmanager = DBConnectionManager.getInstance();
			con = dbmanager.getConnection(ERP);
			ERPSearchHelper erpS = new ERPSearchHelper();
			boolean isExist = false;
			for( int i = 0 ; i< list.size() ; i++){
				
				NumberCode code = list.get(i);
				//System.out.println("AAAAAAAAAAAAAAAAAA");
				isExist = erpS.duplicationCode(code.getCodeType().toString(), code.getCode(),code.getParent());
				//System.out.println("BBBBBBBBBBBBBBBBBB");
				if(!isExist){
					this.erpCodeSend(code,con,sqlType);
				}else{
					System.out.println("ERP 중복 코드 : " +code.getCodeType().getDisplay(Locale.KOREA) +":" + code.getCode() +":"+ code.getName());
				}
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
			throw ex;
		}finally{
			if(dbmanager!=null){
				dbmanager.freeConnection(ERP,con);
			}
		}
	}
	
	public HashMap erpCodeSend(NumberCode code,Connection con,String sqlType){
		
		boolean result = false;
		HashMap mapRe = new HashMap();
		try{
			
			if(code.getCodeType().toString().equals("CADCREATOR")) {				//도면구성분류 (ERP 품목 구성 분류) 	: PDM01
				mapRe = this.erpPDM01(code, con, sqlType);
			}else if(code.getCodeType().toString().equals("CADATTRIBUTE")) {
				if(code.getParent() == null){										//도면 분류 체계 (ERP 품목 유형 분류)	: PDM02
					mapRe = this.erpPDM02(code, con, sqlType);
				}else{
					NumberCode pcode = CodeHelper.manager.getTopParent(code);
					int level =0 ;
					level = CodeHelper.manager.getCodelevel(code, level);
					
//					System.out.println("level------->>>"+level);
					
					if(pcode.getCode().equals("A")){                        		//반제품 
						if(level == 1){												//반제품 - UNIT 					: PDM07
							mapRe = this.erpPDM07(code, con, sqlType);
						}else if(level == 2){										//반제품 -Assy					: PDM08
							mapRe = this.erpPDM08(code, con, sqlType);
						}
					}else if(pcode.getCode().equals("B")){							//구매품(원래료)
						if(level == 1){												//구매품 - 제품군   					: PDM09
							mapRe = this.erpPDM09(code, con, sqlType);
						}else if(level == 2){										//구매품 - 제품기능 				: PDM10
							mapRe = this.erpPDM10(code, con, sqlType);
						}else if(level == 3){										//구매품 - 예비 					: PDM10R
							mapRe = this.erpPDM10R(code, con, sqlType);
						}
					}else if(pcode.getCode().equals("P")){							//가공품(원래료)
						if(level == 1){												//가공품 - 방법 					: PDM11
							mapRe = this.erpPDM11(code, con, sqlType);
						}else if(level == 2){										//가공품 - 분류					: PDM12
							mapRe = this.erpPDM12(code, con, sqlType);
						}else if(level == 3){										//가공품  -예비 					: PDM13
							mapRe = this.erpPDM12R(code, con, sqlType);
						}
					}
				}
			}else if(code.getCodeType().toString().equals("SBUSINESS")) {			//제품 사업부 (ERP-제품 사업군) 		: PDM04
				mapRe = this.erpPDM04(code, con, sqlType);
			}else if(code.getCodeType().toString().equals("SCUSTOMER")) {			//제품 고객사 (ERP-제품 고객사) 		: PDM05
				mapRe = this.erpPDM05(code, con, sqlType);
			}else if(code.getCodeType().toString().equals("SCLASS1")) {				//제품 중분류1 (ERP-제품 장비 분류) 	: PDM03
				mapRe = this.erpPDM03(code, con, sqlType);
			}else if(code.getCodeType().toString().equals("SCLASS2")) {				//제품 중분류2 (ERP-제품 세대)		: PDM06
				mapRe = this.erpPDM06(code, con, sqlType);
			}else if(code.getCodeType().toString().equals("SCLASS3")) {				//제품 중분류2 (ERP-제품 예비)		: PDM06R
				mapRe = this.erpPDM06R(code, con, sqlType);
			}else if(code.getCodeType().toString().equals("CHANGEPURPOSE")){	 	//변경사유 ,요청 유형  				: PDM16
				mapRe = this.erpPDM16(code, con, sqlType);
			}else if(code.getCodeType().toString().equals("EOTYPE")){ 				// 설변구분                     			: pDM17
				mapRe = this.erpPDM17(code, con, sqlType);
			}else if(code.getCodeType().toString().equals("SPEC")){ 				// 규격							: PDM13
				mapRe = this.erpPDM13(code, con, sqlType);
			}else{
				mapRe.put("result", "F");
				mapRe.put("message", "ERP 전송 코드가 아닙니다.");
			}
			
		}catch(Exception e){
			mapRe.put("result", "F");
			mapRe.put("message", e.getLocalizedMessage());
			e.printStackTrace();
		}
		
		return mapRe;
	}
	
	public HashMap erpCodeSend(NumberCode code,String sqlType) throws Exception{
		
		DBConnectionManager dbmanager = null;
		Connection con = null;
		boolean result = false;
		HashMap map = new HashMap();
		try{
			dbmanager = DBConnectionManager.getInstance();
			con = dbmanager.getConnection(ERP);
			ERPSearchHelper erpS = new ERPSearchHelper();
			//boolean isExist = false;
			
			//isExist = erpS.duplicationCode(code.getCodeType().toString(), code.getCode());
			//if(!isExist)
			map = this.erpCodeSend(code,con,sqlType);
			
		}catch(Exception ex){
			
			ex.printStackTrace();
			throw ex;
		}finally{
			if(dbmanager!=null){
				dbmanager.freeConnection(ERP,con);
			}
		}
		
		return map;
		
	}
	
	public HashMap erpPDM01(NumberCode code,Connection con,String sqlType){
		
		HashMap mapRe = new HashMap();
		
		try{
			StringBuffer sql = new StringBuffer();
			String codekey = code.getCode();
			String codeName = code.getName();
			
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM01(ERPUtil.SQLCREATE);
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM01(ERPUtil.SQLUPDATE);
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM01(ERPUtil.SQLDELETE);
			}
			
			if(sql.toString().length()==0) {
				mapRe.put("result", "F");
				mapRe.put("message", "SQL문 ERROR");
				return mapRe;
			}
			
			PreparedStatement st = con.prepareStatement(sql.toString());
			
			int idx = 1;
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				st.setString(idx, codekey);
				st.setString(++idx, codeName);
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				st.setString(idx, codeName);
				st.setString(++idx, codekey);
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				st.setString(idx, codekey);
			}
			
			boolean re = st.execute();
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "등록 성공");
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "UPDATE 성공");
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "삭제 성공");
			}
			
		}catch(Exception e){
			mapRe.put("result", "F");
			mapRe.put("message", e.getLocalizedMessage());
			e.printStackTrace();
		}
		
		return mapRe;
	}
	
	public HashMap erpPDM02(NumberCode code,Connection con,String sqlType){
		
		HashMap mapRe = new HashMap();
		
		try{
			StringBuffer sql = new StringBuffer();
			String codekey = code.getCode();
			String codeName = code.getName();
			
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM02(ERPUtil.SQLCREATE);
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM02(ERPUtil.SQLUPDATE);
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM02(ERPUtil.SQLDELETE);
			}
			
			if(sql.toString().length()==0) {
				mapRe.put("result", "F");
				mapRe.put("message", "SQL문 ERROR");
				return mapRe;
			}
			
			PreparedStatement st = con.prepareStatement(sql.toString());
			
			int idx = 1;
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				st.setString(idx, codekey);
				st.setString(++idx, codeName);
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				st.setString(idx, codeName);
				st.setString(++idx, codekey);
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				st.setString(idx, codekey);
			}
			
			boolean re = st.execute();
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "등록 성공");
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "UPDATE 성공");
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "삭제 성공");
			}
			
		}catch(Exception e){
			mapRe.put("result", "F");
			mapRe.put("message", e.getLocalizedMessage());
			e.printStackTrace();
		}
		
		return mapRe;
	}
	
	public HashMap erpPDM03(NumberCode code,Connection con,String sqlType){
		
		HashMap mapRe = new HashMap();
		
		try{
			StringBuffer sql = new StringBuffer();
			String codekey = code.getCode();
			String codeName = code.getName();
			
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM03(ERPUtil.SQLCREATE);
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM03(ERPUtil.SQLUPDATE);
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM03(ERPUtil.SQLDELETE);
			}
			
			if(sql.toString().length()==0) {
				mapRe.put("result", "F");
				mapRe.put("message", "SQL문 ERROR");
				return mapRe;
			}
			
			PreparedStatement st = con.prepareStatement(sql.toString());
			
			int idx = 1;
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				st.setString(idx, codekey);
				st.setString(++idx, codeName);
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				st.setString(idx, codeName);
				st.setString(++idx, codekey);
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				st.setString(idx, codekey);
			}
			
			boolean re = st.execute();
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "등록 성공");
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "UPDATE 성공");
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "삭제 성공");
			}
			
		}catch(Exception e){
			mapRe.put("result", "F");
			mapRe.put("message", e.getLocalizedMessage());
			e.printStackTrace();
		}
		
		return mapRe;
	}
	
	public HashMap erpPDM04(NumberCode code,Connection con,String sqlType){
		
		HashMap mapRe = new HashMap();
		
		try{
			StringBuffer sql = new StringBuffer();
			String codekey = code.getCode();
			String codeName = code.getName();
			
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM04(ERPUtil.SQLCREATE);
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM04(ERPUtil.SQLUPDATE);
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM04(ERPUtil.SQLDELETE);
			}
			
			if(sql.toString().length()==0) {
				mapRe.put("result", "F");
				mapRe.put("message", "SQL문 ERROR");
				return mapRe;
			}
			
			PreparedStatement st = con.prepareStatement(sql.toString());
			
			int idx = 1;
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				st.setString(idx, codekey);
				st.setString(++idx, codeName);
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				st.setString(idx, codeName);
				st.setString(++idx, codekey);
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				st.setString(idx, codekey);
			}
			
			boolean re = st.execute();
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "등록 성공");
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "UPDATE 성공");
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "삭제 성공");
			}
			
		}catch(Exception e){
			mapRe.put("result", "F");
			mapRe.put("message", e.getLocalizedMessage());
			e.printStackTrace();
		}
		
		return mapRe;
	}
	
	public HashMap erpPDM05(NumberCode code,Connection con,String sqlType){
		
		HashMap mapRe = new HashMap();
		
		try{
			StringBuffer sql = new StringBuffer();
			String codekey = code.getCode();
			String codeName = code.getName();
			
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM05(ERPUtil.SQLCREATE);
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM05(ERPUtil.SQLUPDATE);
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM05(ERPUtil.SQLDELETE);
			}
			
			if(sql.toString().length()==0) {
				mapRe.put("result", "F");
				mapRe.put("message", "SQL문 ERROR");
				return mapRe;
			}
			
			PreparedStatement st = con.prepareStatement(sql.toString());
			
			int idx = 1;
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				st.setString(idx, codekey);
				st.setString(++idx, codeName);
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				st.setString(idx, codeName);
				st.setString(++idx, codekey);
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				st.setString(idx, codekey);
			}
			
			boolean re = st.execute();
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "등록 성공");
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "UPDATE 성공");
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "삭제 성공");
			}
			
		}catch(Exception e){
			mapRe.put("result", "F");
			mapRe.put("message", e.getLocalizedMessage());
			e.printStackTrace();
		}
		
		return mapRe;
	}
	
	public HashMap erpPDM06(NumberCode code,Connection con,String sqlType){
		
		HashMap mapRe = new HashMap();
		
		try{
			StringBuffer sql = new StringBuffer();
			String codekey = code.getCode();
			String codeName = code.getName();
			
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM06(ERPUtil.SQLCREATE);
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM06(ERPUtil.SQLUPDATE);
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM06(ERPUtil.SQLDELETE);
			}
			
			if(sql.toString().length()==0) {
				mapRe.put("result", "F");
				mapRe.put("message", "SQL문 ERROR");
				return mapRe;
			}
			
			PreparedStatement st = con.prepareStatement(sql.toString());
			
			int idx = 1;
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				st.setString(idx, codekey);
				st.setString(++idx, codeName);
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				st.setString(idx, codeName);
				st.setString(++idx, codekey);
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				st.setString(idx, codekey);
			}
			
			boolean re = st.execute();
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "등록 성공");
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "UPDATE 성공");
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "삭제 성공");
			}
			
		}catch(Exception e){
			mapRe.put("result", "F");
			mapRe.put("message", e.getLocalizedMessage());
			e.printStackTrace();
		}
		
		return mapRe;
	}
	
	public HashMap erpPDM06R(NumberCode code,Connection con,String sqlType){
		
		HashMap mapRe = new HashMap();
		
		try{
			StringBuffer sql = new StringBuffer();
			String codekey = code.getCode();
			String codeName = code.getName();
			
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM06R(ERPUtil.SQLCREATE);
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM06R(ERPUtil.SQLUPDATE);
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM06R(ERPUtil.SQLDELETE);
			}
			
			if(sql.toString().length()==0) {
				mapRe.put("result", "F");
				mapRe.put("message", "SQL문 ERROR");
				return mapRe;
			}
			
			PreparedStatement st = con.prepareStatement(sql.toString());
			
			int idx = 1;
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				st.setString(idx, codekey);
				st.setString(++idx, codeName);
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				st.setString(idx, codeName);
				st.setString(++idx, codekey);
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				st.setString(idx, codekey);
			}
			
			boolean re = st.execute();
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "등록 성공");
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "UPDATE 성공");
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "삭제 성공");
			}
			
		}catch(Exception e){
			mapRe.put("result", "F");
			mapRe.put("message", e.getLocalizedMessage());
			e.printStackTrace();
		}
		
		return mapRe;
	}
	
	public HashMap erpPDM07(NumberCode code,Connection con,String sqlType){
		
		HashMap mapRe = new HashMap();
		
		try{
			StringBuffer sql = new StringBuffer();
			String codekey = code.getCode();
			String codeName = code.getName();
			
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM07(ERPUtil.SQLCREATE);
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM07(ERPUtil.SQLUPDATE);
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM07(ERPUtil.SQLDELETE);
			}
			
			if(sql.toString().length()==0) {
				mapRe.put("result", "F");
				mapRe.put("message", "SQL문 ERROR");
				return mapRe;
			}
			
			PreparedStatement st = con.prepareStatement(sql.toString());
			
			int idx = 1;
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				st.setString(idx, codekey);
				st.setString(++idx, codeName);
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				st.setString(idx, codeName);
				st.setString(++idx, codekey);
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				st.setString(idx, codekey);
			}
			
			boolean re = st.execute();
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "등록 성공");
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "UPDATE 성공");
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "삭제 성공");
			}
			
		}catch(Exception e){
			mapRe.put("result", "F");
			mapRe.put("message", e.getLocalizedMessage());
			e.printStackTrace();
		}
		
		return mapRe;
	}
	
	public HashMap erpPDM08(NumberCode code,Connection con,String sqlType){
		
		HashMap mapRe = new HashMap();
		
		try{
			StringBuffer sql = new StringBuffer();
			String codekey = code.getCode();
			String codeName = code.getName();
		
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM08(ERPUtil.SQLCREATE);
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM08(ERPUtil.SQLUPDATE);
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM08(ERPUtil.SQLDELETE);
			}
			
			if(sql.toString().length()==0) {
				mapRe.put("result", "F");
				mapRe.put("message", "SQL문 ERROR");
				return mapRe;
			}
			
			PreparedStatement st = con.prepareStatement(sql.toString());
			
			int idx = 1;
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				st.setString(idx,code.getParent().getCode());
				st.setString(++idx, codekey);
				st.setString(++idx, codeName);
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				st.setString(idx, codeName);
				st.setString(++idx,code.getParent().getCode());
				st.setString(++idx, codekey);
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				st.setString(idx,code.getParent().getCode());
				st.setString(++idx, codekey);
			}
			
			boolean re = st.execute();
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "등록 성공");
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "UPDATE 성공");
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "삭제 성공");
			}
			
		}catch(Exception e){
			mapRe.put("result", "F");
			mapRe.put("message", e.getLocalizedMessage());
			e.printStackTrace();
		}
		
		return mapRe;
	}
	
	public HashMap erpPDM09(NumberCode code,Connection con,String sqlType){
		
		HashMap mapRe = new HashMap();
		
		try{
			StringBuffer sql = new StringBuffer();
			String codekey = code.getCode();
			String codeName = code.getName();
			
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM09(ERPUtil.SQLCREATE);
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM09(ERPUtil.SQLUPDATE);
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM09(ERPUtil.SQLDELETE);
			}
			
			if(sql.toString().length()==0) {
				mapRe.put("result", "F");
				mapRe.put("message", "SQL문 ERROR");
				return mapRe;
			}
			
			PreparedStatement st = con.prepareStatement(sql.toString());
			
			int idx = 1;
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				st.setString(idx, codekey);
				st.setString(++idx, codeName);
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				st.setString(idx, codeName);
				st.setString(++idx, codekey);
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				st.setString(idx, codekey);
			}
			
			boolean re = st.execute();
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "등록 성공");
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "UPDATE 성공");
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "삭제 성공");
			}
			
		}catch(Exception e){
			mapRe.put("result", "F");
			mapRe.put("message", e.getLocalizedMessage());
			e.printStackTrace();
		}
		
		return mapRe;
	}
	
	public HashMap erpPDM10(NumberCode code,Connection con,String sqlType){
		
		HashMap mapRe = new HashMap();
		
		try{
			StringBuffer sql = new StringBuffer();
			String codekey = code.getCode();
			String codeName = code.getName();
		
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM10(ERPUtil.SQLCREATE);
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM10(ERPUtil.SQLUPDATE);
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM10(ERPUtil.SQLDELETE);
			}
			
			if(sql.toString().length()==0) {
				mapRe.put("result", "F");
				mapRe.put("message", "SQL문 ERROR");
				return mapRe;
			}
			
			PreparedStatement st = con.prepareStatement(sql.toString());
			
			int idx = 1;
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				st.setString(idx,code.getParent().getCode());
				st.setString(++idx, codekey);
				st.setString(++idx, codeName);
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				st.setString(idx, codeName);
				st.setString(++idx,code.getParent().getCode());
				st.setString(++idx, codekey);
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				st.setString(idx,code.getParent().getCode());
				st.setString(++idx, codekey);
			}
			
			boolean re = st.execute();
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "등록 성공");
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "UPDATE 성공");
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "삭제 성공");
			}
			
		}catch(Exception e){
			mapRe.put("result", "F");
			mapRe.put("message", e.getLocalizedMessage());
			e.printStackTrace();
		}
		
		return mapRe;
	}
	
	public HashMap erpPDM10R(NumberCode code,Connection con,String sqlType){
		
		HashMap mapRe = new HashMap();
		
		try{
			StringBuffer sql = new StringBuffer();
			String codekey = code.getCode();
			String codeName = code.getName();
		
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM10R(ERPUtil.SQLCREATE);
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM10R(ERPUtil.SQLUPDATE);
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM10R(ERPUtil.SQLDELETE);
			}
			
			if(sql.toString().length()==0) {
				mapRe.put("result", "F");
				mapRe.put("message", "SQL문 ERROR");
				return mapRe;
			}
			
			PreparedStatement st = con.prepareStatement(sql.toString());
			
			int idx = 1;
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				st.setString(idx,code.getParent().getParent().getCode());
				st.setString(++idx,code.getParent().getCode());
				st.setString(++idx, codekey);
				st.setString(++idx, codeName);
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				st.setString(idx, codeName);
				st.setString(++idx,code.getParent().getParent().getCode());
				st.setString(++idx,code.getParent().getCode());
				st.setString(++idx, codekey);
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				st.setString(idx,code.getParent().getParent().getCode());
				st.setString(++idx,code.getParent().getCode());
				st.setString(++idx, codekey);
			}
			
			boolean re = st.execute();
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "등록 성공");
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "UPDATE 성공");
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "삭제 성공");
			}
			
		}catch(Exception e){
			mapRe.put("result", "F");
			mapRe.put("message", e.getLocalizedMessage());
			e.printStackTrace();
		}
		
		return mapRe;
	}
	
	public HashMap erpPDM11(NumberCode code,Connection con,String sqlType){
		
		HashMap mapRe = new HashMap();
		
		try{
			StringBuffer sql = new StringBuffer();
			String codekey = code.getCode();
			String codeName = code.getName();
			
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM11(ERPUtil.SQLCREATE);
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM11(ERPUtil.SQLUPDATE);
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM11(ERPUtil.SQLDELETE);
			}
			
			if(sql.toString().length()==0) {
				mapRe.put("result", "F");
				mapRe.put("message", "SQL문 ERROR");
				return mapRe;
			}
			
			PreparedStatement st = con.prepareStatement(sql.toString());
			
			int idx = 1;
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				st.setString(idx, codekey);
				st.setString(++idx, codeName);
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				st.setString(idx, codeName);
				st.setString(++idx, codekey);
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				st.setString(idx, codekey);
			}
			
			boolean re = st.execute();
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "등록 성공");
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "UPDATE 성공");
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "삭제 성공");
			}
			
		}catch(Exception e){
			mapRe.put("result", "F");
			mapRe.put("message", e.getLocalizedMessage());
			e.printStackTrace();
		}
		
		return mapRe;
	}
	
	public HashMap erpPDM12(NumberCode code,Connection con,String sqlType){
		
		HashMap mapRe = new HashMap();
		
		try{
			StringBuffer sql = new StringBuffer();
			String codekey = code.getCode();
			String codeName = code.getName();
		
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM12(ERPUtil.SQLCREATE);
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM12(ERPUtil.SQLUPDATE);
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM12(ERPUtil.SQLDELETE);
			}
			
			if(sql.toString().length()==0) {
				mapRe.put("result", "F");
				mapRe.put("message", "SQL문 ERROR");
				return mapRe;
			}
			
			PreparedStatement st = con.prepareStatement(sql.toString());
			
			int idx = 1;
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				st.setString(idx,code.getParent().getCode());
				st.setString(++idx, codekey);
				st.setString(++idx, codeName);
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				st.setString(idx, codeName);
				st.setString(++idx,code.getParent().getCode());
				st.setString(++idx, codekey);
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				st.setString(idx,code.getParent().getCode());
				st.setString(++idx, codekey);
			}
			
			boolean re = st.execute();
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "등록 성공");
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "UPDATE 성공");
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "삭제 성공");
			}
			
		}catch(Exception e){
			mapRe.put("result", "F");
			mapRe.put("message", e.getLocalizedMessage());
			e.printStackTrace();
		}
		
		return mapRe;
	}
	
	public HashMap erpPDM12R(NumberCode code,Connection con,String sqlType){
		
		HashMap mapRe = new HashMap();
		
		try{
			StringBuffer sql = new StringBuffer();
			String codekey = code.getCode();
			String codeName = code.getName();
		
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM12R(ERPUtil.SQLCREATE);
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM12R(ERPUtil.SQLUPDATE);
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM12R(ERPUtil.SQLDELETE);
			}
			
			if(sql.toString().length()==0) {
				mapRe.put("result", "F");
				mapRe.put("message", "SQL문 ERROR");
				return mapRe;
			}
			
			PreparedStatement st = con.prepareStatement(sql.toString());
			
			int idx = 1;
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				st.setString(idx,code.getParent().getParent().getCode());
				st.setString(++idx,code.getParent().getCode());
				st.setString(++idx, codekey);
				st.setString(++idx, codeName);
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				st.setString(idx, codeName);
				st.setString(++idx,code.getParent().getParent().getCode());
				st.setString(++idx,code.getParent().getCode());
				st.setString(++idx, codekey);
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				st.setString(idx,code.getParent().getParent().getCode());
				st.setString(++idx,code.getParent().getCode());
				st.setString(++idx, codekey);
			}
			
			boolean re = st.execute();
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "등록 성공");
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "UPDATE 성공");
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "삭제 성공");
			}
			
		}catch(Exception e){
			mapRe.put("result", "F");
			mapRe.put("message", e.getLocalizedMessage());
			e.printStackTrace();
		}
		
		return mapRe;
	}
	
	public HashMap erpPDM13(NumberCode code,Connection con,String sqlType){
		
		HashMap mapRe = new HashMap();
		
		try{
			StringBuffer sql = new StringBuffer();
			String codekey = code.getCode();
			String codeName = code.getName();
			
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM13(ERPUtil.SQLCREATE);
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM13(ERPUtil.SQLUPDATE);
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM13(ERPUtil.SQLDELETE);
			}
			
			if(sql.toString().length()==0) {
				mapRe.put("result", "F");
				mapRe.put("message", "SQL문 ERROR");
				return mapRe;
			}
			
			PreparedStatement st = con.prepareStatement(sql.toString());
			
			int idx = 1;
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				st.setString(idx, codekey);
				st.setString(++idx, codeName);
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				st.setString(idx, codeName);
				st.setString(++idx, codekey);
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				st.setString(idx, codekey);
			}
			
			boolean re = st.execute();
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "등록 성공");
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "UPDATE 성공");
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "삭제 성공");
			}
			
		}catch(Exception e){
			mapRe.put("result", "F");
			mapRe.put("message", e.getLocalizedMessage());
			e.printStackTrace();
		}
		
		return mapRe;
	}
	
	public HashMap erpPDM16(NumberCode code,Connection con,String sqlType){
		
		HashMap mapRe = new HashMap();
		
		try{
			StringBuffer sql = new StringBuffer();
			String codekey = code.getCode();
			String codeName = code.getName();
			
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM16(ERPUtil.SQLCREATE);
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM16(ERPUtil.SQLUPDATE);
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM16(ERPUtil.SQLDELETE);
			}
			
			if(sql.toString().length()==0) {
				mapRe.put("result", "F");
				mapRe.put("message", "SQL문 ERROR");
				return mapRe;
			}
			
			PreparedStatement st = con.prepareStatement(sql.toString());
			
			int idx = 1;
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				st.setString(idx, codekey);
				st.setString(++idx, codeName);
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				st.setString(idx, codeName);
				st.setString(++idx, codekey);
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				st.setString(idx, codekey);
			}
			
			boolean re = st.execute();
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "등록 성공");
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "UPDATE 성공");
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "삭제 성공");
			}
			
		}catch(Exception e){
			mapRe.put("result", "F");
			mapRe.put("message", e.getLocalizedMessage());
			e.printStackTrace();
		}
		
		return mapRe;
	}
	
	public HashMap erpPDM17(NumberCode code,Connection con,String sqlType){
		
		HashMap mapRe = new HashMap();
		
		try{
			StringBuffer sql = new StringBuffer();
			String codekey = code.getCode();
			String codeName = code.getName();
			
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM17(ERPUtil.SQLCREATE);
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM17(ERPUtil.SQLUPDATE);
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				sql = ERPUtil.getSQLPDM17(ERPUtil.SQLDELETE);
			}
			
			if(sql.toString().length()==0) {
				mapRe.put("result", "F");
				mapRe.put("message", "SQL문 ERROR");
				return mapRe;
			}
			
			PreparedStatement st = con.prepareStatement(sql.toString());
			
			int idx = 1;
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				st.setString(idx, codekey);
				st.setString(++idx, codeName);
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				st.setString(idx, codeName);
				st.setString(++idx, codekey);
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				st.setString(idx, codekey);
			}
			
			boolean re = st.execute();
			if(ERPUtil.SQLCREATE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "등록 성공");
			}else if(ERPUtil.SQLUPDATE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "UPDATE 성공");
			}else if(ERPUtil.SQLDELETE.equals(sqlType)){
				mapRe.put("result", "S");
				mapRe.put("message", "삭제 성공");
			}
			
		}catch(Exception e){
			mapRe.put("result", "F");
			mapRe.put("message", e.getLocalizedMessage());
			e.printStackTrace();
		}
		
		return mapRe;
	}
}
