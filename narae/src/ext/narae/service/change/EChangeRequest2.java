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

package ext.narae.service.change;

import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;
import com.ptc.windchill.annotations.metadata.Serialization;

import ext.narae.service.approval.CommonActivity;
import ext.narae.util.OwnPersistable;
import wt.content.ContentHolder;
import wt.fc.InvalidAttributeException;
import wt.inf.container.WTContained;
import wt.util.WTException;

/**
 *
 * <p>
 * Use the <code>newEChangeRequest2</code> static factory method(s), not the
 * <code>EChangeRequest2</code> constructor, to construct instances of this
 * class. Instances must be constructed using the static factory(s), in order to
 * ensure proper initialization of the instance.
 * <p>
 *
 *
 * @version 1.0
 **/

@GenAsPersistable(interfaces = { OwnPersistable.class, WTContained.class,
		ContentHolder.class }, serializable = Serialization.EXTERNALIZABLE_BASIC, properties = {
				@GeneratedProperty(name = "requestNumber", type = String.class),
				@GeneratedProperty(name = "name", type = String.class),
				@GeneratedProperty(name = "purpose", type = String.class),
				@GeneratedProperty(name = "customer", type = String.class),
				@GeneratedProperty(name = "problem", type = String.class, constraints = @PropertyConstraints(upperLimit = 4000)),
				@GeneratedProperty(name = "solution", type = String.class, constraints = @PropertyConstraints(upperLimit = 4000)),
				@GeneratedProperty(name = "termicateType", type = String.class),
				@GeneratedProperty(name = "prjNo", type = String.class),
				@GeneratedProperty(name = "prjSeqNo", type = String.class),
				@GeneratedProperty(name = "unitCode", type = String.class),
				@GeneratedProperty(name = "realCreator", type = String.class) }, foreignKeys = {
						@GeneratedForeignKey(myRoleIsRoleA = false, foreignKeyRole = @ForeignKeyRole(name = "worker", type = wt.org.WTUser.class), myRole = @MyRole(name = "ecr")),
						@GeneratedForeignKey(name = "EcrWorkLink", myRoleIsRoleA = false, foreignKeyRole = @ForeignKeyRole(name = "work", type = CommonActivity.class), myRole = @MyRole(name = "ecr")) })
public class EChangeRequest2 extends _EChangeRequest2 {

	static final long serialVersionUID = 1;

	/**
	 * Default factory for the class.
	 *
	 * @return EChangeRequest2
	 * @exception wt.util.WTException
	 **/
	public static EChangeRequest2 newEChangeRequest2() throws WTException {

		EChangeRequest2 instance = new EChangeRequest2();
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
