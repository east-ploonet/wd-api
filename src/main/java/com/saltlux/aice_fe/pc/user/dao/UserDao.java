package com.saltlux.aice_fe.pc.user.dao;

import com.saltlux.aice_fe.pc.join.vo.CompanyStaffVo;
import com.saltlux.aice_fe.pc.join.vo.CompanyVo;
import com.saltlux.aice_fe.pc.join.vo.StatisticVo;
import com.saltlux.aice_fe.pc.join.vo.TermsVo;
import com.saltlux.aice_fe.pc.my_page.vo.UpdateMyPageVo;
import com.saltlux.aice_fe.pc.user.vo.Request.UserReqBody;

import com.saltlux.aice_fe.pc.user.vo.Response.UserInfoVo;
import com.saltlux.aice_fe.pc.user.vo.Response.UserSimbolVo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface UserDao {

    List<StatisticVo> getItems();

    Long checkBeforeSaveStaff(String email);

    void companyStaffSave(UserReqBody reqBody);

    int companySave(UserReqBody reqBody);

    List<CompanyStaffVo> checkEmail(String email);

    List<CompanyStaffVo> checkNickName(String nickName);

    void companyStaffTermsSave(UserReqBody reqBody);

    List<TermsVo> getAllTerms();

    void statisticSave(UserReqBody reqBody);

    void statisticMyPage(UpdateMyPageVo reqBody);

    UserInfoVo getUserInfo(long loginCompanyStaffPk);

    TermsVo getTerm();
    
    Map<String, Object> getAivoice(Map<String, Object> resultMap); 
    
    void updateFile(Map<String, Object> paramMap);

    void saveAdmin(UserReqBody reqBody);

    String getPassword(long loginCompanyStaffPk);

    void updatePassword(long loginCompanyStaffPk, String staffPw);

    UserSimbolVo getSimbol(long loginCompanyPk);

    String getCompanyId(long pk_company);
}
