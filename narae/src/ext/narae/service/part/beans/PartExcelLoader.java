package ext.narae.service.part.beans;

import java.io.File;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;

import ext.narae.service.drawing.beans.EpmUtil;
import ext.narae.service.erp.beans.ERPSearchHelper;
import ext.narae.service.org.beans.UserHelper;
import ext.narae.util.CommonUtil;
import ext.narae.util.JExcelUtil;
import ext.narae.util.WCUtil;
import ext.narae.util.code.NumberCode;
import ext.narae.util.code.beans.NumberCodeHelper;
import ext.narae.util.iba.IBAUtil;
import jxl.Sheet;
import jxl.Workbook;
import wt.fc.PersistenceHelper;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
import wt.org.WTUser;
import wt.part.QuantityUnit;
import wt.part.WTPart;
import wt.util.WTException;

public class PartExcelLoader {
	private static final boolean VERBOSE = true;

	public static Vector upload(File newfile, Hashtable parm) {

		String number = "";
		String name = "";
		StringBuffer buffer = new StringBuffer();
		Vector<Hashtable> vec = new Vector<Hashtable>();
		Hashtable hash = null;

		try {
//        	System.out.println(":::::::::::::: newfile :" + newfile);
			Workbook wb = JExcelUtil.getWorkbook(newfile);
			String foderPath = "/Default/Part";
			Sheet[] sheets = wb.getSheets();
			int rows = sheets[0].getRows();
			String lifecycle = "DefaultLC";
			String wtPartType = "separable";
			String source = "make";
			String view = "Design";
			String state = "APPROVED";
			String fid = (String) parm.get("fid");
			String createType = (String) parm.get("createType");
//            System.out.println(">>>>>>>>>>>>>> rows :" + rows);
//            System.out.println(">>>>>>>>>>>>>> fid :" + fid);
//            System.out.println(">>>>>>>>>>>>>> createType :" + createType);

			boolean partSaveCheck = true;
			Hashtable specCheckHash = new Hashtable();

			for (int j = 1; j < rows; j++) {
				int h = 0;
				hash = new Hashtable();
				boolean saveCheck = true;

//                if ( (JExcelUtil.getContent(sheets[0].getRow(j), 7)).length()>0 ){

				boolean check = false;

				String oldNumber = JExcelUtil.getContent(sheets[0].getRow(j), h).trim(); // oldNumber
				String location = JExcelUtil.getContent(sheets[0].getRow(j), ++h).trim(); // Location
				location = foderPath + location;
				String quantityUnit = JExcelUtil.getContent(sheets[0].getRow(j), ++h).trim(); // 단위

				if (quantityUnit.toLowerCase().equals("roll")) {
					quantityUnit = "Roll";
				} else if (quantityUnit.toLowerCase().equals("대")) {
					quantityUnit = "dae";
				} else {
					quantityUnit = quantityUnit.toLowerCase();
				}

				String maker = JExcelUtil.getContent(sheets[0].getRow(j), ++h).trim(); // maker
				String spec = JExcelUtil.getContent(sheets[0].getRow(j), ++h).trim(); // 규격
				String isDrawing = JExcelUtil.getContent(sheets[0].getRow(j), ++h).trim(); // 도면 유무
				String designed1 = JExcelUtil.getContent(sheets[0].getRow(j), ++h).trim(); // 사번

				String group = JExcelUtil.getContent(sheets[0].getRow(j), ++h).trim(); // group
				if (group.length() > 0)
					group = group.toUpperCase();
				String type = JExcelUtil.getContent(sheets[0].getRow(j), ++h).trim(); // type
				if (type.length() > 0)
					type = type.toUpperCase();
				String unit = JExcelUtil.getContent(sheets[0].getRow(j), ++h).trim(); // unit
				if (unit.length() > 0)
					unit = unit.toUpperCase();
				String class1 = JExcelUtil.getContent(sheets[0].getRow(j), ++h).trim(); // class1
				if (class1.length() > 0)
					class1 = class1.toUpperCase();
				String class2 = JExcelUtil.getContent(sheets[0].getRow(j), ++h).trim(); // class2
				if (class2.length() > 0)
					class2 = class2.toUpperCase();
				String class3 = JExcelUtil.getContent(sheets[0].getRow(j), ++h).trim(); // class3
				if (class3.length() > 0)
					class3 = class3.toUpperCase();
				String class4 = JExcelUtil.getContent(sheets[0].getRow(j), ++h).trim(); // class4
				if (class4.length() > 0)
					class4 = class4.toUpperCase();

//                    System.out.println("oldNumber : " + oldNumber);
//                    System.out.println("location : " + location);
//                    System.out.println("quantityUnit : " + quantityUnit);
//                    System.out.println("maker : " + maker);
//                    System.out.println("spec : " + spec);
//                    System.out.println("group : " + group);
//                    System.out.println("unit : " + unit);
//                    System.out.println("class1 : " + class1 + " ::::: "+class1.length());
//                    System.out.println("class2 : " + class2+ " ::::: "+class2.length());
//                    System.out.println("class3 : " + class3+ " ::::: "+class3.length());
//                    System.out.println("class4 : " + class4+ " ::::: "+class4.length());

				hash.put("oldNumber", oldNumber); // 구번호
				hash.put("location", location); // 폴더
				hash.put("group", group); // Group
				hash.put("type", type); // Type
				hash.put("unit", unit); // Unit
				hash.put("class1", class1); // Class1
				hash.put("class2", class2); // Class2
				hash.put("class3", class3); // Class3
				hash.put("class4", class4); // Class4
				hash.put("maker", maker); // Maker
				hash.put("quantityunit", quantityUnit); // 단위
				hash.put("spec", spec); // 규격
				hash.put("isDrawing", isDrawing); // 도면유무
				hash.put("designed1", designed1); // userId
//                    System.out.println(">>>>>>>>>>>> "+j+":"+ oldNumber);

				// 품명
				Hashtable retHash = EpmUtil.checkPDMName(hash);
				String numberCheck = (String) retHash.get("numberCheck");
				String nameValue = (String) retHash.get("nameValue");
				String partNumber = (String) retHash.get("numberValue");

				if (numberCheck.equals("false")) {
					hash.put("rslt", "F");
					hash.put("msg", nameValue);
					vec.add(hash);
					partSaveCheck = false;
					saveCheck = false;
					continue;
				}

				hash.put("name", nameValue); // New Number

				// 품번
				hash.put("number", partNumber); //// New Name

				// 폴더
				if (fid.length() == 0) {
					try {
						Folder folder = FolderHelper.service.getFolder(location, WCUtil.getWTContainerRef());
						fid = CommonUtil.getOIDString(folder);
					} catch (WTException e) {
						hash.put("rslt", "F");
						hash.put("msg", "location 정보가 잘못 되었습니다.");
						vec.add(hash);
						partSaveCheck = false;
						saveCheck = false;
						continue;
					}
				}
				hash.put("fid", fid);

				// 단위
				QuantityUnit[] qa = QuantityUnit.getQuantityUnitSet();
				quantityUnit = getQuantityChange(quantityUnit);
				for (int i = 0; i < qa.length; i++) {
					// System.out.println( qa[i].toString().toLowerCase() +":"+
					// quantityUnit.toLowerCase());
					if (qa[i].toString().toLowerCase().equals(quantityUnit)) {
						check = true;
						break;
					}

				}

				if (!check) {
					hash.put("rslt", "F");
					hash.put("msg", "단위가 잘못 되었습니다.");
					vec.add(hash);
					partSaveCheck = false;
					saveCheck = false;
					continue;
				}

				// Maker
				if (maker != null && maker.length() > 0) {
					HashMap erpMap = new HashMap();
					erpMap.put("makerName", maker);
					erpMap.put("searchType", "equals");
					ResultSet rs = ERPSearchHelper.manager.getErpMaker(erpMap);
					if (!rs.next()) {
						hash.put("rslt", "F");
						hash.put("msg", "Maker가 잘못 되었습니다.");
						vec.add(hash);
						partSaveCheck = false;
						saveCheck = false;
						continue;
					}
				}

				// spec 규격
				if (spec != null && spec.length() > 0) {
					NumberCode code = NumberCodeHelper.manager.getNumberCodeNameEquals("SPEC", spec.trim());
					if (code == null) {
						hash.put("rslt", "F");
						hash.put("msg", "입력하신 규격을 찾을 수 없습니다.");
						vec.add(hash);
						partSaveCheck = false;
						saveCheck = false;
						continue;
					} else {
						if (createType.equals("old")) {
							if ("B".equals(type))
								hash.put("createType", createType);
						} else {
							if ("B".equals(type)) { // 구매품(B)일 경우에 체크
								Hashtable rtHas = PartSearchHelper.duplicationNumber(hash);

								String dubleCheck = (String) rtHas.get("return");
								String oid = (String) rtHas.get("oid");

								if (dubleCheck.equals("true")) {
									WTPart dublePart = (WTPart) CommonUtil.getObject(oid);

									hash.put("rslt", "F");
									hash.put("msg", dublePart.getNumber() + "와 품목번호와 규격이 동일합니다.");
									vec.add(hash);
									partSaveCheck = false;
									saveCheck = false;
									continue;
								} else {
//	        		            		System.out.println("partNumber ::::::: "+partNumber);
//	        		            		System.out.println("spec     ::::::: "+spec);
									ArrayList specArray = (ArrayList) specCheckHash.get(partNumber);
									if (specArray != null) {
										String specValue = (String) specArray.get(0);
										if (specValue != null && specValue.equals(spec)) {
											String lineValue = (String) specArray.get(1);
											hash.put("rslt", "F");
											hash.put("msg", "품목번호와 규격이 " + lineValue + "번째 line과 동일합니다.");
											vec.add(hash);
											partSaveCheck = false;
											saveCheck = false;
											continue;
										}
									} else {
										ArrayList specData = new ArrayList();
										specData.add(spec);
										specData.add(String.valueOf(j));
										specCheckHash.put(partNumber, specData);
									}
								}
							}
						}
					}
				} else {
					if ("B".equals(type)) { // 구매품(B)일 경우에 체크
						hash.put("rslt", "F");
						hash.put("msg", "구매품은 규격을 입력하셔야 합니다.");
						vec.add(hash);
						partSaveCheck = false;
						saveCheck = false;
						continue;
					}
				}

				// User
				if (designed1 != null && designed1.length() > 0) {
					WTUser tempUser = UserHelper.service.getUser(designed1);// wt.org.OrganizationServicesHelper.manager.getAuthenticatedUser(userId);
					String userOid = "";
					if (tempUser == null) {
						hash.put("rslt", "F");
						hash.put("msg", "입력하신 아이디에 해당되는 사용자를 찾을 수 없습니다.");
						vec.add(hash);
						partSaveCheck = false;
						saveCheck = false;
						continue;
					}
				}
				// 기타 부품 속성
				hash.put("lifecycle", lifecycle);
				hash.put("source", source);
				hash.put("view", view);
				hash.put("wtPartType", wtPartType);

				if (saveCheck) {
					hash.put("rslt", "C");
					vec.add(hash);
				}

//                }else {
//                	hash.put("rslt", "F");
//             	   	hash.put("msg", "Group값을 입력하여 주십시오.");
//             	   	partSaveCheck = false;
//                }
			}

			if (partSaveCheck) {
				Vector saveArray = new Vector();
				for (int i = 0; i < vec.size(); i++) {
					Hashtable saveData = (Hashtable) vec.get(i);
					boolean oldNumberCheck = true;

					if (createType.equals("old")) {
						String type = (String) saveData.get("type");
						/* 중복 체크 */
						if ("B".equals(type)) { // 구매품(B)일 경우에 체크
							Hashtable rtHas = PartSearchHelper.duplicationNumber(hash);

							String dubleCheck = (String) rtHas.get("return");
							String oid = (String) rtHas.get("oid");

							if (dubleCheck.equals("true")) {
								WTPart dublePart = (WTPart) CommonUtil.getObject(oid);
								String saveOldNumber = IBAUtil.getAttrValue(dublePart, "OldNumber");
								String oldNumber = (String) saveData.get("oldNumber");
								if (saveOldNumber.length() > 0)
									saveOldNumber = saveOldNumber + ",";
								saveOldNumber = saveOldNumber + oldNumber;

								IBAUtil.changeIBAValue(dublePart, "OldNumber", saveOldNumber);

								saveData.put("rslt", "C");
								saveData.put("msg", dublePart.getNumber() + "와 품목번호와 규격이 동일하므로 등록하지 않았습니다.");
								saveArray.add(saveData);
								oldNumberCheck = false;
								continue;
							}
						}
					}

					if (oldNumberCheck) {
						// Part 생성
						Hashtable retPart = PartHelper.manager.create(saveData);

						String rslt = (String) retPart.get("rslt");
						String msg = (String) retPart.get("msg");
						String oid = (String) retPart.get("oid");

						if (!rslt.equals("S")) {
							saveData.put("rslt", rslt);
							saveData.put("msg", msg);
						}

						WTPart part = (WTPart) CommonUtil.getObject(oid);

						System.out.println(
								i + ".Part Create : " + (String) saveData.get("oldNumber") + ":" + part.getNumber());
						saveData.put("rslt", rslt);
						saveData.put("msg", "부품 생성 성공");
						saveData.put("newNumber", part.getNumber());

						if (createType.equals("old")) {
							LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) part, State.toState(state));
						}
						part = (WTPart) PersistenceHelper.manager.refresh(part);

						saveData.put("state", part.getLifeCycleState().getDisplay(Locale.KOREAN));

						saveArray.add(saveData);

					}

				}
				return saveArray;
			} else
				return vec;

//           System.out.println(">>>>>>>>>> vec.size: " +vec.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vec;
	}

	public static String getQuantityChange(String unit) {
		unit = unit.toUpperCase();
		if ("EA".equals(unit)) {
			return "ea";
		} else if ("KG".equals(unit)) {
			return "kg";
		} else if ("M".equals(unit)) {
			return "m";
		} else if ("KM".equals(unit)) {
			return "km";
		} else if ("mm".equals(unit)) {
			return "kg";
		} else if ("ROLL".equals(unit)) {
			return "Roll";
		} else if ("SET".equals(unit)) {
			return "set";
		} else if ("DAE".equals(unit)) {
			return "dae";
		} else if ("INCH".equals(unit)) {
			return "inch";
		} else if ("CC".equals(unit)) {
			return "cc";
		}

		return "";
	}
}
