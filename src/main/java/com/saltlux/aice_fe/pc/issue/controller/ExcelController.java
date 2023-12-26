package com.saltlux.aice_fe.pc.issue.controller;

import com.saltlux.aice_fe._baseline.baseController.BaseController;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.issue.dto.CompanyCustomerExcel;
import com.saltlux.aice_fe.pc.issue.dto.CompanyStaffExcel;
import com.saltlux.aice_fe.pc.issue.service.ExcelService;
import groovy.util.logging.Log4j2;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.parseMediaType;

@RestController
@RequestMapping("${apiVersionPrefix}/workStage/excel")
@AllArgsConstructor
@Log4j2
public class ExcelController extends BaseController {

    private final String CUSTOMER_FILE_NAME_EXCEL_DATA = "고객전체리스트.xlsx";
    private final String CUSTOMER_FILE_NAME_EXCEL_TEMPLATE = "고객일괄등록양식.xlsx";
    private final String EMPLOYEE_FILE_NAME_EXCEL_DATA = "직원전체리스트.xlsx";
    private final String EMPLOYEE_FILE_NAME_EXCEL_TEMPLATE = "직원일괄등록양식.xlsx";
    private final String STEP3_FILE_NAME_EXCEL = "(플루닛) 부서 및 담당자 등록 파일.xlsx";
    private final String STEP4_FILE_NAME_EXCEL = "장애접수_STEP4_장애현상_유형정보.xlsx";

    private final ExcelService excelService;

    @PostMapping("/upload/customer")
    public ResponseEntity<?> uploadFile(@RequestParam(value = "file") MultipartFile file) {
        PcLoginInfoVo pcLoginInfoVo = getPcLoginInfoVo();

        CompanyCustomerExcel companyCustomerExcel = new CompanyCustomerExcel();
        companyCustomerExcel.setPkLoginStaff(pcLoginInfoVo.getLoginCompanyStaffPk());
        companyCustomerExcel.setPkCompany(pcLoginInfoVo.getLoginCompanyPk());
        String result = excelService.uploadCustomerExcel(file, companyCustomerExcel);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/upload/employee")
    public ResponseEntity<?> uploadFiles(@RequestParam(value = "file") MultipartFile file) {
        PcLoginInfoVo pcLoginInfoVo = getPcLoginInfoVo();
        CompanyStaffExcel companyStaffExcel = new CompanyStaffExcel();
        companyStaffExcel.setPkCompany(pcLoginInfoVo.getLoginCompanyPk());
        companyStaffExcel.setPkLoginStaff(pcLoginInfoVo.getLoginCompanyStaffPk());

        String result = excelService.uploadEmployeeExcel(file, companyStaffExcel);

        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/upload/error")
    public ResponseEntity<?> uploadErrorFiles(
    		@RequestParam(value = "file") MultipartFile file,
    		@RequestParam(value="pkCompanyStaff"     , required=false    ) final Integer pkCompanyStaff) {

        String result = excelService.uploadErrorExcel(file,pkCompanyStaff);
        
        System.out.println("result"+result);

        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/upload/step3")
    public ResponseEntity<?> uploadStep3Files(
    		@RequestParam(value = "file") MultipartFile file,
    		@RequestParam(value="pkCompanyStaff"     , required=false    ) final Integer pkCompanyStaff) {
        String result = excelService.uploadStep3Excel(file, pkCompanyStaff);
        
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/download/form")
    public ResponseEntity<?> downloadForm(HttpServletRequest request) {
        CompanyCustomerExcel companyCustomerExcel = new CompanyCustomerExcel();
        companyCustomerExcel.setPkCompany(getPcLoginInfoVo().getLoginCompanyPk());
        Resource fileResource = excelService.downloadCustomerExcel(CUSTOMER_FILE_NAME_EXCEL_DATA, companyCustomerExcel);

        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(fileResource.getFile().getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        return ResponseEntity.ok()
                .contentType(parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileResource.getFilename() + "\"")
                .body(fileResource);
    }
    
  
    @GetMapping("/download/template/nodata")
    public ResponseEntity<?> downloadCustomerTemplateExcel(HttpServletRequest request) {
        Resource fileResource = excelService.downloadCustomerTemplateExcel(CUSTOMER_FILE_NAME_EXCEL_TEMPLATE);

        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(fileResource.getFile().getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        return ResponseEntity.ok()
                .contentType(parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileResource.getFilename() + "\"")
                .body(fileResource);
    }

    @GetMapping("/download/template")
    public ResponseEntity<?> downloadTemplate(HttpServletRequest request) {
        CompanyStaffExcel companyStaffExcel = new CompanyStaffExcel();
        companyStaffExcel.setPkCompany(getPcLoginInfoVo().getLoginCompanyPk());
        Resource fileResource = excelService.downloadCustomerCompanyTemplate(EMPLOYEE_FILE_NAME_EXCEL_DATA, companyStaffExcel );

        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(fileResource.getFile().getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        return ResponseEntity.ok()
                .contentType(parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileResource.getFilename() + "\"")
                .body(fileResource);
    }
    @GetMapping("/download/excel/nodata")
    public ResponseEntity<?> downloadStaffTemplateExcel(HttpServletRequest request) {
        Resource fileResource = excelService.downloadStaffTemplateExcel(EMPLOYEE_FILE_NAME_EXCEL_TEMPLATE);

        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(fileResource.getFile().getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        return ResponseEntity.ok()
                .contentType(parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileResource.getFilename() + "\"")
                .body(fileResource);
    }
    
    @GetMapping("/download/excel/step3")
    public ResponseEntity<?> downloadStep3Excel(HttpServletRequest request) {

        Resource fileResource = excelService.downloadStep4Excel(STEP3_FILE_NAME_EXCEL);
       
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(fileResource.getFile().getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        return ResponseEntity.ok()
                .contentType(parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileResource.getFilename() + "\"")
                .body(fileResource);
    }
    
    @GetMapping("/download/excel/step4")
    public ResponseEntity<?> downloadStep4Excel(HttpServletRequest request) {

        Resource fileResource = excelService.downloadStep4Excel(STEP4_FILE_NAME_EXCEL);
       
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(fileResource.getFile().getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        return ResponseEntity.ok()
                .contentType(parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileResource.getFilename() + "\"")
                .body(fileResource);
    }

    @GetMapping("/download/ojtExcelTemplate")
    public ResponseEntity<?> ojtExcelTemplate(HttpServletRequest request) {
        String fkStaffWorkCode = request.getParameter("fkStaffWorkCode");
        String fileName = "receptionist_excel.xlsx";
        
        if(fkStaffWorkCode.equals("CTGR1003")) fileName = "receptionist_excel.xlsx";
//        if(fkStaffWorkCode.equals("CTGR1004")) fileName = "hr_excel.xlsx";
        else if(fkStaffWorkCode.equals("CTGR1004")) fileName = "hr_excel.xlsx";
        else if(fkStaffWorkCode.equals("CTGR1006")) fileName = "ir_excel.xlsx";
        else fileName = "faq_excel.xlsx";

        
        Resource fileResource = excelService.downloadTemplate(fileName);
        

        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(fileResource.getFile().getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        return ResponseEntity.ok()
                .contentType(parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileResource.getFilename() + "\"")
                .body(fileResource);
    }

}
