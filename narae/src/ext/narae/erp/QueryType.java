package ext.narae.erp;

public enum QueryType {
	/**
	 * 조회 쿼리를 생성한다.
	 */
	SELECT,
	/**
	 * 키값을 기준으로 없을 경우 INSERT, 있을 경우 오류발생
	 */
	INSERT,
	/**
	 * UPDATE 시도
	 */
	UPDATE,
	/**
	 * DELETE 시도
	 */
	DELETE,
	/**
	 * 키값을 기준으로 없을 경우 INSERT, 있을 경우 UPDATE
	 */
	INSERT_OR_UPDATE,
	/**
	 * 프로시저 호출
	 */
	PROCEDURE
}