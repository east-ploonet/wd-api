package com.saltlux.aice_fe.pc.sms.service.Impl;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saltlux.aice_fe._baseline.baseService.FileService;
import com.saltlux.aice_fe._baseline.baseService.impl.BaseServiceImpl;
import com.saltlux.aice_fe._baseline.baseVo.DataMap;
import com.saltlux.aice_fe._baseline.baseVo.FileVo;
import com.saltlux.aice_fe._baseline.commons.Common;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.sms.dao.SmsDao;
import com.saltlux.aice_fe.pc.sms.service.SmsService;
import com.saltlux.aice_fe.pc.sms.vo.SendConsigneeVo;
import com.saltlux.aice_fe.pc.sms.vo.SendFileVo;
import com.saltlux.aice_fe.pc.sms.vo.SmsTranVo;

import ch.qos.logback.core.recovery.ResilientSyslogOutputStream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SmsServiceImpl extends BaseServiceImpl implements SmsService {
	
	@Autowired
	private SmsDao smsDao;
	
	@Autowired
	private FileService fileService;

	@Override
	public Map<String,Object> getInfo(int gubun) {
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		Map<String, Object> results = new HashMap<>();		
		//전체리스트 불러오기
		if(gubun == 1) {
			List<Object> smsVoList = smsDao.getInfo(loginInfoVo);
			results.put("smsVoResList", smsVoList);
		}
        
		return results;
	}
	
	// 등록
	public void tranRegist(String formJson, MultipartFile[] uploadFiles) throws Exception {
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> paramMap = mapper.readValue(formJson, Map.class);
		
		SmsTranVo smsTranVo = new SmsTranVo();
		//settingTranVo.setFk_company(loginInfoVo.getLoginCompanyPk());
		smsTranVo.setFk_message_temp((Common.parseLong(paramMap.get("fkMessageTemp"         ))));
		smsTranVo.setFk_email_management((Common.parseLong(paramMap.get("fkEmailManagement" ))));
		smsTranVo.setFk_tel_line((Common.parseLong(paramMap.get("fkTelLine" ))));
		smsTranVo.setFk_campaign((Common.parseLong(paramMap.get("fkCampagin" ))));
		smsTranVo.setFk_type((Common.parseLong(paramMap.get("fkType" ))));
		//타이틀 추가
		smsTranVo.setFd_send_file_name((Common.NVL(paramMap.get("fdSendFileName"      ), "")) );
		smsTranVo.setFd_send_file_path((Common.NVL(paramMap.get("fdSendFilePath"      ), "")) );
		smsTranVo.setFd_message_flag((Common.NVL(paramMap.get("fdMessageFlag"         ), "")) );
		smsTranVo.setFd_message_content((Common.NVL(paramMap.get("fdMessageContent"   ), "")) );						
		smsTranVo.setFk_writer(loginInfoVo.getLoginCompanyStaffPk());	
	
		    
		Map<String, Object> paramMapInsert = new HashMap();
		
		paramMapInsert.put("smsTranVo", smsTranVo);
		
		try {
			// tbl_send_message (insert)
			int result = smsDao.tranRegist(paramMapInsert);
			
			try {
				
			// tbl_send_consigneee
			SendConsigneeVo sendConsigneeVo = new SendConsigneeVo();
			ObjectMapper sendMapper = new ObjectMapper();
			Map<String, String> sendParamMap = sendMapper.readValue(formJson, Map.class);
			sendConsigneeVo.setFd_send_data((Common.NVL(sendParamMap.get("fdSendData"), "")));
			sendConsigneeVo.setFk_writer(loginInfoVo.getLoginCompanyStaffPk());
			
			Map<String, Object> ConsigneeInsert = new HashMap();
			ConsigneeInsert.put("sendConsigneeVo", sendConsigneeVo);
			
			int sendResult =smsDao.tranConsigneeRegist(ConsigneeInsert);
			
			// tbl_send_file (insert)
			try {
				
				SendFileVo sendTranFileVo = new SendFileVo(); 
				if( uploadFiles != null && uploadFiles.length > 0 ) {

					List<FileVo> fileVoList = fileService.uploadFileToVoList( uploadFiles, "TRAN" );
					//
					if( fileVoList != null && fileVoList.size() > 0) {

						for(int i = 0 ; i < fileVoList.size() ; i ++) {
							sendTranFileVo.setPk_send_file((long) paramMapInsert.get("pk_send_file"));
							sendTranFileVo.setFk_send_message((long) paramMapInsert.get("fk_send_file"));
							sendTranFileVo.setFk_company(loginInfoVo.getLoginCompanyPk());
							sendTranFileVo.setFd_send_file_name( fileVoList.get(i).getFd_file_name() );
							sendTranFileVo.setFd_send_file_path( fileVoList.get(i).getFd_file_name() );
							sendTranFileVo.setFk_modifier(loginInfoVo.getLoginCompanyStaffPk());	
							smsDao.tranFileRegist(sendTranFileVo);
						}
						
					}
				}
				

			} catch (Exception ex) {
				System.out.println("ex:" + ex);
				log.error("********** paramMap : {}", paramMap.toString());
				throwException.statusCode(500);
			}
			
			if ( sendResult <= 0 ) {
				throwException.statusCode(500);
			}
			
			
			}catch (Exception ex) {
				System.out.println("ex:" + ex);
				log.error("********** paramMap : {}", paramMap.toString());
				throwException.statusCode(500);
			}
			
			
//			int result = 1;
			if ( result <= 0 ) {
				throwException.statusCode(500);
			}

		} catch (Exception ex) {
			System.out.println("ex:" + ex);
			log.error("********** paramMap : {}", paramMap.toString());
			throwException.statusCode(500);
		}
	}
	
	
}