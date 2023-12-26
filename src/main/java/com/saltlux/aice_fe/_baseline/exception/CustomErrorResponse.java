package com.saltlux.aice_fe._baseline.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Getter
@Builder
public class CustomErrorResponse {

	private final int           status;
	private final String        message;

//	private final String        error;
//	private final String        code;
//	private final LocalDateTime timestamp = LocalDateTime.now();
	private final OffsetDateTime timestamp = OffsetDateTime.now();

	public static ResponseEntity<CustomErrorResponse> toResponseEntity(CustomErrorCode customErrorCode) {

		HttpStatus httpStatus = customErrorCode.getHttpStatus();
		if(httpStatus == HttpStatus.NO_CONTENT){
			httpStatus = HttpStatus.OK;
		}

		return ResponseEntity
				.status(httpStatus)
				.body(CustomErrorResponse.builder()
					.status ( customErrorCode.getHttpStatus().value() )
					.message( customErrorCode.getDetail() )
//					.error  ( customErrorCode.getHttpStatus().name() )
//					.code   ( customErrorCode.name() )
					.build()
				);
	}
}
