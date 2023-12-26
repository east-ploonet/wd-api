package com.saltlux.aice_fe.pc.ticket.service;

import java.util.Map;

public interface TicketTimeService {

	Map<String, Object> getTicketTime() throws Exception;

	void ticketTimeUpdate(String formJson) throws Exception;
	
	Map<String, Object> getAlarmSet(String type) throws Exception;
	
	void alarmSetUpdate(String formJson, String type) throws Exception;
	
}
