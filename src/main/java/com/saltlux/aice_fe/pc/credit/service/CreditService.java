package com.saltlux.aice_fe.pc.credit.service;

import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.credit.vo.MyNewCreditItemVo;
import com.saltlux.aice_fe.pc.credit.vo.PgPayInfo;
import com.saltlux.aice_fe.pc.credit.vo.PgPayLog;
import com.saltlux.aice_fe.pc.user.vo.Request.UserReqBody;
import com.saltlux.aice_fe.pc.payment.response.PaymentApplyResponseVo;
import com.saltlux.aice_fe.pc.payment.response.PaymentSubscribeApplyResponseVo;
import com.saltlux.aice_fe.pc.payment.vo.PgInfoVo;
import com.saltlux.aice_fe.pc.payment.vo.SubscribeBody;
import com.saltlux.aice_fe.pc.payment.vo.SubscribeBodyNew;
import com.saltlux.aice_fe.pc.payment.vo.SubscribePaymentBody;
import com.saltlux.aice_fe.pc.payment.vo.SuccessPayNewVo;
import com.saltlux.aice_fe.pc.payment.vo.SuccessPayVo;

import java.util.Map;

public interface CreditService {
    Map<String, Object> getPlan(String charge_unit,int charge_term);
    //Map<String, Object> getPlanNew(String charge_unit,int charge_term);
    
    void registerEntry(UserReqBody reqBody, long pkPgPayLog);
   
    Map<String, Object> getBalance(PcLoginInfoVo pcLoginInfoVo, String fk_service_plan, int charge_term, String charge_unit);

    SubscribePaymentBody getBatchKey(long loginCompanyPk);

    PgPayInfo getPgPayInfo(long loginCompanyPk);
    
    void pgPayInfoLogInsert(long loginCompanyPk);
    
    PgPayLog getLastPgPayLog(long loginCompanyPk);
    
    long selectRegisterPgPayLog(long loginCompanyPk, String pk_pp_card);
    
    long registerPgPayLog(PaymentApplyResponseVo paymentApplyResponseVo, long loginCompanyPk, long loginCompanyStaffPk, String billStatus, String tran_cd, MyNewCreditItemVo mySelectPlanList);
    
    long registerPgPayLogSub(PaymentSubscribeApplyResponseVo paymentSubscribeApplyResponseVo, long loginCompanyPk, long loginCompanyStaffPk, String billStatus, String tran_cd, MyNewCreditItemVo mySelectPlanList, String payKey);
    
    long registerCancelPgPayLog(Map<String,Object> paramMap);
    
    void registerCard(PgInfoVo pgInfoVo);

    void updateCard(PgInfoVo pgInfoVo, long loginCompanyPk);

    int myplan(long loginCompanyPk);

    void newPayProsess(SubscribeBody req, PcLoginInfoVo pcLoginInfoVo, String beforeMyPlanName, String planName);
    
    void ppcardMerge(PcLoginInfoVo pcLoginInfoVo, String pk_pp_card);
    
    
    void upgrade(SubscribeBody req, PcLoginInfoVo pcLoginInfoVo);

    void downgrade(SubscribeBody req, PcLoginInfoVo pcLoginInfoVo);

    Map<String, Object> getMyCard(PcLoginInfoVo pcLoginInfoVo);

    Map<String, Object> getPayPage(String fk_service_plan, String fk_service_plan_dc, PcLoginInfoVo pcLoginInfoVo);

    SuccessPayVo getSuccessPay(SubscribeBody req);
    
    SuccessPayNewVo getSuccessPayNew(SubscribeBodyNew req);

    void chargeCredit(PcLoginInfoVo pcLoginInfoVo, PaymentApplyResponseVo paymentApplyResponseVo,String order_method);

    int getPlanMoney(String fk_service_plan, String fk_service_plan_dc);
}
