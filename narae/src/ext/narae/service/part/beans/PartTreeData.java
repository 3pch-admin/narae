/*
 * @(#) PartData.java  Create on 2004. 12. 9.
 * Copyright (c) e3ps. All rights reserverd
 */
package ext.narae.service.part.beans;

import java.sql.SQLException;
import java.util.ArrayList;

import ext.narae.util.iba.IBAUtil;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;

public class PartTreeData implements java.io.Serializable {
	public int level;
	public WTPart part;
	public WTPartUsageLink link;
	public ArrayList children = new ArrayList();
	public String unit = "";
	public double quantity = 1;
	public String itemSeq = "";
//    public SapBomBean bean;
	public String lineImg = "joinbottom";
	public String number = "";
	public String name = "";
	public String plant = "";
	public String version = "";
	public double baseQuantity = 1;
	public String plmCode = "";
	public String maker = "";
	public String spec = "";
	public String purpose = "";
	public PartTreeData sap;

	public String oid = "";
	public String lifecycle = "";
	public String creator = "";
	public String createdate = "";
	public String modifydate = "";

	public PartTreeData(WTPart part, WTPartUsageLink link, int level) {
		this.part = part;
		this.link = link;
		this.level = level;

		PartData data = new PartData(part);

		number = data.getNumber();
		name = data.getName();

		// plant = data.getPlant();
		plant = part.getNumber().lastIndexOf("_") > -1
				? part.getNumber().substring(part.getNumber().lastIndexOf("_") + 1)
				: "";

		version = data.getVersion();

		oid = data.getOid();
		lifecycle = data.getLifecycle();
		creator = data.getCreator();
		createdate = data.getCreateDate().substring(0, 10);
		modifydate = data.getModifyDate("D");

		if (link != null) {
			double qs = (double) link.getQuantity().getAmount();
			unit = link.getQuantity().getUnit().toString();
			quantity = qs;
			try {
				itemSeq = IBAUtil.getAttrValue(link, "ItemSeq");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		try {
			// baseQuantity = Double.parseDouble(IBAUtil.getAttrValue(part,"STD_QUANTITY"));
		} catch (Exception ex) {
		}

		// if(baseQuantity==0)
		baseQuantity = 1;

		maker = data.getMaker();
		spec = data.getSpec();

		try {

			if (!data.getVersion().equals("A"))
				purpose = PartSearchHelper.getLastECO((WTPartMaster) part.getMaster());
		} catch (SQLException e) {

			e.printStackTrace();
		}
	}

//    public PartTreeData(SapBomBean bean,int level){
//    	this.bean = bean;
//    	this.level = level;
//    	this.number = bean.getIDNRK(); //구성부품
//    	this.name = bean.getOJTXP(); //구성부품명
//    	this.unit = bean.getMEINS();	//기본단위
//    	this.quantity = Double.parseDouble(bean.getMENGE());		//구성부품수량
//    	this.itemSeq = bean.getPOSNR(); // 품목번호
//    	this.plant = bean.getWERKS(); // 플랜트
//    	this.baseQuantity = Double.parseDouble(bean.getXMENG());
//    	if(baseQuantity==0)baseQuantity = 1;
//    }

	public boolean compare(PartTreeData dd) {
		if (!unit.equals(dd.unit)) {
			return false;
		}
		if (quantity != dd.quantity) {
			return false;
		}
		if (baseQuantity != dd.baseQuantity) {
			return false;
		}
		return true;
	}

	public boolean equals(PartTreeData pd) {
		if (pd == null)
			return false;
		if (pd.number.equals(number) && pd.plant.equals(plant) && pd.itemSeq.equals(itemSeq)) {
			return true;
		}
		return false;
	}

}
