package com.saltlux.aice_fe.pc.my_page.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class MyPlanResponseVo  extends BaseVo implements Serializable {

    private static final long serialVersionUID = -2856769548941109160L;

    private String fk_service_plan;

    private String item_name;

    private boolean checkNext;

    private String next_item_name;

    private LocalDateTime service_dt_from;

    private LocalDateTime next_service_dt_from;

    private int charge_amount;

    private int next_charge_amount;

    private String pay;

    private String charge_unit;

    private int Available_credit;
}
