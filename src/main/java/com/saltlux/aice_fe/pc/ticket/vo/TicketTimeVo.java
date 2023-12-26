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
public class TicketTimeVo extends BaseVo implements Serializable {

	private static final long serialVersionUID = 3823572499980988785L;
	
	
	long pk_conf_noti_ticket_close;
	long fk_company;
	Date noti_time;
	int close_day_before;
	int close_day_repeat;
//	String close_noti_msg;
	int unread_day_after;
	int unread_day_repeat;
//	String unread_noti_msg;
	int master_day_after;
	long fk_writer;
//	Date fd_regdate;
	long fk_modifier;
//	Date fd_moddate;
	
	
}
