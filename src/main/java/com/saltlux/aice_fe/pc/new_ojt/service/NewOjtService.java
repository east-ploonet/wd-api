package com.saltlux.aice_fe.pc.new_ojt.service;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.saltlux.aice_fe._baseline.baseVo.DataMap;

public interface NewOjtService {
	
	// OJT1
	//회사 정보
	Map<String, Object> getInfo(String pkCompanyStaff) throws Exception;
	//회사 정보 저장
	void companyUpdate(Map<String, Object> paramMap) throws Exception;
	//회사 업무시간 저장
	void companyTimeUpdate(Map<String, Object> paramMap) throws Exception;
	void tempDnisDelete(Map<String, Object> paramMap) throws Exception;
	
	
	//회사 ai 업무시간 저장
	void aiTimeUpdate(Map<String, Object> paramMap, long pkCompanyStaff) throws Exception;
	
	int botStatusCount(String pkCompanyStaff) throws Exception;
	
	int aiBotStatusComplate(String pkCompanyStaff) throws Exception;
	
	//회사 인사말 저장
	void companyIntroUpdate(Map<String, Object> paramMap, JSONArray msgBodyCheckList, long companyStaff) throws Exception;
	
	// 첫인사말 / 긴급안내멘트
	DataMap getIntro(String pkCompanyStaff, String gubun) throws Exception;
	
	// 업무 시간
	Map<String, Object> getOfficeTimeList(Map<String, Object> paramMap) throws Exception;
	
	// 업무 시간(채용 완료시)
	Map<String, Object> getTimeOrientation(Map<String, Object> paramMap) throws Exception;
	
	
	// 회사 업무 시간
	Map<String, Object> getCompanyTimeList(String pkCompanyStaff) throws Exception;
	
	
	// ai 정보 업데이트
	void aiStaffStatusUpdate(long companyStaff, String fdStaffStatusCode) throws Exception;
	
	
	//OJT2 
	//Step 리스트 가져오기 (신규등록)
	Map<String, Object> getStepList(Map<String, Object> paramMap) throws Exception;
	//OJT2 Step 저장
	void ojt2StepRegist(Map<String, Object> paramMap) throws Exception;
	
	//Step 리스트 가져오기 (저장 된 리스트 불러오기)
	Map<String, Object> getStepAll(Map<String, Object> paramMap) throws Exception;
	
	Map<String, Object> stepMenuBotStatusCnt(String pkCompanyStaff) throws Exception;

	//OJT 완료 톡봇 브로커 호출
	void ojtCompleteCallBroker(long companyStaff, String type) throws Exception;
	
	//직무 교육 완료 makeCall API 호출
	Map<String, Object> ojtCompleteMakeCall(String companyStaff, String callPhone) throws Exception;
	
	Map<String, Object> staffDeptChange(long companyStaff, long changeCompanyStaff) throws Exception;
	
	//초기 값 가져오기(업무 수정 부분)
	Map<String, Object> profileCard(Map<String, Object> paramMap) throws Exception;
	
	List<Object> getAiConfWork(long pkCompany) throws Exception;
	
}
