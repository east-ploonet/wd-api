package com.saltlux.aice_fe.pc.notice.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Data
@EqualsAndHashCode(callSuper = false)
public class NoticeDetailVo {
    private Long pk_notice;
    private Long fk_notice_category;
    private Long fk_writer;
    private Date fd_regdate;
    private Long fk_modifier;
    private Date fd_moddate;
    private String fd_title;
    private String fd_content;
    private Date fd_release_start_date;
    private Date fd_release_end_date;
    private String fd_hashtag1;
    private String fd_hashtag2;
    private String fd_hashtag3;
    private String fd_show_main_yn;
    private String fd_show_yn;
    private String fd_thumbnail;
    List<NoticeFileVo> fileVos=new ArrayList<>();

    private Long beforeId;

    private Long frontId;

}
