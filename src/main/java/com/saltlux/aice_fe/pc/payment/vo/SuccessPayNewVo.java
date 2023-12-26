package com.saltlux.aice_fe.pc.payment.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class SuccessPayNewVo extends BaseVo implements Serializable {


    private static final long serialVersionUID = -8293056901226058277L;

    private int pk_pp_card; 
    private String pp_card_name; 
    private int cost; 
    private int credit;
    private LocalDateTime pay_dt_from; 
    private LocalDateTime pay_dt_to; 

}
