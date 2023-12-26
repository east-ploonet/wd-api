package com.saltlux.aice_fe.pc.payment.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class SubscribeBodyNew  extends BaseVo implements Serializable {

    private static final long serialVersionUID = -1419797030343214156L;

    private Long pk_pg_pay_log;
    private int log_yy;
    private int log_mm;
    private int log_dd;
    private Long fk_pg_pay_info;
    private String pay_name;
    private String pay_method;
    private String pay_company_cd;
    private String pay_company_name;
    private String pay_key;
    private String tran_cd;
    private Long pk_company;
    private String item_cd;
    private int sum_cost;
    
}
