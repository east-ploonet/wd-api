package com.saltlux.aice_fe.app.auth.dao;

import com.saltlux.aice_fe.app.auth.vo.AppVersionVo;
import com.saltlux.aice_fe.pc.join.vo.CompanyStaffVo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface AppAuthDao {
	List<CompanyStaffVo>  getCompanyStaffBySignupCode(CompanyStaffVo reqVo);
	CompanyStaffVo  getCompanyStaffByPk(CompanyStaffVo reqVo);
	int             updateCompanyStaffLogin(CompanyStaffVo reqVo);
	int             insertCompanyStaffLogin(CompanyStaffVo reqVo);

	List<AppVersionVo> getAppVersion();

}
