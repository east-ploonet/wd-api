package com.saltlux.aice_fe.pc.statistics.controller;

import static org.springframework.http.MediaType.parseMediaType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonObject;
import com.saltlux.aice_fe._baseline.baseController.BaseController;
import com.saltlux.aice_fe._baseline.baseVo.DataMap;
import com.saltlux.aice_fe._baseline.commons.Common;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.issue.dto.CompanyCustomerExcel;
import com.saltlux.aice_fe.pc.issue.service.ExcelService;
import com.saltlux.aice_fe.pc.issue.vo.ExcelVo;
import com.saltlux.aice_fe.pc.new_ojt.controller.NewOjtController;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
//@AllArgsConstructor
@RequestMapping("${apiVersionPrefix}/workStage/StatiStics") 
public class StatisticsController extends BaseController {
	
	@Value("${ploonet.statistics.api.url}")
	private String statisticsApiUrl;

	//플루닛 api 호출 - 플루닛본사에서 작업 할 때 api 쓰면 됨.
	//String statisticsApiUrl = "http://10.0.27.220:8211/statistics/";

	//로컬 api 호출
	//String statisticsApiUrl = "http://15.164.228.137:8211/statistics/";

	@Autowired
	private ExcelService excelService;

	//상담통계 불러오기
	@GetMapping("/statistics/getApi")
	public Object statisticsApi(
			@RequestParam(value="searchDateType", required=false)  String searchDateType,
			@RequestParam(value="startDate", required=false)  String startDate, // 조회 시작날짜
			@RequestParam(value="endDate", required=false)  String endDate, // 조회끝 날짜
			@RequestParam(value="page", required=false)  int page,
			@RequestParam(value="size", required=false)  String size
			) throws Exception {
		
		System.out.println("searchDateType : "+ searchDateType);
		
		// searchDateType : 오늘/1주일/1개월/3개월
		
		PcLoginInfoVo pcLoginInfoVo = getPcLoginInfoVo();
    	long loginCompanyPk = pcLoginInfoVo.getLoginCompanyPk();
    	//long loginCompanyPk = 70;
    	String apiVer = "v2";
    	  	
    	Map<String, Object> resultMap = new HashMap<>();
    	try {
        	SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
    		
        	// from 시작일(오늘 - 원하는 날짜만큼)  to 종료일(오늘일자)
        	
        	// 오늘날짜
    		Date currentTime = new Date();
    		String nowDate = format.format(currentTime);
    		Date todate = format.parse(nowDate);
    		
    		// format
    		Calendar cal = Calendar.getInstance();
    		cal.setTime(new Date());
    		DateFormat df = new SimpleDateFormat("yyyyMMdd");


    		String from = "20230901";
    		Calendar cal2 = Calendar.getInstance();
    		cal2.setTime(new Date());
    		cal2.add(Calendar.DATE, -1);
    		
    		String to = df.format(cal2.getTime());
    		JSONParser jsonParser = new JSONParser();
    		
            if(page == 1) page = 0;
            else page = page - 1;


			if(searchDateType.equals("0")) {
				cal2.add(Calendar.MONTH, -1);
				cal2.add(Calendar.DATE, 1);
				from = df.format(cal2.getTime());
				to = nowDate;
			}else if(searchDateType.equals("1")) { // 오늘
				cal2.add(Calendar.DATE, -1);
				from = nowDate; // 당일 데이터 없는 경우 result -1 나옴
				to = nowDate;
			}else if(searchDateType.equals("2")) { // 1주일
				cal2.add(Calendar.DATE, -7);
				from = df.format(cal2.getTime());
			}else if(searchDateType.equals("3")) { // 1개월
				cal2.add(Calendar.MONTH, -1);
				from = df.format(cal2.getTime());
			}else if(searchDateType.equals("4")) { // 3개월
				cal2.add(Calendar.MONTH, -3);
				from = df.format(cal2.getTime());
			}
    		
    		if(startDate != "" && endDate != "") {
    			from = startDate;
    			to = endDate;
    		}
    		
    		//1. 상담 통계 조회 (전체조회 및 변경 된 날짜 조회)
    		String statistics = Common.getRestDataApiGet(statisticsApiUrl+apiVer+"/tickets/"+ loginCompanyPk+"/total?from="+from+"&to="+to, null, "GET");
    		JSONObject jsonObj = (JSONObject)jsonParser.parse(statistics); 		
    		resultMap.put("statisticsData", jsonObj);
			System.out.println("상담 통계 전체 내역 url : " + statisticsApiUrl+apiVer+"/tickets/"+ loginCompanyPk+"/total?from="+from+"&to="+to);
			System.out.println("상담 통계 전체 내역 결과 : " + jsonObj);
    		
    		//2. 상담 통계 일자별 조회 (테이블)
    		String statisticsTable = Common.getRestDataApiGet(statisticsApiUrl+apiVer+"/tickets/"+ loginCompanyPk+"/find?page="+page+"&size="+size+"&from="+from+"&to="+to, null, "GET");
    		System.out.println("상담 통계 테이블 url : "+ statisticsApiUrl+apiVer+"/tickets/"+ loginCompanyPk+"/find?page="+page+"&size="+size+"&from="+from+"&to="+to);
    		JSONObject jsonObjTable = (JSONObject)jsonParser.parse(statisticsTable);
    		System.out.println("상담 통계 테이블 결과 "+ jsonObjTable);
    		resultMap.put("statiSticsTable", jsonObjTable);
    		
    		
    	}catch (Exception e) {
			System.out.println("e:" + e);
			resultMap.put("message", "error");
		}
    	
    	return resultMap;
	}
	
