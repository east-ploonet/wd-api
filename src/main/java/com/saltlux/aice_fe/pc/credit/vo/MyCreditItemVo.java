package com.saltlux.aice_fe.pc.credit.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class MyCreditItemVo   extends BaseVo implements Serializable {

    private static final long serialVersionUID = -8427880533717260255L;

    private LocalDateTime service_dt_from;		//시작일
    
    private LocalDateTime service_dt_to; 			//종료일
    
    private String fk_service_plan;

    private String fk_service_plan_dc;

    private String service_plan_name;

    private int available_credit;

    private int residuum_credit;					//사용 크래딧

    private int dc_rate;

    private int credit_free;

    private int credit_main;
    
    private int charge_amount;
    
    private int discount_amount;



}
