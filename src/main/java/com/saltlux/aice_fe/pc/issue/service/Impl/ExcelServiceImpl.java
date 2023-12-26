package com.saltlux.aice_fe.pc.issue.service.Impl;

import com.saltlux.aice_fe._baseline.baseService.FileService;
import com.saltlux.aice_fe._baseline.baseService.impl.BaseServiceImpl;
import com.saltlux.aice_fe._baseline.baseVo.FileVo;
import com.saltlux.aice_fe._baseline.commons.Common;
import com.saltlux.aice_fe.commonCode.dao.CodeDao;
import com.saltlux.aice_fe.commonCode.vo.CodeVo;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.issue.dao.CompanyCustomerDao;
import com.saltlux.aice_fe.pc.issue.dto.CompanyCustomerExcel;
import com.saltlux.aice_fe.pc.issue.dto.CompanyStaffExcel;
import com.saltlux.aice_fe.pc.issue.service.CompanyCustomerService;
import com.saltlux.aice_fe.pc.issue.service.ExcelService;
import com.saltlux.aice_fe.pc.issue.util.StorageProperties;
import com.saltlux.aice_fe.pc.issue.vo.CompanyCustomerVo;
import com.saltlux.aice_fe.pc.issue.vo.ExcelVo;
import com.saltlux.aice_fe.pc.join.vo.CompanyDeptStaffVo;
import com.saltlux.aice_fe.pc.join.vo.CompanyDeptVo;
import com.saltlux.aice_fe.pc.join.vo.CompanyStaffVo;
import com.saltlux.aice_fe.pc.new_ojt.dao.NewOjtDao;
import com.saltlux.aice_fe.pc.new_ojt.vo.NewOjt2StepVo;
import com.saltlux.aice_fe.pc.staff.dao.StaffDao;
import com.saltlux.aice_fe.pc.staff.service.AIStaffService;
import com.saltlux.aice_fe.pc.staff.service.StaffService;

import ch.qos.logback.core.recovery.ResilientSyslogOutputStream;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
@Log4j2
public class ExcelServiceImpl extends BaseServiceImpl implements ExcelService {

    private final String PREFIX_DOWNLOAD_NAME = "download_";
    private final String DOWNLOAD_FOLDER = "download";
    private final String PREFIX_COPY_FILE_NAME = "copy_";
    private final Path rootLocation;
    private final CompanyCustomerDao companyCustomerDao;
    private final StaffDao staffDao;
    private final CodeDao codeDao;
    private final StaffService staffService;
    @Autowired
	private FileService fileService;
    
    @Autowired
	private AIStaffService aiStaffService;
    
    @Autowired
	private NewOjtDao newOjtDao;
    
    private final Integer[] COLS_REQUIRED_EMPLOYEE = new Integer[]{0, 4, 7, 8};

    @Autowired
    public ExcelServiceImpl(StorageProperties storageProperties, CompanyCustomerService customerService, CompanyCustomerDao companyCustomerDao,
                            StaffDao staffDao, CodeDao codeDao, StaffService staffService) {
        this.rootLocation = Paths.get(storageProperties.getLocation());
        this.companyCustomerDao = companyCustomerDao;
        this.staffDao = staffDao;
        this.codeDao = codeDao;
        this.staffService = staffService;
        try {
            Files.createDirectories(this.rootLocation);
        } catch (IOException e) {
            log.error("Could not create the directory where the uploaded files will be stored.", e);
        }
    }


