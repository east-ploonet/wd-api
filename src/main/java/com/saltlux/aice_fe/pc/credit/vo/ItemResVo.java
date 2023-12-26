package com.saltlux.aice_fe.pc.credit.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;


@EqualsAndHashCode(callSuper = true)
@Data
public class ItemResVo extends BaseVo implements Serializable {

    //tbl_service_plan_item

    private String disp_name;

    private String disp_val;

    private String pp_card_cd;
    
    private String pp_card_name;
	
//    private String DISP_VAL;			//엔트리
//    		
//    private String DISP_VAL_1;			//베이직 
//    
//    private String DISP_VAL_2;			//프로페셔널
//    
//    private String DISP_VAL_3;			//비지니스
//    
//    private String DISP_VAL_4;			//엔터프라이즈

}
