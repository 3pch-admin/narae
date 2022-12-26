package ext.narae.service.change.editor;

public class EOActionTempItemData implements java.io.Serializable {

	public String newPart = null;
	public String newVersion = null;
	public String newQuantity = null;
	public String oldPart = null;
	public String oldVersion = null;
	public String oldQuantity = null;
	public String newUnit = null;
	public String oldUnit = null;
	public String newItemSeq = null;
	public String oldItemSeq = null;

	public String editType = "I"; // 기본값은 추가

	public EOActionTempItemData() {

	}

	public String toString() {
		return "[" + editType + "]" + ":" + (newPart == null ? "" : newPart) + ":"
				+ (newQuantity == null ? "" : newQuantity) + ":" + (newItemSeq == null ? "" : newItemSeq) + ":"
				+ (oldPart == null ? "" : oldPart) + ":" + (oldQuantity == null ? "" : oldQuantity) + ":"
				+ (oldItemSeq == null ? "" : oldItemSeq);
	}

}
