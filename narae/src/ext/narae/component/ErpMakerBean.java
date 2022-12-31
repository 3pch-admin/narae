// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ErpMakerBean.java

package ext.narae.component;

import java.io.Serializable;

public class ErpMakerBean implements Serializable {

	public ErpMakerBean(String id, String name) {
		makerCode = id;
		makerName = name;
	}

	public String getMakerCODE() {
		return makerCode;
	}

	public void setMakerCODE(String makerCODE) {
		makerCode = makerCODE;
	}

	public String getMakerName() {
		return makerName;
	}

	public void setMakerName(String makerName) {
		this.makerName = makerName;
	}

	static final long serialVersionUID = 0x1cf84b4b528486c3L;
	private String makerCode;
	private String makerName;
}
