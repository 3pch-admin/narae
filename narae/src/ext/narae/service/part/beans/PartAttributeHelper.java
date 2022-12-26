package ext.narae.service.part.beans;

import java.sql.ResultSet;
import java.util.HashMap;

import ext.narae.service.erp.beans.ERPSearchHelper;
import ext.narae.util.StringUtil;
import ext.narae.util.code.beans.CodeHelper;
import ext.narae.util.iba.IBAUtil;
import wt.iba.value.IBAHolder;
import wt.part.WTPart;

public class PartAttributeHelper {

	// private HashMap ibaAttr = IBAUtil.getAttributes(part);
	private String Maker = "Maker";

	/**
	 * Part Attribute Maker ERP Table Code
	 * 
	 * @param part
	 * @return
	 */
	public static String getMakerCode(WTPart part) {
		String maker = "";
		String makerValue = "";
		try {
			maker = IBAUtil.getAttrValue(part, "Maker");
			maker = maker.trim();

			if (maker != null && maker.length() > 0) {
				HashMap map = new HashMap();
				map.put("makerName", maker);
				map.put("searchType", "equals");
				ResultSet rs = ERPSearchHelper.manager.getErpMaker(map);

				while (rs.next()) {
					makerValue = (String) rs.getObject("Maker");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return maker;
		// String maker = data.getMaker()==null?"":data.getMaker();

	}

	/**
	 * Part maker code
	 * 
	 * @param maker
	 * @return
	 */
	public static String getMakerCode(String maker) {

		String makerCode = "";
		try {
			HashMap map = new HashMap();
			map.put("makerName", maker);
			map.put("searchType", "equals");
			ResultSet rs = ERPSearchHelper.manager.getErpMaker(map);

			while (rs.next()) {
				makerCode = (String) rs.getObject("Maker");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return makerCode;

	}

	/**
	 * Object IBA Maker GET
	 * 
	 * @param holder
	 * @return
	 */
	public static String getIBAMaker(IBAHolder holder) {

		String maker = "";
		try {
			maker = IBAUtil.getAttrValue(holder, "Maker");
			maker = StringUtil.checkNull(maker.trim());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return maker;
	}

	/**
	 * Part Spec
	 * 
	 * @param spec
	 * @return
	 */
	public static String getSpecCode(String spec) {

		try {

			if (spec.length() > 0) {
				spec = CodeHelper.manager.getCode("SPEC", spec);
			} else {
				spec = "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return spec;
	}

	public static String getSpecCode(WTPart part) {

		String spec = "";
		try {
			spec = IBAUtil.getAttrValue(part, "Spec");
			if (spec.length() > 0) {
				spec = CodeHelper.manager.getCode("SPEC", spec);
			} else {
				spec = "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return spec;
	}

	/**
	 * Object IBA Spec GET
	 * 
	 * @param holder
	 * @return
	 */
	public static String getIBASpec(IBAHolder holder) {

		String spec = "";
		try {
			spec = IBAUtil.getAttrValue(holder, "Spec");
			spec = StringUtil.checkNull(spec.trim());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return spec;
	}

}
