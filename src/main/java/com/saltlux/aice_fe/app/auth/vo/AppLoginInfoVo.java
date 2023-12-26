package com.saltlux.aice_fe.app.auth.vo;

import lombok.Data;

import java.io.Serializable;


@Data
public class AppLoginInfoVo implements Serializable {

	private static final long serialVersionUID = -742467676703792427L;

	private long    loginCompanyPk;         // 회사 pk
	private String  loginCompanyName;       // 회사 이름
	private String  loginCompanyId;         // 회사 ID(이메일 형식, @ploonet.com)
	private String  loginCompanyLogoUrl;    // 회사 로고 웹경로

	private long    loginCompanyStaffPk;    // 직원 pk
	private String  loginCompanyStaffId;    // 직원 id
	private String  loginCompanyStaffName;  // 직원 이름

	private String  loginCompanyStaffDept;  // 부서 이름
	private String  loginLevelCode;
	private String  loginRoleName;
}
