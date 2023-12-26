package com.saltlux.aice_fe.pc.channel.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class ChannelTranFileVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -403032330483720780L;

    long pk_tran_file;
    long fk_tran_management;
    long fk_company;
    String fd_file_name;
    String fd_file_size;
    String fd_file_path;
    
    long fk_modifier;
    long fk_writer;
    
}
