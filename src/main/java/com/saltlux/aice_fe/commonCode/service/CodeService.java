package com.saltlux.aice_fe.commonCode.service;

import com.saltlux.aice_fe.commonCode.vo.CodeVo;

import java.util.Map;

public interface CodeService {

	Map<String, Object> selectCodeList(CodeVo codeVo) throws Exception;
	
	Map<String, Object> selectStaffWorkCodeList(Map<String, Object> reqJsonObj) throws Exception;
	
	Map<String, Object> selectUseStaffWorkCodeList(Map<String, Object> reqJsonObj) throws Exception;
	
	
	Map<String, Object> staffWorkCtgrCodeList(String fkStaffWorkCode, long fkCompany) throws Exception;
	
	
	

}
