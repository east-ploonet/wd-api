package com.saltlux.aice_fe.pc.staff.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class AIStaffVo extends BaseVo implements Serializable {

	private static final long serialVersionUID = -8610696236629043239L;

	private long   pk_staff_work;          // 담당 직무 pk
	private String fd_staff_work_name;     // 직무명
	private long   fk_writer;              // [직원] 등록자 fk
	private Date   fd_regdate;             // [직원] 등록일시
	private long   fk_modifier;            // [직원] 수정자 fk
	private Date   fd_modate;              // [직원] 수정일시

}
