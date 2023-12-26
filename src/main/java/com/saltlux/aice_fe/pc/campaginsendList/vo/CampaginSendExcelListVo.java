package com.saltlux.aice_fe.pc.campaginsendList.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class CampaginSendExcelListVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -403032330483720780L;

    String fdCustomerName ; 						// 고객이름
    String fdCustomerPhone;									// 고객 일반전화 (company 고객관리 등록)
    String fdCustomerEmail;							// 고객 이메일
    String fdCustomerMobile;								// 고객 휴대전화
    String fdCompanyDept;							// 소속회사
    
}
