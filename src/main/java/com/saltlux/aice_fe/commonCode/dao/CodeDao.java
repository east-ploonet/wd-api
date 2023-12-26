package com.saltlux.aice_fe.commonCode.dao;

import com.saltlux.aice_fe.commonCode.vo.CodeVo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface CodeDao {

    List<CodeVo> selectCodeList(CodeVo codeVo) throws SQLException;
    
    List<Object> staffWorkCodeList(Map<String, Object> reqJsonObj) throws SQLException;
    
    List<Object> selectUseStaffWorkCodeList(Map<String, Object> reqJsonObj) throws SQLException;
    
    List<Object> staffWorkCtgrCodeList(Map<String, Object> reqJsonObj) throws SQLException;
}
