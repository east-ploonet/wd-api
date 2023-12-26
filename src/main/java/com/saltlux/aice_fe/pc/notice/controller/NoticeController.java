package com.saltlux.aice_fe.pc.notice.controller;

import com.saltlux.aice_fe._baseline.baseVo.ResponseVo;
import com.saltlux.aice_fe.pc.notice.service.NoticeService;
import com.saltlux.aice_fe.pc.notice.vo.NoticeFileVo;
import com.saltlux.aice_fe.pc.notice.vo.NoticeVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("${apiVersionPrefix}/workStage/notice") // end point : localhost:8080/api/v1/workStage/join
public class NoticeController {

    @Autowired
    private NoticeService noticeService;



    @GetMapping("/category")
    public Object getNoticeCategory() {
        Map<String, Object> resultMap = noticeService.findNoticeCategory();
        return new ResponseVo(200, resultMap);
    }



    @GetMapping("")
    public Object getStartNotice(
            @PageableDefault(size = 20, direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false, defaultValue = "") String query
    ) {
        Map<String, Object> resultMap = new HashMap<>();
        Page<NoticeVo> result = noticeService.getNotice(pageable, query);
        resultMap.put("pages", result);
        resultMap.put("size", result.getSize());
        return new ResponseVo(200, result);
    }

    @GetMapping("/{pk_notice}")
    public Object getNotice(
            @PathVariable Long pk_notice
    ) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap = noticeService.getDetailNotice(pk_notice);
        return new ResponseVo(200, resultMap);
    }



}
