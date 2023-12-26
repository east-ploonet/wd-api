package com.saltlux.aice_fe.pc.question.service.impl;

import com.saltlux.aice_fe._baseline.baseService.FileService;
import com.saltlux.aice_fe._baseline.baseService.impl.BaseServiceImpl;
import com.saltlux.aice_fe._baseline.baseVo.FileVo;
import com.saltlux.aice_fe._baseline.util.FormatUtils;
import com.saltlux.aice_fe._baseline.util.IdGenerator;
import com.saltlux.aice_fe.ecApi.service.PloonetApiService;
import com.saltlux.aice_fe.file.service.FileUploadService;
import com.saltlux.aice_fe.file.utils.FileUtils;
import com.saltlux.aice_fe.pc.auth.dao.PcAuthDao;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.join.vo.CompanyStaffVo;
import com.saltlux.aice_fe.pc.notice.vo.NoticeFileVo;
import com.saltlux.aice_fe.pc.question.dao.QuestionDao;
import com.saltlux.aice_fe.pc.question.service.QuestionService;
import com.saltlux.aice_fe.pc.question.vo.QuestionFileVo;
import com.saltlux.aice_fe.pc.question.vo.QuestionTypeVo;
import com.saltlux.aice_fe.pc.question.vo.QuestionVo;
import com.saltlux.aice_fe.pc.question.vo.Request.QuestionReqBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

import com.saltlux.aice_fe.email.service.EmailService;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.logging.SimpleFormatter;
import javax.mail.MessagingException;

@Service
public class QuestionImpl extends BaseServiceImpl implements QuestionService {

    @Autowired
    FileUploadService fileService;

    @Autowired
    PcAuthDao pcAuthDao;

    @Autowired
    private PloonetApiService ploonetApiService;

    @Autowired
    private QuestionDao questionDao;
    
    @Autowired
    EmailService emailService;
    
    @Override
    public void sendAlermEmail(QuestionVo questionVo) throws MessagingException, IOException {

        Map<String, String> map = new HashMap<>();
        List<String> emailList = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
        String regdate = format.format(new Date()).toString();

        // emailList.add("support@ploonet.com");

        // String[] email = questionVo.fd_email.split("@");
        // String userEmail = email[0].replaceAll("(?<=.{3}).", "*") + "@" + email[1].replaceAll("(?<=.{2}).", "*");

        map.put("title", questionVo.getFd_title());
        map.put("regdate", regdate);
        map.put("name",questionVo.getFd_name());
        map.put("cellPhone",questionVo.getFd_mobile());
        map.put("email", questionVo.getFd_email());
        map.put("content", questionVo.getFd_content().replace("\n","</br>").replace("\r\n","</br>"));

        System.out.println(map.toString());

        emailService.sendEmail(emailList, "신규 문의가 들어왔습니다!", "new-qna.html", map);
    }

    @Override
    public void save(QuestionVo reqBody) {
        questionDao.saveQuestion(reqBody);
        questionDao.saveTermsAgree(reqBody);
        if (reqBody.getFileVos().size() > 0) {
            reqBody.getFileVos().forEach(item -> item.setFk_question(reqBody.getPk_question()));
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("questionFileVoList", reqBody.getFileVos());
            questionDao.registerQuestionFile(paramMap);
        }
    }

