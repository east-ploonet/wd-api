package com.saltlux.aice_fe.pc.campaginsendList.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class CampaginSendInfoVo extends BaseVo implements Serializable {

	private static final long serialVersionUID = -403032330483720780L;

    long fk_company;							// 회사pk
    String pk_send_info;						// 발신리스트(pk) 
    String fd_send_message_name ;				// 발신리스트 명
    String fd_message_content ; 				// 설명
    int fd_customer_count;						// 발신회원 수(대상)
    String fd_list_share ; 						// 리스트 공유 여부
    long fk_writer ; 							// [직원]등록자 fk
    long fk_modifier ; 							// [직원]수정자 fk
    Date fd_regdate;
    Date fd_moddate;

    
    
}
