package com.saltlux.aice_fe.pc.staff.dao;

import com.saltlux.aice_fe.pc.join.vo.CompanyStaffVo;
import com.saltlux.aice_fe.pc.staff.vo.AIStaffVo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface AIStaffDao {

	int selectAIstaffCount(Map<String, Object> paramMap);
	List<Map<String, Object>> selectAIstaffList(Map<String, Object> paramMap) throws SQLException;
	Map<String, Object> getAIstaff(Map<String, Object> paramMap) throws SQLException;
	Map<String, Object> getAIMainStaff(Map<String, Object> paramMap) throws SQLException;
	Map<String, Object> getBrandTerms(Map<String, Object> paramMap) throws SQLException;
	
	int updateAIstaff(CompanyStaffVo vo)throws SQLException;
	int workCodeUpdate(CompanyStaffVo vo)throws SQLException;
	int allMainAiUpdate(Map<String, Object> paramMap)throws SQLException;
	int newMainUpdate(Map<String, Object> paramMap)throws SQLException;
	int updateAIStaffStatus(Map<String, Object> paramMap)throws SQLException;
	



}
