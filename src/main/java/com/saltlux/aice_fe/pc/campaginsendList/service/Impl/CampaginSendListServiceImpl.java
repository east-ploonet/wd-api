package com.saltlux.aice_fe.pc.campaginsendList.service.Impl;

import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saltlux.aice_fe._baseline.baseService.FileService;
import com.saltlux.aice_fe._baseline.baseService.impl.BaseServiceImpl;
import com.saltlux.aice_fe._baseline.baseVo.DataMap;
import com.saltlux.aice_fe._baseline.baseVo.FileVo;
import com.saltlux.aice_fe._baseline.commons.Common;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.campaginsendList.dao.CampaginSendListDao;
import com.saltlux.aice_fe.pc.campaginsendList.service.CampaginSendListService;
import com.saltlux.aice_fe.pc.campaginsendList.vo.CampaginSendInfoVo;
import com.saltlux.aice_fe.pc.campaginsendList.vo.CampaginSendListInfoVo;
import com.saltlux.aice_fe.pc.campaginsendList.vo.CampaginSendListInfoVo2;
import com.saltlux.aice_fe.pc.issue.dao.CompanyCustomerDao;
import com.saltlux.aice_fe.pc.issue.dto.TicketIssueCustomerDTO;
import com.saltlux.aice_fe.pc.issue.util.StorageProperties;
import com.saltlux.aice_fe.pc.issue.vo.CompanyCustomerVo;
import com.saltlux.aice_fe.pc.send.vo.SendListVo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class CampaginSendListServiceImpl extends BaseServiceImpl implements CampaginSendListService {
	
	private final Path rootLocation;
	
	@Autowired
	private CampaginSendListDao sendListDao;
	
	@Autowired
	private final CompanyCustomerDao companyCustomerDao;
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	public CampaginSendListServiceImpl(StorageProperties storageProperties,CampaginSendListDao sendListDao, FileService fileService, CompanyCustomerDao companyCustomerDao) {
		this.rootLocation = Paths.get(storageProperties.getLocation());
		this.sendListDao = sendListDao;
		this.fileService = fileService;
		this.companyCustomerDao = companyCustomerDao;
	}
	
	//리스트 삭제
	@Override
	public void sendListDelete(Map<String, Object> paramMap) throws Exception {
		
		CampaginSendInfoVo sendInfoVo = new CampaginSendInfoVo();
		String pkSendInfo = Common.NVL(paramMap.get("pkSendInfo"    ),"");
		sendInfoVo.setPk_send_info(pkSendInfo);
		
		try {
			int deleteList = sendListDao.sendListDelete(sendInfoVo);
			if(deleteList <= 0) {
				throwException.statusCode(500);
			}
		}catch (Exception e) {
			log.error("********** paramMap : {}", paramMap.toString());
			throwException.statusCode(500);
		}

	}
	
	
	//전체리스트 불러오기, 선택 항목 불러오기
	@Override
	public Map<String, Object> getSendList(Map<String, Object> paramMap) throws Exception {
				
		int totalCnt = sendListDao.sendListCnt(paramMap);
		List<Object> listData = sendListDao.getSendList(paramMap);
		
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		Map<String, Object> result          = new HashMap<>();
		result.put("totalCnt", totalCnt);
		result.put("list", listData);
		result.put("pkCompany",loginInfoVo.getLoginCompanyPk());
		return result;
	}
	
	@Override
	public Map<String, Object> infoRegist(Map<String, Object> paramMap) throws Exception {
		
		Map<String, Object> returnParam = new HashMap<>();
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		String message = "";
		 
		//발신리스트 명 추가
		String title = Common.NVL(paramMap.get("sendMessageName"    ),"");
		//설명 추가
		String content = Common.NVL(paramMap.get("messageContent"    ),"");
		//공유 (본인,회사,부서)
		String listShare = Common.NVL(paramMap.get("fdListShare"    ),"");
		String fdListShare = "";
		
		//기본은 본인 선택
		if(listShare.equals("0") || listShare.equals("")) {
			//본인
			fdListShare="B2001";
			//fdListShareCheck="N";
		}else if(listShare.equals("1")) {
			//회사
			fdListShare="B2002";
			//fdListShareCheck="Y";
		}else {
			//부서
			fdListShare="B2003";
			//fdListShareCheck="Y";
		}
		
		
		//테이블 totalList 가져오기
		List<Map<String, Object>> sendList = (List<Map<String, Object>>) paramMap.get("send_list");
	
		
		if(sendList !=null) {
			
			try {
				int sendListSize = sendList.size();
				CampaginSendListInfoVo sendListVo = new CampaginSendListInfoVo();
				
				CampaginSendInfoVo sendInfoVo = new CampaginSendInfoVo();
				
				//키값 설정
				String pkSendInfo = UUID.randomUUID().toString();
				
				sendInfoVo.setFk_company(loginInfoVo.getLoginCompanyPk());
				sendInfoVo.setPk_send_info(pkSendInfo);
				sendInfoVo.setFd_send_message_name(title);
				sendInfoVo.setFd_message_content(content);
				sendInfoVo.setFd_list_share(fdListShare);
				sendInfoVo.setFd_customer_count(sendListSize);
				sendInfoVo.setFk_writer(loginInfoVo.getLoginCompanyStaffPk());
				
				try {
					infoRegistList(sendInfoVo);
				}catch (Exception e) {
					
					log.error("********** paramMap : {}",paramMap.toString());
					
					message = "실패 되었습니다.";
					returnParam.put("message", message);
		        	returnParam.put("status", 500);
		        	
		        	return returnParam;
				}
				
				
				for (int i=0; i<sendListSize; i++) {
					
					System.out.println("---------------------------------------------------");
					Map<String, Object> sendListDt = sendList.get(i);
					sendListVo.setFk_company(loginInfoVo.getLoginCompanyPk());
					sendListVo.setFk_send_info(sendInfoVo.getPk_send_info());
					sendListVo.setFk_company_customer(Long.valueOf((String) sendListDt.get("pkCompanyCustomer")));
					sendListVo.setFk_writer(loginInfoVo.getLoginCompanyStaffPk());
					
					try {
						infoRegistManagement(sendListVo);
					}catch (Exception e) {
						log.error("********** paramMap : {}",paramMap.toString());
						
						message = "실패 되었습니다.";
						returnParam.put("message", message);
			        	returnParam.put("status", 500);
			        	
			        	return returnParam;
					}
				}
				message = "정상적으로 등록되었습니다.";
				returnParam.put("message", message);
	        	returnParam.put("status", 200);
	        	
			}catch (Exception e) {
				
				log.error("********** paramMap : {}",paramMap.toString());
				throwException.statusCode(500);
				
	        	
			}
			
		}
		
		return returnParam;
		
	}
	@Override
	public CampaginSendListInfoVo infoRegistManagement(CampaginSendListInfoVo sendListVo){
		try {
			int result = sendListDao.infoRegistManagement(sendListVo);
			//
			if ( result <= 0 ) {
				throwException.statusCode(500);
			}
			
		} catch (Exception ex) {
			log.error("********** paramMap : {}", sendListVo.toString());
			throwException.statusCode(500);
		}
		return sendListVo;
	}
	
	@Override
	public CampaginSendInfoVo infoRegistList(CampaginSendInfoVo sendInfoVo) {
		System.out.println("--------------------------------------------");
		System.out.println("sendInfoVo"+sendInfoVo);
		try {
			int result = sendListDao.infoRegistList(sendInfoVo);
			//
			if ( result <= 0 ) {
				throwException.statusCode(500);
			}
			
		} catch (Exception ex) {
			log.error("********** paramMap : {}", sendInfoVo.toString());
			throwException.statusCode(500);
		}
		return sendInfoVo;
	}
	
	
	/*
	 * 
	 * 수정구문
	 * 
	 * 
	 * */
	@Override
	public Map<String, Object> sendListUpdate(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> returnParam = new HashMap<>();
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
			
		//발신리스트 명 추가
		String title = Common.NVL(paramMap.get("sendMessageName"    ),"");
		//설명 추가
		String content = Common.NVL(paramMap.get("messageContent"    ),"");
		//공유 (본인,회사,부서)
		String listShare = Common.NVL(paramMap.get("fdListShare"    ),"");
		String selectDefault = Common.NVL(paramMap.get("selectDefault"    ),"");
		String fdListShare = "";
		
		//키값 가져오기
		String pkSendInfo = Common.NVL(paramMap.get("pkSendInfo"    ),"");
		String message = "";
		
		if(listShare.equals("0")) {
			//본인
			fdListShare="B2001";
		}else if(listShare.equals("1")) {
			//회사
			fdListShare="B2002";
		}else {
			//부서
			fdListShare="B2003";
		}
		/*--------------------------------------------*/
		if(listShare == "") {
			if(selectDefault.equals("0")) {
				//본인
				fdListShare="B2001";
			}else if(selectDefault.equals("1")) {
				//회사
				fdListShare="B2002";
			}else {
				//부서
				fdListShare="B2003";
			}
		}
		
		
		
		
		//테이블 totalList 가져오기
		List<Map<String, Object>> sendList = (List<Map<String, Object>>) paramMap.get("send_list");
		
		if(sendList !=null) {
			
			try {
				int sendListSize = sendList.size();
				CampaginSendListInfoVo sendListVo = new CampaginSendListInfoVo();
				
				CampaginSendInfoVo sendInfoVo = new CampaginSendInfoVo();
				sendInfoVo.setFk_company(loginInfoVo.getLoginCompanyPk());
				sendInfoVo.setPk_send_info(pkSendInfo);
				sendInfoVo.setFd_send_message_name(title);
				sendInfoVo.setFd_message_content(content);
				sendInfoVo.setFd_list_share(fdListShare);
				sendInfoVo.setFd_customer_count(sendListSize);
				sendInfoVo.setFk_writer(loginInfoVo.getLoginCompanyStaffPk());
				
				try {
					infoUpdateList(sendInfoVo);
				}catch (Exception e) {
					
					log.error("********** paramMap : {}",paramMap.toString());
					
					message = "info update 실패 되었습니다.";
					returnParam.put("message", message);
		        	returnParam.put("status", 500);
		        	
		        	return returnParam;
				}
				
				//리스트조회, 없으면 삽입 있으면 초기화 후 대입.
				Map<String, Object> ChenckParam = new HashMap<>();
				ChenckParam.put("fkSendInfo", pkSendInfo);
				int listCheck = sendListDao.sendListCheck(ChenckParam);

				if(listCheck >0) {
					//리스트 초기화
					try {
						sendListVo.setFk_company(loginInfoVo.getLoginCompanyPk());
						sendListVo.setFk_send_info(pkSendInfo);
						infoDeleteManagement(sendListVo);
					}catch (Exception e) {
						log.error("********** paramMap : {}",paramMap.toString());
						
						message = "message delete 실패 되었습니다.";
						returnParam.put("message", message);
						returnParam.put("status", 500);
						
						return returnParam;
					}			
				}
				System.out.println(sendListSize);
				if(sendListSize > 0) {
					for (int i=0; i<sendListSize; i++) {
						System.out.println(sendList.get(i).get("pkCompanyCustomer"));
						Map<String, Object> sendListDt = sendList.get(i);
						String pkCompanyCustomer = sendListDt.get("pkCompanyCustomer") + "";
						sendListVo.setFk_company(loginInfoVo.getLoginCompanyPk());
						sendListVo.setFk_send_info(sendInfoVo.getPk_send_info());
						sendListVo.setFk_writer(loginInfoVo.getLoginCompanyStaffPk());
						sendListVo.setFk_company_customer(Long.valueOf((String) pkCompanyCustomer ) );
						try {
							infoUpdateManagement(sendListVo);
						}catch (Exception e) {
							log.error("********** paramMap : {}",paramMap.toString());
							
							message = " message insert 실패 했습니다.";
							returnParam.put("message", message);
							returnParam.put("status", 500);
							
							return returnParam;
						}
					}
				}
				message = "정상적으로 수정되었습니다.";
				returnParam.put("message", message);
	        	returnParam.put("status", 200);
	        	
			}catch (Exception e) {
				System.out.println("ex : " + e);
				log.error("********** paramMap : {}",paramMap.toString());
				throwException.statusCode(500);
				
	        	
			}
			
		}
		
		return returnParam;
	}
	//info 업데이트
	@Override
	public CampaginSendListInfoVo infoUpdateManagement(CampaginSendListInfoVo sendListVo){
		try {
			int result = sendListDao.infoUpdateManagement(sendListVo);
			//
			if ( result <= 0 ) {
				throwException.statusCode(500);
			}
			
		} catch (Exception ex) {
			log.error("********** paramMap : {}", sendListVo.toString());
			throwException.statusCode(500);
		}
		return sendListVo;
	}
	
	//리스트 업데이트
	@Override
	public CampaginSendInfoVo infoUpdateList(CampaginSendInfoVo sendInfoVo) {
		try {
			int result = sendListDao.infoUpdateList(sendInfoVo);
			//
			if ( result <= 0 ) {
				throwException.statusCode(500);
			}
			
		} catch (Exception ex) {
			log.error("********** paramMap : {}", sendInfoVo.toString());
			throwException.statusCode(500);
		}
		return sendInfoVo;
	}
	
	//리스트 초기화
	@Override
	public CampaginSendListInfoVo infoDeleteManagement(CampaginSendListInfoVo sendListVo){
		try {
			int result = sendListDao.infoDeleteManagement(sendListVo);
			//
			if ( result <= 0 ) {
				throwException.statusCode(500);
			}
			
		} catch (Exception ex) {
			log.error("********** paramMap : {}", sendListVo.toString());
			throwException.statusCode(500);
		}
		return sendListVo;
	}
	
	//엑셀 폼 다운로드
    @Override
    public Resource downloadTemplate(String nameTemplate) {
        try {
            Path file = rootLocation.resolve(nameTemplate).normalize();
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read file ");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Could not retrieve file ");
        }
    }
    
    //연락처추가 (직접입력)
    @Override
    public Map<String, Object> saveCompanyCustomer(Map<String, Object> paramMap) throws Exception {
    	
    	
    	System.out.println("paramMap" +paramMap);
    	Map<String, Object> returnParam = new HashMap<>();
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
    	List<Map<String, Object>> formDataResult = (List<Map<String, Object>>) paramMap.get("companyCustomer");
    	List<Long> list = new ArrayList<>();
    	
    	System.out.println("formDataResult" +formDataResult);
    	
    	String message = "";
    	
		if(formDataResult !=null) {
			
			try {
				int formDataResultSize = formDataResult.size();
				CampaginSendListInfoVo2 sendListVo2 = new CampaginSendListInfoVo2();
				if(formDataResultSize > 0) {
					for (int i=0; i<formDataResultSize; i++) {
						Map<String, Object> sendListDt = formDataResult.get(i);
						sendListVo2.setFk_company(loginInfoVo.getLoginCompanyPk());
						sendListVo2.setFd_customer_name(Common.NVL(sendListDt.get("fdCustomerName"), ""));
						sendListVo2.setFd_phone(Common.NVL(sendListDt.get("fdCustomerPhone"), ""));
						sendListVo2.setFd_cell_phone(Common.NVL(sendListDt.get("fdCustomerMobile"), ""));
						sendListVo2.setFd_email_address(Common.NVL(sendListDt.get("fdCustomerEmail"), ""));
						sendListVo2.setFd_department(Common.NVL(sendListDt.get("fdCompanyDept"), ""));
						sendListVo2.setFk_writer(loginInfoVo.getLoginCompanyStaffPk());
						
						if(sendListDao.customerCnt(sendListVo2) > 0) {
							
						}
						try {
							//customerCnt
							saveInfoUpdateManagement(sendListVo2);
							list.add(sendListVo2.getPk_company_customer());
						}catch (Exception e) {
							log.error("********** paramMap : {}",paramMap.toString());
							
							message = " message insert 실패 했습니다.";
							returnParam.put("message", message);
							returnParam.put("status", 500);
							
							return returnParam;
						}
					}
				}
				System.out.println(list);
				message = "정상적으로 수정되었습니다.";
				returnParam.put("list", list);
				returnParam.put("message", message);
	        	returnParam.put("status", 200);
			
			}catch (Exception e) {
				// TODO: handle exception
			}
			
				
		}
		return returnParam;	
    }
    
    
    
	//연락처 업데이트
	@Override
	public CampaginSendListInfoVo2 saveInfoUpdateManagement(CampaginSendListInfoVo2 sendListVo2){
		try {
			int result = sendListDao.saveInfoUpdateManagement(sendListVo2);
			//
			if ( result <= 0 ) {
				throwException.statusCode(500);
			}
			
		} catch (Exception ex) {
			log.error("********** paramMap : {}", sendListVo2.toString());
			throwException.statusCode(500);
		}
		return sendListVo2;
	}
	
	//고객리스트 호출(preformData)
	@Override
	public Map<String, Object> getCustomerList() throws Exception {
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("fk_company", loginInfoVo.getLoginCompanyPk());
		List<Object> listData = sendListDao.getCustomerList(paramMap);
		Map<String, Object> result          = new HashMap<>();
		result.put("list", listData);
		return result;
	}

}