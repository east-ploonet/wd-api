package com.saltlux.aice_fe.pc.payment.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class SubscribeBody  extends BaseVo implements Serializable {

    private static final long serialVersionUID = -1419797030343214156L;

    private String order_idxx;

    private String fk_service_plan;

    private String service_plan_name;

    private String fk_service_plan_dc;

    private int charge_term;

    private String user_type;

    private Long pk_company;

    private Long pk_company_staff;

    private String enc_info;

    private String enc_data;

    private String tracn_cd;

    private LocalDateTime service_dt_to;

    private LocalDateTime service_dt_from;

    private String order_no;

    private String order_method;

    private String order_company_cd;

    private String order_company_name;

    private int sum_money;

}
