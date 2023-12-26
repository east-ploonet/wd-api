package com.saltlux.aice_fe.pc.payment.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;

@EqualsAndHashCode(callSuper = true)
@Data
public class ChargeVo  extends BaseVo implements Serializable {

    private static final long serialVersionUID = -1223922885344179910L;

    private String tran_cd;

    private int good_mny;

    private String enc_data;

    private String enc_info;

    private String ret_pay_method;
    
    private ArrayList creditTopupItem;
}
