package com.saltlux.aice_fe.pc.channel.vo;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=true)
@Data
public class SnsVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 7641693928583996284L;

    //발신 번호(pk)
    private long pk_tran_managemnet;
    
    //회사(fk)
    private long fk_company;
    
    //'발신번호'
    private String fd_tarn_num;
    
    //발신번호명
    private String fd_tarn_name;
    
    //담당자
    private long fk_company_staff;
    
    //담당자 번호
    private String fd_costumer_phone;
    
    //인증번호
    private String fd_veri_code;
    
    //인증여부 
    private String fd_veri_YN;

    //사용여부
    private String fd_usage_status;
    
    //부서
    private long fd_dept;
    
    //사용용도
    private String fd_use;
       
    private long fk_writer;
    //private LocalDateTime fd_regdate ;
    //private LocalDateTime fk_modifier;
    private LocalDateTime fd_moddate;
    
    
}
