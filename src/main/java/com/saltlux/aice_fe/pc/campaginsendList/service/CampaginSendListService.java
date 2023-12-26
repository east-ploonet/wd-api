package com.saltlux.aice_fe.pc.campaginsendList.service;

import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.saltlux.aice_fe.pc.campaginsendList.vo.CampaginSendInfoVo;
import com.saltlux.aice_fe.pc.campaginsendList.vo.CampaginSendListInfoVo;
import com.saltlux.aice_fe.pc.campaginsendList.vo.CampaginSendListInfoVo2;
import com.saltlux.aice_fe.pc.issue.dto.CompanyStaffExcel;
import com.saltlux.aice_fe.pc.issue.vo.CompanyCustomerVo;

public interface CampaginSendListService {
	
	//엑셀 폼 다운로드
	//Resource downloadCustomerCompanyTemplate(String nameTemplate, CompanyStaffExcel companyStaffExcel);
	
	Map<String, Object> infoRegist(Map<String, Object> paramMap) throws Exception;
	
	Map<String, Object> sendListUpdate(Map<String, Object> paramMap) throws Exception;
	
	Map<String, Object> getSendList(Map<String, Object> paramMap) throws Exception;
	
	CampaginSendListInfoVo infoRegistManagement(CampaginSendListInfoVo sendListVo) throws Exception;

	CampaginSendInfoVo infoRegistList(CampaginSendInfoVo sendInfoVo) throws Exception;
	
	CampaginSendListInfoVo infoUpdateManagement(CampaginSendListInfoVo sendListVo) throws Exception;
	
	CampaginSendListInfoVo2 saveInfoUpdateManagement(CampaginSendListInfoVo2 sendListVo2) throws Exception;	
	
	//삭제
	CampaginSendListInfoVo infoDeleteManagement(CampaginSendListInfoVo sendListVo) throws Exception;

	CampaginSendInfoVo infoUpdateList(CampaginSendInfoVo sendInfoVo) throws Exception;
	
	
	//전체 삭제
	void sendListDelete(Map<String, Object> paramMap) throws Exception;
	//엑셀 다운로드
	Resource downloadTemplate(String nameTemplate);
	
	//연락처 추가 직접입력
	Map<String, Object> saveCompanyCustomer(Map<String, Object> paramMap) throws Exception;
	
	//고객리스트 호출
	Map<String, Object> getCustomerList() throws Exception;
}
