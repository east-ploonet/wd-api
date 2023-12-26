package com.saltlux.aice_fe.pc.my_page.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class PgBillVo  extends BaseVo implements Serializable {

    private static final long serialVersionUID = -5888726235192449757L;

//    private Long pk_pg_bill_disp_cost;
//
//    private int disp_order;
//
//    private int disp_cost;
    private String pp_card_cd;
    
    private String pp_card_name;
    
    private int disp_order;
    
    private int cost;
    
    private int credit;
}
