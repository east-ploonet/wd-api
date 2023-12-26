package com.saltlux.aice_fe.pc.staff.service.Impl;

import com.saltlux.aice_fe.pc.join.vo.CompanyDeptStaffVo;
import com.saltlux.aice_fe.pc.staff.dao.DeptDao;
import com.saltlux.aice_fe.pc.staff.service.StaffDeptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StaffDeptServiceImpl implements StaffDeptService {

    @Autowired
    private DeptDao deptDao;

    @Override
    public int updateDeptStaff(CompanyDeptStaffVo companyDeptStaffOld, CompanyDeptStaffVo companyDeptStaffNew) throws Exception {
        companyDeptStaffNew.setFd_dept_master_yn(companyDeptStaffOld.getFd_dept_master_yn());
        deptDao.deleteStaffDept(companyDeptStaffOld);
        return deptDao.insertCompanyDeptStaff(companyDeptStaffNew);
    }
    
    @Override
    public int updateDeptStaffNew(CompanyDeptStaffVo companyDeptStaffOld, CompanyDeptStaffVo companyDeptStaffNew) throws Exception {
        companyDeptStaffNew.setFd_dept_master_yn(companyDeptStaffOld.getFd_dept_master_yn());
        deptDao.deleteStaffDeptNew(companyDeptStaffOld);
        return deptDao.insertCompanyDeptStaff(companyDeptStaffNew);
    }
    
    @Override
    public int updateDeptStaff(CompanyDeptStaffVo companyDeptStaff) throws Exception {
        return deptDao.updateDeptStaff(companyDeptStaff);
    }
}
