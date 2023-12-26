package com.saltlux.aice_fe.pc.my_page.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateMyPageVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -1260852555493263002L;

    private Long pk_company;

    private Long pk_company_staff;

    private String fd_company_phone;

    private String fd_company_website;

    private String fd_biz_license_num;

    private String fd_address_zipcode;

    private String fd_address_common;

    private String fd_address_detail;

    private String fd_company_logo_file_path;

    private String fd_company_logo_file_name;

    private String fd_biz_license_file_path;

    private String fd_biz_license_file_name;

    //추가
    private Date fd_staff_mobile_verify_dt;

    public String fd_staff_mobile_type;

    public String fd_staff_mobile; // 회원 핸드폰 번호

    public String fd_staff_name; // 회원이름

    public String fd_staff_gender_mf; // 성별

    public String fd_staff_birth; //


    private List<StatisticItemVo> statisticVoList= new ArrayList<>();

}
