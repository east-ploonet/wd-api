package com.saltlux.aice_fe.pc.payment.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class PgInfoVo   extends BaseVo implements Serializable {
	private static final long serialVersionUID = -8615816563442345232L;
    private Long pk_pg_info; //PG결제정보 pk
    private String solution_type; /// 솔루션 타입
    private String user_type; // 유저 타입
    private Long fk_company; // 회사 pk
    private Long fk_company_staff; // 소유자 id
    private String pay_name; // 결제자 정보 이름
    private String pay_method;// 결제 타입
    private String pay_company_cd;//결제코드
    private String pay_company_name; // 결제사 이름(카드사이름)
    private String pay_key;
    private String tran_cd;
}
