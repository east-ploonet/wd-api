package com.saltlux.aice_fe.pc.issue.controller;

import com.saltlux.aice_fe._baseline.baseController.BaseController;
import com.saltlux.aice_fe._baseline.baseVo.ResponseVo;
import com.saltlux.aice_fe.app.auth.vo.AppLoginInfoVo;
import com.saltlux.aice_fe.pc.issue.dto.IssueTicketDTO;
import com.saltlux.aice_fe.pc.issue.service.TicketIssueService;
import com.saltlux.aice_fe.pc.issue.vo.IssueTicketVo;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("${apiVersionPrefix}/workStage/issue") // end point : localhost:8080/api/v1/issue
public class TicketIssueController extends BaseController {

	private final List<String> staffLevelCode = new ArrayList<>(Arrays.asList("A1001", "A1002"));

	@Value("${ploonet.api.base.url}")
	private String PLOONET_API_BASE_URL;

	@Value("${ploonet.voice.video.url}")
	private String PLOONET_VOICE_VIDEO_URL;
	
    @Autowired
    private TicketIssueService ticketIssueService;

    @Autowired
	private RestTemplate restTemplate;

    private List<Long> getFkAssignStaffIds(String loginLevelCode, long id) {
		if (!staffLevelCode.contains(loginLevelCode)) {
			return Arrays.asList(id);
		} else {
			return null;
		}
	}

    // 상담/문의 목록
    @RequestMapping(value = {"/ticket/list"}, method = {RequestMethod.POST}, produces=PRODUCES_JSON)
    public Object getTickets(@RequestParam(value = "page"      , required = false, defaultValue = "1"  ) int page
		    , @RequestParam(value = "pageSize"  , required = false, defaultValue = "1000" ) int pageSize
		    , @RequestParam(value = "searchString"    , required = false, defaultValue = ""   ) String searchString
		    , @RequestParam(value = "searchColumn"    , required = false, defaultValue = ""   ) String searchColumn
		    , @RequestParam(value = "searchStatus"    , required = false, defaultValue = ""   ) String searchStatus
		    , @RequestParam(value = "startDate"    , required = false, defaultValue = ""   ) String startDate
		    , @RequestParam(value = "endDate"    , required = false, defaultValue = ""   ) String endDate
		    , @RequestParam(value = "orderBy"    , required = false, defaultValue = ""   ) String orderBy
		    , @RequestParam(value = "orderType"    , required = false, defaultValue = ""   ) String orderType
			, @RequestBody IssueTicketVo issueTicketVo
    ) throws Exception {

		PcLoginInfoVo pcLoginInfoVo = getPcLoginInfoVo();

	    issueTicketVo.setFkAssignStaffIds(getFkAssignStaffIds(pcLoginInfoVo.getLoginLevelCode(), pcLoginInfoVo.getLoginCompanyStaffPk()));

	    issueTicketVo.setFk_company_staff(pcLoginInfoVo.getLoginCompanyPk());
	    issueTicketVo.getSearch().setPage(page);
	    issueTicketVo.getSearch().setPageSize(pageSize);
	    issueTicketVo.getSearch().setSearchString(searchString);
	    issueTicketVo.getSearch().setSearchColumn(searchColumn);
	    issueTicketVo.getSearch().setSearchStatus(searchStatus);
	    issueTicketVo.getSearch().setStartDate(startDate);
	    issueTicketVo.getSearch().setEndDate(endDate);
	    issueTicketVo.getSearch().setOrderBy(orderBy);
	    issueTicketVo.getSearch().setOrderType(orderType);
        Map<String, Object> resultMap = ticketIssueService.getIssues(issueTicketVo);

        return new ResponseVo(200, resultMap);
    }

    @GetMapping("/ticket/listStatus")
	public Object getListStatus(@RequestParam(required = false) String listStatus) throws Exception {
		PcLoginInfoVo pcLoginInfoVo = getPcLoginInfoVo();
		IssueTicketVo issueTicketVo = new IssueTicketVo();
		issueTicketVo.setFkAssignStaffIds(getFkAssignStaffIds(pcLoginInfoVo.getLoginLevelCode(), pcLoginInfoVo.getLoginCompanyStaffPk()));
		issueTicketVo.setFk_company_staff(pcLoginInfoVo.getLoginCompanyPk());

		List<String> listStatusConverted = new ArrayList<>();

		if(listStatus.indexOf("A")!= -1) {
			if(listStatus.indexOf(",") != -1) {
				String[] status = listStatus.split(",");
				listStatusConverted = Arrays.asList(status);
			} else {
				String[] status = new String[]{listStatus};
				listStatusConverted = Arrays.asList(status);
			}
		}

		Map<String, Object> resultMap = ticketIssueService.getStatusIssueTicket(issueTicketVo, listStatusConverted);
		return new ResponseVo(200, resultMap);
	}

