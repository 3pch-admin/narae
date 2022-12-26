package ext.narae.service.erp.beans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.StringTokenizer;

import ext.narae.service.approval.ApprovalMaster;
import ext.narae.service.approval.beans.ApprovalHelper;
import ext.narae.service.change.EChangeRequest2;
import ext.narae.service.change.EcrPartLink;
import ext.narae.service.erp.ERPHistory;
import ext.narae.service.erp.ERPPartLinkR;
import ext.narae.service.part.beans.PartHelper;
import ext.narae.util.DateUtil;
import ext.narae.util.code.beans.CodeHelper;
import ext.narae.util.db.DBConnectionManager;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.query.QuerySpec;
import wt.query.SearchCondition;

public class ERPECRHelper {

	public static ERPECRHelper manager = new ERPECRHelper();

	public void erpECR(EChangeRequest2 ecr, String sendType, ERPHistory history) {

		DBConnectionManager dbmanager = null;
		Connection con = null;
		HashMap mapRe = new HashMap();
		boolean partSend = true;
		try {
			dbmanager = DBConnectionManager.getInstance();
			con = dbmanager.getConnection(ERPUtil.ERP);

			if (sendType.equals(ERPUtil.HISTORY_TYPE_CONFIRM)) {
				/* ECR SEND */
				mapRe = erpECRSend(ecr, con);
				String result = (String) mapRe.get("result");
				String message = (String) mapRe.get("message");

				/* ERPHistory update */
				history.setHistoryType(ERPUtil.HISTORY_TYPE_CONFIRM);
				history.setEoType(ERPUtil.HISTORY_ECR_TYPE);
				history.setMessage(message);

				if (result.equals(ERPUtil.HISTORY_STATE_SUCCESS)) {
					history.setEcrSend(ERPUtil.ECR_SEND_SUCCESS);
				} else {
					history.setEcrSend(ERPUtil.ECR_SEND_FAILE);
				}

				PersistenceHelper.manager.modify(history);

				if (result.equals(ERPUtil.HISTORY_STATE_SUCCESS)) { // 승인됨
					/* PART SEND */
//					QueryResult qr = PersistenceHelper.manager.navigate(ecr,"part",EcrPartLink.class,false);
//					while(qr.hasMoreElements()){
//						EcrPartLink link = (EcrPartLink)qr.nextElement();
//						String version = link.getVersion();
//						WTPartMaster master = (WTPartMaster)link.getPart();
//						WTPart part = PartHelper.manager.getPart(master.getNumber(),version);
//						
//						HashMap mapPRe = new HashMap();
//						mapPRe = ERPECOHelper.manager.createdPDMEOITEM(part, ecr.getRequestNumber(), con);
//						
//						String linkResult =(String)mapPRe.get("result");
//						String linkmessage = (String)mapPRe.get("message");
//						
////						System.out.println(">>>>>>>>>>> linkResult :"+linkResult);
////						System.out.println(">>>>>>>>>>> linkmessage :"+linkmessage);
//						/*ERPPartLinkR CREATE*/
//						ERPPartLinkR erpLink = ERPPartLinkR.newERPPartLinkR(history, link);
//						
//						erpLink.setErp(history);
//						erpLink.setPart(link);
//						erpLink.setMessage(linkmessage);
//						
//						if(ERPUtil.HISTORY_STATE_SUCCESS.equals(linkResult)){//if(linkResult.equals(ERPUtil.HISTORY_STATE_SUCCESS)){
//							erpLink.setResult(ERPUtil.LINK_PART_RESULT_SUCCESS);
//						}else{
//							erpLink.setResult(ERPUtil.LINK_PART_RESULT_FAILE);
//							partSend = false;
//						}
//						PersistenceHelper.manager.save(erpLink);
//					}
//					
//					if(qr.size()>0) {
//						if(partSend) {
//							history.setPartSend(ERPUtil.PART_SEND_SUCCESS);
//							history.setState(ERPUtil.HISTORY_STATE_SUCCESS);
//						}else {
//							history.setPartSend(ERPUtil.PART_SEND_FAILE);
//							history.setState(ERPUtil.HISTORY_STATE_FAILE);
//						}
//					}else{
//						history.setPartSend(ERPUtil.PART_SEND_NO);
					history.setState(ERPUtil.HISTORY_STATE_SUCCESS);
//					}
				} else {
//					history.setPartSend(ERPUtil.PART_SEND_FAILE);
					history.setState(ERPUtil.HISTORY_STATE_FAILE);
				}

				history.setPartSend(ERPUtil.PART_SEND_NO);
				history.setBomSend(ERPUtil.BOM_SEND_NO);
				history = (ERPHistory) PersistenceHelper.manager.modify(history);

			} else if (sendType.equals(ERPUtil.HISTORY_TYPE_COMPLETE)) { // 완료됨

				int totalCount = 0;
				int partFaileCount = 0;
				int bomFaileCount = 0;
				int bomCount = 0;

				/* ECR Check */
				boolean isECR = ERPSearchHelper.manager.duplicationECR(ecr.getRequestNumber());
				/* PDMECO UPdate */
				String result = "";
				String message = "";
				if (isECR) {
					mapRe = this.erpECRUpdate(ecr, con);
					result = (String) mapRe.get("result");
					message = (String) mapRe.get("message");
				} else {
					result = ERPUtil.HISTORY_STATE_FAILE;
					message = "승인된 ECR 정보가 존재하지 않습니다.";
					// return;
				}

				history.setHistoryType(ERPUtil.HISTORY_TYPE_COMPLETE);
				history.setEoType(ERPUtil.HISTORY_ECR_TYPE);
				history.setMessage(message);

				if (result.equals(ERPUtil.HISTORY_STATE_SUCCESS)) {
					history.setEcrSend(ERPUtil.ECR_SEND_SUCCESS);
				} else {
					history.setEcrSend(ERPUtil.ECR_SEND_FAILE);
				}

				history.setPartSend(ERPUtil.PART_SEND_NO);
				history.setBomSend(ERPUtil.BOM_SEND_NO);

				if (partFaileCount == 0 && bomFaileCount == 0) {
					history.setState(ERPUtil.HISTORY_STATE_SUCCESS);
				} else {
					history.setState(ERPUtil.HISTORY_STATE_FAILE);
				}

				history.setPdfSend(ERPUtil.PDF_SEND_NO);
				PersistenceHelper.manager.modify(history);

			} // else if(sendType.equals(ERPUtil.HISTORY_TYPE_COMPLETE)){
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (dbmanager != null) {
				dbmanager.freeConnection(ERPUtil.ERP, con);
			}
		}
	}

