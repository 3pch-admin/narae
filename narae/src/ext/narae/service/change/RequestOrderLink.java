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

import java.io.IOException;
import java.io.ObjectInput;

import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedRole;
import com.ptc.windchill.annotations.metadata.TableProperties;

import wt.fc.ObjectToObjectLink;
import wt.util.WTException;

/**
 *
 * <p>
 * Use the <code>newRequestOrderLink</code> static factory method(s), not the
 * <code>RequestOrderLink</code> constructor, to construct instances of this
 * class. Instances must be constructed using the static factory(s), in order to
 * ensure proper initialization of the instance.
 * <p>
 *
 *
 * @version 1.0
 **/

@GenAsBinaryLink(superClass = ObjectToObjectLink.class, versions = {
		2538346186404157511L }, roleA = @GeneratedRole(name = "order", type = EChangeOrder2.class), roleB = @GeneratedRole(name = "request", type = EChangeRequest2.class), tableProperties = @TableProperties(tableName = "RequestOrderLink"))
public class RequestOrderLink extends _RequestOrderLink {

	static final long serialVersionUID = 1;

	/**
	 * Default factory for the class.
	 *
	 * @param order
	 * @param request
	 * @return RequestOrderLink
	 * @exception wt.util.WTException
	 **/
	public static RequestOrderLink newRequestOrderLink(EChangeOrder2 order, EChangeRequest2 request)
			throws WTException {

		RequestOrderLink instance = new RequestOrderLink();
		instance.initialize(order, request);
		return instance;
	}

	/**
	 * Reads the non-transient fields of this class from an external source.
	 *
	 * @param input
	 * @param readSerialVersionUID
	 * @param superDone
	 * @return boolean
	 * @exception java.io.IOException
	 * @exception java.lang.ClassNotFoundException
	 **/
	boolean readVersion2538346186404157511L(ObjectInput input, long readSerialVersionUID, boolean superDone)
			throws IOException, ClassNotFoundException {

		if (!superDone) // if not doing backward compatibility
			super.readExternal(input); // handle super class

		return true;
	}

}
