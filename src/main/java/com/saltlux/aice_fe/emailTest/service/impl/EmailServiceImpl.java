//package com.saltlux.aice_fe.emailTest.service.impl;
//
//import com.saltlux.aice_fe.email.service.EmailService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.stereotype.Service;
//import org.thymeleaf.spring5.SpringTemplateEngine;
//
//import javax.mail.BodyPart;
//import javax.mail.Message;
//import javax.mail.MessagingException;
//import javax.mail.Session;
//import javax.mail.Transport;
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeBodyPart;
//import javax.mail.internet.MimeMessage;
//import javax.mail.internet.MimeMultipart;
//import javax.mail.internet.MimeUtility;
//import javax.mail.internet.PreencodedMimeBodyPart;
//
//import org.thymeleaf.context.Context;
//
//import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//import java.util.List;
//import java.util.Map;
//import java.util.Properties;
//
//@Service
//@EnableAsync
//public class EmailServiceImpl implements EmailService {
//
//	public static boolean sendMail(String fromMail, String fromName, String toMail, String toName, String title, String content, String mailHost, String mailPort){
//
//		Properties props = new Properties();
//		props.put("mail.smtp.host", mailHost);
//		props.put("mail.smtp.port", mailPort);
//		props.put("mail.smtp.ssl.protocols", "TLSv1.2");
//		Authenticator authenticator = null; // 박책임님 필요
//		if(!StringUtil.isEmpty(mailUser) && !StringUtil.isEmpty(mailPassword))
//		{
//			props.put("mail.smtp.auth", true);
//			//authenticator = new SMTPAuthenticator(mailUser, mailPassword);
//			authenticator = new javax.mail.Authenticator() {
//		        protected javax.mail.PasswordAuthentication getPasswordAuthentication(){
//		            return new javax.mail.PasswordAuthentication(mailUser, mailPassword);
//		        }
//		    };
//		}
//		
//		if(StringUtil.isEquals(startTls, "1")) props.put("mail.smtp.starttls.enable", true);
//		if(StringUtil.isEquals(ssl, "1")){
//			props.put("mail.smtp.ssl.enable", true);
//			props.put("mail.smtp.ssl.trust", mailHost);
//		}
//		
//		try{
//			Session session = Session.getDefaultInstance(props, authenticator);
//			
//			
//			
//			/*
//			Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
//		        protected javax.mail.PasswordAuthentication getPasswordAuthentication(){
//		            return new javax.mail.PasswordAuthentication(mailUser, mailPassword);
//		        }
//		    });
//		    */
//		    //session.setDebug(true);
//			MimeMessage mailMessage = new MimeMessage(session);
//			
//			mailMessage.setHeader("Content-Type","text/html");
//			mailMessage.setSubject(title,"UTF-8");
//			mailMessage.setFrom(new InternetAddress(fromMail)); // 보내는 EMAIL (정확히 적어야 SMTP 서버에서 인증 실패되지 않음)
//			mailMessage.setRecipients(Message.RecipientType.TO, toMail ); //수신자 셋팅
//			
//			BodyPart body = new MimeBodyPart();
//			BodyPart attachment = new PreencodedMimeBodyPart("base64");
//			attachment.setFileName(MimeUtility.encodeText("test.mp4", "euc-kr","B")); //한글 파일명 처리
//			body.setText(content); //일반텍스트
//			MimeMultipart multipart = new MimeMultipart();
//			multipart.addBodyPart(body);
//			multipart.addBodyPart(attachment);
//			mailMessage.setContent(multipart, "text/plain; charset=UTF-8");
//			  
//			Transport.send(mailMessage);
//		
//		}catch(UnsupportedEncodingException | MessagingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return false;
//		}
//		
//		return true;
//	}
//}
