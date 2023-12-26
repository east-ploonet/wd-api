package com.saltlux.aice_fe.pc.receiveNumber.controller;

import com.saltlux.aice_fe._baseline.baseController.BaseController;
import com.saltlux.aice_fe._baseline.baseVo.ResponseVo;
import com.saltlux.aice_fe._baseline.commons.Common;
import com.saltlux.aice_fe.ecApi.service.PloonetApiService;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.join.vo.CompanyDeptStaffVo;
import com.saltlux.aice_fe.pc.join.vo.CompanyDeptVo;
import com.saltlux.aice_fe.pc.join.vo.CompanyStaffVo;
import com.saltlux.aice_fe.pc.receiveNumber.service.ReceiveNumberService;
import com.saltlux.aice_fe.pc.staff.service.StaffService;
import lombok.extern.slf4j.Slf4j;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("${apiVersionPrefix}/workStage/receiveNumber") // end point : localhost:8080/api/v1/workStage/staff
public class ReceiveNumberController extends BaseController {

	@Autowired
	private ReceiveNumberService receiveNumberService; 

	@Autowired
	private PloonetApiService ploonetApiService;

	// 사용가능 번호 api 호출(type = 0, all)
	@PostMapping("/getApi")
	public Object getApiReceiveNumber(
			@RequestParam(value = "type", required = false) int type
			) throws Exception {
		
		JSONArray data = ploonetApiService.voiceNumbers(type, "normal");
		
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("apiList", data.toString());

		return new ResponseVo(200, resultMap);

	}
	
	// AI staff 리스트 호출 (등록 안된 항목들)
	@RequestMapping(value = {"/list"}, method = {RequestMethod.GET}, produces=PRODUCES_JSON)
	public Object getNumberStaffList(
	) throws Exception {

		Map<String, Object> paramMap = new HashMap<>();

		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		paramMap.put("fkCompany", loginInfoVo.getLoginCompanyPk());

		// AI직원 직무로 조건 추가
//		String staffWorkCode = request.getParameter("staffWorkCode");
//		if(!Common.isBlank(staffWorkCode)) paramMap.put("staffWorkCode", staffWorkCode);
//
//		String orderbyType = request.getParameter("orderType");
//		if(!Common.isBlank(orderbyType)) paramMap.put("orderbyType", orderbyType);

		Map<String, Object> resultMap = receiveNumberService.getNumberStaffList(paramMap);
		
		return new ResponseVo( 200, resultMap );
	}
	
	// AI staff 리스트 호출 (등록 완료된 항목들)
	@RequestMapping(value = {"/completeList"}, method = {RequestMethod.GET}, produces=PRODUCES_JSON)
	public Object getNumberStaffListComplete() throws Exception {

		Map<String, Object> paramMap = new HashMap<>();

		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		paramMap.put("fkCompany", loginInfoVo.getLoginCompanyPk());

		// AI직원 직무로 조건 추가
		String staffWorkCode = request.getParameter("staffWorkCode");
		if(!Common.isBlank(staffWorkCode)) paramMap.put("staffWorkCode", staffWorkCode);

		String orderbyType = request.getParameter("orderType");
		if(!Common.isBlank(orderbyType)) paramMap.put("orderbyType", orderbyType);

		Map<String, Object> resultMap = receiveNumberService.getNumberStaffListComplete(paramMap);

		return new ResponseVo( 200, resultMap );
	}
	

	//등록 시 api 호출
	@PostMapping("/regist")
	public Object registReceiveNumber(
			@RequestParam(value = "fkCompanyStaffAi", required = false) Integer fkCompanyStaffAi,
			@RequestParam(value = "vgwId", required = false) String vgwId,
			@RequestParam(value = "number", required = false) String number
			) throws Exception {
		
			PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
			String userType = loginInfoVo.getLoginUserType();
			long fkCompany = loginInfoVo.getLoginCompanyPk();

			Map<String, Object> resultMap =  ploonetApiService.dnisSave(userType,fkCompany,fkCompanyStaffAi,vgwId,number);
		return new ResponseVo(200,resultMap);
	}
}
