package com.saltlux.aice_fe.pc.join.dao;

import com.saltlux.aice_fe.pc.join.vo.CompanyStaffVo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;

@Mapper
@Repository
public interface CompanyStaffDao {

    List<CompanyStaffVo> findAllCompanyStaff() throws SQLException;
}
