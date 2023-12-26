package com.saltlux.aice_fe.pc.channel.controller;

import static org.springframework.http.MediaType.parseMediaType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.saltlux.aice_fe._baseline.baseController.BaseController;
import com.saltlux.aice_fe._baseline.baseVo.ResponseVo;
import com.saltlux.aice_fe._baseline.commons.Common;
import com.saltlux.aice_fe.ecApi.service.PloonetApiService;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.channel.service.ChannelService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("${apiVersionPrefix}/workStage/channel") // end point : localhost:8080/api/v1/workStage/ojt
public class ChannelController extends BaseController {
	
	@Autowired
	private ChannelService channelService;
	
	@Autowired
	private PloonetApiService ploonetApiService;
	
	Map<String, Object> smsAuthNumMap = new HashMap<String, Object>();
	
	
	//고객 전화회선 관리
	@GetMapping("/info")
	public Object info() throws Exception {
		Map<String,Object> resultMap = channelService.getInfo();
		return new ResponseVo(200, resultMap);
	}
	
	//070번호 클릭시 수정
	@GetMapping("/mod/info")
	public Object modInfo(@RequestParam(value="pk_tel_line") final int pk_tel_line) throws Exception {
		Map<String,Object> resultMap = channelService.getModInfo(pk_tel_line);
		return new ResponseVo(200, resultMap);
	}
	
	//고객 전화회선 수정
	@PutMapping("/update")
	public Object updateModeInfo(
			@RequestParam(value="formData"   , required=false) final String formJson,
    		@RequestParam(value="uploadFile"   , required=false) final MultipartFile[] uploadFiles
			) 
			
			throws Exception {
		channelService.updateModeInfo(formJson,uploadFiles);
		return new ResponseVo(200);
	}
		
	//등록
	@PostMapping("/regist")
	public Object modeRegist(
	    		@RequestParam(value="formData"   , required=false) final String formJson,
	    		@RequestParam(value="uploadFile"   , required=false) final MultipartFile[] uploadFiles
	    						 ) throws Exception {
			
		channelService.registModeInfo(formJson, uploadFiles);
			return new ResponseVo(200);
		}
	
	//삭제
	@PostMapping("/delete")
    public Object modeDelete(@RequestBody Map<String, Object> bodyMap) throws Exception {
		channelService.deleteModeInfo(bodyMap);
		return new ResponseVo(200);
	}
		
