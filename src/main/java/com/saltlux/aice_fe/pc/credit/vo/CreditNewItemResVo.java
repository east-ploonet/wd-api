package com.saltlux.aice_fe.pc.credit.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Data
public class CreditNewItemResVo extends BaseVo implements Serializable {

    //tbl_service_plan_item

    private String pp_card_name;
    
    private int cost;
    
    private int credit;
    
    private String My_plan;
    
    private List<CreditItemResVo> creditNewItemVoList = new ArrayList<>();

}
