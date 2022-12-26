/* bcwti
 *
 * Copyright (c) 2008 Parametric Technology Corporation (PTC). All Rights
 * Reserved.
 *
 * This software is the confidential and proprietary information of PTC
 * and is subject to the terms of a software license agreement. You shall
 * not disclose such confidential information and shall use it only in accordance
 * with the terms of the license agreement.
 *
 * ecwti
 */

package ext.narae.service.erp;

import com.ptc.windchill.annotations.metadata.Cardinality;
import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.GeneratedRole;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyAccessors;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;
import com.ptc.windchill.annotations.metadata.Serialization;
import com.ptc.windchill.annotations.metadata.TableProperties;

import wt.fc.ObjectToObjectLink;
import wt.fc.Persistable;
import wt.util.WTException;

/**
 *
 * <p>
 * Use the <code>newERPPartLinkO</code> static factory method(s), not the
 * <code>ERPPartLinkO</code> constructor, to construct instances of this class.
 * Instances must be constructed using the static factory(s), in order to ensure
 * proper initialization of the instance.
 * <p>
 *
 *
 * @version 1.0
 **/

@GenAsBinaryLink(superClass = ObjectToObjectLink.class, serializable = Serialization.EXTERNALIZABLE_BASIC, properties = {
		@GeneratedProperty(name = "result", type = String.class, javaDoc = "1.PART_STATE_COMPLETE . PART_STATE_ERROR≪≫2.PART_ERP_COMPLETE  ,PART_ERP_ERROR"),
		@GeneratedProperty(name = "message", type = String.class),
		@GeneratedProperty(name = "pdfresult", type = String.class),
		@GeneratedProperty(name = "pdfmessage", type = String.class),
		@GeneratedProperty(name = "pdffolder", type = String.class) }, foreignKeys = {
				@GeneratedForeignKey(myRoleIsRoleA = false, foreignKeyRole = @ForeignKeyRole(name = "newPart", type = wt.part.WTPart.class, constraints = @PropertyConstraints(required = true)), myRole = @MyRole(name = "oldPart", cardinality = Cardinality.ONE)),
				@GeneratedForeignKey(myRoleIsRoleA = false, foreignKeyRole = @ForeignKeyRole(name = "history", type = ERPHistory.class, constraints = @PropertyConstraints(required = true)), myRole = @MyRole(name = "theERPPartLinkO", cardinality = Cardinality.ONE)) }, roleA = @GeneratedRole(name = "roleAObject", type = wt.fc.Persistable.class, accessors = @PropertyAccessors(setExceptions = {}), cardinality = Cardinality.ONE), roleB = @GeneratedRole(name = "roleBObject", type = wt.fc.Persistable.class, accessors = @PropertyAccessors(setExceptions = {}), cardinality = Cardinality.ONE), tableProperties = @TableProperties(tableName = "ERPPartLinkO"))
public class ERPPartLinkO extends _ERPPartLinkO {

	static final long serialVersionUID = 1;

	/**
	 * Default factory for the class.
	 *
	 * @param roleAObject
	 * @param roleBObject
	 * @return ERPPartLinkO
	 * @exception wt.util.WTException
	 **/
	public static ERPPartLinkO newERPPartLinkO(Persistable roleAObject, Persistable roleBObject) throws WTException {

		ERPPartLinkO instance = new ERPPartLinkO();
		instance.initialize(roleAObject, roleBObject);
		return instance;
	}

}
