package com.saltlux.aice_fe.pc.sms.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class SmsTranVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -403032330483720780L;

    long pk_send_message;
    long fk_message_temp;
    long fk_email_management;
    long fk_tel_line;
    long fk_campaign;
    long fk_type;
    String fd_send_file_name;
    String fd_send_file_path;
    String fd_message_flag;
    String fd_message_title;
    String fd_message_content;
    
    long fk_modifier;
    long fk_writer;
    Date fd_regdate;
    Date fd_moddate;
    
    
}
