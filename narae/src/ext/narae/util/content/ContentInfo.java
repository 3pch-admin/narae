package ext.narae.util.content;

import java.net.URL;

import ext.narae.util.FileUtil;

class ContentInfo {
	private String type; // 파일 타입 ( FILE or URL )
	private String contentOid; // 객체 OID
	private String name; // 파일명
	private String description; // 파일 설명
	private URL downURL; // 다운 URL ( 파일타입:FILE 일경우 )
	private String fileSize; // 파일 사이즈 ( 파일타입:FILE 일경우 )
	private String businessType; // Business 타입 ( 파일타입:FILE 일경우 )
	private String iconURLStr; // icon 이미지 ( 파일타입:FILE 일경우 )

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getType() {
		return type;
	}

	public URL getDownURL() {
		return downURL;
	}

	public String getContentOid() {
		return contentOid;
	}

	public String getFileSize() {
		return fileSize;
	}

	public String getBusinessType() {
		return businessType;
	}

	public String getDownURLStr() {
		StringBuffer sb = new StringBuffer();
		sb.append("<a target='download' href='");
		sb.append(downURL.toString());
		sb.append("'>&nbsp;" + name);
		sb.append("</a>&nbsp;(&nbsp;" + fileSize + "&nbsp;)");
		return sb.toString();
	}

	public String getIconURLStr() {
		StringBuffer sb = new StringBuffer();
		sb.append("<a target='download' href='");
		sb.append(downURL.toString());
		sb.append("'>" + iconURLStr);
		sb.append("</a>");
		return sb.toString();
	}

	public void setName(String aPath) {
		name = aPath;
	}

	public void setDescription(String aDescription) {
		description = aDescription;
	}

	public void setType(String aType) {
		type = aType;
	}

	public void setDownURL(URL aUrlPath) {
		downURL = aUrlPath;
	}

	public void setContentOid(String aContentOid) {
		contentOid = aContentOid;
	}

	public void setBusinessType(String a_businessType) {
		businessType = a_businessType;
	}

	public void setIconURLStr(String aIconURLStr) {
		iconURLStr = aIconURLStr;
	}

	public void setFileSize(long l_fileSize) {
		fileSize = FileUtil.getFileSizeStr(l_fileSize);
	}
}
