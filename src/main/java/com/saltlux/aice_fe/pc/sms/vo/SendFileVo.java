package com.saltlux.aice_fe.pc.sms.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class SendFileVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -403032330483720780L;

    long pk_send_file;
    long fk_send_message;
    long fk_company;
    String fd_send_file_name;
    String fd_send_file_path;
    long fk_writer;
    Date fd_regdate;
    long fk_modifier;
    Date fd_moddate;
    
    
}
