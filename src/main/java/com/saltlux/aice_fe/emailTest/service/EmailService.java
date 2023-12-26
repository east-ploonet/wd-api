package com.saltlux.aice_fe.emailTest.service;


import javax.mail.MessagingException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface EmailService {
    boolean sendMail(String fromMail, String fromName, String toMail, String toName, String title, String content, String mailHost, String mailPort) throws MessagingException, IOException;
}
