/*
 * @(#) Attributes.java  Create on 2005. 3. 18.
 * Copyright (c) e3ps. All rights reserverd
 */
package ext.narae.util.iba;

import java.util.Hashtable;

import ext.narae.util.jdf.config.Config;
import ext.narae.util.jdf.config.ConfigImpl;

public class IBAAttributes {
	public final static Hashtable PART = IBAUtil.getIBAAttributes("CADAttr");
	public final static Hashtable DRAWING = IBAUtil.getIBAAttributes("CADAttr");
	public final static Hashtable ALL = IBAUtil.getIBAAttributes();

	public final static String[] PART_DEFAULT_ATTR;
	public final static String[] DRW_DEFAULT_ATTR;

	static {
		Config conf = ConfigImpl.getInstance();
		PART_DEFAULT_ATTR = conf.getArray("part.displayAttrs");
		DRW_DEFAULT_ATTR = conf.getArray("drawing.dev.displayAttrs");
	}
}
