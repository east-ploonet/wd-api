package com.saltlux.aice_fe.pc.join.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class CompanyStaffVo extends BaseVo implements Serializable {

	private static final long serialVersionUID = -8820731162255958205L;

    private long   pk_company_staff;                // 직원 pk222
	private long   fk_staff_work;                   // 담당 직무 fk
	private long   fk_company;                      // 회사 fk
	private String fd_company_master_yn;            // 회사 대표직원 여부YN
	private String fd_staff_id;                     // 회사 id
	private String fd_staff_pw;                     // 회사 pw[단방향]
	private String fd_signup_keycode;               // 직원 가입 초대코드
	private String fd_signup_keycode_ok_yn;         // 초대코드 컨펌 여부 YN
	private Date   fd_signup_keycode_date;          // 초대코드 발급일시
	private String fd_staff_ai_yn;                  // AI직원 여부YN
	private String fd_staff_ai_uid;                 // AI직원 캐릭터 uid
	private String fd_dnis;                         // 콜 인입번호
	private String fd_staff_level_code;             // 직원 구분 코드
	private String fd_name;
	private String fd_staff_status_code;            // 직원 계정 상태 코드
	private String fd_staff_name;                   // 직원 이름
	private String fd_staff_mobile;                 // 직원 휴대전화
	private String fd_staff_mobile_type;            // 직원 휴대전화 통신사
	private String fd_staff_phone;                  // 직원 일반전화
	private String fd_staff_email;                  // 직원 이메일
	private String fd_staff_duty;                   // 직원 직급
	private String fd_staff_birth;                  // 직원 생년월일
	private String fd_staff_gender_mf;              // 직원 성별 MF
	private String fd_staff_national_yn;            // 내국인 여부YN
	private String fd_address_zipcode;
	private String fd_address_common;
	private String fd_address_detail;
	private String fd_staff_di;                     // 직원 DI
	private String fd_staff_response_status_code;   // 직원 응답 상태 코드
	private String fd_push_noti_yn;                 // 푸시알림 수신 여부 YN
	private String fd_push_token;                   // 푸시 토큰
	private Date   fd_login_date;                   // 로그인 일시
    private String user_type;
	private long   fk_writer;                       // [직원] 등록자 fk
	private Date   fd_regdate;                      // [직원] 등록일시
	private long   fk_modifier;                     // [직원] 수정자 fk
	private Date   fd_moddate;                      // [직원] 수정일시
	private String   uuid;                      

	//-- Extend Fields --//
	private long   pk_company_dept;                 // 부서 pk
	private String dept_disp_name;                    // 부서명
	private String fd_dept_name;                    // 부서명
	private String fd_writer_name;                  // 등록자 이름
	private String fd_writer_dept_name;             // 등록자 부서
	private String fd_modifier_name;                // 수정자 이름
	private String fd_modifier_dept_name;           // 수정자 부서
	private String fd_staff_level;                  // 직원 구분 (사용 권한)
	private String fd_staff_status;                 // 직원 계정 상태 (고용 상태)
	private String fd_staff_response_status;        // 직원 응답 상태 (담당자 상태)
	private String fd_dept_master_yn;               // 대표 담당자 여부 YN
	private String role_name;

	private long    pk_company;                     // 회사 pk
	private String  fd_company_name;                // 회사 이름
	private String  fd_company_id;                  // 회사 ID(이메일 형식, @ploonet.com)
	private String  fd_company_logo_file_path;      // 회사 로고 웹경로
	private String  fd_company_master_staff_name;	// 회사 대표 담당자 이름

	private List<Long> pk_list;                     // pk 리스트

	private String  fd_app_version;                 // app OS종류
	private String  fd_os_code;                     // app 버전

	private String fd_staff_ci; //직원 CI
	private String fd_staff_persona; // AI직원 목소리 설정
	private String fk_staff_work_code; //AI직원 업무코드

	private String fd_default_ai; // 디폴트 AI직원 (대표 AI직원)
	private String fd_state_code; // 직원 활성화 상태
	private String fd_staff_name_icon;
	public CompanyStaffVo(long pk_company_staff) {
		this.pk_company_staff = pk_company_staff;
	}
	
	private String solution_type;
	private int is_change_password;
    //안내데스크 상태값
    private String fd_sign_up_path_code;			// 담당자 가입 경로

}
