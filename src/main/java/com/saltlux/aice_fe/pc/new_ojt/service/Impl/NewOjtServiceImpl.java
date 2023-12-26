package com.saltlux.aice_fe.pc.new_ojt.service.Impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.saltlux.aice_fe._baseline.baseService.impl.BaseServiceImpl;
import com.saltlux.aice_fe._baseline.baseVo.DataMap;
import com.saltlux.aice_fe._baseline.commons.Common;
import com.saltlux.aice_fe.ecApi.service.PloonetApiService;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.join.dao.JoinDao;
import com.saltlux.aice_fe.pc.join.vo.CompanyDeptStaffVo;
import com.saltlux.aice_fe.pc.join.vo.CompanyDeptVo;
import com.saltlux.aice_fe.pc.new_ojt.dao.NewOjtDao;
import com.saltlux.aice_fe.pc.new_ojt.service.NewOjtService;
import com.saltlux.aice_fe.pc.new_ojt.vo.NewOjt2StepVo;
import com.saltlux.aice_fe.pc.staff.dao.DeptDao;
import com.saltlux.aice_fe.pc.staff.service.AIStaffService;
import com.saltlux.aice_fe.pc.staff.service.StaffDeptService;
import com.saltlux.aice_fe.pc.staff.service.StaffService;

