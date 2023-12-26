package com.saltlux.aice_fe.pc.aiticket.dao;

import java.util.List;
import java.util.Map;

import com.saltlux.aice_fe.pc.aiticket.vo.AiTicketVo;
import com.saltlux.aice_fe.pc.aiticket.vo.DialogueVo;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.saltlux.aice_fe._baseline.baseVo.CustomSqlSessionDaoSupport;
import com.saltlux.aice_fe._baseline.baseVo.DataMap;

@Repository
public class AiTicketDao extends CustomSqlSessionDaoSupport {

	@Autowired
	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }
	
	public List<Object> getList(Map<String, Object> paramMap) {
		return (List<Object>)getSqlSession().selectList("com.saltlux.aice_fe.pc.aiticket.dao.AiTicketDao.getList", paramMap); 
	}
	
	public List<Object> getView(Map<String, Object> paramMap) {
		return (List<Object>)getSqlSession().selectList("com.saltlux.aice_fe.pc.aiticket.dao.AiTicketDao.getView", paramMap); 
	}
	
	public List<Object> getListDue(Map<String, Object> paramMap) {
		return (List<Object>)getSqlSession().selectList("com.saltlux.aice_fe.pc.aiticket.dao.AiTicketDao.getListDue", paramMap); 
	}
	
	public List<Object> getAiTypeList(Map<String, Object> paramMap) {
		return (List<Object>)getSqlSession().selectList("com.saltlux.aice_fe.pc.aiticket.dao.AiTicketDao.getAiTypeList", paramMap); 
	}
	
	public List<Object> getStaffList(Map<String, Object> paramMap) {
		return (List<Object>)getSqlSession().selectList("com.saltlux.aice_fe.pc.aiticket.dao.AiTicketDao.getStaffList", paramMap); 
	}
	
	public int getListCnt(Map<String, Object> paramMap) {
		return (int)getSqlSession().selectOne("com.saltlux.aice_fe.pc.aiticket.dao.AiTicketDao.getListCnt", paramMap);
	}
	
	public List<Object> getTicketList(Map<String, Object> paramMap) {
		return (List<Object>)getSqlSession().selectList("com.saltlux.aice_fe.pc.aiticket.dao.AiTicketDao.getTicketList", paramMap); 
	}
	
	public List<Object> getNewTicketList(Map<String, Object> paramMap) {
		return (List<Object>)getSqlSession().selectList("com.saltlux.aice_fe.pc.aiticket.dao.AiTicketDao.getNewTicketList", paramMap); 
	}
	
	public List<Object> getTicketListDue(Map<String, Object> paramMap) {
		return (List<Object>)getSqlSession().selectList("com.saltlux.aice_fe.pc.aiticket.dao.AiTicketDao.getTicketListDue", paramMap); 
	}
	
	public int getTicketListCnt(Map<String, Object> paramMap) {
		return (int)getSqlSession().selectOne("com.saltlux.aice_fe.pc.aiticket.dao.AiTicketDao.getTicketListCnt", paramMap);
	}
	
	public DataMap getTicketListCnt2(Map<String, Object> paramMap) {
		return getSqlSession().selectOne("com.saltlux.aice_fe.pc.aiticket.dao.AiTicketDao.getTicketListCnt2", paramMap);
	}
	
	public List<Object> getDialogue(Map<String, Object> paramMap) {
		return (List<Object>)getSqlSession().selectList("com.saltlux.aice_fe.pc.aiticket.dao.AiTicketDao.getDialogue", paramMap);
	}
	
	public DataMap getStaffInfo(Map<String, Object> paramMap) {
		return getSqlSession().selectOne("com.saltlux.aice_fe.pc.aiticket.dao.AiTicketDao.getStaffInfo", paramMap);
	}
	
	public DataMap getCustomerInfo(Map<String, Object> paramMap) {
		return getSqlSession().selectOne("com.saltlux.aice_fe.pc.aiticket.dao.AiTicketDao.getCustomerInfo", paramMap);
	}
	

	public int updateCompanyCustomerInfo(Map<String, Object> paramMap) {
		return (int)getSqlSession().update("com.saltlux.aice_fe.pc.aiticket.dao.AiTicketDao.updateCompanyCustomerInfo", paramMap);
	}
	
	public int updateCompanyStaffInfo(Map<String, Object> paramMap) {
		return (int)getSqlSession().update("com.saltlux.aice_fe.pc.aiticket.dao.AiTicketDao.updateCompanyStaffInfo", paramMap);
	}
	
	public int updateStaffCompanyInfo(Map<String, Object> paramMap) {
		return (int)getSqlSession().update("com.saltlux.aice_fe.pc.aiticket.dao.AiTicketDao.updateStaffCompanyInfo", paramMap);
	}
	
	public int updateTicketInfo(Map<String, Object> paramMap) {
		return (int)getSqlSession().update("com.saltlux.aice_fe.pc.aiticket.dao.AiTicketDao.updateTicketInfo", paramMap);
	}
	
	public int updateTicketFlow(Map<String, Object> paramMap) {
		return (int)getSqlSession().update("com.saltlux.aice_fe.pc.aiticket.dao.AiTicketDao.updateTicketFlow", paramMap);
	}
	
	public int changeTicketStaff(Map<String, Object> paramMap) {
		return (int)getSqlSession().update("com.saltlux.aice_fe.pc.aiticket.dao.AiTicketDao.changeTicketStaff", paramMap);
	}

	public int createTicket(AiTicketVo aiTicketVo) {
		return (int)getSqlSession().insert("com.saltlux.aice_fe.pc.aiticket.dao.AiTicketDao.updateIssueTicket", aiTicketVo);
	}

	public int getDialogue(AiTicketVo aiTicketVo) {
		return (int)getSqlSession().selectOne("com.saltlux.aice_fe.pc.aiticket.dao.AiTicketDao.selectDialogueTicket", aiTicketVo);
	}

	public int getAssignStaff(AiTicketVo aiTicketVo) {
		return (int)getSqlSession().selectOne("com.saltlux.aice_fe.pc.aiticket.dao.AiTicketDao.selectAssignStaff", aiTicketVo);
	}

	public int getPkIssueTicket(AiTicketVo aiTicketVo) {
		return (int)getSqlSession().selectOne("com.saltlux.aice_fe.pc.aiticket.dao.AiTicketDao.selectPkIssueTicket", aiTicketVo);
	}

	public int updatePkIssueTicket(Map<String, Object> paramMap) {
		return (int)getSqlSession().update("com.saltlux.aice_fe.pc.aiticket.dao.AiTicketDao.updateFkIssueTicket", paramMap);
	}

	public int updateFdMessage(Map<String, Object> paramMap) {
		return (int)getSqlSession().update("com.saltlux.aice_fe.pc.aiticket.dao.AiTicketDao.updateFdMessage", paramMap);
	}

	public int addDialogueLog(Map<String, Object> paramMapUpdate) {
		return (int) getSqlSession().insert("com.saltlux.aice_fe.pc.aiticket.dao.AiTicketDao.addDialogueLog", paramMapUpdate);
	}

	public List<Object> getFormInputData(String fkIssutTicket) {
		return getSqlSession().selectList("com.saltlux.aice_fe.pc.aiticket.dao.AiTicketDao.getFormInputData", fkIssutTicket);
	}

	public List<Map<String, Object>> getFormInputData2(String fkIssutTicket) {
		return getSqlSession().selectList("com.saltlux.aice_fe.pc.aiticket.dao.AiTicketDao.getFormInputData", fkIssutTicket);
	}

	public int updateFormData(Map<String, Object> paramMap) {
		return getSqlSession().update("com.saltlux.aice_fe.pc.aiticket.dao.AiTicketDao.updateFormData", paramMap);
	}

	public int updateTicketComment(Map<String, Object> paramMap) {
		return getSqlSession().update("com.saltlux.aice_fe.pc.aiticket.dao.AiTicketDao.updateTicketComment", paramMap);
	}

}
