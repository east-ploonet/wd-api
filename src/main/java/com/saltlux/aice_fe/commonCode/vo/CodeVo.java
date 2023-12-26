package com.saltlux.aice_fe.commonCode.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class CodeVo extends BaseVo implements Serializable {

	private static final long serialVersionUID = 6246339253950924452L;

private String  pk_code;        // 코드 pk
private String  fk_up_code;     // 상위 코드 fk
private String  fd_name;        // 코드 이름
private String  fd_name_en;
private String  fd_use_yn;      // 사용여부 YN
private String  fd_sort_num;    // 정렬 순서
private String  fd_memo;        // 코드 설명


private long    pk_company_staff;   //직원 pk

	public CodeVo(String fkUpCode) {
		this.fk_up_code = fkUpCode;
	}
}
