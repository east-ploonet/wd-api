package com.saltlux.aice_fe.pc.join.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class CompanyDeptTagVo extends BaseVo implements Serializable {
    
    private static final long serialVersionUID = 9190212038615178088L;    

    private long fk_company_dept; // 회사 부서 pk
    private long fk_company; // 회사 fk
    
    private String fd_dept_tag; // 부서 검색 키워드
    private long fk_writer; // [직원] 등록자 fk
    private Date fd_regdate; // [직원] 등록일시
    private long fk_modifier; // [직원] 수정자 fk
    private Date fd_modate; // [직원] 수정일시
}