	//담당자통계 불러오기
	@GetMapping("/manager/getApi")
	public Object managerApi(
			@RequestParam(value="searchDateType", required=false)  String searchDateType,
			@RequestParam(value="fkStaff") final long fkStaff,
			@RequestParam(value="startDate", required=false)  String startDate, // 조회 시작날짜
			@RequestParam(value="endDate", required=false)  String endDate, // 조회끝 날짜
			@RequestParam(value="page", required=false)  int page,
			@RequestParam(value="size", required=false)  String size
			) throws Exception {
		
		//fkStaff = 0  : 전체 조회
		
		PcLoginInfoVo pcLoginInfoVo = getPcLoginInfoVo();
    	long loginCompanyPk = pcLoginInfoVo.getLoginCompanyPk();
    	//long loginCompanyPk = 70;
    	String apiVer = "v2";
    	
    	Map<String, Object> resultMap = new HashMap<>();
    	try {
        	SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
    		
    		Date currentTime = new Date();
    		String nowDate = format.format(currentTime);
    		Date todate = format.parse(nowDate);
    		
    		Calendar cal = Calendar.getInstance();
    		cal.setTime(new Date());
    		DateFormat df = new SimpleDateFormat("yyyyMMdd");
    		
    		Calendar cal2 = Calendar.getInstance();
    		cal2.setTime(new Date());
    		cal2.add(Calendar.DATE, -1);

    		String from = "20230901";
    		String to = df.format(cal2.getTime());
    		//String to = nowDate;
    		JSONParser jsonParser = new JSONParser();
    		
    		System.out.println("startDate : "+startDate);
    		System.out.println("endDate : "+endDate);


			if(searchDateType.equals("0")) {
				cal2.add(Calendar.MONTH, -1);
				cal2.add(Calendar.DATE, 1);
				from = df.format(cal2.getTime());
				to = nowDate;
			}else if(searchDateType.equals("1")) { // 오늘
				cal2.add(Calendar.DATE, -1);
				from = nowDate; // 당일 데이터 없는 경우 result -1 나옴
				to = nowDate;
			}else if(searchDateType.equals("2")) { // 1주일
				cal2.add(Calendar.DATE, -7);
				from = df.format(cal2.getTime());
			}else if(searchDateType.equals("3")) { // 1개월
				cal2.add(Calendar.MONTH, -1);
				from = df.format(cal2.getTime());
			}else if(searchDateType.equals("4")) { // 3개월
				cal2.add(Calendar.MONTH, -3);
				from = df.format(cal2.getTime());
			}
    		
            if(page == 1) page = 0;
            else page = page - 1;
    		
    		System.out.println("searchDateType : "+ searchDateType);
    		
    		if(startDate != "" && endDate != "") {
    			from = startDate;
    			to = endDate;
    		}
    		
    		//1. 담당자 통계 전체 조회 (메인 상단 검은색 영역)
    		//플루닛 API
    		//String manager = Common.getRestDataApiGet(statisticsApiUrl+apiVer+"/contacts/staff/"+ loginCompanyPk+"/"+fkStaff+"/total?from="+from+"&to="+to, null, "GET");
    		String manager = Common.getRestDataApiGet(statisticsApiUrl+apiVer+"/contacts/staff/"+ loginCompanyPk+"/"+0+"/total?from="+from+"&to="+to, null, "GET");
    		System.out.println("담당자 통계 전체 내역 url : "+statisticsApiUrl+apiVer+"/contacts/staff/"+ loginCompanyPk+"/"+fkStaff+"/total?from="+from+"&to="+to);
    		JSONObject jsonObj = (JSONObject)jsonParser.parse(manager);   		
    		resultMap.put("managerData", jsonObj);
			System.out.println("담당자 통계 전체 내역 결과 : "+jsonObj);
  
    		//2. 담당자 조회 (select option)
    		//String managerList = Common.getRestDataApiGet(statisticsApiUrl+apiVer+"/staffs/"+loginCompanyPk+"?from="+from+"&to="+to, null, "GET");
    		String managerList = Common.getRestDataApiGet(statisticsApiUrl+apiVer+"/staffs/"+loginCompanyPk+"?from="+from+"&to="+to, null, "GET");
    		System.out.println("담당자 조회 url :  : "+statisticsApiUrl+apiVer+"/staffs/"+loginCompanyPk+"?from="+from+"&to="+to);
    		JSONObject jsonObjList = (JSONObject)jsonParser.parse(managerList); 	
    		JSONArray members = (JSONArray)jsonObjList.get("members");
    		resultMap.put("members", members);

    		//3. 담당자 선택 시 테이블 영역 (전체는 0으로 처리)
    		System.out.println("담당자 통계 테이블 url : "+ statisticsApiUrl+apiVer+"/contacts/staff/"+loginCompanyPk+"/"+fkStaff+"/find?page="+page+"&size="+size+"&from="+from+"&to="+to);
    		String ManagerSelectList =  Common.getRestDataApiGet(statisticsApiUrl+apiVer+"/contacts/staff/"+loginCompanyPk+"/"+fkStaff+"/find?page="+page+"&size="+size+"&from="+from+"&to="+to, null, "GET"); 
    		JSONObject jsonObjSelectList = (JSONObject)jsonParser.parse(ManagerSelectList);
    		JSONArray jsonObjTable = (JSONArray)jsonObjSelectList.get("resultValue");
    		resultMap.put("tableList", jsonObjTable);
    		resultMap.put("data", jsonObjSelectList);
			System.out.println("담당자 통계 테이블 결과 :"+jsonObjSelectList);

    	}catch (Exception e) {
			System.out.println("e:" + e);
			resultMap.put("message", "error");
		}
    	
    	return resultMap;
	}
	
