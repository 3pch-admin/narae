package ext.narae.util.iba;

import wt.iba.definition.litedefinition.AttributeDefDefaultView;

public class AttributeData {
	public String name;
	public String value;
	public String displayName;
	public AttributeDefDefaultView aview;
	public String dataType;

	public AttributeData(String displayName, AttributeDefDefaultView attributedefdefaultview, String dataType) {
		value = "";
		this.dataType = dataType;
		this.displayName = displayName;
		aview = attributedefdefaultview;
	}

	public AttributeData(String name, String displayName, AttributeDefDefaultView attributedefdefaultview,
			String dataType) {
		this(displayName, attributedefdefaultview, dataType);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getDataType() {
		return dataType;
	}

	public AttributeDefDefaultView getAview() {
		return aview;
	}
}
