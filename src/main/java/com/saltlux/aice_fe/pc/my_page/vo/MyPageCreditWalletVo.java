package com.saltlux.aice_fe.pc.my_page.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class MyPageCreditWalletVo  extends BaseVo implements Serializable {

    private static final long serialVersionUID = 499231410409245607L;

    private Long pk_credit_wallet_log;

    private String type;

    private String fd_name;

    private String service_plan_status;

    private String disp_group_cd;

    private LocalDate limit_dt_from;

    private LocalDate limit_dt_to;
    
//    private LocalDate service_plan_dt_from;
//    
//    private LocalDate service_plan_dt_to;

    private LocalDateTime fd_regdate;

    private String memo;

    private String item_name;

    private int credit;

    private String use_flag;





}
