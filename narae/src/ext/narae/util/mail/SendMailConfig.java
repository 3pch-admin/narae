package ext.narae.util.mail;

public class SendMailConfig {

	public static String MAIL_HOST = "165.244.235.213"; // 기본설정
	public static boolean SESSION_DEBUG_MESSAGE_FLAG = true;
	public static String DEFAULT_SENDER_MAIL_ADDRESS = "plmadmin@lgchem.com"; // 기본설정
	public static String DEFAULT_SENDER_NAME = "PLM관리자";
	public static String CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding";
	public static String ADMIN_MAIL_ADDRESS = "mjoha@lgchem.com";
	public static String ADMIN_MAIL_NAME = "PLM Admin";

	// 7bit , 8bit , base64 , quoted-printable
	// 설정을 변경해도 첨부 파일등에는 그대로 적용되지 않음
	// TXT 첨부 --> quoted-printable 적용
	// Binary 첨부 --> base64 적용

	public static String CONTENT_TYPE = "7bit";
	public static String PLAIN_CONTENT_TYPE = "text/plain;charset=utf-8";
	public static String HTML_CONTENT_TYPE = "text/html;charset=utf-8";

}
