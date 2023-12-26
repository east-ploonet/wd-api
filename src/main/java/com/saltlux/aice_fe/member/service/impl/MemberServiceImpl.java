package com.saltlux.aice_fe.member.service.impl;

import com.saltlux.aice_fe._baseline.baseService.impl.BaseServiceImpl;
import com.saltlux.aice_fe.member.dao.MemberDao;
import com.saltlux.aice_fe.member.service.MemberService;
import com.saltlux.aice_fe.member.vo.MemberVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class MemberServiceImpl extends BaseServiceImpl implements MemberService {
	
	@Autowired
	private MemberDao memberDao;

	//회원가입
	@Override
	public void insertMember(Map<String, Object> paramMap) throws Exception {

		Map<String, Object> result = new HashMap<>();
		BCryptPasswordEncoder pwEncoder = new BCryptPasswordEncoder();

		MemberVo tmpMemberVo = new MemberVo();

		tmpMemberVo.setFd_member_id(    paramMap.get("memberId").toString());       // 회원 id

		// 회원 pw (단방향 암호화)
		if( !"".equals(paramMap.get("memberPw"))){

			tmpMemberVo.setFd_member_pw( BCRYPT_ENCODER.encode(paramMap.get("memberPw").toString()) );
		}

		tmpMemberVo.setFd_member_name(  paramMap.get("memberName").toString());     // 회원 이름
		tmpMemberVo.setFd_member_mobile(paramMap.get("memberMobile").toString());   // 회원 휴대전화 번호
		tmpMemberVo.setFd_member_email( paramMap.get("memberEmail").toString());    // 회원 이메일
		tmpMemberVo.setFd_member_status_code( "1001" );    // 회원 이메일

		//DB 처리
		try {
			int pk_member = memberDao.insertMember(tmpMemberVo);

		} catch (DuplicateKeyException dupEx) {
			throwException.statusCode(409);

		} catch (Exception ex) {
			log.error("********** tmpMemberVo : {}", tmpMemberVo.toString());
			throwException.statusCode(500);
		}
	}

	//사용자 목록
	@Override
	public Map<String, Object> selectMemberList(Map<String, Object> paramMap) throws Exception {

		int totalCnt                = memberDao.selectMemberCount(paramMap);
		List<MemberVo> memberVoList = memberDao.selectMemberList(paramMap);

		Map<String, Object> result  = new HashMap<>();
		result.put("totalCnt"   , totalCnt);
		result.put("list"       , memberVoList);

		return result;
	}

	//사용자 상세보기
	@Override
	public MemberVo getMember(Map<String, Object> paramMap) throws Exception {

		MemberVo reqMemberVo= new MemberVo();
		// 숫자형과 문자열 자료를 동시에 수용할 수 있도록 변환 처리 함
		reqMemberVo.setPk_member(  Integer.parseInt( paramMap.get("memberPk").toString() ) );

		MemberVo getMemberVo = memberDao.getMember(reqMemberVo);

		if(getMemberVo == null){
			throwException.statusCode(204);
		}

		return memberDao.getMember(getMemberVo);
	}

	//사용자 아이디 중복확인
	@Override
	public Map<String, Object> memberIdDupCheck(Map<String, Object> paramMap) throws Exception {

		Map<String, Object> result = new HashMap<>();

		MemberVo tmpMemberVo = new MemberVo();
		tmpMemberVo.setFd_member_id(    paramMap.get("memberId").toString());

		try {
			MemberVo getMemberVo = memberDao.getMemberById(tmpMemberVo);

			result.put("memberId"       , paramMap.get("memberId").toString());

			if(getMemberVo == null){
				result.put("acceptYN"    , "Y");

			}else{
				result.put("acceptYN"    , "N");
			}

		} catch (Exception ex) {
			log.error("********** tmpMemberVo : {}", tmpMemberVo.toString());
			throwException.statusCode(500);
		}

		return result;
	}

	//사용자 수정 처리
	@Override
	public MemberVo updateMember(Map<String, Object> paramMap) throws Exception {

		Map<String, Object> result      = new HashMap<>();
		BCryptPasswordEncoder pwEncoder = new BCryptPasswordEncoder();

		MemberVo tmpMemberVo = new MemberVo();

		// 회원 pw (단방향 암호화)
		if( !"".equals(paramMap.get("memberPw"))){
			tmpMemberVo.setFd_member_pw(    pwEncoder.encode( paramMap.get("memberPw").toString() ));
		}
		tmpMemberVo.setFd_member_mobile(    paramMap.get("memberMobile").toString());
		tmpMemberVo.setFd_member_email(     paramMap.get("memberEmail").toString());
		tmpMemberVo.setPk_member(           Integer.parseInt((String) paramMap.get("memberPk")));

		//DB 처리
		try {
			int resultCnt = memberDao.updateMember(tmpMemberVo);

			if(resultCnt == 0){
				throwException.statusCode(204);
			}

		} catch (Exception ex) {
			log.error("********** tmpMemberVo : {}", tmpMemberVo.toString());
			throwException.statusCode(500);
		}

		return tmpMemberVo;
	}

	//사용자 탈퇴 처리
	@Override
	public void dropMember(Map<String, Object> paramMap) throws Exception {

		MemberVo tmpMemberVo = new MemberVo();
		tmpMemberVo.setPk_member( Integer.parseInt( paramMap.get("memberPk").toString() ));

		int resultCnt = memberDao.dropMember(tmpMemberVo);

		if(resultCnt == 0){
			throwException.statusCode(204);
		}
	}

	//사용자 삭제 처리
	@Override
	public void deleteMember(Map<String, Object> paramMap) throws Exception {

		MemberVo tmpMemberVo = new MemberVo();
		tmpMemberVo.setPk_member(Integer.parseInt((String) paramMap.get("memberPk")));

		int resultCnt = memberDao.deleteMember(tmpMemberVo);

		if(resultCnt == 0){
			throwException.statusCode(204);
		}
	}

}
