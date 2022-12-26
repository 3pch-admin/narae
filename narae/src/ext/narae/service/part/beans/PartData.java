/*
 * @(#) PartData.java  Create on 2004. 12. 9.
 * Copyright (c) e3ps. All rights reserverd
 */
package ext.narae.service.part.beans;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Vector;

import ext.narae.service.change.EChangeOrder2;
import ext.narae.service.drawing.beans.DrawingHelper;
import ext.narae.util.CommonUtil;
import ext.narae.util.DateUtil;
import ext.narae.util.StringUtil;
import ext.narae.util.iba.IBAUtil;
import ext.narae.util.web.CommonWebHelper;
import wt.epm.EPMDocument;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.util.WTContext;
import wt.util.WTException;
import wt.vc.VersionControlException;
import wt.vc.VersionControlHelper;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;

/**
 * Part에 관련해서 상세한 정보를 포함하는 Data 클래스
 *
 * @author yhjang@e3ps.com
 * @version 1.00, 2004. 12. 9.
 * @since 1.4
 * @see e3ps.util.attr.ObjectData
 */
public class PartData {
	private boolean isPublish;

	private String approvalState;
	private String creator;
	private String icon;
	private String lifecycle;

	private String unit;
	private String unitDesc;

	private EPMDocument epmDoc;
	private HashMap ibaAttr;
	private WTPart part;

	private final String number;
	private final String name;
	private final String oid;
	private final String version;
	private final String location;
	private final String source;
	private final String viewName;
	private final String unitCode;
	private final String partType;
	private final String createDate;
	private final String modifier;
	private final String modifyDateTypeA;
	private final String modifyDateTypeD;
	private final String description;
	private final String isDrawing;
	private final String maker;
	private final String spec;
	private final String autoNumber;
	private final String oldNumber;
	private final String class4;

	public PartData(final WTPart part, final Locale locale) {
		this(part);
	}

	public PartData(final WTPart part) {
		try {
			this.part = part;
			ibaAttr = IBAUtil.getAttributes(part);
			this.icon = CommonWebHelper.getIconImgTag(part);
			this.creator = VersionControlHelper.getVersionCreator(part).getFullName();// part.getOrganizationReference().getName();
			this.lifecycle = part.getLifeCycleState().getDisplay(SessionHelper.manager.getLocale());
			this.unit = part.getDefaultUnit().getDisplay(SessionHelper.manager.getLocale());
			this.unitDesc = part.getDefaultUnit().getShortDescription(SessionHelper.manager.getLocale());
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}

		this.number = part.getNumber();
		this.name = part.getName();
		this.oid = CommonUtil.getOIDString(part);
		this.source = part.getSource().getDisplay();

		this.unitCode = part.getDefaultUnit().toString();
		this.partType = part.getPartType().getDisplay();
		this.version = part.getVersionIdentifier().getValue();

		this.location = StringUtil.checkNull(part.getLocation());
		View view = ViewHelper.getView(part);
		this.viewName = view == null ? "" : view.getName();

		this.createDate = DateUtil.getDateString(part.getPersistInfo().getCreateStamp(), "a");
		this.modifier = part.getModifierFullName();
		this.modifyDateTypeA = DateUtil.getDateString(part.getPersistInfo().getModifyStamp(), "a");
		this.modifyDateTypeD = DateUtil.getDateString(part.getPersistInfo().getModifyStamp(), "d");

		this.description = (ibaAttr != null) ? StringUtil.checkNull((String) ibaAttr.get("Description")) : "";
		this.isDrawing = (ibaAttr != null) ? StringUtil.checkNull((String) ibaAttr.get("IsDrawing")) : "";
		this.maker = (ibaAttr != null) ? StringUtil.checkNull((String) ibaAttr.get("Maker")) : "";
		this.spec = (ibaAttr != null) ? StringUtil.checkNull((String) ibaAttr.get("Spec")) : "";
		this.oldNumber = (ibaAttr != null) ? StringUtil.checkNull((String) ibaAttr.get("OldNumber")) : "";
		this.autoNumber = (ibaAttr != null) ? StringUtil.checkNull((String) ibaAttr.get("autoNumber")) : "";
		this.class4 = (ibaAttr != null) ? StringUtil.checkNull((String) ibaAttr.get("Class4")) : "";

	}

