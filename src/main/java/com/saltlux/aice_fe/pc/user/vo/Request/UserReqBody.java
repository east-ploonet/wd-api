package com.saltlux.aice_fe.pc.user.vo.Request;

import com.saltlux.aice_fe.pc.join.vo.StatisticVo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
public class UserReqBody {

    public Long pk_company;

    public Long pk_company_staff;

    public String fd_staff_id;  // 이메일

    public String fd_staff_name; // 회원이름

    public String fd_staff_mobile_type;

    public String fd_staff_ci;

    public String fd_staff_di;

    public String fd_staff_mobile; // 회원 핸드폰 번호

    public String fd_company_id; // 개인 아이디  //company

    public Date fd_staff_mobile_verify_dt; //본인 인증 일시

    public String user_type;



    public String fd_staff_gender_mf; // 성별

    public String fd_staff_birth; //

    public String fd_staff_pw; // 비밀번호

    public String fd_company_logo_file_path; // 개인 심볼

    public String fd_company_logo_file_name; //파일 이름

    public String fd_address_zipcode; // 주소 우편번호

    public String fd_address_common; // 주소 기본

    public String fd_address_detail; // 주소 상세

    public List<StatisticReqBody> statisticVoList;

    public List<TermAgreeReqBody> termsAgree;


}