	// AI 통계 불러오기
	@GetMapping("/ai/getApi")
	public Object aiApi(
			@RequestParam(value="searchDateType", required=false)  String searchDateType, // 조회기간 
			@RequestParam(value="searchWorkType", required=false)  String searchWorkType, // 직무명
			@RequestParam(value="startDate", required=false)  String startDate, // 조회 시작날짜
			@RequestParam(value="endDate", required=false)  String endDate, // 조회끝 날짜
			@RequestParam(value="page", required=false)  int page,
			@RequestParam(value="size", required=false)  String size
			) throws Exception {

		PcLoginInfoVo pcLoginInfoVo = getPcLoginInfoVo();
    	long loginCompanyPk = pcLoginInfoVo.getLoginCompanyPk();
    	//long loginCompanyPk = 70;
    	String apiVer = "v2";
    	  	
    	Map<String, Object> resultMap = new HashMap<>();
    	try {
        	SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
    		
        	// from 시작일(오늘 - 원하는 날짜만큼)  to 종료일(오늘일자)
        	
        	// 오늘날짜
    		Date currentTime = new Date();
    		String nowDate = format.format(currentTime); 
    		Date todate = format.parse(nowDate); 
    		
    		// format
    		Calendar cal = Calendar.getInstance();
    		cal.setTime(new Date());
    		DateFormat df = new SimpleDateFormat("yyyyMMdd");
    		
    		Calendar cal2 = Calendar.getInstance();
    		cal2.setTime(new Date());
    		cal2.add(Calendar.DATE, -1);
    		
    		//통계 데이터는 6월1일부터 있음.
    		String from = "20230901";
    		//String to = nowDate;
    		String to = df.format(cal2.getTime());
    		JSONParser jsonParser = new JSONParser();
    		
            if(page == 1) page = 0;
            else page = page - 1;

			if(searchDateType.equals("0")) {
				cal2.add(Calendar.MONTH, -1);
				cal2.add(Calendar.DATE, 1);
				from = df.format(cal2.getTime());
				to = nowDate;
			}else if(searchDateType.equals("1")) { // 오늘
				cal2.add(Calendar.DATE, -1);
				from = nowDate; // 당일 데이터 없는 경우 result -1 나옴
				to = nowDate;
			}else if(searchDateType.equals("2")) { // 1주일
				cal2.add(Calendar.DATE, -7);
				from = df.format(cal2.getTime());
			}else if(searchDateType.equals("3")) { // 1개월
				cal2.add(Calendar.MONTH, -1);
				from = df.format(cal2.getTime());
			}else if(searchDateType.equals("4")) { // 3개월
				cal2.add(Calendar.MONTH, -3);
				from = df.format(cal2.getTime());
			}
    		
    		if(startDate != "" && endDate != "") {
    			from = startDate;
    			to = endDate;
    		}
    		
    		//1. AI 통계 조회 (전체조회 및 변경 된 날짜 조회)
    		String statistics = Common.getRestDataApiGet(statisticsApiUrl+apiVer+"/contacts/ai/"+ loginCompanyPk+"/"+searchWorkType+"/total?from="+from+"&to="+to, null, "GET");
    		System.out.println("AI직원 통계 전체 내역 url : " + statisticsApiUrl+apiVer+"/contacts/ai/"+ loginCompanyPk+"/"+searchWorkType+"/total?from="+from+"&to="+to);
    		System.out.println("AI직원 통계 전체 내역 결과 : "+ statistics);
    		JSONObject jsonObj = (JSONObject)jsonParser.parse(statistics); 		
    		resultMap.put("statisticsData", jsonObj);

//    		//2. AI 통계 일자별 조회 (테이블)
    		System.out.println("AI직원 통계 테이블 url : "+statisticsApiUrl+apiVer+"/contacts/ai/"+ loginCompanyPk+"/"+searchWorkType+"/find?page="+page+"&size="+size+"&from="+from+"&to="+to);
    		String statisticsTable = Common.getRestDataApiGet(statisticsApiUrl+apiVer+"/contacts/ai/"+ loginCompanyPk+"/"+searchWorkType+"/find?page="+page+"&size="+size+"&from="+from+"&to="+to, null, "GET");
    		JSONObject jsonObjTable = (JSONObject)jsonParser.parse(statisticsTable);
    		resultMap.put("statisticsTable", jsonObjTable);
			System.out.println("AI직원 통계 테이블 결과 :" + jsonObjTable);
    		
    	}catch (Exception e) {
			System.out.println("e:" + e);
			resultMap.put("message", "error");
		}
    	
    	return resultMap;
	}
	
