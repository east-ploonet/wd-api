package com.saltlux.aice_fe.pc.quick.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.saltlux.aice_fe._baseline.baseVo.DataMap;


public interface QuickService {

	DataMap getQuickStartUserInfo(Map<String, Object> paramMap, String quickStartType) throws Exception;
	
	void updateUserInfo(Map<String, Object> paramMap, String loginUserType) throws Exception;

}
