
package com.saltlux.aice_fe.pc.auth.dao;

import com.saltlux.aice_fe.pc.join.vo.CompanyStaffVo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface PcAuthDao {

	CompanyStaffVo  getCompanyStaffById(CompanyStaffVo reqVo);

	CompanyStaffVo  getCompanyStaffByKakao (CompanyStaffVo reqVo);

	CompanyStaffVo  getCompanyStaffByPk(CompanyStaffVo reqVo);
	
	CompanyStaffVo  getCompanyStaffByPkNoLevelCode(CompanyStaffVo reqVo);

	void  resetCompanyStaffTicketCode(CompanyStaffVo reqVo);
	
	int updateAgree(String fkCompanyStaff, String agreeValue, String fkCompany);
}

