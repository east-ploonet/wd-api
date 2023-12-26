package com.saltlux.aice_fe.member.service;

import com.saltlux.aice_fe.member.vo.MemberVo;

import java.util.Map;

public interface MemberService {

	void                insertMember(Map<String, Object> paramMap) throws Exception;
	MemberVo            getMember(Map<String, Object> paramMap) throws Exception;
	Map<String, Object> memberIdDupCheck(Map<String, Object> paramMap) throws Exception;
	Map<String, Object> selectMemberList(Map<String, Object> paramMap) throws Exception;
	MemberVo            updateMember(Map<String, Object> paramMap) throws Exception;
	void                dropMember(Map<String, Object> paramMap) throws Exception;
	void                deleteMember(Map<String, Object> paramMap) throws Exception;
}