	public static String getPartNumber(final WTPart part) {
		try {
			HashMap map = IBAUtil.getAttributes(part);
			return StringUtil.checkNull((String) map.get("X_No")) + part.getNumber();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @return Returns the isPublished.
	 */
	public boolean isPublish() {
		return isPublish;
	}

	public String getPartType() {
		return partType;
	}

	public String getViewName() {
		return viewName;
	}

	public HashMap getIBAAttributes() {
		return ibaAttr;
	}

	public String getNumber() {
		return number;
	}

	public String getConvertNumber() {
		return number.indexOf("_") > -1 ? number.substring(0, number.lastIndexOf("_")) : number;
	}

	public String getOid() {
		return oid;
	}

	public String getIcon() {
		return icon;
	}

	public String getVersion() {
		return version;
	}

	public String getIteration() throws VersionControlException {

		return this.part.getIterationIdentifier().getSeries().getValue();
	}

	public String getLocation() {
		return location;
	}

	public String getName() {
		return name;
	}

	public String getLocaleName() {
		String localeName = "";

		try {
			Locale userLocale = SessionHelper.manager.getLocale();
			if (userLocale == null) {
				WTContext.getContext().getLocale();
			}

			if (userLocale.equals(Locale.KOREA) || userLocale.equals(Locale.KOREAN)) {
				localeName = getName();
			} else if (userLocale.equals(Locale.US)) {

				localeName = getName();

			} else {
				localeName = getName();
			}
		} catch (Exception e) {

		}
		return localeName;
	}

	/**
	 * 속성(PLM, SAP 자재구분) 리턴
	 * 
	 * @return PLM:자재 설명
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 속성(도면 유무) 리턴
	 * 
	 * @return
	 */
	public String getIsDrawing() {
		return isDrawing;
	}

	/**
	 * 속성(메이커) 리턴
	 * 
	 * @return
	 */
	public String getMaker() {
		return maker;
	}

	/**
	 * 속성(규격) 리턴
	 * 
	 */
	public String getSpec() {
		return spec;
	}

	/**
	 * 속성(oldNumber) 리턴
	 * 
	 */
	public String getOldeNumber() {
		return oldNumber;
	}

	/**
	 * 속성(oldNumber) 리턴
	 * 
	 */
	public String getautoNumber() {
		return autoNumber;
	}

	public String getSource() {
		return source;
	}

	public String getUnit() {
		return unit;
	}

	/**
	 * @return the unitDesc
	 */
	public String getUnitDesc() {
		return unitDesc;
	}

	public String getUnitCode() {
		return unitCode;
	}

	public String getLifecycle() {
		return lifecycle;
	}

	public EPMDocument getEPMDocument() throws Exception {
		if (epmDoc == null) {
			epmDoc = DrawingHelper.manager.getEPMDocument(part);
		}

		return epmDoc;
	}

	/**
	 * 생성자를 PLM, SAP구분하여 리턴
	 * 
	 * @return
	 */
	public String getCreator() {
		return StringUtil.checkNull(creator);
	}

	/**
	 * 생성일자를 PLM, SAP구분하여 리턴
	 * 
	 * @return
	 */
	public String getCreateDate() {
		return StringUtil.checkNull(createDate);
	}

	/**
	 * 수정자를 PLM, SAP구분하여 리턴
	 * 
	 * @return the modifier
	 */
	public String getModifier() {
		return StringUtil.checkNull(modifier);
	}

	/**
	 * 수정일자를 PLM, SAP구분하여 리턴
	 * 
	 * @return the modifyDate
	 */
	public String getModifyDate() {

		return StringUtil.checkNull(modifyDateTypeA);
	}

	/**
	 * 수정일자를 PLM, SAP구분하여 리턴
	 * 
	 * @return the modifyDate
	 */
	public String getModifyDate(final String type) {

		return modifyDateTypeD;
	}

	/**
	 * 결재진행상태를 리턴
	 * 
	 * @return the approvalState
	 */
	public String getApprovalState() {
		if (approvalState != null) {
			approvalState = PartHelper.manager.getLatestMapprovalState(this.part);
		}
		return approvalState;
	}

	/* Narae add */
	/**
	 * 도번 쳬계 NS-P1-3010-00001
	 * 
	 * @return
	 */
	// *N*/
	public String getItemType() {
		return "PLM";
	}

	public String getGroup() {

		return number.substring(0, 1);
	}

	// *S*/
	public String getType() {

		return number.substring(1, 2);
	}

	// *P1 S이면 P S가 아니면 P1*/
	public String getpUnit() {
		if (this.getType().equals("S")) {
			return number.substring(3, 4);
		} else {
			return number.substring(3, 5);
		}
	}

	// *P1 S이면 1 S가 아니면 없음*/
	public String getClass1() {

		if (this.getType().equals("S")) {
			return number.substring(4, 5);
		} else {
			return "";
		}

	}

	// *3010 A이면 3 A가 아니면 30 */
	public String getClass2() {
		if (this.getType().equals("A")) {
			return number.substring(6, 7);
		} else {
			return number.substring(6, 8);
		}

	}

	// *3010 */
	public String getClass3() {

		if (this.getType().equals("A")) {
			return number.substring(7, 10); // 010
		} else if (this.getType().equals("S")) {
			return number.substring(8, 9); // 1
		} else {
			return number.substring(8, 10); // 10
		}
	}

	// *3010 */
	public String getClass4() {
		if (this.getType().equals("S")) {
			return number.substring(9, 10); // 0
		} else if (this.getType().equals("B")) {
			return class4;
		} else {
			return "";
		}
	}

	public boolean isSelectEO() {
		Vector<EChangeOrder2> vec = PartSearchHelper.getPartEoWorking(this.part);
		boolean isSelect = true;

		// System.out.println("PartDAta :::::::::::::::::::: eco size "+vec.size());
		try {
			if (vec.size() > 0)
				isSelect = false;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return isSelect;
	}
}
