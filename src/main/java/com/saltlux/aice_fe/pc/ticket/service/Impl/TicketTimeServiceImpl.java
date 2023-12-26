package com.saltlux.aice_fe.pc.ticket.service.Impl;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saltlux.aice_fe._baseline.baseService.impl.BaseServiceImpl;
import com.saltlux.aice_fe._baseline.baseVo.DataMap;
import com.saltlux.aice_fe._baseline.commons.Common;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.ticket.dao.TicketTimeDao;
import com.saltlux.aice_fe.pc.ticket.service.TicketTimeService;
import com.saltlux.aice_fe.pc.ticket.vo.AlarmSetVo;
import com.saltlux.aice_fe.pc.ticket.vo.TicketTimeVo;

import ch.qos.logback.core.recovery.ResilientSyslogOutputStream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TicketTimeServiceImpl extends BaseServiceImpl implements TicketTimeService {

    @Autowired
    private TicketTimeDao ticketTimeDao;


    @Override
    public Map<String, Object> getTicketTime() throws Exception {
    	DataMap resultMap = null;
    	PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
    	Map<String, Object> paramMap = new HashMap<>();
    	paramMap.put("fkCompany", loginInfoVo.getLoginCompanyPk());
    	
    	resultMap = ticketTimeDao.getInfo(paramMap);
    	if ( resultMap == null || resultMap.isEmpty() ) throwException.statusCode(204);
    	
        return resultMap;
    }
    
    @Override
	public void ticketTimeUpdate(
			//Map<String, Object> paramMap,
			String formJson
			) throws Exception {
		
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		
		TicketTimeVo ticketTimeVo = new TicketTimeVo();
		
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> paramMap = mapper.readValue(formJson, Map.class);
		
		try {
			
			Map<String, Object> param = new HashMap<>();
			param.put("fkCompany", loginInfoVo.getLoginCompanyPk());
	    	
			DataMap resultMap = ticketTimeDao.getInfo(param);
			
			if(resultMap != null) ticketTimeVo.setPk_conf_noti_ticket_close(Common.parseInt(resultMap.get("pkConfNotiTicketClose"))); 
			int result = 0;
			ticketTimeVo.setFk_company(loginInfoVo.getLoginCompanyPk());
			
			SimpleDateFormat formatter = new SimpleDateFormat("HHmmss");
			Date notiTime = formatter.parse(paramMap.get("notiTime"));

			ticketTimeVo.setNoti_time(notiTime);
			ticketTimeVo.setClose_day_before(Common.parseInt(paramMap.get("closeDayBefore"))); // 마감 며칠전
			ticketTimeVo.setClose_day_repeat(Common.parseInt(paramMap.get("closeDayRepeat"))); // 마감 미처리 반복
			ticketTimeVo.setUnread_day_after(Common.parseInt(paramMap.get("unreadDayAfter")));
			ticketTimeVo.setUnread_day_repeat(Common.parseInt(paramMap.get("unreadDayRepeat")));
			ticketTimeVo.setMaster_day_after(Common.parseInt(paramMap.get("masterDayAfter")));
			
			ticketTimeVo.setFk_writer(loginInfoVo.getLoginCompanyStaffPk());
			ticketTimeVo.setFk_modifier(loginInfoVo.getLoginCompanyStaffPk());
			
			result = ticketTimeDao.ticketTimeUpdate(ticketTimeVo);
			
			if ( result <= 0 ) {
				throwException.statusCode(500);
			}

		} catch (Exception ex) {
			log.error("********** paramMap : {}", paramMap.toString());
			throwException.statusCode(500);
		}
	}
    
    @Override
    public Map<String, Object> getAlarmSet(String type) throws Exception {
    	DataMap resultMap = null;
    	PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
    	
    	Map<String, Object> paramMap = new HashMap<>();
    	paramMap.put("fkCompany", loginInfoVo.getLoginCompanyPk());
    	paramMap.put("type", type);

    	resultMap = ticketTimeDao.getAlarmInfo(paramMap);

    	if ( resultMap == null || resultMap.isEmpty() ) throwException.statusCode(204);
    	
        return resultMap;
    }
    
    @Override
	public void alarmSetUpdate(
			String formJson, String type
			) throws Exception {
		
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		
		AlarmSetVo alarmSetVo = new AlarmSetVo();
		
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> paramMap = mapper.readValue(formJson, Map.class);
		
		try {
			
			Map<String, Object> param = new HashMap<>();
			param.put("fkCompany", loginInfoVo.getLoginCompanyPk());
	    	param.put("type", type);
			
			DataMap resultMap = ticketTimeDao.getAlarmInfo(param);
			System.out.println("resultMap:" + resultMap);
			if(resultMap != null) alarmSetVo.setPk_ai_conf_noti(Common.parseInt(resultMap.get("pkAiConfNoti"))); 
			int result = 0;

			alarmSetVo.setFk_company(loginInfoVo.getLoginCompanyPk());
			alarmSetVo.setCheck_type((paramMap.get("checkType")));
			alarmSetVo.setSms_yn((paramMap.get("smsYn")));
			alarmSetVo.setKakao_yn((paramMap.get("kakaoYn")));
			alarmSetVo.setPush_yn((paramMap.get("pushYn")));
			alarmSetVo.setMsg_body((paramMap.get("msgBody")));
			alarmSetVo.setUse_yn((paramMap.get("useYn")));

			alarmSetVo.setFk_writer(loginInfoVo.getLoginCompanyStaffPk());
			alarmSetVo.setFk_modifier(loginInfoVo.getLoginCompanyStaffPk());

			result = ticketTimeDao.alarmSetUpdate(alarmSetVo);

			if ( result <= 0 ) {
				throwException.statusCode(500);
			}

		} catch (Exception ex) {
			log.error("********** paramMap : {}", paramMap.toString());
			throwException.statusCode(500);
		}
	}

    
}
