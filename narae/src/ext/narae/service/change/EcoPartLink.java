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

import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.GeneratedRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;
import com.ptc.windchill.annotations.metadata.Serialization;
import com.ptc.windchill.annotations.metadata.TableProperties;

import wt.fc.ObjectToObjectLink;
import wt.part.WTPartMaster;
import wt.util.WTException;

/**
 *
 * <p>
 * Use the <code>newEcoPartLink</code> static factory method(s), not the
 * <code>EcoPartLink</code> constructor, to construct instances of this class.
 * Instances must be constructed using the static factory(s), in order to ensure
 * proper initialization of the instance.
 * <p>
 *
 *
 * @version 1.0
 **/

@GenAsBinaryLink(superClass = ObjectToObjectLink.class, serializable = Serialization.EXTERNALIZABLE_BASIC, properties = {
		@GeneratedProperty(name = "version", type = String.class, constraints = @PropertyConstraints(upperLimit = 10)),
		@GeneratedProperty(name = "gubun", type = String.class, constraints = @PropertyConstraints(upperLimit = 10)),
		@GeneratedProperty(name = "partType", type = String.class, javaDoc = "PART,TOP") }, roleA = @GeneratedRole(name = "part", type = WTPartMaster.class), roleB = @GeneratedRole(name = "eco", type = EChangeOrder2.class), tableProperties = @TableProperties(tableName = "EcoPartLink"))
public class EcoPartLink extends _EcoPartLink {

	static final long serialVersionUID = 1;

	/**
	 * Default factory for the class.
	 *
	 * @param part
	 * @param eco
	 * @return EcoPartLink
	 * @exception wt.util.WTException
	 **/
	public static EcoPartLink newEcoPartLink(WTPartMaster part, EChangeOrder2 eco) throws WTException {

		EcoPartLink instance = new EcoPartLink();
		instance.initialize(part, eco);
		return instance;
	}

}
