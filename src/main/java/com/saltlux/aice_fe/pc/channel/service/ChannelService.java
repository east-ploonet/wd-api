package com.saltlux.aice_fe.pc.channel.service;

import java.util.Map;

import javax.mail.NoSuchProviderException;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.saltlux.aice_fe._baseline.baseVo.DataMap;

public interface ChannelService {
	
	
	void emailRegist(String formJson) throws Exception;
	
	void emailUpdate(String formJson) throws Exception;
	
	Map<String, Object> getEmailList(Map<String, Object> paramMap) throws Exception;
	
	
	Map<String, Object> getTranList(Map<String, Object> paramMap) throws Exception;
	
	void tranRegist(String formJson, MultipartFile file) throws Exception;
	
	void tranUpdate(String formJson, MultipartFile[] uploadFiles) throws Exception;
	
	void tranDelete(String formJson) throws Exception;
	
	
	Map<String, Object> getSendEmail(Map<String, Object> paramMap) throws Exception;
	
	
	
	
	Resource downloadTemplate(String nameTemplate);
	
	Map<String,Object> getInfo();
	
	Map<String,Object> getModInfo(int pk_tel_line);

	void updateModeInfo(String formJson, MultipartFile[] uploadFiles) throws Exception;
	
	void registModeInfo(String formJson, MultipartFile[] uploadFiles) throws Exception;
	
	void deleteModeInfo(Map<String, Object> paramMap) throws Exception;
	
	Map<String, Object> emailExcelUpload(MultipartFile file) throws Exception;
	
	
}
