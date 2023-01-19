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

package ext.narae.service.org;

import wt.method.RemoteInterface;

/**
 *
 * @version 1.0
 **/

@RemoteInterface
public interface E3psCompanyService {

	public void password(final String id, final String password) throws Exception;
}
