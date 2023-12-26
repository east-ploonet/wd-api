package com.saltlux.aice_fe.pc.credit.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class MyNewCreditItemVo   extends BaseVo implements Serializable {

    private static final long serialVersionUID = -8427880533717260255L;


    private String pp_card_cd;

    private String pp_card_name; //구독 플랜
    
    private String pp_card_type;
    
    private int credit_policy; // 잔여금액 -> 문의

    private int cost; //구독요금

    private int credit; // 가용금액 
    
    private LocalDateTime fd_regdate; //계약일


}