    // 상담/문의 내역
    @GetMapping("/ticket/view")
    public Object getView(
		    @RequestParam(value="issueTicketPk")    String issueTicketPk
    ) throws Exception {

	    IssueTicketVo issueTicketVo = new IssueTicketVo();

		issueTicketVo.setPk_issue_ticket(   Long.parseLong(issueTicketPk));
//	    issueTicketVo.setFk_modifier(       appLoginInfoVo.getLoginCompanyStaffPk());
		issueTicketVo.setFk_company_staff(getPcLoginInfoVo().getLoginCompanyPk());

		Map<String, Object> resultMap = ticketIssueService.getIssueDialogueList(issueTicketVo);

	    return new ResponseVo(200, resultMap);
    }

	// 상담/문의 고객정보
	@GetMapping("/ticket/customer")
	public Object getCustomer(
			@RequestParam(value="issueTicketPk")    String issueTicketPk
	) throws Exception {

		IssueTicketVo issueTicketVo = new IssueTicketVo();
		issueTicketVo.setPk_issue_ticket(Long.parseLong(issueTicketPk));
		issueTicketVo.setFk_company_staff(getPcLoginInfoVo().getLoginCompanyPk());
		Map<String, Object> resultMap = ticketIssueService.getIssueDialogueCustomer(issueTicketVo);

		return new ResponseVo(200, resultMap);
	}

	// 상담/문의 상세정보
	@GetMapping("/ticket/detail")
	public Object getDetail(
			@RequestParam(value="issueTicketPk")    String issueTicketPk
	) throws Exception {

		IssueTicketVo issueTicketVo = new IssueTicketVo();
		issueTicketVo.setPk_issue_ticket(Long.parseLong(issueTicketPk));
		issueTicketVo.setFk_company_staff(getPcLoginInfoVo().getLoginCompanyPk());
		Map<String, Object> resultMap = ticketIssueService.getIssueTicketDetail(issueTicketVo);

		return new ResponseVo(200, resultMap);
	}

	// 상담/문의 상세정보 > 상담완료 처리
	@PostMapping("/ticket/workflow")
	public Object setWorkflow(
			@RequestBody Map<String, Object> bodyMap
	) throws Exception {

		throwException.requestBodyRequied( bodyMap, "issueTicketPk", "workflowCode");

		PcLoginInfoVo appLoginInfoVo = getPcLoginInfoVo();

		//관리자 정보 조회
		IssueTicketVo issueTicketVo   = new IssueTicketVo();
		issueTicketVo.setPk_issue_ticket(           Long.parseLong( bodyMap.get("issueTicketPk").toString() ));
		issueTicketVo.setFd_ticket_workflow_code(   bodyMap.get("workflowCode").toString() );
		issueTicketVo.setFk_modifier(               appLoginInfoVo.getLoginCompanyStaffPk());

		ticketIssueService.updateIssueTicketWorkflow(issueTicketVo);

		return new ResponseVo(200);
	}

	@PutMapping("/ticket/updateLimitDate")
	public Object updateTicketLimitDate(@RequestParam String pkTicketIssue,
											   @RequestParam long limitDate) throws Exception {

		IssueTicketVo issueTicketVo = new IssueTicketVo();
		issueTicketVo.setPk_issue_ticket(Long.parseLong(pkTicketIssue));
		issueTicketVo.setFd_ticket_limit_date(new Date(limitDate));
		ticketIssueService.updateTicketLimitDate(issueTicketVo);
		return new ResponseVo(200);
	}

