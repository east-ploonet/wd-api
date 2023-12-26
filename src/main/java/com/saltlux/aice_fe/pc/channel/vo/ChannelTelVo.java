package com.saltlux.aice_fe.pc.channel.vo;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=true)
@Data
public class ChannelTelVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 7641693928583996284L;

    //전화 회선(pk)
    private int pk_tel_line;
    
    //회사(fk)
    private long fk_company;
    
    //'070번호'
    private String fd_phone;
    
    //대표번호1 ex) 1588
    private int fd_phone_num1;
    
    //대표번호2) 1234
    private int fd_phone_num2;
    
    //회사 이름
    private String fd_company_name;
    
    //채널 이름
    private String fd_channel_name;
    
    //승인 상태
    private String fd_approve;

    //사용여부
    private String fd_usage_status;
    
    //발신 전용여부
    private String fd_nationwide;
    
    private LocalDateTime fk_writer;
    
    private LocalDateTime fd_regdate ;
    
    private LocalDateTime fk_modifier;
    
    private LocalDateTime fd_moddate;
    
    
}
