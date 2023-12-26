package com.saltlux.aice_fe.pc.issue.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class IssueDialogueVo extends BaseVo implements Serializable {

	private static final long serialVersionUID = -5615702617039476962L;

	private long    pk_issue_dialogue;          // 대화 pk
	private String  fk_issue_contact;           // 컨텍 fk
	private long    fk_company_staff;           // 직원 fk
	private String  fd_customer_uid;            // 고객 인식 uid
//	private String  fd_dnis;                    // 콜 인입번호
	private String  fd_call_broker_id;          // 콜 브로커 id
//	private String  fd_bot_id;                  // 봇 id
	private String  fd_dialogue_status_code;    // 대화 상태 코드
	private String  fd_message;                 // 대화 내용
	private Date    fd_regdate;                 // 등록일시
	private String  fd_dialogue_message_code;
	private String  fd_read_yn;                 // 읽음 여부YN
	private long    fk_company_customer;

	//-- Extend Fields --//
	private String  fd_contact_channel_code;    // 컨텍 채널 코드
	private String  fd_customer_name;           // 대화 고객 이름
	private String  fd_staff_ai_uid;            // 소속회사의 AI uid
}
