package com.saltlux.aice_fe.pc.payment.response;

import lombok.Data;

@Data
public class PaymentApplyResponseVo {
    String res_cd;//결과 코드
    String res_msg;//결과 메시지
    String res_en_msg;//결과 영어 메시지
    String tno;// 결제번호
    String amount; //결제금액


    ///카드 결제시 응답
    String card_cd; //결제 건의 발급 사 코드
    String card_name; //결제 건의 발급 사 명
    String card_no; //결제 건의 카드번호 (카드번호 16자리 중 3번째구간은 마스킹)
    String app_no; //결제 건의 승인번호
    String app_time; //결제 건의 결제(승인) 시간
    String noinf; //결제 건의 무이자 여부
    String noinf_type;  //noinf = Y 일 때 (무이자 결제인 경우)
                        // 카드사 이벤트 무이자인 경우 : CARD
                        // 상점 부담 무이자인 경우 : SHOP
    String quota; //결제 건의 할부 기간
    String card_mny; //결제 건의 총 결제금액 중 신용카드 결제금액
                     //만약 총 결제금액(amount) 10000 원 중 쿠폰할인 2000 원 받았다면 card_mny=8000 이 됩니다.
                     //*페이코 포인트, 쿠폰 100% 결제 시 card_mny=0 으로 리턴될 수 있으니 반드시 총 결제 금액 처리는 amount 금액으로 체크하시기 바랍니다.
    String coupon_mny; //결제 건의 무이자 여부결제 건의 쿠폰 할인, 페이코 포인트 사용 금액
                       //결제 건의 쿠폰 할인 금액 또는 페이코 포인트 사용 금액이 리턴됩니다.
                       //만약 총 결제금액(amount) 10000 원 중 쿠폰 할인을 2000 원 받았다면 coupon_mny=2000 이 됩니다.
    String partcanc_yn; //결제 건의 부분취소 가능 유무
    String card_bin_type_01; //결제 건의 카드 구분 정보 (개인 : 0 / 법인 : 1)
    String card_bin_type_02; //결제 건의 카드 구분 정보(일반 : 0 / 체크 : 1)
    String isp_issuer_cd; //ISP 계열 카드 발급 사 코드 BC96 : 케이뱅크카드 KM90 : 카카오뱅크카드
    String isp_issuer_nm; //ISP 계열 카드 발급 사 명 카카오뱅크의 경우 카카오뱅크 케이뱅크의 경우 K뱅크카드로 리턴됨.
    String payco_point_mny; //결제 건의 페이코 포인트 사용 금액


//    //계좌이체 결제 응답
//    String bankname; //결제 건의 은행 명 ※ 테스트서버에서 계좌이체 테스트 시 금융결제원과 협의된 은행 코드로 리턴됩니다.
//    String bankcode; //결제 건의 은행코드 ※ 테스트서버에서 계좌이체 테스트 시 금융결제원과 협의된 은행 코드로 리턴됩니다.
//    String cash_authno; //현금영수증 승인번호 NHN KCP 결제 창에서 현금영수증 등록 요청한 결제 건의 현금영수증 승인번호
//    String cash_no; //NHN KCP 결제 창에서 현금영수증 등록 요청한 결제 건의 현금영수증 거래번호
//    String bk_mny; //결제 건의 계좌이체 결제 금액
//    String app_time; //결제건의 계좌발급 시간
}
