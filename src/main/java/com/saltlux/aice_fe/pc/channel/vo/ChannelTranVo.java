package com.saltlux.aice_fe.pc.channel.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class ChannelTranVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -403032330483720780L;

    long pk_tran_management;
    long fk_company;
    String fd_tarn_num;
    String user_type;
    String fd_tarn_name;
    String fd_costumer_phone;
    String fd_veri_code;
    String fd_veri_YN;
    String fd_use;
    String fd_nationwide;
    String fd_usageStatus;
    String doc_main_status;			//대표번호신청서 상태
    String file_path_main_num;		//대표번호신청서
    String reg_status; 				//승인여부
    
    String page;
    
    long fk_modifier;
    long fk_writer;
    Date fd_regdate;
    Date fd_moddate;
    
    
}
