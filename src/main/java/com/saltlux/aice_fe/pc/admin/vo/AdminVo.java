package com.saltlux.aice_fe.pc.admin.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class AdminVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 4035462380652037682L;

    private Long pk_admin_user;

    private String user_id;

    private String user_pw;

    private String user_name;

    private int user_level;

    private String use_yn;

    private String user_email;

    private String user_mobile;

    private String memo;

    private Long fk_writer;

    private LocalDateTime fd_regdate;

    private Long fk_modifier;

    private LocalDateTime fd_moddate;

}
