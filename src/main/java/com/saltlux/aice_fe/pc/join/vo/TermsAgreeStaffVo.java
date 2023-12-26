package com.saltlux.aice_fe.pc.join.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class TermsAgreeStaffVo extends BaseVo implements Serializable {

	private static final long serialVersionUID = 8239866427082322235L;

	private long   pk_terms_agree_staff;       // 약관동의[직원] pk
	private long   fk_terms;                   // 약관 fk (FK)
	private long   fk_company_staff;           // 직원 fk (FK)
	private String fd_agree_yn;                // 동의여부 YN
	private Date   fd_agree_date;              // 동의일시
	private String fd_retract_yn;              // 취소여부 YN
	private Date   fd_retract_date;            // 최소일시
}