	public HashMap erpECRConfirmECR(ERPHistory history) {
		DBConnectionManager dbmanager = null;
		Connection con = null;
		HashMap mapRe = new HashMap();
		try {
			EChangeRequest2 ecr = (EChangeRequest2) history.getEo().getObject();
			dbmanager = DBConnectionManager.getInstance();
			con = dbmanager.getConnection(ERPUtil.ERP);
			mapRe = erpECRSend(ecr, con);

			String result = (String) mapRe.get("result");
			String message = (String) mapRe.get("message");

			if (result.equals(ERPUtil.HISTORY_STATE_SUCCESS)) {
				history.setEcrSend(ERPUtil.ECR_SEND_SUCCESS);
				history.setMessage(message);
			} else {
				history.setMessage(message);
			}

			PersistenceHelper.manager.modify(history);

		} catch (Exception ex) {

			ex.printStackTrace();

		} finally {
			if (dbmanager != null) {
				dbmanager.freeConnection(ERPUtil.ERP, con);
			}
		}

		return mapRe;
	}

	public HashMap erpECRCompleteECR(ERPHistory history) {
		DBConnectionManager dbmanager = null;
		Connection con = null;
		HashMap mapRe = new HashMap();
		try {
			EChangeRequest2 ecr = (EChangeRequest2) history.getEo().getObject();
			dbmanager = DBConnectionManager.getInstance();
			con = dbmanager.getConnection(ERPUtil.ERP);
			mapRe = this.erpECRUpdate(ecr, con);

			String result = (String) mapRe.get("result");
			String message = (String) mapRe.get("message");

			if (result.equals(ERPUtil.HISTORY_STATE_SUCCESS)) {
				history.setEcrSend(ERPUtil.ECR_SEND_SUCCESS);
				history.setMessage(message);
			} else {
				history.setMessage(message);
			}

			PersistenceHelper.manager.modify(history);

		} catch (Exception ex) {

			ex.printStackTrace();

		} finally {
			if (dbmanager != null) {
				dbmanager.freeConnection(ERPUtil.ERP, con);
			}
		}

		return mapRe;
	}

