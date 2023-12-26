package com.saltlux.aice_fe.commonCode.controller;

import com.saltlux.aice_fe._baseline.baseController.BaseController;
import com.saltlux.aice_fe._baseline.baseVo.ResponseVo;
import com.saltlux.aice_fe._baseline.commons.Common;
import com.saltlux.aice_fe.commonCode.service.CodeService;
import com.saltlux.aice_fe.commonCode.vo.CodeVo;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("${apiVersionPrefix}/common/code")
public class CommonCodeController extends BaseController {

	@Autowired
	private CodeService codeService;

	@GetMapping("/list")
	public Object listCode (
			@RequestParam(value="upCode", required=false) final String upCode
	) throws Exception {

		throwException.requestParamRequied( upCode );

		CodeVo codeVo = new CodeVo();
		codeVo.setFk_up_code( upCode );

		Map<String, Object> resultMap = codeService.selectCodeList(codeVo);

		return new ResponseVo( 200, resultMap );
	}
	
	@GetMapping("/staffWorkCodeList")
	public Object staffWorkCodeList (@RequestParam(value="fkCompany") final String fkCompany) throws Exception {
		Map<String, Object> reqJsonObj = new HashMap<>();
		reqJsonObj.put("fkCompany", Common.parseLong(fkCompany));
		
		Map<String, Object> resultMap = codeService.selectStaffWorkCodeList(reqJsonObj);
		
		return new ResponseVo( 200, resultMap );
	}
	
	@GetMapping("/selectUseStaffWorkCodeList")
	public Object selectUseStaffWorkCodeList (@RequestParam(value="fkCompany") final String fkCompany) throws Exception {
		System.out.println("fkCompany:" + fkCompany);
		
		Map<String, Object> reqJsonObj = new HashMap<>();
		reqJsonObj.put("fkCompany", Common.parseLong(fkCompany)); 
		
		
		Map<String, Object> resultMap = codeService.selectUseStaffWorkCodeList(reqJsonObj);
		
		return new ResponseVo( 200, resultMap );
	}
	
	
	
	@GetMapping("/staffWorkCtgrCodeList")
	public Object staffWorkCtgrCodeList (@RequestParam(value="fkStaffWorkCode") final String fkStaffWorkCode, @RequestParam(value="fkCompany") final String fkCompany) throws Exception {
		
		throwException.requestParamRequied( fkStaffWorkCode );
		
		
		Map<String, Object> resultMap = codeService.staffWorkCtgrCodeList(fkStaffWorkCode, Common.parseLong(fkCompany));
		
		return new ResponseVo( 200, resultMap );
	}
	
	@GetMapping("/uploadUrl")
	public Object uploadUrl (
	) throws Exception {

		return new ResponseVo( 200, pathFileUpload );
	}
}
