package com.saltlux.aice_fe.pc.admin.service.Impl;

import com.saltlux.aice_fe._baseline.baseService.FileService;
import com.saltlux.aice_fe._baseline.baseService.impl.BaseServiceImpl;
import com.saltlux.aice_fe._baseline.baseVo.AuthTokenVo;
import com.saltlux.aice_fe._baseline.baseVo.ResponseVo;
import com.saltlux.aice_fe._baseline.commons.Common;
import com.saltlux.aice_fe.email.service.EmailService;
import com.saltlux.aice_fe.file.service.FileUploadService;
import com.saltlux.aice_fe.file.vo.UploadFileVo;
import com.saltlux.aice_fe.pc.admin.dao.AdminDao;
import com.saltlux.aice_fe.pc.admin.service.AdminService;
import com.saltlux.aice_fe.pc.admin.vo.AdminVo;
import com.saltlux.aice_fe.pc.admin.vo.ReplyQuestionVo;
import com.saltlux.aice_fe.pc.admin.vo.req.DeleteAdminReq;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginAdminInfoVo;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.notice.dao.NoticeDao;
import com.saltlux.aice_fe.pc.notice.vo.NoticeFileVo;
import com.saltlux.aice_fe.pc.notice.vo.NoticeVo;
import com.saltlux.aice_fe.pc.question.dao.QuestionDao;
import com.saltlux.aice_fe.pc.question.vo.QuestionFileVo;
import com.saltlux.aice_fe.pc.question.vo.QuestionVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class AdminServiceImpl extends BaseServiceImpl implements AdminService {

    @Autowired
    private AdminDao adminDao;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private NoticeDao noticeDao;

    @Autowired
    EmailService emailService;

    @Autowired
    FileUploadService fileUploadService;

    @Override
    public void saveAdmin(AdminVo adminVo) {
        final String staffPw = BCRYPT_ENCODER.encode(Common.NVL(adminVo.getUser_pw(), ""));
        adminVo.setUser_pw(staffPw);
        adminDao.saveAdmin(adminVo);

    }

    @Override
    public ResponseVo loginAdmin(Map<String, Object> bodyMap) {


        AdminVo adminVo = adminDao.getAdminById(bodyMap);

        //해당 id 회원 없음, 직원 계정 상태 : A1101[정상]
        if (adminVo == null) {
            return new ResponseVo(204, "해당 id의 사용자가 없습니다.");
        }
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put("sha256", new org.springframework.security.crypto.password.StandardPasswordEncoder());
        PasswordEncoder sha256 = new DelegatingPasswordEncoder("sha256", encoders);

        //pw 불일치
        if (!BCRYPT_ENCODER.matches(bodyMap.get("admin_pw").toString(), adminVo.getUser_pw())) {
            return new ResponseVo(401, "비밀번호가 일치하지 않습니다.");
        }

        //인증 토큰 세션등록
        AuthTokenVo authTokenVo = AuthTokenVo.encodeToken(adminVo.getPk_admin_user().toString());
        bodyMap.put("authToken", authTokenVo.getToken());

        // ---------- 사용자 정보 세션등록 ----------
        PcLoginAdminInfoVo pcLoginAdminInfoVo = new PcLoginAdminInfoVo();
        pcLoginAdminInfoVo.setPkAdmin(adminVo.getPk_admin_user());
        pcLoginAdminInfoVo.setLoginAdminId(adminVo.getUser_id());
        pcLoginAdminInfoVo.setLoginAdminName(adminVo.getUser_name());
        pcLoginAdminInfoVo.setLoginAdminLevel(adminVo.getUser_level());
        pcLoginAdminInfoVo.setLoginAdminEmail(adminVo.getUser_email());
        pcLoginAdminInfoVo.setLoginAdminMobile(adminVo.getUser_mobile());


        bodyMap.put("loginInfo", pcLoginAdminInfoVo);
        // ---------- 사용자 정보 세션등록 ----------

        return new ResponseVo(200, "로그인 성공", bodyMap);
    }

    @Override
    public Page<QuestionVo> getQuestionsAdmin(Pageable page, String query, String type) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("offset", page.getOffset());
        paramMap.put("pageSize", page.getPageSize());
//        paramMap.put("type",type);
        paramMap.put("query", query);
        paramMap.put("type", type);
        List<QuestionVo> questionVoList = questionDao.getQuestionAdmin(paramMap);
        Integer cnt = questionDao.getQuestionAdminCnt(paramMap);
        if (cnt == null) {
            cnt = 0;
        }

        return new PageImpl<>(questionVoList, page, cnt);
    }

    @Override
    public QuestionVo getQuestionDetail(Long pk_question) {

        QuestionVo questionVo = questionDao.getQuestionAdminDetail(pk_question);
        List<QuestionFileVo> questionFileVoList = questionDao.getQuestionAdminFiles(questionVo.getPk_question());
        questionVo.setFileVos(questionFileVoList);

        return questionVo;
    }

    @Override
    public Map<String, Object> registerQuestionReply(ReplyQuestionVo replyQuestionVo) throws MessagingException, IOException {
        Map<String, Object> resultMap = new HashMap<>();
        questionDao.registerQuestionReply(replyQuestionVo);
        QuestionVo reQuestionVo = questionDao.getQuestionAdminDetail(replyQuestionVo.getPk_question());
        List<QuestionFileVo> questionFileVoList = questionDao.getQuestionAdminFiles(replyQuestionVo.getPk_question());

        Map<String, String> map = new HashMap<>();
        List<String> emailList = new ArrayList<>();
        emailList.add(reQuestionVo.fd_email);
        String[] email = reQuestionVo.fd_email.split("@");
        String userEmail = email[0].replaceAll("(?<=.{3}).", "*") + "@" + email[1].replaceAll("(?<=.{2}).", "*");
        map.put("email", userEmail);
        map.put("title", reQuestionVo.getFd_title());
        map.put("content", reQuestionVo.getFd_content().replace("\n","</br>").replace("\r\n","</br>"));
        map.put("reply", replyQuestionVo.getFd_reply_content().replace("\n","</br>").replace("\r\n","</br>"));
        emailService.sendEmail(emailList, "문의주신 내용에 답변드립니다.", "qna.html", map);

        reQuestionVo.setFileVos(questionFileVoList);
        resultMap.put("questionVo", reQuestionVo);
        return resultMap;
    }

    @Override
    public Page<NoticeVo> getNotice(Pageable page, String query, String type) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("offset", page.getOffset());
        paramMap.put("pageSize", page.getPageSize());
