package com.saltlux.aice_fe.pc.new_ojt.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class NewOjt2StepVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -403032330483720780L;

    long fk_company;							// 회사fk
    long fk_comapny_staff_ai;					// AI직원fk
    String ai_work_cd;							// CTGR3xxx
    String p_ai_work_cd;						// CTGR2xxx, CTGR3xxx
    String enable_yn;							// 설정실행여부 (step1,step2) - 추후사용 예정
    String use_yn;								// 삭제 시 사용(추후예졍)
    String member_name;							// 이름
    String member_phone;						// 유선번호
    String member_mobile;						// 휴대폰전화번호
    
    String front_status;
    String bot_status;

    String term_unit;							// 체크기간 (H:시간, D:일, M:월, m:분)
    int term_val;								// 체크기간 값 : 1시간, 24시간, 30일
    String task_val;							// 기타 설정값(url,aws file path, 키워드, 담당자연결멘트 둥)
    long fk_writer ; 							// [직원]등록자 fk
    long fk_modifier ; 							// [직원]수정자 fk

    //tbl_company (set)
    String solution_type;						//솔루션타입
    String user_type ;							//사용자타입	
    String fd_company_status_code;				//회사 계정 상태 코드
    
    //주소
    String fd_address_zipcode;					//주소 우편번호
    String fd_address_common;					//주소 기본
    String fd_address_detail;					//주소 상세
    String company_parking_msg;					//주차 시설 입력 
    
    //안내데스크 엑셀 업로드 이름
    String path_file;
    
    
    
    
}