	//파일 양식 다운르도
	@GetMapping("/download")
	public ResponseEntity<?> downloadTemplate(HttpServletRequest request) {
		
		String fileName = "default_form.pdf";
		Resource fileResource = channelService.downloadTemplate(fileName);
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(fileResource.getFile().getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
		return ResponseEntity.ok()
                .contentType(parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileResource.getFilename() + "\"")
                .body(fileResource);
	}
	
	
	// setting email 기본정보 입력
	@PostMapping("/emailRegist")
	public Object emailRegist(
			@RequestParam(value="formData" , required=false ) final String formJson
	) throws Exception {
		//
		channelService.emailRegist(formJson);

		return new ResponseVo(200);
	}
		
	// setting email 기본정보 읽기
	@GetMapping("/emailInfo")
	public Object emailInfo(
			@RequestParam(value = "page", required = false, defaultValue = "1"  ) int page,
			@RequestParam(value="pkEmailManagement") final String pkEmailManagement,
			@RequestParam(value="select") final String select
	) throws Exception {

		Map<String, Object> reqJsonObj = new HashMap<>();
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		reqJsonObj.put("page", page);
		reqJsonObj.put("fkCompany", loginInfoVo.getLoginCompanyPk());
		reqJsonObj.put("select", select);
		
		if(!Common.isBlank(pkEmailManagement)) {
			reqJsonObj.put("pkEmailManagement", pkEmailManagement);
		}
		
		Map<String, Object> resultMap = channelService.getEmailList(reqJsonObj);
		
		return new ResponseVo( 200, resultMap );
	}
		
	// setting email 기본정보 업데이트
	@PostMapping("/emailUpdate")
	public Object emailUpdate(
			@RequestParam(value="formData" , required=false ) final String formJson
	) throws Exception {
		//
		channelService.emailUpdate(formJson);

		return new ResponseVo(200);
	}
	
	
	
	
	
	// setting email 기본정보 업데이트
	@PostMapping("/emailTest")
	public Object emailTest(
//			@RequestParam(value="fromMail" , required=false ) final String fromMail,
//			@RequestParam(value="fromName" , required=false ) final String fromName,
			@RequestParam(value="mailHost" , required=false ) final String mailHost,
			@RequestParam(value="mailPort" , required=false ) final String mailPort,
			@RequestParam(value="mailPassword" , required=false ) final String mailPassword,
			@RequestParam(value="mailUser" , required=false ) final String mailUser,
			@RequestParam(value="startTls" , required=false ) final String startTls,
			@RequestParam(value="ssl" , required=false ) final String ssl,
			@RequestParam(value="toMail" , required=false ) final String toMail,
//			@RequestParam(value="toName" , required=false ) final String toName,
//			@RequestParam(value="title" , required=false ) final String title,
			@RequestParam(value="content" , required=false ) final String content
	) throws Exception {
		
		
		//제목은 여기서 세팅
		String title = "테스트메일";
		String toName = "테스트받는사람";
		String fromMail = mailUser; //"이게뭘까"; // 보내는사람의 이메일주소(smtp 설정한 메일주소)
		String fromName = "이게뭘까"; // 사용자명 의미없음
		
		System.out.println("mailUser"+mailUser);
		System.out.println("mailPassword"+mailPassword);
		
		boolean result = Common.sendMail(fromMail,  fromName,mailHost,  mailPort,  mailPassword,  mailUser,startTls,  ssl,toMail,  toName,  title,  content);
				
		return new ResponseVo(200);
	}
	
	
	// setting tran 기본정보 읽기
	@GetMapping("/tranInfo")
	public Object tranInfo(
			@RequestParam(value = "page", required = false, defaultValue = "1"  ) int page,
			@RequestParam(value="pkTranManagement") final String pkTranManagement
	) throws Exception {

		Map<String, Object> reqJsonObj = new HashMap<>();
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		reqJsonObj.put("page", page);
		reqJsonObj.put("fkCompany", loginInfoVo.getLoginCompanyPk());
		
		if(!Common.isBlank(pkTranManagement)) {
			reqJsonObj.put("pkTranManagement", pkTranManagement);
		}
		
		
		Map<String, Object> resultMap = channelService.getTranList(reqJsonObj);
		
		return new ResponseVo( 200, resultMap );
	}
	
	// setting tran 기본정보 입력
		@PostMapping("/tranRegist")
		public Object tranRegist(
				@RequestParam(value="formData" , required=false ) final String formJson,
				@RequestParam(value="file"   , required=false    ) final MultipartFile uploadFiles
		) throws Exception {
			//
			
			channelService.tranRegist(formJson, uploadFiles);

			return new ResponseVo(200);
		}
		
		//인증번호 요청api 호출
		@PostMapping("/tranNum/getApi")
		public Object tranNumApi(@RequestParam(value="idTo" , required=true ) final String idTo) throws Exception {
			
			HttpServletRequest request  = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
			Random rd = new Random();//랜덤 객체 생성
			int createNum = 0;
			String ranNum = "";	
			String resultNum ="";
			
			for(int i=0; i<6; i++) {
				createNum = rd.nextInt(9);
				ranNum = Integer.toString(createNum);
				resultNum += ranNum;
			}
			System.out.println("======인증번호=====");
			System.out.println(resultNum);
			
	        //long number = Integer.parseInt(resultNum);
			//인증번호 세션 생성
//			if ( null == request.getSession().getAttribute("resultNum") ) {
//				request.getSession().setAttribute("resultNum", resultNum);
//			}
			PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
			//System.out.println("session 인증번호 "+ request.getSession().getAttribute("resultNum"));
			
			smsAuthNumMap.put(loginInfoVo.getLoginCompanyStaffPk() + "_smsAuthNum", resultNum);
			request.getSession().setAttribute("resultNum", resultNum);
			
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("smsAuthNum",resultNum);
			
			String body = "[플루닛] 발신번호 인증번호 " + resultNum + "을 화면에 입력해주세요.";
			ploonetApiService.msgSend(loginInfoVo.getLoginCompanyPk(), "INSTANT", "B1006", "aicesvc003", "15336116", idTo, "발신번호 인증번호 발송", body, "voice");
			
			return new ResponseVo(200,resultMap);
			
		}
		
		//인증번호 체크api 호출
		@PostMapping("/tranNum/getApi/check")
		public Object tranNumApiCheck(
				@RequestParam(value="smsAuthNum"     , required=false    ) final Integer smsAuthNum) throws Exception {
			PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
			//인증번호 호출
			//System.out.println(smsAuthNumMap.get(loginInfoVo.getLoginCompanyStaffPk() + "_smsAuthNum"));
			//message 값 비교
			Map<String, Object> resultMap = new HashMap<String, Object>();
			
			//String smsAuthNumCheck = (String) smsAuthNumMap.get(loginInfoVo.getLoginCompanyStaffPk() + "_smsAuthNum");
			String smsAuthNumCheck = (String) request.getSession().getAttribute("resultNum");
			
			if(smsAuthNumCheck != null){
				int check = Integer.parseInt(smsAuthNumCheck);
				if(smsAuthNum == check) {
					smsAuthNumMap.remove(loginInfoVo.getLoginCompanyStaffPk() + "_smsAuthNum");
					request.getSession().removeAttribute("resultNum");
					resultMap.put("smsAuthNum",true);
				}else {
					resultMap.put("smsAuthNum",false);					
				}
			}else {
				resultMap.put("smsAuthNum",false);
			}
			return new ResponseVo(200,resultMap);
		}
		
		// setting email 기본정보 업데이트
		@PostMapping("/tranUpdate")
		public Object tranUpdate(
				@RequestParam(value="modData" , required=false ) final String formJson,
				@RequestParam(value="uploadFile"   , required=false    ) final MultipartFile[] uploadFiles
		) throws Exception {
			//
			
			channelService.tranUpdate(formJson, uploadFiles);
//			String uploadFilePath = FileUtils.getUploadFilePath(FileUtils.FileUploadType.NOTICE, file.getOriginalFilename());
			return new ResponseVo(200);
		}
		
		//삭제
		@PostMapping("/tranDelete")
	    public Object tranDelete(@RequestParam(value="formData" , required=false ) final String formJson) throws Exception {
			channelService.tranDelete(formJson);
			return new ResponseVo(200);
		}
		
		
		// setting email 기본정보 읽기
		@GetMapping("/getSendEmail")
		public Object getSendEmail() throws Exception {

			Map<String, Object> reqJsonObj = new HashMap<>();
			PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
			reqJsonObj.put("fkCompany", loginInfoVo.getLoginCompanyPk());
			
			Map<String, Object> resultMap = channelService.getSendEmail(reqJsonObj);
			
			return new ResponseVo( 200, resultMap );
		}

		// setting email 기본정보 읽기
		@GetMapping("/sendEmailExcelUpload")
		public Object sendEmailExcelUpload(
				@RequestParam("file") MultipartFile file
				) throws Exception {

			Map<String, Object> reqJsonObj = new HashMap<>();
			PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
			
			
//			reqJsonObj.put("fkCompany", loginInfoVo.getLoginCompanyPk());
			Map<String, Object> resultMap = channelService.emailExcelUpload(file); //db에 list로 읽은 리스트들 넘겨줘서 저장해야됨
			
			return new ResponseVo(200 , resultMap);
			
		}
		
		
}