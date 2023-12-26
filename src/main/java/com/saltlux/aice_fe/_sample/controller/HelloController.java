package com.saltlux.aice_fe._sample.controller;

import com.saltlux.aice_fe._baseline.baseController.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("${apiVersionPrefix}")
public class HelloController extends BaseController {

	@Value("${profile.name}")
	private String profileName;


	@GetMapping("/hello")
	public String hello() throws Exception {

		//throwException.status_204(); //throwException 호출시 아래의 코드는 진행하지 않음

		log.trace    ("********** this log level : {}", "TRACE");
		log.debug    ("********** this log level : {}", "DEBUG");
		log.info     ("********** this log level : {}", "INFO");
		log.warn     ("********** this log level : {}", "WARN");
		log.error    ("********** this log level : {}", "ERROR");

		return "hello, this profileName is [" + profileName +"]";
//		return "index.html";
	}
}
