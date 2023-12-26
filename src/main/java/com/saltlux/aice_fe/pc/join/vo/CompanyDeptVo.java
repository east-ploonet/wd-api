package com.saltlux.aice_fe.pc.join.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class CompanyDeptVo extends BaseVo implements Serializable {

	private static final long serialVersionUID = 9190212038615178088L;

    private long   pk_company_dept;         // 회사 부서 pk
	private long   fk_company;              // 회사 fk
	private String fd_dept_name; 			// 부서명
	private String fd_dept_code; 			// 부서코드
	private String fd_dept_role;            // 부서업무 [구분자 ',']
	private String fd_use_yn;               // 사용여부
	private String fd_default_yn;           // 기본 부서 여부YN
	private String fd_dept_ai_yn;           // AI 부서 여부YN
	private long   fk_writer;               // [직원] 등록자 fk
	private Date   fd_regdate;              // [직원] 등록일시
	private long   fk_modifier;             // [직원] 수정자 fk
	private Date   fd_modate;               // [직원] 수정일시

	//-- --//

	private String master_staff_name;		// 대표 담당자명
	private String fk_staff_work_code;

}
