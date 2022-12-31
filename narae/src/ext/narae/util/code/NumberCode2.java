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

package ext.narae.util.code;

import java.util.Locale; // Preserved unmodeled dependency

import com.ptc.windchill.annotations.metadata.Changeable;
import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;
import com.ptc.windchill.annotations.metadata.Serialization;

import ext.narae.util.StringUtil;
import wt.fc.InvalidAttributeException;
import wt.session.SessionHelper; // Preserved unmodeled dependency
import wt.util.WTContext; // Preserved unmodeled dependency
import wt.util.WTException;

/**
 *
 * <p>
 * Use the <code>newNumberCode</code> static factory method(s), not the
 * <code>NumberCode</code> constructor, to construct instances of this class.
 * Instances must be constructed using the static factory(s), in order to ensure
 * proper initialization of the instance.
 * <p>
 *
 *
 * @version 1.0
 **/

@GenAsPersistable(serializable = Serialization.EXTERNALIZABLE_BASIC, properties = {
		@GeneratedProperty(name = "name", type = String.class), @GeneratedProperty(name = "code", type = String.class),
		@GeneratedProperty(name = "description", type = String.class, constraints = @PropertyConstraints(upperLimit = 2000)),
		@GeneratedProperty(name = "disabled", type = boolean.class),
		@GeneratedProperty(name = "engName", type = String.class),
		@GeneratedProperty(name = "codeType", type = NumberCodeType.class, constraints = @PropertyConstraints(changeable = Changeable.VIA_OTHER_MEANS, required = true)) }, foreignKeys = {
				@GeneratedForeignKey(name = "NCode2NCode2Link", myRoleIsRoleA = false, foreignKeyRole = @ForeignKeyRole(name = "parent", type = NumberCode2.class, constraints = @PropertyConstraints(required = true)), myRole = @MyRole(name = "child")) })
public class NumberCode2 extends _NumberCode2 {

	static final long serialVersionUID = 1;

	/**
	 * Gets the value of tsshe attribute: NAME.
	 *
	 * @return String
	 **/
	public String getName() {
		String localeName = "";

		try {
			Locale userLocale = SessionHelper.manager.getLocale();
			if (userLocale == null) {
				WTContext.getContext().getLocale();
			}

			if (userLocale.equals(Locale.KOREA) || userLocale.equals(Locale.KOREAN)) {
				localeName = name;
			} else if (userLocale.equals(Locale.US)) {
				localeName = getEngName();

				if (!StringUtil.checkString(localeName)) {
					localeName = name;
				}
			} else {
				localeName = name;
			}
		} catch (Exception e) {

		}
		return localeName;

		// return name;
	}

	/**
	 * Default factory for the class.
	 *
	 * @return NumberCode
	 * @exception wt.util.WTException
	 **/
	public static NumberCode2 newNumberCode2() throws WTException {

		NumberCode2 instance = new NumberCode2();
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

	public String getKorName() {
		return name;
	}

}
