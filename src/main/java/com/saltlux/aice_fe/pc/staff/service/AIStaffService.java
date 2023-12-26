package com.saltlux.aice_fe.pc.staff.service;

import java.util.Map;


public interface AIStaffService {

    Map<String, Object> selectAIstaffList(Map<String, Object> paramMap) throws Exception;
    Map<String, Object> getAIstaff(Map<String, Object> paramMap) throws Exception;
    
    Map<String, Object> getAIMainStaff(Map<String, Object> paramMap) throws Exception;
    void updateAIStaff(Map<String, Object> paramMap) throws Exception;
    void workCodeUpdate(Map<String, Object> paramMap) throws Exception;
    void updateAIStaffStatus(Map<String, Object> paramMap) throws Exception;

    Map<String, Object> getQuickStart(String uuid) throws Exception;

}
