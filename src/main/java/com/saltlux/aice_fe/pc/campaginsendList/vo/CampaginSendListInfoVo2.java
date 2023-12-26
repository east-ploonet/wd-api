package com.saltlux.aice_fe.pc.campaginsendList.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class CampaginSendListInfoVo2 extends BaseVo implements Serializable {

	private static final long serialVersionUID = -403032330483720780L;

	long pk_company_customer;
    long fk_company;							// 회사pk
    String fk_send_info ; 						// 발신리스트(fk)
    String fd_send_message_name ;				// 발신리스트 명
    String fd_message_content ; 				// 설명
    String fd_send_file_name ; 					// 엑셀 파일이름
    String fd_send_file_path ; 					// 엑셀 파일 경로
    String fd_customer_name ; 					// 고객이름
    String fd_cell_phone ; 						// 고객 휴대전화
    String fd_phone ; 							// 고객 일반전화
    String fd_email_address ; 					// 고객 이메일
    String fd_department ; 						// 소속회사
    long fk_writer ; 							// [직원]등록자 fk
    long fk_modifier ; 							// [직원]수정자 fk
    Date fd_regdate;
    Date fd_moddate;

    
    
}
