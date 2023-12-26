package com.saltlux.aice_fe.pc.send.controller;

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
import com.saltlux.aice_fe.pc.send.service.SendService;
import com.saltlux.aice_fe.pc.send.vo.ExcelListVo;
import com.saltlux.aice_fe._baseline.commons.Common;
import com.saltlux.aice_fe.pc.issue.service.ExcelService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("${apiVersionPrefix}/workStage/send") // end point : localhost:8080/api/v1/workStage/send
public class SendController extends BaseController {
	
	private final String CUSTOMER_EXCEL_TEMPLATE = "고객_엑셀등록양식.xlsx";
	
	@Autowired
	private ExcelService excelService;
	
	@Autowired
	private SendService sendService;
	
	
	@Value("${ploonet.api.audio.api.url}")
	private String audioApiUrl;
	
	@GetMapping("/personaAudio")
    public void personaAudio(HttpServletRequest request, HttpServletResponse response) {
        
		String readText = request.getParameter("readText");
		if(Common.isBlank(readText)) readText = "안녕하세요. 에이아이직원 앨리스입니다. 오늘 어떤 일로 전화주셨을까요?";
		//String readText = "안녕하세요.";
        String apiAddr = audioApiUrl;
        
        System.out.println("apiAddr : " + apiAddr);
        
        try {
        	readText = URLEncoder.encode(readText, "UTF-8");
        	apiAddr += readText;
        	URL url = new URL(apiAddr);
        	
        	System.out.println("apiAddrUrl : " + url);
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
	
	//엑셀 리스트 읽기
	@RequestMapping("/excelList")
	public Object excelList(
		@RequestParam(value="uploadFile"   , required=false    ) final MultipartFile[] uploadFiles
	) throws Exception {
		
		MultipartFile excelFile = uploadFiles[0];
		List<ExcelListVo> excelList = new ArrayList<ExcelListVo>();
		Map<String, Object> result  = new HashMap<>();
		
//		// 웹상에서 업로드 되어 MultipartFile인 경우 바로 InputStream으로 변경하여 사용.
		InputStream inputStream = new ByteArrayInputStream(excelFile.getBytes());
//	    //InputStream inputStream =  new ByteArrayInputStream(file.getBytes());
	    
//	    String filePath = "D:\\student.xlsx"; // xlsx 형식
	    // String filePath = "D:\\student.xls"; // xls 형식
//	    InputStream inputStream =  new FileInputStream(filePath);
		
	    // 엑셀 로드
	    Workbook workbook = WorkbookFactory.create(inputStream);
	    // 시트 로드 0, 첫번째 시트 로드
	    Sheet sheet = workbook.getSheetAt(0);
	    Iterator<Row> rowItr = sheet.iterator();
	    // 행만큼 반복
	    while(rowItr.hasNext()) {
	    	ExcelListVo customer = new ExcelListVo();
	        Row row = rowItr.next();
	        // 첫번재 행이 해더인 경우 스킵, 2번째 행부터 data 로드
	        if(row.getRowNum() == 0) {
	            continue;
	        }
	        Iterator<Cell> cellItr = row.cellIterator();
	        // 한행이 한열씩 읽기 (셀 읽기)
	        while(cellItr.hasNext()) {
	            Cell cell = cellItr.next();
	            int index = cell.getColumnIndex();
	            switch(index) {
	            case 0: // 이름
	            	customer.setFdCustomerName((String)getValueFromCell(cell));
	                break;
	            case 1: // 번호
	            	String phoneVal = (String)getValueFromCell(cell);
	            	phoneVal = phoneVal.replace("-", "");
	            	customer.setFdCustomerMobile(phoneVal);
//	            	customer.setPhone((String)getValueFromCell(cell));
	            	
	                break;
//	            case 2: // 연락처
//	            	customer.setFdPhone((String)getValueFromCell(cell));
//	            	break;
	            case 2: // 이메일
	            	customer.setFdCustomerEmail((String)getValueFromCell(cell));
	                break;
	            // 소속도 엑셀로 등록?
//	            case 4: // 소속
//	            	customer.setFdDepartment((String)getValueFromCell(cell));
//	                break;
	            }
	        }
	        if(Common.isBlank(customer.getFdCustomerName()) || Common.isBlank(customer.getFdCustomerMobile())) {
	        	
	        }else {
	        	excelList.add(customer);
	        }
	    } 
	    
	    result.put("excelList", excelList);
	    
		return new ResponseVo(200, result);
	}
	
//	//고객 정보 읽기
//	@RequestMapping("/customerInfo")
//	public Object customerInfo(
//			@RequestParam(value="fdMobile"     , required=false    ) final String fdMobile
//	) throws Exception {
//		
//		Map<String, Object> reqJsonObj = new HashMap<>();
//		reqJsonObj.put("fdMobile", fdMobile);
//		
//		DataMap resultMap = sendService.getCustomerInfo(reqJsonObj);
//		
//		return new ResponseVo(200, resultMap);
//	}
	
	//고객 정보 읽기
		@RequestMapping("/customerInfo")
		public Object customerInfo(
				@RequestParam(value="pkCompanyCustomer"     , required=false    ) final String pkCompanyCustomer
		) throws Exception {
			
			Map<String, Object> reqJsonObj = new HashMap<>();
			reqJsonObj.put("pkCompanyCustomer", pkCompanyCustomer);
			
			DataMap resultMap = sendService.getCustomerInfo(reqJsonObj);
			
			return new ResponseVo(200, resultMap);
		}
	
	//고객 정보 등록/수정
	@PostMapping("/saveCompanyCustomer")
    public Object saveCompanyCustomer(@RequestParam(value="formData" , required=false ) final String formJson) throws Exception {
        
        sendService.saveCompanyCustomer(formJson);
        return new ResponseVo(200);
    }
	
	//고객 정보 등록/수정 (단일 추가 - 직접 입력)
	@PostMapping("/saveNewCompanyCustomer") 
    public Object saveNewCompanyCustomer(
    		@RequestParam(value="mobileList" , required=false ) final String mobileList,
    		@RequestParam(value="addList" , required=false ) final String addList
    		) throws Exception {
        
		Map<String, Object> resultMap = sendService.saveNewCompanyCustomer(mobileList, addList);
        return new ResponseVo(200, resultMap);
    }
	
	//고객 정보 등록/수정 (엑셀)
	@PostMapping("/saveNewCompanyCustomerList")
    public Object saveNewCompanyCustomerList(
    		@RequestParam(value="mobileList" , required=false ) final String[] mobileList,
    		@RequestParam(value="addList" , required=false ) final String addList
    		) throws Exception {
        
		Map<String, Object> resultMap = sendService.saveNewCompanyCustomerList(mobileList, addList);
        return new ResponseVo(200, resultMap);
    }
	
	
	//발신 눌렀을 때 저장할 정보들
	@PostMapping("/saveSnsInfo")
    public Object saveSnsInfo(
    		@RequestParam(value="totalList" , required=false ) final String[] totalList,
    		@RequestParam(value="numberToList" , required=false ) final String[] numberToList,
    		@RequestParam(value="sendNumber" , required=false ) final String sendNumber,
    		@RequestParam(value="formData" , required=false ) final String formData,
    		@RequestParam(value="adTitle" , required=false ) final String adTitle,
    		@RequestParam(value="uploadFile" , required=false ) final MultipartFile[] uploadFiles
    		) throws Exception {
        
		sendService.saveSnsInfo(totalList, numberToList, sendNumber, formData, adTitle, uploadFiles);
        return new ResponseVo(200);
    }
	
	//발신 이력에서 제목 클릭해서 들어올 때
	@RequestMapping("/getDetailInfo")
	public Object getDetailInfo(
			@RequestParam(value="pkSend"     , required=false    ) final String pkSend
	) throws Exception {
		
		Map<String, Object> reqJsonObj = new HashMap<>();
		reqJsonObj.put("pkSend", pkSend);
		
		DataMap resultMap = sendService.getDetailInfo(reqJsonObj);
		List<Object> resultMap2 = sendService.getDetailCustomer(reqJsonObj);
		
		//발신번호 호출
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		Map<String, Object>defaultYnMap          = new HashMap<>();
		defaultYnMap.put("fk_company", loginInfoVo.getLoginCompanyPk());
		List<Object> defaultYnData = sendService.defaultYnUse(defaultYnMap);
	
		
		Map<String, Object> result  = new HashMap<>();
		result.put("sendDetail", resultMap);
		result.put("sendDetailCustomer", resultMap2);
		result.put("defaultYn", defaultYnData);
		
		return new ResponseVo(200, result);
	}
	
	//발신 이력에서 제목 클릭해서 들어올 때
	@GetMapping("/getRprsnNmbr")
	public Object getRprsnNmbr() throws Exception {
		
		Map<String, Object> reqJsonObj = new HashMap<>();
		
		//발신번호 호출
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		Map<String, Object>defaultYnMap          = new HashMap<>();
		defaultYnMap.put("fk_company", loginInfoVo.getLoginCompanyPk());
		List<Object> defaultYnData = sendService.defaultYnUse(defaultYnMap);
		
		
		DataMap resultMap = sendService.selectDnis(defaultYnMap);
		
		Map<String, Object> result  = new HashMap<>();
		result.put("rprsnNmbr", defaultYnData);
		result.put("resultMap", resultMap);
		
		return new ResponseVo(200, result);
	}
	

	
	//발신 리스트 선택했을 때, 발신 리스트 이름 및 인원 수 호출
	@GetMapping("/getSendHistory")
	public Object getSendHistory(
			@RequestParam(value="page"     , required=false , defaultValue = "1"    ) final int page,
			@RequestParam(value="pageSize", required=true) final int pageSize,
			@RequestParam(value="startDate"     , required=false , defaultValue = ""    ) final String startDate,
			@RequestParam(value="endDate"     , required=false , defaultValue = ""    ) final String endDate,
			@RequestParam(value="channel"     , required=false , defaultValue = ""    ) final String channel,
			@RequestParam(value="channelType"     , required=false , defaultValue = ""    ) final String channelType,
			@RequestParam(value="sender"     , required=false , defaultValue = ""    ) final String sender
		) throws Exception {
		
		Integer offset = (page -1) * pageSize;
		
		Map<String, Object> resultMap = sendService.getSendHistory(offset, pageSize, startDate, endDate, channel, channelType, sender);
		
		return new ResponseVo( 200, resultMap );
	}
	
	//발신 리스트 선택했을 때, 발신 리스트 이름 및 인원 수 호출
	@GetMapping("/getSendUserHistory")
	public Object getSendUserHistory(
			@RequestParam(value="pkBulkSendPlan", required=true) final String pkBulkSendPlan,
			@RequestParam(value="page"     , required=false , defaultValue = "1"    ) final int page,
			@RequestParam(value="pageSize", required=true) final String pageSize,
			@RequestParam(value="search"     , required=false , defaultValue = ""    ) final String search
			) throws Exception {
		
		Integer pageSizeInt = Integer.parseInt(pageSize);
		Integer offset = (page -1) * pageSizeInt;
		
		Map<String, Object> resultMap = sendService.getSendUserHistory(pkBulkSendPlan, offset, pageSizeInt, search);
		
		return new ResponseVo( 200, resultMap );
	}
	
	
	//발신 리스트 선택했을 때, 발신 리스트 이름 및 인원 수 호출
	@GetMapping("/getSendList")
	public Object getSendList() throws Exception {
		
		Map<String, Object> resultMap = sendService.getSendList();
		
		return new ResponseVo( 200, resultMap );
	}

	//발신 리스트 선택했을 때, 발신 리스트 이름 및 인원 수 호출
	@GetMapping("/getSendListCustomer")
	public Object getSendListCustomer(
		@RequestParam(value="sendInfo"     , required=false    ) final String pkSendInfo
		) throws Exception {
		
		Map<String, Object> reqJsonObj = new HashMap<>();
		reqJsonObj.put("pk_send_info", pkSendInfo);
		
		Map<String, Object> resultMap = sendService.getSendListCustomer(reqJsonObj);
		
		return new ResponseVo( 200, resultMap );
	}
	
	//발신 리스트 선택했을 때, 발신 리스트 이름 및 인원 수 호출
	@GetMapping("/getCompanyCustomer")
	public Object getCompanyCustomer(
			@RequestParam(value="page"     , required=false , defaultValue = "1"    ) final int page,
			@RequestParam(value="search"     , required=false , defaultValue = ""    ) final String search
		) throws Exception {
		
		Map<String, Object> resultMap = sendService.getCompanyCustomer(page, search);
		
		return new ResponseVo( 200, resultMap );
	}
	
	@GetMapping("/download/form")
    public ResponseEntity<?> downloadCustomerTemplateExcel(HttpServletRequest request) {
        Resource fileResource = excelService.downloadCustomerTemplateExcel(CUSTOMER_EXCEL_TEMPLATE);
        
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
	
	
	// 셀서식에 맞게 값 읽기
    private static Object getValueFromCell(Cell cell) {
	    switch(cell.getCellType()) {
	        case STRING:
	            return cell.getStringCellValue();
	        case BOOLEAN:
	            return cell.getBooleanCellValue();
	        case NUMERIC:
	            if(DateUtil.isCellDateFormatted(cell)) {
	                return cell.getDateCellValue();
	            }
	            return cell.getNumericCellValue();
	        case FORMULA:
	            return cell.getCellFormula();
	        case BLANK:
	            return "";
	        default:
	            return "";                                
	    }
	}
}