package com.saltlux.aice_fe.pc.sms.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class SendConsigneeVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -403032330483720780L;

    long pk_send_consignee;
    String fd_send_data;
    long fk_modifier;
    long fk_writer;
    Date fd_regdate;
    Date fd_moddate;
    
    
}
