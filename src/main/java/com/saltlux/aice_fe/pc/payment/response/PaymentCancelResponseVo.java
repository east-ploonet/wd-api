package com.saltlux.aice_fe.pc.payment.response;

import lombok.Data;

@Data
public class PaymentCancelResponseVo {
    String res_cd;//결과코드
    String res_msg;//결과메시지
    String tno;//NHN KCP 거래 고유번호
    String canc_time;//취소시간
    String mod_mny;//부분취소일 경우 부분취소금액
    String rem_mny;//부분취소일 경우 남은 원거래 금액
    String mod_pacn_seq_no;//부분취소 일련번호
    String card_mod_mny;//카드취소금액(부분취소일 경우),부분취소금액 중 카드취소 금액
    String coupon_mod_mny;//쿠폰취소금액(부분취소일 경우),부분취소금액 중 쿠폰취소 금액
}
