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
import wt.fc.WTObject;
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

@GenAsPersistable(superClass = WTObject.class, properties = { @GeneratedProperty(name = "name", type = String.class),
		@GeneratedProperty(name = "code", type = String.class),
		@GeneratedProperty(name = "description", type = String.class, constraints = @PropertyConstraints(upperLimit = 2000)),
		@GeneratedProperty(name = "disabled", type = boolean.class),
		@GeneratedProperty(name = "engName", type = String.class),
		@GeneratedProperty(name = "codeType", type = NumberCodeType.class, constraints = @PropertyConstraints(changeable = Changeable.VIA_OTHER_MEANS, required = true)) }, foreignKeys = {
				@GeneratedForeignKey(name = "NCodeNCodeLink", myRoleIsRoleA = false, foreignKeyRole = @ForeignKeyRole(name = "parent", type = NumberCode.class, constraints = @PropertyConstraints(required = false)), myRole = @MyRole(name = "child")) })
public class NumberCode extends _NumberCode {

	static final long serialVersionUID = 1;

	public static NumberCode newNumberCode() throws WTException {
		NumberCode instance = new NumberCode();
		return instance;
	}
}
