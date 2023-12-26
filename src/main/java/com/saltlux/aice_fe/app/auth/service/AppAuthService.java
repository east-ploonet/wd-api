package com.saltlux.aice_fe.app.auth.service;

import com.saltlux.aice_fe._baseline.baseVo.ResponseVo;
import com.saltlux.aice_fe.app.auth.vo.AppLoginInfoVo;
import com.saltlux.aice_fe.member.vo.MemberVo;
import com.saltlux.aice_fe.pc.join.vo.CompanyStaffVo;

import java.util.Map;

public interface AppAuthService {

	ResponseVo  login(CompanyStaffVo companyStaffVo) throws Exception ;
	Map<String, Object> appVersion() throws Exception ;
	AppLoginInfoVo getUserLoginById(long pk_company_staff) throws Exception;
}
