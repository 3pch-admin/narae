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

import com.ptc.windchill.annotations.metadata.Cardinality;
import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;
import com.ptc.windchill.annotations.metadata.Serialization;

import ext.narae.util.OwnPersistable;
import wt.fc.InvalidAttributeException;
import wt.util.WTException;
import wt.xml.XMLLob;

/**
 *
 * <p>
 * Use the <code>newEOEul</code> static factory method(s), not the
 * <code>EOEul</code> constructor, to construct instances of this class.
 * Instances must be constructed using the static factory(s), in order to ensure
 * proper initialization of the instance.
 * <p>
 *
 *
 * @version 1.0
 **/

@GenAsPersistable(interfaces = {
		OwnPersistable.class }, serializable = Serialization.EXTERNALIZABLE_BASIC, properties = {
				@GeneratedProperty(name = "xml", type = XMLLob.class),
				@GeneratedProperty(name = "topAssyOid", type = String.class) }, foreignKeys = {
						@GeneratedForeignKey(name = "EcoEulLink", foreignKeyRole = @ForeignKeyRole(name = "eco", type = EChangeOrder2.class, constraints = @PropertyConstraints(required = true)), myRole = @MyRole(name = "eul")),
						@GeneratedForeignKey(name = "EulBaselineLink", myRoleIsRoleA = false, foreignKeyRole = @ForeignKeyRole(name = "baseline", type = wt.vc.baseline.Baseline.class), myRole = @MyRole(name = "eul", cardinality = Cardinality.ZERO_TO_ONE)) })
public class EOEul extends _EOEul {

	static final long serialVersionUID = 1;

	/**
	 * Default factory for the class.
	 *
	 * @return EOEul
	 * @exception wt.util.WTException
	 **/
	public static EOEul newEOEul() throws WTException {

		EOEul instance = new EOEul();
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
