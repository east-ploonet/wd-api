package com.saltlux.aice_fe.pc.payment.service;

import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.credit.vo.PgPayLog;
import com.saltlux.aice_fe.pc.payment.response.PaymentCancelResponseVo;
import com.saltlux.aice_fe.pc.payment.response.PaymentSubscribeResponseVo;
import com.saltlux.aice_fe.pc.payment.vo.ChargeVo;
import com.saltlux.aice_fe.pc.payment.vo.SubscribeBody;
import com.saltlux.aice_fe.pc.payment.vo.SubscribeBodyNew;

import java.util.Map;

public interface PaymentService {
    //결제 승인 요청
    Map<String,Object> requestApplyPayment(ChargeVo req, PcLoginInfoVo pcLoginInfoVo);


    //결제 취소 요청
    PaymentCancelResponseVo requestCancelPayment(String tno);


    //결제 취소 요청
    PaymentCancelResponseVo requestSubscribeCancelPayment(String tno);


    //구독 배치키 요청
    PaymentSubscribeResponseVo requestNewSubscribe(SubscribeBody req, PcLoginInfoVo pcLoginInfoVo);

    void requestSubscribe(SubscribeBody req, PcLoginInfoVo pcLoginInfoVo);

    Map<String, Object> getOrderIdx();

    Map<String, Object> getSuccessPay(SubscribeBody req, PcLoginInfoVo pcLoginInfoVo);
    
    Map<String, Object> getSuccessPayNew(SubscribeBodyNew req, PcLoginInfoVo pcLoginInfoVo);

    Map<String, Object> getAuthOrderIdx();

    void requestEntrySubScribe(SubscribeBody req, PcLoginInfoVo pcLoginInfoVo);
    
    PaymentCancelResponseVo requestCancelPaymentSTPC(String tno, String modMny, String remMny, long loginCompanyPk, long loginCompanyStaffPk, String item_cd);
}
