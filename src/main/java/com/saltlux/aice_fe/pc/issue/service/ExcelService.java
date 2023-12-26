package com.saltlux.aice_fe.pc.issue.service;

import com.saltlux.aice_fe.pc.issue.dto.CompanyStaffExcel;
import com.saltlux.aice_fe.pc.issue.vo.CompanyCustomerVo;
import com.saltlux.aice_fe.pc.issue.vo.ExcelVo;
import com.saltlux.aice_fe.pc.issue.dto.CompanyCustomerExcel;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceEditor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface ExcelService {

    Resource downloadCustomerCompanyTemplate(String nameTemplate, CompanyStaffExcel companyStaffExcel);
    Resource downloadTemplate(String nameTemplate);

    String uploadCustomerExcel(MultipartFile file, CompanyCustomerExcel companyCustomerExcel);

    String uploadEmployeeExcel(MultipartFile file, CompanyStaffExcel companyStaffExcel);
    
    String uploadErrorExcel(MultipartFile file, long pkCompanyStaff);
    
    String uploadStep3Excel(MultipartFile file, long pkCompanyStaff);

    Resource downloadCustomerExcel(String nameTemplate, CompanyCustomerExcel companyCustomerExcel);

    Resource downloadCustomerTemplateExcel(String nameTemplate );
    
    Resource downloadErrorTemplateExcel(String nameTemplate );
    

    Resource downloadStaffTemplateExcel(String nameTemplate);
    
    
    
    Resource downloadExcelByJson(String nameTemplate, JSONArray jsonArray, List<ExcelVo> columnList);
    
    
    
    //Step4 다운로드
    Resource downloadStep4Excel(String nameTemplate);
}
