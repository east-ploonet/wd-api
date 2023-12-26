package com.saltlux.aice_fe.pc.credit.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;


@EqualsAndHashCode(callSuper = true)
@Data
public class CreditItemVo extends BaseVo implements Serializable {

    //tbl_service_plan_item

    private Long pk_credit_item_cnt;

    private String screen_cd;

    private String fk_service_plan;

    private int disp_order;

    private String disp_name;

    private String disp_unit_name;

    private String item_cnt;


}
