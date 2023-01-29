/*
 * @(#) IBAUtil.java  Create on 2004. 9. 8.
 * Copyright (c) e3ps. All rights reserverd
 */
package ext.narae.util.iba;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;

import ext.narae.util.DateUtil;
import wt.clients.iba.container.NewValueCreator;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.doc.WTDocumentMasterIdentity;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentHelper;
import wt.epm.EPMDocumentMaster;
import wt.epm.EPMDocumentMasterIdentity;
import wt.fc.IdentificationObject;
import wt.fc.Identified;
import wt.fc.IdentityHelper;
import wt.fc.ObjectIdentifier;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.folder.CabinetBased;
import wt.iba.constraint.IBAConstraintException;
import wt.iba.definition.BooleanDefinition;
import wt.iba.definition.FloatDefinition;
import wt.iba.definition.FloatDefinitionReference;
import wt.iba.definition.IntegerDefinition;
import wt.iba.definition.StringDefinition;
import wt.iba.definition.StringDefinitionReference;
import wt.iba.definition.litedefinition.AbstractAttributeDefinizerNodeView;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.litedefinition.AttributeDefNodeView;
import wt.iba.definition.litedefinition.AttributeOrgNodeView;
import wt.iba.definition.litedefinition.FloatDefView;
import wt.iba.definition.litedefinition.IBAUtility;
import wt.iba.definition.litedefinition.StringDefView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.BooleanValue;
import wt.iba.value.DefaultAttributeContainer;
import wt.iba.value.FloatValue;
import wt.iba.value.IBAHolder;
import wt.iba.value.IBAHolderReference;
import wt.iba.value.IntegerValue;
import wt.iba.value.StringValue;
import wt.iba.value.litevalue.AbstractValueView;
import wt.iba.value.litevalue.FloatValueDefaultView;
import wt.iba.value.litevalue.StringValueDefaultView;
import wt.iba.value.litevalue.TimestampValueDefaultView;
import wt.iba.value.litevalue.UnitValueDefaultView;
import wt.iba.value.service.IBAValueHelper;
import wt.part.WTPartMasterIdentity;
import wt.pds.StatementSpec;
import wt.query.ArrayExpression;
import wt.query.AttributeRange;
import wt.query.ClassAttribute;
import wt.query.ConstantExpression;
import wt.query.QuerySpec;
import wt.query.RangeExpression;
import wt.query.RelationalExpression;
import wt.query.SearchCondition;
import wt.query.specification.AttributeSearchExp;
import wt.query.specification.AttributeValueCriteria;
import wt.query.specification.BinaryOperator;
import wt.query.specification.BinaryValue;
import wt.query.specification.NaryOperator;
import wt.query.specification.NaryValue;
import wt.query.specification.OperatorType;
import wt.query.specification.UnaryOperator;
import wt.query.specification.UnaryValue;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.Iterated;

public class IBAUtil {
	/**
	 * 해당 객체의 속성 컨테이너를 반환한다.
	 *
	 * @param attrs  key가 속성이름이고, value가 속성객체인 Hashtable
	 * @param values key가 속성이름이고, value가 저장할 속성값인 Hashtable
	 * @return 속성값이 저장된 컨테이너를 반환
	 * @throws IBAConstraintException
	 * @throws WTPropertyVetoException
	 */
	public static DefaultAttributeContainer getAttrContainer(Hashtable attrs, Hashtable values)
			throws IBAConstraintException, WTPropertyVetoException {
		DefaultAttributeContainer container = new DefaultAttributeContainer();
		Enumeration eu = attrs.keys();

		String key = null;
		Object value = null;
		AttributeDefDefaultView defDefaultView;
		AbstractValueView valueView;

		while (eu.hasMoreElements()) {
			key = (String) eu.nextElement();
			value = values.get(key);
			defDefaultView = (AttributeDefDefaultView) attrs.get(key);

			if (defDefaultView != null && value != null) {
				valueView = (AbstractValueView) NewValueCreator.createNewValueObject(defDefaultView);
				setContainerValue(container, valueView, value);
			}
		}

		return container;
	}

	/**
	 * 컨테이너에 각각의 속성에 값을 저장한다.
	 *
	 * @param container
	 * @param valueView
	 * @param valObj
	 * @throws WTPropertyVetoException
	 * @throws IBAConstraintException
	 */
	private static void setContainerValue(DefaultAttributeContainer container, AbstractValueView valueView,
			Object valObj) throws WTPropertyVetoException, IBAConstraintException {
		if (valueView instanceof StringValueDefaultView) {
			StringValueDefaultView view = (StringValueDefaultView) valueView;
			view.setValue((String) valObj);
			container.addAttributeValue(view);
		} else if (valueView instanceof FloatValueDefaultView) {
			FloatValueDefaultView view = (FloatValueDefaultView) valueView;
			view.setValue(Double.parseDouble((String) valObj));
			container.addAttributeValue(view);
		} else if (valueView instanceof TimestampValueDefaultView) {
			TimestampValueDefaultView view = (TimestampValueDefaultView) valueView;
			view.setValue((Timestamp) valObj);
			container.addAttributeValue(view);
		}
	}

	private static AttributeDefDefaultView getDefaultViewObject(Object obj) throws Exception {
		AttributeDefDefaultView defDefaultView = null;
		if (obj instanceof AttributeDefNodeView)
			defDefaultView = IBADefinitionHelper.service.getAttributeDefDefaultView((AttributeDefNodeView) obj);
		return defDefaultView;
	}

