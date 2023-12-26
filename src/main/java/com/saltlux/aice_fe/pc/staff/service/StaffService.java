package com.saltlux.aice_fe.pc.staff.service;

import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.join.vo.CompanyDeptStaffVo;
import com.saltlux.aice_fe.pc.join.vo.CompanyDeptVo;

import java.util.HashMap;
import java.util.Map;

import com.saltlux.aice_fe.pc.join.vo.CompanyStaffVo;


public interface StaffService {

    Map<String, Object> selectCompanyDeptList(CompanyStaffVo vo) throws Exception;
    Map<String, Object> selectCompanyDeptListDefault(CompanyStaffVo vo) throws Exception;
    void checkDeptName(CompanyDeptVo vo) throws Exception;
    void registDept(Map<String, Object> paramMap) throws Exception;
    void updateDept(Map<String, Object> paramMap) throws Exception;
    void deleteDept(Map<String, Object> paramMap) throws Exception;
	Map<String, Object> staffCountDept(CompanyDeptStaffVo vo) throws Exception;

	Map<String, Object> selectStaffDeptList(CompanyDeptVo vo) throws Exception;
	Map<String, Object> getStaffListPaging(CompanyStaffVo companyStaffVo) throws Exception;
	Map<String, Object> getStaffList(CompanyStaffVo companyStaffVo) throws Exception;
	Map<String, Object> getStaff(CompanyStaffVo companyStaffVo,PcLoginInfoVo pcLoginInfoVo) throws Exception;
	void registStaff(Map<String, Object> paramMap) throws Exception;
	void updateStaff(Map<String, Object> paramMap) throws Exception;
	void deleteStaff(Map<String, Object> paramMap) throws Exception;
	void deleteStaffList(Map<String, Object> paramMap) throws Exception;

	int checkStaffEmail(Map<String, Object> paramMap) throws Exception;
	int checkStaffAccountID(Map<String, Object> paramMap) throws Exception;
	CompanyStaffVo registerStaffInCompany(CompanyStaffVo companyStaffVo) throws Exception;
	PcLoginInfoVo updateCompanyLogo(Map<String, Object> paramMap) throws Exception;
	void updateStaffResponseStatus(Map<String, Object> paramMap) throws Exception;

	Map<String, Object> companyMasterCnt(Map<String, Object> paramMap) throws Exception;

	CompanyStaffVo registerDeptStaff(CompanyStaffVo companyStaffVo) throws Exception;
	
	Map<String, Object> masterStaffUuid(Map<String, Object> paramMap) throws Exception;
	
	int updateRegisterStaff(CompanyStaffVo companyStaffVo) throws Exception;
	
	Map<String, Object> ploonianModify(Map<String, Object> paramMap) throws Exception;
	
	
}
