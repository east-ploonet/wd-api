package com.saltlux.aice_fe.pc.payment.response;

import lombok.Data;

@Data
public class PaymentSubscribeApplyResponseVo {
    String res_cd;//결과 코드
    String res_msg;//결과 메시지
    String res_en_msg;//결과 영어 메시지
    String tno;// 결제번호
    String amount; //결제금액
    String trace_no;//NHN KCP 거래 추적번호
    String card_cd;
    String card_name;

}
