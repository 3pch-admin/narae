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

package ext.narae.service.approval;

import java.sql.Timestamp;

import com.ptc.windchill.annotations.metadata.Cardinality;
import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;
import com.ptc.windchill.annotations.metadata.Serialization;

import wt.fc.InvalidAttributeException;
import wt.util.WTException;

/**
 *
 * <p>
 * Use the <code>newApprovalLine</code> static factory method(s), not the
 * <code>ApprovalLine</code> constructor, to construct instances of this class.
 * Instances must be constructed using the static factory(s), in order to ensure
 * proper initialization of the instance.
 * <p>
 *
 *
 * @version 1.0
 **/

@GenAsPersistable(serializable = Serialization.EXTERNALIZABLE_BASIC, properties = {
		@GeneratedProperty(name = "name", type = String.class), @GeneratedProperty(name = "seq", type = int.class),
		@GeneratedProperty(name = "owner", type = String.class),
		@GeneratedProperty(name = "approveComment", type = String.class, constraints = @PropertyConstraints(upperLimit = 4000)),
		@GeneratedProperty(name = "approveDate", type = Timestamp.class),
		@GeneratedProperty(name = "state", type = String.class),
		@GeneratedProperty(name = "readCheck", type = boolean.class),
		@GeneratedProperty(name = "stepName", type = String.class, javaDoc = "협의전결재/협의/결재"),
		@GeneratedProperty(name = "startTaskDate", type = Timestamp.class, javaDoc = "해당 task가 시작된 시간≪≫- 3일이상 지연시 독촉 메일 전송을 위해 사용") }, foreignKeys = {
				@GeneratedForeignKey(myRoleIsRoleA = false, foreignKeyRole = @ForeignKeyRole(name = "master", type = ApprovalMaster.class, constraints = @PropertyConstraints(required = true)), myRole = @MyRole(name = "line", cardinality = Cardinality.ONE_TO_MANY)) })
public class ApprovalLine extends _ApprovalLine {

	static final long serialVersionUID = 1;

	/**
	 * Default factory for the class.
	 *
	 * @return ApprovalLine
	 * @exception wt.util.WTException
	 **/
	public static ApprovalLine newApprovalLine() throws WTException {

		ApprovalLine instance = new ApprovalLine();
		instance.initialize();
		return instance;
	}

	/**
	 * Supports initialization, following construction of an instance. Invoked by
	 * "new" factory having the same signature.
	 *
	 * @exception wt.util.WTException
	 **/
	protected void initialize() throws WTException {

	}

	/**
	 * Gets the value of the attribute: IDENTITY. Supplies the identity of the
	 * object for business purposes. The identity is composed of name, number or
	 * possibly other attributes. The identity does not include the type of the
	 * object.
	 *
	 *
	 * <BR>
	 * <BR>
	 * <B>Supported API: </B>false
	 *
	 * @deprecated Replaced by IdentityFactory.getDispayIdentifier(object) to return
	 *             a localizable equivalent of getIdentity(). To return a
	 *             localizable value which includes the object type, use
	 *             IdentityFactory.getDisplayIdentity(object). Other alternatives
	 *             are ((WTObject)obj).getDisplayIdentifier() and
	 *             ((WTObject)obj).getDisplayIdentity().
	 *
	 * @return String
	 **/
	public String getIdentity() {

		return null;
	}

	/**
	 * Gets the value of the attribute: TYPE. Identifies the type of the object for
	 * business purposes. This is typically the class name of the object but may be
	 * derived from some other attribute of the object.
	 *
	 *
	 * <BR>
	 * <BR>
	 * <B>Supported API: </B>false
	 *
	 * @deprecated Replaced by IdentityFactory.getDispayType(object) to return a
	 *             localizable equivalent of getType(). Another alternative is
	 *             ((WTObject)obj).getDisplayType().
	 *
	 * @return String
	 **/
	public String getType() {

		return null;
	}

	@Override
	public void checkAttributes() throws InvalidAttributeException {

	}

}
