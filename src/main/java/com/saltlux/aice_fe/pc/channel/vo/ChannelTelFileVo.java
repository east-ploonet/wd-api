package com.saltlux.aice_fe.pc.channel.vo;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=true)
@Data
public class ChannelTelFileVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 7643693928583996284L;

    //전화 회선파일(pk)
    private long pk_tel_line_file;
    
    //회사(fk)
    private long fk_company;
    
    //파일이름
    private String fd_file_name;
    
    //확장자명
    private String fd_mime_code;
    
    //파일크기
    private long fd_file_size;
    
    //파일경로
    private String fd_file_path;
    
    //등록자kf
    private int fk_writer;
    
    //등록일시
    private LocalDateTime fd_regdate ;
    
    //수정자fk
    private int fk_modifier;
    
    //수정일시
    private LocalDateTime fd_moddate;
    
    
    
    
    
}
