package com.saltlux.aice_fe._baseline.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum CustomErrorCode {

	// ■ 200 OK : 성공
	STATUS_200          (OK, "성공"),
	STATUS_200_REGIST   (OK, "등록 되었습니다."),
	STATUS_200_UPDATE   (OK, "수정 되었습니다."),
	STATUS_200_DELETE   (OK, "삭제 되었습니다."),
	STATUS_200_SUCCESS  (OK, "정상 처리 되었습니다."),

	// ■ 204 No Content : 데이터 없음( 응답 body 값을 주어도 출력되지 않는다.)
	STATUS_204          (NO_CONTENT, "응답 데이터가 없습니다."),
//	STATUS_204          (OK , "응답 데이터가 없습니다."),

	// ■ 400 Bad Request : 잘못된 요청
	STATUS_400          (BAD_REQUEST, "잘못된 요청입니다."),

	// ■ 401 Unauthorized : 인증 실패
	STATUS_401          (UNAUTHORIZED, "인증 정보가 없습니다."),

	// ■ 403 Forbidden : 권한 없음
	STATUS_403          (FORBIDDEN, "요청 권한이 없습니다."),

	// ■ 404 Not Found : 요청 URL 없음
	STATUS_404          (NOT_FOUND, "요청 URL이 없습니다."),

	// ■ 405 Method Not Allowed : 제한된 메소드 요청
	STATUS_405          (METHOD_NOT_ALLOWED, "잘못된 메소드 요청입니다."),

	// ■ 409 Conflict : 리소스 충돌
	STATUS_409          (CONFLICT, "데이터가 이미 존재합니다."),

	// ■ 423 Locked : 토큰 만료
	STATUS_423          (LOCKED, "인증이 만료되었습니다."),

	// ■ 500 Internal Server Error : 내부 서버 에러
	STATUS_500          (INTERNAL_SERVER_ERROR, "내부 처리중 오류가 발생했습니다."),

	// ■ 503 Service Unavailable : 서버 응답 없음
	STATUS_503          (SERVICE_UNAVAILABLE, "요청 시간이 초과되었습니다.."),
	;

	private final HttpStatus httpStatus;
	private final String detail;
}
