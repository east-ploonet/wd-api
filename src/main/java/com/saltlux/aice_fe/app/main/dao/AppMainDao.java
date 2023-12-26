package com.saltlux.aice_fe.app.main.dao;

import com.saltlux.aice_fe.commonCode.vo.CodeVo;
import com.saltlux.aice_fe.pc.join.vo.CompanyStaffVo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;

@Mapper
@Repository
public interface AppMainDao {

	List<CodeVo> getMyBizState(CompanyStaffVo reqVo) throws SQLException;
	int setMyBizState(CompanyStaffVo reqVo) throws SQLException;
	CompanyStaffVo getMyPage(CompanyStaffVo reqVo) throws SQLException;
	int setStaffPushNotiYn(CompanyStaffVo reqVo) throws SQLException;

}
