package com.saltlux.aice_fe.pc.my_page.vo;

import com.saltlux.aice_fe._baseline.baseVo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class MyPageCompanyVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -645285405481551178L;

    private Long pk_company;

    private String fd_staff_name;

    private String fd_staff_mobile;

    private String fd_staff_duty;

    private String fd_staff_email;

    private String fd_staff_id;

    private String fd_staff_gender_mf;

    private String fd_address_zipcode;

    private String fd_address_common;

    private String fd_address_detail;

    private String fd_company_website;

    private String fd_company_phone;

    private String fd_staff_phone;

    private String fd_company_id;

    private String fd_company_name;

    private String fd_biz_license_num;

    private String fd_biz_license_file_path;

    private String fd_biz_license_file_name;

    private String fd_company_logo_file_path;

    private String fd_company_logo_file_name;

    private String fd_dept_name;



    private List<StatisticItemVo> statisticItemVoList= new ArrayList<>();



}
