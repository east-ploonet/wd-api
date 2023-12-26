package com.saltlux.aice_fe.pc.join.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class TermsVo extends BaseVo implements Serializable {

	private static final long serialVersionUID = 1116054512745219743L;

	private long   pk_terms;                 // 약관 pk
	private String fd_terms_target_code;     // 약관 적용대상 코드
	private String fd_terms_code;            // 약관 종류 코드
	private String fd_terms_title;           // 약관 제목
	private String fd_terms_contents;        // 약관 내용
	private String fd_terms_mandatory_yn;    // 약관 동의 필수 여부 YN
	private Date   fd_terms_open_date;       // 약관 게시 일시
	private String fd_open_yn;               // 게시 여부 YN
	private int disp_order;
	private Date   fd_regdate;               // 등록일시
	private Date   fd_moddate;               // 수정일시

}
