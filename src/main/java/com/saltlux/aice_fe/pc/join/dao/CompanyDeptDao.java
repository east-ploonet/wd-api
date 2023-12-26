package com.saltlux.aice_fe.pc.join.dao;

import com.saltlux.aice_fe.pc.join.vo.CompanyDeptVo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface CompanyDeptDao {

    int updateNameByPk(CompanyDeptVo companyDeptVo);
}
