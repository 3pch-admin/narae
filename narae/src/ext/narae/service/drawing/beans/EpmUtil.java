package ext.narae.service.drawing.beans;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.logging.log4j.Logger;

import com.ptc.wvs.server.util.PublishUtils;

import ext.narae.service.erp.EPMPDFLink;
import ext.narae.service.erp.ERPHistory;
import ext.narae.service.erp.beans.ERPECOHelper;
import ext.narae.service.erp.beans.ERPSearchHelper;
import ext.narae.service.erp.beans.ERPUtil;
import ext.narae.service.part.beans.PartSearchHelper;
import ext.narae.util.CommonUtil;
import ext.narae.util.SequenceDao;
import ext.narae.util.StringUtil;
import ext.narae.util.code.NumberCode;
import ext.narae.util.code.NumberCode2;
import ext.narae.util.code.beans.NumberCodeHelper;
import ext.narae.util.content.FileDown;
import ext.narae.util.iba.IBAUtil;
import ext.narae.util.obj.ObjectUtil;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.structure.EPMReferenceLink;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.log4j.LogR;
import wt.org.WTUser;
import wt.part.QuantityUnit;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.representation.Representation;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.vc.VersionControlHelper;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;

public class EpmUtil {
	private static String autoNumber = "autoNumber";
	private static final Logger logger = LogR.getLoggerInternal(EpmUtil.class.getName());
	private static EPMReNameLog log = new EPMReNameLog();

