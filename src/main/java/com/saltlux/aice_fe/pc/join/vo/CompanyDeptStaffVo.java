package com.saltlux.aice_fe.pc.join.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class CompanyDeptStaffVo extends BaseVo implements Serializable {

	private static final long serialVersionUID = 8003556405691228726L;

	private long   fk_company_dept;        // 회사 부서 fk
	private long   fk_company_staff;       // 직원 fk
	private String fd_dept_master_yn;      // 대표 담당자 여부 YN
	private Date   fd_regdate;             // 등록일시

	//-- Extend Fields --//
	private List<Long> pk_list;            // pk 리스트

}
