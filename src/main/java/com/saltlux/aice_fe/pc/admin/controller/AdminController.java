package com.saltlux.aice_fe.pc.admin.controller;

import com.saltlux.aice_fe._baseline.baseController.BaseController;
import com.saltlux.aice_fe._baseline.baseVo.ResponseVo;
import com.saltlux.aice_fe.file.service.FileUploadService;
import com.saltlux.aice_fe.file.utils.FileUtils;
import com.saltlux.aice_fe.file.vo.UploadFileVo;
import com.saltlux.aice_fe.pc.admin.service.AdminService;
import com.saltlux.aice_fe.pc.admin.vo.AdminVo;
import com.saltlux.aice_fe.pc.admin.vo.ReplyQuestionVo;
import com.saltlux.aice_fe.pc.admin.vo.req.DeleteAdminReq;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginAdminInfoVo;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.notice.service.NoticeService;
import com.saltlux.aice_fe.pc.notice.vo.NoticeVo;
import com.saltlux.aice_fe.pc.question.service.QuestionService;
import com.saltlux.aice_fe.pc.question.vo.QuestionVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("${apiVersionPrefix}/workStage/admin") // end point : localhost:8080/api/v1/workStage/join
public class AdminController extends BaseController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private QuestionService questionService;


    @Autowired
    FileUploadService fileService;

