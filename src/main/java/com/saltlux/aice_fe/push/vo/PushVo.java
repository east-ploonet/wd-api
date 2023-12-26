package com.saltlux.aice_fe.push.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class PushVo extends BaseVo implements Serializable {

	private static final long serialVersionUID = 5866897761379280419L;
	
	//-- Table Fields --//
	//
	private long    pk_company_staff_push;  // 푸시 메시지 pk
	private long    fk_company_staff;       // 직원 fk
	private long    fk_issue_ticket;        // 티켓 fk
	private String  fd_push_code;           // 푸시 구분 코드
	private String  fd_title;               // 메시지 제목
	private String  fd_message;             // 메시지 내용
	private String  fd_send_result;         // 발송결과[문자열]
	private Date    fd_send_date;           // 발송일시
	private Date    fd_receive_date;        // 수신일시
	//
	//-- Table Fields --//


	//-- Extend Fields --//
	//
	private String  fd_send_date_str;    	// 발송일시[문자열](1시간 전 or mm-dd)
	//
	//-- Extend Fields --//
}
