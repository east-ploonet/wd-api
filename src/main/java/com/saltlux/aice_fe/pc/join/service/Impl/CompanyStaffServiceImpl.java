package com.saltlux.aice_fe.pc.join.service.Impl;

import com.saltlux.aice_fe.pc.join.dao.CompanyStaffDao;
import com.saltlux.aice_fe.pc.join.dao.JoinDao;
import com.saltlux.aice_fe.pc.join.service.CompanyStaffService;
import com.saltlux.aice_fe.pc.join.vo.CompanyStaffVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Slf4j
@Service
public class CompanyStaffServiceImpl implements CompanyStaffService {

    @Autowired
    private CompanyStaffDao companyStaffDao;

    @Override
    public List<CompanyStaffVo> findAll() throws SQLException {
        return companyStaffDao.findAllCompanyStaff();
    }
}
