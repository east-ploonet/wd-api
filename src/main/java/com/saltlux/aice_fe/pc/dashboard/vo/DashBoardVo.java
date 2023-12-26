package com.saltlux.aice_fe.pc.dashboard.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import com.saltlux.aice_fe.pc.issue.dto.IssueTicketDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class DashBoardVo extends BaseVo implements Serializable {

	private static final long serialVersionUID = 3823572499980988785L;

	private long    pk_issue_ticket;            // 티켓 pk
	private String  fd_ticket_workflow_code;    // 티켓 워크플로우 코드
	private String  fd_ticket_status_code;      // 티켓 상태 코드
	private String  fd_ticket_title;            // 티켓 제목
	private long    fk_issue_dialogue_start;    // 티켓 생성 대화 fk
	private long    fk_assign_staff;            // 담당직원 fk
	private String  fd_ticket_owner_uid;        // 티켓 처리 주체 uid
	private Date    fd_ticket_limit_date;       // 티켓 만료일시
	private long    fk_writer;                  // [직원]등록자 fk
	private Date    fd_regdate;                 // [직원]등록일시
	private long    fk_modifier;                // [직원]수정자 fk
	private Date    fd_moddate;                 // [직원]수정일시

	//-- Extend Fields --//
	private String  pk_issue_contact;           // 컨텍 pk
	private String  fd_contact_status_code;     // 컨텍 상태 코드
	private String  fd_contact_channel_code;    // 컨텍 채널 코드

	private long    pk_issue_dialogue;          // 대화 pk
	private long    fk_company_staff;           // 대화 - 직원 fk
	private String  fd_customer_uid;            // 대화 - 고객 인식 uid
	private String  fd_message;                 // 대화 - 내용

	private String  fd_customer_name;           // 대화 고객 이름

	private int     dialogue_read_n_cnt;        // 읽음 여부N 갯수
	private String  fk_issue_contact_min;       // 컨텍 fk (min)
	private String  customer_ani;

	private String  workflow;                   // 대기/진행/종료 상태
	private String  issue_ticket_tags;          // 태그 리스트(구분자 ',' )
	private String 	fk_priority;
	private String 	fd_comment;
	private List<Long> fkAssignStaffIds;
	private String fd_assign_staff_name;
	private String ai_name;
	private List<String> priorities;
	private String pk_code_channel;

	public DashBoardVo() {}

	public DashBoardVo(long fk_assign_staff) {
		this.fk_assign_staff = fk_assign_staff;
	}

	public DashBoardVo(IssueTicketDTO issueTicketDTO) {
//		issueTicketDTO.setFdDueDate(new Date(issueTicketDTO.getDueDateLongTime()));
		this.pk_issue_ticket = issueTicketDTO.getPkIssueTicket();
		this.fd_ticket_workflow_code = issueTicketDTO.getWorkFlowCode();
		if (issueTicketDTO.getDueDateLongTime() != 0) {
			this.fd_ticket_limit_date = new Date(issueTicketDTO.getDueDateLongTime());
		}
		this.fk_assign_staff = issueTicketDTO.getFk_assign_staff();
		this.fd_comment = issueTicketDTO.getFdComment();
		this.fk_priority = issueTicketDTO.getFk_priority();
	}
}
