package com.saltlux.aice_fe.pc.staff.service;

import com.saltlux.aice_fe.pc.join.vo.CompanyDeptStaffVo;

public interface StaffDeptService {

    // update part of key
    int updateDeptStaff(CompanyDeptStaffVo companyDeptStaffOld, CompanyDeptStaffVo companyDeptStaffNew) throws Exception;

    int updateDeptStaffNew(CompanyDeptStaffVo companyDeptStaffOld, CompanyDeptStaffVo companyDeptStaffNew) throws Exception;
    
    // update by composite keys
    int updateDeptStaff(CompanyDeptStaffVo companyDeptStaff) throws Exception;
}
