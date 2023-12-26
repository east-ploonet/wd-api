package com.saltlux.aice_fe.pc.join.service.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saltlux.aice_fe._baseline.baseService.FileService;
import com.saltlux.aice_fe._baseline.baseService.impl.BaseServiceImpl;
import com.saltlux.aice_fe._baseline.baseVo.FileVo;
import com.saltlux.aice_fe._baseline.commons.Common;
import com.saltlux.aice_fe._baseline.security.Encryption;
import com.saltlux.aice_fe._baseline.util.FormatUtils;
import com.saltlux.aice_fe.ecApi.service.NiceApiService;
import com.saltlux.aice_fe.ecApi.service.PloonetApiService;
import com.saltlux.aice_fe.email.service.EmailService;
import com.saltlux.aice_fe.pc.auth.service.PcAuthService;
import com.saltlux.aice_fe.pc.credit.dao.CreditDao;
import com.saltlux.aice_fe.pc.credit.vo.MyNewCreditItemVo;
import com.saltlux.aice_fe.pc.join.dao.JoinDao;
import com.saltlux.aice_fe.pc.join.service.JoinService;
import com.saltlux.aice_fe.pc.join.vo.*;
import com.saltlux.aice_fe.pc.staff.dao.AIStaffDao;

import ch.qos.logback.core.recovery.ResilientSyslogOutputStream;
import com.saltlux.aice_fe.pc.user.dao.UserDao;
import com.saltlux.aice_fe.pc.user.vo.Request.UserReqBody;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class JoinServiceImpl extends BaseServiceImpl implements JoinService {

    @Autowired
    private JoinDao joinDao;
    
    @Autowired
    private CreditDao creditDao;

    @Autowired
    private AIStaffDao aiStaffDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private FileService fileService;

    @Autowired
    PcAuthService pcAuthService;

    @Autowired
    private NiceApiService niceApiService;

    @Autowired
    EmailService emailService;

    @Autowired
    PloonetApiService ploonetApiService;

    @Value("${service.domain}")
    private String serviceDomain;


    @Override
    public Map<String, Object> getTermsList(TermsVo reqTermsVo) throws Exception {

        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> listMap = new ArrayList<>();

        // 약관 목록
        List<TermsVo> listTermsVo = joinDao.getTermsList(reqTermsVo);

        if (listTermsVo == null) {
            throwException.statusCode(204);

        } else {

            for (TermsVo termsVo : listTermsVo) {

                Map<String, Object> mapAdd = new HashMap<>();

                mapAdd.put("termsPk", Long.toString(termsVo.getPk_terms()));
                mapAdd.put("termsTargetCode", termsVo.getFd_terms_target_code());
                mapAdd.put("termsCode", termsVo.getFd_terms_code());
                mapAdd.put("termsTitle", termsVo.getFd_terms_title());
                mapAdd.put("termsContents", termsVo.getFd_terms_contents());
                mapAdd.put("termsMandatoryYn", termsVo.getFd_terms_mandatory_yn());
                mapAdd.put("termsOpenDate", FormatUtils.dateToStringCustomize(termsVo.getFd_terms_open_date(), "yyyy/MM/dd HH:mm"));
                mapAdd.put("openYn", termsVo.getFd_open_yn());
                mapAdd.put("regDate", termsVo.getFd_regdate());
                mapAdd.put("modDate", termsVo.getFd_moddate());

                listMap.add(mapAdd);
            }
        }

        result.put("termsList", listMap);

        return result;
    }

    @Override
    public void registTerms(Map<String, Object> paramMap) throws Exception {

        List<Map<String, Object>> listCompanyTerms = (List<Map<String, Object>>) paramMap.get("listCompanyTerms");
        List<Map<String, Object>> listStaffTerms = (List<Map<String, Object>>) paramMap.get("listStaffTerms");

        int registCnt = 0;

        try {

            // 가입회사 약관 항목
            List<Map<String, Object>> companyTerms = new ArrayList<>();

            for (int i = 0; i < listCompanyTerms.size(); i++) {

                Map<String, Object> map = new HashMap<>();

                map.put("termsFk", Long.parseLong((String) listCompanyTerms.get(i).get("termsFk")));
                map.put("companyFk", Long.parseLong((String) listCompanyTerms.get(i).get("companyFk")));
                map.put("agreeYn", listCompanyTerms.get(i).get("agreeYn"));
                map.put("retractYn", listCompanyTerms.get(i).get("retractYn"));

                companyTerms.add(map);

            }

            paramMap.put("companyTermsList", companyTerms);

            registCnt = joinDao.insertCompanyTerms(paramMap);

            // 담당자 약관 항목
            List<Map<String, Object>> staffTerms = new ArrayList<>();

            for (int i = 0; i < listStaffTerms.size(); i++) {

                Map<String, Object> map = new HashMap<>();

                map.put("termsFk", Long.parseLong((String) listStaffTerms.get(i).get("termsFk")));
                map.put("companyStaffFk", Long.parseLong((String) listStaffTerms.get(i).get("companyStaffFk")));
                map.put("agreeYn", listStaffTerms.get(i).get("agreeYn"));
                map.put("retractYn", listStaffTerms.get(i).get("retractYn"));

                staffTerms.add(map);

            }

            paramMap.put("staffTermsList", staffTerms);

            registCnt = joinDao.insertStaffTerms(paramMap);

        } catch (Exception ex) {
            log.error("********** paramMap : {}", paramMap.toString());
            throwException.statusCode(500);
        }

        if (registCnt == 0) {
            throwException.statusCode(204);
        }
    }

    @Override
    public void registInfo(Map<String, Object> paramMap) throws Exception {

        final String staffPw = Encryption.EncryptSha256((String) paramMap.get("staffPw"));

        // 가입회사 정보
        CompanyVo companyVo = new CompanyVo();

        companyVo.setFd_biz_license_num((String) paramMap.get("bizNum"));
        companyVo.setFd_company_name((String) paramMap.get("companyName"));
        companyVo.setFd_company_id((String) paramMap.get("companyId"));
        companyVo.setFd_company_phone((String) paramMap.get("companyPhone"));
        companyVo.setFd_company_fax((String) paramMap.get("companyFax"));
        companyVo.setFd_address_zipcode((String) paramMap.get("addrZipcode"));
        companyVo.setFd_address_common((String) paramMap.get("addrCommon"));
        companyVo.setFd_address_detail((String) paramMap.get("addrDetail"));
        //companyVo.setFd_company_website((String) paramMap.get("siteUrl"));

        MultipartFile[] uploadFile = (MultipartFile[]) paramMap.get("uploadFile");

        if (uploadFile != null && uploadFile.length > 0) {

            List<FileVo> fileVoList = fileService.uploadFileToVoList(uploadFile, "TBL_COMPANY");
            //
            if (fileVoList != null && fileVoList.size() > 0 && fileVoList.get(0) != null) {

                companyVo.setFd_company_logo_file_name(fileVoList.get(0).getFd_file_name());
                companyVo.setFd_company_logo_file_path(fileVoList.get(0).getFd_file_path());

            }

        }

        // 담당자 정보
        CompanyStaffVo companyStaffVo = new CompanyStaffVo();

        companyStaffVo.setFd_staff_name((String) paramMap.get("staffName"));
        companyStaffVo.setFd_staff_duty((String) paramMap.get("staffDuty"));
        companyStaffVo.setFd_staff_phone((String) paramMap.get("staffPhone"));
        companyStaffVo.setFd_staff_mobile((String) paramMap.get("staffMobile"));
        companyStaffVo.setFd_staff_email((String) paramMap.get("staffEmail"));
        companyStaffVo.setFd_staff_pw(staffPw);
        companyStaffVo.setFd_staff_id((String) paramMap.get("companyId"));

        int companyRegistCnt = 0;
        int companyStaffRegistCnt = 0;
        int companyDeptCnt = 0;

        try {

            companyRegistCnt = joinDao.insertCompany(companyVo);

            if (companyVo.getPk_company() != 0) {

                companyStaffVo.setFk_company(companyVo.getPk_company());
                companyStaffRegistCnt = joinDao.insertCompanyStaff(companyStaffVo);

            }

            // 직원 등록 완료 후 등록자Fk 업데이트
            if (companyStaffVo.getPk_company_staff() != 0) {

                companyVo.setPk_company(companyVo.getPk_company());
                companyVo.setFk_writer(companyStaffVo.getPk_company_staff());

                companyStaffVo.setPk_company_staff(companyStaffVo.getPk_company_staff());
                companyStaffVo.setFk_writer(companyStaffVo.getPk_company_staff());

                joinDao.updateCompanyInfo(companyVo);
                joinDao.updateCompanyStaffInfo(companyStaffVo);

            }

            CompanyDeptVo companyDeptVo = new CompanyDeptVo();

            if (companyRegistCnt != 0 && companyStaffRegistCnt != 0) {

                // 회사 부서 정보
                companyDeptVo.setFk_company(companyVo.getPk_company());
                companyDeptVo.setFd_dept_name((String) paramMap.get("deptName"));
                companyDeptVo.setFd_dept_code((String) paramMap.get("deptCode"));
                companyDeptVo.setFd_dept_role((String) paramMap.get("deptRole"));
                companyDeptVo.setFd_use_yn(Common.NVL(paramMap.get("useYn"), "Y"));
                companyDeptVo.setFk_writer(companyStaffVo.getPk_company_staff());

                companyDeptCnt = joinDao.insertCompanyDept(companyDeptVo);

            }

            if (companyDeptCnt != 0) {

                // 부서 직원 맵핑
                CompanyDeptStaffVo companyDeptStaffVo = new CompanyDeptStaffVo();

                companyDeptStaffVo.setFk_company_dept(companyDeptVo.getPk_company_dept());
                companyDeptStaffVo.setFk_company_staff(companyStaffVo.getPk_company_staff());
                companyDeptStaffVo.setFd_dept_master_yn(Common.NVL(paramMap.get("masterYn"), "Y"));

                joinDao.insertCompanyDeptStaff(companyDeptStaffVo);

            }

        } catch (Exception ex) {
            log.error("********** paramMap : {}", paramMap.toString());
            throwException.statusCode(500);
        }

        if (companyRegistCnt == 0 && companyStaffRegistCnt == 0 && companyDeptCnt == 0) {
            throwException.statusCode(204);
        }

    }

    @Override
    public Map<String, Object> checkStaffId(Map<String, Object> paramMap) throws Exception {

        CompanyStaffVo companyStaffVo = joinDao.checkStaffId(paramMap);
        //
        Map<String, Object> resultMap = new HashMap<>();

        if (companyStaffVo != null && !companyStaffVo.getFd_staff_id().isEmpty()) {

            resultMap.put("dupYn", "Y");
//			throwException.statusCode(409);
        } else {
            resultMap.put("dupYn", "N");
        }

        return resultMap;
    }

    @Override
    public Map<String, Object> dupCheckBizNumber(Map<String, Object> paramMap) throws Exception {

        CompanyVo companyVo = joinDao.dupCheckBizNumber(paramMap);
        //
        Map<String, Object> resultMap = new HashMap<>();

        if (companyVo != null && !companyVo.getFd_biz_license_num().isEmpty()) {
            resultMap.put("dupYn", "Y");

        } else {
            resultMap.put("dupYn", "N");
        }

        return resultMap;
    }

    @Override
    public List<FileVo> fileUpload(Map<String, Object> paramMap) throws Exception {

        return fileService.uploadFileToVoList((MultipartFile[]) paramMap.get("uploadFiles"), "TBL_COMPANY");
    }

    @Override
    public Map<String, Object> registCompany(Map<String, Object> paramMap) throws Exception {

        CompanyVo companyVo = new CompanyVo();
        //
        companyVo.setFd_company_status_code("A1601");    // 회사계정상태코드 (A1601 : 정상)
        companyVo.setFd_company_id(Common.NVL(paramMap.get("companyId"), ""));
        companyVo.setFd_company_name(Common.NVL(paramMap.get("companyName"), ""));
        companyVo.setFd_biz_license_num(Common.NVL(paramMap.get("bizNum"), ""));
        companyVo.setFd_company_phone(Common.NVL(paramMap.get("companyPhone"), ""));
        companyVo.setFd_company_fax(Common.NVL(paramMap.get("companyFax"), ""));
        companyVo.setFd_company_website(Common.NVL(paramMap.get("companyWebsite"), ""));
        companyVo.setFd_address_zipcode(Common.NVL(paramMap.get("addrZipcode"), ""));
        companyVo.setFd_address_common(Common.NVL(paramMap.get("addrCommon"), ""));
        companyVo.setFd_address_detail(Common.NVL(paramMap.get("addrDetail"), ""));
        companyVo.setFd_company_logo_file_name(Common.NVL(paramMap.get("logoFileName"), ""));
        companyVo.setFd_company_logo_file_path(Common.NVL(paramMap.get("logoFilePath"), ""));
        companyVo.setFd_biz_license_file_name(Common.NVL(paramMap.get("bizFileName"), ""));
        companyVo.setFd_biz_license_file_path(Common.NVL(paramMap.get("bizFilePath"), ""));

        Map<String, Object> resultMap = new HashMap<>();

        try {

            int rowCnt = joinDao.insertCompany(companyVo);
            //
            if (rowCnt > 0) {

                resultMap.put("pkCompany", companyVo.getPk_company());

            } else {

                throwException.statusCode(500);

            }

        } catch (Exception ex) {
            log.error("********** paramMap : {}", paramMap.toString());
            throwException.statusCode(500);
        }

        return resultMap;

    }

    @Override
    public Map<String, Object> registStaff(Map<String, Object> paramMap) throws Exception {

        // 비밀번호 암호화(단방향)
        BCryptPasswordEncoder pwEncoder = new BCryptPasswordEncoder();
//		final String staffPw = Encryption.EncryptSha256( Common.NVL(paramMap.get("staffPw"), "") );

        String userType = paramMap.get("userType").toString();
        String userTypeName = "개인회원";
        
        if(userType == "B2001") userTypeName = "기업회원";
        		
        CompanyStaffVo companyStaffVo = new CompanyStaffVo();
        //
        companyStaffVo.setFk_company(Common.parseLong(paramMap.get("fkCompany")));

        //companyStaffVo.setFd_staff_pw					( staffPw );
        companyStaffVo.setFd_staff_pw(BCRYPT_ENCODER.encode(Common.NVL(paramMap.get("staffPw"), "")));

        companyStaffVo.setFd_staff_level_code("A1001");    // 직원구분코드 	(A1001 : 마스터)
        companyStaffVo.setFd_staff_status_code("A1101");    // 직원계정상태코드 (A1101 : 정상)
        //companyStaffVo.setFd_staff_response_status_code	( "A1201" );	// 직원응답상태코드 (A1201 : 수신가능)
        companyStaffVo.setFd_company_master_yn("Y");
        companyStaffVo.setFd_staff_id(Common.NVL(paramMap.get("staffEmail"), ""));
        companyStaffVo.setFd_staff_name(Common.NVL(paramMap.get("staffName"), ""));
        companyStaffVo.setFd_staff_mobile(Common.NVL(paramMap.get("staffMobile"), ""));
        companyStaffVo.setFd_staff_phone(Common.NVL(paramMap.get("staffPhone"), ""));
        companyStaffVo.setFd_staff_email(Common.NVL(paramMap.get("staffEmail"), ""));
        companyStaffVo.setFd_staff_duty(Common.NVL(paramMap.get("staffDuty"), ""));
        companyStaffVo.setFd_staff_birth(Common.NVL(paramMap.get("staffBirth"), ""));
        companyStaffVo.setFd_staff_gender_mf(Common.NVL(paramMap.get("staffGender"), ""));
        companyStaffVo.setFd_staff_national_yn(Common.NVL(paramMap.get("staffNationalYn"), ""));
        companyStaffVo.setFd_staff_mobile_type(Common.NVL(paramMap.get("staffMobileType"), ""));
        companyStaffVo.setFd_staff_di(Common.NVL(paramMap.get("staffDi"), ""));
        companyStaffVo.setFd_staff_ci(Common.NVL(paramMap.get("staffCi"), ""));
        companyStaffVo.setFd_staff_persona(Common.NVL(paramMap.get("staffPersona"), ""));
        companyStaffVo.setFk_staff_work_code(Common.NVL(paramMap.get("staffWorkCode"), "CTGR1003"));


        Map<String, Object> resultMap = new HashMap<>();

        System.out.println("시작!!!!!!!!!!!!!!!!!!!!!!!");
        try {
            int staffRowCnt = joinDao.insertCompanyStaff(companyStaffVo);
            //
            if (staffRowCnt > 0) {

                // '기본' 부서를 기본값으로 등록
                CompanyDeptVo companyDeptVo = new CompanyDeptVo();
                //
                companyDeptVo.setFk_company(companyStaffVo.getFk_company());
                companyDeptVo.setFk_writer(companyStaffVo.getPk_company_staff());
                companyDeptVo.setFd_dept_name("기본");
                companyDeptVo.setFd_default_yn("Y");
                System.out.println("등록시작111");
                int deptRowCnt = joinDao.insertCompanyDept(companyDeptVo);

                // 소속부서 입력값이 있는 경우 부서 추가 등록
                final String deptName = Common.NVL(paramMap.get("companyDeptName"), "");
                //
                if (deptName != null && !deptName.isEmpty()) {

                    companyDeptVo.setFd_dept_name(deptName);
                    companyDeptVo.setFd_default_yn("N");
                    joinDao.insertCompanyDept(companyDeptVo);

                }

                if (deptRowCnt > 0) {

                    // 마지막에 등록된 부서와 담당자 매핑 (기본 또는 소속부서 입력값)
                    CompanyDeptStaffVo companyDeptStaffVo = new CompanyDeptStaffVo();
                    //
                    companyDeptStaffVo.setFk_company_dept(companyDeptVo.getPk_company_dept());
                    companyDeptStaffVo.setFk_company_staff(companyStaffVo.getPk_company_staff());

                    int sdRowCnt = joinDao.insertCompanyDeptStaff(companyDeptStaffVo);
                    //
                    if (sdRowCnt > 0) {

                        // [회사, 직원] 등록자 정보를 현재 등록한 담당자 pk로 등록
                        CompanyVo companyVo = new CompanyVo();
                        companyVo.setPk_company(companyStaffVo.getFk_company());
                        companyVo.setFk_writer(companyStaffVo.getPk_company_staff());
                        companyStaffVo.setFk_writer(companyStaffVo.getPk_company_staff());

                        joinDao.updateCompanyInfo(companyVo);
                        joinDao.updateCompanyStaffInfo(companyStaffVo);


                        // 초대 코드 발급
                        pcAuthService.resetCompanyStaffTicketCode(companyStaffVo);
                        //-- --//

                        resultMap.put("pkStaff", companyStaffVo.getPk_company_staff());

                    } else {
                    	System.out.println("지막에 등록된 부서!!!");
                    	log.error("지막에 등록된 부서!!!");
                        throwException.statusCode(500);
                    }

                } else {
                	System.out.println("부서등록에러!!!");
                	log.error("부서등록에러!!!!");
                    throwException.statusCode(500);
                }

            } else {
            	System.out.println("insertCompanyStaff!!!");
            	log.error("insertCompanyStaff!!!");
                throwException.statusCode(500);
            }

        } catch (Exception ex) {
            log.error("********** paramMap : {}", paramMap.toString());
            log.error("담당자관리 등록 오류 !!!!!!!!ex:!!!!!!!!!!!" + ex);
            System.out.println("담당자관리 등록 오류 !!!!!!!!ex:!!!!!!!!!!!" + ex);
            throwException.statusCode(500);
        }
        UserReqBody reqBody = new UserReqBody();
        reqBody.setUser_type(userType);
        reqBody.setPk_company(companyStaffVo.getFk_company());

        reqBody.setPk_company_staff(companyStaffVo.getPk_company_staff());
        long pkPgPayLog = registerPgPayLogEntry(companyStaffVo.getFk_company(),companyStaffVo.getPk_company_staff());
        
        MyNewCreditItemVo mySelectPlanList = creditDao.getSelectPlan((String) "CARD_SM_001"); //엔트리
        ploonetApiService.firstSendBillEntry(mySelectPlanList, companyStaffVo.getFk_company() , pkPgPayLog);
        //creditDao.registerEntryReserve(reqBody);

        String companyId = userDao.getCompanyId(companyStaffVo.getFk_company());

        Map<String, String> map = new HashMap<>();
        List<String> emailList = new ArrayList<>();
        emailList.add(companyStaffVo.getFd_staff_email());
        String[] email = companyStaffVo.getFd_staff_email().split("@");
        String userEmail = email[0].replaceAll("(?<=.{3}).", "*") + "@" + email[1].replaceAll("(?<=.{2}).", "*");
        String id = companyId.replaceAll("(?<=.{3}).", "*");
        map.put("email", userEmail);
        map.put("company_id", id);
        map.put("userType", userTypeName);
        LocalDate now = LocalDate.now();
        map.put("regDate", now.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")));
        emailService.sendEmail(emailList, "회원가입을 환영합니다.", "join.html", map);

        return resultMap;

    }

    @Override
    public void agreeTermsCompany(Map<String, Object> paramMap) throws Exception {

        final String[] fkTermsArr = Common.NVL(paramMap.get("fkTerms"), "").split(",");
        //
        paramMap.put("fkTermsArr", fkTermsArr);
        paramMap.put("agreeYn", "Y");

        try {

            int rowCnt = joinDao.insertCompanyTerms(paramMap);
            //
            if (rowCnt <= 0) {
                throwException.statusCode(500);
            }

        } catch (Exception ex) {
            log.error("********** paramMap : {}", paramMap.toString());
            throwException.statusCode(500);
        }

    }

    @Override
    public void agreeTermsStaff(Map<String, Object> paramMap) throws Exception {

        final String[] fkTermsArr = Common.NVL(paramMap.get("fkTerms"), "").split(",");
        //
        paramMap.put("fkTermsArr", fkTermsArr);
        paramMap.put("agreeYn", "Y");

        try {

            int rowCnt = joinDao.insertStaffTerms(paramMap);
            //
            if (rowCnt <= 0) {
                throwException.statusCode(500);
            }

        } catch (Exception ex) {
            log.error("********** paramMap : {}", paramMap.toString());
            throwException.statusCode(500);
        }

    }

    @Override
    public long registStaffAi(Map<String, Object> paramMap) throws Exception {

        CompanyStaffVo companyStaffVo = new CompanyStaffVo();
        //
        companyStaffVo.setFk_staff_work(1);                // 담당직무 pk 고정값으로 적용(1 : 리셉셔리스트)
        companyStaffVo.setFk_company(Common.parseLong(paramMap.get("fkCompany")));
        companyStaffVo.setFk_writer(Common.parseLong(paramMap.get("fkWriter")));
        companyStaffVo.setFd_staff_level_code("A1004");    // 직원구분코드 	(A1004 : 일반직원)
        companyStaffVo.setFd_staff_status_code("A1101");    // 직원계정상태코드 (A1101 : 정상)
        //companyStaffVo.setFd_staff_response_status_code	( "A1201" 	);	// 직원응답상태코드 (A1201 : 수신가능)
        companyStaffVo.setFd_staff_ai_yn("Y");
        companyStaffVo.setFd_staff_ai_uid(Common.NVL(paramMap.get("aiUid"), ""));
        companyStaffVo.setFd_staff_id(Common.NVL(paramMap.get("aiEmail"), ""));
        companyStaffVo.setFd_staff_email(Common.NVL(paramMap.get("aiEmail"), ""));
        companyStaffVo.setFd_staff_name(Common.NVL(paramMap.get("aiEmail"), "").split("\\.")[0]);

        companyStaffVo.setFk_staff_work_code(Common.NVL(paramMap.get("fkStaffWorkCode"), "CTGR1003"));
        companyStaffVo.setFd_staff_persona(Common.NVL(paramMap.get("fdStaffPersona"), "1"));
        long staffRowCnt = 0;
        try {

            long result = joinDao.insertCompanyStaffAi(companyStaffVo);
            staffRowCnt = companyStaffVo.getPk_company_staff();
            
            //
            if (staffRowCnt > 0) {

                // 'AI직원' 부서를 기본값으로 등록
//                CompanyDeptVo companyDeptVo = new CompanyDeptVo();
//                //
//                companyDeptVo.setFk_company(companyStaffVo.getFk_company());
//                companyDeptVo.setFk_writer(companyStaffVo.getFk_writer());
////                companyDeptVo.setFd_dept_name("AI직원");
//                companyDeptVo.setFd_default_yn("Y");
//                companyDeptVo.setFd_dept_ai_yn("Y");

//                int deptRowCnt = joinDao.insertCompanyDept(companyDeptVo);
//                //
//                if (deptRowCnt > 0) {

//                    CompanyDeptStaffVo companyDeptStaffVo = new CompanyDeptStaffVo();
//                    //
//                    companyDeptStaffVo.setFk_company_dept(companyDeptVo.getPk_company_dept());
//                    companyDeptStaffVo.setFk_company_staff(companyStaffVo.getPk_company_staff());
//
//                    int sdRowCnt = joinDao.insertCompanyDeptStaff(companyDeptStaffVo);
//                    //
//                    if (sdRowCnt <= 0) {
//                        throwException.statusCode(500);
//                    }

//                } else {
//                    throwException.statusCode(500);
//                }


                // 메인 AI직원 업데이트
                Map<String, Object> mainParamMap = new HashMap<>();
                mainParamMap.put("fd_default_ai", "N");
                mainParamMap.put("fk_company", companyStaffVo.getFk_company());

                //회사에 있는 직무별 AI 모두 메인여부 N 업데이트
                int mainUpdate = aiStaffDao.allMainAiUpdate(mainParamMap);

                //회사 각 직무별 등록순 메인 AI 업데이트
                int newMainUpdate = aiStaffDao.newMainUpdate(mainParamMap);
                
                
            } else {
                throwException.statusCode(500);
                staffRowCnt = 0;
                
            }

        } catch (Exception ex) {
            log.error("********** paramMap : {}", paramMap.toString());
            throwException.statusCode(500);
            staffRowCnt = 0;
        }
        return staffRowCnt;
    }

    @Override
    public Map<String, Object> joinAuth() throws Exception {

        Map<String, Object> resultMap = new HashMap<>();

        //-- 암호화 토큰 발급
        Map<String, Object> resultBody = niceApiService.getCryptoToken();

        if (resultBody != null && !resultBody.isEmpty()) {

            ObjectMapper objectMapper = new ObjectMapper();

            Map<String, Object> dataHeader = objectMapper.convertValue(resultBody.get("dataHeader"), Map.class);
            Map<String, Object> dataBody = objectMapper.convertValue(resultBody.get("dataBody"), Map.class);

            if (dataHeader != null && !dataHeader.isEmpty() && dataBody != null && !dataBody.isEmpty()) {

                // 1. dataHeader부의 GW_RSLT_CD가 "1200"일 경우, dataBody 부가 유효함
                if ("1200".equals(Common.NVL(dataHeader.get("GW_RSLT_CD"), ""))) {

                    // 2. dataBody부의 rsp_cd가 P000일 때, result_cd값이 유효함
                    // 3. dataBody부의 result_cd값이 "0000"일 경우 응답데이터가 유효함
                    if ("P000".equals(Common.NVL(dataBody.get("rsp_cd"), "")) && "0000".equals(Common.NVL(dataBody.get("result_cd"), ""))) {

                        final String req_dtim = Common.NVL(resultBody.get("req_dtim"), "");    // 요청일시
                        final String req_no = Common.NVL(resultBody.get("req_no"), "");    // 요청고유번호
                        final String token_val = Common.NVL(dataBody.get("token_val"), "");    // 암복호화를 위한 서버 토큰 값
                        final String site_code = Common.NVL(dataBody.get("site_code"), "");    // 사이트 코드
                        final String token_version_id = Common.NVL(dataBody.get("token_version_id"), "");    // 서버 토큰 버전

                        //-- 대칭키 & 무결성키 생성 (SHA-256, Base64 Encoding)
                        String value = req_dtim.trim() + req_no.trim() + token_val.trim();

                        MessageDigest md = MessageDigest.getInstance("SHA-256");
                        md.update(value.getBytes());
                        byte[] arrHashValue = md.digest();
                        String resultVal = Base64.encodeBase64String(arrHashValue);
                        //
                        final String key = resultVal.substring(0, 16);
                        final String iv = resultVal.substring(resultVal.length() - 16);
                        final String hmac_key = resultVal.substring(0, 32);

                        //-- 요청데이터 암호화
                        Map<String, Object> reqData = new HashMap<>();
                        reqData.put("requestno", "REQ" + req_no);    // 서비스 요청 고유 번호
                        reqData.put("returnurl", serviceDomain + "/work/Join/authResult");    // 인증결과를 받을 회원사 url
                        reqData.put("sitecode", site_code);    // 암호화토큰요청 API에 응답받은 site_code
                        reqData.put("authtype", "M");    // 인증수단 고정 (M:휴대폰인증)
                        reqData.put("methodtype", "get");    // 결과 전달시 http method 타입
                        reqData.put("popupyn", "Y");
                        //
                        String reqDataStr = objectMapper.writeValueAsString(reqData);

                        SecretKey secretKey = new SecretKeySpec(key.getBytes(), "AES");
                        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
                        c.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv.getBytes()));
                        byte[] encrypted = c.doFinal(reqDataStr.trim().getBytes());
                        final String enc_data = Base64.encodeBase64String(encrypted);

                        //-- Hmac 무결성 체크값 생성
                        Mac mac = Mac.getInstance("HmacSHA256");
                        SecretKeySpec sks = new SecretKeySpec(hmac_key.getBytes(), "HmacSHA256");
                        mac.init(sks);
                        byte[] hmacSha256 = mac.doFinal(enc_data.getBytes());
                        final String integrity_value = Base64.encodeBase64String(hmacSha256);

                        resultMap.put("tokenVersionId", token_version_id);
                        resultMap.put("encData", enc_data);
                        resultMap.put("integrityValue", integrity_value);
                        resultMap.put("resultVal", resultVal);

                    } else {
                        log.error("********************************* nice api error *********************************");
                        log.error("response code    = {}", Common.NVL(dataBody.get("rsp_cd"), ""));
                        log.error("response message = {}", Common.NVL(dataBody.get("res_msg"), ""));
                        log.error("********************************* nice api error *********************************");
                        throwException.statusCode(500);
                    }

                } else {
                    log.error("********************************* nice api error *********************************");
                    log.error("response code    = {}", Common.NVL(dataHeader.get("GW_RSLT_CD"), ""));
                    log.error("response message = {}", Common.NVL(dataHeader.get("GW_RSLT_MSG"), ""));
                    log.error("********************************* nice api error *********************************");
                    throwException.statusCode(500);
                }

            } else {
                throwException.statusCode(500);
            }

        } else {
            throwException.statusCode(500);
        }

        return resultMap;

    }

    @Override
    public Map<String, Object> joinAuthResult(Map<String, Object> paramMap) throws Exception {

        Map<String, Object> resultMap = new HashMap<>();

        if (paramMap != null && !paramMap.isEmpty()) {

            String resultVal = Common.NVL(paramMap.get("encKey"), "");
            String enc_data = Common.NVL(paramMap.get("encData"), "");
            //
            final String key = resultVal.substring(0, 16);
            final String iv = resultVal.substring(resultVal.length() - 16);

            //-- 인증결과 데이터 복호화
            SecretKey secretKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv.getBytes()));
            byte[] cipherEnc = Base64.decodeBase64(enc_data);
            String res_data = new String(c.doFinal(cipherEnc), "euc-kr");

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> resMap = objectMapper.readValue(res_data, Map.class);
            //
            if (resMap != null && !resMap.isEmpty()) {

                String gender = Common.NVL(resMap.get("gender"), "");    // (0:여성  , 1:남성)
                String national = Common.NVL(resMap.get("nationalinfo"), "");    // (0:내국인, 1:외국인)
                int mobileco = Common.parseInt(resMap.get("mobileco"));
                String[] mobileType = {"SK텔레콤", "KT", "LGU+", "", "SK텔레콤 알뜰폰", "KT 알뜰폰", "LGU+ 알뜰폰"};

                resultMap.put("staffName", resMap.get("name"));
                resultMap.put("staffMobile", resMap.get("mobileno"));
                resultMap.put("staffMobileType", mobileType[mobileco - 1]);
                resultMap.put("staffBirth", resMap.get("birthdate"));
                resultMap.put("staffDi", resMap.get("di"));
                resultMap.put("staffCi", resMap.get("ci"));
                resultMap.put("staffGender", "0".equals(gender) ? "F" : "M");
                resultMap.put("staffNational", "0".equals(national) ? "Y" : "N");

            } else {
                throwException.statusCode(500);
            }

        } else {
            throwException.statusCode(500);
        }

        return resultMap;
    }

    @Override
    public Map<String, Object> checkCompanyId(Map<String, Object> paramMap) throws Exception {

        List<CompanyVo> companyVos = joinDao.checkCompanyId(paramMap);
        //
        Map<String, Object> resultMap = new HashMap<>();

        if (companyVos != null && companyVos.size() > 0) {

            resultMap.put("dupYn", "Y");
//			throwException.statusCode(409);
        } else {
            resultMap.put("dupYn", "N");
        }

        return resultMap;
    }
    
    @Override
	public long registerPgPayLogEntry(
					long loginCompanyPk,
					long loginCompanyStaffPk
					) {
		
		Map<String,Object> paramMap = new HashMap<>();
		paramMap.put("loginCompanyPk",loginCompanyPk);
		paramMap.put("pay_name", null); 
		paramMap.put("pay_method", null); 
		paramMap.put("pay_company_cd", null);
		paramMap.put("pay_company_name", null);
		paramMap.put("pay_key", null); 
		paramMap.put("tran_cd", null);   
		paramMap.put("pg_err_cd", null); 
		paramMap.put("pg_err_desc", null); 
		paramMap.put("item_cd", "CARD_SM_001");
		paramMap.put("pk_company_staff",loginCompanyStaffPk);
		paramMap.put("bill_status", "B20102");
		creditDao.registerPgPayLog(paramMap);
		return (long) paramMap.get("pk_pg_pay_log");
	}

}
