package com.saltlux.aice_fe.pc.issue.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class IssueDialogueDetailVo extends BaseVo implements Serializable {

	private static final long serialVersionUID = -4914186335837174896L;

	private long    pk_issue_ticket;                // 티켓 pk
	private String  fd_ticket_workflow_code;        // 티켓 워크플로우 코드
	private String  ticket_workflow_name;           // 티켓 워크플로우 코드 이름
	private String  fd_contact_channel_code_list;   // 컨텍 채널 코드 목록(구문자 ',')
	private String  fd_contact_channel_name_list;   // 컨텍 채널 이름 목록(구문자 ',')
	private String  channel_name;                   // 컨택 채널 코드 이름
	private int     contact_cnt;                    // 컨택 수
	private int     dialogue_cnt;                   // 대화 수
	private String  fd_customer_name;               // 고객 이름
	private String  fd_staff_name;                  // 담당 직원 이름
	private String  fd_dept_name;                   // 담당 직원 부서
}
