package com.saltlux.aice_fe.app.main.service;

import com.saltlux.aice_fe.pc.issue.vo.IssueTicketVo;
import com.saltlux.aice_fe.pc.join.vo.CompanyStaffVo;

import java.util.Map;

public interface AppMainService {

	Map<String, Object> appMainInfo() throws Exception ;
	Map<String, Object> getMyBizState() throws Exception ;
	Map<String, Object> setMyBizState(CompanyStaffVo reqVo) throws Exception ;
	CompanyStaffVo getMyPage() throws Exception ;
	Map<String, Object> myWorkflowCnt(IssueTicketVo reqVo) throws Exception ;
	void setStaffPushNotiYn(CompanyStaffVo reqVo) throws Exception ;
}
