package com.saltlux.aice_fe.pc.join.dao;

import com.saltlux.aice_fe.pc.join.vo.*;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface JoinDao {

	List<TermsVo> getTermsList(TermsVo vo);

	//
	int insertCompanyTerms(Map<String, Object> paramMap) throws SQLException;
	int insertStaffTerms(Map<String, Object> paramMap) throws SQLException;
	//
	int insertCompany(CompanyVo vo)           throws SQLException;
	int insertCompanyStaff(CompanyStaffVo vo)      throws SQLException;
	long insertCompanyStaffAi(CompanyStaffVo vo)      throws SQLException;
	int insertCompanyDept(CompanyDeptVo vo)       throws SQLException;
	int insertCompanyDeptStaff(CompanyDeptStaffVo vo)  throws SQLException;
	//
	int updateCompanyInfo(CompanyVo vo)      throws SQLException;
	int updateCompany(CompanyVo vo)      throws SQLException;
	int updateCompanyStaffInfo(CompanyStaffVo vo) throws SQLException;

	CompanyStaffVo checkStaffId(Map<String, Object> paramMap) throws SQLException;
	CompanyVo dupCheckBizNumber(Map<String, Object> paramMap) throws SQLException;
	List<CompanyVo> checkCompanyId(Map<String, Object> paramMap) throws SQLException;

}
