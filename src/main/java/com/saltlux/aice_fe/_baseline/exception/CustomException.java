package com.saltlux.aice_fe._baseline.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException {

	/* ******** 익셉션 사용법 : 3가지 타입 중 택 1
		1. 사용자 정의 에러 : throw new CustomException(CONFLICT_KR);
		2. 기본 에러 : throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		3. 기본 에러의 메시지 재정의 : throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "내부 에러");
	*/

	private static final long serialVersionUID = 749694320101865516L;
	private final CustomErrorCode customErrorCode;
}
