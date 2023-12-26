package com.saltlux.aice_fe.pc.new_ojt.controller;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saltlux.aice_fe._baseline.baseController.BaseController;
import com.saltlux.aice_fe._baseline.baseVo.DataMap;
import com.saltlux.aice_fe._baseline.baseVo.ResponseVo;
import com.saltlux.aice_fe._baseline.commons.Common;
import com.saltlux.aice_fe.ecApi.service.PloonetApiService;
import com.saltlux.aice_fe.file.service.FileUploadService;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.new_ojt.service.NewOjtService;
import com.saltlux.aice_fe.pc.staff.service.AIStaffService;
import com.saltlux.aice_fe.pc.user.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("${apiVersionPrefix}/workStage/new_ojt") // end point : localhost:8080/api/v1/workStage/ojt
public class NewOjtController extends BaseController {
		
	@Autowired
	private NewOjtService newOjtService;
	
	@Autowired
	private AIStaffService aiStaffService;
	
	@Autowired
    FileUploadService fileService;
	
	@Autowired
	PloonetApiService ploonetApiService;
	 
	@Autowired
	private UserService userService;
	
	@Value("${ploonet.api.audio.api.url}")
	private String audioApiUrl;
	
	@GetMapping("/personaAudio")
    public void personaAudio(HttpServletRequest request, HttpServletResponse response) {
        
		String readText = request.getParameter("readText");
		if(Common.isBlank(readText)) readText = "안녕하세요. 에이아이직원 앨리스입니다. 오늘 어떤 일로 전화주셨을까요?";
        String apiAddr = audioApiUrl;
        
        try {
        	Map<String, Object> reqJsonObj = new HashMap<>();
            
            Map<String, Object> aivoice = userService.getAivoice(reqJsonObj);
			String aivoiceStr =  aivoice.get("fdStaffPersona").toString();
        	readText = URLEncoder.encode(readText, "UTF-8");
        	apiAddr += readText;
        	URL url = new URL(apiAddr + "&sid=" + aivoiceStr);
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

	// OJT1 회사 기본 정보 입력
	// 변경된 OJT 기본정보
	@RequestMapping(value = {"/infoNew"}, method = {RequestMethod.GET})
	public Object infoNew(
			@RequestParam(value="companyStaff") final String pkCompanyStaff
		)throws Exception {
		
		Map<String, Object> resultMap = newOjtService.getInfo(pkCompanyStaff);
		
		return new ResponseVo(200, resultMap);

	}
	
	// 변경된 OJT1 첫인사말 / 긴급안내멘트
	@RequestMapping(value = {"/introNew"}, method = {RequestMethod.GET})
	public Object introNew(
			@RequestParam(value="companyStaff") final String pkCompanyStaff,
			@RequestParam(value="gubun") final String gubun
		) throws Exception {
		
		DataMap resultMap = newOjtService.getIntro(pkCompanyStaff, gubun);
		return new ResponseVo(200, resultMap);

	}
	
	// 변경된 OJT1 업무 시간
	@RequestMapping(value = {"/timeNew"}, method = {RequestMethod.GET})
	public Object timeNew(
			@RequestParam(value="companyStaff") final String pkCompanyStaff	
		) throws Exception {
		
		Map<String, Object> reqJsonObj = new HashMap<>();
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		
		reqJsonObj.put("fkCompany", loginInfoVo.getLoginCompanyPk());
		reqJsonObj.put("pkCompanyStaff", pkCompanyStaff);
		
		Map<String, Object> resultMap = newOjtService.getOfficeTimeList(reqJsonObj);
		
		return new ResponseVo(200, resultMap);
		
	}
	
	// 변경된 OJT3 채용 완료시 ,Orientation
	@RequestMapping(value = {"/timeOrientation"}, method = {RequestMethod.GET})
	public Object timeOrientation(
			@RequestParam(value="companyStaff") final String pkCompanyStaff	
		) throws Exception {
		
		Map<String, Object> reqJsonObj = new HashMap<>();
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		
		reqJsonObj.put("fkCompany", loginInfoVo.getLoginCompanyPk());
		reqJsonObj.put("pkCompanyStaff", pkCompanyStaff);
		
		Map<String, Object> resultMap = newOjtService.getTimeOrientation(reqJsonObj);
		return new ResponseVo(200, resultMap);
		
	}
	

	
	// 변경된 OJT1 회사 업무 시간
	@RequestMapping(value = {"/companyTimeNew"}, method = {RequestMethod.GET})
	public Object companyTimeNew(
			@RequestParam(value="companyStaff") final String pkCompanyStaff	
		) throws Exception {
		
		Map<String, Object> resultMap = newOjtService.getCompanyTimeList(pkCompanyStaff);
		return new ResponseVo(200, resultMap);
		
	}

	// OJT1 회사정보 저장
	@PostMapping("/companyUpdate")
	public Object companyUpdate(@RequestBody Map<String, Object> reqJsonObj) throws Exception {
		
		newOjtService.companyUpdate(reqJsonObj);

		return new ResponseVo(200); 
	}
	
	// OJT1 회사정보 저장 전체
	@PostMapping("/updateCompany")
	public Object updateCompany(
//			@RequestBody Map<String, Object> reqJsonObj
			@RequestParam(value="ojtInfo"     , required=false    ) final String ojtInfoJSON, 
			@RequestParam(value="selectedTime"     , required=false    ) final String selectedTimeJSON, 
			@RequestParam(value="ojtIntro"     , required=false    ) final String ojtIntroJSON,
			@RequestParam(value="msgBodyCheckList"     , required=false    ) final JSONArray msgBodyJsonArr,
			@RequestParam(value="companyStaff"     , required=false    ) final long companyStaff,
			@RequestParam(value="type"     , required=false    ) final String type
			) throws Exception {
		
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> ojtInfo = mapper.readValue(ojtInfoJSON, Map.class);
		Map<String, Object> selectedTime = mapper.readValue(selectedTimeJSON, Map.class);
		Map<String, Object> ojtIntro = mapper.readValue(ojtIntroJSON, Map.class);
		
		newOjtService.companyUpdate(ojtInfo);
		newOjtService.companyTimeUpdate(selectedTime);
		newOjtService.companyIntroUpdate(ojtIntro, msgBodyJsonArr, companyStaff);
		if(type == null) newOjtService.aiStaffStatusUpdate(companyStaff , "A1102"); // 임시저장
		else {
			PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
			ploonetApiService.talkbotBrokerUpdate(loginInfoVo.getLoginCompanyPk(), companyStaff, type);
		}

		return new ResponseVo(200); 
	}
	//
	@PostMapping("/tempDnisDelete")
	public Object tempDnisDelete(@RequestBody Map<String, Object> reqJsonObj) throws Exception {
		
		newOjtService.tempDnisDelete(reqJsonObj);
		
		return new ResponseVo(200); 
	}
	
	// OJT1 회사 업무시간 저장
	@PostMapping("/companyTimeUpdate")
	public Object companyTimeUpdate(@RequestBody Map<String, Object> reqJsonObj) throws Exception {
		
		newOjtService.companyTimeUpdate(reqJsonObj);
		
		return new ResponseVo(200); 
	}
	
	// OJT1 ai 직원 업무시간 저장
	@PostMapping("/aiTimeUpdate")
	public Object aiTimeUpdate(
			@RequestParam(value="data"     , required=false    ) final String dataJSON, 
			@RequestParam(value="companyStaff"     , required=false    ) final long companyStaff,
			@RequestParam(value="statusTemp"     , required=false    ) final boolean statusTemp,
			@RequestParam(value="type"     , required=false    ) final String type
			) throws Exception {
		
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> data = mapper.readValue(dataJSON, Map.class);
		String statusCode = "A1101";
		
		if(!statusTemp || type != null) {// 완료
			statusCode = "A1101";
		}else {// 임시저장
			statusCode = "A1102";
		}
		
		newOjtService.aiTimeUpdate(data, companyStaff);
		newOjtService.aiStaffStatusUpdate(companyStaff , statusCode); // 완료
		if(type != null) { 
			PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
			ploonetApiService.talkbotBrokerUpdate(loginInfoVo.getLoginCompanyPk(), companyStaff, type);	
		}
		
		if(!statusTemp) {// 완료
			newOjtService.ojtCompleteCallBroker(companyStaff, type);
		}
		return new ResponseVo(200); 
	}
	
	// OJT1 인사말 저장
//	@PostMapping("/companyIntroUpdate")
//	public Object companyIntroUpdate(
//			@RequestBody Map<String, Object> reqJsonObj) throws Exception {
//		
//		newOjtService.companyIntroUpdate(reqJsonObj);
//		
//		return new ResponseVo(200); 
//	}
	
	
	// OJT2 step 메뉴리스트(신규등록메뉴)
	@RequestMapping(value = {"/stepMenu"}, method = {RequestMethod.GET}, produces=PRODUCES_JSON)
	public Object stepMenu() throws Exception {
		Map<String, Object> reqJsonObj = new HashMap<>();
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		reqJsonObj.put("fkCompany", loginInfoVo.getLoginCompanyPk());
		Map<String, Object> resultMap = newOjtService.getStepList(reqJsonObj);
		return new ResponseVo( 200, resultMap );	
	}
	
	// OJT2 step 메뉴리스트(등록 후, 수정할 시)
	@RequestMapping(value = {"/stepMenuList"}, method = {RequestMethod.GET}, produces=PRODUCES_JSON)
	public Object stepMenuOneTwo(
			@RequestParam(value="pkCompanyStaff"     , required=false    ) final Integer pkCompanyStaff
			) throws Exception {

		Map<String, Object> reqJsonObj = new HashMap<>();
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();		
		reqJsonObj.put("fkCompany", loginInfoVo.getLoginCompanyPk());
		reqJsonObj.put("pkCompanyStaff", pkCompanyStaff);
		
		Map<String, Object> resultMap = newOjtService.getStepAll(reqJsonObj);
		
		return new ResponseVo( 200, resultMap );	
	}
	
	@RequestMapping(value = {"/stepMenuBotStatusCnt"}, method = {RequestMethod.GET}, produces=PRODUCES_JSON)
	public Object stepMenuBotStatusCnt(
			@RequestParam(value="pkCompanyStaff"     , required=false    ) final Integer pkCompanyStaff
			) throws Exception {

		Map<String, Object> reqJsonObj = new HashMap<>();
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();		
		reqJsonObj.put("fkCompany", loginInfoVo.getLoginCompanyPk());
		reqJsonObj.put("pkCompanyStaff", pkCompanyStaff);
		
		Map<String, Object> resultMap = newOjtService.getStepAll(reqJsonObj);
		
		return new ResponseVo( 200, resultMap );	
	}
	
	// OJT2 step 임시저장
	@PostMapping("/ojt2StepRegist")
	public Object infoRegist(@RequestBody Map<String, Object> reqJsonObj) throws Exception {
		
		newOjtService.ojt2StepRegist(reqJsonObj);
		
		int gubun = Common.parseInt(reqJsonObj.get("gubun"));
		
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		long pkCompanyStaff = Common.parseLong(reqJsonObj.get("pkCompanyStaff"));
		
		if(gubun == 1) {
			ploonetApiService.talkbotBrokerUpdate(loginInfoVo.getLoginCompanyPk(), pkCompanyStaff, "2");
		}	
		return new ResponseVo(200);
	}
	
	@RequestMapping(value = {"/stepMenuBotStatusCnt"}, method = {RequestMethod.GET})
	public Object stepMenuBotStatusCnt(
			@RequestParam(value="pkCompanyStaff") final String pkCompanyStaff
		)throws Exception {
		
		Map<String, Object> result = newOjtService.stepMenuBotStatusCnt(pkCompanyStaff);
		
		Map<String, Object> resultMap          = new HashMap<>();
		resultMap.put("cnt", result.get("cnt"));
		
		return new ResponseVo(200, resultMap);
	}
	
	@RequestMapping(value = {"/botStatusCount"}, method = {RequestMethod.GET})
	public Object botStatusCount(
			@RequestParam(value="pkCompanyStaff") final String pkCompanyStaff
		)throws Exception {
		
		int result = newOjtService.botStatusCount(pkCompanyStaff);
		
		Map<String, Object> resultMap          = new HashMap<>();
		resultMap.put("cnt", result);
		
		return new ResponseVo(200, resultMap);
	}
	
	@RequestMapping(value = {"/aiBotStatusComplate"}, method = {RequestMethod.GET})
	public Object aiBotStatusComplate(@RequestParam(value="pkCompanyStaff") final String pkCompanyStaff)throws Exception {
		
		int result = newOjtService.aiBotStatusComplate(pkCompanyStaff);
		
		Map<String, Object> resultMap          = new HashMap<>();
		resultMap.put("cnt", result);
		
		return new ResponseVo(200, resultMap);
	}
	
	@RequestMapping(value = {"/ojtCompleteMakeCall"})
	public Object ojtCompleteMakeCall(@RequestParam(value="pkCompanyStaff") final String pkCompanyStaff, @RequestParam(value="callPhone") final String callPhone)throws Exception {
		Map<String, Object> resultMap = newOjtService.ojtCompleteMakeCall(pkCompanyStaff, callPhone);
		
		return new ResponseVo(200, resultMap);
	}
	
	//OJT 담당자 변경
	@RequestMapping(value = {"/staffDeptChange"})
	public Object staffDeptChange(@RequestParam(value="pkCompanyStaff") final long pkCompanyStaff, @RequestParam(value="changePkCompanyStaff") final long changePkCompanyStaff)throws Exception {
		
		Map<String, Object> resultMap = newOjtService.staffDeptChange(pkCompanyStaff, changePkCompanyStaff);
		
		return new ResponseVo(200, resultMap);
	}
	
	//초기 값 가져오기(업무 수정 부분)
	@RequestMapping(value = {"/profileCard"}, method = {RequestMethod.GET}, produces=PRODUCES_JSON)
	public Object ProfileCard(@RequestParam(value="pkCompanyStaff") final long pkCompanyStaff) throws Exception {
		Map<String, Object> reqJsonObj = new HashMap<>();
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		reqJsonObj.put("fkCompany", loginInfoVo.getLoginCompanyPk());
		reqJsonObj.put("pkCompanyStaff", pkCompanyStaff);
		Map<String, Object> resultMap = newOjtService.profileCard(reqJsonObj);
		return new ResponseVo( 200, resultMap );	
	}
	
	
}