	@GetMapping("/ai/getWorkCode")
	public Object aiGetWorkCode() throws Exception {

    	String apiVer = "v2";
    	JSONParser jsonParser = new JSONParser();
    	  	
    	Map<String, Object> resultMap = new HashMap<>();
    	try {  		
    		String workCodes = Common.getRestDataApiGet(statisticsApiUrl+apiVer+"/ai-policy-work/ai-work-cd", null, "GET");
    		JSONObject jsonObj = (JSONObject)jsonParser.parse(workCodes); 		
    		resultMap.put("workCodes", jsonObj);

    	}catch (Exception e) {
			System.out.println("e:" + e);
			resultMap.put("message", "error");
		}
    	
    	return resultMap;
	}
	
	 @GetMapping("/download/excel")
	    public ResponseEntity<?> downloadExcel(HttpServletRequest request) {

	        try {
				PcLoginInfoVo pcLoginInfoVo = getPcLoginInfoVo();
		    	long loginCompanyPk = pcLoginInfoVo.getLoginCompanyPk();
		    	String apiVer = "v2";

				Calendar cal = Calendar.getInstance();
				cal.setTime(new Date());
				cal.add(Calendar.MONTH, -1);
				Date oneMonthAgo = cal.getTime();

				SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
				String nowDate = format.format(new Date());
				String from = format.format(oneMonthAgo);
				String to = nowDate;
		    	
	    		System.out.println("상담 통계 엑셀 다운로드 url : "+statisticsApiUrl+apiVer+"/tickets/"+ loginCompanyPk+"/find?page=0&size=10000&from="+from+"&to="+to);
			 	String statisticsTable = Common.getRestDataApiGet(statisticsApiUrl+apiVer+"/tickets/"+ loginCompanyPk+"/find?page=0&size=10000&from="+from+"&to="+to, null, "GET");
			 	
			 	org.json.JSONObject jsonObject = new org.json.JSONObject(statisticsTable);
			 	org.json.JSONArray jsonArr =  jsonObject.getJSONArray("resultValue");
			 	
			 	
			 	List<ExcelVo> columnList = new ArrayList();
			 	columnList.add(new ExcelVo("상담일", "yyyyMmDd"));
			 	columnList.add(new ExcelVo("전체 상담 수", "ticTotCnt"));
			 	columnList.add(new ExcelVo("상담 완료 수", "ticComCnt"));
			 	columnList.add(new ExcelVo("AI 완료", "ticAiProCnt"));
			 	columnList.add(new ExcelVo("담당자 완료", "ticStaProCnt"));
			 	columnList.add(new ExcelVo("AI 평균 처리시간", "ticAveAiConSec"));
			 	columnList.add(new ExcelVo("상담사 평균 처리시간", "ticAveStaSec"));
			 	columnList.add(new ExcelVo("상담사 티켓 이관 건", "ticStaTraCnt"));
			 	columnList.add(new ExcelVo("평균 티켓 전환 응대 시간", "ticAveAiTraTalSec"));
			 	
		        Resource fileResource = excelService.downloadExcelByJson("test.xlsx", jsonArr, columnList);
		        
	        	String contentType = null;
	            contentType = request.getServletContext().getMimeType(fileResource.getFile().getAbsolutePath());
	            if (contentType == null) {
	            	contentType = "application/octet-stream";
	            }
	            return ResponseEntity.ok()
	            		.contentType(parseMediaType(contentType))
	            		.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileResource.getFilename() + "\"")
	            		.body(fileResource);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	    }
	 
