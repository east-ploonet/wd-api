package com.saltlux.aice_fe.pc.ticket.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import com.saltlux.aice_fe.pc.issue.dto.IssueTicketDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

@EqualsAndHashCode(callSuper = true)
@Data
public class AlarmSetVo extends BaseVo implements Serializable{
	
	private static final long serialVersionUID = 3947008974322065815L;
	
	long pk_ai_conf_noti;
	long fk_company;
	long fk_company_staff_ai;
	String check_type;
	String sms_yn;
	String kakao_yn;
	String push_yn;
	String msg_body;
	String use_yn;
	long fk_writer;
	Date fd_regdate;
	long fk_modifier;
	Date fd_moddate;
}