	public HashMap erpECRConfirmPART(ERPHistory history) {
		DBConnectionManager dbmanager = null;
		Connection con = null;
		QueryResult qr = null;
		boolean partSend = true;

		HashMap mapRe = new HashMap();
		try {
			EChangeRequest2 ecr = (EChangeRequest2) history.getEo().getObject();
			String ecrNumber = ecr.getRequestNumber();
			dbmanager = DBConnectionManager.getInstance();
			con = dbmanager.getConnection(ERPUtil.ERP);

			qr = PersistenceHelper.manager.navigate(history, "part", ERPPartLinkR.class, false);

			if (qr.size() > 0) {

				while (qr.hasMoreElements()) {
					ERPPartLinkR linkR = (ERPPartLinkR) qr.nextElement();

					if (linkR.getResult().equals(ERPUtil.PART_SEND_SUCCESS))
						continue;

					EcrPartLink link = linkR.getPart();
					String version = link.getVersion();
					WTPartMaster master = (WTPartMaster) link.getPart();
					WTPart part = PartHelper.manager.getPart(master.getNumber(), version);
					/* ERP 전송 */
					mapRe = ERPECOHelper.manager.createdPDMEOITEM(part, ecrNumber, con);

					String result = (String) mapRe.get("result");
					String message = (String) mapRe.get("message");

					if (result.equals(ERPUtil.HISTORY_STATE_SUCCESS)) {
						linkR.setResult(ERPUtil.PART_SEND_SUCCESS);
						linkR.setMessage(message);
					} else {
						linkR.setResult(ERPUtil.PART_SEND_FAILE);
						linkR.setMessage(message);
						partSend = false;
					}
					PersistenceHelper.manager.modify(linkR);
				}

			} else {
				qr = PersistenceHelper.manager.navigate(ecr, "part", EcrPartLink.class, false);
				while (qr.hasMoreElements()) {
					EcrPartLink link = (EcrPartLink) qr.nextElement();
					String version = link.getVersion();
					WTPartMaster master = (WTPartMaster) link.getPart();
					WTPart part = PartHelper.manager.getPart(master.getNumber(), version);
					/* ERP 전송 */
					mapRe = ERPECOHelper.manager.createdPDMEOITEM(part, ecrNumber, con);
					String result = (String) mapRe.get("result");
					String message = (String) mapRe.get("message");
					ERPPartLinkR linkR = ERPPartLinkR.newERPPartLinkR(history, link);

					if (result.equals(ERPUtil.HISTORY_STATE_SUCCESS)) {
						linkR.setResult(ERPUtil.PART_SEND_SUCCESS);
						linkR.setMessage(message);
					} else {
						linkR.setResult(ERPUtil.PART_SEND_FAILE);
						linkR.setMessage(message);
						partSend = false;
					}
					PersistenceHelper.manager.save(linkR);
				}
			}

			if (partSend) {
				history.setState(ERPUtil.HISTORY_STATE_SUCCESS);
				history.setPartSend(ERPUtil.PART_SEND_SUCCESS);
				PersistenceHelper.manager.modify(history);

				mapRe.put("result", ERPUtil.HISTORY_STATE_SUCCESS);
				mapRe.put("message", "성공");

			} else {
				mapRe.put("result", ERPUtil.HISTORY_STATE_FAILE);
				mapRe.put("message", "ERP 전송시 에러 발생");
			}

		} catch (Exception ex) {
			mapRe.put("result", ERPUtil.HISTORY_STATE_FAILE);
			mapRe.put("message", ex.getLocalizedMessage());
			ex.printStackTrace();

		} finally {
			if (dbmanager != null) {
				dbmanager.freeConnection(ERPUtil.ERP, con);
			}
		}

		return mapRe;
	}

