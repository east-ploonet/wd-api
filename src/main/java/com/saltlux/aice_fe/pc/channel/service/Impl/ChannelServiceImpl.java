package com.saltlux.aice_fe.pc.channel.service.Impl;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saltlux.aice_fe._baseline.baseService.FileService;
import com.saltlux.aice_fe._baseline.baseService.impl.BaseServiceImpl;
import com.saltlux.aice_fe._baseline.baseVo.DataMap;
import com.saltlux.aice_fe._baseline.baseVo.FileVo;
import com.saltlux.aice_fe._baseline.commons.Common;
import com.saltlux.aice_fe.file.utils.FileUtils;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;

import com.saltlux.aice_fe.pc.issue.util.StorageProperties;
import com.saltlux.aice_fe.pc.channel.dao.ChannelDao;
import com.saltlux.aice_fe.pc.channel.service.ChannelService;
import com.saltlux.aice_fe.pc.channel.vo.ChannelTelFileVo;
import com.saltlux.aice_fe.pc.channel.vo.ChannelTelVo;
import com.saltlux.aice_fe.pc.channel.vo.ChannelEmailVo;
import com.saltlux.aice_fe.pc.channel.vo.ChannelTranFileVo;
import com.saltlux.aice_fe.pc.channel.vo.ChannelTranVo;
import com.sun.mail.imap.IMAPSSLStore;
import com.sun.mail.pop3.POP3SSLStore;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class ChannelServiceImpl extends BaseServiceImpl implements ChannelService {
	
	@Value("${path.file.upload}")
	private String pathFileUpload;

	@Autowired
	private ChannelDao channelDao;

	@Autowired
	private FileService fileService;
	
	
	private final Path rootLocation;
	
	@Autowired
	public ChannelServiceImpl(StorageProperties storageProperties){
		this.rootLocation = Paths.get(storageProperties.getLocation());
        try {
            Files.createDirectories(this.rootLocation);
        } catch (IOException e) {
            log.error("Could not create the directory where the uploaded files will be stored.", e);
        }
	}

	//전체리스트
	@Override
	public Map<String,Object>getInfo(){
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		Map<String, Object> results = new HashMap<>();
				
		//070번호 백엔드 처리
		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
		
		Map<String, Object> apiNum = new HashMap<String, Object>();
		apiNum.put("numName", "070-1234-5678");
		listMap.add(apiNum);
		
		Map<String, Object> apiNum2 = new HashMap<String, Object>();
		apiNum2.put("numName", "070-0000-5678");
		listMap.add(apiNum2);
	
//		results.put("loginInfoVo", loginInfoVo);
		results.put("apinum", listMap);
	
		return results;
	}
	
	//리스트 하나 선택시
	@Override
	public Map<String,Object>getModInfo(int pk_tel_line){
		Map<String, Object> results = new HashMap<>();
		List<Object> channelModVoList = channelDao.getModInfo(pk_tel_line);
		results.put("channelModVoList", channelModVoList);
		return results;
	}
	
	//업데이트 구문
	@Override
	public void updateModeInfo(String formJson, MultipartFile[] uploadFiles) throws Exception{
		ChannelTelVo channelVo = new ChannelTelVo();
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		ObjectMapper mapper = new ObjectMapper();
		ChannelTelFileVo channelFileVo = new ChannelTelFileVo();
		
		Map<String, String> paramMap = mapper.readValue(formJson, Map.class);
		
		channelVo.setFk_company(loginInfoVo.getLoginCompanyPk());
		channelVo.setFd_company_name( Common.NVL(paramMap.get("fd_company_name"), ""	)  );
		channelVo.setFd_channel_name( Common.NVL(paramMap.get("fd_channel_name"), ""	)  );
		channelVo.setFd_nationwide( Common.NVL(paramMap.get("fd_nationwide"), ""	)  );
		channelVo.setPk_tel_line(Common.parseInt(paramMap.get("pk_tel_line"	)) );
		int result = channelDao.updateModeInfo(channelVo);
		try {
			int resultFile = 0;
			if( uploadFiles != null && uploadFiles.length > 0 ) {

				List<FileVo> fileVoList = fileService.uploadFileToVoList( uploadFiles, "Channel_List" );
				System.out.println("fileVoList"+fileVoList);
				if( fileVoList != null && fileVoList.size() > 0 && fileVoList.get(0) != null ) {

					//channelFilevo.setFk_company(Common.parseInt(paramMap.get("fk_company")) );
					channelFileVo.setFk_company(loginInfoVo.getLoginCompanyPk());
					//System.out.println(loginInfoVo.getLoginCompanyPk());
					channelFileVo.setFd_file_name(fileVoList.get(0).getFd_file_name());
					channelFileVo.setFd_file_path( fileVoList.get(0).getFd_file_path());
					//channelFilevo.setFd_file_size( fileVoList.get(0).getFd_file_size());
					resultFile = channelDao.updateModeInfoFile(channelFileVo);
				}
			}

			//
			if ( result <= 0 ) {
				throwException.statusCode(500);
			}

		} catch (Exception ex) {
			log.error("********** paramMap : {}", paramMap.toString());
			throwException.statusCode(500);
		}
		
		
	}
	
	
	//저장구문
	@Override
//	public void registModeInfo(Map<String, Object> paramMap) throws Exception{
	public void registModeInfo(String formJson, MultipartFile[] uploadFiles) throws Exception{
		//기본정보 저장 
		
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		ObjectMapper mapper = new ObjectMapper();
		
		Map<String, String> paramMap = mapper.readValue(formJson, Map.class);
		
		ChannelTelVo channelVo = new ChannelTelVo();
		ChannelTelFileVo channelFileVo = new ChannelTelFileVo();
		
		channelVo.setFk_company(loginInfoVo.getLoginCompanyPk());
		channelVo.setFd_phone( Common.NVL(paramMap.get("fd_phone"), ""	)  );
		channelVo.setFd_company_name( Common.NVL(paramMap.get("fd_company_name"), ""	)  );
		channelVo.setFd_channel_name( Common.NVL(paramMap.get("fd_channel_name"), ""	)  );
		channelVo.setFd_nationwide( Common.NVL(paramMap.get("fd_nationwide"), ""	)  );
				
		int result = channelDao.registModeInfo(channelVo);
		try {
			int resultFile = 0;
			if( uploadFiles != null && uploadFiles.length > 0 ) {

				List<FileVo> fileVoList = fileService.uploadFileToVoList( uploadFiles, "Channel_List" );
				System.out.println("fileVoList"+fileVoList);
				if( fileVoList != null && fileVoList.size() > 0 && fileVoList.get(0) != null ) {
					
					
					channelFileVo.setFk_company(loginInfoVo.getLoginCompanyPk());
					channelFileVo.setFd_file_name(fileVoList.get(0).getFd_file_name());
					channelFileVo.setFd_file_path( fileVoList.get(0).getFd_file_path());
					//channelFilevo.setFd_file_size( fileVoList.get(0).getFd_file_size());
					resultFile = channelDao.registModeInfoFile(channelFileVo);
				}

			}

			
			//
			if ( result <= 0 ) {
				throwException.statusCode(500);
			}

		} catch (Exception ex) {
			log.error("********** paramMap : {}", paramMap.toString());
			throwException.statusCode(500);
		}
		
	}
	
	//삭제구문
	@Override
	public void deleteModeInfo(Map<String, Object> paramMap) throws Exception{
		ChannelTelVo channelVo = new ChannelTelVo();
		channelVo.setPk_tel_line(Common.parseInt(paramMap.get("pk_tel_line"	)) );
		try {

			int result = channelDao.deleteModeInfo(channelVo);
			//
			if ( result <= 0 ) {
				throwException.statusCode(500);
			}

		} catch (Exception ex) {
			log.error("********** paramMap : {}", paramMap.toString());
			throwException.statusCode(500);
		}
	}
	
	//파일 기본 양식 다운로드 구문
    @Override
    public Resource downloadTemplate(String nameTemplate) {
        try {
            Path file = rootLocation.resolve(nameTemplate).normalize();
            Resource resource = new UrlResource(file.toUri());
            System.out.println("resource"+resource);
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read file ");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Could not retrieve file ");
        }

    }
    
	public void emailRegist(String formJson) throws Exception {
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		ObjectMapper mapper = new ObjectMapper();
		
		Map<String, String> paramMap = mapper.readValue(formJson, Map.class);
		
		ChannelEmailVo settingEmailVo = new ChannelEmailVo();
		
		String fdpw = Common.getPrivEncrypt(paramMap.get("fdPw"));
		System.out.println("!!!!!!!!!!!!:" + loginInfoVo.getLoginCompanyPk());
		System.out.println("????????????:" + paramMap.get("fdAccountName"));
		settingEmailVo.setFk_company(loginInfoVo.getLoginCompanyPk());
//		settingEmailVo.setFk_company_staff( loginInfoVo.getLoginCompanyStaffPk()); // modifier 및 writer 랑 동일
		settingEmailVo.setFd_host( (Common.NVL(paramMap.get("fdHost"         ), "")) ); 				
		settingEmailVo.setFd_account_name( (Common.NVL(paramMap.get("fdAccountName"         ), "")) );				
		settingEmailVo.setFd_use( (Common.NVL(paramMap.get("fdUse"         ), "")) );				
		settingEmailVo.setFd_usage_status( (Common.NVL(paramMap.get("fdUsageStatus"         ), "")) );			
		settingEmailVo.setFd_mailing_protocol(Integer.parseInt(paramMap.get("fdMailingProtocol"         )));				
		settingEmailVo.setFd_email ( (Common.NVL(paramMap.get("fdEmail"         ), "")) );			
//		settingEmailVo.setFd_pw (Common.NVL(paramMap.get("fdPw"),""));
//		settingEmailVo.setFd_pw (Common.getPrivEncrypt(paramMap.get("fdPw")));
		settingEmailVo.setFd_pw (fdpw);
		
		settingEmailVo.setFd_server ( (Common.NVL(paramMap.get("fdServer"         ), "")) );		
		settingEmailVo.setFd_port ( (Common.NVL(paramMap.get("fdPort"         ), "")) );
		settingEmailVo.setFd_smtp_crtfc ( (Common.NVL(paramMap.get("fdSmtpCrtfc"         ), "")) );
		settingEmailVo.setFd_smtp_tls ( (Common.NVL(paramMap.get("fdSmtpTls"         ), "")) );
		settingEmailVo.setFd_smtp_ssl ( (Common.NVL(paramMap.get("fdSmtpSsl"         ), "")) );
		settingEmailVo.setFd_pop_ssl ( (Common.NVL(paramMap.get("fdImapSsl"         ), "")) );				
		settingEmailVo.setFd_imap_ssl ( (Common.NVL(paramMap.get("fdPopSsl"         ), "")) );				
				
		settingEmailVo.setFk_writer(loginInfoVo.getLoginCompanyStaffPk());	
		    
		try {
			
			int result = channelDao.emailRegist(settingEmailVo);
			
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
	
	
	public void emailUpdate(String formJson) throws Exception {
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		ObjectMapper mapper = new ObjectMapper();
		
		Map<String, String> paramMap = mapper.readValue(formJson, Map.class);
		
		ChannelEmailVo settingEmailVo = new ChannelEmailVo();
		
		String fdpw = Common.getPrivEncrypt(paramMap.get("fdPw"));
		
		
		
		settingEmailVo.setFd_host( (Common.NVL(paramMap.get(""         ), "")) );
		settingEmailVo.setFk_company(loginInfoVo.getLoginCompanyPk());
//		settingEmailVo.setFk_company_staff( loginInfoVo.getLoginCompanyStaffPk()); // modifier 및 writer 랑 동일
		settingEmailVo.setPk_email_management(Integer.parseInt(paramMap.get("pkEmailManagement"         )));
		settingEmailVo.setFd_host( (Common.NVL(paramMap.get("fdHost"         ), "")) ); 				
		settingEmailVo.setFd_account_name( (Common.NVL(paramMap.get("fdAccountName"         ), "")) );				
		settingEmailVo.setFd_use( (Common.NVL(paramMap.get("fdUse"         ), "")) );				
		settingEmailVo.setFd_usage_status( (Common.NVL(paramMap.get("fdUsageStatus"         ), "")) );			
		settingEmailVo.setFd_mailing_protocol(Integer.parseInt(paramMap.get("fdMailingProtocol"         )));				
		settingEmailVo.setFd_email ( (Common.NVL(paramMap.get("fdEmail"         ), "")) );
		settingEmailVo.setFd_pw (fdpw);
		//settingEmailVo.setFd_pw ( (Common.NVL(paramMap.get("fdPw"         ), "")) );			
		settingEmailVo.setFd_server ( (Common.NVL(paramMap.get("fdServer"         ), "")) );		
		settingEmailVo.setFd_port ( (Common.NVL(paramMap.get("fdPort"         ), "")) );
		settingEmailVo.setFd_smtp_crtfc ( (Common.NVL(paramMap.get("fdSmtpCrtfc"         ), "")) );
		settingEmailVo.setFd_smtp_tls ( (Common.NVL(paramMap.get("fdSmtpTls"         ), "")) );
		settingEmailVo.setFd_smtp_ssl ( (Common.NVL(paramMap.get("fdSmtpSsl"         ), "")) );
		settingEmailVo.setFd_pop_ssl ( (Common.NVL(paramMap.get("fdImapSsl"         ), "")) );				
		settingEmailVo.setFd_imap_ssl ( (Common.NVL(paramMap.get("fdPopSsl"         ), "")) );				
				
		settingEmailVo.setFk_modifier(loginInfoVo.getLoginCompanyStaffPk());	
		    
		try {
			
			int result = channelDao.emailUpdate(settingEmailVo);
			
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
	
	
	@Override
	public Map<String, Object> getEmailList(Map<String, Object> paramMap) throws Exception {
		
		int totalCnt = channelDao.emailCnt(paramMap);
		List<Object> listData = channelDao.getEmailList(paramMap);
		Map<String, Object> result          = new HashMap<>();
		result.put("totalCnt", totalCnt);
		result.put("list", listData);
		return result;
	}
	
	
	@Override
	public Map<String, Object> getTranList(Map<String, Object> paramMap) throws Exception {
		
		int totalCnt = channelDao.tranCnt(paramMap);
		List<Object> listData = channelDao.getTranList(paramMap);
		DataMap defaultYn = channelDao.getFdNationwide(paramMap);
		List<Object> listDataTotal = channelDao.getTranListTotal(paramMap);
		
		Map<String, Object> result          = new HashMap<>();
	
		result.put("totalCnt", totalCnt);
		result.put("list", listData);
		result.put("defaultYn", defaultYn);
		result.put("listTotal", listDataTotal);
		return result;
	}
	
	// 발신 리스트 관리 등록
	public void tranRegist(String formJson, MultipartFile file) throws Exception {
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		ObjectMapper mapper = new ObjectMapper();
		
		
		Map<String, String> paramMap = mapper.readValue(formJson, Map.class);
		
		ChannelTranVo settingTranVo = new ChannelTranVo();
//		Long fkCompanyStaff =  (long) Integer.parseInt(paramMap.get("fkCompanyStaff"))
		settingTranVo.setUser_type(loginInfoVo.getLoginUserType());
		settingTranVo.setFd_use((Common.NVL(paramMap.get("fdUse"         ), "")));
		settingTranVo.setFk_company(loginInfoVo.getLoginCompanyPk());
		settingTranVo.setFd_nationwide((Common.NVL(paramMap.get("defaultYn"         ), "")));
		//settingTranVo.setFk_company_staff(fkCompanyStaff); 				
		settingTranVo.setFd_costumer_phone( (Common.NVL(paramMap.get("fdCostumerPhone"         ), "")) ); 				
		settingTranVo.setFd_tarn_num( (Common.NVL(paramMap.get("bizPhoneNum"         ), "")) );				
		settingTranVo.setFd_tarn_name( (Common.NVL(paramMap.get("bizPhoneName"         ), "")) );
		settingTranVo.setFd_veri_code( (Common.NVL(paramMap.get("fdVeriCode"         ), "")) );
		settingTranVo.setFd_veri_YN( (Common.NVL(paramMap.get("fdVeriYN"         ), "")) );
		settingTranVo.setFd_use( (Common.NVL(paramMap.get("enableYn"         ), "")) );
		settingTranVo.setFd_usageStatus((Common.NVL(paramMap.get("fdUsageStatus"         ), "")));
		settingTranVo.setReg_status("INIT");
				
		settingTranVo.setFk_writer(loginInfoVo.getLoginCompanyStaffPk());	
		
		
		//대표번호신청서 등록
		if(file == null) {
			settingTranVo.setDoc_main_status("N");
			settingTranVo.setFile_path_main_num(null);
		}else {
			settingTranVo.setDoc_main_status("Y");
			settingTranVo.setFile_path_main_num(file.getOriginalFilename());
		}
		
		Map<String, Object> paramMapInsert = new HashMap();
		
		paramMapInsert.put("settingTranVo", settingTranVo);
		
		//대표번호 초기화
		if(paramMap.get("defaultYn").equals("Y")) {
			System.out.println("출력값 : "+paramMap.get("defaultYn"));
			channelDao.tranUpdateAll(settingTranVo);
			
		}
		try {
			int result = channelDao.tranRegist(paramMapInsert);
			System.out.println(paramMapInsert.get("pk_company_phone").toString());
			try {
				String path = "sendNumber/" + loginInfoVo.getLoginCompanyPk() + "/require_docs/"+ paramMapInsert.get("pk_company_phone").toString() +"/etc";
                //solutionType/userType/fkCompany/fkCompanyStaffAi/ctgr01/ctgr02/ctgr03/파일명.xlsx
                //예시) B11/B2001/70/224/CTGR1_MANAGE/CTGR2_RECEPTIONIST/CTGR3_DEPART_INFO/장애접수_STEP4_장애현상_유형정보.xlsx
                
                List<FileVo> fileVoList = fileService.uploadFileToVoListS3( file, path );
				

			} catch (Exception ex) {
				System.out.println("e1:" + "파일업로드 오류");
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
	
	//수정구문
	public void tranUpdate(String formJson, MultipartFile[] uploadFiles) throws Exception {
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		ObjectMapper mapper = new ObjectMapper();
		
		Map<String, String> paramMap = mapper.readValue(formJson, Map.class);
		
		ChannelTranVo settingTranVo = new ChannelTranVo();
//		Long fkCompanyStaff =  (long) Integer.parseInt(paramMap.get("fkCompanyStaff"));
		Long pkCompanyPhone =  Common.parseLong(paramMap.get("pkCompanyPhone"));
		settingTranVo.setFk_company(loginInfoVo.getLoginCompanyPk());
		//settingTranVo.setFk_company_staff(fkCompanyStaff); 				
		settingTranVo.setFd_costumer_phone( (Common.NVL(paramMap.get("fdCostumerPhone"         ), "")) );
		settingTranVo.setFd_nationwide((Common.NVL(paramMap.get("defaultYn"         ), "")));
		settingTranVo.setFd_tarn_num( (Common.NVL(paramMap.get("bizPhoneNum"         ), "")) );				
		settingTranVo.setFd_tarn_name( (Common.NVL(paramMap.get("bizPhoneName"         ), "")) );
		settingTranVo.setFd_veri_code( (Common.NVL(paramMap.get("fdVeriCode"         ), "")) );
		settingTranVo.setFd_veri_YN( (Common.NVL(paramMap.get("fdVeriYN"         ), "")) );
		settingTranVo.setFd_usageStatus((Common.NVL(paramMap.get("fdUsagestatus"         ), "")));
		settingTranVo.setFd_use( (Common.NVL(paramMap.get("enableYn"         ), "")) );		
		//settingTranVo.setReg_status((Common.NVL(paramMap.get("regStatus"         ), "")));
		//settingTranVo.setFd_dept(fdDept);			
		settingTranVo.setPk_tran_management(pkCompanyPhone);
		
		//대표번호 초기화
		if(Common.NVL(paramMap.get("defaultYn")).equals("Y")){
			channelDao.tranUpdateAll(settingTranVo);
		}
		
		try {
			int result = channelDao.tranUpdate(settingTranVo);
			
			try {
				
				ChannelTranFileVo settingTranFileVo = new ChannelTranFileVo(); 
				if( uploadFiles != null && uploadFiles.length > 0 ) {

					List<FileVo> fileVoList = fileService.uploadFileToVoList( uploadFiles, "TRAN" );
					
//					System.out.println(pathFileUpload); //  ./log/storage_local/fe
//					String filePath = "D:/Project/workcenter/ploonet_workCenter/ log/storage_local/fe/TRAN/202302/79693b39-39cb-4de9-b86f-70fc1a2333d9.png";
//					
//					File reFile = new File(filePath);
//					reFile.delete();
					
					
					//
					if( fileVoList != null && fileVoList.size() > 0) {
						
						settingTranFileVo.setFk_tran_management(Integer.parseInt(paramMap.get("pkCompanyPhone"         )));
						settingTranFileVo.setFk_company(loginInfoVo.getLoginCompanyPk());
						settingTranFileVo.setFk_modifier(loginInfoVo.getLoginCompanyStaffPk());	
						channelDao.tranFileDeleteFlag(settingTranFileVo); // 지우지 말고 update를 쳐서 flag N > Y 로 바꿈
						for(int i = 0 ; i < fileVoList.size() ; i ++) {
							settingTranFileVo.setFd_file_path( fileVoList.get(i).getFd_file_path() );
							settingTranFileVo.setFd_file_size( fileVoList.get(i).getFd_file_size() );
							settingTranFileVo.setFd_file_name( fileVoList.get(i).getFd_file_name() );
							//파일테이블 insert
							System.out.println("fileVoList"+ fileVoList);
							channelDao.tranFileRegist(settingTranFileVo); // insert 함
						}
						
						// 여기서 삭제
						channelDao.tranFileDelete(settingTranFileVo); // 여기서 pkTranManagement 이면서 flag Y 인 친구들 삭제
					}
				}
				

			} catch (Exception ex) {
				System.out.println("ex:" + ex);
				log.error("********** paramMap : {}", paramMap.toString());
				throwException.statusCode(500);
			}
			
			
			if ( result <= 0 ) {
				throwException.statusCode(500);
			}

		} catch (Exception ex) {
			System.out.println("ex:" + ex);
			log.error("********** paramMap : {}", paramMap.toString());
			throwException.statusCode(500);
		}
	}
	//삭제구문
	@Override
	public void tranDelete(String formJson) throws Exception{
		ChannelTranVo settingTranVo = new ChannelTranVo();
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		
		ObjectMapper mapper = new ObjectMapper();
		
		Map<String, String> paramMap = mapper.readValue(formJson, Map.class);
		
		Long pkCompanyPhone =  Common.parseLong(paramMap.get("pkCompanyPhone"));
		settingTranVo.setPk_tran_management(pkCompanyPhone);
		settingTranVo.setFk_company(loginInfoVo.getLoginCompanyPk());
		try {

			int result = channelDao.tranDelete(settingTranVo);
			//
			if ( result <= 0 ) {
				throwException.statusCode(500);
			}

		} catch (Exception ex) {
			log.error("********** paramMap : {}", paramMap.toString());
			throwException.statusCode(500);
		}
	}
	
	@Override
	public Map<String, Object> getSendEmail(Map<String, Object> paramMap) throws Exception {
		
		List<Object> listData = channelDao.getSendEmail(paramMap);
		Map<String, Object> result          = new HashMap<>();
		result.put("list", listData);
		return result;
	}
	
	public Map<String, Object> emailExcelUpload(MultipartFile file) throws Exception {
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		ObjectMapper mapper = new ObjectMapper();
		
		// 파일을 받아서 처리할 필요가 있음
		
		// 파일 읽기
//		List<ExcelData> dataList = new ArrayList<>();
//
//		String extension = FilenameUtils.getExtension(file.getOriginalFilename()); // 3
//
//		if (!extension.equals("xlsx") && !extension.equals("xls")) {
//			throw new IOException("엑셀파일만 업로드 해주세요.");
//		}
//
//		Workbook workbook = null;
//
//		if (extension.equals("xlsx")) {
//			workbook = new XSSFWorkbook(file.getInputStream());
//		} else if (extension.equals("xls")) {
//			workbook = new HSSFWorkbook(file.getInputStream());
//		}
//
//		Sheet worksheet = workbook.getSheetAt(0);
//
//		for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) { // 4
//
//			Row row = worksheet.getRow(i);
//
//			ExcelData data = new ExcelData();
//
//			data.setNum((int) row.getCell(0).getNumericCellValue());
//			data.setName(row.getCell(1).getStringCellValue());
//			data.setEmail(row.getCell(2).getStringCellValue());
//
//			dataList.add(data);
//		}
		
		Map<String, Object> result          = new HashMap<>();
		result.put("rowNum", "rowNum");
		return result;
	}
}