	public static String getAttrValue2(IBAHolder ibaHolder, String attrName) throws RemoteException, WTException {
		String returnStr = "";
		Class target = ibaHolder.getClass();

		QuerySpec select = new QuerySpec();

		int idx = select.addClassList(target, false);
		int idx_StrVal = select.addClassList(StringValue.class, false);
		int idx_StrDef = select.addClassList(StringDefinition.class, false);
		long lon = ((Persistable) ibaHolder).getPersistInfo().getObjectIdentifier().getId();
		select.appendWhere(new SearchCondition(target, "thePersistInfo.theObjectIdentifier.id", "=", lon),
				new int[] { idx });
		select.appendAnd();
		select.appendSelect(new ClassAttribute(StringValue.class, "value2"), new int[] { idx_StrVal }, false);

		SearchCondition where = new SearchCondition(
				new ClassAttribute(StringValue.class, "theIBAHolderReference.key.id"), "=",
				new ClassAttribute(target, "thePersistInfo.theObjectIdentifier.id"));
		select.appendWhere(where, new int[] { idx_StrVal, idx });
		select.appendAnd();
		where = new SearchCondition(new ClassAttribute(StringValue.class, "definitionReference.hierarchyID"), "=",
				new ClassAttribute(StringDefinition.class, "hierarchyID"));
		select.appendWhere(where, new int[] { idx_StrVal, idx_StrDef });
		select.appendAnd();
		select.appendWhere(new SearchCondition(StringDefinition.class, "name", "=", attrName),
				new int[] { idx_StrDef });

		QueryResult re = PersistenceHelper.manager.find(select);
		while (re.hasMoreElements()) {
			Object[] obj = (Object[]) re.nextElement();
			returnStr = (String) obj[0];
		}

		return returnStr;
	}

	public static String getAttrValue(IBAHolder ibaHolder, String attrName) throws RemoteException, WTException {
		String returnStr = "";
		Class target = ibaHolder.getClass();

		QuerySpec select = new QuerySpec();

		int idx = select.addClassList(target, false);
		int idx_StrVal = select.addClassList(StringValue.class, false);
		int idx_StrDef = select.addClassList(StringDefinition.class, false);
		long lon = ((Persistable) ibaHolder).getPersistInfo().getObjectIdentifier().getId();
		select.appendWhere(new SearchCondition(target, "thePersistInfo.theObjectIdentifier.id", "=", lon),
				new int[] { idx });
		select.appendAnd();
		select.appendSelect(new ClassAttribute(StringValue.class, "value"), new int[] { idx_StrVal }, false);

		SearchCondition where = new SearchCondition(
				new ClassAttribute(StringValue.class, "theIBAHolderReference.key.id"), "=",
				new ClassAttribute(target, "thePersistInfo.theObjectIdentifier.id"));
		select.appendWhere(where, new int[] { idx_StrVal, idx });
		select.appendAnd();
		where = new SearchCondition(new ClassAttribute(StringValue.class, "definitionReference.hierarchyID"), "=",
				new ClassAttribute(StringDefinition.class, "hierarchyID"));
		select.appendWhere(where, new int[] { idx_StrVal, idx_StrDef });
		select.appendAnd();
		select.appendWhere(new SearchCondition(StringDefinition.class, "name", "=", attrName),
				new int[] { idx_StrDef });

		QueryResult re = PersistenceHelper.manager.find(select);
		while (re.hasMoreElements()) {
			Object[] obj = (Object[]) re.nextElement();
			returnStr = (String) obj[0];
		}

		return returnStr;
	}

	/**
	 *
	 * @param ibaHolder
	 * @return key:attr_name,value:attr_value
	 * @throws RemoteException
	 * @throws WTException
	 */
	public static HashMap getAttributes(IBAHolder ibaHolder) throws RemoteException, WTException {
		IBAHolder iba = IBAValueHelper.service.refreshAttributeContainer(ibaHolder, null, null, null);
		DefaultAttributeContainer container = (DefaultAttributeContainer) iba.getAttributeContainer();
		HashMap hash = new HashMap();
		if (container == null)
			return hash;

		AbstractValueView[] avv = container.getAttributeValues();
		for (int i = 0; i < avv.length; i++) {
			if (avv[i] instanceof StringValueDefaultView) {
				StringValueDefaultView sv = (StringValueDefaultView) avv[i];
				hash.put(sv.getDefinition().getName(), sv.getValueAsString());
			} else if (avv[i] instanceof FloatValueDefaultView) {
				FloatValueDefaultView fv = (FloatValueDefaultView) avv[i];
				hash.put(fv.getDefinition().getName(), fv.getValueAsString());
			} else if (avv[i] instanceof TimestampValueDefaultView) {
				TimestampValueDefaultView fv = (TimestampValueDefaultView) avv[i];
				hash.put(fv.getDefinition().getName(), fv.getValueAsString().substring(0, 10));
			}
		}
		return hash;
	}

