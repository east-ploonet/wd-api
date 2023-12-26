package com.saltlux.aice_fe.pc.payment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("${apiVersionPrefix}/payment")
public class PaymentTestController {

    @GetMapping
    String paymentTestForm() {
        return "thymeleaf/payment/payment";
    }


    @GetMapping("/subscribe")
    String paymentTestSubscribeForm() {
    	System.out.println("111111111111111111111111");
        return "thymeleaf/payment/subscribe";
    }
}
