package ext.narae.service.erp.beans;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

import com.ptc.wvs.server.util.PublishUtils;

import ext.narae.service.change.EChangeOrder2;
import ext.narae.service.change.EChangeRequest2;
import ext.narae.service.change.EOEul;
import ext.narae.service.change.EcoPartLink;
import ext.narae.service.change.beans.ChangeECOHelper;
import ext.narae.service.drawing.beans.DrawingHelper;
import ext.narae.service.drawing.beans.EpmPublishUtil;
import ext.narae.service.drawing.beans.EpmSearchHelper;
import ext.narae.service.erp.EPMPDFLink;
import ext.narae.service.erp.ERPBOMPARTLink;
import ext.narae.service.erp.ERPHistory;
import ext.narae.service.erp.ERPPartLinkO;
import ext.narae.service.org.beans.UserHelper;
import ext.narae.service.part.beans.PartAttributeHelper;
import ext.narae.service.part.beans.PartData;
import ext.narae.service.part.beans.PartHelper;
import ext.narae.service.part.beans.PartSearchHelper;
import ext.narae.util.CommonUtil;
import ext.narae.util.DateUtil;
import ext.narae.util.code.beans.CodeHelper;
import ext.narae.util.content.FileDown;
import ext.narae.util.db.DBConnectionManager;
import ext.narae.util.iba.IBAUtil;
import ext.narae.util.jdf.config.ConfigEx;
import ext.narae.util.jdf.config.ConfigExImpl;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.lifecycle.State;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartConfigSpec;
import wt.part.WTPartMaster;
import wt.part.WTPartStandardConfigSpec;
import wt.part.WTPartUsageLink;
import wt.representation.Representation;
import wt.util.WTException;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;
import wt.vc.wip.WorkInProgressHelper;

public class ERPECOHelper {

	/**
	 * @param args
	 */
	static final String ERP = ERPUtil.ERP;
	public static final String MASTER_WORKING = "승인됨";
	public static final String MASTER_COMPLETE = "완료됨";
	public static final String MASTER_REJECT = "반려됨";
	public static ERPECOHelper manager = new ERPECOHelper();
	public static Connection con = null;
	public static ConfigExImpl conf = ConfigEx.getInstance("eSolution");
	public static final boolean enableERP = conf.getBoolean("erp.send", true);
	public static ERPECOSendLog ecolog = new ERPECOSendLog();

	public void erpECO(EChangeOrder2 eco, String sendType, ERPHistory history) {

		if (sendType.equals(ERPUtil.HISTORY_TYPE_COMPLETE)) {
			this.erpECOComplete(eco, history);
		} else {
			this.erpECOConfirm(eco, history);
		}
	}

	public void erpECOConfirm(EChangeOrder2 eco, ERPHistory history) {

		DBConnectionManager dbmanager = null;

		HashMap mapRe = new HashMap();
		boolean partSend = true;
		try {
			dbmanager = DBConnectionManager.getInstance();
			con = dbmanager.getConnection(ERP);

			/* ECO SEND */
			mapRe = this.createPDMECO(eco);
			String result = (String) mapRe.get("result");
			String message = (String) mapRe.get("message");

			/* ERPHistory update */
			history.setHistoryType(ERPUtil.HISTORY_TYPE_CONFIRM);
			history.setEoType(ERPUtil.HISTORY_ECO_TYPE);
			history.setMessage(message);

			if (result.equals(ERPUtil.HISTORY_STATE_SUCCESS)) {
				history.setEcoSend(ERPUtil.ECO_SEND_SUCCESS);
			} else {
				history.setEcoSend(ERPUtil.ECO_SEND_FAILE);
			}

			PersistenceHelper.manager.modify(history);
			if (result.equals(ERPUtil.HISTORY_STATE_SUCCESS)) { // 승인됨
				/* PART SEND */
				QueryResult qr = ChangeECOHelper.manager.getEcoPartLink(eco);

				while (qr.hasMoreElements()) {

					EcoPartLink link = (EcoPartLink) qr.nextElement();
					String version = link.getVersion();
					WTPartMaster master = (WTPartMaster) link.getPart();
					WTPart part = PartHelper.manager.getPart(master.getNumber(), version);

					HashMap mapPRe = new HashMap();
					mapPRe = this.createdPDMEOITEM(part, eco.getOrderNumber(), con);

					String linkResult = (String) mapPRe.get("result");
					String linkmessage = (String) mapPRe.get("message");

					/* ERPPartLinkO CREATE */
					ERPPartLinkO erpLink = ERPPartLinkO.newERPPartLinkO(history, link);

					erpLink.setHistory(history);
					erpLink.setMessage(linkmessage);
					erpLink.setNewPart(part);

					if (ERPUtil.HISTORY_STATE_SUCCESS.equals(linkResult)) {// if(linkResult.equals(ERPUtil.HISTORY_STATE_SUCCESS)){
						erpLink.setResult(ERPUtil.LINK_PART_RESULT_SUCCESS);
					} else {
						erpLink.setResult(ERPUtil.LINK_PART_RESULT_FAILE);
						partSend = false;
					}
					PersistenceHelper.manager.save(erpLink);
				}

				if (qr.size() > 0) {
					if (partSend) {
						history.setPartSend(ERPUtil.PART_SEND_SUCCESS);
						history.setState(ERPUtil.HISTORY_STATE_SUCCESS);
					} else {
						history.setPartSend(ERPUtil.PART_SEND_FAILE);
						history.setState(ERPUtil.HISTORY_STATE_FAILE);
					}
				} else {
					history.setPartSend(ERPUtil.PART_SEND_NO);
					history.setState(ERPUtil.HISTORY_STATE_SUCCESS);
				}
			} else {
				history.setPartSend(ERPUtil.PART_SEND_FAILE);
				history.setState(ERPUtil.HISTORY_STATE_FAILE);
			}

			history.setBomSend(ERPUtil.BOM_SEND_NO);
			history.setPdfSend(ERPUtil.PDF_SEND_NO);
			history = (ERPHistory) PersistenceHelper.manager.modify(history);

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (dbmanager != null) {
				dbmanager.freeConnection(ERP, con);
			}
		}
	}

	public void erpECOComplete(EChangeOrder2 eco, ERPHistory history) {

		DBConnectionManager dbmanager = null;

		HashMap mapRe = new HashMap();
		boolean partSend = true;
		try {
			dbmanager = DBConnectionManager.getInstance();
			con = dbmanager.getConnection(ERP);

			/* ECO Check */
			String process = eco.getProcess();

			String result = "";
			String message = "";

			if (process.equals(ChangeECOHelper.ECR_EXIST)) {
				boolean isECO = ERPSearchHelper.manager.duplicationECO(eco.getOrderNumber());
				/* PDMECO UPdate */
				if (isECO) {
					mapRe = this.upDatePDMECO(eco);
					result = (String) mapRe.get("result");
					message = (String) mapRe.get("message");
				} else {
					result = ERPUtil.HISTORY_STATE_FAILE;
					message = "승인된 ECO 정보가 존재 하지 않습니다.";
					// return;
				}
			} else {
				/* PDMECO Create */
				mapRe = this.createPDMECO(eco);
				result = (String) mapRe.get("result");
				message = (String) mapRe.get("message");
			}

			history.setHistoryType(ERPUtil.HISTORY_TYPE_COMPLETE);
			history.setEoType(ERPUtil.HISTORY_ECO_TYPE);
			history.setMessage(message);

			if (result.equals(ERPUtil.HISTORY_STATE_SUCCESS)) {
				history.setEcoSend(ERPUtil.ECO_SEND_SUCCESS);
			} else {
				history.setEcoSend(ERPUtil.ECO_SEND_FAILE);
			}

			ecolog.infoLog(">>>>>>>>>>>>> ERP SEND : " + eco.getOrderNumber());
			// 설변 품목
			QueryResult qr = ChangeECOHelper.manager.searchECOPartLink(eco, ChangeECOHelper.PART_TYPE_PART);

			boolean bomApprove = true;

			if (qr.size() > 0)
				bomApprove = false;
//			System.out.println("*******qr.size*******"+qr.size());

			boolean isTotalPdf = false;
			while (qr.hasMoreElements()) {

				EcoPartLink link = (EcoPartLink) qr.nextElement();
				String partType = link.getPartType();

				WTPartMaster master = (WTPartMaster) link.getPart();
				WTPart part = PartSearchHelper.getLastWTPart(master);
				/* PART APPROVED PDF,ERP_SEND */
				ChangeECOHelper.manager.commitPartStateChange(history, part);
				part = (WTPart) PersistenceHelper.manager.refresh(part);
//				System.out.println("part1------->>>"+part);
//				System.out.println("part2------->>>"+IBAUtil.getAttrValue(part, "autoNumber"));

				if (part != null) {
					IBAUtil.deleteIBA(part, "autoNumber");
					IBAUtil.createIba(part, "string", "autoNumber", "TRUE");
					System.out.println("여기..");
				}

				part = (WTPart) PersistenceHelper.manager.refresh(part);
				System.out.println("===" + IBAUtil.getAttrValue(part, "autoNumber"));
				if (IBAUtil.getAttrValue(part, "autoNumber") != null
						&& "TRUE".equals(IBAUtil.getAttrValue(part, "autoNumber"))) {
					this.partSend(part, history);
				}

			} // while(qr.hasMoreElements())

			//
			Vector vecEul = ChangeECOHelper.manager.getEoEul(eco);
			Vector vecTop = new Vector();
			for (int i = 0; i < vecEul.size(); i++) {
				EOEul eul = (EOEul) vecEul.get(i);
				WTPart topPart = (WTPart) CommonUtil.getObject(eul.getTopAssyOid());
				vecTop.add(topPart);
			}

			// TOP 품목
			QueryResult qr2 = ChangeECOHelper.manager.searchECOPartLink(eco, ChangeECOHelper.PART_TYPE_TOP);
//			System.out.println("*******qr2.size*******"+qr2.size());
			while (qr2.hasMoreElements()) {

				EcoPartLink link = (EcoPartLink) qr2.nextElement();
				String partType = link.getPartType();

				WTPartMaster master = (WTPartMaster) link.getPart();
				WTPart part = PartSearchHelper.getLastWTPart(master);
				if (!bomApprove) { //
					/* PART APPROVED PDF,ERP_SEND */
					ChangeECOHelper.manager.commitPartStateChange(history, part);
					part = (WTPart) PersistenceHelper.manager.refresh(part);

					if (part != null) {
						IBAUtil.deleteIBA(part, "autoNumber");
						IBAUtil.createIba(part, "string", "autoNumber", "TRUE");
					}

					part = (WTPart) PersistenceHelper.manager.refresh(part);

					if (IBAUtil.getAttrValue(part, "autoNumber") != null
							&& "TRUE".equals(IBAUtil.getAttrValue(part, "autoNumber"))) {
						this.partSend(part, history);
					}
				}

				if (part != null) {
					IBAUtil.deleteIBA(part, "autoNumber");
					IBAUtil.createIba(part, "string", "autoNumber", "TRUE");
				}

				part = (WTPart) PersistenceHelper.manager.refresh(part);

				if (IBAUtil.getAttrValue(part, "autoNumber") != null
						&& "TRUE".equals(IBAUtil.getAttrValue(part, "autoNumber"))) {
					Vector tempBom = this.getBom(part, bomApprove, history);

					if (tempBom.size() > 0) {
						/* BOM SEND */
						ERPBOMPARTLink bomLink = ERPBOMPARTLink.newERPBOMPARTLink(history, part);

						HashMap mapBom = this.sendPDMBOM(tempBom, eco.getApplyDate(), eco.getOrderNumber());
						result = (String) mapBom.get("result");
						message = (String) mapBom.get("message");

						bomLink.setResult(result);
						bomLink.setMessage(message);

						PersistenceHelper.manager.save(bomLink);
					}
				}

				/* Baseline Create */

				if (!vecTop.contains(part)) {

					EOEul eul = EOEul.newEOEul();
					eul.setEco(eco);
					eul.setTopAssyOid(CommonUtil.getOIDString(part));
					WTUser user = UserHelper.getWTUser("Administrator");

					eul = (EOEul) PersistenceHelper.manager.save(eul);
					ChangeECOHelper.manager.createBaseline(part, eul);
				}

			}

			/* HISTORT RESULT */
			this.setHistoryResult(history);

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (dbmanager != null) {
				dbmanager.freeConnection(ERP, con);
			}
		}
	}

