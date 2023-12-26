package com.saltlux.aice_fe._baseline.baseController;

import com.saltlux.aice_fe._baseline.exception.CustomErrorResponse;
import com.saltlux.aice_fe._baseline.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import static com.saltlux.aice_fe._baseline.exception.CustomErrorCode.*;

@Slf4j
@RestControllerAdvice
public class CustomExceptionController {

	/* ********** CustomException ********** */
	@ExceptionHandler(value = { CustomException.class })
	protected ResponseEntity<CustomErrorResponse> handleCustomException(CustomException ex) {
		return CustomErrorResponse.toResponseEntity(ex.getCustomErrorCode());
	}

	/* ********** GlobalException throw CustomException ********** */
	@ExceptionHandler(value = { MissingServletRequestParameterException.class, HttpMediaTypeNotSupportedException.class, HttpMediaTypeNotAcceptableException.class })
	public ResponseEntity<CustomErrorResponse> handleException_400( HttpServletRequest request, Exception ex ) {
		return CustomErrorResponse.toResponseEntity(STATUS_400);
	}

	@ExceptionHandler(value = { NoHandlerFoundException.class })
	public ResponseEntity<CustomErrorResponse> handleException_404( HttpServletRequest request, Exception ex ) {
		return CustomErrorResponse.toResponseEntity(STATUS_404);
	}

	@ExceptionHandler(value = { HttpRequestMethodNotSupportedException.class })
	public ResponseEntity<CustomErrorResponse> handleException_405( HttpServletRequest request, Exception ex ) {
		return CustomErrorResponse.toResponseEntity(STATUS_405);
	}

	@ExceptionHandler(value = { ConstraintViolationException.class, DataIntegrityViolationException.class})
	protected ResponseEntity<CustomErrorResponse> handleException_409() {
		return CustomErrorResponse.toResponseEntity(STATUS_409);
	}

	@ExceptionHandler(value = { AsyncRequestTimeoutException.class })
	public ResponseEntity<CustomErrorResponse> handleException_503( HttpServletRequest request, Exception ex ) {
		log.error("********** handleException_503 : ", ex);
		return CustomErrorResponse.toResponseEntity(STATUS_503);
	}
}