//        paramMap.put("type",type);
        paramMap.put("query", query);
        paramMap.put("type", type);

        List<NoticeVo> questionVoList = noticeDao.getNoticeAdmin(paramMap);

        Integer cnt = noticeDao.getNoticeAdminCnt(paramMap);
        if (cnt == null) {
            cnt = 0;
        }


        return new PageImpl<>(questionVoList, page, cnt);
    }

    @Override
    public void save(NoticeVo noticeVo, PcLoginAdminInfoVo pcLoginInfoVo) throws ParseException {
        noticeVo.setLoginCompanyStaffPk(pcLoginInfoVo.getPkAdmin());

        if (noticeVo.getFd_release_end_date() == null) {
            Date parse2 = Timestamp.valueOf(LocalDateTime.of(2999, 12, 31, 00, 00, 00));
            noticeVo.setFd_release_end_date(parse2);
        }
        noticeDao.save(noticeVo);
        if (noticeVo.getFileVos().size() > 0) {
            noticeVo.getFileVos().forEach(item -> item.setFk_notice(noticeVo.getPk_notice()));
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("noticeFileVoList", noticeVo.getFileVos());
            noticeDao.registerNoticeFile(paramMap);
        }
    }

    @Override
    public void deleteQuestion(DeleteAdminReq req) {
        if (req.getQuestionIdList().size() > 0) {
            questionDao.deleteQuestion(req.getQuestionIdList());
        }

    }

    @Override
    public NoticeVo getNoticeDetail(Long pk_notice) {
        Map<String, Object> resultMap = new HashMap<>();
        noticeDao.updateViews(pk_notice);
        NoticeVo noticeVo = noticeDao.getDetailNoticeAdmin(pk_notice);
        if (noticeVo!= null) {

            List<NoticeFileVo> detailNoticeFiles = noticeDao.getDetailNoticeFiles(noticeVo.getPk_notice());
            if (noticeVo.getFd_thumbnail() != null) {
                UploadFileVo uploadFileVo = fileUploadService.getFileStream(noticeVo.getFd_thumbnail());
                noticeVo.setThumbnail_base64(uploadFileVo.getBase64());
                noticeVo.setThumbnail_file_code(uploadFileVo.getFd_mime_code());
            }
            noticeVo.setFileVos(detailNoticeFiles);
            return noticeVo;
        }else {
            throwException.statusCode(400);
        }
        return null;
    }

    @Override
    public void deleteNotice(DeleteAdminReq req) {
        if (req.getQuestionIdList().size() > 0) {
            noticeDao.deleteNotice(req.getQuestionIdList());
        }
    }

    @Override
    public Map<String, Object> updateNotice(NoticeVo noticeVo) {
        Map<String, Object> resultMap = new HashMap<>();
        NoticeVo notice = noticeDao.getDetailNoticeAdmin(noticeVo.getPk_notice());
        if (notice==null){
            throwException.statusCode(400);
        }

        noticeDao.updateNotice(noticeVo);
        noticeDao.deleteNoticeFiles(noticeVo);
        NoticeVo noticeVoList = noticeDao.getDetailNoticeAdmin(noticeVo.getPk_notice());
        if (noticeVo.getFileVos().size() > 0) {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("noticeFileVoList", noticeVo.getFileVos());
            noticeDao.registerNoticeFile(paramMap);
        }
        resultMap.put("noticeVoList", noticeVoList);
        return resultMap;
    }

    @Override
    public PcLoginAdminInfoVo getAdminUserLoginByPk(long pk_admin) {
        AdminVo adminVoBody=new AdminVo();
        adminVoBody.setPk_admin_user(pk_admin);
        AdminVo adminVo = adminDao.getAdminByPk(adminVoBody);
        PcLoginAdminInfoVo pcLoginAdminInfoVo=new PcLoginAdminInfoVo();
        pcLoginAdminInfoVo.setPkAdmin(adminVo.getPk_admin_user());
        pcLoginAdminInfoVo.setLoginAdminId(adminVo.getUser_id());
        pcLoginAdminInfoVo.setLoginAdminName(adminVo.getUser_name());
        pcLoginAdminInfoVo.setLoginAdminLevel(adminVo.getUser_level());
        pcLoginAdminInfoVo.setLoginAdminEmail(adminVo.getUser_email());
        pcLoginAdminInfoVo.setLoginAdminMobile(adminVo.getUser_mobile());
        return pcLoginAdminInfoVo;
    }
}