import ch.qos.logback.core.recovery.ResilientSyslogOutputStream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NewOjtServiceImpl extends BaseServiceImpl implements NewOjtService {
	
	@Autowired
	private NewOjtDao newOjtDao;
	@Autowired
	private AIStaffService aiStaffService;
	
	 @Value("${ploonet.api.talkbot.api.url}")
	 private String talkbotBrokerUrl;
	 
	 @Value("${ploonet.configmanager.api.url}")
	 private String configmanagerUrl;
	 
	 @Autowired
	 PloonetApiService ploonetApiService;
	 
	 @Autowired
	 StaffDeptService staffDeptService;
	 
	 @Autowired
	 StaffService staffService;
	 
	 
	 @Autowired
	 private JoinDao joinDao;
	 
	 @Value("${ploonet.api.brand.api.url}")
	 public String brandApi;
	 
	// OJT1 회사 정보
	@Override
	public Map<String, Object> getInfo(String pkCompanyStaff) throws Exception {
		
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		
		DataMap resultMap1 = null;
		DataMap resultMap2 = null;
		Map<String, Object> paramMap = new HashMap<>();
		
		paramMap.put("fkCompany", loginInfoVo.getLoginCompanyPk());
		paramMap.put("pkCompanyStaff", pkCompanyStaff);
		
		//Map<String, Object> result          = new HashMap<>();
		Map<String, Object> result          = new HashMap<>();
		resultMap1 = newOjtDao.getInfo(paramMap);
		resultMap2 = newOjtDao.getAvatarInfo(paramMap);
		
		result.put("info", resultMap1);
		result.put("avatarInfo", resultMap2);
			
		return result;
	}
	
	// OJT1 회사 정보 저장
	@Override
	public void companyUpdate(Map<String, Object> paramMap) throws Exception {
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		
		paramMap.put("fkCompany", loginInfoVo.getLoginCompanyPk());
		paramMap.put("fkWriter", loginInfoVo.getLoginCompanyStaffPk());
//		paramMap.put("companyEmail", loginInfoVo.getLoginCompanyStaffId()); 0731 이메일은 수정할 수 있다고 하심
		
		try {
			
			int result = newOjtDao.companyUpdate(paramMap);
			
			if ( result <= 0 ) {
				throwException.statusCode(500);
			}
			
			try {
				Map<String, Object> masterUuid = staffService.masterStaffUuid(paramMap);
				if(masterUuid != null) {
					String staffUuid = masterUuid.get("uuid").toString();
					String solutionType = masterUuid.get("solutionType").toString();
					
					if(staffUuid != null && solutionType.equals("B2001")) {
						
							org.json.JSONObject apiJsonInfo = new org.json.JSONObject();
				            apiJsonInfo.put("memberTypeCd", "M03B0");
				            apiJsonInfo.put("companyMainTelno", paramMap.get("fdCompanyPhone").toString());
				            apiJsonInfo.put("managerEmail", paramMap.get("companyEmail").toString());
							Map<String, Object> apiResult = Common.getRestDataBrandApi(brandApi + "/api/if/v1/member/" + staffUuid, apiJsonInfo, 0, brandApi, null, "companyPath");	
					}
			}
			}catch (Exception e) {
				System.out.println("brand api error:" + e);
			}

		} catch (Exception ex) {
			System.out.println("ex:" + ex);
			log.error("********** paramMap : {}", paramMap.toString());
			throwException.statusCode(500);
		}
	}
	
	// OJT1 회사 정보 저장
	@Override
	public void companyTimeUpdate(Map<String, Object> paramMap) throws Exception {
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		
		Map<String, Object> paramMapUpdate = new HashMap<>();
		
		// 99 + workType "W"
		String wTimeFroms = Common.NVL(paramMap.get("wTimeFrom"), null);
		String wTimeTos = Common.NVL(paramMap.get("wTimeTo"), null);
		
		String [] wTimeFrom = wTimeFroms.split(":");
		String [] wTimeTo =  wTimeTos.split(":");
		int wTimeFromTime = Common.parseInt(wTimeFrom[0]);
		int wTimeFromMin = Common.parseInt(wTimeFrom[1]);
		int wTimeToTime = Common.parseInt(wTimeTo[0]);
		int wTimeToMin = Common.parseInt(wTimeTo[1]);
		

		// 99 + workType "R" rest_lunch
		String rLTimeFroms = Common.NVL(paramMap.get("rLTimeFrom"), null);
		String rLTimeTos = Common.NVL(paramMap.get("rLTimeTo"), null);
		
		String [] rLTimeFrom =  rLTimeFroms.split(":");
		String [] rLTimeTo =  rLTimeTos.split(":");
		int rLTimeFromTime = Common.parseInt(rLTimeFrom[0]);
		int rLTimeFromMin = Common.parseInt(rLTimeFrom[1]);
		int rLTimeToTime = Common.parseInt(rLTimeTo[0]);
		int rLTimeToMin = Common.parseInt(rLTimeTo[1]);
		
		
		try {
			Map<String, Object> paramMapAi = new HashMap<>();
			Map<String, Object> mainAIStaff = aiStaffService.getAIMainStaff(paramMapAi);
			long pkCompanyStaff = Common.parseLong(mainAIStaff.get("pkCompanyStaff"));
			// default 회사정보, ai pk, weeknum 99 , tfh tfm tth ttm , wt, time_type, useyn
			paramMapUpdate.put("fkCompany", loginInfoVo.getLoginCompanyPk());
			paramMapUpdate.put("fkCompanyStaffAi", pkCompanyStaff);
			
			paramMapUpdate.put("weekNum", 99);
			paramMapUpdate.put("useYn", "Y");
			paramMapUpdate.put("fkWriter", loginInfoVo.getLoginCompanyStaffPk());
//			int result = newOjtDao.companyTimeUpdate(paramMap);
			
			int result = 0;
//			result = newOjtDao.companyTimeDelete(paramMapUpdate);
			for (int i = 0; i<2 ; i++) {
				Integer pkAiConfDayOn = 0;
				if(i == 0) {
					paramMapUpdate.put("timeFromHh", wTimeFromTime);
					paramMapUpdate.put("timeFromMin", wTimeFromMin);
					paramMapUpdate.put("timeToHh", wTimeToTime);
					paramMapUpdate.put("timeToMin", wTimeToMin);
					paramMapUpdate.put("workType", "W");
					paramMapUpdate.put("timeType", "WORK_ON");
					pkAiConfDayOn = newOjtDao.getCompanyTime(paramMapUpdate);
				}else {
					paramMapUpdate.put("timeFromHh", rLTimeFromTime);
					paramMapUpdate.put("timeFromMin", rLTimeFromMin);
					paramMapUpdate.put("timeToHh", rLTimeToTime);
					paramMapUpdate.put("timeToMin", rLTimeToMin);
					paramMapUpdate.put("workType", "R");
					paramMapUpdate.put("timeType", "REST_LUNCH");
					pkAiConfDayOn = newOjtDao.getCompanyTime(paramMapUpdate);
				}
				paramMapUpdate.put("pk_ai_conf_day_on", pkAiConfDayOn);
				result = newOjtDao.companyTimeInsert(paramMapUpdate);
			}
			//
			if ( result <= 0 ) {
				throwException.statusCode(500);
			}
			

		} catch (Exception ex) {
			System.out.println("ex:" + ex);
			log.error("********** paramMap : {}", paramMap.toString());
			throwException.statusCode(500);
		}
		
	}
	
	@Override
	public void tempDnisDelete(Map<String, Object> paramMap) throws Exception {
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		String pkCompanyStaff = paramMap.get("pkCompanyStaff").toString();
		DataMap companyDnisInfo = newOjtDao.getCompanyDnis(paramMap);
		if(companyDnisInfo != null) {
			String fullDnis = companyDnisInfo.get("fullDnis").toString();
			String dnis = companyDnisInfo.get("fdDnis").toString();
			paramMap = ploonetApiService.tempDnisDelete(fullDnis, dnis, loginInfoVo.getLoginCompanyPk(), Long.parseLong(pkCompanyStaff));
		}	
		
	}
	
	// OJT1 회사 정보 저장
	@Override
	public void aiTimeUpdate(Map<String, Object> paramMap, long pkCompanyStaff) throws Exception {
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		
		Map<String, Object> paramMapUpdate = new HashMap<>();
		
		try {
			Map<String, Object> paramMapAi = new HashMap<>();
			Map<String, Object> mainAIStaff = aiStaffService.getAIMainStaff(paramMapAi);
//			long pkCompanyStaff = Common.parseLong(mainAIStaff.get("pkCompanyStaff"));
			// default 회사정보, ai pk, weeknum 99 , tfh tfm tth ttm , wt, time_type, useyn
			
			paramMapUpdate.put("fkCompany", loginInfoVo.getLoginCompanyPk());
			paramMapUpdate.put("fkCompanyStaffAi", pkCompanyStaff);
			paramMapUpdate.put("fkWriter", loginInfoVo.getLoginCompanyStaffPk());
			
			paramMapUpdate.put("useYn", "Y");
//				int result = newOjtDao.companyTimeUpdate(paramMap);
			
			int result = 0;
			HashMap hashMap = new HashMap<>();
			
			// 리스트를 그냥 긁어와서
//			result = newOjtDao.aiTimeDelete(paramMapUpdate); // 어짜피 이제 안지울거지만 수정
			for (int i = 0; i<7; i++) {
				
				// 여기서 weekNum 이 i 인걸로 select를 함 (( pk 읽어옴.. ))
			
				Map<String, Object> param = new HashMap<>();
				param.put("fkCompany", loginInfoVo.getLoginCompanyPk());
				param.put("fkCompanyStaffAi", pkCompanyStaff);
				param.put("weekNum", (i+1));
				

				Integer pkAiConfDayOn = newOjtDao.getAiTime(param);

				System.out.println("pkAiConfDayOn : " + pkAiConfDayOn);
				
				// 
				hashMap =  (HashMap) paramMap.get(i + "");
				paramMapUpdate = new HashMap<>();
				paramMapUpdate.put("useYn", "Y");
				paramMapUpdate.put("fkCompany", loginInfoVo.getLoginCompanyPk());
				paramMapUpdate.put("fkCompanyStaffAi", pkCompanyStaff);
				paramMapUpdate.put("fkWriter", loginInfoVo.getLoginCompanyStaffPk());
				paramMapUpdate.put("pk_ai_conf_day_on", pkAiConfDayOn);
				
				if(hashMap != null){
					paramMapUpdate.put("weekNum", Common.parseInt(hashMap.get("newWeekNum")) + 1);
					paramMapUpdate.put("timeFromHh", hashMap.get("timeFromHh"));
					paramMapUpdate.put("timeFromMin", hashMap.get("timeFromMin"));
					paramMapUpdate.put("timeToHh", hashMap.get("timeToHh"));
					paramMapUpdate.put("timeToMin", hashMap.get("timeToMin"));
					paramMapUpdate.put("workType", "W");
					paramMapUpdate.put("timeType", "WORK_ON");
					paramMapUpdate.put("enableYn", "Y");
					result = newOjtDao.aiTimeInsert(paramMapUpdate);
				}else {
//					if(i == 0 || i == 6) {
						// 그냥 0~0 으로 넣어달라고 함 20231019 요청사항
						paramMapUpdate.put("timeFromHh", 0);
						paramMapUpdate.put("timeFromMin", 0);
						paramMapUpdate.put("timeToHh", 0);
						paramMapUpdate.put("timeToMin", 0);
//					}
					paramMapUpdate.put("weekNum", i + 1);
					paramMapUpdate.put("workType", null);
					paramMapUpdate.put("timeType", null);
					paramMapUpdate.put("enableYn", "N");
					result = newOjtDao.aiTimeInsert(paramMapUpdate);
				}

			}
			//
			if ( result <= 0 ) {
				throwException.statusCode(500);
			}
			
			
		} catch (Exception ex) {
			System.out.println("ex:" + ex);
			log.error("********** paramMap : {}", paramMap.toString());
			throwException.statusCode(500);
		}
		
	}
	
	
	// OJT1 첫인사말 / 긴급안내멘트 정보 저장
	@Override
	public void companyIntroUpdate(Map<String, Object> paramMap, JSONArray msgBodyJsonArr, long pkCompanyStaff) throws Exception {
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		
		Map<String, Object> paramMapUpdate = new HashMap<>();
		paramMapUpdate.put("fkCompany", loginInfoVo.getLoginCompanyPk());
		paramMapUpdate.put("fkWriter", loginInfoVo.getLoginCompanyStaffPk());
		paramMapUpdate.put("msgBefore", Common.NVL(paramMap.get("msgBefore"), null ));
		paramMapUpdate.put("defaultYn", Common.NVL(paramMap.get("defaultYn"), null ));
		paramMapUpdate.put("useYn", Common.NVL(paramMap.get("useYn"), null ));
		paramMapUpdate.put("warnYn", Common.NVL(paramMap.get("warnYn"), null ));
		paramMapUpdate.put("fkModifier", loginInfoVo.getLoginCompanyStaffPk());
		
		Map<String, Object> mainAIStaff = aiStaffService.getAIMainStaff(paramMapUpdate);
		paramMapUpdate.put("fkCompanyStaffAi", pkCompanyStaff);
		
		for (Map.Entry<String, Object> pair : paramMap.entrySet()) {
			Integer pkAiConfIntro = 0;
			  if(pair.getKey().contains("msgBody")) {				  
				  paramMapUpdate.put(pair.getKey(), pair.getValue());
				  System.out.println("pair.getKey() " + pair.getKey());
				  System.out.println("pair.getValue()" + pair.getValue());
				  paramMapUpdate.put("fkCompanyStaffAi", pkCompanyStaff);
				  pkAiConfIntro = newOjtDao.getCompanyIntro(paramMapUpdate);
			  }else if(pair.getValue() != null) {
				  paramMapUpdate.put(pair.getKey(), null);
				  paramMapUpdate.put("fkCompanyStaffAi", pkCompanyStaff);
				  pkAiConfIntro = newOjtDao.getCompanyIntro(paramMapUpdate);
			  }
			  paramMapUpdate.put("pk_ai_conf_intro", pkAiConfIntro);
		}
		System.out.println("저장 데이터 : "+paramMapUpdate);
		
		
//		int msgBodyJsonArrSize = paramMap.length();
//		for (int i = 0; i < msgBodyJsonArrSize; i++) {
//			Integer pkAiConfIntro = 0;
//			JSONObject msgObj = msgBodyJsonArr.getJSONObject(i);
//			String msgBodyName = msgObj.getString("id");
//			String msgBodyValue = msgObj.getString("value");
//			if(!msgBodyName.equals("msgBody6")) {
//				// 직접 입력이 아니면
//				paramMapUpdate.put(msgBodyName, msgBodyValue);
//				pkAiConfIntro = newOjtDao.getCompanyIntro(paramMapUpdate);
//			}else {
//				paramMapUpdate.put("msgBody6", Common.NVL(paramMap.get("msgBody6"), null ));
//				pkAiConfIntro = newOjtDao.getCompanyIntro(paramMapUpdate);
//			}
//			paramMapUpdate.put("pk_ai_conf_intro", pkAiConfIntro);
//		}
		
		try {
//			newOjtDao.companyIntroDelete(paramMapUpdate);
			int result = newOjtDao.companyIntroUpdate(paramMapUpdate);
			//
			if ( result <= 0 ) {
				throwException.statusCode(500);
			}
			
		} catch (Exception ex) {
			System.out.println("ex:" + ex);
			log.error("********** paramMap : {}", paramMap.toString());
			throwException.statusCode(500);
		}
	}
	
	// OJT1 첫인사말 / 긴급안내멘트
	@Override
	public DataMap getIntro(String pkCompanyStaff, String gubun) throws Exception {
		
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		
		DataMap resultMap = null;
		Map<String, Object> paramMap = new HashMap<>();
		Map<String, Object> mainAIStaff = aiStaffService.getAIMainStaff(paramMap);
//		long pkCompanyStaff = Common.parseLong(mainAIStaff.get("pkCompanyStaff"));
		
		paramMap.put("pkCompanyStaff", pkCompanyStaff);
		paramMap.put("fkCompany", loginInfoVo.getLoginCompanyPk());
		//Map<String, Object> result          = new HashMap<>();
		
		resultMap = newOjtDao.getIntro(paramMap);
		//resultMap.put("companyEmail", loginInfoVo.getLoginCompanyStaffId());
		
		if(!gubun.equals("1")) {
			paramMap.put("fkCompanyStaffAi", paramMap.get("pkCompanyStaff").toString());
			
			DataMap companyDnisInfo = newOjtDao.getCompanyDnis(paramMap);
			if(companyDnisInfo != null) {
				String fullDnis = companyDnisInfo.get("fullDnis").toString();
				String dnis = companyDnisInfo.get("fdDnis").toString();
				paramMap = ploonetApiService.tempDnisDelete(fullDnis, dnis, loginInfoVo.getLoginCompanyPk(), Long.parseLong(pkCompanyStaff));
			}	
		}
		
		return resultMap;
	}

	// OJT3 업무 시간
	@Override
	public Map<String, Object> getOfficeTimeList(Map<String, Object> paramMap) throws Exception {
		
		List<Object> listData = newOjtDao.getOfficeTimeList(paramMap);
		Map<String, Object> result          = new HashMap<>();
		result.put("list", listData);
		
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();

		paramMap.put("fkCompanyStaffAi", paramMap.get("pkCompanyStaff").toString());
		paramMap.put("fkCompany", loginInfoVo.getLoginCompanyPk());
		
		DataMap companyDnisInfo = newOjtDao.getCompanyDnis(paramMap);
		if(companyDnisInfo != null) {
			String fullDnis = companyDnisInfo.get("fullDnis").toString();
			String dnis = companyDnisInfo.get("fdDnis").toString();
			paramMap = ploonetApiService.tempDnisDelete(fullDnis, dnis, loginInfoVo.getLoginCompanyPk(), Long.parseLong(paramMap.get("pkCompanyStaff").toString()));
		}
		
		return result;
	}
	
	// OJT3 업무 시간
	@Override
	public Map<String, Object> getTimeOrientation(Map<String, Object> paramMap) throws Exception {
		
		List<Object> listData = newOjtDao.getTimeOrientation(paramMap);
		Map<String, Object> result          = new HashMap<>();
		result.put("list", listData);
		return result;
	}
	
	// OJT1 회사 업무시간
	@Override
	public Map<String, Object> getCompanyTimeList(String pkCompanyStaff) throws Exception {
		
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		
		DataMap resultMap = null;
		Map<String, Object> paramMap = new HashMap<>();
		
		paramMap.put("fkCompany", loginInfoVo.getLoginCompanyPk());
		paramMap.put("pkCompanyStaff", pkCompanyStaff);
		//Map<String, Object> result          = new HashMap<>();
		
		List<Object> listData = newOjtDao.getCompanyTimeList(paramMap);
		Map<String, Object> result          = new HashMap<>();
		result.put("list", listData);
		
		return result;
	}
	
	// OJT1 첫인사말 / 긴급안내멘트 정보 저장
	@Override
	public void aiStaffStatusUpdate(long companyStaff, String fdStaffStatusCode) throws Exception {
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		
		Map<String, Object> paramMap = new HashMap<>();
		
		paramMap.put("pkCompanyStaff", companyStaff);
		paramMap.put("fdStaffStatusCode", fdStaffStatusCode);
		paramMap.put("fkCompany", loginInfoVo.getLoginCompanyPk());
		paramMap.put("fkWriter", loginInfoVo.getLoginCompanyStaffPk());
		
		try {
			
			int result = newOjtDao.aiStaffStatusUpdate(paramMap);
			//
			if ( result <= 0 ) {
				throwException.statusCode(500);
			}
			
		} catch (Exception ex) {
			System.out.println("ex:" + ex);
			log.error("********** paramMap : {}", paramMap.toString());
			throwException.statusCode(500);
		}
	}
		
	//OJT2 Step 리스트 불러오기 (신규등록할때)
	@Override
	public Map<String, Object> getStepList(Map<String, Object> paramMap) throws Exception {

		List<Object> listData = newOjtDao.getStepList(paramMap);
		Map<String, Object> result          = new HashMap<>();
		result.put("list", listData);
		
		
		
		return result;
	}
	
	//OJT2 등록 후 리스트 불러오기
	@Override
	public Map<String, Object> getStepAll(Map<String, Object> paramMap) throws Exception {
		
		//Map<String, Object> mainAIStaff = aiStaffService.getAIMainStaff(paramMap);
		//long pkCompanyStaff = Common.parseLong(mainAIStaff.get("pkCompanyStaff"));
		long pkCompanyStaff = Common.parseLong(paramMap.get("pkCompanyStaff"));
		paramMap.put("fkCompanyStaffAi", pkCompanyStaff);
		List<Object> listDataStep12 = newOjtDao.getStepOneTwo(paramMap);
		List<Object> listDataStep12Null = newOjtDao.getStepOneTwoNull(paramMap);
		List<Object> listDataStep34 = newOjtDao.getStepThreeFour(paramMap);
		//안내데스크 주소 항목 list
		List<Object> listDataStep34Address = newOjtDao.getStepThreeFourAddress(paramMap);
		Map<String, Object> result          = new HashMap<>();
		result.put("listOneTwo", listDataStep12);
		result.put("listThreeFour", listDataStep34);
		result.put("listThreeFourAddress", listDataStep34Address);
		result.put("listOneTwoNull", listDataStep12Null);
		
		
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();

		paramMap.put("fkCompanyStaffAi", pkCompanyStaff);
		paramMap.put("fkCompany", loginInfoVo.getLoginCompanyPk());
		paramMap.put("frontStatus", "COMPLETE");
		paramMap.put("botStatus", "COMPLETE");
		newOjtDao.aiConfWorkStatusUpdate(paramMap); 
		
		paramMap.put("fkCompanyStaffAi", pkCompanyStaff);
		paramMap.put("fkCompany", loginInfoVo.getLoginCompanyPk());
		//paramMap.put("botStatus", "FINISHED");
		paramMap.put("botStatus", "COMPLETE"); // 원본
		
		int cnt = newOjtDao.getBotStatusCount(paramMap);
		result.put("cnt", cnt);
		
		return result;
	}
	
	@Override
	public Map<String, Object> stepMenuBotStatusCnt(String pkCompanyStaff) throws Exception {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("fkCompanyStaffAi", pkCompanyStaff);
		Map<String, Object> result          = new HashMap<>();
		
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		
		paramMap.put("fkCompanyStaffAi", pkCompanyStaff);
		paramMap.put("fkCompany", loginInfoVo.getLoginCompanyPk());
		paramMap.put("botStatus", "COMPLETE"); // 원본
		
		int cnt = newOjtDao.getBotStatusCount(paramMap);
		result.put("cnt", cnt);
		
		return result;
	}
	
		
	//OJT2 Step 저장
	@Override
	public void ojt2StepRegist(Map<String, Object> paramMap) throws Exception {
		
		//List<Map<String, Object>> ojt2StepRegist = (List<Map<String, Object>>) paramMap.get("formData");
		Map<String, Object> ojt2StepRegist2 = (Map<String, Object>) paramMap.get("formData");
		
		//tbl_company 저장값
		Map<String, Object> ojt2StepRegist2Address = (Map<String, Object>) paramMap.get("companyData");
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		
		//안내데스크 업로드 값
		String deskFileName = Common.NVL((String) paramMap.get("deskFileName"));
		int gubun = Common.parseInt(paramMap.get("gubun"));
		
		// 담당자연결
		NewOjt2StepVo newOjt2StepVo2 = new NewOjt2StepVo();
		newOjt2StepVo2.setFk_writer(loginInfoVo.getLoginCompanyStaffPk());
		//Map<String, Object> mainAIStaff = aiStaffService.getAIMainStaff(paramMap);
		//long pkCompanyStaff = Common.parseLong(mainAIStaff.get("pkCompanyStaff"));
		long pkCompanyStaff = Common.parseLong(paramMap.get("pkCompanyStaff"));
		
		paramMap.put("fkCompanyStaffAi", pkCompanyStaff);
		paramMap.put("fkCompany", loginInfoVo.getLoginCompanyPk());
		
		//Step1,2 초기화
		newOjtDao.ojt2StepOneTwoDelete(paramMap);
		//Step3,4 초기화
		newOjtDao.ojt2StepThreeFourDelete(paramMap);
		
		try {
			if(ojt2StepRegist2 != null) {
				NewOjt2StepVo newOjt2StepVo = new NewOjt2StepVo();
				newOjt2StepVo.setFk_writer(loginInfoVo.getLoginCompanyStaffPk());
				
				
				//-----------------------------------------------------STEP 1,2 영역 저장-----------------------------------------------------//
				//1.Step1 저장
				for (Entry<String, Object> entrySet : ojt2StepRegist2.entrySet()) {
					if(entrySet.getKey() !=null) {
						newOjt2StepVo.setFk_company(loginInfoVo.getLoginCompanyPk());
						newOjt2StepVo.setFk_comapny_staff_ai(pkCompanyStaff);
						newOjt2StepVo.setAi_work_cd(entrySet.getKey());
						newOjt2StepVo.setP_ai_work_cd(null);
						newOjt2StepVo.setFk_writer( loginInfoVo.getLoginCompanyStaffPk());
						if(gubun == 0) newOjt2StepVo.setFront_status("INIT");
						else {
							newOjt2StepVo.setFront_status("RETRAINING");
						}
						newOjtDao.ojt2StepOneTwoRegist(newOjt2StepVo);
					}
		            System.out.println(entrySet.getKey() + " : " + entrySet.getValue());
		        if(entrySet.getValue().getClass().getTypeName() != "java.util.ArrayList") {
		        	Map<String, Object> step2List = (Map<String, Object>) entrySet.getValue();
		        	//2.Step2 저장
		        	for(String MapKey : step2List.keySet()) {
		        		newOjt2StepVo.setFk_company(loginInfoVo.getLoginCompanyPk());
		        		newOjt2StepVo.setFk_comapny_staff_ai(pkCompanyStaff);
		        		newOjt2StepVo.setAi_work_cd(MapKey);
		        		newOjt2StepVo.setP_ai_work_cd(entrySet.getKey());
		        		newOjt2StepVo.setFk_writer( loginInfoVo.getLoginCompanyStaffPk());
		        		if(gubun == 0) newOjt2StepVo.setFront_status("INIT");
		        		else{
		        			newOjt2StepVo.setFront_status("RETRAINING");
		        		}
		        		newOjtDao.ojt2StepOneTwoRegist(newOjt2StepVo);
		        	}
		        	//3. Step3,4 저장
		        	for(Entry<String, Object> step3Set : step2List.entrySet()) {
		        		
		        		
		        		Map<String, Object> step3List = (Map<String, Object>) step3Set.getValue();
		        		
		        		for(String MapKey : step3List.keySet()) {
		        			
		        			if(MapKey=="CTGR3_DEPART_INFO"){
		        				newOjt2StepVo.setPath_file(deskFileName);
		        			}
		        			
		        			//안내데스크 - 회사위치 및 주차시설 안내하기(테이블 다름)- tbl_company
		        			if(MapKey=="CTGR3_COMPANY_INFO"){
		        				newOjt2StepVo.setFk_company(loginInfoVo.getLoginCompanyPk());
		        				newOjt2StepVo.setSolution_type("B11");
		        				newOjt2StepVo.setUser_type(loginInfoVo.getLoginUserType());
		        				newOjt2StepVo.setFd_address_zipcode(Common.NVL(ojt2StepRegist2Address.get("ojtAddressZipcode"), null));
		        				newOjt2StepVo.setFd_address_common(Common.NVL(ojt2StepRegist2Address.get("fdAddressCommon"), null));
		        				newOjt2StepVo.setFd_address_detail(Common.NVL(ojt2StepRegist2Address.get("fdAddressDetail"), null));
		        				newOjt2StepVo.setCompany_parking_msg(Common.NVL(ojt2StepRegist2Address.get("companyParkingMsg"), null));
		    
		        				// 여기다가 추가하면 될거만 같아요
		        
		        				newOjt2StepVo.setPath_file(null);
		        				newOjtDao.ojt2StepThreeFourRegistAddress(newOjt2StepVo);
		        				
		        			}
		        			Map<String, Object> step3Data = (Map<String, Object>) step3List.get(MapKey);
		        			newOjt2StepVo.setFk_company(loginInfoVo.getLoginCompanyPk());
		        			newOjt2StepVo.setFk_comapny_staff_ai(pkCompanyStaff);
		        			newOjt2StepVo.setAi_work_cd(Common.NVL(MapKey, null));
		        			newOjt2StepVo.setP_ai_work_cd(Common.NVL(step3Set.getKey(), null));
		        			if((step3Data.get("enableYn")).equals("N")) {
		        				newOjt2StepVo.setEnable_yn(Common.NVL(step3Data.get("enableYn"), "N"));
		        				newOjt2StepVo.setMember_name(null);
		        				newOjt2StepVo.setMember_phone(null);
		        				newOjt2StepVo.setMember_mobile(null);
		        				newOjt2StepVo.setTerm_unit(null);
		        				newOjt2StepVo.setTerm_val(Common.parseInt(step3Data.get("termVal"),0));
		        				newOjt2StepVo.setFk_writer( loginInfoVo.getLoginCompanyStaffPk());
		        				
		        			}else{
		        				newOjt2StepVo.setEnable_yn(Common.NVL(step3Data.get("enableYn"), "Y"));
		        				//null처리
		        				if((step3Data.get("memberName"))!=null) {
		        					newOjt2StepVo.setMember_name(Common.NVL(step3Data.get("memberName"), null));
		        				}
		        				if((step3Data.get("memberPhone"))!=null) {
		        					newOjt2StepVo.setMember_phone(Common.NVL(step3Data.get("memberPhone"), null));
		        				}
		        				if((step3Data.get("memberMobile"))!=null) {
		        					newOjt2StepVo.setMember_mobile(Common.NVL(step3Data.get("memberMobile"), null));
		        				}	
		        			}
		        			//장애접수
		        			if(Common.parseInt(step3Data.get("termVal"),0) != 0) {
		        				if(Common.parseInt(step3Data.get("termVal"),0) != 30) {
		        					newOjt2StepVo.setTerm_unit("H");
		        					newOjt2StepVo.setTerm_val(Common.parseInt(step3Data.get("termVal"),0));
		        				}else {
		        					newOjt2StepVo.setTerm_unit("D");
		        					newOjt2StepVo.setTerm_val(Common.parseInt(step3Data.get("termVal"),0));
		        				}
		        			}else {
		        				newOjt2StepVo.setTerm_val(Common.parseInt(step3Data.get("termVal"),0));
		        			}
		        			
		        			newOjt2StepVo.setFk_writer( loginInfoVo.getLoginCompanyStaffPk());
		        			//장애접수 엑셀 업로드
		        			System.out.println("MapKey : " + MapKey);
							if(MapKey.equals("CTGR3_FAULT_LIST")){
								String fileName = Common.NVL((String) paramMap.get("fileName"));
								if(fileName !="") {
									int excelCnt = newOjtDao.getExcelUploadCount(paramMap);
									String path = "B11/" + loginInfoVo.getLoginUserType() + "/" + loginInfoVo.getLoginCompanyPk() + "/" + pkCompanyStaff + "/CTGR1_MANAGE/CTGR2_RECEPTIONIST/CTGR3_DEPART_INFO/"+fileName;
									if(excelCnt == 0) {
										newOjt2StepVo = new NewOjt2StepVo();
										newOjt2StepVo.setFk_company(loginInfoVo.getLoginCompanyPk());
										newOjt2StepVo.setFk_comapny_staff_ai(pkCompanyStaff);
										newOjt2StepVo.setAi_work_cd("CTGR3_FAULT_LIST");
										newOjt2StepVo.setP_ai_work_cd("CTGR2_FAULT_RECEPTION");
										newOjt2StepVo.setEnable_yn("Y");
										newOjt2StepVo.setPath_file(path);
										//변경되는값
										newOjt2StepVo.setFk_writer( loginInfoVo.getLoginCompanyPk());	
										//newOjtDao.ojt2StepThreeFourRegist(newOjt2StepVo);
									}	
								}
							}
		        			//null 처리
		        			if((step3Data.get("taskVal"))==null) {
		        				newOjt2StepVo.setTask_val(null);
		        				//장애접수 엑셀 경로는 따로 처리
		        				if(MapKey.equals("CTGR3_FAULT_LIST")) {
		        					newOjt2StepVo.setTask_val(null);
		        				}
		        			}else {
		        				// 영업 - 태그 키워드 (다중 콤마로 구분)
		        				if(step3Data.get("taskVal") instanceof List) {
		        					
		        					String tag = step3Data.get("taskVal").toString();
		        					String charsToRemove = "[]";
		        					
		        					for (char c : charsToRemove.toCharArray()) {
		        						tag = tag.replace(String.valueOf(c), "");
		        					}
		        					String resultTag = tag.replace(" ", "");
		        					newOjt2StepVo.setTask_val(resultTag);
		        				}else {
		        					newOjt2StepVo.setTerm_unit(null);
		        					newOjt2StepVo.setTask_val(Common.NVL(step3Data.get("taskVal"),""));		            			
		        					
		        				}
		        			}		            		
		        			newOjtDao.ojt2StepThreeFourRegist(newOjt2StepVo);
		        			
		        		}
		        		//장애접수 엑셀 업로드
		        		
		        	}
		        	
		        }
	            	
				}
        
				// AI직원 상태값 변경 A1101: 정상(설정완료), A1102 : 정지(임시저장중),
				paramMap.put("pkCompanyStaff", pkCompanyStaff);
				if(gubun == 0) paramMap.put("fdStaffStatusCode", "A1102");
				else paramMap.put("fdStaffStatusCode", "A1101");
				
				aiStaffService.updateAIStaffStatus(paramMap);
				
//				paramMap.put("fkCompany", loginInfoVo.getLoginCompanyPk());
//				paramMap.put("fkCompanyStaffAi", pkCompanyStaff);
//				paramMap.put("aiWorkCd", "CTGR3_FAULT_LIST");
//				paramMap.put("pAiWorkCd", "CTGR2_FAULT_RECEPTION");
//				int excelCnt = newOjtDao.getExcelUploadCount(paramMap);
//				if(excelCnt == 0) {
//					newOjt2StepVo = new NewOjt2StepVo();
//					newOjt2StepVo.setFk_company(loginInfoVo.getLoginCompanyPk());
//					newOjt2StepVo.setFk_comapny_staff_ai(pkCompanyStaff);
//					newOjt2StepVo.setAi_work_cd("CTGR3_FAULT_LIST");
//					newOjt2StepVo.setP_ai_work_cd("CTGR2_FAULT_RECEPTION");
//					newOjt2StepVo.setEnable_yn("N");
//					//변경되는값
//					newOjt2StepVo.setFk_writer( loginInfoVo.getLoginCompanyPk());
//					
//					newOjtDao.ojt2StepThreeFourRegist(newOjt2StepVo);
//				}
				
			}
			}catch (Exception e) {
			System.out.println("ex:" + e);
			log.error("********** paramMap : {}", paramMap.toString());
			throwException.statusCode(500);
		}
	}
	
	@Override
	public int botStatusCount(String companyStaff) throws Exception {
		Map<String, Object> paramMap = new HashMap<>();
		
		int result = 0;
		try {
			PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
			paramMap.put("fkCompanyStaffAi", companyStaff);
			paramMap.put("fkCompany", loginInfoVo.getLoginCompanyPk());
			//paramMap.put("botStatus", "FINISHED");
			paramMap.put("botStatus", "COMPLETE"); // 원본
			
			paramMap.put("frontStatus", "LEARNING");
			
			result = newOjtDao.getBotStatusCount(paramMap);
			
			paramMap.put("botStatus", "FAILED");
			
			int failedCnt = newOjtDao.getBotStatusCount(paramMap);
			
			if(failedCnt > 0) {
				result = -3;
				return result;
			}
			if(result > 0) {
				
				paramMap.put("fkCompanyStaffAi", companyStaff);
				paramMap.put("fkCompany", loginInfoVo.getLoginCompanyPk());
				paramMap.put("frontStatus", "TRAINING");
				
				newOjtDao.aiConfWorkStatusUpdate(paramMap); 
				
				DataMap companyDnisInfo = newOjtDao.getCompanyDnis(paramMap);
				if(companyDnisInfo != null) {
					String fullDnis = companyDnisInfo.get("fullDnis").toString();
					String dnis = companyDnisInfo.get("fdDnis").toString();
					paramMap = ploonetApiService.tempDnisDelete(fullDnis, dnis, loginInfoVo.getLoginCompanyPk(), Long.parseLong(companyStaff));
				}
				
				JSONArray numberArr = ploonetApiService.voiceNumbers(0, "temp");
				log.info("!!!!!!vocie numberArr :" + numberArr);
				System.out.println("numberArr:" + numberArr);
				JSONObject numberObj = (JSONObject) numberArr.get(0);
				//JSONObject numberObj = new JSONObject();
				log.info("!!!!!!Config manager numberObj :" + numberObj);
								
				//-------------------------무료 시작하기 봇이 있을 시 번호 할당 추가-------------------------//
				//1. AI 리스트 호출
				
				Map<String, Object> resultMap = new HashMap<>();
				
				
				String authToken = loginInfoVo.getAuthToken();
				JSONParser jsonParser = new JSONParser();
				String aiStarffUrl = "/aice/configManager/v1/companies";
				org.json.simple.JSONObject jsonObj = new org.json.simple.JSONObject(resultMap);
				
				System.out.println("url 주소 : "+configmanagerUrl+aiStarffUrl+"/"+loginInfoVo.getLoginCompanyPk()+"/ais");
				
				// 퀵스타트 Ai 리스트 불러오기
				try {
					String apiResult = Common.getRestDataApiGet(configmanagerUrl+aiStarffUrl+"/"+loginInfoVo.getLoginCompanyPk()+"/ais", authToken, "GET");
					System.out.println("apiResultAIList : " + apiResult);
					jsonObj = (org.json.simple.JSONObject) jsonParser.parse(apiResult);
		        	org.json.simple.JSONObject aiStaffObj = (org.json.simple.JSONObject) jsonObj.get("data");
		        	resultMap.put( "data", aiStaffObj);
				}catch (Exception e) {
					System.out.println("Exception" + e);
				}
				
				//기존AI
				boolean mainAi = false;
				//퀵스타트AI
				boolean quickAi = false;
				
				//1.변경staff 값
				Long mainAistaffSeq = null;
				Long quickAistaffSeq = null;
				//2. 할당해줘야 되는 번호 값
				String dnisNum = "";
				
				Map<String, Object> resultMapList1 = (Map<String, Object>) resultMap.get("data");
				org.json.simple.JSONArray array = (org.json.simple.JSONArray) resultMapList1.get("list");
				for(int i=0; i<array.size(); i++) {
					org.json.simple.JSONObject staffList = (org.json.simple.JSONObject) array.get(i);			
					//기존사용자
					if((Long)staffList.get("quickStartStatus") == -1) {
						mainAistaffSeq = (Long)staffList.get("aiStaffSeq");
						mainAi = true;
					//퀵스타트 
					}else if(((Long)staffList.get("quickStartStatus") > 0)){
						quickAistaffSeq = (Long)staffList.get("aiStaffSeq");
						//번호 있음 여부 체크
						//dnis 번호(-)제거
						String dnis = (String)staffList.get("dnisNum");
						//하이픈 제거
						String match = "[^0-9]";
						if(dnis != null) {
							dnis = dnis.replaceAll(match, "");
							//070 제거
							dnisNum = dnis.substring(3);
						}
						quickAi = true;
					}
					
				}
				// AI 정보 수정 -> 퀵스타트 (조이 봇)
				if(mainAi && quickAi) {
					if(quickAistaffSeq != null) {
						try {
							//2.body 보내줘야 될 json 세팅 (조이 봇 사용 안함)
							JSONObject jsonObBody = new JSONObject();
							jsonObBody.put("botDisplayYn", "N");
							jsonObBody.put("quickStartStatus", 3);
				
							String apiUrl = "/aice/configManager/v1/companies/"+loginInfoVo.getLoginCompanyPk()+"/ais/"+quickAistaffSeq;		
							Map<String, Object> apiResult = Common.getRestDataApiPatch(configmanagerUrl+apiUrl, jsonObBody);
				
							//기존 봇으로 dnis 번호 할당
							if(apiResult.get("status").equals("success")) {
								try {
									if(dnisNum == null || dnisNum != "") {
										//할당된 번호가 있으면 API 호출!!
										JSONObject dnisJsonObBody = new JSONObject();
										String dnisApiUrl = "/aice/configManager/v1/companies/"+loginInfoVo.getLoginCompanyPk()+"/dnis/"+dnisNum;
										dnisJsonObBody.put("aiStaffFrom",quickAistaffSeq);
										dnisJsonObBody.put("aiStaffTo",mainAistaffSeq);
										Map<String, Object> apiDnisResult = Common.getRestDataApiPatch(configmanagerUrl+dnisApiUrl, dnisJsonObBody);
										
										//번호 할당을 완료 후 API 호출!! (main Bot) - 기존 봇 사용 함으로 변경
										if(apiDnisResult.get("status").equals("success") && mainAistaffSeq != null) {
											JSONObject jsonObMainBody = new JSONObject();
											jsonObMainBody.put("botDisplayYn", "Y");
											String apiMainBotUrl = "/aice/configManager/v1/companies/"+loginInfoVo.getLoginCompanyPk()+"/ais/"+mainAistaffSeq;
											Map<String, Object> apiMainResult = Common.getRestDataApiPatch(configmanagerUrl+apiMainBotUrl, jsonObMainBody);
										}
										
									} else {
										JSONObject jsonObMainBody = new JSONObject();
										jsonObMainBody.put("botDisplayYn", "Y");
										String apiMainBotUrl = "/aice/configManager/v1/companies/"+loginInfoVo.getLoginCompanyPk()+"/ais/"+mainAistaffSeq;
										Map<String, Object> apiMainResult = Common.getRestDataApiPatch(configmanagerUrl+apiMainBotUrl, jsonObMainBody);
										Map<String, Object> tempDnisResult = ploonetApiService.tempDnisRegist(loginInfoVo.getLoginUserType(), loginInfoVo.getLoginCompanyPk(), mainAistaffSeq, numberObj);
										if(tempDnisResult != null) {				
										}else {
											result = -1;
										}	
									}
									
									
									
									
								}catch (Exception e) {
									System.out.println("Exception" + e);
								}
								
								
							}
							
						}catch (Exception e) {
							System.out.println("Exception" + e);
						}
							
					}
					//4.16 회원 플루니언 수정 (brand api 호출)
	
					try {
						Map<String, Object> ploonianParamMap = new HashMap<>();
						ploonianParamMap.put("fkCompanyStaffAi", mainAistaffSeq);
						ploonianParamMap.put("fkCompany", loginInfoVo.getLoginCompanyPk());
						Map<String, Object> ploonianModify = staffService.ploonianModify(ploonianParamMap);		
						org.json.JSONObject apiJsonInfo = new org.json.JSONObject();
						//1.모델아이디
						apiJsonInfo.put("modelId", ploonianModify.get("fkAiPolicyAvatarImg").toString());
						//2.목소리아이디
						apiJsonInfo.put("voiceId", ploonianModify.get("voiceId").toString());
						//3.의상아이디
						apiJsonInfo.put("styleId", ploonianModify.get("pkAiPolicyAvatarImg").toString());
						Map<String, Object> apiResult = Common.getRestDataBrandApi(brandApi + "/api/if/v1/member/" +loginInfoVo.getUuid()+"/ploonian", apiJsonInfo, 0, brandApi, null, "");
					}catch (Exception e) {
						System.out.println("brand api error:" + e);
					}
					
					
				}else {
					//조이 봇은 ojt 완료 ( quickStartStatus = 3 )
					if(quickAi) {
						JSONObject jsonObBodyQuick = new JSONObject();
						jsonObBodyQuick.put("quickStartStatus", 3);
						String apiUrl = "/aice/configManager/v1/companies/"+loginInfoVo.getLoginCompanyPk()+"/ais/"+quickAistaffSeq;
						Map<String, Object> apiResult = Common.getRestDataApiPatch(configmanagerUrl+apiUrl, jsonObBodyQuick);
						//기간이 만료 되어 만료 된 후, 임시번호 할당 api 추가
						if(dnisNum == null || dnisNum == "") {
							Map<String, Object> tempDnisResult = ploonetApiService.tempDnisRegist(loginInfoVo.getLoginUserType(), loginInfoVo.getLoginCompanyPk(), quickAistaffSeq, numberObj);
							if(tempDnisResult != null) {				
							}else {
								result = -1;
							}
						}
						
						
					}else if(mainAi) {
						Map<String, Object> tempDnisResult = ploonetApiService.tempDnisRegist(loginInfoVo.getLoginUserType(), loginInfoVo.getLoginCompanyPk(), Long.parseLong(companyStaff), numberObj);
						if(tempDnisResult == null) result = -1; 
						
						if(tempDnisResult != null) {
							/*
							String fullDnis = numberObj.getString("number");
							String vgwId = numberObj.getString("vgwId");
							
							Map<String, Object> makeCallResult = ploonetApiService.makeCallAPi("makecall", fullDnis, loginInfoVo.getFdStaffPhone(), vgwId, "voice", "aiservice", "ko", "OJT_INTERVIEW", companyStaff);
							String makeCallStatus = makeCallResult.get("status").toString();
							String makeCallMsg = makeCallResult.get("messages").toString();
							if(!makeCallStatus.equals("200")) {
								result = -1;
							}
							 */
							
						}else {
							result = -1;
						}
												
					}
					
				}
				//-------------------------무료 시작하기 봇이 있을 시 번호 할당 추가-------------------------//
				
			}
		} catch (Exception e) {
			result = -1; 
		}
		return result;
	}
	
	@Override
	public int aiBotStatusComplate(String companyStaff) throws Exception {
		Map<String, Object> paramMap = new HashMap<>();
		
		
		int result = 0;
		try {
			
			PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
			paramMap.put("fkCompanyStaffAi", companyStaff);
			paramMap.put("fkCompany", loginInfoVo.getLoginCompanyPk());
			
			Map<String, Object> aiStatusParamMap = new HashMap<>();
			
			aiStatusParamMap.put("fkCompanyStaffAi", companyStaff);
			aiStatusParamMap.put("fkCompany", loginInfoVo.getLoginCompanyPk());
			aiStatusParamMap.put("frontStatus", "COMPLETE");
			
			DataMap companyDnisInfo = newOjtDao.getCompanyDnis(paramMap);
			if(companyDnisInfo != null) {
				String fullDnis = companyDnisInfo.get("fullDnis").toString();
				String dnis = companyDnisInfo.get("fdDnis").toString();
				paramMap = ploonetApiService.tempDnisDelete(fullDnis, dnis, loginInfoVo.getLoginCompanyPk(), Long.parseLong(companyStaff));
				result = 1;
			}
			
			result = newOjtDao.aiConfWorkStatusUpdate(aiStatusParamMap);
			
		} catch (Exception e) {
			result = -1; 
		}
		return result;
	}
	
	/*
	 * 톡봇 브로커 호출 AI상태 LEARNING 변경해야함
	 */
	@Override
	public void ojtCompleteCallBroker(long companyStaff, String type) throws Exception {
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		HttpResponse<JsonNode> response = null;
		Map<String, Object> paramMap = new HashMap<>();
		
		paramMap.put("fkCompanyStaffAi", companyStaff);
		paramMap.put("fkCompany", loginInfoVo.getLoginCompanyPk());
		paramMap.put("frontStatus", "LEARNING");
		
		newOjtDao.aiConfWorkStatusUpdate(paramMap);
		try {
			String url = "";
			try {
	           
	            JSONObject body = new JSONObject();
	            body.put("staffSeq", companyStaff);
	            body.put("companySeq", loginInfoVo.getLoginCompanyPk());
	            
	            url = talkbotBrokerUrl;
	            
	            response = Unirest.post(url)
                .header("Content-Type", "application/json")
                .body(body)
                .asJson();
	        } catch (UnirestException e) {
	            e.printStackTrace();
	        }

		} catch (Exception ex) {
			System.out.println("ex:" + ex);
			log.error("********** paramMap : {}", paramMap.toString());
			throwException.statusCode(500);
		}
	}
	
	@Override
	public Map<String, Object> ojtCompleteMakeCall(String companyStaff, String callPhone) throws Exception {
		Map<String, Object> paramMap = new HashMap<>();
		Map<String, Object> resultMap = new HashMap<>();
		
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		int result = 0;
		
		paramMap.put("fkCompanyStaffAi", companyStaff);
		paramMap.put("fkCompany", loginInfoVo.getLoginCompanyPk());
		paramMap.put("frontStatus", "TRAINING");
		
		newOjtDao.aiConfWorkStatusUpdate(paramMap); 
		

		
		DataMap companyDnisInfo = newOjtDao.getCompanyDnis(paramMap);
		if(companyDnisInfo != null) {
			String fullDnis = companyDnisInfo.get("fullDnis").toString();
			String dnis = companyDnisInfo.get("fdDnis").toString();
			String vgwId = companyDnisInfo.get("vgwId").toString();
			System.out.println(fullDnis);
			Map<String, Object> makeCallResult = ploonetApiService.makeCallAPi("makecall", fullDnis, callPhone, vgwId, "voice", "aiservice", "ko", "OJT_INTERVIEW", companyStaff);	
		}
		
		/*
		JSONArray numberArr = ploonetApiService.voiceNumbers(0);
		JSONObject numberObj = (JSONObject) numberArr.get(0);
		
		Map<String, Object> tempDnisResult = ploonetApiService.tempDnisRegist(loginInfoVo.getLoginUserType(), loginInfoVo.getLoginCompanyPk(), Long.parseLong(companyStaff), numberObj);
		System.out.println(tempDnisResult);
		
		String tempResultstatus = tempDnisResult.get("status").toString();
		
		if(tempResultstatus.equals("200")) {
		
			String fullDnis = numberObj.getString("number");
			String vgwId = numberObj.getString("vgwId");
			
			Map<String, Object> makeCallResult = ploonetApiService.makeCallAPi("makecall", "07045000004", callPhone, vgwId, "voice", "aiservice", "ko", "OJT_INTERVIEW", companyStaff);
			String makeCallStatus = makeCallResult.get("status").toString();
			if(!makeCallStatus.equals("200")) {
				result = -1;
				resultMap.put("messages", "연락처 필수값 없음!!!");
			}
		}
		*/
		resultMap.put("result", result);
		
		return resultMap;
	}
	
	@Override
	public Map<String, Object> staffDeptChange(long companyStaff, long changeCompanyStaff) throws Exception {
		Map<String, Object> resultMap = new HashMap<>();
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		
		DataMap aiconfWorkDt = null;
		Map<String, Object> paramMap = new HashMap<>();
		
		paramMap.put("fkCompany", loginInfoVo.getLoginCompanyPk());
		paramMap.put("fkCompanyStaffAi", companyStaff);
		
		aiconfWorkDt = newOjtDao.getAiConfWorkDt(paramMap);
		
		String aiWorkCd = Common.NVL(aiconfWorkDt.get("aiWorkCd"), null);
		String dispName = Common.NVL(aiconfWorkDt.get("dispName"), null);
		
		paramMap.put("dispName", dispName);
		
		DataMap companyDetp = newOjtDao.getCompanyDetp(paramMap);
		if(companyDetp != null) {
			
			
			long pkCompanyDept = Long.valueOf(String.valueOf(companyDetp.get("pkCompanyDept")));
			CompanyDeptStaffVo companyDeptStaffOld = new CompanyDeptStaffVo();
			CompanyDeptStaffVo companyDeptStaffNew = new CompanyDeptStaffVo();
			
			companyDeptStaffOld.setFk_company_staff(companyStaff);
			companyDeptStaffOld.setFk_company_dept(pkCompanyDept);
			companyDeptStaffOld.setFd_dept_master_yn("Y");
			
			companyDeptStaffNew.setFk_company_staff(changeCompanyStaff);
			companyDeptStaffNew.setFk_company_dept(pkCompanyDept);
			companyDeptStaffNew.setFd_dept_master_yn("Y");
			
			staffDeptService.updateDeptStaffNew( companyDeptStaffOld,  companyDeptStaffNew);
		}else {
			
			CompanyDeptVo companyDeptVo = new CompanyDeptVo();
			companyDeptVo.setFk_company(loginInfoVo.getLoginCompanyPk());
			
			companyDeptVo.setFd_dept_name(dispName);
            companyDeptVo.setFd_default_yn("N");
            int pkCompanyDept = joinDao.insertCompanyDept(companyDeptVo);
            
            CompanyDeptStaffVo companyDeptStaffOld = new CompanyDeptStaffVo();
			CompanyDeptStaffVo companyDeptStaffNew = new CompanyDeptStaffVo();
			
			companyDeptStaffOld.setFk_company_staff(companyStaff);
			companyDeptStaffOld.setFk_company_dept(companyDeptVo.getPk_company_dept());
			companyDeptStaffOld.setFd_dept_master_yn("Y");
			
			companyDeptStaffNew.setFk_company_staff(changeCompanyStaff);
			companyDeptStaffNew.setFk_company_dept(companyDeptVo.getPk_company_dept());
			companyDeptStaffNew.setFd_dept_master_yn("Y");
			
			staffDeptService.updateDeptStaffNew( companyDeptStaffOld,  companyDeptStaffNew);
		}
		
		resultMap.put("messages", "변경성공");
		
		return resultMap;
		
	}
	
	//초기 값 가져오기(업무 수정 부분)
	@Override
	public Map<String, Object> profileCard(Map<String, Object> paramMap) throws Exception {
		
		List<Object> listData = newOjtDao.profileCard(paramMap);
		List<Object> listDataInfo = newOjtDao.profileCardInfo(paramMap);
		DataMap listDataDefault = newOjtDao.profileCardDefault(paramMap);
		Map<String, Object> result          = new HashMap<>();
		result.put("Info",listDataInfo);
		result.put("list", listData);
		result.put("default", listDataDefault);
		return result;
	}
	
	@Override
	public List<Object> getAiConfWork(long pkCompany) throws Exception {
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("fkCompany", pkCompany);
		List<Object> result = newOjtDao.getAiConfWork(paramMap);

		return result;
	}
	
	
}