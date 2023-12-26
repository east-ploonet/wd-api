package com.saltlux.aice_fe.pc.channel.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class ChannelEmailVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -403032330483720780L;

    Integer pk_email_management;
    long fk_company;
    long fk_company_staff;
    String fd_host;
    String fd_account_name;
    String fd_use;
    String fd_usage_status;
    Integer fd_mailing_protocol;
    String fd_email;
    String fd_pw;
    String fd_server;
    String fd_port;
    String fd_smtp_crtfc;
    String fd_smtp_tls;
    String fd_smtp_ssl;
    String fd_pop_ssl;
    String fd_imap_ssl;
    String page;
    
    long fk_modifier;
    long fk_writer;
    Date fd_regdate;
    Date fd_moddate;

    
    
}
