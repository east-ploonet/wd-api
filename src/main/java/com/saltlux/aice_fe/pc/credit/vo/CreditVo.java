package com.saltlux.aice_fe.pc.credit.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class CreditVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 6608693928583996284L;

    //tbl_service_plan

    private String pk_service_plan;

    private int disp_order;	//표시순서

    private String service_plan_name; // 플랜이름

    private int credit_free;			// 기본제공프레딧

    private int credit_main;			

    private int charge_amount;

    private int discount_amount;

    private int available_credit;

    private String best_yn;

    private String my_plan;
 
    private List<CreditItemResVo> creditItemVoList = new ArrayList<>();

}
