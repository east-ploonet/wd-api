package com.saltlux.aice_fe.pc.payment.controller;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.saltlux.aice_fe._baseline.baseController.BaseController;
import com.saltlux.aice_fe._baseline.baseVo.ResponseVo;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.payment.service.PaymentService;
import com.saltlux.aice_fe.pc.payment.vo.ChargeVo;
import com.saltlux.aice_fe.pc.payment.vo.SubscribeBody;
import com.saltlux.aice_fe.pc.payment.vo.SubscribeBodyNew;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("${apiVersionPrefix}/workStage/payment")
public class PaymentController extends BaseController {

    @Autowired
    PaymentService paymentService;


    @PostMapping("/auth")
    Object paymentForm(@RequestBody ChargeVo req) throws UnirestException {
        PcLoginInfoVo pcLoginInfoVo = getPcLoginInfoVo();
        if (pcLoginInfoVo == null) {
            return new ResponseVo(403);
        }
//        String tranCd = params.get("tran_cd").toString();  // 결제 요청타입
//        String goodMny = params.get("good_mny").toString();  //결제 금액
//        String encData= params.get("enc_data").toString(); //결제창 인증결과 암호화 정보
//        String encInfo = params.get("enc_info").toString(); //결제창 인증결과 암호화 정보
        Map<String, Object> map = paymentService.requestApplyPayment(req, pcLoginInfoVo);
        if (!map.get("code").toString().equals("200")) {
            return new ResponseVo(400, map.get("message").toString());
        }
        return new ResponseVo(200, map);
    }

    @GetMapping("/auth")
    Object getPaymentForm() {
        PcLoginInfoVo pcLoginInfoVo = getPcLoginInfoVo();
        if (pcLoginInfoVo == null) {
            return new ResponseVo(403);
        }
        Map<String, Object> resultMap = paymentService.getAuthOrderIdx();
        return new ResponseVo(200, resultMap);
    }


    @PostMapping("/cancel")
    Object paymentCancelTestForm(@RequestParam String tno) {
        paymentService.requestCancelPayment(tno);
        return new ResponseVo(200);
    }

    @GetMapping("")
    public Object getOrderIdx() {
        PcLoginInfoVo pcLoginInfoVo = getPcLoginInfoVo();
        if (pcLoginInfoVo == null) {
            return new ResponseVo(403);
        }
        Map<String, Object> resultMap = paymentService.getOrderIdx();
        return new ResponseVo(200, resultMap);
    }


    @PostMapping("/subscribe/new")
    public Object newPaymentSubscribeForm(@RequestBody SubscribeBody req) {
        PcLoginInfoVo pcLoginInfoVo = getPcLoginInfoVo();
        if (pcLoginInfoVo == null) {
            return new ResponseVo(403);
        } else if (req.getFk_service_plan().equals("") && req.getFk_service_plan_dc().equals("")) {
            return new ResponseVo(400);
        }

        paymentService.requestNewSubscribe(req, pcLoginInfoVo);
        Map<String, Object> resultMap = paymentService.getSuccessPay(req, pcLoginInfoVo);

        return new ResponseVo(200, resultMap);
    }

    @PostMapping("/subscribe/entry")
    public Object entryPaymentSubscribeForm(@RequestBody SubscribeBody req) {
        PcLoginInfoVo pcLoginInfoVo = getPcLoginInfoVo();
        if (pcLoginInfoVo == null) {
            return new ResponseVo(403);
        } else if (req.getFk_service_plan_dc().equals("")) {
            return new ResponseVo(400);
        }

        paymentService.requestEntrySubScribe(req, pcLoginInfoVo);
        Map<String, Object> resultMap = paymentService.getSuccessPay(req, pcLoginInfoVo);

        return new ResponseVo(200, resultMap);
    }

    @PostMapping("/subscribe")
    public Object paymentSubscribeForm(@RequestBody SubscribeBodyNew req, @RequestBody SubscribeBody reqver2) {
        PcLoginInfoVo pcLoginInfoVo = getPcLoginInfoVo();
        if (pcLoginInfoVo == null) {
            return new ResponseVo(403);
        }
        paymentService.requestNewSubscribe(reqver2, pcLoginInfoVo);
        //Map<String, Object> resultMap = paymentService.getSuccessPay(req, pcLoginInfoVo);
        Map<String, Object> resultMap = paymentService.getSuccessPayNew(req, pcLoginInfoVo);
        return new ResponseVo(200, resultMap);
    }


}
