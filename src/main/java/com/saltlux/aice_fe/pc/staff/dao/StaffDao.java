package com.saltlux.aice_fe.pc.staff.dao;

import com.saltlux.aice_fe.pc.join.vo.CompanyDeptStaffVo;
import com.saltlux.aice_fe.pc.join.vo.CompanyDeptTagVo;
import com.saltlux.aice_fe.pc.join.vo.CompanyStaffVo;
import com.saltlux.aice_fe.pc.join.vo.CompanyDeptVo;
import com.saltlux.aice_fe.pc.join.vo.CompanyVo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.springframework.stereotype.Repository;

import java.util.List;

import java.sql.SQLException;
import java.util.Map;

@Mapper
@Repository
public interface StaffDao {

    List<CompanyDeptVo> selectCompanyDeptList(CompanyStaffVo vo) throws SQLException;
	int staffDefaultAiCnt(CompanyStaffVo vo)     throws SQLException;
	CompanyDeptVo checkDeptName(CompanyDeptVo vo) throws SQLException;
    int insertCompanyDept(CompanyDeptVo vo) throws SQLException;
    int updateCompanyDept(CompanyDeptVo vo) throws SQLException;
    int deleteCompanyDept(CompanyDeptVo vo) throws SQLException;
    int staffCountDept(CompanyDeptStaffVo vo) throws SQLException;
    List<Object> getDeptDispName(Map<String, Object> paramMap) throws SQLException;
	List<CompanyDeptVo> selectStaffDeptList(CompanyDeptVo vo) throws SQLException;
	int DeptListPagingCnt(Map<String, Object> paramMap) throws SQLException;
	int                  getStaffAllCnt(CompanyStaffVo vo);
	int                  getStaffAllListCnt(CompanyStaffVo vo);
	List<CompanyStaffVo> getStaffList(CompanyStaffVo vo);
	List<CompanyStaffVo> getStaffListPaging(CompanyStaffVo vo);
	CompanyStaffVo       getStaff(CompanyStaffVo vo);
	int insertCompanyStaff(CompanyStaffVo vo)     throws SQLException;
	int updateStaff(CompanyStaffVo vo)     throws SQLException;
	int updateStaffAsCustomer(CompanyStaffVo vo)     throws SQLException;
	int updateDeptStaff(CompanyDeptStaffVo vo) throws SQLException;
	int deleteStaff(CompanyStaffVo vo)     throws SQLException;
	int deleteDeptStaff(CompanyDeptStaffVo vo) throws SQLException;
	int deleteStaffList(CompanyStaffVo vo)     throws SQLException;
	int deleteDeptStaffList(CompanyDeptStaffVo vo) throws SQLException;
	CompanyStaffVo checkStaffEmail(Map<String, Object> paramMap) throws SQLException;
	CompanyStaffVo checkStaffAccountID(Map<String, Object> paramMap) throws SQLException;
	int updateCompanyLogo(CompanyVo vo) throws SQLException;
	int updateStaffResponseStatus(CompanyStaffVo vo) throws SQLException;

	CompanyStaffVo checkCompanyMaster(Map<String, Object> paramMap) throws SQLException;
	int companyMasterCnt(Map<String, Object> paramMap) throws SQLException;

	int updateCompanyMasterN(CompanyStaffVo vo) throws SQLException;

	CompanyStaffVo getById(Long id);
	
	int insertCompanyDeptTag(CompanyDeptTagVo vo) throws SQLException;
	
	Map<String, Object> masterStaffUuid(Map<String, Object> paramMap) throws SQLException;
	
	Map<String, Object> ploonianModify(Map<String, Object> paramMap) throws SQLException;

}
