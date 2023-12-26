package com.saltlux.aice_fe.pc.send.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class ExcelListVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -403032330483720780L;

    String fdCustomerName;	//이름
    String fdCustomerMobile;    	//전화번호
    String fdCustomerEmail;	//이메일
}