    @Override
    public Map<String, Object> resetCompanyStaffTicketCode(CompanyStaffVo reqCompanyStaffVo) throws Exception {

        Map<String, Object> resultMap = new HashMap<String, Object>();
        String ticketCode = IdGenerator.generateUniqueId("T");
        Date ticketCodeDate = new Date();

        if (reqCompanyStaffVo.getFd_signup_keycode() == null || "".equals(reqCompanyStaffVo.getFd_signup_keycode())) {
            reqCompanyStaffVo.setFd_signup_keycode(ticketCode);
        }

        Map<String, Object> sendMsgResult = null;

        if (reqCompanyStaffVo.getPk_company_staff() != 0L) {
            pcAuthDao.resetCompanyStaffTicketCode(reqCompanyStaffVo);

            //-- 초대코드 발송 (API 연동)
            CompanyStaffVo companyStaffVo = pcAuthDao.getCompanyStaffByPk(reqCompanyStaffVo);
            companyStaffVo.setFd_signup_keycode(reqCompanyStaffVo.getFd_signup_keycode());

            sendMsgResult = ploonetApiService.sendMessageApi(companyStaffVo);
			/*
			[result body]
			  - msgId 	: 메세지 일련번호
			  - result 	: 처리 결과 구분값(Y:성공, N:실패)
			  - code 	: 처리결과 코드(실패시 실패 코드)
			  - error 	: 에러 메세지
			*/
        }

        resultMap.put("signupCode", reqCompanyStaffVo.getFd_signup_keycode());
        resultMap.put("signupCodeDate", FormatUtils.dateToStringCustomize(ticketCodeDate, "yyyy.MM.dd"));
        resultMap.put("sendMsgResult", sendMsgResult);

        return resultMap;
    }

    @Override
    public Map<String, Object> getType() {
        List<QuestionTypeVo> questionTypeVoList = questionDao.getQuestionType();
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("questionType", questionTypeVoList);
        return resultMap;
    }

    @Override
    public Page<QuestionVo> getQuestions(Pageable page, String query) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("offset", page.getOffset());
        paramMap.put("pageSize", page.getPageSize());
//        paramMap.put("type",type);
        paramMap.put("query", query);
        PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
        List<QuestionVo> questionVoList = new ArrayList<>();
        if (loginInfoVo == null) {
            return new PageImpl<>(questionVoList, page, 0);
        }
        paramMap.put("staff_pk", loginInfoVo.getLoginCompanyStaffPk());
        questionVoList = questionDao.getQuestion(paramMap);


        List<Long> collect = questionVoList.stream().map(
                item -> {
                    Long pk_question = item.getPk_question();
                    return pk_question;
                }
        ).collect(Collectors.toList());

        Integer cnt = 0;
        if (collect.size() > 0) {
            cnt = questionDao.getQuestionCnt(paramMap);
            Map<Long, List<QuestionFileVo>> questionFileMap = questionDao.getQuestionFiles(collect).stream().collect(Collectors.groupingBy(item -> item.getFk_question()));
            questionVoList.forEach(item -> {

                if (questionFileMap.get(item.getPk_question()) != null) {
                    item.setFileVos(questionFileMap.get(item.getPk_question()));
                }
            });
        }

        return new PageImpl<>(questionVoList, page, cnt);
    }

//    @Override
//    public void registerFiles(Map<String, Object> paramMap, Long pk_question) throws Exception {
//        MultipartFile[] uploadFile = (MultipartFile[]) paramMap.get("uploadFile");
//        List<QuestionFileVo> questionFileVoList=new ArrayList<>();
//        if( uploadFile != null && uploadFile.length > 0 ) {
//
//
//                for (MultipartFile file:uploadFile) {
//                    String uploadFilePath = FileUtils.getUploadFilePath(FileUtils.FileUploadType.FAQ, file.getOriginalFilename());
//                    String url = fileService.uploadFileToS3(file, uploadFilePath);
//                    QuestionFileVo questionFileVo=new QuestionFileVo();
//                    questionFileVo.setFk_question(pk_question);
//                    questionFileVo.setFd_file_name(file.getOriginalFilename());
//                    questionFileVo.setFd_file_path(url);
//                    questionFileVo.setFd_file_size(String.valueOf(file.getSize()));
//                    questionFileVo.setFd_mime_code(file.getContentType());
//                    questionFileVoList.add(questionFileVo);
//                }
//
//            }
//
//        paramMap.put("questionFileVoList",questionFileVoList);
//        questionDao.registerQuestionFile(paramMap);
//    }

    @Override
    public Map<String, Object> getQuestionsAdmin(Pageable pageable, String query, String type) {

        return null;
    }


}
