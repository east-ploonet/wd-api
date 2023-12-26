package com.saltlux.aice_fe.pc.ticket.dao;

import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.saltlux.aice_fe._baseline.baseVo.CustomSqlSessionDaoSupport;
import com.saltlux.aice_fe._baseline.baseVo.DataMap;
import com.saltlux.aice_fe.pc.ticket.vo.AlarmSetVo;
import com.saltlux.aice_fe.pc.ticket.vo.TicketTimeVo;

@Repository
public class TicketTimeDao extends CustomSqlSessionDaoSupport {

	@Autowired
	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }
	
	public DataMap getInfo(Map<String, Object> paramMap) {
		return getSqlSession().selectOne("com.saltlux.aice_fe.pc.ticket.dao.TicketTimeDao.getTicketTime", paramMap); 
	}
	
	public int ticketTimeUpdate(TicketTimeVo ticketTimeVo) {
		return (int)getSqlSession().update("com.saltlux.aice_fe.pc.ticket.dao.TicketTimeDao.ticketTimeMerge", ticketTimeVo);
	}
	
	public DataMap getAlarmInfo(Map<String, Object> paramMap) {
		return getSqlSession().selectOne("com.saltlux.aice_fe.pc.ticket.dao.TicketTimeDao.getAlarmSet", paramMap); 
	}
	
	public int alarmSetUpdate(AlarmSetVo alarmSetVo) {
		return (int)getSqlSession().update("com.saltlux.aice_fe.pc.ticket.dao.TicketTimeDao.alarmSetMerge", alarmSetVo);
	}
	
}
