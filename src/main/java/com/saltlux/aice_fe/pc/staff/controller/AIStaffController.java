package com.saltlux.aice_fe.pc.staff.controller;

import com.saltlux.aice_fe._baseline.baseController.BaseController;
import com.saltlux.aice_fe._baseline.baseVo.ResponseVo;
import com.saltlux.aice_fe._baseline.commons.Common;
import com.saltlux.aice_fe.ecApi.service.PloonetApiService;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.join.vo.CompanyDeptStaffVo;
import com.saltlux.aice_fe.pc.join.vo.CompanyDeptVo;
import com.saltlux.aice_fe.pc.join.vo.CompanyStaffVo;
import com.saltlux.aice_fe.pc.staff.service.AIStaffService;
import com.saltlux.aice_fe.pc.staff.service.StaffService;
import com.saltlux.aice_fe.pc.staff.vo.AIStaffVo;
import lombok.extern.slf4j.Slf4j;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("${apiVersionPrefix}/workStage/aiStaff") // end point : localhost:8080/api/v1/workStage/aiStaff
public class AIStaffController extends BaseController {

	@Autowired
	private AIStaffService aiStaffService;
	
	@Autowired
	private PloonetApiService ploonetApiService;
	
	@Value("${ploonet.api.base.billing.url}")
    public String billingUrl;
	
	@Value("${ploonet.configmanager.api.url}")
    public String configmanagerUrl;
	
	//로컬 api 호출
	//String configmanagerUrl = "http://15.164.228.137:8151";

	// AI 담당자 목록
	@RequestMapping(value = {"/list"}, method = {RequestMethod.GET}, produces=PRODUCES_JSON)
	public Object getAIstaffList(
	) throws Exception {

		Map<String, Object> paramMap = new HashMap<>();
		
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
//		paramMap.put("fkCompany", loginInfoVo.getLoginCompanyPk());
//
//		// AI직원 직무로 조건 추가
//		String staffWorkCode = request.getParameter("staffWorkCode");
//		if(!Common.isBlank(staffWorkCode)) paramMap.put("staffWorkCode", staffWorkCode);
//
//		String orderbyType = request.getParameter("orderType");
//		if(!Common.isBlank(orderbyType)) paramMap.put("orderbyType", orderbyType);
//
//		Map<String, Object> resultMap = aiStaffService.selectAIstaffList(paramMap);
//		System.out.println("!!!!!");
//		System.out.println("configmanagerUrl" + configmanagerUrl);
		
		Map<String, Object> resultMap = new HashMap<>();
		
		
		String authToken = loginInfoVo.getAuthToken();
		JSONParser jsonParser = new JSONParser();
		String aiStarffUrl = "/aice/configManager/v1/companies";
		org.json.simple.JSONObject jsonObj = new org.json.simple.JSONObject(resultMap);
		
		System.out.println("url 주소 : "+configmanagerUrl+aiStarffUrl+"/"+loginInfoVo.getLoginCompanyPk()+"/ais");
		
		//기존AI
		boolean mainAi = false;
		//퀵스타트AI
		boolean quickAi = false;
		
		//1.변경staff 값
		Long mainAistaffSeq = null;
		Long quickAistaffSeq = null;
		
		long pkCompanyStaff;
		
		try {
			String apiResult = Common.getRestDataApiGet(configmanagerUrl+aiStarffUrl+"/"+loginInfoVo.getLoginCompanyPk()+"/ais", authToken, "GET");
			System.out.println("apiResultAIList : " + apiResult);
			jsonObj = (org.json.simple.JSONObject) jsonParser.parse(apiResult);
        	org.json.simple.JSONObject aiStaffObj = (org.json.simple.JSONObject) jsonObj.get("data");
        	org.json.simple.JSONArray array = (org.json.simple.JSONArray) aiStaffObj.get("list");
		
			for(int i=0; i<array.size(); i++) {
				org.json.simple.JSONObject staffList = (org.json.simple.JSONObject) array.get(i);	
				if((Long)staffList.get("quickStartStatus") == -1) {
					mainAistaffSeq = (Long)staffList.get("aiStaffSeq");
					mainAi = true;
				//퀵스타트 
				}else if(((Long)staffList.get("quickStartStatus") > 0)){
					quickAistaffSeq = (Long)staffList.get("aiStaffSeq");
					quickAi = true;
				}
				
			}
			if(mainAi && quickAi) {
				pkCompanyStaff = mainAistaffSeq;
			}else {
				if(mainAi) {
					pkCompanyStaff = mainAistaffSeq;
				}else {
					pkCompanyStaff = quickAistaffSeq;
				}
			}
        	resultMap.put( "data", aiStaffObj);
        	resultMap.put( "pkCompanyStaff", pkCompanyStaff);
		}catch (Exception e) {
			System.out.println("Exception" + e);
		}
		
		return new ResponseVo( 200, resultMap );
	}

	// AI 담당자 정보 조회
	@GetMapping("/get")
	public Object aiStaffModify(
			@RequestParam(value="pkCompanyStaff", required=false) final String pkCompanyStaff
	) throws Exception {
		
		throwException.requestParamRequied( pkCompanyStaff );

		Map<String, Object> paramMap = new HashMap<>();

		paramMap.put("pkCompanyStaff", Common.parseLong(pkCompanyStaff));

		Map<String, Object> resultMap = aiStaffService.getAIstaff(paramMap);

		return new ResponseVo(200, resultMap);

	}
	
	@GetMapping("/aiMainStaff")
	public Object aiMainStaff() throws Exception {

		Map<String, Object> paramMap = new HashMap<>();
		Map<String, Object> resultMap = aiStaffService.getAIMainStaff(paramMap);

		return new ResponseVo(200, resultMap);

	}
	
	// 부서 수정
	@PutMapping("/update")
	public Object updateAIstaff (
			@RequestBody Map<String, Object> reqJsonObj
	) throws Exception {
		throwException.requestBodyRequied( reqJsonObj, "pkCompanyStaff" );
		aiStaffService.updateAIStaff(reqJsonObj);

		return new ResponseVo(200);

	}

	@PutMapping("/workCodeUpdate")
	public Object workCodeUpdate (
			@RequestBody Map<String, Object> reqJsonObj
	) throws Exception {

		throwException.requestBodyRequied( reqJsonObj, "pkCompanyStaff" );
		throwException.requestBodyRequied( reqJsonObj, "fkStaffWorkCode" );

		aiStaffService.workCodeUpdate(reqJsonObj);

		return new ResponseVo(200);

	}
	
	@GetMapping("/getQuickStart")
	public Object getQuickStart(
			@RequestParam(value="uuid", required=true) final String uuid
		) throws Exception {
		
		Map<String, Object> resultMap = aiStaffService.getQuickStart(uuid);
		
		return new ResponseVo(200, resultMap);
		
	}

	
}
