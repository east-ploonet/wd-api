package com.saltlux.aice_fe.pc.campaginsendList.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.saltlux.aice_fe._baseline.baseVo.CustomSqlSessionDaoSupport;
import com.saltlux.aice_fe.pc.campaginsendList.vo.CampaginSendInfoVo;
import com.saltlux.aice_fe.pc.campaginsendList.vo.CampaginSendListInfoVo;
import com.saltlux.aice_fe.pc.campaginsendList.vo.CampaginSendListInfoVo2;


@Repository
public class CampaginSendListDao extends CustomSqlSessionDaoSupport {
		
	@Autowired
	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }
	
	public int infoRegistManagement(CampaginSendListInfoVo sendListInfoVo) {
		return (int)getSqlSession().insert("com.saltlux.aice_fe.pc.campaginsendList.dao.CampaginSendListDao.infoRegistManagement", sendListInfoVo);
	}
	
	public int infoRegistList(CampaginSendInfoVo sendInfoVo) {
		return (int)getSqlSession().insert("com.saltlux.aice_fe.pc.campaginsendList.dao.CampaginSendListDao.infoRegistList", sendInfoVo);
	}
	//업데이트 구문
	public int infoUpdateManagement(CampaginSendListInfoVo sendListInfoVo) {
		return (int)getSqlSession().insert("com.saltlux.aice_fe.pc.campaginsendList.dao.CampaginSendListDao.infoUpdateManagement", sendListInfoVo);
	}
	
	public int infoUpdateList(CampaginSendInfoVo sendInfoVo) {
		return (int)getSqlSession().update("com.saltlux.aice_fe.pc.campaginsendList.dao.CampaginSendListDao.infoUpdateList", sendInfoVo);
	}
	
	public int sendListCnt(Map<String, Object> paramMap) {
		return (int)getSqlSession().selectOne("com.saltlux.aice_fe.pc.campaginsendList.dao.CampaginSendListDao.sendListCnt", paramMap);
	}
	
	public int sendListCheck(Map<String, Object> paramMap) {
		return (int)getSqlSession().selectOne("com.saltlux.aice_fe.pc.campaginsendList.dao.CampaginSendListDao.sendListCheck", paramMap);
	}
	
	public List<Object> getSendList(Map<String, Object> paramMap) {
		return (List<Object>)getSqlSession().selectList("com.saltlux.aice_fe.pc.campaginsendList.dao.CampaginSendListDao.getSendList", paramMap);
	}
	
	//삭제구문
	public int infoDeleteManagement(CampaginSendListInfoVo sendListInfoVo) {
		return (int)getSqlSession().delete("com.saltlux.aice_fe.pc.campaginsendList.dao.CampaginSendListDao.infoDeleteManagement", sendListInfoVo);
		
	}
	
	//전체 삭제 구문
	public int sendListDelete(CampaginSendInfoVo sendInfoVo) {
		return (int)getSqlSession().delete("com.saltlux.aice_fe.pc.campaginsendList.dao.CampaginSendListDao.sendListDelete", sendInfoVo);
		
	}
	
	public int customerCnt(CampaginSendListInfoVo2 sendInfoVo) {
		return (int)getSqlSession().selectOne("com.saltlux.aice_fe.pc.campaginsendList.dao.CampaginSendListDao.customerCnt", sendInfoVo);
	}
	
	//연락처 등록 구문
	public int saveInfoUpdateManagement(CampaginSendListInfoVo2 sendListInfoVo2) {
		return (int)getSqlSession().insert("com.saltlux.aice_fe.pc.campaginsendList.dao.CampaginSendListDao.saveInfoUpdateManagement", sendListInfoVo2);
	}
	
	//고객리스트 가져오기
	public List<Object> getCustomerList(Map<String, Object> paramMap) {
		return (List<Object>)getSqlSession().selectList("com.saltlux.aice_fe.pc.campaginsendList.dao.CampaginSendListDao.getCustomerList", paramMap);
	}
}
