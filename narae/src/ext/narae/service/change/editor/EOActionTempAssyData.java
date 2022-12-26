package ext.narae.service.change.editor;

import java.util.ArrayList;

public class EOActionTempAssyData implements java.io.Serializable {

	public String assyPart = null;
	public String nextAssyVersion = null;
	public String stdQuantity = "";
	public String orgStdQuantity = "";

	public ArrayList itemList = new ArrayList();

	public EOActionTempAssyData(String assyPart, String nextAssyVersion, String orgStdQuantity, String stdQuantity) {
		this.assyPart = assyPart;
		this.nextAssyVersion = nextAssyVersion;
		this.stdQuantity = stdQuantity;
		this.orgStdQuantity = orgStdQuantity;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(" - " + assyPart + " , " + orgStdQuantity + "," + stdQuantity);
		sb.append("\n");
		for (int i = 0; i < itemList.size(); i++) {
			EOActionTempItemData eti = (EOActionTempItemData) itemList.get(i);
			sb.append(eti);
			sb.append("\n");
		}
		return sb.toString();
	}
}
