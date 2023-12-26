package com.saltlux.aice_fe.pc.quick.controller;

import static org.springframework.http.MediaType.parseMediaType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saltlux.aice_fe._baseline.baseController.BaseController;
import com.saltlux.aice_fe._baseline.baseVo.DataMap;
import com.saltlux.aice_fe._baseline.baseVo.ResponseVo;
import com.saltlux.aice_fe._baseline.commons.Common;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.issue.service.ExcelService;
import com.saltlux.aice_fe.pc.issue.vo.CompanyCustomerVo;
import com.saltlux.aice_fe.pc.quick.service.QuickService;
import com.saltlux.aice_fe.pc.send.service.SendService;
import com.saltlux.aice_fe.pc.send.vo.ExcelListVo;
import com.saltlux.aice_fe._baseline.commons.Common;
import com.saltlux.aice_fe.pc.issue.service.ExcelService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("${apiVersionPrefix}/workStage/quick") // end point : localhost:8080/api/v1/workStage/send
public class QuickController extends BaseController {
	
	@Value("${ploonet.api.quick.audio.api.url}")
	private String audioApiUrl;
	
	@Autowired
	private QuickService quickService;
	
	@RequestMapping("/getQuickStartUserInfo")
	public Object customerInfo(
			@RequestParam(value="quickStaffPk"     , required=true    ) final String quickStaffPk,
			@RequestParam(value="quickStartType"     , required=true    ) final String quickStartType
	) throws Exception {
		
//		System.out.println("quickStartType " + quickStartType); // 넘어옴
		
		Map<String, Object> reqJsonObj = new HashMap<>();
		reqJsonObj.put("quickStaffPk", quickStaffPk);
		
		DataMap resultMap = quickService.getQuickStartUserInfo(reqJsonObj, quickStartType);
		
		return new ResponseVo(200, resultMap);
	}
	
	@RequestMapping("/updateUserInfo")
	public Object updateUserInfo(
			@RequestParam(value="userInfo"     , required=true    ) final String userInfo,
			@RequestParam(value="loginUserType"     , required=true    ) final String loginUserType
			) throws Exception {
		
		Map<String, Object> reqJsonObj = new HashMap<>();

		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> userInfos = mapper.readValue(userInfo, Map.class);
		
		quickService.updateUserInfo(userInfos, loginUserType);
		
		return new ResponseVo(200);
	}
	
	
	@GetMapping("/personaAudio")
    public void personaAudio(HttpServletRequest request, HttpServletResponse response) {
        
		String readText = request.getParameter("readText");
		if(Common.isBlank(readText)) readText = "안녕하세요. 에이아이직원 앨리스입니다. 오늘 어떤 일로 전화주셨을까요?";
		//String readText = "안녕하세요.";
        String apiAddr = audioApiUrl;
        
        try {
        	Map<String, Object> reqJsonObj = new HashMap<>();
        	System.out.println("hello please : " + apiAddr + readText);
        	
//            Map<String, Object> aivoice = userService.getAivoice(reqJsonObj);
//            System.out.println("hello aivoice : " + aivoice);
//            String aivoiceStr =  aivoice.get("fdStaffPersona").toString();
        	readText = URLEncoder.encode(readText, "UTF-8");
        	
        	
        	apiAddr += readText;
        	URL url = new URL(apiAddr + "&sid=33");
        	
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            
                        
            //POST 요청을 위해 기본값이 false인 setDoOutput을 true로
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            
            int responseCode = conn.getResponseCode();
            final InputStream inputStream = conn.getInputStream();
            
            byte[] bytes = new byte[1024];
            int length;
            OutputStream outputStream = response.getOutputStream();
            
            while ((length = inputStream.read(bytes)) >= 0) {
            	outputStream.write(bytes, 0, length);
            }
            
            //파일유형설정
	        response.setContentType(conn.getContentType()); 
	        //파일길이설정
	        response.setContentLength(conn.getContentLength());
	        //데이터형식/성향설정 (attachment: 첨부파일)
	        response.setHeader("Content-Disposition", "attachment; fileName=\"" + URLEncoder.encode("personaAudio","UTF-8")+"\";");
	        //내용물 인코딩방식설정
	        response.setHeader("Content-Transfer-Encoding", "binary");
	        //버퍼의 출력스트림을 출력
	        
	        inputStream.close();
	        outputStream.flush();
            
            
		} catch (Exception e) {
			System.out.println("ex:" + e);
			// TODO: handle exception
		}finally {
			
		}
    }
}