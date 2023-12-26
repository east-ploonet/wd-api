package com.saltlux.aice_fe.pc.my_page.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class MyPlanVo  extends BaseVo implements Serializable {

    private static final long serialVersionUID = -1896817072039009351L;

    private String fk_service_plan;

    private String fk_service_plan_dc;

    private String item_name;

    private LocalDateTime service_dt_from;

    private LocalDateTime service_dt_to;

    private int charge_amount;

    private int credit_free;

    private int credit_main;

    private int dc_rate;

    private String charge_unit;

    private int charge_term;

}