	 @GetMapping("/download/excel/ai")
	    public ResponseEntity<?> downloadExcelAi(HttpServletRequest request) {
	        try {
				PcLoginInfoVo pcLoginInfoVo = getPcLoginInfoVo();
		    	long loginCompanyPk = pcLoginInfoVo.getLoginCompanyPk();
		    	String apiVer = "v2";

				Calendar cal = Calendar.getInstance();
				cal.setTime(new Date());
				cal.add(Calendar.MONTH, -1);
				Date oneMonthAgo = cal.getTime();

				SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
				String nowDate = format.format(new Date());
				String from = format.format(oneMonthAgo);
				String to = nowDate;
		    	
	    		System.out.println("AI직원 통계 엑셀 다운로드 url : "+statisticsApiUrl+apiVer+"/tickets/"+ loginCompanyPk+"/find?page=0&size=10000&from="+from+"&to="+to);
	    		String statisticsTable = Common.getRestDataApiGet(statisticsApiUrl+apiVer+"/contacts/ai/"+ loginCompanyPk+"/null/find?page=0&size=10000&from="+from+"&to="+to, null, "GET");
			 	org.json.JSONObject jsonObject = new org.json.JSONObject(statisticsTable);
			 	org.json.JSONArray jsonArr =  jsonObject.getJSONArray("resultValue");
				System.out.println("AI직원 통계 엑셀 다운로드 결과 : "+ jsonObject);
			 	
			 	
			 	List<ExcelVo> columnList = new ArrayList();
			 	columnList.add(new ExcelVo("상담일", "yyyyMmDd"));
			 	columnList.add(new ExcelVo("전체 상담 수", "conAiTotCnt"));
			 	columnList.add(new ExcelVo("상담 완료 수", "conAiComCnt"));
			 	columnList.add(new ExcelVo("티켓 전환 수", "conAiTicTraCnt"));
			 	columnList.add(new ExcelVo("ai 직원 처리율", "conAiThr"));
			 	columnList.add(new ExcelVo("평균 처리시간", "ticAveAiConSec"));			 	
		        Resource fileResource = excelService.downloadExcelByJson("test.xlsx", jsonArr, columnList);
		        
	        	String contentType = null;
	            contentType = request.getServletContext().getMimeType(fileResource.getFile().getAbsolutePath());
	            if (contentType == null) {
	            	contentType = "application/octet-stream";
	            }
	            return ResponseEntity.ok()
	            		.contentType(parseMediaType(contentType))
	            		.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileResource.getFilename() + "\"")
	            		.body(fileResource);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	    }
	 
