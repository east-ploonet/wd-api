package com.saltlux.aice_fe._baseline.baseController;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class CustomErrorController implements ErrorController {

	@RequestMapping(value = "/error")
	public Object handleError(HttpServletRequest request) {

		Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

		Map<String, Object> mapAdd = new HashMap<>();

		mapAdd.put("status"         , status.toString()) ;
		mapAdd.put("msg"            , HttpStatus.valueOf(Integer.parseInt(status.toString())).getReasonPhrase() );
		mapAdd.put("timestamp"      , OffsetDateTime.now()) ;

		return mapAdd;
	}

/*
	@RequestMapping(value = "/error")
	public String handleError(HttpServletRequest request, ModelMap model) {

		Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

		Map<String, Object> mapAdd = new HashMap<>();

		mapAdd.put("status"         , status.toString()) ;
		mapAdd.put("msg"            , HttpStatus.valueOf(Integer.parseInt(status.toString())).getReasonPhrase() );
		mapAdd.put("timestamp"      , OffsetDateTime.now()) ;

		model.addAttribute("error"  , mapAdd);

		return "/error/error";
	}
*/
}
