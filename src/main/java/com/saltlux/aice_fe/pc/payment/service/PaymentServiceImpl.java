package com.saltlux.aice_fe.pc.payment.service;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.saltlux.aice_fe._baseline.config.BadRequestException;
import com.saltlux.aice_fe._baseline.exception.ThrowException;
import com.saltlux.aice_fe.ecApi.service.PloonetApiService;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.credit.dao.CreditDao;
import com.saltlux.aice_fe.pc.credit.service.CreditService;
import com.saltlux.aice_fe.pc.credit.vo.MyNewCreditItemVo;
import com.saltlux.aice_fe.pc.credit.vo.PgPayInfo;
import com.saltlux.aice_fe.pc.credit.vo.PgPayLog;
import com.saltlux.aice_fe.pc.payment.PaymentUtils;
import com.saltlux.aice_fe.pc.payment.response.PaymentApplyResponseVo;
import com.saltlux.aice_fe.pc.payment.response.PaymentCancelResponseVo;
import com.saltlux.aice_fe.pc.payment.response.PaymentSubscribeApplyResponseVo;
import com.saltlux.aice_fe.pc.payment.response.PaymentSubscribeResponseVo;
import com.saltlux.aice_fe.pc.payment.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private CreditService creditService;
    
    @Autowired
    private PloonetApiService ploonetApiService;    

    @Value("${payment.url.apply}")
    String applyUrl;

    @Value("${payment.url.cancel}")
    String cancelUrl;

    @Value("${payment.url.subscribe.batchkey}")
    String subscribeBatchUrl;

    @Value("${payment.url.subscribe.apply}")
    String subscribeApplyUrl;

    @Value("${payment.sitecd}")
    String siteCd;

    @Value("${payment.certification}")
    String certification;

    @Value("${payment.private.certification}")
    String privateCertification;

    @Value("${payment.private.password}")
    String privateKeyPassword;


    @Value("${payment.subscribe.private.certification}")
    String subscribePrivateCertification;


    @Value("${payment.subscribe.certification}")
    String subscribeCertification;


    @Value("${payment.subscribe.private.password}")
    String subscribePrivateKeyPassword;

    @Value("${payment.subscribe.sitecd}")
    String subscribeSiteCd;

    @Value("${payment.subscribe.kcpgroupid}")
    String kcpGroupId;

    @Autowired
    private ThrowException throwException;
    @Autowired
    private CreditDao creditDao;
    

    @Override
    public Map<String, Object> requestApplyPayment(ChargeVo req, PcLoginInfoVo pcLoginInfoVo) {
        Map<String, Object> resultMap = new HashMap<>();
        HttpResponse<String> response = null;
        try {
            JSONObject body = new JSONObject();
            body.put("tran_cd", req.getTran_cd());
            body.put("kcp_cert_info", req.getGood_mny());
            body.put("kcp_cert_info", certification);
            body.put("site_cd", siteCd);
            body.put("enc_data", req.getEnc_data());
            body.put("enc_info", req.getEnc_info());
            response = Unirest.post(applyUrl)
                    .body(body)
                    .asString();
            PaymentApplyResponseVo paymentApplyResponseVo = new Gson().fromJson(response.getBody(), PaymentApplyResponseVo.class);
            
            MyNewCreditItemVo mySelectPlanList = creditDao.getSelectPlan((String) req.getCreditTopupItem().get(3));
            
            if (paymentApplyResponseVo.getRes_cd().equals("0000")) {
                //todo 결제 성공
                try {
//                	public long registerPgPayLog(PaymentApplyResponseVo paymentApplyResponseVo, long loginCompanyPk, long loginCompanyStaffPk, String billStatus)
                	long pkPgPayLog = creditService.registerPgPayLog(paymentApplyResponseVo,
                			pcLoginInfoVo.getLoginCompanyPk(),
                			pcLoginInfoVo.getLoginCompanyStaffPk(),
                			"B20111",
                			req.getTran_cd(),
                			mySelectPlanList
                			);
                	//최초 플랜가입
                    ploonetApiService.fristSendBill(mySelectPlanList, pcLoginInfoVo.getLoginCompanyPk(), pkPgPayLog);
                    
                    //creditService.chargeCredit(pcLoginInfoVo, paymentApplyResponseVo, req.getRet_pay_method());
                    resultMap.put("code", "200");
                    resultMap.put("message", "success");
                } catch (BadRequestException e) {
                    requestCancelPayment(paymentApplyResponseVo.getTno());
                    throw new BadRequestException(paymentApplyResponseVo.getRes_msg());
                }
            } else {
                //todo 결제 실패
                throw new BadRequestException(paymentApplyResponseVo.getRes_msg());
            }

        } catch (Exception e) {
            resultMap.put("code", "400");
            resultMap.put("message", e.getMessage());
        }
        return resultMap;
    }

    @Override
    public PaymentCancelResponseVo requestCancelPayment(String tno) {
        HttpResponse<String> response = null;
        try {
            JSONObject body = new JSONObject();
            String mod_type = "STSC";//전체 승인취소 - STSC / 부분취소 - STPC
            String signKeyString = siteCd + "^" + tno + "^" + mod_type;

            body.put("site_cd", siteCd);
            body.put("tno", tno);
            body.put("kcp_cert_info", certification);
            body.put("kcp_sign_data", PaymentUtils.sha256WithRSA(signKeyString, privateCertification, privateKeyPassword));
            body.put("mod_type", mod_type);
//            body .put("mod_mny","STSC");//부분취소일 경우 부분취소금액
//            body .put("rem_mny","STSC");//부분취소일 경우 남은 원거래 금액
            body.put("mod_desc", "취소");
            response = Unirest.post(cancelUrl)
                    .body(body)
                    .asString();
            PaymentCancelResponseVo paymentApplyResponseVo = new Gson().fromJson(response.getBody(), PaymentCancelResponseVo.class);
            if (paymentApplyResponseVo.getRes_cd().equals("0000")) {
                //todo 결제 취소 성공

            } else {
                //todo 배치키 발급 실패
            }
            return paymentApplyResponseVo;
        } catch (Exception e) {

            return null;
        }
//        return null;
    }

    @Override
    public PaymentCancelResponseVo requestSubscribeCancelPayment(String tno) {
        HttpResponse<String> response = null;
        try {
            JSONObject body = new JSONObject();
            String mod_type = "STSC";//전체 승인취소 - STSC / 부분취소 - STPC
            String signKeyString = subscribeSiteCd + "^" + tno + "^" + mod_type;

            body.put("site_cd", subscribeSiteCd);
            body.put("tno", tno);
            body.put("kcp_cert_info", subscribeCertification);
            body.put("kcp_sign_data", PaymentUtils.sha256WithRSA(signKeyString, subscribePrivateCertification, subscribePrivateKeyPassword));
            body.put("mod_type", mod_type);
//            body .put("mod_mny","STSC");//부분취소일 경우 부분취소금액
//            body .put("rem_mny","STSC");//부분취소일 경우 남은 원거래 금액
            body.put("mod_desc", "취소");
            response = Unirest.post(cancelUrl)
                    .body(body)
                    .asString();
            PaymentCancelResponseVo paymentApplyResponseVo = new Gson().fromJson(response.getBody(), PaymentCancelResponseVo.class);
            if (paymentApplyResponseVo.getRes_cd().equals("0000")) {
                //todo 결제 취소 성공

            } else {
                //todo 배치키 발급 실패
            }
            return paymentApplyResponseVo;
        } catch (Exception e) {

            return null;
        }
    }

    @Override
    public PaymentSubscribeResponseVo requestNewSubscribe(SubscribeBody req, PcLoginInfoVo pcLoginInfoVo) {
        HttpResponse<String> response = null;
        PaymentSubscribeResponseVo paymentSubscribeResponseVo = null;
        try {
            JSONObject body = new JSONObject();
            body.put("tran_cd", req.getTracn_cd());
            body.put("kcp_cert_info", subscribeCertification);
            body.put("site_cd", subscribeSiteCd);
            body.put("enc_data", req.getEnc_data());
            body.put("enc_info", req.getEnc_info());
            response = Unirest.post(subscribeBatchUrl)
                    .body(body)
                    .asString();
            paymentSubscribeResponseVo = new Gson().fromJson(response.getBody(), PaymentSubscribeResponseVo.class);
            
            PgInfoVo pgInfoVo = new PgInfoVo();
            pgInfoVo.setFk_company(pcLoginInfoVo.getLoginCompanyPk());
            pgInfoVo.setFk_company_staff(pcLoginInfoVo.getLoginCompanyStaffPk());
            pgInfoVo.setUser_type(pcLoginInfoVo.getLoginUserType());
            pgInfoVo.setPay_key(paymentSubscribeResponseVo.getBatch_key());
            pgInfoVo.setPay_method("CARD");
            pgInfoVo.setPay_company_cd(paymentSubscribeResponseVo.getCard_cd());
            
            pgInfoVo.setTran_cd(req.getTracn_cd());
            pgInfoVo.setPay_company_name(paymentSubscribeResponseVo.getCard_name());

            System.out.println("!!!!!!!!@@@@@@@@@@@@@@");
            String beforeMyPlanName = null;
            String planName = null;
            boolean isFrist = false;
            MyNewCreditItemVo mySelectPlanList = creditDao.getSelectPlan(req.getFk_service_plan());
            
            pgInfoVo.setPay_name(mySelectPlanList.getPp_card_name());
            int myLastCost = 0;
            if (paymentSubscribeResponseVo.getRes_cd().equals("0000")) {
                SubscribePaymentBody subscribePaymentBody = creditService.getBatchKey(pcLoginInfoVo.getLoginCompanyPk());
                if (subscribePaymentBody == null) {
                    creditService.registerCard(pgInfoVo);
                    beforeMyPlanName = mySelectPlanList.getPp_card_name();
                    planName = beforeMyPlanName;
                    isFrist = true;
                } else {
                	//변경전 결제정보 가져오기
                	MyNewCreditItemVo myLastSelectPlan = creditDao.getLastSelectPlan(pcLoginInfoVo.getLoginCompanyPk());
                	
                	//PgPayInfo pgPayInfo = creditService.getPgPayInfo(pcLoginInfoVo.getLoginCompanyPk());
                	//변경 전 기존 결제정보 로그 저장
                	creditService.pgPayInfoLogInsert(pcLoginInfoVo.getLoginCompanyPk());
                    // 결제정보 변경
                	System.out.println("pgInfoVo:" + pgInfoVo);
                	creditService.updateCard(pgInfoVo, pcLoginInfoVo.getLoginCompanyPk());
                    beforeMyPlanName = myLastSelectPlan.getPp_card_name();
                    planName = mySelectPlanList.getPp_card_name();
                    myLastCost = myLastSelectPlan.getCost();
                }
                
                //선택한 플랜의 결제단가
                int cost = mySelectPlanList.getCost();
                
                PaymentSubscribeApplyResponseVo paymentSubscribeApplyResponseVo = batchApply(paymentSubscribeResponseVo.getBatch_key(), req.getOrder_idxx(), cost);
                
                req.setOrder_no(paymentSubscribeApplyResponseVo.getTno());
                req.setOrder_method("CARD");
                req.setOrder_company_cd(paymentSubscribeApplyResponseVo.getCard_cd());
                req.setSum_money(Integer.parseInt(paymentSubscribeApplyResponseVo.getAmount()));
                req.setOrder_company_name(paymentSubscribeApplyResponseVo.getCard_name());
                //creditService.upgrade(req, pcLoginInfoVo);
                
                //결제이력 처음가입
                if(isFrist) {
                	long pkPgPayLog = creditService.selectRegisterPgPayLog(pcLoginInfoVo.getLoginCompanyPk(), req.getFk_service_plan());
                	//최초 플랜가입
                    ploonetApiService.fristSendBill(mySelectPlanList, pcLoginInfoVo.getLoginCompanyPk(), pkPgPayLog);
                    
                }else { //기존 플랜정보가 있는 경우
                	
                	int costPayBack = ploonetApiService.creditClose(pcLoginInfoVo.getLoginCompanyPk());
                	int returnCost = myLastCost - costPayBack;
                	myLastCost = myLastCost - costPayBack;
                	PgPayLog pgPayLog = creditService.getLastPgPayLog(pcLoginInfoVo.getLoginCompanyPk());
                	
                	
                	//부분취소
                	requestCancelPaymentSTPC(pgPayLog.getTran_cd(), myLastCost + "", returnCost + "", pcLoginInfoVo.getLoginCompanyPk(), pcLoginInfoVo.getLoginCompanyStaffPk(), pgPayLog.getItem_cd());
                	
                	long pkPgPayLog = creditService.registerPgPayLogSub(paymentSubscribeApplyResponseVo, pcLoginInfoVo.getLoginCompanyPk(), pcLoginInfoVo.getLoginCompanyStaffPk(), "B20115", paymentSubscribeApplyResponseVo.getTno(), mySelectPlanList, paymentSubscribeResponseVo.getBatch_key());
                	
                	//재가입
                	ploonetApiService.fristSendBill(mySelectPlanList, pcLoginInfoVo.getLoginCompanyPk(), pkPgPayLog);
                	
                }
                
                //creditService.ppcardMerge(pcLoginInfoVo, req.getFk_service_plan());
                creditService.newPayProsess(req, pcLoginInfoVo, beforeMyPlanName, planName);
                
                //todo 배치키 발급성공

            } else {
                //todo 배치키 발급 실패
                throwException.statusCode(400);
                
            }

            return paymentSubscribeResponseVo;

        } catch (Exception e) {
            throwException.statusCode(400);
        }
        return null;
    }

    @Override
    public void requestSubscribe(SubscribeBody req, PcLoginInfoVo pcLoginInfoVo) {
        HttpResponse<String> response = null;
        try {
            //SubscribePaymentBody batchKey = creditService.getBatchKey(pcLoginInfoVo.getLoginCompanyStaffPk(), pcLoginInfoVo.getLoginCompanyPk());
            
            System.out.println("22222222 구독신청");
//            int myInt = creditService.myplan(pcLoginInfoVo.getLoginCompanyPk());
            MyNewCreditItemVo mySelectPlanList= creditDao.getSelectPlan(req.getFk_service_plan());
            
            System.out.println("mySelectPlanList" + mySelectPlanList);
            //creditService.newPayProsess(req,pcLoginInfoVo);
            
//            int money = creditService.getPlanMoney(req.getFk_service_plan(), req.getFk_service_plan_dc());
//            System.out.println("money : "+ money);
//			PaymentSubscribeApplyResponseVo paymentSubscribeApplyResponseVo = batchApply(batchKey.getOrder_key(), req.getOrder_idxx(), money);
//			System.out.println("paymentSubscribeApplyResponseVo"+ paymentSubscribeApplyResponseVo);
//			if (paymentSubscribeApplyResponseVo.getRes_cd().equals("0000")) {
//			}else {
//				System.out.println("에러러러러러러러러러러러");
//			}
            
            
			
//            int selectInt = mySelectPlanList.getCredit_policy();
//
//            if (myInt > selectInt) {
//                creditService.downgrade(req, pcLoginInfoVo);
//
//            } else if (myInt < selectInt) {
//                int money = creditService.getPlanMoney(req.getFk_service_plan(), req.getFk_service_plan_dc());
//                PaymentSubscribeApplyResponseVo paymentSubscribeApplyResponseVo = batchApply(batchKey.getOrder_key(), req.getOrder_idxx(), money);
//                if (paymentSubscribeApplyResponseVo.getRes_cd().equals("0000")) {
//                    //업그레이드
//                    req.setOrder_no(paymentSubscribeApplyResponseVo.getTno());
//                    req.setOrder_method("card");
//                    req.setOrder_company_cd(paymentSubscribeApplyResponseVo.getCard_cd());
//                    req.setSum_money(Integer.parseInt(paymentSubscribeApplyResponseVo.getAmount()));
//                    req.setOrder_company_name(paymentSubscribeApplyResponseVo.getCard_name());
//                    creditService.upgrade(req, pcLoginInfoVo);
//
//                } else {
//                    throwException.statusCode(400);
//                }
//            } else {
//
//            }
            //todo 배치키 발급성공


        } catch (Exception e) {
            throwException.statusCode(400);
        }


    }

    @Override
    public Map<String, Object> getOrderIdx() {
        Map<String, Object> result = new HashMap<>();
        result.put("site_cd", subscribeSiteCd);
        String order_idxx = UUID.randomUUID().toString();
        result.put("order_idxx", order_idxx);
        result.put("kcpgroup_id", kcpGroupId);
        return result;
    }
    
    @Override
    public Map<String, Object> getSuccessPayNew(SubscribeBodyNew req, PcLoginInfoVo pcLoginInfoVo) {
        Map<String, Object> resultMap = new HashMap<>();
        SubscribeBodyNew subscribeBody = new SubscribeBodyNew();
        subscribeBody.setPk_company(pcLoginInfoVo.getLoginCompanyPk());
        SuccessPayNewVo successPayNewVo = creditService.getSuccessPayNew(subscribeBody);               
        resultMap.put("planName",successPayNewVo.getPp_card_name());
        resultMap.put("cost",successPayNewVo.getCost());
        resultMap.put("credit",successPayNewVo.getCredit());
        resultMap.put("payDtFrom",successPayNewVo.getPay_dt_from());
        resultMap.put("payDtTo",successPayNewVo.getPay_dt_to());
        
        
        return resultMap;
    }

    @Override
    public Map<String, Object> getSuccessPay(SubscribeBody req, PcLoginInfoVo pcLoginInfoVo) {
        Map<String, Object> resultMap = new HashMap<>();
        SubscribeBody subscribeBody = new SubscribeBody();
        System.out.println(req.getFk_service_plan());
        subscribeBody.setFk_service_plan(req.getFk_service_plan());
//        subscribeBody.setFk_service_plan("LT011");
        subscribeBody.setPk_company(pcLoginInfoVo.getLoginCompanyPk());
        subscribeBody.setPk_company_staff(req.getPk_company_staff());
        SuccessPayVo successPayVo = creditService.getSuccessPay(subscribeBody);
        if (successPayVo.getCharge_unit().equals("Y")) {
            int amount = successPayVo.getCharge_amount() * 12;
            int pay = amount - (amount / successPayVo.getDc_rate());
            resultMap.put("pay", pay);
        } else {
            resultMap.put("pay", successPayVo.getCharge_amount());
        }
        resultMap.put("charge_unit", successPayVo.getCharge_unit());
        resultMap.put("planName", successPayVo.getService_plan_name());
        resultMap.put("service_dt_from", successPayVo.getService_dt_from());


        return resultMap;
    }

    @Override
    public Map<String, Object> getAuthOrderIdx() {
        Map<String, Object> result = new HashMap<>();
        result.put("site_cd", siteCd);
        String order_idxx = UUID.randomUUID().toString();
        result.put("order_idxx", order_idxx);
        result.put("good_name", "크레딧 충전");
        result.put("site_name", "Ploonet");
        return result;
    }

    @Override
    public void requestEntrySubScribe(SubscribeBody req, PcLoginInfoVo pcLoginInfoVo) {
        SubscribeBody subscribeBody = new SubscribeBody();
        subscribeBody.setFk_service_plan("LT011");
        subscribeBody.setFk_service_plan_dc(req.getFk_service_plan_dc());
        creditService.downgrade(subscribeBody, pcLoginInfoVo);
    }


    public PaymentSubscribeApplyResponseVo batchApply(String batchKey, String order_idxx, int money) {
        HttpResponse<String> response = null;
        try {
            JSONObject body = new JSONObject();
            body.put("site_cd", subscribeSiteCd);
            body.put("kcp_cert_info", subscribeCertification);
            body.put("pay_method", "CARD");// 고정값
            body.put("amount", money);//총 가격
            body.put("card_mny", money);//카드 결제 가격
            body.put("quota", "00");// 고정값
            body.put("currency", "410");//원화 410
            body.put("ordr_idxx", order_idxx);
            body.put("card_tx_type", "11511000");// 고정값
            body.put("bt_batch_key", batchKey);// 배치키
            body.put("bt_group_id", kcpGroupId);// 고정값


            response = Unirest.post(subscribeApplyUrl)
                    .body(body)
                    .asString();
            PaymentSubscribeApplyResponseVo paymentSubscribeApplyResponseVo = new Gson().fromJson(response.getBody(), PaymentSubscribeApplyResponseVo.class);
            if (paymentSubscribeApplyResponseVo.getRes_cd().equals("0000")) {
                //todo 구독 결제 성공

            } else {
                //todo 배치키 발급 실패
            }
            return paymentSubscribeApplyResponseVo;
        } catch (Exception e) {

        }
        return null;
    }
    
    @Override
    public PaymentCancelResponseVo requestCancelPaymentSTPC(String tno, String modMny, String remMny, long loginCompanyPk, long loginCompanyStaffPk, String item_cd) {
        HttpResponse<String> response = null;
        try {
            JSONObject body = new JSONObject();
            String mod_type = "STSC";//전체 승인취소 - STSC / 부분취소 - STPC
            String signKeyString = siteCd + "^" + tno + "^" + mod_type;
            
            body.put("site_cd", siteCd);
            body.put("tno", tno);
            body.put("kcp_cert_info", certification);
            body.put("kcp_sign_data", PaymentUtils.sha256WithRSA(signKeyString, privateCertification, privateKeyPassword));
            body.put("mod_type", mod_type);
            body .put("mod_mny", modMny);//부분취소일 경우 부분취소금액
            body .put("rem_mny", remMny);//부분취소일 경우 남은 원거래 금액
            body.put("mod_desc", "부분취소");
            response = Unirest.post(cancelUrl)
                    .body(body)
                    .asString();
            PaymentCancelResponseVo paymentApplyResponseVo = new Gson().fromJson(response.getBody(), PaymentCancelResponseVo.class);
            if (paymentApplyResponseVo.getRes_cd().equals("0000")) {
                //todo 결제 취소 성공
            	MyNewCreditItemVo mySelectPlanList = creditDao.getSelectPlan(item_cd);
            	
            	Map<String,Object> paramMap = new HashMap<>();
        		paramMap.put("loginCompanyPk",loginCompanyPk);
        		paramMap.put("pay_name", mySelectPlanList.getPp_card_name()); // mySelectPlanList 에 있음
        		paramMap.put("pay_method", "CARD"); // ??????????? db 예시에 CARD 라고 나옴
        		paramMap.put("tran_cd", paymentApplyResponseVo.getTno()); // req 에 담겨있음  
        		paramMap.put("pg_err_cd", null); // ?????????
        		paramMap.put("pg_err_desc", null); //?????????
        		paramMap.put("item_cd", mySelectPlanList.getPp_card_cd());// mySelectList에 있음
        		paramMap.put("pk_company_staff",loginCompanyStaffPk);
        		paramMap.put("bill_status", "B20112");
        		paramMap.put("payback_cost", modMny);
        		
        		//취소 로그
            	long cancelPkPgPayLog = creditService.registerCancelPgPayLog(paramMap);
            	
            	
            } else {
                //todo 배치키 발급 실패
            }
            return paymentApplyResponseVo;
        } catch (Exception e) {

            return null;
        }
//        return null;
    }
    
    
}
