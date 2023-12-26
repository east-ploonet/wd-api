package com.saltlux.aice_fe.pc.question.controller;

import com.saltlux.aice_fe._baseline.baseController.BaseController;
import com.saltlux.aice_fe._baseline.baseService.FileService;
import com.saltlux.aice_fe._baseline.baseVo.ResponseVo;
import com.saltlux.aice_fe.file.service.FileUploadService;
import com.saltlux.aice_fe.file.utils.FileUtils;
import com.saltlux.aice_fe.file.vo.UploadFileVo;
import com.saltlux.aice_fe.pc.question.service.QuestionService;
import com.saltlux.aice_fe.pc.question.vo.QuestionVo;
import com.saltlux.aice_fe.pc.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import javax.mail.MessagingException;

@Slf4j
@RestController
@RequestMapping("${apiVersionPrefix}/workStage/question") // end point : localhost:8080/api/v1/workStage/join
public class QuestionController extends BaseController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserService userService;

    @Autowired
    FileUploadService fileService;

    @GetMapping("/type")
    public Object getQuestionType() {
        Map<String, Object> resultMap = questionService.getType();
        return new ResponseVo(200, resultMap);
    }


    @GetMapping("/user/info")
    public Object getUserInfo() {
        Map<String, Object> resultMap = userService.getUserInfo();
        return new ResponseVo(200, resultMap);
    }

    @GetMapping("/term")
    public Object getTerm() {
        Map<String, Object> resultMap = userService.getQuestionTerm();
        return new ResponseVo(200, resultMap);

    }


    @PostMapping
    public Object registerQuestion(@RequestBody QuestionVo reqBody)
        throws MessagingException, IOException {
        questionService.save(reqBody);
        questionService.sendAlermEmail(reqBody);

        return new ResponseVo(200);
    }


    @GetMapping
    public Object getQuestion(
             @PageableDefault(size = 20, direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false, defaultValue = "") String query) {

        Map<String, Object> resultMap = new HashMap<>();
        Page<QuestionVo> result = questionService.getQuestions(pageable, query);
        resultMap.put("pages", result);
        resultMap.put("size", pageable.getPageSize());
        return new ResponseVo(200, resultMap);
    }

//    @GetMapping("/file/{pk_question}")
//    public Object registInfo(
//            @PathVariable Long pk_question,
//            @RequestParam(value = "uploadFile", required = false) final MultipartFile[] uploadFile
//    ) throws Exception {
//        Map<String, Object> paramMap = new HashMap<>();
//        paramMap.put("uploadFile".trim(), uploadFile);
//        questionService.registerFiles(paramMap, pk_question);
//        return new ResponseVo(200);
//    }

    @PostMapping("/file/upload")
    public Object registerQuestionFile(
            @RequestParam("file") final MultipartFile file
    ) throws Exception {
        String uploadFilePath = FileUtils.getUploadFilePath(FileUtils.FileUploadType.FAQ, file.getOriginalFilename());
        UploadFileVo uploadFileVo = fileService.uploadFileToS3(file, uploadFilePath);
        return new ResponseVo(200, uploadFileVo);
    }

}