//    @PostMapping("/save")
//    public Object saveAdmin(@RequestBody AdminVo adminVo) {
//        PcLoginAdminInfoVo pcLoginInfoVo = getPcLoginAdminInfoVo();
//        if (pcLoginInfoVo == null) {
//            return new ResponseVo(403);
//        }
//        adminService.saveAdmin(adminVo);
//
//        return new ResponseVo(200);
//    }


    @PostMapping("/login")
    public Object LoginAdmin(@RequestBody Map<String, Object> bodyMap) throws Exception {
        throwException.requestBodyRequied(bodyMap, "admin_id", "admin_pw");
        ResponseVo responseVo = null;
        responseVo = adminService.loginAdmin(bodyMap);

        if (200 != responseVo.getStatus()) {
            return responseVo;
        }


        //세션 생성
        request.getSession().invalidate(); //모든 세션 제거
        request.getSession().removeAttribute("pcAuthToken");
        request.getSession().removeAttribute("pcLoginInfo");

        String pcAuthToken = responseVo.getBody().get("authToken").toString();
        Object pcLoginInfo = responseVo.getBody().get("loginInfo");

        request.getSession().setAttribute("pcAuthToken"   , pcAuthToken);
        request.getSession().setAttribute("pcAdminInfo"   , pcLoginInfo);
//	    request.getSession().setMaxInactiveInterval(tokenExpired);

        Map<String, Object> results = new HashMap<>();
        results.put("pcAuthToken", pcAuthToken);
        results.put("pcAdminInfo", pcLoginInfo);

        return new ResponseVo(200, results);
    }

    @GetMapping("/question")
    public Object getQuestionAdminList(
            @PageableDefault(size = 20, direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(required = false, defaultValue = "") String type
    ) throws Exception {
        PcLoginAdminInfoVo pcLoginInfoVo = getPcLoginAdminInfoVo();
        if (pcLoginInfoVo == null) {
            return new ResponseVo(403);
        }
        Map<String, Object> resultMap = new HashMap<>();
        Page<QuestionVo> result = adminService.getQuestionsAdmin(pageable, query, type);
        resultMap.put("pages", result);
        resultMap.put("size", pageable.getPageSize());

        return new ResponseVo(200, resultMap);
    }

    @GetMapping("/question/{pk_question}")
    public Object getQuestionAdmin(
            @PathVariable Long pk_question
    ) {
        PcLoginAdminInfoVo pcLoginInfoVo = getPcLoginAdminInfoVo();
        if (pcLoginInfoVo == null) {
            return new ResponseVo(403);
        }
        QuestionVo result = adminService.getQuestionDetail(pk_question);
        return new ResponseVo(200, result);
    }

    @DeleteMapping("/question")
    public Object deleteQuestion(
            @RequestBody DeleteAdminReq req) {
        if (req.getQuestionIdList().get(0)==null || req.getQuestionIdList().size()<1){
            return new ResponseVo(400);
        }
        PcLoginAdminInfoVo pcLoginInfoVo = getPcLoginAdminInfoVo();
        if (pcLoginInfoVo == null) {
            return new ResponseVo(403);
        }
        adminService.deleteQuestion(req);
        return new ResponseVo(200);
    }

    @PostMapping("/question/reply")
    public Object qetQuestionAdmin(@RequestBody ReplyQuestionVo req) throws MessagingException, IOException {

        PcLoginAdminInfoVo pcLoginInfoVo = getPcLoginAdminInfoVo();
        if (pcLoginInfoVo == null) {
            return new ResponseVo(403);
        }
        req.setFk_reply_writer(1L);
        Map<String, Object> resultMap = adminService.registerQuestionReply(req);

        return new ResponseVo(200, resultMap);

    }

    @GetMapping("/notice")
    public Object getNotice(@PageableDefault(size = 20, direction = Sort.Direction.DESC) Pageable pageable,
                            @RequestParam(required = false, defaultValue = "") String query,
                            @RequestParam(required = false, defaultValue = "") String type) {
        PcLoginAdminInfoVo pcLoginInfoVo = getPcLoginAdminInfoVo();
        if (pcLoginInfoVo == null) {
            return new ResponseVo(403);
        }
        Map<String, Object> resultMap = new HashMap<>();
        Page<NoticeVo> result = adminService.getNotice(pageable, query, type);
        resultMap.put("pages", result);
        resultMap.put("size", pageable.getPageSize());
        return new ResponseVo(200, resultMap);
    }

    @PostMapping("/notice")
    public Object registerNotice(@RequestBody NoticeVo noticeVo) throws ParseException {
        PcLoginAdminInfoVo pcLoginInfoVo = getPcLoginAdminInfoVo();
        if (pcLoginInfoVo == null) {
            return new ResponseVo(403);
        }
        adminService.save(noticeVo, pcLoginInfoVo);
        return new ResponseVo(200);
    }

    @PutMapping("/notice")
    public Object updateNotice(@RequestBody NoticeVo noticeVo) {
        PcLoginAdminInfoVo pcLoginInfoVo = getPcLoginAdminInfoVo();
        if (pcLoginInfoVo == null) {
            return new ResponseVo(403);
        }
        noticeVo.setFk_modifier(pcLoginInfoVo.getPkAdmin());
        Map<String, Object> resultMap = adminService.updateNotice(noticeVo);
        return new ResponseVo(200, resultMap);
    }

    @GetMapping("/notice/{pk_notice}")
    public Object detailNotice(@PathVariable Long pk_notice) {
        PcLoginAdminInfoVo pcLoginInfoVo = getPcLoginAdminInfoVo();
        if (pcLoginInfoVo == null) {
            return new ResponseVo(403);
        }

        NoticeVo resultMap = adminService.getNoticeDetail(pk_notice);
        return new ResponseVo(200, resultMap);
    }

    @DeleteMapping("/notice")
    public Object deleteNotice(
            @RequestBody DeleteAdminReq req) {
        PcLoginAdminInfoVo pcLoginInfoVo = getPcLoginAdminInfoVo();
        if (pcLoginInfoVo == null) {
            return new ResponseVo(403);
        }
        adminService.deleteNotice(req);
        return new ResponseVo(200);
    }


    @GetMapping("/notice/category")
    public Object getNoticeCategory() {
        PcLoginAdminInfoVo pcLoginInfoVo = getPcLoginAdminInfoVo();
        if (pcLoginInfoVo == null) {
            return new ResponseVo(403);
        }
        Map<String, Object> resultMap = noticeService.findNoticeCategory();
        return new ResponseVo(200, resultMap);
    }

    @PostMapping("/notice/file/upload")
    public Object registerNoticeFile(
            @RequestParam("file") final MultipartFile file
    ) throws Exception {
        String uploadFilePath = FileUtils.getUploadFilePath(FileUtils.FileUploadType.NOTICE, file.getOriginalFilename());
        UploadFileVo uploadFileVo = fileService.uploadFileToS3(file, uploadFilePath);
        return new ResponseVo(200, uploadFileVo);
    }


    @GetMapping("/question/type")
    public Object getQuestionType() {
        PcLoginAdminInfoVo pcLoginInfoVo = getPcLoginAdminInfoVo();
        if (pcLoginInfoVo == null) {
            return new ResponseVo(403);
        }
        Map<String, Object> resultMap = questionService.getType();
        return new ResponseVo(200, resultMap);
    }

//    @GetMapping("/notice/file/{fk_notice}")
//    public Object registInfo(
//            @PathVariable Long fk_notice,
//            @RequestParam(value = "uploadImageFile", required = false) final MultipartFile[] uploadImageFile,
//            @RequestParam(value = "uploadFile", required = false) final MultipartFile[] uploadFile
//    ) throws Exception {
//        Map<String, Object> paramMap = new HashMap<>();
//        paramMap.put("uploadFile".trim(), uploadFile);
//        paramMap.put("uploadImageFile".trim(), uploadImageFile);
//        noticeService.registerFiles(paramMap, fk_notice);
//        return new ResponseVo(200);
//    }


}