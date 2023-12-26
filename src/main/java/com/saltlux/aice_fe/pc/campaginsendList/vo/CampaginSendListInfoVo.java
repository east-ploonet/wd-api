package com.saltlux.aice_fe.pc.campaginsendList.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class CampaginSendListInfoVo extends BaseVo implements Serializable {

	private static final long serialVersionUID = -403032330483720780L;

    long fk_company;							// 회사pk
    String fk_send_info ; 						// 발신리스트(fk)
    long fk_company_customer;					// 고객 fk
    long fk_writer ; 							// [직원]등록자 fk
    long fk_modifier ; 							// [직원]수정자 fk
    Date fd_regdate;
    Date fd_moddate;

    
    
}
