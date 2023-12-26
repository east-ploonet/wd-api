package com.saltlux.aice_fe.file.service;

import com.saltlux.aice_fe.file.vo.UploadFileVo;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

public interface FileUploadService {
    UploadFileVo uploadFileToS3(MultipartFile file, String path);
    UploadFileVo uploadFileS3(Path file, String path);
    
    String getFileInfo();

    UploadFileVo getFileStream(String path);

    byte[] download(String path);
}
