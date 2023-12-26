package com.saltlux.aice_fe.pc.user.service.impl;

import com.saltlux.aice_fe._baseline.baseService.FileService;
import com.saltlux.aice_fe._baseline.baseService.impl.BaseServiceImpl;
import com.saltlux.aice_fe._baseline.commons.Common;
import com.saltlux.aice_fe._baseline.util.FormatUtils;
import com.saltlux.aice_fe.ecApi.service.PloonetApiService;
import com.saltlux.aice_fe.email.service.EmailService;
import com.saltlux.aice_fe.pc.auth.service.PcAuthService;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.join.vo.CompanyStaffVo;
import com.saltlux.aice_fe.pc.join.vo.StatisticVo;
import com.saltlux.aice_fe.pc.join.vo.TermsVo;
import com.saltlux.aice_fe.pc.user.dao.UserDao;
import com.saltlux.aice_fe.pc.user.service.UserService;
import com.saltlux.aice_fe.pc.user.vo.Request.UserReqBody;
import com.saltlux.aice_fe.pc.user.vo.Response.UserInfoVo;
import com.saltlux.aice_fe.pc.user.vo.Response.UserSimbolVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl extends BaseServiceImpl implements UserService {

    @Value("${path.browser.storage}")
    protected String pathBrowserStorage;

    @Autowired
    PloonetApiService ploonetApiService;

    @Autowired
    UserDao userDao;

    @Autowired
    FileService fileService;

    @Autowired
    PcAuthService pcAuthService;

    @Autowired
    EmailService emailService;


    @Override
    public void save(UserReqBody reqBody) throws Exception {
        final String staffPw = BCRYPT_ENCODER.encode(Common.NVL(reqBody.getFd_staff_pw(), ""));
        reqBody.setFd_staff_pw(staffPw);
//        List<FileVo> fileVoList = fileService.uploadFileToVoList( uploadFile, "TBL_COMPANY" );
//        if( fileVoList != null && fileVoList.size() > 0 && fileVoList.get(0) != null ) {
//            reqBody.setFd_company_logo_file_name( fileVoList.get(0).getFd_file_name() );
//            reqBody.setFd_company_logo_file_path( fileVoList.get(0).getFd_file_path() );
//        }


        userDao.companySave(reqBody);
        userDao.companyStaffSave(reqBody);
        if (reqBody.getStatisticVoList().size() > 0) {
            userDao.statisticSave(reqBody);
        }

        userDao.companyStaffTermsSave(reqBody);
        CompanyStaffVo companyStaffVo = new CompanyStaffVo();
        reqBody.setUser_type("B2002");
        companyStaffVo.setPk_company_staff(reqBody.pk_company_staff);
        companyStaffVo.setFd_staff_id(reqBody.getFd_staff_id());
        companyStaffVo.setFd_staff_mobile(reqBody.getFd_staff_mobile());
        companyStaffVo.setFd_company_id(reqBody.getFd_company_id());
        companyStaffVo.setFd_staff_name(reqBody.getFd_staff_name());
        
        Map<String, String> map = new HashMap<>();
        List<String> emailList = new ArrayList<>();
        emailList.add(companyStaffVo.getFd_staff_id());
        String[] email = companyStaffVo.getFd_staff_id().split("@");
        String userEmail = email[0].replaceAll("(?<=.{3}).", "*") + "@" + email[1].replaceAll("(?<=.{2}).", "*");
        String company_id = reqBody.getFd_company_id().replaceAll("(?<=.{3}).", "*");
        map.put("email", userEmail);
        map.put("company_id", company_id);
        map.put("userType", "개인회원");
        LocalDate now = LocalDate.now();
        map.put("regDate", now.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")));
        emailService.sendEmail(emailList, "회원가입을 환영합니다.", "join.html", map);
        pcAuthService.resetCompanyStaffTicketIndividualCode(companyStaffVo);
    }

    @Override
    public Map<String, Object> checkEmail(String email) {
        List<CompanyStaffVo> companyStaffVo = userDao.checkEmail(email);
        Map<String, Object> resultMap = new HashMap<>();

        if (companyStaffVo != null && companyStaffVo.size() < 1) {
            resultMap.put("dupYn", "Y");
//			throwException.statusCode(409);
        } else {
            resultMap.put("dupYn", "N");
        }


        return resultMap;
    }

    @Override
    public Map<String, Object> checkNickName(String nickname) {
        List<CompanyStaffVo> companyStaffVo = userDao.checkNickName(nickname);
        Map<String, Object> resultMap = new HashMap<>();

        if (companyStaffVo != null && companyStaffVo.size() < 1) {
            resultMap.put("dupYn", "Y");
//			throwException.statusCode(409);
        } else {
            resultMap.put("dupYn", "N");
        }


        return resultMap;
    }

    @Override
    public Map<String, Object> getAllTerms() {
        List<TermsVo> listTermsVo = userDao.getAllTerms();
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> listMap = new ArrayList<>();

        if (listTermsVo == null) {
            throwException.statusCode(204);

        } else {

            for (TermsVo termsVo : listTermsVo) {

                Map<String, Object> mapAdd = new HashMap<>();
                mapAdd.put("pk_terms", termsVo.getPk_terms());
                mapAdd.put("termsTargetCode", termsVo.getFd_terms_target_code());
                mapAdd.put("disp_order", termsVo.getDisp_order());
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
    public Map<String, Object> getItems() {
        List<StatisticVo> statisticVoList = userDao.getItems();
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> listMap = new ArrayList<>();
        if (statisticVoList == null) {
            throwException.statusCode(204);

        } else {
            for (StatisticVo statisticVo : statisticVoList) {
                Map<String, Object> mapAdd = new HashMap<>();
                mapAdd.put("pk_statistic_item", statisticVo.getPk_statistic_item());
                mapAdd.put("fk_statistic_group", statisticVo.getFk_statistic_group());
                mapAdd.put("disp_order", statisticVo.getDisp_order());
                mapAdd.put("item_name", statisticVo.getItem_name());
                mapAdd.put("input_yn", statisticVo.getInput_yn());
                result.put("select_type", statisticVoList.get(0).getSelect_type());
                listMap.add(mapAdd);
            }
        }

        result.put("statisticVoList", listMap);
        return result;
    }

    @Override
    public Map<String, Object> getUserInfo() {
        Map<String, Object> result = new HashMap<>();
        PcLoginInfoVo pcLoginInfoVo = getPcLoginInfoVo();
        if (pcLoginInfoVo != null) {
            UserInfoVo userInfo = userDao.getUserInfo(pcLoginInfoVo.getLoginCompanyStaffPk());
            result.put("userInfo", userInfo);
            return result;
        }

        return null;
    }

    @Override
    public Map<String, Object> getQuestionTerm() {
        Map<String, Object> result = new HashMap<>();
        TermsVo termsVo = userDao.getTerm();
        result.put("termsVo", termsVo);
        return result;
    }

    @Override
    public void saveAdmin(UserReqBody reqBody) {
        final String staffPw = BCRYPT_ENCODER.encode(Common.NVL(reqBody.getFd_staff_pw(), ""));
        reqBody.setFd_staff_pw(staffPw);
        userDao.saveAdmin(reqBody);
    }


    @Override
    public Map<String, Object> getSimbol(PcLoginInfoVo pcLoginInfoVo) {
        UserSimbolVo simbolVo = userDao.getSimbol(pcLoginInfoVo.getLoginCompanyPk());
        String path = pathBrowserStorage + simbolVo.getFd_company_logo_file_path();
        simbolVo.setFd_company_logo_file_path(path);
        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("simbolVo", simbolVo);
        return resultMap;
    }
    
    @Override
    public Map<String, Object> getAivoice(Map<String, Object> paramMap) {
        Map<String, Object> resultMap = new HashMap<>();
        
        PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
        resultMap.put("fkCompany", loginInfoVo.getLoginCompanyPk());
        
        Map<String, Object> result = userDao.getAivoice(resultMap);
        
        return result;
    }
    


}
