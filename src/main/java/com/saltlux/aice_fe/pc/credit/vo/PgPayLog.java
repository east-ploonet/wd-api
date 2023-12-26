package com.saltlux.aice_fe.pc.credit.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class PgPayLog extends BaseVo implements Serializable {

    private static final long serialVersionUID = 6608693928583996284L;
    
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
    private String pg_err_cd;
    private String pg_err_desc;
    private Long fk_company;
    private String item_cd;
    private Long item_cost;
    private int item_cnt;
    private int month_cnt;
    private Long sum_cost;
    private LocalDateTime pay_dt_from;	
    private LocalDateTime limit_dt_from;	
    private LocalDateTime limit_dt_to;	
    private String repeat_yn;
    private String bill_status;
    private Long payback_cost;
    private String acc_media;
    private String acc_ip;
    private String memo;
    
    
}
