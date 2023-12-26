package com.saltlux.aice_fe.pc.sms.service;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.saltlux.aice_fe._baseline.baseVo.DataMap;

public interface SmsService {
	
	Map<String,Object> getInfo(int gubun);
	
	void tranRegist(String formJson, MultipartFile[] uploadFiles) throws Exception;
	
}
