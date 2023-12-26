package com.saltlux.aice_fe.pc.question.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 918419219509423908L;

    private Long pk_question_type;

    private Long pk_question;

    private Long fk_company_staff;

    public String fd_name;

    public String fd_mobile;

    public String fd_email;

    private String fd_content;

    private String fd_title;

    private Long fk_question_type;

    private Long fk_terms;

    private String fd_agree_yn;

    private String fd_reply_yn;

    private String fd_reply_content;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date fd_regdate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date fd_reply_regdate;

    private String fd_question_name;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date fd_agree_date;

    private String user_name;

    private List<QuestionFileVo> fileVos = new ArrayList<>();
}
