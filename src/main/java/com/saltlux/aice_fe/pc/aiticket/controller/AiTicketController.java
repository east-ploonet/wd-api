package com.saltlux.aice_fe.pc.aiticket.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.saltlux.aice_fe.pc.aiticket.vo.AiTicketVo;
import com.saltlux.aice_fe.pc.aiticket.vo.DialogueVo;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saltlux.aice_fe._baseline.baseController.BaseController;
import com.saltlux.aice_fe._baseline.baseVo.DataMap;
import com.saltlux.aice_fe._baseline.baseVo.ResponseVo;
import com.saltlux.aice_fe._baseline.commons.Common;
import com.saltlux.aice_fe.pc.aiticket.service.AiTicketService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("${apiVersionPrefix}/workStage/aiticket") // end point : localhost:8080/api/v1/aiticket
public class AiTicketController extends BaseController {

	@Autowired
	private AiTicketService aiTicketService;


	// aiCounselor list 불러오기
	@RequestMapping(value = {"/ai/list"}, method = {RequestMethod.GET})
	public Object getList(
			@RequestParam(value = "page", required = false, defaultValue = "1") final int page,
			@RequestParam(value = "pageSize", required = false, defaultValue = "10") final int pageSize,
			@RequestParam(value = "startDateString", required = false, defaultValue = "") final String startDate,
			@RequestParam(value = "endDateString", required = false, defaultValue = "") final String endDate,
			@RequestParam(value = "pkIssueContact", required = false, defaultValue = "") final String pkIssueContact,
			@RequestParam(value = "searchAiName", required = false, defaultValue = "") final String searchAiName,
			@RequestParam(value = "searchType", required = false, defaultValue = "") final String searchType,
			@RequestParam(value = "searchCallerName", required = false, defaultValue = "") final String searchCallerName,
			@RequestParam(value = "searchCustomerInfo", required = false, defaultValue = "") final String searchCustomerInfo
	) throws Exception {

		Integer offset = (page - 1) * 10;

		Map<String, Object> resultMap = aiTicketService.getList(offset, pageSize, startDate, endDate, pkIssueContact, searchAiName, searchType, searchCallerName, searchCustomerInfo);

		return new ResponseVo(200, resultMap);

	}

	// aiCounselor 상세정보 불러오기
	@RequestMapping(value = {"/ai/detail"}, method = {RequestMethod.GET})
	public Object getDetail(
			@RequestParam(value = "pkIssueContact", required = false, defaultValue = "") final String pkIssueContact
	) throws Exception {


		Map<String, Object> resultMap = aiTicketService.getDetail(pkIssueContact);

		return new ResponseVo(200, resultMap);

	}


	// aiCounselorType list 불러오기 // 사용 안함
	@RequestMapping(value = {"/ai/aiTypeList"}, method = {RequestMethod.GET})
	public Object getAiTypeList() throws Exception {

		Map<String, Object> resultMap = aiTicketService.getAiTypeList();

		return new ResponseVo(200, resultMap);

	}


	@RequestMapping(value = {"/ai/dialogue"}, method = {RequestMethod.GET})
	public Object getDialogue(
			@RequestParam(value = "fdCallBrokerId", required = false, defaultValue = "") final String fdCallBrokerId
	) throws Exception {

		Map<String, Object> resultMap = aiTicketService.getDialogue(fdCallBrokerId);

		return new ResponseVo(200, resultMap);

	}

	// ticket list 불러오기
	@RequestMapping(value = {"/ticket/list"}, method = {RequestMethod.GET})
	public Object getTicketList(
			@RequestParam(value = "page", required = false, defaultValue = "1") final int page,
			@RequestParam(value = "pageSize", required = false, defaultValue = "10") final int pageSize,
			@RequestParam(value = "startDateString", required = false, defaultValue = "") final String startDate,
			@RequestParam(value = "endDateString", required = false, defaultValue = "") final String endDate,
			@RequestParam(value = "pkIssueTicket", required = false, defaultValue = "") final String pkIssueTicket,
			@RequestParam(value = "searchAiName", required = false, defaultValue = "") final String searchAiName,
			@RequestParam(value = "searchType", required = false, defaultValue = "") final String searchType,
			@RequestParam(value = "searchCallerName", required = false, defaultValue = "") final String searchCallerName,
			@RequestParam(value = "searchCustomerInfo", required = false, defaultValue = "") final String searchCustomerInfo,
			@RequestParam(value = "searchTypeList", required = false, defaultValue = "") final String searchTypeList,
			@RequestParam(value = "searchWorkflow", required = false, defaultValue = "") final String searchWorkflow,
			@RequestParam(value = "searchStaff", required = false, defaultValue = "") final String searchStaff
	) throws Exception {

		Integer offset = (page - 1) * 10;

		Map<String, Object> resultMap = aiTicketService.getTicketList(offset, pageSize, startDate, endDate, pkIssueTicket, searchAiName, searchType, searchCallerName, searchCustomerInfo, searchTypeList, searchWorkflow, searchStaff);

		return new ResponseVo(200, resultMap);

	}


