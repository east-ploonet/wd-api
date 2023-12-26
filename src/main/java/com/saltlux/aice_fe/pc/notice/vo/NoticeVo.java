package com.saltlux.aice_fe.pc.notice.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Data
public class NoticeVo implements Serializable {

    private static final long serialVersionUID = -8100988328804239265L;

    private Long pk_notice;
    private Long fk_notice_category;
    private Long fk_writer;
    private String fd_writer_name;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date fd_regdate;
    private Long fk_modifier;
    private String fd_modifier_name;



    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date fd_moddate;
    private String fd_title;
    private String fd_content;

    private Long fd_views;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date fd_release_start_date;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date fd_release_end_date;
    private String fd_hashtag1;
    private String fd_hashtag2;
    private String fd_hashtag3;
    private String fd_show_main_yn;
    private String fd_show_yn;
    private String fd_thumbnail;
    private String thumbnail_file_code;
    private String thumbnail_base64;

    private String user_name;
    
    private Long loginCompanyStaffPk;


    /// category
    private Long pk_notice_category;
    private String fd_category_name;


    //file

    List<NoticeFileVo> fileVos=new ArrayList<>();
}
