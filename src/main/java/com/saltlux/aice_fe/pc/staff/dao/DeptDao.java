package com.saltlux.aice_fe.pc.staff.dao;

import com.saltlux.aice_fe.pc.join.vo.CompanyDeptStaffVo;
import com.saltlux.aice_fe.pc.join.vo.CompanyDeptVo;
import com.saltlux.aice_fe.pc.join.vo.CompanyStaffVo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;

@Mapper
@Repository
public interface DeptDao {

    List<CompanyDeptVo> getDefaultDeptMaster(CompanyStaffVo vo) throws SQLException;
    int insertCompanyDeptStaff(CompanyDeptStaffVo vo) throws SQLException;
    int updateDeptStaffDeptMasterN(CompanyDeptStaffVo vo) throws SQLException;
    int updateDeptStaff(CompanyDeptStaffVo vo) throws SQLException;
    void deleteStaffDept(CompanyDeptStaffVo companyDeptStaffVo);
    void deleteStaffDeptNew(CompanyDeptStaffVo companyDeptStaffVo);
    
    CompanyDeptStaffVo getOne(CompanyDeptStaffVo vo) throws SQLException;
}