	public void setHistoryResult(ERPHistory history) {

		/* HISTORT RESULT */
		try {
			boolean isPart = true;
			boolean isBom = true;
			boolean isPdf = true;
			QueryResult qrPart = ERPSearchHelper.manager.searchPartHistory(history);
//			System.out.println(":::::::::::::::::::::::::::::: HISTORT RESULT ::::::::::::::::::::::::::::::");
//			System.out.println(":::::::::::::::: qrPart :"+ qrPart.size());
			if (qrPart.size() > 0) {

				while (qrPart.hasMoreElements()) {
					ERPPartLinkO linkO = (ERPPartLinkO) qrPart.nextElement();
					if (linkO.getResult().equals(ERPUtil.PART_SEND_FAILE)) {
						isPart = false;
						break;
					}
				}
				if (isPart) {
					history.setPartSend(ERPUtil.PART_SEND_SUCCESS);
				} else {
					history.setPartSend(ERPUtil.PART_SEND_FAILE);
				}
			} else {
				history.setPartSend(ERPUtil.PART_SEND_NO);
			}

			QueryResult qrBom = PersistenceHelper.manager.navigate(history, "bomPart", ERPBOMPARTLink.class, false);

//			System.out.println(":::::::::::::::: qrBom :"+ qrBom.size());
			if (qrBom.size() > 0) {

				while (qrBom.hasMoreElements()) {
					ERPBOMPARTLink linkO = (ERPBOMPARTLink) qrBom.nextElement();
					if (linkO.getResult().equals(ERPUtil.BOM_SEND_FAILE)) {
						isBom = false;
						break;
					}
				}
				if (isBom) {
					history.setBomSend(ERPUtil.BOM_SEND_SUCCESS);
				} else {
					history.setBomSend(ERPUtil.BOM_SEND_FAILE);
				}
			} else {
				history.setBomSend(ERPUtil.BOM_SEND_NO);
			}

			QueryResult qrPdf = PersistenceHelper.manager.navigate(history, "epm", EPMPDFLink.class, false);
//			System.out.println(":::::::::::::::: qrPdf :"+ qrPdf.size());

			if (qrPdf.size() > 0) {
				boolean isPdfW = false;
				while (qrPdf.hasMoreElements()) {
					EPMPDFLink linkO = (EPMPDFLink) qrPdf.nextElement();
					if (linkO.getResult().equals(ERPUtil.PDF_SEND_FAILE)) {
						isPdf = false;
					} else if (linkO.getResult().equals(ERPUtil.PDF_SEND_WAITING)) {
						isPdfW = true;
					}

				}
//				System.out.println(":::::::::::::::: qrPdf :"+ qrPdf.size());
				if (isPdf) {
					if (isPdfW) {
						history.setPdfSend(ERPUtil.PDF_SEND_WAITING);
					} else {
						history.setPdfSend(ERPUtil.PDF_SEND_SUCCESS);
					}

				} else {
					history.setPdfSend(ERPUtil.PDF_SEND_FAILE);
				}
			} else {
				history.setPdfSend(ERPUtil.PDF_SEND_NO);
			}
//			System.out.println(":::::::::::::::: isPart :"+ isPart);
//			System.out.println(":::::::::::::::: isBom :"+ isBom);
//			System.out.println(":::::::::::::::: isPdf :"+ isPdf);
			if (isPart && isBom && isPdf) {
				history.setState(ERPUtil.HISTORY_STATE_SUCCESS);
			} else {
				history.setState(ERPUtil.HISTORY_STATE_FAILE);
			}

			PersistenceHelper.manager.modify(history);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void partSend(WTPart part, ERPHistory history) {

		try {

			/* PART SEND */
			HashMap mapPRe = new HashMap();
			EChangeOrder2 eco = (EChangeOrder2) history.getEo().getObject();

			String ver = part.getVersionIdentifier().getSeries().getValue() + "."
					+ part.getIterationIdentifier().getSeries().getValue();
//			System.out.println("ver------->>>"+ver);

			if (!part.getLifeCycleState().toString().equals("APPROVED")) {
				mapPRe.put("result", ERPUtil.PART_SEND_FAILE);
				mapPRe.put("message", "승인되지 않은 품목은 전송할 수 없습니다.");
				mapPRe.put("pdfresult", ERPUtil.PDF_SEND_FAILE);
				mapPRe.put("pdfmessage", "품목이 전송되지 않습니다.");
				mapPRe.put("folderPath", "");
			} else if (WorkInProgressHelper.isCheckedOut(part)) {
				mapPRe.put("result", ERPUtil.PART_SEND_FAILE);
				mapPRe.put("message", "Check out 품목은 전송할 수 없습니다.");
				mapPRe.put("pdfresult", ERPUtil.PDF_SEND_FAILE);
				mapPRe.put("pdfmessage", "품목이 전송되지 않습니다.");
				mapPRe.put("folderPath", "");
			} else if (IBAUtil.getAttrValue(part, "autoNumber") == null
					|| !"TRUE".equals(IBAUtil.getAttrValue(part, "autoNumber"))) {
				mapPRe.put("result", ERPUtil.PART_SEND_FAILE);
				mapPRe.put("message", "채번되지 않은 품목은 전송할 수 없습니다.");
				mapPRe.put("pdfresult", ERPUtil.PDF_SEND_FAILE);
				mapPRe.put("pdfmessage", "품목이 전송되지 않습니다.");
				mapPRe.put("folderPath", "");
			} else if (ERPSearchHelper.manager.duplicationPart(part.getNumber(), ver)) {
				mapPRe.put("result", ERPUtil.PART_SEND_SUCCESS);
				mapPRe.put("message", "이미 전송된 품목입니다.");
				mapPRe.put("pdfresult", ERPUtil.PDF_SEND_SUCCESS);
				mapPRe.put("pdfmessage", "이미 전송된 품목입니다.");
				mapPRe.put("folderPath", "");
			} else {
				mapPRe = this.createdPDM00(part, eco);
			}

			String linkResult = (String) mapPRe.get("result");
			String linkmessage = (String) mapPRe.get("message");

			/* ERPPartLinkO CREATE */
			ERPPartLinkO erpLink = ERPPartLinkO.newERPPartLinkO(history, part);

			erpLink.setHistory(history);
			erpLink.setResult(linkResult);
			erpLink.setMessage(linkmessage);
			erpLink.setNewPart(part);
			PersistenceHelper.manager.save(erpLink);

			EPMDocument epm = DrawingHelper.manager.getEPMDocument(part);
			if ("PDF".equals(epm.getAuthoringApplication().toString())) {
				EPMPDFLink pdf = EPMPDFLink.newEPMPDFLink(epm, history);
				pdf.setResult((String) mapPRe.get("pdfresult"));
				pdf.setMessage((String) mapPRe.get("pdfmessage"));
				String pdfFileName = part.getNumber() + "." + part.getVersionIdentifier().getSeries() + "."
						+ part.getIterationIdentifier().getSeries() + ".pdf";
				pdf.setFileName(pdfFileName);
				pdf.setFolder((String) mapPRe.get("folderPath"));
				PersistenceHelper.manager.save(pdf);
			}

		} catch (Exception e) {
//			System.out.println(":::::::::::::::::: ERP PART SEND ERROR ::::::::::::::::");
			e.printStackTrace();
		}

	}

	public HashMap erpECOConfirmECO(ERPHistory history) {
		DBConnectionManager dbmanager = null;

		HashMap mapRe = new HashMap();
		try {
			EChangeOrder2 eco = (EChangeOrder2) history.getEo().getObject();
			dbmanager = DBConnectionManager.getInstance();
			con = dbmanager.getConnection(ERP);
			mapRe = createPDMECO(eco);

			String result = (String) mapRe.get("result");
			String message = (String) mapRe.get("message");

			if (result.equals(ERPUtil.HISTORY_STATE_SUCCESS)) {
				history.setEcoSend(ERPUtil.ECO_SEND_SUCCESS);
				history.setMessage(message);
			} else {
				history.setMessage(message);
			}

			PersistenceHelper.manager.modify(history);

		} catch (Exception ex) {

			ex.printStackTrace();

		} finally {
			if (dbmanager != null) {
				dbmanager.freeConnection(ERP, con);
			}
		}

		return mapRe;
	}

	public HashMap erpECOCompleteECO(ERPHistory history) {
		DBConnectionManager dbmanager = null;

		HashMap mapRe = new HashMap();
		try {
			EChangeOrder2 eco = (EChangeOrder2) history.getEo().getObject();
			dbmanager = DBConnectionManager.getInstance();
			con = dbmanager.getConnection(ERP);
			mapRe = this.upDatePDMECO(eco);

			String result = (String) mapRe.get("result");
			String message = (String) mapRe.get("message");

			if (result.equals(ERPUtil.HISTORY_STATE_SUCCESS)) {
				history.setEcoSend(ERPUtil.ECO_SEND_SUCCESS);
				history.setMessage(message);
			} else {
				history.setMessage(message);
			}

			PersistenceHelper.manager.modify(history);

		} catch (Exception ex) {

			ex.printStackTrace();

		} finally {
			if (dbmanager != null) {
				dbmanager.freeConnection(ERP, con);
			}
		}

		return mapRe;
	}

	public HashMap erpECOConfirmPART(ERPHistory history) {
		DBConnectionManager dbmanager = null;
		Connection con = null;
		QueryResult qr = null;
		boolean partSend = true;

		HashMap mapRe = new HashMap();
		try {
			EChangeOrder2 eco = (EChangeOrder2) history.getEo().getObject();
			String ecoNumber = eco.getOrderNumber();
			dbmanager = DBConnectionManager.getInstance();
			con = dbmanager.getConnection(ERP);

			qr = PersistenceHelper.manager.navigate(history, "part", ERPPartLinkO.class, false);

			if (qr.size() > 0) {

				while (qr.hasMoreElements()) {
					ERPPartLinkO linkO = (ERPPartLinkO) qr.nextElement();

					if (linkO.getResult().equals(ERPUtil.PART_SEND_SUCCESS))
						continue;

					EcoPartLink link = null;// linkO.getPart();
					String version = link.getVersion();
					WTPartMaster master = (WTPartMaster) link.getPart();
					WTPart part = PartHelper.manager.getPart(master.getNumber(), version);
					/* ERP 전송 */
					mapRe = this.createdPDMEOITEM(part, ecoNumber, con);

					String result = (String) mapRe.get("result");
					String message = (String) mapRe.get("message");

					if (result.equals(ERPUtil.HISTORY_STATE_SUCCESS)) {
						linkO.setResult(ERPUtil.PART_SEND_SUCCESS);
						linkO.setMessage(message);
					} else {
						linkO.setResult(ERPUtil.PART_SEND_FAILE);
						linkO.setMessage(message);
						partSend = false;
					}
					linkO.setNewPart(part);
					PersistenceHelper.manager.modify(linkO);
				}

			} else {
				qr = PersistenceHelper.manager.navigate(eco, "part", EcoPartLink.class, false);
				while (qr.hasMoreElements()) {
					EcoPartLink link = (EcoPartLink) qr.nextElement();
					String version = link.getVersion();
					WTPartMaster master = (WTPartMaster) link.getPart();
					WTPart part = PartHelper.manager.getPart(master.getNumber(), version);
					/* ERP 전송 */
//					System.out.println(":::::::::::::: erpECOConfirmPART1 :::::::::::::::::");
					mapRe = this.createdPDMEOITEM(part, ecoNumber, con);
//					System.out.println(":::::::::::::: erpECOConfirmPART 2:::::::::::::::::");
					String result = (String) mapRe.get("result");
					String message = (String) mapRe.get("message");
//					System.out.println(">>>>>>>> result :" + result);
					ERPPartLinkO linkO = ERPPartLinkO.newERPPartLinkO(history, link);

					if (result.equals(ERPUtil.HISTORY_STATE_SUCCESS)) {
						linkO.setResult(ERPUtil.PART_SEND_SUCCESS);
						linkO.setMessage(message);
					} else {
						linkO.setResult(ERPUtil.PART_SEND_FAILE);
						linkO.setMessage(message);
						partSend = false;
					}
					linkO.setNewPart(part);
//					System.out.println(":::::::::::::: erpECOConfirmPART 3:::::::::::::::::");
					PersistenceHelper.manager.save(linkO);
//					System.out.println(":::::::::::::: erpECOConfirmPART 4:::::::::::::::::");
				}
			}

			if (partSend) {
				if (!ERPUtil.BOM_SEND_FAILE.equals(history.getBomSend())) {
					history.setState(ERPUtil.HISTORY_STATE_SUCCESS);
					history.setPartSend(ERPUtil.PART_SEND_SUCCESS);
					PersistenceHelper.manager.modify(history);
				}

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
				dbmanager.freeConnection(ERP, con);
			}
		}

		return mapRe;
	}

	public HashMap erpECOCompletePART(ERPHistory history) {
		DBConnectionManager dbmanager = null;

		QueryResult qr = null;
		boolean partSend = true;

		HashMap mapRe = new HashMap();
		try {
			EChangeOrder2 eco = (EChangeOrder2) history.getEo().getObject();
			String ecoNumber = eco.getOrderNumber();
			dbmanager = DBConnectionManager.getInstance();
			con = dbmanager.getConnection(ERP);

			qr = ERPSearchHelper.manager.searchPartHistory(history);

			if (qr.size() > 0) {

				while (qr.hasMoreElements()) {
					ERPPartLinkO linkO = (ERPPartLinkO) qr.nextElement();

					if (linkO.getResult().equals(ERPUtil.PART_SEND_SUCCESS))
						continue;

					WTPart part = linkO.getNewPart();
					ChangeECOHelper.manager.commitPartStateChange(history, part);

					/* ERP 전송 */
					String ver = part.getVersionIdentifier().getSeries().getValue() + "."
							+ part.getIterationIdentifier().getSeries().getValue();
					boolean isPart = ERPSearchHelper.manager.duplicationPart(part.getNumber(), ver);
					String result = "";
					String message = "";
					if (isPart) {
						result = ERPUtil.PART_SEND_SUCCESS;
						message = "등록된 코드가 있습니다.";
					} else {
						if (IBAUtil.getAttrValue(part, "autoNumber") != null
								&& "TRUE".equals(IBAUtil.getAttrValue(part, "autoNumber"))) {
							mapRe = this.createdPDM00(part, eco);
							result = (String) mapRe.get("result");
							message = (String) mapRe.get("message");
						}
					}

					if (result.equals(ERPUtil.HISTORY_STATE_SUCCESS)) {
						linkO.setResult(ERPUtil.PART_SEND_SUCCESS);
						linkO.setMessage(message);
					} else {
						linkO.setResult(ERPUtil.PART_SEND_FAILE);
						linkO.setMessage(message);
						partSend = false;
					}
					linkO.setNewPart(part);
					PersistenceHelper.manager.modify(linkO);
				}

			} else {
				// qr = PersistenceHelper.manager.navigate(eco,"part",EcoPartLink.class,false);
				qr = ChangeECOHelper.manager.searchECOPartLink(eco, ChangeECOHelper.PART_TYPE_PART);

				boolean isBomApproved = false;
				if (qr.size() == 0)
					isBomApproved = true;

				while (qr.hasMoreElements()) {
					EcoPartLink link = (EcoPartLink) qr.nextElement();
					String version = link.getVersion();
					WTPartMaster master = (WTPartMaster) link.getPart();
					WTPart part = PartSearchHelper.getLastWTPart(master);

					// WTPart part = PartHelper.manager.getPart(master.getNumber(),version);
					ChangeECOHelper.manager.commitPartStateChange(history, part);

					String ver = part.getVersionIdentifier().getSeries().getValue() + "."
							+ part.getIterationIdentifier().getSeries().getValue();
					boolean isPart = ERPSearchHelper.manager.duplicationPart(part.getNumber(), ver);
					String result = "";
					String message = "";
					if (isPart) {
						result = ERPUtil.PART_SEND_SUCCESS;
						message = "등록된 코드가 있습니다.";
					} else {
						if (IBAUtil.getAttrValue(part, "autoNumber") != null
								&& "TRUE".equals(IBAUtil.getAttrValue(part, "autoNumber"))) {
							mapRe = this.createdPDM00(part, eco);
							result = (String) mapRe.get("result");
							message = (String) mapRe.get("message");
						}
					}

					ERPPartLinkO linkO = ERPPartLinkO.newERPPartLinkO(history, link);

					if (result.equals(ERPUtil.HISTORY_STATE_SUCCESS)) {
						linkO.setResult(ERPUtil.PART_SEND_SUCCESS);
						linkO.setMessage(message);
					} else {
						linkO.setResult(ERPUtil.PART_SEND_FAILE);
						linkO.setMessage(message);
						partSend = false;
					}

					PersistenceHelper.manager.save(linkO);

				}

				qr = ChangeECOHelper.manager.searchECOPartLink(eco, ChangeECOHelper.PART_TYPE_TOP);
				while (qr.hasMoreElements()) {
					EcoPartLink link = (EcoPartLink) qr.nextElement();
					String version = link.getVersion();
					WTPartMaster master = (WTPartMaster) link.getPart();
					WTPart part = PartSearchHelper.getLastWTPart(master);

					// WTPart part = PartHelper.manager.getPart(master.getNumber(),version);
					ChangeECOHelper.manager.commitPartStateChange(history, part);
					if (isBomApproved)
						this.getBom(part, true, history);
				}
			}

			if (partSend) {
				/* HISTORT RESULT */
				setHistoryResult(history);

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
				dbmanager.freeConnection(ERP, con);
			}
		}

		return mapRe;
	}

	public HashMap erpECOCompleteBOM(ERPHistory history) {

		DBConnectionManager dbmanager = null;
		Connection con = null;
		QueryResult qr = null;

		HashMap map = new HashMap();
		int bomCount = 0;
		int bomFaileCount = 0;
		String result = "";
		String message = "";
		try {

			EChangeOrder2 eco = (EChangeOrder2) history.getEo().getObject();

			qr = PersistenceHelper.manager.navigate(history, "bomPart", ERPBOMPARTLink.class, false);
			if (qr.size() > 0) {
				while (qr.hasMoreElements()) {
					ERPBOMPARTLink link = (ERPBOMPARTLink) qr.nextElement();
					WTPart part = link.getBomPart();

					Vector tempBom = this.getBom(part, false, history);
					if (tempBom.size() > 0) {
						bomCount++;
						HashMap mapBom = this.sendPDMBOM(tempBom, eco.getApplyDate(), eco.getOrderNumber());
						result = (String) mapBom.get("result");
						message = (String) mapBom.get("message");
						if (ERPUtil.BOM_SEND_FAILE.equals(result))
							bomFaileCount++;

					} else {
						result = ERPUtil.BOM_SEND_NO;
						message = "전송할 BOM이 없습니다.";
					}

					link.setResult(result);
					link.setMessage(message);
					PersistenceHelper.manager.modify(link);
				}
			} else {
				qr = ChangeECOHelper.manager.searchECOPartLink(eco, ChangeECOHelper.PART_TYPE_TOP);
				while (qr.hasMoreElements()) {

					EcoPartLink link = (EcoPartLink) qr.nextElement();
					String version = link.getVersion();
					WTPartMaster master = (WTPartMaster) link.getPart();
					WTPart part = PartSearchHelper.getLastWTPart(master);
					ERPBOMPARTLink linkbom = ERPBOMPARTLink.newERPBOMPARTLink(history, part);

					Vector tempBom = this.getBom(part, false, history);
					if (tempBom.size() > 0) {
						bomCount++;
						HashMap mapBom = this.sendPDMBOM(tempBom, eco.getApplyDate(), eco.getOrderNumber());
						result = (String) mapBom.get("result");
						message = (String) mapBom.get("message");
						if (ERPUtil.BOM_SEND_FAILE.equals(result))
							bomFaileCount++;

					} else {
						result = ERPUtil.BOM_SEND_NO;
						message = "전송할 BOM이 없습니다.";
					}
					linkbom.setResult(result);
					linkbom.setMessage(message);
					PersistenceHelper.manager.save(linkbom);
				}
			}

			/* HISTORT RESULT */
			setHistoryResult(history);

		} catch (Exception ex) {
			map.put("result", ERPUtil.HISTORY_STATE_FAILE);
			map.put("message", ex.getLocalizedMessage());
			ex.printStackTrace();

		} finally {
			if (dbmanager != null) {
				dbmanager.freeConnection(ERP, con);
			}
		}

		return map;
	}

	public HashMap erpECOCompletePDF(ERPHistory history) {

		DBConnectionManager dbmanager = null;

		QueryResult qr = null;
		HashMap map = new HashMap();
		try {
			dbmanager = DBConnectionManager.getInstance();
			con = dbmanager.getConnection(ERP);
			EChangeOrder2 eco = (EChangeOrder2) history.getEo().getObject();
			qr = PersistenceHelper.manager.navigate(history, "epm", EPMPDFLink.class, false);
			boolean isPdf = true;
			String errMessage = "PDF 전송 성공";
			if (qr.size() > 0) {
				while (qr.hasMoreElements()) {
//					System.out.println("::::::::::::::::::::: PDF 전송 1:::::::::::::::::::");
					EPMPDFLink link = (EPMPDFLink) qr.nextElement();
					if (link.getResult().equals(ERPUtil.PDF_SEND_SUCCESS)
							|| link.getResult().equals(ERPUtil.PDF_SEND_NO))
						continue;

					EPMDocument epm = link.getEpm();
					if ("PROE".equals(epm.getAuthoringApplication().toString())) {
						EpmPublishUtil.publish(epm);
					} else {
						EPMDocumentMaster master = EpmSearchHelper.manager.getEPM3D(epm);
						EPMDocument epm3D = EpmSearchHelper.manager.getLastEPMDocument(master);
						WTPart part = DrawingHelper.manager.getWTPart(epm3D);
						// PDF ERP SEND
						HashMap mapPDF = this.sendPDF(part, true);

						// PDM00 UpDate
						HashMap mapRe = this.updatePDM00(part, mapPDF);
						String result = (String) mapRe.get("result");
						String message = (String) mapRe.get("message");

						if (result.equals(ERPUtil.PDF_SEND_FAILE)) {
							isPdf = false;
							errMessage = message;
						}
						link.setMessage(message);
						link.setResult(result);
						PersistenceHelper.manager.modify(link);
					}

					/*
					 * EPMDocumentMaster master =EpmSearchHelper.manager.getEPM3D(epm2D);
					 * EPMDocument epm3D = EpmSearchHelper.manager.getLastEPMDocument(master);
					 * WTPart part = DrawingHelper.manager.getWTPart(epm3D); //PDF ERP SEND HashMap
					 * mapPDF = this.sendPDF(part,true);
					 * 
					 * PDM00 UpDate HashMap mapRe = this.updatePDM00(part, mapPDF); String result =
					 * (String)mapRe.get("result"); String message = (String)mapRe.get("message");
					 * 
					 * if(result.equals(ERPUtil.PDF_SEND_FAILE)){ isPdf = false; errMessage =
					 * message; } link.setMessage(message); link.setResult(result);
					 * PersistenceHelper.manager.modify(link);
					 */
				}

				/*
				 * if(isPdf){ setHistoryResult(history); map.put("result",
				 * ERPUtil.PDF_SEND_SUCCESS); map.put("message", errMessage); }else{
				 * map.put("result", ERPUtil.PDF_SEND_FAILE); map.put("message", errMessage); }
				 */
			}
		} catch (Exception ex) {
			map.put("result", ERPUtil.BOM_SEND_FAILE);
			map.put("message", ex.getMessage());
			System.out.println(">>>>>>>>>> ERP ERROR sendPDMBOM<<<<<<<<<<<<<");
			// con.rollback();
			ex.printStackTrace();

		} finally {
			if (dbmanager != null) {
				dbmanager.freeConnection(ERP, con);
			}
		}

		return map;
	}

	public HashMap sendPDMBOM(Vector tempBom, String applayDate, String eoNumber) throws SQLException {

		DBConnectionManager dbmanager = null;

		String message = "";
		HashMap map = new HashMap();
		try {
			dbmanager = DBConnectionManager.getInstance();
			con = dbmanager.getConnection(ERP);
			// con.setAutoCommit(false);

			int bomFaileCount = 0;
			for (int i = 0; i < tempBom.size(); i++) {
				HashMap mapRe = new HashMap();
				ArrayList list = (ArrayList) tempBom.get(i);

				boolean isBom = ERPSearchHelper.manager.duplicationBOM(list);
				// System.out.println(">>>>>>>>>>> isBom :" + isBom);
				if (isBom) {
					continue;
				}

				mapRe = this.createdPDMBOM(list, applayDate, eoNumber);

				String result = (String) mapRe.get("result");

				if (ERPUtil.BOM_SEND_FAILE.equals(result)) {
					bomFaileCount++;
					message = (String) mapRe.get("message");

				}

			}

			if (bomFaileCount > 0) {
				map.put("result", ERPUtil.BOM_SEND_FAILE);
				map.put("message", message);
			} else {
				map.put("result", ERPUtil.BOM_SEND_SUCCESS);
				map.put("message", "BOM 전송 성공");
			}

			// con.commit();
		} catch (Exception ex) {
			map.put("result", ERPUtil.BOM_SEND_FAILE);
			map.put("message", ex.getMessage());
			System.out.println(">>>>>>>>>> ERP ERROR sendPDMBOM<<<<<<<<<<<<<");
			// con.rollback();
			ex.printStackTrace();

		} finally {
			if (dbmanager != null) {
				dbmanager.freeConnection(ERP, con);
			}
		}
		return map;
	}

	public String getRequestOrderList(EChangeOrder2 eco) {

		String ecrList = "";
		try {
			QueryResult rt = ChangeECOHelper.manager.getRequestOrderLink(eco);

			while (rt.hasMoreElements()) {
				EChangeRequest2 ecr = (EChangeRequest2) rt.nextElement();

				ecrList += "," + ecr.getRequestNumber();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ecrList;
	}

	public Vector getBom(WTPart topPart, boolean bomApprove, ERPHistory history) throws Exception {
		View view = getView();

		Vector tempBom = new Vector();
		Vector vecPart = new Vector();
		Vector erpBom = new Vector();
		tempBom = this.getBom(topPart, null, view, tempBom);

		for (int i = 0; i < tempBom.size(); i++) {
			ArrayList bomlist = new ArrayList();
			ArrayList bom = (ArrayList) tempBom.get(i);
			WTPart ppart = (WTPart) bom.get(0);
			ppart = (WTPart) PersistenceHelper.manager.refresh(ppart);
			WTPart cpart = (WTPart) bom.get(1);
			cpart = (WTPart) PersistenceHelper.manager.refresh(cpart);

			if (bomApprove) {
				/* Part STATE CHANGE 'APPROVED' */
				if (!vecPart.contains(ppart)) {
					ChangeECOHelper.manager.commitPartStateChange(history, ppart);
					ppart = (WTPart) PersistenceHelper.manager.refresh(ppart);

					if (IBAUtil.getAttrValue(ppart, "autoNumber") != null
							&& "TRUE".equals(IBAUtil.getAttrValue(ppart, "autoNumber"))) {
						this.partSend(ppart, history);
						vecPart.add(ppart);
					}
				}

				if (!vecPart.contains(cpart)) {
					ChangeECOHelper.manager.commitPartStateChange(history, cpart);
					cpart = (WTPart) PersistenceHelper.manager.refresh(cpart);

					if (IBAUtil.getAttrValue(cpart, "autoNumber") != null
							&& "TRUE".equals(IBAUtil.getAttrValue(cpart, "autoNumber"))) {
						this.partSend(cpart, history);
						vecPart.add(cpart);
					}
				}
			}

			if (!ppart.getLifeCycleState().toString().equals("APPROVED"))
				continue;
			if (!cpart.getLifeCycleState().toString().equals("APPROVED"))
				continue;

			if (IBAUtil.getAttrValue(cpart, "autoNumber") != null
					&& "TRUE".equals(IBAUtil.getAttrValue(cpart, "autoNumber"))
					&& IBAUtil.getAttrValue(cpart, "autoNumber") != null
					&& "TRUE".equals(IBAUtil.getAttrValue(cpart, "autoNumber"))) {
				bomlist.add(ppart.getNumber());// 0
				bomlist.add(ppart.getVersionIdentifier().getSeries().getValue() + "."
						+ ppart.getIterationIdentifier().getSeries().getValue());// 1
				bomlist.add(cpart.getNumber());// 2
				bomlist.add(cpart.getVersionIdentifier().getSeries().getValue() + "."
						+ cpart.getIterationIdentifier().getSeries().getValue());// 3
				bomlist.add(bom.get(2));// 4

				erpBom.add(bomlist);
			}

		}

		return erpBom;

	}

	public Vector getBom(WTPart part, WTPartUsageLink link, View view, Vector tempBom) throws Exception {

//		if( IBAUtil.getAttrValue(part, "autoNumber") != null && "TRUE".equals( IBAUtil.getAttrValue(part, "autoNumber") ) ) {
		ArrayList list = this.descentLastPart(part, view, null);

		for (int i = 0; i < list.size(); i++) {
			ArrayList bomlist = new ArrayList();

			Object obj = (Object) list.get(i);
			if (obj instanceof Object[]) {
				Object[] o = (Object[]) obj;
				WTPartUsageLink linko = (WTPartUsageLink) o[0];
				WTPart ppart = (WTPart) linko.getRoleAObject();
				WTPartMaster master = (WTPartMaster) linko.getRoleBObject();
				WTPart cpart = PartSearchHelper.getLastWTPart(master);

				bomlist.add(ppart);
				bomlist.add(cpart);
				bomlist.add(linko.getQuantity().getAmount());
				tempBom.add(bomlist);

				this.getBom((WTPart) o[1], (WTPartUsageLink) o[0], view, tempBom);
			} else {
				WTPartUsageLink linko = (WTPartUsageLink) obj;
				WTPart ppart = (WTPart) linko.getRoleAObject();
				WTPartMaster master = (WTPartMaster) linko.getRoleBObject();
				WTPart cpart = PartSearchHelper.getLastWTPart(master);

				bomlist.add(ppart);
				bomlist.add(cpart);
				bomlist.add(linko.getQuantity().getAmount());
				tempBom.add(bomlist);

				this.getBom(cpart, linko, view, tempBom);
			}

		}
//		}

		return tempBom;
	}

	public View getView() throws WTException {
		return ViewHelper.service.getView("Design");
	}

	public static ArrayList descentLastPart(WTPart part, View view, State state) throws WTException {
		ArrayList v = new ArrayList();
//		if (!PersistenceHelper.isPersistent(part))
//			return v;
		try {
			WTPartConfigSpec configSpec = WTPartConfigSpec
					.newWTPartConfigSpec(WTPartStandardConfigSpec.newWTPartStandardConfigSpec(view, state));
			QueryResult re = wt.part.WTPartHelper.service.getUsesWTParts(part, configSpec);
			while (re.hasMoreElements()) {
				Object obj = re.nextElement();
				if (obj instanceof Object[]) {
					Object oo[] = (Object[]) obj;

					if (!(oo[1] instanceof WTPart)) {
						continue;
					}
					v.add(oo);
				} else if (obj instanceof WTPartUsageLink) {
					WTPartUsageLink link = (WTPartUsageLink) obj;

					v.add(link);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new WTException();
		}
		return v;
	}

	/* ERP PDF SEND */
	public HashMap sendPDF(WTPart part, boolean pdfDown) {

		HashMap mapRe = new HashMap();
		String erpfolderPath = "";
		try {
			String pdfFileName = part.getNumber() + "." + part.getVersionIdentifier().getSeries() + "."
					+ part.getIterationIdentifier().getSeries() + ".pdf";
			EPMDocument epm3D = DrawingHelper.manager.getEPMDocument(part);

			String folderPath = ERPUtil.PDF_FOLDER + File.separator;
			String toDay = DateUtil.getToDay("yyyyMMdd");
			String toYear = toDay.substring(0, 4);
			String toMonth = toDay.substring(4, 8);
			folderPath = folderPath + File.separator + toYear + File.separator + toMonth;
			erpfolderPath = toYear + File.separator + toMonth;

			if (epm3D == null) {
				mapRe.put("result", ERPUtil.PDF_SEND_NO);
				mapRe.put("message", "CAD가 존재 하지 않습니다.");
				mapRe.put("isDrawing", "N");
				mapRe.put("isPbulish", "N");
				return mapRe;
			}
//    		System.out.println("epm3D------->>>"+epm3D);

			if ("PROE".equals(epm3D.getAuthoringApplication().toString())) {
				EPMDocument epm2D = EpmSearchHelper.manager.getEPM2D((EPMDocumentMaster) epm3D.getMaster());

				if (epm2D == null) {
					mapRe.put("result", ERPUtil.PDF_SEND_NO);
					mapRe.put("message", "2D가 존재 하지 않습니다.");
					mapRe.put("isDrawing", "N");
					mapRe.put("isPbulish", "N");
					return mapRe;
				}

				Representation representation = PublishUtils.getRepresentation(epm2D);

				if (representation == null) {
					mapRe.put("result", ERPUtil.HISTORY_STATE_FAILE);
					mapRe.put("message", "변환이 이루어 지지 않습니다.");
					mapRe.put("isDrawing", "Y");
					mapRe.put("isPbulish", "N");
					return mapRe;
				}

				if (pdfDown) {

				}
				representation = (Representation) ContentHelper.service.getContents(representation);
				Vector contentList = ContentHelper.getContentList(representation);
				boolean isPublish = false;
				for (int l = 0; l < contentList.size(); l++) {
					ContentItem contentitem = (ContentItem) contentList.elementAt(l);
					if (contentitem instanceof ApplicationData) {
						ApplicationData drawAppData = (ApplicationData) contentitem;

						if (drawAppData.getRole().toString().equals("SECONDARY")
								&& drawAppData.getFileName().lastIndexOf("pdf") > 0) {
							HashMap map = new HashMap();
							/*
							 * map.put("oid", CommonUtil.getOIDString(drawAppData)); map.put("tempDir",
							 * folderPath); map.put("pdfFileName", pdfFileName);
							 * 
							 * if(pdfDown){ mapRe = FileDown.pdfDown(map); }else{
							 * mapRe.put("isDrawing","Y"); mapRe.put("isPbulish", "N"); }
							 * 
							 * 
							 * mapRe.put("isDrawing","Y"); mapRe.put("isPbulish", "Y"); isPublish = true;
							 */

							mapRe.put("result", ERPUtil.PDF_SEND_WAITING);
							mapRe.put("message", "변환 대기");
							mapRe.put("isDrawing", "Y");
							mapRe.put("isPbulish", "N");
							isPublish = true;
						}
					}
				}

				if (!isPublish) {
					mapRe.put("result", ERPUtil.HISTORY_STATE_FAILE);
					mapRe.put("message", "변환 데이터를 찾지 못했습니다.");
					mapRe.put("isDrawing", "Y");
					mapRe.put("isPbulish", "X");
					return mapRe;
				}

			} else if ("ACAD".equals(epm3D.getAuthoringApplication().toString())) {

				QueryResult result = ContentHelper.service.getContentsByRole((ContentHolder) epm3D,
						ContentRoleType.SECONDARY);
				ContentItem tempitem = null;
				boolean isPublish = false;
				while (result.hasMoreElements()) {
					tempitem = (ContentItem) result.nextElement();
					ApplicationData pAppData = (ApplicationData) tempitem;

					if (pAppData.getDescription() == null || pAppData.getDescription().equals("N"))
						continue;
					HashMap map = new HashMap();
					map.put("oid", CommonUtil.getOIDString(pAppData));
					map.put("tempDir", folderPath);
					map.put("pdfFileName", pdfFileName);
					map.put("epmType", "AutoCad");
					mapRe = FileDown.pdfDown(map);
					mapRe.put("isDrawing", "Y");
					mapRe.put("isPbulish", "Y");
					isPublish = true;
				}

				if (!isPublish) {
					mapRe.put("result", ERPUtil.HISTORY_STATE_FAILE);
					mapRe.put("message", "변환 데이터를 찾지 못했습니다.");
					mapRe.put("isDrawing", "Y");
					mapRe.put("isPbulish", "X");

				}

			} else {// if("PROE".equals(epm3D.getAuthoringApplication().toString())){

				QueryResult result = ContentHelper.service.getContentsByRole((ContentHolder) epm3D,
						ContentRoleType.PRIMARY);
				ContentItem tempitem = null;
				boolean isPublish = false;
				while (result.hasMoreElements()) {
					tempitem = (ContentItem) result.nextElement();
					ApplicationData pAppData = (ApplicationData) tempitem;
//    	    		System.out.println("appfilename------->>>"+pAppData.getFileName());

					HashMap map = new HashMap();
					map.put("oid", CommonUtil.getOIDString(pAppData));
					map.put("tempDir", folderPath);
					map.put("pdfFileName", pdfFileName);
					map.put("epmType", "AutoCad");
					mapRe = FileDown.pdfDown(map);
					mapRe.put("isDrawing", "Y");
					mapRe.put("isPbulish", "Y");
					isPublish = true;
				}

				if (!isPublish) {
					mapRe.put("result", ERPUtil.HISTORY_STATE_FAILE);
					mapRe.put("message", "변환 데이터를 찾지 못했습니다.");
					mapRe.put("isDrawing", "Y");
					mapRe.put("isPbulish", "X");

				}
			} // if("PROE".equals(epm3D.getAuthoringApplication().toString())){

		} catch (Exception e) {
			mapRe.put("result", ERPUtil.HISTORY_STATE_FAILE);
			mapRe.put("message", e.getMessage());
			mapRe.put("isDrawing", "X");
			mapRe.put("isPbulish", "X");

			e.printStackTrace();
		}

		mapRe.put("folderPath", erpfolderPath);
		return mapRe;

	}

	/* ERP PDF SEND */
	public HashMap sendPDF(EPMDocument epm) {

		HashMap mapRe = new HashMap();
		try {
			WTPart part = DrawingHelper.manager.getWTPart(epm);

			String pdfFileName = part.getNumber() + "." + part.getVersionIdentifier().getSeries() + "."
					+ part.getIterationIdentifier().getSeries() + ".pdf";
			EPMDocument epm3D = DrawingHelper.manager.getEPMDocument(part);

			String folderPath = ERPUtil.PDF_FOLDER + File.separator;
			String toDay = DateUtil.getToDay("yyyyMMdd");
			String toYear = toDay.substring(0, 4);
			String toMonth = toDay.substring(4, 8);
			folderPath = folderPath + File.separator + toYear + File.separator + toMonth;

			mapRe.put("folderPath", folderPath);
			if (epm3D == null) {
				mapRe.put("result", ERPUtil.PDF_SEND_NO);
				mapRe.put("message", "CAD가 존재 하지 않습니다.");
				mapRe.put("isDrawing", "N");
				mapRe.put("isPbulish", "N");
				return mapRe;
			}

			if ("PROE".equals(epm3D.getAuthoringApplication().toString())) {
				EPMDocument epm2D = EpmSearchHelper.manager.getEPM2D((EPMDocumentMaster) epm3D.getMaster());

				if (epm2D == null) {
					mapRe.put("result", ERPUtil.PDF_SEND_NO);
					mapRe.put("message", "2D가 존재 하지 않습니다.");
					mapRe.put("isDrawing", "N");
					mapRe.put("isPbulish", "N");
					return mapRe;
				}

				Representation representation = PublishUtils.getRepresentation(epm2D);

				if (representation == null) {
					mapRe.put("result", ERPUtil.HISTORY_STATE_FAILE);
					mapRe.put("message", "변환이 이루어 지지 않습니다.");
					mapRe.put("isDrawing", "Y");
					mapRe.put("isPbulish", "N");
					return mapRe;
				}

				representation = (Representation) ContentHelper.service.getContents(representation);
				Vector contentList = ContentHelper.getContentList(representation);
				boolean isPublish = false;
				for (int l = 0; l < contentList.size(); l++) {
					ContentItem contentitem = (ContentItem) contentList.elementAt(l);
					if (contentitem instanceof ApplicationData) {
						ApplicationData drawAppData = (ApplicationData) contentitem;

						if (drawAppData.getRole().toString().equals("SECONDARY")
								&& drawAppData.getFileName().lastIndexOf("pdf") > 0) {
							HashMap map = new HashMap();

							map.put("oid", CommonUtil.getOIDString(drawAppData));
							map.put("tempDir", folderPath);
							map.put("pdfFileName", pdfFileName);
//    	            		System.out.println("folderPath :" + folderPath);
							mapRe = FileDown.pdfDown(map);

							mapRe.put("isDrawing", "Y");
							mapRe.put("isPbulish", "Y");
							isPublish = true;
						}
					}
				}

				if (!isPublish) {
					mapRe.put("result", ERPUtil.HISTORY_STATE_FAILE);
					mapRe.put("message", "변환 데이터를 찾지 못습니다.");
					mapRe.put("isDrawing", "Y");
					mapRe.put("isPbulish", "X");
					return mapRe;
				}

			} else {// if("PROE".equals(epm3D.getAuthoringApplication().toString())){

				QueryResult result = ContentHelper.service.getContentsByRole((ContentHolder) epm3D,
						ContentRoleType.SECONDARY);
				ContentItem tempitem = null;
				boolean isPublish = false;
				while (result.hasMoreElements()) {
					tempitem = (ContentItem) result.nextElement();
					ApplicationData pAppData = (ApplicationData) tempitem;

					if (pAppData.getDescription() == null || pAppData.getDescription().equals("N"))
						continue;
					HashMap map = new HashMap();
					map.put("oid", CommonUtil.getOIDString(pAppData));
					map.put("tempDir", folderPath);
					map.put("pdfFileName", pdfFileName);
					map.put("epmType", "AutoCad");
					mapRe = FileDown.pdfDown(map);
					mapRe.put("isDrawing", "Y");
					mapRe.put("isPbulish", "Y");
					isPublish = true;
				}

				if (!isPublish) {
					mapRe.put("result", ERPUtil.HISTORY_STATE_FAILE);
					mapRe.put("message", "변환 데이터를 찾지 못습니다.");
					mapRe.put("isDrawing", "Y");
					mapRe.put("isPbulish", "X");

				}
			} // if("PROE".equals(epm3D.getAuthoringApplication().toString())){

		} catch (Exception e) {
			mapRe.put("result", ERPUtil.HISTORY_STATE_FAILE);
			mapRe.put("message", e.getMessage());
			mapRe.put("isDrawing", "X");
			mapRe.put("isPbulish", "X");

			e.printStackTrace();
		}

		return mapRe;

	}

	/* WTPART ERP PDMEOITEM 전송 */
	public HashMap createdPDMEOITEM(WTPart part, String eoNumber, Connection con) {

		boolean result = false;
		HashMap mapRe = new HashMap();
		try {

			if (!enableERP) {
				mapRe.put("result", ERPUtil.HISTORY_STATE_SUCCESS);
				mapRe.put("message", "ERP 전송을 하지 않습니다.");
				return mapRe;
			}

			PartData data = new PartData(part);
			StringBuffer sql = new StringBuffer();
			sql.append("INSERT INTO PDMEOITEM ").append("(DIV,EoNO,ItemCode,ItemVer)").append(" VALUES (?,?,?,?)");

			PreparedStatement st = con.prepareStatement(sql.toString());
			String div = "ECR";
			if (eoNumber.startsWith("ECO")) {
				div = "ECO";
			}

			st.setString(1, div);
			st.setString(2, eoNumber);
			st.setString(3, data.getNumber());
			st.setString(4, data.getVersion() + "." + data.getIteration());
			st.execute();

			mapRe.put("result", ERPUtil.HISTORY_STATE_SUCCESS);
			mapRe.put("message", "성공");

		} catch (Exception e) {
			mapRe.put("result", ERPUtil.HISTORY_STATE_FAILE);
			mapRe.put("message", e.getMessage());
			e.printStackTrace();
		}

		return mapRe;

	}

	/* ERP PDMECO(ECO) TABLE CREATE */
	public HashMap createPDMECO(EChangeOrder2 eco) {

		HashMap mapRe = new HashMap();

		try {
			if (!enableERP) {
				mapRe.put("result", ERPUtil.HISTORY_STATE_SUCCESS);
				mapRe.put("message", "ERP 전송을 하지 않습니다.");
				return mapRe;
			}

			StringBuffer sql = new StringBuffer();
			String ecrNumber = this.getRequestOrderList(eco);

			String purposeValue = "";
			String purpose = eco.getPurpose();
			StringTokenizer tokens = new StringTokenizer(purpose, ",");

			while (tokens.hasMoreTokens()) {
				String pp = (String) tokens.nextToken();

				purposeValue += CodeHelper.manager.getName("CHANGEPURPOSE", pp) + ",";
			}

			String stockPartValue = "";
			String stockPart = eco.getStockPart(); // stockpart
			if (stockPart != null) {
				tokens = new StringTokenizer(stockPart, ",");
				while (tokens.hasMoreTokens()) {
					String pp = (String) tokens.nextToken();

					stockPartValue = CodeHelper.manager.getName("STOCKMANAGEMENT", pp) + ",";
				}
			}

			sql.append("INSERT INTO PDMECO ").append(
					"(PrjNo,PrjSeqNo,UnitCode,EcoNo,EcrNo,Title,Div,Type,StockDiv,ApplyDate,WorkDate,State,ConfirmDate,EndDate,Process)")
					.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

			PreparedStatement st = con.prepareStatement(sql.toString());
			int idx = 1;

			st.setString(idx, eco.getPrjNo());
//			System.out.println("1------->>>"+eco.getPrjNo());
			String prjSeqNo = eco.getPrjSeqNo().substring(eco.getPrjSeqNo().indexOf("-") + 1,
					eco.getPrjSeqNo().length());
			st.setString(++idx, prjSeqNo);
//			System.out.println("2------->>>"+eco.getPrjSeqNo());
			st.setString(++idx, eco.getUnitCode());
//			System.out.println("3------->>>"+eco.getUnitCode());
			st.setString(++idx, eco.getOrderNumber());
//			System.out.println("4------->>>"+eco.getOrderNumber());
			st.setString(++idx, ecrNumber);
//			System.out.println("5------->>>"+ecrNumber);
			st.setString(++idx, eco.getName());
//			System.out.println("6------->>>"+eco.getName());
			st.setString(++idx, CodeHelper.manager.getName("EOTYPE", eco.getEcoType()));
//			System.out.println("7------->>>"+CodeHelper.manager.getName("EOTYPE",eco.getEcoType()));
			st.setString(++idx, purposeValue);
//			System.out.println("8------->>>"+purposeValue);
			st.setString(++idx, stockPartValue);
//			System.out.println("9------->>>"+stockPartValue);
			st.setString(++idx, eco.getApplyDate());
//			System.out.println("10------->>>"+eco.getApplyDate());
			st.setString(++idx, DateUtil.getDateString(eco.getPersistInfo().getCreateStamp(), "d"));
//			System.out.println("11------->>>"+DateUtil.getDateString(eco.getPersistInfo().getCreateStamp(), "d"));
			if (ChangeECOHelper.ECR_NO.equals(eco.getProcess())) {
				st.setString(++idx, MASTER_COMPLETE);
//				System.out.println("12------->>>"+MASTER_COMPLETE);
			} else {
				st.setString(++idx, MASTER_WORKING);
//				System.out.println("13------->>>"+MASTER_WORKING);
			}

			st.setString(++idx, DateUtil.getToDay(ERPUtil.Dateformat));
//			System.out.println("14------->>>"+DateUtil.getToDay(ERPUtil.Dateformat));
			if (ChangeECOHelper.ECR_NO.equals(eco.getProcess())) {
				st.setString(++idx, DateUtil.getToDay(ERPUtil.Dateformat));
//				System.out.println("15------->>>"+DateUtil.getToDay(ERPUtil.Dateformat));
			} else {
				st.setString(++idx, "");
//				System.out.println("16------->>>");
			}

			st.setString(++idx, eco.getProcess());
//			System.out.println("17------->>>"+eco.getProcess());
			st.execute();

			mapRe.put("result", ERPUtil.HISTORY_STATE_SUCCESS);
			mapRe.put("message", "성공");
		} catch (Exception e) {
			System.out.println("::::::::::::::::::::: ERP SEND ERROR :::::::::::::::::");
			mapRe.put("result", ERPUtil.HISTORY_STATE_FAILE);
			mapRe.put("message", e.getMessage());
			e.printStackTrace();
		}

		return mapRe;

	}

	/* ERP PDMECO(ECO) TABLE CREATE */
	public HashMap upDatePDMECO(EChangeOrder2 eco) {

		HashMap mapRe = new HashMap();

		try {

			if (!enableERP) {
				mapRe.put("result", ERPUtil.HISTORY_STATE_SUCCESS);
				mapRe.put("message", "ERP 전송을 하지 않습니다.");
				return mapRe;
			}

			eco = (EChangeOrder2) PersistenceHelper.manager.refresh(eco);

			StringBuffer sql = new StringBuffer();
			String ecrNumber = this.getRequestOrderList(eco);
			String state = MASTER_COMPLETE;
			if (eco.getOrderState().equals(ChangeECOHelper.ECO_REJECTED)) {
				state = MASTER_REJECT;
			}

			sql.append("UPDATE PDMECO ").append(" set state = ?,EndDate= ?").append(" WHERE EcoNo = ?");
			PreparedStatement st = con.prepareStatement(sql.toString());
			int idx = 1;

			st.setString(idx, state);
			st.setString(++idx, DateUtil.getToDay(ERPUtil.Dateformat));
			st.setString(++idx, eco.getOrderNumber());
			st.execute();

			mapRe.put("result", ERPUtil.HISTORY_STATE_SUCCESS);
			mapRe.put("message", "성공");
		} catch (Exception e) {
			System.out.println("::::::::::::::::::::: ERP SEND ERROR :::::::::::::::::");
			mapRe.put("result", ERPUtil.HISTORY_STATE_FAILE);
			mapRe.put("message", e.getMessage());
			e.printStackTrace();
		}

		return mapRe;

	}

	/* WTPART ERP PDM00 품목 코드 전송 */
	public HashMap createdPDM00(WTPart part, EChangeOrder2 eco) {

		boolean result = false;
		HashMap mapRe = new HashMap();

		try {
			eco = (EChangeOrder2) PersistenceHelper.manager.refresh(eco);

			if (!enableERP) {
				mapRe.put("result", ERPUtil.HISTORY_STATE_SUCCESS);
				mapRe.put("message", "ERP 전송을 하지 않습니다.");
				return mapRe;
			}

			/* PDF SEND */
			HashMap mapPDF = this.sendPDF(part, false);

			/* 대칭도면(소재도(가공도)MI */
			String itemCode2 = "";
			String itemVer2 = "";
			EPMDocument epm = DrawingHelper.manager.getEPMDocument(part);
			if (epm != null) {
				EPMDocument refEpm = EpmSearchHelper.manager.getREFDWG(epm);
				WTPart refPart = DrawingHelper.manager.getWTPart(refEpm);
				if (refPart != null) {
					itemCode2 = refPart.getNumber();
					itemVer2 = refPart.getVersionIdentifier().getSeries().getValue() + "."
							+ refPart.getIterationIdentifier().getSeries().getValue();
				}
			}

			/* 상품도면 */
			String DrawNo = "";
			String DrawVer = "";
			PartSearchHelper search = new PartSearchHelper();
			EPMDocument gEpm = search.getGPartDWG(part);
			if (gEpm != null) {
				WTPart gPart = DrawingHelper.manager.getWTPart(gEpm);
				if (gPart != null) {
					DrawNo = gPart.getNumber();
					DrawVer = gPart.getVersionIdentifier().getSeries().getValue() + "."
							+ gPart.getIterationIdentifier().getSeries().getValue();
				}

			}

//			System.out.println("ERRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR1");
			String ecoNumber = eco.getOrderNumber();
			String applyDate = eco.getApplyDate();
			if (eco.getProcess().equals(ChangeECOHelper.ECR_NO)) {
				applyDate = DateUtil.getToDay();
				eco.setApplyDate(applyDate);
				PersistenceHelper.manager.modify(eco);
			}

			System.out.println("ecoNumber : " + ecoNumber);
			String pdfresult = (String) mapPDF.get("result");
			System.out.println("pdfresult : " + pdfresult);
			String pdfmessage = (String) mapPDF.get("message");
			System.out.println("pdfmessage : " + pdfmessage);
			String isDrawing = "";// (String)mapPDF.get("isDrawing");
			System.out.println("isDrawing : " + isDrawing);
			String isPbulish = (String) mapPDF.get("isPbulish");
			System.out.println("isPbulish : " + isPbulish);
			String folderPath = (String) mapPDF.get("folderPath");
			System.out.println("folderPath : " + folderPath);
//			System.out.println("ERRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR1");
//			if(pdfresult.equals(ERPUtil.PDF_SEND_FAILE) || pdfresult.equals(ERPUtil.PDF_SEND_NO))folderPath ="";
			if (pdfresult.equals(pdfresult.equals(ERPUtil.PDF_SEND_NO)))
				folderPath = "";
			if (pdfresult.equals(ERPUtil.PDF_SEND_NO))
				pdfresult = "X";
			if (pdfresult.equals(ERPUtil.PDF_SEND_SUCCESS))
				pdfresult = "Y";
			if (pdfresult.equals(ERPUtil.PDF_SEND_FAILE) || pdfresult.equals(ERPUtil.PDF_SEND_WAITING))
				pdfresult = "N";

			PartData data = new PartData(part);

			ecolog.infoLog("::::::::::::::::::::::::  " + data.getNumber() + " : " + data.getName()
					+ ":::::::::::::::::::::::: ");
			String pdfFilePath = ERPUtil.PDF_FOLDER + File.separator + folderPath;
			String pdfFileName = pdfFilePath + File.separator + data.getNumber() + "." + data.getVersion() + "."
					+ data.getIteration() + ".pdf";
			// File pdfFile = new File()
			File pdfFile = new File(pdfFileName);
//			System.out.println(":::::::::::::::: pdfFileName :"+pdfFileName +" : " + pdfFile.exists());
			if (pdfFile.exists()) {
				pdfresult = "Y";
			}

			if (pdfresult.equals("X"))
				isDrawing = "N";
			else
				isDrawing = "Y";

			StringBuffer sql = new StringBuffer();
			sql.append("INSERT INTO PDM00 ")
					.append("(PDMWorkTime,ItemCode,ItemVer,ItemName,ItemSpec,BaseUnit,ApplyDate,Maker,FileDiv,"
							+ "TransDiv,Folder,itemCode2,itemVer2,EoNo,DrawNo,DrawVer,ItemSpec2,Maker2)")
					.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			System.out.println("sql=" + sql);
			PreparedStatement st = con.prepareStatement(sql.toString());

			int idx = 1;
			String workTime = DateUtil.getTodayDate("yyyyMMddHHmmss");
//			System.out.println(":::::::::::: worktime :" +workTime);

			// 규격
			String spec = data.getSpec();
			String specCode = "";
			if (spec.length() > 0) {
				specCode = CodeHelper.manager.getCode("SPEC", spec);
			} else {
				specCode = null;
			}

			// Maker
			String maker = data.getMaker() == null ? "" : data.getMaker();
			maker = maker.trim();
			String makerCode = "";
			if (maker != null && maker.length() > 0) {
				HashMap map = new HashMap();
				map.put("makerName", maker);
				map.put("searchType", "equals");
				ResultSet rs = ERPSearchHelper.manager.getErpMaker(map);

				while (rs.next()) {
					makerCode = (String) rs.getObject("Maker");
				}
			}

//			System.out.println(workTime+
//					":"+data.getNumber()+":"+ data.getVersion()+
//					"."+data.getIteration()+":"+data.getName()+":"+spec+":"+part.getDefaultUnit().toString()+
//					applyDate+":"+maker+":"+isDrawing+":"+pdfresult+":"+folderPath+
//					itemCode2+":"+itemVer2+":"+ecoNumber+":"+DrawNo+":"+DrawVer);

			st.setString(idx, workTime);
			st.setString(++idx, data.getNumber());
			st.setString(++idx, data.getVersion() + "." + data.getIteration());
			st.setString(++idx, data.getName());
			st.setString(++idx, specCode);
			st.setString(++idx, part.getDefaultUnit().toString());
			st.setString(++idx, applyDate);
			st.setString(++idx, makerCode);
			st.setString(++idx, isDrawing);
			st.setString(++idx, pdfresult);
			st.setString(++idx, folderPath);
			st.setString(++idx, itemCode2);
			st.setString(++idx, itemVer2);
			st.setString(++idx, ecoNumber);
			st.setString(++idx, DrawNo);
			st.setString(++idx, DrawVer);
			st.setString(++idx, spec);
			st.setString(++idx, maker);

			ecolog.infoLog("workTime = " + workTime);
			ecolog.infoLog("ItemCode = " + data.getNumber());
			ecolog.infoLog("ItemVer = " + data.getVersion() + "." + data.getIteration());
			ecolog.infoLog("ItemName = " + data.getName());
			ecolog.infoLog("ItemSpec = " + spec);
			ecolog.infoLog("BaseUnit = " + part.getDefaultUnit().toString());
			ecolog.infoLog("ApplyDate = " + applyDate);
			ecolog.infoLog("Maker = " + makerCode);
			ecolog.infoLog("FileDiv = " + isDrawing);
			ecolog.infoLog("TransDiv = " + pdfresult);
			ecolog.infoLog("Folder = " + folderPath);
			ecolog.infoLog("itemCode2 = " + itemCode2);
			ecolog.infoLog("itemVer2 = " + itemVer2);
			ecolog.infoLog("EoNo = " + ecoNumber);
			ecolog.infoLog("DrawNo = " + DrawNo);
			ecolog.infoLog("ItemSpec2 = " + spec);
			ecolog.infoLog("Maker2 = " + maker);

			st.execute();

			mapRe.put("result", ERPUtil.HISTORY_STATE_SUCCESS);
			mapRe.put("message", "성공");
			mapRe.put("pdfresult", pdfresult);
			mapRe.put("pdfmessage", pdfmessage);
			mapRe.put("folderPath", folderPath);
		} catch (Exception e) {
			mapRe.put("result", ERPUtil.HISTORY_STATE_FAILE);
			mapRe.put("message", e.getMessage());
			mapRe.put("pdfresult", ERPUtil.PDF_SEND_FAILE);
			mapRe.put("pdfmessage", "품목 전송 실패");
			mapRe.put("folderPath", "");
			e.printStackTrace();
		}

		return mapRe;

	}

	public HashMap updatePDM00(WTPart part, HashMap mapPDF) {

		HashMap mapRe = new HashMap();

		DBConnectionManager dbmanager = null;
		try {
			dbmanager = DBConnectionManager.getInstance();
			con = dbmanager.getConnection(ERP);

			String number = part.getNumber();
			String version = part.getVersionIdentifier().getSeries().getValue() + "."
					+ part.getIterationIdentifier().getSeries().getValue();
			String pdfresult = (String) mapPDF.get("result");
			String pdfmessage = (String) mapPDF.get("message");
			String isDrawing = "";// (String)mapPDF.get("isDrawing");
			String isPbulish = (String) mapPDF.get("isPbulish");
			String pdmResult = pdfresult;
			String folderPath = (String) mapPDF.get("folderPath");

			if (pdfresult.equals(ERPUtil.PDF_SEND_NO))
				pdfresult = "X";
			if (pdfresult.equals(ERPUtil.PDF_SEND_SUCCESS))
				pdfresult = "Y";
			if (pdfresult.equals(ERPUtil.PDF_SEND_FAILE))
				pdfresult = "N";

			boolean isExist = ERPSearchHelper.manager.duplicationPart(number, version);

			if (pdfresult.equals("X"))
				isDrawing = "N";
			else
				isDrawing = "Y";

			// System.out.println("UPDATE updatePDM00 : "+number + ":"+ version +":" +
			// isDrawing+":"+pdfresult+":"+folderPath);

			if (isExist) {
				StringBuffer sql = new StringBuffer();
				sql.append("Update PDM00  ").append(" set FileDiv = ?, TransDiv = ?").append(" Where itemCode = ?")
						.append(" and itemVer =?");
				PreparedStatement st = con.prepareStatement(sql.toString());

				int idx = 1;

				st.setString(idx, pdfresult);
				st.setString(++idx, isDrawing);
				st.setString(++idx, number);
				st.setString(++idx, version);
				st.execute();

				mapRe.put("message", pdfmessage);
				mapRe.put("result", pdmResult);
			} else {
				mapRe.put("message", number + "," + version + "의 품번이 존재 하지 않습니다.");
				mapRe.put("result", ERPUtil.PDF_SEND_FAILE);
			}

		} catch (Exception ex) {
			mapRe.put("result", ERPUtil.PDF_SEND_FAILE);
			mapRe.put("message", ex.getLocalizedMessage());
			ex.printStackTrace();

		} finally {
			if (dbmanager != null) {
				dbmanager.freeConnection(ERP, con);
			}
		}

		return mapRe;
	}

	/* ERP PDMBOM CREATE -표준 BOM */
	public HashMap createdPDMBOM(ArrayList list, String applayDate, String eoNumber) throws Exception {

		boolean result = false;
		HashMap mapRe = new HashMap();
		try {

			if (!enableERP) {
				mapRe.put("result", ERPUtil.HISTORY_STATE_SUCCESS);
				mapRe.put("message", "ERP 전송을 하지 않습니다.");
				return mapRe;
			}

			StringBuffer sql = new StringBuffer();
			sql.append("INSERT INTO PDMBOM ")
					.append("(PDMWorkTime,ParentItemCode,ParentItemVer,ItemCode,ItemVer,MkuQty,EoNo)")
					.append(" VALUES (?,?,?,?,?,?,?)");

			PreparedStatement st = con.prepareStatement(sql.toString());

			int idx = 1;
//			System.out.println("CREATE BOM : "+list.get(0)+" :"+ list.get(1)+" :"+ list.get(2)+" :"+ list.get(3)+" :"+ list.get(4));
			Double dd = (Double) list.get(4);

			String workTime = DateUtil.getTodayDate("yyyyMMddHHmmss");
			workTime = workTime.substring(0, workTime.length() - 1);
//			System.out.println("workTime------->>>"+workTime);
			st.setString(idx, workTime);
			st.setString(++idx, (String) list.get(0));
			st.setString(++idx, (String) list.get(1));
			st.setString(++idx, (String) list.get(2));
			st.setString(++idx, (String) list.get(3));
//			System.out.println("MkuQty------->>>"+dd.doubleValue());
			st.setDouble(++idx, dd.doubleValue());
			st.setString(++idx, eoNumber);

			st.execute();

			mapRe.put("result", ERPUtil.BOM_SEND_SUCCESS);
			mapRe.put("message", "성공");

		} catch (Exception e) {
			mapRe.put("result", ERPUtil.BOM_SEND_FAILE);
			mapRe.put("message", e.getMessage());
			e.printStackTrace();
		}

		return mapRe;

	}

	/**
	 * ERP PART synchronization
	 * 
	 * @param args
	 */

	public boolean erpPARTUpdate(String oid) {
		DBConnectionManager dbmanager = null;
		ecolog.infoLog(">>>>>>>>> ERPECOHELPER[erpPARTUpdate]<<<<<<<<<<<<");
		boolean isUpdate = false;
		try {
			dbmanager = DBConnectionManager.getInstance();
			con = dbmanager.getConnection(ERP);
			ERPHistory history = (ERPHistory) CommonUtil.getObject(oid);
			QueryResult qr = ERPSearchHelper.manager.searchPartHistory(history);
			EChangeOrder2 eco = (EChangeOrder2) history.getEo().getObject();
			ecolog.infoLog("[" + eco.getOrderNumber() + "]");
			while (qr.hasMoreElements()) {
				ERPPartLinkO linkO = (ERPPartLinkO) qr.nextElement();
				WTPart part = linkO.getNewPart();

				String partoid = part.getPersistInfo().getObjectIdentifier().toString();
				String itemVer = part.getVersionIdentifier().getValue() + "."
						+ part.getIterationIdentifier().getValue();
				String itemCode = part.getNumber();
				ecolog.infoLog(itemCode + "[" + itemVer + "]");
				boolean isExist = ERPSearchHelper.manager.duplicationPart(itemCode, itemVer);
				if (!isExist) {
					ecolog.exceptionLog("ERP Table에 해당 품목이 존재 하지 않습니다.");
					continue;
				}
				String spec = PartAttributeHelper.getIBASpec(part);
				String maker = PartAttributeHelper.getIBAMaker(part);
				String specCode = "";

				if (spec.length() > 0) {
					specCode = PartAttributeHelper.getSpecCode(spec);
				}

				String makerCode = "";
				if (maker.length() > 0) {
					makerCode = PartAttributeHelper.getMakerCode(maker);
				}

				if (spec.length() > 0 || maker.length() > 0) {
					StringBuffer sql = new StringBuffer();
					sql.append("Update PDM00  ").append(" set ItemSpec = ?, ItemSpec2 = ?,Maker = ?,Maker2 = ?")
							.append(" Where itemCode = ?").append(" and itemVer =?");
					PreparedStatement st = con.prepareStatement(sql.toString());

					int idx = 1;
					ecolog.infoLog("ItemSpec =" + specCode);
					ecolog.infoLog("ItemSpec2 =" + spec);
					ecolog.infoLog("Maker =" + makerCode);
					ecolog.infoLog("Maker2 =" + maker);
					st.setString(idx, specCode);
					st.setString(++idx, spec);
					st.setString(++idx, makerCode);
					st.setString(++idx, maker);
					st.setString(++idx, itemCode);
					st.setString(++idx, itemVer);
					st.execute();
				} else {
					ecolog.exceptionLog("spec,maker의 값이 없습니다.");
				}

			}

			isUpdate = true;
		} catch (Exception ex) {
			isUpdate = false;
			ecolog.exceptionLog(ex.getMessage());
			ex.printStackTrace();

		} finally {
			if (dbmanager != null) {
				dbmanager.freeConnection(ERP, con);
			}
		}

		return isUpdate;

	}

	public static void main(String[] args) {

		System.out.println("enableERP :" + enableERP);

	}

}
