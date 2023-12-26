package com.saltlux.aice_fe.file.vo;

import lombok.Data;

@Data
public class UploadFileVo {
    private String fd_file_name;       // 파일 명
    private String fd_file_path;       // 파일 경로
    private long fd_file_size;       // 파일 용량
    private String fd_mime_code;       // 파일 종류코드
    private String fd_file_type;       // 파일 타입
    private String base64;       // 내용

}