	public HashMap erpECRSend(EChangeRequest2 ecr, Connection con) throws Exception {

		HashMap mapRe = new HashMap();

		try {
			StringBuffer sql = new StringBuffer();
			sql.append("INSERT INTO PDMECR ").append(
					"(PrjNo, PrjSeqNo, UnitCode, EcrNo, Title, Type, State, ConfirmDate, Worker, WorkDate, rqepcode)")
					.append(" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

			PreparedStatement st = con.prepareStatement(sql.toString());

			st.setString(1, ecr.getPrjNo());
			String prjSeqNo = ecr.getPrjSeqNo().substring(ecr.getPrjSeqNo().indexOf("-") + 1,
					ecr.getPrjSeqNo().length());
			st.setString(2, prjSeqNo);
			st.setString(3, ecr.getUnitCode());
			st.setString(4, ecr.getRequestNumber());
			st.setString(5, ecr.getName());

			String purposeValue = "";
			String purpose = ecr.getPurpose();
			StringTokenizer tokens = new StringTokenizer(purpose, ",");

			while (tokens.hasMoreTokens()) {
				String pp = (String) tokens.nextToken();

				purposeValue += CodeHelper.manager.getName("CHANGEPURPOSE", pp) + ",";
			}
			st.setString(6, purposeValue);

			ApprovalMaster master = ApprovalHelper.manager.getApprovalMaster(ecr);
			st.setString(7, master.getLocaleState());
			st.setString(8, "");
			st.setString(9, ecr.getOwner().getFullName());
			st.setString(10, DateUtil.getTimeFormat(ecr.getPersistInfo().getCreateStamp(), ERPUtil.Dateformat));
			st.setString(11, ecr.getRealCreator());
			boolean re = st.execute();

			mapRe.put("result", ERPUtil.HISTORY_STATE_SUCCESS);
			mapRe.put("message", "성공");

		} catch (Exception ex) {
			mapRe.put("result", ERPUtil.HISTORY_STATE_FAILE);
			mapRe.put("message", ex.getMessage());
			ex.printStackTrace();
		}

		return mapRe;
	}

	public HashMap erpECRPartSend(EChangeRequest2 ecr, EcrPartLink partLink) throws Exception {

		DBConnectionManager dbmanager = null;
		Connection con = null;
		HashMap mapRe = new HashMap();

		try {
			dbmanager = DBConnectionManager.getInstance();
			con = dbmanager.getConnection(ERPUtil.ERP);

			StringBuffer sql = new StringBuffer();
			sql.append("INSERT INTO PDMEOITEM ").append("(Div, EONo, ItemCode, ItemVer)")
					.append(" VALUES (?, ?, ?, ?)");

			PreparedStatement st = con.prepareStatement(sql.toString());

			st.setString(1, ecr.getRequestNumber());
			st.setString(2, ecr.getRequestNumber());
			st.setString(3, partLink.getPart().getNumber());
			st.setString(4, partLink.getVersion());
			boolean re = st.execute();

			mapRe.put("return", Boolean.valueOf(re).booleanValue());
			mapRe.put("message", "");

		} catch (Exception ex) {
			mapRe.put("return", "false");
			mapRe.put("message", ex.getLocalizedMessage());
			ex.printStackTrace();
		} finally {
			if (dbmanager != null) {
				dbmanager.freeConnection(ERPUtil.ERP, con);
			}
		}

		return mapRe;
	}

	public HashMap erpECRUpdate(EChangeRequest2 ecr, Connection con) throws Exception {

		HashMap mapRe = new HashMap();

		try {
			StringBuffer sql = new StringBuffer();
			sql.append("UPDATE PDMECR ").append("set state = ?, ConfirmDate = ? ").append("WHERE EcrNo = ?");

			PreparedStatement st = con.prepareStatement(sql.toString());

			ApprovalMaster master = ApprovalHelper.manager.getApprovalMaster(ecr);
			st.setString(1, master.getLocaleState());
			st.setString(2, DateUtil.getToDay(ERPUtil.Dateformat));
			st.setString(3, ecr.getRequestNumber());
			boolean re = st.execute();

			mapRe.put("result", ERPUtil.HISTORY_STATE_SUCCESS);
			mapRe.put("message", "성공");

		} catch (Exception ex) {
			mapRe.put("result", ERPUtil.HISTORY_STATE_FAILE);
			mapRe.put("message", ex.getMessage());
			ex.printStackTrace();
		}

		return mapRe;
	}

	public ERPHistory getERPHistory(EChangeRequest2 ecr) throws Exception {
		QuerySpec qs = new QuerySpec();
		int ii = qs.addClassList(ERPHistory.class, true);
		qs.appendWhere(new SearchCondition(ERPHistory.class, "thePersistInfo.markForDelete", "=",
				ecr.getPersistInfo().getObjectIdentifier().getId()), new int[] { ii });

		QueryResult result = PersistenceHelper.manager.find(qs);

		ERPHistory history = null;
		if (result.hasMoreElements()) {
			Object[] o = (Object[]) result.nextElement();
			history = (ERPHistory) o[0];
		}
		return history;
	}

//	public HashMap erpPartSend(EcoPartLink link,ERPHistory erp){
//		
//		HashMap mapRe = new HashMap();
//	
//		return mapRe;
//	}
//	
//	public HashMap erpPartSend(WTPart part,Connection con){
//		
//		boolean result = false;
//		HashMap mapRe = new HashMap();
//		try{
//			
//			PartData data = new PartData(part);
//			StringBuffer sql = new StringBuffer();
//			sql.append("INSERT INTO PDM00 ")
//			.append("(PDMWorkTime,ItemCode,ItemVer,ItemName,Spec,BaseUnit,Maker)")
//			.append(" VALUES (?,?,?,?,?,?,?)");
//			
//			PreparedStatement st = con.prepareStatement(sql.toString());
//			
//			st.setString(1, DateUtil.getToDay(Dateformatfull).substring(0,DateUtil.getToDay(Dateformatfull).length()-1));				
//			st.setString(2, data.getNumber());
//			st.setString(3, data.getVersion()+"."+data.getIteration());
//			st.setString(4, data.getName());
//			st.setString(4, data.getSpec());
//			st.setString(4, data.getUnit());
//			st.setString(4, data.getMaker());
//			boolean re =st.execute();
//			
//			mapRe.put("return", re);
//			mapRe.put("message", "");
//			
//		}catch(Exception e){
//			mapRe.put("return", "F");
//			mapRe.put("message", e.getLocalizedMessage());
//			e.printStackTrace();
//		}
//		
//		return mapRe;
//		
//	}
//	
//	public HashMap erpECOConfirmSend(EChangeOrder2 eco,Connection con){
//		
//		HashMap mapRe = new HashMap();
//		
//		try{
//			
//			StringBuffer sql = new StringBuffer();
//			String ecrNumber = this.getRequestOrderList(eco);
//			
//			String purposeValue ="";
//			String purpose = eco.getPurpose();
//			StringTokenizer tokens = new StringTokenizer(purpose,",");
//			
//			while(tokens.hasMoreTokens()){
//				String pp = (String)tokens.nextToken();
//		
//				purposeValue +=CodeHelper.manager.getName("CHANGEPURPOSE",pp)+",";
//			}
//			
//			String stockPartValue ="";
//			String stockPart = eco.getStockPart(); //stockpart
//			tokens = new StringTokenizer(stockPart,",");
//			while(tokens.hasMoreTokens()){
//				String pp = (String)tokens.nextToken();
//		
//				stockPartValue = CodeHelper.manager.getName("STOCKMANAGEMENT",pp)+",";
//			}
//			
//			sql.append("INSERT INTO PDMECO ")
//			.append("(EcoNo,EcrNo,Title,Div,Type,StockDiv,ApplyDate,WorkDate,State,ConfirmDate,EndDate)")
//			.append(" VALUES (?,?,?,?,?,?,?)");
//			
//			PreparedStatement st = con.prepareStatement(sql.toString());
//			
//			
//			
//			st.setString(1, eco.getOrderNumber());				
//			st.setString(2, ecrNumber);
//			st.setString(3, eco.getName());
//			st.setString(4, CodeHelper.manager.getName("EOTYPE",eco.getEcoType()));
//			st.setString(4, purposeValue);
//			st.setString(4, stockPartValue);
//			st.setString(4, eco.getMajorLevel());
//			st.setString(4, DateUtil.getDateString(eco.getPersistInfo().getCreateStamp(), "d") );
//			st.setString(4, "작업중");
//			st.setString(4, "");
//			st.setString(4, DateUtil.getToDay(Dateformat));
//			boolean re =st.execute();
//			
//			mapRe.put("return", re);
//			mapRe.put("message", "");
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		
//		return mapRe;
//		
//	}
//	
//	
//	public String getRequestOrderList(EChangeOrder2 eco){
//		
//		String ecrList ="";
//		try{
//			 QueryResult rt = ChangeHelper.manager.getRequestOrderLink(eco);
//			 
//			 while(rt.hasMoreElements()){
//				 EChangeRequest2 ecr = (EChangeRequest2)rt.nextElement();
//				 
//				 ecrList += ","+ecr.getRequestNumber();
//			 }
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		
//		
//		return "";
//	}
//	
//	public HashMap erpEcoCompleteSend(EChangeOrder2 eco){
//		
//		HashMap mapRe = new HashMap();
//		try{
//			
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		
//		return mapRe;
//	}

}