	/* WTPART 속성 Check */
	public static void checkPartAttribute(EPMDocument epm) {
		try {

			WTPart part = DrawingHelper.manager.getWTPart(epm);
			if (part == null)
				return;

			checkPartAttribute(part, epm);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/* WTPART 속성 Check */
	public static void checkPartAttribute(WTPart part) {
		try {
			EPMDocument epm = DrawingHelper.manager.getEPMDocument(part);

			checkPartAttribute(part, epm);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* W/G WTPART 속성 Check */
	public static void checkPartAttribute(WTPart part, EPMDocument epm) {

		try {

			if (!epm.getOwnerApplication().toString().equals("EPM")
					|| epm.getAuthoringApplication().toString().equals("ACAD"))
				return;

			if (!epm.getVersionIdentifier().getSeries().getValue().equals("A"))
				return;

			HashMap ibaAttr = IBAUtil.getAttributes(epm);
			String quantityunit = (ibaAttr != null) ? StringUtil.trim((String) ibaAttr.get("quantityunit")) : "";
			String autoNumberValue = (ibaAttr != null) ? StringUtil.trim((String) ibaAttr.get("message")) : "";

			if (quantityunit.length() > 0) {
				quantityunit = quantityunit.toLowerCase();
				QuantityUnit qu = QuantityUnit.toQuantityUnit(quantityunit);
				if (qu == null) {
					autoNumberValue = autoNumberValue + "(" + quantityunit + ")입력하신 단위의 정보를 찾을 수 없습니다 ,";
				} else {
					try {

						WTPartMaster master = (WTPartMaster) part.getMaster();
						master.setDefaultUnit(QuantityUnit.toQuantityUnit(quantityunit));
						PersistenceHelper.manager.modify(master);

					} catch (Exception e) {
						System.out.println("::::::::::::::::::::::::: WTPart ERROR1::::::::::::::::::::::::: ");
					}

				}
			} else {

				autoNumberValue = autoNumberValue + "단위를 입력 하지 않았습니다 ,";
			}

			IBAUtil.changeIBAValue(epm, "message", autoNumberValue);

		} catch (Exception e) {
			System.out.println("::::::::::::::::::::::::: WTPart ERROR2::::::::::::::::::::::::: ");
		}

	}

	public static void changeDrawing(EPMDocumentMaster master) {

		try {

			EPMDocument epm = EpmSearchHelper.manager.getLastEPMDocument(master);
			String version = epm.getVersionIdentifier().getValue();
			if (!version.equals("A") || !epm.getOwnerApplication().toString().equals("EPM"))
				return;
			if (epm.getDocType().toString().equals("CADDRAWING") || epm.getDocType().toString().equals("FORMAT"))
				return;

			EPMDocument epm2D = EpmSearchHelper.manager.getEPM2D((EPMDocumentMaster) epm.getMaster());

			HashMap ibaAttr = IBAUtil.getAttributes(epm);
			String isAutoNumber = (ibaAttr != null) ? StringUtil.trim((String) ibaAttr.get(autoNumber)) : "";

			if (!isAutoNumber.equals("TRUE"))
				return;

			String pdmNumber = epm.getNumber();
			String pdmName = epm.getName();
			if (epm2D == null)
				return;

			HashMap ibaAttr2D = IBAUtil.getAttributes(epm2D);
			isAutoNumber = (ibaAttr != null) ? StringUtil.trim((String) ibaAttr2D.get(autoNumber)) : "";

			if (epm2D.getNumber().startsWith(epm.getNumber()))
				return;

			String cadExtension = getCadExtension(epm2D.getCADName());
			String pdmNumber2D = pdmNumber + "_2D";
			boolean isNumber2D = CadInfoChange.manager.epmCadInfoChange(epm2D, pdmNumber2D, pdmName,
					pdmNumber + cadExtension);

			if (isNumber2D) {
				IBAUtil.changeIBAValue(epm2D, autoNumber, "TRUE");
			} else {
				IBAUtil.changeIBAValue(epm2D, autoNumber, "FALSE");
			}

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	public static void changeDrawing(EPMDocument epm) {

		try {

//			System.out.println(":::::::::::::: changeDrawing epm:::::::::::::::::");
			/*
			 * 2D Search Drawing Number, Name,CadName Change WG ,A Version,CADDRAWING 이면
			 * Return
			 */
			String version = epm.getVersionIdentifier().getValue();
			if (!version.equals("A") || !epm.getOwnerApplication().toString().equals("EPM"))
				return;
			if (epm.getDocType().toString().equals("CADDRAWING") || epm.getDocType().toString().equals("FORMAT"))
				return;
			HashMap ibaAttr = IBAUtil.getAttributes(epm);
			String isAutoNumber = (ibaAttr != null) ? StringUtil.trim((String) ibaAttr.get(autoNumber)) : "";

			if (!isAutoNumber.equals("TRUE"))
				return;
			EPMDocument epm2D = EpmSearchHelper.manager.getEPM2D((EPMDocumentMaster) epm.getMaster());

			String pdmNumber = epm.getNumber();
			String pdmName = epm.getName();
			if (epm2D == null)
				return;

			HashMap ibaAttr2D = IBAUtil.getAttributes(epm2D);
			isAutoNumber = (ibaAttr != null) ? StringUtil.trim((String) ibaAttr2D.get(autoNumber)) : "";

			if (epm2D.getNumber().startsWith(epm.getNumber()))
				return;

			String cadExtension = getCadExtension(epm2D.getCADName());
			String pdmNumber2D = pdmNumber + "_2D";
			boolean isNumber2D = CadInfoChange.manager.epmCadInfoChange(epm2D, pdmNumber2D, pdmName,
					pdmNumber + cadExtension);

			if (isNumber2D) {
				IBAUtil.changeIBAValue(epm2D, autoNumber, "TRUE");
			} else {
				IBAUtil.changeIBAValue(epm2D, autoNumber, "FALSE");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static String setPDMNumber(Hashtable hash) throws Exception {

		String group = (String) hash.get("group");
		String type = (String) hash.get("type");
		String unit = (String) hash.get("unit");
		String class1 = (String) hash.get("class1");
		String class2 = (String) hash.get("class2");
		String class3 = (String) hash.get("class3");
		String class4 = (String) hash.get("class4");
		String userName = (String) hash.get("userName");

		String number = "";
		number = group + type + "-" + unit;

		if (!(group.length() > 0 && type.length() > 0 && unit.length() > 0))
			return "";

		if (type.equals("S")) {
			if (!(class1.length() > 0 && class2.length() > 0 && class3.length() > 0))
				return "";
			number = number + class1 + "-" + class2 + class3 + class4;
		} else if (type.equals("A")) {
			if (class1.length() == 0)
				return "";
			number = number + "-" + class1 + class2;
		} else if (type.equals("P") || type.equals("B")) {
			if (!(class1.length() > 0 && class2.length() > 0))
				return "";
			number = number + "-" + class1 + class2;
		}

		return number;
	}

	public static String getPDMCodeName(String pdmCode) {
		String pdmCodeName = "";

		if (pdmCode != null) {

			String group = pdmCode.substring(0, 1);
			NumberCode groupCode = NumberCodeHelper.manager.getNumberCode("CADCREATOR", group);
			if (groupCode != null)
				pdmCodeName = groupCode.getName();

			String type = pdmCode.substring(1, 2);
			HashMap typeMap = new HashMap();
			typeMap.put("type", "CADATTRIBUTE");
			typeMap.put("code", type);
			typeMap.put("isParent", "false");

			ArrayList typeArray = null;
			try {
				typeArray = NumberCodeHelper.manager.getNumberCode(typeMap);
				if (typeArray.size() > 0) {
					NumberCode typeCode = (NumberCode) typeArray.get(0);
					pdmCodeName = pdmCodeName + "_" + typeCode.getName();

					if ("S".equals(typeCode.getCode())) {
						String unit = pdmCode.substring(3, 4);
						NumberCode unitCode = NumberCodeHelper.manager.getNumberCode("SBUSINESS", unit);
						if (unitCode != null) {
							pdmCodeName = pdmCodeName + "_" + unitCode.getName();

							String class1 = pdmCode.substring(4, 5);
							NumberCode class1Code = NumberCodeHelper.manager.getNumberCode("SCUSTOMER", class1);
							if (class1Code != null) {
								pdmCodeName = pdmCodeName + "_" + class1Code.getName();

								String class2 = pdmCode.substring(6, 8);
								NumberCode class2Code = NumberCodeHelper.manager.getNumberCode("SCLASS1", class2);
								if (class2Code != null) {
									pdmCodeName = pdmCodeName + "_" + class2Code.getName();

									String class3 = pdmCode.substring(8, 9);
									NumberCode class3Code = NumberCodeHelper.manager.getNumberCode("SCLASS2", class3);
									if (class3Code != null) {
										pdmCodeName = pdmCodeName + "_" + class3Code.getName();

										String class4 = pdmCode.substring(9, 10);
										NumberCode class4Code = NumberCodeHelper.manager.getNumberCode("SCLASS3",
												class4);
										if (class4Code != null) {
											if (!"Non".equals(class4Code.getName()))
												pdmCodeName = pdmCodeName + "_" + class4Code.getName();
										}
									}
								}
							}
						}
					} else {
						String unit = pdmCode.substring(3, 5);
						HashMap map = new HashMap();
						map.put("type", "CADATTRIBUTE");
						map.put("code", unit);
						map.put("parent", typeCode);

						ArrayList unitArray = NumberCodeHelper.manager.getNumberCode(map);
						if (unitArray.size() > 0) {
							NumberCode unitCode = (NumberCode) unitArray.get(0);
							pdmCodeName = pdmCodeName + "_" + unitCode.getName();

							if ("A".equals(typeCode.getCode())) {
								String class1 = pdmCode.substring(6, 7);
								map.put("code", class1);
								map.put("parent", unitCode);

								ArrayList class1Array = NumberCodeHelper.manager.getNumberCode(map);
								if (class1Array.size() > 0) {
									NumberCode class1Code = (NumberCode) class1Array.get(0);
									if (!"Non".equals(class1Code.getName()))
										pdmCodeName = pdmCodeName + "_" + class1Code.getName();
								}
								String class2 = pdmCode.substring(7, 10);
								pdmCodeName = pdmCodeName + "_" + class2;

							} else if ("B".equals(typeCode.getCode()) || "P".equals(typeCode.getCode())) {
								String class1 = pdmCode.substring(6, 8);
								map.put("code", class1);
								map.put("parent", unitCode);

								ArrayList class1Array = NumberCodeHelper.manager.getNumberCode(map);
								if (class1Array.size() > 0) {
									NumberCode class1Code = (NumberCode) class1Array.get(0);
									if (!"Non".equals(class1Code.getName()))
										pdmCodeName = pdmCodeName + "_" + class1Code.getName();

									String class2 = pdmCode.substring(8, 10);
									map.put("code", class2);
									map.put("parent", class1Code);
									ArrayList class2Array = NumberCodeHelper.manager.getNumberCode(map);
									if (class2Array.size() > 0) {
										NumberCode class2Code = (NumberCode) class2Array.get(0);
										if (!"Non".equals(class2Code.getName()))
											pdmCodeName = pdmCodeName + "_" + class2Code.getName();
									}
								}
							}
						}
					}
				}
			} catch (WTException e) {
				e.printStackTrace();
				return null;
			}
		}
		return pdmCodeName;
	}

	public static Hashtable checkPDMName(Hashtable hash) {
		Hashtable returnValue = new Hashtable();
		returnValue.put("numberCheck", "false");

		String group = (String) hash.get("group");
		group = StringUtil.checkNull(group);
		NumberCode groupCode = null;

		if (group.length() > 0) {
			groupCode = NumberCodeHelper.manager.getNumberCode("CADCREATOR", group);
		}

		if (groupCode == null) {
			returnValue.put("nameValue", "입력하신 Group에 해당하는 code값이 없습니다.");
			log.infoLog(" <br> [checkPDMName] groupCode IS NULL : 입력하신 Group에 해당하는 code값이 없습니다. ");
		} else {
			String type = (String) hash.get("type");
			type = StringUtil.checkNull(type);
			HashMap typeMap = new HashMap();
			typeMap.put("type", "CADATTRIBUTE");
			typeMap.put("code", type);
			typeMap.put("isParent", "false");

			ArrayList typeArray = null;
			try {
				if (type.length() > 0) {
					typeArray = NumberCodeHelper.manager.getNumberCode(typeMap);
				}

			} catch (WTException e1) {

				log.infoLog(" <br> [checkPDMName] typeArray WTException : " + e1.getMessage());
				e1.printStackTrace();
			}

			if (typeArray == null || typeArray.size() == 0) {
				returnValue.put("nameValue", "입력하신 Type에 해당하는 code값이 없습니다.");
				log.infoLog(" <br> [checkPDMName] typeArray is null or size =0  typeArray =  " + typeArray);
			} else {
				NumberCode typeCode = (NumberCode) typeArray.get(0);

				String unit = StringUtil.checkNull((String) hash.get("unit"));
				String class1 = StringUtil.checkNull((String) hash.get("class1"));
				String class2 = StringUtil.checkNull((String) hash.get("class2"));
				String class3 = StringUtil.checkNull((String) hash.get("class3"));
				String class4 = StringUtil.checkNull((String) hash.get("class4"));

				if ("S".equals(typeCode.getCode())) {

					log.infoLog(
							" <br> [checkPDMName] :JS getProductSub typeCode.getCode() is S  START ===============");
					NumberCode unitCode1 = null;
					if (unit.length() == 2) {
						String utit1 = unit.substring(0, 1);
						// NumberCodeHelper.manager.getNumberCode("SBUSINESS", unit);
						// unitCode Check 로직 반영... suk 2013.02.14
						unitCode1 = NumberCodeHelper.manager.getNumberCode("SBUSINESS", utit1);
						log.infoLog(" <br>unitCode1 = " + unitCode1.getName() + ":" + unitCode1.getCode() + ":"
								+ CommonUtil.getOIDLongValue(unitCode1));
					} else {
						returnValue.put("nameValue", "입력하신 Unit은 2자리가 아닙니다.");
						log.infoLog(" <br> [checkPDMName] :  입력하신 Unit은 2자리가 아닙니다. ");
					}

					NumberCode unitCode2 = null;

					if (unitCode1 == null) {
						returnValue.put("nameValue", "입력하신 Unit1에 해당하는 code값이 없습니다.");
						log.infoLog(" <br> [checkPDMName] :  입력하신 Unit1에 해당하는 code값이 없습니다. ");
					} else {
						String utit2 = unit.substring(1, 2);
						// NumberCodeHelper.manager.getNumberCode("SBUSINESS", unit);
						// unitCode Check 로직 반영... suk 2013.02.14
						unitCode2 = NumberCodeHelper.manager.getNumberCode("SCUSTOMER", utit2);
						log.infoLog(" <br>unitCode2 = " + unitCode2.getName() + ":" + unitCode2.getCode() + ":"
								+ CommonUtil.getOIDLongValue(unitCode2));
						if (unitCode2 == null) {
							returnValue.put("nameValue", "입력하신 Unit2에 해당하는 code값이 없습니다.");
							log.infoLog(" <br> [checkPDMName] :  입력하신 Unit2에 해당하는 code값이 없습니다. ");
						} else {
							NumberCode class1Code = NumberCodeHelper.manager.getNumberCode("SCLASS1", class1);
							if (class1Code == null) {
								returnValue.put("nameValue", "입력하신 Class-1에 해당하는 code값이 없습니다.");
								log.infoLog(" <br> [checkPDMName] : 입력하신 Class-1에 해당하는 code값이 없습니다. ");
							} else {
								log.infoLog(" <br>class1Code = " + class1Code.getName() + ":" + class1Code.getCode()
										+ ":" + CommonUtil.getOIDLongValue(class1Code));
								// NumberCode class2Code = NumberCodeHelper.manager.getNumberCode("SCLASS1",
								// class2);
								NumberCode class2Code = NumberCodeHelper.manager.getNumberCode("SCLASS2", class2);
								if (class2Code == null) {
									returnValue.put("nameValue", "입력하신 Class-2에 해당하는 code값이 없습니다.");
									log.infoLog(" <br> [checkPDMName] : 입력하신 Class-2에 해당하는 code값이 없습니다.. ");
								} else {
									log.infoLog(" <br>class2Code = " + class2Code.getName() + ":" + class2Code.getCode()
											+ ":" + CommonUtil.getOIDLongValue(class2Code));
									// NumberCode class3Code = NumberCodeHelper.manager.getNumberCode("SCLASS2",
									// class3);
									NumberCode class3Code = NumberCodeHelper.manager.getNumberCode("SCLASS3", class3);
									if (class3Code == null) {
										returnValue.put("nameValue", "입력하신 Class-3에 해당하는 code값이 없습니다.");
										log.infoLog(" <br> [checkPDMName] : 입력하신 Class-3에 해당하는 code값이 없습니다. ");
									} else {
										log.infoLog(" <br>class3Code = " + class3Code.getName() + ":"
												+ class3Code.getCode() + ":" + CommonUtil.getOIDLongValue(class3Code));
										returnValue.put("numberCheck", "true");
										// String nameValue = groupCode.getName() + "_" + typeCode.getName() + "_" +
										// unitCode.getName() + "_" + class1Code.getName() + "_" + class2Code.getName()
										// + "_" +
										// class3Code.getName();
										// if( !"Non".equals(class4Code.getName()))
										// nameValue = nameValue + "_" + class4Code.getName();
										String nameValue = class1Code.getName();

										returnValue.put("nameValue", nameValue);

										String numberValue = groupCode.getCode() + typeCode.getCode() + "-"
												+ unitCode1.getCode() + unitCode2.getCode() + "-" + class1Code.getCode()
												+ class2Code.getCode() + class3Code.getCode();

										returnValue.put("numberValue", numberValue);

										log.infoLog(" <br> [checkPDMName] : numberCheck = " + "true");
										log.infoLog(" <br> [checkPDMName] : nameValue = " + nameValue);
										log.infoLog(" <br> [checkPDMName] : numberValue = " + numberValue);
										log.infoLog(
												" <br> [checkPDMName] : typeCode.getCode() is S  END ===============");
									}
								}
							}
						}
					}
				} else {
					log.infoLog(" <br> [checkPDMName] : typeCode.getCode() is Not S  START ===============");
					try {
						String typeCodeStr = typeCode.getCode();
						log.infoLog(" <br>typeCodeStr = " + typeCodeStr);
						log.infoLog(" <br>typeCode = " + typeCode.getName() + ":" + typeCode.getCode() + ":"
								+ CommonUtil.getOIDLongValue(typeCode));
						HashMap map = new HashMap();
						if (typeCode.getCode().equals("A")) {
							map.put("type", "SCLASS1");
							map.put("code", unit);
							map.put("isParent", "false");
						} else if (typeCode.getCode().equals("P")) {
							map.put("type", "CADATTRIBUTE");
							map.put("code", unit);
							NumberCode typeCodeTem = NumberCodeHelper.manager.getNumberCode("CADATTRIBUTE", "A", true);
							log.infoLog(" <br> parent Oid = " + CommonUtil.getOIDLongValue(typeCodeTem));
							map.put("parent", typeCodeTem);
						} else {
							map.put("type", "CADATTRIBUTE");
							map.put("code", unit);
							map.put("parent", typeCode);
							log.infoLog(" <br> parent Oid = " + CommonUtil.getOIDLongValue(typeCode));
						}
						log.infoLog(" <br>unit = " + unit);
						QuerySpec qs = NumberCodeHelper.manager.getCodeQuerySpec(map);
						log.infoLog(" <br>qs = " + qs);

						ArrayList unitArray = NumberCodeHelper.manager.getNumberCode(map);
						if (unitArray == null || unitArray.size() == 0) {
							returnValue.put("nameValue", "입력하신 Unit에 해당하는 code값이 없습니다.");
							log.infoLog(
									" <br> [checkPDMName] : unitArray is null or size =0 입력하신 Unit에 해당하는 code값이 없습니다.");
						} else {
							NumberCode unitCode = (NumberCode) unitArray.get(0);
							log.infoLog(" <br>unitCode = " + unitCode.getName() + ":" + unitCode.getCode() + ":"
									+ CommonUtil.getOIDLongValue(unitCode));
							map = new HashMap();
							if (typeCode.getCode().equals("A")) {
								map.put("type", "CADATTRIBUTE");
								map.put("code", class1);
								map.put("parent", typeCode);
								log.infoLog(" <br> parent Oid = " + CommonUtil.getOIDLongValue(typeCode));
							} else if (typeCode.getCode().equals("P")) {
								map.put("code", class1);
								map.put("type", "CADATTRIBUTE");
								map.put("parent", typeCode);
								log.infoLog(" <br> parent Oid = " + CommonUtil.getOIDLongValue(typeCode));
							} else {
								map.put("type", "CADATTRIBUTE");
								map.put("code", class1);
								map.put("parent", unitCode);
								log.infoLog(" <br> parent Oid = " + CommonUtil.getOIDLongValue(unitCode));
							}

							ArrayList class1Array = NumberCodeHelper.manager.getNumberCode(map);
							qs = NumberCodeHelper.manager.getCodeQuerySpec(map);
							log.infoLog(" <br>qs = " + qs);
							if (class1Array == null || class1Array.size() == 0) {
								returnValue.put("nameValue", "입력하신 Class-1에 해당하는 code값이 없습니다.");
								log.infoLog(
										" <br> [checkPDMName] : class1Array is null or class1Array =0 입력하신 Class-1에 해당하는 code값이 없습니다.");
							} else {
								NumberCode class1Code = (NumberCode) class1Array.get(0);
								log.infoLog(" <br>class1Code = " + class1Code.getName() + ":" + class1Code.getCode()
										+ ":" + CommonUtil.getOIDLongValue(class1Code));
								map = new HashMap();
								if (typeCode.getCode().equals("A")) {
									map.put("type", "CADATTRIBUTE");
									map.put("code", class2);
									map.put("parent", class1Code);
									log.infoLog(" <br> parent Oid = " + CommonUtil.getOIDLongValue(class1Code));
								} else if (typeCode.getCode().equals("P")) {
									returnValue.put("class1Code", class1Code.getName());
									map.put("type", "CADATTRIBUTE");
									map.put("code", class2);
									map.put("parent", class1Code);
									log.infoLog(" <br> parent Oid = " + CommonUtil.getOIDLongValue(class1Code));
								} else {
									map.put("type", "CADATTRIBUTE");
									map.put("code", class2);
								}
								ArrayList class2Array = NumberCodeHelper.manager.getNumberCode(map);
								qs = NumberCodeHelper.manager.getCodeQuerySpec(map);
								log.infoLog(" <br>qs = " + qs);
								if (class2Array == null || class2Array.size() == 0) {
									returnValue.put("nameValue", "입력하신 Class-2에 해당하는 code값이 없습니다.");
									log.infoLog(
											" <br> [checkPDMName] : class2Array is null or class2Array =0 입력하신 Class-2에 해당하는 code값이 없습니다.");
								} else {
									NumberCode class2Code = (NumberCode) class2Array.get(0);
									log.infoLog(" <br>class2Code = " + class2Code.getName() + ":" + class2Code.getCode()
											+ ":" + CommonUtil.getOIDLongValue(class2Code));
									if ("A".equals(typeCode.getCode())) {
										if (class2.length() != 1) {
											returnValue.put("nameValue", "Class-2은 1자리로 입력하셔야 합니다.");
											log.infoLog(" <br> [checkPDMName] : Class-2은 1자리로 입력하셔야 합니다.");
										} else {
											if (class3.length() == 0 || class3.length() != 3) {
												returnValue.put("nameValue", "Class-3은 3자리로 입력하셔야 합니다.");
												log.infoLog(" <br> [checkPDMName] : Class-3은 3자리로 입력하셔야 합니다.");
											} else {
												returnValue.put("numberCheck", "true");
												String nameValue = unitCode.getName();
												nameValue = class2Code.getName();

												returnValue.put("nameValue", nameValue + "-" + class3);
												String numberValue = groupCode.getCode() + typeCode.getCode() + "-"
														+ unitCode.getCode() + "-" + class1Code.getCode() + class2
														+ class3;
												returnValue.put("numberValue", numberValue);

												log.infoLog(" <br> [checkPDMName] : numberCheck = " + "true");
												log.infoLog(" <br> [checkPDMName] : nameValue = " + nameValue + "-"
														+ class3);
												log.infoLog(" <br> [checkPDMName] : numberValue = " + numberValue);
											}
										} // if(class2.length() != 1){
									} else if ("B".equals(typeCode.getCode()) || "P".equals(typeCode.getCode())) {
										if (class2.length() != 2) {
											returnValue.put("nameValue", "Class-2은 2자리로 입력하셔야 합니다.");
											log.infoLog(" <br> [checkPDMName] : Class-2은 2자리로 입력하셔야 합니다.");
										} else {

											returnValue.put("numberCheck", "true");
											String nameValue = class1Code.getName();
											if ("P".equals(typeCode.getCode()) && !"Non".equals(class1Code.getName())) {
												nameValue = class2Code.getName();
											}
											String numberValue = groupCode.getCode() + typeCode.getCode() + "-"
													+ unitCode.getCode() + "-" + class1Code.getCode()
													+ class2Code.getCode();
											returnValue.put("numberValue", numberValue);
											returnValue.put("nameValue", nameValue + "-" + class3);
											log.infoLog(" <br> [checkPDMName] : numberCheck = " + "true");
											log.infoLog(" <br> [checkPDMName] : nameValue = " + nameValue);
											log.infoLog(" <br> [checkPDMName] : numberValue = " + numberValue);
										}
									} // else if( "B".equals( typeCode.getCode() ) || "P".equals( typeCode.getCode() )
										// ) {
								}
							}
						}
					} catch (WTException e) {
						log.infoLog(" <br><br> [checkPDMName] : WTException : " + e.getMessage());
						e.printStackTrace();
						return returnValue;
					}
				}
				log.infoLog(" <br> [checkPDMName] : typeCode.getCode() is Not S  END ===============");
			}

		}
		return returnValue;
	}

	public static Hashtable checkPDMNumber(Hashtable hash) throws Exception {
		Hashtable returnValue = new Hashtable();
		returnValue.put("numberCheck", "false");

		String group = StringUtil.checkNull((String) hash.get("group"));
		String type = StringUtil.checkNull((String) hash.get("type"));
		String unit = StringUtil.checkNull((String) hash.get("unit"));
		String class1 = StringUtil.checkNull((String) hash.get("class1"));
		String class2 = StringUtil.checkNull((String) hash.get("class2"));
		String class3 = StringUtil.checkNull((String) hash.get("class3"));
		String class4 = StringUtil.checkNull((String) hash.get("class4"));
		String beforeNumber = StringUtil.checkNull((String) hash.get("beforeNumber"));
		java.util.Map<String, Object> keyMap = new HashMap<String, Object>();
		keyMap.put("beforeNumber", beforeNumber);

		keyMap.put("stepType1", "Group");
		keyMap.put("key1", "CADCREATOR");
		keyMap.put("value1", group);
		keyMap.put("stepName1", "GroupCode");
		keyMap.put("checkLen1", 1);
		keyMap.put("isTop1", false);
		keyMap.put("parent1", null);

		keyMap.put("stepType2", "type");
		keyMap.put("key2", "CADTYPE");
		keyMap.put("value2", type);
		keyMap.put("stepName2", "TypeCode");
		keyMap.put("checkLen2", 1);
		keyMap.put("isTop2", false);
		keyMap.put("parent2", null);
		String gubun = group + type;
		if (type.equals("A")) {
			logger.debug("checkPDMNumber check NA Start!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			keyMap.put("runNumberCodeCheckStep", 4);
			// UNIT > 장비군 체크 로직 실행
			keyMap.put("stepType3", "UNIT(장비군)");
			keyMap.put("value3", unit);
			keyMap.put("key3", "SCLASS1");
			keyMap.put("stepName3", "UnitCode(장비군)");
			keyMap.put("checkLen3", 2);
			keyMap.put("isTop3", false);
			keyMap.put("parent3", type);
			keyMap.put("parentkey3", "CADTYPE");
			// class1 > Unit1 체크 로직 실행
			keyMap.put("stepType4", "Class1(Unit1)");
			keyMap.put("key4", "CADATTRIBUTE");
			keyMap.put("value4", class1);
			keyMap.put("stepName4", "class1Code(Unit1)");
			keyMap.put("checkLen4", 2);
			keyMap.put("isTop4", false);
			keyMap.put("parent4", unit);
			keyMap.put("parentkey4", "SCLASS1");
			// Unit2 Key 체크로직 안함
			keyMap.put("stepType5", "Class2(Unit2)");
			keyMap.put("value5", class2);
			keyMap.put("keyINType5", "String");
			keyMap.put("checkLen5", 2);
			// Sub Assy Key 체크로직 안함
			keyMap.put("stepType6", "Class3(Sub Assy)");
			keyMap.put("value6", class3);
			keyMap.put("keyINType6", "Int");
			keyMap.put("checkLen6", 2);
			returnValue.put("isAutoName", false);
			keyMap.put("isAutoName", false);
			keyMap.put("codeCount", 6);
			keyMap.put("matchNumber", "[1][2]-[3]-[4][5][6]");
			// keyMap.put("remainCodes", "value5,value6");
			keyMap.put("remainCodesCount", 2);
			keyMap.put("autoSerialNumber", "000");
			keyMap.put("groupNameidx", "4");
			returnValue.put("autoSerialNumber", "000");
			stepCheck(hash, keyMap, returnValue);
			logger.debug("checkPDMNumber check NA End!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		} else if (type.equals("P")) {
			logger.debug("checkPDMNumber check NP Start!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			keyMap.put("runNumberCodeCheckStep", 5);
			// UNIT > Spare 체크 로직 실행 00으로 쓰다가 나중에 쓰일 가능성이 높아 이렇게 개발.
			keyMap.put("stepType3", "UNIT(Spare)");
			keyMap.put("key3", "SPARE");
			keyMap.put("value3", unit);
			keyMap.put("stepName3", "UnitCode(Spare)");
			keyMap.put("checkLen3", 2);
			keyMap.put("isTop3", false);
			keyMap.put("parent3", "NP");// 이전에는 CADATTRIBUTE/A코드의 자식 코드 기준으로 가져왔지만 종속 없게 해달라고 해서 변경.
			keyMap.put("parentkey3", "GUBUN");
			// class1 체크 로직 실행 부모 종속 X
			keyMap.put("stepType4", "Class1");
			keyMap.put("key4", "CLASS1");
			keyMap.put("value4", class1);
			keyMap.put("stepName4", "class1Code");
			keyMap.put("checkLen4", 2);
			keyMap.put("isTop4", false);
			keyMap.put("parent4", "NP");// 이전에는 CADATTRIBUTE/type 코드의 자식 코드 기준으로 가져왔지만 종속 없게 해달라고 해서 변경.
			keyMap.put("parentkey4", "GUBUN");
			// class2 Key 체크로직 안함
			keyMap.put("stepType5", "Class2");
			keyMap.put("key5", "CLASS2");
			keyMap.put("value5", class2);
			keyMap.put("stepName5", "class2Code");
			keyMap.put("checkLen5", 2);
			keyMap.put("isTop5", false);
			keyMap.put("parent5", "NP");// 이전에는 CADATTRIBUTE/class1Code 코드의 자식 코드 기준으로 가져왔지만 종속 없게 해달라고 해서 변경.
			keyMap.put("parentkey5", "GUBUN");
			returnValue.put("isAutoName", false);
			keyMap.put("isAutoName", false);
			keyMap.put("codeCount", 5);
			keyMap.put("matchNumber", "[1][2]-[3]-[4][5]");
			keyMap.put("autoSerialNumber", "00000");
			// keyMap.put("groupNameidx","4");
			returnValue.put("autoSerialNumber", "00000");
			stepCheck(hash, keyMap, returnValue);
			logger.debug("checkPDMNumber check NP END!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		} else if (gubun.equals("NS")) {
			logger.debug("checkPDMNumber check NS Start!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			keyMap.put("runNumberCodeCheckStep", 7);
			// UNIT > UNIT 첫번째 자리 체크 로직 실행
			keyMap.put("stepType3", "UNIT(1/2)");
			keyMap.put("key3", "SBUSINESS");
			keyMap.put("value3", unit);
			keyMap.put("stepName3", "UnitCode");
			keyMap.put("checkLen3", 2);
			keyMap.put("isTop3", false);
			keyMap.put("parent3", "NS");
			keyMap.put("parentkey3", "GUBUN");
			// UNIT > UNIT 두번째 자리 체크 로직 실행
			keyMap.put("stepType4", "UNIT(2/2)");
			keyMap.put("key4", "SCUSTOMER");
			keyMap.put("value4", unit);
			keyMap.put("stepName4", "UnitCode");
			keyMap.put("checkLen4", 2);
			keyMap.put("isTop4", false);
			keyMap.put("parent4", "NS");
			keyMap.put("parentkey4", "GUBUN");
			// class1 체크 로직 실행
			keyMap.put("stepType5", "Class1(SCLASS1)");
			keyMap.put("key5", "SCLASS1");
			keyMap.put("value5", class1);
			keyMap.put("stepName5", "class1Code");
			keyMap.put("checkLen5", 2);
			keyMap.put("isTop5", false);
			keyMap.put("parent5", "NS");
			keyMap.put("parentkey5", "GUBUN");
			// class2 Key 체크로직 실행
			keyMap.put("stepType6", "Class2(SCLASS2)");
			keyMap.put("key6", "SCLASS2");
			keyMap.put("value6", class2);
			keyMap.put("stepName6", "class2Code");
			keyMap.put("checkLen6", 1);
			keyMap.put("isTop6", false);
			keyMap.put("parent6", "NS");
			keyMap.put("parentkey6", "GUBUN");

			// class3 Key 체크로직 실행
			keyMap.put("stepType7", "Class3(SCLASS3)");
			keyMap.put("key7", "SCLASS3");
			keyMap.put("value7", class3);
			keyMap.put("stepName7", "class3Code");
			keyMap.put("checkLen7", 1);
			keyMap.put("isTop7", false);
			keyMap.put("parent7", "NS");
			keyMap.put("parentkey7", "GUBUN");
			returnValue.put("isAutoName", true);
			keyMap.put("isAutoName", true);
			keyMap.put("autoNameidx", 5);
			keyMap.put("codeCount", 7);
			keyMap.put("groupNameidx", "3");
			keyMap.put("matchNumber", "[1][2]-[3][4]-[5][6][7]");
			keyMap.put("autoSerialNumber", "00000");

			returnValue.put("autoSerialNumber", "00000");
			stepCheck(hash, keyMap, returnValue);
			logger.debug("checkPDMNumber check NS END!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			/*
			 * }else if(gubun.equals("NB")) { keyMap.put("runNumberCodeCheckStep", 5);
			 * //UNIT 체크 로직 실행 keyMap.put("stepType3", "UNIT"); keyMap.put("key3",
			 * "CADATTRIBUTE"); keyMap.put("value3", unit); keyMap.put("stepName3",
			 * "UnitCode"); keyMap.put("checkLen3", 2); keyMap.put("isTop3", false);
			 * keyMap.put("parent3", type ); //class1 체크 로직 실행 keyMap.put("stepType4",
			 * "Class1"); keyMap.put("key4", "CADATTRIBUTE"); keyMap.put("value4", class1);
			 * keyMap.put("stepName4", "class1Code"); keyMap.put("checkLen4", 2);
			 * keyMap.put("isTop4", false); keyMap.put("parent4",unit );
			 * 
			 * //class2 Key 체크로직 안함 keyMap.put("stepType5", "Class2"); keyMap.put("key5",
			 * "CADATTRIBUTE"); keyMap.put("value5", class2); keyMap.put("stepName5",
			 * "class2Code"); keyMap.put("checkLen5", 2); keyMap.put("isTop5", false);
			 * keyMap.put("parent5",null );
			 * 
			 * keyMap.put("isAutoName", true); keyMap.put("autoNameidx",4);
			 * keyMap.put("codeCount",5 ); keyMap.put("matchNumber","[1][2]-[3]-[4][5]");
			 * keyMap.put("autoSerialNumber", "00000"); stepCheck(hash,keyMap,returnValue);
			 */
		} else {
			logger.debug("checkPDMNumber check ETC Start!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			keyMap.put("runNumberCodeCheckStep", 5);
			// UNIT 체크 로직 실행
			keyMap.put("stepType3", "UNIT");
			keyMap.put("key3", "CADATTRIBUTE");
			keyMap.put("value3", unit);
			keyMap.put("stepName3", "UnitCode");
			keyMap.put("checkLen3", 2);
			keyMap.put("isTop3", false);
			keyMap.put("parent3", type);
			keyMap.put("parentkey3", "CADTYPE");
			// class1 체크 로직 실행
			keyMap.put("stepType4", "Class1");
			keyMap.put("key4", "CADATTRIBUTE");
			keyMap.put("value4", class1);
			keyMap.put("stepName4", "class1Code");
			keyMap.put("checkLen4", 2);
			keyMap.put("isTop4", false);
			keyMap.put("parent4", unit);
			keyMap.put("parentkey4", "CADATTRIBUTE");

			// class2 Key 체크로직 안함
			keyMap.put("stepType5", "Class2");
			keyMap.put("key5", "CADATTRIBUTE");
			keyMap.put("value5", class2);
			keyMap.put("stepName5", "class2Code");
			keyMap.put("checkLen5", 2);
			keyMap.put("isTop5", false);
			keyMap.put("parent5", null);

			keyMap.put("isAutoName", true);
			keyMap.put("autoNameidx", 4);
			keyMap.put("codeCount", 5);
			keyMap.put("matchNumber", "[1][2]-[3]-[4][5]");
			keyMap.put("autoSerialNumber", "00000");
			returnValue.put("autoSerialNumber", "00000");
			returnValue.put("isAutoName", true);
			stepCheck(hash, keyMap, returnValue);
			logger.debug("checkPDMNumber check ETC END!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		}
		return returnValue;
	}

	private static boolean booleanobjectNullCheck(Object obj) {
		if (null != obj && obj instanceof Boolean)
			return (Boolean) obj;
		else
			return false;
	}

	private static int intobjectNullCheck(Object obj) {
		if (null != obj && obj instanceof Integer)
			return (int) obj;
		else
			return 0;
	}

	private static void stepCheck(Hashtable hash, java.util.Map<String, Object> keyMap, Hashtable returnValue)
			throws Exception {
		String value = "";
		String stepName = "";
		String key = "";
		String stepType = "";
		String errorMsg = "";
		int checkLen = 0;
		int codeCount = 0;
		int runNumberCodeCheckStep = 0;
		boolean errorCheck = false;
		boolean isAutoName = false;
		String groupNameidx = "";
		String beforeNumber = "";
		boolean isTop = false;
		String parentCode = null;
		NumberCode2 parent = null;
		Object[] returnObj = null;
		int idx = 1;
		// hash class1, keyMap value4
		String ncCodesV = "";
		String matchNumber = StringUtil.checkNull((String) keyMap.get("matchNumber"));
		StringBuffer sbncCodeNameV = new StringBuffer();
		StringBuffer sbncCodeV = new StringBuffer();
		int remainCodesCount = 0;
		beforeNumber = StringUtil.checkNull((String) keyMap.get("beforeNumber"));
		isAutoName = booleanobjectNullCheck(keyMap.get("isAutoName"));
		groupNameidx = StringUtil.checkNull((String) keyMap.get("groupNameidx"));
		codeCount = intobjectNullCheck(keyMap.get("codeCount"));
		remainCodesCount = intobjectNullCheck(keyMap.get("remainCodesCount"));
		do {
			logger.debug("idx=" + idx + "\trunNumberCodeCheckStep=" + runNumberCodeCheckStep);
			key = StringUtil.checkNull((String) keyMap.get("key" + idx));
			value = StringUtil.checkNull((String) keyMap.get("value" + idx));
			stepType = StringUtil.checkNull((String) keyMap.get("stepType" + idx));
			errorMsg = "입력하신 " + stepType + "에 해당하는 code값이 없습니다.";
			stepName = StringUtil.checkNull((String) keyMap.get("stepName" + idx));
			checkLen = intobjectNullCheck(keyMap.get("checkLen" + idx));
			runNumberCodeCheckStep = intobjectNullCheck(keyMap.get("runNumberCodeCheckStep"));
			logger.debug("key" + idx + "=" + key);
			logger.debug("value" + idx + "=" + value);
			logger.debug("stepType" + idx + "=" + stepType);
			// logger.debug("errorMsg"+idx+"="+errorMsg);
			logger.debug("stepName" + idx + "=" + stepName);
			logger.debug("checkLen" + idx + "=" + checkLen);
			logger.debug("runNumberCodeCheckStep=" + runNumberCodeCheckStep);

			if (stepType.contains("/")) {
				String maxLen = stepType.substring(stepType.lastIndexOf("/") + 1, stepType.lastIndexOf(")"));
				if (!maxLen.equals("" + checkLen)) {
					errorCheck = true;
					errorMsg = "입력하신 " + stepType + "에 해당하는 code값의 길이가 규칙에 어긋납니다.";
					returnValue.put("nameValue", errorMsg);
					break;
				}
				checkLen = 1;
				String didx = stepType.substring(stepType.lastIndexOf("(") + 1, stepType.lastIndexOf("/"));
				try {
					value = Character.toString(value.charAt(Integer.parseInt(didx) - 1));
				} catch (Exception fe) {
					fe.printStackTrace();
					returnValue.put("nameValue", errorMsg);
					break;
				}
			}
			isTop = booleanobjectNullCheck(keyMap.get("isTop" + idx));
			parentCode = (String) keyMap.get("parent" + idx);

			logger.debug("isTop" + idx + "=" + isTop);
			logger.debug("parentCode" + idx + "=" + parentCode);
			if (null != parentCode && parentCode.length() > 0) {
				String pkey = StringUtil.checkNull((String) keyMap.get("parentkey" + (idx)));
				String pvalue = parentCode;
				ArrayList<NumberCode2> parent1stList = NumberCodeHelper.manager.getNumberCode2List(pkey, pvalue);
				for (NumberCode2 data : parent1stList) {
					String parent2ndCode = (String) keyMap.get("parent" + (idx - 1));
					logger.debug("parent2ndCode" + idx + "=" + parent2ndCode);
					logger.debug("data.getParent()" + idx + "=" + data.getParent());
					if (null != data.getParent()) {
						logger.debug("data.getParent().getCode()" + idx + "=" + data.getParent().getCode());
						if (null != parent2ndCode && data.getParent().getCode().equals(parent2ndCode)) {
							parent = data;
							break;
						}
					}
				}
				logger.debug("parentOid" + idx + "=" + CommonUtil.getFullOIDString(parent));
			}
			errorCheck = checkStep(stepName, errorMsg, hash, key, value, checkLen, returnValue, isTop, parent,
					sbncCodeV, sbncCodeNameV);
			logger.debug("errorCheck=" + errorCheck);
			if (errorCheck) {
				break;
			}
			idx++;
			logger.debug("idx<=runNumberCodeCheckStep=" + (idx <= runNumberCodeCheckStep));

		} while (idx <= runNumberCodeCheckStep);
		returnValue.put("numberCheck", "false");
		boolean isCheckNumber = true;
		if (!errorCheck) {
			// success
			ncCodesV = sbncCodeV.toString();
			logger.debug("ncCodesV=" + ncCodesV);
			if (ncCodesV.contains(",")) {
				ncCodesV = ncCodesV.substring(0, ncCodesV.lastIndexOf(","));
				String[] datas = ncCodesV.split(",");
				logger.debug("codeCount=" + codeCount);
				logger.debug("datas=" + datas);
				logger.debug("datas length=" + datas.length);
				logger.debug("remainCodesCount=" + remainCodesCount);
				if (codeCount != 0 && null != datas && (datas.length + remainCodesCount) == codeCount) {

					for (int c = 0; c < codeCount - remainCodesCount; c++) {
						String data = datas[c];
						if (null != data && data.length() > 0) {
							logger.debug("[" + (c + 1) + "]" + " change data=" + data);
							matchNumber = matchNumber.replace("[" + (c + 1) + "]", data);
						}
					}
					for (int c = codeCount - remainCodesCount + 1; c <= codeCount; c++) {
						String data = StringUtil.checkNull((String) keyMap.get("value" + c));
						int checkLen2 = intobjectNullCheck(keyMap.get("checkLen" + idx));
						stepType = StringUtil.checkNull((String) keyMap.get("stepType" + c));
						if (null != data && data.length() == checkLen2) {
							String keyINType = StringUtil.checkNull((String) keyMap.get("keyINType" + c));

							boolean isStringCheck = Character.isDigit(data.charAt(0))
									&& Character.isDigit(data.charAt(1));
							logger.debug("c=" + c + "data=" + data);
							logger.debug("c=" + c + "checkLen2=" + checkLen2);
							logger.debug("c=" + c + "stepType=" + stepType);
							logger.debug("c=" + c + "keyINType=" + keyINType);
							logger.debug("c=" + c + "isStringCheck=" + isStringCheck);
							/*
							 * if(keyINType.equals("String")) { if(!isStringCheck) { isCheckNumber = false;
							 * returnValue.put("nameValue", stepType+""); } }else
							 * if(keyINType.equals("Int")) {
							 */
							if (!isStringCheck && keyINType.equals("Int")) {
								isCheckNumber = false;
								returnValue.put("nameValue", stepType + "::: 숫자로만 구성된 코드입니다.");
								logger.debug(
										"[" + (c) + "]" + " change data=" + data + stepType + "::: 숫자로만 구성된 코드입니다.");
							}
							// }
							logger.debug("[" + (c) + "]" + " change data=" + data);
							matchNumber = matchNumber.replace("[" + (c) + "]", data);
						} else {
							isCheckNumber = false;
							returnValue.put("nameValue", stepType + "::: 코드 길이가 맞지 않거나 해당코드가 없습니다.");
						}
					}

					if (isCheckNumber) {
						String autoSerialNumber = StringUtil.checkReplaceStr(((String) keyMap.get("autoSerialNumber")),
								"");
						log.infoLog(" <br> [checkPDMNNumber] : matchNumber ===============" + matchNumber);
						returnValue.put("numberCheck", "true");

						logger.debug("matchNumber=" + matchNumber);
						if (autoSerialNumber.length() == 0) {
							returnValue.put("numberCheck", "false");
							returnValue.put("nameValue", "SerialNumber 없음. 관리자 문의");
						} else if (autoSerialNumber.length() > 0) {
							logger.debug("autoSerialNumber=" + autoSerialNumber);

							logger.debug("before matchNumber=" + matchNumber);
							String aftermatchNumber = matchNumber + "-"
									+ getPdmSerialNumber2(matchNumber, autoSerialNumber);
							if (beforeNumber.startsWith(matchNumber)) {
								aftermatchNumber = beforeNumber;
							}
							logger.debug("beforeNumber=" + beforeNumber);
							logger.debug("after matchNumber=" + aftermatchNumber);
							returnValue.put("autoSerialNumber", autoSerialNumber);
							returnValue.put("numberValue", aftermatchNumber);
						}
						logger.debug("groupNameidx=" + groupNameidx);
						if (null != groupNameidx && groupNameidx.length() > 0) {
							int groupNameint = Integer.parseInt(groupNameidx);
							if (sbncCodeNameV.toString().contains(",")) {
								logger.debug("sbncCodeNameV=" + sbncCodeNameV.toString());
								String[] names = sbncCodeNameV.toString().split(",");
								String dat2 = names[groupNameint - 1];
								logger.debug("dat2=" + dat2);
								if (null != dat2) {
									returnValue.put("class1Code", names[groupNameint - 1]);
								} else {
									returnValue.put("numberCheck", "false");
									returnValue.put("nameValue", "class1Code 없음. 관리자 문의");
								}
							}
						}
						if (isAutoName) {
							int autoNameidx = intobjectNullCheck(keyMap.get("autoNameidx"));
							logger.debug("autoNameidx=" + autoNameidx);
							String autoName = null;
							if (sbncCodeNameV.toString().contains(",")) {
								logger.debug("sbncCodeNameV=" + sbncCodeNameV.toString());
								String[] names = sbncCodeNameV.toString().split(",");
								autoName = names[autoNameidx - 1];
							}
							logger.debug("autoName=" + autoName);
							logger.debug("matchNumber=" + matchNumber);
							log.infoLog(" <br> [checkPDMNNumber] : matchNumber ===============" + matchNumber);
							returnValue.put("nameValue", autoName);
						}
					}

				}
			}
		}

	}

	private static boolean checkStep(String stepName, String errorMsg, Hashtable hash, String key, String value,
			int checkLen, Hashtable returnValue, boolean isTop, NumberCode2 parent, StringBuffer sbncCodeV,
			StringBuffer sbncCodeNameV) {
		boolean errorCheck = false;
		NumberCode2 checkCode = null;
		returnValue.put("result", true);

		log.infoLog(" <br> key = " + key);
		logger.debug(" <br> key = " + key);

		log.infoLog(" <br> value = " + value);
		logger.debug(" <br> value = " + value);

		log.infoLog(" <br> parent = " + parent);
		logger.debug(" <br> parent = " + parent);

		log.infoLog(" <br> isTop = " + isTop);
		logger.debug(" <br> isTop = " + isTop);

		if (value.length() > 0) {
			if (!isTop) {
				if (null != parent) {
					checkCode = NumberCodeHelper.manager.getNumberCode2(key, value, parent);
				} else {
					checkCode = NumberCodeHelper.manager.getNumberCode2(key, value);
				}
			} else {
				checkCode = NumberCodeHelper.manager.getNumberCode2(key, value, isTop);
			}
		}

		log.infoLog("checkCode = " + checkCode);
		if (null == checkCode) {
			returnValue.put("nameValue", errorMsg);
			returnValue.put("result", false);
			errorCheck = true;
			log.infoLog(" <br> [checkPDMNumber] " + stepName + " IS NULL : " + errorMsg);
			logger.debug(" <br> [checkPDMNumber] " + stepName + " IS NULL : " + errorMsg);
		} else if (null != checkCode && checkLen != (checkCode.getCode().length())) {
			returnValue.put("nameValue",
					stepName + " 길이가 맞지 않습니다. : " + checkLen + "\tcheckCode : " + checkCode.getCode());
			returnValue.put("result", false);
			errorCheck = true;
			log.infoLog(" <br> [checkPDMNumber] " + stepName + " 길이가 맞지 않습니다. : " + checkLen + "\tcheckCode : "
					+ checkCode.getCode());
			logger.debug(" <br> [checkPDMNumber] " + stepName + " 길이가 맞지 않습니다. : " + checkLen + "\tcheckCode : "
					+ checkCode.getCode());
		} else if (null != checkCode && checkLen == (checkCode.getCode().length())) {
			log.infoLog(" <br> [checkPDMNumber] " + stepName + " 정상. : " + checkLen + "\tcheckCode : "
					+ checkCode.getCode());
			logger.debug(" <br> [checkPDMNumber] " + stepName + " 정상. : " + checkLen + "\tcheckCode : "
					+ checkCode.getCode());
			returnValue.put("result", true);
			errorCheck = false;
			sbncCodeV.append(checkCode.getCode() + ",");
			sbncCodeNameV.append(checkCode.getName() + ",");
		}

		return errorCheck;
	}

	public static String getCadExtension(String cadName) {

		return cadName.substring(cadName.lastIndexOf("."));
	}

	public static String getPdmSerialNumber(String tempnumber) {
		String serialNum = "";
		try {
			System.out.println("tempnumber=" + tempnumber);
			String epmSerialNum = "";
			String partSerialNum = "";
			if (tempnumber.substring(1, 2).equals("A")) {
				epmSerialNum = SequenceDao.manager.getSeqNo(tempnumber + "-", "000", "EPMDocumentMaster",
						"documentNumber");
				partSerialNum = SequenceDao.manager.getSeqNo(tempnumber + "-", "000", "WTPartMaster", "WTPartNumber");
			} else {
				epmSerialNum = SequenceDao.manager.getSeqNo(tempnumber + "-", "00000", "EPMDocumentMaster",
						"documentNumber");
				partSerialNum = SequenceDao.manager.getSeqNo(tempnumber + "-", "00000", "WTPartMaster", "WTPartNumber");
			}

			System.out.println("epmSerialNum=" + epmSerialNum);
			System.out.println("partSerialNum=" + partSerialNum);
			if (Integer.parseInt(epmSerialNum) > Integer.parseInt(partSerialNum))
				serialNum = epmSerialNum;
			else
				serialNum = partSerialNum;
			System.out.println("serialNum=" + serialNum);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serialNum;
	}

	public static String getPdmSerialNumber2(String tempnumber, String autoSerialNumber) {
		String serialNum = "";
		try {
			System.out.println("tempnumber=" + tempnumber);
			String epmSerialNum = "";
			String partSerialNum = "";
			epmSerialNum = SequenceDao.manager.getSeqNo(tempnumber + "-", autoSerialNumber, "EPMDocumentMaster",
					"documentNumber");
			partSerialNum = SequenceDao.manager.getSeqNo(tempnumber + "-", autoSerialNumber, "WTPartMaster",
					"WTPartNumber");

			System.out.println("epmSerialNum=" + epmSerialNum);
			System.out.println("partSerialNum=" + partSerialNum);
			if (Integer.parseInt(epmSerialNum) > Integer.parseInt(partSerialNum))
				serialNum = epmSerialNum;
			else
				serialNum = partSerialNum;
			System.out.println("serialNum=" + serialNum);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serialNum;
	}

	public static void drawingPublish(EPMDocument epm) {

		// System.out.println(":::::::::::::: drawingPublish :::::::::::::::");
		if (epm.getAuthoringApplication().toString().equals("ACAD")) {

			autoCadPdfPublish(epm);

		} else if (epm.getAuthoringApplication().toString().equals("PROE")
				&& epm.getDocType().toString().equals("CADDRAWING")) {

			pdfFileNameChange(epm);
		}
	}

	public static void pdfFileNameChange(EPMDocument epm) {

		try {
			// System.out.println(":::::::::::::::::::::::: Pbulish START
			// ::::::::::::::::::");
			Representation representation = PublishUtils.getRepresentation(epm);
			representation = (Representation) ContentHelper.service.getContents(representation);
			Vector contentList = ContentHelper.getContentList(representation);

			EPMPDFLink link = getPDFSendList(epm);

			for (int l = 0; l < contentList.size(); l++) {
				ContentItem contentitem = (ContentItem) contentList.elementAt(l);
				if (contentitem instanceof ApplicationData) {
					ApplicationData drawAppData = (ApplicationData) contentitem;

					if (drawAppData.getRole().toString().equals("SECONDARY")
							&& drawAppData.getFileName().lastIndexOf("pdf") > 0
							&& !("N".equals(drawAppData.getDescription()))) {

						HashMap map = new HashMap();
						drawAppData.setDescription("PDF");
						drawAppData = (ApplicationData) PersistenceHelper.manager.save(drawAppData);

						if (link != null) {
							// System.out.println(":::::::::::::::::::::::: ERP PDF SEND START
							// ::::::::::::::::::");
							map.put("oid", CommonUtil.getOIDString(drawAppData));
							map.put("tempDir", link.getFolder());
							map.put("pdfFileName", link.getFileName());

							HashMap mapRe = FileDown.pdfDown(map);

							String result = (String) mapRe.get("result");
							String message = (String) mapRe.get("message");

							link.setResult(result);
							link.setMessage(message);
							link = (EPMPDFLink) PersistenceHelper.manager.modify(link);
							ERPHistory history = link.getHistory();
							history.setPdfSend(result);
							PersistenceHelper.manager.modify(history);

							/* ERP UPDate */
							EPMDocumentMaster epm3DMaster = EpmSearchHelper.manager.getEPM3D(epm);
							EPMDocument epm3D = EpmSearchHelper.manager.getLastEPMDocument(epm3DMaster);
							WTPart part = DrawingHelper.manager.getWTPart(epm3D);
							ERPECOHelper.manager.updatePDM00(part, mapRe);
							// System.out.println(":::::::::::::::::::::::: ERP PDF SEND : "
							// +epm.getNumber());
							// System.out.println(":::::::::::::::::::::::: ERP PDF SEND END
							// ::::::::::::::::::");
							// ERPHistory Update
							ERPECOHelper.manager.setHistoryResult(history);
						}
					}
				}
			}
//	        System.out.println(":::::::::::::::::::::::: Pbulish END ::::::::::::::::::");
		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	public static void autoCadPdfPublish(EPMDocument epm) {
//		System.out.println("::::::::::::::::::::::::autoCadPdfPublish  Pbulish END ::::::::::::::::::");
		try {
			String tempDir = WTProperties.getLocalProperties().getProperty("wt.temp");
			ContentItem item = null;

			byte[] buffer = new byte[1024];

			QueryResult result = ContentHelper.service.getContentsByRole((ContentHolder) epm, ContentRoleType.PRIMARY);
			while (result.hasMoreElements()) {
				item = (ContentItem) result.nextElement();
			}

			ApplicationData adata = (ApplicationData) item;
//			System.out.println(":::::::::::::: adata " + adata);
			InputStream is = ContentServerHelper.service.findContentStream(adata);
			String fileName = epm.getCADName();
			File tempfile = new File(tempDir + File.separator + fileName);
			FileOutputStream fos = new FileOutputStream(tempfile);
			int j = 0;
			while ((j = is.read(buffer, 0, 1024)) > 0)
				fos.write(buffer, 0, j);

			fos.close();
			is.close();

			EpmPublishUtil.pdfPublish(epm, tempfile);
//			System.out.println("::::::::::::::::::::::::autoCadPdfPublish Pbulish END ::::::::::::::::::");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static EPMPDFLink getPDFSendList(EPMDocument epm) {
		EPMPDFLink link = null;
		try {
			QueryResult rt = PersistenceHelper.manager.navigate(epm, "history", EPMPDFLink.class, false);

			while (rt.hasMoreElements()) {
				link = (EPMPDFLink) rt.nextElement();

				if (link.getResult().equals(ERPUtil.PDF_SEND_WAITING)) {
					return link;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return link;
	}

	public static void createEPMChange(EPMDocument epm) {

		try {
			/*
			 * PRO/E CAD Type
			 * CADDRAWING(.drw),CADASSEMBLY(.asm),CADCOMPONENT(.part),FORMAT(.frm)
			 * 채번(도번,도명,CADName), 도면 의 속성 체크 , 부품 속성 체크
			 */

			if (WorkInProgressHelper.isCheckedOut((Workable) epm)) {
				return;
			}
			if (!VersionControlHelper.isLatestIteration(epm)) {
				return;
			}
			if (epm.getDocType().toString().equals("CADDRAWING") || epm.getDocType().toString().equals("FORMAT"))
				return;
			if (!epm.getOwnerApplication().toString().equals("EPM"))
				return;

			if (!epm.getVersionIdentifier().getSeries().getValue().equals("A"))
				return;
			System.out.println(":::::::::::::::::: Cad Number : " + epm.getNumber() + ":" + epm.getName());
			log.infoLog(":::::::::::::::::: EPMDocument createEPMChange START  ::::::::::::::::::::::::");
			logger.debug(":::::::::::::::::: EPMDocument createEPMChange START  ::::::::::::::::::::::::");
			log.infoLog(":::::::::::::::::: Cad Number : " + epm.getNumber() + ":" + epm.getName());
			logger.debug(":::::::::::::::::: Cad Number : " + epm.getNumber() + ":" + epm.getName());
			wt.epm.structure.EPMReferenceLink referenceLink = null;
			boolean isNumber = false;
			String autoNumberValue = "";

			HashMap ibaAttr = IBAUtil.getAttributes(epm);
			String group = (ibaAttr != null) ? StringUtil.checkNull(StringUtil.trim((String) ibaAttr.get("Group")))
					: "";
			if (group != null && group.length() > 0)
				group = group.toUpperCase();
			String type = (ibaAttr != null) ? StringUtil.checkNull(StringUtil.trim((String) ibaAttr.get("Type"))) : "";
			if (type != null && type.length() > 0)
				type = type.toUpperCase();
			String unit = (ibaAttr != null) ? StringUtil.checkNull(StringUtil.trim((String) ibaAttr.get("Unit"))) : "";
			if (unit != null && unit.length() > 0)
				unit = unit.toUpperCase();
			String class1 = (ibaAttr != null) ? StringUtil.checkNull(StringUtil.trim((String) ibaAttr.get("Class1")))
					: "";
			if (class1 != null && class1.length() > 0)
				class1 = class1.toUpperCase();
			String class2 = (ibaAttr != null) ? StringUtil.checkNull(StringUtil.trim((String) ibaAttr.get("Class2")))
					: "";
			if (class2 != null && class2.length() > 0)
				class2 = class2.toUpperCase();
			String class3 = (ibaAttr != null) ? StringUtil.checkNull(StringUtil.trim((String) ibaAttr.get("Class3")))
					: "";
			if (class3 != null && class3.length() > 0)
				class3 = class3.toUpperCase();
			String class4 = (ibaAttr != null) ? StringUtil.checkNull(StringUtil.trim((String) ibaAttr.get("Class4")))
					: "";
			if (class4 != null && class4.length() > 0)
				class4 = class4.toUpperCase();
			String p_Name = (ibaAttr != null) ? StringUtil.checkNull(StringUtil.trim((String) ibaAttr.get("P_Name")))
					: "";
			if (p_Name != null && p_Name.length() > 0)
				p_Name = p_Name.toUpperCase();

			String isAutoNumber = (ibaAttr != null)
					? StringUtil.checkNull(StringUtil.trim((String) ibaAttr.get(autoNumber)))
					: "";
			String spec = (ibaAttr != null) ? StringUtil.checkNull(StringUtil.trim((String) ibaAttr.get("Spec"))) : "";
			String quantityunit = (ibaAttr != null)
					? StringUtil.checkNull(StringUtil.trim((String) ibaAttr.get("quantityunit")))
					: "";
			String maker = (ibaAttr != null) ? StringUtil.checkNull(StringUtil.trim((String) ibaAttr.get("Maker")))
					: "";
			Hashtable hash = new Hashtable();
			hash.put("group", group);
			hash.put("type", type);
			hash.put("unit", unit);
			hash.put("class1", class1);
			hash.put("class2", class2);
			hash.put("class3", class3);
			hash.put("class4", class4);
			hash.put("beforeNumber", epm.getNumber());

			log.infoLog("getAuthoringApplication = " + epm.getAuthoringApplication().toString());
			logger.debug("getAuthoringApplication = " + epm.getAuthoringApplication().toString());
			log.infoLog("epm.getDocType().toString()=" + epm.getDocType().toString());
			logger.debug("epm.getDocType().toString()=" + epm.getDocType().toString());
			log.infoLog("epm.isGeneric() = " + epm.isGeneric());
			logger.debug("epm.isGeneric() = " + epm.isGeneric());
			log.infoLog("epm.isInstance() = " + epm.isInstance());
			logger.debug("epm.isInstance() = " + epm.isInstance());
			log.infoLog("group = " + group);
			logger.debug("group = " + group);
			log.infoLog("type = " + type);
			logger.debug("type = " + type);
			log.infoLog("unit = " + unit);
			logger.debug("unit = " + unit);
			log.infoLog("class1 = " + class1);
			logger.debug("class1 = " + class1);
			log.infoLog("class2 = " + class2);
			logger.debug("class2 = " + class2);
			log.infoLog("class3 = " + class3);
			logger.debug("class3 = " + class3);
			log.infoLog("class4 = " + class4);
			logger.debug("class4 = " + class4);
			log.infoLog("isAutoNumber = " + isAutoNumber);
			logger.debug("isAutoNumber = " + isAutoNumber);
			log.infoLog("spec = " + spec);
			logger.debug("spec = " + spec);
			log.infoLog("quantityunit = " + quantityunit);
			logger.debug("quantityunit = " + quantityunit);
			log.infoLog("maker = " + maker);
			logger.debug("maker = " + maker);
			log.infoLog("p_Name = " + p_Name);
			logger.debug("p_Name = " + p_Name);

			System.out.println("E+++++++++++++" + epm.getAuthoringApplication().toString());
			if (epm.getAuthoringApplication().toString().equals("ACAD")) {
				System.out.println("저장.....");
				IBAUtil.changeIBAValue(epm, "Spec", spec);
				IBAUtil.changeIBAValue(epm, "Maker", maker);
			}

			/* Name Check */
			// Hashtable returnValue = checkPDMName(hash);
			Hashtable returnValue = checkPDMNumber(hash);
			// String class1Codet = (String)returnValue.get("class1Code");
			boolean isAutoName = false;
			String autoSerialNumber = null;
			if (null != returnValue.get("autoSerialNumber"))
				autoSerialNumber = (String) returnValue.get("autoSerialNumber");
			if (null != returnValue.get("isAutoName"))
				isAutoName = (boolean) returnValue.get("isAutoName");
			log.infoLog(" <br> isAutoName TEST  = " + isAutoName);
			log.infoLog(" <br> autoSerialNumber TEST  = " + autoSerialNumber);
			logger.debug(" <br> isAutoName TEST  = " + isAutoName);
			logger.debug(" <br> autoSerialNumber TEST  = " + autoSerialNumber);
			String pdmName = null;
			if (!isAutoName && null != p_Name && p_Name.length() > 0) {
				pdmName = p_Name;
			} else if (null != returnValue.get("nameValue")) {
				pdmName = (String) returnValue.get("nameValue");
			}
			String numberCheck = (String) returnValue.get("numberCheck");
			String pdmNumber = "";
			log.infoLog("numberCheck  = " + numberCheck);
			logger.debug("numberCheck  = " + numberCheck);
			logger.debug("pdmName  = " + pdmName);
			if (numberCheck.equals("false") || null == pdmName) {
				log.exceptionLog("IBAUtil.changeIBAValue  autoNumber, message = false");
				logger.error("IBAUtil.changeIBAValue  autoNumber, message = false");
				IBAUtil.changeIBAValue(epm, autoNumber, "FALSE");
				IBAUtil.changeIBAValue(epm, "message", pdmName);
				String ibaAutoNumber = IBAUtil.getAttrValue(epm, autoNumber);
				String ibamessage = IBAUtil.getAttrValue(epm, "message");
				String LatestVersionFlag = IBAUtil.getAttrValue(epm, "LatestVersionFlag");
				log.exceptionLog("epm  = " + CommonUtil.getOIDString(epm));
				log.exceptionLog("ibaAutoNumber  = " + ibaAutoNumber);
				log.exceptionLog("ibamessage  = " + ibamessage);
				log.exceptionLog("LatestVersionFlag  = " + LatestVersionFlag);
				log.exceptionLog("autoNumber = " + "FALSE");
				log.exceptionLog("message = " + pdmName);
				logger.error("epm  = " + CommonUtil.getOIDString(epm));
				logger.error("ibaAutoNumber  = " + ibaAutoNumber);
				logger.error("ibamessage  = " + ibamessage);
				logger.error("LatestVersionFlag  = " + LatestVersionFlag);
				logger.error("autoNumber = " + "FALSE");
				logger.error("message = " + pdmName);
				return;
			} else {
				pdmNumber = (String) returnValue.get("numberValue");

				if ("S".equals(type)) {
					NumberCode2 unitCode = NumberCodeHelper.manager.getNumberCode2("SBUSINESS", unit);
					if (unitCode != null) {
						IBAUtil.changeIBAValue(epm, "Group_Name", unitCode.getName());
					} else {
						autoNumberValue = autoNumberValue + "[" + unit + "] Group_Name가 존재 하지 않습니다.";
						log.exceptionLog("unitCode  is null");
						logger.error("unitCode  is null");
					}
				} else if ("B".equals(type)) {
					/*
					 * NumberCode2 paCode = NumberCodeHelper.manager.getNumberCode2("CADATTRIBUTE",
					 * type,true); HashMap map = new HashMap(); map.put("code", unit);
					 * map.put("type", "CADATTRIBUTE"); map.put("parent", paCode);
					 * 
					 * log.infoLog("code :" + unit ); log.infoLog("type :" + "CADATTRIBUTE" );
					 * log.infoLog("parent :" + paCode ); logger.debug("code :" + unit );
					 * logger.debug("type :" + "CADATTRIBUTE" ); logger.debug("parent :" + paCode );
					 * QuerySpec qs = NumberCodeHelper.getCodeQuerySpec2(map);
					 * 
					 * log.infoLog("unitCode Query :" + qs ); logger.debug("unitCode Query :" + qs
					 * ); QueryResult qr = PersistenceHelper.manager.find(qs);
					 * log.infoLog("qr.size = " +qr.size()); logger.debug("qr.size = " +qr.size());
					 * if(qr.hasMoreElements()) { Object[] obj = (Object[])qr.nextElement();
					 * NumberCode2 unitCode = (NumberCode2)obj[0]; String epmibaValue =
					 * IBAUtil.getAttrValue(epm, "Group_Name");
					 * if(!unitCode.getName().equals(epmibaValue)){
					 * logger.debug(epm.getNumber()+"\tGroup_Name IBA changeIBA start = "
					 * +unitCode.getName()); IBAUtil.changeIBAValue(epm, "Group_Name",
					 * unitCode.getName());
					 * logger.debug(epm.getNumber()+"\tGroup_Name IBA changeIBA End = "
					 * +unitCode.getName()); } }else{ autoNumberValue = autoNumberValue
					 * +"["+type+","+unit +"]Group_Name 가 존재 하지 않습니다."; }
					 */
				}
				String epmibap_Name = IBAUtil.getAttrValue(epm, "P_Name");
				if (!p_Name.equals(epmibap_Name)) {
					logger.debug(epm.getNumber() + "\tP_Name IBA changeIBA start = " + p_Name);
					IBAUtil.changeIBAValue(epm, "P_Name", p_Name);
					logger.debug(epm.getNumber() + "\tP_Name IBA changeIBA End = " + p_Name);
				}
			}

			System.out.println("++++++++++++++++type++++++++++++ " + type);
			String class1Code = (String) returnValue.get("class1Code");
			System.out.println("++++++++++++++++class1Code++++++++++++ " + class1Code);
			// 추가
			// if("A".equals(type)){
			// String class1Code = (String)returnValue.get("class1Code");
			log.infoLog("++++++++++++++++TEST++++++++++++ " + class1Code);
			System.out.println("++++++++++++++++TEST++++++++++++ " + class1Code);
			log.infoLog(" <br> class1Code TEST  = " + class1Code);
			autoNumberValue = "";
			if (null != class1Code)
				IBAUtil.changeIBAValue(epm, "Group_Name", class1Code);
			// }

			/* 중복 체크 */
			if (!epm.getAuthoringApplication().toString().equals("ACAD")) {
				if ("B".equals(type)) { // 구매품(B)일 경우에 체크
					String number = (String) hash.get("number");
					hash.put("number", pdmNumber);
					hash.put("spec", spec);
					if (spec.length() > 0) {
						Hashtable rtHas = PartSearchHelper.duplicationNumber(hash);

						String dubleCheck = StringUtil.checkNull((String) rtHas.get("return"));
						String oid = StringUtil.checkNull((String) rtHas.get("oid"));

						if (dubleCheck.equals("true")) {
							WTPart dublePart = (WTPart) CommonUtil.getObject(oid);
							autoNumberValue = autoNumberValue + dublePart.getNumber() + "와 규격이 동일합니다." + pdmNumber
									+ ",";

							IBAUtil.changeIBAValue(epm, autoNumber, "FALSE");
							IBAUtil.changeIBAValue(epm, "message", autoNumberValue);
							log.exceptionLog("autoNumber =" + "FALSE");
							log.exceptionLog("message =" + autoNumberValue);
							logger.error("autoNumber =" + "FALSE");
							logger.error("message =" + autoNumberValue);
							return;
						} else {
							NumberCode specCode = NumberCodeHelper.manager.getNumberCodeName("SPEC", spec);
							if (specCode != null) {
								IBAUtil.changeIBAValue(epm, "Class4", specCode.getDescription());
							}

						}
					} else {
						IBAUtil.changeIBAValue(epm, autoNumber, "FALSE");
						IBAUtil.changeIBAValue(epm, "message", "SPEC 을 입력 하지 않았습니다.");
						log.exceptionLog("autoNumber =" + "FALSE");
						log.exceptionLog("message =" + "SPEC 을 입력 하지 않았습니다.");
						logger.error("autoNumber =" + "FALSE");
						logger.error("message =" + "SPEC 을 입력 하지 않았습니다.");
						return;
					}
				}
			}

			/* Sequence Number */
			if (null == autoSerialNumber || (null != autoSerialNumber && autoSerialNumber.length() == 0)) {
				String serialNum = getPdmSerialNumber(pdmNumber);
				if (serialNum.length() == 0) {
					log.exceptionLog("채번시 에러가 발생 하였습니다. =" + pdmNumber);
					logger.error("채번시 에러가 발생 하였습니다. =" + pdmNumber);
					IBAUtil.changeIBAValue(epm, autoNumber, "FALSE");
					IBAUtil.changeIBAValue(epm, "message", "채번시 에러가 발생 하였습니다. =" + pdmNumber);
					return;
				}

				pdmNumber = pdmNumber + "-" + serialNum;
			}
			/* Cad Namue = Number+확장자 */
			String cadExtension = getCadExtension(epm.getCADName());
			String cadName = pdmNumber + cadExtension;
			cadName = cadName.toLowerCase();
			logger.debug("epm.isGeneric()=" + epm.isGeneric() + "\tepm.isInstance()=" + epm.isInstance());
			/*
			 * Number ,Name ,CadName Change ,FamilyTable(generic,instance) Number,Name
			 * Change
			 */
			if (epm.isGeneric() || epm.isInstance()) {
				logger.debug("CadInfoChange.manager.epmInfoChange run epm =" + epm.getNumber() + "\tpdmNumber="
						+ pdmNumber + "\tpdmName=" + pdmName);
				isNumber = CadInfoChange.manager.epmInfoChange(epm, pdmNumber, pdmName);
				logger.debug("CadInfoChange.manager.epmInfoChange run end epm =" + epm.getNumber() + "\tpdmNumber="
						+ pdmNumber + "\tpdmName=" + pdmName);
			} else {
				try {
					logger.debug("CadInfoChange.manager.epmCadInfoChange run epm =" + epm.getNumber() + "\tpdmNumber="
							+ pdmNumber + "\tpdmName=" + pdmName + "\tcadName=" + cadName);
					isNumber = CadInfoChange.manager.epmCadInfoChange(epm, pdmNumber, pdmName, cadName);
					logger.debug("CadInfoChange.manager.epmCadInfoChange run end epm =" + epm.getNumber()
							+ "\tpdmNumber=" + pdmNumber + "\tpdmName=" + pdmName + "\tcadName=" + cadName);
				} catch (Exception e) {
					log.errLog(e.getMessage());
					logger.error(e.getMessage());
					e.printStackTrace();
				}
			}

			/* CADCOMPONENT 인 경우에 */
			if (epm.getAuthoringApplication().toString().equals("PROE")
					&& epm.getDocType().toString().equals("CADCOMPONENT")) {
				// String material = (String)ibaAttr.get("Material"); //PTC_MATERIAL_NAME
				String material = (ibaAttr != null)
						? StringUtil.checkNull(StringUtil.trim((String) ibaAttr.get("Material")))
						: "";

				NumberCode mCode = null;
				if (material.length() > 0) {
					mCode = NumberCodeHelper.manager.getNumberCodeName("MATERIAL", material);
					if (mCode == null) {
						autoNumberValue = autoNumberValue + "입력하신 Material의 정보를 찾을 수 없습니다.";
						log.exceptionLog(autoNumberValue);
						logger.error(autoNumberValue);
					}
				} else {
					autoNumberValue = autoNumberValue + "Material의 정보를 입력 하지 않았습니다..";
					log.exceptionLog(autoNumberValue);
					logger.error(autoNumberValue);
				}
			}

			if (epm.getAuthoringApplication().toString().equals("ACAD")
					|| epm.getDocType().toString().equals("CADASSEMBLY")) {
				// String material = (String)ibaAttr.get("Mat"); //AutoCad,Pro/E .asm
				String material = (ibaAttr != null) ? StringUtil.checkNull(StringUtil.trim((String) ibaAttr.get("Mat")))
						: "";
				NumberCode mCode = null;
				if (material.length() > 0) {
					mCode = NumberCodeHelper.manager.getNumberCodeName("MATERIAL", material);
					if (mCode == null) {
						autoNumberValue = autoNumberValue + "입력하신 MAT 정보를 찾을 수 없습니다.";
						log.exceptionLog(autoNumberValue);
						logger.error(autoNumberValue);
					}
				}
			}

			// if(epm.getDocType().toString().equals("CADCOMPONENT") ){
			if (!epm.getAuthoringApplication().toString().equals("ACAD")) {
				if ("B".equals(type)) {
					if (spec.length() > 0) {
						NumberCode sCode = NumberCodeHelper.manager.getNumberCodeName("SPEC", spec);
						if (sCode == null) {
							autoNumberValue = autoNumberValue + "(" + spec + ")입력하신 규격의 정보를 찾을 수 없습니다 ,";
							log.exceptionLog(autoNumberValue);
							logger.error(autoNumberValue);
						}
					} else {
						autoNumberValue = autoNumberValue + "규격을  입력 하지 않았습니다 ,";
						log.exceptionLog(autoNumberValue);
						logger.error(autoNumberValue);
					}
				}

				if (maker.length() > 0) {
					HashMap erpMap = new HashMap();
					erpMap.put("makerName", maker);
					erpMap.put("searchType", "equals");
					ResultSet rs = ERPSearchHelper.manager.getErpMaker(erpMap);
					if (!rs.next()) {
						autoNumberValue = autoNumberValue + "(" + maker + ")" + "입력하신 Maker의 정보를 찾을 수 없습니다";
						log.exceptionLog(autoNumberValue);
						logger.error(autoNumberValue);
					}

				}
			}
			/* WTPART 속성 Check End */

			/* autoNumber ,message */
			if (isNumber) {
				IBAUtil.changeIBAValue(epm, autoNumber, "TRUE");
				log.infoLog("Final Number Change autoNumber = TRUE");
				logger.debug("Final Number Change autoNumber = TRUE");
			} else {
				IBAUtil.changeIBAValue(epm, autoNumber, "FALSE");
				log.infoLog("Final Number Change autoNumber = FALSE");
				logger.debug("Final Number Change autoNumber = FALSE");
			}

			IBAUtil.changeIBAValue(epm, "message", autoNumberValue);
			log.infoLog(" Final message r = " + autoNumberValue);
			logger.debug(" Final message r = " + autoNumberValue);

		} catch (Exception e) {

			log.errLog(":::::::::::::::::: EPMDocument createEPMChange Exception  START::::::::::::::::::::::::");
			logger.error(":::::::::::::::::: EPMDocument createEPMChange Exception  START::::::::::::::::::::::::");
			log.errLog(e.getMessage());
			e.printStackTrace();
			log.errLog(":::::::::::::::::: EPMDocument createEPMChange Exception  END::::::::::::::::::::::::");
			logger.error(":::::::::::::::::: EPMDocument createEPMChange Exception  END::::::::::::::::::::::::");
		}
		log.infoLog(":::::::::::::::::: EPMDocument createEPMChange END  ::::::::::::::::::::::::");
		logger.debug(":::::::::::::::::: EPMDocument createEPMChange END  ::::::::::::::::::::::::");
	}

	public static void updateEPMChange(EPMDocument epm) {
		log.infoLog(":::::::::::::::::: EPMDocument updateEPMChange START  ::::::::::::::::::::::::");
		// createEPMChange(epm);
		log.infoLog(":::::::::::::::::: EPMDocument updateEPMChange END  ::::::::::::::::::::::::");
	}

	public static void checkInEPMChange(EPMDocument epm) {

		try {
			/*
			 * PRO/E CAD Type
			 * CADDRAWING(.drw),CADASSEMBLY(.asm),CADCOMPONENT(.part),FORMAT(.frm)
			 * 채번(도번,도명,CADName), 도면 의 속성 체크 , 부품 속성 체크
			 */

			// if (WorkInProgressHelper.isCheckedOut((Workable) epm)) { return; }
			// if (!VersionControlHelper.isLatestIteration(epm)) { return; }

			if (epm.getDocType().toString().equals("CADDRAWING") || epm.getDocType().toString().equals("FORMAT"))
				return;

			String version = epm.getVersionIdentifier().getSeries().getValue();
			if (!epm.getOwnerApplication().toString().equals("EPM"))
				return;
			log.infoLog(":::::::::::::::::: EPMDocument checkInEPMChange START  ::::::::::::::::::::::::");
			logger.debug(":::::::::::::::::: EPMDocument checkInEPMChange START  ::::::::::::::::::::::::");
			log.infoLog(":::::::::::::::::: Cad Number : " + epm.getNumber() + ":" + epm.getName());
			logger.debug(":::::::::::::::::: Cad Number : " + epm.getNumber() + ":" + epm.getName());
			wt.epm.structure.EPMReferenceLink referenceLink = null;
			boolean isNumber = false;
			String autoNumberValue = "";

			HashMap ibaAttr = IBAUtil.getAttributes(epm);
			String group = (ibaAttr != null) ? StringUtil.checkNull(StringUtil.trim((String) ibaAttr.get("Group")))
					: "";
			if (group != null && group.length() > 0)
				group = group.toUpperCase();
			String type = (ibaAttr != null) ? StringUtil.checkNull(StringUtil.trim((String) ibaAttr.get("Type"))) : "";
			if (type != null && type.length() > 0)
				type = type.toUpperCase();
			String unit = (ibaAttr != null) ? StringUtil.checkNull(StringUtil.trim((String) ibaAttr.get("Unit"))) : "";
			if (unit != null && unit.length() > 0)
				unit = unit.toUpperCase();
			String class1 = (ibaAttr != null) ? StringUtil.checkNull(StringUtil.trim((String) ibaAttr.get("Class1")))
					: "";
			if (class1 != null && class1.length() > 0)
				class1 = class1.toUpperCase();
			String class2 = (ibaAttr != null) ? StringUtil.checkNull(StringUtil.trim((String) ibaAttr.get("Class2")))
					: "";
			if (class2 != null && class2.length() > 0)
				class2 = class2.toUpperCase();
			String class3 = (ibaAttr != null) ? StringUtil.checkNull(StringUtil.trim((String) ibaAttr.get("Class3")))
					: "";
			if (class3 != null && class3.length() > 0)
				class3 = class3.toUpperCase();
			String class4 = (ibaAttr != null) ? StringUtil.checkNull(StringUtil.trim((String) ibaAttr.get("Class4")))
					: "";
			if (class4 != null && class4.length() > 0)
				class4 = class4.toUpperCase();
			String p_Name = (ibaAttr != null) ? StringUtil.checkNull(StringUtil.trim((String) ibaAttr.get("P_Name")))
					: "";
			if (p_Name != null && p_Name.length() > 0)
				p_Name = p_Name.toUpperCase();

			String isAutoNumber = (ibaAttr != null)
					? StringUtil.checkNull(StringUtil.trim((String) ibaAttr.get(autoNumber)))
					: "";
			String message = (ibaAttr != null) ? StringUtil.checkNull(StringUtil.trim((String) ibaAttr.get("message")))
					: "";
			String spec = (ibaAttr != null) ? StringUtil.checkNull(StringUtil.trim((String) ibaAttr.get("Spec"))) : "";
			String maker = (ibaAttr != null) ? StringUtil.checkNull(StringUtil.trim((String) ibaAttr.get("Maker")))
					: "";
			String quantityunit = (ibaAttr != null)
					? StringUtil.checkNull(StringUtil.trim((String) ibaAttr.get("quantityunit")))
					: "";

			Hashtable hash = new Hashtable();
			hash.put("group", group);
			hash.put("type", type);
			hash.put("unit", unit);
			hash.put("class1", class1);
			hash.put("class2", class2);
			hash.put("class3", class3);
			hash.put("class4", class4);
			hash.put("beforeNumber", epm.getNumber());
			log.infoLog("getAuthoringApplication = " + epm.getAuthoringApplication().toString());
			logger.debug("getAuthoringApplication = " + epm.getAuthoringApplication().toString());
			log.infoLog("epm.getDocType().toString()=" + epm.getDocType().toString());
			logger.debug("epm.getDocType().toString()=" + epm.getDocType().toString());
			log.infoLog("epm.isGeneric() = " + epm.isGeneric());
			log.infoLog("epm.isInstance() = " + epm.isInstance());
			log.infoLog("group = " + group);
			log.infoLog("type = " + type);
			log.infoLog("unit = " + unit);
			log.infoLog("class1 = " + class1);
			log.infoLog("class2 = " + class2);
			log.infoLog("class3 = " + class3);
			log.infoLog("class4 = " + class4);
			log.infoLog("isAutoNumber = " + isAutoNumber);
			log.infoLog("spec = " + spec);
			log.infoLog("quantityunit = " + quantityunit);
			log.infoLog("maker = " + maker);
			log.infoLog("p_Name = " + p_Name);
			logger.debug("epm.isGeneric() = " + epm.isGeneric());
			logger.debug("epm.isInstance() = " + epm.isInstance());
			logger.debug("group = " + group);
			logger.debug("type = " + type);
			logger.debug("unit = " + unit);
			logger.debug("class1 = " + class1);
			logger.debug("class2 = " + class2);
			logger.debug("class3 = " + class3);
			logger.debug("class4 = " + class4);
			logger.debug("isAutoNumber = " + isAutoNumber);
			logger.debug("spec = " + spec);
			logger.debug("quantityunit = " + quantityunit);
			logger.debug("maker = " + maker);
			logger.debug("p_Name = " + p_Name);
			System.out.println("E+++++++++++++" + epm.getAuthoringApplication().toString());

			if (epm.getAuthoringApplication().toString().equals("ACAD")) {
				System.out.println("체크인...........");
				IBAUtil.changeIBAValue(epm, "Spec", spec);
				IBAUtil.changeIBAValue(epm, "Maker", maker);
			}

			/* Name Check */
			Hashtable returnValue = null;
			String pdmName = "";
			String numberCheck = "";
			String pdmNumber = "";
			boolean isAutoName = false;
			String autoSerialNumber = null;

//			returnValue = checkPDMName(hash);
			returnValue = checkPDMNumber(hash);
			if (null != returnValue.get("autoSerialNumber"))
				autoSerialNumber = (String) returnValue.get("autoSerialNumber");
			if (null != returnValue.get("isAutoName"))
				isAutoName = (boolean) returnValue.get("isAutoName");
			numberCheck = (String) returnValue.get("numberCheck");
			logger.debug("isAutoName  = " + isAutoName);
			logger.debug("autoSerialNumber  = " + autoSerialNumber);
			if (!isAutoName && null != p_Name && p_Name.length() > 0) {
				pdmName = p_Name;
			} else if (null != returnValue.get("nameValue")) {
				pdmName = (String) returnValue.get("nameValue");
			}
			numberCheck = (String) returnValue.get("numberCheck");
			log.infoLog("numberCheck  = " + numberCheck);
			logger.debug("numberCheck  = " + numberCheck);
			logger.debug("pdmName  = " + pdmName);
			if (numberCheck.equals("false") || null == pdmName) {
				IBAUtil.changeIBAValue(epm, autoNumber, "FALSE");
				IBAUtil.changeIBAValue(epm, "message", pdmName);
				log.exceptionLog("numberCheck = " + numberCheck);
				log.exceptionLog("autoNumber = " + "FALSE");
				log.exceptionLog("pdmName = " + pdmName);
				logger.error("numberCheck = " + numberCheck);
				logger.error("autoNumber = " + "FALSE");
				logger.error("pdmName = " + pdmName);
				return;
			} else {
				pdmNumber = (String) returnValue.get("numberValue");

				if ("S".equals(type)) {
					NumberCode2 unitCode = NumberCodeHelper.manager.getNumberCode2("SBUSINESS", unit);
					if (unitCode != null) {
						IBAUtil.changeIBAValue(epm, "Group_Name", unitCode.getName());
					} else {
						autoNumberValue = autoNumberValue + "[" + unit + "] Group_Name가 존재 하지 않습니다.";
						log.exceptionLog("unitCode  is null");
						logger.error("unitCode  is null");
					}
				} else if ("B".equals(type)) {
					/*
					 * NumberCode2 paCode = NumberCodeHelper.manager.getNumberCode2("CADATTRIBUTE",
					 * type,true); HashMap map = new HashMap(); map.put("code", unit);
					 * map.put("type", "CADATTRIBUTE"); map.put("parent", paCode);
					 * 
					 * log.infoLog("code :" + unit ); log.infoLog("type :" + "CADATTRIBUTE" );
					 * log.infoLog("parent :" + paCode ); logger.debug("code :" + unit );
					 * logger.debug("type :" + "CADATTRIBUTE" ); logger.debug("parent :" + paCode );
					 * QuerySpec qs = NumberCodeHelper.getCodeQuerySpec2(map);
					 * 
					 * log.infoLog("unitCode Query :" + qs ); logger.debug("unitCode Query :" + qs
					 * ); QueryResult qr = PersistenceHelper.manager.find(qs);
					 * log.infoLog("qr.size = " +qr.size()); logger.debug("qr.size = " +qr.size());
					 * if(qr.hasMoreElements()) { Object[] obj = (Object[])qr.nextElement();
					 * NumberCode2 unitCode = (NumberCode2)obj[0]; String epmibaValue =
					 * IBAUtil.getAttrValue(epm, "Group_Name");
					 * if(!unitCode.getName().equals(epmibaValue)){
					 * logger.debug(epm.getNumber()+"\tGroup_Name IBA changeIBA start = "
					 * +unitCode.getName()); IBAUtil.changeIBAValue(epm, "Group_Name",
					 * unitCode.getName());
					 * logger.debug(epm.getNumber()+"\tGroup_Name IBA changeIBA End = "
					 * +unitCode.getName()); } }else{ autoNumberValue = autoNumberValue
					 * +"["+type+","+unit +"]Group_Name 가 존재 하지 않습니다."; }
					 * 
					 */
				}
				String epmibap_Name = IBAUtil.getAttrValue(epm, "P_Name");
				if (!p_Name.equals(epmibap_Name)) {
					logger.debug(epm.getNumber() + "\tP_Name IBA changeIBA start = " + p_Name);
					IBAUtil.changeIBAValue(epm, "P_Name", p_Name);
					logger.debug(epm.getNumber() + "\tP_Name IBA changeIBA End = " + p_Name);
				}
			}
			// 추가
			if ("A".equals(type)) {
				// returnValue = checkPDMName(hash);
				returnValue = checkPDMNumber(hash);
				String class1Code = (String) returnValue.get("class1Code");
				log.infoLog("++++++++++++++++TEST++++++++++++ " + class1Code);
				System.out.println("++++++++++++++++TEST++++++++++++ " + class1Code);
				log.infoLog(" <br> class1Code TEST  = " + class1Code);
				autoNumberValue = "";
				IBAUtil.changeIBAValue(epm, "Group_Name", class1Code);
			}
			/* 중복 체크 */
			if (!epm.getAuthoringApplication().toString().equals("ACAD")) {
				if ("B".equals(type)) { // 구매품(B)일 경우에 체크
					String number = (String) hash.get("number");
					hash.put("number", pdmNumber);
					hash.put("spec", spec);
					if (spec.length() > 0) {
						Hashtable rtHas = PartSearchHelper.duplicationNumber(hash);

						String dubleCheck = (String) rtHas.get("return");
						String oid = (String) rtHas.get("oid");

						if (dubleCheck.equals("true")) {
							WTPart dublePart = (WTPart) CommonUtil.getObject(oid);
							if (!epm.getNumber().equals(dublePart.getNumber())) { // 자기 자신은 제외
								autoNumberValue = autoNumberValue + dublePart.getNumber() + "와 규격이 동일합니다." + pdmNumber
										+ ",";

								IBAUtil.changeIBAValue(epm, autoNumber, "FALSE");
								IBAUtil.changeIBAValue(epm, "message", autoNumberValue);
								log.exceptionLog("autoNumber =" + "FALSE");
								log.exceptionLog("message =" + autoNumberValue);
								logger.debug("autoNumber =" + "FALSE");
								logger.debug("message =" + autoNumberValue);
								return;
							} // if(!epm.getNumber().equals(dublePart.getNumber())){

						} else {// if(dubleCheck.equals(

							NumberCode specCode = NumberCodeHelper.manager.getNumberCodeName("SPEC", spec);
							if (specCode != null)
								IBAUtil.changeIBAValue(epm, "Class4", specCode.getDescription());

						}
					} else {// if(spec.length()>0){
						IBAUtil.changeIBAValue(epm, autoNumber, "FALSE");
						IBAUtil.changeIBAValue(epm, "message", "SPEC 을 입력 하지 않았습니다.");
						log.exceptionLog("autoNumber =" + "FALSE");
						logger.debug("autoNumber =" + "FALSE");
						log.exceptionLog("message =" + "SPEC 을 입력 하지 않았습니다.");
						logger.debug("message =" + "SPEC 을 입력 하지 않았습니다.");
						return;
					} // if(spec.length()>0){
				}
			} // if(!epm.getAuthoringApplication().toString().equals("ACAD")){

			WTPart part = DrawingHelper.manager.getWTPart(epm);

			if (version.equals("A") && !epm.getNumber().startsWith(pdmNumber)) { // A 버전, 번호가 같지 않은 경우
				log.infoLog("<A 버전, 번호가 같지 않은 경우 START>");
				logger.debug("<A 버전, 번호가 같지 않은 경우 START>");
				/* Sequence Number */
				if (null == autoSerialNumber || (null != autoSerialNumber && autoSerialNumber.length() == 0)) {
					String serialNum = getPdmSerialNumber(pdmNumber);
					pdmNumber = pdmNumber + "-" + serialNum;
				}

				/* Cad Namue = Number+확장자 */
				String cadExtension = getCadExtension(epm.getCADName());
				String cadName = pdmNumber + cadExtension;
				cadName = cadName.toLowerCase();
				logger.debug("epm.isGeneric()=" + epm.isGeneric() + "\tepm.isInstance()=" + epm.isInstance());
				/*
				 * Number ,Name ,CadName Change ,FamilyTable(generic,instance) Number,Name
				 * Change
				 */
				if (epm.isGeneric() || epm.isInstance()) {
					logger.debug("CadInfoChange.manager.epmInfoChange run epm =" + epm.getNumber() + "\tpdmNumber="
							+ pdmNumber + "\tpdmName=" + pdmName);
					isNumber = CadInfoChange.manager.epmInfoChange(epm, pdmNumber, pdmName);
					logger.debug("CadInfoChange.manager.epmInfoChange run end epm =" + epm.getNumber() + "\tpdmNumber="
							+ pdmNumber + "\tpdmName=" + pdmName);
				} else {
					try {
						logger.debug("CadInfoChange.manager.epmCadInfoChange run epm =" + epm.getNumber()
								+ "\tpdmNumber=" + pdmNumber + "\tpdmName=" + pdmName + "\tcadName=" + cadName);
						isNumber = CadInfoChange.manager.epmCadInfoChange(epm, pdmNumber, pdmName, cadName);
						logger.debug("CadInfoChange.manager.epmCadInfoChange run epm =" + epm.getNumber()
								+ "\tpdmNumber=" + pdmNumber + "\tpdmName=" + pdmName + "\tcadName=" + cadName);
					} catch (Exception e) {
						log.errLog(e.getMessage());
						logger.error(e.getMessage());
						e.printStackTrace();
					}
				}

				/* WTPART CHANGE */
				log.infoLog("Part ====" + part);
				if (part != null) {
					log.infoLog("<WTPART CHANGE START>");
					logger.debug("<WTPART CHANGE START>");
					IBAUtil.changeNumber(CommonUtil.getOIDString(part), pdmNumber, pdmName);
					log.infoLog("Change " + pdmNumber + ":" + pdmNumber);
					logger.debug("Change " + pdmNumber + ":" + pdmNumber);
					log.infoLog("<WTPART CHANGE END>");
					logger.debug("<WTPART CHANGE END>");
				}

				/* DRAWING CHANGE */
				EPMDocument epm2D = EpmSearchHelper.manager.getEPM2D((EPMDocumentMaster) epm.getMaster());

				log.infoLog("epm2D ====" + epm2D);
				logger.debug("epm2D ====" + epm2D);
				if (null == epm2D) {
					epm2D = getEPM2D((EPMDocumentMaster) epm.getMaster());
				}

				log.infoLog("epm2D 2====" + epm2D);
				logger.debug("epm2D 2====" + epm2D);

				if (epm2D != null) {
					log.infoLog("<DRAWING CHANGE START>");
					logger.debug("<DRAWING CHANGE START>");
					cadExtension = getCadExtension(epm2D.getCADName());
					String pdmNumber2D = pdmNumber + "_2D";
					logger.debug("CadInfoChange.manager.epmCadInfoChange run epm =" + epm.getNumber()
							+ " 2D \tpdmNumber=" + pdmNumber + "\tpdmName=" + pdmName + "\tpdmNumber+cadExtension="
							+ pdmNumber + cadExtension);
					boolean isNumber2D = CadInfoChange.manager.epmCadInfoChange(epm2D, pdmNumber2D, pdmName,
							pdmNumber + cadExtension);
					logger.debug("CadInfoChange.manager.epmCadInfoChange run epm =" + epm.getNumber()
							+ " 2D \tpdmNumber=" + pdmNumber + "\tpdmName=" + pdmName + "\tpdmNumber+cadExtension="
							+ pdmNumber + cadExtension);

					if (isNumber2D) {
						IBAUtil.changeIBAValue(epm2D, autoNumber, "TRUE");
					} else {
						IBAUtil.changeIBAValue(epm2D, autoNumber, "FALSE");
					}
					log.infoLog("<DRAWING CHANGE END>");
					logger.debug("<DRAWING CHANGE END>");
				}
				log.infoLog("<A 버전, 번호가 같지 않은 경우 END>");
				logger.debug("<A 버전, 번호가 같지 않은 경우 END>");
			} else if (version.equals("A") && epm.getNumber().startsWith(pdmNumber)
					&& epm.getName().toUpperCase().startsWith(pdmNumber)) {
				log.infoLog("<A 버전, 번호가 같은 경우 START>");
				logger.debug("<A 버전, 번호가 같은 경우 START>");
				log.infoLog("epm.getNumber() = " + epm.getNumber());
				log.infoLog("epm.getName() = " + epm.getName());
				logger.debug("epm.getNumber() = " + epm.getNumber());
				logger.debug("epm.getName() = " + epm.getName());
				logger.debug("changeEPMnumber = " + epm.getNumber().substring(0, epm.getNumber().length() - 4));
				logger.debug("changeEPMName = " + pdmName);
				CadInfoChange.manager.epmInfoChange(epm, epm.getNumber().substring(0, epm.getNumber().length() - 4),
						pdmName);

				/* DRAWING CHANGE */
				EPMDocument epm2D = EpmSearchHelper.manager.getEPM2D((EPMDocumentMaster) epm.getMaster());

				log.infoLog("epm2D ====" + epm2D);
				logger.debug("epm2D ====" + epm2D);
				if (null == epm2D) {
					epm2D = getEPM2D((EPMDocumentMaster) epm.getMaster());
				}

				log.infoLog("epm2D 2====" + epm2D);
				logger.debug("epm2D 2====" + epm2D);

				if (epm2D != null) {
					String pdmNumber2D = epm.getNumber() + "_2D";
					logger.debug("CadInfoChange.manager.epmInfoChange run epm =" + epm.getNumber() + " 2D \tpdmNumber="
							+ pdmNumber + "\tpdmName=" + pdmName);
					boolean isNumber2D = CadInfoChange.manager.epmInfoChange(epm2D, pdmNumber2D, pdmName);
					logger.debug("CadInfoChange.manager.epmInfoChange run end epm =" + epm.getNumber()
							+ " 2D \tpdmNumber=" + pdmNumber + "\tpdmName=" + pdmName);
					if (isNumber2D) {
						IBAUtil.changeIBAValue(epm2D, autoNumber, "TRUE");
					} else {
						IBAUtil.changeIBAValue(epm2D, autoNumber, "FALSE");
					}
				}
				log.infoLog("<A 버전, 번호가 같은 경우 END>");
				logger.debug("<A 버전, 번호가 같은 경우 END>");
			}
			logger.debug("type =" + type + "group=" + group);
			try {

				if (!epm.getName().equals(pdmName)) {
					WTUser sessUser = (WTUser) SessionHelper.manager.getPrincipal();
					String susername = sessUser.getName();
					SessionHelper.manager.setAdministrator();
					log.infoLog("<도면명이 다를 경우...>");
					logger.debug("<도면명이 다를 경우>");
					log.infoLog("epm.getNumber() = " + epm.getNumber());
					log.infoLog("epm.getName() = " + epm.getName());
					logger.debug("epm.getNumber() = " + epm.getNumber());
					logger.debug("epm.getName() = " + epm.getName());
					logger.debug("changeEPMnumber = " + epm.getNumber().substring(0, epm.getNumber().length() - 4));
					logger.debug("changeEPMName = " + pdmName);
					CadInfoChange.manager.epmInfoChange(epm, epm.getNumber(), pdmName);
					String pNameAttr = "";
					logger.debug("part ====" + part);
					if (null != part) {
						pNameAttr = IBAUtil.getAttrValue(part, "P_Name");
						log.infoLog("pNameAttr ====" + pNameAttr);
						logger.debug("pdmName ====" + pdmName);
						if (!pNameAttr.equals(pdmName))
							IBAUtil.changeIBAValue(part, "P_Name", pdmName);

						CadInfoChange.manager.partInfoChange(part, part.getNumber(), pdmName);
					}
					pNameAttr = IBAUtil.getAttrValue(epm, "P_Name");
					log.infoLog("pNameAttr ====" + pNameAttr);
					logger.debug("p_Name ====" + pdmName);
					if (!pNameAttr.equals(pdmName))
						IBAUtil.changeIBAValue(epm, "P_Name", pdmName);

					/* DRAWING CHANGE */
					EPMDocument epm2D = EpmSearchHelper.manager.getEPM2D((EPMDocumentMaster) epm.getMaster());
					log.infoLog("epm2D ====" + epm2D);
					logger.debug("epm2D ====" + epm2D);
					if (null == epm2D) {
						epm2D = getEPM2D((EPMDocumentMaster) epm.getMaster());
					}

					log.infoLog("epm2D 2====" + epm2D);
					logger.debug("epm2D 2====" + epm2D);

					if (epm2D != null) {
						String pdmNumber2D = epm.getNumber() + "_2D";
						String name2D = epm2D.getName();
						pNameAttr = IBAUtil.getAttrValue(epm2D, "P_Name");
						log.infoLog("pNameAttr ====" + pNameAttr);
						logger.debug("pdmName ====" + pdmName);
						if (!pNameAttr.equals(pdmName))
							IBAUtil.changeIBAValue(epm2D, "P_Name", pdmName);
						log.infoLog("name2D ====" + name2D);
						logger.debug("pdmName ====" + pdmName);
						if (!name2D.equals(pdmName)) {
							logger.debug("CadInfoChange.manager.epmInfoChange run epm =" + epm.getNumber()
									+ " 2D \tpdmNumber=" + pdmNumber + "\tpdmName=" + pdmName);
							boolean isNumber2D = CadInfoChange.manager.epmInfoChange(epm2D, epm2D.getNumber(), pdmName);
							logger.debug("CadInfoChange.manager.epmInfoChange run end epm =" + epm.getNumber()
									+ " 2D \tpdmNumber=" + pdmNumber + "\tpdmName=" + pdmName);
							if (isNumber2D) {
								IBAUtil.changeIBAValue(epm2D, autoNumber, "TRUE");
							} else {
								IBAUtil.changeIBAValue(epm2D, autoNumber, "FALSE");
							}
						}
					}
					log.infoLog("<도면명이 다를 경우... END>");
					logger.debug("<도면명이 다를 경우... END>");
					SessionHelper.manager.setPrincipal(susername);
				}

			} catch (Exception e) {
				log.errLog(e.getMessage());
				logger.error(e.getMessage());
				e.printStackTrace();
			}
			/* CADCOMPONENT 인 경우에 */
			if (epm.getDocType().toString().equals("CADCOMPONENT")) {
				String material = (String) ibaAttr.get("Material"); // PTC_MATERIAL_NAME

				NumberCode mCode = null;
				if (material != null && material.length() > 0) {
					mCode = NumberCodeHelper.manager.getNumberCodeName("MATERIAL", material);
					if (mCode == null)
						autoNumberValue = autoNumberValue + "입력하신 Material의 정보를 찾을 수 없습니다.";
				} else {
					autoNumberValue = autoNumberValue + "Material의 정보를 입력 하지 않았습니다..";
				}
			}

			/* WTPART 속성 Check Start */

			if (!epm.getAuthoringApplication().toString().equals("ACAD")) {
				if (quantityunit.length() > 0) {
					quantityunit = quantityunit.toLowerCase();
					QuantityUnit qu = QuantityUnit.toQuantityUnit(quantityunit);
					if (qu == null) {
						autoNumberValue = autoNumberValue + "(" + quantityunit + ")입력하신 단위의 정보를 찾을 수 없습니다 ,";
					} else {
						if (part != null) {
//							part = (WTPart) PersistenceHelper.manager.refresh(part);
//							WTPartMaster master = (WTPartMaster) part.getMaster();
//							master.setDefaultUnit(QuantityUnit.toQuantityUnit(quantityunit));
//							PersistenceHelper.manager.modify(master);
						}

					}
				} else {
					autoNumberValue = autoNumberValue + "단위를 입력 하지 않았습니다 ,";
				}

				// if(epm.getDocType().toString().equals("CADCOMPONENT") ){
				if ("B".equals(type)) {
					if (spec.length() > 0) {
						NumberCode sCode = NumberCodeHelper.manager.getNumberCodeName("SPEC", spec);
						if (sCode == null)
							autoNumberValue = autoNumberValue + "(" + spec + ")입력하신 규격의 정보를 찾을 수 없습니다 ,";
					} else {
						autoNumberValue = autoNumberValue + "규격을  입력 하지 않았습니다 ,";
					}
				} else {
					if (spec.length() > 0) {
						NumberCode sCode = NumberCodeHelper.manager.getNumberCodeName("SPEC", spec);
						if (sCode == null)
							autoNumberValue = autoNumberValue + "(" + spec + ")입력하신 규격의 정보를 찾을 수 없습니다 ,";
					}
				}

				if (maker.length() > 0) {
					HashMap erpMap = new HashMap();
					erpMap.put("makerName", maker);
					erpMap.put("searchType", "equals");
					ResultSet rs = ERPSearchHelper.manager.getErpMaker(erpMap);
					if (!rs.next())
						autoNumberValue = autoNumberValue + "(" + maker + ")" + "입력하신 Maker의 정보를 찾을 수 없습니다";
				}
			}
			/* WTPART 속성 Check End */

			/* autoNumber ,message */
			epm = (EPMDocument) PersistenceHelper.manager.refresh(epm);
			if (isNumber) {
//				IBAUtil.changeIBAValue(epm, autoNumber, "TRUE");
			} else {
				IBAUtil.changeIBAValue(epm, autoNumber, "FALSE");
			}

//			IBAUtil.changeIBAValue(epm, "message", autoNumberValue);

		} catch (Exception e) {
			try {
				IBAUtil.changeIBAValue(epm, autoNumber, "FALSE");
				IBAUtil.changeIBAValue(epm, "message", e.getMessage());
			} catch (Exception e2) {
				e2.printStackTrace();
			}

			e.printStackTrace();
		}

	}
	/*
	 * public static Hashtable checkPDMName(Hashtable hash){ Hashtable returnValue =
	 * new Hashtable(); returnValue.put("numberCheck", "false");
	 * 
	 * String group = (String)hash.get("group"); group =
	 * StringUtil.checkNull(group); NumberCode groupCode =null;
	 * 
	 * if(group.length()>0){ groupCode =
	 * NumberCodeHelper.manager.getNumberCode("CADCREATOR", group); }
	 * 
	 * if(groupCode == null){ returnValue.put("nameValue",
	 * "입력하신 Group에 해당하는 code값이 없습니다."); log.
	 * exceptionLog(" [checkPDMName] groupCode IS NULL : 입력하신 Group에 해당하는 code값이 없습니다. "
	 * ); }else { String type = (String)hash.get("type"); type =
	 * StringUtil.checkNull(type); HashMap typeMap = new HashMap();
	 * typeMap.put("type", "CADATTRIBUTE"); typeMap.put("code", type);
	 * typeMap.put("isParent", "false");
	 * 
	 * ArrayList typeArray = null; try { if(type.length()>0){ typeArray =
	 * NumberCodeHelper.manager.getNumberCode(typeMap); }
	 * 
	 * } catch (WTException e1) {
	 * 
	 * log.errLog(" [checkPDMName] typeArray WTException : " +e1.getMessage() );
	 * e1.printStackTrace(); }
	 * 
	 * if( typeArray == null || typeArray.size() == 0){ returnValue.put("nameValue",
	 * "입력하신 Type에 해당하는 code값이 없습니다."); log.
	 * exceptionLog(" [checkPDMName] typeArray is null or size =0  typeArray =  "
	 * +typeArray ); }else { NumberCode typeCode = (NumberCode)typeArray.get(0);
	 * 
	 * String unit =StringUtil.checkNull((String)hash.get("unit")); String class1 =
	 * StringUtil.checkNull( (String)hash.get("class1")); String class2 =
	 * StringUtil.checkNull((String)hash.get("class2")); String class3 =
	 * StringUtil.checkNull((String)hash.get("class3")); String class4 =
	 * StringUtil.checkNull((String)hash.get("class4"));
	 * 
	 * if( "S".equals( typeCode.getCode() ) ) {
	 * 
	 * log.
	 * infoLog(" [checkPDMName] :JS getProductSub typeCode.getCode() is S  START ==============="
	 * ); NumberCode unitCode = null; if(unit.length()>0){
	 * //NumberCodeHelper.manager.getNumberCode("SBUSINESS", unit); //unitCode Check
	 * 로직 반영... suk 2013.02.14 unitCode =
	 * NumberCodeHelper.manager.getNumberCode("SBUSINESS", unit); }
	 * 
	 * if(unitCode == null){ returnValue.put("nameValue",
	 * "입력하신 Unit에 해당하는 code값이 없습니다.");
	 * log.exceptionLog(" [checkPDMName] :  입력하신 Unit에 해당하는 code값이 없습니다. " ); }else
	 * { //NumberCode class1Code =
	 * NumberCodeHelper.manager.getNumberCode("SCUSTOMER", class1); NumberCode
	 * class1Code = NumberCodeHelper.manager.getNumberCode("CADATTRIBUTE", class1);
	 * if(class1Code == null){ returnValue.put("nameValue",
	 * "입력하신 Class-1에 해당하는 code값이 없습니다.");
	 * log.exceptionLog(" [checkPDMName] : 입력하신 Class-1에 해당하는 code값이 없습니다. " );
	 * }else { //NumberCode class2Code =
	 * NumberCodeHelper.manager.getNumberCode("SCLASS1", class2); NumberCode
	 * class2Code = NumberCodeHelper.manager.getNumberCode("CADATTRIBUTE", class2);
	 * if(class2Code == null){ returnValue.put("nameValue",
	 * "입력하신 Class-2에 해당하는 code값이 없습니다.");
	 * log.exceptionLog(" [checkPDMName] : 입력하신 Class-2에 해당하는 code값이 없습니다.. " );
	 * }else { //NumberCode class3Code =
	 * NumberCodeHelper.manager.getNumberCode("SCLASS2", class3); NumberCode
	 * class3Code = NumberCodeHelper.manager.getNumberCode("CADATTRIBUTE", class3);
	 * if(class3Code == null){ returnValue.put("nameValue",
	 * "입력하신 Class-3에 해당하는 code값이 없습니다.");
	 * log.exceptionLog(" [checkPDMName] : 입력하신 Class-3에 해당하는 code값이 없습니다. " );
	 * }else { //NumberCode class4Code =
	 * NumberCodeHelper.manager.getNumberCode("SCLASS3", class4); NumberCode
	 * class4Code = NumberCodeHelper.manager.getNumberCode("CADATTRIBUTE", class4);
	 * if(class4Code == null){ returnValue.put("nameValue",
	 * "입력하신 Class-4에 해당하는 code값이 없습니다.");
	 * log.exceptionLog(" [checkPDMName] : 입력하신 Class-4에 해당하는 code값이 없습니다." ); }else
	 * { returnValue.put("numberCheck", "true");
	 * 
	 * // String nameValue = groupCode.getName() + "_" + typeCode.getName() + "_" +
	 * unitCode.getName() + "_" + class1Code.getName() + "_" + class2Code.getName()
	 * + "_" + // class3Code.getName(); // if( !"Non".equals(class4Code.getName()))
	 * // nameValue = nameValue + "_" + class4Code.getName(); String nameValue =
	 * class2Code.getName();
	 * 
	 * returnValue.put("nameValue", nameValue);
	 * 
	 * String numberValue = groupCode.getCode() + typeCode.getCode() + "-" +
	 * unitCode.getCode() + class1Code.getCode() + "-" + class2Code.getCode() +
	 * class3Code.getCode() + class4Code.getCode();
	 * 
	 * returnValue.put("numberValue", numberValue);
	 * 
	 * log.infoLog(" [checkPDMName] : numberCheck = " +"true" );
	 * log.infoLog(" [checkPDMName] : nameValue = " +nameValue );
	 * log.infoLog(" [checkPDMName] : numberValue = " +numberValue );
	 * log.infoLog(" [checkPDMName] : typeCode.getCode() is S  END ==============="
	 * ); } } } } } } else { log.
	 * infoLog(" [checkPDMName] : typeCode.getCode() is Not S  START ==============="
	 * ); try { String typeCodeStr = typeCode.getCode();
	 * System.out.println("typeCodeStr = "+typeCodeStr); HashMap map = new
	 * HashMap(); if( "A".equals( typeCode.getCode() ) ) { map.put("type",
	 * "SCLASS1"); }else{ map.put("type", "CADATTRIBUTE"); } map.put("code", unit);
	 * map.put("parent", typeCode);
	 * 
	 * ArrayList unitArray = NumberCodeHelper.manager.getNumberCode(map);
	 * if(unitArray == null || unitArray.size() == 0) { returnValue.put("nameValue",
	 * "입력하신 Unit에 해당하는 code값이 없습니다."); log.
	 * exceptionLog(" [checkPDMName] : unitArray is null or size =0 입력하신 Unit에 해당하는 code값이 없습니다."
	 * ); }else { NumberCode unitCode = (NumberCode)unitArray.get(0);
	 * map.put("code", class1); map.put("parent", unitCode); ArrayList class1Array =
	 * NumberCodeHelper.manager.getNumberCode(map); if(class1Array == null ||
	 * class1Array.size() == 0){ returnValue.put("nameValue",
	 * "입력하신 Class-1에 해당하는 code값이 없습니다."); log.
	 * exceptionLog(" [checkPDMName] : class1Array is null or class1Array =0 입력하신 Class-1에 해당하는 code값이 없습니다."
	 * ); }else { NumberCode class1Code = (NumberCode)class1Array.get(0);
	 * 
	 * if( "A".equals( typeCode.getCode() ) ) { if(class2.length() != 3){
	 * returnValue.put("nameValue", "Class-2은 3자리로 입력하셔야 합니다.");
	 * log.exceptionLog(" [checkPDMName] : Class-2은 3자리로 입력하셔야 합니다." ); }else {
	 * returnValue.put("numberCheck", "true"); String nameValue =
	 * unitCode.getName(); if( !"Non".equals(class1Code.getName())){ nameValue =
	 * class1Code.getName(); }
	 * 
	 * returnValue.put("nameValue", nameValue + "-" + class2); String numberValue =
	 * groupCode.getCode() + typeCode.getCode() + "-" + unitCode.getCode() + "-" +
	 * class1Code.getCode() + class2; returnValue.put("numberValue", numberValue);
	 * 
	 * log.infoLog(" [checkPDMName] : numberCheck = " +"true" );
	 * log.infoLog(" [checkPDMName] : nameValue = " +nameValue + "-" + class2 );
	 * log.infoLog(" [checkPDMName] : numberValue = " +numberValue );
	 * }//if(class2.length() != 3){ } else if( "B".equals( typeCode.getCode() ) ||
	 * "P".equals( typeCode.getCode() ) ) { map.put("code", class2);
	 * map.put("parent", class1Code); ArrayList class2Array =
	 * NumberCodeHelper.manager.getNumberCode(map); if(class2Array == null ||
	 * class2Array.size() == 0){ returnValue.put("nameValue",
	 * "입력하신 Class-2에 해당하는 code값이 없습니다."); log.
	 * exceptionLog(" [checkPDMName] : class2Array == null  || class2Array.size() == 0  입력하신 Class-2에 해당하는 code값이 없습니다."
	 * ); }else { NumberCode class2Code = (NumberCode)class2Array.get(0);
	 * returnValue.put("numberCheck", "true");
	 * 
	 * String nameValue = unitCode.getName(); if(
	 * !"Non".equals(class1Code.getName())) nameValue = class1Code.getName(); if(
	 * !"Non".equals(class2Code.getName())) nameValue = class2Code.getName();
	 * 
	 * returnValue.put("nameValue", nameValue);
	 * 
	 * String numberValue = groupCode.getCode() + typeCode.getCode() + "-" +
	 * unitCode.getCode() + "-" + class1Code.getCode() + class2Code.getCode();
	 * returnValue.put("numberValue", numberValue);
	 * 
	 * log.infoLog(" [checkPDMName] : numberCheck = " +"true" );
	 * log.infoLog(" [checkPDMName] : nameValue = " +nameValue );
	 * log.infoLog(" [checkPDMName] : numberValue = " +numberValue );
	 * }//if(class2Array.size() == 0) }//else if( "B".equals( typeCode.getCode() )
	 * || "P".equals( typeCode.getCode() ) ) { } } } catch (WTException e) {
	 * log.exceptionLog(" [checkPDMName] : WTException : " + e.getMessage() );
	 * e.printStackTrace(); return returnValue; } } log.
	 * infoLog(" [checkPDMName] : typeCode.getCode() is Not S  END ==============="
	 * ); }
	 * 
	 * } return returnValue; }
	 */

	public static void test(EPMDocument epm, String _event) {
		try {
			if (WorkInProgressHelper.isCheckedOut((Workable) epm)) {
				return;
			}
			if (!VersionControlHelper.isLatestIteration(epm)) {
				return;
			}
			if (epm.getDocType().toString().equals("CADDRAWING") || epm.getDocType().toString().equals("FORMAT"))
				return;
			if (!epm.getOwnerApplication().toString().equals("EPM"))
				return;
//			System.out.println("::::::::::::: EPMDocument EVENT ::::::::::::::::::::::::::::::: :" +_event);
//			System.out.println("::::::::::::: WorkInProgressHelper.isCheckedOut((Workable) epm : "  + WorkInProgressHelper.isCheckedOut((Workable) epm));
//			System.out.println("::::::::::::: VersionControlHelper.isLatestIteration(epm)) :" + VersionControlHelper.isLatestIteration(epm));
//			System.out.println("::::::::::::: epm :" + epm);
			wt.epm.structure.EPMReferenceLink referenceLink = null;
			boolean isNumber = false;
			String autoNumberValue = "";

			HashMap ibaAttr = IBAUtil.getAttributes(epm);
			String group = (ibaAttr != null) ? StringUtil.trim((String) ibaAttr.get("Group")) : "";
			if (group != null && group.length() > 0)
				group = group.toUpperCase();
			String type = (ibaAttr != null) ? StringUtil.trim((String) ibaAttr.get("Type")) : "";
			if (type != null && type.length() > 0)
				type = type.toUpperCase();
			String unit = (ibaAttr != null) ? StringUtil.trim((String) ibaAttr.get("Unit")) : "";
			if (unit != null && unit.length() > 0)
				unit = unit.toUpperCase();
			String class1 = (ibaAttr != null) ? StringUtil.trim((String) ibaAttr.get("Class1")) : "";
			if (class1 != null && class1.length() > 0)
				class1 = class1.toUpperCase();
			String class2 = (ibaAttr != null) ? StringUtil.trim((String) ibaAttr.get("Class2")) : "";
			if (class2 != null && class2.length() > 0)
				class2 = class2.toUpperCase();
			String class3 = (ibaAttr != null) ? StringUtil.trim((String) ibaAttr.get("Class3")) : "";
			if (class3 != null && class3.length() > 0)
				class3 = class3.toUpperCase();
			String class4 = (ibaAttr != null) ? StringUtil.trim((String) ibaAttr.get("Class4")) : "";
			if (class4 != null && class4.length() > 0)
				class4 = class4.toUpperCase();

			String isAutoNumber = (ibaAttr != null) ? StringUtil.trim((String) ibaAttr.get(autoNumber)) : "";
			String spec = (ibaAttr != null) ? StringUtil.trim((String) ibaAttr.get("Spec")) : "";
			String quantityunit = (ibaAttr != null) ? StringUtil.trim((String) ibaAttr.get("quantityunit")) : "";

//			System.out.println("::::::::::::: group : "  + group);
//			System.out.println("::::::::::::: type : "  + type);
//			System.out.println("::::::::::::: unit : "  + unit);
//			System.out.println("::::::::::::: class1 : "  + class1);
//			System.out.println("::::::::::::: class2 : "  + class2);
//			System.out.println("::::::::::::: class3 : "  + class3);
//			System.out.println("::::::::::::: class4 : "  + class4);
		} catch (Exception e) {

		}

	}

	public static boolean isCheckITEMNAME(EPMDocument _obj) {
		boolean isCheck = true;
		try {
			HashMap ibaAttr = null;
			ibaAttr = IBAUtil.getAttributes(_obj);
			String ITEMNAME = (ibaAttr != null)
					? StringUtil.checkNull(StringUtil.trim((String) ibaAttr.get("ITEMNAME")))
					: "";
			if (ITEMNAME == null || ITEMNAME.length() == 0) {
				isCheck = false;
			}
		} catch (Exception e) {
			isCheck = false;
		}
		return isCheck;
	}

	public static EPMDocument getEPM2D(EPMDocumentMaster master) {

		EPMDocument epm2D = null;
		EPMDocument lastDocument = null;
		try {
			QuerySpec qs = new QuerySpec();
			int idxA = qs.addClassList(EPMReferenceLink.class, false);
			int idxB = qs.addClassList(EPMDocument.class, true);

			// Join
			qs.appendWhere(new SearchCondition(EPMReferenceLink.class, "roleAObjectRef.key.id", EPMDocument.class,
					"thePersistInfo.theObjectIdentifier.id"), new int[] { idxA, idxB });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(EPMReferenceLink.class, "roleBObjectRef.key.id", SearchCondition.EQUAL,
					CommonUtil.getOIDLongValue(master)), new int[] { idxA });
			qs.appendAnd();
			qs.appendWhere(
					new SearchCondition(EPMReferenceLink.class, "referenceType", SearchCondition.EQUAL, "DRAWING"),
					new int[] { idxA }); // DRAWING
			// �ֽ� ���ͷ��̼�
			qs.appendAnd();
			qs.appendWhere(VersionControlHelper.getSearchCondition(EPMDocument.class, true), new int[] { idxB });

			// System.out.println(qs);
			QueryResult rt = PersistenceHelper.manager.find(qs);
			// System.out.println("size : " + rt.size());

			while (rt.hasMoreElements()) {

				Object[] oo = (Object[]) rt.nextElement();
				epm2D = (EPMDocument) oo[0];

				boolean isLasted = ObjectUtil.isLatestVersion(epm2D);
				if (isLasted) {
					lastDocument = epm2D;
					break;
				}
//				System.out.println("EPMDocument 2d:" + epm2D.getNumber()+":" + epm2D.getVersionIdentifier().getSeries().getValue()+"."+epm2D.getIterationIdentifier().getSeries().getValue());

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return epm2D;
	}

	public static void checkNumber(EPMDocument epm) {
		try {
			if (WorkInProgressHelper.isCheckedOut((Workable) epm)) {
				return;
			}
			if (!VersionControlHelper.isLatestIteration(epm)) {
				return;
			}
			if (epm.getDocType().toString().equals("CADDRAWING") || epm.getDocType().toString().equals("FORMAT"))
				return;
			if (!epm.getOwnerApplication().toString().equals("EPM"))
				return;

			if (!epm.getVersionIdentifier().getSeries().getValue().equals("A"))
				return;
			System.out.println(":::::::::::::::::: Cad Number : " + epm.getNumber() + ":" + epm.getName());
			log.infoLog(":::::::::::::::::: EPMDocument createEPMChange START  ::::::::::::::::::::::::");
			logger.debug(":::::::::::::::::: EPMDocument createEPMChange START  ::::::::::::::::::::::::");
			log.infoLog(":::::::::::::::::: Cad Number : " + epm.getNumber() + ":" + epm.getName());
			logger.debug(":::::::::::::::::: Cad Number : " + epm.getNumber() + ":" + epm.getName());
			wt.epm.structure.EPMReferenceLink referenceLink = null;
			boolean isNumber = false;
			String autoNumberValue = "";

			HashMap ibaAttr = IBAUtil.getAttributes(epm);
			String group = (ibaAttr != null) ? StringUtil.checkNull(StringUtil.trim((String) ibaAttr.get("Group")))
					: "";
			if (group != null && group.length() > 0)
				group = group.toUpperCase();
			String type = (ibaAttr != null) ? StringUtil.checkNull(StringUtil.trim((String) ibaAttr.get("Type"))) : "";
			if (type != null && type.length() > 0)
				type = type.toUpperCase();
			String unit = (ibaAttr != null) ? StringUtil.checkNull(StringUtil.trim((String) ibaAttr.get("Unit"))) : "";
			if (unit != null && unit.length() > 0)
				unit = unit.toUpperCase();
			String class1 = (ibaAttr != null) ? StringUtil.checkNull(StringUtil.trim((String) ibaAttr.get("Class1")))
					: "";
			if (class1 != null && class1.length() > 0)
				class1 = class1.toUpperCase();
			String class2 = (ibaAttr != null) ? StringUtil.checkNull(StringUtil.trim((String) ibaAttr.get("Class2")))
					: "";
			if (class2 != null && class2.length() > 0)
				class2 = class2.toUpperCase();
			String class3 = (ibaAttr != null) ? StringUtil.checkNull(StringUtil.trim((String) ibaAttr.get("Class3")))
					: "";
			if (class3 != null && class3.length() > 0)
				class3 = class3.toUpperCase();
			String class4 = (ibaAttr != null) ? StringUtil.checkNull(StringUtil.trim((String) ibaAttr.get("Class4")))
					: "";
			if (class4 != null && class4.length() > 0)
				class4 = class4.toUpperCase();
			String ITEMNAME = (ibaAttr != null)
					? StringUtil.checkNull(StringUtil.trim((String) ibaAttr.get("ITEMNAME")))
					: "";
			if (ITEMNAME != null && ITEMNAME.length() > 0)
				ITEMNAME = ITEMNAME.toUpperCase();

			String isAutoNumber = (ibaAttr != null)
					? StringUtil.checkNull(StringUtil.trim((String) ibaAttr.get(autoNumber)))
					: "";
			String spec = (ibaAttr != null) ? StringUtil.checkNull(StringUtil.trim((String) ibaAttr.get("Spec"))) : "";
			String quantityunit = (ibaAttr != null)
					? StringUtil.checkNull(StringUtil.trim((String) ibaAttr.get("quantityunit")))
					: "";
			String maker = (ibaAttr != null) ? StringUtil.checkNull(StringUtil.trim((String) ibaAttr.get("Maker")))
					: "";
			Hashtable hash = new Hashtable();
			hash.put("group", group);
			hash.put("type", type);
			hash.put("unit", unit);
			hash.put("class1", class1);
			hash.put("class2", class2);
			hash.put("class3", class3);
			hash.put("class4", class4);

			log.infoLog("getAuthoringApplication = " + epm.getAuthoringApplication().toString());
			logger.debug("getAuthoringApplication = " + epm.getAuthoringApplication().toString());
			log.infoLog("epm.getDocType().toString()=" + epm.getDocType().toString());
			logger.debug("epm.getDocType().toString()=" + epm.getDocType().toString());
			log.infoLog("epm.isGeneric() = " + epm.isGeneric());
			logger.debug("epm.isGeneric() = " + epm.isGeneric());
			log.infoLog("epm.isInstance() = " + epm.isInstance());
			logger.debug("epm.isInstance() = " + epm.isInstance());
			log.infoLog("group = " + group);
			logger.debug("group = " + group);
			log.infoLog("type = " + type);
			logger.debug("type = " + type);
			log.infoLog("unit = " + unit);
			logger.debug("unit = " + unit);
			log.infoLog("class1 = " + class1);
			logger.debug("class1 = " + class1);
			log.infoLog("class2 = " + class2);
			logger.debug("class2 = " + class2);
			log.infoLog("class3 = " + class3);
			logger.debug("class3 = " + class3);
			log.infoLog("class4 = " + class4);
			logger.debug("class4 = " + class4);
			log.infoLog("isAutoNumber = " + isAutoNumber);
			logger.debug("isAutoNumber = " + isAutoNumber);
			log.infoLog("spec = " + spec);
			logger.debug("spec = " + spec);
			log.infoLog("quantityunit = " + quantityunit);
			logger.debug("quantityunit = " + quantityunit);
			log.infoLog("maker = " + maker);
			logger.debug("maker = " + maker);
			log.infoLog("ITEMNAME = " + ITEMNAME);
			logger.debug("ITEMNAME = " + ITEMNAME);

			System.out.println("E+++++++++++++" + epm.getAuthoringApplication().toString());
			if (epm.getAuthoringApplication().toString().equals("ACAD")) {
				System.out.println("저장.....");
				IBAUtil.changeIBAValue(epm, "Spec", spec);
				IBAUtil.changeIBAValue(epm, "Maker", maker);
			}

			/* Name Check */
			Hashtable returnValue = checkPDMNumber(hash);
			String pdmName = "";
			if (null != ITEMNAME && ITEMNAME.length() > 0) {
				pdmName = ITEMNAME;
			}
			String numberCheck = (String) returnValue.get("numberCheck");
			String autoSerialNumber = (String) returnValue.get("autoSerialNumber");
			String pdmNumber = "";
			log.infoLog("numberCheck  = " + numberCheck);
			logger.debug("numberCheck  = " + numberCheck);
			if (numberCheck.equals("false")) {
				log.exceptionLog("IBAUtil.changeIBAValue  autoNumber, message = false");
				logger.error("IBAUtil.changeIBAValue  autoNumber, message = false");
				IBAUtil.changeIBAValue(epm, autoNumber, "FALSE");
				IBAUtil.changeIBAValue(epm, "message", pdmName);
				String ibaAutoNumber = IBAUtil.getAttrValue(epm, autoNumber);
				String ibamessage = IBAUtil.getAttrValue(epm, "message");
				String LatestVersionFlag = IBAUtil.getAttrValue(epm, "LatestVersionFlag");
				log.exceptionLog("epm  = " + CommonUtil.getOIDString(epm));
				log.exceptionLog("ibaAutoNumber  = " + ibaAutoNumber);
				log.exceptionLog("ibamessage  = " + ibamessage);
				log.exceptionLog("LatestVersionFlag  = " + LatestVersionFlag);
				log.exceptionLog("autoNumber = " + "FALSE");
				log.exceptionLog("message = " + pdmName);
				logger.error("epm  = " + CommonUtil.getOIDString(epm));
				logger.error("ibaAutoNumber  = " + ibaAutoNumber);
				logger.error("ibamessage  = " + ibamessage);
				logger.error("LatestVersionFlag  = " + LatestVersionFlag);
				logger.error("autoNumber = " + "FALSE");
				logger.error("message = " + pdmName);
				return;
			} else {
				pdmNumber = (String) returnValue.get("numberValue");
				logger.debug("pdmNumber  = " + pdmNumber);
				log.infoLog("pdmNumber  = " + pdmNumber);
				logger.debug("autoSerialNumber  = " + autoSerialNumber);
				log.infoLog("autoSerialNumber  = " + autoSerialNumber);
				if (pdmName.length() == 0)
					pdmName = pdmNumber;
				/*
				 * 도면명 체크 로직 관련 주석 20200908 jtpark
				 * 
				 * 
				 * if("S".equals(type)) { NumberCode unitCode =
				 * NumberCodeHelper.manager.getNumberCode("SBUSINESS", unit); if(unitCode !=
				 * null){ IBAUtil.changeIBAValue(epm, "Group_Name", unitCode.getName()); }else{
				 * autoNumberValue = autoNumberValue +"["+unit +"] Group_Name가 존재 하지 않습니다.";
				 * log.exceptionLog("unitCode  is null"); logger.error("unitCode  is null"); }
				 * }else { NumberCode paCode =
				 * NumberCodeHelper.manager.getNumberCode("CADATTRIBUTE", type,true); HashMap
				 * map = new HashMap(); map.put("code", unit); map.put("type", "CADATTRIBUTE");
				 * map.put("parent", paCode);
				 * 
				 * log.infoLog("code :" + unit ); log.infoLog("type :" + "CADATTRIBUTE" );
				 * log.infoLog("parent :" + paCode ); logger.debug("code :" + unit );
				 * logger.debug("type :" + "CADATTRIBUTE" ); logger.debug("parent :" + paCode );
				 * QuerySpec qs = NumberCodeHelper.getCodeQuerySpec(map);
				 * 
				 * log.infoLog("unitCode Query :" + qs ); logger.debug("unitCode Query :" + qs
				 * ); QueryResult qr = PersistenceHelper.manager.find(qs);
				 * log.infoLog("qr.size = " +qr.size()); logger.debug("qr.size = " +qr.size());
				 * if(qr.hasMoreElements()) { Object[] obj = (Object[])qr.nextElement();
				 * NumberCode unitCode = (NumberCode)obj[0]; String epmibaValue =
				 * IBAUtil.getAttrValue(epm, "Group_Name");
				 * if(!unitCode.getName().equals(epmibaValue)){
				 * logger.debug(epm.getNumber()+"\tGroup_Name IBA changeIBA start = "
				 * +unitCode.getName()); IBAUtil.changeIBAValue(epm, "Group_Name",
				 * unitCode.getName());
				 * logger.debug(epm.getNumber()+"\tGroup_Name IBA changeIBA End = "
				 * +unitCode.getName()); } }else{ autoNumberValue = autoNumberValue
				 * +"["+type+","+unit +"]Group_Name 가 존재 하지 않습니다."; } }
				 */
				String epmibaITEMNAME = IBAUtil.getAttrValue(epm, "ITEMNAME");
				if (!ITEMNAME.equals(epmibaITEMNAME)) {
					logger.debug(epm.getNumber() + "\tITEMNAME IBA changeIBA start = " + ITEMNAME);
					IBAUtil.changeIBAValue(epm, "ITEMNAME", ITEMNAME);
					logger.debug(epm.getNumber() + "\tITEMNAME IBA changeIBA End = " + ITEMNAME);
				}
			}
			/*
			 * 도면명 체크 로직 관련 주석 20200908 jtpark
			 * 
			 * System.out.println("++++++++++++++++type++++++++++++ "+type); String
			 * class1Code = (String)returnValue.get("class1Code");
			 * System.out.println("++++++++++++++++class1Code++++++++++++ "+class1Code);
			 * //추가 if("P".equals(type)){ //String class1Code =
			 * (String)returnValue.get("class1Code");
			 * log.infoLog("++++++++++++++++TEST++++++++++++ "+class1Code);
			 * System.out.println("++++++++++++++++TEST++++++++++++ "+class1Code);
			 * log.infoLog(" <br> class1Code TEST  = "+class1Code);
			 * IBAUtil.changeIBAValue(epm, "Group_Name", class1Code); }
			 */
			/* 중복 체크 */

			if (!epm.getAuthoringApplication().toString().equals("ACAD")) {
				if ("B".equals(type)) { // 구매품(B)일 경우에 체크
					String number = (String) hash.get("number");
					hash.put("number", pdmNumber);
					hash.put("spec", spec);
					if (spec.length() > 0) {
						Hashtable rtHas = PartSearchHelper.duplicationNumber(hash);

						String dubleCheck = StringUtil.checkNull((String) rtHas.get("return"));
						String oid = StringUtil.checkNull((String) rtHas.get("oid"));

						if (dubleCheck.equals("true")) {
							WTPart dublePart = (WTPart) CommonUtil.getObject(oid);
							autoNumberValue = autoNumberValue + dublePart.getNumber() + "와 규격이 동일합니다." + pdmNumber
									+ ",";

							IBAUtil.changeIBAValue(epm, autoNumber, "FALSE");
							IBAUtil.changeIBAValue(epm, "message", autoNumberValue);
							log.exceptionLog("autoNumber =" + "FALSE");
							log.exceptionLog("message =" + autoNumberValue);
							logger.error("autoNumber =" + "FALSE");
							logger.error("message =" + autoNumberValue);
							return;
						} else {
							NumberCode specCode = NumberCodeHelper.manager.getNumberCodeName("SPEC", spec);
							if (specCode != null) {
								IBAUtil.changeIBAValue(epm, "Class4", specCode.getDescription());
							}

						}
					} else {
						IBAUtil.changeIBAValue(epm, autoNumber, "FALSE");
						IBAUtil.changeIBAValue(epm, "message", "SPEC 을 입력 하지 않았습니다.");
						log.exceptionLog("autoNumber =" + "FALSE");
						log.exceptionLog("message =" + "SPEC 을 입력 하지 않았습니다.");
						logger.error("autoNumber =" + "FALSE");
						logger.error("message =" + "SPEC 을 입력 하지 않았습니다.");
						return;
					}
				}
			}

			/* Sequence Number */
			String serialNum = getPdmSerialNumber2(pdmNumber, autoSerialNumber);
			if (serialNum.length() == 0) {
				log.exceptionLog("채번시 에러가 발생 하였습니다. =" + pdmNumber);
				logger.error("채번시 에러가 발생 하였습니다. =" + pdmNumber);
				IBAUtil.changeIBAValue(epm, autoNumber, "FALSE");
				IBAUtil.changeIBAValue(epm, "message", "채번시 에러가 발생 하였습니다. =" + pdmNumber);
				return;
			}
			pdmNumber = pdmNumber + "-" + serialNum;

			/* Cad Namue = Number+확장자 */
			String cadExtension = getCadExtension(epm.getCADName());
			String cadName = pdmNumber + cadExtension;
			cadName = cadName.toLowerCase();
			logger.debug("epm.isGeneric()=" + epm.isGeneric() + "\tepm.isInstance()=" + epm.isInstance());
			/*
			 * Number ,Name ,CadName Change ,FamilyTable(generic,instance) Number,Name
			 * Change
			 */
			if (epm.isGeneric() || epm.isInstance()) {
				logger.debug("CadInfoChange.manager.epmInfoChange run epm =" + epm.getNumber() + "\tpdmNumber="
						+ pdmNumber + "\tpdmName=" + pdmName);
				isNumber = CadInfoChange.manager.epmInfoChange(epm, pdmNumber, pdmName);
				logger.debug("CadInfoChange.manager.epmInfoChange run end epm =" + epm.getNumber() + "\tpdmNumber="
						+ pdmNumber + "\tpdmName=" + pdmName);
			} else {
				try {
					logger.debug("CadInfoChange.manager.epmCadInfoChange run epm =" + epm.getNumber() + "\tpdmNumber="
							+ pdmNumber + "\tpdmName=" + pdmName + "\tcadName=" + cadName);
					isNumber = CadInfoChange.manager.epmCadInfoChange(epm, pdmNumber, pdmName, cadName);
					logger.debug("CadInfoChange.manager.epmCadInfoChange run end epm =" + epm.getNumber()
							+ "\tpdmNumber=" + pdmNumber + "\tpdmName=" + pdmName + "\tcadName=" + cadName);
				} catch (Exception e) {
					log.errLog(e.getMessage());
					logger.error(e.getMessage());
					e.printStackTrace();
				}
			}

			/* CADCOMPONENT 인 경우에 */
			if (epm.getAuthoringApplication().toString().equals("PROE")
					&& epm.getDocType().toString().equals("CADCOMPONENT")) {
				// String material = (String)ibaAttr.get("Material"); //PTC_MATERIAL_NAME
				String material = (ibaAttr != null)
						? StringUtil.checkNull(StringUtil.trim((String) ibaAttr.get("Material")))
						: "";

				NumberCode mCode = null;
				if (material.length() > 0) {
					mCode = NumberCodeHelper.manager.getNumberCodeName("MATERIAL", material);
					if (mCode == null) {
						autoNumberValue = autoNumberValue + "입력하신 Material의 정보를 찾을 수 없습니다.";
						log.exceptionLog(autoNumberValue);
						logger.error(autoNumberValue);
					}
				} else {
					autoNumberValue = autoNumberValue + "Material의 정보를 입력 하지 않았습니다..";
					log.exceptionLog(autoNumberValue);
					logger.error(autoNumberValue);
				}
			}

			if (epm.getAuthoringApplication().toString().equals("ACAD")
					|| epm.getDocType().toString().equals("CADASSEMBLY")) {
				// String material = (String)ibaAttr.get("Mat"); //AutoCad,Pro/E .asm
				String material = (ibaAttr != null) ? StringUtil.checkNull(StringUtil.trim((String) ibaAttr.get("Mat")))
						: "";
				NumberCode mCode = null;
				if (material.length() > 0) {
					mCode = NumberCodeHelper.manager.getNumberCodeName("MATERIAL", material);
					if (mCode == null) {
						autoNumberValue = autoNumberValue + "입력하신 MAT 정보를 찾을 수 없습니다.";
						log.exceptionLog(autoNumberValue);
						logger.error(autoNumberValue);
					}
				}
			}

			// if(epm.getDocType().toString().equals("CADCOMPONENT") ){
			if (!epm.getAuthoringApplication().toString().equals("ACAD")) {
				if ("B".equals(type)) {
					if (spec.length() > 0) {
						NumberCode sCode = NumberCodeHelper.manager.getNumberCodeName("SPEC", spec);
						if (sCode == null) {
							autoNumberValue = autoNumberValue + "(" + spec + ")입력하신 규격의 정보를 찾을 수 없습니다 ,";
							log.exceptionLog(autoNumberValue);
							logger.error(autoNumberValue);
						}
					} else {
						autoNumberValue = autoNumberValue + "규격을  입력 하지 않았습니다 ,";
						log.exceptionLog(autoNumberValue);
						logger.error(autoNumberValue);
					}
				}

				if (maker.length() > 0) {
					HashMap erpMap = new HashMap();
					erpMap.put("makerName", maker);
					erpMap.put("searchType", "equals");
					ResultSet rs = ERPSearchHelper.manager.getErpMaker(erpMap);
					if (!rs.next()) {
						autoNumberValue = autoNumberValue + "(" + maker + ")" + "입력하신 Maker의 정보를 찾을 수 없습니다";
						log.exceptionLog(autoNumberValue);
						logger.error(autoNumberValue);
					}

				}
			}
			/* WTPART 속성 Check End */

			/* autoNumber ,message */
			if (isNumber) {
				IBAUtil.changeIBAValue(epm, autoNumber, "TRUE");
				log.infoLog("Final Number Change autoNumber = TRUE");
				logger.debug("Final Number Change autoNumber = TRUE");
			} else {
				IBAUtil.changeIBAValue(epm, autoNumber, "FALSE");
				log.infoLog("Final Number Change autoNumber = FALSE");
				logger.debug("Final Number Change autoNumber = FALSE");
			}

			IBAUtil.changeIBAValue(epm, "message", autoNumberValue);
			log.infoLog(" Final message r = " + autoNumberValue);
			logger.debug(" Final message r = " + autoNumberValue);

		} catch (Exception e) {

			log.errLog(":::::::::::::::::: EPMDocument createEPMChange Exception  START::::::::::::::::::::::::");
			logger.error(":::::::::::::::::: EPMDocument createEPMChange Exception  START::::::::::::::::::::::::");
			log.errLog(e.getMessage());
			e.printStackTrace();
			log.errLog(":::::::::::::::::: EPMDocument createEPMChange Exception  END::::::::::::::::::::::::");
			logger.error(":::::::::::::::::: EPMDocument createEPMChange Exception  END::::::::::::::::::::::::");
		}
		log.infoLog(":::::::::::::::::: EPMDocument createEPMChange END  ::::::::::::::::::::::::");
		logger.debug(":::::::::::::::::: EPMDocument createEPMChange END  ::::::::::::::::::::::::");
	}
}
