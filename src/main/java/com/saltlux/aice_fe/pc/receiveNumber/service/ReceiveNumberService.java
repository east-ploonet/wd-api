package com.saltlux.aice_fe.pc.receiveNumber.service;

import java.util.Map;


public interface ReceiveNumberService {

    Map<String, Object> getNumberStaffList(Map<String, Object> paramMap) throws Exception;
    
    Map<String, Object> getNumberStaffListComplete(Map<String, Object> paramMap) throws Exception;

}
