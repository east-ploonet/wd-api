package com.saltlux.aice_fe._baseline.exception;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

import static com.saltlux.aice_fe._baseline.exception.CustomErrorCode.*;

@Service
public class ThrowException {

	public void requestBodyRequied(Map<String, Object> reqJsonObj, String... params ) {

        for(String item : params){
            if( reqJsonObj.get(item) == null || reqJsonObj.get(item).equals("") ){
	            this.statusCode(400);
            }
        }
    }
    public void requestParamRequied(String... params ) {

        for(String item : params){
            if( item == null || item.equals("") ){
	            this.statusCode(400);
            }
        }
    }

	public void statusCode(int code) {

		switch (code) {

			//사용자 재 정의 에러
			case 200 :  throw new CustomException(STATUS_200);
			case 204 :  throw new CustomException(STATUS_204);
			case 400 :  throw new CustomException(STATUS_400);
			case 401 :  throw new CustomException(STATUS_401);
			case 403 :  throw new CustomException(STATUS_403);
			case 404 :  throw new CustomException(STATUS_404);
			case 405 :  throw new CustomException(STATUS_405);
			case 409 :  throw new CustomException(STATUS_409);
			case 423 :  throw new CustomException(STATUS_423);
			case 500 :  throw new CustomException(STATUS_500);

			// 스프링 기본 에러
			default:    throw new ResponseStatusException( HttpStatus.valueOf(code) );
		}

	}
}