	 @GetMapping("/download/excel/manager")
	    public ResponseEntity<?> downloadExcelManager(HttpServletRequest request) {
		 
	        try {
				PcLoginInfoVo pcLoginInfoVo = getPcLoginInfoVo();
		    	long loginCompanyPk = pcLoginInfoVo.getLoginCompanyPk();
		    	String apiVer = "v2";

				Calendar cal = Calendar.getInstance();
				cal.setTime(new Date());
				cal.add(Calendar.MONTH, -1);
				Date oneMonthAgo = cal.getTime();

				SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
				String nowDate = format.format(new Date());
				String from = format.format(oneMonthAgo);
				String to = nowDate;

			 	String ManagerSelectList =  Common.getRestDataApiGet(statisticsApiUrl+apiVer+"/contacts/staff/"+loginCompanyPk+"/0/find?page=0&size=10000&from="+from+"&to="+to, null, "GET");
			 	org.json.JSONObject jsonObject = new org.json.JSONObject(ManagerSelectList);
				System.out.println("담당자 통계 엑셀 다운로드 url :" + statisticsApiUrl+apiVer+"/contacts/staff/"+loginCompanyPk+"/0/find?page=0&size=10000&from="+from+"&to="+to);
				System.out.println("담당자 통계 엑셀 다운로드 결과 : "+jsonObject);
			 	org.json.JSONArray jsonArr =  jsonObject.getJSONArray("resultValue");
			 	
			 	
			 	List<ExcelVo> columnList = new ArrayList();
			 	columnList.add(new ExcelVo("상담일", "yyyyMmDd"));
			 	columnList.add(new ExcelVo("담당자 명", "fdStaffName"));
			 	columnList.add(new ExcelVo("전체 상담 수", "conStaTotCnt"));
			 	columnList.add(new ExcelVo("상담 완료 수", "conStaComCnt"));
			 	columnList.add(new ExcelVo("상담 이관 건수", "conStaTraCnt"));
			 	columnList.add(new ExcelVo("평균 처리시간", "conStaAveSec"));
			 	columnList.add(new ExcelVo("누적 상담 대기시간", "conStaWaiSec"));
			 
		        Resource fileResource = excelService.downloadExcelByJson("test.xlsx", jsonArr, columnList);
		        
	        	String contentType = null;
	            contentType = request.getServletContext().getMimeType(fileResource.getFile().getAbsolutePath());
	            if (contentType == null) {
	            	contentType = "application/octet-stream";
	            }
	            return ResponseEntity.ok()
	            		.contentType(parseMediaType(contentType))
	            		.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileResource.getFilename() + "\"")
	            		.body(fileResource);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	    }
	
}