	// ticket 상세정보 불러오기
	@RequestMapping(value = {"/ticket/detail"}, method = {RequestMethod.GET})
	public Object getTicketDetail(
			@RequestParam(value = "pkIssueContact", required = false, defaultValue = "") final String pkIssueContact
	) throws Exception {


		Map<String, Object> resultMap = aiTicketService.getTicketDetail(pkIssueContact);

		return new ResponseVo(200, resultMap);

	}

	// 변경된 OJT 기본정보
	@RequestMapping(value = {"/ticket/customerInfo"}, method = {RequestMethod.GET})
	public Object getCustomerInfo(
			@RequestParam(value = "ani", required = false, defaultValue = "") final String ani,
			@RequestParam(value = "caller", required = false, defaultValue = "") final String caller
	) throws Exception {

		Map<String, Object> resultMap = aiTicketService.getCustomerInfo(ani, caller);

		return new ResponseVo(200, resultMap);

	}


	// 변경된 OJT 기본정보
	@RequestMapping(value = {"/ticket/staffList"}, method = {RequestMethod.GET})
	public Object getStaffList() throws Exception {

		Map<String, Object> resultMap = aiTicketService.getStaffList();

		return new ResponseVo(200, resultMap);

	}


	@PostMapping("/ticket/updateCustomerInfo")
	public Object updateCustomerInfo(
			@RequestParam(value = "customer", required = false) final String customer,
			@RequestParam(value = "callerType", required = false) final String callerType
	) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> customerInfo = mapper.readValue(customer, Map.class);
		aiTicketService.updateCustomerInfo(callerType, customerInfo);

