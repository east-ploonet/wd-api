package com.saltlux.aice_fe.pc.credit.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class ServiceDcVo   extends BaseVo implements Serializable {

    private static final long serialVersionUID = -6964189260220015697L;

    private String pk_service_plan_dc;
    private String pk_service_plan;

    private int dc_rate;
    private String charge_unit;
    private int charge_term;
    private String disp_yn;
    private String dc_name;
}
