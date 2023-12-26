package com.saltlux.aice_fe.pc.question.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionTypeVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 918419219509423908L;

    private Long pk_question_type;

    private String fd_question_name;

}
