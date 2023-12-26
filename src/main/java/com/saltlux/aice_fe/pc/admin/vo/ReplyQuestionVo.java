package com.saltlux.aice_fe.pc.admin.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class ReplyQuestionVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -4801810269429619350L;

    private Long pk_question;
    private String fd_reply_content;
    private Long fk_reply_writer;
}
