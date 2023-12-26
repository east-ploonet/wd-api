package com.saltlux.aice_fe.pc.my_page.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class MyPlanNewVo  extends BaseVo implements Serializable {

    private static final long serialVersionUID = -1896817072039009351L;

    private int fk_pp_card;
    private int fk_pp_card_p;
    private int fk_company;
    private String pp_card_cd;
    private String pp_card_type;
    private String pp_card_name;
    private String pp_card_status;
    private int credit;
    private int credit_from;
    private int credit_policy;
    private int cost;
    private int disp_order;
    private String subsc_yn;

}
