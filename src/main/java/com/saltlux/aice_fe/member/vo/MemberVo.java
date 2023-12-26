package com.saltlux.aice_fe.member.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class MemberVo extends BaseVo implements Serializable {

	private static final long serialVersionUID = 7904633071621052050L;

	//-- Table Fields --//
	//
	private int    pk_member;               // 회원 pk
	private String fd_member_id;            // 회원 id
	private String fd_member_pw;            // 회원 pw
	private String fd_member_name;          // 회원 이름
	private String fd_member_mobile;        // 회원 휴대전화 번호
	private String fd_member_email;         // 회원 email
	private String fd_member_status_code;   // 회원 상태 코드
	private String fd_member_status_date;   // 회원 상태 변경 일시
	private String fd_signup_date;          // 회원 가입 일시
	private String fd_update_date;          // 최근 수정 일시
	private String fd_login_date;           // 최근 로그인 일시
	//
	//-- Table Fields --//


	//-- Extend Fields --//
	//
	//-- Extend Fields --//
}
