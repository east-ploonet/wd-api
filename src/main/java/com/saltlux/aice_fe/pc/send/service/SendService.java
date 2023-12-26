package com.saltlux.aice_fe.pc.send.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.saltlux.aice_fe._baseline.baseVo.DataMap;


public interface SendService {

	DataMap getCustomerInfo(Map<String, Object> paramMap) throws Exception;

	void saveCompanyCustomer(String formJson) throws Exception;

	Map<String, Object> saveNewCompanyCustomer(String mobileList, String addList) throws Exception;
	
	Map<String, Object> saveNewCompanyCustomerList(String[] mobileList, String addList) throws Exception;

	void saveSnsInfo(String[] totalList, String[] numberToList, String sendNumber, String formData, String adTitle, MultipartFile[] uploadFiles) throws Exception;
	
	Map<String, Object> getSendList() throws Exception;
	
	Map<String, Object> getSendHistory(int offset, int pageSize, String startDate, String endDate, String channel, String channelType, String sender) throws Exception;
	
	Map<String, Object> getSendUserHistory(String pkBulkSendPlan, int offset, int pageSize, String search) throws Exception;
	
	Map<String, Object> getSendListCustomer(Map<String, Object> paramMap) throws Exception;

	DataMap getDetailInfo(Map<String, Object> paramMap) throws Exception;
	
	List<Object> defaultYnUse(Map<String, Object> paramMap) throws Exception;
	
	DataMap selectDnis(Map<String, Object> paramMap) throws Exception;
	
	List<Object> getDetailCustomer(Map<String, Object> paramMap) throws Exception;
	
	Map<String, Object> getCompanyCustomer(int page, String search) throws Exception;
}
