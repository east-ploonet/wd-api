package com.saltlux.aice_fe.pc.quick.service.impl;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saltlux.aice_fe._baseline.baseService.FileService;
import com.saltlux.aice_fe._baseline.baseService.impl.BaseServiceImpl;
import com.saltlux.aice_fe._baseline.baseVo.DataMap;
import com.saltlux.aice_fe._baseline.baseVo.FileVo;
import com.saltlux.aice_fe._baseline.baseVo.ResponseVo;
import com.saltlux.aice_fe._baseline.commons.Common;
import com.saltlux.aice_fe.ecApi.service.PloonetApiService;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.quick.dao.QuickDao;
import com.saltlux.aice_fe.pc.quick.service.QuickService;
import com.saltlux.aice_fe.pc.send.dao.SendDao;
import com.saltlux.aice_fe.pc.send.service.SendService;
import com.saltlux.aice_fe.pc.send.vo.SendCustomerVo;

import ch.qos.logback.core.recovery.ResilientSyslogOutputStream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class QuickServiceImpl extends BaseServiceImpl implements QuickService {

	@Autowired
	private QuickDao quickDao;

	@Override
	public DataMap getQuickStartUserInfo(Map<String, Object> paramMap, String quickStartType) {
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		paramMap.put("fkCompany", loginInfoVo.getLoginCompanyPk());
		
		DataMap resultMap = null;

		if("B2002".equals(quickStartType)) {
			resultMap = quickDao.getQuickStartUserInfo(paramMap);
		}else {
			resultMap = quickDao.getQuickStartCompanyInfo(paramMap);
		}

		return resultMap;
	}
	
	// quick 회사 정보 저장
	@Override
	public void updateUserInfo(Map<String, Object> paramMap, String loginUserType) throws Exception {
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		
		Map<String, Object> paramMapUpdate = new HashMap<>();
		String quickStartType = loginUserType; 
		
		
		try {
			
			if("B2002".equals(quickStartType)) {
				
				paramMapUpdate.put("pkCompanyStaff", loginInfoVo.getLoginCompanyStaffPk());
				paramMapUpdate.put("fkCompany", loginInfoVo.getLoginCompanyPk());
				paramMapUpdate.put("fdAddressCommon", paramMap.get("fdAddressCommon"));
				paramMapUpdate.put("fdAddressDetail", paramMap.get("fdAddressDetail"));
				
				quickDao.updateQuickStartUserInfo(paramMapUpdate);
			}else {
				
				
				paramMapUpdate.put("fkCompany", loginInfoVo.getLoginCompanyPk());
				paramMapUpdate.put("fdBizLicenseNum", paramMap.get("fdBizLicenseNum"));
				paramMapUpdate.put("openDt", paramMap.get("openDt"));
				paramMapUpdate.put("fdAddressCommon", paramMap.get("fdAddressCommon"));
				paramMapUpdate.put("fdAddressDetail", paramMap.get("fdAddressDetail"));
				quickDao.updateQuickStartCompanyInfo(paramMapUpdate);
			}
			
		} catch (Exception ex) {
			System.out.println("ex:" + ex);
			log.error("********** paramMap : {}", paramMap.toString());
			throwException.statusCode(500);
		}
		
	}


}