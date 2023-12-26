package com.saltlux.aice_fe.pc.question.dao;

import com.saltlux.aice_fe.pc.admin.vo.ReplyQuestionVo;
import com.saltlux.aice_fe.pc.question.vo.QuestionFileVo;
import com.saltlux.aice_fe.pc.question.vo.QuestionTypeVo;
import com.saltlux.aice_fe.pc.question.vo.QuestionVo;
import com.saltlux.aice_fe.pc.question.vo.Request.QuestionReqBody;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface QuestionDao {

    void saveQuestion(QuestionVo reqBody);

    List<QuestionTypeVo> getQuestionType();

    void saveTermsAgree(QuestionVo reqBody);

    List<QuestionVo> getQuestion(Map<String, Object> paramMap);

    Integer getQuestionCnt(Map<String, Object> paramMap);

    List<QuestionFileVo> getQuestionFiles(List<Long> collect);

    void registerQuestionFile(Map<String, Object> paramMap);

    List<QuestionVo> getQuestionAdmin(Map<String, Object> paramMap);

    Integer getQuestionAdminCnt(Map<String, Object> paramMap);

    QuestionVo getQuestionAdminDetail(Long pk_question);

    List<QuestionFileVo> getQuestionAdminFiles(Long pk_question);

    void registerQuestionReply(ReplyQuestionVo paramMap);

    int getNoticeAdminCnt(Map<String, Object> paramMap);

    void deleteQuestion(List<Long> questionIdList);


}
