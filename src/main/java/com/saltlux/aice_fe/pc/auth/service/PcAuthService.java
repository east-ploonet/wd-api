package com.saltlux.aice_fe.pc.auth.service;

import java.util.Map;

import com.saltlux.aice_fe._baseline.baseVo.ResponseVo;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.join.vo.CompanyStaffVo;

public interface PcAuthService {
	
	// 토큰로그인 추가
	ResponseVo  tokenLogin(CompanyStaffVo reqCompanyStaffVo) throws Exception ;

	ResponseVo  login(CompanyStaffVo companyStaffVo, boolean isSns) throws Exception ;
	ResponseVo  login(CompanyStaffVo companyStaffVo, String authToken, String uuid, String ssoIdToken, boolean isSns) throws Exception ;
    ResponseVo  kakaoLogin(CompanyStaffVo companyStaffVo) throws Exception ;
    ResponseVo  loginByTest(CompanyStaffVo companyStaffVo) throws Exception ;
	Map<String, Object> resetCompanyStaffTicketCode(CompanyStaffVo companyStaffVo) throws Exception ;
	Map<String, Object> resetCompanyStaffTicketIndividualCode(CompanyStaffVo companyStaffVo) throws Exception ;
	
	Map<String, Object> updateAgree(String fkCompanyStaff, String agreeValue, String fkCompany) throws Exception ;
	

	PcLoginInfoVo getUserLoginById(long pk_company_staff) throws Exception;

}
