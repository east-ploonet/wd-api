package com.saltlux.aice_fe.pc.my_page.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class MyPagePayLogVo  extends BaseVo implements Serializable {

    //private Long pk_pg_order_item;
	private Long pk_pg_pay_log;
	
    //private String fd_name;
	private String item_cd;
	
    //private String fd_repeat_type_name;
	private int sum_cost;
	
    //private String item_name;
	private String tran_cd;
	
    //private int sum_money;
	private String pay_company_name;
	
    //private String order_no;
	private String pay_company_cd;
	
    //private String order_method;
	private LocalDateTime fd_regdate;

    //private String order_company_cd;
	private String bill_status;

    //private String order_company_name;
	private String fd_repeat_type_name;
	
	private String pay_method;
	
	private String pp_card_name;

    //private LocalDateTime fd_regdate;

    //private String bill_status;


}
