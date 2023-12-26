package com.saltlux.aice_fe.pc.join.service;

import com.saltlux.aice_fe.pc.join.vo.CompanyStaffVo;

import java.sql.SQLException;
import java.util.List;

public interface CompanyStaffService {

    List<CompanyStaffVo> findAll() throws SQLException;
}
