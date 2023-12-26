package com.saltlux.aice_fe.pc.payment.response;

import lombok.Data;

@Data
public class PaymentSubscribeResponseVo {
    String res_cd;//결과코드
    String res_msg;//결과 메시지
    String batch_key;//이 키를 가맹점 측에 저장(DB 또는 파일로 저장)해두었다가, 자동결제 요청에 이용할 수 있습니다.
    String card_cd;// 배치키 발급 시 요청한 카드사 코드
    String card_name; //배치키 발급 시 요청한 카드사명
    String tno; // NHN KCP 거래번호
}

