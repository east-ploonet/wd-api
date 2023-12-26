package com.saltlux.aice_fe.email.service;


import javax.mail.MessagingException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface EmailService {
    void sendEmail(List<String> userEmails, String title, String templatePath, Map<String, String> values) throws MessagingException, IOException;
}
