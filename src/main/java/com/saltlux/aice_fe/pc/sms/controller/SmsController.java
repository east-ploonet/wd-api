package com.saltlux.aice_fe.pc.sms.controller;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.saltlux.aice_fe._baseline.baseController.BaseController;
import com.saltlux.aice_fe._baseline.baseVo.DataMap;
import com.saltlux.aice_fe._baseline.baseVo.ResponseVo;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.sms.service.SmsService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("${apiVersionPrefix}/workStage/sms") // end point : localhost:8080/api/v1/workStage/ojt
public class SmsController extends BaseController {
	
	@Autowired
	private SmsService smsService;
	
	@Value("${ploonet.api.audio.api.url}")
	private String audioApiUrl;
	
	@Value("${globals.ph.upload.file}")
    private String phNotUpload;
	
	
	/*
	 * 
	 * 문자 발신
	 * 
	 * */
	
	//발신 번호
	@GetMapping("/info")
	public Object info(@RequestParam(value="gubun") final int gubun) throws Exception {
		Map<String,Object> resultMap = smsService.getInfo(gubun);
		return new ResponseVo(200, resultMap);
	}
	
	//등록
	@PostMapping("/tranRegist")
	public Object tranRegist(
			@RequestParam(value="formData" , required=false ) final String formJson,
			@RequestParam(value="uploadFile"   , required=false    ) final MultipartFile[] uploadFiles
	) throws Exception {
		smsService.tranRegist(formJson, uploadFiles);

		return new ResponseVo(200);
	}
	
	
}