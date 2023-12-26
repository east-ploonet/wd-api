package com.saltlux.aice_fe.pc.question.service;

import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.join.vo.CompanyStaffVo;
import com.saltlux.aice_fe.pc.question.vo.QuestionVo;
import com.saltlux.aice_fe.pc.question.vo.Request.QuestionReqBody;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import javax.mail.MessagingException;

import java.util.Map;

public interface QuestionService {
    void save(QuestionVo reqBody);

    Map<String, Object> resetCompanyStaffTicketCode(CompanyStaffVo companyStaffVo) throws Exception ;

    Map<String, Object> getType();

    Page<QuestionVo> getQuestions(Pageable pageable, String query);

//    void registerFiles(Map<String, Object> paramMap, Long pk_question) throws Exception;


    Map<String, Object> getQuestionsAdmin(Pageable pageable, String query, String type);
    
    void sendAlermEmail(QuestionVo questionVo) throws MessagingException, IOException;
}
