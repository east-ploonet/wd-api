package com.saltlux.aice_fe.file.controller;

import com.saltlux.aice_fe._baseline.baseVo.ResponseVo;
import com.saltlux.aice_fe.file.service.FileUploadService;
import com.saltlux.aice_fe.file.utils.FileUtils;
import com.saltlux.aice_fe.file.vo.UploadFileVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;

@RequestMapping("${apiVersionPrefix}/file")
@RestController
public class FileUploadController {

    @Autowired
    FileUploadService fileService;


    @GetMapping(value = "/data")
    public Object getFileStream(@RequestParam("path") String path) {
        UploadFileVo result = fileService.getFileStream(path);
        return new ResponseVo(200, result);
    }

    @GetMapping(value = "/download")
    public ResponseEntity<byte[]> fileDownload(@RequestParam("path") String path) {
        byte[] result = fileService.download(path);
        String[] split = path.split("/");
        int length = split.length;
        String fileName = split[length-1];
        String fileOrign = fileName.substring(fileName.indexOf("_")+1);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentLength(result.length);
        String[] pathSplit = path.split("/");
        httpHeaders.setContentDispositionFormData("attachment", fileOrign);

        return new ResponseEntity(result, httpHeaders, HttpStatus.OK);
    }

    @GetMapping(value = "/image")
    public byte[] showImage(@RequestParam("path") String path) {
        byte[] result = fileService.download(path);
        return result;
    }



}
