/**
 * @(#) UploadFile.java Copyright (c) jerred. All rights reserverd
 * @version 1.00
 * @since jdk 1.4.02
 * @createdate 2004. 3. 3.
 * @author Cho Sung Ok, jerred@bcline.com
 * @desc
 */

package ext.narae.util.content.multipart;

/**
 * 이 소스는 까오기 보드(http://www.kkaok.pe.kr/)의 소스를 참고로 만들어 졌음을 밝힙니다.
 */
public interface MultipartHelper {
	public String getParameter(String param);

	public String[] getParameterValues(String param);

	public UploadFile getFile(String param);
}
