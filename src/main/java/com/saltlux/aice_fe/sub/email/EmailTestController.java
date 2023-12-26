package com.saltlux.aice_fe.sub.email;

import com.saltlux.aice_fe._baseline.baseVo.ResponseVo;
import com.saltlux.aice_fe.email.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("${apiVersionPrefix}/email")
public class EmailTestController {

    @Autowired
    EmailService emailService;

    @GetMapping
    Object paymentEmailForm() {
        ArrayList<String> to = new ArrayList<>();
        to.add("dlcjfgjs321@naver.com");
        Map<String, String> map = new HashMap();
        map.put("name", "가나다라마바사아</br>호이호이호이");
        try {
            emailService.sendEmail(to, "테스트", "sample.html", map);
        } catch (Exception e) {
           
        }
        return new ResponseVo(200);
    }


}
