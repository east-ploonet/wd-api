package com.saltlux.aice_fe.pc.channel.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.saltlux.aice_fe._baseline.baseVo.CustomSqlSessionDaoSupport;
import com.saltlux.aice_fe._baseline.baseVo.DataMap;
import com.saltlux.aice_fe._baseline.baseVo.FileVo;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.issue.vo.IssueTicketVo;
import com.saltlux.aice_fe.pc.channel.vo.ChannelTelFileVo;
import com.saltlux.aice_fe.pc.channel.vo.ChannelTelVo;
import com.saltlux.aice_fe.pc.channel.vo.ChannelEmailVo;
import com.saltlux.aice_fe.pc.channel.vo.ChannelTranFileVo;
import com.saltlux.aice_fe.pc.channel.vo.ChannelTranVo;

@Repository
public class ChannelDao extends CustomSqlSessionDaoSupport {
		
	@Autowired
	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }
	
	public int emailRegist(ChannelEmailVo settingEmailVo) {
		return (int)getSqlSession().insert("com.saltlux.aice_fe.pc.channel.dao.ChannelDao.emailRegist", settingEmailVo);
	}
	
	public int emailUpdate(ChannelEmailVo settingEmailVo) {
		return (int)getSqlSession().update("com.saltlux.aice_fe.pc.channel.dao.ChannelDao.emailUpdate", settingEmailVo);
	}
	
	public List<Object> getEmailList(Map<String, Object> paramMap) {
		return (List<Object>)getSqlSession().selectList("com.saltlux.aice_fe.pc.channel.dao.ChannelDao.getEmailList", paramMap);
	}
	
	public int emailCnt(Map<String, Object> paramMap) {
		return (int)getSqlSession().selectOne("com.saltlux.aice_fe.pc.channel.dao.ChannelDao.emailCnt", paramMap);
	}
	
	public int tranCnt(Map<String, Object> paramMap) {
		return (int)getSqlSession().selectOne("com.saltlux.aice_fe.pc.channel.dao.ChannelDao.tranCnt", paramMap);
	}
	
	public List<Object> getDeptList(Map<String, Object> paramMap) {
		return (List<Object>)getSqlSession().selectList("com.saltlux.aice_fe.pc.channel.dao.ChannelDao.getDeptList", paramMap);
	}
	
	public List<Object> getStaffList(Map<String, Object> paramMap) {
		return (List<Object>)getSqlSession().selectList("com.saltlux.aice_fe.pc.channel.dao.ChannelDao.getStaffList", paramMap);
	}
	
	public int tranRegist(Map<String, Object> paramMapInsert) {
		return (int)getSqlSession().insert("com.saltlux.aice_fe.pc.channel.dao.ChannelDao.tranRegist", paramMapInsert);
	}
	
	public int tranUpdate(ChannelTranVo settingTranVo) {
		return (int)getSqlSession().update("com.saltlux.aice_fe.pc.channel.dao.ChannelDao.tranUpdate", settingTranVo);
	}
	
	public int tranUpdateAll(ChannelTranVo settingTranVo) {
		return (int)getSqlSession().update("com.saltlux.aice_fe.pc.channel.dao.ChannelDao.tranUpdateAll", settingTranVo);
	}
	
	public int tranDelete(ChannelTranVo vo) {
		return (int)getSqlSession().delete("com.saltlux.aice_fe.pc.channel.dao.ChannelDao.tranDelete", vo);
	}
	
	public int tranFileRegist(ChannelTranFileVo settingTranFileVo) {
		return (int)getSqlSession().insert("com.saltlux.aice_fe.pc.channel.dao.ChannelDao.tranFileRegist", settingTranFileVo);
	}
	
	public int tranFileDelete(ChannelTranFileVo settingTranFileVo) {
		return (int)getSqlSession().delete("com.saltlux.aice_fe.pc.channel.dao.ChannelDao.tranFileDelete", settingTranFileVo);
	}
	
	public int tranFileDeleteFlag(ChannelTranFileVo settingTranFileVo) {
		return (int)getSqlSession().update("com.saltlux.aice_fe.pc.channel.dao.ChannelDao.tranFileDeleteFlag", settingTranFileVo);
	}
	
	public List<Object> getTranList(Map<String, Object> paramMap) {
		return (List<Object>)getSqlSession().selectList("com.saltlux.aice_fe.pc.channel.dao.ChannelDao.getTranList", paramMap);
	}
	
	public DataMap getFdNationwide(Map<String, Object> paramMap) {
		return getSqlSession().selectOne("com.saltlux.aice_fe.pc.channel.dao.ChannelDao.getFdNationwide", paramMap);
	}
	
	public List<Object> getTranListTotal(Map<String, Object> paramMap) {
		return (List<Object>)getSqlSession().selectList("com.saltlux.aice_fe.pc.channel.dao.ChannelDao.getTranListTotal", paramMap);
	}
	
	public List<Object> getSendEmail(Map<String, Object> paramMap) {
		return (List<Object>)getSqlSession().selectList("com.saltlux.aice_fe.pc.channel.dao.ChannelDao.getSendEmail", paramMap);
	}
	
	
	
	public List<Object> getInfo(PcLoginInfoVo pcLoginInfoVo){
		return (List<Object>)getSqlSession().selectList("com.saltlux.aice_fe.pc.channel.dao.ChannelDao.getInfo", pcLoginInfoVo);
	}
	
	public List<Object> getInfoSelectOne(PcLoginInfoVo pcLoginInfoVo){
		return (List<Object>)getSqlSession().selectList("com.saltlux.aice_fe.pc.channel.dao.ChannelDao.getInfoSelectOne", pcLoginInfoVo);
	}
	
	public List<Object> getInfoSelectTwo(PcLoginInfoVo pcLoginInfoVo){
		return (List<Object>)getSqlSession().selectList("com.saltlux.aice_fe.pc.channel.dao.ChannelDao.getInfoSelectTwo", pcLoginInfoVo);
	}
	
	public List<Object> getModInfo(int pk_tel_line){
		return (List<Object>)getSqlSession().selectList("com.saltlux.aice_fe.pc.channel.dao.ChannelDao.getModInfo", pk_tel_line);
	}
	
	public int updateModeInfo(ChannelTelVo vo) {
		return (int)getSqlSession().update("com.saltlux.aice_fe.pc.channel.dao.ChannelDao.updateModeInfo", vo);
	}
	
	public int registModeInfo(ChannelTelVo vo) {
		return (int)getSqlSession().insert("com.saltlux.aice_fe.pc.channel.dao.ChannelDao.registModeInfo", vo);
	}
	
	public int registModeInfoFile(ChannelTelFileVo vo) {
		return (int)getSqlSession().insert("com.saltlux.aice_fe.pc.channel.dao.ChannelDao.registModeInfoFile", vo);
	}
	
	public int updateModeInfoFile(ChannelTelFileVo vo) {
		return (int)getSqlSession().update("com.saltlux.aice_fe.pc.channel.dao.ChannelDao.updateModeInfoFile", vo);
	}
	
	public int deleteModeInfo(ChannelTelVo vo) {
		return (int)getSqlSession().delete("com.saltlux.aice_fe.pc.channel.dao.ChannelDao.deleteModeInfo", vo);
	}
	
	
}
