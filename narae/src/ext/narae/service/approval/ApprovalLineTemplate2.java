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

import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;

import wt.util.WTException;

/**
 *
 * <p>
 * Use the <code>newApprovalLineTemplate2</code> static factory method(s), not
 * the <code>ApprovalLineTemplate2</code> constructor, to construct instances of
 * this class. Instances must be constructed using the static factory(s), in
 * order to ensure proper initialization of the instance.
 * <p>
 *
 *
 * @version 1.0
 **/

@GenAsPersistable(superClass = ApprovalLineTemplate.class, properties = {
		@GeneratedProperty(name = "title", type = String.class) })
public class ApprovalLineTemplate2 extends _ApprovalLineTemplate2 {

	static final long serialVersionUID = 1;

	/**
	 * Default factory for the class.
	 *
	 * @return ApprovalLineTemplate2
	 * @exception wt.util.WTException
	 **/
	public static ApprovalLineTemplate2 newApprovalLineTemplate2() throws WTException {

		ApprovalLineTemplate2 instance = new ApprovalLineTemplate2();
		instance.initialize();
		return instance;
	}

}
