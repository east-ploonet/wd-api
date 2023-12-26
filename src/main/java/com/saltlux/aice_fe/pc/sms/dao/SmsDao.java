package com.saltlux.aice_fe.pc.sms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.saltlux.aice_fe._baseline.baseVo.CustomSqlSessionDaoSupport;
import com.saltlux.aice_fe._baseline.baseVo.DataMap;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.sms.vo.SendFileVo;

@Repository
public class SmsDao extends CustomSqlSessionDaoSupport {
		
	@Autowired
	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }
	
	public List<Object> getInfo(PcLoginInfoVo pcLoginInfoVo) {
		return (List<Object>)getSqlSession().selectList("com.saltlux.aice_fe.pc.sms.dao.SmsDao.getInfo", pcLoginInfoVo); 
	}
	
	public int tranRegist(Map<String, Object> paramMapInsert) {
		return (int)getSqlSession().insert("com.saltlux.aice_fe.pc.sms.dao.SmsDao.tranRegist", paramMapInsert);
	}
	
	public int tranConsigneeRegist(Map<String, Object> ConsigneeInsert) {
		return (int)getSqlSession().insert("com.saltlux.aice_fe.pc.sms.dao.SmsDao.tranConsigneeRegist", ConsigneeInsert);
	}
	
	public int tranFileRegist(SendFileVo sendTranFileVo) {
		return (int)getSqlSession().insert("com.saltlux.aice_fe.pc.sms.dao.SmsDao.tranFileRegist", sendTranFileVo);
	}
	
}
