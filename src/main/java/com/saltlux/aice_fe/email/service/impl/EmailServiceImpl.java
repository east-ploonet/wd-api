package com.saltlux.aice_fe.email.service.impl;

import com.saltlux.aice_fe.email.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.thymeleaf.context.Context;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@EnableAsync
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${service.domain}")
    private String serverdDomain;


    @Autowired
    private SpringTemplateEngine templateEngine;

    @Override
    @Async
    public void sendEmail(List<String> userEmails, String title, String templatePath, Map<String, String> values) throws MessagingException, IOException {

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        String pathPrefix = "thymeleaf/email/";
        //메일 제목 설정
        helper.setSubject(title);
        helper.setFrom("support@ploonet.com");
        //수신자 설정
        helper.setTo(userEmails.stream().toArray(String[]::new));

        values.put("server", serverdDomain);
        //템플릿에 전달할 데이터 설정
        Context context = new Context();
        values.forEach((key, value) -> {
            context.setVariable(key, value);
        });

        //메일 내용 설정 : 템플릿 프로세스
        String html = templateEngine.process(pathPrefix + templatePath, context);
        helper.setText(html, true);

        //메일 보내기
        javaMailSender.send(message);

    }
}
