package com.saltlux.aice_fe.pc.admin.service;

import com.saltlux.aice_fe._baseline.baseVo.ResponseVo;
import com.saltlux.aice_fe.pc.admin.vo.AdminVo;
import com.saltlux.aice_fe.pc.admin.vo.ReplyQuestionVo;
import com.saltlux.aice_fe.pc.admin.vo.req.DeleteAdminReq;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginAdminInfoVo;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.notice.vo.NoticeVo;
import com.saltlux.aice_fe.pc.question.vo.QuestionVo;
import org.aspectj.weaver.ast.Not;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.mail.MessagingException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

public interface AdminService {
    void saveAdmin(AdminVo adminVo);

    ResponseVo loginAdmin(Map<String, Object> bodyMap);

    Page<QuestionVo> getQuestionsAdmin(Pageable pageable, String query, String type);

    QuestionVo getQuestionDetail(Long pk_question);

    Map<String, Object> registerQuestionReply(ReplyQuestionVo reqJsonObj) throws MessagingException, IOException;

    Page<NoticeVo> getNotice(Pageable pageable, String query, String type);

    void save(NoticeVo noticeVo, PcLoginAdminInfoVo  pcLoginInfoVo) throws ParseException;

    void deleteQuestion(DeleteAdminReq req);

    NoticeVo getNoticeDetail(Long pk_notice);

    void deleteNotice(DeleteAdminReq req);

    Map<String, Object> updateNotice(NoticeVo noticeVo);


    PcLoginAdminInfoVo getAdminUserLoginByPk(long pk_admin);
}