	public static HashMap getAttributesHidNViewObject(IBAHolder ibaHolder)
			throws RemoteException, WTException, NumberFormatException, WTPropertyVetoException {
		IBAHolder iba = IBAValueHelper.service.refreshAttributeContainer(ibaHolder, null, null, null);
		DefaultAttributeContainer container = (DefaultAttributeContainer) iba.getAttributeContainer();
		HashMap hash = new HashMap();
		if (container == null)
			return hash;

		AbstractValueView[] avv = container.getAttributeValues();
		for (int i = 0; i < avv.length; i++) {
			if (avv[i] instanceof StringValueDefaultView) {
				StringValueDefaultView sv = (StringValueDefaultView) avv[i];
				hash.put(sv.getDefinition().getHierarchyID(), sv);
			} else if (avv[i] instanceof FloatValueDefaultView) {
				FloatValueDefaultView fv = (FloatValueDefaultView) avv[i];
				hash.put(fv.getDefinition().getHierarchyID(), fv);
			} else if (avv[i] instanceof TimestampValueDefaultView) {
				TimestampValueDefaultView fv = (TimestampValueDefaultView) avv[i];
				hash.put(fv.getDefinition().getHierarchyID(), fv);
			}
		}
		return hash;
	}

	public static void changeIBAValue(IBAHolder ibaHolder, String varName, String value)
			throws NumberFormatException, RemoteException, WTPropertyVetoException, WTException {
		if (value == null) {
			return;
		} else if (value.length() == 0) {
			deleteIBA(ibaHolder, varName);
			return;
		}
		// HashMap map = getAttributes(ibaHolder);
		AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(varName);
		// AbstractValueView valueView = (AbstractValueView)
		// NewValueCreator.createNewValueObject(aview);

		HashMap my = getAttributesHidNViewObject(ibaHolder);
		String hid = IBAUtility.numericID(varName);

		if (!my.containsKey(hid)) {
			if (aview instanceof StringDefView)
				createIba(ibaHolder, "string", varName, value);
			else if (aview instanceof FloatDefView)
				createIba(ibaHolder, "float", varName, value);
		} else {
			Object obj = my.get(hid);
			if (obj instanceof StringValueDefaultView) {
				StringValueDefaultView sv = (StringValueDefaultView) obj;
				ReferenceFactory rf = new ReferenceFactory();
				StringValue s = (StringValue) rf.getReference(sv.getObjectID().toString()).getObject();
				s.setValue(value);
				PersistenceHelper.manager.modify(s);
			} else if (obj instanceof FloatValueDefaultView) {
				FloatValueDefaultView sv = (FloatValueDefaultView) obj;
				ReferenceFactory rf = new ReferenceFactory();
				FloatValue s = (FloatValue) rf.getReference(sv.getObjectID().toString()).getObject();
				s.setValue(Double.parseDouble(value));
				PersistenceHelper.manager.modify(s);
			}
		}
	}

	/**
	 * IBA 속성을 삭제한다.
	 *
	 * @param ibaHolder
	 * @param varName   속성명
	 * @throws NumberFormatException
	 * @throws RemoteException
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 */
	public static void deleteIBA(IBAHolder ibaHolder, String varName)
			throws NumberFormatException, RemoteException, WTPropertyVetoException, WTException {
		if (ibaHolder == null) {
			return;
		}
		HashMap my = getAttributesHidNViewObject(ibaHolder);
		String hid = IBAUtility.numericID(varName);
		if (my.containsKey(hid)) {
			Object obj = my.get(hid);
			ReferenceFactory rf = new ReferenceFactory();
			if (obj instanceof StringValueDefaultView) {
				StringValueDefaultView sv = (StringValueDefaultView) obj;
				StringValue s = (StringValue) rf.getReference(sv.getObjectID().toString()).getObject();
				PersistenceHelper.manager.delete(s);
			} else if (obj instanceof FloatValueDefaultView) {
				FloatValueDefaultView sv = (FloatValueDefaultView) obj;
				FloatValue s = (FloatValue) rf.getReference(sv.getObjectID().toString()).getObject();
				PersistenceHelper.manager.delete(s);
			}
		}
	}

	private static String getOperator(AttributeSearchExp searchExp) {
		OperatorType operType = searchExp.getOperator();
		String oper = null;
		if (operType.equals(UnaryOperator.EQUAL))
			oper = "=";
		else if (operType.equals(UnaryOperator.GREATER))
			oper = ">";
		else if (operType.equals(UnaryOperator.GREATER_EQUAL))
			oper = ">=";
		else if (operType.equals(UnaryOperator.IS_NOT_NULL))
			oper = "IS NOT NULL";
		else if (operType.equals(UnaryOperator.IS_NULL))
			oper = "IS NULL";
		else if (operType.equals(UnaryOperator.LESS))
			oper = "<";
		else if (operType.equals(UnaryOperator.LESS_EQUAL))
			oper = "<=";
		else if (operType.equals(UnaryOperator.LIKE))
			oper = " LIKE ";
		else if (operType.equals(UnaryOperator.NOT_EQUAL))
			oper = "<>";
		else if (operType.equals(UnaryOperator.NOT_LIKE))
			oper = " NOT LIKE ";
		else if (operType.equals(BinaryOperator.BETWEEN))
			oper = "BETWEEN";
		else if (operType.equals(BinaryOperator.NOT_BETWEEN))
			oper = "NOT BETWEEN";
		else if (operType.equals(NaryOperator.IN))
			oper = "IN";
		else if (operType.equals(NaryOperator.NOT_IN))
			oper = "NOT IN";
		return oper;
	}

