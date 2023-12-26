package com.saltlux.aice_fe.pc.send.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.saltlux.aice_fe._baseline.baseVo.CustomSqlSessionDaoSupport;
import com.saltlux.aice_fe._baseline.baseVo.DataMap;
import com.saltlux.aice_fe.pc.send.vo.SendCustomerVo;

@Repository
public class SendDao extends CustomSqlSessionDaoSupport {
		
	@Autowired
	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }

	public DataMap getCustomerInfo(Map<String, Object> paramMap) {
		return getSqlSession().selectOne("com.saltlux.aice_fe.pc.send.dao.SendDao.getCustomerInfo", paramMap);
	}
	
	public List<Object> getNewCompanyCustomer(Map<String, Object> paramMap) {
		return (List<Object>)getSqlSession().selectList("com.saltlux.aice_fe.pc.send.dao.SendDao.getNewCompanyCustomer", paramMap);
	}
	
	public List<Object> getNewCompanyCustomerList(Map<String, Object> paramMap) {
		return (List<Object>)getSqlSession().selectList("com.saltlux.aice_fe.pc.send.dao.SendDao.getNewCompanyCustomerList", paramMap);
	}
	
	public int companyCustomerUpdate(SendCustomerVo sendCustomerVo) {
		return (int)getSqlSession().update("com.saltlux.aice_fe.pc.send.dao.SendDao.companyCustomerUpdate", sendCustomerVo);
	}
	
	public int newCompanyCustomer(Map<String, Object> paramMap) {
		return (int)getSqlSession().insert("com.saltlux.aice_fe.pc.send.dao.SendDao.newCompanyCustomer", paramMap);
	}
	
	public int isNewCompanyCustomer(Map<String, Object> paramMap) {
		return (int)getSqlSession().selectOne("com.saltlux.aice_fe.pc.send.dao.SendDao.isNewCompanyCustomer", paramMap);
	}
	
	public int saveSend(Map<String, Object> paramMap) {
		return (int)getSqlSession().insert("com.saltlux.aice_fe.pc.send.dao.SendDao.saveSend", paramMap);
	}
	
	public int saveSendCustomer(Map<String, Object> paramMap) {
		return (int)getSqlSession().insert("com.saltlux.aice_fe.pc.send.dao.SendDao.saveSendCustomer", paramMap);
	}
	
	public List<Object> getSendHistory(Map<String, Object> paramMap) {
		return (List<Object>)getSqlSession().selectList("com.saltlux.aice_fe.pc.send.dao.SendDao.getSendHistory", paramMap);
	}
	public int getSendHistoryCnt(Map<String, Object> paramMap) {
		return (int)getSqlSession().selectOne("com.saltlux.aice_fe.pc.send.dao.SendDao.getSendHistoryCnt", paramMap);
	}
	
	public List<Object> getSendUserHistory(Map<String, Object> paramMap) {
		return (List<Object>)getSqlSession().selectList("com.saltlux.aice_fe.pc.send.dao.SendDao.getSendUserHistory", paramMap);
	}
	public int getSendUserHistoryCnt(Map<String, Object> paramMap) {
		return (int)getSqlSession().selectOne("com.saltlux.aice_fe.pc.send.dao.SendDao.getSendUserHistoryCnt", paramMap);
	}
	
	public List<Object> getSendList(Map<String, Object> paramMap) {
		return (List<Object>)getSqlSession().selectList("com.saltlux.aice_fe.pc.send.dao.SendDao.getSendList", paramMap);
	}

	public List<Object> getSendListCustomer(Map<String, Object> paramMap) {
		return (List<Object>)getSqlSession().selectList("com.saltlux.aice_fe.pc.send.dao.SendDao.getSendListCustomer", paramMap);
	}
	
	public DataMap getDetailInfo(Map<String, Object> paramMap) {
		return getSqlSession().selectOne("com.saltlux.aice_fe.pc.send.dao.SendDao.getDetailInfo", paramMap);
	}
	
	public List<Object> getDetailCustomer(Map<String, Object> paramMap) {
		return (List<Object>)getSqlSession().selectList("com.saltlux.aice_fe.pc.send.dao.SendDao.getDetailCustomer", paramMap);
	}
	
	public List<Object> getCompanyCustomer(Map<String, Object> paramMap) {
		return (List<Object>)getSqlSession().selectList("com.saltlux.aice_fe.pc.send.dao.SendDao.getCompanyCustomer", paramMap);
	}
	
	public int companyCustomerCnt(Map<String, Object> paramMap) {
		return (int)getSqlSession().selectOne("com.saltlux.aice_fe.pc.send.dao.SendDao.companyCustomerCnt", paramMap);
	}
	
	public List<Object> defaultYnUse(Map<String, Object> paramMap) {
		return (List<Object>)getSqlSession().selectList("com.saltlux.aice_fe.pc.send.dao.SendDao.defaultYnUse", paramMap);
	}
	
	
	public DataMap selectDnis(Map<String, Object> paramMap) {
		return getSqlSession().selectOne("com.saltlux.aice_fe.pc.send.dao.SendDao.selectDnis", paramMap);
	}
	
}
