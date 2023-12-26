package com.saltlux.aice_fe.pc.campaginsendList.controller;

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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
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
import com.saltlux.aice_fe.pc.campaginsendList.service.CampaginSendListService;
import com.saltlux.aice_fe.pc.campaginsendList.vo.CampaginSendExcelListVo;
import com.saltlux.aice_fe.pc.campaginsendList.vo.CampaginSendListInfoVo;
import com.saltlux.aice_fe.pc.issue.dto.CompanyStaffExcel;
import com.saltlux.aice_fe.pc.issue.service.CompanyCustomerService;
import com.saltlux.aice_fe.pc.issue.vo.CompanyCustomerVo;
import com.saltlux.aice_fe.pc.send.vo.ExcelListVo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("${apiVersionPrefix}/workStage/CampaginSendList") // end point : localhost:8080/api/v1/workStage/ojt
public class CampaginSendController extends BaseController {
	
	@Autowired
	private CampaginSendListService sendListService;
	
	@Autowired
	private CompanyCustomerService companyCutomerService;
		
	@Value("${globals.ph.upload.file}")
    private String phNotUpload;
		
	//엑셀 리스트 읽기
	@RequestMapping("/excelList")
	public Object excelList(
		@RequestParam(value="uploadFile"   , required=false    ) final MultipartFile[] uploadFiles
	) throws Exception {
		
		MultipartFile excelFile = uploadFiles[0];
		List<CampaginSendExcelListVo> excelList = new ArrayList<CampaginSendExcelListVo>();
		Map<String, Object> result  = new HashMap<>();
		
//		// 웹상에서 업로드 되어 MultipartFile인 경우 바로 InputStream으로 변경하여 사용.
		InputStream inputStream = new ByteArrayInputStream(excelFile.getBytes());

	    // 엑셀 로드
	    Workbook workbook = WorkbookFactory.create(inputStream);
	    // 시트 로드 0, 첫번째 시트 로드
	    Sheet sheet = workbook.getSheetAt(0);
	    Iterator<Row> rowItr = sheet.iterator();
	    // 행만큼 반복
	    while(rowItr.hasNext()) {
	    	CampaginSendExcelListVo customer = new CampaginSendExcelListVo();
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
	            case 1: // 휴대전화
	            	customer.setFdCustomerMobile((String)getValueFromCell(cell));
	                break;
	            case 2: // 연락처
	            	customer.setFdCustomerPhone((String)getValueFromCell(cell));
	                break;
	            case 3: // 이메일
	            	customer.setFdCustomerEmail((String)getValueFromCell(cell));
	                break;
	            case 4: // 소속
	            	customer.setFdCompanyDept((String)getValueFromCell(cell));
	                break;
	            }
	        }
	        excelList.add(customer);
	    } 
	    
	    result.put("excelList", excelList);
	    System.out.println("excelList : "+excelList);
		return new ResponseVo(200, result);
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
    //엑셀 양식 다운로드
    @GetMapping("/download/excelList")
	    public ResponseEntity<?> downloadTemplate(HttpServletRequest request) {
    	
    	String fileName = "sendList_excel.xlsx";
    	Resource fileResource = sendListService.downloadTemplate(fileName);
    	
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
    	
	//발신리스트 관리 등록
	@PostMapping("/infoRegist")
	public Object infoRegist(@RequestBody Map<String, Object> reqJsonObj) throws Exception {
		
		Map<String, Object> result = sendListService.infoRegist(reqJsonObj);
				
		return new ResponseVo(Common.parseInt(result.get("status")), result.get("message").toString());
	}
	
	//발신 전체 리스트 불러오기, 클릭시 상세리스트 포함(fkSendInfo로 구분)
	@GetMapping("/sendInfo")
	public Object sendInfo(
			@RequestParam(value = "page", required = false, defaultValue = "1"  ) int page,
			@RequestParam(value="fkSendInfo") final String fkSendInfo,
			@RequestParam(value="select") final String select
			) throws Exception {
				
		Map<String, Object> reqJsonObj = new HashMap<>();
		PcLoginInfoVo loginInfoVo = getPcLoginInfoVo();
		reqJsonObj.put("page", page);
		reqJsonObj.put("fk_company", loginInfoVo.getLoginCompanyPk());
		reqJsonObj.put("select", select);
		
		if(!Common.isBlank(fkSendInfo)) {
			reqJsonObj.put("fkSendInfo", fkSendInfo);
		}
		
		Map<String, Object> resultMap = sendListService.getSendList(reqJsonObj);
		return new ResponseVo( 200, resultMap );
	}
	
	// 발신리스트 업데이트
	@PostMapping("/sendListUpdate")
	public Object sendInfoUpdate(
			@RequestBody Map<String, Object> reqJsonObj
			) throws Exception {
		
		Map<String, Object> result = sendListService.sendListUpdate(reqJsonObj);
	
		return new ResponseVo(Common.parseInt(result.get("status")), result.get("message").toString());
	}
	
	//발신리스트 삭제
	@PutMapping("/sendListDelete")
	public Object sendListDelete(@RequestBody Map<String, Object> reqJsonObj) throws Exception {
		
		sendListService.sendListDelete(reqJsonObj);
				
		return new ResponseVo(200);
	}
	
	//연락처 추가(직접입력)
    @PostMapping("/saveCompanyCustomer")
    public Object saveCompanyCustomer(@RequestBody Map<String, Object> reqJsonObj) throws Exception {
        
        Map<String, Object> result = sendListService.saveCompanyCustomer(reqJsonObj);
		
        return new ResponseVo(200, result);
//		return new ResponseVo(Common.parseInt(result.get("status")), result.get("message").toString());

    }
    
    //preformData 고객리스트 불러오기
    @GetMapping("/getCustomerInfo")
    public Object getCustomerInfo() throws Exception {
    	//여기에 리스트 항목추가
    	Map<String, Object> resultMap = sendListService.getCustomerList();
    	return new ResponseVo(200, resultMap);
    }
	
}