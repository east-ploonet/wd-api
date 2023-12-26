package com.saltlux.aice_fe.pc.payment.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class SuccessPayVo extends BaseVo implements Serializable {


    private static final long serialVersionUID = -8293056901226058277L;

    private LocalDateTime service_dt_from;

    private String service_plan_name;

    private int charge_amount;

    private int dc_rate;

    private String charge_unit;
}
