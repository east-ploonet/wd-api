package com.saltlux.aice_fe.pc.receiveNumber.dao;

import com.saltlux.aice_fe.pc.join.vo.CompanyStaffVo;
import com.saltlux.aice_fe.pc.staff.vo.AIStaffVo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface ReceiveNumberDao {

	List<Map<String, Object>> getNumberStaffList(Map<String, Object> paramMap) throws SQLException;
	
	List<Map<String, Object>> getNumberStaffListComplete(Map<String, Object> paramMap) throws SQLException;

}