	private static RelationalExpression getRelationExp(AttributeSearchExp searchExp) {
		Object obj = null;
		AttributeValueCriteria attrValueCriteria = searchExp.getValue();
		if (attrValueCriteria instanceof UnaryValue) {
			Object obj1 = ((UnaryValue) attrValueCriteria).getValue();
			if (obj1 instanceof ObjectReference)
				obj = new ConstantExpression(new Long(((ObjectIdentifier) ((ObjectReference) obj1).getKey()).getId()));
			else
				try {
					obj = (RelationalExpression) obj1;
				} catch (ClassCastException e) {
					try {
						obj = new ConstantExpression(obj1);
					} catch (ClassCastException e1) {
						e1.printStackTrace();
					}
				}
		} else if (attrValueCriteria instanceof BinaryValue) {
			BinaryValue binaryvalue = (BinaryValue) attrValueCriteria;
			if (attrValueCriteria instanceof AttributeRange)
				obj = new RangeExpression((AttributeRange) attrValueCriteria);
			else
				try {
					AttributeRange attributerange = new AttributeRange();
					attributerange.setStart(binaryvalue.getStart());
					attributerange.setEnd(binaryvalue.getEnd());
					obj = new RangeExpression(attributerange);
				} catch (WTPropertyVetoException e) {
					e.printStackTrace();
				}
		} else if (attrValueCriteria instanceof NaryValue)
			obj = new ArrayExpression(((NaryValue) attrValueCriteria).getValue());
		return (RelationalExpression) obj;
	}

