package com.saltlux.aice_fe.pc.dashboard.controller;

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
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
import com.saltlux.aice_fe.pc.dashboard.service.DashBoardService;
import com.saltlux.aice_fe.pc.dashboard.vo.DashBoardVo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("${apiVersionPrefix}/workStage/DashBoard") // end point : localhost:8080/api/v1/workStage/ojt
public class DashBoardController extends BaseController {
	
	private final List<String> staffLevelCode = new ArrayList<>(Arrays.asList("A1001", "A1002"));
		
	@Value("${globals.ph.upload.file}")
    private String phNotUpload;
	
	@Value("${ploonet.dashboard.api.url}")
	private String dashboardApiUrl;
	
	//로컬 api 호출
	//String dashboardApiUrl = "http://15.164.228.137:8141/dashboard/monitor/";

	@Autowired
	private DashBoardService dashBoardService;
	
    private List<Long> getFkAssignStaffIds(String loginLevelCode, long id) {
		if (!staffLevelCode.contains(loginLevelCode)) {
			return Arrays.asList(id);
	} else {
			return null;
		}
	}
	
	//대시보드 불러오기
    @RequestMapping(value = {"/dashBoardList"}, method = {RequestMethod.POST}, produces=PRODUCES_JSON)
    public Object dashBoardList(@RequestParam(value = "page"      , required = false, defaultValue = "1"  ) int page
		    , @RequestParam(value = "pageSize"  , required = false, defaultValue = "1000" ) int pageSize
		    , @RequestParam(value = "searchString"    , required = false, defaultValue = ""   ) String searchString
		    , @RequestParam(value = "searchColumn"    , required = false, defaultValue = ""   ) String searchColumn
		    , @RequestParam(value = "searchStatus"    , required = false, defaultValue = ""   ) String searchStatus
		    , @RequestParam(value = "startDate"    , required = false, defaultValue = ""   ) String startDate
		    , @RequestParam(value = "endDate"    , required = false, defaultValue = ""   ) String endDate
		    , @RequestParam(value = "orderBy"    , required = false, defaultValue = ""   ) String orderBy
		    , @RequestParam(value = "orderType"    , required = false, defaultValue = ""   ) String orderType
			, @RequestBody DashBoardVo dashBoardVo
    ) throws Exception {

		PcLoginInfoVo pcLoginInfoVo = getPcLoginInfoVo();
		
		System.out.println("pcLoginInfoVo : "+pcLoginInfoVo);

		dashBoardVo.setFkAssignStaffIds(getFkAssignStaffIds(pcLoginInfoVo.getLoginLevelCode(), pcLoginInfoVo.getLoginCompanyStaffPk()));

		dashBoardVo.setFk_company_staff(pcLoginInfoVo.getLoginCompanyPk());
		dashBoardVo.getSearch().setPage(page);
		dashBoardVo.getSearch().setPageSize(pageSize);
		dashBoardVo.getSearch().setSearchString(searchString);
		dashBoardVo.getSearch().setSearchColumn(searchColumn);
		dashBoardVo.getSearch().setSearchStatus(searchStatus);
		dashBoardVo.getSearch().setStartDate(startDate);
		dashBoardVo.getSearch().setEndDate(endDate);
		dashBoardVo.getSearch().setOrderBy(orderBy);
		dashBoardVo.getSearch().setOrderType(orderType);
        Map<String, Object> resultMap = dashBoardService.getIssues(dashBoardVo);
                
        return new ResponseVo(200, resultMap);
    }
    
    //대시보드 불러오기
    @GetMapping("/dashBoardList/getApi")
    public Object dashBoardListApi() throws Exception {
    	
    	PcLoginInfoVo pcLoginInfoVo = getPcLoginInfoVo();

    	long loginCompanyPk = pcLoginInfoVo.getLoginCompanyPk();
    	//long loginCompanyPk = 70;
    	//long loginCompanyPk = 248;
    	String apiVer = "v1";
    	
    	Map<String, Object> results = new HashMap<>();
    		//1. 상담 통화 현황
    		String ConsultationCallStatus = Common.getRestDataApiGet(dashboardApiUrl + apiVer + "/companys/" + loginCompanyPk + "/contacts/all", null, "GET");
    		JSONParser jsonParser = new JSONParser();
    		org.json.simple.JSONObject jsonObjConsultationCallStatus = (org.json.simple.JSONObject)jsonParser.parse(ConsultationCallStatus);
			System.out.println("dashboard url : " + dashboardApiUrl + apiVer + "/companys/" + loginCompanyPk + "/contacts/all");
    		System.out.println("jsonObjConsultationCallStatus:" + jsonObjConsultationCallStatus);
    		if(jsonObjConsultationCallStatus.get("resultValue") != null) {
    		String jsonObjConsultationCallStatusResult = jsonObjConsultationCallStatus.get("resultValue").toString();
        	
        	//2.오늘의 티켓 현황
        	String ticketToday = Common.getRestDataApiGet(dashboardApiUrl+apiVer+"/companys/"+loginCompanyPk+"/tickets/today", null, "GET");
        	JSONParser jsonParserTicket = new JSONParser();
        	org.json.simple.JSONObject jsonParserTickets = (org.json.simple.JSONObject)jsonParserTicket.parse(ticketToday);
        	String jsonParserTicketResult = jsonParserTickets.get("resultValue").toString();
        	
        	//3. Ai별 상담 현황
        	String aiStatus = Common.getRestDataApiGet(dashboardApiUrl+apiVer+"/companys/"+loginCompanyPk+"/contacts/ais", null, "GET");
        	JSONParser jsonParserAi = new JSONParser();
        	org.json.simple.JSONObject jsonParserAis = (org.json.simple.JSONObject)jsonParserAi.parse(aiStatus);
        	String jsonParserAiResult = jsonParserAis.get("resultValue").toString();
        	System.out.println("URL주소 :"+dashboardApiUrl+apiVer+"/companys/"+loginCompanyPk+"/contacts/ais");
        	System.out.println("결과 "+jsonParserAiResult);
        	
        	//4. 이달의 티켓 현황
        	String ticketMonth = Common.getRestDataApiGet(dashboardApiUrl+apiVer+"/companys/"+loginCompanyPk+"/tickets/month", null, "GET");
        	JSONParser jsonParserTicketMonth = new JSONParser();
        	org.json.simple.JSONObject jsonParserTicketsMonth = (org.json.simple.JSONObject)jsonParserTicketMonth.parse(ticketMonth);
        	String jsonParserTicketMonthResult = jsonParserTicketsMonth.get("resultValue").toString();
        	
        	//System.out.println("jsonParserTicketMonthResult : " +jsonParserTicketMonthResult);
        	
        	results.put("consultationCallStatus", jsonObjConsultationCallStatusResult);
        	results.put("ticketToday", jsonParserTicketResult);
        	results.put("aiStatus", jsonParserAiResult);
        	results.put("ticketMonth", jsonParserTicketMonthResult);
    	}
    	
    	
    	return new ResponseVo(200, results);
    }
	
}