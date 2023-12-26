package com.saltlux.aice_fe.pc.quick.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.saltlux.aice_fe._baseline.baseVo.CustomSqlSessionDaoSupport;
import com.saltlux.aice_fe._baseline.baseVo.DataMap;
import com.saltlux.aice_fe.pc.send.vo.SendCustomerVo;

@Repository
public class QuickDao extends CustomSqlSessionDaoSupport {
		
	@Autowired
	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }

	public DataMap getQuickStartUserInfo(Map<String, Object> paramMap) {
		return getSqlSession().selectOne("com.saltlux.aice_fe.pc.quick.dao.QuickDao.getQuickStartUserInfo", paramMap);
	}
	
	public DataMap getQuickStartCompanyInfo(Map<String, Object> paramMap) {
		return getSqlSession().selectOne("com.saltlux.aice_fe.pc.quick.dao.QuickDao.getQuickStartCompanyInfo", paramMap);
	}
	
	//quick 개인 업데이트
	public int updateQuickStartUserInfo(Map<String, Object> paramMap) {
		return (int)getSqlSession().update("com.saltlux.aice_fe.pc.quick.dao.QuickDao.updateQuickStartUserInfo", paramMap);
	}
	
	//quick 개인 업데이트
	public int updateQuickStartCompanyInfo(Map<String, Object> paramMap) {
		return (int)getSqlSession().update("com.saltlux.aice_fe.pc.quick.dao.QuickDao.updateQuickStartCompanyInfo", paramMap);
	}
}
