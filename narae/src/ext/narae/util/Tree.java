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

package ext.narae.util;

import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

/**
 *
 * @version 1.0
 **/

@GenAsPersistable(foreignKeys = {
		@GeneratedForeignKey(name = "ParentChildLink", foreignKeyRole = @ForeignKeyRole(name = "parent", type = Tree.class, constraints = @PropertyConstraints(required = true)), myRole = @MyRole(name = "child")) })
public interface Tree extends _Tree {

}
