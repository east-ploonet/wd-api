package com.saltlux.aice_fe.pc.credit.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;


@EqualsAndHashCode(callSuper = true)
@Data
public class CreditItemResVo extends BaseVo implements Serializable {

    //tbl_service_plan_item

    private String fk_service_plan;

    private String screen_cd;

    private String display_title;

    private String display_name;

    private int disp_order;





}