    @Override
    public Resource downloadCustomerCompanyTemplate(String nameTemplate, CompanyStaffExcel companyStaffExcel) {

        try {
            String nameFileNew = PREFIX_DOWNLOAD_NAME + nameTemplate;
            String nameFileCopy = PREFIX_COPY_FILE_NAME + nameTemplate;
            Path pathFileOrigin = rootLocation.resolve(nameTemplate).normalize();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd");
            if (!Files.exists(rootLocation.resolve(DOWNLOAD_FOLDER))) {
                new File(rootLocation.resolve(DOWNLOAD_FOLDER).toString()).mkdir();
            }
            Path pathFileNew = rootLocation.resolve(DOWNLOAD_FOLDER + "/" + nameFileNew).normalize();
            Path pathFileCopy = rootLocation.resolve(DOWNLOAD_FOLDER + "/" + nameFileCopy).normalize();
            // delete older file if it exist
            Files.deleteIfExists(pathFileNew);
            Files.deleteIfExists(pathFileCopy);
            Files.copy(pathFileOrigin, pathFileNew);

            XSSFWorkbook wb = new XSSFWorkbook(new File(pathFileNew.toString()));
            XSSFSheet worksheet = wb.getSheetAt(0);

            CompanyStaffVo companyStaffVoReq = new CompanyStaffVo();
            companyStaffVoReq.getSearch().setPageSize(0);
            companyStaffVoReq.setFk_company(companyStaffExcel.getPkCompany());
            List<CompanyStaffVo> companyStaffVos = staffDao.getStaffList(companyStaffVoReq);

            for(int i = 0; i <companyStaffVos.size(); i++) {
                CompanyStaffVo companyStaffVo = companyStaffVos.get(i);
                Row row = worksheet.createRow(i + 1);
                row.createCell(0).setCellValue(companyStaffVo.getPk_company_staff());
                row.createCell(1).setCellValue(companyStaffVo.getFd_staff_name());
                row.createCell(2).setCellValue(companyStaffVo.getFd_staff_duty());
                row.createCell(3).setCellValue(companyStaffVo.getFd_dept_name());
                row.createCell(4).setCellValue(companyStaffVo.getFd_company_master_yn());
                row.createCell(5).setCellValue(companyStaffVo.getFd_staff_status());
                row.createCell(6).setCellValue(companyStaffVo.getFd_staff_level());
                row.createCell(7).setCellValue(companyStaffVo.getFd_staff_phone());
                row.createCell(8).setCellValue(companyStaffVo.getFd_staff_mobile());
                row.createCell(9).setCellValue(companyStaffVo.getFd_staff_email());
//                row.createCell(10).setCellValue(companyStaffVo.getFd_writer_name());
                row.createCell(10).setCellValue(simpleDateFormat.format(companyStaffVo.getFd_regdate()));
            }
            FileOutputStream output_file = new FileOutputStream(new File(pathFileCopy.toString()));
            //write changes
            wb.write(output_file);
            output_file.close();
            wb.close();
            Resource resource = new UrlResource(pathFileNew.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read file ");
            }
        } catch (MalformedURLException | FileNotFoundException e) {
            throw new RuntimeException("Could not retrieve file ");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Resource downloadTemplate(String nameTemplate) {
        try {
            Path file = rootLocation.resolve(nameTemplate).normalize();
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read file ");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Could not retrieve file ");
        }

    }

    @Override
    public String uploadCustomerExcel(MultipartFile file, CompanyCustomerExcel companyCustomerExcel) {
        String message = "";
        String fileName[] = file.getOriginalFilename().split("\\.");
        if (fileName[1].equalsIgnoreCase("xls") || fileName[1].equalsIgnoreCase("xlsx")) {
            try {
                int nCountSuccess = 0;
                int nCountFailed = 0;
                XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
                XSSFSheet workSheet = workbook.getSheetAt(0);
              
                for (int i = 1; i < workSheet.getPhysicalNumberOfRows(); i++) {
                    XSSFRow row = workSheet.getRow(i);
                    if (null == row.getCell(0) || null == row.getCell(5)) {
                    	nCountFailed++;
                        continue;
                    }
                   
                    String customerName = row.getCell(0).getStringCellValue();
                    String customerPhone = "";
                    String customerCellMobile = "";
                    //String customerEmail = row.getCell(6).getStringCellValue();
                    
                    //회사대표번호 체크
                    if(row.getCell(4) !=null) {
                    	try {
                    		customerPhone = row.getCell(4).getStringCellValue().replaceAll("[@.$^-]", "");                    		
                    		if(!customerPhone.matches(".*[0-9].*")) {
                    			nCountFailed++;
                    			continue;
                    		}
                    	}catch (IllegalStateException e) {
                    		System.out.println("e : "+e);
                    		customerPhone = Integer.toString((int)row.getCell(4).getNumericCellValue());
						}
                    }
                    
                    //휴대폰 번호 체크
                    if(row.getCell(5) !=null) {
                    	try {
                    		customerCellMobile = row.getCell(5).getStringCellValue().replaceAll("[@.$^-]", "");                    		
                    		if(!customerCellMobile.matches(".*[0-9].*")) {
                    			nCountFailed++;
                    			continue;
                    		}
                    	}catch (IllegalStateException e) {
                    		System.out.println("e : "+e);
                    		customerCellMobile = Integer.toString((int)row.getCell(4).getNumericCellValue());
						}
                    }
                     
                    CompanyCustomerVo companyCustomerVo = new CompanyCustomerVo();
                    companyCustomerVo.setFd_customer_name(customerName);
                    companyCustomerVo.setFd_company_name(null != row.getCell(1) ? row.getCell(1).getStringCellValue() : "");
                    companyCustomerVo.setFd_company_dept(null != row.getCell(2) ? row.getCell(2).getStringCellValue() : "Sales");
                    companyCustomerVo.setFd_company_position(null != row.getCell(3) ? row.getCell(3).getStringCellValue() : "");
                    //companyCustomerVo.setFd_customer_phone(null != row.getCell(4) ? row.getCell(4).getStringCellValue().replaceAll(" [@.$^-]", " ") : "");
                    companyCustomerVo.setFd_customer_phone(customerPhone);
                    companyCustomerVo.setFd_customer_mobile(customerCellMobile);
                    companyCustomerVo.setFd_customer_email(null != row.getCell(6) ? row.getCell(6).getStringCellValue() : "" );
                    companyCustomerVo.setFd_additional_information(null != row.getCell(7) ? row.getCell(7).getStringCellValue() : "");
                    // set default value
                    companyCustomerVo.setFd_active_state("A2201");
                    companyCustomerVo.setFk_writer(companyCustomerExcel.getPkLoginStaff());
                    companyCustomerVo.setFk_company(companyCustomerExcel.getPkCompany());
                    companyCustomerVo.setFk_modifier(companyCustomerExcel.getPkLoginStaff());
                    companyCustomerVo.setContact_channel_from("A1502");
                    companyCustomerVo.setContact_date_from(new Date());
                    companyCustomerVo.setFd_regdate(new Date());

                    if (companyCustomerDao.insertCompanyCustomer(companyCustomerVo) == 1) {
                        nCountSuccess++;
                    } else {
                        nCountFailed++;
                    }
                }

                message += "성공 " + nCountSuccess + ", 실패 " + nCountFailed;
            } catch (IOException e) {
                message = "업로드에 실패했습니다. 입력사항을 확인해주세요.";
            }
        } else {
            message = "파일 형식을 확인해주세요.";
        }
        return message;
    }
    
    @Override
    public String uploadStep3Excel(MultipartFile file,  long pkCompanyStaff) {
        String message = "";
        String fileName[] = file.getOriginalFilename().split("\\.");
        String password = "";
        String confirmPassword = "";
        PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
        boolean uploadCheck = true;

        if (fileName[1].equalsIgnoreCase("xls") || fileName[1].equalsIgnoreCase("xlsx")) {
        	
            try {
//            	try {
//					List<FileVo> fileVoList = fileService.uploadFileToVoList( file, "departAdmin" );
//				} catch (Exception e1) {
//					System.out.println("e1:" + "파일업로드 오류");
//					
//				}
            	
                int nCountSuccess = 0;
                int nCountFailed = 0;
                XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
                XSSFSheet workSheet = workbook.getSheetAt(0);
                CompanyStaffVo companyStaffVo = new CompanyStaffVo();
                
                for (int i = 1; i < workSheet.getPhysicalNumberOfRows(); i++) {
                	XSSFRow row = workSheet.getRow(i);
                	
                	try {
                		// 필수값체크
                		if(null == row.getCell(0) || null == row.getCell(4)
                				|| row.getCell(0).getStringCellValue()==""
                				|| row.getCell(4).getStringCellValue()==""
                				) {
                			nCountFailed++;
                			continue;
                		}                		
                	}catch (Exception e) {
                		uploadCheck = false;
                		message = "파일 형식을 확인해주세요.";
                		break;
					}

        			String fdStaffName = row.getCell(0).getStringCellValue(); //이름(필수)
//        			String fdStaffDuty = row.getCell(1).getStringCellValue(); //직책
//        			String deptDispName = row.getCell(2).getStringCellValue(); //부서
//        			String fdStaffPhone = row.getCell(3).getStringCellValue(); //회사내선번호
        			String fdStaffMobile = row.getCell(4).getStringCellValue().replaceAll("[@.$^-]", "");//휴대폰번호(필수)
        			
        			//이름 체크 -> 특수 문자 포함 여부
                    if(!fdStaffName.matches("[0-9|a-z|A-Z|ㄱ-ㅎ|ㅏ-ㅣ|가-힝|(|)|.|-]*")) {
                    	nCountFailed++;
                    	continue;
                    }

                    //휴대폰 번호 체크
                    if(!fdStaffMobile.matches(".*[0-9].*")) {
                    	nCountFailed++;
                    	continue;
                    }
       			        			
        			companyStaffVo.setSolution_type("B11");
        			companyStaffVo.setUser_type(loginInfoVo.getLoginUserType());
        			companyStaffVo.setIs_change_password(0);
        			companyStaffVo.setFk_company(loginInfoVo.getLoginCompanyPk());
        			companyStaffVo.setFd_staff_level_code("A1004");
        			companyStaffVo.setFd_staff_id(fdStaffMobile+"@ploonet.com");
        			companyStaffVo.setFd_staff_ai_yn("N");
        			companyStaffVo.setFd_staff_status_code("A1101");
        			companyStaffVo.setFd_staff_response_status_code("A1201");
        			companyStaffVo.setFd_company_master_yn("N");
        			companyStaffVo.setFd_default_ai("N");
        			companyStaffVo.setFd_staff_name(fdStaffName);
        			companyStaffVo.setFd_staff_duty(null != row.getCell(1) ? row.getCell(1).getStringCellValue() : null);
        			companyStaffVo.setDept_disp_name(null != row.getCell(2) ? row.getCell(2).getStringCellValue() : null);
        			companyStaffVo.setFd_staff_phone(null != row.getCell(3) ? row.getCell(3).getStringCellValue() : null);
        			companyStaffVo.setFd_staff_mobile(fdStaffMobile);
        			companyStaffVo.setFd_sign_up_path_code("A3013");
        			try {
        				staffDao.insertCompanyStaff(companyStaffVo);
        				nCountSuccess++;
        			} catch (SQLException e) {
        				message = "관리자에 문의해주세요.";
        			}                			
                }
                if(uploadCheck && nCountFailed == 0) {
                	message += "성공 " + nCountSuccess + ", 실패 " + nCountFailed + "\n업로드 성공하였습니다.";                	
                }else {
                	message += "성공 " + nCountSuccess + ", 실패 " + nCountFailed;
                }
            } catch (IOException e) {
                message = "업로드에 실패했습니다. 입력사항을 확인해주세요.";
                System.out.println("오류 메세지"+e);
            }
        } else {
            message = "파일 형식을 확인해주세요.";
        }
        return message;
    }


    @Override
    public String uploadEmployeeExcel(MultipartFile file, CompanyStaffExcel companyStaffExcel) {
        String message = "";
        String fileName[] = file.getOriginalFilename().split("\\.");
        String password = "";
        String confirmPassword = "";

        if (fileName[1].equalsIgnoreCase("xls") || fileName[1].equalsIgnoreCase("xlsx")) {
            try {
                int nCountSuccess = 0;
                int nCountFailed = 0;
                XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
                XSSFSheet workSheet = workbook.getSheetAt(0);
                CompanyStaffVo companyStaffVo = new CompanyStaffVo();

                for (int i = 1; i < workSheet.getPhysicalNumberOfRows(); i++) {
                    XSSFRow row = workSheet.getRow(i);
                    int countColInvalid = 0;
                    for (int cellCol : COLS_REQUIRED_EMPLOYEE) {
                        if (null == row.getCell(cellCol)) {
                            countColInvalid += 1;
                        } else {
                            String colVal = row.getCell(cellCol).getStringCellValue();
                            if (!StringUtils.isNotBlank(colVal)) {
                                countColInvalid += 1;
                            }
                        }
                    }

                    // if name, permission, Cellphone NO or Email address is null or empty -> continue
                    if (countColInvalid > 0) {
                        continue;
                    }
                    // find department by name
                    if (null != row.getCell(2)) {
                        String departmentName = row.getCell(2).getStringCellValue();
                        if (StringUtils.isNotBlank(departmentName)) {
                            CompanyDeptVo companyDeptVo = new CompanyDeptVo();
                            companyDeptVo.setFk_company(companyStaffExcel.getPkCompany());
                            List<CompanyDeptVo> departments = staffDao.selectStaffDeptList(companyDeptVo);
//                            Optional<CompanyDeptVo> companyDeptVoOptional = departments.stream().filter(department -> department.getFd_dept_name().equals(departmentName)).findFirst();
//                            companyDeptVoOptional.ifPresent(deptVo -> companyStaffVo.setPk_company_dept(deptVo.getPk_company_dept()));
                            for(CompanyDeptVo companyDeptVoItem : departments){
                                if(companyDeptVoItem.getFd_dept_name().equals(departmentName)){
                                    companyStaffVo.setPk_company_dept(companyDeptVoItem.getPk_company_dept());
                                    break;
                                }
                            }
                    }

                    }

                    // find permission by name
                    if (null != row.getCell(4)) {
                        String permissionName = row.getCell(4).getStringCellValue();
                        if (StringUtils.isNotBlank(permissionName)) {
                            CompanyDeptVo companyDeptVo = new CompanyDeptVo();
                            companyDeptVo.setFk_company(companyStaffExcel.getPkCompany());
                            CodeVo codeVo = new CodeVo();
                            codeVo.setFk_up_code( "A1000" );
                            List<CodeVo> listCode = codeDao.selectCodeList( codeVo );
//                            Optional<CodeVo> codeVoOptional = listCode.stream().filter(code -> code.getFd_name().equals(permissionName) || code.getFd_name_en().equals(permissionName)).findFirst();
//                            codeVoOptional.ifPresent(deptVo -> companyStaffVo.setFd_staff_level_code(deptVo.getPk_code()));
                            for(CodeVo codeVoItem : listCode){
                                if(codeVoItem.getFd_name().equals(permissionName)){
                                    companyStaffVo.setFd_staff_level_code(codeVoItem.getPk_code());
                                    break;
                                }
                            }
                        }
                    }

                    companyStaffVo.setFd_staff_name(null != row.getCell(0) ? row.getCell(0).getStringCellValue() : "");
                    companyStaffVo.setFd_staff_duty(null != row.getCell(1) ? row.getCell(1).getStringCellValue() : "");
                    companyStaffVo.setFd_dept_master_yn(null != row.getCell(3) ? row.getCell(3).getStringCellValue() : "N");
                    companyStaffVo.setFd_company_master_yn(null != row.getCell(5) ? row.getCell(5).getStringCellValue() : "N");
                    companyStaffVo.setFd_staff_phone(null != row.getCell(6) ? row.getCell(6).getStringCellValue() : "");
                    companyStaffVo.setFd_staff_mobile(null != row.getCell(7) ? row.getCell(7).getStringCellValue() : "");
                    companyStaffVo.setFd_staff_email(null != row.getCell(8) ? row.getCell(8).getStringCellValue() : "");
                    companyStaffVo.setFd_staff_id(null != row.getCell(8) ? row.getCell(8).getStringCellValue() : "");

                    password = null != row.getCell(9) ? row.getCell(9).getStringCellValue() : "";
                    confirmPassword = null != row.getCell(10) ? row.getCell(10).getStringCellValue() : "";

                    if(password.equals(confirmPassword)) {
                        companyStaffVo.setFd_staff_pw(password);
                    }else{
                        continue;
                    }

                    companyStaffVo.setFd_staff_status_code("A1101");

                    CompanyDeptStaffVo companyDeptStaffVo = new CompanyDeptStaffVo();

                    CompanyStaffVo companyStaffVo1 = staffService.registerStaffInCompany(companyStaffVo);

                    CompanyStaffVo companyStaffVo3 = staffService.registerDeptStaff(companyStaffVo1);

                    companyDeptStaffVo.setFk_company_staff(companyStaffVo3.getPk_company_staff());

                    try{
                        if(staffService.updateRegisterStaff(companyStaffVo3) == 1) {
                            nCountSuccess++;
                        }else{
                            nCountFailed++;
                            staffDao.deleteStaff(companyStaffVo3);
                            staffDao.deleteDeptStaff(companyDeptStaffVo);
                        }
                    }catch(Exception exx){
                        nCountFailed++;
                        staffDao.deleteStaff(companyStaffVo3);
                        staffDao.deleteDeptStaff(companyDeptStaffVo);
                    }
                }

                message += "성공 " + nCountSuccess + ", 실패 " + nCountFailed;
            } catch (IOException e) {
                message = "ERROR System";
            } catch (Exception e) {
                message += "업로드에 실패했습니다. 입력사항을 확인해주세요.";
            }
        } else {
            message = "파일 형식을 확인해주세요.";
        }
        return message;
    }
    
    @Override
    public String uploadErrorExcel(MultipartFile file, long pkCompanyStaff) {
        String message = "";
        String fileName[] = file.getOriginalFilename().split("\\.");
        PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
        
        if (fileName[1].equalsIgnoreCase("xls") || fileName[1].equalsIgnoreCase("xlsx")) {
            try {
            	
            	 Map<String, Object> paramMap = new HashMap<>();
            	 
                 String path = "B11/" + loginInfoVo.getLoginUserType() + "/" + loginInfoVo.getLoginCompanyPk() + "/" + pkCompanyStaff + "/CTGR1_MANAGE/CTGR2_RECEPTIONIST/CTGR3_DEPART_INFO";
                 //solutionType/userType/fkCompany/fkCompanyStaffAi/ctgr01/ctgr02/ctgr03/파일명.xlsx
                 //예시) B11/B2001/70/224/CTGR1_MANAGE/CTGR2_RECEPTIONIST/CTGR3_DEPART_INFO/장애접수_STEP4_장애현상_유형정보.xlsx
                 
            	List<FileVo> fileVoList = fileService.uploadFileToVoListS3( file, path );
            	
            	message += "정상적으로 업로드되었습니다.";
            	paramMap.put("fkCompany", loginInfoVo.getLoginCompanyPk());
            	paramMap.put("fkCompanyStaffAi", pkCompanyStaff);
            	paramMap.put("aiWorkCd", "CTGR3_FAULT_LIST");
            	paramMap.put("pAiWorkCd", "CTGR2_FAULT_RECEPTION");
            	
            	newOjtDao.ojtErrorDataDelete(paramMap);
            	NewOjt2StepVo newOjt2StepVo = new NewOjt2StepVo();
            	
            	newOjt2StepVo.setFk_company(loginInfoVo.getLoginCompanyPk());
				newOjt2StepVo.setFk_comapny_staff_ai(pkCompanyStaff);
				newOjt2StepVo.setAi_work_cd("CTGR3_FAULT_LIST");
				newOjt2StepVo.setP_ai_work_cd("CTGR2_FAULT_RECEPTION");
				newOjt2StepVo.setEnable_yn("Y");
				newOjt2StepVo.setTask_val(null);
				newOjt2StepVo.setPath_file(path + "/" + file.getOriginalFilename());
				//변경되는값
				newOjt2StepVo.setFk_writer( loginInfoVo.getLoginCompanyStaffPk());
				
            	//newOjtDao.ojt2StepThreeFourRegist(newOjt2StepVo);
            	
            } catch (Exception e) {
                message = "ERROR System";
                System.out.println(e);
            }
        } else {
            message = "파일 형식을 확인해주세요.";
        }
        return message;
    } 
    
    @Override
    public Resource downloadCustomerExcel(String nameTemplate, CompanyCustomerExcel companyCustomerExcel) {
        try {
            String nameFileNew = PREFIX_DOWNLOAD_NAME + nameTemplate;
            String nameFileCopy = PREFIX_COPY_FILE_NAME + nameTemplate;
            Path pathFileOrigin = rootLocation.resolve(nameTemplate).normalize();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd");
            if (!Files.exists(rootLocation.resolve(DOWNLOAD_FOLDER))) {
                new File(rootLocation.resolve(DOWNLOAD_FOLDER).toString()).mkdir();
            }
            Path pathFileNew = rootLocation.resolve(DOWNLOAD_FOLDER + "/" + nameFileNew).normalize();
            Path pathFileCopy = rootLocation.resolve(DOWNLOAD_FOLDER + "/" + nameFileCopy).normalize();
            // delete older file if it exist
            Files.deleteIfExists(pathFileNew);
            Files.deleteIfExists(pathFileCopy);
            Files.copy(pathFileOrigin, pathFileNew);

            XSSFWorkbook wb = new XSSFWorkbook(new File(pathFileNew.toString()));
            XSSFSheet worksheet = wb.getSheetAt(0);

            // insert data from database of ticket_issue
            CompanyCustomerVo companyCustomerVoReq = new CompanyCustomerVo();
            companyCustomerVoReq.getSearch().setPageSize(0);
            companyCustomerVoReq.setFk_company(companyCustomerExcel.getPkCompany());
            List<CompanyCustomerVo> companyCustomerVos = companyCustomerDao.getCompanyCustomer(companyCustomerVoReq);
            for (int i = 0; i < companyCustomerVos.size(); i++) {
                CompanyCustomerVo companyCustomerVo = companyCustomerVos.get(i);
                Row row = worksheet.createRow(i + 1);
                Date contactDateFrom = companyCustomerVo.getContact_date_from();
                Date regDateFrom = companyCustomerVo.getContact_date_from();

                row.createCell(0).setCellValue(companyCustomerVo.getPk_company_customer());
                row.createCell(1).setCellValue(companyCustomerVo.getFd_customer_name());
                row.createCell(2).setCellValue(companyCustomerVo.getFd_company_name());
                row.createCell(3).setCellValue(companyCustomerVo.getFd_dept_name());
                row.createCell(4).setCellValue(companyCustomerVo.getFd_company_position());
                row.createCell(5).setCellValue(companyCustomerVo.getFd_customer_phone());
                row.createCell(6).setCellValue(companyCustomerVo.getFd_customer_mobile());
                row.createCell(7).setCellValue(companyCustomerVo.getFd_customer_email());
                row.createCell(8).setCellValue(companyCustomerVo.getFd_state_name());
                row.createCell(9).setCellValue(null != contactDateFrom ? simpleDateFormat.format(contactDateFrom) : "");
//                row.createCell(10).setCellValue(companyCustomerVo.getRegisterer());
                row.createCell(10).setCellValue(null != regDateFrom ? simpleDateFormat.format(regDateFrom) : "");
            }

            FileOutputStream output_file = new FileOutputStream(new File(pathFileCopy.toString()));
            //write changes
            wb.write(output_file);
            output_file.close();
            wb.close();
            Resource resource = new UrlResource(pathFileNew.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read file ");
            }
        } catch (MalformedURLException | FileNotFoundException e) {
            throw new RuntimeException("Could not retrieve file ");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Resource downloadCustomerTemplateExcel(String nameTemplate) {
        try {
            String nameFileNew = PREFIX_DOWNLOAD_NAME + nameTemplate;
            String nameFileCopy = PREFIX_COPY_FILE_NAME + nameTemplate;
             
            Path pathFileOrigin = rootLocation.resolve(nameTemplate).normalize();
            if (!Files.exists(rootLocation.resolve(DOWNLOAD_FOLDER))) {
                new File(rootLocation.resolve(DOWNLOAD_FOLDER).toString()).mkdir();
            }
            Path pathFileNew = rootLocation.resolve(DOWNLOAD_FOLDER + "/" + nameFileNew).normalize();
            Path pathFileCopy = rootLocation.resolve(DOWNLOAD_FOLDER + "/" + nameFileCopy).normalize();
            // delete older file if it exist
            Files.deleteIfExists(pathFileNew);
            Files.deleteIfExists(pathFileCopy);
            Files.copy(pathFileOrigin, pathFileNew);

            XSSFWorkbook wb = new XSSFWorkbook(new File(pathFileNew.toString()));

            // insert data from database of ticket_issue
            CompanyCustomerVo companyCustomerVoReq = new CompanyCustomerVo();
            companyCustomerVoReq.getSearch().setPageSize(0);
            FileOutputStream output_file = new FileOutputStream(new File(pathFileCopy.toString()));
            //write changes
            wb.write(output_file);
            output_file.close();
            wb.close();
            Resource resource = new UrlResource(pathFileNew.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read file ");
            }
        } catch (MalformedURLException | FileNotFoundException e) {
            throw new RuntimeException("Could not retrieve file ");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Resource downloadErrorTemplateExcel(String nameTemplate) {
        try {
            String nameFileNew = PREFIX_DOWNLOAD_NAME + nameTemplate;
            String nameFileCopy = PREFIX_COPY_FILE_NAME + nameTemplate;
            Path pathFileOrigin = rootLocation.resolve(nameTemplate).normalize();
            if (!Files.exists(rootLocation.resolve(DOWNLOAD_FOLDER))) {
                new File(rootLocation.resolve(DOWNLOAD_FOLDER).toString()).mkdir();
            }
            Path pathFileNew = rootLocation.resolve(DOWNLOAD_FOLDER + "/" + nameFileNew).normalize();
            Path pathFileCopy = rootLocation.resolve(DOWNLOAD_FOLDER + "/" + nameFileCopy).normalize();
            // delete older file if it exist
            Files.deleteIfExists(pathFileNew);
            Files.deleteIfExists(pathFileCopy);
            Files.copy(pathFileOrigin, pathFileNew);

            XSSFWorkbook wb = new XSSFWorkbook(new File(pathFileNew.toString()));

            // insert data from database of ticket_issue
            CompanyCustomerVo companyCustomerVoReq = new CompanyCustomerVo();
            companyCustomerVoReq.getSearch().setPageSize(0);
            FileOutputStream output_file = new FileOutputStream(new File(pathFileCopy.toString()));
            //write changes
            wb.write(output_file);
            output_file.close();
            wb.close();
            Resource resource = new UrlResource(pathFileNew.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read file ");
            }
        } catch (MalformedURLException | FileNotFoundException e) {
            throw new RuntimeException("Could not retrieve file ");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
    @Override
    public Resource downloadStaffTemplateExcel(String nameTemplate) {
        try {
            String nameFileNew = PREFIX_DOWNLOAD_NAME + nameTemplate;
            String nameFileCopy = PREFIX_COPY_FILE_NAME + nameTemplate;
            Path pathFileOrigin = rootLocation.resolve(nameTemplate).normalize();
            if (!Files.exists(rootLocation.resolve(DOWNLOAD_FOLDER))) {
                new File(rootLocation.resolve(DOWNLOAD_FOLDER).toString()).mkdir();
            }
            Path pathFileNew = rootLocation.resolve(DOWNLOAD_FOLDER + "/" + nameFileNew).normalize();
            Path pathFileCopy = rootLocation.resolve(DOWNLOAD_FOLDER + "/" + nameFileCopy).normalize();
            // delete older file if it exist
            Files.deleteIfExists(pathFileNew);
            Files.deleteIfExists(pathFileCopy);
            Files.copy(pathFileOrigin, pathFileNew);

            XSSFWorkbook wb = new XSSFWorkbook(new File(pathFileNew.toString()));

            // insert data from database of ticket_issue
            CompanyStaffVo companyStaffVoReq = new CompanyStaffVo();
            companyStaffVoReq.getSearch().setPageSize(0);
            FileOutputStream output_file = new FileOutputStream(new File(pathFileCopy.toString()));
            //write changes
            wb.write(output_file);
            output_file.close();
            wb.close();
            Resource resource = new UrlResource(pathFileNew.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read file ");
            }
        } catch (MalformedURLException | FileNotFoundException e) {
            throw new RuntimeException("Could not retrieve file ");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public Resource downloadStep4Excel(String nameTemplate) {
        try {
            String nameFileNew = PREFIX_DOWNLOAD_NAME + nameTemplate;
            String nameFileCopy = PREFIX_COPY_FILE_NAME + nameTemplate;
            Path pathFileOrigin = rootLocation.resolve(nameTemplate).normalize();
            if (!Files.exists(rootLocation.resolve(DOWNLOAD_FOLDER))) {
                new File(rootLocation.resolve(DOWNLOAD_FOLDER).toString()).mkdir();
            }
            Path pathFileNew = rootLocation.resolve(DOWNLOAD_FOLDER + "/" + nameFileNew).normalize();
            Path pathFileCopy = rootLocation.resolve(DOWNLOAD_FOLDER + "/" + nameFileCopy).normalize();
            // delete older file if it exist
            Files.deleteIfExists(pathFileNew);
            Files.deleteIfExists(pathFileCopy);
            Files.copy(pathFileOrigin, pathFileNew);

            XSSFWorkbook wb = new XSSFWorkbook(new File(pathFileNew.toString()));

            // insert data from database of ticket_issue
            CompanyStaffVo companyStaffVoReq = new CompanyStaffVo();
            companyStaffVoReq.getSearch().setPageSize(0);
            FileOutputStream output_file = new FileOutputStream(new File(pathFileCopy.toString()));
            
            //write changes
            wb.write(output_file);
            output_file.close();
            wb.close();
            Resource resource = new UrlResource(pathFileNew.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read file ");
            }
        } catch (MalformedURLException | FileNotFoundException e) {
            throw new RuntimeException("Could not retrieve file ");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    

    @Override
    public Resource downloadExcelByJson(String nameTemplate, JSONArray jsonArray, List<ExcelVo> columnList) {
    	try {
    		// 파일명 관련
	        String nameFileNew = PREFIX_DOWNLOAD_NAME + nameTemplate;
	        String nameFileCopy = PREFIX_COPY_FILE_NAME + nameTemplate;
	        Path pathFileOrigin = rootLocation.resolve(nameTemplate).normalize();
	        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd");
	        if (!Files.exists(rootLocation.resolve(DOWNLOAD_FOLDER))) {
	            new File(rootLocation.resolve(DOWNLOAD_FOLDER).toString()).mkdir();
	        }
	        Path pathFileNew = rootLocation.resolve(DOWNLOAD_FOLDER + "/" + nameFileNew).normalize();
	        Path pathFileCopy = rootLocation.resolve(DOWNLOAD_FOLDER + "/" + nameFileCopy).normalize();
	        System.out.println(DOWNLOAD_FOLDER + "/" + nameFileNew);
	        
	        // delete older file if it exist\
	        
	        System.out.println(pathFileNew.getFileName());
	        Files.deleteIfExists(pathFileNew);
	        Files.deleteIfExists(pathFileCopy);
	        Files.copy(pathFileOrigin, pathFileNew);
	
	        XSSFWorkbook wb = new XSSFWorkbook(new File(pathFileNew.toString()));
	        XSSFSheet worksheet = wb.getSheetAt(0);
	
	        // 테이블 헤더
            Row row = worksheet.createRow(0);
            for(int j = 0; j < columnList.size(); j++) {
            	ExcelVo excelVo = columnList.get(j);
            	
            	row.createCell(j).setCellValue(excelVo.getColumnName());
            }
	        
	        // insert data 
	        for (int i = 0; i < jsonArray.length() ; i++) {
	            JSONObject rowObj = jsonArray.getJSONObject(i);
	        	
	            row = worksheet.createRow(i + 1);

	            for(int j = 0; j < columnList.size(); j++) {
	            	ExcelVo excelVo = columnList.get(j);
	            	Object data = null;
	            	if(!rowObj.isNull(excelVo.getTargetName())) data = rowObj.get(excelVo.getTargetName());
	            	else data = "0";
	            	
	            	row.createCell(j).setCellValue(data.toString());
	            }
	            
	        }
	
	        FileOutputStream output_file = new FileOutputStream(new File(pathFileCopy.toString()));
	        //write changes
	        wb.write(output_file);
	        output_file.close();
	        wb.close();
	        Resource resource = new UrlResource(pathFileNew.toUri());
	    
	        if (resource.exists() || resource.isReadable()) {
	            return resource;
	        } else {
	            throw new RuntimeException("Could not read file ");
	        }
	    } catch (MalformedURLException | FileNotFoundException e) {
	        throw new RuntimeException("Could not retrieve file ");
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return null;
	}

}
