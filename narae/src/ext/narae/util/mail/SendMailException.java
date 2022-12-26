package ext.narae.util.mail;

/**
 * <pre>
*  메일에서 발생한 Exception 을 담고 있다.
 * </pre>
 * 
 * @author 차종호
 * @version 1.0, 01/01/03
 */
public class SendMailException extends Exception {

	/**
	 *
	 * <pre>
	*   SendMailException 의 constructor
	 * </pre>
	 * 
	 * @param exceptionMessage Exception 내용
	 * @param exceptionSource  발생 Exception 자체
	 * @exception None
	 */

	public SendMailException(String exceptionMessage, Exception exceptionSource) {
		// 로깅 툴로 로그를 적절히 남겨도 된다
		System.out.println("Exception - [" + exceptionMessage + "]");
		exceptionSource.printStackTrace();

	}
}
