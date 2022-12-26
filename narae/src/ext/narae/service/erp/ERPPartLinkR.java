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
import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.GeneratedRole;
import com.ptc.windchill.annotations.metadata.Serialization;
import com.ptc.windchill.annotations.metadata.TableProperties;

import ext.narae.service.change.EcrPartLink;
import wt.fc.ObjectToObjectLink;
import wt.util.WTException;

/**
 *
 * <p>
 * Use the <code>newERPPartLinkR</code> static factory method(s), not the
 * <code>ERPPartLinkR</code> constructor, to construct instances of this class.
 * Instances must be constructed using the static factory(s), in order to ensure
 * proper initialization of the instance.
 * <p>
 *
 *
 * @version 1.0
 **/

@GenAsBinaryLink(superClass = ObjectToObjectLink.class, serializable = Serialization.EXTERNALIZABLE_BASIC, properties = {
		@GeneratedProperty(name = "result", type = String.class, javaDoc = "PART_ERP_COMPLETE  , PART_ERP_ERROR"),
		@GeneratedProperty(name = "message", type = String.class) }, roleA = @GeneratedRole(name = "erp", type = ERPHistory.class, cardinality = Cardinality.ONE), roleB = @GeneratedRole(name = "part", type = EcrPartLink.class), tableProperties = @TableProperties(tableName = "ERPPartLinkR"))
public class ERPPartLinkR extends _ERPPartLinkR {

	static final long serialVersionUID = 1;

	/**
	 * Default factory for the class.
	 *
	 * @param erp
	 * @param part
	 * @return ERPPartLinkR
	 * @exception wt.util.WTException
	 **/
	public static ERPPartLinkR newERPPartLinkR(ERPHistory erp, EcrPartLink part) throws WTException {

		ERPPartLinkR instance = new ERPPartLinkR();
		instance.initialize(erp, part);
		return instance;
	}

}