		return new ResponseVo(200);

	}

	@PostMapping("/ticket/updateTicketInfo")
	public Object updateTicketInfo(
			@RequestParam(value = "ticket", required = false) final String ticket
	) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> ticketInfo = mapper.readValue(ticket, Map.class);
		aiTicketService.updateTicketInfo(ticketInfo);

		return new ResponseVo(200);

	}

	@PostMapping("/ticket/updateTicketFlow")
	public Object updateTicketFlow(
			@RequestParam(value = "pkIssueTicket", required = false) final String pkIssueTicket,
			@RequestParam(value = "workflowCode", required = false) final String workflowCode
	) throws Exception {

		aiTicketService.updateTicketFlow(pkIssueTicket, workflowCode);

		return new ResponseVo(200);

	}

	@PostMapping("/ticket/changeTicketStaff")
	public Object changeTicketStaff(
			@RequestParam(value = "fkIssueTicket", required = false) final String fkIssueTicket,
			@RequestParam(value = "staffPk", required = false) final String staffPk
	) throws Exception {


		aiTicketService.changeTicketStaff(fkIssueTicket, staffPk);

		return new ResponseVo(200);

	}

	// AI상담 list 엑셀 다운로드
	@GetMapping(value = {"/ai/excelDownload"})
	public ResponseEntity<InputStreamResource> aitExcel(HttpServletResponse response,
														@RequestParam(value = "startDateString", required = false, defaultValue = "") final String startDate,
														@RequestParam(value = "endDateString", required = false, defaultValue = "") final String endDate,
														@RequestParam(value = "searchType", required = false, defaultValue = "") final String searchType
	) throws Exception {
		List<Object> listData = aiTicketService.getListDue(startDate, endDate, searchType);

//		System.out.println(listData);

		try (Workbook workbook = new XSSFWorkbook()) {
			Sheet sheet = workbook.createSheet("AI상담리스트");
			int rowNo = 0;

			CellStyle headStyle = workbook.createCellStyle();
			headStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.LIGHT_BLUE.getIndex());
			headStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			Font font = workbook.createFont();
			font.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
			font.setFontHeightInPoints((short) 13);
			headStyle.setFont(font);

			Row headerRow = sheet.createRow(rowNo++);
			headerRow.createCell(0).setCellValue("상담분류");
			headerRow.createCell(1).setCellValue("상담내용");
			headerRow.createCell(2).setCellValue("고객명");
			headerRow.createCell(3).setCellValue("연락처");
			headerRow.createCell(4).setCellValue("요청일");

			for (int i = 0; i <= 4; i++) {
				headerRow.getCell(i).setCellStyle(headStyle);
			}


//            List<Board> list = repository.findAll();

			int listLength = listData.size();

			for (int i = 0; i < listLength; i++) {
				DataMap dt = (DataMap) listData.get(i);
//            	System.out.println("dt" + dt);

				Row row = sheet.createRow(rowNo++);
				row.createCell(0).setCellValue(dt.get("fdName") != null ? dt.get("fdName").toString() : "-"); // 상담 분류
				row.createCell(1).setCellValue(dt.get("msgTitle") != null ? dt.get("msgTitle").toString() : "-"); // 상담 내용 fdTitle -> msgTitle

//				String name = "";
				row.createCell(2).setCellValue(dt.get("callerName") != null ? dt.get("callerName").toString() : "-");
				//row.createCell(2).setCellValue(name); // 고객명
				//row.createCell(3).setCellValue(dt.get("fdCustomerUid") != null ? dt.get("fdCustomerUid").toString() : "-");
				row.createCell(3).setCellValue(dt.get("ani") != null ? dt.get("ani").toString() : ""); // 연락처
				row.createCell(4).setCellValue(dt.get("orderDate") != null ? dt.get("orderDate").toString() : "-"); // 요청일

			}


			sheet.setColumnWidth(0, 3000);
			sheet.setColumnWidth(1, 32000);
			sheet.setColumnWidth(2, 4000);
			sheet.setColumnWidth(3, 6000);
			sheet.setColumnWidth(4, 8000);

			File tmpFile = File.createTempFile("TMP~", ".xlsx");
			try (OutputStream fos = new FileOutputStream(tmpFile);) {
				workbook.write(fos);
			}
			InputStream res = new FileInputStream(tmpFile) {
				@Override
				public void close() throws IOException {
					super.close();
					if (tmpFile.delete()) {
					}
				}
			};

			return ResponseEntity.ok() //
					.contentLength(tmpFile.length()) //
					.contentType(MediaType.APPLICATION_OCTET_STREAM) //
					.header("Content-Disposition", "attachment;filename=AICounselorlist.xlsx") //
					.body(new InputStreamResource(res));

		}
	}

	// ticket list 엑셀 다운로드
	@GetMapping(value = {"/ticket/excelDownload"})
	public ResponseEntity<InputStreamResource> ticketExcel(HttpServletResponse response,
														   @RequestParam(value = "startDateString", required = false, defaultValue = "") final String startDate,
														   @RequestParam(value = "endDateString", required = false, defaultValue = "") final String endDate,
														   @RequestParam(value = "pkIssueTicket", required = false, defaultValue = "") final String pkIssueTicket,
														   @RequestParam(value = "searchAiName", required = false, defaultValue = "") final String searchAiName,
														   @RequestParam(value = "searchType", required = false, defaultValue = "") final String searchType,
														   @RequestParam(value = "searchCustomerInfo", required = false, defaultValue = "") final String searchCustomerInfo,
														   @RequestParam(value = "searchTypeList", required = false, defaultValue = "") final String searchTypeList,
														   @RequestParam(value = "searchWorkflow", required = false, defaultValue = "") final String searchWorkflow,
														   @RequestParam(value = "searchStaff", required = false, defaultValue = "") final String searchStaff
	) throws Exception {

		List<Object> listData = aiTicketService.getTicketListDue(startDate, endDate, pkIssueTicket, searchAiName, searchType, searchCustomerInfo, searchTypeList, searchWorkflow, searchStaff);

		try (Workbook workbook = new XSSFWorkbook()) {
			Sheet sheet = workbook.createSheet("ticketList");
			int rowNo = 0;

			CellStyle headStyle = workbook.createCellStyle();
			headStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.LIGHT_BLUE.getIndex());
			headStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			Font font = workbook.createFont();
			font.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
			font.setFontHeightInPoints((short) 13);
			headStyle.setFont(font);

			Row headerRow = sheet.createRow(rowNo++);
			headerRow.createCell(0).setCellValue("ID");
			headerRow.createCell(1).setCellValue("우선순위");
			headerRow.createCell(2).setCellValue("담당업무");
			headerRow.createCell(3).setCellValue("티켓내용");
			headerRow.createCell(4).setCellValue("처리상태");
			headerRow.createCell(5).setCellValue("고객명");
			headerRow.createCell(6).setCellValue("연락처");
			headerRow.createCell(7).setCellValue("담당자");
			headerRow.createCell(8).setCellValue("요청일");

			for (int i = 0; i <= 8; i++) {
				headerRow.getCell(i).setCellStyle(headStyle);
			}


//            List<Board> list = repository.findAll();

			int listLength = listData.size();

			for (int i = 0; i < listLength; i++) {
				DataMap dt = (DataMap) listData.get(i);
//            	System.out.println("dt" + dt);

				Row row = sheet.createRow(rowNo++);
				row.createCell(0).setCellValue(dt.get("pkIssueTicket") != null ? dt.get("pkIssueTicket").toString() : "-");
				row.createCell(1).setCellValue(dt.get("priority") != null ? dt.get("priority").toString() : "-");
				row.createCell(2).setCellValue(dt.get("fdName") != null ? dt.get("fdName").toString() : "-");
				row.createCell(3).setCellValue(dt.get("fdTicketTitle") != null ? dt.get("fdTicketTitle").toString() : "-");
				row.createCell(4).setCellValue(dt.get("ticketStatus") != null ? dt.get("ticketStatus").toString() : "-");
				String name = Common.NVL(dt.get("fdCustomerUid"), "-");
				if (dt.get("callerStaffName") != null) {
					name = dt.get("callerStaffName").toString();
				} else if (dt.get("customerName") != null) {
					name = dt.get("customerName").toString();
				}

				row.createCell(5).setCellValue(name);

//            	String mobile = "";
//            	if(dt.get("callerStaffMobile") != null) {
//            		mobile = dt.get("callerStaffMobile").toString();
//            	}else if(dt.get("customerMobile") != null) {
//            		mobile = dt.get("customerMobile").toString();
//            	}

				row.createCell(6).setCellValue(dt.get("fdCustomerUid") != null ? dt.get("fdCustomerUid").toString() : "-");
//            	row.createCell(6).setCellValue(dt.get("ani") != null ? dt.get("ani").toString() : "");
				row.createCell(7).setCellValue(dt.get("assignStaff") != null ? dt.get("assignStaff").toString() : "-");
				row.createCell(8).setCellValue(dt.get("fdRegdate") != null ? dt.get("fdRegdate").toString() : "-");

			}


			sheet.setColumnWidth(0, 3000);
			sheet.setColumnWidth(1, 3000);
			sheet.setColumnWidth(2, 4000);
			sheet.setColumnWidth(3, 8000);
			sheet.setColumnWidth(4, 8000);
			sheet.setColumnWidth(5, 8000);
			sheet.setColumnWidth(6, 8000);
			sheet.setColumnWidth(7, 8000);
			sheet.setColumnWidth(8, 8000);

			File tmpFile = File.createTempFile("TMP~", ".xlsx");
			try (OutputStream fos = new FileOutputStream(tmpFile);) {
				workbook.write(fos);
			}
			InputStream res = new FileInputStream(tmpFile) {
				@Override
				public void close() throws IOException {
					super.close();
					if (tmpFile.delete()) {
					}
				}
			};

			return ResponseEntity.ok() //
					.contentLength(tmpFile.length()) //
					.contentType(MediaType.APPLICATION_OCTET_STREAM) //
					.header("Content-Disposition", "attachment;filename=ticketlist.xlsx") //
					.body(new InputStreamResource(res));

		}
	}


	@PostMapping(value = {"/ticket/ticketCreate"})
	public ResponseVo ticketCreate(@RequestBody AiTicketVo aiTicketVo) throws Exception{
			aiTicketService.updateIssueTicket(aiTicketVo);

			return new ResponseVo(200);
	}

	@PostMapping(value = {"/dialogue/edit"})
	public ResponseVo dialogueEdit(@RequestBody List<DialogueVo> dialogueVo) throws Exception{
			aiTicketService.updateDialogueMessage(dialogueVo);

		return new ResponseVo(200);
	}

	@PostMapping(value = {"/dialogue/logging"}, consumes=MediaType.APPLICATION_JSON_VALUE)
	public ResponseVo dialogueLogAdd(@RequestBody DialogueVo dialogueLogVo) throws Exception {
		aiTicketService.addDialogueLog(dialogueLogVo);
		return new ResponseVo(200);
	}

	/**
	 * formData 레코드 조회
	 * @param fkIssueTicket
	 * @return
	 * @throws Exception
	 */
	@GetMapping(value = {"/ticket/{fkIssueTicket}/formdata"})
	public Object getFormInputData(
			@PathVariable(value = "fkIssueTicket") final String fkIssueTicket
	) throws Exception {

		List<Object> resultList = aiTicketService.getFormInputData(fkIssueTicket);
		return new ResponseVo(200, resultList);
	}

	@PatchMapping(value = {"/ticket/{fkIssueTicket}/formdata"})
	public Object updateFormData(
			@PathVariable(value = "fkIssueTicket") final String fkIssueTicket,
			@RequestBody Map<String, Object> reqBody
	) throws Exception {
		Map<String, Object> resultMap = aiTicketService.updateFormData(fkIssueTicket, reqBody);
		return new ResponseVo(200, resultMap);
	}

	@PatchMapping(value = {"/ticket/{fkIssueTicket}/comment"})
	public Object updateTicketComment(
			@PathVariable(value = "fkIssueTicket") final String fkIssueTicket,
			@RequestBody String commentData
	) throws Exception {
		Map<String, Object> resultMap = aiTicketService.updateTicketComment(fkIssueTicket, commentData);
		return new ResponseVo(200, resultMap);
	}
}