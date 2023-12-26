package com.saltlux.aice_fe.pc.issue.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class IssueContactVo extends BaseVo implements Serializable {

	private static final long serialVersionUID = -49262785586128127L;

	private String  pk_issue_contact;           // 컨텍 pk
	private long    fk_issue_ticket;            // 티켓 fk
	private long    fk_company;                 // 회사 fk
	private String  fd_contact_status_code;     // 컨텍 상태 코드
	private String  fd_contact_channel_code;    // 컨텍 채널 코드
	private String  fd_customer_uid;            // 고객 인식 uid
	private String  fd_ai_call_type;            // AI 콜 타입
	private String  fd_role_type;               // role_type
	private String  fd_record_url;              // record_url
	private long    fk_writer;                  // [직원]등록자 fk
	private Date    fd_regdate;                 // [직원]등록일시
	private long    fk_modifier;                // [직원]수정자 fk
	private Date    fd_moddate;                 // [직원]수정일시
}
