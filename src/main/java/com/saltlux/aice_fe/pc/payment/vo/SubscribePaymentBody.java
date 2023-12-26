package com.saltlux.aice_fe.pc.payment.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class SubscribePaymentBody extends BaseVo implements Serializable {

	private Long fk_company;
	private String pay_name;
    private String pay_method;
    private String pay_company_cd;
    private String pay_company_name;
    private String pay_key;
    private String tran_cd;
    private String pg_err_cd;
    private String pg_err_desc;
	


}
