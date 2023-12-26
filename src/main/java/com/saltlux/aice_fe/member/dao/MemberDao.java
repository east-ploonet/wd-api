package com.saltlux.aice_fe.member.dao;

import com.saltlux.aice_fe.member.vo.MemberVo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface MemberDao {

    int             insertMember(MemberVo memberVo);
	MemberVo        getMember(MemberVo memberVo);
	MemberVo        getMemberById(MemberVo memberVo);
	List <MemberVo> selectMemberList(Map<String, Object> param);
	int             selectMemberCount(Map<String, Object> param);
	int             updateMember(MemberVo memberVo);
	int             updatePw(MemberVo memberVo);
	int             dropMember(MemberVo memberVo);
	int             deleteMember(MemberVo memberVo);
}