	@PutMapping("/ticket/updateTicketIssues")
	public Object updateStatus(@RequestBody Map<String, Object> listIssueTicket) throws Exception {
		throwException.requestBodyRequied( listIssueTicket, "list");
		String levelCode = getPcLoginInfoVo().getLoginLevelCode();
		if (!levelCode.equals("A1001") && !levelCode.equals("A1002")) {
			return new ResponseVo(403);
		}
		List<String> list = (List<String>) listIssueTicket.get("list");
		List<IssueTicketVo> vos = new ArrayList<>();
		for (String issueTicketId : list) {
			IssueTicketVo issueTicketVo = new IssueTicketVo();
			issueTicketVo.setFd_ticket_workflow_code("A1305");
			issueTicketVo.setPk_issue_ticket(Long.parseLong(issueTicketId));
			vos.add(issueTicketVo);
		}
		ticketIssueService.updateIssueTicketsStatus(vos);
		return new ResponseVo(200);
	}

	@PutMapping("/ticket/updateTicketIssue")
	public Object updateTicketIssue(@RequestBody IssueTicketDTO ticketIssue) throws Exception {
		ticketIssueService.updateIssueTicket(ticketIssue);
		return new ResponseVo(200);
	}

	@PutMapping("/ticket/updateTicketIssueStatus")
	public Object updateTicketIssueStatus(@RequestParam String pkTicketIssue) throws Exception {

		IssueTicketVo issueTicketVo = new IssueTicketVo();
		issueTicketVo.setPk_issue_ticket(Long.parseLong(pkTicketIssue));
		issueTicketVo.setFd_ticket_status_code("B1608");
		ticketIssueService.updateIssueTicketStatus(issueTicketVo);
		return new ResponseVo(200);
	}

	@PutMapping("/ticket/updateManager")
	public Object updateManager(@RequestParam String pkTicketIssue, @RequestParam String managerId) throws Exception {

		IssueTicketVo issueTicketVo = new IssueTicketVo();
		issueTicketVo.setPk_issue_ticket(Long.parseLong(pkTicketIssue));
		issueTicketVo.setFk_assign_staff(Long.parseLong(managerId));
		ticketIssueService.updateIssueTicketManager(issueTicketVo);
		return new ResponseVo(200);
	}

	@PutMapping("/ticket/updatePriority")
	public Object updatePriority(@RequestParam String pkTicketIssue, @RequestParam String priority) throws Exception {

		IssueTicketVo issueTicketVo = new IssueTicketVo();
		issueTicketVo.setPk_issue_ticket(Long.parseLong(pkTicketIssue));
		issueTicketVo.setFk_priority(priority);
		issueTicketVo.setFk_modifier(getPcLoginInfoVo().getLoginCompanyStaffPk());
		ticketIssueService.updateIssueTicketPriority(issueTicketVo);
		return new ResponseVo(200);
	}

	@PutMapping("/ticket/deleteTickets")
	public Object deleteTickets(@RequestBody Map<String, Object> listIssueTicket) throws Exception {
		throwException.requestBodyRequied(listIssueTicket, "list");

		String levelCode = getPcLoginInfoVo().getLoginLevelCode();
		if (!levelCode.equals("A1001") && !levelCode.equals("A1002")) {
			return new ResponseVo(403);
		}
		List<String> list = (List<String>) listIssueTicket.get("list");
		List<IssueTicketVo> vos = new ArrayList<>();
		for (String issueTicketId : list) {
			IssueTicketVo issueTicketVo = new IssueTicketVo();
			issueTicketVo.setPk_issue_ticket(Long.parseLong(issueTicketId));
			vos.add(issueTicketVo);
		}
		ticketIssueService.deleteIssueTickets(vos);
		return new ResponseVo(200);
	}

	@GetMapping("/deleteDataGarbage")
	public void deleteDataGarbage() throws Exception {
		ticketIssueService.deleteDataGarbageTicket();
	}

	@GetMapping("/ticket/getVoice")
	public ResponseEntity<?> getVoiceTicketByContactId(@RequestParam String pkIssueContact) {
    	try {
			//UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(PLOONET_API_BASE_URL + "/files/stream/private/file/call-rec/call-id/" + pkIssueContact);
    		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(PLOONET_VOICE_VIDEO_URL + "/files/stream/private/file/call-rec/call-id/" + pkIssueContact);
    		
			return restTemplate.getForEntity(builder.toUriString(), byte[].class);
		} catch (HttpServerErrorException e) {
    		return ResponseEntity.noContent().build();
		}
	}
}
