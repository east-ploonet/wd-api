package com.saltlux.aice_fe.pc.issue.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class CompanyCustomerVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -403032330483720780L;

    private long pk_company_customer;    // 고객 pk
    private long fk_company;             // 회사 fk
    private String fd_customer_name;       // 고객 이름
    private String fd_active_state;       // 고객 이름
    private String fd_company_name;       // 고객 이름
    private String fd_company_dept;        // 소속 부서
    private String fd_company_position;    // 소속 직위
    private String fd_customer_mobile;     // 고객 휴대전화
    private String fd_customer_mobile1;     // 고객 휴대전화(010)
    private String fd_customer_mobile2;     // 고객 휴대전화(가운데 3~4자리)
    private String fd_customer_mobile3;     // 고객 휴대전화(끝3~4자리)
    private String fd_customer_phone;      // 고객 일반전화
    private String fd_customer_email;      // 고객 이메일
    private String fd_customer_kakao_uid;  // 고객 카톡 uid
    private String fd_customer_chat_uid;   // 고객 chat uid
    private long fk_writer;              // [직원] 등록자 fk
    private Date fd_regdate;             // [직원] 등록일시
    private long fk_modifier;            // [직원] 수정자 fk
    private Date fd_moddate;             // [직원] 수정일시
    private Date contact_date_from;
    private String contact_channel_from;
    private String fd_additional_information;
    private String fd_customer_address_common;
    private String fd_customer_address_detail;
    private String fd_company_address_common;
    private String fd_company_address_detail;

    //-- Extend Fields --//
    private String loginCompanyLogoUrl;    // 회사 로고 웹경로
    private String fd_customer_uid;        // 상담 고객 uid
    private String fd_ticket_workflow_code;// 상담 티켓 상태코드
    private String fd_contact_channel_code;// 컨텍 채널 코드
    private String fd_contact_channel_name;// 컨텍 채널 이름
    private String fd_company_phone;

    private String fd_dept_name;
    private String fd_state_name;
    private String registerer;
    private String registererDeptName;
    private String updater;
    private String updaterDeptName;

    private List<String> statusesSearch;
    private String contactDateFromSearch;
    private String regDateSearch;
}
