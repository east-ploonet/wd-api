package com.saltlux.aice_fe.pc.notice.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class NoticeFileVo {
    private Long pk_notice_file;            // 파일 pk
    private Long fk_notice;
    private String fd_file_name;       // 파일 명
    private String fd_file_path;       // 파일 경로
    private String fd_file_size;       // 파일 용량
    private String fd_mime_code;       // 파일 종류코드
    private String fd_file_type;       // 파일 타입
}
