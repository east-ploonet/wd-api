package com.saltlux.aice_fe.pc.credit.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class CreditNewVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 6608693928583996284L;

    //tbl_service_plan
    private String pp_card_cd;
    
    private String pp_card_type;
    
    private String pp_card_name;
    
    private int credit;
    
    private int cost;
    
    private int disp_order;
    
    private int deduct_order;
    
    private String default_yn;
    
    private String minus_yn;
    
    private String disp_yn;
    
    private String pay_yn;
    
    private String cust_yn;
    
    private String subsc_yn;
    
    private String subsc_term_unit;
    
    private int subsc_term_cnt;
    
    private String dc_term_unit;
    
    private int dc_term_cnt;
    
    private String dc_rate;
    
    private int pre_use_limit_rate;
    
    private String limit_term_unit;
    
    private int limit_term_cnt;
    
    private String limit_dt_same_yn_from;
    
    private String limit_dt_same_yn_to;
    
    private String use_yn;
    
    private List<CreditItemResVo> creditItemVoList = new ArrayList<>();

}
