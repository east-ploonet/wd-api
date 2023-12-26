package com.saltlux.aice_fe.pc.ticket.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.saltlux.aice_fe._baseline.baseController.BaseController;
import com.saltlux.aice_fe._baseline.baseVo.ResponseVo;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.ticket.service.TicketTimeService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("${apiVersionPrefix}/workStage/ticketTime") // end point : localhost:8080/api/v1/issue
public class TicketTimeController extends BaseController {

	@Autowired
	private TicketTimeService ticketTimeService;
	
    //목록
    @RequestMapping(value = {"/info"}, method = {RequestMethod.GET}, produces=PRODUCES_JSON)
    public Object getTickets() throws Exception {

        Map<String, Object> resultMap = ticketTimeService.getTicketTime();
        return new ResponseVo(200, resultMap);
    }
    
    //등록/수정
    @PostMapping("/merge")
	public Object merge(@RequestParam(value="formData" , required=false ) final String formJson)throws Exception {
    	Map<String, Object> reqJsonObj = new HashMap<>();
    	
    	reqJsonObj.put("formData", formJson);
		
    	ticketTimeService.ticketTimeUpdate(formJson);

		return new ResponseVo(200);
	}
    
    //tbl_ai_conf_noti 목록
    @RequestMapping(value = {"/alarmInfo"}, method = {RequestMethod.GET}, produces=PRODUCES_JSON)
    public Object getAlarmInfo(@RequestParam(value="type" , required=false ) final String type) throws Exception {
    	
        Map<String, Object> resultMap = ticketTimeService.getAlarmSet(type);
        
        return new ResponseVo(200, resultMap);
    }
    
  //tbl_ai_conf_noti 등록/수정
    @PostMapping("/alarmMerge")
	public Object alarmMerge(@RequestParam(value="formDataAlarmSet" , required=false ) final String formJson, @RequestParam(value="type" , required=false ) final String type)throws Exception {
    	Map<String, Object> reqJsonObj = new HashMap<>();

    	reqJsonObj.put("formDataAlarmSet", formJson);

    	ticketTimeService.alarmSetUpdate(formJson, type);

		return new ResponseVo(200);
	}
}
