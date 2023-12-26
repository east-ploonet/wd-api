package com.saltlux.aice_fe.pc.auth.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;



@Data
public class PcLoginInfoVo implements Serializable {

	private static final long serialVersionUID = 6251926713279786981L;

	private long    loginCompanyPk;         // 회사 pk
	private String  loginCompanyName;       // 회사 이름
	private String  loginCompanyId;         // 회사 ID(이메일 형식, @ploonet.com)
	private String  loginCompanyLogoUrl;    // 회사 로고 웹경로

	private long    loginCompanyStaffPk;    // 직원 pk
	private String  loginCompanyStaffId;    // 직원 id
	private String  loginCompanyStaffName;  // 직원 이름
    private String 	loginUserType;			// 유저 타입

	private String  loginCompanyStaffDept;  // 부서 이름
	private String  loginLevelCode;
	private String  loginRoleName;
	private String  fdStaffId;
	private String  authToken;
	private String	ssoIdToken;				// sso id token
	private String  uuid;
	
	private String  fdCompanyMasterYn; //
	private String  fdStaffMobile;
	private String  fdStaffPhone;
	
	private Date    loginDate;           // 로그인 일시
	private String  loginStaffMobile;
	private String  loginStaffMobileType;
	private String  loginStaffBirth;
	private String  loginStaffGender;
	
}
