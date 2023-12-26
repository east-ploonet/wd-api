package com.saltlux.aice_fe.pc.admin.dao;

import com.saltlux.aice_fe.pc.admin.vo.AdminVo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Mapper
@Repository
public interface AdminDao {
    void saveAdmin(AdminVo adminVo);

    AdminVo getAdminById(Map<String, Object> bodyMap);

    AdminVo getAdminByPk (AdminVo reqVo);
}
