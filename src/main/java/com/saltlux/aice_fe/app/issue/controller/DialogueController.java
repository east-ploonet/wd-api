package com.saltlux.aice_fe.app.issue.controller;

import com.saltlux.aice_fe._baseline.baseController.BaseController;
import com.saltlux.aice_fe._baseline.baseVo.ResponseVo;
import com.saltlux.aice_fe.app.auth.vo.AppLoginInfoVo;
import com.saltlux.aice_fe.app.issue.service.IssueService;
import com.saltlux.aice_fe.pc.auth.vo.PcLoginInfoVo;
import com.saltlux.aice_fe.pc.issue.service.TicketIssueService;
import com.saltlux.aice_fe.pc.issue.vo.IssueTicketVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("${apiVersionPrefix}/issue") // end point : localhost:8080/api/v1/issue
public class DialogueController extends BaseController {

	private final List<String> staffLevelCode = new ArrayList<>(Arrays.asList("A1001", "A1002"));

	@Autowired
    private IssueService issueService;

	@Autowired
	private TicketIssueService ticketIssueService;

	private List<Long> getFkAssignStaffIds(String loginLevelCode, long id) {
		if (!staffLevelCode.contains(loginLevelCode)) {
			return Arrays.asList(id);
		} else {
			return null;
		}
	}

    // 상담/문의 목록
    @RequestMapping(value = {"/ticket/list"}, method = {RequestMethod.GET}, produces=PRODUCES_JSON)
    public Object getTestList(
		    @RequestParam Map<String, Object> paramMap
		    , @RequestParam(value = "page"      , required = false, defaultValue = "1"  ) int page
		    , @RequestParam(value = "pageSize"  , required = false, defaultValue = "1000" ) int pageSize
		    , @RequestParam(value = "search"    , required = false, defaultValue = ""   ) String searchString
		    , @RequestParam(value = "searchStatus"    , required = false, defaultValue = ""   ) String searchStatus
    ) throws Exception {

		AppLoginInfoVo appLoginInfoVo = getAppLoginInfoVo();

		IssueTicketVo issueTicketVo = new IssueTicketVo();
		issueTicketVo.setFkAssignStaffIds(getFkAssignStaffIds(appLoginInfoVo.getLoginLevelCode(), appLoginInfoVo.getLoginCompanyStaffPk()));
		issueTicketVo.getSearch().setSearchStatus(searchStatus);

		issueTicketVo.setFk_company_staff(appLoginInfoVo.getLoginCompanyPk());
		issueTicketVo.getSearch().setPage(page);
		issueTicketVo.getSearch().setPageSize(pageSize);
		issueTicketVo.getSearch().setSearchString(searchString);
		Map<String, Object> resultMap = ticketIssueService.getIssues(issueTicketVo);
        return new ResponseVo(200, resultMap);
    }

    // 상담/문의 내역
    @GetMapping("/ticket/view")
    public Object getView(
		    @RequestParam(value="issueTicketPk", required=true)    String issueTicketPk
    ) throws Exception {

	    AppLoginInfoVo appLoginInfoVo = getAppLoginInfoVo();

	    IssueTicketVo issueTicketVo = new IssueTicketVo();

		issueTicketVo.setPk_issue_ticket(   Long.parseLong(issueTicketPk));
	    issueTicketVo.setFk_modifier(       appLoginInfoVo.getLoginCompanyStaffPk());

	    Map<String, Object> resultMap = issueService.getIssueDialogueList(issueTicketVo);

	    return new ResponseVo(200, resultMap);
    }

	// 상담/문의 고객정보
	@GetMapping("/ticket/customer")
	public Object getCustomer(
			@RequestParam(value="issueTicketPk")    String issueTicketPk
	) throws Exception {

		IssueTicketVo issueTicketVo = new IssueTicketVo();
		issueTicketVo.setPk_issue_ticket(Long.parseLong(issueTicketPk));
		issueTicketVo.setFk_company_staff(getAppLoginInfoVo().getLoginCompanyPk());
		Map<String, Object> resultMap = issueService.getIssueDialogueCustomer(issueTicketVo);

		return new ResponseVo(200, resultMap);
	}

	// 상담/문의 상세정보
	@GetMapping("/ticket/detail")
	public Object getDetail(
			@RequestParam(value="issueTicketPk")    String issueTicketPk
	) throws Exception {

		IssueTicketVo issueTicketVo = new IssueTicketVo();
		issueTicketVo.setPk_issue_ticket(Long.parseLong(issueTicketPk));
		issueTicketVo.setFk_company_staff(getAppLoginInfoVo().getLoginCompanyPk());
		Map<String, Object> resultMap = issueService.getIssueDialogueDetail(issueTicketVo);

		return new ResponseVo(200, resultMap);
	}

	// 상담/문의 상세정보 > 상담완료 처리
	@PostMapping("/ticket/workflow")
	public Object setWorkflow(
			@RequestBody Map<String, Object> bodyMap
	) throws Exception {

		throwException.requestBodyRequied( bodyMap, "issueTicketPk", "workflowCode");

		AppLoginInfoVo appLoginInfoVo = getAppLoginInfoVo();

		//관리자 정보 조회
		IssueTicketVo issueTicketVo   = new IssueTicketVo();
		issueTicketVo.setPk_issue_ticket(           Long.parseLong( bodyMap.get("issueTicketPk").toString() ));
		issueTicketVo.setFd_ticket_workflow_code(   bodyMap.get("workflowCode").toString() );
		issueTicketVo.setFk_modifier(               appLoginInfoVo.getLoginCompanyStaffPk());

		issueService.updateIssueTicketWorkflow(issueTicketVo);

		return new ResponseVo(200);
	}

	@GetMapping("/ticket/listStatus")
	public Object getListStatus(@RequestParam(value = "listStatus", required = false) String listStatus) throws Exception {
		AppLoginInfoVo appLoginInfoVo = getAppLoginInfoVo();
		IssueTicketVo issueTicketVo = new IssueTicketVo();
		issueTicketVo.setFkAssignStaffIds(getFkAssignStaffIds(appLoginInfoVo.getLoginLevelCode(), appLoginInfoVo.getLoginCompanyStaffPk()));
		issueTicketVo.setFk_company_staff(appLoginInfoVo.getLoginCompanyPk());
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
}