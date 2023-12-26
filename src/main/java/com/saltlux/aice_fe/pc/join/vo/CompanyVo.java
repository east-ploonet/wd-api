package com.saltlux.aice_fe.pc.join.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class CompanyVo extends BaseVo implements Serializable {

	private static final long serialVersionUID = -8820731162255958205L;

	private long   pk_company;                  // 회사 pk
	private String fd_company_id;               // 회사 id
	private String fd_company_status_code;      // 회원 계정 상태 코드
	private String fd_company_name;             // 회사명
	private String fd_biz_license_num;          // 사업자 번호
	private String fd_company_phone;            // 대표전화
	private String fd_company_fax;              // 팩스번호
	private String fd_company_website;          // 홈페이지 url
	private String fd_address_zipcode;          // 주소 우편번호
	private String fd_address_common;           // 주소 기본
	private String fd_address_detail;           // 주소 상세
	private String fd_company_logo_file_path;   // 회사 로고 파일 경로
	private String fd_company_logo_file_name;   // 회사 로고 파일 이름
	private String fd_biz_license_file_path;    // 사업자등록증 파일 경로
	private String fd_biz_license_file_name;    // 사업자등록증 파일 이름
	private long   fk_writer;                   // [직원] 등록자 fk
	private Date   fd_regdate;                  // [직원] 등록일시
	private long   fk_modifier;                 // [직원] 수정자 fk
	private Date   fd_modate;                   // [직원] 수정일시
}