	/**
	 * EPMDocument의 번호와 이름을 변경한다.
	 *
	 * @param epm
	 * @param number
	 * @param name
	 * @param cadName
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void changeNumber(EPMDocument epm, String number, String name, String cadName)
			throws WTException, WTPropertyVetoException {
		// String pre_number = epm.getNumber();
		// String pre_name = epm.getName();
		String pre_cadName = epm.getCADName();
		// Logger.user.println("> IBAUtil.changeNumber : EPM no = " + epm.getNumber());
		// Logger.user.println("> IBAUtil.changeNumber : cadName = " + pre_cadName);

		boolean isChange = false;
		if (!epm.getNumber().equals(number) || !epm.getName().equals(name))
			isChange = true;

		if (isChange) {
			Identified identified = (Identified) epm.getMaster();

			EPMDocumentMasterIdentity emid = (EPMDocumentMasterIdentity) identified.getIdentificationObject();
			emid.setNumber(number.trim());
			emid.setName(name.trim());
			QueryResult qr = ContentHelper.service.getContentsByRole(epm, ContentRoleType.SECONDARY);
			while (qr.hasMoreElements()) {
				ContentItem item = (ContentItem) qr.nextElement();
				if (item instanceof ApplicationData) {
					ApplicationData ad = (ApplicationData) item;
					// Logger.user.println("> IBAUtil.changeNumber : ad.getFileName() = " +
					// ad.getFileName());
					if (!pre_cadName.equals(ad.getFileName()))
						EPMDocumentHelper.service.changeCADName((EPMDocumentMaster) epm.getMaster(), ad.getFileName());
				}
			}

			IdentityHelper.service.changeIdentity(identified, emid);
			PersistenceHelper.manager.refresh(epm);
		}
	}

	/**
	 * 부품 및 문서의 번호와 이름을 변경한다.
	 *
	 * @param oid
	 * @param number
	 * @param name
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void changeNumber(String oid, String number, String name)
			throws WTException, WTPropertyVetoException {
		number = number.trim();
		name = name.trim();
		ReferenceFactory rf = new ReferenceFactory();
		CabinetBased cabinetbased = (CabinetBased) rf.getReference(oid).getObject();
		IdentificationObject obj = ((Identified) ((Iterated) cabinetbased).getMaster()).getIdentificationObject();
		boolean flag = !name.equals(cabinetbased.getName()) && name.length() != 0;
		boolean flag1 = number.length() != 0;

		if (obj instanceof WTDocumentMasterIdentity) {
			String tempNumber = ((WTDocumentMasterIdentity) obj).getNumber();
			flag1 = flag1 && !tempNumber.equals(number);
			if (flag)
				((WTDocumentMasterIdentity) obj).setName(name);
			if (flag1)
				((WTDocumentMasterIdentity) obj).setNumber(number);
		} else if (obj instanceof WTPartMasterIdentity) {
			String tempNumber = ((WTPartMasterIdentity) obj).getNumber();
			flag1 = flag1 && !tempNumber.equals(number);
			if (flag)
				((WTPartMasterIdentity) obj).setName(name);
			if (flag1)
				((WTPartMasterIdentity) obj).setNumber(number);
		}
		if (flag || flag1) {
			IdentityHelper.service.changeIdentity((Identified) ((Iterated) cabinetbased).getMaster(), obj);
		}
	}

	public static void createIba(IBAHolder ibaHolder, String type, String attrName, String value) throws WTException {
		if (value == null)
			return;
		try {
			if ("string".equalsIgnoreCase(type)) {
				StringValue sv = new StringValue();
				StringDefinition sd = getStringDefinition(attrName);
				if (sd == null)
					return;
				deleteIBA(ibaHolder, attrName, type);

				sv.setValue(value);
				sv.setDefinitionReference((StringDefinitionReference) sd.getAttributeDefinitionReference());
				sv.setIBAHolderReference((IBAHolderReference.newIBAHolderReference(ibaHolder)));

				System.out.println("value=" + value);
				System.out.println("sv=" + attrName);

				PersistenceHelper.manager.save(sv);
			} else if ("float".equalsIgnoreCase(type)) {
				FloatValue fv = new FloatValue();
				FloatDefinition sd = getFloatDefinition(attrName);
				if (sd == null)
					return;
				deleteIBA(ibaHolder, attrName, type);

				fv.setValue(Double.parseDouble(value));
				fv.setDefinitionReference((FloatDefinitionReference) sd.getAttributeDefinitionReference());
				fv.setIBAHolderReference((IBAHolderReference.newIBAHolderReference(ibaHolder)));

				PersistenceHelper.manager.save(fv);
			}
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		}
	}

	private static StringDefinition getStringDefinition(String attrName) throws WTException {
		QuerySpec select = new QuerySpec();
		int idx = select.addClassList(StringDefinition.class, true);
		select.appendWhere(new SearchCondition(StringDefinition.class, "name", "=", attrName), new int[] { idx });
		QueryResult re = PersistenceHelper.manager.find(select);
		while (re.hasMoreElements()) {
			Object[] obj = (Object[]) re.nextElement();
			StringDefinition sv = (StringDefinition) obj[0];
			return sv;
		}
		return null;
	}

	private static FloatDefinition getFloatDefinition(String attrName) throws WTException {
		QuerySpec select = new QuerySpec();
		int idx = select.addClassList(FloatDefinition.class, true);
		select.appendWhere(new SearchCondition(FloatDefinition.class, "name", "=", attrName), new int[] { idx });
		QueryResult re = PersistenceHelper.manager.find(select);
		while (re.hasMoreElements()) {
			Object[] obj = (Object[]) re.nextElement();
			FloatDefinition sv = (FloatDefinition) obj[0];
			return sv;
		}
		return null;
	}

	private static BooleanDefinition getBooleanDefinition(String attrName) throws WTException {
		QuerySpec select = new QuerySpec();
		int idx = select.addClassList(BooleanDefinition.class, true);
		select.appendWhere(new SearchCondition(BooleanDefinition.class, "name", "=", attrName), new int[] { idx });
		QueryResult re = PersistenceHelper.manager.find(select);
		while (re.hasMoreElements()) {
			Object[] obj = (Object[]) re.nextElement();
			BooleanDefinition sv = (BooleanDefinition) obj[0];
			return sv;
		}
		return null;
	}

	private static IntegerDefinition getIntegerDefinition(String attrName) throws WTException {
		QuerySpec select = new QuerySpec();
		int idx = select.addClassList(IntegerDefinition.class, true);
		select.appendWhere(new SearchCondition(IntegerDefinition.class, "name", "=", attrName), new int[] { idx });
		QueryResult re = PersistenceHelper.manager.find(select);
		while (re.hasMoreElements()) {
			Object[] obj = (Object[]) re.nextElement();
			IntegerDefinition sv = (IntegerDefinition) obj[0];
			return sv;
		}
		return null;
	}

	private static double parseDouble(String str) {
		double dd;
		try {
			dd = Double.parseDouble(str);
		} catch (Exception e) {
			dd = 0;
		}
		return dd;
	}

	public static Hashtable settingAttribute(String orgName) {
		Hashtable hash = null;
		// if (orgName.equals("Part") || orgName.equals("PRODUCT"))
		// {
		// hash = settingAttribute("SHARE");
		// }
		// if (hash == null)
		hash = new Hashtable();

		AttributeDefDefaultView aview = null;
		AbstractValueView valueView = null;
		String hid = null;
		try {
			AttributeOrgNodeView orgNodeView[] = IBADefinitionHelper.service.getAttributeOrganizerRoots();
			for (int i = orgNodeView.length - 1; i > -1; i--) {
				String nodeName = orgNodeView[i].getName();
				if (nodeName.equalsIgnoreCase(orgName)) {
					AbstractAttributeDefinizerNodeView defNodeView[] = IBADefinitionHelper.service
							.getAttributeChildren(orgNodeView[i]);
					// softtype attributes
					for (int j = 0; j < defNodeView.length; j++) {
						aview = getDefaultViewObject((AttributeDefNodeView) defNodeView[j]);
						valueView = (AbstractValueView) NewValueCreator.createNewValueObject(aview);
						hid = aview.getHierarchyID();
						if (valueView instanceof UnitValueDefaultView) {
							hash.put(hid, new AttributeData(aview.getLocalizedDisplayString(), aview,
									((UnitValueDefaultView) valueView).toUnit().getUnits()));
						} else if (valueView instanceof StringValueDefaultView) {
							hash.put(hid, new AttributeData(aview.getName(), aview.getLocalizedDisplayString(), aview,
									"string"));
						} else if (valueView instanceof FloatValueDefaultView) {
							hash.put(hid, new AttributeData(aview.getName(), aview.getLocalizedDisplayString(), aview,
									"float"));
						} else if (valueView instanceof TimestampValueDefaultView) {
							hash.put(hid, new AttributeData(aview.getName(), aview.getLocalizedDisplayString(), aview,
									"timestamp"));
						}
					}
					break;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return hash;
	}

	/**
	 * 특정 속성 노드에 연결된 속성을 찾는다.
	 *
	 * @param orgName
	 * @return key:속성이름,value:AttributeData
	 */
	public static Hashtable getIBAAttributes(String orgName) {
		Hashtable hash = new Hashtable();

		AttributeDefDefaultView aview = null;
		AbstractValueView valueView = null;
		try {
			AttributeOrgNodeView orgNodeView[] = IBADefinitionHelper.service.getAttributeOrganizerRoots();
			for (int i = orgNodeView.length - 1; i > -1; i--) {
				String nodeName = orgNodeView[i].getName();
				if (nodeName.equalsIgnoreCase(orgName)) {
					AbstractAttributeDefinizerNodeView defNodeView[] = IBADefinitionHelper.service
							.getAttributeChildren(orgNodeView[i]);
					// softtype attributes
					for (int j = 0; j < defNodeView.length; j++) {
						aview = getDefaultViewObject((AttributeDefNodeView) defNodeView[j]);
						valueView = (AbstractValueView) NewValueCreator.createNewValueObject(aview);
						if (valueView instanceof UnitValueDefaultView)
							hash.put(aview.getName(),
									new AttributeData(aview.getName(), aview.getLocalizedDisplayString(), aview,
											((UnitValueDefaultView) valueView).toUnit().getUnits()));
						else if (valueView instanceof StringValueDefaultView)
							hash.put(aview.getName(), new AttributeData(aview.getName(),
									aview.getLocalizedDisplayString(), aview, "string"));
						else if (valueView instanceof FloatValueDefaultView)
							hash.put(aview.getName(), new AttributeData(aview.getName(),
									aview.getLocalizedDisplayString(), aview, "float"));
						else if (valueView instanceof TimestampValueDefaultView)
							hash.put(aview.getName(), new AttributeData(aview.getName(),
									aview.getLocalizedDisplayString(), aview, "timestamp"));
					}
					break;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return hash;
	}

	/**
	 * 모든 IBA 리턴
	 * 
	 * @return
	 */
	public static Hashtable getIBAAttributes() {
		Hashtable hash = new Hashtable();

		AttributeDefDefaultView aview = null;
		AbstractValueView valueView = null;
		try {
			AttributeOrgNodeView orgNodeView[] = IBADefinitionHelper.service.getAttributeOrganizerRoots();
			for (int i = orgNodeView.length - 1; i > -1; i--) {
				AbstractAttributeDefinizerNodeView defNodeView[] = IBADefinitionHelper.service
						.getAttributeChildren(orgNodeView[i]);
				for (int j = 0; j < defNodeView.length; j++) {
					aview = getDefaultViewObject((AttributeDefNodeView) defNodeView[j]);
					valueView = (AbstractValueView) NewValueCreator.createNewValueObject(aview);
					if (valueView instanceof UnitValueDefaultView)
						hash.put(aview.getName(), new AttributeData(aview.getName(), aview.getLocalizedDisplayString(),
								aview, ((UnitValueDefaultView) valueView).toUnit().getUnits()));
					else if (valueView instanceof StringValueDefaultView)
						hash.put(aview.getName(),
								new AttributeData(aview.getName(), aview.getLocalizedDisplayString(), aview, "string"));
					else if (valueView instanceof FloatValueDefaultView)
						hash.put(aview.getName(),
								new AttributeData(aview.getName(), aview.getLocalizedDisplayString(), aview, "float"));
					else if (valueView instanceof TimestampValueDefaultView)
						hash.put(aview.getName(), new AttributeData(aview.getName(), aview.getLocalizedDisplayString(),
								aview, "timestamp"));
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return hash;
	}

	public static Hashtable setValueToAttributeData(Hashtable ibaHash, Hashtable values) {
		String key = null;
		AttributeData adata = null;
		Enumeration eu = ibaHash.keys();
		while (eu.hasMoreElements()) {
			key = (String) eu.nextElement();
			adata = (AttributeData) ibaHash.get(key);
			adata.value = (String) values.get(key);
		}
		return ibaHash;
	}

	public static DefaultAttributeContainer updateAttribute(IBAHolder ibaHolder, Hashtable hash) {
		DefaultAttributeContainer container = null;
		try {
			IBAHolder iba = IBAValueHelper.service.refreshAttributeContainer(ibaHolder, null, null, null);
			container = (DefaultAttributeContainer) iba.getAttributeContainer();
			if (container == null)
				container = new DefaultAttributeContainer();

			AttributeDefDefaultView definitions[] = container.getAttributeDefinitions();
			Enumeration eu = hash.keys();

			String key = null;
			AttributeData data = null;
			String dataType = null;
			AttributeDefDefaultView aview = null;
			while (eu.hasMoreElements()) {
				key = (String) eu.nextElement();
				data = (AttributeData) hash.get(key);
				if (data.value == null)
					continue;

				dataType = data.dataType;
				// Logger.user.println("> IBAUtil.updateAttribute : dataType = " + dataType + "
				// data.value = " + data.value);

				aview = data.aview;
				boolean flag = false;
				if (definitions != null) {
					// Logger.user.println("> IBAUtil.updateAttribute : definitions.length " +
					// definitions.length);
					for (int i = 0; i < definitions.length; i++) {
						if (definitions[i].isPersistedObjectEqual(aview)) {
							flag = true;
							AbstractValueView aabstractvalueview[] = container.getAttributeValues(definitions[i]);
							if (data.value.trim().length() == 0) {
								container.deleteAttributeValues(definitions[i]);
								break;
							}
							if (dataType.equals("string")) {
								StringValueDefaultView sview = (StringValueDefaultView) aabstractvalueview[0];
								sview.setValue(data.value);
								container.updateAttributeValue(sview);
								break;
							} else if (dataType.equals("float")) {
								FloatValueDefaultView fview = (FloatValueDefaultView) aabstractvalueview[0];
								fview.setValue(parseDouble(data.value));
								container.updateAttributeValue(fview);
								break;
							} else if (dataType.equals("timestamp")) {
								TimestampValueDefaultView view = (TimestampValueDefaultView) aabstractvalueview[0];
								view.setValue(DateUtil.convertDate(data.value));
								container.updateAttributeValue(view);
								break;
							} else {
								UnitValueDefaultView uview = (UnitValueDefaultView) aabstractvalueview[0];
								uview.setValue(parseDouble(data.value));
								container.updateAttributeValue(uview);
								break;
							}
						} // if
					} // for
				} // if

				if (!flag) {
					if (data.value == null || data.value.trim().length() == 0)
						continue;

					if (dataType.equals("string")) {
						StringValueDefaultView sview = (StringValueDefaultView) NewValueCreator
								.createNewValueObject(aview);
						sview.setValue(data.value);
						container.addAttributeValue(sview);
					} else if (dataType.equals("float")) {
						FloatValueDefaultView fview = (FloatValueDefaultView) NewValueCreator
								.createNewValueObject(aview);
						fview.setValue(parseDouble(data.value));
						container.addAttributeValue(fview);
					} else if (dataType.equals("timestamp")) {
						TimestampValueDefaultView view = (TimestampValueDefaultView) NewValueCreator
								.createNewValueObject(aview);
						view.setValue(DateUtil.convertDate(data.value));
						container.addAttributeValue(view);
					} else {
						UnitValueDefaultView uview = (UnitValueDefaultView) NewValueCreator.createNewValueObject(aview);
						uview.setValue(parseDouble(data.value));
						container.addAttributeValue(uview);
					}
				}
			} // while
		} catch (WTException e) {
			e.printStackTrace();
			container = null;
		} catch (RemoteException e) {
			container = null;
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			container = null;
			e.printStackTrace();
		}

		return container;
	}

	public static Hashtable setAttribute(IBAHolder ibaHolder, Hashtable achash) throws RemoteException, WTException {
		IBAHolder iba = IBAValueHelper.service.refreshAttributeContainer(ibaHolder, null, null, null);
		DefaultAttributeContainer container = (DefaultAttributeContainer) iba.getAttributeContainer();
		Hashtable thash = new Hashtable();
		if (container == null)
			return thash;

		AttributeDefDefaultView definitions[] = container.getAttributeDefinitions();

		for (int i = definitions.length - 1; i > -1; i--) {
			String key = definitions[i].getHierarchyID();
			AttributeData data = (AttributeData) achash.get(key);
			if (data == null)
				continue;
			AttributeDefDefaultView av = data.aview;
			if (definitions[i].isPersistedObjectEqual(av)) {
				AbstractValueView aabstractvalueview[] = container.getAttributeValues(definitions[i]);
				if (aabstractvalueview[0] instanceof StringValueDefaultView) {
					StringValueDefaultView bb = (StringValueDefaultView) aabstractvalueview[0];
					// if (bb.getValue() == null)
					// thash.put(key, "");
					// else
					thash.put(key, bb.getValueAsString());
				} else if (aabstractvalueview[0] instanceof FloatValueDefaultView) {
					FloatValueDefaultView bb = (FloatValueDefaultView) aabstractvalueview[0];
					thash.put(key, bb.getValueAsString());
				} else if (aabstractvalueview[0] instanceof UnitValueDefaultView) {
					UnitValueDefaultView bb = (UnitValueDefaultView) aabstractvalueview[0];
					thash.put(key, bb.getValueAsString());
				}
			}
		}
		return thash;
	}

	public static void deleteIBA(IBAHolder holder, String attrName, String type) {
		try {
			if ("s".equals(type) || "string".equals(type)) {
				StringDefinition sd = getStringDefinition(attrName);
				ArrayList<StringValue> list = getStringValues(holder, sd);
				for (StringValue sv : list) {
					PersistenceHelper.manager.delete(sv);
				}
			} else if ("f".equals(type) || "float".equals(type)) {
				FloatDefinition fd = getFloatDefinition(attrName);
				ArrayList<FloatValue> list = getFloatValues(holder, fd);
				for (FloatValue fv : list) {
					PersistenceHelper.manager.delete(fv);
				}
			} else if ("b".equals(type) || "boolean".equals(type)) {
				BooleanDefinition bd = getBooleanDefinition(attrName);
				ArrayList<BooleanValue> list = getBooleanValues(holder, bd);
				for (BooleanValue bv : list) {
					PersistenceHelper.manager.delete(bv);
				}
			} else if ("i".equals(type) || "integer".equals(type)) {
				IntegerDefinition id = getIntegerDefinition(attrName);
				ArrayList<IntegerValue> list = getIntegerValues(holder, id);
				for (IntegerValue iv : list) {
					PersistenceHelper.manager.delete(iv);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static ArrayList<StringValue> getStringValues(IBAHolder holder, StringDefinition sd) throws Exception {
		ArrayList<StringValue> list = new ArrayList<StringValue>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(StringValue.class, true);

		Persistable per = (Persistable) holder;

		SearchCondition sc = new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", "=",
				per.getPersistInfo().getObjectIdentifier().getId());
		query.appendWhere(sc, new int[] { idx });
		query.appendAnd();

		sc = new SearchCondition(StringValue.class, "definitionReference.key.id", "=",
				sd.getPersistInfo().getObjectIdentifier().getId());
		query.appendWhere(sc, new int[] { idx });

		QueryResult result = PersistenceHelper.manager.find((StatementSpec) query);
		StringValue sv = null;
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			sv = (StringValue) obj[0];
			list.add(sv);
		}
		return list;
	}

	private static ArrayList<FloatValue> getFloatValues(IBAHolder holder, FloatDefinition fd) throws Exception {
		ArrayList<FloatValue> list = new ArrayList<FloatValue>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(FloatValue.class, true);

		Persistable per = (Persistable) holder;

		SearchCondition sc = new SearchCondition(FloatValue.class, "theIBAHolderReference.key.id", "=",
				per.getPersistInfo().getObjectIdentifier().getId());
		query.appendWhere(sc, new int[] { idx });
		query.appendAnd();

		sc = new SearchCondition(FloatValue.class, "definitionReference.key.id", "=",
				fd.getPersistInfo().getObjectIdentifier().getId());
		query.appendWhere(sc, new int[] { idx });

		QueryResult result = PersistenceHelper.manager.find((StatementSpec) query);
		FloatValue fv = null;
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			fv = (FloatValue) obj[0];
			list.add(fv);
		}
		return list;
	}

	private static ArrayList<IntegerValue> getIntegerValues(IBAHolder holder, IntegerDefinition id) throws Exception {
		ArrayList<IntegerValue> list = new ArrayList<IntegerValue>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(IntegerValue.class, true);

		Persistable per = (Persistable) holder;

		SearchCondition sc = new SearchCondition(IntegerValue.class, "theIBAHolderReference.key.id", "=",
				per.getPersistInfo().getObjectIdentifier().getId());
		query.appendWhere(sc, new int[] { idx });
		query.appendAnd();

		sc = new SearchCondition(IntegerValue.class, "definitionReference.key.id", "=",
				id.getPersistInfo().getObjectIdentifier().getId());
		query.appendWhere(sc, new int[] { idx });

		QueryResult result = PersistenceHelper.manager.find((StatementSpec) query);
		IntegerValue iv = null;
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			iv = (IntegerValue) obj[0];
			list.add(iv);
		}
		return list;
	}

	private static ArrayList<BooleanValue> getBooleanValues(IBAHolder holder, BooleanDefinition bd) throws Exception {
		ArrayList<BooleanValue> list = new ArrayList<BooleanValue>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(BooleanValue.class, true);

		Persistable per = (Persistable) holder;

		SearchCondition sc = new SearchCondition(BooleanValue.class, "theIBAHolderReference.key.id", "=",
				per.getPersistInfo().getObjectIdentifier().getId());
		query.appendWhere(sc, new int[] { idx });
		query.appendAnd();

		sc = new SearchCondition(BooleanValue.class, "definitionReference.key.id", "=",
				bd.getPersistInfo().getObjectIdentifier().getId());
		query.appendWhere(sc, new int[] { idx });

		QueryResult result = PersistenceHelper.manager.find((StatementSpec) query);
		BooleanValue bv = null;
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			bv = (BooleanValue) obj[0];
			list.add(bv);
		}
		return list;
	}

}
/*******************************************************************************
 * $Log: IBAUtil.java,v $ Revision 1.2 2011/05/23 05:51:11 thhwang
 *
 * Committed on the Free edition of March Hare Software CVSNT Server. Upgrade to
 * CVS Suite for more features and support: http://march-hare.com/cvsnt/
 *
 * Revision 1.1 2011/04/21 03:51:03 thhwang *** empty log message ***
 *
 * Revision 1.1 2011/03/08 01:50:11 thhwang 등록 Committed on the Free edition of
 * March Hare Software CVSNT Server. Upgrade to CVS Suite for more features and
 * support: http://march-hare.com/cvsnt/
 *
 * Revision 1.2 2010/06/07 02:52:53 hyun
 *
 * Committed on the Free edition of March Hare Software CVSNT Server. Upgrade to
 * CVS Suite for more features and support: http://march-hare.com/cvsnt/
 *
 * Revision 1.2 2009/09/03 00:43:43 administrator
 *
 * Committed on the Free edition of March Hare Software CVSNT Server. Upgrade to
 * CVS Suite for more features and support: http://march-hare.com/cvsnt/
 *
 * Revision 1.1 2009/02/25 01:26:26 smkim 최초 작성 Committed on the Free edition of
 * March Hare Software CVSNT Server. Upgrade to CVS Suite for more features and
 * support: http://march-hare.com/cvsnt/
 *
 * Revision 1.3 2008/10/02 06:20:53 sjhan *** empty log message ***
 *
 * Revision 1.2 2007/10/17 04:54:54 sjhan *** empty log message ***
 *
 * Revision 1.1.1.1 2007/04/17 01:21:26 administrator no message
 *
 * Revision 1.1.1.1 2007/02/14 07:53:53 administrator no message
 *
 * Revision 1.3 2006/10/11 05:32:03 shchoi *** empty log message ***
 *
 * Revision 1.2 2006/08/31 01:11:29 shchoi *** empty log message *** Revision
 * 1.1 2006/05/09 02:35:05 shchoi *** empty log message ***
 *
 * Revision 1.2 2006/02/10 08:31:52 shchoi *** empty log message ***
 *
 * Revision 1.1 2005/12/09 12:20:32 shchoi *** empty log message ***
 *
 * Revision 1.7 2005/12/06 05:15:35 shchoi *** empty log message *** Revision
 * 1.6 2005/12/01 08:26:51 shchoi 검색 수정
 *
 * Revision 1.5 2005/11/30 09:59:47 shchoi *** empty log message *** Revision
 * 1.4 2005/11/26 08:29:17 shchoi *** empty log message *** /* Revision 1.3
 * 2005/11/25 01:34:01 shchoi /* getIBAAttributes 메소드 추가 /
 ******************************************************************************/
